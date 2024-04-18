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

package it.eng.sacerasi.grantEntity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import it.eng.sacerasi.entity.SIAplApplic;

/**
 * Auto-generated by: org.apache.openjpa.jdbc.meta.ReverseMappingTool$AnnotatedCodeGenerator edited by fioravanti_f
 */
@Entity
@Table(schema = "SACER_IAM", name = "USR_ORGANIZ_IAM")
public class SIUsrOrganizIam implements Serializable {
    private static final long serialVersionUID = 1L;
    private SIAplApplic sIAplApplic;
    private SIAplTipoOrganiz siAplTipoOrganiz;
    private String dsOrganiz;
    private Long idOrganizApplic;
    private Long idOrganizIam;
    private String nmOrganiz;
    private List<SIOrgEnteConvenzOrg> orgEnteConvenzOrgs = new ArrayList<>();

    private SIUsrOrganizIam siUsrOrganizIam;
    private List<SIUsrOrganizIam> usrOrganizIams = new ArrayList<>();

    public SIUsrOrganizIam() {
    }

    public SIUsrOrganizIam(long idOrganizIam) {
        this.idOrganizIam = idOrganizIam;
    }

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "ID_APPLIC", nullable = false)
    public SIAplApplic getsIAplApplic() {
        return sIAplApplic;
    }

    public void setsIAplApplic(SIAplApplic sIAplApplic) {
        this.sIAplApplic = sIAplApplic;
    }

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "ID_TIPO_ORGANIZ", nullable = false)
    public SIAplTipoOrganiz getSiAplTipoOrganiz() {
        return siAplTipoOrganiz;
    }

    public void setSiAplTipoOrganiz(SIAplTipoOrganiz siAplTipoOrganiz) {
        this.siAplTipoOrganiz = siAplTipoOrganiz;
    }

    @Basic
    @Column(name = "DS_ORGANIZ", nullable = false, length = 254)
    public String getDsOrganiz() {
        return dsOrganiz;
    }

    public void setDsOrganiz(String dsOrganiz) {
        this.dsOrganiz = dsOrganiz;
    }

    @Basic
    @Column(name = "ID_ORGANIZ_APPLIC")
    public Long getIdOrganizApplic() {
        return idOrganizApplic;
    }

    public void setIdOrganizApplic(Long idOrganizApplic) {
        this.idOrganizApplic = idOrganizApplic;
    }

    @Id
    @Column(name = "ID_ORGANIZ_IAM")
    public Long getIdOrganizIam() {
        return idOrganizIam;
    }

    public void setIdOrganizIam(Long idOrganizIam) {
        this.idOrganizIam = idOrganizIam;
    }

    @Basic
    @Column(name = "NM_ORGANIZ", nullable = false, length = 100)
    public String getNmOrganiz() {
        return nmOrganiz;
    }

    public void setNmOrganiz(String nmOrganiz) {
        this.nmOrganiz = nmOrganiz;
    }

    @OneToMany(mappedBy = "siUsrOrganizIam", cascade = CascadeType.MERGE)
    public List<SIOrgEnteConvenzOrg> getOrgEnteConvenzOrgs() {
        return orgEnteConvenzOrgs;
    }

    public void setOrgEnteConvenzOrgs(List<SIOrgEnteConvenzOrg> orgEnteConvenzOrgs) {
        this.orgEnteConvenzOrgs = orgEnteConvenzOrgs;
    }

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "ID_ORGANIZ_IAM_PADRE")
    public SIUsrOrganizIam getSiUsrOrganizIam() {
        return siUsrOrganizIam;
    }

    public void setSiUsrOrganizIam(SIUsrOrganizIam siUsrOrganizIam) {
        this.siUsrOrganizIam = siUsrOrganizIam;
    }

    @OneToMany(mappedBy = "siUsrOrganizIam", cascade = CascadeType.MERGE)
    public List<SIUsrOrganizIam> getUsrOrganizIams() {
        return usrOrganizIams;
    }

    public void setUsrOrganizIams(List<SIUsrOrganizIam> usrOrganizIams) {
        this.usrOrganizIams = usrOrganizIams;
    }

}