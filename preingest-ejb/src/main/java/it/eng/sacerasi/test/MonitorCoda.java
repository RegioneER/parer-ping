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

package it.eng.sacerasi.test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageFormatException;
import javax.jms.Queue;
import javax.jms.QueueBrowser;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueReceiver;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import it.eng.sacerasi.exception.ParerInternalError;
import it.eng.sacerasi.job.coda.dto.InfoCoda;
import it.eng.sacerasi.job.coda.dto.Payload;
import it.eng.sacerasi.job.coda.helper.CodaHelper;
import it.eng.sacerasi.job.preparaxml.dto.PayloadCdPrepXml;
import it.eng.sacerasi.job.producerCodaVerificaH.ejb.ProducerCodaVerificaHEjb;
import it.eng.sacerasi.web.util.Constants.NomeCoda;
import it.eng.sacerasi.ws.util.Costanti;

/**
 * Classe per gestire i messaggi in coda
 *
 * @author Agati_D
 */
@Stateless
@LocalBean
public class MonitorCoda {

    private static final Logger LOG = LoggerFactory.getLogger(MonitorCoda.class);
    public static final String ERRORE_MESSAGGIO_FORMATO_NON_VALIDO = "Errore nel messaggio: formato non valido";
    public static final String JMSX_DELIVERY_COUNT = "JMSXDeliveryCount";

    @Resource(mappedName = "jms/ProducerConnectionFactory")
    private QueueConnectionFactory connFactory;

    @Resource(mappedName = "jms/queue/ProducerCodaVersQueue")
    private Queue producerCodaVersQueue;

    @Resource(mappedName = "jms/dmq")
    private Queue dmqQueue;

    @EJB
    private ProducerCodaVerificaHEjb producerCodaVerificaHEjb;

    @EJB
    private CodaHelper codaHelper;

    @EJB
    private MonitorCoda me;

