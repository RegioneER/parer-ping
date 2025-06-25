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
package it.eng.parer.objectstorage.helper;

import it.eng.parer.objectstorage.dto.BackendStorage;

import java.net.URI;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import it.eng.sacerasi.entity.*;
import it.eng.sacerasi.ws.notificaTrasferimento.dto.FileDepositatoType;
import it.eng.sacerasi.ws.notificaTrasferimento.dto.ListaFileDepositatoType;
import org.apache.commons.lang3.StringUtils;

import it.eng.parer.objectstorage.dto.ObjectStorageBackend;
import it.eng.parer.objectstorage.ejb.AwsClient;
import it.eng.parer.objectstorage.exceptions.ObjectStorageException;
import it.eng.sacerasi.web.helper.ConfigurationHelper;
import it.eng.sacerasi.common.Constants;
import it.eng.sacerasi.util.CRC32CChecksum;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CompleteMultipartUploadRequest;
import software.amazon.awssdk.services.s3.model.CompleteMultipartUploadResponse;
import software.amazon.awssdk.services.s3.model.CreateMultipartUploadResponse;
import software.amazon.awssdk.services.s3.model.CreateMultipartUploadRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.model.UploadPartRequest;
import software.amazon.awssdk.services.s3.model.UploadPartResponse;

@Stateless(mappedName = "SalvataggioBackendHelper")
@LocalBean
public class SalvataggioBackendHelper {

    public static final String CONF_VERSAMENTO = "VERSAMENTO_OGGETTO";
    public static final String CONF_STRUMENTI_URBANISTICI = "COMPONENTI_SU";
    public static final String CONF_STRUMENTI_URBANISTICI_TRASFORMATI = "COMPONENTI_SU_TRASFORMATI";
    public static final String CONF_SISMA = "COMPONENTI_SISMA";
    public static final String CONF_SISMA_TRASFORMATI = "COMPONENTI_SISMA_TRASFORMATI";
    public static final String CONF_REPORT_TRASFORMAZIONI = "REPORT_TRASFORMAZIONI";

    private static final int BUFFER_SIZE = 10 * 1024 * 1024;

    @EJB
    protected ConfigurationHelper configurationHelper;

    @EJB
    protected AwsClient s3Clients;

    @PersistenceContext(unitName = "SacerAsiJPA")
    private EntityManager entityManager;

    /**
     * @param configuration
     *            configurazione per accedere all'object storage
     * @param objectKey
     *            chiave
     *
     * @return InputStream dell'oggetto ottenuto
     *
     * @throws ObjectStorageException
     *             in caso di errore
     */
    public ResponseInputStream<GetObjectResponse> getObject(ObjectStorageBackend configuration, String objectKey)
            throws ObjectStorageException {
        try {
            S3Client s3SourceClient = s3Clients.getClient(configuration.getAddress(), configuration.getAccessKeyId(),
                    configuration.getSecretKey());
            GetObjectRequest getObjectRequest = GetObjectRequest.builder().bucket(configuration.getBucket())
                    .key(objectKey).build();
            return s3SourceClient.getObject(getObjectRequest);

        } catch (AwsServiceException | SdkClientException e) {
            throw ObjectStorageException.builder()
                    .message("{0}: impossibile ottenere dal bucket {1} oggetto con chiave {2}",
                            configuration.getBackendName(), configuration.getBucket(), objectKey)
                    .cause(e).build();
        }

    }

    public void putS3Object(ObjectStorageBackend config, String nomeOggetto, String contenuto,
            Optional<String> base64crc32c) throws ObjectStorageException {
        putS3Object(config, nomeOggetto, RequestBody.fromString(contenuto), base64crc32c);
    }

    public void putS3Object(ObjectStorageBackend config, String nomeOggetto, File file, Optional<String> base64crc32c)
            throws ObjectStorageException {
        putS3Object(config, nomeOggetto, RequestBody.fromFile(file), base64crc32c);
    }

