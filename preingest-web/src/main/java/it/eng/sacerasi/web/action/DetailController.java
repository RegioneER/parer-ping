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

package it.eng.sacerasi.web.action;

import it.eng.sacerasi.slite.gen.form.AmministrazioneForm;
import it.eng.sacerasi.slite.gen.tablebean.PigVersRowBean;
import it.eng.sacerasi.slite.gen.tablebean.PigVersTableBean;
import it.eng.sacerasi.web.ejb.AmministrazioneEjb;
import it.eng.sacerasi.web.helper.ConfigurationHelper;
import it.eng.sacerasi.web.security.PreingestAuthenticator;
import it.eng.spagoIFace.session.SessionCoreManager;
import it.eng.spagoLite.SessionManager;
import it.eng.spagoLite.security.User;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.SOAPFaultException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author Bonora_L
 */
@Controller
@RequestMapping("/")
public class DetailController {

    private AmministrazioneEjb amministrazioneEjb;
    // private UnitaDocumentarieHelper udHelper;
    // private CalcoloMonitoraggioHelper calcoloMonHelper;
    private ConfigurationHelper configurationHelper;
    @Autowired
    private PreingestAuthenticator authenticator;

    private static final Logger logger = LoggerFactory.getLogger(DetailController.class);

    @PostConstruct
    public void init() {
        try {
            configurationHelper = (ConfigurationHelper) new InitialContext()
                    .lookup("java:app/SacerAsync-ejb/ConfigurationHelper");
            // calcoloMonHelper = (CalcoloMonitoraggioHelper) new
            // InitialContext().lookup("java:app/Parer-ejb/CalcoloMonitoraggioHelper");
            // udHelper = (UnitaDocumentarieHelper) new
            // InitialContext().lookup("java:app/Parer-ejb/UnitaDocumentarieHelper");
            amministrazioneEjb = (AmministrazioneEjb) new InitialContext()
                    .lookup("java:app/SacerAsync-ejb/AmministrazioneEjb");
        } catch (NamingException ex) {
            logger.error("Errore nel recupero dell'EJB ConfigurationHelper ", ex);
            throw new IllegalStateException(ex);
        }
    }

    @GetMapping(value = "/vers/{id}")
    public @ResponseBody void detailVers(final HttpServletRequest request, final HttpServletResponse res,
            @PathVariable BigDecimal id, Model model) {
        model.addAttribute("id", id);

        PigVersRowBean row = amministrazioneEjb.getPigVersSoloNomeRowBean(id);
        PigVersTableBean table = new PigVersTableBean();
        table.add(row);
        AmministrazioneForm form = new AmministrazioneForm();
        form.getVersList().setTable(table);
        AmministrazioneAction action = new AmministrazioneAction();

        SessionManager.clearActionHistory(request.getSession());
        SessionCoreManager.setLastPublisher(request.getSession(), "");
        SessionManager.setForm(request.getSession(), form);
        SessionManager.setCurrentAction(request.getSession(), action.getControllerName());
        SessionManager.initMessageBox(request.getSession());
        SessionManager.addPrevExecutionToHistory(request.getSession(), true, false, null);
        User user = checkUser(request.getSession());
        user.setIdOrganizzazioneFoglia(id);
        Map<String, String> organizzazione = new LinkedHashMap<>();
        organizzazione.put("AMBIENTE", row.getString("nm_ambiente_vers"));
        organizzazione.put("VERSATORE", row.getNmVers());
        user.setOrganizzazioneMap(organizzazione);
        user.setConfigurazione(configurationHelper.getConfiguration());
        try {
            authenticator.recuperoAutorizzazioni(request.getSession());
            // ?operation=listNavigationOnClick&table=StruttureList&navigationEvent=dettaglioView&riga=0
            res.sendRedirect(request.getServletContext().getContextPath() + "/" + action.getControllerName()
                    + "?operation=listNavigationOnClick&table=" + form.getVersList().getName() + "&navigationEvent="
                    + AmministrazioneAction.NE_DETTAGLIO_VIEW + "&riga=0");
        } catch (WebServiceException ex) {
            logger.error("Eccezione", ex);
            // TODO : Inviare a una pagina che indica l'impossibilità di caricare la pagina? Direi di si
        } catch (IOException ex) {
            logger.error("Errore nel caricamento del dettaglio versatore", ex);
        }
    }

    private User checkUser(HttpSession session) {
        User user = (User) SessionManager.getUser(session);
        if (user != null) {
            logger.info("Login gi\u00E0 effettuato per l'utente " + user.getUsername());
        } else {
            try {
                user = authenticator.doLogin(session);
            } catch (SOAPFaultException ex) {
                user = null;
            }
        }
        return user;
    }
}
