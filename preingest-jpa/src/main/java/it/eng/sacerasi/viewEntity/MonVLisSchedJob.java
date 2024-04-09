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

package it.eng.sacerasi.viewEntity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * The persistent class for the MON_V_LIS_SCHED_JOB database table.
 * 
 */
@Entity
@Table(name = "MON_V_LIS_SCHED_JOB")
public class MonVLisSchedJob implements Serializable {
    private static final long serialVersionUID = 1L;
    private String dlMsgErr;
    private Date dtRegLogJobFine;
    private Date dtRegLogJobIni;
    private BigDecimal durataGg;
    private BigDecimal durataMin;
    private BigDecimal durataOre;
    private BigDecimal durataSec;
    private BigDecimal idLogJob;
    private String nmJob;

    public MonVLisSchedJob() {
    }

    @Column(name = "DL_MSG_ERR")
    public String getDlMsgErr() {
        return this.dlMsgErr;
    }

    public void setDlMsgErr(String dlMsgErr) {
        this.dlMsgErr = dlMsgErr;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_REG_LOG_JOB_FINE")
    public Date getDtRegLogJobFine() {
        return this.dtRegLogJobFine;
    }

    public void setDtRegLogJobFine(Date dtRegLogJobFine) {
        this.dtRegLogJobFine = dtRegLogJobFine;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_REG_LOG_JOB_INI")
    public Date getDtRegLogJobIni() {
        return this.dtRegLogJobIni;
    }

    public void setDtRegLogJobIni(Date dtRegLogJobIni) {
        this.dtRegLogJobIni = dtRegLogJobIni;
    }

    @Column(name = "DURATA_GG")
    public BigDecimal getDurataGg() {
        return this.durataGg;
    }

    public void setDurataGg(BigDecimal durataGg) {
        this.durataGg = durataGg;
    }

    @Column(name = "DURATA_MIN")
    public BigDecimal getDurataMin() {
        return this.durataMin;
    }

    public void setDurataMin(BigDecimal durataMin) {
        this.durataMin = durataMin;
    }

    @Column(name = "DURATA_ORE")
    public BigDecimal getDurataOre() {
        return this.durataOre;
    }

    public void setDurataOre(BigDecimal durataOre) {
        this.durataOre = durataOre;
    }

    @Column(name = "DURATA_SEC")
    public BigDecimal getDurataSec() {
        return this.durataSec;
    }

    public void setDurataSec(BigDecimal durataSec) {
        this.durataSec = durataSec;
    }

    @Id
    @Column(name = "ID_LOG_JOB")
    public BigDecimal getIdLogJob() {
        return this.idLogJob;
    }

    public void setIdLogJob(BigDecimal idLogJob) {
        this.idLogJob = idLogJob;
    }

    @Column(name = "NM_JOB")
    public String getNmJob() {
        return this.nmJob;
    }

    public void setNmJob(String nmJob) {
        this.nmJob = nmJob;
    }

}
