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
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * The persistent class for the IAM_V_LIS_ORGANIZ_DA_REPLIC database table.
 *
 */
@Entity
@Table(name = "IAM_V_LIS_ORGANIZ_DA_REPLIC")
public class IamVLisOrganizDaReplic implements Serializable {

    private static final long serialVersionUID = 1L;
    private String cdErr;
    private String dsMsgErr;
    private String dsOrdOrganiz;
    private Date dtErr;
    private Date dtLogOrganizDaReplic;
    private BigDecimal idAmbiente;
    private BigDecimal idOrganizApplic;
    private BigDecimal idOrganizDaReplic;
    private BigDecimal idVers;
    private String nmAmbiente;
    private String nmOrganiz;
    private String nmVers;
    private String tiOperReplic;
    private String tiStatoReplic;

    public IamVLisOrganizDaReplic() {
    }

    @Column(name = "CD_ERR")
    public String getCdErr() {
        return this.cdErr;
    }

    public void setCdErr(String cdErr) {
        this.cdErr = cdErr;
    }

    @Column(name = "DS_MSG_ERR")
    public String getDsMsgErr() {
        return this.dsMsgErr;
    }

    public void setDsMsgErr(String dsMsgErr) {
        this.dsMsgErr = dsMsgErr;
    }

    @Column(name = "DS_ORD_ORGANIZ")
    public String getDsOrdOrganiz() {
        return this.dsOrdOrganiz;
    }

    public void setDsOrdOrganiz(String dsOrdOrganiz) {
        this.dsOrdOrganiz = dsOrdOrganiz;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_ERR")
    public Date getDtErr() {
        return this.dtErr;
    }

    public void setDtErr(Date dtErr) {
        this.dtErr = dtErr;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_LOG_ORGANIZ_DA_REPLIC")
    public Date getDtLogOrganizDaReplic() {
        return this.dtLogOrganizDaReplic;
    }

    public void setDtLogOrganizDaReplic(Date dtLogOrganizDaReplic) {
        this.dtLogOrganizDaReplic = dtLogOrganizDaReplic;
    }

    @Column(name = "ID_AMBIENTE")
    public BigDecimal getIdAmbiente() {
        return this.idAmbiente;
    }

    public void setIdAmbiente(BigDecimal idAmbiente) {
        this.idAmbiente = idAmbiente;
    }

    @Column(name = "ID_ORGANIZ_APPLIC")
    public BigDecimal getIdOrganizApplic() {
        return this.idOrganizApplic;
    }

    public void setIdOrganizApplic(BigDecimal idOrganizApplic) {
        this.idOrganizApplic = idOrganizApplic;
    }

    @Id
    @Column(name = "ID_ORGANIZ_DA_REPLIC")
    public BigDecimal getIdOrganizDaReplic() {
        return this.idOrganizDaReplic;
    }

    public void setIdOrganizDaReplic(BigDecimal idOrganizDaReplic) {
        this.idOrganizDaReplic = idOrganizDaReplic;
    }

    @Column(name = "ID_VERS")
    public BigDecimal getIdVers() {
        return this.idVers;
    }

    public void setIdVers(BigDecimal idVers) {
        this.idVers = idVers;
    }

    @Column(name = "NM_AMBIENTE")
    public String getNmAmbiente() {
        return this.nmAmbiente;
    }

    public void setNmAmbiente(String nmAmbiente) {
        this.nmAmbiente = nmAmbiente;
    }

    @Column(name = "NM_ORGANIZ")
    public String getNmOrganiz() {
        return this.nmOrganiz;
    }

    public void setNmOrganiz(String nmOrganiz) {
        this.nmOrganiz = nmOrganiz;
    }

    @Column(name = "NM_VERS")
    public String getNmVers() {
        return this.nmVers;
    }

    public void setNmVers(String nmVers) {
        this.nmVers = nmVers;
    }

    @Column(name = "TI_OPER_REPLIC")
    public String getTiOperReplic() {
        return this.tiOperReplic;
    }

    public void setTiOperReplic(String tiOperReplic) {
        this.tiOperReplic = tiOperReplic;
    }

    @Column(name = "TI_STATO_REPLIC")
    public String getTiStatoReplic() {
        return this.tiStatoReplic;
    }

    public void setTiStatoReplic(String tiStatoReplic) {
        this.tiStatoReplic = tiStatoReplic;
    }

}
