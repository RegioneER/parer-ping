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
package it.eng.sacerasi.sisma.ejb;

import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.ejb.EJB;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.container.annotation.ArquillianTest;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.jupiter.api.Test;

import it.eng.ArquillianUtils;

/**
 *
 * @author manuel.bertuzzi@eng.it
 */
@ArquillianTest
public class InvioSismaHelperTest {

    @EJB
    private InvioSismaHelper helper;

    @Deployment
    public static Archive<?> createArchive() {
	return ArquillianUtils.createPingJar(InvioSismaHelper.class);
    }

    @Test
    void getIdSismaDaInviareQueryIsOk() {
	helper.getIdSismaDaInviare();
	assertTrue(true);
    }

    @Test
    void getDocumentiQueryIsOk() {
	long idSisma = 0L;
	helper.getDocumenti(idSisma);
	assertTrue(true);
    }

    @Test
    void getDettaglioSismaQueryIsOk() {
	long idSisma = 0L;
	helper.getDettaglioSisma(idSisma);
	assertTrue(true);
    }

    @Test
    void getDocumentiDaInviareQueryIsOk() {
	long idSisma = 0L;
	helper.getDocumentiDaInviare(idSisma);
	assertTrue(true);
    }

    @Test
    void existsPigObjectPerVersatoreQueryIsOk() {
	long idVers = 0L;
	String cdKeyObject = "NON ESISTE";
	helper.existsPigObjectPerVersatore(idVers, cdKeyObject);
	assertTrue(true);
    }

    @Test
    void existsPigObjectPerVersatoreSismaInAttesaFileQueryIsOk() {
	long idVers = 0L;
	String cdKeyObject = "NON ESISTE";
	helper.existsPigObjectPerVersatoreSismaInAttesaFile(idVers, cdKeyObject);
	assertTrue(true);
    }

    @Test
    void existsPigObjectPerVersatoreSismaAnnullatoQueryIsOk() {
	long idVers = 0L;
	String cdKeyObject = "NON ESISTE";
	helper.existsPigObjectPerVersatoreSismaAnnullato(idVers, cdKeyObject);
	assertTrue(true);
    }

    @Test
    void existsPigObjectPerVersatoreNoSismaQueryIsOk() {
	long idVers = 0L;
	String cdKeyObject = "NON ESISTE";
	helper.existsPigObjectPerVersatoreNoSisma(idVers, cdKeyObject);
	assertTrue(true);
    }

    @Test
    void getPigObjectPerVersatoreSismaInNewTxQueryIsOk() {
	long idVers = 0L;
	String cdKeyObject = "NON ESISTE";
	helper.getPigObjectPerVersatoreSismaInNewTx(idVers, cdKeyObject);
	assertTrue(true);
    }

    @Test
    void getPigObjectPerVersatoreSismaQueryIsOk() {
	long idVers = 0L;
	String cdKeyObject = "NON ESISTE";
	helper.getPigObjectPerVersatoreSisma(idVers, cdKeyObject);
	assertTrue(true);
    }
}
