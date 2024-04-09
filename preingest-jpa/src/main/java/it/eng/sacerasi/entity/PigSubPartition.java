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
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.id.enhanced.SequenceStyleGenerator;

/**
 * The persistent class for the PIG_SUB_PARTITION database table.
 * 
 */
@Entity
@Table(name = "PIG_SUB_PARTITION")
public class PigSubPartition implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long idSubPartition;
    private String cdSubPartition;
    private PigPartition pigPartition;
    private List<PigValSubPartition> pigValSubPartitions = new ArrayList<>();

    public PigSubPartition() {
        // for Hibernate
    }

    @Id
    @GenericGenerator(name = "PIG_SUB_PARTITION_IDSUBPARTITION_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
            @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SPIG_SUB_PARTITION"),
            @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PIG_SUB_PARTITION_IDSUBPARTITION_GENERATOR")
    @Column(name = "ID_SUB_PARTITION")
    public Long getIdSubPartition() {
        return this.idSubPartition;
    }

    public void setIdSubPartition(Long idSubPartition) {
        this.idSubPartition = idSubPartition;
    }

    @Column(name = "CD_SUB_PARTITION")
    public String getCdSubPartition() {
        return this.cdSubPartition;
    }

    public void setCdSubPartition(String cdSubPartition) {
        this.cdSubPartition = cdSubPartition;
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

    // bi-directional many-to-one association to PigValSubPartition
    @OneToMany(mappedBy = "pigSubPartition")
    public List<PigValSubPartition> getPigValSubPartitions() {
        return this.pigValSubPartitions;
    }

    public void setPigValSubPartitions(List<PigValSubPartition> pigValSubPartitions) {
        this.pigValSubPartitions = pigValSubPartitions;
    }

}
