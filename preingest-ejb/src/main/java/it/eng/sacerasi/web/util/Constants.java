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
package it.eng.sacerasi.web.util;

/**
 *
 * @author Quaranta_M (40M o "FortyEm")
 */
public class Constants {

    public static final int PASSWORD_EXPIRATION_DAYS = 90;
    // Constants for Transformer
    public static final String ENTITY_PACKAGE_NAME = "it.eng.sacerasi.entity";
    public static final String ROWBEAN_PACKAGE_NAME = "it.eng.sacerasi.slite.gen.tablebean";
    public static final String VIEWROWBEAN_PACKAGE_NAME = "it.eng.sacerasi.slite.gen.viewbean";
    public static final String VIEWENTITY_PACKAGE_NAME = "it.eng.sacerasi.viewEntity";

    public static final String NM_APPLIC = "NM_APPLIC";
    public static final String DS_PREFISSO_PATH = "DS_PREFISSO_PATH";

    public enum NomeCoda {

        producerCodaVersQueue, dmqQueue;
    }

    public enum TipoSelettore {

        CODA1, CODA2, CODA3, CODA_VER_HASH;
    }

    public enum ComboFlag {

        SI("1"), NO("0");

        private String value;

        private ComboFlag(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    public enum ComboFlagOk {

        OK("1"), KO("0");

        private String value;

        private ComboFlagOk(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    public enum TiOperReplic {
        INS, MOD, CANC
    }

    public enum TiStatoReplic {
        DA_REPLICARE, REPLICA_OK, REPLICA_NON_POSSIBILE, REPLICA_IN_ERRORE, REPLICA_IN_TIMEOUT
    }

    public enum TiDichVers {
        AMBIENTE, ENTE, STRUTTURA
    }

    public enum ComboFlagPrioTrasfType {
        IMMEDIATA("0-IMMEDIATA"), ALTA("1-ALTA"), NORMALE("2-NORMALE"), BASSA("3-BASSA");

        private final String value;

        private ComboFlagPrioTrasfType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static String getEnumByString(String code) {
            for (ComboFlagPrioTrasfType e : ComboFlagPrioTrasfType.values()) {
                if (code.equals(e.getValue())) {
                    return e.name();
                }
            }
            return null;
        }
    }

    public enum ComboFlagPrioVersType {
        IMMEDIATA("0-IMMEDIATA"), ALTA("1-ALTA"), NORMALE("2-NORMALE"), BASSA("3-BASSA");

        private final String value;

        private ComboFlagPrioVersType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static String getEnumByString(String code) {
            for (ComboFlagPrioVersType e : ComboFlagPrioVersType.values()) {
                if (code.equals(e.getValue())) {
                    return e.name();
                }
            }
            return null;
        }

        public static String getValueByEnumName(String code) {
            for (ComboFlagPrioVersType e : ComboFlagPrioVersType.values()) {
                if (code.equals(e.name())) {
                    return e.getValue();
                }
            }
            return null;
        }
    }

    // MEV22933
    public enum ComboValueParamentersType {
        STRINGA, PASSWORD;
    }

    public static final String OBFUSCATED_STRING = "********";
}
