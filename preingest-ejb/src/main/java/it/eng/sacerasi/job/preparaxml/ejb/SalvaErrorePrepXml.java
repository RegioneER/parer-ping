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

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.sacerasi.common.Constants;
import it.eng.sacerasi.common.Constants.StatoOggetto;
import it.eng.sacerasi.common.Constants.StatoVerificaHash;
import it.eng.sacerasi.entity.PigErrore;
import it.eng.sacerasi.entity.PigObject;
import it.eng.sacerasi.entity.PigSessioneIngest;
import it.eng.sacerasi.entity.PigSisma;
import it.eng.sacerasi.entity.PigStatoSessioneIngest;
import it.eng.sacerasi.entity.PigStrumentiUrbanistici;
import it.eng.sacerasi.entity.PigUnitaDocObject;
import it.eng.sacerasi.entity.PigUnitaDocSessione;
import it.eng.sacerasi.exception.ParerInternalError;
import it.eng.sacerasi.job.coda.ejb.PayloadManagerEjb;
import it.eng.sacerasi.job.preparaxml.dto.OggettoInCoda;
import it.eng.sacerasi.messages.MessaggiHelper;
import it.eng.sacerasi.sisma.ejb.SismaHelper;
import it.eng.sacerasi.strumentiUrbanistici.ejb.StrumentiUrbanisticiHelper;

/**
 *
 * @author Fioravanti_F
 */
@Stateless(mappedName = "SalvaErrorePrepXml")
@LocalBean
@Interceptors({ it.eng.sacerasi.aop.TransactionInterceptor.class })
public class SalvaErrorePrepXml {

    @EJB
    private StrumentiUrbanisticiHelper strumentiUrbanisticiHelper;
    @EJB
    private SismaHelper sismaHelper;
    @EJB
    private MessaggiHelper messaggiHelper;

    private static final Logger log = LoggerFactory.getLogger(SalvaErrorePrepXml.class);

    @PersistenceContext(unitName = "SacerAsiJPA")
    private EntityManager entityManager;
    @Resource
    SessionContext ctx;

    @EJB
    private PayloadManagerEjb payloadManagerHelper;

