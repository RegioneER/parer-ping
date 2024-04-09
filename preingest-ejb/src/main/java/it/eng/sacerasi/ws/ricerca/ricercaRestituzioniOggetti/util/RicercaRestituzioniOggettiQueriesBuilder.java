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

package it.eng.sacerasi.ws.ricerca.ricercaRestituzioniOggetti.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.sacerasi.common.Constants;
import it.eng.sacerasi.ws.ricerca.dto.DatoSpecFiltroConNomeColonna;
import it.eng.sacerasi.ws.ricerca.dto.DatoSpecOrderConNomeColonna;
import it.eng.sacerasi.ws.ricerca.dto.DatoSpecOutputConNomeColonna;
import it.eng.sacerasi.ws.ricerca.enums.OperatoreType;
import it.eng.sacerasi.ws.ricerca.ricercaRestituzioniOggetti.dto.RicercaRestituzioniOggettiExt;
import it.eng.sacerasi.ws.ricerca.util.DateFormatter;
import it.eng.sacerasi.ws.ricerca.util.QueriesUtils;
import it.eng.sacerasi.ws.util.Costanti.AttribDatiSpecDataType;
import it.eng.sacerasi.ws.xml.datiSpecFiltri.FiltroType.FiltroDueValori;
import it.eng.sacerasi.ws.xml.datiSpecFiltri.FiltroType.FiltroNullo;
import it.eng.sacerasi.ws.xml.datiSpecFiltri.FiltroType.FiltroUnValore;

/**
 *
 * @author Gilioli_P
 */
public class RicercaRestituzioniOggettiQueriesBuilder {

    private static final Logger log = LoggerFactory.getLogger(RicercaRestituzioniOggettiQueriesBuilder.class);
    RicercaRestituzioniOggettiExt ricercaRestituzioniOggettiExt;

    public RicercaRestituzioniOggettiQueriesBuilder(RicercaRestituzioniOggettiExt ricercaRestituzioniOggettiExt) {
        this.ricercaRestituzioniOggettiExt = ricercaRestituzioniOggettiExt;
    }

