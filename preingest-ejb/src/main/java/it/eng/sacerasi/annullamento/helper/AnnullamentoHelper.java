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

package it.eng.sacerasi.annullamento.helper;

import it.eng.paginator.util.HibernateUtils;
import it.eng.sacerasi.common.Constants;
import it.eng.sacerasi.entity.PigUnitaDocObject;
import it.eng.sacerasi.entity.PigXmlAnnulSessioneIngest;
import it.eng.sacerasi.helper.GenericHelper;
import org.apache.commons.lang3.StringUtils;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Bonora_L
 */
@Stateless
@LocalBean
public class AnnullamentoHelper extends GenericHelper {

    @SuppressWarnings("unchecked")
    public List<PigXmlAnnulSessioneIngest> retrievePigXmlAnnulSessioneIngests(BigDecimal idSessioneIngest,
            String tiXmlAnnul) {
        Query query = getEntityManager().createQuery(
                "SELECT xml FROM PigXmlAnnulSessioneIngest xml WHERE xml.pigSessioneIngest.idSessioneIngest = :idSessioneIngest AND xml.tiXmlAnnul = :tiXmlAnnul");
        query.setParameter("idSessioneIngest", HibernateUtils.longFrom(idSessioneIngest));
        query.setParameter("tiXmlAnnul", tiXmlAnnul);
        return query.getResultList();
    }

    /**
     * Aggiorna le unità documentarie appartenenti alla sessione con stato {@code oldState} assegnando stato
     * {@code newState}
     *
     * @param idSessioneIngest
     *            id sessione versamento
     * @param oldState
     *            stato precedente
     * @param newState
     *            stato successivo
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void updateUnitaDocSessione(BigDecimal idSessioneIngest, String oldState, String newState) {
        Query q = getEntityManager().createQuery(
                "UPDATE PigUnitaDocSessione udSes SET udSes.tiStatoUnitaDocSessione = :newState WHERE udSes.pigSessioneIngest.idSessioneIngest = :sesId AND udSes.tiStatoUnitaDocSessione = :oldState");
        q.setParameter("sesId", HibernateUtils.longFrom(idSessioneIngest));
        q.setParameter("oldState", oldState);
        q.setParameter("newState", newState);
        q.executeUpdate();
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void updateUnitaDocSessioneNoError(BigDecimal idSessioneIngest, String oldState, String newState) {
        Query q = getEntityManager().createQuery(
                "UPDATE PigUnitaDocSessione udSes SET udSes.tiStatoUnitaDocSessione = :newState WHERE udSes.pigSessioneIngest.idSessioneIngest = :sesId AND udSes.tiStatoUnitaDocSessione = :oldState AND udSes.cdErrSacer IS NULL");
        q.setParameter("sesId", HibernateUtils.longFrom(idSessioneIngest));
        q.setParameter("oldState", oldState);
        q.setParameter("newState", newState);
        q.executeUpdate();
    }

    /**
     * Aggiorna le unità documentarie appartenenti alla sessione con stato di errore {@code oldState} e codice di errore
     * {@code cdErrSacer} assegnando stato {@code newState}
     *
     * @param idSessioneIngest
     *            id sessione versamento
     * @param oldState
     *            stato precedente
     * @param cdErrSacer
     *            codice di errore
     * @param newState
     *            stato successivo
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void updateUnitaDocSessioneWithError(BigDecimal idSessioneIngest, String oldState, String cdErrSacer,
            String newState) {
        String query = "UPDATE PigUnitaDocSessione udSes SET udSes.tiStatoUnitaDocSessione = :newState WHERE udSes.pigSessioneIngest.idSessioneIngest = :sesId AND udSes.tiStatoUnitaDocSessione = :oldState AND udSes.cdErrSacer ";
        if (StringUtils.isNotBlank(cdErrSacer)) {
            query += " = :cdErrSacer";
        } else {
            query += " IS NOT NULL";
        }
        Query q = getEntityManager().createQuery(query);
        q.setParameter("sesId", HibernateUtils.longFrom(idSessioneIngest));
        q.setParameter("oldState", oldState);
        if (StringUtils.isNotBlank(cdErrSacer)) {
            q.setParameter("cdErrSacer", cdErrSacer);
        }
        q.setParameter("newState", newState);
        q.executeUpdate();
    }

    /**
     * Aggiorna le unità documentarie appartenenti all'oggetto con stato {@code oldState} assegnando stato
     * {@code newState}
     *
     * @param idObject
     *            id oggetto
     * @param oldState
     *            stato precedente
     * @param newState
     *            stato successivo
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void updateUnitaDocObject(BigDecimal idObject, String oldState, String newState) {
        Query q = getEntityManager().createQuery(
                "UPDATE PigUnitaDocObject udObj SET udObj.tiStatoUnitaDocObject = :newState WHERE udObj.pigObject.idObject = :idObject AND udObj.tiStatoUnitaDocObject = :oldState");
        q.setParameter("idObject", HibernateUtils.longFrom(idObject));
        q.setParameter("oldState", oldState);
        q.setParameter("newState", newState);
        q.executeUpdate();
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void updateUnitaDocObjectNoError(BigDecimal idObject, String oldState, String newState) {
        Query q = getEntityManager().createQuery(
                "UPDATE PigUnitaDocObject udObj SET udObj.tiStatoUnitaDocObject = :newState WHERE udObj.pigObject.idObject = :idObject AND udObj.tiStatoUnitaDocObject = :oldState AND udObj.cdErrSacer IS NULL");
        q.setParameter("idObject", HibernateUtils.longFrom(idObject));
        q.setParameter("oldState", oldState);
        q.setParameter("newState", newState);
        q.executeUpdate();
    }

    /**
     * Aggiorna le unità documentarie appartenenti all'oggetto con stato di errore {@code oldState} e codice di errore
     * {@code cdErrSacer} assegnando stato {@code newState}
     *
     * @param idObject
     *            id oggetto
     * @param oldState
     *            stato precedente
     * @param cdErrSacer
     *            codice errore
     * @param newState
     *            stato successivo
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void updateUnitaDocObjectWithError(BigDecimal idObject, String oldState, String cdErrSacer,
            String newState) {
        String query = "UPDATE PigUnitaDocObject udObj SET udObj.tiStatoUnitaDocObject = :newState WHERE udObj.pigObject.idObject = :idObject AND udObj.tiStatoUnitaDocObject = :oldState AND udObj.cdErrSacer ";
        if (StringUtils.isNotBlank(cdErrSacer)) {
            query += " = :cdErrSacer";
        } else {
            query += " IS NOT NULL";
        }
        Query q = getEntityManager().createQuery(query);
        q.setParameter("idObject", HibernateUtils.longFrom(idObject));
        q.setParameter("oldState", oldState);
        if (StringUtils.isNotBlank(cdErrSacer)) {
            q.setParameter("cdErrSacer", cdErrSacer);
        }
        q.setParameter("newState", newState);
        q.executeUpdate();
    }

    @SuppressWarnings("unchecked")
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public List<PigUnitaDocObject> retrievePigUnitaDocObject(long idObject, String state) {
        Query q = getEntityManager().createQuery(
                "SELECT udObj FROM PigUnitaDocObject udObj WHERE udObj.pigObject.idObject = :idObject AND udObj.tiStatoUnitaDocObject = :state");
        q.setParameter("idObject", idObject);
        q.setParameter("state", state);
        return q.getResultList();
    }

    // SUE26200
    public Long countPigUnitaDocObject(long idObject, String state) {
        Query q = getEntityManager().createQuery(
                "SELECT count(udObj) FROM PigUnitaDocObject udObj WHERE udObj.pigObject.idObject = :idObject AND udObj.tiStatoUnitaDocObject = :state");
        q.setParameter("idObject", idObject);
        q.setParameter("state", state);
        return (Long) q.getSingleResult();
    }

    /**
     * Ritorna il numero degli oggetti figli che non hanno stato ANNULLATO
     *
     * @param idObjectPadre
     *            id padre
     * @param idObjectFiglio
     *            id figlio
     * 
     * @return count conteggio
     */
    public Long countFigliNonAnnullati(long idObjectPadre, BigDecimal idObjectFiglio) {
        Query q = getEntityManager().createQuery(
                "SELECT COUNT(figli) FROM PigObject figli WHERE figli.pigObjectPadre.idObject = :idObjectPadre AND figli.idObject != :idObjectFiglio AND figli.tiStatoObject != :statoAnnullato");
        q.setParameter("idObjectPadre", idObjectPadre);
        q.setParameter("idObjectFiglio", HibernateUtils.longFrom(idObjectFiglio));
        q.setParameter("statoAnnullato", Constants.StatoOggetto.ANNULLATO.name());
        return (Long) q.getSingleResult();
    }

