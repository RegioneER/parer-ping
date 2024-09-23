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
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.id.enhanced.SequenceStyleGenerator;

/**
 * The persistent class for the PIG_XML_ANNUL_SESSIONE_INGEST database table.
 *
 */
@Entity
@Table(name = "PIG_XML_ANNUL_SESSIONE_INGEST")
public class PigXmlAnnulSessioneIngest implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long idXmlAnnulSessioneIngest;
    private String blXmlAnnul;
    private String cdVersioneXmlAnnul;
    private Date dtRegXmlAnnul;
    private String tiXmlAnnul;
    private PigSessioneIngest pigSessioneIngest;

    public PigXmlAnnulSessioneIngest() {
        // hibernate
    }

    @Id
    @GenericGenerator(name = "PIG_XML_ANNUL_SESSIONE_INGEST_IDXMLANNULSESSIONEINGEST_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
            @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SPIG_XML_ANNUL_SESSIONE_INGEST"),
            @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PIG_XML_ANNUL_SESSIONE_INGEST_IDXMLANNULSESSIONEINGEST_GENERATOR")
    @Column(name = "ID_XML_ANNUL_SESSIONE_INGEST")
    public Long getIdXmlAnnulSessioneIngest() {
        return this.idXmlAnnulSessioneIngest;
    }

    public void setIdXmlAnnulSessioneIngest(Long idXmlAnnulSessioneIngest) {
        this.idXmlAnnulSessioneIngest = idXmlAnnulSessioneIngest;
    }

    @Lob
    @Column(name = "BL_XML_ANNUL")
    public String getBlXmlAnnul() {
        return this.blXmlAnnul;
    }

    public void setBlXmlAnnul(String blXmlAnnul) {
        this.blXmlAnnul = blXmlAnnul;
    }

    @Column(name = "CD_VERSIONE_XML_ANNUL")
    public String getCdVersioneXmlAnnul() {
        return this.cdVersioneXmlAnnul;
    }

    public void setCdVersioneXmlAnnul(String cdVersioneXmlAnnul) {
        this.cdVersioneXmlAnnul = cdVersioneXmlAnnul;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_REG_XML_ANNUL")
    public Date getDtRegXmlAnnul() {
        return this.dtRegXmlAnnul;
    }

    public void setDtRegXmlAnnul(Date dtRegXmlAnnul) {
        this.dtRegXmlAnnul = dtRegXmlAnnul;
    }

    @Column(name = "TI_XML_ANNUL")
    public String getTiXmlAnnul() {
        return this.tiXmlAnnul;
    }

    public void setTiXmlAnnul(String tiXmlAnnul) {
        this.tiXmlAnnul = tiXmlAnnul;
    }

    // bi-directional many-to-one association to PigSessioneIngest
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_SESSIONE_INGEST")
    public PigSessioneIngest getPigSessioneIngest() {
        return this.pigSessioneIngest;
    }

    public void setPigSessioneIngest(PigSessioneIngest pigSessioneIngest) {
        this.pigSessioneIngest = pigSessioneIngest;
    }

}
