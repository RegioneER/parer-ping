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

package it.eng.sacerasi.web.util;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.WordUtils;

/**
 *
 * @author Bonora_L
 */
public class Utils {

    private final static String CARATTERI_AMMESSI_PER_NOME_FILE = "[^A-Za-z0-9\\. _-]";
    private final static String CARATTERI_PUNTEGGIATURA_IN_NOME_FILE = "[., ]";
    private final static String CARATTERI_SOSTITUTIVO_PER_NOME_FILE = "_";

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

    public static String convertSnakeCaseToCamelCase(String word) {
        String camelCaseString = word.toLowerCase();
        camelCaseString = WordUtils.capitalizeFully(camelCaseString, '_');
        camelCaseString = StringUtils.remove(camelCaseString, '_');
        camelCaseString = StringUtils.uncapitalize(camelCaseString);
        return camelCaseString;
    }

    /*
     * Restituisce una stringa formattata con i MB o i GB in base a quanto è grande il numero
     */
    public static String convertBytesToFormattedString(BigDecimal dimensione) {
        /*
         * if(bytes == 0) return '0 Bytes';
         * 
         * var k = 1000, dm = decimalPoint || 2, sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB'], i =
         * Math.floor(Math.log(bytes) / Math.log(k)); return parseFloat((bytes / Math.pow(k, i)).toFixed(dm)) + ' ' +
         * sizes[i];
         */
        String dimen = "0 Bytes";
        if (dimensione != null && !dimensione.equals(BigDecimal.ZERO)) {
            double k = 1000;
            // long decimalPoint
            String sizes[] = { "Bytes", "KB", "MB", "GB", "TB", "PB", "EB", "ZB", "YB" };
            double i = Math.floor(Math.log(dimensione.doubleValue()) / Math.log(k));
            // dimen=new String(""+(dimensione.doubleValue() / Math.pow(k,i)) + sizes[(int)i] );
            dimen = String.format("%.2f %s", (dimensione.doubleValue() / Math.pow(k, i)), sizes[(int) i]);
        }
        /*
         * VECCHIO String dimen="0 MB"; if (dimensione!=null) { if(dimensione.longValueExact()<1000000000) {
         * dimen=String.format("%.2f MB",dimensione.divide(new BigDecimal(1000000))); } else {
         * dimen=String.format("%.2f GB",dimensione.divide(new BigDecimal(1000000000))); } }
         */
        return dimen;
    }

    /*
     * Restituisce una stringa formattata con i MB
     */
    public static BigDecimal convertBytesToMb(BigDecimal dimensione) {
        BigDecimal dimenMb = BigDecimal.ZERO;
        if (dimensione != null && !dimensione.equals(BigDecimal.ZERO)) {
            dimenMb = BigDecimal.valueOf(dimensione.doubleValue() / Math.pow(1000, 2));
        }
        return dimenMb;
    }

    /*
     * Restituisce un nome di file eliminando caratteri strani per poterlo salvare correttamente su filesystem
     */
    public static String normalizzaNomeFile(String nomeFile) {
        return nomeFile.replaceAll(CARATTERI_AMMESSI_PER_NOME_FILE, CARATTERI_SOSTITUTIVO_PER_NOME_FILE);
    }

    public static String EliminaPunteggiatureSpaziNomeFile(String nomeFile) {
        return nomeFile.replaceAll(CARATTERI_PUNTEGGIATURA_IN_NOME_FILE, CARATTERI_SOSTITUTIVO_PER_NOME_FILE);
    }

}
