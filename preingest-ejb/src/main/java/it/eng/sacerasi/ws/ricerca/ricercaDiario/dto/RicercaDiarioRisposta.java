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

package it.eng.sacerasi.ws.ricerca.ricercaDiario.dto;

import it.eng.sacerasi.common.Constants;
import it.eng.sacerasi.common.Constants.EsitoServizio;

/**
 *
 * @author Gilioli_P
 */
public class RicercaDiarioRisposta {

    private Constants.EsitoServizio cdEsito;
    private String cdErr;
    private String dsErr;
    private String nmAmbiente;
    private String nmVersatore;
    private String nmTipoObject;
    private String cdKeyObject;
    private Long idSessione;
    private String tiStatoObject;
    private boolean flTutteSessioni;
    private String xmlDatiSpecOutput;
    private String xmlDatiSpecFiltri;
    private String xmlDatiSpecOrder;
    private int niRecordInizio;
    private int niRecordResultSet;
    private int niRecordTotale;
    private int niRecordOutput;
    private ListaOggRicDiarioType listaOggetti;

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

    public Long getIdSessione() {
        return idSessione;
    }

    public void setIdSessione(Long idSessione) {
        this.idSessione = idSessione;
    }

    public String getTiStatoObject() {
        return tiStatoObject;
    }

    public void setTiStatoObject(String tiStatoObject) {
        this.tiStatoObject = tiStatoObject;
    }

    public boolean isFlTutteSessioni() {
        return flTutteSessioni;
    }

    public void setFlTutteSessioni(boolean flTutteSessioni) {
        this.flTutteSessioni = flTutteSessioni;
    }

    public String getXmlDatiSpecOutput() {
        return xmlDatiSpecOutput;
    }

    public void setXmlDatiSpecOutput(String xmlDatiSpecOutput) {
        this.xmlDatiSpecOutput = xmlDatiSpecOutput;
    }

    public String getXmlDatiSpecFiltri() {
        return xmlDatiSpecFiltri;
    }

    public void setXmlDatiSpecFiltri(String xmlDatiSpecFiltri) {
        this.xmlDatiSpecFiltri = xmlDatiSpecFiltri;
    }

    public String getXmlDatiSpecOrder() {
        return xmlDatiSpecOrder;
    }

    public void setXmlDatiSpecOrder(String xmlDatiSpecOrder) {
        this.xmlDatiSpecOrder = xmlDatiSpecOrder;
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

    public ListaOggRicDiarioType getListaOggetti() {
        return listaOggetti;
    }

    public void setListaOggetti(ListaOggRicDiarioType listaOggetti) {
        this.listaOggetti = listaOggetti;
    }
}
