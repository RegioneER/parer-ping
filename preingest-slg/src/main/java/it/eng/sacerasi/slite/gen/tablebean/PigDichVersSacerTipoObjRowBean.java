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

import it.eng.sacerasi.entity.PigDichVersSacerTipoObj;
import it.eng.sacerasi.entity.PigTipoObject;
import it.eng.spagoLite.db.base.JEEBaseRowInterface;
import it.eng.spagoLite.db.base.row.BaseRow;
import it.eng.spagoLite.db.oracle.bean.column.TableDescriptor;

/**
 * RowBean per la tabella Pig_Dich_Vers_Sacer_Tipo_Obj
 *
 */
public class PigDichVersSacerTipoObjRowBean extends BaseRow implements JEEBaseRowInterface {

    /*
     * @Generated( value = "it.eg.dbtool.db.oracle.beangen.Oracle4JPAClientBeanGen$TableBeanWriter", comments =
     * "This class was generated by OraTool", date = "Tuesday, 11 April 2017 15:39" )
     */

    private static final long serialVersionUID = 1L;
    public static PigDichVersSacerTipoObjTableDescriptor TABLE_DESCRIPTOR = new PigDichVersSacerTipoObjTableDescriptor();

    public PigDichVersSacerTipoObjRowBean() {
        super();
    }

    public TableDescriptor getTableDescriptor() {
        return TABLE_DESCRIPTOR;
    }

    // getter e setter
    public BigDecimal getIdDichVersSacerTipoObj() {
        return getBigDecimal("id_dich_vers_sacer_tipo_obj");
    }

    public void setIdDichVersSacerTipoObj(BigDecimal idDichVersSacerTipoObj) {
        setObject("id_dich_vers_sacer_tipo_obj", idDichVersSacerTipoObj);
    }

    public BigDecimal getIdTipoObject() {
        return getBigDecimal("id_tipo_object");
    }

    public void setIdTipoObject(BigDecimal idTipoObject) {
        setObject("id_tipo_object", idTipoObject);
    }

    public BigDecimal getIdOrganizIam() {
        return getBigDecimal("id_organiz_iam");
    }

    public void setIdOrganizIam(BigDecimal idOrganizIam) {
        setObject("id_organiz_iam", idOrganizIam);
    }

    public String getTiDichVers() {
        return getString("ti_dich_vers");
    }

    public void setTiDichVers(String tiDichVers) {
        setObject("ti_dich_vers", tiDichVers);
    }

    @Override
    public void entityToRowBean(Object obj) {
        PigDichVersSacerTipoObj entity = (PigDichVersSacerTipoObj) obj;

        this.setIdDichVersSacerTipoObj(new BigDecimal(entity.getIdDichVersSacerTipoObj()));
        if (entity.getPigTipoObject() != null) {
            this.setIdTipoObject(new BigDecimal(entity.getPigTipoObject().getIdTipoObject()));
        }
        this.setIdOrganizIam(entity.getIdOrganizIam());
        this.setTiDichVers(entity.getTiDichVers());
    }

    @Override
    public PigDichVersSacerTipoObj rowBeanToEntity() {
        PigDichVersSacerTipoObj entity = new PigDichVersSacerTipoObj();
        if (this.getIdDichVersSacerTipoObj() != null) {
            entity.setIdDichVersSacerTipoObj(this.getIdDichVersSacerTipoObj().longValue());
        }
        if (this.getIdTipoObject() != null) {
            if (entity.getPigTipoObject() == null) {
                entity.setPigTipoObject(new PigTipoObject());
            }
            entity.getPigTipoObject().setIdTipoObject(this.getIdTipoObject().longValue());
        }
        entity.setIdOrganizIam(this.getIdOrganizIam());
        entity.setTiDichVers(this.getTiDichVers());
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
