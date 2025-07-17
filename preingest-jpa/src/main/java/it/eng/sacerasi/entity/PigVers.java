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
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.id.enhanced.SequenceStyleGenerator;

/**
 * The persistent class for the PIG_VERS database table.
 *
 */
@Entity
@XmlRootElement
// @Cacheable(true)
@Table(name = "PIG_VERS")
public class PigVers implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long idVers;
    private String dsPathInputFtp;
    private String dsPathOutputFtp;
    private String dsPathTrasf;
    private String dsVers;
    private Date dtFinValAppartAmbiente;
    private Date dtFineValAppartEnteSiam;
    private Date dtFineValVers;
    private Date dtIniValAppartAmbiente;
    private Date dtIniValAppartEnteSiam;
    private Date dtIniValVers;
    private String flArchivioRestituito;
    private String flCessato;
    private BigDecimal idEnteConvenz;
    private BigDecimal idEnteFornitEstern;
    private String nmVers;
    private List<PigObject> pigObjects = new ArrayList<>();
    private List<PigObjectTrasf> pigObjectTrasfs = new ArrayList<>();
    private List<PigPartitionVers> pigPartitionVers = new ArrayList<>();
    private List<PigSessioneIngest> pigSessioneIngests = new ArrayList<>();
    private List<PigSessioneRecup> pigSessioneRecups = new ArrayList<>();
    private List<PigSopClassDicomVers> pigSopClassDicomVers = new ArrayList<>();
    private List<PigTipoObject> pigTipoObjects = new ArrayList<>();
    private PigAmbienteVers pigAmbienteVer;
    private List<PigXsdDatiSpec> pigXsdDatiSpecs = new ArrayList<>();
    private List<PigDichVersSacer> pigDichVersSacers = new ArrayList<>();
    private List<PigVersTipoObjectDaTrasf> pigVersTipoObjectDaTrasfs = new ArrayList<>();

    public PigVers() {
	// hibernate
    }

    @Id
    @GenericGenerator(name = "PIG_VERS_IDVERS_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
	    @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SPIG_VERS"),
	    @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PIG_VERS_IDVERS_GENERATOR")
    @Column(name = "ID_VERS")
    public Long getIdVers() {
	return this.idVers;
    }

    public void setIdVers(Long idVers) {
	this.idVers = idVers;
    }

    @Column(name = "DS_PATH_INPUT_FTP")
    public String getDsPathInputFtp() {
	return this.dsPathInputFtp;
    }

    public void setDsPathInputFtp(String dsPathInputFtp) {
	this.dsPathInputFtp = dsPathInputFtp;
    }

    @Column(name = "DS_PATH_OUTPUT_FTP")
    public String getDsPathOutputFtp() {
	return this.dsPathOutputFtp;
    }

    public void setDsPathOutputFtp(String dsPathOutputFtp) {
	this.dsPathOutputFtp = dsPathOutputFtp;
    }

    @Column(name = "DS_PATH_TRASF")
    public String getDsPathTrasf() {
	return this.dsPathTrasf;
    }

    public void setDsPathTrasf(String dsPathTrasf) {
	this.dsPathTrasf = dsPathTrasf;
    }

    @Column(name = "DS_VERS")
    public String getDsVers() {
	return this.dsVers;
    }

    public void setDsVers(String dsVers) {
	this.dsVers = dsVers;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_FIN_VAL_APPART_AMBIENTE")
    public Date getDtFinValAppartAmbiente() {
	return this.dtFinValAppartAmbiente;
    }

    public void setDtFinValAppartAmbiente(Date dtFinValAppartAmbiente) {
	this.dtFinValAppartAmbiente = dtFinValAppartAmbiente;
    }

    @Temporal(TemporalType.DATE)
    @Column(name = "DT_FINE_VAL_APPART_ENTE_SIAM")
    public Date getDtFineValAppartEnteSiam() {
	return this.dtFineValAppartEnteSiam;
    }

    public void setDtFineValAppartEnteSiam(Date dtFineValAppartEnteSiam) {
	this.dtFineValAppartEnteSiam = dtFineValAppartEnteSiam;
    }

    @Temporal(TemporalType.DATE)
    @Column(name = "DT_FINE_VAL_VERS")
    public Date getDtFineValVers() {
	return this.dtFineValVers;
    }

    public void setDtFineValVers(Date dtFineValVers) {
	this.dtFineValVers = dtFineValVers;
    }

    @Temporal(TemporalType.DATE)
    @Column(name = "DT_INI_VAL_APPART_AMBIENTE")
    public Date getDtIniValAppartAmbiente() {
	return this.dtIniValAppartAmbiente;
    }

    public void setDtIniValAppartAmbiente(Date dtIniValAppartAmbiente) {
	this.dtIniValAppartAmbiente = dtIniValAppartAmbiente;
    }

    @Temporal(TemporalType.DATE)
    @Column(name = "DT_INI_VAL_APPART_ENTE_SIAM")
    public Date getDtIniValAppartEnteSiam() {
	return this.dtIniValAppartEnteSiam;
    }

    public void setDtIniValAppartEnteSiam(Date dtIniValAppartEnteSiam) {
	this.dtIniValAppartEnteSiam = dtIniValAppartEnteSiam;
    }

    @Temporal(TemporalType.DATE)
    @Column(name = "DT_INI_VAL_VERS")
    public Date getDtIniValVers() {
	return this.dtIniValVers;
    }

    public void setDtIniValVers(Date dtIniValVers) {
	this.dtIniValVers = dtIniValVers;
    }

    @Column(name = "FL_ARCHIVIO_RESTITUITO", columnDefinition = "char")
    public String getFlArchivioRestituito() {
	return this.flArchivioRestituito;
    }

    public void setFlArchivioRestituito(String flArchivioRestituito) {
	this.flArchivioRestituito = flArchivioRestituito;
    }

    @Column(name = "FL_CESSATO", columnDefinition = "char")
    public String getFlCessato() {
	return this.flCessato;
    }

    public void setFlCessato(String flCessato) {
	this.flCessato = flCessato;
    }

    @Column(name = "ID_ENTE_CONVENZ")
    public BigDecimal getIdEnteConvenz() {
	return this.idEnteConvenz;
    }

    public void setIdEnteConvenz(BigDecimal idEnteConvenz) {
	this.idEnteConvenz = idEnteConvenz;
    }

    @Column(name = "ID_ENTE_FORNIT_ESTERN")
    public BigDecimal getIdEnteFornitEstern() {
	return this.idEnteFornitEstern;
    }

    public void setIdEnteFornitEstern(BigDecimal idEnteFornitEstern) {
	this.idEnteFornitEstern = idEnteFornitEstern;
    }

    @Column(name = "NM_VERS")
    public String getNmVers() {
	return this.nmVers;
    }

    public void setNmVers(String nmVers) {
	this.nmVers = nmVers;
    }

    // bi-directional many-to-one association to PigObject
    @OneToMany(mappedBy = "pigVer")
    public List<PigObject> getPigObjects() {
	return this.pigObjects;
    }

    public void setPigObjects(List<PigObject> pigObjects) {
	this.pigObjects = pigObjects;
    }

    public PigObject addPigObject(PigObject pigObject) {
	getPigObjects().add(pigObject);
	pigObject.setPigVer(this);

	return pigObject;
    }

    public PigObject removePigObject(PigObject pigObject) {
	getPigObjects().remove(pigObject);
	pigObject.setPigVer(null);

	return pigObject;
    }

    // bi-directional many-to-one association to PigObjectTrasf
    @OneToMany(mappedBy = "pigVer")
    public List<PigObjectTrasf> getPigObjectTrasfs() {
	return this.pigObjectTrasfs;
    }

    public void setPigObjectTrasfs(List<PigObjectTrasf> pigObjectTrasfs) {
	this.pigObjectTrasfs = pigObjectTrasfs;
    }

    public PigObjectTrasf addPigObjectTrasf(PigObjectTrasf pigObjectTrasf) {
	getPigObjectTrasfs().add(pigObjectTrasf);
	pigObjectTrasf.setPigVer(this);

	return pigObjectTrasf;
    }

    public PigObjectTrasf removePigObjectTrasf(PigObjectTrasf pigObjectTrasf) {
	getPigObjectTrasfs().remove(pigObjectTrasf);
	pigObjectTrasf.setPigVer(null);

	return pigObjectTrasf;
    }

    // bi-directional many-to-one association to PigPartitionVer
    @OneToMany(mappedBy = "pigVers")
    public List<PigPartitionVers> getPigPartitionVers() {
	return this.pigPartitionVers;
    }

    public void setPigPartitionVers(List<PigPartitionVers> pigPartitionVers) {
	this.pigPartitionVers = pigPartitionVers;
    }

    public PigPartitionVers addPigPartitionVer(PigPartitionVers pigPartitionVer) {
	getPigPartitionVers().add(pigPartitionVer);
	pigPartitionVer.setPigVers(this);

	return pigPartitionVer;
    }

    public PigPartitionVers removePigPartitionVer(PigPartitionVers pigPartitionVer) {
	getPigPartitionVers().remove(pigPartitionVer);
	pigPartitionVer.setPigVers(null);

	return pigPartitionVer;
    }

    // bi-directional many-to-one association to PigSessioneIngest
    @OneToMany(mappedBy = "pigVer")
    public List<PigSessioneIngest> getPigSessioneIngests() {
	return this.pigSessioneIngests;
    }

    public void setPigSessioneIngests(List<PigSessioneIngest> pigSessioneIngests) {
	this.pigSessioneIngests = pigSessioneIngests;
    }

    public PigSessioneIngest addPigSessioneIngest(PigSessioneIngest pigSessioneIngest) {
	getPigSessioneIngests().add(pigSessioneIngest);
	pigSessioneIngest.setPigVer(this);

	return pigSessioneIngest;
    }

    public PigSessioneIngest removePigSessioneIngest(PigSessioneIngest pigSessioneIngest) {
	getPigSessioneIngests().remove(pigSessioneIngest);
	pigSessioneIngest.setPigVer(null);

	return pigSessioneIngest;
    }

    // bi-directional many-to-one association to PigSessioneRecup
    @OneToMany(mappedBy = "pigVer")
    public List<PigSessioneRecup> getPigSessioneRecups() {
	return this.pigSessioneRecups;
    }

    public void setPigSessioneRecups(List<PigSessioneRecup> pigSessioneRecups) {
	this.pigSessioneRecups = pigSessioneRecups;
    }

    public PigSessioneRecup addPigSessioneRecup(PigSessioneRecup pigSessioneRecup) {
	getPigSessioneRecups().add(pigSessioneRecup);
	pigSessioneRecup.setPigVer(this);

	return pigSessioneRecup;
    }

    public PigSessioneRecup removePigSessioneRecup(PigSessioneRecup pigSessioneRecup) {
	getPigSessioneRecups().remove(pigSessioneRecup);
	pigSessioneRecup.setPigVer(null);

	return pigSessioneRecup;
    }

    // bi-directional many-to-one association to PigSopClassDicomVer
    @OneToMany(mappedBy = "pigVer")
    public List<PigSopClassDicomVers> getPigSopClassDicomVers() {
	return this.pigSopClassDicomVers;
    }

    public void setPigSopClassDicomVers(List<PigSopClassDicomVers> pigSopClassDicomVers) {
	this.pigSopClassDicomVers = pigSopClassDicomVers;
    }

    public PigSopClassDicomVers addPigSopClassDicomVer(PigSopClassDicomVers pigSopClassDicomVer) {
	getPigSopClassDicomVers().add(pigSopClassDicomVer);
	pigSopClassDicomVer.setPigVer(this);

	return pigSopClassDicomVer;
    }

    public PigSopClassDicomVers removePigSopClassDicomVer(
	    PigSopClassDicomVers pigSopClassDicomVer) {
	getPigSopClassDicomVers().remove(pigSopClassDicomVer);
	pigSopClassDicomVer.setPigVer(null);

	return pigSopClassDicomVer;
    }

    // bi-directional many-to-one association to PigTipoObject
    @OneToMany(mappedBy = "pigVer", cascade = CascadeType.PERSIST)
    // @XmlIDREF
    public List<PigTipoObject> getPigTipoObjects() {
	return this.pigTipoObjects;
    }

    public void setPigTipoObjects(List<PigTipoObject> pigTipoObjects) {
	this.pigTipoObjects = pigTipoObjects;
    }

    public PigTipoObject addPigTipoObject(PigTipoObject pigTipoObject) {
	getPigTipoObjects().add(pigTipoObject);
	pigTipoObject.setPigVer(this);

	return pigTipoObject;
    }

    public PigTipoObject removePigTipoObject(PigTipoObject pigTipoObject) {
	getPigTipoObjects().remove(pigTipoObject);
	pigTipoObject.setPigVer(null);

	return pigTipoObject;
    }

    // bi-directional many-to-one association to PigAmbienteVer
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_AMBIENTE_VERS")
    public PigAmbienteVers getPigAmbienteVer() {
	return this.pigAmbienteVer;
    }

    public void setPigAmbienteVer(PigAmbienteVers pigAmbienteVer) {
	this.pigAmbienteVer = pigAmbienteVer;
    }

    // bi-directional many-to-one association to PigXsdDatiSpec
    @OneToMany(mappedBy = "pigVer", cascade = CascadeType.PERSIST)
    public List<PigXsdDatiSpec> getPigXsdDatiSpecs() {
	return this.pigXsdDatiSpecs;
    }

    public void setPigXsdDatiSpecs(List<PigXsdDatiSpec> pigXsdDatiSpecs) {
	this.pigXsdDatiSpecs = pigXsdDatiSpecs;
    }

    public PigXsdDatiSpec addPigXsdDatiSpec(PigXsdDatiSpec pigXsdDatiSpec) {
	getPigXsdDatiSpecs().add(pigXsdDatiSpec);
	pigXsdDatiSpec.setPigVer(this);

	return pigXsdDatiSpec;
    }

    public PigXsdDatiSpec removePigXsdDatiSpec(PigXsdDatiSpec pigXsdDatiSpec) {
	getPigXsdDatiSpecs().remove(pigXsdDatiSpec);
	pigXsdDatiSpec.setPigVer(null);

	return pigXsdDatiSpec;
    }

    // bi-directional many-to-one association to PigVersDaTrasfTrasf
    @OneToMany(mappedBy = "pigVer")
    public List<PigDichVersSacer> getPigDichVersSacers() {
	return this.pigDichVersSacers;
    }

    public void setPigDichVersSacers(List<PigDichVersSacer> pigDichVersSacers) {
	this.pigDichVersSacers = pigDichVersSacers;
    }

    public PigDichVersSacer addPigDichVersSacer(PigDichVersSacer pigDichVersSacer) {
	getPigDichVersSacers().add(pigDichVersSacer);
	pigDichVersSacer.setPigVer(this);

	return pigDichVersSacer;
    }

    public PigDichVersSacer removePigDichVersSacer(PigDichVersSacer pigDichVersSacer) {
	getPigDichVersSacers().remove(pigDichVersSacer);
	pigDichVersSacer.setPigVer(null);

	return pigDichVersSacer;
    }

    // bi-directional many-to-one association to PigVersTipoObjectDaTrasf
    @OneToMany(mappedBy = "pigVersGen", cascade = CascadeType.REMOVE)
    public List<PigVersTipoObjectDaTrasf> getPigVersTipoObjectDaTrasfs() {
	return this.pigVersTipoObjectDaTrasfs;
    }

    public void setPigVersTipoObjectDaTrasfs(
	    List<PigVersTipoObjectDaTrasf> pigVersTipoObjectDaTrasfs) {
	this.pigVersTipoObjectDaTrasfs = pigVersTipoObjectDaTrasfs;
    }

    public PigVersTipoObjectDaTrasf addPigVersTipoObjectDaTrasf(
	    PigVersTipoObjectDaTrasf pigVersTipoObjectDaTrasf) {
	getPigVersTipoObjectDaTrasfs().add(pigVersTipoObjectDaTrasf);
	pigVersTipoObjectDaTrasf.setPigVersGen(this);

	return pigVersTipoObjectDaTrasf;
    }

    public PigVersTipoObjectDaTrasf removePigVersTipoObjectDaTrasf(
	    PigVersTipoObjectDaTrasf pigVersTipoObjectDaTrasf) {
	getPigVersTipoObjectDaTrasfs().remove(pigVersTipoObjectDaTrasf);
	pigVersTipoObjectDaTrasf.setPigVersGen(null);

	return pigVersTipoObjectDaTrasf;
    }

}
