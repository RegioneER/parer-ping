/**
 *
 */
package it.eng.sacerasi.sisma.dto;

import java.math.BigDecimal;
import java.util.Date;

import it.eng.sacerasi.util.GenericDto;

public class DocSismaDto extends GenericDto {

    private static final long serialVersionUID = 1L;

    private BigDecimal idSismaDocumenti;
    private BigDecimal idSisma;
    private BigDecimal dimensione;
    private int numFileCaricati;
    private String nmFileOrig;
    private String nmFileOs;
    private String nmTipoDocumento;
    private boolean obbligatorio;
    private String cdErr;
    private String dsErr;
    private boolean flEsitoVerifica;
    private Date dtCaricamento;
    private String tiVerificaAgenzia;
    private boolean flDocRicaricato;
    private String blReport;

    public DocSismaDto() {
        super();
    }

    public String getBlReport() {
        return blReport;
    }

    public void setBlReport(String blReport) {
        this.blReport = blReport;
    }

    public BigDecimal getIdSismaDocumenti() {
        return idSismaDocumenti;
    }

    public void setIdSismaDocumenti(BigDecimal idSismaDocumenti) {
        this.idSismaDocumenti = idSismaDocumenti;
    }

    public BigDecimal getIdSisma() {
        return idSisma;
    }

    public void setIdSisma(BigDecimal idSisma) {
        this.idSisma = idSisma;
    }

    public int getNumFileCaricati() {
        return numFileCaricati;
    }

    public void setNumFileCaricati(int numFileCaricati) {
        this.numFileCaricati = numFileCaricati;
    }

    public String getNmFileOrig() {
        return nmFileOrig;
    }

    public void setNmFileOrig(String nmFileOrig) {
        this.nmFileOrig = nmFileOrig;
    }

    public String getNmFileOs() {
        return nmFileOs;
    }

    public void setNmFileOs(String nmFileOs) {
        this.nmFileOs = nmFileOs;
    }

    public String getNmTipoDocumento() {
        return nmTipoDocumento;
    }

    public void setNmTipoDocumento(String nmTipoDocumento) {
        this.nmTipoDocumento = nmTipoDocumento;
    }

    public BigDecimal getDimensione() {
        return dimensione;
    }

    public void setDimensione(BigDecimal dimensione) {
        this.dimensione = dimensione;
    }

    public boolean isObbligatorio() {
        return obbligatorio;
    }

    public void setObbligatorio(boolean obbligatorio) {
        this.obbligatorio = obbligatorio;
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

    public Date getDtCaricamento() {
        return dtCaricamento;
    }

    public void setDtCaricamento(Date dtCaricamento) {
        this.dtCaricamento = dtCaricamento;
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
