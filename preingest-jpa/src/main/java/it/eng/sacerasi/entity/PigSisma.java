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
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.*;

/**
 * The persistent class for the PIG_SISMA database table.
 *
 */
@Entity
@Table(name = "PIG_SISMA")
public class PigSisma implements Serializable {

    public static enum TiStato {
        BOZZA, DA_VERIFICARE, DA_RIVEDERE, VERIFICATO, RICHIESTA_INVIO, INVIO_IN_CORSO, ERRORE, IN_ELABORAZIONE,
        IN_ELABORAZIONE_SA, IN_TRASFORMAZIONE, IN_TRASFORMAZIONE_SA, IN_VERSAMENTO, IN_VERSAMENTO_SA, VERSATO,
        COMPLETATO, ANNULLATO;
    }

    private static final long serialVersionUID = 1L;
    private Long idSisma;
    private BigDecimal anno;
    private String cdErr;
    private String cdKey;
    private String cdKeyOs;
    private String dsErr;
    private String dsDescrizione;
    private Date dtCreazione;
    private Date data;
    private Date dtStato;
    private String numero;
    private String oggetto;
    private TiStato tiStato;
    private BigDecimal annoAg;
    private String numeroAg;
    private String registroAg;
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
    private Date dataAg;
    private String flInviatoAEnte;
    private String flInterventoSoggettoATutela;
    private IamUser iamUser;
    private PigVers pigVer;
    private PigVers pigVerAg;
    private List<PigSismaDocumenti> pigSismaDocumentis = new ArrayList<>();
    private PigSismaValAtto pigSismaValAtto;
    private PigSismaProgettiAg pigSismaProgettiAg;
    private PigSismaFaseProgetto pigSismaFaseProgetto;
    private PigSismaStatoProgetto pigSismaStatoProgetto;

    public PigSisma() {
        /* non usato */
    }

