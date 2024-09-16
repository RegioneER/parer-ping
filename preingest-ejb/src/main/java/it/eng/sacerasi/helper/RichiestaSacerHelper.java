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

package it.eng.sacerasi.helper;

import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
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
import it.eng.sacerasi.ws.ejb.XmlContextCache;

@Stateless(mappedName = "RichiestaSacerHelper")
@LocalBean
public class RichiestaSacerHelper {

    Logger log = LoggerFactory.getLogger(RichiestaSacerHelper.class);
    // Singleton Ejb di gestione cache dei parser Castor
    @EJB
    XmlContextCache xmlContextCache;

    public EsitoConnessione upload(RichiestaSacerInput inputParams) {
        EsitoConnessione esitoConnessione = new EsitoConnessione();
        String responseString = null;
        try {
            boolean useHttps = true;

            HttpParams httpParameters = new BasicHttpParams();
            int timeoutConnection = inputParams.getTimeout();
            int timeoutSoConnection = inputParams.getTimeout();
            HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
            HttpConnectionParams.setSoTimeout(httpParameters, timeoutSoConnection);

            // crea una nuova istanza di HttpClient, predisponendo la chiamata del metodo POST
            HttpClient httpclient = new DefaultHttpClient(httpParameters);

            if (useHttps) {
                // se devo usare HTTPS...
                // creo un array di TrustManager per considerare tutti i certificati server come validi.
                // questo andrebbe rimpiazzato con uno che validi il certificato con un certstore...
                X509TrustManager tm = new X509TrustManager() {
                    @Override
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

                    @Override
                    public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
                        // unused
                    }

                    @Override
                    public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
                        // unused
                    }
                };

                try {
                    // Creo il contesto SSL utilizzando i trust manager creati
                    SSLContext ctx = SSLContext.getInstance("TLS");
                    ctx.init(null, new TrustManager[] { tm }, null);

                    // Creo la connessione https
                    SSLSocketFactory ssf = new SSLSocketFactory(ctx, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
                    ClientConnectionManager ccm = httpclient.getConnectionManager();
                    SchemeRegistry sr = ccm.getSchemeRegistry();
                    sr.register(new Scheme("https", 443, ssf));
                    httpclient = new DefaultHttpClient(ccm, httpclient.getParams());
                } catch (NoSuchAlgorithmException | KeyManagementException e) {
                    log.error("Errore interno nella preparazione della chiamata HTTPS {0}", e.getMessage());
                }
            }

            HttpPost httppost = new HttpPost(inputParams.getUrlRichiesta());

            // Inizializza la request come multipart, nella modalità browser compatible che
            // consente di inviare i dati come campi di una form web
            MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

            // std HTTP POST CHARSET
            // aggiunge alla request il campo testuale VERSIONE
            reqEntity.addPart("VERSIONE", new StringBody(inputParams.getVersioneWsDaInvocare(), MediaType.TEXT_PLAIN,
                    StandardCharsets.ISO_8859_1));

            // aggiunge alla request il campo testuale LOGINNAME
            reqEntity.addPart("LOGINNAME ",
                    new StringBody(inputParams.getUserIdSacer(), MediaType.TEXT_PLAIN, StandardCharsets.ISO_8859_1));

            // aggiunge alla request il campo testuale PASSWORD
            reqEntity.addPart("PASSWORD ",
                    new StringBody(inputParams.getPasswordSacer(), MediaType.TEXT_PLAIN, StandardCharsets.ISO_8859_1));

            if (inputParams.getXmlIndice() != null) {
                // aggiunge alla request il campo testuale XMLINDICE
                reqEntity.addPart("XMLINDICE ", new StringBody(XmlUtils.convertToHTMLCodes(inputParams.getXmlIndice()),
                        MediaType.TEXT_XML, StandardCharsets.ISO_8859_1));
            }

            // aggiunge alla request il campo testuale XMLSIP, con il documento XML dei metadati
            reqEntity.addPart("XMLSIP", new StringBody(XmlUtils.convertToHTMLCodes(inputParams.getXmlRichiestaSacer()),
                    MediaType.TEXT_XML, StandardCharsets.ISO_8859_1));
            // imposta la chiamata del metodo POST con i dati appena caricati
            httppost.setEntity(reqEntity);

            log.info("eseguo la richiesta... {}", httppost.getRequestLine());

            // invoca il web service
            HttpResponse response = null;
            boolean timeoutException = false;
            int statusCode = 0;
            try {
                response = httpclient.execute(httppost);
                statusCode = response.getStatusLine().getStatusCode();
            } catch (Exception ex) {
                timeoutException = true;
                log.error("catch timeoutException ", ex);
            }
            if (timeoutException || statusCode != 200) {
                esitoConnessione.setErroreConnessione(true);
                if (timeoutException) {
                    esitoConnessione.setDescrErrConnessione("Errore timeout");
                } else {
                    esitoConnessione.setDescrErrConnessione(
                            "Errore il server ha ritornato uno status HTTP=" + statusCode + ", diverso da 200");
                }
            } else {
                // recupera la risposta
                HttpEntity resEntity = response.getEntity();
                if (resEntity != null) {
                    responseString = EntityUtils.toString(resEntity);
                    log.trace("Risposta : {}", responseString);

                    switch (inputParams.getTipoRichiesta()) {
                    case VERSAMENTO:
                        EsitoVersamento esitoVersamento = unmarshallEsitoVersamento(responseString);
                        esitoConnessione.setCodiceEsito(esitoVersamento.getEsitoGenerale().getCodiceEsito().name());
                        esitoConnessione.setCodiceErrore(esitoVersamento.getEsitoGenerale().getCodiceErrore());
                        esitoConnessione.setMessaggioErrore(esitoVersamento.getEsitoGenerale().getMessaggioErrore());
                        break;
                    case RECUPERO:
                        StatoConservazione statoConservazione = unmarshallStatoConservazione(responseString);
                        esitoConnessione.setCodiceEsito(statoConservazione.getEsitoGenerale().getCodiceEsito().name());
                        esitoConnessione.setCodiceErrore(statoConservazione.getEsitoGenerale().getCodiceErrore());
                        esitoConnessione.setMessaggioErrore(statoConservazione.getEsitoGenerale().getMessaggioErrore());
                        break;
                    case ANNULLAMENTO:
                        EsitoRichiestaAnnullamentoVersamenti esitoAnnul = unmarshallEsitoRichiestaAnnullamentoVersamenti(
                                responseString);
                        esitoConnessione.setCodiceEsito(esitoAnnul.getEsitoRichiesta().getCodiceEsito().name());
                        esitoConnessione.setCodiceErrore(esitoAnnul.getEsitoRichiesta().getCodiceErrore());
                        esitoConnessione.setMessaggioErrore(esitoAnnul.getEsitoRichiesta().getMessaggioErrore());
                        break;
                    }
                    esitoConnessione.setXmlResponse(responseString);
                    esitoConnessione.setErroreConnessione(false);
                }
            }
        } catch (Exception ex) {
            // MAC#14483 - Gestire esito non conforme in annullamento oggetto
            // Se l'xml c'è ma non lo valida con l'xsd lo deve registrare comunque evitando di lasciarlo nullo
            esitoConnessione.setXmlResponse(responseString);
            esitoConnessione.setErroreConnessione(false);
            esitoConnessione.setDescrErrConnessione(null);
            esitoConnessione.setCodiceEsito(Constants.EsitoVersamento.NEGATIVO.name());
            esitoConnessione.setCodiceErrore(null);
            esitoConnessione
                    .setMessaggioErrore("Errore nella risposta: l'xml di risposta non rispetta l'xsd associato");
            log.error("Errore nella risposta: l'xml di risposta non rispetta l'xsd associato", ex);
        }
        return esitoConnessione;
    }

    private EsitoVersamento unmarshallEsitoVersamento(String resp) throws JAXBException {
        StringReader tmpReader = new StringReader(resp);
        javax.xml.bind.Unmarshaller tmpUnmarshaller = xmlContextCache.getVersRespCtxforEsitoVersamento()
                .createUnmarshaller();
        tmpUnmarshaller.setSchema(xmlContextCache.getSchemaOfVersResp());
        return (EsitoVersamento) (tmpUnmarshaller.unmarshal(tmpReader));
    }

    private StatoConservazione unmarshallStatoConservazione(String resp) throws JAXBException {
        StringReader tmpReader = new StringReader(resp);
        Unmarshaller tmpUnmarshaller = xmlContextCache.getVersRespStatoCtx_StatoConservazione().createUnmarshaller();
        return (StatoConservazione) (tmpUnmarshaller.unmarshal(tmpReader));
    }

    private EsitoRichiestaAnnullamentoVersamenti unmarshallEsitoRichiestaAnnullamentoVersamenti(String resp)
            throws JAXBException {
        StringReader tmpReader = new StringReader(resp);
        javax.xml.bind.Unmarshaller unmarshaller = xmlContextCache
                .getEsitoAnnVersCtx_EsitoRichiestaAnnullamentoVersamenti().createUnmarshaller();
        unmarshaller.setSchema(xmlContextCache.getSchemaOfEsitoRichAnnVers());
        return (EsitoRichiestaAnnullamentoVersamenti) unmarshaller.unmarshal(tmpReader);
    }
}
