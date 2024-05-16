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
import it.eng.sacerasi.common.Constants;
import it.eng.sacerasi.common.Constants.NomiJob;
import it.eng.sacerasi.common.Constants.StatoSessioneIngest;
import it.eng.sacerasi.common.Constants.StatoUnitaDocObject;
import it.eng.sacerasi.common.Constants.TipiRegLogJob;
import it.eng.sacerasi.entity.PigObject;
import it.eng.sacerasi.entity.PigSessioneIngest;
import it.eng.sacerasi.entity.PigUnitaDocObject;
import it.eng.sacerasi.entity.PigUnitaDocSessione;
import it.eng.sacerasi.exception.JMSSendException;
import it.eng.sacerasi.job.coda.dto.Payload;
import it.eng.sacerasi.job.coda.ejb.PrioritaEjb;
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
 *
 * @author Agati_D
 */
@Stateless(mappedName = "PrioritaEjb")
@LocalBean
@Interceptors({ it.eng.sacerasi.aop.TransactionInterceptor.class })
public class ProducerCodaVersamentoEjb {

    private static final String LOG_PREFIX = "PCV ::";
    Logger log = LoggerFactory.getLogger(ProducerCodaVersamentoEjb.class);
    @Resource
    private SessionContext context;
    @EJB
    private CodaHelper codaHelper;
    @EJB
    private JobLogger jobLogger;
    @EJB
    private MessageSenderEjb messageSender;
    @EJB
    private ConfigurationHelper configurationHelper;
    @EJB
    private PrioritaEjb prioritaEjb;

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
        // MEV #15058: Parametro per gestire numero di ud massimo in coda
        final int numMaxUdDaAccodare = Integer
                .parseInt(configurationHelper.getValoreParamApplicByApplic(Constants.NUM_UNITA_DOC_CODA_VERS));
        final int numMaxDicomXgiorno = Integer
                .parseInt(configurationHelper.getValoreParamApplicByApplic(Constants.NUM_MAX_DICOM_XGIORNO));
        // MEV#18661 Determina il numero di studi dicom versati nella data odierna + il numero di StudiDicom in stato
        // IN_CODA_VERS
        final Counter dicomProcessati = new Counter(codaHelper.contaStudiDicomVersatiOggiEInCodaVers());
        final Counter udProcessate = new Counter(0);
        // MAC#31076 - ottengo la lista degli oggetti IN_ATTESA_VERS
        try (Stream<PigObject> oggetti = codaHelper.retrieveObjectsByState(StatoSessioneIngest.IN_ATTESA_VERS)) {
            oggetti.forEach(object -> {
                String infoLogObj = LOG_PREFIX + " idVers=" + object.getPigVer().getIdVers() + ", nmVers="
                        + object.getPigVer().getNmVers() + ", idObject=" + object.getIdObject();
                boolean isLastUdInObj = false;
                // MEV#18661 - Se il numero di dicom versati nel giorno è minore del massimo configurato prosegue
                // altrimenti i prossimi studi dicom li bypasserà
                if (Constants.STUDIO_DICOM.equals(object.getPigTipoObject().getNmTipoObject())
                        && dicomProcessati.get() >= numMaxDicomXgiorno) {
                    log.debug(
                            "Lo StudioDicom {} con id [{}] non è stato processato perché superato il limite di [{}] Dicom versati al giorno",
                            ((object.getDsObject() == null) ? "" : object.getDsObject()), object.getIdObject(),
                            numMaxDicomXgiorno);
                    return;
                }
                BigDecimal idLastSessioneIngest = object.getIdLastSessioneIngest();
                log.debug("{} :: processo oggetto con id {}, objectTypeId={}, idLastSessioneIngest = {}", infoLogObj,
                        object.getIdObject(), object.getPigTipoObject().getIdTipoObject(), idLastSessioneIngest);

                // determino, per l'oggetto, l'insieme delle unità documentarie con stato = DA_VERSARE
                List<Long> unitaDocsIdToProcess = codaHelper.retrieveUnitaDocsIdByIdObjAndState(object.getIdObject(),
                        StatoUnitaDocObject.DA_VERSARE.name());
                log.debug("{} :: trovate {} unita documentarie da processare per l'oggetto {}", infoLogObj,
                        unitaDocsIdToProcess.size(), object.getIdObject());
                // se ho delle UD da processare incremento il contatore degli StudiDicom, voglio evitare di conteggiare
                // Oggetti che per qualche anomalia
                // non hanno UD collegate.
                if (!unitaDocsIdToProcess.isEmpty()
                        && Constants.STUDIO_DICOM.equals(object.getPigTipoObject().getNmTipoObject())) {
                    dicomProcessati.increment();
                }

                for (int k = 0; k < unitaDocsIdToProcess.size(); k++) {
                    String infoLogUd = infoLogObj + ", ud=" + unitaDocsIdToProcess.get(k);
                    if (k == unitaDocsIdToProcess.size() - 1) {
                        isLastUdInObj = true;
                        log.debug("{} :: ultima unità documentaria dell'oggetto {}", infoLogUd, object.getIdObject());
                    }
                    // di ogni ud determino l'identificatore, la chiave e gli xml di versamento a SACER
                    PigUnitaDocObject unitaDoc = codaHelper.findPigUnitaDocObjectById(unitaDocsIdToProcess.get(k));
                    PigUnitaDocSessione unitaDocSessione = codaHelper.retrievePigUnitaDocSessioneByKeyUD(
                            idLastSessioneIngest, unitaDoc.getAaUnitaDocSacer(), unitaDoc.getCdKeyUnitaDocSacer(),
                            unitaDoc.getCdRegistroUnitaDocSacer());
                    // apro una trasazione relativa ad una unità documentaria
                    ProducerCodaVersamentoEjb newProducerCodaVersEjbRef1 = context
                            .getBusinessObject(ProducerCodaVersamentoEjb.class);
                    BigDecimal idAmbienteVers = BigDecimal.valueOf(unitaDoc.getPigObject().getPigTipoObject()
                            .getPigVer().getPigAmbienteVer().getIdAmbienteVers());
                    BigDecimal idVers = BigDecimal
                            .valueOf(unitaDoc.getPigObject().getPigTipoObject().getPigVer().getIdVers());
                    BigDecimal idTipoObject = BigDecimal
                            .valueOf(unitaDoc.getPigObject().getPigTipoObject().getIdTipoObject());
                    String urlServVers = configurationHelper.getValoreParamApplicByTipoObj("DS_URL_SERV_VERS",
                            idAmbienteVers, idVers, idTipoObject);
                    try {
                        newProducerCodaVersEjbRef1.manageUnitaDoc(unitaDoc.getIdUnitaDocObject(),
                                unitaDocSessione.getIdUnitaDocSessione(), urlServVers, isLastUdInObj, infoLogUd);
                    } catch (JMSException | JsonProcessingException | JMSSendException e) {
                        throw new ObjectProcessException(e);
                    }
                    udProcessate.increment();
                    // MEV #15058: Parametro per gestire numero di ud massimo in coda
                    //
                    if (udProcessate.get() == numMaxUdDaAccodare) {
                        throw new MaxUnitaDocInCodaException();
                    }
                }
                if (udProcessate.get() == numMaxUdDaAccodare) {
                    throw new MaxUnitaDocInCodaException();
                }
            });
        } catch (MaxUnitaDocInCodaException e) {
            // non devo fare niente, ho finito e loggo che mi fermo qua
            log.info("{} Raggiunto il limite massimo di {} unità documentarie che si possono mettere in coda",
                    LOG_PREFIX, numMaxUdDaAccodare);
        } catch (RuntimeException e) {
            // c'è stato un errore, registro sul log la fine esecuzione del job segnalando l'errore
            jobLogger.writeLog(NomiJob.PRODUCER_CODA_VERS, TipiRegLogJob.ERRORE, e.getMessage());
            log.debug("{} :: scrivo log fine job {} in {}", LOG_PREFIX, NomiJob.PRODUCER_CODA_VERS,
                    TipiRegLogJob.ERRORE);
        }
        jobLogger.writeLog(NomiJob.PRODUCER_CODA_VERS, TipiRegLogJob.FINE_SCHEDULAZIONE, null);
        log.debug("{} :: nessun errore - scrivo log fine job {}", LOG_PREFIX, NomiJob.PRODUCER_CODA_VERS);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void manageUnitaDoc(long unitaDocId, long unitaDocSessioneId, String urlServVers, boolean isLastUdInObj,
            String infoLogUd) throws JMSException, JsonProcessingException, JMSSendException {
        log.debug("PCV :: manageUnitaDoc processo ud id = {}", unitaDocId);
        PigUnitaDocObject unitaDoc = codaHelper.findLockPigUnitaDocObjectById(unitaDocId);
        PigUnitaDocSessione unitaDocSessione = codaHelper.findLockPigUnitaDocSessioneById(unitaDocSessioneId);
        Payload payload = this.buildPayload(unitaDoc, unitaDocSessioneId, urlServVers);
        String queueToUse = codaHelper.selectQueue(unitaDoc.getNiSizeFileByte());
        log.debug("{} :: dimensione file  = {} coda '{}' selezionata", infoLogUd, unitaDoc.getNiSizeFileByte(),
                queueToUse);
        try {
            log.debug("{} :: inserimento in coda", infoLogUd);
            messageSender.produceMessages(payload, queueToUse);
            log.debug("{} :: messaggio inserito in coda", infoLogUd);
        } catch (JMSSendException ex) {
            log.error(infoLogUd + " :: errore nell'inserimento in coda della unità documentaria con id '" + unitaDocId
                    + "'", ex);
            this.handleSendError(payload.getSessionId().longValue(), unitaDocId);
            throw ex;
        }
        // setto lo stato dell'UD inserita nella coda a IN_CODA_VERS
        unitaDoc.setTiStatoUnitaDocObject(Constants.StatoUnitaDocObject.IN_CODA_VERS.name());
        unitaDocSessione.setTiStatoUnitaDocSessione(Constants.StatoUnitaDocSessione.IN_CODA_VERS.name());
        log.debug("{} :: setto lo stato dell'UD '{}' inserita nella coda in IN_CODA_VERS", infoLogUd, unitaDocId);
        if (isLastUdInObj) {
            // ho finito di processare le unità documentarie dell'oggetto corrente:
            // aggiorno lo stato dell'oggetto e della sua ultima sessione assegnando IN_CODA_VERS
            PigObject object = unitaDoc.getPigObject();
            object.setTiStatoObject(Constants.StatoOggetto.IN_CODA_VERS.name());
            codaHelper.updateLastSessionState(object, Constants.StatoSessioneIngest.IN_CODA_VERS.name());
            log.debug(
                    "{} :: ho finito di processare le ud dell'oggetto '{}': aggiorno lo stato dell'oggetto e della sua ultima sessione in IN_CODA_VERS",
                    infoLogUd, object.getIdObject());
        }
    }

