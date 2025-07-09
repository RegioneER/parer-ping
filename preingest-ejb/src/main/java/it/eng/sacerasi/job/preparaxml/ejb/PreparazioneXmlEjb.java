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

package it.eng.sacerasi.job.preparaxml.ejb;

import it.eng.parer.objectstorage.dto.BackendStorage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.MarshalException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationException;
import javax.xml.stream.XMLStreamException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import it.eng.parer.objectstorage.dto.ObjectStorageBackend;
import it.eng.parer.objectstorage.exceptions.ObjectStorageException;
import it.eng.parer.objectstorage.helper.SalvataggioBackendHelper;
import it.eng.sacerasi.common.Chiave;
import it.eng.sacerasi.common.Constants;
import it.eng.sacerasi.common.Constants.TipiEncBinari;
import it.eng.sacerasi.common.Constants.TipiHash;
import it.eng.sacerasi.corrispondenzeVers.helper.CorrispondenzeVersHelper;
import it.eng.sacerasi.entity.PigFileObject;
import it.eng.sacerasi.entity.PigFileObjectStorage;
import it.eng.sacerasi.entity.PigInfoDicom;
import it.eng.sacerasi.entity.PigObject;
import it.eng.sacerasi.entity.PigTipoFileObject;
import it.eng.sacerasi.entity.PigTipoObject;
import it.eng.sacerasi.entity.PigUnitaDocObject;
import it.eng.sacerasi.exception.ParerInternalError;
import it.eng.sacerasi.job.preparaxml.dto.FileObjectExt;
import it.eng.sacerasi.job.preparaxml.dto.FileUnitaDoc;
import it.eng.sacerasi.job.preparaxml.dto.OggettoInCoda;
import it.eng.sacerasi.job.preparaxml.dto.UnitaDocObject;
import it.eng.sacerasi.job.preparaxml.util.XmlUtils;
import it.eng.sacerasi.messages.MessaggiWSBundle;
import it.eng.sacerasi.viewEntity.PigVLisStrutVersSacer;
import it.eng.sacerasi.viewEntity.UsrVAbilStrutSacerXping;
import it.eng.sacerasi.web.helper.ConfigurationHelper;
import it.eng.sacerasi.ws.dto.IRispostaWS;
import it.eng.sacerasi.ws.ejb.XmlContextCache;
import it.eng.sacerasi.ws.xml.invioAsync.ChiaveType;
import it.eng.sacerasi.ws.xml.invioAsync.FileType;
import it.eng.sacerasi.ws.xml.invioAsync.ListaUnitaDocumentarieType;
import it.eng.sacerasi.ws.xml.invioAsync.UnitaDocumentariaType;
import it.eng.sacerasixml.xsd.util.Utils;
import java.util.logging.Level;
import javax.xml.parsers.ParserConfigurationException;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

@SuppressWarnings("unchecked")
@Stateless(mappedName = "PreparazioneXmlEjb")
@LocalBean
public class PreparazioneXmlEjb {

    private static final Logger log = LoggerFactory.getLogger(PreparazioneXmlEjb.class);
    // Singleton Ejb di gestione cache dei parser Castor
    @EJB
    XmlContextCache xmlContextCache;
    // EJB per verifiche sul DB
    @EJB
    ControlliPrepXml controlliPrepXml;
    // EJB per corrispondenze dei versatori
    @EJB
    CorrispondenzeVersHelper corVersHelper;
    //
    @EJB
    ConfigurationHelper configurationHelper;
    //
    @EJB
    SalvataggioBackendHelper salvataggioBackendHelper;

