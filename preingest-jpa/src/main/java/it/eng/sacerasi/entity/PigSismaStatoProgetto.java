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
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * The persistent class for the PIG_SISMA_STATO_PROGETTO database table.
 *
 */
@Entity
@Table(name = "PIG_SISMA_STATO_PROGETTO")
public class PigSismaStatoProgetto implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long idSismaStatoProgetto;
    private String tiStatoProgetto;
    private String dsStatoProgetto;

    public PigSismaStatoProgetto() {
	// for Hibernate
    }

    @Id
    @Column(name = "ID_SISMA_STATO_PROGETTO")
    public Long getIdSismaStatoProgetto() {
	return idSismaStatoProgetto;
    }

    public void setIdSismaStatoProgetto(Long idSismaStatoProgetto) {
	this.idSismaStatoProgetto = idSismaStatoProgetto;
    }

    @Column(name = "TI_STATO_PROGETTO")
    public String getTiStatoProgetto() {
	return tiStatoProgetto;
    }

    public void setTiStatoProgetto(String tiStatoProgetto) {
	this.tiStatoProgetto = tiStatoProgetto;
    }

    @Column(name = "DS_STATO_PROGETTO")
    public String getDsStatoProgetto() {
	return dsStatoProgetto;
    }

    public void setDsStatoProgetto(String dsStatoProgetto) {
	this.dsStatoProgetto = dsStatoProgetto;
    }

}
