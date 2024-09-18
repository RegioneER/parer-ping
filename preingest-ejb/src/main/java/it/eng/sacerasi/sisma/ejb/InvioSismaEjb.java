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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.ejb.ApplicationException;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.parer.objectstorage.dto.ObjectStorageBackend;
import it.eng.parer.objectstorage.exceptions.ObjectStorageException;
import it.eng.parer.objectstorage.helper.SalvataggioBackendHelper;
import it.eng.sacerasi.common.Constants;
import it.eng.sacerasi.entity.PigErrore;
import it.eng.sacerasi.entity.PigObject;
import it.eng.sacerasi.entity.PigSisma;
import it.eng.sacerasi.entity.PigSisma.TiStato;
import it.eng.sacerasi.entity.PigSismaDocumenti;
import it.eng.sacerasi.entity.PigSismaFaseProgetto;
import it.eng.sacerasi.entity.PigSismaFinanziamento;
import it.eng.sacerasi.entity.PigSismaProgettiAg;
import it.eng.sacerasi.entity.PigSismaStatoProgetto;
import it.eng.sacerasi.entity.PigSismaValAtto;
import it.eng.sacerasi.entity.PigTipoObject;
import it.eng.sacerasi.entity.PigVers;
import it.eng.sacerasi.grantEntity.SIOrgEnteSiam;
import it.eng.sacerasi.helper.GenericHelper;
import it.eng.sacerasi.job.ejb.JobLogger;
import it.eng.sacerasi.job.util.NfsUtils;
import it.eng.sacerasi.messages.MessaggiHelper;
import it.eng.sacerasi.sisma.dto.DatiAnagraficiDto;
import it.eng.sacerasi.sisma.xml.invioSisma.CamiciaFascicoloType;
import it.eng.sacerasi.sisma.xml.invioSisma.ProgettiSisma;
import it.eng.sacerasi.sisma.xml.invioSisma.ProgettiSisma.Ente;
import it.eng.sacerasi.sisma.xml.invioSisma.ProgettiSisma.TipiDocumento;
import it.eng.sacerasi.sisma.xml.invioSisma.ProgettiSisma.TipiDocumento.TipoDocumento;
import it.eng.sacerasi.sisma.xml.invioSisma.ProgettiSisma.TipoDocumentoPrincipale;
import it.eng.sacerasi.viewEntity.PigVSismaChecks;
import it.eng.sacerasi.web.ejb.AmministrazioneEjb;
import it.eng.sacerasi.web.helper.AmministrazioneHelper;
import it.eng.sacerasi.web.helper.ConfigurationHelper;
import it.eng.sacerasi.ws.ejb.XmlContextCache;
import it.eng.sacerasi.ws.invioOggettoAsincrono.ejb.InvioOggettoAsincronoEjb;
import it.eng.sacerasi.ws.notificaTrasferimento.dto.FileDepositatoType;
import it.eng.sacerasi.ws.notificaTrasferimento.dto.ListaFileDepositatoType;
import it.eng.sacerasi.ws.notificaTrasferimento.ejb.NotificaTrasferimentoEjb;
import it.eng.sacerasi.ws.response.InvioOggettoAsincronoRisposta;
import it.eng.sacerasi.ws.response.NotificaTrasferimentoRisposta;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

@Stateless(mappedName = "InvioSismaEjb")
@LocalBean
public class InvioSismaEjb {

    public static final String NOME_TIPO_FILE = "ProgettoRicostruzione";

    public static final String ERR_01 = "PING-ERRSISMA01";
    public static final String ERR_INVIO_SISMA = " --- ERRORE invio sisma";

    Logger log = LoggerFactory.getLogger(InvioSismaEjb.class);
    @EJB
    private InvioSismaHelper invioSismaHelper;
    @EJB
    private JobLogger jobLoggerEjb;
    @EJB
    private InvioOggettoAsincronoEjb invioOggettoAsincronoEjb;
    @EJB
    private NotificaTrasferimentoEjb notificaTrasferimentoEjb;
    @EJB
    private SismaEjb sismaEjb;
    @EJB
    private SismaHelper sismaHelper;
    @EJB
    private GenericHelper genericHelper;
    @EJB
    private XmlContextCache xmlContextCache;
    @EJB
    private MessaggiHelper messaggiHelper;
    @EJB
    private ConfigurationHelper configurationHelper;
    @EJB
    private InvioSismaEjb me;
    @EJB
    private AmministrazioneHelper amministrazioneHelper;
    @EJB
    private AmministrazioneEjb amministrazioneEjb;
    @EJB
    private SalvataggioBackendHelper salvataggioBackendHelper;

