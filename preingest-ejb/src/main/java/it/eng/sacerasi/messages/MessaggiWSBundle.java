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

package it.eng.sacerasi.messages;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.sacerasi.exception.MessageBundleNotFoundException;
import it.eng.spagoCore.util.UUIDMdcLogUtil;

public class MessaggiWSBundle {

    private static Logger log = LoggerFactory.getLogger(MessaggiWSBundle.class);

    /*
     * Metodi statici, implementazione causata dalla necessità di mantenere invariata l'interfaccia
     * della classe originale: un normalissimo Bundle con un file di properties
     */
    public static String getString(String key) {
	switch (key) {
	case MessaggiWSBundle.ERR_666:
	    return getDefaultErrorMessage(key);
	case MessaggiWSBundle.ERR_PERSISTENCE:
	    return getDefaultErrorMessage(key);
	case MessaggiWSBundle.ERR_UPDATE:
	    return getDefaultErrorMessage(key);
	case MessaggiWSBundle.ERR_XML_MALFORMED:
	    return getDefaultErrorMessage(key);
	default:
	    // l'operazione di StringEscapeUtils.unescapeJava viene svolta nel singleton
	    return lookupCacheRef().getString(key);
	}
    }

    public static String getString(String key, Object... params) {
	switch (key) {
	case MessaggiWSBundle.ERR_666:
	    return getDefaultErrorMessage(key, params);
	case MessaggiWSBundle.ERR_PERSISTENCE:
	    return getDefaultErrorMessage(key, params);
	case MessaggiWSBundle.ERR_UPDATE:
	    return getDefaultErrorMessage(key, params);
	case MessaggiWSBundle.ERR_XML_MALFORMED:
	    return getDefaultErrorMessage(key, params);
	default:
	    // l'operazione di StringEscapeUtils.unescapeJava viene svolta nel singleton
	    return lookupCacheRef().getString(key, params);
	}
    }

    private static MessaggiWSCache lookupCacheRef() {
	try {
	    return (MessaggiWSCache) new InitialContext()
		    .lookup("java:app/SacerAsync-ejb/MessaggiWSCache");
	} catch (NamingException ex) {
	    log.error("Errore lookup dei messaggi " + ExceptionUtils.getRootCauseMessage(ex), ex);
	    throw new MessageBundleNotFoundException("Errore lookup singleton dei messaggi "
		    + ExceptionUtils.getRootCauseMessage(ex));
	}
    }

    private static String getDefaultErrorMessage(String key, Object... params) {
	// get or generate uuid
	final String uuid = UUIDMdcLogUtil.getUuid();
	// log original message
	log.error("Risposta originale : " + lookupCacheRef().getString(key, params));
	return lookupCacheRef().getString(MessaggiWSBundle.WS_GENERIC_ERROR_UUID, uuid);
    }

    // Le righe che seguono verranno mostrate raggruppate in Netbeans
    //
    // <editor-fold defaultstate="collapsed" desc="COSTANTI DI ERRORE">
    //
    // Queste sono le costanti di errore, questo elenco deve coincidere
    // con il contenuto della tabella PIG_ERRORE
    //
    // ERRORI IMPREVISTI
    public static final String ERR_666 = "666";
    public static final String ERR_666P = "666P";
    public static final String ERR_PERSISTENCE = "ERR-PERSISTENCE";
    public static final String ERR_UPDATE = "ERR-UPDATE";
    public static final String ERR_XML_MALFORMED = "ERR-XML-MALFORMED";
    public static final String ERR_SESSIONE = "ERR-SESSIONE";
    public static final String ERR_OBJECT = "ERR-OBJECT";
    public static final String ERR_SESSIONE_RECUP = "ERR-SESSIONE-RECUP";
    public static final String ERR_NOTIF_PREL = "ERR-NOTIF-PREL";
    public static final String ERR_NOTIF_IN_ATTESA_PREL = "ERR-NOTIF-IN-ATTESA-PREL";
    public static final String ERR_PULIZIA_NOTIFICATO = "ERR-PULIZIA-NOTIFICATO";
    public static final String ERR_WS_CHECK = "WS-CHECK";
    public static final String ERR_TIPOOBJECT_PRIORITA_CHECK = "ERR-MANCA-PRIORITA";

