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

import it.eng.integriam.server.ws.reputente.ListaIndIp;
import it.eng.integriam.server.ws.reputente.ListaOrganizAbil;
import it.eng.integriam.server.ws.reputente.ListaServiziAutor;
import it.eng.integriam.server.ws.reputente.ListaTipiDatoAbil;
import it.eng.integriam.server.ws.reputente.OrganizAbil;
import it.eng.integriam.server.ws.reputente.TipoDatoAbil;
import it.eng.integriam.server.ws.reputente.Utente;
import it.eng.sacerasi.entity.IamAbilOrganiz;
import it.eng.sacerasi.entity.IamAbilTipoDato;
import it.eng.sacerasi.entity.IamAutorServ;
import it.eng.sacerasi.entity.IamIndIpUser;
import it.eng.sacerasi.entity.IamUser;
import it.eng.sacerasi.exception.ParerErrorSeverity;
import it.eng.sacerasi.exception.ParerInternalError;
import it.eng.sacerasi.messages.MessaggiWSBundle;
import it.eng.sacerasi.web.dto.PairAbil;
import it.eng.sacerasi.ws.dto.IRispostaWS;
import it.eng.sacerasi.ws.replicaUtente.dto.ModificaUtenteExt;
import it.eng.sacerasi.ws.replicaUtente.dto.RispostaWSModificaUtente;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.ejb.EJB;
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
@Stateless(mappedName = "ModificaUtenteEjb")
@LocalBean
public class ModificaUtenteEjb {

    private static final Logger log = LoggerFactory.getLogger(ModificaUtenteEjb.class);
    @PersistenceContext(unitName = "SacerAsiJPA")
    private EntityManager entityManager;
    @EJB
    private ModificaUtenteEjb muHelper;

    public IamUser getIamUser(long idUserIam) {
	return entityManager.getReference(IamUser.class, idUserIam);
    }

    /**
     * Modifica utente secondo il nuovo algoritmo di analisi
     *
     * @param muExt      dto servizio di modifica utente {@link ModificaUtenteExt}
     * @param rispostaWs risposta ws
     *
     * @throws ParerInternalError errore generico
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void update2IamUser(ModificaUtenteExt muExt, RispostaWSModificaUtente rispostaWs)
	    throws ParerInternalError {
	Utente utente = muExt.getModificaUtenteInput();
	log.debug("Ricevuta chiamata update per l'utente {}", utente.getNmUserid());
	try {
	    eseguiModificaUtente(utente);
	} catch (Exception ex) {
	    log.error(ExceptionUtils.getRootCauseMessage(ex), ex);
	    rispostaWs.setSeverity(IRispostaWS.SeverityEnum.ERROR);
	    rispostaWs.setErrorCode(MessaggiWSBundle.SERVIZI_USR_001);
	    rispostaWs.setErrorMessage(
		    "Errore nel salvataggio dell'utente " + ExceptionUtils.getRootCauseMessage(ex));
	    throw new ParerInternalError(ParerErrorSeverity.ERROR,
		    "Errore nel salvataggio dell'utente " + ExceptionUtils.getRootCauseMessage(ex),
		    ex);
	}

    }

    @SuppressWarnings("rawtypes")
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void eseguiModificaUtente(Utente utente) {
	/* Ricavo l'utente da modificare */
	IamUser iamUser = muHelper.getIamUser(utente.getIdUserIam());
	log.debug("Utente {} recuperato da DB", utente.getNmUserid());

	/* Lo modifico */
	iamUser.setNmUserid(utente.getNmUserid());
	iamUser.setCdPsw(utente.getCdPsw());
	iamUser.setCdSalt(utente.getCdSalt());
	iamUser.setNmCognomeUser(utente.getNmCognomeUser());
	iamUser.setNmNomeUser(utente.getNmNomeUser());
	iamUser.setFlAttivo(utente.getFlAttivo());
	iamUser.setDtRegPsw(utente.getDtRegPsw());
	iamUser.setDtScadPsw(utente.getDtScadPsw());
	iamUser.setCdFisc(utente.getCdFisc());
	iamUser.setDsEmail(utente.getDsEmail());
	iamUser.setFlUserAdmin(utente.getFlUserAdmin());
	iamUser.setFlContrIp(utente.getFlContrIp());
	iamUser.setTipoUser(utente.getTipoUser());
	iamUser.setTipoAuth(utente.getTipoAuth());

