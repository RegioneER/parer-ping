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

package it.eng.sacerasi.ws.puliziaNotificato.helper;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import it.eng.sacerasi.common.Constants;
import it.eng.sacerasi.messages.MessaggiWSBundle;
import it.eng.sacerasi.ws.dto.IRispostaWS.SeverityEnum;
import it.eng.sacerasi.ws.dto.RispostaControlli;
import it.eng.sacerasi.ws.ejb.ControlliWS;
import it.eng.sacerasi.ws.puliziaNotificato.dto.PuliziaNotificatoExt;
import it.eng.sacerasi.ws.puliziaNotificato.dto.RispostaWSPuliziaNotificato;
import it.eng.sacerasi.ws.puliziaNotificato.ejb.ControlliPuliziaNotificato;

@Stateless(mappedName = "PuliziaNotificatoCheckHelper")
@LocalBean
public class PuliziaNotificatoCheckHelper {

    @EJB
    private ControlliWS controlliWS;
    @EJB
    private ControlliPuliziaNotificato controlliPulNotif;

    public void checkRichiesta(PuliziaNotificatoExt pnExt, RispostaWSPuliziaNotificato rispostaWs) {
	RispostaControlli rispostaControlli = new RispostaControlli();

	// Verifica Nome Ambiente
	if (rispostaWs.getSeverity() != SeverityEnum.ERROR) {
	    rispostaControlli.reset();
	    rispostaControlli = controlliWS
		    .verificaNomeAmbiente(pnExt.getPuliziaNotificatoInput().getNmAmbiente());
	    if (!rispostaControlli.isrBoolean()) {
		if (rispostaControlli.getCodErr() == null) {
		    rispostaControlli.setCodErr(MessaggiWSBundle.PING_ELIMINAPREL_001);
		    rispostaControlli.setDsErr(
			    MessaggiWSBundle.getString(MessaggiWSBundle.PING_ELIMINAPREL_001,
				    pnExt.getPuliziaNotificatoInput().getNmAmbiente()));
		    setRispostaWsError(rispostaWs, SeverityEnum.ERROR, Constants.EsitoServizio.KO,
			    rispostaControlli);
		} else {
		    // Errore 666
		    setRispostaWsError(rispostaWs, SeverityEnum.ERROR, Constants.EsitoServizio.KO,
			    rispostaControlli);
		}
	    } else {
		rispostaWs.getPuliziaNotificatoRisposta()
			.setNmAmbiente(pnExt.getPuliziaNotificatoInput().getNmAmbiente());
	    }
	}

	Long idVersatore = null;
	if (rispostaWs.getSeverity() != SeverityEnum.ERROR) {
	    rispostaControlli.reset();
	    rispostaControlli = controlliWS.verificaNomeVersatore(
		    pnExt.getPuliziaNotificatoInput().getNmAmbiente(),
		    pnExt.getPuliziaNotificatoInput().getNmVersatore());
	    if (!rispostaControlli.isrBoolean()) {
		if (rispostaControlli.getCodErr() == null) {
		    rispostaControlli.setCodErr(MessaggiWSBundle.PING_ELIMINAPREL_002);
		    rispostaControlli.setDsErr(
			    MessaggiWSBundle.getString(MessaggiWSBundle.PING_ELIMINAPREL_002,
				    pnExt.getPuliziaNotificatoInput().getNmVersatore()));
		    setRispostaWsError(rispostaWs, SeverityEnum.ERROR, Constants.EsitoServizio.KO,
			    rispostaControlli);
		} else {
		    // Errore 666
		    setRispostaWsError(rispostaWs, SeverityEnum.ERROR, Constants.EsitoServizio.KO,
			    rispostaControlli);
		}
	    } else {
		idVersatore = rispostaControlli.getrLong();
		rispostaWs.getPuliziaNotificatoRisposta()
			.setNmVersatore(pnExt.getPuliziaNotificatoInput().getNmVersatore());
	    }
	}
	pnExt.setIdVersatore(idVersatore);

	// Verifica che chiave object sia diverso da stringa vuota e spazi
	if (rispostaWs.getSeverity() != SeverityEnum.ERROR) {
	    rispostaControlli.reset();
	    rispostaControlli = controlliWS.verificaNotBlankCdKeyObject(
		    pnExt.getPuliziaNotificatoInput().getCdKeyObject(),
		    MessaggiWSBundle.PING_ELIMINAPREL_004);
	    if (!rispostaControlli.isrBoolean()) {
		setRispostaWsError(rispostaWs, SeverityEnum.ERROR, Constants.EsitoServizio.KO,
			rispostaControlli);
	    }
	}
	// Verifica che chiave object sia lungo meno di 96 caratteri
	if (rispostaWs.getSeverity() != SeverityEnum.ERROR) {
	    rispostaControlli.reset();
	    rispostaControlli = controlliWS.verificaCdKeyObjectLength(
		    pnExt.getPuliziaNotificatoInput().getCdKeyObject(),
		    MessaggiWSBundle.PING_ELIMINAPREL_008);
	    if (!rispostaControlli.isrBoolean()) {
		setRispostaWsError(rispostaWs, SeverityEnum.ERROR, Constants.EsitoServizio.KO,
			rispostaControlli);
	    }
	}
	// Verifica esistenza oggetto su db
	Long idObject = null;
	if (rispostaWs.getSeverity() != SeverityEnum.ERROR) {
	    rispostaControlli.reset();
	    rispostaControlli = controlliWS.verificaCdKeyObject(
		    pnExt.getPuliziaNotificatoInput().getNmAmbiente(),
		    pnExt.getPuliziaNotificatoInput().getNmVersatore(),
		    pnExt.getPuliziaNotificatoInput().getCdKeyObject());
	    if (!rispostaControlli.isrBoolean()) {
		rispostaControlli.setCodErr(MessaggiWSBundle.PING_ELIMINAPREL_005);
		rispostaControlli
			.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.PING_ELIMINAPREL_005,
				pnExt.getPuliziaNotificatoInput().getCdKeyObject()));
		setRispostaWsError(rispostaWs, SeverityEnum.ERROR, Constants.EsitoServizio.KO,
			rispostaControlli);
	    } else {
		rispostaWs.getPuliziaNotificatoRisposta()
			.setCdKeyObject(pnExt.getPuliziaNotificatoInput().getCdKeyObject());
		idObject = rispostaControlli.getrLong();
	    }
	}
	pnExt.setIdObject(idObject);

	Long idSessione = null;
	if (rispostaWs.getSeverity() != SeverityEnum.ERROR) {
	    rispostaControlli.reset();
	    rispostaControlli = controlliPulNotif.verificaOggetto(idObject);
	    if (!rispostaControlli.isrBoolean()) {
		setRispostaWsError(rispostaWs, SeverityEnum.ERROR, Constants.EsitoServizio.KO,
			rispostaControlli);
	    } else {
		idSessione = rispostaControlli.getrLong();
	    }
	}
	pnExt.setIdSessioneRecup(idSessione);
    }

    private void setRispostaWsError(RispostaWSPuliziaNotificato rispostaWs, SeverityEnum sev,
	    Constants.EsitoServizio esito, RispostaControlli rispostaControlli) {
	rispostaWs.setSeverity(sev);
	rispostaWs.setErrorCode(rispostaControlli.getCodErr());
	rispostaWs.setErrorMessage(rispostaControlli.getDsErr());
	rispostaWs.getPuliziaNotificatoRisposta().setCdEsito(esito);
	rispostaWs.getPuliziaNotificatoRisposta().setCdErr(rispostaControlli.getCodErr());
	rispostaWs.getPuliziaNotificatoRisposta().setDlErr(rispostaControlli.getDsErr());
    }
}
