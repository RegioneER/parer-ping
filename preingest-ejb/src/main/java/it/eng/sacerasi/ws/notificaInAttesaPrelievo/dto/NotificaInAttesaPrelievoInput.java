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

package it.eng.sacerasi.ws.notificaInAttesaPrelievo.dto;

/**
 *
 * @author Gilioli_P
 */
public class NotificaInAttesaPrelievoInput {

    private String nmAmbiente;
    private String nmVersatore;
    // private String cdPassword;
    private String cdKeyObject;

    // public NotificaInAttesaPrelievoInput(String nmAmbiente, String nmVersatore, String cdPassword, String
    // cdKeyObject) {
    // this.nmAmbiente = nmAmbiente;
    // this.nmVersatore = nmVersatore;
    // this.cdPassword = cdPassword;
    // this.cdKeyObject = cdKeyObject;
    // }
    public NotificaInAttesaPrelievoInput(String nmAmbiente, String nmVersatore, String cdKeyObject) {
        this.nmAmbiente = nmAmbiente;
        this.nmVersatore = nmVersatore;
        this.cdKeyObject = cdKeyObject;
    }

    /**
     * @return the nmAmbiente
     */
    public String getNmAmbiente() {
        return nmAmbiente;
    }

    /**
     * @param nmAmbiente
     *            the nmAmbiente to set
     */
    public void setNmAmbiente(String nmAmbiente) {
        this.nmAmbiente = nmAmbiente;
    }

    /**
     * @return the nmVersatore
     */
    public String getNmVersatore() {
        return nmVersatore;
    }

    /**
     * @param nmVersatore
     *            the nmVersatore to set
     */
    public void setNmVersatore(String nmVersatore) {
        this.nmVersatore = nmVersatore;
    }

    // /**
    // * @return the cdPassword
    // */
    // public String getCdPassword() {
    // return cdPassword;
    // }

    // /**
    // * @param cdPassword the cdPassword to set
    // */
    // public void setCdPassword(String cdPassword) {
    // this.cdPassword = cdPassword;
    // }

    /**
     * @return the cdKeyObject
     */
    public String getCdKeyObject() {
        return cdKeyObject;
    }

    /**
     * @param cdKeyObject
     *            the cdKeyObject to set
     */
    public void setCdKeyObject(String cdKeyObject) {
        this.cdKeyObject = cdKeyObject;
    }
}
