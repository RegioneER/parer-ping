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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.id.enhanced.SequenceStyleGenerator;

/**
 * The persistent class for the PIG_AMBIENTE_VERS database table.
 *
 */
@Entity
@Table(name = "PIG_AMBIENTE_VERS")
public class PigAmbienteVers implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long idAmbienteVers;
    private String dsAmbienteVers;
    private String dsNote;
    private Date dtFineVal;
    private Date dtIniVal;
    private BigDecimal idEnteConserv;
    private BigDecimal idEnteGestore;
    private String nmAmbienteVers;
    private List<PigVers> pigVers = new ArrayList<>();

    public PigAmbienteVers() {
	// for Hibernate
    }

    @Id
    @GenericGenerator(name = "PIG_AMBIENTE_VERS_IDAMBIENTEVERS_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
	    @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SPIG_AMBIENTE_VERS"),
	    @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PIG_AMBIENTE_VERS_IDAMBIENTEVERS_GENERATOR")
    @Column(name = "ID_AMBIENTE_VERS")
    public Long getIdAmbienteVers() {
	return this.idAmbienteVers;
    }

    public void setIdAmbienteVers(Long idAmbienteVers) {
	this.idAmbienteVers = idAmbienteVers;
    }

    @Column(name = "DS_AMBIENTE_VERS")
    public String getDsAmbienteVers() {
	return this.dsAmbienteVers;
    }

    public void setDsAmbienteVers(String dsAmbienteVers) {
	this.dsAmbienteVers = dsAmbienteVers;
    }

    @Column(name = "DS_NOTE")
    public String getDsNote() {
	return this.dsNote;
    }

    public void setDsNote(String dsNote) {
	this.dsNote = dsNote;
    }

    @Temporal(TemporalType.DATE)
    @Column(name = "DT_FINE_VAL")
    public Date getDtFineVal() {
	return this.dtFineVal;
    }

    public void setDtFineVal(Date dtFineVal) {
	this.dtFineVal = dtFineVal;
    }

    @Temporal(TemporalType.DATE)
    @Column(name = "DT_INI_VAL")
    public Date getDtIniVal() {
	return this.dtIniVal;
    }

    public void setDtIniVal(Date dtIniVal) {
	this.dtIniVal = dtIniVal;
    }

    @Column(name = "ID_ENTE_CONSERV")
    public BigDecimal getIdEnteConserv() {
	return this.idEnteConserv;
    }

    public void setIdEnteConserv(BigDecimal idEnteConserv) {
	this.idEnteConserv = idEnteConserv;
    }

    @Column(name = "ID_ENTE_GESTORE")
    public BigDecimal getIdEnteGestore() {
	return this.idEnteGestore;
    }

    public void setIdEnteGestore(BigDecimal idEnteGestore) {
	this.idEnteGestore = idEnteGestore;
    }

    @Column(name = "NM_AMBIENTE_VERS")
    public String getNmAmbienteVers() {
	return this.nmAmbienteVers;
    }

    public void setNmAmbienteVers(String nmAmbienteVers) {
	this.nmAmbienteVers = nmAmbienteVers;
    }

    // bi-directional many-to-one association to PigVer
    @OneToMany(mappedBy = "pigAmbienteVer")
    public List<PigVers> getPigVers() {
	return this.pigVers;
    }

    public void setPigVers(List<PigVers> pigVers) {
	this.pigVers = pigVers;
    }

}
