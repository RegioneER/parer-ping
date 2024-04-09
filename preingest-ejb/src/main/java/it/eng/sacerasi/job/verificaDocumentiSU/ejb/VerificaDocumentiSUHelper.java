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

package it.eng.sacerasi.job.verificaDocumentiSU.ejb;

import it.eng.paginator.util.HibernateUtils;
import it.eng.sacerasi.helper.GenericHelper;
import it.eng.sacerasi.viewEntity.PigVSuLisDocDaVerif;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.List;

/**
 *
 * @author gilioli_p
 */
@SuppressWarnings("unchecked")
@Stateless(mappedName = "VerificaDocumentiSUHelper")
@LocalBean
public class VerificaDocumentiSUHelper extends GenericHelper {

    @PersistenceContext(unitName = "SacerAsiJPA")
    private EntityManager entityManager;

    public List<PigVSuLisDocDaVerif> getDocumentiDaVerificare() {
        Query q = entityManager.createNamedQuery("PigVSuLisDocDaVerif.findAll");
        return q.getResultList();
    }

    public List<PigVSuLisDocDaVerif> getDocumentiDaVerificare(BigDecimal idStrumentoUrbanistico) {
        Query q = entityManager.createQuery(
                "SELECT lisDocDaVerif FROM PigVSuLisDocDaVerif lisDocDaVerif WHERE lisDocDaVerif.id.idStrumentiUrbanistici = :idStrumentoUrbanistico ");
        q.setParameter("idStrumentoUrbanistico", idStrumentoUrbanistico);
        return q.getResultList();
    }

    public boolean isVerificaTerminata(BigDecimal idStrumentoUrbanistico) {
        Query q = entityManager.createQuery("SELECT COUNT(logJob) FROM PigLogJob logJob WHERE "
                + "(logJob.idRecord = :idStrumentoUrbanistico " + "AND logJob.tiRegLogJob = 'INIZIO_SCHEDULAZIONE')"
                + "AND (logJob.idRecord = :idStrumentoUrbanistico "
                + "AND logJob.tiRegLogJob != 'FINE_SCHEDULAZIONE') ");
        q.setParameter("idStrumentoUrbanistico", idStrumentoUrbanistico);
        return ((Long) q.getSingleResult()) == 2;
    }

    public boolean verificaInCorso(BigDecimal idStrumentoUrbanistico) {
        Query qInizio = entityManager.createQuery("SELECT COUNT(logJob) FROM PigLogJob logJob WHERE "
                + "logJob.idRecord = :idStrumentoUrbanistico " + "AND logJob.tiRegLogJob = 'INIZIO_SCHEDULAZIONE' ");
        qInizio.setParameter("idStrumentoUrbanistico", idStrumentoUrbanistico);
        Long numInizio = (Long) qInizio.getSingleResult();

        Query qFine = entityManager.createQuery(
                "SELECT COUNT(logJob) FROM PigLogJob logJob WHERE " + "logJob.idRecord = :idStrumentoUrbanistico "
                        + "AND logJob.tiRegLogJob IN ('FINE_SCHEDULAZIONE', 'ERRORE') ");
        qFine.setParameter("idStrumentoUrbanistico", idStrumentoUrbanistico);
        Long numFine = (Long) qFine.getSingleResult();

        return numInizio.compareTo(numFine) != 0;
    }

    public boolean existsDocumentiDaVerificarePerStrumentoUrbanistico(BigDecimal idStrumentoUrbanistico) {
        Query q = entityManager.createQuery("SELECT COUNT(lisDocDaVerif) FROM PigStrumUrbDocumenti lisDocDaVerif "
                + "WHERE lisDocDaVerif.pigStrumentiUrbanistici.idStrumentiUrbanistici = :idStrumentoUrbanistico "
                + "AND lisDocDaVerif.flEsitoVerifica = '0' " + "AND lisDocDaVerif.flDeleted = '0' ");
        q.setParameter("idStrumentoUrbanistico", HibernateUtils.longFrom(idStrumentoUrbanistico));
        return ((Long) q.getSingleResult()) > 0;
    }

    public boolean existsDocumentiDaVerificareSenzaErrorePerStrumentoUrbanistico(BigDecimal idStrumentoUrbanistico) {
        Query q = entityManager.createQuery("SELECT COUNT(lisDocDaVerif) FROM PigStrumUrbDocumenti lisDocDaVerif "
                + "WHERE lisDocDaVerif.pigStrumentiUrbanistici.idStrumentiUrbanistici = :idStrumentoUrbanistico "
                + "AND lisDocDaVerif.flEsitoVerifica = '0' " + "AND lisDocDaVerif.cdErr IS NULL ");
        q.setParameter("idStrumentoUrbanistico", HibernateUtils.longFrom(idStrumentoUrbanistico));
        return ((Long) q.getSingleResult()) > 0;
    }

    public boolean existsDocumentiVerificatiConErrorePerStrumentoUrbanistico(BigDecimal idStrumentoUrbanistico) {
        Query q = entityManager.createQuery("SELECT COUNT(lisDocDaVerif) FROM PigStrumUrbDocumenti lisDocDaVerif "
                + "WHERE lisDocDaVerif.pigStrumentiUrbanistici.idStrumentiUrbanistici = :idStrumentoUrbanistico "
                + "AND lisDocDaVerif.cdErr IS NOT NULL ");
        q.setParameter("idStrumentoUrbanistico", HibernateUtils.longFrom(idStrumentoUrbanistico));
        return ((Long) q.getSingleResult()) > 0;
    }

    public boolean existsDocumentiDaVerificarePerStrumentoUrbanisticoByVista(BigDecimal idStrumentoUrbanistico) {
        Query q = entityManager.createQuery(
                "SELECT COUNT(lisDocDaVerif) FROM PigVSuLisDocDaVerif lisDocDaVerif WHERE lisDocDaVerif.id.idStrumentiUrbanistici = :idStrumentoUrbanistico ");
        q.setParameter("idStrumentoUrbanistico", idStrumentoUrbanistico);
        return ((Long) q.getSingleResult()) > 0;
    }

    public List<String> getDocumentiVerificatiConErrorePerStrumentoUrbanistico(BigDecimal idStrumentoUrbanistico) {
        Query q = entityManager.createQuery("SELECT lisDocDaVerif.nmFileOrig FROM PigStrumUrbDocumenti lisDocDaVerif "
                + "WHERE lisDocDaVerif.pigStrumentiUrbanistici.idStrumentiUrbanistici = :idStrumentoUrbanistico "
                + "AND lisDocDaVerif.cdErr IS NOT NULL " + "AND lisDocDaVerif.flDeleted = '0' ");
        q.setParameter("idStrumentoUrbanistico", HibernateUtils.longFrom(idStrumentoUrbanistico));
        return q.getResultList();
    }
}
