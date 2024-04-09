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
 *         Bean per la tabella Pig_File_Object
 *
 */
public class PigFileObjectTableDescriptor extends TableDescriptor {

    /*
     * @Generated( value = "it.eg.dbtool.db.oracle.beangen.Oracle4JPAClientBeanGen$TableBeanWriter", comments =
     * "This class was generated by OraTool", date = "Monday, 17 March 2014 15:12" )
     */

    public static final String SELECT = "Select * from Pig_File_Object /**/";
    public static final String TABLE_NAME = "Pig_File_Object";
    public static final String COL_ID_FILE_OBJECT = "id_file_object";
    public static final String COL_ID_OBJECT = "id_object";
    public static final String COL_NM_FILE_OBJECT = "nm_file_object";
    public static final String COL_ID_TIPO_FILE_OBJECT = "id_tipo_file_object";
    public static final String COL_DS_HASH_FILE_VERS = "ds_hash_file_vers";
    public static final String COL_TI_ALGO_HASH_FILE_VERS = "ti_algo_hash_file_vers";
    public static final String COL_CD_ENCODING_HASH_FILE_VERS = "cd_encoding_hash_file_vers";

    private static Map<String, ColumnDescriptor> map = new LinkedHashMap<String, ColumnDescriptor>();

    static {
        map.put(COL_ID_FILE_OBJECT, new ColumnDescriptor(COL_ID_FILE_OBJECT, Types.DECIMAL, 22, true));
        map.put(COL_ID_OBJECT, new ColumnDescriptor(COL_ID_OBJECT, Types.DECIMAL, 22, false));
        map.put(COL_NM_FILE_OBJECT, new ColumnDescriptor(COL_NM_FILE_OBJECT, Types.VARCHAR, 100, false));
        map.put(COL_ID_TIPO_FILE_OBJECT, new ColumnDescriptor(COL_ID_TIPO_FILE_OBJECT, Types.DECIMAL, 22, false));
        map.put(COL_DS_HASH_FILE_VERS, new ColumnDescriptor(COL_DS_HASH_FILE_VERS, Types.VARCHAR, 254, false));
        map.put(COL_TI_ALGO_HASH_FILE_VERS, new ColumnDescriptor(COL_TI_ALGO_HASH_FILE_VERS, Types.VARCHAR, 20, false));
        map.put(COL_CD_ENCODING_HASH_FILE_VERS,
                new ColumnDescriptor(COL_CD_ENCODING_HASH_FILE_VERS, Types.VARCHAR, 100, false));
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
