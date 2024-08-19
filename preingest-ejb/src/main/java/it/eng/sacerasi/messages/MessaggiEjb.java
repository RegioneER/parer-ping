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

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.sacerasi.entity.PigClasseErrore;
import it.eng.sacerasi.entity.PigErrore;
import it.eng.sacerasi.exception.ParerUserError;
import it.eng.sacerasi.slite.gen.tablebean.PigClasseErroreRowBean;
import it.eng.sacerasi.slite.gen.tablebean.PigClasseErroreTableBean;
import it.eng.sacerasi.slite.gen.tablebean.PigErroreRowBean;
import it.eng.sacerasi.slite.gen.tablebean.PigErroreTableBean;
import it.eng.sacerasi.web.util.Transform;

/**
 *
 * @author Bonora_L
 */
@Stateless(mappedName = "MessaggiEjb")
@LocalBean
public class MessaggiEjb {

    @EJB
    private MessaggiHelper helper;
    private static final Logger logger = LoggerFactory.getLogger(MessaggiEjb.class);

    /**
     * Ritorna in base agli stati dati come parametro il tablebean della lista di classi di errore relativa agli stati
     *
     * @param stati
     *            lista degli stati
     *
     * @return il tableBean contenente la lista
     *
     * @throws ParerUserError
     *             errore generico
     */
    public PigClasseErroreTableBean getPigClasseErroreTableBean(List<String> stati) throws ParerUserError {
        PigClasseErroreTableBean table = new PigClasseErroreTableBean();
        List<PigClasseErrore> list;
        if (stati != null && !stati.isEmpty()) {
            if (stati.size() > 1) {
                list = helper.retrieveListaClassi(stati);
            } else {
                list = helper.retrieveListaClassi(stati.get(0));
            }
        } else {
            list = helper.retrieveListaClassi((String) null);
        }
        if (list != null && !list.isEmpty()) {
            try {
                for (PigClasseErrore pigClasseErrore : list) {
                    PigClasseErroreRowBean row = (PigClasseErroreRowBean) Transform.entity2RowBean(pigClasseErrore);
                    row.setString("ds_classe_composita", row.getCdClasseErrore() + " - " + row.getDsClasseErrore());
                    table.add(row);
                }
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                    | IllegalArgumentException | InvocationTargetException ex) {
                String msg = "Errore durante il recupero della lista di classi di errore "
                        + ExceptionUtils.getRootCauseMessage(ex);
                logger.error(msg, ex);
                throw new ParerUserError(msg);
            }
        }
        return table;
    }

    public PigErroreTableBean getPigErroreTableBean(String cdClasseErrore) throws ParerUserError {
        PigErroreTableBean table = new PigErroreTableBean();
        if (StringUtils.isNotBlank(cdClasseErrore)) {
            List<PigErrore> list = helper.retrieveListaErrori(cdClasseErrore);
            if (list != null && !list.isEmpty()) {
                try {
                    for (PigErrore pigErrore : list) {
                        PigErroreRowBean row = (PigErroreRowBean) Transform.entity2RowBean(pigErrore);
                        row.setString("ds_errore_composito",
                                row.getCdErrore() + " - " + StringEscapeUtils.unescapeJava(row.getDsErroreFiltro()));
                        table.add(row);
                    }
                } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException
                        | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                    String msg = "Errore durante il recupero della lista di errori "
                            + ExceptionUtils.getRootCauseMessage(ex);
                    logger.error(msg, ex);
                    throw new ParerUserError(msg);
                }
            }
        }
        return table;
    }
}
