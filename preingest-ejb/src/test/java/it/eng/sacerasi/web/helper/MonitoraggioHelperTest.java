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
import static it.eng.ArquillianUtils.aDateArray;
import static it.eng.ArquillianUtils.aFlag;
import static it.eng.ArquillianUtils.aListOfBigDecimal;
import static it.eng.ArquillianUtils.aListOfString;
import static it.eng.ArquillianUtils.aLong;
import static it.eng.ArquillianUtils.aSetOfBigDecimal;
import static it.eng.ArquillianUtils.aString;
import static it.eng.ArquillianUtils.aStringArray;
import static it.eng.ArquillianUtils.throwExceptionIf;
import static it.eng.ArquillianUtils.todayTs;
import static it.eng.ArquillianUtils.tomorrowTs;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.container.annotation.ArquillianTest;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import it.eng.ArquillianUtils;
import it.eng.ExpectedException;
import it.eng.sacerasi.web.dto.MonitoraggioFiltriListaOggDerVersFallitiBean;
import it.eng.sacerasi.web.dto.MonitoraggioFiltriListaOggettiBean;
import it.eng.sacerasi.web.dto.MonitoraggioFiltriListaVersFallitiBean;
import it.eng.spagoCore.error.EMFError;

/**
 * @author manuel.bertuzzi@eng.it
 */
@ArquillianTest
public class MonitoraggioHelperTest {
    @EJB
    private MonitoraggioHelper helper;

    @Deployment
    public static Archive<?> createTestArchive() {
	return ArquillianUtils.createPingJar(MonitoraggioHelper.class)
		.addClass("it.eng.sacerasi.web.util.Transform")
		.addPackages(true, "it.eng.sacerasi.web.dto", "org.springframework.cglib.proxy")
		.addPackages(false, "org.springframework.cglib.core");
    }

    @Test
    void entityManagerIsNotNull() {
	assertNotNull(helper.getEntityManager());
    }

    @Test
    void getMonVRiepVersViewBean_queryIsOk() {
	List<Object> idVersList = new ArrayList<>();
	idVersList.add(BigDecimal.valueOf(-1));
	helper.getMonVRiepVersViewBean(idVersList);
	assertTrue(true);
    }

    @Test
    void getMonVObjRangeDtTableBean_queryIsOk() {
	Long idUser = aLong();
	BigDecimal idAmbienteVers = aBigDecimal();
	BigDecimal idVers = aBigDecimal();
	BigDecimal idTipoObject = aBigDecimal();
	BigDecimal idObject = aBigDecimal();
	String cdObjectKey = aString();
	helper.getMonVObjRangeDtTableBean(idUser, idAmbienteVers, idVers, idTipoObject, idObject,
		cdObjectKey);
	assertTrue(true);
    }

    @Test
    void getMonVObjAnnulRangeDtTableBeansSoloAmbiente_queryIsOk() {
	Long idUser = aLong();
	BigDecimal idAmbienteVers = aBigDecimal();
	BigDecimal idVers = null;
	BigDecimal idTipoObject = null;
	helper.getMonVObjAnnulRangeDtTableBean(idUser, idAmbienteVers, idVers, idTipoObject);
	assertTrue(true);
    }

    @Test
    void getMonVObjAnnulRangeDtTableBeansAmbienteVersamento_queryIsOk() {
	Long idUser = aLong();
	BigDecimal idAmbienteVers = aBigDecimal();
	BigDecimal idVers = aBigDecimal();
	BigDecimal idTipoObject = null;
	helper.getMonVObjAnnulRangeDtTableBean(idUser, idAmbienteVers, idVers, idTipoObject);
	assertTrue(true);
    }

    @Test
    void getMonVSesRangeDtTableBean_queryIsOk() {
	Long idUser = aLong();
	BigDecimal idAmbienteVers = aBigDecimal();
	BigDecimal idVers = aBigDecimal();
	String nmTipoObject = aString();
	helper.getMonVSesRangeDtTableBean(idUser, idAmbienteVers, idVers, nmTipoObject);
	assertTrue(true);
    }

    @Test
    void getMonVSesRangeDtTableBeanSoloAmbiente_queryIsOk() {
	Long idUser = aLong();
	BigDecimal idAmbienteVers = aBigDecimal();
	BigDecimal idVers = null;
	String nmTipoObject = null;
	helper.getMonVSesRangeDtTableBean(idUser, idAmbienteVers, idVers, nmTipoObject);
	assertTrue(true);
    }

