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
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * The persistent class for the MON_V_LIS_OBJ database table.
 *
 */
@Entity
@Table(name = "MON_V_LIS_OBJ")
public class MonVLisObj implements Serializable {

    private static final long serialVersionUID = 1L;
    private BigDecimal aaUnitaDocSacer;
    private String cdKeyObject;
    private String cdKeyUnitaDocSacer;
    private String cdRegistroUnitaDocSacer;
    private String dsInfoObject;
    private String dsKeyOrd;
    private Date dtStatoCor;
    private Date dtVers;
    private BigDecimal idAmbienteVers;
    private BigDecimal idObject;
    private BigDecimal idTipoObject;
    private BigDecimal idVers;
    private String nmAmbienteVers;
    private String nmTipoObject;
    private String nmVers;
    private String tiDtCreazione;
    private String tiStatoObject;
    private String tiStatoObjectVis;
    private String tiStatoVerificaHash;
    private String tiVersFile;
    private String trasformazioneUtilizzata;
    private BigDecimal niSizeFileVers;
    private String note;
    private BigDecimal niUdProdotte;
    private String tiGestOggettiFigli;

    public MonVLisObj() {
    }

    public MonVLisObj(String nmAmbienteVers, String nmVers, BigDecimal idObject, String tiStatoObject,
            String tiStatoObjectVis, String tiStatoVerificaHash, String cdKeyObject, Date dtVers, String dsInfoObject,
            Date dtStatoCor, BigDecimal niSizeFileVers, String nmTipoObject, String note,
            String trasformazioneUtilizzata, BigDecimal niUdProdotte, String tiVersFile, String dsKeyOrd) {
        this(nmAmbienteVers, nmVers, idObject, tiStatoObject, tiStatoObjectVis, tiStatoVerificaHash, cdKeyObject,
                dtVers, dsInfoObject, dtStatoCor, niSizeFileVers, nmTipoObject, note, trasformazioneUtilizzata,
                niUdProdotte, tiVersFile, dsKeyOrd, null);
    }

    public MonVLisObj(String nmAmbienteVers, String nmVers, BigDecimal idObject, String tiStatoObject,
            String tiStatoObjectVis, String tiStatoVerificaHash, String cdKeyObject, Date dtVers, String dsInfoObject,
            Date dtStatoCor, BigDecimal niSizeFileVers, String nmTipoObject, String note,
            String trasformazioneUtilizzata, BigDecimal niUdProdotte, String tiVersFile, String dsKeyOrd,
            String tiGestOggettiFigli) {
        this.nmAmbienteVers = nmAmbienteVers;
        this.nmVers = nmVers;
        this.idObject = idObject;
        this.tiStatoObject = tiStatoObject;
        this.tiStatoObjectVis = tiStatoObjectVis;
        this.tiStatoVerificaHash = tiStatoVerificaHash;
        this.cdKeyObject = cdKeyObject;
        this.dtVers = dtVers;
        this.dsInfoObject = dsInfoObject;
        this.dtStatoCor = dtStatoCor;
        this.niSizeFileVers = niSizeFileVers;
        this.nmTipoObject = nmTipoObject;
        this.note = note;
        this.trasformazioneUtilizzata = trasformazioneUtilizzata;
        this.niUdProdotte = niUdProdotte;
        this.tiVersFile = tiVersFile;
        this.dsKeyOrd = dsKeyOrd;
        this.tiGestOggettiFigli = tiGestOggettiFigli;
    }

    @Column(name = "AA_UNITA_DOC_SACER")
    public BigDecimal getAaUnitaDocSacer() {
        return this.aaUnitaDocSacer;
    }

    public void setAaUnitaDocSacer(BigDecimal aaUnitaDocSacer) {
        this.aaUnitaDocSacer = aaUnitaDocSacer;
    }

    @Column(name = "CD_KEY_OBJECT")
    public String getCdKeyObject() {
        return this.cdKeyObject;
    }

    public void setCdKeyObject(String cdKeyObject) {
        this.cdKeyObject = cdKeyObject;
    }

    @Column(name = "CD_KEY_UNITA_DOC_SACER")
    public String getCdKeyUnitaDocSacer() {
        return this.cdKeyUnitaDocSacer;
    }

    public void setCdKeyUnitaDocSacer(String cdKeyUnitaDocSacer) {
        this.cdKeyUnitaDocSacer = cdKeyUnitaDocSacer;
    }

    @Column(name = "CD_REGISTRO_UNITA_DOC_SACER")
    public String getCdRegistroUnitaDocSacer() {
        return this.cdRegistroUnitaDocSacer;
    }

    public void setCdRegistroUnitaDocSacer(String cdRegistroUnitaDocSacer) {
        this.cdRegistroUnitaDocSacer = cdRegistroUnitaDocSacer;
    }

    @Column(name = "DS_INFO_OBJECT")
    public String getDsInfoObject() {
        return this.dsInfoObject;
    }

    public void setDsInfoObject(String dsInfoObject) {
        this.dsInfoObject = dsInfoObject;
    }

    @Column(name = "DS_KEY_ORD")
    public String getDsKeyOrd() {
        return this.dsKeyOrd;
    }

    public void setDsKeyOrd(String dsKeyOrd) {
        this.dsKeyOrd = dsKeyOrd;
    }

    @Column(name = "DT_STATO_COR")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getDtStatoCor() {
        return this.dtStatoCor;
    }

    public void setDtStatoCor(Date dtStatoCor) {
        this.dtStatoCor = dtStatoCor;
    }

    @Column(name = "DT_VERS")
    @Temporal(TemporalType.TIMESTAMP)
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

    @Column(name = "NM_VERS")
    public String getNmVers() {
        return this.nmVers;
    }

    public void setNmVers(String nmVers) {
        this.nmVers = nmVers;
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

    @Column(name = "TI_STATO_OBJECT_VIS")
    public String getTiStatoObjectVis() {
        return this.tiStatoObjectVis;
    }

    public void setTiStatoObjectVis(String tiStatoObjectVis) {
        this.tiStatoObjectVis = tiStatoObjectVis;
    }

    @Column(name = "TI_STATO_VERIFICA_HASH")
    public String getTiStatoVerificaHash() {
        return this.tiStatoVerificaHash;
    }

    public void setTiStatoVerificaHash(String tiStatoVerificaHash) {
        this.tiStatoVerificaHash = tiStatoVerificaHash;
    }

    @Column(name = "TI_VERS_FILE")
    public String getTiVersFile() {
        return this.tiVersFile;
    }

    public void setTiVersFile(String tiVersFile) {
        this.tiVersFile = tiVersFile;
    }

    @Column(name = "TRASFORMAZIONE_UTILIZZATA")
    public String getTrasformazioneUtilizzata() {
        return this.trasformazioneUtilizzata;
    }

    public void setTrasformazioneUtilizzata(String trasformazioneUtilizzata) {
        this.trasformazioneUtilizzata = trasformazioneUtilizzata;
    }

    @Column(name = "NI_SIZE_FILE_VERS")
    public BigDecimal getNiSizeFileVers() {
        return this.niSizeFileVers;
    }

    public void setNiSizeFileVers(BigDecimal niSizeFileVers) {
        this.niSizeFileVers = niSizeFileVers;
    }

    @Column(name = "NOTE")
    public String getNote() {
        return this.note;
    }

    public void setNote(String note) {
        this.note = note;
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
}
