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

package it.eng.sacerasi.ws.ricerca.ricercaRecuperati.dto;

/**
 *
 * @author Filippini_M
 */
public class RicercaRecuperatiInput {

    private String nmAmbiente;
    private String nmVersatore;
    // private String cdPassword;

    // public RicercaRecuperatiInput(String nmAmbiente, String nmVersatore,
    // String cdPassword) {
    public RicercaRecuperatiInput(String nmAmbiente, String nmVersatore) {

        this.nmAmbiente = nmAmbiente;
        this.nmVersatore = nmVersatore;
        // this.cdPassword = cdPassword;

    }

    public String getNmAmbiente() {
        return nmAmbiente;
    }

    public String getNmVersatore() {
        return nmVersatore;
    }

    // public String getCdPassword() {
    // return cdPassword;
    // }

    public void setNmAmbiente(String nmAmbiente) {
        this.nmAmbiente = nmAmbiente;
    }

    public void setNmVersatore(String nmVersatore) {
        this.nmVersatore = nmVersatore;
    }

    // public void setCdPassword(String cdPassword) {
    // this.cdPassword = cdPassword;
    // }

}
