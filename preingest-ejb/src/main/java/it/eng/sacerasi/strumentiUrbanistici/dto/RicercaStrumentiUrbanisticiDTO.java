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

package it.eng.sacerasi.strumentiUrbanistici.dto;

import java.math.BigDecimal;
import java.util.Date;

import it.eng.sacerasi.slite.gen.form.StrumentiUrbanisticiForm;
import it.eng.spagoCore.error.EMFError;

/**
 * @author Cappelli_F
 */
public class RicercaStrumentiUrbanisticiDTO {

    private String tiStrumentoUrbanistico;
    private String nmFaseElaborazione;
    private Date dtCreazione;
    private String cdOggetto;
    private String anno;
    private String cdNumero;
    private String nmStato;

    // MEV 30026
    private Long idPuc;
    private String nrBurert;
    private Date dtBurert;
    private String cdRepertorio;
    private Long annoProtocollo;
    private String cdProtocollo;
    private Date dtProtocollo;
    private boolean isUfficioUrbanistica;

    public RicercaStrumentiUrbanisticiDTO() {
    }

    public RicercaStrumentiUrbanisticiDTO(
            StrumentiUrbanisticiForm.FiltriStrumentiUrbanistici filtri,
            boolean isUfficioUrbanistica) throws EMFError {
        this.tiStrumentoUrbanistico = filtri.getTi_strumento_urbanistico().parse();
        this.nmFaseElaborazione = filtri.getNm_fase_elaborazione().parse();
        this.dtCreazione = filtri.getDt_creazione().parse();
        this.cdOggetto = filtri.getCd_oggetto().parse();
        this.anno = filtri.getNi_anno().parse();
        this.cdNumero = filtri.getCd_numero().parse();
        this.nmStato = filtri.getNm_stato().parse();

        this.idPuc = filtri.getId_puc_filtro().parse() != null
                ? filtri.getId_puc_filtro().parse().longValue()
                : null;
        this.nrBurert = filtri.getNr_burert_filtro().parse();
        this.dtBurert = filtri.getDt_burert_filtro().parse();
        this.cdRepertorio = filtri.getCd_repertorio_filtro().parse();
        this.annoProtocollo = filtri.getAnno_protocollo_filtro().parse() != null
                ? filtri.getAnno_protocollo_filtro().parse().longValue()
                : null;
        this.cdProtocollo = filtri.getCd_protocollo_filtro().parse();
        this.dtProtocollo = filtri.getDt_protocollo_filtro().parse();

        this.isUfficioUrbanistica = isUfficioUrbanistica;
    }

    public String getTiStrumentoUrbanistico() {
        return tiStrumentoUrbanistico;
    }

    public void setTiStrumentoUrbanistico(String tiStrumentoUrbanistico) {
        this.tiStrumentoUrbanistico = tiStrumentoUrbanistico;
    }

    public boolean isTiStrumentoUrbanistico() {
        return this.tiStrumentoUrbanistico != null && !this.tiStrumentoUrbanistico.isEmpty();
    }

    public String getNmFaseElaborazione() {
        return nmFaseElaborazione;
    }

    public void setNmFaseElaborazione(String nmFaseElaborazione) {
        this.nmFaseElaborazione = nmFaseElaborazione;
    }

    public boolean isNmFaseElaborazione() {
        return this.nmFaseElaborazione != null && !this.nmFaseElaborazione.isEmpty();
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

    public String getCdOggetto() {
        return cdOggetto;
    }

    public void setCdOggetto(String cdOggetto) {
        this.cdOggetto = cdOggetto;
    }

    public boolean isCdOggetto() {
        return this.cdOggetto != null && !this.cdOggetto.isEmpty();
    }

    public String getAnno() {
        return anno;
    }

    public void setAnno(String anno) {
        this.anno = anno;
    }

    public boolean isAnno() {
        return this.anno != null;
    }

    public String getCdNumero() {
        return cdNumero;
    }

    public void setCdNumero(String cdNumero) {
        this.cdNumero = cdNumero;
    }

    public boolean isCdNumero() {
        return this.cdNumero != null && !this.cdNumero.isEmpty();
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

    public boolean isUfficioUrbanistica() {
        return isUfficioUrbanistica;
    }

    public void setUfficioUrbanistica(boolean ufficioUrbanistica) {
        isUfficioUrbanistica = ufficioUrbanistica;
    }

    public Long getIdPuc() {
        return idPuc;
    }

    public void setIdPuc(Long idPuc) {
        this.idPuc = idPuc;
    }

    public boolean isIdPuc() {
        return this.idPuc != null;
    }

    public String getNrBurert() {
        return nrBurert;
    }

    public void setNrBurert(String nrBurert) {
        this.nrBurert = nrBurert;
    }

    public boolean isNrBurert() {
        return this.nrBurert != null && !this.nrBurert.isEmpty();
    }

    public Date getDtBurert() {
        return dtBurert;
    }

    public void setDtBurert(Date dtBurert) {
        this.dtBurert = dtBurert;
    }

    public boolean isDtBurert() {
        return this.dtBurert != null;
    }

    public String getCdRepertorio() {
        return cdRepertorio;
    }

    public void setCdRepertorio(String cdRepertorio) {
        this.cdRepertorio = cdRepertorio;
    }

    public boolean isCdRepertorio() {
        return this.cdRepertorio != null && !this.cdRepertorio.isEmpty();
    }

    public Long getAnnoProtocollo() {
        return annoProtocollo;
    }

    public void setAnnoProtocollo(Long annoProtocollo) {
        this.annoProtocollo = annoProtocollo;
    }

    public boolean isAnnoProtocollo() {
        return this.annoProtocollo != null;
    }

    public String getCdProtocollo() {
        return cdProtocollo;
    }

    public void setCdProtocollo(String cdProtocollo) {
        this.cdProtocollo = cdProtocollo;
    }

    public boolean isCdProtocollo() {
        return this.cdProtocollo != null && !this.cdProtocollo.isEmpty();
    }

    public Date getDtProtocollo() {
        return dtProtocollo;
    }

    public void setDtProtocollo(Date dtProtocollo) {
        this.dtProtocollo = dtProtocollo;
    }

    public boolean isDtProtocollo() {
        return this.dtProtocollo != null;
    }
}
