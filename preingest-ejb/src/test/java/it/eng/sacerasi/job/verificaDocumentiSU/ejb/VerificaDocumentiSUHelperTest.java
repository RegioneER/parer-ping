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
package it.eng.sacerasi.job.verificaDocumentiSU.ejb;

import static it.eng.ArquillianUtils.aBigDecimal;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;

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
public class VerificaDocumentiSUHelperTest {
    @EJB
    private VerificaDocumentiSUHelper helper;

    @Deployment
    public static Archive<?> createTestArchive() {
	return ArquillianUtils.createPingJar(VerificaDocumentiSUHelper.class);
    }

    @Test
    void entityManagerNotNull() {
	assertNotNull(helper.getEntityManager());
    }

    @Test
    void getDocumentiDaVerificare_0args_queryIsOk() {
	helper.getDocumentiDaVerificare();
	assertTrue(true);
    }

    @Test
    void getDocumentiDaVerificare_BigDecimal_queryIsOk() {
	BigDecimal idStrumentoUrbanistico = aBigDecimal();
	helper.getDocumentiDaVerificare(idStrumentoUrbanistico);
	assertTrue(true);
    }

    @Test
    void isVerificaTerminata_queryIsOk() {
	BigDecimal idStrumentoUrbanistico = aBigDecimal();
	helper.isVerificaTerminata(idStrumentoUrbanistico);
	assertTrue(true);
    }

    @Test
    void verificaInCorso_queryIsOk() {
	BigDecimal idStrumentoUrbanistico = aBigDecimal();
	helper.verificaInCorso(idStrumentoUrbanistico);
	assertTrue(true);
    }

    @Test
    void existsDocumentiDaVerificarePerStrumentoUrbanistico_queryIsOk() {
	BigDecimal idStrumentoUrbanistico = aBigDecimal();
	helper.existsDocumentiDaVerificarePerStrumentoUrbanistico(idStrumentoUrbanistico);
	assertTrue(true);
    }

    @Test
    void existsDocumentiDaVerificareSenzaErrorePerStrumentoUrbanistico_queryIsOk() {
	BigDecimal idStrumentoUrbanistico = aBigDecimal();
	helper.existsDocumentiDaVerificareSenzaErrorePerStrumentoUrbanistico(
		idStrumentoUrbanistico);
	assertTrue(true);
    }

    @Test
    void existsDocumentiVerificatiConErrorePerStrumentoUrbanistico_queryIsOk() {
	BigDecimal idStrumentoUrbanistico = aBigDecimal();
	helper.existsDocumentiVerificatiConErrorePerStrumentoUrbanistico(idStrumentoUrbanistico);
	assertTrue(true);
    }

    @Test
    void existsDocumentiDaVerificarePerStrumentoUrbanisticoByVista_queryIsOk() {
	BigDecimal idStrumentoUrbanistico = aBigDecimal();
	helper.existsDocumentiDaVerificarePerStrumentoUrbanisticoByVista(idStrumentoUrbanistico);
	assertTrue(true);
    }

    @Test
    void getDocumentiVerificatiConErrorePerStrumentoUrbanistico_queryIsOk() {
	BigDecimal idStrumentoUrbanistico = aBigDecimal();
	helper.getDocumentiVerificatiConErrorePerStrumentoUrbanistico(idStrumentoUrbanistico);
	assertTrue(true);
    }
}
