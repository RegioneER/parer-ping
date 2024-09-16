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
 * The persistent class for the MON_V_OBJ_RANGE_DT database table.
 *
 */
@Entity
@Table(name = "MON_V_OBJ_RANGE_DT")
public class MonVObjRangeDt implements Serializable {
    private static final long serialVersionUID = 1L;
    private BigDecimal idObject;
    private BigDecimal idTipoObject;
    private BigDecimal idVers;
    private String tiClasseVersFile;
    private String tiDtCreazione;
    private String tiStatoObject;
    private String cdKeyObject;

    public MonVObjRangeDt() {
    }

    @Id
    @Column(name = "ID_OBJECT")
    public BigDecimal getIdObject() {
        return this.idObject;
    }

    public void setIdObject(BigDecimal idObject) {
        this.idObject = idObject;
    }

    @Column(name = "ID_TIPO_OBJECT")
    public BigDecimal getIdTipoObject() {
        return this.idTipoObject;
    }

    public void setIdTipoObject(BigDecimal idTipoObject) {
        this.idTipoObject = idTipoObject;
    }

    @Column(name = "ID_VERS")
    public BigDecimal getIdVers() {
        return this.idVers;
    }

    public void setIdVers(BigDecimal idVers) {
        this.idVers = idVers;
    }

    @Column(name = "TI_CLASSE_VERS_FILE")
    public String getTiClasseVersFile() {
        return this.tiClasseVersFile;
    }

    public void setTiClasseVersFile(String tiClasseVersFile) {
        this.tiClasseVersFile = tiClasseVersFile;
    }

    @Column(name = "TI_DT_CREAZIONE")
    public String getTiDtCreazione() {
        return this.tiDtCreazione;
    }

    public void setTiDtCreazione(String tiDtCreazione) {
        this.tiDtCreazione = tiDtCreazione;
    }

    @Column(name = "TI_STATO_OBJECT")
    public String getTiStatoObject() {
        return this.tiStatoObject;
    }

    public void setTiStatoObject(String tiStatoObject) {
        this.tiStatoObject = tiStatoObject;
    }

    // MEV 26979
    @Column(name = "CD_KEY_OBJECT")
    public String getCdKeyObject() {
        return cdKeyObject;
    }

    public void setCdKeyObject(String cdKeyObject) {
        this.cdKeyObject = cdKeyObject;
    }
}
