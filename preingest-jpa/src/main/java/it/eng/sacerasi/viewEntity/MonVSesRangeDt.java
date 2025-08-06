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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * The persistent class for the MON_V_SES_RANGE_DT database table.
 *
 */
@Entity
@Table(name = "MON_V_SES_RANGE_DT")
public class MonVSesRangeDt implements Serializable {
    private static final long serialVersionUID = 1L;
    private String flNonRisolub;
    private String flVerif;
    private BigDecimal idSessioneIngest;
    private BigDecimal idVers;
    private String nmTipoObject;
    private String tiDtCreazione;
    private String tiStato;
    private String tiStatoRisoluz;

    public MonVSesRangeDt() {
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

    @Id
    @Column(name = "ID_SESSIONE_INGEST")
    public BigDecimal getIdSessioneIngest() {
	return this.idSessioneIngest;
    }

    public void setIdSessioneIngest(BigDecimal idSessioneIngest) {
	this.idSessioneIngest = idSessioneIngest;
    }

    @Column(name = "ID_VERS")
    public BigDecimal getIdVers() {
	return this.idVers;
    }

    public void setIdVers(BigDecimal idVers) {
	this.idVers = idVers;
    }

    @Column(name = "NM_TIPO_OBJECT")
    public String getNmTipoObject() {
	return this.nmTipoObject;
    }

    public void setNmTipoObject(String nmTipoObject) {
	this.nmTipoObject = nmTipoObject;
    }

    @Column(name = "TI_DT_CREAZIONE")
    public String getTiDtCreazione() {
	return this.tiDtCreazione;
    }

    public void setTiDtCreazione(String tiDtCreazione) {
	this.tiDtCreazione = tiDtCreazione;
    }

    @Column(name = "TI_STATO")
    public String getTiStato() {
	return this.tiStato;
    }

    public void setTiStato(String tiStato) {
	this.tiStato = tiStato;
    }

    @Column(name = "TI_STATO_RISOLUZ")
    public String getTiStatoRisoluz() {
	return this.tiStatoRisoluz;
    }

    public void setTiStatoRisoluz(String tiStatoRisoluz) {
	this.tiStatoRisoluz = tiStatoRisoluz;
    }

}
