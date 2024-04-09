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

package it.eng.xformer.job.ejb;

import it.eng.sacerasi.entity.PigErrore;
import it.eng.xformer.common.Constants;
import it.eng.sacerasi.entity.PigObject;
import it.eng.sacerasi.entity.PigObjectTrasf;
import it.eng.sacerasi.entity.PigStrumentiUrbanistici;
import it.eng.sacerasi.entity.PigTipoFileObject;
import it.eng.sacerasi.exception.ParerInternalError;
import it.eng.sacerasi.messages.MessaggiHelper;
import it.eng.sacerasi.strumentiUrbanistici.ejb.StrumentiUrbanisticiHelper;
import it.eng.sacerasi.web.helper.ConfigurationHelper;
import it.eng.sacerasi.ws.invioOggettoAsincrono.ejb.InvioOggettoAsincronoEjb;
import it.eng.sacerasi.ws.notificaTrasferimento.dto.FileDepositatoType;
import it.eng.sacerasi.ws.notificaTrasferimento.dto.ListaFileDepositatoType;
import it.eng.sacerasi.ws.notificaTrasferimento.ejb.NotificaTrasferimentoEjb;
import it.eng.sacerasi.ws.response.InvioOggettoAsincronoEstesoRisposta;
import it.eng.sacerasi.ws.response.NotificaTrasferimentoRisposta;
import it.eng.xformer.helper.GenericJobHelper;
import it.eng.xformer.helper.TrasformazioniHelper;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;
import javax.xml.ws.WebServiceException;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import it.eng.parer.objectstorage.exceptions.ObjectStorageException;
import it.eng.sacerasi.entity.PigSisma;
import it.eng.sacerasi.sisma.ejb.SismaHelper;
import org.apache.commons.io.FileUtils;

/**
 * @author Cappelli_F
 */
@Stateless(mappedName = "InviaOggettiGeneratiAPing")
@LocalBean
@Interceptors({ it.eng.sacerasi.aop.TransactionInterceptor.class })
public class InviaOggettiGeneratiAPing {

    private final Logger logger = LoggerFactory.getLogger(InviaOggettiGeneratiAPing.class);

    @EJB
    private GenericJobHelper jobHelper;

    @EJB
    private TrasformazioniHelper trasformazioniHelper;
    @EJB
    private ConfigurationHelper configurationHelper;
    @EJB
    private StrumentiUrbanisticiHelper strumentiUrbanisticiHelper;
    @EJB
    private SismaHelper sismaHelper;

    @EJB
    private NotificaTrasferimentoEjb notificaTrasferimentoEjb;
    @EJB
    private InvioOggettoAsincronoEjb invioOggettoAsincronoEjb;

