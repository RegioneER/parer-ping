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

package it.eng.sacerasi.sisma.ejb;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
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

import org.apache.commons.io.FileUtils;
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
import it.eng.sacerasi.entity.PigSisma;
import it.eng.sacerasi.entity.PigSismaDocEntry;
import it.eng.sacerasi.entity.PigSismaDocumenti;
import it.eng.sacerasi.helper.GenericHelper;
import it.eng.sacerasi.job.ejb.JobLogger;
import it.eng.sacerasi.job.util.NfsUtils;
import it.eng.sacerasi.job.util.VerificheDocumentiSUSismaEtc;
import it.eng.sacerasi.messages.MessaggiHelper;
import it.eng.sacerasi.sisma.dto.VerificaZipFileResponse;
import it.eng.sacerasi.web.helper.ConfigurationHelper;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

/**
 *
 * @author Gilioli_P
 */
@Stateless(mappedName = "VerificaDocumentiSismaEjb")
@LocalBean
@Interceptors({ it.eng.sacerasi.aop.TransactionInterceptor.class })
public class VerificaDocumentiSismaEjb {

    public static final String FILENAME_FORMAT_REGEXP = "[0-9a-zA-Z\\.\\s\\-\\_\\(\\)\\/]+";

    Logger log = LoggerFactory.getLogger(VerificaDocumentiSismaEjb.class);
    @EJB
    private VerificaDocumentiSismaHelper verificaDocumentiSismaHelper;
    @EJB
    private SismaHelper sismaHelper;
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

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void callVerificaDocumentiAsync(BigDecimal idSisma) {
        log.info(VerificaDocumentiSismaEjb.class.getSimpleName() + " --- Inizio chiamata asincrona...");
        try {
            // Scrivo nella tabella dei log l'inizio del "job" e relativo riferimento a sisma
            // indicando come ID_RECORD sisma
            jobLoggerEjb.writeAtomicLog(Constants.NomiJob.VERIFICA_DOCUMENTI_SISMA,
                    Constants.TipiRegLogJob.INIZIO_SCHEDULAZIONE, null, idSisma);
            sessionContext.getBusinessObject(VerificaDocumentiSismaEjb.class).verificaDocumentiAsync(idSisma);
        } catch (Exception e) {
            // INUTILI in quanto intercettati
        }
        log.info(VerificaDocumentiSismaEjb.class.getSimpleName() + " --- Fine chiamata asincrona...");
    }

    @Asynchronous
    public void verificaDocumentiAsync(BigDecimal idSisma) throws IOException {
        // Locko sisma che sto considerando
        log.info(VerificaDocumentiSismaEjb.class.getSimpleName() + " --- Richiesta LOCK su sisma " + idSisma);
        verificaDocumentiSismaHelper.findByIdWithLock(PigSisma.class, idSisma);

        log.info(VerificaDocumentiSismaEjb.class.getSimpleName()
                + " --- Chiamata JOB per verifica documenti di sisma con id: " + idSisma);
        // Recupero i documenti da verificare
        List<PigSismaDocumenti> documentiDaVerificare = verificaDocumentiSismaHelper.getDocumentiDaVerificare(idSisma);
        log.info("Recuperati " + documentiDaVerificare.size() + " documenti per sisma da elaborare");
        // Per ogni documento di sisma corrente, ne eseguo la verifica
        for (PigSismaDocumenti documentoDaVerificare : documentiDaVerificare) {
            try {
                eseguiVerifica(new BigDecimal(documentoDaVerificare.getIdSismaDocumenti()));
            } catch (Exception ex) {
                log.error(String.format("Errore tecnico su doc [%d] del sisma [%d]",
                        documentoDaVerificare.getIdSismaDocumenti(), idSisma.longValueExact()), ex);
                PigErrore errore = messaggiHelper.retrievePigErrore("PING-ERRSISMA22");
                registraEsitoDocumentiSisma(new BigDecimal(documentoDaVerificare.getIdSismaDocumenti()),
                        Constants.DB_FALSE, null, errore.getCdErrore(), errore.getDsErrore(), null);
            }
        }
        jobLoggerEjb.writeAtomicLog(Constants.NomiJob.VERIFICA_DOCUMENTI_SISMA,
                Constants.TipiRegLogJob.FINE_SCHEDULAZIONE, null, idSisma);
        log.info(VerificaDocumentiSismaEjb.class.getSimpleName() + " --- FINE chiamata per verifica documenti sisma");
    }

