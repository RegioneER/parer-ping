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
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.eclipse.persistence.oxm.annotations.XmlInverseReference;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.id.enhanced.SequenceStyleGenerator;

/**
 * The persistent class for the PIG_XSD_DATI_SPEC database table.
 * 
 */
@Entity
@Table(name = "PIG_XSD_DATI_SPEC")
public class PigXsdDatiSpec implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long idXsdSpec;
    private String blXsd;
    private String cdVersioneXsd;
    private Date dtVersioneXsd;
    private String tiEntita;
    private List<PigAttribDatiSpec> pigAttribDatiSpecs = new ArrayList<>();
    private List<PigInfoDicom> pigInfoDicoms = new ArrayList<>();
    private PigTipoFileObject pigTipoFileObject;
    private PigTipoObject pigTipoObject;
    private PigVers pigVer;

    public PigXsdDatiSpec() {
        // hibernate
    }

    @Id
    @GenericGenerator(name = "PIG_XSD_DATI_SPEC_IDXSDSPEC_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
            @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SPIG_XSD_DATI_SPEC"),
            @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PIG_XSD_DATI_SPEC_IDXSDSPEC_GENERATOR")
    @Column(name = "ID_XSD_SPEC")
    public Long getIdXsdSpec() {
        return this.idXsdSpec;
    }

    public void setIdXsdSpec(Long idXsdSpec) {
        this.idXsdSpec = idXsdSpec;
    }

    @Lob()
    @Column(name = "BL_XSD")
    public String getBlXsd() {
        return this.blXsd;
    }

    public void setBlXsd(String blXsd) {
        this.blXsd = blXsd;
    }

    @Column(name = "CD_VERSIONE_XSD")
    public String getCdVersioneXsd() {
        return this.cdVersioneXsd;
    }

    public void setCdVersioneXsd(String cdVersioneXsd) {
        this.cdVersioneXsd = cdVersioneXsd;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_VERSIONE_XSD")
    public Date getDtVersioneXsd() {
        return this.dtVersioneXsd;
    }

    public void setDtVersioneXsd(Date dtVersioneXsd) {
        this.dtVersioneXsd = dtVersioneXsd;
    }

    @Column(name = "TI_ENTITA")
    public String getTiEntita() {
        return this.tiEntita;
    }

    public void setTiEntita(String tiEntita) {
        this.tiEntita = tiEntita;
    }

    // bi-directional many-to-one association to PigAttribDatiSpec
    @OneToMany(mappedBy = "pigXsdDatiSpec", cascade = CascadeType.PERSIST)
    @OrderBy("niOrd ASC")
    public List<PigAttribDatiSpec> getPigAttribDatiSpecs() {
        return this.pigAttribDatiSpecs;
    }

    public void setPigAttribDatiSpecs(List<PigAttribDatiSpec> pigAttribDatiSpecs) {
        this.pigAttribDatiSpecs = pigAttribDatiSpecs;
    }

    // bi-directional many-to-one association to PigInfoDicom
    @OneToMany(mappedBy = "pigXsdDatiSpec")
    public List<PigInfoDicom> getPigInfoDicoms() {
        return this.pigInfoDicoms;
    }

    public void setPigInfoDicoms(List<PigInfoDicom> pigInfoDicoms) {
        this.pigInfoDicoms = pigInfoDicoms;
    }

    // bi-directional many-to-one association to PigTipoFileObject
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_TIPO_FILE_OBJECT")
    @XmlInverseReference(mappedBy = "pigXsdDatiSpecs")
    public PigTipoFileObject getPigTipoFileObject() {
        return this.pigTipoFileObject;
    }

    public void setPigTipoFileObject(PigTipoFileObject pigTipoFileObject) {
        this.pigTipoFileObject = pigTipoFileObject;
    }

    // bi-directional many-to-one association to PigTipoObject
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_TIPO_OBJECT")
    @XmlInverseReference(mappedBy = "pigXsdDatiSpecs")
    public PigTipoObject getPigTipoObject() {
        return this.pigTipoObject;
    }

    public void setPigTipoObject(PigTipoObject pigTipoObject) {
        this.pigTipoObject = pigTipoObject;
    }

    // bi-directional many-to-one association to PigVers
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_VERS")
    @XmlInverseReference(mappedBy = "pigXsdDatiSpecs")
    public PigVers getPigVer() {
        return this.pigVer;
    }

    public void setPigVer(PigVers pigVer) {
        this.pigVer = pigVer;
    }

}
