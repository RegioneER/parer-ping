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
package it.eng.xformer.helper;

import static it.eng.ArquillianUtils.aFlag;
import static it.eng.ArquillianUtils.aLong;
import static it.eng.ArquillianUtils.aString;
import static it.eng.ArquillianUtils.todayTs;
import static it.eng.ArquillianUtils.tomorrowTs;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;

import javax.ejb.EJB;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.container.annotation.ArquillianTest;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.jupiter.api.Test;

import it.eng.ArquillianUtils;
import it.eng.sacerasi.entity.PigObject;
import it.eng.sacerasi.entity.PigObjectTrasf;
import it.eng.sacerasi.entity.PigTipoObject;
import it.eng.sacerasi.entity.PigVers;
import it.eng.xformer.dto.RicercaTrasformazioneBean;

/**
 * @author manuel.bertuzzi@eng.it
 */
@ArquillianTest
public class TrasformazioniQueryHelperTest {
    @EJB
    private TrasformazioniQueryHelper helper;

    @Deployment
    public static Archive<?> createTestArchive() {
	return ArquillianUtils.createPingJar(TrasformazioniQueryHelper.class).addPackages(true,
		"it.eng.xformer.dto");
    }

    @Test
    void entityManagerIsNotNull() {
	assertNotNull(helper.getEntityManager());
    }

    @Test
    void searchXfoTrasf_queryIsOk() {
	RicercaTrasformazioneBean filtri = new RicercaTrasformazioneBean();
	filtri.setCd_trasf(aString());
	filtri.setCd_versione_cor(aString());
	filtri.setDs_trasf(aString());
	filtri.setDs_versione_cor(aString());
	filtri.setDt_istituz(todayTs());
	filtri.setDt_soppres(tomorrowTs());
	filtri.setFl_attiva(aFlag());
	helper.searchXfoTrasf(filtri);
	assertTrue(true);
    }

    @Test
    void searchXfoStoricoTrasfbyXfoTrasf_queryIsOk() {
	long idTrasf = aLong();
	helper.searchXfoStoricoTrasfbyXfoTrasf(idTrasf);
	assertTrue(true);
    }

    @Test
    void searchXfoSetParamTrasfbyXfoTrasf_queryIsOk() {
	long idTrasf = aLong();
	helper.searchXfoSetParamTrasfbyXfoTrasf(idTrasf);
	assertTrue(true);
    }

    @Test
    void searchXfoParamTrasfbySet_queryIsOk() {
	long idSetParamTrasf = aLong();
	helper.searchXfoParamTrasfbySet(idSetParamTrasf);
	assertTrue(true);
    }

    @Test
    void transformationNameExists_queryIsOk() {
	String transformationName = aString();
	helper.transformationNameExists(transformationName);
	assertTrue(true);
    }

    @Test
    void parametersSetExists_queryIsOk() {
	String parametersSetName = aString();
	long idTrasf = aLong();
	helper.parametersSetExists(parametersSetName, idTrasf);
	assertTrue(true);
    }

    @Test
    void getParametersSet_queryIsOk() {
	String parametersSetName = aString();
	long idTrasf = aLong();
	helper.getParametersSet(parametersSetName, idTrasf);
	assertTrue(true);
    }

    @Test
    void gettAllXfoParamTrasfbyTrasf_queryIsOk() {
	long idTrasf = aLong();
	helper.gettAllXfoParamTrasfbyTrasf(idTrasf);
	assertTrue(true);
    }

    @Test
    void searchXfoParamTrasfbyName_queryIsOk() {
	String paramenterName = aString();
	long idTrasf = aLong();
	helper.searchXfoParamTrasfbyName(paramenterName, idTrasf);
	assertTrue(true);
    }

    @Test
    void searchPigVValoreParamTrasfByName_queryIsOk() {
	String paramenterName = aString();
	long idVersTipoObjectDaTrasf = aLong();
	helper.searchPigVValoreParamTrasfByName(paramenterName, idVersTipoObjectDaTrasf);
	assertTrue(true);
    }

    @Test
    void getPigVersTipoObjectDaTrasf_queryIsOk() {
	PigObject object = aPigObject();
	helper.getPigVersTipoObjectDaTrasf(object);
	assertTrue(true);
    }

    private PigObject aPigObject() {
	PigObject object = new PigObject();
	object.setCdVersGen(aString());
	object.setPigTipoObject(new PigTipoObject());
	object.getPigTipoObject().setIdTipoObject(aLong());
	object.setIdObject(aLong());
	return object;
    }

    @Test
    void searchGeneratedPigObjects_queryIsOk() {
	PigObject object = aPigObject();
	helper.searchGeneratedPigObjects(object);
	assertTrue(true);
    }

    @Test
    void findGeneratedPigObjectTrasf_queryIsOk() {
	String cdKeyObjectTrasf = aString();
	PigObject object = aPigObject();
	helper.findGeneratedPigObjectTrasf(cdKeyObjectTrasf, object);
	assertTrue(true);
    }

    @Test
    void searchPigObjectTrasfInPigObjects_queryIsOk() {
	PigObjectTrasf pot = new PigObjectTrasf();
	pot.setCdKeyObjectTrasf(aString());
	pot.setPigVer(new PigVers());
	pot.getPigVer().setIdVers(aLong());
	pot.setPigObject(new PigObject());
	pot.getPigObject().setIdObject(aLong());
	helper.searchPigObjectTrasfInPigObjects(pot);
	assertTrue(true);
    }

    @Test
    void isTransformationAssigned_queryIsOk() {
	long idTrasf = aLong();
	helper.isTransformationAssigned(idTrasf);
	assertTrue(true);
    }

    @Test
    void searchAuxiliaryFilesByXfoTras_queryIsOk() {
	long idTrasf = aLong();
	helper.searchAuxiliaryFilesByXfoTras(idTrasf);
	assertTrue(true);
    }

    @Test
    void isVersionUnique_queryIsOk() {
	long idTrasf = aLong();
	String version = aString();
	helper.isVersionUnique(idTrasf, version);
	assertTrue(true);
    }

    @Test
    void isVersionDateUnique_queryIsOk() {
	long idTrasf = aLong();
	Date date = todayTs();
	helper.isVersionDateUnique(idTrasf, date);
	assertTrue(true);
    }

    @Test
    void isDateIstituzOverlapping_queryIsOk() {
	long idTrasf = aLong();
	Date date = todayTs();
	helper.isDateIstituzOverlapping(idTrasf, date);
	assertTrue(true);
    }

    @Test
    void retriveKettleId_queryIsOk() {
	long idTrasf = aLong();
	helper.retriveKettleId(idTrasf);
	assertTrue(true);
    }

}
