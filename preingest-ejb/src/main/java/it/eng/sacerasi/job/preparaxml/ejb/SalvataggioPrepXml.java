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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.eng.sacerasi.job.preparaxml.ejb;

import it.eng.paginator.util.HibernateUtils;
import it.eng.sacerasi.common.Constants;
import it.eng.sacerasi.common.Constants.TipiXmlSacer;
import it.eng.sacerasi.entity.PigContUnitaDocSacer;
import it.eng.sacerasi.entity.PigObject;
import it.eng.sacerasi.entity.PigSessioneIngest;
import it.eng.sacerasi.entity.PigStatoSessioneIngest;
import it.eng.sacerasi.entity.PigTipoObject;
import it.eng.sacerasi.entity.PigUnitaDocObject;
import it.eng.sacerasi.entity.PigUnitaDocSessione;
import it.eng.sacerasi.entity.PigXmlSacerUnitaDoc;
import it.eng.sacerasi.entity.PigXmlSacerUnitaDocSes;
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
 *
 * @author Fioravanti_F
 */
@SuppressWarnings("unchecked")
@Stateless(mappedName = "SalvataggioPrepXml")
@LocalBean
@Interceptors({ it.eng.sacerasi.aop.TransactionInterceptor.class })
public class SalvataggioPrepXml {
    @PersistenceContext(unitName = "SacerAsiJPA")
    private EntityManager entityManager;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void salvaTutto(OggettoInCoda oggetto) {
        List<PigUnitaDocObject> lstObjects;
        List<PigXmlSacerUnitaDoc> lstXml;
        PigUnitaDocObject tmpPigUnitaDocObject;
        PigUnitaDocSessione tmpPigUnitaDocSessione;
        PigXmlSacerUnitaDoc tmpXmlSacerUnitaDoc;
        PigSessioneIngest tmpSessioneIngest;
        PigContUnitaDocSacer tmpContUnitaDocSacer;
        PigTipoObject pTipoObj;

        PigObject tmpPigObject = entityManager.find(PigObject.class, oggetto.getRifPigObject().getIdObject());
        final Long idVers = tmpPigObject.getPigVer().getIdVers();
        pTipoObj = tmpPigObject.getPigTipoObject();

        // creazione o aggiornamento del contatore delle unità documentarie (per i file immagine DICOM)
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
                tmpPigObject.getIdLastSessioneIngest().longValue());

