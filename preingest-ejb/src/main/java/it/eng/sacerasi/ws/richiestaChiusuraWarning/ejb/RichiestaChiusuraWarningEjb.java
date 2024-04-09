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

package it.eng.sacerasi.ws.richiestaChiusuraWarning.ejb;

import it.eng.sacerasi.common.Constants;
import it.eng.sacerasi.ws.dto.IRispostaWS;
import it.eng.sacerasi.ws.dto.RispostaControlli;
import it.eng.sacerasi.ws.ejb.SalvataggioDati;
import it.eng.sacerasi.ws.response.RichiestaChiusuraWarningRisposta;
import it.eng.sacerasi.ws.richiestaChiusuraWarning.dto.RichiestaChiusuraWarningExt;
import it.eng.sacerasi.ws.richiestaChiusuraWarning.dto.RichiestaChiusuraWarningInput;
import it.eng.sacerasi.ws.richiestaChiusuraWarning.dto.RispostaWSRichiestaChiusuraWarning;
import it.eng.sacerasi.ws.richiestaChiusuraWarning.dto.WSDescRichiestaChiusuraWarning;
import it.eng.sacerasi.ws.richiestaChiusuraWarning.helper.RichiestaChiusuraWarningCheckHelper;
import it.eng.sacerasi.ws.util.WsTransactionManager;
import java.util.Date;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.transaction.UserTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Bonora_L
 */
@Stateless(mappedName = "RichiestaChiusuraWarningEjb")
@LocalBean
@TransactionManagement(TransactionManagementType.BEAN)
public class RichiestaChiusuraWarningEjb {

    private static final Logger log = LoggerFactory.getLogger(RichiestaChiusuraWarningEjb.class);

    @Resource
    private UserTransaction utx;
    @EJB
    private SalvataggioDati salvataggioDati;
    @EJB
    private RichiestaChiusuraWarningCheckHelper richiestaChiusuraWarningCheckHelper;

    private WsTransactionManager wtm;

    // public RichiestaChiusuraWarningRisposta richiestaChiusuraWarning(String nmAmbiente,
    // String nmVersatore, String cdPassword, String cdKeyObject, String dlMotivazione) {
    public RichiestaChiusuraWarningRisposta richiestaChiusuraWarning(String nmAmbiente, String nmVersatore,
            String cdKeyObject, String dlMotivazione) {

        // Istanzio la risposta
        RispostaWSRichiestaChiusuraWarning rispostaWs = new RispostaWSRichiestaChiusuraWarning();
        rispostaWs.setRichiestaChiusuraWarningRisposta(new RichiestaChiusuraWarningRisposta());
        // Imposto l'esito della risposta di default OK
        rispostaWs.getRichiestaChiusuraWarningRisposta().setCdEsito(Constants.EsitoServizio.OK);
        // Istanzio l'oggetto che contiene i parametri ricevuti
        // RichiestaChiusuraWarningInput inputParameters = new RichiestaChiusuraWarningInput(nmAmbiente, nmVersatore,
        // cdPassword, cdKeyObject, dlMotivazione);
        RichiestaChiusuraWarningInput inputParameters = new RichiestaChiusuraWarningInput(nmAmbiente, nmVersatore,
                cdKeyObject, dlMotivazione);
        // Istanzio l'Ext con l'oggetto creato
        RichiestaChiusuraWarningExt rcwExt = new RichiestaChiusuraWarningExt();
        rcwExt.setDescrizione(new WSDescRichiestaChiusuraWarning());
        rcwExt.setRichiestaChiusuraWarningInput(inputParameters);
        wtm = new WsTransactionManager(utx);
        // Chiamo la classe RichiestaChiusuraWarningCheck che gestisce i controlli e popola la rispostaWs
        richiestaChiusuraWarningCheckHelper.checkRichiesta(rcwExt, rispostaWs);

        if (rispostaWs.getSeverity() != IRispostaWS.SeverityEnum.ERROR) {
            RispostaControlli tmpRispCon = new RispostaControlli();
            wtm.beginTrans(rispostaWs);
            // Ho passato i controlli con esito positivo
            if (rcwExt.getIdLastSession() != null) {
                tmpRispCon = salvataggioDati.modificaSessione(rcwExt.getIdLastSession(),
                        Constants.StatoOggetto.CHIUSO_WARNING, dlMotivazione, true);
                if (tmpRispCon.getCodErr() != null) {
                    setRispostaWsError(rispostaWs, tmpRispCon);
                }
            }
            if (rispostaWs.getSeverity() != IRispostaWS.SeverityEnum.ERROR) {
                Date now = tmpRispCon.getrDate();
                tmpRispCon.reset();
                tmpRispCon = salvataggioDati.creaStatoSessione(rcwExt.getIdLastSession(),
                        Constants.StatoOggetto.CHIUSO_WARNING.name(), now);
                if (tmpRispCon.getCodErr() != null) {
                    setRispostaWsError(rispostaWs, tmpRispCon);
                }
            }
            if (rispostaWs.getSeverity() != IRispostaWS.SeverityEnum.ERROR) {
                tmpRispCon.reset();
                if (rcwExt.getIdObject() != null) {
                    tmpRispCon = salvataggioDati.modificaOggetto(rcwExt.getIdObject(),
                            Constants.StatoOggetto.CHIUSO_WARNING);
                    if (tmpRispCon.getCodErr() != null) {
                        setRispostaWsError(rispostaWs, tmpRispCon);
                    }
                }
            }
            if (tmpRispCon.getCodErr() == null && (rispostaWs.getErrorType() != IRispostaWS.ErrorTypeEnum.DB_FATAL)) {
                log.info("Committing...");
                wtm.commit(rispostaWs);
            }
        }
        return rispostaWs.getRichiestaChiusuraWarningRisposta();
    }

    private void setRispostaWsError(RispostaWSRichiestaChiusuraWarning risp, RispostaControlli tmpRispostaControlli) {
        risp.setSeverity(IRispostaWS.SeverityEnum.ERROR);
        risp.setErrorCode(tmpRispostaControlli.getCodErr());
        risp.setErrorMessage(tmpRispostaControlli.getDsErr());
        risp.getRichiestaChiusuraWarningRisposta().setCdEsito(Constants.EsitoServizio.KO);
        risp.getRichiestaChiusuraWarningRisposta().setCdErr(tmpRispostaControlli.getCodErr());
        risp.getRichiestaChiusuraWarningRisposta().setDlErr(tmpRispostaControlli.getDsErr());
        wtm.rollback(risp);
        log.info("Rollbacking...");
    }
}
