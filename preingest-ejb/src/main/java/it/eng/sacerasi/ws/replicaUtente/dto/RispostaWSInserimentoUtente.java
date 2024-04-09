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

package it.eng.sacerasi.ws.replicaUtente.dto;

import it.eng.integriam.server.ws.reputente.InserimentoUtenteRisposta;
import it.eng.sacerasi.ws.dto.IRispostaWS;
import it.eng.sacerasi.ws.util.AvanzamentoWs;

/**
 *
 * @author Gilioli_P
 */
public class RispostaWSInserimentoUtente implements IRispostaWS {

    private static final long serialVersionUID = 1L;
    private SeverityEnum severity = SeverityEnum.OK;
    private ErrorTypeEnum errorType = ErrorTypeEnum.NOERROR;
    private String errorMessage;
    private String errorCode;
    private InserimentoUtenteRisposta inserimentoUtenteRisposta;

    public SeverityEnum getSeverity() {
        return severity;
    }

    public void setSeverity(SeverityEnum severity) {
        this.severity = severity;
    }

    public ErrorTypeEnum getErrorType() {
        return errorType;
    }

    public void setErrorType(ErrorTypeEnum errorType) {
        this.errorType = errorType;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public InserimentoUtenteRisposta getInserimentoUtenteRisposta() {
        return inserimentoUtenteRisposta;
    }

    public void setInserimentoUtenteRisposta(InserimentoUtenteRisposta inserimentoUtenteRisposta) {
        this.inserimentoUtenteRisposta = inserimentoUtenteRisposta;
    }

    public AvanzamentoWs getAvanzamento() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setAvanzamento(AvanzamentoWs avanzamento) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setEsitoWsErrBundle(String errCode, Object... params) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setEsitoWsErrBundle(String errCode) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setEsitoWsWarnBundle(String errCode, Object... params) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setEsitoWsWarnBundle(String errCode) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setEsitoWsError(String errCode, String errMessage) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setEsitoWsWarning(String errCode, String errMessage) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
