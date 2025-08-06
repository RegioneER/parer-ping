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

/*
 * To change this license header, choose License Headers in Project Properties. To change this
 * template file, choose Tools | Templates and open the template in the editor.
 */
package it.eng.sacerasi.ws.rest.monitoraggio.dto.rmonitor;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

/**
 *
 * @author fioravanti_f
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HostMonitor {

    private String versione;
    private String codiceErrore;
    private String descrErrore;
    private List<MonitorJob> job;
    private List<MonitorAltro> altri;

    public String getVersione() {
	return versione;
    }

    public void setVersione(String versione) {
	this.versione = versione;
    }

    public String getCodiceErrore() {
	return codiceErrore;
    }

    public void setCodiceErrore(String codiceErrore) {
	this.codiceErrore = codiceErrore;
    }

    public String getDescrErrore() {
	return descrErrore;
    }

    public void setDescrErrore(String descrErrore) {
	this.descrErrore = descrErrore;
    }

    public List<MonitorJob> getJob() {
	return job;
    }

    public void setJob(List<MonitorJob> job) {
	this.job = job;
    }

    public List<MonitorAltro> getAltri() {
	return altri;
    }

    public void setAltri(List<MonitorAltro> altri) {
	this.altri = altri;
    }

}
