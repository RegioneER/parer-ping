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

package it.eng.sacerasi.job.preparaxml.ejb;

import it.eng.paginator.util.HibernateUtils;
import it.eng.sacerasi.common.Constants;
import it.eng.sacerasi.common.Constants.TipiXmlSacer;
import it.eng.sacerasi.entity.PigContUnitaDocSacer;
import it.eng.sacerasi.entity.PigFascicoloObject;
import it.eng.sacerasi.entity.PigFascicoloSessione;
import it.eng.sacerasi.entity.PigObject;
import it.eng.sacerasi.entity.PigSessioneIngest;
import it.eng.sacerasi.entity.PigStatoSessioneIngest;
import it.eng.sacerasi.entity.PigTipoObject;
import it.eng.sacerasi.entity.PigUnitaDocObject;
import it.eng.sacerasi.entity.PigUnitaDocSessione;
import it.eng.sacerasi.entity.PigXmlSacerFascicolo;
import it.eng.sacerasi.entity.PigXmlSacerFascicoloSes;
import it.eng.sacerasi.entity.PigXmlSacerUnitaDoc;
import it.eng.sacerasi.entity.PigXmlSacerUnitaDocSes;
import it.eng.sacerasi.job.preparaxml.dto.FascicoloDocObject;
import it.eng.sacerasi.job.preparaxml.dto.OggettoInCoda;
import it.eng.sacerasi.job.preparaxml.dto.UnitaDocObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 * @author Fioravanti_F
 */
@SuppressWarnings("unchecked")
@Stateless(mappedName = "SalvataggioPrepXml")
@LocalBean
@Interceptors({
	it.eng.sacerasi.aop.TransactionInterceptor.class })
public class SalvataggioPrepXml {
    @PersistenceContext(unitName = "SacerAsiJPA")
    private EntityManager entityManager;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void salvaTutto(OggettoInCoda oggetto) {
	PigTipoObject pTipoObj;

	PigObject tmpPigObject = entityManager.find(PigObject.class,
		oggetto.getRifPigObject().getIdObject());
	pTipoObj = tmpPigObject.getPigTipoObject();

	// MEV 32983
	Constants.TipoContenutoTipoOggetto tipoContenutoTipoObject = Constants.TipoContenutoTipoOggetto
		.valueOf(pTipoObj.getTiContenuto());

	if (tipoContenutoTipoObject.equals(Constants.TipoContenutoTipoOggetto.FASCICOLO)) {
	    salvaFascicoli(oggetto, tmpPigObject);
	} else if (tipoContenutoTipoObject.equals(Constants.TipoContenutoTipoOggetto.UD)) {
	    salvaUnitaDocumentarie(oggetto, tmpPigObject);
	}
    }

