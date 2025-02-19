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
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * The persistent class for the MON_V_VIS_OBJ database table.
 *
 */
@Entity
@Table(name = "MON_V_VIS_OBJ")
public class MonVVisObj implements Serializable {

    private static final long serialVersionUID = 1L;
    private String blXml;
    private String cdKeyObject;
    private String cdKeyObjectPadre;
    private String cdTrasf;
    private String cdVersGen;
    private String cdVersioneTrasf;
    private String cdVersioneXmlVers;
    private String dlMotivoChiusoWarning;
    private String dlMotivoForzaAccettazione;
    private String dsInfoObject;
    private String dsObjectPadre;
    private String note;
    private Date dtApertura;
    private Date dtChiusura;
    private Date dtStatoCorPadre;
    private String flForzaAccettazione;
    private String flForzaWarning;
    private String flRichAnnulTimeout;
    private BigDecimal idAmbienteVers;
    private BigDecimal idAmbienteVersPadre;
    private BigDecimal idLastSessioneIngest;
    private BigDecimal idObject;
    private BigDecimal idObjectPadre;
    private BigDecimal idObjectTrasf;
    private BigDecimal idTipoObject;
    private BigDecimal idTipoObjectPadre;
    private BigDecimal idVers;
    private BigDecimal idVersPadre;
    private BigDecimal niTotObjectTrasf;
    private BigDecimal niUnitaDocAttese;
    private BigDecimal niUnitaDocDaVers;
    private BigDecimal niUnitaDocVers;
    private BigDecimal niUnitaDocVersErr;
    private BigDecimal niUnitaDocVersOk;
    private BigDecimal niUnitaDocVersTimeout;
    private String nmAmbienteVers;
    private String nmAmbienteVersPadre;
    private String nmTipoObject;
    private String nmTipoObjectPadre;
    private String nmVers;
    private String nmVersPadre;
    private BigDecimal pgOggettoTrasf;
    private String tiGestOggettiFigli;
    private String tiStatoObject;
    private String tiStatoObjectPadre;
    private String tiStatoVerificaHash;
    private String tiVersFile;
    private String tiPriorita;
    private String tiPrioritaVersamento;
    private String nmUseridVers;

    public MonVVisObj() {
    }

    @Lob()
    @Column(name = "BL_XML")
    public String getBlXml() {
        return this.blXml;
    }

    public void setBlXml(String blXml) {
        this.blXml = blXml;
    }

    @Column(name = "CD_KEY_OBJECT")
    public String getCdKeyObject() {
        return this.cdKeyObject;
    }

    public void setCdKeyObject(String cdKeyObject) {
        this.cdKeyObject = cdKeyObject;
    }

    @Column(name = "CD_KEY_OBJECT_PADRE")
    public String getCdKeyObjectPadre() {
        return this.cdKeyObjectPadre;
    }

    public void setCdKeyObjectPadre(String cdKeyObjectPadre) {
        this.cdKeyObjectPadre = cdKeyObjectPadre;
    }

    @Column(name = "CD_TRASF")
    public String getCdTrasf() {
        return this.cdTrasf;
    }

    public void setCdTrasf(String cdTrasf) {
        this.cdTrasf = cdTrasf;
    }

    @Column(name = "CD_VERS_GEN")
    public String getCdVersGen() {
        return this.cdVersGen;
    }

    public void setCdVersGen(String cdVersGen) {
        this.cdVersGen = cdVersGen;
    }

    @Column(name = "CD_VERSIONE_TRASF")
    public String getCdVersioneTrasf() {
        return this.cdVersioneTrasf;
    }

    public void setCdVersioneTrasf(String cdVersioneTrasf) {
        this.cdVersioneTrasf = cdVersioneTrasf;
    }

    @Column(name = "CD_VERSIONE_XML_VERS")
    public String getCdVersioneXmlVers() {
        return this.cdVersioneXmlVers;
    }

    public void setCdVersioneXmlVers(String cdVersioneXmlVers) {
        this.cdVersioneXmlVers = cdVersioneXmlVers;
    }

