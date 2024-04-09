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
 * The persistent class for the PIG_VERS_TIPO_OBJECT_DA_TRASF database table.
 *
 */
@Entity
@Table(name = "PIG_VERS_TIPO_OBJECT_DA_TRASF")
@NamedQuery(name = "PigVersTipoObjectDaTrasf.findAll", query = "SELECT p FROM PigVersTipoObjectDaTrasf p")
public class PigVersTipoObjectDaTrasf implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long idVersTipoObjectDaTrasf;
    private String cdVersGen;
    private List<PigValoreSetParamTrasf> pigValoreSetParamTrasfs = new ArrayList<>();
    private PigTipoObject pigTipoObjectGen;
    private PigTipoObject pigTipoObjectDaTrasf;
    private PigVers pigVersGen;

    public PigVersTipoObjectDaTrasf() {
        // hibernate
    }

    @Id
    @GenericGenerator(name = "PIG_VERS_TIPO_OBJECT_DA_TRASF_IDVERSTIPOOBJECTDATRASF_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
            @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SPIG_VERS_TIPO_OBJECT_DA_TRASF"),
            @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PIG_VERS_TIPO_OBJECT_DA_TRASF_IDVERSTIPOOBJECTDATRASF_GENERATOR")
    @Column(name = "ID_VERS_TIPO_OBJECT_DA_TRASF")
    public Long getIdVersTipoObjectDaTrasf() {
        return this.idVersTipoObjectDaTrasf;
    }

    public void setIdVersTipoObjectDaTrasf(Long idVersTipoObjectDaTrasf) {
        this.idVersTipoObjectDaTrasf = idVersTipoObjectDaTrasf;
    }

    @Column(name = "CD_VERS_GEN")
    public String getCdVersGen() {
        return this.cdVersGen;
    }

    public void setCdVersGen(String cdVersGen) {
        this.cdVersGen = cdVersGen;
    }

    // bi-directional many-to-one association to PigValoreSetParamTrasf
    @OneToMany(mappedBy = "pigVersTipoObjectDaTrasf")
    public List<PigValoreSetParamTrasf> getPigValoreSetParamTrasfs() {
        return this.pigValoreSetParamTrasfs;
    }

    public void setPigValoreSetParamTrasfs(List<PigValoreSetParamTrasf> pigValoreSetParamTrasfs) {
        this.pigValoreSetParamTrasfs = pigValoreSetParamTrasfs;
    }

    public PigValoreSetParamTrasf addPigValoreSetParamTrasf(PigValoreSetParamTrasf pigValoreSetParamTrasf) {
        getPigValoreSetParamTrasfs().add(pigValoreSetParamTrasf);
        pigValoreSetParamTrasf.setPigVersTipoObjectDaTrasf(this);

        return pigValoreSetParamTrasf;
    }

    public PigValoreSetParamTrasf removePigValoreSetParamTrasf(PigValoreSetParamTrasf pigValoreSetParamTrasf) {
        getPigValoreSetParamTrasfs().remove(pigValoreSetParamTrasf);
        pigValoreSetParamTrasf.setPigVersTipoObjectDaTrasf(null);

        return pigValoreSetParamTrasf;
    }

    // bi-directional many-to-one association to PigTipoObject
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_TIPO_OBJECT_GEN")
    public PigTipoObject getPigTipoObjectGen() {
        return this.pigTipoObjectGen;
    }

    public void setPigTipoObjectGen(PigTipoObject pigTipoObjectGen) {
        this.pigTipoObjectGen = pigTipoObjectGen;
    }

    // bi-directional many-to-one association to PigTipoObject
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_TIPO_OBJECT_DA_TRASF")
    public PigTipoObject getPigTipoObjectDaTrasf() {
        return this.pigTipoObjectDaTrasf;
    }

    public void setPigTipoObjectDaTrasf(PigTipoObject pigTipoObjectDaTrasf) {
        this.pigTipoObjectDaTrasf = pigTipoObjectDaTrasf;
    }

    // bi-directional many-to-one association to PigVer
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_VERS_GEN")
    public PigVers getPigVersGen() {
        return this.pigVersGen;
    }

    public void setPigVersGen(PigVers pigVersGen) {
        this.pigVersGen = pigVersGen;
    }

}
