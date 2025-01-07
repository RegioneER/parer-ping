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

package it.eng.sacerasi.slite.gen.tablebean;

import it.eng.spagoLite.db.oracle.bean.column.ColumnDescriptor;
import it.eng.spagoLite.db.oracle.bean.column.TableDescriptor;

import java.sql.Types;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.annotation.Generated;

/**
 * @author Sloth
 *
 *         Bean per la tabella Pig_Strum_Urb_Storico_Stati
 *
 */
public class PigStrumUrbStoricoStatiTableDescriptor extends TableDescriptor {

    /*
     * @Generated( value = "it.eg.dbtool.db.oracle.beangen.Oracle4JPAClientBeanGen$TableBeanWriter", comments =
     * "This class was generated by OraTool", date = "Wednesday, 15 May 2024 12:32" )
     */

    public static final String SELECT = "Select * from Pig_Strum_Urb_Storico_Stati /**/";
    public static final String TABLE_NAME = "Pig_Strum_Urb_Storico_Stati";
    public static final String COL_ID_STATO = "id_stato";
    public static final String COL_ID_STRUMENTI_URBANISTICI = "id_strumenti_urbanistici";
    public static final String COL_TI_STATO = "ti_stato";
    public static final String COL_TS_REG_STATO = "ts_reg_stato";
    public static final String COL_CD_DESC = "cd_desc";

    private static Map<String, ColumnDescriptor> map = new LinkedHashMap<String, ColumnDescriptor>();

    static {
        map.put(COL_ID_STATO, new ColumnDescriptor(COL_ID_STATO, Types.DECIMAL, 22, true));
        map.put(COL_ID_STRUMENTI_URBANISTICI,
                new ColumnDescriptor(COL_ID_STRUMENTI_URBANISTICI, Types.DECIMAL, 22, false));
        map.put(COL_TI_STATO, new ColumnDescriptor(COL_TI_STATO, Types.VARCHAR, 30, false));
        map.put(COL_TS_REG_STATO, new ColumnDescriptor(COL_TS_REG_STATO, Types.TIMESTAMP, 11, false));
        map.put(COL_CD_DESC, new ColumnDescriptor(COL_CD_DESC, Types.VARCHAR, 1024, false));
    }

    public Map<String, ColumnDescriptor> getColumnMap() {
        return map;
    }

    public String getTableName() {
        return TABLE_NAME;
    }

    public String getStatement() {
        return SELECT;
    }

}
