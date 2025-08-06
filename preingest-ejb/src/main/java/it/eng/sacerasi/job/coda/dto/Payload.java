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

package it.eng.sacerasi.job.coda.dto;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 *
 * @author Agati_D
 */
public class Payload implements Serializable {

    private static final long serialVersionUID = 8498807903884396903L;
    private BigDecimal sessionId;
    private long objectId;
    private long unitaDocId;
    private long unitaDocSessionId;
    private String cdRegistroUnitaDocSacer;
    private BigDecimal aaUnitaDocSacer;
    private String cdKeyUnitaDocSacer;
    private String urlServVersamento;
    private long xmlVersamentoSacerId;
    private long xmlIndiceId;
    // private String xmlVersamentoSacer;
    // private String xmlIndice;
    private String userIdSacer;
    private String passwordSacer;

    public BigDecimal getSessionId() {
	return sessionId;
    }

    public void setSessionId(BigDecimal sessionId) {
	this.sessionId = sessionId;
    }

    public long getObjectId() {
	return objectId;
    }

    public void setObjectId(long objectId) {
	this.objectId = objectId;
    }

    public long getUnitaDocId() {
	return unitaDocId;
    }

    public void setUnitaDocId(long unitaDocId) {
	this.unitaDocId = unitaDocId;
    }

    public String getCdRegistroUnitaDocSacer() {
	return cdRegistroUnitaDocSacer;
    }

    public void setCdRegistroUnitaDocSacer(String cdRegistroUnitaDocSacer) {
	this.cdRegistroUnitaDocSacer = cdRegistroUnitaDocSacer;
    }

    public BigDecimal getAaUnitaDocSacer() {
	return aaUnitaDocSacer;
    }

    public void setAaUnitaDocSacer(BigDecimal aaUnitaDocSacer) {
	this.aaUnitaDocSacer = aaUnitaDocSacer;
    }

    public String getCdKeyUnitaDocSacer() {
	return cdKeyUnitaDocSacer;
    }

    public void setCdKeyUnitaDocSacer(String cdKeyUnitaDocSacer) {
	this.cdKeyUnitaDocSacer = cdKeyUnitaDocSacer;
    }

    public String getUrlServVersamento() {
	return urlServVersamento;
    }

    public void setUrlServVersamento(String urlServVersamento) {
	this.urlServVersamento = urlServVersamento;
    }

    public long getXmlVersamentoSacerId() {
	return xmlVersamentoSacerId;
    }

    public void setXmlVersamentoSacerId(long xmlVersamentoSacerId) {
	this.xmlVersamentoSacerId = xmlVersamentoSacerId;
    }

    public long getXmlIndiceId() {
	return xmlIndiceId;
    }

    public void setXmlIndiceId(long xmlIndiceId) {
	this.xmlIndiceId = xmlIndiceId;
    }

    public String getUserIdSacer() {
	return userIdSacer;
    }

    public void setUserIdSacer(String userIdSacer) {
	this.userIdSacer = userIdSacer;
    }

    public String getPasswordSacer() {
	return passwordSacer;
    }

    public void setPasswordSacer(String passwordSacer) {
	this.passwordSacer = passwordSacer;
    }

    public long getUnitaDocSessionId() {
	return unitaDocSessionId;
    }

    public void setUnitaDocSessionId(long unitaDocSessionId) {
	this.unitaDocSessionId = unitaDocSessionId;
    }

    @Override
    public String toString() {
	return "Payload{" + "sessionId=" + sessionId + "\n objectId=" + objectId + "\n unitaDocId="
		+ unitaDocId + "\n unitaDocSessionId=" + unitaDocSessionId
		+ "\n cdRegistroUnitaDocSacer=" + cdRegistroUnitaDocSacer + "\n aaUnitaDocSacer="
		+ aaUnitaDocSacer + "\n cdKeyUnitaDocSacer=" + cdKeyUnitaDocSacer
		+ "\n urlServVersamento=" + urlServVersamento + "\n xmlVersamentoSacerId="
		+ xmlVersamentoSacerId + "\n xmlIndiceId=" + xmlIndiceId + "\n userIdSacer="
		+ userIdSacer + "\n passwordSacer=" + passwordSacer + '}';
    }

}
