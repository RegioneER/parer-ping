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

package it.eng.sacerasi.ws.response;

import it.eng.sacerasi.common.Constants;

/**
 *
 * @author Bonora_L
 */
public class RichiestaChiusuraWarningRisposta {

    private Constants.EsitoServizio cdEsito;
    private String cdErr;
    private String dlErr;
    private String nmAmbiente;
    private String nmVersatore;
    private String cdKeyObject;

    /**
     * @return the cdEsito
     */
    public Constants.EsitoServizio getCdEsito() {
        return cdEsito;
    }

    /**
     * @param cdEsito
     *            the cdEsito to set
     */
    public void setCdEsito(Constants.EsitoServizio cdEsito) {
        this.cdEsito = cdEsito;
    }

    /**
     * @return the cdErr
     */
    public String getCdErr() {
        return cdErr;
    }

    /**
     * @param cdErr
     *            the cdErr to set
     */
    public void setCdErr(String cdErr) {
        this.cdErr = cdErr;
    }

    /**
     * @return the dlErr
     */
    public String getDlErr() {
        return dlErr;
    }

    /**
     * @param dlErr
     *            the dlErr to set
     */
    public void setDlErr(String dlErr) {
        this.dlErr = dlErr;
    }

    /**
     * @return the nmAmbiente
     */
    public String getNmAmbiente() {
        return nmAmbiente;
    }

    /**
     * @param nmAmbiente
     *            the nmAmbiente to set
     */
    public void setNmAmbiente(String nmAmbiente) {
        this.nmAmbiente = nmAmbiente;
    }

    /**
     * @return the nmVersatore
     */
    public String getNmVersatore() {
        return nmVersatore;
    }

    /**
     * @param nmVersatore
     *            the nmVersatore to set
     */
    public void setNmVersatore(String nmVersatore) {
        this.nmVersatore = nmVersatore;
    }

    /**
     * @return the cdKeyObject
     */
    public String getCdKeyObject() {
        return cdKeyObject;
    }

    /**
     * @param cdKeyObject
     *            the cdKeyObject to set
     */
    public void setCdKeyObject(String cdKeyObject) {
        this.cdKeyObject = cdKeyObject;
    }
}
