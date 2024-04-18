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

package it.eng.xformer.kettle.ejb;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.parer.kettle.exceptions.KettleException;
import it.eng.parer.kettle.exceptions.KettleServiceException;
import it.eng.parer.kettle.model.Esito;
import it.eng.parer.kettle.model.EsitoStatusCodaTrasformazione;
import it.eng.sacerasi.entity.PigTipoObject;
import it.eng.sacerasi.entity.XfoFileTrasf;
import it.eng.sacerasi.entity.XfoStoricoTrasf;
import it.eng.sacerasi.entity.XfoTrasf;
import it.eng.sacerasi.exception.ParerUserError;
import it.eng.sacerasi.slite.gen.tablebean.PigTipoObjectRowBean;
import it.eng.sacerasi.slite.gen.tablebean.PigTipoObjectTableBean;
import it.eng.sacerasi.slite.gen.tablebean.XfoStoricoTrasfRowBean;
import it.eng.sacerasi.slite.gen.tablebean.XfoStoricoTrasfTableBean;
import it.eng.sacerasi.slite.gen.tablebean.XfoTrasfRowBean;
import it.eng.sacerasi.slite.gen.tablebean.XfoTrasfTableBean;
import it.eng.sacerasi.web.helper.ConfigurationHelper;
import it.eng.sacerasi.web.util.Transform;
import it.eng.xformer.common.Constants;
import it.eng.xformer.dto.RicercaTrasformazioneBean;
import it.eng.xformer.helper.TrasformazioniHelper;
import it.eng.xformer.ws.client.KettleWsClient;
import it.eng.xformer.ws.client.KettleWsExecuteTrasformationClient;

/**
 * @author Cappelli_F
 */
@Stateless(mappedName = "RepositoryManager")
@LocalBean
@Interceptors({ it.eng.sacerasi.aop.TransactionInterceptor.class })
public class RepositoryManager {

    private final Logger logger = LoggerFactory.getLogger(RepositoryManager.class);

    @EJB(mappedName = "java:app/SacerAsync-ejb/ConfigurationHelper")
    private ConfigurationHelper configurationHelper;

    @EJB(mappedName = "java:app/SacerAsync-ejb/TrasformazioniHelper")
    private TrasformazioniHelper helper;

    @EJB(mappedName = "java:app/SacerAsync-ejb/RepositoryManager")
    private RepositoryManager me;

    @EJB(mappedName = "java:app/SacerAsync-ejb/KettleWsClient")
    private KettleWsClient kwsClient;

    @Resource
    private SessionContext context;

    public long insertNewTransformation(String name, String description, String enabled, String version,
            String versionDescription, Date istituz, Date soppress, byte[] zipPackage, String kettleId)
            throws ParerUserError {
        XfoTrasf xfoTransformation = new XfoTrasf();

        try {
            xfoTransformation.setCdTrasf(name);
            xfoTransformation.setDsTrasf(description);
            xfoTransformation.setCdVersioneCor(version);
            xfoTransformation.setDsVersioneCor(versionDescription);
            xfoTransformation.setFlAttiva(enabled);
            xfoTransformation.setBlTrasf(zipPackage);

            xfoTransformation.setDtIstituz(new Date());
            xfoTransformation.setDtSoppres(new GregorianCalendar(2444, Calendar.DECEMBER, 31).getTime());

            if (istituz != null) {
                xfoTransformation.setDtIstituz(istituz);
            }
            if (soppress != null) {
                xfoTransformation.setDtSoppres(soppress);
            }

            // salva il nome della cartella della trasformazione come CD_KETTLE_ID
            xfoTransformation.setCdKettleId(kettleId);

            helper.insertEntity(xfoTransformation, true);

            if (zipPackage != null) {
                me.insertAuxiliaryFilesIntoDb(zipPackage, xfoTransformation);
                boolean result = me.insertTransformationInKettleRepository(version + "-" + versionDescription,
                        zipPackage, xfoTransformation);

                if (!result) {
                    throw new ParerUserError(
                            "Trasformazione malformata? Non è stato possibile inserirla nel repository.");
                }
            }
        } catch (IOException e) {
            String messaggio = "Errore nell'inserimento di una nuova trasformazione: ";
            messaggio += ExceptionUtils.getRootCauseMessage(e);
            logger.error(messaggio, e);
            throw new ParerUserError(messaggio);
        }

        return xfoTransformation.getIdTrasf();
    }

