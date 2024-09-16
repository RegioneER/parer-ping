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
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.id.enhanced.SequenceStyleGenerator;

/**
 * The persistent class for the PIG_XML_OBJECT_TRASF database table.
 *
 */
@Entity
@Table(name = "PIG_XML_OBJECT_TRASF")
@NamedQuery(name = "PigXmlObjectTrasf.findAll", query = "SELECT p FROM PigXmlObjectTrasf p")
public class PigXmlObjectTrasf implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long idXmlObjectTrasf;
    private String blXml;
    private String cdVersioneXmlVers;
    private PigObjectTrasf pigObjectTrasf;

    public PigXmlObjectTrasf() {
        // hibernate
    }

    @Id
    @GenericGenerator(name = "PIG_XML_OBJECT_TRASF_IDXMLOBJECTTRASF_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
            @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SPIG_XML_OBJECT_TRASF"),
            @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PIG_XML_OBJECT_TRASF_IDXMLOBJECTTRASF_GENERATOR")
    @Column(name = "ID_XML_OBJECT_TRASF")
    public Long getIdXmlObjectTrasf() {
        return this.idXmlObjectTrasf;
    }

    public void setIdXmlObjectTrasf(Long idXmlObjectTrasf) {
        this.idXmlObjectTrasf = idXmlObjectTrasf;
    }

    @Lob
    @Column(name = "BL_XML")
    public String getBlXml() {
        return this.blXml;
    }

    public void setBlXml(String blXml) {
        this.blXml = blXml;
    }

    @Column(name = "CD_VERSIONE_XML_VERS")
    public String getCdVersioneXmlVers() {
        return this.cdVersioneXmlVers;
    }

    public void setCdVersioneXmlVers(String cdVersioneXmlVers) {
        this.cdVersioneXmlVers = cdVersioneXmlVers;
    }

    // bi-directional many-to-one association to PigObjectTrasf
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_OBJECT_TRASF")
    public PigObjectTrasf getPigObjectTrasf() {
        return this.pigObjectTrasf;
    }

    public void setPigObjectTrasf(PigObjectTrasf pigObjectTrasf) {
        this.pigObjectTrasf = pigObjectTrasf;
    }

}
