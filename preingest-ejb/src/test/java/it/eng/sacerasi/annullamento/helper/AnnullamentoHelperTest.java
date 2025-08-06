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
package it.eng.sacerasi.annullamento.helper;

import static it.eng.ArquillianUtils.aBigDecimal;
import static it.eng.ArquillianUtils.aLong;
import static it.eng.ArquillianUtils.aString;
import static it.eng.ArquillianUtils.throwExpectedExceptionIfNoResultException;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;

import javax.ejb.EJB;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.container.annotation.ArquillianTest;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.jupiter.api.Test;

import it.eng.ArquillianUtils;
import it.eng.ExpectedException;
import it.eng.RollbackException;

/**
 * @author manuel.bertuzzi@eng.it
 */
@ArquillianTest
public class AnnullamentoHelperTest {
    @EJB
    private AnnullamentoHelper helper;

    @Deployment
    public static Archive<?> createTestArchive() {
	return ArquillianUtils.createPingJar(AnnullamentoHelper.class);
    }

    @Test
    void retrievePigXmlAnnulSessioneIngests_queryIsOk() {
	BigDecimal idSessioneIngest = aBigDecimal();
	String tiXmlAnnul = aString();
	helper.retrievePigXmlAnnulSessioneIngests(idSessioneIngest, tiXmlAnnul);
	assertTrue(true);
    }

    @Test
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    void updateUnitaDocSessione_queryIsOk() {
	BigDecimal idSessioneIngest = BigDecimal.valueOf(2916);
	String oldState = "VERSATA_ERR";
	String newState = "VERSATA_TIMEOUT";

	assertThrows(RollbackException.class, () -> {
	    helper.updateUnitaDocSessione(idSessioneIngest, oldState, newState);
	    throw new RollbackException();
	});
    }

    @Test
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    void updateUnitaDocSessioneNoError_queryIsOk() {
	BigDecimal idSessioneIngest = aBigDecimal();
	String oldState = aString();
	String newState = aString();

	assertThrows(RollbackException.class, () -> {
	    helper.updateUnitaDocSessioneNoError(idSessioneIngest, oldState, newState);
	    throw new RollbackException();
	});
    }

    @Test
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    void updateUnitaDocSessioneWithError_queryIsOk() {
	BigDecimal idSessioneIngest = aBigDecimal();
	String oldState = aString();
	String cdErrSacer = aString();
	String newState = aString();

	assertThrows(RollbackException.class, () -> {
	    helper.updateUnitaDocSessioneWithError(idSessioneIngest, oldState, cdErrSacer,
		    newState);
	    throw new RollbackException();
	});
    }

    @Test
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    void updateUnitaDocObject_queryIsOk() {
	BigDecimal idObject = aBigDecimal();
	String oldState = aString();
	String newState = aString();

	assertThrows(RollbackException.class, () -> {
	    helper.updateUnitaDocObject(idObject, oldState, newState);
	    throw new RollbackException();
	});
    }

    @Test
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    void updateUnitaDocObjectNoError_queryIsOk() {
	BigDecimal idObject = aBigDecimal();
	String oldState = aString();
	String newState = aString();

	assertThrows(RollbackException.class, () -> {
	    helper.updateUnitaDocObjectNoError(idObject, oldState, newState);
	    throw new RollbackException();
	});
    }

    @Test
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    void updateUnitaDocObjectWithError_queryIsOk() {
	BigDecimal idObject = aBigDecimal();
	String oldState = aString();
	String cdErrSacer = aString();
	String newState = aString();

	assertThrows(RollbackException.class, () -> {
	    helper.updateUnitaDocObjectWithError(idObject, oldState, cdErrSacer, newState);
	    throw new RollbackException();
	});
    }

    @Test
    void retrievePigUnitaDocObject_queryIsOk() {
	long idObject = aLong();
	String state = aString();
	helper.retrievePigUnitaDocObject(idObject, state);
	assertTrue(true);
    }

    @Test
    void countFigliNonAnnullati_queryIsOk() {
	long idObjectPadre = aLong();
	BigDecimal idObjectFiglio = aBigDecimal();
	helper.countFigliNonAnnullati(idObjectPadre, idObjectFiglio);
	assertTrue(true);
    }

    @Test
    void countFigliNonAnnullatiOCorretti_queryIsOk() {
	long idObjectPadre = aLong();
	BigDecimal idObjectFiglio = aBigDecimal();
	helper.countFigliNonAnnullatiOCorretti(idObjectPadre, idObjectFiglio);
	assertTrue(true);
    }

    @Test
    void getIdOrganizIamFromMonVLisUnitaDocObject_queryIsOk() {
	long idObject = aLong();

	assertThrows(ExpectedException.class, () -> {
	    try {
		helper.getIdOrganizIamFromMonVLisUnitaDocObject(idObject);
	    } catch (Exception e) {
		throwExpectedExceptionIfNoResultException(e);
	    }
	});
    }
}
