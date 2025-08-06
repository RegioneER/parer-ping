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

import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author Agati_D
 */
public class InfoCoda {

    protected long objectId;
    protected long unitaDocSessionId;
    protected String cdRegistroUnitaDocSacer;
    protected BigDecimal aaUnitaDocSacer;
    protected String cdKeyUnitaDocSacer;
    protected String messageSelector;
    protected Date sentTimestamp;
    protected String undeliveredComment;
    protected String undeliveredReason;
    protected Date undeliveredTimestamp;
    protected int deliveryCount;
    protected String messageID;

    public long getObjectId() {
	return objectId;
    }

    public void setObjectId(long objectId) {
	this.objectId = objectId;
    }

    public long getUnitaDocSessionId() {
	return unitaDocSessionId;
    }

    public void setUnitaDocSessionId(long unitaDocSessionId) {
	this.unitaDocSessionId = unitaDocSessionId;
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

    public String getMessageSelector() {
	return messageSelector;
    }

    public void setMessageSelector(String messageSelector) {
	this.messageSelector = messageSelector;
    }

    public Date getSentTimestamp() {
	return sentTimestamp;
    }

    public void setSentTimestamp(Date sentTimestamp) {
	this.sentTimestamp = sentTimestamp;
    }

    public String getUndeliveredComment() {
	return undeliveredComment;
    }

    public void setUndeliveredComment(String undeliveredComment) {
	this.undeliveredComment = undeliveredComment;
    }

    public String getUndeliveredReason() {
	return undeliveredReason;
    }

    public void setUndeliveredReason(String undeliveredReason) {
	this.undeliveredReason = undeliveredReason;
    }

    public Date getUndeliveredTimestamp() {
	return undeliveredTimestamp;
    }

    public void setUndeliveredTimestamp(Date undeliveredTimestamp) {
	this.undeliveredTimestamp = undeliveredTimestamp;
    }

    public int getDeliveryCount() {
	return deliveryCount;
    }

    public void setDeliveryCount(int deliveryCount) {
	this.deliveryCount = deliveryCount;
    }

    public String getMessageID() {
	return messageID;
    }

    public void setMessageID(String messageID) {
	this.messageID = messageID;
    }
}
