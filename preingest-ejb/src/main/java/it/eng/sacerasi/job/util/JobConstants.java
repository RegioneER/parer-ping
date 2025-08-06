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

package it.eng.sacerasi.job.util;

public class JobConstants {

    public static final String DB_TRUE = "1";
    public static final String DB_FALSE = "0";
    public static final String DB_WARNING = "2";

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

    public enum OpTypeEnum {

	INIZIO_SCHEDULAZIONE, FINE_SCHEDULAZIONE, ERRORE
    }

    public enum JobEnum {
	CREAZIONE_ELENCHI_VERS, CREAZIONE_ELENCHI_VERS_FASCICOLI, AGGIORNA_STATO_ARCHIVIAZIONE,
	ELABORA_SESSIONI_RECUPERO, REGISTRA_SCHEDULAZIONI_JOB_TPI, CREAZIONE_INDICI_ELENCHI_VERS,
	CREAZIONE_INDICI_ELENCHI_VERS_FASC, VERIFICA_FIRME_A_DATA_VERS,
	PRODUCER_CODA_INDICI_AIP_DA_ELAB,
	// SCARICO_CA,
	// SCARICO_CRL,
	CALCOLO_CONTENUTO_SACER, CALCOLA_STRUTTURA, CALCOLA_CHIAVE_UD_DOC, CALCOLA_ESTRAZIONE,
	ALLINEAMENTO_ORGANIZZAZIONI, VERIFICA_MASSIVA_VERS_FALLITI, VERIFICA_VERS_FALLITI,
	CREAZIONE_INDICE_AIP, CREAZIONE_INDICE_AIP_FASC, CALCOLO_SERIE, INPUT_SERIE,
	GENERAZIONE_CONTENUTO_EFFETTIVO_SERIE_UD, CONTROLLA_CONTENUTO_SERIE_UD,
	CREAZIONE_AUTOMATICA_SERIE, SET_SERIE_UD_VALIDATA, VERIFICA_COMPATIBILITA_REGISTRO,
	CREAZIONE_INDICE_AIP_SERIE_UD, GENERAZIONE_AUTOMATICA_CONTENUTO_EFFETTIVO,
	CONTROLLO_AUTOMATICO_CONTENUTO_EFFETTIVO,
	// INIZIALIZZAZIONE_LOG_SACER, ALLINEAMENTO_LOG_SACER,
	EVASIONE_RICH_ANNUL_VERS, CALCOLA_STRUTTURA_JOB, EVASIONE_RICH_REST_ARCH,
	CREAZIONE_ELENCHI_INDICI_AIP_UD, CREAZIONE_ELENCHI_INDICI_AIP_FASC,
	CALCOLO_CONTENUTO_FASCICOLI, ALLINEA_ENTI_CONVENZIONATI, VERIFICA_COMPATIBILITA_TIPO_FASC,
	VALIDAZIONE_FASCICOLI, PREPARA_PARTIZIONE_DA_MIGRARE, PREPARA_PARTIZIONE_DA_MIGRARE_1,
	PREPARA_PARTIZIONE_DA_MIGRARE_2, PREPARA_PARTIZIONE_DA_MIGRARE_3,
	PREPARA_PARTIZIONE_DA_MIGRARE_4, PRODUCER_CODA_DA_MIGRARE, PRODUCER_CODA_DA_MIGRARE_1,
	PRODUCER_CODA_DA_MIGRARE_2, PRODUCER_CODA_DA_MIGRARE_3, PRODUCER_CODA_DA_MIGRARE_4,
	VERIFICA_MIGRAZIONE_SUBPARTIZIONE, CONTROLLA_MIGRAZIONE_SUBPARTIZIONE,
	CALCOLO_CONTENUTO_AGGIORNAMENTI_METADATI, CALCOLO_CONSISTENZA
    }

    public enum StatoSessioniRecupEnum {

	CHIUSO_ERR, CHIUSO_OK, ELIMINATO, IN_CORSO
    }

    public enum TipoSessioniRecupEnum {

	DOWNLOAD, SERVIZIO
    }

    public enum StatoDtVersRecupEnum {

	DA_RECUPERARE, ERRORE, RECUPERATA
    }

    public enum ArkStatusEnum {

	ARCHIVIATA, ARCHIVIATA_ERR, DA_ARCHIVIARE, DA_RI_ARCHIVIARE, IN_CORSO_MIGRAZ, REGISTRATA;

	public static ArkStatusEnum[] getEnums(ArkStatusEnum... vals) {
	    return vals;
	}

	public static ArkStatusEnum[] getComboRicercaDtVers() {
	    return getEnums(IN_CORSO_MIGRAZ, REGISTRATA, ARCHIVIATA, ARCHIVIATA_ERR, DA_ARCHIVIARE);
	}
    }

    public enum ArkPath {

	PRIMARIO, SECONDARIO
    }

    public enum RetrieveTpiErrors {

	RETRIEVE_001, RETRIEVE_002, RETRIEVE_003, RETRIEVE_004, RETRIEVE_005, RETRIEVE_006,
	RETRIEVE_007, RETRIEVE_008, RETRIEVE_009, RETRIEVE_010
    }

    public enum StatoSchedJob {

	CONSOLIDATA, REGISTRATA
    }

    public enum LockTypeEnum {

	LOCK_UNICO, LOCK_PER_STRUT
    }
}
