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
    //
    @Resource(mappedName = "jms/ProducerConnectionFactory")
    private ConnectionFactory connectionFactory;
    @Resource(mappedName = "jms/queue/ProducerCodaVersQueue")
    private Queue queue;

    public void produceQueue() throws ParerInternalError {

        List<PigObject> tmpOggetti = null;
        String rootFtpValue;

        rootFtpValue = commonDb.getRootFtpParam();
        tmpOggetti = getListaObjectDaVersPreHashSenzaPadre();
        tmpOggetti.addAll(getListaObjectDaVersPreHashConPadre());
        log.info("Producer Coda Verifica Hash:: oggetti da processare per il calcolo hash: {}", tmpOggetti.size());

        for (PigObject tmpObject : tmpOggetti) {
            me.inviaMessaggio(tmpObject, rootFtpValue);
        }
        jobLoggerEjb.writeAtomicLog(Constants.NomiJob.PRODUCER_CODA_VERIFICA_H,
                Constants.TipiRegLogJob.FINE_SCHEDULAZIONE, null);
    }

    public void inviaMessaggio(PigObject pigObject, String rootFtpValue) throws ParerInternalError {

        try {
            PayloadCdPrepXml tmpPayloadCdPrepXml = new PayloadCdPrepXml();
            tmpPayloadCdPrepXml.setIdPigObject(pigObject.getIdObject());
            tmpPayloadCdPrepXml.setIdLastSessioneIngest(pigObject.getIdLastSessioneIngest().longValue());
            tmpPayloadCdPrepXml.setRootDirectory(rootFtpValue);

            ObjectMapper mapper = new ObjectMapper();
            String payloadCdPrepXmlJson = mapper.writeValueAsString(tmpPayloadCdPrepXml);

            log.debug(String.format("Apro transazione per la sessione %s",
                    pigObject.getIdLastSessioneIngest().longValue()));

            try {

                MessageProducer messageProducer = null;
                Connection connection = null;
                Session session = null;
                try {
                    impostaLockStatoVerHash(pigObject.getIdLastSessioneIngest().longValue(),
                            Constants.StatoVerificaHash.IN_CODA);

                    TextMessage textMessage = null;

                    connection = connectionFactory.createConnection();

                    log.debug(String.format("Creo la connessione alla coda per la sessione %s",
                            pigObject.getIdLastSessioneIngest().longValue()));

                    session = connection.createSession(true, Session.AUTO_ACKNOWLEDGE);
                    messageProducer = session.createProducer(queue);

                    textMessage = session.createTextMessage();

                    log.debug(String.format("Creo l'oggetto in coda per la sessione %s",
                            pigObject.getIdLastSessioneIngest().longValue()));
                    // app selector
                    textMessage.setStringProperty(Costanti.JMSMsgProperties.MSG_K_APP, Costanti.PING);
                    textMessage.setStringProperty("queueType", SELETTORE_CODA);
                    textMessage.setStringProperty(Costanti.JMSMsgProperties.MSG_K_PAYLOAD_TYPE,
                            Costanti.PAYLOAD_TYPE_VERIFICAH);
                    textMessage.setText(payloadCdPrepXmlJson);

                    messageProducer.send(textMessage);

                    log.debug(String.format("Inviato l'oggetto in coda per la sessione %s",
                            pigObject.getIdLastSessioneIngest().longValue()));

                } catch (ParerInternalError ex) {
                    throw ex;
                } finally {
                    if (messageProducer != null) {
                        messageProducer.close();
                    }
                    if (session != null) {
                        session.close();
                    }
                    if (connection != null) {
                        connection.close();
                    }
                    log.debug(String.format("Chiusa la connessione per la sessione %s",
                            pigObject.getIdLastSessioneIngest().longValue()));
                }
            } catch (JMSException ex) {
                // può essere lanciata dalla createObjectMessage, dalla send o dalla close
                throw new ParerInternalError(ex);
            }
            log.debug(String.format("Chiudo transazione per la sessione %s",
                    pigObject.getIdLastSessioneIngest().longValue()));
        } catch (SecurityException | IllegalStateException | JsonProcessingException ex) {
            throw new ParerInternalError(ex);
        }
        log.debug("Chiudo transazione per la sessione {}", pigObject.getIdLastSessioneIngest());

    }

    /*
     * Seleziona tutti gli oggetti SENZA PADRE con stato IN_ATTESA_SCHED o DA_TRASFORMARE e tiStatoVerificaHash nullo
     * usato da producer coda verifica hash
     */
    public List<PigObject> getListaObjectDaVersPreHashSenzaPadre() {
        String queryStr = "SELECT u FROM PigSessioneIngest si JOIN si.pigObject u "
                + "WHERE u.tiStatoObject IN (:tiStatoObjectIn) " + "AND si.idSessioneIngest = u.idLastSessioneIngest "
                + "AND si.tiStatoVerificaHash IS NULL " + "AND u.pigObjectPadre IS NULL";

        javax.persistence.Query query = entityManager.createQuery(queryStr);
        List<String> stati = new ArrayList<>();
        // MEV 31102 ora cerchiamo igli oggetti in stato IN_CODA_HASH e non più quelli in IN_ATTESA_SCHED
        stati.add(Constants.StatoOggetto.IN_CODA_HASH.name());
        query.setParameter("tiStatoObjectIn", stati);

        return query.getResultList();
    }

    /*
     * Seleziona tutti gli oggetti CON PADRE per cui il padre sia con stato 'VERSATO_A_PING' e tiStatoVerificaHash nullo
     * usato da producer coda verifica hash
     */
    private List<PigObject> getListaObjectDaVersPreHashConPadre() {
        String queryStr = "SELECT u FROM PigSessioneIngest si JOIN si.pigObject u "
                + "WHERE u.tiStatoObject = :tiStatoObjectIn " + "AND si.idSessioneIngest = u.idLastSessioneIngest "
                + "AND si.tiStatoVerificaHash IS NULL " + "AND u.pigObjectPadre IS NOT NULL "
                + "AND u.pigObjectPadre.tiStatoObject = 'VERSATO_A_PING'";

        javax.persistence.Query query = entityManager.createQuery(queryStr);
        // MEV 31102 ora cerchiamo igli oggetti in stato IN_CODA_HASH e non più quelli in IN_ATTESA_SCHED
        query.setParameter("tiStatoObjectIn", Constants.StatoOggetto.IN_CODA_HASH.name());

        return query.getResultList();
    }

    /*
     * usato da producer coda verifica hash
     */
    private void impostaLockStatoVerHash(long idLastSessioneIngest, Constants.StatoVerificaHash stato)
            throws ParerInternalError {
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
            log.error("Eccezione", ex);
            throw new ParerInternalError(ex);
        }
    }
}
