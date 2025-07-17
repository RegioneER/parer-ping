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

package it.eng.sacerasi.ws.util;

import it.eng.sacerasi.messages.MessaggiWSBundle;
import it.eng.sacerasi.ws.dto.RispostaControlli;
import java.io.File;
import java.io.InputStream;
import javax.resource.ResourceException;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xadisk.additional.XAFileInputStreamWrapper;
import org.xadisk.bridge.proxies.interfaces.XAFileInputStream;
import org.xadisk.connector.outbound.XADiskConnection;
import org.xadisk.connector.outbound.XADiskConnectionFactory;
import org.xadisk.filesystem.exceptions.DirectoryNotEmptyException;
import org.xadisk.filesystem.exceptions.FileNotExistsException;
import org.xadisk.filesystem.exceptions.FileUnderUseException;
import org.xadisk.filesystem.exceptions.InsufficientPermissionOnFileException;
import org.xadisk.filesystem.exceptions.LockingFailedException;
import org.xadisk.filesystem.exceptions.NoTransactionAssociatedException;

/**
 *
 * @author Bonora_L
 */
public class Util {

    private static final Logger log = LoggerFactory.getLogger(Util.class);

    public static RispostaControlli rimuoviDir(XADiskConnectionFactory xadCf, String dirPath) {
	RispostaControlli tmpRispConn = new RispostaControlli();
	XADiskConnection xadConn = null;

	try {
	    // elimina file
	    File tmpFile = new File(dirPath);
	    if (tmpFile.exists() && tmpFile.isDirectory()) {
		xadConn = xadCf.getConnection();
		rimuoviDirRicorsivamente(tmpFile, xadConn);
	    }
	    tmpRispConn.setrBoolean(true);
	} catch (ResourceException | DirectoryNotEmptyException | FileNotExistsException
		| FileUnderUseException | InsufficientPermissionOnFileException
		| LockingFailedException | NoTransactionAssociatedException
		| InterruptedException e) {
	    log.error("Errore nella rimozione dei file ", e);

	    tmpRispConn.setrBoolean(false);
	    tmpRispConn.setCodErr(MessaggiWSBundle.ERR_666);
	    tmpRispConn.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
		    String.join("\n", ExceptionUtils.getRootCauseStackTrace(e))));
	} finally {
	    if (xadConn != null) {
		xadConn.close();
		log.info("close effettuato");
	    }
	}
	return tmpRispConn;
    }

    // FF - questo metodo viene chiamato per TUTTE le rimozioni della directory FTP
    public static void rimuoviDirRicorsivamente(File dirPath, XADiskConnection xadConn)
	    throws FileNotExistsException, LockingFailedException, NoTransactionAssociatedException,
	    InterruptedException, InsufficientPermissionOnFileException, DirectoryNotEmptyException,
	    FileUnderUseException {
	if (xadConn.fileExistsAndIsDirectory(dirPath)) {
	    File[] elencoFile = Util.listFiles(xadConn, dirPath);
	    if (elencoFile != null && elencoFile.length > 0) {
		for (File file : elencoFile) {
		    if (xadConn.fileExistsAndIsDirectory(file)) {
			rimuoviDirRicorsivamente(file, xadConn);
		    } else {
			xadConn.deleteFile(file);
		    }
		}
	    }
	    xadConn.deleteFile(dirPath);
	} else {
	    log.info("***************************");
	    log.warn("La directory da rimuovere non esiste, ignoro il problema: "
		    + dirPath.toString());
	    log.info("***************************");
	}
    }

    /*
     * Funzione ricorsiva che cancella tutti i file e le cartelle presenti a qualsiasi livello
     * tranne la cartella "dirPath" (solo se è al livello 1 della ricorsione) e tranne il file sotto
     * "dirPath" (solo se è al livello 1 della ricorsione) con nome "cartellaPrincipale".zip.
     */
    public static void eliminaCartellaRicorsivamenteTranneSeStessa(File dirPath,
	    XADiskConnection xadConn, String cartellaPrincipale, int livello)
	    throws FileNotExistsException, LockingFailedException, NoTransactionAssociatedException,
	    InterruptedException, InsufficientPermissionOnFileException, DirectoryNotEmptyException,
	    FileUnderUseException {
	livello++;
	if (xadConn.fileExistsAndIsDirectory(dirPath)) {
	    File[] elencoFile = Util.listFiles(xadConn, dirPath);
	    if (elencoFile != null && elencoFile.length > 0) {
		for (File file : elencoFile) {
		    if (xadConn.fileExistsAndIsDirectory(file)) {
			eliminaCartellaRicorsivamenteTranneSeStessa(file, xadConn,
				cartellaPrincipale, livello);
		    } else {
			if (livello == 1) {
			    if (file.getName().equals(cartellaPrincipale + ".zip") == false) {
				xadConn.deleteFile(file);
			    }
			} else {
			    xadConn.deleteFile(file);
			}
		    }
		}
	    }
	    if (livello != 1) {
		xadConn.deleteFile(dirPath);
	    }
	} else {
	    log.info("***************************");
	    log.warn("La directory da rimuovere non esiste, ignoro il problema: "
		    + dirPath.toString());
	    log.info("***************************");
	}
    }

    public static File[] listFiles(XADiskConnection xadConn, File root)
	    throws FileNotExistsException, LockingFailedException, LockingFailedException,
	    NoTransactionAssociatedException, InterruptedException,
	    InsufficientPermissionOnFileException {
	String[] filesName = xadConn.listFiles(root);
	File[] files = new File[filesName.length];
	for (int i = 0; i < filesName.length; i++) {
	    files[i] = new File(root, filesName[i]);
	}
	return files;
    }

    public static InputStream getFileInputStream(XADiskConnection xadConn, File file)
	    throws FileNotExistsException, InsufficientPermissionOnFileException,
	    LockingFailedException, NoTransactionAssociatedException, InterruptedException {
	XAFileInputStream xafis = xadConn.createXAFileInputStream(file);
	InputStream inputStream = new XAFileInputStreamWrapper(xafis);
	return inputStream;
    }
}
