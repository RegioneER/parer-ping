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

package it.eng.sacerasi.job.preparaxml.ejb;

import java.io.StringWriter;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.text.WordUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import it.eng.parer.ws.xml.versReq.CamiciaFascicoloType;
import it.eng.parer.ws.xml.versReq.ChiaveType;
import it.eng.parer.ws.xml.versReq.ComponenteType;
import it.eng.parer.ws.xml.versReq.ConfigType;
import it.eng.parer.ws.xml.versReq.DatiFiscaliType;
import it.eng.parer.ws.xml.versReq.DatiSpecificiType;
import it.eng.parer.ws.xml.versReq.DocumentoCollegatoType;
import it.eng.parer.ws.xml.versReq.DocumentoType;
import it.eng.parer.ws.xml.versReq.FascicoloType;
import it.eng.parer.ws.xml.versReq.IntestazioneType;
import it.eng.parer.ws.xml.versReq.ObjectFactory;
import it.eng.parer.ws.xml.versReq.ProfiloArchivisticoType;
import it.eng.parer.ws.xml.versReq.ProfiloArchivisticoType.FascicoliSecondari;
import it.eng.parer.ws.xml.versReq.ProfiloDocumentoType;
import it.eng.parer.ws.xml.versReq.ProfiloUnitaDocumentariaType;
import it.eng.parer.ws.xml.versReq.StrutturaType;
import it.eng.parer.ws.xml.versReq.StrutturaType.Componenti;
import it.eng.parer.ws.xml.versReq.TipoConservazioneType;
import it.eng.parer.ws.xml.versReq.TipoSupportoType;
import it.eng.parer.ws.xml.versReq.UnitaDocumentaria;
import it.eng.parer.ws.xml.versReq.UnitaDocumentaria.Allegati;
import it.eng.parer.ws.xml.versReq.UnitaDocumentaria.Annessi;
import it.eng.parer.ws.xml.versReq.UnitaDocumentaria.Annotazioni;
import it.eng.parer.ws.xml.versReq.VersatoreType;
import it.eng.parer.ws.xml.versReqMultiMedia.ComponenteType.ForzaFormato;
import it.eng.parer.ws.xml.versReqMultiMedia.ComponenteType.ForzaHash;
import it.eng.parer.ws.xml.versReqMultiMedia.IndiceMM;
import it.eng.sacerasi.common.Constants;
import it.eng.sacerasi.entity.PigAttribDatiSpec;
import it.eng.sacerasi.entity.PigInfoDicom;
import it.eng.sacerasi.entity.PigObject;
import it.eng.sacerasi.entity.PigXsdDatiSpec;
import it.eng.sacerasi.exception.ParerInternalError;
import it.eng.sacerasi.job.preparaxml.dto.FileUnitaDoc;
import it.eng.sacerasi.job.preparaxml.dto.OggettoInCoda;
import it.eng.sacerasi.job.preparaxml.dto.UnitaDocObject;
import it.eng.sacerasi.ws.ejb.XmlContextCache;
import it.eng.sacerasixml.xsd.util.Utils;

@Stateless(mappedName = "ProduzioneXmlEjb")
@LocalBean
public class ProduzioneXmlEjb {

    private static final Logger log = LoggerFactory.getLogger(ProduzioneXmlEjb.class);

    // Singleton Ejb di gestione cache dei parser Castor
    @EJB
    XmlContextCache xmlContextCache;

    /**
     * Esegue la produzione degli xml di versamento e di indice multimedia con i dati contenuti nell'oggetto in coda
     *
     * @param oggetto
     *            l'oggetto in coda
     *
     * @throws ParerInternalError
     *             In caso di eccezione
     */
    public void produci(OggettoInCoda oggetto) throws ParerInternalError {
        switch (oggetto.getTipoVersamento()) {
        case NO_ZIP:
        case ZIP_NO_XML_SACER:
            // Eseguo la creazione dell'xml di versamento
            gestisciCreazioneXml(oggetto);
            break;
        case ZIP_CON_XML_SACER:
            // Eseguo solo la creazione dell'indice multimedia in quanto
            // l'xml è già stato recuperato in fase di preparazione
            gestisciRecuperoXml(oggetto);
            break;
        }
    }

