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

package it.eng.sacerasi.ws.notificaTrasferimento.ejb;

import java.util.Calendar;
import java.util.Date;

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
import org.xadisk.connector.outbound.XADiskConnectionFactory;

import it.eng.parer.objectstorage.dto.ObjectStorageBackend;
import it.eng.parer.objectstorage.exceptions.ObjectStorageException;
import it.eng.parer.objectstorage.helper.SalvataggioBackendHelper;
import it.eng.sacerasi.aop.TransactionInterceptor;
import it.eng.sacerasi.common.Constants;
import it.eng.sacerasi.entity.PigErrore;
import it.eng.sacerasi.entity.PigObject;
import it.eng.sacerasi.entity.PigSisma;
import it.eng.sacerasi.entity.PigStrumentiUrbanistici;
import it.eng.sacerasi.exception.ParerInternalError;
import it.eng.sacerasi.messages.MessaggiHelper;
import it.eng.sacerasi.messages.MessaggiWSBundle;
import it.eng.sacerasi.sisma.ejb.SismaHelper;
import it.eng.sacerasi.strumentiUrbanistici.ejb.StrumentiUrbanisticiHelper;
import it.eng.sacerasi.ws.dto.IRispostaWS;
import it.eng.sacerasi.ws.dto.IRispostaWS.SeverityEnum;
import it.eng.sacerasi.ws.dto.RispostaControlli;
import it.eng.sacerasi.ws.ejb.SalvataggioDati;
import it.eng.sacerasi.ws.notificaTrasferimento.dto.FileDepositatoRespType;
import it.eng.sacerasi.ws.notificaTrasferimento.dto.ListaFileDepositatoType;
import it.eng.sacerasi.ws.notificaTrasferimento.dto.NotificaTrasferimentoExt;
import it.eng.sacerasi.ws.notificaTrasferimento.dto.NotificaTrasferimentoInput;
import it.eng.sacerasi.ws.notificaTrasferimento.dto.RispostaNotificaWS;
import it.eng.sacerasi.ws.notificaTrasferimento.dto.WSDescNotificaTrasf;
import it.eng.sacerasi.ws.notificaTrasferimento.helper.NotificaTrasferimentoCheckHelper;
import it.eng.sacerasi.ws.response.NotificaTrasferimentoRisposta;
import it.eng.sacerasi.ws.util.Util;

/**
 *
 * @author Fioravanti_F
 */
@Stateless(mappedName = "NotificaTrasferimento")
@LocalBean
public class NotificaTrasferimentoEjb {

    private static final Logger log = LoggerFactory.getLogger(NotificaTrasferimentoEjb.class);

    @Resource
    private SessionContext ctx;
    @EJB
    private SalvataggioDati salvataggioDati;
    @EJB
    private NotificaTrasferimentoCheckHelper notificaTrasferimentoCheckHelper;
    @EJB
    private StrumentiUrbanisticiHelper strumentiUrbanisticiHelper;
    @EJB
    private SismaHelper sismaHelper;
    @EJB
    private MessaggiHelper messaggiHelper;
    @Resource(mappedName = "jca/xadiskLocal")
    private XADiskConnectionFactory xadCf;

    @EJB
    private SalvataggioBackendHelper salvataggioBackendHelper;

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public NotificaTrasferimentoRisposta notificaAvvenutoTrasferimentoFileInNewTx(String nmAmbiente, String nmVersatore,
            String cdKeyObject, ListaFileDepositatoType listaFileDepositati) throws ObjectStorageException {
        return notificaAvvenutoTrasferimentoFile(nmAmbiente, nmVersatore, cdKeyObject, listaFileDepositati);
    }

