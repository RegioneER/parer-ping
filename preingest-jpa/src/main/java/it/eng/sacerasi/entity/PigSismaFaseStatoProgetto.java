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
 * The persistent class for the PIG_SISMA_FASE_STATO_PROGETTO database table.
 *
 */
@Entity
@Table(name = "PIG_SISMA_FASE_STATO_PROGETTO")
public class PigSismaFaseStatoProgetto implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long idSismaFaseStatoProgetto;
    private PigSismaFaseProgetto pigSismaFaseProgetto;
    private PigSismaStatoProgetto pigSismaStatoProgetto;

    public PigSismaFaseStatoProgetto() {
        // for Hibernate
    }

    @Id
    @Column(name = "ID_SISMA_FASE_STATO_PROGETTO")
    public Long getIdSismaFaseStatoProgetto() {
        return idSismaFaseStatoProgetto;
    }

    public void setIdSismaFaseStatoProgetto(Long idSismaFaseStatoProgetto) {
        this.idSismaFaseStatoProgetto = idSismaFaseStatoProgetto;
    }

    // bi-directional many-to-one association to PigSismaFaseProgetto
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_SISMA_FASE_PROGETTO")
    public PigSismaFaseProgetto getPigSismaFaseProgetto() {
        return pigSismaFaseProgetto;
    }

    public void setPigSismaFaseProgetto(PigSismaFaseProgetto pigSismaFaseProgetto) {
        this.pigSismaFaseProgetto = pigSismaFaseProgetto;
    }

    // bi-directional many-to-one association to PigSismaStatoProgetto
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_SISMA_STATO_PROGETTO")
    public PigSismaStatoProgetto getPigSismaStatoProgetto() {
        return pigSismaStatoProgetto;
    }

    public void setPigSismaStatoProgetto(PigSismaStatoProgetto pigSismaStatoProgetto) {
        this.pigSismaStatoProgetto = pigSismaStatoProgetto;
    }

}
