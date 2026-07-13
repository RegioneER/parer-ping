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

package it.eng.sacerasi.job.consumerCodaVerificaH.spring;

import it.eng.parer.objectstorage.exceptions.BackendException;
import it.eng.parer.objectstorage.exceptions.ObjectStorageException;
import it.eng.sacerasi.common.Constants;
import it.eng.sacerasi.entity.*;
import it.eng.sacerasi.exception.ParerInternalError;
import it.eng.sacerasi.exception.SacerPingRuntimeException;
import it.eng.sacerasi.exception.error.ErrorCategory.PingErrorCategory;
import it.eng.sacerasi.job.coda.dto.OutboxEvent;
import it.eng.sacerasi.job.preparaxml.dto.OggettoInCoda;
import it.eng.sacerasi.job.preparaxml.dto.PayloadCdPrepXml;
import it.eng.sacerasi.job.preparaxml.ejb.VerificaHashAsyncEjb;
import it.eng.sacerasi.messages.MessaggiHelper;
import it.eng.sacerasi.sisma.ejb.SismaHelper;
import it.eng.sacerasi.strumentiUrbanistici.ejb.StrumentiUrbanisticiHelper;
import it.eng.sacerasi.ws.dto.IRispostaWS.SeverityEnum;
import it.eng.sacerasi.ws.dto.RispostaControlli;
import it.eng.sacerasi.ws.ejb.SalvataggioDati;
import org.apache.avro.generic.GenericRecord;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.transaction.annotation.Transactional;

import javax.ejb.*;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static it.eng.sacerasi.common.Constants.JPA_PORPERTIES_TIMEOUT;

/**
 *
 * @author DiLorenzo_F
 */
public class ConsumerCodaVHBean {

    private static final String DESC_CONSUMER = "ConsumerCodaVH";
    private static final String ECCEZIONE_STRING = "Eccezione";

    Logger log = LoggerFactory.getLogger(ConsumerCodaVHBean.class);

    @PersistenceContext
    private EntityManager entityManager;

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

