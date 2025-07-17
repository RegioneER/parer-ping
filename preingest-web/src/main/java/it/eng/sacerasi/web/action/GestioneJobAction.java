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

package it.eng.sacerasi.web.action;

import it.eng.parer.jboss.timer.service.JbossTimerEjb;
import it.eng.sacerasi.common.Constants;
import it.eng.sacerasi.slite.gen.Application;
import it.eng.sacerasi.slite.gen.action.GestioneJobAbstractAction;
import it.eng.sacerasi.slite.gen.form.GestioneJobForm.GestioneJobRicercaFiltri;
import it.eng.sacerasi.slite.gen.form.MonitoraggioForm;
import it.eng.sacerasi.slite.gen.viewbean.MonVVisLastSchedJobRowBean;
import it.eng.sacerasi.web.ejb.GestioneJobEjb;
import it.eng.sacerasi.web.helper.ConfigurationHelper;
import it.eng.sacerasi.web.helper.MonitoraggioHelper;
import it.eng.sacerasi.web.util.ComboGetter;
import it.eng.spagoCore.error.EMFError;
import it.eng.spagoLite.db.base.BaseTableInterface;
import it.eng.spagoLite.db.base.row.BaseRow;
import it.eng.spagoLite.db.base.table.BaseTable;
import it.eng.spagoLite.db.oracle.decode.DecodeMap;
import it.eng.spagoLite.form.base.BaseForm;
import it.eng.spagoLite.form.fields.Field;
import it.eng.spagoLite.form.fields.Fields;
import it.eng.spagoLite.form.fields.SingleValueField;
import it.eng.spagoLite.form.fields.impl.Button;
import it.eng.spagoLite.form.fields.impl.CheckBox;
import it.eng.spagoLite.form.fields.impl.Input;
import it.eng.spagoLite.message.Message;
import it.eng.spagoLite.message.Message.MessageLevel;
import it.eng.spagoLite.message.MessageBox.ViewMode;
import it.eng.spagoLite.security.Secure;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;
import javax.ejb.EJB;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GestioneJobAction extends GestioneJobAbstractAction {

    public static final String FROM_SCHEDULAZIONI_JOB = "fromSchedulazioniJob";

    private Logger LOG = LoggerFactory.getLogger(GestioneJobAction.class.getName());

    @EJB(mappedName = "java:app/JbossTimerWrapper-ejb/JbossTimerEjb")
    private JbossTimerEjb jbossTimerEjb;

    @EJB(mappedName = "java:app/SacerAsync-ejb/MonitoraggioHelper")
    private MonitoraggioHelper monitoraggioHelper;

    @EJB(mappedName = "java:app/SacerAsync-ejb/ConfigurationHelper")
    private ConfigurationHelper configurationHelper;

    @EJB(mappedName = "java:app/SacerAsync-ejb/GestioneJobEjb")
    private GestioneJobEjb gestioneJobEjb;

    private enum OPERAZIONE {
	START("lancio il timer"), ESECUZIONE_SINGOLA("esecuzione singola"), STOP("stop");

	protected String desc;

	OPERAZIONE(String desc) {
	    this.desc = desc;
	}

	public String description() {
	    return desc;
	}
    };

    /**
     * Returns the activation date of job otherwise <code>null</code>
     *
     * @param jobName the job name
     *
     * @return
     *
     * @throws EMFError
     */
    private Timestamp getActivationDateJob(String jobName) throws EMFError {
	Timestamp res = null;

	MonVVisLastSchedJobRowBean rb = monitoraggioHelper.getMonVVisLastSchedJob(jobName);
	if (rb.getFlJobAttivo() != null) {
	    if (rb.getFlJobAttivo().equals("1")) {
		res = rb.getDtRegLogJobIni();
	    }
	}
	return res;
    }

    @Override
    public void initOnClick() throws EMFError {
	// FIXME: sul csv, a differenza di sacer e iam la gestione dei job non è agganciata
	// all'initOnClick
	gestioneJob();
    }

    public void process() throws EMFError {
	// forwardToPublisher(getDefaultPublsherName());
    }

    @Override
    public String getControllerName() {
	return Application.Actions.GESTIONE_JOB;
    }

    @Override
    protected String getDefaultPublsherName() {
	return Application.Publisher.GESTIONE_JOB;
    }

    public void changePwd() throws EMFError {
	redirectToAction("Login.html", "?operation=fwdChangePwd", null);
    }

    private String getNomeApplicazione() {
	String nomeApplicazione = configurationHelper.getParamApplicApplicationName();
	return nomeApplicazione;
    }

    @Secure(action = "Menu.AmministrazioneSistema.GestioneJob")
    public void gestioneJob() throws EMFError {
	String nomeApplicazione = getNomeApplicazione();

	getUser().getMenu().reset();
	getUser().getMenu().select("Menu.AmministrazioneSistema.GestioneJob");

	// <editor-fold defaultstate="collapsed" desc="UI Gestione job per Preparazione XML Sacer">
	Timestamp dataAttivazioneJob = getActivationDateJob(Constants.NomiJob.PREPARA_XML.name());
	StatoJob preparaXml = new StatoJob(Constants.NomiJob.PREPARA_XML.name(),
		getForm().getPreparaXMLSACER().getFl_data_accurata(),
		getForm().getPreparaXMLSACER().getStartPreparaXMLSACER(),
		getForm().getPreparaXMLSACER().getStartOncePreparaXMLSACER(),
		getForm().getPreparaXMLSACER().getStopPreparaXMLSACER(),
		getForm().getPreparaXMLSACER().getDt_prossima_attivazione(),
		getForm().getPreparaXMLSACER().getAttivo(),
		getForm().getPreparaXMLSACER().getDt_reg_log_job_ini(), dataAttivazioneJob);

	gestisciStatoJob(preparaXml);

	// </editor-fold>
	// <editor-fold defaultstate="collapsed" desc="UI Gestione job per Producer coda
	// versamento">
	dataAttivazioneJob = getActivationDateJob(Constants.NomiJob.PRODUCER_CODA_VERS.name());
	StatoJob producerCodaVers = new StatoJob(Constants.NomiJob.PRODUCER_CODA_VERS.name(),
		getForm().getProducerCodaVersamento().getFl_data_accurata(),
		getForm().getProducerCodaVersamento().getStartProducerCodaVersamento(),
		getForm().getProducerCodaVersamento().getStartOnceProducerCodaVersamento(),
		getForm().getProducerCodaVersamento().getStopProducerCodaVersamento(),
		getForm().getProducerCodaVersamento().getDt_prossima_attivazione(),
		getForm().getProducerCodaVersamento().getAttivo(),
		getForm().getProducerCodaVersamento().getDt_reg_log_job_ini(), dataAttivazioneJob);

	gestisciStatoJob(producerCodaVers);

	// </editor-fold>
	// <editor-fold defaultstate="collapsed" desc="UI Gestione job per Recupera errori in coda">
	dataAttivazioneJob = getActivationDateJob(Constants.NomiJob.RECUPERA_ERRORI_IN_CODA.name());
	StatoJob recuperaErrori = new StatoJob(Constants.NomiJob.RECUPERA_ERRORI_IN_CODA.name(),
		getForm().getRecuperaErroriCoda().getFl_data_accurata(),
		getForm().getRecuperaErroriCoda().getStartRecuperaErroriCoda(),
		getForm().getRecuperaErroriCoda().getStartOnceRecuperaErroriCoda(),
		getForm().getRecuperaErroriCoda().getStopRecuperaErroriCoda(),
		getForm().getRecuperaErroriCoda().getDt_prossima_attivazione(),
		getForm().getRecuperaErroriCoda().getAttivo(),
		getForm().getRecuperaErroriCoda().getDt_reg_log_job_ini(), dataAttivazioneJob);

	gestisciStatoJob(recuperaErrori);

	// </editor-fold>
	// <editor-fold defaultstate="collapsed" desc="UI Gestione job per Recupera versamenti in
	// error">
	dataAttivazioneJob = getActivationDateJob(Constants.NomiJob.RECUPERA_VERS_ERR.name());
	StatoJob recuperaVersamentiErrati = new StatoJob(Constants.NomiJob.RECUPERA_VERS_ERR.name(),
		getForm().getRecuperaVersErr().getFl_data_accurata(),
		getForm().getRecuperaVersErr().getStartRecuperaVersErr(),
		getForm().getRecuperaVersErr().getStartOnceRecuperaVersErr(),
		getForm().getRecuperaVersErr().getStopRecuperaVersErr(),
		getForm().getRecuperaVersErr().getDt_prossima_attivazione(),
		getForm().getRecuperaVersErr().getAttivo(),
		getForm().getRecuperaVersErr().getDt_reg_log_job_ini(), dataAttivazioneJob);

	gestisciStatoJob(recuperaVersamentiErrati);

	// </editor-fold>
	// <editor-fold defaultstate="collapsed" desc="UI Gestione job per Recupero da Sacer">
	dataAttivazioneJob = getActivationDateJob(Constants.NomiJob.RECUPERO_SACER.name());
	StatoJob recuperoSacer = new StatoJob(Constants.NomiJob.RECUPERO_SACER.name(),
		getForm().getRecuperoSACER().getFl_data_accurata(),
		getForm().getRecuperoSACER().getStartRecuperoSACER(),
		getForm().getRecuperoSACER().getStartOnceRecuperoSACER(),
		getForm().getRecuperoSACER().getStopRecuperoSACER(),
		getForm().getRecuperoSACER().getDt_prossima_attivazione(),
		getForm().getRecuperoSACER().getAttivo(),
		getForm().getRecuperoSACER().getDt_reg_log_job_ini(), dataAttivazioneJob);

	gestisciStatoJob(recuperoSacer);

	// </editor-fold>
	// <editor-fold defaultstate="collapsed" desc="UI Gestione job per Producer coda verifica
	// hash">
	dataAttivazioneJob = getActivationDateJob(
		Constants.NomiJob.PRODUCER_CODA_VERIFICA_H.name());
	StatoJob producerCodaVerificaH = new StatoJob(
		Constants.NomiJob.PRODUCER_CODA_VERIFICA_H.name(),
		getForm().getProducerCodaVerificaH().getFl_data_accurata(),
		getForm().getProducerCodaVerificaH().getStartProducerCodaVerificaH(),
		getForm().getProducerCodaVerificaH().getStartOnceProducerCodaVerificaH(),
		getForm().getProducerCodaVerificaH().getStopProducerCodaVerificaH(),
		getForm().getProducerCodaVerificaH().getDt_prossima_attivazione(),
		getForm().getProducerCodaVerificaH().getAttivo(),
		getForm().getProducerCodaVerificaH().getDt_reg_log_job_ini(), dataAttivazioneJob);

	gestisciStatoJob(producerCodaVerificaH);

	// </editor-fold>
	// <editor-fold defaultstate="collapsed" desc="UI Gestione job per Allineamento
	// organizzazioni">
	// nextActivation = allineamentoOrganizzazioniTimer.getNextElaboration(nomeApplicazione);
	dataAttivazioneJob = getActivationDateJob(
		Constants.NomiJob.ALLINEAMENTO_ORGANIZZAZIONI.name());
	StatoJob allineamentoOrganizazzioni = new StatoJob(
		Constants.NomiJob.ALLINEAMENTO_ORGANIZZAZIONI.name(),
		getForm().getAllineamentoOrganizzazioni().getFl_data_accurata(),
		getForm().getAllineamentoOrganizzazioni().getStartAllineamentoOrganizzazioni(),
		getForm().getAllineamentoOrganizzazioni().getStartOnceAllineamentoOrganizzazioni(),
		getForm().getAllineamentoOrganizzazioni().getStopAllineamentoOrganizzazioni(),
		getForm().getAllineamentoOrganizzazioni().getDt_prossima_attivazione(),
		getForm().getAllineamentoOrganizzazioni().getAttivo(),
		getForm().getAllineamentoOrganizzazioni().getDt_reg_log_job_ini(),
		dataAttivazioneJob);

	gestisciStatoJob(allineamentoOrganizazzioni);

	// </editor-fold>
	// <editor-fold defaultstate="collapsed" desc="UI Gestione job per Inizializzazione log">
	dataAttivazioneJob = getActivationDateJob(
		it.eng.parer.sacerlog.job.Constants.NomiJob.INIZIALIZZAZIONE_LOG.name() + "_"
			+ nomeApplicazione);
	StatoJob inizializzazioneLog = new StatoJob(
		it.eng.parer.sacerlog.job.Constants.NomiJob.INIZIALIZZAZIONE_LOG.name(),
		getForm().getInizializzazioneLog().getFl_data_accurata(), null,
		getForm().getInizializzazioneLog().getStartOnceInizializzazioneLog(), null,
		getForm().getInizializzazioneLog().getDt_prossima_attivazione(),
		getForm().getInizializzazioneLog().getAttivo(),
		getForm().getInizializzazioneLog().getDt_reg_log_job_ini(), dataAttivazioneJob);

	gestisciStatoJob(inizializzazioneLog);

	// </editor-fold>
	// <editor-fold defaultstate="collapsed" desc="UI Gestione job per l'allineamento dei log">
	//////////////////////
	// ALLINEAMENTO LOG //
	//////////////////////
	// Setto le informazioni sul job "ALLINEAMENTO LOG"
	dataAttivazioneJob = getActivationDateJob(
		it.eng.parer.sacerlog.job.Constants.NomiJob.ALLINEAMENTO_LOG.name() + "_"
			+ nomeApplicazione);
	StatoJob allineamentoLog = new StatoJob(
		it.eng.parer.sacerlog.job.Constants.NomiJob.ALLINEAMENTO_LOG.name(),
		getForm().getAllineamentoLog().getFl_data_accurata(),
		getForm().getAllineamentoLog().getStartAllineamentoLog(),
		getForm().getAllineamentoLog().getStartOnceAllineamentoLog(),
		getForm().getAllineamentoLog().getStopAllineamentoLog(),
		getForm().getAllineamentoLog().getDt_prossima_attivazione(),
		getForm().getAllineamentoLog().getAttivo(),
		getForm().getAllineamentoLog().getDt_reg_log_job_ini(), dataAttivazioneJob);

	gestisciStatoJob(allineamentoLog);
	// </editor-fold>
	// <editor-fold defaultstate="collapsed" desc="UI Gestione job per Esegui trasformazione">
	dataAttivazioneJob = getActivationDateJob(Constants.NomiJob.ESEGUI_TRASFORMAZIONE.name());
	StatoJob eseguiTrasformazione = new StatoJob(Constants.NomiJob.ESEGUI_TRASFORMAZIONE.name(),
		getForm().getEseguiTrasformazione().getFl_data_accurata(),
		getForm().getEseguiTrasformazione().getStartEseguiTrasformazione(),
		getForm().getEseguiTrasformazione().getStartOnceEseguiTrasformazione(),
		getForm().getEseguiTrasformazione().getStopEseguiTrasformazione(),
		getForm().getEseguiTrasformazione().getVt_prossima_attivazione(),
		getForm().getEseguiTrasformazione().getAttivo(),
		getForm().getEseguiTrasformazione().getVt_reg_log_job_ini(), dataAttivazioneJob);

	gestisciStatoJob(eseguiTrasformazione);
	// </editor-fold>
	// <editor-fold defaultstate="collapsed" desc="UI Gestione job per Invia oggetti generati a
	// PING">
	dataAttivazioneJob = getActivationDateJob(
		Constants.NomiJob.INVIA_OGGETTI_GENERATI_A_PING.name());
	StatoJob inviaOggettiGenerati = new StatoJob(
		Constants.NomiJob.INVIA_OGGETTI_GENERATI_A_PING.name(),
		getForm().getInviaOggettiGeneratiAPing().getFl_data_accurata(),
		getForm().getInviaOggettiGeneratiAPing().getStartInviaOggettiGeneratiAPing(),
		getForm().getInviaOggettiGeneratiAPing().getStartOnceInviaOggettiGeneratiAPing(),
		getForm().getInviaOggettiGeneratiAPing().getStopInviaOggettiGeneratiAPing(),
		getForm().getInviaOggettiGeneratiAPing().getVt_prossima_attivazione(),
		getForm().getInviaOggettiGeneratiAPing().getAttivo(),
		getForm().getInviaOggettiGeneratiAPing().getVt_reg_log_job_ini(),
		dataAttivazioneJob);

	gestisciStatoJob(inviaOggettiGenerati);
	// </editor-fold>
	// <editor-fold defaultstate="collapsed" desc="UI Gestione job per Invio strumenti
	// urbanistici">
	// nextActivation = allineamentoOrganizzazioniTimer.getNextElaboration(nomeApplicazione);
	dataAttivazioneJob = getActivationDateJob(
		Constants.NomiJob.INVIO_STRUMENTI_URBANISTICI.name());
	StatoJob invioStrumentiUrbanistici = new StatoJob(
		Constants.NomiJob.INVIO_STRUMENTI_URBANISTICI.name(),
		getForm().getInvioSU().getFl_data_accurata(),
		getForm().getInvioSU().getStartInvioSU(),
		getForm().getInvioSU().getStartOnceInvioSU(),
		getForm().getInvioSU().getStopInvioSU(),
		getForm().getInvioSU().getDt_prossima_attivazione(),
		getForm().getInvioSU().getAttivo(), getForm().getInvioSU().getDt_reg_log_job_ini(),
		dataAttivazioneJob);

	gestisciStatoJob(invioStrumentiUrbanistici);

	// </editor-fold>
	// <editor-fold defaultstate="collapsed" desc="UI Gestione job per Invio sisma">
	// nextActivation = allineamentoOrganizzazioniTimer.getNextElaboration(nomeApplicazione);
	dataAttivazioneJob = getActivationDateJob(Constants.NomiJob.INVIO_SISMA.name());
	StatoJob invioSisma = new StatoJob(Constants.NomiJob.INVIO_SISMA.name(),
		getForm().getInvioSisma().getFl_data_accurata(),
		getForm().getInvioSisma().getStartInvioSisma(),
		getForm().getInvioSisma().getStartOnceInvioSisma(),
		getForm().getInvioSisma().getStopInvioSisma(),
		getForm().getInvioSisma().getDt_prossima_attivazione(),
		getForm().getInvioSisma().getAttivo(),
		getForm().getInvioSisma().getDt_reg_log_job_ini(), dataAttivazioneJob);

	gestisciStatoJob(invioSisma);
	// </editor-fold>
	forwardToPublisher(Application.Publisher.GESTIONE_JOB);
    }

    /**
     * Cuore della classe: qui è definita la logica STANDARD degli stati dei job a livello di
     * <b>interfaccia web<b>. Per i job che devono implementare una logica non standard non è
     * consigliabile utilizzare questo metodo. Si è cercato di mantenere una simmetria tra
     * esposizione/inibizione dei controlli grafici.
     *
     * @param statoJob Rappresentazione dello stato <b>a livello di interfaccia grafica</b> del job.
     *
     * @throws EMFError in caso di errore generale
     */
    private void gestisciStatoJob(StatoJob statoJob) throws EMFError {
	// se non è ancora passato un minuto da quando è stato premuto un pulsante non posso fare
	// nulla
	boolean operazioneInCorso = jbossTimerEjb.isEsecuzioneInCorso(statoJob.getNomeJob());

	statoJob.getFlagDataAccurata().setViewMode();
	statoJob.getFlagDataAccurata()
		.setValue("L'operazione richiesta verrà effettuata entro il prossimo minuto.");
	statoJob.getStart().setHidden(operazioneInCorso);
	statoJob.getEsecuzioneSingola().setHidden(operazioneInCorso);
	statoJob.getStop().setHidden(operazioneInCorso);
	statoJob.getDataProssimaAttivazione().setHidden(operazioneInCorso);

	statoJob.getFlagDataAccurata().setHidden(!operazioneInCorso);
	if (operazioneInCorso) {
	    return;
	}

	// Posso operare sulla pagina
	Date nextActivation = jbossTimerEjb.getDataProssimaAttivazione(statoJob.getNomeJob());
	boolean dataAccurata = jbossTimerEjb
		.isDataProssimaAttivazioneAccurata(statoJob.getNomeJob());
	DateFormat formato = new SimpleDateFormat(Constants.DATE_FORMAT_JOB);

	/*
	 * Se il job è già schedulato o in esecuzione singola nascondo il pulsante Start/esecuzione
	 * singola, mostro Stop e visualizzo la prossima attivazione. Viceversa se è fermo mostro
	 * Start e nascondo Stop
	 */
	if (nextActivation != null) {
	    statoJob.getStart().setViewMode();
	    statoJob.getEsecuzioneSingola().setViewMode();
	    statoJob.getStop().setEditMode();
	    statoJob.getDataProssimaAttivazione().setValue(formato.format(nextActivation));
	} else {
	    statoJob.getStart().setEditMode();
	    statoJob.getEsecuzioneSingola().setEditMode();
	    statoJob.getStop().setViewMode();
	    statoJob.getDataProssimaAttivazione().setValue(null);
	}

	boolean flagHidden = nextActivation == null || dataAccurata;
	// se la data c'è ma non è accurata non visualizzare la "data prossima attivazione"
	statoJob.getDataProssimaAttivazione().setHidden(!flagHidden);

	if (statoJob.getDataAttivazione() != null) {
	    statoJob.getCheckAttivo().setChecked(true);
	    statoJob.getDataRegistrazioneJob()
		    .setValue(formato.format(new Date(statoJob.getDataAttivazione().getTime())));
	} else {
	    statoJob.getCheckAttivo().setChecked(false);
	    statoJob.getDataRegistrazioneJob().setValue(null);
	}
    }

    // <editor-fold defaultstate="collapsed" desc="UI Classe che mappa lo stato dei job">
    /**
     * Astrazione dei componenti della pagina utilizzati per i "box" dei job.
     *
     * @author Snidero_L
     */
    private static final class StatoJob {

	private final String nomeJob;
	private final Input<String> flagDataAccurata;
	private final Button<String> start;
	private final Button<String> esecuzioneSingola;
	private final Button<String> stop;
	private final Input<Timestamp> dataProssimaAttivazione;
	private final CheckBox<String> checkAttivo;
	private final Input<Timestamp> dataRegistrazioneJob;
	private final Timestamp dataAttivazione;

	// Mi serve per evitare una null pointer Exception
	private static final Button<String> NULL_BUTTON = new Button<String>(null, "EMPTY_BUTTON",
		"Pulsante vuoto", null, null, null, false, true, true, false);

	public StatoJob(String nomeJob, Input<String> flagDataAccurata, Button<String> start,
		Button<String> esecuzioneSingola, Button<String> stop,
		Input<Timestamp> dataProssimaAttivazione, CheckBox<String> checkAttivo,
		Input<Timestamp> dataRegistrazioneJob, Timestamp dataAttivazione) {
	    this.nomeJob = nomeJob;
	    this.flagDataAccurata = flagDataAccurata;
	    this.start = start;
	    this.esecuzioneSingola = esecuzioneSingola;
	    this.stop = stop;
	    this.dataProssimaAttivazione = dataProssimaAttivazione;
	    this.checkAttivo = checkAttivo;
	    this.dataRegistrazioneJob = dataRegistrazioneJob;
	    this.dataAttivazione = dataAttivazione;
	}

	public String getNomeJob() {
	    return nomeJob;
	}

	public Input<String> getFlagDataAccurata() {
	    return flagDataAccurata;
	}

	public Button<String> getStart() {
	    if (start == null) {
		return NULL_BUTTON;
	    }
	    return start;
	}

	public Button<String> getEsecuzioneSingola() {
	    return esecuzioneSingola;
	}

	public Button<String> getStop() {
	    if (stop == null) {
		return NULL_BUTTON;
	    }
	    return stop;
	}

	public Input<Timestamp> getDataProssimaAttivazione() {
	    return dataProssimaAttivazione;
	}

	public CheckBox<String> getCheckAttivo() {
	    return checkAttivo;
	}

	public Input<Timestamp> getDataRegistrazioneJob() {
	    return dataRegistrazioneJob;
	}

	public Timestamp getDataAttivazione() {
	    return dataAttivazione;
	}
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Funzioni non implementate">
    @Override
    public void loadDettaglio() throws EMFError {
	throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void undoDettaglio() throws EMFError {
	throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void saveDettaglio() throws EMFError {
	throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void dettaglioOnClick() throws EMFError {
	throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void elencoOnClick() throws EMFError {
	goBack();
    }

    @Override
    public void insertDettaglio() throws EMFError {
	throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void update(Fields<Field> fields) throws EMFError {
	throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void delete(Fields<Field> fields) throws EMFError {
	throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void reloadAfterGoBack(String publisherName) {
	try {
	    ricercaGestioneJob();
	} catch (EMFError ex) {
	    LOG.error(ex.getDescription());
	    getMessageBox().addError("Errore durante il caricamento dell'elenco dei JOB");
	}
	// getSession().removeAttribute("backToRicercaJob");
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Methods end manage PreparaXmlSacer schedulation">
    @Override
    public void startPreparaXMLSACER() throws EMFError {
	esegui(Constants.NomiJob.PREPARA_XML.name(), "Prepara xml", null, OPERAZIONE.START);
    }

    @Override
    public void startOncePreparaXMLSACER() throws EMFError {
	esegui(Constants.NomiJob.PREPARA_XML.name(), "Prepara xml", null,
		OPERAZIONE.ESECUZIONE_SINGOLA);
    }

    @Override
    public void stopPreparaXMLSACER() throws EMFError {
	esegui(Constants.NomiJob.PREPARA_XML.name(), "Prepara xml", null, OPERAZIONE.STOP);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Methods end manage ProducerCodaVersamento
    // schedulation">
    @Override
    public void startProducerCodaVersamento() throws EMFError {
	esegui(Constants.NomiJob.PRODUCER_CODA_VERS.name(), "Producer coda versamento", null,
		OPERAZIONE.START);
    }

    @Override
    public void startOnceProducerCodaVersamento() throws EMFError {
	esegui(Constants.NomiJob.PRODUCER_CODA_VERS.name(), "Producer coda versamento", null,
		OPERAZIONE.ESECUZIONE_SINGOLA);
    }

    @Override
    public void stopProducerCodaVersamento() throws EMFError {
	esegui(Constants.NomiJob.PRODUCER_CODA_VERS.name(), "Producer coda versamento", null,
		OPERAZIONE.STOP);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Methods end manage RecuperoErroriCoda
    // schedulation">
    @Override
    public void startRecuperaErroriCoda() throws EMFError {
	esegui(Constants.NomiJob.RECUPERA_ERRORI_IN_CODA.name(), "Recupero errori in coda", null,
		OPERAZIONE.START);
    }

    @Override
    public void startOnceRecuperaErroriCoda() throws EMFError {
	esegui(Constants.NomiJob.RECUPERA_ERRORI_IN_CODA.name(), "Recupero errori in coda", null,
		OPERAZIONE.ESECUZIONE_SINGOLA);
    }

    @Override
    public void stopRecuperaErroriCoda() throws EMFError {
	esegui(Constants.NomiJob.RECUPERA_ERRORI_IN_CODA.name(), "Recupero errori in coda", null,
		OPERAZIONE.STOP);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Methods end manage RecuperoSacer schedulation">
    @Override
    public void startRecuperoSACER() throws EMFError {
	esegui(Constants.NomiJob.RECUPERO_SACER.name(), "Recupero sacer", null, OPERAZIONE.START);
    }

    @Override
    public void startOnceRecuperoSACER() throws EMFError {
	esegui(Constants.NomiJob.RECUPERO_SACER.name(), "Recupero sacer", null,
		OPERAZIONE.ESECUZIONE_SINGOLA);
    }

    @Override
    public void stopRecuperoSACER() throws EMFError {
	esegui(Constants.NomiJob.RECUPERO_SACER.name(), "Recupero sacer", null, OPERAZIONE.STOP);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Methods end manage RecuperaVersamentoErrato
    // schedulation">
    @Override
    public void startRecuperaVersErr() throws EMFError {
	esegui(Constants.NomiJob.RECUPERA_VERS_ERR.name(), "Recupera vers err", null,
		OPERAZIONE.START);
    }

    @Override
    public void startOnceRecuperaVersErr() throws EMFError {
	esegui(Constants.NomiJob.RECUPERA_VERS_ERR.name(), "Recupera vers err", null,
		OPERAZIONE.ESECUZIONE_SINGOLA);
    }

    @Override
    public void stopRecuperaVersErr() throws EMFError {
	esegui(Constants.NomiJob.RECUPERA_VERS_ERR.name(), "Recupera vers err", null,
		OPERAZIONE.STOP);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Methods end manage ProducerCodaVerifica
    // schedulation">
    @Override
    public void startProducerCodaVerificaH() throws EMFError {
	esegui(Constants.NomiJob.PRODUCER_CODA_VERIFICA_H.name(), "Producer Coda Verifica Hash",
		null, OPERAZIONE.START);
    }

    @Override
    public void startOnceProducerCodaVerificaH() throws EMFError {
	esegui(Constants.NomiJob.PRODUCER_CODA_VERIFICA_H.name(), "Producer Coda Verifica Hash",
		null, OPERAZIONE.ESECUZIONE_SINGOLA);
    }

    @Override
    public void stopProducerCodaVerificaH() throws EMFError {
	esegui(Constants.NomiJob.PRODUCER_CODA_VERIFICA_H.name(), "Producer Coda Verifica Hash",
		null, OPERAZIONE.STOP);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Methods end manage AllineamentoOrganizzazioni
    // schedulation">
    @Override
    public void startAllineamentoOrganizzazioni() throws EMFError {
	esegui(Constants.NomiJob.ALLINEAMENTO_ORGANIZZAZIONI.name(), "Allineamento organizzazioni",
		null, OPERAZIONE.START);
    }

    @Override
    public void startOnceAllineamentoOrganizzazioni() throws EMFError {
	esegui(Constants.NomiJob.ALLINEAMENTO_ORGANIZZAZIONI.name(), "Allineamento organizzazioni",
		null, OPERAZIONE.ESECUZIONE_SINGOLA);
    }

    @Override
    public void stopAllineamentoOrganizzazioni() throws EMFError {
	esegui(Constants.NomiJob.ALLINEAMENTO_ORGANIZZAZIONI.name(), "Allineamento organizzazioni",
		null, OPERAZIONE.STOP);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Methods end manage Inizializzazione Log
    // schedulation">
    @Override
    public void startOnceInizializzazioneLog() throws EMFError {
	String nomeApplicazione = getNomeApplicazione();
	esegui(it.eng.parer.sacerlog.job.Constants.NomiJob.INIZIALIZZAZIONE_LOG.name(),
		"Inizializzazione Log", nomeApplicazione, OPERAZIONE.ESECUZIONE_SINGOLA);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Methods end manage Allineamento Log
    // schedulation">
    @Override
    public void startAllineamentoLog() throws EMFError {
	String nomeApplicazione = getNomeApplicazione();
	esegui(it.eng.parer.sacerlog.job.Constants.NomiJob.ALLINEAMENTO_LOG.name(),
		"Allineamento Log", nomeApplicazione, OPERAZIONE.START);
    }

    @Override
    public void startOnceAllineamentoLog() throws EMFError {
	String nomeApplicazione = getNomeApplicazione();
	esegui(it.eng.parer.sacerlog.job.Constants.NomiJob.ALLINEAMENTO_LOG.name(),
		"Allineamento Log", nomeApplicazione, OPERAZIONE.ESECUZIONE_SINGOLA);
    }

    @Override
    public void stopAllineamentoLog() throws EMFError {
	String nomeApplicazione = getNomeApplicazione();
	esegui(it.eng.parer.sacerlog.job.Constants.NomiJob.ALLINEAMENTO_LOG.name(),
		"Allineamento Log", nomeApplicazione, OPERAZIONE.STOP);
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Methodi per gestire Esegui trasformazione">
    @Override
    public void startEseguiTrasformazione() throws EMFError {
	esegui(Constants.NomiJob.ESEGUI_TRASFORMAZIONE.name(), "Esegui trasformazione", null,
		OPERAZIONE.START);
    }

    @Override
    public void startOnceEseguiTrasformazione() throws EMFError {
	esegui(Constants.NomiJob.ESEGUI_TRASFORMAZIONE.name(), "Esegui trasformazione", null,
		OPERAZIONE.ESECUZIONE_SINGOLA);
    }

    @Override
    public void stopEseguiTrasformazione() throws EMFError {
	esegui(Constants.NomiJob.ESEGUI_TRASFORMAZIONE.name(), "Esegui trasformazione", null,
		OPERAZIONE.STOP);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Methodi per gestire Invia oggetti generati a
    // PING">
    @Override
    public void startInviaOggettiGeneratiAPing() throws EMFError {
	esegui(Constants.NomiJob.INVIA_OGGETTI_GENERATI_A_PING.name(),
		"Invia oggetti generati a PING", null, OPERAZIONE.START);
    }

    @Override
    public void startOnceInviaOggettiGeneratiAPing() throws EMFError {
	esegui(Constants.NomiJob.INVIA_OGGETTI_GENERATI_A_PING.name(),
		"Invia oggetti generati a PING", null, OPERAZIONE.ESECUZIONE_SINGOLA);
    }

    @Override
    public void stopInviaOggettiGeneratiAPing() throws EMFError {
	esegui(Constants.NomiJob.INVIA_OGGETTI_GENERATI_A_PING.name(),
		"Invia oggetti generati a PING", null, OPERAZIONE.STOP);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Methods end manage Invio strumenti urbanistici
    // schedulation">
    @Override
    public void startInvioSU() throws EMFError {
	esegui(Constants.NomiJob.INVIO_STRUMENTI_URBANISTICI.name(), "Invio strumenti urbanistici",
		null, OPERAZIONE.START);
    }

    @Override
    public void startOnceInvioSU() throws EMFError {
	esegui(Constants.NomiJob.INVIO_STRUMENTI_URBANISTICI.name(), "Invio strumenti urbanistici",
		null, OPERAZIONE.ESECUZIONE_SINGOLA);
    }

    @Override
    public void stopInvioSU() throws EMFError {
	esegui(Constants.NomiJob.INVIO_STRUMENTI_URBANISTICI.name(), "Invio strumenti urbanistici",
		null, OPERAZIONE.STOP);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Methods end manage Invio sisma schedulation">
    @Override
    public void startInvioSisma() throws EMFError {
	esegui(Constants.NomiJob.INVIO_SISMA.name(), "Invio sisma", null, OPERAZIONE.START);
    }

    @Override
    public void startOnceInvioSisma() throws EMFError {
	esegui(Constants.NomiJob.INVIO_SISMA.name(), "Invio sisma", null,
		OPERAZIONE.ESECUZIONE_SINGOLA);
    }

    @Override
    public void stopInvioSisma() throws EMFError {
	esegui(Constants.NomiJob.INVIO_SISMA.name(), "Invio sisma", null, OPERAZIONE.STOP);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Esecuzione di un job STANDARD">

    /**
     * Esegui una delle seguenti operazioni:
     * <ul>
     * <li>{@link OPERAZIONE#START}</li>
     * <li>{@link OPERAZIONE#ESECUZIONE_SINGOLA}</li>
     * <li>{@link OPERAZIONE#STOP}</li>
     * </ul>
     *
     * @param nomeJob          nome del job
     * @param descrizioneJob   descrizione (che comparirà sul LOG) del job
     * @param nomeApplicazione nome dell'applicazione. <b>Obbligatorio per i job che elaborano i LOG
     *                         "PREMIS"</b>
     * @param operazione       una delle tre operazioni dell'enum
     *
     * @throws EMFError Errore di esecuzione
     */
    private void esegui(String nomeJob, String descrizioneJob, String nomeApplicazione,
	    OPERAZIONE operazione) throws EMFError {
	// Messaggio sul LOG di sistema
	StringBuilder info = new StringBuilder(descrizioneJob);
	info.append(": ").append(operazione.description()).append(" [").append(nomeJob);
	if (nomeApplicazione != null) {
	    info.append("_").append(nomeApplicazione);
	}
	info.append("]");
	LOG.info(info.toString());

	String message = "Errore durante la schedulazione del job";

	switch (operazione) {
	case START:
	    jbossTimerEjb.start(nomeJob, null);
	    message = descrizioneJob + ": job correttamente schedulato";
	    break;
	case ESECUZIONE_SINGOLA:
	    jbossTimerEjb.esecuzioneSingola(nomeJob, null);
	    message = descrizioneJob + ": job correttamente schedulato per esecuzione singola";
	    break;
	case STOP:
	    jbossTimerEjb.stop(nomeJob);
	    message = descrizioneJob + ": schedulazione job annullata";
	    break;
	}

	// Segnalo l'avvenuta operazione sul job
	getMessageBox().addMessage(new Message(MessageLevel.INF, message));
	getMessageBox().setViewMode(ViewMode.plain);
	// Risetto la pagina rilanciando l'initOnClick
	initOnClick();
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="NUOVA GESTIONE JOB">
    @Secure(action = "Menu.AmministrazioneSistema.GestioneJobRicerca")
    public void gestioneJobRicercaPage() throws EMFError {
	getUser().getMenu().reset();
	getUser().getMenu().select("Menu.AmministrazioneSistema.GestioneJobRicerca");
	getForm().getGestioneJobRicercaList().setTable(null);
	resetFiltriGestioneJobRicercaPage();
	popolaInformazioniJob();
	tabRicercaJobTabOnClick();
	getForm().getGestioneJobInfo().getSalvaFotoGestioneJob().setEditMode();
	getForm().getGestioneJobInfo().getDisabilitaAllJobs().setEditMode();
	getForm().getGestioneJobInfo2().getRipristinaFotoGestioneJob().setEditMode();
	getForm().getGestioneJobInfo().getRicaricaGestioneJob().setEditMode();
	getSession().removeAttribute("visualizzaRipristinaFoto");
	abilitaDisabilitaBottoniJob(
		!gestioneJobEjb.isPigDecJobFotoEmpty() && gestioneJobEjb.areAllJobsDisattivati(),
		getSession().getAttribute("fotoSalvata") != null);
	forwardToPublisher(Application.Publisher.GESTIONE_JOB_RICERCA);
    }

    public void resetFiltriGestioneJobRicercaPage() throws EMFError {
	getForm().getGestioneJobRicercaFiltri().setEditMode();
	getForm().getGestioneJobRicercaFiltri().reset();
	getForm().getGestioneJobRicercaList().setTable(null);
	BaseTable ambitoTableBean = gestioneJobEjb.getAmbitoJob();
	getForm().getGestioneJobRicercaFiltri().getNm_ambito().setDecodeMap(
		DecodeMap.Factory.newInstance(ambitoTableBean, "nm_ambito", "nm_ambito"));
	getForm().getGestioneJobRicercaFiltri().getTi_stato_job()
		.setDecodeMap(ComboGetter.getMappaTiStatoJob());
    }

    public String[] calcolaInformazioniJob() {
	BaseRow infoJobRowBean = gestioneJobEjb.getInfoJobRowBean();
	int niTotJobPresenti = infoJobRowBean.getBigDecimal("ni_tot_job_presenti") != null
		? infoJobRowBean.getBigDecimal("ni_tot_job_presenti").intValue()
		: 0;
	int niTotJobAttivi = infoJobRowBean.getBigDecimal("ni_tot_job_attivi") != null
		? infoJobRowBean.getBigDecimal("ni_tot_job_attivi").intValue()
		: 0;
	int niTotJobDisattivi = infoJobRowBean.getBigDecimal("ni_tot_job_disattivi") != null
		? infoJobRowBean.getBigDecimal("ni_tot_job_disattivi").intValue()
		: 0;

	String[] info = new String[3];
	info[0] = "" + niTotJobPresenti;
	info[1] = "" + niTotJobAttivi;
	info[2] = "" + niTotJobDisattivi;
	return info;
    }

    public void popolaInformazioniJob() {
	String[] info = calcolaInformazioniJob();
	getForm().getGestioneJobRicercaInfo().getNi_tot_job_presenti().setValue(info[0]);
	getForm().getGestioneJobRicercaInfo().getNi_tot_job_attivi().setValue(info[1]);
	getForm().getGestioneJobRicercaInfo().getNi_tot_job_disattivi().setValue(info[2]);
    }

    public void popolaInfoDecJobAmministrazioneJobTab() throws EMFError {
	String[] info = calcolaInformazioniJob();
	getForm().getGestioneJobInfo().getNi_tot_job_presenti().setValue(info[0]);
	getForm().getGestioneJobInfo().getNi_tot_job_attivi().setValue(info[1]);
	getForm().getGestioneJobInfo().getNi_tot_job_disattivi().setValue(info[2]);
    }

    @Override
    public void ricercaGestioneJob() throws EMFError {
	getForm().getGestioneJobRicercaFiltri().getRicercaGestioneJob().setDisableHourGlass(true);
	GestioneJobRicercaFiltri filtri = getForm().getGestioneJobRicercaFiltri();

	if (getRequest().getAttribute("fromLink") == null
		&& getRequest().getAttribute("fromListaPrinc") == null) {
	    if (getSession().getAttribute(FROM_SCHEDULAZIONI_JOB) != null) {
		getSession().removeAttribute(FROM_SCHEDULAZIONI_JOB);
	    } else {
		filtri.post(getRequest());
	    }
	}

	popolaInformazioniJob();
	if (filtri.validate(getMessageBox())) {
	    BaseTable jobTB = gestioneJobEjb.getPigDecJobTableBean(filtri);
	    getForm().getGestioneJobRicercaList().setTable(jobTB);
	    getForm().getGestioneJobRicercaList().getTable().setPageSize(100);
	    getForm().getGestioneJobRicercaList().getTable().first();

	}
	forwardToPublisher(Application.Publisher.GESTIONE_JOB_RICERCA);
    }

    @Override
    public void tabRicercaJobTabOnClick() throws EMFError {
	getForm().getGestioneJobTabs()
		.setCurrentTab(getForm().getGestioneJobTabs().getRicercaJobTab());
	ricercaGestioneJob();
	forwardToPublisher(Application.Publisher.GESTIONE_JOB_RICERCA);
    }

    @Override
    public void tabAmmJobTabOnClick() throws EMFError {
	getForm().getGestioneJobTabs().setCurrentTab(getForm().getGestioneJobTabs().getAmmJobTab());
	abilitaDisabilitaBottoniJob(
		!gestioneJobEjb.isPigDecJobFotoEmpty() && gestioneJobEjb.areAllJobsDisattivati(),
		getSession().getAttribute("fotoSalvata") != null);

	decoraDatiTabAmmJobTab();

	forwardToPublisher(Application.Publisher.GESTIONE_JOB_RICERCA);
    }

    public void decoraDatiTabAmmJobTab() throws EMFError {
	popolaInfoDecJobAmministrazioneJobTab();
	popolaInfoDecJobFotoAmministrazioneJobTab();

	BaseTable jobTB = gestioneJobEjb.getPigDecJobTableBeanPerAmm();
	getForm().getGestioneJobListPerAmm().setTable(jobTB);
	getForm().getGestioneJobListPerAmm().getTable().setPageSize(100);
	getForm().getGestioneJobListPerAmm().getTable().first();

	BaseTable jobFotoTB = gestioneJobEjb.getPigDecJobFotoTableBeanPerAmm();
	getForm().getGestioneJobFotoListPerAmm().setTable(jobFotoTB);
	getForm().getGestioneJobFotoListPerAmm().getTable().setPageSize(100);
	getForm().getGestioneJobFotoListPerAmm().getTable().first();

    }

    @SuppressWarnings("unchecked")
    public void getNuoviJob() throws JSONException {
	JSONObject jso = new JSONObject();
	// Recupero i nomi dei nuovi JOB
	Object[] nmJobObj = gestioneJobEjb.getInfoJobFotoNomiJobRowBean();
	jso.put("nm_job_array_nuovi", (Set<String>) nmJobObj[0]);
	jso.put("nm_job_array_solo_foto", (Set<String>) nmJobObj[1]);
	redirectToAjax(jso);
    }

    public void popolaInfoDecJobFotoAmministrazioneJobTab() throws EMFError {
	BaseRow infoJobFotoRowBean = gestioneJobEjb.getInfoJobFotoRowBean();
	int niTotJobPresenti2 = infoJobFotoRowBean.getBigDecimal("ni_tot_job_presenti2") != null
		? infoJobFotoRowBean.getBigDecimal("ni_tot_job_presenti2").intValue()
		: 0;
	int niTotJobAttivi2 = infoJobFotoRowBean.getBigDecimal("ni_tot_job_attivi2") != null
		? infoJobFotoRowBean.getBigDecimal("ni_tot_job_attivi2").intValue()
		: 0;
	int niTotJobDisattivi2 = infoJobFotoRowBean.getBigDecimal("ni_tot_job_disattivi2") != null
		? infoJobFotoRowBean.getBigDecimal("ni_tot_job_disattivi2").intValue()
		: 0;
	int niTotJobNuovi2 = infoJobFotoRowBean.getBigDecimal("ni_tot_job_nuovi2") != null
		? infoJobFotoRowBean.getBigDecimal("ni_tot_job_nuovi2").intValue()
		: 0;
	int niTotJobSoloFoto = infoJobFotoRowBean.getBigDecimal("ni_tot_job_solo_foto") != null
		? infoJobFotoRowBean.getBigDecimal("ni_tot_job_solo_foto").intValue()
		: 0;

	Date dataLastFoto = infoJobFotoRowBean.getTimestamp("last_job_foto");
	DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

	getForm().getGestioneJobInfo2().getNi_tot_job_presenti2().setValue("" + niTotJobPresenti2);
	getForm().getGestioneJobInfo2().getNi_tot_job_attivi2().setValue("" + niTotJobAttivi2);
	getForm().getGestioneJobInfo2().getNi_tot_job_disattivi2()
		.setValue("" + niTotJobDisattivi2);

	getForm().getGestioneJobInfo2().getNi_tot_job_nuovi2().setValue("" + niTotJobNuovi2);
	getForm().getGestioneJobInfo2().getNi_tot_job_solo_foto().setValue("" + niTotJobSoloFoto);

	getForm().getInfoJob2Section()
		.setLegend("Foto dei job alla data " + df.format(dataLastFoto));

    }

    @Override
    public void startMassivoGestioneJob() throws EMFError {
	// Recupero i record selezionati
	getForm().getGestioneJobRicercaList().post(getRequest());
	BaseTable tabella = (BaseTable) getForm().getGestioneJobRicercaList().getTable();

	if (tabella != null) {
	    ArrayList<Object[]> listaSelezionati = new ArrayList<>();
	    ArrayList<Object[]> listaNonSelezionati = new ArrayList<>();
	    ArrayList<Object[]> listaNoTimer = new ArrayList<>();
	    boolean almenoUnoSel = false;
	    for (int i = 0; i < tabella.size(); i++) {
		BaseRow riga = tabella.getRow(i);
		if (riga.getString("job_selezionati").equals("1")) {
		    almenoUnoSel = true;
		    Object[] jobDaValutare = new Object[3];
		    jobDaValutare[0] = i;
		    jobDaValutare[1] = riga.getString("nm_job");
		    jobDaValutare[2] = riga.getString("ds_job");
		    if (riga.getString("stato_job").equals("DISATTIVO")) {
			if (riga.getString("ti_sched_job").equals("NO_TIMER")) {
			    listaNoTimer.add(jobDaValutare);
			} else {
			    listaSelezionati.add(jobDaValutare);
			}
		    } else {
			listaNonSelezionati.add(jobDaValutare);
		    }
		}
	    }

	    if (almenoUnoSel) {// listaSelezionati

		String message = "";
		String jobSchedulatiString = "";
		for (Object[] obj : listaSelezionati) {
		    startGestioneJobOperation((int) obj[0], (String) obj[1], (String) obj[2]);
		    jobSchedulatiString = jobSchedulatiString + (String) obj[1] + "<br>";
		}
		if (!jobSchedulatiString.equals("")) {
		    message = "Sono stati schedulati i seguenti job: <br><br>" + jobSchedulatiString
			    + "<br>";
		}

		String jobNonSchedulatiString = "";
		for (Object[] obj : listaNonSelezionati) {
		    jobNonSchedulatiString = jobNonSchedulatiString + (String) obj[1] + "<br>";
		}
		if (!jobNonSchedulatiString.equals("")) {
		    message = message + "<br>Non sono stati schedulati i seguenti job: <br><br>"
			    + jobNonSchedulatiString
			    + "<br> in quanto in stato già ATTIVO o IN_ELABORAZIONE<br>";
		}

		String jobNoTimerString = "";
		for (Object[] obj : listaNoTimer) {
		    jobNoTimerString = jobNoTimerString + (String) obj[1] + "<br>";
		}
		if (!jobNoTimerString.equals("")) {
		    message = message + "<br>Non sono stati schedulati i seguenti job: <br><br>"
			    + jobNoTimerString
			    + "<br> in quanto di tipo NO_TIMER. Per essi è possibile lanciare solo l'ESECUZIONE SINGOLA<br>";
		}

		getMessageBox().clear();
		getMessageBox().setViewMode(ViewMode.alert);
		getMessageBox().addInfo(message
			+ "<br>L'operazione richiesta diventerà effettiva entro il prossimo minuto.");
	    } else {
		getMessageBox().addInfo("Nessun job selezionato");
	    }
	} else {
	    getMessageBox().addInfo("Nessun job selezionato");
	}
	popolaInformazioniJob();
	ricercaGestioneJob();
    }

    @Override
    public void stopMassivoGestioneJob() throws EMFError {
	// Recupero i record selezionati
	getForm().getGestioneJobRicercaList().post(getRequest());
	BaseTable tabella = (BaseTable) getForm().getGestioneJobRicercaList().getTable();

	if (tabella != null) {
	    ArrayList<Object[]> listaSelezionati = new ArrayList<>();
	    ArrayList<Object[]> listaNonSelezionati = new ArrayList<>();
	    ArrayList<Object[]> listaNoTimer = new ArrayList<>();
	    boolean almenoUnoSel = false;
	    for (int i = 0; i < tabella.size(); i++) {
		BaseRow riga = tabella.getRow(i);
		if (riga.getString("job_selezionati").equals("1")) {
		    almenoUnoSel = true;
		    Object[] jobDaValutare = new Object[3];
		    jobDaValutare[0] = i;
		    jobDaValutare[1] = riga.getString("nm_job");
		    jobDaValutare[2] = riga.getString("ds_job");
		    if (riga.getString("stato_job").equals("ATTIVO")) {
			if (riga.getString("ti_sched_job").equals("NO_TIMER")) {
			    listaNoTimer.add(jobDaValutare);
			} else {
			    listaSelezionati.add(jobDaValutare);
			}
		    } else {
			listaNonSelezionati.add(jobDaValutare);
		    }
		}
	    }

	    if (almenoUnoSel) {
		String jobSchedulatiString = "";

		String message = "";
		for (Object[] obj : listaSelezionati) {
		    stopGestioneJobOperation((int) obj[0], (String) obj[1], (String) obj[2]);
		    jobSchedulatiString = jobSchedulatiString + (String) obj[1] + "<br>";
		}
		if (!jobSchedulatiString.equals("")) {
		    message = "Sono stati stoppati i seguenti job: <br><br>" + jobSchedulatiString
			    + "<br>";
		}

		String jobNonSchedulatiString = "";
		for (Object[] obj : listaNonSelezionati) {
		    jobNonSchedulatiString = jobNonSchedulatiString + (String) obj[1] + "<br>";
		}
		if (!jobNonSchedulatiString.equals("")) {
		    message = message + "<br>Non sono stati stoppati i seguenti job: <br><br>"
			    + jobNonSchedulatiString
			    + "<br> in quanto in stato già DISATTIVO o IN_ESECUZIONE<br>";
		}

		String jobNoTimerString = "";
		for (Object[] obj : listaNoTimer) {
		    jobNoTimerString = jobNoTimerString + (String) obj[1] + "<br>";
		}
		if (!jobNoTimerString.equals("")) {
		    message = message + "<br>Non sono stati stoppati i seguenti job: <br><br>"
			    + jobNoTimerString + "<br> in quanto di tipo NO_TIMER<br>";
		}

		getMessageBox().clear();
		getMessageBox().setViewMode(ViewMode.alert);
		getMessageBox().addInfo(message
			+ "<br>L'operazione richiesta diventerà effettiva entro il prossimo minuto.");
	    } else {
		getMessageBox().addInfo("Nessun job selezionato");
	    }
	} else {
	    getMessageBox().addInfo("Nessun job selezionato");
	}
	popolaInformazioniJob();
	ricercaGestioneJob();
    }

    @Override
    public void esecuzioneSingolaMassivaGestioneJob() throws EMFError {
	// Recupero i record selezionati
	getForm().getGestioneJobRicercaList().post(getRequest());
	BaseTable tabella = (BaseTable) getForm().getGestioneJobRicercaList().getTable();

	if (tabella != null) {
	    ArrayList<Object[]> listaSelezionati = new ArrayList<>();
	    ArrayList<Object[]> listaNonSelezionati = new ArrayList<>();
	    boolean almenoUnoSel = false;
	    for (int i = 0; i < tabella.size(); i++) {
		BaseRow riga = tabella.getRow(i);
		if (riga.getString("job_selezionati").equals("1")) {
		    almenoUnoSel = true;
		    Object[] jobDaValutare = new Object[3];
		    jobDaValutare[0] = i;
		    jobDaValutare[1] = riga.getString("nm_job");
		    jobDaValutare[2] = riga.getString("ds_job");
		    if (riga.getString("stato_job").equals("DISATTIVO")) {
			listaSelezionati.add(jobDaValutare);
		    } else {
			listaNonSelezionati.add(jobDaValutare);
		    }
		}
	    }

	    if (almenoUnoSel) {
		String jobSchedulatiString = "";

		String message = "";
		for (Object[] obj : listaSelezionati) {
		    esecuzioneSingolaGestioneJobOperation((int) obj[0], (String) obj[1],
			    (String) obj[2]);
		    jobSchedulatiString = jobSchedulatiString + (String) obj[1] + "<br>";
		}

		if (!jobSchedulatiString.equals("")) {
		    message = "Sono stati attivati in esecuzione singola i seguenti job: <br><br>"
			    + jobSchedulatiString + "<br>";
		}

		String jobNonSchedulatiString = "";
		for (Object[] obj : listaNonSelezionati) {
		    jobNonSchedulatiString = jobNonSchedulatiString + (String) obj[1] + "<br>";
		}
		if (!jobNonSchedulatiString.equals("")) {
		    message = message
			    + "<br>Non sono stati attivati in esecuzione singola i seguenti job: <br><br>"
			    + jobNonSchedulatiString
			    + "<br> in quanto in stato già ATTIVO o IN_ESECUZIONE<br>";
		}

		getMessageBox().clear();
		getMessageBox().setViewMode(ViewMode.alert);
		getMessageBox().addInfo(message
			+ "L'operazione richiesta diventerà effettiva entro il prossimo minuto.");
	    } else {
		getMessageBox().addInfo("Nessun job selezionato");
	    }
	} else {
	    getMessageBox().addInfo("Nessun job selezionato");
	}
	popolaInformazioniJob();
	ricercaGestioneJob();
    }

    @Override
    public void salvaFotoGestioneJob() throws EMFError {
	// Eseguo il salvataggio foto, solo se ho almeno 1 JOB attivo
	BaseTable tabella = (BaseTable) getForm().getGestioneJobListPerAmm().getTable();
	boolean trovatoAttivo = false;
	for (BaseRow riga : tabella) {
	    if (riga.getString("stato_job").equals("ATTIVO")) {
		trovatoAttivo = true;
		break;
	    }
	}
	if (trovatoAttivo) {
	    gestioneJobEjb.salvaFoto();
	    getSession().setAttribute("fotoSalvata", true);
	    getMessageBox().addInfo("Foto JOB salvata con successo!");
	} else {
	    getMessageBox().addInfo("Nessun JOB attivo trovato: non è stata salvata la foto!");
	}
	tabAmmJobTabOnClick();

	abilitaDisabilitaBottoniJob(
		!gestioneJobEjb.isPigDecJobFotoEmpty() && gestioneJobEjb.areAllJobsDisattivati(),
		getSession().getAttribute("fotoSalvata") != null);

    }

    @Override
    public void ripristinaFotoGestioneJob() throws EMFError {
	gestioneJobEjb.ripristinaFotoGestioneJob();
	tabAmmJobTabOnClick();
	getMessageBox().addInfo(
		"Ripristino foto effettuato con successo! Attendere il minuto successivo per l'allineamento dei JOB eventualmente rischedulati");
	forwardToPublisher(getLastPublisher());
    }

    @Override
    public void ricaricaGestioneJob() throws EMFError {
	tabAmmJobTabOnClick();
    }

    public void abilitaDisabilitaBottoniJob(boolean abilitaRipristinaFoto,
	    boolean abilitaDisabilitaAllJobs) {
	if (abilitaRipristinaFoto) {
	    getForm().getGestioneJobInfo2().getRipristinaFotoGestioneJob().setReadonly(false);
	    getSession().setAttribute("visualizzaRipristinaFoto", true);
	} else {
	    getForm().getGestioneJobInfo2().getRipristinaFotoGestioneJob().setReadonly(true);
	    getSession().removeAttribute("visualizzaRipristinaFoto");
	}

	if (abilitaDisabilitaAllJobs) {
	    getForm().getGestioneJobInfo().getDisabilitaAllJobs().setReadonly(false);
	} else {
	    getForm().getGestioneJobInfo().getDisabilitaAllJobs().setReadonly(true);
	}
    }

    public void apriVisualizzaSchedulazioniJob() throws EMFError {
	Integer riga = Integer.parseInt(getRequest().getParameter("riga"));
	String nmJob = ((BaseTable) getForm().getGestioneJobRicercaList().getTable()).getRow(riga)
		.getString("nm_job");
	redirectToSchedulazioniJob(nmJob);
    }

    private void redirectToSchedulazioniJob(String nmJob) throws EMFError {
	MonitoraggioForm form = prepareRedirectToSchedulazioniJob(nmJob);
	redirectToPage(Application.Actions.MONITORAGGIO, form,
		form.getJobSchedulatiList().getName(),
		getForm().getGestioneJobRicercaList().getTable(), getNavigationEvent());
    }

    private MonitoraggioForm prepareRedirectToSchedulazioniJob(String nmJob) throws EMFError {
	MonitoraggioForm form = new MonitoraggioForm();
	/* Preparo la pagina di destinazione */
	form.getFiltriJobSchedulati().setEditMode();
	DecodeMap dec = ComboGetter.getMappaSortedGenericEnum("nm_job", Constants.NomiJob.values());
	form.getFiltriJobSchedulati().getNm_job().setDecodeMap(dec);
	/* Setto il valore del Job da cercare in Visualizzazione Job Schedulati */
	form.getFiltriJobSchedulati().getNm_job().setValue(nmJob);
	// Preparo la data di Schedulazione Da una settimana prima rispetto la data corrente
	Calendar c = Calendar.getInstance();
	c.add(Calendar.DATE, -7);
	c.set(Calendar.HOUR_OF_DAY, 0);
	c.set(Calendar.MINUTE, 0);
	c.set(Calendar.SECOND, 0);
	c.set(Calendar.MILLISECOND, 0);
	DateFormat f = new SimpleDateFormat("dd/MM/yyyy");
	form.getFiltriJobSchedulati().getDt_reg_log_job_da().setValue(f.format(c.getTime()));
	getSession().setAttribute("fromGestioneJob", true);
	return form;
    }

    @SuppressWarnings("unchecked")
    private void redirectToPage(final String action, BaseForm form, String listToPopulate,
	    BaseTableInterface<?> table, String event) throws EMFError {
	((it.eng.spagoLite.form.list.List<SingleValueField<?>>) form.getComponent(listToPopulate))
		.setTable(table);
	redirectToAction(action, "?operation=ricercaJobSchedulatiDaGestioneJob", form);
    }

    public void startGestioneJobOperation() throws EMFError {
	// Recupero la riga sulla quale ho cliccato Start
	Integer riga = Integer.parseInt(getRequest().getParameter("riga"));
	// Eseguo lo start del job interessato
	String nmJob = getForm().getGestioneJobRicercaList().getTable().getRow(riga)
		.getString("nm_job");
	String dsJob = getForm().getGestioneJobRicercaList().getTable().getRow(riga)
		.getString("ds_job");
	startGestioneJobOperation(riga, nmJob, dsJob);
	getRequest().setAttribute("fromListaPrinc", true);
	ricercaGestioneJob();
    }

    public void startGestioneJobOperation(int riga, String nmJob, String dsJob) throws EMFError {
	// Eseguo lo start del job interessato
	setJobVBeforeOperation(nmJob, riga);
	eseguiNuovo(nmJob, dsJob, null, OPERAZIONE.START);
    }

    public void stopGestioneJobOperation() throws EMFError {
	// Recupero la riga sulla quale ho cliccato Start
	Integer riga = Integer.parseInt(getRequest().getParameter("riga"));
	// Eseguo lo start del job interessato
	String nmJob = getForm().getGestioneJobRicercaList().getTable().getRow(riga)
		.getString("nm_job");
	String dsJob = getForm().getGestioneJobRicercaList().getTable().getRow(riga)
		.getString("ds_job");
	stopGestioneJobOperation(riga, nmJob, dsJob);
	getRequest().setAttribute("fromListaPrinc", true);
	ricercaGestioneJob();
    }

    public void stopGestioneJobOperation(int riga, String nmJob, String dsJob) throws EMFError {
	// Eseguo lo start del job interessato
	setJobVBeforeOperation(nmJob, riga);
	eseguiNuovo(nmJob, dsJob, null, OPERAZIONE.STOP);
    }

    public void esecuzioneSingolaGestioneJobOperation() throws EMFError {
	// Recupero la riga sulla quale ho cliccato Start
	Integer riga = Integer.parseInt(getRequest().getParameter("riga"));
	// Eseguo lo start del job interessato
	String nmJob = getForm().getGestioneJobRicercaList().getTable().getRow(riga)
		.getString("nm_job");
	String dsJob = getForm().getGestioneJobRicercaList().getTable().getRow(riga)
		.getString("ds_job");
	esecuzioneSingolaGestioneJobOperation(riga, nmJob, dsJob);
	getRequest().setAttribute("fromListaPrinc", true);
	ricercaGestioneJob();
    }

    public void esecuzioneSingolaGestioneJobOperation(int riga, String nmJob, String dsJob)
	    throws EMFError {
	// Eseguo lo start del job interessato
	setJobVBeforeOperation(nmJob, riga);
	eseguiNuovo(nmJob, dsJob, null, OPERAZIONE.ESECUZIONE_SINGOLA);
    }

    @Override
    public void totJobOperation() throws EMFError {
	ricercaGestioneJob();
	forwardToPublisher(getLastPublisher());
    }

    @Override
    public void totJobAttiviOperation() throws EMFError {
	String[] attivi = new String[1];
	attivi[0] = "ATTIVO";
	getRequest().setAttribute("fromLink", true);
	getForm().getGestioneJobRicercaFiltri().getTi_stato_job().setValues(attivi);
	ricercaGestioneJob();
	forwardToPublisher(getLastPublisher());
    }

    @Override
    public void totJobDisattiviOperation() throws EMFError {
	String[] disattivi = new String[1];
	disattivi[0] = "DISATTIVO";
	getRequest().setAttribute("fromLink", true);
	getForm().getGestioneJobRicercaFiltri().getTi_stato_job().setValues(disattivi);
	ricercaGestioneJob();
	forwardToPublisher(getLastPublisher());
    }

    @Override
    public void disabilitaAllJobs() throws EMFError {
	gestioneJobEjb.disabilitaAllJobs();
	tabAmmJobTabOnClick();
	getMessageBox().addInfo("Tutti i job disattivati con successo!");
	forwardToPublisher(getLastPublisher());
    }

    private void eseguiNuovo(String nomeJob, String descrizioneJob, String nomeApplicazione,
	    OPERAZIONE operazione) throws EMFError {
	// Messaggio sul logger di sistema
	StringBuilder info = new StringBuilder(descrizioneJob);
	info.append(": ").append(operazione.description()).append(" [").append(nomeJob);
	if (nomeApplicazione != null) {
	    info.append("_").append(nomeApplicazione);
	}
	info.append("]");
	LOG.info("{0}", info.toString());

	String message = "Errore durante la schedulazione del job";

	switch (operazione) {
	case START:
	    jbossTimerEjb.start(nomeJob, null);
	    message = descrizioneJob
		    + ": job correttamente schedulato. L'operazione richiesta verrà schedulata correttamente entro il prossimo minuto.";
	    break;
	case ESECUZIONE_SINGOLA:
	    jbossTimerEjb.esecuzioneSingola(nomeJob, null);
	    message = descrizioneJob
		    + ": job correttamente schedulato per esecuzione singola. L'operazione richiesta verrà effettuata entro il prossimo minuto.";
	    break;
	case STOP:
	    jbossTimerEjb.stop(nomeJob);
	    message = descrizioneJob
		    + ": schedulazione job annullata. L'operazione richiesta diventerà effettiva entro il prossimo minuto.";
	    break;
	}

	// Segnalo l'avvenuta operazione sul job
	getMessageBox().addMessage(new Message(MessageLevel.INF, message));
	getMessageBox().setViewMode(ViewMode.plain);
    }

    public void setJobVBeforeOperation(String nmJob, int riga) throws EMFError {
	Timestamp dataAttivazioneJob = getActivationDateJob(nmJob);
	StatoJob statoJob = new StatoJob(nmJob, null, null, null, null, null, null, null,
		dataAttivazioneJob);
	gestisciStatoJobNuovo(statoJob);
    }

    private boolean gestisciStatoJobNuovo(StatoJob statoJob) throws EMFError {
	// se non è ancora passato un minuto da quando è stato premuto un pulsante non posso fare
	// nulla
	return jbossTimerEjb.isEsecuzioneInCorso(statoJob.getNomeJob());

    }
    // </editor-fold>

}
