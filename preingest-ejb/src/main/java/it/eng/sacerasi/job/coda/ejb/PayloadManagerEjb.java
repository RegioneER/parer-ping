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

package it.eng.sacerasi.job.coda.ejb;

import java.io.File;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;
import javax.resource.ResourceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xadisk.connector.outbound.XADiskConnection;
import org.xadisk.connector.outbound.XADiskConnectionFactory;
import org.xadisk.filesystem.exceptions.DirectoryNotEmptyException;
import org.xadisk.filesystem.exceptions.FileNotExistsException;
import org.xadisk.filesystem.exceptions.FileUnderUseException;
import org.xadisk.filesystem.exceptions.InsufficientPermissionOnFileException;
import org.xadisk.filesystem.exceptions.LockingFailedException;
import org.xadisk.filesystem.exceptions.NoTransactionAssociatedException;

import com.fasterxml.jackson.databind.ObjectMapper;

import it.eng.parer.objectstorage.dto.ObjectStorageBackend;
import it.eng.parer.objectstorage.helper.SalvataggioBackendHelper;
import it.eng.sacerasi.common.Constants;
import it.eng.sacerasi.common.Constants.StatoUnitaDocObject;
import it.eng.sacerasi.common.Constants.StatoUnitaDocSessione;
import it.eng.sacerasi.common.ejb.CommonDb;
import it.eng.sacerasi.entity.PigErrore;
import it.eng.sacerasi.entity.PigFileObject;
import it.eng.sacerasi.entity.PigObject;
import it.eng.sacerasi.entity.PigObjectTrasf;
import it.eng.sacerasi.entity.PigSessioneIngest;
import it.eng.sacerasi.entity.PigSisma;
import it.eng.sacerasi.entity.PigStrumentiUrbanistici;
import it.eng.sacerasi.entity.PigUnitaDocObject;
import it.eng.sacerasi.entity.PigUnitaDocSessione;
import it.eng.sacerasi.exception.ParerInternalError;
import it.eng.sacerasi.helper.RichiestaSacerHelper;
import it.eng.sacerasi.job.coda.dto.Payload;
import it.eng.sacerasi.job.coda.helper.CodaHelper;
import it.eng.sacerasi.job.dto.EsitoConnessione;
import it.eng.sacerasi.job.dto.RichiestaSacerInput;
import it.eng.sacerasi.messages.MessaggiHelper;
import it.eng.sacerasi.messages.MessaggiWSBundle;
import it.eng.sacerasi.sisma.ejb.SismaEjb;
import it.eng.sacerasi.sisma.ejb.SismaHelper;
import it.eng.sacerasi.strumentiUrbanistici.ejb.StrumentiUrbanisticiHelper;
import it.eng.sacerasi.web.helper.ConfigurationHelper;
import it.eng.sacerasi.ws.util.Util;

@Stateless(mappedName = "PayloadManagerEjb")
@LocalBean
public class PayloadManagerEjb {

    private static final Logger log = LoggerFactory.getLogger(PayloadManagerEjb.class);

    @EJB
    private CodaHelper codaHelper;
    @EJB
    private CommonDb commonDb;
    @EJB
    private ConfigurationHelper configurationHelper;
    @EJB
    private StrumentiUrbanisticiHelper strumentiUrbanisticiHelper;
    @EJB
    private SismaEjb sismaEjb;
    @EJB
    private SismaHelper sismaHelper;
    @EJB
    private SalvataggioBackendHelper salvataggioBackendHelper;
    @EJB
    private RichiestaSacerHelper richiestaSacerHelper;
    @EJB
    private MessaggiHelper messaggiHelper;

    @Resource(mappedName = "jca/xadiskLocal")
    private XADiskConnectionFactory xadCf;

