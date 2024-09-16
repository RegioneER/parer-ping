/**
 *
 */
package it.eng.sacerasi.sisma.dto;

import java.io.Serializable;

public class AutorizzazioneDto implements Serializable {

    private static final long serialVersionUID = 1L;

    public AutorizzazioneDto() {
        super();
    }

    private boolean autorizzato;
    private String messaggioDiErrore;

    public boolean isAutorizzato() {
        return autorizzato;
    }

    public void setAutorizzato(boolean autorizzato) {
        this.autorizzato = autorizzato;
    }

    public String getMessaggioDiErrore() {
        return messaggioDiErrore;
    }

    public void setMessaggioDiErrore(String messaggioDiErrore) {
        this.messaggioDiErrore = messaggioDiErrore;
    }

}