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

package it.eng.xformer.web.util;

import java.util.Collections;
import java.util.List;

import it.eng.spagoLite.db.base.row.BaseRow;
import it.eng.spagoLite.db.base.table.BaseTable;
import it.eng.spagoLite.db.decodemap.DecodeMapIF;
import it.eng.spagoLite.db.oracle.decode.DecodeMap;

@SuppressWarnings("unchecked")
public class ComboGetter {

    public ComboGetter() {
        // empty
    }

    /*
     * GESTIONE DECODEMAP GENERICHE
     */
    public static DecodeMapIF getMappaGenericFlagSiNo() {
        BaseTable bt = new BaseTable();
        BaseRow br = new BaseRow();
        BaseRow br1 = new BaseRow();
        // Imposto i valori della combo INDICATORE
        DecodeMap mappaIndicatore = new DecodeMap();
        br.setString("flag", Constants.ComboFlag.SI.name());
        br.setString("valore", Constants.ComboFlag.SI.getValue());
        bt.add(br);
        br1.setString("flag", Constants.ComboFlag.NO.name());
        br1.setString("valore", Constants.ComboFlag.NO.getValue());
        bt.add(br1);
        mappaIndicatore.populatedMap(bt, "valore", "flag");
        return mappaIndicatore;
    }

    public static DecodeMapIF getMappaParametersSetType() {
        BaseTable bt = new BaseTable();
        BaseRow br = new BaseRow();
        BaseRow br1 = new BaseRow();
        // Imposto i valori della combo INDICATORE
        DecodeMap mappaIndicatore = new DecodeMap();
        br.setString("flag", Constants.ComboFlagParametersSetType.ARCHIVISTICO.name());
        br.setString("valore", Constants.ComboFlagParametersSetType.ARCHIVISTICO.getValue());
        bt.add(br);
        br1.setString("flag", Constants.ComboFlagParametersSetType.TECNICO.name());
        br1.setString("valore", Constants.ComboFlagParametersSetType.TECNICO.getValue());
        bt.add(br1);
        mappaIndicatore.populatedMap(bt, "valore", "flag");
        return mappaIndicatore;
    }

    public static <T extends Enum<?>> DecodeMap getMappaSortedGenericEnum(String key, T... enumerator) {
        BaseTable bt = new BaseTable();
        DecodeMap mappa = new DecodeMap();
        for (T mod : Utils.sortEnum(enumerator)) {
            bt.add(createKeyValueBaseRow(key, mod.name()));
        }
        mappa.populatedMap(bt, key, key);
        return mappa;
    }

    public static <T extends Enum<?>> DecodeMap getMappaOrdinalGenericEnum(String key, T... enumerator) {
        BaseTable bt = new BaseTable();
        DecodeMap mappa = new DecodeMap();
        for (T mod : enumerator) {
            bt.add(createKeyValueBaseRow(key, mod.name()));
        }
        mappa.populatedMap(bt, key, key);
        return mappa;
    }

    private static BaseRow createKeyValueBaseRow(String key, String value) {
        BaseRow br = new BaseRow();
        br.setString(key, value);
        return br;
    }

    public static <T extends Enum<?>> DecodeMap getMappaSortedGenericStringList(String key, List<String> list) {
        BaseTable bt = new BaseTable();
        DecodeMap mappa = new DecodeMap();

        Collections.sort(list);
        for (String value : list) {
            bt.add(createKeyValueBaseRow(key, value));
        }
        mappa.populatedMap(bt, key, key);
        return mappa;
    }
}
