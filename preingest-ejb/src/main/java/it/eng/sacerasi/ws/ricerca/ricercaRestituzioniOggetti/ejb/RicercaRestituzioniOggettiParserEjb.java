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

package it.eng.sacerasi.ws.ricerca.ricercaRestituzioniOggetti.ejb;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.MarshalException;
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
import it.eng.sacerasi.ws.ricerca.ricercaRestituzioniOggetti.dto.RicercaRestituzioniOggettiExt;
import it.eng.sacerasi.ws.ricerca.ricercaRestituzioniOggetti.dto.RispostaWSRicercaRestituzioniOggetti;
import it.eng.sacerasi.ws.xml.datiSpecFiltri.FiltroType;
import it.eng.sacerasi.ws.xml.datiSpecFiltri.ListaFiltriType;
import it.eng.sacerasi.ws.xml.datiSpecOrder.ListaDatiSpecificiOrderType;
import it.eng.sacerasi.ws.xml.datiSpecOrder.OrderType;
import it.eng.sacerasi.ws.xml.datiSpecOut.ListaDatiSpecificiOutType;
import it.eng.sacerasi.ws.xml.datiSpecResult.ListaValoriDatiSpecificiType;
import it.eng.sacerasixml.xsd.util.Utils;

@SuppressWarnings("unchecked")
@Stateless(mappedName = "RicercaRestituzioniOggettiParserEjb")
@LocalBean
public class RicercaRestituzioniOggettiParserEjb {

    private static final Logger log = LoggerFactory.getLogger(RicercaRestituzioniOggettiParserEjb.class);
    // Singleton Sjb di gestione cache dei parser Castor
    @EJB
    private XmlContextCache xmlContextCache;
    @EJB
    private ControlliRicerca controlliRicerca;

    /*
     * Recupero la lista degli attributi specificati da almeno una delle versioni XSD dei dati specifici per un
     * determinato tipo oggetto
     */
    private void initLisAttribDatiSpecByIdTipoObj(RicercaRestituzioniOggettiExt ricercaRestituzioniOggettiExt) {

        List<AttribDatiSpecBean> listaAttribDatiSpecBean = controlliRicerca
                .getAttribDatiSpecBean(ricercaRestituzioniOggettiExt.getIdTipoObject());
        ricercaRestituzioniOggettiExt.setListaAttribDatiSpec(listaAttribDatiSpecBean);
    }