    // MEV26267 - in caso di verifica fallita genero un report con l'elenco dei file in errore.
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    private void eseguiVerifica(BigDecimal idSismaDocumenti) throws IOException, ObjectStorageException {
        Integer numFiles = null;
        PigErrore errore = null;
        String report = null;

        PigSismaDocumenti sismaDocumenti = verificaDocumentiSismaHelper.findById(PigSismaDocumenti.class,
                idSismaDocumenti.longValue());
        // Chiamata di tipo HEAD (non contiene il body in quanto ho bisogno di una sola informazione stile ack)
        // Accedo al bucket e recupero il file documento, se esiste
        ObjectStorageBackend config = salvataggioBackendHelper.getObjectStorageConfiguration("SISMA",
                configurationHelper.getValoreParamApplicByApplic(Constants.BUCKET_VERIFICA_SISMA));

        if (salvataggioBackendHelper.isActive()) {
            boolean doesObjectExist = salvataggioBackendHelper.doesObjectExist(config, sismaDocumenti.getNmFileOs());
            if (doesObjectExist) {
                File tempFile = null;
                try {
                    // Recupero l'oggetto
                    ResponseInputStream<GetObjectResponse> object = salvataggioBackendHelper.getObject(config,
                            sismaDocumenti.getNmFileOs());
                    // Creo il file (che mi aspetto zip) in una cartella temporanea
                    String rootFtp = configurationHelper.getValoreParamApplicByApplic(Constants.ROOT_FTP);
                    String dsPathInputFtp = sismaDocumenti.getPigSisma().getPigVer().getDsPathInputFtp();
                    String cdKey = sismaDocumenti.getPigSisma().getCdKey();
                    String dirCompletaFtp = rootFtp + dsPathInputFtp + cdKey;
                    // Creo la directory
                    NfsUtils.createEmptyDir(dirCompletaFtp);
                    // Ci piazzo il file zip temporaneo sul quale farò i controlli
                    tempFile = File.createTempFile("SISMA_", null, new File(dirCompletaFtp));
                    // Il file temporaneo deve essere messo in input_folder o in una analoga in fileserver
                    FileOutputStream fos = new FileOutputStream(tempFile);
                    IOUtils.copy(object, fos);
                    fos.close();

                    // Controllo che sia effettivamente un file zip
                    if (!FilenameUtils.getExtension(sismaDocumenti.getNmFileOrig()).equals("zip")) {
                        errore = messaggiHelper.retrievePigErrore("PING-ERRSISMA11");
                    }

                    if (!VerificheDocumentiSUSismaEtc.isValidZip(tempFile)) {
                        errore = messaggiHelper.retrievePigErrore("PING-ERRSISMA05");
                        errore.setDsErrore(StringUtils.replace(errore.getDsErrore(), "{0}", tempFile.getName()));
                    }

                    // Inizio i successivi controlli senza dover scompattare il file zip
                    VerificaZipFileResponse response = checkTempZipFile(sismaDocumenti.getNmFileOrig(), tempFile,
                            sismaDocumenti);
                    errore = response.getErrore();
                    numFiles = response.getFilesCount();
                    report = response.getReport();
                } finally {
                    if (tempFile != null) {
                        FileUtils.deleteQuietly(tempFile);
                    }
                }
            } else {
                errore = messaggiHelper.retrievePigErrore("PING-ERRSISMA04");
                errore.setDsErrore(StringUtils.replace(errore.getDsErrore(), "{0}", sismaDocumenti.getNmFileOs()));
            }
        }

        if (errore == null) {
            // Ho passato tutti i controlli, esito positivo
            registraEsitoDocumentiSisma(idSismaDocumenti, Constants.DB_TRUE, numFiles, null, null, null);
        } else {
            // MEV26267 - Ho trovato degli errori, registro anche il report
            registraEsitoDocumentiSisma(idSismaDocumenti, Constants.DB_FALSE, null, errore.getCdErrore(),
                    errore.getDsErrore(), report);
        }
    }