    /**
     * Costruisce le due queries di conta e ricerca
     *
     * @return objects, un array di object di 3 elementi contenenti nell'ordine: 1) la stringa della queryConta 2) la
     *         stringa della queryRicerca 3) la LinkedHashMap contenente i parametri da passare alla query in fase di
     *         sua esecuzione
     */
    public Object[] buildQueries() {
        Object[] objects = new Object[3];

        StringBuilder standardSelectForQueryRicerca = new StringBuilder(
                "SELECT oggetto.idObject, oggetto.cdKeyObject, sessione_recupero.tiStato, "
                        + "sessione_recupero.dtApertura, sessione_recupero.dtChiusura, "
                        + "sessione_recupero.cdErr, sessione_recupero.dlErr, sessione_recupero.idSessioneRecup ");

        StringBuilder queryConta;
        StringBuilder queryRicerca;
        int contatoreValori = 0;
        // LinkedHashMap con coppia chiave-valore per salvare i parametri della query
        LinkedHashMap<String, Object> valoriQuery = new LinkedHashMap<>();

        StringBuilder whereCondition = new StringBuilder(
                "WHERE sessione_recupero.pigObject.idObject = oggetto.idObject ");

        // Aggiungo condizione WHERE in base alla richiesta
        if (StringUtils
                .isNotBlank(ricercaRestituzioniOggettiExt.getRicercaRestituzioniOggettiInput().getNmAmbiente())) {
            whereCondition.append("AND sessione_recupero.nmAmbienteVers = :valore").append(contatoreValori).append(" ");
            valoriQuery.put(":valore" + contatoreValori,
                    ricercaRestituzioniOggettiExt.getRicercaRestituzioniOggettiInput().getNmAmbiente());
            contatoreValori++;
        }
        if (StringUtils
                .isNotBlank(ricercaRestituzioniOggettiExt.getRicercaRestituzioniOggettiInput().getNmVersatore())) {
            whereCondition.append("AND sessione_recupero.nmVers = :valore").append(contatoreValori).append(" ");
            valoriQuery.put(":valore" + contatoreValori,
                    ricercaRestituzioniOggettiExt.getRicercaRestituzioniOggettiInput().getNmVersatore());
            contatoreValori++;
        }
        if (StringUtils
                .isNotBlank(ricercaRestituzioniOggettiExt.getRicercaRestituzioniOggettiInput().getNmTipoObject())) {
            whereCondition.append("AND oggetto.pigTipoObject.nmTipoObject = :valore").append(contatoreValori)
                    .append(" ");
            valoriQuery.put(":valore" + contatoreValori,
                    ricercaRestituzioniOggettiExt.getRicercaRestituzioniOggettiInput().getNmTipoObject());
            contatoreValori++;
        }
        if (StringUtils
                .isNotBlank(ricercaRestituzioniOggettiExt.getRicercaRestituzioniOggettiInput().getCdKeyObject())) {
            whereCondition.append("AND oggetto.cdKeyObject = :valore").append(contatoreValori).append(" ");
            valoriQuery.put(":valore" + contatoreValori,
                    ricercaRestituzioniOggettiExt.getRicercaRestituzioniOggettiInput().getCdKeyObject());
            contatoreValori++;
        }
        if (StringUtils
                .isNotBlank(ricercaRestituzioniOggettiExt.getRicercaRestituzioniOggettiInput().getTiStatoSessione())) {
            whereCondition.append("AND sessione_recupero.tiStato = :valore").append(contatoreValori).append(" ");
            valoriQuery.put(":valore" + contatoreValori,
                    ricercaRestituzioniOggettiExt.getRicercaRestituzioniOggettiInput().getTiStatoSessione());
            contatoreValori++;
        }

        if (ricercaRestituzioniOggettiExt.getRicercaRestituzioniOggettiInput().getDtAperturaSessioneDa() != null
                && ricercaRestituzioniOggettiExt.getRicercaRestituzioniOggettiInput()
                        .getDtAperturaSessioneA() != null) {
            whereCondition.append("AND sessione_recupero.dtApertura BETWEEN ").append(":valore").append(contatoreValori)
                    .append(" AND ");
            valoriQuery.put(":valore" + contatoreValori,
                    ricercaRestituzioniOggettiExt.getRicercaRestituzioniOggettiInput().getDtAperturaSessioneDa());
            contatoreValori++;
            whereCondition.append(":valore").append(contatoreValori).append(" ");
            valoriQuery.put(":valore" + contatoreValori,
                    ricercaRestituzioniOggettiExt.getRicercaRestituzioniOggettiInput().getDtAperturaSessioneA());
            contatoreValori++;
        } else if (ricercaRestituzioniOggettiExt.getRicercaRestituzioniOggettiInput()
                .getDtAperturaSessioneDa() != null) {
            whereCondition.append("AND sessione_recupero.dtApertura >= :valore").append(contatoreValori).append(" ");
            valoriQuery.put(":valore" + contatoreValori,
                    ricercaRestituzioniOggettiExt.getRicercaRestituzioniOggettiInput().getDtAperturaSessioneDa());
            contatoreValori++;
        } else if (ricercaRestituzioniOggettiExt.getRicercaRestituzioniOggettiInput()
                .getDtAperturaSessioneA() != null) {
            whereCondition.append("AND sessione_recupero.dtApertura <= :valore").append(contatoreValori).append(" ");
            valoriQuery.put(":valore" + contatoreValori,
                    ricercaRestituzioniOggettiExt.getRicercaRestituzioniOggettiInput().getDtAperturaSessioneA());
            contatoreValori++;
        }

        // Se nome tipo object in input è definito
        if (ricercaRestituzioniOggettiExt.getRicercaRestituzioniOggettiInput().getNmTipoObject() != null) {
            // Se nome tipo object è uguale a Studio Dicom
            if (ricercaRestituzioniOggettiExt.getRicercaRestituzioniOggettiInput().getNmTipoObject()
                    .equals(Constants.STUDIO_DICOM)) {
                queryRicerca = new StringBuilder(
                        "FROM PigObject oggetto JOIN oggetto.pigSessioneRecups sessione_recupero JOIN oggetto.pigInfoDicoms info_dicom ");
                queryRicerca = queryRicerca.append(whereCondition);
                queryConta = new StringBuilder(
                        "SELECT COUNT(oggetto) FROM PigObject oggetto JOIN oggetto.pigSessioneRecups sessione_recupero JOIN oggetto.pigInfoDicoms info_dicom ");
                queryConta = queryConta.append(whereCondition);

                // Se XMLDatiSpecFiltri è definito
                if (StringUtils.isNotBlank(
                        ricercaRestituzioniOggettiExt.getRicercaRestituzioniOggettiInput().getXmlDatiSpecFiltri())) {
                    // Aggiungo la congiunzione AND con parentesi di apertura
                    queryRicerca.append("AND (");
                    queryConta.append("AND (");
                    boolean firstTime = true;
                    // Per ogni filtro definito in XMLDatiSpecFiltri
                    for (DatoSpecFiltroConNomeColonna datoSpecFiltro : ricercaRestituzioniOggettiExt
                            .getDatiSpecFiltriConNomeColonna()) {
                        // Ricavo il filtro dato specifico guardando quale dei 3 casi è diverso da null
                        FiltroUnValore fUnValore = datoSpecFiltro.getFiltro().getFiltroUnValore();
                        FiltroNullo fNullo = datoSpecFiltro.getFiltro().getFiltroNullo();
                        FiltroDueValori fDueValori = datoSpecFiltro.getFiltro().getFiltroDueValori();

                        AttribDatiSpecDataType dataType = datoSpecFiltro.getDataType();

                        if (!firstTime) {
                            // Aggiunta congiunzione AND
                            queryRicerca.append(" AND ");
                            queryConta.append(" AND ");
                        }

                        String nomeColonnaCammellata = QueriesUtils.toCamelCase(datoSpecFiltro.getColumnName());
                        // Costruisco il frammento di query
                        if (fUnValore != null) {
                            // Nome colonna
                            if (!dataType.equals(AttribDatiSpecDataType.DATA)
                                    && !dataType.equals(AttribDatiSpecDataType.DATETIME)) {
                                queryRicerca.append("UPPER(info_dicom.").append(nomeColonnaCammellata).append(") ");
                                queryConta.append("UPPER(info_dicom.").append(nomeColonnaCammellata).append(") ");
                            } else {
                                queryRicerca.append("info_dicom.").append(nomeColonnaCammellata).append(" ");
                                queryConta.append("info_dicom.").append(nomeColonnaCammellata).append(" ");
                            }

                            String[] opEl = (String[]) QueriesUtils.getMappingOperazione()
                                    .get(fUnValore.getOperatore());
                            // Operatore
                            queryRicerca.append(opEl[0]).append(" ");
                            queryConta.append(opEl[0]).append(" ");

                            // Eventuali simboli percentuali più valore
                            // Se l'operatore è di tipo IN
                            if (opEl[0].equals(OperatoreType.IN.name())) {
                                // Passa come parametro una lista
                                valoriQuery.put(":valore" + contatoreValori,
                                        Arrays.asList(fUnValore.getValore().toUpperCase().split(",")));
                            } else {
                                // Se sto passando un filtro di tipo alfanumerico o numerico
                                if (!dataType.equals(AttribDatiSpecDataType.DATA)
                                        && !dataType.equals(AttribDatiSpecDataType.DATETIME)) {
                                    valoriQuery.put(":valore" + contatoreValori,
                                            opEl[1] + fUnValore.getValore().toUpperCase() + opEl[2]);
                                } // Se sto passando un filtri di tipo data
                                else {
                                    try {
                                        if (dataType.equals(AttribDatiSpecDataType.DATETIME)) {
                                            // Ho passato come parametri dei timestamp
                                            DateFormat timeStampDf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
                                            Date date = (Date) timeStampDf.parse(fUnValore.getValore());
                                            valoriQuery.put(":valore" + contatoreValori, date);
                                        } else if (dataType.equals(AttribDatiSpecDataType.DATA)) {
                                            // Passa come parametro una data
                                            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                                            Date date = (Date) formatter.parse(fUnValore.getValore());
                                            valoriQuery.put(":valore" + contatoreValori, date);
                                        }
                                    } catch (ParseException ex) {
                                        log.error(ex.getMessage(), ex);
                                        // FIXME: In caso di parseException, come lo gestisco? Forse meglio lanciare un
                                        // 666
                                    }
                                }
                            }

                            queryRicerca.append(":valore").append(contatoreValori);
                            queryConta.append(":valore").append(contatoreValori);
                            contatoreValori++;
                        } else if (fNullo != null) {
                            // Nome colonna con operatore nullo
                            queryRicerca.append("info_dicom.").append(nomeColonnaCammellata).append(" IS NULL ");
                            queryConta.append("info_dicom.").append(nomeColonnaCammellata).append(" IS NULL ");
                        } else if (fDueValori != null) {
                            try {
                                // Controllo il tipo di dato dell'attributo e lo gestisco tra data, timestamp e stringa
                                // Controllo che la data sotto forma di stringa, sia nel formato corretto
                                if (dataType.equals(AttribDatiSpecDataType.DATETIME)) {
                                    // Ho passato come parametri dei timestamp
                                    DateFormat timeStampDf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
                                    Date date1 = timeStampDf.parse(fDueValori.getValore1());
                                    Date date2 = timeStampDf.parse(fDueValori.getValore2());
                                    // Nome colonna con 2 operatori
                                    queryRicerca.append("info_dicom.").append(nomeColonnaCammellata);
                                    queryRicerca.append(" BETWEEN ").append(":valore").append(contatoreValori)
                                            .append(" AND ");
                                    queryConta.append("info_dicom.").append(nomeColonnaCammellata);
                                    queryConta.append(" BETWEEN ").append(":valore").append(contatoreValori)
                                            .append(" AND ");
                                    valoriQuery.put(":valore" + contatoreValori, date1);
                                    contatoreValori++;
                                    queryRicerca.append(":valore").append(contatoreValori);
                                    queryConta.append(":valore").append(contatoreValori);
                                    valoriQuery.put(":valore" + contatoreValori, date2);
                                    contatoreValori++;
                                } else if (dataType.equals(AttribDatiSpecDataType.DATA)) {
                                    // Passa come parametro una data
                                    DateFormat formatter;
                                    Date date1;
                                    Date date2;
                                    formatter = new SimpleDateFormat("yyyy-MM-dd");
                                    date1 = (Date) formatter.parse(fDueValori.getValore1());
                                    date2 = DateFormatter.formatta2359(fDueValori.getValore2());

                                    // Nome colonna con 2 operatori
                                    queryRicerca.append("info_dicom.").append(nomeColonnaCammellata);
                                    queryRicerca.append(" BETWEEN ").append(":valore").append(contatoreValori)
                                            .append(" AND ");
                                    queryConta.append("info_dicom.").append(nomeColonnaCammellata);
                                    queryConta.append(" BETWEEN ").append(":valore").append(contatoreValori)
                                            .append(" AND ");
                                    valoriQuery.put(":valore" + contatoreValori, date1);
                                    contatoreValori++;
                                    queryRicerca.append(":valore").append(contatoreValori);
                                    queryConta.append(":valore").append(contatoreValori);
                                    valoriQuery.put(":valore" + contatoreValori, date2);
                                    contatoreValori++;
                                } else {
                                    // Considero i parametri stringhe
                                    queryRicerca.append("info_dicom.").append(nomeColonnaCammellata);
                                    queryRicerca.append(" BETWEEN ").append(":valore").append(contatoreValori)
                                            .append(" AND ");
                                    queryConta.append("info_dicom.").append(nomeColonnaCammellata);
                                    queryConta.append(" BETWEEN ").append(":valore").append(contatoreValori)
                                            .append(" AND ");
                                    valoriQuery.put(":valore" + contatoreValori, fDueValori.getValore1());
                                    contatoreValori++;
                                    queryRicerca.append(":valore").append(contatoreValori);
                                    queryConta.append(":valore").append(contatoreValori);
                                    valoriQuery.put(":valore" + contatoreValori, fDueValori.getValore2());
                                    contatoreValori++;
                                }
                            } catch (ParseException ex) {
                                log.error(ex.getMessage(), ex);
                                // FIXME: In caso di parseException, come lo gestisco? Forse meglio lanciare un 666
                            }
                        }

                        firstTime = false;
                    }
                    // Uscito dal ciclo for di filtri, chiudo la parentesi
                    queryRicerca.append(") ");
                    queryConta.append(") ");
                }

                // Aggiungo il frammento ORDER BY
                String clauseString = "ORDER BY ";

                // Se XMLDatiSpecOrder è definito (non mi interesso della queryConta)
                if (StringUtils.isNotBlank(
                        ricercaRestituzioniOggettiExt.getRicercaRestituzioniOggettiInput().getXmlDatiSpecOrder())) {

                    // Per ogni dato specifico di ordine
                    // for (int i = 0; i < ricercaDiarioExt.getDatiSpecOrderConNomeColonna().size(); i++) {
                    for (DatoSpecOrderConNomeColonna dato : ricercaRestituzioniOggettiExt
                            .getDatiSpecOrderConNomeColonna()) {
                        // Aggiungi la virgola
                        queryRicerca.append(clauseString);

                        // Aggiungo il frammento di query
                        String nomeColonnaCammellata = QueriesUtils.toCamelCase(dato.getColumnName());
                        queryRicerca.append("info_dicom.").append(nomeColonnaCammellata).append(" ");
                        queryRicerca.append(dato.getDatoSpecificoOrder().getTipoOrder());
                        clauseString = ", ";
                    }
                }
                queryRicerca.append(clauseString).append("sessione_recupero.dtApertura DESC");

                StringBuilder queryRicercaPart = new StringBuilder("");
                // Se XMLDatiSpecOutput è definito
                if (StringUtils.isNotBlank(
                        ricercaRestituzioniOggettiExt.getRicercaRestituzioniOggettiInput().getXmlDatiSpecOutput())) {
                    // Scorro la lista dei dati specifici (nomeDatoSpec, nomeColonnaSuDB) da definire come output della
                    // query
                    List<DatoSpecOutputConNomeColonna> listaDatiSpecOutputConNomeColonna = ricercaRestituzioniOggettiExt
                            .getDatiSpecOutputConNomeColonna();

                    for (DatoSpecOutputConNomeColonna dato : listaDatiSpecOutputConNomeColonna) {
                        queryRicercaPart.append(", ");
                        queryRicercaPart.append("info_dicom.")
                                .append(QueriesUtils.toCamelCase((String) dato.getColumnName()));
                    }
                    queryRicercaPart.append(" ");
                }
                // A questo punto ultimo la query di ricerca aggiungendo la parte iniziale di SELECT con i parametri da
                // restituire in output
                queryRicerca = standardSelectForQueryRicerca.append(queryRicercaPart).append(queryRicerca);
            } // End If Studio Dicom
            else {
                queryRicerca = new StringBuilder(
                        "FROM PigObject oggetto JOIN oggetto.pigSessioneRecups sessione_recupero ");
                queryRicerca = standardSelectForQueryRicerca.append(queryRicerca).append(whereCondition)
                        .append("ORDER BY sessione_recupero.cdKeyObject ASC, sessione_recupero.dtApertura DESC ");
                queryConta = new StringBuilder(
                        "SELECT COUNT(oggetto) FROM PigObject oggetto JOIN oggetto.pigSessioneRecups sessione_recupero ");
                queryConta = queryConta.append(whereCondition);
            }
        } // End If se nome tipo object è definito
        else {
            queryRicerca = new StringBuilder(
                    "FROM PigObject oggetto JOIN oggetto.pigSessioneRecups sessione_recupero ");
            queryRicerca = standardSelectForQueryRicerca.append(queryRicerca).append(whereCondition)
                    .append("ORDER BY sessione_recupero.cdKeyObject, sessione_recupero.dtApertura ");
            queryConta = new StringBuilder(
                    "SELECT COUNT(oggetto) FROM PigObject oggetto JOIN oggetto.pigSessioneRecups sessione_recupero ");
            queryConta = queryConta.append(whereCondition);
        }
        // Restituisco in un array di object le due stringhe e la LinkedHashMap coi valori
        objects[0] = queryConta.toString();
        objects[1] = queryRicerca.toString();
        objects[2] = valoriQuery;
        return objects;
    }
}
