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

package it.eng.sacerasi.web.rest.controller;

import it.eng.sacerasi.versamento.ejb.VersamentoOggettoEjb;
import it.eng.sacerasi.ws.dto.RispostaControlli;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import javax.ejb.EJB;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import it.eng.sacerasi.ws.rest.ejb.ControlliRestWS;

/**
 *
 * @author S257421
 *
 *         MEV#30735 - Rendere indipendente PING da SIAM
 */
@Controller
@RequestMapping("/rest/verificheSuPing")
public class VerificheSuPingController {

    @EJB(mappedName = "java:app/SacerAsync-ejb/VersamentoOggettoEjb")
    private VersamentoOggettoEjb versamentoOggettoEjb;
    @EJB(mappedName = "java:app/SacerAsync-ejb/ControlliRestWS")
    private ControlliRestWS controlliRestWS;

    private static final Logger log = LoggerFactory.getLogger(VerificheSuPingController.class);

    @RequestMapping(value = "/versatoreCessato.json", method = RequestMethod.POST)
    public @ResponseBody Boolean versatoreCessato(@RequestParam BigDecimal idOrganizApplic,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization, HttpServletResponse response) {
        log.debug("Chiamato il servizio {}", "/versatoreCessato.json");
        if (checkAuth(authorization, response)) {
            return versamentoOggettoEjb.isVersatoreCessato(idOrganizApplic);
        } else {
            return false;
        }
    }

    @RequestMapping(value = "/existsVersamentiPerUtente.json", method = RequestMethod.POST)
    public @ResponseBody Boolean existsVersamentiPerUtente(@RequestParam BigDecimal idUserIam,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization, HttpServletResponse response) {
        log.debug("Chiamato il servizio /existsVersamentiPerUtente.json");
        if (checkAuth(authorization, response)) {
            return versamentoOggettoEjb.checkExistsVersamentiPing(idUserIam.longValueExact());
        } else {
            return false;
        }
    }

    /*
     * Verifica le credenziali presenti nella Basic Authentication
     */
    private boolean checkAuth(String authorization, HttpServletResponse response) {
        boolean ret = false;
        if (authorization != null && authorization.toLowerCase().startsWith("basic")) {
            // Authorization: Basic base64credentials
            String base64Credentials = authorization.substring("Basic".length()).trim();
            byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
            String credentials = new String(credDecoded, StandardCharsets.UTF_8);
            // credentials = username:password
            String[] values = credentials.split(":", 2);
            if (values[0] == null || values[0].trim().equals("") || values[1] == null || values[1].trim().equals("")) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            } else {
                RispostaControlli risp = controlliRestWS.checkCredenziali(values[0], values[1], null,
                        ControlliRestWS.TipiWSPerControlli.WS_REST);
                if (risp.isrBoolean()) {
                    log.debug("Utente {} abilitato al servizio", values[0]);
                    ret = true;
                } else {
                    log.info("L'utente {} non è abilitato al sistema o all'esecuzione del servizio", values[0]);
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    ret = false;
                }
            }
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
        return ret;
    }

}
