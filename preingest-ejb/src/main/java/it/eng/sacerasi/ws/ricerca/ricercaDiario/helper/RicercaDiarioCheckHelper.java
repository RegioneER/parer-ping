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

package it.eng.sacerasi.ws.ricerca.ricercaDiario.helper;

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
import it.eng.sacerasi.ws.ricerca.ricercaDiario.dto.RicercaDiarioExt;
import it.eng.sacerasi.ws.ricerca.ricercaDiario.dto.RispostaWSRicercaDiario;

@Stateless(mappedName = "RicercaDiarioCheckHelper")
@LocalBean
public class RicercaDiarioCheckHelper {

    @EJB
    private ControlliWS controlliWS;
    @EJB
    private ControlliRicerca controlliRicerca;

    public void checkSessione(RicercaDiarioExt ricercaDiarioExt, RispostaWSRicercaDiario rispostaWs) {
        RispostaControlli rispostaControlli = new RispostaControlli();
        // Verifica Nome Ambiente
        if (rispostaWs.getSeverity() != SeverityEnum.ERROR) {
            rispostaControlli.reset();
            rispostaControlli = controlliWS
                    .verificaNomeAmbiente(ricercaDiarioExt.getRicercaDiarioInput().getNmAmbiente());
            if (!rispostaControlli.isrBoolean()) {
                if (rispostaControlli.getCodErr() == null) {
                    rispostaControlli.setCodErr(MessaggiWSBundle.PING_DIARIO_001);
                    rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.PING_DIARIO_001,
                            ricercaDiarioExt.getRicercaDiarioInput().getNmAmbiente()));
                    setRispostaWsError(rispostaWs, SeverityEnum.ERROR, Constants.EsitoServizio.KO, rispostaControlli);
                } else {
                    // Errore 666
                    setRispostaWsError(rispostaWs, SeverityEnum.ERROR, Constants.EsitoServizio.KO, rispostaControlli);
                }
            }
            // In ogni caso setto in risposta il nome ambiente ricevuto in input
            rispostaWs.getRicercaDiarioRisposta()
                    .setNmAmbiente(ricercaDiarioExt.getRicercaDiarioInput().getNmAmbiente());
        }

        // Verifica nome versatore nell'ambito dell'ambiente
        Long idVersatore = null;
        if (rispostaWs.getSeverity() != SeverityEnum.ERROR) {
            rispostaControlli.reset();
            rispostaControlli = controlliWS.verificaNomeVersatore(
                    ricercaDiarioExt.getRicercaDiarioInput().getNmAmbiente(),
                    ricercaDiarioExt.getRicercaDiarioInput().getNmVersatore());
            if (!rispostaControlli.isrBoolean()) {
                if (rispostaControlli.getCodErr() == null) {
                    rispostaControlli.setCodErr(MessaggiWSBundle.PING_DIARIO_002);
                    rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.PING_DIARIO_002,
                            ricercaDiarioExt.getRicercaDiarioInput().getNmVersatore()));
                    setRispostaWsError(rispostaWs, SeverityEnum.ERROR, Constants.EsitoServizio.KO, rispostaControlli);
                } else {
                    // Errore 666
                    setRispostaWsError(rispostaWs, SeverityEnum.ERROR, Constants.EsitoServizio.KO, rispostaControlli);
                }
            } else {
                // Setto l'id versatore per eventuali (in caso di assenza di errore) utilizzi successivi
                idVersatore = rispostaControlli.getrLong();
            }
            // In ogni caso setto in risposta il nome versatore ricevuto in input
            rispostaWs.getRicercaDiarioRisposta()
                    .setNmVersatore(ricercaDiarioExt.getRicercaDiarioInput().getNmVersatore());
        }
        ricercaDiarioExt.setIdVersatore(idVersatore);

        Long idTipoObject = null;
        // Verifica nome tipo object
        if (rispostaWs.getSeverity() != SeverityEnum.ERROR) {
            rispostaControlli.reset();
            rispostaControlli = controlliWS.verificaNomeTipoObject(idVersatore,
                    ricercaDiarioExt.getRicercaDiarioInput().getNmTipoObject(), MessaggiWSBundle.PING_DIARIO_004);
            if (!rispostaControlli.isrBoolean()) {
                setRispostaWsError(rispostaWs, SeverityEnum.ERROR, Constants.EsitoServizio.KO, rispostaControlli);
            } else {
                idTipoObject = rispostaControlli.getrLong();
            }
            // In ogni caso setto in risposta il nome tipo object ricevuto in input
            rispostaWs.getRicercaDiarioRisposta()
                    .setNmTipoObject(ricercaDiarioExt.getRicercaDiarioInput().getNmTipoObject());
        }
        // Setto l'id tipo object per eventuali (in caso di assenza di errore) utilizzi successivi
        ricercaDiarioExt.setIdTipoObject(idTipoObject);

        // Verifica presenza nome tipo object con i file XML
        if (rispostaWs.getSeverity() != SeverityEnum.ERROR) {
            rispostaControlli.reset();
            rispostaControlli = controlliRicerca.verificaNomeTipoObjectConXML(
                    ricercaDiarioExt.getRicercaDiarioInput().getNmTipoObject(),
                    ricercaDiarioExt.getRicercaDiarioInput().getXmlDatiSpecFiltri(),
                    ricercaDiarioExt.getRicercaDiarioInput().getXmlDatiSpecOutput(),
                    ricercaDiarioExt.getRicercaDiarioInput().getXmlDatiSpecOrder());
            if (!rispostaControlli.isrBoolean()) {
                rispostaControlli.setCodErr(MessaggiWSBundle.PING_DIARIO_005);
                rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.PING_DIARIO_005));
                setRispostaWsError(rispostaWs, SeverityEnum.ERROR, Constants.EsitoServizio.KO, rispostaControlli);
            }
        }
    }

    private void setRispostaWsError(RispostaWSRicercaDiario rispostaWs, SeverityEnum sev, EsitoServizio esito,
            RispostaControlli rispostaControlli) {
        rispostaWs.setSeverity(sev);
        rispostaWs.setErrorCode(rispostaControlli.getCodErr());
        rispostaWs.setErrorMessage(rispostaControlli.getDsErr());
        rispostaWs.getRicercaDiarioRisposta().setCdEsito(esito);
        rispostaWs.getRicercaDiarioRisposta().setCdErr(rispostaControlli.getCodErr());
        rispostaWs.getRicercaDiarioRisposta().setDsErr(rispostaControlli.getDsErr());
    }
}
