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

package it.eng.sacerasi.web.dto;

import java.math.BigDecimal;

/**
 *
 * @author Gilioli_P
 */
public class MonitoraggioFiltriListaOggDerVersFallitiBean {

    private BigDecimal idAmbienteVers;
    private BigDecimal idVers;
    private BigDecimal idTipoObject;
    private String nmTipoObject;
    private String verificati;
    private String nonRisolubili;
    private String daRecuperare;
    private BigDecimal idObject;
    private String chiave;

    public BigDecimal getIdAmbienteVers() {
        return idAmbienteVers;
    }

    public void setIdAmbienteVers(BigDecimal idAmbienteVers) {
        this.idAmbienteVers = idAmbienteVers;
    }

    public BigDecimal getIdVers() {
        return idVers;
    }

    public void setIdVers(BigDecimal idVers) {
        this.idVers = idVers;
    }

    public BigDecimal getIdTipoObject() {
        return idTipoObject;
    }

    public void setIdTipoObject(BigDecimal idTipoObject) {
        this.idTipoObject = idTipoObject;
    }

    public String getNmTipoObject() {
        return nmTipoObject;
    }

    public void setNmTipoObject(String nmTipoObject) {
        this.nmTipoObject = nmTipoObject;
    }

    public String getVerificati() {
        return verificati;
    }

    public void setVerificati(String verificati) {
        this.verificati = verificati;
    }

    public String getNonRisolubili() {
        return nonRisolubili;
    }

    public void setNonRisolubili(String nonRisolubili) {
        this.nonRisolubili = nonRisolubili;
    }

    public String getDaRecuperare() {
        return daRecuperare;
    }

    public void setDaRecuperare(String daRecuperare) {
        this.daRecuperare = daRecuperare;
    }

    // MEV 26979
    public BigDecimal getIdObject() {
        return idObject;
    }

    public void setIdObject(BigDecimal idObject) {
        this.idObject = idObject;
    }

    public String getChiave() {
        return chiave;
    }

    public void setChiave(String chiave) {
        this.chiave = chiave;
    }
}
