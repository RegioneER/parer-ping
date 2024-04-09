/*
 * Engineering Ingegneria Informatica S.p.A.
 *
 * Copyright (C) 2023 Regione Emilia-Romagna
 * <p/>
 * This program is free software: you can redistribute it and/or modify it under the terms of
 * the GNU Affero General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU Affero General Public License along with this program.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package it.eng.sacerasi.ws.puliziaNotificato.ejb;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.transaction.UserTransaction;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.sacerasi.common.Constants;
import it.eng.sacerasi.messages.MessaggiWSBundle;
import it.eng.sacerasi.ws.dto.IRispostaWS;
import it.eng.sacerasi.ws.dto.RispostaControlli;
import it.eng.sacerasi.ws.ejb.SalvataggioDati;
import it.eng.sacerasi.ws.puliziaNotificato.dto.PuliziaNotificatoExt;
import it.eng.sacerasi.ws.puliziaNotificato.dto.PuliziaNotificatoInput;
import it.eng.sacerasi.ws.puliziaNotificato.dto.RispostaWSPuliziaNotificato;
import it.eng.sacerasi.ws.puliziaNotificato.dto.WSDescPuliziaNotificato;
import it.eng.sacerasi.ws.puliziaNotificato.helper.PuliziaNotificatoCheckHelper;
import it.eng.sacerasi.ws.response.PuliziaNotificatoRisposta;
import it.eng.sacerasi.ws.util.WsTransactionManager;

/**
 *
 * @author Bonora_L
 */
@Stateless(mappedName = "PuliziaNotificatoEjb")
@LocalBean
@TransactionManagement(TransactionManagementType.BEAN)
public class PuliziaNotificatoEjb {

    private static final Logger log = LoggerFactory.getLogger(PuliziaNotificatoEjb.class);

    @Resource
    private UserTransaction utx;
    @EJB
    private SalvataggioDati salvataggioDati;
    @EJB
    private PuliziaNotificatoCheckHelper puliziaNotificatoCheckHelper;
    private WsTransactionManager wtm;

    public PuliziaNotificatoRisposta puliziaNotificato(String nmAmbiente, String nmVersatore, String cdKeyObject) {
        log.debug("Eseguita chiamata WS PuliziaNotificato con i parametri : \n {} ,\n {} ,\n {}", nmAmbiente,
                nmVersatore, cdKeyObject);
        RispostaWSPuliziaNotificato rispostaWs = new RispostaWSPuliziaNotificato();
        rispostaWs.setPuliziaNotificatoRisposta(new PuliziaNotificatoRisposta());
        // Imposto l'esito della risposta di default OK
        rispostaWs.getPuliziaNotificatoRisposta().setCdEsito(Constants.EsitoServizio.OK);
        // Istanzio l'oggetto che contiene i parametri ricevuti
        PuliziaNotificatoInput inputParameters = new PuliziaNotificatoInput(nmAmbiente, nmVersatore, cdKeyObject);
        // Istanzio l'Ext con l'oggetto creato
        PuliziaNotificatoExt pnExt = new PuliziaNotificatoExt();
        pnExt.setDescrizione(new WSDescPuliziaNotificato());
        pnExt.setPuliziaNotificatoInput(inputParameters);
        wtm = new WsTransactionManager(utx);
        // Chiamo la classe PuliziaNotificatoCheck che gestisce i controlli e popola la rispostaWs
        log.debug("PuliziaNotificato - Eseguo i controlli sui parametri di input");
        puliziaNotificatoCheckHelper.checkRichiesta(pnExt, rispostaWs);

        if (StringUtils.isNotBlank(nmAmbiente) && StringUtils.isNotBlank(nmVersatore)
                && StringUtils.isNotBlank(cdKeyObject)) {

            RispostaControlli tmpRispCon = new RispostaControlli();
            wtm.beginTrans(rispostaWs);

            // Verifico l'esito dei controlli:
            if (rispostaWs.getSeverity() != IRispostaWS.SeverityEnum.ERROR) {
                // Ho passato i controlli con esito positivo
                // Modifico la sessione esistente con stato ELIMINATO
                log.debug(
                        "PuliziaNotificato - Controlli passati con successo, modifico lo stato della sessione di recupero");
                tmpRispCon = salvataggioDati.modificaSessioneRecupero(pnExt.getIdSessioneRecup(),
                        Constants.StatoSessioneRecup.ELIMINATO, null, null);
                if (tmpRispCon.getCodErr() != null) {
                    log.debug("PuliziaNotificato - fallita modifica dello stato");
                    tmpRispCon.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_PULIZIA_NOTIFICATO));
                    setRispostaWsError(rispostaWs, tmpRispCon);
                }
            } else {
                // Errore - creo una nuova sessione chiusa con stato CHIUSO_ERR_ELIMINATO
                log.debug(
                        "PuliziaNotificato - Controlli falliti, registro una nuova sessione di recupero con stato CHIUSO_ERR_ELIMINATO");
                tmpRispCon = salvataggioDati.creaSessioneRecupero(pnExt, rispostaWs.getErrorCode(),
                        rispostaWs.getErrorMessage());
                if (tmpRispCon.getCodErr() != null) {
                    log.debug("PuliziaNotificato - fallita creazione della nuova sessione");
                    setRispostaWsError(rispostaWs, tmpRispCon);
                }
            }

            if (tmpRispCon.isrBoolean() && rispostaWs.getErrorType() != IRispostaWS.ErrorTypeEnum.DB_FATAL) {
                log.info("Committing...");
                wtm.commit(rispostaWs);
            }
        }

        return rispostaWs.getPuliziaNotificatoRisposta();
    }

    private void setRispostaWsError(RispostaWSPuliziaNotificato risp, RispostaControlli tmpRispostaControlli) {
        risp.setSeverity(IRispostaWS.SeverityEnum.ERROR);
        risp.setErrorCode(tmpRispostaControlli.getCodErr());
        risp.setErrorMessage(tmpRispostaControlli.getDsErr());
        risp.getPuliziaNotificatoRisposta().setCdEsito(Constants.EsitoServizio.KO);
        risp.getPuliziaNotificatoRisposta().setCdErr(tmpRispostaControlli.getCodErr());
        risp.getPuliziaNotificatoRisposta().setDlErr(tmpRispostaControlli.getDsErr());
        wtm.rollback(risp);
        log.debug("Rollbacking...");
    }
}
