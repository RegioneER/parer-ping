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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.id.enhanced.SequenceStyleGenerator;

/**
 * The persistent class for the PIG_DICH_VERS_SACER_TIPO_OBJ database table.
 *
 */
@Entity
@Table(name = "PIG_DICH_VERS_SACER_TIPO_OBJ")
public class PigDichVersSacerTipoObj implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long idDichVersSacerTipoObj;
    private java.math.BigDecimal idOrganizIam;
    private String tiDichVers;
    private PigTipoObject pigTipoObject;

    public PigDichVersSacerTipoObj() {
	// for Hibernate
    }

    @Id
    @GenericGenerator(name = "PIG_DICH_VERS_SACER_TIPO_OBJ_IDDICHVERSSACERTIPOOBJ_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
	    @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SPIG_DICH_VERS_SACER_TIPO_OBJ"),
	    @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PIG_DICH_VERS_SACER_TIPO_OBJ_IDDICHVERSSACERTIPOOBJ_GENERATOR")
    @Column(name = "ID_DICH_VERS_SACER_TIPO_OBJ")
    public Long getIdDichVersSacerTipoObj() {
	return this.idDichVersSacerTipoObj;
    }

    public void setIdDichVersSacerTipoObj(Long idDichVersSacerTipoObj) {
	this.idDichVersSacerTipoObj = idDichVersSacerTipoObj;
    }

    @Column(name = "ID_ORGANIZ_IAM")
    public java.math.BigDecimal getIdOrganizIam() {
	return this.idOrganizIam;
    }

    public void setIdOrganizIam(java.math.BigDecimal idOrganizIam) {
	this.idOrganizIam = idOrganizIam;
    }

    @Column(name = "TI_DICH_VERS")
    public String getTiDichVers() {
	return this.tiDichVers;
    }

    public void setTiDichVers(String tiDichVers) {
	this.tiDichVers = tiDichVers;
    }

    // bi-directional many-to-one association to PigTipoObject
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_TIPO_OBJECT")
    public PigTipoObject getPigTipoObject() {
	return this.pigTipoObject;
    }

    public void setPigTipoObject(PigTipoObject pigTipoObject) {
	this.pigTipoObject = pigTipoObject;
    }

}