    private void putS3Object(ObjectStorageBackend configuration, String objectKey, RequestBody requestBody,
            Optional<String> base64crc32c) throws ObjectStorageException {
        try {
            PutObjectRequest.Builder putObjectBuilder = PutObjectRequest.builder().bucket(configuration.getBucket())
                    .key(objectKey);

            if (base64crc32c.isPresent()) {
                putObjectBuilder.checksumCRC32C(base64crc32c.get());
            }

            S3Client s3SourceClient = s3Clients.getClient(configuration.getAddress(), configuration.getAccessKeyId(),
                    configuration.getSecretKey());

            PutObjectRequest objectRequest = putObjectBuilder.build();
            s3SourceClient.putObject(objectRequest, requestBody);

        } catch (S3Exception e) {
            throw ObjectStorageException.builder()
                    .message("{0}: impossibile caricare sul bucket {1} oggetto con chiave {2}",
                            configuration.getBackendName(), configuration.getBucket(), objectKey)
                    .cause(e).build();
        }
    }

    public void deleteObject(ObjectStorageBackend configuration, String objectKey) throws ObjectStorageException {
        try {
            DeleteObjectRequest delOb = DeleteObjectRequest.builder().bucket(configuration.getBucket()).key(objectKey)
                    .build();

            S3Client s3SourceClient = s3Clients.getClient(configuration.getAddress(), configuration.getAccessKeyId(),
                    configuration.getSecretKey());

            s3SourceClient.deleteObject(delOb);
        } catch (S3Exception e) {
            throw ObjectStorageException.builder()
                    .message("{0}: impossibile eliminare dal bucket {1} oggetto con chiave {2}",
                            configuration.getBackendName(), configuration.getBucket(), objectKey)
                    .cause(e).build();
        }
    }

    public boolean doesObjectExist(ObjectStorageBackend configuration, String key) {
        S3Client s3SourceClient = s3Clients.getClient(configuration.getAddress(), configuration.getAccessKeyId(),
                configuration.getSecretKey());

        HeadObjectRequest objectRequest = HeadObjectRequest.builder().key(key).bucket(configuration.getBucket())
                .build();

        try {
            s3SourceClient.headObject(objectRequest);

            return true;

        } catch (S3Exception e) {
            return false;
        }
    }

    public CreateMultipartUploadResponse initiateMultipartUpload(
            CreateMultipartUploadRequest initiateMultipartUploadRequest, ObjectStorageBackend configuration) {
        S3Client s3SourceClient = s3Clients.getClient(configuration.getAddress(), configuration.getAccessKeyId(),
                configuration.getSecretKey());
        return s3SourceClient.createMultipartUpload(initiateMultipartUploadRequest);
    }

    public CompleteMultipartUploadResponse completeMultipartUpload(
            CompleteMultipartUploadRequest completeMultipartUploadRequest, ObjectStorageBackend configuration) {
        S3Client s3SourceClient = s3Clients.getClient(configuration.getAddress(), configuration.getAccessKeyId(),
                configuration.getSecretKey());
        return s3SourceClient.completeMultipartUpload(completeMultipartUploadRequest);
    }

    public UploadPartResponse uploadPart(UploadPartRequest uploadPartRequest, byte[] byteArray,
            ObjectStorageBackend configuration) {
        S3Client s3SourceClient = s3Clients.getClient(configuration.getAddress(), configuration.getAccessKeyId(),
                configuration.getSecretKey());
        return s3SourceClient.uploadPart(uploadPartRequest, RequestBody.fromBytes(byteArray));
    }

    public Long getObjectSize(ObjectStorageBackend configuration, String key) {
        S3Client s3SourceClient = s3Clients.getClient(configuration.getAddress(), configuration.getAccessKeyId(),
                configuration.getSecretKey());

        HeadObjectRequest objectRequest = HeadObjectRequest.builder().key(key).bucket(configuration.getBucket())
                .build();
        return s3SourceClient.headObject(objectRequest).contentLength();
    }

    private static final String BUCKET = "BUCKET";
    private static final String ACCESS_KEY_ID_SYS_PROP = "ACCESS_KEY_ID_SYS_PROP";
    private static final String SECRET_KEY_SYS_PROP = "SECRET_KEY_SYS_PROP";

