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

package it.eng.sacerasi.ws.notificaPrelievo.ejb;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.resource.ResourceException;
import javax.transaction.UserTransaction;

import org.apache.commons.lang3.exception.ExceptionUtils;
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

import it.eng.sacerasi.common.Constants;
import it.eng.sacerasi.common.ejb.CommonDb;
import it.eng.sacerasi.messages.MessaggiWSBundle;
import it.eng.sacerasi.ws.dto.IRispostaWS;
import it.eng.sacerasi.ws.dto.RispostaControlli;
import it.eng.sacerasi.ws.ejb.SalvataggioDati;
import it.eng.sacerasi.ws.notificaPrelievo.dto.NotificaPrelievoExt;
import it.eng.sacerasi.ws.notificaPrelievo.dto.NotificaPrelievoInput;
import it.eng.sacerasi.ws.notificaPrelievo.dto.RispostaWSNotificaPrelievo;
import it.eng.sacerasi.ws.notificaPrelievo.dto.WSDescNotificaPrelievo;
import it.eng.sacerasi.ws.notificaPrelievo.helper.NotificaPrelievoCheckHelper;
import it.eng.sacerasi.ws.response.NotificaPrelievoRisposta;
import it.eng.sacerasi.ws.util.WsTransactionManager;

/**
 *
 * @author Bonora_L
 */
@Stateless(mappedName = "NotificaPrelievoEjb")
@LocalBean
@TransactionManagement(TransactionManagementType.BEAN)
public class NotificaPrelievoEjb {

    private static final Logger log = LoggerFactory.getLogger(NotificaPrelievoEjb.class);

    @Resource
    private UserTransaction utx;
    @EJB
    private SalvataggioDati salvataggioDati;
    @EJB
    private CommonDb commonDb;
    @EJB
    private NotificaPrelievoCheckHelper notificaPrelievoCheckHelper;
    //
    @Resource(mappedName = "jca/xadiskLocal")
    private XADiskConnectionFactory xadCf;
    private WsTransactionManager wtm;

