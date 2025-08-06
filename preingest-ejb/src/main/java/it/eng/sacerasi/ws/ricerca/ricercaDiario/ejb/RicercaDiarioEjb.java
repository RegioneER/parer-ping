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

package it.eng.sacerasi.ws.ricerca.ricercaDiario.ejb;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import it.eng.sacerasi.common.Constants;
import it.eng.sacerasi.ws.dto.IRispostaWS;
import it.eng.sacerasi.ws.ricerca.dto.DatoSpecOutputConNomeColonna;
import it.eng.sacerasi.ws.ricerca.ricercaDiario.dto.ListaOggRicDiarioType;
import it.eng.sacerasi.ws.ricerca.ricercaDiario.dto.RicercaDiarioExt;
import it.eng.sacerasi.ws.ricerca.ricercaDiario.dto.RicercaDiarioInput;
import it.eng.sacerasi.ws.ricerca.ricercaDiario.dto.RicercaDiarioRisposta;
import it.eng.sacerasi.ws.ricerca.ricercaDiario.dto.RispostaWSRicercaDiario;
import it.eng.sacerasi.ws.ricerca.ricercaDiario.dto.WSDescRicercaDiario;
import it.eng.sacerasi.ws.ricerca.ricercaDiario.helper.RicercaDiarioCheckHelper;
import it.eng.sacerasi.ws.ricerca.ricercaDiario.util.RicercaDiarioQueriesBuilder;

/**
 *
 * @author Gilioli_P
 */
@Stateless(mappedName = "RicercaDiarioEjb")
@LocalBean
public class RicercaDiarioEjb {

    @EJB
    private RicercaDiarioExecuteQueries executeQueries;
    @EJB
    private RicercaDiarioCheckHelper ricercaDiarioCheckHelper;
    @EJB
    private RicercaDiarioParserEjb ricercaDiarioParserEjb;

