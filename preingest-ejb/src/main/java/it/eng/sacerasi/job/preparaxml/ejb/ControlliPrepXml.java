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

/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */
package it.eng.sacerasi.job.preparaxml.ejb;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import it.eng.sacerasi.common.Chiave;
import it.eng.sacerasi.common.Constants;
import it.eng.sacerasi.entity.PigContUnitaDocSacer;
import it.eng.sacerasi.entity.PigObject;
import it.eng.sacerasi.entity.PigTipoFileObject;
import it.eng.sacerasi.entity.PigTipoObject;
import it.eng.sacerasi.job.preparaxml.dto.OggettoInCoda;
import it.eng.sacerasi.viewEntity.PigVChkOrgVersSacer;
import it.eng.sacerasi.viewEntity.PigVChkSesPrecNotAnnul;
import it.eng.sacerasi.viewEntity.PigVChkSimulaVersSacer;

/**
 *
 * @author Fioravanti_F
 */
@SuppressWarnings("unchecked")
@Stateless(mappedName = "ControlliPrepXml")
@LocalBean
@TransactionAttribute(TransactionAttributeType.REQUIRED) // inutile, ma meglio andare sul sicuro
public class ControlliPrepXml {

    @PersistenceContext(unitName = "SacerAsiJPA")
    private EntityManager entityManager;

    /*
     * usato da job produzione xml
     */
    public List<PigObject> getListaObjectDaVersPostHash() {
	// MAC#29391 - Gestione della priorità di versamento del JOB PREPARA XML SACER
	String queryStr = "select u from PigObject u " + "join u.pigSessioneIngests si "
		+ "where u.tiStatoObject = :tiStatoObjectIn "
		+ "and si.idSessioneIngest = u.idLastSessioneIngest "
		+ "and si.tiStatoVerificaHash = :tiStatoVerificaHashIn "
		+ "ORDER BY u.tiPrioritaVersamento, si.dtApertura ASC";
	javax.persistence.Query query = entityManager.createQuery(queryStr);
	query.setParameter("tiStatoObjectIn", Constants.StatoOggetto.IN_ATTESA_SCHED.name());
	query.setParameter("tiStatoVerificaHashIn", Constants.StatoVerificaHash.OK.name());

	return query.getResultList();
    }

    public BigDecimal recuperaAggiornaContUDSacer(OggettoInCoda oggetto, BigDecimal annoRif) {
	BigDecimal tmpRet;
	PigTipoObject pTipoObj;
	List<PigContUnitaDocSacer> tmpPigContUnitaDocSacers;
	PigContUnitaDocSacer tmpContUnitaDocSacer;
	OggettoInCoda.ContUnitaDocSacer contUnitaDoc = new OggettoInCoda().new ContUnitaDocSacer();

	oggetto.setValoreContUnitaDocSacer(contUnitaDoc);
	pTipoObj = entityManager.find(PigTipoObject.class,
		oggetto.getRifPigObject().getPigTipoObject().getIdTipoObject());

	String queryStr = "select p from PigContUnitaDocSacer p "
		+ "where p.pigTipoObject = :pigTipoObject "
		+ "and p.aaUnitaDocSacer = :aaUnitaDocSacer";
	javax.persistence.Query query = entityManager.createQuery(queryStr);
	query.setParameter("pigTipoObject", pTipoObj);
	query.setParameter("aaUnitaDocSacer", annoRif);
	tmpPigContUnitaDocSacers = query.getResultList();

	if (tmpPigContUnitaDocSacers != null && tmpPigContUnitaDocSacers.size() == 1) {
	    tmpContUnitaDocSacer = tmpPigContUnitaDocSacers.get(0);
	    tmpRet = tmpContUnitaDocSacer.getPgContUnitaDocSacer()
		    .add(new BigDecimal(BigInteger.ONE));
	    //
	    contUnitaDoc
		    .setRifIdPigContUnitaDocSacer(tmpContUnitaDocSacer.getIdContUnitaDocSacer());
	    contUnitaDoc.setValore(tmpRet);
	    //
	} else {
	    tmpRet = BigDecimal.ONE;
	    //
	    contUnitaDoc.setRifIdPigTipoObject(pTipoObj.getIdTipoObject());
	    contUnitaDoc.setAnnoUnitaDocSacer(annoRif);
	    contUnitaDoc.setValore(tmpRet);
	    //
	}

	return tmpRet;
    }

