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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * The persistent class for the USR_V_TREE_ORGANIZ_IAM database table.
 *
 */
@Entity
@Table(schema = "SACER_IAM", name = "USR_V_TREE_ORGANIZ_IAM")
public class UsrVTreeOrganizIam implements Serializable {

    private static final long serialVersionUID = 1L;
    private String dlCompositoOrganiz;
    private String dlPathIdOrganizIam;
    private String dsOrganiz;
    private String flLastLivello;
    private BigDecimal idApplic;
    private BigDecimal idOrganizApplic;
    private BigDecimal idOrganizIam;
    private BigDecimal idOrganizIamPadre;
    private BigDecimal idTipoOrganiz;
    private String nmApplic;
    private String nmOrganiz;
    private String nmTipoOrganiz;

    public UsrVTreeOrganizIam() {
    }

    @Column(name = "DL_COMPOSITO_ORGANIZ")
    public String getDlCompositoOrganiz() {
	return this.dlCompositoOrganiz;
    }

    public void setDlCompositoOrganiz(String dlCompositoOrganiz) {
	this.dlCompositoOrganiz = dlCompositoOrganiz;
    }

    @Column(name = "DL_PATH_ID_ORGANIZ_IAM")
    public String getDlPathIdOrganizIam() {
	return this.dlPathIdOrganizIam;
    }

    public void setDlPathIdOrganizIam(String dlPathIdOrganizIam) {
	this.dlPathIdOrganizIam = dlPathIdOrganizIam;
    }

    @Column(name = "DS_ORGANIZ")
    public String getDsOrganiz() {
	return this.dsOrganiz;
    }

    public void setDsOrganiz(String dsOrganiz) {
	this.dsOrganiz = dsOrganiz;
    }

    @Column(name = "FL_LAST_LIVELLO", columnDefinition = "char")
    public String getFlLastLivello() {
	return this.flLastLivello;
    }

    public void setFlLastLivello(String flLastLivello) {
	this.flLastLivello = flLastLivello;
    }

    @Column(name = "ID_APPLIC")
    public BigDecimal getIdApplic() {
	return this.idApplic;
    }

    public void setIdApplic(BigDecimal idApplic) {
	this.idApplic = idApplic;
    }

    @Column(name = "ID_ORGANIZ_APPLIC")
    public BigDecimal getIdOrganizApplic() {
	return this.idOrganizApplic;
    }

    public void setIdOrganizApplic(BigDecimal idOrganizApplic) {
	this.idOrganizApplic = idOrganizApplic;
    }

    @Id
    @Column(name = "ID_ORGANIZ_IAM")
    public BigDecimal getIdOrganizIam() {
	return this.idOrganizIam;
    }

    public void setIdOrganizIam(BigDecimal idOrganizIam) {
	this.idOrganizIam = idOrganizIam;
    }

    @Column(name = "ID_ORGANIZ_IAM_PADRE")
    public BigDecimal getIdOrganizIamPadre() {
	return this.idOrganizIamPadre;
    }

    public void setIdOrganizIamPadre(BigDecimal idOrganizIamPadre) {
	this.idOrganizIamPadre = idOrganizIamPadre;
    }

    @Column(name = "ID_TIPO_ORGANIZ")
    public BigDecimal getIdTipoOrganiz() {
	return this.idTipoOrganiz;
    }

    public void setIdTipoOrganiz(BigDecimal idTipoOrganiz) {
	this.idTipoOrganiz = idTipoOrganiz;
    }

    @Column(name = "NM_APPLIC")
    public String getNmApplic() {
	return this.nmApplic;
    }

    public void setNmApplic(String nmApplic) {
	this.nmApplic = nmApplic;
    }

    @Column(name = "NM_ORGANIZ")
    public String getNmOrganiz() {
	return this.nmOrganiz;
    }

    public void setNmOrganiz(String nmOrganiz) {
	this.nmOrganiz = nmOrganiz;
    }

    @Column(name = "NM_TIPO_ORGANIZ")
    public String getNmTipoOrganiz() {
	return this.nmTipoOrganiz;
    }

    public void setNmTipoOrganiz(String nmTipoOrganiz) {
	this.nmTipoOrganiz = nmTipoOrganiz;
    }

}
