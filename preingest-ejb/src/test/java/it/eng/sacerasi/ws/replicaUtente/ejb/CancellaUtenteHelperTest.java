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

import static org.junit.jupiter.api.Assertions.assertThrows;

import javax.ejb.EJB;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.container.annotation.ArquillianTest;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.jupiter.api.Test;

import it.eng.ArquillianUtils;
import it.eng.RollbackException;
import it.eng.sacerasi.ws.replicaUtente.dto.CancellaUtenteExt;
import it.eng.sacerasi.ws.replicaUtente.dto.RispostaWSCancellaUtente;

/**
 *
 * @author manuel.bertuzzi@eng.it
 */
@ArquillianTest
public class CancellaUtenteHelperTest {
    @EJB
    private CancellaUtenteEjb helper;

    @Deployment
    public static Archive<?> createTestArchive() {
	return ArquillianUtils.createPingJar(CancellaUtenteEjb.class)
		.addClasses(CancellaUtenteExt.class, RispostaWSCancellaUtente.class)
		.addPackages(true, "it.eng.sacerasi.ws.dto", "org.apache.commons.lang3");
    }

    @Test
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    void deleteIamUser_queryIsOk() {
	CancellaUtenteExt cuExt = new CancellaUtenteExt();
	cuExt.setIdUserIam(-99);
	RispostaWSCancellaUtente rispostaWs = new RispostaWSCancellaUtente();

	assertThrows(RollbackException.class, () -> {
	    helper.deleteIamUser(cuExt, rispostaWs);
	    throw new RollbackException();
	});

    }

}
