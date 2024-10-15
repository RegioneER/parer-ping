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

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * The persistent class for the ORG_V_RIC_ENTE_CONVENZ database table.
 *
 */
// @Immutable
@Entity
@Table(schema = "SACER_IAM", name = "ORG_V_RIC_ENTE_CONVENZ_BY_EST")
public class OrgVRicEnteConvenzByEsterno implements Serializable {

    private static final long serialVersionUID = 1L;
    private OrgVRicEnteConvenzByEsternoId orgVRicEnteConvenzByEsternoId;
    private String nmEnteConvenz;
    private BigDecimal idCategEnte;
    private BigDecimal idAmbitoTerrit;
    private Date dtCessazione;
    private Date dtIniVal;
    private String flRecesso;
    private BigDecimal enteAttivo;
    private BigDecimal idAmbienteEnteConvenz;
    private Date dtDecAccordo;
    private Date dtScadAccordo;
    private String flInCorsoConvenz;
    private String flNonConvenz;
    private String tiEnteConvenz;
    private BigDecimal idEnteConserv;
    private BigDecimal idEnteGestore;
    private Date dtFineValidAccordo;

    public OrgVRicEnteConvenzByEsterno() {
    }

    public OrgVRicEnteConvenzByEsterno(BigDecimal idEnteConvenz, String nmEnteConvenz, BigDecimal idCategEnte,
            BigDecimal idAmbitoTerrit, Date dtCessazione, Date dtIniVal, String flRecesso, BigDecimal enteAttivo,
            BigDecimal idAmbienteEnteConvenz, Date dtDecAccordo, Date dtScadAccordo, String flInCorsoConvenz,
            String flNonConvenz, String tiEnteConvenz, BigDecimal idEnteConserv, BigDecimal idEnteGestore,
            Date dtFineValidAccordo) {
        this.orgVRicEnteConvenzByEsternoId = new OrgVRicEnteConvenzByEsternoId();
        this.orgVRicEnteConvenzByEsternoId.setIdEnteConvenz(idEnteConvenz);
        this.nmEnteConvenz = nmEnteConvenz;
        this.idCategEnte = idCategEnte;
        this.idAmbitoTerrit = idAmbitoTerrit;
        this.dtCessazione = dtCessazione;
        this.dtIniVal = dtIniVal;
        this.flRecesso = flRecesso;
        this.enteAttivo = enteAttivo;
        this.idAmbienteEnteConvenz = idAmbienteEnteConvenz;
        this.dtDecAccordo = dtDecAccordo;
        this.dtScadAccordo = dtScadAccordo;
        this.flInCorsoConvenz = flInCorsoConvenz;
        this.flNonConvenz = flNonConvenz;
        this.tiEnteConvenz = tiEnteConvenz;
        this.idEnteConserv = idEnteConserv;
        this.idEnteGestore = idEnteGestore;
        this.dtFineValidAccordo = dtFineValidAccordo;
    }