    public void prepara(OggettoInCoda oggettoInCoda, String rootDirectory)
            throws ParerInternalError, ObjectStorageException {
        PigObject pigObject = oggettoInCoda.getRifPigObject();
        PigTipoObject pigTipoObject = pigObject.getPigTipoObject();
        String pathVersatore = pigObject.getPigVer().getDsPathInputFtp();

        log.debug("PreparazioneXml :: Elaboro l'oggetto {}", pigObject.getCdKeyObject());

        oggettoInCoda.setTipoOggetto(Constants.TipiOggetto.getEnum(pigTipoObject.getNmTipoObject()));
        oggettoInCoda.setTipoVersamento(Constants.TipoVersamento.valueOf(pigTipoObject.getTiVersFile()));
        oggettoInCoda.setTipoCalcolo(Constants.TipoCalcolo.valueOf(pigTipoObject.getTiCalcKeyUnitaDoc()));
        oggettoInCoda.setUrnDirectoryOgg(pigObject.getCdKeyObject() + "/");
        oggettoInCoda.setListaFileObjectExt(new ArrayList<>());

        log.debug("costruisce la lista dei file effettivamente versati ed il loro urn");
        // costruisce la lista dei file effettivamente versati ed il loro urn
        if (pigObject.getPigFileObjects() != null && !pigObject.getPigFileObjects().isEmpty()) {
            for (PigFileObject tmpFileObject : pigObject.getPigFileObjects()) {
                FileObjectExt tmpFileObjectExt = new FileObjectExt();
                tmpFileObjectExt.setRifPigFileObject(tmpFileObject);
                tmpFileObjectExt.setUrnFile(rootDirectory + pathVersatore + oggettoInCoda.getUrnDirectoryOgg()
                        + tmpFileObject.getNmFileObject());
                tmpFileObjectExt.setUrnFileRel(
                        pathVersatore + oggettoInCoda.getUrnDirectoryOgg() + tmpFileObject.getNmFileObject());
                // default = SHA-256
                tmpFileObjectExt.setTipoHashFile(StringUtils.isNotBlank(tmpFileObject.getTiAlgoHashFileVers())
                        ? tmpFileObject.getTiAlgoHashFileVers() : TipiHash.SHA_256.descrivi());
                // encoding default = hexBinary
                tmpFileObjectExt.setEncodingFile(StringUtils.isNotBlank(tmpFileObject.getCdEncodingHashFileVers())
                        ? tmpFileObject.getCdEncodingHashFileVers() : TipiEncBinari.HEX_BINARY.descrivi());

                // MEV25602 - mi serviranno per recuperare eventualmente l'oggetto da Object Storage
                // MEV 34843 se PigFileObjectStorage esiste allora il file è salvato su OS.
                if (tmpFileObject.getPigFileObjectStorage() != null) {
                    PigFileObjectStorage pigFileObjectStorage = tmpFileObject.getPigFileObjectStorage();
                    tmpFileObjectExt.setIdBackend(pigFileObjectStorage.getIdDecBackend());
                    tmpFileObjectExt.setNmOsTenant(pigFileObjectStorage.getNmTenant());
                    tmpFileObjectExt.setNmBucket(pigFileObjectStorage.getNmBucket());
                    tmpFileObjectExt.setCdKeyFile(pigFileObjectStorage.getCdKeyFile());
                }

                oggettoInCoda.getListaFileObjectExt().add(tmpFileObjectExt);
            }
        }

        log.debug("impostazione hash, già verificati nel job di verifica asincrona hash");
        // la verifica hash non viene fatta qui ma nel job dedicato
        if (pigTipoObject.getFlContrHash() != null && pigTipoObject.getFlContrHash().equals("1")) {
            for (FileObjectExt tmpFileObjectExt : oggettoInCoda.getListaFileObjectExt()) {
                if (tmpFileObjectExt.getRifPigFileObject().getDsHashFileVers() != null
                        && !tmpFileObjectExt.getRifPigFileObject().getDsHashFileVers().isEmpty()) {
                    tmpFileObjectExt.setTipoHashFile(tmpFileObjectExt.getRifPigFileObject().getTiAlgoHashFileVers());
                    tmpFileObjectExt.setHashFile(tmpFileObjectExt.getRifPigFileObject().getDsHashFileVers());
                    log.debug("hash calcolato {} di tipo {}", tmpFileObjectExt.getHashFile(),
                            tmpFileObjectExt.getRifPigFileObject().getTiAlgoHashFileVers());
                }
            }
        }

        // se sono sopravvissuto,
        if (oggettoInCoda.getTipoVersamento().equals(Constants.TipoVersamento.NO_ZIP)
                || oggettoInCoda.getTipoVersamento().equals(Constants.TipoVersamento.ZIP_NO_XML_SACER)) {
            if (oggettoInCoda.getSeverity() != IRispostaWS.SeverityEnum.ERROR) {
                log.debug("Parsing dell'XML di versamento asincrono");
                if (oggettoInCoda.getRifPigObject().getPigXmlObjects() != null
                        && !oggettoInCoda.getRifPigObject().getPigXmlObjects().isEmpty()
                        && oggettoInCoda.getRifPigObject().getPigXmlObjects().get(0).getBlXml() != null) {
                    try {
                        Unmarshaller tmpUnmarshaller = xmlContextCache.getVersReqAsyncCtx_ListaUnitaDocumentarie()
                                .createUnmarshaller();
                        tmpUnmarshaller.setSchema(xmlContextCache.getSchemaOfInvioAsync());
                        JAXBElement<ListaUnitaDocumentarieType> elemento = (JAXBElement<ListaUnitaDocumentarieType>) tmpUnmarshaller
                                .unmarshal(Utils.getSaxSourceForUnmarshal(
                                        oggettoInCoda.getRifPigObject().getPigXmlObjects().get(0).getBlXml()));
                        oggettoInCoda.setParsedListaUnitaDoc(elemento.getValue());
                    } catch (JAXBException | SAXException e) {
                        throw new ParerInternalError(e);
                    } catch (ParserConfigurationException ex) {
                        java.util.logging.Logger.getLogger(PreparazioneXmlEjb.class.getName()).log(Level.SEVERE, null,
                                ex);
                    }
                }
                log.debug("Parsing dell'XML di versamento asincrono - OK");
            }
        }
        if (oggettoInCoda.getSeverity() != IRispostaWS.SeverityEnum.ERROR) {
            //
            oggettoInCoda.setListaUnitaDocObject(new ArrayList<>());
            //
            BigDecimal idOrganizIam = null;
            String nmUseridSacer = null;
            if (oggettoInCoda.getTipoVersamento().equals(Constants.TipoVersamento.NO_ZIP)
                    || oggettoInCoda.getTipoVersamento().equals(Constants.TipoVersamento.ZIP_NO_XML_SACER)) {
                List<PigVLisStrutVersSacer> idOrganizIamStruts = corVersHelper
                        .getIdOrganizIamStrut(pigTipoObject.getIdTipoObject());
                if (idOrganizIamStruts == null || idOrganizIamStruts.isEmpty() || idOrganizIamStruts.size() > 1) {
                    this.setError(MessaggiWSBundle.PING_PREPXML_FILE_014, MessaggiWSBundle.getString(
                            MessaggiWSBundle.PING_PREPXML_FILE_014, pigObject.getCdKeyObject()), oggettoInCoda);
                } else {
                    final PigVLisStrutVersSacer organiz = idOrganizIamStruts.get(0);
                    idOrganizIam = organiz.getPigVLisStrutVersSacerId().getIdOrganizIamStrut();
                    nmUseridSacer = organiz.getNmUseridSacer();
                }
            }

            switch (oggettoInCoda.getTipoVersamento()) {
            case NO_ZIP:
                log.debug("Gestione no zip");
                this.gestioneNoZip(idOrganizIam, nmUseridSacer, oggettoInCoda, pigTipoObject, pigObject);
                break;
            case ZIP_CON_XML_SACER:
            case ZIP_NO_XML_SACER:
                log.debug("Gestione zip");

                // MEV 25602 - se il servizio di object storage è attivo e il file
                // è conservato su OS, copiamolo su disco.
                this.copiaDaOS(oggettoInCoda);

                this.gestioneZip(idOrganizIam, nmUseridSacer, oggettoInCoda, pigTipoObject, pigObject);
                break;
            default:
                throw new ParerInternalError("Tipo SIP non supportato");
            }
        }
    }

