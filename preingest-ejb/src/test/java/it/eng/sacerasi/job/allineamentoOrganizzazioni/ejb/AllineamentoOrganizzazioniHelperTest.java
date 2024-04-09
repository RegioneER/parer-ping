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
package it.eng.sacerasi.job.allineamentoOrganizzazioni.ejb;
/*import it.eng.sacerasi.common.Constants;
import it.eng.sacerasi.entity.IamOrganizDaReplic;
import it.eng.sacerasi.entity.PigAmbienteVers;
import it.eng.sacerasi.entity.PigTipoObject;
import it.eng.sacerasi.entity.PigVers;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import javax.ejb.embeddable.EJBContainer;
import org.junit.Test;*/

import it.eng.ArquillianUtils;
import it.eng.RollbackException;
import it.eng.sacerasi.common.Constants;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ejb.*;

import java.math.BigDecimal;
import java.util.List;

import static it.eng.ArquillianUtils.*;
import static it.eng.sacerasi.common.Constants.*;
import static org.junit.Assert.assertTrue;

/**
 * @author manuel.bertuzzi@eng.it
 */
@RunWith(Arquillian.class)
public class AllineamentoOrganizzazioniHelperTest {

    @EJB
    private AllineamentoOrganizzazioniHelper helper;

    @Deployment
    public static Archive<?> createTestArchive() {
        return ArquillianUtils.createPingJar(AllineamentoOrganizzazioniHelper.class);
    }

    public AllineamentoOrganizzazioniHelperTest() {
    }

    @Test
    public void getIamOrganizDaReplic_queryIsOk() {

        helper.getIamOrganizDaReplic();
        assertTrue(true);
    }

    @Test
    public void getPigAmbienteVers_queryIsOk() {
        BigDecimal idAmbienteVers = aBigDecimal();

        helper.getPigAmbienteVers(idAmbienteVers);
        assertTrue(true);
    }

    @Test
    public void getPigVers_queryIsOk() {
        BigDecimal idVers = aBigDecimal();

        helper.getPigVers(idVers);
        assertTrue(true);
    }

    @Test
    public void getPigTipoObjectList_queryIsOk() {
        List<Long> idVers = aListOfLong(2);

        helper.getPigTipoObjectList(idVers);
        assertTrue(true);
    }

    @Test(expected = RollbackException.class)
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    @Ignore("non si può fare rollback perché apre una transazione separata ")
    public void writeEsitoIamOrganizDaReplic_queryIsOk() {
        Long idOrganizDaReplic = 326L;

        String dsErr = aString();
        for (Constants.EsitoServizio esitoServizio : Constants.EsitoServizio.values()) {
            String cdErr = SERVIZI_ORG_001;
            helper.writeEsitoIamOrganizDaReplic(idOrganizDaReplic, esitoServizio, cdErr, dsErr);
            cdErr = SERVIZI_ORG_002;
            helper.writeEsitoIamOrganizDaReplic(idOrganizDaReplic, esitoServizio, cdErr, dsErr);
            cdErr = SERVIZI_ORG_003;
            helper.writeEsitoIamOrganizDaReplic(idOrganizDaReplic, esitoServizio, cdErr, dsErr);
            cdErr = SERVIZI_ORG_004;
            helper.writeEsitoIamOrganizDaReplic(idOrganizDaReplic, esitoServizio, cdErr, dsErr);
            cdErr = SERVIZI_ORG_005;
            helper.writeEsitoIamOrganizDaReplic(idOrganizDaReplic, esitoServizio, cdErr, dsErr);
            cdErr = SERVIZI_ORG_006;
            helper.writeEsitoIamOrganizDaReplic(idOrganizDaReplic, esitoServizio, cdErr, dsErr);
            cdErr = SERVIZI_ORG_007;
            helper.writeEsitoIamOrganizDaReplic(idOrganizDaReplic, esitoServizio, cdErr, dsErr);
            cdErr = REPLICA_ORG_001;
            helper.writeEsitoIamOrganizDaReplic(idOrganizDaReplic, esitoServizio, cdErr, dsErr);

        }
        throw new RollbackException();
    }

    @Test
    public void getEnteConvenzInfo_queryIsOk() {
        BigDecimal idVers = aBigDecimal();

        helper.getEnteConvenzInfo(idVers);
        assertTrue(true);
    }
}
