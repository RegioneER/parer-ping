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

package it.eng.sacerasi.ws.notificaInAttesaPrelievo.ejb;

import it.eng.sacerasi.common.Constants;
import it.eng.sacerasi.entity.PigSessioneRecup;
import it.eng.sacerasi.ws.dto.RispostaControlli;
import it.eng.sacerasi.messages.MessaggiWSBundle;
import java.util.List;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Gilioli_P
 */
@Stateless(mappedName = "ControlliNotificaInAttesaPrelievo")
@LocalBean
public class ControlliNotificaInAttesaPrelievo {

    private static final Logger log = LoggerFactory
	    .getLogger(ControlliNotificaInAttesaPrelievo.class);
    @PersistenceContext(unitName = "SacerAsiJPA")
    private EntityManager entityManager;

    @SuppressWarnings("unchecked")
    public RispostaControlli verificaOggetto(Long idObject) {
	RispostaControlli rispostaControlli = new RispostaControlli();
	rispostaControlli.setrBoolean(true);

	// Per il controllo bisogna ottenere la sessione più recente relativa all'oggetto
	PigSessioneRecup sessione = null;
	// Eseguo una select prendendo, delle sessioni di quell'oggetto, quella che ha l'id maggiore
	// tra tutte
	// Sicuramente questa è la più recente sessione di recupero.
	try {
	    String queryStr = "SELECT ses FROM PigSessioneRecup ses "
		    + "WHERE ses.idSessioneRecup = ("
		    + "SELECT MAX(sessioni.idSessioneRecup) FROM PigSessioneRecup sessioni WHERE sessioni.pigObject.idObject = :idObj"
		    + ")";

	    javax.persistence.Query query = entityManager.createQuery(queryStr);
	    query.setParameter("idObj", idObject);
	    List<PigSessioneRecup> sessioni = query.getResultList();
	    if (!sessioni.isEmpty()) {
		sessione = sessioni.get(0);
	    } else {
		rispostaControlli.setrBoolean(false);
		rispostaControlli.setCodErr(MessaggiWSBundle.PING_NOTIFATTESAPREL_006);
		rispostaControlli.setDsErr(
			MessaggiWSBundle.getString(MessaggiWSBundle.PING_NOTIFATTESAPREL_006));
	    }
	} catch (Exception e) {
	    rispostaControlli.setrBoolean(false);
	    rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
	    rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
		    String.join("\n", ExceptionUtils.getRootCauseStackTrace(e))));
	    log.error("Eccezione nella lettura  della tabella delle sessioni di recupero ", e);
	}

	if (rispostaControlli.isrBoolean()) {
	    rispostaControlli.setrLong(sessione.getIdSessioneRecup());
	    if (!sessione.getTiStato().equals(Constants.StatoSessioneRecup.RECUPERATO.name())) {
		rispostaControlli.setrBoolean(false);
		rispostaControlli.setCodErr(MessaggiWSBundle.PING_NOTIFATTESAPREL_007);
		rispostaControlli.setDsErr(
			MessaggiWSBundle.getString(MessaggiWSBundle.PING_NOTIFATTESAPREL_007));
	    }
	}

	return rispostaControlli;
    }
}