    private void gestioneNoZip(BigDecimal idOrganizIam, String nmUseridSacer, OggettoInCoda oggettoInCoda,
            PigTipoObject pigTipoObject, PigObject pigObject) throws ParerInternalError {
        UnitaDocObject tmpUnitaDocObject = new UnitaDocObject();
        tmpUnitaDocObject.setIdOrganizSacer(idOrganizIam);
        tmpUnitaDocObject.setNmUserIdSacer(nmUseridSacer);
        if (idOrganizIam != null) {
            UsrVAbilStrutSacerXping strutturaAbilitata = corVersHelper.getStrutturaAbilitata(idOrganizIam,
                    nmUseridSacer);
            tmpUnitaDocObject.setNmAmbienteSacer(strutturaAbilitata.getNmAmbiente());
            tmpUnitaDocObject.setNmEnteSacer(strutturaAbilitata.getNmEnte());
            tmpUnitaDocObject.setNmStrutSacer(strutturaAbilitata.getNmStrut());
        }
        //
        oggettoInCoda.getListaUnitaDocObject().add(tmpUnitaDocObject);
        Chiave tmpChiave = new Chiave();
        boolean verificaCoerenza = false;

        // calcolo della chiave
        switch (oggettoInCoda.getTipoCalcolo()) {
        case CALC_DICOM:
            log.debug("Calcolo chiave dicom");
            List<PigUnitaDocObject> tmpPigUnitaDocObjects = pigObject.getPigUnitaDocObjects();
            if (tmpPigUnitaDocObjects != null && !tmpPigUnitaDocObjects.isEmpty()) {
                tmpChiave.setAnno(tmpPigUnitaDocObjects.get(0).getAaUnitaDocSacer().longValue());
                tmpChiave.setRegistro(tmpPigUnitaDocObjects.get(0).getCdRegistroUnitaDocSacer());
                tmpChiave.setNumero(tmpPigUnitaDocObjects.get(0).getCdKeyUnitaDocSacer());
                tmpUnitaDocObject.setChiaveUd(tmpChiave);
            } else {
                List<PigInfoDicom> tmpPigInfoDicoms = pigObject.getPigInfoDicoms();
                if (tmpPigInfoDicoms != null && tmpPigInfoDicoms.size() == 1) {
                    // getYear() è deprecato, ma non è colpa mia se la gestione delle date in Java fa schifo
                    tmpChiave.setAnno((long) tmpPigInfoDicoms.get(0).getDtStudyDate().getYear() + 1900);
                    //
                    tmpChiave.setRegistro(pigTipoObject.getCdRegistroUnitaDocSacer());
                    tmpChiave.setNumero(controlliPrepXml
                            .recuperaAggiornaContUDSacer(oggettoInCoda, new BigDecimal(tmpChiave.getAnno()))
                            .toString());
                    tmpUnitaDocObject.setChiaveUd(tmpChiave);
                } else {
                    throw new ParerInternalError(
                            "PigInfoDicom non può essere vuota o avere più di un elemento in questo punto.");
                }
            }
            break;
        case XML_VERS:
            log.debug("Calcolo chiave  xml vers");
            verificaCoerenza = true;
            ChiaveType tmpChiaveType;
            tmpChiaveType = oggettoInCoda.getParsedListaUnitaDoc().getUnitaDocumentaria().get(0).getChiave();
            tmpChiave.setAnno((long) tmpChiaveType.getAnno());
            tmpChiave.setNumero(tmpChiaveType.getNumero());
            tmpChiave.setRegistro(tmpChiaveType.getTipoRegistro());
            tmpUnitaDocObject.setChiaveUd(tmpChiave);
            break;
        default:
            throw new ParerInternalError("Tipo calcolo non supportato");
        }
        // calcolo chiave - fine
        log.debug("calcolo chiave - fine");
        File tmpFile;
        long tmpTotalSize = 0;
        long tmpIdFile = 1;
        tmpUnitaDocObject.setListaFileUnitaDoc(new ArrayList<>());
        // aggiungo i file...
        log.debug("aggiungo i file...");
        for (FileObjectExt tmpFileObjectExt : oggettoInCoda.getListaFileObjectExt()) {
            log.debug("aggiungo il file {}", tmpFileObjectExt.getUrnFileRel());
            FileUnitaDoc tmpFileUnitaDoc = new FileUnitaDoc();
            tmpFileUnitaDoc.setIdFile(Long.toString(tmpIdFile));
            tmpFileUnitaDoc.setUrnFile(tmpFileObjectExt.getUrnFileRel());
            tmpFileUnitaDoc.setNomeFile(tmpFileObjectExt.getRifPigFileObject().getNmFileObject());
            tmpFileUnitaDoc.setRifPigTipoFileObject(tmpFileObjectExt.getRifPigFileObject().getPigTipoFileObject());
            tmpFileUnitaDoc.setTipoHashFile(tmpFileObjectExt.getTipoHashFile());
            tmpFileUnitaDoc.setHashFile(tmpFileObjectExt.getHashFile());
            tmpFileUnitaDoc.setEncodingFile(tmpFileObjectExt.getEncodingFile());
            tmpIdFile++;
            //
            tmpFile = new File(tmpFileObjectExt.getUrnFile());
            tmpTotalSize += tmpFile.length();
            //
            tmpUnitaDocObject.getListaFileUnitaDoc().add(tmpFileUnitaDoc);
        }
        tmpUnitaDocObject.setSizeInByte(tmpTotalSize);
        log.debug("file aggiunti - fine");
        if (verificaCoerenza) {
            log.debug(
                    "Verifico se per tutti i file definiti nel XML è definito l'elemento in ListaFileUnitaDoc con stesso tipo di file");
            verificaCoerenzaUDVersNoZip(oggettoInCoda, pigObject);
        }

        int sizeUnitaDocObjects = oggettoInCoda.getListaUnitaDocObject().size();
        if (oggettoInCoda.getSeverity() != IRispostaWS.SeverityEnum.ERROR) {
            log.debug(
                    "Verifico che il numero di unità documentarie calcolate corrisponda a quelle attese all'interno dell'oggetto");
            if (pigObject.getNiUnitaDocAttese() != null
                    && pigObject.getNiUnitaDocAttese().intValue() != sizeUnitaDocObjects) {
                this.setError(MessaggiWSBundle.PING_PREPXML_FILE_013,
                        MessaggiWSBundle.getString(MessaggiWSBundle.PING_PREPXML_FILE_013, pigObject.getCdKeyObject()),
                        oggettoInCoda);
            }
        }
    }

    private void verificaCoerenzaUDVersNoZip(OggettoInCoda oggettoInCoda, PigObject pigObject) {
        for (UnitaDocumentariaType ud : oggettoInCoda.getParsedListaUnitaDoc().getUnitaDocumentaria()) {
            String tmpChiaveOut = ud.getChiave().getTipoRegistro() + "^" + ud.getChiave().getAnno() + "^"
                    + ud.getChiave().getNumero();
            for (FileType fileType : ud.getFiles().getFile()) {
                int conta = 0;
                for (UnitaDocObject unitaDocObject : oggettoInCoda.getListaUnitaDocObject()) {
                    for (FileUnitaDoc file : unitaDocObject.getListaFileUnitaDoc()) {
                        if (file.getRifPigTipoFileObject().getNmTipoFileObject().equals(fileType.getTipoFile())) {
                            conta++;
                            file.setParsedFileType(fileType);
                        }
                    }
                }
                if (conta == 0) {
                    /*
                     * se non sono riuscito ad assegnare il tipo file ad alcun file, fermo l'operazione e restituisco il
                     * nome del tipo file non trovato
                     */
                    this.setError(MessaggiWSBundle.PING_PREPXML_FILE_009,
                            MessaggiWSBundle.getString(MessaggiWSBundle.PING_PREPXML_FILE_009,
                                    pigObject.getCdKeyObject(), tmpChiaveOut, fileType.getTipoFile()),
                            oggettoInCoda);
                    break;
                }
            }
        }
    }

    //
    private static final int CNST_DIRECTORY = 1;
    private static final int CNST_KEY_REG = 2;
    private static final int CNST_KEY_ANNO = 3;
    private static final int CNST_KEY_NUM = 4;
    private static final int CNST_FILE = 5;
    //

