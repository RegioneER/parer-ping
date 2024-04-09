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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.eng.sacerasi.ws.ricerca.ricercaRecuperati.dto;

import it.eng.sacerasi.common.Constants;

/**
 *
 * @author Filippini_M
 */
public class RicercaRecuperatiRisposta {

    private Constants.EsitoServizio cdEsito;
    private String cdErr;
    private String dsErr;
    private String nmAmbiente;
    private String nmVersatore;
    private ListaOggRicRecuperatiType listaOggetti;

    public ListaOggRicRecuperatiType getListaOggetti() {
        return listaOggetti;
    }

    public void setListaOggetti(ListaOggRicRecuperatiType listaOggetti) {
        this.listaOggetti = listaOggetti;
    }

    public void setCdErr(String cdErr) {
        this.cdErr = cdErr;
    }

    public void setDsErr(String dsErr) {
        this.dsErr = dsErr;
    }

    public void setNmAmbiente(String nmAmbiente) {
        this.nmAmbiente = nmAmbiente;
    }

    public void setNmVersatore(String nmVersatore) {
        this.nmVersatore = nmVersatore;
    }

    public String getCdErr() {
        return cdErr;
    }

    public String getDsErr() {
        return dsErr;
    }

    public String getNmAmbiente() {
        return nmAmbiente;
    }

    public String getNmVersatore() {
        return nmVersatore;
    }

    public Constants.EsitoServizio getCdEsito() {
        return cdEsito;
    }

    public void setCdEsito(Constants.EsitoServizio cdEsito) {
        this.cdEsito = cdEsito;
    }

}
