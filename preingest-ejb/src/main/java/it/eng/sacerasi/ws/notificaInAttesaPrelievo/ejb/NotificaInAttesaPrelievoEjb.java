/*
 * Engineering Ingegneria Informatica S.p.A.
 *
 * Copyright (C) 2023 Regione Emilia-Romagna <p/> This program is free software: you can
 * redistribute it and/or modify it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version. <p/> This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Affero General Public License for more details. <p/> You should
 * have received a copy of the GNU Affero General Public License along with this program. If not,
 * see <https://www.gnu.org/licenses/>.
 */

package it.eng.sacerasi.ws.notificaInAttesaPrelievo.ejb;

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
import it.eng.sacerasi.ws.notificaInAttesaPrelievo.dto.NotificaInAttesaPrelievoExt;
import it.eng.sacerasi.ws.notificaInAttesaPrelievo.dto.NotificaInAttesaPrelievoInput;
import it.eng.sacerasi.ws.notificaInAttesaPrelievo.dto.RispostaWSNotificaInAttesaPrelievo;
import it.eng.sacerasi.ws.notificaInAttesaPrelievo.dto.WSDescNotificaInAttesaPrelievo;
import it.eng.sacerasi.ws.notificaInAttesaPrelievo.helper.NotificaInAttesaPrelievoCheckHelper;
import it.eng.sacerasi.ws.response.NotificaInAttesaPrelievoRisposta;
import it.eng.sacerasi.ws.util.WsTransactionManager;

/**
 *
 * @author Gilioli_P
 */
@Stateless(mappedName = "NotificaInAttesaPrelievoEjb")
@LocalBean
@TransactionManagement(TransactionManagementType.BEAN)
public class NotificaInAttesaPrelievoEjb {

    @Resource
    private UserTransaction utx;
    @EJB
    private SalvataggioDati salvataggioDati;
    @EJB
    private CommonDb commonDb;
    @EJB
    private NotificaInAttesaPrelievoCheckHelper notificaInAttesaPrelievoCheckHelper;
    //
    @Resource(mappedName = "jca/xadiskLocal")
    private XADiskConnectionFactory xadCf;
    private static final Logger log = LoggerFactory.getLogger(NotificaInAttesaPrelievoEjb.class);
    private WsTransactionManager wtm;

    // public NotificaInAttesaPrelievoRisposta notificaInAttesaPrelievo(String nmAmbiente,
    // String nmVersatore, String cdPassword, String cdKeyObject) {
    public NotificaInAttesaPrelievoRisposta notificaInAttesaPrelievo(String nmAmbiente,
	    String nmVersatore, String cdKeyObject) {
	// Istanzio la risposta
	RispostaWSNotificaInAttesaPrelievo rispostaWs = new RispostaWSNotificaInAttesaPrelievo();
	rispostaWs.setNotificaInAttesaPrelievoRisposta(new NotificaInAttesaPrelievoRisposta());
	// Imposto l'esito della risposta di default OK
	rispostaWs.getNotificaInAttesaPrelievoRisposta().setCdEsito(Constants.EsitoServizio.OK);
	// Istanzio l'oggetto che contiene i parametri ricevuti
	// NotificaInAttesaPrelievoInput inputParameters = new
	// NotificaInAttesaPrelievoInput(nmAmbiente, nmVersatore,
	// cdPassword, cdKeyObject);
	NotificaInAttesaPrelievoInput inputParameters = new NotificaInAttesaPrelievoInput(
		nmAmbiente, nmVersatore, cdKeyObject);
	// Istanzio l'Ext con l'oggetto creato
	NotificaInAttesaPrelievoExt niapExt = new NotificaInAttesaPrelievoExt();
	niapExt.setDescrizione(new WSDescNotificaInAttesaPrelievo());
	niapExt.setNotificaInAttesaPrelievoInput(inputParameters);
	wtm = new WsTransactionManager(utx);
	// Chiamo la classe NotificaInAttesaPrelievoCheck che gestisce i controlli e popola la
	// rispostaWs
	notificaInAttesaPrelievoCheckHelper.checkRichiesta(niapExt, rispostaWs);

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
	    tmpRispCon.setDsErr(
		    MessaggiWSBundle.getString(MessaggiWSBundle.ERR_NOTIF_IN_ATTESA_PREL));
	    setRispostaWsError(rispostaWs, tmpRispCon);
	}
	// Verifico l'esito dei controlli:
	if (rispostaWs.getSeverity() != IRispostaWS.SeverityEnum.ERROR) {
	    // Ho passato i controlli con esito positivo
	    // Modifico la sessione esistente con stato IN_ATTESA_PRELIEVO
	    niapExt.setStatoSessione(Constants.StatoSessioneRecup.IN_ATTESA_PRELIEVO);
	    tmpRispCon = salvataggioDati.modificaSessioneRecupero(niapExt, null, null);
	    if (tmpRispCon.getCodErr() != null) {
		setRispostaWsError(rispostaWs, tmpRispCon);
	    }
	} else {
	    // Ho passato i controlli con esito negativo
	    if (niapExt.getIdSessioneRecup() != null && niapExt.getStatoSessione()
		    .equals(Constants.StatoSessioneRecup.IN_ATTESA_RECUP)) {
		// Se esiste una sessione e il suo stato vale IN_ATTESA_RECUP, la modifico
		// npExt.setDtApertura(null);
		niapExt.setDtChiusura(Calendar.getInstance().getTime());
		niapExt.setStatoSessione(Constants.StatoSessioneRecup.CHIUSO_ERR_NOTIFICATO);

		tmpRispCon = salvataggioDati.modificaSessioneRecupero(niapExt,
			rispostaWs.getErrorCode(), rispostaWs.getErrorMessage());
		if (tmpRispCon.getCodErr() != null) {
		    setRispostaWsError(rispostaWs, tmpRispCon);
		}
	    } else {
		// Non esiste la sessione
		// Ne creo una nuova con stato di errore CHIUSO_ERR_NOTIFICATO
		Date now = Calendar.getInstance().getTime();
		niapExt.setDtApertura(now);
		niapExt.setDtChiusura(now);
		niapExt.setStatoSessione(Constants.StatoSessioneRecup.CHIUSO_ERR_NOTIFICATO);

		tmpRispCon = salvataggioDati.creaSessioneRecupero(niapExt,
			rispostaWs.getErrorCode(), rispostaWs.getErrorMessage());
		if (tmpRispCon.getCodErr() != null) {
		    setRispostaWsError(rispostaWs, tmpRispCon);
		}
	    }
	    // Utilizzo XADisk per creare una transazione in modo da eliminare i file in maniera
	    // atomica.
	    // Elimina files se presenti nella directory ftp output
	    // Errore o no, elimino infine i file, se esistono
	    if (rispostaWs.getSeverity() != IRispostaWS.SeverityEnum.ERROR) {
		if (niapExt.getFtpOutput() != null) {
		    File udFile = new File(rootFtp.concat(niapExt.getFtpOutput())
			    .concat(File.separator).concat("UD_").concat(cdKeyObject)
			    .concat(Constants.ZIP_EXTENSION));
		    File pcFile = new File(rootFtp.concat(niapExt.getFtpOutput())
			    .concat(File.separator).concat("PC_").concat(cdKeyObject)
			    .concat(Constants.ZIP_EXTENSION));

		    tmpRispCon.reset();
		    tmpRispCon = this.rimuoviFile(udFile, pcFile);
		    if (tmpRispCon.getCodErr() != null) {
			setRispostaWsError(rispostaWs, tmpRispCon);
		    }
		}
	    }
	}