    private void gestioneZip(BigDecimal idOrganizIam, String nmUseridSacer, OggettoInCoda oggettoInCoda,
            PigTipoObject pigTipoObject, PigObject pigObject) throws ParerInternalError {
        /*
         * RegExp ^(([^/\^]+)\^([0-9]{4})\^([^/\^]+))/([^/]+)$
         *
         */

        String regExp = "^(([^/\\^]+)\\^([0-9]{4})\\^([^/\\^]+))/([^/]+)$";
        Pattern validEntryPattrn = Pattern.compile(regExp);
        boolean prosegui = true;
        if (oggettoInCoda.getListaFileObjectExt().size() == 1) {
            String zipRelativePath = oggettoInCoda.getListaFileObjectExt().get(0).getUrnFileRel();
            String objRelativePath = new File(zipRelativePath).getParent() + File.separator;
            // set di tutte le chiavi UD definite nello ZIP
            Set<List<String>> tmpInsiemeUd = new HashSet<>();
            // mappa delle UD contenute nel file ZIP escluse perché già versate
            Map<String, UnitaDocObject> mappaUnitaDocObject = new HashMap<>();
            // mappa (sulle stesse UD di listaUnitaDocObject) per verificare rapidamente le UD già inserite
            Map<String, Chiave> mappaUDEscluse = new HashMap<>();
            Chiave tmpChiaveUdDirectory = new Chiave();
            try (ZipFile zipFile = new ZipFile(oggettoInCoda.getListaFileObjectExt().get(0).getUrnFile())) {
                // crea una directory temporanea con nome casuale nella stessa
                // directory in cui si trova lo zip da decomprimere
                // NOTA: Ho bisogno che il contenuto della cartella creata
                // da Glassfish sia accessibile da Tomcat. Di default
                // Files.createTempDirectory crea una cartella con i seguenti
                // permessi: rwx --- ---
                Set<PosixFilePermission> permsDir = PosixFilePermissions.fromString("rwxrwxr-x");
                FileAttribute<Set<PosixFilePermission>> attributeDir = PosixFilePermissions.asFileAttribute(permsDir);

                Path tmpPathZipEsploso = Files.createTempDirectory(
                        new File(oggettoInCoda.getListaFileObjectExt().get(0).getUrnFile()).getParentFile().toPath(),
                        "tp_", attributeDir);

                Enumeration<? extends ZipEntry> zipEntries = zipFile.entries();
                // questi attributi POSIX verranno imposti ai file temporanei estratti dal file ZIP
                Set<PosixFilePermission> permsFile = PosixFilePermissions.fromString("rw-rw-r--");
                FileAttribute<Set<PosixFilePermission>> attributeFile = PosixFilePermissions.asFileAttribute(permsFile);
                //
                log.debug("Apertura e analisi dello zip");
                // MAC #23269 - Gestione zip vuoti.
                if (!zipEntries.hasMoreElements()) {
                    this.setError(MessaggiWSBundle.PING_PREPXML_FILE_021,
                            MessaggiWSBundle.getString(MessaggiWSBundle.PING_PREPXML_FILE_021,
                                    pigObject.getCdKeyObject(),
                                    oggettoInCoda.getListaFileObjectExt().get(0).getUrnFile()),
                            oggettoInCoda);
                }

                // MAC #25499 - GEstione zip contenenti solo cartelle vuote
                boolean isZipEmpty = true;

                while (zipEntries.hasMoreElements() && oggettoInCoda.getSeverity() != IRispostaWS.SeverityEnum.ERROR) {
                    ZipEntry tmpArchiveEntry = zipEntries.nextElement();

                    if (tmpArchiveEntry.isDirectory()) {
                        // evita di elaborare le entry di tipo directory
                        continue;
                    }

                    if (tmpArchiveEntry.getSize() < 1) {
                        // è impossibile elaborare documenti vuoti, definiti solo
                        // dalla entry nella directory interna del file ZIP
                        this.setError(MessaggiWSBundle.PING_PREPXML_FILE_019,
                                MessaggiWSBundle.getString(MessaggiWSBundle.PING_PREPXML_FILE_019,
                                        pigObject.getCdKeyObject(), tmpArchiveEntry.getName()),
                                oggettoInCoda);
                        break;
                    }

                    // MAC #25499 - GEstione zip contenenti solo cartelle vuote
                    // ho trovato almeno un file...
                    isZipEmpty = false;

                    Matcher entryMatcher = validEntryPattrn.matcher(tmpArchiveEntry.getName());
                    if (entryMatcher.find() && entryMatcher.groupCount() == 5) { // riga buona
                        //
                        if (log.isDebugEnabled()) {
                            log.debug("Directory {}: Registro {}: Anno {}: Numero {}: File {}",
                                    entryMatcher.group(CNST_DIRECTORY), entryMatcher.group(CNST_KEY_REG),
                                    entryMatcher.group(CNST_KEY_ANNO), entryMatcher.group(CNST_KEY_NUM),
                                    entryMatcher.group(CNST_FILE));
                        }
                        // chiave
                        tmpChiaveUdDirectory = new Chiave();
                        tmpChiaveUdDirectory.setAnno(Long.parseLong(entryMatcher.group(CNST_KEY_ANNO)));
                        tmpChiaveUdDirectory.setRegistro(entryMatcher.group(CNST_KEY_REG));
                        tmpChiaveUdDirectory.setNumero(entryMatcher.group(CNST_KEY_NUM));
                        //
                        // cerco l'UD identificata tra quelle già censite
                        prosegui = true;
                        UnitaDocObject tmpUnitaDocObject = mappaUnitaDocObject.get(entryMatcher.group(CNST_DIRECTORY));
                        if (tmpUnitaDocObject == null) {
                            // non ho trovato l'UD tra quelle già censite. La devo creare.
                            if (log.isDebugEnabled()) {
                                log.debug("Creazione nuova UD {}", entryMatcher.group(CNST_DIRECTORY));
                            }
                            //
                            String[] tmpArrP = new String[] { "", "", "" };
                            tmpArrP[0] = tmpChiaveUdDirectory.getAnno().toString();
                            tmpArrP[1] = tmpChiaveUdDirectory.getRegistro();
                            tmpArrP[2] = tmpChiaveUdDirectory.getNumero();
                            tmpInsiemeUd.add(Arrays.asList(tmpArrP));
                            /*
                             * non considerare l'UD se questa è già stata versata correttamente oppure è in errore con
                             * codice UD-002 o UD-002-001 (chiave duplicata)
                             */
                            if (controlliPrepXml.verificaPUDocObjNonVersata(tmpChiaveUdDirectory,
                                    pigObject.getIdObject())) {
                                tmpUnitaDocObject = new UnitaDocObject();
                                tmpUnitaDocObject.setChiaveUd(tmpChiaveUdDirectory);
                                tmpUnitaDocObject.setChiaveCompatta(entryMatcher.group(CNST_DIRECTORY));
                                tmpUnitaDocObject.setListaFileUnitaDoc(new ArrayList<>());
                                tmpUnitaDocObject.setUrnFileZip(zipRelativePath);
                                // memorizzo nella lista e nella mappa la nuova unità documentaria creata
                                mappaUnitaDocObject.put(entryMatcher.group(CNST_DIRECTORY), tmpUnitaDocObject);
                                oggettoInCoda.getListaUnitaDocObject().add(tmpUnitaDocObject);
                            } else {
                                if (log.isDebugEnabled()) {
                                    log.debug("Creazione nuova UD non possibile perché già versata in Sacer. "
                                            + "non analizzo il file. {}", entryMatcher.group(CNST_DIRECTORY));
                                }
                                // l'UD non deve essere creata perché già versata in Sacer,
                                // quindi non proseguo oltre con la sua analisi
                                // Nota che questo non costituisce errore,
                                // perciò l'elaborazione deve comunque proseguire con l'UD successiva
                                prosegui = false;
                                /*
                                 * memorizzo, se non già fatto, la chiave di questa UD, nella lista delle UD escluse In
                                 * seguito dovrà verificare se le UD definite nell'XML asincrono sono memorizzate nella
                                 * lista UD principale oppure in questa
                                 */
                                if (mappaUDEscluse.get(entryMatcher.group(CNST_DIRECTORY)) == null) {
                                    mappaUDEscluse.put(entryMatcher.group(CNST_DIRECTORY), tmpChiaveUdDirectory);
                                }
                            }
                        }

                        if (prosegui && oggettoInCoda.getSeverity() != IRispostaWS.SeverityEnum.ERROR) {
                            /*
                             * se l'UD era già stata censita oppure è appena stata creata, devo verificare se il file
                             * riferito dalla entry dello ZIP è inseribile. Nel caso, lo inserisco.
                             */
                            FileUnitaDoc fudDaInviare = null;
                            if (oggettoInCoda.getTipoVersamento() == Constants.TipoVersamento.ZIP_CON_XML_SACER) {
                                // caso ZIP CON XML SACER
                                log.debug("caso ZIP CON XML SACER");
                                fudDaInviare = this.gestEntryZipConXml(tmpArchiveEntry, tmpUnitaDocObject,
                                        entryMatcher);
                                if (tmpUnitaDocObject.getUrnUDXml() != null
                                        && tmpUnitaDocObject.getUrnUDXml().equals(tmpArchiveEntry.getName())) {
                                    log.debug("leggi il contenuto dell'XML");
                                    ByteArrayOutputStream tmpOutputStream = new ByteArrayOutputStream();
                                    this.leggiDaZipFile(tmpOutputStream, zipFile, tmpArchiveEntry);
                                    // get xml declared encoding MAC #15042
                                    String encodingFromXMLDeclaration = XmlUtils
                                            .getXmlEcondingDeclaration(tmpOutputStream.toString()).name();
                                    tmpUnitaDocObject.setUnitaDocumentariaXml(
                                            tmpOutputStream.toString(encodingFromXMLDeclaration));
                                    it.eng.parer.ws.xml.versReq.UnitaDocumentaria tmpUd = this
                                            .caricaStruttAbilDaXmlSIP(tmpUnitaDocObject);
                                    if (!this.verificaCoerenzaChiaveXmlSIP(tmpUd, tmpChiaveUdDirectory,
                                            entryMatcher.group(CNST_DIRECTORY), oggettoInCoda, pigObject)) {
                                        // se questa verifica è fallita, annullo il riferimento al file da inviare
                                        // così da evitare di estrarlo dallo ZIP (dovrebbe in ogni caso essere null,
                                        // ma meglio non rischiare), dal momento che in ogni caso
                                        // il versamento è condannato.
                                        fudDaInviare = null;
                                    }
                                }
                            } else {
                                // caso ZIP NO XML
                                log.debug("caso ZIP NO XML");
                                tmpUnitaDocObject.setIdOrganizSacer(idOrganizIam);
                                tmpUnitaDocObject.setNmUserIdSacer(nmUseridSacer);
                                if (idOrganizIam != null) {
                                    UsrVAbilStrutSacerXping strutturaAbilitata = corVersHelper
                                            .getStrutturaAbilitata(idOrganizIam, nmUseridSacer);
                                    tmpUnitaDocObject.setNmAmbienteSacer(strutturaAbilitata.getNmAmbiente());
                                    tmpUnitaDocObject.setNmEnteSacer(strutturaAbilitata.getNmEnte());
                                    tmpUnitaDocObject.setNmStrutSacer(strutturaAbilitata.getNmStrut());
                                }
                                fudDaInviare = this.gestEntryZipNoXml(tmpArchiveEntry, tmpUnitaDocObject, entryMatcher,
                                        oggettoInCoda, pigTipoObject, pigObject);
                            }
                            // se nello zip ho individuato un file da inviare a SACER, lo estraggo
                            // nella directory temporanea creata prima e ne memorizzo
                            // il path relativo
                            if (fudDaInviare != null) {
                                // NOTA: Ho bisogno che il file creato a partire dal file ZIP
                                // da Glassfish sia accessibile da Tomcat. Di default
                                // Files.createTempFile crea un file con i seguenti
                                // permessi: rw- --- ---
                                // gli attributi POSIX definiti in attributeFile permettono
                                // la lettura del file anche a "group" e "others" ("rw- r-- r--")
                                Path tmpOutPath = Files.createTempFile(tmpPathZipEsploso, "tf_", "", attributeFile);
                                FileOutputStream fos = new FileOutputStream(tmpOutPath.toFile());
                                this.leggiDaZipFile(fos, zipFile, tmpArchiveEntry);
                                fudDaInviare.setUrnFile(this.assemblaNomeFileRel(objRelativePath, tmpOutPath));
                            }
                        }
                    } else { // riga sbagliata -- l'oggetto deve essere distrutto.
                        // PING_PREPXML_FILE_003
                        // Nel file .zip dell''oggetto {0}, sono presenti cartelle non coerenti con il pattern
                        // che consente di identificare le unità documentarie
                        String dirZip = StringUtils.substring(tmpArchiveEntry.getName(), 0,
                                tmpArchiveEntry.getName().lastIndexOf("/"));
                        this.setError(MessaggiWSBundle.PING_PREPXML_FILE_003, MessaggiWSBundle
                                .getString(MessaggiWSBundle.PING_PREPXML_FILE_003, pigObject.getCdKeyObject(), dirZip),
                                oggettoInCoda);
                    }

                }
                // MAC #25499 - Gestione zip contenenti solo cartelle vuote
                if (isZipEmpty) {
                    this.setError(MessaggiWSBundle.PING_PREPXML_FILE_021,
                            MessaggiWSBundle.getString(MessaggiWSBundle.PING_PREPXML_FILE_021,
                                    pigObject.getCdKeyObject(),
                                    oggettoInCoda.getListaFileObjectExt().get(0).getUrnFile()),
                            oggettoInCoda);
                }

                log.info("PigObject {}", pigObject.getCdKeyObject());
                log.info("In questo file ZIP sono state dichiarate {} Unità doc", tmpInsiemeUd.size());
            } catch (ValidationException | MarshalException | XMLStreamException e) {
                log.error("Errore nell'unmarshalling dell'xml contenuto nello zip : {}",
                        ExceptionUtils.getRootCauseMessage(e), e);
                this.setError(
                        MessaggiWSBundle.PING_PREPXML_FILE_017, MessaggiWSBundle
                                .getString(MessaggiWSBundle.PING_PREPXML_FILE_017, tmpChiaveUdDirectory.toString()),
                        oggettoInCoda);
            } catch (IOException e) {
                // il file ZIP è rotto -- l'oggetto deve essere distrutto.
                // PING_PREPXML_FILE_010
                log.error("Errore di I/O nella lettura dello zip", e);
                this.setError(MessaggiWSBundle.PING_PREPXML_FILE_010,
                        MessaggiWSBundle.getString(MessaggiWSBundle.PING_PREPXML_FILE_010, pigObject.getCdKeyObject(),
                                ExceptionUtils.getRootCauseMessage(e)),
                        oggettoInCoda);
            } catch (Exception e) {
                log.error("Errore nell'unmarshalling dell'xml contenuto nello zip : "
                        + ExceptionUtils.getRootCauseMessage(e), e);
                this.setError(
                        MessaggiWSBundle.PING_PREPXML_FILE_017, MessaggiWSBundle
                                .getString(MessaggiWSBundle.PING_PREPXML_FILE_017, tmpChiaveUdDirectory.toString()),
                        oggettoInCoda);
            }

            /*
             * loop su tutte le UD create dal precedente passaggio per effettuare i test di coerenza. Ovviamente il loop
             * è distinto per i 2 casi di ZIP con e senza XML
             */
            if (oggettoInCoda.getSeverity() != IRispostaWS.SeverityEnum.ERROR) {
                this.verificaCoerenzaUDVersZip(mappaUnitaDocObject, mappaUDEscluse, oggettoInCoda, pigObject);
            }

            /*
             * Se sono ancora vivo verifico se esiste una precedente lista di unità doc, in questo caso la confronto con
             * quella dichiarata nel file zip per verificare se sono uguali (devono essere composte dalle stesse UD -
             * reg+anno+num, non necessariamente uguali come composizione)
             */
            if (oggettoInCoda.getSeverity() != IRispostaWS.SeverityEnum.ERROR) {
                /*
                 * MEV#14100 Se per l’oggetto è presente una sessione antecedente alla corrente (quella più recente) e
                 * tale sessione si è chiusa con stato diverso da ANNULLATO (vedi vista PIG_V_CHK_SES_PREC_NOT_ANNUL)
                 */
                if (controlliPrepXml.checkPigVChkSesPrecNotAnnul(new BigDecimal(pigObject.getIdObject()))) {
                    this.verificaCoerenzaVersamentoPrec(tmpInsiemeUd, oggettoInCoda, pigObject);
                }
            }
            //
            if (oggettoInCoda.getSeverity() != IRispostaWS.SeverityEnum.ERROR) {
                int sizeUnitaDocObjects = tmpInsiemeUd.size();
                log.debug(
                        "Verifico che il numero di unità documentarie calcolate corrisponda a quelle attese all'interno dell'oggetto");
                if (pigObject.getNiUnitaDocAttese() != null
                        && pigObject.getNiUnitaDocAttese().intValue() != sizeUnitaDocObjects) {
                    this.setError(MessaggiWSBundle.PING_PREPXML_FILE_013, MessaggiWSBundle.getString(
                            MessaggiWSBundle.PING_PREPXML_FILE_013, pigObject.getCdKeyObject()), oggettoInCoda);
                }
            }
        } else {
            throw new ParerInternalError(
                    "PIG_FILE_OBJECT non può essere vuota o avere più di un elemento in questo punto.");
        }
    }

