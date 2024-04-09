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

package it.eng.sacerasi.ws.response;

import it.eng.sacerasi.ws.notificaTrasferimento.dto.ListaFileDepositatoRespType;

/**
 *
 * @author Bonora_L
 */
public class NotificaTrasferimentoRisposta {

    private String cdEsito;
    private String cdErr;
    private String dsErr;
    private String nmAmbiente;
    private String nmVersatore;
    private String cdKeyObject;
    private ListaFileDepositatoRespType listaFileDepositati;

    public NotificaTrasferimentoRisposta() {
        listaFileDepositati = new ListaFileDepositatoRespType();
    }

    public String getCdErr() {
        return cdErr;
    }

    public void setCdErr(String cdErr) {
        this.cdErr = cdErr;
    }

    public String getCdEsito() {
        return cdEsito;
    }

    public void setCdEsito(String cdEsito) {
        this.cdEsito = cdEsito;
    }

    public String getCdKeyObject() {
        return cdKeyObject;
    }

    public void setCdKeyObject(String cdKeyObject) {
        this.cdKeyObject = cdKeyObject;
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

    public ListaFileDepositatoRespType getListaFileDepositati() {
        return listaFileDepositati;
    }

    public void setListaFileDepositati(ListaFileDepositatoRespType listaFileDepositati) {
        this.listaFileDepositati = listaFileDepositati;
    }
}
