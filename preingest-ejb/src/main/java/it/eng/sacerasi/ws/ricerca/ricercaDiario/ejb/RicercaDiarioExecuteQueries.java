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

package it.eng.sacerasi.ws.ricerca.ricercaDiario.ejb;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.sacerasi.common.Constants;
import it.eng.sacerasi.entity.PigSessioneRecup;
import it.eng.sacerasi.entity.PigUnitaDocObject;
import it.eng.sacerasi.messages.MessaggiWSBundle;
import it.eng.sacerasi.ws.dto.IRispostaWS;
import it.eng.sacerasi.ws.ricerca.dto.DatoSpecOutputConNomeColonna;
import it.eng.sacerasi.ws.ricerca.ricercaDiario.dto.ListaOggRicDiarioType;
import it.eng.sacerasi.ws.ricerca.ricercaDiario.dto.OggettoRicDiarioType;
import it.eng.sacerasi.ws.ricerca.ricercaDiario.dto.RicercaDiarioExt;
import it.eng.sacerasi.ws.ricerca.ricercaDiario.dto.RispostaWSRicercaDiario;
import it.eng.sacerasi.ws.xml.datiSpecResult.ListaValoriDatiSpecificiType;
import it.eng.sacerasi.ws.xml.datiSpecResult.ValoreDatoSpecificoType;

/**
 *
 * @author Gilioli_P
 */
@Stateless(mappedName = "RicercaDiarioExecuteQueries")
@LocalBean
public class RicercaDiarioExecuteQueries {

    private static final Logger log = LoggerFactory.getLogger(RicercaDiarioExecuteQueries.class);
    @PersistenceContext(unitName = "SacerAsiJPA")
    private EntityManager entityManager;

    @EJB
    private RicercaDiarioParserEjb ricercaDiarioParser;