    public OrgVRicEnteConvenzByEsterno(BigDecimal idEnteConvenz, String nmEnteConvenz, BigDecimal idCategEnte,
            BigDecimal idAmbitoTerrit, Date dtIniVal, Date dtCessazione, BigDecimal enteAttivo, String flRecesso,
            Date dtScadAccordo) {
        this.orgVRicEnteConvenzByEsternoId = new OrgVRicEnteConvenzByEsternoId();
        this.orgVRicEnteConvenzByEsternoId.setIdEnteConvenz(idEnteConvenz);
        this.nmEnteConvenz = nmEnteConvenz;
        this.idCategEnte = idCategEnte;
        this.idAmbitoTerrit = idAmbitoTerrit;
        this.dtIniVal = dtIniVal;
        this.dtCessazione = dtCessazione;
        this.enteAttivo = enteAttivo;
        this.flRecesso = flRecesso;
        this.dtScadAccordo = dtScadAccordo;
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
    @Column(name = "DT_DEC_ACCORDO")
    public Date getDtDecAccordo() {
        return this.dtDecAccordo;
    }

    public void setDtDecAccordo(Date dtDecAccordo) {
        this.dtDecAccordo = dtDecAccordo;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_SCAD_ACCORDO")
    public Date getDtScadAccordo() {
        return this.dtScadAccordo;
    }

    public void setDtScadAccordo(Date dtScadAccordo) {
        this.dtScadAccordo = dtScadAccordo;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_INI_VAL")
    public Date getDtIniVal() {
        return this.dtIniVal;
    }

    public void setDtIniVal(Date dtIniVal) {
        this.dtIniVal = dtIniVal;
    }

    @Column(name = "ENTE_ATTIVO", columnDefinition = "char")
    public BigDecimal getEnteAttivo() {
        return this.enteAttivo;
    }

    public void setEnteAttivo(BigDecimal enteAttivo) {
        this.enteAttivo = enteAttivo;
    }

    @Column(name = "FL_IN_CORSO_CONVENZ", columnDefinition = "char")
    public String getFlInCorsoConvenz() {
        return this.flInCorsoConvenz;
    }

    public void setFlInCorsoConvenz(String flInCorsoConvenz) {
        this.flInCorsoConvenz = flInCorsoConvenz;
    }

    @Column(name = "FL_NON_CONVENZ", columnDefinition = "char")
    public String getFlNonConvenz() {
        return this.flNonConvenz;
    }

    public void setFlNonConvenz(String flNonConvenz) {
        this.flNonConvenz = flNonConvenz;
    }

    @Column(name = "FL_RECESSO", columnDefinition = "char")
    public String getFlRecesso() {
        return this.flRecesso;
    }

    public void setFlRecesso(String flRecesso) {
        this.flRecesso = flRecesso;
    }

    @Column(name = "ID_AMBIENTE_ENTE_CONVENZ")
    public BigDecimal getIdAmbienteEnteConvenz() {
        return this.idAmbienteEnteConvenz;
    }

    public void setIdAmbienteEnteConvenz(BigDecimal idAmbienteEnteConvenz) {
        this.idAmbienteEnteConvenz = idAmbienteEnteConvenz;
    }

    @Column(name = "ID_AMBITO_TERRIT")
    public BigDecimal getIdAmbitoTerrit() {
        return this.idAmbitoTerrit;
    }

    public void setIdAmbitoTerrit(BigDecimal idAmbitoTerrit) {
        this.idAmbitoTerrit = idAmbitoTerrit;
    }

    @Column(name = "ID_CATEG_ENTE")
    public BigDecimal getIdCategEnte() {
        return this.idCategEnte;
    }

    public void setIdCategEnte(BigDecimal idCategEnte) {
        this.idCategEnte = idCategEnte;
    }

    @Column(name = "ID_ENTE_CONSERV")
    public BigDecimal getIdEnteConserv() {
        return this.idEnteConserv;
    }

    public void setIdEnteConserv(BigDecimal idEnteConserv) {
        this.idEnteConserv = idEnteConserv;
    }

    @Column(name = "ID_ENTE_GESTORE")
    public BigDecimal getIdEnteGestore() {
        return this.idEnteGestore;
    }

    public void setIdEnteGestore(BigDecimal idEnteGestore) {
        this.idEnteGestore = idEnteGestore;
    }

    @Column(name = "NM_ENTE_CONVENZ")
    public String getNmEnteConvenz() {
        return this.nmEnteConvenz;
    }

    public void setNmEnteConvenz(String nmEnteConvenz) {
        this.nmEnteConvenz = nmEnteConvenz;
    }

    @Column(name = "TI_ENTE_CONVENZ")
    public String getTiEnteConvenz() {
        return this.tiEnteConvenz;
    }

    public void setTiEnteConvenz(String tiEnteConvenz) {
        this.tiEnteConvenz = tiEnteConvenz;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_FINE_VALID_ACCORDO")
    public Date getDtFineValidAccordo() {
        return this.dtFineValidAccordo;
    }

    public void setDtFineValidAccordo(Date dtFineValidAccordo) {
        this.dtFineValidAccordo = dtFineValidAccordo;
    }

    @EmbeddedId()
    public OrgVRicEnteConvenzByEsternoId getOrgVRicEnteConvenzByEsternoId() {
        return orgVRicEnteConvenzByEsternoId;
    }

    public void setOrgVRicEnteConvenzByEsternoId(OrgVRicEnteConvenzByEsternoId orgVRicEnteConvenzByEsternoId) {
        this.orgVRicEnteConvenzByEsternoId = orgVRicEnteConvenzByEsternoId;
    }

}
