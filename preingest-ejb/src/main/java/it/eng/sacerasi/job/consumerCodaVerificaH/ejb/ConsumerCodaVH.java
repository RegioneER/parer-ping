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

package it.eng.sacerasi.job.consumerCodaVerificaH.ejb;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.eng.sacerasi.job.preparaxml.dto.OggettoInCoda;
import it.eng.sacerasi.job.preparaxml.dto.PayloadCdPrepXml;
import it.eng.sacerasi.common.Constants;
import it.eng.sacerasi.exception.ParerInternalError;
import it.eng.sacerasi.job.preparaxml.ejb.VerificaHashAsyncEjb;
import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.ejb.MessageDrivenContext;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import it.eng.sacerasi.ws.dto.IRispostaWS.SeverityEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import it.eng.parer.objectstorage.exceptions.ObjectStorageException;
import static it.eng.sacerasi.common.Constants.JPA_PORPERTIES_TIMEOUT;
import it.eng.sacerasi.entity.PigObject;
import it.eng.sacerasi.entity.PigSessioneIngest;
import it.eng.sacerasi.ws.dto.RispostaControlli;
import it.eng.sacerasi.ws.ejb.SalvataggioDati;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;
import it.eng.sacerasi.entity.PigErrore;
import it.eng.sacerasi.entity.PigSisma;
import it.eng.sacerasi.entity.PigStatoSessioneIngest;
import it.eng.sacerasi.entity.PigStrumentiUrbanistici;
import it.eng.sacerasi.messages.MessaggiHelper;
import it.eng.sacerasi.sisma.ejb.SismaHelper;
import it.eng.sacerasi.strumentiUrbanistici.ejb.StrumentiUrbanisticiHelper;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

/**
 *
 * @author Fioravanti_F
 */
@MessageDriven(name = "ConsumerCodaVH", activationConfig = {
	@ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge"),
	@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
	@ActivationConfigProperty(propertyName = "messageSelector", propertyValue = "queueType = 'CODA_VER_HASH'"),
	@ActivationConfigProperty(propertyName = "destination", propertyValue = "jms/queue/ProducerCodaVersQueue") })
