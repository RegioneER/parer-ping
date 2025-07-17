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
import static it.eng.ArquillianUtils.aListOfBigDecimal;
import static it.eng.ArquillianUtils.aListOfString;
import static it.eng.ArquillianUtils.aLong;
import static it.eng.ArquillianUtils.aString;
import static it.eng.ArquillianUtils.throwExpectedExceptionIfNoResultException;
import static it.eng.ArquillianUtils.throwRollbackExceptionIfNoResultException;
import static it.eng.ArquillianUtils.todayTs;
import static it.eng.ArquillianUtils.tomorrowTs;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.container.annotation.ArquillianTest;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import it.eng.ArquillianUtils;
import it.eng.ExpectedException;
import it.eng.RollbackException;
import it.eng.sacerasi.entity.PigAmbienteVers;
import it.eng.sacerasi.entity.PigVers;
import it.eng.sacerasi.job.allineamentoEntiConvenzionati.util.CostantiAllineaEntiConv;
import it.eng.sacerasi.job.allineamentoEntiConvenzionati.util.CostantiAllineaEntiConv.EsitoServizio;
import it.eng.sacerasi.slite.gen.tablebean.PigTipoObjectRowBean;
import it.eng.sacerasi.slite.gen.tablebean.PigVersRowBean;

/**
 * @author manuel.bertuzzi@eng.it
 */
@ArquillianTest
public class AmministrazioneHelperTest {

    @EJB
    private AmministrazioneHelper helper;

    @Deployment
    public static Archive<?> createTestArchive() {
	return ArquillianUtils.createPingJar(AmministrazioneHelper.class).addPackages(true,
		"it.eng.sacerasi.job.allineamentoEntiConvenzionati.util");
    }

    @Test
    void getPigAmbienteVersAbilitatiListQueryIsOk() {
	String nmAmbienteVers = aString();
	Long idUtente = aLong();
	String nmApplic = aString();
	helper.getPigAmbienteVersAbilitatiList(nmAmbienteVers, idUtente, nmApplic);
	assertTrue(true);
    }

    @Test
    void getPigAmbienteVersByIdQueryIsOk() {
	BigDecimal idAmbienteVers = aBigDecimal();
	helper.getPigAmbienteVersById(idAmbienteVers);
	assertTrue(true);
    }

    @Test
    void getPigAmbienteVersByNameQueryIsOk() {
	String nmAmbienteVers = aString();

	helper.getPigAmbienteVersByName(nmAmbienteVers);
	assertTrue(true);
    }

    @Test
    void getPigVersListQueryIsOk() {
	BigDecimal idAmbienteVers = aBigDecimal();

	helper.getPigVersList(idAmbienteVers);
	assertTrue(true);
    }

    @Test
    void existUtentiUnAmbienteQueryIsOk() {
	BigDecimal idAmbienteVers = aBigDecimal();
	String nmApplic = aString();

	helper.existUtentiUnAmbiente(idAmbienteVers, nmApplic);
	assertTrue(true);
    }

    @Test
    void existPigVersValidiDataOdiernaQueryIsOk() {
	BigDecimal idAmbienteVers = aBigDecimal();

	helper.existPigVersValidiDataOdierna(idAmbienteVers);
	assertTrue(true);
    }

    @Test
    void getPigVersListFromKeyQueryIsOk() {
	String nmVers = aString();
	BigDecimal idAmbienteVers = aBigDecimal();

	helper.getPigVersListFromKey(nmVers, idAmbienteVers);
	assertTrue(true);
    }

    @Test
    void getPigVersListFromComboQueryIsOk() {
	PigVers vers = new PigVers();
	vers.setNmVers(aString());
	vers.setPigAmbienteVer(new PigAmbienteVers());
	vers.getPigAmbienteVer().setNmAmbienteVers(aString());
	Long idUserIam = aLong();
	helper.getPigVersListFromCombo(vers, idUserIam);
	assertTrue(true);
    }

    @Test
    void getPigVRicVersListQueryIsOk() {
	String nmVers = aString();
	String nmAmbienteVers = aString();
	String nmAmbienteSacer = aString();
	String nmEnteSacer = aString();
	String nmStrutSacer = aString();
	String nmUseridSacer = aString();
	String nmAmbienteEnteConvenz = aString();
	String nmEnteConvenz = aString();
	Long idUserIam = aLong();
	String nmTipoVersatore = aString();

	helper.getPigVRicVersList(0L, nmVers, nmAmbienteVers, nmAmbienteSacer, nmEnteSacer,
		nmStrutSacer, nmUseridSacer, nmAmbienteEnteConvenz, nmEnteConvenz, idUserIam,
		nmTipoVersatore);
	assertTrue(true);
    }

    @Test
    void getPigVersByNameQueryIsOk() {
	String nmVers = aString();
	BigDecimal idAmb = aBigDecimal();

	helper.getPigVersByName(nmVers, idAmb);
	assertTrue(true);
    }

    @Test
    void getPigVersByIdQueryIsOk() {
	BigDecimal idVers = aBigDecimal();

	helper.getPigVersById(idVers);
	assertTrue(true);
    }

