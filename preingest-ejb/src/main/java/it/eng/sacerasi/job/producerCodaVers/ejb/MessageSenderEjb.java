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

package it.eng.sacerasi.job.producerCodaVers.ejb;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.eng.sacerasi.exception.JMSSendException;
import it.eng.sacerasi.job.coda.dto.Payload;
import it.eng.sacerasi.ws.util.Costanti;

import javax.annotation.Resource;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

/**
 *
 * @author Agati_D
 */
@Stateless(mappedName = "PrioritaEjb")
@LocalBean
public class MessageSenderEjb {
    @Resource(mappedName = "jms/ProducerConnectionFactory")
    private ConnectionFactory connectionFactory;
    @Resource(mappedName = "jms/queue/ProducerCodaVersQueue")
    private Queue queue;

    public void produceMessages(Payload queueItem, String queueToUse)
            throws JMSException, JsonProcessingException, JMSSendException {
        MessageProducer messageProducer;

        TextMessage textmessage;
        ObjectMapper mapper = new ObjectMapper();
        String payloadQueueItemJson = "";

        Connection connection = connectionFactory.createConnection();
        /*
         * The first parameter of the createSession() method is a Boolean indicating if the session is transacted. If
         * this value is true, several messages can be sent as part of a transaction by invoking the commit() method in
         * the session object. Similarly, they can be rolled back by invoking its rollback() method. The second
         * parameter of the createSession() method indicates how messages are acknowledged by the message receiver.
         * Valid values for this parameter are defined as constants in the javax.jms.Session interface.
         * Session.AUTO_ACKNOWLEDGE: indicates that the session will automatically acknowledge the receipt of a message.
         * Session.CLIENT_ACKNOWLEDGE: indicates that the message receiver must explicitly call the acknowledge() method
         * on the message. Session.DUPS_OK_ACKNOWLEDGE: indicates that the session will lazily acknowledge the receipt
         * of messages. Using this value might result in some messages being delivered more than once.
         */
        Session session = connection.createSession(true, Session.AUTO_ACKNOWLEDGE);
        messageProducer = session.createProducer(queue);

        payloadQueueItemJson = mapper.writeValueAsString(queueItem);

        textmessage = session.createTextMessage();
        // app selector
        textmessage.setStringProperty(Costanti.JMSMsgProperties.MSG_K_APP, Costanti.PING);
        textmessage.setStringProperty("queueType", queueToUse);

        textmessage.setStringProperty(Costanti.JMSMsgProperties.MSG_K_PAYLOAD_TYPE, Costanti.PAYLOAD_TYPE_CODA_VERS);
        textmessage.setText(payloadQueueItemJson);
        try {
            messageProducer.send(textmessage);
        } catch (JMSException ex) {
            throw new JMSSendException("Errore nell'invio del messaggio in coda", ex);
        } finally {
            messageProducer.close();
            session.close();
            connection.close();
        }

    }

}
