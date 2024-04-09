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

package it.eng.xformer.helper;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Stateless
@LocalBean
public class GenericJobQueryHelper {
    @PersistenceContext(unitName = "SacerAsiJPA")
    protected EntityManager entityManager;

    @SuppressWarnings("unchecked")
    public List<Long> selectPOIDFromQueue(String stato) {
        String queryVers = "SELECT DISTINCT tiObjTrasf.pigVer.idVers FROM PigVersTipoObjectDaTrasf pvt JOIN pvt.pigTipoObjectDaTrasf tiObjTrasf";
        Query preparedStmt = entityManager.createQuery(queryVers);
        List<Long> idVers = preparedStmt.getResultList();

        String query = "SELECT po.idObject FROM PigSessioneIngest ses JOIN ses.pigObject po WHERE po.tiStatoObject = :stato AND po.tiGestOggettiFigli = 'AUTOMATICA' AND po.pigVer.idVers IN (:idVers) AND ses.idSessioneIngest = po.idLastSessioneIngest AND po.tiPriorita IS NOT null ORDER BY po.tiPriorita ASC, ses.dtApertura ASC";
        preparedStmt = entityManager.createQuery(query);
        preparedStmt.setParameter("stato", stato);
        preparedStmt.setParameter("idVers", idVers);

        return preparedStmt.getResultList();
    }

}
