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
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * The persistent class for the PIG_STRUM_URB_PIANO_DOC_REQ database table.
 *
 */
@Entity
@Table(name = "PIG_STRUM_URB_PIANO_DOC_REQ")
public class PigStrumUrbPianoDocReq implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long idStrumUrbPianoDocReq;
    private String flDocObbligatorio;
    private PigStrumUrbPianoStato pigStrumUrbPianoStato;
    private PigStrumUrbValDoc pigStrumUrbValDoc;

    public PigStrumUrbPianoDocReq() {
        // for Hibernate
    }

    @Id
    @Column(name = "ID_STRUM_URB_PIANO_DOC_REQ")
    public Long getIdStrumUrbPianoDocReq() {
        return this.idStrumUrbPianoDocReq;
    }

    public void setIdStrumUrbPianoDocReq(Long idStrumUrbPianoDocReq) {
        this.idStrumUrbPianoDocReq = idStrumUrbPianoDocReq;
    }

    @Column(name = "FL_DOC_OBBLIGATORIO", columnDefinition = "char")
    public String getFlDocObbligatorio() {
        return this.flDocObbligatorio;
    }

    public void setFlDocObbligatorio(String flDocObbligatorio) {
        this.flDocObbligatorio = flDocObbligatorio;
    }

    // bi-directional many-to-one association to PigStrumUrbPianoStato
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_STRUM_URB_PIANO_STATO")
    public PigStrumUrbPianoStato getPigStrumUrbPianoStato() {
        return this.pigStrumUrbPianoStato;
    }

    public void setPigStrumUrbPianoStato(PigStrumUrbPianoStato pigStrumUrbPianoStato) {
        this.pigStrumUrbPianoStato = pigStrumUrbPianoStato;
    }

    // bi-directional many-to-one association to PigStrumUrbValDoc
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_PIG_STRUM_URB_VAL_DOC")
    public PigStrumUrbValDoc getPigStrumUrbValDoc() {
        return this.pigStrumUrbValDoc;
    }

    public void setPigStrumUrbValDoc(PigStrumUrbValDoc pigStrumUrbValDoc) {
        this.pigStrumUrbValDoc = pigStrumUrbValDoc;
    }

}
