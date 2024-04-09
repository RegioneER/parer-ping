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

package it.eng.sacerasi.versamento.ejb;

import it.eng.sacerasi.common.Constants;
import it.eng.sacerasi.entity.PigObject;
import it.eng.sacerasi.entity.PigVers;
import it.eng.sacerasi.exception.ParerUserError;
import it.eng.sacerasi.slite.gen.tablebean.PigObjectRowBean;
import it.eng.sacerasi.slite.gen.viewbean.MonVLisStatoVersRowBean;
import it.eng.sacerasi.slite.gen.viewbean.MonVLisStatoVersTableBean;
import it.eng.sacerasi.versamento.helper.VersamentoOggettoHelper;
import it.eng.sacerasi.viewEntity.MonVLisStatoVers;
import it.eng.sacerasi.web.util.Transform;
import it.eng.spagoLite.db.base.BaseTableInterface;
import it.eng.spagoLite.db.base.row.BaseRow;
import it.eng.spagoLite.db.base.table.BaseTable;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Bonora_L
 */
@Stateless
@LocalBean
@Interceptors({ it.eng.sacerasi.aop.TransactionInterceptor.class })
public class VersamentoOggettoEjb {

    private static final Logger logger = LoggerFactory.getLogger(VersamentoOggettoEjb.class);

    @Resource
    private SessionContext ctx;

    @PersistenceContext
    private EntityManager entityManager;
    @EJB
    private VersamentoOggettoHelper helper;

    public EntityManager getEntityManager() {
        return entityManager;
    }

    /**
     * Ritorna il tableBean MonVLisStatoVersTableBean contenente la lista di record in base ai parametri passati
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
     *            numero
     * @param dsObject
     *            descrizione
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
     * @return entity bean {@link MonVLisStatoVersTableBean}
     * 
     * @throws ParerUserError
     *             errore generico
     */
    public MonVLisStatoVersTableBean getMonVLisStatoVersTableBean(long idUtente, BigDecimal idAmbiente,
            BigDecimal idVers, BigDecimal idTipoOggetto, BigDecimal idObject, String cdKeyObject, String dsObject,
            Date dataDa, Date dataA, String tiStatoEsterno, List<String> tiStatoObject, List<String> tiVersFile,
            String note) throws ParerUserError {
        List<MonVLisStatoVers> listObjects = helper.getMonVLisStatoVers(idUtente, idAmbiente, idVers, idTipoOggetto,
                idObject, cdKeyObject, dsObject, dataDa, dataA, tiStatoEsterno, tiStatoObject, tiVersFile, note);
        MonVLisStatoVersTableBean table = new MonVLisStatoVersTableBean();
        if (!listObjects.isEmpty()) {
            try {
                for (MonVLisStatoVers mvlsv : listObjects) {
                    MonVLisStatoVersRowBean row = (MonVLisStatoVersRowBean) Transform.entity2RowBean(mvlsv);
                    row.setString("nm_versatore", mvlsv.getNmAmbienteVers() + ", " + mvlsv.getNmVers());

                    if (row.getTiVersFile().equals(Constants.TipoVersamento.ZIP_CON_XML_SACER.name())) {
                        row.setBigDecimal("ni_ud_prodotte", row.getNiUdProdotte());
                        // MEV 26891
                        row.setString("ti_gestione_figli", "--");
                    } else {
                        row.setBigDecimal("ni_ud_prodotte", new BigDecimal(0));
                        row.setString("ti_gestione_figli", row.getTiGestOggettiFigli());
                    }

                    table.add(row);
                }
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                    | IllegalArgumentException | InvocationTargetException ex) {
                logger.error("Errore durante il recupero dei versamenti " + ExceptionUtils.getRootCauseMessage(ex), ex);
                throw new ParerUserError("Errore durante il recupero dei versamenti");
            }
        }
        return table;
    }

    public <T> BaseTableInterface getColumnFromPigObjectTableBean(BigDecimal idTipoObject, Class<T> resultClass,
            String... columns) {
        BaseTableInterface<?> tmpTableBean = new BaseTable();
        List<Object[]> resultList = helper.getColumnFromPigObject(idTipoObject, columns);
        for (Object[] value : resultList) {
            BaseRow row = new BaseRow();
            if (value instanceof Object[]) {
                for (int index = 0; index < columns.length; index++) {
                    String column = columns[index];
                    Object[] res = value;
                    Object val;
                    if (res[index] instanceof Long) {
                        val = new BigDecimal((Long) res[index]);
                    } else {
                        val = res[index];
                    }
                    row.setObject(column, val);
                }
            }
            tmpTableBean.add(row);
        }

        return tmpTableBean;
    }

    public PigObjectRowBean getPigObjectRowBean(BigDecimal idObject) throws ParerUserError {
        PigObject obj = helper.findById(PigObject.class, idObject);
        PigObjectRowBean row = null;
        try {
            row = (PigObjectRowBean) Transform.entity2RowBean(obj);
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                | IllegalArgumentException | InvocationTargetException ex) {
            logger.error("Errore durante il recupero dell'oggetto " + ExceptionUtils.getRootCauseMessage(ex), ex);
            throw new ParerUserError("Errore durante il recupero dell'oggetto");
        }
        return row;
    }

    // MEV#21995 computa il path dove stoccare su object storage per questo versatore
    public String getFileOsPathByVers(BigDecimal idVers) {
        PigVers vers = helper.findById(PigVers.class, idVers);
        return String.format("%s/%s/INPUT_FOLDER/", vers.getPigAmbienteVer().getNmAmbienteVers(), vers.getNmVers());
    }

    // MEV27034
    /*
     * Torna True se il versatore è cessato altrimenti false, anche nel caso un cui il versatore non venga trovato.
     */
    public boolean isVersatoreCessato(BigDecimal idOrganizApplic) {
        boolean ret = false;
        PigVers vers = helper.findById(PigVers.class, idOrganizApplic);
        if (vers != null && vers.getFlCessato() != null) {
            ret = vers.getFlCessato().equals("1");
        } else {
            ret = false;
        }
        return ret;
    }

    /**
     * Controlla se l'utente passato in input ha effettuato versamenti di oggetti in Ping
     *
     * @param idUserIam
     *            l'utente versante
     *
     * @return true se sono presenti versamenti in Ping effettuati dall'utente
     */
    public boolean checkExistsVersamentiPing(long idUserIam) {
        Query query = entityManager.createQuery(
                "SELECT oggetto FROM PigObject oggetto " + "WHERE oggetto.iamUser.idUserIam = :idUserIam ");
        query.setParameter("idUserIam", idUserIam);
        query.setMaxResults(1);
        return !query.getResultList().isEmpty();
    }
}
