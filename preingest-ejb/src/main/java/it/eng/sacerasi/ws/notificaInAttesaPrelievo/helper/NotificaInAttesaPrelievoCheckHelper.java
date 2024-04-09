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

package it.eng.sacerasi.ws.notificaInAttesaPrelievo.helper;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import it.eng.sacerasi.common.Constants;
import it.eng.sacerasi.messages.MessaggiWSBundle;
import it.eng.sacerasi.ws.dto.IRispostaWS.SeverityEnum;
import it.eng.sacerasi.ws.dto.RispostaControlli;
import it.eng.sacerasi.ws.ejb.ControlliWS;
import it.eng.sacerasi.ws.notificaInAttesaPrelievo.dto.NotificaInAttesaPrelievoExt;
import it.eng.sacerasi.ws.notificaInAttesaPrelievo.dto.RispostaWSNotificaInAttesaPrelievo;
import it.eng.sacerasi.ws.notificaInAttesaPrelievo.ejb.ControlliNotificaInAttesaPrelievo;

@Stateless(mappedName = "NotificaInAttesaPrelievoCheckHelper")
@LocalBean
public class NotificaInAttesaPrelievoCheckHelper {

    @EJB
    private ControlliWS controlliWS;
    @EJB
    private ControlliNotificaInAttesaPrelievo controlliNotif;

    public void checkRichiesta(NotificaInAttesaPrelievoExt niapExt, RispostaWSNotificaInAttesaPrelievo rispostaWs) {
        RispostaControlli rispostaControlli = new RispostaControlli();
        // Verifica Nome Ambiente
        if (rispostaWs.getSeverity() != SeverityEnum.ERROR) {
            rispostaControlli.reset();
            rispostaControlli = controlliWS
                    .verificaNomeAmbiente(niapExt.getNotificaInAttesaPrelievoInput().getNmAmbiente());
            if (!rispostaControlli.isrBoolean()) {
                if (rispostaControlli.getCodErr() == null) {
                    rispostaControlli.setCodErr(MessaggiWSBundle.PING_NOTIFATTESAPREL_001);
                    rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.PING_NOTIFATTESAPREL_001,
                            niapExt.getNotificaInAttesaPrelievoInput().getNmAmbiente()));
                    setRispostaWsError(rispostaWs, SeverityEnum.ERROR, Constants.EsitoServizio.KO, rispostaControlli);
                } else {
                    // Errore 666
                    setRispostaWsError(rispostaWs, SeverityEnum.ERROR, Constants.EsitoServizio.KO, rispostaControlli);
                }
            } else {
                rispostaWs.getNotificaInAttesaPrelievoRisposta()
                        .setNmAmbiente(niapExt.getNotificaInAttesaPrelievoInput().getNmAmbiente());
            }
        }

        Long idVersatore = null;
        if (rispostaWs.getSeverity() != SeverityEnum.ERROR) {
            rispostaControlli.reset();
            rispostaControlli = controlliWS.verificaNomeVersatore(
                    niapExt.getNotificaInAttesaPrelievoInput().getNmAmbiente(),
                    niapExt.getNotificaInAttesaPrelievoInput().getNmVersatore());
            if (!rispostaControlli.isrBoolean()) {
                if (rispostaControlli.getCodErr() == null) {
                    rispostaControlli.setCodErr(MessaggiWSBundle.PING_NOTIFATTESAPREL_002);
                    rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.PING_NOTIFATTESAPREL_002,
                            niapExt.getNotificaInAttesaPrelievoInput().getNmVersatore()));
                    setRispostaWsError(rispostaWs, SeverityEnum.ERROR, Constants.EsitoServizio.KO, rispostaControlli);
                } else {
                    // Errore 666
                    setRispostaWsError(rispostaWs, SeverityEnum.ERROR, Constants.EsitoServizio.KO, rispostaControlli);
                }
            } else {
                idVersatore = rispostaControlli.getrLong();
                rispostaWs.getNotificaInAttesaPrelievoRisposta()
                        .setNmVersatore(niapExt.getNotificaInAttesaPrelievoInput().getNmVersatore());
                niapExt.setFtpOutput((String) rispostaControlli.getrObject());
            }
        }
        niapExt.setIdVersatore(idVersatore);

        // Verifica che chiave object sia diverso da stringa vuota e spazi
        if (rispostaWs.getSeverity() != SeverityEnum.ERROR) {
            rispostaControlli.reset();
            rispostaControlli = controlliWS.verificaNotBlankCdKeyObject(
                    niapExt.getNotificaInAttesaPrelievoInput().getCdKeyObject(),
                    MessaggiWSBundle.PING_NOTIFATTESAPREL_004);
            if (!rispostaControlli.isrBoolean()) {
                setRispostaWsError(rispostaWs, SeverityEnum.ERROR, Constants.EsitoServizio.KO, rispostaControlli);
            }
        }
        // Verifica che chiave object sia lungo meno di 96 caratteri
        if (rispostaWs.getSeverity() != SeverityEnum.ERROR) {
            rispostaControlli.reset();
            rispostaControlli = controlliWS.verificaCdKeyObjectLength(
                    niapExt.getNotificaInAttesaPrelievoInput().getCdKeyObject(),
                    MessaggiWSBundle.PING_NOTIFATTESAPREL_008);
            if (!rispostaControlli.isrBoolean()) {
                setRispostaWsError(rispostaWs, SeverityEnum.ERROR, Constants.EsitoServizio.KO, rispostaControlli);
            }
        }
        // Verifica esistenza oggetto su db
        Long idObject = null;
        if (rispostaWs.getSeverity() != SeverityEnum.ERROR) {
            rispostaControlli.reset();
            rispostaControlli = controlliWS.verificaCdKeyObject(
                    niapExt.getNotificaInAttesaPrelievoInput().getNmAmbiente(),
                    niapExt.getNotificaInAttesaPrelievoInput().getNmVersatore(),
                    niapExt.getNotificaInAttesaPrelievoInput().getCdKeyObject());
            if (!rispostaControlli.isrBoolean()) {
                rispostaControlli.setCodErr(MessaggiWSBundle.PING_NOTIFATTESAPREL_005);
                rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.PING_NOTIFATTESAPREL_005,
                        niapExt.getNotificaInAttesaPrelievoInput().getCdKeyObject()));
                setRispostaWsError(rispostaWs, SeverityEnum.ERROR, Constants.EsitoServizio.KO, rispostaControlli);
            } else {
                rispostaWs.getNotificaInAttesaPrelievoRisposta()
                        .setCdKeyObject(niapExt.getNotificaInAttesaPrelievoInput().getCdKeyObject());
                idObject = rispostaControlli.getrLong();
            }
        }
        niapExt.setIdObject(idObject);

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
        niapExt.setIdSessioneRecup(idSessione);
    }

    private void setRispostaWsError(RispostaWSNotificaInAttesaPrelievo rispostaWs, SeverityEnum sev,
            Constants.EsitoServizio esito, RispostaControlli rispostaControlli) {
        rispostaWs.setSeverity(sev);
        rispostaWs.setErrorCode(rispostaControlli.getCodErr());
        rispostaWs.setErrorMessage(rispostaControlli.getDsErr());
        rispostaWs.getNotificaInAttesaPrelievoRisposta().setCdEsito(esito);
        rispostaWs.getNotificaInAttesaPrelievoRisposta().setCdErr(rispostaControlli.getCodErr());
        rispostaWs.getNotificaInAttesaPrelievoRisposta().setDlErr(rispostaControlli.getDsErr());
    }
}
