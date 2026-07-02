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

package it.eng.sacerasi.web.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.parer.objectstorage.dto.ObjectStorageBackend;
import it.eng.parer.objectstorage.helper.BackendHelper;
import software.amazon.awssdk.services.s3.model.CompleteMultipartUploadRequest;
import software.amazon.awssdk.services.s3.model.CompletedMultipartUpload;
import software.amazon.awssdk.services.s3.model.CompletedPart;
import software.amazon.awssdk.services.s3.model.CreateMultipartUploadRequest;
import software.amazon.awssdk.services.s3.model.CreateMultipartUploadResponse;
import software.amazon.awssdk.services.s3.model.UploadPartRequest;
import software.amazon.awssdk.services.s3.model.UploadPartResponse;
import software.amazon.awssdk.utils.IoUtils;

/**
 *
 * @author Iacolucci_M
 */
public class S3UploadSessionSisma implements Serializable {

    private static final long serialVersionUID = 925103772459971617L;

    private static final Logger log = LoggerFactory.getLogger(S3UploadSessionSisma.class);
    private BigDecimal idSisma = null;
    private String bucketName = null;
    private String keyName = null;
    private Date dataInizio = null;
    private Date dataFine = null;
    private transient BackendHelper backendHelper;
    private List<CompletedPart> partETags = null;
    private transient CreateMultipartUploadRequest initRequest = null;
    private transient CreateMultipartUploadResponse initResponse = null;
    private ObjectStorageBackend config;

    public BigDecimal getIdSisma() {
        return idSisma;
    }

    public String getKeyName() {
        return keyName;
    }

    public Date getDataInizio() {
        return dataInizio;
    }

    public Date getDataFine() {
        return dataFine;
    }

    public S3UploadSessionSisma(BackendHelper backendHelper, BigDecimal idSisma, String keyName,
            ObjectStorageBackend config) {
        this.idSisma = idSisma;
        this.bucketName = config.getBucket();
        this.keyName = keyName;
        this.backendHelper = backendHelper;
        this.config = config;
    }

    public boolean existsOnOS() {
        return backendHelper.doesS3ObjectExist(this.config, this.keyName);
    }

    private void start() {
        dataInizio = new Date();
        log.info("Inizio UploadMultipart to S3 [{}]", dataInizio);
        // Create a list of ETag objects. You retrieve ETags for each object part uploaded,
        // then, after each individual part has been uploaded, pass the list of ETags to
        // the request to complete the upload.
        partETags = new ArrayList<>();
        // Initiate the multipart upload.
        initRequest = CreateMultipartUploadRequest.builder().bucket(bucketName).key(keyName)
                .build();
        initResponse = backendHelper.initiateS3MultipartUpload(initRequest, config);
        log.info("Multipart Upload Sisma a S3 Inizializzato.");
    }

    private void stop() {
        CompleteMultipartUploadRequest compRequest = CompleteMultipartUploadRequest.builder()
                .uploadId(initResponse.uploadId()).bucket(bucketName).key(keyName)
                .multipartUpload(CompletedMultipartUpload.builder().parts(partETags).build())
                .build();

        backendHelper.completeS3MultipartUpload(compRequest, config);
        dataFine = new Date();
        log.info("Fine Sisma UploadMultipart to S3 [{}]", dataFine);
    }

    /*
     * Torna TRUE se Ã¨ stato l'upload dell'ultimo chunck e se il file Ã¨ stato caricato
     * completamente
     */
    public boolean uploadChunk(InputStream input, int chunk, int chunks) {
        boolean isLastPart = (chunk == (chunks - 1)) ? true : false;
        if (chunk == 0) {
            start();
        }
        byte[] bytes = null;
        try {
            bytes = IoUtils.toByteArray(input);
        } catch (IOException ex) {
            log.error("Errore caricamento chunk su S3!", ex);
        }
        log.info("Inizio l'update del chunk Sisma [{}] di [{}].", chunk, chunks);
        // Create the request to upload a part.
        UploadPartRequest uploadRequest = UploadPartRequest.builder().bucket(bucketName)
                .key(keyName).uploadId(initResponse.uploadId()).partNumber(chunk + 1).build();
        // Upload the part and add the response's ETag to our list.
        UploadPartResponse uploadResult = backendHelper.uploadS3Part(uploadRequest, bytes, config);
        log.info("Upload del chunk Sisma [{}] OK.", chunk);
        partETags.add(
                CompletedPart.builder().partNumber(chunk + 1).eTag(uploadResult.eTag()).build());
        if (isLastPart) {
            stop();
        }
        return isLastPart;
    }

}
