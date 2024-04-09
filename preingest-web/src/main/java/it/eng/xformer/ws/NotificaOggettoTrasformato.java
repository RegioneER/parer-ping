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

package it.eng.xformer.ws;

import javax.ejb.EJB;
import javax.jws.HandlerChain;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import it.eng.xformer.job.ejb.EseguiTrasformazione;

/**
 * WS di notifica oggetto trasformato. Scopo di questo ws è aggiornare lo stato del <em>PigObject</em> associato alla
 * trasformazione.
 *
 * @author Cappelli_F
 * @author Snidero_L
 */
@WebService(serviceName = "NotificaOggettoTrasformato")
@HandlerChain(file = "/ws_handler.xml")
public class NotificaOggettoTrasformato {

    @EJB
    private EseguiTrasformazione eseguiTrasformazione;

    /**
     * Metodo invocato da <em>parer-kettle</em> al termine di una trasformazione. L'handler chain è stato implementato
     * in questo progetto (non nel framework) a causa delle sue specificità. In questo caso non è necessario effettuare
     * alcun controllo di sicurezza ulteriore rispetto a quelli effettuati nell'handler chain associato.
     *
     * @param idOggetto
     *            id dell'oggetto che ha subito la trasformazione.
     * @param numeroErrori
     *            numero degli eventuali errori della trasformazione.
     * @param report
     *            parametro opzionale sia in caso di trasformazione eseguita con successo sia in caso di errore.
     * 
     * @return risposta a <em>kettle-server</em>.
     */
    @WebMethod(operationName = "notificaOggettoTrasformato")
    public NotificaOggettoTrasformatoRisposta notificaOggettoTrasformato(@WebParam(name = "idOggetto") long idOggetto,
            @WebParam(name = "numeroErrori") int numeroErrori, @WebParam(name = "report") String report) {
        NotificaOggettoTrasformatoRisposta risposta = new NotificaOggettoTrasformatoRisposta();
        risposta.setEsito("OK");

        try {
            eseguiTrasformazione.notificaOggettoTrasformato(idOggetto, numeroErrori, report);
        } catch (Exception ex) {
            // il metodo notificaOggettoTrasformato è asincrono e restituisce void, nessuna eccezione sarà mai catturata
            // qui
            // risposta.setEsito("KO");
            // risposta.setDettagli(ex.getMessage());
        }

        return risposta;
    }
}