    @Test
    void getPigVersAbilitatiListQueryIsOk() {
	List<BigDecimal> idVersList = aListOfBigDecimal(2);
	Long idUserIam = aLong();

	helper.getPigVersAbilitatiList(idVersList, idUserIam);
	assertTrue(true);
    }

    @Test
    void existUtentiUnVersatoreQueryIsOk() {
	BigDecimal idVers = aBigDecimal();
	String nmApplic = aString();

	helper.existUtentiUnVersatore(idVers, nmApplic);
	assertTrue(true);
    }

    @Test
    void getPigTipoObjectListQueryIsOk() {
	BigDecimal idVers = aBigDecimal();

	helper.getPigTipoObjectList(idVers);
	assertTrue(true);
    }

    @Test
    void getPigTipoObjectByNameQueryIsOk() {
	String nmTipoObj = aString();
	BigDecimal idVers = aBigDecimal();

	helper.getPigTipoObjectByName(nmTipoObj, idVers);
	assertTrue(true);
    }

    @Test
    void getPigTipoObjectByIdQueryIsOk() {
	BigDecimal idTipoObj = aBigDecimal();

	helper.getPigTipoObjectById(idTipoObj);
	assertTrue(true);
    }

    @Test
    void getPigTipoObjectNoDaTrasfAbilitatiListQueryIsOk() {
	BigDecimal idVers = aBigDecimal();
	long idUtente = aLong();

	helper.getPigTipoObjectNoDaTrasfAbilitatiList(idVers, idUtente);
	assertTrue(true);
    }

    @Test
    void getPigDichVersSacerListQueryIsOk() {
	BigDecimal idVers = aBigDecimal();

	helper.getPigDichVersSacerList(idVers);
	assertTrue(true);
    }

    @Test
    void getPigDichVersSacerQueryIsOk() {
	BigDecimal idVers = aBigDecimal();
	BigDecimal idOrganizIam = aBigDecimal();

	helper.getPigDichVersSacer(idVers, idOrganizIam);
	assertTrue(true);
    }

    @Test
    void getPigDichVersSacerTipoObjQueryIsOk() {
	BigDecimal idTipoObject = aBigDecimal();
	BigDecimal idOrganizIam = aBigDecimal();

	helper.getPigDichVersSacerTipoObj(idTipoObject, idOrganizIam);
	assertTrue(true);
    }

    @Test
    void getPigObjectListByTipoObjQueryIsOk() {
	BigDecimal idTipoObj = aBigDecimal();

	helper.getPigObjectListByTipoObj(idTipoObj);
	assertTrue(true);
    }

    @Test
    void esisteOggettoPerIdTipoQueryIsOk() {
	BigDecimal idTipoOggetto = aBigDecimal();

	helper.esisteOggettoPerIdTipo(idTipoOggetto);
	assertTrue(true);
    }

    @Test
    void getPigTipoFileObjectListQueryIsOk() {
	BigDecimal idtipoObject = aBigDecimal();

	helper.getPigTipoFileObjectList(idtipoObject);
	assertTrue(true);
    }

    @Test
    void getPigTipoFileObjectByNameQueryIsOk() {
	String nmTipoFileObj = aString();
	BigDecimal idTipoObj = aBigDecimal();

	helper.getPigTipoFileObjectByName(nmTipoFileObj, idTipoObj);
	assertTrue(true);
    }

    @Test
    void getPigTipoFileObjectByIdQueryIsOk() {
	BigDecimal idTipoFileObj = aBigDecimal();

	helper.getPigTipoFileObjectById(idTipoFileObj);
	assertTrue(true);
    }

    @Test
    void getPigFileObjectListByTipoFileObjectQueryIsOk() {
	BigDecimal idTipoFileObj = aBigDecimal();

	helper.getPigFileObjectListByTipoFileObject(idTipoFileObj);
	assertTrue(true);
    }

    @Test
    void getPigSopClassDicomListQueryIsOk() {
	String cdSopClassDicom = aString();
	String dsSopClassDicom = aString();

	helper.getPigSopClassDicomList(cdSopClassDicom, dsSopClassDicom);
	assertTrue(true);
    }

    @Test
    void getPigSopClassDicomByNameQueryIsOk() {
	String nmSopClass = aString();

	helper.getPigSopClassDicomByName(nmSopClass);
	assertTrue(true);
    }

    @Test
    void getPigSopClassDicomByIdQueryIsOk() {
	BigDecimal idSopClass = aBigDecimal();

	helper.getPigSopClassDicomById(idSopClass);
	assertTrue(true);
    }

    @Test
    void getPigSopClassDicomVersListQueryIsOk() {
	BigDecimal idSopClassDicom = aBigDecimal();
	BigDecimal idVers = aBigDecimal();

	helper.getPigSopClassDicomVersList(idSopClassDicom, idVers);
	assertTrue(true);
    }

    @Test
    void getPigSopClassDicomDispListQueryIsOk() {
	PigVers vers = new PigVers();
	vers.setIdVers(-99L);
	vers.setNmVers(aString());
	vers.setPigAmbienteVer(new PigAmbienteVers());
	vers.getPigAmbienteVer().setNmAmbienteVers(aString());
	helper.getPigSopClassDicomDispList(vers);
	assertTrue(true);
    }

