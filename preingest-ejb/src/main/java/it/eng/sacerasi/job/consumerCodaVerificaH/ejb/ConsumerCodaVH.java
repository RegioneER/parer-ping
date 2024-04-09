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

package it.eng.sacerasi.job.consumerCodaVerificaH.ejb;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.eng.sacerasi.job.preparaxml.dto.OggettoInCoda;
import it.eng.sacerasi.job.preparaxml.dto.PayloadCdPrepXml;
import it.eng.sacerasi.job.preparaxml.ejb.ControlliPrepXml;
import it.eng.sacerasi.job.preparaxml.ejb.SalvaErrorePrepXml;
import it.eng.sacerasi.common.Constants;
import it.eng.sacerasi.exception.ParerInternalError;
import it.eng.sacerasi.job.preparaxml.ejb.VerificaHashAsyncEjb;
import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.ejb.MessageDrivenContext;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
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

    @Resource
    private MessageDrivenContext mdc;

    @EJB
    private SalvaErrorePrepXml salvaErrore;
    @EJB
    private ControlliPrepXml controlli;
    @EJB
    private VerificaHashAsyncEjb verificaHashAsync;

    @Override
    public void onMessage(Message message) {

        log.debug(DESC_CONSUMER + " :: inizio a processare il messaggio ");

        try {

            ObjectMapper mapper = new ObjectMapper();
            if (message instanceof TextMessage) {
                TextMessage textMessage = (TextMessage) message;

                PayloadCdPrepXml tmpPayloadCdPrepXml;
                tmpPayloadCdPrepXml = mapper.readValue(textMessage.getText(), PayloadCdPrepXml.class);

                log.info("{} :: processo l'oggetto (IdPigObject) {}", DESC_CONSUMER,
                        tmpPayloadCdPrepXml.getIdPigObject());
                if (controlli.controllaLockStatoVerHash(tmpPayloadCdPrepXml.getIdLastSessioneIngest(),
                        Constants.StatoVerificaHash.IN_CODA)) {
                    OggettoInCoda tmpOggettoInCoda = new OggettoInCoda();
                    tmpOggettoInCoda
                            .setRifPigObject(controlli.riagganciaPigObject(tmpPayloadCdPrepXml.getIdPigObject()));

                    verificaHashAsync.verificaHash(tmpPayloadCdPrepXml.getRootDirectory(), tmpOggettoInCoda);
                    if (tmpOggettoInCoda.getSeverity() != SeverityEnum.ERROR) {
                        controlli.impostaStatoVerHash(tmpPayloadCdPrepXml.getIdLastSessioneIngest(),
                                Constants.StatoVerificaHash.OK);
                    } else {
                        // gestione errore - la chiamata al metodo di chiusura imposta anche a KO lo stato di
                        // verifica hash
                        salvaErrore.chiudiInErrore(tmpPayloadCdPrepXml.getRootDirectory(), tmpOggettoInCoda, true);
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
            log.error(DESC_CONSUMER + " ::  errore nel consumer, rollback transazione: ParerInternalError  ", ex);
            mdc.setRollbackOnly();
        }
        log.debug(DESC_CONSUMER + " :: il consumer ha terminato e committato");
    }
}
