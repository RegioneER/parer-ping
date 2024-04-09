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

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * The persistent class for the USR_V_ABIL_AMB_ENTE_CONVENZ database table.
 */
@Entity
@Table(schema = "SACER_IAM", name = "USR_V_ABIL_AMB_ENTE_CONVENZ")
@NamedQuery(name = "UsrVAbilAmbEnteConvenz.findAll", query = "SELECT u FROM UsrVAbilAmbEnteConvenz u")
public class UsrVAbilAmbEnteConvenz implements Serializable {

    private static final long serialVersionUID = 1L;

    private String dsAmbienteEnteConvenz;

    private String nmAmbienteEnteConvenz;

    private String nmUserid;

    public UsrVAbilAmbEnteConvenz() {
    }

    public UsrVAbilAmbEnteConvenz(BigDecimal idAmbienteEnteConvenz, String nmAmbienteEnteConvenz,
            String dsAmbienteEnteConvenz) {
        this.usrVAbilAmbEnteConvenzId = new UsrVAbilAmbEnteConvenzId();
        this.usrVAbilAmbEnteConvenzId.setIdAmbienteEnteConvenz(idAmbienteEnteConvenz);
        this.nmAmbienteEnteConvenz = nmAmbienteEnteConvenz;
        this.dsAmbienteEnteConvenz = dsAmbienteEnteConvenz;
    }

    @Column(name = "DS_AMBIENTE_ENTE_CONVENZ")
    public String getDsAmbienteEnteConvenz() {
        return this.dsAmbienteEnteConvenz;
    }

    public void setDsAmbienteEnteConvenz(String dsAmbienteEnteConvenz) {
        this.dsAmbienteEnteConvenz = dsAmbienteEnteConvenz;
    }

    @Column(name = "NM_AMBIENTE_ENTE_CONVENZ")
    public String getNmAmbienteEnteConvenz() {
        return this.nmAmbienteEnteConvenz;
    }

    public void setNmAmbienteEnteConvenz(String nmAmbienteEnteConvenz) {
        this.nmAmbienteEnteConvenz = nmAmbienteEnteConvenz;
    }

    @Column(name = "NM_USERID")
    public String getNmUserid() {
        return this.nmUserid;
    }

    public void setNmUserid(String nmUserid) {
        this.nmUserid = nmUserid;
    }

    private UsrVAbilAmbEnteConvenzId usrVAbilAmbEnteConvenzId;

    @EmbeddedId()
    public UsrVAbilAmbEnteConvenzId getUsrVAbilAmbEnteConvenzId() {
        return usrVAbilAmbEnteConvenzId;
    }

    public void setUsrVAbilAmbEnteConvenzId(UsrVAbilAmbEnteConvenzId usrVAbilAmbEnteConvenzId) {
        this.usrVAbilAmbEnteConvenzId = usrVAbilAmbEnteConvenzId;
    }
}
