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
package it.eng.sacerasi.annullamento.helper;

import it.eng.ArquillianUtils;
import it.eng.ExpectedException;
import it.eng.RollbackException;

import java.math.BigDecimal;
import javax.ejb.EJB;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Test;
import org.junit.runner.RunWith;

import static it.eng.ArquillianUtils.*;
import static org.junit.Assert.assertTrue;

/**
 * @author manuel.bertuzzi@eng.it
 */
@RunWith(Arquillian.class)
public class AnnullamentoHelperTest {
    @EJB
    private AnnullamentoHelper helper;

    @Deployment
    public static Archive<?> createTestArchive() {
        return ArquillianUtils.createPingJar(AnnullamentoHelper.class);
    }

    @Test
    public void retrievePigXmlAnnulSessioneIngests_queryIsOk() {
        BigDecimal idSessioneIngest = aBigDecimal();
        String tiXmlAnnul = aString();
        helper.retrievePigXmlAnnulSessioneIngests(idSessioneIngest, tiXmlAnnul);
        assertTrue(true);
    }

    @Test(expected = RollbackException.class)
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void updateUnitaDocSessione_queryIsOk() {
        BigDecimal idSessioneIngest = BigDecimal.valueOf(2916);
        String oldState = "VERSATA_ERR";
        String newState = "VERSATA_TIMEOUT";
        helper.updateUnitaDocSessione(idSessioneIngest, oldState, newState);
        throw new RollbackException();
    }

    @Test(expected = RollbackException.class)
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void updateUnitaDocSessioneNoError_queryIsOk() {
        BigDecimal idSessioneIngest = aBigDecimal();
        String oldState = aString();
        String newState = aString();
        helper.updateUnitaDocSessioneNoError(idSessioneIngest, oldState, newState);
        throw new RollbackException();
    }

    @Test(expected = RollbackException.class)
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void updateUnitaDocSessioneWithError_queryIsOk() {
        BigDecimal idSessioneIngest = aBigDecimal();
        String oldState = aString();
        String cdErrSacer = aString();
        String newState = aString();
        helper.updateUnitaDocSessioneWithError(idSessioneIngest, oldState, cdErrSacer, newState);
        throw new RollbackException();
    }

    @Test(expected = RollbackException.class)
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void updateUnitaDocObject_queryIsOk() {
        BigDecimal idObject = aBigDecimal();
        String oldState = aString();
        String newState = aString();
        helper.updateUnitaDocObject(idObject, oldState, newState);
        throw new RollbackException();
    }

    @Test(expected = RollbackException.class)
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void updateUnitaDocObjectNoError_queryIsOk() {
        BigDecimal idObject = aBigDecimal();
        String oldState = aString();
        String newState = aString();
        helper.updateUnitaDocObjectNoError(idObject, oldState, newState);
        throw new RollbackException();
    }

    @Test(expected = RollbackException.class)
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void updateUnitaDocObjectWithError_queryIsOk() {
        BigDecimal idObject = aBigDecimal();
        String oldState = aString();
        String cdErrSacer = aString();
        String newState = aString();
        helper.updateUnitaDocObjectWithError(idObject, oldState, cdErrSacer, newState);
        throw new RollbackException();
    }

    @Test
    public void retrievePigUnitaDocObject_queryIsOk() {
        long idObject = aLong();
        String state = aString();
        helper.retrievePigUnitaDocObject(idObject, state);
        assertTrue(true);
    }

    @Test
    public void countFigliNonAnnullati_queryIsOk() {
        long idObjectPadre = aLong();
        BigDecimal idObjectFiglio = aBigDecimal();
        helper.countFigliNonAnnullati(idObjectPadre, idObjectFiglio);
        assertTrue(true);
    }

    @Test
    public void countFigliNonAnnullatiOCorretti_queryIsOk() {
        long idObjectPadre = aLong();
        BigDecimal idObjectFiglio = aBigDecimal();
        helper.countFigliNonAnnullatiOCorretti(idObjectPadre, idObjectFiglio);
        assertTrue(true);
    }

    @Test(expected = ExpectedException.class)
    public void getIdOrganizIamFromMonVLisUnitaDocObject_queryIsOk() {
        long idObject = aLong();
        try {
            helper.getIdOrganizIamFromMonVLisUnitaDocObject(idObject);
        } catch (Exception e) {
            throwExpectedExceptionIfNoResultException(e);
        }
    }
}
