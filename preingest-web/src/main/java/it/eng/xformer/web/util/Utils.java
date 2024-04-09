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

import java.util.Collection;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 *
 * @author Bonora_L
 */
public class Utils {

    /**
     * Metodo statico per ordinare un enum tramite il valore
     *
     * @param <T>
     *            ordinamento enum generici
     * @param enumValues
     *            l'array di valori dell'enum
     * 
     * @return la collezione ordinata
     */
    public static <T extends Enum<?>> Collection<T> sortEnum(T[] enumValues) {
        SortedMap<String, T> map = new TreeMap<String, T>();
        for (T l : enumValues) {
            map.put(l.name(), l);
        }
        return map.values();
    }
}
