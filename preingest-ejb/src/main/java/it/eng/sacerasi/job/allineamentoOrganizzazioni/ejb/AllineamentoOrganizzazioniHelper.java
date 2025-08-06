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

package it.eng.sacerasi.job.allineamentoOrganizzazioni.ejb;

import it.eng.paginator.util.HibernateUtils;
import it.eng.sacerasi.common.Constants;
import it.eng.sacerasi.entity.IamOrganizDaReplic;
import it.eng.sacerasi.entity.PigAmbienteVers;
import it.eng.sacerasi.entity.PigTipoObject;
import it.eng.sacerasi.entity.PigVers;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author Gilioli_P
 */
@Stateless(mappedName = "AllineamentoOrganizzazioniHelper")
@LocalBean
public class AllineamentoOrganizzazioniHelper {
    @PersistenceContext(unitName = "SacerAsiJPA")
    private EntityManager entityManager;
    @EJB
    private AllineamentoOrganizzazioniHelper me;

    @SuppressWarnings("unchecked")
    public List<IamOrganizDaReplic> getIamOrganizDaReplic() {
	String queryStr = "SELECT organiz FROM IamOrganizDaReplic organiz "
		+ "WHERE organiz.tiStatoReplic "
		+ "IN ('DA_REPLICARE', 'REPLICA_IN_TIMEOUT', 'REPLICA_IN_ERRORE') "
		+ "ORDER BY organiz.dtLogOrganizDaReplic ";
	javax.persistence.Query query = entityManager.createQuery(queryStr);
	return query.getResultList();
    }

    public PigAmbienteVers getPigAmbienteVers(BigDecimal idAmbienteVers) {
	return entityManager.find(PigAmbienteVers.class, idAmbienteVers.longValue());
    }

    public PigVers getPigVers(BigDecimal idVers) {
	return entityManager.find(PigVers.class, idVers.longValue());
    }

    @SuppressWarnings("unchecked")
    public List<PigTipoObject> getPigTipoObjectList(List<Long> idVers) {
	String queryStr = "SELECT u FROM PigTipoObject u " + "WHERE u.pigVer.idVers IN (:idVers) ";
	Query q = entityManager.createQuery(queryStr);
	q.setParameter("idVers", idVers);
	return q.getResultList();
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void writeEsitoIamOrganizDaReplic(Long idOrganizDaReplic,
	    Constants.EsitoServizio esitoServizio, String cdErr, String dsErr) {
	IamOrganizDaReplic organizDaReplic = entityManager.find(IamOrganizDaReplic.class,
		idOrganizDaReplic);
	if (esitoServizio == Constants.EsitoServizio.OK) {
	    organizDaReplic.setTiStatoReplic(Constants.TiStatoReplic.REPLICA_OK.name());
	    organizDaReplic.setCdErr(null);
	    organizDaReplic.setDsMsgErr(null);
	    organizDaReplic.setDtErr(null);
	} else if (esitoServizio == Constants.EsitoServizio.KO) {
	    switch (cdErr) {
	    case Constants.SERVIZI_ORG_001:
	    case Constants.SERVIZI_ORG_007:
	    case Constants.SERVIZI_ORG_004:
	    case Constants.SERVIZI_ORG_006:
		organizDaReplic.setTiStatoReplic(Constants.TiStatoReplic.REPLICA_IN_ERRORE.name());
		organizDaReplic.setCdErr(cdErr);
		organizDaReplic.setDsMsgErr(dsErr);
		organizDaReplic.setDtErr(new Date());
		break;
	    case Constants.SERVIZI_ORG_002:
		organizDaReplic.setTiStatoReplic(Constants.TiStatoReplic.REPLICA_OK.name());
		organizDaReplic.setCdErr(null);
		organizDaReplic.setDsMsgErr(null);
		organizDaReplic.setDtErr(null);
		break;
	    case Constants.SERVIZI_ORG_003:
	    case Constants.SERVIZI_ORG_005:
		organizDaReplic
			.setTiStatoReplic(Constants.TiStatoReplic.REPLICA_NON_POSSIBILE.name());
		organizDaReplic.setCdErr(cdErr);
		organizDaReplic.setDsMsgErr(dsErr);
		organizDaReplic.setDtErr(new Date());
		break;
	    default:
		break;
	    }
	} else if (esitoServizio == Constants.EsitoServizio.NO_RISPOSTA) {
	    organizDaReplic.setTiStatoReplic(Constants.TiStatoReplic.REPLICA_IN_TIMEOUT.name());
	    organizDaReplic.setCdErr(cdErr);
	    organizDaReplic.setDsMsgErr(dsErr);
	    organizDaReplic.setDtErr(new Date());
	}
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> getEnteConvenzInfo(BigDecimal idVers) {
	String queryStr = "SELECT vers.idEnteConvenz, vers.idEnteFornitEstern, vers.dtIniValAppartEnteSiam, vers.dtFineValAppartEnteSiam FROM PigVers vers "
		+ "WHERE vers.idVers = :idVers ";
	Query q = entityManager.createQuery(queryStr);
	q.setParameter("idVers", HibernateUtils.longFrom(idVers));
	List<Object[]> versObjList = q.getResultList();
	Map<String, Object> mappa = new HashMap<>();
	if (!versObjList.isEmpty()) {
	    Object[] versObj = versObjList.get(0);
	    mappa.put("idEnteConvenz", versObj[0]);
	    mappa.put("idEnteFornitEstern", versObj[1]);
	    mappa.put("dtIniVal", versObj[2]);
	    mappa.put("dtFineVal", versObj[3]);
	}
	return mappa;
    }

}
