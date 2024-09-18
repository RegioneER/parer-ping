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
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.id.enhanced.SequenceStyleGenerator;

/**
 * The persistent class for the PIG_STATO_SESSIONE_INGEST database table.
 *
 */
@Entity
@Table(name = "PIG_STATO_SESSIONE_INGEST")
public class PigStatoSessioneIngest implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long idStatoSessioneIngest;
    private String tiStato;
    private Date tsRegStato;
    private PigSessioneIngest pigSessioneIngest;
    private Long idVers;

    public PigStatoSessioneIngest() {
        // non usato
    }

    @Id
    @GenericGenerator(name = "PIG_STATO_SESSIONE_INGEST_IDSTATOSESSIONEINGEST_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
            @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SPIG_STATO_SESSIONE_INGEST"),
            @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PIG_STATO_SESSIONE_INGEST_IDSTATOSESSIONEINGEST_GENERATOR")
    @Column(name = "ID_STATO_SESSIONE_INGEST")
    public Long getIdStatoSessioneIngest() {
        return this.idStatoSessioneIngest;
    }

    public void setIdStatoSessioneIngest(Long idStatoSessioneIngest) {
        this.idStatoSessioneIngest = idStatoSessioneIngest;
    }

    @Column(name = "TI_STATO")
    public String getTiStato() {
        return this.tiStato;
    }

    public void setTiStato(String tiStato) {
        this.tiStato = tiStato;
    }

    @Column(name = "TS_REG_STATO")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getTsRegStato() {
        return this.tsRegStato;
    }

    public void setTsRegStato(Date tsRegStato) {
        this.tsRegStato = tsRegStato;
    }

    // bi-directional many-to-one association to PigSessioneIngest
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_SESSIONE_INGEST")
    public PigSessioneIngest getPigSessioneIngest() {
        return this.pigSessioneIngest;
    }

    public void setPigSessioneIngest(PigSessioneIngest pigSessioneIngest) {
        this.pigSessioneIngest = pigSessioneIngest;
    }

    // usata solo come chiave di partizionamento, non voglio la join con PigVers
    @Column(name = "ID_VERS")
    public Long getIdVers() {
        return this.idVers;
    }

    public void setIdVers(Long idVers) {
        this.idVers = idVers;
    }

}
