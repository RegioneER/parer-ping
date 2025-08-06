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

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import javax.ejb.EJB;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.container.annotation.ArquillianTest;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import it.eng.ArquillianUtils;
import it.eng.sacerasi.entity.PigSisma;
import it.eng.sacerasi.entity.PigVers;
import it.eng.sacerasi.sisma.dto.RicercaSismaDTO;

/**
 *
 * @author manuel.bertuzzi@eng.it
 */

@ArquillianTest
public class SismaHelperTest {

    @EJB
    private SismaHelper helper;

    @Deployment
    public static Archive<?> createArchive() {
	return ArquillianUtils.createPingJar(SismaHelper.class);
    }

    @Test
    void findSismaByVersQueryIsOk() {

	BigDecimal idVers = BigDecimal.ZERO;
	helper.findSismaByVers(new RicercaSismaDTO(), idVers);
	assertTrue(true);
    }

    @Test
    void findSismaQueryIsOk() {
	helper.findSisma();
	assertTrue(true);
    }

    @Test
    void findSismaTranneQueryIsOk() {

	PigSisma.TiStato[] stati = new PigSisma.TiStato[] {
		PigSisma.TiStato.ANNULLATO, PigSisma.TiStato.DA_RIVEDERE };
	helper.findSismaTranne(new RicercaSismaDTO(), stati);
	assertTrue(true);
    }

    @Test
    void findSismaByVersAndStatesQueryIsOk() {
	BigDecimal idVers = BigDecimal.ZERO;
	Set<PigSisma.TiStato> set = new HashSet<>();
	set.add(PigSisma.TiStato.ANNULLATO);
	helper.findSismaByVersAndStates(new RicercaSismaDTO(), idVers, set);
	assertTrue(true);

    }

    @Test
    void findSismaByStatesQueryIsOk() {
	Set<PigSisma.TiStato> set = new HashSet<>();
	set.add(PigSisma.TiStato.ANNULLATO);
	helper.findSismaByStates(set);
	assertTrue(true);

    }

    @Test
    void findDatiPerRecuperoByIdSismaEstraiAgenziaQueryIsOk() {
	BigDecimal id = BigDecimal.ZERO;
	boolean estraiAgenzia = true;
	BigDecimal idAgenzia = BigDecimal.ZERO;
	helper.findDatiPerRecuperoByIdSisma(id, estraiAgenzia, idAgenzia);
	assertTrue(true);
    }

    @Test
    void findDatiPerRecuperoByIdSismaQueryIsOk() {
	BigDecimal id = BigDecimal.ZERO;
	boolean estraiAgenzia = false;
	BigDecimal idAgenzia = BigDecimal.ZERO;
	helper.findDatiPerRecuperoByIdSisma(id, estraiAgenzia, idAgenzia);
	assertTrue(true);
    }

    @Test
    void findSismaByVersAndCdKeyQueryIsOk() {
	PigVers pigVer = new PigVers();
	pigVer.setIdVers(0L);
	String cdKey = "NIENTE";
	helper.findSismaByVersAndCdKey(pigVer, cdKey);
	assertTrue(true);

    }

    @Test
    void findPigSismaStatoProgettoQueryIsOk() {
	helper.findPigSismaStatoProgetto();
	assertTrue(true);
    }

    @Test
    void findPigSismaStatoProgettoByIdSismaFaseProgettoQueryIsOk() {
	BigDecimal idSismaFaseProgetto = BigDecimal.ZERO;
	helper.findPigSismaStatoProgettoByIdSismaFaseProgetto(idSismaFaseProgetto);
	assertTrue(true);
    }

    @Test
    void getPigSismaByCdKeyAndTiStatoQueryIsOk() {
	String cdKey = "NIENTE";
	PigSisma.TiStato tiStato = PigSisma.TiStato.ANNULLATO;
	helper.getPigSismaByCdKeyAndTiStato(cdKey, tiStato);
	assertTrue(true);
    }

    @Test
    void getSismaByVersAndCdKeyQueryIsOk() {
	PigVers pigVer = new PigVers();
	pigVer.setIdVers(0L);
	String cdKey = "NIENTE";
	helper.getSismaByVersAndCdKey(pigVer, cdKey);
	assertTrue(true);
    }

    @Test
    void findPigSismaFinanziamentoQueryIsOk() {
	helper.findPigSismaFinanziamento();
	assertTrue(true);
    }

    @Test
    void findPigSismaFinanziamentoByIdVersQueryIsOk() {
	BigDecimal idVers = BigDecimal.ZERO;
	helper.findPigSismaFinanziamentoByIdVers(idVers);
	assertTrue(true);
    }

    @Test
    void getFasiInseriteFinVersQueryIsOk() {
	BigDecimal idSismaFinanziamento = BigDecimal.ZERO;
	BigDecimal idVers = BigDecimal.ZERO;
	helper.getFasiInseriteFinVers(idSismaFinanziamento, idVers);
	assertTrue(true);
    }

