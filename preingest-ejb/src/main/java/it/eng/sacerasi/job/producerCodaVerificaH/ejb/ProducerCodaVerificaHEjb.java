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

package it.eng.sacerasi.job.producerCodaVerificaH.ejb;

import java.util.List;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

import it.eng.sacerasi.entity.PigOutboxEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.eng.sacerasi.common.Constants;
import static it.eng.sacerasi.common.Constants.JPA_PORPERTIES_TIMEOUT;
import it.eng.sacerasi.common.ejb.CommonDb;
import it.eng.sacerasi.entity.PigObject;
import it.eng.sacerasi.entity.PigSessioneIngest;
import it.eng.sacerasi.exception.ParerInternalError;
import it.eng.sacerasi.job.ejb.JobLogger;
import it.eng.sacerasi.job.preparaxml.dto.PayloadCdPrepXml;
import it.eng.sacerasi.web.helper.ConfigurationHelper;
import it.eng.sacerasi.ws.util.Costanti;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Fioravanti_F
 */
@SuppressWarnings("unchecked")
@Stateless(mappedName = "ProducerCodaVerificaHEjb")
@LocalBean
@TransactionManagement(TransactionManagementType.CONTAINER)
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class ProducerCodaVerificaHEjb {

    public static final String SELETTORE_CODA = "CODA_VER_HASH";
    //
    Logger log = LoggerFactory.getLogger(ProducerCodaVerificaHEjb.class);
    @PersistenceContext(unitName = "SacerAsiJPA")
    private EntityManager entityManager;
    @EJB
    private JobLogger jobLoggerEjb;
    @EJB
    ProducerCodaVerificaHEjb me;
    @EJB
    private CommonDb commonDb;
    @EJB
    private ConfigurationHelper configurationHelper;
    //
    @Resource(mappedName = "jms/ProducerConnectionFactory")
    private ConnectionFactory connectionFactory;
    @Resource(mappedName = "jms/queue/ProducerCodaVersQueue")
    private Queue queue;

    public void produceQueue() throws ParerInternalError {

        String rootFtpValue = commonDb.getRootFtpParam();
        List<PigObject> tmpOggetti = getListaObjectDaVersPreHashSenzaPadre();
        tmpOggetti.addAll(getListaObjectDaVersPreHashConPadre());
        log.info("Producer Coda Verifica Hash:: oggetti da processare per il calcolo hash: {}",
                tmpOggetti.size());

        // Parametro broker: JMS (embedded JBoss) oppure KAFKA (outbox → JDBC Source Connector)
        final boolean useKafka = Constants.BrokerType.KAFKA.name().equalsIgnoreCase(
                configurationHelper.getValoreParamApplicByApplic(Constants.PRODUCER_BROKER_TYPE));
        log.info("Producer Coda Verifica Hash:: broker selezionato: {}",
                useKafka ? "KAFKA" : "JMS");

        for (PigObject tmpObject : tmpOggetti) {
            if (useKafka) {
                me.inviaMessaggioOutboxPattern(tmpObject, rootFtpValue);
            } else {
                me.inviaMessaggio(tmpObject, rootFtpValue);
            }
        }
        jobLoggerEjb.writeAtomicLog(Constants.NomiJob.PRODUCER_CODA_VERIFICA_H,
                Constants.TipiRegLogJob.FINE_SCHEDULAZIONE, null);
    }

    public void inviaMessaggio(PigObject pigObject, String rootFtpValue) throws ParerInternalError {

        try (Connection connection = connectionFactory.createConnection();
                Session session = connection.createSession(true, Session.AUTO_ACKNOWLEDGE);
                MessageProducer messageProducer = session.createProducer(queue);) {

            PayloadCdPrepXml tmpPayloadCdPrepXml = new PayloadCdPrepXml();
            tmpPayloadCdPrepXml.setIdPigObject(pigObject.getIdObject());
            tmpPayloadCdPrepXml
                    .setIdLastSessioneIngest(pigObject.getIdLastSessioneIngest().longValue());
            tmpPayloadCdPrepXml.setRootDirectory(rootFtpValue);

            ObjectMapper mapper = new ObjectMapper();
            String payloadCdPrepXmlJson = mapper.writeValueAsString(tmpPayloadCdPrepXml);

            log.debug("Apro transazione per la sessione {}",
                    pigObject.getIdLastSessioneIngest().longValue());

            impostaLockStatoVerHash(pigObject.getIdLastSessioneIngest().longValue(),
                    Constants.StatoVerificaHash.IN_CODA);

            TextMessage textMessage = null;

            log.debug("Creo la connessione alla coda per la sessione {}",
                    pigObject.getIdLastSessioneIngest().longValue());

            textMessage = session.createTextMessage();

            log.debug("Creo l'oggetto in coda per la sessione {}",
                    pigObject.getIdLastSessioneIngest().longValue());
            // app selector
            textMessage.setStringProperty(Costanti.JMSMsgProperties.MSG_K_APP, Costanti.PING);
            textMessage.setStringProperty("queueType", SELETTORE_CODA);
            textMessage.setStringProperty(Costanti.JMSMsgProperties.MSG_K_PAYLOAD_TYPE,
                    Costanti.PAYLOAD_TYPE_VERIFICAH);
            textMessage.setText(payloadCdPrepXmlJson);

            messageProducer.send(textMessage);

            log.debug("Inviato l'oggetto in coda per la sessione {}",
                    pigObject.getIdLastSessioneIngest().longValue());

        } catch (SecurityException | IllegalStateException | JsonProcessingException
                | JMSException ex) {
            throw new ParerInternalError(ex);
        }
    }

    public void inviaMessaggioOutboxPattern(PigObject pigObject, String rootFtpValue)
            throws ParerInternalError {

        try {
            PayloadCdPrepXml tmpPayloadCdPrepXml = new PayloadCdPrepXml();
            tmpPayloadCdPrepXml.setIdPigObject(pigObject.getIdObject());
            tmpPayloadCdPrepXml
                    .setIdLastSessioneIngest(pigObject.getIdLastSessioneIngest().longValue());
            tmpPayloadCdPrepXml.setRootDirectory(rootFtpValue);

            ObjectMapper mapper = new ObjectMapper();
            String payloadCdPrepXmlJson = mapper.writeValueAsString(tmpPayloadCdPrepXml);

            log.debug("Inserimento outbox event per oggetto {} (sessione {})",
                    pigObject.getIdObject(), pigObject.getIdLastSessioneIngest().longValue());

            impostaLockStatoVerHash(pigObject.getIdLastSessioneIngest().longValue(),
                    Constants.StatoVerificaHash.IN_CODA);

            // Outbox Pattern: inserimento atomico nella stessa transazione JTA del DB.
            // Il JDBC Source Connector legge la tabella e pubblica su Kafka.
            PigOutboxEvent outboxEvent = new PigOutboxEvent();
            outboxEvent.setAggregateType(SELETTORE_CODA);
            outboxEvent.setAggregateId(String.valueOf(pigObject.getIdObject()));
            outboxEvent.setType("VERIFICA_HASH");
            outboxEvent.setPayload(payloadCdPrepXmlJson);
            entityManager.persist(outboxEvent);

            log.debug("Outbox event {} inserito per oggetto {} (sessione {})",
                    outboxEvent.getId(), pigObject.getIdObject(),
                    pigObject.getIdLastSessioneIngest().longValue());

        } catch (SecurityException | IllegalStateException | JsonProcessingException ex) {
            throw new ParerInternalError(ex);
        } catch (Exception ex) {
            // Errore imprevisto (es. PersistenceException): logghiamo il contesto
            // dell'oggetto e rilanciamo; la TX è già marcata rollback dal container.
            log.error(
                    "Errore imprevisto nell'inserimento outbox event per oggetto {} (sessione {})",
                    pigObject.getIdObject(), pigObject.getIdLastSessioneIngest().longValue(), ex);
            throw ex;
        }
    }

    /*
     * Seleziona tutti gli oggetti SENZA PADRE con stato IN_ATTESA_SCHED o DA_TRASFORMARE e
     * tiStatoVerificaHash nullo usato da producer coda verifica hash
     */
    public List<PigObject> getListaObjectDaVersPreHashSenzaPadre() {
        String queryStr = "SELECT u FROM PigSessioneIngest si JOIN si.pigObject u "
                + "WHERE u.tiStatoObject IN (:tiStatoObjectIn) "
                + "AND si.idSessioneIngest = u.idLastSessioneIngest "
                + "AND si.tiStatoVerificaHash IS NULL " + "AND u.pigObjectPadre IS NULL";

        javax.persistence.Query query = entityManager.createQuery(queryStr);
        List<String> stati = new ArrayList<>();
        // MEV 31102 ora cerchiamo igli oggetti in stato IN_CODA_HASH e non più quelli in
        // IN_ATTESA_SCHED
        stati.add(Constants.StatoOggetto.IN_CODA_HASH.name());
        query.setParameter("tiStatoObjectIn", stati);

        return query.getResultList();
    }

    /*
     * Seleziona tutti gli oggetti CON PADRE per cui il padre sia con stato 'VERSATO_A_PING' e
     * tiStatoVerificaHash nullo usato da producer coda verifica hash
     */
    private List<PigObject> getListaObjectDaVersPreHashConPadre() {
        String queryStr = "SELECT u FROM PigSessioneIngest si JOIN si.pigObject u "
                + "WHERE u.tiStatoObject = :tiStatoObjectIn "
                + "AND si.idSessioneIngest = u.idLastSessioneIngest "
                + "AND si.tiStatoVerificaHash IS NULL " + "AND u.pigObjectPadre IS NOT NULL "
                + "AND u.pigObjectPadre.tiStatoObject = 'VERSATO_A_PING'";

        javax.persistence.Query query = entityManager.createQuery(queryStr);
        // MEV 31102 ora cerchiamo igli oggetti in stato IN_CODA_HASH e non più quelli in
        // IN_ATTESA_SCHED
        query.setParameter("tiStatoObjectIn", Constants.StatoOggetto.IN_CODA_HASH.name());

        return query.getResultList();
    }

    /*
     * usato da producer coda verifica hash
     */
    private void impostaLockStatoVerHash(long idLastSessioneIngest,
            Constants.StatoVerificaHash stato) throws ParerInternalError {
        try {
            log.debug("Blocco la riga {} e la Aggiorno", idLastSessioneIngest);
            Map<String, Object> properties = new HashMap<>();
            properties.put(JPA_PORPERTIES_TIMEOUT, 25000);
            PigSessioneIngest tmpSessioneIngest;
            tmpSessioneIngest = entityManager.find(PigSessioneIngest.class, idLastSessioneIngest,
                    LockModeType.PESSIMISTIC_WRITE, properties);
            tmpSessioneIngest.setTiStatoVerificaHash(stato.name());
            entityManager.flush();
        } catch (Exception ex) {
            throw new ParerInternalError(ex);
        }
    }
}
