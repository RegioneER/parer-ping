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
 *         Bean per la tabella Mon_V_Vis_Obj
 *
 */
public class MonVVisObjTableDescriptor extends TableDescriptor {

    /*
     * @Generated( value = "it.eg.dbtool.db.oracle.beangen.Oracle4JPAClientBeanGen$ViewBeanWriter", comments =
     * "This class was generated by OraTool", date = "Wednesday, 9 August 2017 09:47" )
     */

    public static final String SELECT = "Select * from Mon_V_Vis_Obj /**/";
    public static final String TABLE_NAME = "Mon_V_Vis_Obj";
    public static final String COL_ID_AMBIENTE_VERS = "id_ambiente_vers";
    public static final String COL_NM_AMBIENTE_VERS = "nm_ambiente_vers";
    public static final String COL_ID_VERS = "id_vers";
    public static final String COL_NM_VERS = "nm_vers";
    public static final String COL_ID_OBJECT = "id_object";
    public static final String COL_CD_KEY_OBJECT = "cd_key_object";
    public static final String COL_ID_TIPO_OBJECT = "id_tipo_object";
    public static final String COL_NM_TIPO_OBJECT = "nm_tipo_object";
    public static final String COL_DS_INFO_OBJECT = "ds_info_object";
    public static final String COL_TI_STATO_OBJECT = "ti_stato_object";
    public static final String COL_FL_RICH_ANNUL_TIMEOUT = "fl_rich_annul_timeout";
    public static final String COL_ID_LAST_SESSIONE_INGEST = "id_last_sessione_ingest";
    public static final String COL_NI_UNITA_DOC_DA_VERS = "ni_unita_doc_da_vers";
    public static final String COL_NI_UNITA_DOC_VERS = "ni_unita_doc_vers";
    public static final String COL_NI_UNITA_DOC_VERS_OK = "ni_unita_doc_vers_ok";
    public static final String COL_NI_UNITA_DOC_VERS_ERR = "ni_unita_doc_vers_err";
    public static final String COL_NI_UNITA_DOC_VERS_TIMEOUT = "ni_unita_doc_vers_timeout";
    public static final String COL_DT_APERTURA = "dt_apertura";
    public static final String COL_DT_CHIUSURA = "dt_chiusura";
    public static final String COL_FL_FORZA_WARNING = "fl_forza_warning";
    public static final String COL_FL_FORZA_ACCETTAZIONE = "fl_forza_accettazione";
    public static final String COL_DL_MOTIVO_FORZA_ACCETTAZIONE = "dl_motivo_forza_accettazione";
    public static final String COL_DL_MOTIVO_CHIUSO_WARNING = "dl_motivo_chiuso_warning";
    public static final String COL_CD_VERSIONE_XML_VERS = "cd_versione_xml_vers";
    public static final String COL_BL_XML = "bl_xml";
    public static final String COL_TI_STATO_VERIFICA_HASH = "ti_stato_verifica_hash";
    public static final String COL_CD_VERS_GEN = "cd_vers_gen";
    public static final String COL_CD_TRASF = "cd_trasf";
    public static final String COL_CD_VERSIONE_TRASF = "cd_versione_trasf";
    public static final String COL_TI_GEST_OGGETTI_FIGLI = "ti_gest_oggetti_figli";
    public static final String COL_NI_UNITA_DOC_ATTESE = "ni_unita_doc_attese";
    public static final String COL_PG_OGGETTO_TRASF = "pg_oggetto_trasf";
    public static final String COL_TI_VERS_FILE = "ti_vers_file";
    public static final String COL_ID_AMBIENTE_VERS_PADRE = "id_ambiente_vers_padre";
    public static final String COL_NM_AMBIENTE_VERS_PADRE = "nm_ambiente_vers_padre";
    public static final String COL_ID_VERS_PADRE = "id_vers_padre";
    public static final String COL_NM_VERS_PADRE = "nm_vers_padre";
    public static final String COL_ID_OBJECT_PADRE = "id_object_padre";
    public static final String COL_CD_KEY_OBJECT_PADRE = "cd_key_object_padre";
    public static final String COL_DS_OBJECT_PADRE = "ds_object_padre";
    public static final String COL_ID_TIPO_OBJECT_PADRE = "id_tipo_object_padre";
    public static final String COL_NM_TIPO_OBJECT_PADRE = "nm_tipo_object_padre";
    public static final String COL_TI_STATO_OBJECT_PADRE = "ti_stato_object_padre";
    public static final String COL_DT_STATO_COR_PADRE = "dt_stato_cor_padre";
    public static final String COL_NI_TOT_OBJECT_TRASF = "ni_tot_object_trasf";
    public static final String COL_ID_OBJECT_TRASF = "id_object_trasf";

