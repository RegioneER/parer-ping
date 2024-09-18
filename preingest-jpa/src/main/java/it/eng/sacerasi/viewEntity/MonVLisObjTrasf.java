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
 * The persistent class for the MON_V_LIS_OBJ_TRASF database table.
 *
 */
@Entity
@Table(name = "MON_V_LIS_OBJ_TRASF")
public class MonVLisObjTrasf implements Serializable {
    private static final long serialVersionUID = 1L;
    private String cdErr;
    private String cdKeyObjectTrasf;
    private String dlErr;
    private String dsObjectTrasf;
    private String dsPath;
    private String dsPathTrasf;
    private BigDecimal idObjectDaTrasfPing;
    private BigDecimal idObjectTrasf;
    private BigDecimal idVersTrasf;
    private String nmAmbienteVersTrasf;
    private String nmTipoObjectTrasf;
    private String nmVersTrasf;
    private BigDecimal pgOggettoTrasf;
    private String tiStatoTrasf;
    private BigDecimal niUdProdotte;

    public MonVLisObjTrasf() {
    }

    @Column(name = "CD_ERR")
    public String getCdErr() {
        return this.cdErr;
    }

    public void setCdErr(String cdErr) {
        this.cdErr = cdErr;
    }

    @Column(name = "CD_KEY_OBJECT_TRASF")
    public String getCdKeyObjectTrasf() {
        return this.cdKeyObjectTrasf;
    }

    public void setCdKeyObjectTrasf(String cdKeyObjectTrasf) {
        this.cdKeyObjectTrasf = cdKeyObjectTrasf;
    }

    @Column(name = "DL_ERR")
    public String getDlErr() {
        return this.dlErr;
    }

    public void setDlErr(String dlErr) {
        this.dlErr = dlErr;
    }

    @Column(name = "DS_OBJECT_TRASF")
    public String getDsObjectTrasf() {
        return this.dsObjectTrasf;
    }

    public void setDsObjectTrasf(String dsObjectTrasf) {
        this.dsObjectTrasf = dsObjectTrasf;
    }

    @Column(name = "DS_PATH")
    public String getDsPath() {
        return this.dsPath;
    }

    public void setDsPath(String dsPath) {
        this.dsPath = dsPath;
    }

    @Column(name = "DS_PATH_TRASF")
    public String getDsPathTrasf() {
        return this.dsPathTrasf;
    }

    public void setDsPathTrasf(String dsPathTrasf) {
        this.dsPathTrasf = dsPathTrasf;
    }

    @Column(name = "ID_OBJECT_DA_TRASF_PING")
    public BigDecimal getIdObjectDaTrasfPing() {
        return this.idObjectDaTrasfPing;
    }

    public void setIdObjectDaTrasfPing(BigDecimal idObjectDaTrasfPing) {
        this.idObjectDaTrasfPing = idObjectDaTrasfPing;
    }

    @Id
    @Column(name = "ID_OBJECT_TRASF")
    public BigDecimal getIdObjectTrasf() {
        return this.idObjectTrasf;
    }

    public void setIdObjectTrasf(BigDecimal idObjectTrasf) {
        this.idObjectTrasf = idObjectTrasf;
    }

    @Column(name = "ID_VERS_TRASF")
    public BigDecimal getIdVersTrasf() {
        return this.idVersTrasf;
    }

    public void setIdVersTrasf(BigDecimal idVersTrasf) {
        this.idVersTrasf = idVersTrasf;
    }

    @Column(name = "NM_AMBIENTE_VERS_TRASF")
    public String getNmAmbienteVersTrasf() {
        return this.nmAmbienteVersTrasf;
    }

    public void setNmAmbienteVersTrasf(String nmAmbienteVersTrasf) {
        this.nmAmbienteVersTrasf = nmAmbienteVersTrasf;
    }

    @Column(name = "NM_TIPO_OBJECT_TRASF")
    public String getNmTipoObjectTrasf() {
        return this.nmTipoObjectTrasf;
    }

    public void setNmTipoObjectTrasf(String nmTipoObjectTrasf) {
        this.nmTipoObjectTrasf = nmTipoObjectTrasf;
    }

    @Column(name = "NM_VERS_TRASF")
    public String getNmVersTrasf() {
        return this.nmVersTrasf;
    }

    public void setNmVersTrasf(String nmVersTrasf) {
        this.nmVersTrasf = nmVersTrasf;
    }

    @Column(name = "PG_OGGETTO_TRASF")
    public BigDecimal getPgOggettoTrasf() {
        return this.pgOggettoTrasf;
    }

    public void setPgOggettoTrasf(BigDecimal pgOggettoTrasf) {
        this.pgOggettoTrasf = pgOggettoTrasf;
    }

    @Column(name = "TI_STATO_TRASF")
    public String getTiStatoTrasf() {
        return this.tiStatoTrasf;
    }

    public void setTiStatoTrasf(String tiStatoTrasf) {
        this.tiStatoTrasf = tiStatoTrasf;
    }

    @Column(name = "NI_UD_PRODOTTE")
    public BigDecimal getNiUdProdotte() {
        return niUdProdotte;
    }

    public void setNiUdProdotte(BigDecimal niUdProdotte) {
        this.niUdProdotte = niUdProdotte;
    }
}
