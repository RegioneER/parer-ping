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
 *         Bean per la tabella Mon_V_Vis_Obj_Trasf
 *
 */
public class MonVVisObjTrasfTableDescriptor extends TableDescriptor {

    /*
     * @Generated( value = "it.eg.dbtool.db.oracle.beangen.Oracle4JPAClientBeanGen$ViewBeanWriter", comments =
     * "This class was generated by OraTool", date = "Thursday, 8 September 2016 09:50" )
     */

    public static final String SELECT = "Select * from Mon_V_Vis_Obj_Trasf /**/";
    public static final String TABLE_NAME = "Mon_V_Vis_Obj_Trasf";
    public static final String COL_ID_OBJECT_TRASF = "id_object_trasf";
    public static final String COL_ID_VERS_TRASF = "id_vers_trasf";
    public static final String COL_NM_AMBIENTE_VERS_TRASF = "nm_ambiente_vers_trasf";
    public static final String COL_NM_VERS_TRASF = "nm_vers_trasf";
    public static final String COL_CD_KEY_OBJECT_TRASF = "cd_key_object_trasf";
    public static final String COL_PG_OGGETTO_TRASF = "pg_oggetto_trasf";
    public static final String COL_NM_TIPO_OBJECT_TRASF = "nm_tipo_object_trasf";
    public static final String COL_DS_OBJECT_TRASF = "ds_object_trasf";
    public static final String COL_DS_PATH = "ds_path";
    public static final String COL_DS_PATH_TRASF = "ds_path_trasf";
    public static final String COL_DS_HASH_FILE_VERS = "ds_hash_file_vers";
    public static final String COL_TI_ALGO_HASH_FILE_VERS = "ti_algo_hash_file_vers";
    public static final String COL_CD_ENCODING_HASH_FILE_VERS = "cd_encoding_hash_file_vers";
    public static final String COL_TI_STATO_TRASF = "ti_stato_trasf";
    public static final String COL_CD_ERR = "cd_err";
    public static final String COL_DL_ERR = "dl_err";
    public static final String COL_NM_AMBIENTE_VERS_DA_TRASF = "nm_ambiente_vers_da_trasf";
    public static final String COL_NM_VERS_DA_TRASF = "nm_vers_da_trasf";
    public static final String COL_ID_VERS_DA_TRASF = "id_vers_da_trasf";
    public static final String COL_CD_KEY_OBJECT_DA_TRASF = "cd_key_object_da_trasf";
    public static final String COL_ID_OBJECT_DA_TRASF = "id_object_da_trasf";
    public static final String COL_NM_TIPO_OBJECT_DA_TRASF = "nm_tipo_object_da_trasf";
    public static final String COL_DS_OBJECT_DA_TRASF = "ds_object_da_trasf";
    public static final String COL_NI_TOT_OBJECT_TRASF = "ni_tot_object_trasf";
    public static final String COL_TI_STATO_OBJECT_DA_TRASF = "ti_stato_object_da_trasf";
    public static final String COL_DT_STATO_COR_DA_TRASF = "dt_stato_cor_da_trasf";

    private static Map<String, ColumnDescriptor> map = new LinkedHashMap<String, ColumnDescriptor>();

