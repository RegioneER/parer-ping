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

package it.eng.xformer.helper;

import java.util.Date;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.Query;

import org.apache.commons.lang3.StringUtils;

import it.eng.paginator.util.HibernateUtils;
import it.eng.sacerasi.entity.PigObject;
import it.eng.sacerasi.entity.PigObjectTrasf;
import it.eng.sacerasi.entity.PigTipoObject;
import it.eng.sacerasi.entity.PigVersTipoObjectDaTrasf;
import it.eng.sacerasi.entity.XfoFileTrasf;
import it.eng.sacerasi.entity.XfoParamTrasf;
import it.eng.sacerasi.entity.XfoSetParamTrasf;
import it.eng.sacerasi.entity.XfoStoricoTrasf;
import it.eng.sacerasi.entity.XfoTrasf;
import it.eng.sacerasi.helper.GenericHelper;
import it.eng.sacerasi.viewEntity.PigVValoreParamTrasf;
import it.eng.xformer.dto.RicercaTrasformazioneBean;

@SuppressWarnings("unchecked")
@Stateless
@LocalBean
public class TrasformazioniQueryHelper extends GenericHelper {
    public List<XfoTrasf> searchXfoTrasf(RicercaTrasformazioneBean filtri) {
	String queryStr = "SELECT x FROM XfoTrasf x";
	StringBuilder whereClause = new StringBuilder("");

	if (StringUtils.isNotBlank(filtri.getCd_trasf())) {
	    if (whereClause.length() != 0) {
		whereClause.append(" AND ");
	    }

	    whereClause.append("UPPER(x.cdTrasf) LIKE :cdTrasf");
	}

	if (StringUtils.isNotBlank(filtri.getDs_trasf())) {
	    if (whereClause.length() != 0) {
		whereClause.append(" AND ");
	    }

	    whereClause.append("UPPER(x.dsTrasf) LIKE :dsTrasf");
	}

	if (StringUtils.isNotBlank(filtri.getCd_versione_cor())) {
	    if (whereClause.length() != 0) {
		whereClause.append(" AND ");
	    }

	    whereClause.append("UPPER(x.cdVersioneCor) LIKE :cdVersioneCor");
	}

	if (StringUtils.isNotBlank(filtri.getDs_versione_cor())) {
	    if (whereClause.length() != 0) {
		whereClause.append(" AND ");
	    }

	    whereClause.append("UPPER(x.dsVersioneCor) LIKE :dsVersioneCor");
	}

	if (StringUtils.isNotBlank(filtri.getFl_attiva())) {
	    if (whereClause.length() != 0) {
		whereClause.append(" AND ");
	    }

	    whereClause.append("x.flAttiva = :flAttiva");
	}

	if (filtri.getDt_istituz() != null) {
	    if (whereClause.length() != 0) {
		whereClause.append(" AND ");
	    }

	    whereClause.append("x.dtIstituz >= :dtIstituz");
	}

	if (filtri.getDt_soppres() != null) {
	    if (whereClause.length() != 0) {
		whereClause.append(" AND ");
	    }

	    whereClause.append("x.dtSoppres <= :dtSoppres");
	}

	if (whereClause.length() != 0) {
	    whereClause.insert(0, " WHERE ");
	}

	Query query = getEntityManager()
		.createQuery(queryStr + whereClause + " ORDER BY x.cdTrasf ASC");
	if (StringUtils.isNotBlank(filtri.getCd_trasf())) {
	    query.setParameter("cdTrasf", "%" + filtri.getCd_trasf().toUpperCase() + "%");
	}

	if (StringUtils.isNotBlank(filtri.getDs_trasf())) {
	    query.setParameter("dsTrasf", "%" + filtri.getDs_trasf().toUpperCase() + "%");
	}

	if (StringUtils.isNotBlank(filtri.getCd_versione_cor())) {
	    query.setParameter("cdVersioneCor",
		    "%" + filtri.getCd_versione_cor().toUpperCase() + "%");
	}

	if (StringUtils.isNotBlank(filtri.getDs_versione_cor())) {
	    query.setParameter("dsVersioneCor",
		    "%" + filtri.getDs_versione_cor().toUpperCase() + "%");
	}

	if (StringUtils.isNotBlank(filtri.getFl_attiva())) {
	    query.setParameter("flAttiva", filtri.getFl_attiva());
	}

	if (filtri.getDt_istituz() != null) {
	    query.setParameter("dtIstituz", filtri.getDt_istituz());
	}

	if (filtri.getDt_soppres() != null) {
	    query.setParameter("dtSoppres", filtri.getDt_soppres());
	}

	return query.getResultList();
    }

    public List<XfoStoricoTrasf> searchXfoStoricoTrasfbyXfoTrasf(long idTrasf) {
	String queryStr = "SELECT x FROM XfoStoricoTrasf x WHERE x.xfoTrasf.idTrasf = :idTrasf";

	Query query = getEntityManager().createQuery(queryStr);
	query.setParameter("idTrasf", idTrasf);

	return query.getResultList();
    }

