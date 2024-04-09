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

package it.eng.sacerasi.ws.notificaPrelievo.helper;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import it.eng.sacerasi.common.Constants;
import it.eng.sacerasi.messages.MessaggiWSBundle;
import it.eng.sacerasi.ws.dto.IRispostaWS.SeverityEnum;
import it.eng.sacerasi.ws.dto.RispostaControlli;
import it.eng.sacerasi.ws.ejb.ControlliWS;
import it.eng.sacerasi.ws.notificaPrelievo.dto.NotificaPrelievoExt;
import it.eng.sacerasi.ws.notificaPrelievo.dto.RispostaWSNotificaPrelievo;
import it.eng.sacerasi.ws.notificaPrelievo.ejb.ControlliNotificaPrelievo;

@Stateless(mappedName = "NotificaPrelievoCheckHelper")
@LocalBean
public class NotificaPrelievoCheckHelper {

    @EJB
    private ControlliWS controlliWS;
    @EJB
    private ControlliNotificaPrelievo controlliNotif;

    public void checkRichiesta(NotificaPrelievoExt npExt, RispostaWSNotificaPrelievo rispostaWs) {
        RispostaControlli rispostaControlli = new RispostaControlli();
        // Verifica Nome Ambiente
        if (rispostaWs.getSeverity() != SeverityEnum.ERROR) {
            rispostaControlli.reset();
            rispostaControlli = controlliWS.verificaNomeAmbiente(npExt.getNotificaPrelievoInput().getNmAmbiente());
            if (!rispostaControlli.isrBoolean()) {
                if (rispostaControlli.getCodErr() == null) {
                    rispostaControlli.setCodErr(MessaggiWSBundle.PING_NOTIFPREL_001);
                    rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.PING_NOTIFPREL_001,
                            npExt.getNotificaPrelievoInput().getNmAmbiente()));
                    setRispostaWsError(rispostaWs, SeverityEnum.ERROR, Constants.EsitoServizio.KO, rispostaControlli);
                } else {
                    // Errore 666
                    setRispostaWsError(rispostaWs, SeverityEnum.ERROR, Constants.EsitoServizio.KO, rispostaControlli);
                }
            } else {
                rispostaWs.getNotificaPrelievoRisposta()
                        .setNmAmbiente(npExt.getNotificaPrelievoInput().getNmAmbiente());
            }
        }

        Long idVersatore = null;
        if (rispostaWs.getSeverity() != SeverityEnum.ERROR) {
            rispostaControlli.reset();
            rispostaControlli = controlliWS.verificaNomeVersatore(npExt.getNotificaPrelievoInput().getNmAmbiente(),
                    npExt.getNotificaPrelievoInput().getNmVersatore());
            if (!rispostaControlli.isrBoolean()) {
                if (rispostaControlli.getCodErr() == null) {
                    rispostaControlli.setCodErr(MessaggiWSBundle.PING_NOTIFPREL_002);
                    rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.PING_NOTIFPREL_002,
                            npExt.getNotificaPrelievoInput().getNmVersatore()));
                    setRispostaWsError(rispostaWs, SeverityEnum.ERROR, Constants.EsitoServizio.KO, rispostaControlli);
                } else {
                    // Errore 666
                    setRispostaWsError(rispostaWs, SeverityEnum.ERROR, Constants.EsitoServizio.KO, rispostaControlli);
                }
            } else {
                idVersatore = rispostaControlli.getrLong();
                rispostaWs.getNotificaPrelievoRisposta()
                        .setNmVersatore(npExt.getNotificaPrelievoInput().getNmVersatore());
                npExt.setFtpOutput((String) rispostaControlli.getrObject());
            }
        }
        npExt.setIdVersatore(idVersatore);

        // Verifica che chiave object sia diverso da stringa vuota e spazi
        if (rispostaWs.getSeverity() != SeverityEnum.ERROR) {
            rispostaControlli.reset();
            rispostaControlli = controlliWS.verificaNotBlankCdKeyObject(
                    npExt.getNotificaPrelievoInput().getCdKeyObject(), MessaggiWSBundle.PING_NOTIFPREL_004);
            if (!rispostaControlli.isrBoolean()) {
                setRispostaWsError(rispostaWs, SeverityEnum.ERROR, Constants.EsitoServizio.KO, rispostaControlli);
            }
        }
        // Verifica che chiave object sia lungo meno di 96 caratteri
        if (rispostaWs.getSeverity() != SeverityEnum.ERROR) {
            rispostaControlli.reset();
            rispostaControlli = controlliWS.verificaCdKeyObjectLength(npExt.getNotificaPrelievoInput().getCdKeyObject(),
                    MessaggiWSBundle.PING_NOTIFPREL_008);
            if (!rispostaControlli.isrBoolean()) {
                setRispostaWsError(rispostaWs, SeverityEnum.ERROR, Constants.EsitoServizio.KO, rispostaControlli);
            }
        }
        // Verifica esistenza oggetto su db
        Long idObject = null;
        if (rispostaWs.getSeverity() != SeverityEnum.ERROR) {
            rispostaControlli.reset();
            rispostaControlli = controlliWS.verificaCdKeyObject(npExt.getNotificaPrelievoInput().getNmAmbiente(),
                    npExt.getNotificaPrelievoInput().getNmVersatore(),
                    npExt.getNotificaPrelievoInput().getCdKeyObject());
            if (!rispostaControlli.isrBoolean()) {
                rispostaControlli.setCodErr(MessaggiWSBundle.PING_NOTIFPREL_005);
                rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.PING_NOTIFPREL_005,
                        npExt.getNotificaPrelievoInput().getCdKeyObject()));
                setRispostaWsError(rispostaWs, SeverityEnum.ERROR, Constants.EsitoServizio.KO, rispostaControlli);
            } else {
                rispostaWs.getNotificaPrelievoRisposta()
                        .setCdKeyObject(npExt.getNotificaPrelievoInput().getCdKeyObject());
                idObject = rispostaControlli.getrLong();
            }
        }
        npExt.setIdObject(idObject);

        Long idSessione = null;
        if (rispostaWs.getSeverity() != SeverityEnum.ERROR) {
            rispostaControlli.reset();
            rispostaControlli = controlliNotif.verificaOggetto(idObject);
            if (!rispostaControlli.isrBoolean()) {
                setRispostaWsError(rispostaWs, SeverityEnum.ERROR, Constants.EsitoServizio.KO, rispostaControlli);
            } else {
                idSessione = rispostaControlli.getrLong();
            }
        }
        npExt.setIdSessioneRecup(idSessione);
    }

    private void setRispostaWsError(RispostaWSNotificaPrelievo rispostaWs, SeverityEnum sev,
            Constants.EsitoServizio esito, RispostaControlli rispostaControlli) {
        rispostaWs.setSeverity(sev);
        rispostaWs.setErrorCode(rispostaControlli.getCodErr());
        rispostaWs.setErrorMessage(rispostaControlli.getDsErr());
        rispostaWs.getNotificaPrelievoRisposta().setCdEsito(esito);
        rispostaWs.getNotificaPrelievoRisposta().setCdErr(rispostaControlli.getCodErr());
        rispostaWs.getNotificaPrelievoRisposta().setDlErr(rispostaControlli.getDsErr());
    }
}
