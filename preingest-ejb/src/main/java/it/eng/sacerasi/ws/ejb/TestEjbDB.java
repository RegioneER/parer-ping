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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.eng.sacerasi.ws.ejb;

import it.eng.sacerasi.entity.PigAmbienteVers;
import java.util.List;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.UserTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Fioravanti_F
 */
@Stateless(mappedName = "TestEjbDB")
@LocalBean
@TransactionManagement(TransactionManagementType.BEAN)
public class TestEjbDB {

    @Resource
    private UserTransaction utx;
    //
    private static final Logger log = LoggerFactory.getLogger(TestEjbDB.class);
    //
    @PersistenceContext(unitName = "SacerAsiJPA")
    private EntityManager entityManager;

    public void ciao() {
    }

    @SuppressWarnings("unchecked")
    public void leggiVolumiDocumento(long idDoc) {

        List<PigAmbienteVers> lstVolumi = null;

        try {
            String queryStr = "select t from PigAmbienteVer t";

            javax.persistence.Query query = entityManager.createQuery(queryStr);
            lstVolumi = query.getResultList();
            log.info("letti record " + lstVolumi.size());

        } catch (Exception e) {

            log.error("Eccezione nella lettura  della tabella AsiAmbienteVer" + e);
        }

    }
}