    private void gestisciCreazioneXml(OggettoInCoda obj) throws ParerInternalError {
        for (UnitaDocObject udObj : obj.getListaUnitaDocObject()) {
            // Creo gli elementi per l'xml versamento e multimedia separatamente
            // i file invece li gestirà per entrambi nello stesso ciclo
            UnitaDocumentaria unitaDoc = gestisciXmlVersamento(obj, udObj);
            IndiceMM indiceMm = initXmlMultimedia(udObj, obj.getTipoVersamento());

            int allegCounter = 0;
            int annesCounter = 0;
            int annotCounter = 0;
            // Ciclo i file
            for (FileUnitaDoc fileUd : udObj.getListaFileUnitaDoc()) {
                // Popola i documenti per l'xml di versamento
                DocumentoType doc = null;
                if (fileUd.getRifPigTipoFileObject().getTiDocSacer().equals(Constants.DocTypeEnum.PRINCIPALE.name())) {
                    // CASO DOC PRINCIPALE
                    doc = unitaDoc.getDocumentoPrincipale();
                    doc.setIDDocumento(fileUd.getRifPigTipoFileObject().getTiDocSacer());
                } else if (fileUd.getRifPigTipoFileObject().getTiDocSacer()
                        .equals(Constants.DocTypeEnum.ALLEGATO.name())) {
                    // CASO ALLEGATO - Se è il primo istanzio la collection
                    if (allegCounter == 0) {
                        unitaDoc.setAllegati(new Allegati());
                    }
                    allegCounter++;
                    doc = new DocumentoType();
                    doc.setIDDocumento(fileUd.getRifPigTipoFileObject().getTiDocSacer() + allegCounter);
                    unitaDoc.getAllegati().getAllegato().add(doc);
                } else if (fileUd.getRifPigTipoFileObject().getTiDocSacer()
                        .equals(Constants.DocTypeEnum.ANNESSO.name())) {
                    // CASO ANNESSO - Se è il primo istanzio la collection
                    if (annesCounter == 0) {
                        unitaDoc.setAnnessi(new Annessi());
                    }
                    annesCounter++;
                    doc = new DocumentoType();
                    doc.setIDDocumento(fileUd.getRifPigTipoFileObject().getTiDocSacer() + annesCounter);
                    unitaDoc.getAnnessi().getAnnesso().add(doc);
                } else if (fileUd.getRifPigTipoFileObject().getTiDocSacer()
                        .equals(Constants.DocTypeEnum.ANNOTAZIONE.name())) {
                    // CASO ANNOTAZIONE - Se è il primo istanzio la collection
                    if (annotCounter == 0) {
                        unitaDoc.setAnnotazioni(new Annotazioni());
                    }
                    annotCounter++;
                    doc = new DocumentoType();
                    doc.setIDDocumento(fileUd.getRifPigTipoFileObject().getTiDocSacer() + annotCounter);
                    unitaDoc.getAnnotazioni().getAnnotazione().add(doc);
                }
                // Eseguo la popolazione del documento, qualsiasi esso sia
                // (PRINCIPALE,ALLEGATO,ANNESSO,ANNOTAZIONE)
                popolaDocumento(doc, obj, fileUd);

                // Genera i componenti per l'indice multimedia
                it.eng.parer.ws.xml.versReqMultiMedia.ComponenteType comp = new it.eng.parer.ws.xml.versReqMultiMedia.ComponenteType();
                // gestisce i componenti per l'xml multimedia
                popolaComponenteMMNoZipZipNoXml(comp, fileUd);
                indiceMm.getComponenti().getComponente().add(comp);
            }
            unitaDoc.setNumeroAllegati(allegCounter);
            unitaDoc.setNumeroAnnessi(annesCounter);
            unitaDoc.setNumeroAnnotazioni(annotCounter);

            udObj.setUnitaDocumentariaXmlBean(unitaDoc);
            udObj.setIndiceMMXmlBean(indiceMm);

            try {
                marshallXmlVers(unitaDoc, udObj);
                log.debug(udObj.getUnitaDocumentariaXml());
                marshallXmlMm(indiceMm, udObj);
                log.debug(udObj.getIndiceMMXml());
            } catch (Exception ex) {
                throw new ParerInternalError(ex);
            }
        }
    }

    private void marshallXmlVers(UnitaDocumentaria unitaDoc, UnitaDocObject udObj) throws JAXBException {
        StringWriter tmpWriter = new StringWriter();
        // Eseguo il marshalling degli oggetti creati per salvarli poi nell'oggetto UnitaDocObject
        Marshaller udMarshaller = xmlContextCache.getVersReqCtxforUD().createMarshaller();
        udMarshaller.marshal(unitaDoc, tmpWriter);
        tmpWriter.flush();
        udObj.setUnitaDocumentariaXml(tmpWriter.toString());
    }

    private void marshallXmlMm(IndiceMM indiceMm, UnitaDocObject udObj) throws JAXBException {
        StringWriter tmpWriter = new StringWriter();
        // Eseguo il marshalling dell'indice per salvarlo poi nell'oggetto UnitaDocObject
        Marshaller indexMarshaller = xmlContextCache.getVersReqMMCtxforIndiceMM().createMarshaller();
        indexMarshaller.marshal(indiceMm, tmpWriter);
        tmpWriter.flush();
        udObj.setIndiceMMXml(tmpWriter.toString());
    }

