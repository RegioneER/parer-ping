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

package it.eng.sacerasi.common;

/**
 *
 * @author Quaranta_M
 */
public class Constants {

    public static final int MAX_BYTES_DICOM_SIZE = 4000;
    public static final String DICOM_SEPARATOR = ";";
    public static final String ROOT_FTP = "ROOT_FTP";
    public static final String ROOT_TRASF = "ROOT_TRASFORMAZIONI";
    public static final String TIMEOUT_VERS_SACER = "TIMEOUT_VERS_SACER";
    public static final String URL_ANNUL_VERS = "URL_ANNUL_VERS";
    public static final String VERSIONE_XML_ANNUL = "VERSIONE_XML_ANNUL";
    public static final String VERSIONE_XML_SACER = "1.5";
    public static final String VERSIONE_XML_MM = "1.0";
    public static final String NUM_UNITA_DOC_CODA_VERS = "NUM_UNITA_DOC_CODA_VERS";
    public static final String NUM_MAX_DICOM_XGIORNO = "NUM_MAX_DICOM_XGIORNO";

    public static final String ID_VERSATORE_AGENZIA = "ID_VERSATORE_AGENZIA";
    public static final String URL_ALLINEA_ENTE_CONVENZ = "URL_ALLINEA_ENTE_CONVENZ";
    public static final String USERID_REPLICA_ORG = "USERID_REPLICA_ORG";
    public static final String PSW_REPLICA_ORG = "PSW_REPLICA_ORG";
    public static final String URL_REPLICA_ORG = "URL_REPLICA_ORG";
    public static final String NM_APPLIC = "NM_APPLIC";
    public static final String USERID_RECUP_INFO = "USERID_RECUP_INFO";
    public static final String PSW_RECUP_INFO = "PSW_RECUP_INFO";
    public static final String URL_RECUP_NEWS = "URL_RECUP_NEWS";
    public static final String URL_MODIFICA_PASSWORD = "URL_MODIFICA_PASSWORD";
    public static final String ESTENSIONI_FILE_DA_TRASF = "ESTENSIONI_FILE_DA_TRASF";
    public static final String DIM_MAX_FILE_DA_VERSARE_FTP = "DIM_MAX_FILE_DA_VERSARE_FTP";
    public static final String DIM_MAX_FILE_DA_VERSARE_ARCH = "DIM_MAX_FILE_DA_VERSARE_ARCH";
    public static final String DIM_MAX_FILE_DA_VERSARE_OS = "DIM_MAX_FILE_DA_VERSARE_OS";
    public static final String URL_RECUP_HELP = "URL_RECUP_HELP";
    public static final String DS_PREFISSO_PATH = "DS_PREFISSO_PATH";
    public static final String ACCETTA_STUDI_IN_WARNING = "ACCETTA_STUDI_IN_WARNING";

    // MEV 31714 espressa in minuti
    public static final String ETA_MINIMA_RECUPERO_ERRORI = "ETA_MINIMA_RECUPERO_ERRORI";

    // Costanti per il log dei login ws e la disattivazione automatica utenti
    public static final String IDP_MAX_TENTATIVI_FALLITI = "MAX_TENTATIVI_FALLITI";
    public static final String IDP_MAX_GIORNI = "MAX_GIORNI";
    public static final String IDP_QRY_DISABLE_USER = "QRY_DISABLE_USER";
    public static final String IDP_QRY_VERIFICA_DISATTIVAZIONE_UTENTE = "QRY_VERIFICA_DISATTIVAZIONE_UTENTE";
    public static final String IDP_QRY_REGISTRA_EVENTO_UTENTE = "QRY_REGISTRA_EVENTO_UTENTE";

    public static final String APP_CHIAMANTE = "SACER_PREINGEST";
    public static final String DB_TRUE = "1";
    public static final String DB_FALSE = "0";
    //
    public static final String ZIP_XML_SEPARATOR = "^";
    // Costanti dei prefissi dei nomi file di recupero
    public static final String UD_FILE_PREFIX_SACER = "UD_";
    public static final String UD_FILE_PREFIX_PREINGEST = "UD_";
    public static final String PC_FILE_PREFIX_SACER = "PC-UD_";
    public static final String PC_FILE_PREFIX_PREINGEST = "PC_";
    public static final String ZIP_EXTENSION = ".zip";
    public static final String COD_VERS_ERR_CHIAVE_DUPLICATA_OLD = "UD-002"; // codice in uso fino a
									     // metà del 2013
    public static final String COD_VERS_ERR_CHIAVE_DUPLICATA_NEW = "UD-002-001"; // codice
										 // aggiornato,
										 // attivo dalla
										 // seconda metà del
										 // 2013