    /**
     * Ottieni la configurazione per potersi collegare a quel bucket dell'Object Storage scelto.
     *
     * @param nomeBackend
     *            nome del backend <strong> di tipo PIG_DEC_BACKEND.NM_TIPO_BACKEND = 'OS' </strong>come censito su
     *            DEC_BACKEND (per esempio OBJECT_STORAGE_PRIMARIO)
     * @param tipoUsoOs
     *            ambito di utilizzo di questo backend (per esempio STAGING)
     *
     * @return Configurazione dell'Object Storage per quell'ambito
     *
     * @throws ObjectStorageException
     *             in caso di errore
     */
    public ObjectStorageBackend getObjectStorageConfiguration(final String nomeBackend, final String tipoUsoOs)
            throws ObjectStorageException {
        TypedQuery<DecConfigObjectStorage> query = entityManager.createQuery(
                "Select c from DecConfigObjectStorage c where c.tiUsoConfigObjectStorage = :tipoUsoOs and c.decBackend.nmBackend = :nomeBackend order by c.nmConfigObjectStorage",
                DecConfigObjectStorage.class);
        query.setParameter("tipoUsoOs", tipoUsoOs);
        query.setParameter("nomeBackend", nomeBackend);
        List<DecConfigObjectStorage> resultList = query.getResultList();
        String bucket = null;
        String nomeSystemPropertyAccessKeyId = null;
        String nomeSystemPropertySecretKey = null;
        String storageAddress = null;
        Long backendId = null;

        for (DecConfigObjectStorage decConfigObjectStorage : resultList) {
            switch (decConfigObjectStorage.getNmConfigObjectStorage()) {
            case ACCESS_KEY_ID_SYS_PROP:
                nomeSystemPropertyAccessKeyId = decConfigObjectStorage.getDsValoreConfigObjectStorage();
                break;
            case BUCKET:
                bucket = decConfigObjectStorage.getDsValoreConfigObjectStorage();
                break;
            case SECRET_KEY_SYS_PROP:
                nomeSystemPropertySecretKey = decConfigObjectStorage.getDsValoreConfigObjectStorage();
                break;
            default:
                throw ObjectStorageException.builder()
                        .message("Impossibile stabilire la tipologia del parametro per l'object storage").build();
            }
            // identico per tutti perché definito nella tabella padre
            storageAddress = decConfigObjectStorage.getDecBackend().getDlBackendUri();
            backendId = decConfigObjectStorage.getDecBackend().getIdDecBackend();
        }
        if (StringUtils.isBlank(bucket) || StringUtils.isBlank(nomeSystemPropertyAccessKeyId)
                || StringUtils.isBlank(nomeSystemPropertySecretKey) || StringUtils.isBlank(storageAddress)) {
            throw ObjectStorageException.builder()
                    .message("Impossibile stabilire la tipologia del parametro per l'object storage").build();
        }

        final String accessKeyId = System.getProperty(nomeSystemPropertyAccessKeyId);
        final String secretKey = System.getProperty(nomeSystemPropertySecretKey);
        final URI osURI = URI.create(storageAddress);
        final String stagingBucket = bucket;
        final Long idBackend = backendId;

        return new ObjectStorageBackend() {
            private static final long serialVersionUID = -7032516962480163852L;

            @Override
            public String getBackendName() {
                return nomeBackend;
            }

            @Override
            public URI getAddress() {
                return osURI;
            }

            @Override
            public String getBucket() {
                return stagingBucket;
            }

            @Override
            public String getAccessKeyId() {
                return accessKeyId;
            }

            @Override
            public String getSecretKey() {
                return secretKey;
            }

            @Override
            public Long getBackendId() {
                return idBackend;
            }
        };
    }

    // MEV34843 restituisce il nome univoco del backend per i versamenti per il tipo oggetto specificato.
    public String lookupBackendForVersamenti(BigDecimal idAmbienteVers, BigDecimal idVers, BigDecimal idTipoObject) {
        return configurationHelper.getValoreParamApplicByTipoObj(Constants.BACKEND_VERSAMENTI, idAmbienteVers, idVers,
                idTipoObject);
    }

    public String lookupBackendForSisma() {
        return configurationHelper.getValoreParamApplicByApplic(Constants.BACKEND_SISMA);
    }

