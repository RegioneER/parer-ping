/*
 * Engineering Ingegneria Informatica S.p.A.
 *
 * Copyright (C) 2023 Regione Emilia-Romagna <p/> This program is free software: you can
 * redistribute it and/or modify it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version. <p/> This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Affero General Public License for more details. <p/> You should
 * have received a copy of the GNU Affero General Public License along with this program. If not,
 * see <https://www.gnu.org/licenses/>.
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
 * The persistent class for the PIG_PARTITION_VERS database table.
 *
 */
@Entity
@Table(name = "PIG_PARTITION_VERS")
public class PigPartitionVers implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long idPartitionVers;
    private String tiPartition;
    private PigPartition pigPartition;
    private PigVers pigVers;

    public PigPartitionVers() {
	// for Hibernate
    }

    @Id
    @GenericGenerator(name = "PIG_PARTITION_VERS_IDPARTITIONVERS_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
	    @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SPIG_PARTITION_VERS"),
	    @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PIG_PARTITION_VERS_IDPARTITIONVERS_GENERATOR")
    @Column(name = "ID_PARTITION_VERS")
    public Long getIdPartitionVers() {
	return this.idPartitionVers;
    }

    public void setIdPartitionVers(Long idPartitionVers) {
	this.idPartitionVers = idPartitionVers;
    }

    @Column(name = "TI_PARTITION")
    public String getTiPartition() {
	return this.tiPartition;
    }

    public void setTiPartition(String tiPartition) {
	this.tiPartition = tiPartition;
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

    // bi-directional many-to-one association to PigVer
    @ManyToOne
    @JoinColumn(name = "ID_VERS")
    public PigVers getPigVers() {
	return this.pigVers;
    }

    public void setPigVers(PigVers pigVers) {
	this.pigVers = pigVers;
    }

}
