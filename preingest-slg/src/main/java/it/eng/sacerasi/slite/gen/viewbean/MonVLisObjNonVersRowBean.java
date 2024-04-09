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

import it.eng.sacerasi.viewEntity.MonVLisObjNonVers;
import it.eng.sacerasi.viewEntity.MonVLisObjNonVersId;
import it.eng.spagoLite.db.base.JEEBaseRowInterface;
import it.eng.spagoLite.db.base.row.BaseRow;
import it.eng.spagoLite.db.oracle.bean.column.TableDescriptor;

/**
 * RowBean per la tabella Mon_V_Lis_Obj_Non_Vers
 *
 */
public class MonVLisObjNonVersRowBean extends BaseRow implements JEEBaseRowInterface {

    private static final long serialVersionUID = 1L;

    /*
     * @Generated( value = "it.eg.dbtool.db.oracle.beangen.Oracle4JPAClientBeanGen$ViewBeanWriter", comments =
     * "This class was generated by OraTool", date = "Thursday, 18 January 2018 11:13" )
     */

    public static MonVLisObjNonVersTableDescriptor TABLE_DESCRIPTOR = new MonVLisObjNonVersTableDescriptor();

    public MonVLisObjNonVersRowBean() {
        super();
    }

    public TableDescriptor getTableDescriptor() {
        return TABLE_DESCRIPTOR;
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

    public String getNmTipoObject() {
        return getString("nm_tipo_object");
    }

    public void setNmTipoObject(String nmTipoObject) {
        setObject("nm_tipo_object", nmTipoObject);
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

    public Timestamp getDtFirstSesErr() {
        return getTimestamp("dt_first_ses_err");
    }

    public void setDtFirstSesErr(Timestamp dtFirstSesErr) {
        setObject("dt_first_ses_err", dtFirstSesErr);
    }

    public Timestamp getDtLastSesErr() {
        return getTimestamp("dt_last_ses_err");
    }

    public void setDtLastSesErr(Timestamp dtLastSesErr) {
        setObject("dt_last_ses_err", dtLastSesErr);
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

    public String getFlVersSacerDaRecup() {
        return getString("fl_vers_sacer_da_recup");
    }

    public void setFlVersSacerDaRecup(String flVersSacerDaRecup) {
        setObject("fl_vers_sacer_da_recup", flVersSacerDaRecup);
    }

    @Override
    public void entityToRowBean(Object obj) {
        MonVLisObjNonVers entity = (MonVLisObjNonVers) obj;
        this.setIdAmbienteVers(entity.getIdAmbienteVers());
        this.setNmAmbienteVers(entity.getNmAmbienteVers());
        this.setIdVers(entity.getMonVLisObjNonVersId() == null ? null : entity.getMonVLisObjNonVersId().getIdVers());
        this.setNmVers(entity.getNmVers());
        this.setNmTipoObject(entity.getNmTipoObject());
        this.setCdKeyObject(
                entity.getMonVLisObjNonVersId() == null ? null : entity.getMonVLisObjNonVersId().getCdKeyObject());
        this.setDsObject(entity.getDsObject());
        if (entity.getDtFirstSesErr() != null) {
            this.setDtFirstSesErr(new Timestamp(entity.getDtFirstSesErr().getTime()));
        }
        if (entity.getDtLastSesErr() != null) {
            this.setDtLastSesErr(new Timestamp(entity.getDtLastSesErr().getTime()));
        }
        this.setFlVerif(entity.getFlVerif());
        this.setFlNonRisolub(entity.getFlNonRisolub());
        this.setFlVersSacerDaRecup(entity.getFlVersSacerDaRecup());
    }

    @Override
    public MonVLisObjNonVers rowBeanToEntity() {
        MonVLisObjNonVers entity = new MonVLisObjNonVers();
        entity.setIdAmbienteVers(this.getIdAmbienteVers());
        entity.setNmAmbienteVers(this.getNmAmbienteVers());
        entity.setMonVLisObjNonVersId(new MonVLisObjNonVersId());
        entity.getMonVLisObjNonVersId().setIdVers(this.getIdVers());
        entity.setNmVers(this.getNmVers());
        entity.setNmTipoObject(this.getNmTipoObject());
        entity.getMonVLisObjNonVersId().setCdKeyObject(this.getCdKeyObject());
        entity.setDsObject(this.getDsObject());
        entity.setDtFirstSesErr(this.getDtFirstSesErr());
        entity.setDtLastSesErr(this.getDtLastSesErr());
        entity.setFlVerif(this.getFlVerif());
        entity.setFlNonRisolub(this.getFlNonRisolub());
        entity.setFlVersSacerDaRecup(this.getFlVersSacerDaRecup());
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
