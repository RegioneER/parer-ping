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
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * The persistent class for the ORG_STRUT database table.
 *
 */
@Entity
@XmlRootElement
@Cacheable(true)
@Table(schema = "SACER", name = "ORG_STRUT")
public class OrgStrut implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long idStrut;
    private String cdIpa;
    private String dlNoteStrut;
    private String dsStrut;
    private String cdStrutNormaliz;
    private Date dtIniVal;
    private Date dtFineVal;
    private Date dtIniValStrut;
    private Date dtFineValStrut;
    private String flTemplate;
    private BigDecimal idEnteConvenz;
    private String nmStrut;
    private OrgEnte orgEnte;

    public OrgStrut() {
        // for Hibernate
    }

    @Id
    @Column(name = "ID_STRUT")
    public Long getIdStrut() {
        return this.idStrut;
    }

    public void setIdStrut(Long idStrut) {
        this.idStrut = idStrut;
    }

    @Column(name = "CD_IPA")
    public String getCdIpa() {
        return cdIpa;
    }

    public void setCdIpa(String cdIpa) {
        this.cdIpa = cdIpa;
    }

    @Column(name = "DL_NOTE_STRUT")
    public String getDlNoteStrut() {
        return this.dlNoteStrut;
    }

    public void setDlNoteStrut(String dlNoteStrut) {
        this.dlNoteStrut = dlNoteStrut;
    }

    @Column(name = "DS_STRUT")
    public String getDsStrut() {
        return this.dsStrut;
    }

    public void setDsStrut(String dsStrut) {
        this.dsStrut = dsStrut;
    }

    @Column(name = "CD_STRUT_NORMALIZ")
    public String getCdStrutNormaliz() {
        return this.cdStrutNormaliz;
    }

    public void setCdStrutNormaliz(String cdStrutNormaliz) {
        this.cdStrutNormaliz = cdStrutNormaliz;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_INI_VAL")
    public Date getDtIniVal() {
        return dtIniVal;
    }

    public void setDtIniVal(Date dtIniVal) {
        this.dtIniVal = dtIniVal;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_FINE_VAL")
    public Date getDtFineVal() {
        return dtFineVal;
    }

    public void setDtFineVal(Date dtFineVal) {
        this.dtFineVal = dtFineVal;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_INI_VAL_STRUT")
    public Date getDtIniValStrut() {
        return dtIniValStrut;
    }

    public void setDtIniValStrut(Date dtIniValStrut) {
        this.dtIniValStrut = dtIniValStrut;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_FINE_VAL_STRUT")
    public Date getDtFineValStrut() {
        return dtFineValStrut;
    }

    public void setDtFineValStrut(Date dtFineValStrut) {
        this.dtFineValStrut = dtFineValStrut;
    }

    @Column(name = "FL_TEMPLATE", columnDefinition = "char")
    public String getFlTemplate() {
        return this.flTemplate;
    }

    public void setFlTemplate(String flTemplate) {
        this.flTemplate = flTemplate;
    }

    @Column(name = "ID_ENTE_CONVENZ")
    public BigDecimal getIdEnteConvenz() {
        return idEnteConvenz;
    }

    public void setIdEnteConvenz(BigDecimal idEnteConvenz) {
        this.idEnteConvenz = idEnteConvenz;
    }

    @Column(name = "NM_STRUT")
    public String getNmStrut() {
        return this.nmStrut;
    }

    public void setNmStrut(String nmStrut) {
        this.nmStrut = nmStrut;
    }

    // bi-directional many-to-one association to OrgEnte
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_ENTE")
    @XmlTransient
    public OrgEnte getOrgEnte() {
        return this.orgEnte;
    }

    public void setOrgEnte(OrgEnte orgEnte) {
        this.orgEnte = orgEnte;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 43 * hash + (int) (this.idStrut ^ (this.idStrut >>> 32));
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final OrgStrut other = (OrgStrut) obj;
        if (this.idStrut != other.idStrut) {
            return false;
        }
        return true;
    }
}
