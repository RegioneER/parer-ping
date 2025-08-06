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
 * The persistent class for the USR_USER database table.
 *
 */
@Entity
@Table(name = "USR_USER", schema = "SACER_IAM")
public class UsrUser implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long idUserIam;
    private String cdFisc;
    private String cdPsw;
    private String cdSalt;
    private String dsEmail;
    private Date dtRegPsw;
    private Date dtScadPsw;
    private String flAttivo;
    private String flContrIp;
    private String flUserAdmin;
    private String nmCognomeUser;
    private String nmNomeUser;
    private String nmUserid;
    private String tipoUser;
    private SIOrgEnteSiam siOrgEnteSiam;

    public UsrUser() {
	// Hibernate
    }

    @Id
    @Column(name = "ID_USER_IAM")
    public Long getIdUserIam() {
	return this.idUserIam;
    }

    public void setIdUserIam(Long idUserIam) {
	this.idUserIam = idUserIam;
    }

    @Column(name = "CD_FISC")
    public String getCdFisc() {
	return this.cdFisc;
    }

    public void setCdFisc(String cdFisc) {
	this.cdFisc = cdFisc;
    }

    @Column(name = "CD_PSW")
    public String getCdPsw() {
	return this.cdPsw;
    }

    public void setCdPsw(String cdPsw) {
	this.cdPsw = cdPsw;
    }

    @Column(name = "CD_SALT")
    public String getCdSalt() {
	return this.cdSalt;
    }

    public void setCdSalt(String cdSalt) {
	this.cdSalt = cdSalt;
    }

    @Column(name = "DS_EMAIL")
    public String getDsEmail() {
	return this.dsEmail;
    }

    public void setDsEmail(String dsEmail) {
	this.dsEmail = dsEmail;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_REG_PSW")
    public Date getDtRegPsw() {
	return this.dtRegPsw;
    }

    public void setDtRegPsw(Date dtRegPsw) {
	this.dtRegPsw = dtRegPsw;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_SCAD_PSW")
    public Date getDtScadPsw() {
	return this.dtScadPsw;
    }

    public void setDtScadPsw(Date dtScadPsw) {
	this.dtScadPsw = dtScadPsw;
    }

    @Column(name = "FL_ATTIVO", columnDefinition = "char")
    public String getFlAttivo() {
	return this.flAttivo;
    }

    public void setFlAttivo(String flAttivo) {
	this.flAttivo = flAttivo;
    }

    @Column(name = "FL_CONTR_IP", columnDefinition = "char")
    public String getFlContrIp() {
	return this.flContrIp;
    }

    public void setFlContrIp(String flContrIp) {
	this.flContrIp = flContrIp;
    }

    @Column(name = "FL_USER_ADMIN", columnDefinition = "char")
    public String getFlUserAdmin() {
	return this.flUserAdmin;
    }

    public void setFlUserAdmin(String flUserAdmin) {
	this.flUserAdmin = flUserAdmin;
    }

    @Column(name = "NM_COGNOME_USER")
    public String getNmCognomeUser() {
	return this.nmCognomeUser;
    }

    public void setNmCognomeUser(String nmCognomeUser) {
	this.nmCognomeUser = nmCognomeUser;
    }

    @Column(name = "NM_NOME_USER")
    public String getNmNomeUser() {
	return this.nmNomeUser;
    }

    public void setNmNomeUser(String nmNomeUser) {
	this.nmNomeUser = nmNomeUser;
    }

    @Column(name = "NM_USERID")
    public String getNmUserid() {
	return this.nmUserid;
    }

    public void setNmUserid(String nmUserid) {
	this.nmUserid = nmUserid;
    }

    @Column(name = "TIPO_USER")
    public String getTipoUser() {
	return this.tipoUser;
    }

    public void setTipoUser(String tipoUser) {
	this.tipoUser = tipoUser;
    }

    // bi-directional many-to-one association to SIOrgEnteSiam
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_ENTE_SIAM")
    public SIOrgEnteSiam getSiOrgEnteSiam() {
	return this.siOrgEnteSiam;
    }

    public void setSiOrgEnteSiam(SIOrgEnteSiam siOrgEnteSiam) {
	this.siOrgEnteSiam = siOrgEnteSiam;
    }

}
