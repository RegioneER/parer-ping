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
import static it.eng.ArquillianUtils.aString;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
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
public class ConfigurationHelperTest {
    @EJB
    private ConfigurationHelper helper;

    @Deployment
    public static Archive<?> createTestArchive() {
	return ArquillianUtils.createPingJar(ConfigurationHelper.class).addPackages(true,
		"org.apache.commons.lang3");
    }

    @Test
    void getConfiguration_queryIsOk() {
	helper.getConfiguration();
	assertTrue(true);
    }

    @Test
    void getParamApplicApplicationName_queryIsOk() {
	helper.getParamApplicApplicationName();
	assertTrue(true);
    }

    @Test
    void getValoreParamApplic_AMBIENTEVERS_queryIsOk() {
	String nmParamApplic = "NON_ESISTE";
	BigDecimal idAmbienteVers = aBigDecimal();

	assertThrows(ExpectedException.class, () -> {
	    try {
		helper.getValoreParamApplicByAmbienteVers(nmParamApplic, idAmbienteVers);
		fail();
	    } catch (Exception e) {
		throwExceptionIf(ExpectedException.class, e,
			"ParamApplicNotFoundException: Parametro " + nmParamApplic
				+ " non definito o non valorizzato");
	    }
	});
    }

    @Test
    void getValoreParamApplic_APPLIC_queryIsOk() {
	String nmParamApplic = "NON_ESISTE";

	assertThrows(ExpectedException.class, () -> {
	    try {
		helper.getValoreParamApplicByApplic(nmParamApplic);
		fail();
	    } catch (Exception e) {
		throwExceptionIf(ExpectedException.class, e,
			"ParamApplicNotFoundException: Parametro " + nmParamApplic
				+ " non definito o non valorizzato");
	    }
	});

    }

    @Test
    void getValoreParamApplic_TIPOOBJECT_queryIsOk() {
	String nmParamApplic = "NON_ESISTE";
	BigDecimal idAmbienteVers = aBigDecimal();
	BigDecimal idVers = aBigDecimal();
	BigDecimal idTipoObject = aBigDecimal();

	assertThrows(ExpectedException.class, () -> {
	    try {
		helper.getValoreParamApplicByTipoObj(nmParamApplic, idAmbienteVers, idVers,
			idTipoObject);
		fail();
	    } catch (Exception e) {
		throwExceptionIf(ExpectedException.class, e,
			"ParamApplicNotFoundException: Parametro " + nmParamApplic
				+ " non definito o non valorizzato");
	    }
	});
    }

    @Test
    void getValoreParamApplic_VERS_queryIsOk() {
	String nmParamApplic = "NON_ESISTE";
	BigDecimal idAmbienteVers = aBigDecimal();
	BigDecimal idVers = aBigDecimal();

	assertThrows(ExpectedException.class, () -> {
	    try {
		helper.getValoreParamApplicByIdVers(nmParamApplic, idAmbienteVers, idVers);
		fail();
	    } catch (Exception e) {
		throwExceptionIf(ExpectedException.class, e,
			"ParamApplicNotFoundException: Parametro " + nmParamApplic
				+ " non definito o non valorizzato");
	    }
	});
    }

    @Test
    void getValoreParamApplic_String_queryIsOk() {
	String nmParamApplic = aString();

	assertThrows(ExpectedException.class, () -> {
	    try {
		helper.getValoreParamApplicByApplic(nmParamApplic);
	    } catch (Exception e) {
		throwExceptionIf(ExpectedException.class, e, "ParamApplicNotFoundException");
	    }
	});
    }

}