    @Test
    void findPigSismaFaseProgettoByFinQueryIsOk() {
	BigDecimal idSismaFinanziamento = BigDecimal.ZERO;
	helper.findPigSismaFaseProgettoByFin(idSismaFinanziamento);
	assertTrue(true);
    }

    @Test
    void getPigSismaProgettiAgByIdEnteFinanziamentoQueryIsOk() {
	BigDecimal idEnteSiam = BigDecimal.ZERO;
	BigDecimal idSismaFinanziamento = BigDecimal.ZERO;
	helper.getPigSismaProgettiAgByIdEnteFinanziamento(idEnteSiam, idSismaFinanziamento);
	assertTrue(true);
    }

    @Test
    void getPigSismaValAttoByIdQueryIsOk() {
	BigDecimal idSismaValAtto = BigDecimal.ZERO;
	helper.getPigSismaValAttoById(idSismaValAtto);
	assertTrue(true);
    }

    @Test
    void getPigVersById_BigDecimalQueryIsOk() {
	BigDecimal idVers = BigDecimal.ZERO;
	helper.getPigVersById(idVers);
	assertTrue(true);
    }

    @Test
    void getPigVersById_longQueryIsOk() {
	long idVers = 0L;
	helper.getPigVersById(idVers);
	assertTrue(true);
    }

    @Test
    void getSIOrgEnteSiamByIdQueryIsOk() {
	BigDecimal id = BigDecimal.ZERO;
	helper.getSIOrgEnteSiamById(id);
	assertTrue(true);
    }

    @Test
    void getPigSismaValDocByNomeTipoDocQueryIsOk() {
	String nmTipoDocumento = "NIENTE";
	helper.getPigSismaValDocByNomeTipoDoc(nmTipoDocumento);
	assertTrue(true);
    }

    @Test
    @Disabled("evitiamo di cancellare dati a caso")
    void GetPigSismaDocumentiBySismaNmFileOrigQueryIsOk() {
	PigSisma pigSisma = new PigSisma();
	pigSisma.setIdSisma(0L);
	String nmFileOrig = "NIENTE";
	helper.getPigSismaDocumentiBySismaNmFileOrig(pigSisma, nmFileOrig);
	assertTrue(true);
    }

    @Test
    @Disabled("evitiamo di aggiornare dati a caso")
    void aggiornaStatoInNuovaTransazioneQueryIsOk() {
	PigSisma su = new PigSisma();
	su.setIdSisma(0L);
	PigSisma.TiStato tiStato = PigSisma.TiStato.ANNULLATO;
	helper.aggiornaStatoInNuovaTransazione(su, tiStato);
	assertTrue(true);
    }

    @Test
    @Disabled("evitiamo di aggiornare dati a caso")
    void aggiornaStatoQueryIsOk() {
	PigSisma su = new PigSisma();
	su.setIdSisma(0L);
	PigSisma.TiStato tiStato = PigSisma.TiStato.ANNULLATO;
	helper.aggiornaStato(su, tiStato);
	assertTrue(true);
    }

    @Test
    void getDatiNavigazionePerSismaQueryIsOk() {
	BigDecimal idSisma = BigDecimal.ZERO;
	helper.getDatiNavigazionePerSisma(idSisma);
	assertTrue(true);
    }

    @Test
    void findPigSismaDocumentiDaRicaricareByidSismaQueryIsOk() {
	BigDecimal idSisma = BigDecimal.ZERO;
	helper.findPigSismaDocumentiDaRicaricareByidSisma(idSisma);
	assertTrue(true);
    }

    @Test
    void getDatiAnagraficiByIdVersQueryIsOk() {
	BigDecimal idEnteSiam = BigDecimal.ONE;
	helper.getDatiAnagraficiByIdVers(idEnteSiam, null);
	assertTrue(true);
    }

    @Test
    void getOrgEnteSiamByPigVersQueryIsOk() {
	PigVers vers = new PigVers();
	vers.setIdVers(0L);
	helper.getOrgEnteSiamByPigVers(vers);
	assertTrue(true);
    }

    @Test
    void getDimensioneDocumentiBySismaQueryIsOk() {
	PigSisma pigSisma = new PigSisma();
	pigSisma.setIdSisma(0L);
	helper.getDimensioneDocumentiBySisma(pigSisma);
	assertTrue(true);
    }

    @Test
    void findDatiAmbienteByIdSismaQueryIsOk() {
	BigDecimal id = BigDecimal.ZERO;
	helper.findDatiAmbienteByIdSisma(id);
	assertTrue(true);
    }

    @Test
    void wxistsPigSismaDocumentiDaVerificareQueryIsOk() {
	BigDecimal idSisma = BigDecimal.ZERO;
	helper.existsPigSismaDocumentiDaVerificare(idSisma);
	assertTrue(true);
    }
}