    // MEV 32983
    private void salvaFascicoli(OggettoInCoda oggetto, PigObject pigObject) {
	PigFascicoloObject tmpPigFascicoloObject;
	PigXmlSacerFascicolo tmpXmlSacerFascicolo;

	final Long idVers = pigObject.getPigVer().getIdVers();

	// recupero della sessione corrente
	PigSessioneIngest tmpSessioneIngest = entityManager.find(PigSessioneIngest.class,
		pigObject.getIdLastSessioneIngest().longValue());

	long numFascicoliDaVersare = 0;
	// salvataggio delle unità doc generate dall'oggetto
	for (FascicoloDocObject tmpFascicoloObject : oggetto.getListaFascicoloDocObject()) {
	    String queryStr = "select f from PigFascicoloObject f "
		    + "where f.pigObject = :pigObject "
		    + "and f.aaFascicoloSacer = :aaFascicoloSacer "
		    + "and f.cdKeyFascicoloSacer = :cdKeyFascicoloSacer ";
	    Query query = entityManager.createQuery(queryStr);

	    query.setParameter("pigObject", pigObject);
	    query.setParameter("aaFascicoloSacer",
		    HibernateUtils.bigDecimalFrom(tmpFascicoloObject.getChiaveUd().getAnno()));
	    query.setParameter("cdKeyFascicoloSacer", tmpFascicoloObject.getChiaveUd().getNumero());
	    List<PigFascicoloObject> lstObjects = query.getResultList();

	    if (lstObjects != null && !lstObjects.isEmpty()) {
		// se PigUnitaDocObject esiste la carico
		tmpPigFascicoloObject = lstObjects.get(0);
	    } else {
		// altrimenti la creo
		tmpPigFascicoloObject = new PigFascicoloObject();
		tmpPigFascicoloObject.setPigObject(pigObject);
		tmpPigFascicoloObject.setAaFascicoloSacer(
			new BigDecimal(tmpFascicoloObject.getChiaveUd().getAnno()));
		tmpPigFascicoloObject
			.setCdKeyFascicoloSacer(tmpFascicoloObject.getChiaveUd().getNumero());
		tmpPigFascicoloObject.setPigXmlSacerFascicolos(new ArrayList<>());
		tmpPigFascicoloObject.setIdVers(idVers);

		pigObject.getPigFascicoloObjects().add(tmpPigFascicoloObject);
	    }

	    if (tmpPigFascicoloObject.getTiStatoFascicoloObject() == null
		    || tmpPigFascicoloObject.getTiStatoFascicoloObject().isEmpty()
		    || tmpPigFascicoloObject.getTiStatoFascicoloObject()
			    .equals(Constants.StatoUnitaDocObject.VERSATA_ERR.name())
		    || tmpPigFascicoloObject.getTiStatoFascicoloObject()
			    .equals(Constants.StatoUnitaDocObject.PREPARA_XML_IN_ERRORE.name())
		    || tmpPigFascicoloObject.getTiStatoFascicoloObject()
			    .equals(Constants.StatoUnitaDocObject.PREPARA_XML_OK.name())
		    || tmpPigFascicoloObject.getTiStatoFascicoloObject()
			    .equals(Constants.StatoUnitaDocObject.ANNULLATA.name())) {

		// Se PigUnitaDocObject esiste ed è in stato VERSATA_ERR o PREPARA_XML_IN_ERRORE o
		// PREPARA_XML_OK o
		// ANNULLATA
		// oppure non esiste e l'ho appena creata...
		//
		numFascicoliDaVersare++;
		//
		tmpPigFascicoloObject
			.setNiSizeFileByte(new BigDecimal(tmpFascicoloObject.getSizeInByte()));
		tmpPigFascicoloObject
			.setTiStatoFascicoloObject(Constants.StatoUnitaDocObject.DA_VERSARE.name());
		tmpPigFascicoloObject.setCdErrSacer(null);
		tmpPigFascicoloObject.setDlErrSacer(null);
		tmpPigFascicoloObject.setIdOrganizIam(tmpFascicoloObject.getIdOrganizSacer());
		tmpPigFascicoloObject
			.setFlVersSimulato(tmpFascicoloObject.isSimulaVersamento() ? "1" : "0");
		tmpPigFascicoloObject.setCdVerWsSacer(tmpFascicoloObject.getVersioneWsVersamento());

		// gestione XML
		// aggiunta o sostituzione dell'XML_INDICE
		queryStr = "select f from PigXmlSacerFascicolo f "
			+ "where f.pigFascicoloObject = :pigFascicoloObject "
			+ "and f.tiXmlSacer = :tiXmlSacer ";
		query = entityManager.createQuery(queryStr);
		query.setParameter("pigFascicoloObject", tmpPigFascicoloObject);
		query.setParameter("tiXmlSacer", Constants.TipiXmlSacer.XML_VERS.name());
		List<PigXmlSacerFascicolo> lstXml = query.getResultList();
		if (lstXml != null && !lstXml.isEmpty()) {
		    tmpXmlSacerFascicolo = lstXml.get(0);
		} else {
		    tmpXmlSacerFascicolo = new PigXmlSacerFascicolo();
		    tmpXmlSacerFascicolo.setPigFascicoloObject(tmpPigFascicoloObject);
		    tmpXmlSacerFascicolo.setTiXmlSacer(Constants.TipiXmlSacer.XML_VERS.name());
		    tmpXmlSacerFascicolo.setIdVers(idVers);
		    //
		    tmpPigFascicoloObject.getPigXmlSacerFascicolos().add(tmpXmlSacerFascicolo);
		}

		tmpXmlSacerFascicolo.setBlXmlSacer(tmpFascicoloObject.getDocumentoXml());

		// MEV 31639
		tmpXmlSacerFascicolo.setFlXmlMod(Constants.DB_FALSE);
	    }

	    // inserimento record in pigUnitaDocSessione
	    PigFascicoloSessione tmpPigFascicolocSessione = new PigFascicoloSessione();
	    tmpPigFascicolocSessione.setPigSessioneIngest(tmpSessioneIngest);
	    tmpPigFascicolocSessione.setIdVers(idVers);
	    tmpPigFascicolocSessione.setAaFascicoloSacer(
		    new BigDecimal(tmpFascicoloObject.getChiaveUd().getAnno()));
	    tmpPigFascicolocSessione
		    .setCdKeyFascicoloSacer(tmpFascicoloObject.getChiaveUd().getNumero());
	    tmpPigFascicolocSessione
		    .setNiSizeFileByte(new BigDecimal(tmpFascicoloObject.getSizeInByte()));
	    tmpPigFascicolocSessione
		    .setTiStatoFascicoloSessione(tmpPigFascicoloObject.getTiStatoFascicoloObject());
	    tmpPigFascicolocSessione.setCdErrSacer(null);
	    tmpPigFascicolocSessione.setDlErrSacer(null);
	    tmpPigFascicolocSessione.setIdOrganizIam(tmpPigFascicoloObject.getIdOrganizIam());
	    tmpPigFascicolocSessione.setFlVersSimulato(tmpPigFascicoloObject.getFlVersSimulato());
	    tmpPigFascicolocSessione.setCdVerWsSacer(tmpFascicoloObject.getVersioneWsVersamento());

	    if (tmpPigFascicolocSessione.getPigXmlSacerFascicolosSes() == null) {
		tmpPigFascicolocSessione.setPigXmlSacerFascicolosSes(new ArrayList<>());
	    }

	    PigXmlSacerFascicoloSes xmlFascicoloSes = new PigXmlSacerFascicoloSes();
	    xmlFascicoloSes.setIdVers(idVers);
	    xmlFascicoloSes.setTiXmlSacer(TipiXmlSacer.XML_VERS.name());
	    xmlFascicoloSes.setBlXmlSacer(tmpFascicoloObject.getDocumentoXml());
	    //
	    tmpPigFascicolocSessione.addPigXmlSacerFascicoloSes(xmlFascicoloSes);
	    //
	    tmpSessioneIngest.getPigFascicoloSessiones().add(tmpPigFascicolocSessione);
	}

	Date now = Calendar.getInstance().getTime();
	if (numFascicoliDaVersare > 0) {
	    tmpSessioneIngest.setTiStato(Constants.StatoSessioneIngest.IN_ATTESA_VERS.name());
	    pigObject.setTiStatoObject(Constants.StatoOggetto.IN_ATTESA_VERS.name());
	    // aggiornamento dei dati della sessione corrente
	    tmpSessioneIngest.setNiFascicoliDaVers(new BigDecimal(numFascicoliDaVersare));
	    tmpSessioneIngest.setNiFascicoliVers(BigDecimal.ZERO);
	} else {
	    tmpSessioneIngest.setDtChiusura(now);
	    tmpSessioneIngest.setTiStato(Constants.StatoSessioneIngest.CHIUSO_OK.name());
	    pigObject.setTiStatoObject(Constants.StatoOggetto.CHIUSO_OK.name());
	}

	PigStatoSessioneIngest pigStatoSessione = new PigStatoSessioneIngest();
	pigStatoSessione.setPigSessioneIngest(tmpSessioneIngest);
	pigStatoSessione.setIdVers(idVers);
	pigStatoSessione.setTiStato(tmpSessioneIngest.getTiStato());
	pigStatoSessione.setTsRegStato(now);
	entityManager.persist(pigStatoSessione);
	tmpSessioneIngest.setIdStatoSessioneIngestCor(
		new BigDecimal(pigStatoSessione.getIdStatoSessioneIngest()));

	oggetto.setRifPigObject(pigObject);
	entityManager.flush();
    }

