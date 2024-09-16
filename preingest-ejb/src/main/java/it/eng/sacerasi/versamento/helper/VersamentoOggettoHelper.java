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

package it.eng.sacerasi.versamento.helper;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.apache.commons.lang3.StringUtils;

import it.eng.paginator.util.HibernateUtils;
import it.eng.sacerasi.helper.GenericHelper;
import it.eng.sacerasi.viewEntity.MonVLisStatoVers;
import it.eng.sacerasi.web.util.Utils;

/**
 *
 * @author Bonora_L
 */
@Stateless
@LocalBean
public class VersamentoOggettoHelper extends GenericHelper {

    /**
     * Ricerco i versamenti oggetto eseguiti, visualizzabili dall'utente il cui id == <code>idUtente</code> dati i
     * parametri passati in input NB.Il parametro dsObject andrà aggiunto in un prossimo momento
     *
     * @param idUtente
     *            id utente
     * @param idAmbiente
     *            id ambiente
     * @param idVers
     *            id versamento
     * @param idTipoOggetto
     *            id tipo oggetto
     * @param idObject
     *            id oggetto
     * @param cdKeyObject
     *            chiave oggetto
     * @param dsObject
     *            descrizione oggetto
     * @param dataDa
     *            data da
     * @param dataA
     *            da a
     * @param tiStatoEsterno
     *            tipo stato
     * @param tiStatoObject
     *            tipo stato oggetto
     * @param tiVersFile
     *            tipo versamento file
     * @param note
     *            campo note dell'oggetto
     *
     * @return la lista di record
     */
    @SuppressWarnings("unchecked")
    public List<MonVLisStatoVers> getMonVLisStatoVers(long idUtente, BigDecimal idAmbiente, BigDecimal idVers,
            BigDecimal idTipoOggetto, BigDecimal idObject, String cdKeyObject, String dsObject, Date dataDa, Date dataA,
            String tiStatoEsterno, List<String> tiStatoObject, List<String> tiVersFile, String note) {
        StringBuilder queryStr = new StringBuilder(
                "SELECT m FROM MonVLisStatoVers m WHERE m.id.idUserIam =:idUserIam AND m.idAmbienteVers = :idAmbiente");
        String clause = " AND ";
        if (idVers != null) {
            queryStr.append(clause).append("m.idVers = :idVers");
        }
        if (idTipoOggetto != null) {
            queryStr.append(clause).append("m.idTipoObject = :idTipoOggetto");
        }
        if (idObject != null) {
            queryStr.append(clause).append("m.idObject = :idObject");
        }
        if (StringUtils.isNotBlank(cdKeyObject)) {
            queryStr.append(clause).append("LOWER(m.cdKeyObject) LIKE LOWER(:cdKeyObject)");
        }
        if (StringUtils.isNotBlank(dsObject)) {
            queryStr.append(clause).append("LOWER(m.dsObject) LIKE LOWER(:dsObject)");
        }
        if (dataDa != null && dataA != null) {
            queryStr.append(clause).append("m.dtVers BETWEEN :dataDa AND :dataA");
        }
        if (StringUtils.isNotBlank(tiStatoEsterno)) {
            queryStr.append(clause).append("m.tiStatoEsterno LIKE :tiStatoEsterno");
        }
        if (tiStatoObject != null && !tiStatoObject.isEmpty()) {
            queryStr.append(clause).append("m.tiStatoObject IN (:tiStatoObject)");
        }
        if (tiVersFile != null && !tiVersFile.isEmpty()) {
            queryStr.append(clause).append("m.tiVersFile IN (:tiVersFile)");
        }
        // MEV 30343
        if (StringUtils.isNotBlank(note)) {
            queryStr.append(clause).append("LOWER(m.note) like LOWER(:note)");
        }
        queryStr.append(" ORDER BY m.dtVers DESC");
        Query query = getEntityManager().createQuery(queryStr.toString());
        query.setParameter("idUserIam", HibernateUtils.bigDecimalFrom(idUtente));
        query.setParameter("idAmbiente", idAmbiente);
        if (idVers != null) {
            query.setParameter("idVers", idVers);
        }
        if (idTipoOggetto != null) {
            query.setParameter("idTipoOggetto", idTipoOggetto);
        }
        if (idObject != null) {
            query.setParameter("idObject", idObject);
        }
        if (StringUtils.isNotBlank(cdKeyObject)) {
            query.setParameter("cdKeyObject", "%" + cdKeyObject + "%");
        }
        if (StringUtils.isNotBlank(dsObject)) {
            query.setParameter("dsObject", "%" + dsObject + "%");
        }
        if (dataDa != null && dataA != null) {
            query.setParameter("dataDa", dataDa);
            query.setParameter("dataA", dataA);
        }
        if (StringUtils.isNotBlank(tiStatoEsterno)) {
            query.setParameter("tiStatoEsterno", "%" + tiStatoEsterno + "%");
        }
        if (tiStatoObject != null && !tiStatoObject.isEmpty()) {
            query.setParameter("tiStatoObject", tiStatoObject);
        }
        if (tiVersFile != null && !tiVersFile.isEmpty()) {
            query.setParameter("tiVersFile", tiVersFile);
        }
        if (StringUtils.isNotBlank(note)) {
            query.setParameter("note", "%" + note + "%");
        }
        return query.getResultList();
    }

    public List<Object[]> getColumnFromPigObject(BigDecimal idTipoObject, String... columns) {
        StringBuilder builder = new StringBuilder("SELECT ");
        String concatenated = "";
        int i = 0;
        for (String column : columns) {
            String columnCamelCase = Utils.convertSnakeCaseToCamelCase(column);
            if (i > 0) {
                builder.append(",");
                concatenated += ",";
            }
            builder.append("u.").append(columnCamelCase);
            concatenated += "u." + columnCamelCase;
            i++;
        }
        builder.append(
                " FROM PigObject u JOIN u.pigSessioneIngests ses WHERE ses.idSessioneIngest = u.idLastSessioneIngest AND u.pigTipoObject.idTipoObject = :idTipoObject AND u.tiStatoObject = 'DA_TRASFORMARE' AND ses.tiStatoVerificaHash = 'OK' AND u.tiGestOggettiFigli = 'MANUALE'")
                .append(" ORDER BY ").append(concatenated);
        final TypedQuery<Object[]> query = getEntityManager().createQuery(builder.toString(), Object[].class)
                .setParameter("idTipoObject", HibernateUtils.longFrom(idTipoObject));
        return query.getResultList();
    }
}
