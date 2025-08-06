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

package it.eng.sacerasi.ws.richiestaSopClassList.helper;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import it.eng.sacerasi.common.Constants;
import it.eng.sacerasi.entity.PigSopClassDicom;
import it.eng.sacerasi.messages.MessaggiWSBundle;
import it.eng.sacerasi.ws.dto.IRispostaWS.SeverityEnum;
import it.eng.sacerasi.ws.dto.RispostaControlli;
import it.eng.sacerasi.ws.ejb.ControlliWS;
import it.eng.sacerasi.ws.richiestaSopClassList.dto.RichiestaSopClassListExt;
import it.eng.sacerasi.ws.richiestaSopClassList.dto.RispostaWSRichiestaSopClassList;
import it.eng.sacerasi.ws.richiestaSopClassList.ejb.RichiestaSopClassListHelper;

@Stateless(mappedName = "RichiestaSopClassListCheckHelper")
@LocalBean
public class RichiestaSopClassListCheckHelper {

    @EJB
    private ControlliWS controlliWS;
    @EJB
    private RichiestaSopClassListHelper helper;

    @SuppressWarnings("unchecked")
    public void checkRichiesta(RichiestaSopClassListExt rsclExt,
	    RispostaWSRichiestaSopClassList rispostaWs) {
	RispostaControlli rispostaControlli = new RispostaControlli();

	// Verifica Nome Ambiente
	if (rispostaWs.getSeverity() != SeverityEnum.ERROR) {
	    rispostaControlli.reset();
	    rispostaControlli = controlliWS
		    .verificaNomeAmbiente(rsclExt.getRichiestaSopClassListInput().getNmAmbiente());
	    if (!rispostaControlli.isrBoolean()) {
		if (rispostaControlli.getCodErr() == null) {
		    rispostaControlli.setCodErr(MessaggiWSBundle.PING_SOP_001);
		    rispostaControlli
			    .setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.PING_SOP_001,
				    rsclExt.getRichiestaSopClassListInput().getNmAmbiente()));
		    setRispostaWsError(rispostaWs, SeverityEnum.ERROR, Constants.EsitoServizio.KO,
			    rispostaControlli);
		} else {
		    // Errore 666
		    setRispostaWsError(rispostaWs, SeverityEnum.ERROR, Constants.EsitoServizio.KO,
			    rispostaControlli);
		}
	    } else {
		rispostaWs.getRichiestaSopClassListRisposta()
			.setNmAmbiente(rsclExt.getRichiestaSopClassListInput().getNmAmbiente());
	    }
	}

	Long idVersatore = null;
	if (rispostaWs.getSeverity() != SeverityEnum.ERROR) {
	    rispostaControlli.reset();
	    rispostaControlli = controlliWS.verificaNomeVersatore(
		    rsclExt.getRichiestaSopClassListInput().getNmAmbiente(),
		    rsclExt.getRichiestaSopClassListInput().getNmVersatore());
	    if (!rispostaControlli.isrBoolean()) {
		if (rispostaControlli.getCodErr() == null) {
		    rispostaControlli.setCodErr(MessaggiWSBundle.PING_SOP_002);
		    rispostaControlli
			    .setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.PING_SOP_002,
				    rsclExt.getRichiestaSopClassListInput().getNmVersatore()));
		    setRispostaWsError(rispostaWs, SeverityEnum.ERROR, Constants.EsitoServizio.KO,
			    rispostaControlli);
		} else {
		    // Errore 666
		    setRispostaWsError(rispostaWs, SeverityEnum.ERROR, Constants.EsitoServizio.KO,
			    rispostaControlli);
		}
	    } else {
		idVersatore = rispostaControlli.getrLong();
		rispostaWs.getRichiestaSopClassListRisposta()
			.setNmVersatore(rsclExt.getRichiestaSopClassListInput().getNmVersatore());
	    }
	}
	rsclExt.setIdVersatore(idVersatore);

	if (rispostaWs.getSeverity() != SeverityEnum.ERROR) {
	    rispostaControlli.reset();
	    rispostaControlli = helper.getSopClassListByIdVersatore(idVersatore);
	    if (!rispostaControlli.isrBoolean()) {
		setRispostaWsError(rispostaWs, SeverityEnum.ERROR, Constants.EsitoServizio.KO,
			rispostaControlli);
	    } else {
		rsclExt.setListaSopClass((List<PigSopClassDicom>) rispostaControlli.getrObject());
	    }
	}
    }

    private void setRispostaWsError(RispostaWSRichiestaSopClassList rispostaWs, SeverityEnum sev,
	    Constants.EsitoServizio esito, RispostaControlli rispostaControlli) {
	rispostaWs.setSeverity(sev);
	rispostaWs.setErrorCode(rispostaControlli.getCodErr());
	rispostaWs.setErrorMessage(rispostaControlli.getDsErr());
	rispostaWs.getRichiestaSopClassListRisposta().setCdEsito(esito);
	rispostaWs.getRichiestaSopClassListRisposta().setCdErr(rispostaControlli.getCodErr());
	rispostaWs.getRichiestaSopClassListRisposta().setDsErr(rispostaControlli.getDsErr());
    }
}