    // ERRORI IMPREVISTI TEMPLATE (ossia da restituire all'utente a fronte degli
    // errori imprevisti)
    public static final String WS_GENERIC_ERROR_UUID = "WS-GENERIC-ERROR-UUID";

    // ERRORI PARAMETRI INVIO
    public static final String PING_SENDOBJ_001 = "PING-SENDOBJ-001";
    public static final String PING_SENDOBJ_002 = "PING-SENDOBJ-002";
    public static final String PING_SENDOBJ_003 = "PING-SENDOBJ-003";
    public static final String PING_SENDOBJ_004 = "PING-SENDOBJ-004";
    public static final String PING_SENDOBJ_005 = "PING-SENDOBJ-005";
    public static final String PING_SENDOBJ_006 = "PING-SENDOBJ-006";
    public static final String PING_SENDOBJ_007 = "PING-SENDOBJ-007";
    public static final String PING_SENDOBJ_008 = "PING-SENDOBJ-008";
    public static final String PING_SENDOBJ_009 = "PING-SENDOBJ-009";
    public static final String PING_SENDOBJ_010 = "PING-SENDOBJ-010";
    public static final String PING_SENDOBJ_011 = "PING-SENDOBJ-011";
    public static final String PING_SENDOBJ_012 = "PING-SENDOBJ-012";
    public static final String PING_SENDOBJ_013 = "PING-SENDOBJ-013";
    public static final String PING_SENDOBJ_014 = "PING-SENDOBJ-014";
    public static final String PING_SENDOBJ_015 = "PING-SENDOBJ-015";
    public static final String PING_SENDOBJ_016 = "PING-SENDOBJ-016";
    public static final String PING_SENDOBJ_017 = "PING-SENDOBJ-017";
    public static final String PING_SENDOBJ_018 = "PING-SENDOBJ-018";
    public static final String PING_SENDOBJ_019 = "PING-SENDOBJ-019";
    public static final String PING_SENDOBJ_020 = "PING-SENDOBJ-020";
    public static final String PING_SENDOBJ_021 = "PING-SENDOBJ-021";
    public static final String PING_SENDOBJ_022 = "PING-SENDOBJ-022";
    public static final String PING_SENDOBJ_023 = "PING-SENDOBJ-023";
    public static final String PING_SENDOBJ_024 = "PING-SENDOBJ-024";
    public static final String PING_SENDOBJ_025 = "PING-SENDOBJ-025";
    public static final String PING_SENDOBJ_026 = "PING-SENDOBJ-026";

    // ERRORI XML
    public static final String PING_SENDOBJ_XML_001 = "PING-SENDOBJ-XML-001";
    public static final String PING_SENDOBJ_XML_002 = "PING-SENDOBJ-XML-002";
    public static final String PING_SENDOBJ_XML_003 = "PING-SENDOBJ-XML-003";
    public static final String PING_SENDOBJ_XML_004 = "PING-SENDOBJ-XML-004";
    public static final String PING_SENDOBJ_XML_005 = "PING-SENDOBJ-XML-005";
    public static final String PING_SENDOBJ_XML_006 = "PING-SENDOBJ-XML-006";
    public static final String PING_SENDOBJ_XML_007 = "PING-SENDOBJ-XML-007";
    public static final String PING_SENDOBJ_XML_008 = "PING-SENDOBJ-XML-008";
    public static final String PING_SENDOBJ_XML_009 = "PING-SENDOBJ-XML-009";
    public static final String PING_SENDOBJ_XML_010 = "PING-SENDOBJ-XML-010";
    public static final String PING_SENDOBJ_XML_011 = "PING-SENDOBJ-XML-011";
    public static final String PING_SENDOBJ_XML_012 = "PING-SENDOBJ-XML-012";

