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
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable()
public class MonVLisObjNonVersId implements Serializable {

    private static final long serialVersionUID = 1L;
    private String cdKeyObject;

    @Column(name = "CD_KEY_OBJECT")
    public String getCdKeyObject() {
	return cdKeyObject;
    }

    public void setCdKeyObject(String cdKeyObject) {
	this.cdKeyObject = cdKeyObject;
    }

    private BigDecimal idVers;

    @Column(name = "ID_VERS")
    public BigDecimal getIdVers() {
	return idVers;
    }

    public void setIdVers(BigDecimal idVers) {
	this.idVers = idVers;
    }

    @Override
    public int hashCode() {
	int hash = 5;
	hash = 73 * hash + Objects.hashCode(this.cdKeyObject);
	hash = 73 * hash + Objects.hashCode(this.idVers);
	return hash;
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj) {
	    return true;
	}
	if (obj == null) {
	    return false;
	}
	if (getClass() != obj.getClass()) {
	    return false;
	}
	final MonVLisObjNonVersId other = (MonVLisObjNonVersId) obj;
	if (!Objects.equals(this.cdKeyObject, other.cdKeyObject)) {
	    return false;
	}
	if (!Objects.equals(this.idVers, other.idVers)) {
	    return false;
	}
	return true;
    }
}