    // Istanzio l'oggetto UnitaDocumentaria e ne inizializzo i campi
    private UnitaDocumentaria initUnitaDocumentaria() {
        UnitaDocumentaria unitaDoc = new UnitaDocumentaria();
        unitaDoc.setIntestazione(new IntestazioneType());
        unitaDoc.setConfigurazione(new ConfigType());
        unitaDoc.setDocumentoPrincipale(new DocumentoType());
        return unitaDoc;
    }

    // Popolo l'intestazione con i dati contenuti in pigObject e nell'unitaDocObject
    private void popolaIntestazione(IntestazioneType intestazione, PigObject pigObject, UnitaDocObject udObject) {
        intestazione.setVersione(udObject.getVersioneWsVersamento());

        intestazione.setChiave(new ChiaveType());
        intestazione.getChiave().setAnno(udObject.getChiaveUd().getAnno().intValue());
        intestazione.getChiave().setNumero(udObject.getChiaveUd().getNumero());
        intestazione.getChiave().setTipoRegistro(udObject.getChiaveUd().getRegistro());

        intestazione.setTipologiaUnitaDocumentaria(pigObject.getPigTipoObject().getNmTipoUnitaDocSacer());

        intestazione.setVersatore(new VersatoreType());
        intestazione.getVersatore().setAmbiente(udObject.getNmAmbienteSacer());
        intestazione.getVersatore().setEnte(udObject.getNmEnteSacer());
        intestazione.getVersatore().setStruttura(udObject.getNmStrutSacer());
        intestazione.getVersatore().setUserID(udObject.getNmUserIdSacer());
    }

    // Popolo la configurazione con i flag contenuti nella tabella pigTipoObject relativa all'oggetto versato
    private void popolaConfigurazione(ConfigType configurazione, PigObject pigObject) {
        configurazione.setForzaAccettazione(
                pigObject.getPigTipoObject().getFlForzaAccettazioneSacer().equals(Constants.DB_TRUE));
        configurazione
                .setForzaCollegamento(pigObject.getPigTipoObject().getFlForzaCollegamento().equals(Constants.DB_TRUE));
        configurazione.setForzaConservazione(
                pigObject.getPigTipoObject().getFlForzaConservazione().equals(Constants.DB_TRUE));
        configurazione
                .setTipoConservazione(TipoConservazioneType.valueOf(pigObject.getPigTipoObject().getTiConservazione()));
    }

    // Popolo il profilo archivistico se presente all'interno dell'xml di versamento async
    private void popolaProfiloArchivistico(ProfiloArchivisticoType profiloArchivisticoSacer,
            it.eng.sacerasi.ws.xml.invioAsync.ProfiloArchivisticoType profiloArchivisticoAsync) {
        if (profiloArchivisticoAsync != null) {
            profiloArchivisticoSacer.setFascicoliSecondari(new FascicoliSecondari());
            profiloArchivisticoSacer.setFascicoloPrincipale(new CamiciaFascicoloType());

            // Popolo il fascicolo principale
            profiloArchivisticoSacer.getFascicoloPrincipale()
                    .setClassifica(profiloArchivisticoAsync.getFascicoloPrincipale().getClassifica());
            profiloArchivisticoSacer.getFascicoloPrincipale().setFascicolo(new FascicoloType());
            profiloArchivisticoSacer.getFascicoloPrincipale().setSottoFascicolo(new FascicoloType());

            profiloArchivisticoSacer.getFascicoloPrincipale().getFascicolo().setIdentificativo(
                    profiloArchivisticoAsync.getFascicoloPrincipale().getFascicolo().getIdentificativo());
            // Nuovo Jaxb
            ObjectFactory fact = new ObjectFactory();
            JAXBElement<String> str = fact.createFascicoloTypeOggetto(
                    profiloArchivisticoAsync.getFascicoloPrincipale().getFascicolo().getOggetto());
            profiloArchivisticoSacer.getFascicoloPrincipale().getFascicolo().setOggetto(str);
            profiloArchivisticoSacer.getFascicoloPrincipale().getSottoFascicolo().setIdentificativo(
                    profiloArchivisticoAsync.getFascicoloPrincipale().getSottoFascicolo().getIdentificativo());
            // Nuovo JAXB
            JAXBElement<String> str2 = fact.createFascicoloTypeOggetto(
                    profiloArchivisticoAsync.getFascicoloPrincipale().getSottoFascicolo().getOggetto());
            profiloArchivisticoSacer.getFascicoloPrincipale().getSottoFascicolo().setOggetto(str2);

            // Popolo i fascicoli secondari
            // Nuovo per Jaxb
            if (profiloArchivisticoAsync.getFascicoliSecondari().getFascicoloSecondario() != null) {
                profiloArchivisticoSacer.setFascicoliSecondari(new FascicoliSecondari());
            }
            for (it.eng.sacerasi.ws.xml.invioAsync.CamiciaFascicoloType fasc : profiloArchivisticoAsync
                    .getFascicoliSecondari().getFascicoloSecondario()) {
                // Nuovo JAXB
                CamiciaFascicoloType camic = new CamiciaFascicoloType();
                camic.setClassifica(fasc.getClassifica());
                camic.setFascicolo(new FascicoloType());
                camic.setSottoFascicolo(new FascicoloType());
                camic.getFascicolo().setIdentificativo(fasc.getFascicolo().getIdentificativo());
                JAXBElement<String> str3 = fact.createFascicoloTypeOggetto(fasc.getFascicolo().getOggetto());
                camic.getFascicolo().setOggetto(str3);
                camic.getSottoFascicolo().setIdentificativo(fasc.getSottoFascicolo().getIdentificativo());
                JAXBElement<String> str4 = fact.createFascicoloTypeOggetto(fasc.getSottoFascicolo().getOggetto());
                camic.getSottoFascicolo().setOggetto(str4);
                profiloArchivisticoSacer.getFascicoliSecondari().getFascicoloSecondario().add(camic);
            }
        }
    }