    public void handleSendError(long sessionId, long unitaDocId) {
        PigUnitaDocObject unitaDoc = codaHelper.findPigUnitaDocObjectById(unitaDocId);
        // aggiorno la sessione con dtChiusura pari all'istante corrente e lo stato della sessione tiStato =
        // CHIUSO_ERR_CODA
        PigSessioneIngest session = codaHelper.findPigSessioneIngestById(sessionId);
        codaHelper.updateSession(session, new Date(), Constants.StatoSessioneIngest.CHIUSO_ERR_CODA.name());
        // setto l’indicatore di sessione verificata() a false
        session.setFlSesErrVerif(Constants.DB_FALSE);
        // setto cdErr = PING-PRODCODA-001
        session.setCdErr(MessaggiWSBundle.PING_PRODCODA_001);
        // setto dlErr = Fallita registrazione in coda di versamento per unità doc. <registro>-<anno>-<numero>
        session.setDlErr(
                MessaggiWSBundle.getString(MessaggiWSBundle.PING_PRODCODA_001, unitaDoc.getCdRegistroUnitaDocSacer(),
                        unitaDoc.getAaUnitaDocSacer(), unitaDoc.getCdKeyUnitaDocSacer()));
        // aggiorno l'oggetto riferito alla sessione, assegnando stato = CHIUSO_ERR_CODA
        session.getPigObject().setTiStatoObject(Constants.StatoOggetto.CHIUSO_ERR_CODA.name());
    }

