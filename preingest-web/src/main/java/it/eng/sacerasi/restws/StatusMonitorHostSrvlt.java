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
package it.eng.sacerasi.restws;

import java.io.IOException;
import java.io.OutputStreamWriter;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import it.eng.sacerasi.common.Constants;
import it.eng.sacerasi.job.ejb.JobLogger;
import it.eng.sacerasi.messages.MessaggiWSBundle;
import it.eng.sacerasi.restws.util.Response405;
import it.eng.sacerasi.restws.util.SimplReqPrsr;
import it.eng.sacerasi.ws.dto.IRispostaWS;
import it.eng.sacerasi.ws.rest.monitoraggio.dto.MonFakeSessn;
import it.eng.sacerasi.ws.rest.monitoraggio.dto.RispostaWSStatusMonitor;
import it.eng.sacerasi.ws.rest.monitoraggio.dto.StatusMonExt;
import it.eng.sacerasi.ws.rest.monitoraggio.dto.WSDescStatusMonitor;
import it.eng.sacerasi.ws.rest.monitoraggio.dto.rmonitor.HostMonitor;
import it.eng.sacerasi.ws.rest.monitoraggio.ejb.StatusMonitorSync;

/**
 *
 * @author fioravanti_f
 */
public class StatusMonitorHostSrvlt extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger log = LoggerFactory.getLogger(StatusMonitorHostSrvlt.class);

    public StatusMonitorHostSrvlt() {
        super();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Response405.fancy405(resp, Response405.NomeWebServiceRest.WS_STATUS_MONITOR);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        StatusMonitorSync statusMonitorSync;
        JobLogger jobLogger;
        RispostaWSStatusMonitor rispostaWs;
        StatusMonExt statusMonExt;
        HostMonitor myEsito;
        MonFakeSessn sessioneFinta = new MonFakeSessn();
        SimplReqPrsr myReqPrsr = new SimplReqPrsr();

        SimplReqPrsr.ReqPrsrConfig tmpPrsrConfig = new SimplReqPrsr().new ReqPrsrConfig();
        rispostaWs = new RispostaWSStatusMonitor();
        statusMonExt = new StatusMonExt();
        statusMonExt.setDescrizione(new WSDescStatusMonitor());

        // Recupera l'ejb, se possibile - altrimenti segnala errore
        try {
            statusMonitorSync = (StatusMonitorSync) new InitialContext()
                    .lookup("java:app/SacerAsync-ejb/StatusMonitorSync");
        } catch (NamingException ex) {
            log.error("Errore nel recupero dell'EJB ", ex);
            throw new ServletException("Impossibile recuperare l'ejb StatusMonitorSync", ex);
        }

        // Recupera l'ejb, se possibile - altrimenti segnala errore
        try {
            jobLogger = (JobLogger) new InitialContext().lookup("java:app/SacerAsync-ejb/JobLogger");
        } catch (NamingException ex) {
            log.error("Errore nel recupero dell'EJB ", ex);
            throw new ServletException("Impossibile recuperare l'ejb JobLogger", ex);
        }

        statusMonitorSync.initRispostaWs(rispostaWs, statusMonExt);

        // logga l'inizio della chiamata al ws
        jobLogger.writeAtomicLog(Constants.NomiJob.WS_MONITORAGGIO_STATUS, Constants.TipiRegLogJob.INIZIO_SCHEDULAZIONE,
                null);
        //
        sessioneFinta.setIpChiamante(myReqPrsr.leggiIpVersante(request));
        log.info("Request, indirizzo IP di provenienza:  " + sessioneFinta.getIpChiamante());

        try {
            if (request.getContentType() != null
                    && request.getContentType().toLowerCase().indexOf("multipart/form-data") > -1) {
                rispostaWs.setSeverity(IRispostaWS.SeverityEnum.ERROR);
                rispostaWs.setEsitoWsErrBundle(MessaggiWSBundle.ERR_WS_CHECK,
                        "La chiamata è multipart/formdata, dovrebbe essere application/x-www-form-urlencoded");
                log.error("Errore nella servlet di monitoraggio: la chiamata è multipart/formdata,"
                        + " dovrebbe essere application/x-www-form-urlencoded");
            } else {
                tmpPrsrConfig.setSessioneFinta(sessioneFinta);
                tmpPrsrConfig.setRequest(request);
                myReqPrsr.parse(rispostaWs, tmpPrsrConfig);
                //
                if (rispostaWs.getSeverity() != IRispostaWS.SeverityEnum.OK) {
                    rispostaWs.setEsitoWsError(rispostaWs.getErrorCode(), rispostaWs.getErrorMessage());
                }
                /*
                 * ******************************************************************************** fine della verifica
                 * della struttura/signature del web service. Verifica dei dati effettivamente versati
                 * ********************************************************************************
                 */
                // testa le credenziali utente, tramite ejb
                if (rispostaWs.getSeverity() == IRispostaWS.SeverityEnum.OK) {
                    statusMonitorSync.verificaCredenziali(sessioneFinta.getLoginName(), sessioneFinta.getPassword(),
                            sessioneFinta.getIpChiamante(), rispostaWs, statusMonExt);
                }

                // prepara risposta
                if (rispostaWs.getSeverity() == IRispostaWS.SeverityEnum.OK) {
                    statusMonitorSync.recuperaStatusIstanza(rispostaWs, statusMonExt);
                }
            }
        } catch (Exception e1) {
            rispostaWs.setSeverity(IRispostaWS.SeverityEnum.ERROR);
            rispostaWs.setEsitoWsErrBundle(MessaggiWSBundle.ERR_666,
                    "Eccezione generica nella servlet di monitoraggio " + ExceptionUtils.getRootCauseMessage(e1));
            log.error("Eccezione generica nella servlet di monitoraggio", e1);
        }

        // logga la fine della chiamata al ws, eventualmente con l'errore
        if (rispostaWs.getSeverity() == IRispostaWS.SeverityEnum.OK) {
            jobLogger.writeAtomicLog(Constants.NomiJob.WS_MONITORAGGIO_STATUS,
                    Constants.TipiRegLogJob.FINE_SCHEDULAZIONE, null);
        } else {
            jobLogger.writeAtomicLog(Constants.NomiJob.WS_MONITORAGGIO_STATUS, Constants.TipiRegLogJob.ERRORE,
                    rispostaWs.getErrorCode() + ": " + rispostaWs.getErrorMessage());
        }
        // rispondi
        myEsito = rispostaWs.getIstanzaEsito();
        response.reset();
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json; charset=\"utf-8\"");
        ServletOutputStream out = response.getOutputStream();
        OutputStreamWriter tmpStreamWriter = new OutputStreamWriter(out, "UTF-8");

        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(tmpStreamWriter, myEsito);
        } catch (Exception e) {
            log.error("Eccezione nella servlet di monitoraggio", e);
        } finally {
            try {
                tmpStreamWriter.close();
            } catch (Exception ei) {
                log.error("Eccezione nella servlet di monitoraggio", ei);
            }
            try {
                out.flush();
                out.close();
            } catch (Exception ei) {
                log.error("Eccezione nella servlet di monitoraggio", ei);
            }
        }
    }
}
