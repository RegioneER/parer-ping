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

package it.eng.sacerasi.job.preparaxml.dto;

import it.eng.sacerasi.entity.PigFileObject;
import java.io.Serializable;

/**
 *
 * @author Fioravanti_F
 */
public class FileObjectExt implements Serializable {

    private static final long serialVersionUID = 1L;
    private PigFileObject rifPigFileObject;
    String urnFile;
    String tipoHashFile;
    String encodingFile;
    String hashFile;
    //
    String urnFileRel;
    private Long idBackend;
    private String nmOsTenant;
    String nmBucket;
    String cdKeyFile;

    public PigFileObject getRifPigFileObject() {
	return rifPigFileObject;
    }

    public void setRifPigFileObject(PigFileObject rifPigFileObject) {
	this.rifPigFileObject = rifPigFileObject;
    }

    public String getUrnFile() {
	return urnFile;
    }

    public void setUrnFile(String urnFile) {
	this.urnFile = urnFile;
    }

    public String getTipoHashFile() {
	return tipoHashFile;
    }

    public void setTipoHashFile(String tipoHashFile) {
	this.tipoHashFile = tipoHashFile;
    }

    public String getEncodingFile() {
	return encodingFile;
    }

    public void setEncodingFile(String encodingFile) {
	this.encodingFile = encodingFile;
    }

    public String getHashFile() {
	return hashFile;
    }

    public void setHashFile(String hashFile) {
	this.hashFile = hashFile;
    }

    public String getUrnFileRel() {
	return urnFileRel;
    }

    public void setUrnFileRel(String urnFileRel) {
	this.urnFileRel = urnFileRel;
    }

    // MEV 34843
    public Long getIdBackend() {
	return idBackend;
    }

    public void setIdBackend(Long idBackend) {
	this.idBackend = idBackend;
    }

    public String getNmOsTenant() {
	return nmOsTenant;
    }

    public void setNmOsTenant(String nmOsTenant) {
	this.nmOsTenant = nmOsTenant;
    }

    public String getNmBucket() {
	return nmBucket;
    }

    public void setNmBucket(String nmBucket) {
	this.nmBucket = nmBucket;
    }

    public String getCdKeyFile() {
	return cdKeyFile;
    }

    public void setCdKeyFile(String cdKeyFile) {
	this.cdKeyFile = cdKeyFile;
    }
}
