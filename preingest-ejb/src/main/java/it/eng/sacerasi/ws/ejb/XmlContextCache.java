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

package it.eng.sacerasi.ws.ejb;

import it.eng.parerxml.xsd.FileXSD;
import it.eng.parerxml.xsd.FileXSDUtil;
import it.eng.sacerasi.sisma.xml.invioSisma.ProgettiSisma;
import it.eng.sacerasi.su.xml.invioSU.StrumentiUrbanistici;
import it.eng.sacerasi.ws.xml.datiSpecDicom.DatiSpecificiType;
import it.eng.sacerasi.ws.xml.datiSpecFiltri.ListaFiltriType;
import it.eng.sacerasi.ws.xml.datiSpecOrder.ListaDatiSpecificiOrderType;
import it.eng.sacerasi.ws.xml.datiSpecOut.ListaDatiSpecificiOutType;
import it.eng.sacerasi.ws.xml.datiSpecResult.ListaValoriDatiSpecificiType;
import it.eng.sacerasi.ws.xml.invioAsync.ListaUnitaDocumentarieType;
import it.eng.sacerasi.ws.xml.invioDaTrasf.OggettoType;
import it.eng.sacerasi.xml.unitaDocumentaria.UnitaDocumentariaType;
import javax.annotation.PostConstruct;
import javax.ejb.LocalBean;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

/**
 *
 * @author Fioravanti_F
 */
@Singleton
@LocalBean
@Startup
public class XmlContextCache {

    private static final Logger log = LoggerFactory.getLogger(XmlContextCache.class);
    // JAXB
    // Annullamento versamenti
    JAXBContext richAnnVersCtx_RichiestaAnnullamentoVersamenti = null;
    Schema richAnnVersSchema = null;
    JAXBContext esitoAnnVersCtx_EsitoRichiestaAnnullamentoVersamenti = null;
    Schema esitoRichAnnVersSchema = null;

    // Versameto
    JAXBContext versReqCtx_UD = null;
    JAXBContext versRespCtx_EsitoVersamento = null;
    JAXBContext versRespStatoCtx_StatoConservazione = null;
    JAXBContext versReqStatoCtx_Recupero = null;

    Schema versReqSchema = null;
    Schema versReqStatoSchema = null;
    Schema versRespSchema = null;

    // MultiMedia
    JAXBContext versReqMMCtx_IndiceMM = null;
    JAXBContext versReqStatoMMCtx_IndiceMM = null;

    // PING
    JAXBContext versReqAsyncCtx_ListaUnitaDocumentarie = null;
    Schema invioAsyncSchema = null;
    JAXBContext versReqAsyncDaTrasfCtx_Oggetto = null;
    Schema invioAsyncDaTrasfSchema = null;

    // datispecFiltri
    JAXBContext datispecFiltriCtx_ListaFiltri = null;
    Schema datispecFiltriSchema = null;
    // datispecOut
    JAXBContext datispecOutCtx_ListaDatiSpecificiOut = null;
    Schema datispecOutSchema = null;
    // datispecOrder
    JAXBContext datispecOrderCtx_ListaDatiSpecificiOrder = null;
    Schema datispecOrderSchema = null;
    // datispecResult
    JAXBContext datispecResultCtx_ListaValoriDatiSpecifici = null;

    JAXBContext versReqAsyncDSDicomCtx_DatiSpecifici = null;
    Schema datiSpecificiDicomSchema = null;

    JAXBContext invioSUCtx_InvioSU = null;
    Schema invioSUSchema = null;

    JAXBContext invioSismaCtx_InvioSisma = null;
    Schema invioSismaSchema = null;

    JAXBContext unitaDocumentariaCtx_UnitaDocumentaria = null;
    Schema unitaDocumentariaSchema = null;

