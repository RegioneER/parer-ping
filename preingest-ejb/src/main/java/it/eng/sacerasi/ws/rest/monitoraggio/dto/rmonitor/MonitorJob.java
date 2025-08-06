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

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Date;

/**
 *
 * @author fioravanti_f
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MonitorJob {

    public enum StatiTimer {
	ON, OFF
    }

    public enum StatiJob {
	CHIUSA_OK, CHIUSA_ERR, IN_CORSO
    }

    public enum Allarmi {
	NESSUN_ALLARME, ERRORE_SCHEDULAZIONE
    }

    private String nomeJob;
    private StatiTimer statoTimer;
    private StatiJob statoJob;
    private Allarmi allarmiDaUltimaChiamata;
    private Date tSUltimaAttivita;

    public String getNomeJob() {
	return nomeJob;
    }

    public void setNomeJob(String nomeJob) {
	this.nomeJob = nomeJob;
    }

    public StatiTimer getStatoTimer() {
	return statoTimer;
    }

    public void setStatoTimer(StatiTimer statoTimer) {
	this.statoTimer = statoTimer;
    }

    public StatiJob getStatoJob() {
	return statoJob;
    }

    public void setStatoJob(StatiJob statoJob) {
	this.statoJob = statoJob;
    }

    public Allarmi getAllarmiDaUltimaChiamata() {
	return allarmiDaUltimaChiamata;
    }

    public void setAllarmiDaUltimaChiamata(Allarmi allarmiDaUltimaChiamata) {
	this.allarmiDaUltimaChiamata = allarmiDaUltimaChiamata;
    }

    // NOTA: "yyyy-MM-dd'T'HH:mm:ss.SSSXXX" questo è il formato RFC3339, analogo al più celebre
    // ISO8601,
    // compreso di frazioni di secondo e timezone
    // @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX",
    // timezone = "CET")
    // @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX",
    // timezone = "GMT")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX", timezone = "CET")
    public Date gettSUltimaAttivita() {
	return tSUltimaAttivita;
    }

    public void settSUltimaAttivita(Date tSUltimaAttivita) {
	this.tSUltimaAttivita = tSUltimaAttivita;
    }

}
