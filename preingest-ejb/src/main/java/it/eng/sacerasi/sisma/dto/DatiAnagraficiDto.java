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

/**
 *
 */
package it.eng.sacerasi.sisma.dto;

import java.io.Serializable;
import java.math.BigDecimal;

public class DatiAnagraficiDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private BigDecimal idEnteSiam;
    private String soggettoAttuatore;
    private String naturaSoggettoAttuatore;
    private String enteProprietario;
    private String naturaEnteProprietario;
    private String ubicazioneComune;
    private String ubicazioneProvincia;
    private boolean soggettoATutela;

    public DatiAnagraficiDto() {
	super();
    }

    public BigDecimal getIdEnteSiam() {
	return idEnteSiam;
    }

    public void setIdEnteSiam(BigDecimal idEnteSiam) {
	this.idEnteSiam = idEnteSiam;
    }

    public String getSoggettoAttuatore() {
	return soggettoAttuatore;
    }

    public void setSoggettoAttuatore(String soggettoAttuatore) {
	this.soggettoAttuatore = soggettoAttuatore;
    }

    public String getNaturaSoggettoAttuatore() {
	return naturaSoggettoAttuatore;
    }

    public void setNaturaSoggettoAttuatore(String naturaSoggettoAttuatore) {
	this.naturaSoggettoAttuatore = naturaSoggettoAttuatore;
    }

    public String getEnteProprietario() {
	return enteProprietario;
    }

    public void setEnteProprietario(String enteProprietario) {
	this.enteProprietario = enteProprietario;
    }

    public String getNaturaEnteProprietario() {
	return naturaEnteProprietario;
    }

    public void setNaturaEnteProprietario(String naturaEnteProprietario) {
	this.naturaEnteProprietario = naturaEnteProprietario;
    }

    public String getUbicazioneComune() {
	return ubicazioneComune;
    }

    public void setUbicazioneComune(String ubicazioneComune) {
	this.ubicazioneComune = ubicazioneComune;
    }

    public String getUbicazioneProvincia() {
	return ubicazioneProvincia;
    }

    public void setUbicazioneProvincia(String ubicazioneProvincia) {
	this.ubicazioneProvincia = ubicazioneProvincia;
    }

    public boolean isSoggettoATutela() {
	return soggettoATutela;
    }

    public void setSoggettoATutela(boolean soggettoATutela) {
	this.soggettoATutela = soggettoATutela;
    }

}
