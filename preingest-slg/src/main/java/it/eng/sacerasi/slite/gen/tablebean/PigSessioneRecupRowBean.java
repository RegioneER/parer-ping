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
import java.sql.Timestamp;

import it.eng.sacerasi.entity.PigObject;
import it.eng.sacerasi.entity.PigSessioneRecup;
import it.eng.sacerasi.entity.PigVers;
import it.eng.spagoLite.db.base.JEEBaseRowInterface;
import it.eng.spagoLite.db.base.row.BaseRow;
import it.eng.spagoLite.db.oracle.bean.column.TableDescriptor;

/**
 * RowBean per la tabella Pig_Sessione_Recup
 *
 */
public class PigSessioneRecupRowBean extends BaseRow implements JEEBaseRowInterface {

    private static final long serialVersionUID = 1L;

    /*
     * @Generated( value = "it.eg.dbtool.db.oracle.beangen.Oracle4JPAClientBeanGen$TableBeanWriter", comments =
     * "This class was generated by OraTool", date = "Monday, 17 March 2014 15:12" )
     */

    public static PigSessioneRecupTableDescriptor TABLE_DESCRIPTOR = new PigSessioneRecupTableDescriptor();

    public PigSessioneRecupRowBean() {
        super();
    }

    public TableDescriptor getTableDescriptor() {
        return TABLE_DESCRIPTOR;
    }

    // getter e setter
    public BigDecimal getIdSessioneRecup() {
        return getBigDecimal("id_sessione_recup");
    }

    public void setIdSessioneRecup(BigDecimal idSessioneRecup) {
        setObject("id_sessione_recup", idSessioneRecup);
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

    public BigDecimal getIdVers() {
        return getBigDecimal("id_vers");
    }

    public void setIdVers(BigDecimal idVers) {
        setObject("id_vers", idVers);
    }

    public BigDecimal getIdObject() {
        return getBigDecimal("id_object");
    }

    public void setIdObject(BigDecimal idObject) {
        setObject("id_object", idObject);
    }

    public Timestamp getDtApertura() {
        return getTimestamp("dt_apertura");
    }

    public void setDtApertura(Timestamp dtApertura) {
        setObject("dt_apertura", dtApertura);
    }

    public Timestamp getDtChiusura() {
        return getTimestamp("dt_chiusura");
    }

    public void setDtChiusura(Timestamp dtChiusura) {
        setObject("dt_chiusura", dtChiusura);
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

    @Override
    public void entityToRowBean(Object obj) {
        PigSessioneRecup entity = (PigSessioneRecup) obj;

        this.setIdSessioneRecup(new BigDecimal(entity.getIdSessioneRecup()));
        this.setNmAmbienteVers(entity.getNmAmbienteVers());
        this.setNmVers(entity.getNmVers());
        this.setCdKeyObject(entity.getCdKeyObject());
        if (entity.getPigVer() != null) {
            this.setIdVers(new BigDecimal(entity.getPigVer().getIdVers()));
        }

        if (entity.getPigObject() != null) {
            this.setIdObject(new BigDecimal(entity.getPigObject().getIdObject()));
        }

        if (entity.getDtApertura() != null) {
            this.setDtApertura(new Timestamp(entity.getDtApertura().getTime()));
        }
        if (entity.getDtChiusura() != null) {
            this.setDtChiusura(new Timestamp(entity.getDtChiusura().getTime()));
        }
        this.setTiStato(entity.getTiStato());
        this.setCdErr(entity.getCdErr());
        this.setDlErr(entity.getDlErr());
    }

    @Override
    public PigSessioneRecup rowBeanToEntity() {
        PigSessioneRecup entity = new PigSessioneRecup();
        if (this.getIdSessioneRecup() != null) {
            entity.setIdSessioneRecup(this.getIdSessioneRecup().longValue());
        }
        entity.setNmAmbienteVers(this.getNmAmbienteVers());
        entity.setNmVers(this.getNmVers());
        entity.setCdKeyObject(this.getCdKeyObject());
        if (this.getIdVers() != null) {
            if (entity.getPigVer() == null) {
                entity.setPigVer(new PigVers());
            }
            entity.getPigVer().setIdVers(this.getIdVers().longValue());
        }
        if (this.getIdObject() != null) {
            if (entity.getPigObject() == null) {
                entity.setPigObject(new PigObject());
            }
            entity.getPigObject().setIdObject(this.getIdObject().longValue());
        }
        entity.setDtApertura(this.getDtApertura());
        entity.setDtChiusura(this.getDtChiusura());
        entity.setTiStato(this.getTiStato());
        entity.setCdErr(this.getCdErr());
        entity.setDlErr(this.getDlErr());
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
