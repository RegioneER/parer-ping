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
package it.eng.sacerasi.ws.rest.monitoraggio.ejb;

import it.eng.sacerasi.ws.dto.IRispostaWS;
import it.eng.sacerasi.ws.dto.RispostaControlli;
import it.eng.sacerasi.ws.rest.ejb.ControlliRestWS;
import it.eng.sacerasi.ws.rest.monitoraggio.dto.RispostaWSStatusMonitor;
import it.eng.sacerasi.ws.rest.monitoraggio.dto.StatusMonExt;
import it.eng.sacerasi.ws.rest.monitoraggio.dto.rmonitor.HostMonitor;
import it.eng.sacerasi.ws.rest.monitoraggio.dto.rmonitor.MonitorAltro;
import it.eng.sacerasi.ws.rest.monitoraggio.dto.rmonitor.MonitorJob;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 *
 * @author fioravanti_f
 */
@Stateless(mappedName = "StatusMonitorGen")
@LocalBean
public class StatusMonitorGen {

    @EJB
    ControlliMonitor controlliMonitor;

    @EJB
    JobStatusMonitor jobStatusMonitor;

    @EJB
    AltriStatusMonitor altriStatusMonitor;
    @EJB
    ControlliRestWS controlliWS;
    // nell'originale su SACER la classe di chiama ControlliWs
    // in questo caso, quel nome era già "occupato"

    public void calcolaStatusGlobale(RispostaWSStatusMonitor rispostaWs, StatusMonExt mon) {
	// verifica se sono abilitato a chiamare questa funzione
	RispostaControlli rc = controlliWS.checkAuthWSNoOrg(mon.getUtente(), mon.getDescrizione());
	if (!rc.isrBoolean()) {
	    rispostaWs.setEsitoWsError(rc.getCodErr(), rc.getDsErr());
	    return;
	}

	// determino il timestamp dell'ultima esecuzione oppure 24 ore da questo istante
	// se non è mai stato eseguito.
	// la data mi serve per verificare se dall'ultima verifica dello stato
	// si sono verificati degli allarmi su un job
	Date ultimaChiamataDelWs = null;
	rc = controlliMonitor.leggiUltimaChiamataWS();
	if (rc.isrBoolean()) {
	    ultimaChiamataDelWs = rc.getrDate();
	} else {
	    rispostaWs.setEsitoWsError(rc.getCodErr(), rc.getDsErr());
	    return;
	}

	HostMonitor myEsito = rispostaWs.getIstanzaEsito();
	// iniziamo con il monitoraggio dei job
	List<MonitorJob> tmpLstJob = new ArrayList<>();
	jobStatusMonitor.calcolaStatoJob(rispostaWs, tmpLstJob, ultimaChiamataDelWs);
	//
	List<MonitorAltro> tmpLstAltro = new ArrayList<>();
	if (rispostaWs.getSeverity() != IRispostaWS.SeverityEnum.ERROR) {
	    // monitoraggio degli altri parametri
	    altriStatusMonitor.calcolaStatoDatabase(tmpLstAltro);
	}
	//
	if (rispostaWs.getSeverity() != IRispostaWS.SeverityEnum.ERROR) {
	    // tutto OK, aggancio all'oggetto di risposta i due array di parametri
	    if (!tmpLstJob.isEmpty()) {
		myEsito.setJob(tmpLstJob);
	    }
	    if (!tmpLstAltro.isEmpty()) {
		myEsito.setAltri(tmpLstAltro);
	    }
	}
    }

    public void calcolaStatusHost(RispostaWSStatusMonitor rispostaWs, StatusMonExt mon) {
	// verifica se sono abilitato a chiamare questa funzione
	RispostaControlli rc = controlliWS.checkAuthWSNoOrg(mon.getUtente(), mon.getDescrizione());
	if (!rc.isrBoolean()) {
	    rispostaWs.setEsitoWsError(rc.getCodErr(), rc.getDsErr());
	    return;
	}
	HostMonitor myEsito = rispostaWs.getIstanzaEsito();
	List<MonitorJob> tmpLstJob = new ArrayList<>();
	// monitoraggio degli altri parametri
	List<MonitorAltro> tmpLstAltro = new ArrayList<>();
	altriStatusMonitor.calcolaStatoCodaMorta(tmpLstAltro);
	if (rispostaWs.getSeverity() != IRispostaWS.SeverityEnum.ERROR) {
	    // tutto OK, aggancio all'oggetto di risposta i due array di parametri
	    if (!tmpLstJob.isEmpty()) {
		myEsito.setJob(tmpLstJob);
	    }
	    if (!tmpLstAltro.isEmpty()) {
		myEsito.setAltri(tmpLstAltro);
	    }
	}
    }

}
