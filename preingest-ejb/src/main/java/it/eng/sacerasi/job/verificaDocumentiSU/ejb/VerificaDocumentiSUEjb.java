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

package it.eng.sacerasi.job.verificaDocumentiSU.ejb;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.annotation.Resource;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.parer.objectstorage.dto.ObjectStorageBackend;
import it.eng.parer.objectstorage.exceptions.ObjectStorageException;
import it.eng.parer.objectstorage.helper.SalvataggioBackendHelper;
import it.eng.sacerasi.common.Constants;
import it.eng.sacerasi.entity.PigErrore;
import it.eng.sacerasi.entity.PigStrumUrbDocumenti;
import it.eng.sacerasi.entity.PigStrumentiUrbanistici;
import it.eng.sacerasi.helper.GenericHelper;
import it.eng.sacerasi.job.ejb.JobLogger;
import it.eng.sacerasi.job.util.NfsUtils;
import it.eng.sacerasi.job.util.VerificheDocumentiSUSismaEtc;
import it.eng.sacerasi.messages.MessaggiHelper;
import it.eng.sacerasi.strumentiUrbanistici.dto.VerificaZipFileResponse;
import it.eng.sacerasi.viewEntity.PigVSuLisDocDaVerif;
import it.eng.sacerasi.web.helper.ConfigurationHelper;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

/**
 *
 * @author Gilioli_P
 */
@Stateless(mappedName = "VerificaDocumentiSUEjb")
@LocalBean
@Interceptors({ it.eng.sacerasi.aop.TransactionInterceptor.class })
public class VerificaDocumentiSUEjb {

    public static final String FILENAME_FORMAT_REGEXP = "[0-9a-zA-Z\\.\\s\\-\\_\\(\\)\\/]+";

    public static final String PING_ERRSU22 = "PING-ERRSU22";

    Logger log = LoggerFactory.getLogger(VerificaDocumentiSUEjb.class);
    @EJB
    private VerificaDocumentiSUHelper verificaDocumentiSUHelper;
    @EJB
    private ConfigurationHelper configurationHelper;
    @EJB
    private JobLogger jobLoggerEjb;
    @EJB
    private GenericHelper genericHelper;
    @EJB
    private MessaggiHelper messaggiHelper;
    @Resource
    private SessionContext sessionContext;
    @EJB
    private SalvataggioBackendHelper salvataggioBackendHelper;