    @Test
    void getMonVSesRangeDtTableBeanAmbienteVersatore_queryIsOk() {
	Long idUser = aLong();
	BigDecimal idAmbienteVers = aBigDecimal();
	BigDecimal idVers = aBigDecimal();
	String nmTipoObject = null;
	helper.getMonVSesRangeDtTableBean(idUser, idAmbienteVers, idVers, nmTipoObject);
	assertTrue(true);
    }

    @Test
    void getSessioniErrateListTB_queryIsOk() {
	String flVerificato = aString();
	int maxResults = 10;
	helper.getSessioniErrateListTB(flVerificato, maxResults);
	assertTrue(true);
    }

    @Test
    // non si riesce a fare rollback per via della flush all'interno del metodo, quindi testo con id
    // negativo e mi
    // aspetto che non trovi nulla
    void saveFlVerificati_queryIsOk() {
	BigDecimal idSesErr = BigDecimal.valueOf(-99);
	String flSesErrVerif = "0";
	assertThrows(ExpectedException.class, () -> {
	    try {
		helper.saveFlVerificati(idSesErr, flSesErrVerif);
	    } catch (Exception e) {
		throwExceptionIf(ExpectedException.class, e,
			"java.lang.IndexOutOfBoundsException: Index: 0, Size: 0");
	    }
	});
    }

    @Test
    void getMonVLisObjViewBean_queryIsOk() {
	MonitoraggioFiltriListaOggettiBean filtri = new MonitoraggioFiltriListaOggettiBean();
	filtri.setAnno(aBigDecimal());
	filtri.setChiave(aString());
	filtri.setCodice(aString());
	filtri.setGiornoVersA(tomorrowTs());
	filtri.setGiornoVersAValidato(tomorrowTs());
	filtri.setGiornoVersDa(todayTs());
	filtri.setGiornoVersDaValidato(todayTs());
	filtri.setIdAmbienteVers(aBigDecimal());
	filtri.setIdTipoObject(aBigDecimal());
	filtri.setIdVers(aBigDecimal());
	filtri.setMinutiVersA(aBigDecimal());
	filtri.setMinutiVersDa(aBigDecimal());
	filtri.setOreVersA(aBigDecimal());
	filtri.setOreVersDa(aBigDecimal());
	filtri.setPeriodoVers(aString());
	filtri.setRegistro(aString());
	filtri.setStatoObject(aListOfString(2));
	filtri.setTiVersFile(aListOfString(2));
	helper.getMonVLisObjViewBean(filtri);
	assertTrue(true);
    }

    @Test
    void getMonVLisObjDCMHashViewBean_queryIsOk() {
	BigDecimal idObject = aBigDecimal();
	BigDecimal idVers = aBigDecimal();
	String dsDcmHash = aString();
	helper.getMonVLisObjDCMHashViewBean(idObject, idVers, dsDcmHash);
	assertTrue(true);
    }

    @Test
    void getMonVVisSesErrataRowBean_queryIsOk() {
	BigDecimal idSessione = aBigDecimal();
	helper.getMonVVisSesErrataRowBean(idSessione);
	assertTrue(true);
    }

    @Test
    void getMonVLisSchedJobViewBean_queryIsOk() {
	String nmJob = aString();
	Date[] dateValidate = aDateArray(2);
	helper.getMonVLisSchedJobViewBean(dateValidate, nmJob);
	assertTrue(true);
    }

    @Test
    void getMonVVisLastSchedJob_queryIsOk() {
	String nomeJob = aString();
	helper.getMonVVisLastSchedJob(nomeJob);
	assertTrue(true);
    }

    @Test
    void getMonVVisObjRowBean_queryIsOk() {
	BigDecimal idObject = aBigDecimal();
	helper.getMonVVisObjRowBean(idObject);
	assertTrue(true);
    }

    @Test
    void getPigInfoDicomRowBean_queryIsOk() {
	BigDecimal idObject = aBigDecimal();
	helper.getPigInfoDicomRowBean(idObject);
	assertTrue(true);
    }

