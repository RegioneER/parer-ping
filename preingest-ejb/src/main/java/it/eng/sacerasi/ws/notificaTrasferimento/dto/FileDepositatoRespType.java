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

/**
 *
 * @author Bonora_L
 */
public class FileDepositatoRespType {
    private String nmTipoObject;
    private String nmNomeFile;
    private String cdEncoding;
    private String tiAlgoritmoHash;
    private String dsHashFile;
    private String nmOsBucket;
    private String nmNomeFileOs;

    /**
     * @return the nmTipoObject
     */
    public String getNmTipoObject() {
        return nmTipoObject;
    }

    /**
     * @param nmTipoObject
     *            the nmTipoObject to set
     */
    public void setNmTipoObject(String nmTipoObject) {
        this.nmTipoObject = nmTipoObject;
    }

    /**
     * @return the nmNomeFile
     */
    public String getNmNomeFile() {
        return nmNomeFile;
    }

    /**
     * @param nmNomeFile
     *            the nmNomeFile to set
     */
    public void setNmNomeFile(String nmNomeFile) {
        this.nmNomeFile = nmNomeFile;
    }

    /**
     * @return the cdEncoding
     */
    public String getCdEncoding() {
        return cdEncoding;
    }

    /**
     * @param cdEncoding
     *            the cdEncoding to set
     */
    public void setCdEncoding(String cdEncoding) {
        this.cdEncoding = cdEncoding;
    }

    /**
     * @return the tiAlgoritmoHash
     */
    public String getTiAlgoritmoHash() {
        return tiAlgoritmoHash;
    }

    /**
     * @param tiAlgoritmoHash
     *            the tiAlgoritmoHash to set
     */
    public void setTiAlgoritmoHash(String tiAlgoritmoHash) {
        this.tiAlgoritmoHash = tiAlgoritmoHash;
    }

    /**
     * @return the dsHashFile
     */
    public String getDsHashFile() {
        return dsHashFile;
    }

    /**
     * @param dsHashFile
     *            the dsHashFile to set
     */
    public void setDsHashFile(String dsHashFile) {
        this.dsHashFile = dsHashFile;
    }

    public String getNmOsBucket() {
        return nmOsBucket;
    }

    public void setNmOsBucket(String nmOsBucket) {
        this.nmOsBucket = nmOsBucket;
    }

    public String getNmNomeFileOs() {
        return nmNomeFileOs;
    }

    public void setNmNomeFileOs(String nmNomeFileOs) {
        this.nmNomeFileOs = nmNomeFileOs;
    }
}
