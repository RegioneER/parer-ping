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

package it.eng.sacerasi.ws.replicaUtente.ejb;

import it.eng.integriam.server.ws.Costanti;
import it.eng.integriam.server.ws.reputente.CancellaUtenteRisposta;
import it.eng.integriam.server.ws.reputente.InserimentoUtenteRisposta;
import it.eng.integriam.server.ws.reputente.ModificaUtenteRisposta;
import it.eng.integriam.server.ws.reputente.ReplicaUtenteInterface;
import it.eng.integriam.server.ws.reputente.Utente;
import it.eng.sacerasi.ws.dto.IRispostaWS;
import it.eng.sacerasi.ws.replicaUtente.dto.CancellaUtenteExt;
import it.eng.sacerasi.ws.replicaUtente.dto.InserimentoUtenteExt;
import it.eng.sacerasi.ws.replicaUtente.dto.ModificaUtenteExt;
import it.eng.sacerasi.ws.replicaUtente.dto.RispostaWSCancellaUtente;
import it.eng.sacerasi.ws.replicaUtente.dto.RispostaWSInserimentoUtente;
import it.eng.sacerasi.ws.replicaUtente.dto.RispostaWSModificaUtente;
import it.eng.sacerasi.ws.replicaUtente.dto.WSDescCancellaUtente;
import it.eng.sacerasi.ws.replicaUtente.dto.WSDescInserimentoUtente;
import it.eng.sacerasi.ws.replicaUtente.dto.WSDescModificaUtente;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import it.eng.sacerasi.ws.replicaUtente.helper.CancellaUtenteCheckHelper;
import it.eng.sacerasi.ws.replicaUtente.helper.InserimentoUtenteCheckHelper;
import it.eng.sacerasi.ws.replicaUtente.helper.ModificaUtenteCheckHelper;

/**
 *
 * @author Gilioli_P
 */
@Stateless
@Interceptors({ it.eng.sacerasi.aop.TransactionInterceptor.class })
public class ReplicaUtenteEjb implements ReplicaUtenteInterface {

    private static final Logger log = LoggerFactory.getLogger(ReplicaUtenteEjb.class);
    private static final String INFO_FINE_CONTROLLI = "Fine controlli sui parametri di input";

    @EJB
    private InserimentoUtenteEjb inserimentoUtente;
    @EJB
    private ModificaUtenteEjb modificaUtente;
    @EJB
    private CancellaUtenteEjb cancellaUtente;
    @EJB
    private CancellaUtenteCheckHelper cancellaUtenteCheckHelper;
    @EJB
    private InserimentoUtenteCheckHelper inserimentoUtenteCheckHelper;
    @EJB
    private ModificaUtenteCheckHelper modificaUtenteCheckHelper;

    @Override
    public InserimentoUtenteRisposta inserimentoUtente(Utente utente) {
        /* Istanzio la risposta */
        RispostaWSInserimentoUtente rispostaWs = new RispostaWSInserimentoUtente();
        rispostaWs.setInserimentoUtenteRisposta(new InserimentoUtenteRisposta());

        /* Imposto l'esito della risposta di default OK */
        rispostaWs.getInserimentoUtenteRisposta().setCdEsito(Costanti.EsitoServizio.OK);

        /* Istanzio l'Ext con l'oggetto creato */
        InserimentoUtenteExt iuExt = new InserimentoUtenteExt();
        iuExt.setDescrizione(new WSDescInserimentoUtente());
        iuExt.setInserimentoUtenteInput(utente);

        log.info("Inizio controlli sui parametri di input forniti per Inserimento Utente");
        inserimentoUtenteCheckHelper.checkSessione(iuExt, rispostaWs);
        log.info(INFO_FINE_CONTROLLI);

        if (rispostaWs.getSeverity() != IRispostaWS.SeverityEnum.ERROR) {
            try {
                log.info("Inizio inserimento utente");
                /*
                 * Se i controlli sono andati a buon fine vuol dire che: - o l'utente esiste (e non è attivo), - o
                 * l'utente non esiste. Nel primo caso, lo modifico...
                 */
                if (inserimentoUtente.existsUtente(iuExt.getInserimentoUtenteInput().getIdUserIam())) {
                    inserimentoUtente.updateFromInserimentoIamUser(iuExt, rispostaWs);
                } // ... nel secondo lo inserisco da zero
                else {
                    inserimentoUtente.insertIamUser(iuExt, rispostaWs);
                }

                /* Popola la risposta */
                rispostaWs.getInserimentoUtenteRisposta().setUtente(utente);

                log.info("Fine inserimento utente: operazione completata con successo!");
            } catch (Exception e) {
                rispostaWs.setSeverity(IRispostaWS.SeverityEnum.ERROR);
                rispostaWs.setErrorType(IRispostaWS.ErrorTypeEnum.DB_FATAL);
                rispostaWs.setErrorMessage("Errore inserimento utente EJB " + e.getMessage());

                rispostaWs.getInserimentoUtenteRisposta().setCdEsito(Costanti.EsitoServizio.KO);
                rispostaWs.getInserimentoUtenteRisposta().setCdErr(rispostaWs.getErrorCode());
                rispostaWs.getInserimentoUtenteRisposta().setDsErr(rispostaWs.getErrorMessage());

                log.error("La procedura di Inserimento utente non è stata portata a termine: eseguito rollback [{}]",
                        e.getMessage());
            }
        }
        /* Ritorno la risposta */
        return rispostaWs.getInserimentoUtenteRisposta();
    }