    @Test
    void getPigSopClassDicomVersQueryIsOk() {
	BigDecimal idSopClassDicom = aBigDecimal();
	BigDecimal idVers = aBigDecimal();

	helper.getPigSopClassDicomVers(idSopClassDicom, idVers);
	assertTrue(true);
    }

    @Test
    void getPigXsdDatiSpecListIdTipoObjQueryIsOk() {
	BigDecimal idTipoObj = aBigDecimal();
	BigDecimal idTipoFileObj = null;

	helper.getPigXsdDatiSpecList(idTipoObj, idTipoFileObj);
	assertTrue(true);
    }

    @Test
    void getPigXsdDatiSpecListIdTipoFileObjQueryIsOk() {
	BigDecimal idTipoObj = null;
	BigDecimal idTipoFileObj = aBigDecimal();

	helper.getPigXsdDatiSpecList(idTipoObj, idTipoFileObj);
	assertTrue(true);
    }

    @Test
    void getPigXsdDatiSpecByIdQueryIsOk() {
	BigDecimal idXsdDatiSpec = aBigDecimal();

	helper.getPigXsdDatiSpecById(idXsdDatiSpec);
	assertTrue(true);
    }

    @Test
    void getFullPigXsdDatiSpecByIdQueryIsOk() {
	BigDecimal idXsdDatiSpec = aBigDecimal();

	helper.getFullPigXsdDatiSpecById(idXsdDatiSpec);
	assertTrue(true);
    }

    @Test
    void getLastXsdDatiSpecQueryIsOk() {
	BigDecimal idTipoObj = aBigDecimal();
	BigDecimal idTipoFileObj = aBigDecimal();

	helper.getLastXsdDatiSpec(idTipoObj, idTipoFileObj);
	assertTrue(true);
    }

    @Test
    void getOrdPigXsdDatiSpecListQueryIsOk() {
	BigDecimal idTipoObj = aBigDecimal();
	BigDecimal idTipoFileObj = aBigDecimal();

	helper.getOrdPigXsdDatiSpecList(idTipoObj, idTipoFileObj);
	assertTrue(true);
    }

    @Test
    void getSecondLastXsdDatiSpecQueryIsOk() {
	BigDecimal idTipoObj = aBigDecimal();
	BigDecimal idTipoFileObj = aBigDecimal();

	helper.getSecondLastXsdDatiSpec(idTipoObj, idTipoFileObj);
	assertTrue(true);
    }

    @Test
    void getPigAttribDatiSpecListQueryIsOk() {
	BigDecimal idXsdDatiSpec = aBigDecimal();

	helper.getPigAttribDatiSpecList(idXsdDatiSpec);
	assertTrue(true);
    }

    @Test
    void getPigInfoDicomListByXsdQueryIsOk() {
	BigDecimal idXsdDatiSpec = aBigDecimal();

	helper.getPigInfoDicomListByXsd(idXsdDatiSpec);
	assertTrue(true);
    }

    @Test
    void getPigStatoObjectListQueryIsOk() {
	helper.getPigStatoObjectList();
	assertTrue(true);
    }

    @Test
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    void updatePigStatoObjectQueryIsOk() {
	String tiStatoObject = aString();
	String dsTiStatoObject = aString();
	assertThrows(RollbackException.class, () -> {
	    try {
		helper.updatePigStatoObject(tiStatoObject, dsTiStatoObject);
	    } catch (Exception e) {
		throwRollbackExceptionIfNoResultException(e);
	    }
	});
    }

    @Test
    void getPigDichVersSacerTipoObjListQueryIsOk() {
	BigDecimal idTipoObject = aBigDecimal();
	helper.getPigDichVersSacerTipoObjList(idTipoObject);
	assertTrue(true);
    }

    @Test
    void retrievePigVersTipoObjectDaTrasfListQueryIsOk() {
	BigDecimal idTipoObject = aBigDecimal();
	helper.retrievePigVersTipoObjectDaTrasfList(idTipoObject);
	assertTrue(true);
    }

    @Test
    void getPigVersTipoObjectDaTrasfCdVersQueryIsOk() {
	BigDecimal idTipoObjectDaTrasf = aBigDecimal();
	String cdVersGen = aString();
	BigDecimal idVersGen = null;
	helper.getPigVersTipoObjectDaTrasf(idTipoObjectDaTrasf, cdVersGen, idVersGen);
	assertTrue(true);
    }

    @Test
    void getPigVersTipoObjectDaTrasfIdVersQueryIsOk() {
	BigDecimal idTipoObjectDaTrasf = aBigDecimal();
	String cdVersGen = null;
	BigDecimal idVersGen = aBigDecimal();
	helper.getPigVersTipoObjectDaTrasf(idTipoObjectDaTrasf, cdVersGen, idVersGen);
	assertTrue(true);
    }

    @Test
    void existsPigObjectGenQueryIsOk() {
	Long idVers = aLong();
	Long idTipoObject = aLong();
	helper.existsPigObjectGen(idVers, idTipoObject);
	assertTrue(true);
    }

