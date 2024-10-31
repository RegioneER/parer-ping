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

import java.sql.Types;
import java.util.LinkedHashMap;
import java.util.Map;

import it.eng.spagoLite.db.oracle.bean.column.ColumnDescriptor;
import it.eng.spagoLite.db.oracle.bean.column.TableDescriptor;

/**
 * @author Sloth
 *
 *         Bean per la tabella Mon_V_Lis_Stato_Vers
 *
 */
public class MonVLisStatoVersTableDescriptor extends TableDescriptor {

    /*
     * @Generated( value = "it.eg.dbtool.db.oracle.beangen.Oracle4JPAClientBeanGen$ViewBeanWriter", comments =
     * "This class was generated by OraTool", date = "Thursday, 26 July 2018 13:00" )
     */

    public static final String SELECT = "Select * from Mon_V_Lis_Stato_Vers /**/";
    public static final String TABLE_NAME = "Mon_V_Lis_Stato_Vers";
    public static final String COL_ID_USER_IAM = "id_user_iam";
    public static final String COL_ID_AMBIENTE_VERS = "id_ambiente_vers";
    public static final String COL_NM_AMBIENTE_VERS = "nm_ambiente_vers";
    public static final String COL_ID_TIPO_OBJECT = "id_tipo_object";
    public static final String COL_NM_TIPO_OBJECT = "nm_tipo_object";
    public static final String COL_TI_VERS_FILE = "ti_vers_file";
    public static final String COL_ID_VERS = "id_vers";
    public static final String COL_NM_VERS = "nm_vers";
    public static final String COL_ID_OBJECT = "id_object";
    public static final String COL_CD_KEY_OBJECT = "cd_key_object";
    public static final String COL_DS_OBJECT = "ds_object";
    public static final String COL_TI_STATO_ESTERNO = "ti_stato_esterno";
    public static final String COL_ID_FILE_OBJECT = "id_file_object";
    public static final String COL_NI_BYTE_FILE_VERS = "ni_byte_file_vers";
    public static final String COL_DS_HASH_FILE_VERS = "ds_hash_file_vers";
    public static final String COL_ID_SESSIONE_INGEST = "id_sessione_ingest";
    public static final String COL_DT_VERS = "dt_vers";
    public static final String COL_ID_USER_IAM_VERS = "id_user_iam_vers";
    public static final String COL_NM_USERID_VERS = "nm_userid_vers";
    public static final String COL_TI_STATO_OBJECT = "ti_stato_object";
    public static final String COL_TI_STATO_CALCOLATO = "ti_stato_calcolato";
    public static final String COL_TI_STATO_VISUALIZZATO = "ti_stato_visualizzato";

    private static Map<String, ColumnDescriptor> map = new LinkedHashMap<String, ColumnDescriptor>();

    static {
        map.put(COL_ID_USER_IAM, new ColumnDescriptor(COL_ID_USER_IAM, Types.DECIMAL, 22, true));
        map.put(COL_ID_AMBIENTE_VERS, new ColumnDescriptor(COL_ID_AMBIENTE_VERS, Types.DECIMAL, 22, true));
        map.put(COL_NM_AMBIENTE_VERS, new ColumnDescriptor(COL_NM_AMBIENTE_VERS, Types.VARCHAR, 100, true));
        map.put(COL_ID_TIPO_OBJECT, new ColumnDescriptor(COL_ID_TIPO_OBJECT, Types.DECIMAL, 22, true));
        map.put(COL_NM_TIPO_OBJECT, new ColumnDescriptor(COL_NM_TIPO_OBJECT, Types.VARCHAR, 100, true));
        map.put(COL_TI_VERS_FILE, new ColumnDescriptor(COL_TI_VERS_FILE, Types.VARCHAR, 20, true));
        map.put(COL_ID_VERS, new ColumnDescriptor(COL_ID_VERS, Types.DECIMAL, 22, true));
        map.put(COL_NM_VERS, new ColumnDescriptor(COL_NM_VERS, Types.VARCHAR, 100, true));
        map.put(COL_ID_OBJECT, new ColumnDescriptor(COL_ID_OBJECT, Types.DECIMAL, 22, true));
        map.put(COL_CD_KEY_OBJECT, new ColumnDescriptor(COL_CD_KEY_OBJECT, Types.VARCHAR, 100, true));
        map.put(COL_DS_OBJECT, new ColumnDescriptor(COL_DS_OBJECT, Types.VARCHAR, 1024, true));
        map.put(COL_TI_STATO_ESTERNO, new ColumnDescriptor(COL_TI_STATO_ESTERNO, Types.VARCHAR, 30, true));
        map.put(COL_ID_FILE_OBJECT, new ColumnDescriptor(COL_ID_FILE_OBJECT, Types.DECIMAL, 22, true));
        map.put(COL_NI_BYTE_FILE_VERS, new ColumnDescriptor(COL_NI_BYTE_FILE_VERS, Types.DECIMAL, 22, true));
        map.put(COL_DS_HASH_FILE_VERS, new ColumnDescriptor(COL_DS_HASH_FILE_VERS, Types.VARCHAR, 254, true));
        map.put(COL_ID_SESSIONE_INGEST, new ColumnDescriptor(COL_ID_SESSIONE_INGEST, Types.DECIMAL, 22, true));
        map.put(COL_DT_VERS, new ColumnDescriptor(COL_DT_VERS, Types.TIMESTAMP, 7, true));
        map.put(COL_ID_USER_IAM_VERS, new ColumnDescriptor(COL_ID_USER_IAM_VERS, Types.DECIMAL, 22, true));
        map.put(COL_NM_USERID_VERS, new ColumnDescriptor(COL_NM_USERID_VERS, Types.VARCHAR, 100, true));
        map.put(COL_TI_STATO_OBJECT, new ColumnDescriptor(COL_TI_STATO_OBJECT, Types.VARCHAR, 30, true));
        map.put(COL_TI_STATO_CALCOLATO, new ColumnDescriptor(COL_TI_STATO_CALCOLATO, Types.VARCHAR, 30, true));
        map.put(COL_TI_STATO_VISUALIZZATO, new ColumnDescriptor(COL_TI_STATO_VISUALIZZATO, Types.VARCHAR, 30, true));
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
