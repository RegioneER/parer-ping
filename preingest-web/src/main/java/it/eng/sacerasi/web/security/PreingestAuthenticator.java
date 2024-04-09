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

package it.eng.sacerasi.web.security;

import it.eng.integriam.client.util.UserUtil;
import it.eng.integriam.client.ws.IAMSoapClients;
import it.eng.integriam.client.ws.recauth.RecuperoAutorizzazioni;
import it.eng.integriam.client.ws.recauth.RecuperoAutorizzazioniRisposta;
import it.eng.integriam.client.ws.recauth.AuthWSException_Exception;
import it.eng.sacerasi.web.helper.ConfigurationHelper;
import it.eng.spagoLite.SessionManager;
import it.eng.spagoLite.security.User;
import it.eng.spagoLite.security.auth.Authenticator;
import javax.ejb.EJB;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Agati_D
 */
public class PreingestAuthenticator extends Authenticator {

    @EJB(mappedName = "java:app/SacerAsync-ejb/ConfigurationHelper")
    private ConfigurationHelper configHelper;

    @Override
    public User recuperoAutorizzazioni(HttpSession httpSession) {
        User utente = (User) SessionManager.getUser(httpSession);
        RecuperoAutorizzazioni client = IAMSoapClients.recuperoAutorizzazioniClient(
                utente.getConfigurazione().get("USERID_RECUP_INFO"), utente.getConfigurazione().get("PSW_RECUP_INFO"),
                utente.getConfigurazione().get("URL_RECUP_AUTOR_USER"));
        RecuperoAutorizzazioniRisposta resp;
        try {
            resp = client.recuperoAutorizzazioniPerNome(utente.getUsername(), getAppName(),
                    utente.getIdOrganizzazioneFoglia().intValue());
        } catch (AuthWSException_Exception e) {
            throw new RuntimeException(e);
        }
        UserUtil.fillComponenti(utente, resp);
        SessionManager.setUser(httpSession, utente);
        return utente;
    }

    @Override
    protected String getAppName() {
        return configHelper.getParamApplicApplicationName();
    }

}
