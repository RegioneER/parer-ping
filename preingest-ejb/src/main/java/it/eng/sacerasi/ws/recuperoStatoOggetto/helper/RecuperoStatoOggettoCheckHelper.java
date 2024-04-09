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

package it.eng.sacerasi.ws.recuperoStatoOggetto.helper;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import it.eng.sacerasi.common.Constants;
import it.eng.sacerasi.messages.MessaggiWSBundle;
import it.eng.sacerasi.ws.dto.IRispostaWS.SeverityEnum;
import it.eng.sacerasi.ws.dto.RispostaControlli;
import it.eng.sacerasi.ws.ejb.ControlliWS;
import it.eng.sacerasi.ws.recuperoStatoOggetto.dto.RecuperoStatoOggettoExt;
import it.eng.sacerasi.ws.recuperoStatoOggetto.dto.RispostaWSRecuperoStatoOggetto;

@Stateless(mappedName = "RecuperoStatoOggettoCheckHelper")
@LocalBean
public class RecuperoStatoOggettoCheckHelper {

    @EJB
    private ControlliWS controlliWS;

    public void checkRichiesta(RecuperoStatoOggettoExt rsoe, RispostaWSRecuperoStatoOggetto rispostaWs) {
        RispostaControlli rispostaControlli = new RispostaControlli();
        // Verifica Nome Ambiente
        if (rispostaWs.getSeverity() != SeverityEnum.ERROR) {
            rispostaControlli.reset();
            rispostaControlli = controlliWS.verificaNomeAmbiente(rsoe.getRecuperoStatoOggettoInput().getNmAmbiente());
            if (!rispostaControlli.isrBoolean()) {
                if (rispostaControlli.getCodErr() == null) {
                    rispostaControlli.setCodErr(MessaggiWSBundle.PING_RECDIPSTATO_001);
                    rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.PING_RECDIPSTATO_001,
                            rsoe.getRecuperoStatoOggettoInput().getNmAmbiente()));
                    setRispostaWsError(rispostaWs, SeverityEnum.ERROR, Constants.EsitoServizio.KO, rispostaControlli);
                } else {
                    // Errore 666
                    setRispostaWsError(rispostaWs, SeverityEnum.ERROR, Constants.EsitoServizio.KO, rispostaControlli);
                }
            } else {
                rispostaWs.getRecuperoStatoOggettoRisposta()
                        .setNmAmbiente(rsoe.getRecuperoStatoOggettoInput().getNmAmbiente());
            }
        }

        Long idVersatore = null;
        if (rispostaWs.getSeverity() != SeverityEnum.ERROR) {
            rispostaControlli.reset();
            rispostaControlli = controlliWS.verificaNomeVersatore(rsoe.getRecuperoStatoOggettoInput().getNmAmbiente(),
                    rsoe.getRecuperoStatoOggettoInput().getNmVersatore());
            if (!rispostaControlli.isrBoolean()) {
                if (rispostaControlli.getCodErr() == null) {
                    rispostaControlli.setCodErr(MessaggiWSBundle.PING_RECDIPSTATO_002);
                    rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.PING_RECDIPSTATO_001,
                            rsoe.getRecuperoStatoOggettoInput().getNmVersatore()));
                    setRispostaWsError(rispostaWs, SeverityEnum.ERROR, Constants.EsitoServizio.KO, rispostaControlli);
                } else {
                    // Errore 666
                    setRispostaWsError(rispostaWs, SeverityEnum.ERROR, Constants.EsitoServizio.KO, rispostaControlli);
                }
            } else {
                idVersatore = rispostaControlli.getrLong();
                rispostaWs.getRecuperoStatoOggettoRisposta()
                        .setNmVersatore(rsoe.getRecuperoStatoOggettoInput().getNmVersatore());
            }
        }

        // Verifica che chiave object sia diverso da stringa vuota e spazi
        if (rispostaWs.getSeverity() != SeverityEnum.ERROR) {
            rispostaControlli.reset();
            rispostaControlli = controlliWS.verificaNotBlankCdKeyObject(
                    rsoe.getRecuperoStatoOggettoInput().getCdKeyObject(), MessaggiWSBundle.PING_RECDIPSTATO_004);
            if (!rispostaControlli.isrBoolean()) {
                setRispostaWsError(rispostaWs, SeverityEnum.ERROR, Constants.EsitoServizio.KO, rispostaControlli);
            }
        }

        // Verifica che chiave object sia lungo meno di 96 caratteri
        if (rispostaWs.getSeverity() != SeverityEnum.ERROR) {
            rispostaControlli.reset();
            rispostaControlli = controlliWS.verificaCdKeyObjectLength(
                    rsoe.getRecuperoStatoOggettoInput().getCdKeyObject(), MessaggiWSBundle.PING_RECDIPSTATO_006);
            if (!rispostaControlli.isrBoolean()) {
                setRispostaWsError(rispostaWs, SeverityEnum.ERROR, Constants.EsitoServizio.KO, rispostaControlli);
            }
        }

        // Verifica esistenza oggetto su db
        Long idObject = null;
        if (rispostaWs.getSeverity() != SeverityEnum.ERROR) {
            rispostaControlli.reset();
            rispostaControlli = controlliWS.verificaCdKeyObject(rsoe.getRecuperoStatoOggettoInput().getNmAmbiente(),
                    rsoe.getRecuperoStatoOggettoInput().getNmVersatore(),
                    rsoe.getRecuperoStatoOggettoInput().getCdKeyObject());
            if (!rispostaControlli.isrBoolean()) {
                rispostaControlli.setCodErr(MessaggiWSBundle.PING_RECDIPSTATO_005);
                rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.PING_RECDIPSTATO_005,
                        rsoe.getRecuperoStatoOggettoInput().getCdKeyObject()));
                setRispostaWsError(rispostaWs, SeverityEnum.ERROR, Constants.EsitoServizio.KO, rispostaControlli);
            } else {
                rispostaWs.getRecuperoStatoOggettoRisposta()
                        .setCdKeyObject(rsoe.getRecuperoStatoOggettoInput().getCdKeyObject());
                idObject = rispostaControlli.getrLong();
            }
        }
        rsoe.setIdObject(idObject);
    }

    private void setRispostaWsError(RispostaWSRecuperoStatoOggetto rispostaWs, SeverityEnum sev,
            Constants.EsitoServizio esito, RispostaControlli rispostaControlli) {
        rispostaWs.setSeverity(sev);
        rispostaWs.setErrorCode(rispostaControlli.getCodErr());
        rispostaWs.setErrorMessage(rispostaControlli.getDsErr());
        rispostaWs.getRecuperoStatoOggettoRisposta().setCdEsito(esito);
        rispostaWs.getRecuperoStatoOggettoRisposta().setCdErr(rispostaControlli.getCodErr());
        rispostaWs.getRecuperoStatoOggettoRisposta().setDlErr(rispostaControlli.getDsErr());
    }
}
