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

package it.eng.sacerasi.job.allineamentoEntiConvenzionati.util;

/**
 *
 * @author Gilioli_P
 */
public class CostantiAllineaEntiConv {

    // COSTANTI DI ERRORE MANDATI IN RISPOSTA DAL WS (SacerIAM)
    public static final String SERVIZI_ENTE_001 = "SERVIZI-ENTE-001"; // Errore nella creazione del
								      // client per la
								      // chiamata al WS di
								      // AllineamentoEnteConvenzionato
    public static final String SERVIZI_ENTE_002 = "SERVIZI-ENTE-002"; // Utente non riconosciuto o
								      // non abilitato
    public static final String ALLINEA_ENTE_001 = "ALLINEA-ENTE-001"; // Non risponde o errore
								      // sistema
    public static final String ERR_666 = "666";

    public enum TiOperAllinea {
	ALLINEA
    }

    public enum TiStatoAllinea {
	DA_ALLINEARE, ALLINEA_OK, ALLINEA_IN_ERRORE, ALLINEA_NON_POSSIBILE, ALLINEA_IN_TIMEOUT
    }

    public enum EsitoServizio {
	OK, KO, NO_RISPOSTA
    }

}
