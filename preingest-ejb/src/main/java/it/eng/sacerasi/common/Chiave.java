/*
 * Engineering Ingegneria Informatica S.p.A.
 *
 * Copyright (C) 2023 Regione Emilia-Romagna <p/> This program is free software: you can
 * redistribute it and/or modify it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version. <p/> This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Affero General Public License for more details. <p/> You should
 * have received a copy of the GNU Affero General Public License along with this program. If not,
 * see <https://www.gnu.org/licenses/>.
 */

package it.eng.sacerasi.common;

import java.io.Serializable;

/**
 *
 * @author Quaranta_M
 */
public class Chiave implements Serializable {

    private Long anno;
    private String registro;
    private String numero;

    private String chiaveCompatta;
    private String nomefileDerivato;

    public Long getAnno() {
	return anno;
    }

    public void setAnno(Long anno) {
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

    public String getChiaveCompatta() {
	return chiaveCompatta;
    }

    public void setChiaveCompatta(String chiaveCompatta) {
	this.chiaveCompatta = chiaveCompatta;
    }

    public String getNomefileDerivato() {
	return nomefileDerivato;
    }

    public void setNomefileDerivato(String nomefileDerivato) {
	this.nomefileDerivato = nomefileDerivato;
    }

    @Override
    public String toString() {
	return "registro=" + registro + ", anno=" + anno + ", numero=" + numero;
    }

}
