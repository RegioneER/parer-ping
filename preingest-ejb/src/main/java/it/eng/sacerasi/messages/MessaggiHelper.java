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

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.lang3.StringUtils;

import it.eng.sacerasi.entity.PigClasseErrore;
import it.eng.sacerasi.entity.PigErrore;

/**
 *
 * @author Bonora_L
 */
@SuppressWarnings("unchecked")
@Stateless
@LocalBean
public class MessaggiHelper {

    @PersistenceContext(unitName = "SacerAsiJPA")
    private EntityManager entityManager;

    public List<PigErrore> retrieveListaErrori(String cdClasseErrore) {
        String qlString = "SELECT e FROM PigErrore e " + (StringUtils.isNotBlank(cdClasseErrore)
                ? "WHERE e.pigClasseErrore.cdClasseErrore = :cdClasseErrore" : "");
        Query query = entityManager.createQuery(qlString);
        if (StringUtils.isNotBlank(cdClasseErrore)) {
            query.setParameter("cdClasseErrore", cdClasseErrore);
        }
        return query.getResultList();
    }

    public List<PigClasseErrore> retrieveListaClassi(String tiStato) {
        String qlString = "SELECT c FROM PigStatoClasseErrore sc " + "JOIN sc.pigClasseErrore c "
                + (StringUtils.isNotBlank(tiStato) ? "WHERE sc.pigStatoObject.tiStatoObject = :tiStato" : "");
        Query query = entityManager.createQuery(qlString);
        if (StringUtils.isNotBlank(tiStato)) {
            query.setParameter("tiStato", tiStato);
        }
        return query.getResultList();
    }

    public List<PigClasseErrore> retrieveListaClassi(List<String> tiStato) {
        Query query = entityManager.createQuery("SELECT c FROM PigStatoClasseErrore sc " + "JOIN sc.pigClasseErrore c "
                + "WHERE sc.pigStatoObject.tiStatoObject IN (:tiStato)");
        query.setParameter("tiStato", tiStato);
        return query.getResultList();
    }

    public PigErrore retrievePigErrore(String cdErrore) {
        Query query = entityManager
                .createQuery("SELECT errore FROM PigErrore errore WHERE errore.cdErrore = :cdErrore");
        query.setParameter("cdErrore", cdErrore);
        List<PigErrore> list = query.getResultList();
        if (!list.isEmpty()) {
            return list.get(0);
        }
        return null;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public PigErrore retrievePigErroreNewTx(String cdErrore) {
        Query query = entityManager
                .createQuery("SELECT errore FROM PigErrore errore " + "WHERE errore.cdErrore = :cdErrore");
        query.setParameter("cdErrore", cdErrore);
        List<PigErrore> list = query.getResultList();
        if (!list.isEmpty()) {
            return list.get(0);
        }
        return null;
    }

    public List<PigErrore> retrievePigErroreLike(String cdErrore) {
        cdErrore += "%";
        Query query = entityManager
                .createQuery("SELECT errore FROM PigErrore errore " + "WHERE errore.cdErrore LIKE :cdErrore");
        query.setParameter("cdErrore", cdErrore);
        return query.getResultList();
    }

}