    public String lookupBackendForStrumentiUrbanistici() {
        return configurationHelper.getValoreParamApplicByApplic(Constants.BACKEND_STRUMENTI_URBANISTICI);
    }

    public String lookupBackendForReportTrasformazioni() {
        return configurationHelper.getValoreParamApplicByApplic(Constants.BACKEND_REPORT_TRASFORMAZIONI);
    }

    public DecBackend getBackendEntity(String nomeBackend) {
        TypedQuery<DecBackend> query = entityManager
                .createQuery("Select d from DecBackend d where d.nmBackend = :nomeBackend", DecBackend.class);
        query.setParameter("nomeBackend", nomeBackend);
        return query.getSingleResult();
    }

    public DecBackend getBackendEntity(Long id) {
        return entityManager.find(DecBackend.class, id);
    }

    /**
     * Ottieni la configurazione del backend a partire dal nome del backend
     *
     * @param nomeBackend
     *            per esempio "OBJECT_STORAGE_PRIMARIO"
     *
     * @return Informazioni sul Backend identificato
     *
     * @throws ObjectStorageException
     *             in caso di errore
     */
    public BackendStorage getBackend(String nomeBackend) throws ObjectStorageException {
        try {

            DecBackend backend = getBackendEntity(nomeBackend);
            final BackendStorage.STORAGE_TYPE type = BackendStorage.STORAGE_TYPE.valueOf(backend.getNmTipoBackend());
            final String backendName = backend.getNmBackend();
            final Long idBackend = backend.getIdDecBackend();

            return new BackendStorage() {
                private static final long serialVersionUID = 5092016605462729859L;

                @Override
                public BackendStorage.STORAGE_TYPE getType() {
                    return type;
                }

                @Override
                public String getBackendName() {
                    return backendName;
                }

                @Override
                public Long getBackendId() {
                    return idBackend;
                }
            };

        } catch (IllegalArgumentException | NonUniqueResultException e) {
            throw ObjectStorageException.builder().message("Impossibile ottenere le informazioni di backend").cause(e)
                    .build();
        }

    }

    // MEV34843 - restituisce il backend per il versamento oggetti
    public BackendStorage getBackendForVersamento(BigDecimal idAmbienteVers, BigDecimal idVers, BigDecimal idTipoObject)
            throws ObjectStorageException {
        String backendName = lookupBackendForVersamenti(idAmbienteVers, idVers, idTipoObject);
        return getBackend(backendName);
    }

    public BackendStorage getBackendForSisma() throws ObjectStorageException {
        String backendName = lookupBackendForSisma();
        return getBackend(backendName);
    }

    public BackendStorage getBackendForStrumentiUrbanistici() throws ObjectStorageException {
        String backendName = lookupBackendForStrumentiUrbanistici();
        return getBackend(backendName);
    }

    public BackendStorage getBackendForReportTrasformazioni() throws ObjectStorageException {
        String backendName = lookupBackendForReportTrasformazioni();
        return getBackend(backendName);
    }

    public ObjectStorageBackend getObjectStorageConfigurationForVersamento(String nomeBackend)
            throws ObjectStorageException {
        return getObjectStorageConfiguration(nomeBackend, CONF_VERSAMENTO);
    }

    public ObjectStorageBackend getObjectStorageConfigurationForVersamento(String nomeBackend,
            String nomeBucketUtilizzato) throws ObjectStorageException {
        ObjectStorageBackend objectStorageConfiguration = getObjectStorageConfiguration(nomeBackend, CONF_VERSAMENTO);
        return new ObjectStorageBackend() {
            private static final long serialVersionUID = -7032516962480163852L;

            @Override
            public String getBackendName() {
                return nomeBackend;
            }

            @Override
            public URI getAddress() {
                return objectStorageConfiguration.getAddress();
            }

            @Override
            public String getBucket() {
                return nomeBucketUtilizzato;
            }

            @Override
            public String getAccessKeyId() {
                return objectStorageConfiguration.getAccessKeyId();
            }

            @Override
            public String getSecretKey() {
                return objectStorageConfiguration.getSecretKey();
            }

            @Override
            public Long getBackendId() {
                return objectStorageConfiguration.getBackendId();
            }
        };
    }