    /**
     * Recupera i messaggi dalla coda
     *
     * @param queue
     *            nome coda
     * @param messageSelector
     *            selettore
     *
     * @return lista di messaggi
     *
     * @throws JMSException
     *             errore generico
     */
    public List<InfoCoda> retrieveMsgInQueue(String queue, String messageSelector) throws JMSException {
        List<InfoCoda> msgList = new ArrayList<>();
        QueueConnection queueConn = null;
        QueueBrowser queueBrowser = null;
        QueueSession queueSession = null;
        try {
            queueConn = connFactory.createQueueConnection();
            queueSession = queueConn.createQueueSession(true, Session.AUTO_ACKNOWLEDGE);
            if (queue.equals(NomeCoda.producerCodaVersQueue.name())) {
                queueBrowser = queueSession.createBrowser(producerCodaVersQueue);
            } else if (queue.equals(NomeCoda.dmqQueue.name())) {
                queueBrowser = queueSession.createBrowser(dmqQueue);
            } else {
                // coda non presente
            }
            Enumeration<?> e = queueBrowser.getEnumeration();

            while (e.hasMoreElements()) {

                InfoCoda infoCoda = new InfoCoda();
                Message textMessage = (Message) e.nextElement();
                /*
                 * se il selettore in input non è valorizzato o coincide con il selettore presente nel messaggio, allora
                 * il messaggio va visualizzato
                 */
                if (messageSelector == null || textMessage.getStringProperty("queueType").equals(messageSelector)) {

                    infoCoda.setMessageSelector(textMessage.getStringProperty("queueType"));

                    infoCoda.setSentTimestamp(new Date(textMessage.getJMSTimestamp()));

                    // Mai disponibili su HornetQ/Jboss
                    if (textMessage.getStringProperty("JMS_SUN_DMQ_UNDELIVERED_COMMENT") != null) {
                        infoCoda.setUndeliveredComment(
                                textMessage.getStringProperty("JMS_SUN_DMQ_UNDELIVERED_COMMENT"));
                    }
                    if (textMessage.getStringProperty("JMS_SUN_DMQ_UNDELIVERED_REASON") != null) {
                        infoCoda.setUndeliveredReason(textMessage.getStringProperty("JMS_SUN_DMQ_UNDELIVERED_REASON"));
                    }
                    if (textMessage.getStringProperty("JMS_SUN_DMQ_UNDELIVERED_TIMESTAMP") != null) {
                        long millis = Long
                                .parseLong(textMessage.getStringProperty("JMS_SUN_DMQ_UNDELIVERED_TIMESTAMP"));
                        infoCoda.setUndeliveredTimestamp(new Date(millis));
                    }

                    if (textMessage.getStringProperty(JMSX_DELIVERY_COUNT) != null) {
                        infoCoda.setDeliveryCount(Integer.parseInt(textMessage.getStringProperty(JMSX_DELIVERY_COUNT)));
                    }
                    String messageID = textMessage.getJMSMessageID();
                    infoCoda.setMessageID(messageID);

                    if (textMessage instanceof TextMessage) {
                        ObjectMapper mapper = new ObjectMapper();
                        Object genericPayload = null;
                        try {
                            // deserializzo il messaggio
                            if (textMessage.getStringProperty(Costanti.JMSMsgProperties.MSG_K_PAYLOAD_TYPE) != null) {
                                switch (textMessage.getStringProperty(Costanti.JMSMsgProperties.MSG_K_PAYLOAD_TYPE)) {
                                case Costanti.PAYLOAD_TYPE_CODA_VERS:
                                    genericPayload = mapper.readValue(((TextMessage) textMessage).getText(),
                                            Payload.class);
                                    break;
                                case Costanti.PAYLOAD_TYPE_VERIFICAH:
                                    genericPayload = mapper.readValue(((TextMessage) textMessage).getText(),
                                            PayloadCdPrepXml.class);
                                    break;
                                default:
                                    throw new MessageFormatException("Errore nel messaggio: payload Type not define");
                                }
                            }
                        } catch (MessageFormatException ex) {
                            LOG.error(ERRORE_MESSAGGIO_FORMATO_NON_VALIDO);
                            throw new JMSException(ERRORE_MESSAGGIO_FORMATO_NON_VALIDO);
                        }
                        if (genericPayload instanceof Payload) {
                            Payload payload = (Payload) genericPayload;
                            infoCoda.setObjectId(payload.getObjectId());
                            infoCoda.setUnitaDocSessionId(payload.getUnitaDocSessionId());
                            infoCoda.setCdRegistroUnitaDocSacer(payload.getCdRegistroUnitaDocSacer());
                            infoCoda.setAaUnitaDocSacer(payload.getAaUnitaDocSacer());
                            infoCoda.setCdKeyUnitaDocSacer(payload.getCdKeyUnitaDocSacer());
                        } else if (genericPayload instanceof PayloadCdPrepXml) {
                            PayloadCdPrepXml payloadCdPrepXml = (PayloadCdPrepXml) genericPayload;
                            infoCoda.setObjectId(payloadCdPrepXml.getIdPigObject());
                            infoCoda.setUnitaDocSessionId(payloadCdPrepXml.getIdLastSessioneIngest());
                        }
                        msgList.add(infoCoda);
                    }
                }
            }
        } catch (Exception ex) {
            throw new JMSException("Errore nella lettura dalla coda '" + queue + "' con selettore " + messageSelector);
        } finally {
            if (queueBrowser != null) {
                queueBrowser.close();
            }
            if (queueSession != null) {
                queueSession.close();
            }
            if (queueConn != null) {
                queueConn.close();
            }
        }
        return msgList;
    }

