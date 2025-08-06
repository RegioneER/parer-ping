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

/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */
package it.eng.sacerasi.job.preparaxml.dto;

import it.eng.parer.ws.xml.versReq.UnitaDocumentaria;
import it.eng.parer.ws.xml.versReqMultiMedia.IndiceMM;
import it.eng.sacerasi.common.Chiave;
import it.eng.sacerasi.common.Constants;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;

/**
 *
 * @author Fioravanti_F
 */
public class UnitaDocObject implements Serializable {

    private static final long serialVersionUID = 1L;
    private transient UnitaDocumentaria unitaDocumentariaXmlBean;
    private String unitaDocumentariaXml;
    private transient IndiceMM indiceMMXmlBean;
    private String indiceMMXml;
    private String urnUDXml; // urn all'interno del file zip dell'xml di versamento (per
			     // zip-con-xml)
    private String versioneWsVersamento; // la versione del WS di versamento di Sacer da invocare
					 // per il versamento UD
    //
    private Chiave chiaveUd;
    private long sizeInByte;
    private String urnFileZip; // urn del file zip
    private ArrayList<FileUnitaDoc> listaFileUnitaDoc;
    //
    private long lastAssignedID = 0; // id dell'ultimo componente dichiarato per l'UD (per
				     // zip-no-xml)
    private long totaleDocPrincipali = 0; // numero di file/documento di tipo PRINCIPALE nell'UD
					  // (per zip-no-xml) ->
					  // DEVE essere = 1
    private String chiaveCompatta; // memorizza la chiave nel formato compatto registro^anno^numero,
				   // usato quando si
				   // processano i file .ZIP
    // Analisi 1.19 - Modifica versatore
    private BigDecimal idOrganizSacer;
    private boolean simulaVersamento = false;
    private String nmUserIdSacer;
    private String nmAmbienteSacer;
    private String nmStrutSacer;
    private String nmEnteSacer;

    public UnitaDocObject() {
	// imposta il default della versione del WS versamento a 1.5
	versioneWsVersamento = Constants.VERSIONE_XML_SACER;
    }

    public UnitaDocumentaria getUnitaDocumentariaXmlBean() {
	return unitaDocumentariaXmlBean;
    }

    public void setUnitaDocumentariaXmlBean(UnitaDocumentaria unitaDocumentariaXmlBean) {
	this.unitaDocumentariaXmlBean = unitaDocumentariaXmlBean;
    }

    public String getUnitaDocumentariaXml() {
	return unitaDocumentariaXml;
    }

    public void setUnitaDocumentariaXml(String unitaDocumentariaXml) {
	this.unitaDocumentariaXml = unitaDocumentariaXml;
    }

    public IndiceMM getIndiceMMXmlBean() {
	return indiceMMXmlBean;
    }

    public void setIndiceMMXmlBean(IndiceMM indiceMMXmlBean) {
	this.indiceMMXmlBean = indiceMMXmlBean;
    }

    public String getIndiceMMXml() {
	return indiceMMXml;
    }

    public void setIndiceMMXml(String indiceMMXml) {
	this.indiceMMXml = indiceMMXml;
    }

    public String getUrnUDXml() {
	return urnUDXml;
    }

    public void setUrnUDXml(String urnUDXml) {
	this.urnUDXml = urnUDXml;
    }

    public String getVersioneWsVersamento() {
	return versioneWsVersamento;
    }

    public void setVersioneWsVersamento(String versioneWsVersamento) {
	this.versioneWsVersamento = versioneWsVersamento;
    }

    public Chiave getChiaveUd() {
	return chiaveUd;
    }

    public void setChiaveUd(Chiave chiaveUd) {
	this.chiaveUd = chiaveUd;
    }

    public long getSizeInByte() {
	return sizeInByte;
    }

    public void setSizeInByte(long sizeInByte) {
	this.sizeInByte = sizeInByte;
    }

    public String getUrnFileZip() {
	return urnFileZip;
    }

    public void setUrnFileZip(String urnFileZip) {
	this.urnFileZip = urnFileZip;
    }

    public ArrayList<FileUnitaDoc> getListaFileUnitaDoc() {
	return listaFileUnitaDoc;
    }

    public void setListaFileUnitaDoc(ArrayList<FileUnitaDoc> listaFileUnitaDoc) {
	this.listaFileUnitaDoc = listaFileUnitaDoc;
    }

    public long getLastAssignedID() {
	return lastAssignedID;
    }

    public void setLastAssignedID(long lastAssignedID) {
	this.lastAssignedID = lastAssignedID;
    }

    public long getTotaleDocPrincipali() {
	return totaleDocPrincipali;
    }

    public void setTotaleDocPrincipali(long totaleDocPrincipali) {
	this.totaleDocPrincipali = totaleDocPrincipali;
    }

    public String getChiaveCompatta() {
	return chiaveCompatta;
    }

    public void setChiaveCompatta(String chiaveCompatta) {
	this.chiaveCompatta = chiaveCompatta;
    }

    public BigDecimal getIdOrganizSacer() {
	return idOrganizSacer;
    }

    public void setIdOrganizSacer(BigDecimal idOrganizSacer) {
	this.idOrganizSacer = idOrganizSacer;
    }

    public boolean isSimulaVersamento() {
	return simulaVersamento;
    }

    public void setSimulaVersamento(boolean simulaVersamento) {
	this.simulaVersamento = simulaVersamento;
    }

    public String getNmUserIdSacer() {
	return nmUserIdSacer;
    }

    public void setNmUserIdSacer(String nmUserIdSacer) {
	this.nmUserIdSacer = nmUserIdSacer;
    }

    public String getNmAmbienteSacer() {
	return nmAmbienteSacer;
    }

    public void setNmAmbienteSacer(String nmAmbienteSacer) {
	this.nmAmbienteSacer = nmAmbienteSacer;
    }

    public String getNmStrutSacer() {
	return nmStrutSacer;
    }

    public void setNmStrutSacer(String nmStrutSacer) {
	this.nmStrutSacer = nmStrutSacer;
    }

    public String getNmEnteSacer() {
	return nmEnteSacer;
    }

    public void setNmEnteSacer(String nmEnteSacer) {
	this.nmEnteSacer = nmEnteSacer;
    }

}
