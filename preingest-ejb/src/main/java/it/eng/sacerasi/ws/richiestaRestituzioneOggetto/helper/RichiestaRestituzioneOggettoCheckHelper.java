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

package it.eng.sacerasi.ws.richiestaRestituzioneOggetto.helper;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import it.eng.sacerasi.common.Constants;
import it.eng.sacerasi.messages.MessaggiWSBundle;
import it.eng.sacerasi.ws.dto.IRispostaWS.SeverityEnum;
import it.eng.sacerasi.ws.dto.RispostaControlli;
import it.eng.sacerasi.ws.ejb.ControlliWS;
import it.eng.sacerasi.ws.richiestaRestituzioneOggetto.dto.RichiestaRestituzioneOggettoExt;
import it.eng.sacerasi.ws.richiestaRestituzioneOggetto.dto.RispostaWSRichiestaRestituzioneOggetto;
import it.eng.sacerasi.ws.richiestaRestituzioneOggetto.ejb.ControlliRichiestaRestituzioneOggetto;

@Stateless(mappedName = "RichiestaRestituzioneOggettoCheckHelper")
@LocalBean
public class RichiestaRestituzioneOggettoCheckHelper {

    @EJB
    private ControlliWS controlliWS;
    @EJB
    private ControlliRichiestaRestituzioneOggetto controlliRichRestObj;