    public NotificaTrasferimentoRisposta notificaAvvenutoTrasferimentoFile(String nmAmbiente, String nmVersatore,
            String cdKeyObject, ListaFileDepositatoType listaFileDepositati) throws ObjectStorageException {

        log.debug("Ricevuta richiesta di NotificaTrasferimento con i parametri : nmAmbiente = {}, "
                + "nmVersatore = {} , cdKeyObject = {}", nmAmbiente, nmVersatore, cdKeyObject);
        // Istanzio la response
        RispostaNotificaWS risp = new RispostaNotificaWS();
        risp.setNotificaResponse(new NotificaTrasferimentoRisposta());
        risp.getNotificaResponse().setCdEsito(Constants.EsitoServizio.OK.name());
        // Istanzio l'oggetto che contiene i parametri ricevuti
        // NotificaTrasferimentoInput nti = new NotificaTrasferimentoInput(nmAmbiente, nmVersatore, cdPassword,
        // cdKeyObject, listaFileDepositati);
        NotificaTrasferimentoInput nti = new NotificaTrasferimentoInput(nmAmbiente, nmVersatore, cdKeyObject,
                listaFileDepositati);
        // Istanzio la Ext con l'oggetto creato
        NotificaTrasferimentoExt nte = new NotificaTrasferimentoExt();
        nte.setDescrizione(new WSDescNotificaTrasf());
        nte.setNotificaTrasf(nti);
        nte.setFlAggiornaOggetto(false);

        // Chiamo la classe NotificaTrasferimentoCheck.check() che gestisce i controlli notifica e popola la rispostaWs
        log.debug("Inizio controlli sui parametri");
        notificaTrasferimentoCheckHelper.check(nte, risp);
        log.debug("Fine controlli sui parametri");
        final Date now = Calendar.getInstance().getTime();
        try {
            if (risp.getSeverity() != IRispostaWS.SeverityEnum.ERROR) {
                ctx.getBusinessObject(NotificaTrasferimentoEjb.class).handleCheckSuccess(nte, risp, now);
            } else {
                ctx.getBusinessObject(NotificaTrasferimentoEjb.class).handleCheckError(nte, risp, now);
            }
        } catch (ParerInternalError | ObjectStorageException pie) {
            // RollBack già eseguito
            log.error(pie.getMessage() + " ROLLBACK : " + ctx.getRollbackOnly());
        }

        return risp.getNotificaResponse();
    }

    private void setRispostaWsError(RispostaNotificaWS risp, RispostaControlli tmpRispostaControlli)
            throws ParerInternalError {
        risp.setSeverity(SeverityEnum.ERROR);
        risp.setErrorCode(tmpRispostaControlli.getCodErr());
        risp.setErrorMessage(tmpRispostaControlli.getDsErr());
        risp.getNotificaResponse().setCdEsito(Constants.EsitoServizio.KO.name());
        risp.getNotificaResponse().setCdErr(tmpRispostaControlli.getCodErr());
        risp.getNotificaResponse().setDsErr(tmpRispostaControlli.getDsErr());
        log.debug("Errore Notifica : " + tmpRispostaControlli.getCodErr() + " - " + tmpRispostaControlli.getDsErr());
        log.debug("Fine transazione - ROLLBACK");
        throw new ParerInternalError(tmpRispostaControlli.getCodErr());
        // wtm.rollback(risp);
    }

