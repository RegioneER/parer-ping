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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.eng.sacerasi.ws.puliziaNotificato.dto;

import it.eng.sacerasi.ws.dto.IRispostaWS;
import it.eng.sacerasi.ws.response.PuliziaNotificatoRisposta;

/**
 *
 * @author Bonora_L
 */
public class RispostaWSPuliziaNotificato implements IRispostaWS {

    private IRispostaWS.SeverityEnum severity = IRispostaWS.SeverityEnum.OK;
    private IRispostaWS.ErrorTypeEnum errorType = IRispostaWS.ErrorTypeEnum.NOERROR;
    private String errorMessage;
    private String errorCode;
    private PuliziaNotificatoRisposta puliziaNotificatoRisposta;

    @Override
    public String getErrorCode() {
        return errorCode;
    }

    @Override
    public String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public ErrorTypeEnum getErrorType() {
        return errorType;
    }

    @Override
    public SeverityEnum getSeverity() {
        return severity;
    }

    @Override
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    @Override
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public void setErrorType(ErrorTypeEnum errorType) {
        this.errorType = errorType;
    }

    @Override
    public void setSeverity(SeverityEnum severity) {
        this.severity = severity;
    }

    public PuliziaNotificatoRisposta getPuliziaNotificatoRisposta() {
        return puliziaNotificatoRisposta;
    }

    public void setPuliziaNotificatoRisposta(PuliziaNotificatoRisposta puliziaNotificatoRisposta) {
        this.puliziaNotificatoRisposta = puliziaNotificatoRisposta;
    }

}