    public Payload buildPayload(PigUnitaDocObject unitaDoc, long unitaDocSessioneId, String urlServVers) {
        Payload payload = new Payload();
        payload.setObjectId(unitaDoc.getPigObject().getIdObject());
        payload.setSessionId(unitaDoc.getPigObject().getIdLastSessioneIngest());
        payload.setUnitaDocId(unitaDoc.getIdUnitaDocObject());
        payload.setUnitaDocSessionId(unitaDocSessioneId);
        // la chiave è formata da: CD_REGISTRO_UNITA_DOC_SACER, AA_UNITA_DOC_SACER, CD_KEY_UNITA_DOC_SACER
        payload.setCdRegistroUnitaDocSacer(unitaDoc.getCdRegistroUnitaDocSacer());
        payload.setAaUnitaDocSacer(unitaDoc.getAaUnitaDocSacer());
        payload.setCdKeyUnitaDocSacer(unitaDoc.getCdKeyUnitaDocSacer());
        payload.setUrlServVersamento(urlServVers);
        // in coda vengono messi solo gli id degli xml per evitare problemi di memoria
        payload.setXmlVersamentoSacerId(codaHelper.retrieveXmlIdByUdIdAndType(unitaDoc.getIdUnitaDocObject(),
                Constants.TipiXmlSacer.XML_VERS.name()));
        payload.setXmlIndiceId(codaHelper.retrieveXmlIdByUdIdAndType(unitaDoc.getIdUnitaDocObject(),
                Constants.TipiXmlSacer.XML_INDICE.name()));

        BigDecimal idAmbienteVers = BigDecimal.valueOf(
                unitaDoc.getPigObject().getPigTipoObject().getPigVer().getPigAmbienteVer().getIdAmbienteVers());
        BigDecimal idVers = BigDecimal.valueOf(unitaDoc.getPigObject().getPigTipoObject().getPigVer().getIdVers());
        BigDecimal idTipoObject = BigDecimal.valueOf(unitaDoc.getPigObject().getPigTipoObject().getIdTipoObject());
        String nmUseridSacer = configurationHelper.getValoreParamApplicByTipoObj("USERID_USER_VERS", idAmbienteVers,
                idVers, idTipoObject);
        String cdPswSacer = configurationHelper.getValoreParamApplicByTipoObj("PSW_USER_VERS", idAmbienteVers, idVers,
                idTipoObject);

        payload.setUserIdSacer(nmUseridSacer);
        payload.setPasswordSacer(cdPswSacer);

        return payload;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void gestisciAging() {
        Stream<PigObject> objects = codaHelper.retrieveObjectsByState(StatoSessioneIngest.IN_ATTESA_VERS);
        objects.forEach(prioritaEjb::valutaEscalation);
    }
}
