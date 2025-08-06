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

package it.eng.sacerasi.web.helper;

import it.eng.paginator.util.HibernateUtils;
import it.eng.sacerasi.entity.PigAmbienteVers;
import it.eng.sacerasi.entity.PigTipoObject;
import it.eng.sacerasi.entity.PigVers;
import it.eng.sacerasi.slite.gen.tablebean.PigAmbienteVersTableBean;
import it.eng.sacerasi.slite.gen.tablebean.PigTipoObjectTableBean;
import it.eng.sacerasi.slite.gen.tablebean.PigVersRowBean;
import it.eng.sacerasi.slite.gen.tablebean.PigVersTableBean;
import it.eng.sacerasi.web.util.Transform;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Session Bean implementation class ComboHelper Contiene i metodi, per la gestione della
 * persistenza su DB per le operazioni CRUD su oggetti di Sacer Asincrono
 */
@SuppressWarnings("unchecked")
@Stateless
@LocalBean
public class ComboHelper {

    public ComboHelper() {
	/*
	 * per sonar
	 *
	 */
    }

    private static final Logger log = LoggerFactory.getLogger(ComboHelper.class.getName());
    @PersistenceContext(unitName = "SacerAsiJPA")
    private EntityManager entityManager;

    /**
     * Recupera l'ambiente versatore in base alle abilitazioni
     *
     * @param idUtente id utente
     *
     * @return ambienteTableBean, il tablebean di ambienti versatore
     */
    public PigAmbienteVersTableBean getAmbienteVersatoreFromUtente(long idUtente) {
	String queryStr = "SELECT DISTINCT ambienteVer "
		+ " FROM IamUser iamUsr JOIN iamUsr.iamAbilOrganizs iamAbilOrgs, PigVers ver JOIN ver.pigAmbienteVer ambienteVer "
		+ " WHERE iamAbilOrgs.iamUser.idUserIam = :idUtente "
		+ " AND iamAbilOrgs.idOrganizApplic = ver.idVers "
		+ " ORDER BY ambienteVer.nmAmbienteVers ";

	Query query = entityManager.createQuery(queryStr);
	query.setParameter("idUtente", idUtente);

	PigAmbienteVersTableBean ambienteTableBean = new PigAmbienteVersTableBean();
	List<PigAmbienteVers> ambienteList = query.getResultList();
	try {
	    if (!ambienteList.isEmpty()) {
		// trasformo la lista di entity (risultante della query) in un tablebean
		ambienteTableBean = (PigAmbienteVersTableBean) Transform
			.entities2TableBean(ambienteList);
	    }
	} catch (Exception e) {
	    log.error(e.getMessage());
	}
	return ambienteTableBean;
    }

    public PigVersTableBean getVersatoreFromAmbienteVersatore(Long idUtente,
	    BigDecimal idAmbienteVers) {
	String queryStr = "SELECT ver "
		+ " FROM IamUser iamUsr JOIN iamUsr.iamAbilOrganizs iamAbilOrgs, PigVers ver JOIN ver.pigAmbienteVer ambienteVer "
		+ " WHERE iamAbilOrgs.iamUser.idUserIam = :idUtente "
		+ " AND ambienteVer.idAmbienteVers = :idAmbienteVers " + " AND ver.flCessato != 1 "
		+ " AND iamAbilOrgs.idOrganizApplic = ver.idVers " + " ORDER BY ver.nmVers ";

	Query query = entityManager.createQuery(queryStr);
	query.setParameter("idUtente", idUtente);
	query.setParameter("idAmbienteVers", idAmbienteVers.longValue());

	PigVersTableBean versTableBean = new PigVersTableBean();
	List<PigVers> versList = query.getResultList();
	try {
	    if (!versList.isEmpty()) {
		// trasformo la lista di entity (risultante della query) in un tablebean
		versTableBean = (PigVersTableBean) Transform.entities2TableBean(versList);
	    }
	} catch (Exception e) {
	    log.error(e.getMessage(), e);
	}
	return versTableBean;
    }

    public PigTipoObjectTableBean getTipoObjectFromVersatore(long idUtente, BigDecimal idVers) {
	StringBuilder queryStr = new StringBuilder(
		"SELECT DISTINCT u from PigTipoObject u, IamAbilTipoDato iatd "
			+ "WHERE iatd.idTipoDatoApplic = u.idTipoObject "
			+ "AND iatd.nmClasseTipoDato = 'TIPO_OBJECT' "
			+ "AND iatd.iamAbilOrganiz.iamUser.idUserIam = :idUtente ");

	if (idVers != null) {
	    queryStr.append("AND iatd.iamAbilOrganiz.idOrganizApplic = :idVers ");
	}

	queryStr.append("ORDER BY u.nmTipoObject ");

	Query query = entityManager.createQuery(queryStr.toString());
	if (idVers != null) {
	    query.setParameter("idVers", idVers);
	}
	query.setParameter("idUtente", idUtente);

	PigTipoObjectTableBean tipoObjectTableBean = new PigTipoObjectTableBean();
	List<PigTipoObject> tipoObjectList = query.getResultList();
	try {
	    if (!tipoObjectList.isEmpty()) {
		// trasformo la lista di entity (risultante della query) in un tablebean
		tipoObjectTableBean = (PigTipoObjectTableBean) Transform
			.entities2TableBean(tipoObjectList);
	    }
	} catch (Exception e) {
	    log.error(e.getMessage(), e);
	}
	return tipoObjectTableBean;
    }

