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
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * The persistent class for the MON_V_VIS_LAST_SCHED_JOB database table.
 *
 */
@Entity
@Table(name = "MON_V_VIS_LAST_SCHED_JOB")
@NamedQuery(name = "MonVVisLastSchedJob.findUltimaAttivazioneByJob", query = "SELECT v FROM MonVVisLastSchedJob v WHERE v.nmJob=:nmJob")
public class MonVVisLastSchedJob implements Serializable {
    private static final long serialVersionUID = 1L;
    private Date dtRegLogJobIni;
    private String flJobAttivo;
    private BigDecimal idLogJob;
    private String lastExecOk;
    private String nmJob;

    public MonVVisLastSchedJob() {
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_REG_LOG_JOB_INI")
    public Date getDtRegLogJobIni() {
        return this.dtRegLogJobIni;
    }

    public void setDtRegLogJobIni(Date dtRegLogJobIni) {
        this.dtRegLogJobIni = dtRegLogJobIni;
    }

    @Column(name = "FL_JOB_ATTIVO", columnDefinition = "char")
    public String getFlJobAttivo() {
        return this.flJobAttivo;
    }

    public void setFlJobAttivo(String flJobAttivo) {
        this.flJobAttivo = flJobAttivo;
    }

    @Id
    @Column(name = "ID_LOG_JOB")
    public BigDecimal getIdLogJob() {
        return this.idLogJob;
    }

    public void setIdLogJob(BigDecimal idLogJob) {
        this.idLogJob = idLogJob;
    }

    @Column(name = "LAST_EXEC_OK", columnDefinition = "char")
    public String getLastExecOk() {
        return this.lastExecOk;
    }

    public void setLastExecOk(String lastExecOk) {
        this.lastExecOk = lastExecOk;
    }

    @Column(name = "NM_JOB")
    public String getNmJob() {
        return this.nmJob;
    }

    public void setNmJob(String nmJob) {
        this.nmJob = nmJob;
    }

}
