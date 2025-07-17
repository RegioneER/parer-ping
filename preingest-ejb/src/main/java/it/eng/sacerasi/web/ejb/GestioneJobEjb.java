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

package it.eng.sacerasi.web.ejb;

import it.eng.sacerasi.entity.PigDecJob;
import it.eng.sacerasi.entity.PigDecJobFoto;
import it.eng.sacerasi.web.helper.GestioneJobHelper;
import it.eng.sacerasi.slite.gen.form.GestioneJobForm;
import it.eng.spagoCore.error.EMFError;
import it.eng.spagoLite.db.base.row.BaseRow;
import it.eng.spagoLite.db.base.table.BaseTable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Gilioli_P
 */
@SuppressWarnings("unchecked")
@Stateless
@LocalBean
@Interceptors({
	it.eng.sacerasi.aop.TransactionInterceptor.class })
public class GestioneJobEjb {

    private static final Logger LOGGER = LoggerFactory.getLogger(GestioneJobEjb.class);
    private static final String ATTIVO = "ATTIVO";
    private static final String DISATTIVO = "DISATTIVO";

    @EJB
    GestioneJobHelper helper;

    public void salvaFoto() {
	// Cancello tutti i record di DEC_JOB_FOTO
	helper.bulkDeletePigDecJobFoto();
	// Copio tutti i record di DEC_JOB in DEC_JOB_FOTO
	helper.copyToFoto();
    }

    public void disabilitaAllJobs() {
	// Disattivo tutti i jobs
	helper.disabilitaAllJobs();
    }

    public boolean isPigDecJobFotoEmpty() {
	return helper.isPigDecJobFotoEmpty();
    }

    public boolean areAllJobsDisattivati() {
	return helper.areAllJobsDisattivati();
    }

    public void ripristinaFotoGestioneJob() {
	// Ripristino solo lo stato, se i dati di schedulazione sono non nulli,
	// altrimenti non ripristino lo stato.
	List<PigDecJobFoto> jobFotoList = helper.getEntityManager()
		.createNamedQuery("PigDecJobFoto.findAll").getResultList();
	for (PigDecJobFoto jobFoto : jobFotoList) {
	    // Se il job nella foto risulta schedulato
	    if (jobFoto.getDtProssimaAttivazioneFoto() != null) {
		PigDecJob job = helper.getEntityManager().find(PigDecJob.class,
			jobFoto.getIdJobFoto());
		// Ripristino lo stato di schedulato (ATTIVO) solo se nel frattempo non ho annullato
		// i parametri di schedulazione
		if (job.getCdSchedHour() != null) {
		    job.setTiStatoTimer(ATTIVO);
		}
	    }
	}
    }

    public BaseRow getInfoJobFotoRowBean() {
	// Recupero la data di ultima foto
	Date dataLastFoto = helper.getDataLastFotoJob();
	// Recupero il numero totale di job presenti nella foto
	int numJobFoto = helper.getNumJobFoto();
	// Recupero il numero di job attivi presenti nella foto
	int numJobAttivi = helper.getNumJobFotoAttivi();
	// 1) Recupero il numero di job "nuovi" in PIG_DEC_JOB (e non in foto)
	// 2) Recupero il numero di job in foto, eliminati da PIG_DEC_JOB
	int[] numJob = helper.getNumJobRimossiPresenti();
	int numJobNuovi = numJob[0];
	int numJobSoloFoto = numJob[1];
	BaseRow row = new BaseRow();
	row.setBigDecimal("ni_tot_job_presenti2", BigDecimal.valueOf(numJobFoto));
	row.setBigDecimal("ni_tot_job_attivi2", BigDecimal.valueOf(numJobAttivi));
	row.setBigDecimal("ni_tot_job_disattivi2", BigDecimal.valueOf(numJobFoto - numJobAttivi));
	row.setBigDecimal("ni_tot_job_nuovi2", BigDecimal.valueOf(numJobNuovi));
	row.setBigDecimal("ni_tot_job_solo_foto", BigDecimal.valueOf(numJobSoloFoto));
	row.setTimestamp("last_job_foto", new Timestamp(dataLastFoto.getTime()));
	return row;
    }

