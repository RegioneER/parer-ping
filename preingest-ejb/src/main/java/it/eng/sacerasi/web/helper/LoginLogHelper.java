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

import it.eng.parer.sacerlog.ejb.common.AppServerInstance;
import it.eng.sacerasi.common.Constants;
import it.eng.sacerasi.entity.SIAplApplic;
import it.eng.sacerasi.entity.SLLogLoginUser;
import it.eng.spagoLite.security.IUser;
import java.util.Date;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author fioravanti_f
 */
@Stateless(mappedName = "LoginLogHelper")
@LocalBean
public class LoginLogHelper {

    Logger log = LoggerFactory.getLogger(LoginLogHelper.class);
    @PersistenceContext(unitName = "SacerAsiJPA")
    private EntityManager entityManager;
    @EJB
    private AppServerInstance appServerInstance;

    public enum TipiEvento {
        LOGIN, LOGOUT
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void writeLogEvento(IUser<?> user, String indIpClient, TipiEvento tipoEvento) {

        try {
            SIAplApplic tmpAplApplic;
            String queryStr = "select t from SIAplApplic t " + "where t.nmApplic = :nmApplic ";
            javax.persistence.Query query = entityManager.createQuery(queryStr, SIAplApplic.class);
            query.setParameter("nmApplic", Constants.APP_CHIAMANTE);
            tmpAplApplic = (SIAplApplic) query.getSingleResult();

            String localServerName = appServerInstance.getName();

            SLLogLoginUser tmpLLogLoginUser = new SLLogLoginUser();
            tmpLLogLoginUser.setsIAplApplic(tmpAplApplic);
            tmpLLogLoginUser.setNmUserid(user.getUsername());
            tmpLLogLoginUser.setCdIndIpClient(indIpClient);
            tmpLLogLoginUser.setCdIndServer(localServerName);
            tmpLLogLoginUser.setDtEvento(new Date());
            tmpLLogLoginUser.setTipoEvento(tipoEvento.name());
            // Modifica per lo SPID
            if (user.getUserType() != null) {
                tmpLLogLoginUser.setTipoUtenteAuth(user.getUserType().name());
                tmpLLogLoginUser.setCdIdEsterno(user.getExternalId());
            }

            entityManager.persist(tmpLLogLoginUser);
            entityManager.flush();

        } catch (Exception e) {
            log.error("Eccezione nel log dell'evento login/logout (writeLogEvento) ", e);
            throw new RuntimeException(e);
        }
    }
}