    public void parseXMLDatiSpecFiltri(RicercaRestituzioniOggettiExt ricercaRestituzioniOggettiExt,
            RispostaWSRicercaRestituzioniOggetti rispostaWs) {
        //
        RispostaControlli rispostaControlli = new RispostaControlli();
        // L'istanza dei filtri dati specifici decodificati dal relativo XML in input
        ListaFiltriType parsedListaFiltri = null;
        //
        initLisAttribDatiSpecByIdTipoObj(ricercaRestituzioniOggettiExt);

        if (rispostaWs.getSeverity() != SeverityEnum.ERROR) {
            String datiXml = ricercaRestituzioniOggettiExt.getRicercaRestituzioniOggettiInput().getXmlDatiSpecFiltri();
            try {
                Unmarshaller tmpUnmarshallerFiltri = xmlContextCache.getDatispecFiltriCtx_ListaFiltri()
                        .createUnmarshaller();
                tmpUnmarshallerFiltri.setSchema(xmlContextCache.getDatispecFiltriSchema());
                JAXBElement<ListaFiltriType> elemento = (JAXBElement<ListaFiltriType>) tmpUnmarshallerFiltri
                        .unmarshal(Utils.getSaxSourceForUnmarshal(datiXml));
                parsedListaFiltri = elemento.getValue();
            } catch (ValidationException e) {
                rispostaWs.setSeverity(SeverityEnum.ERROR);
                rispostaWs.setErrorCode(MessaggiWSBundle.ERR_XML_MALFORMED);
                rispostaWs.setErrorMessage(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_XML_MALFORMED,
                        String.join("\n", ExceptionUtils.getRootCauseStackTrace(e))));
                rispostaWs.getricercaRestituzioniOggettiRisposta().setCdEsito(Constants.EsitoServizio.KO);
                rispostaWs.getricercaRestituzioniOggettiRisposta().setCdErr(MessaggiWSBundle.ERR_XML_MALFORMED);
                rispostaWs.getricercaRestituzioniOggettiRisposta()
                        .setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_XML_MALFORMED,
                                String.join("\n", ExceptionUtils.getRootCauseStackTrace(e))));
                log.error("Errore: XML malformato nel blocco di dati generali.", e);
            } catch (Exception e) {
                rispostaWs.setSeverity(SeverityEnum.ERROR);
                rispostaWs.setErrorCode(MessaggiWSBundle.ERR_666);
                rispostaWs.setErrorMessage(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666));
                rispostaWs.getricercaRestituzioniOggettiRisposta().setCdEsito(Constants.EsitoServizio.KO);
                rispostaWs.getricercaRestituzioniOggettiRisposta().setCdErr(MessaggiWSBundle.ERR_666);
                rispostaWs.getricercaRestituzioniOggettiRisposta().setDsErr(MessaggiWSBundle.getString(
                        MessaggiWSBundle.ERR_666, String.join("\n", ExceptionUtils.getRootCauseStackTrace(e))));
                log.error(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666), e);
            }
        }

        ////////////////////////////////////////
        // VERIFICA XML DATI SPECIFICI FILTRI //
        ////////////////////////////////////////

        if (rispostaWs.getSeverity() != SeverityEnum.ERROR) {
            rispostaControlli.reset();
            // Costruisco la lista dei filtri passati in input, aggiungendo il campo relativo al loro nome su DB, per
            // ora nullo
            List<DatoSpecFiltroConNomeColonna> listaDatiSpecFiltriConNomeColonna = new ArrayList<>();
            for (FiltroType f : parsedListaFiltri.getFiltro()) {
                DatoSpecFiltroConNomeColonna filtro = new DatoSpecFiltroConNomeColonna(f, null);
                listaDatiSpecFiltriConNomeColonna.add(filtro);
            }

            // Eseguo il controllo
            rispostaControlli = controlliRicerca.verificaXMLDatiSpecFiltri(
                    ricercaRestituzioniOggettiExt.getIdTipoObject(), listaDatiSpecFiltriConNomeColonna,
                    ricercaRestituzioniOggettiExt.getListaAttribDatiSpec());
            if (!rispostaControlli.isrBoolean()) {
                switch (rispostaControlli.getCodErr()) {
                case "006":
                    rispostaControlli.setCodErr(MessaggiWSBundle.PING_RESTIT_006);
                    rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.PING_RESTIT_006,
                            rispostaControlli.getrObject(), rispostaControlli.getrString()));
                    break;
                case "007":
                    rispostaControlli.setCodErr(MessaggiWSBundle.PING_RESTIT_007);
                    rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.PING_RESTIT_007,
                            rispostaControlli.getrObject(), rispostaControlli.getrString()));
                    break;
                case "666":
                    rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
                    rispostaControlli.setDsErr(
                            MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666, rispostaControlli.getrObject())
                                    + rispostaControlli.getrString());
                    break;
                }
                setRispostaWsError(rispostaWs, rispostaControlli);
            } else {
                // Se i controlli sono andati a buon fine, setto la lista dei filtri col campo colonna aggiunto
                List<DatoSpecFiltroConNomeColonna> lista = (List<DatoSpecFiltroConNomeColonna>) rispostaControlli
                        .getrObject();
                ricercaRestituzioniOggettiExt.setDatiSpecFiltriConNomeColonna(lista);
            }
        }
    }

    public void parseXMLDatiSpecOutput(RicercaRestituzioniOggettiExt ricercaRestituzioniOggettiExt,
            RispostaWSRicercaRestituzioniOggetti rispostaWs) {
        //
        RispostaControlli rispostaControlli = new RispostaControlli();
        // L'istanza dei dati specifici di output decodificati dal relativo XML in input
        ListaDatiSpecificiOutType parsedListaDatiSpecificiOut = null;
        //
        initLisAttribDatiSpecByIdTipoObj(ricercaRestituzioniOggettiExt);

        if (rispostaWs.getSeverity() != SeverityEnum.ERROR) {
            try {
                Unmarshaller tmpUnmarshallerOutput = xmlContextCache.getDatispecOutCtx_ListaDatiSpecificiOut()
                        .createUnmarshaller();
                tmpUnmarshallerOutput.setSchema(xmlContextCache.getDatispecOutSchema());
                JAXBElement<ListaDatiSpecificiOutType> elemento = (JAXBElement<ListaDatiSpecificiOutType>) tmpUnmarshallerOutput
                        .unmarshal(Utils.getSaxSourceForUnmarshal(ricercaRestituzioniOggettiExt
                                .getRicercaRestituzioniOggettiInput().getXmlDatiSpecOutput()));
                parsedListaDatiSpecificiOut = elemento.getValue();
            } catch (MarshalException e) {
                rispostaWs.setSeverity(SeverityEnum.ERROR);
                rispostaWs.setErrorCode(MessaggiWSBundle.ERR_666);
                rispostaWs.setErrorMessage(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666));
                rispostaWs.getricercaRestituzioniOggettiRisposta().setCdEsito(Constants.EsitoServizio.KO);
                rispostaWs.getricercaRestituzioniOggettiRisposta().setCdErr(MessaggiWSBundle.ERR_666);
                rispostaWs.getricercaRestituzioniOggettiRisposta()
                        .setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666, e.getLocalizedMessage()));
                log.error(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666), e);
            } catch (ValidationException e) {
                rispostaWs.setSeverity(SeverityEnum.ERROR);
                rispostaWs.setErrorCode(MessaggiWSBundle.ERR_XML_MALFORMED);
                rispostaWs.setErrorMessage(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_XML_MALFORMED,
                        String.join("\n", ExceptionUtils.getRootCauseStackTrace(e))));
                rispostaWs.getricercaRestituzioniOggettiRisposta().setCdEsito(Constants.EsitoServizio.KO);
                rispostaWs.getricercaRestituzioniOggettiRisposta().setCdErr(MessaggiWSBundle.ERR_XML_MALFORMED);
                rispostaWs.getricercaRestituzioniOggettiRisposta()
                        .setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_XML_MALFORMED,
                                String.join("\n", ExceptionUtils.getRootCauseStackTrace(e))));
                log.error("Errore: XML malformato nel blocco di dati generali.", e);
            } catch (Exception e) {
                rispostaWs.setSeverity(SeverityEnum.ERROR);
                rispostaWs.setErrorCode(MessaggiWSBundle.ERR_666);
                rispostaWs.setErrorMessage(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666));
                rispostaWs.getricercaRestituzioniOggettiRisposta().setCdEsito(Constants.EsitoServizio.KO);
                rispostaWs.getricercaRestituzioniOggettiRisposta().setCdErr(MessaggiWSBundle.ERR_666);
                rispostaWs.getricercaRestituzioniOggettiRisposta().setDsErr(MessaggiWSBundle.getString(
                        MessaggiWSBundle.ERR_666, String.join("\n", ExceptionUtils.getRootCauseStackTrace(e))));
                log.error(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666), e);
            }

            ////////////////////////////////////////
            // VERIFICA XML DATI SPECIFICI OUTPUT //
            ////////////////////////////////////////

            if (rispostaWs.getSeverity() != SeverityEnum.ERROR) {
                rispostaControlli.reset();
                List<String> listaDatiSpecOutput = parsedListaDatiSpecificiOut.getDatoSpecificoOut();
                rispostaControlli = controlliRicerca.verificaXMLDatiSpecOutput(
                        ricercaRestituzioniOggettiExt.getIdTipoObject(), listaDatiSpecOutput,
                        ricercaRestituzioniOggettiExt.getListaAttribDatiSpec());
                if (!rispostaControlli.isrBoolean()) {
                    switch (rispostaControlli.getCodErr()) {
                    case "008":
                        rispostaControlli.setCodErr(MessaggiWSBundle.PING_RESTIT_008);
                        rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.PING_RESTIT_008,
                                rispostaControlli.getrObject(), rispostaControlli.getrString()));
                        break;
                    case "666":
                        rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
                        rispostaControlli.setDsErr(
                                MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666, rispostaControlli.getrObject())
                                        + rispostaControlli.getrString());
                        break;
                    }
                    setRispostaWsError(rispostaWs, rispostaControlli);
                } else {
                    // LinkedHashMap listaDatiSpecOutputConNomeColonna = (LinkedHashMap<String, String>)
                    // rispostaControlli.getrObject();
                    List<DatoSpecOutputConNomeColonna> listaDatiSpecOutputConNomeColonna = (List<DatoSpecOutputConNomeColonna>) rispostaControlli
                            .getrObject();
                    // Se il controllo è andato a buon fine, setto una LinkedHashMap con i dati specifici di output col
                    // campo colonna aggiunto
                    ricercaRestituzioniOggettiExt.setDatiSpecOutputConNomeColonna(listaDatiSpecOutputConNomeColonna);
                }
            }
        }
    }

    public void parseXMLDatiSpecOrder(RicercaRestituzioniOggettiExt ricercaRestituzioniOggettiExt,
            RispostaWSRicercaRestituzioniOggetti rispostaWs) {
        //
        RispostaControlli rispostaControlli = new RispostaControlli();
        /*
         * L'istanza relativa all'ordine dei dati specifici da presentare in output decodificati dal relativo XML in
         * input
         */
        ListaDatiSpecificiOrderType parsedListaDatiSpecificiOrder = null;
        //
        initLisAttribDatiSpecByIdTipoObj(ricercaRestituzioniOggettiExt);

        if (rispostaWs.getSeverity() != SeverityEnum.ERROR) {
            try {
                Unmarshaller tmpUnmarshallerOrder = xmlContextCache.getDatispecOrderCtx_ListaDatiSpecificiOrder()
                        .createUnmarshaller();
                tmpUnmarshallerOrder.setSchema(xmlContextCache.getDatispecOrderSchema());
                JAXBElement<ListaDatiSpecificiOrderType> elemento = (JAXBElement<ListaDatiSpecificiOrderType>) tmpUnmarshallerOrder
                        .unmarshal(Utils.getSaxSourceForUnmarshal(ricercaRestituzioniOggettiExt
                                .getRicercaRestituzioniOggettiInput().getXmlDatiSpecOrder()));
                parsedListaDatiSpecificiOrder = elemento.getValue();
            } catch (MarshalException e) {
                rispostaWs.setSeverity(SeverityEnum.ERROR);
                rispostaWs.setErrorCode(MessaggiWSBundle.ERR_666);
                rispostaWs.setErrorMessage(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666));
                rispostaWs.getricercaRestituzioniOggettiRisposta().setCdEsito(Constants.EsitoServizio.KO);
                rispostaWs.getricercaRestituzioniOggettiRisposta().setCdErr(MessaggiWSBundle.ERR_666);
                rispostaWs.getricercaRestituzioniOggettiRisposta()
                        .setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666, e.getLocalizedMessage()));
                log.error(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666), e);
            } catch (ValidationException e) {
                rispostaWs.setSeverity(SeverityEnum.ERROR);
                rispostaWs.setErrorCode(MessaggiWSBundle.ERR_XML_MALFORMED);
                rispostaWs.setErrorMessage(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_XML_MALFORMED,
                        String.join("\n", ExceptionUtils.getRootCauseStackTrace(e))));
                rispostaWs.getricercaRestituzioniOggettiRisposta().setCdEsito(Constants.EsitoServizio.KO);
                rispostaWs.getricercaRestituzioniOggettiRisposta().setCdErr(MessaggiWSBundle.ERR_XML_MALFORMED);
                rispostaWs.getricercaRestituzioniOggettiRisposta()
                        .setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_XML_MALFORMED,
                                String.join("\n", ExceptionUtils.getRootCauseStackTrace(e))));
                log.error("Errore: XML malformato nel blocco di dati generali.", e);
            } catch (Exception e) {
                rispostaWs.setSeverity(SeverityEnum.ERROR);
                rispostaWs.setErrorCode(MessaggiWSBundle.ERR_666);
                rispostaWs.setErrorMessage(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666));
                rispostaWs.getricercaRestituzioniOggettiRisposta().setCdEsito(Constants.EsitoServizio.KO);
                rispostaWs.getricercaRestituzioniOggettiRisposta().setCdErr(MessaggiWSBundle.ERR_666);
                rispostaWs.getricercaRestituzioniOggettiRisposta()
                        .setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666, e.getLocalizedMessage()));
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
                        ricercaRestituzioniOggettiExt.getIdTipoObject(), listaDatiSpecOrderConNomeColonna,
                        ricercaRestituzioniOggettiExt.getListaAttribDatiSpec());
                if (!rispostaControlli.isrBoolean()) {
                    switch (rispostaControlli.getCodErr()) {
                    case "009":
                        rispostaControlli.setCodErr(MessaggiWSBundle.PING_RESTIT_009);
                        rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.PING_RESTIT_009,
                                rispostaControlli.getrObject(), rispostaControlli.getrString()));
                        break;
                    case "666":
                        rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
                        rispostaControlli.setDsErr(
                                MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666, rispostaControlli.getrObject())
                                        + rispostaControlli.getrString());
                        break;
                    }
                    setRispostaWsError(rispostaWs, rispostaControlli);
                } else {
                    // Se il controllo è andato a buon fine, setto la lista con l'ordine dati specifici col campo
                    // colonna aggiunto
                    ricercaRestituzioniOggettiExt.setDatiSpecOrderConNomeColonna(
                            (List<DatoSpecOrderConNomeColonna>) rispostaControlli.getrObject());
                }
            }
        }
    }

    public String parseDatiSpecResult(JAXBElement<ListaValoriDatiSpecificiType> lista,
            RicercaRestituzioniOggettiExt ricercaRestituzioniOggettiExt,
            RispostaWSRicercaRestituzioniOggetti rispostaWs) {
        StringWriter tmpWriterResult = new StringWriter();
        //
        initLisAttribDatiSpecByIdTipoObj(ricercaRestituzioniOggettiExt);

        if (rispostaWs.getSeverity() != SeverityEnum.ERROR) {
            try {
                Marshaller mareshallo = xmlContextCache.getDatispecResultCtx_ListaValoriDatiSpecifici()
                        .createMarshaller();
                mareshallo.marshal(lista, tmpWriterResult);
            } catch (ValidationException e) {
                rispostaWs.setSeverity(SeverityEnum.ERROR);
                rispostaWs.setErrorCode(MessaggiWSBundle.ERR_XML_MALFORMED);
                rispostaWs.setErrorMessage(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_XML_MALFORMED,
                        String.join("\n", ExceptionUtils.getRootCauseStackTrace(e))));
                rispostaWs.getricercaRestituzioniOggettiRisposta().setCdEsito(Constants.EsitoServizio.KO);
                rispostaWs.getricercaRestituzioniOggettiRisposta().setCdErr(MessaggiWSBundle.ERR_XML_MALFORMED);
                rispostaWs.getricercaRestituzioniOggettiRisposta()
                        .setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_XML_MALFORMED,
                                String.join("\n", ExceptionUtils.getRootCauseStackTrace(e))));
                log.error("Errore: XML malformato nel blocco di dati generali.", e);
            } catch (Exception e) {
                rispostaWs.setSeverity(SeverityEnum.ERROR);
                rispostaWs.setErrorCode(MessaggiWSBundle.ERR_666);
                rispostaWs.setErrorMessage(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666));
                rispostaWs.getricercaRestituzioniOggettiRisposta().setCdEsito(Constants.EsitoServizio.KO);
                rispostaWs.getricercaRestituzioniOggettiRisposta().setCdErr(MessaggiWSBundle.ERR_666);
                rispostaWs.getricercaRestituzioniOggettiRisposta().setDsErr(MessaggiWSBundle.getString(
                        MessaggiWSBundle.ERR_666, String.join("\n", ExceptionUtils.getRootCauseStackTrace(e))));
                log.error(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666), e);
            }
        }
        return tmpWriterResult.toString();
    }

    private void setRispostaWsError(RispostaWSRicercaRestituzioniOggetti rispostaWs,
            RispostaControlli rispostaControlli) {
        rispostaWs.setSeverity(SeverityEnum.ERROR);
        rispostaWs.setErrorCode(rispostaControlli.getCodErr());
        rispostaWs.setErrorMessage(rispostaControlli.getDsErr());
        rispostaWs.getricercaRestituzioniOggettiRisposta().setCdEsito(Constants.EsitoServizio.KO);
        rispostaWs.getricercaRestituzioniOggettiRisposta().setCdErr(rispostaControlli.getCodErr());
        rispostaWs.getricercaRestituzioniOggettiRisposta().setDsErr(rispostaControlli.getDsErr());
    }
}