@TransactionManagement(TransactionManagementType.CONTAINER)
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class ConsumerCodaVH implements MessageListener {

    private static final String DESC_CONSUMER = "ConsumerCodaVH";

    Logger log = LoggerFactory.getLogger(ConsumerCodaVH.class);
    @PersistenceContext(unitName = "SacerAsiJPA")
    private EntityManager entityManager;
    @Resource
    private MessageDrivenContext mdc;

    @EJB
    private SalvataggioDati salvataggioDati;
    @EJB
    private VerificaHashAsyncEjb verificaHashAsync;
    @EJB
    private StrumentiUrbanisticiHelper strumentiUrbanisticiHelper;
    @EJB
    private SismaHelper sismaHelper;
    @EJB
    private MessaggiHelper messaggiHelper;

    @Override
    public void onMessage(Message message) {

	log.debug(DESC_CONSUMER + " :: inizio a processare il messaggio ");

	try {

	    ObjectMapper mapper = new ObjectMapper();
	    if (message instanceof TextMessage) {
		TextMessage textMessage = (TextMessage) message;

		PayloadCdPrepXml tmpPayloadCdPrepXml;
		tmpPayloadCdPrepXml = mapper.readValue(textMessage.getText(),
			PayloadCdPrepXml.class);

		log.info("{} :: processo l'oggetto (IdPigObject) {}", DESC_CONSUMER,
			tmpPayloadCdPrepXml.getIdPigObject());
		if (controllaLockStatoVerHash(tmpPayloadCdPrepXml.getIdLastSessioneIngest(),
			Constants.StatoVerificaHash.IN_CODA)) {
		    OggettoInCoda tmpOggettoInCoda = new OggettoInCoda();
		    tmpOggettoInCoda.setRifPigObject(
			    riagganciaPigObject(tmpPayloadCdPrepXml.getIdPigObject()));

		    verificaHashAsync.verificaHash(tmpPayloadCdPrepXml.getRootDirectory(),
			    tmpOggettoInCoda);
		    if (tmpOggettoInCoda.getSeverity() != SeverityEnum.ERROR) {
			impostaStatoVerHashSuccess(tmpOggettoInCoda.getRifPigObject());
		    } else {
			// gestione errore - la chiamata al metodo di chiusura imposta anche a KO lo
			// stato di
			// verifica hash
			chiudiInErrore(tmpOggettoInCoda);
		    }
		} else {
		    log.error(
			    "{} :: errore non critico nel consumer: identificato un messaggio (doppio) che presenta lo stato di verifica hash diverso da IN_CODA. Forse è uno zombie riesumato dalla DLQ :: ID di pigObject {} :: ID di lastSessione Ingest {}",
			    DESC_CONSUMER, tmpPayloadCdPrepXml.getIdPigObject(),
			    tmpPayloadCdPrepXml.getIdLastSessioneIngest());
		}
	    }
	    log.debug(DESC_CONSUMER + " :: il consumer ha terminato e committato");
	} catch (JMSException ex) {
	    log.error(DESC_CONSUMER + " :: errore nel consumer: JMSException ", ex);
	    mdc.setRollbackOnly();
	} catch (JsonProcessingException | ParerInternalError | ObjectStorageException ex) {
	    log.error(DESC_CONSUMER
		    + " ::  errore nel consumer, rollback transazione: ParerInternalError  ", ex);
	    mdc.setRollbackOnly();
	}
	log.debug(DESC_CONSUMER + " :: il consumer ha terminato e committato");
    }

    /*
     * usato da consumer coda verifica hash
     */
    private void impostaStatoVerHashSuccess(PigObject oggetto) throws ParerInternalError {
	try {
	    long idLastSessioneIngest = oggetto.getIdLastSessioneIngest().longValue();

	    if (oggetto.getPigTipoObject().getTiVersFile()
		    .equals(Constants.TipoVersamento.NO_ZIP.name())
		    || oggetto.getPigTipoObject().getTiVersFile()
			    .equals(Constants.TipoVersamento.ZIP_NO_XML_SACER.name())
		    || oggetto.getPigTipoObject().getTiVersFile()
			    .equals(Constants.TipoVersamento.ZIP_CON_XML_SACER.name())) {

		log.debug("ConsumerCodaVH - Modifica sessione in IN_ATTESA_SCHED");
		RispostaControlli tmpRispostaControlli = salvataggioDati.modificaSessione(
			idLastSessioneIngest, Constants.StatoSessioneIngest.IN_ATTESA_SCHED, null,
			null);

		if (tmpRispostaControlli.getCodErr() != null) {
		    logError(tmpRispostaControlli);
		    throw new ParerInternalError(tmpRispostaControlli.getCodErr());
		}

		log.debug("ConsumerCodaVH - Creazione stato sessione");
		tmpRispostaControlli.reset();
		tmpRispostaControlli = salvataggioDati.creaStatoSessione(idLastSessioneIngest,
			Constants.StatoSessioneIngest.IN_ATTESA_SCHED.name(),
			Calendar.getInstance().getTime());
		if (tmpRispostaControlli.getCodErr() != null) {
		    logError(tmpRispostaControlli);
		    throw new ParerInternalError(tmpRispostaControlli.getCodErr());
		}

		log.debug("Modifica oggetto in IN_ATTESA_SCHED");
		tmpRispostaControlli.reset();
		tmpRispostaControlli = salvataggioDati.modificaOggetto(oggetto.getIdObject(),
			Constants.StatoOggetto.IN_ATTESA_SCHED);
		if (tmpRispostaControlli.getCodErr() != null) {
		    logError(tmpRispostaControlli);
		    throw new ParerInternalError(tmpRispostaControlli.getCodErr());
		}

	    } else if (oggetto.getPigTipoObject().getTiVersFile()
		    .equals(Constants.TipoVersamento.DA_TRASFORMARE.name())) {
		log.debug("ConsumerCodaVH - Modifica sessione in IN_ATTESA_SCHED");
		RispostaControlli tmpRispostaControlli = salvataggioDati.modificaSessione(
			idLastSessioneIngest, Constants.StatoSessioneIngest.DA_TRASFORMARE, null,
			null);

		if (tmpRispostaControlli.getCodErr() != null) {
		    logError(tmpRispostaControlli);
		    throw new ParerInternalError(tmpRispostaControlli.getCodErr());
		}

		log.debug("ConsumerCodaVH - Creazione stato sessione");
		tmpRispostaControlli.reset();
		tmpRispostaControlli = salvataggioDati.creaStatoSessione(idLastSessioneIngest,
			Constants.StatoSessioneIngest.DA_TRASFORMARE.name(),
			Calendar.getInstance().getTime());
		if (tmpRispostaControlli.getCodErr() != null) {
		    logError(tmpRispostaControlli);
		    throw new ParerInternalError(tmpRispostaControlli.getCodErr());
		}

		log.debug("Modifica oggetto in IN_ATTESA_SCHED");
		tmpRispostaControlli.reset();
		tmpRispostaControlli = salvataggioDati.modificaOggetto(oggetto.getIdObject(),
			Constants.StatoOggetto.DA_TRASFORMARE);
		if (tmpRispostaControlli.getCodErr() != null) {
		    logError(tmpRispostaControlli);
		    throw new ParerInternalError(tmpRispostaControlli.getCodErr());
		}
	    }

	    PigSessioneIngest tmpSessioneIngest;
	    tmpSessioneIngest = entityManager.find(PigSessioneIngest.class, idLastSessioneIngest);
	    tmpSessioneIngest.setTiStatoVerificaHash(Constants.StatoVerificaHash.OK.name());
	    entityManager.flush();
	} catch (Exception ex) {
	    log.error("Eccezione", ex);
	    throw new ParerInternalError(ex);
	}
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    private void chiudiInErrore(OggettoInCoda oggetto) {

	BigDecimal tmpIdLastSess = oggetto.getRifPigObject().getIdLastSessioneIngest();
	PigObject tmpPigObject;
	PigSessioneIngest tmpSessioneIngest;
	Date now = new Date();
	tmpSessioneIngest = entityManager.find(PigSessioneIngest.class, tmpIdLastSess.longValue());

	tmpSessioneIngest.setDtChiusura(now);
	tmpSessioneIngest.setTiStato(Constants.StatoOggetto.CHIUSO_ERR_VERIFICA_HASH.name());
	tmpSessioneIngest.setCdErr(oggetto.getErrorCode());
	tmpSessioneIngest.setDlErr(oggetto.getErrorMessage());
	tmpSessioneIngest.setFlSesErrVerif("0"); // imposto a ZERO il flag di sessione verificata.
	// imposta a KO lo stato di verifica Hash,
	tmpSessioneIngest.setTiStatoVerificaHash(Constants.StatoVerificaHash.KO.name());

	tmpPigObject = entityManager.find(PigObject.class, oggetto.getRifPigObject().getIdObject());
	tmpPigObject.setTiStatoObject(Constants.StatoOggetto.CHIUSO_ERR_VERIFICA_HASH.name());

	entityManager.flush();

	oggetto.setRifPigObject(tmpPigObject);

	PigStatoSessioneIngest pigStatoSessione = new PigStatoSessioneIngest();
	pigStatoSessione.setPigSessioneIngest(tmpSessioneIngest);
	pigStatoSessione.setIdVers(tmpSessioneIngest.getPigVer().getIdVers());
	pigStatoSessione.setTiStato(Constants.StatoOggetto.CHIUSO_ERR_VERIFICA_HASH.name());
	pigStatoSessione.setTsRegStato(new Timestamp(now.getTime()));

	entityManager.persist(pigStatoSessione);

	tmpSessioneIngest.setIdStatoSessioneIngestCor(
		new BigDecimal(pigStatoSessione.getIdStatoSessioneIngest()));
	entityManager.flush();

	// MEV 22064 - Il SU va in stato ERRORE
	if (tmpPigObject.getPigObjectPadre() != null) {
	    // MEV 22064 - ora lo stato da gestire è IN_VERSAMENTO e non più IN_ELABORAZIONE per i
	    // SU.
	    PigStrumentiUrbanistici pigStrumentiUrbanistici = strumentiUrbanisticiHelper
		    .getPigStrumUrbByCdKeyAndTiStato(
			    tmpPigObject.getPigObjectPadre().getCdKeyObject(),
			    PigStrumentiUrbanistici.TiStato.IN_VERSAMENTO);
	    if (pigStrumentiUrbanistici != null) {
		PigErrore errore = messaggiHelper.retrievePigErrore("PING-ERRSU27");
		pigStrumentiUrbanistici = strumentiUrbanisticiHelper.aggiornaStato(
			pigStrumentiUrbanistici, PigStrumentiUrbanistici.TiStato.ERRORE);
		pigStrumentiUrbanistici.setCdErr(errore.getCdErrore());
		pigStrumentiUrbanistici.setDsErr(errore.getDsErrore());
	    }

	    // MEV 30935 - ora lo stato da gestire è IN_VERSAMENTO e non più IN_ELABORAZIONE per
	    // i SU.
	    PigSisma pigSisma = sismaHelper.getPigSismaByCdKeyAndTiStato(
		    tmpPigObject.getPigObjectPadre().getCdKeyObject(),
		    PigSisma.TiStato.IN_VERSAMENTO);

	    if (pigSisma == null) {
		pigSisma = sismaHelper.getPigSismaByCdKeyAndTiStato(
			tmpPigObject.getPigObjectPadre().getCdKeyObject(),
			PigSisma.TiStato.IN_VERSAMENTO_SA);
	    }

	    if (pigSisma != null) {
		PigErrore errore = messaggiHelper.retrievePigErrore("PING-ERRSSISMA27");
		pigSisma = sismaHelper.aggiornaStato(pigSisma, PigSisma.TiStato.ERRORE);
		pigSisma.setCdErr(errore.getCdErrore());
		pigSisma.setDsErr(errore.getDsErrore());
	    }
	}
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    private boolean controllaLockStatoVerHash(long idLastSessioneIngest,
	    Constants.StatoVerificaHash stato) throws ParerInternalError {
	try {
	    log.debug("Leggo la riga {} e la blocco", idLastSessioneIngest);
	    Map<String, Object> properties = new HashMap<>();
	    properties.put(JPA_PORPERTIES_TIMEOUT, 25000);
	    PigSessioneIngest tmpSessioneIngest;
	    tmpSessioneIngest = entityManager.find(PigSessioneIngest.class, idLastSessioneIngest,
		    LockModeType.PESSIMISTIC_WRITE, properties);
	    return (tmpSessioneIngest.getTiStatoVerificaHash().equalsIgnoreCase(stato.name()));
	} catch (Exception ex) {
	    log.error("Eccezione", ex);
	    throw new ParerInternalError(ex);
	}
    }

    private PigObject riagganciaPigObject(long idObject) throws ParerInternalError {
	PigObject object;
	try {
	    object = entityManager.find(PigObject.class, idObject);
	    return object;
	} catch (Exception ex) {
	    log.error("Eccezione", ex);
	    throw new ParerInternalError(ex);
	}
    }

    private void logError(RispostaControlli rispostaControlli) {
	log.debug("Errore Notifica : {0} - {1} ", rispostaControlli.getCodErr(),
		rispostaControlli.getDsErr());
	log.debug("Fine transazione - ROLLBACK");
    }
}
