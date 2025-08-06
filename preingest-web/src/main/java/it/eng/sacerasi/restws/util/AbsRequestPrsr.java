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

/*
 * To change this license header, choose License Headers in Project Properties. To change this
 * template file, choose Tools | Templates and open the template in the editor.
 */
package it.eng.sacerasi.restws.util;

import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author fioravanti_f
 */
public abstract class AbsRequestPrsr {

    private static final Logger log = LoggerFactory.getLogger(AbsRequestPrsr.class);

    /**
     * lettura dell'indirizzo IP del chiamante. Si presuppone che il load balancer o il reverse
     * proxy impostino la variabile X-FORWARDED-FOR tra gli header HTTP della request. Questo è
     * l'unico sistema per recepire l'IP nel caso in cui l'application server non sia esposto
     * direttamente. NOTA: è ovvio che l'application server è esposto direttamente solo sui PC di
     * sviluppo.
     *
     * @param request richiesta HTTP di tipo {@link HttpServletRequest}
     *
     * @return restituisce l'IP del client letto estratto dalla request
     */
    public String leggiIpVersante(HttpServletRequest request) {
	String ipVers = request.getHeader("RERFwFor");
	// cerco l'header custom della RER
	if (ipVers == null || ipVers.isEmpty()) {
	    ipVers = request.getHeader("X-FORWARDED-FOR");
	    // se non c'e`, uso l'header standard
	}
	if (ipVers == null || ipVers.isEmpty()) {
	    ipVers = request.getRemoteAddr();
	    // se non c'e` perche' la macchina e' esposta direttamente,
	    // leggo l'IP fisico del chiamante
	}
	log.info("Request, indirizzo di provenienza - IP:  " + ipVers);
	return ipVers;
    }

}
