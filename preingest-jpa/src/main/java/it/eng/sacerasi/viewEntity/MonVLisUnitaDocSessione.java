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

package it.eng.sacerasi.viewEntity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * The persistent class for the MON_V_LIS_UNITA_DOC_SESSIONE database table.
 *
 */
@Entity
@Table(name = "MON_V_LIS_UNITA_DOC_SESSIONE")
@NamedQuery(name = "MonVLisUnitaDocSessione.findByIdSessioneIngest", query = "SELECT m.cdErrSacer, min(m.dlErrSacer), min(m.cdConcatDlErrSacer) FROM MonVLisUnitaDocSessione m WHERE m.idSessioneIngest=:idSessioneIngest AND m.cdErrSacer IS NOT NULL GROUP BY m.cdErrSacer ORDER BY m.cdErrSacer")
public class MonVLisUnitaDocSessione implements Serializable {

    private static final long serialVersionUID = 1L;
    private BigDecimal aaUnitaDocSacer;
    private String cdConcatDlErrSacer;
    private String cdErrSacer;
    private String cdKeyUnitaDocSacer;
    private String cdRegistroUnitaDocSacer;
    private String dlCompositoOrganiz;
    private String dlErrSacer;
    private String flVersSimulato;
    private BigDecimal idOrganizIam;
    private BigDecimal idSessioneIngest;
    private BigDecimal idUnitaDocSessione;
    private BigDecimal niSizeFileByte;
    private String nmAmbiente;
    private String nmEnte;
    private String nmStrut;
    private String tiStatoUnitaDocSessione;

    public MonVLisUnitaDocSessione() {
    }

    @Column(name = "AA_UNITA_DOC_SACER")
    public BigDecimal getAaUnitaDocSacer() {
        return this.aaUnitaDocSacer;
    }

    public void setAaUnitaDocSacer(BigDecimal aaUnitaDocSacer) {
        this.aaUnitaDocSacer = aaUnitaDocSacer;
    }

    @Column(name = "CD_CONCAT_DL_ERR_SACER")
    public String getCdConcatDlErrSacer() {
        return this.cdConcatDlErrSacer;
    }

    public void setCdConcatDlErrSacer(String cdConcatDlErrSacer) {
        this.cdConcatDlErrSacer = cdConcatDlErrSacer;
    }

    @Column(name = "CD_ERR_SACER")
    public String getCdErrSacer() {
        return this.cdErrSacer;
    }

    public void setCdErrSacer(String cdErrSacer) {
        this.cdErrSacer = cdErrSacer;
    }

    @Column(name = "CD_KEY_UNITA_DOC_SACER")
    public String getCdKeyUnitaDocSacer() {
        return this.cdKeyUnitaDocSacer;
    }

    public void setCdKeyUnitaDocSacer(String cdKeyUnitaDocSacer) {
        this.cdKeyUnitaDocSacer = cdKeyUnitaDocSacer;
    }

    @Column(name = "CD_REGISTRO_UNITA_DOC_SACER")
    public String getCdRegistroUnitaDocSacer() {
        return this.cdRegistroUnitaDocSacer;
    }

    public void setCdRegistroUnitaDocSacer(String cdRegistroUnitaDocSacer) {
        this.cdRegistroUnitaDocSacer = cdRegistroUnitaDocSacer;
    }

    @Column(name = "DL_COMPOSITO_ORGANIZ")
    public String getDlCompositoOrganiz() {
        return this.dlCompositoOrganiz;
    }

    public void setDlCompositoOrganiz(String dlCompositoOrganiz) {
        this.dlCompositoOrganiz = dlCompositoOrganiz;
    }

    @Column(name = "DL_ERR_SACER")
    public String getDlErrSacer() {
        return this.dlErrSacer;
    }

    public void setDlErrSacer(String dlErrSacer) {
        this.dlErrSacer = dlErrSacer;
    }

    @Column(name = "FL_VERS_SIMULATO", columnDefinition = "char")
    public String getFlVersSimulato() {
        return this.flVersSimulato;
    }

    public void setFlVersSimulato(String flVersSimulato) {
        this.flVersSimulato = flVersSimulato;
    }

    @Column(name = "ID_ORGANIZ_IAM")
    public BigDecimal getIdOrganizIam() {
        return this.idOrganizIam;
    }

    public void setIdOrganizIam(BigDecimal idOrganizIam) {
        this.idOrganizIam = idOrganizIam;
    }

    @Column(name = "ID_SESSIONE_INGEST")
    public BigDecimal getIdSessioneIngest() {
        return this.idSessioneIngest;
    }

    public void setIdSessioneIngest(BigDecimal idSessioneIngest) {
        this.idSessioneIngest = idSessioneIngest;
    }

    @Id
    @Column(name = "ID_UNITA_DOC_SESSIONE")
    public BigDecimal getIdUnitaDocSessione() {
        return this.idUnitaDocSessione;
    }

    public void setIdUnitaDocSessione(BigDecimal idUnitaDocSessione) {
        this.idUnitaDocSessione = idUnitaDocSessione;
    }

    @Column(name = "NI_SIZE_FILE_BYTE")
    public BigDecimal getNiSizeFileByte() {
        return this.niSizeFileByte;
    }

    public void setNiSizeFileByte(BigDecimal niSizeFileByte) {
        this.niSizeFileByte = niSizeFileByte;
    }

    @Column(name = "NM_AMBIENTE")
    public String getNmAmbiente() {
        return this.nmAmbiente;
    }

    public void setNmAmbiente(String nmAmbiente) {
        this.nmAmbiente = nmAmbiente;
    }

    @Column(name = "NM_ENTE")
    public String getNmEnte() {
        return this.nmEnte;
    }

    public void setNmEnte(String nmEnte) {
        this.nmEnte = nmEnte;
    }

    @Column(name = "NM_STRUT")
    public String getNmStrut() {
        return this.nmStrut;
    }

    public void setNmStrut(String nmStrut) {
        this.nmStrut = nmStrut;
    }

    @Column(name = "TI_STATO_UNITA_DOC_SESSIONE")
    public String getTiStatoUnitaDocSessione() {
        return this.tiStatoUnitaDocSessione;
    }

    public void setTiStatoUnitaDocSessione(String tiStatoUnitaDocSessione) {
        this.tiStatoUnitaDocSessione = tiStatoUnitaDocSessione;
    }

}
