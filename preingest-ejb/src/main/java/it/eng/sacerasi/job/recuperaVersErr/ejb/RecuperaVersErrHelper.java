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
package it.eng.sacerasi.job.recuperaVersErr.ejb;

import it.eng.sacerasi.common.Constants;
import it.eng.sacerasi.common.Constants.StatoSessioneIngest;
import it.eng.sacerasi.entity.PigObject;
import it.eng.sacerasi.entity.PigSessioneIngest;
import it.eng.sacerasi.entity.PigSisma;
import it.eng.sacerasi.entity.PigStatoSessioneIngest;
import it.eng.sacerasi.entity.PigStrumentiUrbanistici;
import it.eng.sacerasi.entity.PigUnitaDocObject;
import it.eng.sacerasi.entity.PigUnitaDocSessione;
import it.eng.sacerasi.entity.PigXmlSacerUnitaDocSes;
import it.eng.sacerasi.entity.PigXmlSessioneIngest;
import it.eng.sacerasi.sisma.ejb.SismaHelper;
import it.eng.sacerasi.strumentiUrbanistici.ejb.StrumentiUrbanisticiHelper;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Bonora_L
 */
@Stateless(mappedName = "RecuperaVersErrHelper")
@LocalBean
public class RecuperaVersErrHelper {

    private static final Logger log = LoggerFactory.getLogger(RecuperaVersErrHelper.class);
    @PersistenceContext(unitName = "SacerAsiJPA")
    private EntityManager entityManager;
    @EJB
    private StrumentiUrbanisticiHelper strumentiUrbanisticiHelper;
    @EJB
    private RecuperaVersErrHelper me;
    @EJB
    private SismaHelper sismaHelper;

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void elaboraOggetto(PigObject obj) {
        obj = entityManager.find(PigObject.class, obj.getIdObject());
        boolean isValidObject = checkIsValidObj(obj);
        if (isValidObject) {
            log.debug("JOB {} - modifico lo stato dell'oggetto {} in IN_ATTESA_VERS",
                    Constants.NomiJob.RECUPERA_VERS_ERR.name(), obj.getCdKeyObject());
            if (Constants.StatoOggetto.CHIUSO_ERR_VERS.name().equals(obj.getTiStatoObject())
                    || Constants.StatoOggetto.CHIUSO_ERR_RECUPERABILE.name().equals(obj.getTiStatoObject())) {
                log.debug(
                        "JOB {} - l’oggetto ha stato = CHIUSO_ERR_VERS, rendo nullo l’indicatore che segnala che l’oggetto e’ da recuperare",
                        Constants.NomiJob.RECUPERA_VERS_ERR.name());
                obj.setFlVersSacerDaRecup(null);
            }
            obj.setTiStatoObject(Constants.StatoOggetto.IN_ATTESA_VERS.name());
            log.debug("JOB {} - creo la nuova sessione con stato IN_ATTESA_VERS per l'oggetto {}, id {}",
                    Constants.NomiJob.RECUPERA_VERS_ERR.name(), obj.getCdKeyObject(), obj.getIdObject());
            PigSessioneIngest oldSession = entityManager.find(PigSessioneIngest.class,
                    obj.getIdLastSessioneIngest().longValue());
            PigSessioneIngest sessione = me.creaSessione(obj, oldSession);
            log.debug("JOB {} - sessione {} creata", Constants.NomiJob.RECUPERA_VERS_ERR.name(),
                    sessione.getIdSessioneIngest());
            log.debug("JOB {} - creo gli oggetti Xml per la nuova sessione basandomi sui dati della precedente",
                    Constants.NomiJob.RECUPERA_VERS_ERR.name());
            me.creaXmlSessioneIngest(oldSession, sessione);
            log.debug("JOB {} - creati gli oggetti Xml", Constants.NomiJob.RECUPERA_VERS_ERR.name());
            obj.setIdLastSessioneIngest(new BigDecimal(sessione.getIdSessioneIngest()));

            // MEV 31816 - correggo lo stato di un eventuale SISMA
            if (obj.getPigObjectPadre() != null) {
                PigSisma pigSisma = sismaHelper.getPigSismaByCdKeyAndTiStato(obj.getPigObjectPadre().getCdKeyObject(),
                        PigSisma.TiStato.ERRORE);

                if (pigSisma != null) {
                    // Setta lo stato di PigSisma
                    Enum<Constants.TipoVersatore> tipo = sismaHelper.getTipoVersatore(pigSisma.getPigVer());
                    if (tipo.equals(Constants.TipoVersatore.SA_PUBBLICO)
                            && pigSisma.getFlInviatoAEnte().equals(Constants.DB_FALSE)) {
                        sismaHelper.aggiornaStato(pigSisma, PigSisma.TiStato.IN_VERSAMENTO_SA);
                    } else {
                        sismaHelper.aggiornaStato(pigSisma, PigSisma.TiStato.IN_VERSAMENTO);
                    }

                    log.debug("JOB {} - aggiornato lo stato del SISMA collegato",
                            Constants.NomiJob.RECUPERA_VERS_ERR.name());
                }
            }

            // MEV 31651 - correggo lo stato di un eventuale Strumento Urbanistico
            if (obj.getPigObjectPadre() != null) {
                PigStrumentiUrbanistici pigStrumentiUrbanistici = strumentiUrbanisticiHelper
                        .getPigStrumUrbByCdKeyAndTiStato(obj.getPigObjectPadre().getCdKeyObject(),
                                PigStrumentiUrbanistici.TiStato.ERRORE);
                if (pigStrumentiUrbanistici != null) {
                    strumentiUrbanisticiHelper.aggiornaStato(pigStrumentiUrbanistici,
                            PigStrumentiUrbanistici.TiStato.IN_VERSAMENTO);
                    log.debug("JOB {} - aggiornato lo stato del SU collegato",
                            Constants.NomiJob.RECUPERA_VERS_ERR.name());
                }
            }
        }
    }

