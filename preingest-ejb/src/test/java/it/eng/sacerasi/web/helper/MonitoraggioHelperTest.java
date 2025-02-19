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
package it.eng.sacerasi.web.helper;

import it.eng.ArquillianUtils;
import static it.eng.ArquillianUtils.*;
import it.eng.ExpectedException;
import it.eng.sacerasi.web.dto.MonitoraggioFiltriListaOggDerVersFallitiBean;
import it.eng.sacerasi.web.dto.MonitoraggioFiltriListaOggettiBean;
import it.eng.sacerasi.web.dto.MonitoraggioFiltriListaVersFallitiBean;
import it.eng.spagoCore.error.EMFError;
import java.math.BigDecimal;
import java.util.*;
import javax.ejb.EJB;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author manuel.bertuzzi@eng.it
 */
@RunWith(Arquillian.class)
public class MonitoraggioHelperTest {
    @EJB
    private MonitoraggioHelper helper;

    @Deployment
    public static Archive<?> createTestArchive() {
        return ArquillianUtils.createPingJar(MonitoraggioHelper.class).addClass("it.eng.sacerasi.web.util.Transform")
                .addPackages(true, "it.eng.sacerasi.web.dto", "org.springframework.cglib.proxy")
                .addPackages(false, "org.springframework.cglib.core");
    }

    @Test
    public void entityManagerIsNotNull() {
        assertNotNull(helper.getEntityManager());
    }

    @Test
    public void getMonVRiepVersViewBean_queryIsOk() {
        List<Object> idVersList = new ArrayList<>();
        idVersList.add(BigDecimal.valueOf(-1));
        helper.getMonVRiepVersViewBean(idVersList);
        assertTrue(true);
    }

    @Test
    public void getMonVObjRangeDtTableBean_queryIsOk() {
        Long idUser = aLong();
        BigDecimal idAmbienteVers = aBigDecimal();
        BigDecimal idVers = aBigDecimal();
        BigDecimal idTipoObject = aBigDecimal();
        BigDecimal idObject = aBigDecimal();
        String cdObjectKey = aString();
        helper.getMonVObjRangeDtTableBean(idUser, idAmbienteVers, idVers, idTipoObject, idObject, cdObjectKey);
        assertTrue(true);
    }

    @Test
    public void getMonVObjAnnulRangeDtTableBeansSoloAmbiente_queryIsOk() {
        Long idUser = aLong();
        BigDecimal idAmbienteVers = aBigDecimal();
        BigDecimal idVers = null;
        BigDecimal idTipoObject = null;
        helper.getMonVObjAnnulRangeDtTableBean(idUser, idAmbienteVers, idVers, idTipoObject);
        assertTrue(true);
    }

    @Test
    public void getMonVObjAnnulRangeDtTableBeansAmbienteVersamento_queryIsOk() {
        Long idUser = aLong();
        BigDecimal idAmbienteVers = aBigDecimal();
        BigDecimal idVers = aBigDecimal();
        BigDecimal idTipoObject = null;
        helper.getMonVObjAnnulRangeDtTableBean(idUser, idAmbienteVers, idVers, idTipoObject);
        assertTrue(true);
    }

    @Test
    public void getMonVSesRangeDtTableBean_queryIsOk() {
        Long idUser = aLong();
        BigDecimal idAmbienteVers = aBigDecimal();
        BigDecimal idVers = aBigDecimal();
        String nmTipoObject = aString();
        helper.getMonVSesRangeDtTableBean(idUser, idAmbienteVers, idVers, nmTipoObject);
        assertTrue(true);
    }

    @Test
    public void getMonVSesRangeDtTableBeanSoloAmbiente_queryIsOk() {
        Long idUser = aLong();
        BigDecimal idAmbienteVers = aBigDecimal();
        BigDecimal idVers = null;
        String nmTipoObject = null;
        helper.getMonVSesRangeDtTableBean(idUser, idAmbienteVers, idVers, nmTipoObject);
        assertTrue(true);
    }

    @Test
    public void getMonVSesRangeDtTableBeanAmbienteVersatore_queryIsOk() {
        Long idUser = aLong();
        BigDecimal idAmbienteVers = aBigDecimal();
        BigDecimal idVers = aBigDecimal();
        String nmTipoObject = null;
        helper.getMonVSesRangeDtTableBean(idUser, idAmbienteVers, idVers, nmTipoObject);
        assertTrue(true);
    }