    @EJB
    private MessaggiHelper messaggiHelper;

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void run() throws ParerInternalError {
        try {
            // prima notifica l'invio
            List<Long> pigObjectsIds = jobHelper.selectPOIDFromQueue(Constants.Stato.TRASFORMATO.name());
            for (Long poid : pigObjectsIds) {
                PigObject po = trasformazioniHelper.findById(PigObject.class, poid);
                logger.info("[InviaOggettiGeneratiAPing] inizio invio oggetto id: " + po.getIdObject() + " nome: "
                        + po.getCdKeyObject());

                List<PigObjectTrasf> pigObjectTrasfs = trasformazioniHelper.searchGeneratedPigObjects(po);

                int packagesCounter = pigObjectTrasfs.size();

                for (PigObjectTrasf pot : pigObjectTrasfs) {
                    PigObject oldPigObject = trasformazioniHelper.searchPigObjectTrasfInPigObjects(pot);
                    // Se l'oggetto trasformato non esiste già in PING o è in errore, allora lo verso
                    if (!isPigObjectValid(oldPigObject)) {
                        sendPigObjectTrasfToPing(pot, packagesCounter);
                    }
                }
                logger.info("[InviaOggettiGeneratiAPing] fine invio oggetto id: " + po.getIdObject() + " nome: "
                        + po.getCdKeyObject());
            }

            // poi se l'oggetto è in Ping con lo stato "IN_ATTESA_FILE"
            pigObjectsIds = jobHelper.selectPOIDFromQueue(Constants.Stato.TRASFORMATO.name());
            for (Long poid : pigObjectsIds) {
                PigObject po = trasformazioniHelper.findById(PigObject.class, poid);
                logger.info("[InviaOggettiGeneratiAPing] inizio notifica oggetto id: " + po.getIdObject() + " nome: "
                        + po.getCdKeyObject());

                List<PigObjectTrasf> pigObjectTrasfs = trasformazioniHelper.searchGeneratedPigObjects(po);

                for (PigObjectTrasf pot : pigObjectTrasfs) {
                    PigObject oldPigObject = trasformazioniHelper.searchPigObjectTrasfInPigObjects(pot);
                    if (oldPigObject != null
                            && oldPigObject.getTiStatoObject().equals(Constants.Stato.IN_ATTESA_FILE.name())) {
                        String dsPathTrasf = pot.getPigVer().getDsPathTrasf();
                        String dsPathInputFtp = pot.getPigVer().getDsPathInputFtp();
                        // quindi i path da dove prendere il paccchetto e il path dove mettere i pacchetti generati.
                        String inputFilename = configurationHelper.getValoreParamApplicByApplic(Constants.ROOT_TRASF)
                                + File.separator + dsPathTrasf + File.separator + pot.getCdKeyObjectTrasf()
                                + File.separator + pot.getCdKeyObjectTrasf() + Constants.STANDARD_PACKAGE_EXTENSION;
                        String outputFilename = configurationHelper.getValoreParamApplicByApplic(Constants.ROOT_FTP)
                                + File.separator + dsPathInputFtp + File.separator + pot.getCdKeyObjectTrasf()
                                + File.separator + pot.getCdKeyObjectTrasf() + Constants.STANDARD_PACKAGE_EXTENSION;

                        Path inputFile = Paths.get(inputFilename);
                        Path outputFile = Paths.get(outputFilename);

                        if (!inputFile.toFile().exists() || !inputFile.toFile().isFile()
                                || !inputFile.toFile().canRead()) {
                            setError(pot, "TRASFORMAZIONE_FILE_NOT_FOUND", "File " + inputFilename + " non trovato.");
                            // segno in errore anche l'oggetto padre
                            jobHelper.changePigObjectAndSessionState(pot.getPigObject(),
                                    Constants.Stato.ERRORE_VERSAMENTO_A_PING.name());

                            // MEV 22064 - trova e modifica lo stato del SU
                            aggiornaStatoEventualeStrumentoUrbanistico(pot.getPigObject().getCdKeyObject(),
                                    PigStrumentiUrbanistici.TiStato.IN_TRASFORMAZIONE,
                                    PigStrumentiUrbanistici.TiStato.ERRORE, "PING-ERRSU27");

                            // MEV 30935 - trova e modifica lo stato del Sisma
                            aggiornaStatoEventualeSisma(pot.getPigObject().getCdKeyObject(),
                                    PigSisma.TiStato.IN_TRASFORMAZIONE, PigSisma.TiStato.ERRORE, "PING-ERRSU27");
                            aggiornaStatoEventualeSisma(pot.getPigObject().getCdKeyObject(),
                                    PigSisma.TiStato.IN_TRASFORMAZIONE_SA, PigSisma.TiStato.ERRORE,
                                    Constants.PING_ERRSSISMA27);

                            // passa all'oggetto successivo
                            continue;
                        } else {
                            try {
                                // MEV 30039 - cancello la cartella se esiste già per fare pulizia.
                                if (Files.exists(outputFile.getParent())) {
                                    FileUtils.deleteDirectory(outputFile.getParent().toFile());
                                }
                                Files.createDirectories(outputFile.getParent());
                                Files.copy(inputFile, outputFile, StandardCopyOption.REPLACE_EXISTING);
                            } catch (IOException ex) {
                                setError(pot, "TRASFORMAZIONE_IO_EXCEPTION",
                                        "Errore nella copia del file " + inputFilename + ".");

                                // segno in errore anche l'oggetto padre
                                jobHelper.changePigObjectAndSessionStateAtomic(pot.getPigObject().getIdObject(),
                                        Constants.Stato.ERRORE_VERSAMENTO_A_PING.name());

                                // MEV 22064 - trova e modifica lo stato del SU
                                aggiornaStatoEventualeStrumentoUrbanistico(pot.getPigObject().getCdKeyObject(),
                                        PigStrumentiUrbanistici.TiStato.IN_TRASFORMAZIONE,
                                        PigStrumentiUrbanistici.TiStato.ERRORE, "PING-ERRSU27");

                                // MEV 30935 - trova e modifica lo stato del Sisma
                                aggiornaStatoEventualeSisma(pot.getPigObject().getCdKeyObject(),
                                        PigSisma.TiStato.IN_TRASFORMAZIONE, PigSisma.TiStato.ERRORE, "PING-ERRSU27");
                                aggiornaStatoEventualeSisma(pot.getPigObject().getCdKeyObject(),
                                        PigSisma.TiStato.IN_TRASFORMAZIONE_SA, PigSisma.TiStato.ERRORE,
                                        Constants.PING_ERRSSISMA27);

                                // passa all'oggetto successivo
                                continue;
                            }
                        }

                        // controlla che le configurazioni necessarie siano corrette.
                        if (pot.getPigTipoObject().getPigTipoFileObjects() == null
                                || pot.getPigTipoObject().getPigTipoFileObjects().isEmpty()) {
                            setError(pot, "TRASFORMAZIONE_BAD_CONFIG", "Nessun tipo file definito per il tipo oggetto "
                                    + pot.getPigTipoObject().getNmTipoObject() + ".");

                            // segno in errore anche l'oggetto padre
                            jobHelper.changePigObjectAndSessionStateAtomic(pot.getPigObject().getIdObject(),
                                    Constants.Stato.ERRORE_VERSAMENTO_A_PING.name());

                            // MEV 22064 - trova e modifica lo stato del SU
                            aggiornaStatoEventualeStrumentoUrbanistico(pot.getPigObject().getCdKeyObject(),
                                    PigStrumentiUrbanistici.TiStato.IN_TRASFORMAZIONE,
                                    PigStrumentiUrbanistici.TiStato.ERRORE, "PING-ERRSU27");

                            // MEV 30935 - trova e modifica lo stato del Sisma
                            aggiornaStatoEventualeSisma(pot.getPigObject().getCdKeyObject(),
                                    PigSisma.TiStato.IN_TRASFORMAZIONE, PigSisma.TiStato.ERRORE, "PING-ERRSU27");
                            aggiornaStatoEventualeSisma(pot.getPigObject().getCdKeyObject(),
                                    PigSisma.TiStato.IN_TRASFORMAZIONE_SA, PigSisma.TiStato.ERRORE,
                                    Constants.PING_ERRSSISMA27);

                            // passa all'oggetto successivo
                            continue;
                        }

                        // il file è trasferito e va notificato a PING
                        notifyFileTransfer(pot);
                    }
                }

                logger.info("[InviaOggettiGeneratiAPing] fine niotifica oggetto id: " + po.getIdObject() + " nome: "
                        + po.getCdKeyObject());
            }

            // fai un ultimo giro e controlla che siano stati versati tutti gli oggetti trasformati
            pigObjectsIds = jobHelper.selectPOIDFromQueue(Constants.Stato.TRASFORMATO.name());
            for (Long poid : pigObjectsIds) {
                PigObject po = trasformazioniHelper.findById(PigObject.class, poid);
                logger.info("[InviaOggettiGeneratiAPing] inizio verifica versamento oggetto id: " + po.getIdObject()
                        + " nome: " + po.getCdKeyObject());

                List<PigObjectTrasf> pigObjectTrasfs = trasformazioniHelper.searchGeneratedPigObjects(po);

                boolean completed = true;

                for (PigObjectTrasf pot : pigObjectTrasfs) {
                    PigObject oldPigObject = trasformazioniHelper.searchPigObjectTrasfInPigObjects(pot);
                    if (oldPigObject == null
                            || (!oldPigObject.getTiStatoObject().equals(Constants.Stato.IN_ATTESA_SCHED.name())
                                    && !oldPigObject.getTiStatoObject().equals(Constants.Stato.CHIUSO_OK.name()))) {
                        completed = false;
                    }
                }

                if (completed) {
                    jobHelper.changePigObjectAndSessionState(po, Constants.Stato.VERSATO_A_PING.name());

                    // MEV 22064 - trova e modifica lo stato del SU
                    aggiornaStatoEventualeStrumentoUrbanistico(po.getCdKeyObject(),
                            PigStrumentiUrbanistici.TiStato.IN_TRASFORMAZIONE,
                            PigStrumentiUrbanistici.TiStato.IN_VERSAMENTO, null);

                    // MEV 30935 - trova e modifica lo stato del Sisma
                    aggiornaStatoEventualeSisma(po.getCdKeyObject(), PigSisma.TiStato.IN_TRASFORMAZIONE,
                            PigSisma.TiStato.IN_VERSAMENTO, null);
                    aggiornaStatoEventualeSisma(po.getCdKeyObject(), PigSisma.TiStato.IN_TRASFORMAZIONE_SA,
                            PigSisma.TiStato.IN_VERSAMENTO_SA, null);
                }
                logger.info("[InviaOggettiGeneratiAPing] fine verifica versamento oggetto id: " + po.getIdObject()
                        + " nome: " + po.getCdKeyObject());
            }
        } catch (Exception ex) {
            String messaggio = "Eccezione imprevista durante il versamento a PING: ";
            messaggio += ExceptionUtils.getRootCauseMessage(ex);
            logger.error(messaggio, ex);
            throw new ParerInternalError(messaggio, ex);
        }
    }

