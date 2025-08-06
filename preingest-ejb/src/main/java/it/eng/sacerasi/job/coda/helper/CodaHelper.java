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

package it.eng.sacerasi.job.coda.helper;

import it.eng.paginator.util.HibernateUtils;
import it.eng.sacerasi.common.Constants.StatoSessioneIngest;
import it.eng.sacerasi.common.Constants.StatoUnitaDocSessione;
import it.eng.sacerasi.common.Constants.StatoVerificaHash;
import it.eng.sacerasi.entity.PigObject;
import it.eng.sacerasi.entity.PigSessioneIngest;
import it.eng.sacerasi.entity.PigStatoSessioneIngest;
import it.eng.sacerasi.entity.PigUnitaDocObject;
import it.eng.sacerasi.entity.PigUnitaDocSessione;
import it.eng.sacerasi.entity.PigVers;
import it.eng.sacerasi.entity.PigXmlSacerUnitaDoc;
import it.eng.sacerasi.viewEntity.MonVCalcStatoObjDaTrasf;
import it.eng.sacerasi.web.util.Constants;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static it.eng.sacerasi.common.Constants.JPA_PORPERTIES_TIMEOUT;

/**
 *
 * @author Agati_D
 */
@SuppressWarnings("unchecked")
@Stateless(mappedName = "CodaHelper")
@LocalBean
public class CodaHelper {

    Logger log = LoggerFactory.getLogger(CodaHelper.class);

    @PersistenceContext(unitName = "SacerAsiJPA")
    private EntityManager em;

    public List<PigVers> retrieveVersatori() {
	// MEV#27321 - Introduzione della priorità di versamento di un oggetto ZIP_CON_XML_SACER e
	// NO_ZIP
	Query q = em.createQuery("SELECT v FROM PigSessioneIngest ses, PigObject obj "
		+ "JOIN   ses.pigVer v " + "WHERE  ses.idSessioneIngest = obj.idLastSessioneIngest "
		+ "AND    ses.tiStato='IN_ATTESA_VERS' "
		+ "ORDER BY obj.tiPrioritaVersamento, ses.dtApertura ASC");
	return q.getResultList();
    }

    public Stream<PigObject> retrieveObjectsByState(StatoSessioneIngest stato) {
	Query q = em.createQuery("SELECT obj FROM PigSessioneIngest ses, PigObject obj "
		+ "WHERE ses.idSessioneIngest = obj.idLastSessioneIngest "
		+ "AND ses.tiStato=:statoSessione "
		+ "ORDER BY obj.tiPrioritaVersamento, ses.dtApertura ASC");
	q.setParameter("statoSessione", stato.name());
	return q.getResultStream();
    }

    public EntityManager getEntityManager() {
	return em;
    }

    public List<Long> retrieveObjectsIdByIdVersAndState(Long idVers, String state1, String state2) {
	Query q = em.createQuery("SELECT o.idObject " + "FROM PigObject o "
		+ "where (o.pigVer.idVers = :idVers and o.tiStatoObject = :state1) "
		+ "or (o.pigVer.idVers = :idVers and o.tiStatoObject = :state2) ORDER BY o.tiPrioritaVersamento");
	q.setParameter("idVers", idVers);
	q.setParameter("state1", state1);
	q.setParameter("state2", state2);
	return q.getResultList();
    }

    public List<Long> retrieveObjectsIdByIdVersAndState(Long idVers, String state) {
	Query q = em.createQuery("SELECT o.idObject " + "FROM PigObject o "
		+ "WHERE o.pigVer.idVers = :idVers AND o.tiStatoObject = :state1 ORDER BY o.tiPrioritaVersamento");

	q.setParameter("idVers", idVers);
	q.setParameter("state1", state);
	return q.getResultList();
    }

    public PigObject findPigObjectById(Long pigObjectId) {
	return em.find(PigObject.class, pigObjectId);
    }

