/*
 * Engineering Ingegneria Informatica S.p.A.
 *
 * Copyright (C) 2023 Regione Emilia-Romagna <p/> This program is free software: you can
 * redistribute it and/or modify it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version. <p/> This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Affero General Public License for more details. <p/> You should
 * have received a copy of the GNU Affero General Public License along with this program. If not,
 * see <https://www.gnu.org/licenses/>.
 */

package it.eng.sacerasi.grantEntity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * The persistent class for the ORG_APPART_COLLEG_ENTI database table.
 *
 */
@Entity
@Table(schema = "SACER_IAM", name = "ORG_APPART_COLLEG_ENTI")
public class OrgAppartCollegEnti implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long idAppartCollegEnti;
    private Date dtIniVal;
    private Date dtFinVal;
    private SIOrgEnteSiam orgEnteSiam;
    private OrgCollegEntiConvenz orgCollegEntiConvenz;

    public OrgAppartCollegEnti() {
	// for Hibernate
    }

    @Id
    @Column(name = "ID_APPART_COLLEG_ENTI")
    public Long getIdAppartCollegEnti() {
	return this.idAppartCollegEnti;
    }

    public void setIdAppartCollegEnti(Long idAppartCollegEnti) {
	this.idAppartCollegEnti = idAppartCollegEnti;
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
    @JoinColumn(name = "ID_ENTE_CONVENZ")
    public SIOrgEnteSiam getOrgEnteSiam() {
	return this.orgEnteSiam;
    }

    public void setOrgEnteSiam(SIOrgEnteSiam orgEnteSiam) {
	this.orgEnteSiam = orgEnteSiam;
    }

    // bi-directional many-to-one association to OrgCollegEntiConvenz
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_COLLEG_ENTI_CONVENZ")
    public OrgCollegEntiConvenz getOrgCollegEntiConvenz() {
	return this.orgCollegEntiConvenz;
    }

    public void setOrgCollegEntiConvenz(OrgCollegEntiConvenz orgCollegEntiConvenz) {
	this.orgCollegEntiConvenz = orgCollegEntiConvenz;
    }

}
