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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.SourceLocator;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.file.PathUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import it.eng.parer.objectstorage.helper.SalvataggioBackendHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.parer.kettle.exceptions.KettleException;
import it.eng.parer.kettle.exceptions.KettleServiceException;
import it.eng.sacerasi.corrispondenzeVers.helper.CorrispondenzeVersHelper;
import it.eng.sacerasi.entity.PigErrore;
import it.eng.sacerasi.entity.PigFileObject;
import it.eng.sacerasi.entity.PigKSInstance;
import it.eng.sacerasi.entity.PigObject;
import it.eng.sacerasi.entity.PigObjectTrasf;
import it.eng.sacerasi.entity.PigSessioneIngest;
import it.eng.sacerasi.entity.PigSisma;
import it.eng.sacerasi.entity.PigStrumentiUrbanistici;
import it.eng.sacerasi.entity.PigTipoObject;
import it.eng.sacerasi.entity.PigVers;
import it.eng.sacerasi.entity.PigVersTipoObjectDaTrasf;
import it.eng.sacerasi.entity.XfoFileTrasf;
import it.eng.sacerasi.entity.XfoTrasf;
import it.eng.sacerasi.exception.ParerInternalError;
import it.eng.sacerasi.messages.MessaggiHelper;
import it.eng.sacerasi.sisma.ejb.SismaHelper;
import it.eng.sacerasi.strumentiUrbanistici.ejb.StrumentiUrbanisticiHelper;
import it.eng.sacerasi.viewEntity.PigVLisStrutVersSacer;
import it.eng.sacerasi.viewEntity.PigVValoreParamTrasf;
import it.eng.sacerasi.viewEntity.UsrVAbilStrutSacerXping;
import it.eng.sacerasi.web.helper.ConfigurationHelper;
import it.eng.xformer.common.Constants;
import it.eng.xformer.helper.GenericJobHelper;
import it.eng.xformer.helper.TrasformazioniHelper;
import it.eng.xformer.kettle.ejb.RepositoryManagerEjb;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.xml.bind.DatatypeConverter;

/**
 * @author Cappelli_F
 */
@Stateless(mappedName = "EseguiTrasformazione")
@LocalBean
@Interceptors({ it.eng.sacerasi.aop.TransactionInterceptor.class })
public class EseguiTrasformazione {

    private final Logger logger = LoggerFactory.getLogger(EseguiTrasformazione.class);

    @EJB(mappedName = "java:app/SacerAsync-ejb/RepositoryManagerEjb")
    private RepositoryManagerEjb repositoryManager;

    @EJB(mappedName = "java:app/SacerAsync-ejb/TrasformazioniHelper")
    private TrasformazioniHelper trasformazioniHelper;

    @EJB(mappedName = "java:app/SacerAsync-ejb/ConfigurationHelper")
    private ConfigurationHelper configurationHelper;

    @EJB(mappedName = "java:app/SacerAsync-ejb/CorrispondenzeVersHelper")
    private CorrispondenzeVersHelper corrispondenzeVersHelper;

    @EJB(mappedName = "java:app/SacerAsync-ejb/GenericJobHelper")
    private GenericJobHelper jobHelper;

    @EJB(mappedName = "java:app/SacerAsync-ejb/SalvataggioBackendHelper")
    private SalvataggioBackendHelper salvataggioBackendHelper;

    @EJB
    private MessaggiHelper messaggiHelper;

    @EJB
    private StrumentiUrbanisticiHelper strumentiUrbanisticiHelper;
    @EJB
    private SismaHelper sismaHelper;

    @EJB
    private EseguiTrasformazione self;

