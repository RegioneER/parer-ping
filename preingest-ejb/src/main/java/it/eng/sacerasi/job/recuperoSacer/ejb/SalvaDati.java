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

package it.eng.sacerasi.job.recuperoSacer.ejb;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.resource.ResourceException;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xadisk.connector.outbound.XADiskConnection;
import org.xadisk.connector.outbound.XADiskConnectionFactory;
import org.xadisk.filesystem.exceptions.DirectoryNotEmptyException;
import org.xadisk.filesystem.exceptions.FileAlreadyExistsException;
import org.xadisk.filesystem.exceptions.FileNotExistsException;
import org.xadisk.filesystem.exceptions.FileUnderUseException;
import org.xadisk.filesystem.exceptions.InsufficientPermissionOnFileException;
import org.xadisk.filesystem.exceptions.LockingFailedException;
import org.xadisk.filesystem.exceptions.NoTransactionAssociatedException;

import it.eng.sacerasi.common.Constants;
import it.eng.sacerasi.common.ejb.CommonDb;
import it.eng.sacerasi.entity.PigSessioneRecup;
import it.eng.sacerasi.entity.PigUnitaDocObject;
import it.eng.sacerasi.exception.ParerInternalError;
import it.eng.sacerasi.web.helper.ConfigurationHelper;

/**
 *
 * @author Bonora_L
 */
@Stateless(mappedName = "SalvaDati")
@LocalBean
@TransactionManagement(TransactionManagementType.CONTAINER)
public class SalvaDati {

    private static final Logger log = LoggerFactory.getLogger(SalvaDati.class);

    //
    @PersistenceContext(unitName = "SacerAsiJPA")
    private EntityManager entityManager;
    //
    @Resource(mappedName = "jca/xadiskLocal")
    private XADiskConnectionFactory xadCf;
    @EJB
    private CommonDb commonDb;
    @EJB
    private ConfigurationHelper configurationHelper;
    private static final String FILE_KEY_SEPARATOR = "-";

    public void elabora(PigSessioneRecup sessioneRec, PigUnitaDocObject unitaDocObj) throws ParerInternalError {
        // I file da rinominare sono quelli creati da Sacer con le due chiamate di recupero eseguite nel metodo sopra
        // String rootFtp = commonDb.getRootFtpParam();
        // // String ftpOutput = rootFtp.concat(sessioneRec.getPigVer().getDsPathOutputFtp());
        // BigDecimal idAmbienteVers = BigDecimal.valueOf(
        // sessioneRec.getPigObject().getPigTipoObject().getPigVer().getPigAmbienteVer().getIdAmbienteVers());
        // BigDecimal idVers =
        // BigDecimal.valueOf(sessioneRec.getPigObject().getPigTipoObject().getPigVer().getIdVers());
        // BigDecimal idTipoObject =
        // BigDecimal.valueOf(sessioneRec.getPigObject().getPigTipoObject().getIdTipoObject());
        String ftpOutput = sessioneRec.getPigObject().getPigTipoObject().getPigVer().getDsPathOutputFtp();
        // String ftpOutput = rootFtp.concat(configurationHelper.getValoreParamApplic("DS_PATH_OUTPUT_FTP",
        // idAmbienteVers,
        // idVers, idTipoObject, Constants.TipoPigVGetValAppart.TIPOOBJECT));
        String fileSuffix = unitaDocObj.getCdRegistroUnitaDocSacer().concat(FILE_KEY_SEPARATOR)
                .concat(unitaDocObj.getAaUnitaDocSacer().toString().concat(FILE_KEY_SEPARATOR))
                .concat(unitaDocObj.getCdKeyUnitaDocSacer().concat(Constants.ZIP_EXTENSION));
        // Istanzio il file costruendo il pathname UD_<registro>-<anno>-<numero>.zip
        File fileUd = new File(ftpOutput.concat(Constants.UD_FILE_PREFIX_SACER).concat(fileSuffix));
        // Se esiste (e dovrebbe esistere), lo rinomino in UD_<CdKeyObject>.zip
        File fileUdDest = new File(ftpOutput.concat(Constants.UD_FILE_PREFIX_PREINGEST)
                .concat(sessioneRec.getPigObject().getCdKeyObject()).concat(Constants.ZIP_EXTENSION));
        // Istanzio il file costruendo il pathname PC_<registro>-<anno>-<numero>.zip
        File filePc = new File(ftpOutput.concat(Constants.PC_FILE_PREFIX_SACER).concat(fileSuffix));
        // Se esiste (e dovrebbe esistere), lo rinomino in PC_<CdKeyObject>.zip
        File filePcDest = new File(ftpOutput.concat(Constants.PC_FILE_PREFIX_PREINGEST)
                .concat(sessioneRec.getPigObject().getCdKeyObject()).concat(Constants.ZIP_EXTENSION));

        if (this.modificaSessione(sessioneRec, Constants.StatoSessioneRecup.RECUPERATO, null, null, null)
                && this.rinominaFiles(fileUd, fileUdDest, filePc, filePcDest)) {
            log.info("Commit effettuato");
        } else {
            log.info("Rollback effettuato");
            throw new ParerInternalError("Errore grave nella procedura elabora in recuperoOggettiSacer");
        }
    }