    // Costanti Gestione Job
    public static final String DATE_FORMAT_JOB = "dd/MM/yyyy HH:mm:ss";
    public static final String DATE_FORMAT = "dd/MM/yyyy";
    // Costanti PigObjectTrasf
    public static final String DS_PATH_TRASF_NON_DEFINITO = "NON_DEFINITO";
    // Costanti per scarto scadenza password utente
    public static final String NUM_GIORNI_ESPONI_SCAD_PSW = "NUM_GIORNI_ESPONI_SCAD_PSW";

    public static final String JPA_PORPERTIES_TIMEOUT = "javax.persistence.lock.timeout";

    // Parti comuni
    public static final String OBJECT_STORAGE_ADDR = "OBJECT_STORAGE_ADDR";
    public static final String TENANT_OBJECT_STORAGE = "TENANT_OBJECT_STORAGE";
    public static final String URL_ASSOCIAZIONE_UTENTE_CF = "URL_ASSOCIAZIONE_UTENTE_CF";
    public static final String URL_BACK_ASSOCIAZIONE_UTENTE_CF = "URL_BACK_ASSOCIAZIONE_UTENTE_CF";

    public static final String S3_CLIENT_CONNECTION_TIMEOUT = "S3_CLIENT_CONNECTION_TIMEOUT";

    public static final String S3_CLIENT_MAX_CONNECTIONS = "S3_CLIENT_MAX_CONNECTIONS";

    public static final String S3_CLIENT_SOCKET_TIMEOUT = "S3_CLIENT_SOCKET_TIMEOUT";

    public enum Sex {
	M, F
    }

    public enum StatoOggetto {

	ANNULLATO, CHIUSO_ERR_CODA, CHIUSO_ERR_CRASH_DPI, CHIUSO_ERR_CRASH_FS_PRIM,
	CHIUSO_ERR_CRASH_FS_SECOND, CHIUSO_ERR_CRASH_FTP, CHIUSO_ERR_NOTIF, CHIUSO_ERR_SCHED,
	CHIUSO_ERR_RECUPERABILE, CHIUSO_ERR_VERS, CHIUSO_ERR_VERSAMENTO_A_PING,
	CHIUSO_ERR_TRASFORMAZIONE, CHIUSO_OK, CHIUSO_WARNING, DA_TRASFORMARE,
	ERRORE_VERSAMENTO_A_PING, ERRORE_TRASFORMAZIONE, IN_ATTESA_FILE, IN_ATTESA_SCHED,
	IN_ATTESA_VERS, IN_CODA_VERS, IN_CORSO_ANNULLAMENTO, PREPARAZIONE_OGG_IN_CORSO, TRASFORMATO,
	TRASFORMAZIONE_NON_ATTIVA, VERSATO_A_PING, WARNING, WARNING_TRASFORMAZIONE,
	TRASFORMAZIONE_IN_CORSO, CHIUSO_ERR_VERIFICA_HASH, IN_CODA_HASH,
	// Stati fake lista riepilogo oggetti versati
	IN_CORSO_VERS_SACER, PROBLEMA_VERS_SACER, CHIUSO_ERR, PROBLEMA_PREPARAZIONE_SIP,
	WARNING_CHIAVE_DUPLICATA;

	public static StatoOggetto[] getEnums(StatoOggetto... vals) {
	    return vals;
	}

	public static StatoOggetto[] getStatiOggettoMonitoraggioListaOggetti() {
	    return getEnums(CHIUSO_OK, IN_ATTESA_FILE, IN_ATTESA_SCHED, IN_ATTESA_VERS,
		    IN_CODA_VERS, CHIUSO_ERR_VERS, CHIUSO_ERR_RECUPERABILE, WARNING, CHIUSO_WARNING,
		    DA_TRASFORMARE, TRASFORMAZIONE_NON_ATTIVA, PREPARAZIONE_OGG_IN_CORSO,
		    TRASFORMAZIONE_IN_CORSO, ERRORE_TRASFORMAZIONE, WARNING_TRASFORMAZIONE,
		    TRASFORMATO, VERSATO_A_PING, ERRORE_VERSAMENTO_A_PING, IN_CORSO_VERS_SACER,
		    PROBLEMA_PREPARAZIONE_SIP, PROBLEMA_VERS_SACER, CHIUSO_ERR_TRASFORMAZIONE,
		    CHIUSO_ERR_VERSAMENTO_A_PING, CHIUSO_ERR_SCHED, IN_CORSO_ANNULLAMENTO,
		    ANNULLATO, WARNING_CHIAVE_DUPLICATA, IN_CODA_HASH, CHIUSO_ERR_VERIFICA_HASH);
	}

