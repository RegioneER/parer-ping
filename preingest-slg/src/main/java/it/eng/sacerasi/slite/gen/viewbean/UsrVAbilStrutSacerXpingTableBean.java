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

package it.eng.sacerasi.slite.gen.viewbean;

import java.util.Iterator;

/**
 * ViewBean per la vista Usr_V_Abil_Strut_Sacer_Xping
 *
 */
import it.eng.spagoLite.db.base.table.AbstractBaseTable;
import it.eng.spagoLite.db.oracle.bean.column.TableDescriptor;

/**
 * TableBean per la tabella Usr_V_Abil_Strut_Sacer_Xping
 *
 */
public class UsrVAbilStrutSacerXpingTableBean extends AbstractBaseTable<UsrVAbilStrutSacerXpingRowBean> {

    private static final long serialVersionUID = 1L;

    /*
     * @Generated( value = "it.eg.dbtool.db.oracle.beangen.Oracle4JPAClientBeanGen$ViewBeanWriter", comments =
     * "This class was generated by OraTool", date = "Monday, 8 May 2017 17:21" )
     */

    public static UsrVAbilStrutSacerXpingTableDescriptor TABLE_DESCRIPTOR = new UsrVAbilStrutSacerXpingTableDescriptor();

    public UsrVAbilStrutSacerXpingTableBean() {
        super();
    }

    protected UsrVAbilStrutSacerXpingRowBean createRow() {
        return new UsrVAbilStrutSacerXpingRowBean();
    }

    public TableDescriptor getTableDescriptor() {
        return TABLE_DESCRIPTOR;
    }

    @Deprecated
    public Iterator<UsrVAbilStrutSacerXpingRowBean> getRowsIterator() {
        return iterator();
    }
}
