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

package it.eng.sacerasi.ws.recuperoStatoOggetto.helper;

import it.eng.sacerasi.exception.ParerErrorSeverity;
import it.eng.sacerasi.exception.ParerInternalError;
import it.eng.sacerasi.messages.MessaggiWSBundle;
import it.eng.sacerasi.ws.dto.IRispostaWS.SeverityEnum;
import it.eng.sacerasi.ws.recuperoStatoOggetto.dto.RecuperoStatoOggettoExt;
import it.eng.sacerasi.ws.recuperoStatoOggetto.dto.RispostaWSRecuperoStatoOggetto;
import java.util.List;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Gilioli_P
 */
@Stateless(mappedName = "RecuperoStatoOggettoHelper")
@LocalBean
public class RecuperoStatoOggettoHelper {

    private static final Logger log = LoggerFactory.getLogger(RecuperoStatoOggettoHelper.class);
    @PersistenceContext(unitName = "SacerAsiJPA")
    private EntityManager entityManager;

    @SuppressWarnings("unchecked")
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Object[] getStatoOggetto(RecuperoStatoOggettoExt rsoExt,
	    RispostaWSRecuperoStatoOggetto rispostaWs) throws ParerInternalError {
	Object[] result = null;
	try {
	    Query q = entityManager
		    .createQuery("SELECT obj.tiStatoObject, statoObj.dsTiStatoObject "
			    + "FROM PigObject obj, PigStatoObject statoObj "
			    + "WHERE obj.tiStatoObject = statoObj.tiStatoObject "
			    + "AND obj.idObject = :idObject ");
	    q.setParameter("idObject", rsoExt.getIdObject());
	    List<Object[]> resultList = q.getResultList();
	    if (!resultList.isEmpty()) {
		result = resultList.get(0);
	    } else {
		throw new Exception(
			"Impossibile trovare PigObject con idObject " + rsoExt.getIdObject());
	    }
	} catch (Exception e) {
	    log.error("Eccezione nel recupero dell'oggetto :", e);
	    rispostaWs.setSeverity(SeverityEnum.ERROR);
	    rispostaWs.setErrorCode(MessaggiWSBundle.ERR_666);
	    rispostaWs.setErrorMessage("Eccezione nel recupero dell'oggetto "
		    + String.join("\n", ExceptionUtils.getRootCauseStackTrace(e)));
	    throw new ParerInternalError(ParerErrorSeverity.ERROR,
		    "Eccezione nel recupero dell'oggetto "
			    + String.join("\n", ExceptionUtils.getRootCauseStackTrace(e)),
		    e);
	}
	return result;
    }

}