	public static StatoOggetto[] getStatiOggettoTrasformazione() {
	    return getEnums(IN_ATTESA_FILE, DA_TRASFORMARE, TRASFORMAZIONE_NON_ATTIVA,
		    PREPARAZIONE_OGG_IN_CORSO, TRASFORMAZIONE_IN_CORSO, ERRORE_TRASFORMAZIONE,
		    WARNING_TRASFORMAZIONE, TRASFORMATO, VERSATO_A_PING, ERRORE_VERSAMENTO_A_PING,
		    IN_CORSO_VERS_SACER, PROBLEMA_VERS_SACER, CHIUSO_ERR_TRASFORMAZIONE,
		    CHIUSO_ERR_VERSAMENTO_A_PING, IN_CORSO_ANNULLAMENTO, ANNULLATO);
	}

	public static StatoOggetto[] getStatiTrasformazione() {
	    return getEnums(IN_ATTESA_FILE, DA_TRASFORMARE, TRASFORMAZIONE_NON_ATTIVA,
		    PREPARAZIONE_OGG_IN_CORSO, TRASFORMAZIONE_IN_CORSO, ERRORE_TRASFORMAZIONE,
		    WARNING_TRASFORMAZIONE, TRASFORMATO, VERSATO_A_PING, ERRORE_VERSAMENTO_A_PING,
		    IN_CORSO_VERS_SACER, PROBLEMA_VERS_SACER, IN_CODA_HASH,
		    PROBLEMA_PREPARAZIONE_SIP);
	}

	public static StatoOggetto[] getStatiNoTrasformazione() {
	    return getEnums(IN_ATTESA_FILE, IN_ATTESA_SCHED, IN_ATTESA_VERS, IN_CODA_VERS, WARNING,
		    IN_CODA_HASH);
	}

    }

    public enum StatoSessioneIngest {
	ANNULLATA, CHIUSO_ERR, CHIUSO_ERR_CODA, CHIUSO_ERR_CRASH_DPI, CHIUSO_ERR_CRASH_FTP,
	CHIUSO_ERR_NOTIF, CHIUSO_ERR_SCHED, CHIUSO_ERR_RECUPERABILE, CHIUSO_ERR_VERS,
	CHIUSO_ERR_VERSAMENTO_A_PING, CHIUSO_ERR_TRASFORMAZIONE, CHIUSO_FORZATA, CHIUSO_OK,
	CHIUSO_WARNING, DA_TRASFORMARE, ERRORE_VERSAMENTO_A_PING, ERRORE_TRASFORMAZIONE,
	IN_ATTESA_FILE, IN_ATTESA_SCHED, IN_ATTESA_VERS, IN_CODA_VERS, IN_CORSO_ANNULLAMENTO,
	PREPARAZIONE_OGG_IN_CORSO, TRASFORMATO, TRASFORMAZIONE_NON_ATTIVA, VERSATO_A_PING, WARNING,
	WARNING_TRASFORMAZIONE, TRASFORMAZIONE_IN_CORSO, CHIUSO_ERR_VERIFICA_HASH, IN_CODA_HASH;

	public static StatoSessioneIngest[] getStatiVersamentiFalliti() {
	    return getEnums(CHIUSO_ERR, CHIUSO_ERR_NOTIF, CHIUSO_ERR_SCHED, CHIUSO_ERR_CODA,
		    CHIUSO_ERR_VERS, CHIUSO_ERR_RECUPERABILE, CHIUSO_ERR_TRASFORMAZIONE,
		    CHIUSO_ERR_VERSAMENTO_A_PING);
	}

	public static StatoSessioneIngest[] getEnums(StatoSessioneIngest... vals) {
	    return vals;
	}
    }

    public enum StatoSessioneRecup {
	CHIUSO_ERR, CHIUSO_ERR_ELIMINATO, CHIUSO_ERR_NOTIFICATO, CHIUSO_ERR_PRELEVATO,
	CHIUSO_ERR_RECUPERATO, CHIUSO_OK, ELIMINATO, IN_ATTESA_PRELIEVO, IN_ATTESA_RECUP,
	RECUPERATO;
    }

