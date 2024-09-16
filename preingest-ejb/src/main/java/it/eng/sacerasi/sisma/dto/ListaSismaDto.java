/**
 *
 */
package it.eng.sacerasi.sisma.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import it.eng.sacerasi.common.Constants;

public class ListaSismaDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private long idSisma;
    private String nmTipoSisma;
    private String tiFaseSisma;
    private String dsFaseSisma;
    private String codiceIntervento;
    private long anno;
    private String cdKey;
    private String tiStato;
    private String statoProgetto;
    private String dsDescrizione;
    private Date dtCreazione;
    private BigDecimal dimensione;
    private long idVersatore;
    private String nmVersatore;
    private String nmSoggettoAttuatore;
    private BigDecimal annoAg;
    private String numeroAg;
    private String registroAg;
    private String dsTipoFinanziamento;
    private String oggetto;
    private Enum<Constants.TipoVersatore> tipoVersatore;

    public ListaSismaDto() {
        super();
    }

    public long getIdSisma() {
        return idSisma;
    }

    public void setIdSisma(long idSisma) {
        this.idSisma = idSisma;
    }

    public String getCodiceIntervento() {
        return codiceIntervento;
    }

    public void setCodiceIntervento(String codiceIntervento) {
        this.codiceIntervento = codiceIntervento;
    }

    public String getNmTipoSisma() {
        return nmTipoSisma;
    }

    public void setNmTipoSisma(String nmTipoSisma) {
        this.nmTipoSisma = nmTipoSisma;
    }

    public String getTiFaseSisma() {
        return tiFaseSisma;
    }

    public void setTiFaseSisma(String tiFaseSisma) {
        this.tiFaseSisma = tiFaseSisma;
    }

    public String getDsFaseSisma() {
        return dsFaseSisma;
    }

    public void setDsFaseSisma(String dsFaseSisma) {
        this.dsFaseSisma = dsFaseSisma;
    }

    public long getAnno() {
        return anno;
    }

    public void setAnno(long anno) {
        this.anno = anno;
    }

    public String getCdKey() {
        return cdKey;
    }

    public void setCdKey(String cdKey) {
        this.cdKey = cdKey;
    }

    public String getTiStato() {
        return tiStato;
    }

    public void setTiStato(String tiStato) {
        this.tiStato = tiStato;
    }

    public String getStatoProgetto() {
        return statoProgetto;
    }

    public void setStatoProgetto(String statoProgetto) {
        this.statoProgetto = statoProgetto;
    }

    public String getDsDescrizione() {
        return dsDescrizione;
    }

    public void setDsDescrizione(String dsDescrizione) {
        this.dsDescrizione = dsDescrizione;
    }

    public Date getDtCreazione() {
        return dtCreazione;
    }

    public void setDtCreazione(Date dtCreazione) {
        this.dtCreazione = dtCreazione;
    }

    public BigDecimal getDimensione() {
        return dimensione;
    }

    public void setDimensione(BigDecimal dimensione) {
        this.dimensione = dimensione;
    }

    public long getIdVersatore() {
        return idVersatore;
    }

    public void setIdVersatore(long idVersatore) {
        this.idVersatore = idVersatore;
    }

    public String getNmVersatore() {
        return nmVersatore;
    }

    public void setNmVersatore(String nmVersatore) {
        this.nmVersatore = nmVersatore;
    }

    public String getNmSoggettoAttuatore() {
        return nmSoggettoAttuatore;
    }

    public void setNmSoggettoAttuatore(String nmSoggettoAttuatore) {
        this.nmSoggettoAttuatore = nmSoggettoAttuatore;
    }

    public BigDecimal getAnnoAg() {
        return annoAg;
    }

    public void setAnnoAg(BigDecimal annoAg) {
        this.annoAg = annoAg;
    }

    public String getNumeroAg() {
        return numeroAg;
    }

    public void setNumeroAg(String numeroAg) {
        this.numeroAg = numeroAg;
    }

    public String getRegistroAg() {
        return registroAg;
    }

    public void setRegistroAg(String registroAg) {
        this.registroAg = registroAg;
    }

    public String getDsTipoFinanziamento() {
        return dsTipoFinanziamento;
    }

    public void setDsTipoFinanziamento(String dsTipoFinanziamento) {
        this.dsTipoFinanziamento = dsTipoFinanziamento;
    }

    public String getOggetto() {
        return oggetto;
    }

    public void setOggetto(String oggetto) {
        this.oggetto = oggetto;
    }

    public Enum<Constants.TipoVersatore> getTipoVersatore() {
        return tipoVersatore;
    }

    public void setTipoVersatore(Enum<Constants.TipoVersatore> tipoVersatore) {
        this.tipoVersatore = tipoVersatore;
    }
}