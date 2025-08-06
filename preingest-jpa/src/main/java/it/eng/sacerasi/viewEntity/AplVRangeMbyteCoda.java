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

package it.eng.sacerasi.viewEntity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * The persistent class for the APL_V_RANGE_MBYTE_CODA database table.
 *
 */
@Entity
@Table(name = "APL_V_RANGE_MBYTE_CODA")
public class AplVRangeMbyteCoda implements Serializable {
    private static final long serialVersionUID = 1L;
    private String idCodaDaUsare;
    private BigDecimal niLimiteInf;
    private BigDecimal niLimiteSup;

    public AplVRangeMbyteCoda() {
    }

    @Column(name = "ID_CODA_DA_USARE")
    public String getIdCodaDaUsare() {
	return this.idCodaDaUsare;
    }

    public void setIdCodaDaUsare(String idCodaDaUsare) {
	this.idCodaDaUsare = idCodaDaUsare;
    }

    @Id
    @Column(name = "NI_LIMITE_INF")
    public BigDecimal getNiLimiteInf() {
	return this.niLimiteInf;
    }

    public void setNiLimiteInf(BigDecimal niLimiteInf) {
	this.niLimiteInf = niLimiteInf;
    }

    @Column(name = "NI_LIMITE_SUP")
    public BigDecimal getNiLimiteSup() {
	return this.niLimiteSup;
    }

    public void setNiLimiteSup(BigDecimal niLimiteSup) {
	this.niLimiteSup = niLimiteSup;
    }

}