    @Lock(LockType.WRITE)
    public void esegui() throws ParerInternalError {
        // Verifiche che oggetti in "TRASFORMAZIONE_NON_ATTIVA" debbano rimanere in tale stato o essere rimessi in
        // trasformazione
        List<Long> pigObjectsIds = jobHelper.selectPOIDFromQueue(Constants.Stato.TRASFORMAZIONE_NON_ATTIVA.name());
        for (Long poid : pigObjectsIds) {
            PigObject po = trasformazioniHelper.findById(PigObject.class, poid);
            XfoTrasf transformation = po.getPigTipoObject().getXfoTrasf();

            // Trova la trasformazione impostata per la coppia tipo oggetto e versatore
            if (transformation != null && !transformation.getFlAttiva().equals(Constants.Flag.NO.getValue())) {
                if (!po.getTiStatoObject().equals(Constants.Stato.DA_TRASFORMARE.name())) {
                    jobHelper.changePigObjectAndSessionStateAtomic(po.getIdObject(),
                            Constants.Stato.DA_TRASFORMARE.name());
                }

                logger.info("Trasformazione {} riattivata per oggetto {}", transformation.getCdTrasf(),
                        po.getCdKeyObject());
            }

        }

        pigObjectsIds = jobHelper.selectPOIDFromQueue(Constants.Stato.DA_TRASFORMARE.name());

        // MEV 23539 - divido i pig object trovati per istanza a cui devono essere inviati
        // e ne trasforamo uno per istanza di kettle.
        List<PigKSInstance> pigKSInstances = trasformazioniHelper.getPigKSInstances();
        for (PigKSInstance pigKSInstance : pigKSInstances) {
            List<Long> poIds = trasformazioniHelper.getFirstPigObjectByInstance(pigKSInstance.getNmKsInstance(),
                    pigObjectsIds);
            for (Long poId : poIds) {
                PigObject po = trasformazioniHelper.findById(PigObject.class, poId);

                // Trova la sessione corrente per il pig object e controlla che la verifica dell'hash sia OK
                PigSessioneIngest currentSession = jobHelper.searchCurrentSession(po);
                if (currentSession != null && currentSession.getTiStatoVerificaHash() != null
                        && currentSession.getTiStatoVerificaHash().equals("OK")) {
                    try {
                        self.transformPigObject(po);
                    } catch (ParerInternalError e) {
                        // calcolo tutte le directory necessarie per la trasformazione
                        String workingDirectory = pigKSInstance.getDirKsInstance();
                        File transformationDirectory = new File(workingDirectory + File.separator + po.getIdObject());
                        cleanTransformationDirectory(transformationDirectory);
                    }

                    // Ne abbiamo trasformato uno, ora esci fino alla prossima invocazione del job.
                    break;
                } else {
                    logger.warn("Scartato pacchetto {} (Verifica hash non 'OK').", po.getIdObject());
                }
            }
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void transformPigObject(PigObject po) throws ParerInternalError {
        jobHelper.changePigObjectAndSessionStateAtomic(po.getIdObject(),
                Constants.Stato.TRASFORMAZIONE_IN_CORSO.name());

        // MEV 22064 - trova e modifica lo stato del SU
        aggiornaStatoEventualeStrumentoUrbanistico(po.getCdKeyObject(), PigStrumentiUrbanistici.TiStato.IN_ELABORAZIONE,
                PigStrumentiUrbanistici.TiStato.IN_TRASFORMAZIONE, null);

        // MEV 30935 - trova e modifica lo stato del Sisma
        aggiornaStatoEventualeSisma(po.getCdKeyObject(), PigSisma.TiStato.IN_ELABORAZIONE,
                PigSisma.TiStato.IN_TRASFORMAZIONE, null);
        aggiornaStatoEventualeSisma(po.getCdKeyObject(), PigSisma.TiStato.IN_ELABORAZIONE_SA,
                PigSisma.TiStato.IN_TRASFORMAZIONE_SA, null);

        long idObject = po.getIdObject();
        po = trasformazioniHelper.findById(PigObject.class, idObject);

        // Estrai tipo oggetto e versatore dal pig object
        String tipoOggetto = po.getPigTipoObject().getNmTipoObject();
        String versatore = po.getPigVer().getNmVers();

        logger.info("Ricerca trasformazione per oggetto {} (versatore: {}, tipo: {} )", po.getCdKeyObject(), versatore,
                tipoOggetto);

        // dal oggetto da trasformare recupera versatore finale e tipo oggetto finale
        PigVersTipoObjectDaTrasf pigVersTipoObjectDaTrasf = trasformazioniHelper.getPigVersTipoObjectDaTrasf(po);

        if (pigVersTipoObjectDaTrasf == null) {
            String messaggio = "Impossibile recuperare versatore o tipo dell'oggetto da trasformare.";
            jobHelper.changePigObjectAndSessionStateAtomic(po.getIdObject(),
                    Constants.Stato.ERRORE_TRASFORMAZIONE.name(), Constants.XF_ERROR_CODE, messaggio);
            logger.error(messaggio);

            // MEV 22064 - trova e modifica lo stato del SU
            aggiornaStatoEventualeStrumentoUrbanistico(po.getCdKeyObject(),
                    PigStrumentiUrbanistici.TiStato.IN_TRASFORMAZIONE, PigStrumentiUrbanistici.TiStato.ERRORE,
                    "PING-ERRSU27");

            // MEV 30935 - trova e modifica lo stato del Sisma
            aggiornaStatoEventualeSisma(po.getCdKeyObject(), PigSisma.TiStato.IN_TRASFORMAZIONE,
                    PigSisma.TiStato.ERRORE, Constants.PING_ERRSSISMA27);
            aggiornaStatoEventualeSisma(po.getCdKeyObject(), PigSisma.TiStato.IN_TRASFORMAZIONE_SA,
                    PigSisma.TiStato.ERRORE, Constants.PING_ERRSSISMA27);

            throw new ParerInternalError(messaggio);
        }

        PigVers versatoreDiDestinazione = pigVersTipoObjectDaTrasf.getPigVersGen();
        PigTipoObject pigTipoObject = pigVersTipoObjectDaTrasf.getPigTipoObjectGen();

        if (versatoreDiDestinazione == null) {
            String messaggio = "Versatore di destinazione assente o mal configurato.";
            jobHelper.changePigObjectAndSessionStateAtomic(po.getIdObject(),
                    Constants.Stato.ERRORE_TRASFORMAZIONE.name(), Constants.XF_ERROR_CODE, messaggio);
            logger.error(messaggio);
            throw new ParerInternalError(messaggio);
        }

        String dsPathTrasf = versatoreDiDestinazione.getDsPathTrasf();
        if (dsPathTrasf == null) {
            String messaggio = "Cartella di destinazione per il versatore di destinazione ("
                    + versatoreDiDestinazione.getDsVers() + ") assente o mal configurata.";
            jobHelper.changePigObjectAndSessionStateAtomic(po.getIdObject(),
                    Constants.Stato.ERRORE_TRASFORMAZIONE.name(), Constants.XF_ERROR_CODE, messaggio);
            logger.error(messaggio);
            throw new ParerInternalError(messaggio);
        }

        String dsPathInputFtp = po.getPigVer().getDsPathInputFtp();
        if (dsPathInputFtp == null) {
            String messaggio = "Cartella ftp per il versatore originale (" + po.getPigVer().getDsVers()
                    + ") assente o mal configurata.";
            jobHelper.changePigObjectAndSessionStateAtomic(po.getIdObject(),
                    Constants.Stato.ERRORE_TRASFORMAZIONE.name(), Constants.XF_ERROR_CODE, messaggio);
            logger.error(messaggio);
            // MEV 22064 - trova e modifica lo stato del SU
            aggiornaStatoEventualeStrumentoUrbanistico(po.getCdKeyObject(),
                    PigStrumentiUrbanistici.TiStato.IN_TRASFORMAZIONE, PigStrumentiUrbanistici.TiStato.ERRORE,
                    "PING-ERRSU27");

            // MEV 30935 - trova e modifica lo stato del Sisma
            aggiornaStatoEventualeSisma(po.getCdKeyObject(), PigSisma.TiStato.IN_TRASFORMAZIONE,
                    PigSisma.TiStato.ERRORE, Constants.PING_ERRSSISMA27);
            aggiornaStatoEventualeSisma(po.getCdKeyObject(), PigSisma.TiStato.IN_TRASFORMAZIONE_SA,
                    PigSisma.TiStato.ERRORE, Constants.PING_ERRSSISMA27);

            throw new ParerInternalError(messaggio);
        }

        // MEV22000 - controllo se il pacchetto è memorizzato su object storage e se object storage è attivo
        // MEV22000 - FIXME? diamo per scontato che un oggetto da trasformare abbia uno ed un solo file allegato,
        // corretto?
        PigFileObject pfo = po.getPigFileObjects().get(0);
        String inputFilenameOS = pfo.getCdKeyFile();
        String inputFileOSBucket = pfo.getNmBucket();
        if (inputFileOSBucket != null && inputFilenameOS != null && !salvataggioBackendHelper.isActive()) {
            String messaggio = "File memorizzato su Object Storage ma Object Storage inattivo.";
            jobHelper.changePigObjectAndSessionStateAtomic(po.getIdObject(),
                    Constants.Stato.ERRORE_TRASFORMAZIONE.name(), Constants.XF_ERROR_CODE, messaggio);
            logger.error(messaggio);
            // MEV 22064 - trova e modifica lo stato del SU
            aggiornaStatoEventualeStrumentoUrbanistico(po.getCdKeyObject(),
                    PigStrumentiUrbanistici.TiStato.IN_TRASFORMAZIONE, PigStrumentiUrbanistici.TiStato.ERRORE,
                    "PING-ERRSU27");

            // MEV 30935 - trova e modifica lo stato del Sisma
            aggiornaStatoEventualeSisma(po.getCdKeyObject(), PigSisma.TiStato.IN_TRASFORMAZIONE,
                    PigSisma.TiStato.ERRORE, Constants.PING_ERRSSISMA27);
            aggiornaStatoEventualeSisma(po.getCdKeyObject(), PigSisma.TiStato.IN_TRASFORMAZIONE_SA,
                    PigSisma.TiStato.ERRORE, Constants.PING_ERRSSISMA27);

            throw new ParerInternalError(messaggio);
        }

        // quindi i path da dove prendere il pacchetto e il path dove mettere i pacchetti generati.
        String inputFilename = configurationHelper.getValoreParamApplicByApplic(Constants.ROOT_FTP) + File.separator
                + dsPathInputFtp + File.separator + po.getCdKeyObject() + File.separator + pfo.getNmFileObject();

        // con queste informazioni trovo ente,struttura,ambiente,utente, etc...
        List<PigVLisStrutVersSacer> organizIamStruts = corrispondenzeVersHelper
                .getIdOrganizIamStrut(pigTipoObject.getIdTipoObject());
        if (organizIamStruts == null || organizIamStruts.size() > 1) {
            String messaggio = "Trovate più strutture legate al tipo oggetto " + pigTipoObject.getNmTipoObject();
            jobHelper.changePigObjectAndSessionStateAtomic(po.getIdObject(),
                    Constants.Stato.ERRORE_TRASFORMAZIONE.name(), Constants.XF_ERROR_CODE, messaggio);
            logger.error(messaggio);

            // MEV 22064 - trova e modifica lo stato del SU
            aggiornaStatoEventualeStrumentoUrbanistico(po.getCdKeyObject(),
                    PigStrumentiUrbanistici.TiStato.IN_TRASFORMAZIONE, PigStrumentiUrbanistici.TiStato.ERRORE,
                    "PING-ERRSU27");

            // MEV 30935 - trova e modifica lo stato del Sisma
            aggiornaStatoEventualeSisma(po.getCdKeyObject(), PigSisma.TiStato.IN_TRASFORMAZIONE,
                    PigSisma.TiStato.ERRORE, Constants.PING_ERRSSISMA27);
            aggiornaStatoEventualeSisma(po.getCdKeyObject(), PigSisma.TiStato.IN_TRASFORMAZIONE_SA,
                    PigSisma.TiStato.ERRORE, Constants.PING_ERRSSISMA27);

            throw new ParerInternalError(messaggio);
        }

        if (organizIamStruts.isEmpty()) {
            String messaggio = "Nessuna struttura legata al tipo oggetto " + pigTipoObject.getNmTipoObject();
            jobHelper.changePigObjectAndSessionStateAtomic(po.getIdObject(),
                    Constants.Stato.ERRORE_TRASFORMAZIONE.name(), Constants.XF_ERROR_CODE, messaggio);
            logger.error(messaggio);

            // MEV 22064 - trova e modifica lo stato del SU
            aggiornaStatoEventualeStrumentoUrbanistico(po.getCdKeyObject(),
                    PigStrumentiUrbanistici.TiStato.IN_TRASFORMAZIONE, PigStrumentiUrbanistici.TiStato.ERRORE,
                    "PING-ERRSU27");

            // MEV 30935 - trova e modifica lo stato del Sisma
            aggiornaStatoEventualeSisma(po.getCdKeyObject(), PigSisma.TiStato.IN_TRASFORMAZIONE,
                    PigSisma.TiStato.ERRORE, Constants.PING_ERRSSISMA27);
            aggiornaStatoEventualeSisma(po.getCdKeyObject(), PigSisma.TiStato.IN_TRASFORMAZIONE_SA,
                    PigSisma.TiStato.ERRORE, Constants.PING_ERRSSISMA27);

            throw new ParerInternalError(messaggio);
        }

        PigVLisStrutVersSacer organiz = organizIamStruts.get(0);
        UsrVAbilStrutSacerXping strutturaAbilitata = null;
        try {
            strutturaAbilitata = corrispondenzeVersHelper.getStrutturaAbilitata(
                    organiz.getPigVLisStrutVersSacerId().getIdOrganizIamStrut(), organiz.getNmUseridSacer());
        } catch (Exception ex) {
            String messaggio = "Nessuna corrispondenza trovata in Sacer per "
                    + organiz.getPigVLisStrutVersSacerId().getIdOrganizIamStrut() + " - " + organiz.getNmUseridSacer();
            jobHelper.changePigObjectAndSessionStateAtomic(po.getIdObject(),
                    Constants.Stato.ERRORE_TRASFORMAZIONE.name(), Constants.XF_ERROR_CODE, messaggio);
            logger.error(messaggio);

            // MEV 22064 - trova e modifica lo stato del SU
            aggiornaStatoEventualeStrumentoUrbanistico(po.getCdKeyObject(),
                    PigStrumentiUrbanistici.TiStato.IN_TRASFORMAZIONE, PigStrumentiUrbanistici.TiStato.ERRORE,
                    "PING-ERRSU27");

            // MEV 30935 - trova e modifica lo stato del Sisma
            aggiornaStatoEventualeSisma(po.getCdKeyObject(), PigSisma.TiStato.IN_TRASFORMAZIONE,
                    PigSisma.TiStato.ERRORE, Constants.PING_ERRSSISMA27);
            aggiornaStatoEventualeSisma(po.getCdKeyObject(), PigSisma.TiStato.IN_TRASFORMAZIONE_SA,
                    PigSisma.TiStato.ERRORE, Constants.PING_ERRSSISMA27);

            throw new ParerInternalError(messaggio);
        }

        String ente = strutturaAbilitata.getNmEnte();
        String struttura = strutturaAbilitata.getNmStrut();
        String utente = organiz.getNmUseridSacer();
        String ambiente = strutturaAbilitata.getNmAmbiente();

        String forzaAccettazione = pigTipoObject.getFlForzaAccettazioneSacer();
        String forzaCollegamento = pigTipoObject.getFlForzaCollegamento();
        String forzaConservazione = pigTipoObject.getFlForzaConservazione();

        // controllo la trasformazione da lanciare
        XfoTrasf transformation = po.getPigTipoObject().getXfoTrasf();

        // se la trasformazione impostata per la coppia tipo oggetto e versatore non esiste o è disattiva usciamo.
        if (transformation == null) {
            jobHelper.changePigObjectAndSessionState(po, Constants.Stato.TRASFORMAZIONE_NON_ATTIVA.name());
            String messaggio = "Trasformazione NON trovata per oggetto " + po.getCdKeyObject() + " (stato: "
                    + Constants.Stato.DA_TRASFORMARE.name() + " -> " + Constants.Stato.TRASFORMAZIONE_NON_ATTIVA.name()
                    + ")";
            logger.warn(messaggio);

            // MEV 22064 - trova e modifica lo stato del SU
            aggiornaStatoEventualeStrumentoUrbanistico(po.getCdKeyObject(),
                    PigStrumentiUrbanistici.TiStato.IN_TRASFORMAZIONE, PigStrumentiUrbanistici.TiStato.ERRORE,
                    "PING-ERRSU27");

            // MEV 30935 - trova e modifica lo stato del Sisma
            aggiornaStatoEventualeSisma(po.getCdKeyObject(), PigSisma.TiStato.IN_TRASFORMAZIONE,
                    PigSisma.TiStato.ERRORE, Constants.PING_ERRSSISMA27);
            aggiornaStatoEventualeSisma(po.getCdKeyObject(), PigSisma.TiStato.IN_TRASFORMAZIONE_SA,
                    PigSisma.TiStato.ERRORE, Constants.PING_ERRSSISMA27);

            return;
        }
        if (transformation.getFlAttiva().equals(Constants.Flag.NO.getValue())) {
            jobHelper.changePigObjectAndSessionState(po, Constants.Stato.TRASFORMAZIONE_NON_ATTIVA.name());
            String messaggio = "Trasformazione disattivata per oggetto " + po.getCdKeyObject() + " (stato: "
                    + Constants.Stato.DA_TRASFORMARE.name() + " -> " + Constants.Stato.TRASFORMAZIONE_NON_ATTIVA.name()
                    + ")";
            logger.warn(messaggio);

            // MEV 22064 - trova e modifica lo stato del SU
            aggiornaStatoEventualeStrumentoUrbanistico(po.getCdKeyObject(),
                    PigStrumentiUrbanistici.TiStato.IN_TRASFORMAZIONE, PigStrumentiUrbanistici.TiStato.ERRORE,
                    "PING-ERRSU27");

            // MEV 30935 - trova e modifica lo stato del Sisma
            aggiornaStatoEventualeSisma(po.getCdKeyObject(), PigSisma.TiStato.IN_TRASFORMAZIONE,
                    PigSisma.TiStato.ERRORE, Constants.PING_ERRSSISMA27);
            aggiornaStatoEventualeSisma(po.getCdKeyObject(), PigSisma.TiStato.IN_TRASFORMAZIONE_SA,
                    PigSisma.TiStato.ERRORE, Constants.PING_ERRSSISMA27);

            return;
        }

        // salva l'informazione sulla trasformazione scelta e ricarica il pig object
        po = jobHelper.setPigObjectAndSessionChoosenTransformationAtomic(po.getIdObject(), transformation);

        logger.info("Trasformazione {} trovata per oggetto {}", transformation.getCdTrasf(), po.getCdKeyObject());

        // cerco la corretta istanza di kettle server per inviarle la trasformazione
        PigKSInstance pigKSInstance = trasformazioniHelper.getPigObjectKettleServerInstance(idObject);
        if (pigKSInstance == null) {
            jobHelper.changePigObjectAndSessionStateAtomic(po.getIdObject(),
                    Constants.Stato.ERRORE_TRASFORMAZIONE.name());
            String messaggio = "Instanza di Kettle Server non configurata per oggetto " + po.getCdKeyObject()
                    + " (stato: " + Constants.Stato.DA_TRASFORMARE.name() + " -> "
                    + Constants.Stato.ERRORE_TRASFORMAZIONE.name() + ")";
            logger.error(messaggio);

            // MEV 22064 - trova e modifica lo stato del SU
            aggiornaStatoEventualeStrumentoUrbanistico(po.getCdKeyObject(),
                    PigStrumentiUrbanistici.TiStato.IN_TRASFORMAZIONE, PigStrumentiUrbanistici.TiStato.ERRORE,
                    "PING-ERRSU27");

            // MEV 30935 - trova e modifica lo stato del Sisma
            aggiornaStatoEventualeSisma(po.getCdKeyObject(), PigSisma.TiStato.IN_TRASFORMAZIONE,
                    PigSisma.TiStato.ERRORE, Constants.PING_ERRSSISMA27);
            aggiornaStatoEventualeSisma(po.getCdKeyObject(), PigSisma.TiStato.IN_TRASFORMAZIONE_SA,
                    PigSisma.TiStato.ERRORE, Constants.PING_ERRSSISMA27);

            return;
        }

        // calcolo tutte le directory necessarie per la trasformazione
        String workingDirectory = pigKSInstance.getDirKsInstance();
        File transformationDirectory = new File(workingDirectory + File.separator + idObject);
        File tmpFilesDirectory = new File(workingDirectory + File.separator + idObject + File.separator + "tmp");
        File udsFinalDirectory = new File(workingDirectory + File.separator + idObject + File.separator + "UDs");

        // MEV 31648 - recupera i paramtri per laconnessione al db di appoggio delle trasformazioni
        String kettleDBHost = configurationHelper.getValoreParamApplicByApplic(Constants.XF_KETTLE_DB_HOST);
        String kettleDBPort = configurationHelper.getValoreParamApplicByApplic(Constants.XF_KETTLE_DB_PORT);
        String kettleDBName = configurationHelper.getValoreParamApplicByApplic(Constants.XF_KETTLE_DB_NAME);
        String kettleDBUser = configurationHelper.getValoreParamApplicByApplic(Constants.XF_KETTLE_DB_USER);
        String kettleDBPassword = configurationHelper.getValoreParamApplicByApplic(Constants.XF_KETTLE_DB_PASSWORD);

        try (ZipInputStream zipInputStream = new ZipInputStream(
                new ByteArrayInputStream(transformation.getBlTrasf()))) {
            ZipEntry entry = zipInputStream.getNextEntry();
            // La trasformazione è contenuta tutta in una cartella, quella cartella ha il nome della trasformazione.
            if (entry != null && entry.isDirectory()) {
                Map<String, String> filledParameters = new HashMap<>();

                String transformationName = entry.getName();
                Map<String, String> parameters = null;
                try {
                    parameters = repositoryManager.listJobParameters(transformationName);
                } catch (KettleException ex) {
                    String messaggio = "Errore nel recupero dei parametri della trasformazione.";
                    jobHelper.changePigObjectAndSessionStateAtomic(po.getIdObject(),
                            Constants.Stato.ERRORE_TRASFORMAZIONE.name(), Constants.XF_ERROR_CODE, messaggio);
                    logger.error(messaggio);
                    // MEV 22064 - trova e modifica lo stato del SU
                    aggiornaStatoEventualeStrumentoUrbanistico(po.getCdKeyObject(),
                            PigStrumentiUrbanistici.TiStato.IN_TRASFORMAZIONE, PigStrumentiUrbanistici.TiStato.ERRORE,
                            "PING-ERRSU27");

                    // MEV 30935 - trova e modifica lo stato del Sisma
                    aggiornaStatoEventualeSisma(po.getCdKeyObject(), PigSisma.TiStato.IN_TRASFORMAZIONE,
                            PigSisma.TiStato.ERRORE, Constants.PING_ERRSSISMA27);
                    aggiornaStatoEventualeSisma(po.getCdKeyObject(), PigSisma.TiStato.IN_TRASFORMAZIONE_SA,
                            PigSisma.TiStato.ERRORE, Constants.PING_ERRSSISMA27);

                    throw new ParerInternalError(messaggio);
                }

                for (Map.Entry<String, String> parameter : parameters.entrySet()) {
                    switch (parameter.getKey()) {
                    case Constants.XF_AMBIENTE:
                    case Constants.XF_ENTE:
                    case Constants.XF_STRUTTURA:
                    case Constants.XF_UTENTE:
                    case Constants.XF_INPUT_FILE_NAME:
                    case Constants.XF_OUTPUT_DIR:
                    case Constants.XF_OUTPUT_FILE_BASE_NAME:
                    case Constants.XF_FORZA_ACCETTAZIONE:
                    case Constants.XF_FORZA_COLLEGAMENTO:
                    case Constants.XF_FORZA_CONSERVAZIONE:
                    case Constants.XF_AUXILIARY_FILES_DIR:
                    case Constants.XF_TMP_DIR:
                        // MEV31648
                    case Constants.XF_KETTLE_DB_HOST:
                    case Constants.XF_KETTLE_DB_PORT:
                    case Constants.XF_KETTLE_DB_NAME:
                    case Constants.XF_KETTLE_DB_USER:
                    case Constants.XF_KETTLE_DB_PASSWORD:
                    case Constants.XF_DB_TABLE_ID:
                        break;
                    default:
                        PigVValoreParamTrasf valoreParam = trasformazioniHelper.searchPigVValoreParamTrasfByName(
                                parameter.getKey(), pigVersTipoObjectDaTrasf.getIdVersTipoObjectDaTrasf());
                        String cleanParameter = "";

                        if (valoreParam != null && valoreParam.getDsValoreParam() != null) {
                            cleanParameter = valoreParam.getDsValoreParam();
                            // rimuovo le eventuali parentesi quadre, presenti se il parametro è quello di default
                            if (cleanParameter.startsWith("[") && cleanParameter.endsWith("]")) {
                                cleanParameter = cleanParameter.substring(1, cleanParameter.length() - 1);
                            }
                        }

                        filledParameters.put(parameter.getKey(), cleanParameter);
                    }
                }

                filledParameters.put(Constants.XF_AMBIENTE, ambiente);
                filledParameters.put(Constants.XF_ENTE, ente);
                filledParameters.put(Constants.XF_STRUTTURA, struttura);
                filledParameters.put(Constants.XF_UTENTE, utente);

                filledParameters.put(Constants.XF_FORZA_ACCETTAZIONE, forzaAccettazione);
                filledParameters.put(Constants.XF_FORZA_COLLEGAMENTO, forzaCollegamento);
                filledParameters.put(Constants.XF_FORZA_CONSERVAZIONE, forzaConservazione);

                filledParameters.put(Constants.XF_OUTPUT_DIR, udsFinalDirectory.getCanonicalPath());
                filledParameters.put(Constants.XF_TMP_DIR, tmpFilesDirectory.getCanonicalPath());

                String xfOutputFileBaseName = po.getCdKeyObject();
                if (Constants.XF_OUTPUT_FILE_BASE_NAME_MAX_LENGTH != -1) {
                    xfOutputFileBaseName = xfOutputFileBaseName.substring(0,
                            Math.min(xfOutputFileBaseName.length(), Constants.XF_OUTPUT_FILE_BASE_NAME_MAX_LENGTH));
                }

                filledParameters.put(Constants.XF_OUTPUT_FILE_BASE_NAME, xfOutputFileBaseName);
                filledParameters.put(Constants.XF_INPUT_FILE_NAME, inputFilename);
                filledParameters.put(Constants.XF_AUXILIARY_FILES_DIR, transformationDirectory.getCanonicalPath()
                        + File.separator + transformationName + File.separator);

                // MEV22000 - riempi gli eventuali valori per recuperare il file da object storage
                inputFileOSBucket = inputFileOSBucket == null ? "" : inputFileOSBucket;
                inputFilenameOS = inputFilenameOS == null ? "" : inputFilenameOS;
                filledParameters.put(Constants.XF_OBJECT_STORAGE_BUCKET, inputFileOSBucket);
                filledParameters.put(Constants.XF_OBJECT_STORAGE_KEY, inputFilenameOS);

                // MEV31648 - riempi i parametri del db di appoggio
                filledParameters.put(Constants.XF_KETTLE_DB_HOST, kettleDBHost);
                filledParameters.put(Constants.XF_KETTLE_DB_PORT, kettleDBPort);
                filledParameters.put(Constants.XF_KETTLE_DB_NAME, kettleDBName);
                filledParameters.put(Constants.XF_KETTLE_DB_USER, kettleDBUser);
                filledParameters.put(Constants.XF_KETTLE_DB_PASSWORD, kettleDBPassword);
                filledParameters.put(Constants.XF_DB_TABLE_ID, po.getIdObject().toString());

                // MAC 26890
                if (transformationDirectory.exists()) {
                    logger.warn("La cartella {} esiste già, la rimuoviamo.",
                            transformationDirectory.getCanonicalPath());
                    cleanTransformationDirectory(transformationDirectory);
                }

                boolean createResult = transformationDirectory.mkdirs();
                if (!createResult) {
                    String messaggio = "Impossibile creare la cartella " + transformationDirectory.getCanonicalPath();
                    jobHelper.changePigObjectAndSessionStateAtomic(po.getIdObject(),
                            Constants.Stato.ERRORE_TRASFORMAZIONE.name(), Constants.XF_ERROR_CODE, messaggio);
                    logger.error(messaggio);

                    // MEV 22064 - trova e modifica lo stato del SU
                    aggiornaStatoEventualeStrumentoUrbanistico(po.getCdKeyObject(),
                            PigStrumentiUrbanistici.TiStato.IN_TRASFORMAZIONE, PigStrumentiUrbanistici.TiStato.ERRORE,
                            "PING-ERRSU27");

                    // MEV 30935 - trova e modifica lo stato del Sisma
                    aggiornaStatoEventualeSisma(po.getCdKeyObject(), PigSisma.TiStato.IN_TRASFORMAZIONE,
                            PigSisma.TiStato.ERRORE, Constants.PING_ERRSSISMA27);
                    aggiornaStatoEventualeSisma(po.getCdKeyObject(), PigSisma.TiStato.IN_TRASFORMAZIONE_SA,
                            PigSisma.TiStato.ERRORE, Constants.PING_ERRSSISMA27);

                    throw new ParerInternalError(messaggio);
                }

                createResult = tmpFilesDirectory.mkdirs();
                if (!createResult) {
                    String messaggio = "Impossibile creare la cartella " + tmpFilesDirectory.getCanonicalPath();
                    jobHelper.changePigObjectAndSessionStateAtomic(po.getIdObject(),
                            Constants.Stato.ERRORE_TRASFORMAZIONE.name(), Constants.XF_ERROR_CODE, messaggio);
                    logger.error(messaggio);

                    // MEV 22064 - trova e modifica lo stato del SU
                    aggiornaStatoEventualeStrumentoUrbanistico(po.getCdKeyObject(),
                            PigStrumentiUrbanistici.TiStato.IN_TRASFORMAZIONE, PigStrumentiUrbanistici.TiStato.ERRORE,
                            "PING-ERRSU27");

                    // MEV 30935 - trova e modifica lo stato del Sisma
                    aggiornaStatoEventualeSisma(po.getCdKeyObject(), PigSisma.TiStato.IN_TRASFORMAZIONE,
                            PigSisma.TiStato.ERRORE, Constants.PING_ERRSSISMA27);
                    aggiornaStatoEventualeSisma(po.getCdKeyObject(), PigSisma.TiStato.IN_TRASFORMAZIONE_SA,
                            PigSisma.TiStato.ERRORE, Constants.PING_ERRSSISMA27);

                    throw new ParerInternalError(messaggio);
                }

                createResult = udsFinalDirectory.mkdirs();
                if (!createResult) {
                    String messaggio = "Impossibile creare la cartella " + udsFinalDirectory.getCanonicalPath();
                    jobHelper.changePigObjectAndSessionStateAtomic(po.getIdObject(),
                            Constants.Stato.ERRORE_TRASFORMAZIONE.name(), Constants.XF_ERROR_CODE, messaggio);
                    logger.error(messaggio);

                    // MEV 22064 - trova e modifica lo stato del SU
                    aggiornaStatoEventualeStrumentoUrbanistico(po.getCdKeyObject(),
                            PigStrumentiUrbanistici.TiStato.IN_TRASFORMAZIONE, PigStrumentiUrbanistici.TiStato.ERRORE,
                            "PING-ERRSU27");

                    // MEV 30935 - trova e modifica lo stato del Sisma
                    aggiornaStatoEventualeSisma(po.getCdKeyObject(), PigSisma.TiStato.IN_TRASFORMAZIONE,
                            PigSisma.TiStato.ERRORE, Constants.PING_ERRSSISMA27);
                    aggiornaStatoEventualeSisma(po.getCdKeyObject(), PigSisma.TiStato.IN_TRASFORMAZIONE_SA,
                            PigSisma.TiStato.ERRORE, Constants.PING_ERRSSISMA27);

                    throw new ParerInternalError(messaggio);
                }

                // Estrai i file ausiliari della trasformazione e stoccali temporaneamente su disco
                List<XfoFileTrasf> files = trasformazioniHelper
                        .searchAuxiliaryFilesByXfoTras(transformation.getIdTrasf());
                for (XfoFileTrasf file : files) {
                    File auxiliaryFile = new File(transformationDirectory, file.getNmFileTrasf());

                    auxiliaryFile.getParentFile().mkdirs();
                    try (FileOutputStream fos = new FileOutputStream(auxiliaryFile);
                            ByteArrayInputStream bais = new ByteArrayInputStream(file.getBlFileTrasf());) {
                        IOUtils.copy(bais, fos);
                        logger.debug("Creato il file {} di dimensione {} bytes",
                                workingDirectory + File.separator + idObject + File.separator + file.getNmFileTrasf(),
                                file.getBlFileTrasf().length);
                    } catch (IOException e) {
                        String messaggio = "Errore durante la creazione del file " + workingDirectory + File.separator
                                + idObject + File.separator + file.getNmFileTrasf() + ": " + e.getMessage();
                        jobHelper.changePigObjectAndSessionStateAtomic(po.getIdObject(),
                                Constants.Stato.ERRORE_TRASFORMAZIONE.name(), Constants.XF_ERROR_CODE, messaggio);
                        logger.error(messaggio);

                        // MEV 22064 - trova e modifica lo stato del SU
                        aggiornaStatoEventualeStrumentoUrbanistico(po.getCdKeyObject(),
                                PigStrumentiUrbanistici.TiStato.IN_TRASFORMAZIONE,
                                PigStrumentiUrbanistici.TiStato.ERRORE, "PING-ERRSU27");

                        // MEV 30935 - trova e modifica lo stato del Sisma
                        aggiornaStatoEventualeSisma(po.getCdKeyObject(), PigSisma.TiStato.IN_TRASFORMAZIONE,
                                PigSisma.TiStato.ERRORE, Constants.PING_ERRSSISMA27);
                        aggiornaStatoEventualeSisma(po.getCdKeyObject(), PigSisma.TiStato.IN_TRASFORMAZIONE_SA,
                                PigSisma.TiStato.ERRORE, Constants.PING_ERRSSISMA27);

                        throw new ParerInternalError(messaggio);
                    }
                }

                boolean result = repositoryManager.executeJob(po.getIdObject(), entry.getName(), filledParameters,
                        pigKSInstance.getUrlKsInstance());

                if (!result) {
                    cleanTransformationDirectory(transformationDirectory);

                    String messaggio = "Coda piena per : " + po.getCdKeyObject();
                    jobHelper.changePigObjectAndSessionStateAtomic(po.getIdObject(),
                            Constants.Stato.DA_TRASFORMARE.name(), Constants.XF_WARNING_CODE,
                            "Coda piena su kettle server.");
                    logger.info(messaggio);

                    // MEV 22064 - trova e modifica lo stato del SU
                    aggiornaStatoEventualeStrumentoUrbanistico(po.getCdKeyObject(),
                            PigStrumentiUrbanistici.TiStato.IN_TRASFORMAZIONE, PigStrumentiUrbanistici.TiStato.ERRORE,
                            "PING-ERRSU27");

                    // MEV 30935 - trova e modifica lo stato del Sisma
                    aggiornaStatoEventualeSisma(po.getCdKeyObject(), PigSisma.TiStato.IN_TRASFORMAZIONE,
                            PigSisma.TiStato.ERRORE, Constants.PING_ERRSSISMA27);
                    aggiornaStatoEventualeSisma(po.getCdKeyObject(), PigSisma.TiStato.IN_TRASFORMAZIONE_SA,
                            PigSisma.TiStato.ERRORE, Constants.PING_ERRSSISMA27);

                }

            } else {
                cleanTransformationDirectory(transformationDirectory);

                String messaggio = "Pacchetto della trasformazione " + transformation.getCdTrasf() + " malformato.";
                jobHelper.changePigObjectAndSessionStateAtomic(po.getIdObject(),
                        Constants.Stato.ERRORE_TRASFORMAZIONE.name(), Constants.XF_ERROR_CODE, messaggio);
                logger.error(messaggio);

                // MEV 22064 - trova e modifica lo stato del SU
                aggiornaStatoEventualeStrumentoUrbanistico(po.getCdKeyObject(),
                        PigStrumentiUrbanistici.TiStato.IN_TRASFORMAZIONE, PigStrumentiUrbanistici.TiStato.ERRORE,
                        "PING-ERRSU27");

                // MEV 30935 - trova e modifica lo stato del Sisma
                aggiornaStatoEventualeSisma(po.getCdKeyObject(), PigSisma.TiStato.IN_TRASFORMAZIONE,
                        PigSisma.TiStato.ERRORE, Constants.PING_ERRSSISMA27);
                aggiornaStatoEventualeSisma(po.getCdKeyObject(), PigSisma.TiStato.IN_TRASFORMAZIONE_SA,
                        PigSisma.TiStato.ERRORE, Constants.PING_ERRSSISMA27);

            }
        } catch (KettleException ex) {
            String messaggio = "Errore durante la richiesta di esecuzione della trasformazione: " + ex.getMessage();
            jobHelper.changePigObjectAndSessionStateAtomic(po.getIdObject(),
                    Constants.Stato.ERRORE_TRASFORMAZIONE.name(), Constants.XF_ERROR_CODE, messaggio);
            logger.error(messaggio);

            // MEV 22064 - trova e modifica lo stato del SU
            aggiornaStatoEventualeStrumentoUrbanistico(po.getCdKeyObject(),
                    PigStrumentiUrbanistici.TiStato.IN_TRASFORMAZIONE, PigStrumentiUrbanistici.TiStato.ERRORE,
                    "PING-ERRSU27");
            // MEV 30935 - trova e modifica lo stato del Sisma
            aggiornaStatoEventualeSisma(po.getCdKeyObject(), PigSisma.TiStato.IN_TRASFORMAZIONE,
                    PigSisma.TiStato.ERRORE, Constants.PING_ERRSSISMA27);
            aggiornaStatoEventualeSisma(po.getCdKeyObject(), PigSisma.TiStato.IN_TRASFORMAZIONE_SA,
                    PigSisma.TiStato.ERRORE, Constants.PING_ERRSSISMA27);

        } catch (KettleServiceException ex) {
            String messaggio = "Errore durante nella comunicazione con in webservices di Parer Kettle Server";
            jobHelper.changePigObjectAndSessionStateAtomic(po.getIdObject(), Constants.Stato.DA_TRASFORMARE.name(),
                    Constants.XF_WARNING_CODE, messaggio);
            logger.error(messaggio);
        } catch (IOException ex) {
            String messaggio = "Eccezione imprevista nell'apertura del pacchetto della trasformazione ";
            messaggio += ExceptionUtils.getRootCauseMessage(ex);
            jobHelper.changePigObjectAndSessionStateAtomic(po.getIdObject(),
                    Constants.Stato.ERRORE_TRASFORMAZIONE.name(), Constants.XF_ERROR_CODE, messaggio);
            logger.error(messaggio, ex);

            // MEV 22064 - trova e modifica lo stato del SU
            aggiornaStatoEventualeStrumentoUrbanistico(po.getCdKeyObject(),
                    PigStrumentiUrbanistici.TiStato.IN_TRASFORMAZIONE, PigStrumentiUrbanistici.TiStato.ERRORE,
                    "PING-ERRSU27");

            // MEV 30935 - trova e modifica lo stato del Sisma
            aggiornaStatoEventualeSisma(po.getCdKeyObject(), PigSisma.TiStato.IN_TRASFORMAZIONE,
                    PigSisma.TiStato.ERRORE, Constants.PING_ERRSSISMA27);
            aggiornaStatoEventualeSisma(po.getCdKeyObject(), PigSisma.TiStato.IN_TRASFORMAZIONE_SA,
                    PigSisma.TiStato.ERRORE, Constants.PING_ERRSSISMA27);

            throw new ParerInternalError(messaggio, ex);
        }
    }

    public class NotificaOggettoTrasformatoResponse {

        public String esito;
        public String messaggio;

        public NotificaOggettoTrasformatoResponse() {
            this.esito = "OK";
            this.messaggio = "";
        }

        public NotificaOggettoTrasformatoResponse(String esito, String messaggio) {
            this.esito = "OK";
            this.messaggio = "";
        }
    }

    @Asynchronous
    public void notificaOggettoTrasformato(long idOggetto, int numeroErrori, String report) throws Exception {
        logger.debug("[notificaOggettoTrasformato] inizio ({}).", idOggetto);

        PigObject po = trasformazioniHelper.findById(PigObject.class, idOggetto);
        if (po == null) {
            String messaggio = "Oggetto " + idOggetto + " non trovato o inesistente.";
            logger.error(messaggio);
            throw new Exception(messaggio);
        }

        logger.info("[notificaOggettoTrasformato] Oggetto id: {} nome: {}", idOggetto, po.getCdKeyObject());

        // cerco la corretta istanza di kettle server per inviarle la trasformazione
        PigKSInstance pigKSInstance = trasformazioniHelper.getPigObjectKettleServerInstance(idOggetto);
        if (pigKSInstance == null) {
            jobHelper.changePigObjectAndSessionStateAtomic(po.getIdObject(),
                    Constants.Stato.ERRORE_TRASFORMAZIONE.name());
            String messaggio = "Instanza di Kettle Server non configurata per oggetto " + po.getCdKeyObject()
                    + " (stato: " + Constants.Stato.DA_TRASFORMARE.name() + " -> "
                    + Constants.Stato.ERRORE_TRASFORMAZIONE.name() + ")";
            logger.error(messaggio);

            // MEV 22064 - trova e modifica lo stato del SU
            aggiornaStatoEventualeStrumentoUrbanistico(po.getCdKeyObject(),
                    PigStrumentiUrbanistici.TiStato.IN_TRASFORMAZIONE, PigStrumentiUrbanistici.TiStato.ERRORE,
                    "PING-ERRSU27");

            // MEV 30935 - trova e modifica lo stato del Sisma
            aggiornaStatoEventualeSisma(po.getCdKeyObject(), PigSisma.TiStato.IN_TRASFORMAZIONE,
                    PigSisma.TiStato.ERRORE, Constants.PING_ERRSSISMA27);
            aggiornaStatoEventualeSisma(po.getCdKeyObject(), PigSisma.TiStato.IN_TRASFORMAZIONE_SA,
                    PigSisma.TiStato.ERRORE, Constants.PING_ERRSSISMA27);

            return;
        }

        String workingDirectory = pigKSInstance.getDirKsInstance();
        File transformationDirectory = new File(workingDirectory + File.separator + idOggetto);
        File udsFinalDirectory = new File(workingDirectory + File.separator + idOggetto + File.separator + "UDs");

        jobHelper.changePigObjectAndSessionStateAtomic(po.getIdObject(),
                Constants.Stato.PREPARAZIONE_OGG_IN_CORSO.name());

        // dal oggetto da trasformare recupera versatore finale e tipo oggetto finale
        PigVersTipoObjectDaTrasf pigVersTipoObjectDaTrasf = trasformazioniHelper.getPigVersTipoObjectDaTrasf(po);

        if (pigVersTipoObjectDaTrasf == null) {
            cleanTransformationDirectory(transformationDirectory);

            String messaggio = "Tipo oggetto o versatore per oggetto " + idOggetto + " non trovato o inesistente.";
            logger.error(messaggio);
            throw new Exception(messaggio);
        }

        PigVers versatoreDiDestinazione = pigVersTipoObjectDaTrasf.getPigVersGen();
        PigTipoObject pigTipoObject = pigVersTipoObjectDaTrasf.getPigTipoObjectGen();

        if (versatoreDiDestinazione == null) {
            cleanTransformationDirectory(transformationDirectory);

            String messaggio = "Versatore di destinazione assente o mal configurato.";
            jobHelper.changePigObjectAndSessionStateAtomic(po.getIdObject(),
                    Constants.Stato.ERRORE_TRASFORMAZIONE.name(), Constants.XF_ERROR_CODE, messaggio);
            logger.error(messaggio);

            // MEV 22064 - trova e modifica lo stato del SU
            aggiornaStatoEventualeStrumentoUrbanistico(po.getCdKeyObject(),
                    PigStrumentiUrbanistici.TiStato.IN_TRASFORMAZIONE, PigStrumentiUrbanistici.TiStato.ERRORE,
                    "PING-ERRSU27");

            // MEV 30935 - trova e modifica lo stato del Sisma
            aggiornaStatoEventualeSisma(po.getCdKeyObject(), PigSisma.TiStato.IN_TRASFORMAZIONE,
                    PigSisma.TiStato.ERRORE, Constants.PING_ERRSSISMA27);
            aggiornaStatoEventualeSisma(po.getCdKeyObject(), PigSisma.TiStato.IN_TRASFORMAZIONE_SA,
                    PigSisma.TiStato.ERRORE, Constants.PING_ERRSSISMA27);

            throw new Exception(messaggio);
        }

        String dsPathTrasf = versatoreDiDestinazione.getDsPathTrasf();
        if (dsPathTrasf == null) {
            cleanTransformationDirectory(transformationDirectory);

            String messaggio = "Cartella di destinazione per il versatore di destinazione ("
                    + versatoreDiDestinazione.getDsVers() + ") assente o mal configurato.";
            jobHelper.changePigObjectAndSessionStateAtomic(po.getIdObject(),
                    Constants.Stato.ERRORE_TRASFORMAZIONE.name(), Constants.XF_ERROR_CODE, messaggio);
            logger.error(messaggio);

            // MEV 22064 - trova e modifica lo stato del SU
            aggiornaStatoEventualeStrumentoUrbanistico(po.getCdKeyObject(),
                    PigStrumentiUrbanistici.TiStato.IN_TRASFORMAZIONE, PigStrumentiUrbanistici.TiStato.ERRORE,
                    "PING-ERRSU27");

            // MEV 30935 - trova e modifica lo stato del Sisma
            aggiornaStatoEventualeSisma(po.getCdKeyObject(), PigSisma.TiStato.IN_TRASFORMAZIONE,
                    PigSisma.TiStato.ERRORE, Constants.PING_ERRSSISMA27);
            aggiornaStatoEventualeSisma(po.getCdKeyObject(), PigSisma.TiStato.IN_TRASFORMAZIONE_SA,
                    PigSisma.TiStato.ERRORE, Constants.PING_ERRSSISMA27);

            throw new Exception(messaggio);
        }

        String parkingDirectory = configurationHelper.getValoreParamApplicByApplic(Constants.ROOT_TRASF)
                + File.separator + dsPathTrasf + File.separator;

        logger.info("[notificaOggettoTrasformato] {} - {}: inizio preparazione report.", idOggetto,
                po.getCdKeyObject());
        try {
            Document xmlReport = DocumentHelper.parseText(report);
            xmlReport.getRootElement().addElement("NomeOggetto")
                    .setText(StringEscapeUtils.escapeXml10(po.getCdKeyObject()));
            xmlReport.getRootElement().addElement("TipoOggetto")
                    .setText(StringEscapeUtils.escapeXml10(po.getPigTipoObject().getNmTipoObject()));
            xmlReport.getRootElement().addElement("NomeTrasformazione")
                    .setText(StringEscapeUtils.escapeXml10(po.getPigTipoObject().getXfoTrasf().getCdTrasf()));
            xmlReport.getRootElement().addElement("VersioneTrasformazione")
                    .setText(StringEscapeUtils.escapeXml10(po.getPigTipoObject().getXfoTrasf().getCdVersioneCor()));
            xmlReport.getRootElement().addElement("Versatore")
                    .setText(StringEscapeUtils.escapeXml10(po.getPigVer().getNmVers()));
            xmlReport.getRootElement().addElement("VersatoreDiDestinazione")
                    .setText(StringEscapeUtils.escapeXml10(versatoreDiDestinazione.getNmVers()));

            if (numeroErrori > 0) {
                xmlReport.getRootElement().addElement("Esito").setText(StringEscapeUtils.escapeXml10("ERRORE"));
            } else if (numeroErrori == 0) {
                xmlReport.getRootElement().addElement("Esito").setText(StringEscapeUtils.escapeXml10("TRASFORMATO"));
            } else {
                xmlReport.getRootElement().addElement("Esito").setText(StringEscapeUtils.escapeXml10("WARNING"));
            }

            // numero di figli generati
            File[] udsDirectories = udsFinalDirectory.listFiles(f -> f.isDirectory());
            int maxZipSize = Integer
                    .parseInt(configurationHelper.getValoreParamApplicByApplic(Constants.NUMERO_UNITA_DOC_ZIP));
            int packagesTobeGenerated = udsDirectories.length / maxZipSize
                    + (udsDirectories.length % maxZipSize != 0 ? 1 : 0);
            xmlReport.getRootElement().addElement("NumeroOggettiFigli")
                    .setText(StringEscapeUtils.escapeXml10(String.valueOf(packagesTobeGenerated)));

            // con queste informazioni trovo ente,struttura,ambiente,utente, etc...
            List<PigVLisStrutVersSacer> organizIamStruts = corrispondenzeVersHelper
                    .getIdOrganizIamStrut(pigTipoObject.getIdTipoObject());
            if (organizIamStruts != null && organizIamStruts.size() <= 1 && !organizIamStruts.isEmpty()) {
                PigVLisStrutVersSacer organiz = organizIamStruts.get(0);
                UsrVAbilStrutSacerXping strutturaAbilitata = corrispondenzeVersHelper.getStrutturaAbilitata(
                        organiz.getPigVLisStrutVersSacerId().getIdOrganizIamStrut(), organiz.getNmUseridSacer());

                xmlReport.getRootElement().addElement("Ambiente")
                        .setText(StringEscapeUtils.escapeXml10(strutturaAbilitata.getNmAmbiente()));
                xmlReport.getRootElement().addElement("Ente")
                        .setText(StringEscapeUtils.escapeXml10(strutturaAbilitata.getNmEnte()));
                xmlReport.getRootElement().addElement("Struttura")
                        .setText(StringEscapeUtils.escapeXml10(strutturaAbilitata.getNmStrut()));
            }

            // MEV22000 - FIXME? diamo per scontato che un oggetto da trasformare abbia uno ed un solo file allegato,
            // corretto?
            PigFileObject pfo = po.getPigFileObjects().get(0);

            xmlReport.getRootElement().addElement("DimensioneOggetto")
                    .setText(StringEscapeUtils.escapeXml10(String.valueOf(pfo.getNiSizeFileVers())));

            report = xmlReport.asXML();

            report = transformReport(report, Constants.REPORT_XSL_FILE_LOCATION);

            logger.info("[notificaOggettoTrasformato] {} - {}: fine preparazione report.", idOggetto,
                    po.getCdKeyObject());
        } catch (DocumentException ex) {
            // TODO: potrebbe anche essere una string vuota...
        }

        if (numeroErrori > 0) {
            jobHelper.changePigObjectAndSessionStateAtomic(po.getIdObject(),
                    Constants.Stato.ERRORE_TRASFORMAZIONE.name(), Constants.XF_ERROR_CODE, "");
            trasformazioniHelper.saveReportIntoPigSession(po, report);
            cleanTransformationDirectory(transformationDirectory);

            // MEV 22064 - trova e modifica lo stato del SU
            aggiornaStatoEventualeStrumentoUrbanistico(po.getCdKeyObject(),
                    PigStrumentiUrbanistici.TiStato.IN_TRASFORMAZIONE, PigStrumentiUrbanistici.TiStato.ERRORE,
                    "PING-ERRSU27");

            // MEV 30935 - trova e modifica lo stato del Sisma
            aggiornaStatoEventualeSisma(po.getCdKeyObject(), PigSisma.TiStato.IN_TRASFORMAZIONE,
                    PigSisma.TiStato.ERRORE, Constants.PING_ERRSSISMA27);
            aggiornaStatoEventualeSisma(po.getCdKeyObject(), PigSisma.TiStato.IN_TRASFORMAZIONE_SA,
                    PigSisma.TiStato.ERRORE, Constants.PING_ERRSSISMA27);

        } else {
            List<String> zipNames = null;

            try {
                boolean verificationResult = verifyUdsNames(udsFinalDirectory.getCanonicalPath());
                if (verificationResult) {
                    zipNames = createOutputPackages(udsFinalDirectory.getCanonicalPath(), parkingDirectory, po);

                    // se la lista dei pacchetti è vuota dai un errore (è andato storto qualche cosa).
                    if (!zipNames.isEmpty()) {
                        registerOutputPackages(zipNames, po, pigTipoObject, versatoreDiDestinazione);

                        if (numeroErrori == -1) {
                            jobHelper.changePigObjectAndSessionState(po, Constants.Stato.WARNING_TRASFORMAZIONE.name());
                        } else {
                            jobHelper.changePigObjectAndSessionState(po, Constants.Stato.TRASFORMATO.name());
                        }
                    } else {
                        String messaggio = "Nessun pacchetto figlio generato.";
                        jobHelper.changePigObjectAndSessionStateAtomic(po.getIdObject(),
                                Constants.Stato.ERRORE_TRASFORMAZIONE.name(), Constants.XF_ERROR_CODE, messaggio);

                        // MEV 22064 - trova e modifica lo stato del SU
                        aggiornaStatoEventualeStrumentoUrbanistico(po.getCdKeyObject(),
                                PigStrumentiUrbanistici.TiStato.IN_TRASFORMAZIONE,
                                PigStrumentiUrbanistici.TiStato.ERRORE, "PING-ERRSU27");

                        // MEV 30935 - trova e modifica lo stato del Sisma
                        aggiornaStatoEventualeSisma(po.getCdKeyObject(), PigSisma.TiStato.IN_TRASFORMAZIONE,
                                PigSisma.TiStato.ERRORE, Constants.PING_ERRSSISMA27);
                        aggiornaStatoEventualeSisma(po.getCdKeyObject(), PigSisma.TiStato.IN_TRASFORMAZIONE_SA,
                                PigSisma.TiStato.ERRORE, Constants.PING_ERRSSISMA27);
                    }
                } else {
                    String messaggio = "UD malformata/e.";
                    jobHelper.changePigObjectAndSessionStateAtomic(po.getIdObject(),
                            Constants.Stato.ERRORE_TRASFORMAZIONE.name(), Constants.XF_ERROR_CODE, messaggio);

                    // MEV 22064 - trova e modifica lo stato del SU
                    aggiornaStatoEventualeStrumentoUrbanistico(po.getCdKeyObject(),
                            PigStrumentiUrbanistici.TiStato.IN_TRASFORMAZIONE, PigStrumentiUrbanistici.TiStato.ERRORE,
                            "PING-ERRSU27");

                    // MEV 30935 - trova e modifica lo stato del Sisma
                    aggiornaStatoEventualeSisma(po.getCdKeyObject(), PigSisma.TiStato.IN_TRASFORMAZIONE,
                            PigSisma.TiStato.ERRORE, Constants.PING_ERRSSISMA27);
                    aggiornaStatoEventualeSisma(po.getCdKeyObject(), PigSisma.TiStato.IN_TRASFORMAZIONE_SA,
                            PigSisma.TiStato.ERRORE, Constants.PING_ERRSSISMA27);
                }

                trasformazioniHelper.saveReportIntoPigSession(po, report);

            } catch (ParerInternalError ex) {
                jobHelper.changePigObjectAndSessionStateAtomic(po.getIdObject(),
                        Constants.Stato.ERRORE_TRASFORMAZIONE.name(), Constants.XF_ERROR_CODE, ex.getMessage());

                // MEV 22064 - trova e modifica lo stato del SU
                aggiornaStatoEventualeStrumentoUrbanistico(po.getCdKeyObject(),
                        PigStrumentiUrbanistici.TiStato.IN_TRASFORMAZIONE, PigStrumentiUrbanistici.TiStato.ERRORE,
                        "PING-ERRSU27");

                // MEV 30935 - trova e modifica lo stato del Sisma
                aggiornaStatoEventualeSisma(po.getCdKeyObject(), PigSisma.TiStato.IN_TRASFORMAZIONE,
                        PigSisma.TiStato.ERRORE, Constants.PING_ERRSSISMA27);
                aggiornaStatoEventualeSisma(po.getCdKeyObject(), PigSisma.TiStato.IN_TRASFORMAZIONE_SA,
                        PigSisma.TiStato.ERRORE, Constants.PING_ERRSSISMA27);

                // pulisci i pacchetti già copiati
                if (zipNames != null) {
                    for (String zipName : zipNames) {
                        File zipFile = new File(zipName);
                        zipFile.delete();
                    }
                }

                throw ex;
            } catch (Exception ex) {
                String messaggio = "Eccezione imprevista nell'apertura del pacchetto della trasformazione ";
                messaggio += ExceptionUtils.getRootCauseMessage(ex);
                jobHelper.changePigObjectAndSessionStateAtomic(po.getIdObject(),
                        Constants.Stato.ERRORE_TRASFORMAZIONE.name(), Constants.XF_ERROR_CODE, messaggio);
                logger.error(messaggio, ex);
                throw new Exception(messaggio);
            } finally {
                cleanTransformationDirectory(transformationDirectory);
            }

            logger.info("[notificaOggettoTrasformato] {} - {}: fine generazione pacchetti figli.", idOggetto,
                    po.getCdKeyObject());
        }

        logger.info("[notificaOggettoTrasformato] fine (id: " + idOggetto + " nome: " + po.getCdKeyObject() + ").");
    }

    private void cleanTransformationDirectory(File transformationDirectory) throws ParerInternalError {
        logger.debug("[cleanTransformationDirectory] inizio cancellazione ({})", transformationDirectory);
        try {
            if (transformationDirectory.isDirectory()) {
                PathUtils.delete(transformationDirectory.toPath());
            }
        } catch (Exception ex) {
            String messaggio = "Eccezione imprevista nella cancellazione della directory " + transformationDirectory;
            messaggio += ExceptionUtils.getRootCauseMessage(ex);
            logger.error(messaggio, ex);
            throw new ParerInternalError(messaggio, ex);
        }

        logger.debug("[cleanTransformationDirectory] fine cancellazione (" + transformationDirectory + ")");
    }

    private void registerOutputPackages(List<String> zipNames, PigObject fatherObj, PigTipoObject tipoObjectGen,
            PigVers versGen) throws ParerInternalError {
        logger.info("[notificaOggettoTrasformato] inizio registrazione pacchetti figli su oggetto id: {} nome: {}",
                fatherObj.getIdObject(), fatherObj.getCdKeyObject());

        // Controlla che, se esiste un'esecuzione precedente, il numero di figli generati allora sia uguale
        // al numero di figli generati nell'esecuzione corrente.
        List<PigObjectTrasf> previouslyGeneratedPigObjects = trasformazioniHelper.searchGeneratedPigObjects(fatherObj);
        if (!previouslyGeneratedPigObjects.isEmpty() && previouslyGeneratedPigObjects.size() != zipNames.size()) {
            String messaggio = "Il numero (" + previouslyGeneratedPigObjects.size()
                    + ") di pacchetti figli generati all'esecuzione " + "precedente è differente dal numero ("
                    + zipNames.size() + ") di pacchetti generati dall'esecuzione corrente.";
            logger.error(messaggio);
            throw new ParerInternalError(messaggio);
        }

        for (String packageName : zipNames) {
            File packageFile = new File(packageName);
            if (packageFile.isFile() && packageFile.getAbsolutePath().endsWith(Constants.STANDARD_PACKAGE_EXTENSION)) {
                try (InputStream is = new FileInputStream(packageFile)) {
                    String cdObjectKey = FilenameUtils.removeExtension(packageFile.getName());

                    String hash = jobHelper.calculateHash(is);

                    PigObjectTrasf pigObjectTrasf = trasformazioniHelper.findGeneratedPigObjectTrasf(cdObjectKey,
                            fatherObj);

                    if (pigObjectTrasf == null) {
                        pigObjectTrasf = new PigObjectTrasf();
                    }

                    pigObjectTrasf.setCdKeyObjectTrasf(cdObjectKey);
                    pigObjectTrasf.setPigObject(fatherObj);
                    pigObjectTrasf.setPgOggettoTrasf(
                            new BigDecimal(cdObjectKey.substring(Math.max(0, cdObjectKey.length() - 4))));
                    pigObjectTrasf.setCdEncodingHashFileVers("hexBinary");
                    pigObjectTrasf.setDsHashFileVers(hash);
                    pigObjectTrasf.setDsObjectTrasf("");
                    pigObjectTrasf.setDsPath(cdObjectKey);
                    pigObjectTrasf.setPigTipoObject(tipoObjectGen);
                    pigObjectTrasf.setPigVer(versGen);
                    pigObjectTrasf.setTiAlgoHashFileVers(Constants.TipoHash.SHA_1);

                    trasformazioniHelper.insertEntity(pigObjectTrasf, false);
                } catch (Exception ex) {
                    String messaggio = "Errore nella registrazione dei pacchetti: ";
                    messaggio += ExceptionUtils.getRootCauseMessage(ex);
                    logger.error(messaggio, ex);
                    throw new ParerInternalError(messaggio, ex);
                }
            }
        }

        logger.info("[notificaOggettoTrasformato] fine registrazione pacchetti figli su oggetto id: {} nome: {}",
                fatherObj.getIdObject(), fatherObj.getCdKeyObject());
    }

    private List<String> createOutputPackages(String udsFinalDirectory, String outputDirectory, PigObject po)
            throws IOException, NoSuchAlgorithmException {
        logger.info("[notificaOggettoTrasformato] inizio creazione pacchetti figli su oggetto id: {} nome: {}",
                po.getIdObject(), po.getCdKeyObject());
        List<String> zipNames = new ArrayList<>();

        File udsFinalDirectoryFile = new File(udsFinalDirectory);

        int count = 0;
        int maxZipSize = Integer
                .parseInt(configurationHelper.getValoreParamApplicByApplic(Constants.NUMERO_UNITA_DOC_ZIP));
        ZipOutputStream zipOS = null;
        String zipFileBasePath = outputDirectory + File.separator + po.getCdKeyObject();
        try {
            String[] entries = udsFinalDirectoryFile.list();

            int packagesTobeGenerated = entries.length / maxZipSize + (entries.length % maxZipSize != 0 ? 1 : 0);
            logger.info("[notificaOggettoTrasformato] {} pacchetti figli da creare per oggetto id: {}  nome: {}",
                    packagesTobeGenerated, po.getIdObject(), po.getCdKeyObject());

            // ordinali alfabeticamente
            Arrays.sort(entries);

            for (int i = 0; i < entries.length; i++) {
                if (i % maxZipSize == 0) {
                    count += 1;

                    if (zipOS != null) {
                        zipOS.close();
                        logger.info(
                                "[notificaOggettoTrasformato] fine generazione pacchetto {}/{} per oggetto id: {} nome: {}",
                                count, packagesTobeGenerated, po.getIdObject(), po.getCdKeyObject());
                    }

                    logger.info(
                            "[notificaOggettoTrasformato] inizio generazione pacchetto {}/{} per oggetto id: {} nome: {}",
                            count, packagesTobeGenerated, po.getIdObject(), po.getCdKeyObject());

                    // MEV 27880 - genero l'md5 dell'id del versatore in modo da avewre una string con lunghezza fissa.
                    MessageDigest md = MessageDigest.getInstance("MD5");
                    md.update(po.getPigVer().getIdVers().toString().getBytes());
                    byte[] digest = md.digest();
                    String versIdHash = DatatypeConverter.printHexBinary(digest).toUpperCase();

                    // crea la cartella che conterrà lo zip
                    File finalOutputDirectory = new File(
                            zipFileBasePath + "_" + versIdHash + "_" + String.format("%04d", count));
                    finalOutputDirectory.mkdirs();

                    File finalFileName = new File(finalOutputDirectory, po.getCdKeyObject() + "_" + versIdHash + "_"
                            + String.format("%04d", count) + Constants.STANDARD_PACKAGE_EXTENSION);
                    zipNames.add(finalFileName.getCanonicalPath());

                    zipOS = new ZipOutputStream(new FileOutputStream(finalFileName));

                }

                addUdToZipFile(new File(udsFinalDirectoryFile + File.separator + entries[i]), entries[i], zipOS);
            }
        } finally {
            // chiudi anche l'ultimo stream
            if (zipOS != null) {
                zipOS.close();
            }
        }

        logger.info("[notificaOggettoTrasformato] generati {} pacchetti per oggetto id: {} nome: {}", count,
                po.getIdObject(), po.getCdKeyObject());

        return zipNames;
    }

    private void addUdToZipFile(File udPath, String udName, ZipOutputStream zos) throws IOException {
        String[] files = udPath.list();
        for (String file : files) {
            File location = new File(udPath, file);
            String entryName = udName + File.separator + file;

            ZipEntry zipEntry = null;

            try (FileInputStream fis = new FileInputStream(location)) {
                zipEntry = new ZipEntry(entryName);
                zos.putNextEntry(zipEntry);
                IOUtils.copy(fis, zos);
            } finally {
                if (zos != null) {
                    zos.closeEntry();
                }

            }
        }
    }

    private boolean verifyUdsNames(String udsFinalDirectory) {
        // la cartella che passiamo come argomento deve contenere solo alre cartelle ben formarte, contenenti un file
        // con nome ben formato.
        String regExp = "^(([^\\^]+)\\^([0-9]{4})\\^([^\\^]+))$";
        Pattern validEntryPattern = Pattern.compile(regExp);

        regExp = "^(([^\\^]+)\\^([0-9]{4})\\^([^\\^]+)\\.xml)$";
        Pattern validEntryPatternForFile = Pattern.compile(regExp);

        File udsFinalDirectoryFile = new File(udsFinalDirectory);
        String[] entries = udsFinalDirectoryFile.list();
        for (String entry : entries) {
            File udDirectory = new File(udsFinalDirectory, entry);
            Matcher entryMatcher = validEntryPattern.matcher(entry);

            if (udDirectory.isDirectory() && entryMatcher.find() && entryMatcher.groupCount() == 4) {
                boolean udFound = false;

                String[] files = udDirectory.list();
                for (String file : files) {
                    Matcher entryMatcherFile = validEntryPatternForFile.matcher(file);
                    if (entryMatcherFile.find() && entryMatcherFile.groupCount() == 4) {
                        udFound = true;
                    }
                }

                if (!udFound) {
                    return false;
                }
            } else {
                return false;
            }
        }

        return true;
    }

    // This method applies the xslFilename to inFilename and writes
    // the output to outFilename.
    private String transformReport(String inXml, String xslFilename) {
        StringWriter outWriter = new StringWriter();
        try {
            // Create transformer factory
            TransformerFactory factory = TransformerFactory.newInstance();

            // Use the factory to create a template containing the xsl file
            Templates template = factory.newTemplates(new StreamSource(getClass().getResourceAsStream(xslFilename)));

            // Use the template to create a transformer
            Transformer xformer = template.newTransformer();

            // Prepare the input and output files
            Source source = new StreamSource(new StringReader(inXml));
            Result result = new StreamResult(outWriter);

            // Apply the xsl file to the source file and write the result
            // to the output file
            xformer.transform(source, result);
        } catch (TransformerConfigurationException e) {
            // An error occurred in the XSL file
            logger.info("[transformReport] errore nel file xsl: {}", e.getMessage());
        } catch (TransformerException e) {
            // An error occurred while applying the XSL file
            // Get location of error in input file
            SourceLocator locator = e.getLocator();
            int col = locator.getColumnNumber();
            int line = locator.getLineNumber();

            logger.info("[transformReport] errore nella trasformaziione xsl: {} col {} line {}", e.getMessage(), col,
                    line);
        }

        return outWriter.getBuffer().toString();
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
}