    private void popolaProfiloUnitaDocumentaria(ProfiloUnitaDocumentariaType profiloUdSacer,
            it.eng.sacerasi.ws.xml.invioAsync.ProfiloUnitaDocumentariaType profiloUdAsync, OggettoInCoda obj) {
        if (profiloUdAsync != null) {
            profiloUdSacer
                    .setData(Utils.getStandardXsDateFormat(profiloUdAsync.getData().toGregorianCalendar().getTime()));
            profiloUdSacer.setOggetto(profiloUdAsync.getOggetto());
        } else {
            if (obj.getTipoOggetto() == Constants.TipiOggetto.STUDIO_DICOM) {
                if (obj.getRifPigObject().getPigInfoDicoms().get(0) != null) {
                    Date dtStudyDate = obj.getRifPigObject().getPigInfoDicoms().get(0).getDtStudyDate();
                    String modality = obj.getRifPigObject().getPigInfoDicoms().get(0).getDlListaModalityInStudy();
                    profiloUdSacer.setOggetto(modality);
                    profiloUdSacer.setData(Utils.getStandardXsDateFormat(dtStudyDate));
                }
            }
        }
    }

    private void popolaDatiSpecifici(DatiSpecificiType datiSpecificiSacer,
            it.eng.sacerasi.ws.xml.invioAsync.DatiSpecificiType datiSpecificiAsync, OggettoInCoda obj)
            throws ParerInternalError {
        // Verifico la presenza di dati specifici
        if (datiSpecificiAsync != null) {

            // Verifico il tipo oggetto, se DICOM gestisco verificando gli attributi
            // Altrimenti copio per intero l'oggetto dati specifici dal versamento async
            if (obj.getTipoOggetto() == Constants.TipiOggetto.STUDIO_DICOM) {
                String cdVersioneDatiSpecDicom = obj.getRifPigObject().getPigInfoDicoms().get(0)
                        .getCdVersioneDatiSpecDicom();
                datiSpecificiSacer.setVersioneDatiSpecifici(cdVersioneDatiSpecDicom);
                for (PigXsdDatiSpec xsdDatiSpec : obj.getRifPigObject().getPigTipoObject().getPigXsdDatiSpecs()) {
                    // Ciclo gli xsd dei dati specifici del tipo oggetto finché non ottengo quello
                    // con la versione specificata
                    if (xsdDatiSpec.getCdVersioneXsd().equals(cdVersioneDatiSpecDicom)) {

                        List<PigAttribDatiSpec> pigAttribDatiSpecs = xsdDatiSpec.getPigAttribDatiSpecs();
                        try {
                            for (PigAttribDatiSpec att : pigAttribDatiSpecs) {
                                // Per ogni attributo dell'xsd che ha il campo fl_vers_sacer a 1,
                                // richiama il metodo getter della tabella PigInfoDicom relativa all'attributo con un
                                // invoke
                                // e associa ad ogni attributo il valore della riga
                                if (att.getFlVersSacer().equals(Constants.DB_TRUE)) {
                                    DateFormat timeStampDf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
                                    DateFormat dateDf = new SimpleDateFormat("yyyy-MM-dd");

                                    // Estraggo la classe di PigInfoDicom
                                    Class<?> c = Class.forName(PigInfoDicom.class.getName());

                                    char[] delimiters = { '_' };
                                    // Istanzio il metodo definito come get + nome della colonna da estrarre degli
                                    // attributi
                                    // della PigInfoDicom, sostituendo gli '_' e capitalizzando i termini
                                    // ES: cd_AET_nodo_dicom -> getCdAetNodoDicom
                                    Method method = c.getDeclaredMethod("get" + WordUtils
                                            .capitalizeFully(att.getNmColDatiSpec(), delimiters).replace("_", ""));
                                    // Invoca il metodo ottenuto sulla tabella PigInfoDicom dell'oggetto per ottenere il
                                    // valore di quell'attributo
                                    Object value = method.invoke(obj.getRifPigObject().getPigInfoDicoms().get(0));
                                    if (value != null) {
                                        // NUOVO CODICE JAXB
                                        // Crea un elemento per il campo Any e lo aggiunge ai dati specifici
                                        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                                        // XXE: This is the PRIMARY defense. If DTDs (doctypes) are disallowed,
                                        // almost all XML entity attacks are prevented
                                        final String FEATURE = "http://apache.org/xml/features/disallow-doctype-decl";
                                        dbf.setFeature(FEATURE, true);
                                        dbf.setFeature("http://xml.org/sax/features/external-general-entities", false);

                                        dbf.setFeature("http://xml.org/sax/features/external-parameter-entities",
                                                false);
                                        // ... and these as well, per Timothy Morgan's 2014 paper:
                                        // "XML Schema, DTD, and Entity Attacks" (see reference below)
                                        dbf.setXIncludeAware(false);
                                        dbf.setExpandEntityReferences(false);
                                        // As stated in the documentation, "Feature for Secure Processing (FSP)" is the
                                        // central mechanism that will
                                        // help you safeguard XML processing. It instructs XML processors, such as
                                        // parsers, validators,
                                        // and transformers, to try and process XML securely, and the FSP can be used as
                                        // an alternative to
                                        // dbf.setExpandEntityReferences(false); to allow some safe level of Entity
                                        // Expansion
                                        // Exists from JDK6.
                                        dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
                                        // ... and, per Timothy Morgan:
                                        // "If for some reason support for inline DOCTYPEs are a requirement, then
                                        // ensure the entity settings are disabled (as shown above) and beware that SSRF
                                        // attacks
                                        // (http://cwe.mitre.org/data/definitions/918.html) and denial
                                        // of service attacks (such as billion laughs or decompression bombs via "jar:")
                                        // are a risk."
                                        DocumentBuilder docBuilder = dbf.newDocumentBuilder();
                                        Document doc = docBuilder.newDocument();
                                        Element elem = doc.createElement(att.getNmAttribDatiSpec());
                                        if (value instanceof Date && att.getTiDatatypeCol()
                                                .equals(Constants.AttribDatiSpecDataType.DATETIME.name())) {
                                            elem.setTextContent(timeStampDf.format(value));
                                        } else if (value instanceof Date && att.getTiDatatypeCol()
                                                .equals(Constants.AttribDatiSpecDataType.DATA.name())) {
                                            elem.setTextContent(dateDf.format(value));
                                        } else {
                                            elem.setTextContent(value.toString());
                                        }
                                        datiSpecificiSacer.getAny().add(elem);
                                    }
                                }
                            }
                        } catch (Exception ex) {
                            throw new ParerInternalError(ex);
                        }
                    }
                }
            } else {
                // Non è dicom, copio per intero l'oggetto dati specifici dal versamento async
                datiSpecificiSacer.setVersioneDatiSpecifici(datiSpecificiAsync.getVersioneDatiSpecifici());
                datiSpecificiSacer.getAny().addAll(datiSpecificiAsync.getAny());
            }
        }
    }

