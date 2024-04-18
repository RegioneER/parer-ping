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
 *         Bean per la tabella Pig_Log_Job
 *
 */
public class PigLogJobTableDescriptor extends TableDescriptor {

    /*
     * @Generated( value = "it.eg.dbtool.db.oracle.beangen.Oracle4JPAClientBeanGen$TableBeanWriter", comments =
     * "This class was generated by OraTool", date = "Monday, 17 March 2014 15:12" )
     */

    public static final String SELECT = "Select * from Pig_Log_Job /**/";
    public static final String TABLE_NAME = "Pig_Log_Job";
    public static final String COL_ID_LOG_JOB = "id_log_job";
    public static final String COL_NM_JOB = "nm_job";
    public static final String COL_TI_REG_LOG_JOB = "ti_reg_log_job";
    public static final String COL_DT_REG_LOG_JOB = "dt_reg_log_job";
    public static final String COL_DL_MSG_ERR = "dl_msg_err";

    private static Map<String, ColumnDescriptor> map = new LinkedHashMap<String, ColumnDescriptor>();

    static {
        map.put(COL_ID_LOG_JOB, new ColumnDescriptor(COL_ID_LOG_JOB, Types.DECIMAL, 22, true));
        map.put(COL_NM_JOB, new ColumnDescriptor(COL_NM_JOB, Types.VARCHAR, 100, false));
        map.put(COL_TI_REG_LOG_JOB, new ColumnDescriptor(COL_TI_REG_LOG_JOB, Types.VARCHAR, 20, false));
        map.put(COL_DT_REG_LOG_JOB, new ColumnDescriptor(COL_DT_REG_LOG_JOB, Types.TIMESTAMP, 7, false));
        map.put(COL_DL_MSG_ERR, new ColumnDescriptor(COL_DL_MSG_ERR, Types.VARCHAR, 1024, false));
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