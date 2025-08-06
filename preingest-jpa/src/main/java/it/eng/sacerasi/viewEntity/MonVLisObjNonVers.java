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

package it.eng.sacerasi.viewEntity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * The persistent class for the MON_V_LIS_OBJ_NON_VERS database table.
 */
@Entity
@Table(name = "MON_V_LIS_OBJ_NON_VERS")
public class MonVLisObjNonVers implements Serializable {

    private static final long serialVersionUID = 1L;

    private String dsObject;

    private String flNonRisolub;

    private String flVerif;

    private String flVersSacerDaRecup;

    private BigDecimal idAmbienteVers;

    private String nmAmbienteVers;

    private String nmTipoObject;

    private String nmVers;

    private Date dtFirstSesErr;

    private Date dtLastSesErr;

    public MonVLisObjNonVers() {
    }

    @Column(name = "DS_OBJECT")
    public String getDsObject() {
	return this.dsObject;
    }

    public void setDsObject(String dsObject) {
	this.dsObject = dsObject;
    }

    @Column(name = "FL_NON_RISOLUB", columnDefinition = "char")
    public String getFlNonRisolub() {
	return this.flNonRisolub;
    }

    public void setFlNonRisolub(String flNonRisolub) {
	this.flNonRisolub = flNonRisolub;
    }

    @Column(name = "FL_VERIF", columnDefinition = "char")
    public String getFlVerif() {
	return this.flVerif;
    }

    public void setFlVerif(String flVerif) {
	this.flVerif = flVerif;
    }

    @Column(name = "FL_VERS_SACER_DA_RECUP", columnDefinition = "char")
    public String getFlVersSacerDaRecup() {
	return this.flVersSacerDaRecup;
    }

    public void setFlVersSacerDaRecup(String flVersSacerDaRecup) {
	this.flVersSacerDaRecup = flVersSacerDaRecup;
    }

    @Column(name = "ID_AMBIENTE_VERS")
    public BigDecimal getIdAmbienteVers() {
	return this.idAmbienteVers;
    }

    public void setIdAmbienteVers(BigDecimal idAmbienteVers) {
	this.idAmbienteVers = idAmbienteVers;
    }

    @Column(name = "NM_AMBIENTE_VERS")
    public String getNmAmbienteVers() {
	return this.nmAmbienteVers;
    }

    public void setNmAmbienteVers(String nmAmbienteVers) {
	this.nmAmbienteVers = nmAmbienteVers;
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

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_FIRST_SES_ERR")
    public Date getDtFirstSesErr() {
	return this.dtFirstSesErr;
    }

    public void setDtFirstSesErr(Date dtFirstSesErr) {
	this.dtFirstSesErr = dtFirstSesErr;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_LAST_SES_ERR")
    public Date getDtLastSesErr() {
	return this.dtLastSesErr;
    }

    public void setDtLastSesErr(Date dtLastSesErr) {
	this.dtLastSesErr = dtLastSesErr;
    }

    private MonVLisObjNonVersId monVLisObjNonVersId;

    @EmbeddedId()
    public MonVLisObjNonVersId getMonVLisObjNonVersId() {
	return monVLisObjNonVersId;
    }

    public void setMonVLisObjNonVersId(MonVLisObjNonVersId monVLisObjNonVersId) {
	this.monVLisObjNonVersId = monVLisObjNonVersId;
    }
}