    @PostConstruct
    protected void initSingleton() {
        try {
            log.info("Inizializzazione singleton XMLContext...");

            SchemaFactory schemaFctry = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

            // INIZIO SERVIZI PING
            // Invio Async
            versReqAsyncCtx_ListaUnitaDocumentarie = JAXBContext.newInstance(ListaUnitaDocumentarieType.class,
                    it.eng.sacerasi.ws.xml.invioAsync.ObjectFactory.class);
            invioAsyncSchema = schemaFctry.newSchema(
                    it.eng.sacerasixml.xsd.FileXSDUtil.getURLFileXSD(it.eng.sacerasixml.xsd.FileXSD.INVIO_ASYNC_XSD));
            // Invio da trasf
            versReqAsyncDaTrasfCtx_Oggetto = JAXBContext.newInstance(OggettoType.class,
                    it.eng.sacerasi.ws.xml.invioDaTrasf.ObjectFactory.class);
            invioAsyncDaTrasfSchema = schemaFctry.newSchema(it.eng.sacerasixml.xsd.FileXSDUtil
                    .getURLFileXSD(it.eng.sacerasixml.xsd.FileXSD.INVIO_DA_TRASF_XSD));
            // DatiSpecFiltri
            datispecFiltriCtx_ListaFiltri = JAXBContext.newInstance(ListaFiltriType.class,
                    it.eng.sacerasi.ws.xml.datiSpecFiltri.ObjectFactory.class);
            datispecFiltriSchema = schemaFctry.newSchema(it.eng.sacerasixml.xsd.FileXSDUtil
                    .getURLFileXSD(it.eng.sacerasixml.xsd.FileXSD.DATI_SPEC_FILTRI_XSD));
            // DatiSpecOut
            datispecOutCtx_ListaDatiSpecificiOut = JAXBContext.newInstance(ListaDatiSpecificiOutType.class,
                    it.eng.sacerasi.ws.xml.datiSpecOut.ObjectFactory.class);
            datispecOutSchema = schemaFctry.newSchema(
                    it.eng.sacerasixml.xsd.FileXSDUtil.getURLFileXSD(it.eng.sacerasixml.xsd.FileXSD.DATI_SPEC_OUT_XSD)); // DatiSpecOut
            // DatiSpecOrder
            datispecOrderCtx_ListaDatiSpecificiOrder = JAXBContext.newInstance(ListaDatiSpecificiOrderType.class,
                    it.eng.sacerasi.ws.xml.datiSpecOrder.ObjectFactory.class);
            datispecOrderSchema = schemaFctry.newSchema(it.eng.sacerasixml.xsd.FileXSDUtil
                    .getURLFileXSD(it.eng.sacerasixml.xsd.FileXSD.DATI_SPEC_ORDER_XSD));
            // DatiSpecResult
            datispecResultCtx_ListaValoriDatiSpecifici = JAXBContext.newInstance(ListaValoriDatiSpecificiType.class,
                    it.eng.sacerasi.ws.xml.datiSpecResult.ObjectFactory.class);
            // DatiSpecDicom
            versReqAsyncDSDicomCtx_DatiSpecifici = JAXBContext.newInstance(DatiSpecificiType.class,
                    it.eng.sacerasi.ws.xml.datiSpecDicom.ObjectFactory.class);
            datiSpecificiDicomSchema = schemaFctry.newSchema(it.eng.sacerasixml.xsd.FileXSDUtil
                    .getURLFileXSD(it.eng.sacerasixml.xsd.FileXSD.DATI_SPEC_DICOM_XSD));
            // Invio Strumento Urbanistico
            invioSUCtx_InvioSU = JAXBContext.newInstance(StrumentiUrbanistici.class,
                    it.eng.sacerasi.su.xml.invioSU.ObjectFactory.class);
            invioSUSchema = schemaFctry.newSchema(
                    it.eng.sacerasixml.xsd.FileXSDUtil.getURLFileXSD(it.eng.sacerasixml.xsd.FileXSD.INVIO_SU_XSD));
            // Invio Sisma
            invioSismaCtx_InvioSisma = JAXBContext.newInstance(ProgettiSisma.class,
                    it.eng.sacerasi.sisma.xml.invioSisma.ObjectFactory.class);
            invioSismaSchema = schemaFctry.newSchema(
                    it.eng.sacerasixml.xsd.FileXSDUtil.getURLFileXSD(it.eng.sacerasixml.xsd.FileXSD.INVIO_SISMA_XSD));

            // MEV 31639 Unità Documentaria
            unitaDocumentariaCtx_UnitaDocumentaria = JAXBContext.newInstance(UnitaDocumentariaType.class,
                    it.eng.sacerasi.xml.unitaDocumentaria.ObjectFactory.class);
            unitaDocumentariaSchema = schemaFctry.newSchema(it.eng.sacerasixml.xsd.FileXSDUtil
                    .getURLFileXSD(it.eng.sacerasixml.xsd.FileXSD.UNITA_DOCUMENTARIA_XSD));

            // FINE SERVIZI PING
            // Inizio servizi SACER
            // Servizi per annullamento versamento
            richAnnVersCtx_RichiestaAnnullamentoVersamenti = JAXBContext.newInstance(
                    it.eng.parer.ws.xml.richAnnullVers.RichiestaAnnullamentoVersamenti.class,
                    it.eng.parer.ws.xml.richAnnullVers.ObjectFactory.class);
            richAnnVersSchema = schemaFctry.newSchema(FileXSDUtil.getURLFileXSD(FileXSD.ANNULLA_VERS_REQ_XSD));
            esitoAnnVersCtx_EsitoRichiestaAnnullamentoVersamenti = JAXBContext.newInstance(
                    it.eng.parer.ws.xml.esitoRichAnnullVers.EsitoRichiestaAnnullamentoVersamenti.class,
                    it.eng.parer.ws.xml.esitoRichAnnullVers.ObjectFactory.class);
            esitoRichAnnVersSchema = schemaFctry.newSchema(FileXSDUtil.getURLFileXSD(FileXSD.ANNULLA_VERS_RESP_XSD));

            // Servizi per versamento
            versReqCtx_UD = JAXBContext.newInstance(it.eng.parer.ws.xml.versReq.UnitaDocumentaria.class,
                    it.eng.parer.ws.xml.versReq.ObjectFactory.class);
            versReqSchema = schemaFctry.newSchema(FileXSDUtil.getURLFileXSD(FileXSD.VERS_REQ_XSD));

            versReqStatoCtx_Recupero = JAXBContext.newInstance(it.eng.parer.ws.xml.versReqStato.Recupero.class,
                    it.eng.parer.ws.xml.versReqStato.ObjectFactory.class);
            versReqStatoSchema = schemaFctry.newSchema(FileXSDUtil.getURLFileXSD(FileXSD.VERS_REQ_STATO_XSD));

            versRespCtx_EsitoVersamento = JAXBContext.newInstance(it.eng.parer.ws.xml.versResp.EsitoVersamento.class,
                    it.eng.parer.ws.xml.versResp.ObjectFactory.class);
            versRespStatoCtx_StatoConservazione = JAXBContext.newInstance(
                    it.eng.parer.ws.xml.versRespStato.StatoConservazione.class,
                    it.eng.parer.ws.xml.versRespStato.ObjectFactory.class);
            versRespSchema = schemaFctry.newSchema(FileXSDUtil.getURLFileXSD(FileXSD.VERS_RESP_XSD));

            // Servizi multimedia
            versReqMMCtx_IndiceMM = JAXBContext.newInstance(it.eng.parer.ws.xml.versReqMultiMedia.IndiceMM.class,
                    it.eng.parer.ws.xml.versReqMultiMedia.ObjectFactory.class);
            versReqStatoMMCtx_IndiceMM = JAXBContext.newInstance(it.eng.parer.ws.xml.versReqStatoMM.IndiceMM.class,
                    it.eng.parer.ws.xml.versReqStatoMM.ObjectFactory.class);
            // Fine servizi Sacer

            log.info("Inizializzazione singleton XMLContext... completata.");
        } catch (JAXBException | SAXException ex) {
            // log.fatal("Inizializzazione singleton XMLContext fallita! ", ex);
            log.error("Inizializzazione singleton XMLContext fallita! ", ex);
            throw new RuntimeException(ex);
        }
    }

