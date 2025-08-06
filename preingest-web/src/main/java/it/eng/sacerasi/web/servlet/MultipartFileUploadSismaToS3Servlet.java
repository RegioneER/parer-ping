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

import it.eng.parer.objectstorage.dto.BackendStorage;
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
import it.eng.sacerasi.sisma.dto.DocSismaDto;
import it.eng.sacerasi.sisma.dto.SismaDto;
import it.eng.sacerasi.sisma.ejb.SismaEjb;
import it.eng.sacerasi.slite.gen.form.SismaForm;
import it.eng.sacerasi.versamento.ejb.VersamentoOggettoEjb;

@WebServlet("/MultipartFileUploadSismaToS3Servlet")
public class MultipartFileUploadSismaToS3Servlet extends HttpServlet {

    private static final Logger log = LoggerFactory
	    .getLogger(MultipartFileUploadSismaToS3Servlet.class);

    private static final long serialVersionUID = 3447685998419256747L;
    private static final String RESP_SUCCESS = "{\"jsonrpc\" : \"2.0\", \"result\" : \"success\", \"id\" : \"id\"}";
    private static final String RESP_ERROR = "{\"jsonrpc\" : \"2.0\", \"error\" : {\"code\": 501, \"message\": \"Errore nell'apertura dello stream.\"}, \"id\" : \"id\"}";
    private static final String RESP_ERROR_FILE_ALREADY_EXISTS = "{\"jsonrpc\" : \"2.0\", \"error\" : {\"code\": 502, \"message\": \"Il file gia' esiste sull'object storage!\"}, \"id\" : \"id\"}";

    public static final String JSON = "application/json";
    public static final int BUF_SIZE = 2 * 1024;

    private static final String PREFISSO_SESSIONE = "SISMA_";

    @EJB(mappedName = "java:app/SacerAsync-ejb/SismaEjb")
    private SismaEjb sismaEjb;
    @EJB(mappedName = "java:app/SacerAsync-ejb/SalvataggioBackendHelper")
    private SalvataggioBackendHelper salvataggioBackendHelper;
    @EJB(mappedName = "java:app/SacerAsync-ejb/VersamentoOggettoEjb")
    private VersamentoOggettoEjb versamentoOggettoEjb;

    /**
     * Handles an HTTP POST request from Plupload.
     *
     * @param req  The HTTP request
     * @param resp The HTTP response
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
	    throws ServletException, IOException {
	String responseString = RESP_SUCCESS;
	boolean isMultipart = ServletFileUpload.isMultipartContent(req);
	boolean inErrore = false;
	int chunk = 0;
	int chunks = 0;
	String nomeFile = null;
	String nomeCampo = null;
	String idSessione = null;
	String nmTipoDocumento = null;
	String tiVerificaAgenzia = null;
	S3UploadSessionSisma s3UploadSessionSisma = null;
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
			    /*
			     * Questo nuovo parametro lo memorizza per creare poi il tipo documento
			     * in sostituzione di quello che era stato eventualmente fleggato
			     * dall'Agenzia con KO
			     */
			} else if ("tiVerificaAgenzia".equals(nomeCampo)) {
			    // Se dal web fiene passato null in java viene ricevuto come "false"
			    // quindi in questo caso
			    // va ritrasformato in null (java!)
			    if (value != null && value.equals("false")) {
				tiVerificaAgenzia = null;
			    } else {
				tiVerificaAgenzia = value;
			    }
			}
		    } // Gestisce un multi-part MIME encoded file.
		    else {
			if (chunk == 0) {
			    /*
			     * Prima di iniziare determina il nome del FileOS e lo mette in sessione
			     * di Upload. Ottiene dalla sessione l'ID dello strumento urbanistico
			     */
			    SismaForm form = (SismaForm) req.getSession()
				    .getAttribute("###_FORM_CONTAINER");
			    BigDecimal idSisma = form.getDatiGeneraliOutput().getId_sisma_out()
				    .parse();

			    // MEV 34843
			    BackendStorage backendVersamento = salvataggioBackendHelper
				    .getBackendForSisma();
			    ObjectStorageBackend config = salvataggioBackendHelper
				    .getObjectStorageConfigurationForSisma(
					    backendVersamento.getBackendName());

			    SismaDto sisma = sismaEjb.getSismaById(idSisma);

			    String nmFileOs = versamentoOggettoEjb.computeOsFileKey(
				    sisma.getIdVers(), nomeFile,
				    VersamentoOggettoEjb.OS_KEY_POSTFIX.SISMA.name());
			    s3UploadSessionSisma = new S3UploadSessionSisma(
				    salvataggioBackendHelper, idSisma, nmFileOs, config);
			    if (s3UploadSessionSisma.existsOnOS()) {
				responseString = RESP_ERROR_FILE_ALREADY_EXISTS;
				log.info("Il file {} già esiste sull'object storage!", nmFileOs);
				inErrore = true;
			    } else {
				req.getSession().setAttribute(PREFISSO_SESSIONE + idSessione,
					s3UploadSessionSisma);
			    }
			} else {
			    s3UploadSessionSisma = (S3UploadSessionSisma) req.getSession()
				    .getAttribute(PREFISSO_SESSIONE + idSessione);
			}
			/* Fa l'upload del chunk, se è l'ultimo registra il documento anche su DB */
			if (!inErrore && s3UploadSessionSisma.uploadChunk(input, chunk, chunks)) {
			    DocSismaDto dto = new DocSismaDto();
			    dto.setIdSisma(s3UploadSessionSisma.getIdSisma());
			    dto.setNmFileOrig(nomeFile);
			    dto.setNmFileOs(s3UploadSessionSisma.getKeyName());
			    dto.setNmTipoDocumento(nmTipoDocumento);
			    dto.setDimensione(dimensione);
			    dto.setTiVerificaAgenzia(tiVerificaAgenzia);
			    if (tiVerificaAgenzia != null && tiVerificaAgenzia.equals("0")) {
				dto.setFlDocRicaricato(true);
			    }
			    sismaEjb.salvaTipoDocumento(dto);
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
	    log.debug("nome: {}", nomeFile);
	    log.debug("nmTipoDocumento: {},", nmTipoDocumento);
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
