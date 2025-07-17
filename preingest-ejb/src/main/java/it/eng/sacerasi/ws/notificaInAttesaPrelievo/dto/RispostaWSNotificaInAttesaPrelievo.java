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

package it.eng.sacerasi.ws.notificaInAttesaPrelievo.dto;

import it.eng.sacerasi.ws.dto.IRispostaWS;
import it.eng.sacerasi.ws.response.NotificaInAttesaPrelievoRisposta;

/**
 *
 * @author Gilioli_P
 */
public class RispostaWSNotificaInAttesaPrelievo implements IRispostaWS {
    private static final long serialVersionUID = 1L;
    private IRispostaWS.SeverityEnum severity = IRispostaWS.SeverityEnum.OK;
    private IRispostaWS.ErrorTypeEnum errorType = IRispostaWS.ErrorTypeEnum.NOERROR;
    private String errorMessage;
    private String errorCode;
    private NotificaInAttesaPrelievoRisposta notificaInAttesaPrelievoRisposta;

    /**
     * @return the severity
     */
    @Override
    public IRispostaWS.SeverityEnum getSeverity() {
	return severity;
    }

    /**
     * @param severity the severity to set
     */
    @Override
    public void setSeverity(IRispostaWS.SeverityEnum severity) {
	this.severity = severity;
    }

    /**
     * @return the errorType
     */
    @Override
    public IRispostaWS.ErrorTypeEnum getErrorType() {
	return errorType;
    }

    /**
     * @param errorType the errorType to set
     */
    @Override
    public void setErrorType(IRispostaWS.ErrorTypeEnum errorType) {
	this.errorType = errorType;
    }

    /**
     * @return the errorMessage
     */
    @Override
    public String getErrorMessage() {
	return errorMessage;
    }

    /**
     * @param errorMessage the errorMessage to set
     */
    @Override
    public void setErrorMessage(String errorMessage) {
	this.errorMessage = errorMessage;
    }

    /**
     * @return the errorCode
     */
    @Override
    public String getErrorCode() {
	return errorCode;
    }

    /**
     * @param errorCode the errorCode to set
     */
    @Override
    public void setErrorCode(String errorCode) {
	this.errorCode = errorCode;
    }

    public NotificaInAttesaPrelievoRisposta getNotificaInAttesaPrelievoRisposta() {
	return notificaInAttesaPrelievoRisposta;
    }

    public void setNotificaInAttesaPrelievoRisposta(
	    NotificaInAttesaPrelievoRisposta notificaInAttesaPrelievoRisposta) {
	this.notificaInAttesaPrelievoRisposta = notificaInAttesaPrelievoRisposta;
    }
}
