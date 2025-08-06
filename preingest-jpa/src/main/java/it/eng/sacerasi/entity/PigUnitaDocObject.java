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

package it.eng.sacerasi.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.id.enhanced.SequenceStyleGenerator;

/**
 * The persistent class for the PIG_UNITA_DOC_OBJECT database table.
 *
 */
@Entity
@Table(name = "PIG_UNITA_DOC_OBJECT")
public class PigUnitaDocObject implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long idUnitaDocObject;
    private BigDecimal aaUnitaDocSacer;
    private String cdErrSacer;
    private String cdKeyUnitaDocSacer;
    private String cdRegistroUnitaDocSacer;
    private String cdVerWsSacer;
    private String dlErrSacer;
    private String flVersSimulato;
    private BigDecimal idOrganizIam;
    private BigDecimal niSizeFileByte;
    private String tiStatoUnitaDocObject;
    private PigObject pigObject;
    private List<PigXmlSacerUnitaDoc> pigXmlSacerUnitaDocs = new ArrayList<>();
    private Long idVers;
    // MEV 27407
    private Date dtStato;

    public PigUnitaDocObject() {
	// for Hibernate
    }

    @Id
    @GenericGenerator(name = "PIG_UNITA_DOC_OBJECT_IDUNITADOCOBJECT_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
	    @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SPIG_UNITA_DOC_OBJECT"),
	    @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PIG_UNITA_DOC_OBJECT_IDUNITADOCOBJECT_GENERATOR")
    @Column(name = "ID_UNITA_DOC_OBJECT")
    public Long getIdUnitaDocObject() {
	return this.idUnitaDocObject;
    }

    public void setIdUnitaDocObject(Long idUnitaDocObject) {
	this.idUnitaDocObject = idUnitaDocObject;
    }

    @Column(name = "AA_UNITA_DOC_SACER")
    public BigDecimal getAaUnitaDocSacer() {
	return this.aaUnitaDocSacer;
    }

    public void setAaUnitaDocSacer(BigDecimal aaUnitaDocSacer) {
	this.aaUnitaDocSacer = aaUnitaDocSacer;
    }

    @Column(name = "CD_ERR_SACER")
    public String getCdErrSacer() {
	return this.cdErrSacer;
    }

    public void setCdErrSacer(String cdErrSacer) {
	this.cdErrSacer = cdErrSacer;
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

    @Column(name = "CD_VER_WS_SACER")
    public String getCdVerWsSacer() {
	return cdVerWsSacer;
    }

    public void setCdVerWsSacer(String cdVerWsSacer) {
	this.cdVerWsSacer = cdVerWsSacer;
    }

    @Column(name = "DL_ERR_SACER")
    public String getDlErrSacer() {
	return this.dlErrSacer;
    }

    public void setDlErrSacer(String dlErrSacer) {
	this.dlErrSacer = dlErrSacer;
    }

    @Column(name = "FL_VERS_SIMULATO", columnDefinition = "char")
    public String getFlVersSimulato() {
	return this.flVersSimulato;
    }

    public void setFlVersSimulato(String flVersSimulato) {
	this.flVersSimulato = flVersSimulato;
    }

    @Column(name = "ID_ORGANIZ_IAM")
    public BigDecimal getIdOrganizIam() {
	return this.idOrganizIam;
    }

    public void setIdOrganizIam(BigDecimal idOrganizIam) {
	this.idOrganizIam = idOrganizIam;
    }

    @Column(name = "NI_SIZE_FILE_BYTE")
    public BigDecimal getNiSizeFileByte() {
	return this.niSizeFileByte;
    }

    public void setNiSizeFileByte(BigDecimal niSizeFileByte) {
	this.niSizeFileByte = niSizeFileByte;
    }

    @Column(name = "TI_STATO_UNITA_DOC_OBJECT")
    public String getTiStatoUnitaDocObject() {
	return this.tiStatoUnitaDocObject;
    }

    public void setTiStatoUnitaDocObject(String tiStatoUnitaDocObject) {
	this.tiStatoUnitaDocObject = tiStatoUnitaDocObject;
    }

    // bi-directional many-to-one association to PigObject
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_OBJECT")
    public PigObject getPigObject() {
	return this.pigObject;
    }

    public void setPigObject(PigObject pigObject) {
	this.pigObject = pigObject;
    }

    // bi-directional many-to-one association to PigXmlSacerUnitaDoc
    @OneToMany(mappedBy = "pigUnitaDocObject", cascade = {
	    CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH })
    public List<PigXmlSacerUnitaDoc> getPigXmlSacerUnitaDocs() {
	return this.pigXmlSacerUnitaDocs;
    }

    public void setPigXmlSacerUnitaDocs(List<PigXmlSacerUnitaDoc> pigXmlSacerUnitaDocs) {
	this.pigXmlSacerUnitaDocs = pigXmlSacerUnitaDocs;
    }

    // usata solo come chiave di partizionamento, non voglio la join con PigVers
    @Column(name = "ID_VERS")
    public Long getIdVers() {
	return this.idVers;
    }

    public void setIdVers(Long idVers) {
	this.idVers = idVers;
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
