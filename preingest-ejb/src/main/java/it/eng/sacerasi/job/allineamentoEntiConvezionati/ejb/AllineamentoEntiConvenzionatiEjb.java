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

package it.eng.sacerasi.job.allineamentoEntiConvezionati.ejb;

import it.eng.integriam.client.ws.IAMSoapClients;
import it.eng.integriam.client.ws.allenteconv.AllineamentoEnteConvenzionato;
import it.eng.integriam.client.ws.allenteconv.RispostaWSAllineamentoEnteConvenzionato;
import it.eng.sacerasi.common.Constants;
import it.eng.sacerasi.entity.IamEnteSiamDaAllinea;
import it.eng.sacerasi.job.allineamentoEntiConvenzionati.util.CostantiAllineaEntiConv;
import it.eng.sacerasi.job.allineamentoEntiConvenzionati.util.CostantiAllineaEntiConv.EsitoServizio;
import it.eng.sacerasi.job.ejb.JobHelper;
import it.eng.sacerasi.job.ejb.JobLogger;
import it.eng.sacerasi.web.helper.AmministrazioneHelper;
import it.eng.sacerasi.web.helper.ConfigurationHelper;
import java.math.BigDecimal;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.SOAPFaultException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Gilioli_P
 */
@Stateless(mappedName = "AllineamentoEntiConvenzionatiEjb")
@LocalBean
@Interceptors({ it.eng.sacerasi.aop.TransactionInterceptor.class })
public class AllineamentoEntiConvenzionatiEjb {

    Logger log = LoggerFactory.getLogger(AllineamentoEntiConvenzionatiEjb.class);
    @EJB
    private ConfigurationHelper coHelper;
    @EJB
    private AmministrazioneHelper amministrazioneHelper;
    @EJB
    private JobHelper jobHelper;
    @EJB
    private JobLogger jobLoggerEjb;

    /**
     * Metodo chiamato dal JOB di allineamento ente convenzionato su IAM
     *
     */
    public void allineaEntiConvenzionati() {
        allineaEntiConvenzionati(null);
    }

