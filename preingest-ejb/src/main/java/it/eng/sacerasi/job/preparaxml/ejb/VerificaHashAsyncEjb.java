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

/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
*/
package it.eng.sacerasi.job.preparaxml.ejb;

import it.eng.parer.objectstorage.dto.BackendStorage;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.parer.objectstorage.dto.ObjectStorageBackend;
import it.eng.parer.objectstorage.exceptions.ObjectStorageException;
import it.eng.parer.objectstorage.helper.SalvataggioBackendHelper;
import it.eng.sacerasi.common.Constants.TipiEncBinari;
import it.eng.sacerasi.common.Constants.TipiHash;
import it.eng.sacerasi.entity.PigFileObject;
import it.eng.sacerasi.entity.PigFileObjectStorage;
import it.eng.sacerasi.entity.PigObject;
import it.eng.sacerasi.entity.PigTipoObject;
import it.eng.sacerasi.exception.ParerInternalError;
import it.eng.sacerasi.job.preparaxml.dto.FileObjectExt;
import it.eng.sacerasi.job.preparaxml.dto.OggettoInCoda;
import it.eng.sacerasi.messages.MessaggiWSBundle;
import it.eng.sacerasi.web.helper.ConfigurationHelper;
import it.eng.sacerasi.ws.dto.IRispostaWS;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

@Stateless(mappedName = "VerificaHashAsyncEjb")
@LocalBean
public class VerificaHashAsyncEjb {

    private static final Logger log = LoggerFactory.getLogger(VerificaHashAsyncEjb.class);
    // EJB per verifiche sul DB
    @EJB
    ControlliPrepXml controlliPrepXml;
    @EJB
    ConfigurationHelper configurationHelper = null;

    @EJB
    SalvataggioBackendHelper salvataggioBackendHelper;

    public void verificaHash(String rootDirectory, OggettoInCoda oggetto)
            throws ParerInternalError, ObjectStorageException {
        PigObject pigObject;
        PigTipoObject pigTipoObject;
        OggettoInCoda oggettoInCoda = oggetto;
        pigObject = oggettoInCoda.getRifPigObject();
        pigTipoObject = pigObject.getPigTipoObject();
        String pathVersatore = pigObject.getPigVer().getDsPathInputFtp();

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
                // default hexBinary
                tmpFileObjectExt.setEncodingFile(StringUtils.isNotBlank(tmpFileObject.getCdEncodingHashFileVers())
                        ? tmpFileObject.getCdEncodingHashFileVers() : TipiEncBinari.HEX_BINARY.descrivi());

                // MEV 24717 - memorizzo gli eventuali parametri per recuperare l'oggetto da Object Storage
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

        log.debug("verifica hash...");
        // verifica hash
        if (str2Bool(pigTipoObject.getFlContrHash())) {
            log.debug("verifica hash da fare.");
            boolean tmpVerificaOk;
            for (FileObjectExt tmpFileObjectExt : oggettoInCoda.getListaFileObjectExt()) {
                tmpVerificaOk = this.verificaHash(tmpFileObjectExt);
                if (!tmpVerificaOk) {
                    // MAC 28465 -Sostituito PING-PREPXML-FILE-001 con nuovo errore PING_VERHASH_FILE_001
                    // Il file {0} dell''oggettoInCoda {1} non è integro
                    this.setError(MessaggiWSBundle.PING_VERHASH_FILE_001,
                            MessaggiWSBundle.getString(MessaggiWSBundle.PING_VERHASH_FILE_001,
                                    tmpFileObjectExt.getRifPigFileObject().getNmFileObject(),
                                    pigObject.getCdKeyObject()),
                            oggettoInCoda);
                    break;
                }
            }
        }
    }

