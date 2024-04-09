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
import it.eng.sacerasi.ws.replicaUtente.dto.CancellaUtenteExt;
import it.eng.sacerasi.ws.replicaUtente.dto.RispostaWSCancellaUtente;

@Stateless(mappedName = "CancellaUtenteCheckHelper")
@LocalBean
public class CancellaUtenteCheckHelper {

    @EJB
    private ControlliReplicaUtente controlliRU;

    public void checkSessione(CancellaUtenteExt cancellaUtenteExt, RispostaWSCancellaUtente rispostaWs) {
        RispostaControlli rispostaControlli = new RispostaControlli();
        // Verifica Utente
        if (rispostaWs.getSeverity() != SeverityEnum.ERROR) {
            rispostaControlli.reset();
            rispostaControlli = controlliRU.verificaEsistenzaUtente(cancellaUtenteExt.getIdUserIam());
            if (!rispostaControlli.isrBoolean()) {
                if (rispostaControlli.getCodErr() == null) {
                    rispostaControlli.setCodErr(MessaggiWSBundle.SERVIZI_USR_004);
                    rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.SERVIZI_USR_004,
                            cancellaUtenteExt.getIdUserIam()));
                    setRispostaWsError(rispostaWs, SeverityEnum.ERROR, Costanti.EsitoServizio.KO, rispostaControlli);
                } else {
                    // Errore 666
                    setRispostaWsError(rispostaWs, SeverityEnum.ERROR, Costanti.EsitoServizio.KO, rispostaControlli);
                }
            }
        }
    }

    private void setRispostaWsError(RispostaWSCancellaUtente rispostaWs, SeverityEnum sev, EsitoServizio esito,
            RispostaControlli rispostaControlli) {
        rispostaWs.setSeverity(sev);
        rispostaWs.setErrorCode(rispostaControlli.getCodErr());
        rispostaWs.setErrorMessage(rispostaControlli.getDsErr());
        rispostaWs.getCancellaUtenteRisposta().setCdEsito(esito);
        rispostaWs.getCancellaUtenteRisposta().setCdErr(rispostaControlli.getCodErr());
        rispostaWs.getCancellaUtenteRisposta().setDsErr(rispostaControlli.getDsErr());
    }
}