    /**
     * Metodo chiamato per il ricalcolo su IAM dei servizi erogati sull'ultimo accordo dell'ente convenzionato associato
     * alla struttura
     *
     * @param enteSiamDaAllineaList
     *            lista di elementi {@link IamEnteSiamDaAllinea}
     */
    public void allineaEntiConvenzionati(List<IamEnteSiamDaAllinea> enteSiamDaAllineaList) {
        boolean arrivoDaOnLine = false;
        /*
         * Determino l'insieme delle registrazioni nel log degli enti da allineare con stato DA_ALLINEA,
         * ALLINEA_IN_TIMEOUT o ALLINEA_IN_ERRORE
         */
        if (enteSiamDaAllineaList == null) {
            enteSiamDaAllineaList = amministrazioneHelper.getIamEnteSiamDaAllinea();
        } else {
            arrivoDaOnLine = true;
        }

        // Istanzio risposta con esito OK di default
        RispostaWSAllineamentoEnteConvenzionato rispostaWsAec = new RispostaWSAllineamentoEnteConvenzionato();

        /*
         * Mi tengo una variabile che mi dice se la replica è andata o meno a buon fine per la scrittura sulla tabella
         * di log
         */
        boolean replicaOK = true;

        /* Ricavo i dati per la chiamata del ws */
        String url = coHelper.getValoreParamApplicByApplic(Constants.URL_ALLINEA_ENTE_CONVENZ);
        String nmUserid = coHelper.getValoreParamApplicByApplic(Constants.USERID_REPLICA_ORG);
        String cdPsw = coHelper.getValoreParamApplicByApplic(Constants.PSW_REPLICA_ORG);

        /* Per ogni registrazione determinata */
        for (IamEnteSiamDaAllinea enteSiamDaAllinea : enteSiamDaAllineaList) {

            BigDecimal idEnteSiam = enteSiamDaAllinea.getIdEnteSiam();

            try {

                /* Recupero il client per chiamata al WS */
                AllineamentoEnteConvenzionato client = IAMSoapClients.allineamentoEnteConvenzionatoClient(nmUserid,
                        cdPsw, url);

                if (client != null) {
                    log.info(
                            "Allineamento Ente Convenzionato - Preparazione attivazione servizio per l'ente convenzionato "
                                    + idEnteSiam);

                    // Esito chiamata WS
                    rispostaWsAec = client.ricalcoloServiziErogati(idEnteSiam.intValue());

                    // La risposta del WS può avere esito OK, WARNING o ERROR, per esitoServizio in questa fase divido
                    // in OK e KO
                    EsitoServizio esitoServizio = rispostaWsAec.getEsito().name().equals(
                            CostantiAllineaEntiConv.EsitoServizio.OK.name()) ? CostantiAllineaEntiConv.EsitoServizio.OK
                                    : CostantiAllineaEntiConv.EsitoServizio.KO;

                    // Scrivo l'esito del singolo Allineamento Ente Convenzionato
                    amministrazioneHelper.writeEsitoIamEnteSiamDaAllinea(enteSiamDaAllinea.getIdEnteSiamDaAllinea(),
                            esitoServizio, rispostaWsAec.getErrorCode(), rispostaWsAec.getErrorMessage());

                    String posNeg = esitoServizio.name().equals(CostantiAllineaEntiConv.EsitoServizio.OK.name())
                            ? "positiva" : "negativa";
                    log.info("Allineamento Ente Convenzionato - Risposta WS " + posNeg + " per l'ente siam "
                            + idEnteSiam);

                    // Se non è OK mi salvo l'informazione
                    if (!esitoServizio.name().equals(CostantiAllineaEntiConv.EsitoServizio.OK.name())) {
                        replicaOK = false;
                    }

                } else {
                    /* Se il client è null, ci sono stati problemi */
                    amministrazioneHelper.writeEsitoIamEnteSiamDaAllinea(enteSiamDaAllinea.getIdEnteSiamDaAllinea(),
                            CostantiAllineaEntiConv.EsitoServizio.KO, CostantiAllineaEntiConv.SERVIZI_ENTE_001,
                            "Errore nella creazione del client per la chiamata al WS di AllineamentoEnteConvenzionato");
                    log.error("Allineamento Ente Convenzionato - Risposta WS negativa per l'ente siam " + idEnteSiam);
                    break;
                }

            } catch (SOAPFaultException e) {
                /* Errori di autenticazione */
                amministrazioneHelper.writeEsitoIamEnteSiamDaAllinea(enteSiamDaAllinea.getIdEnteSiamDaAllinea(),
                        CostantiAllineaEntiConv.EsitoServizio.KO, CostantiAllineaEntiConv.SERVIZI_ENTE_002,
                        e.getFault().getFaultCode() + ": " + e.getFault().getFaultString());
                log.error("Allineamento Ente Convenzionato - Risposta WS negativa per l'ente siam " + idEnteSiam
                        + " - Utente che attiva il servizio non riconosciuto o non abilitato", e);
                replicaOK = false;
                break;
            } catch (WebServiceException e) {
                /* Se non risponde... */
                amministrazioneHelper.writeEsitoIamEnteSiamDaAllinea(enteSiamDaAllinea.getIdEnteSiamDaAllinea(),
                        CostantiAllineaEntiConv.EsitoServizio.NO_RISPOSTA, CostantiAllineaEntiConv.ALLINEA_ENTE_001,
                        "Il servizio di allineamento ente siam non risponde");
                log.error("Allineamento Ente Convenzionato - Risposta WS negativa per l'ente siam " + idEnteSiam
                        + " - Il servizio di allineamento ente convenzionato non risponde");
                replicaOK = false;
                break;
            } catch (Exception e) {
                /* ... o si verifica qualche errore di esecuzione */
                amministrazioneHelper.writeEsitoIamEnteSiamDaAllinea(enteSiamDaAllinea.getIdEnteSiamDaAllinea(),
                        CostantiAllineaEntiConv.EsitoServizio.KO, CostantiAllineaEntiConv.ALLINEA_ENTE_001,
                        e.getMessage());
                log.error("Allineamento Ente Convenzionato - Risposta WS negativa per l'ente siam " + idEnteSiam, e);
                replicaOK = false;
                break;
            }

        } // End for

        /* Scrivo nel log del job l'esito finale */
        if (!arrivoDaOnLine) {
            if (replicaOK) {
                // jobHelper.writeAtomicLogJob(JobConstants.JobEnum.ALLINEA_ENTI_CONVENZIONATI.name(),
                // JobConstants.OpTypeEnum.FINE_SCHEDULAZIONE.name());
                jobLoggerEjb.writeAtomicLog(Constants.NomiJob.ALLINEA_ENTI_CONVENZIONATI,
                        Constants.TipiRegLogJob.FINE_SCHEDULAZIONE, null);
            } else {
                // jobHelper.writeAtomicLogJob(JobConstants.JobEnum.ALLINEA_ENTI_CONVENZIONATI.name(),
                // JobConstants.OpTypeEnum.ERRORE.name(), "Errore durante la chiamata al WS di allineamento ente
                // convenzionato");
                jobLoggerEjb.writeAtomicLog(Constants.NomiJob.ALLINEA_ENTI_CONVENZIONATI,
                        Constants.TipiRegLogJob.ERRORE, "Errore durante la chiamata al WS di allineamento ente siam");
            }
        }
    }
}
