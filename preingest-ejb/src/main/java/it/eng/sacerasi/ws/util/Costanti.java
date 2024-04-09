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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.eng.sacerasi.ws.util;

/**
 *
 * @author Fioravanti_F
 */
public class Costanti {

    //
    public static final String WS_INVIO_OGGETTO_ASYNC_VRSN = "1.0";
    public static final String WS_NOTIFICA_TRASF_VRSN = "1.0";
    public static final String WS_RICERCA_DIARIO_VRSN = "1.0";
    public static final String WS_RICERCA_RESTIT_VRSN = "1.0";
    public static final String WS_RICH_CHIUS_WARN_VRSN = "1.0";
    public static final String WS_RICH_REST_OGGETTO_VRSN = "1.0";
    public static final String WS_NOTIFICA_PREL_VRSN = "1.0";
    public static final String WS_NOTIFICA_IN_ATTESA_PREL_VRSN = "1.0";
    public static final String WS_RICH_SOPCLASS_LIST_VRSN = "1.0";
    public static final String WS_PULIZIA_NOTIF_VRSN = "1.0";
    public static final String WS_CANCELLA_UTENTE_VRSN = "1.0";
    public static final String WS_MODIFICA_UTENTE_VRSN = "1.0";
    public static final String WS_INSERIMENTO_UTENTE_VRSN = "1.0";
    public static final String WS_RECUPERO_STATO_OGGETTO_VRSN = "1.0";
    public static final String WS_STATUS_MONITOR_VRSN = "1.0";
    //
    public static final String[] WS_INVIO_OGGETTO_ASYNC_COMP = { "1.0" };
    public static final String[] WS_NOTIFICA_TRASF_COMP = { "1.0" };
    public static final String[] WS_RICERCA_DIARIO_COMP = { "1.0" };
    public static final String[] WS_RICERCA_RESTIT_COMP = { "1.0" };
    public static final String[] WS_RICH_CHIUS_WARN_COMP = { "1.0" };
    public static final String[] WS_RICH_REST_OGGETTO_COMP = { "1.0" };
    public static final String[] WS_NOTIFICA_PREL_COMP = { "1.0" };
    public static final String[] WS_NOTIFICA_IN_ATTESA_PREL_COMP = { "1.0" };
    public static final String[] WS_RICH_SOPCLASS_LIST_COMP = { "1.0" };
    public static final String[] WS_PULIZIA_NOTIF_COMP = { "1.0" };
    public static final String[] WS_CANCELLA_UTENTE_COMP = { "1.0" };
    public static final String[] WS_MODIFICA_UTENTE_COMP = { "1.0" };
    public static final String[] WS_INSERIMENTO_UTENTE_COMP = { "1.0" };
    public static final String[] WS_RECUPERO_STATO_OGGETTO_COMP = { "1.0" };
    public static final String[] WS_STATUS_MONITOR_COMP = { "1.0" };
    //
    public static final String WS_INVIO_OGGETTO_ASYNC = "InvioOggettoAsincrono";
    public static final String WS_NOTIFICA_TRASF_ASYNC = "NotificaTrasferimentoFile";
    public static final String WS_RICERCA_DIARIO_ASYNC = "RicercaDiario";
    public static final String WS_RICERCA_RESTIT_ASYNC = "ricercaRestituzioniOggetti";
    public static final String WS_RICH_CHIUS_WARN_ASYNC = "RichiestaChiusuraWarning";
    public static final String WS_RICH_REST_OGGETTO_ASYNC = "RichiestaRestituzioneOggetto";
    public static final String WS_NOTIFICA_PREL_ASYNC = "NotificaPrelievo";
    public static final String WS_NOTIFICA_IN_ATTESA_PREL_ASYNC = "NotificaInAttesaPrelievo";
    public static final String WS_RICH_SOPCLASS_LIST_ASYNC = "RichiestaSopClassList";
    public static final String WS_PULIZIA_NOTIF_ASYNC = "PuliziaNotificato";
    public static final String WS_CANCELLA_UTENTE_ASYNC = "CancellaUtente";
    public static final String WS_MODIFICA_UTENTE_ASYNC = "ModificaUtente";
    public static final String WS_INSERIMENTO_UTENTE_ASYNC = "InserisciUtente";
    public static final String WS_RECUPERO_STATO_OGGETTO_ASYNC = "RecuperoStatoOggetto";
    public static final String WS_STATUS_MONITOR_NOME = "StatusMonitor";
    //
    //
    //
    public final static String PING = "PING";

    // payload Type
    public final static String PAYLOAD_TYPE_CODA_VERS = "PayloadCodaVersamento";
    public final static String PAYLOAD_TYPE_VERIFICAH = "PayloadCodaVerificaH";

    public enum ModificatoriWS {
        // TAG_VERIFICA_FORMATI_OLD,
        // TAG_VERIFICA_FORMATI_1_25,
        // TAG_MIGRAZIONE,
        // TAG_DATISPEC_EXT,
        // TAG_ESTESI_1_3_OUT // ID documento, tag Versatore
    }

    public enum AttribDatiSpecDataType {

        ALFANUMERICO, NUMERICO, DATA, DATETIME
    }

    //
    public class JMSMsgProperties {

        // msg properties
        public final static String MSG_K_QUEUETYPE = "queueType";
        public final static String MSG_K_APP = "fromApplication";

        // payload Type
        public final static String MSG_K_PAYLOAD_TYPE = "PayloadType";

    }
}