    @Test
    void getMonVLisUnitaDocObjectTableBean_BigDecimal_queryIsOk() {
	BigDecimal idObject = aBigDecimal();
	helper.getMonVLisUnitaDocObjectTableBean(idObject);
	assertTrue(true);
    }

    @Test
    void getMonVLisUnitaDocObjectTableBean_6args_queryIsOk() {
	BigDecimal idObject = aBigDecimal();
	String cdRegistroUnitaDoc = aString();
	BigDecimal aaKeyUnitaDoc = aBigDecimal();
	String cdKeyUnitaDoc = aString();
	String tiStatoUnitaDocObject = aString();
	String cdErr = aString();
	helper.getMonVLisUnitaDocObjectTableBean(idObject, cdRegistroUnitaDoc, aaKeyUnitaDoc,
		cdKeyUnitaDoc, tiStatoUnitaDocObject, cdErr);
	assertTrue(true);
    }

    @Test
    void getCdErrSacerFromMonVLisUnitaDocObjectTableBean_queryIsOk() {
	BigDecimal idObject = aBigDecimal();
	helper.getCdErrSacerFromMonVLisUnitaDocObjectTableBean(idObject);
	assertTrue(true);
    }

    @Test
    void getMonVLisVersObjTableBean_queryIsOk() {
	BigDecimal idObject = aBigDecimal();
	helper.getMonVLisVersObjTableBean(idObject);
	assertTrue(true);
    }

    @Test
    void getMonVLisFileObjectTableBean_queryIsOk() {
	BigDecimal idObject = aBigDecimal();
	helper.getMonVLisFileObjectTableBean(idObject);
	assertTrue(true);
    }

    @Test
    void getMonVVisUnitaDocObjectRowBean_queryIsOk() {
	BigDecimal idObject = aBigDecimal();
	helper.getMonVVisUnitaDocObjectRowBean(idObject);
	assertTrue(true);
    }

    @Test
    void getMonVLisVersFallitiViewBean_queryIsOk() {
	MonitoraggioFiltriListaVersFallitiBean filtriVers = new MonitoraggioFiltriListaVersFallitiBean();
	filtriVers.setClasseErrore(aString());
	filtriVers.setErrore(aString());
	filtriVers.setGiornoVersA(tomorrowTs());
	filtriVers.setGiornoVersAValidato(tomorrowTs());
	filtriVers.setGiornoVersDa(todayTs());
	filtriVers.setGiornoVersDaValidato(todayTs());
	filtriVers.setIdAmbienteVers(aBigDecimal());
	filtriVers.setIdTipoObject(aBigDecimal());
	filtriVers.setIdVers(aBigDecimal());
	filtriVers.setMinutiVersA(aBigDecimal());
	filtriVers.setMinutiVersDa(aBigDecimal());
	filtriVers.setNmTipoObject(aString());
	filtriVers.setNonRisolubili(aFlag());
	filtriVers.setOreVersA(aBigDecimal());
	filtriVers.setOreVersDa(aBigDecimal());
	filtriVers.setPeriodoVers(aString());
	filtriVers.setStati(aListOfString(2));
	filtriVers.setStatoRisoluzione(aString());
	filtriVers.setTipoErrore(aString());
	filtriVers.setVerificati(aFlag());
	helper.getMonVLisVersFallitiViewBean(filtriVers);
	assertTrue(true);
    }

    @Test
    @Disabled("non usa query hql e potenzialmente modifica dati su db")
    void saveFlVerificatiNonRisolubili_queryIsOk() {
	BigDecimal idSessioneIngest = aBigDecimal();
	String flSessioneErrVerif = aString();
	String flSessioneErrNonRisolub = aString();
	helper.saveFlVerificatiNonRisolubili(idSessioneIngest, flSessioneErrVerif,
		flSessioneErrNonRisolub);
	assertTrue(true);
    }

    @Test
    void getMonVObjNonVersTableBean_queryIsOk() {
	Long idUser = aLong();
	BigDecimal idAmbienteVers = aBigDecimal();
	BigDecimal idVers = aBigDecimal();
	String nmTipoObject = aString();
	helper.getMonVObjNonVersTableBean(idUser, idAmbienteVers, idVers, nmTipoObject);
	assertTrue(true);
    }