    @Interceptors({ TransactionInterceptor.class })
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void handleCheckSuccess(NotificaTrasferimentoExt nte, RispostaNotificaWS risp, final Date now)
            throws ParerInternalError, ObjectStorageException {
        // Nessun errore nei controlli
        log.debug("Cancellazione di tutti i file dalla tabella PigFileObject per l'oggetto corrente");
        salvataggioDati.deleteFileObject(nte);

        log.debug("Creazione degli oggetti in PigFileObject");
        RispostaControlli tmpRispostaControlli = salvataggioDati.creaFileObjects(nte, risp);
        if (tmpRispostaControlli.getCodErr() != null) {
            setRispostaWsError(risp, tmpRispostaControlli);
        }
        log.debug("Fine creazione degli oggetti in PigFileObject");

        if (nte.getTipoObject().equals(Constants.TipoVersamento.NO_ZIP.name())
                || nte.getTipoObject().equals(Constants.TipoVersamento.ZIP_NO_XML_SACER.name())
                || nte.getTipoObject().equals(Constants.TipoVersamento.ZIP_CON_XML_SACER.name())) {
            // MEV 31102 - si aggiunge lo stato IN_CODA_HASH per il job controlla hash
            if (risp.getSeverity() != IRispostaWS.SeverityEnum.ERROR) {
                log.debug("Modifica sessione in IN_CODA_HASH");
                tmpRispostaControlli.reset();
                tmpRispostaControlli = salvataggioDati.modificaSessione(nte.getIdLastSession(),
                        Constants.StatoSessioneIngest.IN_CODA_HASH, null, null);
                if (tmpRispostaControlli.getCodErr() != null) {
                    setRispostaWsError(risp, tmpRispostaControlli);
                }
            }

            if (risp.getSeverity() != IRispostaWS.SeverityEnum.ERROR) {
                log.debug("Creazione stato sessione");
                tmpRispostaControlli.reset();
                tmpRispostaControlli = salvataggioDati.creaStatoSessione(nte.getIdLastSession(),
                        Constants.StatoSessioneIngest.IN_CODA_HASH.name(), now);
                if (tmpRispostaControlli.getCodErr() != null) {
                    setRispostaWsError(risp, tmpRispostaControlli);
                }
            }

            if (risp.getSeverity() != IRispostaWS.SeverityEnum.ERROR) {
                log.debug("Modifica oggetto in IN_ATTESA_SCHED");
                tmpRispostaControlli.reset();
                tmpRispostaControlli = salvataggioDati.modificaOggetto(nte.getIdObject(),
                        Constants.StatoOggetto.IN_CODA_HASH);
                if (tmpRispostaControlli.getCodErr() != null) {
                    setRispostaWsError(risp, tmpRispostaControlli);
                }
            }
        } else if (nte.getTipoObject().equals(Constants.TipoVersamento.DA_TRASFORMARE.name())) {
            // MEV 31102 - si aggiunge lo stato IN_CODA_HASH per il job controlla hash
            if (risp.getSeverity() != IRispostaWS.SeverityEnum.ERROR) {
                log.debug("Modifica sessione in DA_TRASFORMARE");
                tmpRispostaControlli.reset();
                tmpRispostaControlli = salvataggioDati.modificaSessione(nte.getIdLastSession(),
                        Constants.StatoSessioneIngest.IN_CODA_HASH, null, null);
                if (tmpRispostaControlli.getCodErr() != null) {
                    setRispostaWsError(risp, tmpRispostaControlli);
                }
            }

            if (risp.getSeverity() != IRispostaWS.SeverityEnum.ERROR) {
                log.debug("Creazione stato sessione");
                tmpRispostaControlli.reset();
                tmpRispostaControlli = salvataggioDati.creaStatoSessione(nte.getIdLastSession(),
                        Constants.StatoSessioneIngest.IN_CODA_HASH.name(), now);
                if (tmpRispostaControlli.getCodErr() != null) {
                    setRispostaWsError(risp, tmpRispostaControlli);
                }
            }

            if (risp.getSeverity() != IRispostaWS.SeverityEnum.ERROR) {
                log.debug("Modifica oggetto in DA_TRASFORMARE");
                tmpRispostaControlli.reset();
                tmpRispostaControlli = salvataggioDati.modificaOggetto(nte.getIdObject(),
                        Constants.StatoOggetto.IN_CODA_HASH);
                if (tmpRispostaControlli.getCodErr() != null) {
                    setRispostaWsError(risp, tmpRispostaControlli);
                }
            }
        }
    }

