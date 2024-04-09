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
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.eng.sacerasi.ws.rest.monitoraggio.ejb;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.jms.JMSException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.sacerasi.job.coda.dto.InfoCoda;
import it.eng.sacerasi.job.coda.dto.InfoCodaExt;
import it.eng.sacerasi.test.MonitorCoda;
import it.eng.sacerasi.web.util.Constants;
import it.eng.sacerasi.ws.rest.monitoraggio.dto.rmonitor.MonitorAltro;
import it.eng.sacerasi.ws.util.Costanti;

/**
 *
 * @author fioravanti_f
 */
@Stateless(mappedName = "AltriStatusMonitor")
@LocalBean
public class AltriStatusMonitor {

    private enum MonitorAltri {
        STATO_CONNESSIONE_ORCL, PRESENZA_ERRORI_CODA_JMS
    }

    private enum MonitorSondeGenEsiti {
        OK, ERROR, WARNING
    }

    private static final Logger log = LoggerFactory.getLogger(AltriStatusMonitor.class);

    private static final String ERROR_SEPARATOR = ", ";
    private static final String JMS_SELECTOR = Costanti.JMSMsgProperties.MSG_K_APP + " = '" + Costanti.PING + "'";

    @EJB
    ControlliMonitor controlliMonitor;
    @EJB
    MonitorCoda monitorCoda;

    public void calcolaStatoDatabase(List<MonitorAltro> listaMon) {
        MonitorAltro tmpAltro = new MonitorAltro();
        tmpAltro.setNome(MonitorAltri.STATO_CONNESSIONE_ORCL.name());
        if (controlliMonitor.controllaStatoDbOracle()) {
            tmpAltro.setStato(MonitorSondeGenEsiti.OK.name());
        } else {
            tmpAltro.setStato(MonitorSondeGenEsiti.ERROR.name() + "|Il database Oracle non è raggiungibile ");
            log.error("Errore: Il database Oracle non è raggiungibile");
        }
        listaMon.add(tmpAltro);
    }

    public void calcolaStatoCodaMorta(List<MonitorAltro> listaMon) {
        MonitorAltro tmpAltro = new MonitorAltro();
        tmpAltro.setNome(MonitorAltri.PRESENZA_ERRORI_CODA_JMS.name());
        try {
            List<InfoCoda> infoCodas = monitorCoda.retrieveGenericMsgInQueue(Constants.NomeCoda.dmqQueue.name(),
                    JMS_SELECTOR);
            this.elabJmsMessage(tmpAltro, infoCodas);
        } catch (JMSException e) {
            log.error("Errore nel recupero delle informazioni della DMQ", e);
            throw new RuntimeException("Errore nel recupero delle informazioni della DMQ");
        }
        listaMon.add(tmpAltro);
    }

    private void elabJmsMessage(MonitorAltro tmpAltro, List<InfoCoda> infoCodas) {
        /*
         * if (infoCodas.size() == 0) { tmpAltro.setStato(MonitorSondeGenEsiti.OK.name()); } else {
         * tmpAltro.setStato(MonitorSondeGenEsiti.ERROR.name() + "|ci sono " + infoCodas.size() +
         * " messaggi nella DMQ del server "); log.error("Errore: ci sono " + infoCodas.size() +
         * " messaggi nella DMQ del server"); }
         */

        // non esistono messaggi su DLQ
        if (infoCodas.size() == 0) {
            tmpAltro.setStato(MonitorSondeGenEsiti.OK.name());
        } else {
            // preparo messaggio da inviare di ERROR
            StringBuffer koMsg = new StringBuffer();
            koMsg.append(MonitorSondeGenEsiti.ERROR.name() + "| messaggi rilevati in DLQ: ");

            // creazione messaggio raggruppando per payloadType/state
            Map<String, InfoCodaExt> mapInfos = this.buildInfoCodaMap(infoCodas);
            // per ogni payloadType creo parte del messaggio da inviare al trapper Zabbix
            for (Iterator<String> it = mapInfos.keySet().iterator(); it.hasNext();) {
                String key = it.next();
                InfoCodaExt tmpInfoCoda = mapInfos.get(key);
                koMsg.append(tmpInfoCoda.getCountMsg() + " messaggi/o");
                if (StringUtils.isNotBlank(tmpInfoCoda.getMessageSelector())) {
                    koMsg.append(" di queue type " + tmpInfoCoda.getMessageSelector());
                } /*
                   * else { koMsg.append(" di tipo non rilevato"); }
                   */

                koMsg.append(ERROR_SEPARATOR);
            }
            // preparazione messaggio di errore (inviato a Zabbix)
            String msg = koMsg.toString().substring(0, koMsg.toString().length() - ERROR_SEPARATOR.length());// remove
                                                                                                             // last
                                                                                                             // char
            tmpAltro.setStato(msg);
            log.error(msg);
        }
    }

    private Map<String, InfoCodaExt> buildInfoCodaMap(List<InfoCoda> infoCodas) {
        Map<String, InfoCodaExt> mapInfos = new HashMap<>();
        for (InfoCoda info : infoCodas) {
            String key = this.elabMapInfoKey(info);
            if (mapInfos.containsKey(key)) {
                // estrae oggetto e aggiorna
                InfoCodaExt tmpInfoCoda = mapInfos.get(key);
                tmpInfoCoda.incCountMsg(); // incremento contatore msg in DLQ
                mapInfos.put(key, tmpInfoCoda);
            } else {
                // init
                InfoCodaExt tmpInfoCoda = new InfoCodaExt();
                tmpInfoCoda.setMessageSelector(info.getMessageSelector());
                mapInfos.put(key, tmpInfoCoda);
            }
        }
        return mapInfos;
    }

    private String elabMapInfoKey(InfoCoda info) {
        StringBuffer tmpKey = new StringBuffer();
        tmpKey.append(info.getMessageSelector());
        return tmpKey.toString();
    }
}
