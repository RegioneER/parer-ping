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
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import it.eng.sacerasi.entity.constraint.ConstOrgCollegEntiConvenz.TiColleg;

/**
 * The persistent class for the ORG_COLLEG_ENTI_CONVENZ database table.
 *
 */
@Entity
@Table(schema = "SACER_IAM", name = "ORG_COLLEG_ENTI_CONVENZ")
@NamedQuery(name = "OrgCollegEntiConvenz.findAll", query = "SELECT o FROM OrgCollegEntiConvenz o")
public class OrgCollegEntiConvenz implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long idCollegEntiConvenz;
    private String dsColleg;
    private String nmColleg;
    private TiColleg tiColleg;
    private Date dtIniVal;
    private Date dtFinVal;
    private SIOrgEnteSiam orgEnteSiam;
    private List<OrgAppartCollegEnti> orgAppartCollegEntis = new ArrayList<>();

    public OrgCollegEntiConvenz() {
        // for Hibernate
    }

    @Id
    @Column(name = "ID_COLLEG_ENTI_CONVENZ")
    public Long getIdCollegEntiConvenz() {
        return this.idCollegEntiConvenz;
    }

    public void setIdCollegEntiConvenz(Long idCollegEntiConvenz) {
        this.idCollegEntiConvenz = idCollegEntiConvenz;
    }

    @Column(name = "DS_COLLEG")
    public String getDsColleg() {
        return this.dsColleg;
    }

    public void setDsColleg(String dsColleg) {
        this.dsColleg = dsColleg;
    }

    @Column(name = "NM_COLLEG")
    public String getNmColleg() {
        return this.nmColleg;
    }

    public void setNmColleg(String nmColleg) {
        this.nmColleg = nmColleg;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "TI_COLLEG")
    public TiColleg getTiColleg() {
        return this.tiColleg;
    }

    public void setTiColleg(TiColleg tiColleg) {
        this.tiColleg = tiColleg;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_INI_VAL")
    public Date getDtIniVal() {
        return this.dtIniVal;
    }

    public void setDtIniVal(Date dtIniVal) {
        this.dtIniVal = dtIniVal;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_FIN_VAL")
    public Date getDtFinVal() {
        return this.dtFinVal;
    }

    public void setDtFinVal(Date dtFinVal) {
        this.dtFinVal = dtFinVal;
    }

    // bi-directional many-to-one association to OrgEnteSiam
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_ENTE_CONVENZ_CAPOFILA")
    public SIOrgEnteSiam getSIOrgEnteSiam() {
        return this.orgEnteSiam;
    }

    public void setSIOrgEnteSiam(SIOrgEnteSiam orgEnteSiam) {
        this.orgEnteSiam = orgEnteSiam;
    }

    // bi-directional many-to-one association to OrgAppartCollegEnti
    @OneToMany(mappedBy = "orgCollegEntiConvenz")
    public List<OrgAppartCollegEnti> getOrgAppartCollegEntis() {
        return this.orgAppartCollegEntis;
    }

    public void setOrgAppartCollegEntis(List<OrgAppartCollegEnti> orgAppartCollegEntis) {
        this.orgAppartCollegEntis = orgAppartCollegEntis;
    }

    public OrgAppartCollegEnti addOrgAppartCollegEnti(OrgAppartCollegEnti orgAppartCollegEnti) {
        getOrgAppartCollegEntis().add(orgAppartCollegEnti);
        orgAppartCollegEnti.setOrgCollegEntiConvenz(this);

        return orgAppartCollegEnti;
    }

    public OrgAppartCollegEnti removeOrgAppartCollegEnti(OrgAppartCollegEnti orgAppartCollegEnti) {
        getOrgAppartCollegEntis().remove(orgAppartCollegEnti);
        orgAppartCollegEnti.setOrgCollegEntiConvenz(null);

        return orgAppartCollegEnti;
    }

}
