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

package it.eng.sacerasi.ws.ricerca.ricercaDiario.ejb;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationException;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.sacerasi.common.Constants;
import it.eng.sacerasi.messages.MessaggiWSBundle;
import it.eng.sacerasi.ws.dto.IRispostaWS.SeverityEnum;
import it.eng.sacerasi.ws.dto.RispostaControlli;
import it.eng.sacerasi.ws.ejb.XmlContextCache;
import it.eng.sacerasi.ws.ricerca.dto.AttribDatiSpecBean;
import it.eng.sacerasi.ws.ricerca.dto.ControlliRicerca;
import it.eng.sacerasi.ws.ricerca.dto.DatoSpecFiltroConNomeColonna;
import it.eng.sacerasi.ws.ricerca.dto.DatoSpecOrderConNomeColonna;
import it.eng.sacerasi.ws.ricerca.dto.DatoSpecOutputConNomeColonna;
import it.eng.sacerasi.ws.ricerca.ricercaDiario.dto.RicercaDiarioExt;
import it.eng.sacerasi.ws.ricerca.ricercaDiario.dto.RispostaWSRicercaDiario;
import it.eng.sacerasi.ws.xml.datiSpecFiltri.FiltroType;
import it.eng.sacerasi.ws.xml.datiSpecFiltri.ListaFiltriType;
import it.eng.sacerasi.ws.xml.datiSpecOrder.ListaDatiSpecificiOrderType;
import it.eng.sacerasi.ws.xml.datiSpecOrder.OrderType;
import it.eng.sacerasi.ws.xml.datiSpecOut.ListaDatiSpecificiOutType;
import it.eng.sacerasi.ws.xml.datiSpecResult.ListaValoriDatiSpecificiType;
import it.eng.sacerasixml.xsd.util.Utils;

@SuppressWarnings("unchecked")
@Stateless(mappedName = "RicercaDiarioParserEjb")
@LocalBean
public class RicercaDiarioParserEjb {

    private static final Logger log = LoggerFactory.getLogger(RicercaDiarioParserEjb.class);
    // Singleton Sjb di gestione cache dei parser Castor
    @EJB
    private XmlContextCache xmlContextCache;
    @EJB
    private ControlliRicerca controlliRicerca;

    /*
     * Recupero la lista degli attributi specificati da almeno una delle versioni XSD dei dati
     * specifici per un determinato tipo oggetto
     */
    private void initLisAttribDatiSpecByIdTipoObj(RicercaDiarioExt ricercaDiarioExt) {
	List<AttribDatiSpecBean> listaAttribDatiSpecBean = controlliRicerca
		.getAttribDatiSpecBean(ricercaDiarioExt.getIdTipoObject());
	ricercaDiarioExt.setListaAttribDatiSpec(listaAttribDatiSpecBean);
    }