    public boolean verificaPUDocObjNonVersata(Chiave chiave, long idObj) {
	boolean tmpRet = false;
	long conta;

	String queryStr = "select count(pig_udo) from PigUnitaDocObject pig_udo "
		+ " where pig_udo.pigObject.idObject = :idObjectIn "
		+ "and pig_udo.cdRegistroUnitaDocSacer = :cdRegistroUnitaDocSacerIn "
		+ "and pig_udo.aaUnitaDocSacer = :aaUnitaDocSacerIn "
		+ "and pig_udo.cdKeyUnitaDocSacer = :cdKeyUnitaDocSacerIn "
		+ "and (pig_udo.tiStatoUnitaDocObject = :stateOk "
		+ " or (pig_udo.tiStatoUnitaDocObject = :stateErr "
		+ "  and (pig_udo.cdErrSacer = :errCode "
		+ "    or pig_udo.cdErrSacer = :errCodeAlt))) ";

	javax.persistence.Query query = entityManager.createQuery(queryStr);
	query.setParameter("idObjectIn", idObj);
	query.setParameter("cdRegistroUnitaDocSacerIn", chiave.getRegistro());
	query.setParameter("aaUnitaDocSacerIn", new BigDecimal(chiave.getAnno()));
	query.setParameter("cdKeyUnitaDocSacerIn", chiave.getNumero());
	query.setParameter("stateOk", Constants.StatoUnitaDocObject.VERSATA_OK.name());
	query.setParameter("stateErr", Constants.StatoUnitaDocObject.VERSATA_ERR.name());
	query.setParameter("errCode", Constants.COD_VERS_ERR_CHIAVE_DUPLICATA_OLD);
	query.setParameter("errCodeAlt", Constants.COD_VERS_ERR_CHIAVE_DUPLICATA_NEW);

	conta = (Long) query.getSingleResult();

	if (conta == 0) {
	    tmpRet = true;
	}
	return tmpRet;
    }

    public PigTipoFileObject getPigTipoFileObj(String nmTipoFileObj, long idTipoObj) {
	List<PigTipoFileObject> pigTipoFileObjects;

	String queryStr = "select u from PigTipoFileObject u where u.pigTipoObject.idTipoObject = :idTipoObject "
		+ "and u.nmTipoFileObject = :nmTipoFileObject ";

	javax.persistence.Query query = entityManager.createQuery(queryStr);
	query.setParameter("idTipoObject", idTipoObj);
	query.setParameter("nmTipoFileObject", nmTipoFileObj);
	pigTipoFileObjects = query.getResultList();
	if (pigTipoFileObjects != null && !pigTipoFileObjects.isEmpty()) {
	    return pigTipoFileObjects.get(0);
	} else {
	    return null;
	}
    }

    public boolean checkStrutturaPigVChkOrgVersSacer(BigDecimal idObject) {
	PigVChkOrgVersSacer view = entityManager.find(PigVChkOrgVersSacer.class, idObject);
	return view.getFlOrgVersSacerOk().equals("1");
    }

    public boolean checkSimulazionePigVChkSimulaVersSacer(BigDecimal idObject) {
	PigVChkSimulaVersSacer view = entityManager.find(PigVChkSimulaVersSacer.class, idObject);
	return view.getFlSimulaVersSacerOk().equals("1");
    }

    public boolean checkPigVChkSesPrecNotAnnul(BigDecimal idObject) {
	PigVChkSesPrecNotAnnul view = entityManager.find(PigVChkSesPrecNotAnnul.class, idObject);
	return view.getFlSesPrecNotAnnullata().equals("1");
    }

}
