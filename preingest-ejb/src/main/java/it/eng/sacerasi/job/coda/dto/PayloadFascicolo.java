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
 * @author Cappelli_F
 */
public class PayloadFascicolo implements Serializable {

    private static final long serialVersionUID = 8498807903884396903L;
    private long objectId;
    private BigDecimal sessionId;
    private long fascicoloId;
    private long fascicoloSessionId;
    private BigDecimal aaFascicoloSacer;
    private String cdKeyFascicoloSacer;
    private String urlServVersamento;
    private String userIdSacer;
    private String passwordSacer;

    public long getObjectId() {
        return objectId;
    }

    public void setObjectId(long objectId) {
        this.objectId = objectId;
    }

    public BigDecimal getSessionId() {
        return sessionId;
    }

    public void setSessionId(BigDecimal sessionId) {
        this.sessionId = sessionId;
    }

    public String getUrlServVersamento() {
        return urlServVersamento;
    }

    public void setUrlServVersamento(String urlServVersamento) {
        this.urlServVersamento = urlServVersamento;
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

    public long getFascicoloId() {
        return this.fascicoloId;
    }

    public void setFascicoloId(long fascicoloId) {
        this.fascicoloId = fascicoloId;
    }

    public long getFascicoloSessionId() {
        return this.fascicoloSessionId;
    }

    public void setFascicoloSessionId(long fascicoloSessionId) {
        this.fascicoloSessionId = fascicoloSessionId;
    }

    public BigDecimal getAaFascicoloSacer() {
        return this.aaFascicoloSacer;
    }

    public void setAaFascicoloSacer(BigDecimal aaFascicoloSacer) {
        this.aaFascicoloSacer = aaFascicoloSacer;
    }

    public String getCdKeyFascicoloSacer() {
        return this.cdKeyFascicoloSacer;
    }

    public void setCdKeyFascicoloSacer(String cdKeyFascicoloSacer) {
        this.cdKeyFascicoloSacer = cdKeyFascicoloSacer;
    }
}
