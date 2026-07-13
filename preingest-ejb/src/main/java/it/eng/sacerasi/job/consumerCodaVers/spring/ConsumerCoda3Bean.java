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

package it.eng.sacerasi.job.consumerCodaVers.spring;

import it.eng.sacerasi.entity.PigOutboxEvent;
import it.eng.sacerasi.exception.ParerInternalError;
import it.eng.sacerasi.exception.SacerPingRuntimeException;
import it.eng.sacerasi.exception.error.ErrorCategory.PingErrorCategory;
import it.eng.sacerasi.job.coda.dto.OutboxEvent;
import it.eng.sacerasi.job.coda.dto.Payload;
import it.eng.sacerasi.job.coda.spring.PayloadManagerBean;
import org.apache.avro.generic.GenericRecord;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;

/**
 *
 * @author DiLorenzo_F
 */
public class ConsumerCoda3Bean {

    private static final String DESC_CONSUMER = "CCV3";

    Logger log = LoggerFactory.getLogger(ConsumerCoda3Bean.class);

    @PersistenceContext
    private EntityManager entityManager;

    private PayloadManagerBean payloadManagerHelper;

    @Autowired
    public ConsumerCoda3Bean(PayloadManagerBean payloadManagerHelper) {
        this.payloadManagerHelper = payloadManagerHelper;
    }

    @KafkaListener(id = "#{systemProperties['kafka.prefix']}.ConsumerGroupCV3", groupId = "#{systemProperties['kafka.prefix']}.spring-sacer_ping.coda3", topics = "#{systemProperties['kafka.prefix']}.sacer_ping.coda3.raw", concurrency = "#{systemProperties['kafka.concurrency.ping.coda3']}")
    @Transactional
    public void listenCCV3(GenericRecord avroRecord) {
        OutboxEvent event = new OutboxEvent(avroRecord);
        try {
            log.debug(DESC_CONSUMER + " :: inizio a processare il messaggio");
            // check idempotenza prima della business logic
            PigOutboxEvent outboxEvent = entityManager.find(PigOutboxEvent.class,
                    event.getIdOutboxEvent());
            if (outboxEvent != null && outboxEvent.getProcessed().equals(1)) {
                log.warn("{} :: messaggio già processato, skip", DESC_CONSUMER);
            } else {
                Payload in = event.getDeserializedPayload(Payload.class);
                payloadManagerHelper.manageMessagePayload(in, DESC_CONSUMER);
                // messaggio processato con successo
                aggiornaOutboxEvent(outboxEvent);
            }
            log.debug(DESC_CONSUMER + " :: il consumer ha terminato e committato");
        } catch (ParerInternalError ex) {
            log.error(DESC_CONSUMER
                    + " :: errore nel consumer, rollback transazione: "
                    + ExceptionUtils.getRootCauseMessage(ex), ex);

            Throwable rootCause = ExceptionUtils.getRootCause(ex);

            // Se la causa radice è non-retryable (configurato in DefaultErrorHandler),
            // la rilanciamo direttamente → DefaultErrorHandler la riconosce → DLT senza retry
            if (rootCause instanceof SecurityException) {
                throw (SecurityException) rootCause;
            }
            if (rootCause instanceof IllegalStateException) {
                throw (IllegalStateException) rootCause;
            }

            // Qualsiasi altro errore applicativo → retryable → retry → DLT
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
}
