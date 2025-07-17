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
 * The persistent class for the MON_V_VIS_UNITA_DOC_SESSIONE database table.
 *
 */
@Entity
@Table(name = "MON_V_VIS_UNITA_DOC_SESSIONE")
public class MonVVisUnitaDocSessione implements Serializable {

    private static final long serialVersionUID = 1L;
    private BigDecimal aaUnitaDocSacer;
    private String blXmlIndiceSacer;
    private String blXmlVersSacer;
    private String cdErrSacer;
    private String cdKeyObject;
    private String cdKeyUnitaDocSacer;
    private String cdRegistroUnitaDocSacer;
    private String dlCompositoOrganiz;
    private String dlErrSacer;
    private String dsObject;
    private Date dtApertura;
    private Date dtChiusura;
    private String flVersSimulato;
    private BigDecimal idAmbienteVers;
    private BigDecimal idOrganizIam;
    private BigDecimal idSessioneIngest;
    private BigDecimal idUnitaDocSessione;
    private BigDecimal idVers;
    private BigDecimal niSizeFileByte;
    private String nmAmbienteVers;
    private String nmVers;
    private String tiStatoUnitaDocObject;
    // MEV 27407
    private Date dtStato;

    public MonVVisUnitaDocSessione() {
    }

    @Column(name = "AA_UNITA_DOC_SACER")
    public BigDecimal getAaUnitaDocSacer() {
	return this.aaUnitaDocSacer;
    }

    public void setAaUnitaDocSacer(BigDecimal aaUnitaDocSacer) {
	this.aaUnitaDocSacer = aaUnitaDocSacer;
    }

    @Lob
    @Column(name = "BL_XML_INDICE_SACER")
    public String getBlXmlIndiceSacer() {
	return this.blXmlIndiceSacer;
    }

    public void setBlXmlIndiceSacer(String blXmlIndiceSacer) {
	this.blXmlIndiceSacer = blXmlIndiceSacer;
    }

    @Lob
    @Column(name = "BL_XML_VERS_SACER")
    public String getBlXmlVersSacer() {
	return this.blXmlVersSacer;
    }

    public void setBlXmlVersSacer(String blXmlVersSacer) {
	this.blXmlVersSacer = blXmlVersSacer;
    }

    @Column(name = "CD_ERR_SACER")
    public String getCdErrSacer() {
	return this.cdErrSacer;
    }

    public void setCdErrSacer(String cdErrSacer) {
	this.cdErrSacer = cdErrSacer;
    }

    @Column(name = "CD_KEY_OBJECT")
    public String getCdKeyObject() {
	return this.cdKeyObject;
    }

    public void setCdKeyObject(String cdKeyObject) {
	this.cdKeyObject = cdKeyObject;
    }

    @Column(name = "CD_KEY_UNITA_DOC_SACER")
    public String getCdKeyUnitaDocSacer() {
	return this.cdKeyUnitaDocSacer;
    }

    public void setCdKeyUnitaDocSacer(String cdKeyUnitaDocSacer) {
	this.cdKeyUnitaDocSacer = cdKeyUnitaDocSacer;
    }

    @Column(name = "CD_REGISTRO_UNITA_DOC_SACER")
    public String getCdRegistroUnitaDocSacer() {
	return this.cdRegistroUnitaDocSacer;
    }

    public void setCdRegistroUnitaDocSacer(String cdRegistroUnitaDocSacer) {
	this.cdRegistroUnitaDocSacer = cdRegistroUnitaDocSacer;
    }

    @Column(name = "DL_COMPOSITO_ORGANIZ")
    public String getDlCompositoOrganiz() {
	return this.dlCompositoOrganiz;
    }

    public void setDlCompositoOrganiz(String dlCompositoOrganiz) {
	this.dlCompositoOrganiz = dlCompositoOrganiz;
    }

    @Column(name = "DL_ERR_SACER")
    public String getDlErrSacer() {
	return this.dlErrSacer;
    }

    public void setDlErrSacer(String dlErrSacer) {
	this.dlErrSacer = dlErrSacer;
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

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_CHIUSURA")
    public Date getDtChiusura() {
	return this.dtChiusura;
    }

    public void setDtChiusura(Date dtChiusura) {
	this.dtChiusura = dtChiusura;
    }

    @Column(name = "FL_VERS_SIMULATO", columnDefinition = "char")
    public String getFlVersSimulato() {
	return this.flVersSimulato;
    }

    public void setFlVersSimulato(String flVersSimulato) {
	this.flVersSimulato = flVersSimulato;
    }

    @Column(name = "ID_AMBIENTE_VERS")
    public BigDecimal getIdAmbienteVers() {
	return this.idAmbienteVers;
    }

    public void setIdAmbienteVers(BigDecimal idAmbienteVers) {
	this.idAmbienteVers = idAmbienteVers;
    }

    @Column(name = "ID_ORGANIZ_IAM")
    public BigDecimal getIdOrganizIam() {
	return this.idOrganizIam;
    }

    public void setIdOrganizIam(BigDecimal idOrganizIam) {
	this.idOrganizIam = idOrganizIam;
    }

    @Column(name = "ID_SESSIONE_INGEST")
    public BigDecimal getIdSessioneIngest() {
	return this.idSessioneIngest;
    }

    public void setIdSessioneIngest(BigDecimal idSessioneIngest) {
	this.idSessioneIngest = idSessioneIngest;
    }

    @Id
    @Column(name = "ID_UNITA_DOC_SESSIONE")
    public BigDecimal getIdUnitaDocSessione() {
	return this.idUnitaDocSessione;
    }

    public void setIdUnitaDocSessione(BigDecimal idUnitaDocSessione) {
	this.idUnitaDocSessione = idUnitaDocSessione;
    }

    @Column(name = "ID_VERS")
    public BigDecimal getIdVers() {
	return this.idVers;
    }

    public void setIdVers(BigDecimal idVers) {
	this.idVers = idVers;
    }

    @Column(name = "NI_SIZE_FILE_BYTE")
    public BigDecimal getNiSizeFileByte() {
	return this.niSizeFileByte;
    }

    public void setNiSizeFileByte(BigDecimal niSizeFileByte) {
	this.niSizeFileByte = niSizeFileByte;
    }

    @Column(name = "NM_AMBIENTE_VERS")
    public String getNmAmbienteVers() {
	return this.nmAmbienteVers;
    }

    public void setNmAmbienteVers(String nmAmbienteVers) {
	this.nmAmbienteVers = nmAmbienteVers;
    }

    @Column(name = "NM_VERS")
    public String getNmVers() {
	return this.nmVers;
    }

    public void setNmVers(String nmVers) {
	this.nmVers = nmVers;
    }

    @Column(name = "TI_STATO_UNITA_DOC_OBJECT")
    public String getTiStatoUnitaDocObject() {
	return this.tiStatoUnitaDocObject;
    }

    public void setTiStatoUnitaDocObject(String tiStatoUnitaDocObject) {
	this.tiStatoUnitaDocObject = tiStatoUnitaDocObject;
    }

    // MEV 27407
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_STATO")
    public Date getDtStato() {
	return dtStato;
    }

    public void setDtStato(Date dtStato) {
	this.dtStato = dtStato;
    }
}
