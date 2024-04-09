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

package it.eng.sacerasi.job.timer;

import it.eng.parer.jboss.timer.common.CronSchedule;
import it.eng.sacerasi.common.Constants;
import it.eng.sacerasi.exception.ParerInternalError;
import it.eng.sacerasi.job.allineamentoOrganizzazioni.ejb.AllineamentoOrganizzazioniEjb;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.ScheduleExpression;
import javax.ejb.Singleton;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Gilioli_P
 */
@Singleton(mappedName = "AllineamentoOrganizzazioniTimer")
@Lock(LockType.READ)
@LocalBean
public class AllineamentoOrganizzazioniTimer extends JobTimer {

    private static final Logger logger = LoggerFactory.getLogger(AllineamentoOrganizzazioniTimer.class);
    @EJB
    private AllineamentoOrganizzazioniTimer thisTimer;
    @EJB
    private AllineamentoOrganizzazioniEjb allineamentoOrganizzazioniEjb;

    public AllineamentoOrganizzazioniTimer() {
        super(Constants.NomiJob.ALLINEAMENTO_ORGANIZZAZIONI);
        logger.debug(AllineamentoOrganizzazioniTimer.class.getName() + " creato");
    }

    @Override
    @Lock(LockType.WRITE)
    public void startSingleAction(String applicationName) {
        if (!isActive()) {
            timerService.createTimer(TIME_DURATION, jobName);
        }
    }

    @Override
    @Lock(LockType.WRITE)
    public void startCronScheduled(CronSchedule sched, String applicationName) {
        ScheduleExpression tmpScheduleExpression;

        if (!isActive()) {
            logger.info("Schedulazione: Ore: " + sched.getHour());
            logger.info("Schedulazione: Minuti: " + sched.getMinute());
            logger.info("Schedulazione: DOW: " + sched.getDayOfWeek());
            logger.info("Schedulazione: Mese: " + sched.getMonth());
            logger.info("Schedulazione: DOM: " + sched.getDayOfMonth());

            tmpScheduleExpression = new ScheduleExpression();
            tmpScheduleExpression.hour(sched.getHour());
            tmpScheduleExpression.minute(sched.getMinute());
            tmpScheduleExpression.dayOfWeek(sched.getDayOfWeek());
            tmpScheduleExpression.month(sched.getMonth());
            tmpScheduleExpression.dayOfMonth(sched.getDayOfMonth());
            logger.info("Lancio il timer AllineamentoOrganizzazioniTimer...");
            timerService.createCalendarTimer(tmpScheduleExpression, new TimerConfig(jobName, false));
        }
    }

    @Override
    @Lock(LockType.WRITE)
    public void stop(String applicationName) {
        for (Object obj : timerService.getTimers()) {
            Timer timer = (Timer) obj;
            String scheduled = (String) timer.getInfo();
            if (scheduled.equals(jobName)) {
                timer.cancel();
            }
        }
    }

    @Timeout
    public void doJob(Timer timer) {
        if (timer.getInfo().equals(jobName)) {
            thisTimer.startProcess(timer);
        }
    }

    @Override
    public void startProcess(Timer timer) {
        try {
            jobLogger.writeAtomicLog(Constants.NomiJob.ALLINEAMENTO_ORGANIZZAZIONI,
                    Constants.TipiRegLogJob.INIZIO_SCHEDULAZIONE, null);
            allineamentoOrganizzazioniEjb.allineamentoOrganizzazioni();
        } catch (ParerInternalError e) {
            // questo log viene scritto solo in caso di errore.
            String message = null;
            Exception nativeExcp = e.getNativeException();
            if (nativeExcp != null) {
                message = nativeExcp.getMessage();
            }
            if (e.getCause() != null) {
                message = e.getCause().getMessage();
            }
            jobLogger.writeAtomicLog(Constants.NomiJob.ALLINEAMENTO_ORGANIZZAZIONI, Constants.TipiRegLogJob.ERRORE,
                    message);
            logger.error("Errore nell'esecuzione del job di allineamento organizzazioni", e);
        } catch (Exception e) {
            // questo log viene scritto solo in caso di errore.
            String message = null;
            if (e.getCause() != null) {
                message = e.getCause().getMessage();
            }
            jobLogger.writeAtomicLog(Constants.NomiJob.ALLINEAMENTO_ORGANIZZAZIONI, Constants.TipiRegLogJob.ERRORE,
                    message);
            logger.error("Errore nell'esecuzione del job di allineamento organizzazioni", e);
        }
    }
}
