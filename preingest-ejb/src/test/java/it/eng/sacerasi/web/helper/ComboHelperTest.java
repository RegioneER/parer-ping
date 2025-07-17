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
package it.eng.sacerasi.web.helper;

import static it.eng.ArquillianUtils.aBigDecimal;
import static it.eng.ArquillianUtils.aLong;
import static it.eng.ArquillianUtils.aStringArray;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static it.eng.ArquillianUtils.throwExpectedExceptionIfNoResultException;

import java.math.BigDecimal;

import javax.ejb.EJB;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.container.annotation.ArquillianTest;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.jupiter.api.Test;

import it.eng.ArquillianUtils;
import it.eng.ExpectedException;

/**
 * @author manuel.bertuzzi@eng.it
 */
@ArquillianTest
public class ComboHelperTest {
    @EJB
    private ComboHelper helper;

    @Deployment
    public static Archive<?> createTestArchive() {
	return ArquillianUtils.createPingJar(ComboHelper.class);
    }

    @Test
    void getAmbienteVersatoreFromUtente_queryIsOk() {
	long idUtente = aLong();
	helper.getAmbienteVersatoreFromUtente(idUtente);
	assertTrue(true);
    }

    @Test
    void getVersatoreFromAmbienteVersatore_queryIsOk() {
	Long idUtente = aLong();
	BigDecimal idAmbienteVers = aBigDecimal();
	helper.getVersatoreFromAmbienteVersatore(idUtente, idAmbienteVers);
	assertTrue(true);
    }

    @Test
    void getTipoObjectFromVersatore_long_BigDecimal_queryIsOk() {
	long idUtente = aLong();
	BigDecimal idVers = aBigDecimal();
	helper.getTipoObjectFromVersatore(idUtente, idVers);
	assertTrue(true);
    }

    @Test
    void getTipoObjectFromVersatore_3args_queryIsOk() {
	long idUtente = aLong();
	BigDecimal idVers = aBigDecimal();
	String[] tipoVers = aStringArray(2);
	helper.getTipoObjectFromVersatore(idUtente, idVers, tipoVers);
	assertTrue(true);
    }

    @Test
    void getTipoObjectFromVersatoreNoFleggati_queryIsOk() {
	long idUtente = aLong();
	BigDecimal idVers = aBigDecimal();
	String[] tipoVers = aStringArray(2);
	helper.getTipoObjectFromVersatoreNoFleggati(idUtente, idVers, tipoVers);
	assertTrue(true);
    }

    @Test
    void getVersatori_queryIsOk() {
	long idUtente = aLong();
	helper.getVersatori(idUtente);
	assertTrue(true);
    }

    @Test
    void getIdAmbienteVersatore_queryIsOk() {
	BigDecimal idVers = aBigDecimal();

	assertThrows(ExpectedException.class, () -> {
	    try {
		helper.getIdAmbienteVersatore(idVers);
	    } catch (Exception e) {
		throwExpectedExceptionIfNoResultException(e);
	    }
	});
    }
}
