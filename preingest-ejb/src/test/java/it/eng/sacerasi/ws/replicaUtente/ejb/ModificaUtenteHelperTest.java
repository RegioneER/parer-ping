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

/*
 * To change this license header, choose License Headers in Project Properties. To change this
 * template file, choose Tools | Templates and open the template in the editor.
 */
package it.eng.sacerasi.ws.replicaUtente.ejb;

import static it.eng.ArquillianUtils.aLong;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;
import java.util.HashSet;

import javax.ejb.EJB;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.container.annotation.ArquillianTest;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import it.eng.ArquillianUtils;
import it.eng.RollbackException;
import it.eng.integriam.server.ws.reputente.Utente;

/**
 * @author manuel.bertuzzi@eng.it
 */
@ArquillianTest
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
    void getIamUser_queryIsOk() {
	long idUserIam = aLong();
	helper.getIamUser(idUserIam);
	assertTrue(true);
    }

    @Test
    @Disabled("difficile da testare in maniera safe")
    void eseguiModificaUtente_queryIsOk() {
	Utente utente = new Utente();
	utente.setIdUserIam(-99L);
	helper.eseguiModificaUtente(utente);
	assertTrue(true);
    }

    @Test
    void getListaAbilOrganizDB_queryIsOk() {
	long idUserIam = 1L;
	assertFalse(helper.getListaAbilOrganizDB(idUserIam).isEmpty());
    }

    @Test
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    void deleteIamAbilTipoDato_queryIsOk() {
	Collection<Long> idSet = new HashSet<>();
	idSet.add(-999L);

	assertThrows(RollbackException.class, () -> {
	    helper.deleteIamAbilTipoDato(idSet);
	    throw new RollbackException();
	});
    }

    @Test
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    void deleteIamAutorServ_queryIsOk() {
	Collection<Long> idSet = new HashSet<>();
	idSet.add(-999L);

	assertThrows(RollbackException.class, () -> {
	    helper.deleteIamAutorServ(idSet);
	    throw new RollbackException();
	});
    }

    @Test
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    void deleteIamAbilOrganiz_queryIsOk() {
	Collection<Long> idSet = new HashSet<>();
	idSet.add(-999L);

	assertThrows(RollbackException.class, () -> {
	    helper.deleteIamAbilOrganiz(idSet);
	    throw new RollbackException();
	});
    }
}
