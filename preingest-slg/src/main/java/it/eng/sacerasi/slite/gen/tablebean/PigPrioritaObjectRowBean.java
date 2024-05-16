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

import it.eng.sacerasi.entity.PigObject;
import it.eng.sacerasi.entity.PigPrioritaObject;
import it.eng.spagoLite.db.base.JEEBaseRowInterface;
import it.eng.spagoLite.db.base.row.BaseRow;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class PigPrioritaObjectRowBean extends BaseRow implements JEEBaseRowInterface {

    private static final long serialVersionUID = 1L;

    public PigPrioritaObjectRowBean() {
        super();
    }

    public BigDecimal getIdStatoSessioneIngest() {
        return getBigDecimal("id_stato_sessione_ingest");
    }

    public void setIdStatoSessioneIngest(BigDecimal idStatoSessioneIngest) {
        setObject("id_stato_sessione_ingest", idStatoSessioneIngest);
    }

    public BigDecimal getIdObject() {
        return getBigDecimal("id_object");
    }

    public void setIdObject(BigDecimal idObject) {
        setObject("id_object", idObject);
    }

    public BigDecimal getIdPrioritaObject() {
        return getBigDecimal("id_priorita_object");
    }

    public void setIdPrioritaObject(BigDecimal idPrioritaObject) {
        setObject("id_priorita_object", idPrioritaObject);
    }

    public Timestamp getDtModifica() {
        return getTimestamp("dt_modifica");
    }

    public void setDtModifica(Timestamp dtModifica) {
        setObject("dt_modifica", dtModifica);
    }

    public String getNmUser() {
        return getString("nm_user");
    }

    public void setNmUser(String nmUser) {
        setObject("nm_user", nmUser);
    }

    public String getTiPrioritaVersamento() {
        return getString("ti_priorita_versamento");
    }

    public void setTiPrioritaVersamento(String tiPrioritaVersamento) {
        setObject("ti_priorita_versamento", tiPrioritaVersamento);
    }

    @Override
    public void entityToRowBean(Object obj) {
        PigPrioritaObject entity = (PigPrioritaObject) obj;
        this.setIdPrioritaObject(new BigDecimal(entity.getIdPrioritaObject()));
        this.setIdObject(new BigDecimal(entity.getPigObject().getIdObject()));
        this.setDtModifica(Timestamp.valueOf((entity.getDtModifica())));
        this.setNmUser(entity.getNmUser());
        this.setTiPrioritaVersamento(entity.getTiPrioritaVersamento());
    }

    @Override
    public PigPrioritaObject rowBeanToEntity() {
        PigPrioritaObject entity = new PigPrioritaObject();
        entity.setIdPrioritaObject(this.getIdPrioritaObject().longValue());

        if (this.getIdObject() != null) {
            entity.setPigObject(new PigObject());
            entity.getPigObject().setIdObject(this.getIdObject().longValue());
        }

        entity.setDtModifica(this.getDtModifica().toLocalDateTime());
        entity.setNmUser(this.getNmUser());
        entity.setTiPrioritaVersamento(this.getTiPrioritaVersamento());

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
