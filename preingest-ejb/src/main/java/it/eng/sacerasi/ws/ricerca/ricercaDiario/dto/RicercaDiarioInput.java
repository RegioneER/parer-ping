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

package it.eng.sacerasi.ws.ricerca.ricercaDiario.dto;

/**
 *
 * @author Gilioli_P
 */
public class RicercaDiarioInput {

    private String nmAmbiente;
    private String nmVersatore;
    // private String cdPassword;
    private String nmTipoObject;
    private String cdKeyObject;
    private Long idSessione;
    private String tiStatoObject;
    private boolean flTutteSessioni;
    private int niRecordInizio;
    private int niRecordResultSet;
    private String xmlDatiSpecOutput;
    private String xmlDatiSpecFiltri;
    private String xmlDatiSpecOrder;

    // public RicercaDiarioInput(String nmAmbiente, String nmVersatore,
    // String cdPassword, String nmTipoObject, String cdKeyObject, Integer idSessione, String
    // tiStatoObject,
    // boolean flTutteSessioni, int niRecordInizio, int niRecordResultSet,
    // String xmlDatiSpecOutput, String xmlDatiSpecFiltri, String xmlDatiSpecOrder) {
    public RicercaDiarioInput(String nmAmbiente, String nmVersatore, String nmTipoObject,
	    String cdKeyObject, Long idSessione, String tiStatoObject, boolean flTutteSessioni,
	    int niRecordInizio, int niRecordResultSet, String xmlDatiSpecOutput,
	    String xmlDatiSpecFiltri, String xmlDatiSpecOrder) {

	this.nmAmbiente = nmAmbiente;
	this.nmVersatore = nmVersatore;
	// this.cdPassword = cdPassword;
	this.nmTipoObject = nmTipoObject;
	this.cdKeyObject = cdKeyObject;
	this.idSessione = idSessione;
	this.tiStatoObject = tiStatoObject;
	this.flTutteSessioni = flTutteSessioni;
	this.niRecordInizio = niRecordInizio;
	this.niRecordResultSet = niRecordResultSet;
	this.xmlDatiSpecFiltri = xmlDatiSpecFiltri;
	this.xmlDatiSpecOrder = xmlDatiSpecOrder;
	this.xmlDatiSpecOutput = xmlDatiSpecOutput;
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

    public Long getIdSessione() {
	return idSessione;
    }

    public void setIdSessione(Long idSessione) {
	this.idSessione = idSessione;
    }

    public String getTiStatoObject() {
	return tiStatoObject;
    }

    public void setTiStatoObject(String tiStatoObject) {
	this.tiStatoObject = tiStatoObject;
    }

    public boolean isFlTutteSessioni() {
	return flTutteSessioni;
    }

    public void setFlTutteSessioni(boolean flTutteSessioni) {
	this.flTutteSessioni = flTutteSessioni;
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
