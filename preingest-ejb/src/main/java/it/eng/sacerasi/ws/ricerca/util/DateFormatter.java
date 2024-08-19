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
package it.eng.sacerasi.ws.ricerca.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.sacerasi.ws.ricerca.ricercaDiario.util.RicercaDiarioQueriesBuilder;

/**
 *
 * @author Gilioli_P
 */
public class DateFormatter {

    private static final Logger log = LoggerFactory.getLogger(RicercaDiarioQueriesBuilder.class);

    /**
     * Imposta come orario di una data le 23:59
     *
     * @param dataA
     *            data A
     *
     * @return data formattata tipo {@link Date}
     */
    public static Date formatta2359(String dataA) {
        DateFormat formatter;
        Date dataOraA = null;
        try {
            formatter = new SimpleDateFormat("yyyy-MM-dd");
            dataOraA = (Date) formatter.parse(dataA);

            Calendar c = Calendar.getInstance();
            c.setTime(dataOraA);
            c.set(Calendar.HOUR_OF_DAY, 23);
            c.set(Calendar.MINUTE, 59);
            c.set(Calendar.SECOND, 59);
            dataOraA = c.getTime();

        } catch (ParseException ex) {
            log.error(ex.getMessage(), ex);
        }

        return dataOraA;
    }
}
