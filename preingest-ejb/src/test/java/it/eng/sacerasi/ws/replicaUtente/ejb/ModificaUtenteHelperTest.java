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
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.eng.sacerasi.ws.replicaUtente.ejb;

import it.eng.ArquillianUtils;
import it.eng.RollbackException;
import it.eng.integriam.server.ws.reputente.Utente;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ejb.EJB;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import java.util.Collection;
import java.util.HashSet;

import static it.eng.ArquillianUtils.aLong;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author manuel.bertuzzi@eng.it
 */
@RunWith(Arquillian.class)
public class ModificaUtenteHelperTest {
    @EJB
    private ModificaUtenteEjb helper;

    @Deployment
    public static Archive<?> createTestArchive() {
        return ArquillianUtils.createPingJar(ModificaUtenteEjb.class)
                .addPackages(true, "it.eng.sacerasi.ws.replicaUtente.dto", "it.eng.sacerasi.ws.dto")
                .addClass("it.eng.integriam.server.ws.reputente.Utente");
    }

    @Test
    public void getIamUser_queryIsOk() {
        long idUserIam = aLong();
        helper.getIamUser(idUserIam);
        assertTrue(true);
    }

    @Test
    @Ignore("difficile da testare in maniera safe")
    public void eseguiModificaUtente_queryIsOk() {
        Utente utente = new Utente();
        utente.setIdUserIam(-99L);
        helper.eseguiModificaUtente(utente);
        assertTrue(true);
    }

    @Test
    public void getListaAbilOrganizDB_queryIsOk() {
        long idUserIam = 1L;
        assertFalse(helper.getListaAbilOrganizDB(idUserIam).isEmpty());
    }

    @Test(expected = RollbackException.class)
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void deleteIamAbilTipoDato_queryIsOk() {
        Collection<Long> idSet = new HashSet<>();
        idSet.add(-999L);
        helper.deleteIamAbilTipoDato(idSet);
        throw new RollbackException();
    }

    @Test(expected = RollbackException.class)
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void deleteIamAutorServ_queryIsOk() {
        Collection<Long> idSet = new HashSet<>();
        idSet.add(-999L);
        helper.deleteIamAutorServ(idSet);
        throw new RollbackException();
    }

    @Test(expected = RollbackException.class)
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void deleteIamAbilOrganiz_queryIsOk() {
        Collection<Long> idSet = new HashSet<>();
        idSet.add(-999L);
        helper.deleteIamAbilOrganiz(idSet);
        throw new RollbackException();
    }
}