    @Test
    public void getSessioniErrateListTB_queryIsOk() {
        String flVerificato = aString();
        int maxResults = 10;
        helper.getSessioniErrateListTB(flVerificato, maxResults);
        assertTrue(true);
    }

    @Test(expected = ExpectedException.class)
    // non si riesce a fare rollback per via della flush all'interno del metodo, quindi testo con id negativo e mi
    // aspetto che non trovi nulla
    public void saveFlVerificati_queryIsOk() {
        BigDecimal idSesErr = BigDecimal.valueOf(-99);
        String flSesErrVerif = "0";
        try {
            helper.saveFlVerificati(idSesErr, flSesErrVerif);
        } catch (Exception e) {
            throwExceptionIf(ExpectedException.class, e, "java.lang.IndexOutOfBoundsException: Index: 0, Size: 0");
        }
    }

    @Test
    public void getMonVLisObjViewBean_queryIsOk() {
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
    public void getMonVLisObjDCMHashViewBean_queryIsOk() {
        BigDecimal idObject = aBigDecimal();
        BigDecimal idVers = aBigDecimal();
        String dsDcmHash = aString();
        helper.getMonVLisObjDCMHashViewBean(idObject, idVers, dsDcmHash);
        assertTrue(true);
    }

    @Test
    public void getMonVVisSesErrataRowBean_queryIsOk() {
        BigDecimal idSessione = aBigDecimal();
        helper.getMonVVisSesErrataRowBean(idSessione);
        assertTrue(true);
    }

    @Test
    public void getMonVLisSchedJobViewBean_queryIsOk() {
        String nmJob = aString();
        Date[] dateValidate = aDateArray(2);
        helper.getMonVLisSchedJobViewBean(dateValidate, nmJob);
        assertTrue(true);
    }

    @Test
    public void getMonVVisLastSchedJob_queryIsOk() {
        String nomeJob = aString();
        helper.getMonVVisLastSchedJob(nomeJob);
        assertTrue(true);
    }

    @Test
    public void getMonVVisObjRowBean_queryIsOk() {
        BigDecimal idObject = aBigDecimal();
        helper.getMonVVisObjRowBean(idObject);
        assertTrue(true);
    }

    @Test
    public void getPigInfoDicomRowBean_queryIsOk() {
        BigDecimal idObject = aBigDecimal();
        helper.getPigInfoDicomRowBean(idObject);
        assertTrue(true);
    }

    @Test
    public void getMonVLisUnitaDocObjectTableBean_BigDecimal_queryIsOk() {
        BigDecimal idObject = aBigDecimal();
        helper.getMonVLisUnitaDocObjectTableBean(idObject);
        assertTrue(true);
    }

    @Test
    public void getMonVLisUnitaDocObjectTableBean_6args_queryIsOk() {
        BigDecimal idObject = aBigDecimal();
        String cdRegistroUnitaDoc = aString();
        BigDecimal aaKeyUnitaDoc = aBigDecimal();
        String cdKeyUnitaDoc = aString();
        String tiStatoUnitaDocObject = aString();
        String cdErr = aString();
        helper.getMonVLisUnitaDocObjectTableBean(idObject, cdRegistroUnitaDoc, aaKeyUnitaDoc, cdKeyUnitaDoc,
                tiStatoUnitaDocObject, cdErr);
        assertTrue(true);
    }

    @Test
    public void getCdErrSacerFromMonVLisUnitaDocObjectTableBean_queryIsOk() {
        BigDecimal idObject = aBigDecimal();
        helper.getCdErrSacerFromMonVLisUnitaDocObjectTableBean(idObject);
        assertTrue(true);
    }

    @Test
    public void getMonVLisVersObjTableBean_queryIsOk() {
        BigDecimal idObject = aBigDecimal();
        helper.getMonVLisVersObjTableBean(idObject);
        assertTrue(true);
    }

    @Test
    public void getMonVLisFileObjectTableBean_queryIsOk() {
        BigDecimal idObject = aBigDecimal();
        helper.getMonVLisFileObjectTableBean(idObject);
        assertTrue(true);
    }

    @Test
    public void getMonVVisUnitaDocObjectRowBean_queryIsOk() {
        BigDecimal idObject = aBigDecimal();
        helper.getMonVVisUnitaDocObjectRowBean(idObject);
        assertTrue(true);
    }

    @Test
    public void getMonVLisVersFallitiViewBean_queryIsOk() {
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
    @Ignore("non usa query hql e potenzialmente modifica dati su db")
    public void saveFlVerificatiNonRisolubili_queryIsOk() {
        BigDecimal idSessioneIngest = aBigDecimal();
        String flSessioneErrVerif = aString();
        String flSessioneErrNonRisolub = aString();
        helper.saveFlVerificatiNonRisolubili(idSessioneIngest, flSessioneErrVerif, flSessioneErrNonRisolub);
        assertTrue(true);
    }

    @Test
    public void getMonVObjNonVersTableBean_queryIsOk() {
        Long idUser = aLong();
        BigDecimal idAmbienteVers = aBigDecimal();
        BigDecimal idVers = aBigDecimal();
        String nmTipoObject = aString();
        helper.getMonVObjNonVersTableBean(idUser, idAmbienteVers, idVers, nmTipoObject);
        assertTrue(true);
    }

    @Test
    public void getMonVObjNonVersTableBeanSoloAmbiente_queryIsOk() {
        Long idUser = aLong();
        BigDecimal idAmbienteVers = aBigDecimal();
        BigDecimal idVers = null;
        String nmTipoObject = null;
        helper.getMonVObjNonVersTableBean(idUser, idAmbienteVers, idVers, nmTipoObject);
        assertTrue(true);
    }

    @Test
    public void getMonVObjNonVersTableBeanAmbienteVersatore_queryIsOk() {
        Long idUser = aLong();
        BigDecimal idAmbienteVers = aBigDecimal();
        BigDecimal idVers = aBigDecimal();
        String nmTipoObject = null;
        helper.getMonVObjNonVersTableBean(idUser, idAmbienteVers, idVers, nmTipoObject);
        assertTrue(true);
    }

    @Test
    public void getMonVVisVersFallitoRowBean_queryIsOk() {
        BigDecimal idSessioneIngest = aBigDecimal();
        helper.getMonVVisVersFallitoRowBean(idSessioneIngest);
        assertTrue(true);
    }

    @Test(expected = ExpectedException.class)
    // uso un id negativo e mi aspetto che dia un errore dipendente dal fatto di non aver trovato record
    public void salvaDettaglioVersamento_queryIsOk() {
        BigDecimal idSessioneIngest = BigDecimal.valueOf(-99);
        String flSessioneErrVerif = aString();
        String flSessioneErrNonRisolub = aString();
        try {
            helper.salvaDettaglioVersamento(idSessioneIngest, flSessioneErrVerif, flSessioneErrNonRisolub);
        } catch (Exception e) {
            throwExceptionIf(ExpectedException.class, e, "java.lang.IndexOutOfBoundsException: Index: 0, Size: 0");
        }
    }

    @Test
    public void salvaFlVersSacerDaRecup_queryIsOk() {
        BigDecimal idVers = BigDecimal.valueOf(-99);
        String cdKeyObject = "FAKE FAKE FAKE";
        String flVersSacerDaRecup = aString();
        helper.salvaFlVersSacerDaRecup(idVers, cdKeyObject, flVersSacerDaRecup);
        assertTrue(true);
    }

    @Test
    @Ignore("non usa hql e potenzialmente modifica dei dati")
    public void salvaNoteInfoTipoGestFigliObject_queryIsOk() {
        BigDecimal idObject = aBigDecimal();
        String note = aString();
        String info = aString();
        String tipoGestioneFigli = aString();
        boolean isStudioDicom = false;
        String sPriorita = aString();
        String sPrioritaVersamento = aString();
        helper.updatePigObject(idObject, note, info, tipoGestioneFigli, isStudioDicom, sPriorita, sPrioritaVersamento,
                "username");
        assertTrue(true);
    }

    @Test(expected = ExpectedException.class)
    public void getDsPathInputFtp_queryIsOk() {
        BigDecimal idVers = BigDecimal.valueOf(-1);
        try {
            helper.getDsPathInputFtp(idVers);
        } catch (Exception e) {
            throwExceptionIf(ExpectedException.class, e,
                    "Unable to find it.eng.sacerasi.entity.PigVers with id " + idVers);
        }
    }

    @Test(expected = ExpectedException.class)
    public void getDsPathTrasf_queryIsOk() {
        BigDecimal idVers = BigDecimal.valueOf(-1);
        try {
            helper.getDsPathTrasf(idVers);
        } catch (Exception e) {
            throwExceptionIf(ExpectedException.class, e,
                    "Unable to find it.eng.sacerasi.entity.PigVers with id " + idVers);
        }
    }

    @Test
    public void getMonVLisObjNonVersViewBean_queryIsOk() {
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
    public void getMonVVisObjNonVersRowBean_queryIsOk() throws EMFError {
        BigDecimal idVers = aBigDecimal();
        String cdKeyObject = aString();
        helper.getMonVVisObjNonVersRowBean(idVers, cdKeyObject);
        assertTrue(true);
    }

    @Test(expected = ExpectedException.class)
    public void getObjNonVersFlVerif_queryIsOk() throws EMFError {
        BigDecimal idVers = aBigDecimal();
        String cdKeyObject = aString();
        try {
            helper.getObjNonVersFlVerif(idVers, cdKeyObject);
        } catch (Exception e) {
            throwExceptionIf(ExpectedException.class, e, "java.lang.IndexOutOfBoundsException: Index: 0, Size: 0");
        }
    }

    @Test
    public void getPigObject_queryIsOk() {
        BigDecimal idVers = aBigDecimal();
        String cdKeyObject = aString();
        helper.getPigObject(idVers, cdKeyObject);
        assertTrue(true);
    }

    @Test
    public void getPigObjectRowBean_queryIsOk() {
        BigDecimal idVers = aBigDecimal();
        String cdKeyObject = aString();
        helper.getPigObjectRowBean(idVers, cdKeyObject);
        assertTrue(true);
    }

    @Test
    public void getMonVLisVersObjNonVersViewBean_queryIsOk() throws EMFError {
        BigDecimal idVers = aBigDecimal();
        String cdKeyObject = aString();
        helper.getMonVLisVersObjNonVersViewBean(idVers, cdKeyObject);
        assertTrue(true);
    }

    @Test(expected = ExpectedException.class)
    public void getAmbienteVersFromIdVers_queryIsOk() {
        BigDecimal idVers = BigDecimal.valueOf(-1);
        try {
            helper.getAmbienteVersFromIdVers(idVers);
        } catch (Exception e) {
            throwExceptionIf(ExpectedException.class, e,
                    "javax.persistence.NoResultException: No entity found for query");
        }
    }

    @Test(expected = ExpectedException.class)
    public void getNomeVersFromId_queryIsOk() {
        BigDecimal idVers = BigDecimal.valueOf(-1);
        try {
            helper.getNomeVersFromId(idVers);
        } catch (Exception e) {
            throwExceptionIf(ExpectedException.class, e, "java.lang.NullPointerException");
        }
    }

    @Test(expected = ExpectedException.class)
    public void getIdAmbienteVersatore_queryIsOk() {
        BigDecimal idVers = BigDecimal.valueOf(-1);
        try {
            helper.getIdAmbienteVersatore(idVers);
        } catch (Exception e) {
            throwExceptionIf(ExpectedException.class, e,
                    "javax.persistence.NoResultException: No entity found for query");
        }
    }

    @Test(expected = ExpectedException.class)
    public void getXmlVersErr_queryIsOk() {
        BigDecimal idSessioneIngest = aBigDecimal();
        try {
            byte[] result = helper.getXmlVersErr(idSessioneIngest);
        } catch (Exception e) {
            throwExceptionIf(ExpectedException.class, e, "java.lang.NullPointerException");
        }
    }

    @Test(expected = ExpectedException.class)
    public void getXmlSesErr_queryIsOk() {
        BigDecimal idSessione = BigDecimal.valueOf(-99);
        try {
            helper.getXmlSesErr(idSessione);
        } catch (Exception e) {
            throwExceptionIf(ExpectedException.class, e, "java.lang.NullPointerException");
        }
    }

    @Test
    public void getMonVLisUnitaDocSessioneTableBean_BigDecimal_queryIsOk() {
        BigDecimal idSessioneIngest = aBigDecimal();
        helper.getMonVLisUnitaDocSessioneTableBean(idSessioneIngest);
        assertTrue(true);
    }

    @Test
    public void getMonVLisUnitaDocSessioneTableBean_9args_queryIsOk() {
        BigDecimal idSessioneIngest = aBigDecimal();
        String cdRegistroUnitaDoc = aString();
        BigDecimal aaKeyUnitaDoc = aBigDecimal();
        String cdKeyUnitaDoc = aString();
        String tiStatoUnitaDocSessione = aString();
        String cdErr = aString();
        String nmStrut = aString();
        String flStrutturaNonDefinita = aString();
        String flVersSimulato = aString();
        helper.getMonVLisUnitaDocSessioneTableBean(idSessioneIngest, cdRegistroUnitaDoc, aaKeyUnitaDoc, cdKeyUnitaDoc,
                tiStatoUnitaDocSessione, cdErr, nmStrut, flStrutturaNonDefinita, flVersSimulato);
        assertTrue(true);
    }

    @Test
    public void getCdErrSacerFromMonVLisUnitaDocSessioneTableBean_queryIsOk() {
        BigDecimal idSessioneIngest = aBigDecimal();
        helper.getCdErrSacerFromMonVLisUnitaDocSessioneTableBean(idSessioneIngest);
        assertTrue(true);
    }

    @Test
    public void getTiStatoObject_queryIsOk() {
        BigDecimal idVers = aBigDecimal();
        String cdKeyObject = aString();
        helper.getTiStatoObject(idVers, cdKeyObject);
        assertTrue(true);
    }

    @Test
    public void getPigSessioneIngestRowBean_queryIsOk() {
        BigDecimal idSessioneIngest = aBigDecimal();
        helper.getPigSessioneIngestRowBean(idSessioneIngest);
        assertTrue(true);
    }

    @Test
    public void getPigObjectsFromSessions_queryIsOk() {
        Set<BigDecimal> idSessioneSet = aSetOfBigDecimal(2);
        helper.getPigObjectsFromSessions(idSessioneSet);
        assertTrue(true);
    }

    @Test
    public void getIamVLisOrganizDaReplicTableBean_queryIsOk() throws EMFError {
        BigDecimal idAmbiente = aBigDecimal();
        BigDecimal idVers = aBigDecimal();
        String tiOper = aString();
        String tiStato = aString();

        helper.getIamVLisOrganizDaReplicTableBean(idAmbiente, idVers, tiOper, tiStato);
        assertTrue(true);
    }

    @Test
    public void retrieveMonVLisObjTrasf_queryIsOk() {
        BigDecimal idObject = aBigDecimal();
        helper.retrieveMonVLisObjTrasf(idObject);
        assertTrue(true);
    }

    @Test
    public void retrievePigStatoSessioneIngestFromPigObject_queryIsOk() {
        BigDecimal idObject = aBigDecimal();
        helper.retrievePigStatoSessioneIngestFromPigObject(idObject);
        assertTrue(true);
    }

    @Test
    public void retrievePigStatoSessioneIngest_queryIsOk() {
        BigDecimal idSessioneIngest = aBigDecimal();
        helper.retrievePigStatoSessioneIngest(idSessioneIngest);
        assertTrue(true);
    }

    @Test
    public void getPigObjectsPadri_queryIsOk() {
        List<BigDecimal> ids = aListOfBigDecimal(2);
        helper.getPigObjectsPadri(ids);
        assertTrue(true);
    }

    @Test
    @Ignore("non usa hql e potenzialmente modifica dati sul DB")
    public void creaStatoSessione_queryIsOk() {
        BigDecimal idSessioneIngest = aBigDecimal();
        String statoSessione = aString();
        Date dtRegStato = todayTs();
        helper.creaStatoSessione(idSessioneIngest, statoSessione, dtRegStato);
        assertTrue(true);
    }

    @Test
    public void countPigUnitaDocObject_queryIsOk() {
        BigDecimal idObject = aBigDecimal();
        String[] statiUdObject = aStringArray(2);
        helper.countPigUnitaDocObject(idObject, statiUdObject);
        assertTrue(true);
    }

    @Test
    public void existsUDPerObjectVersataOkOrVersataErr_queryIsOk() {
        BigDecimal idObject = aBigDecimal();
        String cdErrSacer = aString();
        helper.existsUDPerObjectVersataOkOrVersataErr(idObject, cdErrSacer);
        assertTrue(true);
    }

    @Test
    public void getFigliWithStatus_queryIsOk() {
        long idObjectPadre = aLong();
        String[] stati = aStringArray(2);
        helper.getFigliWithStatus(idObjectPadre, stati);
        assertTrue(true);
    }

    @Test
    public void getTuttiFigli_queryIsOk() {
        long idObjectPadre = aLong();
        helper.getTuttiFigli(idObjectPadre);
        assertTrue(true);
    }
}