    private void notifyFileTransfer(PigObjectTrasf pot) throws ObjectStorageException {
        try {
            logger.debug("[notifyFileTransfer] inizio su oggetto figlio id: " + pot.getIdObjectTrasf() + " nome: "
                    + pot.getCdKeyObjectTrasf());
            List<PigTipoFileObject> pigTipoFileObjects = pot.getPigTipoObject().getPigTipoFileObjects();
            if (pigTipoFileObjects == null || pigTipoFileObjects.isEmpty()) {
                setError(pot, "BAD_CONFIG", "Per il tipo di oggetto generato non è definito alcun tipo file.");
                // segno in errore anche l'oggetto padre
                if (!pot.getPigObject().getTiStatoObject().equals(Constants.Stato.ERRORE_VERSAMENTO_A_PING.name())) {
                    jobHelper.changePigObjectAndSessionState(pot.getPigObject(),
                            Constants.Stato.ERRORE_VERSAMENTO_A_PING.name());

                    // MEV 22064 - trova e modifica lo stato del SU
                    aggiornaStatoEventualeStrumentoUrbanistico(pot.getPigObject().getCdKeyObject(),
                            PigStrumentiUrbanistici.TiStato.IN_TRASFORMAZIONE, PigStrumentiUrbanistici.TiStato.ERRORE,
                            "PING-ERRSU27");

                    // MEV 30935 - trova e modifica lo stato del Sisma
                    aggiornaStatoEventualeSisma(pot.getPigObject().getCdKeyObject(), PigSisma.TiStato.IN_TRASFORMAZIONE,
                            PigSisma.TiStato.ERRORE, "PING-ERRSU27");
                    aggiornaStatoEventualeSisma(pot.getPigObject().getCdKeyObject(),
                            PigSisma.TiStato.IN_TRASFORMAZIONE_SA, PigSisma.TiStato.ERRORE, Constants.PING_ERRSSISMA27);
                }
                return;
            }

            String nmAmbiente = pot.getPigVer().getPigAmbienteVer().getNmAmbienteVers();
            String nmVersatore = pot.getPigVer().getNmVers();
            String cdKeyObject = pot.getCdKeyObjectTrasf();

            FileDepositatoType fileDepositato = new FileDepositatoType();
            fileDepositato.setNmNomeFile(pot.getCdKeyObjectTrasf() + Constants.STANDARD_PACKAGE_EXTENSION);
            fileDepositato.setNmTipoFile(pot.getPigTipoObject().getPigTipoFileObjects().get(0).getNmTipoFileObject());
            fileDepositato.setDsHashFile(pot.getDsHashFileVers());
            fileDepositato.setCdEncoding(pot.getCdEncodingHashFileVers());
            fileDepositato.setTiAlgoritmoHash(pot.getTiAlgoHashFileVers());

            ListaFileDepositatoType listaFileDepositati = new ListaFileDepositatoType();
            List<FileDepositatoType> fileDepositati = listaFileDepositati.getFileDepositato();
            fileDepositati.add(fileDepositato);

            NotificaTrasferimentoRisposta notificaTrasferimentoRisposta = notificaTrasferimentoEjb
                    .notificaAvvenutoTrasferimentoFile(nmAmbiente, nmVersatore, cdKeyObject, listaFileDepositati);

            switch (notificaTrasferimentoRisposta.getCdEsito()) {
            case "KO":

                setError(pot, notificaTrasferimentoRisposta.getCdErr(), notificaTrasferimentoRisposta.getDsErr());

                if (!pot.getCdErr().equals("666")) {
                    if (!pot.getPigObject().getTiStatoObject()
                            .equals(Constants.Stato.ERRORE_VERSAMENTO_A_PING.name())) {
                        // segno in errore anche l'oggetto padre
                        jobHelper.changePigObjectAndSessionState(pot.getPigObject(),
                                Constants.Stato.ERRORE_VERSAMENTO_A_PING.name());

                        // MEV 22064 - trova e modifica lo stato del SU
                        aggiornaStatoEventualeStrumentoUrbanistico(pot.getPigObject().getCdKeyObject(),
                                PigStrumentiUrbanistici.TiStato.IN_TRASFORMAZIONE,
                                PigStrumentiUrbanistici.TiStato.ERRORE, "PING-ERRSU27");

                        // MEV 30935 - trova e modifica lo stato del Sisma
                        aggiornaStatoEventualeSisma(pot.getPigObject().getCdKeyObject(),
                                PigSisma.TiStato.IN_TRASFORMAZIONE, PigSisma.TiStato.ERRORE, "PING-ERRSU27");
                        aggiornaStatoEventualeSisma(pot.getPigObject().getCdKeyObject(),
                                PigSisma.TiStato.IN_TRASFORMAZIONE_SA, PigSisma.TiStato.ERRORE,
                                Constants.PING_ERRSSISMA27);
                    }
                }

                break;
            case "NO_RISPOSTA":
                // FIXME: questa direi non serve più visto che non sono più ws
                setError(pot, "PING_TIMEOUT", "I servizi di PING non rispondono.");
                break;
            case "WARN":
                // TODO, che warning possibili ci possono essere?
                break;
            case "OK":
                // va tutto bene passa all'oggetto seguente.
                // ma prima pulisci eventuali errori rimasti da un'esecuzione precedente.
                setError(pot, "", "");
                break;
            }
        } catch (WebServiceException ex) {
            setError(pot, "PING_TIMEOUT", "I servizi di PING non rispondono.");
        }

        logger.debug("[notifyFileTransfer] fine su oggetto figlio id: " + pot.getIdObjectTrasf() + " nome: "
                + pot.getCdKeyObjectTrasf());
    }

