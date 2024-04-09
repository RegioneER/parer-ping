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

import it.eng.parer.jboss.timer.common.JbossJobTimer;
import it.eng.sacerasi.common.Constants;
import it.eng.parer.jboss.timer.common.CronSchedule;
import it.eng.sacerasi.job.ejb.JobLogger;
import java.util.Date;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.NoMoreTimeoutsException;
import javax.ejb.Timer;
import javax.ejb.TimerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Moretti_Lu
 */
@Lock(LockType.READ)
public abstract class JobTimer implements JbossJobTimer {

    protected static final int TIME_DURATION = 2000;

    private final Logger logger = LoggerFactory.getLogger(JobTimer.class);

    protected final String jobName;
    @Resource
    protected TimerService timerService;

    @EJB(mappedName = "java:app/SacerAsync-ejb/JobLogger")
    protected JobLogger jobLogger;

    protected JobTimer(Constants.NomiJob jobName) {
        if (jobName == null) {
            throw new IllegalArgumentException();
        }

        this.jobName = jobName.name();
    }

    @Override
    public String getJobName() {
        return jobName;
    }

    protected boolean isActive() {
        boolean result = false;

        for (Object obj : timerService.getTimers()) {
            Timer timer = (Timer) obj;
            String scheduled = (String) timer.getInfo();
            if (scheduled.equals(jobName)) {
                result = true;
                break;
            }
        }

        return result;
    }

    @Override
    public Date getNextElaboration(String applicationName) {
        try {
            for (Object obj : timerService.getTimers()) {
                Timer timer = (Timer) obj;
                String scheduled = (String) timer.getInfo();

                if (scheduled.equals(jobName)) {
                    return timer.getNextTimeout();
                }
            }

        } catch (NoMoreTimeoutsException e) {
            logger.warn("Il metodo timer.getNextTimeout() ha restituito eccezione");
        }
        return null;
    }

    /**
     * This method is invoked by <code>doJob</code> and invokes the job business logic.
     *
     * @param timer
     *            - timer
     * 
     * @throws Exception
     *             errore generico
     */
    public abstract void startProcess(Timer timer) throws Exception;

    @Override
    public abstract void startCronScheduled(CronSchedule sched, String applicationName);

    @Override
    public abstract void startSingleAction(String applicationName);

    @Override
    public abstract void stop(String applicationName);
}
