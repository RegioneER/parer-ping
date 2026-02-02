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

package it.eng.sacerasi.exception.error;

public class ErrorCategory {

    // Enum with string constructor and conversion method
    public enum PingErrorCategory {
        INTERNAL_ERROR("INTERNAL_ERROR"), USER_ERROR("USER_ERROR"),
        VALIDATION_ERROR("VALIDATION_ERROR");

        private final String value;

        PingErrorCategory(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static PingErrorCategory fromValue(String value) {
            for (PingErrorCategory category : PingErrorCategory.values()) {
                if (category.getValue().equalsIgnoreCase(value)) {
                    return category;
                }
            }
            throw new IllegalArgumentException("Unknown error category: " + value);
        }
    }
}