	/* GESTIONE INDIRIZZI IP */
	/* Elimino i vecchi ip */
	entityManager.createNamedQuery("IamIndIpUser.deleteByIdUser")
		.setParameter("iamUser", iamUser).executeUpdate();
	log.debug("Eseguita bulk delete su IamIndIpUser per l'utente {}", utente.getNmUserid());
	/* Creo i nuovi IP */
	ListaIndIp listaIndIp = utente.getListaIndIp();
	if (listaIndIp != null && listaIndIp.getIndIp() != null) {
	    log.debug("Necessario persistere {} record di IamIndIpUser per l'utente {}",
		    listaIndIp.getIndIp().size(), utente.getNmUserid());
	    for (String ip : listaIndIp.getIndIp()) {
		IamIndIpUser iamIp = new IamIndIpUser();
		iamIp.setIamUser(iamUser);
		iamIp.setCdIndIpUser(ip);
		iamUser.getIamIndIpUsers().add(iamIp);
	    }
	    log.debug("Eseguita persist di nuove IamIndIpUser per l'utente {}",
		    utente.getNmUserid());
	}

	/* GESTIONE ABILITAZIONI: ORGANIZZAZIONI, TIPI DATO, SERVIZI */
	log.debug("Recupero la lista delle abilitazioni sulle organizzazioni per l'utente {}",
		utente.getNmUserid());
	/* Il sistema legge tutte le abilitazioni alle organizzazioni dell?utente */
	List<IamAbilOrganiz> listaAbilOrganizDB = getListaAbilOrganizDB(utente.getIdUserIam());
	log.debug("Recuperate {} organizzazioni per l'utente {}", listaAbilOrganizDB.size(),
		utente.getNmUserid());

	// Ricavo la lista delle abilitazioni alle organizzazioni passate al servizio
	ListaOrganizAbil listaOrganizAbil = utente.getListaOrganizAbil();
	// Memorizzo gli idOrganizApplic con relativo flag organiz default passati al servizio
	// in modo tale che sia agevole rintracciarli
	Map<Long, String> idOrganizApplicFlagMap = new HashMap<>();
	if (listaOrganizAbil != null && listaOrganizAbil.getOrganizAbilList() != null) {
	    for (OrganizAbil organizAbil : listaOrganizAbil) {
		idOrganizApplicFlagMap.put(organizAbil.getIdOrganizApplicAbil().longValue(),
			organizAbil.isFlOrganizDefault() ? "1" : "0");
	    }
	}
	// Tengo traccia degli eventuali iamAbilOrganiz da rimuovere
	Set<Long> idAbilOrganizToRemove = new HashSet<>();
	// Tengo traccia degli eventuali iamAbilTipoDato da rimuovere
	Set<Long> idAbilTipoDatoToRemove = new HashSet<>();
	// Tengo traccia degli eventuali iamAbilTipoDato da rimuovere
	Set<Long> idAutorServToRemove = new HashSet<>();

