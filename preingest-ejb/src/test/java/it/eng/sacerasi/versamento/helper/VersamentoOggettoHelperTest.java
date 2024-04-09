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
package it.eng.sacerasi.versamento.helper;

import it.eng.ArquillianUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Test;
import org.junit.runner.RunWith;

import static it.eng.ArquillianUtils.*;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author manuel.bertuzzi@eng.it
 */
@RunWith(Arquillian.class)
public class VersamentoOggettoHelperTest {

    @EJB
    private VersamentoOggettoHelper helper;

    @Deployment
    public static Archive<?> createTestArchive() {
        return ArquillianUtils.createPingJar(VersamentoOggettoHelper.class);
    }

    @Test
    public void entityManagerIsNotNull() {
        assertNotNull(helper.getEntityManager());
    }

    @Test
    public void getMonVLisStatoVers_queryIsOk() {
        long idUtente = aLong();
        BigDecimal idAmbiente = aBigDecimal();
        BigDecimal idVers = aBigDecimal();
        BigDecimal idTipoOggetto = aBigDecimal();
        BigDecimal idOggetto = aBigDecimal();
        String cdKeyObject = aString();
        String dsObject = aString();
        String note = aString();
        Date dataDa = todayTs();
        Date dataA = tomorrowTs();
        String tiStatoEsterno = aString();
        List<String> tiStatoObject = aListOfString(2);
        List<String> tiVersFile = aListOfString(2);
        helper.getMonVLisStatoVers(idUtente, idAmbiente, idVers, idTipoOggetto, idOggetto, cdKeyObject, dsObject,
                dataDa, dataA, tiStatoEsterno, tiStatoObject, tiVersFile, note);
        assertTrue(true);
    }
}