    private boolean checkIsValidObj(PigObject obj) {
        boolean isValidObj = false;
        String queryStr = "SELECT obj FROM PigObject obj " + "WHERE obj.idObject = :idObjectToCheck "
                + "AND (obj.tiStatoObject = :statoErrTimeout OR (obj.tiStatoObject = :statoErrVers AND obj.flVersSacerDaRecup = :flVersSacerDaRecup))";
        javax.persistence.Query query = entityManager.createQuery(queryStr);
        query.setParameter("idObjectToCheck", obj.getIdObject());
        query.setParameter("statoErrTimeout", Constants.StatoOggetto.CHIUSO_ERR_RECUPERABILE.name());
        query.setParameter("statoErrVers", Constants.StatoOggetto.CHIUSO_ERR_VERS.name());
        query.setParameter("flVersSacerDaRecup", "1");
        // TODO: testare dalla parte online se questo lock funziona bene
        PigObject validObject = ((PigObject) query.setLockMode(LockModeType.PESSIMISTIC_READ).getSingleResult());
        if (validObject != null) {
            isValidObj = true;
        }
        return isValidObj;
    }

    public PigSessioneIngest creaSessione(PigObject obj, PigSessioneIngest oldSession) {
        PigSessioneIngest sessione = new PigSessioneIngest();
        sessione.setTiStato(Constants.StatoSessioneIngest.IN_ATTESA_VERS.name());
        sessione.setPigVer(obj.getPigVer());
        sessione.setDtApertura(Calendar.getInstance().getTime());
        sessione.setDtChiusura(null);
        sessione.setCdKeyObject(obj.getCdKeyObject());
        sessione.setNmAmbienteVers(obj.getPigVer().getPigAmbienteVer().getNmAmbienteVers());
        sessione.setNmVers(obj.getPigVer().getNmVers());
        sessione.setNmTipoObject(obj.getPigTipoObject().getNmTipoObject());
        sessione.setPigObject(obj);
        sessione.setFlFileCifrato(oldSession.getFlFileCifrato());
        sessione.setFlForzaAccettazione(oldSession.getFlForzaAccettazione());
        sessione.setFlForzaWarning(oldSession.getFlForzaWarning());
        sessione.setCdVersioneXmlVers(oldSession.getCdVersioneXmlVers());
        sessione.setPigXmlSessioneIngests(new ArrayList<>());
        sessione.setTiStatoVerificaHash(oldSession.getTiStatoVerificaHash());
        sessione.setNmAmbienteVersPadre(oldSession.getNmAmbienteVersPadre());
        sessione.setNmVersPadre(oldSession.getNmVersPadre());
        sessione.setCdKeyObjectPadre(oldSession.getCdKeyObjectPadre());
        sessione.setNiUnitaDocAttese(oldSession.getNiUnitaDocAttese());
        sessione.setNiTotObjectTrasf(oldSession.getNiTotObjectTrasf());
        sessione.setPgOggettoTrasf(oldSession.getPgOggettoTrasf());
        sessione.setDsObject(oldSession.getDsObject());
        /*
         * Imposto le nuove informazioni relative alla trasformazione
         */
        sessione.setCdTrasf(oldSession.getCdTrasf());
        sessione.setCdVersGen(oldSession.getCdVersGen());
        sessione.setCdVersioneTrasf(oldSession.getCdVersioneTrasf());
        List<PigUnitaDocObject> udDaVersare = getUdVersTimeout(obj);
        /*
         * Il numero di unità documentarie da versare definito nella sessione è pari al conteggio delle unità
         * documentarie dell'oggetto con stato = VERSATA_TIMEOUT; il numero di unità documentarie versate definito nella
         * sessione è pari a 0
         */
        sessione.setNiUnitaDocDaVers(new BigDecimal(udDaVersare.size()));
        sessione.setNiUnitaDocVers(BigDecimal.ZERO);
        sessione.setPigUnitaDocSessiones(new ArrayList<>());
        registraUD(udDaVersare, sessione, oldSession);
        entityManager.persist(sessione);
        entityManager.flush();
        PigStatoSessioneIngest pigStatoSessione = new PigStatoSessioneIngest();
        pigStatoSessione.setPigSessioneIngest(sessione);
        pigStatoSessione.setIdVers(sessione.getPigVer().getIdVers());
        pigStatoSessione.setTiStato(StatoSessioneIngest.IN_ATTESA_VERS.name());
        pigStatoSessione.setTsRegStato(new Date());
        entityManager.persist(pigStatoSessione);
        sessione.setIdStatoSessioneIngestCor(new BigDecimal(pigStatoSessione.getIdStatoSessioneIngest()));
        entityManager.flush();
        return sessione;
    }