    @Test
    void existsPigObjectDaTrasformareQueryIsOk() {
	String cdVersGen = aString();
	Long idTipoObjectTrasf = aLong();
	helper.existsPigObjectDaTrasformare(cdVersGen, idTipoObjectTrasf);
	assertTrue(true);
    }

    @Test
    void getPigVValoreSetParamTrasfListQueryIsOk() {
	BigDecimal idVersTipoObjectDaTrasf = aBigDecimal();
	helper.getPigVValoreSetParamTrasfList(idVersTipoObjectDaTrasf);
	assertTrue(true);
    }

    @Test
    void getPigVValoreSetParamTrasfListByIdSetParamTrasfQueryIsOk() {
	BigDecimal idSetParamTrasf = aBigDecimal();
	helper.getPigVValoreSetParamTrasfListByIdSetParamTrasf(idSetParamTrasf);
	assertTrue(true);
    }

    @Test
    void getPigStoricoVersAmbienteListQueryIsOk() {
	BigDecimal idVers = aBigDecimal();
	helper.getPigStoricoVersAmbienteList(idVers);
	assertTrue(true);
    }

    @Test
    void getPigVValoreParamTrasfListQueryIsOk() {
	BigDecimal idSetParamTrasf = aBigDecimal();
	BigDecimal idVersTipoObjectDaTrasf = aBigDecimal();
	helper.getPigVValoreParamTrasfList(idSetParamTrasf, idVersTipoObjectDaTrasf);
	assertTrue(true);
    }

    @Test
    void getPigVValParamTrasfDefSpecListQueryIsOk() {
	BigDecimal idSetParamTrasf = aBigDecimal();
	BigDecimal idVersTipoObjectDaTrasf = aBigDecimal();
	helper.getPigVValParamTrasfDefSpecList(idSetParamTrasf, idVersTipoObjectDaTrasf);
	assertTrue(true);
    }

    @Test
    void getPigVValParamTrasfDefSpecTypeByNameQueryIsOk() {
	BigDecimal idSetParamTrasf = aBigDecimal();
	BigDecimal idVersTipoObjectDaTrasf = aBigDecimal();
	String nmParamTrasf = aString();

	assertThrows(ExpectedException.class, () -> {
	    try {
		helper.getPigVValParamTrasfDefSpecTypeByName(idSetParamTrasf,
			idVersTipoObjectDaTrasf, nmParamTrasf);
	    } catch (Exception e) {
		throwExpectedExceptionIfNoResultException(e);
	    }
	});
    }

    @Test
    void getPigVValParamTrasfDefSpecByNameQueryIsOk() {
	BigDecimal idSetParamTrasf = aBigDecimal();
	BigDecimal idVersTipoObjectDaTrasf = aBigDecimal();
	String nmParamTrasf = aString();

	assertThrows(ExpectedException.class, () -> {
	    try {
		helper.getPigVValParamTrasfDefSpecByName(idSetParamTrasf, idVersTipoObjectDaTrasf,
			nmParamTrasf);
	    } catch (Exception e) {
		throwExpectedExceptionIfNoResultException(e);
	    }
	});
    }

    @Test
    void getPigValoreSetParamTrasfQueryIsOk() {
	BigDecimal idSetParamTrasf = aBigDecimal();
	BigDecimal idVersTipoObjectDaTrasf = aBigDecimal();
	helper.getPigValoreSetParamTrasf(idSetParamTrasf, idVersTipoObjectDaTrasf);
	assertTrue(true);
    }

    @Test
    void getXfoTrasfQueryIsOk() {
	helper.getXfoTrasf();
	assertTrue(true);
    }

    @Test
    void getAmbientiFromUsrVAbilStrutSacerXpingQueryIsOk() {
	long idUserIam = aLong();
	helper.getAmbientiFromUsrVAbilStrutSacerXping(idUserIam);
	assertTrue(true);
    }

    @Test
    void getAmbienteFromUsrVAbilStrutSacerXpingQueryIsOk() {
	long idUserIam = aLong();
	String nmAmbiente = aString();

	assertThrows(ExpectedException.class, () -> {

	    try {
		helper.getAmbienteFromUsrVAbilStrutSacerXping(idUserIam, nmAmbiente);
	    } catch (Exception e) {
		throwExpectedExceptionIfNoResultException(e);
	    }
	});
    }

    @Test
    void getEntiFromUsrVAbilStrutSacerXping_long_BigDecimalQueryIsOk() {
	long idUserIam = aLong();
	BigDecimal idAmbiente = aBigDecimal();
	helper.getEntiFromUsrVAbilStrutSacerXping(idUserIam, idAmbiente);
	assertTrue(true);
    }

    @Test
    void getEntiFromUsrVAbilStrutSacerXping_long_StringQueryIsOk() {
	long idUserIam = aLong();
	String nmAmbiente = aString();
	helper.getEntiFromUsrVAbilStrutSacerXping(idUserIam, nmAmbiente);
	assertTrue(true);
    }

