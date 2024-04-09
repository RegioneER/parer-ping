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
 *         Bean per la tabella Pig_Unita_Doc_Sessione
 *
 */
public class PigUnitaDocSessioneTableDescriptor extends TableDescriptor {

    /*
     * @Generated( value = "it.eg.dbtool.db.oracle.beangen.Oracle4JPAClientBeanGen$TableBeanWriter", comments =
     * "This class was generated by OraTool", date = "Monday, 17 March 2014 15:12" )
     */

    public static final String SELECT = "Select * from Pig_Unita_Doc_Sessione /**/";
    public static final String TABLE_NAME = "Pig_Unita_Doc_Sessione";
    public static final String COL_ID_UNITA_DOC_SESSIONE = "id_unita_doc_sessione";
    public static final String COL_ID_SESSIONE_INGEST = "id_sessione_ingest";
    public static final String COL_CD_REGISTRO_UNITA_DOC_SACER = "cd_registro_unita_doc_sacer";
    public static final String COL_AA_UNITA_DOC_SACER = "aa_unita_doc_sacer";
    public static final String COL_CD_KEY_UNITA_DOC_SACER = "cd_key_unita_doc_sacer";
    public static final String COL_TI_STATO_UNITA_DOC_SESSIONE = "ti_stato_unita_doc_sessione";
    public static final String COL_NI_SIZE_FILE_BYTE = "ni_size_file_byte";
    public static final String COL_CD_ERR_SACER = "cd_err_sacer";
    public static final String COL_DL_ERR_SACER = "dl_err_sacer";

    private static Map<String, ColumnDescriptor> map = new LinkedHashMap<String, ColumnDescriptor>();

    static {
        map.put(COL_ID_UNITA_DOC_SESSIONE, new ColumnDescriptor(COL_ID_UNITA_DOC_SESSIONE, Types.DECIMAL, 22, true));
        map.put(COL_ID_SESSIONE_INGEST, new ColumnDescriptor(COL_ID_SESSIONE_INGEST, Types.DECIMAL, 22, false));
        map.put(COL_CD_REGISTRO_UNITA_DOC_SACER,
                new ColumnDescriptor(COL_CD_REGISTRO_UNITA_DOC_SACER, Types.VARCHAR, 100, false));
        map.put(COL_AA_UNITA_DOC_SACER, new ColumnDescriptor(COL_AA_UNITA_DOC_SACER, Types.DECIMAL, 22, false));
        map.put(COL_CD_KEY_UNITA_DOC_SACER,
                new ColumnDescriptor(COL_CD_KEY_UNITA_DOC_SACER, Types.VARCHAR, 100, false));
        map.put(COL_TI_STATO_UNITA_DOC_SESSIONE,
                new ColumnDescriptor(COL_TI_STATO_UNITA_DOC_SESSIONE, Types.VARCHAR, 20, false));
        map.put(COL_NI_SIZE_FILE_BYTE, new ColumnDescriptor(COL_NI_SIZE_FILE_BYTE, Types.DECIMAL, 22, false));
        map.put(COL_CD_ERR_SACER, new ColumnDescriptor(COL_CD_ERR_SACER, Types.VARCHAR, 100, false));
        map.put(COL_DL_ERR_SACER, new ColumnDescriptor(COL_DL_ERR_SACER, Types.VARCHAR, 1024, false));
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