    /*
     * torna un Oggetto per chiave e vi mette un lock pessimistico.
     */
    public PigObject findPigObjectByIdWithLock(Long pigObjectId) {
	final LockModeType lockModeType = LockModeType.PESSIMISTIC_WRITE;
	log.debug("Leggo PigObject id={} e faccio lock {}", pigObjectId, lockModeType);
	Map<String, Object> properties = new HashMap<>();
	properties.put(JPA_PORPERTIES_TIMEOUT, 25000);
	/*
	 * Attenzione, Oracle usa multi version control quindi il LockModeType.PESSIMISTIC_WRITE
	 * impedisce scritture concorrenti ma ammette che qualcun altro legga questo record ma senza
	 * gli eventuali update fatti in questa sessione
	 */
	return em.find(PigObject.class, pigObjectId, lockModeType, properties);
    }

    public List<Long> retrieveUnitaDocsIdByIdObjAndState(Long objId, String state) {
	Query q = em.createQuery("SELECT ud.idUnitaDocObject " + "FROM PigUnitaDocObject ud "
		+ "where ud.pigObject.idObject = :objId "
		+ "and ud.tiStatoUnitaDocObject = :state");
	q.setParameter("objId", objId);
	q.setParameter("state", state);
	return q.getResultList();
    }

    public PigUnitaDocObject findPigUnitaDocObjectById(Long unitaDocId) {
	return em.find(PigUnitaDocObject.class, unitaDocId);
    }

    public PigUnitaDocObject findLockPigUnitaDocObjectById(Long unitaDocId) {
	final LockModeType lockType = LockModeType.PESSIMISTIC_WRITE;
	log.debug("Leggo PigUnitaDocObject id={} e faccio lock {}", unitaDocId, lockType);
	Map<String, Object> properties = new HashMap<>();
	properties.put(JPA_PORPERTIES_TIMEOUT, 25000);
	/*
	 * Attenzione, Oracle usa multi version control quindi il LockModeType.PESSIMISTIC_WRITE
	 * impedisce scritture concorrenti ma ammette che qualcun altro legga questo record ma senza
	 * gli eventuali update fatti in quest sessione
	 */
	return em.find(PigUnitaDocObject.class, unitaDocId, lockType, properties);
    }

    public String selectQueue(BigDecimal niSizeFileByte) {
	Query q = em.createQuery("SELECT r.idCodaDaUsare " + "FROM AplVRangeMbyteCoda r "
		+ "where r.niLimiteInf < :dim " + "and r.niLimiteSup >= :dim");
	q.setParameter("dim", niSizeFileByte);
	return (String) q.getSingleResult();
    }

    public Long retrieveXmlIdByUdIdAndType(long idUnitaDocObject, String type) {
	Query q = em
		.createQuery("SELECT xmlUd.idXmlSacerUnitaDoc " + "FROM PigXmlSacerUnitaDoc xmlUd "
			+ "where xmlUd.pigUnitaDocObject.idUnitaDocObject = :unitaDocId "
			+ "and xmlUd.tiXmlSacer = :type");
	q.setParameter("unitaDocId", idUnitaDocObject);
	q.setParameter("type", type);
	return (Long) q.getSingleResult();
    }

    /*
     * Il sistema conta il numero di StudiDicom versati nella data odierna + il numero di StudiDicom
     * in stato = IN_CODA_VERS
     */
    public long contaStudiDicomVersatiOggiEInCodaVers() {
	Query q = em.createNativeQuery("SELECT " + "    COUNT(1) " + "FROM "
		+ "    pig_sessione_ingest         ses "
		+ "    JOIN pig_stato_sessione_ingest   ss ON ses.id_sessione_ingest = ss.id_sessione_ingest "
		+ "WHERE  ses.nm_tipo_object = '" + it.eng.sacerasi.common.Constants.STUDIO_DICOM
		+ "' "
		+ "    AND ( (   ss.ti_stato = ( 'CHIUSO_OK' )  AND ses.ti_stato = ( 'CHIUSO_OK' ) "
		+ "                AND trunc(ts_reg_stato, 'dd') = trunc(SYSDATE, 'dd') ) "
		+ "            OR   ( ss.ti_stato IN ('IN_CODA_VERS') "
		+ "               AND ses.ti_stato = ( 'IN_CODA_VERS' ) "
		+ "               AND trunc(ts_reg_stato, 'dd') = trunc(SYSDATE, 'dd') "
		+ "               AND NOT EXISTS (    SELECT * FROM "
		+ "                                    pig_stato_sessione_ingest ss2 "
		+ "                                WHERE "
		+ "                                    ss2.id_sessione_ingest = ss.id_sessione_ingest "
		+ "                                    AND ss2.ts_reg_stato > ss.ts_reg_stato "
		+ "                                ) ) ) ");
	return ((BigDecimal) q.getSingleResult()).longValueExact();
    }

