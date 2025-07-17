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
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * The persistent class for the MON_V_LIS_VERS_FALLITI database table.
 *
 */
@Entity
@Table(name = "MON_V_LIS_VERS_FALLITI")
public class MonVLisVersFalliti implements Serializable {
    private static final long serialVersionUID = 1L;
    private String blXml;
    private String cdErr;
    private String cdKeyObject;
    private String dlErr;
    private String dsObject;
    private Date dtApertura;
    private Date dtStatoCor;
    private String flNonRisolub;
    private String flVerif;
    private BigDecimal idAmbienteVers;
    private BigDecimal idSessioneIngest;
    private BigDecimal idVers;
    private String nmAmbienteVers;
    private String nmTipoObject;
    private String nmVers;
    private String tiDtCreazione;
    private String tiStato;
    private String tiStatoRisoluz;

    public MonVLisVersFalliti() {
    }

    @Lob
    @Column(name = "BL_XML")
    public String getBlXml() {
	return this.blXml;
    }

    public void setBlXml(String blXml) {
	this.blXml = blXml;
    }

    @Column(name = "CD_ERR")
    public String getCdErr() {
	return this.cdErr;
    }

    public void setCdErr(String cdErr) {
	this.cdErr = cdErr;
    }

    @Column(name = "CD_KEY_OBJECT")
    public String getCdKeyObject() {
	return this.cdKeyObject;
    }

    public void setCdKeyObject(String cdKeyObject) {
	this.cdKeyObject = cdKeyObject;
    }

    @Column(name = "DL_ERR")
    public String getDlErr() {
	return this.dlErr;
    }

    public void setDlErr(String dlErr) {
	this.dlErr = dlErr;
    }

    @Column(name = "DS_OBJECT")
    public String getDsObject() {
	return this.dsObject;
    }

    public void setDsObject(String dsObject) {
	this.dsObject = dsObject;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_APERTURA")
    public Date getDtApertura() {
	return this.dtApertura;
    }

    public void setDtApertura(Date dtApertura) {
	this.dtApertura = dtApertura;
    }

    @Column(name = "DT_STATO_COR")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getDtStatoCor() {
	return this.dtStatoCor;
    }

    public void setDtStatoCor(Date dtStatoCor) {
	this.dtStatoCor = dtStatoCor;
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

    @Column(name = "ID_AMBIENTE_VERS")
    public BigDecimal getIdAmbienteVers() {
	return this.idAmbienteVers;
    }

    public void setIdAmbienteVers(BigDecimal idAmbienteVers) {
	this.idAmbienteVers = idAmbienteVers;
    }

    @Id
    @Column(name = "ID_SESSIONE_INGEST")
    public BigDecimal getIdSessioneIngest() {
	return this.idSessioneIngest;
    }

    public void setIdSessioneIngest(BigDecimal idSessioneIngest) {
	this.idSessioneIngest = idSessioneIngest;
    }

    @Column(name = "ID_VERS")
    public BigDecimal getIdVers() {
	return this.idVers;
    }

    public void setIdVers(BigDecimal idVers) {
	this.idVers = idVers;
    }

    @Column(name = "NM_AMBIENTE_VERS")
    public String getNmAmbienteVers() {
	return this.nmAmbienteVers;
    }

    public void setNmAmbienteVers(String nmAmbienteVers) {
	this.nmAmbienteVers = nmAmbienteVers;
    }

    @Column(name = "NM_TIPO_OBJECT")
    public String getNmTipoObject() {
	return this.nmTipoObject;
    }

    public void setNmTipoObject(String nmTipoObject) {
	this.nmTipoObject = nmTipoObject;
    }

    @Column(name = "NM_VERS")
    public String getNmVers() {
	return this.nmVers;
    }

    public void setNmVers(String nmVers) {
	this.nmVers = nmVers;
    }

    @Column(name = "TI_DT_CREAZIONE")
    public String getTiDtCreazione() {
	return this.tiDtCreazione;
    }

    public void setTiDtCreazione(String tiDtCreazione) {
	this.tiDtCreazione = tiDtCreazione;
    }

    @Column(name = "TI_STATO")
    public String getTiStato() {
	return this.tiStato;
    }

    public void setTiStato(String tiStato) {
	this.tiStato = tiStato;
    }

    @Column(name = "TI_STATO_RISOLUZ")
    public String getTiStatoRisoluz() {
	return this.tiStatoRisoluz;
    }

    public void setTiStatoRisoluz(String tiStatoRisoluz) {
	this.tiStatoRisoluz = tiStatoRisoluz;
    }

}
