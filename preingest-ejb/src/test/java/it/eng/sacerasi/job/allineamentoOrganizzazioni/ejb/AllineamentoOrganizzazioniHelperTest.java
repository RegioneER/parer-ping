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
package it.eng.sacerasi.job.allineamentoOrganizzazioni.ejb;

import static it.eng.ArquillianUtils.aBigDecimal;
import static it.eng.ArquillianUtils.aListOfLong;
import static it.eng.ArquillianUtils.aString;
import static it.eng.sacerasi.common.Constants.REPLICA_ORG_001;
import static it.eng.sacerasi.common.Constants.SERVIZI_ORG_001;
import static it.eng.sacerasi.common.Constants.SERVIZI_ORG_002;
import static it.eng.sacerasi.common.Constants.SERVIZI_ORG_003;
import static it.eng.sacerasi.common.Constants.SERVIZI_ORG_004;
import static it.eng.sacerasi.common.Constants.SERVIZI_ORG_005;
import static it.eng.sacerasi.common.Constants.SERVIZI_ORG_006;
import static it.eng.sacerasi.common.Constants.SERVIZI_ORG_007;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
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
import it.eng.RollbackException;
import it.eng.sacerasi.common.Constants;

/**
 * @author manuel.bertuzzi@eng.it
 */
@ArquillianTest
public class AllineamentoOrganizzazioniHelperTest {

    @EJB
    private AllineamentoOrganizzazioniHelper helper;

    @Deployment
    public static Archive<?> createTestArchive() {
	return ArquillianUtils.createPingJar(AllineamentoOrganizzazioniHelper.class);
    }

    @Test
    void getIamOrganizDaReplic_queryIsOk() {

	helper.getIamOrganizDaReplic();
	assertTrue(true);
    }

    @Test
    void getPigAmbienteVers_queryIsOk() {
	BigDecimal idAmbienteVers = aBigDecimal();

	helper.getPigAmbienteVers(idAmbienteVers);
	assertTrue(true);
    }

    @Test
    void getPigVers_queryIsOk() {
	BigDecimal idVers = aBigDecimal();

	helper.getPigVers(idVers);
	assertTrue(true);
    }

    @Test
    void getPigTipoObjectList_queryIsOk() {
	List<Long> idVers = aListOfLong(2);

	helper.getPigTipoObjectList(idVers);
	assertTrue(true);
    }

    @Test
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    @Disabled("non si può fare rollback perché apre una transazione separata ")
    void writeEsitoIamOrganizDaReplic_queryIsOk() {
	Long idOrganizDaReplic = 326L;
	String dsErr = aString();

	assertThrows(RollbackException.class, () -> {
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
	});
    }

    @Test
    void getEnteConvenzInfo_queryIsOk() {
	BigDecimal idVers = aBigDecimal();

	helper.getEnteConvenzInfo(idVers);
	assertTrue(true);
    }
}
