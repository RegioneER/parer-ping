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

package it.eng.sacerasi.web.util;

import java.util.Collections;
import java.util.List;

import it.eng.sacerasi.util.DateUtil;
import it.eng.spagoLite.db.base.row.BaseRow;
import it.eng.spagoLite.db.base.table.BaseTable;
import it.eng.spagoLite.db.decodemap.DecodeMapIF;
import it.eng.spagoLite.db.oracle.decode.DecodeMap;

@SuppressWarnings("unchecked")
public class ComboGetter {

    public static final String CAMPO_REGISTRO_AG = "registro_ag";
    public static final String CAMPO_TI_GESTIONE_PARAM = "ti_gestione_param";
    public static final String CAMPO_TIPOLOGIA = "tipologia";
    public static final String CAMPO_VALORE = "valore";
    public static final String CAMPO_FLAG = "flag";
    public static final String CAMPO_NOME = "nome";
    public static final String CAMPO_ANNO = "anno";
    public static final String CAMPO_TIPO_VERSATORE = "nm_tipo_vers";

    private ComboGetter() {
        throw new IllegalStateException("Utility class");
    }

    public static DecodeMap getMappaTiOperReplic() {
        BaseTable bt = new BaseTable();
        DecodeMap mappaTiOper = new DecodeMap();
        String key = "ti_oper";
        for (Constants.TiOperReplic tiOper : Utils.sortEnum(Constants.TiOperReplic.values())) {
            bt.add(createKeyValueBaseRow(key, tiOper.name()));
        }
        mappaTiOper.populatedMap(bt, key, key);
        return mappaTiOper;
    }

    public static DecodeMap getMappaTiStatoReplic() {
        BaseTable bt = new BaseTable();
        DecodeMap mappaTiStato = new DecodeMap();
        String key = "ti_stato";
        for (Constants.TiStatoReplic tiStato : Utils.sortEnum(Constants.TiStatoReplic.values())) {
            bt.add(createKeyValueBaseRow(key, tiStato.name()));
        }
        mappaTiStato.populatedMap(bt, key, key);
        return mappaTiStato;
    }

    public static DecodeMap getMappaTiDichVers() {
        BaseTable bt = new BaseTable();
        DecodeMap mappaTiDichVers = new DecodeMap();
        String key = "ti_dich_vers";
        for (Constants.TiDichVers tiDichVers : Utils.sortEnum(Constants.TiDichVers.values())) {
            bt.add(createKeyValueBaseRow(key, tiDichVers.name()));
        }
        mappaTiDichVers.populatedMap(bt, key, key);
        return mappaTiDichVers;
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
        br.setString(CAMPO_FLAG, Constants.ComboFlag.SI.name());
        br.setString(CAMPO_VALORE, Constants.ComboFlag.SI.getValue());
        bt.add(br);
        br1.setString(CAMPO_FLAG, Constants.ComboFlag.NO.name());
        br1.setString(CAMPO_VALORE, Constants.ComboFlag.NO.getValue());
        bt.add(br1);
        mappaIndicatore.populatedMap(bt, CAMPO_VALORE, CAMPO_FLAG);
        return mappaIndicatore;
    }

