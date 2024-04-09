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

package it.eng.sacerasi.ws.richiestaSopClassList.ejb;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.sacerasi.entity.PigSopClassDicom;
import it.eng.sacerasi.messages.MessaggiWSBundle;
import it.eng.sacerasi.ws.dto.RispostaControlli;

/**
 *
 * @author Bonora_L
 */
@Stateless(mappedName = "RichiestaSopClassListHelper")
@LocalBean
public class RichiestaSopClassListHelper {

    private static final Logger log = LoggerFactory.getLogger(RichiestaSopClassListHelper.class);
    @PersistenceContext(unitName = "SacerAsiJPA")
    private EntityManager entityManager;

    @SuppressWarnings("unchecked")
    public RispostaControlli getSopClassListByIdVersatore(Long idVersatore) {

        RispostaControlli rispostaControlli = new RispostaControlli();
        rispostaControlli.setrBoolean(true);

        try {
            String queryStr = "SELECT sop FROM PigSopClassDicomVers sopVers INNER JOIN sopVers.pigSopClassDicom sop WHERE sopVers.pigVer.idVers = :idVers";

            javax.persistence.Query query = entityManager.createQuery(queryStr);
            query.setParameter("idVers", idVersatore);
            List<PigSopClassDicom> listaSopClass = query.getResultList();
            rispostaControlli.setrObject(listaSopClass);
        } catch (Exception e) {
            rispostaControlli.setrBoolean(false);
            rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
            rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
                    String.join("\n", ExceptionUtils.getRootCauseStackTrace(e))));
            log.error("Eccezione nella lettura  della tabella delle liste SopClass ", e);
        }

        return rispostaControlli;
    }
}
