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
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * The persistent class for the PIG_STRUM_URB_ATTO database table.
 *
 */
@Entity
@Table(name = "PIG_STRUM_URB_ATTO")
public class PigStrumUrbAtto implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long idAtto;
    private String tiAtto;
    private String cdNome;
    private String dsDescrizione;

    public PigStrumUrbAtto() {
        // placeholder
    }

    @Id
    @Column(name = "ID_ATTO")
    public Long getIdAtto() {
        return this.idAtto;
    }

    public void setIdAtto(Long idAtto) {
        this.idAtto = idAtto;
    }

    @Column(name = "TI_ATTO")
    public String getTiAtto() {
        return tiAtto;
    }

    public void setTiAtto(String tiAtto) {
        this.tiAtto = tiAtto;
    }

    @Column(name = "CD_NOME")
    public String getCdNome() {
        return cdNome;
    }

    public void setCdNome(String cdName) {
        this.cdNome = cdName;
    }

    @Column(name = "DS_DESCRIZIONE")
    public String getDsDescrizione() {
        return dsDescrizione;
    }

    public void setDsDescrizione(String dsDescription) {
        this.dsDescrizione = dsDescription;
    }

}