    private void sendPigObjectTrasfToPing(PigObjectTrasf pot, int packagesCount) {
        try {
            logger.debug("[sendPigObjectTrasfToPing] inizio su oggetto figlio id: " + pot.getIdObjectTrasf() + " nome: "
                    + pot.getCdKeyObjectTrasf());

            String nmAmbiente = pot.getPigVer().getPigAmbienteVer().getNmAmbienteVers();
            String nmVersatore = pot.getPigVer().getNmVers();
            String cdKeyObject = pot.getCdKeyObjectTrasf();
            String dsObject = pot.getDsObjectTrasf();
            String nmTipoObject = pot.getPigTipoObject().getNmTipoObject();
            boolean flFileCifrato = false;
            boolean flForzaWarning = false;
            boolean flForzaAccettazione = false;
            String cdVersioneXml = null; // TODO da gestire sia qui che nel job 2
            String xml = null; // TODO da gestire sia qui che nel job 2
            String nmAmbienteObjectPadre = pot.getPigObject().getPigVer().getPigAmbienteVer().getNmAmbienteVers();
            String nmVersatoreObjectPadre = pot.getPigObject().getPigVer().getNmVers();
            String cdKeyObjectPadre = pot.getPigObject().getCdKeyObject();
            BigDecimal niTotObjectFigli = new BigDecimal(packagesCount);
            BigDecimal pgObjectFiglio = pot.getPgOggettoTrasf();
            BigDecimal niUnitaDocAttese = null; // TODO da gestire sia qui che nel job 2

            InvioOggettoAsincronoEstesoRisposta invioOggettoAsincronoEstesoRisposta = invioOggettoAsincronoEjb
                    .invioOggettoAsincronoEsteso(configurationHelper.getValoreParamApplicByApplic(Constants.IOGP_USER),
                            nmAmbiente, nmVersatore, cdKeyObject, dsObject, nmTipoObject, flFileCifrato, flForzaWarning,
                            flForzaAccettazione, null, cdVersioneXml, xml, nmAmbienteObjectPadre,
                            nmVersatoreObjectPadre, cdKeyObjectPadre, niTotObjectFigli, pgObjectFiglio,
                            niUnitaDocAttese, null, null, null, null);

            switch (invioOggettoAsincronoEstesoRisposta.getCdEsito()) {
            case KO:

                setError(pot, invioOggettoAsincronoEstesoRisposta.getCdErr(),
                        invioOggettoAsincronoEstesoRisposta.getDsErr());

                if (!pot.getCdErr().equals("PING-SENDOBJ-OBJ-010") && !pot.getCdErr().equals("666")) {
                    if (!pot.getPigObject().getTiStatoObject()
                            .equals(Constants.Stato.ERRORE_VERSAMENTO_A_PING.name())) {
                        // segno in errore anche l'oggetto padre
                        jobHelper.changePigObjectAndSessionState(pot.getPigObject(),
                                Constants.Stato.ERRORE_VERSAMENTO_A_PING.name());

                        // MEV 22064 - trova e modifica lo stato del SU
                        aggiornaStatoEventualeStrumentoUrbanistico(pot.getPigObject().getCdKeyObject(),
                                PigStrumentiUrbanistici.TiStato.IN_TRASFORMAZIONE,
                                PigStrumentiUrbanistici.TiStato.ERRORE, "PING-ERRSU27");

                        // MEV 30935 - trova e modifica lo stato del Sisma
                        aggiornaStatoEventualeSisma(pot.getPigObject().getCdKeyObject(),
                                PigSisma.TiStato.IN_TRASFORMAZIONE, PigSisma.TiStato.ERRORE, "PING-ERRSU27");
                        aggiornaStatoEventualeSisma(pot.getPigObject().getCdKeyObject(),
                                PigSisma.TiStato.IN_TRASFORMAZIONE_SA, PigSisma.TiStato.ERRORE,
                                Constants.PING_ERRSSISMA27);
                    }
                }

                break;
            case NO_RISPOSTA:
                // FIXME: questa direi non serve più visto che non sono più ws
                setError(pot, "PING_TIMEOUT", "I servizi di PING non rispondono.");
                break;
            case WARN:
                // TODO, che warning possibili ci possono essere?
                break;
            case OK:
                // va tutto bene passa all'oggetto seguente.
                // ma prima pulisci eventuali errori rimasti da un'esecuzione precedente.
                setError(pot, "", "");
                break;
            }
        } catch (WebServiceException ex) {
            setError(pot, "PING_TIMEOUT", "I servizi di PING non rispondono.");
        }

        logger.debug("[sendPigObjectTrasfToPing] fine su oggetto figlio id: " + pot.getIdObjectTrasf() + " nome: "
                + pot.getCdKeyObjectTrasf());
    }

