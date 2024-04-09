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
package it.eng.sacerasi.ws.handler;

import it.eng.sacerasi.ws.dto.RispostaControlli;
import it.eng.sacerasi.ws.rest.ejb.ControlliRestWS;
import it.eng.spagoLite.security.auth.AuthenticationHandlerConstants;
import static it.eng.spagoLite.security.auth.AuthenticationHandlerConstants.QNAME_WSSE_HEADER;
import static it.eng.spagoLite.security.auth.AuthenticationHandlerConstants.WSSE_XSD_URI;
import java.util.HashSet;
import java.util.Set;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPFault;
import javax.xml.ws.ProtocolException;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import javax.xml.ws.soap.SOAPFaultException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author fioravanti_f
 */
public class IdpLoginHandler implements SOAPHandler<SOAPMessageContext> {

    private static final Logger log = LoggerFactory.getLogger(IdpLoginHandler.class);

    @Override
    public boolean handleMessage(SOAPMessageContext msgCtx) {
        ControlliRestWS myControlliWs;
        Boolean outbound = (Boolean) msgCtx.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
        String ipAddress = "NON_CALCOLATO";
        if (!outbound) {
            Object tmpRequest = msgCtx.get(MessageContext.SERVLET_REQUEST);
            if (tmpRequest != null && tmpRequest instanceof HttpServletRequest) {
                ipAddress = ((HttpServletRequest) tmpRequest).getHeader("X-FORWARDED-FOR");
                if (ipAddress == null || ipAddress.isEmpty()) {
                    ipAddress = ((HttpServletRequest) tmpRequest).getRemoteAddr();
                }
            }
            log.debug("IdpLoginHandler attivato. Client IP Address: " + ipAddress);

            // nell'originale su SACER la classe di chiama ControlliWs
            // in questo caso, quel nome era già "occupato".
            // Recupera l'ejb, se possibile, altrimenti segnala errore
            try {
                myControlliWs = (ControlliRestWS) new InitialContext()
                        .lookup("java:app/SacerAsync-ejb/ControlliRestWS");
            } catch (NamingException ex) {
                log.error("Errore nel recupero dell'EJB ", ex);
                throw new ProtocolException("Impossibile recuperare l'ejb ControlliRestWS", ex);
            }

            try {
                NodeList usernameEl = (NodeList) msgCtx.getMessage().getSOAPHeader()
                        .getElementsByTagNameNS(WSSE_XSD_URI, "Username");
                NodeList passwordEl = (NodeList) msgCtx.getMessage().getSOAPHeader()
                        .getElementsByTagNameNS(WSSE_XSD_URI, "Password");
                Node userNode = null;
                Node passNode = null;
                if (usernameEl != null && passwordEl != null && (userNode = usernameEl.item(0)) != null
                        && (passNode = passwordEl.item(0)) != null) {
                    String username = userNode.getFirstChild().getNodeValue();
                    String password = passNode.getFirstChild().getNodeValue();
                    RispostaControlli rc = myControlliWs.checkCredenziali(username, password, ipAddress,
                            ControlliRestWS.TipiWSPerControlli.WS_SOAP);
                    if (!rc.isrBoolean()) {
                        try {
                            SOAPFactory fac = SOAPFactory.newInstance();
                            SOAPFault sfault = fac.createFault();
                            sfault.setFaultCode(rc.getCodErr());
                            sfault.setFaultString(rc.getDsErr());
                            throw new SOAPFaultException(sfault);
                        } catch (SOAPException e1) {
                            log.error("Errore durante la creazione dell'eccezione SOAP", e1);
                            throw new ProtocolException(e1);
                        }
                    }
                    msgCtx.put(AuthenticationHandlerConstants.AUTHN_STAUTS, java.lang.Boolean.TRUE);
                    msgCtx.put(AuthenticationHandlerConstants.USER, username);
                    msgCtx.put(AuthenticationHandlerConstants.PWD, password);
                } else {
                    throw new ProtocolException("Username e password sono obbligatorie");
                }

            } catch (DOMException | SOAPException e) {
                throw new ProtocolException(e);
            }
            msgCtx.setScope(AuthenticationHandlerConstants.AUTHN_STAUTS, MessageContext.Scope.APPLICATION);
            msgCtx.setScope(AuthenticationHandlerConstants.USER, MessageContext.Scope.APPLICATION);
            msgCtx.setScope(AuthenticationHandlerConstants.PWD, MessageContext.Scope.APPLICATION);
        }
        return true;
    }

    @Override
    public boolean handleFault(SOAPMessageContext context) {
        return true;
    }

    @Override
    public void close(MessageContext context) {
    }

    @Override
    public Set<QName> getHeaders() {
        HashSet<QName> headers = new HashSet<QName>();
        headers.add(QNAME_WSSE_HEADER);
        return headers;
    }

}