    /**
     *
     * @param queue
     *            nome coda
     * @param messageSelector
     *            selettore
     *
     * @return elementi di tipo {@link InfoCoda}
     *
     * @throws JMSException
     *             errore generico
     */
    public List<InfoCoda> retrieveGenericMsgInQueue(String queue, String messageSelector) throws JMSException {
        List<InfoCoda> msgList = new ArrayList<>();
        QueueConnection queueConn = null;
        QueueBrowser queueBrowser = null;
        QueueSession queueSession = null;
        try {
            queueConn = connFactory.createQueueConnection();
            queueSession = queueConn.createQueueSession(true, Session.AUTO_ACKNOWLEDGE);
            if (queue.equals(NomeCoda.producerCodaVersQueue.name())) {
                queueBrowser = queueSession.createBrowser(producerCodaVersQueue);
            } else if (queue.equals(NomeCoda.dmqQueue.name())) {
                queueBrowser = queueSession.createBrowser(dmqQueue, messageSelector);
            }
            Enumeration<?> e = queueBrowser.getEnumeration();

            while (e.hasMoreElements()) {

                InfoCoda infoCoda = new InfoCoda();
                Message objMessage = (Message) e.nextElement();

                /*
                 * JMS Message Metadata custom info (PARER) a) queueType
                 */
                if (objMessage.getStringProperty(Costanti.JMSMsgProperties.MSG_K_QUEUETYPE) != null) {
                    infoCoda.setMessageSelector(
                            objMessage.getStringProperty(Costanti.JMSMsgProperties.MSG_K_QUEUETYPE));
                } else {
                    continue; // passa al messaggio successivo (Nota: avendo inserito un selettore, caso che non
                              // dovrebbe mai verificarsi)
                }

                infoCoda.setSentTimestamp(new Date(objMessage.getJMSTimestamp()));

                infoCoda.setMessageID(objMessage.getJMSMessageID());

                if (objMessage.getStringProperty(JMSX_DELIVERY_COUNT) != null) {
                    infoCoda.setDeliveryCount(Integer.parseInt(objMessage.getStringProperty(JMSX_DELIVERY_COUNT)));
                }

                msgList.add(infoCoda);
            }
        } catch (Exception ex) {
            throw new JMSException("Errore nella lettura dalla coda '" + queue + "' con selettore " + messageSelector);
        } finally {
            if (queueBrowser != null) {
                queueBrowser.close();
            }
            if (queueSession != null) {
                queueSession.close();
            }
            if (queueConn != null) {
                queueConn.close();
            }
        }
        return msgList;
    }

    /**
     * Effettua il redelivery di un messaggio alla coda specificata consumandolo dalla coda di origine e inviandolo alla
     * coda di destinazione
     *
     * @param msgID
     *            id messaggio
     * @param queueName
     *            nome coda
     *
     * @return L'id del messaggio inviato
     *
     * @throws ParerInternalError
     *             errore generico
     * @throws JMSException
     *             errore generico
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public String redeliveryMsg(String msgID, String queueName) throws ParerInternalError, JMSException {
        Message message = me.readMsgFromQueueAtomic(msgID, queueName);
        String id = message.getJMSMessageID();
        me.deliveryMsg(message);
        return id;
    }

    /**
     * Invia un messaggio alla coda
     *
     * @param message
     *            dto messaggio {@link Message}
     *
     * @throws JMSException
     *             errore generico
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void deliveryMsg(Message message) throws JMSException {
        QueueConnection queueConn = null;
        QueueSession queueSession = null;
        try {
            // ATTENZIONE!!! senza la riga sottostante da questo errore:
            // javax.jms.JMSException: Could not create a session: Only allowed one session per connection. See the J2EE
            // spec, e.g. J2EE1.4 Section 6.6
            queueConn = connFactory.createQueueConnection();
            queueSession = queueConn.createQueueSession(true, Session.AUTO_ACKNOWLEDGE);
            QueueSender qSender = queueSession.createSender(producerCodaVersQueue);
            qSender.send(message);
        } catch (Exception ex) {
            LOG.error("Eccezione", ex);
            throw new JMSException("Errore nella lettura dalla coda morta");
        } finally {
            if (queueSession != null) {
                queueSession.close();
            }
            if (queueConn != null) {
                queueConn.close();
            }
        }
    }

    /**
     * Legge e consuma il messaggio specificato. La lettura viene effettuata in modo atomico.
     *
     * @param msgID
     *            id messaggio
     * @param queueName
     *            nome coda
     *
     * @return il messaggio letto e consumato
     *
     * @throws JMSException
     *             errore generico
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Message readMsgFromQueueAtomic(String msgID, String queueName) throws JMSException {
        return me.readMsgFromQueue(msgID, queueName);
    }

    /**
     * Legge e consuma il messaggio specificato
     *
     * @param msgID
     *            id messaggio
     * @param queueName
     *            nome coda
     *
     * @return il messaggio letto e consumato
     *
     * @throws JMSException
     *             errore generico
     */
    public Message readMsgFromQueue(String msgID, String queueName) throws JMSException {
        QueueConnection queueConn = null;
        QueueSession queueSession = null;
        try {
            // A cosa serve questo sleep?
            Thread.sleep(4000);
            QueueReceiver qReceiver = null;
            queueConn = connFactory.createQueueConnection();
            // E' corretto effettare esplicitamente lo start?
            queueConn.start();
            queueSession = queueConn.createQueueSession(true, Session.AUTO_ACKNOWLEDGE);
            if (queueName.equals(NomeCoda.producerCodaVersQueue.name())) {
                qReceiver = queueSession.createReceiver(producerCodaVersQueue, "JMSMessageID='" + msgID + "'");
            } else if (queueName.equals(NomeCoda.dmqQueue.name())) {
                qReceiver = queueSession.createReceiver(dmqQueue, "JMSMessageID='" + msgID + "'");
            } else {
                throw new JMSException("Errore durante la lettura del messaggio con id " + msgID + " dalla coda "
                        + queueName + ": coda non presente");
            }
            Message message = qReceiver.receive(6000L);
            if (message == null) {
                throw new JMSException(
                        "Errore durante il recupero del messaggio con id " + msgID + " dalla coda " + queueName);
            }
            return message;
        } catch (JMSException e) {
            throw e;
        } catch (Exception ex) {
            LOG.error("Eccezione", ex);
            throw new JMSException(
                    "Errore durante la lettura del messaggio con id " + msgID + " dalla coda " + queueName);
        } finally {
            if (queueSession != null) {
                queueSession.close();
            }
            if (queueConn != null) {
                queueConn.close();
            }
        }
    }