    /*
     * Dalla documentazione: --------------------- 1) il sistema aggiorna la sessione in PIG_SESSIONE_INGEST con
     * dtChiusura pari all?istante corrente, tiStato = CHIUSO_ERR_SCHED, cdErr e dsErr valorizzati dai controlli svolti
     * 2) il sistema aggiorna l?oggetto corrente, assegnando stato = CHIUSO_ERR_SCHED 3) il sistema elimina i file
     * dall?area FTP; i file sono raggiungibili con il percorso definito da ? cartella root specificata dal parametro
     * ?ROOT_FTP? ? cartella definita dal path di input specificato dal versatore ? cartella definita dal codice
     * identificante l?oggetto 4) il sistema elimina la cartella definita dal codice identificante l?oggetto dall?area
     * FTP 5) aggiorna le unità doc object e unità doc sessione allo stato PREPARA_XML_IN_ERRORE
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void chiudiInErrore(String rootDir, OggettoInCoda oggetto, boolean impostaKoHash) throws ParerInternalError {
        log.info("***************************");
        log.info("Chiusura in errore di preparazione XML dell'oggetto numero {}",
                oggetto.getRifPigObject().getIdObject());
        log.info("***************************");
        if (ctx.getRollbackOnly()) {
            ctx.getBusinessObject(SalvaErrorePrepXml.class).chiudiInErroreNewTx(rootDir, oggetto, impostaKoHash);
        } else {
            ctx.getBusinessObject(SalvaErrorePrepXml.class).chiudiInErroreReq(rootDir, oggetto, impostaKoHash);
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void chiudiInErroreNewTx(String rootDir, OggettoInCoda oggetto, boolean impostaKoHash)
            throws ParerInternalError {
        chiudiInErroreReq(rootDir, oggetto, impostaKoHash);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void chiudiInErroreReq(String rootDir, OggettoInCoda oggetto, boolean impostaKoHash)
            throws ParerInternalError {
        salvaDatiErr(oggetto, impostaKoHash);
        bulkUpdateUnitaDoc(PigUnitaDocObject.class.getSimpleName(), oggetto.getRifPigObject().getIdObject());
        bulkUpdateUnitaDoc(PigUnitaDocSessione.class.getSimpleName(),
                oggetto.getRifPigObject().getIdLastSessioneIngest().longValue());
        // Nuovo punto 6 dell'analisi
        // Cancella tutto nella cartella tranne se stessa ed il file .zip con lo stesso nome (CD_KEY_OBJECT)
        if (oggetto.getRifPigObject().getPigTipoObject().getTiVersFile()
                .equals(Constants.TipoVersamento.ZIP_CON_XML_SACER.name())) {
            payloadManagerHelper.eliminaCartellaTranneSeStessa(oggetto.getRifPigObject());
        }
    }

    private void salvaDatiErr(OggettoInCoda oggetto, boolean impostaKoHash) {
        BigDecimal tmpIdLastSess = oggetto.getRifPigObject().getIdLastSessioneIngest();
        PigObject tmpPigObject;
        PigSessioneIngest tmpSessioneIngest;
        Date now = new Date();
        tmpSessioneIngest = entityManager.find(PigSessioneIngest.class, tmpIdLastSess.longValue());

        // Verifico se in salvaTutto era stato messo lo stato IN_ATTESA_VERS. NON deve essere persistito, perciò lo
        // elimino
        if (tmpSessioneIngest.getTiStato().equals(Constants.StatoOggetto.IN_ATTESA_VERS.name())) {
            BigDecimal idStatoSessioneIngestCor = tmpSessioneIngest.getIdStatoSessioneIngestCor();
            PigStatoSessioneIngest pigStatoSessione = entityManager.find(PigStatoSessioneIngest.class,
                    idStatoSessioneIngestCor.longValue());
            entityManager.remove(pigStatoSessione);
        }

        tmpSessioneIngest.setDtChiusura(now);
        tmpSessioneIngest.setTiStato(Constants.StatoOggetto.CHIUSO_ERR_SCHED.name());
        tmpSessioneIngest.setCdErr(oggetto.getErrorCode());
        tmpSessioneIngest.setDlErr(oggetto.getErrorMessage());
        tmpSessioneIngest.setFlSesErrVerif("0"); // imposto a ZERO il flag di sessione verificata.
        if (impostaKoHash) {
            // se necessario imposta a KO lo stato di verifica Hash,
            // se l'errore si verifica durante il job di verifica asincrona hash
            tmpSessioneIngest.setTiStatoVerificaHash(StatoVerificaHash.KO.name());
        }
        //
        tmpPigObject = entityManager.find(PigObject.class, oggetto.getRifPigObject().getIdObject());

        tmpPigObject.setTiStatoObject(StatoOggetto.CHIUSO_ERR_SCHED.name());
        //
        entityManager.flush();

        oggetto.setRifPigObject(tmpPigObject);

        PigStatoSessioneIngest pigStatoSessione = new PigStatoSessioneIngest();
        pigStatoSessione.setPigSessioneIngest(tmpSessioneIngest);
        pigStatoSessione.setIdVers(tmpSessioneIngest.getPigVer().getIdVers());
        pigStatoSessione.setTiStato(StatoOggetto.CHIUSO_ERR_SCHED.name());
        pigStatoSessione.setTsRegStato(new Timestamp(now.getTime()));

        entityManager.persist(pigStatoSessione);

        tmpSessioneIngest.setIdStatoSessioneIngestCor(new BigDecimal(pigStatoSessione.getIdStatoSessioneIngest()));
        entityManager.flush();
        // MEV 22064 - Il SU va in stato ERRORE
        if (tmpPigObject.getPigObjectPadre() != null) {
            // MEV 22064 - ora lo stato da gestire è IN_VERSAMENTO e non più IN_ELABORAZIONE per i SU.
            PigStrumentiUrbanistici pigStrumentiUrbanistici = strumentiUrbanisticiHelper
                    .getPigStrumUrbByCdKeyAndTiStato(tmpPigObject.getPigObjectPadre().getCdKeyObject(),
                            PigStrumentiUrbanistici.TiStato.IN_VERSAMENTO);
            if (pigStrumentiUrbanistici != null) {
                PigErrore errore = messaggiHelper.retrievePigErrore("PING-ERRSU27");
                pigStrumentiUrbanistici = strumentiUrbanisticiHelper.aggiornaStato(pigStrumentiUrbanistici,
                        PigStrumentiUrbanistici.TiStato.ERRORE);
                pigStrumentiUrbanistici.setCdErr(errore.getCdErrore());
                pigStrumentiUrbanistici.setDsErr(errore.getDsErrore());
            }
        }

        // MEV 30935 - Il SISMA va in stato ERRORE
        if (tmpPigObject.getPigObjectPadre() != null) {
            // MEV 30935 - ora lo stato da gestire è IN_VERSAMENTO e non più IN_ELABORAZIONE per
            // i SU.
            PigSisma pigSisma = sismaHelper.getPigSismaByCdKeyAndTiStato(
                    tmpPigObject.getPigObjectPadre().getCdKeyObject(), PigSisma.TiStato.IN_VERSAMENTO);

            if (pigSisma == null) {
                pigSisma = sismaHelper.getPigSismaByCdKeyAndTiStato(tmpPigObject.getPigObjectPadre().getCdKeyObject(),
                        PigSisma.TiStato.IN_VERSAMENTO_SA);
            }

            if (pigSisma != null) {
                PigErrore errore = messaggiHelper.retrievePigErrore("PING-ERRSSISMA27");
                pigSisma = sismaHelper.aggiornaStato(pigSisma, PigSisma.TiStato.ERRORE);
                pigSisma.setCdErr(errore.getCdErrore());
                pigSisma.setDsErr(errore.getDsErrore());
            }
        }
    }

    private void bulkUpdateUnitaDoc(String unitaDocTable, long id) {
        if (unitaDocTable.equals(PigUnitaDocObject.class.getSimpleName())) {
            bulkUpdateUnitaDocObject(id);
        } else if (unitaDocTable.equals(PigUnitaDocSessione.class.getSimpleName())) {
            bulkUpdateUnitaDocSessione(id);
        }
    }

    private void bulkUpdateUnitaDocObject(long idObject) {
        /*
         * Eseguo una bulk update nativa per poter utilizzare i dati dalla vista a. se la unità doc è in errore, stato =
         * PREPARA_XML_IN_ERRORE ed il codice e la descrizione di errore ritornati dalla vista
         */
        Query query = entityManager.createNativeQuery(
                "UPDATE PIG_UNITA_DOC_OBJECT udObj SET (udObj.TI_STATO_UNITA_DOC_OBJECT, udObj.CD_ERR_SACER, udObj.DL_ERR_SACER ) = (SELECT 'PREPARA_XML_IN_ERRORE', chk.CD_ERR, chk.DS_ERR FROM PIG_V_LIS_CHK_UD_OBJ_ERR chk WHERE chk.id_object = ?1 AND chk.id_unita_doc_object = udObj.id_unita_doc_object AND chk.FL_UNITA_DOC_ERR = '1') WHERE udObj.id_object = ?1 AND udObj.ti_stato_unita_doc_object = 'DA_VERSARE' AND EXISTS (SELECT 1 FROM PIG_V_LIS_CHK_UD_OBJ_ERR chk WHERE chk.id_object = ?1 AND chk.id_unita_doc_object = udObj.id_unita_doc_object AND chk.FL_UNITA_DOC_ERR = '1')");
        query.setParameter(1, idObject);
        query.executeUpdate();
        entityManager.flush();
        /*
         * b. se l'unità doc non è in errore, stato = PREPARA_XML_OK
         */
        query = entityManager.createNativeQuery(
                "UPDATE PIG_UNITA_DOC_OBJECT udObj SET udObj.TI_STATO_UNITA_DOC_OBJECT = 'PREPARA_XML_OK' WHERE udObj.id_object = ?1 AND udObj.ti_stato_unita_doc_object = 'DA_VERSARE' AND EXISTS (SELECT 1 FROM PIG_V_LIS_CHK_UD_OBJ_ERR chk WHERE chk.id_object = ?1 AND chk.id_unita_doc_object = udObj.id_unita_doc_object AND chk.FL_UNITA_DOC_ERR  = '0')");
        query.setParameter(1, idObject);
        query.executeUpdate();
        entityManager.flush();
    }

    private void bulkUpdateUnitaDocSessione(long idSessioneIngest) {
        /*
         * Eseguo una bulk update nativa per poter utilizzare i dati dalla vista a. se la unità doc è in errore, stato =
         * PREPARA_XML_IN_ERRORE ed il codice e la descrizione di errore ritornati dalla vista
         */
        Query query = entityManager.createNativeQuery(
                "UPDATE PIG_UNITA_DOC_SESSIONE udSes SET (udSes.TI_STATO_UNITA_DOC_SESSIONE, udSes.CD_ERR_SACER, udSes.DL_ERR_SACER ) = (SELECT 'PREPARA_XML_IN_ERRORE', chk.CD_ERR, chk.DS_ERR FROM PIG_V_LIS_CHK_UD_SES_ERR chk WHERE chk.id_sessione_ingest = ?1 AND chk.id_unita_doc_sessione = udSes.id_unita_doc_sessione AND chk.FL_UNITA_DOC_ERR = '1') WHERE udSes.id_sessione_ingest = ?1 AND udSes.ti_stato_unita_doc_sessione = 'DA_VERSARE'AND EXISTS (SELECT 1 FROM PIG_V_LIS_CHK_UD_SES_ERR chk WHERE chk.id_sessione_ingest = ?1 AND chk.id_unita_doc_sessione = udSes.id_unita_doc_sessione AND chk.FL_UNITA_DOC_ERR = '1')");
        query.setParameter(1, idSessioneIngest);
        query.executeUpdate();
        entityManager.flush();
        /*
         * b. se l'unità doc non è in errore, stato = PREPARA_XML_OK
         */
        query = entityManager.createNativeQuery(
                "UPDATE PIG_UNITA_DOC_SESSIONE udSes SET udSes.TI_STATO_UNITA_DOC_SESSIONE = 'PREPARA_XML_OK' WHERE udSes.id_sessione_ingest = ?1 AND udSes.ti_stato_unita_doc_sessione = 'DA_VERSARE' AND EXISTS (SELECT 1 FROM PIG_V_LIS_CHK_UD_SES_ERR chk WHERE chk.id_sessione_ingest = ?1 AND chk.id_unita_doc_sessione = udSes.id_unita_doc_sessione AND chk.FL_UNITA_DOC_ERR = '0')");
        query.setParameter(1, idSessioneIngest);
        query.executeUpdate();
        entityManager.flush();
    }
}
