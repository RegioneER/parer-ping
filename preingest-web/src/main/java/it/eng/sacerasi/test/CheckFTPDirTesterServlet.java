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
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.eng.sacerasi.test;

import java.io.IOException;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.sacerasi.common.ejb.CommonDb;
import it.eng.sacerasi.slite.gen.tablebean.PigVersRowBean;
import it.eng.sacerasi.slite.gen.tablebean.PigVersTableBean;
import it.eng.sacerasi.web.ejb.CheckFTPDirTesterEjb;
import it.eng.sacerasi.web.helper.ConfigurationHelper;
import it.eng.sacerasi.ws.dto.RispostaControlli;

/**
 *
 * @author Bonora_L
 */
@WebServlet(urlPatterns = { "/CheckFTPDirServlet" }, asyncSupported = true)
public class CheckFTPDirTesterServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static final Logger log = LoggerFactory.getLogger(CheckFTPDirTesterServlet.class);
    @EJB
    CheckFTPDirTesterEjb checkEjb;
    @EJB
    CommonDb commonDb;
    @EJB
    ConfigurationHelper configurationHelper;

    public CheckFTPDirTesterServlet() {
        super();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        this.doPost(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        StringBuilder tableBuilder = new StringBuilder();
        buildResponse(tableBuilder, checkEjb.getPigVersList());

        request.setAttribute("tablerows", tableBuilder.toString());
        request.getRequestDispatcher("checkFTPDir.jsp").forward(request, response);
    }

    private void buildResponse(StringBuilder builder, PigVersTableBean versatori) {
        String rootFtp = "";
        try {
            rootFtp = commonDb.getRootFtpParam();
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
        for (PigVersRowBean row : versatori) {
            String error = null;
            builder.append("<tr>");
            // VERSATORE
            builder.append("<td>");
            builder.append(row.getNmVers());
            builder.append("</td>");
            // Directory di Input
            builder.append("<td>");
            // BigDecimal idAmbienteVers = row.getIdAmbienteVers();
            String dsPathInputFtp = row.getDsPathInputFtp();
            // String dsPathInputFtp = configurationHelper.getValoreParamApplic("DS_PATH_INPUT_FTP", idAmbienteVers,
            // row.getIdVers(), null, Constants.TipoPigVGetValAppart.VERS);
            builder.append(dsPathInputFtp);
            builder.append("</td>");
            // Check
            builder.append("<td>");
            // RispostaControlli risp = checkEjb.doCheck(rootFtp, row.getDsPathInputFtp());
            RispostaControlli risp = checkEjb.doCheck(rootFtp, dsPathInputFtp);
            builder.append(risp.isrBoolean());
            if (!risp.isrBoolean()) {
                error = risp.getDsErr() + "<br/>";
            }

            builder.append("</td>");
            // Directory di output
            builder.append("<td>");
            // builder.append(row.getDsPathOutputFtp());
            builder.append(dsPathInputFtp);
            builder.append("</td>");
            // Check
            risp.reset();
            builder.append("<td>");
            // risp = checkEjb.doCheck(rootFtp, row.getDsPathOutputFtp());
            risp = checkEjb.doCheck(rootFtp, dsPathInputFtp);
            builder.append(risp.isrBoolean());
            if (!risp.isrBoolean()) {
                error = (error != null ? error.concat(risp.getDsErr()) : risp.getDsErr());
            }
            builder.append("</td>");
            // Error
            builder.append("<td>");
            builder.append(error != null ? error : "N/A");
            builder.append("</td>");

            builder.append("</tr>");
        }
    }
}