    public List<XfoSetParamTrasf> searchXfoSetParamTrasfbyXfoTrasf(long idTrasf) {
	String queryStr = "SELECT x FROM XfoSetParamTrasf x WHERE x.xfoTrasf.idTrasf = :idTrasf";

	Query query = getEntityManager().createQuery(queryStr);
	query.setParameter("idTrasf", idTrasf);

	return query.getResultList();
    }

    public List<XfoParamTrasf> searchXfoParamTrasfbySet(long idSetParamTrasf) {
	String queryStr = "SELECT x FROM XfoParamTrasf x WHERE x.xfoSetParamTrasf.idSetParamTrasf = :idSetParamTrasf ORDER BY x.nmParamTrasf ASC";

	Query query = getEntityManager().createQuery(queryStr);
	query.setParameter("idSetParamTrasf", idSetParamTrasf);

	return query.getResultList();
    }

    public boolean transformationNameExists(String transformationName) {
	String queryStr = "SELECT x FROM XfoTrasf x WHERE x.cdTrasf = :cdTrasf";

	Query query = getEntityManager().createQuery(queryStr);
	query.setParameter("cdTrasf", transformationName);

	List<XfoTrasf> transfs = query.getResultList();
	return !transfs.isEmpty();
    }

    public boolean parametersSetExists(String parametersSetName, long idTrasf) {
	String queryStr = "SELECT x FROM XfoSetParamTrasf x WHERE x.nmSetParamTrasf = :nmSetParamTrasf AND x.xfoTrasf.idTrasf = :idTrasf";

	Query query = getEntityManager().createQuery(queryStr);
	query.setParameter("nmSetParamTrasf", parametersSetName);
	query.setParameter("idTrasf", idTrasf);

	List<XfoSetParamTrasf> parameters = query.getResultList();
	if (parameters.isEmpty()) {
	    return false;
	} else {
	    return true;
	}
    }

    public XfoSetParamTrasf getParametersSet(String parametersSetName, long idTrasf) {
	String queryStr = "SELECT x FROM XfoSetParamTrasf x WHERE x.nmSetParamTrasf = :nmSetParamTrasf AND x.xfoTrasf.idTrasf = :idTrasf";

	Query query = getEntityManager().createQuery(queryStr);
	query.setParameter("nmSetParamTrasf", parametersSetName);
	query.setParameter("idTrasf", idTrasf);

	List<XfoSetParamTrasf> parameters = query.getResultList();
	if (parameters.isEmpty()) {
	    return null;
	} else {
	    return parameters.get(0);
	}
    }

    public List<XfoParamTrasf> gettAllXfoParamTrasfbyTrasf(long idTrasf) {
	String queryStr = "SELECT x FROM XfoParamTrasf x WHERE x.xfoSetParamTrasf.xfoTrasf.idTrasf = :idTrasf";

	Query query = getEntityManager().createQuery(queryStr);
	query.setParameter("idTrasf", idTrasf);

	return query.getResultList();
    }

    public XfoParamTrasf searchXfoParamTrasfbyName(String paramenterName, long idTrasf) {
	String queryStr = "SELECT x FROM XfoParamTrasf x WHERE x.nmParamTrasf = :nmParamTrasf AND x.xfoSetParamTrasf.xfoTrasf.idTrasf = :idTrasf";

	Query query = getEntityManager().createQuery(queryStr);
	query.setParameter("nmParamTrasf", paramenterName);
	query.setParameter("idTrasf", idTrasf);

	List<XfoParamTrasf> results = query.getResultList();

	if (results.isEmpty()) {
	    return null;
	} else {
	    return results.get(0);
	}
    }

    public PigVValoreParamTrasf searchPigVValoreParamTrasfByName(String paramenterName,
	    long idVersTipoObjectDaTrasf) {
	String queryStr = "SELECT x FROM PigVValoreParamTrasf x WHERE x.nmParamTrasf = :nmParamTrasf AND x.id.idVersTipoObjectDaTrasf = :idVersTipoObjectDaTrasf";

	Query query = getEntityManager().createQuery(queryStr);
	query.setParameter("nmParamTrasf", paramenterName);
	query.setParameter("idVersTipoObjectDaTrasf",
		HibernateUtils.bigDecimalFrom(idVersTipoObjectDaTrasf));

	List<PigVValoreParamTrasf> results = query.getResultList();

	if (results.isEmpty()) {
	    return null;
	} else {
	    return results.get(0);
	}
    }

