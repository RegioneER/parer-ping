/*
 * Engineering Ingegneria Informatica S.p.A.
 *
 * Copyright (C) 2023 Regione Emilia-Romagna <p/> This program is free software: you can
 * redistribute it and/or modify it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version. <p/> This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Affero General Public License for more details. <p/> You should
 * have received a copy of the GNU Affero General Public License along with this program. If not,
 * see <https://www.gnu.org/licenses/>.
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
 * The persistent class for the PIG_XML_SACER_UNITA_DOC_SES database table.
 *
 */
@Entity
@Table(name = "PIG_XML_SACER_UNITA_DOC_SES")
public class PigXmlSacerUnitaDocSes implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long idXmlSacerUnitaDocSes;
    private String blXmlSacer;
    private String tiXmlSacer;
    private PigUnitaDocSessione pigUnitaDocSessione;
    private Long idVers;

    public PigXmlSacerUnitaDocSes() {
	// hibernate
    }

    @Id
    @GenericGenerator(name = "PIG_XML_SACER_UNITA_DOC_SES_IDXMLSACERUNITADOCSES_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
	    @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SPIG_XML_SACER_UNITA_DOC_SES"),
	    @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PIG_XML_SACER_UNITA_DOC_SES_IDXMLSACERUNITADOCSES_GENERATOR")
    @Column(name = "ID_XML_SACER_UNITA_DOC_SES")
    public Long getIdXmlSacerUnitaDocSes() {
	return this.idXmlSacerUnitaDocSes;
    }

    public void setIdXmlSacerUnitaDocSes(Long idXmlSacerUnitaDocSes) {
	this.idXmlSacerUnitaDocSes = idXmlSacerUnitaDocSes;
    }

    @Lob
    @Column(name = "BL_XML_SACER")
    public String getBlXmlSacer() {
	return this.blXmlSacer;
    }

    public void setBlXmlSacer(String blXmlSacer) {
	this.blXmlSacer = blXmlSacer;
    }

    @Column(name = "TI_XML_SACER")
    public String getTiXmlSacer() {
	return this.tiXmlSacer;
    }

    public void setTiXmlSacer(String tiXmlSacer) {
	this.tiXmlSacer = tiXmlSacer;
    }

    // bi-directional many-to-one association to PigUnitaDocSessione
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_UNITA_DOC_SESSIONE")
    public PigUnitaDocSessione getPigUnitaDocSessione() {
	return this.pigUnitaDocSessione;
    }

    public void setPigUnitaDocSessione(PigUnitaDocSessione pigUnitaDocSessione) {
	this.pigUnitaDocSessione = pigUnitaDocSessione;
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