    @Column(name = "NOTE")
    public String getNote() {
        return this.note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @Column(name = "DL_MOTIVO_CHIUSO_WARNING")
    public String getDlMotivoChiusoWarning() {
        return this.dlMotivoChiusoWarning;
    }

    public void setDlMotivoChiusoWarning(String dlMotivoChiusoWarning) {
        this.dlMotivoChiusoWarning = dlMotivoChiusoWarning;
    }

    @Column(name = "DL_MOTIVO_FORZA_ACCETTAZIONE")
    public String getDlMotivoForzaAccettazione() {
        return this.dlMotivoForzaAccettazione;
    }

    public void setDlMotivoForzaAccettazione(String dlMotivoForzaAccettazione) {
        this.dlMotivoForzaAccettazione = dlMotivoForzaAccettazione;
    }

    @Column(name = "DS_INFO_OBJECT")
    public String getDsInfoObject() {
        return this.dsInfoObject;
    }

    public void setDsInfoObject(String dsInfoObject) {
        this.dsInfoObject = dsInfoObject;
    }

    @Column(name = "DS_OBJECT_PADRE")
    public String getDsObjectPadre() {
        return this.dsObjectPadre;
    }

    public void setDsObjectPadre(String dsObjectPadre) {
        this.dsObjectPadre = dsObjectPadre;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_APERTURA")
    public Date getDtApertura() {
        return this.dtApertura;
    }

    public void setDtApertura(Date dtApertura) {
        this.dtApertura = dtApertura;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_CHIUSURA")
    public Date getDtChiusura() {
        return this.dtChiusura;
    }

    public void setDtChiusura(Date dtChiusura) {
        this.dtChiusura = dtChiusura;
    }

    @Column(name = "DT_STATO_COR_PADRE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getDtStatoCorPadre() {
        return this.dtStatoCorPadre;
    }

    public void setDtStatoCorPadre(Date dtStatoCorPadre) {
        this.dtStatoCorPadre = dtStatoCorPadre;
    }

    @Column(name = "FL_FORZA_ACCETTAZIONE", columnDefinition = "char")
    public String getFlForzaAccettazione() {
        return this.flForzaAccettazione;
    }

    public void setFlForzaAccettazione(String flForzaAccettazione) {
        this.flForzaAccettazione = flForzaAccettazione;
    }

    @Column(name = "FL_FORZA_WARNING", columnDefinition = "char")
    public String getFlForzaWarning() {
        return this.flForzaWarning;
    }

    public void setFlForzaWarning(String flForzaWarning) {
        this.flForzaWarning = flForzaWarning;
    }

    @Column(name = "FL_RICH_ANNUL_TIMEOUT", columnDefinition = "char")
    public String getFlRichAnnulTimeout() {
        return this.flRichAnnulTimeout;
    }

    public void setFlRichAnnulTimeout(String flRichAnnulTimeout) {
        this.flRichAnnulTimeout = flRichAnnulTimeout;
    }

    @Column(name = "ID_AMBIENTE_VERS")
    public BigDecimal getIdAmbienteVers() {
        return this.idAmbienteVers;
    }

    public void setIdAmbienteVers(BigDecimal idAmbienteVers) {
        this.idAmbienteVers = idAmbienteVers;
    }

    @Column(name = "ID_AMBIENTE_VERS_PADRE")
    public BigDecimal getIdAmbienteVersPadre() {
        return this.idAmbienteVersPadre;
    }

    public void setIdAmbienteVersPadre(BigDecimal idAmbienteVersPadre) {
        this.idAmbienteVersPadre = idAmbienteVersPadre;
    }

    @Column(name = "ID_LAST_SESSIONE_INGEST")
    public BigDecimal getIdLastSessioneIngest() {
        return idLastSessioneIngest;
    }

    public void setIdLastSessioneIngest(BigDecimal idLastSessioneIngest) {
        this.idLastSessioneIngest = idLastSessioneIngest;
    }

    @Id
    @Column(name = "ID_OBJECT")
    public BigDecimal getIdObject() {
        return this.idObject;
    }

    public void setIdObject(BigDecimal idObject) {
        this.idObject = idObject;
    }

    @Column(name = "ID_OBJECT_PADRE")
    public BigDecimal getIdObjectPadre() {
        return this.idObjectPadre;
    }

    public void setIdObjectPadre(BigDecimal idObjectPadre) {
        this.idObjectPadre = idObjectPadre;
    }

    @Column(name = "ID_OBJECT_TRASF")
    public BigDecimal getIdObjectTrasf() {
        return this.idObjectTrasf;
    }

    public void setIdObjectTrasf(BigDecimal idObjectTrasf) {
        this.idObjectTrasf = idObjectTrasf;
    }

    @Column(name = "ID_TIPO_OBJECT")
    public BigDecimal getIdTipoObject() {
        return this.idTipoObject;
    }

    public void setIdTipoObject(BigDecimal idTipoObject) {
        this.idTipoObject = idTipoObject;
    }

    @Column(name = "ID_TIPO_OBJECT_PADRE")
    public BigDecimal getIdTipoObjectPadre() {
        return this.idTipoObjectPadre;
    }

    public void setIdTipoObjectPadre(BigDecimal idTipoObjectPadre) {
        this.idTipoObjectPadre = idTipoObjectPadre;
    }

    @Column(name = "ID_VERS")
    public BigDecimal getIdVers() {
        return this.idVers;
    }

    public void setIdVers(BigDecimal idVers) {
        this.idVers = idVers;
    }

    @Column(name = "ID_VERS_PADRE")
    public BigDecimal getIdVersPadre() {
        return this.idVersPadre;
    }

    public void setIdVersPadre(BigDecimal idVersPadre) {
        this.idVersPadre = idVersPadre;
    }

    @Column(name = "NI_TOT_OBJECT_TRASF")
    public BigDecimal getNiTotObjectTrasf() {
        return this.niTotObjectTrasf;
    }

    public void setNiTotObjectTrasf(BigDecimal niTotObjectTrasf) {
        this.niTotObjectTrasf = niTotObjectTrasf;
    }

    @Column(name = "NI_UNITA_DOC_ATTESE")
    public BigDecimal getNiUnitaDocAttese() {
        return this.niUnitaDocAttese;
    }

    public void setNiUnitaDocAttese(BigDecimal niUnitaDocAttese) {
        this.niUnitaDocAttese = niUnitaDocAttese;
    }

    @Column(name = "NI_UNITA_DOC_DA_VERS")
    public BigDecimal getNiUnitaDocDaVers() {
        return this.niUnitaDocDaVers;
    }

    public void setNiUnitaDocDaVers(BigDecimal niUnitaDocDaVers) {
        this.niUnitaDocDaVers = niUnitaDocDaVers;
    }

    @Column(name = "NI_UNITA_DOC_VERS")
    public BigDecimal getNiUnitaDocVers() {
        return this.niUnitaDocVers;
    }

    public void setNiUnitaDocVers(BigDecimal niUnitaDocVers) {
        this.niUnitaDocVers = niUnitaDocVers;
    }

    @Column(name = "NI_UNITA_DOC_VERS_ERR")
    public BigDecimal getNiUnitaDocVersErr() {
        return this.niUnitaDocVersErr;
    }

    public void setNiUnitaDocVersErr(BigDecimal niUnitaDocVersErr) {
        this.niUnitaDocVersErr = niUnitaDocVersErr;
    }

    @Column(name = "NI_UNITA_DOC_VERS_OK")
    public BigDecimal getNiUnitaDocVersOk() {
        return this.niUnitaDocVersOk;
    }

    public void setNiUnitaDocVersOk(BigDecimal niUnitaDocVersOk) {
        this.niUnitaDocVersOk = niUnitaDocVersOk;
    }

    @Column(name = "NI_UNITA_DOC_VERS_TIMEOUT")
    public BigDecimal getNiUnitaDocVersTimeout() {
        return this.niUnitaDocVersTimeout;
    }

    public void setNiUnitaDocVersTimeout(BigDecimal niUnitaDocVersTimeout) {
        this.niUnitaDocVersTimeout = niUnitaDocVersTimeout;
    }

    @Column(name = "NM_AMBIENTE_VERS")
    public String getNmAmbienteVers() {
        return this.nmAmbienteVers;
    }

    public void setNmAmbienteVers(String nmAmbienteVers) {
        this.nmAmbienteVers = nmAmbienteVers;
    }

    @Column(name = "NM_AMBIENTE_VERS_PADRE")
    public String getNmAmbienteVersPadre() {
        return this.nmAmbienteVersPadre;
    }

    public void setNmAmbienteVersPadre(String nmAmbienteVersPadre) {
        this.nmAmbienteVersPadre = nmAmbienteVersPadre;
    }

    @Column(name = "NM_TIPO_OBJECT")
    public String getNmTipoObject() {
        return this.nmTipoObject;
    }

    public void setNmTipoObject(String nmTipoObject) {
        this.nmTipoObject = nmTipoObject;
    }

    @Column(name = "NM_TIPO_OBJECT_PADRE")
    public String getNmTipoObjectPadre() {
        return this.nmTipoObjectPadre;
    }

    public void setNmTipoObjectPadre(String nmTipoObjectPadre) {
        this.nmTipoObjectPadre = nmTipoObjectPadre;
    }

    @Column(name = "NM_VERS")
    public String getNmVers() {
        return this.nmVers;
    }

    public void setNmVers(String nmVers) {
        this.nmVers = nmVers;
    }

    @Column(name = "NM_VERS_PADRE")
    public String getNmVersPadre() {
        return this.nmVersPadre;
    }

    public void setNmVersPadre(String nmVersPadre) {
        this.nmVersPadre = nmVersPadre;
    }

    @Column(name = "PG_OGGETTO_TRASF")
    public BigDecimal getPgOggettoTrasf() {
        return this.pgOggettoTrasf;
    }

    public void setPgOggettoTrasf(BigDecimal pgOggettoTrasf) {
        this.pgOggettoTrasf = pgOggettoTrasf;
    }

    @Column(name = "TI_GEST_OGGETTI_FIGLI")
    public String getTiGestOggettiFigli() {
        return tiGestOggettiFigli;
    }

    public void setTiGestOggettiFigli(String tiGestOggettiFigli) {
        this.tiGestOggettiFigli = tiGestOggettiFigli;
    }

    @Column(name = "TI_STATO_OBJECT")
    public String getTiStatoObject() {
        return this.tiStatoObject;
    }

    public void setTiStatoObject(String tiStatoObject) {
        this.tiStatoObject = tiStatoObject;
    }

    @Column(name = "TI_STATO_OBJECT_PADRE")
    public String getTiStatoObjectPadre() {
        return this.tiStatoObjectPadre;
    }

    public void setTiStatoObjectPadre(String tiStatoObjectPadre) {
        this.tiStatoObjectPadre = tiStatoObjectPadre;
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

    @Column(name = "TI_PRIORITA")
    public String getTiPriorita() {
        return tiPriorita;
    }

    public void setTiPriorita(String tiPriorita) {
        this.tiPriorita = tiPriorita;
    }

    @Column(name = "TI_PRIORITA_VERSAMENTO")
    public String getTiPrioritaVersamento() {
        return tiPrioritaVersamento;
    }

    public void setTiPrioritaVersamento(String tiPrioritaVersamento) {
        this.tiPrioritaVersamento = tiPrioritaVersamento;
    }

    @Column(name = "NM_USERID_VERS")
    public String getNmUseridVers() {
        return nmUseridVers;
    }

    public void setNmUseridVers(String nmUseridVers) {
        this.nmUseridVers = nmUseridVers;
    }
}
