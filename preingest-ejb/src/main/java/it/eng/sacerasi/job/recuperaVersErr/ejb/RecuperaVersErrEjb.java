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

package it.eng.sacerasi.job.recuperaVersErr.ejb;

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
 * @author Gilioli_P
 */
@Stateless(mappedName = "RecuperaVersErrEjb")
@LocalBean
@Interceptors({ it.eng.sacerasi.aop.TransactionInterceptor.class })
public class RecuperaVersErrEjb {

    Logger log = LoggerFactory.getLogger(RecuperaVersErrEjb.class);
    @EJB
    private JobLogger jobLoggerEjb;
    @EJB
    private JobHelper helper;
    @EJB
    private RecuperaVersErrHelper recHelper;

    public void recuperaVersErr() throws ParerInternalError, NamingException {
        // Recupero la lista di id dei versatori
        List<Long> versatori = helper.getListaVersatori();
        // Recupero, per ogni versatore, l’insieme degli oggetti con stato = CHIUSO_ERR_RECUPERABILE o
        // CHIUSO_ERR_VERS con settato l’indicatore che segnala che l’oggetto è da recuperare,
        // per i quali non siano presenti unità documentarie con stato = IN_CODA_VERS
        // e non siano presenti unità documentarie con stato = DA_VERSARE
        List<PigObject> tmpOggetti = recHelper.getListaObjects(versatori,
                Constants.StatoOggetto.CHIUSO_ERR_RECUPERABILE.name(), Constants.StatoOggetto.CHIUSO_ERR_VERS.name());
        log.info("JOB " + Constants.NomiJob.RECUPERA_VERS_ERR.name() + " - ottenuti " + tmpOggetti.size()
                + " oggetti con stato CHIUSO_ERR_RECUPERABILE "
                + "o CHIUSO_ERR_VERS con settato l’indicatore che segnala che l’oggetto e’ da recuperare, senza UD con stato IN_CODA_VERS "
                + "e senza UD con stato DA_VERSARE");
        if (!tmpOggetti.isEmpty()) {
            // Se la lista non è vuota, per ogni oggetto cambio lo stato in IN_ATTESA_VERS
            // Creo una nuova sessione e la assegno all'oggetto
            for (PigObject obj : tmpOggetti) {
                recHelper.elaboraOggetto(obj);
                // Registra una UD della sessione per ogni UD dell'oggetto con stato = VERSATA_TIMEOUT
                // recHelper.registraUD(obj.getPigUnitaDocObjects());
            }
        }
        // Quando ho elaborato l'ultimo oggetto, scrivo sul log la fine esecuzione del job
        jobLoggerEjb.writeAtomicLog(Constants.NomiJob.RECUPERA_VERS_ERR, Constants.TipiRegLogJob.FINE_SCHEDULAZIONE,
                null);
    }
}