    private boolean isPigObjectValid(PigObject po) {
        if (po == null) {
            return false;
        }

        switch (po.getTiStatoObject()) {
        case "CHIUSO_ERR":
        case "CHIUSO_ERR_NOTIF":
        case "CHIUSO_ERR_SCHED":
        case "CHIUSO_ERR_CODA":
        case "CHIUSO_ERR_VERS":
        case "CHIUSO_ERR_CRASH_DPI":
        case "CHIUSO_ERR_CRASH_FTP":
        case "CHIUSO_ERR_CRASH_FS_PRIM":
        case "CHIUSO_ERR_CRASH_FS_SECOND":
        case "ANNULLATO":
            return false;
        }

        return true;
    }

    private void setError(PigObjectTrasf pot, String code, String description) {
        pot.setCdErr(code);
        pot.setDlErr(description);
    }

    // MEV 22064
    private void aggiornaStatoEventualeStrumentoUrbanistico(String pigObjectCdKey,
            PigStrumentiUrbanistici.TiStato statoCorrente, PigStrumentiUrbanistici.TiStato nuovoStato,
            String codiceMessaggio) {
        PigStrumentiUrbanistici pigStrumUrb = strumentiUrbanisticiHelper.getPigStrumUrbByCdKeyAndTiStato(pigObjectCdKey,
                statoCorrente);
        if (pigStrumUrb != null) {
            PigStrumentiUrbanistici psu = strumentiUrbanisticiHelper.aggiornaStatoInNuovaTransazione(pigStrumUrb,
                    nuovoStato);

            if (codiceMessaggio != null && !codiceMessaggio.isEmpty()) {
                PigErrore errore = messaggiHelper.retrievePigErrore(codiceMessaggio);
                psu.setCdErr(errore.getCdErrore());
                psu.setDsErr(errore.getDsErrore());
            }

        }
    }

