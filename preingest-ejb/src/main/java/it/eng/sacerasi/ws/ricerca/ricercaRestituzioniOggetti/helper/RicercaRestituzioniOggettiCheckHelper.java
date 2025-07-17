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

package it.eng.sacerasi.ws.ricerca.ricercaRestituzioniOggetti.helper;

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
import it.eng.sacerasi.ws.ricerca.ricercaRestituzioniOggetti.dto.RicercaRestituzioniOggettiExt;
import it.eng.sacerasi.ws.ricerca.ricercaRestituzioniOggetti.dto.RispostaWSRicercaRestituzioniOggetti;

@Stateless(mappedName = "RicercaRestituzioniOggettiCheckHelper")
@LocalBean
public class RicercaRestituzioniOggettiCheckHelper {

    @EJB
    private ControlliWS controlliWS;
    @EJB
    private ControlliRicerca controlliRicerca;

    public void checkSessione(RicercaRestituzioniOggettiExt ricercaRestituzioniOggettiExt,
	    RispostaWSRicercaRestituzioniOggetti rispostaWs) {
	RispostaControlli rispostaControlli = new RispostaControlli();
	// Verifica Nome Ambiente
	if (rispostaWs.getSeverity() != SeverityEnum.ERROR) {
	    rispostaControlli.reset();
	    rispostaControlli = controlliWS.verificaNomeAmbiente(ricercaRestituzioniOggettiExt
		    .getRicercaRestituzioniOggettiInput().getNmAmbiente());
	    if (!rispostaControlli.isrBoolean()) {
		if (rispostaControlli.getCodErr() == null) {
		    rispostaControlli.setCodErr(MessaggiWSBundle.PING_RESTIT_001);
		    rispostaControlli.setDsErr(MessaggiWSBundle.getString(
			    MessaggiWSBundle.PING_RESTIT_001, ricercaRestituzioniOggettiExt
				    .getRicercaRestituzioniOggettiInput().getNmAmbiente()));
		    setRispostaWsError(rispostaWs, SeverityEnum.ERROR, Constants.EsitoServizio.KO,
			    rispostaControlli);
		} else {
		    // Errore 666
		    setRispostaWsError(rispostaWs, SeverityEnum.ERROR, Constants.EsitoServizio.KO,
			    rispostaControlli);
		}
	    }
	    // In ogni caso setto in risposta il nome ambiente ricevuto in input
	    rispostaWs.getricercaRestituzioniOggettiRisposta()
		    .setNmAmbiente(ricercaRestituzioniOggettiExt
			    .getRicercaRestituzioniOggettiInput().getNmAmbiente());
	}

	// Verifica nome versatore nell'ambito dell'ambiente
	Long idVersatore = null;
	if (rispostaWs.getSeverity() != SeverityEnum.ERROR) {
	    rispostaControlli.reset();
	    rispostaControlli = controlliWS.verificaNomeVersatore(
		    ricercaRestituzioniOggettiExt.getRicercaRestituzioniOggettiInput()
			    .getNmAmbiente(),
		    ricercaRestituzioniOggettiExt.getRicercaRestituzioniOggettiInput()
			    .getNmVersatore());
	    if (!rispostaControlli.isrBoolean()) {
		if (rispostaControlli.getCodErr() == null) {
		    rispostaControlli.setCodErr(MessaggiWSBundle.PING_RESTIT_002);
		    rispostaControlli.setDsErr(MessaggiWSBundle.getString(
			    MessaggiWSBundle.PING_RESTIT_002, ricercaRestituzioniOggettiExt
				    .getRicercaRestituzioniOggettiInput().getNmVersatore()));
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
	    rispostaWs.getricercaRestituzioniOggettiRisposta()
		    .setNmVersatore(ricercaRestituzioniOggettiExt
			    .getRicercaRestituzioniOggettiInput().getNmVersatore());
	}
	ricercaRestituzioniOggettiExt.setIdVersatore(idVersatore);

	Long idTipoObject = null;
	// Verifica nome tipo object
	if (rispostaWs.getSeverity() != SeverityEnum.ERROR) {
	    rispostaControlli.reset();
	    rispostaControlli = controlliWS
		    .verificaNomeTipoObject(
			    idVersatore, ricercaRestituzioniOggettiExt
				    .getRicercaRestituzioniOggettiInput().getNmTipoObject(),
			    MessaggiWSBundle.PING_RESTIT_004);
	    if (!rispostaControlli.isrBoolean()) {
		setRispostaWsError(rispostaWs, SeverityEnum.ERROR, Constants.EsitoServizio.KO,
			rispostaControlli);
	    } else {
		idTipoObject = rispostaControlli.getrLong();
	    }
	    // In ogni caso setto in risposta il nome tipo object ricevuto in input
	    rispostaWs.getricercaRestituzioniOggettiRisposta()
		    .setNmTipoObject(ricercaRestituzioniOggettiExt
			    .getRicercaRestituzioniOggettiInput().getNmTipoObject());
	}
	// Setto l'id tipo object per eventuali (in caso di assenza di errore) utilizzi successivi
	ricercaRestituzioniOggettiExt.setIdTipoObject(idTipoObject);

	// Verifica presenza nome tipo object con i file XML
	if (rispostaWs.getSeverity() != SeverityEnum.ERROR) {
	    rispostaControlli.reset();
	    rispostaControlli = controlliRicerca.verificaNomeTipoObjectConXML(
		    ricercaRestituzioniOggettiExt.getRicercaRestituzioniOggettiInput()
			    .getNmTipoObject(),
		    ricercaRestituzioniOggettiExt.getRicercaRestituzioniOggettiInput()
			    .getXmlDatiSpecFiltri(),
		    ricercaRestituzioniOggettiExt.getRicercaRestituzioniOggettiInput()
			    .getXmlDatiSpecOutput(),
		    ricercaRestituzioniOggettiExt.getRicercaRestituzioniOggettiInput()
			    .getXmlDatiSpecOrder());
	    if (!rispostaControlli.isrBoolean()) {
		setRispostaWsError(rispostaWs, SeverityEnum.ERROR, Constants.EsitoServizio.KO,
			rispostaControlli);
	    }
	}
    }

    private void setRispostaWsError(RispostaWSRicercaRestituzioniOggetti rispostaWs,
	    SeverityEnum sev, EsitoServizio esito, RispostaControlli rispostaControlli) {
	rispostaWs.setSeverity(sev);
	rispostaWs.setErrorCode(rispostaControlli.getCodErr());
	rispostaWs.setErrorMessage(rispostaControlli.getDsErr());
	rispostaWs.getricercaRestituzioniOggettiRisposta().setCdEsito(esito);
	rispostaWs.getricercaRestituzioniOggettiRisposta().setCdErr(rispostaControlli.getCodErr());
	rispostaWs.getricercaRestituzioniOggettiRisposta().setDsErr(rispostaControlli.getDsErr());
    }
}
