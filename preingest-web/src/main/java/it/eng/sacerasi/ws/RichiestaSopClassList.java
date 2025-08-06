/*
 * Engineering Ingegneria Informatica S.p.A.
 *
 * Copyright (C) 2023 Regione Emilia-Romagna <p/> This program is free software: you can
 * redistribute it and/or modify it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version. <p/> This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Affero General Public License for more details. <p/> You should
 * have received a copy of the GNU Affero General Public License along with this program. If not,
 * see <https://www.gnu.org/licenses/>.
 */

package it.eng.sacerasi.ws;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.jws.HandlerChain;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.namespace.QName;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

import org.apache.cxf.annotations.SchemaValidation;

import it.eng.sacerasi.ws.response.RichiestaSopClassListRisposta;
import it.eng.sacerasi.ws.richiestaSopClassList.ejb.RichiestaSopClassListEjb;
import it.eng.sacerasi.ws.util.WsHelper;
import it.eng.spagoLite.security.auth.AuthenticationHandlerConstants;
import it.eng.spagoLite.security.auth.WSLoginHandler;
import it.eng.spagoLite.security.exception.AuthWSException;

@WebService(serviceName = "RichiestaSopClassList")
@HandlerChain(file = "/ws_handler.xml")
@SchemaValidation
public class RichiestaSopClassList {
    @Resource
    private WebServiceContext wsCtx;
    @EJB
    private WsHelper wsHelper;
    @EJB
    private RichiestaSopClassListEjb ejbRef;

    @WebMethod(operationName = "richiestaSopClassList")
    public RichiestaSopClassListRisposta richiestaSopClassList(
	    @WebParam(name = "nmAmbiente") String nmAmbiente,
	    @WebParam(name = "nmVersatore") String nmVersatore) {

	MessageContext msgCtx = wsCtx.getMessageContext();
	String username = (String) msgCtx.get(AuthenticationHandlerConstants.USER);
	String servizioWeb = ((QName) msgCtx.get(MessageContext.WSDL_SERVICE)).getLocalPart();
	RichiestaSopClassListRisposta risposta = null;
	try {
	    wsHelper.checkAuthorizations(nmAmbiente, nmVersatore, username, servizioWeb);
	    risposta = ejbRef.richiestaSopClassList(nmAmbiente, nmVersatore);
	} catch (AuthWSException e) {
	    WSLoginHandler.throwSOAPFault(e);
	}
	return risposta;
    }

}