    private void leggiDaZipFile(OutputStream os, ZipFile zipfile, ZipEntry entry) throws IOException {
        final int CNST_BUFFER = 1024 * 512;
        BufferedOutputStream dest = new BufferedOutputStream(os, CNST_BUFFER);
        BufferedInputStream zis = new BufferedInputStream(zipfile.getInputStream(entry));
        int count;
        byte[] data = new byte[CNST_BUFFER];
        try {
            while ((count = zis.read(data, 0, CNST_BUFFER)) != -1) {
                dest.write(data, 0, count);
            }
        } finally {
            IOUtils.closeQuietly(zis);
            IOUtils.closeQuietly(dest);
        }
    }

    private String assemblaNomeFileRel(String directoryBase, Path filePath) {
        int tmpCnt = filePath.getNameCount();
        return directoryBase + filePath.subpath(tmpCnt - 2, tmpCnt);
    }

    private FileUnitaDoc gestEntryZipConXml(ZipEntry archiveEntry, UnitaDocObject unitaDocObject, Matcher matcher) {
        if (matcher.group(CNST_FILE).equalsIgnoreCase(matcher.group(CNST_DIRECTORY) + ".xml")) {
            unitaDocObject.setUrnUDXml(archiveEntry.getName());
            // se il file si chiama come la directory più l'estensione .xml, devo leggerne il contenuto.
            // potrei farlo qui ma risulta più comodo leggerlo nel ciclo principale
            // dove ho l'istanza della classe ZipFile da cui ricavare l'inputstream
            log.debug("nome file = nome cartella: devo leggerlo");
            return null;
        } else {
            // se il file ha nome diverso da quello della directory in cui è contenuto, deve
            // essere inviato a SACER. Per ottimizzare la velocità di versamento lo estraggo
            // in un file temporaneo, in una directory temporanea.
            // Anche in questo caso caso risulta più comodo leggerlo nel ciclo principale
            if (log.isDebugEnabled()) {
                log.debug("nome file != nome cartella: devo inviarlo a sacer. file: {}", matcher.group(CNST_FILE));
            }
            FileUnitaDoc tmpFileUnitaDoc = new FileUnitaDoc();
            // ID file = nome file, dovrò essere identico nell'xml di versamento
            tmpFileUnitaDoc.setIdFile(matcher.group(CNST_FILE));
            // conservo l'URL originale del file nell'archivio, che manderò a titolo informativo a SACER
            tmpFileUnitaDoc.setUrnFileInZip(archiveEntry.getName());
            // aggiungo il file appena individuato
            unitaDocObject.getListaFileUnitaDoc().add(tmpFileUnitaDoc);
            // incrementa dimensione UD
            unitaDocObject.setSizeInByte(unitaDocObject.getSizeInByte() + archiveEntry.getSize());
            // restituisco l'istanza di FileUnitaDoc, su cui scrivere l'URN del file temporaneo
            return tmpFileUnitaDoc;
        }
    }