    @Test
    void getStruttureFromUsrVAbilStrutSacerXping_long_BigDecimalQueryIsOk() {
	long idUserIam = aLong();
	BigDecimal idEnte = aBigDecimal();
	helper.getStruttureFromUsrVAbilStrutSacerXping(idUserIam, idEnte);
	assertTrue(true);
    }

    @Test
    void getStruttureFromUsrVAbilStrutSacerXping_3argsQueryIsOk() {
	long idUserIam = aLong();
	String nmAmbiente = aString();
	String nmEnte = aString();
	helper.getStruttureFromUsrVAbilStrutSacerXping(idUserIam, nmAmbiente, nmEnte);
	assertTrue(true);
    }

    @Test
    void getUsrVChkCreaAmbSacerQueryIsOk() {
	long idUser = aLong();
	String nmApplic = aString();

	assertThrows(ExpectedException.class, () -> {
	    try {
		helper.getUsrVChkCreaAmbSacer(idUser, nmApplic);
	    } catch (Exception e) {
		throwExpectedExceptionIfNoResultException(e);
	    }
	});
    }

    @Test
    void getUsrVAbilAmbXverListQueryIsOk() {
	long idUser = aLong();
	String nmApplic = aString();
	helper.getUsrVAbilAmbXverList(idUser, nmApplic);
	assertTrue(true);
    }

    @Test
    void getNmUseridSacerByPigVLisStrutVersSacerQueryIsOk() {
	helper.getNmUseridSacerByPigVLisStrutVersSacer();
	assertTrue(true);
    }

    @Test
    void getOrganizIamFromUsrVAbilStrutSacerXpingQueryIsOk() {
	long idUserIam = aLong();
	BigDecimal idEnte = aBigDecimal();
	helper.getOrganizIamFromUsrVAbilStrutSacerXping(idUserIam, idEnte);
	assertTrue(true);
    }

    @Test
    void getDlCompositoOrganizAmbientiQueryIsOk() {
	long idUserIam = aLong();
	helper.getDlCompositoOrganizAmbienti(idUserIam);
	assertTrue(true);
    }

    @Test
    void getDlCompositoOrganizEntiQueryIsOk() {
	long idUserIam = aLong();
	helper.getDlCompositoOrganizEnti(idUserIam);
	assertTrue(true);
    }

    @Test
    void getDlCompositoOrganizStruttureQueryIsOk() {
	long idUserIam = aLong();
	helper.getDlCompositoOrganizStrutture(idUserIam);
	assertTrue(true);
    }

    @Test
    void existPigValoreSetParamTrasfQueryIsOk() {
	BigDecimal idVersTipoObjectDaTrasf = aBigDecimal();
	BigDecimal idSetParamTrasf = aBigDecimal();
	helper.existPigValoreSetParamTrasf(idVersTipoObjectDaTrasf, idSetParamTrasf);
	assertTrue(true);
    }

    @Test
    void existPigValoreParamTrasfQueryIsOk() {
	BigDecimal idValoreSetParamTrasf = aBigDecimal();
	BigDecimal idParamTrasf = aBigDecimal();
	helper.existPigValoreParamTrasf(idValoreSetParamTrasf, idParamTrasf);
	assertTrue(true);
    }

    @Test
    void existPigVValParamTrasfDefSpecQueryIsOk() {
	BigDecimal idValoreSetParamTrasf = aBigDecimal();
	String nmParamTrasf = aString();
	helper.existPigVValParamTrasfDefSpec(idValoreSetParamTrasf, nmParamTrasf);
	assertTrue(true);
    }

    @Test
    void getPigObjectTrasf_BigDecimal_StringQueryIsOk() {
	BigDecimal idObject = aBigDecimal();
	String cdKeyObjectTrasf = aString();
	helper.getPigObjectTrasf(idObject, cdKeyObjectTrasf);
	assertTrue(true);
    }

    @Test
    void getPigObjectTrasf_BigDecimal_BigDecimalQueryIsOk() {
	BigDecimal idObject = aBigDecimal();
	BigDecimal pgOggettoTrasf = aBigDecimal();
	helper.getPigObjectTrasf(idObject, pgOggettoTrasf);
	assertTrue(true);
    }

    @Test
    void getPigObjectTrasf_long_StringQueryIsOk() {
	long idVers = aLong();
	String cdKeyObjectTrasf = aString();
	helper.getPigObjectTrasf(idVers, cdKeyObjectTrasf);
	assertTrue(true);
    }

    @Test
    void countPigObjectFigliQueryIsOk() {
	BigDecimal idObjectPadre = aBigDecimal();
	String tiStato = aString();
	helper.countPigObjectFigli(idObjectPadre, tiStato);
	assertTrue(true);
    }

    @Test
    void existPigDichVersSacerQueryIsOk() {
	BigDecimal idVers = aBigDecimal();
	BigDecimal idOrganizIam = aBigDecimal();
	helper.existPigDichVersSacer(idVers, idOrganizIam);
	assertTrue(true);
    }

