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
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.eng.sacerasi.job.ejb;

import java.math.BigDecimal;
import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import it.eng.parer.sacerlog.ejb.common.AppServerInstance;
import it.eng.sacerasi.common.Constants.NomiJob;
import it.eng.sacerasi.common.Constants.TipiRegLogJob;
//import it.eng.sacerasi.common.ejb.AppServerInstance;
import it.eng.sacerasi.entity.PigLogJob;

/**
 *
 * @author Fioravanti_F
 */
@Stateless(mappedName = "JobLogger")
@LocalBean
public class JobLogger {

    //
    @EJB
    JobLogger me;
    //
    @PersistenceContext(unitName = "SacerAsiJPA")
    private EntityManager entityManager;

    @EJB
    private AppServerInstance appServerInstance;

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void writeAtomicLog(NomiJob nomeJob, TipiRegLogJob tipoReg, String messaggioErr) {
        me.writeLog(nomeJob, tipoReg, messaggioErr);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void writeAtomicLog(NomiJob nomeJob, TipiRegLogJob tipoReg, String messaggioErr, BigDecimal idRecord) {
        me.writeLog(nomeJob, tipoReg, messaggioErr, idRecord);
    }

    public void writeLog(NomiJob nomeJob, TipiRegLogJob tipoReg, String messaggioErr) {
        PigLogJob tmpLogJob = new PigLogJob();
        tmpLogJob.setNmJob(nomeJob.toString());
        tmpLogJob.setTiRegLogJob(tipoReg.toString());
        tmpLogJob.setDtRegLogJob(new Date());
        tmpLogJob.setDlMsgErr(messaggioErr);
        tmpLogJob.setCdIndServer(appServerInstance.getName());

        entityManager.persist(tmpLogJob);
        entityManager.flush();
    }

    public void writeLog(NomiJob nomeJob, TipiRegLogJob tipoReg, String messaggioErr, BigDecimal idRecord) {
        PigLogJob tmpLogJob = new PigLogJob();
        tmpLogJob.setNmJob(nomeJob.toString());
        tmpLogJob.setTiRegLogJob(tipoReg.toString());
        tmpLogJob.setDtRegLogJob(new Date());
        tmpLogJob.setDlMsgErr(messaggioErr);
        tmpLogJob.setIdRecord(idRecord);
        tmpLogJob.setCdIndServer(appServerInstance.getName());

        entityManager.persist(tmpLogJob);
        entityManager.flush();
    }
}