    public Object[] getInfoJobFotoNomiJobRowBean() {
	Object[] nmJobObj = helper.getNomiJobRimossiPresenti();
	return nmJobObj;
    }

    public BaseTable getAmbitoJob() {
	List<String> ambitoList = helper.getAmbitoJob();
	BaseTable ambitoTableBean = new BaseTable();
	try {
	    for (String ambito : ambitoList) {
		BaseRow ambitoRowBean = new BaseRow();
		ambitoRowBean.setString("nm_ambito", ambito);
		ambitoTableBean.add(ambitoRowBean);
	    }
	} catch (Exception e) {
	    LOGGER.error(e.getMessage(), e);
	}
	return ambitoTableBean;
    }

    public BaseRow getInfoJobRowBean() {
	Object[] obj = helper.getInfoJob();
	Long numTot = (Long) obj[0];
	Long numTot2 = (Long) obj[1];
	BaseRow row = new BaseRow();
	row.setTimestamp("dt_stato_job", new Timestamp(new Date().getTime()));
	row.setBigDecimal("ni_tot_job_presenti", BigDecimal.valueOf(numTot));
	row.setBigDecimal("ni_tot_job_attivi", BigDecimal.valueOf(numTot2));
	row.setBigDecimal("ni_tot_job_disattivi", BigDecimal.valueOf(numTot - numTot2));
	return row;
    }

    public BaseTable getPigDecJobTableBean(GestioneJobForm.GestioneJobRicercaFiltri filtri)
	    throws EMFError {
	BaseTable jobTableBean = new BaseTable();
	List<Object[]> listaJob = helper.getPigDecJobList(filtri.getNm_ambito().parse(),
		filtri.getDs_job().parse(), filtri.getTi_stato_job().parse());
	try {
	    if (listaJob != null && !listaJob.isEmpty()) {
		for (Object[] job : listaJob) {
		    boolean attivo = false;
		    BaseRow row = new BaseRow();
		    row.setString("nm_job", ((String) job[0]));
		    row.setString("ds_job", (String) job[1]);
		    // TODO: LOG
		    if (job[2] != null) {
			row.setTimestamp("dt_prossima_attivazione",
				new Timestamp(((Date) job[2]).getTime()));
			if ((String) job[9] == null || (((String) job[9]).equals(ATTIVO)
				|| ((String) job[9]).equals("INATTIVO"))) {
			    attivo = true;
			}
		    }
		    // Data ultima esecuzione
		    if (job[4] != null) {
			row.setTimestamp("dt_ultima_esecuzione",
				new Timestamp(((Date) job[4]).getTime()));
		    }

		    if (attivo) {
			row.setString("stato_job", ATTIVO);
			row.setString("operazione_job_start", "0");
			row.setString("operazione_job_single", "0");
			row.setString("operazione_job_stop", "1");
		    } else if (job[5] != null && ((Character) job[5]).toString().equals("1")) {
			row.setString("stato_job", "IN_ESECUZIONE");
			row.setString("operazione_job_start", "0");
			row.setString("operazione_job_stop", "0");
			row.setString("operazione_job_single", "0");
		    }
		    // Se è in ESECUZIONE_SINGOLA
		    else if (((String) job[9] != null // ti_stato_timer
			    && ((String) job[9]).equals("ESECUZIONE_SINGOLA"))) {
			row.setString("stato_job", DISATTIVO);
			row.setString("operazione_job_stop", "1");
			row.setString("operazione_job_single", "0");
			row.setString("operazione_job_start", "0");
		    } else {
			row.setString("stato_job", DISATTIVO);
			row.setString("operazione_job_stop", "0");
			row.setString("operazione_job_single", "1");
			row.setString("operazione_job_start", "1");
		    }

		    if (((String) job[10]).equals("NO_TIMER")) {
			row.setString("operazione_job_start", "0");
			row.setString("operazione_job_stop", "0");
		    }
		    row.setString("ti_sched_job", (String) job[10]);

		    // Ultima esecuzione OK
		    if (job[6] != null) {
			row.setString("last_exec_ok", ((Character) job[6]).toString());
		    }

		    row.setString("job_selezionati", "0");

		    row.setString("nm_ambito", (String) job[7]);
		    row.setBigDecimal("ni_ord_exec", (BigDecimal) job[8]);

		    jobTableBean.add(row);
		}
	    }
	} catch (Exception e) {
	    LOGGER.error(e.getMessage(), e);
	}

	return jobTableBean;
    }

