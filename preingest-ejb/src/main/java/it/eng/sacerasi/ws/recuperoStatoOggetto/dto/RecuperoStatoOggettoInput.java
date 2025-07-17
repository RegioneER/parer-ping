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

package it.eng.sacerasi.ws.recuperoStatoOggetto.dto;

/**
 *
 * @author Gilioli_P
 */
public class RecuperoStatoOggettoInput {

    private String nmAmbiente;
    private String nmVersatore;
    private String cdKeyObject;

    public RecuperoStatoOggettoInput(String nmAmbiente, String nmVersatore, String cdKeyObject) {
	this.nmAmbiente = nmAmbiente;
	this.nmVersatore = nmVersatore;
	this.cdKeyObject = cdKeyObject;
    }

    public String getNmAmbiente() {
	return nmAmbiente;
    }

    public void setNmAmbiente(String nmAmbiente) {
	this.nmAmbiente = nmAmbiente;
    }

    public String getNmVersatore() {
	return nmVersatore;
    }

    public void setNmVersatore(String nmVersatore) {
	this.nmVersatore = nmVersatore;
    }

    public String getCdKeyObject() {
	return cdKeyObject;
    }

    public void setCdKeyObject(String cdKeyObject) {
	this.cdKeyObject = cdKeyObject;
    }

}
