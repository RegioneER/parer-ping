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

package it.eng.sacerasi.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * The persistent class for the PIG_SISMA_PROGETTI_AG database table.
 *
 */
@Entity
@Table(name = "PIG_SISMA_PROGETTI_AG")
public class PigSismaProgettiAg implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long idSismaProgettiAg;
    private BigDecimal idEnteSiam;
    private String codiceIntervento;
    private String denominazioneIntervento;
    private String nmEnteSiam;
    private String soggettoAttuatore;
    private String naturaSoggettoAttuatore;
    private String enteProprietario;
    private String naturaEnteProprietario;
    private String ubicazioneComune;
    private String ubicazioneProvincia;
    private String flInterventoSoggettoATutela;
    private PigSismaFinanziamento pigSismaFinanziamento;

    public PigSismaProgettiAg() {
        // for Hibernate
    }

    @Id
    @Column(name = "ID_SISMA_PROGETTI_AG")
    public Long getIdSismaProgettiAg() {
        return idSismaProgettiAg;
    }

    public void setIdSismaProgettiAg(Long idSismaProgettiAg) {
        this.idSismaProgettiAg = idSismaProgettiAg;
    }

    @Column(name = "ID_ENTE_SIAM")
    public BigDecimal getIdEnteSiam() {
        return idEnteSiam;
    }

    public void setIdEnteSiam(BigDecimal idEnteSiam) {
        this.idEnteSiam = idEnteSiam;
    }

    @Column(name = "CODICE_INTERVENTO")
    public String getCodiceIntervento() {
        return codiceIntervento;
    }

    public void setCodiceIntervento(String codiceIntervento) {
        this.codiceIntervento = codiceIntervento;
    }

    @Column(name = "DENOMINAZIONE_INTERVENTO")
    public String getDenominazioneIntervento() {
        return denominazioneIntervento;
    }

    public void setDenominazioneIntervento(String denominazioneIntervento) {
        this.denominazioneIntervento = denominazioneIntervento;
    }

    @Column(name = "NM_ENTE_SIAM")
    public String getNmEnteSiam() {
        return nmEnteSiam;
    }

    public void setNmEnteSiam(String nmEnteSiam) {
        this.nmEnteSiam = nmEnteSiam;
    }

    @Column(name = "SOGGETTO_ATTUATORE")
    public String getSoggettoAttuatore() {
        return soggettoAttuatore;
    }

    public void setSoggettoAttuatore(String soggettoAttuatore) {
        this.soggettoAttuatore = soggettoAttuatore;
    }

    @Column(name = "NATURA_SOGGETTO_ATTUATORE")
    public String getNaturaSoggettoAttuatore() {
        return naturaSoggettoAttuatore;
    }

    public void setNaturaSoggettoAttuatore(String naturaSoggettoAttuatore) {
        this.naturaSoggettoAttuatore = naturaSoggettoAttuatore;
    }

    @Column(name = "ENTE_PROPRIETARIO")
    public String getEnteProprietario() {
        return enteProprietario;
    }

    public void setEnteProprietario(String enteProprietario) {
        this.enteProprietario = enteProprietario;
    }

    @Column(name = "NATURA_ENTE_PROPRIETARIO")
    public String getNaturaEnteProprietario() {
        return naturaEnteProprietario;
    }

    public void setNaturaEnteProprietario(String naturaEnteProprietario) {
        this.naturaEnteProprietario = naturaEnteProprietario;
    }

    @Column(name = "UBICAZIONE_COMUNE")
    public String getUbicazioneComune() {
        return ubicazioneComune;
    }

    public void setUbicazioneComune(String ubicazioneComune) {
        this.ubicazioneComune = ubicazioneComune;
    }

    @Column(name = "UBICAZIONE_PROVINCIA")
    public String getUbicazioneProvincia() {
        return ubicazioneProvincia;
    }

    public void setUbicazioneProvincia(String ubicazioneProvincia) {
        this.ubicazioneProvincia = ubicazioneProvincia;
    }

    @Column(name = "FL_INTERVENTO_SOGGETTO_A_TUTELA")
    public String getFlInterventoSoggettoATutela() {
        return flInterventoSoggettoATutela;
    }

    public void setFlInterventoSoggettoATutela(String flInterventoSoggettoATutela) {
        this.flInterventoSoggettoATutela = flInterventoSoggettoATutela;
    }

    // bi-directional many-to-one association to PigSismaFinanziamento
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_SISMA_FINANZIAMENTO")
    public PigSismaFinanziamento getPigSismaFinanziamento() {
        return pigSismaFinanziamento;
    }

    public void setPigSismaFinanziamento(PigSismaFinanziamento pigSismaFinanziamento) {
        this.pigSismaFinanziamento = pigSismaFinanziamento;
    }

}