    @Interceptors({ TransactionInterceptor.class })
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void handleCheckError(NotificaTrasferimentoExt nte, RispostaNotificaWS risp, final Date now)
            throws ParerInternalError, ObjectStorageException {
        RispostaControlli tmpRispCon = new RispostaControlli();
        if (nte.isFlAggiornaOggetto()) {
            // Se l'errore è diverso da PING_NOT_006
            if (!risp.getErrorCode().equals(MessaggiWSBundle.PING_NOT_006)) {
                // Ho passato i primi controlliWS con esito positivo
                if (nte.getIdLastSession() != null) {
                    tmpRispCon = salvataggioDati.modificaSessione(nte.getIdLastSession(),
                            Constants.StatoSessioneIngest.CHIUSO_ERR_NOTIF, risp.getErrorCode(),
                            risp.getErrorMessage());
                    if (tmpRispCon.getCodErr() != null) {
                        setRispostaWsError(risp, tmpRispCon);
                    }
                }

                Date sesDate = tmpRispCon.getrDate();
                log.debug("Creazione stato sessione");
                tmpRispCon.reset();
                tmpRispCon = salvataggioDati.creaStatoSessione(nte.getIdLastSession(),
                        Constants.StatoSessioneIngest.CHIUSO_ERR_NOTIF.name(), sesDate);
                if (tmpRispCon.getCodErr() != null) {
                    setRispostaWsError(risp, tmpRispCon);
                }

                tmpRispCon.reset();
                if (nte.getIdObject() != null) {
                    tmpRispCon = salvataggioDati.modificaOggetto(nte.getIdObject(),
                            Constants.StatoOggetto.CHIUSO_ERR_NOTIF);
                    if (tmpRispCon.getCodErr() != null) {
                        setRispostaWsError(risp, tmpRispCon);
                    }

                    handleStrumUrbError(nte.getIdObject());
                    handleSismaError(nte.getIdObject());
                }
            }
        } else {
            // Non ho passato i primi controlli con esito positivo
            // Creo una nuova sessione con stato CHIUSO_ERR_NOTIF
            tmpRispCon = salvataggioDati.creaSessione(nte, risp.getErrorCode(), risp.getErrorMessage());
            if (tmpRispCon.getCodErr() != null) {
                setRispostaWsError(risp, tmpRispCon);
            } else {
                long idSessione = tmpRispCon.getrLong();
                Date dtSessione = tmpRispCon.getrDate();

                log.debug("Creazione stato sessione");
                tmpRispCon.reset();
                tmpRispCon = salvataggioDati.creaStatoSessione(idSessione,
                        Constants.StatoOggetto.CHIUSO_ERR_NOTIF.name(), dtSessione);
                if (tmpRispCon.getCodErr() != null) {
                    setRispostaWsError(risp, tmpRispCon);
                }
            }
        }

        // MEV 21995 elimina i file se presenti su object storage
        if (salvataggioBackendHelper.isActive() && nte.isFlCancellaFile()) {
            for (FileDepositatoRespType fileDep : risp.getNotificaResponse().getListaFileDepositati()
                    .getFileDepositato()) {
                ObjectStorageBackend config = salvataggioBackendHelper.getObjectStorageConfiguration("VERS_OGGETTO",
                        fileDep.getNmOsBucket());
                if (salvataggioBackendHelper.doesObjectExist(config, fileDep.getNmNomeFileOs())
                        && salvataggioBackendHelper.isActive()) {
                    salvataggioBackendHelper.deleteObject(config, fileDep.getNmNomeFileOs());
                }
            }
        }

        // Elimina file se presenti nella directory ftp
        if (nte.isFlCancellaFile() && nte.getFtpPath() != null) {
            // Utilizzo XADisk per creare una transazione in modo da eliminare i file in maniera atomica.
            tmpRispCon.reset();
            tmpRispCon = Util.rimuoviDir(xadCf, nte.getFtpPath());
            if (tmpRispCon.getCodErr() != null) {
                setRispostaWsError(risp, tmpRispCon);
            }
        }
    }

    // MEV 22064 - errore tecnico (per chiuso err notif)
    private void handleStrumUrbError(Long objectId) {
        PigObject object = strumentiUrbanisticiHelper.getEntityManager().find(PigObject.class, objectId);
        // MEV 22064 - Il SU va in stato ERRORE
        if (object.getPigObjectPadre() != null) {
            // MEV 22064 - ora lo stato da gestire è IN_VERSAMENTO e non più IN_ELABORAZIONE per i SU.
            PigStrumentiUrbanistici pigStrumentiUrbanistici = strumentiUrbanisticiHelper
                    .getPigStrumUrbByCdKeyAndTiStato(object.getPigObjectPadre().getCdKeyObject(),
                            PigStrumentiUrbanistici.TiStato.IN_VERSAMENTO);
            if (pigStrumentiUrbanistici != null) {
                PigErrore errore = messaggiHelper.retrievePigErrore("PING-ERRSU27");
                pigStrumentiUrbanistici = strumentiUrbanisticiHelper.aggiornaStato(pigStrumentiUrbanistici,
                        PigStrumentiUrbanistici.TiStato.ERRORE);
                pigStrumentiUrbanistici.setCdErr(errore.getCdErrore());
                pigStrumentiUrbanistici.setDsErr(errore.getDsErrore());
            }
        }
    }

    // MEV 30935 - errore tecnico (per chiuso err notif)
    private void handleSismaError(Long objectId) {
        PigObject object = sismaHelper.getEntityManager().find(PigObject.class, objectId);

        // MEV 30935 - Il SISMA va in stato ERRORE
        if (object.getPigObjectPadre() != null) {
            // MEV 30935 - ora lo stato da gestire è IN_VERSAMENTO e non più IN_ELABORAZIONE per
            // i SU.
            PigSisma pigSisma = sismaHelper.getPigSismaByCdKeyAndTiStato(object.getPigObjectPadre().getCdKeyObject(),
                    PigSisma.TiStato.IN_VERSAMENTO);

            if (pigSisma == null) {
                pigSisma = sismaHelper.getPigSismaByCdKeyAndTiStato(object.getPigObjectPadre().getCdKeyObject(),
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
}