	log.debug(
		"Inizio il confronto tra le abilitazioni memorizzate su DB con quelle passate al WS per l'utente {}",
		utente.getNmUserid());
	/////////////////////////////////////////////
	// Per ogni elemento di ListaAbilOrganizDB //
	/////////////////////////////////////////////
	for (IamAbilOrganiz abilOrganizDB : listaAbilOrganizDB) {
	    long idOrganizApplicDB = abilOrganizDB.getIdOrganizApplic().longValue();
	    OrganizAbil organizAbil = null;
	    if (listaOrganizAbil != null && listaOrganizAbil.getOrganizAbilList() != null) {
		for (int i = 0; i < listaOrganizAbil.getOrganizAbilList().size(); i++) {
		    if (idOrganizApplicDB == listaOrganizAbil.getOrganizAbilList().get(i)
			    .getIdOrganizApplicAbil()) {
			organizAbil = listaOrganizAbil.getOrganizAbilList().get(i);
			break;
		    }
		}
	    }
	    // a) Se ListaAbilOrganizDB.id_organiz_applic esiste in ListaOrganizAbil in input
	    if (organizAbil != null) {
		/*
		 * i) Se fl_organiz_default in ListaAbilOrganizDB.id_organiz_applic e' diverso da
		 * FlOrganizDefault in ListaOrganizAbil in input
		 */
		String flOrganizDefaultDB = abilOrganizDB.getFlOrganizDefault();
		String flOrganizDefault = organizAbil.isFlOrganizDefault() ? "1" : "0";

		if (!flOrganizDefaultDB.equals(flOrganizDefault)) {
		    // Modifica record IAM_ABIL_ORGANIZ, aggiornando fl_organiz_default
		    abilOrganizDB.setFlOrganizDefault(flOrganizDefault);
		}

		/*
		 * Ricavo le coppie nmClasseTipoDato e idTipoDatoApplic dalla ListaTipiDatoAbil
		 * passatami in input relativa all'abilitazione sulle organizzazioni considerata
		 */
		Set<PairAbil> abilTipoDatoSet = new HashSet<>();
		ListaTipiDatoAbil ltda = organizAbil.getListaTipiDatoAbil();
		if (ltda.getTipoDatoAbilList() != null) {
		    for (TipoDatoAbil tda : ltda) {
			abilTipoDatoSet.add(new PairAbil<>(tda.getNmClasseTipoDato(),
				new BigDecimal(tda.getIdTipoDatoApplic())));
		    }
		}

		// ii) Per ogni elemento di ListaAbilTipoDatoDB relativo a
		// ListaAbilOrganizDB.id_abil_organiz
		List<IamAbilTipoDato> listaAbilTipoDatoDB = abilOrganizDB.getIamAbilTipoDatos();

		for (IamAbilTipoDato abilTipoDatoDB : listaAbilTipoDatoDB) {
		    /*
		     * Se la coppia ListaAbilTipoDatoDB.nm_classe_tipo_dato e
		     * ListaAbilTipoDatoDB.id_tipo_dato_applic non esiste in ListaTipiDatoAbil in
		     * input
		     */
		    String nmClasseTipoDatoDB = abilTipoDatoDB.getNmClasseTipoDato();
		    BigDecimal idTipoDatoApplicDB = abilTipoDatoDB.getIdTipoDatoApplic();
		    if (!abilTipoDatoSet
			    .contains(new PairAbil<>(nmClasseTipoDatoDB, idTipoDatoApplicDB))) {
			// Il record va eliminato
			idAbilTipoDatoToRemove.add(abilTipoDatoDB.getIdAbilTipoDato());
		    }
		}

		/*
		 * Ricavo nmServizioWeb dalla ListaAutorServ passatami in input relativo
		 * all'abilitazione sulle organizzazioni considerata
		 */
		Set<String> autorServSet = new HashSet<>();
		ListaServiziAutor lsa = organizAbil.getListaServiziAutor();
		if (lsa.getNmServizioAutor() != null) {
		    autorServSet.addAll(lsa.getNmServizioAutor());
		}

		// Per ogni elemento di ListaAutorServDB relativo a
		// ListaAbilOrganizDB.id_abil_organiz
		List<IamAutorServ> listaAutorServDB = abilOrganizDB.getIamAutorServs();

		for (IamAutorServ autorServDB : listaAutorServDB) {
		    // Se ListaAutorServDB.nm_servizio_web non esiste in ListaServiziAutor in input
		    String nmServizioWeb = autorServDB.getNmServizioWeb();
		    if (!autorServSet.contains(nmServizioWeb)) {
			// Il record va eliminato
			idAutorServToRemove.add(autorServDB.getIdAutorServ());
		    }
		}
	    } else {
		/*
		 * b) Se ListaAbilOrganizDB.id_organiz_applic esiste in ListaOrganizAbil in input i)
		 * Elimino il record da IAM_ABIL_ORGANIZ
		 */
		idAbilOrganizToRemove.add(abilOrganizDB.getIdAbilOrganiz());
	    }
	} // END FOR ListaAbilOrganizDB

