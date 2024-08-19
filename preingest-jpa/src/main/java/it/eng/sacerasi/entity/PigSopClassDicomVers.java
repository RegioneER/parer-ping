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

import org.eclipse.persistence.oxm.annotations.XmlInverseReference;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.id.enhanced.SequenceStyleGenerator;

/**
 * The persistent class for the PIG_SOP_CLASS_DICOM_VERS database table.
 *
 */
@Entity
@Table(name = "PIG_SOP_CLASS_DICOM_VERS")
public class PigSopClassDicomVers implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long idSopClassDicomVers;
    private PigSopClassDicom pigSopClassDicom;
    private PigVers pigVer;

    public PigSopClassDicomVers() {
        // for Hibernate
    }

    @Id
    @GenericGenerator(name = "PIG_SOP_CLASS_DICOM_VERS_IDSOPCLASSDICOMVERS_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
            @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SPIG_SOP_CLASS_DICOM_VERS"),
            @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PIG_SOP_CLASS_DICOM_VERS_IDSOPCLASSDICOMVERS_GENERATOR")
    @Column(name = "ID_SOP_CLASS_DICOM_VERS")
    // @XmlID
    public Long getIdSopClassDicomVers() {
        return this.idSopClassDicomVers;
    }

    public void setIdSopClassDicomVers(Long idSopClassDicomVers) {
        this.idSopClassDicomVers = idSopClassDicomVers;
    }

    // bi-directional many-to-one association to PigSopClassDicom
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_SOP_CLASS_DICOM")

    public PigSopClassDicom getPigSopClassDicom() {
        return this.pigSopClassDicom;
    }

    public void setPigSopClassDicom(PigSopClassDicom pigSopClassDicom) {
        this.pigSopClassDicom = pigSopClassDicom;
    }

    // bi-directional many-to-one association to PigVers
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_VERS")
    @XmlInverseReference(mappedBy = "pigSopClassDicomVers")
    public PigVers getPigVer() {
        return this.pigVer;
    }

    public void setPigVer(PigVers pigVer) {
        this.pigVer = pigVer;
    }

}
