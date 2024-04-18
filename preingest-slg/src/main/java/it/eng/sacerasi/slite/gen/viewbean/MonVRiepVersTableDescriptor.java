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
 *         Bean per la tabella Mon_V_Riep_Vers
 *
 */
public class MonVRiepVersTableDescriptor extends TableDescriptor {

    /*
     * @Generated( value = "it.eg.dbtool.db.oracle.beangen.Oracle4JPAClientBeanGen$ViewBeanWriter", comments =
     * "This class was generated by OraTool", date = "Monday, 17 March 2014 15:12" )
     */
    public static final String SELECT = "Select * from Mon_V_Riep_Vers /**/";
    public static final String TABLE_NAME = "Mon_V_Riep_Vers";
    public static final String COL_NM_AMBIENTE_VERS = "nm_ambiente_vers";
    public static final String COL_NM_VERS = "nm_vers";
    public static final String COL_ID_VERS = "id_vers";
    public static final String COL_FL_CESSATO = "fl_cessato";
    public static final String COL_FL_SES_ERR_NORIS_NOVER = "fl_ses_err_noris_nover";
    public static final String COL_FL_OBJ_WARN = "fl_obj_warn";
    public static final String COL_FL_SES_NOTIF_DELAY = "fl_ses_notif_delay";
    public static final String COL_FL_SES_REGCODA_NORIS_NOVER = "fl_ses_regcoda_noris_nover";
    public static final String COL_FL_SES_PREPXML_REGCODA_DELAY = "fl_ses_prepxml_regcoda_delay";
    public static final String COL_FL_SES_VERS_SACER_NORIS_NOVER = "fl_ses_vers_sacer_noris_nover";
    public static final String COL_FL_SES_VERS_SACER_DELAY = "fl_ses_vers_sacer_delay";

    private static Map<String, ColumnDescriptor> map = new LinkedHashMap<String, ColumnDescriptor>();

    static {
        map.put(COL_NM_AMBIENTE_VERS, new ColumnDescriptor(COL_NM_AMBIENTE_VERS, Types.VARCHAR, 100, true));
        map.put(COL_NM_VERS, new ColumnDescriptor(COL_NM_VERS, Types.VARCHAR, 100, true));
        map.put(COL_ID_VERS, new ColumnDescriptor(COL_ID_VERS, Types.DECIMAL, 22, true));
        map.put(COL_FL_CESSATO, new ColumnDescriptor(COL_FL_CESSATO, Types.VARCHAR, 1, false));
        map.put(COL_FL_SES_ERR_NORIS_NOVER, new ColumnDescriptor(COL_FL_SES_ERR_NORIS_NOVER, Types.VARCHAR, 1, true));
        map.put(COL_FL_OBJ_WARN, new ColumnDescriptor(COL_FL_OBJ_WARN, Types.VARCHAR, 1, true));
        map.put(COL_FL_SES_NOTIF_DELAY, new ColumnDescriptor(COL_FL_SES_NOTIF_DELAY, Types.VARCHAR, 1, true));
        map.put(COL_FL_SES_REGCODA_NORIS_NOVER,
                new ColumnDescriptor(COL_FL_SES_REGCODA_NORIS_NOVER, Types.VARCHAR, 1, true));
        map.put(COL_FL_SES_PREPXML_REGCODA_DELAY,
                new ColumnDescriptor(COL_FL_SES_PREPXML_REGCODA_DELAY, Types.VARCHAR, 1, true));
        map.put(COL_FL_SES_VERS_SACER_NORIS_NOVER,
                new ColumnDescriptor(COL_FL_SES_VERS_SACER_NORIS_NOVER, Types.VARCHAR, 1, true));
        map.put(COL_FL_SES_VERS_SACER_DELAY, new ColumnDescriptor(COL_FL_SES_VERS_SACER_DELAY, Types.VARCHAR, 1, true));
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