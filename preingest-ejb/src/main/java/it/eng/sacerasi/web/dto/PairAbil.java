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

package it.eng.sacerasi.web.dto;

public class PairAbil<Classe, IdTipoDato> {

    private final Classe classe;
    private final IdTipoDato idTipoDato;

    public PairAbil(Classe appl, IdTipoDato idTipoDato) {
	this.classe = appl;
	this.idTipoDato = idTipoDato;
    }

    public Classe getClasse() {
	return classe;
    }

    public IdTipoDato getIdTipoDato() {
	return idTipoDato;
    }

    @Override
    public int hashCode() {
	return classe.hashCode() ^ idTipoDato.hashCode();
    }

    @Override
    public boolean equals(Object o) {
	if (o == null) {
	    return false;
	}
	if (!(o instanceof PairAbil)) {
	    return false;
	}
	PairAbil<?, ?> pairo = (PairAbil<?, ?>) o;
	return this.classe.equals(pairo.getClasse())
		&& this.idTipoDato.equals(pairo.getIdTipoDato());
    }

    @Override
    public String toString() {
	return "PairAbil{" + "classe=" + classe + ", idTipoDato=" + idTipoDato + '}';
    }
}