    private UnitaDocumentaria gestisciXmlVersamento(OggettoInCoda obj, UnitaDocObject udObj)
            throws ParerInternalError, IndexOutOfBoundsException {
        UnitaDocumentaria unitaDoc = initUnitaDocumentaria();
        popolaIntestazione(unitaDoc.getIntestazione(), obj.getRifPigObject(), udObj);
        popolaConfigurazione(unitaDoc.getConfigurazione(), obj.getRifPigObject());
        // Ciclo sulle unita documentarie dell'xml per caricare i dati dell'unita documentaria corretta
        for (it.eng.sacerasi.ws.xml.invioAsync.UnitaDocumentariaType unitaDocumentaria : obj.getParsedListaUnitaDoc()
                .getUnitaDocumentaria()) {

            if (obj.getTipoOggetto() == Constants.TipiOggetto.STUDIO_DICOM) {
                popolaDatiUnitaDoc(unitaDocumentaria, unitaDoc, obj);
            } else {
                String xmlChiave = unitaDocumentaria.getChiave().getTipoRegistro() + "^"
                        + unitaDocumentaria.getChiave().getAnno() + "^" + unitaDocumentaria.getChiave().getNumero();
                String objChiave = udObj.getChiaveUd().getRegistro() + "^" + udObj.getChiaveUd().getAnno() + "^"
                        + udObj.getChiaveUd().getNumero();
                if (xmlChiave.equals(objChiave)) {
                    popolaDatiUnitaDoc(unitaDocumentaria, unitaDoc, obj);
                    break;
                }
            }
        }

        return unitaDoc;
    }

