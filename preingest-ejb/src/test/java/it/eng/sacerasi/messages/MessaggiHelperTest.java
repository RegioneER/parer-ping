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
package it.eng.sacerasi.messages;

import it.eng.ArquillianUtils;

import java.util.List;
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
public class MessaggiHelperTest {
    @EJB
    private MessaggiHelper helper;

    @Deployment
    public static Archive<?> createTestArchive() {
        return ArquillianUtils.createPingJar(MessaggiHelper.class);
    }

    @Test
    public void retrieveListaErrori_queryIsOk() {
        String cdClasseErrore = aString();
        helper.retrieveListaErrori(cdClasseErrore);
        assertTrue(true);
    }

    @Test
    public void retrieveListaClassi_String_queryIsOk() {
        String tiStato = aString();
        helper.retrieveListaClassi(tiStato);
        assertTrue(true);
    }

    @Test
    public void retrieveListaClassi_List_queryIsOk() {
        List<String> tiStato = aListOfString(2);
        helper.retrieveListaClassi(tiStato);
        assertTrue(true);
    }

    @Test
    public void retrievePigErrore_queryIsOk() {
        String cdErrore = aString();
        helper.retrievePigErrore(cdErrore);
        assertTrue(true);
    }

    @Test
    public void retrievePigErroreNewTx_queryIsOk() {
        String cdErrore = aString();
        helper.retrievePigErroreNewTx(cdErrore);
        assertTrue(true);
    }

    @Test
    public void retrievePigErroreLike_queryIsOk() {
        String cdErrore = aString();
        helper.retrievePigErroreLike(cdErrore);
        assertTrue(true);
    }
}
