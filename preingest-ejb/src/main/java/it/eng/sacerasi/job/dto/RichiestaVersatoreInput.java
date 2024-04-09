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
 * @author Bonora_L
 */
public class RichiestaVersatoreInput {

    private String urlRichiesta;
    private String nmAmbiente;
    private String nmVers;
    private String cdKeyObject;
    private Integer timeout;

    public RichiestaVersatoreInput(String urlRichiesta, String nmAmbiente, String nmVers, String cdKeyObject,
            Integer timeout) {
        this.urlRichiesta = urlRichiesta;
        this.nmAmbiente = nmAmbiente;
        this.nmVers = nmVers;
        this.cdKeyObject = cdKeyObject;
        this.timeout = timeout;
    }

    /**
     * @return the urlRichiesta
     */
    public String getUrlRichiesta() {
        return urlRichiesta;
    }

    /**
     * @param urlRichiesta
     *            the urlRichiesta to set
     */
    public void setUrlRichiesta(String urlRichiesta) {
        this.urlRichiesta = urlRichiesta;
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
     * @return the nmVers
     */
    public String getNmVers() {
        return nmVers;
    }

    /**
     * @param nmVers
     *            the nmVers to set
     */
    public void setNmVers(String nmVers) {
        this.nmVers = nmVers;
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

    /**
     * @return the timeout
     */
    public Integer getTimeout() {
        return timeout;
    }

    /**
     * @param timeout
     *            the timeout to set
     */
    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }
}
