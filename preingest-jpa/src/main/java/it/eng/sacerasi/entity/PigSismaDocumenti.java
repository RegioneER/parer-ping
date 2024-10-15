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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * The persistent class for the PIG_SISMA_DOCUMENTI database table.
 *
 */
@Entity
@Table(name = "PIG_SISMA_DOCUMENTI")
public class PigSismaDocumenti implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long idSismaDocumenti;
    private String nmFileOs;
    private String cdErr;
    private String dsErr;
    private String flDeleted;
    private String flEsitoVerifica;
    private String tiVerificaAgenzia;
    private PigSisma pigSisma;
    private PigSismaValDoc pigSismaValDoc;
    private String nmFileOrig;
    private BigDecimal numFiles;
    private BigDecimal dimensione;
    private Date dtCaricamento;
    private String flDocRicaricato;
    private List<PigSismaDocEntry> pigSismaDocEntrys = new ArrayList<>();
    private String blReport;

    public PigSismaDocumenti() {
        // non usato
    }

    @Id
    @SequenceGenerator(name = "PIG_SISMA_DOCUMENTI_IDSISMADOCUMENTI_GENERATOR", sequenceName = "SPIG_SISMA_DOCUMENTI", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PIG_SISMA_DOCUMENTI_IDSISMADOCUMENTI_GENERATOR")
    @Column(name = "ID_SISMA_DOCUMENTI")
    public Long getIdSismaDocumenti() {
        return this.idSismaDocumenti;
    }

    public void setIdSismaDocumenti(Long idSismaDocumenti) {
        this.idSismaDocumenti = idSismaDocumenti;
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

    @Column(name = "FL_DELETED", columnDefinition = "CHAR")
    public String getFlDeleted() {
        return this.flDeleted;
    }

    public void setFlDeleted(String flDeleted) {
        this.flDeleted = flDeleted;
    }

    @Column(name = "FL_ESITO_VERIFICA", columnDefinition = "CHAR")
    public String getFlEsitoVerifica() {
        return this.flEsitoVerifica;
    }

    public void setFlEsitoVerifica(String flEsitoVerifica) {
        this.flEsitoVerifica = flEsitoVerifica;
    }

    @Column(name = "TI_VERIFICA_AGENZIA")
    public String getTiVerificaAgenzia() {
        return tiVerificaAgenzia;
    }

    public void setTiVerificaAgenzia(String tiVerificaAgenzia) {
        this.tiVerificaAgenzia = tiVerificaAgenzia;
    }

    // bi-directional many-to-one association to PigSisma
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_SISMA")
    public PigSisma getPigSisma() {
        return this.pigSisma;
    }

    public void setPigSisma(PigSisma pigSisma) {
        this.pigSisma = pigSisma;
    }

    // bi-directional many-to-one association to PigSismaValDoc
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_SISMA_VAL_DOC")
    public PigSismaValDoc getPigSismaValDoc() {
        return this.pigSismaValDoc;
    }

    public void setPigSismaValDoc(PigSismaValDoc pigSismaValDoc) {
        this.pigSismaValDoc = pigSismaValDoc;
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

    @Column(name = "FL_DOC_RICARICATO", columnDefinition = "CHAR")
    public String getFlDocRicaricato() {
        return flDocRicaricato;
    }

    public void setFlDocRicaricato(String flDocRicaricato) {
        this.flDocRicaricato = flDocRicaricato;
    }

    // bi-directional many-to-one association to PigSismaDocEntry
    @OneToMany(mappedBy = "pigSismaDocumenti")
    public List<PigSismaDocEntry> getPigSismaDocEntrys() {
        return this.pigSismaDocEntrys;
    }

    public void setPigSismaDocEntrys(List<PigSismaDocEntry> pigSismaDocEntrys) {
        this.pigSismaDocEntrys = pigSismaDocEntrys;
    }

    public PigSismaDocEntry addPigSismaDocEntry(PigSismaDocEntry pigSismaDocEtry) {
        getPigSismaDocEntrys().add(pigSismaDocEtry);
        pigSismaDocEtry.setPigSismaDocumenti(this);

        return pigSismaDocEtry;
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
