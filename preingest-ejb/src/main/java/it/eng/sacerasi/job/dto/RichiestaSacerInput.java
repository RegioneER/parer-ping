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

package it.eng.sacerasi.job.dto;

/**
 *
 * @author Bonora_L
 */
public class RichiestaSacerInput {

    public enum TipoRichiestaSacer {

	VERSAMENTO, RECUPERO, ANNULLAMENTO
    }

    private TipoRichiestaSacer tipoRichiesta;
    private String versioneWsDaInvocare;
    private String urlRichiesta;
    private String xmlRichiestaSacer;
    private String xmlIndice;
    private String userIdSacer;
    private String passwordSacer;
    private Integer timeout;

    public RichiestaSacerInput(TipoRichiestaSacer tipoRichiesta, String versioneWsDaInvocare,
	    String urlRichiesta, String xmlRichiestaSacer, String xmlIndice, String userIdSacer,
	    String passwordSacer, Integer timeout) {
	this.tipoRichiesta = tipoRichiesta;
	this.versioneWsDaInvocare = versioneWsDaInvocare;
	this.urlRichiesta = urlRichiesta;
	this.xmlRichiestaSacer = xmlRichiestaSacer;
	this.xmlIndice = xmlIndice;
	this.userIdSacer = userIdSacer;
	this.passwordSacer = passwordSacer;
	this.timeout = timeout;
    }

    /**
     * @return the tipoRichiesta
     */
    public TipoRichiestaSacer getTipoRichiesta() {
	return tipoRichiesta;
    }

    /**
     * @param tipoRichiesta the tipoRichiesta to set
     */
    public void setTipoRichiesta(TipoRichiestaSacer tipoRichiesta) {
	this.tipoRichiesta = tipoRichiesta;
    }

    public String getVersioneWsDaInvocare() {
	return versioneWsDaInvocare;
    }

    public void setVersioneWsDaInvocare(String versioneWsDaInvocare) {
	this.versioneWsDaInvocare = versioneWsDaInvocare;
    }

    /**
     * @return the urlRichiesta
     */
    public String getUrlRichiesta() {
	return urlRichiesta;
    }

    /**
     * @param urlRichiesta the urlRichiesta to set
     */
    public void setUrlRichiesta(String urlRichiesta) {
	this.urlRichiesta = urlRichiesta;
    }

    /**
     * @return the xmlRichiestaSacer
     */
    public String getXmlRichiestaSacer() {
	return xmlRichiestaSacer;
    }

    /**
     * @param xmlRichiestaSacer the xmlRichiestaSacer to set
     */
    public void setXmlRichiestaSacer(String xmlRichiestaSacer) {
	this.xmlRichiestaSacer = xmlRichiestaSacer;
    }

    /**
     * @return the xmlIndice
     */
    public String getXmlIndice() {
	return xmlIndice;
    }

    /**
     * @param xmlIndice the xmlIndice to set
     */
    public void setXmlIndice(String xmlIndice) {
	this.xmlIndice = xmlIndice;
    }

    /**
     * @return the userIdSacer
     */
    public String getUserIdSacer() {
	return userIdSacer;
    }

    /**
     * @param userIdSacer the userIdSacer to set
     */
    public void setUserIdSacer(String userIdSacer) {
	this.userIdSacer = userIdSacer;
    }

    /**
     * @return the passwordSacer
     */
    public String getPasswordSacer() {
	return passwordSacer;
    }

    /**
     * @param passwordSacer the passwordSacer to set
     */
    public void setPasswordSacer(String passwordSacer) {
	this.passwordSacer = passwordSacer;
    }

    /**
     * @return the timeout
     */
    public Integer getTimeout() {
	return timeout;
    }

    /**
     * @param timeout the timeout to set
     */
    public void setTimeout(Integer timeout) {
	this.timeout = timeout;
    }
}
