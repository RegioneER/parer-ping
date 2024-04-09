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
public class PigVLisDichVersSacerId implements Serializable {

    private static final long serialVersionUID = 1L;
    private BigDecimal idDichVersSacer;

    @Column(name = "ID_DICH_VERS_SACER")
    public BigDecimal getIdDichVersSacer() {
        return idDichVersSacer;
    }

    public void setIdDichVersSacer(BigDecimal idDichVersSacer) {
        this.idDichVersSacer = idDichVersSacer;
    }

    private BigDecimal idOrganizIamDich;

    @Column(name = "ID_ORGANIZ_IAM_DICH")
    public BigDecimal getIdOrganizIamDich() {
        return idOrganizIamDich;
    }

    public void setIdOrganizIamDich(BigDecimal idOrganizIamDich) {
        this.idOrganizIamDich = idOrganizIamDich;
    }

    private String tiDichVersTiObj;

    @Column(name = "TI_DICH_VERS_TI_OBJ")
    public String getTiDichVersTiObj() {
        return tiDichVersTiObj;
    }

    public void setTiDichVersTiObj(String tiDichVersTiObj) {
        this.tiDichVersTiObj = tiDichVersTiObj;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + Objects.hashCode(this.idDichVersSacer);
        hash = 23 * hash + Objects.hashCode(this.idOrganizIamDich);
        hash = 23 * hash + Objects.hashCode(this.tiDichVersTiObj);
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
        final PigVLisDichVersSacerId other = (PigVLisDichVersSacerId) obj;
        if (!Objects.equals(this.tiDichVersTiObj, other.tiDichVersTiObj)) {
            return false;
        }
        if (!Objects.equals(this.idDichVersSacer, other.idDichVersSacer)) {
            return false;
        }
        if (!Objects.equals(this.idOrganizIamDich, other.idOrganizIamDich)) {
            return false;
        }
        return true;
    }
}
