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

import it.eng.sacerasi.common.Constants;
import it.eng.parer.jboss.timer.common.CronSchedule;
import it.eng.sacerasi.common.Constants.NomiJob;
import it.eng.sacerasi.job.producerCodaVers.ejb.ProducerCodaVersamentoEjb;
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
 * @author Agati_D
 */
@Singleton(mappedName = "ProducerCodaVersamentoTimer")
@Lock(LockType.READ)
@LocalBean
public class ProducerCodaVersamentoTimer extends JobTimer {

    private static final Logger logger = LoggerFactory.getLogger(ProducerCodaVersamentoTimer.class);
    @EJB
    private ProducerCodaVersamentoTimer thisTimer;
    @EJB
    private ProducerCodaVersamentoEjb producerCodaVersEjb;

    public ProducerCodaVersamentoTimer() {
        super(NomiJob.PRODUCER_CODA_VERS);
        logger.debug("{} creato", this.getClass().getName());
    }

    @Override
    @Lock(LockType.WRITE)
    // @Interceptors({JbossTimerNodeInterceptor.class})
    public void startSingleAction(String applicationName) {
        if (!isActive()) {
            timerService.createTimer(TIME_DURATION, jobName);
        }
    }

    @Override
    @Lock(LockType.WRITE)
    // @Interceptors({JbossTimerNodeInterceptor.class, JbossTimerStartInterceptor.class})
    public void startCronScheduled(CronSchedule sched, String applicationName) {
        ScheduleExpression tmpScheduleExpression;

        if (!isActive()) {
            logger.info("Schedulazione: Ore: {}", sched.getHour());
            logger.info("Schedulazione: Minuti: {}", sched.getMinute());
            logger.info("Schedulazione: DOW: {}", sched.getDayOfWeek());
            logger.info("Schedulazione: Mese: {}", sched.getMonth());
            logger.info("Schedulazione: DOM: {}", sched.getDayOfMonth());

            tmpScheduleExpression = new ScheduleExpression();
            tmpScheduleExpression.hour(sched.getHour());
            tmpScheduleExpression.minute(sched.getMinute());
            tmpScheduleExpression.dayOfWeek(sched.getDayOfWeek());
            tmpScheduleExpression.month(sched.getMonth());
            tmpScheduleExpression.dayOfMonth(sched.getDayOfMonth());
            logger.info("Lancio il timer ProducerCodaVersamentoTimer...");
            timerService.createCalendarTimer(tmpScheduleExpression, new TimerConfig(jobName, false));
        }
    }

    @Override
    @Lock(LockType.WRITE)
    // @Interceptors({JbossTimerNodeInterceptor.class})
    public void stop(String applicationName) {
        for (Timer timer : timerService.getTimers()) {
            String scheduled = (String) timer.getInfo();
            if (scheduled.equals(jobName)) {
                timer.cancel();
            }
        }
    }

    @Timeout
    // @Interceptors({JbossTimerTimeoutInterceptor.class})
    public void doJob(Timer timer) {
        if (timer.getInfo().equals(jobName)) {
            try {
                thisTimer.startProcess(timer);
            } catch (Exception e) {
                logger.error("Errore nell'esecuzione del job ProducerCodaVersamento", e);
            }
        }
    }

    @Override
    public void startProcess(Timer timer) throws Exception {
        jobLogger.writeAtomicLog(Constants.NomiJob.PRODUCER_CODA_VERS, Constants.TipiRegLogJob.INIZIO_SCHEDULAZIONE,
                null);
        try {
            producerCodaVersEjb.produceQueue();
            producerCodaVersEjb.gestisciAging();
        } catch (Exception e) {
            // questo log viene scritto solo in caso di errore.
            String message = null;
            if (e.getCause() != null) {
                message = e.getCause().getMessage();
            }
            jobLogger.writeAtomicLog(Constants.NomiJob.PRODUCER_CODA_VERS, Constants.TipiRegLogJob.ERRORE, message);
            logger.error("Errore nell'esecuzione del job ProducerCodaVersamento", e);
            logger.info("Timer cancellato");
            timer.cancel();
        }
    }
}
