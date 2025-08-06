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

package it.eng.sacerasi.ws.recuperoStatoOggetto.dto;

import it.eng.sacerasi.ws.dto.IRispostaWS;
import it.eng.sacerasi.ws.response.RecuperoStatoOggettoRisposta;

/**
 *
 * @author Gilioli_P
 */
public class RispostaWSRecuperoStatoOggetto implements IRispostaWS {

    private IRispostaWS.SeverityEnum severity = IRispostaWS.SeverityEnum.OK;
    private IRispostaWS.ErrorTypeEnum errorType = IRispostaWS.ErrorTypeEnum.NOERROR;
    private String errorMessage;
    private String errorCode;
    private RecuperoStatoOggettoRisposta recuperoStatoOggettoRisposta;

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

    public RecuperoStatoOggettoRisposta getRecuperoStatoOggettoRisposta() {
	return recuperoStatoOggettoRisposta;
    }

    public void setRecuperoStatoOggettoRisposta(
	    RecuperoStatoOggettoRisposta recuperoStatoOggettoRisposta) {
	this.recuperoStatoOggettoRisposta = recuperoStatoOggettoRisposta;
    }
}
