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
 * The persistent class for the PIG_V_CHK_DEL_DICHVERSSACER database table.
 *
 */
@Entity
@Table(name = "PIG_V_CHK_DEL_DICHVERSSACER")
@NamedQuery(name = "PigVChkDelDichverssacer.findAll", query = "SELECT p FROM PigVChkDelDichverssacer p")
public class PigVChkDelDichverssacer implements Serializable {

    private static final long serialVersionUID = 1L;
    private String flDelDchVersSacerOk;
    private BigDecimal idDichVersSacer;

    public PigVChkDelDichverssacer() {
    }

    @Column(name = "FL_DEL_DCH_VERS_SACER_OK", columnDefinition = "char")
    public String getFlDelDchVersSacerOk() {
        return this.flDelDchVersSacerOk;
    }

    public void setFlDelDchVersSacerOk(String flDelDchVersSacerOk) {
        this.flDelDchVersSacerOk = flDelDchVersSacerOk;
    }

    @Id
    @Column(name = "ID_DICH_VERS_SACER")
    public BigDecimal getIdDichVersSacer() {
        return this.idDichVersSacer;
    }

    public void setIdDichVersSacer(BigDecimal idDichVersSacer) {
        this.idDichVersSacer = idDichVersSacer;
    }

}
