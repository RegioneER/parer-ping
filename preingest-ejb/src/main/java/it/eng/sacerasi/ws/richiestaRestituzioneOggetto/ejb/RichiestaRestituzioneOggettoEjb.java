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

package it.eng.sacerasi.ws.richiestaRestituzioneOggetto.ejb;

import it.eng.sacerasi.common.Constants;
import it.eng.sacerasi.ws.dto.IRispostaWS;
import it.eng.sacerasi.ws.dto.RispostaControlli;
import it.eng.sacerasi.ws.ejb.SalvataggioDati;
import it.eng.sacerasi.ws.response.RichiestaRestituzioneOggettoRisposta;
import it.eng.sacerasi.ws.richiestaRestituzioneOggetto.dto.RichiestaRestituzioneOggettoExt;
import it.eng.sacerasi.ws.richiestaRestituzioneOggetto.dto.RichiestaRestituzioneOggettoInput;
import it.eng.sacerasi.ws.richiestaRestituzioneOggetto.dto.RispostaWSRichiestaRestituzioneOggetto;
import it.eng.sacerasi.ws.richiestaRestituzioneOggetto.dto.WSDescRichiestaRestituzioneOggetto;
import it.eng.sacerasi.ws.richiestaRestituzioneOggetto.helper.RichiestaRestituzioneOggettoCheckHelper;
import java.util.Calendar;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless(mappedName = "RichiestaRestituzioneOggettoEjb")
@LocalBean
@Interceptors({ it.eng.sacerasi.aop.TransactionInterceptor.class })
public class RichiestaRestituzioneOggettoEjb {

    private static final Logger log = LoggerFactory.getLogger(RichiestaRestituzioneOggettoEjb.class);

    @EJB
    private SalvataggioDati salvataggioDati;
    @EJB
    private RichiestaRestituzioneOggettoCheckHelper richiestaRestituzioneOggettoCheckHelper;

    public RichiestaRestituzioneOggettoRisposta richiestaRestituzioneOggetto(String nmAmbiente, String nmVersatore,
            String cdKeyObject) {

        log.debug("Ricevuta richiesta di RichiestaRestituzioneOggetto con i parametri : " + "nmAmbiente = {} " + " , "
                + "nmVersatore = {}  , cdKeyObject = {}", nmAmbiente, nmVersatore, cdKeyObject);
        // Istanzio la risposta
        RispostaWSRichiestaRestituzioneOggetto rispostaWs = new RispostaWSRichiestaRestituzioneOggetto();
        rispostaWs.setRichiestaRestituzioneOggettoRisposta(new RichiestaRestituzioneOggettoRisposta());
        // Imposto l'esito della risposta di default OK
        rispostaWs.getRichiestaRestituzioneOggettoRisposta().setCdEsito(Constants.EsitoServizio.OK);
        // Istanzio l'oggetto che contiene i parametri ricevuti
        RichiestaRestituzioneOggettoInput inputParameters = new RichiestaRestituzioneOggettoInput(nmAmbiente,
                nmVersatore, cdKeyObject);
        // Istanzio l'Ext con l'oggetto creato
        RichiestaRestituzioneOggettoExt rroExt = new RichiestaRestituzioneOggettoExt();
        rroExt.setDescrizione(new WSDescRichiestaRestituzioneOggetto());
        rroExt.setRichiestaRestituzioneOggettoInput(inputParameters);

        // Chiamo la classe RichiestaRestituzioneOggettoCheck che gestisce i controlli e popola la rispostaWs
        log.debug("Inizio controlli");
        richiestaRestituzioneOggettoCheckHelper.checkRichiesta(rroExt, rispostaWs);
        log.debug("Fine controlli");

        RispostaControlli tmpRispCon;

        log.debug("Apertura transazione");
        rroExt.setDtApertura(Calendar.getInstance().getTime());
        if (rispostaWs.getSeverity() != IRispostaWS.SeverityEnum.ERROR) {
            // Ho passato i controlli con esito positivo
            log.debug("STATO SESSIONE: IN_ATTESA_RECUP");
            rroExt.setStatoSessione(Constants.StatoSessioneRecup.IN_ATTESA_RECUP);
        } else {
            // Ho passato i controlli con esito negativo
            log.debug("STATO SESSIONE: CHIUSO_ERR");
            rroExt.setStatoSessione(Constants.StatoSessioneRecup.CHIUSO_ERR);
        }

        log.debug(
                "Creo sessione di recupero con stato definito sopra e codErr (se presente) : {}, dsErr (se presente): {}",
                rispostaWs.getErrorCode(), rispostaWs.getErrorMessage());
        tmpRispCon = salvataggioDati.creaSessioneRecupero(rroExt, rispostaWs.getErrorCode(),
                rispostaWs.getErrorMessage());

        if (tmpRispCon.getCodErr() != null) {
            setRispostaWsError(rispostaWs, tmpRispCon);
        }

        if (tmpRispCon.isrBoolean() && rispostaWs.getErrorType() != IRispostaWS.ErrorTypeEnum.DB_FATAL) {
            log.info("Fine transazione - COMMIT");

        }

        return rispostaWs.getRichiestaRestituzioneOggettoRisposta();
    }

    private void setRispostaWsError(RispostaWSRichiestaRestituzioneOggetto risp,
            RispostaControlli tmpRispostaControlli) {
        risp.setSeverity(IRispostaWS.SeverityEnum.ERROR);
        risp.setErrorCode(tmpRispostaControlli.getCodErr());
        risp.setErrorMessage(tmpRispostaControlli.getDsErr());
        risp.getRichiestaRestituzioneOggettoRisposta().setCdEsito(Constants.EsitoServizio.KO);
        risp.getRichiestaRestituzioneOggettoRisposta().setCdErr(tmpRispostaControlli.getCodErr());
        risp.getRichiestaRestituzioneOggettoRisposta().setDlErr(tmpRispostaControlli.getDsErr());
        log.debug("Errore richiesta : {} - {}", tmpRispostaControlli.getCodErr(), tmpRispostaControlli.getDsErr());
        log.debug("Fine transazione - ROLLBACK");

    }
}
