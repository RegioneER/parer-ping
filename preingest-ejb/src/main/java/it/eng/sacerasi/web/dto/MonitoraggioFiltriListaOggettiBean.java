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

package it.eng.sacerasi.web.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Gilioli_P
 */
public class MonitoraggioFiltriListaOggettiBean implements Serializable {

    private BigDecimal idAmbienteVers;
    private BigDecimal idVers;
    private BigDecimal idTipoObject;
    private String periodoVers;
    private Date giornoVersDa;
    private Date giornoVersA;
    private BigDecimal oreVersDa;
    private BigDecimal minutiVersDa;
    private BigDecimal oreVersA;
    private BigDecimal minutiVersA;
    private Date giornoVersDaValidato;
    private Date giornoVersAValidato;
    private List<String> statoObject;
    private String registro;
    private BigDecimal anno;
    private String codice;
    private String chiave;
    private List<String> tiVersFile;
    private BigDecimal idObject;

    public BigDecimal getIdAmbienteVers() {
	return idAmbienteVers;
    }

    public void setIdAmbienteVers(BigDecimal idAmbienteVers) {
	this.idAmbienteVers = idAmbienteVers;
    }

    public BigDecimal getIdVers() {
	return idVers;
    }

    public void setIdVers(BigDecimal idVers) {
	this.idVers = idVers;
    }

    public BigDecimal getIdTipoObject() {
	return idTipoObject;
    }

    public void setIdTipoObject(BigDecimal idTipoObject) {
	this.idTipoObject = idTipoObject;
    }

    public String getPeriodoVers() {
	return periodoVers;
    }

    public void setPeriodoVers(String periodoVers) {
	this.periodoVers = periodoVers;
    }

    public Date getGiornoVersDa() {
	return giornoVersDa;
    }

    public void setGiornoVersDa(Date giornoVersDa) {
	this.giornoVersDa = giornoVersDa;
    }

    public Date getGiornoVersA() {
	return giornoVersA;
    }

    public void setGiornoVersA(Date giornoVersA) {
	this.giornoVersA = giornoVersA;
    }

    public BigDecimal getOreVersDa() {
	return oreVersDa;
    }

    public void setOreVersDa(BigDecimal oreVersDa) {
	this.oreVersDa = oreVersDa;
    }

    public BigDecimal getMinutiVersDa() {
	return minutiVersDa;
    }

    public void setMinutiVersDa(BigDecimal minutiVersDa) {
	this.minutiVersDa = minutiVersDa;
    }

    public BigDecimal getOreVersA() {
	return oreVersA;
    }

    public void setOreVersA(BigDecimal oreVersA) {
	this.oreVersA = oreVersA;
    }

    public BigDecimal getMinutiVersA() {
	return minutiVersA;
    }

    public void setMinutiVersA(BigDecimal minutiVersA) {
	this.minutiVersA = minutiVersA;
    }

    public Date getGiornoVersDaValidato() {
	return giornoVersDaValidato;
    }

    public void setGiornoVersDaValidato(Date giornoVersDaValidato) {
	this.giornoVersDaValidato = giornoVersDaValidato;
    }

    public Date getGiornoVersAValidato() {
	return giornoVersAValidato;
    }

    public void setGiornoVersAValidato(Date giornoVersAValidato) {
	this.giornoVersAValidato = giornoVersAValidato;
    }

    public List<String> getStatoObject() {
	return statoObject;
    }

    public void setStatoObject(List<String> statoObject) {
	this.statoObject = statoObject;
    }

    public String getRegistro() {
	return registro;
    }

    public void setRegistro(String registro) {
	this.registro = registro;
    }

    public BigDecimal getAnno() {
	return anno;
    }

    public void setAnno(BigDecimal anno) {
	this.anno = anno;
    }

    public String getCodice() {
	return codice;
    }

    public void setCodice(String codice) {
	this.codice = codice;
    }

    public String getChiave() {
	return chiave;
    }

    public void setChiave(String chiave) {
	this.chiave = chiave;
    }

    public List<String> getTiVersFile() {
	return tiVersFile;
    }

    public void setTiVersFile(List<String> tiVersFile) {
	this.tiVersFile = tiVersFile;
    }

    // MEV 26979
    public BigDecimal getIdObject() {
	return idObject;
    }

    public void setIdObject(BigDecimal idObject) {
	this.idObject = idObject;
    }
}