    /**
     * Calcola il risultato della queryConta passata in input
     *
     * @param queryConta
     *            la query conta
     * @param valoriParametriQuery
     *            i valori dei parametri da passare alla query
     * @param rispostaWs
     *            risposta ws
     *
     * @return conta, il risultato della query
     */
    public Long eseguiQueryConta(String queryConta, Map<?, ?> valoriParametriQuery,
            RispostaWSRicercaDiario rispostaWs) {
        Long conta = 0L;
        try {
            Query query = entityManager.createQuery(queryConta);
            if (valoriParametriQuery != null) {
                Set<?> keysValori = valoriParametriQuery.keySet();
                Iterator<?> keyIter = keysValori.iterator();
                while (keyIter.hasNext()) {
                    String key = (String) keyIter.next();
                    Object valore = valoriParametriQuery.get(key);
                    query.setParameter((key).substring(1), valore);
                }
            }
            conta = (Long) query.getSingleResult();
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            rispostaWs.setSeverity(IRispostaWS.SeverityEnum.ERROR);
            rispostaWs.setErrorCode(MessaggiWSBundle.ERR_666);
            rispostaWs.setErrorMessage(
                    MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666, "Errore nell'esecuzione della query conta"));
            rispostaWs.getRicercaDiarioRisposta().setCdEsito(Constants.EsitoServizio.KO);
            rispostaWs.getRicercaDiarioRisposta().setCdErr(MessaggiWSBundle.ERR_666);
            rispostaWs.getRicercaDiarioRisposta().setDsErr(
                    MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666, "Errore nell'esecuzione della query conta"));
        }
        return conta;
    }

    /**
     * Calcola il risultato della queryRicerca passata in input
     *
     * @param rdExt
     *            java bean
     * @param datiSpecOutputConNomeColonna
     *            lista dati specifici per colonna di tipo {@link DatoSpecOutputConNomeColonna}
     * @param queryRicerca
     *            query ricerca
     * @param valoriParametriQuery
     *            i valori dei parametri da passare alla query
     * @param niRecordInizio
     *            numero record
     * @param niRecordResultSet
     *            numero record risultato
     * @param cdKeyObject
     *            numreo oggetto
     * @param rispostaWs
     *            risposta ws
     *
     * @return ListaOggRicDiarioType, l'oggetto contenente la lista di oggetti, risultato della query ricerca
     */
    @SuppressWarnings("unchecked")
    public ListaOggRicDiarioType eseguiQueryRicerca(RicercaDiarioExt rdExt,
            List<DatoSpecOutputConNomeColonna> datiSpecOutputConNomeColonna, String queryRicerca,
            Map<?, ?> valoriParametriQuery, Integer niRecordInizio, Integer niRecordResultSet, String cdKeyObject,
            RispostaWSRicercaDiario rispostaWs) {
        List<OggettoRicDiarioType> listaObject = new ArrayList<>();
        ListaOggRicDiarioType listaOggType = new ListaOggRicDiarioType();
        listaOggType.setOggetto(listaObject);
        try {
            Query query = entityManager.createQuery(queryRicerca);
            if (valoriParametriQuery != null) {
                Set<?> keys = valoriParametriQuery.keySet();
                Iterator<?> keyIter = keys.iterator();
                while (keyIter.hasNext()) {
                    String key = (String) keyIter.next();
                    Object valore = valoriParametriQuery.get(key);
                    query.setParameter((key).substring(1), valore);
                }
            }
            // Ottengo i risultati della query a partire da
            query.setFirstResult(niRecordInizio - 1);
            // E prendo niRecordResultSet risultati
            query.setMaxResults(niRecordResultSet);
            // Eseguo la query, ottengo una lista di oggetti (i campi della query)
            List<Object[]> resultSet = query.getResultList();

            // Ora, costruisco l'output, facendo attenzione all'ordine degli elementi
            for (Object[] o : resultSet) {
                int indice = 0;
                // Setto tutti i campi di default
                OggettoRicDiarioType obj = new OggettoRicDiarioType();
                obj.setIdObject((Long) o[indice++]);
                obj.setCdKeyObject((String) o[indice++]);
                obj.setTiStatoSessione((String) o[indice++]);
                obj.setDtAperturaSessione((Date) o[indice++]);
                obj.setDtChiusuraSessione((Date) o[indice++]);
                obj.setCdErr((String) o[indice++]);
                obj.setDsErr((String) o[indice++]);
                obj.setIdSessione((Long) o[indice++]);
                obj.setTiStatoObject((String) o[indice++]);
                obj.setFlForzaAccettazione(((String) o[indice++]).equals("1"));
                obj.setDlMotivoForzaAccettazione((String) o[indice++]);
                obj.setFlForzaWarning(((String) o[indice++]).equals("1"));
                obj.setDlMotivoChiusoWarning((String) o[indice++]);

                /*
                 * Ora devo settare i campi in più che ho chiesto in output: TiStatoSessioneRecup,
                 * DtAperturaSessioneRecup, ChiaveUnitaDoc e l'XML risultato, a seconda che vengano rispettate le
                 * condizioni
                 */

                /*
                 * 1) Cominciamo con i dati specifici segnalati come dati da mettere in output Ricavo nuovamente la
                 * lista dei campi in più che formeranno l'XML risultato e poi li andrà ad inserire in una lista valori
                 * dati specifici (regolata dall'XSD dell'XMLDatiSpecResult)
                 */
                if (datiSpecOutputConNomeColonna != null) {
                    ListaValoriDatiSpecificiType listaDS = new ListaValoriDatiSpecificiType();
                    int contatore = 0;
                    for (DatoSpecOutputConNomeColonna dato : datiSpecOutputConNomeColonna) {
                        ValoreDatoSpecificoType valoreDS = new ValoreDatoSpecificoType();
                        valoreDS.setDatoSpecifico(dato.getDatoSpecificoOutput());

                        if (o[indice + contatore] instanceof Date) {
                            try {
                                DateFormat formatter;
                                Date date = (Date) o[indice + contatore];
                                formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
                                String s = formatter.format(date);
                                valoreDS.setValore(s);
                            } catch (Exception e) {
                                log.error("Errore nella formattazione del campo data in output", e);
                            }
                        } else if (o[indice + contatore] instanceof BigDecimal) {
                            valoreDS.setValore(((BigDecimal) o[indice + contatore]).toString());
                        } else {
                            valoreDS.setValore((String) o[indice + contatore]);
                        }
                        contatore++;
                        listaDS.getValoreDatoSpecifico().add(valoreDS);
                    }

                    /*
                     * Ora marshallo la lista valori dati specifici per ottenere la stringa xml che andrà a settare nel
                     * campo dell'XML risultato
                     */
                    obj.setXmlDatiSpecResult(ricercaDiarioParser.parseDatiSpecResult(listaDS, rdExt, rispostaWs));

                    /*
                     * 2) Comincio controllando se il parametro in input CdKeyObject è stato valorizzato
                     */
                    if (cdKeyObject != null) {
                        PigSessioneRecup ses = getSessioneRecupRecente(obj.getIdObject());
                        if (ses != null) {
                            if (ses.getTiStato() != null) {
                                obj.setTiStatoSessioneRecup(ses.getTiStato());
                            }
                            if (ses.getDtApertura() != null) {
                                obj.setDtAperturaSessioneRecup(ses.getDtApertura());
                            }
                        }
                    }

                    /*
                     * 3) Controllo il numero di unità documentarie definite per l'oggetto
                     */
                    long numUD = getNumUDDefinite(obj.getIdObject());
                    if (numUD == 0) {
                        obj.setChiaveUnitaDoc(null);
                    } else if (numUD == 1) {
                        PigUnitaDocObject udObj = getPigUnitaDocObject(obj.getIdObject());
                        obj.setChiaveUnitaDoc(udObj.getCdRegistroUnitaDocSacer() + "-" + udObj.getAaUnitaDocSacer()
                                + "-" + udObj.getCdKeyUnitaDocSacer());
                    } else {
                        obj.setChiaveUnitaDoc("Il numero di unità documentarie generate dall'oggetto � > 1");
                    }

                }
                // Metto il record nella lista
                listaObject.add(obj);
            }
            // Metto la lista nel suo bean
            listaOggType.setOggetto(listaObject);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            rispostaWs.setSeverity(IRispostaWS.SeverityEnum.ERROR);
            rispostaWs.setErrorCode(MessaggiWSBundle.ERR_666);
            rispostaWs.setErrorMessage(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
                    "Errore nell'esecuzione della query di ricerca"));
            rispostaWs.getRicercaDiarioRisposta().setCdEsito(Constants.EsitoServizio.KO);
            rispostaWs.getRicercaDiarioRisposta().setCdErr(MessaggiWSBundle.ERR_666);
            rispostaWs.getRicercaDiarioRisposta().setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
                    "Errore nell'esecuzione della query di ricerca"));
        }
        return listaOggType;
    }

    @SuppressWarnings("unchecked")
    private PigSessioneRecup getSessioneRecupRecente(Long idObject) {
        String queryStr = "SELECT ses FROM PigSessioneRecup ses WHERE ses.pigObject.idObject = :idObject "
                + "ORDER BY ses.dtApertura DESC ";
        Query query = entityManager.createQuery(queryStr);
        query.setParameter("idObject", idObject);
        List<PigSessioneRecup> sexList = query.getResultList();
        PigSessioneRecup sesRec = new PigSessioneRecup();
        if (!sexList.isEmpty()) {
            sesRec = sexList.get(0);
        }
        return sesRec;
    }

    private long getNumUDDefinite(Long idObject) {
        String queryStr = "SELECT COUNT(ud) FROM PigUnitaDocObject ud WHERE ud.pigObject.idObject = :idObject ";
        Query query = entityManager.createQuery(queryStr);
        query.setParameter("idObject", idObject);
        return (Long) query.getSingleResult();
    }

    private PigUnitaDocObject getPigUnitaDocObject(Long idObject) {
        String queryStr = "SELECT ud FROM PigUnitaDocObject ud WHERE ud.pigObject.idObject = :idObject ";
        Query query = entityManager.createQuery(queryStr);
        query.setParameter("idObject", idObject);
        return (PigUnitaDocObject) query.getResultList().get(0);
    }
}
