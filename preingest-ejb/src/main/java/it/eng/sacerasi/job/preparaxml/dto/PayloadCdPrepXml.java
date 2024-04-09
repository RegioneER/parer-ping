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
package it.eng.sacerasi.job.preparaxml.dto;

import java.io.Serializable;

/**
 *
 * @author Fioravanti_F
 */
public class PayloadCdPrepXml implements Serializable {

    private static final long serialVersionUID = -6419312740980744709L;
    private String rootDirectory;
    private long idPigObject;
    private long idLastSessioneIngest;

    public String getRootDirectory() {
        return rootDirectory;
    }

    public void setRootDirectory(String rootDirectory) {
        this.rootDirectory = rootDirectory;
    }

    public long getIdPigObject() {
        return idPigObject;
    }

    public void setIdPigObject(long idPigObject) {
        this.idPigObject = idPigObject;
    }

    public long getIdLastSessioneIngest() {
        return idLastSessioneIngest;
    }

    public void setIdLastSessioneIngest(long idLastSessioneIngest) {
        this.idLastSessioneIngest = idLastSessioneIngest;
    }

    @Override
    public String toString() {
        return "PayloadCdPrepXml{" + "rootDirectory=" + rootDirectory + "\n idPigObject=" + idPigObject + '}';
    }

}