    private void salvaUnitaDocumentarie(OggettoInCoda oggetto, PigObject pigObject) {
	PigContUnitaDocSacer tmpContUnitaDocSacer;
	PigSessioneIngest tmpSessioneIngest;
	List<PigUnitaDocObject> lstObjects;
	PigUnitaDocObject tmpPigUnitaDocObject;
	PigUnitaDocSessione tmpPigUnitaDocSessione;
	PigXmlSacerUnitaDoc tmpXmlSacerUnitaDoc;
	List<PigXmlSacerUnitaDoc> lstXml;

	PigTipoObject pTipoObj = pigObject.getPigTipoObject();
	final Long idVers = pigObject.getPigVer().getIdVers();

	// creazione o aggiornamento del contatore delle unità documentarie (per i file immagine
	// DICOM)
	OggettoInCoda.ContUnitaDocSacer tmpContUnitaDoc;
	tmpContUnitaDoc = oggetto.getValoreContUnitaDocSacer();
	if (tmpContUnitaDoc != null) {
	    if (tmpContUnitaDoc.getRifIdPigContUnitaDocSacer() != 0) {
		tmpContUnitaDocSacer = entityManager.find(PigContUnitaDocSacer.class,
			tmpContUnitaDoc.getRifIdPigContUnitaDocSacer());
		tmpContUnitaDocSacer.setPgContUnitaDocSacer(tmpContUnitaDoc.getValore());
	    } else {
		tmpContUnitaDocSacer = new PigContUnitaDocSacer();
		tmpContUnitaDocSacer.setPigTipoObject(pTipoObj);
		tmpContUnitaDocSacer.setAaUnitaDocSacer(tmpContUnitaDoc.getAnnoUnitaDocSacer());
		tmpContUnitaDocSacer.setPgContUnitaDocSacer(tmpContUnitaDoc.getValore());
		pTipoObj.getPigContUnitaDocSacers().add(tmpContUnitaDocSacer);
	    }
	}

	// recupero della sessione corrente
	tmpSessioneIngest = entityManager.find(PigSessioneIngest.class,
		pigObject.getIdLastSessioneIngest().longValue());

	long numUnitaDocDaVersare = 0;
	// salvataggio delle unità doc generate dall'oggetto
	for (UnitaDocObject tmpUnitaDocObject : oggetto.getListaUnitaDocObject()) {
	    String queryStr = "select u from PigUnitaDocObject u "
		    + "where u.pigObject = :pigObject "
		    + "and u.aaUnitaDocSacer = :aaUnitaDocSacer "
		    + "and u.cdKeyUnitaDocSacer = :cdKeyUnitaDocSacer "
		    + "and u.cdRegistroUnitaDocSacer = :cdRegistroUnitaDocSacer ";
	    Query query = entityManager.createQuery(queryStr);

	    query.setParameter("pigObject", pigObject);
	    query.setParameter("aaUnitaDocSacer",
		    HibernateUtils.bigDecimalFrom(tmpUnitaDocObject.getChiaveUd().getAnno()));
	    query.setParameter("cdKeyUnitaDocSacer", tmpUnitaDocObject.getChiaveUd().getNumero());
	    query.setParameter("cdRegistroUnitaDocSacer",
		    tmpUnitaDocObject.getChiaveUd().getRegistro());
	    lstObjects = query.getResultList();

	    if (lstObjects != null && !lstObjects.isEmpty()) {
		// se PigUnitaDocObject esiste la carico
		tmpPigUnitaDocObject = lstObjects.get(0);
	    } else {
		// altrimenti la creo
		tmpPigUnitaDocObject = new PigUnitaDocObject();
		tmpPigUnitaDocObject.setPigObject(pigObject);
		tmpPigUnitaDocObject.setAaUnitaDocSacer(
			new BigDecimal(tmpUnitaDocObject.getChiaveUd().getAnno()));
		tmpPigUnitaDocObject
			.setCdKeyUnitaDocSacer(tmpUnitaDocObject.getChiaveUd().getNumero());
		tmpPigUnitaDocObject
			.setCdRegistroUnitaDocSacer(tmpUnitaDocObject.getChiaveUd().getRegistro());
		tmpPigUnitaDocObject.setPigXmlSacerUnitaDocs(new ArrayList<>());
		tmpPigUnitaDocObject.setIdVers(idVers);
		//
		pigObject.getPigUnitaDocObjects().add(tmpPigUnitaDocObject);
	    }

	    if (tmpPigUnitaDocObject.getTiStatoUnitaDocObject() == null
		    || tmpPigUnitaDocObject.getTiStatoUnitaDocObject().isEmpty()
		    || tmpPigUnitaDocObject.getTiStatoUnitaDocObject()
			    .equals(Constants.StatoUnitaDocObject.VERSATA_ERR.name())
		    || tmpPigUnitaDocObject.getTiStatoUnitaDocObject()
			    .equals(Constants.StatoUnitaDocObject.PREPARA_XML_IN_ERRORE.name())
		    || tmpPigUnitaDocObject.getTiStatoUnitaDocObject()
			    .equals(Constants.StatoUnitaDocObject.PREPARA_XML_OK.name())
		    || tmpPigUnitaDocObject.getTiStatoUnitaDocObject()
			    .equals(Constants.StatoUnitaDocObject.ANNULLATA.name())) {
		// Se PigUnitaDocObject esiste ed è in stato VERSATA_ERR o PREPARA_XML_IN_ERRORE o
		// PREPARA_XML_OK o
		// ANNULLATA
		// oppure non esiste e l'ho appena creata...
		//
		numUnitaDocDaVersare++;
		//
		tmpPigUnitaDocObject
			.setNiSizeFileByte(new BigDecimal(tmpUnitaDocObject.getSizeInByte()));
		tmpPigUnitaDocObject
			.setTiStatoUnitaDocObject(Constants.StatoUnitaDocObject.DA_VERSARE.name());
		// MEV 27407
		tmpPigUnitaDocObject.setDtStato(new Date());
		tmpPigUnitaDocObject.setCdErrSacer(null);
		tmpPigUnitaDocObject.setDlErrSacer(null);
		tmpPigUnitaDocObject.setIdOrganizIam(tmpUnitaDocObject.getIdOrganizSacer());
		tmpPigUnitaDocObject
			.setFlVersSimulato(tmpUnitaDocObject.isSimulaVersamento() ? "1" : "0");
		tmpPigUnitaDocObject.setCdVerWsSacer(tmpUnitaDocObject.getVersioneWsVersamento());
		// gestione XML
		// aggiunta o sostituzione dell'XML_INDICE
		queryStr = "select u from PigXmlSacerUnitaDoc u "
			+ "where u.pigUnitaDocObject = :pigUnitaDocObject "
			+ "and u.tiXmlSacer = :tiXmlSacer ";
		query = entityManager.createQuery(queryStr);
		query.setParameter("pigUnitaDocObject", tmpPigUnitaDocObject);
		query.setParameter("tiXmlSacer", Constants.TipiXmlSacer.XML_INDICE.name());
		lstXml = query.getResultList();
		if (lstXml != null && !lstXml.isEmpty()) {
		    tmpXmlSacerUnitaDoc = lstXml.get(0);
		} else {
		    tmpXmlSacerUnitaDoc = new PigXmlSacerUnitaDoc();
		    tmpXmlSacerUnitaDoc.setPigUnitaDocObject(tmpPigUnitaDocObject);
		    tmpXmlSacerUnitaDoc.setTiXmlSacer(Constants.TipiXmlSacer.XML_INDICE.name());
		    tmpXmlSacerUnitaDoc.setIdVers(idVers);
		    //
		    tmpPigUnitaDocObject.getPigXmlSacerUnitaDocs().add(tmpXmlSacerUnitaDoc);
		}
		tmpXmlSacerUnitaDoc.setBlXmlSacer(tmpUnitaDocObject.getIndiceMMXml());
		// MEV 31639
		tmpXmlSacerUnitaDoc.setFlXmlMod(Constants.DB_FALSE);

		// aggiunta o sostituzione dell'XML_VERS
		queryStr = "select u from PigXmlSacerUnitaDoc u "
			+ "where u.pigUnitaDocObject = :pigUnitaDocObject "
			+ "and u.tiXmlSacer = :tiXmlSacer ";
		query = entityManager.createQuery(queryStr);
		query.setParameter("pigUnitaDocObject", tmpPigUnitaDocObject);
		query.setParameter("tiXmlSacer", Constants.TipiXmlSacer.XML_VERS.name());
		lstXml = query.getResultList();
		if (lstXml != null && !lstXml.isEmpty()) {
		    tmpXmlSacerUnitaDoc = lstXml.get(0);
		} else {
		    tmpXmlSacerUnitaDoc = new PigXmlSacerUnitaDoc();
		    tmpXmlSacerUnitaDoc.setPigUnitaDocObject(tmpPigUnitaDocObject);
		    tmpXmlSacerUnitaDoc.setTiXmlSacer(Constants.TipiXmlSacer.XML_VERS.name());
		    tmpXmlSacerUnitaDoc.setIdVers(idVers);
		    //
		    tmpPigUnitaDocObject.getPigXmlSacerUnitaDocs().add(tmpXmlSacerUnitaDoc);
		}
		tmpXmlSacerUnitaDoc.setBlXmlSacer(tmpUnitaDocObject.getDocumentoXml());
		// fine gestione XML
		// MEV 31639
		tmpXmlSacerUnitaDoc.setFlXmlMod(Constants.DB_FALSE);
	    }

	    // inserimento record in pigUnitaDocSessione
	    tmpPigUnitaDocSessione = new PigUnitaDocSessione();
	    tmpPigUnitaDocSessione.setPigSessioneIngest(tmpSessioneIngest);
	    tmpPigUnitaDocSessione.setIdVers(idVers);
	    tmpPigUnitaDocSessione
		    .setAaUnitaDocSacer(new BigDecimal(tmpUnitaDocObject.getChiaveUd().getAnno()));
	    tmpPigUnitaDocSessione
		    .setCdKeyUnitaDocSacer(tmpUnitaDocObject.getChiaveUd().getNumero());
	    tmpPigUnitaDocSessione
		    .setCdRegistroUnitaDocSacer(tmpUnitaDocObject.getChiaveUd().getRegistro());
	    tmpPigUnitaDocSessione
		    .setNiSizeFileByte(new BigDecimal(tmpUnitaDocObject.getSizeInByte()));
	    tmpPigUnitaDocSessione
		    .setTiStatoUnitaDocSessione(tmpPigUnitaDocObject.getTiStatoUnitaDocObject());
	    tmpPigUnitaDocSessione.setDtStato(new Date());
	    tmpPigUnitaDocSessione.setCdErrSacer(null);
	    tmpPigUnitaDocSessione.setDlErrSacer(null);
	    tmpPigUnitaDocSessione.setIdOrganizIam(tmpPigUnitaDocObject.getIdOrganizIam());
	    tmpPigUnitaDocSessione.setFlVersSimulato(tmpPigUnitaDocObject.getFlVersSimulato());
	    tmpPigUnitaDocSessione.setCdVerWsSacer(tmpUnitaDocObject.getVersioneWsVersamento());

	    if (tmpPigUnitaDocSessione.getPigXmlSacerUnitaDocSes() == null) {
		tmpPigUnitaDocSessione.setPigXmlSacerUnitaDocSes(new ArrayList<>());
	    }
	    PigXmlSacerUnitaDocSes xmlUnitaDocSes = new PigXmlSacerUnitaDocSes();
	    xmlUnitaDocSes.setIdVers(idVers);
	    xmlUnitaDocSes.setTiXmlSacer(TipiXmlSacer.XML_VERS.name());
	    xmlUnitaDocSes.setBlXmlSacer(tmpUnitaDocObject.getDocumentoXml());
	    //
	    tmpPigUnitaDocSessione.addPigXmlSacerUnitaDocS(xmlUnitaDocSes);
	    //
	    tmpSessioneIngest.getPigUnitaDocSessiones().add(tmpPigUnitaDocSessione);

	    xmlUnitaDocSes = new PigXmlSacerUnitaDocSes();
	    xmlUnitaDocSes.setIdVers(idVers);
	    xmlUnitaDocSes.setTiXmlSacer(TipiXmlSacer.XML_INDICE.name());
	    xmlUnitaDocSes.setBlXmlSacer(tmpUnitaDocObject.getIndiceMMXml());
	    //
	    tmpPigUnitaDocSessione.addPigXmlSacerUnitaDocS(xmlUnitaDocSes);
	    //
	    tmpSessioneIngest.getPigUnitaDocSessiones().add(tmpPigUnitaDocSessione);
	}
	Date now = Calendar.getInstance().getTime();
	if (numUnitaDocDaVersare > 0) {
	    tmpSessioneIngest.setTiStato(Constants.StatoSessioneIngest.IN_ATTESA_VERS.name());
	    pigObject.setTiStatoObject(Constants.StatoOggetto.IN_ATTESA_VERS.name());
	    // aggiornamento dei dati della sessione corrente
	    tmpSessioneIngest.setNiUnitaDocDaVers(new BigDecimal(numUnitaDocDaVersare));
	    tmpSessioneIngest.setNiUnitaDocVers(BigDecimal.ZERO);
	} else {
	    tmpSessioneIngest.setDtChiusura(now);
	    tmpSessioneIngest.setTiStato(Constants.StatoSessioneIngest.CHIUSO_OK.name());
	    pigObject.setTiStatoObject(Constants.StatoOggetto.CHIUSO_OK.name());
	}

	PigStatoSessioneIngest pigStatoSessione = new PigStatoSessioneIngest();
	pigStatoSessione.setPigSessioneIngest(tmpSessioneIngest);
	pigStatoSessione.setIdVers(idVers);
	pigStatoSessione.setTiStato(tmpSessioneIngest.getTiStato());
	pigStatoSessione.setTsRegStato(now);
	entityManager.persist(pigStatoSessione);
	tmpSessioneIngest.setIdStatoSessioneIngestCor(
		new BigDecimal(pigStatoSessione.getIdStatoSessioneIngest()));

	oggetto.setRifPigObject(pigObject);
	entityManager.flush();
    }
}
