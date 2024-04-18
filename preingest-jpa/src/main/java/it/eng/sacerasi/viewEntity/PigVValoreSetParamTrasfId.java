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
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable()
public class PigVValoreSetParamTrasfId implements Serializable {

    private static final long serialVersionUID = 1L;
    private BigDecimal idSetParamTrasf;

    @Column(name = "ID_SET_PARAM_TRASF")
    public BigDecimal getIdSetParamTrasf() {
        return idSetParamTrasf;
    }

    public void setIdSetParamTrasf(BigDecimal idSetParamTrasf) {
        this.idSetParamTrasf = idSetParamTrasf;
    }

    private BigDecimal idVersTipoObjectDaTrasf;

    @Column(name = "ID_VERS_TIPO_OBJECT_DA_TRASF")
    public BigDecimal getIdVersTipoObjectDaTrasf() {
        return idVersTipoObjectDaTrasf;
    }

    public void setIdVersTipoObjectDaTrasf(BigDecimal idVersTipoObjectDaTrasf) {
        this.idVersTipoObjectDaTrasf = idVersTipoObjectDaTrasf;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 73 * hash + Objects.hashCode(this.idSetParamTrasf);
        hash = 73 * hash + Objects.hashCode(this.idVersTipoObjectDaTrasf);
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
        final PigVValoreSetParamTrasfId other = (PigVValoreSetParamTrasfId) obj;
        if (!Objects.equals(this.idSetParamTrasf, other.idSetParamTrasf)) {
            return false;
        }
        if (!Objects.equals(this.idVersTipoObjectDaTrasf, other.idVersTipoObjectDaTrasf)) {
            return false;
        }
        return true;
    }
}