    public void creaXmlSessioneIngest(PigSessioneIngest oldSessione, PigSessioneIngest sessione) {
        for (PigXmlSessioneIngest oldXmlSessione : oldSessione.getPigXmlSessioneIngests()) {
            PigXmlSessioneIngest xmlSessione = new PigXmlSessioneIngest();
            xmlSessione.setPigSessioneIngest(sessione);
            xmlSessione.setIdVers(sessione.getPigVer().getIdVers());
            xmlSessione.setBlXml(oldXmlSessione.getBlXml());
            sessione.getPigXmlSessioneIngests().add(xmlSessione);
        }
    }

    @SuppressWarnings("unchecked")
    public List<PigObject> getListaObjects(List<Long> idVersatori, String statoTimeout, String statoErrVers,
            int olderThanMinutes) {
        String queryStr = "SELECT DISTINCT obj FROM PigObject obj " + "JOIN obj.pigSessioneIngests siIng "
                + "WHERE ((obj.tiStatoObject = :statoTimeout AND siIng.dtChiusura <= :data) OR (obj.tiStatoObject = :statoErrVers AND obj.flVersSacerDaRecup = :flVersSacerDaRecup)) "
                + "AND obj.pigVer.idVers IN (:idVers) " + "AND NOT EXISTS(" + "SELECT ud FROM PigUnitaDocObject ud "
                + "WHERE ud.pigObject.idObject = obj.idObject " + "AND (ud.tiStatoUnitaDocObject = '"
                + Constants.StatoUnitaDocObject.IN_CODA_VERS.name() + "' " + "OR ud.tiStatoUnitaDocObject = '"
                + Constants.StatoUnitaDocObject.DA_VERSARE.name() + "') " + ") "
                + "AND obj.idLastSessioneIngest = siIng.idSessioneIngest ";
        javax.persistence.Query query = entityManager.createQuery(queryStr);
        query.setParameter("statoTimeout", statoTimeout);
        query.setParameter("statoErrVers", statoErrVers);
        query.setParameter("idVers", idVersatori);
        query.setParameter("flVersSacerDaRecup", "1");
        Date ades = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(ades);

        // MEV 31714 - la variabile olderThanMinutes deve essere positiva!
        if (olderThanMinutes < 0)
            olderThanMinutes = 0;

        calendar.add(Calendar.MINUTE, -olderThanMinutes);
        query.setParameter("data", calendar.getTime());
        return query.getResultList();
    }