    private FileUnitaDoc gestEntryZipNoXml(ZipEntry archiveEntry, UnitaDocObject unitaDocObject, Matcher matcher,
            OggettoInCoda oggettoInCoda, PigTipoObject pigTipoObject, PigObject pigObject) {
        /*
         * RegExp ^([^/\^]+)\^([^/]+)$
         *
         */

        final int CNST_FILE_PREFIX = 1;
        final int CNST_FILE_SUFFIX = 2;

        String regExp = "^([^/\\^]+)\\^([^/]+)$";
        Pattern validEntryPattrn = Pattern.compile(regExp);
        Matcher entryMatcher = validEntryPattrn.matcher(matcher.group(CNST_FILE));
        if (entryMatcher.find() && entryMatcher.groupCount() == 2) { // riga buona
            String tmpNomeFile = entryMatcher.group(CNST_FILE_SUFFIX);
            if (log.isDebugEnabled()) {
                log.debug("Prefisso {} : Suffisso  {}", entryMatcher.group(CNST_FILE_PREFIX), tmpNomeFile);
            }

            PigTipoFileObject tmpTipoFileObject = controlliPrepXml
                    .getPigTipoFileObj(entryMatcher.group(CNST_FILE_PREFIX), pigTipoObject.getIdTipoObject());

            if (tmpTipoFileObject != null) {
                // incremento l'ultimo ID file assegnato per l'UD
                unitaDocObject.setLastAssignedID(unitaDocObject.getLastAssignedID() + 1);
                //
                if (tmpTipoFileObject.getTiDocSacer().equals(Constants.DocTypeEnum.PRINCIPALE.name())) {
                    // incremento il contatore di documenti PRINCIPALE nell'UD.
                    // in seguito verificherò che questo numero sia uguale a 1
                    unitaDocObject.setTotaleDocPrincipali(unitaDocObject.getTotaleDocPrincipali() + 1);
                }
                //
                FileUnitaDoc tmpFileUnitaDoc = new FileUnitaDoc();
                // ID file calcolato in automatico, nell'xml dovrò chiamarsi IDnum
                tmpFileUnitaDoc.setIdFile("ID" + unitaDocObject.getLastAssignedID());
                tmpFileUnitaDoc.setUrnFileInZip(archiveEntry.getName());
                tmpFileUnitaDoc.setRifPigTipoFileObject(tmpTipoFileObject);
                tmpFileUnitaDoc.setNomeFile(tmpNomeFile);
                // aggiungo il file appena individuato
                unitaDocObject.getListaFileUnitaDoc().add(tmpFileUnitaDoc);
                // incrementa dimensione UD
                unitaDocObject.setSizeInByte(unitaDocObject.getSizeInByte() + archiveEntry.getSize());
                // se il file va bene, deve
                // essere inviato a SACER. Per ottimizzare la velocità di versamento lo estraggo
                // in un file temporaneo, in una directory temporanea.
                // Anche in questo caso caso risulta più comodo leggerlo nel ciclo principale
                return tmpFileUnitaDoc;
            } else {
                // PING_PREPXML_FILE_005
                this.setError(MessaggiWSBundle.PING_PREPXML_FILE_005,
                        MessaggiWSBundle.getString(MessaggiWSBundle.PING_PREPXML_FILE_005, pigObject.getCdKeyObject(),
                                matcher.group(CNST_DIRECTORY), matcher.group(CNST_FILE)),
                        oggettoInCoda);
            }
        } else {
            // PING_PREPXML_FILE_004
            // Nel file .zip dell''oggetto {0}, per l''unità documentaria {1} sono presenti file non coerenti
            // con il pattern che consente di identificare il tipo di file
            this.setError(MessaggiWSBundle.PING_PREPXML_FILE_004,
                    MessaggiWSBundle.getString(MessaggiWSBundle.PING_PREPXML_FILE_004, pigObject.getCdKeyObject(),
                            matcher.group(CNST_DIRECTORY)),
                    oggettoInCoda);
        }
        return null;
    }

