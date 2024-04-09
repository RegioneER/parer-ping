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
import java.sql.Timestamp;

import it.eng.sacerasi.viewEntity.MonVLisVersObj;
import it.eng.spagoLite.db.base.JEEBaseRowInterface;
import it.eng.spagoLite.db.base.row.BaseRow;
import it.eng.spagoLite.db.oracle.bean.column.TableDescriptor;

/**
 * RowBean per la tabella Mon_V_Lis_Vers_Obj
 *
 */
public class MonVLisVersObjRowBean extends BaseRow implements JEEBaseRowInterface {

    private static final long serialVersionUID = 1L;

    /*
     * @Generated( value = "it.eg.dbtool.db.oracle.beangen.Oracle4JPAClientBeanGen$ViewBeanWriter", comments =
     * "This class was generated by OraTool", date = "Thursday, 5 January 2017 16:41" )
     */

    public static MonVLisVersObjTableDescriptor TABLE_DESCRIPTOR = new MonVLisVersObjTableDescriptor();

    public MonVLisVersObjRowBean() {
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

    public BigDecimal getIdSessioneIngest() {
        return getBigDecimal("id_sessione_ingest");
    }

    public void setIdSessioneIngest(BigDecimal idSessioneIngest) {
        setObject("id_sessione_ingest", idSessioneIngest);
    }

    public Timestamp getDtApertura() {
        return getTimestamp("dt_apertura");
    }

    public void setDtApertura(Timestamp dtApertura) {
        setObject("dt_apertura", dtApertura);
    }

    public String getTiStato() {
        return getString("ti_stato");
    }

    public void setTiStato(String tiStato) {
        setObject("ti_stato", tiStato);
    }

    public String getCdErr() {
        return getString("cd_err");
    }

    public void setCdErr(String cdErr) {
        setObject("cd_err", cdErr);
    }

    public String getDlErr() {
        return getString("dl_err");
    }

    public void setDlErr(String dlErr) {
        setObject("dl_err", dlErr);
    }

    public String getFlVerif() {
        return getString("fl_verif");
    }

    public void setFlVerif(String flVerif) {
        setObject("fl_verif", flVerif);
    }

    public String getFlNonRisolub() {
        return getString("fl_non_risolub");
    }

    public void setFlNonRisolub(String flNonRisolub) {
        setObject("fl_non_risolub", flNonRisolub);
    }

    public String getNote() {
        return getString("note");
    }

    public void setNote(String note) {
        setObject("note", note);
    }

    public Timestamp getTsRegStato() {
        return getTimestamp("ts_reg_stato");
    }

    public void setTsRegStato(Timestamp tsRegStato) {
        setObject("ts_reg_stato", tsRegStato);
    }

    public String getNmReportTrasfOS() {
        return getString("nmReportTrasfOS");
    }

    public void setNmReportTrasfOS(String nmReportTrasfOS) {
        setObject("nmReportTrasfOS", nmReportTrasfOS);
    }

    @Override
    public void entityToRowBean(Object obj) {
        MonVLisVersObj entity = (MonVLisVersObj) obj;
        this.setIdObject(entity.getIdObject());
        this.setIdSessioneIngest(entity.getIdSessioneIngest());
        if (entity.getDtApertura() != null) {
            this.setDtApertura(new Timestamp(entity.getDtApertura().getTime()));
        }
        this.setTiStato(entity.getTiStato());
        this.setCdErr(entity.getCdErr());
        this.setDlErr(entity.getDlErr());
        this.setFlVerif(entity.getFlVerif());
        this.setFlNonRisolub(entity.getFlNonRisolub());
        if (entity.getTsRegStato() != null) {
            this.setTsRegStato(new Timestamp(entity.getTsRegStato().getTime()));
        }
        this.setNote(entity.getNote());
        this.setNmReportTrasfOS(entity.getNmReportTrasfOS());
    }

    @Override
    public MonVLisVersObj rowBeanToEntity() {
        MonVLisVersObj entity = new MonVLisVersObj();
        entity.setIdObject(this.getIdObject());
        entity.setIdSessioneIngest(this.getIdSessioneIngest());
        entity.setDtApertura(this.getDtApertura());
        entity.setTiStato(this.getTiStato());
        entity.setCdErr(this.getCdErr());
        entity.setDlErr(this.getDlErr());
        entity.setFlVerif(this.getFlVerif());
        entity.setFlNonRisolub(this.getFlNonRisolub());
        entity.setTsRegStato(this.getTsRegStato());
        entity.setNmReportTrasfOS(this.getNmReportTrasfOS());
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
