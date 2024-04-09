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
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.eng.sacerasi.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.joda.time.format.DateTimeFormat;

/**
 *
 * @author Iacolucci_M
 */
public class DateUtil {

    public static final String DATE_FORMAT_SLASH = "dd/MM/yyyy";
    public static final String DATE_FORMAT_MINUS = "dd-MM-yyyy";
    public static final String DATE_FORMAT_SLASH_TIME = "dd/MM/yyyy HH:mm:ss";
    public static final String MAX_DATE_FORMAT_SLASH = "31/12/2444";
    public static final Date MAX_DATE = new Date(
            DateTimeFormat.forPattern(DATE_FORMAT_SLASH).parseMillis(MAX_DATE_FORMAT_SLASH));

    public static String formatDateWithSlash(Date data) {
        if (data == null) {
            return null;
        } else {
            SimpleDateFormat df = new SimpleDateFormat(DATE_FORMAT_SLASH);
            return df.format(data);
        }
    }

    public static String formatDateWithMinus(Date data) {
        SimpleDateFormat df = new SimpleDateFormat(DATE_FORMAT_MINUS);
        return df.format(data);
    }

    public static String formatDateWithSlashAndTime(Date data) {
        SimpleDateFormat df = new SimpleDateFormat(DATE_FORMAT_SLASH_TIME);
        return df.format(data);
    }

    /*
     * Torna una lista di anni compresi nel range passato
     */
    public static List<Integer> getYearsBetween(int number1, int number2) {
        List<Integer> yearsInRange = new ArrayList<>();
        for (int t = number1; t <= number2; t++) {
            yearsInRange.add(t);
        }
        return yearsInRange;
    }

    public static Date getDataInfinita() {
        Calendar c = Calendar.getInstance();
        c.set(2444, Calendar.DECEMBER, 31, 0, 0, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }

    public static Date getDataOdiernaNoTime() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }

    public static String ucContrDate(String aggiunta, Date dtIniVal, Date dtFineVal) {
        String error = null;
        if (dtIniVal == null || dtFineVal == null) {
            error = "Una delle due date " + aggiunta + " risulta essere nulla </br>";
        } else if (dtFineVal.compareTo(dtIniVal) < 0) {
            error = "La data di fine validità " + aggiunta + " non può essere inferiore alla data di inizio validità "
                    + aggiunta + "</br>";
        }
        return error;
    }

    public static String ucContrInclusione(String aggiuntaFiglio, String aggiuntaPadre, Date dtIniValFiglio,
            Date dtFineValFiglio, Date dtIniValPadre, Date dtFineValPadre) {
        String error = null;
        if (dtFineValFiglio == null || dtIniValPadre == null || dtIniValFiglio == null || dtFineValPadre == null) {
            error = "Una delle date " + aggiuntaFiglio + " o " + aggiuntaPadre + " risulta essere nulla </br>";
        } else if (!(dtIniValFiglio.compareTo(dtIniValPadre) >= 0 && dtIniValFiglio.compareTo(dtFineValPadre) <= 0
                && dtFineValFiglio.compareTo(dtIniValPadre) >= 0 && dtFineValFiglio.compareTo(dtFineValPadre) <= 0)) {
            error = "La validità " + aggiuntaFiglio + " non è inclusa nella validità " + aggiuntaPadre + "</br>";
        }
        return error;
    }

}
