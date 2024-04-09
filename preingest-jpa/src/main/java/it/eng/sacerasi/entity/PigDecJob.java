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

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.id.enhanced.SequenceStyleGenerator;

import it.eng.parer.jboss.timer.common.JobTable;
import it.eng.parer.jboss.timer.common.entity.JobDefinition;

/**
 * The persistent class for the PIG_DEC_JOB database table.
 *
 */
/* PIG_DEC_JOB */
@Entity
@Table(name = "PIG_DEC_JOB")
public class PigDecJob extends JobDefinition implements JobTable {

    private static final long serialVersionUID = 1L;
    private Long idJob;
    private String nmJob;
    private String dsJob;
    private String tiSchedJob;
    private String tiScopoJob;
    private String nmAmbito;
    private BigDecimal niOrdExec;

    public PigDecJob() {
        // for Hibernate
    }

    @Id
    @GenericGenerator(name = "PIG_DEC_JOB_IDJOB_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
            @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SPIG_DEC_JOB"),
            @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PIG_DEC_JOB_IDJOB_GENERATOR")
    @Column(name = "ID_JOB")
    @Override
    public Long getIdJob() {
        return idJob;
    }

    @Override
    public void setIdJob(Long idJob) {
        this.idJob = idJob;
    }

    @Column(name = "NM_JOB")
    @Override
    public String getNmJob() {
        return nmJob;
    }

    @Override
    public void setNmJob(String nmJob) {
        this.nmJob = nmJob;
    }

    @Column(name = "DS_JOB")
    @Override
    public String getDsJob() {
        return dsJob;
    }

    @Override
    public void setDsJob(String dsJob) {
        this.dsJob = dsJob;
    }

    @Column(name = "TI_SCHED_JOB")
    @Override
    public String getTiSchedJob() {
        return tiSchedJob;
    }

    @Override
    public void setTiSchedJob(String tiSchedJob) {
        this.tiSchedJob = tiSchedJob;
    }

    @Column(name = "TI_SCOPO_JOB")
    @Override
    public String getTiScopoJob() {
        return tiScopoJob;
    }

    @Override
    public void setTiScopoJob(String tiScopoJob) {
        this.tiScopoJob = tiScopoJob;
    }

    @Column(name = "NM_AMBITO")
    @Override
    public String getNmAmbito() {
        return nmAmbito;
    }

    @Override
    public void setNmAmbito(String nmAmbito) {
        this.nmAmbito = nmAmbito;
    }

    @Column(name = "NI_ORD_EXEC")
    @Override
    public BigDecimal getNiOrdExec() {
        return niOrdExec;
    }

    @Override
    public void setNiOrdExec(BigDecimal niOrdExec) {
        this.niOrdExec = niOrdExec;
    }

}
