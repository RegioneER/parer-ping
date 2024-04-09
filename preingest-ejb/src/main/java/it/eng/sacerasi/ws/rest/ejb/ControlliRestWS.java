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
package it.eng.sacerasi.ws.rest.ejb;

import it.eng.parer.idpjaas.logutils.LogDto;
import it.eng.sacerasi.entity.IamUser;
import it.eng.sacerasi.messages.MessaggiWSBundle;
import it.eng.sacerasi.ws.dto.IWSDesc;
import it.eng.sacerasi.ws.dto.RispostaControlli;
import it.eng.spagoLite.security.User;
import it.eng.spagoLite.security.auth.WSLoginHandler;
import it.eng.spagoLite.security.exception.AuthWSException;
import java.util.Date;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Questa classe è un sottoinsieme della classe ControlliWS di SACER. La classe ControlliWS di PING svolge compiti
 * differenti e non mi pareva il caso di integrare questi metodi
 *
 * @author fioravanti_f
 */
@Stateless(mappedName = "ControlliRestWS")
@LocalBean
@TransactionAttribute(value = TransactionAttributeType.REQUIRES_NEW)
public class ControlliRestWS {

    @EJB
    WsIdpLogger idpLogger;

    private static final Logger log = LoggerFactory.getLogger(ControlliRestWS.class);

    @PersistenceContext(unitName = "SacerAsiJPA")
    private EntityManager entityManager;

    public enum TipiWSPerControlli {
        WS_REST, WS_SOAP
    }

