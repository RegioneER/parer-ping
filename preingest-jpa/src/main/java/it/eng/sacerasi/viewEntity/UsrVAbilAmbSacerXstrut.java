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

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * The persistent class for the USR_V_ABIL_AMB_SACER_XSTRUT database table.
 */
@Entity
@Table(schema = "SACER_IAM", name = "USR_V_ABIL_AMB_SACER_XSTRUT")
@NamedQuery(name = "UsrVAbilAmbSacerXstrut.findAll", query = "SELECT u FROM UsrVAbilAmbSacerXstrut u")
public class UsrVAbilAmbSacerXstrut implements Serializable {

    private static final long serialVersionUID = 1L;

    private String dsOrganiz;

    private String nmApplic;

    private String nmOrganiz;

    public UsrVAbilAmbSacerXstrut() {
        //
    }

    @Column(name = "DS_ORGANIZ")
    public String getDsOrganiz() {
        return this.dsOrganiz;
    }

    public void setDsOrganiz(String dsOrganiz) {
        this.dsOrganiz = dsOrganiz;
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

    private UsrVAbilAmbSacerXstrutId usrVAbilAmbSacerXstrutId;

    @EmbeddedId()
    public UsrVAbilAmbSacerXstrutId getUsrVAbilAmbSacerXstrutId() {
        return usrVAbilAmbSacerXstrutId;
    }

    public void setUsrVAbilAmbSacerXstrutId(UsrVAbilAmbSacerXstrutId usrVAbilAmbSacerXstrutId) {
        this.usrVAbilAmbSacerXstrutId = usrVAbilAmbSacerXstrutId;
    }
}
