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
package it.eng.parer.objectstorage.helper;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.commons.io.IOUtils;
import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import java.util.Optional;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.apache.commons.lang3.StringUtils;

import it.eng.parer.objectstorage.dto.BackendStorage;
import it.eng.parer.objectstorage.dto.ObjectStorageBackend;
import it.eng.parer.objectstorage.ejb.AwsClient;
import it.eng.parer.objectstorage.ejb.ObjectStorageConfigCache;
import it.eng.parer.objectstorage.exceptions.BackendException;
import it.eng.parer.objectstorage.exceptions.ObjectStorageException;
import it.eng.sacerasi.common.Constants;
import it.eng.sacerasi.entity.DecBackend;
import it.eng.sacerasi.entity.DecConfigObjectStorage;
import it.eng.sacerasi.web.helper.ConfigurationHelper;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CompleteMultipartUploadRequest;
import software.amazon.awssdk.services.s3.model.CompleteMultipartUploadResponse;
import software.amazon.awssdk.services.s3.model.CreateMultipartUploadRequest;
import software.amazon.awssdk.services.s3.model.CreateMultipartUploadResponse;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.model.UploadPartRequest;
import software.amazon.awssdk.services.s3.model.UploadPartResponse;

@Stateless(mappedName = "BackendHelper")
@LocalBean
public class BackendHelper {

    public static final String CONF_VERSAMENTO = "VERSAMENTO_OGGETTO";
    public static final String CONF_STRUMENTI_URBANISTICI = "COMPONENTI_SU";
    public static final String CONF_STRUMENTI_URBANISTICI_TRASFORMATI = "COMPONENTI_SU_TRASFORMATI";
    public static final String CONF_SISMA = "COMPONENTI_SISMA";
    public static final String CONF_SISMA_TRASFORMATI = "COMPONENTI_SISMA_TRASFORMATI";
    public static final String CONF_REPORT_TRASFORMAZIONI = "REPORT_TRASFORMAZIONI";

    private static final String BUCKET = "BUCKET";
    private static final String ACCESS_KEY_ID_SYS_PROP = "ACCESS_KEY_ID_SYS_PROP";
    private static final String SECRET_KEY_SYS_PROP = "SECRET_KEY_SYS_PROP";

    @EJB
    protected ConfigurationHelper configurationHelper;

    @EJB
    protected AwsClient s3Clients;

    @EJB
    protected ObjectStorageConfigCache configCache;

    @PersistenceContext(unitName = "SacerAsiJPA")
    private EntityManager entityManager;

    private static final int BUFFER_SIZE = 128 * 1024; // 128 KB

    /**
     * @param configuration configurazione per accedere all'object storage
     * @param objectKey     chiave
     *
     * @return InputStream dell'oggetto ottenuto
     *
     * @throws ObjectStorageException in caso di errore
     */
    public ResponseInputStream<GetObjectResponse> getS3Object(ObjectStorageBackend configuration,
            String objectKey) throws ObjectStorageException {
        try {
            S3Client s3Client = s3Clients.getClient(configuration.getAddress(),
                    configuration.getAccessKeyId(), configuration.getSecretKey());
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(configuration.getBucket()).key(objectKey).build();
            return s3Client.getObject(getObjectRequest);

        } catch (AwsServiceException | SdkClientException e) {
            throw ObjectStorageException.builder()
                    .message("{0}: impossibile ottenere dal bucket {1} oggetto con chiave {2}",
                            configuration.getBackendName(), configuration.getBucket(), objectKey)
                    .cause(e).build();
        }

    }

    /**
     * Scarica un oggetto S3 scrivendolo sull'OutputStream fornito tramite {@code IOUtils.copyLarge}
     * con buffer da {@value #BUFFER_SIZE} byte (10 MB).
     * <p>
     * Lo stream S3 viene sempre chiuso al termine tramite try-with-resources; il caller è
     * responsabile della chiusura dell'OutputStream di destinazione.
     * </p>
     *
     * @param configuration configurazione per accedere all'object storage
     * @param objectKey     chiave dell'oggetto
     * @param outputStream  stream di destinazione
     *
     * @throws ObjectStorageException in caso di errore S3
     * @throws IOException            in caso di errore I/O sulla scrittura
     */
    public void getS3Object(ObjectStorageBackend configuration, String objectKey,
            OutputStream outputStream) throws ObjectStorageException, IOException {
        try (ResponseInputStream<GetObjectResponse> object = getS3Object(configuration,
                objectKey)) {
            IOUtils.copyLarge(object, outputStream, new byte[BUFFER_SIZE]);
        }
    }