    // public NotificaPrelievoRisposta notificaPrelievo(String nmAmbiente,
    // String nmVersatore, String cdPassword, String cdKeyObject) {
    public NotificaPrelievoRisposta notificaPrelievo(String nmAmbiente, String nmVersatore, String cdKeyObject) {
        // Istanzio la risposta
        RispostaWSNotificaPrelievo rispostaWs = new RispostaWSNotificaPrelievo();
        rispostaWs.setNotificaPrelievoRisposta(new NotificaPrelievoRisposta());
        // Imposto l'esito della risposta di default OK
        rispostaWs.getNotificaPrelievoRisposta().setCdEsito(Constants.EsitoServizio.OK);
        // Istanzio l'oggetto che contiene i parametri ricevuti
        NotificaPrelievoInput inputParameters = new NotificaPrelievoInput(nmAmbiente, nmVersatore, cdKeyObject);
        // Istanzio l'Ext con l'oggetto creato
        NotificaPrelievoExt npExt = new NotificaPrelievoExt();
        npExt.setDescrizione(new WSDescNotificaPrelievo());
        npExt.setNotificaPrelievoInput(inputParameters);
        wtm = new WsTransactionManager(utx);
        // Chiamo la classe RichiestaRestituzioneOggettoCheck che gestisce i controlli e popola la rispostaWs
        notificaPrelievoCheckHelper.checkRichiesta(npExt, rispostaWs);

        RispostaControlli tmpRispCon = new RispostaControlli();
        wtm.beginTrans(rispostaWs);
        String rootFtp = "";
        try {
            rootFtp = commonDb.getRootFtpParam();
        } catch (Exception ex) {
            log.error(ex.getMessage());
            tmpRispCon.reset();
            tmpRispCon.setrBoolean(false);
            tmpRispCon.setCodErr(MessaggiWSBundle.ERR_666);
            tmpRispCon.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_NOTIF_PREL));
            setRispostaWsError(rispostaWs, tmpRispCon);
        }
        // Verifico l'esito dei controlli:
        if (rispostaWs.getSeverity() != IRispostaWS.SeverityEnum.ERROR) {
            // Ho passato i controlli con esito positivo
            // Modifico la sessione esistente chiudendola con stato CHIUSO_OK
            npExt.setDtApertura(null);
            npExt.setDtChiusura(Calendar.getInstance().getTime());
            npExt.setStatoSessione(Constants.StatoSessioneRecup.CHIUSO_OK);
            tmpRispCon = salvataggioDati.modificaSessioneRecupero(npExt, null, null);
            if (tmpRispCon.getCodErr() != null) {
                setRispostaWsError(rispostaWs, tmpRispCon);
            }
        } else {
            // Ho passato i controlli con esito negativo
            if (npExt.getIdSessioneRecup() != null) {
                // Se esiste una sessione, la modifico
                npExt.setDtApertura(null);
                npExt.setDtChiusura(Calendar.getInstance().getTime());
                npExt.setStatoSessione(Constants.StatoSessioneRecup.CHIUSO_ERR_PRELEVATO);

                tmpRispCon = salvataggioDati.modificaSessioneRecupero(npExt, rispostaWs.getErrorCode(),
                        rispostaWs.getErrorMessage());
                if (tmpRispCon.getCodErr() != null) {
                    setRispostaWsError(rispostaWs, tmpRispCon);
                }
            } else {
                // Non esiste la sessione
                // Ne creo una nuova con stato di errore CHIUSA_ERR_PRELEVATO
                Date now = Calendar.getInstance().getTime();
                npExt.setDtApertura(now);
                npExt.setDtChiusura(now);
                npExt.setStatoSessione(Constants.StatoSessioneRecup.CHIUSO_ERR_PRELEVATO);

                tmpRispCon = salvataggioDati.creaSessioneRecupero(npExt, rispostaWs.getErrorCode(),
                        rispostaWs.getErrorMessage());
                if (tmpRispCon.getCodErr() != null) {
                    setRispostaWsError(rispostaWs, tmpRispCon);
                }
            }
        }

        // Utilizzo XADisk per creare una transazione in modo da eliminare i file in maniera atomica.
        // Elimina files se presenti nella directory ftp output
        // Errore o no, elimino infine i file, se esistono
        if (npExt.getFtpOutput() != null) {
            File udFile = new File(rootFtp.concat(npExt.getFtpOutput()).concat(File.separator).concat("UD_")
                    .concat(cdKeyObject).concat(Constants.ZIP_EXTENSION));
            File pcFile = new File(rootFtp.concat(npExt.getFtpOutput()).concat(File.separator).concat("PC_")
                    .concat(cdKeyObject).concat(Constants.ZIP_EXTENSION));

            tmpRispCon.reset();
            tmpRispCon = this.rimuoviFile(udFile, pcFile);
            if (tmpRispCon.getCodErr() != null) {
                setRispostaWsError(rispostaWs, tmpRispCon);
            }
        }

        if (tmpRispCon.isrBoolean() && rispostaWs.getErrorType() != IRispostaWS.ErrorTypeEnum.DB_FATAL) {
            log.info("Committing...");
            wtm.commit(rispostaWs);
        }

        return rispostaWs.getNotificaPrelievoRisposta();
    }

    private RispostaControlli rimuoviFile(File... files) {
        RispostaControlli tmpRispConn = new RispostaControlli();
        XADiskConnection xadConn = null;

        try {
            //
            // elimina file
            xadConn = xadCf.getConnection();
            for (File file : files) {
                if (file.exists()) {
                    xadConn.deleteFile(file);
                }
            }
            xadConn.close();
            tmpRispConn.setrBoolean(true);
        } catch (ResourceException | DirectoryNotEmptyException | FileNotExistsException | FileUnderUseException
                | InsufficientPermissionOnFileException | LockingFailedException | NoTransactionAssociatedException
                | InterruptedException e) {
            log.error("Errore nella rimozione dei file ", e);
            if (xadConn != null) {
                xadConn.close();
                log.info("close effettuato");
            }
            tmpRispConn.setrBoolean(false);
            tmpRispConn.setCodErr(MessaggiWSBundle.ERR_666);
            tmpRispConn.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
                    String.join("\n", ExceptionUtils.getRootCauseStackTrace(e))));
        }
        return tmpRispConn;
    }

    private void setRispostaWsError(RispostaWSNotificaPrelievo risp, RispostaControlli tmpRispostaControlli) {
        risp.setSeverity(IRispostaWS.SeverityEnum.ERROR);
        risp.setErrorCode(tmpRispostaControlli.getCodErr());
        risp.setErrorMessage(tmpRispostaControlli.getDsErr());
        risp.getNotificaPrelievoRisposta().setCdEsito(Constants.EsitoServizio.KO);
        risp.getNotificaPrelievoRisposta().setCdErr(tmpRispostaControlli.getCodErr());
        risp.getNotificaPrelievoRisposta().setDlErr(tmpRispostaControlli.getDsErr());
        wtm.rollback(risp);
        log.info("Rollbacking...");
    }
}