    public void invioSisma() {
        log.info("{} --- Chiamata JOB per invio sisma", InvioSismaEjb.class.getSimpleName());
        List<Long> ids = invioSismaHelper.getIdSismaDaInviare();
        log.info("Recuperati {} sisma da inviare", ids.size());
        for (Long idSismaDaInviare : ids) {
            // Apro una transazione (recupero un proxy per invocare il metodo con una nuova transazione)
            try {
                me.gestisciInvioSisma(idSismaDaInviare);
            } catch (InvioSismaException e) {
                registraErroreSisma(idSismaDaInviare, e.getCdErr(), e.getDsErr(), TiStato.ERRORE);
            } catch (Exception ex) {
                PigErrore errore = messaggiHelper.retrievePigErrore("PING-ERRSISMA23");
                registraErroreSisma(idSismaDaInviare, errore.getCdErrore(), errore.getDsErrore(), TiStato.ERRORE);
            }
        }
        jobLoggerEjb.writeAtomicLog(Constants.NomiJob.INVIO_SISMA, Constants.TipiRegLogJob.FINE_SCHEDULAZIONE, null);
        log.info("{} --- FINE chiamata per invio sisma", InvioSismaEjb.class.getSimpleName());
    }

    @ApplicationException(rollback = true)
    private class InvioSismaException extends Exception {

        private static final long serialVersionUID = 1L;
        private final long idSismaDaInviare;
        private final String cdErr;
        private final String dsErr;

        public InvioSismaException(final long idSismaDaInviare, final String cdErr, final String dsErr) {
            this.idSismaDaInviare = idSismaDaInviare;
            this.cdErr = cdErr;
            this.dsErr = dsErr;
        }

        public long getIdSismaDaInviare() {
            return idSismaDaInviare;
        }

        public String getCdErr() {
            return cdErr;
        }

