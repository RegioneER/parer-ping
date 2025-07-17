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
 * The persistent class for the ORG_V_TREE_AMBITO_TERRIT database table.
 *
 */
@Entity
@Table(schema = "SACER_IAM", name = "ORG_V_TREE_AMBITO_TERRIT")
public class OrgVTreeAmbitoTerrit implements Serializable {

    private static final long serialVersionUID = 1L;
    private String cdAmbitoTerrit;
    private String dsTreeCdAmbitoTerrit;
    private String dsTreeIdAmbitoTerrit;
    private BigDecimal idAmbitoTerrit;
    private BigDecimal idAmbitoTerritPadre;
    private String tiAmbitoTerrit;

    public OrgVTreeAmbitoTerrit() {
    }

    @Column(name = "CD_AMBITO_TERRIT")
    public String getCdAmbitoTerrit() {
	return this.cdAmbitoTerrit;
    }

    public void setCdAmbitoTerrit(String cdAmbitoTerrit) {
	this.cdAmbitoTerrit = cdAmbitoTerrit;
    }

    @Column(name = "DS_TREE_CD_AMBITO_TERRIT")
    public String getDsTreeCdAmbitoTerrit() {
	return this.dsTreeCdAmbitoTerrit;
    }

    public void setDsTreeCdAmbitoTerrit(String dsTreeCdAmbitoTerrit) {
	this.dsTreeCdAmbitoTerrit = dsTreeCdAmbitoTerrit;
    }

    @Column(name = "DS_TREE_ID_AMBITO_TERRIT")
    public String getDsTreeIdAmbitoTerrit() {
	return this.dsTreeIdAmbitoTerrit;
    }

    public void setDsTreeIdAmbitoTerrit(String dsTreeIdAmbitoTerrit) {
	this.dsTreeIdAmbitoTerrit = dsTreeIdAmbitoTerrit;
    }

    @Id
    @Column(name = "ID_AMBITO_TERRIT")
    public BigDecimal getIdAmbitoTerrit() {
	return this.idAmbitoTerrit;
    }

    public void setIdAmbitoTerrit(BigDecimal idAmbitoTerrit) {
	this.idAmbitoTerrit = idAmbitoTerrit;
    }

    @Column(name = "ID_AMBITO_TERRIT_PADRE")
    public BigDecimal getIdAmbitoTerritPadre() {
	return this.idAmbitoTerritPadre;
    }

    public void setIdAmbitoTerritPadre(BigDecimal idAmbitoTerritPadre) {
	this.idAmbitoTerritPadre = idAmbitoTerritPadre;
    }

    @Column(name = "TI_AMBITO_TERRIT")
    public String getTiAmbitoTerrit() {
	return this.tiAmbitoTerrit;
    }

    public void setTiAmbitoTerrit(String tiAmbitoTerrit) {
	this.tiAmbitoTerrit = tiAmbitoTerrit;
    }

}