    public void putS3Object(ObjectStorageBackend config, String nomeOggetto, String contenuto,
            Optional<String> base64crc32c) throws ObjectStorageException {
        putS3Object(config, nomeOggetto, RequestBody.fromString(contenuto), base64crc32c);
    }

    public void putS3Object(ObjectStorageBackend config, String nomeOggetto, File file,
            Optional<String> base64crc32c) throws ObjectStorageException {
        putS3Object(config, nomeOggetto, RequestBody.fromFile(file), base64crc32c);
    }

    private void putS3Object(ObjectStorageBackend configuration, String objectKey,
            RequestBody requestBody, Optional<String> base64crc32c) throws ObjectStorageException {
        try {
            PutObjectRequest.Builder putObjectBuilder = PutObjectRequest.builder()
                    .bucket(configuration.getBucket()).key(objectKey);

            if (base64crc32c.isPresent()) {
                putObjectBuilder.checksumCRC32C(base64crc32c.get());
            }

            S3Client s3Client = s3Clients.getClient(configuration.getAddress(),
                    configuration.getAccessKeyId(), configuration.getSecretKey());

            PutObjectRequest objectRequest = putObjectBuilder.build();
            s3Client.putObject(objectRequest, requestBody);

        } catch (S3Exception e) {
            throw ObjectStorageException.builder()
                    .message("{0}: impossibile caricare sul bucket {1} oggetto con chiave {2}",
                            configuration.getBackendName(), configuration.getBucket(), objectKey)
                    .cause(e).build();
        }
    }

    public void deleteS3Object(ObjectStorageBackend configuration, String objectKey)
            throws ObjectStorageException {
        try {
            DeleteObjectRequest delOb = DeleteObjectRequest.builder()
                    .bucket(configuration.getBucket()).key(objectKey).build();

            S3Client s3Client = s3Clients.getClient(configuration.getAddress(),
                    configuration.getAccessKeyId(), configuration.getSecretKey());

            s3Client.deleteObject(delOb);
        } catch (S3Exception e) {
            throw ObjectStorageException.builder()
                    .message("{0}: impossibile eliminare dal bucket {1} oggetto con chiave {2}",
                            configuration.getBackendName(), configuration.getBucket(), objectKey)
                    .cause(e).build();
        }
    }

    public boolean doesS3ObjectExist(ObjectStorageBackend configuration, String key) {
        S3Client s3Client = s3Clients.getClient(configuration.getAddress(),
                configuration.getAccessKeyId(), configuration.getSecretKey());

        HeadObjectRequest objectRequest = HeadObjectRequest.builder().key(key)
                .bucket(configuration.getBucket()).build();

        try {
            s3Client.headObject(objectRequest);

            return true;

        } catch (S3Exception e) {
            return false;
        }
    }

    public CreateMultipartUploadResponse initiateS3MultipartUpload(
            CreateMultipartUploadRequest initiateMultipartUploadRequest,
            ObjectStorageBackend configuration) {
        S3Client s3Client = s3Clients.getClient(configuration.getAddress(),
                configuration.getAccessKeyId(), configuration.getSecretKey());
        return s3Client.createMultipartUpload(initiateMultipartUploadRequest);
    }

    public CompleteMultipartUploadResponse completeS3MultipartUpload(
            CompleteMultipartUploadRequest completeMultipartUploadRequest,
            ObjectStorageBackend configuration) {
        S3Client s3Client = s3Clients.getClient(configuration.getAddress(),
                configuration.getAccessKeyId(), configuration.getSecretKey());
        return s3Client.completeMultipartUpload(completeMultipartUploadRequest);
    }

    public UploadPartResponse uploadS3Part(UploadPartRequest uploadPartRequest, byte[] byteArray,
            ObjectStorageBackend configuration) {
        S3Client s3Client = s3Clients.getClient(configuration.getAddress(),
                configuration.getAccessKeyId(), configuration.getSecretKey());
        return s3Client.uploadPart(uploadPartRequest, RequestBody.fromBytes(byteArray));
    }

