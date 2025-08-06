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

package it.eng.sacerasi.ws.ricerca.ricercaRestituzioniOggetti.ejb;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import it.eng.sacerasi.common.Constants;
import it.eng.sacerasi.ws.dto.IRispostaWS;
import it.eng.sacerasi.ws.ricerca.dto.DatoSpecOutputConNomeColonna;
import it.eng.sacerasi.ws.ricerca.ricercaRestituzioniOggetti.dto.ListaOggRicRestOggType;
import it.eng.sacerasi.ws.ricerca.ricercaRestituzioniOggetti.dto.OggettoRicRestOggType;
import it.eng.sacerasi.ws.ricerca.ricercaRestituzioniOggetti.dto.RicercaRestituzioniOggettiExt;
import it.eng.sacerasi.ws.ricerca.ricercaRestituzioniOggetti.dto.RicercaRestituzioniOggettiInput;
import it.eng.sacerasi.ws.ricerca.ricercaRestituzioniOggetti.dto.RicercaRestituzioniOggettiRisposta;
import it.eng.sacerasi.ws.ricerca.ricercaRestituzioniOggetti.dto.RispostaWSRicercaRestituzioniOggetti;
import it.eng.sacerasi.ws.ricerca.ricercaRestituzioniOggetti.dto.WSDescRicercaRestituzioniOggetti;
import it.eng.sacerasi.ws.ricerca.ricercaRestituzioniOggetti.helper.RicercaRestituzioniOggettiCheckHelper;
import it.eng.sacerasi.ws.ricerca.ricercaRestituzioniOggetti.util.RicercaRestituzioniOggettiQueriesBuilder;

/**
 *
 * @author Gilioli_P
 */
@Stateless(mappedName = "RicercaRestituzioniOggettiEjb")
@LocalBean
public class RicercaRestituzioniOggettiEjb {

    @EJB
    private RicercaRestituzioniOggettiExecuteQueries executeQueries;
    @EJB
    private RicercaRestituzioniOggettiCheckHelper ricercaRestituzioniOggettiCheckHelper;
    @EJB
    private RicercaRestituzioniOggettiParserEjb ricercaRestituzioniOggettiParser;

