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

import java.util.Date;

/**
 *
 * @author Gilioli_P
 */
public class OggettoRicDiarioType {

    private Long idObject;
    private String cdKeyObject;
    private String tiStatoObject;
    private String tiStatoSessione;
    private Date dtAperturaSessione;
    private Date dtChiusuraSessione;
    private Long idSessione;
    private boolean flForzaAccettazione;
    private String dlMotivoForzaAccettazione;
    private boolean flForzaWarning;
    private String dlMotivoChiusoWarning;
    private String cdErr;
    private String dsErr;
    private String tiStatoSessioneRecup;
    private Date dtAperturaSessioneRecup;
    private String chiaveUnitaDoc;
    private String xmlDatiSpecResult;

    public Long getIdObject() {
	return idObject;
    }

    public void setIdObject(Long idObject) {
	this.idObject = idObject;
    }

    public String getCdKeyObject() {
	return cdKeyObject;
    }

    public void setCdKeyObject(String cdKeyObject) {
	this.cdKeyObject = cdKeyObject;
    }

    public String getTiStatoObject() {
	return tiStatoObject;
    }

    public void setTiStatoObject(String tiStatoObject) {
	this.tiStatoObject = tiStatoObject;
    }

    public String getTiStatoSessione() {
	return tiStatoSessione;
    }

    public void setTiStatoSessione(String tiStatoSessione) {
	this.tiStatoSessione = tiStatoSessione;
    }

    public Date getDtAperturaSessione() {
	return dtAperturaSessione;
    }

    public void setDtAperturaSessione(Date dtAperturaSessione) {
	this.dtAperturaSessione = dtAperturaSessione;
    }

    public Date getDtChiusuraSessione() {
	return dtChiusuraSessione;
    }

    public void setDtChiusuraSessione(Date dtChiusuraSessione) {
	this.dtChiusuraSessione = dtChiusuraSessione;
    }

    public Long getIdSessione() {
	return idSessione;
    }

    public void setIdSessione(Long idSessione) {
	this.idSessione = idSessione;
    }

    public boolean isFlForzaAccettazione() {
	return flForzaAccettazione;
    }

    public void setFlForzaAccettazione(boolean flForzaAccettazione) {
	this.flForzaAccettazione = flForzaAccettazione;
    }

    public boolean isFlForzaWarning() {
	return flForzaWarning;
    }

    public void setFlForzaWarning(boolean flForzaWarning) {
	this.flForzaWarning = flForzaWarning;
    }

    public String getCdErr() {
	return cdErr;
    }

    public void setCdErr(String cdErr) {
	this.cdErr = cdErr;
    }

    public String getDsErr() {
	return dsErr;
    }

    public void setDsErr(String dsErr) {
	this.dsErr = dsErr;
    }

    public String getXmlDatiSpecResult() {
	return xmlDatiSpecResult;
    }

    public void setXmlDatiSpecResult(String xmlDatiSpecResult) {
	this.xmlDatiSpecResult = xmlDatiSpecResult;
    }

    public String getDlMotivoForzaAccettazione() {
	return dlMotivoForzaAccettazione;
    }

    public void setDlMotivoForzaAccettazione(String dlMotivoForzaAccettazione) {
	this.dlMotivoForzaAccettazione = dlMotivoForzaAccettazione;
    }

    public String getDlMotivoChiusoWarning() {
	return dlMotivoChiusoWarning;
    }

    public void setDlMotivoChiusoWarning(String dlMotivoChiusoWarning) {
	this.dlMotivoChiusoWarning = dlMotivoChiusoWarning;
    }

    public String getTiStatoSessioneRecup() {
	return tiStatoSessioneRecup;
    }

    public void setTiStatoSessioneRecup(String tiStatoSessioneRecup) {
	this.tiStatoSessioneRecup = tiStatoSessioneRecup;
    }

    public Date getDtAperturaSessioneRecup() {
	return dtAperturaSessioneRecup;
    }

    public void setDtAperturaSessioneRecup(Date dtAperturaSessioneRecup) {
	this.dtAperturaSessioneRecup = dtAperturaSessioneRecup;
    }

    public String getChiaveUnitaDoc() {
	return chiaveUnitaDoc;
    }

    public void setChiaveUnitaDoc(String chiaveUnitaDoc) {
	this.chiaveUnitaDoc = chiaveUnitaDoc;
    }

    @Override
    public String toString() {
	return getIdObject() + " " + getCdKeyObject() + " " + getTiStatoSessione() + " "
		+ getDtAperturaSessione() + " " + getDtChiusuraSessione() + " " + getIdSessione()
		+ " " + isFlForzaAccettazione() + " " + isFlForzaWarning() + " " + getCdErr() + " "
		+ getDsErr() + " " + getXmlDatiSpecResult();
    }
}
