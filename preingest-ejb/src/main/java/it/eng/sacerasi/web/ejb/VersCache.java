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
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.eng.sacerasi.web.ejb;

import it.eng.sacerasi.entity.PigVers;
import java.util.HashMap;
import javax.annotation.PostConstruct;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import java.util.UUID;

/**
 *
 * @author Filippini_M
 */

@Singleton
@LocalBean
@Startup
public class VersCache {

    HashMap<UUID, PigVers> versMap;

    @PostConstruct
    protected void initSingleton() {
        versMap = new HashMap<UUID, PigVers>();
    }

    public PigVers getPigVers(UUID key) {
        /*
         * OrgStrut toRemove = struts.get(key); struts.remove(key); return toRemove;
         */
        return versMap.get(key);
    }

    public PigVers setPigVers(UUID key, PigVers vers) {
        return versMap.put(key, vers);
    }

    public void removePigVers(UUID key) {
        versMap.remove(key);
    }

}