    @Test
    void existPigDichVersSacerTipoObjQueryIsOk() {
	BigDecimal idTipoObject = aBigDecimal();
	BigDecimal idOrganizIam = aBigDecimal();
	helper.existPigDichVersSacerTipoObj(idTipoObject, idOrganizIam);
	assertTrue(true);
    }

    @Test
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    void bulkDeletePigDichVersSacerTipoObjQueryIsOk() {
	long idTipoObject = -9999L;

	assertThrows(RollbackException.class,
		() -> helper.bulkDeletePigDichVersSacerTipoObj(idTipoObject));
    }

    @Test
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    void bulkDeletePigVersTipoObjectDaTrasfQueryIsOk() {
	long idTipoObject = -9999L;

	assertThrows(RollbackException.class,
		() -> helper.bulkDeletePigVersTipoObjectDaTrasf(idTipoObject));
    }

    @Test
    void findUserQueryIsOk() {
	String username = aString();

	assertThrows(ExpectedException.class, () -> {
	    try {
		helper.findUser(username);
	    } catch (Exception e) {
		throwExpectedExceptionIfNoResultException(e);
	    }
	});
    }

    @Test
    void findByCodiceFiscaleQueryIsOk() {
	String codiceFiscale = aString();
	helper.findByCodiceFiscale(codiceFiscale);
	assertTrue(true);
    }

    @Test
    void getPigParamApplicListQueryIsOk() {
	String tiParamApplic = aString();
	String tiGestioneParam = aString();
	String flAppartApplic = aString();
	String flAppartAmbiente = aString();
	String flAppartVers = aString();
	String flAppartTipoOggetto = aString();
	helper.getPigParamApplicList(tiParamApplic, tiGestioneParam, flAppartApplic,
		flAppartAmbiente, flAppartVers, flAppartTipoOggetto);
	assertTrue(true);
    }

    @Test
    void existsPigParamApplicQueryIsOk() {
	String nmParamApplic = aString();
	BigDecimal idParamApplic = aBigDecimal();
	helper.existsPigParamApplic(nmParamApplic, idParamApplic);
	assertTrue(true);
    }

    @Test
    void getPigValoreParamApplic_long_StringQueryIsOk() {
	long idParamApplic = aLong();
	String tiAppart = aString();
	helper.getPigValoreParamApplic(idParamApplic, tiAppart);
	assertTrue(true);
    }

    @Test
    void getTiParamApplicQueryIsOk() {
	helper.getTiParamApplic();
	assertTrue(true);
    }

    @Test
    void retrievePigAmbienteVersByVersQueryIsOk() {
	BigDecimal idVers = aBigDecimal();

	assertThrows(ExpectedException.class, () -> {
	    try {
		helper.retrievePigAmbienteVersByVers(idVers);
	    } catch (Exception e) {
		throwExpectedExceptionIfNoResultException(e);
	    }
	});
    }

    @Test
    void getPigParamApplicListVers_ListQueryIsOk() {
	List<String> funzione = aListOfString(2);
	helper.getPigParamApplicListVers(funzione);
	assertTrue(true);
    }

    @Test
    void getPigValoreParamApplic_5argsQueryIsOk() {
	BigDecimal idParamApplic = aBigDecimal();
	String tiAppart = aString();
	BigDecimal idAmbienteVers = aBigDecimal();
	BigDecimal idVers = aBigDecimal();
	BigDecimal idTipoObject = aBigDecimal();
	helper.getPigValoreParamApplic(idParamApplic, tiAppart, idAmbienteVers, idVers,
		idTipoObject);
	assertTrue(true);
    }

    @Test
    void getPigParamApplicListAmbienteQueryIsOk() {
	List<String> funzione = aListOfString(2);
	helper.getPigParamApplicListAmbiente(funzione);
	assertTrue(true);
    }

    @Test
    void getPigParamApplicListTipoOggettoQueryIsOk() {
	List<String> funzione = aListOfString(2);
	helper.getPigParamApplicListTipoOggetto(funzione);
	assertTrue(true);
    }

    @Test
    void retrieveAmbientiEntiConvenzAbilitatiQueryIsOk() {
	BigDecimal idUserIam = aBigDecimal();
	helper.retrieveAmbientiEntiConvenzAbilitati(idUserIam);
	assertTrue(true);
    }

    @Test
    void getOrgVRicEnteConvenzList_4argsQueryIsOk() {
	BigDecimal idUserIamCor = aBigDecimal();
	BigDecimal idAmbienteEnteConvenz = aBigDecimal();
	String tiEnteConvenz = aString();
	String flNonConvenz = aString();
	helper.getOrgVRicEnteConvenzList(idUserIamCor, idAmbienteEnteConvenz, tiEnteConvenz,
		flNonConvenz);
	assertTrue(true);
    }

    @Test
    void getOrgVRicEnteConvenzList_3argsQueryIsOk() {
	BigDecimal idUserIamCor = aBigDecimal();
	BigDecimal idAmbienteEnteConvenz = aBigDecimal();
	String flNonConvenz = aString();
	helper.getOrgVRicEnteConvenzList(idUserIamCor, idAmbienteEnteConvenz, flNonConvenz);
	assertTrue(true);
    }