    public RispostaControlli checkCredenziali(String loginName, String password, String indirizzoIP,
            TipiWSPerControlli tipows) {
        User utente = null;
        RispostaControlli rispostaControlli;
        rispostaControlli = new RispostaControlli();
        rispostaControlli.setrBoolean(false);

        log.info("Indirizzo IP del chiamante: " + indirizzoIP);

        if (loginName == null || loginName.isEmpty()) {
            rispostaControlli.setCodErr(MessaggiWSBundle.PING_RESTWS_001);
            rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.PING_RESTWS_001));
            return rispostaControlli;
        }

        // preparazione del log del login
        LogDto tmpLogDto = new LogDto();
        tmpLogDto.setNmAttore("Preingest WS");
        tmpLogDto.setNmUser(loginName);
        tmpLogDto.setCdIndIpClient(indirizzoIP);
        tmpLogDto.setTsEvento(new Date());
        // nota, non imposto l'indirizzo del server, verrà letto dal singleton da WsIdpLogger

        try {
            WSLoginHandler.login(loginName, password, indirizzoIP, entityManager);
            // se l'autenticazione riesce, non va in eccezione.
            // passo quindi a leggere i dati dell'utente dal db
            IamUser iamUser;
            String queryStr = "select iu from IamUser iu where iu.nmUserid = :nmUseridIn";
            javax.persistence.Query query = entityManager.createQuery(queryStr, IamUser.class);
            query.setParameter("nmUseridIn", loginName);
            iamUser = (IamUser) query.getSingleResult();
            //
            utente = new User();
            utente.setUsername(loginName);
            utente.setIdUtente(iamUser.getIdUserIam());
            // log della corretta autenticazione
            tmpLogDto.setTipoEvento(LogDto.TipiEvento.LOGIN_OK);
            tmpLogDto.setDsEvento("WS, login OK");
            //
            rispostaControlli.setrObject(utente);
            rispostaControlli.setrBoolean(true);
        } catch (AuthWSException e) {
            switch (tipows) {
            case WS_REST:
                if (e.getCodiceErrore().equals(AuthWSException.CodiceErrore.UTENTE_SCADUTO)) {
                    rispostaControlli.setCodErr(MessaggiWSBundle.PING_RESTWS_002);
                    rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.PING_RESTWS_002, loginName));
                } else if (e.getCodiceErrore().equals(AuthWSException.CodiceErrore.UTENTE_NON_ATTIVO)) {
                    rispostaControlli.setCodErr(MessaggiWSBundle.PING_RESTWS_003);
                    rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.PING_RESTWS_003, loginName));
                } else if (e.getCodiceErrore().equals(AuthWSException.CodiceErrore.LOGIN_FALLITO)) {
                    rispostaControlli.setCodErr(MessaggiWSBundle.PING_RESTWS_005);
                    rispostaControlli.setDsErr(
                            MessaggiWSBundle.getString(MessaggiWSBundle.PING_RESTWS_005, e.getDescrizioneErrore()));
                }
                break;
            case WS_SOAP:
                rispostaControlli.setCodErr(e.getCodiceErrore().name());
                rispostaControlli.setDsErr(e.getDescrizioneErrore());
                break;
            }
            //
            // log dell'errore di autenticazione; ripeto la sequenza di if per chiarezza.
            // Per altro nel caso sia stato invocato il ws SOAP, la distinzione
            // del tipo di errore non l'ho ancora eseguita.
            //
            if (e.getCodiceErrore().equals(AuthWSException.CodiceErrore.UTENTE_SCADUTO)) {
                tmpLogDto.setTipoEvento(LogDto.TipiEvento.EXPIRED);
                tmpLogDto.setDsEvento("WS, " + e.getDescrizioneErrore());
            } else if (e.getCodiceErrore().equals(AuthWSException.CodiceErrore.UTENTE_NON_ATTIVO)) {
                tmpLogDto.setTipoEvento(LogDto.TipiEvento.LOCKED);
                tmpLogDto.setDsEvento("WS, " + e.getDescrizioneErrore());
            } else if (e.getCodiceErrore().equals(AuthWSException.CodiceErrore.LOGIN_FALLITO)) {
                // se l'autenticazione fallisce, devo capire se è stata sbagliata la password oppure
                // non esiste l'utente. Provo a caricarlo e verifico la cosa.
                String queryStr = "select count(iu) from IamUser iu where iu.nmUserid = :nmUseridIn";
                javax.persistence.Query query = entityManager.createQuery(queryStr);
                query.setParameter("nmUseridIn", loginName);
                long tmpNumUtenti = (Long) query.getSingleResult();
                if (tmpNumUtenti > 0) {
                    tmpLogDto.setTipoEvento(LogDto.TipiEvento.BAD_PASS);
                    tmpLogDto.setDsEvento("WS, bad password");
                } else {
                    tmpLogDto.setTipoEvento(LogDto.TipiEvento.BAD_USER);
                    tmpLogDto.setDsEvento("WS, utente sconosciuto");
                }
            }

        } catch (Exception e) {
            rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
            rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
                    "Eccezione nella fase di autenticazione del EJB "
                            + String.join("\n", ExceptionUtils.getRootCauseStackTrace(e))));
            log.error("Eccezione nella fase di autenticazione del EJB ", e);
        }

        // scrittura log
        idpLogger.scriviLog(tmpLogDto);
        //

        return rispostaControlli;
    }

    public RispostaControlli checkAuthWSNoOrg(User utente, IWSDesc descrizione) {
        RispostaControlli rispostaControlli;
        rispostaControlli = new RispostaControlli();
        rispostaControlli.setrBoolean(false);

        try {
            String querString = "select count(iu) from IamUser iu " + "JOIN iu.iamAbilOrganizs iao "
                    + "JOIN iao.iamAutorServs ias  " + "WHERE iu.nmUserid = :nmUserid  "
                    + "AND ias.nmServizioWeb = :servizioWeb";
            javax.persistence.Query query = entityManager.createQuery(querString);
            query.setParameter("nmUserid", utente.getUsername());
            query.setParameter("servizioWeb", descrizione.getNomeWs());
            long num = (long) query.getSingleResult();
            if (num > 0) {
                rispostaControlli.setrBoolean(true);
            } else {
                // L''utente {0} non è autorizzato alla funzione {1}
                rispostaControlli.setCodErr(MessaggiWSBundle.PING_RESTWS_004);
                rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.PING_RESTWS_004,
                        utente.getUsername(), descrizione.getNomeWs()));
            }
        } catch (Exception e) {
            rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
            rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
                    "Eccezione nella fase di autenticazione del EJB "
                            + String.join("\n", ExceptionUtils.getRootCauseStackTrace(e))));
            log.error("Eccezione nella fase di autenticazione del EJB ", e);
        }

        return rispostaControlli;
    }

}
