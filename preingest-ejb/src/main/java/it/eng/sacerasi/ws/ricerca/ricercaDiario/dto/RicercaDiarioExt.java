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

import it.eng.sacerasi.ws.dto.IWSDesc;
import it.eng.sacerasi.ws.ricerca.dto.AttribDatiSpecBean;
import it.eng.sacerasi.ws.ricerca.dto.DatoSpecFiltroConNomeColonna;
import it.eng.sacerasi.ws.ricerca.dto.DatoSpecOrderConNomeColonna;
import it.eng.sacerasi.ws.ricerca.dto.DatoSpecOutputConNomeColonna;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Gilioli_P
 */
public class RicercaDiarioExt {

    private IWSDesc descrizione;
    private Long idVersatore;
    private Long idTipoObject;
    private Date dtApertura;
    private Date dtChiusura;
    private List<AttribDatiSpecBean> listaAttribDatiSpec;
    private List<DatoSpecFiltroConNomeColonna> datiSpecFiltriConNomeColonna;
    private List<DatoSpecOutputConNomeColonna> datiSpecOutputConNomeColonna;
    private List<DatoSpecOrderConNomeColonna> datiSpecOrderConNomeColonna;
    private String xmlDatiSpecOutput;
    private RicercaDiarioInput ricercaDiarioInput;

    public IWSDesc getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(IWSDesc descrizione) {
        this.descrizione = descrizione;
    }

    public Long getIdVersatore() {
        return idVersatore;
    }

    public void setIdVersatore(Long idVersatore) {
        this.idVersatore = idVersatore;
    }

    public Long getIdTipoObject() {
        return idTipoObject;
    }

    public void setIdTipoObject(Long idTipoObject) {
        this.idTipoObject = idTipoObject;
    }

    public Date getDtApertura() {
        return dtApertura;
    }

    public void setDtApertura(Date dtApertura) {
        this.dtApertura = dtApertura;
    }

    public Date getDtChiusura() {
        return dtChiusura;
    }

    public void setDtChiusura(Date dtChiusura) {
        this.dtChiusura = dtChiusura;
    }

    public List<AttribDatiSpecBean> getListaAttribDatiSpec() {
        return listaAttribDatiSpec;
    }

    public void setListaAttribDatiSpec(List<AttribDatiSpecBean> listaAttribDatiSpec) {
        this.listaAttribDatiSpec = listaAttribDatiSpec;
    }

    public List<DatoSpecFiltroConNomeColonna> getDatiSpecFiltriConNomeColonna() {
        return datiSpecFiltriConNomeColonna;
    }

    public void setDatiSpecFiltriConNomeColonna(List<DatoSpecFiltroConNomeColonna> datiSpecFiltriConNomeColonna) {
        this.datiSpecFiltriConNomeColonna = datiSpecFiltriConNomeColonna;
    }

    public List<DatoSpecOutputConNomeColonna> getDatiSpecOutputConNomeColonna() {
        return datiSpecOutputConNomeColonna;
    }

    public void setDatiSpecOutputConNomeColonna(List<DatoSpecOutputConNomeColonna> datiSpecOutputConNomeColonna) {
        this.datiSpecOutputConNomeColonna = datiSpecOutputConNomeColonna;
    }

    public List<DatoSpecOrderConNomeColonna> getDatiSpecOrderConNomeColonna() {
        return datiSpecOrderConNomeColonna;
    }

    public void setDatiSpecOrderConNomeColonna(List<DatoSpecOrderConNomeColonna> datiSpecOrderConNomeColonna) {
        this.datiSpecOrderConNomeColonna = datiSpecOrderConNomeColonna;
    }

    public String getXmlDatiSpecOutput() {
        return xmlDatiSpecOutput;
    }

    public void setXmlDatiSpecOutput(String xmlDatiSpecOutput) {
        this.xmlDatiSpecOutput = xmlDatiSpecOutput;
    }

    public RicercaDiarioInput getRicercaDiarioInput() {
        return ricercaDiarioInput;
    }

    public void setRicercaDiarioInput(RicercaDiarioInput ricercaDiarioInput) {
        this.ricercaDiarioInput = ricercaDiarioInput;
    }
}