    public String findXmlSacerUnitaDocById(Long xmlId) {
	PigXmlSacerUnitaDoc xmlSacerUnitaDoc = em.find(PigXmlSacerUnitaDoc.class, xmlId);
	return xmlSacerUnitaDoc.getBlXmlSacer();
    }

    public PigSessioneIngest retrieveSessionByObject(PigObject object) {
	Query q = em
		.createQuery("SELECT ses " + "FROM PigObject obj JOIN obj.pigSessioneIngests ses "
			+ "where obj.idObject = :objectId "
			+ "and obj.idLastSessioneIngest = ses.idSessioneIngest");
	q.setParameter("objectId", object.getIdObject());
	return (PigSessioneIngest) q.getSingleResult();
    }

    public void updateLastSessionState(PigObject object, String state) {
	PigSessioneIngest session = this.retrieveSessionByObject(object);
	session.setTiStato(state);
	creaStatoSessione(session, state, new Date());
    }

    public void creaStatoSessione(PigSessioneIngest pigSessioneIngest, String statoSessione,
	    Date dtRegStato) {
	PigStatoSessioneIngest pigStatoSessione = new PigStatoSessioneIngest();
	pigStatoSessione.setPigSessioneIngest(pigSessioneIngest);
	pigStatoSessione.setTiStato(statoSessione);
	pigStatoSessione.setTsRegStato(dtRegStato);
	pigStatoSessione.setIdVers(pigSessioneIngest.getPigVer().getIdVers());
	em.persist(pigStatoSessione);
	pigSessioneIngest.setIdStatoSessioneIngestCor(
		new BigDecimal(pigStatoSessione.getIdStatoSessioneIngest()));
	em.flush();
    }

    public void updateSession(PigSessioneIngest session, Date date, String state) {
	session.setDtChiusura(date);
	session.setTiStato(state);
	creaStatoSessione(session, state, date);
    }

    public Long countUdInObj(PigObject object, String state, String errCode) {
	Query q = em.createQuery(
		"SELECT count(ud) " + "FROM PigObject obj JOIN obj.pigUnitaDocObjects ud "
			+ "WHERE obj.idObject = :objId " + "AND ud.tiStatoUnitaDocObject = :state "
			+ "AND ud.cdErrSacer = :errCode ");
	q.setParameter("objId", object.getIdObject());
	q.setParameter("state", state);
	q.setParameter("errCode", errCode);
	return (Long) q.getSingleResult();
    }

    public void incrementaVersateOk(PigSessioneIngest session) {
	// MAC#26848 - Oggetti in stato IN_CODA_VERS
	session.setNiUnitaDocVersOk(session.getNiUnitaDocVersOk().add(BigDecimal.ONE));
    }

    public void incrementaVersateTimeout(PigSessioneIngest session) {
	// MAC#26848 - Oggetti in stato IN_CODA_VERS
	session.setNiUnitaDocVersTimeout(session.getNiUnitaDocVersTimeout().add(BigDecimal.ONE));
    }

    public void incrementaVersateErr(PigSessioneIngest session) {
	// MAC#26848 - Oggetti in stato IN_CODA_VERS
	session.setNiUnitaDocVersErr(session.getNiUnitaDocVersErr().add(BigDecimal.ONE));
    }

