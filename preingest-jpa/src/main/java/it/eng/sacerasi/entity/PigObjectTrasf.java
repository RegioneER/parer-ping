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

package it.eng.sacerasi.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.id.enhanced.SequenceStyleGenerator;

/**
 * The persistent class for the PIG_OBJECT_TRASF database table.
 *
 */
@Entity
@Table(name = "PIG_OBJECT_TRASF")
@NamedQuery(name = "PigObjectTrasf.findAll", query = "SELECT p FROM PigObjectTrasf p")
public class PigObjectTrasf implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long idObjectTrasf;
    private String cdEncodingHashFileVers;
    private String cdErr;
    private String cdKeyObjectTrasf;
    private String dlErr;
    private String dsHashFileVers;
    private String dsObjectTrasf;
    private String dsPath;
    private BigDecimal pgOggettoTrasf;
    private String tiAlgoHashFileVers;
    private PigObject pigObject;
    private PigTipoObject pigTipoObject;
    private PigVers pigVer;
    private List<PigXmlObjectTrasf> pigXmlObjectTrasfs = new ArrayList<>();

    public PigObjectTrasf() {
        // for Hibernate
    }

    @Id
    @GenericGenerator(name = "PIG_OBJECT_TRASF_IDOBJECTTRASF_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
            @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SPIG_OBJECT_TRASF"),
            @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PIG_OBJECT_TRASF_IDOBJECTTRASF_GENERATOR")
    @Column(name = "ID_OBJECT_TRASF")
    public Long getIdObjectTrasf() {
        return this.idObjectTrasf;
    }

    public void setIdObjectTrasf(Long idObjectTrasf) {
        this.idObjectTrasf = idObjectTrasf;
    }

    @Column(name = "CD_ENCODING_HASH_FILE_VERS")
    public String getCdEncodingHashFileVers() {
        return this.cdEncodingHashFileVers;
    }

    public void setCdEncodingHashFileVers(String cdEncodingHashFileVers) {
        this.cdEncodingHashFileVers = cdEncodingHashFileVers;
    }

    @Column(name = "CD_ERR")
    public String getCdErr() {
        return this.cdErr;
    }

    public void setCdErr(String cdErr) {
        this.cdErr = cdErr;
    }

    @Column(name = "CD_KEY_OBJECT_TRASF")
    public String getCdKeyObjectTrasf() {
        return this.cdKeyObjectTrasf;
    }

    public void setCdKeyObjectTrasf(String cdKeyObjectTrasf) {
        this.cdKeyObjectTrasf = cdKeyObjectTrasf;
    }

    @Column(name = "DL_ERR")
    public String getDlErr() {
        return this.dlErr;
    }

    public void setDlErr(String dlErr) {
        this.dlErr = dlErr;
    }

    @Column(name = "DS_HASH_FILE_VERS")
    public String getDsHashFileVers() {
        return this.dsHashFileVers;
    }

    public void setDsHashFileVers(String dsHashFileVers) {
        this.dsHashFileVers = dsHashFileVers;
    }

    @Column(name = "DS_OBJECT_TRASF")
    public String getDsObjectTrasf() {
        return this.dsObjectTrasf;
    }

    public void setDsObjectTrasf(String dsObjectTrasf) {
        this.dsObjectTrasf = dsObjectTrasf;
    }

    @Column(name = "DS_PATH")
    public String getDsPath() {
        return this.dsPath;
    }

    public void setDsPath(String dsPath) {
        this.dsPath = dsPath;
    }

    @Column(name = "PG_OGGETTO_TRASF")
    public BigDecimal getPgOggettoTrasf() {
        return this.pgOggettoTrasf;
    }

    public void setPgOggettoTrasf(BigDecimal pgOggettoTrasf) {
        this.pgOggettoTrasf = pgOggettoTrasf;
    }

    @Column(name = "TI_ALGO_HASH_FILE_VERS")
    public String getTiAlgoHashFileVers() {
        return this.tiAlgoHashFileVers;
    }

    public void setTiAlgoHashFileVers(String tiAlgoHashFileVers) {
        this.tiAlgoHashFileVers = tiAlgoHashFileVers;
    }

    // bi-directional many-to-one association to PigObject
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_OBJECT")
    public PigObject getPigObject() {
        return this.pigObject;
    }

    public void setPigObject(PigObject pigObject) {
        this.pigObject = pigObject;
    }

    // bi-directional many-to-one association to PigTipoObject
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_TIPO_OBJECT")
    public PigTipoObject getPigTipoObject() {
        return this.pigTipoObject;
    }

    public void setPigTipoObject(PigTipoObject pigTipoObject) {
        this.pigTipoObject = pigTipoObject;
    }

    // bi-directional many-to-one association to PigVer
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_VERS")
    public PigVers getPigVer() {
        return this.pigVer;
    }

    public void setPigVer(PigVers pigVer) {
        this.pigVer = pigVer;
    }

    // bi-directional many-to-one association to PigXmlObjectTrasf
    @OneToMany(mappedBy = "pigObjectTrasf", cascade = { CascadeType.PERSIST })
    public List<PigXmlObjectTrasf> getPigXmlObjectTrasfs() {
        return this.pigXmlObjectTrasfs;
    }

    public void setPigXmlObjectTrasfs(List<PigXmlObjectTrasf> pigXmlObjectTrasfs) {
        this.pigXmlObjectTrasfs = pigXmlObjectTrasfs;
    }

    public PigXmlObjectTrasf addPigXmlObjectTrasf(PigXmlObjectTrasf pigXmlObjectTrasf) {
        getPigXmlObjectTrasfs().add(pigXmlObjectTrasf);
        pigXmlObjectTrasf.setPigObjectTrasf(this);

        return pigXmlObjectTrasf;
    }

    public PigXmlObjectTrasf removePigXmlObjectTrasf(PigXmlObjectTrasf pigXmlObjectTrasf) {
        getPigXmlObjectTrasfs().remove(pigXmlObjectTrasf);
        pigXmlObjectTrasf.setPigObjectTrasf(null);

        return pigXmlObjectTrasf;
    }

}