    public BaseTable getPigDecJobTableBeanPerAmm() {
	List<Object[]> listaJob = helper.getPigDecJobListPerAmm();
	return populateBaseTableJob(listaJob);
    }

    public BaseTable getPigDecJobFotoTableBeanPerAmm() {
	List<Object[]> listaJob = helper.getPigDecJobFotoListPerAmm();
	return populateBaseTableJobFoto(listaJob);
    }

    public BaseTable populateBaseTableJob(List<Object[]> listaJob) {
	BaseTable jobTableBean = new BaseTable();
	try {
	    if (listaJob != null && !listaJob.isEmpty()) {
		for (Object[] job : listaJob) {
		    boolean attivo = false;
		    BaseRow row = new BaseRow();
		    row.setString("nm_job", ((String) job[0]));
		    row.setString("ds_job", (String) job[1]);
		    if (job[2] != null) {
			row.setTimestamp("dt_prossima_attivazione",
				new Timestamp(((Date) job[2]).getTime()));
			if ((String) job[6] == null || (((String) job[6]).equals(ATTIVO)
				|| ((String) job[6]).equals(DISATTIVO))) {
			    attivo = true;
			}
		    }
		    // Data ultima esecuzione
		    if (job[4] != null) {
			row.setTimestamp("dt_ultima_esecuzione",
				new Timestamp(((Date) job[4]).getTime()));
		    }

		    if (attivo) {
			row.setString("stato_job", ATTIVO);
		    } else if (job[5] != null && ((Character) job[5]).toString().equals("1")) {
			row.setString("stato_job", "IN_ESECUZIONE");

		    } else {
			row.setString("stato_job", DISATTIVO);

		    }

		    jobTableBean.add(row);
		}
	    }
	} catch (Exception e) {
	    LOGGER.error(e.getMessage(), e);
	}

	return jobTableBean;
    }

    public BaseTable populateBaseTableJobFoto(List<Object[]> listaJob) {
	BaseTable jobTableBean = new BaseTable();
	try {
	    if (listaJob != null && !listaJob.isEmpty()) {
		for (Object[] job : listaJob) {
		    boolean attivo = false;
		    BaseRow row = new BaseRow();
		    row.setString("nm_job", ((String) job[0]));
		    row.setString("ds_job", (String) job[1]);
		    if (job[2] != null) {
			row.setTimestamp("dt_prossima_attivazione",
				new Timestamp(((Date) job[2]).getTime()));
			if ((String) job[6] == null || (((String) job[6]).equals(ATTIVO)
				|| ((String) job[6]).equals(DISATTIVO))) {
			    attivo = true;
			}
		    }
		    // Data ultima esecuzione
		    if (job[4] != null) {
			row.setTimestamp("dt_ultima_esecuzione",
				new Timestamp(((Date) job[4]).getTime()));
		    }

		    if (attivo) {
			row.setString("stato_job", ATTIVO);

		    } else if (job[5] != null && ((Character) job[5]).toString().equals("1")) {
			row.setString("stato_job", DISATTIVO);

		    } else {
			row.setString("stato_job", DISATTIVO);

		    }

		    jobTableBean.add(row);
		}
	    }
	} catch (Exception e) {
	    LOGGER.error(e.getMessage(), e);
	}

	return jobTableBean;
    }

    public boolean isNoTimerJob(String nmJob) {
	return helper.getPigDecJobByName(nmJob).getTiSchedJob().equals("NO_TIMER");
    }

    public String getDsJob(String nmJob) {
	return helper.getPigDecJobByName(nmJob).getDsJob();
    }

}