    public VerificaZipFileResponse checkTempZipFile(String nomeFileZipOriginale, File file,
            PigSismaDocumenti sismaDocumenti) throws IOException {
        VerificaZipFileResponse response = new VerificaZipFileResponse();
        StringBuilder report = new StringBuilder("");
        ZipEntry entry;
        ZipFile zipFile;
        Enumeration<? extends ZipEntry> entries;
        zipFile = new ZipFile(file);
        entries = zipFile.entries();
        Integer numFiles = null;
        sismaHelper.cancellaEntryDocumenti(sismaDocumenti);
        if (entries.hasMoreElements()) {
            // estraggo i file
            numFiles = 0;
            while (entries.hasMoreElements()) {
                entry = entries.nextElement();
                // Registra ogni Entry all'interno dello zip sul DB
                PigSismaDocEntry pigSismaDocEntry = new PigSismaDocEntry();
                pigSismaDocEntry.setNmEntry(entry.getName());
                pigSismaDocEntry.setPigSismaDocumenti(sismaDocumenti);
                // Forse non serve....
                verificaDocumentiSismaHelper.getEntityManager().persist(pigSismaDocEntry);
                // MEV#20262 Controllare lunghezza di tutto il path non superiore a 254 caratteri.
                // MEV#25743 - Modifiche alla verifica dei file caricati - inclusione del nome del file zip nel
                // controllo di lunghezza path
                // Se il nome dello zip originale + tutto il path dell'entry è superiore a 254 caratteri da errore.
                if (VerificheDocumentiSUSismaEtc.isLongMoreThan254Chars(nomeFileZipOriginale, entry.getName())) {
                    PigErrore errore = messaggiHelper.retrievePigErrore("PING-ERRSISMA22");
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
                        PigErrore errore = messaggiHelper.retrievePigErrore("PING-ERRSISMA06");
                        errore.setDsErrore(
                                StringUtils.replace(errore.getDsErrore(), "{0}", sismaDocumenti.getNmFileOs()));
                        response.setErrore(errore);

                        report.append("FILE VUOTO: ").append(entry.getName());
                        report.append(System.getProperty("line.separator"));
                    }
                    if (FilenameUtils.getExtension(entry.getName()).equals("zip")) {
                        PigErrore errore = messaggiHelper.retrievePigErrore("PING-ERRSISMA12");
                        response.setErrore(errore);

                        report.append("FILE ZIP NON AMMESSI: ").append(entry.getName());
                        report.append(System.getProperty("line.separator"));
                    }
                }
            }

            if (sismaDocumenti.getPigSismaValDoc().getFlDocPrincipale().equals("1") && numFiles > 1) {
                PigErrore errore = messaggiHelper.retrievePigErrore("PING-ERRSISMA10");
                response.setErrore(errore);
            }

        } else {
            PigErrore errore = messaggiHelper.retrievePigErrore("PING-ERRSISMA03");
            errore.setDsErrore(StringUtils.replace(errore.getDsErrore(), "{0}", sismaDocumenti.getNmFileOs()));
            response.setErrore(errore);
        }

        zipFile.close();

        response.setReport(report.toString());
        response.setFilesCount(numFiles);

        return response;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    private void registraEsitoDocumentiSisma(BigDecimal idSismaDocumenti, String flEsitoVerifica, Integer numFiles,
            String cdErr, String dsErr, String report) {
        // Assumo il lock sull'oggetto PIG_SISMA_DOCUMENTI
        PigSismaDocumenti sismaDocumenti = genericHelper.findByIdWithLock(PigSismaDocumenti.class,
                idSismaDocumenti.longValue());
        sismaDocumenti.setFlEsitoVerifica(flEsitoVerifica);
        // Cancella eventuali OK o KO messi dal'agenzia.
        sismaDocumenti.setTiVerificaAgenzia(null);
        if (numFiles != null) {
            sismaDocumenti.setNumFiles(BigDecimal.valueOf(numFiles));
        }
        sismaDocumenti.setCdErr(cdErr);
        sismaDocumenti.setDsErr(dsErr);
        sismaDocumenti.setBlReport(report);
    }

    public boolean isVerificaTerminata(BigDecimal idSisma) {
        return verificaDocumentiSismaHelper.isVerificaTerminata(idSisma);
    }

    public boolean verificaInCorso(BigDecimal idSisma) {
        return verificaDocumentiSismaHelper.verificaInCorso(idSisma);
    }

    public boolean existsDocumentiDaVerificareConErrorePerSisma(BigDecimal idSisma) {
        return verificaDocumentiSismaHelper.existsDocumentiVerificatiConErrorePerSisma(idSisma);
    }

    public boolean existsDocumentiDaVerificareSenzaErrorePerSisma(BigDecimal idSisma) {
        return verificaDocumentiSismaHelper.existsDocumentiDaVerificareSenzaErrorePerSisma(idSisma);
    }

    public List<String> getDocumentiVerificatiConErrorePerSisma(BigDecimal idSisma) {
        return verificaDocumentiSismaHelper.getDocumentiVerificatiConErrorePerSisma(idSisma);
    }
}
