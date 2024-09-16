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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.id.enhanced.SequenceStyleGenerator;

/**
 * The persistent class for the PIG_VAL_SUB_PARTITION database table.
 *
 */
@Entity
@Table(name = "PIG_VAL_SUB_PARTITION")
public class PigValSubPartition implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long idValSubPartition;
    private String cdValSubPartition;
    private PigPartition pigPartition;
    private PigSubPartition pigSubPartition;

    public PigValSubPartition() {
        // hibernate
    }

    @Id
    @GenericGenerator(name = "PIG_VAL_SUB_PARTITION_IDVALSUBPARTITION_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
            @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SPIG_VAL_SUB_PARTITION"),
            @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PIG_VAL_SUB_PARTITION_IDVALSUBPARTITION_GENERATOR")
    @Column(name = "ID_VAL_SUB_PARTITION")
    public Long getIdValSubPartition() {
        return this.idValSubPartition;
    }

    public void setIdValSubPartition(Long idValSubPartition) {
        this.idValSubPartition = idValSubPartition;
    }

    @Column(name = "CD_VAL_SUB_PARTITION")
    public String getCdValSubPartition() {
        return this.cdValSubPartition;
    }

    public void setCdValSubPartition(String cdValSubPartition) {
        this.cdValSubPartition = cdValSubPartition;
    }

    // bi-directional many-to-one association to PigPartition
    @ManyToOne
    @JoinColumn(name = "ID_PARTITION")
    public PigPartition getPigPartition() {
        return this.pigPartition;
    }

    public void setPigPartition(PigPartition pigPartition) {
        this.pigPartition = pigPartition;
    }

    // bi-directional many-to-one association to PigSubPartition
    @ManyToOne
    @JoinColumn(name = "ID_SUB_PARTITION")
    public PigSubPartition getPigSubPartition() {
        return this.pigSubPartition;
    }

    public void setPigSubPartition(PigSubPartition pigSubPartition) {
        this.pigSubPartition = pigSubPartition;
    }

}
