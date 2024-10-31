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
package it.eng.sacerasi.job.recuperaErrori.ejb;

import it.eng.sacerasi.common.Constants;
import it.eng.sacerasi.common.Constants.StatoSessioneIngest;
import it.eng.sacerasi.entity.PigObject;
import it.eng.sacerasi.entity.PigSessioneIngest;
import it.eng.sacerasi.entity.PigStatoSessioneIngest;
import it.eng.sacerasi.entity.PigUnitaDocObject;
import it.eng.sacerasi.entity.PigUnitaDocSessione;
import it.eng.sacerasi.entity.PigXmlSacerUnitaDocSes;
import it.eng.sacerasi.entity.PigXmlSessioneIngest;
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
import javax.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Bonora_L
 */
@Stateless(mappedName = "RecuperoErroriInCodaHelper")
@LocalBean
public class RecuperoErroriInCodaHelper {

    private static final Logger log = LoggerFactory.getLogger(RecuperoErroriInCodaHelper.class);
    @PersistenceContext(unitName = "SacerAsiJPA")
    private EntityManager entityManager;
    @EJB
    private RecuperoErroriInCodaHelper me;

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void elaboraOggetto(PigObject obj) {
        obj = entityManager.find(PigObject.class, obj.getIdObject());
        log.debug("JOB RecuperaErroriInCoda - creo la nuova sessione con stato IN_ATTESA_VERS per l'oggetto {}",
                obj.getCdKeyObject());
        PigSessioneIngest oldSession = entityManager.find(PigSessioneIngest.class,
                obj.getIdLastSessioneIngest().longValue());
        PigSessioneIngest sessione = me.creaSessione(obj, oldSession);
        log.debug("JOB RecuperaErroriInCoda - sessione {} creata", sessione.getIdSessioneIngest());
        log.debug(
                "JOB RecuperaErroriInCoda - creo gli oggetti Xml per la nuova sessione basandomi sui dati della precedente");
        me.creaXmlSessioneIngest(oldSession, sessione);
        log.debug("JOB RecuperaErroriInCoda - creati gli oggetti Xml");
        log.debug(
                "JOB RecuperaErroriInCoda - per ogni unità doc dell'oggetto creo una unità doc sessione da associare alla sessione creata");
        for (PigUnitaDocObject udObj : obj.getPigUnitaDocObjects()) {
            // ff 2016-05-04 - rimosso stato VERSATA_TIMEOUT per le UD da processare
            if (udObj.getTiStatoUnitaDocObject().equals(Constants.StatoUnitaDocObject.DA_VERSARE.name())) {
                me.creaUnitaDocSessione(udObj, sessione, oldSession);
            }
        }
        log.debug("JOB RecuperaErroriInCoda - modifico lo stato dell'oggetto {} in IN_ATTESA_VERS",
                obj.getCdKeyObject());
        obj.setTiStatoObject(Constants.StatoOggetto.IN_ATTESA_VERS.name());
        obj.setIdLastSessioneIngest(new BigDecimal(sessione.getIdSessioneIngest()));
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
        /*
         * Il numero di unità documentarie da versare definito nella sessione è pari al conteggio delle unità
         * documentarie dell'oggetto con stato = DA_VERSARE; il numero di unità documentarie versate definito nella
         * sessione è pari a 0
         */
        sessione.setNiUnitaDocDaVers(new BigDecimal(getCountUdDaVersare(obj)));
        sessione.setNiUnitaDocVers(BigDecimal.ZERO);
        sessione.setPigUnitaDocSessiones(new ArrayList<>());

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

    public void creaUnitaDocSessione(PigUnitaDocObject udObj, PigSessioneIngest sessione,
            PigSessioneIngest oldSession) {
        PigUnitaDocSessione udSessione = new PigUnitaDocSessione();
        udSessione.setAaUnitaDocSacer(udObj.getAaUnitaDocSacer());
        udSessione.setCdErrSacer(null);
        udSessione.setCdKeyUnitaDocSacer(udObj.getCdKeyUnitaDocSacer());
        udSessione.setCdRegistroUnitaDocSacer(udObj.getCdRegistroUnitaDocSacer());
        udSessione.setDlErrSacer(null);
        udSessione.setNiSizeFileByte(udObj.getNiSizeFileByte());
        udSessione.setPigSessioneIngest(sessione);
        udSessione.setIdVers(sessione.getPigVer().getIdVers());
        udSessione.setTiStatoUnitaDocSessione(Constants.StatoUnitaDocSessione.DA_VERSARE.name());
        udSessione.setDtStato(new Date());
        // Aggiunti nuovi campi relativi alla trasformazione
        udSessione.setIdOrganizIam(udObj.getIdOrganizIam());
        udSessione.setFlVersSimulato(udObj.getFlVersSimulato());
        sessione.getPigUnitaDocSessiones().add(udSessione);
        /* Copia nella Ud di sessione appena creata tutti gli xml della ud dell'oggetto della vecchia sessione */
        for (PigUnitaDocSessione oldUdSessione : oldSession.getPigUnitaDocSessiones()) {
            if (udSessione.getAaUnitaDocSacer().equals(oldUdSessione.getAaUnitaDocSacer())
                    && udSessione.getCdKeyUnitaDocSacer().equals(oldUdSessione.getCdKeyUnitaDocSacer())
                    && udSessione.getCdRegistroUnitaDocSacer().equals(oldUdSessione.getCdRegistroUnitaDocSacer())) {
                for (PigXmlSacerUnitaDocSes oldUdSessXml : oldUdSessione.getPigXmlSacerUnitaDocSes()) {
                    PigXmlSacerUnitaDocSes newUdSessXml = new PigXmlSacerUnitaDocSes();
                    newUdSessXml.setIdVers(sessione.getPigVer().getIdVers());
                    newUdSessXml.setBlXmlSacer(oldUdSessXml.getBlXmlSacer());
                    newUdSessXml.setTiXmlSacer(oldUdSessXml.getTiXmlSacer());
                    newUdSessXml.setPigUnitaDocSessione(udSessione);
                    // MAC#15916 - Copiatura XML vers a SACER quando creo sessione
                    if (udSessione.getPigXmlSacerUnitaDocSes() == null) {
                        udSessione.setPigXmlSacerUnitaDocSes(new ArrayList<>());
                    }
                    udSessione.addPigXmlSacerUnitaDocS(newUdSessXml);
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    public List<PigObject> getListaObjects(List<Long> idVersatori, Constants.StatoOggetto stato) {
        String queryStr = "SELECT obj FROM PigObject obj " + "WHERE obj.tiStatoObject = :tiStato "
                + "AND obj.pigVer.idVers IN (:idVers) " + "AND NOT EXISTS(" + "SELECT ud FROM PigUnitaDocObject ud "
                + "WHERE ud.pigObject.idObject = obj.idObject " + "AND ud.tiStatoUnitaDocObject = '"
                + Constants.StatoUnitaDocObject.IN_CODA_VERS.name() + "' " + ")";
        javax.persistence.Query query = entityManager.createQuery(queryStr);
        query.setParameter("tiStato", stato.name());
        query.setParameter("idVers", idVersatori);

        return query.getResultList();
    }

    public int getCountUdDaVersare(PigObject obj) {
        int counter = 0;
        for (PigUnitaDocObject ud : obj.getPigUnitaDocObjects()) {
            if (ud.getTiStatoUnitaDocObject().equals(Constants.StatoUnitaDocObject.DA_VERSARE.name())) {
                counter++;
            }
        }
        return counter;
    }
}
