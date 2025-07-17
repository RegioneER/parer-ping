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

import javax.persistence.Basic;
import javax.persistence.CascadeType;
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
 * The persistent class for the ORG_ACCORDO_ENTE database table.
 *
 */
@Entity
@Table(schema = "SACER_IAM", name = "ORG_ACCORDO_ENTE")
public class SIOrgAccordoEnte implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long idAccordoEnte;
    private BigDecimal aaRepertorio;
    private String cdCapitolo;
    private String cdCig;
    private String cdCoge;
    private String cdCup;
    private String cdDdt;
    private String cdKeyRepertorio;
    private String cdOda;
    private String cdRegistroRepertorio;
    private String cdRifContab;
    private String cdUfe;
    private String dsAttoAccordo;
    private String dsFirmatarioEnte;
    private Date dtRichModuloInfo;
    private String dsNoteAccordo;
    private String dsUfe;
    private Date dtAttoAccordo;
    private Date dtDecAccordo;
    private Date dtRegAccordo;
    private Date dtScadAccordo;
    private Date dtFineValidAccordo;
    private String flPagamento;
    private String flRecesso;
    private BigDecimal niAbitanti;
    private SIOrgEnteSiam siOrgEnteConvenz;
    private SIOrgEnteSiam orgEnteSiamByIdEnteConvenzAmministratore;
    private SIOrgEnteSiam orgEnteSiamByIdEnteConvenzConserv;
    private SIOrgEnteSiam orgEnteSiamByIdEnteConvenzGestore;

    public SIOrgAccordoEnte() {
    }

    public SIOrgAccordoEnte(long idAccordoEnte) {
	this.idAccordoEnte = idAccordoEnte;
    }

    @Id
    @Column(name = "ID_ACCORDO_ENTE")
    public Long getIdAccordoEnte() {
	return this.idAccordoEnte;
    }

    public void setIdAccordoEnte(Long idAccordoEnte) {
	this.idAccordoEnte = idAccordoEnte;
    }

    @Basic
    @Column(name = "AA_REPERTORIO")
    public BigDecimal getAaRepertorio() {
	return this.aaRepertorio;
    }

    public void setAaRepertorio(BigDecimal aaRepertorio) {
	this.aaRepertorio = aaRepertorio;
    }

    @Basic
    @Column(name = "CD_CAPITOLO")
    public String getCdCapitolo() {
	return this.cdCapitolo;
    }

    public void setCdCapitolo(String cdCapitolo) {
	this.cdCapitolo = cdCapitolo;
    }

    @Basic
    @Column(name = "CD_CIG")
    public String getCdCig() {
	return this.cdCig;
    }

    public void setCdCig(String cdCig) {
	this.cdCig = cdCig;
    }

    @Basic
    @Column(name = "CD_COGE")
    public String getCdCoge() {
	return this.cdCoge;
    }

    public void setCdCoge(String cdCoge) {
	this.cdCoge = cdCoge;
    }

    @Basic
    @Column(name = "CD_CUP")
    public String getCdCup() {
	return this.cdCup;
    }

    public void setCdCup(String cdCup) {
	this.cdCup = cdCup;
    }

    @Basic
    @Column(name = "CD_DDT")
    public String getCdDdt() {
	return cdDdt;
    }

    public void setCdDdt(String cdDdt) {
	this.cdDdt = cdDdt;
    }

    @Basic
    @Column(name = "CD_ODA")
    public String getCdOda() {
	return cdOda;
    }

    public void setCdOda(String cdOda) {
	this.cdOda = cdOda;
    }

    @Basic
    @Column(name = "CD_KEY_REPERTORIO")
    public String getCdKeyRepertorio() {
	return this.cdKeyRepertorio;
    }

    public void setCdKeyRepertorio(String cdKeyRepertorio) {
	this.cdKeyRepertorio = cdKeyRepertorio;
    }

    @Basic
    @Column(name = "CD_REGISTRO_REPERTORIO")
    public String getCdRegistroRepertorio() {
	return this.cdRegistroRepertorio;
    }

    public void setCdRegistroRepertorio(String cdRegistroRepertorio) {
	this.cdRegistroRepertorio = cdRegistroRepertorio;
    }

    @Basic
    @Column(name = "CD_RIF_CONTAB")
    public String getCdRifContab() {
	return this.cdRifContab;
    }

    public void setCdRifContab(String cdRifContab) {
	this.cdRifContab = cdRifContab;
    }

    @Basic
    @Column(name = "CD_UFE")
    public String getCdUfe() {
	return this.cdUfe;
    }

    public void setCdUfe(String cdUfe) {
	this.cdUfe = cdUfe;
    }

    @Basic
    @Column(name = "DS_ATTO_ACCORDO")
    public String getDsAttoAccordo() {
	return this.dsAttoAccordo;
    }

    public void setDsAttoAccordo(String dsAttoAccordo) {
	this.dsAttoAccordo = dsAttoAccordo;
    }

    @Basic
    @Column(name = "DS_FIRMATARIO_ENTE")
    public String getDsFirmatarioEnte() {
	return this.dsFirmatarioEnte;
    }

    public void setDsFirmatarioEnte(String dsFirmatarioEnte) {
	this.dsFirmatarioEnte = dsFirmatarioEnte;
    }

    @Basic
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_RICH_MODULO_INFO")
    public Date getDtRichModuloInfo() {
	return this.dtRichModuloInfo;
    }

    public void setDtRichModuloInfo(Date dtRichModuloInfo) {
	this.dtRichModuloInfo = dtRichModuloInfo;
    }

    @Basic
    @Column(name = "DS_NOTE_ACCORDO")
    public String getDsNoteAccordo() {
	return dsNoteAccordo;
    }

    public void setDsNoteAccordo(String dsNoteAccordo) {
	this.dsNoteAccordo = dsNoteAccordo;
    }

    @Basic
    @Column(name = "DS_UFE")
    public String getDsUfe() {
	return this.dsUfe;
    }

    public void setDsUfe(String dsUfe) {
	this.dsUfe = dsUfe;
    }

    @Basic
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_ATTO_ACCORDO")
    public Date getDtAttoAccordo() {
	return this.dtAttoAccordo;
    }

    public void setDtAttoAccordo(Date dtAttoAccordo) {
	this.dtAttoAccordo = dtAttoAccordo;
    }

    @Basic
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_REG_ACCORDO")
    public Date getDtRegAccordo() {
	return this.dtRegAccordo;
    }

    public void setDtRegAccordo(Date dtRegAccordo) {
	this.dtRegAccordo = dtRegAccordo;
    }

    @Basic
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_DEC_ACCORDO")
    public Date getDtDecAccordo() {
	return dtDecAccordo;
    }

    public void setDtDecAccordo(Date dtDecAccordo) {
	this.dtDecAccordo = dtDecAccordo;
    }

    @Basic
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_SCAD_ACCORDO")
    public Date getDtScadAccordo() {
	return this.dtScadAccordo;
    }

    public void setDtScadAccordo(Date dtScadAccordo) {
	this.dtScadAccordo = dtScadAccordo;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_FINE_VALID_ACCORDO")
    public Date getDtFineValidAccordo() {
	return this.dtFineValidAccordo;
    }

    public void setDtFineValidAccordo(Date dtFineValidAccordo) {
	this.dtFineValidAccordo = dtFineValidAccordo;
    }

    @Basic
    @Column(name = "FL_PAGAMENTO", columnDefinition = "char")
    public String getFlPagamento() {
	return this.flPagamento;
    }

    public void setFlPagamento(String flPagamento) {
	this.flPagamento = flPagamento;
    }

    @Basic
    @Column(name = "FL_RECESSO", columnDefinition = "char")
    public String getFlRecesso() {
	return this.flRecesso;
    }

    public void setFlRecesso(String flRecesso) {
	this.flRecesso = flRecesso;
    }

    @Basic
    @Column(name = "NI_ABITANTI")
    public BigDecimal getNiAbitanti() {
	return this.niAbitanti;
    }

    public void setNiAbitanti(BigDecimal niAbitanti) {
	this.niAbitanti = niAbitanti;
    }

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "ID_ENTE_CONVENZ", nullable = false)
    public SIOrgEnteSiam getSiOrgEnteConvenz() {
	return siOrgEnteConvenz;
    }

    public void setSiOrgEnteConvenz(SIOrgEnteSiam siOrgEnteConvenz) {
	this.siOrgEnteConvenz = siOrgEnteConvenz;
    }

    // bi-directional many-to-one association to OrgEnteSiam
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_ENTE_CONVENZ_AMMINISTRATORE")
    public SIOrgEnteSiam getOrgEnteSiamByIdEnteConvenzAmministratore() {
	return this.orgEnteSiamByIdEnteConvenzAmministratore;
    }

    public void setOrgEnteSiamByIdEnteConvenzAmministratore(
	    SIOrgEnteSiam orgEnteSiamByIdEnteConvenzAmministratore) {
	this.orgEnteSiamByIdEnteConvenzAmministratore = orgEnteSiamByIdEnteConvenzAmministratore;
    }

    // bi-directional many-to-one association to OrgEnteSiam
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_ENTE_CONVENZ_CONSERV")
    public SIOrgEnteSiam getOrgEnteSiamByIdEnteConvenzConserv() {
	return this.orgEnteSiamByIdEnteConvenzConserv;
    }

    public void setOrgEnteSiamByIdEnteConvenzConserv(
	    SIOrgEnteSiam orgEnteSiamByIdEnteConvenzConserv) {
	this.orgEnteSiamByIdEnteConvenzConserv = orgEnteSiamByIdEnteConvenzConserv;
    }

    // bi-directional many-to-one association to OrgEnteSiam
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_ENTE_CONVENZ_GESTORE")
    public SIOrgEnteSiam getOrgEnteSiamByIdEnteConvenzGestore() {
	return this.orgEnteSiamByIdEnteConvenzGestore;
    }

    public void setOrgEnteSiamByIdEnteConvenzGestore(
	    SIOrgEnteSiam orgEnteSiamByIdEnteConvenzGestore) {
	this.orgEnteSiamByIdEnteConvenzGestore = orgEnteSiamByIdEnteConvenzGestore;
    }
}
