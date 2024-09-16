/**
 *
 */
package it.eng.sacerasi.sisma.dto;

import it.eng.sacerasi.util.GenericDto;

public class NavigazioneSismaDto extends GenericDto {

    private static final long serialVersionUID = 1L;

    private boolean verificaInCorso;
    private boolean verificaErrata;
    private boolean fileMancante;

    public NavigazioneSismaDto() {
        super();
    }

    public boolean isVerificaInCorso() {
        return verificaInCorso;
    }

    public void setVerificaInCorso(boolean verificaInCorso) {
        this.verificaInCorso = verificaInCorso;
    }

    public boolean isVerificaErrata() {
        return verificaErrata;
    }

    public void setVerificaErrata(boolean verificaErrata) {
        this.verificaErrata = verificaErrata;
    }

    public boolean isFileMancante() {
        return fileMancante;
    }

    public void setFileMancante(boolean fileMancante) {
        this.fileMancante = fileMancante;
    }

}
