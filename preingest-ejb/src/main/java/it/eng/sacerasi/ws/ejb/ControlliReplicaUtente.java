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

package it.eng.sacerasi.ws.ejb;

import it.eng.sacerasi.entity.IamUser;
//import it.eng.sacerasi.web.ejb.AuthEjb;
import it.eng.sacerasi.ws.dto.RispostaControlli;
import it.eng.sacerasi.messages.MessaggiWSBundle;
import java.util.List;
//import javax.ejb.EJB;
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
@Stateless(mappedName = "ControlliReplicaUtente")
@LocalBean
@TransactionAttribute(value = TransactionAttributeType.REQUIRES_NEW)
public class ControlliReplicaUtente {

    private static final Logger log = LoggerFactory.getLogger(ControlliReplicaUtente.class);
    //
    @PersistenceContext(unitName = "SacerAsiJPA")
    private EntityManager entityManager;

    /**
     * Verifica l'esistenza di un utente in base all'identificazione in input
     *
     * @param idUserIam
     *            l'id dell'utente
     * 
     * @return RispostaControlli.isrBoolean() == true se esiste l'utente
     *
     */
    public RispostaControlli verificaEsistenzaUtente(long idUserIam) {
        RispostaControlli rispostaControlli = new RispostaControlli();
        rispostaControlli.setrBoolean(false);
        try {
            IamUser user = entityManager.find(IamUser.class, idUserIam);
            if (user != null) {
                rispostaControlli.setrBoolean(true);
            }

        } catch (Exception e) {
            rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
            rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
                    String.join("\n", ExceptionUtils.getRootCauseStackTrace(e))));
            log.error("Eccezione nella lettura della tabella degli utenti ", e);
        }
        return rispostaControlli;
    }

    /**
     * Verifica l'esistenza di un utente in base all'identificazione in input
     *
     * @param idUserIam
     *            l'id dell'utente
     * 
     * @return RispostaControlli.isrBoolean() == true se esiste l'utente ed è attivo
     *
     */
    @SuppressWarnings("unchecked")
    public RispostaControlli verificaEsistenzaUtenteAttivo(long idUserIam) {
        RispostaControlli rispostaControlli = new RispostaControlli();
        rispostaControlli.setrBoolean(false);
        try {
            Query query = entityManager
                    .createQuery("SELECT u FROM IamUser u WHERE u.idUserIam = :idUserIam AND u.flAttivo = '1' ");
            query.setParameter("idUserIam", idUserIam);
            List<IamUser> userList = query.getResultList();
            if (userList != null && !userList.isEmpty()) {
                rispostaControlli.setrBoolean(true);
            }

        } catch (Exception e) {
            rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
            rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
                    String.join("\n", ExceptionUtils.getRootCauseStackTrace(e))));
            log.error("Eccezione nella lettura della tabella degli utenti ", e);
        }
        return rispostaControlli;
    }

    /**
     * Verifica l'esistenza di uno userid in base al valore in input
     *
     * @param nmUserid
     *            il nome utente da verificare
     * 
     * @return RispostaControlli.isrBoolean() == true se esiste
     *
     */
    @SuppressWarnings("unchecked")
    public RispostaControlli verificaEsistenzaNmUserid(String nmUserid) {
        RispostaControlli rispostaControlli = new RispostaControlli();
        rispostaControlli.setrBoolean(false);
        try {
            if (nmUserid != null) {
                Query query = entityManager
                        .createQuery("SELECT u FROM IamUser u WHERE u.nmUserid = :nmUserid AND u.flAttivo = '1'");
                query.setParameter("nmUserid", nmUserid);
                List<IamUser> userList = query.getResultList();
                if (userList != null && !userList.isEmpty()) {
                    rispostaControlli.setrBoolean(true);
                }
            }
        } catch (Exception e) {
            rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
            rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
                    String.join("\n", ExceptionUtils.getRootCauseStackTrace(e))));
            log.error("Eccezione nella lettura della tabella degli utenti ", e);
        }
        return rispostaControlli;
    }
}