    public void parseXMLDatiSpecFiltri(RicercaDiarioExt ricercaDiarioExt,
	    RispostaWSRicercaDiario rispostaWs) {
	// L'istanza dei filtri dati specifici decodificati dal relativo XML in input
	ListaFiltriType parsedListaFiltri = null;
	RispostaControlli rispostaControlli = new RispostaControlli();
	// init
	initLisAttribDatiSpecByIdTipoObj(ricercaDiarioExt);

	if (rispostaWs.getSeverity() != SeverityEnum.ERROR) {
	    try {
		Unmarshaller tmpUnmarshallerFiltri = xmlContextCache
			.getDatispecFiltriCtx_ListaFiltri().createUnmarshaller();
		tmpUnmarshallerFiltri.setSchema(xmlContextCache.getDatispecFiltriSchema());
		JAXBElement<ListaFiltriType> elemento = (JAXBElement<ListaFiltriType>) tmpUnmarshallerFiltri
			.unmarshal(Utils.getSaxSourceForUnmarshal(
				ricercaDiarioExt.getRicercaDiarioInput().getXmlDatiSpecFiltri()));
		parsedListaFiltri = elemento.getValue();
	    } catch (ValidationException e) {
		rispostaWs.setSeverity(SeverityEnum.ERROR);
		rispostaWs.setErrorCode(MessaggiWSBundle.ERR_XML_MALFORMED);
		rispostaWs.setErrorMessage(MessaggiWSBundle
			.getString(MessaggiWSBundle.ERR_XML_MALFORMED, e.getLocalizedMessage()));
		rispostaWs.getRicercaDiarioRisposta().setCdEsito(Constants.EsitoServizio.KO);
		rispostaWs.getRicercaDiarioRisposta().setCdErr(MessaggiWSBundle.ERR_XML_MALFORMED);
		rispostaWs.getRicercaDiarioRisposta().setDsErr(MessaggiWSBundle
			.getString(MessaggiWSBundle.ERR_XML_MALFORMED, e.getLocalizedMessage()));
		log.error("Errore: XML malformato nel blocco di dati generali.", e);
	    } catch (Exception e) {
		rispostaWs.setSeverity(SeverityEnum.ERROR);
		rispostaWs.setErrorCode(MessaggiWSBundle.ERR_666);
		rispostaWs.setErrorMessage(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666));
		rispostaWs.getRicercaDiarioRisposta().setCdEsito(Constants.EsitoServizio.KO);
		rispostaWs.getRicercaDiarioRisposta().setCdErr(MessaggiWSBundle.ERR_666);
		rispostaWs.getRicercaDiarioRisposta()
			.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
				String.join("\n", ExceptionUtils.getRootCauseStackTrace(e))));
		log.error(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666), e);
	    }
	}

	////////////////////////////////////////
	// VERIFICA XML DATI SPECIFICI FILTRI //
	////////////////////////////////////////

	if (rispostaWs.getSeverity() != SeverityEnum.ERROR) {
	    rispostaControlli.reset();
	    // Costruisco la lista dei filtri passati in input, aggiungendo il campo relativo al
	    // loro nome su DB, per
	    // ora nullo
	    List<DatoSpecFiltroConNomeColonna> listaDatiSpecFiltriConNomeColonna = new ArrayList<>();
	    for (FiltroType f : parsedListaFiltri.getFiltro()) {
		DatoSpecFiltroConNomeColonna filtro = new DatoSpecFiltroConNomeColonna(f, null);
		listaDatiSpecFiltriConNomeColonna.add(filtro);
	    }

	    // Eseguo il controllo
	    rispostaControlli = controlliRicerca.verificaXMLDatiSpecFiltri(
		    ricercaDiarioExt.getIdTipoObject(), listaDatiSpecFiltriConNomeColonna,
		    ricercaDiarioExt.getListaAttribDatiSpec());
	    if (!rispostaControlli.isrBoolean()) {
		switch (rispostaControlli.getCodErr()) {
		case "006":
		    rispostaControlli.setCodErr(MessaggiWSBundle.PING_DIARIO_006);
		    rispostaControlli.setDsErr(MessaggiWSBundle.getString(
			    MessaggiWSBundle.PING_DIARIO_006, rispostaControlli.getrObject(),
			    rispostaControlli.getrString()));
		    break;
		case "007":
		    rispostaControlli.setCodErr(MessaggiWSBundle.PING_DIARIO_007);
		    rispostaControlli.setDsErr(MessaggiWSBundle.getString(
			    MessaggiWSBundle.PING_DIARIO_007, rispostaControlli.getrObject(),
			    rispostaControlli.getrString()));
		    break;
		case "666":
		    rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
		    rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
			    rispostaControlli.getrObject()) + rispostaControlli.getrString());
		    break;
		}
		setRispostaWsError(rispostaWs, rispostaControlli);
	    } else {
		// Se i controlli sono andati a buon fine, setto la lista dei filtri col campo
		// colonna aggiunto
		List<DatoSpecFiltroConNomeColonna> lista = (List<DatoSpecFiltroConNomeColonna>) rispostaControlli
			.getrObject();
		ricercaDiarioExt.setDatiSpecFiltriConNomeColonna(lista);
	    }
	}
    }

    public void parseXMLDatiSpecOutput(RicercaDiarioExt ricercaDiarioExt,
	    RispostaWSRicercaDiario rispostaWs) {
	// L'istanza dei dati specifici di output decodificati dal relativo XML in input
	ListaDatiSpecificiOutType parsedListaDatiSpecificiOut = null;
	RispostaControlli rispostaControlli = new RispostaControlli();
	//
	initLisAttribDatiSpecByIdTipoObj(ricercaDiarioExt);
	//
	if (rispostaWs.getSeverity() != SeverityEnum.ERROR) {
	    try {
		Unmarshaller tmpUnmarshallerOutput = xmlContextCache
			.getDatispecOutCtx_ListaDatiSpecificiOut().createUnmarshaller();
		tmpUnmarshallerOutput.setSchema(xmlContextCache.getDatispecOutSchema());
		JAXBElement<ListaDatiSpecificiOutType> elemento = (JAXBElement<ListaDatiSpecificiOutType>) tmpUnmarshallerOutput
			.unmarshal(Utils.getSaxSourceForUnmarshal(
				ricercaDiarioExt.getRicercaDiarioInput().getXmlDatiSpecOutput()));
		parsedListaDatiSpecificiOut = elemento.getValue();
	    } catch (ValidationException e) {
		rispostaWs.setSeverity(SeverityEnum.ERROR);
		rispostaWs.setErrorCode(MessaggiWSBundle.ERR_XML_MALFORMED);
		rispostaWs.setErrorMessage(
			MessaggiWSBundle.getString(MessaggiWSBundle.ERR_XML_MALFORMED,
				String.join("\n", ExceptionUtils.getRootCauseStackTrace(e))));
		rispostaWs.getRicercaDiarioRisposta().setCdEsito(Constants.EsitoServizio.KO);
		rispostaWs.getRicercaDiarioRisposta().setCdErr(MessaggiWSBundle.ERR_XML_MALFORMED);
		rispostaWs.getRicercaDiarioRisposta()
			.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_XML_MALFORMED,
				String.join("\n", ExceptionUtils.getRootCauseStackTrace(e))));
		log.error("Errore: XML malformato nel blocco di dati generali.", e);
	    } catch (Exception e) {
		rispostaWs.setSeverity(SeverityEnum.ERROR);
		rispostaWs.setErrorCode(MessaggiWSBundle.ERR_666);
		rispostaWs.setErrorMessage(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666));
		rispostaWs.getRicercaDiarioRisposta().setCdEsito(Constants.EsitoServizio.KO);
		rispostaWs.getRicercaDiarioRisposta().setCdErr(MessaggiWSBundle.ERR_666);
		rispostaWs.getRicercaDiarioRisposta()
			.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
				String.join("\n", ExceptionUtils.getRootCauseStackTrace(e))));
		log.error(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666), e);
	    }

	    ////////////////////////////////////////
	    // VERIFICA XML DATI SPECIFICI OUTPUT //
	    ////////////////////////////////////////

	    if (rispostaWs.getSeverity() != SeverityEnum.ERROR) {
		rispostaControlli.reset();
		List<String> listaDatiSpecOutput = parsedListaDatiSpecificiOut
			.getDatoSpecificoOut();
		rispostaControlli = controlliRicerca.verificaXMLDatiSpecOutput(
			ricercaDiarioExt.getIdTipoObject(), listaDatiSpecOutput,
			ricercaDiarioExt.getListaAttribDatiSpec());
		if (!rispostaControlli.isrBoolean()) {
		    switch (rispostaControlli.getCodErr()) {
		    case "008":
			rispostaControlli.setCodErr(MessaggiWSBundle.PING_DIARIO_008);
			rispostaControlli.setDsErr(MessaggiWSBundle.getString(
				MessaggiWSBundle.PING_DIARIO_008, rispostaControlli.getrObject(),
				rispostaControlli.getrString()));
			break;
		    case "666":
			rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
			rispostaControlli.setDsErr(MessaggiWSBundle
				.getString(MessaggiWSBundle.ERR_666, rispostaControlli.getrObject())
				+ rispostaControlli.getrString());
			break;
		    }
		    setRispostaWsError(rispostaWs, rispostaControlli);
		} else {
		    List<DatoSpecOutputConNomeColonna> listaDatiSpecOutputConNomeColonna = (List<DatoSpecOutputConNomeColonna>) rispostaControlli
			    .getrObject();
		    // Se il controllo è andato a buon fine, setto una LinkedHashMap con i dati
		    // specifici di output col
		    // campo colonna aggiunto
		    ricercaDiarioExt
			    .setDatiSpecOutputConNomeColonna(listaDatiSpecOutputConNomeColonna);
		}
	    }
	}
    }

    public void parseXMLDatiSpecOrder(RicercaDiarioExt ricercaDiarioExt,
	    RispostaWSRicercaDiario rispostaWs) {
	/*
	 * L'istanza relativa all'ordine dei dati specifici da presentare in output decodificati dal
	 * relativo XML in input
	 */
	ListaDatiSpecificiOrderType parsedListaDatiSpecificiOrder = null;
	RispostaControlli rispostaControlli = new RispostaControlli();

	//
	initLisAttribDatiSpecByIdTipoObj(ricercaDiarioExt);

	if (rispostaWs.getSeverity() != SeverityEnum.ERROR) {
	    try {
		Unmarshaller tmpUnmarshallerOrder = xmlContextCache
			.getDatispecOrderCtx_ListaDatiSpecificiOrder().createUnmarshaller();
		tmpUnmarshallerOrder.setSchema(xmlContextCache.getDatispecOrderSchema());
		JAXBElement<ListaDatiSpecificiOrderType> elemento = (JAXBElement<ListaDatiSpecificiOrderType>) tmpUnmarshallerOrder
			.unmarshal(Utils.getSaxSourceForUnmarshal(
				ricercaDiarioExt.getRicercaDiarioInput().getXmlDatiSpecOrder()));
		parsedListaDatiSpecificiOrder = elemento.getValue();
	    } catch (ValidationException e) {
		rispostaWs.setSeverity(SeverityEnum.ERROR);
		rispostaWs.setErrorCode(MessaggiWSBundle.ERR_XML_MALFORMED);
		rispostaWs.setErrorMessage(
			MessaggiWSBundle.getString(MessaggiWSBundle.ERR_XML_MALFORMED,
				String.join("\n", ExceptionUtils.getRootCauseStackTrace(e))));
		rispostaWs.getRicercaDiarioRisposta().setCdEsito(Constants.EsitoServizio.KO);
		rispostaWs.getRicercaDiarioRisposta().setCdErr(MessaggiWSBundle.ERR_XML_MALFORMED);
		rispostaWs.getRicercaDiarioRisposta()
			.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_XML_MALFORMED,
				String.join("\n", ExceptionUtils.getRootCauseStackTrace(e))));
		log.error("Errore: XML malformato nel blocco di dati generali.", e);
	    } catch (Exception e) {
		rispostaWs.setSeverity(SeverityEnum.ERROR);
		rispostaWs.setErrorCode(MessaggiWSBundle.ERR_666);
		rispostaWs.setErrorMessage(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666));
		rispostaWs.getRicercaDiarioRisposta().setCdEsito(Constants.EsitoServizio.KO);
		rispostaWs.getRicercaDiarioRisposta().setCdErr(MessaggiWSBundle.ERR_666);
		rispostaWs.getRicercaDiarioRisposta()
			.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
				String.join("\n", ExceptionUtils.getRootCauseStackTrace(e))));
		log.error(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666), e);
	    }

	    ///////////////////////////////////////
	    // VERIFICA XML DATI SPECIFICI ORDER //
	    ///////////////////////////////////////

	    if (rispostaWs.getSeverity() != SeverityEnum.ERROR) {
		rispostaControlli.reset();
		List<DatoSpecOrderConNomeColonna> listaDatiSpecOrderConNomeColonna = new ArrayList<>();
		for (OrderType dato : parsedListaDatiSpecificiOrder.getDatoSpecificoOrder()) {
		    DatoSpecOrderConNomeColonna ds = new DatoSpecOrderConNomeColonna(dato, null);
		    listaDatiSpecOrderConNomeColonna.add(ds);
		}
		rispostaControlli = controlliRicerca.verificaXMLDatiSpecOrder(
			ricercaDiarioExt.getIdTipoObject(), listaDatiSpecOrderConNomeColonna,
			ricercaDiarioExt.getListaAttribDatiSpec());
		if (!rispostaControlli.isrBoolean()) {
		    switch (rispostaControlli.getCodErr()) {
		    case "009":
			rispostaControlli.setCodErr(MessaggiWSBundle.PING_DIARIO_009);
			rispostaControlli.setDsErr(MessaggiWSBundle.getString(
				MessaggiWSBundle.PING_DIARIO_009, rispostaControlli.getrObject(),
				rispostaControlli.getrString()));
			break;
		    case "666":
			rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
			rispostaControlli.setDsErr(MessaggiWSBundle
				.getString(MessaggiWSBundle.ERR_666, rispostaControlli.getrObject())
				+ rispostaControlli.getrString());
			break;
		    }
		    setRispostaWsError(rispostaWs, rispostaControlli);
		} else {
		    // Se il controllo è andato a buon fine, setto la lista con l'ordine dati
		    // specifici col campo
		    // colonna aggiunto
		    ricercaDiarioExt.setDatiSpecOrderConNomeColonna(
			    (List<DatoSpecOrderConNomeColonna>) rispostaControlli.getrObject());
		}
	    }
	}
    }

    public String parseDatiSpecResult(ListaValoriDatiSpecificiType lista,
	    RicercaDiarioExt ricercaDiarioExt, RispostaWSRicercaDiario rispostaWs) {
	// init
	initLisAttribDatiSpecByIdTipoObj(ricercaDiarioExt);

	StringWriter tmpWriterResult = new StringWriter();
	if (rispostaWs.getSeverity() != SeverityEnum.ERROR) {
	    try {
		Marshaller mareshallo = xmlContextCache
			.getDatispecResultCtx_ListaValoriDatiSpecifici().createMarshaller();
		it.eng.sacerasi.ws.xml.datiSpecResult.ObjectFactory factory = new it.eng.sacerasi.ws.xml.datiSpecResult.ObjectFactory();
		JAXBElement<ListaValoriDatiSpecificiType> elemento = factory
			.createListaValoriDatiSpecifici(lista);
		mareshallo.marshal(elemento, tmpWriterResult);
	    } catch (ValidationException e) {
		rispostaWs.setSeverity(SeverityEnum.ERROR);
		rispostaWs.setErrorCode(MessaggiWSBundle.ERR_XML_MALFORMED);
		rispostaWs.setErrorMessage(
			MessaggiWSBundle.getString(MessaggiWSBundle.ERR_XML_MALFORMED,
				String.join("\n", ExceptionUtils.getRootCauseStackTrace(e))));
		rispostaWs.getRicercaDiarioRisposta().setCdEsito(Constants.EsitoServizio.KO);
		rispostaWs.getRicercaDiarioRisposta().setCdErr(MessaggiWSBundle.ERR_XML_MALFORMED);
		rispostaWs.getRicercaDiarioRisposta()
			.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_XML_MALFORMED,
				String.join("\n", ExceptionUtils.getRootCauseStackTrace(e))));
		log.error("Errore: XML malformato nel blocco di dati generali.", e);
	    } catch (Exception e) {
		rispostaWs.setSeverity(SeverityEnum.ERROR);
		rispostaWs.setErrorCode(MessaggiWSBundle.ERR_666);
		rispostaWs.setErrorMessage(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666));
		rispostaWs.getRicercaDiarioRisposta().setCdEsito(Constants.EsitoServizio.KO);
		rispostaWs.getRicercaDiarioRisposta().setCdErr(MessaggiWSBundle.ERR_666);
		rispostaWs.getRicercaDiarioRisposta()
			.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
				String.join("\n", ExceptionUtils.getRootCauseStackTrace(e))));
		log.error(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666), e);
	    }
	}
	return tmpWriterResult.toString();
    }

    private void setRispostaWsError(RispostaWSRicercaDiario rispostaWs,
	    RispostaControlli rispostaControlli) {
	rispostaWs.setSeverity(SeverityEnum.ERROR);
	rispostaWs.setErrorCode(rispostaControlli.getCodErr());
	rispostaWs.setErrorMessage(rispostaControlli.getDsErr());
	rispostaWs.getRicercaDiarioRisposta().setCdEsito(Constants.EsitoServizio.KO);
	rispostaWs.getRicercaDiarioRisposta().setCdErr(rispostaControlli.getCodErr());
	rispostaWs.getRicercaDiarioRisposta().setDsErr(rispostaControlli.getDsErr());
    }
}