        long numUnitaDocDaVersare = 0;
        // salvataggio delle unità doc generate dall'oggetto
        for (UnitaDocObject tmpUnitaDocObject : oggetto.getListaUnitaDocObject()) {
            String queryStr = "select u from PigUnitaDocObject u " + "where u.pigObject = :pigObject "
                    + "and u.aaUnitaDocSacer = :aaUnitaDocSacer " + "and u.cdKeyUnitaDocSacer = :cdKeyUnitaDocSacer "
                    + "and u.cdRegistroUnitaDocSacer = :cdRegistroUnitaDocSacer ";
            Query query = entityManager.createQuery(queryStr);

            query.setParameter("pigObject", tmpPigObject);
            query.setParameter("aaUnitaDocSacer",
                    HibernateUtils.bigDecimalFrom(tmpUnitaDocObject.getChiaveUd().getAnno()));
            query.setParameter("cdKeyUnitaDocSacer", tmpUnitaDocObject.getChiaveUd().getNumero());
            query.setParameter("cdRegistroUnitaDocSacer", tmpUnitaDocObject.getChiaveUd().getRegistro());
            lstObjects = query.getResultList();

            if (lstObjects != null && !lstObjects.isEmpty()) {
                // se PigUnitaDocObject esiste la carico
                tmpPigUnitaDocObject = lstObjects.get(0);
            } else {
                // altrimenti la creo
                tmpPigUnitaDocObject = new PigUnitaDocObject();
                tmpPigUnitaDocObject.setPigObject(tmpPigObject);
                tmpPigUnitaDocObject.setAaUnitaDocSacer(new BigDecimal(tmpUnitaDocObject.getChiaveUd().getAnno()));
                tmpPigUnitaDocObject.setCdKeyUnitaDocSacer(tmpUnitaDocObject.getChiaveUd().getNumero());
                tmpPigUnitaDocObject.setCdRegistroUnitaDocSacer(tmpUnitaDocObject.getChiaveUd().getRegistro());
                tmpPigUnitaDocObject.setPigXmlSacerUnitaDocs(new ArrayList<>());
                tmpPigUnitaDocObject.setIdVers(idVers);
                //
                tmpPigObject.getPigUnitaDocObjects().add(tmpPigUnitaDocObject);
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
                // Se PigUnitaDocObject esiste ed è in stato VERSATA_ERR o PREPARA_XML_IN_ERRORE o PREPARA_XML_OK o
                // ANNULLATA
                // oppure non esiste e l'ho appena creata...
                //
                numUnitaDocDaVersare++;
                //
                tmpPigUnitaDocObject.setNiSizeFileByte(new BigDecimal(tmpUnitaDocObject.getSizeInByte()));
                tmpPigUnitaDocObject.setTiStatoUnitaDocObject(Constants.StatoUnitaDocObject.DA_VERSARE.name());
                tmpPigUnitaDocObject.setCdErrSacer(null);
                tmpPigUnitaDocObject.setDlErrSacer(null);
                tmpPigUnitaDocObject.setIdOrganizIam(tmpUnitaDocObject.getIdOrganizSacer());
                tmpPigUnitaDocObject.setFlVersSimulato(tmpUnitaDocObject.isSimulaVersamento() ? "1" : "0");
                tmpPigUnitaDocObject.setCdVerWsSacer(tmpUnitaDocObject.getVersioneWsVersamento());
                // gestione XML
                // aggiunta o sostituzione dell'XML_INDICE
                queryStr = "select u from PigXmlSacerUnitaDoc u " + "where u.pigUnitaDocObject = :pigUnitaDocObject "
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
                queryStr = "select u from PigXmlSacerUnitaDoc u " + "where u.pigUnitaDocObject = :pigUnitaDocObject "
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
                tmpXmlSacerUnitaDoc.setBlXmlSacer(tmpUnitaDocObject.getUnitaDocumentariaXml());
                // fine gestione XML
                // MEV 31639
                tmpXmlSacerUnitaDoc.setFlXmlMod(Constants.DB_FALSE);
            }

            // inserimento record in pigUnitaDocSessione
            tmpPigUnitaDocSessione = new PigUnitaDocSessione();
            tmpPigUnitaDocSessione.setPigSessioneIngest(tmpSessioneIngest);
            tmpPigUnitaDocSessione.setIdVers(idVers);
            tmpPigUnitaDocSessione.setAaUnitaDocSacer(new BigDecimal(tmpUnitaDocObject.getChiaveUd().getAnno()));
            tmpPigUnitaDocSessione.setCdKeyUnitaDocSacer(tmpUnitaDocObject.getChiaveUd().getNumero());
            tmpPigUnitaDocSessione.setCdRegistroUnitaDocSacer(tmpUnitaDocObject.getChiaveUd().getRegistro());
            tmpPigUnitaDocSessione.setNiSizeFileByte(new BigDecimal(tmpUnitaDocObject.getSizeInByte()));
            tmpPigUnitaDocSessione.setTiStatoUnitaDocSessione(tmpPigUnitaDocObject.getTiStatoUnitaDocObject());
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
            xmlUnitaDocSes.setBlXmlSacer(tmpUnitaDocObject.getUnitaDocumentariaXml());
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
            tmpPigObject.setTiStatoObject(Constants.StatoOggetto.IN_ATTESA_VERS.name());
            // aggiornamento dei dati della sessione corrente
            tmpSessioneIngest.setNiUnitaDocDaVers(new BigDecimal(numUnitaDocDaVersare));
            tmpSessioneIngest.setNiUnitaDocVers(BigDecimal.ZERO);
        } else {
            tmpSessioneIngest.setDtChiusura(now);
            tmpSessioneIngest.setTiStato(Constants.StatoSessioneIngest.CHIUSO_OK.name());
            tmpPigObject.setTiStatoObject(Constants.StatoOggetto.CHIUSO_OK.name());
        }

        PigStatoSessioneIngest pigStatoSessione = new PigStatoSessioneIngest();
        pigStatoSessione.setPigSessioneIngest(tmpSessioneIngest);
        pigStatoSessione.setIdVers(idVers);
        pigStatoSessione.setTiStato(tmpSessioneIngest.getTiStato());
        pigStatoSessione.setTsRegStato(now);
        entityManager.persist(pigStatoSessione);
        tmpSessioneIngest.setIdStatoSessioneIngestCor(new BigDecimal(pigStatoSessione.getIdStatoSessioneIngest()));

        oggetto.setRifPigObject(tmpPigObject);
        entityManager.flush();
    }
}
