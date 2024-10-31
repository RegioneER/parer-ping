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

package it.eng.xformer.ws.client;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.jws.WebResult;
import javax.xml.ws.WebServiceException;

import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;

import it.eng.parer.kettle.exceptions.KettleException;
import it.eng.parer.kettle.exceptions.KettleServiceException;
import it.eng.parer.kettle.model.AbstractEsito;
import it.eng.parer.kettle.model.Esito;
import it.eng.parer.kettle.model.EsitoCartella;
import it.eng.parer.kettle.model.EsitoEsitenzaCartella;
import it.eng.parer.kettle.model.EsitoJob;
import it.eng.parer.kettle.model.EsitoListaParametri;
import it.eng.parer.kettle.model.EsitoStatusCodaTrasformazione;
import it.eng.parer.kettle.model.EsitoTransformation;
import it.eng.parer.kettle.model.KettleJob;
import it.eng.parer.kettle.model.KettleTransformation;
import it.eng.parer.kettle.model.Parametro;
import it.eng.parer.kettle.soap.client.TrasformazioniSoapService;

/**
 *
 * @author Cappelli_F
 */
public class KettleWsExecuteTrasformationClient {

    private TrasformazioniSoapService client;

    public KettleWsExecuteTrasformationClient(String endpoint) {
        JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();

        Map<String, Object> props = new HashMap<String, Object>();
        props.put("mtom-enabled", Boolean.TRUE);
        factory.setProperties(props);

        factory.setServiceClass(TrasformazioniSoapService.class);

        factory.setAddress(endpoint);

        client = (TrasformazioniSoapService) factory.create();
    }

    /*
     * Metodo che innesca l'inizio della <em>Trasformazione</em>. Lo stato che il <tt>PIG_OBJECT</tt> deve assumere in
     * caso di esito positivo deve essere <tt>TRASFORMAZIONE_IN_CORSO</tt> .
     *
     * @param idOggetto id del <tt>PIG_OBJECT</tt> relativo alla <em>Trasformazione</em>.
     *
     * @param nomeTrasformazione nome <em>univoco</em> della trasformazione
     *
     * @param parametri della trasformazioni (definiti sul repository kettle)
     *
     * @return Esito dell'avvenuto lancio della trasformazione
     */
    public Esito eseguiTrasformazione(long idOggetto, String nomeTrasformazione, Map<String, String> parametri)
            throws KettleServiceException {
        try {
            List<Parametro> parametriList = new ArrayList<Parametro>();

            for (Map.Entry<String, String> parametro : parametri.entrySet()) {
                parametriList.add(new Parametro(parametro.getKey(), parametro.getValue()));
            }

            Esito esito = client.eseguiTrasformazione(idOggetto, nomeTrasformazione, parametriList);
            return esito;

        } catch (WebServiceException ex) {
            throw new KettleServiceException(ex.getMessage(), ex);
        }
    }

    /**
     * Inserimento del job di kettle. Necessario utilizzare <em>MTOM</em> per trasferire il file.
     *
     * @param jobXmlFile
     *            file xml
     * @param versionComment
     *            versione
     *
     * @throws KettleException
     *             errore generico
     */
    public void inserisciJob(File jobXmlFile, String versionComment) throws KettleException {
        DataHandler jobDescriptor = new DataHandler(new FileDataSource(jobXmlFile));

        KettleJob kettleJob = new KettleJob();
        kettleJob.setJobDescriptor(jobDescriptor);
        kettleJob.setVersione(versionComment);

        EsitoJob esito = client.inserisciJob(kettleJob);
        if (esito.getEsitoSintetico() == AbstractEsito.ESITO_SINTETICO.KO) {
            throw new KettleException(esito.getDettaglio());
        }
    }