    @Lock(LockType.READ)
    public JAXBContext getRichAnnVersCtx_RichiestaAnnullamentoVersamenti() {
        return richAnnVersCtx_RichiestaAnnullamentoVersamenti;
    }

    @Lock(LockType.READ)
    public JAXBContext getEsitoAnnVersCtx_EsitoRichiestaAnnullamentoVersamenti() {
        return esitoAnnVersCtx_EsitoRichiestaAnnullamentoVersamenti;
    }

    @Lock(LockType.READ)
    public Schema getSchemaOfRichAnnVers() {
        return richAnnVersSchema;
    }

    @Lock(LockType.READ)
    public Schema getSchemaOfEsitoRichAnnVers() {
        return esitoRichAnnVersSchema;
    }

    @Lock(LockType.READ)
    public JAXBContext getVersReqCtxforUD() {
        return versReqCtx_UD;
    }

    @Lock(LockType.READ)
    public JAXBContext getVersRespCtxforEsitoVersamento() {
        return versRespCtx_EsitoVersamento;
    }

    @Lock(LockType.READ)
    public JAXBContext getVersRespStatoCtx_StatoConservazione() {
        return versRespStatoCtx_StatoConservazione;
    }

    @Lock(LockType.READ)
    public Schema getSchemaOfVersReq() {
        return versReqSchema;
    }

