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
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * The persistent class for the PIG_V_LIS_CHK_UD_SES_ERR database table.
 *
 */
@Entity
@Table(name = "PIG_V_LIS_CHK_UD_SES_ERR")
@NamedQuery(name = "PigVLisChkUdSesErr.findAll", query = "SELECT p FROM PigVLisChkUdSesErr p")
public class PigVLisChkUdSesErr implements Serializable {

    private static final long serialVersionUID = 1L;
    // private String cdErr;
    private String dsErr;
    private String flUnitaDocErr;
    private BigDecimal idSessioneIngest;
    private BigDecimal idUnitaDocSessione;

    public PigVLisChkUdSesErr() {
    }

    /*
     * @Column(name = "CD_ERR") public String getCdErr() { return this.cdErr; }
     *
     * public void setCdErr(String cdErr) { this.cdErr = cdErr; }
     */

    @Column(name = "DS_ERR")
    public String getDsErr() {
        return this.dsErr;
    }

    public void setDsErr(String dsErr) {
        this.dsErr = dsErr;
    }

    @Column(name = "FL_UNITA_DOC_ERR", columnDefinition = "char")
    public String getFlUnitaDocErr() {
        return this.flUnitaDocErr;
    }

    public void setFlUnitaDocErr(String flUnitaDocErr) {
        this.flUnitaDocErr = flUnitaDocErr;
    }

    @Column(name = "ID_SESSIONE_INGEST")
    public BigDecimal getIdSessioneIngest() {
        return this.idSessioneIngest;
    }

    public void setIdSessioneIngest(BigDecimal idSessioneIngest) {
        this.idSessioneIngest = idSessioneIngest;
    }

    @Id
    @Column(name = "ID_UNITA_DOC_SESSIONE")
    public BigDecimal getIdUnitaDocSessione() {
        return this.idUnitaDocSessione;
    }

    public void setIdUnitaDocSessione(BigDecimal idUnitaDocSessione) {
        this.idUnitaDocSessione = idUnitaDocSessione;
    }

}
