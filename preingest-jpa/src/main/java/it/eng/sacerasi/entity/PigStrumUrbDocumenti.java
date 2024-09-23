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
import java.math.BigDecimal;
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
 * The persistent class for the PIG_STRUM_URB_DOCUMENTI database table.
 *
 */
@Entity
@Table(name = "PIG_STRUM_URB_DOCUMENTI")
public class PigStrumUrbDocumenti implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long idStrumUrbDocumenti;
    private String nmFileOs;
    private String cdErr;
    private String dsErr;
    private String flDeleted;
    private String flEsitoVerifica;
    private PigStrumentiUrbanistici pigStrumentiUrbanistici;
    private PigStrumUrbValDoc pigStrumUrbValDoc;
    private String nmFileOrig;
    private BigDecimal numFiles;
    private BigDecimal dimensione;
    private Date dtCaricamento;
    private String blReport;

    public PigStrumUrbDocumenti() {
        // for Hibernate
    }

    @Id
    @GenericGenerator(name = "PIG_STRUM_URB_DOCUMENTI_IDSTRUMURBDOCUMENTI_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
            @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SPIG_STRUM_URB_DOCUMENTI"),
            @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PIG_STRUM_URB_DOCUMENTI_IDSTRUMURBDOCUMENTI_GENERATOR")
    @Column(name = "ID_STRUM_URB_DOCUMENTI")
    public Long getIdStrumUrbDocumenti() {
        return this.idStrumUrbDocumenti;
    }

    public void setIdStrumUrbDocumenti(Long idStrumUrbDocumenti) {
        this.idStrumUrbDocumenti = idStrumUrbDocumenti;
    }

    @Column(name = "NM_FILE_OS")
    public String getNmFileOs() {
        return this.nmFileOs;
    }

    public void setNmFileOs(String nmFileOs) {
        this.nmFileOs = nmFileOs;
    }

    @Column(name = "CD_ERR")
    public String getCdErr() {
        return this.cdErr;
    }

    public void setCdErr(String cdErr) {
        this.cdErr = cdErr;
    }

    @Column(name = "DS_ERR")
    public String getDsErr() {
        return this.dsErr;
    }

    public void setDsErr(String dsErr) {
        this.dsErr = dsErr;
    }

    @Column(name = "FL_DELETED", columnDefinition = "char")
    public String getFlDeleted() {
        return this.flDeleted;
    }

    public void setFlDeleted(String flDeleted) {
        this.flDeleted = flDeleted;
    }

    @Column(name = "FL_ESITO_VERIFICA", columnDefinition = "char")
    public String getFlEsitoVerifica() {
        return this.flEsitoVerifica;
    }

    public void setFlEsitoVerifica(String flEsitoVerifica) {
        this.flEsitoVerifica = flEsitoVerifica;
    }

    // bi-directional many-to-one association to PigStrumentiUrbanistici
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_STRUMENTI_URBANISTICI")
    public PigStrumentiUrbanistici getPigStrumentiUrbanistici() {
        return this.pigStrumentiUrbanistici;
    }

    public void setPigStrumentiUrbanistici(PigStrumentiUrbanistici pigStrumentiUrbanistici) {
        this.pigStrumentiUrbanistici = pigStrumentiUrbanistici;
    }

    // bi-directional many-to-one association to PigStrumUrbValDoc
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_STRUM_URB_VAL_DOC")
    public PigStrumUrbValDoc getPigStrumUrbValDoc() {
        return this.pigStrumUrbValDoc;
    }

    public void setPigStrumUrbValDoc(PigStrumUrbValDoc pigStrumUrbValDoc) {
        this.pigStrumUrbValDoc = pigStrumUrbValDoc;
    }

    @Column(name = "NM_FILE_ORIG")
    public String getNmFileOrig() {
        return this.nmFileOrig;
    }

    public void setNmFileOrig(String nmFileOrig) {
        this.nmFileOrig = nmFileOrig;
    }

    @Column(name = "NUM_FILES")
    public BigDecimal getNumFiles() {
        return this.numFiles;
    }

    public void setNumFiles(BigDecimal numFiles) {
        this.numFiles = numFiles;
    }

    @Column(name = "DIMENSIONE")
    public BigDecimal getDimensione() {
        return dimensione;
    }

    public void setDimensione(BigDecimal dimensione) {
        this.dimensione = dimensione;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_CARICAMENTO")
    public Date getDtCaricamento() {
        return dtCaricamento;
    }

    public void setDtCaricamento(Date dtCaricamento) {
        this.dtCaricamento = dtCaricamento;
    }

    @Lob()
    @Column(name = "BL_REPORT")
    public String getBlReport() {
        return blReport;
    }

    public void setBlReport(String blReport) {
        this.blReport = blReport;
    }
}
