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
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * The persistent class for the PIG_SISMA_FASE_PROGETTO database table.
 *
 */
@Entity
@Table(name = "PIG_SISMA_FASE_PROGETTO")
public class PigSismaFaseProgetto implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long idSismaFaseProgetto;
    private String tiFaseSisma;
    private String dsFaseSisma;
    private BigDecimal niOrd;
    private PigSismaFinanziamento pigSismaFinanziamento;

    public PigSismaFaseProgetto() {
	// non usato
    }

    @Id
    @Column(name = "ID_SISMA_FASE_PROGETTO")
    public Long getIdSismaFaseProgetto() {
	return idSismaFaseProgetto;
    }

    public void setIdSismaFaseProgetto(Long idSismaFaseProgetto) {
	this.idSismaFaseProgetto = idSismaFaseProgetto;
    }

    @Column(name = "TI_FASE_SISMA")
    public String getTiFaseSisma() {
	return this.tiFaseSisma;
    }

    public void setTiFaseSisma(String tiFaseSisma) {
	this.tiFaseSisma = tiFaseSisma;
    }

    @Column(name = "DS_FASE_SISMA")
    public String getDsFaseSisma() {
	return dsFaseSisma;
    }

    public void setDsFaseSisma(String dsFaseSisma) {
	this.dsFaseSisma = dsFaseSisma;
    }

    @Column(name = "NI_ORD")
    public BigDecimal getNiOrd() {
	return niOrd;
    }

    public void setNiOrd(BigDecimal niOrd) {
	this.niOrd = niOrd;
    }

    // bi-directional many-to-one association to PigSismaFinanziamento
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_SISMA_FINANZIAMENTO")
    public PigSismaFinanziamento getPigSismaFinanziamento() {
	return this.pigSismaFinanziamento;
    }

    public void setPigSismaFinanziamento(PigSismaFinanziamento pigSismaFinanziamento) {
	this.pigSismaFinanziamento = pigSismaFinanziamento;
    }

}