    public void verificaDocumenti() throws IOException, ObjectStorageException {
        log.info(VerificaDocumentiSUEjb.class.getSimpleName()
                + " --- Chiamata JOB per verifica documenti strumenti urbanistici");
        List<PigVSuLisDocDaVerif> documentiDaVerificare = verificaDocumentiSUHelper.getDocumentiDaVerificare();
        log.info("Recuperati " + documentiDaVerificare.size() + " documenti strumenti urbanistici da elaborare");
        Integer numFiles = null;
        PigErrore errore = null;
        String report = null;

        ObjectStorageBackend config = salvataggioBackendHelper.getObjectStorageConfiguration("STR_URBANISTICI",
                configurationHelper.getValoreParamApplicByApplic(Constants.BUCKET_VERIFICA_STRUMENTI_URBANISTICI));

        // Per ogni file dello Strumento Urbanistico corrente
        if (salvataggioBackendHelper.isActive()) {
            for (PigVSuLisDocDaVerif documentoDaVerificare : documentiDaVerificare) {
                try {
                    PigStrumUrbDocumenti strumUrbDocumenti = genericHelper.findById(PigStrumUrbDocumenti.class,
                            documentoDaVerificare.getPigVSuLisDocDaVerifId().getIdStrumUrbDocumenti());

                    // Chiamata di tipo HEAD (non contiene il body in quanto ho bisogno di una sola informazione stile
                    // ack)
                    // Accedo al bucket e recupero il file, se esiste
                    boolean doesObjectExist = salvataggioBackendHelper.doesObjectExist(config,
                            strumUrbDocumenti.getNmFileOs());

                    if (doesObjectExist) {
                        File tempFile = null;
                        try {
                            ResponseInputStream<GetObjectResponse> objectContent = salvataggioBackendHelper
                                    .getObject(config, strumUrbDocumenti.getNmFileOs());
                            // Partendo dall'input stream S3 Amazon, recupero il file
                            // Creo il file (che mi aspetto zip) in una cartella temporanea
                            tempFile = File.createTempFile(strumUrbDocumenti.getNmFileOs(), null,
                                    new File(System.getProperty("java.io.tmpdir")));
                            IOUtils.copy(objectContent, new FileOutputStream(tempFile));

                            // 1° Controllo che sia effettivamente un file zip
                            if (!FilenameUtils.getExtension(strumUrbDocumenti.getNmFileOrig()).equals("zip")) {
                                errore = messaggiHelper.retrievePigErrore("PING-ERRSU11");
                            }

                            if (!VerificheDocumentiSUSismaEtc.isValidZip(tempFile)) {
                                errore = messaggiHelper.retrievePigErrore("PING-ERRSU05");
                                errore.setDsErrore(
                                        StringUtils.replace(errore.getDsErrore(), "{0}", tempFile.getName()));
                            }

                            // Inizio i successivi controlli senza dover scompattare il file zip
                            VerificaZipFileResponse response = checkTempZipFile(strumUrbDocumenti.getNmFileOrig(),
                                    tempFile, strumUrbDocumenti);
                            errore = response.getErrore();
                            numFiles = response.getFilesCount();
                            report = response.getReport();

                        } finally {
                            if (tempFile != null) {
                                Files.delete(tempFile.toPath());
                            }
                        }
                    } else {
                        errore = messaggiHelper.retrievePigErrore("PING-ERRSU04");
                        errore.setDsErrore(
                                StringUtils.replace(errore.getDsErrore(), "{0}", strumUrbDocumenti.getNmFileOs()));
                    }
                    if (errore == null) {
                        // Ho passato tutti i controlli, esito positivo
                        registraEsitoDocumentiSU(
                                documentoDaVerificare.getPigVSuLisDocDaVerifId().getIdStrumUrbDocumenti(), "1",
                                numFiles, null, null, null);
                    } else {
                        // MEV25704 - Ho trovato degli errori, registro anche il report
                        registraEsitoDocumentiSU(
                                documentoDaVerificare.getPigVSuLisDocDaVerifId().getIdStrumUrbDocumenti(),
                                Constants.DB_FALSE, null, errore.getCdErrore(), errore.getDsErrore(), report);
                    }
                } catch (Exception ex) {
                    log.error(String.format("Errore tecnico su doc [%d] del SU [%d]",
                            documentoDaVerificare.getPigVSuLisDocDaVerifId().getIdStrumUrbDocumenti().longValueExact(),
                            documentoDaVerificare.getPigVSuLisDocDaVerifId().getIdStrumentiUrbanistici()
                                    .longValueExact()),
                            ex);
                    errore = messaggiHelper.retrievePigErrore("PING-ERRSU22");
                    registraEsitoDocumentiSU(documentoDaVerificare.getPigVSuLisDocDaVerifId().getIdStrumUrbDocumenti(),
                            "0", null, errore.getCdErrore(), errore.getDsErrore(), null);
                }
            }
        }

        jobLoggerEjb.writeAtomicLog(Constants.NomiJob.VERIFICA_DOCUMENTI_STRUMENTI_URBANISTICI,
                Constants.TipiRegLogJob.FINE_SCHEDULAZIONE, null);
        log.info(VerificaDocumentiSUEjb.class.getSimpleName()
                + " --- FINE chiamata per verifica documenti strumenti urbanistici");
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void callVerificaDocumentiAsync(BigDecimal idStrumentoUrbanistico) {
        log.info(VerificaDocumentiSUEjb.class.getSimpleName() + " --- Inizio chiamata asincrona...");
        try {
            // Scrivo nella tabella dei log l'inizio del "job" e relativo riferimento allo strumento urbanistico
            // indicando come ID_RECORD lo strumento urbanistico
            jobLoggerEjb.writeAtomicLog(Constants.NomiJob.VERIFICA_DOCUMENTI_STRUMENTI_URBANISTICI,
                    Constants.TipiRegLogJob.INIZIO_SCHEDULAZIONE, null, idStrumentoUrbanistico);
            sessionContext.getBusinessObject(VerificaDocumentiSUEjb.class)
                    .verificaDocumentiAsync(idStrumentoUrbanistico);
        } catch (Exception e) {
            // INUTILI in quanto intercettati
        }
        log.info(VerificaDocumentiSUEjb.class.getSimpleName() + " --- Fine chiamata asincrona...");
    }

    @Asynchronous
    public void verificaDocumentiAsync(BigDecimal idStrumentoUrbanistico) throws IOException {
        // Locko lo strumento urbanistico che sto considerando
        log.info(VerificaDocumentiSUEjb.class.getSimpleName() + " --- Richiesta LOCK su strumento urbanistico "
                + idStrumentoUrbanistico);
        verificaDocumentiSUHelper.findByIdWithLock(PigStrumentiUrbanistici.class, idStrumentoUrbanistico);

        log.info(VerificaDocumentiSUEjb.class.getSimpleName()
                + " --- Chiamata JOB per verifica documenti dello strumento urbanistico con id: "
                + idStrumentoUrbanistico);

        // Recupero i documenti da verificare
        List<PigVSuLisDocDaVerif> documentiDaVerificare = verificaDocumentiSUHelper
                .getDocumentiDaVerificare(idStrumentoUrbanistico);
        log.info("Recuperati " + documentiDaVerificare.size() + " documenti per lo strumento urbanistico da elaborare");

        // Per ogni documento dello Strumento Urbanistico corrente, ne eseguo la verifica
        for (PigVSuLisDocDaVerif documentoDaVerificare : documentiDaVerificare) {
            try {
                eseguiVerifica(documentoDaVerificare.getPigVSuLisDocDaVerifId().getIdStrumUrbDocumenti());
            } catch (Exception ex) {
                log.error(String.format("Errore tecnico su doc [%d] del SU [%d]",
                        documentoDaVerificare.getPigVSuLisDocDaVerifId().getIdStrumUrbDocumenti().longValueExact(),
                        documentoDaVerificare.getPigVSuLisDocDaVerifId().getIdStrumentiUrbanistici().longValueExact()),
                        ex);
                PigErrore errore = messaggiHelper.retrievePigErrore(PING_ERRSU22);
                registraEsitoDocumentiSU(documentoDaVerificare.getPigVSuLisDocDaVerifId().getIdStrumUrbDocumenti(), "0",
                        null, errore.getCdErrore(), errore.getDsErrore(), null);
            }
        }

        jobLoggerEjb.writeAtomicLog(Constants.NomiJob.VERIFICA_DOCUMENTI_STRUMENTI_URBANISTICI,
                Constants.TipiRegLogJob.FINE_SCHEDULAZIONE, null, idStrumentoUrbanistico);
        log.info(VerificaDocumentiSUEjb.class.getSimpleName()
                + " --- FINE chiamata per verifica documenti strumenti urbanistici");
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    private void eseguiVerifica(BigDecimal idStrumUrbDocumenti) throws IOException, ObjectStorageException {
        Integer numFiles = null;
        PigErrore errore = null;
        String report = null;

        PigStrumUrbDocumenti strumUrbDocumenti = verificaDocumentiSUHelper.findById(PigStrumUrbDocumenti.class,
                idStrumUrbDocumenti.longValue());

        // Chiamata di tipo HEAD (non contiene il body in quanto ho bisogno di una sola informazione stile ack)
        // Accedo al bucket e recupero il file documento, se esiste
        ObjectStorageBackend config = salvataggioBackendHelper.getObjectStorageConfiguration("STR_URBANISTICI",
                configurationHelper.getValoreParamApplicByApplic(Constants.BUCKET_VERIFICA_STRUMENTI_URBANISTICI));

        if (salvataggioBackendHelper.isActive()) {
            boolean doesObjectExist = salvataggioBackendHelper.doesObjectExist(config, strumUrbDocumenti.getNmFileOs());

            if (doesObjectExist) {
                File tempFile = null;
                try {
                    // Recupero l'oggetto
                    ResponseInputStream<GetObjectResponse> objectContent = salvataggioBackendHelper.getObject(config,
                            strumUrbDocumenti.getNmFileOs());
                    // Partendo dall'input stream S3 Amazon, recupero il file
                    // Creo il file (che mi aspetto zip) in una cartella temporanea
                    String rootFtp = configurationHelper.getValoreParamApplicByApplic(Constants.ROOT_FTP);
                    String dsPathInputFtp = strumUrbDocumenti.getPigStrumentiUrbanistici().getPigVer()
                            .getDsPathInputFtp();
                    String cdKey = strumUrbDocumenti.getPigStrumentiUrbanistici().getCdKey();
                    String dirCompletaFtp = rootFtp + dsPathInputFtp + cdKey;
                    // Creo la directory
                    NfsUtils.createEmptyDir(dirCompletaFtp);
                    // Ci piazzo il file zip temporaneo sul quale farò i controlli
                    tempFile = File.createTempFile("SU_", null, new File(dirCompletaFtp));
                    // Il file temporaneo deve essere messo in input_folder o in una analoga in fileserver
                    IOUtils.copy(objectContent, new FileOutputStream(tempFile));

                    // Controllo che sia effettivamente un file zip
                    if (!FilenameUtils.getExtension(strumUrbDocumenti.getNmFileOrig()).equals("zip")) {
                        errore = messaggiHelper.retrievePigErrore("PING-ERRSU11");
                    }

                    if (!VerificheDocumentiSUSismaEtc.isValidZip(tempFile)) {
                        errore = messaggiHelper.retrievePigErrore("PING-ERRSU05");
                        errore.setDsErrore(StringUtils.replace(errore.getDsErrore(), "{0}", tempFile.getName()));
                    }

                    // Inizio i successivi controlli senza dover scompattare il file zip
                    VerificaZipFileResponse response = checkTempZipFile(strumUrbDocumenti.getNmFileOrig(), tempFile,
                            strumUrbDocumenti);
                    errore = response.getErrore();
                    numFiles = response.getFilesCount();
                    report = response.getReport();

                } finally {
                    if (tempFile != null) {
                        Files.delete(tempFile.toPath());
                    }
                }
            } else {
                errore = messaggiHelper.retrievePigErrore("PING-ERRSU04");
                errore.setDsErrore(StringUtils.replace(errore.getDsErrore(), "{0}", strumUrbDocumenti.getNmFileOs()));
            }
        }

        if (errore == null) {
            // Ho passato tutti i controlli, esito positivo
            registraEsitoDocumentiSU(idStrumUrbDocumenti, "1", numFiles, null, null, null);
        } else {
            // MEV25704 - Ho trovato degli errori, registro anche il report
            registraEsitoDocumentiSU(idStrumUrbDocumenti, Constants.DB_FALSE, null, errore.getCdErrore(),
                    errore.getDsErrore(), report);
        }
    }

    public VerificaZipFileResponse checkTempZipFile(String nomeFileZipOriginale, File file,
            PigStrumUrbDocumenti strumUrbDocumenti) throws FileNotFoundException, IOException {
        VerificaZipFileResponse response = new VerificaZipFileResponse();
        StringBuilder report = new StringBuilder("");
        ZipEntry entry;
        ZipFile zipFile;
        Enumeration<? extends ZipEntry> entries;
        zipFile = new ZipFile(file);
        entries = zipFile.entries();
        Integer numFiles = null;
        if (entries.hasMoreElements()) {
            // estraggo i file
            numFiles = 0;
            while (entries.hasMoreElements()) {
                entry = entries.nextElement();
                // MEV#20262 Controllare lunghezza di tutto il path non superiore a 254 caratteri.
                // MEV#25743 - Modifiche alla verifica dei file caricati - inclusione del nome del file zip nel
                // controllo di lunghezza path
                // Se il nome dello zip originale + tutto il path dell'entry è superiore a 254 caratteri da errore.
                if (VerificheDocumentiSUSismaEtc.isLongMoreThan254Chars(nomeFileZipOriginale, entry.getName())) {
                    PigErrore errore = messaggiHelper.retrievePigErrore("PING-ERRSU22");
                    response.setErrore(errore);

                    report.append("ERRORE LUNGHEZZA PATH: ").append(entry.getName());
                    report.append(System.getProperty("line.separator"));
                }
                // MEV #24938: Adeguamenti progetto sisma
                if (!entry.getName().matches(FILENAME_FORMAT_REGEXP)) {
                    // MEV 30808
                    int pointOfFailure = VerificheDocumentiSUSismaEtc
                            .firstFailurePoint(Pattern.compile(FILENAME_FORMAT_REGEXP), entry.getName());

                    PigErrore errore = messaggiHelper.retrievePigErrore("PING-ERRSISMA26");
                    response.setErrore(errore);

                    report.append("ERRORE CARATTERI NON AMMESSI: ").append(entry.getName())
                            .append(System.getProperty("line.separator"))
                            .append("   Posizione primo carattere errato: ").append(pointOfFailure);
                    report.append(System.getProperty("line.separator"));
                }

                if (!entry.isDirectory()) {
                    numFiles++;
                    // Controlli sul fileTemp
                    if (entry.getSize() == 0) {
                        PigErrore errore = messaggiHelper.retrievePigErrore("PING-ERRSU06");
                        errore.setDsErrore(
                                StringUtils.replace(errore.getDsErrore(), "{0}", strumUrbDocumenti.getNmFileOs()));
                        response.setErrore(errore);

                        report.append("FILE VUOTO: ").append(entry.getName());
                        report.append(System.getProperty("line.separator"));
                    }

                    if (FilenameUtils.getExtension(entry.getName()).equals("zip")) {
                        PigErrore errore = messaggiHelper.retrievePigErrore("PING-ERRSU12");
                        response.setErrore(errore);

                        report.append("FILE ZIP NON AMMESSI: ").append(entry.getName());
                        report.append(System.getProperty("line.separator"));
                    }
                }
            }
            if (strumUrbDocumenti.getPigStrumUrbValDoc().getFlDocPrincipale().equals("1") && numFiles > 1) {
                PigErrore errore = messaggiHelper.retrievePigErrore("PING-ERRSU10");
                response.setErrore(errore);
            }
        } else {
            PigErrore errore = messaggiHelper.retrievePigErrore("PING-ERRSU03");
            errore.setDsErrore(StringUtils.replace(errore.getDsErrore(), "{0}", strumUrbDocumenti.getNmFileOs()));
            response.setErrore(errore);
        }

        zipFile.close();

        response.setReport(report.toString());
        response.setFilesCount(numFiles);

        return response;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    private void registraEsitoDocumentiSU(BigDecimal idStrumUrbDocumenti, String flEsitoVerifica, Integer numFiles,
            String cdErr, String dsErr, String report) {
        // Assumo il lock sull'oggetto PIG_STRUM_URB_DOCUMENTI
        PigStrumUrbDocumenti strumUrbDocumenti = genericHelper.findByIdWithLock(PigStrumUrbDocumenti.class,
                idStrumUrbDocumenti.longValue());
        strumUrbDocumenti.setFlEsitoVerifica(flEsitoVerifica);
        if (numFiles != null) {
            strumUrbDocumenti.setNumFiles(BigDecimal.valueOf(numFiles));
        }
        strumUrbDocumenti.setCdErr(cdErr);
        strumUrbDocumenti.setDsErr(dsErr);
        strumUrbDocumenti.setBlReport(report);
    }

    // Controlla se per lo strumento urbanistico in questione esistono ancora documenti da verificare
    public boolean existsDocumentiDaVerificarePerStrumentoUrbanisticoByVista(BigDecimal idStrumentoUrbanistico) {
        return verificaDocumentiSUHelper
                .existsDocumentiDaVerificarePerStrumentoUrbanisticoByVista(idStrumentoUrbanistico);
    }

    // Controlla se per lo strumento urbanistico in questione esistono ancora documenti da verificare
    public boolean existsDocumentiDaVerificarePerStrumentoUrbanistico(BigDecimal idStrumentoUrbanistico) {
        return verificaDocumentiSUHelper.existsDocumentiDaVerificarePerStrumentoUrbanistico(idStrumentoUrbanistico);
    }

    public boolean isVerificaTerminata(BigDecimal idStrumentoUrbanistico) {
        return verificaDocumentiSUHelper.isVerificaTerminata(idStrumentoUrbanistico);
    }

    public boolean verificaInCorso(BigDecimal idStrumentoUrbanistico) {
        return verificaDocumentiSUHelper.verificaInCorso(idStrumentoUrbanistico);
    }

    public boolean existsDocumentiDaVerificareConErrorePerStrumentoUrbanistico(BigDecimal idStrumentoUrbanistico) {
        return verificaDocumentiSUHelper
                .existsDocumentiVerificatiConErrorePerStrumentoUrbanistico(idStrumentoUrbanistico);
    }

    public boolean existsDocumentiDaVerificareSenzaErrorePerStrumentoUrbanistico(BigDecimal idStrumentoUrbanistico) {
        return verificaDocumentiSUHelper
                .existsDocumentiDaVerificareSenzaErrorePerStrumentoUrbanistico(idStrumentoUrbanistico);
    }

    public List<String> getDocumentiVerificatiConErrorePerStrumentoUrbanistico(BigDecimal idStrumentoUrbanistico) {
        return verificaDocumentiSUHelper.getDocumentiVerificatiConErrorePerStrumentoUrbanistico(idStrumentoUrbanistico);
    }
}
