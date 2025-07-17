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

import java.math.BigDecimal;

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

import it.eng.sacerasi.ws.invioOggettoAsincrono.ejb.InvioOggettoAsincronoEjb;
import it.eng.sacerasi.ws.response.InvioOggettoAsincronoEstesoRisposta;
import it.eng.sacerasi.ws.response.InvioOggettoAsincronoRisposta;
import it.eng.sacerasi.ws.util.WsHelper;
import it.eng.spagoLite.security.auth.AuthenticationHandlerConstants;
import it.eng.spagoLite.security.auth.WSLoginHandler;
import it.eng.spagoLite.security.exception.AuthWSException;

@WebService(serviceName = "InvioOggettoAsincrono")
@HandlerChain(file = "/ws_handler.xml")
@SchemaValidation
public class InvioOggettoAsincrono {

    @Resource
    private WebServiceContext wsCtx;
    @EJB
    private InvioOggettoAsincronoEjb ejbRef;
    @EJB
    private WsHelper wsHelper;

    @WebMethod(operationName = "invioOggettoAsincrono")
    @HandlerChain(file = "/ws_handler.xml")
    public InvioOggettoAsincronoRisposta invioOggettoAsincrono(
	    @WebParam(name = "nmAmbiente") String nmAmbiente,
	    @WebParam(name = "nmVersatore") String nmVersatore,
	    @WebParam(name = "cdKeyObject") String cdKeyObject,
	    @WebParam(name = "nmTipoObject") String nmTipoObject,
	    @WebParam(name = "flFileCifrato") boolean flFileCifrato,
	    @WebParam(name = "flForzaWarning") boolean flForzaWarning,
	    @WebParam(name = "flForzaAccettazione") boolean flForzaAccettazione,
	    @WebParam(name = "dlMotivazione") String dlMotivazione,
	    @WebParam(name = "cdVersioneXml") String cdVersioneXml,
	    @WebParam(name = "xml") String xml) {

	MessageContext msgCtx = wsCtx.getMessageContext();
	String username = (String) msgCtx.get(AuthenticationHandlerConstants.USER);
	String servizioWeb = ((QName) msgCtx.get(MessageContext.WSDL_SERVICE)).getLocalPart();
	InvioOggettoAsincronoRisposta risposta = null;
	try {
	    wsHelper.checkAuthorizations(nmAmbiente, nmVersatore, username, servizioWeb);
	    risposta = ejbRef.invioOggettoAsincrono(username, nmAmbiente, nmVersatore, cdKeyObject,
		    nmTipoObject, flFileCifrato, flForzaWarning, flForzaAccettazione, dlMotivazione,
		    cdVersioneXml, xml, null);
	} catch (AuthWSException e) {
	    WSLoginHandler.throwSOAPFault(e);
	}
	return risposta;
    }

    @WebMethod(operationName = "invioOggettoAsincronoEsteso")
    @HandlerChain(file = "ws_handler.xml")
    public InvioOggettoAsincronoEstesoRisposta invioOggettoAsincronoEsteso(
	    @WebParam(name = "nmAmbiente") String nmAmbiente,
	    @WebParam(name = "nmVersatore") String nmVersatore,
	    @WebParam(name = "cdKeyObject") String cdKeyObject,
	    @WebParam(name = "dsObject") String dsObject,
	    @WebParam(name = "nmTipoObject") String nmTipoObject,
	    @WebParam(name = "flFileCifrato") boolean flFileCifrato,
	    @WebParam(name = "flForzaWarning") boolean flForzaWarning,
	    @WebParam(name = "flForzaAccettazione") boolean flForzaAccettazione,
	    @WebParam(name = "dlMotivazione") String dlMotivazione,
	    @WebParam(name = "cdVersioneXml") String cdVersioneXml,
	    @WebParam(name = "xml") String xml,
	    @WebParam(name = "nmAmbienteObjectPadre") String nmAmbienteObjectPadre,
	    @WebParam(name = "nmVersatoreObjectPadre") String nmVersatoreObjectPadre,
	    @WebParam(name = "cdKeyObjectPadre") String cdKeyObjectPadre,
	    @WebParam(name = "niTotObjectFigli") BigDecimal niTotObjectFigli,
	    @WebParam(name = "pgObjectFiglio") BigDecimal pgObjectFiglio,
	    @WebParam(name = "niUnitaDocAttese") BigDecimal niUnitaDocAttese,
	    @WebParam(name = "cdVersGen") String cdVersGen,
	    @WebParam(name = "tiGestOggettiFigli") String tiGestOggettiFigli) {
	MessageContext msgCtx = wsCtx.getMessageContext();
	String username = (String) msgCtx.get(AuthenticationHandlerConstants.USER);
	String servizioWeb = ((QName) msgCtx.get(MessageContext.WSDL_SERVICE)).getLocalPart();
	InvioOggettoAsincronoEstesoRisposta risposta = null;
	try {
	    wsHelper.checkAuthorizations(nmAmbiente, nmVersatore, username, servizioWeb);
	    // FIXME il parametro priorità per ora non è passato, vedere se possibile cambiare la
	    // signature del
	    // webservice
	    risposta = ejbRef.invioOggettoAsincronoEsteso(username, nmAmbiente, nmVersatore,
		    cdKeyObject, dsObject, nmTipoObject, flFileCifrato, flForzaWarning,
		    flForzaAccettazione, dlMotivazione, cdVersioneXml, xml, nmAmbienteObjectPadre,
		    nmVersatoreObjectPadre, cdKeyObjectPadre, niTotObjectFigli, pgObjectFiglio,
		    niUnitaDocAttese, cdVersGen, tiGestOggettiFigli, null, null);
	} catch (AuthWSException e) {
	    WSLoginHandler.throwSOAPFault(e);
	}
	return risposta;
    }
}
