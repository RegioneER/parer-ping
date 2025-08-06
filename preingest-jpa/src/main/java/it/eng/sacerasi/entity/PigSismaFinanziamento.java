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
 * The persistent class for the PIG_SISMA_FINANZIAMENTO database table.
 *
 */
@Entity
@Table(name = "PIG_SISMA_FINANZIAMENTO")
public class PigSismaFinanziamento implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long idSismaFinanziamento;
    private String cdTipoFinanziamento;
    private String dsTipoFinanziamento;
    private String dsTipoRegistroSaPubblico;
    private String dsTipoRegistroAgenzia;

    public PigSismaFinanziamento() {
    }

    @Id
    @SequenceGenerator(name = "PIG_SISMA_FINANZIAMENTO_IDSISMAFINANZIAMENTO_GENERATOR", sequenceName = "SPIG_SISMA_FINANZIAMENTO", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PIG_SISMA_FINANZIAMENTO_IDSISMAFINANZIAMENTO_GENERATOR")
    @Column(name = "ID_SISMA_FINANZIAMENTO")
    public Long getIdSismaFinanziamento() {
	return this.idSismaFinanziamento;
    }

    public void setIdSismaFinanziamento(Long idSismaFinanziamento) {
	this.idSismaFinanziamento = idSismaFinanziamento;
    }

    @Column(name = "CD_TIPO_FINANZIAMENTO")
    public String getCdTipoFinanziamento() {
	return this.cdTipoFinanziamento;
    }

    public void setCdTipoFinanziamento(String cdTipoFinanziamento) {
	this.cdTipoFinanziamento = cdTipoFinanziamento;
    }

    @Column(name = "DS_TIPO_FINANZIAMENTO")
    public String getDsTipoFinanziamento() {
	return this.dsTipoFinanziamento;
    }

    public void setDsTipoFinanziamento(String dsTipoFinanziamento) {
	this.dsTipoFinanziamento = dsTipoFinanziamento;
    }

    @Column(name = "DS_TIPO_REGISTRO_SA_PUBBLICO")
    public String getDsTipoRegistroSaPubblico() {
	return dsTipoRegistroSaPubblico;
    }

    public void setDsTipoRegistroSaPubblico(String dsTipoRegistroSaPubblico) {
	this.dsTipoRegistroSaPubblico = dsTipoRegistroSaPubblico;
    }

    @Column(name = "DS_TIPO_REGISTRO_AGENZIA")
    public String getDsTipoRegistroAgenzia() {
	return dsTipoRegistroAgenzia;
    }

    public void setDsTipoRegistroAgenzia(String dsTipoRegistroAgenzia) {
	this.dsTipoRegistroAgenzia = dsTipoRegistroAgenzia;
    }

}
