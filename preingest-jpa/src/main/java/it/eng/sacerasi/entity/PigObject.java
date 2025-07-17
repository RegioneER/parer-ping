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
import java.time.LocalDateTime;
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
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.id.enhanced.SequenceStyleGenerator;

/**
 * The persistent class for the PIG_OBJECT database table.
 *
 */
@Entity
@Table(name = "PIG_OBJECT")
@NamedQuery(name = "PigObject.findByIdTipoOggetto", query = "SELECT p FROM PigObject p WHERE p.pigTipoObject.idTipoObject=:idTipoOggetto")
public class PigObject implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long idObject;
    private String cdKeyObject;
    private String cdTrasf;
    private String cdVersGen;
    private String cdVersioneTrasf;
    private String dsObject;
    private String flRichAnnulTimeout;
    private String flVersSacerDaRecup;
    private BigDecimal idLastSessioneIngest;
    private BigDecimal mmFirstSes;
    private BigDecimal niTotObjectTrasf;
    private BigDecimal niUnitaDocAttese;
    private BigDecimal pgOggettoTrasf;
    private String tiGestOggettiFigli;
    private String tiStatoObject;
    private String note;
    private List<PigFileObject> pigFileObjects = new ArrayList<>();
    private List<PigInfoDicom> pigInfoDicoms = new ArrayList<>();
    private IamUser iamUser;
    private PigObject pigObjectPadre;
    private List<PigObject> pigObjects = new ArrayList<>();
    private PigTipoObject pigTipoObject;
    private PigVers pigVer;
    private String tiPriorita;
    private String tiPrioritaVersamento;
    private List<PigObjectTrasf> pigObjectTrasfs = new ArrayList<>();
    private List<PigSessioneIngest> pigSessioneIngests = new ArrayList<>();
    private List<PigSessioneRecup> pigSessioneRecups = new ArrayList<>();
    private List<PigUnitaDocObject> pigUnitaDocObjects = new ArrayList<>();
    private List<PigXmlObject> pigXmlObjects = new ArrayList<>();
    private List<PigPrioritaObject> pigPrioritaObjects = new ArrayList<>();

    public PigObject() {
	// for Hibernate
    }

    @Id
    @GenericGenerator(name = "PIG_OBJECT_IDOBJECT_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
	    @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SPIG_OBJECT"),
	    @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PIG_OBJECT_IDOBJECT_GENERATOR")
    @Column(name = "ID_OBJECT")
    public Long getIdObject() {
	return this.idObject;
    }

    public void setIdObject(Long idObject) {
	this.idObject = idObject;
    }

    @Column(name = "CD_KEY_OBJECT")
    public String getCdKeyObject() {
	return this.cdKeyObject;
    }

    public void setCdKeyObject(String cdKeyObject) {
	this.cdKeyObject = cdKeyObject;
    }

    @Column(name = "CD_TRASF")
    public String getCdTrasf() {
	return this.cdTrasf;
    }

    public void setCdTrasf(String cdTrasf) {
	this.cdTrasf = cdTrasf;
    }

    @Column(name = "CD_VERS_GEN")
    public String getCdVersGen() {
	return this.cdVersGen;
    }

    public void setCdVersGen(String cdVersGen) {
	this.cdVersGen = cdVersGen;
    }

    @Column(name = "CD_VERSIONE_TRASF")
    public String getCdVersioneTrasf() {
	return this.cdVersioneTrasf;
    }

    public void setCdVersioneTrasf(String cdVersioneTrasf) {
	this.cdVersioneTrasf = cdVersioneTrasf;
    }

    @Column(name = "DS_OBJECT")
    public String getDsObject() {
	return this.dsObject;
    }

    public void setDsObject(String dsObject) {
	this.dsObject = dsObject;
    }

    @Column(name = "FL_RICH_ANNUL_TIMEOUT", columnDefinition = "char")
    public String getFlRichAnnulTimeout() {
	return this.flRichAnnulTimeout;
    }

    public void setFlRichAnnulTimeout(String flRichAnnulTimeout) {
	this.flRichAnnulTimeout = flRichAnnulTimeout;
    }

    @Column(name = "FL_VERS_SACER_DA_RECUP", columnDefinition = "char")
    public String getFlVersSacerDaRecup() {
	return this.flVersSacerDaRecup;
    }

    public void setFlVersSacerDaRecup(String flVersSacerDaRecup) {
	this.flVersSacerDaRecup = flVersSacerDaRecup;
    }

    @Column(name = "ID_LAST_SESSIONE_INGEST")
    public BigDecimal getIdLastSessioneIngest() {
	return this.idLastSessioneIngest;
    }

    public void setIdLastSessioneIngest(BigDecimal idLastSessioneIngest) {
	this.idLastSessioneIngest = idLastSessioneIngest;
    }

    @Column(name = "MM_FIRST_SES")
    public BigDecimal getMmFirstSes() {
	return this.mmFirstSes;
    }

    public void setMmFirstSes(BigDecimal mmFirstSes) {
	this.mmFirstSes = mmFirstSes;
    }

    @Column(name = "NI_TOT_OBJECT_TRASF")
    public BigDecimal getNiTotObjectTrasf() {
	return this.niTotObjectTrasf;
    }

    public void setNiTotObjectTrasf(BigDecimal niTotObjectTrasf) {
	this.niTotObjectTrasf = niTotObjectTrasf;
    }

    @Column(name = "NI_UNITA_DOC_ATTESE")
    public BigDecimal getNiUnitaDocAttese() {
	return this.niUnitaDocAttese;
    }

    public void setNiUnitaDocAttese(BigDecimal niUnitaDocAttese) {
	this.niUnitaDocAttese = niUnitaDocAttese;
    }

    @Column(name = "PG_OGGETTO_TRASF")
    public BigDecimal getPgOggettoTrasf() {
	return this.pgOggettoTrasf;
    }

    public void setPgOggettoTrasf(BigDecimal pgOggettoTrasf) {
	this.pgOggettoTrasf = pgOggettoTrasf;
    }

    @Column(name = "TI_GEST_OGGETTI_FIGLI")
    public String getTiGestOggettiFigli() {
	return tiGestOggettiFigli;
    }

    public void setTiGestOggettiFigli(String tiGestOggettiFigli) {
	this.tiGestOggettiFigli = tiGestOggettiFigli;
    }

    @Column(name = "TI_STATO_OBJECT")
    public String getTiStatoObject() {
	return this.tiStatoObject;
    }

    public void setTiStatoObject(String tiStatoObject) {
	this.tiStatoObject = tiStatoObject;
    }

    @Column(name = "NOTE")
    public String getNote() {
	return this.note;
    }

    public void setNote(String note) {
	this.note = note;
    }

    // bi-directional many-to-one association to PigFileObject
    @OneToMany(mappedBy = "pigObject")
    public List<PigFileObject> getPigFileObjects() {
	return this.pigFileObjects;
    }

    public void setPigFileObjects(List<PigFileObject> pigFileObjects) {
	this.pigFileObjects = pigFileObjects;
    }

    public PigFileObject addPigFileObject(PigFileObject pigFileObject) {
	getPigFileObjects().add(pigFileObject);
	pigFileObject.setPigObject(this);

	return pigFileObject;
    }

    public PigFileObject removePigFileObject(PigFileObject pigFileObject) {
	getPigFileObjects().remove(pigFileObject);
	pigFileObject.setPigObject(null);

	return pigFileObject;
    }

    // bi-directional many-to-one association to PigInfoDicom
    @OneToMany(mappedBy = "pigObject")
    public List<PigInfoDicom> getPigInfoDicoms() {
	return this.pigInfoDicoms;
    }

    public void setPigInfoDicoms(List<PigInfoDicom> pigInfoDicoms) {
	this.pigInfoDicoms = pigInfoDicoms;
    }

    // bi-directional many-to-one association to IamUser
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_USER_IAM")
    public IamUser getIamUser() {
	return this.iamUser;
    }

    public void setIamUser(IamUser iamUser) {
	this.iamUser = iamUser;
    }

    public PigInfoDicom addPigInfoDicom(PigInfoDicom pigInfoDicom) {
	getPigInfoDicoms().add(pigInfoDicom);
	pigInfoDicom.setPigObject(this);

	return pigInfoDicom;
    }

    public PigInfoDicom removePigInfoDicom(PigInfoDicom pigInfoDicom) {
	getPigInfoDicoms().remove(pigInfoDicom);
	pigInfoDicom.setPigObject(null);

	return pigInfoDicom;
    }

    // bi-directional many-to-one association to PigObject
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_OBJECT_PADRE")
    public PigObject getPigObjectPadre() {
	return this.pigObjectPadre;
    }

    public void setPigObjectPadre(PigObject pigObjectPadre) {
	this.pigObjectPadre = pigObjectPadre;
    }

    // bi-directional many-to-one association to PigObject
    @OneToMany(mappedBy = "pigObjectPadre")
    public List<PigObject> getPigObjects() {
	return this.pigObjects;
    }

    public void setPigObjects(List<PigObject> pigObjects) {
	this.pigObjects = pigObjects;
    }

    public PigObject addPigObject(PigObject pigObject) {
	getPigObjects().add(pigObject);
	pigObject.setPigObjectPadre(this);

	return pigObject;
    }

    public PigObject removePigObject(PigObject pigObject) {
	getPigObjects().remove(pigObject);
	pigObject.setPigObjectPadre(null);

	return pigObject;
    }

    // bi-directional many-to-one association to PigTipoObject
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_TIPO_OBJECT")
    public PigTipoObject getPigTipoObject() {
	return this.pigTipoObject;
    }

    public void setPigTipoObject(PigTipoObject pigTipoObject) {
	this.pigTipoObject = pigTipoObject;
    }

    // bi-directional many-to-one association to PigVer
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_VERS")
    public PigVers getPigVer() {
	return this.pigVer;
    }

    public void setPigVer(PigVers pigVer) {
	this.pigVer = pigVer;
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

    protected void setTiPrioritaVersamento(String tiPrioritaVersamento) {
	this.tiPrioritaVersamento = tiPrioritaVersamento;
    }

    public void impostaPrioritaVersamento(String tiPrioritaVersamento, String username) {
	this.tiPrioritaVersamento = tiPrioritaVersamento;
	if (tiPrioritaVersamento != null) {
	    PigPrioritaObject storico = new PigPrioritaObject();
	    storico.setDtModifica(LocalDateTime.now());
	    storico.setNmUser(username);
	    storico.setTiPrioritaVersamento(this.tiPrioritaVersamento);
	    this.addPigPrioritaObject(storico);
	}
    }

    // bi-directional many-to-one association to PigSessioneIngest
    @OneToMany(mappedBy = "pigObject")
    public List<PigSessioneIngest> getPigSessioneIngests() {
	return this.pigSessioneIngests;
    }

    public void setPigSessioneIngests(List<PigSessioneIngest> pigSessioneIngests) {
	this.pigSessioneIngests = pigSessioneIngests;
    }

    public PigSessioneIngest addPigSessioneIngest(PigSessioneIngest pigSessioneIngest) {
	getPigSessioneIngests().add(pigSessioneIngest);
	pigSessioneIngest.setPigObject(this);

	return pigSessioneIngest;
    }

    public PigSessioneIngest removePigSessioneIngest(PigSessioneIngest pigSessioneIngest) {
	getPigSessioneIngests().remove(pigSessioneIngest);
	pigSessioneIngest.setPigObject(null);

	return pigSessioneIngest;
    }

    // bi-directional many-to-one association to PigSessioneRecup
    @OneToMany(mappedBy = "pigObject")
    public List<PigSessioneRecup> getPigSessioneRecups() {
	return this.pigSessioneRecups;
    }

    public void setPigSessioneRecups(List<PigSessioneRecup> pigSessioneRecups) {
	this.pigSessioneRecups = pigSessioneRecups;
    }

    public PigSessioneRecup addPigSessioneRecup(PigSessioneRecup pigSessioneRecup) {
	getPigSessioneRecups().add(pigSessioneRecup);
	pigSessioneRecup.setPigObject(this);

	return pigSessioneRecup;
    }

    public PigSessioneRecup removePigSessioneRecup(PigSessioneRecup pigSessioneRecup) {
	getPigSessioneRecups().remove(pigSessioneRecup);
	pigSessioneRecup.setPigObject(null);

	return pigSessioneRecup;
    }

    // bi-directional many-to-one association to PigUnitaDocObject
    @OneToMany(mappedBy = "pigObject", cascade = {
	    CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH })
    public List<PigUnitaDocObject> getPigUnitaDocObjects() {
	return this.pigUnitaDocObjects;
    }

    public void setPigUnitaDocObjects(List<PigUnitaDocObject> pigUnitaDocObjects) {
	this.pigUnitaDocObjects = pigUnitaDocObjects;
    }

    public PigUnitaDocObject addPigUnitaDocObject(PigUnitaDocObject pigUnitaDocObject) {
	getPigUnitaDocObjects().add(pigUnitaDocObject);
	pigUnitaDocObject.setPigObject(this);

	return pigUnitaDocObject;
    }

    public PigUnitaDocObject removePigUnitaDocObject(PigUnitaDocObject pigUnitaDocObject) {
	getPigUnitaDocObjects().remove(pigUnitaDocObject);
	pigUnitaDocObject.setPigObject(null);

	return pigUnitaDocObject;
    }

    // bi-directional many-to-one association to PigXmlObject
    @OneToMany(mappedBy = "pigObject")
    public List<PigXmlObject> getPigXmlObjects() {
	return this.pigXmlObjects;
    }

    public void setPigXmlObjects(List<PigXmlObject> pigXmlObjects) {
	this.pigXmlObjects = pigXmlObjects;
    }

    public PigXmlObject addPigXmlObject(PigXmlObject pigXmlObject) {
	getPigXmlObjects().add(pigXmlObject);
	pigXmlObject.setPigObject(this);

	return pigXmlObject;
    }

    public PigXmlObject removePigXmlObject(PigXmlObject pigXmlObject) {
	getPigXmlObjects().remove(pigXmlObject);
	pigXmlObject.setPigObject(null);

	return pigXmlObject;
    }

    // bi-directional many-to-one association to PigObjectTrasf
    @OneToMany(mappedBy = "pigObject")
    public List<PigObjectTrasf> getPigObjectTrasfs() {
	return this.pigObjectTrasfs;
    }

    public void setPigObjectTrasfs(List<PigObjectTrasf> pigObjectTrasfs) {
	this.pigObjectTrasfs = pigObjectTrasfs;
    }

    public PigObjectTrasf addPigObjectTrasf(PigObjectTrasf pigObjectTrasf) {
	getPigObjectTrasfs().add(pigObjectTrasf);
	pigObjectTrasf.setPigObject(this);

	return pigObjectTrasf;
    }

    @OneToMany(mappedBy = "pigObject", cascade = {
	    CascadeType.PERSIST, CascadeType.REMOVE })
    public List<PigPrioritaObject> getPigPrioritaObjects() {
	return pigPrioritaObjects;
    }

    public void setPigPrioritaObjects(List<PigPrioritaObject> pigPrioritaObjects) {
	this.pigPrioritaObjects = pigPrioritaObjects;
    }

    private PigPrioritaObject addPigPrioritaObject(PigPrioritaObject pigPrioritaObject) {
	getPigPrioritaObjects().add(pigPrioritaObject);
	pigPrioritaObject.setPigObject(this);
	return pigPrioritaObject;
    }
}
