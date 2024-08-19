/**
 *
 */
package it.eng.sacerasi.annullamento.dto;

import java.io.Serializable;

public class ChiaveUd implements Serializable {

    private static final long serialVersionUID = 1L;

    private String anno;
    private String numero;
    private String registro;

    public ChiaveUd(String anno, String numero, String registro) {
        this.anno = anno;
        this.numero = numero;
        this.registro = registro;
    }

    public String getAnno() {
        return anno;
    }

    public void setAnno(String anno) {
        this.anno = anno;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getRegistro() {
        return registro;
    }

    public void setRegistro(String registro) {
        this.registro = registro;
    }

}