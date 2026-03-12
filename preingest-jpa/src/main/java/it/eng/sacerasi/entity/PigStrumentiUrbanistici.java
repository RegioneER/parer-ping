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

package it.eng.sacerasi.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.id.enhanced.SequenceStyleGenerator;

/**
 * The persistent class for the PIG_STRUMENTI_URBANISTICI database table.
 *
 */
@Entity
@Table(name = "PIG_STRUMENTI_URBANISTICI")
public class PigStrumentiUrbanistici implements Serializable {

    public enum TiStato {
        BOZZA, ERRORE, RICHIESTA_INVIO, INVIO_IN_CORSO, IN_ELABORAZIONE_ENTE,
        IN_TRASFORMAZIONE_ENTE, IN_VERSAMENTO_ENTE, IN_ELABORAZIONE, IN_TRASFORMAZIONE,
        IN_VERSAMENTO, VERSATO, ANNULLATO, COMPLETATO
    }

    private static final long serialVersionUID = 1L;
    private Long idStrumentiUrbanistici;
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
    private IamUser iamUser;
    private PigStrumUrbPianoStato pigStrumUrbPianoStato;
    private PigVers pigVer;
    private List<PigStrumUrbCollegamenti> pigStrumUrbCollegamentis = new ArrayList<>();
    private List<PigStrumUrbDocumenti> pigStrumUrbDocumentis = new ArrayList<>();

    // MEV 30026
    private Long idPuc;
    private String nrBurert;
    private Date dtBurert;
    private String cdRepertorio;
    private BigDecimal annoProtocollo;
    private String cdProtocollo;
    private Date dtProtocollo;
    private String flInviatoAEnte;

    // MEV 40123
    private String classificaUrb;
    private String idFascicoloUrb;
    private String oggettoFascicoloUrb;
    private String idSottofascicoloUrb;
    private String oggettoSottofascicoloUrb;

    public PigStrumentiUrbanistici() {
        // for Hibernate
    }

