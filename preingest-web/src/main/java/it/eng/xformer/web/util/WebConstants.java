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

package it.eng.xformer.web.util;

public class WebConstants {

    public static final int DEFAULT_PAGE_SIZE = 10;
    public static final int ONE_HUNDRED_PAGE_SIZE = 100;

    public enum DOWNLOAD_ATTRS {
	DOWNLOAD_ACTION, DOWNLOAD_FILENAME, DOWNLOAD_FILEPATH, DOWNLOAD_DELETEFILE,
	DOWNLOAD_CONTENTTYPE
    }

    // public static final int STRUTLIST_PAGE_SIZE = 20;
    // public static final int FORMATI_PAGE_SIZE = 10;
    // public static final int FILESIZE = 1024;
    // public static final int DICH_ABIL_STRUT = 0;
    // public static final int DICH_ABIL_DATI = 1;
    // public static final int CRITERIO_INSERT = 0;
    // public static final int CRITERIO_EDIT = 1;
    // public static final String formato_data = "(gg/mm/aaaa)";
    //
    // public static final String DATE_FORMAT_TIMESTAMP_TYPE = "dd/MM/yyyy HH.mm.ss";
    // public static final String DATE_FORMAT_DATE_TYPE = "dd/MM/yyyy";
    //
    // public enum Organizzazione {
    //
    // AMBIENTE, ENTE, STRUTTURA, AMBIENTE_ID, ENTE_ID, STRUTTURA_ID
    // }
    //
    // public enum XsdType {
    //
    // TIPO_DOC, TIPO_COMP_DOC, TIPO_UNITA_DOC
    // }
    //
    // public enum SignerType {
    //
    // P7M,
    // CADES_BES, CADES_T, CADES_C, CADES_X_Long,
    // XML_DSIG, XADES, XADES_BES, XADES_T, XADES_C, XADES_X, XADES_XL,
    // PDF_DSIG, PADES, PADES_BES, PADES_T, PADES_C,
    // TSR,
    // M7M,
    // P7S
    // }
    //
    // public enum TipoCompDocCombo {
    //
    // CONTENUTO, CONVERTITORE, FIRMA, MARCA, RAPPRESENTAZIONE, SEGNATURA
    // }
    //
    // public enum TipoAmbitoTerritoriale {
    //
    // FORMA_ASSOCIATA("FORMA_ASSOCIATA"), PROVINCIA("PROVINCIA"), REGIONE_STATO("REGIONE/STATO");
    //
    // private String nome;
    //
    // public String getNome() {
    // return nome;
    // }
    //
    // private TipoAmbitoTerritoriale(String nome){
    // this.nome = nome;
    // }
    // }
    //
    // public enum DOWNLOAD_ATTRS {
    //
    // DOWNLOAD_ACTION, DOWNLOAD_FILENAME, DOWNLOAD_FILEPATH, DOWNLOAD_DELETEFILE,
    // DOWNLOAD_CONTENTTYPE
    // }
    //
    // public enum DOWNLOAD_DIP {
    //
    // DIP_RISPOSTA_WS, DIP_RECUPERO_EXT, DIP_ENTITA
    // }
    //
    // // Nome parametri monitoraggio sintetico
    // public static final String PARAMETER_STATO = "ti_stato";
    // public static final String PARAMETER_CREAZIONE = "ti_creazione";
    // public static final String PARAMETER_TIPO = "tipo";
    //
    // public static final String PARAMETER_STATO_TUTTI = "TUTTI";
    // public static final String PARAMETER_CREAZIONE_OGGI = "OGGI";
    // public static final String PARAMETER_CREAZIONE_30GG = "30gg";
    // public static final String PARAMETER_CREAZIONE_B30 = "B30gg";
    // public static final String PARAMETER_TIPO_UD = "UNITA_DOC";
    // public static final String PARAMETER_TIPO_DOC = "DOCUMENTI";
    //
    // public static final String PARAMETER_SESSION_GET_CNT = "SINTETICO_CALCOLA_CONTATORI";
    //
    // // Nome parametri sotto strutture
    // public static final String PARAMETER_EVENT = "event";
    // public static final String PARAMETER_RIGA = "riga";
    //
    // // Nome parametri Dettaglio serie
    // public static final String PARAMETER_VER_SERIE = "VerSerieList";
    //
    // public enum PARAMETER_JSON_FUTURE_SERIE {
    //
    // TIPO_CREAZIONE, CODICE_SERIE, ANNO_SERIE, ID_VERSIONE, RESULT, ID_STRUT
    // }
    //
    // public enum PARAMETER_JSON_FUTURE_SERIE_RESULT {
    //
    // OK, NO_LOCK, ERROR, WORKING
    // }
    //
    // // Nome parametro titolario
    // public enum PARAMETER_TITOLARIO {
    //
    // DATE_TITOLARIO, DATE_TITOLARIO_STRING, LIVELLI_TITOLARIO, NOME_LIVELLI_TITOLARIO,
    // LIVELLI_PARSING,
    // NUM_ORDINE_VOCI, VOCI_MAP, LIVELLI_VOCI
    // }
}