    // ERRORI DICOM
    public static final String PING_SENDOBJ_DICOM_001 = "PING-SENDOBJ-DICOM-001";
    public static final String PING_SENDOBJ_DICOM_002 = "PING-SENDOBJ-DICOM-002";
    public static final String PING_SENDOBJ_DICOM_003 = "PING-SENDOBJ-DICOM-003";
    public static final String PING_SENDOBJ_DICOM_004 = "PING-SENDOBJ-DICOM-004";
    public static final String PING_SENDOBJ_DICOM_005 = "PING-SENDOBJ-DICOM-005";
    public static final String PING_SENDOBJ_DICOM_006 = "PING-SENDOBJ-DICOM-006";
    public static final String PING_SENDOBJ_DICOM_007 = "PING-SENDOBJ-DICOM-007";
    public static final String PING_SENDOBJ_DICOM_008 = "PING-SENDOBJ-DICOM-008";

    // ERRORI OGGETTO
    public static final String PING_SENDOBJ_OBJ_001 = "PING-SENDOBJ-OBJ-001";
    public static final String PING_SENDOBJ_OBJ_002 = "PING-SENDOBJ-OBJ-002";
    public static final String PING_SENDOBJ_OBJ_003 = "PING-SENDOBJ-OBJ-003";
    public static final String PING_SENDOBJ_OBJ_004 = "PING-SENDOBJ-OBJ-004";
    public static final String PING_SENDOBJ_OBJ_005 = "PING-SENDOBJ-OBJ-005";
    public static final String PING_SENDOBJ_OBJ_006 = "PING-SENDOBJ-OBJ-006";
    public static final String PING_SENDOBJ_OBJ_007 = "PING-SENDOBJ-OBJ-007";
    public static final String PING_SENDOBJ_OBJ_008 = "PING-SENDOBJ-OBJ-008";
    public static final String PING_SENDOBJ_OBJ_009 = "PING-SENDOBJ-OBJ-009";
    public static final String PING_SENDOBJ_OBJ_010 = "PING-SENDOBJ-OBJ-010";
    public static final String PING_SENDOBJ_OBJ_011 = "PING-SENDOBJ-OBJ-011";
    public static final String PING_SENDOBJ_OBJ_012 = "PING-SENDOBJ-OBJ-012";
    public static final String PING_SENDOBJ_OBJ_013 = "PING-SENDOBJ-OBJ-013";

    // ERRORI NOTIFICA
    public static final String PING_NOT_001 = "PING-NOT-001";
    public static final String PING_NOT_002 = "PING-NOT-002";
    public static final String PING_NOT_003 = "PING-NOT-003";
    public static final String PING_NOT_004 = "PING-NOT-004";
    public static final String PING_NOT_005 = "PING-NOT-005";
    public static final String PING_NOT_006 = "PING-NOT-006";
    public static final String PING_NOT_007 = "PING-NOT-007";
    public static final String PING_NOT_008 = "PING-NOT-008";
    public static final String PING_NOT_009 = "PING-NOT-009";
    public static final String PING_NOT_010 = "PING-NOT-010";
    public static final String PING_NOT_011 = "PING-NOT-011";
    public static final String PING_NOT_012 = "PING-NOT-012";
    public static final String PING_NOT_013 = "PING-NOT-013";
    public static final String PING_NOT_014 = "PING-NOT-014";
    public static final String PING_NOT_015 = "PING-NOT-015";
    public static final String PING_NOT_016 = "PING-NOT-016";
    public static final String PING_NOT_017 = "PING-NOT-017";
    public static final String PING_NOT_018 = "PING-NOT-018";
    public static final String PING_NOT_019 = "PING-NOT-019";
    /**
     * Il versamento di almeno uno degli oggetti figli dell''''oggetto {0} \u00E8 fallito e
     * l''''oggetto ''"padre''" ha assunto stato = {1}
     */
    public static final String PING_NOT_020 = "PING-NOT-020";
    public static final String PING_NOT_021 = "PING-NOT-021"; // MAC 28343