    /**
     * Cancella il messaggio dalla coda
     *
     * @param msgID
     *            id messaggio
     * @param queueName
     *            nome coda
     *
     * @return L'id del messaggio eliminato
     *
     * @throws JMSException
     *             errore generico
     */
    public String deleteMsgFromQueue(String msgID, String queueName) throws JMSException {
        Message message = me.readMsgFromQueue(msgID, queueName);
        return message.getJMSMessageID();
    }

    /**
     * Controlla la presenza di un messaggio in coda
     *
     * @param messageSelector
     *            selettore
     * @param parToCheck
     *            numero parametro da verificare
     *
     * @return Il numero di occorrenze del messaggio cercato all'interno della coda
     *
     * @throws JMSException
     *             errore generico
     */
    public int checkMsgInQueue(String messageSelector, BigDecimal parToCheck) throws JMSException {
        int msgCount = 0;
        long unitaDocSessionId;
        long idObject;
        QueueConnection queueConn = null;
        QueueSession queueSession = null;
        QueueBrowser queueBrowser = null;

        ObjectMapper mapper = new ObjectMapper();
        try {
            queueConn = connFactory.createQueueConnection();
            queueSession = queueConn.createQueueSession(true, Session.AUTO_ACKNOWLEDGE);
            queueBrowser = queueSession.createBrowser(producerCodaVersQueue);
            Enumeration<?> e = queueBrowser.getEnumeration();
            while (e.hasMoreElements()) {
                TextMessage textMessage = (TextMessage) e.nextElement();
                Object genericPayload = null;
                try {
                    // deserializzo il messaggio
                    if (textMessage.getStringProperty(Costanti.JMSMsgProperties.MSG_K_PAYLOAD_TYPE) != null) {
                        switch (textMessage.getStringProperty(Costanti.JMSMsgProperties.MSG_K_PAYLOAD_TYPE)) {
                        case Costanti.PAYLOAD_TYPE_CODA_VERS:
                            genericPayload = mapper.readValue(textMessage.getText(), Payload.class);
                            break;
                        case Costanti.PAYLOAD_TYPE_VERIFICAH:
                            genericPayload = mapper.readValue(textMessage.getText(), PayloadCdPrepXml.class);
                            break;
                        default:
                            throw new MessageFormatException("Errore nel messaggio: payload Type not define");
                        }
                    }
                } catch (MessageFormatException ex) {
                    LOG.error(ERRORE_MESSAGGIO_FORMATO_NON_VALIDO);
                    throw new JMSException(ERRORE_MESSAGGIO_FORMATO_NON_VALIDO);
                }
                if (genericPayload instanceof Payload) {
                    Payload payload = (Payload) genericPayload;
                    unitaDocSessionId = payload.getUnitaDocSessionId();
                    if (new BigDecimal(unitaDocSessionId).equals(parToCheck)) {
                        msgCount++;
                    }
                } else if (genericPayload instanceof PayloadCdPrepXml) {
                    PayloadCdPrepXml payloadCdPrepXml = (PayloadCdPrepXml) genericPayload;
                    idObject = payloadCdPrepXml.getIdPigObject();
                    if (new BigDecimal(idObject).equals(parToCheck)) {
                        msgCount++;
                    }
                }
            }
        } catch (Exception ex) {
            throw new JMSException(
                    "Errore nella lettura dalla coda 'producerCodaVersQueue' con selettore " + messageSelector);
        } finally {
            if (queueBrowser != null) {
                queueBrowser.close();
            }
            if (queueSession != null) {
                queueSession.close();
            }
            if (queueConn != null) {
                queueConn.close();
            }
        }
        return msgCount;
    }

