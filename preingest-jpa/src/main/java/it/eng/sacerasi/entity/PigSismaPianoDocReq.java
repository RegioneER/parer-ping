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
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * The persistent class for the PIG_SISMA_PIANO_DOC_REQ database table.
 * 
 */
@Entity
@Table(name = "PIG_SISMA_PIANO_DOC_REQ")
@NamedQuery(name = "PigSismaPianoDocReq.findAll", query = "SELECT p FROM PigSismaPianoDocReq p")
public class PigSismaPianoDocReq implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long idSismaPianoDocReq;
    private String flDocObbligatorio;
    private PigSismaValDoc pigSismaValDoc;

    private PigSismaFaseProgetto pigSismaFaseProgetto;

    public PigSismaPianoDocReq() {
        // hibernate
    }

    @Id
    @SequenceGenerator(name = "PIG_SISMA_PIANO_DOC_REQ_IDSISMAPIANODOCREQ_GENERATOR", sequenceName = "SPIG_SISMA_PIANO_DOC_REQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PIG_SISMA_PIANO_DOC_REQ_IDSISMAPIANODOCREQ_GENERATOR")
    @Column(name = "ID_SISMA_PIANO_DOC_REQ")
    public Long getIdSismaPianoDocReq() {
        return this.idSismaPianoDocReq;
    }

    public void setIdSismaPianoDocReq(Long idSismaPianoDocReq) {
        this.idSismaPianoDocReq = idSismaPianoDocReq;
    }

    @Column(name = "FL_DOC_OBBLIGATORIO", columnDefinition = "CHAR")
    public String getFlDocObbligatorio() {
        return this.flDocObbligatorio;
    }

    public void setFlDocObbligatorio(String flDocObbligatorio) {
        this.flDocObbligatorio = flDocObbligatorio;
    }

    // bi-directional many-to-one association to PigSismaValDoc
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_PIG_SISMA_VAL_DOC")
    public PigSismaValDoc getPigSismaValDoc() {
        return this.pigSismaValDoc;
    }

    public void setPigSismaValDoc(PigSismaValDoc pigSismaValDoc) {
        this.pigSismaValDoc = pigSismaValDoc;
    }

    // bi-directional many-to-one association to PigSismaValDoc
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_SISMA_FASE_PROGETTO")
    public PigSismaFaseProgetto getPigSismaFaseProgetto() {
        return pigSismaFaseProgetto;
    }

    public void setPigSismaFaseProgetto(PigSismaFaseProgetto pigSismaFaseProgetto) {
        this.pigSismaFaseProgetto = pigSismaFaseProgetto;
    }
}
