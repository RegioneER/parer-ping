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

package it.eng.sacerasi.job.coda.dto;

/**
 *
 * @author sinatti_s
 */
public class InfoCodaExt extends InfoCoda {

    private int countMsg = 1; // default

    public int getCountMsg() {
        return countMsg;
    }

    public void incCountMsg() {
        this.countMsg = this.countMsg + 1;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((aaUnitaDocSacer == null) ? 0 : aaUnitaDocSacer.hashCode());
        result = prime * result + ((cdKeyUnitaDocSacer == null) ? 0 : cdKeyUnitaDocSacer.hashCode());
        result = prime * result + ((cdRegistroUnitaDocSacer == null) ? 0 : cdRegistroUnitaDocSacer.hashCode());
        result = prime * result + deliveryCount;
        result = prime * result + ((messageID == null) ? 0 : messageID.hashCode());
        result = prime * result + ((messageSelector == null) ? 0 : messageSelector.hashCode());
        result = prime * result + (int) (objectId ^ (objectId >>> 32));
        result = prime * result + ((sentTimestamp == null) ? 0 : sentTimestamp.hashCode());
        result = prime * result + ((undeliveredComment == null) ? 0 : undeliveredComment.hashCode());
        result = prime * result + ((undeliveredReason == null) ? 0 : undeliveredReason.hashCode());
        result = prime * result + ((undeliveredTimestamp == null) ? 0 : undeliveredTimestamp.hashCode());
        result = prime * result + (int) (unitaDocSessionId ^ (unitaDocSessionId >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        InfoCoda other = (InfoCoda) obj;
        if (aaUnitaDocSacer == null) {
            if (other.aaUnitaDocSacer != null)
                return false;
        } else if (!aaUnitaDocSacer.equals(other.aaUnitaDocSacer))
            return false;
        if (cdKeyUnitaDocSacer == null) {
            if (other.cdKeyUnitaDocSacer != null)
                return false;
        } else if (!cdKeyUnitaDocSacer.equals(other.cdKeyUnitaDocSacer))
            return false;
        if (cdRegistroUnitaDocSacer == null) {
            if (other.cdRegistroUnitaDocSacer != null)
                return false;
        } else if (!cdRegistroUnitaDocSacer.equals(other.cdRegistroUnitaDocSacer))
            return false;
        if (deliveryCount != other.deliveryCount)
            return false;
        if (messageID == null) {
            if (other.messageID != null)
                return false;
        } else if (!messageID.equals(other.messageID))
            return false;
        if (messageSelector == null) {
            if (other.messageSelector != null)
                return false;
        } else if (!messageSelector.equals(other.messageSelector))
            return false;
        if (objectId != other.objectId)
            return false;
        if (sentTimestamp == null) {
            if (other.sentTimestamp != null)
                return false;
        } else if (!sentTimestamp.equals(other.sentTimestamp))
            return false;
        if (undeliveredComment == null) {
            if (other.undeliveredComment != null)
                return false;
        } else if (!undeliveredComment.equals(other.undeliveredComment))
            return false;
        if (undeliveredReason == null) {
            if (other.undeliveredReason != null)
                return false;
        } else if (!undeliveredReason.equals(other.undeliveredReason))
            return false;
        if (undeliveredTimestamp == null) {
            if (other.undeliveredTimestamp != null)
                return false;
        } else if (!undeliveredTimestamp.equals(other.undeliveredTimestamp))
            return false;

        return unitaDocSessionId == other.unitaDocSessionId;
    }

}
