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
package it.eng.sacerasi.job.coda.helper;

import it.eng.ArquillianUtils;
import it.eng.ExpectedException;
import it.eng.RollbackException;
import it.eng.sacerasi.entity.PigObject;
import it.eng.sacerasi.entity.PigSessioneIngest;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ejb.EJB;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import java.math.BigDecimal;

import static it.eng.ArquillianUtils.*;
import static org.junit.Assert.assertTrue;

/**
 * @author manuel.bertuzzi@eng.it
 */
@RunWith(Arquillian.class)
public class CodaHelperTest {
    @EJB
    private CodaHelper helper;

    @Deployment
    public static Archive<?> createTestArchive() {
        return ArquillianUtils.createPingJar(CodaHelper.class).addClass("it.eng.sacerasi.web.util.Constants");
    }

    @Test
    public void retrieveVersatori_queryIsOk() {
        helper.retrieveVersatori();
        assertTrue(true);
    }

    @Test
    public void retrieveObjectsIdByIdVersAndState_3args_queryIsOk() {
        Long idVers = aLong();
        String state1 = aString();
        String state2 = aString();
        helper.retrieveObjectsIdByIdVersAndState(idVers, state1, state2);
        assertTrue(true);
    }

    @Test
    public void retrieveObjectsIdByIdVersAndState_Long_String_queryIsOk() {
        Long idVers = aLong();
        String state = aString();
        helper.retrieveObjectsIdByIdVersAndState(idVers, state);
        assertTrue(true);
    }

    @Test
    public void findPigObjectById_queryIsOk() {
        Long pigObjectId = aLong();
        helper.findPigObjectById(pigObjectId);
        assertTrue(true);
    }

    @Test
    public void findPigObjectByIdWithLock_queryIsOk() {
        Long pigObjectId = aLong();
        helper.findPigObjectByIdWithLock(pigObjectId);
        assertTrue(true);
    }

    @Test
    public void retrieveUnitaDocsIdByIdObjAndState_queryIsOk() {
        Long objId = aLong();
        String state = aString();
        helper.retrieveUnitaDocsIdByIdObjAndState(objId, state);
        assertTrue(true);
    }

    @Test
    public void findPigUnitaDocObjectById_queryIsOk() {
        Long unitaDocId = aLong();
        helper.findPigUnitaDocObjectById(unitaDocId);
        assertTrue(true);
    }

    @Test
    public void findLockPigUnitaDocObjectById_queryIsOk() {
        Long unitaDocId = aLong();
        helper.findLockPigUnitaDocObjectById(unitaDocId);
        assertTrue(true);
    }

    @Test(expected = ExpectedException.class)
    public void selectQueue_queryIsOk() {
        BigDecimal niSizeFileByte = aBigDecimal();
        try {
            helper.selectQueue(niSizeFileByte);
        } catch (Exception e) {
            throwExpectedExceptionIfNoResultException(e);
        }
    }

    @Test(expected = ExpectedException.class)
    public void retrieveXmlIdByUdIdAndType_queryIsOk() {
        long idUnitaDocObject = aLong();
        String type = aString();
        try {
            helper.retrieveXmlIdByUdIdAndType(idUnitaDocObject, type);
        } catch (Exception e) {
            throwExpectedExceptionIfNoResultException(e);
        }
    }

    @Test
    public void contaStudiDicomVersatiOggiEInCodaVers_queryIsOk() {
        helper.contaStudiDicomVersatiOggiEInCodaVers();
        assertTrue(true);
    }

    @Test(expected = ExpectedException.class)
    public void retrieveSessionByObject_queryIsOk() {
        PigObject object = aPigObject();
        try {
            helper.retrieveSessionByObject(object);
        } catch (Exception e) {
            throwExpectedExceptionIfNoResultException(e);
        }
    }

    private PigObject aPigObject() {
        PigObject object = new PigObject();
        object.setIdObject(aLong());
        return object;
    }

    @Test
    public void countUdInObj_queryIsOk() {
        PigObject object = aPigObject();
        String state = aString();
        String errCode = aString();
        helper.countUdInObj(object, state, errCode);
        assertTrue(true);
    }

    @Test(expected = RollbackException.class)
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void incrementaVersateOk_queryIsOk() {
        PigSessioneIngest session = aPigSessioneIngest();
        try {
            helper.incrementaVersateOk(session);
        } catch (Exception e) {
            throwExceptionIf(RollbackException.class, e, "Entity not managed");
        }
        throw new RollbackException();
    }

    private PigSessioneIngest aPigSessioneIngest() {
        PigSessioneIngest session = new PigSessioneIngest();
        session.setIdSessioneIngest(aLong());
        return session;
    }

    @Test(expected = RollbackException.class)
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void incrementaVersateTimeout_queryIsOk() {
        PigSessioneIngest session = aPigSessioneIngest();
        try {
            helper.incrementaVersateTimeout(session);
        } catch (Exception e) {
            throwExceptionIf(RollbackException.class, e, "Entity not managed");
        }
        throw new RollbackException();
    }

    @Test(expected = RollbackException.class)
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void incrementaVersateErr_queryIsOk() {
        PigSessioneIngest session = aPigSessioneIngest();
        try {
            helper.incrementaVersateErr(session);
        } catch (Exception e) {
            throwExceptionIf(RollbackException.class, e, "Entity not managed");
        }
        throw new RollbackException();
    }

    @Test(expected = RollbackException.class)
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void incrementaVersate_queryIsOk() {
        PigSessioneIngest session = aPigSessioneIngest();
        try {
            helper.incrementaVersate(session);
            throw new RollbackException();
        } catch (Exception e) {
            throwExceptionIf(RollbackException.class, e, "Entity not managed");
        }
        throw new RollbackException();
    }

    @Test
    public void findPigUnitaDocSessioneById_queryIsOk() {
        Long unitaDocSessionId = aLong();
        helper.findPigUnitaDocSessioneById(unitaDocSessionId);
        assertTrue(true);
    }

    @Test
    public void findLockPigUnitaDocSessioneById_queryIsOk() {
        Long unitaDocSessionId = aLong();
        helper.findLockPigUnitaDocSessioneById(unitaDocSessionId);
        assertTrue(true);
    }

    @Test(expected = ExpectedException.class)
    public void retrievePigUnitaDocSessioneByKeyUD_queryIsOk() {
        BigDecimal idSessioneIngest = aBigDecimal();
        BigDecimal aaUnitaDocSacer = aBigDecimal();
        String cdKeyUnitaDocSacer = aString();
        String cdRegistroUnitaDocSacer = aString();
        try {
            helper.retrievePigUnitaDocSessioneByKeyUD(idSessioneIngest, aaUnitaDocSacer, cdKeyUnitaDocSacer,
                    cdRegistroUnitaDocSacer);
        } catch (Exception e) {
            throwExpectedExceptionIfNoResultException(e);
        }
    }

    @Test
    public void checkConsumed_queryIsOk() {
        String messageSelector = aString();
        BigDecimal paramToCheck = aBigDecimal();
        helper.checkConsumed(messageSelector, paramToCheck);
        assertTrue(true);
    }

    @Test
    public void findPigSessioneIngestById_queryIsOk() {
        Long idSessione = aLong();
        helper.findPigSessioneIngestById(idSessione);
        assertTrue(true);
    }

    @Test
    public void getCalcoloStatoObjDaTrasf_queryIsOk() {
        Long idObjectPadre = aLong();
        helper.getCalcoloStatoObjDaTrasf(idObjectPadre);
        assertTrue(true);
    }
}