    public void checkRichiesta(RichiestaRestituzioneOggettoExt rroExt,
            RispostaWSRichiestaRestituzioneOggetto rispostaWs) {
        RispostaControlli rispostaControlli = new RispostaControlli();

        // Verifica Nome Ambiente
        if (rispostaWs.getSeverity() != SeverityEnum.ERROR) {
            rispostaControlli.reset();
            rispostaControlli = controlliWS
                    .verificaNomeAmbiente(rroExt.getRichiestaRestituzioneOggettoInput().getNmAmbiente());
            if (!rispostaControlli.isrBoolean()) {
                if (rispostaControlli.getCodErr() == null) {
                    rispostaControlli.setCodErr(MessaggiWSBundle.PING_RICHOBJ_001);
                    rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.PING_RICHOBJ_001,
                            rroExt.getRichiestaRestituzioneOggettoInput().getNmAmbiente()));
                    setRispostaWsError(rispostaWs, SeverityEnum.ERROR, Constants.EsitoServizio.KO, rispostaControlli);
                } else {
                    // Errore 666
                    setRispostaWsError(rispostaWs, SeverityEnum.ERROR, Constants.EsitoServizio.KO, rispostaControlli);
                }
            } else {
                rispostaWs.getRichiestaRestituzioneOggettoRisposta()
                        .setNmAmbiente(rroExt.getRichiestaRestituzioneOggettoInput().getNmAmbiente());
            }
        }

        Long idVersatore = null;
        if (rispostaWs.getSeverity() != SeverityEnum.ERROR) {
            rispostaControlli.reset();
            rispostaControlli = controlliWS.verificaNomeVersatore(
                    rroExt.getRichiestaRestituzioneOggettoInput().getNmAmbiente(),
                    rroExt.getRichiestaRestituzioneOggettoInput().getNmVersatore());
            if (!rispostaControlli.isrBoolean()) {
                if (rispostaControlli.getCodErr() == null) {
                    rispostaControlli.setCodErr(MessaggiWSBundle.PING_RICHOBJ_002);
                    rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.PING_RICHOBJ_002,
                            rroExt.getRichiestaRestituzioneOggettoInput().getNmVersatore()));
                    setRispostaWsError(rispostaWs, SeverityEnum.ERROR, Constants.EsitoServizio.KO, rispostaControlli);
                } else {
                    // Errore 666
                    setRispostaWsError(rispostaWs, SeverityEnum.ERROR, Constants.EsitoServizio.KO, rispostaControlli);
                }
            } else {
                idVersatore = rispostaControlli.getrLong();
                rispostaWs.getRichiestaRestituzioneOggettoRisposta()
                        .setNmVersatore(rroExt.getRichiestaRestituzioneOggettoInput().getNmVersatore());
            }
        }
        rroExt.setIdVersatore(idVersatore);

        // // Verifica password affinché corrisponda alla password del versatore
        // if (rispostaWs.getSeverity() != SeverityEnum.ERROR) {
        // rispostaControlli.reset();
        // rispostaControlli = controlliWS.verificaPassword(idVersatore,
        // rroExt.getRichiestaRestituzioneOggettoInput().getCdPassword());
        // if (!rispostaControlli.isrBoolean()) {
        // if (rispostaControlli.getCodErr() == null) {
        // rispostaControlli.setCodErr(MessaggiWSBundle.PING_RICHOBJ_003);
        // rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.PING_RICHOBJ_003));
        // setRispostaWsError(SeverityEnum.ERROR, Constants.EsitoServizio.KO);
        // } else {
        // // Errore 666
        // setRispostaWsError(SeverityEnum.ERROR, Constants.EsitoServizio.KO);
        // }
        // }
        // }

        // Verifica che chiave object sia diverso da stringa vuota e spazi
        if (rispostaWs.getSeverity() != SeverityEnum.ERROR) {
            rispostaControlli.reset();
            rispostaControlli = controlliWS.verificaNotBlankCdKeyObject(
                    rroExt.getRichiestaRestituzioneOggettoInput().getCdKeyObject(), MessaggiWSBundle.PING_RICHOBJ_004);
            if (!rispostaControlli.isrBoolean()) {
                setRispostaWsError(rispostaWs, SeverityEnum.ERROR, Constants.EsitoServizio.KO, rispostaControlli);
            }
        }
        // Verifica che chiave object sia lungo meno di 96 caratteri
        if (rispostaWs.getSeverity() != SeverityEnum.ERROR) {
            rispostaControlli.reset();
            rispostaControlli = controlliWS.verificaCdKeyObjectLength(
                    rroExt.getRichiestaRestituzioneOggettoInput().getCdKeyObject(), MessaggiWSBundle.PING_RICHOBJ_009);
            if (!rispostaControlli.isrBoolean()) {
                setRispostaWsError(rispostaWs, SeverityEnum.ERROR, Constants.EsitoServizio.KO, rispostaControlli);
            }
        }
        // Verifica esistenza oggetto su db
        Long idObject = null;
        if (rispostaWs.getSeverity() != SeverityEnum.ERROR) {
            rispostaControlli.reset();
            rispostaControlli = controlliWS.verificaCdKeyObject(
                    rroExt.getRichiestaRestituzioneOggettoInput().getNmAmbiente(),
                    rroExt.getRichiestaRestituzioneOggettoInput().getNmVersatore(),
                    rroExt.getRichiestaRestituzioneOggettoInput().getCdKeyObject());
            if (!rispostaControlli.isrBoolean()) {
                rispostaControlli.setCodErr(MessaggiWSBundle.PING_RICHOBJ_005);
                rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.PING_RICHOBJ_005));
                setRispostaWsError(rispostaWs, SeverityEnum.ERROR, Constants.EsitoServizio.KO, rispostaControlli);
            } else {
                rispostaWs.getRichiestaRestituzioneOggettoRisposta()
                        .setCdKeyObject(rroExt.getRichiestaRestituzioneOggettoInput().getCdKeyObject());
                idObject = rispostaControlli.getrLong();
            }
        }
        rroExt.setIdObject(idObject);

        if (rispostaWs.getSeverity() != SeverityEnum.ERROR) {
            rispostaControlli.reset();
            rispostaControlli = controlliRichRestObj.verificaOggetto(idObject);
            if (!rispostaControlli.isrBoolean()) {
                setRispostaWsError(rispostaWs, SeverityEnum.ERROR, Constants.EsitoServizio.KO, rispostaControlli);
            }
        }
    }

    private void setRispostaWsError(RispostaWSRichiestaRestituzioneOggetto rispostaWs, SeverityEnum sev,
            Constants.EsitoServizio esito, RispostaControlli rispostaControlli) {
        rispostaWs.setSeverity(sev);
        rispostaWs.setErrorCode(rispostaControlli.getCodErr());
        rispostaWs.setErrorMessage(rispostaControlli.getDsErr());
        rispostaWs.getRichiestaRestituzioneOggettoRisposta().setCdEsito(esito);
        rispostaWs.getRichiestaRestituzioneOggettoRisposta().setCdErr(rispostaControlli.getCodErr());
        rispostaWs.getRichiestaRestituzioneOggettoRisposta().setDlErr(rispostaControlli.getDsErr());
    }
}
