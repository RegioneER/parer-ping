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

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Gilioli_P
 */
public class MonitoraggioFiltriListaVersFallitiBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private BigDecimal idAmbienteVers;
    private BigDecimal idVers;
    private BigDecimal idTipoObject;
    private String nmTipoObject;
    private String tipoErrore;
    private String statoRisoluzione;
    private String periodoVers;
    private Date giornoVersDa;
    private Date giornoVersA;
    private BigDecimal oreVersDa;
    private BigDecimal minutiVersDa;
    private BigDecimal oreVersA;
    private BigDecimal minutiVersA;
    private Date giornoVersDaValidato;
    private Date giornoVersAValidato;
    private String errore;
    private String classeErrore;
    private String verificati;
    private String nonRisolubili;
    private List<String> stati;
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

    public String getTipoErrore() {
        return tipoErrore;
    }

    public void setTipoErrore(String tipoErrore) {
        this.tipoErrore = tipoErrore;
    }

    public String getStatoRisoluzione() {
        return statoRisoluzione;
    }

    public void setStatoRisoluzione(String statoRisoluzione) {
        this.statoRisoluzione = statoRisoluzione;
    }

    public String getPeriodoVers() {
        return periodoVers;
    }

    public void setPeriodoVers(String periodoVers) {
        this.periodoVers = periodoVers;
    }

    public Date getGiornoVersDa() {
        return giornoVersDa;
    }

    public void setGiornoVersDa(Date giornoVersDa) {
        this.giornoVersDa = giornoVersDa;
    }

    public Date getGiornoVersA() {
        return giornoVersA;
    }

    public void setGiornoVersA(Date giornoVersA) {
        this.giornoVersA = giornoVersA;
    }

    public BigDecimal getOreVersDa() {
        return oreVersDa;
    }

    public void setOreVersDa(BigDecimal oreVersDa) {
        this.oreVersDa = oreVersDa;
    }

    public BigDecimal getMinutiVersDa() {
        return minutiVersDa;
    }

    public void setMinutiVersDa(BigDecimal minutiVersDa) {
        this.minutiVersDa = minutiVersDa;
    }

    public BigDecimal getOreVersA() {
        return oreVersA;
    }

    public void setOreVersA(BigDecimal oreVersA) {
        this.oreVersA = oreVersA;
    }

    public BigDecimal getMinutiVersA() {
        return minutiVersA;
    }

    public void setMinutiVersA(BigDecimal minutiVersA) {
        this.minutiVersA = minutiVersA;
    }

    public Date getGiornoVersDaValidato() {
        return giornoVersDaValidato;
    }

    public void setGiornoVersDaValidato(Date giornoVersDaValidato) {
        this.giornoVersDaValidato = giornoVersDaValidato;
    }

    public Date getGiornoVersAValidato() {
        return giornoVersAValidato;
    }

    public void setGiornoVersAValidato(Date giornoVersAValidato) {
        this.giornoVersAValidato = giornoVersAValidato;
    }

    public String getErrore() {
        return errore;
    }

    public void setErrore(String errore) {
        this.errore = errore;
    }

    public String getClasseErrore() {
        return classeErrore;
    }

    public void setClasseErrore(String classeErrore) {
        this.classeErrore = classeErrore;
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

    public List<String> getStati() {
        return stati;
    }

    public void setStati(List<String> stati) {
        this.stati = stati;
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
