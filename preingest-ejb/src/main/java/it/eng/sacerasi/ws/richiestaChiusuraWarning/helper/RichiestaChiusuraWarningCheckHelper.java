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

package it.eng.sacerasi.ws.richiestaChiusuraWarning.helper;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.apache.commons.lang3.StringUtils;

import it.eng.sacerasi.common.Constants;
import it.eng.sacerasi.messages.MessaggiWSBundle;
import it.eng.sacerasi.ws.dto.IRispostaWS.SeverityEnum;
import it.eng.sacerasi.ws.dto.RispostaControlli;
import it.eng.sacerasi.ws.ejb.ControlliWS;
import it.eng.sacerasi.ws.richiestaChiusuraWarning.dto.RichiestaChiusuraWarningExt;
import it.eng.sacerasi.ws.richiestaChiusuraWarning.dto.RispostaWSRichiestaChiusuraWarning;

@Stateless(mappedName = "RichiestaChiusuraWarningCheckHelper")
@LocalBean
public class RichiestaChiusuraWarningCheckHelper {

    @EJB
    private ControlliWS controlliWS;

    public void checkRichiesta(RichiestaChiusuraWarningExt rcwe,
	    RispostaWSRichiestaChiusuraWarning rispostaWs) {
	RispostaControlli rispostaControlli = new RispostaControlli();
	// Verifica Nome Ambiente
	if (rispostaWs.getSeverity() != SeverityEnum.ERROR) {
	    rispostaControlli.reset();
	    rispostaControlli = controlliWS
		    .verificaNomeAmbiente(rcwe.getRichiestaChiusuraWarningInput().getNmAmbiente());
	    if (!rispostaControlli.isrBoolean()) {
		if (rispostaControlli.getCodErr() == null) {
		    rispostaControlli.setCodErr(MessaggiWSBundle.PING_CHIUWARN_001);
		    rispostaControlli
			    .setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.PING_CHIUWARN_001,
				    rcwe.getRichiestaChiusuraWarningInput().getNmAmbiente()));
		    setRispostaWsError(rispostaWs, SeverityEnum.ERROR, Constants.EsitoServizio.KO,
			    rispostaControlli);
		} else {
		    // Errore 666
		    setRispostaWsError(rispostaWs, SeverityEnum.ERROR, Constants.EsitoServizio.KO,
			    rispostaControlli);
		}
	    } else {
		rispostaWs.getRichiestaChiusuraWarningRisposta()
			.setNmAmbiente(rcwe.getRichiestaChiusuraWarningInput().getNmAmbiente());
	    }
	}

	Long idVersatore = null;
	if (rispostaWs.getSeverity() != SeverityEnum.ERROR) {
	    rispostaControlli.reset();
	    rispostaControlli = controlliWS.verificaNomeVersatore(
		    rcwe.getRichiestaChiusuraWarningInput().getNmAmbiente(),
		    rcwe.getRichiestaChiusuraWarningInput().getNmVersatore());
	    if (!rispostaControlli.isrBoolean()) {
		if (rispostaControlli.getCodErr() == null) {
		    rispostaControlli.setCodErr(MessaggiWSBundle.PING_CHIUWARN_002);
		    rispostaControlli
			    .setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.PING_CHIUWARN_002,
				    rcwe.getRichiestaChiusuraWarningInput().getNmVersatore()));
		    setRispostaWsError(rispostaWs, SeverityEnum.ERROR, Constants.EsitoServizio.KO,
			    rispostaControlli);
		} else {
		    // Errore 666
		    setRispostaWsError(rispostaWs, SeverityEnum.ERROR, Constants.EsitoServizio.KO,
			    rispostaControlli);
		}
	    } else {
		idVersatore = rispostaControlli.getrLong();
		rispostaWs.getRichiestaChiusuraWarningRisposta()
			.setNmVersatore(rcwe.getRichiestaChiusuraWarningInput().getNmVersatore());
	    }
	}

	// Verifica che chiave object sia diverso da stringa vuota e spazi
	if (rispostaWs.getSeverity() != SeverityEnum.ERROR) {
	    rispostaControlli.reset();
	    rispostaControlli = controlliWS.verificaNotBlankCdKeyObject(
		    rcwe.getRichiestaChiusuraWarningInput().getCdKeyObject(),
		    MessaggiWSBundle.PING_CHIUWARN_004);
	    if (!rispostaControlli.isrBoolean()) {
		setRispostaWsError(rispostaWs, SeverityEnum.ERROR, Constants.EsitoServizio.KO,
			rispostaControlli);
	    }
	}
	// Verifica che chiave object sia lungo meno di 96 caratteri
	if (rispostaWs.getSeverity() != SeverityEnum.ERROR) {
	    rispostaControlli.reset();
	    rispostaControlli = controlliWS.verificaCdKeyObjectLength(
		    rcwe.getRichiestaChiusuraWarningInput().getCdKeyObject(),
		    MessaggiWSBundle.PING_CHIUWARN_008);
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
		    rcwe.getRichiestaChiusuraWarningInput().getNmAmbiente(),
		    rcwe.getRichiestaChiusuraWarningInput().getNmVersatore(),
		    rcwe.getRichiestaChiusuraWarningInput().getCdKeyObject());
	    if (!rispostaControlli.isrBoolean()) {
		rispostaControlli.setCodErr(MessaggiWSBundle.PING_CHIUWARN_005);
		rispostaControlli
			.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.PING_CHIUWARN_005,
				rcwe.getRichiestaChiusuraWarningInput().getCdKeyObject()));
		setRispostaWsError(rispostaWs, SeverityEnum.ERROR, Constants.EsitoServizio.KO,
			rispostaControlli);
	    } else {
		rispostaWs.getRichiestaChiusuraWarningRisposta()
			.setCdKeyObject(rcwe.getRichiestaChiusuraWarningInput().getCdKeyObject());
		idObject = rispostaControlli.getrLong();
	    }
	}
	rcwe.setIdObject(idObject);

	Long idLastSession = null;
	// Verifica che l'oggetto sia in stato WARNING
	if (rispostaWs.getSeverity() != SeverityEnum.ERROR) {
	    rispostaControlli.reset();
	    rispostaControlli = controlliWS.verificaStatoOggetto(idObject,
		    Constants.StatoOggetto.WARNING, MessaggiWSBundle.PING_CHIUWARN_006);
	    if (!rispostaControlli.isrBoolean()) {
		setRispostaWsError(rispostaWs, SeverityEnum.ERROR, Constants.EsitoServizio.KO,
			rispostaControlli);
	    } else {
		idLastSession = rispostaControlli.getrLong();
	    }
	}
	rcwe.setIdLastSession(idLastSession);

	// Verifica che sia stato popolato il campo dlMotivazione
	if (rispostaWs.getSeverity() != SeverityEnum.ERROR) {
	    rispostaControlli.reset();
	    if (StringUtils.isBlank(rcwe.getRichiestaChiusuraWarningInput().getDlMotivazione())) {
		rispostaControlli.setrBoolean(false);
		rispostaControlli.setCodErr(MessaggiWSBundle.PING_CHIUWARN_007);
		rispostaControlli
			.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.PING_CHIUWARN_007));
		setRispostaWsError(rispostaWs, SeverityEnum.ERROR, Constants.EsitoServizio.KO,
			rispostaControlli);
	    }
	}
    }

    private void setRispostaWsError(RispostaWSRichiestaChiusuraWarning rispostaWs, SeverityEnum sev,
	    Constants.EsitoServizio esito, RispostaControlli rispostaControlli) {
	rispostaWs.setSeverity(sev);
	rispostaWs.setErrorCode(rispostaControlli.getCodErr());
	rispostaWs.setErrorMessage(rispostaControlli.getDsErr());
	rispostaWs.getRichiestaChiusuraWarningRisposta().setCdEsito(esito);
	rispostaWs.getRichiestaChiusuraWarningRisposta().setCdErr(rispostaControlli.getCodErr());
	rispostaWs.getRichiestaChiusuraWarningRisposta().setDlErr(rispostaControlli.getDsErr());
    }
}
