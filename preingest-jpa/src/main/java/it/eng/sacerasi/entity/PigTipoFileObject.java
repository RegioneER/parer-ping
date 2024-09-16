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
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.eclipse.persistence.oxm.annotations.XmlInverseReference;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.id.enhanced.SequenceStyleGenerator;

/**
 * The persistent class for the PIG_TIPO_FILE_OBJECT database table.
 *
 */
@Entity

@Table(name = "PIG_TIPO_FILE_OBJECT")
public class PigTipoFileObject implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long idTipoFileObject;
    private String dsFmtRapprCalcSacer;
    private String dsFmtRapprEstesoCalcSacer;
    private String dsTipoFileObject;
    private String flCalcHashSacer;
    private String flVerFirmaFmtSacer;
    private String flVersSacerAsinc;
    private String nmFmtFileCalcSacer;
    private String nmFmtFileVersSacer;
    private String nmTipoCompDocSacer;
    private String nmTipoDocSacer;
    private String nmTipoFileObject;
    private String nmTipoStrutDocSacer;
    private String tiCalcHashSacer;
    private String tiDocSacer;
    private List<PigFileObject> pigFileObjects = new ArrayList<>();
    private PigTipoObject pigTipoObject;
    private List<PigXsdDatiSpec> pigXsdDatiSpecs = new ArrayList<>();

    public PigTipoFileObject() {
        // non utilizzato
    }

    @Id
    @GenericGenerator(name = "PIG_TIPO_FILE_OBJECT_IDTIPOFILEOBJECT_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
            @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SPIG_TIPO_FILE_OBJECT"),
            @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PIG_TIPO_FILE_OBJECT_IDTIPOFILEOBJECT_GENERATOR")
    @Column(name = "ID_TIPO_FILE_OBJECT")
    public Long getIdTipoFileObject() {
        return this.idTipoFileObject;
    }

    public void setIdTipoFileObject(Long idTipoFileObject) {
        this.idTipoFileObject = idTipoFileObject;
    }

    @Column(name = "DS_FMT_RAPPR_CALC_SACER")
    public String getDsFmtRapprCalcSacer() {
        return this.dsFmtRapprCalcSacer;
    }

    public void setDsFmtRapprCalcSacer(String dsFmtRapprCalcSacer) {
        this.dsFmtRapprCalcSacer = dsFmtRapprCalcSacer;
    }

    @Column(name = "DS_FMT_RAPPR_ESTESO_CALC_SACER")
    public String getDsFmtRapprEstesoCalcSacer() {
        return this.dsFmtRapprEstesoCalcSacer;
    }

    public void setDsFmtRapprEstesoCalcSacer(String dsFmtRapprEstesoCalcSacer) {
        this.dsFmtRapprEstesoCalcSacer = dsFmtRapprEstesoCalcSacer;
    }

    @Column(name = "DS_TIPO_FILE_OBJECT")
    public String getDsTipoFileObject() {
        return this.dsTipoFileObject;
    }

    public void setDsTipoFileObject(String dsTipoFileObject) {
        this.dsTipoFileObject = dsTipoFileObject;
    }

    @Column(name = "FL_CALC_HASH_SACER", columnDefinition = "char")
    public String getFlCalcHashSacer() {
        return this.flCalcHashSacer;
    }

    public void setFlCalcHashSacer(String flCalcHashSacer) {
        this.flCalcHashSacer = flCalcHashSacer;
    }

    @Column(name = "FL_VER_FIRMA_FMT_SACER", columnDefinition = "char")
    public String getFlVerFirmaFmtSacer() {
        return this.flVerFirmaFmtSacer;
    }

    public void setFlVerFirmaFmtSacer(String flVerFirmaFmtSacer) {
        this.flVerFirmaFmtSacer = flVerFirmaFmtSacer;
    }

    @Column(name = "FL_VERS_SACER_ASINC", columnDefinition = "char")
    public String getFlVersSacerAsinc() {
        return this.flVersSacerAsinc;
    }

    public void setFlVersSacerAsinc(String flVersSacerAsinc) {
        this.flVersSacerAsinc = flVersSacerAsinc;
    }

    @Column(name = "NM_FMT_FILE_CALC_SACER")
    public String getNmFmtFileCalcSacer() {
        return this.nmFmtFileCalcSacer;
    }

    public void setNmFmtFileCalcSacer(String nmFmtFileCalcSacer) {
        this.nmFmtFileCalcSacer = nmFmtFileCalcSacer;
    }

    @Column(name = "NM_FMT_FILE_VERS_SACER")
    public String getNmFmtFileVersSacer() {
        return this.nmFmtFileVersSacer;
    }

    public void setNmFmtFileVersSacer(String nmFmtFileVersSacer) {
        this.nmFmtFileVersSacer = nmFmtFileVersSacer;
    }

    @Column(name = "NM_TIPO_COMP_DOC_SACER")
    public String getNmTipoCompDocSacer() {
        return this.nmTipoCompDocSacer;
    }

    public void setNmTipoCompDocSacer(String nmTipoCompDocSacer) {
        this.nmTipoCompDocSacer = nmTipoCompDocSacer;
    }

    @Column(name = "NM_TIPO_DOC_SACER")
    public String getNmTipoDocSacer() {
        return this.nmTipoDocSacer;
    }

    public void setNmTipoDocSacer(String nmTipoDocSacer) {
        this.nmTipoDocSacer = nmTipoDocSacer;
    }

    @Column(name = "NM_TIPO_FILE_OBJECT")
    public String getNmTipoFileObject() {
        return this.nmTipoFileObject;
    }

    public void setNmTipoFileObject(String nmTipoFileObject) {
        this.nmTipoFileObject = nmTipoFileObject;
    }

    @Column(name = "NM_TIPO_STRUT_DOC_SACER")
    public String getNmTipoStrutDocSacer() {
        return this.nmTipoStrutDocSacer;
    }

    public void setNmTipoStrutDocSacer(String nmTipoStrutDocSacer) {
        this.nmTipoStrutDocSacer = nmTipoStrutDocSacer;
    }

    @Column(name = "TI_CALC_HASH_SACER")
    public String getTiCalcHashSacer() {
        return this.tiCalcHashSacer;
    }

    public void setTiCalcHashSacer(String tiCalcHashSacer) {
        this.tiCalcHashSacer = tiCalcHashSacer;
    }

    @Column(name = "TI_DOC_SACER")
    public String getTiDocSacer() {
        return this.tiDocSacer;
    }

    public void setTiDocSacer(String tiDocSacer) {
        this.tiDocSacer = tiDocSacer;
    }

    // bi-directional many-to-one association to PigFileObject
    @OneToMany(mappedBy = "pigTipoFileObject", cascade = CascadeType.PERSIST)
    public List<PigFileObject> getPigFileObjects() {
        return this.pigFileObjects;
    }

    public void setPigFileObjects(List<PigFileObject> pigFileObjects) {
        this.pigFileObjects = pigFileObjects;
    }

    // bi-directional many-to-one association to PigTipoObject
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_TIPO_OBJECT")
    @XmlInverseReference(mappedBy = "pigTipoFileObjects")
    public PigTipoObject getPigTipoObject() {
        return this.pigTipoObject;
    }

    public void setPigTipoObject(PigTipoObject pigTipoObject) {
        this.pigTipoObject = pigTipoObject;
    }

    // bi-directional many-to-one association to PigXsdDatiSpec
    @OneToMany(mappedBy = "pigTipoFileObject")
    public List<PigXsdDatiSpec> getPigXsdDatiSpecs() {
        return this.pigXsdDatiSpecs;
    }

    public void setPigXsdDatiSpecs(List<PigXsdDatiSpec> pigXsdDatiSpecs) {
        this.pigXsdDatiSpecs = pigXsdDatiSpecs;
    }

}
