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
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.id.enhanced.SequenceStyleGenerator;

/**
 * The persistent class for the PIG_SESSIONE_RECUP database table.
 * 
 */
@Entity
@Table(name = "PIG_SESSIONE_RECUP")
public class PigSessioneRecup implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long idSessioneRecup;
    private String cdErr;
    private String cdKeyObject;
    private String dlErr;
    private Date dtApertura;
    private Date dtChiusura;
    private String nmAmbienteVers;
    private String nmVers;
    private String tiStato;
    private PigObject pigObject;
    private PigVers pigVer;

    public PigSessioneRecup() {
        // for Hibernate
    }

    @Id
    @GenericGenerator(name = "PIG_SESSIONE_RECUP_IDSESSIONERECUP_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
            @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SPIG_SESSIONE_RECUP"),
            @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PIG_SESSIONE_RECUP_IDSESSIONERECUP_GENERATOR")
    @Column(name = "ID_SESSIONE_RECUP")
    public Long getIdSessioneRecup() {
        return this.idSessioneRecup;
    }

    public void setIdSessioneRecup(Long idSessioneRecup) {
        this.idSessioneRecup = idSessioneRecup;
    }

    @Column(name = "CD_ERR")
    public String getCdErr() {
        return this.cdErr;
    }

    public void setCdErr(String cdErr) {
        this.cdErr = cdErr;
    }

    @Column(name = "CD_KEY_OBJECT")
    public String getCdKeyObject() {
        return this.cdKeyObject;
    }

    public void setCdKeyObject(String cdKeyObject) {
        this.cdKeyObject = cdKeyObject;
    }

    @Column(name = "DL_ERR")
    public String getDlErr() {
        return this.dlErr;
    }

    public void setDlErr(String dlErr) {
        this.dlErr = dlErr;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_APERTURA")
    public Date getDtApertura() {
        return this.dtApertura;
    }

    public void setDtApertura(Date dtApertura) {
        this.dtApertura = dtApertura;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_CHIUSURA")
    public Date getDtChiusura() {
        return this.dtChiusura;
    }

    public void setDtChiusura(Date dtChiusura) {
        this.dtChiusura = dtChiusura;
    }

    @Column(name = "NM_AMBIENTE_VERS")
    public String getNmAmbienteVers() {
        return this.nmAmbienteVers;
    }

    public void setNmAmbienteVers(String nmAmbienteVers) {
        this.nmAmbienteVers = nmAmbienteVers;
    }

    @Column(name = "NM_VERS")
    public String getNmVers() {
        return this.nmVers;
    }

    public void setNmVers(String nmVers) {
        this.nmVers = nmVers;
    }

    @Column(name = "TI_STATO")
    public String getTiStato() {
        return this.tiStato;
    }

    public void setTiStato(String tiStato) {
        this.tiStato = tiStato;
    }

    // bi-directional many-to-one association to PigObject
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_OBJECT")
    public PigObject getPigObject() {
        return this.pigObject;
    }

    public void setPigObject(PigObject pigObject) {
        this.pigObject = pigObject;
    }

    // bi-directional many-to-one association to PigVers
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_VERS")
    public PigVers getPigVer() {
        return this.pigVer;
    }

    public void setPigVer(PigVers pigVer) {
        this.pigVer = pigVer;
    }

}
