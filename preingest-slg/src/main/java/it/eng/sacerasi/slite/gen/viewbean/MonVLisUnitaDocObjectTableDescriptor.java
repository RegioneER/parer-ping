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
 *         Bean per la tabella Mon_V_Lis_Unita_Doc_Object
 *
 */
public class MonVLisUnitaDocObjectTableDescriptor extends TableDescriptor {

    /*
     * @Generated( value = "it.eg.dbtool.db.oracle.beangen.Oracle4JPAClientBeanGen$ViewBeanWriter", comments =
     * "This class was generated by OraTool", date = "Thursday, 27 April 2017 10:21" )
     */

    public static final String SELECT = "Select * from Mon_V_Lis_Unita_Doc_Object /**/";
    public static final String TABLE_NAME = "Mon_V_Lis_Unita_Doc_Object";
    public static final String COL_ID_UNITA_DOC_OBJECT = "id_unita_doc_object";
    public static final String COL_ID_OBJECT = "id_object";
    public static final String COL_CD_REGISTRO_UNITA_DOC_SACER = "cd_registro_unita_doc_sacer";
    public static final String COL_AA_UNITA_DOC_SACER = "aa_unita_doc_sacer";
    public static final String COL_CD_KEY_UNITA_DOC_SACER = "cd_key_unita_doc_sacer";
    public static final String COL_NI_SIZE_FILE_BYTE = "ni_size_file_byte";
    public static final String COL_TI_STATO_UNITA_DOC_OBJECT = "ti_stato_unita_doc_object";
    public static final String COL_CD_ERR_SACER = "cd_err_sacer";
    public static final String COL_DL_ERR_SACER = "dl_err_sacer";
    public static final String COL_ID_ORGANIZ_IAM = "id_organiz_iam";
    public static final String COL_FL_VERS_SIMULATO = "fl_vers_simulato";
    public static final String COL_DL_COMPOSITO_ORGANIZ = "dl_composito_organiz";
    public static final String COL_CD_CONCAT_DL_ERR_SACER = "cd_concat_dl_err_sacer";
    public static final String COL_DT_STATO = "dt_stato";

    private static Map<String, ColumnDescriptor> map = new LinkedHashMap<String, ColumnDescriptor>();

    static {
        map.put(COL_ID_UNITA_DOC_OBJECT, new ColumnDescriptor(COL_ID_UNITA_DOC_OBJECT, Types.DECIMAL, 22, true));
        map.put(COL_ID_OBJECT, new ColumnDescriptor(COL_ID_OBJECT, Types.DECIMAL, 22, true));
        map.put(COL_CD_REGISTRO_UNITA_DOC_SACER,
                new ColumnDescriptor(COL_CD_REGISTRO_UNITA_DOC_SACER, Types.VARCHAR, 100, true));
        map.put(COL_AA_UNITA_DOC_SACER, new ColumnDescriptor(COL_AA_UNITA_DOC_SACER, Types.DECIMAL, 22, true));
        map.put(COL_CD_KEY_UNITA_DOC_SACER, new ColumnDescriptor(COL_CD_KEY_UNITA_DOC_SACER, Types.VARCHAR, 100, true));
        map.put(COL_NI_SIZE_FILE_BYTE, new ColumnDescriptor(COL_NI_SIZE_FILE_BYTE, Types.DECIMAL, 22, true));
        map.put(COL_TI_STATO_UNITA_DOC_OBJECT,
                new ColumnDescriptor(COL_TI_STATO_UNITA_DOC_OBJECT, Types.VARCHAR, 25, true));
        map.put(COL_CD_ERR_SACER, new ColumnDescriptor(COL_CD_ERR_SACER, Types.VARCHAR, 100, true));
        map.put(COL_DL_ERR_SACER, new ColumnDescriptor(COL_DL_ERR_SACER, Types.VARCHAR, 1024, true));
        map.put(COL_ID_ORGANIZ_IAM, new ColumnDescriptor(COL_ID_ORGANIZ_IAM, Types.DECIMAL, 22, true));
        map.put(COL_FL_VERS_SIMULATO, new ColumnDescriptor(COL_FL_VERS_SIMULATO, Types.VARCHAR, 1, true));
        map.put(COL_DL_COMPOSITO_ORGANIZ, new ColumnDescriptor(COL_DL_COMPOSITO_ORGANIZ, Types.VARCHAR, 4000, true));
        map.put(COL_CD_CONCAT_DL_ERR_SACER,
                new ColumnDescriptor(COL_CD_CONCAT_DL_ERR_SACER, Types.VARCHAR, 1125, true));
        map.put(COL_DT_STATO, new ColumnDescriptor(COL_DT_STATO, Types.TIMESTAMP, 7, true));
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
