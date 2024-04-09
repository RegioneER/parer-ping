/*
 * Engineering Ingegneria Informatica S.p.A.
 *
 * Copyright (C) 2023 Regione Emilia-Romagna
 * <p/>
 * This program is free software: you can redistribute it and/or modify it under the terms of
 * the GNU Affero General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU Affero General Public License along with this program.
 * If not, see <https://www.gnu.org/licenses/>.
 */

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.eng.sacerasi.ws.rest.monitoraggio.ejb;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import it.eng.sacerasi.common.Constants;
import it.eng.sacerasi.entity.PigDecJob;
import it.eng.sacerasi.ws.dto.RispostaControlli;
import it.eng.sacerasi.ws.rest.monitoraggio.dto.RispostaWSStatusMonitor;
import it.eng.sacerasi.ws.rest.monitoraggio.dto.rmonitor.MonitorJob;

/**
 *
 * @author fioravanti_f
 */
@Stateless(mappedName = "JobStatusMonitor")
@LocalBean
public class JobStatusMonitor {

    @EJB
    ControlliMonitor controlliMonitor;

    @SuppressWarnings("unchecked")
    public void calcolaStatoJob(RispostaWSStatusMonitor rispostaWs, List<MonitorJob> tmpLstJob,
            Date ultimaChiamataDelWs) {
        List<PigDecJob> lstDj = null;
        RispostaControlli rc = controlliMonitor.leggiElencoJob();
        if (rc.isrBoolean()) {
            lstDj = (List<PigDecJob>) rc.getrObject();
        } else {
            rispostaWs.setEsitoWsError(rc.getCodErr(), rc.getDsErr());
            return;
        }

        //
        for (PigDecJob dj : lstDj) {
            MonitorJob tmpJob = new MonitorJob();
            tmpJob.setNomeJob(dj.getNmJob());
            //
            if (dj.getTiSchedJob().equals("STANDARD")) {
                rc = controlliMonitor.recNuovaEsecuzioneTimer(dj.getNmJob());
                if (rc.isrBoolean()) {
                    if (rc.getrDate() != null) {
                        tmpJob.setStatoTimer(MonitorJob.StatiTimer.ON);
                    } else {
                        tmpJob.setStatoTimer(MonitorJob.StatiTimer.OFF);
                    }
                } else {
                    rispostaWs.setEsitoWsError(rc.getCodErr(), rc.getDsErr());
                    return;
                }
            } else {
                // se il job non è "STANDARD", allora è classificato come "NO_TIMER".
                // Non posso verificare lo stato di un timer che
                // non esiste, perciò rendo per default il valore "ON" che non produce
                // warning in sede di monitoraggio
                tmpJob.setStatoTimer(MonitorJob.StatiTimer.ON);
            }

            //
            Date ultimaAttivitaDelJob = null;
            rc = controlliMonitor.leggiUltimaRegistrazione(dj.getNmJob());
            if (rc.isrBoolean()) {
                ultimaAttivitaDelJob = rc.getrDate();
                tmpJob.settSUltimaAttivita(ultimaAttivitaDelJob);
                switch ((Constants.TipiRegLogJob) rc.getrObject()) {
                case INIZIO_SCHEDULAZIONE:
                    tmpJob.setStatoJob(MonitorJob.StatiJob.IN_CORSO);
                    break;
                case FINE_SCHEDULAZIONE:
                    tmpJob.setStatoJob(MonitorJob.StatiJob.CHIUSA_OK);
                    break;
                case ERRORE:
                    tmpJob.setStatoJob(MonitorJob.StatiJob.CHIUSA_ERR);
                    break;
                }
            } else {
                rispostaWs.setEsitoWsError(rc.getCodErr(), rc.getDsErr());
                return;
            }

            //
            rc = controlliMonitor.leggiAllarmiInIntervallo(dj.getNmJob(), ultimaChiamataDelWs, ultimaAttivitaDelJob);
            if (rc.isrBoolean()) {
                if (rc.getrLong() > 0) {
                    tmpJob.setAllarmiDaUltimaChiamata(MonitorJob.Allarmi.ERRORE_SCHEDULAZIONE);
                } else {
                    tmpJob.setAllarmiDaUltimaChiamata(MonitorJob.Allarmi.NESSUN_ALLARME);
                }
            } else {
                rispostaWs.setEsitoWsError(rc.getCodErr(), rc.getDsErr());
                return;
            }

            //
            tmpLstJob.add(tmpJob);
        }
    }
}