    public enum StatoUnitaDocObject {
	ANNULLATA, DA_VERSARE, ERR_CRASH_FS_PRIM, ERR_CRASH_FS_SECOND, IN_CODA_VERS,
	IN_CORSO_ANNULLAMENTO, VERSATA_ERR, VERSATA_OK, VERSATA_TIMEOUT, PREPARA_XML_IN_ERRORE,
	PREPARA_XML_OK
    }

    public enum StatoUnitaDocSessione {
	ANNULLATA, DA_VERSARE, IN_CODA_VERS, IN_CORSO_ANNULLAMENTO, VERSATA_ERR, VERSATA_OK,
	VERSATA_TIMEOUT, PREPARA_XML_IN_ERRORE;
    }

    public enum TipoClasseVersamento {
	DA_TRASFORMARE, NON_DA_TRASFORMARE
    }

    public enum TipoVersamento {

	NO_ZIP, ZIP_CON_XML_SACER, ZIP_NO_XML_SACER, DA_TRASFORMARE;

	public static TipoVersamento[] getEnums(TipoVersamento... vals) {
	    return vals;
	}

	public static TipoVersamento[] getTipoVersamentoTrasf() {
	    return getEnums(DA_TRASFORMARE);
	}

	public static TipoVersamento[] getTipoVersamentoNoTrasf() {
	    return getEnums(NO_ZIP, ZIP_CON_XML_SACER, ZIP_NO_XML_SACER);
	}
    }

    public enum TipoGestioneOggettiFigli {
	AUTOMATICA, MANUALE;

	public static TipoGestioneOggettiFigli[] getEnums(TipoGestioneOggettiFigli... vals) {
	    return vals;
	}

	public static TipoGestioneOggettiFigli[] getTipoGestioneOggettiFigli() {
	    return getEnums(AUTOMATICA, MANUALE);
	}
    }

    public enum EsitoServizio {
	OK, KO, WARN, NO_RISPOSTA
    }

    public enum HashCalcType {
	FILE_HASH_DICOM, NOTIFICATO
    }

    public enum TipoCalcolo {
	CALC_DICOM, CARTELLA_ZIP, XML_VERS
    }

    public enum TipoRicerca {
	DIARIO, RESTIT
    }

    public enum TipiHash {
	SCONOSCIUTO("SCONOSCIUTO", -1), MD5("MD5", 16), SHA_1("SHA-1", 20), SHA_224("SHA-224", 28),
	SHA_256("SHA-256", 32), SHA_384("SHA-384", 48), SHA_512("SHA-512", 64);

	private String desc;
	private int lenght;

	private TipiHash(String ds, int ln) {
	    desc = ds;
	    lenght = ln;
	}

	public String descrivi() {
	    return desc;
	}

	public int lunghezza() {
	    return lenght;
	}

	public static TipiHash evaluateByLenght(int lenght) {
	    for (TipiHash hash : values()) {
		if (hash.lunghezza() == lenght) {
		    return hash;
		}
	    }
	    return SCONOSCIUTO;
	}

	public static TipiHash evaluateByDesc(String desc) {
	    for (TipiHash hash : values()) {
		if (hash.descrivi().equals(desc)) {
		    return hash;
		}
	    }
	    return SCONOSCIUTO;
	}

	public static String alldesc() {
	    StringBuilder all = new StringBuilder();
	    for (TipiHash hash : values()) {
		if (!hash.equals(SCONOSCIUTO)) {
		    all.append(hash.descrivi());
		    all.append(",");
		}
	    }
	    // remove last one
	    all.delete(0, all.length() - 1);
	    return all.toString();
	}
    }

    public enum TipiEncBinari {

	SCONOSCIUTO("SCONOSCIUTO"), HEX_BINARY("hexBinary"), // questo è l'unico valore ammissibile
	BASE64("BASE64");

	private String desc;

	private TipiEncBinari(String ds) {
	    desc = ds;
	}

	public String descrivi() {
	    return desc;
	}

	public static TipiEncBinari evaluateByDesc(String desc) {
	    for (TipiEncBinari bin : values()) {
		/*
		 * equalsIgnoreCase = dato che non esiste una codifica "forte" il chiamante (e.g.
		 * ping che utilizza BASE64 e non Base64 come sui ws, ma non ha importanza la
		 * sintassi quanto la semantica ...)
		 */
		if (bin.descrivi().equalsIgnoreCase(desc)) {
		    return bin;
		}
	    }
	    return TipiEncBinari.SCONOSCIUTO;
	}
    }