    private static Map<String, ColumnDescriptor> map = new LinkedHashMap<String, ColumnDescriptor>();

    static {
        map.put(COL_ID_AMBIENTE_VERS, new ColumnDescriptor(COL_ID_AMBIENTE_VERS, Types.DECIMAL, 22, true));
        map.put(COL_NM_AMBIENTE_VERS, new ColumnDescriptor(COL_NM_AMBIENTE_VERS, Types.VARCHAR, 100, true));
        map.put(COL_ID_VERS, new ColumnDescriptor(COL_ID_VERS, Types.DECIMAL, 22, true));
        map.put(COL_NM_VERS, new ColumnDescriptor(COL_NM_VERS, Types.VARCHAR, 100, true));
        map.put(COL_ID_OBJECT, new ColumnDescriptor(COL_ID_OBJECT, Types.DECIMAL, 22, true));
        map.put(COL_CD_KEY_OBJECT, new ColumnDescriptor(COL_CD_KEY_OBJECT, Types.VARCHAR, 100, true));
        map.put(COL_ID_TIPO_OBJECT, new ColumnDescriptor(COL_ID_TIPO_OBJECT, Types.DECIMAL, 22, true));
        map.put(COL_NM_TIPO_OBJECT, new ColumnDescriptor(COL_NM_TIPO_OBJECT, Types.VARCHAR, 100, true));
        map.put(COL_DS_INFO_OBJECT, new ColumnDescriptor(COL_DS_INFO_OBJECT, Types.VARCHAR, 1024, true));
        map.put(COL_TI_STATO_OBJECT, new ColumnDescriptor(COL_TI_STATO_OBJECT, Types.VARCHAR, 36, true));
        map.put(COL_FL_RICH_ANNUL_TIMEOUT, new ColumnDescriptor(COL_FL_RICH_ANNUL_TIMEOUT, Types.VARCHAR, 1, true));
        map.put(COL_ID_LAST_SESSIONE_INGEST,
                new ColumnDescriptor(COL_ID_LAST_SESSIONE_INGEST, Types.DECIMAL, 22, true));
        map.put(COL_NI_UNITA_DOC_DA_VERS, new ColumnDescriptor(COL_NI_UNITA_DOC_DA_VERS, Types.DECIMAL, 22, true));
        map.put(COL_NI_UNITA_DOC_VERS, new ColumnDescriptor(COL_NI_UNITA_DOC_VERS, Types.DECIMAL, 22, true));
        map.put(COL_NI_UNITA_DOC_VERS_OK, new ColumnDescriptor(COL_NI_UNITA_DOC_VERS_OK, Types.DECIMAL, 22, true));
        map.put(COL_NI_UNITA_DOC_VERS_ERR, new ColumnDescriptor(COL_NI_UNITA_DOC_VERS_ERR, Types.DECIMAL, 22, true));
        map.put(COL_NI_UNITA_DOC_VERS_TIMEOUT,
                new ColumnDescriptor(COL_NI_UNITA_DOC_VERS_TIMEOUT, Types.DECIMAL, 22, true));
        map.put(COL_DT_APERTURA, new ColumnDescriptor(COL_DT_APERTURA, Types.TIMESTAMP, 7, true));
        map.put(COL_DT_CHIUSURA, new ColumnDescriptor(COL_DT_CHIUSURA, Types.TIMESTAMP, 7, true));
        map.put(COL_FL_FORZA_WARNING, new ColumnDescriptor(COL_FL_FORZA_WARNING, Types.VARCHAR, 1, true));
        map.put(COL_FL_FORZA_ACCETTAZIONE, new ColumnDescriptor(COL_FL_FORZA_ACCETTAZIONE, Types.VARCHAR, 1, true));
        map.put(COL_DL_MOTIVO_FORZA_ACCETTAZIONE,
                new ColumnDescriptor(COL_DL_MOTIVO_FORZA_ACCETTAZIONE, Types.VARCHAR, 1024, true));
        map.put(COL_DL_MOTIVO_CHIUSO_WARNING,
                new ColumnDescriptor(COL_DL_MOTIVO_CHIUSO_WARNING, Types.VARCHAR, 1024, true));
        map.put(COL_CD_VERSIONE_XML_VERS, new ColumnDescriptor(COL_CD_VERSIONE_XML_VERS, Types.VARCHAR, 100, true));
        map.put(COL_BL_XML, new ColumnDescriptor(COL_BL_XML, Types.CLOB, 4000, true));
        map.put(COL_TI_STATO_VERIFICA_HASH, new ColumnDescriptor(COL_TI_STATO_VERIFICA_HASH, Types.VARCHAR, 20, true));
        map.put(COL_CD_VERS_GEN, new ColumnDescriptor(COL_CD_VERS_GEN, Types.VARCHAR, 100, true));
        map.put(COL_CD_TRASF, new ColumnDescriptor(COL_CD_TRASF, Types.VARCHAR, 100, true));
        map.put(COL_CD_VERSIONE_TRASF, new ColumnDescriptor(COL_CD_VERSIONE_TRASF, Types.VARCHAR, 100, true));
        map.put(COL_TI_GEST_OGGETTI_FIGLI, new ColumnDescriptor(COL_TI_GEST_OGGETTI_FIGLI, Types.VARCHAR, 20, true));
        map.put(COL_NI_UNITA_DOC_ATTESE, new ColumnDescriptor(COL_NI_UNITA_DOC_ATTESE, Types.DECIMAL, 22, true));
        map.put(COL_PG_OGGETTO_TRASF, new ColumnDescriptor(COL_PG_OGGETTO_TRASF, Types.DECIMAL, 22, true));
        map.put(COL_TI_VERS_FILE, new ColumnDescriptor(COL_TI_VERS_FILE, Types.VARCHAR, 20, true));
        map.put(COL_ID_AMBIENTE_VERS_PADRE, new ColumnDescriptor(COL_ID_AMBIENTE_VERS_PADRE, Types.DECIMAL, 22, true));
        map.put(COL_NM_AMBIENTE_VERS_PADRE, new ColumnDescriptor(COL_NM_AMBIENTE_VERS_PADRE, Types.VARCHAR, 100, true));
        map.put(COL_ID_VERS_PADRE, new ColumnDescriptor(COL_ID_VERS_PADRE, Types.DECIMAL, 22, true));
        map.put(COL_NM_VERS_PADRE, new ColumnDescriptor(COL_NM_VERS_PADRE, Types.VARCHAR, 100, true));
        map.put(COL_ID_OBJECT_PADRE, new ColumnDescriptor(COL_ID_OBJECT_PADRE, Types.DECIMAL, 22, true));
        map.put(COL_CD_KEY_OBJECT_PADRE, new ColumnDescriptor(COL_CD_KEY_OBJECT_PADRE, Types.VARCHAR, 100, true));
        map.put(COL_DS_OBJECT_PADRE, new ColumnDescriptor(COL_DS_OBJECT_PADRE, Types.VARCHAR, 1024, true));
        map.put(COL_ID_TIPO_OBJECT_PADRE, new ColumnDescriptor(COL_ID_TIPO_OBJECT_PADRE, Types.DECIMAL, 22, true));
        map.put(COL_NM_TIPO_OBJECT_PADRE, new ColumnDescriptor(COL_NM_TIPO_OBJECT_PADRE, Types.VARCHAR, 100, true));
        map.put(COL_TI_STATO_OBJECT_PADRE, new ColumnDescriptor(COL_TI_STATO_OBJECT_PADRE, Types.VARCHAR, 30, true));
        map.put(COL_DT_STATO_COR_PADRE, new ColumnDescriptor(COL_DT_STATO_COR_PADRE, Types.TIMESTAMP, 11, true));
        map.put(COL_NI_TOT_OBJECT_TRASF, new ColumnDescriptor(COL_NI_TOT_OBJECT_TRASF, Types.DECIMAL, 22, true));
        map.put(COL_ID_OBJECT_TRASF, new ColumnDescriptor(COL_ID_OBJECT_TRASF, Types.DECIMAL, 22, true));
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
