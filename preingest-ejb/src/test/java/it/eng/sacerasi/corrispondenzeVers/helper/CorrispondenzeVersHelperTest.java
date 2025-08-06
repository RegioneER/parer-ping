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
package it.eng.sacerasi.corrispondenzeVers.helper;

import static it.eng.ArquillianUtils.aBigDecimal;
import static it.eng.ArquillianUtils.aLong;
import static it.eng.ArquillianUtils.aString;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static it.eng.ArquillianUtils.throwExceptionIf;

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
public class CorrispondenzeVersHelperTest {
    @EJB
    private CorrispondenzeVersHelper helper;

    @Deployment
    public static Archive<?> createTestArchive() {
	return ArquillianUtils.createPingJar(CorrispondenzeVersHelper.class);
    }

    @Test
    void getIdOrganizIamStrut_long_queryIsOk() {
	long idTipoObj = aLong();
	helper.getIdOrganizIamStrut(idTipoObj);
	assertTrue(true);
    }

    @Test
    void getIdOrganizIamStrut_long_BigDecimal_queryIsOk() {
	long idTipoObj = aLong();
	BigDecimal idOrganizIamStrut = aBigDecimal();
	helper.getIdOrganizIamStrut(idTipoObj, idOrganizIamStrut);
	assertTrue(true);
    }

    @Test
    void getStrutturaAbilitata_BigDecimal_String_queryIsOk() {
	BigDecimal idOrganizIam = aBigDecimal();
	String nmUserId = aString();

	assertThrows(ExpectedException.class, () -> {
	    try {
		helper.getStrutturaAbilitata(idOrganizIam, nmUserId);
	    } catch (Exception e) {
		throwExceptionIf(ExpectedException.class, e, " No entity found for query");
	    }
	});
    }

    @Test
    void getStrutturaAbilitata_4args_queryIsOk() {
	String nmAmbiente = aString();
	String nmEnte = aString();
	String nmStrut = aString();
	String nmUserId = aString();
	helper.getStrutturaAbilitata(nmAmbiente, nmEnte, nmStrut, nmUserId);
	assertTrue(true);
    }
}
