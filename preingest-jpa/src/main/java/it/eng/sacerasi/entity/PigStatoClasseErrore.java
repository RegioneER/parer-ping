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
 * The persistent class for the PIG_STATO_CLASSE_ERRORE database table.
 *
 */
@Entity
@Table(name = "PIG_STATO_CLASSE_ERRORE")
public class PigStatoClasseErrore implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long idStatoClasseErrore;
    private PigClasseErrore pigClasseErrore;
    private PigStatoObject pigStatoObject;

    public PigStatoClasseErrore() {
	// for Hibernate
    }

    @Id
    @GenericGenerator(name = "PIG_STATO_CLASSE_ERRORE_IDSTATOCLASSEERRORE_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
	    @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SPIG_STATO_CLASSE_ERRORE"),
	    @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PIG_STATO_CLASSE_ERRORE_IDSTATOCLASSEERRORE_GENERATOR")
    @Column(name = "ID_STATO_CLASSE_ERRORE")
    public Long getIdStatoClasseErrore() {
	return this.idStatoClasseErrore;
    }

    public void setIdStatoClasseErrore(Long idStatoClasseErrore) {
	this.idStatoClasseErrore = idStatoClasseErrore;
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

    // bi-directional many-to-one association to PigStatoObject
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_STATO_OBJECT")
    public PigStatoObject getPigStatoObject() {
	return this.pigStatoObject;
    }

    public void setPigStatoObject(PigStatoObject pigStatoObject) {
	this.pigStatoObject = pigStatoObject;
    }

}