    @Test
    void getMonVObjNonVersTableBeanSoloAmbiente_queryIsOk() {
	Long idUser = aLong();
	BigDecimal idAmbienteVers = aBigDecimal();
	BigDecimal idVers = null;
	String nmTipoObject = null;
	helper.getMonVObjNonVersTableBean(idUser, idAmbienteVers, idVers, nmTipoObject);
	assertTrue(true);
    }

    @Test
    void getMonVObjNonVersTableBeanAmbienteVersatore_queryIsOk() {
	Long idUser = aLong();
	BigDecimal idAmbienteVers = aBigDecimal();
	BigDecimal idVers = aBigDecimal();
	String nmTipoObject = null;
	helper.getMonVObjNonVersTableBean(idUser, idAmbienteVers, idVers, nmTipoObject);
	assertTrue(true);
    }

    @Test
    void getMonVVisVersFallitoRowBean_queryIsOk() {
	BigDecimal idSessioneIngest = aBigDecimal();
	helper.getMonVVisVersFallitoRowBean(idSessioneIngest);
	assertTrue(true);
    }

    @Test
    // uso un id negativo e mi aspetto che dia un errore dipendente dal fatto di non aver trovato
    // record
    void salvaDettaglioVersamento_queryIsOk() {
	BigDecimal idSessioneIngest = BigDecimal.valueOf(-99);
	String flSessioneErrVerif = aString();
	String flSessioneErrNonRisolub = aString();

	assertThrows(ExpectedException.class, () -> {
	    try {
		helper.salvaDettaglioVersamento(idSessioneIngest, flSessioneErrVerif,
			flSessioneErrNonRisolub);
	    } catch (Exception e) {
		throwExceptionIf(ExpectedException.class, e,
			"java.lang.IndexOutOfBoundsException: Index: 0, Size: 0");
	    }
	});
    }

    @Test
    void salvaFlVersSacerDaRecup_queryIsOk() {
	BigDecimal idVers = BigDecimal.valueOf(-99);
	String cdKeyObject = "FAKE FAKE FAKE";
	String flVersSacerDaRecup = aString();
	helper.salvaFlVersSacerDaRecup(idVers, cdKeyObject, flVersSacerDaRecup);
	assertTrue(true);
    }

    @Test
    @Disabled("non usa hql e potenzialmente modifica dei dati")
    void salvaNoteInfoTipoGestFigliObject_queryIsOk() {
	BigDecimal idObject = aBigDecimal();
	String note = aString();
	String info = aString();
	String tipoGestioneFigli = aString();
	boolean isStudioDicom = false;
	String sPriorita = aString();
	String sPrioritaVersamento = aString();
	helper.updatePigObject(idObject, note, info, tipoGestioneFigli, isStudioDicom, sPriorita,
		sPrioritaVersamento, "username");
	assertTrue(true);
    }

    @Test
    void getDsPathInputFtp_queryIsOk() {
	BigDecimal idVers = BigDecimal.valueOf(-1);

	assertThrows(ExpectedException.class, () -> {
	    try {
		helper.getDsPathInputFtp(idVers);
	    } catch (Exception e) {
		throwExceptionIf(ExpectedException.class, e,
			"Unable to find it.eng.sacerasi.entity.PigVers with id " + idVers);
	    }
	});
    }

    @Test
    void getDsPathTrasf_queryIsOk() {
	BigDecimal idVers = BigDecimal.valueOf(-1);

	assertThrows(ExpectedException.class, () -> {
	    try {
		helper.getDsPathTrasf(idVers);
	    } catch (Exception e) {
		throwExceptionIf(ExpectedException.class, e,
			"Unable to find it.eng.sacerasi.entity.PigVers with id " + idVers);
	    }
	});
    }

    @Test
    void getMonVLisObjNonVersViewBean_queryIsOk() {
	MonitoraggioFiltriListaOggDerVersFallitiBean filtriObj = new MonitoraggioFiltriListaOggDerVersFallitiBean();
	filtriObj.setDaRecuperare(aFlag());
	filtriObj.setIdAmbienteVers(aBigDecimal());
	filtriObj.setIdTipoObject(aBigDecimal());
	filtriObj.setIdVers(aBigDecimal());
	filtriObj.setNmTipoObject(aString());
	filtriObj.setNonRisolubili(aFlag());
	filtriObj.setVerificati(aFlag());
	helper.getMonVLisObjNonVersViewBean(filtriObj);
	assertTrue(true);
    }

