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

package it.eng.sacerasi.web.util;

import it.eng.sacerasi.web.helper.ConfigurationHelper;
import it.eng.spagoLite.actions.application.ApplicationBaseProperties;
import it.eng.spagoLite.actions.application.IApplicationBasePropertiesSevice;
import javax.ejb.EJB;

/**
 *
 * @author Iacolucci_M
 *
 *         Implementazione che fornisce al framework SpagoLite i dati essenziali dell'applicazione per poter utilizzare
 *         l'Help on line da IAM
 */
public class ApplicationBasePropertiesSeviceImpl implements IApplicationBasePropertiesSevice {

    @EJB(mappedName = "java:app/SacerAsync-ejb/ConfigurationHelper")
    private ConfigurationHelper configHelper;

    @Override
    public ApplicationBaseProperties getApplicationBaseProperties() {

        String nmApplic = configHelper.getParamApplicApplicationName(); // NM_APPLIC
        String user = configHelper.getValoreParamApplicByApplic(it.eng.sacerasi.common.Constants.USERID_RECUP_INFO); // USERID_RECUP_INFO
        String password = configHelper.getValoreParamApplicByApplic(it.eng.sacerasi.common.Constants.PSW_RECUP_INFO); // PSW_RECUP_INFO
        String url = configHelper.getValoreParamApplicByApplic(it.eng.sacerasi.common.Constants.URL_RECUP_HELP); // URL_RECUP_HELP

        ApplicationBaseProperties prop = new ApplicationBaseProperties(nmApplic, user, password, url);

        return prop;
    }

}
