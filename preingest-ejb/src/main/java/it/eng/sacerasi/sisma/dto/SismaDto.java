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

/**
 *
 */
package it.eng.sacerasi.sisma.dto;

import java.math.BigDecimal;
import java.util.Date;

import it.eng.sacerasi.util.GenericDto;

public class SismaDto extends GenericDto {

    private static final long serialVersionUID = 1L;

    private long idSisma;
    private BigDecimal idVers;
    private String cdTipoFinanziamento;
    private String dsTipoFinanziamento;
    private String tiStato;
    private String dsDescrizione;
    private Date dtStato;
    private Date dtCreazione;
    private Date data;
    private BigDecimal priorita;
    private long idUserIam;
    private BigDecimal anno;
    private String numero;
    private String nmAmbienteVers;
    private String nmTipoSisma;
    private String tiFaseSisma;
    private String dsFaseSisma;
    private String cdKey;
    private String oggetto;
    private String tiTipoAtto;
    private String codiceIntervento;
    private String denominazioneIntervento;
    private BigDecimal idTipoFinanziamento;
    private BigDecimal idSismaValAtto;
    private BigDecimal idSismaProgettiAg;
    private BigDecimal idSismaFaseProgetto;
    private BigDecimal idSismaStatoProgetto;
    private String dsStatoProgetto;
    private String nmTipoAtto;
    private BigDecimal annoAg;
    private String numeroAg;
    private String registroAg;
    private Date dataAg;
    private String classifica;
    private String idFascicolo;
    private String oggettoFascicolo;
    private String idSottofascicolo;
    private String oggettoSottofascicolo;
    private String classificaAg;
    private String idFascicoloAg;
    private String oggettoFascicoloAg;
    private String idSottofascicoloAg;
    private String oggettoSottofascicoloAg;
    private boolean flInviatoAEnte;
    private boolean flInterventoSoggettoATutela;
    private String cdErr;
    private String dsErr;

    public SismaDto() {
        super();
    }

    public String getTiTipoAtto() {
        return tiTipoAtto;
    }

    public void setTiTipoAtto(String tiTipoAtto) {
        this.tiTipoAtto = tiTipoAtto;
    }

    public String getCodiceIntervento() {
        return codiceIntervento;
    }

    public void setCodiceIntervento(String codiceIntervento) {
        this.codiceIntervento = codiceIntervento;
    }

    public String getDenominazioneIntervento() {
        return denominazioneIntervento;
    }

    public void setDenominazioneIntervento(String denominazioneIntervento) {
        this.denominazioneIntervento = denominazioneIntervento;
    }

    public String getDsFaseSisma() {
        return dsFaseSisma;
    }

    public void setDsFaseSisma(String dsFaseSisma) {
        this.dsFaseSisma = dsFaseSisma;
    }

    public String getCdTipoFinanziamento() {
        return cdTipoFinanziamento;
    }

    public void setCdTipoFinanziamento(String cdTipoFinanziamento) {
        this.cdTipoFinanziamento = cdTipoFinanziamento;
    }

    public BigDecimal getIdTipoFinanziamento() {
        return idTipoFinanziamento;
    }

    public void setIdTipoFinanziamento(BigDecimal idTipoFinanziamento) {
        this.idTipoFinanziamento = idTipoFinanziamento;
    }

    public String getDsTipoFinanziamento() {
        return dsTipoFinanziamento;
    }

    public void setDsTipoFinanziamento(String dsTipoFinanziamento) {
        this.dsTipoFinanziamento = dsTipoFinanziamento;
    }

    public Date getDtStato() {
        return dtStato;
    }

    public void setDtStato(Date dtStato) {
        this.dtStato = dtStato;
    }

    public String getDsDescrizione() {
        return dsDescrizione;
    }

    public void setDsDescrizione(String dsDescrizione) {
        this.dsDescrizione = dsDescrizione;
    }

    public String getCdKey() {
        return cdKey;
    }

