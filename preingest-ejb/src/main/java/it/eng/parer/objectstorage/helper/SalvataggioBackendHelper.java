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

import java.net.URI;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.apache.commons.lang3.StringUtils;

import it.eng.parer.objectstorage.dto.ObjectStorageBackend;
import it.eng.parer.objectstorage.ejb.AwsClient;
import it.eng.parer.objectstorage.exceptions.ObjectStorageException;
import it.eng.sacerasi.web.helper.ConfigurationHelper;
import it.eng.sacerasi.common.Constants;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
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

    @EJB
    protected ConfigurationHelper configurationHelper;

    @EJB
    protected AwsClient s3Clients;

    private boolean isActiveFlag = true;

    /**
     *
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

    public void putS3Object(ObjectStorageBackend config, String nomeOggetto, String contenuto)
            throws ObjectStorageException {
        putS3Object(config, nomeOggetto, RequestBody.fromString(contenuto));
    }

    public void putS3Object(ObjectStorageBackend config, String nomeOggetto, File file) throws ObjectStorageException {
        putS3Object(config, nomeOggetto, RequestBody.fromFile(file));
    }

    private void putS3Object(ObjectStorageBackend configuration, String objectKey, RequestBody requestBody)
            throws ObjectStorageException {
        try {
            Map<String, String> metadata = new HashMap<>();
            metadata.put("x-amz-meta-myVal", "test");
            PutObjectRequest putOb = PutObjectRequest.builder().bucket(configuration.getBucket()).key(objectKey)
                    .metadata(metadata).build();

            S3Client s3SourceClient = s3Clients.getClient(configuration.getAddress(), configuration.getAccessKeyId(),
                    configuration.getSecretKey());
            s3SourceClient.putObject(putOb, requestBody);

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

    public boolean isActive() {
        if (isActiveFlag) {
            final String isActiveSystemValue = System.getProperty(Constants.OBJECT_STORAGE_ENABLED);
            if (isActiveSystemValue != null) {
                isActiveFlag = !isActiveSystemValue.toLowerCase().trim().equals(Constants.OBJECT_STORAGE_DISATTIVO);
            }
        }

        return isActiveFlag;
    }

    /**
     * Ottieni la configurazione per potersi collegare a quel bucket dell'Object Storage scelto.
     *
     * @param nmService
     *            nome del backend <strong> di tipo DEC_BACKEND.NM_TIPO_BACKEND = 'OS' </strong>come censito su
     *            DEC_BACKEND (per esempio OBJECT_STORAGE_PRIMARIO)
     * @param nmBucket
     *            nome del bucket
     *
     * @return Configurazione dell'Object Storage per quell'ambito
     *
     * @throws ObjectStorageException
     *             in caso di errore
     */
    public ObjectStorageBackend getObjectStorageConfiguration(final String nmService, final String nmBucket)
            throws ObjectStorageException {
        String storageAddress = configurationHelper.getValoreParamApplicByApplic(Constants.OBJECT_STORAGE_ADDR);

        ArrayList<String> credentials = chooseCredentials(nmService);

        final String nomeSystemPropertyAccessKeyId = configurationHelper
                .getValoreParamApplicByApplic(credentials.get(0));
        final String nomeSystemPropertySecretKey = configurationHelper.getValoreParamApplicByApplic(credentials.get(1));
        // Istanzio il client http (possiede le chiamate al protocollo Amazon S3)

        if (StringUtils.isBlank(nmBucket) || StringUtils.isBlank(nomeSystemPropertyAccessKeyId)
                || StringUtils.isBlank(nomeSystemPropertySecretKey) || StringUtils.isBlank(storageAddress)) {
            throw ObjectStorageException.builder()
                    .message("Impossibile stabilire la tiplogia del parametro per l'object storage").build();
        }

        final String accessKeyId = System.getProperty(nomeSystemPropertyAccessKeyId);
        final String secretKey = System.getProperty(nomeSystemPropertySecretKey);
        final URI osURI = URI.create(storageAddress);
        final String stagingBucket = nmBucket;

        return new ObjectStorageBackend() {
            private static final long serialVersionUID = -7032516962480163852L;

            @Override
            public String getBackendName() {
                return nmService;
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
        };

    }

    private ArrayList<String> chooseCredentials(final String nmService) {
        ArrayList<String> credentials = new ArrayList<>();

        switch (nmService) {
        case "SISMA":
            credentials.add(Constants.SISMA_S3_ACCESS_KEY_ID);
            credentials.add(Constants.SISMA_S3_SECRET_KEY);
            credentials.add(Constants.BUCKET_VERSAMENTO_OGGETTO);
            break;
        case "VERS_OGGETTO":
            credentials.add(Constants.VO_S3_ACCESS_KEY_ID);
            credentials.add(Constants.VO_S3_SECRET_KEY);
            credentials.add(Constants.BUCKET_VERSAMENTO_OGGETTO);
            break;
        case "STR_URBANISTICI":
            credentials.add(Constants.SU_S3_ACCESS_KEY_ID);
            credentials.add(Constants.SU_S3_SECRET_KEY);
            credentials.add(Constants.BUCKET_VERIFICA_STRUMENTI_URBANISTICI);
            break;
        case "XF":
            credentials.add(Constants.XF_S3_ACCESS_KEY_ID);
            credentials.add(Constants.XF_S3_SECRET_KEY);
            credentials.add(Constants.BUCKET_REPORT_XFORMER);
            break;
        default:
            throw new IllegalArgumentException(
                    "non sono state configurate credenziali per object storage per il servizio " + nmService);
        }

        return credentials;
    }
}