    public RicercaRestituzioniOggettiRisposta ricercaRestituzioniOggetti(String nmAmbiente,
	    String nmVersatore, String nmTipoObject, String cdKeyObject, String tiStatoSessione,
	    Date dtAperturaSessioneDa, Date dtAperturaSessioneA, Integer niRecordInizio,
	    Integer niRecordResultSet, String xmlDatiSpecOutput, String xmlDatiSpecFiltri,
	    String xmlDatiSpecOrder) {

	// Istanzio la risposta
	RispostaWSRicercaRestituzioniOggetti rispostaWs = new RispostaWSRicercaRestituzioniOggetti();
	rispostaWs.setricercaRestituzioniOggettiRisposta(new RicercaRestituzioniOggettiRisposta());
	// Imposto l'esito della risposta di default OK
	rispostaWs.getricercaRestituzioniOggettiRisposta().setCdEsito(Constants.EsitoServizio.OK);

	// Istanzio l'oggetto che contiene i parametri ricevuti in input
	RicercaRestituzioniOggettiInput rroInput = new RicercaRestituzioniOggettiInput(nmAmbiente,
		nmVersatore, nmTipoObject, cdKeyObject, tiStatoSessione, dtAperturaSessioneDa,
		dtAperturaSessioneA, niRecordInizio, niRecordResultSet, xmlDatiSpecOutput,
		xmlDatiSpecFiltri, xmlDatiSpecOrder);

	// Istanzio l'Ext con l'oggetto creato e setto i parametri descrizione e quelli in input
	RicercaRestituzioniOggettiExt rroExt = new RicercaRestituzioniOggettiExt();
	rroExt.setDescrizione(new WSDescRicercaRestituzioniOggetti());
	rroExt.setRicercaRestituzioniOggettiInput(rroInput);

	// 1: Chiamo la classe RicercaRestituzioniOggettiCheck che gestisce i controlli di sessione
	// e di oggetto e
	// popola la rispostaWs
	ricercaRestituzioniOggettiCheckHelper.checkSessione(rroExt, rispostaWs);

	// 2: Chiamo la classe RicercaRestituzioniOggettiParser per "parsare" gli xml di input
	// (filtri, output, order),
	// se questi sono presenti
	if (rispostaWs.getSeverity() != IRispostaWS.SeverityEnum.ERROR
		&& rroExt.getRicercaRestituzioniOggettiInput().getXmlDatiSpecFiltri() != null
		&& !rroExt.getRicercaRestituzioniOggettiInput().getXmlDatiSpecFiltri().isEmpty()) {
	    // Chiamo il metodo per parsare l'XML dei filtri dati specifici
	    ricercaRestituzioniOggettiParser.parseXMLDatiSpecFiltri(rroExt, rispostaWs);
	}

	if (rispostaWs.getSeverity() != IRispostaWS.SeverityEnum.ERROR
		&& rroExt.getRicercaRestituzioniOggettiInput().getXmlDatiSpecOutput() != null
		&& !rroExt.getRicercaRestituzioniOggettiInput().getXmlDatiSpecOutput().isEmpty()) {
	    // Chiamo il metodo per parsare l'XML dei dati specifici di output
	    ricercaRestituzioniOggettiParser.parseXMLDatiSpecOutput(rroExt, rispostaWs);
	}

	if (rispostaWs.getSeverity() != IRispostaWS.SeverityEnum.ERROR
		&& rroExt.getRicercaRestituzioniOggettiInput().getXmlDatiSpecOrder() != null
		&& !rroExt.getRicercaRestituzioniOggettiInput().getXmlDatiSpecOrder().isEmpty()) {
	    // Chiamo il metodo per parsare l'XML dell'ordine dei dati specifici
	    ricercaRestituzioniOggettiParser.parseXMLDatiSpecOrder(rroExt, rispostaWs);
	}

	// Setto altri parametri di output in risposta: sono quelli che avevo anche in input
	rispostaWs.getricercaRestituzioniOggettiRisposta().setCdKeyObject(cdKeyObject);
	rispostaWs.getricercaRestituzioniOggettiRisposta().setTiStatoSessione(tiStatoSessione);
	rispostaWs.getricercaRestituzioniOggettiRisposta().setNiRecordInizio(niRecordInizio);
	rispostaWs.getricercaRestituzioniOggettiRisposta().setNiRecordResultSet(niRecordResultSet);

	// Ricavo le queries di conteggio e ricerca
	RicercaRestituzioniOggettiQueriesBuilder queriesBuilder = new RicercaRestituzioniOggettiQueriesBuilder(
		rroExt);
	Object[] objects = new Object[3];
	if (rispostaWs.getSeverity() != IRispostaWS.SeverityEnum.ERROR) {
	    objects = queriesBuilder.buildQueries();
	}

	Long conta;
	ListaOggRicRestOggType listaOggetti = new ListaOggRicRestOggType();
	listaOggetti.setOggetto(new ArrayList<OggettoRicRestOggType>());

	// 3: Eseguo le queries di conta e ricerca
	if (rispostaWs.getSeverity() != IRispostaWS.SeverityEnum.ERROR) {
	    String queryConta = (String) objects[0];
	    String queryRicerca = (String) objects[1];
	    LinkedHashMap valoriParametriQuery = (LinkedHashMap) objects[2];
	    List<DatoSpecOutputConNomeColonna> datiSpecOutputConNomeColonna = rroExt
		    .getDatiSpecOutputConNomeColonna();

	    conta = executeQueries.eseguiQueryConta(queryConta, valoriParametriQuery, rispostaWs);

	    int inizio = niRecordInizio == 0 ? niRecordInizio + 1 : niRecordInizio;
	    if (rispostaWs.getSeverity() != IRispostaWS.SeverityEnum.ERROR) {
		listaOggetti = executeQueries.eseguiQueryRicerca(rroExt,
			datiSpecOutputConNomeColonna, queryRicerca, valoriParametriQuery, inizio,
			niRecordResultSet, cdKeyObject, rispostaWs);
	    }
	    // Setto il numero totale di record selezionati con la ricerca
	    rispostaWs.getricercaRestituzioniOggettiRisposta().setNiRecordTotale(conta.intValue());
	    /*
	     * Setto il numero di record restituiti dalla query di ricerca (dipende dai valori
	     * NiRecordInizio e NiRecordResultSet passati in input)
	     */
	    rispostaWs.getricercaRestituzioniOggettiRisposta()
		    .setNiRecordOutput(listaOggetti != null ? listaOggetti.getOggetto().size() : 0);

	    if (rispostaWs.getSeverity() != IRispostaWS.SeverityEnum.ERROR) {
		// Setto la lista object risultato
		rispostaWs.getricercaRestituzioniOggettiRisposta().setListaOggetti(listaOggetti);
	    }
	}
	// Ritorno la risposta
	return rispostaWs.getricercaRestituzioniOggettiRisposta();
    }
}
