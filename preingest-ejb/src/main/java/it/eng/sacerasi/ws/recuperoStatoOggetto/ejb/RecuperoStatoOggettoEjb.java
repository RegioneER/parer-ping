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

package it.eng.sacerasi.ws.recuperoStatoOggetto.ejb;

import it.eng.sacerasi.common.Constants;
import it.eng.sacerasi.ws.dto.IRispostaWS;
import it.eng.sacerasi.ws.recuperoStatoOggetto.dto.RecuperoStatoOggettoExt;
import it.eng.sacerasi.ws.recuperoStatoOggetto.dto.RecuperoStatoOggettoInput;
import it.eng.sacerasi.ws.recuperoStatoOggetto.dto.RispostaWSRecuperoStatoOggetto;
import it.eng.sacerasi.ws.recuperoStatoOggetto.dto.WSDescRecuperoStatoOggetto;
import it.eng.sacerasi.ws.recuperoStatoOggetto.helper.RecuperoStatoOggettoCheckHelper;
import it.eng.sacerasi.ws.recuperoStatoOggetto.helper.RecuperoStatoOggettoHelper;
import it.eng.sacerasi.ws.response.RecuperoStatoOggettoRisposta;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

/**
 *
 * @author Gilioli_P
 */
@Stateless(mappedName = "RecuperoStatoOggettoEjb")
@LocalBean
@Interceptors({ it.eng.sacerasi.aop.TransactionInterceptor.class })
public class RecuperoStatoOggettoEjb {

    @EJB
    private RecuperoStatoOggettoHelper recuperoStatoOggettoHelper;
    @EJB
    private RecuperoStatoOggettoCheckHelper recuperoStatoOggettoCheckHelper;

    public RecuperoStatoOggettoRisposta recuperaStatoObj(String nmAmbiente, String nmVersatore, String cdKeyObject) {

        // Istanzio la risposta
        RispostaWSRecuperoStatoOggetto rispostaWs = new RispostaWSRecuperoStatoOggetto();
        rispostaWs.setRecuperoStatoOggettoRisposta(new RecuperoStatoOggettoRisposta());
        // Imposto l'esito della risposta di default OK
        rispostaWs.getRecuperoStatoOggettoRisposta().setCdEsito(Constants.EsitoServizio.OK);
        // Istanzio l'oggetto che contiene i parametri ricevuti
        RecuperoStatoOggettoInput inputParameters = new RecuperoStatoOggettoInput(nmAmbiente, nmVersatore, cdKeyObject);
        // Istanzio l'Ext con l'oggetto creato
        RecuperoStatoOggettoExt rsoExt = new RecuperoStatoOggettoExt();
        rsoExt.setDescrizione(new WSDescRecuperoStatoOggetto());
        rsoExt.setRecuperoStatoOggettoInput(inputParameters);
        // Effettua i controlli e popola la rispostaWs
        recuperoStatoOggettoCheckHelper.checkRichiesta(rsoExt, rispostaWs);

        if (rispostaWs.getSeverity() != IRispostaWS.SeverityEnum.ERROR) {
            try {
                // Ho passato i controlli con esito positivo, recupero l'oggetto
                Object[] obj = recuperoStatoOggettoHelper.getStatoOggetto(rsoExt, rispostaWs);

                rispostaWs.getRecuperoStatoOggettoRisposta().setNmAmbiente(nmAmbiente);
                rispostaWs.getRecuperoStatoOggettoRisposta().setNmVersatore(nmVersatore);
                rispostaWs.getRecuperoStatoOggettoRisposta().setCdKeyObject(cdKeyObject);
                rispostaWs.getRecuperoStatoOggettoRisposta().setStatoOggetto((String) obj[0]);
                rispostaWs.getRecuperoStatoOggettoRisposta().setDescrizioneStatoOggetto((String) obj[1]);

            } catch (Exception e) {
                rispostaWs.setSeverity(IRispostaWS.SeverityEnum.ERROR);
                rispostaWs.setErrorType(IRispostaWS.ErrorTypeEnum.DB_FATAL);
                rispostaWs.setErrorMessage("Errore recupera stato obj  EJB " + e.getMessage());

                rispostaWs.getRecuperoStatoOggettoRisposta().setCdEsito(Constants.EsitoServizio.KO);
                rispostaWs.getRecuperoStatoOggettoRisposta().setCdErr(rispostaWs.getErrorCode());
                rispostaWs.getRecuperoStatoOggettoRisposta().setDlErr(rispostaWs.getErrorMessage());
            }
        }

        return rispostaWs.getRecuperoStatoOggettoRisposta();
    }
}
