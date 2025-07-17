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

package it.eng.sacerasi.ws.util;

import it.eng.spagoLite.security.auth.WSLoginHandler;
import it.eng.spagoLite.security.exception.AuthWSException;
import java.util.List;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author Agati_D
 */
@Stateless(mappedName = "JobHelper")
@LocalBean
public class WsHelper {

    @PersistenceContext(unitName = "SacerAsiJPA")
    private EntityManager em;

    public void checkAuthorizations(String nmAmbiente, String nmVersatore, String username,
	    String servizioWeb) throws AuthWSException {
	Integer idOrganiz = retrieveIdOrgFromNmAmbienteAndNmVersatore(nmAmbiente, nmVersatore);
	if (idOrganiz != null) {
	    WSLoginHandler.checkAuthz(username, idOrganiz, servizioWeb, em);
	}
    }

    @SuppressWarnings("unchecked")
    private Integer retrieveIdOrgFromNmAmbienteAndNmVersatore(String nmAmbiente,
	    String nmVersatore) {
	Query q = em.createQuery("SELECT pigVers.idVers "
		+ "FROM PigVers pigVers JOIN pigVers.pigAmbienteVer ambienteVers "
		+ "where pigVers.nmVers = :nmVersatore "
		+ "and ambienteVers.nmAmbienteVers = :nmAmbiente");
	q.setParameter("nmAmbiente", nmAmbiente);
	q.setParameter("nmVersatore", nmVersatore);
	List<Long> queueId = q.getResultList();
	return (queueId.isEmpty()) ? null : queueId.get(0).intValue();
    }

}
