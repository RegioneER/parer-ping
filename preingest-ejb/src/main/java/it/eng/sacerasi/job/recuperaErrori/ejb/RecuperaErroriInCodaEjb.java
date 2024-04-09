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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.eng.sacerasi.job.recuperaErrori.ejb;

import it.eng.sacerasi.common.Constants;
import it.eng.sacerasi.entity.PigObject;
import it.eng.sacerasi.exception.ParerInternalError;
import it.eng.sacerasi.job.ejb.JobHelper;
import it.eng.sacerasi.job.ejb.JobLogger;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.naming.NamingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Bonora_L
 */
@Stateless(mappedName = "RecuperaErroriInCodaEjb")
@LocalBean
@Interceptors({ it.eng.sacerasi.aop.TransactionInterceptor.class })
public class RecuperaErroriInCodaEjb {

    Logger log = LoggerFactory.getLogger(RecuperaErroriInCodaEjb.class);
    @EJB
    private JobLogger jobLoggerEjb;
    @EJB
    private JobHelper helper;
    @EJB
    private RecuperoErroriInCodaHelper recHelper;

    public void recuperaErrori() throws ParerInternalError, NamingException {
        // Recupero la lista di id dei versatori
        List<Long> versatori = helper.getListaVersatori();
        // In base a quella lista, recupero gli oggetti con stato CHIUSO_ERR_CODA per i
        // quali non siano presenti unità documentarie con stato = IN_CODA_VERS
        List<PigObject> tmpOggetti = recHelper.getListaObjects(versatori, Constants.StatoOggetto.CHIUSO_ERR_CODA);
        log.info("JOB RecuperaErroriInCoda - ottenuti " + tmpOggetti.size()
                + " oggetti con stato CHIUSO_ERR_CODA senza UD con stato IN_CODA_VERS");
        if (!tmpOggetti.isEmpty()) {
            // Se la lista non è vuota, per ogni oggetto cambio lo stato in IN_ATTESA_VERS
            // Creo una nuova sessione e la assegno all'oggetto
            for (PigObject obj : tmpOggetti) {
                recHelper.elaboraOggetto(obj);
            }
        }
        // Quando ho elaborato l'ultimo oggetto, scrivo sul log la fine esecuzione del job
        jobLoggerEjb.writeAtomicLog(Constants.NomiJob.RECUPERA_ERRORI_IN_CODA,
                Constants.TipiRegLogJob.FINE_SCHEDULAZIONE, null);
    }
}