    // ERRORI RICERCA DIARIO
    public static final String PING_DIARIO_001 = "PING-DIARIO-001";
    public static final String PING_DIARIO_002 = "PING-DIARIO-002";
    public static final String PING_DIARIO_003 = "PING-DIARIO-003";
    public static final String PING_DIARIO_004 = "PING-DIARIO-004";
    public static final String PING_DIARIO_005 = "PING-DIARIO-005";
    public static final String PING_DIARIO_006 = "PING-DIARIO-006";
    public static final String PING_DIARIO_007 = "PING-DIARIO-007";
    public static final String PING_DIARIO_008 = "PING-DIARIO-008";
    public static final String PING_DIARIO_009 = "PING-DIARIO-009";

    // ERRORI RICERCA RESTITUZIONE OGGETTI
    public static final String PING_RESTIT_001 = "PING-RESTIT-001";
    public static final String PING_RESTIT_002 = "PING-RESTIT-002";
    public static final String PING_RESTIT_003 = "PING-RESTIT-003";
    public static final String PING_RESTIT_004 = "PING-RESTIT-004";
    public static final String PING_RESTIT_005 = "PING-RESTIT-005";
    public static final String PING_RESTIT_006 = "PING-RESTIT-006";
    public static final String PING_RESTIT_007 = "PING-RESTIT-007";
    public static final String PING_RESTIT_008 = "PING-RESTIT-008";
    public static final String PING_RESTIT_009 = "PING-RESTIT-009";

    // Errori Job
    public static final String PING_PREPXML_FILE_001 = "PING-PREPXML-FILE-001";
    public static final String PING_PREPXML_FILE_002 = "PING-PREPXML-FILE-002";
    public static final String PING_PREPXML_FILE_003 = "PING-PREPXML-FILE-003";
    public static final String PING_PREPXML_FILE_004 = "PING-PREPXML-FILE-004";
    public static final String PING_PREPXML_FILE_005 = "PING-PREPXML-FILE-005";
    public static final String PING_PREPXML_FILE_006 = "PING-PREPXML-FILE-006";
    public static final String PING_PREPXML_FILE_007 = "PING-PREPXML-FILE-007";
    public static final String PING_PREPXML_FILE_008 = "PING-PREPXML-FILE-008";
    public static final String PING_PREPXML_FILE_009 = "PING-PREPXML-FILE-009";
    public static final String PING_PREPXML_FILE_010 = "PING-PREPXML-FILE-010";
    public static final String PING_PREPXML_FILE_011 = "PING-PREPXML-FILE-011";
    public static final String PING_PREPXML_FILE_012 = "PING-PREPXML-FILE-012";
    public static final String PING_PREPXML_FILE_013 = "PING-PREPXML-FILE-013";
    public static final String PING_PREPXML_FILE_014 = "PING-PREPXML-FILE-014";
    public static final String PING_PREPXML_FILE_015 = "PING-PREPXML-FILE-015";
    public static final String PING_PREPXML_FILE_016 = "PING-PREPXML-FILE-016";
    public static final String PING_PREPXML_FILE_017 = "PING-PREPXML-FILE-017";
    public static final String PING_VERHASH_FILE_001 = "PING-VERHASH-FILE-001";
    /**
     * Nel file .zip dell''oggetto {0}, la cartella relativa all''unit\u00e0 documentaria {1} non
     * coincide con la chiave del SIP XML versato
     */
    public static final String PING_PREPXML_FILE_018 = "PING-PREPXML-FILE-018";
    /**
     * Nel file .zip dell''oggetto {0}, il documento {1} risulta avere la dimensione di 0 byte
     */
    public static final String PING_PREPXML_FILE_019 = "PING-PREPXML-FILE-019";
    /**
     * Il versamento di almeno uno degli oggetti figli dell’oggetto {0} e’ fallito e l’oggetto
     * “padre” ha assunto stato = {1}
     */
    public static final String PING_PREPXML_FILE_020 = "PING-PREPXML-FILE-020";

    // MAC #23269 - Il file .zip dell'oggetto versato è vuoto.
    public static final String PING_PREPXML_FILE_021 = "PING-PREPXML-FILE-021";