    public static DecodeMapIF getMappaGenericFlagVerificaOkDaRivedere() {
        BaseTable bt = new BaseTable();
        BaseRow br = new BaseRow();
        BaseRow br1 = new BaseRow();
        // Imposto i valori della combo INDICATORE
        DecodeMap mappaIndicatore = new DecodeMap();
        br.setString(CAMPO_FLAG, "Verifica OK");
        br.setString(CAMPO_VALORE, Constants.ComboFlagOk.OK.getValue());
        bt.add(br);
        br1.setString(CAMPO_FLAG, "Da rivedere");
        br1.setString(CAMPO_VALORE, Constants.ComboFlagOk.KO.getValue());
        bt.add(br1);
        mappaIndicatore.populatedMap(bt, CAMPO_VALORE, CAMPO_FLAG);
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

    public static DecodeMapIF getMappaTiGestioneParam() {
        BaseTable bt = new BaseTable();
        /* Imposto i valori della combo */
        DecodeMap mappaTiGestioneParam = new DecodeMap();
        bt.add(createKeyValueBaseRow(CAMPO_TI_GESTIONE_PARAM, "amministrazione"));
        bt.add(createKeyValueBaseRow(CAMPO_TI_GESTIONE_PARAM, "conservazione"));
        bt.add(createKeyValueBaseRow(CAMPO_TI_GESTIONE_PARAM, "gestione"));
        mappaTiGestioneParam.populatedMap(bt, CAMPO_TI_GESTIONE_PARAM, CAMPO_TI_GESTIONE_PARAM);
        return mappaTiGestioneParam;
    }

    public static DecodeMapIF getMappaTipologia() {
        BaseTable bt = new BaseTable();
        /* Imposto i valori della combo */
        DecodeMap mappaTipologia = new DecodeMap();
        bt.add(createKeyValueBaseRow(CAMPO_TIPOLOGIA, "PRODUTTORE"));
        bt.add(createKeyValueBaseRow(CAMPO_TIPOLOGIA, "FORNITORE_ESTERNO"));
        bt.add(createKeyValueBaseRow(CAMPO_TIPOLOGIA, "SOGGETTO_ATTUATORE"));
        mappaTipologia.populatedMap(bt, CAMPO_TIPOLOGIA, CAMPO_TIPOLOGIA);
        return mappaTipologia;
    }

    public static DecodeMapIF getRangeAnniReversed(int anno1, int anno2) {
        return getRangeAnniCommon(anno1, anno2, true);
    }

    public static DecodeMapIF getRangeAnni(int anno1, int anno2) {
        return getRangeAnniCommon(anno1, anno2, false);
    }

    private static DecodeMapIF getRangeAnniCommon(int anno1, int anno2, boolean reversed) {
        BaseTable bt = new BaseTable();
        /* Imposto i valori della combo */
        DecodeMap mappaAnni = new DecodeMap();
        List<Integer> l = DateUtil.getYearsBetween(anno1, anno2);
        if (reversed) {
            Collections.reverse(l);
        }

        for (Integer anno : l) {
            BaseRow row = new BaseRow();
            row.setString(CAMPO_ANNO, Integer.toString(anno));
            bt.add(row);
        }
        mappaAnni.populatedMap(bt, CAMPO_ANNO, CAMPO_ANNO);
        return mappaAnni;
    }

    // TOrna ti tipi Versatore per la query MEV#25727 - Aggiungere colonna tipo versatore in gestione versatori
    public static DecodeMapIF getTipiVersatore() {
        BaseTable bt = new BaseTable();
        /* Imposto i valori della combo */
        DecodeMap mappaTipi = new DecodeMap();
        BaseRow row = new BaseRow();
        row.setString(CAMPO_TIPO_VERSATORE, "FORNITORE_ESTERNO");
        bt.add(row);
        row = new BaseRow();
        row.setString(CAMPO_TIPO_VERSATORE, "PRODUTTORE");
        bt.add(row);
        row = new BaseRow();
        row.setString(CAMPO_TIPO_VERSATORE, "SOGGETTO_ATTUATORE");
        bt.add(row);
        mappaTipi.populatedMap(bt, CAMPO_TIPO_VERSATORE, CAMPO_TIPO_VERSATORE);
        return mappaTipi;
    }

    public static DecodeMapIF getValoriRegistroAg() {
        BaseTable bt = new BaseTable();
        /* Imposto i valori della combo */
        DecodeMap mappaTipologia = new DecodeMap();
        bt.add(createKeyValueBaseRow(CAMPO_REGISTRO_AG, "CR"));
        bt.add(createKeyValueBaseRow(CAMPO_REGISTRO_AG, "PG"));
        mappaTipologia.populatedMap(bt, CAMPO_REGISTRO_AG, CAMPO_REGISTRO_AG);
        return mappaTipologia;
    }

    // MEV22933
    public static DecodeMapIF getTiValoreParamApplicCombo() {
        BaseTable bt = new BaseTable();
        BaseRow br = new BaseRow();
        BaseRow br1 = new BaseRow();

        DecodeMap mappaTipiValori = new DecodeMap();
        br.setString(CAMPO_NOME, Constants.ComboValueParamentersType.STRINGA.name());
        br.setString(CAMPO_VALORE, Constants.ComboValueParamentersType.STRINGA.name());
        bt.add(br);
        br1.setString(CAMPO_NOME, Constants.ComboValueParamentersType.PASSWORD.name());
        br1.setString(CAMPO_VALORE, Constants.ComboValueParamentersType.PASSWORD.name());
        bt.add(br1);
        mappaTipiValori.populatedMap(bt, CAMPO_VALORE, CAMPO_NOME);
        return mappaTipiValori;
    }

    public static DecodeMap getMappaTiStatoJob() {
        BaseTable bt = new BaseTable();
        BaseRow br = new BaseRow();
        BaseRow br1 = new BaseRow();
        BaseRow br2 = new BaseRow();
        DecodeMap mappaStatoAgg = new DecodeMap();
        br.setString("ti_stato_job", "ATTIVO");
        bt.add(br);
        br1.setString("ti_stato_job", "DISATTIVO");
        bt.add(br1);
        br2.setString("ti_stato_job", "IN_ESECUZIONE");
        bt.add(br2);
        mappaStatoAgg.populatedMap(bt, "ti_stato_job", "ti_stato_job");
        return mappaStatoAgg;
    }
}