        public String getDsErr() {
            return dsErr;
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void gestisciInvioSisma(long idSismaDaInviare)
            throws InvioSismaException, ObjectStorageException, IOException {
        // Locko sisma
        PigSisma sismaDaInviare = genericHelper.findByIdWithLock(PigSisma.class, idSismaDaInviare);
        /*
         * Determina se deve versare ad agenzia o a SA Pubblico. Se il flagInviatoAEnte==true significa che il primo
         * invio è già stato fatto
         */
        PigVers vers; // Conterrà il versatore su cui versare!
        boolean daVersareInAgenzia = false;
        Enum<Constants.TipoVersatore> tipoVersatore = sismaHelper.getTipoVersatore(sismaDaInviare.getPigVer());
        if (tipoVersatore.equals(Constants.TipoVersatore.SA_PRIVATO)
                || sismaDaInviare.getFlInviatoAEnte().equals(Constants.DB_TRUE)) {
            // Il progetto deve essere inviato per forza solo in agenzia
            vers = sismaDaInviare.getPigVerAg();
            daVersareInAgenzia = true;
        } else {
            vers = sismaDaInviare.getPigVer();
        }
        if (!invioSismaHelper.existsPigObjectPerVersatoreNoSisma(vers.getIdVers(), sismaDaInviare.getCdKey())) {
            // Verifico se lo stato è ancora "RICHIESTA_INVIO"
            if (sismaDaInviare.getTiStato().equals(TiStato.RICHIESTA_INVIO)) {
                sismaDaInviare.setCdErr(null);
                sismaDaInviare.setDsErr(null);
                // A quel punto locko pure i figli
                for (PigSismaDocumenti sismaDocumenti : sismaDaInviare.getPigSismaDocumentis()) {
                    genericHelper.findByIdWithLock(PigSismaDocumenti.class, sismaDocumenti.getIdSismaDocumenti());
                }
                // Controlli tramite vista PIG_V_SISMA_CHECKS
                PigVSismaChecks check = genericHelper.findViewById(PigVSismaChecks.class,
                        BigDecimal.valueOf(sismaDaInviare.getIdSisma()));
                // Controlli sui documenti di sisma attraverso i valori della vista
                if (check.getFlVerificaErrata().equals(Constants.DB_TRUE)
                        || check.getFlVerificaInCorso().equals(Constants.DB_TRUE)
                        || check.getFlFileMancante().equals(Constants.DB_TRUE)) {
                    log.error("{} --- ERRORE condizioni invio sisma", InvioSismaEjb.class.getSimpleName());
                    PigErrore errore = messaggiHelper.retrievePigErrore(ERR_01);
                    throw new InvioSismaException(idSismaDaInviare, errore.getCdErrore(), errore.getDsErrore());
                }
                /**
                 * ******** AVVIO IL PROCESSO DI INVIO ********
                 */
                // Setta lo stato di PigSisma
                sismaDaInviare.setTiStato(PigSisma.TiStato.INVIO_IN_CORSO);
                sismaDaInviare.setDtStato(new Date());
                genericHelper.getEntityManager().flush();
                String xmlVersamento = "";
                try {
                    // Genero l'xml di versamento
                    xmlVersamento = creaXml(sismaDaInviare, vers, daVersareInAgenzia);
                } catch (Exception ex) {
                    log.error("{} --- ERRORE creazione XML invio sisma", InvioSismaEjb.class.getSimpleName(), ex);
                    PigErrore errore = messaggiHelper.retrievePigErroreNewTx("PING-ERRSISMA14");
                    throw new InvioSismaException(idSismaDaInviare, errore.getCdErrore(), errore.getDsErrore());
                }
                // Preparo il file zip contenente tutti i files relativi a sisma +
                // Sisma.xml
                File fileXmlVersamento = null;
                String nomeFilePacchetto = sismaDaInviare.getCdKey() + ".zip";
                String rootFtp = configurationHelper.getValoreParamApplicByApplic(Constants.ROOT_FTP);
                String dsPathInputFtp = vers.getDsPathInputFtp();
                String dirCompletaFtp = rootFtp + dsPathInputFtp;
                File fileTemporaneoGenerale = null;
                try {
                    fileTemporaneoGenerale = new File(dirCompletaFtp + nomeFilePacchetto + ".TEMP");
                    try (FileOutputStream fos = new FileOutputStream(fileTemporaneoGenerale);
                            ZipOutputStream zos = new ZipOutputStream(fos)) {
                        // Creo il file con l'xml
                        fileXmlVersamento = File.createTempFile(InvioSismaHelper.NOME_FILE_XML, "",
                                new File(dirCompletaFtp));
                        FileUtils.writeStringToFile(fileXmlVersamento, xmlVersamento, StandardCharsets.UTF_8);
                        // Lo aggiungo allo zip
                        addToZipFile(fileXmlVersamento, zos, InvioSismaHelper.NOME_FILE_XML);
                        // Recupero i file dall'object storage
                        List<PigSismaDocumenti> documentiDaInviare = invioSismaHelper
                                .getDocumentiDaInviare(sismaDaInviare.getIdSisma());
                        //
                        ObjectStorageBackend config = salvataggioBackendHelper.getObjectStorageConfiguration("SISMA",
                                configurationHelper.getValoreParamApplicByApplic(Constants.BUCKET_VERIFICA_SISMA));

                        if (salvataggioBackendHelper.isActive()) {
                            for (PigSismaDocumenti sismaDocumenti : documentiDaInviare) {
                                String nmFileOs = sismaDocumenti.getNmFileOs();
                                String nmFileOrig = sismaDocumenti.getNmFileOrig();
                                // Chiamata di tipo HEAD (non contiene il body in quanto ho bisogno di una sola
                                // informazione
                                // stile ack)
                                boolean doesObjectExist = salvataggioBackendHelper.doesObjectExist(config, nmFileOs);
                                if (doesObjectExist) {
                                    File tempFile = File.createTempFile(nmFileOrig, "", new File(dirCompletaFtp));
                                    try (FileOutputStream fosTemp = new FileOutputStream(tempFile);) {
                                        ResponseInputStream<GetObjectResponse> object = salvataggioBackendHelper
                                                .getObject(config, nmFileOs);
                                        // Creo il file in una cartella temporanea
                                        IOUtils.copy(object, fosTemp);
                                        String nomeFileLowerCase = sismaDocumenti.getPigSismaValDoc()
                                                .getNmTipoDocumento().replace(" ", "_").toLowerCase() + ".zip";
                                        // Aggiungo il file scaricato dall'OS al file zip
                                        addToZipFile(tempFile, zos, nomeFileLowerCase);
                                    } finally {
                                        if (tempFile != null) {
                                            FileUtils.deleteQuietly(tempFile);
                                        }
                                    }
                                } else {
                                    log.error("{} --- ERRORE creazione ZIP invio sisma",
                                            InvioSismaEjb.class.getSimpleName());
                                    PigErrore errore = messaggiHelper.retrievePigErrore("PING-ERRSISMA04");
                                    String dsErrore = StringUtils.replace(errore.getDsErrore(), "{0}",
                                            sismaDocumenti.getNmFileOs());
                                    throw new InvioSismaException(idSismaDaInviare, errore.getCdErrore(), dsErrore);
                                }
                            }
                        }
                    } catch (Exception ex) {
                        log.error("{} --- ERRORE creazione ZIP invio sisma", InvioSismaEjb.class.getSimpleName(), ex);
                        PigErrore errore = messaggiHelper.retrievePigErrore("PING-ERRSISMA15");
                        throw new InvioSismaException(idSismaDaInviare, errore.getCdErrore(), errore.getDsErrore());
                    }
                    String nmUserid = sismaDaInviare.getIamUser().getNmUserid();
                    String nmAmbienteVers = vers.getPigAmbienteVer().getNmAmbienteVers();
                    String nmVers = vers.getNmVers();
                    String cdKeyObject = sismaDaInviare.getCdKey();
                    // Controlla che nel sistema non esista già l’ oggetto in fase di invio
                    // Ora include anche gli oggetti in stato annullato per reinviarli
                    if ((!invioSismaHelper.existsPigObjectPerVersatore(vers.getIdVers(), sismaDaInviare.getCdKey()))
                            || invioSismaHelper.existsPigObjectPerVersatoreSismaAnnullato(vers.getIdVers(),
                                    sismaDaInviare.getCdKey())) {
                        // Chiama il servizio NotificaInvioOggetto (metodo invioOggettoAsincrono)
                        PigTipoObject pigTipoObject = amministrazioneHelper.getPigTipoObjectByName(
                                InvioSismaHelper.NOME_TIPO_OGGETTO_DA_TRASFORMARE, new BigDecimal(vers.getIdVers()));
                        boolean flFileCifrato = false;
                        boolean flForzaWarning = false;
                        boolean flForzaAccettazione = false;
                        String cdVersioneXML = "1.0";
                        InvioOggettoAsincronoRisposta risposta = null;
                        try {
                            String priorita = null;
                            if (pigTipoObject.getTiPriorita() != null) {
                                priorita = it.eng.sacerasi.web.util.Constants.ComboFlagPrioTrasfType
                                        .getEnumByString(pigTipoObject.getTiPriorita());
                            }
                            risposta = invioOggettoAsincronoEjb.invioOggettoAsincronoEsteso(nmUserid, nmAmbienteVers,
                                    nmVers, cdKeyObject, null, InvioSismaHelper.NOME_TIPO_OGGETTO_DA_TRASFORMARE,
                                    flFileCifrato, flForzaWarning, flForzaAccettazione, null, cdVersioneXML,
                                    xmlVersamento, null, null, null, null, null, null, null, null, priorita, null);
                        } catch (Exception e) {
                            log.error("{}{}", InvioSismaEjb.class.getSimpleName(), ERR_INVIO_SISMA);
                            PigErrore errore = messaggiHelper.retrievePigErroreNewTx("PING-ERRSISMA17");
                            throw new InvioSismaException(idSismaDaInviare, errore.getCdErrore(),
                                    errore.getDsErrore() + e);
                        }
                        if (risposta.getCdEsito().equals(Constants.EsitoServizio.KO)) {
                            PigErrore errore = messaggiHelper.retrievePigErrore(risposta.getCdErr());
                            throw new InvioSismaException(idSismaDaInviare, errore.getCdErrore(), errore.getDsErrore());
                        }
                    } else if (invioSismaHelper.existsPigObjectPerVersatoreSismaInAttesaFile(vers.getIdVers(),
                            sismaDaInviare.getCdKey())) {
                        //
                    } else {
                        log.error("{}{}", InvioSismaEjb.class.getSimpleName(), ERR_INVIO_SISMA);
                        PigErrore errore = messaggiHelper.retrievePigErrore(ERR_01);
                        throw new InvioSismaException(idSismaDaInviare, errore.getCdErrore(), errore.getDsErrore());
                    }
                    String zipSisma = sismaDaInviare.getCdKey();
                    try {
                        // Effettua lo scarico del nuovo file da trasformare nella cartella ftp di SacerPing dell'utente
                        // relativo nella cartella INPUT_FOLDER
                        NfsUtils.createEmptyDir(dirCompletaFtp + zipSisma);
                        Files.move(Paths.get(dirCompletaFtp + nomeFilePacchetto + ".TEMP"),
                                Paths.get(String.join(zipSisma, dirCompletaFtp, "/", ".zip")));
                    } catch (IOException ex) {
                        log.error("{}{}", InvioSismaEjb.class.getSimpleName(), ERR_INVIO_SISMA);
                        PigErrore errore = messaggiHelper.retrievePigErrore("PING-ERRSISMA15");
                        throw new InvioSismaException(idSismaDaInviare, errore.getCdErrore(), errore.getDsErrore());
                    }
                    invioSismaHelper.getEntityManager().flush();
                    // Chiama il servizio NotificaTrasferimentoFile (metodo notificaAvvenutoTrasferimentoFile)
                    PigObject obj = invioSismaHelper.getPigObjectPerVersatoreSismaInNewTx(vers.getIdVers(),
                            cdKeyObject);
                    if (obj == null) {
                        log.error("{}{}", InvioSismaEjb.class.getSimpleName(), ERR_INVIO_SISMA);
                        PigErrore errore = messaggiHelper.retrievePigErrore(ERR_01);
                        throw new InvioSismaException(idSismaDaInviare, errore.getCdErrore(), errore.getDsErrore());
                    } else if (obj.getTiStatoObject().equals("DA_TRASFORMARE")) {
                        return;
                    } else if (!obj.getTiStatoObject().equals("IN_ATTESA_FILE")) {
                        log.error("{}{}", InvioSismaEjb.class.getSimpleName(), ERR_INVIO_SISMA);
                        PigErrore errore = messaggiHelper.retrievePigErrore("PING-ERRSISMA20");
                        String dsErrore = StringUtils.replace(errore.getDsErrore(), "{0}",
                                "" + sismaDaInviare.getIdSisma());
                        throw new InvioSismaException(idSismaDaInviare, errore.getCdErrore(), dsErrore);
                    } else if (obj.getTiStatoObject().equals("IN_ATTESA_FILE")) {
                        NotificaTrasferimentoRisposta risposta = null;
                        try {
                            ListaFileDepositatoType listaFileDepositatoType = new ListaFileDepositatoType();
                            FileDepositatoType fileDepositatoType = new FileDepositatoType();
                            fileDepositatoType.setNmTipoFile(NOME_TIPO_FILE);
                            fileDepositatoType.setNmNomeFile(nomeFilePacchetto);
                            listaFileDepositatoType.getFileDepositato().add(fileDepositatoType);
                            risposta = notificaTrasferimentoEjb.notificaAvvenutoTrasferimentoFileInNewTx(nmAmbienteVers,
                                    nmVers, cdKeyObject, listaFileDepositatoType);
                        } catch (Exception e) {
                            log.error("{}{}", InvioSismaEjb.class.getSimpleName(), ERR_INVIO_SISMA);
                            PigErrore errore = messaggiHelper.retrievePigErroreNewTx("PING-ERRSISMA18");
                            throw new InvioSismaException(idSismaDaInviare, errore.getCdErrore(), errore.getDsErrore());
                        }
                        if (risposta.getCdEsito().equals(Constants.EsitoServizio.KO.name())) {
                            PigErrore errore = messaggiHelper.retrievePigErrore(risposta.getCdErr());
                            throw new InvioSismaException(idSismaDaInviare, errore.getCdErrore(), errore.getDsErrore());
                        }
                    }
                    // Il sistema effettua il caricamento del file ZipSisma nel Bucket
                    // BUCKET_SISMA_TRASFORMATI
                    try {
                        ObjectStorageBackend config = salvataggioBackendHelper.getObjectStorageConfiguration("SISMA",
                                configurationHelper.getValoreParamApplicByApplic(Constants.BUCKET_VERIFICA_SISMA));
                        if (salvataggioBackendHelper.isActive()) {
                            salvataggioBackendHelper.putS3Object(config, sismaDaInviare.getCdKeyOs() + ".zip", new File(
                                    rootFtp + "/" + dsPathInputFtp + "/" + zipSisma + "/" + zipSisma + ".zip"));
                        }
                    } catch (SdkClientException e) {
                        log.error("{}{}{}", InvioSismaEjb.class.getSimpleName(), " --- ERRORE invio sisma: ",
                                e.getMessage());
                        PigErrore errore = messaggiHelper.retrievePigErroreNewTx("PING-ERRSISMA19");
                        throw new InvioSismaException(idSismaDaInviare, errore.getCdErrore(), errore.getDsErrore());
                    }
                    // Setta lo stato di PigSisma
                    Enum<Constants.TipoVersatore> tipo = sismaHelper.getTipoVersatore(sismaDaInviare.getPigVer());
                    if (tipo.equals(Constants.TipoVersatore.SA_PUBBLICO)
                            && sismaDaInviare.getFlInviatoAEnte().equals(Constants.DB_FALSE)) {
                        sismaDaInviare.setTiStato(TiStato.IN_ELABORAZIONE_SA);
                    } else {
                        sismaDaInviare.setTiStato(TiStato.IN_ELABORAZIONE);
                    }

                    sismaDaInviare.setDtStato(new Date());
                    genericHelper.getEntityManager().flush();
                } finally {
                    if (fileXmlVersamento != null) {
                        FileUtils.deleteQuietly(fileXmlVersamento);
                    }
                    if (fileTemporaneoGenerale != null) {
                        FileUtils.deleteQuietly(fileTemporaneoGenerale);
                    }
                }
            }
        } else {
            log.error("{}{}", InvioSismaEjb.class.getSimpleName(), ERR_INVIO_SISMA);
            PigErrore errore = messaggiHelper.retrievePigErrore("PING-SENDOBJ-OBJ-001");
            throw new InvioSismaException(idSismaDaInviare, errore.getCdErrore(), errore.getDsErrore());
        }
    }

    public void addToZipFile(File file, ZipOutputStream zos, String nomeFileLowerCase) throws IOException {
        try (FileInputStream fis = new FileInputStream(file)) {
            ZipEntry zipEntry = new ZipEntry(nomeFileLowerCase);
            zos.putNextEntry(zipEntry);
            byte[] bytes = new byte[1024];
            int length;
            while ((length = fis.read(bytes)) >= 0) {
                zos.write(bytes, 0, length);
            }
            zos.closeEntry();
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    private String creaXml(PigSisma sismaDaInviare, PigVers pigVers, boolean daVersareInAgenzia)
            throws InvioSismaException, JAXBException {
        ProgettiSisma sisma = new ProgettiSisma();
        // Popolo l'ente
        sisma.setEnte(new Ente());
        SIOrgEnteSiam orgEnteSiam = sismaHelper.getOrgEnteSiamByPigVers(pigVers);
        String tipoVersatore = "";
        PigSismaProgettiAg pigSismaProgettiAg = sismaDaInviare.getPigSismaProgettiAg();
        PigSismaFinanziamento pigSismaFinanziamento = pigSismaProgettiAg.getPigSismaFinanziamento();
        PigSismaValAtto pigSismaValAtto = sismaDaInviare.getPigSismaValAtto();
        PigSismaFaseProgetto pigSismaFaseProgetto = sismaDaInviare.getPigSismaFaseProgetto();
        PigSismaStatoProgetto pigSismaStatoProgetto = sismaDaInviare.getPigSismaStatoProgetto();
        if (pigVers.getIdEnteConvenz() != null) {
            tipoVersatore = it.eng.sacerasi.entity.constraint.SIOrgEnteSiam.TiEnteConvenz.PRODUTTORE.name();
        } else {
            tipoVersatore = amministrazioneEjb.getTipologiaEnteNonConvenz(pigVers.getIdEnteFornitEstern());
        }
        // Valorizzazione dati Ente
        sisma.getEnte().setTipologiaVersatore(tipoVersatore);
        sisma.getEnte().setVersatore(pigVers.getNmVers());
        sisma.getEnte().setDenominazioneEnteVersante(orgEnteSiam.getNmEnteSiam());
        // Valorizzazione dati principali Sisma
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        // Valorizzazione dati profilo archivistico
        ProgettiSisma.ProfiloArchivistico profiloArchivistico = new ProgettiSisma.ProfiloArchivistico();
        sisma.setProfiloArchivistico(profiloArchivistico);
        CamiciaFascicoloType camiciaFascicoloType = new CamiciaFascicoloType();
        CamiciaFascicoloType.Fascicolo fascicolo = new CamiciaFascicoloType.Fascicolo();
        CamiciaFascicoloType.SottoFascicolo sottoFascicolo = new CamiciaFascicoloType.SottoFascicolo();
        profiloArchivistico.setFascicoloPrincipale(camiciaFascicoloType);
        camiciaFascicoloType.setFascicolo(fascicolo);
        camiciaFascicoloType.setSottoFascicolo(sottoFascicolo);
        if (daVersareInAgenzia) {
            // Versamento all'Agenzia, Dati agenzia
            sisma.setTipoRegistro(pigSismaFinanziamento.getDsTipoRegistroAgenzia());
            sisma.setData(dateFormat.format(sismaDaInviare.getDataAg())); // CAMPO DATA AGENZIA
            sisma.setNumero(sismaDaInviare.getRegistroAg() + "_" + sismaDaInviare.getNumeroAg()); // CAMPO NUMERO
            // AGENZIA
            sisma.setAnno(sismaDaInviare.getAnnoAg().intValueExact()); // CAMPO ANNO AGENZIA
            // Dati Ente
            sisma.setTipoRegistroEnte(pigSismaFinanziamento.getDsTipoRegistroSaPubblico());
            sisma.setAnnoRegistroEnte(sismaDaInviare.getAnno().intValueExact());
            sisma.setNumeroRegistroEnte(pigSismaProgettiAg.getCodiceIntervento() + "_" + pigSismaValAtto.getTiTipoAtto()
                    + "_" + sismaDaInviare.getNumero());
            sisma.setDataEnte(dateFormat.format(sismaDaInviare.getData()));
            // DATI PROFILO
            camiciaFascicoloType.setClassifica(sismaDaInviare.getClassificaAg());
            fascicolo.setIdentificativo(sismaDaInviare.getIdFascicoloAg());
            fascicolo.setOggetto(sismaDaInviare.getOggettoFascicoloAg());
            sottoFascicolo.setIdentificativo(sismaDaInviare.getIdSottofascicoloAg());
            sottoFascicolo.setOggetto(sismaDaInviare.getOggettoSottofascicoloAg());
            // MANCANO PERO' I FASCICOLI SECONDARI !!!!!
        } else {
            // Versamento all'Ente
            String strDate = dateFormat.format(sismaDaInviare.getData());
            sisma.setData(strDate);
            sisma.setAnno(sismaDaInviare.getAnno().shortValueExact());
            sisma.setNumero(pigSismaProgettiAg.getCodiceIntervento() + "_" + pigSismaValAtto.getTiTipoAtto() + "_"
                    + sismaDaInviare.getNumero());
            sisma.setTipoRegistro(pigSismaFinanziamento.getDsTipoRegistroSaPubblico());
            // DATI PROFILO
            camiciaFascicoloType.setClassifica(sismaDaInviare.getClassifica());
            fascicolo.setIdentificativo(sismaDaInviare.getIdFascicolo());
            fascicolo.setOggetto(sismaDaInviare.getOggettoFascicolo());
            sottoFascicolo.setIdentificativo(sismaDaInviare.getIdSottofascicolo());
            sottoFascicolo.setOggetto(sismaDaInviare.getOggettoSottofascicolo());
            // MANCANO PERO' I FASCICOLI SECONDARI !!!!!
        }
        sisma.setOggetto(sismaDaInviare.getOggetto());
        sisma.setIdentificativoAttoEnte(pigSismaValAtto.getTiTipoAtto() + "_" + sismaDaInviare.getNumero());
        sisma.setLineaFinanziamento(pigSismaFinanziamento.getDsTipoFinanziamento());
        sisma.setCodiceIntervento(pigSismaProgettiAg.getCodiceIntervento());
        DatiAnagraficiDto dto = sismaEjb
                .getDatiVersatoreByIdVers(BigDecimal.valueOf(sismaDaInviare.getPigVer().getIdVers()), sismaDaInviare);
        sisma.setSoggettoATutela(
                sismaDaInviare.getFlInterventoSoggettoATutela().equals(Constants.DB_TRUE) ? "SI" : "NO");
        sisma.setDenominazioneIntervento(pigSismaProgettiAg.getDenominazioneIntervento());
        sisma.setEnteProprietario(dto.getEnteProprietario());
        sisma.setNaturaEnteProprietario(dto.getNaturaEnteProprietario());
        sisma.setUbicazioneImmobileComune(dto.getUbicazioneComune());
        sisma.setUbicazioneImmobileProvincia(dto.getUbicazioneProvincia());
        sisma.setNote(sismaDaInviare.getDsDescrizione());
        sisma.setSoggettoAttuatore(dto.getSoggettoAttuatore());
        sisma.setNaturaSoggettoAttuatore(dto.getNaturaSoggettoAttuatore());
        sisma.setFaseProgettuale(pigSismaFaseProgetto.getDsFaseSisma());
        sisma.setStatoProgetto(pigSismaStatoProgetto.getDsStatoProgetto());
        sisma.setTipologiaUnitaDocumentaria(pigSismaFaseProgetto.getDsFaseSisma());
        // Documenti
        // PRINCIPALE
        List<Object[]> documenti = invioSismaHelper.getDocumenti(sismaDaInviare.getIdSisma());

        // MEV 29976 - cerchiamo il documento principale
        Object[] documentoPrincipale = null;
        for (Object[] documento : documenti) {
            if (documento.length >= 3 && ((String) documento[2]).equals(Constants.DB_TRUE)) {
                documentoPrincipale = documento;
                break;
            }
        }

        if (documentoPrincipale == null) {
            log.error("{}{}", InvioSismaEjb.class.getSimpleName(), ERR_INVIO_SISMA);
            PigErrore errore = messaggiHelper.retrievePigErrore("PING-SENDOBJ-OBJ-001");
            throw new InvioSismaException(sismaDaInviare.getIdSisma(), errore.getCdErrore(), errore.getDsErrore());
        }

        TipoDocumentoPrincipale tipoDocumentoPrincipale = new TipoDocumentoPrincipale();

        tipoDocumentoPrincipale.setNomeTipoDocumento((String) documentoPrincipale[0]);
        tipoDocumentoPrincipale.setNomeFileOriginale((String) documentoPrincipale[1]);
        // Calcolo il nome file versato nel seguente modo: nome tipo documento in lower case con gli underscore +
        // .zip
        String nomeFileVersato = ((String) documentoPrincipale[0]).replace(" ", "_").toLowerCase();
        tipoDocumentoPrincipale.setNomeFileVersato(nomeFileVersato + ".zip"); //
        sisma.setTipoDocumentoPrincipale(tipoDocumentoPrincipale);
        // ALTRI DOCUMENTI
        TipiDocumento tipiDocumento = new TipiDocumento();
        for (int i = 0; i < documenti.size(); i++) {
            Object[] documento = documenti.get(i);

            // MEV 29976 - scarto il documento principale
            if (documento.length >= 3 && ((String) documento[2]).equals(Constants.DB_TRUE)) {
                continue;
            }

            TipoDocumento tipoDocumento = new TipoDocumento();
            tipoDocumento.setNomeTipoDocumento((String) documento[0]);
            tipoDocumento.setNomeFileOriginale((String) documento[1]);
            nomeFileVersato = ((String) documento[0]).replace(" ", "_").toLowerCase();
            tipoDocumento.setNomeFileVersato(nomeFileVersato + ".zip");
            tipiDocumento.getTipoDocumento().add(tipoDocumento);
        }
        if (!tipiDocumento.getTipoDocumento().isEmpty()) {
            sisma.setTipiDocumento(tipiDocumento);
        }
        sisma.setTipiDocumento(tipiDocumento);
        log.info("{}{}", InvioSismaEjb.class.getSimpleName(), " --- Creazione XML invio sisma");
        String xml = marshallXmlInvioSisma(sisma);
        log.debug("{}{}", "XML: ", xml);
        return xml;
    }

    private String marshallXmlInvioSisma(ProgettiSisma sisma) throws JAXBException {
        StringWriter tmpWriter = new StringWriter();
        // Eseguo il marshalling degli oggetti creati
        Marshaller udMarshaller = xmlContextCache.getInvioSismaCtx_InvioSisma().createMarshaller();
        udMarshaller.marshal(sisma, tmpWriter);
        tmpWriter.flush();
        return tmpWriter.toString();
    }

    private void registraErroreSisma(long idSismaDaInviare, String cdErr, String dsErr, TiStato tiStato) {
        PigSisma sisma = genericHelper.findById(PigSisma.class, idSismaDaInviare);
        sisma.setCdErr(cdErr);
        sisma.setDsErr(dsErr);
        sisma.setTiStato(tiStato);
        sisma.setDtStato(new Date());
    }
}
