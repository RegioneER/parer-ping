/**
 *
 */
package it.eng.sacerasi.sisma.dto;

import java.io.Serializable;

import it.eng.sacerasi.entity.PigSisma;

public class EsitoSalvataggioSisma implements Serializable {

    private static final long serialVersionUID = 1L;

    private boolean ok;
    private PigSisma.TiStato stato;

    public EsitoSalvataggioSisma() {
        super();
    }

    public boolean isOk() {
        return ok;
    }

    public void setOk(boolean ok) {
        this.ok = ok;
    }

    public PigSisma.TiStato getStato() {
        return stato;
    }

    public void setStato(PigSisma.TiStato stato) {
        this.stato = stato;
    }

}