    private void verificaCoerenzaUDVersZip(Map<String, UnitaDocObject> mappaUnitaDocObject,
            Map<String, Chiave> mappaUDEscluse, OggettoInCoda oggettoInCoda, PigObject pigObject) {
        if (oggettoInCoda.getTipoVersamento() == Constants.TipoVersamento.ZIP_CON_XML_SACER) {
            /*
             * caso ZIP CON XML SACER loop su tutte le UD create dal precedente passaggio per effettuare i test di
             * coerenza.
             */
            for (UnitaDocObject unitaDocObject : oggettoInCoda.getListaUnitaDocObject()) {
                if (unitaDocObject.getUnitaDocumentariaXml() == null
                        || unitaDocObject.getUnitaDocumentariaXml().isEmpty()) {
                    // PING_PREPXML_FILE_002
                    // Nel file .zip dell''oggetto {0}, per l''unità documentaria {1} non è presente
                    // il file xml con cui effettuare il versamento a SACER
                    this.setError(MessaggiWSBundle.PING_PREPXML_FILE_002,
                            MessaggiWSBundle.getString(MessaggiWSBundle.PING_PREPXML_FILE_002,
                                    pigObject.getCdKeyObject(), unitaDocObject.getChiaveCompatta()),
                            oggettoInCoda);
                    //
                    break;
                }
            }
        } else {
            /*
             * caso ZIP NO XML loop su tutte le UD create dal precedente passaggio per effettuare i test di coerenza.
             */
            for (UnitaDocObject unitaDocObject : oggettoInCoda.getListaUnitaDocObject()) {
                if (unitaDocObject.getTotaleDocPrincipali() != 1) {
                    // PING_PREPXML_FILE_006
                    // Nel file .zip dell''oggetto {0}, per l''unità documentaria {1} è presente più di un file
                    // il cui tipo definisce il documento principale,
                    // oppure non è presente il file il cui tipo definisce il documento principale
                    this.setError(MessaggiWSBundle.PING_PREPXML_FILE_006,
                            MessaggiWSBundle.getString(MessaggiWSBundle.PING_PREPXML_FILE_006,
                                    pigObject.getCdKeyObject(), unitaDocObject.getChiaveCompatta()),
                            oggettoInCoda);
                    //
                    break;
                }
            }
            /*
             * in seguito alla verifica di coerenza delle UD, verifico se è stato versato un XML per l'oggetto. Nel
             * caso, verifico che tutte le UD dichiarate nell'XML abbiano una corrispondente UD nello zip, inoltre in
             * ogni UD dichiarata nell'XML i tipi file descritti devono avere almeno un file definito nello zip
             */
            if (oggettoInCoda.getSeverity() != IRispostaWS.SeverityEnum.ERROR
                    && oggettoInCoda.getParsedListaUnitaDoc() != null
                    && oggettoInCoda.getParsedListaUnitaDoc().getUnitaDocumentaria() != null
                    && (!oggettoInCoda.getParsedListaUnitaDoc().getUnitaDocumentaria().isEmpty())) {
                for (UnitaDocumentariaType tmpDocumentariaXml : oggettoInCoda.getParsedListaUnitaDoc()
                        .getUnitaDocumentaria()) {
                    String tmpChiaveOut = tmpDocumentariaXml.getChiave().getTipoRegistro() + "^"
                            + tmpDocumentariaXml.getChiave().getAnno() + "^"
                            + tmpDocumentariaXml.getChiave().getNumero();

                    String tmpUDOKey = this.trovaUDOFromChiaveInZip(tmpDocumentariaXml.getChiave(), oggettoInCoda);
                    if (tmpUDOKey != null) { // ho trovato l'UD nella lista di quelle "buone"?
                        /*
                         * verifico se per tutti i tipi file definiti nell'XML dell'UD c'è almeno un file per l'U. Doc
                         * Obj a questi file impongo il tipo file. Se non trovo neanche un file segnalo l'errore.
                         */
                        String tmpTipoFileNonTrovato = this.impostaTipoFileinZip(mappaUnitaDocObject,
                                tmpDocumentariaXml, tmpUDOKey);
                        if (tmpTipoFileNonTrovato != null) {
                            // PING_PREPXML_FILE_008
                            // Nel file .zip dell''oggetto {0}, per l''unità documentaria {1} non è presente il file
                            // di di tipo {2} che è definito nel XML versato
                            this.setError(MessaggiWSBundle.PING_PREPXML_FILE_008,
                                    MessaggiWSBundle.getString(MessaggiWSBundle.PING_PREPXML_FILE_008,
                                            pigObject.getCdKeyObject(), tmpChiaveOut, tmpTipoFileNonTrovato),
                                    oggettoInCoda);
                            //
                            break;
                        }
                    } else {
                        // se non ho trovato l'UD definita nell'XML tra quelle "buone",
                        // faccio un ultimo tentativo e la cerco tra quelle escluse.
                        if (!this.trovaUDOFromChiaveInEscluseZip(mappaUDEscluse, tmpDocumentariaXml.getChiave())) {
                            // PING_PREPXML_FILE_007
                            // Nel file .zip dell''oggetto {0}, non è presente la cartella relativa
                            // all''unità documentaria {1} che è definita nel XML versato
                            this.setError(MessaggiWSBundle.PING_PREPXML_FILE_007,
                                    MessaggiWSBundle.getString(MessaggiWSBundle.PING_PREPXML_FILE_007,
                                            pigObject.getCdKeyObject(), tmpChiaveOut),
                                    oggettoInCoda);
                            //
                            break;
                        }
                    }
                }
            }
        }
    }

    private void verificaCoerenzaVersamentoPrec(Set<List<String>> tmpInsiemeUd, OggettoInCoda oggettoInCoda,
            PigObject pigObject) {
        long tmpQuantitaUd = pigObject.getPigUnitaDocObjects().size();
        if (tmpQuantitaUd == 0) {
            log.info("Non esiste un versamento precedente prodotto da questo pigObject");
            return;
        }
        //
        log.info("Esiste un versamento precedente, composto da {} Unità doc", tmpQuantitaUd);
        if (tmpQuantitaUd != tmpInsiemeUd.size()) {
            this.setError(MessaggiWSBundle.PING_PREPXML_FILE_011,
                    MessaggiWSBundle.getString(MessaggiWSBundle.PING_PREPXML_FILE_011, pigObject.getCdKeyObject()),
                    oggettoInCoda);
            return;
        }
        //
        for (PigUnitaDocObject tmpDocObject : pigObject.getPigUnitaDocObjects()) {
            String[] tmpArrP = new String[] { "", "", "" };
            tmpArrP[0] = tmpDocObject.getAaUnitaDocSacer().toString();
            tmpArrP[1] = tmpDocObject.getCdRegistroUnitaDocSacer();
            tmpArrP[2] = tmpDocObject.getCdKeyUnitaDocSacer();
            if (!tmpInsiemeUd.contains(Arrays.asList(tmpArrP))) {
                this.setError(MessaggiWSBundle.PING_PREPXML_FILE_012,
                        MessaggiWSBundle.getString(MessaggiWSBundle.PING_PREPXML_FILE_012, pigObject.getCdKeyObject()),
                        oggettoInCoda);
                return;
            }
        }

    }

