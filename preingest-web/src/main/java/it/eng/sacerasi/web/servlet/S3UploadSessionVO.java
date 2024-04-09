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

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.eng.sacerasi.web.servlet;

import software.amazon.awssdk.utils.IoUtils;
import it.eng.parer.objectstorage.helper.SalvataggioBackendHelper;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.model.CompleteMultipartUploadRequest;
import software.amazon.awssdk.services.s3.model.CompletedMultipartUpload;
import software.amazon.awssdk.services.s3.model.CompletedPart;
import software.amazon.awssdk.services.s3.model.CreateMultipartUploadRequest;
import software.amazon.awssdk.services.s3.model.CreateMultipartUploadResponse;
import software.amazon.awssdk.services.s3.model.UploadPartRequest;
import software.amazon.awssdk.services.s3.model.UploadPartResponse;
import it.eng.parer.objectstorage.dto.ObjectStorageBackend;
import it.eng.parer.objectstorage.exceptions.ObjectStorageException;

/**
 *
 * @author Cappelli_F
 */
public class S3UploadSessionVO {

    private static final Logger log = LoggerFactory.getLogger(S3UploadSessionVO.class);
    private BigDecimal idStrumentiUrbanistici;
    private String bucketName = null;
    private String keyName = null;
    private Date dataInizio = null;
    private Date dataFine = null;
    private SalvataggioBackendHelper salvataggioBackendHelper;
    private List<CompletedPart> partETags = null;
    private CreateMultipartUploadRequest initRequest = null;
    private CreateMultipartUploadResponse initResponse = null;
    private ObjectStorageBackend config;

    public String getKeyName() {
        return keyName;
    }

    public Date getDataInizio() {
        return dataInizio;
    }

    public Date getDataFine() {
        return dataFine;
    }

    public S3UploadSessionVO(SalvataggioBackendHelper salvataggioBackendHelper, ObjectStorageBackend config,
            String keyName) {
        this.bucketName = config.getBucket();
        this.keyName = keyName;
        this.salvataggioBackendHelper = salvataggioBackendHelper;
        this.config = config;
    }

    public S3UploadSessionVO(SalvataggioBackendHelper salvataggioBackendHelper, BigDecimal idStrumentiUrbanistici,
            String bucketName, String keyName) throws ObjectStorageException {
        this.idStrumentiUrbanistici = idStrumentiUrbanistici;
        this.bucketName = bucketName;
        this.keyName = keyName;
        this.salvataggioBackendHelper = salvataggioBackendHelper;
        ObjectStorageBackend config = salvataggioBackendHelper.getObjectStorageConfiguration("VERS_OGGETTO",
                bucketName);
        this.config = config;
    }

    private void start() {
        dataInizio = new Date();
        log.info(String.format("Inizio UploadMultipart to S3 [%s]", dataInizio));
        // Create a list of ETag objects. You retrieve ETags for each object part uploaded,
        // then, after each individual part has been uploaded, pass the list of ETags to
        // the request to complete the upload.
        partETags = new ArrayList<CompletedPart>();
        // Initiate the multipart upload.
        initRequest = CreateMultipartUploadRequest.builder().bucket(bucketName).key(keyName).build();
        initResponse = salvataggioBackendHelper.initiateMultipartUpload(initRequest, config);
        log.info("Multipart Upload a S3 Inizializzato.");
    }

    private void stop() {
        // Complete the multipart upload.
        CompleteMultipartUploadRequest compRequest = CompleteMultipartUploadRequest.builder()
                .uploadId(initResponse.uploadId()).bucket(bucketName).key(keyName)
                .multipartUpload(CompletedMultipartUpload.builder().parts(partETags).build()).build();

        salvataggioBackendHelper.completeMultipartUpload(compRequest, config);
        dataFine = new Date();
        log.info(String.format("Fine UploadMultipart to S3 [%s]", dataFine));
    }

    /*
     * Torna TRUE se Ã¨ stato l'upload dell'ultimo chunck e se il file Ã¨ stato caricato completamente
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
        log.info(String.format("Inizio l'update del chunk [%d] di [%d].", chunk, chunks));
        // Create the request to upload a part.
        UploadPartRequest uploadRequest = UploadPartRequest.builder().bucket(bucketName).key(keyName)
                .uploadId(initResponse.uploadId()).partNumber(chunk + 1).build();
        // Upload the part and add the response's ETag to our list.
        UploadPartResponse uploadResult = salvataggioBackendHelper.uploadPart(uploadRequest, bytes, config);
        log.info(String.format("Upload del chunk [%d] OK.", chunk, chunks));
        partETags.add(CompletedPart.builder().partNumber(chunk + 1).eTag(uploadResult.eTag()).build());
        if (isLastPart) {
            stop();
        }
        return isLastPart;
    }

    public boolean isOSActive() {
        return salvataggioBackendHelper.isActive();
    }
}
