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

import it.eng.sacerasi.common.Constants;
import it.eng.sacerasi.entity.PigObject;
import it.eng.sacerasi.entity.PigSessioneIngest;
import it.eng.sacerasi.entity.PigUnitaDocObject;
import it.eng.sacerasi.entity.PigUnitaDocSessione;
import it.eng.sacerasi.entity.PigVers;
import it.eng.sacerasi.exception.JMSSendException;
import it.eng.sacerasi.job.coda.dto.Payload;
import it.eng.sacerasi.job.coda.helper.CodaHelper;
import it.eng.sacerasi.job.ejb.JobLogger;
import it.eng.sacerasi.messages.MessaggiWSBundle;
import it.eng.sacerasi.web.helper.ConfigurationHelper;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Agati_D
 */
@Stateless(mappedName = "ProducerCodaVersamentoEjb")
@LocalBean
@Interceptors({ it.eng.sacerasi.aop.TransactionInterceptor.class })
public class ProducerCodaVersamentoEjb {

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

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void produceQueue() throws Exception {
        boolean finished = false;

        String producer = "PCV";
        log.info("{} :: avvio producer", producer);
        // determino l'insieme dei versatori
        List<PigVers> versatori = codaHelper.retrieveVersatori();
        log.debug("PCV :: trovati {} versatori", versatori.size());
        if (versatori.isEmpty()) {
            log.debug("PCV :: non ci sono versatori da processare: ho finito");
            finished = true;
        }
        // MEV #15058: Parametro per gestire numero di ud massimo in coda
        //
        int numMaxUdDaAccodare = Integer
                .parseInt(configurationHelper.getValoreParamApplicByApplic(Constants.NUM_UNITA_DOC_CODA_VERS));
        int numMaxDicomXgiorno = Integer
                .parseInt(configurationHelper.getValoreParamApplicByApplic(Constants.NUM_MAX_DICOM_XGIORNO));
        /*
         * MEV#18661
         * 
         * Determina il numero di studi dicom versati nella data odierna + il numero di StudiDicom in stato IN_CODA_VERS
         */
        long totStudiDicomVersati = codaHelper.contaStudiDicomVersatiOggiEInCodaVers();
        int udProcessate = 0;
        for (int i = 0; i < versatori.size(); i++) {
            String infoLogVers = producer + " vers = " + versatori.get(i).getIdVers() + ", nome vers "
                    + versatori.get(i).getNmVers();
            log.debug(infoLogVers);
            boolean isLastVers = false;
            // determino, per ogni versatore, l'insieme degli oggetti con stato = IN_ATTESA_VERS
            List<Long> objectsIdToProcess = codaHelper.retrieveObjectsIdByIdVersAndState(versatori.get(i).getIdVers(),
                    Constants.StatoOggetto.IN_ATTESA_VERS.name());

            log.debug(infoLogVers + " :: trovati " + objectsIdToProcess.size()
                    + " oggetti da processare per il versatore '" + versatori.get(i).getNmVers() + "' id = "
                    + versatori.get(i).getIdVers());

            if (i == versatori.size() - 1) {
                isLastVers = true;
                log.debug("{} :: ultimo versatore", infoLogVers);
                if (objectsIdToProcess.isEmpty()) {
                    log.debug("PCV :: non ci sono oggetti da processare: ho finito");
                    finished = true;
                }
            }
            for (int j = 0; j < objectsIdToProcess.size(); j++) {
                String infoLogObj = infoLogVers + ", obj = " + objectsIdToProcess.get(j);
                boolean isLastUdInObj = false;
                boolean isLastObject = false;

                PigObject object = codaHelper.findPigObjectById(objectsIdToProcess.get(j));
                // MEV#18661 - Se il numero di dicom versati nel giorno è minore del massimo configurato prosegue
                // altrimenti i prossimi studi dicom li bypasserà
                if (object.getPigTipoObject().getNmTipoObject().equals(Constants.STUDIO_DICOM)) {
                    if (totStudiDicomVersati < numMaxDicomXgiorno) {
                        totStudiDicomVersati++;
                    } else {
                        log.debug(String.format(
                                "Lo StudioDicom %s con id [%d] non è stato processato perché superato il limite di [%d] Dicom versati al giorno",
                                ((object.getDsObject() == null) ? "" : object.getDsObject()), object.getIdObject(),
                                numMaxDicomXgiorno));
                        continue;
                    }
                }
                // determino l'identificatore dell'oggetto
                Long objectTypeId = object.getIdObject();
                // determino l'identificatore dell'ultima sessione dell'oggetto
                BigDecimal idLastSessioneIngest = object.getIdLastSessioneIngest();
                log.debug(infoLogObj + " :: processo oggetto con id " + objectsIdToProcess.get(j) + ", objectTypeId = "
                        + objectTypeId + ", idLastSessioneIngest = " + idLastSessioneIngest);
                // determino, per l'oggetto, l'insieme delle unità documentarie con stato = DA_VERSARE
                List<Long> unitaDocsIdToProcess = codaHelper.retrieveUnitaDocsIdByIdObjAndState(
                        objectsIdToProcess.get(j), Constants.StatoUnitaDocObject.DA_VERSARE.name());
                log.debug(infoLogObj + " :: trovate " + unitaDocsIdToProcess.size()
                        + " unita documentarie da processare per l'oggetto " + object.getIdObject());

                if (isLastVers && (j == objectsIdToProcess.size() - 1)) {
                    isLastObject = true;
                    log.debug(infoLogObj + " :: ultimo object");
                    if (unitaDocsIdToProcess.isEmpty()) {
                        log.debug("PCV :: non ci sono ud da processare: ho finito");
                        finished = true;
                    }
                }
                for (int k = 0; k < unitaDocsIdToProcess.size(); k++) {
                    String infoLogUd = infoLogVers + ", ud = " + unitaDocsIdToProcess.get(k);
                    if (k == unitaDocsIdToProcess.size() - 1) {
                        isLastUdInObj = true;
                        log.debug(infoLogUd + " :: ultima unità documentaria dell'oggetto " + object.getIdObject());
                    }
                    if (isLastObject && (k == unitaDocsIdToProcess.size() - 1)) {
                        finished = true;
                        log.debug(
                                infoLogUd + " :: ultima unità documentaria dell'ultimo oggetto dell'ultimo versatore");
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
                    // newProducerCodaVersEjbRef1.manageUnitaDoc(unitaDoc.getIdUnitaDocObject(), urlServVers, finished,
                    // isLastUdInObj, infoLogUd);
                    newProducerCodaVersEjbRef1.manageUnitaDoc(unitaDoc.getIdUnitaDocObject(),
                            unitaDocSessione.getIdUnitaDocSessione(), urlServVers, isLastUdInObj, infoLogUd);
                    udProcessate++;
                    // MEV #15058: Parametro per gestire numero di ud massimo in coda
                    //
                    if (udProcessate == numMaxUdDaAccodare) {
                        finished = true;
                        break;
                    }
                }
                if (udProcessate == numMaxUdDaAccodare) {
                    finished = true;
                    break;
                }
            }
            if (udProcessate == numMaxUdDaAccodare) {
                finished = true;
                break;
            }
        }
        if (finished) {
            jobLogger.writeLog(Constants.NomiJob.PRODUCER_CODA_VERS, Constants.TipiRegLogJob.FINE_SCHEDULAZIONE, null);
            log.debug(producer + " :: nessun errore - scrivo log fine job " + Constants.NomiJob.PRODUCER_CODA_VERS);
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void manageUnitaDoc(long unitaDocId, long unitaDocSessioneId, String urlServVers, boolean isLastUdInObj,
            String infoLogUd) throws Exception {
        log.debug("PCV :: manageUnitaDoc processo ud id = " + unitaDocId);
        PigUnitaDocObject unitaDoc = codaHelper.findLockPigUnitaDocObjectById(unitaDocId);
        PigUnitaDocSessione unitaDocSessione = codaHelper.findLockPigUnitaDocSessioneById(unitaDocSessioneId);
        Payload payload = this.buildPayload(unitaDoc, unitaDocSessioneId, urlServVers);
        // TODO: vedere come evitare di fare una query ogni volta
        String queueToUse = codaHelper.selectQueue(unitaDoc.getNiSizeFileByte());
        log.debug(infoLogUd + " :: dimensione file  = " + unitaDoc.getNiSizeFileByte() + " coda '" + queueToUse
                + "' selezionata");
        try {
            log.debug("{} :: inserimento in coda", infoLogUd);
            messageSender.produceMessages(payload, queueToUse);
            log.debug("{} :: messaggio inserito in coda", infoLogUd);
        } catch (JMSSendException ex) {
            log.error(infoLogUd + " :: errore nell'inserimento in coda della unità documentaria con id '" + unitaDocId
                    + "'", ex);
            this.handleSendError(payload.getSessionId().longValue(), unitaDocId, infoLogUd, ex.getMessage());
            throw ex;
        }
        // setto lo stato dell'UD inserita nella coda a IN_CODA_VERS
        unitaDoc.setTiStatoUnitaDocObject(Constants.StatoUnitaDocObject.IN_CODA_VERS.name());
        unitaDocSessione.setTiStatoUnitaDocSessione(Constants.StatoUnitaDocSessione.IN_CODA_VERS.name());
        log.debug(infoLogUd + " :: setto lo stato dell'UD '" + unitaDocId + "' inserita nella coda in IN_CODA_VERS");
        if (isLastUdInObj) {
            // ho finito di processare le unità documentarie dell'oggetto corrente:
            // aggiorno lo stato dell'oggetto e della sua ultima sessione assegnando IN_CODA_VERS
            PigObject object = unitaDoc.getPigObject();
            object.setTiStatoObject(Constants.StatoOggetto.IN_CODA_VERS.name());
            codaHelper.updateLastSessionState(object, Constants.StatoSessioneIngest.IN_CODA_VERS.name());
            log.debug(infoLogUd + " :: ho finito di processare le ud dell'oggetto '" + object.getIdObject() + "': "
                    + "aggiorno lo stato dell'oggetto e della sua ultima sessione in IN_CODA_VERS");
        }
    }

    public void handleSendError(long sessionId, long unitaDocId, String infoLogUd, String exMessage) {
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
        // registro sul log la fine esecuzione del job segnalando l'errore
        jobLogger.writeLog(Constants.NomiJob.PRODUCER_CODA_VERS, Constants.TipiRegLogJob.ERRORE, exMessage);
        log.debug(infoLogUd + " :: scrivo log fine job " + Constants.NomiJob.PRODUCER_CODA_VERS + " in "
                + Constants.TipiRegLogJob.ERRORE);
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

}
