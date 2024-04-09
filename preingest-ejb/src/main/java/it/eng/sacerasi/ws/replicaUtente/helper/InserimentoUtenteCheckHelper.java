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

package it.eng.sacerasi.ws.replicaUtente.helper;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import it.eng.integriam.server.ws.Costanti;
import it.eng.integriam.server.ws.Costanti.EsitoServizio;
import it.eng.sacerasi.messages.MessaggiWSBundle;
import it.eng.sacerasi.ws.dto.IRispostaWS.SeverityEnum;
import it.eng.sacerasi.ws.dto.RispostaControlli;
import it.eng.sacerasi.ws.ejb.ControlliReplicaUtente;
import it.eng.sacerasi.ws.replicaUtente.dto.InserimentoUtenteExt;
import it.eng.sacerasi.ws.replicaUtente.dto.RispostaWSInserimentoUtente;

@Stateless(mappedName = "InserimentoUtenteCheckHelper")
@LocalBean
public class InserimentoUtenteCheckHelper {

    @EJB
    private ControlliReplicaUtente controlliRU;

    public void checkSessione(InserimentoUtenteExt inserimentoUtenteExt, RispostaWSInserimentoUtente rispostaWs) {
        RispostaControlli rispostaControlli = new RispostaControlli();
        // Verifica Utente
        if (rispostaWs.getSeverity() != SeverityEnum.ERROR) {
            rispostaControlli.reset();
            rispostaControlli = controlliRU
                    .verificaEsistenzaUtenteAttivo(inserimentoUtenteExt.getInserimentoUtenteInput().getIdUserIam());
            if (rispostaControlli.isrBoolean()) {
                if (rispostaControlli.getCodErr() == null) {
                    rispostaControlli.setCodErr(MessaggiWSBundle.SERVIZI_USR_002);
                    rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.SERVIZI_USR_002,
                            inserimentoUtenteExt.getInserimentoUtenteInput().getIdUserIam()));
                    setRispostaWsError(rispostaWs, SeverityEnum.ERROR, Costanti.EsitoServizio.KO, rispostaControlli);
                } else {
                    // Errore 666
                    setRispostaWsError(rispostaWs, SeverityEnum.ERROR, Costanti.EsitoServizio.KO, rispostaControlli);
                }
            }
        }

        // Verifica Userid
        if (rispostaWs.getSeverity() != SeverityEnum.ERROR) {
            rispostaControlli.reset();
            rispostaControlli = controlliRU
                    .verificaEsistenzaNmUserid(inserimentoUtenteExt.getInserimentoUtenteInput().getNmUserid());
            if (rispostaControlli.isrBoolean()) {
                if (rispostaControlli.getCodErr() == null) {
                    rispostaControlli.setCodErr(MessaggiWSBundle.SERVIZI_USR_003);
                    rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.SERVIZI_USR_003,
                            inserimentoUtenteExt.getInserimentoUtenteInput().getNmUserid()));
                    setRispostaWsError(rispostaWs, SeverityEnum.ERROR, Costanti.EsitoServizio.KO, rispostaControlli);
                } else {
                    // Errore 666
                    setRispostaWsError(rispostaWs, SeverityEnum.ERROR, Costanti.EsitoServizio.KO, rispostaControlli);
                }
            }
        }
    }

    private void setRispostaWsError(RispostaWSInserimentoUtente rispostaWs, SeverityEnum sev, EsitoServizio esito,
            RispostaControlli rispostaControlli) {
        rispostaWs.setSeverity(sev);
        rispostaWs.setErrorCode(rispostaControlli.getCodErr());
        rispostaWs.setErrorMessage(rispostaControlli.getDsErr());
        rispostaWs.getInserimentoUtenteRisposta().setCdEsito(esito);
        rispostaWs.getInserimentoUtenteRisposta().setCdErr(rispostaControlli.getCodErr());
        rispostaWs.getInserimentoUtenteRisposta().setDsErr(rispostaControlli.getDsErr());
    }
}