	// Elimino fisicamente i record dal DB
	log.info("Elimino i record");
	log.debug(
		"Per evitare di eseguire una clausola IN superiore a 1000 splitto la lista, nel caso sia necessario");
	final int MAX_LIST_SIZE = 999;
	if (!idAbilTipoDatoToRemove.isEmpty()) {
	    log.debug("TIPI DATO");
	    // Per evitare di eseguire una clausola IN superiore a 1000 splitto la lista, nel caso
	    // sia necessario
	    if (idAbilTipoDatoToRemove.size() > MAX_LIST_SIZE) {
		List<Long> list = new ArrayList<>(idAbilTipoDatoToRemove);
		log.debug("Da rimuovere {} abilitazione ai tipi dato", list.size());
		int numeroCicli = (list.size() + (MAX_LIST_SIZE - 1)) / MAX_LIST_SIZE;
		log.debug("Numero di blocchi per rimuovere le abilitazioni: {}", numeroCicli);
		for (int i = 0; i < numeroCicli; i++) {
		    int from = i * MAX_LIST_SIZE;
		    int to = ((i + 1) * MAX_LIST_SIZE) - 1;
		    if (to > list.size()) {
			to = list.size();
		    }
		    log.debug("FROM :{}, TO : {}", from, to);
		    if (from != to && from < list.size()) {
			log.debug("Elimino il blocco {} - {}", from, to);
			List<Long> deleteList = list.subList(from, to);
			muHelper.deleteIamAbilTipoDato(deleteList);
		    }
		}
	    } else {
		muHelper.deleteIamAbilTipoDato(idAbilTipoDatoToRemove);
	    }
	}
	if (!idAutorServToRemove.isEmpty()) {
	    log.debug("SERVIZI");
	    // Per evitare di eseguire una clausola IN superiore a 1000 splitto la lista, nel caso
	    // sia necessario
	    if (idAutorServToRemove.size() > MAX_LIST_SIZE) {
		List<Long> list = new ArrayList<>(idAutorServToRemove);
		log.debug("Da rimuovere {} servizi", list.size());
		int numeroCicli = (list.size() + (MAX_LIST_SIZE - 1)) / MAX_LIST_SIZE;
		log.debug("Numero di blocchi per rimuovere le abilitazione: {}", numeroCicli);
		for (int i = 0; i < numeroCicli; i++) {
		    int from = i * MAX_LIST_SIZE;
		    int to = ((i + 1) * MAX_LIST_SIZE) - 1;
		    if (to > list.size()) {
			to = list.size();
		    }
		    log.debug("FROM :{}, TO : {}", from, to);
		    if (from != to && from < list.size()) {
			log.debug("Elimino il blocco {}-{}", from, to);
			List<Long> deleteList = list.subList(from, to);
			muHelper.deleteIamAutorServ(deleteList);
		    }
		}
	    } else {
		muHelper.deleteIamAutorServ(idAutorServToRemove);
	    }
	}
	if (!idAbilOrganizToRemove.isEmpty()) {
	    log.debug("ORGANIZZAZIONI");
	    // Per evitare di eseguire una clausola IN superiore a 1000 splitto la lista, nel caso
	    // sia necessario
	    if (idAbilOrganizToRemove.size() > MAX_LIST_SIZE) {
		List<Long> list = new ArrayList<>(idAbilOrganizToRemove);
		log.debug("Da rimuovere {} organizzazioni", list.size());
		int numeroCicli = (list.size() + (MAX_LIST_SIZE - 1)) / MAX_LIST_SIZE;
		log.debug("Numero di blocchi per rimuovere le abilitazione: {}", numeroCicli);
		for (int i = 0; i < numeroCicli; i++) {
		    int from = i * MAX_LIST_SIZE;
		    int to = ((i + 1) * MAX_LIST_SIZE) - 1;
		    if (to > list.size()) {
			to = list.size();
		    }
		    log.debug("FROM :{}, TO : {}", from, to);
		    if (from != to && from < list.size()) {
			log.debug("Elimino il blocco {}-{}", from, to);
			List<Long> deleteList = list.subList(from, to);
			muHelper.deleteIamAbilOrganiz(deleteList);
		    }
		}
	    } else {
		muHelper.deleteIamAbilOrganiz(idAbilOrganizToRemove);
	    }
	}

