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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.eclipse.persistence.oxm.annotations.XmlInverseReference;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.id.enhanced.SequenceStyleGenerator;

/**
 * The persistent class for the PIG_SOP_CLASS_DICOM database table.
 *
 */
@Entity
@Table(name = "PIG_SOP_CLASS_DICOM")
public class PigSopClassDicom implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long idSopClassDicom;
    private String cdSopClassDicom;
    private String dsSopClassDicom;
    private List<PigSopClassDicomVers> pigSopClassDicomVers = new ArrayList<>();

    public PigSopClassDicom() {
        // for Hibernate
    }

    @Id
    @GenericGenerator(name = "PIG_SOP_CLASS_DICOM_IDSOPCLASSDICOM_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
            @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SPIG_SOP_CLASS_DICOM"),
            @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PIG_SOP_CLASS_DICOM_IDSOPCLASSDICOM_GENERATOR")
    @Column(name = "ID_SOP_CLASS_DICOM")
    public Long getIdSopClassDicom() {
        return this.idSopClassDicom;
    }

    public void setIdSopClassDicom(Long idSopClassDicom) {
        this.idSopClassDicom = idSopClassDicom;
    }

    @Column(name = "CD_SOP_CLASS_DICOM")
    public String getCdSopClassDicom() {
        return this.cdSopClassDicom;
    }

    public void setCdSopClassDicom(String cdSopClassDicom) {
        this.cdSopClassDicom = cdSopClassDicom;
    }

    @Column(name = "DS_SOP_CLASS_DICOM")
    public String getDsSopClassDicom() {
        return this.dsSopClassDicom;
    }

    public void setDsSopClassDicom(String dsSopClassDicom) {
        this.dsSopClassDicom = dsSopClassDicom;
    }

    // bi-directional many-to-one association to PigSopClassDicomVers
    @OneToMany(mappedBy = "pigSopClassDicom")
    @XmlInverseReference(mappedBy = "pigSopClassDicom")
    public List<PigSopClassDicomVers> getPigSopClassDicomVers() {
        return this.pigSopClassDicomVers;
    }

    public void setPigSopClassDicomVers(List<PigSopClassDicomVers> pigSopClassDicomVers) {
        this.pigSopClassDicomVers = pigSopClassDicomVers;
    }

}
