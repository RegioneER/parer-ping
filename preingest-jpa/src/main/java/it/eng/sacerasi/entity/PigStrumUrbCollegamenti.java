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
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.id.enhanced.SequenceStyleGenerator;

/**
 * The persistent class for the PIG_STRUM_URB_COLLEGAMENTI database table.
 *
 */
@Entity
@Table(name = "PIG_STRUM_URB_COLLEGAMENTI")
@NamedQuery(name = "PigStrumUrbCollegamenti.findAll", query = "SELECT p FROM PigStrumUrbCollegamenti p")
public class PigStrumUrbCollegamenti implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long idStrumUrbCollegamenti;
    private BigDecimal anno;
    private String numero;
    private PigStrumentiUrbanistici pigStrumentiUrbanistici;
    private PigStrumUrbPianoStato pigStrumUrbPianoStato;

    public PigStrumUrbCollegamenti() {
        // for Hibernate
    }

    @Id
    @GenericGenerator(name = "PIG_STRUM_URB_COLLEGAMENTI_IDSTRUMURBCOLLEGAMENTI_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
            @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SPIG_STRUM_URB_COLLEGAMENTI"),
            @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PIG_STRUM_URB_COLLEGAMENTI_IDSTRUMURBCOLLEGAMENTI_GENERATOR")
    @Column(name = "ID_STRUM_URB_COLLEGAMENTI")
    public Long getIdStrumUrbCollegamenti() {
        return this.idStrumUrbCollegamenti;
    }

    public void setIdStrumUrbCollegamenti(Long idStrumUrbCollegamenti) {
        this.idStrumUrbCollegamenti = idStrumUrbCollegamenti;
    }

    public BigDecimal getAnno() {
        return this.anno;
    }

    public void setAnno(BigDecimal anno) {
        this.anno = anno;
    }

    public String getNumero() {
        return this.numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    // bi-directional many-to-one association to PigStrumentiUrbanistici
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_STRUMENTI_URBANISTICI")
    public PigStrumentiUrbanistici getPigStrumentiUrbanistici() {
        return this.pigStrumentiUrbanistici;
    }

    public void setPigStrumentiUrbanistici(PigStrumentiUrbanistici pigStrumentiUrbanistici) {
        this.pigStrumentiUrbanistici = pigStrumentiUrbanistici;
    }

    // bi-directional many-to-one association to PigStrumUrbPianoStato
    @ManyToOne
    @JoinColumn(name = "ID_STRUM_URB_PIANO_STATO")
    public PigStrumUrbPianoStato getPigStrumUrbPianoStato() {
        return this.pigStrumUrbPianoStato;
    }

    public void setPigStrumUrbPianoStato(PigStrumUrbPianoStato pigStrumUrbPianoStato) {
        this.pigStrumUrbPianoStato = pigStrumUrbPianoStato;
    }
}
