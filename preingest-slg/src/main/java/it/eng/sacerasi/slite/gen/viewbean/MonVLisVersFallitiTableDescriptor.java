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
 *         Bean per la tabella Mon_V_Lis_Vers_Falliti
 *
 */
public class MonVLisVersFallitiTableDescriptor extends TableDescriptor {

    /*
     * @Generated( value = "it.eg.dbtool.db.oracle.beangen.Oracle4JPAClientBeanGen$ViewBeanWriter", comments =
     * "This class was generated by OraTool", date = "Tuesday, 23 August 2016 14:32" )
     */

    public static final String SELECT = "Select * from Mon_V_Lis_Vers_Falliti /**/";
    public static final String TABLE_NAME = "Mon_V_Lis_Vers_Falliti";
    public static final String COL_ID_SESSIONE_INGEST = "id_sessione_ingest";
    public static final String COL_DT_APERTURA = "dt_apertura";
    public static final String COL_TI_STATO = "ti_stato";
    public static final String COL_DT_STATO_COR = "dt_stato_cor";
    public static final String COL_ID_AMBIENTE_VERS = "id_ambiente_vers";
    public static final String COL_NM_AMBIENTE_VERS = "nm_ambiente_vers";
    public static final String COL_ID_VERS = "id_vers";
    public static final String COL_NM_VERS = "nm_vers";
    public static final String COL_CD_KEY_OBJECT = "cd_key_object";
    public static final String COL_DS_OBJECT = "ds_object";
    public static final String COL_CD_ERR = "cd_err";
    public static final String COL_DL_ERR = "dl_err";
    public static final String COL_TI_STATO_RISOLUZ = "ti_stato_risoluz";
    public static final String COL_FL_VERIF = "fl_verif";
    public static final String COL_FL_NON_RISOLUB = "fl_non_risolub";
    public static final String COL_NM_TIPO_OBJECT = "nm_tipo_object";
    public static final String COL_TI_DT_CREAZIONE = "ti_dt_creazione";
    public static final String COL_BL_XML = "bl_xml";

    private static Map<String, ColumnDescriptor> map = new LinkedHashMap<String, ColumnDescriptor>();

    static {
        map.put(COL_ID_SESSIONE_INGEST, new ColumnDescriptor(COL_ID_SESSIONE_INGEST, Types.DECIMAL, 22, true));
        map.put(COL_DT_APERTURA, new ColumnDescriptor(COL_DT_APERTURA, Types.TIMESTAMP, 7, true));
        map.put(COL_TI_STATO, new ColumnDescriptor(COL_TI_STATO, Types.VARCHAR, 30, true));
        map.put(COL_DT_STATO_COR, new ColumnDescriptor(COL_DT_STATO_COR, Types.TIMESTAMP, 11, true));
        map.put(COL_ID_AMBIENTE_VERS, new ColumnDescriptor(COL_ID_AMBIENTE_VERS, Types.DECIMAL, 22, true));
        map.put(COL_NM_AMBIENTE_VERS, new ColumnDescriptor(COL_NM_AMBIENTE_VERS, Types.VARCHAR, 100, true));
        map.put(COL_ID_VERS, new ColumnDescriptor(COL_ID_VERS, Types.DECIMAL, 22, true));
        map.put(COL_NM_VERS, new ColumnDescriptor(COL_NM_VERS, Types.VARCHAR, 100, true));
        map.put(COL_CD_KEY_OBJECT, new ColumnDescriptor(COL_CD_KEY_OBJECT, Types.VARCHAR, 100, true));
        map.put(COL_DS_OBJECT, new ColumnDescriptor(COL_DS_OBJECT, Types.VARCHAR, 1024, true));
        map.put(COL_CD_ERR, new ColumnDescriptor(COL_CD_ERR, Types.VARCHAR, 100, true));
        map.put(COL_DL_ERR, new ColumnDescriptor(COL_DL_ERR, Types.VARCHAR, 1024, true));
        map.put(COL_TI_STATO_RISOLUZ, new ColumnDescriptor(COL_TI_STATO_RISOLUZ, Types.VARCHAR, 14, true));
        map.put(COL_FL_VERIF, new ColumnDescriptor(COL_FL_VERIF, Types.VARCHAR, 1, true));
        map.put(COL_FL_NON_RISOLUB, new ColumnDescriptor(COL_FL_NON_RISOLUB, Types.VARCHAR, 1, true));
        map.put(COL_NM_TIPO_OBJECT, new ColumnDescriptor(COL_NM_TIPO_OBJECT, Types.VARCHAR, 100, true));
        map.put(COL_TI_DT_CREAZIONE, new ColumnDescriptor(COL_TI_DT_CREAZIONE, Types.VARCHAR, 19, true));
        map.put(COL_BL_XML, new ColumnDescriptor(COL_BL_XML, Types.CLOB, 4000, true));
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
