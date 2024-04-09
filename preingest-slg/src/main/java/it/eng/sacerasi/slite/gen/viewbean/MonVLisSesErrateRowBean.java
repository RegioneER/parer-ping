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

import it.eng.sacerasi.viewEntity.MonVLisSesErrate;
import it.eng.spagoLite.db.base.JEEBaseRowInterface;
import it.eng.spagoLite.db.base.row.BaseRow;
import it.eng.spagoLite.db.oracle.bean.column.TableDescriptor;

/**
 * RowBean per la tabella Mon_V_Lis_Ses_Errate
 *
 */
public class MonVLisSesErrateRowBean extends BaseRow implements JEEBaseRowInterface {

    private static final long serialVersionUID = 1L;

    /*
     * @Generated( value = "it.eg.dbtool.db.oracle.beangen.Oracle4JPAClientBeanGen$ViewBeanWriter", comments =
     * "This class was generated by OraTool", date = "Monday, 17 March 2014 15:12" )
     */

    public static MonVLisSesErrateTableDescriptor TABLE_DESCRIPTOR = new MonVLisSesErrateTableDescriptor();

    public MonVLisSesErrateRowBean() {
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

    public String getNmAmbienteVers() {
        return getString("nm_ambiente_vers");
    }

    public void setNmAmbienteVers(String nmAmbienteVers) {
        setObject("nm_ambiente_vers", nmAmbienteVers);
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

    public String getNmTipoObject() {
        return getString("nm_tipo_object");
    }

    public void setNmTipoObject(String nmTipoObject) {
        setObject("nm_tipo_object", nmTipoObject);
    }

    public String getBlXml() {
        return getString("bl_xml");
    }

    public void setBlXml(String blXml) {
        setObject("bl_xml", blXml);
    }

    @Override
    public void entityToRowBean(Object obj) {
        MonVLisSesErrate entity = (MonVLisSesErrate) obj;
        this.setIdSessioneIngest(entity.getIdSessioneIngest());
        if (entity.getDtApertura() != null) {
            this.setDtApertura(new Timestamp(entity.getDtApertura().getTime()));
        }
        this.setNmAmbienteVers(entity.getNmAmbienteVers());
        this.setNmVers(entity.getNmVers());
        this.setCdKeyObject(entity.getCdKeyObject());
        this.setTiStato(entity.getTiStato());
        this.setCdErr(entity.getCdErr());
        this.setDlErr(entity.getDlErr());
        this.setFlVerif(entity.getFlVerif());
        // this.setNmTipoObject(entity.getNmTipoObject());
        this.setBlXml(entity.getBlXml());
    }

    @Override
    public MonVLisSesErrate rowBeanToEntity() {
        MonVLisSesErrate entity = new MonVLisSesErrate();
        entity.setIdSessioneIngest(this.getIdSessioneIngest());
        entity.setDtApertura(this.getDtApertura());
        entity.setNmAmbienteVers(this.getNmAmbienteVers());
        entity.setNmVers(this.getNmVers());
        entity.setCdKeyObject(this.getCdKeyObject());
        entity.setTiStato(this.getTiStato());
        entity.setCdErr(this.getCdErr());
        entity.setDlErr(this.getDlErr());
        entity.setFlVerif(this.getFlVerif());
        // entity.setNmTipoObject(this.getNmTipoObject());
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
