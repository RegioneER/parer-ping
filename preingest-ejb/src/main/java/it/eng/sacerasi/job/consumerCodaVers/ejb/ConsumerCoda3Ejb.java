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

package it.eng.sacerasi.job.consumerCodaVers.ejb;

import it.eng.sacerasi.exception.ParerInternalError;
import it.eng.sacerasi.job.coda.ejb.PayloadManagerEjb;

import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.MessageDriven;
import javax.ejb.MessageDrivenContext;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Agati_D
 */
@MessageDriven(name = "ConsumerCoda3", activationConfig = {
	@ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge"),
	@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
	@ActivationConfigProperty(propertyName = "messageSelector", propertyValue = "queueType = 'CODA3'"),
	@ActivationConfigProperty(propertyName = "destination", propertyValue = "jms/queue/ProducerCodaVersQueue"),
	@ActivationConfigProperty(propertyName = "transactionTimeout", propertyValue = "3600") })
@TransactionManagement(TransactionManagementType.CONTAINER)
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class ConsumerCoda3Ejb implements MessageListener {

    private static final String DESC_CONSUMER = "CCV3";

    Logger log = LoggerFactory.getLogger(ConsumerCoda3Ejb.class);

    @Resource
    private MessageDrivenContext mdc;

    @EJB
    private PayloadManagerEjb payloadManagerHelper;

    @Override
    public void onMessage(Message message) {

	log.debug(DESC_CONSUMER + " :: inizio a processare il messaggio ");
	try {

	    payloadManagerHelper.manageMessagePayload(message, DESC_CONSUMER);
	} catch (ParerInternalError ex) {
	    log.error(DESC_CONSUMER
		    + " :: errore nel consumer, rollback transazione: ParerInternalError "
		    + ExceptionUtils.getRootCauseMessage(ex), ex);
	    mdc.setRollbackOnly();
	} catch (SecurityException | IllegalStateException ex) {
	    log.error(DESC_CONSUMER + " :: errore nel consumer: problemi nella transazione"
		    + ExceptionUtils.getRootCauseMessage(ex), ex);
	    throw new EJBException();
	} catch (JMSException ex) {
	    log.error(DESC_CONSUMER + " :: errore nel consumer: JMSException "
		    + ExceptionUtils.getRootCauseMessage(ex), ex);
	    mdc.setRollbackOnly();
	}
	log.debug(DESC_CONSUMER + " :: il consumer ha terminato e committato");
    }
}