    public PigTipoObjectTableBean getTipoObjectFromVersatore(long idUtente, BigDecimal idVers,
	    String... tipoVers) {
	return getTipoObjectFromVersatore(idUtente, idVers, false, tipoVers);
    }

    /*
     * Esclude i tipi oggetto come fleggati per non essere visualizzati
     */
    public PigTipoObjectTableBean getTipoObjectFromVersatoreNoFleggati(long idUtente,
	    BigDecimal idVers, String... tipoVers) {
	return getTipoObjectFromVersatore(idUtente, idVers, true, tipoVers);
    }

    /*
     * Se viene passato il flag a vero non vengono estratti i tipi oggetto con il flag di visibilità
     * alzato
     */
    private PigTipoObjectTableBean getTipoObjectFromVersatore(long idUtente, BigDecimal idVers,
	    boolean escludiFleggati, String... tipoVers) {
	StringBuilder queryStr = new StringBuilder(
		"SELECT DISTINCT u from PigTipoObject u, IamAbilTipoDato iatd "
			+ "WHERE iatd.idTipoDatoApplic = u.idTipoObject "
			+ "AND iatd.nmClasseTipoDato = 'TIPO_OBJECT' "
			+ "AND iatd.iamAbilOrganiz.iamUser.idUserIam = :idUtente ");
	if (escludiFleggati) {
	    queryStr.append(" AND (u.flNoVisibVersOgg='0' OR u.flNoVisibVersOgg IS NULL)");
	}
	if (idVers != null) {
	    queryStr.append("AND iatd.iamAbilOrganiz.idOrganizApplic = :idVers ");
	}

	if (tipoVers != null && tipoVers.length > 0) {
	    if (tipoVers.length == 1) {
		queryStr.append("AND u.tiVersFile = :tiVers ");
	    } else {
		queryStr.append("AND u.tiVersFile IN (:tiVers) ");
	    }
	}

	queryStr.append("ORDER BY u.nmTipoObject ");

	Query query = entityManager.createQuery(queryStr.toString());
	if (idVers != null) {
	    query.setParameter("idVers", idVers);
	}
	if (tipoVers != null && tipoVers.length > 0) {
	    if (tipoVers.length == 1) {
		query.setParameter("tiVers", tipoVers[0]);
	    } else {
		List<String> tmp = Arrays.asList(tipoVers);
		query.setParameter("tiVers", tmp);
	    }
	}
	query.setParameter("idUtente", idUtente);

	PigTipoObjectTableBean tipoObjectTableBean = new PigTipoObjectTableBean();
	List<PigTipoObject> tipoObjectList = query.getResultList();
	try {
	    if (!tipoObjectList.isEmpty()) {
		// trasformo la lista di entity (risultante della query) in un tablebean
		tipoObjectTableBean = (PigTipoObjectTableBean) Transform
			.entities2TableBean(tipoObjectList);
	    }
	} catch (Exception e) {
	    log.error(e.getMessage(), e);
	}
	return tipoObjectTableBean;
    }

    /**
     * Recupera i versatori da mostrare nella combo della pagina di scelta versatori dopo aver
     * effettuato il login
     *
     * @param idUtente id utente
     *
     * @return Object[], l'object array contenente i dati sui versatori
     */
    public Object[] getVersatori(long idUtente) {
	String queryStr = "SELECT ver, CONCAT(ver.pigAmbienteVer.nmAmbienteVers, ', ', ver.nmVers) "
		+ " FROM IamUser iamUsr JOIN iamUsr.iamAbilOrganizs iamAbilOrgs, PigVers ver "
		+ " WHERE iamAbilOrgs.iamUser.idUserIam = :idUtente "
		+ " AND iamAbilOrgs.idOrganizApplic = ver.idVers "
		+ " ORDER BY ver.pigAmbienteVer.nmAmbienteVers, ver.nmVers";

	Query query = entityManager.createQuery(queryStr);
	query.setParameter("idUtente", idUtente);
	List<Object[]> pigVers = query.getResultList();
	PigVersTableBean versatoriTableBean = new PigVersTableBean();
	BigDecimal idDefVers = null;
	try {
	    // trasformo la lista di entity (risultante della query) in un tablebean
	    for (Object[] row : pigVers) {
		PigVersRowBean rowBean = (PigVersRowBean) Transform.entity2RowBean(row[0]);
		// setto il nome esteso : Ambiente, Versatore
		rowBean.setString("nm_extname", row[1].toString());
		versatoriTableBean.add(rowBean);
	    }
	} catch (Exception e) {
	    log.error(e.getMessage());
	}
	return new Object[] {
		versatoriTableBean, idDefVers };
    }

    public BigDecimal getIdAmbienteVersatore(BigDecimal idVers) {
	String queryStr = "SELECT u.pigAmbienteVer.idAmbienteVers FROM PigVers u WHERE u.idVers = :idVers ";
	Query query = entityManager.createQuery(queryStr);
	query.setParameter("idVers", HibernateUtils.longFrom(idVers));
	Long res = (Long) query.getSingleResult();
	return new BigDecimal(res);
    }
}
