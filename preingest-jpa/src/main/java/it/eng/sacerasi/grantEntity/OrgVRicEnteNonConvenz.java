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
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * The persistent class for the ORG_V_RIC_ENTE_NON_CONVENZ database table.
 *
 */
@Entity
@Table(schema = "SACER_IAM", name = "ORG_V_RIC_ENTE_NON_CONVENZ")
public class OrgVRicEnteNonConvenz implements Serializable {
    private static final long serialVersionUID = 1L;
    private String archivista;
    private Date dtCessazione;
    private Date dtIniVal;
    private BigDecimal idEnteSiam;
    private BigDecimal idUserIamArk;
    private BigDecimal idUserIamCor;
    private String nmEnteSiam;
    private String nmUseridArk;
    private String tiEnteNonConvenz;

    public OrgVRicEnteNonConvenz() {
    }

    public String getArchivista() {
	return this.archivista;
    }

    public void setArchivista(String archivista) {
	this.archivista = archivista;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_CESSAZIONE")
    public Date getDtCessazione() {
	return this.dtCessazione;
    }

    public void setDtCessazione(Date dtCessazione) {
	this.dtCessazione = dtCessazione;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_INI_VAL")
    public Date getDtIniVal() {
	return this.dtIniVal;
    }

    public void setDtIniVal(Date dtIniVal) {
	this.dtIniVal = dtIniVal;
    }

    @Id
    @Column(name = "ID_ENTE_SIAM")
    public BigDecimal getIdEnteSiam() {
	return this.idEnteSiam;
    }

    public void setIdEnteSiam(BigDecimal idEnteSiam) {
	this.idEnteSiam = idEnteSiam;
    }

    @Column(name = "ID_USER_IAM_ARK")
    public BigDecimal getIdUserIamArk() {
	return this.idUserIamArk;
    }

    public void setIdUserIamArk(BigDecimal idUserIamArk) {
	this.idUserIamArk = idUserIamArk;
    }

    @Column(name = "ID_USER_IAM_COR")
    public BigDecimal getIdUserIamCor() {
	return this.idUserIamCor;
    }

    public void setIdUserIamCor(BigDecimal idUserIamCor) {
	this.idUserIamCor = idUserIamCor;
    }

    @Column(name = "NM_ENTE_SIAM")
    public String getNmEnteSiam() {
	return this.nmEnteSiam;
    }

    public void setNmEnteSiam(String nmEnteSiam) {
	this.nmEnteSiam = nmEnteSiam;
    }

    @Column(name = "NM_USERID_ARK")
    public String getNmUseridArk() {
	return this.nmUseridArk;
    }

    public void setNmUseridArk(String nmUseridArk) {
	this.nmUseridArk = nmUseridArk;
    }

    @Column(name = "TI_ENTE_NON_CONVENZ")
    public String getTiEnteNonConvenz() {
	return this.tiEnteNonConvenz;
    }

    public void setTiEnteNonConvenz(String tiEnteNonConvenz) {
	this.tiEnteNonConvenz = tiEnteNonConvenz;
    }

}
