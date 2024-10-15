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
 * The persistent class for the USR_V_ABIL_STRUT_SACER_XPING database table.
 */
@Entity
@Table(schema = "SACER_IAM", name = "USR_V_ABIL_STRUT_SACER_XPING")
public class UsrVAbilStrutSacerXping implements Serializable {

    private static final long serialVersionUID = 1L;

    private String dlCompositoOrganiz;

    private BigDecimal idAmbiente;

    private BigDecimal idEnte;

    private BigDecimal idOrganizIamAmbiente;

    private BigDecimal idOrganizIamEnte;

    private BigDecimal idOrganizIamStrut;

    private String nmAmbiente;

    private String nmEnte;

    private String nmStrut;

    private String nmUserid;

    public UsrVAbilStrutSacerXping(BigDecimal idUserIam, String type, BigDecimal id, String nmCampo) {
        this.usrVAbilStrutSacerXpingId = new UsrVAbilStrutSacerXpingId();
        this.usrVAbilStrutSacerXpingId.setIdUserIam(idUserIam);
        switch (type) {
        case "AMBIENTE":
            this.idAmbiente = id;
            this.nmAmbiente = nmCampo;
            break;
        case "ENTE":
            this.idEnte = id;
            this.nmEnte = nmCampo;
            break;
        case "STRUTTURA":
            this.usrVAbilStrutSacerXpingId.setIdStrut(id);
            this.nmStrut = nmCampo;
            break;
        }
    }

    public UsrVAbilStrutSacerXping(BigDecimal idOrganizIam, String type, String dlCompositoOrganiz) {
        this.dlCompositoOrganiz = dlCompositoOrganiz;
        switch (type) {
        case "AMBIENTE":
            this.idOrganizIamAmbiente = idOrganizIam;
            break;
        case "ENTE":
            this.idOrganizIamEnte = idOrganizIam;
            break;
        case "STRUTTURA":
            this.idOrganizIamStrut = idOrganizIam;
            break;
        }
    }

    public UsrVAbilStrutSacerXping(BigDecimal idAmbiente, String nmAmbiente, BigDecimal idEnte, String nmEnte,
            BigDecimal idStrut, String nmStrut) {
        this.idAmbiente = idAmbiente;
        this.idEnte = idEnte;
        this.usrVAbilStrutSacerXpingId = new UsrVAbilStrutSacerXpingId();
        this.usrVAbilStrutSacerXpingId.setIdStrut(idStrut);
        this.nmAmbiente = nmAmbiente;
        this.nmEnte = nmEnte;
        this.nmStrut = nmStrut;
    }

    public UsrVAbilStrutSacerXping() {
    }

    @Column(name = "DL_COMPOSITO_ORGANIZ")
    public String getDlCompositoOrganiz() {
        return this.dlCompositoOrganiz;
    }

    public void setDlCompositoOrganiz(String dlCompositoOrganiz) {
        this.dlCompositoOrganiz = dlCompositoOrganiz;
    }

    @Column(name = "ID_AMBIENTE")
    public BigDecimal getIdAmbiente() {
        return this.idAmbiente;
    }

    public void setIdAmbiente(BigDecimal idAmbiente) {
        this.idAmbiente = idAmbiente;
    }

    @Column(name = "ID_ENTE")
    public BigDecimal getIdEnte() {
        return this.idEnte;
    }

    public void setIdEnte(BigDecimal idEnte) {
        this.idEnte = idEnte;
    }

    @Column(name = "ID_ORGANIZ_IAM_AMBIENTE")
    public BigDecimal getIdOrganizIamAmbiente() {
        return this.idOrganizIamAmbiente;
    }

    public void setIdOrganizIamAmbiente(BigDecimal idOrganizIamAmbiente) {
        this.idOrganizIamAmbiente = idOrganizIamAmbiente;
    }

    @Column(name = "ID_ORGANIZ_IAM_ENTE")
    public BigDecimal getIdOrganizIamEnte() {
        return this.idOrganizIamEnte;
    }

    public void setIdOrganizIamEnte(BigDecimal idOrganizIamEnte) {
        this.idOrganizIamEnte = idOrganizIamEnte;
    }

    @Column(name = "ID_ORGANIZ_IAM_STRUT")
    public BigDecimal getIdOrganizIamStrut() {
        return this.idOrganizIamStrut;
    }

    public void setIdOrganizIamStrut(BigDecimal idOrganizIamStrut) {
        this.idOrganizIamStrut = idOrganizIamStrut;
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

    @Column(name = "NM_USERID")
    public String getNmUserid() {
        return this.nmUserid;
    }

    public void setNmUserid(String nmUserid) {
        this.nmUserid = nmUserid;
    }

    private UsrVAbilStrutSacerXpingId usrVAbilStrutSacerXpingId;

    @EmbeddedId()
    public UsrVAbilStrutSacerXpingId getUsrVAbilStrutSacerXpingId() {
        return usrVAbilStrutSacerXpingId;
    }

    public void setUsrVAbilStrutSacerXpingId(UsrVAbilStrutSacerXpingId usrVAbilStrutSacerXpingId) {
        this.usrVAbilStrutSacerXpingId = usrVAbilStrutSacerXpingId;
    }
}
