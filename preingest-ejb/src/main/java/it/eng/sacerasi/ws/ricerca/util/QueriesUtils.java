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

package it.eng.sacerasi.ws.ricerca.util;

import java.util.HashMap;
import java.util.Map;

import it.eng.sacerasi.ws.ricerca.enums.OperatoreType;

/**
 *
 * @author Gilioli_P
 *
 *         Classe con alcuni metodi statici utilizzati nella costruzione delle queries di ricerca
 *         diario e restituzione oggetti
 */
public class QueriesUtils {

    public static Map<String, String[]> getMappingOperazione() {
	HashMap<String, String[]> op = new HashMap<>();
	op.put(OperatoreType.UGUALE.name(), new String[] {
		"=", "", "" });
	op.put(OperatoreType.DIVERSO.name(), new String[] {
		"!=", "", "" });
	op.put(OperatoreType.MAGGIORE.name(), new String[] {
		">", "", "" });
	op.put(OperatoreType.MAGGIORE_UGUALE.name(), new String[] {
		">=", "", "" });
	op.put(OperatoreType.MINORE.name(), new String[] {
		"<", "", "" });
	op.put(OperatoreType.MINORE_UGUALE.name(), new String[] {
		"<=", "", "" });
	op.put(OperatoreType.INIZIA_PER.name(), new String[] {
		"LIKE", "", "%" });
	op.put(OperatoreType.CONTIENE.name(), new String[] {
		"LIKE", "%", "%" });
	op.put(OperatoreType.NON_CONTIENE.name(), new String[] {
		"NOT LIKE", "%", "%" });
	op.put(OperatoreType.IN.name(), new String[] {
		"IN", "", "" });
	return op;
    }

    // Metodo per "cammellare" una stringa
    public static String toCamelCase(String s) {
	String[] parts = s.split("_");
	String camelCaseString = parts[0];
	for (int i = 1; i < parts.length; i++) {
	    camelCaseString = camelCaseString + toProperCase(parts[i]);
	}
	return camelCaseString;
    }

    public static String toProperCase(String s) {
	return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
    }
}