    private IndiceMM initXmlMultimedia(UnitaDocObject udObj, Constants.TipoVersamento tipoVers) {
        IndiceMM indiceMm = initIndiceMm();
        switch (tipoVers) {
        case ZIP_CON_XML_SACER:
        case ZIP_NO_XML_SACER:
            // scrivo nel codice oggetto il nome originale del file zip,
            // questa informazione verrà ignorata da SACER
            indiceMm.setCodiceOggetto(udObj.getUrnFileZip());
            break;
        }
        return indiceMm;
    }

    private void popolaDatiUnitaDoc(it.eng.sacerasi.ws.xml.invioAsync.UnitaDocumentariaType unitaDocumentaria,
            UnitaDocumentaria unitaDoc, OggettoInCoda obj) throws ParerInternalError {
        // Ho trovato l'UD corretta, verifico la presenza dei dati e nel caso popolo l'oggetto
        if (unitaDocumentaria.getProfiloArchivistico() != null) {
            unitaDoc.setProfiloArchivistico(new ProfiloArchivisticoType());
            popolaProfiloArchivistico(unitaDoc.getProfiloArchivistico(), unitaDocumentaria.getProfiloArchivistico());
        }
        if (unitaDocumentaria.getProfiloUnitaDocumentaria() != null
                || obj.getTipoOggetto() == Constants.TipiOggetto.STUDIO_DICOM) {
            unitaDoc.setProfiloUnitaDocumentaria(new ProfiloUnitaDocumentariaType());
            popolaProfiloUnitaDocumentaria(unitaDoc.getProfiloUnitaDocumentaria(),
                    unitaDocumentaria.getProfiloUnitaDocumentaria(), obj);
        }
        if (unitaDocumentaria.getDatiSpecifici() != null) {
            it.eng.parer.ws.xml.versReq.ObjectFactory fact = new it.eng.parer.ws.xml.versReq.ObjectFactory();
            JAXBElement<DatiSpecificiType> elem = fact.createUnitaDocumentariaDatiSpecifici(new DatiSpecificiType());
            unitaDoc.setDatiSpecifici(elem);
            popolaDatiSpecifici(unitaDoc.getDatiSpecifici().getValue(), unitaDocumentaria.getDatiSpecifici().getValue(),
                    obj);
        }
        if (unitaDocumentaria.getDocumentiCollegati() != null) {
            unitaDoc.setDocumentiCollegati(new DocumentoCollegatoType());
            popolaDocumentiCollegati(unitaDoc.getDocumentiCollegati(), unitaDocumentaria.getDocumentiCollegati());
        }
    }

    /**
     * Inner class atta a ordinare gli attributi dati specifici in ordine di campo NiOrd
     */
    public class PigAttribDatiSpecComparator implements Comparator<PigAttribDatiSpec> {

        @Override
        public int compare(PigAttribDatiSpec c1, PigAttribDatiSpec c2) {
            if (c1.getNiOrd().longValue() == c2.getNiOrd().longValue()) {
                return 0;
            } else if (c1.getNiOrd().longValue() > c2.getNiOrd().longValue()) {
                return 1;
            } else {
                return -1;
            }
        }
    }

    private void popolaDocumentiCollegati(DocumentoCollegatoType documentiCollegatiSacer,
            it.eng.sacerasi.ws.xml.invioAsync.DocumentoCollegatoType documentiCollegatiAsync) {
        if (documentiCollegatiAsync != null) {
            for (it.eng.sacerasi.ws.xml.invioAsync.DocumentoCollegatoType.DocumentoCollegato doc : documentiCollegatiAsync
                    .getDocumentoCollegato()) {
                it.eng.parer.ws.xml.versReq.DocumentoCollegatoType.DocumentoCollegato docSacer = new it.eng.parer.ws.xml.versReq.DocumentoCollegatoType.DocumentoCollegato();
                docSacer.setDescrizioneCollegamento(doc.getDescrizioneCollegamento());
                docSacer.setChiaveCollegamento(new ChiaveType());
                docSacer.getChiaveCollegamento().setAnno(doc.getChiaveCollegamento().getAnno());
                docSacer.getChiaveCollegamento().setNumero(doc.getChiaveCollegamento().getNumero());
                docSacer.getChiaveCollegamento().setTipoRegistro(doc.getChiaveCollegamento().getTipoRegistro());
                documentiCollegatiSacer.getDocumentoCollegato().add(docSacer);
            }
        }
    }