    public void manageMessagePayload(Message message, String consumerName) throws JMSException, ParerInternalError {

        ObjectMapper mapper = new ObjectMapper();
        if (message instanceof TextMessage) {
            String consumer = consumerName;
            final UUID uuid = UUID.randomUUID();
            try {
                TextMessage textMessage = (TextMessage) message;
                Payload payload;
                payload = mapper.readValue(textMessage.getText(), Payload.class);
                log.debug("[{}] inizio objectId={}", uuid, payload.getObjectId());
                PigObject object = codaHelper.findPigObjectById(payload.getObjectId());
                log.debug("[{}] lock PigObject '{}'", uuid, payload.getObjectId());
                PigUnitaDocObject unitaDoc = codaHelper.findLockPigUnitaDocObjectById(payload.getUnitaDocId());
                PigUnitaDocSessione unitaDocSessione = codaHelper
                        .findLockPigUnitaDocSessioneById(payload.getUnitaDocSessionId());
                final boolean inCodaVersamento = unitaDoc.getTiStatoUnitaDocObject()
                        .equalsIgnoreCase(StatoUnitaDocObject.IN_CODA_VERS.name())
                        && unitaDocSessione.getTiStatoUnitaDocSessione()
                                .equalsIgnoreCase(StatoUnitaDocSessione.IN_CODA_VERS.name());
                log.debug(
                        "[{}] Stati controllati: TiStatoUnitaDocObject = '{}', TiStatoUnitaDocSessione = '{}', esito controllo = {}",
                        uuid, unitaDoc.getTiStatoUnitaDocObject(), unitaDocSessione.getTiStatoUnitaDocSessione(),
                        inCodaVersamento);
                if (inCodaVersamento) {
                    final String infoLog = "[" + uuid + "] " + consumer + " - idObj = " + object.getIdObject()
                            + ", idSes = " + object.getIdLastSessioneIngest() + ", ud = "
                            + unitaDoc.getIdUnitaDocObject() + " :: ";
                    log.debug("{} ricevuta UD '{}' dell'oggetto '{}'", infoLog, payload.getUnitaDocId(),
                            payload.getObjectId());
                    // per evitare problemi di memoria il produttore mette in coda solo gli id degli xml
                    // e adesso con questi id gli xml vengono recuperati
                    long xmlVersamentoSacerId = payload.getXmlVersamentoSacerId();
                    long xmlIndiceId = payload.getXmlIndiceId();
                    String xmlVersamentoSacer = codaHelper.findXmlSacerUnitaDocById(xmlVersamentoSacerId);
                    String xmlIndice = codaHelper.findXmlSacerUnitaDocById(xmlIndiceId);
                    String urlVersamento = payload.getUrlServVersamento();
                    String userIdSacer = payload.getUserIdSacer();
                    String passwordSacer = payload.getPasswordSacer();
                    // chiamo il Servizio Multimedia di Sacer
                    // recupero il timeout di chiamata al Servizio Multimedia di Sacer
                    Integer timeout = new Integer(
                            configurationHelper.getValoreParamApplicByApplic(Constants.TIMEOUT_VERS_SACER));
                    // creo l'oggetto contenente i suoi parametri (TipoRichiesta = VERSAMENTO)
                    // costante oppure letto dall'xml del SIP versato nel file ZIP
                    RichiestaSacerInput input = new RichiestaSacerInput(
                            RichiestaSacerInput.TipoRichiestaSacer.VERSAMENTO, unitaDoc.getCdVerWsSacer(),
                            urlVersamento, xmlVersamentoSacer, xmlIndice, userIdSacer, passwordSacer, timeout);
                    // chiamo il Servizio Multimedia di Sacer
                    EsitoConnessione esitoConnessione = richiestaSacerHelper.upload(input);

                    String codiceErroreVersamento = esitoConnessione.getCodiceErrore();
                    String codiceEsitoVersamento = esitoConnessione.getCodiceEsito();
                    String messaggioErroreVersamento = esitoConnessione.getMessaggioErrore();

                    boolean incrementaVersateTimeout = false;
                    boolean incrementaVersateOk = false;
                    boolean incrementaVersateErrore = false;

                    if (esitoConnessione.isErroreConnessione()) {
                        log.debug("{} UD '{}' andata in timeout", infoLog, unitaDoc.getIdUnitaDocObject());
                        unitaDoc.setTiStatoUnitaDocObject(Constants.StatoUnitaDocObject.VERSATA_TIMEOUT.name());
                        unitaDocSessione
                                .setTiStatoUnitaDocSessione(Constants.StatoUnitaDocSessione.VERSATA_TIMEOUT.name());
                        incrementaVersateTimeout = true;
                        String messaggioErrore = esitoConnessione.getDescrErrConnessione();
                        log.error("{} {}", infoLog, messaggioErrore);
                    }
                    log.debug("{} ho versato l'UD '{}': codice esito = {}; codice errore = {}; messaggio errore = {}",
                            infoLog, payload.getUnitaDocId(), codiceEsitoVersamento, codiceErroreVersamento,
                            messaggioErroreVersamento);

                    if (codiceEsitoVersamento != null) {
                        if (codiceEsitoVersamento.equals(Constants.EsitoVersamento.POSITIVO.name())
                                || codiceEsitoVersamento.equals(Constants.EsitoVersamento.WARNING.name())) {
                            // se POSITIVO o WARNING modifico lo stato della unità documentaria in VERSATA_OK
                            this.handlePositivoWarning(codiceEsitoVersamento, unitaDoc, unitaDocSessione, infoLog);
                            incrementaVersateOk = true;
                        } else {
                            // se NEGATIVO modifico lo stato della unità documentaria in VERSATA_ERR
                            this.handleNegativo(codiceEsitoVersamento, unitaDoc, unitaDocSessione, infoLog,
                                    codiceErroreVersamento, messaggioErroreVersamento);
                            incrementaVersateErrore = true;
                        }
                    }
                    // se il numero delle unità documentarie versate coincide con il numero di unità documentarie da
                    // versare
                    log.debug(
                            "{} Aggiorno i contatori incrementaVersateOk={},incrementaVersateErrore={},incrementaVersateTimeout={}",
                            infoLog, incrementaVersateOk, incrementaVersateErrore, incrementaVersateTimeout);
                    PigSessioneIngest session = codaHelper.aggiornaContatori(
                            object.getIdLastSessioneIngest().longValueExact(), incrementaVersateOk,
                            incrementaVersateErrore, incrementaVersateTimeout);
                    log.debug("{} Versate {} UD; da versare = {}", infoLog, session.getNiUnitaDocVers(),
                            session.getNiUnitaDocDaVers());
                    if (session.getNiUnitaDocVers().intValue() == session.getNiUnitaDocDaVers().intValue()) {

                        log.debug("{} il numero di UD versate coincide con quello di UD da versare", infoLog);
                        long duplicateUd = codaHelper.countUdInObj(object,
                                Constants.StatoUnitaDocObject.VERSATA_ERR.name(),
                                Constants.COD_VERS_ERR_CHIAVE_DUPLICATA_OLD)
                                + codaHelper.countUdInObj(object, Constants.StatoUnitaDocObject.VERSATA_ERR.name(),
                                        Constants.COD_VERS_ERR_CHIAVE_DUPLICATA_NEW);
                        long timeoutUd = session.getNiUnitaDocVersTimeout().longValue();
                        BigDecimal niUdVersOk = session.getNiUnitaDocVersOk();
                        // MEV 30209 - Quante ud in ERR_666?
                        long err666Ud = codaHelper.countUdInObj(object,
                                Constants.StatoUnitaDocObject.VERSATA_ERR.name(), MessaggiWSBundle.ERR_666);

                        boolean deleteFtp = false;
                        Date now = Calendar.getInstance().getTime();
                        // PUNTO a
                        if (session.getNiUnitaDocVersOk().intValue() == session.getNiUnitaDocDaVers().intValue()
                                || (timeoutUd == 0 && niUdVersOk.add(new BigDecimal(duplicateUd))
                                        .equals(session.getNiUnitaDocDaVers()))) {
                            // tutte le UD contenute nello zip sono state chiuse positivamente o sono fallite per errore
                            // di una chiave già presente e nessuna è andata in timeout
                            log.debug(
                                    "{} tutte le UD contenute nello zip sono state chiuse positivamente o sono fallite per errore di una chiave già presente e nessuna è andata in timeout",
                                    infoLog);
                            log.debug(
                                    "{} Nello zip sono presenti {} UD già presenti in sacer (versate precedentemente)",
                                    infoLog, duplicateUd);
                            // la sessione viene chiusa positivamente ed assume lo stato CHIUSO_OK
                            session.setTiStato(Constants.StatoSessioneIngest.CHIUSO_OK.name());
                            // aggiorno l'oggetto assegnado stato = CHIUSO_OK
                            object.setTiStatoObject(Constants.StatoOggetto.CHIUSO_OK.name());
                            session.setDtChiusura(now);
                            codaHelper.creaStatoSessione(session, Constants.StatoSessioneIngest.CHIUSO_OK.name(), now);
                            log.debug(
                                    "{} chiudo la sessione positivamente: setto stato sessione '{}' e stato oggetto '{}' ",
                                    infoLog, session.getTiStato(), object.getTiStatoObject());
                            deleteFtp = true;

                            log.debug("{} oggetto '{}' => padre '{}'", infoLog, object.getIdObject(),
                                    object.getPigObjectPadre());
                            // Se esiste un oggetto 'padre', definisce il suo stato
                            if (object.getPigObjectPadre() != null) {
                                // MEV#14100 metodo definisciStatoOggettoPadre()
                                definisciStatoOggettoPadre(object.getPigObjectPadre().getIdObject());
                            }
                        } else {
                            // assegno zero al flag di sessione verificata
                            session.setFlSesErrVerif(Constants.DB_FALSE);
                            log.debug("{} imposto flSessioneErrVerificata '{}'", infoLog, session.getFlSesErrVerif());
                            // PUNTO c
                            if (timeoutUd > 0 || err666Ud > 0) {
                                // Esistono UD chiuse con TIMEOUT Imposto oggetto e sessione con stato
                                // CHIUSO_ERR_RECUPERABILE
                                // MEV 30209 Esistono UD chiuse in VERSATA_ERR con codice d'errore ERR_666 -> Imposto
                                // oggetto e sessione con stato CHIUSO_ERR_RECUPERABILE
                                log.debug("{} Nello zip sono presenti {} UD versate con TIMEOUT", infoLog, timeoutUd);
                                log.debug("{} Nello zip sono presenti {} UD versate con ERR_666", infoLog, err666Ud);
                                // la sessione viene chiusa negativamente ed assume lo stato CHIUSO_ERR_RECUPERABILE
                                session.setTiStato(Constants.StatoSessioneIngest.CHIUSO_ERR_RECUPERABILE.name());
                                // assegno codice di errore = PING-CONSCODA-002 e setto dlErr
                                session.setCdErr(MessaggiWSBundle.PING_CONSCODA_002);
                                // setto dlErr = Il versamento di almeno una unità documentaria è fallito
                                session.setDlErr(MessaggiWSBundle.getString(MessaggiWSBundle.PING_CONSCODA_002));
                                // aggiorno l'oggetto assegnado stato = CHIUSO_OK
                                object.setTiStatoObject(Constants.StatoOggetto.CHIUSO_ERR_RECUPERABILE.name());
                                session.setDtChiusura(now);
                                codaHelper.creaStatoSessione(session,
                                        Constants.StatoSessioneIngest.CHIUSO_ERR_RECUPERABILE.name(), now);
                                // PUNTO b
                            } else {
                                long errNotDuplicateUd = session.getNiUnitaDocDaVers().longValue()
                                        - (niUdVersOk.longValue() + duplicateUd);
                                log.debug(
                                        "{} Nello zip sono presenti {} UD versate con errore diverso da CHIAVE DUPLICATA",
                                        infoLog, errNotDuplicateUd);
                                /*
                                 * se almeno una UD è stata chiusa in errore la sessione viene chiusa in errore ed
                                 * assume lo stato CHIUSO_ERR_VERS (anche se potenzialmente alcune UD sono state inviate
                                 * a SACER)
                                 */
                                session.setTiStato(Constants.StatoSessioneIngest.CHIUSO_ERR_VERS.name());
                                // assegno codice di errore = PING-CONSCODA-001 e setto dlErr
                                session.setCdErr(MessaggiWSBundle.PING_CONSCODA_001);
                                // setto dlErr = Il versamento di almeno una unità documentaria è fallito
                                session.setDlErr(MessaggiWSBundle.getString(MessaggiWSBundle.PING_CONSCODA_001));
                                // aggiorno l'oggetto assegnando stato = CHIUSO_ERR_VERS
                                object.setTiStatoObject(Constants.StatoOggetto.CHIUSO_ERR_VERS.name());
                                session.setDtChiusura(now);
                                codaHelper.creaStatoSessione(session, Constants.StatoOggetto.CHIUSO_ERR_VERS.name(),
                                        now);
                                log.debug(
                                        "{} almeno una UD è stata chiusa in errore: sessione e oggetto assumono lo  stato {} e "
                                                + "assegno zero al flag di sessione verificata (anche se potenzialmente alcune UD sono state inviate a SACER)",
                                        infoLog, object.getTiStatoObject());
                                // non cancello più il file perché adesso questo caso viene trattato come quello
                                // timeout. Ovvero verrà recuperato.

                                // MEV 22064 - Il SU va in stato ERRORE
                                if (object.getPigObjectPadre() != null) {
                                    // MEV 22064 - ora lo stato da gestire è IN_VERSAMENTO e non più IN_ELABORAZIONE per
                                    // i SU.
                                    PigStrumentiUrbanistici pigStrumentiUrbanistici = strumentiUrbanisticiHelper
                                            .getPigStrumUrbByCdKeyAndTiStato(
                                                    object.getPigObjectPadre().getCdKeyObject(),
                                                    PigStrumentiUrbanistici.TiStato.IN_VERSAMENTO);
                                    if (pigStrumentiUrbanistici != null) {
                                        PigErrore errore = messaggiHelper.retrievePigErrore("PING-ERRSU27");
                                        pigStrumentiUrbanistici = strumentiUrbanisticiHelper.aggiornaStato(
                                                pigStrumentiUrbanistici, PigStrumentiUrbanistici.TiStato.ERRORE);
                                        pigStrumentiUrbanistici.setCdErr(errore.getCdErrore());
                                        pigStrumentiUrbanistici.setDsErr(errore.getDsErrore());
                                    }
                                }

                                // MEV 30935 - Il SISMA va in stato ERRORE
                                if (object.getPigObjectPadre() != null) {
                                    // MEV 30935 - ora lo stato da gestire è IN_VERSAMENTO e non più IN_ELABORAZIONE per
                                    // i SU.
                                    PigSisma pigSisma = sismaHelper.getPigSismaByCdKeyAndTiStato(
                                            object.getPigObjectPadre().getCdKeyObject(),
                                            PigSisma.TiStato.IN_VERSAMENTO);
                                    if (pigSisma == null) {
                                        pigSisma = sismaHelper.getPigSismaByCdKeyAndTiStato(
                                                object.getPigObjectPadre().getCdKeyObject(),
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
                        if (deleteFtp) {
                            // cancello cartella raggiungibile con il percorso:
                            // - cartella root specificata dal parametro "ROOT_FTP"
                            String rootDir = commonDb.getRootFtpParam();
                            // - cartella definita dal path di input specificato dal versatore
                            String ftpPath = object.getPigVer().getDsPathInputFtp();
                            // - cartella definita dal codice identificante l'oggetto
                            String ftpDir = object.getCdKeyObject();
                            deleteDir(infoLog, rootDir, ftpPath, ftpDir);

                            // MEV25602 - mi assicuro di cancellare anche il file su OS se presente.
                            for (PigFileObject fileObject : object.getPigFileObjects()) {
                                if (fileObject.getNmBucket() != null && fileObject.getCdKeyFile() != null
                                        && salvataggioBackendHelper.isActive()) {
                                    ObjectStorageBackend config = salvataggioBackendHelper
                                            .getObjectStorageConfiguration("VERS_OGGETTO", fileObject.getNmBucket());
                                    salvataggioBackendHelper.deleteObject(config, fileObject.getCdKeyFile());
                                }
                            }
                        }
                    }
                } else {
                    log.error(
                            "Errore, messaggio probabilmente doppio nella coda, PigObject: {}  unitaDoc: {} unitaDocSessione: {}",
                            object.getIdObject(), unitaDoc.getIdUnitaDocObject(),
                            unitaDocSessione.getIdUnitaDocSessione());
                }
                log.debug("[{}] fine objectId={}", uuid, payload.getObjectId());
            } catch (InterruptedException e) {
                // Restore interrupted state...
                Thread.currentThread().interrupt();
            } catch (Exception ex) {
                throw new ParerInternalError("Errore generico nel consumer " + ex);
            }
        }
    }

    /*
     * MEV#14100 - Metodo riutilizzabile che definisce lo stato dell'oggetto padre e gestisce la cancellazione delle
     * cartelle degli oggetti trasformati e quella del padre
     */
    public void definisciStatoOggettoPadre(Long idOggettoPadre)
            throws LockingFailedException, NoTransactionAssociatedException, DirectoryNotEmptyException,
            ResourceException, InterruptedException, FileUnderUseException, InsufficientPermissionOnFileException {
        if (idOggettoPadre != null) {
            codaHelper.getEntityManager().flush();
            // assume il lock sull'oggetto padre
            PigObject oggettoPadre = codaHelper.findPigObjectByIdWithLock(idOggettoPadre);
            if (oggettoPadre != null) {
                String statoPadreCalcolato = codaHelper.getCalcoloStatoObjDaTrasf(oggettoPadre.getIdObject());
                if (statoPadreCalcolato != null) {
                    Date now = Calendar.getInstance().getTime();
                    String rootDirTrasf = commonDb.getRootTrasfParam();
                    PigSessioneIngest sessionePadre = codaHelper.retrieveSessionByObject(oggettoPadre);
                    // Caso CHIUSO_OK, punto a)
                    if (statoPadreCalcolato.equals(Constants.StatoOggetto.CHIUSO_OK.name())) {
                        // Ulteriore modifica successiva che evita di creare 'n' volte lo stato chiuo Ok nella
                        // sessione
                        if (!oggettoPadre.getTiStatoObject().equals(Constants.StatoOggetto.CHIUSO_OK.name())) {
                            oggettoPadre.setTiStatoObject(Constants.StatoOggetto.CHIUSO_OK.name());
                            sessionePadre.setTiStato(Constants.StatoSessioneIngest.CHIUSO_OK.name());
                            sessionePadre.setDtChiusura(now);
                            codaHelper.creaStatoSessione(sessionePadre, Constants.StatoSessioneIngest.CHIUSO_OK.name(),
                                    now);
                            // Punto 4) Elimina le cartelle degli oggetti generati da trasformazione a partire dal
                            // parametro "ROOT_FTP"
                            // MAC#15470
                            if (oggettoPadre.getTiGestOggettiFigli() != null && oggettoPadre.getTiGestOggettiFigli()
                                    .equals(Constants.TipoGestioneOggettiFigli.AUTOMATICA.name())) {
                                for (PigObjectTrasf objectFiglio : oggettoPadre.getPigObjectTrasfs()) {
                                    deleteDir("", rootDirTrasf, objectFiglio.getPigVer().getDsPathTrasf(),
                                            objectFiglio.getDsPath());
                                }
                            }
                            /*
                             * Punto 5) evo#17165 5. se esiste un PIG_STRUMENTI_URBANISTICI avente cd_key =
                             * oggettoPadre.cd_key_object ed il campo ti_stato di PIG_STRUMENTI_URBANISTICI =
                             * ‘IN_ELABORAZIONE’ allora il sistema apre una transazione ed aggiorna lo stato di
                             * PIG_STRUMENTI_URBANISTICI = ‘VERSATO’.
                             */

                            // MEV 22064 - ora lo stato da gestire è IN_VERSAMENTO e non più IN_ELABORAZIONE per i SU.
                            PigStrumentiUrbanistici pigStrumentiUrbanistici = strumentiUrbanisticiHelper
                                    .getPigStrumUrbByCdKeyAndTiStato(oggettoPadre.getCdKeyObject(),
                                            PigStrumentiUrbanistici.TiStato.IN_VERSAMENTO);
                            if (pigStrumentiUrbanistici != null) {
                                strumentiUrbanisticiHelper.aggiornaStato(pigStrumentiUrbanistici,
                                        PigStrumentiUrbanistici.TiStato.VERSATO);
                            }

                            // MEV 30935 - ora lo stato da gestire è IN_VERSAMENTO e non più IN_ELABORAZIONE per i
                            // Sisma.
                            /* Imposta lo stato VERSATO o COMPLETATO nell'eventuale Oggetto SISMA */
                            PigSisma pigSisma = sismaHelper.getPigSismaByCdKeyAndTiStato(oggettoPadre.getCdKeyObject(),
                                    PigSisma.TiStato.IN_VERSAMENTO);

                            if (pigSisma == null) {
                                pigSisma = sismaHelper.getPigSismaByCdKeyAndTiStato(oggettoPadre.getCdKeyObject(),
                                        PigSisma.TiStato.IN_VERSAMENTO_SA);
                            }

                            if (pigSisma != null) {
                                sismaEjb.aggiornaStatoInviatoASacer(pigSisma);
                            }
                        }
                        // Caso ANNULLATO, punto b)
                    } else { // Punto i)
                        if (statoPadreCalcolato.equals(Constants.StatoOggetto.ANNULLATO.name())) {
                            oggettoPadre.setTiStatoObject(Constants.StatoOggetto.ANNULLATO.name());
                            sessionePadre.setTiStato(Constants.StatoSessioneIngest.ANNULLATA.name());
                            sessionePadre.setDtChiusura(now);
                            codaHelper.creaStatoSessione(sessionePadre, Constants.StatoSessioneIngest.ANNULLATA.name(),
                                    now);
                            // Punto 4) Elimina le cartelle degli oggetti generati da trasformazione a partire dal
                            // parametro "ROOT_FTP"
                            // MAC#15470
                            if (oggettoPadre.getTiGestOggettiFigli() != null && oggettoPadre.getTiGestOggettiFigli()
                                    .equals(Constants.TipoGestioneOggettiFigli.AUTOMATICA.name())) {
                                for (PigObjectTrasf objectFiglio : oggettoPadre.getPigObjectTrasfs()) {
                                    deleteDir("", rootDirTrasf, objectFiglio.getPigVer().getDsPathTrasf(),
                                            objectFiglio.getDsPath());
                                }
                            }
                            // Punto 6) Elimina la cartella dell'oggetto padre
                            // deleteDir("", rootDir, oggettoPadre.getPigVer().getDsPathInputFtp(), Punto ii)
                        } else if (statoPadreCalcolato.equals(Constants.StatoOggetto.CHIUSO_ERR_VERS.name())) {
                            oggettoPadre.setTiStatoObject(Constants.StatoOggetto.CHIUSO_ERR_VERS.name());
                            sessionePadre.setTiStato(Constants.StatoSessioneIngest.CHIUSO_ERR_VERS.name());
                            sessionePadre.setDtChiusura(now);
                            codaHelper.creaStatoSessione(sessionePadre,
                                    Constants.StatoSessioneIngest.CHIUSO_ERR_VERS.name(), now);
                            sessionePadre.setCdErr(MessaggiWSBundle.PING_ERR_VERS_001);
                            sessionePadre.setDlErr(MessaggiWSBundle.getString(MessaggiWSBundle.PING_ERR_VERS_001));
                            // Punto 5) Aggiorna tutte le sessioni dell'oggetto padre
                            for (PigSessioneIngest ses : oggettoPadre.getPigSessioneIngests()) {
                                ses.setFlSesErrVerif("1");
                                ses.setFlSesErrNonRisolub("1");
                            }
                            // Punto 6) Elimina le cartelle degli oggetti generati da trasformazione a partire dal
                            // parametro "ROOT_FTP"
                            // MAC#15470
                            if (oggettoPadre.getTiGestOggettiFigli() != null && oggettoPadre.getTiGestOggettiFigli()
                                    .equals(Constants.TipoGestioneOggettiFigli.AUTOMATICA.name())) {
                                for (PigObjectTrasf objectFiglio : oggettoPadre.getPigObjectTrasfs()) {
                                    deleteDir("", rootDirTrasf, objectFiglio.getPigVer().getDsPathTrasf(),
                                            objectFiglio.getDsPath());
                                }
                            }
                            // Punto 7) Elimina la cartella dell'oggetto padre
                            // deleteDir("", rootDir, oggettoPadre.getPigVer().getDsPathInputFtp(),
                        }
                    }
                } else {
                    log.debug("Il calcolo dello stato dell'oggetto padre [{}] ha determinato uno stato NULLO",
                            idOggettoPadre);
                }
            }
        }
    }

    public void deleteDirTrasf(String dsPathTrasf, String dsPath) throws ParerInternalError {
        try {
            String rootDirTrasf = commonDb.getRootTrasfParam();
            deleteDir("", rootDirTrasf, dsPathTrasf, dsPath);
        } catch (InterruptedException e) {
            // Restore interrupted state...
            Thread.currentThread().interrupt();
        } catch (Exception ex) {
            throw new ParerInternalError(ex.getMessage());
        }
    }

    public void deleteDir(String infoLog, String rootDir, String ftpPath, String ftpDir)
            throws InterruptedException, FileUnderUseException, LockingFailedException, DirectoryNotEmptyException,
            InsufficientPermissionOnFileException, NoTransactionAssociatedException, ResourceException {
        XADiskConnection xadConn = null;
        try {
            log.debug("{} cancello la cartella {}{}{}", infoLog, rootDir, ftpPath, ftpDir);

            String directoryToRemove = rootDir + ftpPath + ftpDir;
            // elimina file
            File tmpDirectory = new File(directoryToRemove);
            xadConn = xadCf.getConnection();
            // FF - chiamato il metodo di cancellazione ricorsiva
            // per eliminare la dir temporanea estratta dallo zip
            Util.rimuoviDirRicorsivamente(tmpDirectory, xadConn);
        } catch (FileNotExistsException ex) {
            // la mancata cancellazione della cartella non è un errore.
            log.warn(" {} errore nella cancellazione della cartella {}{}{}: la risorsa non esiste", infoLog, rootDir,
                    ftpPath, ftpDir);
        } finally {
            if (xadConn != null) {
                xadConn.close();
                log.debug("Effettuata chiusura della connessione XADisk");
            }
        }
    }

    public void eliminaCartellaTranneSeStessa(PigObject object) throws ParerInternalError {
        try {
            // cancello cartella raggiungibile con il percorso:
            // - cartella root specificata dal parametro "ROOT_FTP"
            String rootDir = commonDb.getRootFtpParam();
            // - cartella definita dal path di input specificato dal versatore
            String ftpPath = object.getPigVer().getDsPathInputFtp();
            // - cartella definita dal codice identificante l'oggetto
            String ftpDir = object.getCdKeyObject();
            eliminaCartellaTranneSeStessa("", rootDir, ftpPath, ftpDir);
        } catch (InterruptedException e) {
            // Restore interrupted state...
            Thread.currentThread().interrupt();
        } catch (Exception ex) {
            throw new ParerInternalError(ex.getMessage());
        }
    }

    /*
     * Funzione di cancellazione (ricorsiva) di una cartella tranne se stessa (cartellaPrincipale) ed il file
     * "cartellaPrincipale".zip contenuto sotto di lei. Il resto cancella tutto.
     */
    public void eliminaCartellaTranneSeStessa(String infoLog, String rootDir, String ftpPath, String ftpDir)
            throws InterruptedException, FileUnderUseException, LockingFailedException, DirectoryNotEmptyException,
            InsufficientPermissionOnFileException, NoTransactionAssociatedException, ResourceException {
        XADiskConnection xadConn = null;
        try {
            log.debug("{} cancello la cartella {}{}{}", infoLog, rootDir, ftpPath, ftpDir);

            String directoryToRemove = rootDir + ftpPath + ftpDir;
            // elimina file
            File tmpDirectory = new File(directoryToRemove);
            xadConn = xadCf.getConnection();
            // FF - chiamato il metodo di cancellazione ricorsiva
            // per eliminare la dir temporanea estratta dallo zip
            int livello = 0;
            Util.eliminaCartellaRicorsivamenteTranneSeStessa(tmpDirectory, xadConn, ftpDir, livello);
        } catch (FileNotExistsException ex) {
            // la mancata cancellazione della cartella non è un errore.
            log.warn("{} errore nella cancellazione della cartella {}{}{}: la risorsa non esiste", infoLog, rootDir,
                    ftpPath, ftpDir);
        } finally {
            if (xadConn != null) {
                xadConn.close();
                log.debug("Effettuata chiusura della connessione XADisk");
            }
        }
    }

    private void handlePositivoWarning(String codiceEsito, PigUnitaDocObject unitaDoc,
            PigUnitaDocSessione unitaDocSessione, String infoLog) {
        log.debug(
                "{} esito versamento UD '{}' {} modifico lo stato della UD in VERSATA_OK modifico lo stato della UD SESSIONE in VERSATA_OK",
                infoLog, unitaDoc.getIdUnitaDocObject(), codiceEsito);
        unitaDoc.setTiStatoUnitaDocObject(Constants.StatoUnitaDocObject.VERSATA_OK.name());
        unitaDocSessione.setTiStatoUnitaDocSessione(Constants.StatoUnitaDocSessione.VERSATA_OK.name());
        log.debug("{} Stato UD '{}' {}", infoLog, unitaDoc.getIdUnitaDocObject(), unitaDoc.getTiStatoUnitaDocObject());
        // incremento il numero delle unità documentarie versate con successo
    }

    private void handleNegativo(String codiceEsito, PigUnitaDocObject unitaDoc, PigUnitaDocSessione unitaDocSessione,
            String infoLog, String codiceErrore, String messaggioErrore) {
        log.debug("{} Esito versamento UD '{}' {} modifico lo stato della UD SESSIONE in VERSATA_ERR", infoLog,
                unitaDoc.getIdUnitaDocObject(), codiceEsito);
        unitaDocSessione.setTiStatoUnitaDocSessione(Constants.StatoUnitaDocSessione.VERSATA_ERR.name());
        // setto codice e descrizione dell'errore determinati da Sacer
        unitaDocSessione.setCdErrSacer(codiceErrore);
        unitaDocSessione.setDlErrSacer(messaggioErrore);

        log.debug("{} modifico lo stato della UD in VERSATA_ERR", infoLog);
        unitaDoc.setTiStatoUnitaDocObject(Constants.StatoUnitaDocObject.VERSATA_ERR.name());
        // setto codice e descrizione dell'errore determinati da Sacer
        unitaDoc.setCdErrSacer(codiceErrore);
        unitaDoc.setDlErrSacer(messaggioErrore);
    }
}