    public List<PigUnitaDocObject> getUdVersTimeout(PigObject obj) {
        List<PigUnitaDocObject> list = new ArrayList<>();
        for (PigUnitaDocObject ud : obj.getPigUnitaDocObjects()) {
            if (ud.getTiStatoUnitaDocObject().equals(Constants.StatoUnitaDocObject.VERSATA_TIMEOUT.name())
                    || (ud.getTiStatoUnitaDocObject().equals(Constants.StatoUnitaDocObject.VERSATA_ERR.name())
                            && !ud.getCdErrSacer().equals(Constants.COD_VERS_ERR_CHIAVE_DUPLICATA_NEW))) {
                list.add(ud);
            }
        }
        return list;
    }

    public void registraUD(List<PigUnitaDocObject> pigUnitaDocObjects, PigSessioneIngest sessione,
            PigSessioneIngest oldSession) {
        for (PigUnitaDocObject pudo : pigUnitaDocObjects) {
            if (pudo.getTiStatoUnitaDocObject().equals(Constants.StatoUnitaDocObject.VERSATA_TIMEOUT.name())
                    || (pudo.getTiStatoUnitaDocObject().equals(Constants.StatoUnitaDocObject.VERSATA_ERR.name())
                            && !pudo.getCdErrSacer().equals(Constants.COD_VERS_ERR_CHIAVE_DUPLICATA_NEW))) {

                // MEV 27407
                Date dtStato = new Date();

                // Registra una UD della sessione per ogni UD dell'oggetto con stato = VERSATA_TIMEOUT o stato =
                // VERSATA_ERR
                PigUnitaDocSessione udSessione = new PigUnitaDocSessione();
                udSessione.setTiStatoUnitaDocSessione(Constants.StatoUnitaDocObject.DA_VERSARE.name());
                udSessione.setDtStato(dtStato);
                udSessione.setPigSessioneIngest(sessione);
                udSessione.setIdVers(sessione.getPigVer().getIdVers());
                udSessione.setCdRegistroUnitaDocSacer(pudo.getCdRegistroUnitaDocSacer());
                udSessione.setAaUnitaDocSacer(pudo.getAaUnitaDocSacer());
                udSessione.setCdKeyUnitaDocSacer(pudo.getCdKeyUnitaDocSacer());
                udSessione.setNiSizeFileByte(pudo.getNiSizeFileByte());
                // Aggiunti nuovi campi relativi alla trasformazione
                udSessione.setIdOrganizIam(pudo.getIdOrganizIam());
                udSessione.setFlVersSimulato(pudo.getFlVersSimulato());
                // MAC#15916 - Aggiunto metodo
                udSessione.setPigXmlSacerUnitaDocSes(new ArrayList<>());
                sessione.getPigUnitaDocSessiones().add(udSessione);
                /*
                 * aggiorno le unità documentarie dell’oggetto con stato = VERSATA_TIMEOUT o VERSATA_ERR, assegnando
                 * stato = DA_VERSARE e annullo il codice e la descrizione dell’errore
                 */
                pudo.setTiStatoUnitaDocObject(Constants.StatoUnitaDocObject.DA_VERSARE.name());
                pudo.setDtStato(dtStato);
                pudo.setCdErrSacer(null);
                pudo.setDlErrSacer(null);

                /*
                 * Copia nella Ud di sessione appena creata tutti gli xml della ud dell'oggetto della vecchia sessione
                 */
                for (PigUnitaDocSessione oldUdSessione : oldSession.getPigUnitaDocSessiones()) {
                    if (udSessione.getAaUnitaDocSacer().equals(oldUdSessione.getAaUnitaDocSacer())
                            && udSessione.getCdKeyUnitaDocSacer().equals(oldUdSessione.getCdKeyUnitaDocSacer())
                            && udSessione.getCdRegistroUnitaDocSacer()
                                    .equals(oldUdSessione.getCdRegistroUnitaDocSacer())) {
                        for (PigXmlSacerUnitaDocSes oldUdSessXml : oldUdSessione.getPigXmlSacerUnitaDocSes()) {
                            PigXmlSacerUnitaDocSes newUdSessXml = new PigXmlSacerUnitaDocSes();
                            newUdSessXml.setIdVers(sessione.getPigVer().getIdVers());
                            newUdSessXml.setBlXmlSacer(oldUdSessXml.getBlXmlSacer());
                            newUdSessXml.setTiXmlSacer(oldUdSessXml.getTiXmlSacer());
                            newUdSessXml.setPigUnitaDocSessione(udSessione);
                            // MAC#15916 - Aggiunto metodo
                            udSessione.addPigXmlSacerUnitaDocS(newUdSessXml);
                        }
                    }
                }
            }
        }
    }
}
