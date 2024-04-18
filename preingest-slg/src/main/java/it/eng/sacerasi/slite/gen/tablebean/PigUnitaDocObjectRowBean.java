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
import java.util.Objects;

import it.eng.sacerasi.entity.PigObject;
import it.eng.sacerasi.entity.PigUnitaDocObject;
import it.eng.spagoLite.db.base.JEEBaseRowInterface;
import it.eng.spagoLite.db.base.row.BaseRow;
import it.eng.spagoLite.db.oracle.bean.column.TableDescriptor;

/**
 * RowBean per la tabella Pig_Unita_Doc_Object
 *
 */
public class PigUnitaDocObjectRowBean extends BaseRow implements JEEBaseRowInterface {

    private static final long serialVersionUID = 1L;

    /*
     * @Generated( value = "it.eg.dbtool.db.oracle.beangen.Oracle4JPAClientBeanGen$TableBeanWriter", comments =
     * "This class was generated by OraTool", date = "Monday, 17 March 2014 15:12" )
     */

    public static PigUnitaDocObjectTableDescriptor TABLE_DESCRIPTOR = new PigUnitaDocObjectTableDescriptor();

    public PigUnitaDocObjectRowBean() {
        super();
    }

    public TableDescriptor getTableDescriptor() {
        return TABLE_DESCRIPTOR;
    }

    // getter e setter
    public BigDecimal getIdUnitaDocObject() {
        return getBigDecimal("id_unita_doc_object");
    }

    public void setIdUnitaDocObject(BigDecimal idUnitaDocObject) {
        setObject("id_unita_doc_object", idUnitaDocObject);
    }

    public BigDecimal getIdObject() {
        return getBigDecimal("id_object");
    }

    public void setIdObject(BigDecimal idObject) {
        setObject("id_object", idObject);
    }

    public String getCdRegistroUnitaDocSacer() {
        return getString("cd_registro_unita_doc_sacer");
    }

    public void setCdRegistroUnitaDocSacer(String cdRegistroUnitaDocSacer) {
        setObject("cd_registro_unita_doc_sacer", cdRegistroUnitaDocSacer);
    }

    public BigDecimal getAaUnitaDocSacer() {
        return getBigDecimal("aa_unita_doc_sacer");
    }

    public void setAaUnitaDocSacer(BigDecimal aaUnitaDocSacer) {
        setObject("aa_unita_doc_sacer", aaUnitaDocSacer);
    }

    public String getCdKeyUnitaDocSacer() {
        return getString("cd_key_unita_doc_sacer");
    }

    public void setCdKeyUnitaDocSacer(String cdKeyUnitaDocSacer) {
        setObject("cd_key_unita_doc_sacer", cdKeyUnitaDocSacer);
    }

    public BigDecimal getNiSizeFileByte() {
        return getBigDecimal("ni_size_file_byte");
    }

    public void setNiSizeFileByte(BigDecimal niSizeFileByte) {
        setObject("ni_size_file_byte", niSizeFileByte);
    }

    public String getTiStatoUnitaDocObject() {
        return getString("ti_stato_unita_doc_object");
    }

    public void setTiStatoUnitaDocObject(String tiStatoUnitaDocObject) {
        setObject("ti_stato_unita_doc_object", tiStatoUnitaDocObject);
    }

    public String getCdErrSacer() {
        return getString("cd_err_sacer");
    }

    public void setCdErrSacer(String cdErrSacer) {
        setObject("cd_err_sacer", cdErrSacer);
    }

    public String getDlErrSacer() {
        return getString("dl_err_sacer");
    }

    public void setDlErrSacer(String dlErrSacer) {
        setObject("dl_err_sacer", dlErrSacer);
    }

    public BigDecimal getIdVers() {
        return getBigDecimal("id_vers");
    }

    public void setIdVers(BigDecimal idVers) {
        setObject("id_vers", idVers);
    }

    @Override
    public void entityToRowBean(Object obj) {
        PigUnitaDocObject entity = (PigUnitaDocObject) obj;

        this.setIdUnitaDocObject(new BigDecimal(entity.getIdUnitaDocObject()));
        if (entity.getPigObject() != null) {
            this.setIdObject(new BigDecimal(entity.getPigObject().getIdObject()));

        }
        this.setCdRegistroUnitaDocSacer(entity.getCdRegistroUnitaDocSacer());
        this.setAaUnitaDocSacer(entity.getAaUnitaDocSacer());
        this.setCdKeyUnitaDocSacer(entity.getCdKeyUnitaDocSacer());
        this.setNiSizeFileByte(entity.getNiSizeFileByte());
        this.setTiStatoUnitaDocObject(entity.getTiStatoUnitaDocObject());
        this.setCdErrSacer(entity.getCdErrSacer());
        this.setDlErrSacer(entity.getDlErrSacer());
        this.setIdVers(Objects.nonNull(entity.getIdVers()) ? BigDecimal.valueOf(entity.getIdVers()) : null);
    }

    @Override
    public PigUnitaDocObject rowBeanToEntity() {
        PigUnitaDocObject entity = new PigUnitaDocObject();
        if (this.getIdUnitaDocObject() != null) {
            entity.setIdUnitaDocObject(this.getIdUnitaDocObject().longValue());
        }
        if (this.getIdObject() != null) {
            if (entity.getPigObject() == null) {
                entity.setPigObject(new PigObject());
            }
            entity.getPigObject().setIdObject(this.getIdObject().longValue());
        }
        entity.setCdRegistroUnitaDocSacer(this.getCdRegistroUnitaDocSacer());
        entity.setAaUnitaDocSacer(this.getAaUnitaDocSacer());
        entity.setCdKeyUnitaDocSacer(this.getCdKeyUnitaDocSacer());
        entity.setNiSizeFileByte(this.getNiSizeFileByte());
        entity.setTiStatoUnitaDocObject(this.getTiStatoUnitaDocObject());
        entity.setCdErrSacer(this.getCdErrSacer());
        entity.setDlErrSacer(this.getDlErrSacer());
        entity.setIdVers(Objects.nonNull(this.getIdVers()) ? this.getIdVers().longValue() : null);
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