    private String trovaUDOFromChiaveInZip(ChiaveType tmpChiave, OggettoInCoda oggettoInCoda) {
        for (UnitaDocObject unitaDocObject : oggettoInCoda.getListaUnitaDocObject()) {
            if (unitaDocObject.getChiaveUd().getAnno() == tmpChiave.getAnno()
                    && unitaDocObject.getChiaveUd().getNumero().equals(tmpChiave.getNumero())
                    && unitaDocObject.getChiaveUd().getRegistro().equals(tmpChiave.getTipoRegistro())) {
                return unitaDocObject.getChiaveCompatta();
            }
        }
        return null;
    }

    private boolean trovaUDOFromChiaveInEscluseZip(Map<String, Chiave> mappaUDEscluse, ChiaveType tmpChiave) {
        for (Chiave chiave : mappaUDEscluse.values()) {
            if (chiave.getAnno() == tmpChiave.getAnno() && chiave.getNumero().equals(tmpChiave.getNumero())
                    && chiave.getRegistro().equals(tmpChiave.getTipoRegistro())) {
                return true;
            }
        }
        return false;
    }

    private String impostaTipoFileinZip(Map<String, UnitaDocObject> mappaUnitaDocObject, UnitaDocumentariaType ud,
            String chiaveCompUD) {
        if (ud.getFiles() != null && ud.getFiles().getFile() != null && !ud.getFiles().getFile().isEmpty()) {
            for (FileType fileType : ud.getFiles().getFile()) {
                int conta = 0;

                for (FileUnitaDoc file : mappaUnitaDocObject.get(chiaveCompUD).getListaFileUnitaDoc()) {
                    if (file.getRifPigTipoFileObject().getNmTipoFileObject().equals(fileType.getTipoFile())) {
                        conta++;
                        file.setParsedFileType(fileType);
                    }
                }
                if (conta == 0) {
                    /*
                     * se non sono riuscito ad assegnare il tipo file ad alcun file, fermo l'operazione e restituisco il
                     * nome del tipo file non trovato
                     */
                    return fileType.getTipoFile();
                }
            }
        }
        return null;
    }

    /*
     * Questo metodo viene invocato solo nel caso di ZIP_CON_XML ed effettua il parsing del SIP presente nel file ZIP
     * per caricare la struttura dichiarata
     *
     * @param tmpUnitaDocObject
     *
     * @return istanza di UnitaDocumentaria corrispondente all'XML del SIP
     *
     * @throws JAXBException
     */
    private it.eng.parer.ws.xml.versReq.UnitaDocumentaria caricaStruttAbilDaXmlSIP(UnitaDocObject tmpUnitaDocObject)
            throws JAXBException {
        String unitaDocumentariaXml = tmpUnitaDocObject.getUnitaDocumentariaXml();
        StringReader reader = new StringReader(unitaDocumentariaXml);
        Unmarshaller unmarshaller = xmlContextCache.getVersReqCtxforUD().createUnmarshaller();
        unmarshaller.setSchema(xmlContextCache.getSchemaOfVersReq());
        it.eng.parer.ws.xml.versReq.UnitaDocumentaria ud = (it.eng.parer.ws.xml.versReq.UnitaDocumentaria) unmarshaller
                .unmarshal(reader);

        BigDecimal idOrganizIam = corVersHelper.getStrutturaAbilitata(ud.getIntestazione().getVersatore().getAmbiente(),
                ud.getIntestazione().getVersatore().getEnte(), ud.getIntestazione().getVersatore().getStruttura(),
                ud.getIntestazione().getVersatore().getUserID());

        tmpUnitaDocObject.setIdOrganizSacer(idOrganizIam);
        tmpUnitaDocObject.setNmAmbienteSacer(ud.getIntestazione().getVersatore().getAmbiente());
        tmpUnitaDocObject.setNmEnteSacer(ud.getIntestazione().getVersatore().getEnte());
        tmpUnitaDocObject.setNmStrutSacer(ud.getIntestazione().getVersatore().getStruttura());
        tmpUnitaDocObject.setNmUserIdSacer(ud.getIntestazione().getVersatore().getUserID());
        //
        if (ud.getConfigurazione().isSimulaSalvataggioDatiInDB() == null) {
            ud.getConfigurazione().setSimulaSalvataggioDatiInDB(Boolean.FALSE);
        }
        tmpUnitaDocObject.setSimulaVersamento(ud.getConfigurazione().isSimulaSalvataggioDatiInDB());
        //
        tmpUnitaDocObject.setVersioneWsVersamento(ud.getIntestazione().getVersione());
        return ud;
    }

    private boolean verificaCoerenzaChiaveXmlSIP(it.eng.parer.ws.xml.versReq.UnitaDocumentaria ud, Chiave tmpChiave,
            String directory, OggettoInCoda oggettoInCoda, PigObject pigObject) {
        // MEV#14555 - Prepara XML: nei controlli di coerenza considerare la presenza di caratteri non ammessi
        String numeroChiaveCalcolato = it.eng.sacerasi.web.util.Utils
                .normalizzaNomeFile(ud.getIntestazione().getChiave().getNumero());
        String registroChiaveCalcolato = it.eng.sacerasi.web.util.Utils
                .normalizzaNomeFile(ud.getIntestazione().getChiave().getTipoRegistro());
        if ((ud.getIntestazione().getChiave().getAnno() != tmpChiave.getAnno())
                // Confronta la chiave ricavata dal nome del file da quella ricalcolata con i dati nell'XML
                || (!numeroChiaveCalcolato.equals(tmpChiave.getNumero()))
                || (!registroChiaveCalcolato.equals(tmpChiave.getRegistro()))) {
            // se la chiave espressa dalla directory non coincide con quella espressa nel SIP versato,
            // il versamento viene distrutto
            this.setError(
                    MessaggiWSBundle.PING_PREPXML_FILE_018, MessaggiWSBundle
                            .getString(MessaggiWSBundle.PING_PREPXML_FILE_018, pigObject.getCdKeyObject(), directory),
                    oggettoInCoda);
            return false;
        }
        return true;
    }

    private void setError(String errCode, String errMess, OggettoInCoda oggettoInCoda) {
        oggettoInCoda.setSeverity(IRispostaWS.SeverityEnum.ERROR);
        oggettoInCoda.setErrorCode(errCode);
        oggettoInCoda.setErrorMessage(errMess);
        log.info(errCode);
        log.info(errMess);
    }

    // MEV25602
    private void copiaDaOS(OggettoInCoda oggettoInCoda) throws ParerInternalError, ObjectStorageException {
        if (oggettoInCoda.getListaFileObjectExt().size() == 1) {
            FileObjectExt fileObject = oggettoInCoda.getListaFileObjectExt().get(0);
            if (fileObject.getNmBucket() != null && fileObject.getCdKeyFile() != null) {

                BackendStorage backend = salvataggioBackendHelper.getBackend(fileObject.getIdBackend());
                ObjectStorageBackend config = salvataggioBackendHelper
                        .getObjectStorageConfigurationForVersamento(backend.getBackendName(), fileObject.getNmBucket());

                ResponseInputStream<GetObjectResponse> ogg = salvataggioBackendHelper.getObject(config,
                        fileObject.getCdKeyFile());

                try {
                    File destinationFile = new File(fileObject.getUrnFile());
                    FileUtils.copyInputStreamToFile(ogg, destinationFile);
                } catch (IOException ex) {
                    log.error("Errore in fase di copia verso object storage", ex);
                }
            }
        } else {
            throw new ParerInternalError(
                    "PIG_FILE_OBJECT non può essere vuota o avere più di un elemento in questo punto.");
        }
    }
}
