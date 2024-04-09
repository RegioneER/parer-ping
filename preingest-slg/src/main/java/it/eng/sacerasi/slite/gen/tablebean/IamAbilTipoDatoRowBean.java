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

import it.eng.sacerasi.entity.IamAbilOrganiz;
import it.eng.sacerasi.entity.IamAbilTipoDato;
import it.eng.spagoLite.db.base.JEEBaseRowInterface;
import it.eng.spagoLite.db.base.row.BaseRow;
import it.eng.spagoLite.db.oracle.bean.column.TableDescriptor;

/**
 * RowBean per la tabella Iam_Abil_Tipo_Dato
 *
 */
public class IamAbilTipoDatoRowBean extends BaseRow implements JEEBaseRowInterface {

    /*
     * @Generated( value = "it.eg.dbtool.db.oracle.beangen.Oracle4JPAClientBeanGen$TableBeanWriter", comments =
     * "This class was generated by OraTool", date = "Thursday, 17 April 2014 10:57" )
     */

    private static final long serialVersionUID = 1L;
    public static IamAbilTipoDatoTableDescriptor TABLE_DESCRIPTOR = new IamAbilTipoDatoTableDescriptor();

    public IamAbilTipoDatoRowBean() {
        super();
    }

    public TableDescriptor getTableDescriptor() {
        return TABLE_DESCRIPTOR;
    }

    // getter e setter
    public BigDecimal getIdAbilTipoDato() {
        return getBigDecimal("id_abil_tipo_dato");
    }

    public void setIdAbilTipoDato(BigDecimal idAbilTipoDato) {
        setObject("id_abil_tipo_dato", idAbilTipoDato);
    }

    public BigDecimal getIdAbilOrganiz() {
        return getBigDecimal("id_abil_organiz");
    }

    public void setIdAbilOrganiz(BigDecimal idAbilOrganiz) {
        setObject("id_abil_organiz", idAbilOrganiz);
    }

    public BigDecimal getIdTipoDatoApplic() {
        return getBigDecimal("id_tipo_dato_applic");
    }

    public void setIdTipoDatoApplic(BigDecimal idTipoDatoApplic) {
        setObject("id_tipo_dato_applic", idTipoDatoApplic);
    }

    public String getNmClasseTipoDato() {
        return getString("nm_classe_tipo_dato");
    }

    public void setNmClasseTipoDato(String nmClasseTipoDato) {
        setObject("nm_classe_tipo_dato", nmClasseTipoDato);
    }

    @Override
    public void entityToRowBean(Object obj) {
        IamAbilTipoDato entity = (IamAbilTipoDato) obj;

        this.setIdAbilTipoDato(new BigDecimal(entity.getIdAbilTipoDato()));

        if (entity.getIamAbilOrganiz() != null) {
            this.setIdAbilOrganiz(new BigDecimal(entity.getIamAbilOrganiz().getIdAbilOrganiz()));
        }
        this.setIdTipoDatoApplic(entity.getIdTipoDatoApplic());
        this.setNmClasseTipoDato(entity.getNmClasseTipoDato());
    }

    @Override
    public IamAbilTipoDato rowBeanToEntity() {
        IamAbilTipoDato entity = new IamAbilTipoDato();
        if (this.getIdAbilTipoDato() != null) {
            entity.setIdAbilTipoDato(this.getIdAbilTipoDato().longValue());
        }
        if (this.getIdAbilOrganiz() != null) {
            if (entity.getIamAbilOrganiz() == null) {
                entity.setIamAbilOrganiz(new IamAbilOrganiz());
            }
            entity.getIamAbilOrganiz().setIdAbilOrganiz(this.getIdAbilOrganiz().longValue());
        }
        entity.setIdTipoDatoApplic(this.getIdTipoDatoApplic());
        entity.setNmClasseTipoDato(this.getNmClasseTipoDato());
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