    /**
     * Ritorna il numero degli oggetti figli che non hanno stato ANNULLATO o CHIUSO_OK
     *
     * @param idObjectPadre
     *            id padre
     * @param idObjectFiglio
     *            id figlio
     * 
     * @return count conteggio
     */
    public Long countFigliNonAnnullatiOCorretti(long idObjectPadre, BigDecimal idObjectFiglio) {
        Query q = getEntityManager().createQuery(
                "SELECT COUNT(figli) FROM PigObject figli WHERE figli.pigObjectPadre.idObject = :idObjectPadre AND figli.idObject != :idObjectFiglio AND figli.tiStatoObject NOT IN (:stati)");
        q.setParameter("idObjectPadre", HibernateUtils.longFrom(idObjectPadre));
        q.setParameter("idObjectFiglio", HibernateUtils.longFrom(idObjectFiglio));
        String[] s = new String[] { Constants.StatoOggetto.ANNULLATO.name(), Constants.StatoOggetto.CHIUSO_OK.name() };
        q.setParameter("stati", Arrays.asList(s));
        return (Long) q.getSingleResult();
    }

    public BigDecimal getIdOrganizIamFromMonVLisUnitaDocObject(long idObject) {
        final TypedQuery<BigDecimal> idOrganizIam = getEntityManager().createQuery(
                "SELECT DISTINCT (u.idOrganizIam) "
                        + "FROM MonVLisUnitaDocObject u WHERE u.idObject = :idObject AND u.idOrganizIam IS NOT NULL",
                BigDecimal.class).setParameter("idObject", HibernateUtils.bigDecimalFrom(idObject));
        return idOrganizIam.getSingleResult();
    }
}
