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

package it.eng.sacerasi.ws.ricerca.dto;

import it.eng.sacerasi.ws.util.Costanti.AttribDatiSpecDataType;

/**
 *
 * @author Gilioli_P
 */
public class AttribDatiSpecBean {

    private String nmAttribDatiSpec;
    private String flFiltroDiario;
    private String nomeColonna;
    private AttribDatiSpecDataType dataType;

    public String getNmAttribDatiSpec() {
	return nmAttribDatiSpec;
    }

    public AttribDatiSpecDataType getDataType() {
	return dataType;
    }

    public void setDataType(AttribDatiSpecDataType dataType) {
	this.dataType = dataType;
    }

    public void setNmAttribDatiSpec(String nmAttribDatiSpec) {
	this.nmAttribDatiSpec = nmAttribDatiSpec;
    }

    public String getNomeColonna() {
	return nomeColonna;
    }

    public void setNomeColonna(String nomeColonna) {
	this.nomeColonna = nomeColonna;
    }

    public String getFlFiltroDiario() {
	return flFiltroDiario;
    }

    public void setFlFiltroDiario(String flFiltroDiario) {
	this.flFiltroDiario = flFiltroDiario;
    }
}