    public RicercaDiarioRisposta ricercaDiario(String nmAmbiente, String nmVersatore,
	    String nmTipoObject, String cdKeyObject, Long idSessione, String tiStatoObject,
	    boolean flTutteSessioni, Integer niRecordInizio, Integer niRecordResultSet,
	    String xmlDatiSpecOutput, String xmlDatiSpecFiltri, String xmlDatiSpecOrder) {

	// Istanzio la risposta
	RispostaWSRicercaDiario rispostaWs = new RispostaWSRicercaDiario();
	rispostaWs.setRicercaDiarioRisposta(new RicercaDiarioRisposta());
	// Imposto l'esito della risposta di default OK
	rispostaWs.getRicercaDiarioRisposta().setCdEsito(Constants.EsitoServizio.OK);

	// Istanzio l'oggetto che contiene i parametri ricevuti in input
	RicercaDiarioInput rdInput = new RicercaDiarioInput(nmAmbiente, nmVersatore, nmTipoObject,
		cdKeyObject, idSessione, tiStatoObject, flTutteSessioni, niRecordInizio,
		niRecordResultSet, xmlDatiSpecOutput, xmlDatiSpecFiltri, xmlDatiSpecOrder);

	// Istanzio l'Ext con l'oggetto creato e setto i parametri descrizione e quelli in input
	RicercaDiarioExt rdExt = new RicercaDiarioExt();
	rdExt.setDescrizione(new WSDescRicercaDiario());
	rdExt.setRicercaDiarioInput(rdInput);

	// 1: Chiamo la classe RicercaDiarioCheck che gestisce i controlli di sessione e di oggetto
	// e popola la
	// rispostaWs
	ricercaDiarioCheckHelper.checkSessione(rdExt, rispostaWs);

	// 2: Chiamo la classe RicercaDiarioParser per "parsare" gli xml di input (filtri, output,
	// order), se questi
	// sono presenti
	if (rispostaWs.getSeverity() != IRispostaWS.SeverityEnum.ERROR
		&& rdExt.getRicercaDiarioInput().getXmlDatiSpecFiltri() != null
		&& !rdExt.getRicercaDiarioInput().getXmlDatiSpecFiltri().isEmpty()) {
	    // Chiamo il metodo per parsare l'XML dei filtri dati specifici
	    ricercaDiarioParserEjb.parseXMLDatiSpecFiltri(rdExt, rispostaWs);
	}

	if (rispostaWs.getSeverity() != IRispostaWS.SeverityEnum.ERROR
		&& rdExt.getRicercaDiarioInput().getXmlDatiSpecOutput() != null
		&& !rdExt.getRicercaDiarioInput().getXmlDatiSpecOutput().isEmpty()) {
	    // Chiamo il metodo per parsare l'XML dei dati specifici di output
	    ricercaDiarioParserEjb.parseXMLDatiSpecOutput(rdExt, rispostaWs);
	}

	if (rispostaWs.getSeverity() != IRispostaWS.SeverityEnum.ERROR
		&& rdExt.getRicercaDiarioInput().getXmlDatiSpecOrder() != null
		&& !rdExt.getRicercaDiarioInput().getXmlDatiSpecOrder().isEmpty()) {
	    // Chiamo il metodo per parsare l'XML dell'ordine dei dati specifici
	    ricercaDiarioParserEjb.parseXMLDatiSpecOrder(rdExt, rispostaWs);
	}

	// Setto altri parametri di output in risposta: sono quelli che avevo anche in input
	rispostaWs.getRicercaDiarioRisposta().setCdKeyObject(cdKeyObject);
	rispostaWs.getRicercaDiarioRisposta().setIdSessione(idSessione);
	rispostaWs.getRicercaDiarioRisposta().setTiStatoObject(tiStatoObject);
	rispostaWs.getRicercaDiarioRisposta().setFlTutteSessioni(flTutteSessioni);
	rispostaWs.getRicercaDiarioRisposta().setXmlDatiSpecOutput(xmlDatiSpecOutput);
	rispostaWs.getRicercaDiarioRisposta().setXmlDatiSpecFiltri(xmlDatiSpecFiltri);
	rispostaWs.getRicercaDiarioRisposta().setXmlDatiSpecOrder(xmlDatiSpecOrder);
	rispostaWs.getRicercaDiarioRisposta().setNiRecordInizio(niRecordInizio);
	rispostaWs.getRicercaDiarioRisposta().setNiRecordResultSet(niRecordResultSet);

	// Ricavo le queries di conteggio e ricerca
	RicercaDiarioQueriesBuilder queriesBuilder = new RicercaDiarioQueriesBuilder(rdExt);
	Object[] objects = new Object[3];
	if (rispostaWs.getSeverity() != IRispostaWS.SeverityEnum.ERROR) {
	    objects = queriesBuilder.buildQueries();
	}

	Long conta;
	ListaOggRicDiarioType listaOggetti = new ListaOggRicDiarioType();
	listaOggetti.setOggetto(new ArrayList<>());

	// 3: Eseguo le queries di conta e ricerca
	if (rispostaWs.getSeverity() != IRispostaWS.SeverityEnum.ERROR) {
	    String queryConta = (String) objects[0];
	    String queryRicerca = (String) objects[1];
	    LinkedHashMap<?, ?> valoriParametriQuery = (LinkedHashMap<?, ?>) objects[2];
	    List<DatoSpecOutputConNomeColonna> datiSpecOutputConNomeColonna = rdExt
		    .getDatiSpecOutputConNomeColonna();

	    conta = executeQueries.eseguiQueryConta(queryConta, valoriParametriQuery, rispostaWs);

	    int inizio = niRecordInizio == 0 ? niRecordInizio + 1 : niRecordInizio;
	    if (rispostaWs.getSeverity() != IRispostaWS.SeverityEnum.ERROR) {
		listaOggetti = executeQueries.eseguiQueryRicerca(rdExt,
			datiSpecOutputConNomeColonna, queryRicerca, valoriParametriQuery, inizio,
			niRecordResultSet, cdKeyObject, rispostaWs);
	    }
	    // Setto il numero totale di record selezionati con la ricerca
	    rispostaWs.getRicercaDiarioRisposta().setNiRecordTotale(conta.intValue());
	    /*
	     * Setto il numero di record restituiti dalla query di ricerca (dipende dai valori
	     * NiRecordInizio e NiRecordResultSet passati in input)
	     */
	    rispostaWs.getRicercaDiarioRisposta()
		    .setNiRecordOutput(listaOggetti != null ? listaOggetti.getOggetto().size() : 0);

	    if (rispostaWs.getSeverity() != IRispostaWS.SeverityEnum.ERROR) {
		// Setto la lista object risultato
		rispostaWs.getRicercaDiarioRisposta().setListaOggetti(listaOggetti);
	    }
	}
	// Ritorno la risposta
	return rispostaWs.getRicercaDiarioRisposta();
    }
}
