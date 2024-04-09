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

import java.sql.Types;
import java.util.LinkedHashMap;
import java.util.Map;

import it.eng.spagoLite.db.oracle.bean.column.ColumnDescriptor;
import it.eng.spagoLite.db.oracle.bean.column.TableDescriptor;

/**
 * @author Sloth
 *
 *         Bean per la tabella Pig_Sessione_Recup
 *
 */
public class PigSessioneRecupTableDescriptor extends TableDescriptor {

    /*
     * @Generated( value = "it.eg.dbtool.db.oracle.beangen.Oracle4JPAClientBeanGen$TableBeanWriter", comments =
     * "This class was generated by OraTool", date = "Monday, 17 March 2014 15:12" )
     */

    public static final String SELECT = "Select * from Pig_Sessione_Recup /**/";
    public static final String TABLE_NAME = "Pig_Sessione_Recup";
    public static final String COL_ID_SESSIONE_RECUP = "id_sessione_recup";
    public static final String COL_NM_AMBIENTE_VERS = "nm_ambiente_vers";
    public static final String COL_NM_VERS = "nm_vers";
    public static final String COL_CD_KEY_OBJECT = "cd_key_object";
    public static final String COL_ID_VERS = "id_vers";
    public static final String COL_ID_OBJECT = "id_object";
    public static final String COL_DT_APERTURA = "dt_apertura";
    public static final String COL_DT_CHIUSURA = "dt_chiusura";
    public static final String COL_TI_STATO = "ti_stato";
    public static final String COL_CD_ERR = "cd_err";
    public static final String COL_DL_ERR = "dl_err";

    private static Map<String, ColumnDescriptor> map = new LinkedHashMap<String, ColumnDescriptor>();

    static {
        map.put(COL_ID_SESSIONE_RECUP, new ColumnDescriptor(COL_ID_SESSIONE_RECUP, Types.DECIMAL, 22, true));
        map.put(COL_NM_AMBIENTE_VERS, new ColumnDescriptor(COL_NM_AMBIENTE_VERS, Types.VARCHAR, 100, false));
        map.put(COL_NM_VERS, new ColumnDescriptor(COL_NM_VERS, Types.VARCHAR, 100, false));
        map.put(COL_CD_KEY_OBJECT, new ColumnDescriptor(COL_CD_KEY_OBJECT, Types.VARCHAR, 100, false));
        map.put(COL_ID_VERS, new ColumnDescriptor(COL_ID_VERS, Types.DECIMAL, 22, false));
        map.put(COL_ID_OBJECT, new ColumnDescriptor(COL_ID_OBJECT, Types.DECIMAL, 22, false));
        map.put(COL_DT_APERTURA, new ColumnDescriptor(COL_DT_APERTURA, Types.TIMESTAMP, 7, false));
        map.put(COL_DT_CHIUSURA, new ColumnDescriptor(COL_DT_CHIUSURA, Types.TIMESTAMP, 7, false));
        map.put(COL_TI_STATO, new ColumnDescriptor(COL_TI_STATO, Types.VARCHAR, 30, false));
        map.put(COL_CD_ERR, new ColumnDescriptor(COL_CD_ERR, Types.VARCHAR, 100, false));
        map.put(COL_DL_ERR, new ColumnDescriptor(COL_DL_ERR, Types.VARCHAR, 1024, false));
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
