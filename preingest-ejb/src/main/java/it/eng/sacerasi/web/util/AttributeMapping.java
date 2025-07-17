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
 * To change this template, choose Tools | Templates and open the template in the editor.
 */
package it.eng.sacerasi.web.util;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Quaranta_M
 */
public class AttributeMapping {

    private static final Set<String> list = new HashSet<String>();
    private static final Set<String> idList = new HashSet<String>();

    private AttributeMapping() {
    }

    ;

    // list of attribute not to be copied
    static {
	list.add("pigObjects");
	list.add("pigSessioneIngests");
	list.add("pigSessioneRecups");
	list.add("pigContUnitaDocSacers");
	list.add("pigObjects");
	list.add("pigFileObjects");
	list.add("pigInfoDicoms");
	list.add("cdSopClassDicom");
	list.add("dsSopClassDicom");
	list.add("pigAmbienteVer");
	// list.add("pigUsrAppartUserVers");

    }
    // id list to be copied
    static {
	idList.add("idSopClassDicom");
    }

    public static boolean contains(String s) {
	return list.contains(s);
    }

    public static boolean idListContains(String s) {
	return idList.contains(s);
    }
}
