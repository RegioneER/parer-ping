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

package it.eng.sacerasi.web.action;

import java.io.IOException;
import it.eng.integriam.client.ws.IAMSoapClients;
import it.eng.integriam.client.ws.renews.News;
import it.eng.integriam.client.ws.renews.RestituzioneNewsApplicazione;
import it.eng.integriam.client.ws.renews.RestituzioneNewsApplicazioneRisposta;
import it.eng.sacerasi.common.Constants;
import it.eng.sacerasi.slite.gen.Application;
import it.eng.sacerasi.slite.gen.action.HomeAbstractAction;
import it.eng.sacerasi.web.helper.ConfigurationHelper;
import it.eng.spagoCore.error.EMFError;
import it.eng.spagoLite.form.fields.Field;
import it.eng.spagoLite.form.fields.Fields;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.ejb.EJB;
import org.apache.commons.codec.binary.Base64;

import it.eng.spagoLite.security.auth.PwdUtil;
import it.eng.util.EncryptionUtil;
import java.util.Calendar;
import java.util.Date;

public class HomeAction extends HomeAbstractAction {

    @EJB(mappedName = "java:app/SacerAsync-ejb/ConfigurationHelper")
    private ConfigurationHelper configHelper;

    private static Logger logger = LoggerFactory.getLogger(HomeAction.class.getName());

    @Override
    public void initOnClick() throws EMFError {
        //
    }

    public void process() throws EMFError {
        try {
            findNews();
            if (getUser().getScadenzaPwd() != null) {
                Date now = new Date();
                Calendar cal = Calendar.getInstance();
                cal.setTime(getUser().getScadenzaPwd());
                int numGiorni = Integer
                        .parseInt(configHelper.getValoreParamApplicByApplic(Constants.NUM_GIORNI_ESPONI_SCAD_PSW));
                cal.add(Calendar.DATE, -numGiorni);

                if (cal.getTime().before(now) && getUser().getScadenzaPwd().after(now)) {
                    long from = now.getTime();
                    long to = getUser().getScadenzaPwd().getTime();
                    long millisecondiFraDueDate = to - from;
                    // 1 giorno medio = 1000*60*60*24 ms = 86400000 ms
                    double diffGiorni = Math.round(millisecondiFraDueDate / 86400000.0);
                    getMessageBox().addError("Attenzione: la password scadr\u00E0 tra " + (int) diffGiorni
                            + " giorni. Si prega di modificarla al pi\u00F9 presto");
                }

            }
        } catch (Exception e) {
            logger.error("Errore nel recupero delle news", e);
        }
        forwardToPublisher(getDefaultPublsherName());
    }

    @Override
    public String getControllerName() {
        return Application.Actions.HOME;
    }

    @Override
    protected String getDefaultPublsherName() {
        return Application.Publisher.HOME;
    }

    @Override
    public void loadDettaglio() throws EMFError {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void undoDettaglio() throws EMFError {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void saveDettaglio() throws EMFError {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void dettaglioOnClick() throws EMFError {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void elencoOnClick() throws EMFError {
        goBack();
    }

    @Override
    public void insertDettaglio() throws EMFError {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void update(Fields<Field> fields) throws EMFError {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void delete(Fields<Field> fields) throws EMFError {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void reloadAfterGoBack(String publisherName) {
        //
    }

    private void findNews() {
        ArrayList<Map<String, Object>> list = new ArrayList<>();
        RestituzioneNewsApplicazione client = IAMSoapClients.restituzioneNewsApplicazioneClient(
                configHelper.getValoreParamApplicByApplic(Constants.USERID_RECUP_INFO),
                configHelper.getValoreParamApplicByApplic(Constants.PSW_RECUP_INFO),
                configHelper.getValoreParamApplicByApplic(Constants.URL_RECUP_NEWS));
        // LS: 28/01/2016 sostituita la costante
        // RestituzioneNewsApplicazioneRisposta resp =
        // client.restituzioneNewsApplicazione(Constants.SACERPING);
        RestituzioneNewsApplicazioneRisposta resp = client
                .restituzioneNewsApplicazione(configHelper.getParamApplicApplicationName());

        String newline = System.getProperty("line.separator");
        if (resp.getListaNews() != null) {
            for (News row : resp.getListaNews().getNews()) {
                Map<String, Object> news = new HashMap<>();
                String line = "";
                if (row.getDlTesto() != null) {
                    line = row.getDlTesto().replaceAll(newline, "<br />");
                }
                SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
                String dateFormatted = fmt.format(row.getDtIniPubblic().toGregorianCalendar().getTime());
                news.put("dsOggetto", "<font size=\"1\">" + dateFormatted + "</font></br><b><font size=\"2\"> "
                        + row.getDsOggetto() + "</font></b>");
                news.put("dlTesto", line);
                news.put("dtIniPubblic", row.getDtIniPubblic());

                list.add(news);
            }
        }
        getRequest().setAttribute("news", list);

    }

    public void changePwd() throws EMFError, IOException {
        this.freeze();
        StringBuilder sb = new StringBuilder();
        sb.append(getRequest().getScheme());
        sb.append("://");
        sb.append(getRequest().getServerName());
        sb.append(":");
        sb.append(getRequest().getServerPort());
        sb.append(getRequest().getContextPath());
        String retURL = sb.toString();
        String salt = Base64.encodeBase64URLSafeString(PwdUtil.generateSalt());
        // Pagina verso cui Iam deve tornare una volta fatta l'associazione con utente PARER.
        String hmac = EncryptionUtil.getHMAC(retURL + ":" + salt);
        this.getResponse().sendRedirect(configHelper.getValoreParamApplicByApplic(Constants.URL_MODIFICA_PASSWORD)
                + "?r=" + retURL + "&h=" + hmac + "&s=" + salt);

    }

    @Override
    public void mostraInformativa() throws EMFError {
        forwardToPublisher("/login/informativa");
    }

}
