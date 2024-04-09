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
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * The persistent class for the PIG_V_RIC_VERS database table.
 */
@Entity
@Table(name = "PIG_V_RIC_VERS")
public class PigVRicVers implements Serializable {

    private static final long serialVersionUID = 1L;

    private String dlCompositoOrganizStrut;

    private String dsListaDichStrutSacer;

    private BigDecimal idAmbienteVers;

    private String nmAmbienteVers;

    private String nmOrganizIamAmbiente;

    private String nmOrganizIamEnte;

    private String nmOrganizIamStrut;

    private String nmUseridSacer;

    private String nmVers;
    private String nmTipoVersatore;

    // MEV 27543
    private String nmEnteConvenz;
    private String nmAmbienteEnteConvenz;

    public PigVRicVers() {
    }

    public PigVRicVers(BigDecimal idAmbienteVers, String nmAmbienteVers, BigDecimal idVers, String nmVers,
            String dsListaDichStrutSacer, String nmAmbienteEnteConvenz, String nmEnteConvenz, String nmTipoVersatore) {
        this.pigVRicVersId = new PigVRicVersId();
        this.idAmbienteVers = idAmbienteVers;
        this.nmAmbienteVers = nmAmbienteVers;
        this.pigVRicVersId.setIdVers(idVers);
        this.nmVers = nmVers;
        this.dsListaDichStrutSacer = dsListaDichStrutSacer;
        this.nmAmbienteEnteConvenz = nmAmbienteEnteConvenz;
        this.nmEnteConvenz = nmEnteConvenz;
        this.nmTipoVersatore = nmTipoVersatore;
    }

    @Column(name = "DL_COMPOSITO_ORGANIZ_STRUT")
    public String getDlCompositoOrganizStrut() {
        return this.dlCompositoOrganizStrut;
    }

    public void setDlCompositoOrganizStrut(String dlCompositoOrganizStrut) {
        this.dlCompositoOrganizStrut = dlCompositoOrganizStrut;
    }

    @Column(name = "DS_LISTA_DICH_STRUT_SACER")
    public String getDsListaDichStrutSacer() {
        return this.dsListaDichStrutSacer;
    }

    public void setDsListaDichStrutSacer(String dsListaDichStrutSacer) {
        this.dsListaDichStrutSacer = dsListaDichStrutSacer;
    }

    @Column(name = "ID_AMBIENTE_VERS")
    public BigDecimal getIdAmbienteVers() {
        return this.idAmbienteVers;
    }

    public void setIdAmbienteVers(BigDecimal idAmbienteVers) {
        this.idAmbienteVers = idAmbienteVers;
    }

    @Column(name = "NM_AMBIENTE_VERS")
    public String getNmAmbienteVers() {
        return this.nmAmbienteVers;
    }

    public void setNmAmbienteVers(String nmAmbienteVers) {
        this.nmAmbienteVers = nmAmbienteVers;
    }

    @Column(name = "NM_ORGANIZ_IAM_AMBIENTE")
    public String getNmOrganizIamAmbiente() {
        return this.nmOrganizIamAmbiente;
    }

    public void setNmOrganizIamAmbiente(String nmOrganizIamAmbiente) {
        this.nmOrganizIamAmbiente = nmOrganizIamAmbiente;
    }

    @Column(name = "NM_ORGANIZ_IAM_ENTE")
    public String getNmOrganizIamEnte() {
        return this.nmOrganizIamEnte;
    }

    public void setNmOrganizIamEnte(String nmOrganizIamEnte) {
        this.nmOrganizIamEnte = nmOrganizIamEnte;
    }

    @Column(name = "NM_ORGANIZ_IAM_STRUT")
    public String getNmOrganizIamStrut() {
        return this.nmOrganizIamStrut;
    }

    public void setNmOrganizIamStrut(String nmOrganizIamStrut) {
        this.nmOrganizIamStrut = nmOrganizIamStrut;
    }

    @Column(name = "NM_USERID_SACER")
    public String getNmUseridSacer() {
        return this.nmUseridSacer;
    }

    public void setNmUseridSacer(String nmUseridSacer) {
        this.nmUseridSacer = nmUseridSacer;
    }

    @Column(name = "NM_VERS")
    public String getNmVers() {
        return this.nmVers;
    }

    public void setNmVers(String nmVers) {
        this.nmVers = nmVers;
    }

    private PigVRicVersId pigVRicVersId;

    @EmbeddedId()
    public PigVRicVersId getPigVRicVersId() {
        return pigVRicVersId;
    }

    public void setPigVRicVersId(PigVRicVersId pigVRicVersId) {
        this.pigVRicVersId = pigVRicVersId;
    }

    @Column(name = "NM_ENTE_CONVENZ")
    public String getNmEnteConvenz() {
        return nmEnteConvenz;
    }

    public void setNmEnteConvenz(String nmEnteConvenz) {
        this.nmEnteConvenz = nmEnteConvenz;
    }

    @Column(name = "NM_AMBIENTE_ENTE_CONVENZ")
    public String getNmAmbienteEnteConvenz() {
        return nmAmbienteEnteConvenz;
    }

    public void setNmAmbienteEnteConvenz(String nmAmbienteEnteConvenz) {
        this.nmAmbienteEnteConvenz = nmAmbienteEnteConvenz;
    }

    @Column(name = "NM_TIPO_VERSATORE")
    public String getNmTipoVersatore() {
        return nmTipoVersatore;
    }

    public void setNmTipoVersatore(String nmTipoVersatore) {
        this.nmTipoVersatore = nmTipoVersatore;
    }

}