	////////////////////////////////////////////////////
	// Per ogni elemento di ListaOrganizAbil in input //
	////////////////////////////////////////////////////
	if (listaOrganizAbil != null && listaOrganizAbil.getOrganizAbilList() != null) {
	    /* Per ogni elemento di ListaOrganizAbil in input */
	    for (OrganizAbil organizAbil : listaOrganizAbil) {
		IamAbilOrganiz abilOrganizDB = null;
		for (int i = 0; i < listaAbilOrganizDB.size(); i++) {
		    if (organizAbil.getIdOrganizApplicAbil() == listaAbilOrganizDB.get(i)
			    .getIdOrganizApplic().longValue()) {
			abilOrganizDB = listaAbilOrganizDB.get(i);
			break;
		    }
		}
		// Se ListaOrganizAbil.IdOrganizApplicAbil non esiste in ListaAbilOrganizDB
		if (abilOrganizDB == null) {
		    /* Inserisco il record in IAM_ABIL_ORGANIZ */
		    IamAbilOrganiz iamAbilOrganiz = new IamAbilOrganiz();
		    iamAbilOrganiz.setIamAutorServs(new ArrayList<>());
		    iamAbilOrganiz.setIamAbilTipoDatos(new ArrayList<>());
		    iamAbilOrganiz.setIdOrganizApplic(
			    new BigDecimal(organizAbil.getIdOrganizApplicAbil()));
		    iamAbilOrganiz
			    .setFlOrganizDefault(organizAbil.isFlOrganizDefault() ? "1" : "0");

		    /*
		     * Per ogni elemento di ListaTipiDatoAbil in input inserisci record in
		     * IAM_ABIL_TIPO_DATO
		     */
		    ListaTipiDatoAbil ltda = organizAbil.getListaTipiDatoAbil();
		    if (ltda.getTipoDatoAbilList() != null) {
			if (ltda.getTipoDatoAbilList() != null) {
			    log.debug("Necessario persistere {} record di IamAbilTipoDato",
				    ltda.getTipoDatoAbilList().size());
			    for (TipoDatoAbil tipiDatoApplic : ltda.getTipoDatoAbilList()) {
				IamAbilTipoDato iamAbilTipoDato = new IamAbilTipoDato();
				iamAbilTipoDato.setIdTipoDatoApplic(
					new BigDecimal(tipiDatoApplic.getIdTipoDatoApplic()));
				iamAbilTipoDato
					.setNmClasseTipoDato(tipiDatoApplic.getNmClasseTipoDato());
				iamAbilTipoDato.setIamAbilOrganiz(iamAbilOrganiz);
				iamAbilOrganiz.getIamAbilTipoDatos().add(iamAbilTipoDato);
			    }
			    log.debug("Eseguita persist di nuovi IamAbilTipoDato");
			}
		    }

		    /*
		     * Per ogni elemento di ListaServiziAutor in input inserisci record da
		     * IAM_AUTOR_SERV
		     */
		    ListaServiziAutor lsa = organizAbil.getListaServiziAutor();
		    if (lsa.getNmServizioAutor() != null) {
			log.debug("Necessario persistere {} record di servizi autor",
				lsa.getNmServizioAutor().size());
			for (String nmServizioAutor : lsa.getNmServizioAutor()) {
			    IamAutorServ iamAutorServ = new IamAutorServ();
			    iamAutorServ.setIamAbilOrganiz(iamAbilOrganiz);
			    iamAutorServ.setNmServizioWeb(nmServizioAutor);
			    iamAbilOrganiz.getIamAutorServs().add(iamAutorServ);
			}
			log.debug("Eseguita persist di nuovi servizi autor");
		    }

		    iamAbilOrganiz.setIamUser(iamUser);
		    iamUser.getIamAbilOrganizs().add(iamAbilOrganiz);
		    log.debug("Eseguita persist di nuove IamAbilOrganiz");
		} else {

		    /*
		     * Ricavo le coppie nmClasseTipoDato e idTipoDatoApplic dalla
		     * ListaTipiDatoAbilDB relativa all'abilitazione sulle organizzazioni
		     * considerata
		     */
		    Set<PairAbil> abilTipoDatoSetDB = new HashSet<>();
		    List<IamAbilTipoDato> listaAbilTipoDatoDB = abilOrganizDB.getIamAbilTipoDatos();
		    for (IamAbilTipoDato abilTipoDatoDB : listaAbilTipoDatoDB) {
			abilTipoDatoSetDB.add(new PairAbil<>(abilTipoDatoDB.getNmClasseTipoDato(),
				abilTipoDatoDB.getIdTipoDatoApplic()));
		    }

		    /* Per ogni elemento di ListaTipiDatoAbil in input */
		    ListaTipiDatoAbil listaTipiDatoAbil = organizAbil.getListaTipiDatoAbil();
		    if (listaTipiDatoAbil.getTipoDatoAbilList() != null) {
			if (listaTipiDatoAbil.getTipoDatoAbilList() != null) {
			    boolean newTipoDato = false;
			    for (TipoDatoAbil tipiDatoAbil : listaTipiDatoAbil
				    .getTipoDatoAbilList()) {
				/*
				 * Se la coppia ListaAbilTipoDato.nm_classe_tipo_dato e
				 * ListaAbilTipoDato.id_tipo_dato_applic non esiste in
				 * ListaTipiDatoAbilDB
				 */
				String nmClasseTipoDato = tipiDatoAbil.getNmClasseTipoDato();
				BigDecimal idTipoDatoApplic = new BigDecimal(
					tipiDatoAbil.getIdTipoDatoApplic());
				if (!abilTipoDatoSetDB.contains(
					new PairAbil<>(nmClasseTipoDato, idTipoDatoApplic))) {
				    /* Inserisci il record da IAM_ABIL_TIPO_DATO */
				    IamAbilTipoDato iamAbilTipoDato = new IamAbilTipoDato();
				    iamAbilTipoDato.setIdTipoDatoApplic(
					    new BigDecimal(tipiDatoAbil.getIdTipoDatoApplic()));
				    iamAbilTipoDato.setNmClasseTipoDato(
					    tipiDatoAbil.getNmClasseTipoDato());
				    iamAbilTipoDato.setIamAbilOrganiz(abilOrganizDB);
				    abilOrganizDB.getIamAbilTipoDatos().add(iamAbilTipoDato);
				    newTipoDato = true;
				}
			    }
			    if (newTipoDato) {
				log.debug("Eseguita persist di nuovi IamAbilTipoDato");
			    }
			}
		    }

		    /*
		     * Ricavo nmServizioWeb dalla ListaAutorServDB relativo all'abilitazione sulle
		     * organizzazioni considerata
		     */
		    Set<String> autorServSetDB = new HashSet<>();
		    List<IamAutorServ> listaAutorServDB = abilOrganizDB.getIamAutorServs();
		    if (!listaAutorServDB.isEmpty()) {
			for (IamAutorServ autorServDB : listaAutorServDB) {
			    autorServSetDB.add(autorServDB.getNmServizioWeb());
			}
		    }

		    /* Per ogni elemento di ListaServiziAutor in input */
		    ListaServiziAutor lsa = organizAbil.getListaServiziAutor();
		    if (lsa.getNmServizioAutor() != null) {
			boolean newServices = false;
			for (String nmServizioAutor : lsa.getNmServizioAutor()) {
			    if (!autorServSetDB.contains(nmServizioAutor)) {
				IamAutorServ iamAutorServ = new IamAutorServ();
				iamAutorServ.setIamAbilOrganiz(abilOrganizDB);
				iamAutorServ.setNmServizioWeb(nmServizioAutor);
				abilOrganizDB.getIamAutorServs().add(iamAutorServ);
				newServices = true;
			    }
			}
			if (newServices) {
			    log.debug("Eseguita persist di nuovi servizi autor");
			}
		    }
		    abilOrganizDB.setIamUser(iamUser);
		    iamUser.getIamAbilOrganizs().add(abilOrganizDB);
		}
	    }
	}
    }

    @SuppressWarnings("unchecked")
    public List<IamAbilOrganiz> getListaAbilOrganizDB(long idUserIam) {
	String queryStr = "SELECT u FROM IamAbilOrganiz u "
		+ "WHERE u.iamUser.idUserIam = :idUserIam " + "ORDER BY u.idOrganizApplic ";

	Query query = entityManager.createQuery(queryStr);
	query.setParameter("idUserIam", idUserIam);

	return query.getResultList();
    }

    public void deleteIamAbilTipoDato(Collection<Long> idSet) {
	String queryStr = "DELETE FROM IamAbilTipoDato u " + "WHERE u.idAbilTipoDato IN (:idSet) ";
	Query query = entityManager.createQuery(queryStr);
	query.setParameter("idSet", idSet);
	query.executeUpdate();
	entityManager.flush();
    }

    public void deleteIamAutorServ(Collection<Long> idSet) {
	String queryStr = "DELETE FROM IamAutorServ u " + "WHERE u.idAutorServ IN (:idSet) ";
	Query query = entityManager.createQuery(queryStr);
	query.setParameter("idSet", idSet);
	query.executeUpdate();
	entityManager.flush();
    }

    public void deleteIamAbilOrganiz(Collection<Long> idSet) {
	String queryStr = "DELETE FROM IamAbilOrganiz u " + "WHERE u.idAbilOrganiz IN (:idSet) ";
	Query query = entityManager.createQuery(queryStr);
	query.setParameter("idSet", idSet);
	query.executeUpdate();
	entityManager.flush();
    }
}