    public void setCdKey(String cdKey) {
        this.cdKey = cdKey;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public long getIdSisma() {
        return idSisma;
    }

    public void setIdSisma(long idSisma) {
        this.idSisma = idSisma;
    }

    public BigDecimal getIdVers() {
        return idVers;
    }

    public void setIdVers(BigDecimal idVers) {
        this.idVers = idVers;
    }

    public String getTiStato() {
        return tiStato;
    }

    public void setTiStato(String tiStato) {
        this.tiStato = tiStato;
    }

    public Date getDtCreazione() {
        return dtCreazione;
    }

    public void setDtCreazione(Date dtCreazione) {
        this.dtCreazione = dtCreazione;
    }

    public BigDecimal getPriorita() {
        return priorita;
    }

    public void setPriorita(BigDecimal priorita) {
        this.priorita = priorita;
    }

    public long getIdUserIam() {
        return idUserIam;
    }

    public void setIdUserIam(long idUserIam) {
        this.idUserIam = idUserIam;
    }

    public BigDecimal getAnno() {
        return anno;
    }

    public void setAnno(BigDecimal anno) {
        this.anno = anno;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getNmAmbienteVers() {
        return nmAmbienteVers;
    }

    public void setNmAmbienteVers(String nmAmbienteVers) {
        this.nmAmbienteVers = nmAmbienteVers;
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

    public String getOggetto() {
        return oggetto;
    }

    public void setOggetto(String oggetto) {
        this.oggetto = oggetto;
    }

    public String getCdErr() {
        return cdErr;
    }

    public void setCdErr(String cdErr) {
        this.cdErr = cdErr;
    }

    public String getDsErr() {
        return dsErr;
    }

    public void setDsErr(String dsErr) {
        this.dsErr = dsErr;
    }

    public BigDecimal getIdSismaValAtto() {
        return idSismaValAtto;
    }

    public void setIdSismaValAtto(BigDecimal idSismaValAtto) {
        this.idSismaValAtto = idSismaValAtto;
    }

    public String getNmTipoAtto() {
        return nmTipoAtto;
    }

    public void setNmTipoAtto(String nmTipoAtto) {
        this.nmTipoAtto = nmTipoAtto;
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

    public Date getDataAg() {
        return dataAg;
    }

    public void setDataAg(Date dataAg) {
        this.dataAg = dataAg;
    }

    public String getClassifica() {
        return classifica;
    }

    public void setClassifica(String classifica) {
        this.classifica = classifica;
    }

    public String getIdFascicolo() {
        return idFascicolo;
    }

    public void setIdFascicolo(String idFascicolo) {
        this.idFascicolo = idFascicolo;
    }

    public String getOggettoFascicolo() {
        return oggettoFascicolo;
    }

    public void setOggettoFascicolo(String oggettoFascicolo) {
        this.oggettoFascicolo = oggettoFascicolo;
    }

    public String getIdSottofascicolo() {
        return idSottofascicolo;
    }

    public void setIdSottofascicolo(String idSottofascicolo) {
        this.idSottofascicolo = idSottofascicolo;
    }

    public String getOggettoSottofascicolo() {
        return oggettoSottofascicolo;
    }

    public void setOggettoSottofascicolo(String oggettoSottofascicolo) {
        this.oggettoSottofascicolo = oggettoSottofascicolo;
    }

    public String getClassificaAg() {
        return classificaAg;
    }

    public void setClassificaAg(String classificaAg) {
        this.classificaAg = classificaAg;
    }

    public String getIdFascicoloAg() {
        return idFascicoloAg;
    }

    public void setIdFascicoloAg(String idFascicoloAg) {
        this.idFascicoloAg = idFascicoloAg;
    }

    public String getOggettoFascicoloAg() {
        return oggettoFascicoloAg;
    }

    public void setOggettoFascicoloAg(String oggettoFascicoloAg) {
        this.oggettoFascicoloAg = oggettoFascicoloAg;
    }

    public String getIdSottofascicoloAg() {
        return idSottofascicoloAg;
    }

    public void setIdSottofascicoloAg(String idSottofascicoloAg) {
        this.idSottofascicoloAg = idSottofascicoloAg;
    }

    public String getOggettoSottofascicoloAg() {
        return oggettoSottofascicoloAg;
    }

    public void setOggettoSottofascicoloAg(String oggettoSottofascicoloAg) {
        this.oggettoSottofascicoloAg = oggettoSottofascicoloAg;
    }

    public boolean isFlInviatoAEnte() {
        return flInviatoAEnte;
    }

    public void setFlInviatoAEnte(boolean flInviatoAEnte) {
        this.flInviatoAEnte = flInviatoAEnte;
    }

    public boolean isFlInterventoSoggettoATutela() {
        return flInterventoSoggettoATutela;
    }

    public void setFlInterventoSoggettoATutela(boolean flInterventoSoggettoATutela) {
        this.flInterventoSoggettoATutela = flInterventoSoggettoATutela;
    }

    public BigDecimal getIdSismaProgettiAg() {
        return idSismaProgettiAg;
    }

    public void setIdSismaProgettiAg(BigDecimal idSismaProgettiAg) {
        this.idSismaProgettiAg = idSismaProgettiAg;
    }

    public BigDecimal getIdSismaFaseProgetto() {
        return idSismaFaseProgetto;
    }

    public void setIdSismaFaseProgetto(BigDecimal idSismaFaseProgetto) {
        this.idSismaFaseProgetto = idSismaFaseProgetto;
    }

    public BigDecimal getIdSismaStatoProgetto() {
        return idSismaStatoProgetto;
    }

    public void setIdSismaStatoProgetto(BigDecimal idSismaStatoProgetto) {
        this.idSismaStatoProgetto = idSismaStatoProgetto;
    }

    public String getDsStatoProgetto() {
        return dsStatoProgetto;
    }

    public void setDsStatoProgetto(String dsStatoProgetto) {
        this.dsStatoProgetto = dsStatoProgetto;
    }

}