    /**
     * Inserimento della trasformation di kettle. Necessario utilizzare <em>MTOM</em> per trasferire il file.
     *
     * @param transformationXmlFile
     *            file trasformazione xml
     * @param versionComment
     *            versione
     *
     * @throws KettleException
     *             errore generico
     */
    public void inserisciTransformation(File transformationXmlFile, String versionComment) throws KettleException {
        DataHandler transformationDescriptor = new DataHandler(new FileDataSource(transformationXmlFile));

        KettleTransformation kettleTrasformation = new KettleTransformation();
        kettleTrasformation.setTransformationDescriptor(transformationDescriptor);
        kettleTrasformation.setVersione(versionComment);

        EsitoTransformation esito = client.inserisciTransformation(kettleTrasformation);
        if (esito.getEsitoSintetico() == AbstractEsito.ESITO_SINTETICO.KO) {
            throw new KettleException(esito.getDettaglio());
        }
    }

    /**
     * Inserisci la cartella (corrispondente al nome della <em>Trasformazione</em>).
     *
     * @param nomeCartella
     *            nome della cartella/trasformazione
     *
     * @throws KettleException
     *             errore generico
     *
     */
    public void inserisciCartella(String nomeCartella) throws KettleException {
        EsitoCartella esito = client.inserisciCartella(nomeCartella);
        if (esito.getEsitoSintetico() == AbstractEsito.ESITO_SINTETICO.KO) {
            throw new KettleException(esito.getDettaglio());
        }
    }

    /**
     * Elimina la cartella (corrispondente al nome della <em>Trasformazione</em>).
     *
     * @param nomeCartella
     *            nome della cartella/trasformazione
     *
     * @throws KettleException
     *             errore generico
     *
     */
    public void eliminaCartella(String nomeCartella) throws KettleException {
        EsitoCartella esito = client.eliminaCartella(nomeCartella);
        if (esito.getEsitoSintetico() == AbstractEsito.ESITO_SINTETICO.KO) {
            throw new KettleException(esito.getDettaglio());
        }
    }

    /**
     * Ottieni la lista dei parametri della trasformazione.
     *
     * @param nomeTrasformazione
     *            nome della trasformazione/cartella.
     *
     * @return Lista dei parametri associati alla trasformazione/cartella.
     *
     * @throws KettleException
     *             errore generico
     * @throws KettleServiceException
     *             errore generico
     *
     */
    @WebResult(name = "ParametriList")
    public Map<String, String> ottieniParametri(String nomeTrasformazione)
            throws KettleException, KettleServiceException {
        try {
            Map<String, String> parametersMap = new HashMap<String, String>();

            EsitoListaParametri esito = client.ottieniParametri(nomeTrasformazione);
            if (esito.getEsitoSintetico() == AbstractEsito.ESITO_SINTETICO.KO) {
                throw new KettleException(esito.getDettaglio());
            }

            List<Parametro> parameters = esito.getParameters();
            for (Parametro parameter : parameters) {
                parametersMap.put(parameter.getNomeParametro(), parameter.getValoreParametro());
            }

            return parametersMap;
        } catch (WebServiceException ex) {
            throw new KettleServiceException("Errore di connessione con Parer Kettle server: " + ex.getMessage());
        }
    }

    /**
     * Ottieni la lista dei parametri della trasformazione.
     *
     * @param nomeCartella
     *            nome della trasformazione/cartella.
     *
     * @return esitenza della cartella
     *
     * @throws KettleException
     *             errore generico
     *
     */
    @WebResult(name = "Esistenza")
    public boolean esistenzaCartella(String nomeCartella) throws KettleException {
        EsitoEsitenzaCartella esito = client.esistenzaCartella(nomeCartella);
        if (esito.getEsitoSintetico() == AbstractEsito.ESITO_SINTETICO.KO) {
            throw new KettleException(esito.getDettaglio());
        }

        return esito.isEsito();
    }

    @WebResult(name = "EsitoStatusCodaTrasformazione")
    public EsitoStatusCodaTrasformazione statusCodaTrasformazione(Date startDate, Date endDate, int numResults) {
        return client.statusCodaTrasformazione(startDate, endDate, numResults);
    }
}
