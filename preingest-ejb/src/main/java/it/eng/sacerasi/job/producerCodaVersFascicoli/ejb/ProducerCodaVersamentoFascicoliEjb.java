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
package it.eng.sacerasi.job.producerCodaVersFascicoli.ejb;

import com.fasterxml.jackson.core.JsonProcessingException;
import it.eng.sacerasi.common.Constants;
import it.eng.sacerasi.common.Constants.NomiJob;
import it.eng.sacerasi.common.Constants.StatoSessioneIngest;
import it.eng.sacerasi.common.Constants.StatoUnitaDocObject;
import it.eng.sacerasi.common.Constants.TipiRegLogJob;
import it.eng.sacerasi.entity.PigFascicoloObject;
import it.eng.sacerasi.entity.PigFascicoloSessione;
import it.eng.sacerasi.entity.PigObject;
import it.eng.sacerasi.entity.PigSessioneIngest;
import it.eng.sacerasi.exception.JMSSendException;
import it.eng.sacerasi.job.coda.dto.PayloadFascicolo;
import it.eng.sacerasi.job.coda.helper.CodaHelper;
import it.eng.sacerasi.job.ejb.JobLogger;
import it.eng.sacerasi.messages.MessaggiWSBundle;
import it.eng.sacerasi.web.helper.ConfigurationHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import javax.ejb.*;
import javax.interceptor.Interceptors;
import javax.jms.JMSException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

/**
 * @author Cappelli_F
 */
@Stateless(mappedName = "ProducerCodaVersamentoFascicoliEjb")
@LocalBean
@Interceptors({
	it.eng.sacerasi.aop.TransactionInterceptor.class })
public class ProducerCodaVersamentoFascicoliEjb {

    public static final String SELETTORE_CODA = "CODA_VER_FASC";

    private static final String LOG_PREFIX = "PCVF ::";
    Logger log = LoggerFactory.getLogger(ProducerCodaVersamentoFascicoliEjb.class);
    @Resource
    private SessionContext context;
    @EJB
    private CodaHelper codaHelper;
    @EJB
    private JobLogger jobLogger;
    @EJB
    private MessageSenderEjbVersFascicoli messageSender;
    @EJB
    private ConfigurationHelper configurationHelper;

    class Counter {

	long value;

	Counter(long initialize) {
	    value = initialize;
	}

	long get() {
	    return value;
	}

	void increment() {
	    value++;
	}
    }

    class MaxUnitaDocInCodaException extends RuntimeException {
    }

    class ObjectProcessException extends RuntimeException {

