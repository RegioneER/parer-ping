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
import java.math.BigDecimal;
import javax.persistence.CascadeType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.id.enhanced.SequenceStyleGenerator;

/**
 * The persistent class for the PIG_FILE_OBJECT database table.
 *
 */
@Entity
@Table(name = "PIG_FILE_OBJECT")
public class PigFileObject implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long idFileObject;
    private String cdEncodingHashFileVers;
    private String dsHashFileVers;
    private BigDecimal niSizeFileVers;
    private String nmFileObject;
    private String tiAlgoHashFileVers;
    private PigObject pigObject;
    private PigTipoFileObject pigTipoFileObject;
    private PigFileObjectStorage pigFileObjectStorage;
    private Long idVers;

    public PigFileObject() {
	// for Hibernate
    }

    @Id
    @GenericGenerator(name = "PIG_FILE_OBJECT_IDFILEOBJECT_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
	    @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SPIG_FILE_OBJECT"),
	    @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PIG_FILE_OBJECT_IDFILEOBJECT_GENERATOR")
    @Column(name = "ID_FILE_OBJECT")
    public Long getIdFileObject() {
	return this.idFileObject;
    }

    public void setIdFileObject(Long idFileObject) {
	this.idFileObject = idFileObject;
    }

    @Column(name = "CD_ENCODING_HASH_FILE_VERS")
    public String getCdEncodingHashFileVers() {
	return this.cdEncodingHashFileVers;
    }

    public void setCdEncodingHashFileVers(String cdEncodingHashFileVers) {
	this.cdEncodingHashFileVers = cdEncodingHashFileVers;
    }

    @Column(name = "DS_HASH_FILE_VERS")
    public String getDsHashFileVers() {
	return this.dsHashFileVers;
    }

    public void setDsHashFileVers(String dsHashFileVers) {
	this.dsHashFileVers = dsHashFileVers;
    }

    @Column(name = "NI_SIZE_FILE_VERS")
    public BigDecimal getNiSizeFileVers() {
	return this.niSizeFileVers;
    }

    public void setNiSizeFileVers(BigDecimal niSizeFileVers) {
	this.niSizeFileVers = niSizeFileVers;
    }

    @Column(name = "NM_FILE_OBJECT")
    public String getNmFileObject() {
	return this.nmFileObject;
    }

    public void setNmFileObject(String nmFileObject) {
	this.nmFileObject = nmFileObject;
    }

    @Column(name = "TI_ALGO_HASH_FILE_VERS")
    public String getTiAlgoHashFileVers() {
	return this.tiAlgoHashFileVers;
    }

    public void setTiAlgoHashFileVers(String tiAlgoHashFileVers) {
	this.tiAlgoHashFileVers = tiAlgoHashFileVers;
    }

    // bi-directional many-to-one association to PigObject
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_OBJECT")
    public PigObject getPigObject() {
	return this.pigObject;
    }

    public void setPigObject(PigObject pigObject) {
	this.pigObject = pigObject;
    }

    // bi-directional many-to-one association to PigTipoFileObject
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_TIPO_FILE_OBJECT")
    public PigTipoFileObject getPigTipoFileObject() {
	return this.pigTipoFileObject;
    }

    public void setPigTipoFileObject(PigTipoFileObject pigTipoFileObject) {
	this.pigTipoFileObject = pigTipoFileObject;
    }

    // usata solo come chiave di partizionamento, non voglio la join con PigVers
    @Column(name = "ID_VERS")
    public Long getIdVers() {
	return this.idVers;
    }

    public void setIdVers(Long idVers) {
	this.idVers = idVers;
    }

    @OneToOne(mappedBy = "pigFileObject", cascade = {
	    CascadeType.PERSIST })
    public PigFileObjectStorage getPigFileObjectStorage() {
	return pigFileObjectStorage;
    }

    public void setPigFileObjectStorage(PigFileObjectStorage pigFileObjectStorage) {
	this.pigFileObjectStorage = pigFileObjectStorage;
    }

}
