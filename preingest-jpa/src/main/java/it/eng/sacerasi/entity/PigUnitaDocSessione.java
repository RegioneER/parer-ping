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
 * The persistent class for the PIG_UNITA_DOC_SESSIONE database table.
 *
 */
@Entity
@Table(name = "PIG_UNITA_DOC_SESSIONE")
public class PigUnitaDocSessione implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long idUnitaDocSessione;
    private BigDecimal aaUnitaDocSacer;
    private String cdErrSacer;
    private String cdKeyUnitaDocSacer;
    private String cdRegistroUnitaDocSacer;
    private String cdVerWsSacer;
    private String dlErrSacer;
    private String flVersSimulato;
    private BigDecimal idOrganizIam;
    private BigDecimal niSizeFileByte;
    private String tiStatoUnitaDocSessione;
    private PigSessioneIngest pigSessioneIngest;
    private List<PigXmlSacerUnitaDocSes> pigXmlSacerUnitaDocSes = new ArrayList<>();
    private Long idVers;
    // MEV 27407
    private Date dtStato;

    public PigUnitaDocSessione() {
	// for Hibernate
    }

    @Id
    @GenericGenerator(name = "PIG_UNITA_DOC_SESSIONE_IDUNITADOCSESSIONE_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
	    @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SPIG_UNITA_DOC_SESSIONE"),
	    @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PIG_UNITA_DOC_SESSIONE_IDUNITADOCSESSIONE_GENERATOR")
    @Column(name = "ID_UNITA_DOC_SESSIONE")
    public Long getIdUnitaDocSessione() {
	return this.idUnitaDocSessione;
    }

    public void setIdUnitaDocSessione(Long idUnitaDocSessione) {
	this.idUnitaDocSessione = idUnitaDocSessione;
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

    @Column(name = "TI_STATO_UNITA_DOC_SESSIONE")
    public String getTiStatoUnitaDocSessione() {
	return this.tiStatoUnitaDocSessione;
    }

    public void setTiStatoUnitaDocSessione(String tiStatoUnitaDocSessione) {
	this.tiStatoUnitaDocSessione = tiStatoUnitaDocSessione;
    }

    // bi-directional many-to-one association to PigSessioneIngest
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_SESSIONE_INGEST")
    public PigSessioneIngest getPigSessioneIngest() {
	return this.pigSessioneIngest;
    }

    public void setPigSessioneIngest(PigSessioneIngest pigSessioneIngest) {
	this.pigSessioneIngest = pigSessioneIngest;
    }

    // bi-directional many-to-one association to PigXmlSacerUnitaDocSes
    @OneToMany(mappedBy = "pigUnitaDocSessione", cascade = {
	    CascadeType.PERSIST })
    public List<PigXmlSacerUnitaDocSes> getPigXmlSacerUnitaDocSes() {
	return this.pigXmlSacerUnitaDocSes;
    }

    public void setPigXmlSacerUnitaDocSes(List<PigXmlSacerUnitaDocSes> pigXmlSacerUnitaDocSes) {
	this.pigXmlSacerUnitaDocSes = pigXmlSacerUnitaDocSes;
    }

    public PigXmlSacerUnitaDocSes addPigXmlSacerUnitaDocS(
	    PigXmlSacerUnitaDocSes pigXmlSacerUnitaDocS) {
	getPigXmlSacerUnitaDocSes().add(pigXmlSacerUnitaDocS);
	pigXmlSacerUnitaDocS.setPigUnitaDocSessione(this);

	return pigXmlSacerUnitaDocS;
    }

    public PigXmlSacerUnitaDocSes removePigXmlSacerUnitaDocS(
	    PigXmlSacerUnitaDocSes pigXmlSacerUnitaDocS) {
	getPigXmlSacerUnitaDocSes().remove(pigXmlSacerUnitaDocS);
	pigXmlSacerUnitaDocS.setPigUnitaDocSessione(null);

	return pigXmlSacerUnitaDocS;
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
