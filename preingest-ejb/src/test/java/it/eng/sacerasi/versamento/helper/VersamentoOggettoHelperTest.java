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
package it.eng.sacerasi.versamento.helper;

import static it.eng.ArquillianUtils.aBigDecimal;
import static it.eng.ArquillianUtils.aListOfString;
import static it.eng.ArquillianUtils.aLong;
import static it.eng.ArquillianUtils.aString;
import static it.eng.ArquillianUtils.todayTs;
import static it.eng.ArquillianUtils.tomorrowTs;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.container.annotation.ArquillianTest;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.jupiter.api.Test;

import it.eng.ArquillianUtils;

/**
 * @author manuel.bertuzzi@eng.it
 */
@ArquillianTest
public class VersamentoOggettoHelperTest {

    @EJB
    private VersamentoOggettoHelper helper;

    @Deployment
    public static Archive<?> createTestArchive() {
	return ArquillianUtils.createPingJar(VersamentoOggettoHelper.class);
    }

    @Test
    void entityManagerIsNotNull() {
	assertNotNull(helper.getEntityManager());
    }

    @Test
    void getMonVLisStatoVers_queryIsOk() {
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
	helper.getMonVLisStatoVers(idUtente, idAmbiente, idVers, idTipoOggetto, idOggetto,
		cdKeyObject, dsObject, dataDa, dataA, tiStatoEsterno, tiStatoObject, tiVersFile,
		note);
	assertTrue(true);
    }
}
