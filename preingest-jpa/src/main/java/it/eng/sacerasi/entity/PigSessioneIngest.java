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

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.id.enhanced.SequenceStyleGenerator;

/**
 * The persistent class for the PIG_SESSIONE_INGEST database table.
 *
 */
@NamedStoredProcedureQuery(name = "aggiornaContatori", procedureName = "AGGIORNA_CONTATORI", parameters = {
        @StoredProcedureParameter(name = "IDSESSIONEINGEST", type = Long.class, mode = ParameterMode.IN),
        @StoredProcedureParameter(name = "INCREMENTAVERSATEOK", type = Integer.class, mode = ParameterMode.IN),
        @StoredProcedureParameter(name = "INCREMENTAVERSATEERRORE", type = Integer.class, mode = ParameterMode.IN),
        @StoredProcedureParameter(name = "INCREMENTAVERSATETIMEOUT", type = Integer.class, mode = ParameterMode.IN) })
@Entity
@Table(name = "PIG_SESSIONE_INGEST")
public class PigSessioneIngest implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long idSessioneIngest;
    private String cdErr;
    private String cdKeyObject;
    private String cdKeyObjectPadre;
    private String cdTrasf;
    private String cdVersGen;
    private String cdVersioneTrasf;
    private String cdVersioneXmlVers;
    private String dlErr;
    private String dlMotivoChiusoWarning;
    private String dlMotivoForzaAccettazione;
    private String dsObject;
    private Date dtApertura;
    private Date dtChiusura;
    private String flFileCifrato;
    private String flForzaAccettazione;
    private String flForzaWarning;
    private String flSesErrNonRisolub;
    private String flSesErrVerif;
    private String note;
    private BigDecimal idStatoSessioneIngestCor;
    private BigDecimal niTotObjectTrasf;
    private BigDecimal niUnitaDocAttese;
    private BigDecimal niUnitaDocDaVers;
    private BigDecimal niUnitaDocVers;
    private BigDecimal niUnitaDocVersErr;
    private BigDecimal niUnitaDocVersOk;
    private BigDecimal niUnitaDocVersTimeout;
    private String nmAmbienteVers;
    private String nmAmbienteVersPadre;
    private String nmTipoObject;
    private String nmVers;
    private String nmVersPadre;
    private BigDecimal pgOggettoTrasf;
    private String tiGestOggettiFigli;
    private String tiStato;
    private String tiStatoVerificaHash;
    private PigObject pigObject;
    private PigVers pigVer;
    private List<PigStatoSessioneIngest> pigStatoSessioneIngests = new ArrayList<>();
    private List<PigUnitaDocSessione> pigUnitaDocSessiones = new ArrayList<>();
    private List<PigXmlSessioneIngest> pigXmlSessioneIngests = new ArrayList<>();
    private List<PigXmlAnnulSessioneIngest> pigXmlAnnulSessioneIngests = new ArrayList<>();
    private String nmReportTrasfOS;

    public PigSessioneIngest() {
        // for Hibernate
    }

    @Id
    @GenericGenerator(name = "PIG_SESSIONE_INGEST_IDSESSIONEINGEST_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
            @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SPIG_SESSIONE_INGEST"),
            @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PIG_SESSIONE_INGEST_IDSESSIONEINGEST_GENERATOR")
    @Column(name = "ID_SESSIONE_INGEST")
    public Long getIdSessioneIngest() {
        return this.idSessioneIngest;
    }

    public void setIdSessioneIngest(Long idSessioneIngest) {
        this.idSessioneIngest = idSessioneIngest;
    }

    @Column(name = "CD_ERR")
    public String getCdErr() {
        return this.cdErr;
    }

    public void setCdErr(String cdErr) {
        this.cdErr = cdErr;
    }

    @Column(name = "CD_KEY_OBJECT")
    public String getCdKeyObject() {
        return this.cdKeyObject;
    }

    public void setCdKeyObject(String cdKeyObject) {
        this.cdKeyObject = cdKeyObject;
    }

    @Column(name = "CD_KEY_OBJECT_PADRE")
    public String getCdKeyObjectPadre() {
        return this.cdKeyObjectPadre;
    }

    public void setCdKeyObjectPadre(String cdKeyObjectPadre) {
        this.cdKeyObjectPadre = cdKeyObjectPadre;
    }

    @Column(name = "CD_TRASF")
    public String getCdTrasf() {
        return this.cdTrasf;
    }

    public void setCdTrasf(String cdTrasf) {
        this.cdTrasf = cdTrasf;
    }

    @Column(name = "NOTE")
    public String getNote() {
        return this.note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @Column(name = "CD_VERS_GEN")
    public String getCdVersGen() {
        return this.cdVersGen;
    }

    public void setCdVersGen(String cdVersGen) {
        this.cdVersGen = cdVersGen;
    }

    @Column(name = "CD_VERSIONE_TRASF")
    public String getCdVersioneTrasf() {
        return this.cdVersioneTrasf;
    }

    public void setCdVersioneTrasf(String cdVersioneTrasf) {
        this.cdVersioneTrasf = cdVersioneTrasf;
    }

    @Column(name = "CD_VERSIONE_XML_VERS")
    public String getCdVersioneXmlVers() {
        return this.cdVersioneXmlVers;
    }

    public void setCdVersioneXmlVers(String cdVersioneXmlVers) {
        this.cdVersioneXmlVers = cdVersioneXmlVers;
    }

    @Column(name = "DL_ERR")
    public String getDlErr() {
        return this.dlErr;
    }

    public void setDlErr(String dlErr) {
        this.dlErr = dlErr;
    }

    @Column(name = "DL_MOTIVO_CHIUSO_WARNING")
    public String getDlMotivoChiusoWarning() {
        return this.dlMotivoChiusoWarning;
    }

    public void setDlMotivoChiusoWarning(String dlMotivoChiusoWarning) {
        this.dlMotivoChiusoWarning = dlMotivoChiusoWarning;
    }

    @Column(name = "DL_MOTIVO_FORZA_ACCETTAZIONE")
    public String getDlMotivoForzaAccettazione() {
        return this.dlMotivoForzaAccettazione;
    }

    public void setDlMotivoForzaAccettazione(String dlMotivoForzaAccettazione) {
        this.dlMotivoForzaAccettazione = dlMotivoForzaAccettazione;
    }

    @Column(name = "DS_OBJECT")
    public String getDsObject() {
        return this.dsObject;
    }

    public void setDsObject(String dsObject) {
        this.dsObject = dsObject;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_APERTURA")
    public Date getDtApertura() {
        return this.dtApertura;
    }

    public void setDtApertura(Date dtApertura) {
        this.dtApertura = dtApertura;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_CHIUSURA")
    public Date getDtChiusura() {
        return this.dtChiusura;
    }

    public void setDtChiusura(Date dtChiusura) {
        this.dtChiusura = dtChiusura;
    }

    @Column(name = "FL_FILE_CIFRATO", columnDefinition = "char")
    public String getFlFileCifrato() {
        return this.flFileCifrato;
    }

    public void setFlFileCifrato(String flFileCifrato) {
        this.flFileCifrato = flFileCifrato;
    }

    @Column(name = "FL_FORZA_ACCETTAZIONE", columnDefinition = "char")
    public String getFlForzaAccettazione() {
        return this.flForzaAccettazione;
    }

    public void setFlForzaAccettazione(String flForzaAccettazione) {
        this.flForzaAccettazione = flForzaAccettazione;
    }

    @Column(name = "FL_FORZA_WARNING", columnDefinition = "char")
    public String getFlForzaWarning() {
        return this.flForzaWarning;
    }

    public void setFlForzaWarning(String flForzaWarning) {
        this.flForzaWarning = flForzaWarning;
    }

    @Column(name = "FL_SES_ERR_NON_RISOLUB", columnDefinition = "char")
    public String getFlSesErrNonRisolub() {
        return this.flSesErrNonRisolub;
    }

    public void setFlSesErrNonRisolub(String flSesErrNonRisolub) {
        this.flSesErrNonRisolub = flSesErrNonRisolub;
    }

    @Column(name = "FL_SES_ERR_VERIF", columnDefinition = "char")
    public String getFlSesErrVerif() {
        return this.flSesErrVerif;
    }

    public void setFlSesErrVerif(String flSesErrVerif) {
        this.flSesErrVerif = flSesErrVerif;
    }

    @Column(name = "ID_STATO_SESSIONE_INGEST_COR")
    public BigDecimal getIdStatoSessioneIngestCor() {
        return this.idStatoSessioneIngestCor;
    }

    public void setIdStatoSessioneIngestCor(BigDecimal idStatoSessioneIngestCor) {
        this.idStatoSessioneIngestCor = idStatoSessioneIngestCor;
    }

    @Column(name = "NI_TOT_OBJECT_TRASF")
    public BigDecimal getNiTotObjectTrasf() {
        return this.niTotObjectTrasf;
    }

    public void setNiTotObjectTrasf(BigDecimal niTotObjectTrasf) {
        this.niTotObjectTrasf = niTotObjectTrasf;
    }

    @Column(name = "NI_UNITA_DOC_ATTESE")
    public BigDecimal getNiUnitaDocAttese() {
        return this.niUnitaDocAttese;
    }

    public void setNiUnitaDocAttese(BigDecimal niUnitaDocAttese) {
        this.niUnitaDocAttese = niUnitaDocAttese;
    }

    @Column(name = "NI_UNITA_DOC_DA_VERS")
    public BigDecimal getNiUnitaDocDaVers() {
        return this.niUnitaDocDaVers;
    }

    public void setNiUnitaDocDaVers(BigDecimal niUnitaDocDaVers) {
        this.niUnitaDocDaVers = niUnitaDocDaVers;
    }

    @Column(name = "NI_UNITA_DOC_VERS")
    public BigDecimal getNiUnitaDocVers() {
        return this.niUnitaDocVers;
    }

    public void setNiUnitaDocVers(BigDecimal niUnitaDocVers) {
        this.niUnitaDocVers = niUnitaDocVers;
    }

    @Column(name = "NI_UNITA_DOC_VERS_ERR")
    public BigDecimal getNiUnitaDocVersErr() {
        return this.niUnitaDocVersErr;
    }

    public void setNiUnitaDocVersErr(BigDecimal niUnitaDocVersErr) {
        this.niUnitaDocVersErr = niUnitaDocVersErr;
    }

    @Column(name = "NI_UNITA_DOC_VERS_OK")
    public BigDecimal getNiUnitaDocVersOk() {
        return this.niUnitaDocVersOk;
    }

    public void setNiUnitaDocVersOk(BigDecimal niUnitaDocVersOk) {
        this.niUnitaDocVersOk = niUnitaDocVersOk;
    }

    @Column(name = "NI_UNITA_DOC_VERS_TIMEOUT")
    public BigDecimal getNiUnitaDocVersTimeout() {
        return this.niUnitaDocVersTimeout;
    }

    public void setNiUnitaDocVersTimeout(BigDecimal niUnitaDocVersTimeout) {
        this.niUnitaDocVersTimeout = niUnitaDocVersTimeout;
    }

    @Column(name = "NM_AMBIENTE_VERS")
    public String getNmAmbienteVers() {
        return this.nmAmbienteVers;
    }

    public void setNmAmbienteVers(String nmAmbienteVers) {
        this.nmAmbienteVers = nmAmbienteVers;
    }

    @Column(name = "NM_AMBIENTE_VERS_PADRE")
    public String getNmAmbienteVersPadre() {
        return this.nmAmbienteVersPadre;
    }

    public void setNmAmbienteVersPadre(String nmAmbienteVersPadre) {
        this.nmAmbienteVersPadre = nmAmbienteVersPadre;
    }

    @Column(name = "NM_TIPO_OBJECT")
    public String getNmTipoObject() {
        return this.nmTipoObject;
    }

    public void setNmTipoObject(String nmTipoObject) {
        this.nmTipoObject = nmTipoObject;
    }

    @Column(name = "NM_VERS")
    public String getNmVers() {
        return this.nmVers;
    }

    public void setNmVers(String nmVers) {
        this.nmVers = nmVers;
    }

    @Column(name = "NM_VERS_PADRE")
    public String getNmVersPadre() {
        return this.nmVersPadre;
    }

    public void setNmVersPadre(String nmVersPadre) {
        this.nmVersPadre = nmVersPadre;
    }

    @Column(name = "PG_OGGETTO_TRASF")
    public BigDecimal getPgOggettoTrasf() {
        return this.pgOggettoTrasf;
    }

    public void setPgOggettoTrasf(BigDecimal pgOggettoTrasf) {
        this.pgOggettoTrasf = pgOggettoTrasf;
    }

    @Column(name = "TI_GEST_OGGETTI_FIGLI")
    public String getTiGestOggettiFigli() {
        return tiGestOggettiFigli;
    }

    public void setTiGestOggettiFigli(String tiGestOggettiFigli) {
        this.tiGestOggettiFigli = tiGestOggettiFigli;
    }

    @Column(name = "TI_STATO")
    public String getTiStato() {
        return this.tiStato;
    }

    public void setTiStato(String tiStato) {
        this.tiStato = tiStato;
    }

    @Column(name = "TI_STATO_VERIFICA_HASH")
    public String getTiStatoVerificaHash() {
        return this.tiStatoVerificaHash;
    }

    public void setTiStatoVerificaHash(String tiStatoVerificaHash) {
        this.tiStatoVerificaHash = tiStatoVerificaHash;
    }

    // bi-directional many-to-one association to PigObject
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_OBJECT")
    public PigObject getPigObject() {
        return this.pigObject;
    }

    public void setPigObject(PigObject pigObject) {
        this.pigObject = pigObject;
    }

    // bi-directional many-to-one association to PigVers
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_VERS")
    public PigVers getPigVer() {
        return this.pigVer;
    }

    public void setPigVer(PigVers pigVer) {
        this.pigVer = pigVer;
    }

    @Column(name = "NM_REPORT_TRASF_OS")
    public String getNmReportTrasfOS() {
        return nmReportTrasfOS;
    }

    public void setNmReportTrasfOS(String NmReportTrasfOS) {
        this.nmReportTrasfOS = NmReportTrasfOS;
    }

    // bi-directional many-to-one association to PigStatoSessioneIngest
    @OneToMany(mappedBy = "pigSessioneIngest", cascade = { CascadeType.PERSIST })
    public List<PigStatoSessioneIngest> getPigStatoSessioneIngests() {
        return this.pigStatoSessioneIngests;
    }

    public void setPigStatoSessioneIngests(List<PigStatoSessioneIngest> pigStatoSessioneIngests) {
        this.pigStatoSessioneIngests = pigStatoSessioneIngests;
    }

    public PigStatoSessioneIngest addPigStatoSessioneIngest(PigStatoSessioneIngest pigStatoSessioneIngest) {
        getPigStatoSessioneIngests().add(pigStatoSessioneIngest);
        pigStatoSessioneIngest.setPigSessioneIngest(this);

        return pigStatoSessioneIngest;
    }

    public PigStatoSessioneIngest removePigStatoSessioneIngest(PigStatoSessioneIngest pigStatoSessioneIngest) {
        getPigStatoSessioneIngests().remove(pigStatoSessioneIngest);
        pigStatoSessioneIngest.setPigSessioneIngest(null);

        return pigStatoSessioneIngest;
    }

    // bi-directional many-to-one association to PigUnitaDocSessione
    @OneToMany(mappedBy = "pigSessioneIngest", cascade = { CascadeType.PERSIST })
    public List<PigUnitaDocSessione> getPigUnitaDocSessiones() {
        return this.pigUnitaDocSessiones;
    }

    public void setPigUnitaDocSessiones(List<PigUnitaDocSessione> pigUnitaDocSessiones) {
        this.pigUnitaDocSessiones = pigUnitaDocSessiones;
    }

    public PigUnitaDocSessione addPigUnitaDocSessione(PigUnitaDocSessione pigUnitaDocSessione) {
        getPigUnitaDocSessiones().add(pigUnitaDocSessione);
        pigUnitaDocSessione.setPigSessioneIngest(this);

        return pigUnitaDocSessione;
    }

    public PigUnitaDocSessione removePigUnitaDocSessione(PigUnitaDocSessione pigUnitaDocSessione) {
        getPigUnitaDocSessiones().remove(pigUnitaDocSessione);
        pigUnitaDocSessione.setPigSessioneIngest(null);

        return pigUnitaDocSessione;
    }

    // bi-directional many-to-one association to PigXmlSessioneIngest
    @OneToMany(mappedBy = "pigSessioneIngest", cascade = { CascadeType.PERSIST })
    public List<PigXmlSessioneIngest> getPigXmlSessioneIngests() {
        return this.pigXmlSessioneIngests;
    }

    public void setPigXmlSessioneIngests(List<PigXmlSessioneIngest> pigXmlSessioneIngests) {
        this.pigXmlSessioneIngests = pigXmlSessioneIngests;
    }

    public PigXmlSessioneIngest addPigXmlSessioneIngest(PigXmlSessioneIngest pigXmlSessioneIngest) {
        getPigXmlSessioneIngests().add(pigXmlSessioneIngest);
        pigXmlSessioneIngest.setPigSessioneIngest(this);

        return pigXmlSessioneIngest;
    }

    public PigXmlSessioneIngest removePigXmlSessioneIngest(PigXmlSessioneIngest pigXmlSessioneIngest) {
        getPigXmlSessioneIngests().remove(pigXmlSessioneIngest);
        pigXmlSessioneIngest.setPigSessioneIngest(null);

        return pigXmlSessioneIngest;
    }

    // bi-directional many-to-one association to PigXmlAnnulSessioneIngest
    @OneToMany(mappedBy = "pigSessioneIngest")
    public List<PigXmlAnnulSessioneIngest> getPigXmlAnnulSessioneIngests() {
        return this.pigXmlAnnulSessioneIngests;
    }

    public void setPigXmlAnnulSessioneIngests(List<PigXmlAnnulSessioneIngest> pigXmlAnnulSessioneIngests) {
        this.pigXmlAnnulSessioneIngests = pigXmlAnnulSessioneIngests;
    }

    public PigXmlAnnulSessioneIngest addPigXmlAnnulSessioneIngest(PigXmlAnnulSessioneIngest pigXmlAnnulSessioneIngest) {
        getPigXmlAnnulSessioneIngests().add(pigXmlAnnulSessioneIngest);
        pigXmlAnnulSessioneIngest.setPigSessioneIngest(this);

        return pigXmlAnnulSessioneIngest;
    }

    public PigXmlAnnulSessioneIngest removePigXmlAnnulSessioneIngest(
            PigXmlAnnulSessioneIngest pigXmlAnnulSessioneIngest) {
        getPigXmlAnnulSessioneIngests().remove(pigXmlAnnulSessioneIngest);
        pigXmlAnnulSessioneIngest.setPigSessioneIngest(null);

        return pigXmlAnnulSessioneIngest;
    }

}
