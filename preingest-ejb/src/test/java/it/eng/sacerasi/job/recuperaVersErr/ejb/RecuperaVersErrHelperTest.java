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
package it.eng.sacerasi.job.recuperaVersErr.ejb;

import it.eng.ArquillianUtils;
import it.eng.sacerasi.common.Constants;
import it.eng.sacerasi.entity.PigObject;
import it.eng.sacerasi.entity.PigUnitaDocObject;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ejb.EJB;
import java.util.ArrayList;
import java.util.List;

import static it.eng.ArquillianUtils.aListOfLong;
import static it.eng.ArquillianUtils.aString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author manuel.bertuzzi@eng.it
 */
@RunWith(Arquillian.class)
public class RecuperaVersErrHelperTest {
    @EJB
    private RecuperaVersErrHelper helper;

    @Deployment
    public static Archive<?> createTestArchive() {
        return ArquillianUtils.createPingJar(RecuperaVersErrHelper.class);
    }

    @Test
    public void getListaObjects_queryIsOk() {
        List<Long> idVersatori = aListOfLong(2);
        String statoTimeout = aString();
        String statoErrVers = aString();
        helper.getListaObjects(idVersatori, statoTimeout, statoErrVers, 60);
        assertTrue(true);
    }

    @Test
    public void getUdVersTimeoutConsideraVerataErr() {
        PigUnitaDocObject versataOk = new PigUnitaDocObject();
        versataOk.setTiStatoUnitaDocObject(Constants.StatoUnitaDocObject.VERSATA_OK.name());
        PigUnitaDocObject versataErr = new PigUnitaDocObject();
        versataErr.setTiStatoUnitaDocObject(Constants.StatoUnitaDocObject.VERSATA_ERR.name());

        PigObject obj = new PigObject();
        obj.setPigUnitaDocObjects(new ArrayList<>());
        obj.getPigUnitaDocObjects().add(versataOk);
        obj.getPigUnitaDocObjects().add(versataErr);

        final List<PigUnitaDocObject> list = helper.getUdVersTimeout(obj);
        assertEquals(1, list.size());
        assertEquals(versataErr.getTiStatoUnitaDocObject(), list.get(0).getTiStatoUnitaDocObject());
    }

    @Test
    public void getUdVersTimeoutConsideraVerataTimeout() {
        PigUnitaDocObject versataOk = new PigUnitaDocObject();
        versataOk.setTiStatoUnitaDocObject(Constants.StatoUnitaDocObject.VERSATA_OK.name());

        PigUnitaDocObject versataTimeout = new PigUnitaDocObject();
        versataTimeout.setTiStatoUnitaDocObject(Constants.StatoUnitaDocObject.VERSATA_TIMEOUT.name());

        PigObject obj = new PigObject();
        obj.setPigUnitaDocObjects(new ArrayList<>());
        obj.getPigUnitaDocObjects().add(versataOk);
        obj.getPigUnitaDocObjects().add(versataTimeout);

        final List<PigUnitaDocObject> list = helper.getUdVersTimeout(obj);
        assertEquals(1, list.size());
        assertEquals(versataTimeout.getTiStatoUnitaDocObject(), list.get(0).getTiStatoUnitaDocObject());
    }
}