    @Id
    @GenericGenerator(name = "PIG_STRUMENTI_URBANISTICI_IDSTRUMENTIURBANISTICI_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
            @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SPIG_STRUMENTI_URBANISTICI"),
            @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PIG_STRUMENTI_URBANISTICI_IDSTRUMENTIURBANISTICI_GENERATOR")
    @Column(name = "ID_STRUMENTI_URBANISTICI")
    public Long getIdStrumentiUrbanistici() {
        return this.idStrumentiUrbanistici;
    }

    public void setIdStrumentiUrbanistici(Long idStrumentiUrbanistici) {
        this.idStrumentiUrbanistici = idStrumentiUrbanistici;
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

    public String getNumero() {
        return this.numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

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

    // bi-directional many-to-one association to IamUser
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_USER_IAM")
    public IamUser getIamUser() {
        return this.iamUser;
    }

    public void setIamUser(IamUser iamUser) {
        this.iamUser = iamUser;
    }

    // bi-directional many-to-one association to PigStrumUrbPianoStato
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_STRUM_URB_PIANO_STATO")
    public PigStrumUrbPianoStato getPigStrumUrbPianoStato() {
        return this.pigStrumUrbPianoStato;
    }

    public void setPigStrumUrbPianoStato(PigStrumUrbPianoStato pigStrumUrbPianoStato) {
        this.pigStrumUrbPianoStato = pigStrumUrbPianoStato;
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

    // bi-directional many-to-one association to PigStrumUrbCollegamenti
    @OneToMany(mappedBy = "pigStrumentiUrbanistici")
    public List<PigStrumUrbCollegamenti> getPigStrumUrbCollegamentis() {
        return this.pigStrumUrbCollegamentis;
    }

    public void setPigStrumUrbCollegamentis(
            List<PigStrumUrbCollegamenti> pigStrumUrbCollegamentis) {
        this.pigStrumUrbCollegamentis = pigStrumUrbCollegamentis;
    }

    public PigStrumUrbCollegamenti addPigStrumUrbCollegamenti(
            PigStrumUrbCollegamenti pigStrumUrbCollegamenti) {
        getPigStrumUrbCollegamentis().add(pigStrumUrbCollegamenti);
        pigStrumUrbCollegamenti.setPigStrumentiUrbanistici(this);

        return pigStrumUrbCollegamenti;
    }

    public PigStrumUrbCollegamenti removePigStrumUrbCollegamenti(
            PigStrumUrbCollegamenti pigStrumUrbCollegamenti) {
        getPigStrumUrbCollegamentis().remove(pigStrumUrbCollegamenti);
        pigStrumUrbCollegamenti.setPigStrumentiUrbanistici(null);

        return pigStrumUrbCollegamenti;
    }

    // bi-directional many-to-one association to PigStrumUrbDocumenti
    @OneToMany(mappedBy = "pigStrumentiUrbanistici", cascade = CascadeType.REMOVE)
    public List<PigStrumUrbDocumenti> getPigStrumUrbDocumentis() {
        return this.pigStrumUrbDocumentis;
    }

    public void setPigStrumUrbDocumentis(List<PigStrumUrbDocumenti> pigStrumUrbDocumentis) {
        this.pigStrumUrbDocumentis = pigStrumUrbDocumentis;
    }

    public PigStrumUrbDocumenti addPigStrumUrbDocumenti(PigStrumUrbDocumenti pigStrumUrbDocumenti) {
        getPigStrumUrbDocumentis().add(pigStrumUrbDocumenti);
        pigStrumUrbDocumenti.setPigStrumentiUrbanistici(this);

        return pigStrumUrbDocumenti;
    }

    public PigStrumUrbDocumenti removePigStrumUrbDocumenti(
            PigStrumUrbDocumenti pigStrumUrbDocumenti) {
        getPigStrumUrbDocumentis().remove(pigStrumUrbDocumenti);
        pigStrumUrbDocumenti.setPigStrumentiUrbanistici(null);

        return pigStrumUrbDocumenti;
    }

    @Column(name = "ID_PUC")
    public Long getIdPuc() {
        return idPuc;
    }

    public void setIdPuc(Long idPuc) {
        this.idPuc = idPuc;
    }

    @Column(name = "NR_BURERT")
    public String getNrBurert() {
        return nrBurert;
    }

    public void setNrBurert(String nrBurert) {
        this.nrBurert = nrBurert;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DATA_BURERT")
    public Date getDtBurert() {
        return dtBurert;
    }

    public void setDtBurert(Date dtBurert) {
        this.dtBurert = dtBurert;
    }

    @Column(name = "REPERTORIO")
    public String getCdRepertorio() {
        return cdRepertorio;
    }

    public void setCdRepertorio(String cdRepertorio) {
        this.cdRepertorio = cdRepertorio;
    }

    @Column(name = "ANNO_PROTOCOLLO")
    public BigDecimal getAnnoProtocollo() {
        return annoProtocollo;
    }

    public void setAnnoProtocollo(BigDecimal annoProtocollo) {
        this.annoProtocollo = annoProtocollo;
    }

    @Column(name = "NR_PROTOCOLLO")
    public String getCdProtocollo() {
        return cdProtocollo;
    }

    public void setCdProtocollo(String cdProtocollo) {
        this.cdProtocollo = cdProtocollo;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DATA_PROTOCOLLO")
    public Date getDtProtocollo() {
        return dtProtocollo;
    }

    public void setDtProtocollo(Date dtProtocollo) {
        this.dtProtocollo = dtProtocollo;
    }

    @Column(name = "FL_INVIATO_A_ENTE")
    public String getFlInviatoAEnte() {
        return flInviatoAEnte;
    }

    public void setFlInviatoAEnte(String flInviatoAEnte) {
        this.flInviatoAEnte = flInviatoAEnte;
    }

    @Column(name = "CLASSIFICA_URB")
    public String getClassificaUrb() {
        return classificaUrb;
    }

    public void setClassificaUrb(String classificaUrb) {
        this.classificaUrb = classificaUrb;
    }

    @Column(name = "ID_FASCICOLO_URB")
    public String getIdFascicoloUrb() {
        return idFascicoloUrb;
    }

    public void setIdFascicoloUrb(String idFascicoloUrb) {
        this.idFascicoloUrb = idFascicoloUrb;
    }

    @Column(name = "OGGETTO_FASCICOLO_URB")
    public String getOggettoFascicoloUrb() {
        return oggettoFascicoloUrb;
    }

    public void setOggettoFascicoloUrb(String oggettoFascicoloUrb) {
        this.oggettoFascicoloUrb = oggettoFascicoloUrb;
    }

    @Column(name = "ID_SOTTOFASCICOLO_URB")
    public String getIdSottofascicoloUrb() {
        return idSottofascicoloUrb;
    }

    public void setIdSottofascicoloUrb(String idSottofascicoloUrb) {
        this.idSottofascicoloUrb = idSottofascicoloUrb;
    }

    @Column(name = "OGGETTO_SOTTOFASCICOLO_URB")
    public String getOggettoSottofascicoloUrb() {
        return oggettoSottofascicoloUrb;
    }

    public void setOggettoSottofascicoloUrb(String oggettoSottofascicoloUrb) {
        this.oggettoSottofascicoloUrb = oggettoSottofascicoloUrb;
    }
}
