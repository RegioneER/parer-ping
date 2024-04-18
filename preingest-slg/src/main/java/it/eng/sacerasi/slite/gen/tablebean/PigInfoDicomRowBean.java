/*
 * Engineering Ingegneria Informatica S.p.A.
 *
 * Copyright (C) 2023 Regione Emilia-Romagna
 * <p/>
 * This program is free software: you can redistribute it and/or modify it under the terms of
 * the GNU Affero General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU Affero General Public License along with this program.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package it.eng.sacerasi.slite.gen.tablebean;

import java.math.BigDecimal;
import java.sql.Timestamp;

import it.eng.sacerasi.entity.PigInfoDicom;
import it.eng.sacerasi.entity.PigObject;
import it.eng.sacerasi.entity.PigXsdDatiSpec;
import it.eng.spagoLite.db.base.JEEBaseRowInterface;
import it.eng.spagoLite.db.base.row.BaseRow;
import it.eng.spagoLite.db.oracle.bean.column.TableDescriptor;

/**
 * RowBean per la tabella Pig_Info_Dicom
 *
 */
public class PigInfoDicomRowBean extends BaseRow implements JEEBaseRowInterface {

    /*
     * @Generated( value = "it.eg.dbtool.db.oracle.beangen.Oracle4JPAClientBeanGen$TableBeanWriter", comments =
     * "This class was generated by OraTool", date = "Monday, 17 March 2014 15:12" )
     */

    private static final long serialVersionUID = 1L;
    public static PigInfoDicomTableDescriptor TABLE_DESCRIPTOR = new PigInfoDicomTableDescriptor();

    public PigInfoDicomRowBean() {
        super();
    }

    public TableDescriptor getTableDescriptor() {
        return TABLE_DESCRIPTOR;
    }

    // getter e setter
    public BigDecimal getIdInfoDicom() {
        return getBigDecimal("id_info_dicom");
    }

    public void setIdInfoDicom(BigDecimal idInfoDicom) {
        setObject("id_info_dicom", idInfoDicom);
    }

    public BigDecimal getIdObject() {
        return getBigDecimal("id_object");
    }

    public void setIdObject(BigDecimal idObject) {
        setObject("id_object", idObject);
    }

    public BigDecimal getIdVers() {
        return getBigDecimal("id_vers");
    }

    public void setIdVers(BigDecimal idVers) {
        setObject("id_vers", idVers);
    }

    public String getCdVersioneDatiSpecDicom() {
        return getString("cd_versione_dati_spec_dicom");
    }

    public void setCdVersioneDatiSpecDicom(String cdVersioneDatiSpecDicom) {
        setObject("cd_versione_dati_spec_dicom", cdVersioneDatiSpecDicom);
    }

    public String getCdAetNodoDicom() {
        return getString("cd_aet_nodo_dicom");
    }

    public void setCdAetNodoDicom(String cdAetNodoDicom) {
        setObject("cd_aet_nodo_dicom", cdAetNodoDicom);
    }

    public String getDlListaSopClass() {
        return getString("dl_lista_sop_class");
    }

    public void setDlListaSopClass(String dlListaSopClass) {
        setObject("dl_lista_sop_class", dlListaSopClass);
    }

    public Timestamp getDtStudyDate() {
        return getTimestamp("dt_study_date");
    }

    public void setDtStudyDate(Timestamp dtStudyDate) {
        setObject("dt_study_date", dtStudyDate);
    }

    public String getDsAccessionNumber() {
        return getString("ds_accession_number");
    }

    public void setDsAccessionNumber(String dsAccessionNumber) {
        setObject("ds_accession_number", dsAccessionNumber);
    }

    public String getDlListaModalityInStudy() {
        return getString("dl_lista_modality_in_study");
    }

    public void setDlListaModalityInStudy(String dlListaModalityInStudy) {
        setObject("dl_lista_modality_in_study", dlListaModalityInStudy);
    }

    public String getDsInstitutionName() {
        return getString("ds_institution_name");
    }

    public void setDsInstitutionName(String dsInstitutionName) {
        setObject("ds_institution_name", dsInstitutionName);
    }

    public String getDsRefPhysicianName() {
        return getString("ds_ref_physician_name");
    }