    // Errori richiesta chiusura warning
    public static final String PING_CHIUWARN_001 = "PING-CHIUWARN-001";
    public static final String PING_CHIUWARN_002 = "PING-CHIUWARN-002";
    public static final String PING_CHIUWARN_003 = "PING-CHIUWARN-003";
    public static final String PING_CHIUWARN_004 = "PING-CHIUWARN-004";
    public static final String PING_CHIUWARN_005 = "PING-CHIUWARN-005";
    public static final String PING_CHIUWARN_006 = "PING-CHIUWARN-006";
    public static final String PING_CHIUWARN_007 = "PING-CHIUWARN-007";
    public static final String PING_CHIUWARN_008 = "PING-CHIUWARN-008";

    // Errori richiesta restituzione oggetto
    public static final String PING_RICHOBJ_001 = "PING-RICHOBJ-001";
    public static final String PING_RICHOBJ_002 = "PING-RICHOBJ-002";
    public static final String PING_RICHOBJ_003 = "PING-RICHOBJ-003";
    public static final String PING_RICHOBJ_004 = "PING-RICHOBJ-004";
    public static final String PING_RICHOBJ_005 = "PING-RICHOBJ-005";
    public static final String PING_RICHOBJ_006 = "PING-RICHOBJ-006";
    public static final String PING_RICHOBJ_007 = "PING-RICHOBJ-007";
    public static final String PING_RICHOBJ_008 = "PING-RICHOBJ-008";
    public static final String PING_RICHOBJ_009 = "PING-RICHOBJ-009";

    // Errori richiesta notifica prelievo
    public static final String PING_NOTIFPREL_001 = "PING-NOTIFPREL-001";
    public static final String PING_NOTIFPREL_002 = "PING-NOTIFPREL-002";
    public static final String PING_NOTIFPREL_003 = "PING-NOTIFPREL-003";
    public static final String PING_NOTIFPREL_004 = "PING-NOTIFPREL-004";
    public static final String PING_NOTIFPREL_005 = "PING-NOTIFPREL-005";
    public static final String PING_NOTIFPREL_006 = "PING-NOTIFPREL-006";
    public static final String PING_NOTIFPREL_007 = "PING-NOTIFPREL-007";
    public static final String PING_NOTIFPREL_008 = "PING-NOTIFPREL-008";

    // Errori richiesta notifica in attesa prelievo
    public static final String PING_NOTIFATTESAPREL_001 = "PING-NOTIFATTESAPREL-001";
    public static final String PING_NOTIFATTESAPREL_002 = "PING-NOTIFATTESAPREL-002";
    public static final String PING_NOTIFATTESAPREL_003 = "PING-NOTIFATTESAPREL-003";
    public static final String PING_NOTIFATTESAPREL_004 = "PING-NOTIFATTESAPREL-004";
    public static final String PING_NOTIFATTESAPREL_005 = "PING-NOTIFATTESAPREL-005";
    public static final String PING_NOTIFATTESAPREL_006 = "PING-NOTIFATTESAPREL-006";
    public static final String PING_NOTIFATTESAPREL_007 = "PING-NOTIFATTESAPREL-007";
    public static final String PING_NOTIFATTESAPREL_008 = "PING-NOTIFATTESAPREL-008";

    // Errori recupero stato oggetto
    public static final String PING_RECDIPSTATO_001 = "PING-RECDIPSTATO-001";
    public static final String PING_RECDIPSTATO_002 = "PING-RECDIPSTATO-002";
    public static final String PING_RECDIPSTATO_003 = "PING-RECDIPSTATO-003";
    public static final String PING_RECDIPSTATO_004 = "PING-RECDIPSTATO-004";
    public static final String PING_RECDIPSTATO_005 = "PING-RECDIPSTATO-005";
    public static final String PING_RECDIPSTATO_006 = "PING-RECDIPSTATO-006";

    // Errori richiesta Sop Class List
    public static final String PING_SOP_001 = "PING-SOP-001";
    public static final String PING_SOP_002 = "PING-SOP-002";
    public static final String PING_SOP_003 = "PING-SOP-003";

    // Errori Producer / Consumer
    public static final String PING_PRODCODA_001 = "PING-PRODCODA-001";
    public static final String PING_CONSCODA_001 = "PING-CONSCODA-001";
    public static final String PING_CONSCODA_002 = "PING-CONSCODA-002";

