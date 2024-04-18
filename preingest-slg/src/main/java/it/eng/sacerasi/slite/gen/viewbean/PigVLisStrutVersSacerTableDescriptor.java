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
 *         Bean per la tabella Pig_V_Lis_Strut_Vers_Sacer
 *
 */
public class PigVLisStrutVersSacerTableDescriptor extends TableDescriptor {

    /*
     * @Generated( value = "it.eg.dbtool.db.oracle.beangen.Oracle4JPAClientBeanGen$ViewBeanWriter", comments =
     * "This class was generated by OraTool", date = "Monday, 27 March 2017 15:48" )
     */

    public static final String SELECT = "Select * from Pig_V_Lis_Strut_Vers_Sacer /**/";
    public static final String TABLE_NAME = "Pig_V_Lis_Strut_Vers_Sacer";
    public static final String COL_ID_VERS = "id_vers";
    public static final String COL_ID_TIPO_OBJECT = "id_tipo_object";
    public static final String COL_TI_DICH_VERS_TI_OBJ = "ti_dich_vers_ti_obj";
    public static final String COL_ID_DICH_VERS_SACER = "id_dich_vers_sacer";
    public static final String COL_ID_ORGANIZ_IAM_DICH = "id_organiz_iam_dich";
    public static final String COL_TI_DICH_VERS = "ti_dich_vers";
    public static final String COL_NM_USERID_SACER = "nm_userid_sacer";
    public static final String COL_CD_PASSWORD_SACER = "cd_password_sacer";
    public static final String COL_ID_ORGANIZ_IAM_STRUT = "id_organiz_iam_strut";
    public static final String COL_NM_ORGANIZ_IAM_STRUT = "nm_organiz_iam_strut";
    public static final String COL_DL_COMPOSITO_ORGANIZ_STRUT = "dl_composito_organiz_strut";

    private static Map<String, ColumnDescriptor> map = new LinkedHashMap<String, ColumnDescriptor>();

    static {
        map.put(COL_ID_VERS, new ColumnDescriptor(COL_ID_VERS, Types.DECIMAL, 22, true));
        map.put(COL_ID_TIPO_OBJECT, new ColumnDescriptor(COL_ID_TIPO_OBJECT, Types.DECIMAL, 22, true));
        map.put(COL_TI_DICH_VERS_TI_OBJ, new ColumnDescriptor(COL_TI_DICH_VERS_TI_OBJ, Types.VARCHAR, 6, true));
        map.put(COL_ID_DICH_VERS_SACER, new ColumnDescriptor(COL_ID_DICH_VERS_SACER, Types.DECIMAL, 22, true));
        map.put(COL_ID_ORGANIZ_IAM_DICH, new ColumnDescriptor(COL_ID_ORGANIZ_IAM_DICH, Types.DECIMAL, 22, true));
        map.put(COL_TI_DICH_VERS, new ColumnDescriptor(COL_TI_DICH_VERS, Types.VARCHAR, 20, true));
        map.put(COL_NM_USERID_SACER, new ColumnDescriptor(COL_NM_USERID_SACER, Types.VARCHAR, 100, true));
        map.put(COL_CD_PASSWORD_SACER, new ColumnDescriptor(COL_CD_PASSWORD_SACER, Types.VARCHAR, 100, true));
        map.put(COL_ID_ORGANIZ_IAM_STRUT, new ColumnDescriptor(COL_ID_ORGANIZ_IAM_STRUT, Types.DECIMAL, 22, true));
        map.put(COL_NM_ORGANIZ_IAM_STRUT, new ColumnDescriptor(COL_NM_ORGANIZ_IAM_STRUT, Types.VARCHAR, 100, true));
        map.put(COL_DL_COMPOSITO_ORGANIZ_STRUT,
                new ColumnDescriptor(COL_DL_COMPOSITO_ORGANIZ_STRUT, Types.VARCHAR, 4000, true));
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