    public void elaboraErrore(PigSessioneRecup sessioneRec, Constants.ServizioRecupero tipoServizio,
            PigUnitaDocObject unitaDocObj, String codErr, String dsErr) throws ParerInternalError {
        File fileUd = null;
        if (tipoServizio == Constants.ServizioRecupero.RECUPERO_PC_SERVICE) {
            // // Il file da eliminare è quello creato da Sacer con l'ultima chiamata di recupero Ud
            // String rootFtp = commonDb.getRootFtpParam();
            // // String ftpOutput = rootFtp.concat(sessioneRec.getPigVer().getDsPathOutputFtp());
            // BigDecimal idAmbienteVers = BigDecimal.valueOf(
            // sessioneRec.getPigObject().getPigTipoObject().getPigVer().getPigAmbienteVer().getIdAmbienteVers());
            // BigDecimal idVers = BigDecimal
            // .valueOf(sessioneRec.getPigObject().getPigTipoObject().getPigVer().getIdVers());
            // BigDecimal idTipoObject = BigDecimal
            // .valueOf(sessioneRec.getPigObject().getPigTipoObject().getIdTipoObject());
            // String ftpOutput = rootFtp.concat(configurationHelper.getValoreParamApplic("DS_PATH_OUTPUT_FTP",
            // idAmbienteVers, idVers, idTipoObject, Constants.TipoPigVGetValAppart.TIPOOBJECT));
            String ftpOutput = sessioneRec.getPigObject().getPigTipoObject().getPigVer().getDsPathOutputFtp();
            String fileSuffix = unitaDocObj.getCdRegistroUnitaDocSacer().concat(FILE_KEY_SEPARATOR)
                    .concat(unitaDocObj.getAaUnitaDocSacer().toString().concat(FILE_KEY_SEPARATOR))
                    .concat(unitaDocObj.getCdKeyUnitaDocSacer().concat(Constants.ZIP_EXTENSION));
            // Istanzio il file costruendo il pathname UD_<registro>-<anno>-<numero>.zip
            fileUd = new File(ftpOutput.concat(Constants.UD_FILE_PREFIX_SACER).concat(fileSuffix));
            // Se esiste (e dovrebbe esistere), lo elimino
        }

        // Eseguo il commit dell'operazione in due casi:
        // - O il servizio è RECUPERO_UD e allora devo solo modificare la sessione
        // - O il servizio è RECUPERO_PC e oltre alla modifica sessione va anche eliminato il file UD creato
        // precedentemente
        if ((tipoServizio == Constants.ServizioRecupero.RECUPERO_UD_SERVICE && this.modificaSessione(sessioneRec,
                Constants.StatoSessioneRecup.CHIUSO_ERR_RECUPERATO, Calendar.getInstance().getTime(), codErr, dsErr))
                || (tipoServizio == Constants.ServizioRecupero.RECUPERO_PC_SERVICE
                        && this.modificaSessione(sessioneRec, Constants.StatoSessioneRecup.CHIUSO_ERR_RECUPERATO,
                                Calendar.getInstance().getTime(), codErr, dsErr)
                        && fileUd != null && this.rimuoviFile(fileUd))) {
            log.info("Commit effettuato");
        } else {
            log.info("Rollback effettuato");
            throw new ParerInternalError("Errore grave nella procedura elaboraErrore in recuperoOggettiSacer");
        }

    }

    private boolean modificaSessione(PigSessioneRecup sessioneRec, Constants.StatoSessioneRecup stato, Date dtChiusura,
            String codErr, String dsErr) {
        boolean tmpRet;
        try {
            PigSessioneRecup tmp = entityManager.find(PigSessioneRecup.class, sessioneRec.getIdSessioneRecup());
            tmp.setTiStato(stato.name());
            if (dtChiusura != null) {
                tmp.setDtChiusura(dtChiusura);
            }
            if (StringUtils.isNotBlank(codErr)) {
                tmp.setCdErr(codErr);
            }
            if (StringUtils.isNotBlank(dsErr)) {
                tmp.setDlErr(dsErr);
            }
            tmpRet = true;
        } catch (Exception e) {
            log.error("Errore nella persistenza dei dati di errore ", e);
            tmpRet = false;
        }
        return tmpRet;
    }

    private boolean rinominaFiles(File fileUd, File fileUdDest, File filePc, File filePcDest) {
        boolean tmpRet;
        XADiskConnection xadConn = null;

        try {
            // Apre la connessione
            xadConn = xadCf.getConnection();
            // Rinomina i files
            xadConn.moveFile(fileUd, fileUdDest);
            xadConn.moveFile(filePc, filePcDest);
            // Chiude la connessione
            xadConn.close();
            tmpRet = true;
        } catch (ResourceException | FileAlreadyExistsException | FileNotExistsException | FileUnderUseException
                | InsufficientPermissionOnFileException | LockingFailedException | NoTransactionAssociatedException
                | InterruptedException e) {
            log.error("Errore nella ridenominazione dei file ", e);
            if (xadConn != null) {
                xadConn.close();
                log.info("close effettuato");
            }
            tmpRet = false;
        }
        return tmpRet;
    }

    private boolean rimuoviFile(File file) {
        boolean tmpRet;
        XADiskConnection xadConn = null;
        try {
            //
            // elimina file
            xadConn = xadCf.getConnection();
            xadConn.deleteFile(file);
            xadConn.close();
            tmpRet = true;
        } catch (ResourceException | DirectoryNotEmptyException | FileNotExistsException | FileUnderUseException
                | InsufficientPermissionOnFileException | LockingFailedException | NoTransactionAssociatedException
                | InterruptedException e) {
            log.error("Errore nella rimozione del file ", e);
            if (xadConn != null) {
                xadConn.close();
                log.info("close effettuato");
            }
            tmpRet = false;
        }
        return tmpRet;
    }
}
