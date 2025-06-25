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
import it.eng.parer.objectstorage.helper.SalvataggioBackendHelper;

import it.eng.sacerasi.slite.gen.form.StrumentiUrbanisticiForm;
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
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import it.eng.parer.objectstorage.dto.ObjectStorageBackend;
import it.eng.parer.objectstorage.exceptions.ObjectStorageException;
import it.eng.sacerasi.entity.PigStrumUrbDocumenti;
import it.eng.sacerasi.entity.PigStrumUrbDocumentiStorage;
import it.eng.sacerasi.strumentiUrbanistici.ejb.StrumentiUrbanisticiHelper;

/**
 *
 * @author MIacolucci
 */
@WebServlet("/StrumentiUrbanisticiDownloadServlet")
public class DownloadStrumentiUrbanisticiServlet extends HttpServlet {

    private static final long serialVersionUID = -2790402629889569112L;

    @EJB(mappedName = "java:app/SacerAsync-ejb/SalvataggioBackendHelper")
    private SalvataggioBackendHelper salvataggioBackendHelper;
    @EJB
    private StrumentiUrbanisticiHelper strumentiUrbanisticiHelper;

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

            PigStrumUrbDocumenti pigStrumUrbDocumenti = strumentiUrbanisticiHelper.getPigStrumUrbDocumentiByName(chiave,
                    idStrumento);
            if (pigStrumUrbDocumenti != null) {
                // MEV 34843
                PigStrumUrbDocumentiStorage pigStrumUrbDocumentiStorage = pigStrumUrbDocumenti
                        .getPigStrumUrbDocumentiStorage();
                BackendStorage backend = salvataggioBackendHelper
                        .getBackend(pigStrumUrbDocumentiStorage.getIdDecBackend());
                ObjectStorageBackend config = salvataggioBackendHelper
                        .getObjectStorageConfigurationForStrumentiUrbanistici(backend.getBackendName(),
                                pigStrumUrbDocumentiStorage.getNmBucket());

                ResponseInputStream<GetObjectResponse> ogg = salvataggioBackendHelper.getObject(config,
                        pigStrumUrbDocumentiStorage.getCdKeyFile());

                byte[] buf = new byte[1024];
                int count = 0;
                // This should send the file to browser
                OutputStream out = response.getOutputStream();
                while ((count = ogg.read(buf)) != -1) {
                    out.write(buf, 0, count);
                }
                out.flush();
                ogg.close();
            }
        } catch (ObjectStorageException e) {

        }
    }

}
