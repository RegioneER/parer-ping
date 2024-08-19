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
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * The persistent class for the MON_V_RIEP_VERS database table.
 *
 */
@Entity
@Table(name = "MON_V_RIEP_VERS")
public class MonVRiepVers implements Serializable {
    private static final long serialVersionUID = 1L;
    private String flCessato;
    private String flObjWarn;
    private String flSesErrNorisNover;
    private String flSesNotifDelay;
    private String flSesPrepxmlRegcodaDelay;
    private String flSesRegcodaNorisNover;
    private String flSesVersSacerDelay;
    private String flSesVersSacerNorisNover;
    private BigDecimal idVers;
    private String nmAmbienteVers;
    private String nmVers;

    public MonVRiepVers() {
    }

    @Column(name = "FL_CESSATO", columnDefinition = "char")
    public String getFlCessato() {
        return this.flCessato;
    }

    public void setFlCessato(String flCessato) {
        this.flCessato = flCessato;
    }

    @Column(name = "FL_OBJ_WARN", columnDefinition = "char")
    public String getFlObjWarn() {
        return this.flObjWarn;
    }

    public void setFlObjWarn(String flObjWarn) {
        this.flObjWarn = flObjWarn;
    }

    @Column(name = "FL_SES_ERR_NORIS_NOVER", columnDefinition = "char")
    public String getFlSesErrNorisNover() {
        return this.flSesErrNorisNover;
    }

    public void setFlSesErrNorisNover(String flSesErrNorisNover) {
        this.flSesErrNorisNover = flSesErrNorisNover;
    }

    @Column(name = "FL_SES_NOTIF_DELAY", columnDefinition = "char")
    public String getFlSesNotifDelay() {
        return this.flSesNotifDelay;
    }

    public void setFlSesNotifDelay(String flSesNotifDelay) {
        this.flSesNotifDelay = flSesNotifDelay;
    }

    @Column(name = "FL_SES_PREPXML_REGCODA_DELAY", columnDefinition = "char")
    public String getFlSesPrepxmlRegcodaDelay() {
        return this.flSesPrepxmlRegcodaDelay;
    }

    public void setFlSesPrepxmlRegcodaDelay(String flSesPrepxmlRegcodaDelay) {
        this.flSesPrepxmlRegcodaDelay = flSesPrepxmlRegcodaDelay;
    }

    @Column(name = "FL_SES_REGCODA_NORIS_NOVER", columnDefinition = "char")
    public String getFlSesRegcodaNorisNover() {
        return this.flSesRegcodaNorisNover;
    }

    public void setFlSesRegcodaNorisNover(String flSesRegcodaNorisNover) {
        this.flSesRegcodaNorisNover = flSesRegcodaNorisNover;
    }

    @Column(name = "FL_SES_VERS_SACER_DELAY", columnDefinition = "char")
    public String getFlSesVersSacerDelay() {
        return this.flSesVersSacerDelay;
    }

    public void setFlSesVersSacerDelay(String flSesVersSacerDelay) {
        this.flSesVersSacerDelay = flSesVersSacerDelay;
    }

    @Column(name = "FL_SES_VERS_SACER_NORIS_NOVER", columnDefinition = "char")
    public String getFlSesVersSacerNorisNover() {
        return this.flSesVersSacerNorisNover;
    }

    public void setFlSesVersSacerNorisNover(String flSesVersSacerNorisNover) {
        this.flSesVersSacerNorisNover = flSesVersSacerNorisNover;
    }

    @Id
    @Column(name = "ID_VERS")
    public BigDecimal getIdVers() {
        return this.idVers;
    }

    public void setIdVers(BigDecimal idVers) {
        this.idVers = idVers;
    }

    @Column(name = "NM_AMBIENTE_VERS")
    public String getNmAmbienteVers() {
        return this.nmAmbienteVers;
    }

    public void setNmAmbienteVers(String nmAmbienteVers) {
        this.nmAmbienteVers = nmAmbienteVers;
    }

    @Column(name = "NM_VERS")
    public String getNmVers() {
        return this.nmVers;
    }

    public void setNmVers(String nmVers) {
        this.nmVers = nmVers;
    }

}
