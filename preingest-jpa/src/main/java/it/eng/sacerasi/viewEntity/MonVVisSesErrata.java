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
 * The persistent class for the MON_V_VIS_SES_ERRATA database table.
 *
 */
@Entity
@Table(name = "MON_V_VIS_SES_ERRATA")
public class MonVVisSesErrata implements Serializable {

    private static final long serialVersionUID = 1L;
    private String blXml;
    private String cdErr;
    private String cdKeyObject;
    private String cdVersioneXmlVers;
    private String dlErr;
    private String dlMotivoChiusoWarning;
    private String dlMotivoForzaAccettazione;
    private Date dtApertura;
    private Date dtChiusura;
    private String flForzaAccettazione;
    private String flForzaWarning;
    private String flVerif;
    private BigDecimal idSessioneIngest;
    private String nmAmbienteVers;
    private String nmTipoObject;
    private String nmVers;
    private String tiStato;

    public MonVVisSesErrata() {
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

    @Column(name = "CD_VERSIONE_XML_VERS")
    public String getCdVersioneXmlVers() {
	return this.cdVersioneXmlVers;
    }

    public void setCdVersioneXmlVers(String cdVersioneXmlVers) {
	this.cdVersioneXmlVers = cdVersioneXmlVers;
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

    @Column(name = "FL_VERIF", columnDefinition = "char")
    public String getFlVerif() {
	return this.flVerif;
    }

    public void setFlVerif(String flVerif) {
	this.flVerif = flVerif;
    }

    @Id
    @Column(name = "ID_SESSIONE_INGEST")
    public BigDecimal getIdSessioneIngest() {
	return this.idSessioneIngest;
    }

    public void setIdSessioneIngest(BigDecimal idSessioneIngest) {
	this.idSessioneIngest = idSessioneIngest;
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

    @Column(name = "TI_STATO")
    public String getTiStato() {
	return this.tiStato;
    }

    public void setTiStato(String tiStato) {
	this.tiStato = tiStato;
    }
}
