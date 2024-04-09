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
package it.eng.sacerasi.job.verificaDocumentiSU.ejb;

import it.eng.ArquillianUtils;

import java.math.BigDecimal;
import javax.ejb.EJB;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Test;
import org.junit.runner.RunWith;

import static it.eng.ArquillianUtils.aBigDecimal;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author manuel.bertuzzi@eng.it
 */
@RunWith(Arquillian.class)

public class VerificaDocumentiSUHelperTest {
    @EJB
    private VerificaDocumentiSUHelper helper;

    @Deployment
    public static Archive<?> createTestArchive() {
        return ArquillianUtils.createPingJar(VerificaDocumentiSUHelper.class);
    }

    @Test
    public void entityManagerNotNull() {
        assertNotNull(helper.getEntityManager());
    }

    @Test
    public void getDocumentiDaVerificare_0args_queryIsOk() {
        helper.getDocumentiDaVerificare();
        assertTrue(true);
    }

    @Test
    public void getDocumentiDaVerificare_BigDecimal_queryIsOk() {
        BigDecimal idStrumentoUrbanistico = aBigDecimal();
        helper.getDocumentiDaVerificare(idStrumentoUrbanistico);
        assertTrue(true);
    }

    @Test
    public void isVerificaTerminata_queryIsOk() {
        BigDecimal idStrumentoUrbanistico = aBigDecimal();
        helper.isVerificaTerminata(idStrumentoUrbanistico);
        assertTrue(true);
    }

    @Test
    public void verificaInCorso_queryIsOk() {
        BigDecimal idStrumentoUrbanistico = aBigDecimal();
        helper.verificaInCorso(idStrumentoUrbanistico);
        assertTrue(true);
    }

    @Test
    public void existsDocumentiDaVerificarePerStrumentoUrbanistico_queryIsOk() {
        BigDecimal idStrumentoUrbanistico = aBigDecimal();
        helper.existsDocumentiDaVerificarePerStrumentoUrbanistico(idStrumentoUrbanistico);
        assertTrue(true);
    }

    @Test
    public void existsDocumentiDaVerificareSenzaErrorePerStrumentoUrbanistico_queryIsOk() {
        BigDecimal idStrumentoUrbanistico = aBigDecimal();
        helper.existsDocumentiDaVerificareSenzaErrorePerStrumentoUrbanistico(idStrumentoUrbanistico);
        assertTrue(true);
    }

    @Test
    public void existsDocumentiVerificatiConErrorePerStrumentoUrbanistico_queryIsOk() {
        BigDecimal idStrumentoUrbanistico = aBigDecimal();
        helper.existsDocumentiVerificatiConErrorePerStrumentoUrbanistico(idStrumentoUrbanistico);
        assertTrue(true);
    }

    @Test
    public void existsDocumentiDaVerificarePerStrumentoUrbanisticoByVista_queryIsOk() {
        BigDecimal idStrumentoUrbanistico = aBigDecimal();
        helper.existsDocumentiDaVerificarePerStrumentoUrbanisticoByVista(idStrumentoUrbanistico);
        assertTrue(true);
    }

    @Test
    public void getDocumentiVerificatiConErrorePerStrumentoUrbanistico_queryIsOk() {
        BigDecimal idStrumentoUrbanistico = aBigDecimal();
        helper.getDocumentiVerificatiConErrorePerStrumentoUrbanistico(idStrumentoUrbanistico);
        assertTrue(true);
    }
}
