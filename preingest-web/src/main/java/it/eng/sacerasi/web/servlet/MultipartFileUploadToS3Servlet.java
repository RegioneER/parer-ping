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

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.parer.objectstorage.dto.ObjectStorageBackend;
import it.eng.parer.objectstorage.helper.SalvataggioBackendHelper;
import it.eng.sacerasi.common.Constants;
import it.eng.sacerasi.slite.gen.form.StrumentiUrbanisticiForm;
import it.eng.sacerasi.strumentiUrbanistici.ejb.StrumentiUrbanisticiEjb;
import it.eng.sacerasi.web.helper.ConfigurationHelper;

@WebServlet("/MultipartFileUploadToS3Servlet")
public class MultipartFileUploadToS3Servlet extends HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(MultipartFileUploadToS3Servlet.class);
    private static final long serialVersionUID = 3447685998419256747L;
    private static final String RESP_SUCCESS = "{\"jsonrpc\" : \"2.0\", \"result\" : \"success\", \"id\" : \"id\"}";
    private static final String RESP_ERROR = "{\"jsonrpc\" : \"2.0\", \"error\" : {\"code\": 501, \"message\": \"Errore nell'apertura dello stream.\"}, \"id\" : \"id\"}";
    private static final String RESP_ERROR_FILE_ALREADY_EXISTS = "{\"jsonrpc\" : \"2.0\", \"error\" : {\"code\": 502, \"message\": \"Il file gia' esiste sull'object storage!\"}, \"id\" : \"id\"}";

    public static final String JSON = "application/json";
    public static final int BUF_SIZE = 2 * 1024;

    @EJB(mappedName = "java:app/SacerAsync-ejb/StrumentiUrbanisticiEjb")
    private StrumentiUrbanisticiEjb strumentiUrbanisticiEjb;
    @EJB(mappedName = "java:app/SacerAsync-ejb/ConfigurationHelper")
    private ConfigurationHelper configurationHelper;
    @EJB
    private SalvataggioBackendHelper salvataggioBackendHelper;

    /**
     * Handles an HTTP POST request from Plupload.
     *
     * @param req
     *            The HTTP request
     * @param resp
     *            The HTTP response
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String responseString = RESP_SUCCESS;
        boolean isMultipart = ServletFileUpload.isMultipartContent(req);
        boolean inErrore = false;
        int chunk = 0;
        int chunks = 0;
        String nomeFile = null;
        String nomeCampo = null;
        String idSessione = null;
        String nmTipoDocumento = null;
        S3UploadSessionSU s3UploadSession = null;
        BigDecimal dimensione = null;

        if (isMultipart) {
            ServletFileUpload upload = new ServletFileUpload();
            try {
                FileItemIterator iter = upload.getItemIterator(req);
                while (iter.hasNext()) {
                    FileItemStream item = iter.next();
                    InputStream input = item.openStream();
                    // Gestisce un form field.
                    if (item.isFormField()) {
                        nomeCampo = item.getFieldName();
                        String value = Streams.asString(input);
                        if ("name".equals(nomeCampo)) {
                            nomeFile = value;
                        } else if ("chunks".equals(nomeCampo)) {
                            chunks = Integer.parseInt(value);
                        } else if ("chunk".equals(nomeCampo)) {
                            chunk = Integer.parseInt(value);
                        } else if ("nmTipoDocumento".equals(nomeCampo)) {
                            nmTipoDocumento = value;
                        } else if ("idSessione".equals(nomeCampo)) {
                            idSessione = value;
                        } else if ("dimensione".equals(nomeCampo)) {
                            dimensione = new BigDecimal(value);
                        }
                    } // Gestisce un multi-part MIME encoded file.
                    else {
                        if (chunk == 0) {
                            /*
                             * Prima di iniziare determina il nome del FileOS e lo mette in sessione di Upload. Ottiene
                             * dalla sessione l'ID dello strumento urbanistico
                             */
                            StrumentiUrbanisticiForm form = (StrumentiUrbanisticiForm) req.getSession()
                                    .getAttribute("###_FORM_CONTAINER");
                            BigDecimal idStrumento = form.getDatiGeneraliOutput().getId_strumenti_urbanistici_out()
                                    .parse();
                            ObjectStorageBackend config = salvataggioBackendHelper.getObjectStorageConfiguration(
                                    "STR_URBANISTICI", configurationHelper.getValoreParamApplicByApplic(
                                            Constants.BUCKET_VERIFICA_STRUMENTI_URBANISTICI));

                            String nmFieOs = strumentiUrbanisticiEjb.getFileOsNameBySU(idStrumento, nomeFile);
                            s3UploadSession = new S3UploadSessionSU(salvataggioBackendHelper, idStrumento,
                                    config.getBucket(), nmFieOs);
                            if (s3UploadSession.existsFileOnOS()) {
                                responseString = RESP_ERROR_FILE_ALREADY_EXISTS;
                                log.info(String.format("Il file [%s] già esiste sull'object storage!", nmFieOs));
                                inErrore = true;
                            } else {
                                req.getSession().setAttribute(idSessione, s3UploadSession);
                            }
                        } else {
                            s3UploadSession = (S3UploadSessionSU) req.getSession().getAttribute(idSessione);
                        }
                        /* Fa l'upload del chunk, se è l'ultimo registra il documento anche su DB */
                        if (inErrore == false && s3UploadSession.uploadChunk(input, chunk, chunks)) {
                            StrumentiUrbanisticiEjb.DocStrumDto dto = new StrumentiUrbanisticiEjb.DocStrumDto();
                            dto.setIdStrumentiUrbanistici(s3UploadSession.getIdStrumentiUrbanistici());
                            dto.setNmFileOrig(nomeFile);
                            dto.setNmFileOs(s3UploadSession.getKeyName());
                            dto.setNmTipoDocumento(nmTipoDocumento);
                            dto.setDimensione(dimensione);
                            strumentiUrbanisticiEjb.salvaTipoDocumento(dto);
                        }
                    }
                }
            } catch (Exception e) {
                responseString = RESP_ERROR;
                log.error("Eccezione", e);
            }
        } // Non una multi-part MIME request.
        else {
            responseString = RESP_ERROR;
        }
        if (chunk == chunks - 1) {
            log.debug("nome：" + nomeFile);
            log.debug("nmTipoDocumento：" + nmTipoDocumento);
            req.getSession().removeAttribute(idSessione);
        }
        resp.setContentType(JSON);
        byte[] responseBytes = responseString.getBytes();
        resp.setContentLength(responseBytes.length);
        ServletOutputStream output = resp.getOutputStream();
        output.write(responseBytes);
        output.flush();
    }

}
