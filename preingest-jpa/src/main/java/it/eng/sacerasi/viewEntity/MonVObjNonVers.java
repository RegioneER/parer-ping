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

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * The persistent class for the MON_V_OBJ_NON_VERS database table.
 */
@Entity
@Table(name = "MON_V_OBJ_NON_VERS")
public class MonVObjNonVers implements Serializable {

    private static final long serialVersionUID = 1L;

    private String flNonRisolub;

    private String flVerif;

    private String nmTipoObject;

    public MonVObjNonVers() {
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

    @Column(name = "NM_TIPO_OBJECT")
    public String getNmTipoObject() {
	return this.nmTipoObject;
    }

    public void setNmTipoObject(String nmTipoObject) {
	this.nmTipoObject = nmTipoObject;
    }

    private MonVObjNonVersId monVObjNonVersId;

    @EmbeddedId()
    public MonVObjNonVersId getMonVObjNonVersId() {
	return monVObjNonVersId;
    }

    public void setMonVObjNonVersId(MonVObjNonVersId monVObjNonVersId) {
	this.monVObjNonVersId = monVObjNonVersId;
    }
}
