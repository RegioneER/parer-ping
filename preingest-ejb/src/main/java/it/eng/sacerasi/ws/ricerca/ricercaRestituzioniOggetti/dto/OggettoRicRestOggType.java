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

package it.eng.sacerasi.ws.ricerca.ricercaRestituzioniOggetti.dto;

import java.util.Date;

/**
 *
 * @author Gilioli_P
 */
public class OggettoRicRestOggType {

    private Long idObject;
    private String cdKeyObject;
    private String tiStatoSessione;
    private Date dtAperturaSessione;
    private Date dtChiusuraSessione;
    private Long idSessione;
    private String chiaveUnitaDoc;
    private String cdErr;
    private String dsErr;
    private String xmlDatiSpecResult;

    public Long getIdObject() {
        return idObject;
    }

    public void setIdObject(Long idObject) {
        this.idObject = idObject;
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

    public Date getDtAperturaSessione() {
        return dtAperturaSessione;
    }

    public void setDtAperturaSessione(Date dtAperturaSessione) {
        this.dtAperturaSessione = dtAperturaSessione;
    }

    public Date getDtChiusuraSessione() {
        return dtChiusuraSessione;
    }

    public void setDtChiusuraSessione(Date dtChiusuraSessione) {
        this.dtChiusuraSessione = dtChiusuraSessione;
    }

    public Long getIdSessione() {
        return idSessione;
    }

    public void setIdSessione(Long idSessione) {
        this.idSessione = idSessione;
    }

    public String getChiaveUnitaDoc() {
        return chiaveUnitaDoc;
    }

    public void setChiaveUnitaDoc(String chiaveUnitaDoc) {
        this.chiaveUnitaDoc = chiaveUnitaDoc;
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

    public String getXmlDatiSpecResult() {
        return xmlDatiSpecResult;
    }

    public void setXmlDatiSpecResult(String xmlDatiSpecResult) {
        this.xmlDatiSpecResult = xmlDatiSpecResult;
    }

    @Override
    public String toString() {
        return getIdObject() + " " + getCdKeyObject() + " " + getTiStatoSessione() + " " + getDtAperturaSessione() + " "
                + getDtChiusuraSessione() + " " + getIdSessione() + " " + getChiaveUnitaDoc() + " " + getCdErr() + " "
                + getDsErr() + " " + getXmlDatiSpecResult();
    }
}
