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

package it.eng.sacerasi.sisma.ejb;

import java.math.BigDecimal;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import it.eng.paginator.util.HibernateUtils;
import it.eng.sacerasi.entity.PigSismaDocumenti;
import it.eng.sacerasi.helper.GenericHelper;

/**
 *
 * @author gilioli_p
 */
@SuppressWarnings("unchecked")
@Stateless(mappedName = "VerificaDocumentiSismaHelper")
@LocalBean
public class VerificaDocumentiSismaHelper extends GenericHelper {

    @PersistenceContext(unitName = "SacerAsiJPA")
    private EntityManager entityManager;

    public List<PigSismaDocumenti> getDocumentiDaVerificare(BigDecimal idSisma) {
	Query q = entityManager.createQuery("SELECT d FROM PigSismaDocumenti d JOIN d.pigSisma s "
		+ "WHERE s.idSisma = :idSisma AND d.flDeleted='0' AND d.flEsitoVerifica = '0'");
	q.setParameter("idSisma", HibernateUtils.longFrom(idSisma));
	List<PigSismaDocumenti> lista = q.getResultList();
	return lista;
    }

    public boolean isVerificaTerminata(BigDecimal idSisma) {
	Query q = entityManager.createQuery(
		"SELECT COUNT(logJob) FROM PigLogJob logJob WHERE " + "(logJob.idRecord = :idSisma "
			+ "AND logJob.tiRegLogJob = 'INIZIO_SCHEDULAZIONE')"
			+ "AND (logJob.idRecord = :idSisma "
			+ "AND logJob.tiRegLogJob != 'FINE_SCHEDULAZIONE') ");
	q.setParameter("idSisma", idSisma);
	return ((Long) q.getSingleResult()) == 2;
    }

    public boolean verificaInCorso(BigDecimal idSisma) {
	Query qInizio = entityManager.createQuery(
		"SELECT COUNT(logJob) FROM PigLogJob logJob WHERE " + "logJob.idRecord = :idSisma "
			+ "AND logJob.tiRegLogJob = 'INIZIO_SCHEDULAZIONE' ");
	qInizio.setParameter("idSisma", idSisma);
	Long numInizio = (Long) qInizio.getSingleResult();

	Query qFine = entityManager.createQuery(
		"SELECT COUNT(logJob) FROM PigLogJob logJob WHERE " + "logJob.idRecord = :idSisma "
			+ "AND logJob.tiRegLogJob IN ('FINE_SCHEDULAZIONE', 'ERRORE') ");
	qFine.setParameter("idSisma", idSisma);
	Long numFine = (Long) qFine.getSingleResult();

	return numInizio.compareTo(numFine) != 0;
    }

    public boolean existsDocumentiDaVerificareSenzaErrorePerSisma(BigDecimal idSisma) {
	Query q = entityManager
		.createQuery("SELECT COUNT(lisDocDaVerif) FROM PigSismaDocumenti lisDocDaVerif "
			+ "WHERE lisDocDaVerif.pigSisma.idSisma = :idSisma "
			+ "AND lisDocDaVerif.flEsitoVerifica = '0' "
			+ "AND lisDocDaVerif.cdErr IS NULL ");
	q.setParameter("idSisma", HibernateUtils.longFrom(idSisma));
	return ((Long) q.getSingleResult()) > 0;
    }

    public boolean existsDocumentiVerificatiConErrorePerSisma(BigDecimal idSisma) {
	Query q = entityManager
		.createQuery("SELECT COUNT(lisDocDaVerif) FROM PigSismaDocumenti lisDocDaVerif "
			+ "WHERE lisDocDaVerif.pigSisma.idSisma = :idSisma "
			+ "AND lisDocDaVerif.cdErr IS NOT NULL ");
	q.setParameter("idSisma", HibernateUtils.longFrom(idSisma));
	return ((Long) q.getSingleResult()) > 0;
    }

    public List<String> getDocumentiVerificatiConErrorePerSisma(BigDecimal idSisma) {
	Query q = entityManager
		.createQuery("SELECT lisDocDaVerif.nmFileOrig FROM PigSismaDocumenti lisDocDaVerif "
			+ "WHERE lisDocDaVerif.pigSisma.idSisma = :idSisma "
			+ "AND lisDocDaVerif.cdErr IS NOT NULL "
			+ "AND lisDocDaVerif.flDeleted = '0' ");
	q.setParameter("idSisma", HibernateUtils.longFrom(idSisma));
	return q.getResultList();
    }
}
