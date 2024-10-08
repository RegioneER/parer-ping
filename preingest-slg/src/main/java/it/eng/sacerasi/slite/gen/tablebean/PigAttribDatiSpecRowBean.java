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

import it.eng.sacerasi.entity.PigAttribDatiSpec;
import it.eng.sacerasi.entity.PigXsdDatiSpec;
import it.eng.spagoLite.db.base.JEEBaseRowInterface;
import it.eng.spagoLite.db.base.row.BaseRow;
import it.eng.spagoLite.db.oracle.bean.column.TableDescriptor;

/**
 * RowBean per la tabella Pig_Attrib_Dati_Spec
 *
 */
public class PigAttribDatiSpecRowBean extends BaseRow implements JEEBaseRowInterface {

    /*
     * @Generated( value = "it.eg.dbtool.db.oracle.beangen.Oracle4JPAClientBeanGen$TableBeanWriter", comments =
     * "This class was generated by OraTool", date = "Monday, 17 March 2014 15:12" )
     */

    private static final long serialVersionUID = 1L;
    public static PigAttribDatiSpecTableDescriptor TABLE_DESCRIPTOR = new PigAttribDatiSpecTableDescriptor();

    public PigAttribDatiSpecRowBean() {
        super();
    }

    public TableDescriptor getTableDescriptor() {
        return TABLE_DESCRIPTOR;
    }

    // getter e setter
    public BigDecimal getIdAttribDatiSpec() {
        return getBigDecimal("id_attrib_dati_spec");
    }

    public void setIdAttribDatiSpec(BigDecimal idAttribDatiSpec) {
        setObject("id_attrib_dati_spec", idAttribDatiSpec);
    }

    public BigDecimal getIdXsdSpec() {
        return getBigDecimal("id_xsd_spec");
    }

    public void setIdXsdSpec(BigDecimal idXsdSpec) {
        setObject("id_xsd_spec", idXsdSpec);
    }

    public String getNmAttribDatiSpec() {
        return getString("nm_attrib_dati_spec");
    }

    public void setNmAttribDatiSpec(String nmAttribDatiSpec) {
        setObject("nm_attrib_dati_spec", nmAttribDatiSpec);
    }

    public BigDecimal getNiOrd() {
        return getBigDecimal("ni_ord");
    }

    public void setNiOrd(BigDecimal niOrd) {
        setObject("ni_ord", niOrd);
    }

    public String getFlFiltroDiario() {
        return getString("fl_filtro_diario");
    }

    public void setFlFiltroDiario(String flFiltroDiario) {
        setObject("fl_filtro_diario", flFiltroDiario);
    }

    public String getFlVersSacer() {
        return getString("fl_vers_sacer");
    }

    public void setFlVersSacer(String flVersSacer) {
        setObject("fl_vers_sacer", flVersSacer);
    }

    public String getNmColDatiSpec() {
        return getString("nm_col_dati_spec");
    }

    public void setNmColDatiSpec(String nmColDatiSpec) {
        setObject("nm_col_dati_spec", nmColDatiSpec);
    }

    public String getCdDatatypeXsd() {
        return getString("cd_datatype_xsd");
    }

    public void setCdDatatypeXsd(String cdDatatypeXsd) {
        setObject("cd_datatype_xsd", cdDatatypeXsd);
    }

    public String getTiDatatypeCol() {
        return getString("ti_datatype_col");
    }

    public void setTiDatatypeCol(String tiDatatypeCol) {
        setObject("ti_datatype_col", tiDatatypeCol);
    }

    @Override
    public void entityToRowBean(Object obj) {
        PigAttribDatiSpec entity = (PigAttribDatiSpec) obj;

        this.setIdAttribDatiSpec(new BigDecimal(entity.getIdAttribDatiSpec()));
        if (entity.getPigXsdDatiSpec() != null) {
            this.setIdXsdSpec(new BigDecimal(entity.getPigXsdDatiSpec().getIdXsdSpec()));
        }

        this.setNmAttribDatiSpec(entity.getNmAttribDatiSpec());
        this.setNiOrd(entity.getNiOrd());
        this.setFlFiltroDiario(entity.getFlFiltroDiario());
        this.setFlVersSacer(entity.getFlVersSacer());
        this.setNmColDatiSpec(entity.getNmColDatiSpec());
        this.setCdDatatypeXsd(entity.getCdDatatypeXsd());
        this.setTiDatatypeCol(entity.getTiDatatypeCol());
    }

    @Override
    public PigAttribDatiSpec rowBeanToEntity() {
        PigAttribDatiSpec entity = new PigAttribDatiSpec();
        if (this.getIdAttribDatiSpec() != null) {
            entity.setIdAttribDatiSpec(this.getIdAttribDatiSpec().longValue());
        }
        if (this.getIdXsdSpec() != null) {
            if (entity.getPigXsdDatiSpec() == null) {
                entity.setPigXsdDatiSpec(new PigXsdDatiSpec());
            }
            entity.getPigXsdDatiSpec().setIdXsdSpec(this.getIdXsdSpec().longValue());
        }
        entity.setNmAttribDatiSpec(this.getNmAttribDatiSpec());
        entity.setNiOrd(this.getNiOrd());
        entity.setFlFiltroDiario(this.getFlFiltroDiario());
        entity.setFlVersSacer(this.getFlVersSacer());
        entity.setNmColDatiSpec(this.getNmColDatiSpec());
        entity.setCdDatatypeXsd(this.getCdDatatypeXsd());
        entity.setTiDatatypeCol(this.getTiDatatypeCol());
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
