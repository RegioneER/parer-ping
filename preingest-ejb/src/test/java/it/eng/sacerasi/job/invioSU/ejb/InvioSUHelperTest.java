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
package it.eng.sacerasi.job.invioSU.ejb;

import static it.eng.ArquillianUtils.aLong;
import static it.eng.ArquillianUtils.aString;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.ejb.EJB;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.container.annotation.ArquillianTest;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import it.eng.ArquillianUtils;

/**
 * @author manuel.bertuzzi@eng.it
 */
@ArquillianTest
public class InvioSUHelperTest {
    @EJB
    private InvioSUHelper helper;

    @Deployment
    public static Archive<?> createTestArchive() {
	return ArquillianUtils.createPingJar(InvioSUHelper.class);
    }

    @Test
    void entityManagerIsNotNull() {
	assertNotNull(helper.getEntityManager());
    }

    @Test
    void getIdStrumentiUrbanisticiDaInviare_queryIsOk() {
	helper.getIdStrumentiUrbanisticiDaInviare();
	assertTrue(true);
    }

    @Test
    @Disabled("could not resolve property: nmDocumento ")
    void getTipoDocumentoPrincipale_queryIsOk() {
	long idStrumentiUrbanistici = aLong();
	helper.getTipoDocumentoPrincipale(idStrumentiUrbanistici);
	assertTrue(true);
    }

    @Test
    void getDocumenti_queryIsOk() {
	long idStrumentiUrbanistici = aLong();
	helper.getDocumenti(idStrumentiUrbanistici);
	assertTrue(true);
    }

    @Test
    void getCollegamenti_queryIsOk() {
	long idStrumentiUrbanistici = aLong();
	helper.getCollegamenti(idStrumentiUrbanistici);
	assertTrue(true);
    }

    @Test
    @Disabled("PigVDettStrumentoUrbanistico is not mapped")
    void getDettaglioStrumentoUrbanistico_queryIsOk() {
	long idStrumentiUrbanistici = aLong();
	helper.getDettaglioStrumentoUrbanistico(idStrumentiUrbanistici);
	assertTrue(true);
    }

    @Test
    void getDocumentiDaInviare_queryIsOk() {
	long idStrumentiUrbanistici = aLong();
	helper.getDocumentiDaInviare(idStrumentiUrbanistici);
	assertTrue(true);
    }

    @Test
    void existsPigObjectPerVersatore_queryIsOk() {
	long idVers = aLong();
	String cdKeyObject = aString();
	helper.existsPigObjectPerVersatore(idVers, cdKeyObject);
	assertTrue(true);
    }

    @Test
    void existsPigObjectPerVersatoreStrumUrbInAttesaFile_queryIsOk() {
	long idVers = aLong();
	String cdKeyObject = aString();
	helper.existsPigObjectPerVersatoreStrumUrbInAttesaFile(idVers, cdKeyObject);
	assertTrue(true);
    }

    @Test
    void existsPigObjectPerVersatoreStrumUrbAnnullato_queryIsOk() {
	long idVers = aLong();
	String cdKeyObject = aString();
	helper.existsPigObjectPerVersatoreStrumUrbAnnullato(idVers, cdKeyObject);
	assertTrue(true);
    }

    @Test
    void existsPigObjectPerVersatoreNoStrumUrb_queryIsOk() {
	long idVers = aLong();
	String cdKeyObject = aString();
	helper.existsPigObjectPerVersatoreNoStrumUrb(idVers, cdKeyObject);
	assertTrue(true);
    }

    @Test
    void getPigObjectPerVersatoreStrumUrbInNewTx_queryIsOk() {
	long idVers = aLong();
	String cdKeyObject = aString();
	helper.getPigObjectPerVersatoreStrumUrbInNewTx(idVers, cdKeyObject);
	assertTrue(true);
    }

    @Test
    void getPigObjectPerVersatoreStrumUrb_queryIsOk() {
	long idVers = aLong();
	String cdKeyObject = aString();
	helper.getPigObjectPerVersatoreStrumUrb(idVers, cdKeyObject);
	assertTrue(true);
    }
}
