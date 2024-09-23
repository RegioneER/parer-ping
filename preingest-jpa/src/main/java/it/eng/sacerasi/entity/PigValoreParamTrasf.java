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
 * The persistent class for the PIG_VALORE_PARAM_TRASF database table.
 *
 */
@Entity
@Table(name = "PIG_VALORE_PARAM_TRASF")
public class PigValoreParamTrasf implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long idValoreParamTrasf;
    private String dsValoreParam;
    private PigValoreSetParamTrasf pigValoreSetParamTrasf;
    private XfoParamTrasf xfoParamTrasf;

    public PigValoreParamTrasf() {
        // hibernate
    }

    @Id
    @GenericGenerator(name = "PIG_VALORE_PARAM_TRASF_IDVALOREPARAMTRASF_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
            @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SPIG_VALORE_PARAM_TRASF"),
            @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PIG_VALORE_PARAM_TRASF_IDVALOREPARAMTRASF_GENERATOR")
    @Column(name = "ID_VALORE_PARAM_TRASF")
    public Long getIdValoreParamTrasf() {
        return this.idValoreParamTrasf;
    }

    public void setIdValoreParamTrasf(Long idValoreParamTrasf) {
        this.idValoreParamTrasf = idValoreParamTrasf;
    }

    @Column(name = "DS_VALORE_PARAM")
    public String getDsValoreParam() {
        return this.dsValoreParam;
    }

    public void setDsValoreParam(String dsValoreParam) {
        this.dsValoreParam = dsValoreParam;
    }

    // bi-directional many-to-one association to PigValoreSetParamTrasf
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_VALORE_SET_PARAM_TRASF")
    public PigValoreSetParamTrasf getPigValoreSetParamTrasf() {
        return this.pigValoreSetParamTrasf;
    }

    public void setPigValoreSetParamTrasf(PigValoreSetParamTrasf pigValoreSetParamTrasf) {
        this.pigValoreSetParamTrasf = pigValoreSetParamTrasf;
    }

    // bi-directional many-to-one association to XfoParamTrasf
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_PARAM_TRASF")
    public XfoParamTrasf getXfoParamTrasf() {
        return this.xfoParamTrasf;
    }

    public void setXfoParamTrasf(XfoParamTrasf xfoParamTrasf) {
        this.xfoParamTrasf = xfoParamTrasf;
    }

}
