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
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.id.enhanced.SequenceStyleGenerator;

/**
 * The persistent class for the PIG_INFO_DICOM database table.
 *
 */
@Entity
@Table(name = "PIG_INFO_DICOM")
public class PigInfoDicom implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long idInfoDicom;
    private String blDcmHashTxt;
    private String blGlobalHashTxt;
    private String cdAetNodoDicom;
    private String cdEncodingDcmHash;
    private String cdEncodingFileHash;
    private String cdEncodingGlobalHash;
    private String cdPatientId;
    private String cdPatientIdIssuer;
    private String cdVersioneDatiSpecDicom;
    private String dlListaModalityInStudy;
    private String dlListaSopClass;
    private String dlStudyDescription;
    private String dsAccessionNumber;
    private String dsDcmHash;
    private String dsFileHash;
    private String dsGlobalHash;
    private String dsInstitutionName;
    private String dsPatientName;
    private String dsRefPhysicianName;
    private String dsStudyId;
    private String dsStudyInstanceUid;
    private Date dtPatientBirthDate;
    private Date dtPresaInCarico;
    private Date dtStudyDate;
    private BigDecimal idVers;
    private BigDecimal niStudyRelatedImages;
    private BigDecimal niStudyRelatedSeries;
    private String tiAlgoDcmHash;
    private String tiAlgoFileHash;
    private String tiAlgoGlobalHash;
    private String tiPatientSex;
    private PigObject pigObject;
    private PigXsdDatiSpec pigXsdDatiSpec;

    public PigInfoDicom() {
	// for Hibernate
    }

    @Id
    @GenericGenerator(name = "PIG_INFO_DICOM_IDINFODICOM_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
	    @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SPIG_INFO_DICOM"),
	    @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PIG_INFO_DICOM_IDINFODICOM_GENERATOR")
    @Column(name = "ID_INFO_DICOM")
    public Long getIdInfoDicom() {
	return this.idInfoDicom;
    }

    public void setIdInfoDicom(Long idInfoDicom) {
	this.idInfoDicom = idInfoDicom;
    }

    @Lob()
    @Column(name = "BL_DCM_HASH_TXT")
    public String getBlDcmHashTxt() {
	return this.blDcmHashTxt;
    }

    public void setBlDcmHashTxt(String blDcmHashTxt) {
	this.blDcmHashTxt = blDcmHashTxt;
    }

    @Lob()
    @Column(name = "BL_GLOBAL_HASH_TXT")
    public String getBlGlobalHashTxt() {
	return this.blGlobalHashTxt;
    }

    public void setBlGlobalHashTxt(String blGlobalHashTxt) {
	this.blGlobalHashTxt = blGlobalHashTxt;
    }

    @Column(name = "CD_AET_NODO_DICOM")
    public String getCdAetNodoDicom() {
	return this.cdAetNodoDicom;
    }

    public void setCdAetNodoDicom(String cdAetNodoDicom) {
	this.cdAetNodoDicom = cdAetNodoDicom;
    }

    @Column(name = "CD_ENCODING_DCM_HASH")
    public String getCdEncodingDcmHash() {
	return this.cdEncodingDcmHash;
    }

    public void setCdEncodingDcmHash(String cdEncodingDcmHash) {
	this.cdEncodingDcmHash = cdEncodingDcmHash;
    }

    @Column(name = "CD_ENCODING_FILE_HASH")
    public String getCdEncodingFileHash() {
	return this.cdEncodingFileHash;
    }

    public void setCdEncodingFileHash(String cdEncodingFileHash) {
	this.cdEncodingFileHash = cdEncodingFileHash;
    }

    @Column(name = "CD_ENCODING_GLOBAL_HASH")
    public String getCdEncodingGlobalHash() {
	return this.cdEncodingGlobalHash;
    }

    public void setCdEncodingGlobalHash(String cdEncodingGlobalHash) {
	this.cdEncodingGlobalHash = cdEncodingGlobalHash;
    }

    @Column(name = "CD_PATIENT_ID")
    public String getCdPatientId() {
	return this.cdPatientId;
    }

    public void setCdPatientId(String cdPatientId) {
	this.cdPatientId = cdPatientId;
    }

    @Column(name = "CD_PATIENT_ID_ISSUER")
    public String getCdPatientIdIssuer() {
	return this.cdPatientIdIssuer;
    }

    public void setCdPatientIdIssuer(String cdPatientIdIssuer) {
	this.cdPatientIdIssuer = cdPatientIdIssuer;
    }

    @Column(name = "CD_VERSIONE_DATI_SPEC_DICOM")
    public String getCdVersioneDatiSpecDicom() {
	return this.cdVersioneDatiSpecDicom;
    }

    public void setCdVersioneDatiSpecDicom(String cdVersioneDatiSpecDicom) {
	this.cdVersioneDatiSpecDicom = cdVersioneDatiSpecDicom;
    }

    @Column(name = "DL_LISTA_MODALITY_IN_STUDY")
    public String getDlListaModalityInStudy() {
	return this.dlListaModalityInStudy;
    }

    public void setDlListaModalityInStudy(String dlListaModalityInStudy) {
	this.dlListaModalityInStudy = dlListaModalityInStudy;
    }

    @Column(name = "DL_LISTA_SOP_CLASS")
    public String getDlListaSopClass() {
	return this.dlListaSopClass;
    }

    public void setDlListaSopClass(String dlListaSopClass) {
	this.dlListaSopClass = dlListaSopClass;
    }

    @Column(name = "DL_STUDY_DESCRIPTION")
    public String getDlStudyDescription() {
	return this.dlStudyDescription;
    }

    public void setDlStudyDescription(String dlStudyDescription) {
	this.dlStudyDescription = dlStudyDescription;
    }

    @Column(name = "DS_ACCESSION_NUMBER")
    public String getDsAccessionNumber() {
	return this.dsAccessionNumber;
    }

    public void setDsAccessionNumber(String dsAccessionNumber) {
	this.dsAccessionNumber = dsAccessionNumber;
    }

    @Column(name = "DS_DCM_HASH")
    public String getDsDcmHash() {
	return this.dsDcmHash;
    }

    public void setDsDcmHash(String dsDcmHash) {
	this.dsDcmHash = dsDcmHash;
    }

    @Column(name = "DS_FILE_HASH")
    public String getDsFileHash() {
	return this.dsFileHash;
    }

    public void setDsFileHash(String dsFileHash) {
	this.dsFileHash = dsFileHash;
    }

    @Column(name = "DS_GLOBAL_HASH")
    public String getDsGlobalHash() {
	return this.dsGlobalHash;
    }

    public void setDsGlobalHash(String dsGlobalHash) {
	this.dsGlobalHash = dsGlobalHash;
    }

    @Column(name = "DS_INSTITUTION_NAME")
    public String getDsInstitutionName() {
	return this.dsInstitutionName;
    }

    public void setDsInstitutionName(String dsInstitutionName) {
	this.dsInstitutionName = dsInstitutionName;
    }

    @Column(name = "DS_PATIENT_NAME")
    public String getDsPatientName() {
	return this.dsPatientName;
    }

    public void setDsPatientName(String dsPatientName) {
	this.dsPatientName = dsPatientName;
    }

    @Column(name = "DS_REF_PHYSICIAN_NAME")
    public String getDsRefPhysicianName() {
	return this.dsRefPhysicianName;
    }

    public void setDsRefPhysicianName(String dsRefPhysicianName) {
	this.dsRefPhysicianName = dsRefPhysicianName;
    }

    @Column(name = "DS_STUDY_ID")
    public String getDsStudyId() {
	return this.dsStudyId;
    }

    public void setDsStudyId(String dsStudyId) {
	this.dsStudyId = dsStudyId;
    }

    @Column(name = "DS_STUDY_INSTANCE_UID")
    public String getDsStudyInstanceUid() {
	return this.dsStudyInstanceUid;
    }

    public void setDsStudyInstanceUid(String dsStudyInstanceUid) {
	this.dsStudyInstanceUid = dsStudyInstanceUid;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_PATIENT_BIRTH_DATE")
    public Date getDtPatientBirthDate() {
	return this.dtPatientBirthDate;
    }

    public void setDtPatientBirthDate(Date dtPatientBirthDate) {
	this.dtPatientBirthDate = dtPatientBirthDate;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_PRESA_IN_CARICO")
    public Date getDtPresaInCarico() {
	return this.dtPresaInCarico;
    }

    public void setDtPresaInCarico(Date dtPresaInCarico) {
	this.dtPresaInCarico = dtPresaInCarico;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_STUDY_DATE")
    public Date getDtStudyDate() {
	return this.dtStudyDate;
    }

    public void setDtStudyDate(Date dtStudyDate) {
	this.dtStudyDate = dtStudyDate;
    }

    @Column(name = "ID_VERS")
    public BigDecimal getIdVers() {
	return this.idVers;
    }

    public void setIdVers(BigDecimal idVers) {
	this.idVers = idVers;
    }

    @Column(name = "NI_STUDY_RELATED_IMAGES")
    public BigDecimal getNiStudyRelatedImages() {
	return this.niStudyRelatedImages;
    }

    public void setNiStudyRelatedImages(BigDecimal niStudyRelatedImages) {
	this.niStudyRelatedImages = niStudyRelatedImages;
    }

    @Column(name = "NI_STUDY_RELATED_SERIES")
    public BigDecimal getNiStudyRelatedSeries() {
	return this.niStudyRelatedSeries;
    }

    public void setNiStudyRelatedSeries(BigDecimal niStudyRelatedSeries) {
	this.niStudyRelatedSeries = niStudyRelatedSeries;
    }

    @Column(name = "TI_ALGO_DCM_HASH")
    public String getTiAlgoDcmHash() {
	return this.tiAlgoDcmHash;
    }

    public void setTiAlgoDcmHash(String tiAlgoDcmHash) {
	this.tiAlgoDcmHash = tiAlgoDcmHash;
    }

    @Column(name = "TI_ALGO_FILE_HASH")
    public String getTiAlgoFileHash() {
	return this.tiAlgoFileHash;
    }

    public void setTiAlgoFileHash(String tiAlgoFileHash) {
	this.tiAlgoFileHash = tiAlgoFileHash;
    }

    @Column(name = "TI_ALGO_GLOBAL_HASH")
    public String getTiAlgoGlobalHash() {
	return this.tiAlgoGlobalHash;
    }

    public void setTiAlgoGlobalHash(String tiAlgoGlobalHash) {
	this.tiAlgoGlobalHash = tiAlgoGlobalHash;
    }

    @Column(name = "TI_PATIENT_SEX", columnDefinition = "char")
    public String getTiPatientSex() {
	return this.tiPatientSex;
    }

    public void setTiPatientSex(String tiPatientSex) {
	this.tiPatientSex = tiPatientSex;
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

    // bi-directional many-to-one association to PigXsdDatiSpec
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_XSD_SPEC")
    public PigXsdDatiSpec getPigXsdDatiSpec() {
	return this.pigXsdDatiSpec;
    }

    public void setPigXsdDatiSpec(PigXsdDatiSpec pigXsdDatiSpec) {
	this.pigXsdDatiSpec = pigXsdDatiSpec;
    }

}
