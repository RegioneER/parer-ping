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
import java.util.Calendar;

import it.eng.sacerasi.viewEntity.MonVLisVersFalliti;
import it.eng.spagoLite.db.base.JEEBaseRowInterface;
import it.eng.spagoLite.db.base.row.BaseRow;
import it.eng.spagoLite.db.oracle.bean.column.TableDescriptor;

/**
 * RowBean per la tabella Mon_V_Lis_Vers_Falliti
 *
 */
public class MonVLisVersFallitiRowBean extends BaseRow implements JEEBaseRowInterface {

    private static final long serialVersionUID = 1L;

    /*
     * @Generated( value = "it.eg.dbtool.db.oracle.beangen.Oracle4JPAClientBeanGen$ViewBeanWriter", comments =
     * "This class was generated by OraTool", date = "Tuesday, 23 August 2016 14:32" )
     */
    public static MonVLisVersFallitiTableDescriptor TABLE_DESCRIPTOR = new MonVLisVersFallitiTableDescriptor();

    public MonVLisVersFallitiRowBean() {
        super();
    }

    public TableDescriptor getTableDescriptor() {
        return TABLE_DESCRIPTOR;
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

    public Timestamp getDtStatoCor() {
        return getTimestamp("dt_stato_cor");
    }

    public void setDtStatoCor(Timestamp dtStatoCor) {
        setObject("dt_stato_cor", dtStatoCor);
    }

    public BigDecimal getIdAmbienteVers() {
        return getBigDecimal("id_ambiente_vers");
    }

    public void setIdAmbienteVers(BigDecimal idAmbienteVers) {
        setObject("id_ambiente_vers", idAmbienteVers);
    }

    public String getNmAmbienteVers() {
        return getString("nm_ambiente_vers");
    }

    public void setNmAmbienteVers(String nmAmbienteVers) {
        setObject("nm_ambiente_vers", nmAmbienteVers);
    }

    public BigDecimal getIdVers() {
        return getBigDecimal("id_vers");
    }

    public void setIdVers(BigDecimal idVers) {
        setObject("id_vers", idVers);
    }

    public String getNmVers() {
        return getString("nm_vers");
    }

    public void setNmVers(String nmVers) {
        setObject("nm_vers", nmVers);
    }

    public String getCdKeyObject() {
        return getString("cd_key_object");
    }

    public void setCdKeyObject(String cdKeyObject) {
        setObject("cd_key_object", cdKeyObject);
    }

    public String getDsObject() {
        return getString("ds_object");
    }

    public void setDsObject(String dsObject) {
        setObject("ds_object", dsObject);
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

    public String getTiStatoRisoluz() {
        return getString("ti_stato_risoluz");
    }

    public void setTiStatoRisoluz(String tiStatoRisoluz) {
        setObject("ti_stato_risoluz", tiStatoRisoluz);
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

    public String getNmTipoObject() {
        return getString("nm_tipo_object");
    }

    public void setNmTipoObject(String nmTipoObject) {
        setObject("nm_tipo_object", nmTipoObject);
    }

    public String getTiDtCreazione() {
        return getString("ti_dt_creazione");
    }

    public void setTiDtCreazione(String tiDtCreazione) {
        setObject("ti_dt_creazione", tiDtCreazione);
    }

    public String getBlXml() {
        return getString("bl_xml");
    }

    public void setBlXml(String blXml) {
        setObject("bl_xml", blXml);
    }

    @Override
    public void entityToRowBean(Object obj) {
        MonVLisVersFalliti entity = (MonVLisVersFalliti) obj;
        this.setIdSessioneIngest(entity.getIdSessioneIngest());
        if (entity.getDtApertura() != null) {
            this.setDtApertura(new Timestamp(entity.getDtApertura().getTime()));
        }
        this.setTiStato(entity.getTiStato());
        if (entity.getDtStatoCor() != null) {
            this.setDtStatoCor(new Timestamp(entity.getDtStatoCor().getTime()));
        }
        this.setIdAmbienteVers(entity.getIdAmbienteVers());
        this.setNmAmbienteVers(entity.getNmAmbienteVers());
        this.setIdVers(entity.getIdVers());
        this.setNmVers(entity.getNmVers());
        this.setCdKeyObject(entity.getCdKeyObject());
        this.setDsObject(entity.getDsObject());
        this.setCdErr(entity.getCdErr());
        this.setDlErr(entity.getDlErr());
        this.setTiStatoRisoluz(entity.getTiStatoRisoluz());
        this.setFlVerif(entity.getFlVerif());
        this.setFlNonRisolub(entity.getFlNonRisolub());
        this.setNmTipoObject(entity.getNmTipoObject());
        this.setTiDtCreazione(entity.getTiDtCreazione());
        this.setBlXml(entity.getBlXml());
    }

    @Override
    public MonVLisVersFalliti rowBeanToEntity() {
        MonVLisVersFalliti entity = new MonVLisVersFalliti();
        entity.setIdSessioneIngest(this.getIdSessioneIngest());
        entity.setDtApertura(this.getDtApertura());
        entity.setTiStato(this.getTiStato());
        if (this.getDtStatoCor() != null) {
            Calendar tmp = Calendar.getInstance();
            tmp.setTimeInMillis(this.getDtStatoCor().getTime());
            entity.setDtStatoCor(tmp.getTime());
        }
        entity.setIdAmbienteVers(this.getIdAmbienteVers());
        entity.setNmAmbienteVers(this.getNmAmbienteVers());
        entity.setIdVers(this.getIdVers());
        entity.setNmVers(this.getNmVers());
        entity.setCdKeyObject(this.getCdKeyObject());
        entity.setDsObject(this.getDsObject());
        entity.setCdErr(this.getCdErr());
        entity.setDlErr(this.getDlErr());
        entity.setTiStatoRisoluz(this.getTiStatoRisoluz());
        entity.setFlVerif(this.getFlVerif());
        entity.setFlNonRisolub(this.getFlNonRisolub());
        entity.setNmTipoObject(this.getNmTipoObject());
        entity.setTiDtCreazione(this.getTiDtCreazione());
        entity.setBlXml(this.getBlXml());
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