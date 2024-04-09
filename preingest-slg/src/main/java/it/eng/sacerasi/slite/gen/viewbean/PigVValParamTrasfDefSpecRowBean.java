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

import it.eng.sacerasi.viewEntity.PigVValParamTrasfDefSpec;
import it.eng.sacerasi.viewEntity.PigVValParamTrasfDefSpecId;
import it.eng.spagoLite.db.base.JEEBaseRowInterface;
import it.eng.spagoLite.db.base.row.BaseRow;
import it.eng.spagoLite.db.oracle.bean.column.TableDescriptor;

public class PigVValParamTrasfDefSpecRowBean extends BaseRow implements JEEBaseRowInterface {

    private static final long serialVersionUID = 1L;

    /*
     * @Generated( value = "it.eg.dbtool.db.oracle.beangen.Oracle4JPAClientBeanGen$ViewBeanWriter", comments =
     * "This class was generated by OraTool", date = "Friday, 5 May 2017 15:11" )
     */

    public static PigVValParamTrasfDefSpecTableDescriptor TABLE_DESCRIPTOR = new PigVValParamTrasfDefSpecTableDescriptor();

    public PigVValParamTrasfDefSpecRowBean() {
        super();
    }

    public TableDescriptor getTableDescriptor() {
        return TABLE_DESCRIPTOR;
    }

    public BigDecimal getIdVersTipoObjectDaTrasf() {
        return getBigDecimal("id_vers_tipo_object_da_trasf");
    }

    public void setIdVersTipoObjectDaTrasf(BigDecimal idVersTipoObjectDaTrasf) {
        setObject("id_vers_tipo_object_da_trasf", idVersTipoObjectDaTrasf);
    }

    public BigDecimal getIdSetParamTrasf() {
        return getBigDecimal("id_set_param_trasf");
    }

    public void setIdSetParamTrasf(BigDecimal idSetParamTrasf) {
        setObject("id_set_param_trasf", idSetParamTrasf);
    }

    public String getNmSetParamTrasf() {
        return getString("nm_set_param_trasf");
    }

    public void setNmSetParamTrasf(String nmSetParamTrasf) {
        setObject("nm_set_param_trasf", nmSetParamTrasf);
    }

    public String getNmParamTrasf() {
        return getString("nm_param_trasf");
    }

    public void setNmParamTrasf(String nmParamTrasf) {
        setObject("nm_param_trasf", nmParamTrasf);
    }

    public BigDecimal getIdParamTrasf() {
        return getBigDecimal("id_param_trasf");
    }

    public void setIdParamTrasf(BigDecimal idParamTrasf) {
        setObject("id_param_trasf", idParamTrasf);
    }

    public String getDsParamTrasf() {
        return getString("ds_param_trasf");
    }

    public void setDsParamTrasf(String dsParamTrasf) {
        setObject("ds_param_trasf", dsParamTrasf);
    }

    public String getTiParamTrasf() {
        return getString("ti_param_trasf");
    }

    public void setTiParamTrasf(String tiParamTrasf) {
        setObject("ti_param_trasf", tiParamTrasf);
    }

    public BigDecimal getIdValoreSetParamTrasf() {
        return getBigDecimal("id_valore_set_param_trasf");
    }

    public void setIdValoreSetParamTrasf(BigDecimal idValoreSetParamTrasf) {
        setObject("id_valore_set_param_trasf", idValoreSetParamTrasf);
    }

    public BigDecimal getIdValoreParamTrasf() {
        return getBigDecimal("id_valore_param_trasf");
    }

    public void setIdValoreParamTrasf(BigDecimal idValoreParamTrasf) {
        setObject("id_valore_param_trasf", idValoreParamTrasf);
    }

    public String getValParam() {
        return getString("val_param");
    }

    public void setValParam(String valParam) {
        setObject("val_param", valParam);
    }

    public String getTiValParam() {
        return getString("ti_val_param");
    }

    public void setTiValParam(String tiValParam) {
        setObject("ti_val_param", tiValParam);
    }

    @Override
    public void entityToRowBean(Object obj) {
        PigVValParamTrasfDefSpec entity = (PigVValParamTrasfDefSpec) obj;
        this.setIdVersTipoObjectDaTrasf(entity.getPigVValParamTrasfDefSpecId() == null ? null
                : entity.getPigVValParamTrasfDefSpecId().getIdVersTipoObjectDaTrasf());
        this.setIdSetParamTrasf(entity.getIdSetParamTrasf());
        this.setNmSetParamTrasf(entity.getNmSetParamTrasf());
        this.setNmParamTrasf(entity.getNmParamTrasf());
        this.setIdParamTrasf(entity.getPigVValParamTrasfDefSpecId() == null ? null
                : entity.getPigVValParamTrasfDefSpecId().getIdParamTrasf());
        this.setDsParamTrasf(entity.getDsParamTrasf());
        this.setTiParamTrasf(entity.getTiParamTrasf());
        this.setIdValoreSetParamTrasf(entity.getIdValoreSetParamTrasf());
        this.setIdValoreParamTrasf(entity.getIdValoreParamTrasf());
        this.setValParam(entity.getValParam());
        this.setTiValParam(entity.getTiValParam());
    }

    @Override
    public PigVValParamTrasfDefSpec rowBeanToEntity() {
        PigVValParamTrasfDefSpec entity = new PigVValParamTrasfDefSpec();
        entity.setPigVValParamTrasfDefSpecId(new PigVValParamTrasfDefSpecId());
        entity.getPigVValParamTrasfDefSpecId().setIdVersTipoObjectDaTrasf(this.getIdVersTipoObjectDaTrasf());
        entity.setIdSetParamTrasf(this.getIdSetParamTrasf());
        entity.setNmSetParamTrasf(this.getNmSetParamTrasf());
        entity.setNmParamTrasf(this.getNmParamTrasf());
        entity.getPigVValParamTrasfDefSpecId().setIdParamTrasf(this.getIdParamTrasf());
        entity.setDsParamTrasf(this.getDsParamTrasf());
        entity.setTiParamTrasf(this.getTiParamTrasf());
        entity.setIdValoreSetParamTrasf(this.getIdValoreSetParamTrasf());
        entity.setIdValoreParamTrasf(this.getIdValoreParamTrasf());
        entity.setValParam(this.getValParam());
        entity.setTiValParam(this.getTiValParam());
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
