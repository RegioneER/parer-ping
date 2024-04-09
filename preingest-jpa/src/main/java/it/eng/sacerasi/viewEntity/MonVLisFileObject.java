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

package it.eng.sacerasi.viewEntity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * The persistent class for the MON_V_LIS_FILE_OBJECT database table.
 *
 */
@Entity
@Table(name = "MON_V_LIS_FILE_OBJECT")
public class MonVLisFileObject implements Serializable {

    private static final long serialVersionUID = 1L;
    private String cdEncodingHashFileVers;
    private String dsHashFileVers;
    private BigDecimal idFileObject;
    private BigDecimal idObject;
    private BigDecimal idTipoFileObject;
    private String nmFileObject;
    private String nmTipoFileObject;
    private String tiAlgoHashFileVers;
    private BigDecimal niSizeFileVers;
    private String nmBucket;
    private String cdKeyFile;

    public MonVLisFileObject() {
    }

    @Column(name = "CD_ENCODING_HASH_FILE_VERS")
    public String getCdEncodingHashFileVers() {
        return this.cdEncodingHashFileVers;
    }

    public void setCdEncodingHashFileVers(String cdEncodingHashFileVers) {
        this.cdEncodingHashFileVers = cdEncodingHashFileVers;
    }

    @Column(name = "DS_HASH_FILE_VERS")
    public String getDsHashFileVers() {
        return this.dsHashFileVers;
    }

    public void setDsHashFileVers(String dsHashFileVers) {
        this.dsHashFileVers = dsHashFileVers;
    }

    @Id
    @Column(name = "ID_FILE_OBJECT")
    public BigDecimal getIdFileObject() {
        return this.idFileObject;
    }

    public void setIdFileObject(BigDecimal idFileObject) {
        this.idFileObject = idFileObject;
    }

    @Column(name = "ID_OBJECT")
    public BigDecimal getIdObject() {
        return this.idObject;
    }

    public void setIdObject(BigDecimal idObject) {
        this.idObject = idObject;
    }

    @Column(name = "ID_TIPO_FILE_OBJECT")
    public BigDecimal getIdTipoFileObject() {
        return this.idTipoFileObject;
    }

    public void setIdTipoFileObject(BigDecimal idTipoFileObject) {
        this.idTipoFileObject = idTipoFileObject;
    }

    @Column(name = "NM_FILE_OBJECT")
    public String getNmFileObject() {
        return this.nmFileObject;
    }

    public void setNmFileObject(String nmFileObject) {
        this.nmFileObject = nmFileObject;
    }

    @Column(name = "NM_TIPO_FILE_OBJECT")
    public String getNmTipoFileObject() {
        return this.nmTipoFileObject;
    }

    public void setNmTipoFileObject(String nmTipoFileObject) {
        this.nmTipoFileObject = nmTipoFileObject;
    }

    @Column(name = "TI_ALGO_HASH_FILE_VERS")
    public String getTiAlgoHashFileVers() {
        return this.tiAlgoHashFileVers;
    }

    public void setTiAlgoHashFileVers(String tiAlgoHashFileVers) {
        this.tiAlgoHashFileVers = tiAlgoHashFileVers;
    }

    @Column(name = "NI_SIZE_FILE_VERS")
    public BigDecimal getNiSizeFileVers() {
        return this.niSizeFileVers;
    }

    public void setNiSizeFileVers(BigDecimal niSizeFileVers) {
        this.niSizeFileVers = niSizeFileVers;
    }

    @Column(name = "NM_BUCKET")
    public String getNmBucket() {
        return nmBucket;
    }

    public void setNmBucket(String nmBucket) {
        this.nmBucket = nmBucket;
    }

    @Column(name = "CD_KEY_FILE")
    public String getCdKeyFile() {
        return cdKeyFile;
    }

    public void setCdKeyFile(String cdKeyFile) {
        this.cdKeyFile = cdKeyFile;
    }

}