    static {
        map.put(COL_ID_OBJECT_TRASF, new ColumnDescriptor(COL_ID_OBJECT_TRASF, Types.DECIMAL, 22, true));
        map.put(COL_ID_VERS_TRASF, new ColumnDescriptor(COL_ID_VERS_TRASF, Types.DECIMAL, 22, true));
        map.put(COL_NM_AMBIENTE_VERS_TRASF, new ColumnDescriptor(COL_NM_AMBIENTE_VERS_TRASF, Types.VARCHAR, 100, true));
        map.put(COL_NM_VERS_TRASF, new ColumnDescriptor(COL_NM_VERS_TRASF, Types.VARCHAR, 100, true));
        map.put(COL_CD_KEY_OBJECT_TRASF, new ColumnDescriptor(COL_CD_KEY_OBJECT_TRASF, Types.VARCHAR, 100, true));
        map.put(COL_PG_OGGETTO_TRASF, new ColumnDescriptor(COL_PG_OGGETTO_TRASF, Types.DECIMAL, 22, true));
        map.put(COL_NM_TIPO_OBJECT_TRASF, new ColumnDescriptor(COL_NM_TIPO_OBJECT_TRASF, Types.VARCHAR, 100, true));
        map.put(COL_DS_OBJECT_TRASF, new ColumnDescriptor(COL_DS_OBJECT_TRASF, Types.VARCHAR, 1024, true));
        map.put(COL_DS_PATH, new ColumnDescriptor(COL_DS_PATH, Types.VARCHAR, 254, true));
        map.put(COL_DS_PATH_TRASF, new ColumnDescriptor(COL_DS_PATH_TRASF, Types.VARCHAR, 254, true));
        map.put(COL_DS_HASH_FILE_VERS, new ColumnDescriptor(COL_DS_HASH_FILE_VERS, Types.VARCHAR, 254, true));
        map.put(COL_TI_ALGO_HASH_FILE_VERS, new ColumnDescriptor(COL_TI_ALGO_HASH_FILE_VERS, Types.VARCHAR, 20, true));
        map.put(COL_CD_ENCODING_HASH_FILE_VERS,
                new ColumnDescriptor(COL_CD_ENCODING_HASH_FILE_VERS, Types.VARCHAR, 100, true));
        map.put(COL_TI_STATO_TRASF, new ColumnDescriptor(COL_TI_STATO_TRASF, Types.VARCHAR, 20, true));
        map.put(COL_CD_ERR, new ColumnDescriptor(COL_CD_ERR, Types.VARCHAR, 100, true));
        map.put(COL_DL_ERR, new ColumnDescriptor(COL_DL_ERR, Types.VARCHAR, 1024, true));
        map.put(COL_NM_AMBIENTE_VERS_DA_TRASF,
                new ColumnDescriptor(COL_NM_AMBIENTE_VERS_DA_TRASF, Types.VARCHAR, 100, true));
        map.put(COL_NM_VERS_DA_TRASF, new ColumnDescriptor(COL_NM_VERS_DA_TRASF, Types.VARCHAR, 100, true));
        map.put(COL_ID_VERS_DA_TRASF, new ColumnDescriptor(COL_ID_VERS_DA_TRASF, Types.DECIMAL, 22, true));
        map.put(COL_CD_KEY_OBJECT_DA_TRASF, new ColumnDescriptor(COL_CD_KEY_OBJECT_DA_TRASF, Types.VARCHAR, 100, true));
        map.put(COL_ID_OBJECT_DA_TRASF, new ColumnDescriptor(COL_ID_OBJECT_DA_TRASF, Types.DECIMAL, 22, true));
        map.put(COL_NM_TIPO_OBJECT_DA_TRASF,
                new ColumnDescriptor(COL_NM_TIPO_OBJECT_DA_TRASF, Types.VARCHAR, 100, true));
        map.put(COL_DS_OBJECT_DA_TRASF, new ColumnDescriptor(COL_DS_OBJECT_DA_TRASF, Types.VARCHAR, 1024, true));
        map.put(COL_NI_TOT_OBJECT_TRASF, new ColumnDescriptor(COL_NI_TOT_OBJECT_TRASF, Types.DECIMAL, 22, true));
        map.put(COL_TI_STATO_OBJECT_DA_TRASF,
                new ColumnDescriptor(COL_TI_STATO_OBJECT_DA_TRASF, Types.VARCHAR, 20, true));
        map.put(COL_DT_STATO_COR_DA_TRASF, new ColumnDescriptor(COL_DT_STATO_COR_DA_TRASF, Types.TIMESTAMP, 11, true));
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