    @Test
    void getEnteConvenzConservQueryIsOk() {
	BigDecimal idEnteSiamGestore = aBigDecimal();
	helper.getEnteConvenzConserv(idEnteSiamGestore);
	assertTrue(true);
    }

    @Test
    void getSIOrgAmbienteEnteConvenzByEnteConvenzQueryIsOk() {
	BigDecimal idEnteConvenz = aBigDecimal();

	assertThrows(ExpectedException.class, () -> {
	    try {
		helper.getSIOrgAmbienteEnteConvenzByEnteConvenz(idEnteConvenz);
	    } catch (Exception e) {
		throwExpectedExceptionIfNoResultException(e);
	    }
	});
    }

    @Test
    void retrieveSIOrgEnteConvenzOrgQueryIsOk() {
	BigDecimal idVers = aBigDecimal();
	helper.retrieveSIOrgEnteConvenzOrg(idVers);
	assertTrue(true);
    }

    @Test
    void retrieveEntiNonConvenzAbilitatiQueryIsOk() {
	BigDecimal idUserIam = aBigDecimal();
	String tiEnteNonConvenz = aString();
	helper.retrieveEntiNonConvenzAbilitati(idUserIam, tiEnteNonConvenz);
	assertTrue(true);
    }

    @Test
    void getSIUsrOrganizIamQueryIsOk() {
	BigDecimal idVers = aBigDecimal();
	helper.getSIUsrOrganizIam(idVers);
	assertTrue(true);
    }

    @Test
    void checkEsistenzaPeriodoValiditaAssociazioneEnteConvenzVersQueryIsOk() {
	BigDecimal idEnteConvenz = aBigDecimal();
	Date dtIniVal = todayTs();
	Date dtFineVal = tomorrowTs();
	helper.checkEsistenzaPeriodoValiditaAssociazioneEnteConvenzVers(idEnteConvenz, dtIniVal,
		dtFineVal);
	assertTrue(true);
    }

    @Test
    void existsPeriodoValiditaAssociazioneEnteConvenzVersAccordoValidoQueryIsOk() {
	BigDecimal idEnteConvenz = aBigDecimal();
	Date dtIniVal = todayTs();
	Date dtFineVal = tomorrowTs();
	helper.existsPeriodoValiditaAssociazioneEnteConvenzVersAccordoValido(idEnteConvenz,
		dtIniVal, dtFineVal);
	assertTrue(true);
    }

    @Test
    void existsAccordoValidoQueryIsOk() {
	BigDecimal idEnteConvenz = aBigDecimal();
	helper.existsAccordoValido(idEnteConvenz);
	assertTrue(true);
    }

    @Test
    void retrieveSiOrgEnteConvenzQueryIsOk() {
	BigDecimal idAmbienteEnteConvenz = aBigDecimal();
	helper.retrieveSiOrgEnteConvenz(idAmbienteEnteConvenz);
	assertTrue(true);
    }

    @Test
    void checkEsistenzaAssociazioneEnteConvenzVersQueryIsOk() {
	String nmApplic = aString();
	BigDecimal idVers = aBigDecimal();
	Date dtIniVal = todayTs();
	Date dtFineVal = tomorrowTs();
	BigDecimal idEnteConvenzOrg = aBigDecimal();
	helper.checkEsistenzaAssociazioneEnteConvenzVers(nmApplic, idVers, dtIniVal, dtFineVal,
		idEnteConvenzOrg);
	assertTrue(true);
    }

    @Test
    void getSIOrgEnteConvenzOrg_3argsQueryIsOk() {
	BigDecimal idVers = aBigDecimal();
	BigDecimal idEnteConvenz = aBigDecimal();
	Date dtIniVal = todayTs();

	assertThrows(ExpectedException.class, () -> {
	    try {
		helper.getSIOrgEnteConvenzOrg(idVers, idEnteConvenz, dtIniVal);
	    } catch (Exception e) {
		throwExpectedExceptionIfNoResultException(e);
	    }
	});
    }

    @Test
    void getSIOrgEnteConvenzOrg_BigDecimalQueryIsOk() {
	BigDecimal idVers = aBigDecimal();
	helper.getSIOrgEnteConvenzOrg(idVers);
	assertTrue(true);
    }

    @Test
    @Disabled("deprecato")
    void getIamEnteSiamDaAllineaQueryIsOk() {
	helper.getIamEnteSiamDaAllinea();
	assertTrue(true);
    }

    @Test
    @Disabled("deprecato")
    void getEntiConvenzionatiAbilitatiQueryIsOk() {
	long idUserIamCor = aLong();
	BigDecimal idAmbienteEnteConvenz = aBigDecimal();
	helper.getEntiConvenzionatiAbilitati(idUserIamCor, idAmbienteEnteConvenz);
	assertTrue(true);
    }

    @Test
    void retrieveEntiConvenzAbilitatiAmbienteQueryIsOk() {
	BigDecimal idUserIamCor = aBigDecimal();
	BigDecimal idAmbienteEnteConvenz = aBigDecimal();
	String flNonConvenz = aString();
	helper.retrieveEntiConvenzAbilitatiAmbiente(idUserIamCor, idAmbienteEnteConvenz,
		flNonConvenz);
	assertTrue(true);
    }