    public enum TipiEntita {
	OBJ, FILE
    }

    public enum TipoPartizione {
	OBJ, SES
    }

    public enum TipiRegLogJob {
	ERRORE, FINE_SCHEDULAZIONE, INIZIO_SCHEDULAZIONE
    }

    public enum NomiJob {
	PREPARA_XML, PRODUCER_CODA_VERIFICA_H, PRODUCER_CODA_VERS, RECUPERA_ERRORI_IN_CODA,
	RECUPERA_VERS_ERR, RECUPERO_SACER, ALLINEAMENTO_ORGANIZZAZIONI, WS_MONITORAGGIO_STATUS,
	ESEGUI_TRASFORMAZIONE, ALLINEA_ENTI_CONVENZIONATI, VERIFICA_DOCUMENTI_STRUMENTI_URBANISTICI,
	INVIO_STRUMENTI_URBANISTICI, VERIFICA_DOCUMENTI_SISMA, INVIO_SISMA,
	INVIA_OGGETTI_GENERATI_A_PING, ALLINEAMENTO_LOG, INIZIALIZZAZIONE_LOG;

	public static NomiJob[] getEnums(NomiJob... vals) {
	    return vals;
	}

	public static NomiJob[] getComboSchedulazioniJob() {
	    return getEnums(PREPARA_XML, PRODUCER_CODA_VERIFICA_H, PRODUCER_CODA_VERS,
		    RECUPERA_ERRORI_IN_CODA, RECUPERA_VERS_ERR, RECUPERO_SACER,
		    ALLINEAMENTO_ORGANIZZAZIONI, ESEGUI_TRASFORMAZIONE,
		    INVIA_OGGETTI_GENERATI_A_PING, VERIFICA_DOCUMENTI_STRUMENTI_URBANISTICI,
		    INVIO_STRUMENTI_URBANISTICI, INVIO_SISMA, ALLINEAMENTO_LOG,
		    INIZIALIZZAZIONE_LOG);
	}
    }

    public enum EsitoVersamento {
	POSITIVO, NEGATIVO, WARNING
    }

    public static final String STUDIO_DICOM = "StudioDicom";
    public static final String TIPO_ZIP = "Tipo Zip";

    public enum TipiOggetto {

	STUDIO_DICOM(Constants.STUDIO_DICOM), TIPO_ZIP(Constants.TIPO_ZIP), ALTRO("");

	private String valore;

	private TipiOggetto(String val) {
	    this.valore = val;
	}

	public String getValue() {
	    return valore;
	}

	@Override
	public String toString() {
	    return this.valore;
	}

	public static TipiOggetto getEnum(String value) {
	    if (value == null) {
		return null;
	    }
	    for (TipiOggetto v : values()) {
		if (value.equalsIgnoreCase(v.getValue())) {
		    return v;
		}
	    }
	    return ALTRO;
	}
    }

    public static final String PRINCIPALE = "PRINC";
    public static final String ALLEGATO = "ALLEG_";
    public static final String ANNESSO = "ANNESSO_";
    public static final String ANNOTAZIONE = "ANNOT_";

    public enum DocTypeEnum {

	PRINCIPALE(Constants.PRINCIPALE), ALLEGATO(Constants.ALLEGATO), ANNESSO(Constants.ANNESSO),
	ANNOTAZIONE(Constants.ANNOTAZIONE);

	private String valore;

	private DocTypeEnum(String val) {
	    this.valore = val;
	}

	public String getValue() {
	    return valore;
	}

	@Override
	public String toString() {
	    return this.valore;
	}

	public static DocTypeEnum getEnum(String value) {
	    DocTypeEnum result = null;
	    if (value == null) {
		return null;
	    }
	    for (DocTypeEnum v : values()) {
		if (value.equalsIgnoreCase(v.getValue())) {
		    result = v;
		    break;
		}
	    }
	    return result;
	}
    }

    public enum TipiXmlSacer {
	XML_INDICE, XML_VERS
    }

    public enum TipiXmlAnnul {
	RICHIESTA, RISPOSTA
    }

    public enum AttribDatiSpecDataType {
	ALFANUMERICO, DATA, DATETIME, NUMERICO
    }

    public static final String RECUPERO_UD_SERVICE = "RecuperoUnitaDocumentariaSync";
    public static final String RECUPERO_PC_SERVICE = "RecProveConservSync";

    public enum ServizioRecupero {

