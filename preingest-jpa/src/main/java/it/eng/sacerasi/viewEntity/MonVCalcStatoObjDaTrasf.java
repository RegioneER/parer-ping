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

package it.eng.sacerasi.viewEntity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * The persistent class for the MON_V_CALC_STATO_OBJ_DA_TRASF database table.
 *
 */
@Entity
@Table(name = "MON_V_CALC_STATO_OBJ_DA_TRASF")
@NamedQuery(name = "MonVCalcStatoObjDaTrasf.findAll", query = "SELECT m FROM MonVCalcStatoObjDaTrasf m")
public class MonVCalcStatoObjDaTrasf implements Serializable {

    private static final long serialVersionUID = 1L;
    private String tiStatoObjectPadre;
    private BigDecimal idOggettoPadre;

    public MonVCalcStatoObjDaTrasf() {
    }

    @Column(name = "TI_STATO_OBJECT_PADRE")
    public String getTiStatoObjectPadre() {
        return this.tiStatoObjectPadre;
    }

    public void setTiStatoObjectPadre(String tiStatoObjectPadre) {
        this.tiStatoObjectPadre = tiStatoObjectPadre;
    }

    @Id
    @Column(name = "ID_OGGETTO_PADRE")
    public BigDecimal getIdOggettoPadre() {
        return this.idOggettoPadre;
    }

    public void setIdOggettoPadre(BigDecimal idOggettoPadre) {
        this.idOggettoPadre = idOggettoPadre;
    }

}