    @Test
    void isStoricoPresenteQueryIsOk() {
	long idVers = aLong();
	long idAmbienteVersExcluded = aLong();
	Date dtIniValAppartAmbiente = todayTs();
	Date dtFinValAppartAmbiente = tomorrowTs();
	helper.isStoricoPresente(idVers, idAmbienteVersExcluded, dtIniValAppartAmbiente,
		dtFinValAppartAmbiente);
	assertTrue(true);
    }

    @Test
    void getOrgEnteConvenzCollegUserAbilListQueryIsOk() {
	BigDecimal idUserIamCor = aBigDecimal();
	BigDecimal idEnteConvenz = aBigDecimal();
	helper.getOrgEnteConvenzCollegUserAbilList(idUserIamCor, idEnteConvenz);
	assertTrue(true);
    }

    @Test
    void retrieveOrgAppartCollegEntiByIdEnteConvenzQueryIsOk() {
	BigDecimal idEnteConvenz = aBigDecimal();
	helper.retrieveOrgAppartCollegEntiByIdEnteConvenz(idEnteConvenz);
	assertTrue(true);
    }

    @Test
    void getFunzioneParametriQueryIsOk() {
	helper.getFunzioneParametri();
	assertTrue(true);
    }

    @Test
    void getEnteConvenzConservListQueryIsOk() {
	long idUserIamCor = aLong();
	BigDecimal idEnteSiamGestore = aBigDecimal();
	helper.getEnteConvenzConservList(idUserIamCor, idEnteSiamGestore);
	assertTrue(true);
    }

    @Test
    void retrieveOrgAccordoEnteValidoQueryIsOk() {
	BigDecimal idEnteConvenz = aBigDecimal();
	helper.retrieveOrgAccordoEnteValido(idEnteConvenz);
	assertTrue(true);
    }

    @Test
    void getOrganizzazioniSacerFromUsrVAbilStrutSacerXpingQueryIsOk() {
	long idUserIam = aLong();
	String tiDichVers = "AMBIENTE";
	helper.getOrganizzazioniSacerFromUsrVAbilStrutSacerXping(idUserIam, tiDichVers);
	tiDichVers = "ENTE";
	helper.getOrganizzazioniSacerFromUsrVAbilStrutSacerXping(idUserIam, tiDichVers);
	tiDichVers = "STRUTTURA";
	helper.getOrganizzazioniSacerFromUsrVAbilStrutSacerXping(idUserIam, tiDichVers);
	assertTrue(true);
    }

    @Test
    void retrieveOrgAppartCollegEntiQueryIsOk() {
	BigDecimal idCollegEntiConvenz = aBigDecimal();
	helper.retrieveOrgAppartCollegEnti(idCollegEntiConvenz);
	assertTrue(true);
    }

    @Test
    void findDistinctByIdEnteConvenzQueryIsOk() {
	BigDecimal idEnteConvenz = BigDecimal.ZERO;
	helper.findDistinctByIdEnteConvenz(idEnteConvenz);
	assertTrue(true);
    }

    @Test
    void getPigXsdDatiSpecListQueryIsOk() {
	helper.getPigXsdDatiSpecList(BigDecimal.ZERO, BigDecimal.ZERO);
	assertTrue(true);
    }

    @Test
    void updatePigVersQueryIsOk() {
	PigVersRowBean rowBean = new PigVersRowBean();
	rowBean.setIdAmbienteVers(BigDecimal.ZERO);

	assertThrows(RollbackException.class, () -> {
	    try {
		helper.updatePigVers(BigDecimal.ZERO, rowBean);
		fail("non deve trovare nessun record con id 0");
	    } catch (Exception e) {
		throwRollbackExceptionIfNoResultException(e);
	    }
	});
    }

    @Test
    void updateTipoObjQueryIsOk() {
	assertThrows(RollbackException.class, () -> {
	    try {
		helper.updateTipoObj(BigDecimal.ZERO, new PigTipoObjectRowBean());
		fail("non deve trovare nessun record con id 0");
	    } catch (Exception e) {
		throwRollbackExceptionIfNoResultException(e);
	    }
	});
    }

    @Test
    void getPigParamApplicListVersQueryIsOk() {
	helper.getPigParamApplicListVers(Arrays.asList("prova"));
	assertTrue(true);
    }

    @Test
    void writeEsitoIamEnteSiamDaAllineaQueryIsOk() {
	for (final EsitoServizio esitoServizio : EsitoServizio.values()) {
	    try {
		helper.writeEsitoIamEnteSiamDaAllinea(0L, esitoServizio,
			CostantiAllineaEntiConv.ERR_666, "dsErr");
		fail("non deve trovare nessun record con id 0");
	    } catch (Exception e) {
		assertTrue(e.getMessage().contains("java.lang.NullPointerException"));
	    }

	}
    }

    @Test
    void countStrutConStessoEnteQueryIsOk() {
	helper.countPigObjectFigli(BigDecimal.ZERO, "tiStato");
	assertTrue(true);
    }
}