    public void incrementaVersate(PigSessioneIngest session) {
	// MAC#26848 - Oggetti in stato IN_CODA_VERS
	session.setNiUnitaDocVers(session.getNiUnitaDocVers().add(BigDecimal.ONE));
    }

    public PigUnitaDocSessione findPigUnitaDocSessioneById(Long unitaDocSessionId) {
	return em.find(PigUnitaDocSessione.class, unitaDocSessionId);
    }

    public PigUnitaDocSessione findLockPigUnitaDocSessioneById(Long unitaDocSessionId) {
	final LockModeType lockModeType = LockModeType.PESSIMISTIC_WRITE;
	log.debug("Leggo il record PigUnitaDocSessione id={} e faccio lock {}", unitaDocSessionId,
		lockModeType);
	Map<String, Object> properties = new HashMap<>();
	properties.put(JPA_PORPERTIES_TIMEOUT, 25000);
	/*
	 * Attenzione, Oracle usa multi version control quindi il LockModeType.PESSIMISTIC_WRITE
	 * impedisce scritture concorrenti ma ammette che qualcun altro legga questo record ma senza
	 * gli eventuali update fatti in quest sessione
	 */
	return em.find(PigUnitaDocSessione.class, unitaDocSessionId, lockModeType, properties);
    }

    public PigUnitaDocSessione retrievePigUnitaDocSessioneByKeyUD(BigDecimal idSessioneIngest,
	    BigDecimal aaUnitaDocSacer, String cdKeyUnitaDocSacer, String cdRegistroUnitaDocSacer) {
	Query q = em.createQuery("SELECT udSes " + "FROM PigUnitaDocSessione udSes "
		+ "WHERE udSes.pigSessioneIngest.idSessioneIngest = :idSessione "
		+ "AND udSes.aaUnitaDocSacer = :aaUd " + "AND udSes.cdKeyUnitaDocSacer = :keyUd "
		+ "AND udSes.cdRegistroUnitaDocSacer = :regUd");
	q.setParameter("idSessione", HibernateUtils.longFrom(idSessioneIngest));
	q.setParameter("aaUd", aaUnitaDocSacer);
	q.setParameter("keyUd", cdKeyUnitaDocSacer);
	q.setParameter("regUd", cdRegistroUnitaDocSacer);
	return (PigUnitaDocSessione) q.getSingleResult();
    }

    public Long checkConsumed(String messageSelector, BigDecimal paramToCheck) {
	Long msgConsumed = null;
	if (Constants.TipoSelettore.CODA1.name().equals(messageSelector)
		|| Constants.TipoSelettore.CODA2.name().equals(messageSelector)
		|| Constants.TipoSelettore.CODA3.name().equals(messageSelector)) {
	    Query q = em.createQuery("SELECT count(udSess) " + "FROM PigUnitaDocSessione udSess "
		    + "WHERE udSess.idUnitaDocSessione = :idUnitaDocSessione "
		    + "AND (udSess.tiStatoUnitaDocSessione = :versataErr "
		    + "OR udSess.tiStatoUnitaDocSessione = :versataOk "
		    + "OR udSess.tiStatoUnitaDocSessione = :versataTimeout)");
	    q.setParameter("idUnitaDocSessione", paramToCheck);
	    q.setParameter("versataErr", StatoUnitaDocSessione.VERSATA_ERR.name());
	    q.setParameter("versataOk", StatoUnitaDocSessione.VERSATA_OK.name());
	    q.setParameter("versataTimeout", StatoUnitaDocSessione.VERSATA_TIMEOUT.name());
	    msgConsumed = (Long) q.getSingleResult();
	} else if (Constants.TipoSelettore.CODA_VER_HASH.name().equals(messageSelector)) {
	    Query q = em.createQuery("SELECT count(obj) "
		    + "FROM PigObject obj JOIN obj.pigSessioneIngests ses "
		    + "WHERE obj.idObject = :objId "
		    + "AND ses.idSessioneIngest = obj.idLastSessioneIngest "
		    + "AND (ses.tiStatoVerificaHash = :statoKo OR ses.tiStatoVerificaHash = :statoOk)");
	    q.setParameter("objId", paramToCheck);
	    q.setParameter("statoKo", StatoVerificaHash.KO.name());
	    q.setParameter("statoOk", StatoVerificaHash.OK.name());
	    msgConsumed = (Long) q.getSingleResult();
	}
	return msgConsumed;
    }