    public PigVersTipoObjectDaTrasf getPigVersTipoObjectDaTrasf(PigObject object) {
	String queryStr = "SELECT x FROM PigVersTipoObjectDaTrasf x WHERE x.cdVersGen= :cdVersGen AND x.pigTipoObjectDaTrasf.idTipoObject = :idTipoObject";

	Query query = getEntityManager().createQuery(queryStr);
	query.setParameter("cdVersGen", object.getCdVersGen());
	query.setParameter("idTipoObject", object.getPigTipoObject().getIdTipoObject());

	List<PigVersTipoObjectDaTrasf> results = query.getResultList();

	if (results.isEmpty()) {
	    return null;
	} else {
	    return results.get(0);
	}
    }

    public List<PigObjectTrasf> searchGeneratedPigObjects(PigObject object) {
	String queryStr = "SELECT x FROM PigObjectTrasf x WHERE x.pigObject.idObject = :idFatherObject";

	Query query = getEntityManager().createQuery(queryStr);
	query.setParameter("idFatherObject", object.getIdObject());

	return query.getResultList();
    }

    public PigObjectTrasf findGeneratedPigObjectTrasf(String cdKeyObjectTrasf, PigObject object) {
	String queryStr = "SELECT x FROM PigObjectTrasf x WHERE x.cdKeyObjectTrasf = :cdKeyObjectTrasf AND x.pigObject.idObject = :idFatherObject";

	Query query = getEntityManager().createQuery(queryStr);
	query.setParameter("idFatherObject", object.getIdObject());
	query.setParameter("cdKeyObjectTrasf", cdKeyObjectTrasf);

	List<PigObjectTrasf> results = query.getResultList();

	if (results.isEmpty()) {
	    return null;
	} else {
	    return results.get(0);
	}
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public PigObject searchPigObjectTrasfInPigObjects(PigObjectTrasf pot) {
	String queryStr = "SELECT x FROM PigObject x WHERE x.cdKeyObject = :cdKeyObjectTrasf AND x.pigVer.idVers = :idVersTrasf AND x.pigObjectPadre.idObject = :idFatherObject";

	Query query = getEntityManager().createQuery(queryStr);
	query.setParameter("cdKeyObjectTrasf", pot.getCdKeyObjectTrasf());
	query.setParameter("idVersTrasf", pot.getPigVer().getIdVers());
	query.setParameter("idFatherObject", pot.getPigObject().getIdObject());

	List<PigObject> results = query.getResultList();

	if (results.isEmpty()) {
	    return null;
	} else {
	    return results.get(0);
	}
    }

    public boolean isTransformationAssigned(long idTrasf) {
	String queryString = "SELECT x FROM PigTipoObject x WHERE x.xfoTrasf.idTrasf = :idTrasf";

	Query query = getEntityManager().createQuery(queryString);
	query.setParameter("idTrasf", idTrasf);

	List<PigTipoObject> results = query.getResultList();

	return !results.isEmpty();
    }

    public List<XfoFileTrasf> searchAuxiliaryFilesByXfoTras(long idTrasf) {
	String queryString = "SELECT x FROM XfoFileTrasf x WHERE x.xfoTrasf.idTrasf = :idTrasf";

	Query query = getEntityManager().createQuery(queryString);
	query.setParameter("idTrasf", idTrasf);

	return query.getResultList();
    }

    public boolean isVersionUnique(long idTrasf, String version) {
	String queryString = "SELECT x FROM XfoStoricoTrasf x WHERE x.xfoTrasf.idTrasf = :idTrasf AND x.cdVersione = :version";

	Query query = getEntityManager().createQuery(queryString);
	query.setParameter("idTrasf", idTrasf);
	query.setParameter("version", version);

	List<XfoStoricoTrasf> versions = query.getResultList();

	return versions.isEmpty();
    }

    public boolean isVersionDateUnique(long idTrasf, Date date) {
	String queryString = "SELECT x FROM XfoStoricoTrasf x WHERE x.xfoTrasf.idTrasf = :idTrasf AND x.dtIstituz = :date";

	Query query = getEntityManager().createQuery(queryString);
	query.setParameter("idTrasf", idTrasf);
	query.setParameter("date", date);

	List<XfoStoricoTrasf> versions = query.getResultList();

	return versions.isEmpty();
    }

    public boolean isDateIstituzOverlapping(long idTrasf, Date date) {
	String queryString = "SELECT x FROM XfoStoricoTrasf x WHERE x.xfoTrasf.idTrasf = :idTrasf AND x.dtSoppres >= :date";

	Query query = getEntityManager().createQuery(queryString);
	query.setParameter("idTrasf", idTrasf);
	query.setParameter("date", date);

	List<XfoStoricoTrasf> versions = query.getResultList();

	return !versions.isEmpty();
    }

    public String retriveKettleId(long idTrasf) {
	String queryString = "SELECT x.cdKettleId FROM XfoTrasf x WHERE x.idTrasf = :idTrasf";

	Query query = getEntityManager().createQuery(queryString);
	query.setParameter("idTrasf", idTrasf);

	List<String> results = query.getResultList();

	if (results.isEmpty()) {
	    return null;
	} else {
	    return results.get(0) != null ? results.get(0) : "";
	}
    }
}
