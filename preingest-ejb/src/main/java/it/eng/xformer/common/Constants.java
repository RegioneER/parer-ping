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

package it.eng.xformer.common;

/**
 * @author Cappelli_F
 */
public class Constants {

    public enum Stato {
        DA_TRASFORMARE, TRASFORMAZIONE_NON_ATTIVA, TRASFORMATO, ERRORE_TRASFORMAZIONE, IN_ATTESA_FILE, IN_ATTESA_SCHED,
        CHIUSO_OK, VERSATO_A_PING, ERRORE_VERSAMENTO_A_PING, TRASFORMAZIONE_IN_CORSO, PREPARAZIONE_OGG_IN_CORSO,
        WARNING_TRASFORMAZIONE, IN_CODA_HASH
    }

    public enum TipiRegLogJob {
        ERRORE, FINE_SCHEDULAZIONE, INIZIO_SCHEDULAZIONE
    }

    public class TipoHash {

        private TipoHash() {
            throw new IllegalStateException("Classe di utilità");
        }

        public static final String MD5 = "MD5";
        public static final String SHA_1 = "SHA-1"; // questo è l'unico valore ammissibile
        public static final String SHA_256 = "SHA-256";
    }

    public enum Flag {

        SI("1"), NO("0");

        private final String value;

        private Flag(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    public static final int XF_OUTPUT_FILE_BASE_NAME_MAX_LENGTH = 70;

    public static final String ROOT_FTP = "ROOT_FTP";
    public static final String ROOT_TRASF = "ROOT_TRASFORMAZIONI";
    public static final String STANDARD_PACKAGE_EXTENSION = ".zip";
    public static final String SERVER_NAME_SYSTEM_PROPERTY = "SERVER_NAME_SYSTEM_PROPERTY";

    // Costanti Gestione Job
    public static final String DATE_FORMAT_JOB = "dd/MM/yyyy HH:mm:ss";
    public static final String XFORMER_TI_PARAM_APPLIC = "XFORMER";
    public static final String XFO_WORK_DIR_PARAM_NAME = "XFO_WORK_DIRECTORY";
    public static final String NUMERO_UNITA_DOC_ZIP = "NUMERO_UNITA_DOC_ZIP";

    // Constants for Transformer
    public static final String ENTITY_PACKAGE_NAME = "it.eng.xformer.entity";
    public static final String ROWBEAN_PACKAGE_NAME = "it.eng.xformer.slite.gen.tablebean";
    public static final String VIEWROWBEAN_PACKAGE_NAME = "it.eng.xformer.slite.gen.viewbean";
    public static final String VIEWENTITY_PACKAGE_NAME = "it.eng.xformer.viewEntity";

    // Nomi dei parametri
    public static final String XF_ENTE = "XF_ENTE";
    public static final String XF_AMBIENTE = "XF_AMBIENTE";
    public static final String XF_STRUTTURA = "XF_STRUTTURA";
    public static final String XF_UTENTE = "XF_UTENTE";
    public static final String XF_OUTPUT_DIR = "XF_OUTPUT_DIR";
    public static final String XF_OUTPUT_FILE_BASE_NAME = "XF_OUTPUT_FILE_BASE_NAME";
    public static final String XF_INPUT_FILE_NAME = "XF_INPUT_FILE_NAME";
    public static final String XF_FORZA_ACCETTAZIONE = "XF_FORZA_ACCETTAZIONE";
    public static final String XF_FORZA_COLLEGAMENTO = "XF_FORZA_COLLEGAMENTO";
    public static final String XF_FORZA_CONSERVAZIONE = "XF_FORZA_CONSERVAZIONE";
    public static final String XF_AUXILIARY_FILES_DIR = "XF_AUXILIARY_FILES_DIR";
    public static final String XF_OBJECT_STORAGE_URL = "XF_OBJECT_STORAGE_URL";
    public static final String XF_OBJECT_STORAGE_USER = "XF_OBJECT_STORAGE_USER";
    public static final String XF_OBJECT_STORAGE_PASSWORD = "XF_OBJECT_STORAGE_PASSWORD";
    public static final String XF_OBJECT_STORAGE_KEY = "XF_OBJECT_STORAGE_KEY";
    public static final String XF_OBJECT_STORAGE_BUCKET = "XF_OBJECT_STORAGE_BUCKET";
    public static final String XF_TMP_DIR = "XF_TMP_DIR";
    public static final String XF_REPORT_ID = "XF_REPORT_ID";
    // MEV31648
    public static final String XF_KETTLE_DB_HOST = "XF_KETTLE_DB_HOST";
    public static final String XF_KETTLE_DB_NAME = "XF_KETTLE_DB_NAME";
    public static final String XF_KETTLE_DB_PASSWORD = "XF_KETTLE_DB_PASSWORD";
    public static final String XF_KETTLE_DB_PORT = "XF_KETTLE_DB_PORT";
    public static final String XF_KETTLE_DB_USER = "XF_KETTLE_DB_USER";
    public static final String XF_DB_TABLE_ID = "XF_DB_TABLE_ID";

    // Nomi dei parametri per inviaOggettiGeneratiAPing
    public static final String IOGP_USER = "USER_INVIO_ASYNC";
    public static final String IOGP_PSW = "PSW_INVIO_ASYNC";
    public static final String IOGP_TIMEOUT = "TIMEOUT_INVIO_ASYNC";
    public static final String IOGP_URL = "URL_INVIO_ASYNC";
    public static final String NT_URL = "URL_NOTIF_TRASF";

    // Errori codificati
    public static final int ERRORE_TIMEOUT_TRASFORMAZIONE = -42;

    // nomi dei parametri necessari per interagire con kettle server
    public static final String XFO_KETTLE_WS_ENDPOINT = "KETTLE_WS_ENDPOINT";
    public static final String NM_INSTANZA_KETTLE_SERVER = "NM_INSTANZA_KETTLE_SERVER";

    // nomi dei parametri relativi allo storage su s3 per i report di trasformazione
    public static final String OBJECT_STORAGE_ADDR = "OBJECT_STORAGE_ADDR";
    public static final String XF_S3_ACCESS_KEY_ID = "XF_S3_ACCESS_KEY_ID";
    public static final String XF_S3_SECRET_KEY = "XF_S3_SECRET_KEY";
    public static final String BUCKET_REPORT_XFORMER = "BUCKET_REPORT_XFORMER";

    public static final String REPORT_XSL_FILE_LOCATION = "/report.xsl";

    public static final String XF_ERROR_CODE = "XF_ERR";
    public static final String XF_WARNING_CODE = "XF_WAR";

    public static final String PING_ERRSSISMA27 = "PING-ERRSSISMA27";
}
