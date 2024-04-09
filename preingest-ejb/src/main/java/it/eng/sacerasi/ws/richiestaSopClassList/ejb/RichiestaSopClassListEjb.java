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

package it.eng.sacerasi.ws.richiestaSopClassList.ejb;

import javax.ejb.EJB;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import it.eng.sacerasi.common.Constants;
import it.eng.sacerasi.entity.PigSopClassDicom;
import it.eng.sacerasi.ws.dto.IRispostaWS;
import it.eng.sacerasi.ws.response.RichiestaSopClassListRisposta;
import it.eng.sacerasi.ws.richiestaSopClassList.dto.RichiestaSopClassListExt;
import it.eng.sacerasi.ws.richiestaSopClassList.dto.RichiestaSopClassListInput;
import it.eng.sacerasi.ws.richiestaSopClassList.dto.RispostaWSRichiestaSopClassList;
import it.eng.sacerasi.ws.richiestaSopClassList.dto.SopClassRespType;
import it.eng.sacerasi.ws.richiestaSopClassList.dto.WSDescRichiestaSopClassList;
import it.eng.sacerasi.ws.richiestaSopClassList.helper.RichiestaSopClassListCheckHelper;

/**
 *
 * @author Bonora_L
 */
@Stateless(mappedName = "RichiestaSopClassListEjb")
@LocalBean
public class RichiestaSopClassListEjb {

    @EJB
    private RichiestaSopClassListCheckHelper richiestaSopClassListCheckHelper;

    public RichiestaSopClassListRisposta richiestaSopClassList(String nmAmbiente, String nmVersatore) {

        // Istanzio la risposta
        RispostaWSRichiestaSopClassList rispostaWs = new RispostaWSRichiestaSopClassList();
        rispostaWs.setRichiestaSopClassListRisposta(new RichiestaSopClassListRisposta());
        // Imposto l'esito della risposta di default OK
        rispostaWs.getRichiestaSopClassListRisposta().setCdEsito(Constants.EsitoServizio.OK);
        // Istanzio l'oggetto che contiene i parametri ricevuti
        // RichiestaSopClassListInput inputParameters = new RichiestaSopClassListInput(nmAmbiente, nmVersatore,
        // cdPassword);
        RichiestaSopClassListInput inputParameters = new RichiestaSopClassListInput(nmAmbiente, nmVersatore);
        // Istanzio l'Ext con l'oggetto creato
        RichiestaSopClassListExt rsclExt = new RichiestaSopClassListExt();
        rsclExt.setDescrizione(new WSDescRichiestaSopClassList());
        rsclExt.setRichiestaSopClassListInput(inputParameters);
        // Chiamo la classe RichiestaSopClassListCheck che gestisce i controlli e popola la rispostaWs
        richiestaSopClassListCheckHelper.checkRichiesta(rsclExt, rispostaWs);

        if (rispostaWs.getSeverity() != IRispostaWS.SeverityEnum.ERROR) {
            if (rsclExt.getListaSopClass() != null) {
                for (PigSopClassDicom sop : rsclExt.getListaSopClass()) {
                    SopClassRespType sopResp = new SopClassRespType();
                    sopResp.setCdSopClass(sop.getCdSopClassDicom());
                    sopResp.setDsSopClass(sop.getDsSopClassDicom());
                    rispostaWs.getRichiestaSopClassListRisposta().getListaSOPClass().getSopClass().add(sopResp);
                }
            }
        }

        return rispostaWs.getRichiestaSopClassListRisposta();
    }
}
