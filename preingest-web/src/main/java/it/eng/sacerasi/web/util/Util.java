/*
 * Engineering Ingegneria Informatica S.p.A.
 *
 * Copyright (C) 2023 Regione Emilia-Romagna <p/> This program is free software: you can
 * redistribute it and/or modify it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version. <p/> This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Affero General Public License for more details. <p/> You should
 * have received a copy of the GNU Affero General Public License along with this program. If not,
 * see <https://www.gnu.org/licenses/>.
 */

package it.eng.sacerasi.web.util;

import java.util.Collection;
import java.util.SortedMap;
import java.util.TreeMap;

import it.eng.spagoLite.db.base.row.BaseRow;
import it.eng.spagoLite.db.base.table.AbstractBaseTable;
import it.eng.spagoLite.db.base.table.BaseTable;
import it.eng.spagoLite.db.oracle.decode.DecodeMap;

/**
 *
 * @author Bonora_L
 */
public class Util {

    /**
     * Metodo statico per ordinare un enum tramite il valore
     *
     * @param <T>        elemento generico di tipo {@link Enum}
     * @param enumValues l'array di valori dell'enum
     *
     * @return la collezione ordinata
     */
    public static <T extends Enum<?>> Collection<T> sortEnum(T[] enumValues) {
	SortedMap<String, T> map = new TreeMap<String, T>();
	for (T l : enumValues) {
	    map.put(l.name(), l);
	}
	return map.values();
    }

    /*
     * Metodo di popolamento delle combobox di tipo Flag SI/NO
     *
     * @return DecodeMap per i flag contenente la codifica utilizzata nel DB
     */
    public static DecodeMap getFlagComboDecodeMap() {
	// Imposto i valori della combo FL_ATTIVO per i Filtri Utenti
	BaseTable bt = new BaseTable();
	BaseRow br = new BaseRow();
	BaseRow br1 = new BaseRow();
	br.setString("flag", "SI");
	br.setString("valore", WebConstants.DB_TRUE);
	bt.add(br);
	br1.setString("flag", "NO");
	br1.setString("valore", WebConstants.DB_FALSE);
	bt.add(br1);
	DecodeMap combo = DecodeMap.Factory.newInstance(bt, "valore", "flag");
	return combo;
    }

    public static BaseRow createKeyValueBaseRow(String key, String value) {
	BaseRow br = new BaseRow();
	br.setString(key, value);
	return br;
    }

    /**
     * Metodo generico di popolamento di una decodeMap dato i valori di un enum
     *
     * @param <T>        elemento generico di tipo {@link Enum}
     * @param key        chiave
     * @param enumValues valori di tipo T
     *
     * @return mappa con codifica chiave/valore
     */
    public static <T extends Enum<?>> DecodeMap createDataDecodeMap(String key, T[] enumValues) {
	// Inizializzo le combo di supporto
	BaseTable bt = new BaseTable();

	// Imposto i valori della combo ordinati
	DecodeMap mappa = new DecodeMap();

	for (T value : Util.sortEnum(enumValues)) {
	    bt.add(Util.createKeyValueBaseRow(key, value.name()));
	}
	mappa.populatedMap(bt, key, key);
	return mappa;
    }

    public static <T extends AbstractBaseTable<?>> DecodeMap createDataDecodeMap(T tableBean,
	    String key, String desc) {
	// Imposto i valori della combo ordinati
	DecodeMap mappa = new DecodeMap();
	mappa.populatedMap(tableBean, key, desc);
	return mappa;
    }

}
