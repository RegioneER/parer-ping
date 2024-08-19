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

package it.eng.sacerasi.sisma.dto;

import it.eng.sacerasi.slite.gen.form.SismaForm;
import it.eng.spagoCore.error.EMFError;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Cappelli_F
 */
public class RicercaSismaDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String soggettoAttuatore;
    private BigDecimal idLineaFin;
    private String nmIntervento;
    private String nmFaseProg;
    private String nmStatoProg;
    private String nmStato;
    private BigDecimal anno;
    private String cdIdentificativo;
    private String cdOggetto;
    private String cdIdVersamento;
    private String cdRegistroAgenzia;
    private String cdNumeroAgenzia;
    private BigDecimal annoAgenzia;
    private Date dtCreazione;

    private boolean isAgenzia;

    public RicercaSismaDTO() {
    }

    public RicercaSismaDTO(SismaForm.FiltriSisma filtri, boolean isAgenzia) throws EMFError {
        this.soggettoAttuatore = filtri.getId_soggetto_att().parse();
        this.idLineaFin = filtri.getId_linea_fin().parse();
        this.nmIntervento = filtri.getNm_intervento().parse();
        this.nmFaseProg = filtri.getNm_fase_prog().parse();
        this.nmStatoProg = filtri.getNm_stato_prog().parse();
        this.nmStato = filtri.getNm_stato().parse();
        this.anno = filtri.getAnno().parse();
        this.cdIdentificativo = filtri.getCd_identificativo().parse();
        this.cdOggetto = filtri.getCd_oggetto().parse();
        this.dtCreazione = filtri.getDt_creazione().parse();
        this.cdRegistroAgenzia = filtri.getCd_registro_agenzia().parse();
        this.cdNumeroAgenzia = filtri.getCd_num_agenzia().parse();
        this.annoAgenzia = filtri.getAnno_agenzia().parse();

        this.isAgenzia = isAgenzia;
    }

    public String getSoggettoAttuatore() {
        return soggettoAttuatore;
    }

    public void setSoggettoAttuatore(String soggettoAttuatore) {
        this.soggettoAttuatore = soggettoAttuatore;
    }

    public boolean isSoggettoAttuatore() {
        return this.soggettoAttuatore != null && !this.soggettoAttuatore.isEmpty();
    }

    public BigDecimal getIdLineaFin() {
        return idLineaFin;
    }

    public void setIdLineaFin(BigDecimal idLineaFin) {
        this.idLineaFin = idLineaFin;
    }

    public boolean isIdLineaFin() {
        return this.idLineaFin != null;
    }

    public String getNmIntervento() {
        return nmIntervento;
    }

    public void setNmIntervento(String nmIntervento) {
        this.nmIntervento = nmIntervento;
    }

    public boolean isNmIntervento() {
        return this.nmIntervento != null && !this.nmIntervento.isEmpty();
    }

    public String getNmFaseProg() {
        return nmFaseProg;
    }

    public void setNmFaseProg(String nmFaseProg) {
        this.nmFaseProg = nmFaseProg;
    }

    public boolean isNmFaseProg() {
        return this.nmFaseProg != null && !this.nmFaseProg.isEmpty();
    }

    public String getNmStatoProg() {
        return nmStatoProg;
    }

    public void setNmStatoProg(String nmStatoProg) {
        this.nmStatoProg = nmStatoProg;
    }

    public boolean isNmStatoProg() {
        return this.nmStatoProg != null && !this.nmStatoProg.isEmpty();
    }

    public String getNmStato() {
        return nmStato;
    }

    public void setNmStato(String nmStato) {
        this.nmStato = nmStato;
    }

    public boolean isNmStato() {
        return this.nmStato != null && !this.nmStato.isEmpty();
    }

    public BigDecimal getAnno() {
        return anno;
    }

    public void setAnno(BigDecimal anno) {
        this.anno = anno;
    }

    public boolean isAnno() {
        return this.anno != null;
    }

    public String getCdIdentificativo() {
        return cdIdentificativo;
    }

    public void setCdIdentificativo(String cdIdentificativo) {
        this.cdIdentificativo = cdIdentificativo;
    }

    public boolean isCdIdentificativo() {
        return this.cdIdentificativo != null && !this.cdIdentificativo.isEmpty();
    }

    public String getCdOggetto() {
        return cdOggetto;
    }

    public void setCdOggetto(String cdOggetto) {
        this.cdOggetto = cdOggetto;
    }

    public boolean isCdOggetto() {
        return this.cdOggetto != null && !this.cdOggetto.isEmpty();
    }

    public String getCdIdVersamento() {
        return cdIdVersamento;
    }

    public void setCdIdVersamento(String cdIdVersamento) {
        this.cdIdVersamento = cdIdVersamento;
    }

    public boolean isCdIdVersamento() {
        return this.cdIdVersamento != null && !this.cdIdVersamento.isEmpty();
    }

    public String getCdRegistroAgenzia() {
        return cdRegistroAgenzia;
    }

    public void setCdRegistroAgenzia(String cdRegistroAgenzia) {
        this.cdRegistroAgenzia = cdRegistroAgenzia;
    }

    public boolean isCdRegistroAgenzia() {
        return this.cdRegistroAgenzia != null && !this.cdRegistroAgenzia.isEmpty();
    }

    public String getCdNumeroAgenzia() {
        return cdNumeroAgenzia;
    }

    public void setCdNumeroAgenzia(String cdNumeroAgenzia) {
        this.cdNumeroAgenzia = cdNumeroAgenzia;
    }

    public boolean isCdNumeroAgenzia() {
        return this.cdNumeroAgenzia != null && !this.cdNumeroAgenzia.isEmpty();
    }

    public BigDecimal getAnnoAgenzia() {
        return annoAgenzia;
    }

    public void setAnnoAgenzia(BigDecimal annoAgenzia) {
        this.annoAgenzia = annoAgenzia;
    }

    public boolean isAnnoAgenzia() {
        return this.annoAgenzia != null;
    }

    public Date getDtCreazione() {
        return dtCreazione;
    }

    public void setDtCreazione(Date dtCreazione) {
        this.dtCreazione = dtCreazione;
    }

    public boolean isDtCreazione() {
        return this.dtCreazione != null;
    }

    public boolean isAgenzia() {
        return isAgenzia;
    }

    public void setIsAgenzia(boolean isAgenzia) {
        this.isAgenzia = isAgenzia;
    }
}
