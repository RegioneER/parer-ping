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
 * ViewBean per la vista Mon_V_Lis_Ses_Errate
 *
 */
import it.eng.spagoLite.db.base.table.AbstractBaseTable;
import it.eng.spagoLite.db.oracle.bean.column.TableDescriptor;

/**
 * TableBean per la tabella Mon_V_Lis_Ses_Errate
 *
 */
public class MonVLisSesErrateTableBean extends AbstractBaseTable<MonVLisSesErrateRowBean> {

    private static final long serialVersionUID = 1L;

    /*
     * @Generated( value = "it.eg.dbtool.db.oracle.beangen.Oracle4JPAClientBeanGen$ViewBeanWriter", comments =
     * "This class was generated by OraTool", date = "Monday, 17 March 2014 15:12" )
     */

    public static MonVLisSesErrateTableDescriptor TABLE_DESCRIPTOR = new MonVLisSesErrateTableDescriptor();

    public MonVLisSesErrateTableBean() {
        super();
    }

    protected MonVLisSesErrateRowBean createRow() {
        return new MonVLisSesErrateRowBean();
    }

    public TableDescriptor getTableDescriptor() {
        return TABLE_DESCRIPTOR;
    }

    @Deprecated
    public Iterator<MonVLisSesErrateRowBean> getRowsIterator() {
        return iterator();
    }
}
