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

package it.eng.sacerasi.ws.replicaUtente.ejb;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.lang3.exception.ExceptionUtils;

import it.eng.paginator.util.HibernateUtils;
import it.eng.sacerasi.exception.ParerErrorSeverity;
import it.eng.sacerasi.exception.ParerInternalError;
import it.eng.sacerasi.messages.MessaggiWSBundle;
import it.eng.sacerasi.ws.dto.IRispostaWS;
import it.eng.sacerasi.ws.replicaUtente.dto.CancellaUtenteExt;
import it.eng.sacerasi.ws.replicaUtente.dto.RispostaWSCancellaUtente;

/**
 *
 * @author Gilioli_P
 */
@Stateless(mappedName = "CancellaUtenteEjb")
@LocalBean
public class CancellaUtenteEjb {

    @PersistenceContext(unitName = "SacerAsiJPA")
    private EntityManager entityManager;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void deleteIamUser(CancellaUtenteExt cuExt, RispostaWSCancellaUtente rispostaWs)
	    throws ParerInternalError {
	try {
	    Query q = entityManager
		    .createQuery("DELETE FROM IamUser u WHERE u.idUserIam = :idUserIam ");
	    q.setParameter("idUserIam", HibernateUtils.longFrom(cuExt.getIdUserIam()));
	    q.executeUpdate();
	} catch (Exception ex) {
	    rispostaWs.setSeverity(IRispostaWS.SeverityEnum.ERROR);
	    rispostaWs.setErrorCode(MessaggiWSBundle.SERVIZI_USR_001);
	    rispostaWs.setErrorMessage("Errore nella cancellazione dell'utente "
		    + ExceptionUtils.getRootCauseMessage(ex));
	    throw new ParerInternalError(ParerErrorSeverity.ERROR,
		    "Errore nella cancellazione dell'utente "
			    + ExceptionUtils.getRootCauseMessage(ex),
		    ex);
	}
    }
}