    @Test
    void getMonVVisObjNonVersRowBean_queryIsOk() throws EMFError {
	BigDecimal idVers = aBigDecimal();
	String cdKeyObject = aString();
	helper.getMonVVisObjNonVersRowBean(idVers, cdKeyObject);
	assertTrue(true);
    }

    @Test
    void getObjNonVersFlVerif_queryIsOk() {
	BigDecimal idVers = aBigDecimal();
	String cdKeyObject = aString();

	assertThrows(ExpectedException.class, () -> {
	    try {
		helper.getObjNonVersFlVerif(idVers, cdKeyObject);
	    } catch (Exception e) {
		throwExceptionIf(ExpectedException.class, e,
			"java.lang.IndexOutOfBoundsException: Index: 0, Size: 0");
	    }
	});
    }

    @Test
    void getPigObject_queryIsOk() {
	BigDecimal idVers = aBigDecimal();
	String cdKeyObject = aString();
	helper.getPigObject(idVers, cdKeyObject);
	assertTrue(true);
    }

    @Test
    void getPigObjectRowBean_queryIsOk() {
	BigDecimal idVers = aBigDecimal();
	String cdKeyObject = aString();
	helper.getPigObjectRowBean(idVers, cdKeyObject);
	assertTrue(true);
    }

    @Test
    void getMonVLisVersObjNonVersViewBean_queryIsOk() throws EMFError {
	BigDecimal idVers = aBigDecimal();
	String cdKeyObject = aString();
	helper.getMonVLisVersObjNonVersViewBean(idVers, cdKeyObject);
	assertTrue(true);
    }

    @Test
    void getAmbienteVersFromIdVers_queryIsOk() {
	BigDecimal idVers = BigDecimal.valueOf(-1);

	assertThrows(ExpectedException.class, () -> {
	    try {
		helper.getAmbienteVersFromIdVers(idVers);
	    } catch (Exception e) {
		throwExceptionIf(ExpectedException.class, e,
			"javax.persistence.NoResultException: No entity found for query");
	    }
	});
    }

    @Test
    void getNomeVersFromId_queryIsOk() {
	BigDecimal idVers = BigDecimal.valueOf(-1);

	assertThrows(ExpectedException.class, () -> {
	    try {
		helper.getNomeVersFromId(idVers);
	    } catch (Exception e) {
		throwExceptionIf(ExpectedException.class, e, "java.lang.NullPointerException");
	    }
	});
    }

    @Test
    void getIdAmbienteVersatore_queryIsOk() {
	BigDecimal idVers = BigDecimal.valueOf(-1);

	assertThrows(ExpectedException.class, () -> {
	    try {
		helper.getIdAmbienteVersatore(idVers);
	    } catch (Exception e) {
		throwExceptionIf(ExpectedException.class, e,
			"javax.persistence.NoResultException: No entity found for query");
	    }
	});
    }

    @Test
    void getXmlVersErr_queryIsOk() {
	BigDecimal idSessioneIngest = aBigDecimal();

	assertThrows(ExpectedException.class, () -> {
	    try {
		helper.getXmlVersErr(idSessioneIngest);
	    } catch (Exception e) {
		throwExceptionIf(ExpectedException.class, e, "java.lang.NullPointerException");
	    }
	});
    }

    @Test
    void getXmlSesErr_queryIsOk() {
	BigDecimal idSessione = BigDecimal.valueOf(-99);

	assertThrows(ExpectedException.class, () -> {
	    try {
		helper.getXmlSesErr(idSessione);
	    } catch (Exception e) {
		throwExceptionIf(ExpectedException.class, e, "java.lang.NullPointerException");
	    }
	});
    }

    @Test
    void getMonVLisUnitaDocSessioneTableBean_BigDecimal_queryIsOk() {
	BigDecimal idSessioneIngest = aBigDecimal();
	helper.getMonVLisUnitaDocSessioneTableBean(idSessioneIngest);
	assertTrue(true);
    }

