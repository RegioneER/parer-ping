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
 * The persistent class for the PIG_V_SISMA_CHECKS database table.
 *
 */
@Entity
@Table(name = "PIG_V_SISMA_CHECKS")
public class PigVSismaChecks implements Serializable {

    private static final long serialVersionUID = 1L;
    private String flFileMancante;
    private String flVerificaErrata;
    private String flVerificaInCorso;
    private BigDecimal idSisma;

    public PigVSismaChecks() {
    }

    @Column(name = "FL_FILE_MANCANTE", columnDefinition = "CHAR")
    public String getFlFileMancante() {
        return this.flFileMancante;
    }

    public void setFlFileMancante(String flFileMancante) {
        this.flFileMancante = flFileMancante;
    }

    @Column(name = "FL_VERIFICA_ERRATA", columnDefinition = "CHAR")
    public String getFlVerificaErrata() {
        return this.flVerificaErrata;
    }

    public void setFlVerificaErrata(String flVerificaErrata) {
        this.flVerificaErrata = flVerificaErrata;
    }

    @Column(name = "FL_VERIFICA_IN_CORSO", columnDefinition = "CHAR")
    public String getFlVerificaInCorso() {
        return this.flVerificaInCorso;
    }

    public void setFlVerificaInCorso(String flVerificaInCorso) {
        this.flVerificaInCorso = flVerificaInCorso;
    }

    @Id
    @Column(name = "ID_SISMA")
    public BigDecimal getIdSisma() {
        return this.idSisma;
    }

    public void setIdSisma(BigDecimal idSisma) {
        this.idSisma = idSisma;
    }

}
