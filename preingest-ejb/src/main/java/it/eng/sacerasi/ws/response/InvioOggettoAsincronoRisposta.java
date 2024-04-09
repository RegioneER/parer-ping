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
package it.eng.sacerasi.ws.response;

import it.eng.sacerasi.common.Constants;

/**
 *
 * @author Gilioli_P
 */
public class InvioOggettoAsincronoRisposta {

    private Constants.EsitoServizio cdEsito;
    private String cdErr;
    private String dsErr;
    private String nmAmbiente;
    private String nmVersatore;
    private String cdKeyObject;
    private String nmTipoObject;
    private boolean flFileCifrato;
    private boolean flForzaWarning;
    private boolean flForzaAccettazione;
    private String cdVersioneXML;
    private String xml;

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
     * @return the dsErr
     */
    public String getDsErr() {
        return dsErr;
    }

    /**
     * @param dsErr
     *            the dsErr to set
     */
    public void setDsErr(String dsErr) {
        this.dsErr = dsErr;
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

    /**
     * @return the nmTipoObject
     */
    public String getNmTipoObject() {
        return nmTipoObject;
    }

    /**
     * @param nmTipoObject
     *            the nmTipoObject to set
     */
    public void setNmTipoObject(String nmTipoObject) {
        this.nmTipoObject = nmTipoObject;
    }

    /**
     * @return the flFileCifrato
     */
    public boolean isFlFileCifrato() {
        return flFileCifrato;
    }

    /**
     * @param flFileCifrato
     *            the flFileCifrato to set
     */
    public void setFlFileCifrato(boolean flFileCifrato) {
        this.flFileCifrato = flFileCifrato;
    }

    public boolean isFlForzaWarning() {
        return flForzaWarning;
    }

    public void setFlForzaWarning(boolean flForzaWarning) {
        this.flForzaWarning = flForzaWarning;
    }

    /**
     * @return the flForzaAccettazione
     */
    public boolean isFlForzaAccettazione() {
        return flForzaAccettazione;
    }

    /**
     * @param flForzaAccettazione
     *            the flForzaAccettazione to set
     */
    public void setFlForzaAccettazione(boolean flForzaAccettazione) {
        this.flForzaAccettazione = flForzaAccettazione;
    }

    /**
     * @return the cdVersioneXML
     */
    public String getCdVersioneXML() {
        return cdVersioneXML;
    }

    /**
     * @param cdVersioneXML
     *            the cdVersioneXML to set
     */
    public void setCdVersioneXML(String cdVersioneXML) {
        this.cdVersioneXML = cdVersioneXML;
    }

    /**
     * @return the xml
     */
    public String getXml() {
        return xml;
    }

    /**
     * @param xml
     *            the xml to set
     */
    public void setXml(String xml) {
        this.xml = xml;
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
}