    @Test
    void getMonVLisUnitaDocSessioneTableBean_9args_queryIsOk() {
	BigDecimal idSessioneIngest = aBigDecimal();
	String cdRegistroUnitaDoc = aString();
	BigDecimal aaKeyUnitaDoc = aBigDecimal();
	String cdKeyUnitaDoc = aString();
	String tiStatoUnitaDocSessione = aString();
	String cdErr = aString();
	String nmStrut = aString();
	String flStrutturaNonDefinita = aString();
	String flVersSimulato = aString();
	helper.getMonVLisUnitaDocSessioneTableBean(idSessioneIngest, cdRegistroUnitaDoc,
		aaKeyUnitaDoc, cdKeyUnitaDoc, tiStatoUnitaDocSessione, cdErr, nmStrut,
		flStrutturaNonDefinita, flVersSimulato);
	assertTrue(true);
    }

    @Test
    void getCdErrSacerFromMonVLisUnitaDocSessioneTableBean_queryIsOk() {
	BigDecimal idSessioneIngest = aBigDecimal();
	helper.getCdErrSacerFromMonVLisUnitaDocSessioneTableBean(idSessioneIngest);
	assertTrue(true);
    }

    @Test
    void getTiStatoObject_queryIsOk() {
	BigDecimal idVers = aBigDecimal();
	String cdKeyObject = aString();
	helper.getTiStatoObject(idVers, cdKeyObject);
	assertTrue(true);
    }

    @Test
    void getPigSessioneIngestRowBean_queryIsOk() {
	BigDecimal idSessioneIngest = aBigDecimal();
	helper.getPigSessioneIngestRowBean(idSessioneIngest);
	assertTrue(true);
    }

    @Test
    void getPigObjectsFromSessions_queryIsOk() {
	Set<BigDecimal> idSessioneSet = aSetOfBigDecimal(2);
	helper.getPigObjectsFromSessions(idSessioneSet);
	assertTrue(true);
    }

    @Test
    void getIamVLisOrganizDaReplicTableBean_queryIsOk() {
	BigDecimal idAmbiente = aBigDecimal();
	BigDecimal idVers = aBigDecimal();
	String tiOper = aString();
	String tiStato = aString();

	helper.getIamVLisOrganizDaReplicTableBean(idAmbiente, idVers, tiOper, tiStato);
	assertTrue(true);
    }

    @Test
    void retrieveMonVLisObjTrasf_queryIsOk() {
	BigDecimal idObject = aBigDecimal();
	helper.retrieveMonVLisObjTrasf(idObject);
	assertTrue(true);
    }

    @Test
    void retrievePigStatoSessioneIngestFromPigObject_queryIsOk() {
	BigDecimal idObject = aBigDecimal();
	helper.retrievePigStatoSessioneIngestFromPigObject(idObject);
	assertTrue(true);
    }

    @Test
    void retrievePigStatoSessioneIngest_queryIsOk() {
	BigDecimal idSessioneIngest = aBigDecimal();
	helper.retrievePigStatoSessioneIngest(idSessioneIngest);
	assertTrue(true);
    }

    @Test
    void getPigObjectsPadri_queryIsOk() {
	List<BigDecimal> ids = aListOfBigDecimal(2);
	helper.getPigObjectsPadri(ids);
	assertTrue(true);
    }

    @Test
    @Disabled("non usa hql e potenzialmente modifica dati sul DB")
    void creaStatoSessione_queryIsOk() {
	BigDecimal idSessioneIngest = aBigDecimal();
	String statoSessione = aString();
	Date dtRegStato = todayTs();
	helper.creaStatoSessione(idSessioneIngest, statoSessione, dtRegStato);
	assertTrue(true);
    }

    @Test
    void countPigUnitaDocObject_queryIsOk() {
	BigDecimal idObject = aBigDecimal();
	String[] statiUdObject = aStringArray(2);
	helper.countPigUnitaDocObject(idObject, statiUdObject);
	assertTrue(true);
    }

    @Test
    void existsUDPerObjectVersataOkOrVersataErr_queryIsOk() {
	BigDecimal idObject = aBigDecimal();
	String cdErrSacer = aString();
	helper.existsUDPerObjectVersataOkOrVersataErr(idObject, cdErrSacer);
	assertTrue(true);
    }

    @Test
    void getFigliWithStatus_queryIsOk() {
	long idObjectPadre = aLong();
	String[] stati = aStringArray(2);
	helper.getFigliWithStatus(idObjectPadre, stati);
	assertTrue(true);
    }

    @Test
    void getTuttiFigli_queryIsOk() {
	long idObjectPadre = aLong();
	helper.getTuttiFigli(idObjectPadre);
	assertTrue(true);
    }
}
