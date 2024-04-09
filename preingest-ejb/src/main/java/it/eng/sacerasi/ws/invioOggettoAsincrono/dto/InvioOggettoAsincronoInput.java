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
package it.eng.sacerasi.ws.invioOggettoAsincrono.dto;

/**
 *
 * @author Gilioli_P
 */
public class InvioOggettoAsincronoInput {

    private String nmAmbiente;
    private String nmVersatore;
    private String cdKeyObject;
    private String nmTipoObject;
    private boolean flFileCifrato;
    private boolean flForzaWarning;
    private boolean flForzaAccettazione;
    private String dlMotivazione;
    private String cdVersioneXml;
    private String xml;
    private String tiPrioritaVersamento;

    /**
     * Metodo costruttore
     *
     * @param nmAmbiente
     *            nome ambiente
     * @param nmVersatore
     *            nome versatore
     * @param cdKeyObject
     *            numero oggetto
     * @param nmTipoObject
     *            tipo oggetto
     * @param flFileCifrato
     *            flag 1/0 (true/false)
     * @param flForzaWarning
     *            flag 1/0 (true/false)
     * @param flForzaAccettazione
     *            flag 1/0 (true/false)
     * @param dlMotivazione
     *            descrizione motivazione
     * @param cdVersioneXml
     *            versione xml
     * @param xml
     *            contenuto xml
     * @param tiPrioritaVersamento
     *            priorita di versamento
     */
    public InvioOggettoAsincronoInput(String nmAmbiente, String nmVersatore, String cdKeyObject, String nmTipoObject,
            boolean flFileCifrato, boolean flForzaWarning, boolean flForzaAccettazione, String dlMotivazione,
            String cdVersioneXml, String xml, String tiPrioritaVersamento) {
        this.nmAmbiente = nmAmbiente;
        this.nmVersatore = nmVersatore;
        this.cdKeyObject = cdKeyObject;
        this.nmTipoObject = nmTipoObject;
        this.flFileCifrato = flFileCifrato;
        this.flForzaWarning = flForzaWarning;
        this.flForzaAccettazione = flForzaAccettazione;
        this.dlMotivazione = dlMotivazione;
        this.cdVersioneXml = cdVersioneXml;
        this.xml = xml;
        this.tiPrioritaVersamento = tiPrioritaVersamento;
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

    /**
     * @return the flForzaWarning
     */
    public boolean isFlForzaWarning() {
        return flForzaWarning;
    }

    /**
     * @param flForzaWarning
     *            the flForzaWarning to set
     */
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
     * @return the dlMotivazione
     */
    public String getDlMotivazione() {
        return dlMotivazione;
    }

    /**
     * @param dlMotivazione
     *            the dlMotivazione to set
     */
    public void setDlMotivazione(String dlMotivazione) {
        this.dlMotivazione = dlMotivazione;
    }

    /**
     * @return the cdVersioneXml
     */
    public String getCdVersioneXml() {
        return cdVersioneXml;
    }

    /**
     * @param cdVersioneXml
     *            the cdVersioneXml to set
     */
    public void setCdVersioneXml(String cdVersioneXml) {
        this.cdVersioneXml = cdVersioneXml;
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

    public String getTiPrioritaVersamento() {
        return tiPrioritaVersamento;
    }

    public void setTiPrioritaVersamento(String tiPrioritaVersamento) {
        this.tiPrioritaVersamento = tiPrioritaVersamento;
    }

}
