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

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.id.enhanced.SequenceStyleGenerator;

/**
 * The persistent class for the PIG_CONT_UNITA_DOC_SACER database table.
 *
 */
@Entity
@Table(name = "PIG_CONT_UNITA_DOC_SACER")
public class PigContUnitaDocSacer implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long idContUnitaDocSacer;
    private BigDecimal aaUnitaDocSacer;
    private BigDecimal pgContUnitaDocSacer;
    private PigTipoObject pigTipoObject;

    public PigContUnitaDocSacer() {
        // for Hibernate
    }

    @Id
    @GenericGenerator(name = "PIG_CONT_UNITA_DOC_SACER_IDCONTUNITADOCSACER_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
            @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SPIG_CONT_UNITA_DOC_SACER"),
            @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PIG_CONT_UNITA_DOC_SACER_IDCONTUNITADOCSACER_GENERATOR")
    @Column(name = "ID_CONT_UNITA_DOC_SACER")
    public Long getIdContUnitaDocSacer() {
        return this.idContUnitaDocSacer;
    }

    public void setIdContUnitaDocSacer(Long idContUnitaDocSacer) {
        this.idContUnitaDocSacer = idContUnitaDocSacer;
    }

    @Column(name = "AA_UNITA_DOC_SACER")
    public BigDecimal getAaUnitaDocSacer() {
        return this.aaUnitaDocSacer;
    }

    public void setAaUnitaDocSacer(BigDecimal aaUnitaDocSacer) {
        this.aaUnitaDocSacer = aaUnitaDocSacer;
    }

    @Column(name = "PG_CONT_UNITA_DOC_SACER")
    public BigDecimal getPgContUnitaDocSacer() {
        return this.pgContUnitaDocSacer;
    }

    public void setPgContUnitaDocSacer(BigDecimal pgContUnitaDocSacer) {
        this.pgContUnitaDocSacer = pgContUnitaDocSacer;
    }

    // bi-directional many-to-one association to PigTipoObject
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_TIPO_OBJECT")
    public PigTipoObject getPigTipoObject() {
        return this.pigTipoObject;
    }

    public void setPigTipoObject(PigTipoObject pigTipoObject) {
        this.pigTipoObject = pigTipoObject;
    }

}
