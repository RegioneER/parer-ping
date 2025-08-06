/*
 * Engineering Ingegneria Informatica S.p.A.
 *
 * Copyright (C) 2023 Regione Emilia-Romagna <p/> This program is free software: you can
 * redistribute it and/or modify it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version. <p/> This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Affero General Public License for more details. <p/> You should
 * have received a copy of the GNU Affero General Public License along with this program. If not,
 * see <https://www.gnu.org/licenses/>.
 */

package it.eng.sacerasi.ws.ricerca.ricercaRecuperati.helper;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import it.eng.sacerasi.common.Constants;
import it.eng.sacerasi.common.Constants.EsitoServizio;
import it.eng.sacerasi.messages.MessaggiWSBundle;
import it.eng.sacerasi.ws.dto.IRispostaWS.SeverityEnum;
import it.eng.sacerasi.ws.dto.RispostaControlli;
import it.eng.sacerasi.ws.ejb.ControlliWS;
import it.eng.sacerasi.ws.ricerca.dto.ControlliRicerca;
import it.eng.sacerasi.ws.ricerca.ricercaRecuperati.dto.RicercaRecuperatiExt;
import it.eng.sacerasi.ws.ricerca.ricercaRecuperati.dto.RispostaWSRicercaRecuperati;

@Stateless(mappedName = "RicercaRecuperatiCheckHelper")
@LocalBean
public class RicercaRecuperatiCheckHelper {

    @EJB
    private ControlliWS controlliWS;
    @EJB
    private ControlliRicerca controlliRicerca;

    public void checkSessione(RicercaRecuperatiExt ricercaRecuperatiExt,
	    RispostaWSRicercaRecuperati rispostaWs) {
	RispostaControlli rispostaControlli = new RispostaControlli();
	// Verifica Nome Ambiente
	if (rispostaWs.getSeverity() != SeverityEnum.ERROR) {
	    rispostaControlli.reset();
	    rispostaControlli = controlliWS.verificaNomeAmbiente(
		    ricercaRecuperatiExt.getRicercaRecuperatiInput().getNmAmbiente());
	    if (!rispostaControlli.isrBoolean()) {
		if (rispostaControlli.getCodErr() == null) {
		    rispostaControlli.setCodErr(MessaggiWSBundle.PING_RICRECUP_001);
		    rispostaControlli.setDsErr(MessaggiWSBundle.getString(
			    MessaggiWSBundle.PING_RICRECUP_001,
			    ricercaRecuperatiExt.getRicercaRecuperatiInput().getNmAmbiente()));
		    setRispostaWsError(rispostaWs, SeverityEnum.ERROR, Constants.EsitoServizio.KO,
			    rispostaControlli);
		} else {
		    // Errore 666
		    setRispostaWsError(rispostaWs, SeverityEnum.ERROR, Constants.EsitoServizio.KO,
			    rispostaControlli);
		}
	    }
	    // In ogni caso setto in risposta il nome ambiente ricevuto in input
	    rispostaWs.getRicercaRecuperatiRisposta().setNmAmbiente(
		    ricercaRecuperatiExt.getRicercaRecuperatiInput().getNmAmbiente());
	}

	// Verifica nome versatore nell'ambito dell'ambiente
	Long idVersatore = null;
	if (rispostaWs.getSeverity() != SeverityEnum.ERROR) {
	    rispostaControlli.reset();
	    rispostaControlli = controlliWS.verificaNomeVersatore(
		    ricercaRecuperatiExt.getRicercaRecuperatiInput().getNmAmbiente(),
		    ricercaRecuperatiExt.getRicercaRecuperatiInput().getNmVersatore());
	    if (!rispostaControlli.isrBoolean()) {
		if (rispostaControlli.getCodErr() == null) {
		    rispostaControlli.setCodErr(MessaggiWSBundle.PING_RICRECUP_002);
		    rispostaControlli.setDsErr(MessaggiWSBundle.getString(
			    MessaggiWSBundle.PING_RICRECUP_002,
			    ricercaRecuperatiExt.getRicercaRecuperatiInput().getNmVersatore()));
		    setRispostaWsError(rispostaWs, SeverityEnum.ERROR, Constants.EsitoServizio.KO,
			    rispostaControlli);
		} else {
		    // Errore 666
		    setRispostaWsError(rispostaWs, SeverityEnum.ERROR, Constants.EsitoServizio.KO,
			    rispostaControlli);
		}
	    } else {
		// Setto l'id versatore per eventuali (in caso di assenza di errore) utilizzi
		// successivi
		idVersatore = rispostaControlli.getrLong();
	    }
	    // In ogni caso setto in risposta il nome versatore ricevuto in input
	    rispostaWs.getRicercaRecuperatiRisposta().setNmVersatore(
		    ricercaRecuperatiExt.getRicercaRecuperatiInput().getNmVersatore());
	}
	ricercaRecuperatiExt.setIdVersatore(idVersatore);
    }

    private void setRispostaWsError(RispostaWSRicercaRecuperati rispostaWs, SeverityEnum sev,
	    EsitoServizio esito, RispostaControlli rispostaControlli) {
	rispostaWs.setSeverity(sev);
	rispostaWs.setErrorCode(rispostaControlli.getCodErr());
	rispostaWs.setErrorMessage(rispostaControlli.getDsErr());
	rispostaWs.getRicercaRecuperatiRisposta().setCdEsito(esito);
	rispostaWs.getRicercaRecuperatiRisposta().setCdErr(rispostaControlli.getCodErr());
	rispostaWs.getRicercaRecuperatiRisposta().setDsErr(rispostaControlli.getDsErr());
    }
}
