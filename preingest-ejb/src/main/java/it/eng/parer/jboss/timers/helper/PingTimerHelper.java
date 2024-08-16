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

package it.eng.parer.jboss.timers.helper;

import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import it.eng.parer.jboss.timer.common.JbossJobTimer;
import it.eng.parer.jboss.timer.common.JobTable;
import it.eng.parer.jboss.timer.exception.TimerNotFoundException;
import it.eng.parer.jboss.timer.helper.AbstractJbossTimerHelper;
import it.eng.parer.jboss.timer.helper.JbossTimerHelper;
import it.eng.sacerasi.web.helper.ConfigurationHelper;

/**
 *
 * Helper per la gestione dei job su jboss.
 *
 * @author Snidero_L
 */
@Stateless
// (name = "timerHelper")
// La riga sottostante va inidicata se si vuole utilizzare il bean in un bean di spring
// @EJB(name = "java:app/jboss-timers/TimerHelper", beanInterface =
// it.eng.parer.jboss.timer.helper.JbossTimerHelper.class)
public class PingTimerHelper extends AbstractJbossTimerHelper implements JbossTimerHelper {

    @PersistenceContext(unitName = "SacerAsiJPA")
    private EntityManager em;

    @EJB
    private ConfigurationHelper paramApplicHelper;

    @EJB
    private TimerRepository timerRepository;

    @Override
    public String getApplicationName() {
        return paramApplicHelper.getParamApplicApplicationName();
    }

    @Override
    public List<JobTable> getJobs() {
        List<JobTable> resultList = em.createQuery("Select d From PigDecJob d", JobTable.class).getResultList();
        return resultList;
    }

    @Override
    public JobTable getJob(String jobName) throws TimerNotFoundException {
        TypedQuery<JobTable> query = em.createQuery("Select d From PigDecJob d Where d.nmJob = :nmJob", JobTable.class)
                .setParameter("nmJob", jobName);
        List<JobTable> list = query.setMaxResults(1).getResultList();

        if (list.isEmpty()) {
            throw new TimerNotFoundException(jobName);
        }
        return list.get(0);
    }

    @Override
    public JbossJobTimer getTimer(String jobName) throws TimerNotFoundException {
        JbossJobTimer job = timerRepository.getConfiguredTimer(jobName);
        if (job != null) {
            return job;
        }
        throw new TimerNotFoundException(jobName);
    }

    @Override
    public Set<String> getApplicationTimerNames() {
        return timerRepository.getConfiguredTimersName();
    }

}
