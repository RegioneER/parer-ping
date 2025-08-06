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
import javax.persistence.FetchType;
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
 * The persistent class for the PIG_ERRORE database table.
 *
 */
@Entity
@Table(name = "PIG_ERRORE")
public class PigErrore implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long idErrore;
    private String cdErrore;
    private String dsErrore;
    private String dsErroreFiltro;
    private PigClasseErrore pigClasseErrore;

    public PigErrore() {
	// for Hibernate
    }

    @Id
    @GenericGenerator(name = "PIG_ERRORE_IDERRORE_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
	    @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SPIG_ERRORE"),
	    @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PIG_ERRORE_IDERRORE_GENERATOR")
    @Column(name = "ID_ERRORE")
    public Long getIdErrore() {
	return this.idErrore;
    }

    public void setIdErrore(Long idErrore) {
	this.idErrore = idErrore;
    }

    @Column(name = "CD_ERRORE")
    public String getCdErrore() {
	return this.cdErrore;
    }

    public void setCdErrore(String cdErrore) {
	this.cdErrore = cdErrore;
    }

    @Column(name = "DS_ERRORE")
    public String getDsErrore() {
	return this.dsErrore;
    }

    public void setDsErrore(String dsErrore) {
	this.dsErrore = dsErrore;
    }

    @Column(name = "DS_ERRORE_FILTRO")
    public String getDsErroreFiltro() {
	return this.dsErroreFiltro;
    }

    public void setDsErroreFiltro(String dsErroreFiltro) {
	this.dsErroreFiltro = dsErroreFiltro;
    }

    // bi-directional many-to-one association to PigClasseErrore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_CLASSE_ERRORE")
    public PigClasseErrore getPigClasseErrore() {
	return this.pigClasseErrore;
    }

    public void setPigClasseErrore(PigClasseErrore pigClasseErrore) {
	this.pigClasseErrore = pigClasseErrore;
    }

}