    public XfoTrasfRowBean getXfoTrasRowBean(long idTrasf) throws ParerUserError {
        XfoTrasfRowBean rowBean = null;

        XfoTrasf xfoTrasf = helper.findById(XfoTrasf.class, idTrasf);

        try {
            rowBean = (XfoTrasfRowBean) Transform.entity2RowBean(xfoTrasf);
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                | IllegalArgumentException | InvocationTargetException e) {
            String messaggio = "Eccezione imprevista nell recupero della Trasformazione ";
            messaggio += ExceptionUtils.getRootCauseMessage(e);
            logger.error(messaggio, e);
            throw new ParerUserError(messaggio);
        }

        return rowBean;
    }

    public XfoStoricoTrasfRowBean getXfoStoricoTrasRowBean(long idStoricoTrasf) throws ParerUserError {
        XfoStoricoTrasfRowBean rowBean = null;

        XfoStoricoTrasf xfoStoricoTrasf = helper.findById(XfoStoricoTrasf.class, idStoricoTrasf);

        try {
            rowBean = (XfoStoricoTrasfRowBean) Transform.entity2RowBean(xfoStoricoTrasf);
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                | IllegalArgumentException | InvocationTargetException e) {
            String messaggio = "Eccezione imprevista nell recupero dello storico della Trasformazione ";
            messaggio += ExceptionUtils.getRootCauseMessage(e);
            logger.error(messaggio, e);
            throw new ParerUserError(messaggio);
        }

        return rowBean;
    }

