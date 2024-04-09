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
package it.eng.sacerasi.job.invioSU.ejb;

import it.eng.ArquillianUtils;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ejb.EJB;

import static it.eng.ArquillianUtils.*;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author manuel.bertuzzi@eng.it
 */
@RunWith(Arquillian.class)
public class InvioSUHelperTest {
    @EJB
    private InvioSUHelper helper;

    @Deployment
    public static Archive<?> createTestArchive() {
        return ArquillianUtils.createPingJar(InvioSUHelper.class);
    }

    @Test
    public void entityManagerIsNotNull() {
        assertNotNull(helper.getEntityManager());
    }

    @Test
    public void getIdStrumentiUrbanisticiDaInviare_queryIsOk() {
        helper.getIdStrumentiUrbanisticiDaInviare();
        assertTrue(true);
    }

    @Test
    @Ignore("could not resolve property: nmDocumento ")
    public void getTipoDocumentoPrincipale_queryIsOk() {
        long idStrumentiUrbanistici = aLong();
        helper.getTipoDocumentoPrincipale(idStrumentiUrbanistici);
        assertTrue(true);
    }

    @Test
    public void getDocumenti_queryIsOk() {
        long idStrumentiUrbanistici = aLong();
        helper.getDocumenti(idStrumentiUrbanistici);
        assertTrue(true);
    }

    @Test
    public void getCollegamenti_queryIsOk() {
        long idStrumentiUrbanistici = aLong();
        helper.getCollegamenti(idStrumentiUrbanistici);
        assertTrue(true);
    }

    @Test
    @Ignore("PigVDettStrumentoUrbanistico is not mapped")
    public void getDettaglioStrumentoUrbanistico_queryIsOk() {
        long idStrumentiUrbanistici = aLong();
        helper.getDettaglioStrumentoUrbanistico(idStrumentiUrbanistici);
        assertTrue(true);
    }

    @Test
    public void getDocumentiDaInviare_queryIsOk() {
        long idStrumentiUrbanistici = aLong();
        helper.getDocumentiDaInviare(idStrumentiUrbanistici);
        assertTrue(true);
    }

    @Test
    public void existsPigObjectPerVersatore_queryIsOk() {
        long idVers = aLong();
        String cdKeyObject = aString();
        helper.existsPigObjectPerVersatore(idVers, cdKeyObject);
        assertTrue(true);
    }

    @Test
    public void existsPigObjectPerVersatoreStrumUrbInAttesaFile_queryIsOk() {
        long idVers = aLong();
        String cdKeyObject = aString();
        helper.existsPigObjectPerVersatoreStrumUrbInAttesaFile(idVers, cdKeyObject);
        assertTrue(true);
    }

    @Test
    public void existsPigObjectPerVersatoreStrumUrbAnnullato_queryIsOk() {
        long idVers = aLong();
        String cdKeyObject = aString();
        helper.existsPigObjectPerVersatoreStrumUrbAnnullato(idVers, cdKeyObject);
        assertTrue(true);
    }

    @Test
    public void existsPigObjectPerVersatoreNoStrumUrb_queryIsOk() {
        long idVers = aLong();
        String cdKeyObject = aString();
        helper.existsPigObjectPerVersatoreNoStrumUrb(idVers, cdKeyObject);
        assertTrue(true);
    }

    @Test
    public void getPigObjectPerVersatoreStrumUrbInNewTx_queryIsOk() {
        long idVers = aLong();
        String cdKeyObject = aString();
        helper.getPigObjectPerVersatoreStrumUrbInNewTx(idVers, cdKeyObject);
        assertTrue(true);
    }

    @Test
    public void getPigObjectPerVersatoreStrumUrb_queryIsOk() {
        long idVers = aLong();
        String cdKeyObject = aString();
        helper.getPigObjectPerVersatoreStrumUrb(idVers, cdKeyObject);
        assertTrue(true);
    }
}
