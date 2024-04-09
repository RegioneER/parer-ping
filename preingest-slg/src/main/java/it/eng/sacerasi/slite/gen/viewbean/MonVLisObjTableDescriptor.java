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
 *         Bean per la tabella Mon_V_Lis_Obj
 *
 */
public class MonVLisObjTableDescriptor extends TableDescriptor {

    /*
     * @Generated( value = "it.eg.dbtool.db.oracle.beangen.Oracle4JPAClientBeanGen$ViewBeanWriter", comments =
     * "This class was generated by OraTool", date = "Friday, 13 January 2017 15:27" )
     */

    public static final String SELECT = "Select * from Mon_V_Lis_Obj /**/";
    public static final String TABLE_NAME = "Mon_V_Lis_Obj";
    public static final String COL_ID_AMBIENTE_VERS = "id_ambiente_vers";
    public static final String COL_NM_AMBIENTE_VERS = "nm_ambiente_vers";
    public static final String COL_ID_VERS = "id_vers";
    public static final String COL_NM_VERS = "nm_vers";
    public static final String COL_ID_OBJECT = "id_object";
    public static final String COL_NM_TIPO_OBJECT = "nm_tipo_object";
    public static final String COL_ID_TIPO_OBJECT = "id_tipo_object";
    public static final String COL_TI_STATO_OBJECT = "ti_stato_object";
    public static final String COL_TI_STATO_OBJECT_VIS = "ti_stato_object_vis";
    public static final String COL_NI_UNITA_DOC_VERS_OK = "ni_unita_doc_vers_ok";
    public static final String COL_CD_KEY_OBJECT = "cd_key_object";
    public static final String COL_DT_STATO_COR = "dt_stato_cor";
    public static final String COL_DT_VERS = "dt_vers";
    public static final String COL_TI_DT_CREAZIONE = "ti_dt_creazione";
    public static final String COL_CD_REGISTRO_UNITA_DOC_SACER = "cd_registro_unita_doc_sacer";
    public static final String COL_AA_UNITA_DOC_SACER = "aa_unita_doc_sacer";
    public static final String COL_CD_KEY_UNITA_DOC_SACER = "cd_key_unita_doc_sacer";
    public static final String COL_DS_KEY_ORD = "ds_key_ord";
    public static final String COL_DS_INFO_OBJECT = "ds_info_object";
    public static final String COL_TI_STATO_VERIFICA_HASH = "ti_stato_verifica_hash";
    public static final String COL_TI_VERS_FILE = "ti_vers_file";

    private static Map<String, ColumnDescriptor> map = new LinkedHashMap<String, ColumnDescriptor>();

    static {
        map.put(COL_ID_AMBIENTE_VERS, new ColumnDescriptor(COL_ID_AMBIENTE_VERS, Types.DECIMAL, 22, true));
        map.put(COL_NM_AMBIENTE_VERS, new ColumnDescriptor(COL_NM_AMBIENTE_VERS, Types.VARCHAR, 100, true));
        map.put(COL_ID_VERS, new ColumnDescriptor(COL_ID_VERS, Types.DECIMAL, 22, true));
        map.put(COL_NM_VERS, new ColumnDescriptor(COL_NM_VERS, Types.VARCHAR, 100, true));
        map.put(COL_ID_OBJECT, new ColumnDescriptor(COL_ID_OBJECT, Types.DECIMAL, 22, true));
        map.put(COL_NM_TIPO_OBJECT, new ColumnDescriptor(COL_NM_TIPO_OBJECT, Types.VARCHAR, 100, true));
        map.put(COL_ID_TIPO_OBJECT, new ColumnDescriptor(COL_ID_TIPO_OBJECT, Types.DECIMAL, 22, true));
        map.put(COL_TI_STATO_OBJECT, new ColumnDescriptor(COL_TI_STATO_OBJECT, Types.VARCHAR, 30, true));
        map.put(COL_TI_STATO_OBJECT_VIS, new ColumnDescriptor(COL_TI_STATO_OBJECT_VIS, Types.VARCHAR, 36, true));
        map.put(COL_NI_UNITA_DOC_VERS_OK, new ColumnDescriptor(COL_NI_UNITA_DOC_VERS_OK, Types.DECIMAL, 22, true));
        map.put(COL_CD_KEY_OBJECT, new ColumnDescriptor(COL_CD_KEY_OBJECT, Types.VARCHAR, 100, true));
        map.put(COL_DT_STATO_COR, new ColumnDescriptor(COL_DT_STATO_COR, Types.TIMESTAMP, 11, true));
        map.put(COL_DT_VERS, new ColumnDescriptor(COL_DT_VERS, Types.TIMESTAMP, 11, true));
        map.put(COL_TI_DT_CREAZIONE, new ColumnDescriptor(COL_TI_DT_CREAZIONE, Types.VARCHAR, 19, true));
        map.put(COL_CD_REGISTRO_UNITA_DOC_SACER,
                new ColumnDescriptor(COL_CD_REGISTRO_UNITA_DOC_SACER, Types.VARCHAR, 100, true));
        map.put(COL_AA_UNITA_DOC_SACER, new ColumnDescriptor(COL_AA_UNITA_DOC_SACER, Types.DECIMAL, 22, true));
        map.put(COL_CD_KEY_UNITA_DOC_SACER, new ColumnDescriptor(COL_CD_KEY_UNITA_DOC_SACER, Types.VARCHAR, 100, true));
        map.put(COL_DS_KEY_ORD, new ColumnDescriptor(COL_DS_KEY_ORD, Types.VARCHAR, 200, true));
        map.put(COL_DS_INFO_OBJECT, new ColumnDescriptor(COL_DS_INFO_OBJECT, Types.VARCHAR, 1024, true));
        map.put(COL_TI_STATO_VERIFICA_HASH, new ColumnDescriptor(COL_TI_STATO_VERIFICA_HASH, Types.VARCHAR, 20, true));
        map.put(COL_TI_VERS_FILE, new ColumnDescriptor(COL_TI_VERS_FILE, Types.VARCHAR, 20, true));
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