    public Long getS3ObjectSize(ObjectStorageBackend configuration, String key) {
        S3Client s3Client = s3Clients.getClient(configuration.getAddress(),
                configuration.getAccessKeyId(), configuration.getSecretKey());

        HeadObjectRequest objectRequest = HeadObjectRequest.builder().key(key)
                .bucket(configuration.getBucket()).build();
        return s3Client.headObject(objectRequest).contentLength();
    }

    /**
     * Ottieni la configurazione per potersi collegare a quel bucket dell'Object Storage scelto.
     *
     * @param nomeBackend nome del backend <strong> di tipo PIG_DEC_BACKEND.NM_TIPO_BACKEND = 'OS'
     *                    </strong>come censito su DEC_BACKEND (per esempio OBJECT_STORAGE_PRIMARIO)
     * @param tipoUsoOs   ambito di utilizzo di questo backend (per esempio STAGING)
     *
     * @return Configurazione dell'Object Storage per quell'ambito
     *
     * @throws ObjectStorageException in caso di errore
     */
    public ObjectStorageBackend getObjectStorageConfiguration(final String nomeBackend,
            final String tipoUsoOs) throws ObjectStorageException {
        ObjectStorageBackend cached = configCache.getOsConfig(nomeBackend, tipoUsoOs);
        if (cached != null) {
            return cached;
        }
        ObjectStorageBackend loaded = loadObjectStorageConfiguration(nomeBackend, tipoUsoOs);
        return configCache.putOsConfigIfAbsent(nomeBackend, tipoUsoOs, loaded);
    }