    public void setDsRefPhysicianName(String dsRefPhysicianName) {
        setObject("ds_ref_physician_name", dsRefPhysicianName);
    }

    public String getDlStudyDescription() {
        return getString("dl_study_description");
    }

    public void setDlStudyDescription(String dlStudyDescription) {
        setObject("dl_study_description", dlStudyDescription);
    }

    public String getDsPatientName() {
        return getString("ds_patient_name");
    }

    public void setDsPatientName(String dsPatientName) {
        setObject("ds_patient_name", dsPatientName);
    }

    public String getCdPatientId() {
        return getString("cd_patient_id");
    }

    public void setCdPatientId(String cdPatientId) {
        setObject("cd_patient_id", cdPatientId);
    }

    public String getCdPatientIdIssuer() {
        return getString("cd_patient_id_issuer");
    }

    public void setCdPatientIdIssuer(String cdPatientIdIssuer) {
        setObject("cd_patient_id_issuer", cdPatientIdIssuer);
    }

    public Timestamp getDtPatientBirthDate() {
        return getTimestamp("dt_patient_birth_date");
    }

    public void setDtPatientBirthDate(Timestamp dtPatientBirthDate) {
        setObject("dt_patient_birth_date", dtPatientBirthDate);
    }

    public String getTiPatientSex() {
        return getString("ti_patient_sex");
    }

    public void setTiPatientSex(String tiPatientSex) {
        setObject("ti_patient_sex", tiPatientSex);
    }

    public String getDsStudyInstanceUid() {
        return getString("ds_study_instance_uid");
    }

    public void setDsStudyInstanceUid(String dsStudyInstanceUid) {
        setObject("ds_study_instance_uid", dsStudyInstanceUid);
    }

    public BigDecimal getNiStudyRelatedSeries() {
        return getBigDecimal("ni_study_related_series");
    }

    public void setNiStudyRelatedSeries(BigDecimal niStudyRelatedSeries) {
        setObject("ni_study_related_series", niStudyRelatedSeries);
    }

    public BigDecimal getNiStudyRelatedImages() {
        return getBigDecimal("ni_study_related_images");
    }

    public void setNiStudyRelatedImages(BigDecimal niStudyRelatedImages) {
        setObject("ni_study_related_images", niStudyRelatedImages);
    }

    public String getDsStudyId() {
        return getString("ds_study_id");
    }

    public void setDsStudyId(String dsStudyId) {
        setObject("ds_study_id", dsStudyId);
    }

    public Timestamp getDtPresaInCarico() {
        return getTimestamp("dt_presa_in_carico");
    }

    public void setDtPresaInCarico(Timestamp dtPresaInCarico) {
        setObject("dt_presa_in_carico", dtPresaInCarico);
    }

    public String getBlDcmHashTxt() {
        return getString("bl_dcm_hash_txt");
    }

    public void setBlDcmHashTxt(String blDcmHashTxt) {
        setObject("bl_dcm_hash_txt", blDcmHashTxt);
    }

    public String getDsDcmHash() {
        return getString("ds_dcm_hash");
    }

    public void setDsDcmHash(String dsDcmHash) {
        setObject("ds_dcm_hash", dsDcmHash);
    }

    public String getTiAlgoDcmHash() {
        return getString("ti_algo_dcm_hash");
    }

    public void setTiAlgoDcmHash(String tiAlgoDcmHash) {
        setObject("ti_algo_dcm_hash", tiAlgoDcmHash);
    }

    public String getCdEncodingDcmHash() {
        return getString("cd_encoding_dcm_hash");
    }

    public void setCdEncodingDcmHash(String cdEncodingDcmHash) {
        setObject("cd_encoding_dcm_hash", cdEncodingDcmHash);
    }

    public String getBlGlobalHashTxt() {
        return getString("bl_global_hash_txt");
    }

    public void setBlGlobalHashTxt(String blGlobalHashTxt) {
        setObject("bl_global_hash_txt", blGlobalHashTxt);
    }

    public String getDsGlobalHash() {
        return getString("ds_global_hash");
    }

    public void setDsGlobalHash(String dsGlobalHash) {
        setObject("ds_global_hash", dsGlobalHash);
    }