	ObjectProcessException(Exception e) {
	    super(e);
	}
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void produceQueue() {
	log.info("{} avvio producer", LOG_PREFIX);
	// MEV #15058: Parametro per gestire numero di fascicoli massimo in coda
	final int numMaxFascicoliDaAccodare = Integer.parseInt(configurationHelper
		.getValoreParamApplicByApplic(Constants.NUM_FASCICOLI_CODA_VERS));

	final Counter fascicoliProcessati = new Counter(0);
	// MAC#31076 - ottengo la lista degli oggetti IN_ATTESA_VERS
	try (Stream<PigObject> oggetti = codaHelper.retrieveObjectsByStateAndContentType(
		StatoSessioneIngest.IN_ATTESA_VERS, Constants.TipoContenutoTipoOggetto.FASCICOLO)) {
	    oggetti.forEach(object -> {
		String infoLogObj = LOG_PREFIX + " idVers=" + object.getPigVer().getIdVers()
			+ ", nmVers=" + object.getPigVer().getNmVers() + ", idObject="
			+ object.getIdObject();

		boolean isLastFascicoloInObj = false;

		BigDecimal idLastSessioneIngest = object.getIdLastSessioneIngest();
		log.debug(
			"{} :: processo oggetto con id {}, objectTypeId={}, idLastSessioneIngest = {}",
			infoLogObj, object.getIdObject(),
			object.getPigTipoObject().getIdTipoObject(), idLastSessioneIngest);

		// determino, per l'oggetto, l'insieme dei fascicoli con stato = DA_VERSARE
		List<Long> fascicoliIdToProcess = codaHelper.retrieveFascicoliIdByIdObjAndState(
			object.getIdObject(), StatoUnitaDocObject.DA_VERSARE.name());
		log.debug("{} :: trovati {} fascicoli da processare per l'oggetto {}", infoLogObj,
			fascicoliIdToProcess.size(), object.getIdObject());

		for (int k = 0; k < fascicoliIdToProcess.size(); k++) {

		    String infoLogFascicolo = infoLogObj + ", fascicolo="
			    + fascicoliIdToProcess.get(k);
		    if (k == fascicoliIdToProcess.size() - 1) {
			isLastFascicoloInObj = true;
			log.debug("{} :: ultimo fascicolo dell'oggetto {}", infoLogFascicolo,
				object.getIdObject());
		    }
		    // di ogni facicolo determino l'identificatore e la chiave
		    PigFascicoloObject fascicolo = codaHelper
			    .findPigFascicoloObjectById(fascicoliIdToProcess.get(k));
		    PigFascicoloSessione fascicoloSessione = codaHelper
			    .retrievePigFascicoloSessioneByKeyUD(idLastSessioneIngest,
				    fascicolo.getAaFascicoloSacer(),
				    fascicolo.getCdKeyFascicoloSacer());

		    // apro una trasazione relativa ad una fascicolo
		    ProducerCodaVersamentoFascicoliEjb newProducerCodaVersEjbRef1 = context
			    .getBusinessObject(ProducerCodaVersamentoFascicoliEjb.class);
		    BigDecimal idAmbienteVers = BigDecimal
			    .valueOf(fascicolo.getPigObject().getPigTipoObject().getPigVer()
				    .getPigAmbienteVer().getIdAmbienteVers());
		    BigDecimal idVers = BigDecimal.valueOf(
			    fascicolo.getPigObject().getPigTipoObject().getPigVer().getIdVers());
		    BigDecimal idTipoObject = BigDecimal
			    .valueOf(fascicolo.getPigObject().getPigTipoObject().getIdTipoObject());

		    String urlServVers = configurationHelper.getValoreParamApplicByTipoObj(
			    "DS_URL_SERV_VERS", idAmbienteVers, idVers, idTipoObject);
		    try {
			newProducerCodaVersEjbRef1.manageFascicolo(fascicolo.getIdFascicoloObject(),
				fascicoloSessione.getIdFascicoloSessione(), urlServVers,
				isLastFascicoloInObj, infoLogFascicolo);
		    } catch (JMSException | JsonProcessingException | JMSSendException e) {
			throw new ObjectProcessException(e);
		    }

		    fascicoliProcessati.increment();
		    // MEV #15058: Parametro per gestire numero di fascicoli massimo in coda
		    //
		    if (fascicoliProcessati.get() == numMaxFascicoliDaAccodare) {
			throw new MaxUnitaDocInCodaException();
		    }
		}

		// FIXME perchè si rifà il controllo? Che mi sfugge?
		if (fascicoliProcessati.get() == numMaxFascicoliDaAccodare) {
		    throw new MaxUnitaDocInCodaException();
		}
	    });
	} catch (MaxUnitaDocInCodaException e) {
	    // non devo fare niente, ho finito e loggo che mi fermo qua
	    log.info(
		    "{} Raggiunto il limite massimo di {} fascicoli che si possono mettere in coda",
		    LOG_PREFIX, numMaxFascicoliDaAccodare);
	} catch (RuntimeException e) {
	    // c'è stato un errore, registro sul log la fine esecuzione del job segnalando l'errore
	    jobLogger.writeLog(NomiJob.PRODUCER_CODA_VERS_FASCICOLI, TipiRegLogJob.ERRORE,
		    e.getMessage());
	    log.debug("{} :: scrivo log fine job {} in {}", LOG_PREFIX,
		    NomiJob.PRODUCER_CODA_VERS_FASCICOLI, TipiRegLogJob.ERRORE);
	}
	jobLogger.writeLog(NomiJob.PRODUCER_CODA_VERS_FASCICOLI, TipiRegLogJob.FINE_SCHEDULAZIONE,
		null);
	log.debug("{} :: nessun errore - scrivo log fine job {}", LOG_PREFIX,
		NomiJob.PRODUCER_CODA_VERS_FASCICOLI);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void manageFascicolo(long fascicoloId, long fascicoloSessioneId, String urlServVers,
	    boolean isLastFascicoloInObj, String infoLogFascicolo)
	    throws JMSException, JsonProcessingException, JMSSendException {

	log.debug("PCV :: manageFascicolo processo fascicolo id = {}", fascicoloId);
	PigFascicoloObject fascicolo = codaHelper.findLockPigFascicoloObjectById(fascicoloId);
	PigFascicoloSessione fascicoloSessione = codaHelper
		.findLockPigFascicoloSessioneById(fascicoloSessioneId);

	PayloadFascicolo payload = this.buildPayload(fascicolo, fascicoloSessioneId, urlServVers);

	try {
	    log.debug("{} :: inserimento in coda", infoLogFascicolo);
	    messageSender.produceMessages(payload, SELETTORE_CODA);
	    log.debug("{} :: messaggio inserito in coda", infoLogFascicolo);
	} catch (JMSSendException ex) {
	    log.error(
		    infoLogFascicolo + " :: errore nell'inserimento in coda del fascicolo con id '"
			    + fascicoloId + "'",
		    ex);
	    this.handleSendError(payload.getSessionId().longValue(), fascicoloId);
	    throw ex;
	}
	// MEV 27407
	Date dtStato = new Date();

	// setto lo stato del fascicolo inserita nella coda a IN_CODA_VERS
	fascicolo.setTiStatoFascicoloObject(Constants.StatoUnitaDocObject.IN_CODA_VERS.name());
	fascicolo.setDtStato(dtStato);
	fascicoloSessione
		.setTiStatoFascicoloSessione(Constants.StatoUnitaDocSessione.IN_CODA_VERS.name());
	fascicoloSessione.setDtStato(dtStato);

	log.debug("{} :: setto lo stato del Fascicolo '{}' inserita nella coda in IN_CODA_VERS",
		infoLogFascicolo, fascicoloId);
	if (isLastFascicoloInObj) {
	    // ho finito di processare i fascicoli dell'oggetto corrente:
	    // aggiorno lo stato dell'oggetto e della sua ultima sessione assegnando IN_CODA_VERS
	    PigObject object = fascicolo.getPigObject();
	    object.setTiStatoObject(Constants.StatoOggetto.IN_CODA_VERS.name());
	    codaHelper.updateLastSessionState(object,
		    Constants.StatoSessioneIngest.IN_CODA_VERS.name());
	    log.debug(
		    "{} :: ho finito di processare i fascicoli dell'oggetto '{}': aggiorno lo stato dell'oggetto e della sua ultima sessione in IN_CODA_VERS",
		    infoLogFascicolo, object.getIdObject());
	}
    }

    public void handleSendError(long sessionId, long fascicoloId) {
	PigFascicoloObject fascicolo = codaHelper.findPigFascicoloObjectById(fascicoloId);

	// aggiorno la sessione con dtChiusura pari all'istante corrente e lo stato della sessione
	// tiStato =
	// CHIUSO_ERR_CODA
	PigSessioneIngest session = codaHelper.findPigSessioneIngestById(sessionId);
	codaHelper.updateSession(session, new Date(),
		Constants.StatoSessioneIngest.CHIUSO_ERR_CODA.name());
	// setto l’indicatore di sessione verificata() a false
	session.setFlSesErrVerif(Constants.DB_FALSE);
	// setto cdErr = PING-PRODCODA-001
	session.setCdErr(MessaggiWSBundle.PING_PRODCODA_001);
	// setto dlErr = Fallita registrazione in coda di versamento per fascicolo <anno>-<numero>
	session.setDlErr(MessaggiWSBundle.getString(MessaggiWSBundle.PING_PRODCODAFASC_001,
		fascicolo.getAaFascicoloSacer(), fascicolo.getCdKeyFascicoloSacer()));
	// aggiorno l'oggetto riferito alla sessione, assegnando stato = CHIUSO_ERR_CODA
	session.getPigObject().setTiStatoObject(Constants.StatoOggetto.CHIUSO_ERR_CODA.name());
    }

    public PayloadFascicolo buildPayload(PigFascicoloObject fascicolo, long fascicoloSessioneId,
	    String urlServVers) {
	PayloadFascicolo payload = new PayloadFascicolo();
	payload.setObjectId(fascicolo.getPigObject().getIdObject());
	payload.setSessionId(fascicolo.getPigObject().getIdLastSessioneIngest());
	payload.setFascicoloId(fascicolo.getIdFascicoloObject());
	payload.setFascicoloSessionId(fascicoloSessioneId);

	// la chiave è formata da: AA_FASCICOLO_SACER, CD_KEY_FASCICOLO_SACER
	payload.setAaFascicoloSacer(fascicolo.getAaFascicoloSacer());
	payload.setCdKeyFascicoloSacer(fascicolo.getCdKeyFascicoloSacer());
	payload.setUrlServVersamento(urlServVers);

	BigDecimal idAmbienteVers = BigDecimal.valueOf(fascicolo.getPigObject().getPigTipoObject()
		.getPigVer().getPigAmbienteVer().getIdAmbienteVers());
	BigDecimal idVers = BigDecimal
		.valueOf(fascicolo.getPigObject().getPigTipoObject().getPigVer().getIdVers());
	BigDecimal idTipoObject = BigDecimal
		.valueOf(fascicolo.getPigObject().getPigTipoObject().getIdTipoObject());
	String nmUseridSacer = configurationHelper.getValoreParamApplicByTipoObj("USERID_USER_VERS",
		idAmbienteVers, idVers, idTipoObject);
	String cdPswSacer = configurationHelper.getValoreParamApplicByTipoObj("PSW_USER_VERS",
		idAmbienteVers, idVers, idTipoObject);

	payload.setUserIdSacer(nmUseridSacer);
	payload.setPasswordSacer(cdPswSacer);

	return payload;
    }
}
