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
import java.io.OutputStream;
import java.math.BigDecimal;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.parer.objectstorage.dto.BackendStorage;
import it.eng.parer.objectstorage.dto.ObjectStorageBackend;
import it.eng.parer.objectstorage.exceptions.BackendException;
import it.eng.parer.objectstorage.exceptions.ObjectStorageException;
import it.eng.parer.objectstorage.helper.BackendHelper;
import it.eng.sacerasi.entity.PigStrumUrbDocumenti;
import it.eng.sacerasi.entity.PigStrumUrbDocumentiStorage;
import it.eng.sacerasi.slite.gen.form.StrumentiUrbanisticiForm;
import it.eng.sacerasi.strumentiUrbanistici.ejb.StrumentiUrbanisticiHelper;
import it.eng.spagoCore.error.EMFError;

/**
 * @author MIacolucci
 */
@WebServlet("/StrumentiUrbanisticiDownloadServlet")
public class DownloadStrumentiUrbanisticiServlet extends HttpServlet {

    private static final long serialVersionUID = -2790402629889569112L;

    @EJB(mappedName = "java:app/SacerAsync-ejb/BackendHelper")
    private BackendHelper backendHelper;
    @EJB
    private StrumentiUrbanisticiHelper strumentiUrbanisticiHelper;

    private static final Logger log = LoggerFactory
            .getLogger(DownloadStrumentiUrbanisticiServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String chiave = request.getParameter("chiave");
        String fileType = "application/zip";
        response.setContentType(fileType);
        response.setHeader("Content-disposition", "attachment; filename=" + chiave);
        StrumentiUrbanisticiForm form = (StrumentiUrbanisticiForm) request.getSession()
                .getAttribute("###_FORM_CONTAINER");
        BigDecimal idStrumento = null;
        try {
            idStrumento = form.getDatiGeneraliOutput().getId_strumenti_urbanistici_out().parse();
        } catch (EMFError ex) {
        }

        try {

            PigStrumUrbDocumenti pigStrumUrbDocumenti = strumentiUrbanisticiHelper
                    .getPigStrumUrbDocumentiByName(chiave, idStrumento);
            if (pigStrumUrbDocumenti != null) {
                // MEV 34843
                PigStrumUrbDocumentiStorage pigStrumUrbDocumentiStorage = pigStrumUrbDocumenti
                        .getPigStrumUrbDocumentiStorage();
                BackendStorage backend = backendHelper
                        .getBackend(pigStrumUrbDocumentiStorage.getIdDecBackend());
                ObjectStorageBackend config = backendHelper
                        .getObjectStorageConfigurationForStrumentiUrbanistici(
                                backend.getBackendName(),
                                pigStrumUrbDocumentiStorage.getNmBucket());

                // This should send the file to browser
                OutputStream out = response.getOutputStream();
                backendHelper.getS3Object(config, pigStrumUrbDocumentiStorage.getCdKeyFile(), out);
                out.flush();
            }
        } catch (BackendException | ObjectStorageException | IOException e) {
            log.error(
                    "DownloadStrumentiUrbanisticiServlet: ERRORE durante la comunicazione con il sistema Object Storage.",
                    e);
        }
    }

}
