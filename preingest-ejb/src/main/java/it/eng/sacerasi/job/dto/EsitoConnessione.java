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

package it.eng.sacerasi.job.dto;

/**
 *
 * @author Agati_D
 */
public class EsitoConnessione {

    private boolean erroreConnessione;
    private String descrErr;
    private String xmlResponse;
    private String descrErrConnessione;
    private String codiceEsito;
    private String codiceErrore;
    private String messaggioErrore;

    public boolean isErroreConnessione() {
        return erroreConnessione;
    }

    public void setErroreConnessione(boolean erroreConnessione) {
        this.erroreConnessione = erroreConnessione;
    }

    public String getDescrErr() {
        return descrErr;
    }

    public void setDescrErr(String descrErr) {
        this.descrErr = descrErr;
    }

    public String getXmlResponse() {
        return xmlResponse;
    }

    public void setXmlResponse(String xmlResponse) {
        this.xmlResponse = xmlResponse;
    }

    public String getDescrErrConnessione() {
        return descrErrConnessione;
    }

    public void setDescrErrConnessione(String descrErrConnessione) {
        this.descrErrConnessione = descrErrConnessione;
    }

    public String getCodiceEsito() {
        return codiceEsito;
    }

    public void setCodiceEsito(String codiceEsito) {
        this.codiceEsito = codiceEsito;
    }

    public String getCodiceErrore() {
        return codiceErrore;
    }

    public void setCodiceErrore(String codiceErrore) {
        this.codiceErrore = codiceErrore;
    }

    public String getMessaggioErrore() {
        return messaggioErrore;
    }

    public void setMessaggioErrore(String messaggioErrore) {
        this.messaggioErrore = messaggioErrore;
    }
}
