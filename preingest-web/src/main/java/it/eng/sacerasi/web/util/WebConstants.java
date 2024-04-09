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

package it.eng.sacerasi.web.util;

public class WebConstants {

    public static final int DEFAULT_PAGE_SIZE = 10;
    public static final int FILESIZE = 1024;
    public static final String MIME_TYPE_GENERIC = "application/octet-stream";

    public enum DOWNLOAD_ATTRS {

        DOWNLOAD_ACTION, DOWNLOAD_FILENAME, DOWNLOAD_FILEPATH, DOWNLOAD_DELETEFILE, DOWNLOAD_CONTENTTYPE
    }

    public enum ComboFlag {

        SI(DB_TRUE), NO(DB_FALSE);

        String value;

        ComboFlag(String value) {
            this.value = value;
        }

        public String getValue() {
            return this.value;
        }
    }

    public enum organizzazione {

        AMBIENTE, VERSATORE, VERSATORE_LINK
    }

    public enum conservazione {

        VERSAMENTO_ANTICIPATO, MIGRAZIONE, FISCALE, VERSAMENTO_IN_ARCHIVIO
    }

    public enum tiDatatype {

        ALFANUMERICO, DATA, DATETIME, NUMERICO
    }

    public enum tiStatoRisoluz {

        RISOLTO, IN_CORSO, WARNING, CHIUSO_WARNING, NON_RISOLTO, NON_RISOLUBILE, NON_RISOLTO_VERIFICATO,
        NON_RISOLTO_NON_VERIFICATO;

        public static tiStatoRisoluz[] getEnums(tiStatoRisoluz... vals) {
            return vals;
        }

        public static tiStatoRisoluz[] getStatiRisoluzione() {
            return getEnums(RISOLTO, IN_CORSO, WARNING, CHIUSO_WARNING, NON_RISOLTO);
        }

        public static tiStatoRisoluz[] getStatiCalcoloRiepilogo() {
            return getEnums(RISOLTO, IN_CORSO, WARNING, CHIUSO_WARNING, NON_RISOLUBILE, NON_RISOLTO_VERIFICATO,
                    NON_RISOLTO_NON_VERIFICATO);
        }

    }

    public enum SezioneMonitoraggio {

        RIEPILOGO_VERSATORE, RIEPILOGO_VERSAMENTI, FILTRI_DOCUMENTI, FILTRI_VERSAMENTI, FILTRI_DOCUMENTI_NON_VERS,
        SESSIONI_ERRATE, OPERAZIONI_VOLUMI, CONTENUTO_SACER, JOB_SCHEDULATI, REPLICA_ORG
    }

    public enum vistaListaRiepilogoVersamenti {

        OBJ_RANGE_DT, SES_RANGE_DT, OBJ_NON_VERS
    }

    public enum tiErr {

        INVIO_OGGETTO, NOTIFICA_FILE, PREPARAZIONE_XML, REGISTRAZIONE_IN_CODA, VERSAMENTO_SACER
    }

    public enum fieldsTipoFileObj {

        nm_tipo_doc_sacer, ti_doc_sacer, nm_tipo_strut_doc_sacer, nm_tipo_comp_doc_sacer, nm_fmt_file_vers_sacer,
        fl_ver_firma_fmt_sacer, nm_fmt_file_calc_sacer, ds_fmt_rappr_esteso_calc_sacer, ds_fmt_rappr_calc_sacer,
        fl_calc_hash_sacer
    }

    // Costanti per Monitoraggio
    public final static String CORR = "CORR";
    public final static String SEI_GG_PREC_CORR = "6_GG_PREC_CORR";
    public final static String PREC_SEI_GG_PREC_CORR = "PREC_6_GG_PREC_CORR";
    public static final String DB_TRUE = "1";
    public static final String DB_FALSE = "0";

    // Strumenti urbanistici verifica documenti
    public enum PARAMETER_JSON_VERIFICA_DOCUMENTI_STRUM_URB {

        ID_STRUMENTO_URBANISTICO, OGGETTO, VERIFICA_TERMINATA, STOP_POLL, LISTA_DOCUMENTI_CON_ERRORI,
        DOCUMENTI_CON_ERRORI
    }

    // Sisma verifica documenti
    public enum PARAMETER_JSON_VERIFICA_DOCUMENTI_SISMA {

        ID_SISMA, OGGETTO, VERIFICA_TERMINATA, STOP_POLL, LISTA_DOCUMENTI_CON_ERRORI, DOCUMENTI_CON_ERRORI
    }

}
