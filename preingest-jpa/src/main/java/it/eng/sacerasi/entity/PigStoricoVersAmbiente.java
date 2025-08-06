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
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.id.enhanced.SequenceStyleGenerator;

/**
 * The persistent class for the PIG_STORICO_VERS_AMBIENTE database table.
 *
 */
@Entity
@Table(name = "PIG_STORICO_VERS_AMBIENTE")
public class PigStoricoVersAmbiente implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long idStoricoVersAmbiente;
    private Date dtFineVal;
    private Date dtIniVal;
    private PigAmbienteVers pigAmbienteVer;
    private PigVers pigVer;

    public PigStoricoVersAmbiente() {
	// for Hibernate
    }

    @Id
    @GenericGenerator(name = "PIG_STORICO_VERS_AMBIENTE_IDSTORICOVERSAMBIENTE_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
	    @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SPIG_STORICO_VERS_AMBIENTE"),
	    @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PIG_STORICO_VERS_AMBIENTE_IDSTORICOVERSAMBIENTE_GENERATOR")
    @Column(name = "ID_STORICO_VERS_AMBIENTE")
    public Long getIdStoricoVersAmbiente() {
	return this.idStoricoVersAmbiente;
    }

    public void setIdStoricoVersAmbiente(Long idStoricoVersAmbiente) {
	this.idStoricoVersAmbiente = idStoricoVersAmbiente;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_FINE_VAL")
    public Date getDtFineVal() {
	return this.dtFineVal;
    }

    public void setDtFineVal(Date dtFineVal) {
	this.dtFineVal = dtFineVal;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_INI_VAL")
    public Date getDtIniVal() {
	return this.dtIniVal;
    }

    public void setDtIniVal(Date dtIniVal) {
	this.dtIniVal = dtIniVal;
    }

    // bi-directional many-to-one association to PigAmbienteVer
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_AMBIENTE_VERS")
    public PigAmbienteVers getPigAmbienteVer() {
	return this.pigAmbienteVer;
    }

    public void setPigAmbienteVer(PigAmbienteVers pigAmbienteVer) {
	this.pigAmbienteVer = pigAmbienteVer;
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

}
