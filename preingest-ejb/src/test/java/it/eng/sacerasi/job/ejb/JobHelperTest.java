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
package it.eng.sacerasi.job.ejb;

import it.eng.ArquillianUtils;
import static it.eng.ArquillianUtils.aListOfLong;
import it.eng.sacerasi.common.Constants;
import java.util.List;
import javax.ejb.EJB;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author manuel.bertuzzi@eng.it
 */
@RunWith(Arquillian.class)
public class JobHelperTest {
    @EJB
    private JobHelper helper;

    @Deployment
    public static Archive<?> createTestArchive() {
        return ArquillianUtils.createPingJar(JobHelper.class);
    }

    @Test
    public void getListaVersatori_queryIsOk() {
        helper.getListaVersatori();
        assertTrue(true);
    }

    @Test
    public void getListaSessioni_queryIsOk() {
        List<Long> idVersatori = aListOfLong(2);
        for (Constants.StatoSessioneRecup stato : Constants.StatoSessioneRecup.values()) {
            helper.getListaSessioni(idVersatori, stato);
            assertTrue(true);
        }
    }

    @Test
    public void getListaObjects_queryIsOk() {
        List<Long> idVersatori = aListOfLong(2);
        for (Constants.StatoOggetto stato : Constants.StatoOggetto.values()) {
            helper.getListaObjects(idVersatori, stato);
            assertTrue(true);
        }
    }
}
