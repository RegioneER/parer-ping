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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * The persistent class for the PIG_SISMA_VAL_ATTO database table.
 *
 */
@Entity
@Table(name = "PIG_SISMA_VAL_ATTO")
public class PigSismaValAtto implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long idSismaValAtto;
    private String nmTipoAtto;
    private String tiTipoAtto;

    public PigSismaValAtto() {
	// hibernate
    }

    @Id
    @SequenceGenerator(name = "PIG_SISMA_VAL_ATTO_IDSISMAVALATTO_GENERATOR", sequenceName = "SPIG_SISMA_VAL_ATTO", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PIG_SISMA_VAL_ATTO_IDSISMAVALATTO_GENERATOR")
    @Column(name = "ID_SISMA_VAL_ATTO")
    public Long getIdSismaValAtto() {
	return idSismaValAtto;
    }

    public void setIdSismaValAtto(Long idSismaValAtto) {
	this.idSismaValAtto = idSismaValAtto;
    }

    @Column(name = "NM_TIPO_ATTO")
    public String getNmTipoAtto() {
	return nmTipoAtto;
    }

    public void setNmTipoAtto(String nmTipoAtto) {
	this.nmTipoAtto = nmTipoAtto;
    }

    @Column(name = "TI_TIPO_ATTO")
    public String getTiTipoAtto() {
	return tiTipoAtto;
    }

    public void setTiTipoAtto(String tiTipoAtto) {
	this.tiTipoAtto = tiTipoAtto;
    }

}
