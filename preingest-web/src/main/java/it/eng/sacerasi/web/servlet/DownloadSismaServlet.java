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

import it.eng.parer.objectstorage.dto.BackendStorage;
import it.eng.sacerasi.slite.gen.form.SismaForm;
import it.eng.spagoCore.error.EMFError;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import it.eng.parer.objectstorage.helper.SalvataggioBackendHelper;
import it.eng.parer.objectstorage.dto.ObjectStorageBackend;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import it.eng.parer.objectstorage.exceptions.ObjectStorageException;
import it.eng.sacerasi.entity.PigSismaDocumenti;
import it.eng.sacerasi.entity.PigSismaDocumentiStorage;
import it.eng.sacerasi.sisma.ejb.SismaHelper;

/**
 *
 * @author MIacolucci
 */
@WebServlet("/SismaDownloadServlet")
public class DownloadSismaServlet extends HttpServlet {

    private static final long serialVersionUID = -2851040320489951165L;

    @EJB
    private SalvataggioBackendHelper salvataggioBackendHelper;
    @EJB
    private SismaHelper sismaHelper;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String chiave = request.getParameter("chiave");
        String fileType = "application/zip";
        response.setContentType(fileType);
        response.setHeader("Content-disposition", "attachment; filename=" + chiave);
        SismaForm form = (SismaForm) request.getSession().getAttribute("###_FORM_CONTAINER");
        BigDecimal idSisma = null;
        try {
            idSisma = form.getDatiGeneraliOutput().getId_sisma_out().parse();
        } catch (EMFError ex) {
        }

        try {

            PigSismaDocumenti pigSismaDocumenti = sismaHelper.getPigSismaDocumentiByName(chiave, idSisma);
            if (pigSismaDocumenti != null) {
                // MEV 34843
                PigSismaDocumentiStorage pigSismaDocumentiStorage = pigSismaDocumenti.getPigSismaDocumentiStorage();
                BackendStorage backend = salvataggioBackendHelper
                        .getBackend(pigSismaDocumentiStorage.getIdDecBackend());
                ObjectStorageBackend config = salvataggioBackendHelper
                        .getObjectStorageConfigurationForSisma(backend.getBackendName(),
                                pigSismaDocumentiStorage.getNmBucket());

                ResponseInputStream<GetObjectResponse> inStream = salvataggioBackendHelper.getObject(config,
                        pigSismaDocumentiStorage.getCdKeyFile());

                byte[] buf = new byte[1024];
                int count = 0;
                // This should send the file to browser
                OutputStream out = response.getOutputStream();
                while ((count = inStream.read(buf)) != -1) {
                    out.write(buf, 0, count);
                }
                out.flush();
                inStream.close();
            }
        } catch (ObjectStorageException e) {

        }
    }

}