    private void popolaDocumento(DocumentoType doc, OggettoInCoda obj, FileUnitaDoc fileUd) {
        // Popolo il documento con i dati contenuti nell'oggetto in coda e nel fileUnitaDoc
        // Qualsiasi esso sia, Doc principale, allegato, annesso o annotazione
        doc.setTipoDocumento(fileUd.getRifPigTipoFileObject().getNmTipoDocSacer());

        if (fileUd.getParsedFileType() != null && fileUd.getParsedFileType().getProfiloDocumento() != null) {
            doc.setProfiloDocumento(new ProfiloDocumentoType());
            doc.getProfiloDocumento().setAutore(fileUd.getParsedFileType().getProfiloDocumento().getAutore());
            doc.getProfiloDocumento().setDescrizione(fileUd.getParsedFileType().getProfiloDocumento().getDescrizione());
        } else {
            if (obj.getTipoOggetto() == Constants.TipiOggetto.STUDIO_DICOM) {
                doc.setProfiloDocumento(new ProfiloDocumentoType());
                if (obj.getRifPigObject().getPigInfoDicoms().get(0) != null) {
                    doc.getProfiloDocumento()
                            .setAutore(obj.getRifPigObject().getPigInfoDicoms().get(0).getDsInstitutionName());
                    doc.getProfiloDocumento()
                            .setDescrizione(obj.getRifPigObject().getPigInfoDicoms().get(0).getDlStudyDescription());
                }
            }
        }
        if (fileUd.getParsedFileType() != null && fileUd.getParsedFileType().getDatiSpecifici() != null) {
            it.eng.parer.ws.xml.versReq.ObjectFactory fac = new it.eng.parer.ws.xml.versReq.ObjectFactory();
            doc.setDatiSpecifici(fac.createDocumentoTypeDatiSpecifici(new DatiSpecificiType()));
            doc.getDatiSpecifici().getValue().setVersioneDatiSpecifici(
                    fileUd.getParsedFileType().getDatiSpecifici().getValue().getVersioneDatiSpecifici());
            doc.getDatiSpecifici().getValue().getAny()
                    .addAll(fileUd.getParsedFileType().getDatiSpecifici().getValue().getAny());
        }
        if (fileUd.getParsedFileType() != null && fileUd.getParsedFileType().getDatiFiscali() != null) {
            doc.setDatiFiscali(new DatiFiscaliType());
            doc.getDatiFiscali().setCF(fileUd.getParsedFileType().getDatiFiscali().getCF());
            doc.getDatiFiscali().setCognome(fileUd.getParsedFileType().getDatiFiscali().getCognome());
            doc.getDatiFiscali().setDataEmissione(fileUd.getParsedFileType().getDatiFiscali().getDataEmissione());
            doc.getDatiFiscali()
                    .setDataTermineEmissione(fileUd.getParsedFileType().getDatiFiscali().getDataTermineEmissione());
            doc.getDatiFiscali().setDenominazione(fileUd.getParsedFileType().getDatiFiscali().getDenominazione());
            doc.getDatiFiscali().setNome(fileUd.getParsedFileType().getDatiFiscali().getNome());
            doc.getDatiFiscali()
                    .setNumeroProgressivo(fileUd.getParsedFileType().getDatiFiscali().getNumeroProgressivo());
            doc.getDatiFiscali().setPIVA(fileUd.getParsedFileType().getDatiFiscali().getPIVA());
            doc.getDatiFiscali().setPeriodoFiscale(fileUd.getParsedFileType().getDatiFiscali().getPeriodoFiscale());
            doc.getDatiFiscali().setRegistro(fileUd.getParsedFileType().getDatiFiscali().getRegistro());
        }
        doc.setStrutturaOriginale(new StrutturaType());
        doc.getStrutturaOriginale().setTipoStruttura(fileUd.getRifPigTipoFileObject().getNmTipoStrutDocSacer());

        doc.getStrutturaOriginale().setComponenti(new Componenti());

        ComponenteType comp = new ComponenteType();
        comp.setID(fileUd.getIdFile());
        comp.setOrdinePresentazione(1);
        comp.setTipoComponente(fileUd.getRifPigTipoFileObject().getNmTipoCompDocSacer());
        comp.setTipoSupportoComponente(TipoSupportoType.FILE);
        comp.setNomeComponente(fileUd.getNomeFile());
        comp.setFormatoFileVersato(fileUd.getRifPigTipoFileObject().getNmFmtFileVersSacer());
        comp.setHashVersato(fileUd.getHashFile());
        if (fileUd.getParsedFileType() != null) {
            comp.setUtilizzoDataFirmaPerRifTemp(fileUd.getParsedFileType().isUtilizzoDataFirmaPerRifTemp());
        }
        if (fileUd.getParsedFileType() != null && fileUd.getParsedFileType().getRiferimentoTemporale() != null) {
            comp.setRiferimentoTemporale(fileUd.getParsedFileType().getRiferimentoTemporale());
        }
        if (fileUd.getParsedFileType() != null
                && fileUd.getParsedFileType().getDescrizioneRiferimentoTemporale() != null) {
            comp.setDescrizioneRiferimentoTemporale(fileUd.getParsedFileType().getDescrizioneRiferimentoTemporale());
        }

        doc.getStrutturaOriginale().getComponenti().getComponente().add(comp);
    }

