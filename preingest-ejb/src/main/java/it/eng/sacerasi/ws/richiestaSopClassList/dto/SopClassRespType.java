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

package it.eng.sacerasi.ws.richiestaSopClassList.dto;

/**
 *
 * @author Bonora_L
 */
public class SopClassRespType {

    private String cdSopClass;
    private String dsSopClass;

    /**
     * @return the cdSopClass
     */
    public String getCdSopClass() {
        return cdSopClass;
    }

    /**
     * @param cdSopClass
     *            the cdSopClass to set
     */
    public void setCdSopClass(String cdSopClass) {
        this.cdSopClass = cdSopClass;
    }

    /**
     * @return the dsSopClass
     */
    public String getDsSopClass() {
        return dsSopClass;
    }

    /**
     * @param dsSopClass
     *            the dsSopClass to set
     */
    public void setDsSopClass(String dsSopClass) {
        this.dsSopClass = dsSopClass;
    }
}