    public String getTiAlgoGlobalHash() {
        return getString("ti_algo_global_hash");
    }

    public void setTiAlgoGlobalHash(String tiAlgoGlobalHash) {
        setObject("ti_algo_global_hash", tiAlgoGlobalHash);
    }

    public String getCdEncodingGlobalHash() {
        return getString("cd_encoding_global_hash");
    }

    public void setCdEncodingGlobalHash(String cdEncodingGlobalHash) {
        setObject("cd_encoding_global_hash", cdEncodingGlobalHash);
    }

    public String getDsFileHash() {
        return getString("ds_file_hash");
    }

    public void setDsFileHash(String dsFileHash) {
        setObject("ds_file_hash", dsFileHash);
    }

    public String getTiAlgoFileHash() {
        return getString("ti_algo_file_hash");
    }

    public void setTiAlgoFileHash(String tiAlgoFileHash) {
        setObject("ti_algo_file_hash", tiAlgoFileHash);
    }

    public String getCdEncodingFileHash() {
        return getString("cd_encoding_file_hash");
    }

    public void setCdEncodingFileHash(String cdEncodingFileHash) {
        setObject("cd_encoding_file_hash", cdEncodingFileHash);
    }

    public BigDecimal getIdXsdSpec() {
        return getBigDecimal("id_xsd_spec");
    }

    public void setIdXsdSpec(BigDecimal idXsdSpec) {
        setObject("id_xsd_spec", idXsdSpec);
    }

    @Override
    public void entityToRowBean(Object obj) {
        PigInfoDicom entity = (PigInfoDicom) obj;

        this.setIdInfoDicom(new BigDecimal(entity.getIdInfoDicom()));
        if (entity.getPigObject() != null) {
            this.setIdObject(new BigDecimal(entity.getPigObject().getIdObject()));
        }

        this.setIdVers(entity.getIdVers());
        this.setCdVersioneDatiSpecDicom(entity.getCdVersioneDatiSpecDicom());
        this.setCdAetNodoDicom(entity.getCdAetNodoDicom());
        this.setDlListaSopClass(entity.getDlListaSopClass());
        if (entity.getDtStudyDate() != null) {
            this.setDtStudyDate(new Timestamp(entity.getDtStudyDate().getTime()));
        }
        this.setDsAccessionNumber(entity.getDsAccessionNumber());
        this.setDlListaModalityInStudy(entity.getDlListaModalityInStudy());
        this.setDsInstitutionName(entity.getDsInstitutionName());
        this.setDsRefPhysicianName(entity.getDsRefPhysicianName());
        this.setDlStudyDescription(entity.getDlStudyDescription());
        this.setDsPatientName(entity.getDsPatientName());
        this.setCdPatientId(entity.getCdPatientId());
        this.setCdPatientIdIssuer(entity.getCdPatientIdIssuer());
        if (entity.getDtPatientBirthDate() != null) {
            this.setDtPatientBirthDate(new Timestamp(entity.getDtPatientBirthDate().getTime()));
        }
        this.setTiPatientSex(entity.getTiPatientSex());
        this.setDsStudyInstanceUid(entity.getDsStudyInstanceUid());
        this.setNiStudyRelatedSeries(entity.getNiStudyRelatedSeries());
        this.setNiStudyRelatedImages(entity.getNiStudyRelatedImages());
        this.setDsStudyId(entity.getDsStudyId());
        if (entity.getDtPresaInCarico() != null) {
            this.setDtPresaInCarico(new Timestamp(entity.getDtPresaInCarico().getTime()));
        }
        this.setBlDcmHashTxt(entity.getBlDcmHashTxt());
        this.setDsDcmHash(entity.getDsDcmHash());
        this.setTiAlgoDcmHash(entity.getTiAlgoDcmHash());
        this.setCdEncodingDcmHash(entity.getCdEncodingDcmHash());
        this.setBlGlobalHashTxt(entity.getBlGlobalHashTxt());
        this.setDsGlobalHash(entity.getDsGlobalHash());
        this.setTiAlgoGlobalHash(entity.getTiAlgoGlobalHash());
        this.setCdEncodingGlobalHash(entity.getCdEncodingGlobalHash());
        this.setDsFileHash(entity.getDsFileHash());
        this.setTiAlgoFileHash(entity.getTiAlgoFileHash());
        this.setCdEncodingFileHash(entity.getCdEncodingFileHash());
        if (entity.getPigXsdDatiSpec() != null) {
            this.setIdXsdSpec(new BigDecimal(entity.getPigXsdDatiSpec().getIdXsdSpec()));

        }
    }

