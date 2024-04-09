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
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable()
public class PigVSuLisDocsPianoId implements Serializable {

    private static final long serialVersionUID = 1L;
    private String nmTipoStrumento;

    @Column(name = "NM_TIPO_STRUMENTO")
    public String getNmTipoStrumento() {
        return nmTipoStrumento;
    }

    public void setNmTipoStrumento(String nmTipoStrumento) {
        this.nmTipoStrumento = nmTipoStrumento;
    }

    private String tiFaseStrumento;

    @Column(name = "TI_FASE_STRUMENTO")
    public String getTiFaseStrumento() {
        return tiFaseStrumento;
    }

    public void setTiFaseStrumento(String tiFaseStrumento) {
        this.tiFaseStrumento = tiFaseStrumento;
    }

    private String nmTipoDocumento;

    @Column(name = "NM_TIPO_DOCUMENTO")
    public String getNmTipoDocumento() {
        return nmTipoDocumento;
    }

    public void setNmTipoDocumento(String nmTipoDocumento) {
        this.nmTipoDocumento = nmTipoDocumento;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + Objects.hashCode(this.nmTipoStrumento);
        hash = 59 * hash + Objects.hashCode(this.tiFaseStrumento);
        hash = 59 * hash + Objects.hashCode(this.nmTipoDocumento);
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
        final PigVSuLisDocsPianoId other = (PigVSuLisDocsPianoId) obj;
        if (!Objects.equals(this.nmTipoStrumento, other.nmTipoStrumento)) {
            return false;
        }
        if (!Objects.equals(this.tiFaseStrumento, other.tiFaseStrumento)) {
            return false;
        }
        if (!Objects.equals(this.nmTipoDocumento, other.nmTipoDocumento)) {
            return false;
        }
        return true;
    }
}
