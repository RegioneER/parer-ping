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

package it.eng.sacerasi.ws.ricerca.ricercaRestituzioniOggetti.dto;

import java.util.Date;

/**
 *
 * @author Gilioli_P
 */
public class RicercaRestituzioniOggettiInput {

    private String nmAmbiente;
    private String nmVersatore;
    // private String cdPassword;
    private String nmTipoObject;
    private String cdKeyObject;
    private String tiStatoSessione;
    private Date dtAperturaSessioneDa;
    private Date dtAperturaSessioneA;
    private int niRecordInizio;
    private int niRecordResultSet;
    private String xmlDatiSpecOutput;
    private String xmlDatiSpecFiltri;
    private String xmlDatiSpecOrder;

    // public RicercaRestituzioniOggettiInput(String nmAmbiente, String nmVersatore,
    // String cdPassword, String nmTipoObject, String cdKeyObject, String tiStatoSessione,
    // Date dtAperturaSessioneDa, Date dtAperturaSessioneA, int niRecordInizio, int niRecordResultSet,
    // String xmlDatiSpecOutput, String xmlDatiSpecFiltri, String xmlDatiSpecOrder) {
    public RicercaRestituzioniOggettiInput(String nmAmbiente, String nmVersatore, String nmTipoObject,
            String cdKeyObject, String tiStatoSessione, Date dtAperturaSessioneDa, Date dtAperturaSessioneA,
            int niRecordInizio, int niRecordResultSet, String xmlDatiSpecOutput, String xmlDatiSpecFiltri,
            String xmlDatiSpecOrder) {

        this.nmAmbiente = nmAmbiente;
        this.nmVersatore = nmVersatore;
        // this.cdPassword = cdPassword;
        this.nmTipoObject = nmTipoObject;
        this.cdKeyObject = cdKeyObject;
        this.tiStatoSessione = tiStatoSessione;
        this.dtAperturaSessioneDa = dtAperturaSessioneDa;
        this.dtAperturaSessioneA = dtAperturaSessioneA;
        this.niRecordInizio = niRecordInizio;
        this.niRecordResultSet = niRecordResultSet;
        this.xmlDatiSpecOutput = xmlDatiSpecOutput;
        this.xmlDatiSpecFiltri = xmlDatiSpecFiltri;
        this.xmlDatiSpecOrder = xmlDatiSpecOrder;
    }

    public String getNmAmbiente() {
        return nmAmbiente;
    }

    public void setNmAmbiente(String nmAmbiente) {
        this.nmAmbiente = nmAmbiente;
    }

    public String getNmVersatore() {
        return nmVersatore;
    }

    public void setNmVersatore(String nmVersatore) {
        this.nmVersatore = nmVersatore;
    }

    // public String getCdPassword() {
    // return cdPassword;
    // }
    //
    // public void setCdPassword(String cdPassword) {
    // this.cdPassword = cdPassword;
    // }

    public String getNmTipoObject() {
        return nmTipoObject;
    }

    public void setNmTipoObject(String nmTipoObject) {
        this.nmTipoObject = nmTipoObject;
    }

    public String getCdKeyObject() {
        return cdKeyObject;
    }

    public void setCdKeyObject(String cdKeyObject) {
        this.cdKeyObject = cdKeyObject;
    }

    public String getTiStatoSessione() {
        return tiStatoSessione;
    }

    public void setTiStatoSessione(String tiStatoSessione) {
        this.tiStatoSessione = tiStatoSessione;
    }

    public Date getDtAperturaSessioneDa() {
        return dtAperturaSessioneDa;
    }

    public void setDtAperturaSessioneDa(Date dtAperturaSessioneDa) {
        this.dtAperturaSessioneDa = dtAperturaSessioneDa;
    }

    public Date getDtAperturaSessioneA() {
        return dtAperturaSessioneA;
    }

    public void setDtAperturaSessioneA(Date dtAperturaSessioneA) {
        this.dtAperturaSessioneA = dtAperturaSessioneA;
    }

    public int getNiRecordInizio() {
        return niRecordInizio;
    }

    public void setNiRecordInizio(int niRecordInizio) {
        this.niRecordInizio = niRecordInizio;
    }

    public int getNiRecordResultSet() {
        return niRecordResultSet;
    }

    public void setNiRecordResultSet(int niRecordResultSet) {
        this.niRecordResultSet = niRecordResultSet;
    }

    public String getXmlDatiSpecOutput() {
        return xmlDatiSpecOutput;
    }

    public void setXmlDatiSpecOutput(String xmlDatiSpecOutput) {
        this.xmlDatiSpecOutput = xmlDatiSpecOutput;
    }

    public String getXmlDatiSpecFiltri() {
        return xmlDatiSpecFiltri;
    }

    public void setXmlDatiSpecFiltri(String xmlDatiSpecFiltri) {
        this.xmlDatiSpecFiltri = xmlDatiSpecFiltri;
    }

    public String getXmlDatiSpecOrder() {
        return xmlDatiSpecOrder;
    }

    public void setXmlDatiSpecOrder(String xmlDatiSpecOrder) {
        this.xmlDatiSpecOrder = xmlDatiSpecOrder;
    }
}
