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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.eng.sacerasi.ws.ricerca.ricercaRecuperati.dto;

import it.eng.sacerasi.ws.dto.IWSDesc;

/**
 *
 * @author Filippini_M
 */
public class RicercaRecuperatiExt {

    private IWSDesc descrizione;
    private Long idVersatore;
    private Long idTipoObject;
    private RicercaRecuperatiInput ricercaRecuperatiInput;

    public IWSDesc getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(IWSDesc descrizione) {
        this.descrizione = descrizione;
    }

    public Long getIdVersatore() {
        return idVersatore;
    }

    public void setIdVersatore(Long idVersatore) {
        this.idVersatore = idVersatore;
    }

    public Long getIdTipoObject() {
        return idTipoObject;
    }

    public void setIdTipoObject(Long idTipoObject) {
        this.idTipoObject = idTipoObject;
    }

    public RicercaRecuperatiInput getRicercaRecuperatiInput() {
        return ricercaRecuperatiInput;
    }

    public void setRicercaRecuperatiInput(RicercaRecuperatiInput ricercaRecuperatiInput) {
        this.ricercaRecuperatiInput = ricercaRecuperatiInput;
    }
}
