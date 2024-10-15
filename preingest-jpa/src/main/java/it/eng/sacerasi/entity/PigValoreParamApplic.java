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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.id.enhanced.SequenceStyleGenerator;

/**
 * The persistent class for the PIG_VALORE_PARAM_APPLIC database table.
 *
 */
@Entity
@Table(name = "PIG_VALORE_PARAM_APPLIC")
public class PigValoreParamApplic implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long idValoreParamApplic;
    private String dsValoreParamApplic;
    private String tiAppart;
    private PigAmbienteVers pigAmbienteVer;
    private PigParamApplic pigParamApplic;
    private PigTipoObject pigTipoObject;
    private PigVers pigVer;

    public PigValoreParamApplic() {
        // hibernate
    }

    @Id
    @GenericGenerator(name = "PIG_VALORE_PARAM_APPLIC_IDVALOREPARAMAPPLIC_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
            @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SPIG_VALORE_PARAM_APPLIC"),
            @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PIG_VALORE_PARAM_APPLIC_IDVALOREPARAMAPPLIC_GENERATOR")
    @Column(name = "ID_VALORE_PARAM_APPLIC")
    public Long getIdValoreParamApplic() {
        return this.idValoreParamApplic;
    }

    public void setIdValoreParamApplic(Long idValoreParamApplic) {
        this.idValoreParamApplic = idValoreParamApplic;
    }

    @Column(name = "DS_VALORE_PARAM_APPLIC")
    public String getDsValoreParamApplic() {
        return this.dsValoreParamApplic;
    }

    public void setDsValoreParamApplic(String dsValoreParamApplic) {
        this.dsValoreParamApplic = dsValoreParamApplic;
    }

    @Column(name = "TI_APPART")
    public String getTiAppart() {
        return this.tiAppart;
    }

    public void setTiAppart(String tiAppart) {
        this.tiAppart = tiAppart;
    }

    // bi-directional many-to-one association to PigAmbienteVer
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_AMBIENTE_VERS")
    public PigAmbienteVers getPigAmbienteVer() {
        return this.pigAmbienteVer;
    }

    public void setPigAmbienteVer(PigAmbienteVers pigAmbienteVer) {
        this.pigAmbienteVer = pigAmbienteVer;
    }

    // bi-directional many-to-one association to PigParamApplic
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_PARAM_APPLIC")
    public PigParamApplic getPigParamApplic() {
        return this.pigParamApplic;
    }

    public void setPigParamApplic(PigParamApplic pigParamApplic) {
        this.pigParamApplic = pigParamApplic;
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

}