    public ObjectStorageBackend getObjectStorageConfigurationForSisma(String nomeBackend)
            throws ObjectStorageException {
        return getObjectStorageConfiguration(nomeBackend, CONF_SISMA);
    }

    public ObjectStorageBackend getObjectStorageConfigurationForSismaTrasformati(String nomeBackend)
            throws ObjectStorageException {
        return getObjectStorageConfiguration(nomeBackend, CONF_SISMA_TRASFORMATI);
    }

    public ObjectStorageBackend getObjectStorageConfigurationForSisma(String nomeBackend, String nomeBucketUtilizzato)
            throws ObjectStorageException {
        ObjectStorageBackend objectStorageConfiguration = getObjectStorageConfiguration(nomeBackend, CONF_SISMA);
        return new ObjectStorageBackend() {
            private static final long serialVersionUID = -7032516962480163852L;

            @Override
            public String getBackendName() {
                return nomeBackend;
            }

            @Override
            public URI getAddress() {
                return objectStorageConfiguration.getAddress();
            }

            @Override
            public String getBucket() {
                return nomeBucketUtilizzato;
            }

            @Override
            public String getAccessKeyId() {
                return objectStorageConfiguration.getAccessKeyId();
            }

            @Override
            public String getSecretKey() {
                return objectStorageConfiguration.getSecretKey();
            }

            @Override
            public Long getBackendId() {
                return objectStorageConfiguration.getBackendId();
            }
        };
    }

    public ObjectStorageBackend getObjectStorageConfigurationForStrumentiUrbanistici(String nomeBackend)
            throws ObjectStorageException {
        return getObjectStorageConfiguration(nomeBackend, CONF_STRUMENTI_URBANISTICI);
    }

    public ObjectStorageBackend getObjectStorageConfigurationForStrumentiUrbanisticiTrasformati(String nomeBackend)
            throws ObjectStorageException {
        return getObjectStorageConfiguration(nomeBackend, CONF_STRUMENTI_URBANISTICI_TRASFORMATI);
    }

    public ObjectStorageBackend getObjectStorageConfigurationForStrumentiUrbanistici(String nomeBackend,
            String nomeBucketUtilizzato) throws ObjectStorageException {
        ObjectStorageBackend objectStorageConfiguration = getObjectStorageConfiguration(nomeBackend,
                CONF_STRUMENTI_URBANISTICI);
        return new ObjectStorageBackend() {
            private static final long serialVersionUID = -7032516962480163852L;

            @Override
            public String getBackendName() {
                return nomeBackend;
            }

            @Override
            public URI getAddress() {
                return objectStorageConfiguration.getAddress();
            }

            @Override
            public String getBucket() {
                return nomeBucketUtilizzato;
            }

            @Override
            public String getAccessKeyId() {
                return objectStorageConfiguration.getAccessKeyId();
            }

            @Override
            public String getSecretKey() {
                return objectStorageConfiguration.getSecretKey();
            }

            @Override
            public Long getBackendId() {
                return objectStorageConfiguration.getBackendId();
            }
        };
    }

    public ObjectStorageBackend getObjectStorageConfigurationForReportTrasformazioni(String nomeBackend)
            throws ObjectStorageException {
        return getObjectStorageConfiguration(nomeBackend, CONF_REPORT_TRASFORMAZIONI);
    }

    public ObjectStorageBackend getObjectStorageConfigurationForReportTrasformazioni(String nomeBackend,
            String nomeBucketUtilizzato) throws ObjectStorageException {
        ObjectStorageBackend objectStorageConfiguration = getObjectStorageConfiguration(nomeBackend,
                CONF_REPORT_TRASFORMAZIONI);
        return new ObjectStorageBackend() {
            private static final long serialVersionUID = -7032516962480163852L;

            @Override
            public String getBackendName() {
                return nomeBackend;
            }

            @Override
            public URI getAddress() {
                return objectStorageConfiguration.getAddress();
            }

            @Override
            public String getBucket() {
                return nomeBucketUtilizzato;
            }

            @Override
            public String getAccessKeyId() {
                return objectStorageConfiguration.getAccessKeyId();
            }

            @Override
            public String getSecretKey() {
                return objectStorageConfiguration.getSecretKey();
            }

            @Override
            public Long getBackendId() {
                return objectStorageConfiguration.getBackendId();
            }
        };
    }