    private boolean verificaHash(FileObjectExt file) throws ParerInternalError, ObjectStorageException {
        boolean tmpret = false;
        String tmpHash;

        log.info("verificaHash - inizio");
        if (file.getRifPigFileObject().getDsHashFileVers() != null
                && !file.getRifPigFileObject().getDsHashFileVers().isEmpty()) {
            try {

                // MEV 24717 - se il servizio di object storage è attivo e il file
                // è conservato su OS, non prendere lo stream da disco
                if (file.getNmBucket() != null && file.getCdKeyFile() != null) {
                    tmpHash = this.calculateHashFromOS(file, file.getTipoHashFile());
                } else {
                    tmpHash = this.calculateHash(file.getUrnFile(), file.getTipoHashFile());
                }

                /*
                 * nota per me: dovrei verificare che i parametri relativi all'algoritmo ed alla codifica coincidano con
                 * SHA ed hexBinary. Per ora ignoro la cosa e confronto l'hash fornito dal versatore con un hash SHA ed
                 * hexBinary del file... se non coincide rilevo l'errore.
                 */
                file.setTipoHashFile(file.getTipoHashFile());
                file.setEncodingFile(file.getEncodingFile());
                file.setHashFile(tmpHash);

                log.debug("hash calcolato: {} algoritmo {}", file.getHashFile(), file.getTipoHashFile());

                if (file.getHashFile().equalsIgnoreCase(file.getRifPigFileObject().getDsHashFileVers())) {
                    tmpret = true;
                } else {
                    log.info("Errore nella verifica dell'hash ");
                    log.info("Oggetto .........{}", file.getUrnFile());
                    log.info("Hash dichiarato .{}", file.getRifPigFileObject().getDsHashFileVers());
                    log.info("Hash calcolato...{}", file.getHashFile());
                }
            } catch (IOException | NoSuchAlgorithmException ex) {
                throw new ParerInternalError(ex);
            }
        }
        log.info("verificaHash - fine");
        return tmpret;
    }

    /*
     * calcolo l'hash in streaming, lento ma mi tutela da eventuali out of memory
     */
    private String calculateHash(String fileUrn, String tipoHash) throws NoSuchAlgorithmException, IOException {
        MessageDigest md = MessageDigest.getInstance(tipoHash);
        DigestInputStream dis = null;
        int ch;
        final int BUFFER_SIZE = 100 * 1024 * 1024; // 100 MB

        try (InputStream is = new FileInputStream(fileUrn);) {
            log.debug("Provider {}", md.getProvider());
            dis = new DigestInputStream(is, md);
            byte[] buffer = new byte[BUFFER_SIZE];
            while ((ch = dis.read(buffer)) != -1) {
                log.trace("Letti {} bytes", ch);
            }
        } finally {
            IOUtils.closeQuietly(dis);
        }

        byte[] pwdHash = md.digest();
        return toHexBinary(pwdHash);
    }

    /*
     * calcolo l'hash in streaming, lento ma mi tutela da eventuali out of memory
     */
    private String calculateHashFromOS(FileObjectExt file, String tipoHash)
            throws NoSuchAlgorithmException, IOException, ObjectStorageException {
        MessageDigest md = MessageDigest.getInstance(tipoHash);
        int ch;
        final int BUFFER_SIZE = 100 * 1024 * 1024; // 100 MB

        BackendStorage backend = salvataggioBackendHelper.getBackend(file.getIdBackend());
        ObjectStorageBackend config = salvataggioBackendHelper
                .getObjectStorageConfigurationForVersamento(backend.getBackendName(), file.getNmBucket());

        ResponseInputStream<GetObjectResponse> ogg = salvataggioBackendHelper.getObject(config, file.getCdKeyFile());

        DigestInputStream dis = new DigestInputStream(ogg, md);
        log.debug("Provider {}", md.getProvider());

        byte[] buffer = new byte[BUFFER_SIZE];
        while ((ch = dis.read(buffer)) != -1) {
            log.trace("Letti {} bytes", ch);
        }

        byte[] pwdHash = md.digest();
        return toHexBinary(pwdHash);
    }

    private String toHexBinary(byte[] dati) {
        if (dati != null) {
            StringBuilder sb = new StringBuilder();
            for (byte b : dati) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } else {
            return "";
        }
    }

    private boolean str2Bool(String stringa) {
        boolean tmpRet = false;
        if (stringa != null && stringa.equals("1")) {
            tmpRet = true;
        }
        return tmpRet;
    }

    private void setError(String errCode, String errMess, OggettoInCoda oggettoInCoda) {
        oggettoInCoda.setSeverity(IRispostaWS.SeverityEnum.ERROR);
        oggettoInCoda.setErrorCode(errCode);
        oggettoInCoda.setErrorMessage(errMess);
        log.info(errCode);
        log.info(errMess);
    }
}
