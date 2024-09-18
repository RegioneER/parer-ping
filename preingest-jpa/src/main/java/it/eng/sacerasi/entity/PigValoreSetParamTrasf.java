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

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.id.enhanced.SequenceStyleGenerator;

/**
 * The persistent class for the PIG_VALORE_SET_PARAM_TRASF database table.
 *
 */
@Entity
@Table(name = "PIG_VALORE_SET_PARAM_TRASF")
public class PigValoreSetParamTrasf implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long idValoreSetParamTrasf;
    private List<PigValoreParamTrasf> pigValoreParamTrasfs = new ArrayList<>();
    private PigVersTipoObjectDaTrasf pigVersTipoObjectDaTrasf;
    private XfoSetParamTrasf xfoSetParamTrasf;

    public PigValoreSetParamTrasf() {
        // hibernate
    }

    @Id
    @GenericGenerator(name = "PIG_VALORE_SET_PARAM_TRASF_IDVALORESETPARAMTRASF_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
            @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SPIG_VALORE_SET_PARAM_TRASF"),
            @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PIG_VALORE_SET_PARAM_TRASF_IDVALORESETPARAMTRASF_GENERATOR")
    @Column(name = "ID_VALORE_SET_PARAM_TRASF")
    public Long getIdValoreSetParamTrasf() {
        return this.idValoreSetParamTrasf;
    }

    public void setIdValoreSetParamTrasf(Long idValoreSetParamTrasf) {
        this.idValoreSetParamTrasf = idValoreSetParamTrasf;
    }

    // bi-directional many-to-one association to PigValoreParamTrasf
    @OneToMany(mappedBy = "pigValoreSetParamTrasf", cascade = { CascadeType.REMOVE })
    public List<PigValoreParamTrasf> getPigValoreParamTrasfs() {
        return this.pigValoreParamTrasfs;
    }

    public void setPigValoreParamTrasfs(List<PigValoreParamTrasf> pigValoreParamTrasfs) {
        this.pigValoreParamTrasfs = pigValoreParamTrasfs;
    }

    public PigValoreParamTrasf addPigValoreParamTrasf(PigValoreParamTrasf pigValoreParamTrasf) {
        getPigValoreParamTrasfs().add(pigValoreParamTrasf);
        pigValoreParamTrasf.setPigValoreSetParamTrasf(this);

        return pigValoreParamTrasf;
    }

    public PigValoreParamTrasf removePigValoreParamTrasf(PigValoreParamTrasf pigValoreParamTrasf) {
        getPigValoreParamTrasfs().remove(pigValoreParamTrasf);
        pigValoreParamTrasf.setPigValoreSetParamTrasf(null);

        return pigValoreParamTrasf;
    }

    // bi-directional many-to-one association to PigVersTipoObjectDaTrasf
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_VERS_TIPO_OBJECT_DA_TRASF")
    public PigVersTipoObjectDaTrasf getPigVersTipoObjectDaTrasf() {
        return this.pigVersTipoObjectDaTrasf;
    }

    public void setPigVersTipoObjectDaTrasf(PigVersTipoObjectDaTrasf pigVersTipoObjectDaTrasf) {
        this.pigVersTipoObjectDaTrasf = pigVersTipoObjectDaTrasf;
    }

    // bi-directional many-to-one association to XfoSetParamTrasf
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_SET_PARAM_TRASF")
    public XfoSetParamTrasf getXfoSetParamTrasf() {
        return this.xfoSetParamTrasf;
    }

    public void setXfoSetParamTrasf(XfoSetParamTrasf xfoSetParamTrasf) {
        this.xfoSetParamTrasf = xfoSetParamTrasf;
    }

}