    private ObjectStorageBackend loadObjectStorageConfiguration(final String nomeBackend,
            final String tipoUsoOs) throws ObjectStorageException {
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
                nomeSystemPropertyAccessKeyId = decConfigObjectStorage
                        .getDsValoreConfigObjectStorage();
                break;
            case BUCKET:
                bucket = decConfigObjectStorage.getDsValoreConfigObjectStorage();
                break;
            case SECRET_KEY_SYS_PROP:
                nomeSystemPropertySecretKey = decConfigObjectStorage
                        .getDsValoreConfigObjectStorage();
                break;
            default:
                throw ObjectStorageException.builder().message(
                        "Impossibile stabilire la tipologia del parametro per l'object storage")
                        .build();
            }
            // identico per tutti perché definito nella tabella padre
            storageAddress = decConfigObjectStorage.getDecBackend().getDlBackendUri();
            backendId = decConfigObjectStorage.getDecBackend().getIdDecBackend();
        }
        if (StringUtils.isBlank(bucket) || StringUtils.isBlank(nomeSystemPropertyAccessKeyId)
                || StringUtils.isBlank(nomeSystemPropertySecretKey)
                || StringUtils.isBlank(storageAddress)) {
            throw ObjectStorageException.builder()
                    .message(
                            "Impossibile stabilire la tipologia del parametro per l'object storage")
                    .build();
        }
        final String finalBucket = bucket;
        final URI finalAddress = URI.create(storageAddress);
        final String finalAccessKeyId = System.getProperty(nomeSystemPropertyAccessKeyId);
        final String finalSecretKey = System.getProperty(nomeSystemPropertySecretKey);
        final Long finalBackendId = backendId;
        return new ObjectStorageBackend() {
            @Override
            public String getBackendName() {
                return nomeBackend;
            }

            @Override
            public URI getAddress() {
                return finalAddress;
            }

            @Override
            public String getBucket() {
                return finalBucket;
            }

            @Override
            public String getAccessKeyId() {
                return finalAccessKeyId;
            }

            @Override
            public String getSecretKey() {
                return finalSecretKey;
            }

            @Override
            public Long getBackendId() {
                return finalBackendId;
            }
        };
    }

    // MEV34843 restituisce il nome univoco del backend per i versamenti per il tipo oggetto
    // specificato.
    public String lookupBackendForVersamenti(BigDecimal idAmbienteVers, BigDecimal idVers,
            BigDecimal idTipoObject) {
        return configurationHelper.getValoreParamApplicByTipoObj(Constants.BACKEND_VERSAMENTI,
                idAmbienteVers, idVers, idTipoObject);
    }

    public String lookupBackendForSisma() {
        return configurationHelper.getValoreParamApplicByApplic(Constants.BACKEND_SISMA);
    }

    public String lookupBackendForStrumentiUrbanistici() {
        return configurationHelper
                .getValoreParamApplicByApplic(Constants.BACKEND_STRUMENTI_URBANISTICI);
    }

    public String lookupBackendForReportTrasformazioni() {
        return configurationHelper
                .getValoreParamApplicByApplic(Constants.BACKEND_REPORT_TRASFORMAZIONI);
    }

    public DecBackend getBackendEntity(String nomeBackend) {
        Long cachedId = configCache.getBackendId(nomeBackend);
        if (cachedId != null) {
            return entityManager.getReference(DecBackend.class, cachedId);
        }
        return loadBackendEntity(nomeBackend);
    }

    private DecBackend loadBackendEntity(String nomeBackend) {
        TypedQuery<DecBackend> query = entityManager.createQuery(
                "Select d from DecBackend d where d.nmBackend = :nomeBackend", DecBackend.class);
        query.setParameter("nomeBackend", nomeBackend);
        DecBackend backend = query.getSingleResult();
        configCache.putBackendIdIfAbsent(nomeBackend, backend.getIdDecBackend());
        return backend;
    }

    public DecBackend getBackendEntity(Long id) {
        return entityManager.find(DecBackend.class, id);
    }

    /**
     * Ottieni la configurazione del backend a partire dal nome del backend
     *
     * @param nomeBackend per esempio "OBJECT_STORAGE_PRIMARIO"
     *
     * @return Informazioni sul Backend identificato
     *
     * @throws BackendException in caso di errore
     */
    public BackendStorage getBackend(String nomeBackend) throws BackendException {
        BackendStorage cached = configCache.getBackend(nomeBackend);
        if (cached != null) {
            return cached;
        }
        try {
            DecBackend backend = getBackendEntity(nomeBackend);
            final BackendStorage.STORAGE_TYPE tipo = BackendStorage.STORAGE_TYPE
                    .valueOf(backend.getNmTipoBackend());
            final String nmBackend = backend.getNmBackend();
            final Long idBackend = backend.getIdDecBackend();
            BackendStorage loaded = new BackendStorage() {
                @Override
                public STORAGE_TYPE getType() {
                    return tipo;
                }

                @Override
                public String getBackendName() {
                    return nmBackend;
                }

                @Override
                public Long getBackendId() {
                    return idBackend;
                }
            };
            return configCache.putBackendIfAbsent(nomeBackend, loaded);
        } catch (IllegalArgumentException | NonUniqueResultException e) {
            throw BackendException.builder()
                    .message("Impossibile ottenere le informazioni di backend").cause(e).build();
        }
    }

    // MEV34843 - restituisce il backend per il versamento oggetti
    public BackendStorage getBackendForVersamento(BigDecimal idAmbienteVers, BigDecimal idVers,
            BigDecimal idTipoObject) throws BackendException {
        String backendName = lookupBackendForVersamenti(idAmbienteVers, idVers, idTipoObject);
        return getBackend(backendName);
    }

    public BackendStorage getBackendForSisma() throws BackendException {
        String backendName = lookupBackendForSisma();
        return getBackend(backendName);
    }

    public BackendStorage getBackendForStrumentiUrbanistici() throws BackendException {
        String backendName = lookupBackendForStrumentiUrbanistici();
        return getBackend(backendName);
    }

    public BackendStorage getBackendForReportTrasformazioni() throws BackendException {
        String backendName = lookupBackendForReportTrasformazioni();
        return getBackend(backendName);
    }

    public ObjectStorageBackend getObjectStorageConfigurationForVersamento(String nomeBackend)
            throws ObjectStorageException {
        return getObjectStorageConfiguration(nomeBackend, CONF_VERSAMENTO);
    }

    public ObjectStorageBackend getObjectStorageConfigurationForVersamento(String nomeBackend,
            String nomeBucketUtilizzato) throws ObjectStorageException {
        return overrideBucketNameOnOSConfiguration(
                getObjectStorageConfiguration(nomeBackend, CONF_VERSAMENTO), nomeBucketUtilizzato);
    }

    public ObjectStorageBackend getObjectStorageConfigurationForSisma(String nomeBackend)
            throws ObjectStorageException {
        return getObjectStorageConfiguration(nomeBackend, CONF_SISMA);
    }

    public ObjectStorageBackend getObjectStorageConfigurationForSismaTrasformati(String nomeBackend)
            throws ObjectStorageException {
        return getObjectStorageConfiguration(nomeBackend, CONF_SISMA_TRASFORMATI);
    }

    public ObjectStorageBackend getObjectStorageConfigurationForSisma(String nomeBackend,
            String nomeBucketUtilizzato) throws ObjectStorageException {
        return overrideBucketNameOnOSConfiguration(
                getObjectStorageConfiguration(nomeBackend, CONF_SISMA), nomeBucketUtilizzato);
    }

    public ObjectStorageBackend getObjectStorageConfigurationForStrumentiUrbanistici(
            String nomeBackend) throws ObjectStorageException {
        return getObjectStorageConfiguration(nomeBackend, CONF_STRUMENTI_URBANISTICI);
    }

    public ObjectStorageBackend getObjectStorageConfigurationForStrumentiUrbanisticiTrasformati(
            String nomeBackend) throws ObjectStorageException {
        return getObjectStorageConfiguration(nomeBackend, CONF_STRUMENTI_URBANISTICI_TRASFORMATI);
    }

    public ObjectStorageBackend getObjectStorageConfigurationForStrumentiUrbanistici(
            String nomeBackend, String nomeBucketUtilizzato) throws ObjectStorageException {
        return overrideBucketNameOnOSConfiguration(
                getObjectStorageConfiguration(nomeBackend, CONF_STRUMENTI_URBANISTICI),
                nomeBucketUtilizzato);
    }

    public ObjectStorageBackend getObjectStorageConfigurationForReportTrasformazioni(
            String nomeBackend) throws ObjectStorageException {
        return getObjectStorageConfiguration(nomeBackend, CONF_REPORT_TRASFORMAZIONI);
    }

    public ObjectStorageBackend getObjectStorageConfigurationForReportTrasformazioni(
            String nomeBackend, String nomeBucketUtilizzato) throws ObjectStorageException {
        return overrideBucketNameOnOSConfiguration(
                getObjectStorageConfiguration(nomeBackend, CONF_REPORT_TRASFORMAZIONI),
                nomeBucketUtilizzato);
    }

    private ObjectStorageBackend overrideBucketNameOnOSConfiguration(ObjectStorageBackend base,
            String newBucket) {
        return new ObjectStorageBackend() {
            @Override
            public String getBackendName() {
                return base.getBackendName();
            }

            @Override
            public java.net.URI getAddress() {
                return base.getAddress();
            }

            @Override
            public String getBucket() {
                return newBucket;
            }

            @Override
            public String getAccessKeyId() {
                return base.getAccessKeyId();
            }

            @Override
            public String getSecretKey() {
                return base.getSecretKey();
            }

            @Override
            public Long getBackendId() {
                return base.getBackendId();
            }
        };
    }

    /**
     * Ottieni la configurazione del backend a partire dall'id del backend
     *
     * @param backendId chiave primaria del record
     *
     * @return Informazioni sul Backend identificato
     *
     * @throws BackendException in caso di errore
     */
    public BackendStorage getBackend(Long backendId) throws BackendException {
        BackendStorage cached = configCache.getBackendById(backendId);
        if (cached != null) {
            return cached;
        }
        try {
            DecBackend backend = getBackendEntity(backendId);
            final BackendStorage.STORAGE_TYPE tipo = BackendStorage.STORAGE_TYPE
                    .valueOf(backend.getNmTipoBackend());
            final String nmBackend = backend.getNmBackend();
            final Long idBackend = backend.getIdDecBackend();
            BackendStorage loaded = new BackendStorage() {
                @Override
                public STORAGE_TYPE getType() {
                    return tipo;
                }

                @Override
                public String getBackendName() {
                    return nmBackend;
                }

                @Override
                public Long getBackendId() {
                    return idBackend;
                }
            };
            return configCache.putBackendByIdIfAbsent(backendId, loaded);
        } catch (IllegalArgumentException | NonUniqueResultException e) {
            throw BackendException.builder()
                    .message("Impossibile ottenere le informazioni di backend").cause(e).build();
        }
    }
}
