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

package it.eng.sacerasi.job.ejb;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import it.eng.sacerasi.common.Constants;
import it.eng.sacerasi.entity.PigObject;
import it.eng.sacerasi.entity.PigSessioneRecup;

/**
 *
 * @author Bonora_L
 */
@SuppressWarnings("unchecked")
@Stateless(mappedName = "JobHelper")
@LocalBean
public class JobHelper {

    @PersistenceContext(unitName = "SacerAsiJPA")
    private EntityManager entityManager;

    public List<Long> getListaVersatori() {
	List<Long> lstObjects;
	String queryStr = "SELECT u.idVers FROM PigVers u";
	javax.persistence.Query query = entityManager.createQuery(queryStr);
	lstObjects = query.getResultList();

	return lstObjects;
    }

    public List<PigSessioneRecup> getListaSessioni(List<Long> idVersatori,
	    Constants.StatoSessioneRecup stato) {
	List<PigSessioneRecup> lstSessioni;
	String queryStr = "SELECT u FROM PigSessioneRecup u WHERE u.tiStato = :tiStato AND u.pigVer.idVers IN (:idVers) ";
	javax.persistence.Query query = entityManager.createQuery(queryStr);
	query.setParameter("tiStato", stato.name());
	query.setParameter("idVers", idVersatori);
	lstSessioni = query.getResultList();

	return lstSessioni;
    }

    public List<PigObject> getListaObjects(List<Long> idVersatori, Constants.StatoOggetto stato) {
	List<PigObject> lstObjects;
	String queryStr = "SELECT u FROM PigObject u WHERE u.tiStatoObject = :tiStato AND u.pigVer.idVers IN (:idVers) ";
	javax.persistence.Query query = entityManager.createQuery(queryStr);
	query.setParameter("tiStato", stato.name());
	query.setParameter("idVers", idVersatori);
	lstObjects = query.getResultList();

	return lstObjects;
    }
}
