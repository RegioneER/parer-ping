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
