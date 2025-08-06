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
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.id.enhanced.SequenceStyleGenerator;

/**
 *
 * @author Cappelli_F
 */
@Entity
@Table(name = "PIG_SISMA_STORICO_STATI")
@NamedQuery(name = "PigSismaStoricoStati.findAll", query = "SELECT p FROM PigSismaStoricoStati p")
public class PigSismaStoricoStati implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long idStato;
    private String tiStato;
    private Date tsRegStato;
    private String descrizione;
    private PigSisma pigSisma;

    public PigSismaStoricoStati() {
	// non usato
    }

    @Id
    @GenericGenerator(name = "PIG_SISMA_STORICO_STATI_IDSTATO_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
	    @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SPIG_STRUM_URB_STORICO_STATI"),
	    @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PIG_SISMA_STORICO_STATI_IDSTATO_GENERATOR")
    @Column(name = "ID_STATO")
    public Long getIdStato() {
	return idStato;
    }

    public void setIdStato(Long idStato) {
	this.idStato = idStato;
    }

    @Column(name = "TI_STATO")
    public String getTiStato() {
	return tiStato;
    }

    public void setTiStato(String tiStato) {
	this.tiStato = tiStato;
    }

    @Column(name = "TS_REG_STATO")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getTsRegStato() {
	return tsRegStato;
    }

    public void setTsRegStato(Date tsRegStato) {
	this.tsRegStato = tsRegStato;
    }

    @Column(name = "CD_DESC")
    public String getDescrizione() {
	return descrizione;
    }

    public void setDescrizione(String descrizione) {
	this.descrizione = descrizione;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_SISMA")
    public PigSisma getPigSisma() {
	return pigSisma;
    }

    public void setPigSisma(PigSisma pigSisma) {
	this.pigSisma = pigSisma;
    }

}
