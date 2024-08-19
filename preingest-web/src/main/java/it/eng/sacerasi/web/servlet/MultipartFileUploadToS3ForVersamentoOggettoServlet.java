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

package it.eng.sacerasi.web.servlet;

import it.eng.parer.objectstorage.dto.ObjectStorageBackend;
import it.eng.parer.objectstorage.exceptions.ObjectStorageException;
import it.eng.parer.objectstorage.helper.SalvataggioBackendHelper;
import it.eng.sacerasi.common.Constants;
import it.eng.sacerasi.exception.ParerUserError;
import it.eng.sacerasi.messages.MessaggiWSBundle;
import it.eng.sacerasi.slite.gen.form.VersamentoOggettoForm;
import it.eng.sacerasi.slite.gen.tablebean.PigTipoFileObjectTableBean;
import it.eng.sacerasi.slite.gen.tablebean.PigTipoObjectRowBean;
import it.eng.sacerasi.slite.gen.tablebean.PigTipoObjectTableBean;
import it.eng.sacerasi.slite.gen.tablebean.PigVersTipoObjectDaTrasfRowBean;
import it.eng.sacerasi.slite.gen.tablebean.PigVersTipoObjectDaTrasfTableBean;
import it.eng.sacerasi.slite.gen.tablebean.PigXsdDatiSpecTableBean;
import it.eng.sacerasi.util.BinEncUtility;
import it.eng.sacerasi.versamento.ejb.VersamentoOggettoEjb;
import it.eng.sacerasi.web.ejb.AmministrazioneEjb;
import it.eng.sacerasi.web.helper.ComboHelper;
import it.eng.sacerasi.web.helper.ConfigurationHelper;
import it.eng.sacerasi.web.util.Utils;
import it.eng.sacerasi.web.util.WebConstants;
import it.eng.sacerasi.ws.InvioOggettoAsincrono;
import it.eng.sacerasi.ws.NotificaTrasferimento;
import it.eng.sacerasi.ws.invioOggettoAsincrono.ejb.InvioOggettoAsincronoEjb;
import it.eng.sacerasi.ws.notificaTrasferimento.dto.FileDepositatoType;
import it.eng.sacerasi.ws.notificaTrasferimento.dto.ListaFileDepositatoType;
import it.eng.sacerasi.ws.notificaTrasferimento.ejb.NotificaTrasferimentoEjb;
import it.eng.sacerasi.ws.response.InvioOggettoAsincronoRisposta;
import it.eng.sacerasi.ws.response.NotificaTrasferimentoRisposta;
import it.eng.sacerasi.ws.util.WsHelper;
import it.eng.spagoLite.SessionManager;
import it.eng.spagoLite.db.oracle.decode.DecodeMap;
import it.eng.spagoLite.security.User;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import javax.ejb.EJB;
import javax.jws.WebService;

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
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet("/MultipartFileUploadToS3ForVersamentoOggettoServlet")
public class MultipartFileUploadToS3ForVersamentoOggettoServlet extends HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(MultipartFileUploadToS3ForVersamentoOggettoServlet.class);
    private static final long serialVersionUID = 3447685998419256747L;
    private static final String RESP_SUCCESS = "{\"jsonrpc\" : \"2.0\", \"result\" : {\"code\": 200, \"message\": \"Errore nell'apertura dello stream.\"}, \"id\" : \"id\"}";
    private static final String RESP_ERROR_MULTIPART = "{\"jsonrpc\" : \"2.0\", \"result\" : {\"code\": 501, \"message\": \"Errore nell'apertura dello stream.\"}, \"id\" : \"id\"}";
    // private static final String RESP_ERROR_FILE_ALREADY_EXISTS = "{\"jsonrpc\" : \"2.0\", \"result\" : {\"code\":
    // 502, \"message\": \"Il file esiste già sull'object storage.\"}, \"id\" : \"id\"}";
    private static final String RESP_ERROR_GENERIC = "{\"jsonrpc\" : \"2.0\", \"result\" : {\"code\": 503, \"message\": \"Errore inatteso.\"}, \"id\" : \"id\"}";
    private static final String RESP_ERROR_OS_DISABLED = "{\"jsonrpc\" : \"2.0\", \"result\" : {\"code\": 504, \"message\": \"Server Object Storage non presente.\"}, \"id\" : \"id\"}";

    public static final String JSON = "application/json";
    public static final int BUF_SIZE = 2 * 1024;

    @EJB(mappedName = "java:app/SacerAsync-ejb/ConfigurationHelper")
    private ConfigurationHelper configurationHelper;
    @EJB(mappedName = "java:app/SacerAsync-ejb/VersamentoOggettoEjb")
    private VersamentoOggettoEjb versamentoOggettoEjb;
    @EJB(mappedName = "java:app/SacerAsync-ejb/WsHelper")
    private WsHelper wsHelper;
    @EJB(mappedName = "java:app/SacerAsync-ejb/AmministrazioneEjb")
    private AmministrazioneEjb amministrazioneEjb;
    @EJB(mappedName = "java:app/SacerAsync-ejb/InvioOggettoAsincronoEjb")
    private InvioOggettoAsincronoEjb invioOggettoAsincronoEjb;
    @EJB(mappedName = "java:app/SacerAsync-ejb/ComboHelper")
    private ComboHelper comboHelper;
    @EJB(mappedName = "java:app/SacerAsync-ejb/NotificaTrasferimentoEjb")
    private NotificaTrasferimentoEjb notificaTrasferimentoEjb;
    @EJB(mappedName = "java:app/SacerAsync-ejb/SalvataggioBackendHelper")
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
        int chunk = 0;
        int chunks = 0;
        S3UploadSessionVO s3UploadSession;
        String nomeCampo;

        String nomeFile = null;
        String idSessione = null;
        BigDecimal dimensione = null;
        String nm_ambiente_vers = null;
        String nm_vers = null;
        String nm_tipo_object = null;
        String ds_object = null;
        String ds_hash_file_vers = null;
        String cd_vers_gen = null;
        String ti_gest_oggetti_figli = null;
        String cd_versione_xml = null;
        String xml_to_upload_string = null;
        String nm_ambiente_vers_padre = null;
        String nm_vers_padre = null;
        String nm_tipo_object_padre = null;
        String cd_key_object_padre = null;
        String ds_object_padre = null;
        String ni_tot_object_trasf = null;
        String pg_oggetto_trasf = null;
        String ti_priorita_versamento = null;

        User user = (User) SessionManager.getUser(req.getSession());

        // MEV24582
        if (!salvataggioBackendHelper.isActive()) {
            responseString = RESP_ERROR_OS_DISABLED;
            resp.setContentType(JSON);
            byte[] responseBytes = responseString.getBytes();
            resp.setContentLength(responseBytes.length);
            ServletOutputStream output = resp.getOutputStream();
            output.write(responseBytes);
            output.flush();

            return;
        }

        if (isMultipart) {
            ServletFileUpload upload = new ServletFileUpload();

            try {
                ObjectStorageBackend config = salvataggioBackendHelper.getObjectStorageConfiguration("VERS_OGGETTO",
                        configurationHelper.getValoreParamApplicByApplic(Constants.BUCKET_VERSAMENTO_OGGETTO));

                InputStream input = null;

                try {
                    FileItemIterator iter = upload.getItemIterator(req);
                    while (iter.hasNext()) {
                        FileItemStream item = iter.next();
                        input = item.openStream();
                        // Gestisce un form field.
                        if (item.isFormField()) {
                            nomeCampo = item.getFieldName();
                            String value = Streams.asString(input);
                            if (null != nomeCampo) {
                                switch (nomeCampo) {
                                case "name":
                                    nomeFile = value;
                                    break;
                                case "chunks":
                                    chunks = Integer.parseInt(value);
                                    break;
                                case "chunk":
                                    chunk = Integer.parseInt(value);
                                    break;
                                case "nm_ambiente_vers":
                                    nm_ambiente_vers = value;
                                    break;
                                case "nm_vers":
                                    nm_vers = value;
                                    break;
                                case "nm_tipo_object":
                                    nm_tipo_object = value;
                                    break;
                                case "ds_object":
                                    ds_object = value;
                                    break;
                                case "ds_hash_file_vers":
                                    ds_hash_file_vers = value;
                                    break;
                                case "idSessione":
                                    idSessione = value;
                                    break;
                                case "cd_vers_gen":
                                    cd_vers_gen = value;
                                    break;
                                case "ti_gest_oggetti_figli":
                                    ti_gest_oggetti_figli = value;
                                    break;
                                case "dimensione":
                                    dimensione = new BigDecimal(value);
                                    break;
                                case "cd_versione_xml":
                                    cd_versione_xml = value;
                                    break;
                                case "xml_to_upload_string":
                                    xml_to_upload_string = value;
                                    break;

                                // MEV 25602
                                case "nm_ambiente_vers_padre":
                                    nm_ambiente_vers_padre = value;
                                    break;
                                case "nm_vers_padre":
                                    nm_vers_padre = value;
                                    break;
                                case "nm_tipo_object_padre":
                                    nm_tipo_object_padre = value;
                                    break;
                                case "cd_key_object_padre":
                                    cd_key_object_padre = value;
                                    break;
                                case "ds_object_padre":
                                    ds_object_padre = value;
                                    break;
                                case "ni_tot_object_trasf":
                                    ni_tot_object_trasf = value;
                                    break;
                                case "pg_oggetto_trasf":
                                    pg_oggetto_trasf = value;
                                    break;
                                case "ti_priorita_versamento":
                                    ti_priorita_versamento = value;
                                    break;

                                default:
                                    break;
                                }
                            }
                        } // Gestisce un multi-part MIME encoded file.
                        else {
                            if (chunk == 0) {
                                checkFile(nomeFile, dimensione);

                                inviaOggetto(user, nomeFile, nm_ambiente_vers, nm_vers, nm_tipo_object,
                                        ds_hash_file_vers, ds_object, cd_vers_gen, ti_gest_oggetti_figli,
                                        xml_to_upload_string, cd_versione_xml, nm_ambiente_vers_padre, nm_vers_padre,
                                        nm_tipo_object_padre, cd_key_object_padre, ds_object_padre, ni_tot_object_trasf,
                                        pg_oggetto_trasf, ti_priorita_versamento);

                                String nmFileOs = versamentoOggettoEjb
                                        .getFileOsPathByVers(user.getIdOrganizzazioneFoglia())
                                        + Utils.eliminaPunteggiatureSpaziNomeFile(nomeFile);

                                s3UploadSession = new S3UploadSessionVO(salvataggioBackendHelper, config, nmFileOs);
                                req.getSession().setAttribute(idSessione, s3UploadSession);
                            } else {
                                s3UploadSession = (S3UploadSessionVO) req.getSession().getAttribute(idSessione);
                            }

                            /* Fa l'upload del chunk, se è l'ultimo registra il documento anche su DB */
                            // MAC 27228
                            if (s3UploadSession != null && s3UploadSession.uploadChunk(input, chunk, chunks)) {
                                versaOggetto(user, nomeFile, nm_ambiente_vers, nm_vers, nm_tipo_object,
                                        ds_hash_file_vers, ds_object, cd_vers_gen, ti_gest_oggetti_figli,
                                        config.getBucket());
                            }
                        }
                    }
                } catch (ParerUserError e) {
                    responseString = "{\"jsonrpc\" : \"2.0\", \"result\" : {\"code\": 505, \"message\": \""
                            + e.getMessage() + "\"}, \"id\" : \"id\"}";
                    log.error("MultipartFileUploadToS3ForVersamentoOggettoServlet", e);

                    if (input != null) {
                        // se non leggo nulla dallo stream plupload.js pensa che ci sia un errore http,
                        // nel nostro caso l'errore vogliamo gestirlo noi.
                        IOUtils.toByteArray(input);
                    }
                } catch (Exception ex) {
                    responseString = RESP_ERROR_GENERIC;
                    log.error("MultipartFileUploadToS3ForVersamentoOggettoServlet", ex);

                    if (input != null) {
                        // se non leggo nulla dallo stream plupload.js pensa che ci sia un errore http,
                        // nel nostro caso l'errore vogliamo gestirlo noi.
                        IOUtils.toByteArray(input);
                    }
                } finally {
                    if (input != null) {
                        input.close();
                    }
                }
            } catch (ObjectStorageException e) {
                log.error("MultipartFileUploadToS3ForVersamentoOggettoServlet", e);
            }
        } // Non una multi-part MIME request.
        else {
            responseString = RESP_ERROR_MULTIPART;
        }

        if (chunk == chunks - 1) {
            // abbiamo finito!
            req.getSession().removeAttribute(idSessione);
        }

        resp.setContentType(JSON);
        byte[] responseBytes = responseString.getBytes();
        resp.setContentLength(responseBytes.length);
        ServletOutputStream output = resp.getOutputStream();
        output.write(responseBytes);
        output.flush();
    }

    private void inviaOggetto(User user, String nomeFile, String nm_ambiente_vers, String nm_vers,
            String nm_tipo_object, String ds_hash_file_vers, String ds_object, String cd_vers_gen,
            String ti_gest_oggetti_figli, String xml_to_upload_string, String cd_versione_xml,
            String nm_ambiente_vers_padre, String nm_vers_padre, String nm_tipo_object_padre,
            String cd_key_object_padre, String ds_object_padre, String ni_tot_object_trasf, String pg_oggetto_trasf,
            String ti_priorita_versamento) throws ParerUserError {

        VersamentoOggettoForm form = new VersamentoOggettoForm();
        form.getVersamentoOggettoDetail().getNm_ambiente_vers().setValue(nm_ambiente_vers);
        form.getVersamentoOggettoDetail().getNm_vers().setValue(nm_vers);
        form.getVersamentoOggettoDetail().getNm_tipo_object().setValue(nm_tipo_object);
        form.getVersamentoOggettoDetail().getDs_hash_file_vers().setValue(ds_hash_file_vers);
        form.getVersamentoOggettoDetail().getDs_object().setValue(ds_object);
        form.getVersamentoOggettoDetail().getCd_vers_gen().setValue(cd_vers_gen);
        form.getVersamentoOggettoDetail().getTi_gest_oggetti_figli().setValue(ti_gest_oggetti_figli);
        form.getVersamentoOggettoDetail().getCd_versione_xml().setValue(cd_versione_xml);

        form.getVersamentoOggettoDetail().getNm_ambiente_vers_padre().setValue(nm_ambiente_vers_padre);
        form.getVersamentoOggettoDetail().getNm_vers_padre().setValue(nm_vers_padre);
        form.getVersamentoOggettoDetail().getNm_tipo_object_padre().setValue(nm_tipo_object_padre);
        form.getVersamentoOggettoDetail().getCd_key_object_padre().setValue(cd_key_object_padre);
        form.getVersamentoOggettoDetail().getDs_object_padre().setValue(ds_object_padre);
        form.getVersamentoOggettoDetail().getNi_tot_object_trasf().setValue(ni_tot_object_trasf);
        form.getVersamentoOggettoDetail().getPg_oggetto_trasf().setValue(pg_oggetto_trasf);

        try {
            BigDecimal idObjTrasf = null;
            String tiPriorita = null;
            String tiPrioritaVersamento = null;

            // sanitizzazione dell'input
            String ambiente = form.getVersamentoOggettoDetail().getNm_ambiente_vers().parse();
            BigDecimal idVers = user.getIdOrganizzazioneFoglia();
            String versatore = form.getVersamentoOggettoDetail().getNm_vers().parse();
            BigDecimal idTipoObject = form.getVersamentoOggettoDetail().getNm_tipo_object().parse();
            String dsHashFileVers = form.getVersamentoOggettoDetail().getDs_hash_file_vers().parse();
            String dsObject = form.getVersamentoOggettoDetail().getDs_object().parse();
            String cdVersioneXml = form.getVersamentoOggettoDetail().getCd_versione_xml().parse();

            BigDecimal idObjectPadre = form.getVersamentoOggettoDetail().getCd_key_object_padre().parse();
            String cdKeyObjectPadre = null;
            String ambientePadre = StringUtils
                    .trimToNull(form.getVersamentoOggettoDetail().getNm_ambiente_vers_padre().getDecodedValue());
            String versatorePadre = StringUtils
                    .trimToNull(form.getVersamentoOggettoDetail().getNm_vers_padre().getDecodedValue());
            String cdVersGen = form.getVersamentoOggettoDetail().getCd_vers_gen().parse();
            BigDecimal niTotObjectTrasf = form.getVersamentoOggettoDetail().getNi_tot_object_trasf().parse();
            BigDecimal pgOggettoTrasf = form.getVersamentoOggettoDetail().getPg_oggetto_trasf().parse();

            // Verifico che l'utente sia abilitato ai servizi
            WebService annotationInvio = InvioOggettoAsincrono.class.getAnnotation(WebService.class);
            WebService annotationNotif = NotificaTrasferimento.class.getAnnotation(WebService.class);
            wsHelper.checkAuthorizations(ambiente, versatore, user.getUsername(), annotationInvio.serviceName());
            wsHelper.checkAuthorizations(ambiente, versatore, user.getUsername(), annotationNotif.serviceName());

            PigTipoObjectRowBean pigTipoObjectRowBean = amministrazioneEjb.getPigTipoObjectRowBean(idTipoObject);

            // imposto la decode map per recuperare il nome del tipo oggetto (si può recuperare da db anche volendo...)
            PigTipoObjectTableBean tipoObjectTableBean = comboHelper.getTipoObjectFromVersatore(user.getIdUtente(),
                    idVers, Constants.TipoVersamento.DA_TRASFORMARE.name(),
                    Constants.TipoVersamento.ZIP_CON_XML_SACER.name());
            form.getVersamentoOggettoDetail().getNm_tipo_object().setDecodeMap(
                    DecodeMap.Factory.newInstance(tipoObjectTableBean, "id_tipo_object", "nm_tipo_object"));

            if (pigTipoObjectRowBean.getFlContrHash().equals(WebConstants.DB_TRUE)
                    && StringUtils.isBlank(dsHashFileVers)) {
                throw new ParerUserError("Il tipo oggetto prevede che sia controllato l'hash del file");
            }

            String cdKeyObject = nomeFile.substring(0, nomeFile.indexOf('.'));

            boolean oggettoDaTrasformare = pigTipoObjectRowBean.getTiVersFile()
                    .equals(Constants.TipoVersamento.DA_TRASFORMARE.name());

            // MEV25602 - non ci occupiamo PIÙ di versare solo oggetti "DA_TRASFORMARE"
            if (oggettoDaTrasformare) {
                if (pigTipoObjectRowBean.getTiPriorita() != null) {
                    tiPriorita = it.eng.sacerasi.web.util.Constants.ComboFlagPrioTrasfType
                            .getEnumByString(pigTipoObjectRowBean.getTiPriorita());
                } else {
                    throw new ParerUserError("Impossibile versare, priorità non impostata sul Tipo Oggetto.");
                }
            }
            if (ti_priorita_versamento != null) {
                tiPrioritaVersamento = it.eng.sacerasi.web.util.Constants.ComboFlagPrioVersType
                        .getValueByEnumName(ti_priorita_versamento);
            } else if (pigTipoObjectRowBean.getTiPrioritaVersamento() != null) {
                tiPrioritaVersamento = pigTipoObjectRowBean.getTiPrioritaVersamento();
            }

            // if (pigTipoObjectRowBean.getTiPrioritaVersamento() != null) {
            // tiPrioritaVersamento = it.eng.sacerasi.web.util.Constants.ComboFlagPrioVersType
            // .getEnumByString(pigTipoObjectRowBean.getTiPrioritaVersamento());
            // tiPrioritaVersamento = pigTipoObjectRowBean.getTiPrioritaVersamento();
            // }

            /*
             * MEV#13040 - Se non è definita la regular Expression
             */
            if (pigTipoObjectRowBean.getDsRegExpCdVers() == null
                    || pigTipoObjectRowBean.getDsRegExpCdVers().equals("")) {
                PigVersTipoObjectDaTrasfTableBean tipoObjDaTrasfTB = amministrazioneEjb
                        .getPigVersTipoObjectDaTrasfTableBean(pigTipoObjectRowBean.getIdTipoObject());
                if (tipoObjDaTrasfTB != null && tipoObjDaTrasfTB.size() == 1) {
                    cdVersGen = tipoObjDaTrasfTB.getRow(0).getCdVersGen();
                }
            }

            boolean fileUploaded = StringUtils.isNotBlank(xml_to_upload_string);
            PigXsdDatiSpecTableBean pigXsdDatiSpecTableBean = amministrazioneEjb
                    .getPigXsdDatiSpecTableBean(idTipoObject, null);

            // MAC27271
            if (!pigXsdDatiSpecTableBean.isEmpty()) {
                if (!fileUploaded || StringUtils.isBlank(cdVersioneXml)) {
                    throw new ParerUserError(
                            "Per questo tipo oggetto è necessario allegare un indice oggetto e specificare una versione xml.");
                }
            }

            if (fileUploaded && StringUtils.isBlank(cdVersioneXml)) {
                throw new ParerUserError(
                        "Per l'upload del file XML \u00E8 necessario definire anche la versione del xml");
            } else if (StringUtils.isBlank(xml_to_upload_string) && StringUtils.isNotBlank(cdVersioneXml)) {
                throw new ParerUserError("\u00C8 stato definita la versione del xml senza eseguire l'upload del file");
            } else if (fileUploaded && pigXsdDatiSpecTableBean.isEmpty()) {
                throw new ParerUserError(
                        "Per l'upload del file XML \u00E8 necessario definire per il tipo di oggetto lo XSD di validazione");
            }

            // Controlli su un eventuale oggetto di tipo DA_TRASFORMARE padre
            if (idObjectPadre != null) {
                cdKeyObjectPadre = form.getVersamentoOggettoDetail().getCd_key_object_padre().getDecodedValue();
                if (niTotObjectTrasf == null || niTotObjectTrasf.compareTo(BigDecimal.ZERO) <= 0) {
                    throw new ParerUserError(
                            "Per l'oggetto da trasformare deve essere definito il numero di oggetti generati da trasformazione");
                }
                if (pgOggettoTrasf == null || pgOggettoTrasf.compareTo(BigDecimal.ZERO) <= 0) {
                    throw new ParerUserError(
                            "Per l'oggetto da versare deve essere definito il progressivo dell'oggetto generato da trasformazione");
                }
                if (pgOggettoTrasf.compareTo(niTotObjectTrasf) > 0) {
                    throw new ParerUserError(
                            "Per l'oggetto da versare il progressivo dell'oggetto generato da trasformazione deve essere minore o uguale al numero di oggetti generati");
                }
                BigDecimal idTipoObjectPadre = form.getVersamentoOggettoDetail().getNm_tipo_object_padre().parse();
                if (idTipoObjectPadre != null) {
                    PigVersTipoObjectDaTrasfRowBean pigVersTipoObjectDaTrasfRowBean = amministrazioneEjb
                            .getPigVersTipoObjectDaTrasfRowBean(idTipoObjectPadre, null, idVers);
                    if (pigVersTipoObjectDaTrasfRowBean == null
                            || !pigVersTipoObjectDaTrasfRowBean.getIdTipoObjectGen().equals(idTipoObject)) {
                        throw new ParerUserError(
                                "Per il tipo oggetto da trasformare non \u00E8 definito il versatore e/o il tipo oggetto a cui appartiene l'oggetto da versare");
                    }
                }

                // algo hash
                String tiHashFileVers = null;
                if (StringUtils.isNotBlank(dsHashFileVers)) {
                    if (!BinEncUtility.isHexString(dsHashFileVers)) {
                        throw new ParerUserError("Il formato dell'hash non è coerente");
                    } else {
                        // check algo type
                        tiHashFileVers = this.impostaAlgoHash(dsHashFileVers).descrivi();
                        if (tiHashFileVers.equals(Constants.TipiHash.SCONOSCIUTO.descrivi())) {
                            throw new ParerUserError("Il formato dell'hash non è tra quelli supportati ("
                                    + Constants.TipiHash.alldesc() + ")");
                        }
                    }
                } else {
                    tiHashFileVers = Constants.TipiHash.SHA_256.descrivi(); // default
                }

                idObjTrasf = amministrazioneEjb.checkPigObjectTrasf(idObjectPadre, idTipoObject, idVers, cdKeyObject,
                        pgOggettoTrasf);
                if (idObjTrasf == null) {

                    long idObjTrasfLong = amministrazioneEjb.createPigObjectTrasf(idObjectPadre, cdKeyObject, dsObject,
                            idVers, idTipoObject, Constants.DS_PATH_TRASF_NON_DEFINITO, dsHashFileVers, tiHashFileVers,
                            Constants.TipiEncBinari.HEX_BINARY.descrivi(), pgOggettoTrasf, cdVersioneXml,
                            xml_to_upload_string);
                    idObjTrasf = new BigDecimal(idObjTrasfLong);
                } else {
                    amministrazioneEjb.updatePigObjectTrasf(idObjTrasf, dsObject, Constants.DS_PATH_TRASF_NON_DEFINITO,
                            dsHashFileVers, tiHashFileVers, Constants.TipiEncBinari.HEX_BINARY.descrivi(),
                            pgOggettoTrasf, cdVersioneXml, xml_to_upload_string);
                }
            }

            // Eseguo l'invioOggettoAsincrono
            String tipoObject = form.getVersamentoOggettoDetail().getNm_tipo_object().getDecodedValue();

            InvioOggettoAsincronoRisposta invioRisposta = invioOggettoAsincronoEjb.invioOggettoAsincronoEsteso(
                    user.getUsername(), ambiente, versatore, cdKeyObject, dsObject, tipoObject, false, false, false,
                    null, cdVersioneXml, xml_to_upload_string, ambientePadre, versatorePadre, cdKeyObjectPadre,
                    niTotObjectTrasf, pgOggettoTrasf, null, cdVersGen, ti_gest_oggetti_figli, tiPriorita,
                    tiPrioritaVersamento);

            // Controllo della risposta
            if (invioRisposta.getCdEsito() == Constants.EsitoServizio.OK
                    || invioRisposta.getCdErr().equals(MessaggiWSBundle.PING_SENDOBJ_OBJ_010)) {
                log.debug("Fase 1 completata: l'invio oggetto a PreIngest \u00E8 terminato con successo");
            } else {
                if (idObjTrasf != null) {
                    amministrazioneEjb.updatePigObjectTrasf(idObjTrasf, invioRisposta.getCdErr(),
                            invioRisposta.getDsErr());
                }
                if (idObjectPadre != null) {
                    amministrazioneEjb.checkPigObjectFigliAndUpdate(idObjectPadre);
                }
                throw new ParerUserError("Errore " + invioRisposta.getCdErr() + ": " + invioRisposta.getDsErr());
            }
        } catch (ParerUserError ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ParerUserError("Errore generico");
        }
    }

    private void versaOggetto(User user, String nomeFile, String nm_ambiente_vers, String nm_vers,
            String nm_tipo_object, String ds_hash_file_vers, String ds_object, String cd_vers_gen,
            String ti_gest_oggetti_figli, String nm_os_bucket) throws ParerUserError {

        VersamentoOggettoForm form = new VersamentoOggettoForm();
        form.getVersamentoOggettoDetail().getNm_ambiente_vers().setValue(nm_ambiente_vers);
        form.getVersamentoOggettoDetail().getNm_vers().setValue(nm_vers);
        form.getVersamentoOggettoDetail().getNm_tipo_object().setValue(nm_tipo_object);
        form.getVersamentoOggettoDetail().getDs_hash_file_vers().setValue(ds_hash_file_vers);
        form.getVersamentoOggettoDetail().getDs_object().setValue(ds_object);
        form.getVersamentoOggettoDetail().getCd_vers_gen().setValue(cd_vers_gen);
        form.getVersamentoOggettoDetail().getTi_gest_oggetti_figli().setValue(ti_gest_oggetti_figli);

        try {
            String nmTipoFile;

            String ambiente = form.getVersamentoOggettoDetail().getNm_ambiente_vers().parse();
            BigDecimal idVers = user.getIdOrganizzazioneFoglia();
            String versatore = form.getVersamentoOggettoDetail().getNm_vers().parse();
            BigDecimal idTipoObject = form.getVersamentoOggettoDetail().getNm_tipo_object().parse();
            String dsHashFileVers = form.getVersamentoOggettoDetail().getDs_hash_file_vers().parse();

            // Verifico che l'utente sia abilitato ai servizi
            WebService annotationInvio = InvioOggettoAsincrono.class.getAnnotation(WebService.class);
            WebService annotationNotif = NotificaTrasferimento.class.getAnnotation(WebService.class);
            wsHelper.checkAuthorizations(ambiente, versatore, user.getUsername(), annotationInvio.serviceName());
            wsHelper.checkAuthorizations(ambiente, versatore, user.getUsername(), annotationNotif.serviceName());

            PigTipoObjectRowBean pigTipoObjectRowBean = amministrazioneEjb.getPigTipoObjectRowBean(idTipoObject);

            // imposto la decode map per recuperare il nome del tipo oggetto (si può recuperare da db anche volendo...)
            PigTipoObjectTableBean tipoObjectTableBean = comboHelper.getTipoObjectFromVersatore(user.getIdUtente(),
                    idVers, Constants.TipoVersamento.DA_TRASFORMARE.name(),
                    Constants.TipoVersamento.ZIP_CON_XML_SACER.name());
            form.getVersamentoOggettoDetail().getNm_tipo_object().setDecodeMap(
                    DecodeMap.Factory.newInstance(tipoObjectTableBean, "id_tipo_object", "nm_tipo_object"));

            PigTipoFileObjectTableBean pigTipoFileObjectTableBean = amministrazioneEjb
                    .getPigTipoFileObjectTableBean(idTipoObject);
            if (pigTipoFileObjectTableBean.isEmpty()) {
                throw new ParerUserError(
                        "Nella configurazione dell'oggetto manca la definizione del tipo file. Non \u00E8 possibile eseguire il versamento");
            } else if (pigTipoFileObjectTableBean.size() > 1) {
                throw new ParerUserError(
                        "Il tipo di oggetto deve presentare un solo tipo di file per eseguire il versamento");
            } else {
                nmTipoFile = pigTipoFileObjectTableBean.getRow(0).getNmTipoFileObject();
            }

            if (pigTipoObjectRowBean.getFlContrHash().equals(WebConstants.DB_TRUE)
                    && StringUtils.isBlank(dsHashFileVers)) {
                throw new ParerUserError("Il tipo oggetto prevede che sia controllato l'hash del file");
            }

            String cdKeyObject = nomeFile.substring(0, nomeFile.indexOf('.'));
            String estensioneDedotta = nomeFile.substring(nomeFile.indexOf('.'));

            boolean oggettoDaTrasformare = pigTipoObjectRowBean.getTiVersFile()
                    .equals(Constants.TipoVersamento.DA_TRASFORMARE.name());

            // controllo con parametro estensioni ammesse se da trasformare,
            // altrimenti solo .zip
            String estensioni = null;
            String[] estensioniAmmesse = null;
            if (oggettoDaTrasformare) {
                estensioni = configurationHelper.getValoreParamApplicByApplic(Constants.ESTENSIONI_FILE_DA_TRASF);
                estensioniAmmesse = estensioni.split(",");
            } else {
                estensioni = Constants.ZIP_EXTENSION;
                estensioniAmmesse = new String[1];
                estensioniAmmesse[0] = Constants.ZIP_EXTENSION;
            }

            for (String est : estensioniAmmesse) {
                if ((cdKeyObject + est).equals(nomeFile)) {
                    // servirà poi per passarla alla notifica del trasferimento file
                    estensioneDedotta = est;
                    break;
                }
            }

            // algo hash
            String tiHashFileVers = null;
            if (StringUtils.isNotBlank(dsHashFileVers)) {
                if (!BinEncUtility.isHexString(dsHashFileVers)) {
                    throw new ParerUserError("Il formato dell'hash non è coerente");
                } else {
                    // check algo type
                    tiHashFileVers = this.impostaAlgoHash(dsHashFileVers).descrivi();
                    if (tiHashFileVers.equals(Constants.TipiHash.SCONOSCIUTO.descrivi())) {
                        throw new ParerUserError("Il formato dell'hash non è tra quelli supportati ("
                                + Constants.TipiHash.alldesc() + ")");
                    }
                }
            } else {
                tiHashFileVers = Constants.TipiHash.SHA_256.descrivi();// default
            }

            String nmFileOs = versamentoOggettoEjb.getFileOsPathByVers(user.getIdOrganizzazioneFoglia())
                    + Utils.eliminaPunteggiatureSpaziNomeFile(nomeFile);

            // Eseguo la NotificaTrasferimentoFile
            ListaFileDepositatoType listaFile = new ListaFileDepositatoType();
            listaFile.setFileDepositato(new ArrayList<FileDepositatoType>());

            FileDepositatoType file = new FileDepositatoType();
            file.setNmNomeFile(cdKeyObject + estensioneDedotta);
            file.setNmTipoFile(nmTipoFile);
            if (StringUtils.isNotBlank(dsHashFileVers)) {
                file.setCdEncoding(Constants.TipiEncBinari.HEX_BINARY.descrivi());
                file.setTiAlgoritmoHash(tiHashFileVers);
                file.setDsHashFile(dsHashFileVers);
            } else {
                file.setCdEncoding(null);
                file.setTiAlgoritmoHash(null);
                file.setDsHashFile(null);
            }

            file.setNmNomeFileOs(nmFileOs);
            file.setNmOsBucket(nm_os_bucket);

            listaFile.getFileDepositato().add(file);

            NotificaTrasferimentoRisposta notificaRisposta = notificaTrasferimentoEjb
                    .notificaAvvenutoTrasferimentoFile(ambiente, versatore, cdKeyObject, listaFile);

            // Messaggio per l'utente
            if (notificaRisposta.getCdEsito().equals(Constants.EsitoServizio.OK.name())) {
                log.debug("Versamento oggetto completato con successo: " + cdKeyObject);
            } else {
                String errorMessage = "Errore " + notificaRisposta.getCdErr() + ": " + notificaRisposta.getDsErr();
                throw new ParerUserError(errorMessage);
            }
        } catch (ParerUserError ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ParerUserError("Errore generico");
        }

    }

    private Constants.TipiHash impostaAlgoHash(String hash) {
        Constants.TipiHash trovato = null;
        switch (Constants.TipiHash.evaluateByLenght(BinEncUtility.decodeUTF8HexString(hash).length)) {
        case MD5:
            trovato = Constants.TipiHash.MD5;
            break;
        case SHA_1:
            trovato = Constants.TipiHash.SHA_1;
            break;
        case SHA_224:
            trovato = Constants.TipiHash.SHA_224;
            break;
        case SHA_256:
            trovato = Constants.TipiHash.SHA_256;
            break;
        case SHA_384:
            trovato = Constants.TipiHash.SHA_384;
            break;
        case SHA_512:
            trovato = Constants.TipiHash.SHA_512;
            break;
        default:
            trovato = Constants.TipiHash.SCONOSCIUTO;
            break;
        }
        return trovato;
    }

    private void checkFile(String nomeFile, BigDecimal dimensioneFile) throws ParerUserError {

        final int maxLength = 256; // FIMXE

        String dim = "";
        try {
            dim = configurationHelper.getValoreParamApplicByApplic(Constants.DIM_MAX_FILE_DA_VERSARE_OS);
        } catch (Exception ex) {
            throw new ParerUserError(
                    "Errore inatteso di PreIngest: Errata configurazione della dimensione massima del file da versare.");
        }

        BigDecimal maxDim;
        if (!StringUtils.isNumeric(dim)) {
            throw new ParerUserError(
                    "Errore inatteso di PreIngest: Errata configurazione della dimensione massima del file da versare.");
        } else {
            maxDim = new BigDecimal(dim);
        }
        String estensioni = null;
        String[] estensioniAmmesseLowerCase = null;
        // Se versamento da trasformare
        estensioni = configurationHelper.getValoreParamApplicByApplic(Constants.ESTENSIONI_FILE_DA_TRASF).toLowerCase();
        estensioniAmmesseLowerCase = estensioni.split(",");

        if (estensioniAmmesseLowerCase == null) {
            throw new ParerUserError(
                    "Errore inatteso di PreIngest: Errata configurazione delle estensioni ammesse per i file da trasformare");
        }
        if (nomeFile.length() > maxLength) {
            throw new ParerUserError("Il nome del file da versare (compresa estensione) non pu\u00F2 superare i "
                    + maxLength + " caratteri");
            // MEV15373 - Tolto di nuovo il controllo sui più punti nel nome file
        } else if (StringUtils.countMatches(nomeFile, ".") > 1) {
            throw new ParerUserError("Il nome del file pu\u00F2 contenere al massimo un solo carattere '.'");
        } else if (!StringUtils.endsWithAny(nomeFile.toLowerCase(), estensioniAmmesseLowerCase)) {
            throw new ParerUserError("L'estensione del file deve essere una delle seguenti: " + estensioni);
        } else if (StringUtils.isBlank(StringUtils.substringBefore(nomeFile, "."))) {
            throw new ParerUserError("Il nome del file deve contenere almeno un carattere");
        } else if (dimensioneFile.longValue() > maxDim.longValue()) {
            throw new ParerUserError(
                    "La dimensione del file non pu\u00F2 superare la dimensione massima prevista che \u00E8 pari a "
                            + dim + " Byte.");
        }
    }

}
