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

package it.eng.sacerasi.viewEntity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * The persistent class for the MON_V_LIS_VERS_OBJ database table.
 *
 */
@Entity
@Table(name = "MON_V_LIS_VERS_OBJ")
public class MonVLisVersObj implements Serializable {

    private static final long serialVersionUID = 1L;
    private String cdErr;
    private String dlErr;
    private Date dtApertura;
    private String flNonRisolub;
    private String flVerif;
    private BigDecimal idObject;
    private BigDecimal idSessioneIngest;
    private String tiStato;
    private Date tsRegStato;
    private String note;
    private String nmReportTrasfOS;

    public MonVLisVersObj() {
    }

    @Column(name = "CD_ERR")
    public String getCdErr() {
	return this.cdErr;
    }

    public void setCdErr(String cdErr) {
	this.cdErr = cdErr;
    }

    @Column(name = "DL_ERR")
    public String getDlErr() {
	return this.dlErr;
    }

    public void setDlErr(String dlErr) {
	this.dlErr = dlErr;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_APERTURA")
    public Date getDtApertura() {
	return this.dtApertura;
    }

    public void setDtApertura(Date dtApertura) {
	this.dtApertura = dtApertura;
    }

    @Column(name = "FL_NON_RISOLUB", columnDefinition = "char")
    public String getFlNonRisolub() {
	return this.flNonRisolub;
    }

    public void setFlNonRisolub(String flNonRisolub) {
	this.flNonRisolub = flNonRisolub;
    }

    @Column(name = "FL_VERIF", columnDefinition = "char")
    public String getFlVerif() {
	return this.flVerif;
    }

    public void setFlVerif(String flVerif) {
	this.flVerif = flVerif;
    }

    @Column(name = "ID_OBJECT")
    public BigDecimal getIdObject() {
	return this.idObject;
    }

    public void setIdObject(BigDecimal idObject) {
	this.idObject = idObject;
    }

    @Id
    @Column(name = "ID_SESSIONE_INGEST")
    public BigDecimal getIdSessioneIngest() {
	return this.idSessioneIngest;
    }

    public void setIdSessioneIngest(BigDecimal idSessioneIngest) {
	this.idSessioneIngest = idSessioneIngest;
    }

    @Column(name = "TI_STATO")
    public String getTiStato() {
	return this.tiStato;
    }

    public void setTiStato(String tiStato) {
	this.tiStato = tiStato;
    }

    @Column(name = "NOTE")
    public String getNote() {
	return this.note;
    }

    public void setNote(String note) {
	this.note = note;
    }

    @Column(name = "TS_REG_STATO")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getTsRegStato() {
	return this.tsRegStato;
    }

    public void setTsRegStato(Date tsRegStato) {
	this.tsRegStato = tsRegStato;
    }

    @Column(name = "NM_REPORT_TRASF_OS")
    public String getNmReportTrasfOS() {
	return nmReportTrasfOS;
    }

    public void setNmReportTrasfOS(String NmReportTrasfOS) {
	this.nmReportTrasfOS = NmReportTrasfOS;
    }
}
