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

/**
 *
 */
package it.eng.sacerasi.sisma.dto;

import java.math.BigDecimal;

import it.eng.sacerasi.util.GenericDto;

public class DocUploadDto extends GenericDto {

    private static final long serialVersionUID = 1L;

    private String nmTipoDocumento;
    private boolean obbligatorio;
    private boolean principale;
    private String nmFileOrig;
    private String cdErr;
    private String dsErr;
    private boolean flEsitoVerifica;
    private BigDecimal dimensione;
    private String dimensioneStringa;
    private String tiVerificaAgenzia;
    private String dataDoc;
    private boolean flDocRicaricato;

    public DocUploadDto() {
        super();
    }

    public String getNmTipoDocumento() {
        return nmTipoDocumento;
    }

    public void setNmTipoDocumento(String nmTipoDocumento) {
        this.nmTipoDocumento = nmTipoDocumento;
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

    public boolean isFlEsitoVerifica() {
        return flEsitoVerifica;
    }

    public void setFlEsitoVerifica(boolean flEsitoVerifica) {
        this.flEsitoVerifica = flEsitoVerifica;
    }

    public boolean isObbligatorio() {
        return obbligatorio;
    }

    public void setObbligatorio(boolean obbligatorio) {
        this.obbligatorio = obbligatorio;
    }

    public boolean isPrincipale() {
        return principale;
    }

    public void setPrincipale(boolean principale) {
        this.principale = principale;
    }

    public String getNmFileOrig() {
        return nmFileOrig;
    }

    public void setNmFileOrig(String nmFileOrig) {
        this.nmFileOrig = nmFileOrig;
    }

    public BigDecimal getDimensione() {
        return dimensione;
    }

    public void setDimensione(BigDecimal dimensione) {
        this.dimensione = dimensione;
    }

    public String getDimensioneStringa() {
        return dimensioneStringa;
    }

    public void setDimensioneStringa(String dimensioneStringa) {
        this.dimensioneStringa = dimensioneStringa;
    }

    public String getDataDoc() {
        return dataDoc;
    }

    public void setDataDoc(String dataDoc) {
        this.dataDoc = dataDoc;
    }

    public String getTiVerificaAgenzia() {
        return tiVerificaAgenzia;
    }

    public void setTiVerificaAgenzia(String tiVerificaAgenzia) {
        this.tiVerificaAgenzia = tiVerificaAgenzia;
    }

    public boolean isFlDocRicaricato() {
        return flDocRicaricato;
    }

    public void setFlDocRicaricato(boolean flDocRicaricato) {
        this.flDocRicaricato = flDocRicaricato;
    }

}
