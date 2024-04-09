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

package it.eng.sacerasi.messages;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.sacerasi.entity.PigErrore;

@Startup
@Singleton(mappedName = "MessaggiWSCache")
public class MessaggiWSCache {

    private static Logger log = LoggerFactory.getLogger(MessaggiWSCache.class);

    @EJB
    MessaggiHelper messaggiHelper;

    Map<String, String> errorMap;

    @PostConstruct
    public void initSingleton() {
        log.info("Inizializzazione singleton MessaggiWSCache...");
        try {
            List<PigErrore> list = messaggiHelper.retrieveListaErrori((String) null);
            errorMap = new HashMap<>();
            for (PigErrore err : list) {
                errorMap.put(err.getCdErrore(), err.getDsErrore());
            }
        } catch (RuntimeException ex) {
            // log.fatal("Inizializzazione singleton MessaggiWSCache fallita! ", ex);
            log.error("Inizializzazione singleton MessaggiWSCache fallita! ", ex);
            throw ex;
        }
        log.info("Inizializzazione singleton MessaggiWSCache... completata.");
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public String getString(String key) {
        return StringEscapeUtils.unescapeJava(errorMap.get(key));
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public String getString(String key, Object... params) {
        return StringEscapeUtils.unescapeJava(MessageFormat.format(errorMap.get(key), params));
    }

}
