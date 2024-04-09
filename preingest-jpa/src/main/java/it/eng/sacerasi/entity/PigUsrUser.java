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
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.id.enhanced.SequenceStyleGenerator;

/**
 * The persistent class for the PIG_USR_USER database table.
 *
 */
// @Entity
// @Table(name = "PIG_USR_USER")
/**
 * @deprecated non più mappata, per ora non utilizzata
 *
 */
@Deprecated
public class PigUsrUser implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long idUser;
    private String cdPsw;
    private Date dtRegPsw;
    private Date dtScadPsw;
    private String flAttivo;
    private String nmCognomeUser;
    private String nmNomeUser;
    private String nmUserId;

    public PigUsrUser() {
    }

    @Id
    @GenericGenerator(name = "PIG_USR_USER_IDUSER_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
            @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SPIG_USR_USER"),
            @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PIG_USR_USER_IDUSER_GENERATOR")
    @Column(name = "ID_USER")
    public Long getIdUser() {
        return this.idUser;
    }

    public void setIdUser(Long idUser) {
        this.idUser = idUser;
    }

    @Column(name = "CD_PSW")
    public String getCdPsw() {
        return this.cdPsw;
    }

    public void setCdPsw(String cdPsw) {
        this.cdPsw = cdPsw;
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

    @Column(name = "NM_USER_ID")
    public String getNmUserId() {
        return this.nmUserId;
    }

    public void setNmUserId(String nmUserId) {
        this.nmUserId = nmUserId;
    }

}
