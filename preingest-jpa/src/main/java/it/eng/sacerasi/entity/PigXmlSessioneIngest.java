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
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.id.enhanced.SequenceStyleGenerator;

/**
 * The persistent class for the PIG_XML_SESSIONE_INGEST database table.
 * 
 */
@Entity
@Table(name = "PIG_XML_SESSIONE_INGEST")
public class PigXmlSessioneIngest implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long idXmlSessioneIngest;
    private String blXml;
    private PigSessioneIngest pigSessioneIngest;
    private Long idVers;

    public PigXmlSessioneIngest() {
        // hibernate
    }

    @Id
    @GenericGenerator(name = "PIG_XML_SESSIONE_INGEST_IDXMLSESSIONEINGEST_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
            @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SPIG_XML_SESSIONE_INGEST"),
            @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PIG_XML_SESSIONE_INGEST_IDXMLSESSIONEINGEST_GENERATOR")
    @Column(name = "ID_XML_SESSIONE_INGEST")
    public Long getIdXmlSessioneIngest() {
        return this.idXmlSessioneIngest;
    }

    public void setIdXmlSessioneIngest(Long idXmlSessioneIngest) {
        this.idXmlSessioneIngest = idXmlSessioneIngest;
    }

    @Lob()
    @Column(name = "BL_XML")
    public String getBlXml() {
        return this.blXml;
    }

    public void setBlXml(String blXml) {
        this.blXml = blXml;
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

    // usata solo come chiave di partizionamento, non voglio la join con PigVers
    @Column(name = "ID_VERS")
    public Long getIdVers() {
        return this.idVers;
    }

    public void setIdVers(Long idVers) {
        this.idVers = idVers;
    }

}
