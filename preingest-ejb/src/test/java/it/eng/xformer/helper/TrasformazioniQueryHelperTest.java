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
package it.eng.xformer.helper;

import it.eng.ArquillianUtils;
import it.eng.sacerasi.entity.*;
import it.eng.xformer.dto.RicercaTrasformazioneBean;

import java.util.Date;
import javax.ejb.EJB;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Test;
import org.junit.runner.RunWith;

import static it.eng.ArquillianUtils.*;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author manuel.bertuzzi@eng.it
 */
@RunWith(Arquillian.class)
public class TrasformazioniQueryHelperTest {
    @EJB
    private TrasformazioniQueryHelper helper;

    @Deployment
    public static Archive<?> createTestArchive() {
        return ArquillianUtils.createPingJar(TrasformazioniQueryHelper.class).addPackages(true, "it.eng.xformer.dto");
    }

    @Test
    public void entityManagerIsNotNull() {
        assertNotNull(helper.getEntityManager());
    }

    @Test
    public void searchXfoTrasf_queryIsOk() {
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
    public void searchXfoStoricoTrasfbyXfoTrasf_queryIsOk() {
        long idTrasf = aLong();
        helper.searchXfoStoricoTrasfbyXfoTrasf(idTrasf);
        assertTrue(true);
    }

    @Test
    public void searchXfoSetParamTrasfbyXfoTrasf_queryIsOk() {
        long idTrasf = aLong();
        helper.searchXfoSetParamTrasfbyXfoTrasf(idTrasf);
        assertTrue(true);
    }

    @Test
    public void searchXfoParamTrasfbySet_queryIsOk() {
        long idSetParamTrasf = aLong();
        helper.searchXfoParamTrasfbySet(idSetParamTrasf);
        assertTrue(true);
    }

    @Test
    public void transformationNameExists_queryIsOk() {
        String transformationName = aString();
        helper.transformationNameExists(transformationName);
        assertTrue(true);
    }

    @Test
    public void parametersSetExists_queryIsOk() {
        String parametersSetName = aString();
        long idTrasf = aLong();
        helper.parametersSetExists(parametersSetName, idTrasf);
        assertTrue(true);
    }

    @Test
    public void getParametersSet_queryIsOk() {
        String parametersSetName = aString();
        long idTrasf = aLong();
        helper.getParametersSet(parametersSetName, idTrasf);
        assertTrue(true);
    }

    @Test
    public void gettAllXfoParamTrasfbyTrasf_queryIsOk() {
        long idTrasf = aLong();
        helper.gettAllXfoParamTrasfbyTrasf(idTrasf);
        assertTrue(true);
    }

    @Test
    public void searchXfoParamTrasfbyName_queryIsOk() {
        String paramenterName = aString();
        long idTrasf = aLong();
        helper.searchXfoParamTrasfbyName(paramenterName, idTrasf);
        assertTrue(true);
    }

    @Test
    public void searchPigVValoreParamTrasfByName_queryIsOk() {
        String paramenterName = aString();
        long idVersTipoObjectDaTrasf = aLong();
        helper.searchPigVValoreParamTrasfByName(paramenterName, idVersTipoObjectDaTrasf);
        assertTrue(true);
    }

    @Test
    public void getPigVersTipoObjectDaTrasf_queryIsOk() {
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
    public void searchGeneratedPigObjects_queryIsOk() {
        PigObject object = aPigObject();
        helper.searchGeneratedPigObjects(object);
        assertTrue(true);
    }

    @Test
    public void findGeneratedPigObjectTrasf_queryIsOk() {
        String cdKeyObjectTrasf = aString();
        PigObject object = aPigObject();
        helper.findGeneratedPigObjectTrasf(cdKeyObjectTrasf, object);
        assertTrue(true);
    }

    @Test
    public void searchPigObjectTrasfInPigObjects_queryIsOk() {
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
    public void searchAssignedPigTipoObjects_queryIsOk() {
        long idTrasf = aLong();
        helper.searchAssignedPigTipoObjects(idTrasf);
        assertTrue(true);
    }

    @Test
    public void isTransformationAssigned_queryIsOk() {
        long idTrasf = aLong();
        helper.isTransformationAssigned(idTrasf);
        assertTrue(true);
    }

    @Test
    public void searchAuxiliaryFilesByXfoTras_queryIsOk() {
        long idTrasf = aLong();
        helper.searchAuxiliaryFilesByXfoTras(idTrasf);
        assertTrue(true);
    }

    @Test
    public void isVersionUnique_queryIsOk() {
        long idTrasf = aLong();
        String version = aString();
        helper.isVersionUnique(idTrasf, version);
        assertTrue(true);
    }

    @Test
    public void isVersionDateUnique_queryIsOk() {
        long idTrasf = aLong();
        Date date = todayTs();
        helper.isVersionDateUnique(idTrasf, date);
        assertTrue(true);
    }

    @Test
    public void isDateIstituzOverlapping_queryIsOk() {
        long idTrasf = aLong();
        Date date = todayTs();
        helper.isDateIstituzOverlapping(idTrasf, date);
        assertTrue(true);
    }

    @Test
    public void retriveKettleId_queryIsOk() {
        long idTrasf = aLong();
        helper.retriveKettleId(idTrasf);
        assertTrue(true);
    }

}
