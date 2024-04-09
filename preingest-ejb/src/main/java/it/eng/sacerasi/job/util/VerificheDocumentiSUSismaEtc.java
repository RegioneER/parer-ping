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

package it.eng.sacerasi.job.util;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Iacolucci
 */
public class VerificheDocumentiSUSismaEtc {

    private static final Logger log = LoggerFactory.getLogger(VerificheDocumentiSUSismaEtc.class);

    /**
     * 
     * @param completeZipFileName
     *            es.: pippo.zip
     * @param entryInsideZip
     *            es.: \pippo\paperino\file.doc
     * 
     * @return false se la lunghezza complessiva del nome dello zip + entry maggiore 254 caratteri altrimenti true.
     */
    public static final boolean isLongMoreThan254Chars(String completeZipFileName, String entryInsideZip) {
        boolean esito = false;
        // Prende solo il nome del file senza ".zip" finale
        String nomeSenzaZip = completeZipFileName.substring(0, completeZipFileName.indexOf(".zip"));
        String nomeCompleto = nomeSenzaZip + "_" + entryInsideZip;
        int lun = nomeCompleto.length();
        if (lun > 254) {
            log.debug(String.format("l'entry [%s] è lunga [%d] quindi > 254 caratteri!", nomeCompleto, lun));
            esito = true;
        } else {
            log.debug(String.format("l'entry [%s] è lunga [%d].", nomeCompleto, lun));
        }
        return esito;
    }

    /**
     * Torna true se file è uno zip valido
     * 
     * @param file
     *            il file da verificare
     * 
     * @return true se file è uno zip valido
     */
    public static boolean isValidZip(final File file) {
        ZipFile zipfile = null;
        try {
            zipfile = new ZipFile(file);
            return true;
        } catch (IOException e) {
            return false;
        } finally {
            try {
                if (zipfile != null) {
                    zipfile.close();
                    zipfile = null;
                }
            } catch (IOException e) {
                log.error("Errore nella chiusura del file zip", e);
            }
        }
    }

    // MEV 30808
    public static int firstFailurePoint(Pattern regex, String str) {
        for (int i = 0; i <= str.length(); i++) {
            Matcher m = regex.matcher(str.substring(0, i));
            if (!m.matches() && !m.hitEnd()) {
                return i;
            }
        }

        if (regex.matcher(str).matches()) {
            return -1;
        } else {
            return str.length();
        }
    }

}