    @Id
    @SequenceGenerator(name = "PIG_SISMA_IDSISMA_GENERATOR", sequenceName = "SPIG_SISMA", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PIG_SISMA_IDSISMA_GENERATOR")
    @Column(name = "ID_SISMA")
    public Long getIdSisma() {
        return this.idSisma;
    }

    public void setIdSisma(Long idSisma) {
        this.idSisma = idSisma;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DATA")
    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_STATO")
    public Date getDtStato() {
        return dtStato;
    }

    public void setDtStato(Date dtStato) {
        this.dtStato = dtStato;
    }

    @Column(name = "ANNO")
    public BigDecimal getAnno() {
        return this.anno;
    }

    public void setAnno(BigDecimal anno) {
        this.anno = anno;
    }

    @Column(name = "CD_ERR")
    public String getCdErr() {
        return this.cdErr;
    }

    public void setCdErr(String cdErr) {
        this.cdErr = cdErr;
    }

    @Column(name = "CD_KEY")
    public String getCdKey() {
        return this.cdKey;
    }

    public void setCdKey(String cdKey) {
        this.cdKey = cdKey;
    }

    @Column(name = "DS_DESCRIZIONE")
    public String getDsDescrizione() {
        return this.dsDescrizione;
    }

    public void setDsDescrizione(String dsDescrizione) {
        this.dsDescrizione = dsDescrizione;
    }

    @Column(name = "CD_KEY_OS")
    public String getCdKeyOs() {
        return this.cdKeyOs;
    }

    public void setCdKeyOs(String cdKeyOs) {
        this.cdKeyOs = cdKeyOs;
    }

    @Column(name = "DS_ERR")
    public String getDsErr() {
        return this.dsErr;
    }

    public void setDsErr(String dsErr) {
        this.dsErr = dsErr;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_CREAZIONE")
    public Date getDtCreazione() {
        return this.dtCreazione;
    }

    public void setDtCreazione(Date dtCreazione) {
        this.dtCreazione = dtCreazione;
    }

    @Column(name = "NUMERO")
    public String getNumero() {
        return this.numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    @Column(name = "OGGETTO")
    public String getOggetto() {
        return this.oggetto;
    }

    public void setOggetto(String oggetto) {
        this.oggetto = oggetto;
    }

    @Column(name = "TI_STATO")
    @Enumerated(EnumType.STRING)
    public TiStato getTiStato() {
        return this.tiStato;
    }

    public void setTiStato(TiStato tiStato) {
        this.tiStato = tiStato;
    }

    @Column(name = "ANNO_AG")
    public BigDecimal getAnnoAg() {
        return annoAg;
    }

    public void setAnnoAg(BigDecimal annoAg) {
        this.annoAg = annoAg;
    }

    @Column(name = "NUMERO_AG")
    public String getNumeroAg() {
        return numeroAg;
    }

    public void setNumeroAg(String numeroAg) {
        this.numeroAg = numeroAg;
    }

    @Column(name = "REGISTRO_AG")
    public String getRegistroAg() {
        return registroAg;
    }

    public void setRegistroAg(String registroAg) {
        this.registroAg = registroAg;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DATA_AG")
    public Date getDataAg() {
        return dataAg;
    }

    public void setDataAg(Date dataAg) {
        this.dataAg = dataAg;
    }

    @Column(name = "FL_INVIATO_A_ENTE")
    public String getFlInviatoAEnte() {
        return flInviatoAEnte;
    }

    public void setFlInviatoAEnte(String flInviatoAEnte) {
        this.flInviatoAEnte = flInviatoAEnte;
    }

    @Column(name = "FL_INTERVENTO_SOGGETTO_A_TUTELA")
    public String getFlInterventoSoggettoATutela() {
        return flInterventoSoggettoATutela;
    }

    public void setFlInterventoSoggettoATutela(String flInterventoSoggettoATutela) {
        this.flInterventoSoggettoATutela = flInterventoSoggettoATutela;
    }

    @Column(name = "CLASSIFICA")
    public String getClassifica() {
        return classifica;
    }

    public void setClassifica(String classifica) {
        this.classifica = classifica;
    }

    @Column(name = "ID_FASCICOLO")
    public String getIdFascicolo() {
        return idFascicolo;
    }

    public void setIdFascicolo(String idFascicolo) {
        this.idFascicolo = idFascicolo;
    }

    @Column(name = "OGGETTO_FASCICOLO")
    public String getOggettoFascicolo() {
        return oggettoFascicolo;
    }

    public void setOggettoFascicolo(String oggettoFascicolo) {
        this.oggettoFascicolo = oggettoFascicolo;
    }

    @Column(name = "ID_SOTTOFASCICOLO")
    public String getIdSottofascicolo() {
        return idSottofascicolo;
    }

    public void setIdSottofascicolo(String idSottofascicolo) {
        this.idSottofascicolo = idSottofascicolo;
    }

    @Column(name = "OGGETTO_SOTTOFASCICOLO")
    public String getOggettoSottofascicolo() {
        return oggettoSottofascicolo;
    }

    public void setOggettoSottofascicolo(String oggettoSottofascicolo) {
        this.oggettoSottofascicolo = oggettoSottofascicolo;
    }

    @Column(name = "CLASSIFICA_AG")
    public String getClassificaAg() {
        return classificaAg;
    }

    public void setClassificaAg(String classificaAg) {
        this.classificaAg = classificaAg;
    }

    @Column(name = "ID_FASCICOLO_AG")
    public String getIdFascicoloAg() {
        return idFascicoloAg;
    }

    public void setIdFascicoloAg(String idFascicoloAg) {
        this.idFascicoloAg = idFascicoloAg;
    }

    @Column(name = "OGGETTO_FASCICOLO_AG")
    public String getOggettoFascicoloAg() {
        return oggettoFascicoloAg;
    }

    public void setOggettoFascicoloAg(String oggettoFascicoloAg) {
        this.oggettoFascicoloAg = oggettoFascicoloAg;
    }

    @Column(name = "ID_SOTTOFASCICOLO_AG")
    public String getIdSottofascicoloAg() {
        return idSottofascicoloAg;
    }

    public void setIdSottofascicoloAg(String idSottofascicoloAg) {
        this.idSottofascicoloAg = idSottofascicoloAg;
    }

    @Column(name = "OGGETTO_SOTTOFASCICOLO_AG")
    public String getOggettoSottofascicoloAg() {
        return oggettoSottofascicoloAg;
    }

    public void setOggettoSottofascicoloAg(String oggettoSottofascicoloAg) {
        this.oggettoSottofascicoloAg = oggettoSottofascicoloAg;
    }

    // bi-directional many-to-one association to IamUser
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_USER_IAM")
    public IamUser getIamUser() {
        return this.iamUser;
    }

    public void setIamUser(IamUser iamUser) {
        this.iamUser = iamUser;
    }

    // bi-directional many-to-one association to PigVer
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_VERS")
    public PigVers getPigVer() {
        return this.pigVer;
    }

    public void setPigVer(PigVers pigVer) {
        this.pigVer = pigVer;
    }

    // bi-directional many-to-one association to PigVerAg
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_VERS_AG")
    public PigVers getPigVerAg() {
        return this.pigVerAg;
    }

    public void setPigVerAg(PigVers pigVerAg) {
        this.pigVerAg = pigVerAg;
    }

    // bi-directional many-to-one association to PigVer
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_SISMA_VAL_ATTO")
    public PigSismaValAtto getPigSismaValAtto() {
        return pigSismaValAtto;
    }

    public void setPigSismaValAtto(PigSismaValAtto pigSismaValAtto) {
        this.pigSismaValAtto = pigSismaValAtto;
    }

    // bi-directional many-to-one association to PigSismaDocumenti
    @OneToMany(mappedBy = "pigSisma", cascade = CascadeType.REMOVE)
    public List<PigSismaDocumenti> getPigSismaDocumentis() {
        return this.pigSismaDocumentis;
    }

    public void setPigSismaDocumentis(List<PigSismaDocumenti> pigSismaDocumentis) {
        this.pigSismaDocumentis = pigSismaDocumentis;
    }

    public PigSismaDocumenti addPigSismaDocumenti(PigSismaDocumenti pigSismaDocumenti) {
        getPigSismaDocumentis().add(pigSismaDocumenti);
        pigSismaDocumenti.setPigSisma(this);

        return pigSismaDocumenti;
    }

    public PigSismaDocumenti removePigSismaDocumenti(PigSismaDocumenti pigSismaDocumenti) {
        getPigSismaDocumentis().remove(pigSismaDocumenti);
        pigSismaDocumenti.setPigSisma(null);

        return pigSismaDocumenti;
    }

    // bi-directional many-to-one association to PigSismaProgettiAg
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_SISMA_PROGETTI_AG")
    public PigSismaProgettiAg getPigSismaProgettiAg() {
        return pigSismaProgettiAg;
    }

    public void setPigSismaProgettiAg(PigSismaProgettiAg pigSismaProgettiAg) {
        this.pigSismaProgettiAg = pigSismaProgettiAg;
    }

    // bi-directional many-to-one association to PigSismaFaseProgetto
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ID_SISMA_FASE_PROGETTO")
    public PigSismaFaseProgetto getPigSismaFaseProgetto() {
        return pigSismaFaseProgetto;
    }

    public void setPigSismaFaseProgetto(PigSismaFaseProgetto pigSismaFaseProgetto) {
        this.pigSismaFaseProgetto = pigSismaFaseProgetto;
    }

    // bi-directional many-to-one association to PigSismaStatoProgetto
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_SISMA_STATO_PROGETTO")
    public PigSismaStatoProgetto getPigSismaStatoProgetto() {
        return pigSismaStatoProgetto;
    }

    public void setPigSismaStatoProgetto(PigSismaStatoProgetto pigSismaStatoProgetto) {
        this.pigSismaStatoProgetto = pigSismaStatoProgetto;
    }

}
