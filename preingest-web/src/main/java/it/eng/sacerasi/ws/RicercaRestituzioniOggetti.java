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

package it.eng.sacerasi.ws;

import java.util.Date;

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

import it.eng.sacerasi.ws.ricerca.ricercaRestituzioniOggetti.dto.RicercaRestituzioniOggettiRisposta;
import it.eng.sacerasi.ws.ricerca.ricercaRestituzioniOggetti.ejb.RicercaRestituzioniOggettiEjb;
import it.eng.sacerasi.ws.util.WsHelper;
import it.eng.spagoLite.security.auth.AuthenticationHandlerConstants;
import it.eng.spagoLite.security.auth.WSLoginHandler;
import it.eng.spagoLite.security.exception.AuthWSException;

@WebService(serviceName = "RicercaRestituzioniOggetti")
@HandlerChain(file = "/ws_handler.xml")
@SchemaValidation
public class RicercaRestituzioniOggetti {
    @Resource
    private WebServiceContext wsCtx;
    @EJB
    private WsHelper wsHelper;
    @EJB
    private RicercaRestituzioniOggettiEjb ejbRef;

    @WebMethod(operationName = "ricercaRestituzioniOggetti")
    public RicercaRestituzioniOggettiRisposta ricercaRestituzioniOggetti(
            @WebParam(name = "nmAmbiente") String nmAmbiente, @WebParam(name = "nmVersatore") String nmVersatore,
            @WebParam(name = "nmTipoObject") String nmTipoObject, @WebParam(name = "cdKeyObject") String cdKeyObject,
            @WebParam(name = "tiStatoSessione") String tiStatoSessione,
            @WebParam(name = "dtAperturaSessioneDa") Date dtAperturaSessioneDa,
            @WebParam(name = "dtAperturaSessioneA") Date dtAperturaSessioneA,
            @WebParam(name = "niRecordInizio") Integer niRecordInizio,
            @WebParam(name = "niRecordResultSet") Integer niRecordResultSet,
            @WebParam(name = "xmlDatiSpecOutput") String xmlDatiSpecOutput,
            @WebParam(name = "xmlDatiSpecFiltri") String xmlDatiSpecFiltri,
            @WebParam(name = "xmlDatiSpecOrder") String xmlDatiSpecOrder) {

        MessageContext msgCtx = wsCtx.getMessageContext();
        String username = (String) msgCtx.get(AuthenticationHandlerConstants.USER);
        String servizioWeb = ((QName) msgCtx.get(MessageContext.WSDL_SERVICE)).getLocalPart();
        RicercaRestituzioniOggettiRisposta risposta = null;
        try {
            wsHelper.checkAuthorizations(nmAmbiente, nmVersatore, username, servizioWeb);
            risposta = ejbRef.ricercaRestituzioniOggetti(nmAmbiente, nmVersatore, nmTipoObject, cdKeyObject,
                    tiStatoSessione, dtAperturaSessioneDa, dtAperturaSessioneA, niRecordInizio, niRecordResultSet,
                    xmlDatiSpecOutput, xmlDatiSpecFiltri, xmlDatiSpecOrder);
        } catch (AuthWSException e) {
            WSLoginHandler.throwSOAPFault(e);
        }
        return risposta;
    }

}
