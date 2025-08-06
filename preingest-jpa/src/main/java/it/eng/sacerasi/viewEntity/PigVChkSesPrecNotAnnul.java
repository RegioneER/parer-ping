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
 * The persistent class for the PIG_V_CHK_SES_PREC_NOT_ANNUL database table.
 *
 */
@Entity
@Table(name = "PIG_V_CHK_SES_PREC_NOT_ANNUL")
public class PigVChkSesPrecNotAnnul implements Serializable {

    private static final long serialVersionUID = 1L;
    private String flSesPrecNotAnnullata;
    private BigDecimal idObject;

    public PigVChkSesPrecNotAnnul() {
    }

    @Column(name = "FL_SES_PREC_NOT_ANNULLATA", columnDefinition = "char")
    public String getFlSesPrecNotAnnullata() {
	return this.flSesPrecNotAnnullata;
    }

    public void setFlSesPrecNotAnnullata(String flSesPrecNotAnnullata) {
	this.flSesPrecNotAnnullata = flSesPrecNotAnnullata;
    }

    @Id
    @Column(name = "ID_OBJECT")
    public BigDecimal getIdObject() {
	return this.idObject;
    }

    public void setIdObject(BigDecimal idObject) {
	this.idObject = idObject;
    }

}
