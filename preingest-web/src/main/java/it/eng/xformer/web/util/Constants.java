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

package it.eng.xformer.web.util;

/**
 *
 * @author Agati_D
 */
public class Constants {

    public final static int PASSWORD_EXPIRATION_DAYS = 90;
    public final static String TRASFORMAZIONE = "TRASFORMAZIONE";
    // Constants for Transformer
    public final static String ENTITY_PACKAGE_NAME = "it.eng.xformer.entity";
    public final static String ROWBEAN_PACKAGE_NAME = "it.eng.xformer.slite.gen.tablebean";
    public final static String VIEWROWBEAN_PACKAGE_NAME = "it.eng.xformer.slite.gen.viewbean";
    public final static String VIEWENTITY_PACKAGE_NAME = "it.eng.xformer.viewEntity";

    public final static String TRANSFORMATION_PKG_MIME_TYPE = "application/zip";

    public final static String DEFAULT_PARAMENTER_SET_NAME = "Parametri standard";
    public final static String DEFAULT_PARAMENTER_SET_DESCRIPTION = "Set di parametri standard applicati alla trasformazione";
    public static final String DIM_MAX_FILE_DA_VERSARE = "DIM_MAX_FILE_DA_VERSARE";
    public static final String DIM_MAX_FILE_DA_VERSARE_OS = "DIM_MAX_FILE_DA_VERSARE_OS";
    public final static String XFO_MAIN_JOB_FILENAME = "main.kjb";

    public enum ComboFlag {

        SI("1"), NO("0");

        private final String value;

        private ComboFlag(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    public enum ComboFlagParametersSetType {

        ARCHIVISTICO("1"), TECNICO("0");

        private final String value;

        private ComboFlagParametersSetType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    public enum ComboFlagParametersType {
        NUMERICO, ALFANUMERICO, DATA, FLAG;
    }
}
