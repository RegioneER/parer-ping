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

package it.eng.sacerasi.ws.invioOggettoAsincrono.ejb;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.sacerasi.common.Constants;
import it.eng.sacerasi.messages.MessaggiWSBundle;
import it.eng.sacerasi.sisma.ejb.InvioSismaHelper;
import it.eng.sacerasi.ws.dto.IRispostaWS.SeverityEnum;
import it.eng.sacerasi.ws.dto.RispostaControlli;
import it.eng.sacerasi.ws.ejb.XmlContextCache;
import it.eng.sacerasi.ws.invioOggettoAsincrono.dto.InvioOggettoAsincronoExt;
import it.eng.sacerasi.ws.invioOggettoAsincrono.dto.RispostaWSInvioOggettoAsincrono;
import it.eng.sacerasi.ws.invioOggettoAsincrono.util.XmlFragmentValidator;
import it.eng.sacerasi.ws.xml.invioAsync.FileType;
import it.eng.sacerasi.ws.xml.invioAsync.ListaUnitaDocumentarieType;
import it.eng.sacerasi.ws.xml.invioAsync.UnitaDocumentariaType.Files;
import it.eng.sacerasi.ws.xml.invioDaTrasf.OggettoType;
import it.eng.sacerasixml.xsd.util.Utils;

@Stateless(mappedName = "InvioOggettoAsincronoParserEjb")
@LocalBean
public class InvioOggettoAsincronoParserEjb {

    private static final Logger log = LoggerFactory.getLogger(InvioOggettoAsincronoParserEjb.class);

    // singleton ejb di gestione cache dei parser Castor
    @EJB
    XmlContextCache xmlContextCache;
    @EJB
    ControlliInvioOggettoAsincrono controlliInvioOggettoAsincrono;
    @EJB
    DatiSpecificiParserEjb datiSpecificiParser;

