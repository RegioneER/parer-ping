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

/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */
package it.eng.sacerasi.ws.ricerca.ricercaRecuperati.ejb;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import it.eng.sacerasi.common.Constants;
import it.eng.sacerasi.ws.dto.IRispostaWS;
import it.eng.sacerasi.ws.ricerca.ricercaRecuperati.dto.ListaOggRicRecuperatiType;
import it.eng.sacerasi.ws.ricerca.ricercaRecuperati.dto.RicercaRecuperatiExt;
import it.eng.sacerasi.ws.ricerca.ricercaRecuperati.dto.RicercaRecuperatiInput;
import it.eng.sacerasi.ws.ricerca.ricercaRecuperati.dto.RicercaRecuperatiRisposta;
import it.eng.sacerasi.ws.ricerca.ricercaRecuperati.dto.RispostaWSRicercaRecuperati;
import it.eng.sacerasi.ws.ricerca.ricercaRecuperati.helper.RicercaRecuperatiCheckHelper;

/**
 *
 * @author Filippini_M
 */

@Stateless(mappedName = "RicercaRecuperatiEjb")
@LocalBean
public class RicercaRecuperatiEjb {

    @EJB
    private RicercaRecuperatiExecuteQueries executeQueries;
    @EJB
    private RicercaRecuperatiCheckHelper ricercaRecuperatiCheckHelper;

    public RicercaRecuperatiRisposta ricercaRecuperati(String nmAmbiente, String nmVersatore) {

	// Istanzio la risposta
	RispostaWSRicercaRecuperati rispostaWs = new RispostaWSRicercaRecuperati();
	rispostaWs.setRicercaRecuperatiRisposta(new RicercaRecuperatiRisposta());
	// Imposto l'esito della risposta di default OK
	rispostaWs.getRicercaRecuperatiRisposta().setCdEsito(Constants.EsitoServizio.OK);

	// Istanzio l'oggetto che contiene i parametri ricevuti in input
	RicercaRecuperatiInput rdInput = new RicercaRecuperatiInput(nmAmbiente, nmVersatore);

	// Istanzio l'Ext con l'oggetto creato e setto i parametri descrizione e quelli in input
	RicercaRecuperatiExt ricercaRecuperatiExt = new RicercaRecuperatiExt();
	ricercaRecuperatiExt.setRicercaRecuperatiInput(rdInput);

	// 1: Chiamo la classe RicercaDiarioCheck che gestisce i controlli di sessione e di oggetto
	// e popola la
	// rispostaWs
	ricercaRecuperatiCheckHelper.checkSessione(ricercaRecuperatiExt, rispostaWs);

	// non ho bisogno di aggiungere altri dati alla risposta proveniente dall'input, sono tutti
	// già inizializzati dal check

	// Lista da associare al risultato
	ListaOggRicRecuperatiType listaOggType = new ListaOggRicRecuperatiType();

	if (rispostaWs.getSeverity() != IRispostaWS.SeverityEnum.ERROR) {
	    listaOggType = executeQueries.querySearch(nmAmbiente, nmVersatore);
	}

	rispostaWs.getRicercaRecuperatiRisposta().setNmAmbiente(nmAmbiente);
	rispostaWs.getRicercaRecuperatiRisposta().setNmVersatore(nmVersatore);
	rispostaWs.getRicercaRecuperatiRisposta().setListaOggetti(listaOggType);

	// Ritorno la risposta
	return rispostaWs.getRicercaRecuperatiRisposta();
    }

}
