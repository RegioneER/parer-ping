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

package it.eng.sacerasi.helper;

import java.io.StringReader;
import java.nio.charset.StandardCharsets;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.parer.ws.xml.esitoRichAnnullVers.EsitoRichiestaAnnullamentoVersamenti;
import it.eng.parer.ws.xml.versResp.EsitoVersamento;
import it.eng.parer.ws.xml.versRespStato.StatoConservazione;
import it.eng.sacerasi.common.Constants;
import it.eng.sacerasi.job.dto.EsitoConnessione;
import it.eng.sacerasi.job.dto.RichiestaSacerInput;
import it.eng.sacerasi.job.preparaxml.util.XmlUtils;
import it.eng.sacerasi.messages.MessaggiWSBundle;
import it.eng.sacerasi.ws.ejb.XmlContextCache;

@Stateless(mappedName = "RichiestaSacerHelper")
@LocalBean
public class RichiestaSacerHelper {

    Logger log = LoggerFactory.getLogger(RichiestaSacerHelper.class);
    // Singleton Ejb di gestione cache dei parser jaxb
    @EJB
    XmlContextCache xmlContextCache;
    // Singleton Ejb di gestione del pool di connessioni http
    @EJB
    RichiestaSacerCmPoolHelper richiestaSacerCmPoolHelper;

    public EsitoConnessione upload(RichiestaSacerInput inputParams) {
        // inizializzazion bean esito
        EsitoConnessione esitoConnessione = new EsitoConnessione();
        // impostazione dei parametri di connessione
        int timeout = inputParams.getTimeout();
        RequestConfig reqConfig = RequestConfig.custom().setConnectTimeout(timeout)
                .setSocketTimeout(timeout).build();

        // creazione cliente http attraverso il pool
        try (CloseableHttpClient httpclient = richiestaSacerCmPoolHelper
                .createHttpClient(reqConfig)) {

            HttpPost httppost = new HttpPost(inputParams.getUrlRichiesta());
            HttpEntity reqEntity = buildMultipartEntity(inputParams);
            httppost.setEntity(reqEntity);

            log.info("Executing request... {}", httppost.getRequestLine());
            HttpResponse response = httpclient.execute(httppost);
            int statusCode = response.getStatusLine().getStatusCode();

            if (statusCode != 200) {
                setError(esitoConnessione, "Il servizio restituisce errore " + statusCode);
            } else {
                HttpEntity resEntity = response.getEntity();
                if (resEntity != null) {
                    String responseString = EntityUtils.toString(resEntity);
                    log.trace("Response: {}", responseString);
                    processResponse(responseString, inputParams, esitoConnessione);
                    esitoConnessione.setXmlResponse(responseString);
                    esitoConnessione.setErroreConnessione(false);
                }
            }
        } catch (Exception ex) {
            final String msg = "Richiesta al servizio scaduta o fallita";
            log.error(msg, ex);
            setError(esitoConnessione, msg);
        }
        return esitoConnessione;
    }

    private HttpEntity buildMultipartEntity(RichiestaSacerInput inputParams) {
        // Creazione del multipart entity per la richiesta
        final String contentTypeTextPlain = "text/plain";
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        builder.addTextBody("VERSIONE", inputParams.getVersioneWsDaInvocare(),
                ContentType.create(contentTypeTextPlain, StandardCharsets.ISO_8859_1));
        builder.addTextBody("LOGINNAME", inputParams.getUserIdSacer(),
                ContentType.create(contentTypeTextPlain, StandardCharsets.ISO_8859_1));
        builder.addTextBody("PASSWORD", inputParams.getPasswordSacer(),
                ContentType.create(contentTypeTextPlain, StandardCharsets.ISO_8859_1));
        if (inputParams.getXmlIndice() != null) {
            builder.addTextBody("XMLINDICE",
                    XmlUtils.convertToHTMLCodes(inputParams.getXmlIndice()),
                    ContentType.create("text/xml", StandardCharsets.ISO_8859_1));
        }
        builder.addTextBody("XMLSIP",
                XmlUtils.convertToHTMLCodes(inputParams.getXmlRichiestaSacer()),
                ContentType.create("text/xml", StandardCharsets.ISO_8859_1));
        return builder.build();
    }

