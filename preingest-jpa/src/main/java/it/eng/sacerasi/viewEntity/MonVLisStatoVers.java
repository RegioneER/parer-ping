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
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * The persistent class for the MON_V_LIS_STATO_VERS database table.
 */
@Entity
@Table(name = "MON_V_LIS_STATO_VERS")
@NamedQuery(name = "MonVLisStatoVers.findAll", query = "SELECT m FROM MonVLisStatoVers m")
public class MonVLisStatoVers implements Serializable {

    private static final long serialVersionUID = 1L;

    private String cdKeyObject;

    private String dsObject;

    private String dsHashFileVers;

    private Date dtVers;

    private BigDecimal idAmbienteVers;

    private BigDecimal idFileObject;

    private BigDecimal idSessioneIngest;

    private BigDecimal idTipoObject;

    private BigDecimal idUserIamVers;

    private BigDecimal idVers;

    private BigDecimal niByteFileVers;

    private String nmAmbienteVers;

    private String nmTipoObject;

    private String nmUseridVers;

    private String nmVers;

    private BigDecimal idObject;

    private String tiStatoEsterno;

    private String tiStatoObject;

    private String tiVersFile;
    private BigDecimal niUdProdotte;
    private String tiGestOggettiFigli;

    // MEV 30343
    private String note;

    private MonVLisStatoVersId monVLisStatoVersId;

    public MonVLisStatoVers() {
    }

    @Column(name = "CD_KEY_OBJECT")
    public String getCdKeyObject() {
        return this.cdKeyObject;
    }

    public void setCdKeyObject(String cdKeyObject) {
        this.cdKeyObject = cdKeyObject;
    }

    @Column(name = "DS_HASH_FILE_VERS")
    public String getDsHashFileVers() {
        return dsHashFileVers;
    }

    public void setDsHashFileVers(String dsHashFileVers) {
        this.dsHashFileVers = dsHashFileVers;
    }

    @Column(name = "DS_OBJECT")
    public String getDsObject() {
        return this.dsObject;
    }

    public void setDsObject(String dsObject) {
        this.dsObject = dsObject;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_VERS")
    public Date getDtVers() {
        return this.dtVers;
    }

    public void setDtVers(Date dtVers) {
        this.dtVers = dtVers;
    }

    @Column(name = "ID_AMBIENTE_VERS")
    public BigDecimal getIdAmbienteVers() {
        return this.idAmbienteVers;
    }

    public void setIdAmbienteVers(BigDecimal idAmbienteVers) {
        this.idAmbienteVers = idAmbienteVers;
    }

    @Column(name = "ID_FILE_OBJECT")
    public BigDecimal getIdFileObject() {
        return this.idFileObject;
    }

    public void setIdFileObject(BigDecimal idFileObject) {
        this.idFileObject = idFileObject;
    }

    @Column(name = "ID_SESSIONE_INGEST")
    public BigDecimal getIdSessioneIngest() {
        return this.idSessioneIngest;
    }

    public void setIdSessioneIngest(BigDecimal idSessioneIngest) {
        this.idSessioneIngest = idSessioneIngest;
    }

    @Column(name = "ID_TIPO_OBJECT")
    public BigDecimal getIdTipoObject() {
        return this.idTipoObject;
    }

    public void setIdTipoObject(BigDecimal idTipoObject) {
        this.idTipoObject = idTipoObject;
    }

    @Column(name = "ID_USER_IAM_VERS")
    public BigDecimal getIdUserIamVers() {
        return this.idUserIamVers;
    }

    public void setIdUserIamVers(BigDecimal idUserIamVers) {
        this.idUserIamVers = idUserIamVers;
    }

    @Column(name = "ID_VERS")
    public BigDecimal getIdVers() {
        return this.idVers;
    }

    public void setIdVers(BigDecimal idVers) {
        this.idVers = idVers;
    }

    @Column(name = "NI_BYTE_FILE_VERS")
    public BigDecimal getNiByteFileVers() {
        return this.niByteFileVers;
    }

    public void setNiByteFileVers(BigDecimal niByteFileVers) {
        this.niByteFileVers = niByteFileVers;
    }

    @Column(name = "NM_AMBIENTE_VERS")
    public String getNmAmbienteVers() {
        return this.nmAmbienteVers;
    }

    public void setNmAmbienteVers(String nmAmbienteVers) {
        this.nmAmbienteVers = nmAmbienteVers;
    }

    @Column(name = "NM_TIPO_OBJECT")
    public String getNmTipoObject() {
        return this.nmTipoObject;
    }

    public void setNmTipoObject(String nmTipoObject) {
        this.nmTipoObject = nmTipoObject;
    }

    @Column(name = "NM_USERID_VERS")
    public String getNmUseridVers() {
        return this.nmUseridVers;
    }

    public void setNmUseridVers(String nmUseridVers) {
        this.nmUseridVers = nmUseridVers;
    }

    @Column(name = "NM_VERS")
    public String getNmVers() {
        return this.nmVers;
    }

    public void setNmVers(String nmVers) {
        this.nmVers = nmVers;
    }

    @Column(name = "ID_OBJECT", insertable = false, updatable = false)
    public BigDecimal getIdObject() {
        return idObject;
    }

    public void setIdObject(BigDecimal idObject) {
        this.idObject = idObject;
    }

    @Column(name = "TI_STATO_ESTERNO")
    public String getTiStatoEsterno() {
        return this.tiStatoEsterno;
    }

    public void setTiStatoEsterno(String tiStatoEsterno) {
        this.tiStatoEsterno = tiStatoEsterno;
    }

    @Column(name = "TI_STATO_OBJECT")
    public String getTiStatoObject() {
        return tiStatoObject;
    }

    public void setTiStatoObject(String tiStatoObject) {
        this.tiStatoObject = tiStatoObject;
    }

    @Column(name = "TI_VERS_FILE")
    public String getTiVersFile() {
        return tiVersFile;
    }

    public void setTiVersFile(String tiVersFile) {
        this.tiVersFile = tiVersFile;
    }

    @Column(name = "NI_UD_PRODOTTE")
    public BigDecimal getNiUdProdotte() {
        return niUdProdotte;
    }

    public void setNiUdProdotte(BigDecimal niUdProdotte) {
        this.niUdProdotte = niUdProdotte;
    }

    @Column(name = "TI_GEST_OGGETTI_FIGLI")
    public String getTiGestOggettiFigli() {
        return tiGestOggettiFigli;
    }

    public void setTiGestOggettiFigli(String tiGestOggettiFigli) {
        this.tiGestOggettiFigli = tiGestOggettiFigli;
    }

    @Column(name = "NOTE")
    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @EmbeddedId()
    public MonVLisStatoVersId getMonVLisStatoVersId() {
        return monVLisStatoVersId;
    }

    public void setMonVLisStatoVersId(MonVLisStatoVersId monVLisStatoVersId) {
        this.monVLisStatoVersId = monVLisStatoVersId;
    }
}
