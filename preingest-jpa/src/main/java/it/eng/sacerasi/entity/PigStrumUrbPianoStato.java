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
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * The persistent class for the PIG_STRUM_URB_PIANO_STATO database table.
 *
 */
@Entity
@Table(name = "PIG_STRUM_URB_PIANO_STATO")
public class PigStrumUrbPianoStato implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long idStrumUrbPianoStato;
    private String nmTipoStrumentoUrbanistico;
    private String tiFaseStrumento;
    private List<PigStrumentiUrbanistici> pigStrumentiUrbanisticis = new ArrayList<>();
    private List<PigStrumUrbPianoDocReq> pigStrumUrbPianoDocReqs = new ArrayList<>();

    public PigStrumUrbPianoStato() {
        // for Hibernate
    }

    @Id
    @Column(name = "ID_STRUM_URB_PIANO_STATO")
    public Long getIdStrumUrbPianoStato() {
        return this.idStrumUrbPianoStato;
    }

    public void setIdStrumUrbPianoStato(Long idStrumUrbPianoStato) {
        this.idStrumUrbPianoStato = idStrumUrbPianoStato;
    }

    @Column(name = "NM_TIPO_STRUMENTO_URBANISTICO")
    public String getNmTipoStrumentoUrbanistico() {
        return this.nmTipoStrumentoUrbanistico;
    }

    public void setNmTipoStrumentoUrbanistico(String nmTipoStrumentoUrbanistico) {
        this.nmTipoStrumentoUrbanistico = nmTipoStrumentoUrbanistico;
    }

    @Column(name = "TI_FASE_STRUMENTO")
    public String getTiFaseStrumento() {
        return this.tiFaseStrumento;
    }

    public void setTiFaseStrumento(String tiFaseStrumento) {
        this.tiFaseStrumento = tiFaseStrumento;
    }

    // bi-directional many-to-one association to PigStrumentiUrbanistici
    @OneToMany(mappedBy = "pigStrumUrbPianoStato")
    public List<PigStrumentiUrbanistici> getPigStrumentiUrbanisticis() {
        return this.pigStrumentiUrbanisticis;
    }

    public void setPigStrumentiUrbanisticis(List<PigStrumentiUrbanistici> pigStrumentiUrbanisticis) {
        this.pigStrumentiUrbanisticis = pigStrumentiUrbanisticis;
    }

    public PigStrumentiUrbanistici addPigStrumentiUrbanistici(PigStrumentiUrbanistici pigStrumentiUrbanistici) {
        getPigStrumentiUrbanisticis().add(pigStrumentiUrbanistici);
        pigStrumentiUrbanistici.setPigStrumUrbPianoStato(this);

        return pigStrumentiUrbanistici;
    }

    public PigStrumentiUrbanistici removePigStrumentiUrbanistici(PigStrumentiUrbanistici pigStrumentiUrbanistici) {
        getPigStrumentiUrbanisticis().remove(pigStrumentiUrbanistici);
        pigStrumentiUrbanistici.setPigStrumUrbPianoStato(null);

        return pigStrumentiUrbanistici;
    }

    // bi-directional many-to-one association to PigStrumUrbPianoDocReq
    @OneToMany(mappedBy = "pigStrumUrbPianoStato")
    public List<PigStrumUrbPianoDocReq> getPigStrumUrbPianoDocReqs() {
        return this.pigStrumUrbPianoDocReqs;
    }

    public void setPigStrumUrbPianoDocReqs(List<PigStrumUrbPianoDocReq> pigStrumUrbPianoDocReqs) {
        this.pigStrumUrbPianoDocReqs = pigStrumUrbPianoDocReqs;
    }

    public PigStrumUrbPianoDocReq addPigStrumUrbPianoDocReq(PigStrumUrbPianoDocReq pigStrumUrbPianoDocReq) {
        getPigStrumUrbPianoDocReqs().add(pigStrumUrbPianoDocReq);
        pigStrumUrbPianoDocReq.setPigStrumUrbPianoStato(this);

        return pigStrumUrbPianoDocReq;
    }

    public PigStrumUrbPianoDocReq removePigStrumUrbPianoDocReq(PigStrumUrbPianoDocReq pigStrumUrbPianoDocReq) {
        getPigStrumUrbPianoDocReqs().remove(pigStrumUrbPianoDocReq);
        pigStrumUrbPianoDocReq.setPigStrumUrbPianoStato(null);

        return pigStrumUrbPianoDocReq;
    }

}