    public XfoTrasfTableBean getRicercaTrasformazioniTableBean(RicercaTrasformazioneBean filtri) throws ParerUserError {
        XfoTrasfTableBean table = new XfoTrasfTableBean();

        List<XfoTrasf> trasformazioni = helper.searchXfoTrasf(filtri);

        if (trasformazioni != null && !trasformazioni.isEmpty()) {
            try {
                table = (XfoTrasfTableBean) Transform.entities2TableBean(trasformazioni);
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                    | IllegalArgumentException | InvocationTargetException ex) {
                logger.error(
                        "Errore durante il recupero delle trasformazioni: " + ExceptionUtils.getRootCauseMessage(ex),
                        ex);
                throw new ParerUserError(
                        "Errore durante il recupero delle trasformazioni: " + ExceptionUtils.getRootCauseMessage(ex));
            }
        }

        return table;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void updateTransformation(long idTrasf, String name, String description, String enabled, String version,
            String versionDescription, Date istituz, Date soppress, byte[] zipPackage, String kettleId)
            throws ParerUserError {
        updateTransformationInternal(idTrasf, name, description, enabled, version, versionDescription, istituz,
                soppress, zipPackage, kettleId);
    }

    public void updateTransformationNoNewTransition(long idTrasf, String name, String description, String enabled,
            String version, String versionDescription, Date istituz, Date soppress, byte[] zipPackage, String kettleId)
            throws ParerUserError {
        updateTransformationInternal(idTrasf, name, description, enabled, version, versionDescription, istituz,
                soppress, zipPackage, kettleId);
    }

    private void updateTransformationInternal(long idTrasf, String name, String description, String enabled,
            String version, String versionDescription, Date istituz, Date soppress, byte[] zipPackage, String kettleId)
            throws ParerUserError {
        try {
            XfoTrasf trasf = helper.findByIdWithLock(XfoTrasf.class, idTrasf);
            if (trasf != null) {
                trasf.setCdTrasf(name);
                trasf.setDsTrasf(description);
                trasf.setCdVersioneCor(version);
                trasf.setDsVersioneCor(versionDescription);
                if (istituz != null) {
                    trasf.setDtIstituz(istituz);
                }
                if (soppress != null) {
                    trasf.setDtSoppres(soppress);
                }

                trasf.setFlAttiva(enabled);

                // salva il nome della cartella della trasformazione come CD_KETTLE_ID
                trasf.setCdKettleId(kettleId);

                if (zipPackage != null) {

                    if (trasf.getBlTrasf() != null) {
                        // MEV 21859 - se non riesce a cancellarala dal repository di kettle nessun problema,
                        // lo segnaliamo però nei log.
                        try {
                            me.deleteTransformationFromRepository(trasf.getBlTrasf());
                        } catch (KettleException ke) {
                            logger.warn("Cancellazione della trasformazione da kettle fallita: " + ke.getMessage());
                        }
                    }

                    removeAuxiliaryFilesFromDb(trasf, true);
                    insertAuxiliaryFilesIntoDb(zipPackage, trasf);
                    boolean result = me.insertTransformationInKettleRepository(version + "-" + versionDescription,
                            zipPackage, trasf);

                    trasf.setBlTrasf(zipPackage);
                    if (!result) {
                        throw new KettleException("errore nell'inserimento della trasformazione.");
                    }
                }
            } else {
                String messaggio = "Eccezione imprevista nell'aggiornamento della trasformazione ";
                logger.error(messaggio);
                throw new ParerUserError(messaggio);
            }
        } catch (KettleException | IOException ex) {
            String messaggio = "Eccezione imprevista nell'aggiornamento della trasformazione ";
            messaggio += ExceptionUtils.getRootCauseMessage(ex);
            logger.error(messaggio, ex);
            throw new ParerUserError(messaggio);
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public boolean deleteTransformation(long idTrasf) throws ParerUserError {
        logger.debug("Eseguo l'eliminazione della trasformazione.");
        try {
            if (!isTransformationAssigned(idTrasf)) {
                XfoTrasf trasf = helper.findById(XfoTrasf.class, idTrasf);

                if (trasf.getBlTrasf() != null) { // questo dovrebbe essere sempre vero salvo errori sul db.
                    // MEV 21859 - se non riesce a cancellarala dal repository di kettle nessun problema,
                    // lo segnaliamo però nei log.
                    try {
                        me.deleteTransformationFromRepository(trasf.getBlTrasf());
                    } catch (KettleException ke) {
                        logger.warn("Cancellazione della trasformazione da kettle fallita: " + ke.getMessage());
                    }
                }

                removeAuxiliaryFilesFromDb(trasf, false);

                helper.removeEntity(trasf, true);

                return true;
            } else {
                return false;
            }

        } catch (IOException e) {
            String messaggio = "Eccezione imprevista nell'eliminazione della trasformazione ";
            messaggio += ExceptionUtils.getRootCauseMessage(e);
            logger.error(messaggio, e);
            throw new ParerUserError(messaggio);
        }
    }

    public boolean isTransformationAssigned(long idTrasf) {
        return helper.isTransformationAssigned(idTrasf);
    }

    public boolean isTransformationAlreadyPresent(String nomeCartella) throws KettleException {
        // MEV 21589 - rimosso controllo esitenza cartella du repository kettle e
        // sostituito con un controllo su db di Ping
        return helper.isTransformationAlreadyPresent(nomeCartella);
    }

    public void deleteTransformationFromRepository(byte[] zipPackage) throws IOException, KettleException {
        try (ZipInputStream zipInputStream = new ZipInputStream(new ByteArrayInputStream(zipPackage))) {
            ZipEntry entry;
            while ((entry = zipInputStream.getNextEntry()) != null) {
                // rimuovo direttamente la cartella radice per eliminare tutta la trasformazione
                if (entry.isDirectory()) {
                    // MAC #21589
                    kwsClient.eliminaCartella(entry.getName());
                    break;
                }
            }
        }
    }

    public boolean insertTransformationInKettleRepository(String versionComment, byte[] zipPackage, XfoTrasf xfoTrasf) {
        // crea la cartella temporane dove decomprimerlo
        File workingDirectory = new File(
                configurationHelper.getValoreParamApplicByApplic(Constants.XFO_WORK_DIR_PARAM_NAME));
        File temporaryDirectory = new File(workingDirectory, UUID.randomUUID().toString());

        if (workingDirectory.exists()) {
            try {
                temporaryDirectory.mkdir();

                // decomprimilo
                try (ZipInputStream zipInputStream = new ZipInputStream(new ByteArrayInputStream(zipPackage))) {
                    ZipEntry entry;
                    while ((entry = zipInputStream.getNextEntry()) != null) {
                        File tmpFile = new File(temporaryDirectory, entry.getName());
                        // FIXME: il file ancora non esiste su disco, questo controllo forse non funziona...
                        if (!tmpFile.isHidden()) {
                            if (!entry.isDirectory()) {
                                // TODO bisgona gestirea almeno anche i file .kdb e forse altri
                                if (entry.getName().endsWith(".kjb")) {
                                    tmpFile.getParentFile().mkdirs();

                                    try (FileOutputStream fos = new FileOutputStream(tmpFile)) {
                                        IOUtils.copy(zipInputStream, fos);
                                    } catch (FileNotFoundException ex) {
                                        LoggerFactory.getLogger(RepositoryManager.class.getName()).error("Eccezione",
                                                ex);
                                    }

                                    if (tmpFile.length() != 0) {
                                        kwsClient.inserisciJob(tmpFile, versionComment);
                                    }

                                } else if (entry.getName().endsWith(".ktr")) {
                                    tmpFile.getParentFile().mkdirs();
                                    try (FileOutputStream fos = new FileOutputStream(tmpFile)) {
                                        IOUtils.copy(zipInputStream, fos);
                                    }

                                    if (tmpFile.length() != 0) {
                                        kwsClient.inserisciTransformation(tmpFile, versionComment);
                                    }
                                }
                            } else {
                                kwsClient.inserisciCartella(entry.getName());
                            }
                        } else {
                            logger.warn("Il file " + entry.getName()
                                    + " è stato escluso durante l'inserimento della trasformazione "
                                    + xfoTrasf.getCdTrasf());
                        }
                    }
                }
            } catch (KettleException | IOException ke) {
                logger.error("Errore nell'inserimento della trasformazione durante il dialogo con il repository: "
                        + ke.getMessage());
                return false;
            } finally {
                temporaryDirectory.delete();
            }

        } else {
            logger.error("Errore nell'inserimento della trasformazione: la cartella di lavoro non esiste.");
            return false;
        }

        return true;
    }

    public void insertAuxiliaryFilesIntoDb(byte[] zipPackage, XfoTrasf xfoTrasf) throws IOException {
        try (ZipInputStream zipInputStream = new ZipInputStream(new ByteArrayInputStream(zipPackage))) {
            ZipEntry entry;
            while ((entry = zipInputStream.getNextEntry()) != null) {
                if (!entry.isDirectory() && !entry.getName().endsWith(".kjb") && !entry.getName().endsWith(".ktr")) {

                    try (ByteArrayOutputStream blobStream = new ByteArrayOutputStream()) {
                        IOUtils.copy(zipInputStream, blobStream);
                        helper.insertNewAuxiliaryFile(xfoTrasf, entry.getName(), blobStream.toByteArray());
                    } catch (FileNotFoundException ex) {
                        logger.error("Errore nell'insermento deei file ausiliari: " + ex.getMessage(), ex);
                    }
                }
            }
        }
    }

    public void removeAuxiliaryFilesFromDb(XfoTrasf xfoTrasf, boolean flush) {
        List<XfoFileTrasf> files = helper.searchAuxiliaryFilesByXfoTras(xfoTrasf.getIdTrasf());
        for (XfoFileTrasf file : files) {
            helper.removeEntity(file, flush);
        }
    }

    public Map<String, String> listJobParameters(String transformationName)
            throws KettleException, KettleServiceException {
        try {
            Map<String, String> parameters = kwsClient.ottieniParametri(transformationName);
            return parameters;
        } catch (KettleException ex) {
            logger.error("Errore nel recupero della lista dei parametri: " + ex.getMessage(), ex);
            throw ex;
        }
    }

    public boolean executeJob(long idOggetto, String name, Map<String, String> filledParameters)
            throws KettleServiceException, KettleException {
        Esito esito = kwsClient.eseguiTrasformazione(idOggetto, name, filledParameters);

        if (esito.getEsitoSintetico() == Esito.ESITO_SINTETICO.KO) {
            throw new KettleException(esito.getDettaglio());
        } else if (esito.getEsitoSintetico() == Esito.ESITO_SINTETICO.CODA_PIENA) {
            return false;
        }

        return true;
    }

    public boolean executeJob(long idOggetto, String name, Map<String, String> filledParameters, String endpoint)
            throws KettleServiceException, KettleException {
        KettleWsExecuteTrasformationClient client = new KettleWsExecuteTrasformationClient(endpoint);

        Esito esito = client.eseguiTrasformazione(idOggetto, name, filledParameters);

        if (esito.getEsitoSintetico() == Esito.ESITO_SINTETICO.KO) {
            throw new KettleException(esito.getDettaglio());
        } else if (esito.getEsitoSintetico() == Esito.ESITO_SINTETICO.CODA_PIENA) {
            return false;
        }

        return true;
    }

    public EsitoStatusCodaTrasformazione getStatusCodaTrasformazioni(String endpoint) {
        KettleWsExecuteTrasformationClient client = new KettleWsExecuteTrasformationClient(endpoint);
        Date endDate = new Date();

        Calendar cal = Calendar.getInstance();
        cal.setTime(endDate);
        cal.add(Calendar.DATE, -30);
        Date startDate = cal.getTime();
        return client.statusCodaTrasformazione(startDate, endDate, 10);
    }

    public XfoStoricoTrasf storicizeVersion(XfoTrasf xfoTrasf, Date dtSoppres) {
        XfoStoricoTrasf xst = new XfoStoricoTrasf();

        xst.setCdTrasf(xfoTrasf.getCdTrasf());
        xst.setCdVersione(xfoTrasf.getCdVersioneCor());
        xst.setDsVersione(xfoTrasf.getDsVersioneCor());
        xst.setDtIstituz(xfoTrasf.getDtIstituz());

        if (dtSoppres.before(xfoTrasf.getDtSoppres()))
            xst.setDtSoppres(dtSoppres);
        else
            xst.setDtSoppres(xfoTrasf.getDtSoppres());

        xst.setBlTrasf(xfoTrasf.getBlTrasf());
        xst.setXfoTrasf(xfoTrasf);

        helper.insertEntity(xst, true);

        return xst;
    }

    public XfoStoricoTrasfTableBean searchVersionsByTransformation(long idTrasf) throws ParerUserError {
        XfoStoricoTrasfTableBean table = new XfoStoricoTrasfTableBean();

        List<XfoStoricoTrasf> versions = helper.searchXfoStoricoTrasfbyXfoTrasf(idTrasf);
        if (versions != null && !versions.isEmpty()) {
            try {
                for (XfoStoricoTrasf xst : versions) {
                    XfoStoricoTrasfRowBean row = (XfoStoricoTrasfRowBean) Transform.entity2RowBean(xst);
                    row.setString("scaricaTrasformazione", "Scarica");
                    table.add(row);
                }

            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                    | IllegalArgumentException | InvocationTargetException ex) {
                logger.error("Errore durante il recupero delle versioni della trasformazione: "
                        + ExceptionUtils.getRootCauseMessage(ex), ex);
                throw new ParerUserError("Errore durante il recupero delle versioni della trasformazione: "
                        + ExceptionUtils.getRootCauseMessage(ex));
            }
        }

        return table;
    }

    public PigTipoObjectTableBean searchAssignedPigTipoObjects(long idTrasf) throws ParerUserError {
        PigTipoObjectTableBean table = new PigTipoObjectTableBean();

        List<PigTipoObject> pigTipoObjects = helper.searchAssignedPigTipoObjects(idTrasf);
        if (pigTipoObjects != null && !pigTipoObjects.isEmpty()) {
            try {
                for (PigTipoObject pto : pigTipoObjects) {
                    PigTipoObjectRowBean row = (PigTipoObjectRowBean) Transform.entity2RowBean(pto);

                    row.setString("cd_versatore", pto.getPigVer().getNmVers());
                    row.setString("cd_ambiente", pto.getPigVer().getPigAmbienteVer().getNmAmbienteVers());

                    table.add(row);
                }

            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                    | IllegalArgumentException | InvocationTargetException ex) {
                logger.error("Errore durante il recupero delle versioni della trasformazione: "
                        + ExceptionUtils.getRootCauseMessage(ex), ex);
                throw new ParerUserError("Errore durante il recupero delle versioni della trasformazione: "
                        + ExceptionUtils.getRootCauseMessage(ex));
            }
        }

        return table;
    }

}