    private IndiceMM initIndiceMm() {
        IndiceMM indiceMm = new IndiceMM();
        indiceMm.setVersione(Constants.VERSIONE_XML_MM);
        indiceMm.setApplicativoChiamante(Constants.APP_CHIAMANTE);
        indiceMm.setComponenti(new it.eng.parer.ws.xml.versReqMultiMedia.IndiceMM.Componenti());
        return indiceMm;
    }

    private void popolaComponenteMMZipConXml(it.eng.parer.ws.xml.versReqMultiMedia.ComponenteType comp,
            FileUnitaDoc fileUd) {
        comp.setID(fileUd.getIdFile());
        comp.setURNFile(fileUd.getUrnFile());
        if (fileUd.getUrnFileInZip() != null) {
            // il tag PathOggetto, ignorato da SACER, contiene l'indirizzo orginale del file all'interno del file ZIP
            comp.setPathOggetto(fileUd.getUrnFileInZip());
        }
        comp.setVerificaFirmeFormati(true);
        comp.setCalcolaHash(true);
    }

    private void popolaComponenteMMNoZipZipNoXml(it.eng.parer.ws.xml.versReqMultiMedia.ComponenteType comp,
            FileUnitaDoc fileUd) {
        comp.setID(fileUd.getIdFile());
        comp.setURNFile(fileUd.getUrnFile());
        if (fileUd.getUrnFileInZip() != null) {
            // il tag PathOggetto, ignorato da SACER, contiene l'indirizzo orginale del file all'interno del file ZIP
            comp.setPathOggetto(fileUd.getUrnFileInZip());
        }
        boolean verificaFormatiSacer = fileUd.getRifPigTipoFileObject().getFlVerFirmaFmtSacer()
                .equals(Constants.DB_TRUE);
        boolean calcolaHashSacer = fileUd.getRifPigTipoFileObject().getFlCalcHashSacer().equals(Constants.DB_TRUE);
        comp.setVerificaFirmeFormati(verificaFormatiSacer);

        if (!verificaFormatiSacer) {
            comp.setForzaFormato(new ForzaFormato());
            comp.getForzaFormato().setFormatoStandard(fileUd.getRifPigTipoFileObject().getNmFmtFileCalcSacer());
            comp.getForzaFormato()
                    .setFormatoRappresentazioneEsteso(fileUd.getRifPigTipoFileObject().getDsFmtRapprEstesoCalcSacer());
            comp.getForzaFormato()
                    .setFormatoRappresentazioneCompatto(fileUd.getRifPigTipoFileObject().getDsFmtRapprCalcSacer());
        }
        comp.setCalcolaHash(calcolaHashSacer);

        if (!calcolaHashSacer) {
            comp.setForzaHash(new ForzaHash());
            comp.getForzaHash().setAlgoritmo(fileUd.getTipoHashFile());
            comp.getForzaHash().setEncoding(fileUd.getEncodingFile());
            comp.getForzaHash().setHash(fileUd.getHashFile());
        }
    }

    private void gestisciRecuperoXml(OggettoInCoda obj) throws ParerInternalError {
        for (UnitaDocObject udObj : obj.getListaUnitaDocObject()) {
            IndiceMM indiceMm = initXmlMultimedia(udObj, obj.getTipoVersamento());
            for (FileUnitaDoc fileUd : udObj.getListaFileUnitaDoc()) {
                // Genera i componenti per l'indice multimedia
                it.eng.parer.ws.xml.versReqMultiMedia.ComponenteType comp = new it.eng.parer.ws.xml.versReqMultiMedia.ComponenteType();
                // gestisce i componenti per l'xml multimedia
                popolaComponenteMMZipConXml(comp, fileUd);
                indiceMm.getComponenti().getComponente().add(comp);
            }
            udObj.setIndiceMMXmlBean(indiceMm);
            try {
                marshallXmlMm(indiceMm, udObj);
            } catch (Exception ex) {
                throw new ParerInternalError(ex);
            }
        }
    }
}