    @Override
    public PigInfoDicom rowBeanToEntity() {
        PigInfoDicom entity = new PigInfoDicom();
        if (this.getIdInfoDicom() != null) {
            entity.setIdInfoDicom(this.getIdInfoDicom().longValue());
        }
        if (this.getIdObject() != null) {
            if (entity.getPigObject() == null) {
                entity.setPigObject(new PigObject());
            }
            entity.getPigObject().setIdObject(this.getIdObject().longValue());
        }
        entity.setIdVers(this.getIdVers());
        entity.setCdVersioneDatiSpecDicom(this.getCdVersioneDatiSpecDicom());
        entity.setCdAetNodoDicom(this.getCdAetNodoDicom());
        entity.setDlListaSopClass(this.getDlListaSopClass());
        entity.setDtStudyDate(this.getDtStudyDate());
        entity.setDsAccessionNumber(this.getDsAccessionNumber());
        entity.setDlListaModalityInStudy(this.getDlListaModalityInStudy());
        entity.setDsInstitutionName(this.getDsInstitutionName());
        entity.setDsRefPhysicianName(this.getDsRefPhysicianName());
        entity.setDlStudyDescription(this.getDlStudyDescription());
        entity.setDsPatientName(this.getDsPatientName());
        entity.setCdPatientId(this.getCdPatientId());
        entity.setCdPatientIdIssuer(this.getCdPatientIdIssuer());
        entity.setDtPatientBirthDate(this.getDtPatientBirthDate());
        entity.setTiPatientSex(this.getTiPatientSex());
        entity.setDsStudyInstanceUid(this.getDsStudyInstanceUid());
        entity.setNiStudyRelatedSeries(this.getNiStudyRelatedSeries());
        entity.setNiStudyRelatedImages(this.getNiStudyRelatedImages());
        entity.setDsStudyId(this.getDsStudyId());
        entity.setDtPresaInCarico(this.getDtPresaInCarico());
        entity.setBlDcmHashTxt(this.getBlDcmHashTxt());
        entity.setDsDcmHash(this.getDsDcmHash());
        entity.setTiAlgoDcmHash(this.getTiAlgoDcmHash());
        entity.setCdEncodingDcmHash(this.getCdEncodingDcmHash());
        entity.setBlGlobalHashTxt(this.getBlGlobalHashTxt());
        entity.setDsGlobalHash(this.getDsGlobalHash());
        entity.setTiAlgoGlobalHash(this.getTiAlgoGlobalHash());
        entity.setCdEncodingGlobalHash(this.getCdEncodingGlobalHash());
        entity.setDsFileHash(this.getDsFileHash());
        entity.setTiAlgoFileHash(this.getTiAlgoFileHash());
        entity.setCdEncodingFileHash(this.getCdEncodingFileHash());
        if (this.getIdXsdSpec() != null) {
            if (entity.getPigXsdDatiSpec() == null) {
                entity.setPigXsdDatiSpec(new PigXsdDatiSpec());
            }
            entity.getPigXsdDatiSpec().setIdXsdSpec(this.getIdXsdSpec().longValue());
        }
        return entity;
    }

    // gestione della paginazione
    public void setRownum(Integer rownum) {
        setObject("rownum", rownum);
    }

    public Integer getRownum() {
        return Integer.parseInt(getObject("rownum").toString());
    }

    public void setRnum(Integer rnum) {
        setObject("rnum", rnum);
    }

    public Integer getRnum() {
        return Integer.parseInt(getObject("rnum").toString());
    }

    public void setNumrecords(Integer numRecords) {
        setObject("numrecords", numRecords);
    }

    public Integer getNumrecords() {
        return Integer.parseInt(getObject("numrecords").toString());
    }

}