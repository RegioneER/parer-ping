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

package it.eng.sacerasi.ws.invioOggettoAsincrono.util;

import it.eng.sacerasi.common.Constants;
import it.eng.sacerasi.ws.dto.IRispostaWS;
import it.eng.sacerasi.ws.dto.RispostaControlli;
import it.eng.sacerasi.ws.invioOggettoAsincrono.dto.InvioOggettoAsincronoExt;
import it.eng.sacerasi.ws.invioOggettoAsincrono.dto.RispostaWSInvioOggettoAsincrono;
import it.eng.sacerasi.messages.MessaggiWSBundle;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 *
 * @author Bonora_L
 */
public class XmlFragmentValidator {

    private static final Logger log = LoggerFactory.getLogger(XmlFragmentValidator.class);
    private RispostaWSInvioOggettoAsincrono rispostaWs = null;
    private RispostaControlli rispostaControlli = null;
    private InvioOggettoAsincronoExt invioOggettoAsincronoExt = null;

    /**
     *
     * @param rispostaWs               risposta ws
     * @param invioOggettoAsincronoExt dto versamento oggetto
     */
    public XmlFragmentValidator(RispostaWSInvioOggettoAsincrono rispostaWs,
	    InvioOggettoAsincronoExt invioOggettoAsincronoExt) {
	this.rispostaWs = rispostaWs;
	this.invioOggettoAsincronoExt = invioOggettoAsincronoExt;
	this.rispostaControlli = new RispostaControlli();
    }

    /**
     * Esegue la validatione di un frammento xml dato lo xsd
     *
     * @param xsdString contenuto xsd
     * @param xmlString contenuto xml
     * @param codErr    codice errore
     * @param params    parametri (opzionali)
     */
    public void validateXmlFragment(String xsdString, String xmlString, String codErr,
	    String... params) {
	Schema xsdSchema;
	SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
	try {
	    schemaFactory.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "");
	    schemaFactory.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
	    xsdSchema = schemaFactory.newSchema(new Source[] {
		    new StreamSource(new StringReader(xsdString)) });
	    Validator validator = xsdSchema.newValidator();
	    validator.setErrorHandler(new SimpleErrorHandler(codErr, params));
	    validator.validate(new StreamSource(new StringReader(xmlString)));
	} catch (SAXException | IOException ex) {
	    rispostaWs.setSeverity(IRispostaWS.SeverityEnum.ERROR);
	    rispostaWs.setErrorCode(MessaggiWSBundle.ERR_XML_MALFORMED);
	    String msg = MessaggiWSBundle.getString(MessaggiWSBundle.ERR_XML_MALFORMED)
		    + " L'Indice Oggetto versato non è conforme alla versione Indice Oggetto selezionato.";
	    rispostaWs.setErrorMessage(msg);
	    rispostaWs.getInvioOggettoAsincronoRisposta().setCdEsito(Constants.EsitoServizio.KO);
	    rispostaWs.getInvioOggettoAsincronoRisposta()
		    .setCdErr(MessaggiWSBundle.ERR_XML_MALFORMED);
	    rispostaWs.getInvioOggettoAsincronoRisposta().setDsErr(msg);
	    invioOggettoAsincronoExt.setFlRegistraObject(false);
	    invioOggettoAsincronoExt.setFlRegistraXMLObject(false);
	    invioOggettoAsincronoExt.setFlRegistraDatiSpecDicom(false);
	    log.error(msg + String.join("\n", ExceptionUtils.getMessage(ex)));
	}
    }

    public class SimpleErrorHandler implements ErrorHandler {

	String codErr = "";
	String[] params;

	public SimpleErrorHandler(String codErr, String[] params) {
	    this.codErr = codErr;
	    this.params = params;
	}

	@Override
	public void warning(SAXParseException e) throws SAXException {
	    log.error("Eccezione", e);
	}

	@Override
	public void error(SAXParseException e) throws SAXException {
	    rispostaControlli.setCodErr(this.codErr);

	    List<String> list = new ArrayList<>(Arrays.asList(this.params));
	    list.add(String.join("\n", ExceptionUtils.getRootCauseStackTrace(e)));

	    rispostaControlli.setDsErr(MessaggiWSBundle.getString(this.codErr, list.toArray()));
	    rispostaWs.setSeverity(IRispostaWS.SeverityEnum.ERROR);
	    rispostaWs.setErrorCode(rispostaControlli.getCodErr());
	    rispostaWs.setErrorMessage(
		    "L'Indice Oggetto versato non è conforme alla versione Indice Oggetto selezionato.");
	    rispostaWs.getInvioOggettoAsincronoRisposta().setCdEsito(Constants.EsitoServizio.KO);
	    rispostaWs.getInvioOggettoAsincronoRisposta().setCdErr(rispostaControlli.getCodErr());
	    rispostaWs.getInvioOggettoAsincronoRisposta().setDsErr(
		    "L'Indice Oggetto versato non è conforme alla versione Indice Oggetto selezionato.");
	    invioOggettoAsincronoExt.setFlRegistraObject(false);
	    invioOggettoAsincronoExt.setFlRegistraXMLObject(false);
	    invioOggettoAsincronoExt.setFlRegistraDatiSpecDicom(false);
	    log.error(String.join("\n", ExceptionUtils.getRootCauseStackTrace(e)));
	}

	@Override
	public void fatalError(SAXParseException e) throws SAXException {
	    rispostaWs.setSeverity(IRispostaWS.SeverityEnum.ERROR);
	    rispostaWs.setErrorCode(MessaggiWSBundle.ERR_666);
	    String msg = MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
		    String.join("\n", ExceptionUtils.getMessage(e)));
	    rispostaWs.setErrorMessage(msg);
	    rispostaWs.getInvioOggettoAsincronoRisposta().setCdEsito(Constants.EsitoServizio.KO);
	    rispostaWs.getInvioOggettoAsincronoRisposta().setCdErr(MessaggiWSBundle.ERR_666);
	    rispostaWs.getInvioOggettoAsincronoRisposta().setDsErr(msg);
	    invioOggettoAsincronoExt.setFlRegistraObject(false);
	    invioOggettoAsincronoExt.setFlRegistraXMLObject(false);
	    invioOggettoAsincronoExt.setFlRegistraDatiSpecDicom(false);
	    log.error(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
		    String.join("\n", ExceptionUtils.getRootCauseStackTrace(e))));
	}
    }
}