    private void processResponse(String responseString, RichiestaSacerInput inputParams,
            EsitoConnessione esitoConnessione) {
        try {
            switch (inputParams.getTipoRichiesta()) {
            case VERSAMENTO:
                EsitoVersamento esitoVersamento = unmarshallEsitoVersamento(responseString);
                esitoConnessione
                        .setCodiceEsito(esitoVersamento.getEsitoGenerale().getCodiceEsito().name());
                esitoConnessione
                        .setCodiceErrore(esitoVersamento.getEsitoGenerale().getCodiceErrore());
                esitoConnessione.setMessaggioErrore(
                        esitoVersamento.getEsitoGenerale().getMessaggioErrore());
                break;
            case RECUPERO:
                StatoConservazione statoConservazione = unmarshallStatoConservazione(
                        responseString);
                esitoConnessione.setCodiceEsito(
                        statoConservazione.getEsitoGenerale().getCodiceEsito().name());
                esitoConnessione
                        .setCodiceErrore(statoConservazione.getEsitoGenerale().getCodiceErrore());
                esitoConnessione.setMessaggioErrore(
                        statoConservazione.getEsitoGenerale().getMessaggioErrore());
                break;
            case ANNULLAMENTO:
                EsitoRichiestaAnnullamentoVersamenti esitoAnnul = unmarshallEsitoRichiestaAnnullamentoVersamenti(
                        responseString);
                esitoConnessione
                        .setCodiceEsito(esitoAnnul.getEsitoRichiesta().getCodiceEsito().name());
                esitoConnessione.setCodiceErrore(esitoAnnul.getEsitoRichiesta().getCodiceErrore());
                esitoConnessione
                        .setMessaggioErrore(esitoAnnul.getEsitoRichiesta().getMessaggioErrore());
                break;
            }
        } catch (Exception ex) {
            final String msg = "Errore nella risposta: l'xml di risposta non rispetta l'xsd associato";
            log.error(msg, ex);
            esitoConnessione.setCodiceEsito(Constants.EsitoVersamento.NEGATIVO.name());
            esitoConnessione.setCodiceErrore(MessaggiWSBundle.ERR_666);
            esitoConnessione.setMessaggioErrore(msg);
        }
    }

    private void setError(EsitoConnessione esito, String errorMessage) {
        esito.setErroreConnessione(true);
        esito.setDescrErrConnessione(errorMessage);
    }

    private EsitoVersamento unmarshallEsitoVersamento(String resp) throws JAXBException {
        StringReader tmpReader = new StringReader(resp);
        javax.xml.bind.Unmarshaller tmpUnmarshaller = xmlContextCache
                .getVersRespCtxforEsitoVersamento().createUnmarshaller();
        tmpUnmarshaller.setSchema(xmlContextCache.getSchemaOfVersResp());
        return (EsitoVersamento) (tmpUnmarshaller.unmarshal(tmpReader));
    }

    private StatoConservazione unmarshallStatoConservazione(String resp) throws JAXBException {
        StringReader tmpReader = new StringReader(resp);
        Unmarshaller tmpUnmarshaller = xmlContextCache.getVersRespStatoCtx_StatoConservazione()
                .createUnmarshaller();
        return (StatoConservazione) (tmpUnmarshaller.unmarshal(tmpReader));
    }

    private EsitoRichiestaAnnullamentoVersamenti unmarshallEsitoRichiestaAnnullamentoVersamenti(
            String resp) throws JAXBException {
        StringReader tmpReader = new StringReader(resp);
        javax.xml.bind.Unmarshaller unmarshaller = xmlContextCache
                .getEsitoAnnVersCtx_EsitoRichiestaAnnullamentoVersamenti().createUnmarshaller();
        unmarshaller.setSchema(xmlContextCache.getSchemaOfEsitoRichAnnVers());
        return (EsitoRichiestaAnnullamentoVersamenti) unmarshaller.unmarshal(tmpReader);
    }
}
