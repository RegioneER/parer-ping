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

package it.eng.sacerasi.ws.ricerca.ricercaRestituzioniOggetti.dto;

import java.util.Date;

import it.eng.sacerasi.common.Constants;
import it.eng.sacerasi.common.Constants.EsitoServizio;

/**
 *
 * @author Gilioli_P
 */
public class RicercaRestituzioniOggettiRisposta {

    private Constants.EsitoServizio cdEsito;
    private String cdErr;
    private String dsErr;
    private String nmAmbiente;
    private String nmVersatore;
    private String nmTipoObject;
    private String cdKeyObject;
    private String tiStatoSessione;
    private Date dtAperturaSessioneDa;
    private Date dtAperturaSessioneA;
    private int niRecordInizio;
    private int niRecordResultSet;
    private int niRecordTotale;
    private int niRecordOutput;
    private ListaOggRicRestOggType listaOggetti;

    public EsitoServizio getCdEsito() {
	return cdEsito;
    }

    public void setCdEsito(EsitoServizio cdEsito) {
	this.cdEsito = cdEsito;
    }

    public String getCdErr() {
	return cdErr;
    }

    public void setCdErr(String cdErr) {
	this.cdErr = cdErr;
    }

    public String getDsErr() {
	return dsErr;
    }

    public void setDsErr(String dsErr) {
	this.dsErr = dsErr;
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

    public String getNmTipoObject() {
	return nmTipoObject;
    }

    public void setNmTipoObject(String nmTipoObject) {
	this.nmTipoObject = nmTipoObject;
    }

    public String getCdKeyObject() {
	return cdKeyObject;
    }

    public void setCdKeyObject(String cdKeyObject) {
	this.cdKeyObject = cdKeyObject;
    }

    public String getTiStatoSessione() {
	return tiStatoSessione;
    }

    public void setTiStatoSessione(String tiStatoSessione) {
	this.tiStatoSessione = tiStatoSessione;
    }

    public Date getDtAperturaSessioneDa() {
	return dtAperturaSessioneDa;
    }

    public void setDtAperturaSessioneDa(Date dtAperturaSessioneDa) {
	this.dtAperturaSessioneDa = dtAperturaSessioneDa;
    }

    public Date getDtAperturaSessioneA() {
	return dtAperturaSessioneA;
    }

    public void setDtAperturaSessioneA(Date dtAperturaSessioneA) {
	this.dtAperturaSessioneA = dtAperturaSessioneA;
    }

    public int getNiRecordInizio() {
	return niRecordInizio;
    }

    public void setNiRecordInizio(int niRecordInizio) {
	this.niRecordInizio = niRecordInizio;
    }

    public int getNiRecordResultSet() {
	return niRecordResultSet;
    }

    public void setNiRecordResultSet(int niRecordResultSet) {
	this.niRecordResultSet = niRecordResultSet;
    }

    public int getNiRecordTotale() {
	return niRecordTotale;
    }

    public void setNiRecordTotale(int niRecordTotale) {
	this.niRecordTotale = niRecordTotale;
    }

    public int getNiRecordOutput() {
	return niRecordOutput;
    }

    public void setNiRecordOutput(int niRecordOutput) {
	this.niRecordOutput = niRecordOutput;
    }

    public ListaOggRicRestOggType getListaOggetti() {
	return listaOggetti;
    }

    public void setListaOggetti(ListaOggRicRestOggType listaOggetti) {
	this.listaOggetti = listaOggetti;
    }
}
