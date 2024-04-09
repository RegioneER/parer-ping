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

package it.eng.sacerasi.ws.ricerca.enums;

public enum OperatoreCompresoType {

    // ------------------/
    // - Enum Constants -/
    // ------------------/

    /**
     * Constant COMPRESO_FRA
     */
    COMPRESO_FRA("COMPRESO_FRA");

    /**
     * Field value.
     */
    private final java.lang.String value;

    /**
     * Field enumConstants.
     */
    private static final java.util.Map<java.lang.String, OperatoreCompresoType> enumConstants = new java.util.HashMap<java.lang.String, OperatoreCompresoType>();

    static {
        for (OperatoreCompresoType c : OperatoreCompresoType.values()) {
            OperatoreCompresoType.enumConstants.put(c.value, c);
        }

    };

    private OperatoreCompresoType(final java.lang.String value) {
        this.value = value;
    }

    /**
     * Method fromValue.
     * 
     * @param value
     *            stringa da convertire
     * 
     * @return the constant for this value
     */
    public static OperatoreCompresoType fromValue(final java.lang.String value) {
        OperatoreCompresoType c = OperatoreCompresoType.enumConstants.get(value);
        if (c != null) {
            return c;
        }
        throw new IllegalArgumentException(value);
    }

    /**
     * 
     * 
     * @param value
     *            set del valore
     */
    public void setValue(final java.lang.String value) {
    }

    /**
     * Method toString.
     * 
     * @return the value of this constant
     */
    public java.lang.String toString() {
        return this.value;
    }

    /**
     * Method value.
     * 
     * @return the value of this constant
     */
    public java.lang.String value() {
        return this.value;
    }

}