    /**
     * Ottieni la configurazione del backend a partire dall'id del backend
     *
     * @param backendId
     *            chiave primaria del record
     *
     * @return Informazioni sul Backend identificato
     *
     * @throws ObjectStorageException
     *             in caso di errore
     */
    public BackendStorage getBackend(Long backendId) throws ObjectStorageException {
        try {

            DecBackend backend = getBackendEntity(backendId);
            final BackendStorage.STORAGE_TYPE type = BackendStorage.STORAGE_TYPE.valueOf(backend.getNmTipoBackend());
            final String backendName = backend.getNmBackend();
            final Long idBackend = backend.getIdDecBackend();

            return new BackendStorage() {
                private static final long serialVersionUID = 5092016605462729859L;

                @Override
                public BackendStorage.STORAGE_TYPE getType() {
                    return type;
                }

                @Override
                public String getBackendName() {
                    return backendName;
                }

                @Override
                public Long getBackendId() {
                    return idBackend;
                }
            };

        } catch (IllegalArgumentException | NonUniqueResultException e) {
            throw ObjectStorageException.builder().message("Impossibile ottenere le informazioni di backend").cause(e)
                    .build();
        }

    }

    public void addBackendInfosToFilesDepositati(String nmAmbiente, String nmVersatore, String cdKeyObject,
            ListaFileDepositatoType listaFileDepositati) throws ObjectStorageException {
        String queryStr = "SELECT obj FROM PigObject obj INNER JOIN obj.pigVer vers "
                + "WHERE vers.pigAmbienteVer.nmAmbienteVers = :nmAmbiente AND vers.nmVers = :nmVers AND obj.cdKeyObject = :cdKey";
        javax.persistence.Query query = entityManager.createQuery(queryStr);
        query.setParameter("nmAmbiente", nmAmbiente);
        query.setParameter("nmVers", nmVersatore);
        query.setParameter("cdKey", cdKeyObject);

        List<PigObject> results = query.getResultList();
        if (results.isEmpty()) {
            throw ObjectStorageException.builder()
                    .message("Impossibile ottenere le informazioni di backend, oggetto non trovato.").build();
        }

        PigObject obj = results.get(0);
        PigVers vers = obj.getPigVer();
        PigAmbienteVers ambienteVers = vers.getPigAmbienteVer();

        BackendStorage backendVersamento = getBackendForVersamento(BigDecimal.valueOf(ambienteVers.getIdAmbienteVers()),
                BigDecimal.valueOf(vers.getIdVers()), BigDecimal.valueOf(obj.getPigTipoObject().getIdTipoObject()));

        String tenantOs = configurationHelper.getValoreParamApplicByApplic(Constants.TENANT_OBJECT_STORAGE);

        for (FileDepositatoType file : listaFileDepositati.getFileDepositato()) {
            file.setIdBackend(backendVersamento.getBackendId());

            if (backendVersamento.isObjectStorage()) {
                ObjectStorageBackend config = getObjectStorageConfigurationForVersamento(
                        backendVersamento.getBackendName());

                file.setNmOsTenant(tenantOs);
                file.setNmOsBucket(config.getBucket());
            }
        }
    }

    /**
     * Calcola il checksum CRC32C (base64 encoded) del file da inviare via S3
     * <p>
     * Nota: questa scelta deriva dal modello supportato dal vendor
     * (https://docs.aws.amazon.com/AmazonS3/latest/userguide/checking-object-integrity.html)
     *
     * @param resource
     *            file
     *
     * @return rappresentazione base64 del contenuto calcolato
     *
     * @throws IOException
     *             errore generico
     */
    private String calculateFileCRC32CBase64(Path resource) throws IOException {
        byte[] buffer = new byte[BUFFER_SIZE];
        int readed;
        CRC32CChecksum crc32c = new CRC32CChecksum();
        try (InputStream is = Files.newInputStream(resource)) {
            while ((readed = is.read(buffer)) != -1) {
                crc32c.update(buffer, 0, readed);
            }
        }
        return Base64.getEncoder().encodeToString(crc32c.getValueAsBytes());
    }
}
