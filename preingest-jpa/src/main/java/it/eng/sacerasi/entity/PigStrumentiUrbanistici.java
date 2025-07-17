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
	BOZZA, ERRORE, RICHIESTA_INVIO, INVIO_IN_CORSO, IN_ELABORAZIONE, IN_TRASFORMAZIONE,
	IN_VERSAMENTO, VERSATO, ANNULLATO
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

}
