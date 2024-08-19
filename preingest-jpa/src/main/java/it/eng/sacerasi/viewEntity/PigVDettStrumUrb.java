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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * The persistent class for the PIG_V_DETT_STRUM_URB database table.
 *
 */
@Entity
@Table(name = "PIG_V_DETT_STRUM_URB")
public class PigVDettStrumUrb implements Serializable {

    private static final long serialVersionUID = 1L;
    // private String errDesc;
    private String flDeleted;
    private String flEsitoVerifica;
    private BigDecimal idDocumento;
    private BigDecimal idStrumentiUrbanistici;
    // private String nmDocumento;

    public PigVDettStrumUrb() {
    }

    /*
     * @Column(name = "ERR_DESC") public String getErrDesc() { return this.errDesc; }
     *
     * public void setErrDesc(String errDesc) { this.errDesc = errDesc; }
     */

    @Column(name = "FL_DELETED", columnDefinition = "char")
    public String getFlDeleted() {
        return this.flDeleted;
    }

    public void setFlDeleted(String flDeleted) {
        this.flDeleted = flDeleted;
    }

    @Column(name = "FL_ESITO_VERIFICA", columnDefinition = "char")
    public String getFlEsitoVerifica() {
        return this.flEsitoVerifica;
    }

    public void setFlEsitoVerifica(String flEsitoVerifica) {
        this.flEsitoVerifica = flEsitoVerifica;
    }

    @Column(name = "ID_DOCUMENTO")
    public BigDecimal getIdDocumento() {
        return this.idDocumento;
    }

    public void setIdDocumento(BigDecimal idDocumento) {
        this.idDocumento = idDocumento;
    }

    @Id
    @Column(name = "ID_STRUMENTI_URBANISTICI")
    public BigDecimal getIdStrumentiUrbanistici() {
        return this.idStrumentiUrbanistici;
    }

    public void setIdStrumentiUrbanistici(BigDecimal idStrumentiUrbanistici) {
        this.idStrumentiUrbanistici = idStrumentiUrbanistici;
    }

    /*
     * @Column(name = "NM_DOCUMENTO") public String getNmDocumento() { return this.nmDocumento; }
     *
     * public void setNmDocumento(String nmDocumento) { this.nmDocumento = nmDocumento; }
     */

}
