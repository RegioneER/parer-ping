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
import java.util.ArrayList;
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

import org.eclipse.persistence.oxm.annotations.XmlInverseReference;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.id.enhanced.SequenceStyleGenerator;

/**
 * The persistent class for the PIG_TIPO_OBJECT database table.
 *
 */
@Entity

@Table(name = "PIG_TIPO_OBJECT")
public class PigTipoObject implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long idTipoObject;
    private String cdRegistroUnitaDocSacer;
    private String dsRegExpCdVers;
    private String dsTipoObject;
    private String flContrHash;
    private String flForzaAccettazioneSacer;
    private String flForzaCollegamento;
    private String flForzaConservazione;
    private String nmTipoObject;
    private String nmTipoUnitaDocSacer;
    private String tiCalcKeyUnitaDoc;
    private String tiConservazione;
    private String tiVersFile;
    private List<PigContUnitaDocSacer> pigContUnitaDocSacers = new ArrayList<>();
    private List<PigObject> pigObjects = new ArrayList<>();
    private List<PigObjectTrasf> pigObjectTrasfs = new ArrayList<>();
    private List<PigTipoFileObject> pigTipoFileObjects = new ArrayList<>();
    private PigVers pigVer;
    private XfoTrasf xfoTrasf;
    private String tiPriorita;
    private String tiPrioritaVersamento;
    private String flNoVisibVersOgg;// FL_NO_VISIB_VERS_OGG
    private List<PigXsdDatiSpec> pigXsdDatiSpecs = new ArrayList<>();
    private List<PigDichVersSacerTipoObj> pigDichVersSacerTipoObjs = new ArrayList<>();
    private List<PigVersTipoObjectDaTrasf> pigVersTipoObjectDaTrasfs1 = new ArrayList<>();
    private List<PigVersTipoObjectDaTrasf> pigVersTipoObjectDaTrasfs2 = new ArrayList<>();

    public PigTipoObject() {
	// for Hibernate
    }

    @Id
    @GenericGenerator(name = "PIG_TIPO_OBJECT_IDTIPOOBJECT_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
	    @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SPIG_TIPO_OBJECT"),
	    @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PIG_TIPO_OBJECT_IDTIPOOBJECT_GENERATOR")
    @Column(name = "ID_TIPO_OBJECT")
    // @XmlID
    public Long getIdTipoObject() {
	return this.idTipoObject;
    }

    public void setIdTipoObject(Long idTipoObject) {
	this.idTipoObject = idTipoObject;
    }

    @Column(name = "CD_REGISTRO_UNITA_DOC_SACER")
    public String getCdRegistroUnitaDocSacer() {
	return this.cdRegistroUnitaDocSacer;
    }

    public void setCdRegistroUnitaDocSacer(String cdRegistroUnitaDocSacer) {
	this.cdRegistroUnitaDocSacer = cdRegistroUnitaDocSacer;
    }

    @Column(name = "DS_REG_EXP_CD_VERS")
    public String getDsRegExpCdVers() {
	return this.dsRegExpCdVers;
    }

    public void setDsRegExpCdVers(String dsRegExpCdVers) {
	this.dsRegExpCdVers = dsRegExpCdVers;
    }

    @Column(name = "DS_TIPO_OBJECT")
    public String getDsTipoObject() {
	return this.dsTipoObject;
    }

    public void setDsTipoObject(String dsTipoObject) {
	this.dsTipoObject = dsTipoObject;
    }

    @Column(name = "FL_CONTR_HASH", columnDefinition = "char")
    public String getFlContrHash() {
	return this.flContrHash;
    }

    public void setFlContrHash(String flContrHash) {
	this.flContrHash = flContrHash;
    }

    @Column(name = "FL_FORZA_ACCETTAZIONE_SACER", columnDefinition = "char")
    public String getFlForzaAccettazioneSacer() {
	return this.flForzaAccettazioneSacer;
    }

    public void setFlForzaAccettazioneSacer(String flForzaAccettazioneSacer) {
	this.flForzaAccettazioneSacer = flForzaAccettazioneSacer;
    }

    @Column(name = "FL_FORZA_COLLEGAMENTO", columnDefinition = "char")
    public String getFlForzaCollegamento() {
	return this.flForzaCollegamento;
    }

    public void setFlForzaCollegamento(String flForzaCollegamento) {
	this.flForzaCollegamento = flForzaCollegamento;
    }

    @Column(name = "FL_FORZA_CONSERVAZIONE", columnDefinition = "char")
    public String getFlForzaConservazione() {
	return this.flForzaConservazione;
    }

    public void setFlForzaConservazione(String flForzaConservazione) {
	this.flForzaConservazione = flForzaConservazione;
    }

    @Column(name = "NM_TIPO_OBJECT")
    public String getNmTipoObject() {
	return this.nmTipoObject;
    }

    public void setNmTipoObject(String nmTipoObject) {
	this.nmTipoObject = nmTipoObject;
    }

    @Column(name = "NM_TIPO_UNITA_DOC_SACER")
    public String getNmTipoUnitaDocSacer() {
	return this.nmTipoUnitaDocSacer;
    }

    public void setNmTipoUnitaDocSacer(String nmTipoUnitaDocSacer) {
	this.nmTipoUnitaDocSacer = nmTipoUnitaDocSacer;
    }

    @Column(name = "TI_CALC_KEY_UNITA_DOC")
    public String getTiCalcKeyUnitaDoc() {
	return this.tiCalcKeyUnitaDoc;
    }

    public void setTiCalcKeyUnitaDoc(String tiCalcKeyUnitaDoc) {
	this.tiCalcKeyUnitaDoc = tiCalcKeyUnitaDoc;
    }

    @Column(name = "TI_CONSERVAZIONE")
    public String getTiConservazione() {
	return this.tiConservazione;
    }

    public void setTiConservazione(String tiConservazione) {
	this.tiConservazione = tiConservazione;
    }

    @Column(name = "TI_VERS_FILE")
    public String getTiVersFile() {
	return this.tiVersFile;
    }

    public void setTiVersFile(String tiVersFile) {
	this.tiVersFile = tiVersFile;
    }

    @Column(name = "TI_PRIORITA")
    public String getTiPriorita() {
	return this.tiPriorita;
    }

    public void setTiPriorita(String tiPriorita) {
	this.tiPriorita = tiPriorita;
    }

    @Column(name = "TI_PRIORITA_VERSAMENTO")
    public String getTiPrioritaVersamento() {
	return this.tiPrioritaVersamento;
    }

    public void setTiPrioritaVersamento(String tiPrioritaVersamento) {
	this.tiPrioritaVersamento = tiPrioritaVersamento;
    }

    @Column(name = "FL_NO_VISIB_VERS_OGG", columnDefinition = "char")
    public String getFlNoVisibVersOgg() {
	return flNoVisibVersOgg;
    }

    public void setFlNoVisibVersOgg(String flNoVisibVersOgg) {
	this.flNoVisibVersOgg = flNoVisibVersOgg;
    }

    // bi-directional many-to-one association to PigContUnitaDocSacer
    @OneToMany(mappedBy = "pigTipoObject", cascade = {
	    CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH })
    public List<PigContUnitaDocSacer> getPigContUnitaDocSacers() {
	return this.pigContUnitaDocSacers;
    }

    public void setPigContUnitaDocSacers(List<PigContUnitaDocSacer> pigContUnitaDocSacers) {
	this.pigContUnitaDocSacers = pigContUnitaDocSacers;
    }

    public PigContUnitaDocSacer addPigContUnitaDocSacer(PigContUnitaDocSacer pigContUnitaDocSacer) {
	getPigContUnitaDocSacers().add(pigContUnitaDocSacer);
	pigContUnitaDocSacer.setPigTipoObject(this);

	return pigContUnitaDocSacer;
    }

    public PigContUnitaDocSacer removePigContUnitaDocSacer(
	    PigContUnitaDocSacer pigContUnitaDocSacer) {
	getPigContUnitaDocSacers().remove(pigContUnitaDocSacer);
	pigContUnitaDocSacer.setPigTipoObject(null);

	return pigContUnitaDocSacer;
    }

    // bi-directional many-to-one association to PigObject
    @OneToMany(mappedBy = "pigTipoObject")
    public List<PigObject> getPigObjects() {
	return this.pigObjects;
    }

    public void setPigObjects(List<PigObject> pigObjects) {
	this.pigObjects = pigObjects;
    }

    public PigObject addPigObject(PigObject pigObject) {
	getPigObjects().add(pigObject);
	pigObject.setPigTipoObject(this);

	return pigObject;
    }

    public PigObject removePigObject(PigObject pigObject) {
	getPigObjects().remove(pigObject);
	pigObject.setPigTipoObject(null);

	return pigObject;
    }

    // bi-directional many-to-one association to PigObjectTrasf
    @OneToMany(mappedBy = "pigTipoObject")
    public List<PigObjectTrasf> getPigObjectTrasfs() {
	return this.pigObjectTrasfs;
    }

    public void setPigObjectTrasfs(List<PigObjectTrasf> pigObjectTrasfs) {
	this.pigObjectTrasfs = pigObjectTrasfs;
    }

    public PigObjectTrasf addPigObjectTrasf(PigObjectTrasf pigObjectTrasf) {
	getPigObjectTrasfs().add(pigObjectTrasf);
	pigObjectTrasf.setPigTipoObject(this);

	return pigObjectTrasf;
    }

    public PigObjectTrasf removePigObjectTrasf(PigObjectTrasf pigObjectTrasf) {
	getPigObjectTrasfs().remove(pigObjectTrasf);
	pigObjectTrasf.setPigTipoObject(null);

	return pigObjectTrasf;
    }

    // bi-directional many-to-one association to PigTipoFileObject
    @OneToMany(mappedBy = "pigTipoObject", cascade = CascadeType.PERSIST)
    public List<PigTipoFileObject> getPigTipoFileObjects() {
	return this.pigTipoFileObjects;
    }

    public void setPigTipoFileObjects(List<PigTipoFileObject> pigTipoFileObjects) {
	this.pigTipoFileObjects = pigTipoFileObjects;
    }

    public PigTipoFileObject addPigTipoFileObject(PigTipoFileObject pigTipoFileObject) {
	getPigTipoFileObjects().add(pigTipoFileObject);
	pigTipoFileObject.setPigTipoObject(this);

	return pigTipoFileObject;
    }

    public PigTipoFileObject removePigTipoFileObject(PigTipoFileObject pigTipoFileObject) {
	getPigTipoFileObjects().remove(pigTipoFileObject);
	pigTipoFileObject.setPigTipoObject(null);

	return pigTipoFileObject;
    }

    // bi-directional many-to-one association to PigVers
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_VERS")
    @XmlInverseReference(mappedBy = "pigTipoObjects")
    public PigVers getPigVer() {
	return this.pigVer;
    }

    public void setPigVer(PigVers pigVer) {
	this.pigVer = pigVer;
    }

    // bi-directional many-to-one association to XfoTrasf
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_TRASF")
    public XfoTrasf getXfoTrasf() {
	return this.xfoTrasf;
    }

    public void setXfoTrasf(XfoTrasf xfoTrasf) {
	this.xfoTrasf = xfoTrasf;
    }

    // bi-directional many-to-one association to PigXsdDatiSpec
    @OneToMany(mappedBy = "pigTipoObject", cascade = CascadeType.PERSIST)
    public List<PigXsdDatiSpec> getPigXsdDatiSpecs() {
	return this.pigXsdDatiSpecs;
    }

    public void setPigXsdDatiSpecs(List<PigXsdDatiSpec> pigXsdDatiSpecs) {
	this.pigXsdDatiSpecs = pigXsdDatiSpecs;
    }

    public PigXsdDatiSpec addPigXsdDatiSpec(PigXsdDatiSpec pigXsdDatiSpec) {
	getPigXsdDatiSpecs().add(pigXsdDatiSpec);
	pigXsdDatiSpec.setPigTipoObject(this);

	return pigXsdDatiSpec;
    }

    public PigXsdDatiSpec removePigXsdDatiSpec(PigXsdDatiSpec pigXsdDatiSpec) {
	getPigXsdDatiSpecs().remove(pigXsdDatiSpec);
	pigXsdDatiSpec.setPigTipoObject(null);

	return pigXsdDatiSpec;
    }

    // bi-directional many-to-one association to PigDichVersSacerTipoObj
    @OneToMany(mappedBy = "pigTipoObject")
    public List<PigDichVersSacerTipoObj> getPigDichVersSacerTipoObjs() {
	return this.pigDichVersSacerTipoObjs;
    }

    public void setPigDichVersSacerTipoObjs(
	    List<PigDichVersSacerTipoObj> pigDichVersSacerTipoObjs) {
	this.pigDichVersSacerTipoObjs = pigDichVersSacerTipoObjs;
    }

    public PigDichVersSacerTipoObj addPigDichVersSacerTipoObj(
	    PigDichVersSacerTipoObj pigDichVersSacerTipoObj) {
	getPigDichVersSacerTipoObjs().add(pigDichVersSacerTipoObj);
	pigDichVersSacerTipoObj.setPigTipoObject(this);

	return pigDichVersSacerTipoObj;
    }

    public PigDichVersSacerTipoObj removePigDichVersSacerTipoObj(
	    PigDichVersSacerTipoObj pigDichVersSacerTipoObj) {
	getPigDichVersSacerTipoObjs().remove(pigDichVersSacerTipoObj);
	pigDichVersSacerTipoObj.setPigTipoObject(null);

	return pigDichVersSacerTipoObj;
    }

    // bi-directional many-to-one association to PigVersTipoObjectDaTrasf
    @OneToMany(mappedBy = "pigTipoObjectGen", cascade = CascadeType.REMOVE)
    public List<PigVersTipoObjectDaTrasf> getPigVersTipoObjectDaTrasfs1() {
	return this.pigVersTipoObjectDaTrasfs1;
    }

    public void setPigVersTipoObjectDaTrasfs1(
	    List<PigVersTipoObjectDaTrasf> pigVersTipoObjectDaTrasfs1) {
	this.pigVersTipoObjectDaTrasfs1 = pigVersTipoObjectDaTrasfs1;
    }

    public PigVersTipoObjectDaTrasf addPigVersTipoObjectDaTrasfs1(
	    PigVersTipoObjectDaTrasf pigVersTipoObjectDaTrasfs1) {
	getPigVersTipoObjectDaTrasfs1().add(pigVersTipoObjectDaTrasfs1);
	pigVersTipoObjectDaTrasfs1.setPigTipoObjectGen(this);

	return pigVersTipoObjectDaTrasfs1;
    }

    public PigVersTipoObjectDaTrasf removePigVersTipoObjectDaTrasfs1(
	    PigVersTipoObjectDaTrasf pigVersTipoObjectDaTrasfs1) {
	getPigVersTipoObjectDaTrasfs1().remove(pigVersTipoObjectDaTrasfs1);
	pigVersTipoObjectDaTrasfs1.setPigTipoObjectGen(null);

	return pigVersTipoObjectDaTrasfs1;
    }

    // bi-directional many-to-one association to PigVersTipoObjectDaTrasf
    @OneToMany(mappedBy = "pigTipoObjectDaTrasf")
    public List<PigVersTipoObjectDaTrasf> getPigVersTipoObjectDaTrasfs2() {
	return this.pigVersTipoObjectDaTrasfs2;
    }

    public void setPigVersTipoObjectDaTrasfs2(
	    List<PigVersTipoObjectDaTrasf> pigVersTipoObjectDaTrasfs2) {
	this.pigVersTipoObjectDaTrasfs2 = pigVersTipoObjectDaTrasfs2;
    }

    public PigVersTipoObjectDaTrasf addPigVersTipoObjectDaTrasfs2(
	    PigVersTipoObjectDaTrasf pigVersTipoObjectDaTrasfs2) {
	getPigVersTipoObjectDaTrasfs2().add(pigVersTipoObjectDaTrasfs2);
	pigVersTipoObjectDaTrasfs2.setPigTipoObjectDaTrasf(this);

	return pigVersTipoObjectDaTrasfs2;
    }

    public PigVersTipoObjectDaTrasf removePigVersTipoObjectDaTrasfs2(
	    PigVersTipoObjectDaTrasf pigVersTipoObjectDaTrasfs2) {
	getPigVersTipoObjectDaTrasfs2().remove(pigVersTipoObjectDaTrasfs2);
	pigVersTipoObjectDaTrasfs2.setPigTipoObjectDaTrasf(null);

	return pigVersTipoObjectDaTrasfs2;
    }

}