	if (tmpRispCon.isrBoolean()
		&& rispostaWs.getErrorType() != IRispostaWS.ErrorTypeEnum.DB_FATAL) {
	    log.info("Committing...");
	    wtm.commit(rispostaWs);
	}

	// Se i controlli hanno rilevato un errore
	if (rispostaWs.getSeverity() != IRispostaWS.SeverityEnum.ERROR) {
	    // Se l'errore è pari a PING_NOTIFATTESAPREL_007 e lo stato sessione vale
	    // IN_ATTESA_PRELIEVO oppure CHIUSO_OK
	    if (rispostaWs.getNotificaInAttesaPrelievoRisposta().getCdErr() != null
		    && rispostaWs.getNotificaInAttesaPrelievoRisposta().getCdErr()
			    .equals(MessaggiWSBundle.PING_NOTIFATTESAPREL_007)
		    && (niapExt.getStatoSessione()
			    .equals(Constants.StatoSessioneRecup.IN_ATTESA_PRELIEVO)
			    || niapExt.getStatoSessione()
				    .equals(Constants.StatoSessioneRecup.CHIUSO_OK))) {
		rispostaWs.setSeverity(IRispostaWS.SeverityEnum.OK);
		rispostaWs.setErrorCode(null);
		rispostaWs.setErrorMessage(null);
		rispostaWs.getNotificaInAttesaPrelievoRisposta()
			.setCdEsito(Constants.EsitoServizio.OK);
		rispostaWs.getNotificaInAttesaPrelievoRisposta().setCdErr(null);
		rispostaWs.getNotificaInAttesaPrelievoRisposta().setDlErr(null);
	    }
	}

	return rispostaWs.getNotificaInAttesaPrelievoRisposta();
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
	} catch (ResourceException | DirectoryNotEmptyException | FileNotExistsException
		| FileUnderUseException | InsufficientPermissionOnFileException
		| LockingFailedException | NoTransactionAssociatedException
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

    private void setRispostaWsError(RispostaWSNotificaInAttesaPrelievo risp,
	    RispostaControlli tmpRispostaControlli) {
	risp.setSeverity(IRispostaWS.SeverityEnum.ERROR);
	risp.setErrorCode(tmpRispostaControlli.getCodErr());
	risp.setErrorMessage(tmpRispostaControlli.getDsErr());
	risp.getNotificaInAttesaPrelievoRisposta().setCdEsito(Constants.EsitoServizio.KO);
	risp.getNotificaInAttesaPrelievoRisposta().setCdErr(tmpRispostaControlli.getCodErr());
	risp.getNotificaInAttesaPrelievoRisposta().setDlErr(tmpRispostaControlli.getDsErr());
	wtm.rollback(risp);
	log.info("Rollbacking...");
    }
}
