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

package it.eng.sacerasi.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * The persistent class for the PIG_DEC_JOB_FOTO database table.
 * 
 */
@Entity
@Table(name = "PIG_DEC_JOB_FOTO")
@NamedQuery(name = "PigDecJobFoto.findAll", query = "SELECT p FROM PigDecJobFoto p")
public class PigDecJobFoto implements Serializable {
    private static final long serialVersionUID = 1L;
    private long idJobFoto;
    private String cdSchedDayofmonthFoto;
    private String cdSchedDayofweekFoto;
    private String cdSchedHourFoto;
    private String cdSchedMinuteFoto;
    private String cdSchedMonthFoto;
    private String dsJobFoto;
    private Date dtJobFoto;
    private Date dtProssimaAttivazioneFoto;
    private String flDataAccurataFoto;
    private BigDecimal niOrdExecFoto;
    private String nmAmbitoFoto;
    private String nmJobFoto;
    private String nmNodoAssegnatoFoto;
    private String tiSchedJobFoto;
    private String tiScopoJobFoto;
    private String tiStatoTimerFoto;

    public PigDecJobFoto() {
        // placeholder
    }

    @Id
    // @SequenceGenerator(name="PIG_DEC_JOB_FOTO_IDJOBFOTO_GENERATOR", sequenceName="$STABLE")
    // @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="PIG_DEC_JOB_FOTO_IDJOBFOTO_GENERATOR")
    @Column(name = "ID_JOB_FOTO")
    public long getIdJobFoto() {
        return this.idJobFoto;
    }

    public void setIdJobFoto(long idJobFoto) {
        this.idJobFoto = idJobFoto;
    }

    @Column(name = "CD_SCHED_DAYOFMONTH_FOTO")
    public String getCdSchedDayofmonthFoto() {
        return this.cdSchedDayofmonthFoto;
    }

    public void setCdSchedDayofmonthFoto(String cdSchedDayofmonthFoto) {
        this.cdSchedDayofmonthFoto = cdSchedDayofmonthFoto;
    }

    @Column(name = "CD_SCHED_DAYOFWEEK_FOTO")
    public String getCdSchedDayofweekFoto() {
        return this.cdSchedDayofweekFoto;
    }

    public void setCdSchedDayofweekFoto(String cdSchedDayofweekFoto) {
        this.cdSchedDayofweekFoto = cdSchedDayofweekFoto;
    }

    @Column(name = "CD_SCHED_HOUR_FOTO")
    public String getCdSchedHourFoto() {
        return this.cdSchedHourFoto;
    }

    public void setCdSchedHourFoto(String cdSchedHourFoto) {
        this.cdSchedHourFoto = cdSchedHourFoto;
    }

    @Column(name = "CD_SCHED_MINUTE_FOTO")
    public String getCdSchedMinuteFoto() {
        return this.cdSchedMinuteFoto;
    }

    public void setCdSchedMinuteFoto(String cdSchedMinuteFoto) {
        this.cdSchedMinuteFoto = cdSchedMinuteFoto;
    }

    @Column(name = "CD_SCHED_MONTH_FOTO")
    public String getCdSchedMonthFoto() {
        return this.cdSchedMonthFoto;
    }

    public void setCdSchedMonthFoto(String cdSchedMonthFoto) {
        this.cdSchedMonthFoto = cdSchedMonthFoto;
    }

    @Column(name = "DS_JOB_FOTO")
    public String getDsJobFoto() {
        return this.dsJobFoto;
    }

    public void setDsJobFoto(String dsJobFoto) {
        this.dsJobFoto = dsJobFoto;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_JOB_FOTO")
    public Date getDtJobFoto() {
        return this.dtJobFoto;
    }

    public void setDtJobFoto(Date dtJobFoto) {
        this.dtJobFoto = dtJobFoto;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_PROSSIMA_ATTIVAZIONE_FOTO")
    public Date getDtProssimaAttivazioneFoto() {
        return this.dtProssimaAttivazioneFoto;
    }

    public void setDtProssimaAttivazioneFoto(Date dtProssimaAttivazioneFoto) {
        this.dtProssimaAttivazioneFoto = dtProssimaAttivazioneFoto;
    }

    @Column(name = "FL_DATA_ACCURATA_FOTO")
    public String getFlDataAccurataFoto() {
        return this.flDataAccurataFoto;
    }

    public void setFlDataAccurataFoto(String flDataAccurataFoto) {
        this.flDataAccurataFoto = flDataAccurataFoto;
    }

    @Column(name = "NI_ORD_EXEC_FOTO")
    public BigDecimal getNiOrdExecFoto() {
        return this.niOrdExecFoto;
    }

    public void setNiOrdExecFoto(BigDecimal niOrdExecFoto) {
        this.niOrdExecFoto = niOrdExecFoto;
    }

    @Column(name = "NM_AMBITO_FOTO")
    public String getNmAmbitoFoto() {
        return this.nmAmbitoFoto;
    }

    public void setNmAmbitoFoto(String nmAmbitoFoto) {
        this.nmAmbitoFoto = nmAmbitoFoto;
    }

    @Column(name = "NM_JOB_FOTO")
    public String getNmJobFoto() {
        return this.nmJobFoto;
    }

    public void setNmJobFoto(String nmJobFoto) {
        this.nmJobFoto = nmJobFoto;
    }

    @Column(name = "NM_NODO_ASSEGNATO_FOTO")
    public String getNmNodoAssegnatoFoto() {
        return this.nmNodoAssegnatoFoto;
    }

    public void setNmNodoAssegnatoFoto(String nmNodoAssegnatoFoto) {
        this.nmNodoAssegnatoFoto = nmNodoAssegnatoFoto;
    }

    @Column(name = "TI_SCHED_JOB_FOTO")
    public String getTiSchedJobFoto() {
        return this.tiSchedJobFoto;
    }

    public void setTiSchedJobFoto(String tiSchedJobFoto) {
        this.tiSchedJobFoto = tiSchedJobFoto;
    }

    @Column(name = "TI_SCOPO_JOB_FOTO")
    public String getTiScopoJobFoto() {
        return this.tiScopoJobFoto;
    }

    public void setTiScopoJobFoto(String tiScopoJobFoto) {
        this.tiScopoJobFoto = tiScopoJobFoto;
    }

    @Column(name = "TI_STATO_TIMER_FOTO")
    public String getTiStatoTimerFoto() {
        return this.tiStatoTimerFoto;
    }

    public void setTiStatoTimerFoto(String tiStatoTimerFoto) {
        this.tiStatoTimerFoto = tiStatoTimerFoto;
    }

}