    @Override
    public ModificaUtenteRisposta modificaUtente(Utente utente) {
        /* Istanzio la risposta */
        RispostaWSModificaUtente rispostaWs = new RispostaWSModificaUtente();
        rispostaWs.setModificaUtenteRisposta(new ModificaUtenteRisposta());

        /* Imposto l'esito della risposta di default OK */
        rispostaWs.getModificaUtenteRisposta().setCdEsito(Costanti.EsitoServizio.OK);

        /* Istanzio l'Ext con l'oggetto creato */
        ModificaUtenteExt muExt = new ModificaUtenteExt();
        muExt.setDescrizione(new WSDescModificaUtente());
        muExt.setModificaUtenteInput(utente);

        log.info("Inizio controlli sui parametri di input forniti per Modifica Utente {}", utente.getNmUserid());
        modificaUtenteCheckHelper.checkSessione(muExt, rispostaWs);
        log.info(INFO_FINE_CONTROLLI);

        if (rispostaWs.getSeverity() != IRispostaWS.SeverityEnum.ERROR) {
            try {
                log.info("Inizio modifica utente");
                /* Se i controlli sono andati a buon fine modifico l'utente... */
                modificaUtente.update2IamUser(muExt, rispostaWs);

                /* Popola la risposta */
                rispostaWs.getModificaUtenteRisposta().setUtente(utente);

                log.info("Fine modifica utente: operazione completata con successo!");
            } catch (Exception e) {
                rispostaWs.setSeverity(IRispostaWS.SeverityEnum.ERROR);
                rispostaWs.setErrorType(IRispostaWS.ErrorTypeEnum.DB_FATAL);
                rispostaWs.setErrorMessage("Errore inserimento utente EJB " + e.getMessage());

                rispostaWs.getModificaUtenteRisposta().setCdEsito(Costanti.EsitoServizio.KO);
                rispostaWs.getModificaUtenteRisposta().setCdErr(rispostaWs.getErrorCode());
                rispostaWs.getModificaUtenteRisposta().setDsErr(rispostaWs.getErrorMessage());

                log.error("La procedura di Modifica utente non è stata portata a termine: eseguito rollback [{}]",
                        e.getMessage());
            }
        }
        /* Ritorno la risposta */
        return rispostaWs.getModificaUtenteRisposta();
    }

    @Override
    public CancellaUtenteRisposta cancellaUtente(Integer idUserIam) {

        /* Istanzio la risposta */
        RispostaWSCancellaUtente rispostaWs = new RispostaWSCancellaUtente();
        rispostaWs.setCancellaUtenteRisposta(new CancellaUtenteRisposta());

        /* Imposto l'esito della risposta di default OK */
        rispostaWs.getCancellaUtenteRisposta().setCdEsito(Costanti.EsitoServizio.OK);

        /* Istanzio l'Ext con l'oggetto creato */
        CancellaUtenteExt cuExt = new CancellaUtenteExt();
        cuExt.setDescrizione(new WSDescCancellaUtente());
        cuExt.setIdUserIam(idUserIam);

        log.info("Inizio controlli sui parametri di input forniti per Cancella Utente");
        cancellaUtenteCheckHelper.checkSessione(cuExt, rispostaWs);
        log.info(INFO_FINE_CONTROLLI);

        if (rispostaWs.getSeverity() != IRispostaWS.SeverityEnum.ERROR) {
            try {
                log.info("Inizio cancellazione utente");

                /* Se i controlli sono andati a buon fine modifico/elimino l'utente... */
                cancellaUtente.deleteIamUser(cuExt, rispostaWs);

                /* Popola la risposta */
                rispostaWs.getCancellaUtenteRisposta().setIdUserIam(idUserIam);

                log.info("Fine cancellazione utente: operazione completata con successo!");
            } catch (Exception e) {
                rispostaWs.setSeverity(IRispostaWS.SeverityEnum.ERROR);
                rispostaWs.setErrorType(IRispostaWS.ErrorTypeEnum.DB_FATAL);
                rispostaWs.setErrorMessage("Errore inserimento utente EJB " + e.getMessage());

                rispostaWs.getCancellaUtenteRisposta().setCdEsito(Costanti.EsitoServizio.KO);
                rispostaWs.getCancellaUtenteRisposta().setCdErr(rispostaWs.getErrorCode());
                rispostaWs.getCancellaUtenteRisposta().setDsErr(rispostaWs.getErrorMessage());

                log.error("La procedura di Cancellazione utente non è stata portata a termine: eseguito rollback [{}]",
                        e.getMessage());
            }
        }
        /* Ritorno la risposta */
        return rispostaWs.getCancellaUtenteRisposta();
    }
}