    @SuppressWarnings("unchecked")
    public void parseXML(InvioOggettoAsincronoExt invioOggettoAsincronoExt,
            RispostaWSInvioOggettoAsincrono rispostaWs) {
        //
        RispostaControlli rispostaControlli = new RispostaControlli();
        ListaUnitaDocumentarieType parsedListaUnitaDoc = null;
        // l'istanza dell'unità documentaria decodificata dall'XML di versamento
        // TODO: problema di NPE (vedere riga 428)
        OggettoType oggetto = null;

        if (rispostaWs.getSeverity() != SeverityEnum.ERROR) {
            try {
                Unmarshaller tmpUnmarshaller;
                if (invioOggettoAsincronoExt.getTiVersFile().equals(Constants.TipoVersamento.NO_ZIP.name())
                        || invioOggettoAsincronoExt.getTiVersFile()
                                .equals(Constants.TipoVersamento.ZIP_NO_XML_SACER.name())) {
                    tmpUnmarshaller = xmlContextCache.getVersReqAsyncCtx_ListaUnitaDocumentarie().createUnmarshaller();
                    tmpUnmarshaller.setSchema(xmlContextCache.getSchemaOfInvioAsync());
                    JAXBElement<ListaUnitaDocumentarieType> elemento = (JAXBElement<ListaUnitaDocumentarieType>) tmpUnmarshaller
                            .unmarshal(Utils.getSaxSourceForUnmarshal(
                                    invioOggettoAsincronoExt.getInvioOggettoAsincronoInput().getXml()));
                    parsedListaUnitaDoc = elemento.getValue();
                } else if (invioOggettoAsincronoExt.getTiVersFile()
                        .equals(Constants.TipoVersamento.ZIP_CON_XML_SACER.name())
                        || invioOggettoAsincronoExt.getTiVersFile()
                                .equals(Constants.TipoVersamento.DA_TRASFORMARE.name())) {
                    if (invioOggettoAsincronoExt.getNmTipoObject().equals("StrumentoUrbanistico")) {
                        tmpUnmarshaller = xmlContextCache.getInvioSUCtx_InvioSU().createUnmarshaller();
                        tmpUnmarshaller.setSchema(xmlContextCache.getInvioSUSchema());
                        // JAXBElement<OggettoType> elemento = (JAXBElement<OggettoType>)
                        // tmpUnmarshaller.unmarshal(Utils.getSaxSourceForUnmarshal(invioOggettoAsincronoExt.getInvioOggettoAsincronoInput().getXml()));
                        // oggetto = elemento.getValue();
                        tmpUnmarshaller.unmarshal(Utils.getSaxSourceForUnmarshal(
                                invioOggettoAsincronoExt.getInvioOggettoAsincronoInput().getXml()));
                    } else if (invioOggettoAsincronoExt.getNmTipoObject()
                            .equals(InvioSismaHelper.NOME_TIPO_OGGETTO_DA_TRASFORMARE)) {
                        tmpUnmarshaller = xmlContextCache.getInvioSismaCtx_InvioSisma().createUnmarshaller();
                        tmpUnmarshaller.setSchema(xmlContextCache.getInvioSismaSchema());
                        tmpUnmarshaller.unmarshal(Utils.getSaxSourceForUnmarshal(
                                invioOggettoAsincronoExt.getInvioOggettoAsincronoInput().getXml()));
                    } else {
                        // MEV 27034 - provo a parsare l'xml per vedere se è ben formato. Non entro nel dettaglio della
                        // sua forma.
                        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
                        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

                        try (InputStream instream = new ByteArrayInputStream(
                                invioOggettoAsincronoExt.getInvioOggettoAsincronoInput().getXml().getBytes())) {
                            documentBuilder.parse(instream);
                        }

                        // MEV27034 - prima il controllo era fatto così.
                        // tmpUnmarshaller = xmlContextCache.getVersReqAsyncDaTrasfCtx_Oggetto().createUnmarshaller();
                        // tmpUnmarshaller.setSchema(xmlContextCache.getSchemaOfInvioAsyncDaTrasfSchema());
                        // JAXBElement<OggettoType> elemento = (JAXBElement<OggettoType>) tmpUnmarshaller
                        // .unmarshal(Utils.getSaxSourceForUnmarshal(
                        // invioOggettoAsincronoExt.getInvioOggettoAsincronoInput().getXml()));
                        // oggetto = elemento.getValue();

                    }
                }
            } catch (UnmarshalException e) {
                rispostaWs.setSeverity(SeverityEnum.ERROR);
                rispostaWs.setErrorCode(MessaggiWSBundle.PING_SENDOBJ_XML_001);
                rispostaWs.setErrorMessage(MessaggiWSBundle.getString(MessaggiWSBundle.PING_SENDOBJ_XML_001));
                rispostaWs.getInvioOggettoAsincronoRisposta().setCdEsito(Constants.EsitoServizio.KO);
                rispostaWs.getInvioOggettoAsincronoRisposta().setCdErr(MessaggiWSBundle.PING_SENDOBJ_XML_001);
                rispostaWs.getInvioOggettoAsincronoRisposta()
                        .setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.PING_SENDOBJ_XML_001));
                invioOggettoAsincronoExt.setFlRegistraObject(false);
                invioOggettoAsincronoExt.setFlRegistraXMLObject(false);
                invioOggettoAsincronoExt.setFlRegistraDatiSpecDicom(false);
                log.error(MessaggiWSBundle.getString(MessaggiWSBundle.PING_SENDOBJ_XML_001), e);
            } catch (ValidationException e) {
                rispostaWs.setSeverity(SeverityEnum.ERROR);
                rispostaWs.setErrorCode(MessaggiWSBundle.ERR_XML_MALFORMED);
                rispostaWs.setErrorMessage(
                        MessaggiWSBundle.getString(MessaggiWSBundle.ERR_XML_MALFORMED, e.getMessage()));
                rispostaWs.getInvioOggettoAsincronoRisposta().setCdEsito(Constants.EsitoServizio.KO);
                rispostaWs.getInvioOggettoAsincronoRisposta().setCdErr(MessaggiWSBundle.ERR_XML_MALFORMED);
                rispostaWs.getInvioOggettoAsincronoRisposta()
                        .setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_XML_MALFORMED,
                                String.join("\n", ExceptionUtils.getRootCauseStackTrace(e))));
                invioOggettoAsincronoExt.setFlRegistraObject(false);
                invioOggettoAsincronoExt.setFlRegistraXMLObject(false);
                invioOggettoAsincronoExt.setFlRegistraDatiSpecDicom(false);
                log.error("Errore: XML malformato nel blocco di dati generali.", e);
            } catch (Exception e) {
                rispostaWs.setSeverity(SeverityEnum.ERROR);
                rispostaWs.setErrorCode(MessaggiWSBundle.PING_SENDOBJ_XML_001);
                rispostaWs.setErrorMessage(MessaggiWSBundle.getString(MessaggiWSBundle.PING_SENDOBJ_XML_001));
                rispostaWs.getInvioOggettoAsincronoRisposta().setCdEsito(Constants.EsitoServizio.KO);
                rispostaWs.getInvioOggettoAsincronoRisposta().setCdErr(MessaggiWSBundle.PING_SENDOBJ_XML_001);
                rispostaWs.getInvioOggettoAsincronoRisposta()
                        .setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.PING_SENDOBJ_XML_001));
                invioOggettoAsincronoExt.setFlRegistraObject(false);
                invioOggettoAsincronoExt.setFlRegistraXMLObject(false);
                invioOggettoAsincronoExt.setFlRegistraDatiSpecDicom(false);
                log.error(MessaggiWSBundle.getString(MessaggiWSBundle.PING_SENDOBJ_XML_001), e);
            }
        }

        //////////////////////////////////////////
        // VERIFICA VERSIONE XSD DATI SPECIFICI //
        //////////////////////////////////////////

        /*
         * Per ogni unità documentaria verifico gli elementi - Versione dati specifici - Elementi
         */
        if (rispostaWs.getSeverity() != SeverityEnum.ERROR && (invioOggettoAsincronoExt.getTiVersFile()
                .equals(Constants.TipoVersamento.NO_ZIP.name())
                || invioOggettoAsincronoExt.getTiVersFile().equals(Constants.TipoVersamento.ZIP_NO_XML_SACER.name()))) {
            rispostaControlli.reset();
            // se il tag <UnitaDocumentaria> esiste
            if (parsedListaUnitaDoc.getUnitaDocumentaria() != null
                    && parsedListaUnitaDoc.getUnitaDocumentaria().size() > 0) {
                // Prima di ciclare le unità documentarie verifico che per il tipo di versamento sia definito il numero
                // di unità documentarie corretto
                rispostaControlli = controlliInvioOggettoAsincrono.verificaUdInXml(
                        invioOggettoAsincronoExt.getIdTipoObject(), parsedListaUnitaDoc.getUnitaDocumentaria().size());
                if (!rispostaControlli.isrBoolean()) {
                    setRispostaWsError(invioOggettoAsincronoExt, rispostaWs, rispostaControlli);
                }
                if (rispostaWs.getSeverity() != SeverityEnum.ERROR) {
                    // int contaCollegamentiOK = 0;
                    // CICLO FOR SULLE UNITA' DOCUMENTARIE
                    for (int i = 0; i < parsedListaUnitaDoc.getUnitaDocumentaria().size(); i++) {
                        // Ricavo la versione dei dati specifici
                        String versioneDatiSpecifici = parsedListaUnitaDoc.getUnitaDocumentaria().get(i)
                                .getDatiSpecifici() != null
                                        ? parsedListaUnitaDoc.getUnitaDocumentaria().get(i).getDatiSpecifici()
                                                .getValue().getVersioneDatiSpecifici()
                                        : null;
                        // Ricavo l'insieme degli elementi File
                        Files insiemeFile = parsedListaUnitaDoc.getUnitaDocumentaria().get(i).getFiles();

                        // Verifico la presenza del tag Chiave nell'xml
                        if (rispostaWs.getSeverity() != SeverityEnum.ERROR) {
                            rispostaControlli.reset();
                            rispostaControlli = controlliInvioOggettoAsincrono.verificaChiave(
                                    invioOggettoAsincronoExt.getIdTipoObject(),
                                    parsedListaUnitaDoc.getUnitaDocumentaria().get(i).getChiave());
                            if (!rispostaControlli.isrBoolean()) {
                                setRispostaWsError(invioOggettoAsincronoExt, rispostaWs, rispostaControlli);
                                break;
                            }
                        }
                        // Verifico la presenza del tag Files nell'xml
                        if (rispostaWs.getSeverity() != SeverityEnum.ERROR) {
                            rispostaControlli.reset();
                            rispostaControlli = controlliInvioOggettoAsincrono.verificaFiles(
                                    invioOggettoAsincronoExt.getIdTipoObject(),
                                    parsedListaUnitaDoc.getUnitaDocumentaria().get(i).getFiles());
                            if (!rispostaControlli.isrBoolean()) {
                                setRispostaWsError(invioOggettoAsincronoExt, rispostaWs, rispostaControlli);
                                break;
                            }
                        }

                        // $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
                        // Inizio controlli su versioneDatiSpecifici
                        // $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
                        // Ricavo l'info se per il tipo oggetto specificato in input
                        // sono definite le versioni del XSD dei dati specifici
                        // e in caso verifico se una di esse è prevista
                        String xsdString = null;
                        if (rispostaWs.getSeverity() != SeverityEnum.ERROR) {
                            rispostaControlli.reset();
                            rispostaControlli = controlliInvioOggettoAsincrono.verificaVersioneXsd(
                                    invioOggettoAsincronoExt.getIdTipoObject(), versioneDatiSpecifici);
                            if (!rispostaControlli.isrBoolean()) {
                                setRispostaWsError(invioOggettoAsincronoExt, rispostaWs, rispostaControlli);
                                break;
                            } else {
                                xsdString = rispostaControlli.getrString();
                                invioOggettoAsincronoExt.setIdXsdDatiSpec(rispostaControlli.getrLong());
                            }
                        }

                        // Valido i dati specifici con l'xsd ritornato (Solo se il tipo oggetto è NON STUDIODICOM)
                        if (rispostaWs.getSeverity() != SeverityEnum.ERROR
                                && !invioOggettoAsincronoExt.getNmTipoObject().equalsIgnoreCase(Constants.STUDIO_DICOM)
                                && parsedListaUnitaDoc.getUnitaDocumentaria().get(i).getDatiSpecifici() != null) {

                            if (xsdString == null) {
                                rispostaControlli.setrBoolean(false);
                                rispostaControlli.setCodErr(MessaggiWSBundle.PING_SENDOBJ_XML_004);
                                rispostaControlli
                                        .setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.PING_SENDOBJ_XML_004,
                                                versioneDatiSpecifici, "XSD non definito"));
                                setRispostaWsError(invioOggettoAsincronoExt, rispostaWs, rispostaControlli);
                            } else {
                                try {
                                    StringWriter sw = new StringWriter();
                                    Marshaller marshaller = xmlContextCache.getVersReqAsyncCtx_ListaUnitaDocumentarie()
                                            .createMarshaller();
                                    marshaller.marshal(
                                            parsedListaUnitaDoc.getUnitaDocumentaria().get(i).getDatiSpecifici(), sw);
                                    sw.flush();
                                    sw.close();
                                    XmlFragmentValidator validator = new XmlFragmentValidator(rispostaWs,
                                            invioOggettoAsincronoExt);
                                    validator.validateXmlFragment(xsdString, sw.toString(),
                                            MessaggiWSBundle.PING_SENDOBJ_XML_004, versioneDatiSpecifici);
                                } catch (JAXBException | IOException ex) {
                                    rispostaWs.setSeverity(SeverityEnum.ERROR);
                                    rispostaWs.setErrorCode(MessaggiWSBundle.ERR_666);
                                    String msg = MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
                                            String.join("\n", ExceptionUtils.getRootCauseStackTrace(ex)));
                                    rispostaWs.setErrorMessage(msg);
                                    rispostaWs.getInvioOggettoAsincronoRisposta()
                                            .setCdEsito(Constants.EsitoServizio.KO);
                                    rispostaWs.getInvioOggettoAsincronoRisposta().setCdErr(MessaggiWSBundle.ERR_666);
                                    rispostaWs.getInvioOggettoAsincronoRisposta().setDsErr(msg);
                                    invioOggettoAsincronoExt.setFlRegistraObject(false);
                                    invioOggettoAsincronoExt.setFlRegistraXMLObject(false);
                                    invioOggettoAsincronoExt.setFlRegistraDatiSpecDicom(false);
                                    log.error(msg);
                                }
                            }
                        }

                        if (rispostaWs.getSeverity() != SeverityEnum.ERROR && insiemeFile != null) {
                            // $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
                            // Inizio controlli su insiemeFile
                            // $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
                            // Per ogni singolo file
                            Set<String> tipiFileObject = new HashSet<String>();
                            for (FileType singoloFile : insiemeFile.getFile()) {
                                String nmTipoFileObject = null;
                                String versioneDatiSpecificiFile = null;
                                nmTipoFileObject = singoloFile.getTipoFile();
                                if (rispostaWs.getSeverity() != SeverityEnum.ERROR) {
                                    // Determino i seguenti elementi
                                    versioneDatiSpecificiFile = singoloFile.getDatiSpecifici() != null
                                            ? singoloFile.getDatiSpecifici().getValue().getVersioneDatiSpecifici()
                                            : null;

                                    // Verifico se il tipo file specificato è definito per il tipo di oggetto
                                    rispostaControlli.reset();
                                    rispostaControlli = controlliInvioOggettoAsincrono.verificaTipoFileObject(
                                            invioOggettoAsincronoExt.getIdTipoObject(), nmTipoFileObject,
                                            versioneDatiSpecificiFile);
                                    if (!rispostaControlli.isrBoolean()) {
                                        setRispostaWsError(invioOggettoAsincronoExt, rispostaWs, rispostaControlli);
                                        break;
                                    } else {
                                        xsdString = rispostaControlli.getrString();
                                    }
                                }
                                // Valido i dati specifici del file con l'xsd ritornato
                                if (rispostaWs.getSeverity() != SeverityEnum.ERROR
                                        && singoloFile.getDatiSpecifici() != null) {
                                    try {
                                        StringWriter sw = new StringWriter();
                                        Marshaller marshaller = xmlContextCache
                                                .getVersReqAsyncCtx_ListaUnitaDocumentarie().createMarshaller();
                                        marshaller.marshal(singoloFile.getDatiSpecifici(), sw);
                                        sw.flush();
                                        sw.close();
                                        XmlFragmentValidator validator = new XmlFragmentValidator(rispostaWs,
                                                invioOggettoAsincronoExt);
                                        validator.validateXmlFragment(xsdString, sw.toString(),
                                                MessaggiWSBundle.PING_SENDOBJ_XML_008, nmTipoFileObject,
                                                versioneDatiSpecificiFile);
                                    } catch (JAXBException | IOException ex) {
                                        rispostaWs.setSeverity(SeverityEnum.ERROR);
                                        rispostaWs.setErrorCode(MessaggiWSBundle.ERR_666);
                                        String msg = MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
                                                ex.getMessage());
                                        rispostaWs.setErrorMessage(msg);
                                        rispostaWs.getInvioOggettoAsincronoRisposta()
                                                .setCdEsito(Constants.EsitoServizio.KO);
                                        rispostaWs.getInvioOggettoAsincronoRisposta()
                                                .setCdErr(MessaggiWSBundle.ERR_666);
                                        rispostaWs.getInvioOggettoAsincronoRisposta().setDsErr(msg);
                                        invioOggettoAsincronoExt.setFlRegistraObject(false);
                                        invioOggettoAsincronoExt.setFlRegistraXMLObject(false);
                                        invioOggettoAsincronoExt.setFlRegistraDatiSpecDicom(false);
                                        log.error(msg);
                                    }
                                }
                                // verifico che non esista un altro tag File con stesso valore del tag TipoFile di
                                // quello corrente
                                if (rispostaWs.getSeverity() != SeverityEnum.ERROR) {
                                    rispostaControlli.reset();
                                    if (!tipiFileObject.add(nmTipoFileObject)) {
                                        rispostaControlli.setrBoolean(false);
                                        rispostaControlli.setCodErr(MessaggiWSBundle.PING_SENDOBJ_XML_011);
                                        rispostaControlli.setDsErr(
                                                MessaggiWSBundle.getString(MessaggiWSBundle.PING_SENDOBJ_XML_011));
                                        setRispostaWsError(invioOggettoAsincronoExt, rispostaWs, rispostaControlli);
                                        break;
                                    }
                                }

                            } // end for sui file
                        }
                        if (rispostaWs.getSeverity() != SeverityEnum.ERROR) {
                            rispostaControlli.reset();
                            // Nel caso il tipo oggetto sia STUDIODICOM, istanzio il parser specifico
                            if (invioOggettoAsincronoExt.getTiVersFile().equals(Constants.TipoVersamento.NO_ZIP.name())
                                    && invioOggettoAsincronoExt.getNmTipoObject()
                                            .equalsIgnoreCase(Constants.STUDIO_DICOM)) {
                                StringWriter sw = new StringWriter();
                                try {
                                    Marshaller marshaller = xmlContextCache.getVersReqAsyncCtx_ListaUnitaDocumentarie()
                                            .createMarshaller();
                                    marshaller.marshal(
                                            parsedListaUnitaDoc.getUnitaDocumentaria().get(i).getDatiSpecifici(), sw);
                                    sw.flush();
                                    sw.close();
                                } catch (JAXBException | IOException ex) {
                                    rispostaWs.setSeverity(SeverityEnum.ERROR);
                                    rispostaWs.setErrorCode(MessaggiWSBundle.ERR_666);
                                    String msg = MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
                                            String.join("\n", ExceptionUtils.getRootCauseStackTrace(ex)));
                                    rispostaWs.setErrorMessage(msg);
                                    rispostaWs.getInvioOggettoAsincronoRisposta()
                                            .setCdEsito(Constants.EsitoServizio.KO);
                                    rispostaWs.getInvioOggettoAsincronoRisposta().setCdErr(MessaggiWSBundle.ERR_666);
                                    rispostaWs.getInvioOggettoAsincronoRisposta().setDsErr(msg);
                                    invioOggettoAsincronoExt.setFlRegistraObject(false);
                                    invioOggettoAsincronoExt.setFlRegistraXMLObject(false);
                                    invioOggettoAsincronoExt.setFlRegistraDatiSpecDicom(false);
                                    log.error(msg);
                                }
                                invioOggettoAsincronoExt.setDatiSpecDicom(sw.toString());
                                invioOggettoAsincronoExt.setVersioneDatiSpecifici(versioneDatiSpecifici);
                                //
                                datiSpecificiParser.parseXML(invioOggettoAsincronoExt, rispostaWs);
                            }
                        }
                    } // end ciclo for sulle unità documentarie
                }
            }
        } else if (rispostaWs.getSeverity() != SeverityEnum.ERROR && (invioOggettoAsincronoExt.getTiVersFile()
                .equals(Constants.TipoVersamento.ZIP_CON_XML_SACER.name())
                || invioOggettoAsincronoExt.getTiVersFile().equals(Constants.TipoVersamento.DA_TRASFORMARE.name()))) {
            rispostaControlli.reset();

            // $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
            // Inizio controlli su versioneDatiSpecifici
            // $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
            // Ricavo l'info se per il tipo oggetto specificato in input
            // sono definite le versioni del XSD dei dati specifici
            // e in caso verifico se una di esse è prevista
            String xsdString = null;
            // MEV27034 - l'indice xml è validato nella sua interezza, non più solo la speicifica parte dei dati
            // specifici.
            String xml = invioOggettoAsincronoExt.getInvioOggettoAsincronoInput().getXml();
            String cdVersioneXml = invioOggettoAsincronoExt.getInvioOggettoAsincronoInput().getCdVersioneXml();
            if (StringUtils.isNotBlank(cdVersioneXml) && StringUtils.isNotBlank(xml)) {
                // Sono definiti versione e xml per l'oggetto
                rispostaControlli.reset();
                if (rispostaWs.getSeverity() != SeverityEnum.ERROR) {
                    rispostaControlli = controlliInvioOggettoAsincrono
                            .verificaVersioneXsd(invioOggettoAsincronoExt.getIdTipoObject(), cdVersioneXml);
                    if (!rispostaControlli.isrBoolean()) {
                        setRispostaWsError(invioOggettoAsincronoExt, rispostaWs, rispostaControlli);
                    } else {
                        xsdString = rispostaControlli.getrString();
                        invioOggettoAsincronoExt.setIdXsdDatiSpec(rispostaControlli.getrLong());
                    }
                }

                if (rispostaWs.getSeverity() != SeverityEnum.ERROR) {
                    if (xsdString == null) {
                        rispostaControlli.setrBoolean(false);
                        rispostaControlli.setCodErr(MessaggiWSBundle.PING_SENDOBJ_XML_004);
                        rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.PING_SENDOBJ_XML_004,
                                oggetto.getDatiSpecifici().getVersioneDatiSpecifici(), "XSD non definito"));
                        setRispostaWsError(invioOggettoAsincronoExt, rispostaWs, rispostaControlli);
                    } else {
                        XmlFragmentValidator validator = new XmlFragmentValidator(rispostaWs, invioOggettoAsincronoExt);
                        validator.validateXmlFragment(xsdString, xml, MessaggiWSBundle.PING_SENDOBJ_XML_004,
                                cdVersioneXml);
                    }
                }
            }
        }
    } // end metodo

    private void setRispostaWsError(InvioOggettoAsincronoExt invioOggettoAsincronoExt,
            RispostaWSInvioOggettoAsincrono rispostaWs, RispostaControlli rispostaControlli) {
        rispostaWs.setSeverity(SeverityEnum.ERROR);
        rispostaWs.setErrorCode(rispostaControlli.getCodErr());
        rispostaWs.setErrorMessage(rispostaControlli.getDsErr());
        rispostaWs.getInvioOggettoAsincronoRisposta().setCdEsito(Constants.EsitoServizio.KO);
        rispostaWs.getInvioOggettoAsincronoRisposta().setCdErr(rispostaControlli.getCodErr());
        rispostaWs.getInvioOggettoAsincronoRisposta().setDsErr(rispostaControlli.getDsErr());
        invioOggettoAsincronoExt.setFlRegistraObject(false);
        invioOggettoAsincronoExt.setFlRegistraXMLObject(false);
        invioOggettoAsincronoExt.setFlRegistraDatiSpecDicom(false);
    }
}
