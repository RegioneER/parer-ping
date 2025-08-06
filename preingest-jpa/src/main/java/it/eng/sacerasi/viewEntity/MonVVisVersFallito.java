/*
 * Engineering Ingegneria Informatica S.p.A.
 *
 * Copyright (C) 2023 Regione Emilia-Romagna <p/> This program is free software: you can
 * redistribute it and/or modify it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version. <p/> This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Affero General Public License for more details. <p/> You should
 * have received a copy of the GNU Affero General Public License along with this program. If not,
 * see <https://www.gnu.org/licenses/>.
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
 * The persistent class for the MON_V_VIS_VERS_FALLITO database table.
 *
 */
@Entity
@Table(name = "MON_V_VIS_VERS_FALLITO")
public class MonVVisVersFallito implements Serializable {

    private static final long serialVersionUID = 1L;
    private String blXml;
    private String cdErr;
    private String cdKeyObject;
    private String cdKeyObjectPadre;
    private String cdVersGen;
    private String cdVersioneXmlVers;
    private String cdWarn;
    private String dlErr;
    private String dlMotivoChiusoWarning;
    private String dlMotivoForzaAccettazione;
    private String dlWarn;
    private String dsObject;
    private Date dtApertura;
    private Date dtChiusura;
    private Date dtStatoCor;
    private String flForzaAccettazione;
    private String flForzaWarning;
    private String flNonRisolub;
    private String flVerif;
    private BigDecimal idAmbienteVers;
    private BigDecimal idObject;
    private BigDecimal idSessioneIngest;
    private BigDecimal idVers;
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
    private String nmVers;
    private String nmVersPadre;
    private BigDecimal pgOggettoTrasf;
    private String tiErr;
    private String tiGestOggettiFigli;
    private String tiStato;
    private String tiStatoRisoluz;
    private String tiStatoVerificaHash;
    private String note;
    private String cdTrasf;
    private String cdVersioneTrasf;

    public MonVVisVersFallito() {
    }

    @Lob()
    @Column(name = "BL_XML")
    public String getBlXml() {
	return this.blXml;
    }

    public void setBlXml(String blXml) {
	this.blXml = blXml;
    }

    @Column(name = "CD_ERR")
    public String getCdErr() {
	return this.cdErr;
    }

    public void setCdErr(String cdErr) {
	this.cdErr = cdErr;
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

    @Column(name = "CD_VERS_GEN")
    public String getCdVersGen() {
	return this.cdVersGen;
    }

    public void setCdVersGen(String cdVersGen) {
	this.cdVersGen = cdVersGen;
    }

    @Column(name = "CD_VERSIONE_XML_VERS")
    public String getCdVersioneXmlVers() {
	return this.cdVersioneXmlVers;
    }

    public void setCdVersioneXmlVers(String cdVersioneXmlVers) {
	this.cdVersioneXmlVers = cdVersioneXmlVers;
    }

    @Column(name = "CD_WARN")
    public String getCdWarn() {
	return this.cdWarn;
    }

    public void setCdWarn(String cdWarn) {
	this.cdWarn = cdWarn;
    }

    @Column(name = "DL_ERR")
    public String getDlErr() {
	return this.dlErr;
    }

    public void setDlErr(String dlErr) {
	this.dlErr = dlErr;
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

    @Column(name = "DL_WARN")
    public String getDlWarn() {
	return this.dlWarn;
    }

    public void setDlWarn(String dlWarn) {
	this.dlWarn = dlWarn;
    }

    @Column(name = "DS_OBJECT")
    public String getDsObject() {
	return this.dsObject;
    }

    public void setDsObject(String dsObject) {
	this.dsObject = dsObject;
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

    @Column(name = "DT_STATO_COR")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getDtStatoCor() {
	return this.dtStatoCor;
    }

    public void setDtStatoCor(Date dtStatoCor) {
	this.dtStatoCor = dtStatoCor;
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

    @Column(name = "FL_NON_RISOLUB", columnDefinition = "char")
    public String getFlNonRisolub() {
	return this.flNonRisolub;
    }

    public void setFlNonRisolub(String flNonRisolub) {
	this.flNonRisolub = flNonRisolub;
    }

    @Column(name = "FL_VERIF", columnDefinition = "char")
    public String getFlVerif() {
	return this.flVerif;
    }

    public void setFlVerif(String flVerif) {
	this.flVerif = flVerif;
    }

    @Column(name = "ID_AMBIENTE_VERS")
    public BigDecimal getIdAmbienteVers() {
	return this.idAmbienteVers;
    }

    public void setIdAmbienteVers(BigDecimal idAmbienteVers) {
	this.idAmbienteVers = idAmbienteVers;
    }

    @Column(name = "ID_OBJECT")
    public BigDecimal getIdObject() {
	return this.idObject;
    }

    public void setIdObject(BigDecimal idObject) {
	this.idObject = idObject;
    }

    @Id
    @Column(name = "ID_SESSIONE_INGEST")
    public BigDecimal getIdSessioneIngest() {
	return this.idSessioneIngest;
    }

    public void setIdSessioneIngest(BigDecimal idSessioneIngest) {
	this.idSessioneIngest = idSessioneIngest;
    }

    @Column(name = "ID_VERS")
    public BigDecimal getIdVers() {
	return this.idVers;
    }

    public void setIdVers(BigDecimal idVers) {
	this.idVers = idVers;
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

    @Column(name = "NOTE")
    public String getNote() {
	return this.note;
    }

    public void setNote(String note) {
	this.note = note;
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

    @Column(name = "TI_ERR")
    public String getTiErr() {
	return this.tiErr;
    }

    public void setTiErr(String tiErr) {
	this.tiErr = tiErr;
    }

    @Column(name = "TI_GEST_OGGETTI_FIGLI")
    public String getTiGestOggettiFigli() {
	return tiGestOggettiFigli;
    }

    public void setTiGestOggettiFigli(String tiGestOggettiFigli) {
	this.tiGestOggettiFigli = tiGestOggettiFigli;
    }

    @Column(name = "TI_STATO")
    public String getTiStato() {
	return this.tiStato;
    }

    public void setTiStato(String tiStato) {
	this.tiStato = tiStato;
    }

    @Column(name = "TI_STATO_RISOLUZ")
    public String getTiStatoRisoluz() {
	return this.tiStatoRisoluz;
    }

    public void setTiStatoRisoluz(String tiStatoRisoluz) {
	this.tiStatoRisoluz = tiStatoRisoluz;
    }

    @Column(name = "TI_STATO_VERIFICA_HASH")
    public String getTiStatoVerificaHash() {
	return this.tiStatoVerificaHash;
    }

    public void setTiStatoVerificaHash(String tiStatoVerificaHash) {
	this.tiStatoVerificaHash = tiStatoVerificaHash;
    }

    @Column(name = "CD_TRASF")
    public String getCdTrasf() {
	return this.cdTrasf;
    }

    public void setCdTrasf(String cdTrasf) {
	this.cdTrasf = cdTrasf;
    }

    @Column(name = "CD_VERSIONE_TRASF")
    public String getCdVersioneTrasf() {
	return this.cdVersioneTrasf;
    }

    public void setCdVersioneTrasf(String cdVersioneTrasf) {
	this.cdVersioneTrasf = cdVersioneTrasf;
    }

}
