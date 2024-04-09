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

package it.eng.sacerasi.ws.notificaTrasferimento.dto;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Bonora_L
 */
public class ListaFileDepositatoRespType {

    private List<FileDepositatoRespType> fileDepositato;

    public ListaFileDepositatoRespType() {
        fileDepositato = new ArrayList<FileDepositatoRespType>();
    }

    /**
     * @return the fileDepositato
     */
    public List<FileDepositatoRespType> getFileDepositato() {
        return fileDepositato;
    }

    /**
     * @param fileDepositato
     *            the fileDepositato to set
     */
    public void setFileDepositato(List<FileDepositatoRespType> fileDepositato) {
        this.fileDepositato = fileDepositato;
    }

}