	RECUPERO_UD_SERVICE(Constants.RECUPERO_UD_SERVICE),
	RECUPERO_PC_SERVICE(Constants.RECUPERO_PC_SERVICE);

	private String valore;

	private ServizioRecupero(String val) {
	    this.valore = val;
	}

	public String getValue() {
	    return valore;
	}

	@Override
	public String toString() {
	    return this.valore;
	}

	public static ServizioRecupero getEnum(String value) {
	    ServizioRecupero result = null;
	    if (value == null) {
		return null;
	    }
	    for (ServizioRecupero v : values()) {
		if (value.equalsIgnoreCase(v.getValue())) {
		    result = v;
		    break;
		}
	    }
	    return result;
	}
    }

    public enum StatoVerificaHash {
	IN_CODA, OK, KO
    }

    public enum TiOperReplic {
	INS, MOD, CANC
    }

    public enum TipoDato {
	TIPO_OBJECT
    }

    public enum TiStatoReplic {
	DA_REPLICARE, REPLICA_OK, REPLICA_NON_POSSIBILE, REPLICA_IN_ERRORE, REPLICA_IN_TIMEOUT
    }

    public enum NmOrganizReplic {
	AMBIENTE, VERSATORE
    }

    // vista da cui recuperare i valori
    public enum TipoPigVGetValAppart {
	AMBIENTEVERS, VERS, TIPOOBJECT, APPLIC;

	public static TipoPigVGetValAppart next(TipoPigVGetValAppart last) {
	    switch (last) {
	    case TIPOOBJECT:
		return VERS;
	    case VERS:
		return AMBIENTEVERS;
	    case AMBIENTEVERS:
		return APPLIC;
	    default:
		return null;
	    }
	}
    }

    public enum TiAppart {
	AMBIENTE, VERS, TIPO_OGGETTO, APPLIC
    }

    // COSTANTI DI ERRORE INSERIMENTO, MODIFICA, CANCELLAZIONE ORGANIZZAZIONE MANDATI IN RISPOSTA
    // DAL WS (SacerIAM)
    public static final String SERVIZI_ORG_001 = "SERVIZI-ORG-001";
    public static final String SERVIZI_ORG_002 = "SERVIZI-ORG-002";
    public static final String SERVIZI_ORG_003 = "SERVIZI-ORG-003";
    public static final String SERVIZI_ORG_004 = "SERVIZI-ORG-004";
    public static final String SERVIZI_ORG_005 = "SERVIZI-ORG-005";
    public static final String SERVIZI_ORG_006 = "SERVIZI-ORG-006";
    public static final String SERVIZI_ORG_007 = "SERVIZI-ORG-007";
    public static final String REPLICA_ORG_001 = "REPLICA-ORG-001";

    public enum NmParamApplic {
	VERSIONE_XML_RECUP_UD, USERID_RECUP_UD, PSW_RECUP_UD, TIMEOUT_RECUP_UD, URL_RECUP_RAPP_VERS
    }

    // MEV34843 - lista dei parametri per determinare i backend a seconda dell'ambito applicativo.
    public static final String BACKEND_VERSAMENTI = "BACKEND_VERSAMENTI";
    public static final String BACKEND_SISMA = "BACKEND_SISMA";
    public static final String BACKEND_STRUMENTI_URBANISTICI = "BACKEND_STRUMENTI_URBANISTICI";
    public static final String BACKEND_REPORT_TRASFORMAZIONI = "BACKEND_REPORT_TRASFORMAZIONI";

    // MEV #24582 e MEV #21995
    // Proprietà application server
    public static final String OBJECT_STORAGE_ENABLED = "object-storage-enabled";
    // Valori della proprietà dell'app server
    public static final String OBJECT_STORAGE_DISATTIVO = "false";
    public static final String OBJECT_STORAGE_ATTIVO = "true";
    public static final String PARAMETRO_OBJECT_STORAGE_ATTIVO = "attivo";

    public static final String TIPO_VERSATORE_SISMA_SELEZIONATO = "TIPO_VERSATORE_SISMA_SELEZIONATO";
    public static final String TIPO_VERSATORE_SISMA_UTENTE = "TIPO_VERSATORE_SISMA_UTENTE";

    public enum TipoVersatore {
	AGENZIA, SA_PUBBLICO, SA_PRIVATO
    }

    // MEV 26012
    public enum TipoStorage {
	FTP, OS
    }

    // MAC 31076
    public static final String NUM_GIORNI_ESCALATION = "NUM_GIORNI_ESCALATION";
}
