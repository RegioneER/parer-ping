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

package it.eng.sacerasi.ws.puliziaNotificato.dto;

/**
 *
 * @author Bonora_L
 */
public class PuliziaNotificatoInput {

    private String NmAmbiente;
    private String NmVersatore;
    // private String CdPassword;
    private String CdKeyObject;

    // public PuliziaNotificatoInput(String NmAmbiente, String NmVersatore, String CdPassword,
    // String CdKeyObject) {
    public PuliziaNotificatoInput(String NmAmbiente, String NmVersatore, String CdKeyObject) {
	this.NmAmbiente = NmAmbiente;
	this.NmVersatore = NmVersatore;
	// this.CdPassword = CdPassword;
	this.CdKeyObject = CdKeyObject;
    }

    /**
     * @return the NmAmbiente
     */
    public String getNmAmbiente() {
	return NmAmbiente;
    }

    /**
     * @param NmAmbiente the NmAmbiente to set
     */
    public void setNmAmbiente(String NmAmbiente) {
	this.NmAmbiente = NmAmbiente;
    }

    /**
     * @return the NmVersatore
     */
    public String getNmVersatore() {
	return NmVersatore;
    }

    /**
     * @param NmVersatore the NmVersatore to set
     */
    public void setNmVersatore(String NmVersatore) {
	this.NmVersatore = NmVersatore;
    }

    // /**
    // * @return the CdPassword
    // */
    // public String getCdPassword() {
    // return CdPassword;
    // }
    //
    // /**
    // * @param CdPassword the CdPassword to set
    // */
    // public void setCdPassword(String CdPassword) {
    // this.CdPassword = CdPassword;
    // }

    /**
     * @return the CdKeyObject
     */
    public String getCdKeyObject() {
	return CdKeyObject;
    }

    /**
     * @param CdKeyObject the CdKeyObject to set
     */
    public void setCdKeyObject(String CdKeyObject) {
	this.CdKeyObject = CdKeyObject;
    }

}
