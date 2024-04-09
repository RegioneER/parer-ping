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
package it.eng.sacerasi.strumentiUrbanistici.ejb;

import it.eng.ArquillianUtils;
import static it.eng.ArquillianUtils.*;
import it.eng.sacerasi.entity.PigStrumentiUrbanistici;
import it.eng.sacerasi.entity.PigVers;
import it.eng.sacerasi.strumentiUrbanistici.dto.RicercaStrumentiUrbanisticiDTO;
import java.math.BigDecimal;
import java.util.EnumSet;
import javax.ejb.EJB;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author manuel.bertuzzi@eng.it
 */
@RunWith(Arquillian.class)
public class StrumentiUrbanisticiHelperTest {
    @EJB
    private StrumentiUrbanisticiHelper helper;

    @Deployment
    public static Archive<?> createTestArchive() {
        return ArquillianUtils.createPingJar(StrumentiUrbanisticiHelper.class);
    }

    @Test
    public void getEntityManager_notNull() {
        assertNotNull(helper.getEntityManager());
    }

    @Test
    public void findTipiStrumentiUrbanistici_queryIsOk() {
        helper.findTipiStrumentiUrbanistici();
        assertTrue(true);
    }

    private PigVers aPigVers() {
        PigVers pigVer = new PigVers();
        pigVer.setIdVers(aLong());
        return pigVer;
    }

    @Test
    public void findSUByVersAndStates_queryIsOk() {
        BigDecimal idVers = BigDecimal.ZERO;
        EnumSet set = EnumSet.of(PigStrumentiUrbanistici.TiStato.INVIO_IN_CORSO);
        helper.findSUByVersAndStates(new RicercaStrumentiUrbanisticiDTO(), idVers, set);
        assertTrue(true);
    }

    @Test
    public void findSUByVersAndCdKey_queryIsOk() {
        PigVers pigVer = aPigVers();
        String cdKey = aString();
        helper.findSUByVersAndCdKey(pigVer, cdKey);
        assertTrue(true);
    }

    @Test
    public void findNumeriByVersAnnoTipoSUFase_queryIsOk() {
        PigVers pigVer = aPigVers();
        BigDecimal anno = aBigDecimal();
        String nmTipoStrumento = aString();
        String fase = aString();
        helper.findNumeriByVersAnnoTipoSUFase(pigVer, anno, nmTipoStrumento, fase);
        assertTrue(true);
    }

    @Test
    public void getSUByVersAndCdKey_queryIsOk() {
        PigVers pigVer = aPigVers();
        String cdKey = aString();
        helper.getSUByVersAndCdKey(pigVer, cdKey);
        assertTrue(true);
    }

    @Test
    public void findPigStrumUrbPianoStatoByNomeTipo_queryIsOk() {
        String nomeTipo = aString();
        helper.findPigStrumUrbPianoStatoByNomeTipo(nomeTipo);
        assertTrue(true);
    }

    @Test
    public void getPigStrumUrbPianoStatoByNomeTipoByTipoAndFase_queryIsOk() {
        String nmTipoStrumentoUrbanistico = aString();
        String tiFaseStrumento = aString();
        helper.getPigStrumUrbPianoStatoByNomeTipoByTipoAndFase(nmTipoStrumentoUrbanistico, tiFaseStrumento);
        assertTrue(true);
    }

    @Test
    public void getPigStrumUrbValDocByNomeTipoDoc_queryIsOk() {
        String nmTipoDocumento = aString();
        helper.getPigStrumUrbValDocByNomeTipoDoc(nmTipoDocumento);
        assertTrue(true);
    }

    @Test
    public void getPigStrumUrbDocumentiBySuNmFileOrig_queryIsOk() {
        PigStrumentiUrbanistici pigStrumentiUrbanistici = null;
        String nmFileOrig = aString();
        helper.getPigStrumUrbDocumentiBySuNmFileOrig(pigStrumentiUrbanistici, nmFileOrig);
        assertTrue(true);
    }

    @Test
    public void getPigStrumUrbByCdKeyAndTiStato_queryIsOk() {
        String cdKey = aString();
        PigStrumentiUrbanistici.TiStato tiStato = null;
        helper.getPigStrumUrbByCdKeyAndTiStato(cdKey, tiStato);
        assertTrue(true);
    }

    @Test
    public void getDatiNavigazionePerSU_queryIsOk() {
        BigDecimal idStrumentiUrbanistici = aBigDecimal();
        helper.getDatiNavigazionePerSU(idStrumentiUrbanistici);
        assertTrue(true);
    }

    @Test
    public void findPigVSuLisDocsPianoByTipoStrumentoFase_queryIsOk() {
        String nmTipoStrumento = aString();
        String tiFaseStrumento = aString();
        helper.findPigVSuLisDocsPianoByTipoStrumentoFase(nmTipoStrumento, tiFaseStrumento);
        assertTrue(true);
    }

    @Test
    public void getDatiAnagraficiByVers_queryIsOk() {
        PigVers vers = aPigVers();
        helper.getDatiAnagraficiByVers(vers);
        assertTrue(true);
    }

    @Test
    public void getDatiAnagraficiByIdVers_queryIsOk() {
        BigDecimal idVers = aBigDecimal();
        helper.getDatiAnagraficiByIdVers(idVers);
        assertTrue(true);
    }

    @Test
    public void getDimensioneDocumentiBySU_queryIsOk() {
        PigStrumentiUrbanistici pigStrumentiUrbanistici = null;
        helper.getDimensioneDocumentiBySU(pigStrumentiUrbanistici);
        assertTrue(true);
    }

    @Test
    public void findDatiAmbienteByIdSU_queryIsOk() {
        BigDecimal id = aBigDecimal();
        helper.findDatiAmbienteByIdSU(id);
        assertTrue(true);
    }

    @Test
    public void existsPigStrumUrbDocumenti_queryIsOk() {
        BigDecimal idStrumentoUrbanistico = aBigDecimal();
        helper.existsPigStrumUrbDocumenti(idStrumentoUrbanistico);
        assertTrue(true);
    }
}
