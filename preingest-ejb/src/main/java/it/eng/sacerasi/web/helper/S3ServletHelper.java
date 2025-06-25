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

package it.eng.sacerasi.web.helper;

import it.eng.paginator.util.HibernateUtils;
import it.eng.sacerasi.entity.PigAmbienteVers;
import it.eng.sacerasi.entity.PigTipoObject;
import it.eng.sacerasi.entity.PigVers;
import it.eng.sacerasi.slite.gen.tablebean.PigAmbienteVersTableBean;
import it.eng.sacerasi.slite.gen.tablebean.PigTipoObjectTableBean;
import it.eng.sacerasi.slite.gen.tablebean.PigVersRowBean;
import it.eng.sacerasi.slite.gen.tablebean.PigVersTableBean;
import it.eng.sacerasi.web.util.Transform;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Session Bean implementation class ComboHelper Contiene i metodi, per la gestione della persistenza su DB per le
 * operazioni CRUD su oggetti di Sacer Asincrono
 */
@SuppressWarnings("unchecked")
@Stateless
@LocalBean
public class S3ServletHelper {

    public S3ServletHelper() {
        /*
         * per sonar
         *
         */
    }

    private static final Logger log = LoggerFactory.getLogger(S3ServletHelper.class.getName());

    @PersistenceContext(unitName = "SacerAsiJPA")
    private EntityManager entityManager;

    public BigDecimal getIdAmbienteVersatore(BigDecimal idVers) {
        String queryStr = "SELECT u.pigAmbienteVer.idAmbienteVers FROM PigVers u WHERE u.idVers = :idVers ";
        Query query = entityManager.createQuery(queryStr);
        query.setParameter("idVers", HibernateUtils.longFrom(idVers));
        Long res = (Long) query.getSingleResult();
        return new BigDecimal(res);
    }
}