    public PigSessioneIngest findPigSessioneIngestById(Long idSessione) {
	return em.find(PigSessioneIngest.class, idSessione);
    }

    /**
     * Calcola lo stato che l'oggetto padre dovrà assumere come da analisi (punto 2.11.2)
     *
     * @param idObjectPadre id padre
     *
     * @return valore calcolo
     */
    public String getCalcoloStatoObjDaTrasf(Long idObjectPadre) {
	MonVCalcStatoObjDaTrasf obj = em.find(MonVCalcStatoObjDaTrasf.class,
		new BigDecimal(idObjectPadre));
	return (obj == null ? null : obj.getTiStatoObjectPadre());
    }

    /**
     * Effettua l'aggiornamento dei contatori in un'operazione singola per demandare al database la
     * gestione della concorrenza. Aggiorna sempre {@link PigSessioneIngest#niUnitaDocVers} ed
     * eventuali altri contatori in base ai parametri che vengono passati.
     *
     * @param idSessioneIngest         primary key di {@link PigSessioneIngest}
     * @param incrementaVersateOk      se true indica di aggiornare
     *                                 {@link PigSessioneIngest#niUnitaDocVersOk}
     * @param incrementaVersateErrore  se true indica di aggiornare
     *                                 {@link PigSessioneIngest#niUnitaDocVersErr}
     * @param incrementaVersateTimeout se true indica di aggiornare
     *                                 {@link PigSessioneIngest#niUnitaDocVersTimeout}
     *
     * @return {@link PigSessioneIngest} aggiornato
     */
    public PigSessioneIngest aggiornaContatori(long idSessioneIngest, boolean incrementaVersateOk,
	    boolean incrementaVersateErrore, boolean incrementaVersateTimeout) {

	StoredProcedureQuery query = this.em.createNamedStoredProcedureQuery("aggiornaContatori");
	query.setParameter("IDSESSIONEINGEST", idSessioneIngest);
	query.setParameter("INCREMENTAVERSATEOK", incrementaVersateOk ? 1 : 0);
	query.setParameter("INCREMENTAVERSATEERRORE", incrementaVersateErrore ? 1 : 0);
	query.setParameter("INCREMENTAVERSATETIMEOUT", incrementaVersateTimeout ? 1 : 0);
	query.execute();
	// recupero il dato aggiornat
	PigSessioneIngest session = em.find(PigSessioneIngest.class, idSessioneIngest);
	log.debug("Prima del refresh PigSessioneIngest id={} niUnitaDocVers={}",
		session.getIdSessioneIngest(), session.getNiUnitaDocVers());
	em.refresh(session);
	log.debug("Dopo refresh PigSessioneIngest id={} niUnitaDocVers={}",
		session.getIdSessioneIngest(), session.getNiUnitaDocVers());
	if (session.getNiUnitaDocVersTimeout() == null) {
	    session.setNiUnitaDocVersTimeout(BigDecimal.ZERO);
	}
	if (session.getNiUnitaDocVersOk() == null) {
	    session.setNiUnitaDocVersOk(BigDecimal.ZERO);
	}
	if (session.getNiUnitaDocVersErr() == null) {
	    session.setNiUnitaDocVersErr(BigDecimal.ZERO);
	}
	return session;
    }

    public void updatePrioritaOggetto(Long idObject, String priorita, String username) {
	log.debug("Aggiorno la priorita dell'oggetto {} a {}", idObject, priorita);
	PigObject object = em.find(PigObject.class, idObject);
	object.impostaPrioritaVersamento(priorita, username);
    }
}
