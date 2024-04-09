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

package it.eng.sacerasi.slite.gen.viewbean;

import java.math.BigDecimal;

import it.eng.sacerasi.viewEntity.MonVLisFileObject;
import it.eng.spagoLite.db.base.JEEBaseRowInterface;
import it.eng.spagoLite.db.base.row.BaseRow;
import it.eng.spagoLite.db.oracle.bean.column.TableDescriptor;

/**
 * RowBean per la tabella Mon_V_Lis_File_Object
 *
 */
public class MonVLisFileObjectRowBean extends BaseRow implements JEEBaseRowInterface {

    private static final long serialVersionUID = 1L;

    /*
     * @Generated( value = "it.eg.dbtool.db.oracle.beangen.Oracle4JPAClientBeanGen$ViewBeanWriter", comments =
     * "This class was generated by OraTool", date = "Monday, 17 March 2014 15:12" )
     */

    public static MonVLisFileObjectTableDescriptor TABLE_DESCRIPTOR = new MonVLisFileObjectTableDescriptor();

    public MonVLisFileObjectRowBean() {
        super();
    }

    public TableDescriptor getTableDescriptor() {
        return TABLE_DESCRIPTOR;
    }

    public BigDecimal getIdObject() {
        return getBigDecimal("id_object");
    }

    public void setIdObject(BigDecimal idObject) {
        setObject("id_object", idObject);
    }

    public BigDecimal getIdFileObject() {
        return getBigDecimal("id_file_object");
    }

    public void setIdFileObject(BigDecimal idFileObject) {
        setObject("id_file_object", idFileObject);
    }

    public String getNmFileObject() {
        return getString("nm_file_object");
    }

    public void setNmFileObject(String nmFileObject) {
        setObject("nm_file_object", nmFileObject);
    }

    public BigDecimal getIdTipoFileObject() {
        return getBigDecimal("id_tipo_file_object");
    }

    public void setIdTipoFileObject(BigDecimal idTipoFileObject) {
        setObject("id_tipo_file_object", idTipoFileObject);
    }

    public String getNmTipoFileObject() {
        return getString("nm_tipo_file_object");
    }

    public void setNmTipoFileObject(String nmTipoFileObject) {
        setObject("nm_tipo_file_object", nmTipoFileObject);
    }

    public String getDsHashFileVers() {
        return getString("ds_hash_file_vers");
    }

    public void setDsHashFileVers(String dsHashFileVers) {
        setObject("ds_hash_file_vers", dsHashFileVers);
    }

    public String getTiAlgoHashFileVers() {
        return getString("ti_algo_hash_file_vers");
    }

    public void setTiAlgoHashFileVers(String tiAlgoHashFileVers) {
        setObject("ti_algo_hash_file_vers", tiAlgoHashFileVers);
    }

    public String getCdEncodingHashFileVers() {
        return getString("cd_encoding_hash_file_vers");
    }

    public void setCdEncodingHashFileVers(String cdEncodingHashFileVers) {
        setObject("cd_encoding_hash_file_vers", cdEncodingHashFileVers);
    }

    public BigDecimal getNiSizeFileVers() {
        return getBigDecimal("ni_size_file_vers");
    }

    public void setNiSizeFileVers(BigDecimal niSizeFileVers) {
        setObject("ni_size_file_vers", niSizeFileVers);
    }

    public String getNmBucket() {
        return getString("nm_bucket");
    }

    public void setNmBucket(String nm_bucket) {
        setObject("nm_bucket", nm_bucket);
    }

    public String getCdKeyFile() {
        return getString("cd_key_file");
    }

    public void setCdKeyFile(String cd_key_file) {
        setObject("cd_key_file", cd_key_file);
    }

    @Override
    public void entityToRowBean(Object obj) {
        MonVLisFileObject entity = (MonVLisFileObject) obj;
        this.setIdObject(entity.getIdObject());
        this.setIdFileObject(entity.getIdFileObject());
        this.setNmFileObject(entity.getNmFileObject());
        this.setIdTipoFileObject(entity.getIdTipoFileObject());
        this.setNmTipoFileObject(entity.getNmTipoFileObject());
        this.setDsHashFileVers(entity.getDsHashFileVers());
        this.setTiAlgoHashFileVers(entity.getTiAlgoHashFileVers());
        this.setCdEncodingHashFileVers(entity.getCdEncodingHashFileVers());
        this.setNiSizeFileVers(entity.getNiSizeFileVers());
        // MEV21995
        this.setNmBucket(entity.getNmBucket());
        this.setCdKeyFile(entity.getCdKeyFile());
    }

    @Override
    public MonVLisFileObject rowBeanToEntity() {
        MonVLisFileObject entity = new MonVLisFileObject();
        entity.setIdObject(this.getIdObject());
        entity.setIdFileObject(this.getIdFileObject());
        entity.setNmFileObject(this.getNmFileObject());
        entity.setIdTipoFileObject(this.getIdTipoFileObject());
        entity.setNmTipoFileObject(this.getNmTipoFileObject());
        entity.setDsHashFileVers(this.getDsHashFileVers());
        entity.setTiAlgoHashFileVers(this.getTiAlgoHashFileVers());
        entity.setCdEncodingHashFileVers(this.getCdEncodingHashFileVers());
        entity.setNiSizeFileVers(this.getNiSizeFileVers());
        // MEV21995
        entity.setNmBucket(this.getNmBucket());
        entity.setCdKeyFile(this.getCdKeyFile());
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
