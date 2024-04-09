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

package it.eng.xformer.job.timer;

import it.eng.parer.jboss.timer.common.CronSchedule;
import it.eng.sacerasi.common.Constants;
import it.eng.sacerasi.job.timer.JobTimer;
import it.eng.xformer.job.ejb.InviaOggettiGeneratiAPing;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.ScheduleExpression;
import javax.ejb.SessionContext;
import javax.ejb.Singleton;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author cek
 */
@Singleton(mappedName = "InviaOggettiGeneratiAPingTimer")
@Lock(LockType.READ)
@LocalBean
public class InviaOggettiGeneratiAPingTimer extends JobTimer {
    private final Logger logger = LoggerFactory.getLogger(InviaOggettiGeneratiAPingTimer.class);

    @EJB
    private InviaOggettiGeneratiAPing inviaOggettiGeneratiAPing;
    @Resource
    private SessionContext context;

    public InviaOggettiGeneratiAPingTimer() {
        super(Constants.NomiJob.INVIA_OGGETTI_GENERATI_A_PING);
    }

    @Override
    @Lock(LockType.WRITE)
    public void startCronScheduled(CronSchedule sched, String applicationName) {
        boolean existTimer = false;
        ScheduleExpression tmpScheduleExpression;

        for (Object obj : timerService.getTimers()) {
            Timer timer = (Timer) obj;
            String scheduled = (String) timer.getInfo();
            if (scheduled.equals(jobName)) {
                existTimer = true;
            }
        }
        if (!existTimer) {
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
            logger.info("Lancio il timer InviaOggettiGeneratiAPingTimer...");
            timerService.createCalendarTimer(tmpScheduleExpression, new TimerConfig(jobName, false));
        }
    }

    @Override
    @Lock(LockType.WRITE)
    public void startSingleAction(String applicationName) {
        boolean existTimer = false;

        for (Object obj : timerService.getTimers()) {
            Timer timer = (Timer) obj;
            String scheduled = (String) timer.getInfo();
            if (scheduled.equals(jobName)) {
                existTimer = true;
            }
        }
        if (!existTimer) {
            timerService.createTimer(TIME_DURATION, jobName);
        }
    }

    @Override
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
            context.getBusinessObject(InviaOggettiGeneratiAPingTimer.class).startProcess(timer);
        }
    }

    @Override
    public void startProcess(Timer timer) {
        try {
            jobLogger.writeAtomicLog(Constants.NomiJob.INVIA_OGGETTI_GENERATI_A_PING,
                    Constants.TipiRegLogJob.INIZIO_SCHEDULAZIONE, null);
            inviaOggettiGeneratiAPing.run();
            jobLogger.writeAtomicLog(Constants.NomiJob.INVIA_OGGETTI_GENERATI_A_PING,
                    Constants.TipiRegLogJob.FINE_SCHEDULAZIONE, null);
        } catch (Exception e) {
            // questo log viene scritto solo in caso di errore.
            String message = null;
            if (e.getCause() != null) {
                message = e.getCause().getMessage();
            }
            jobLogger.writeAtomicLog(Constants.NomiJob.INVIA_OGGETTI_GENERATI_A_PING, Constants.TipiRegLogJob.ERRORE,
                    message);
            logger.error("Errore nell'esecuzione del job di \"Invia oggetti generati a PING\"", e);
        }
    }
}
