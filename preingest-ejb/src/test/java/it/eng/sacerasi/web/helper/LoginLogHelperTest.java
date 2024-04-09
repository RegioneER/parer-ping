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
import it.eng.spagoLite.security.BaseUser;
import it.eng.spagoLite.security.IUser;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ejb.EJB;

import static org.junit.Assert.assertTrue;

/**
 *
 * @author manuel.bertuzzi@eng.it
 */
@RunWith(Arquillian.class)
public class LoginLogHelperTest {
    @EJB
    private LoginLogHelper helper;

    @Deployment
    public static Archive<?> createTestArchive() {
        return ArquillianUtils.createPingJar(LoginLogHelper.class)
                .addClass("it.eng.parer.sacerlog.ejb.helper.SacerLogHelper")
                .addPackages(true, "it.eng.parer.sacerlog.ejb.common", "it.eng.parer.sacerlog.common");
    }

    @Test
    public void writeLogEvento_queryIsOk() {
        IUser user = new BaseUser("test", "arquillian");
        user.setUsername("arquillian_test");
        user.setUserType(IUser.UserType.SPID_FEDERA);
        String indIpClient = "127.0.0.1";
        helper.writeLogEvento(user, indIpClient, LoginLogHelper.TipiEvento.LOGIN);
        helper.writeLogEvento(user, indIpClient, LoginLogHelper.TipiEvento.LOGOUT);
        assertTrue(true);
    }
}
