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
package it.eng.sacerasi.ws.recuperoStatoOggetto.ejb;

import it.eng.ArquillianUtils;
import it.eng.ExpectedException;
import it.eng.sacerasi.ws.recuperoStatoOggetto.dto.RecuperoStatoOggettoExt;
import it.eng.sacerasi.ws.recuperoStatoOggetto.dto.RispostaWSRecuperoStatoOggetto;
import it.eng.sacerasi.ws.recuperoStatoOggetto.helper.RecuperoStatoOggettoHelper;

import javax.ejb.EJB;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * @author manuel.bertuzzi@eng.it
 */
@RunWith(Arquillian.class)

public class RecuperoStatoOggettoHelperTest {

    @EJB
    private RecuperoStatoOggettoHelper helper;

    @Deployment
    public static Archive<?> createTestArchive() {
        return ArquillianUtils.createPingJar(RecuperoStatoOggettoHelper.class)
                .addClass("org.apache.commons.lang3.exception.ExceptionUtils")
                .addPackages(true, "it.eng.sacerasi.ws.dto")
                .addPackages(true, "it.eng.sacerasi.ws.recuperoStatoOggetto.dto");
    }

    @Test(expected = ExpectedException.class)
    public void getStatoOggetto_queryIsOk() {
        RecuperoStatoOggettoExt rsoExt = new RecuperoStatoOggettoExt();
        rsoExt.setIdObject(-99L);
        RispostaWSRecuperoStatoOggetto rispostaWs = new RispostaWSRecuperoStatoOggetto();
        try {
            helper.getStatoOggetto(rsoExt, rispostaWs);
        } catch (Exception e) {
            ArquillianUtils.throwExceptionIf(ExpectedException.class, e,
                    "Impossibile trovare PigObject con idObject " + rsoExt.getIdObject());
        }
    }

}
