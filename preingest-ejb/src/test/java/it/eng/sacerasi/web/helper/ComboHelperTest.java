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
import it.eng.ExpectedException;

import java.math.BigDecimal;
import javax.ejb.EJB;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Test;
import org.junit.runner.RunWith;

import static it.eng.ArquillianUtils.*;
import static org.junit.Assert.assertTrue;

/**
 * @author manuel.bertuzzi@eng.it
 */
@RunWith(Arquillian.class)
public class ComboHelperTest {
    @EJB
    private ComboHelper helper;

    @Deployment
    public static Archive<?> createTestArchive() {
        return ArquillianUtils.createPingJar(ComboHelper.class);
    }

    @Test
    public void getAmbienteVersatoreFromUtente_queryIsOk() {
        long idUtente = aLong();
        helper.getAmbienteVersatoreFromUtente(idUtente);
        assertTrue(true);
    }

    @Test
    public void getVersatoreFromAmbienteVersatore_queryIsOk() {
        Long idUtente = aLong();
        BigDecimal idAmbienteVers = aBigDecimal();
        helper.getVersatoreFromAmbienteVersatore(idUtente, idAmbienteVers);
        assertTrue(true);
    }

    @Test
    public void getTipoObjectFromVersatore_long_BigDecimal_queryIsOk() {
        long idUtente = aLong();
        BigDecimal idVers = aBigDecimal();
        helper.getTipoObjectFromVersatore(idUtente, idVers);
        assertTrue(true);
    }

    @Test
    public void getTipoObjectFromVersatore_3args_queryIsOk() {
        long idUtente = aLong();
        BigDecimal idVers = aBigDecimal();
        String[] tipoVers = aStringArray(2);
        helper.getTipoObjectFromVersatore(idUtente, idVers, tipoVers);
        assertTrue(true);
    }

    @Test
    public void getTipoObjectFromVersatoreNoFleggati_queryIsOk() {
        long idUtente = aLong();
        BigDecimal idVers = aBigDecimal();
        String[] tipoVers = aStringArray(2);
        helper.getTipoObjectFromVersatoreNoFleggati(idUtente, idVers, tipoVers);
        assertTrue(true);
    }

    @Test
    public void getVersatori_queryIsOk() {
        long idUtente = aLong();
        helper.getVersatori(idUtente);
        assertTrue(true);
    }

    @Test(expected = ExpectedException.class)
    public void getIdAmbienteVersatore_queryIsOk() {
        BigDecimal idVers = aBigDecimal();
        try {
            helper.getIdAmbienteVersatore(idVers);
        } catch (Exception e) {
            throwExpectedExceptionIfNoResultException(e);
        }
    }
}
