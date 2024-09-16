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
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * The persistent class for the MON_V_VIS_OBJ_TRASF database table.
 *
 */
@Entity
@Table(name = "MON_V_VIS_OBJ_TRASF")
@NamedQuery(name = "MonVVisObjTrasf.findAll", query = "SELECT m FROM MonVVisObjTrasf m")
public class MonVVisObjTrasf implements Serializable {
    private static final long serialVersionUID = 1L;
    private String cdEncodingHashFileVers;
    private String cdErr;
    private String cdKeyObjectDaTrasf;
    private String cdKeyObjectTrasf;
    private String dlErr;
    private String dsHashFileVers;
    private String dsObjectDaTrasf;
    private String dsObjectTrasf;
    private String dsPath;
    private String dsPathTrasf;
    private Date dtStatoCorDaTrasf;
    private BigDecimal idObjectDaTrasf;
    private BigDecimal idObjectTrasf;
    private BigDecimal idVersDaTrasf;
    private BigDecimal idVersTrasf;
    private BigDecimal niTotObjectTrasf;
    private String nmAmbienteVersDaTrasf;
    private String nmAmbienteVersTrasf;
    private String nmTipoObjectDaTrasf;
    private String nmTipoObjectTrasf;
    private String nmVersDaTrasf;
    private String nmVersTrasf;
    private BigDecimal pgOggettoTrasf;
    private String tiAlgoHashFileVers;
    private String tiStatoObjectDaTrasf;
    private String tiStatoTrasf;

    public MonVVisObjTrasf() {
    }

    @Column(name = "CD_ENCODING_HASH_FILE_VERS")
    public String getCdEncodingHashFileVers() {
        return this.cdEncodingHashFileVers;
    }

    public void setCdEncodingHashFileVers(String cdEncodingHashFileVers) {
        this.cdEncodingHashFileVers = cdEncodingHashFileVers;
    }

    @Column(name = "CD_ERR")
    public String getCdErr() {
        return this.cdErr;
    }

    public void setCdErr(String cdErr) {
        this.cdErr = cdErr;
    }

    @Column(name = "CD_KEY_OBJECT_DA_TRASF")
    public String getCdKeyObjectDaTrasf() {
        return this.cdKeyObjectDaTrasf;
    }

    public void setCdKeyObjectDaTrasf(String cdKeyObjectDaTrasf) {
        this.cdKeyObjectDaTrasf = cdKeyObjectDaTrasf;
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

    @Column(name = "DS_HASH_FILE_VERS")
    public String getDsHashFileVers() {
        return this.dsHashFileVers;
    }

    public void setDsHashFileVers(String dsHashFileVers) {
        this.dsHashFileVers = dsHashFileVers;
    }

    @Column(name = "DS_OBJECT_DA_TRASF")
    public String getDsObjectDaTrasf() {
        return this.dsObjectDaTrasf;
    }

    public void setDsObjectDaTrasf(String dsObjectDaTrasf) {
        this.dsObjectDaTrasf = dsObjectDaTrasf;
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

    @Column(name = "DT_STATO_COR_DA_TRASF")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getDtStatoCorDaTrasf() {
        return this.dtStatoCorDaTrasf;
    }

    public void setDtStatoCorDaTrasf(Date dtStatoCorDaTrasf) {
        this.dtStatoCorDaTrasf = dtStatoCorDaTrasf;
    }

    @Column(name = "ID_OBJECT_DA_TRASF")
    public BigDecimal getIdObjectDaTrasf() {
        return this.idObjectDaTrasf;
    }

    public void setIdObjectDaTrasf(BigDecimal idObjectDaTrasf) {
        this.idObjectDaTrasf = idObjectDaTrasf;
    }

    @Id
    @Column(name = "ID_OBJECT_TRASF")
    public BigDecimal getIdObjectTrasf() {
        return this.idObjectTrasf;
    }

    public void setIdObjectTrasf(BigDecimal idObjectTrasf) {
        this.idObjectTrasf = idObjectTrasf;
    }

    @Column(name = "ID_VERS_DA_TRASF")
    public BigDecimal getIdVersDaTrasf() {
        return this.idVersDaTrasf;
    }

    public void setIdVersDaTrasf(BigDecimal idVersDaTrasf) {
        this.idVersDaTrasf = idVersDaTrasf;
    }

    @Column(name = "ID_VERS_TRASF")
    public BigDecimal getIdVersTrasf() {
        return this.idVersTrasf;
    }

    public void setIdVersTrasf(BigDecimal idVersTrasf) {
        this.idVersTrasf = idVersTrasf;
    }

    @Column(name = "NI_TOT_OBJECT_TRASF")
    public BigDecimal getNiTotObjectTrasf() {
        return this.niTotObjectTrasf;
    }

    public void setNiTotObjectTrasf(BigDecimal niTotObjectTrasf) {
        this.niTotObjectTrasf = niTotObjectTrasf;
    }

    @Column(name = "NM_AMBIENTE_VERS_DA_TRASF")
    public String getNmAmbienteVersDaTrasf() {
        return this.nmAmbienteVersDaTrasf;
    }

    public void setNmAmbienteVersDaTrasf(String nmAmbienteVersDaTrasf) {
        this.nmAmbienteVersDaTrasf = nmAmbienteVersDaTrasf;
    }

    @Column(name = "NM_AMBIENTE_VERS_TRASF")
    public String getNmAmbienteVersTrasf() {
        return this.nmAmbienteVersTrasf;
    }

    public void setNmAmbienteVersTrasf(String nmAmbienteVersTrasf) {
        this.nmAmbienteVersTrasf = nmAmbienteVersTrasf;
    }

    @Column(name = "NM_TIPO_OBJECT_DA_TRASF")
    public String getNmTipoObjectDaTrasf() {
        return this.nmTipoObjectDaTrasf;
    }

    public void setNmTipoObjectDaTrasf(String nmTipoObjectDaTrasf) {
        this.nmTipoObjectDaTrasf = nmTipoObjectDaTrasf;
    }

    @Column(name = "NM_TIPO_OBJECT_TRASF")
    public String getNmTipoObjectTrasf() {
        return this.nmTipoObjectTrasf;
    }

    public void setNmTipoObjectTrasf(String nmTipoObjectTrasf) {
        this.nmTipoObjectTrasf = nmTipoObjectTrasf;
    }

    @Column(name = "NM_VERS_DA_TRASF")
    public String getNmVersDaTrasf() {
        return this.nmVersDaTrasf;
    }

    public void setNmVersDaTrasf(String nmVersDaTrasf) {
        this.nmVersDaTrasf = nmVersDaTrasf;
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

    @Column(name = "TI_ALGO_HASH_FILE_VERS")
    public String getTiAlgoHashFileVers() {
        return this.tiAlgoHashFileVers;
    }

    public void setTiAlgoHashFileVers(String tiAlgoHashFileVers) {
        this.tiAlgoHashFileVers = tiAlgoHashFileVers;
    }

    @Column(name = "TI_STATO_OBJECT_DA_TRASF")
    public String getTiStatoObjectDaTrasf() {
        return this.tiStatoObjectDaTrasf;
    }

    public void setTiStatoObjectDaTrasf(String tiStatoObjectDaTrasf) {
        this.tiStatoObjectDaTrasf = tiStatoObjectDaTrasf;
    }

    @Column(name = "TI_STATO_TRASF")
    public String getTiStatoTrasf() {
        return this.tiStatoTrasf;
    }

    public void setTiStatoTrasf(String tiStatoTrasf) {
        this.tiStatoTrasf = tiStatoTrasf;
    }

}
