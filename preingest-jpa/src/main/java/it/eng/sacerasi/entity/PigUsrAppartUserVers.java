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

package it.eng.sacerasi.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.id.enhanced.SequenceStyleGenerator;

/**
 * The persistent class for the PIG_USR_APPART_USER_VERS database table.
 *
 */
// @Entity
// @Table(name = "PIG_USR_APPART_USER_VERS")

/**
 * @deprecated non più mappata, per ora non utilizzata
 *
 */
@Deprecated
public class PigUsrAppartUserVers implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long idAppartUserVers;
    private PigUsrUser pigUsrUser;
    private PigVers pigVer;

    public PigUsrAppartUserVers() {
        // for Hibernate
    }

    @Id
    @GenericGenerator(name = "PIG_USR_APPART_USER_VERS_IDAPPARTUSERVERS_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
            @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SPIG_USR_APPART_USER_VERS"),
            @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PIG_USR_APPART_USER_VERS_IDAPPARTUSERVERS_GENERATOR")
    @Column(name = "ID_APPART_USER_VERS")
    public Long getIdAppartUserVers() {
        return this.idAppartUserVers;
    }

    public void setIdAppartUserVers(Long idAppartUserVers) {
        this.idAppartUserVers = idAppartUserVers;
    }

    // bi-directional many-to-one association to PigUsrUser
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_USER")
    public PigUsrUser getPigUsrUser() {
        return this.pigUsrUser;
    }

    public void setPigUsrUser(PigUsrUser pigUsrUser) {
        this.pigUsrUser = pigUsrUser;
    }

    // bi-directional many-to-one association to PigVers
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_VERS")
    public PigVers getPigVer() {
        return this.pigVer;
    }

    public void setPigVer(PigVers pigVer) {
        this.pigVer = pigVer;
    }

}