    @KafkaListener(id = "#{systemProperties['kafka.prefix']}.ConsumerGroupVH", groupId = "#{systemProperties['kafka.prefix']}.spring-sacer_ping.verifica-hash", topics = "#{systemProperties['kafka.prefix']}.sacer_ping.coda_ver_hash.raw", concurrency = "#{systemProperties['kafka.concurrency.ping.coda-verifica-hash']}")
    @Transactional
    public void listenConsumerCodaVH(GenericRecord avroRecord) {
        OutboxEvent event = new OutboxEvent(avroRecord);
        log.debug(DESC_CONSUMER + " :: inizio a processare il messaggio");

        try {
            // check idempotenza prima della business logic
            PigOutboxEvent outboxEvent = entityManager.find(PigOutboxEvent.class,
                    event.getIdOutboxEvent());
            if (outboxEvent != null && outboxEvent.getProcessed().equals(1)) {
                log.warn("{} :: messaggio già processato, skip", DESC_CONSUMER);
            } else {
                PayloadCdPrepXml tmpPayloadCdPrepXml = event
                        .getDeserializedPayload(PayloadCdPrepXml.class);

                log.info("{} :: processo l'oggetto (IdPigObject) {}", DESC_CONSUMER,
                        tmpPayloadCdPrepXml.getIdPigObject());
                if (controllaLockStatoVerHash(tmpPayloadCdPrepXml.getIdLastSessioneIngest(),
                        Constants.StatoVerificaHash.IN_CODA)) {
                    OggettoInCoda tmpOggettoInCoda = new OggettoInCoda();
                    tmpOggettoInCoda
                            .setRifPigObject(
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
                    // Messaggio zombie/duplicato: stato verifica hash diverso da IN_CODA
                    // → il processing è già stato eseguito (o non deve essere eseguito)
                    // → NON è un errore che richiede retry: il listener ritorna normalmente
                    // → AckMode.RECORD committa l'offset → messaggio consumato ✓
                    log.error(
                            "{} :: errore non critico nel consumer: identificato un messaggio (doppio) che presenta lo stato di verifica hash diverso da IN_CODA. Forse è uno zombie riesumato dalla DLQ :: ID di pigObject {} :: ID di lastSessione Ingest {}",
                            DESC_CONSUMER, tmpPayloadCdPrepXml.getIdPigObject(),
                            tmpPayloadCdPrepXml.getIdLastSessioneIngest());
                }

                // messaggio processato con successo
                aggiornaOutboxEvent(outboxEvent);
            }
            log.debug(DESC_CONSUMER + " :: il consumer ha terminato e committato");

        } catch (BackendException ex) {
            // Errore transitorio del backend di storage (connessione, configurazione S3/MinIO)
            // → sempre retryable → retry con backoff → DLT
            log.error("{} :: errore backend storage nel consumer, rollback transazione: {}",
                    DESC_CONSUMER, ExceptionUtils.getRootCauseMessage(ex), ex);
            throw new SacerPingRuntimeException(ex.getMessage(), ex,
                    PingErrorCategory.INTERNAL_ERROR);

        } catch (ObjectStorageException ex) {
            // Errore transitorio infrastrutturale (rete, S3, ecc.)
            // → sempre retryable → retry con backoff → DLT
            log.error("{} :: errore object storage nel consumer, rollback transazione: {}",
                    DESC_CONSUMER, ExceptionUtils.getRootCauseMessage(ex), ex);
            throw new SacerPingRuntimeException(ex.getMessage(), ex,
                    PingErrorCategory.INTERNAL_ERROR);

        } catch (ParerInternalError ex) {
            // Errore applicativo: ispeziona la root cause
            // → se non-retryable (configurato in DefaultErrorHandler) → DLT senza retry
            // → altrimenti → retry con backoff → DLT
            log.error("{} :: errore applicativo nel consumer, rollback transazione: {}",
                    DESC_CONSUMER, ExceptionUtils.getRootCauseMessage(ex), ex);

            Throwable rootCause = ExceptionUtils.getRootCause(ex);
            if (rootCause instanceof SecurityException) {
                throw (SecurityException) rootCause;
            }
            if (rootCause instanceof IllegalStateException) {
                throw (IllegalStateException) rootCause;
            }
            // tutti gli altri casi → retryable
            throw new SacerPingRuntimeException(ex.getMessage(), ex,
                    PingErrorCategory.INTERNAL_ERROR);

        } catch (Exception ex) {
            // Errore non previsto (safety net)
            // → DefaultErrorHandler → retry con backoff → DLT
            log.error("{} :: errore non previsto nel consumer, rollback transazione: {}",
                    DESC_CONSUMER, ExceptionUtils.getRootCauseMessage(ex), ex);
            throw new SacerPingRuntimeException(ex.getMessage(), ex,
                    PingErrorCategory.INTERNAL_ERROR);
        }
    }

    private void aggiornaOutboxEvent(PigOutboxEvent outboxEvent) {
        if (outboxEvent != null) {
            outboxEvent.setProcessed(1);
            outboxEvent.setProcessedAt(LocalDateTime.now());
            entityManager.flush();
        } else {
            log.warn("{} :: outboxEvent non trovato, impossibile aggiornare lo stato processed",
                    DESC_CONSUMER);
        }
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
                        idLastSessioneIngest,
                        Constants.StatoSessioneIngest.IN_ATTESA_SCHED, null, null);

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
                        idLastSessioneIngest,
                        Constants.StatoSessioneIngest.DA_TRASFORMARE, null, null);

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
            log.error(ECCEZIONE_STRING, ex);
            throw new ParerInternalError(ex);
        }
    }

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
                            PigStrumentiUrbanistici.TiStato.IN_VERSAMENTO_ENTE);
            if (pigStrumentiUrbanistici != null) {
                PigErrore errore = messaggiHelper.retrievePigErrore("PING-ERRSU27");
                pigStrumentiUrbanistici = strumentiUrbanisticiHelper.aggiornaStato(
                        pigStrumentiUrbanistici,
                        PigStrumentiUrbanistici.TiStato.ERRORE);
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

    private boolean controllaLockStatoVerHash(long idLastSessioneIngest,
            Constants.StatoVerificaHash stato)
            throws ParerInternalError {
        try {
            log.debug("Leggo la riga {} e la blocco", idLastSessioneIngest);
            Map<String, Object> properties = new HashMap<>();
            properties.put(JPA_PORPERTIES_TIMEOUT, 25000);
            PigSessioneIngest tmpSessioneIngest;
            tmpSessioneIngest = entityManager.find(PigSessioneIngest.class, idLastSessioneIngest,
                    LockModeType.PESSIMISTIC_WRITE, properties);
            return (tmpSessioneIngest.getTiStatoVerificaHash().equalsIgnoreCase(stato.name()));
        } catch (Exception ex) {
            log.error(ECCEZIONE_STRING, ex);
            throw new ParerInternalError(ex);
        }
    }

    private PigObject riagganciaPigObject(long idObject) throws ParerInternalError {
        PigObject object;
        try {
            object = entityManager.find(PigObject.class, idObject);
            return object;
        } catch (Exception ex) {
            log.error(ECCEZIONE_STRING, ex);
            throw new ParerInternalError(ex);
        }
    }

    private void logError(RispostaControlli rispostaControlli) {
        log.debug("Errore Notifica : {0} - {1} ", rispostaControlli.getCodErr(),
                rispostaControlli.getDsErr());
        log.debug("Fine transazione - ROLLBACK");
    }
}
