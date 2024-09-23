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
import javax.persistence.Table;

/**
 * The persistent class for the MON_V_CHK_OK_OBJ database table.
 *
 */
@Entity
@Table(name = "MON_V_CHK_OK_OBJ")
public class MonVChkOkObj implements Serializable {
    private static final long serialVersionUID = 1L;
    private String flObjectChiusoOk;
    private BigDecimal idObject;
    private BigDecimal idObjectPadre;

    public MonVChkOkObj() {
    }

    @Column(name = "FL_OBJECT_CHIUSO_OK", columnDefinition = "char")
    public String getFlObjectChiusoOk() {
        return this.flObjectChiusoOk;
    }

    public void setFlObjectChiusoOk(String flObjectChiusoOk) {
        this.flObjectChiusoOk = flObjectChiusoOk;
    }

    @Id
    @Column(name = "ID_OBJECT")
    public BigDecimal getIdObject() {
        return this.idObject;
    }

    public void setIdObject(BigDecimal idObject) {
        this.idObject = idObject;
    }

    @Column(name = "ID_OBJECT_PADRE")
    public BigDecimal getIdObjectPadre() {
        return this.idObjectPadre;
    }

    public void setIdObjectPadre(BigDecimal idObjectPadre) {
        this.idObjectPadre = idObjectPadre;
    }

}
