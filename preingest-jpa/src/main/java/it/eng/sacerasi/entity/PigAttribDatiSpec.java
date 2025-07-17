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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.eclipse.persistence.oxm.annotations.XmlInverseReference;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.id.enhanced.SequenceStyleGenerator;

/**
 * The persistent class for the PIG_ATTRIB_DATI_SPEC database table.
 *
 */
@Entity
@Table(name = "PIG_ATTRIB_DATI_SPEC")
public class PigAttribDatiSpec implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long idAttribDatiSpec;
    private String cdDatatypeXsd;
    private String flFiltroDiario;
    private String flVersSacer;
    private BigDecimal niOrd;
    private String nmAttribDatiSpec;
    private String nmColDatiSpec;
    private String tiDatatypeCol;
    private PigXsdDatiSpec pigXsdDatiSpec;

    public PigAttribDatiSpec() {
	// for Hibernate
    }

    @Id
    @GenericGenerator(name = "PIG_ATTRIB_DATI_SPEC_IDATTRIBDATISPEC_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
	    @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SPIG_ATTRIB_DATI_SPEC"),
	    @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PIG_ATTRIB_DATI_SPEC_IDATTRIBDATISPEC_GENERATOR")
    @Column(name = "ID_ATTRIB_DATI_SPEC")
    public Long getIdAttribDatiSpec() {
	return this.idAttribDatiSpec;
    }

    public void setIdAttribDatiSpec(Long idAttribDatiSpec) {
	this.idAttribDatiSpec = idAttribDatiSpec;
    }

    @Column(name = "CD_DATATYPE_XSD")
    public String getCdDatatypeXsd() {
	return this.cdDatatypeXsd;
    }

    public void setCdDatatypeXsd(String cdDatatypeXsd) {
	this.cdDatatypeXsd = cdDatatypeXsd;
    }

    @Column(name = "FL_FILTRO_DIARIO", columnDefinition = "char")
    public String getFlFiltroDiario() {
	return this.flFiltroDiario;
    }

    public void setFlFiltroDiario(String flFiltroDiario) {
	this.flFiltroDiario = flFiltroDiario;
    }

    @Column(name = "FL_VERS_SACER", columnDefinition = "char")
    public String getFlVersSacer() {
	return this.flVersSacer;
    }

    public void setFlVersSacer(String flVersSacer) {
	this.flVersSacer = flVersSacer;
    }

    @Column(name = "NI_ORD")
    public BigDecimal getNiOrd() {
	return this.niOrd;
    }

    public void setNiOrd(BigDecimal niOrd) {
	this.niOrd = niOrd;
    }

    @Column(name = "NM_ATTRIB_DATI_SPEC")
    public String getNmAttribDatiSpec() {
	return this.nmAttribDatiSpec;
    }

    public void setNmAttribDatiSpec(String nmAttribDatiSpec) {
	this.nmAttribDatiSpec = nmAttribDatiSpec;
    }

    @Column(name = "NM_COL_DATI_SPEC")
    public String getNmColDatiSpec() {
	return this.nmColDatiSpec;
    }

    public void setNmColDatiSpec(String nmColDatiSpec) {
	this.nmColDatiSpec = nmColDatiSpec;
    }

    @Column(name = "TI_DATATYPE_COL")
    public String getTiDatatypeCol() {
	return this.tiDatatypeCol;
    }

    public void setTiDatatypeCol(String tiDatatypeCol) {
	this.tiDatatypeCol = tiDatatypeCol;
    }

    // bi-directional many-to-one association to PigXsdDatiSpec
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_XSD_SPEC")
    @XmlInverseReference(mappedBy = "pigAttribDatiSpecs")
    public PigXsdDatiSpec getPigXsdDatiSpec() {
	return this.pigXsdDatiSpec;
    }

    public void setPigXsdDatiSpec(PigXsdDatiSpec pigXsdDatiSpec) {
	this.pigXsdDatiSpec = pigXsdDatiSpec;
    }

}