    // MEV 30935
    private void aggiornaStatoEventualeSisma(String pigObjectCdKey, PigSisma.TiStato statoCorrente,
            PigSisma.TiStato nuovoStato, String codiceMessaggio) {
        PigSisma pigSisma = sismaHelper.getPigSismaByCdKeyAndTiStato(pigObjectCdKey, statoCorrente);
        if (pigSisma != null) {
            PigSisma ps = sismaHelper.aggiornaStatoInNuovaTransazione(pigSisma, nuovoStato);

            if (codiceMessaggio != null && !codiceMessaggio.isEmpty()) {
                PigErrore errore = messaggiHelper.retrievePigErrore(codiceMessaggio);
                ps.setCdErr(errore.getCdErrore());
                ps.setDsErr(errore.getDsErrore());
            }

        }
    }

    static public class ConnectionInfo {

        private String user;
        private String password;
        private String serviceURLInvioOggetto;
        private String serviceURLNotificaTrasferimento;
        private Integer timeout;

        public String getUser() {
            return user;
        }

        public void setUser(String user) {
            this.user = user;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getServiceURLInvioOggetto() {
            return serviceURLInvioOggetto;
        }

        public void setServiceURLInvioOggetto(String serviceURLInvioOggetto) {
            this.serviceURLInvioOggetto = serviceURLInvioOggetto;
        }

        public String getServiceURLNotificaTrasferimento() {
            return serviceURLNotificaTrasferimento;
        }

        public void setServiceURLNotificaTrasferimento(String serviceURLNotificaTrasferimento) {
            this.serviceURLNotificaTrasferimento = serviceURLNotificaTrasferimento;
        }

        public Integer getTimeout() {
            return timeout;
        }

        public void setTimeout(Integer timeout) {
            this.timeout = timeout;
        }
    }
}