    @Lock(LockType.READ)
    public Schema getSchemaOfVersResp() {
        return versRespSchema;
    }

    @Lock(LockType.READ)
    public JAXBContext getVersReqMMCtxforIndiceMM() {
        return versReqMMCtx_IndiceMM;
    }

    @Lock(LockType.READ)
    public JAXBContext getVersReqStatoMMCtx_IndiceMM() {
        return versReqStatoMMCtx_IndiceMM;
    }

    @Lock(LockType.READ)
    public JAXBContext getVersReqStatoCtx_Recupero() {
        return versReqStatoCtx_Recupero;
    }

    @Lock(LockType.READ)
    public JAXBContext getVersReqAsyncCtx_ListaUnitaDocumentarie() {
        return versReqAsyncCtx_ListaUnitaDocumentarie;
    }

    @Lock(LockType.READ)
    public Schema getSchemaOfInvioAsync() {
        return invioAsyncSchema;
    }

    @Lock(LockType.READ)
    public JAXBContext getVersReqAsyncDaTrasfCtx_Oggetto() {
        return versReqAsyncDaTrasfCtx_Oggetto;
    }

    @Lock(LockType.READ)
    public Schema getSchemaOfInvioAsyncDaTrasfSchema() {
        return invioAsyncDaTrasfSchema;
    }

    @Lock(LockType.READ)
    public JAXBContext getDatispecFiltriCtx_ListaFiltri() {
        return datispecFiltriCtx_ListaFiltri;
    }

    @Lock(LockType.READ)
    public Schema getDatispecFiltriSchema() {
        return datispecFiltriSchema;
    }

    @Lock(LockType.READ)
    public JAXBContext getDatispecOutCtx_ListaDatiSpecificiOut() {
        return datispecOutCtx_ListaDatiSpecificiOut;
    }

    @Lock(LockType.READ)
    public Schema getDatispecOutSchema() {
        return datispecOutSchema;
    }

    @Lock(LockType.READ)
    public JAXBContext getDatispecOrderCtx_ListaDatiSpecificiOrder() {
        return datispecOrderCtx_ListaDatiSpecificiOrder;
    }

    @Lock(LockType.READ)
    public Schema getDatispecOrderSchema() {
        return datispecOrderSchema;
    }

    @Lock(LockType.READ)
    public JAXBContext getVersReqAsyncDSDicomCtx_DatiSpecifici() {
        return versReqAsyncDSDicomCtx_DatiSpecifici;
    }

    @Lock(LockType.READ)
    public Schema getDatiSpecificiDicomSchema() {
        return datiSpecificiDicomSchema;
    }

    @Lock(LockType.READ)
    public JAXBContext getDatispecResultCtx_ListaValoriDatiSpecifici() {
        return datispecResultCtx_ListaValoriDatiSpecifici;
    }

    @Lock(LockType.READ)
    public Schema getInvioSUSchema() {
        return invioSUSchema;
    }

    @Lock(LockType.READ)
    public JAXBContext getInvioSUCtx_InvioSU() {
        return invioSUCtx_InvioSU;
    }

    @Lock(LockType.READ)
    public Schema getInvioSismaSchema() {
        return invioSismaSchema;
    }

    @Lock(LockType.READ)
    public JAXBContext getInvioSismaCtx_InvioSisma() {
        return invioSismaCtx_InvioSisma;
    }

    @Lock(LockType.READ)
    public Schema getUnitaDocumentariaSchema() {
        return unitaDocumentariaSchema;
    }

    @Lock(LockType.READ)
    public JAXBContext getUnitaDocumentariaCtx_UnitaDocumentaria() {
        return unitaDocumentariaCtx_UnitaDocumentaria;
    }
}