    /**
     * Controlla se un messaggio è stato consumato. In base al messageSelector verifica nel db lo stato del messaggio
     * tramite il valore di paramToCheck
     *
     * @param messageSelector
     *            selettore
     * @param paramToCheck
     *            parametro da verificare
     *
     * @return numero messaggi consumati
     */
    public int checkMsgConsumed(String messageSelector, BigDecimal paramToCheck) {
        int msgCount = -1;
        msgCount = codaHelper.checkConsumed(messageSelector, paramToCheck).intValue();
        return msgCount;
    }

    /**
     *
     * @param msgConsumedNum
     *            numero messaggi consumati
     * @param msgDeliveredNum
     *            numero messaggi inviati
     * @param messageSelector
     *            selettore
     *
     * @return La stringa del messaggio da visualizzare all'utente
     *
     * @throws JMSException
     *             errore generico
     */
    public String buildSingleUserMsg(int msgConsumedNum, int msgDeliveredNum, String messageSelector)
            throws JMSException {
        String userMsg = null;
        if (msgConsumedNum > 1 || msgDeliveredNum > 1) {
            throw new JMSException("Errore nella lettura dalla coda 'producerCodaVersQueue' con selettore "
                    + messageSelector + ": messaggio con id object o con id unita doc sessione duplicato");
        } else {
            if (msgConsumedNum == 1) { // messaggio consumato
                userMsg = "Messaggio con selettore " + messageSelector
                        + " consegnato alla coda 'producerCodaVersQueue' e processato dal sistema";
            } else { // messaggio con consumato. Controllo se è stato consegnato
                if (msgDeliveredNum == 1) {
                    userMsg = "Messaggio con selettore " + messageSelector
                            + " inviato e consegnato alla coda 'producerCodaVersQueue' ma non ancora processato dal sistema";
                } else { // messaggio né consegnato né processato. Questo potrebbe essere un problema.
                    userMsg = "Attenzione: il messaggio con selettore " + messageSelector
                            + " è stato inviato alla coda 'producerCodaVersQueue' ma non risulta ancora né consegnato né processato dal sistema";
                }
            }
        }
        return userMsg;
    }

    public String buildMultipleUserMsg(int totMsg, int msgConsumedNum, int msgDeliveredNum) throws JMSException {
        String userMsg = null;
        if (msgConsumedNum > totMsg || msgDeliveredNum > totMsg) {
            throw new JMSException(
                    "Errore nella lettura dalla coda 'producerCodaVersQueue' : messaggi con id object o con id unita doc sessione duplicati");
        } else {
            if (msgConsumedNum == totMsg) { // messaggi consumati
                userMsg = totMsg + " messaggi consegnati alla coda 'producerCodaVersQueue' e processati dal sistema";
            } else { // Presenti messaggi non consumati. Controllo se è stato consegnato
                userMsg = totMsg + " messaggi inviati , " + msgDeliveredNum
                        + " consegnati alla coda 'producerCodaVersQueue', di cui " + msgConsumedNum
                        + " processati dal sistema";
            }
        }
        return userMsg;
    }
}