    // Errori recupero sacer
    public static final String ERR_TIMEOUT_RECUPERO = "ERR-TIMEOUT-RECUPERO";
    public static final String ERR_NEGATIVO_RECUPERO_UD = "ERR-NEGATIVO-RECUPERO-UD";
    public static final String ERR_NEGATIVO_RECUPERO_PC = "ERR-NEGATIVO-RECUPERO-PC";

    // Errori notifica disponibilità
    public static final String ERR_TIMEOUT_NOTIF_DISP = "ERR-TIMEOUT-NOTIF-DISP";
    public static final String ERR_NEGATIVO_NOTIF_DISP = "ERR-NEGATIVO-NOTIF-DISP";

    // ERRORI RICERCA RECUPERATI
    public static final String PING_RICRECUP_001 = "PING-RICRECUP-001";
    public static final String PING_RICRECUP_002 = "PING-RICRECUP-002";
    public static final String PING_RICRECUP_003 = "PING-RICRECUP-003";

    // Errori richiesta pulizia notificato
    public static final String PING_ELIMINAPREL_001 = "PING-ELIMINAPREL-001";
    public static final String PING_ELIMINAPREL_002 = "PING-ELIMINAPREL-002";
    public static final String PING_ELIMINAPREL_003 = "PING-ELIMINAPREL-003";
    public static final String PING_ELIMINAPREL_004 = "PING-ELIMINAPREL-004";
    public static final String PING_ELIMINAPREL_005 = "PING-ELIMINAPREL-005";
    public static final String PING_ELIMINAPREL_006 = "PING-ELIMINAPREL-006";
    public static final String PING_ELIMINAPREL_007 = "PING-ELIMINAPREL-007";
    public static final String PING_ELIMINAPREL_008 = "PING-ELIMINAPREL-008";

    // ERRORI INSERIMENTO, MODIFICA, CANCELLAZIONE ORGANIZZAZIONE
    public static final String SERVIZI_ORG_001 = "SERVIZI-ORG-001";
    public static final String SERVIZI_ORG_002 = "SERVIZI-ORG-002";
    public static final String SERVIZI_ORG_003 = "SERVIZI-ORG-003";
    public static final String SERVIZI_ORG_004 = "SERVIZI-ORG-004";
    public static final String SERVIZI_ORG_005 = "SERVIZI-ORG-005";
    public static final String SERVIZI_ORG_006 = "SERVIZI-ORG-006";
    public static final String SERVIZI_ORG_007 = "SERVIZI-ORG-007";

    // ERRORI INSERIMENTO, MODIFICA, CANCELLAZIONE UTENTE
    public static final String SERVIZI_USR_001 = "SERVIZI-USR-001";
    public static final String SERVIZI_USR_002 = "SERVIZI-USR-002";
    public static final String SERVIZI_USR_003 = "SERVIZI-USR-003";
    public static final String SERVIZI_USR_004 = "SERVIZI-USR-004";
    public static final String SERVIZI_USR_005 = "SERVIZI-USR-005";

    // ERRORI PARTIZIONI SESSIONI
    public static final String PART_SES_001 = "PART_SES_001";
    public static final String PART_SES_002 = "PART_SES_002";

    // ERRORI PARTIZIONI OGGETTI
    public static final String PART_OBJ_001 = "PART_OBJ_001";
    public static final String PART_OBJ_002 = "PART_OBJ_002";

    // ERRORI TRASFORMAZIONE
    public static final String PING_ERR_VERS_001 = "PING_ERR_VERS_001";

    // ERRORI DEI SERVIZI REST
    public static final String PING_RESTWS_001 = "PING-RESTWS-001";
    public static final String PING_RESTWS_002 = "PING-RESTWS-002";
    public static final String PING_RESTWS_003 = "PING-RESTWS-003";
    public static final String PING_RESTWS_004 = "PING-RESTWS-004";
    public static final String PING_RESTWS_005 = "PING-RESTWS-005";

    // </editor-fold>
}
