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

package it.eng.sacerasi.ws.richiestaRestituzioneOggetto.ejb;

import it.eng.sacerasi.common.Constants;
import it.eng.sacerasi.entity.PigObject;
import it.eng.sacerasi.ws.dto.RispostaControlli;
import it.eng.sacerasi.messages.MessaggiWSBundle;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Bonora_L
 */
@Stateless(mappedName = "ControlliRichiestaRestituzioneOggetto")
@LocalBean
public class ControlliRichiestaRestituzioneOggetto {

    private static final Logger log = LoggerFactory
	    .getLogger(ControlliRichiestaRestituzioneOggetto.class);
    @PersistenceContext(unitName = "SacerAsiJPA")
    private EntityManager entityManager;

    public RispostaControlli verificaOggetto(Long idObject) {
	RispostaControlli rispostaControlli = new RispostaControlli();
	rispostaControlli.setrBoolean(true);

	PigObject obj = entityManager.find(PigObject.class, idObject);
	if (rispostaControlli.isrBoolean()
		&& !obj.getTiStatoObject().equals(Constants.StatoOggetto.CHIUSO_OK.name())) {
	    rispostaControlli.setrBoolean(false);
	    rispostaControlli.setCodErr(MessaggiWSBundle.PING_RICHOBJ_007);
	    rispostaControlli
		    .setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.PING_RICHOBJ_007));
	}
	if (rispostaControlli.isrBoolean() && !obj.getPigTipoObject().getTiVersFile()
		.equals(Constants.TipoVersamento.NO_ZIP.name())) {
	    rispostaControlli.setrBoolean(false);
	    rispostaControlli.setCodErr(MessaggiWSBundle.PING_RICHOBJ_006);
	    rispostaControlli
		    .setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.PING_RICHOBJ_006));
	}
	// Per il terzo controllo bisogna ottenere la sessione più recente relativa all'oggetto
	if (rispostaControlli.isrBoolean()) {
	    // Eseguo una select verificando che non esistano sessioni con stato
	    // 'IN_ATTESA_RECUP','RECUPERATO','IN_ATTESA_PRELIEVO','CHIUSO_OK'.
	    try {
		String queryStr = "SELECT COUNT(ses) FROM PigSessioneRecup ses "
			+ "WHERE ses.pigObject.idObject = :idObj "
			+ "AND ses.tiStato IN ('IN_ATTESA_RECUP','RECUPERATO','IN_ATTESA_PRELIEVO','CHIUSO_OK')";

		javax.persistence.Query query = entityManager.createQuery(queryStr);
		query.setParameter("idObj", idObject);
		Long count = (Long) query.getSingleResult();
		if (count > 0) {
		    rispostaControlli.setrBoolean(false);
		    rispostaControlli.setCodErr(MessaggiWSBundle.PING_RICHOBJ_008);
		    rispostaControlli.setDsErr(
			    MessaggiWSBundle.getString(MessaggiWSBundle.PING_RICHOBJ_008));
		}
	    } catch (Exception e) {
		rispostaControlli.setrBoolean(false);
		rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
		rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
			String.join("\n", ExceptionUtils.getRootCauseStackTrace(e))));
		log.error("Eccezione nella lettura  della tabella delle sessioni di recupero ", e);
	    }
	}

	return rispostaControlli;
    }
}
