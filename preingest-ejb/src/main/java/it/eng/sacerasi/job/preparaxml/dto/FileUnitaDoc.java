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

/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */
package it.eng.sacerasi.job.preparaxml.dto;

import it.eng.sacerasi.entity.PigTipoFileObject;
import it.eng.sacerasi.ws.xml.invioAsync.FileType;

/**
 *
 * @author Fioravanti_F
 */
public class FileUnitaDoc {

    String idFile;
    String nomeFile;
    String urnFile; // da vedere
    String urnFileInZip;
    PigTipoFileObject rifPigTipoFileObject;
    String tipoHashFile;
    String encodingFile;
    String hashFile;
    FileType parsedFileType;

    public String getIdFile() {
	return idFile;
    }

    public void setIdFile(String idFile) {
	this.idFile = idFile;
    }

    public String getNomeFile() {
	return nomeFile;
    }

    public void setNomeFile(String nomeFile) {
	this.nomeFile = nomeFile;
    }

    public String getUrnFile() {
	return urnFile;
    }

    public void setUrnFile(String urnFile) {
	this.urnFile = urnFile;
    }

    public String getUrnFileInZip() {
	return urnFileInZip;
    }

    public void setUrnFileInZip(String urnFileInZip) {
	this.urnFileInZip = urnFileInZip;
    }

    public PigTipoFileObject getRifPigTipoFileObject() {
	return rifPigTipoFileObject;
    }

    public void setRifPigTipoFileObject(PigTipoFileObject rifPigTipoFileObject) {
	this.rifPigTipoFileObject = rifPigTipoFileObject;
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

    public FileType getParsedFileType() {
	return parsedFileType;
    }

    public void setParsedFileType(FileType parsedFileType) {
	this.parsedFileType = parsedFileType;
    }
}
