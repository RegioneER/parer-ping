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

package it.eng.parer.jboss.timers.helper;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;

import it.eng.parer.jboss.timer.common.JbossJobTimer;
import it.eng.parer.sacerlog.job.SacerLogAllineamentoTimer;
import it.eng.parer.sacerlog.job.SacerLogTimer;
import it.eng.sacerasi.common.Constants;
import it.eng.sacerasi.job.timer.AllineamentoOrganizzazioniTimer;
import it.eng.sacerasi.job.timer.InvioSUTimer;
import it.eng.sacerasi.job.timer.InvioSismaTimer;
import it.eng.sacerasi.job.timer.PreparaXmlTimer;
import it.eng.sacerasi.job.timer.ProducerCodaVerificaHTimer;
import it.eng.sacerasi.job.timer.ProducerCodaVersamentoTimer;
import it.eng.sacerasi.job.timer.RecuperaErroriInCodaTimer;
import it.eng.sacerasi.job.timer.RecuperaVersErrTimer;
import it.eng.sacerasi.job.timer.RecuperoSacerTimer;
import it.eng.xformer.job.timer.EseguiTrasformazioneTimer;
import it.eng.xformer.job.timer.InviaOggettiGeneratiAPingTimer;

/**
 * Singleton utilizzato per censire tutti i timer di Sacer.
 *
 * @author Snidero_L
 */
@Singleton
public class TimerRepository {

    @EJB
    private PreparaXmlTimer preparaXml;

    @EJB
    private ProducerCodaVersamentoTimer producerCodaVersamento;

    @EJB
    private AllineamentoOrganizzazioniTimer allineamentoOrganizzazioni;

    @EJB
    private ProducerCodaVerificaHTimer producerCodaVerificaH;

    @EJB
    private RecuperaErroriInCodaTimer recuperaErroriInCoda;

    @EJB
    private RecuperaVersErrTimer recuperaVersErr;

    @EJB
    private RecuperoSacerTimer recuperoSacer;

    @EJB
    private InvioSUTimer invioSU;

    @EJB
    private InvioSismaTimer invioSisma;

    @EJB(mappedName = "java:app/sacerlog-ejb/SacerLogTimer")
    private SacerLogTimer sacerLogTimer;
    @EJB(mappedName = "java:app/sacerlog-ejb/SacerLogAllineamentoTimer")
    private SacerLogAllineamentoTimer sacerLogAllineamentoTimer;

    // xFormer job timers
    @EJB
    private EseguiTrasformazioneTimer eseguiTrasformazioneTimer;
    @EJB
    private InviaOggettiGeneratiAPingTimer inviaOggettiGeneratiAPingTimer;

    private Map<String, JbossJobTimer> map;

    @PostConstruct
    public void initialize() {
        map = new HashMap<>();
        map.put(Constants.NomiJob.PREPARA_XML.name(), preparaXml);
        map.put(Constants.NomiJob.PRODUCER_CODA_VERIFICA_H.name(), producerCodaVerificaH);
        map.put(Constants.NomiJob.PRODUCER_CODA_VERS.name(), producerCodaVersamento);
        map.put(Constants.NomiJob.RECUPERA_ERRORI_IN_CODA.name(), recuperaErroriInCoda);
        map.put(Constants.NomiJob.RECUPERA_VERS_ERR.name(), recuperaVersErr);
        map.put(Constants.NomiJob.RECUPERO_SACER.name(), recuperoSacer);
        map.put(Constants.NomiJob.ALLINEAMENTO_ORGANIZZAZIONI.name(), allineamentoOrganizzazioni);
        map.put(it.eng.parer.sacerlog.job.Constants.NomiJob.INIZIALIZZAZIONE_LOG.name(), sacerLogTimer);
        map.put(it.eng.parer.sacerlog.job.Constants.NomiJob.ALLINEAMENTO_LOG.name(), sacerLogAllineamentoTimer);
        map.put(Constants.NomiJob.INVIO_STRUMENTI_URBANISTICI.name(), invioSU);
        map.put(Constants.NomiJob.INVIO_SISMA.name(), invioSisma);

        // xFormer job timers
        map.put(Constants.NomiJob.ESEGUI_TRASFORMAZIONE.name(), eseguiTrasformazioneTimer);
        map.put(Constants.NomiJob.INVIA_OGGETTI_GENERATI_A_PING.name(), inviaOggettiGeneratiAPingTimer);
    }

    /**
     * Ottieni i nomi di tutti i timer configurati su Sacer.
     *
     * @return insieme dei nomi di tutti i timer.
     */
    @Lock(LockType.READ)
    public Set<String> getConfiguredTimersName() {
        return map.keySet();
    }

    /**
     * Ottieni l'implementazione del timer definito.
     *
     * @param jobName
     *            nome del job
     * 
     * @return istanza del timer oppure null
     */
    @Lock(LockType.READ)
    public JbossJobTimer getConfiguredTimer(String jobName) {
        return map.get(jobName);
    }
}
