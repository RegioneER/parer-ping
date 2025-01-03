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

import it.eng.spagoLite.db.base.BaseRowInterface;
import it.eng.spagoLite.db.base.JEEBaseRowInterface;
import it.eng.spagoLite.db.base.row.BaseRow;
import it.eng.spagoLite.db.oracle.bean.column.TableDescriptor;
import it.eng.sacerasi.entity.PigStrumUrbStoricoStati;
import it.eng.sacerasi.entity.PigStrumentiUrbanistici;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.sql.Clob;
import java.sql.Types;
import java.sql.SQLException;
import javax.annotation.Generated;

/**
 * RowBean per la tabella Pig_Strum_Urb_Storico_Stati
 *
 */
public class PigStrumUrbStoricoStatiRowBean extends BaseRow implements BaseRowInterface, JEEBaseRowInterface {

    /*
     * @Generated( value = "it.eg.dbtool.db.oracle.beangen.Oracle4JPAClientBeanGen$TableBeanWriter", comments =
     * "This class was generated by OraTool", date = "Wednesday, 15 May 2024 12:32" )
     */

    public static PigStrumUrbStoricoStatiTableDescriptor TABLE_DESCRIPTOR = new PigStrumUrbStoricoStatiTableDescriptor();

    public PigStrumUrbStoricoStatiRowBean() {
        super();
    }

    public TableDescriptor getTableDescriptor() {
        return TABLE_DESCRIPTOR;
    }

    // getter e setter
    public BigDecimal getIdStato() {
        return getBigDecimal("id_stato");
    }

    public void setIdStato(BigDecimal idStato) {
        setObject("id_stato", idStato);
    }

    public BigDecimal getIdStrumentiUrbanistici() {
        return getBigDecimal("id_strumenti_urbanistici");
    }

    public void setIdStrumentiUrbanistici(BigDecimal idStrumentiUrbanistici) {
        setObject("id_strumenti_urbanistici", idStrumentiUrbanistici);
    }

    public String getTiStato() {
        return getString("ti_stato");
    }

    public void setTiStato(String tiStato) {
        setObject("ti_stato", tiStato);
    }

    public Timestamp getTsRegStato() {
        return getTimestamp("ts_reg_stato");
    }

    public void setTsRegStato(Timestamp tsRegStato) {
        setObject("ts_reg_stato", tsRegStato);
    }

    public String getCdDesc() {
        return getString("cd_desc");
    }

    public void setCdDesc(String cdDesc) {
        setObject("cd_desc", cdDesc);
    }

    @Override
    public void entityToRowBean(Object obj) {
        PigStrumUrbStoricoStati entity = (PigStrumUrbStoricoStati) obj;
        this.setIdStato(entity.getIdStato() == null ? null : BigDecimal.valueOf(entity.getIdStato()));
        if (entity.getPigStrumentiUrbanistici() != null) {
            this.setIdStrumentiUrbanistici(entity.getPigStrumentiUrbanistici().getIdStrumentiUrbanistici() == null
                    ? null : BigDecimal.valueOf(entity.getPigStrumentiUrbanistici().getIdStrumentiUrbanistici()));
        }
        this.setTiStato(entity.getTiStato());
        this.setTsRegStato(new Timestamp(entity.getTsRegStato().getTime()));
        this.setCdDesc(entity.getDescrizione());
    }

    @Override
    public PigStrumUrbStoricoStati rowBeanToEntity() {
        PigStrumUrbStoricoStati entity = new PigStrumUrbStoricoStati();
        if (this.getIdStato() != null) {
            entity.setIdStato(this.getIdStato().longValue());
        }
        if (this.getIdStrumentiUrbanistici() != null) {
            if (entity.getPigStrumentiUrbanistici() == null) {
                entity.setPigStrumentiUrbanistici(new PigStrumentiUrbanistici());
            }
            entity.getPigStrumentiUrbanistici().setIdStrumentiUrbanistici(this.getIdStrumentiUrbanistici().longValue());
        }
        entity.setTiStato(this.getTiStato());
        entity.setTsRegStato(this.getTsRegStato());
        entity.setDescrizione(this.getCdDesc());
        return entity;
    }

    // gestione della paginazione
    public void setRownum(Integer rownum) {
        setObject("rownum", rownum);
    }

    public Integer getRownum() {
        return Integer.valueOf(getObject("rownum").toString());
    }

    public void setRnum(Integer rnum) {
        setObject("rnum", rnum);
    }

    public Integer getRnum() {
        return Integer.valueOf(getObject("rnum").toString());
    }

    public void setNumrecords(Integer numRecords) {
        setObject("numrecords", numRecords);
    }

    public Integer getNumrecords() {
        return Integer.valueOf(getObject("numrecords").toString());
    }

}
