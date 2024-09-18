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

package it.eng.sacerasi.ws.ejb;

import it.eng.paginator.util.HibernateUtils;
import org.apache.commons.codec.binary.Base64;
import it.eng.sacerasi.common.Constants;
import it.eng.sacerasi.common.Constants.StatoOggetto;
import it.eng.sacerasi.common.Constants.StatoSessioneIngest;
import it.eng.sacerasi.entity.*;
import it.eng.sacerasi.exception.ParerInternalError;
import it.eng.sacerasi.ws.dto.RispostaControlli;
import it.eng.sacerasi.ws.invioOggettoAsincrono.dto.InvioOggettoAsincronoEstesoInput;
import it.eng.sacerasi.ws.invioOggettoAsincrono.dto.InvioOggettoAsincronoExt;
import it.eng.sacerasi.ws.notificaInAttesaPrelievo.dto.NotificaInAttesaPrelievoExt;
import it.eng.sacerasi.ws.notificaPrelievo.dto.NotificaPrelievoExt;
import it.eng.sacerasi.ws.notificaTrasferimento.dto.FileDepositatoRespType;
import it.eng.sacerasi.ws.notificaTrasferimento.dto.NotificaTrasferimentoExt;
import it.eng.sacerasi.ws.notificaTrasferimento.dto.RispostaNotificaWS;
import it.eng.sacerasi.ws.puliziaNotificato.dto.PuliziaNotificatoExt;
import it.eng.sacerasi.ws.richiestaRestituzioneOggetto.dto.RichiestaRestituzioneOggettoExt;
import it.eng.sacerasi.messages.MessaggiWSBundle;
import it.eng.sacerasi.ws.invioOggettoAsincrono.dto.InvioOggettoAsincronoInput;
import it.eng.sacerasi.ws.xml.datiSpecDicom.DatiSpecificiType;
import java.io.File;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
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
import javax.persistence.Query;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import it.eng.parer.objectstorage.helper.SalvataggioBackendHelper;
import it.eng.parer.objectstorage.dto.ObjectStorageBackend;
import it.eng.parer.objectstorage.exceptions.ObjectStorageException;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author Bonora_L
 */
@Stateless(mappedName = "SalvataggioDati")
@LocalBean
public class SalvataggioDati {

    private static final Logger log = LoggerFactory.getLogger(SalvataggioDati.class);
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
    private static final String ERRORE_UPDATE_SESSIONE = "Eccezione nell'update della sessione :";
    private static final String ERRORE_UPDATE_OGGETTO = "Eccezione nell'update dell'oggetto :";
    private static final String ERRORE_PERSISTENZA_OGGETTO = "Eccezione nella persistenza dell'oggetto :";
    @PersistenceContext(unitName = "SacerAsiJPA")
    private EntityManager entityManager;

    @EJB
    private SalvataggioBackendHelper salvataggioBackendHelper;

    public void deleteFileObject(NotificaTrasferimentoExt nte) {
        // elimino tutti i file dalla tabella PIG_FILE_OBJECT per l’oggetto corrente
        PigObject obj = entityManager.find(PigObject.class, nte.getIdObject());
        javax.persistence.Query q = entityManager
                .createQuery("DELETE FROM PigFileObject fileObj WHERE fileObj.pigObject.idObject = :objId");
        q.setParameter("objId", nte.getIdObject());
        q.executeUpdate();
        entityManager.refresh(obj);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public RispostaControlli creaFileObjects(NotificaTrasferimentoExt nte, RispostaNotificaWS rispostaWs)
            throws ObjectStorageException {
        RispostaControlli rispostaControlli = new RispostaControlli();

        for (FileDepositatoRespType fileDepRest : rispostaWs.getNotificaResponse().getListaFileDepositati()
                .getFileDepositato()) {

            long size = 0L;

            // MEV21995 recupera la dimensione del file su object storage
            if (salvataggioBackendHelper.isActive() && fileDepRest.getNmOsBucket() != null
                    && fileDepRest.getNmNomeFileOs() != null) {
                ObjectStorageBackend config = salvataggioBackendHelper.getObjectStorageConfiguration("VERS_OGGETTO",
                        fileDepRest.getNmOsBucket());
                size = salvataggioBackendHelper.getObjectSize(config, fileDepRest.getNmNomeFileOs());
            } else {
                File file = new File(nte.getFtpPath() + File.separator + fileDepRest.getNmNomeFile());
                if (file.exists() && file.isFile()) {
                    size = FileUtils.sizeOf(file);
                }
            }

            rispostaControlli = salvaFileObject(nte, fileDepRest, size);
            if (!rispostaControlli.isrBoolean()) {
                break;
            }
        }

        return rispostaControlli;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    private RispostaControlli salvaFileObject(NotificaTrasferimentoExt nte, FileDepositatoRespType fileDepRest,
            long size) {
        RispostaControlli risp = new RispostaControlli();
        risp.setrBoolean(true);

        PigFileObject fiObj;
        PigObject obj = entityManager.find(PigObject.class, nte.getIdObject());

        boolean persist = true;
        // Verifico che il file object non esista già a causa di un precedente versamento (Caso warning)
        // Nel qual caso eseguo la modifica per il file object trovato
        if ((fiObj = getPigFileObject(nte.getIdObject(), fileDepRest.getNmNomeFile())) != null) {
            persist = false;
        } else {
            fiObj = new PigFileObject();
            fiObj.setPigObject(obj);
            obj.getPigFileObjects().add(fiObj);
            fiObj.setNmFileObject(fileDepRest.getNmNomeFile());
        }
        fiObj.setCdEncodingHashFileVers(fileDepRest.getCdEncoding());
        fiObj.setDsHashFileVers(fileDepRest.getDsHashFile());
        fiObj.setTiAlgoHashFileVers(fileDepRest.getTiAlgoritmoHash());
        fiObj.setPigTipoFileObject(
                entityManager.find(PigTipoFileObject.class, nte.getTipoFileObjects().get(fileDepRest.getNmNomeFile())));
        fiObj.setNiSizeFileVers(new BigDecimal(size));

        // MEV21995 Aggiungo anche le eventuali info di object storage
        fiObj.setNmBucket(fileDepRest.getNmOsBucket());
        fiObj.setCdKeyFile(fileDepRest.getNmNomeFileOs());
        fiObj.setIdVers(obj.getPigVer().getIdVers());

        try {
            if (persist) {
                entityManager.persist(fiObj);
            }
            entityManager.flush();
        } catch (RuntimeException re) {
            /// logga l'errore e blocca tutto
            log.error("Eccezione nella persistenza del file object ", re);
            risp.setrBoolean(false);
            risp.setCodErr(MessaggiWSBundle.ERR_666);
            risp.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_PERSISTENCE,
                    String.join("\n", ExceptionUtils.getRootCauseStackTrace(re))));
        }

        return risp;
    }

    @SuppressWarnings("unchecked")
    private PigFileObject getPigFileObject(Long idObject, String nmFileObject) {
        PigFileObject fileObj = null;

        String queryStr = "SELECT u FROM PigFileObject u WHERE u.pigObject.idObject = :idObject "
                + "AND u.nmFileObject = :nmFileObject";
        javax.persistence.Query query = entityManager.createQuery(queryStr);
        query.setParameter("idObject", idObject);
        query.setParameter("nmFileObject", nmFileObject);
        List<PigFileObject> fileObjList = query.getResultList();
        if (!fileObjList.isEmpty()) {
            fileObj = fileObjList.get(0);
        }

        return fileObj;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public RispostaControlli modificaSessione(Long idLastSession, StatoSessioneIngest stato, String cdErr,
            String dsErr) {
        RispostaControlli risp = new RispostaControlli();
        risp.setrBoolean(true);
        try {
            PigSessioneIngest sessione = entityManager.find(PigSessioneIngest.class, idLastSession);
            if (stato != null) {
                sessione.setTiStato(stato.name());
            }
            if (cdErr != null || stato == Constants.StatoSessioneIngest.CHIUSO_FORZATA) {
                if (cdErr != null) {
                    sessione.setCdErr(cdErr);
                }
                if (sessione.getDtChiusura() == null) {
                    Date now = Calendar.getInstance().getTime();
                    sessione.setDtChiusura(now);
                    risp.setrDate(now);
                }
            }
            if (dsErr != null) {
                // In caso di errore, setto la descrizione e il flag di sessione errata verificata a false
                sessione.setDlErr(dsErr);
                sessione.setFlSesErrVerif(Constants.DB_FALSE);
            }
            entityManager.flush();
        } catch (Exception e) {
            log.error(ERRORE_UPDATE_SESSIONE, e);
            risp.setrBoolean(false);
            risp.setCodErr(MessaggiWSBundle.ERR_666);
            risp.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_UPDATE,
                    String.join("\n", ExceptionUtils.getRootCauseStackTrace(e))));
        }
        return risp;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public RispostaControlli modificaSessione(Long idLastSession, StatoOggetto stato, String dlMotivoChiusoWarning,
            boolean setDtChius) {
        RispostaControlli risp = new RispostaControlli();
        risp.setrBoolean(true);
        try {
            PigSessioneIngest sessione = entityManager.find(PigSessioneIngest.class, idLastSession);
            if (stato != null) {
                sessione.setTiStato(stato.name());
            }
            if (setDtChius && sessione.getDtChiusura() == null) {
                Date now = Calendar.getInstance().getTime();
                sessione.setDtChiusura(now);
                risp.setrDate(now);
            }
            if (dlMotivoChiusoWarning != null) {
                sessione.setDlMotivoChiusoWarning(dlMotivoChiusoWarning);
            }
            entityManager.flush();
        } catch (Exception e) {
            log.error(ERRORE_UPDATE_SESSIONE, e);
            risp.setrBoolean(false);
            risp.setCodErr(MessaggiWSBundle.ERR_666);
            risp.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_OBJECT));
        }
        return risp;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public RispostaControlli modificaSessione(Long idLastSession, Long idObject) {
        RispostaControlli risp = new RispostaControlli();
        risp.setrBoolean(true);
        try {
            PigSessioneIngest sessione = entityManager.find(PigSessioneIngest.class, idLastSession);
            if (idObject != null) {
                sessione.setPigObject(entityManager.find(PigObject.class, idObject));
            }
            entityManager.flush();
        } catch (Exception e) {
            log.error(ERRORE_UPDATE_SESSIONE, e);
            risp.setrBoolean(false);
            risp.setCodErr(MessaggiWSBundle.ERR_666);
            risp.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_OBJECT));
        }
        return risp;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public RispostaControlli modificaOggetto(Long idOggetto, StatoOggetto stato) {
        RispostaControlli risp = new RispostaControlli();
        risp.setrBoolean(true);
        try {
            PigObject oggetto = entityManager.find(PigObject.class, idOggetto);
            if (stato != null) {
                oggetto.setTiStatoObject(stato.name());
            }
            entityManager.flush();
        } catch (Exception e) {
            log.error(ERRORE_UPDATE_OGGETTO, e);
            risp.setrBoolean(false);
            risp.setCodErr(MessaggiWSBundle.ERR_666);
            risp.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_UPDATE,
                    String.join("\n", ExceptionUtils.getRootCauseStackTrace(e))));
        }
        return risp;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public RispostaControlli modificaOggetto(Long idOggetto, StatoSessioneIngest stato, Long idLastSession,
            String username, Long idOggettoPadre, BigDecimal pgOggettoTrasf, BigDecimal niUnitaDocAttese,
            String dsObject, String cdVersGen, String tiGestOggettiFigli) {
        RispostaControlli risp = new RispostaControlli();
        risp.setrBoolean(true);
        try {
            PigObject oggetto = entityManager.find(PigObject.class, idOggetto);
            if (stato != null) {
                oggetto.setTiStatoObject(stato.name());
            }
            if (idLastSession != null) {
                oggetto.setIdLastSessioneIngest(new BigDecimal(idLastSession));
            }
            IamUser user = getIamUser(username);
            oggetto.setIamUser(user);

            if (idOggettoPadre != null) {
                PigObject oggettoPadre = entityManager.find(PigObject.class, idOggettoPadre);
                oggetto.setPigObjectPadre(oggettoPadre);
            }
            oggetto.setPgOggettoTrasf(pgOggettoTrasf);
            oggetto.setNiUnitaDocAttese(niUnitaDocAttese);
            oggetto.setDsObject(dsObject);
            oggetto.setCdVersGen(cdVersGen);
            oggetto.setTiGestOggettiFigli(tiGestOggettiFigli);

            entityManager.flush();
        } catch (Exception e) {
            log.error(ERRORE_UPDATE_OGGETTO, e);
            risp.setrBoolean(false);
            risp.setCodErr(MessaggiWSBundle.ERR_666);
            risp.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_OBJECT));
        }
        return risp;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public RispostaControlli creaSessione(NotificaTrasferimentoExt nte, String cdErr, String dsErr) {
        RispostaControlli risp = new RispostaControlli();
        risp.setrBoolean(true);

        PigSessioneIngest sessione = new PigSessioneIngest();
        sessione.setTiStato(StatoOggetto.CHIUSO_ERR_NOTIF.name());
        if (nte.getIdVersatore() != null) {
            sessione.setPigVer(entityManager.find(PigVers.class, nte.getIdVersatore()));
        }
        sessione.setDtApertura(Calendar.getInstance().getTime());
        sessione.setDtChiusura(Calendar.getInstance().getTime());
        sessione.setCdKeyObject(nte.getNotificaTrasf().getCdKeyObject());
        sessione.setNmAmbienteVers(nte.getNotificaTrasf().getNmAmbiente());
        sessione.setNmVers(nte.getNotificaTrasf().getNmVersatore());
        // Sto mettendo la sessione per forza in errore, perciò setto il flag sessione verificata
        sessione.setFlSesErrVerif(Constants.DB_FALSE);
        sessione.setCdErr(cdErr);
        sessione.setDlErr(dsErr);

        try {
            entityManager.persist(sessione);
            entityManager.flush();

            risp.setrLong(sessione.getIdSessioneIngest());
            risp.setrDate(sessione.getDtApertura());
        } catch (Exception e) {
            log.error(ERRORE_PERSISTENZA_OGGETTO, e);
            risp.setrBoolean(false);
            risp.setCodErr(MessaggiWSBundle.ERR_666);
            risp.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_PERSISTENCE,
                    String.join("\n", ExceptionUtils.getRootCauseStackTrace(e))));
        }

        return risp;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public RispostaControlli creaSessione(InvioOggettoAsincronoExt ioae, String cdErr, String dsErr) {
        RispostaControlli risp = new RispostaControlli();
        risp.setrBoolean(true);
        PigSessioneIngest sessione = new PigSessioneIngest();
        sessione.setTiStato(ioae.getStatoSessione().name());

        if (ioae.getIdVersatore() != null) {
            sessione.setPigVer(entityManager.find(PigVers.class, ioae.getIdVersatore()));
        }
        sessione.setDtApertura(ioae.getDtApertura());
        sessione.setDtChiusura(ioae.getDtChiusura());
        sessione.setCdKeyObject(ioae.getInvioOggettoAsincronoInput().getCdKeyObject());
        sessione.setNmAmbienteVers(ioae.getInvioOggettoAsincronoInput().getNmAmbiente());
        sessione.setNmVers(ioae.getInvioOggettoAsincronoInput().getNmVersatore());
        sessione.setNmTipoObject(ioae.getNmTipoObject());
        sessione.setFlFileCifrato(
                ioae.getInvioOggettoAsincronoInput().isFlFileCifrato() ? Constants.DB_TRUE : Constants.DB_FALSE);
        sessione.setFlForzaAccettazione(
                ioae.getInvioOggettoAsincronoInput().isFlForzaAccettazione() ? Constants.DB_TRUE : Constants.DB_FALSE);
        sessione.setFlForzaWarning(
                ioae.getInvioOggettoAsincronoInput().isFlForzaWarning() ? Constants.DB_TRUE : Constants.DB_FALSE);
        sessione.setDlMotivoForzaAccettazione(ioae.getInvioOggettoAsincronoInput().getDlMotivazione());
        sessione.setCdVersioneXmlVers(ioae.getInvioOggettoAsincronoInput().getCdVersioneXml());
        sessione.setCdErr(cdErr);
        sessione.setDlErr(dsErr);
        if (ioae.getInvioOggettoAsincronoInput() instanceof InvioOggettoAsincronoEstesoInput) {
            InvioOggettoAsincronoEstesoInput input = (InvioOggettoAsincronoEstesoInput) ioae
                    .getInvioOggettoAsincronoInput();
            sessione.setNmAmbienteVersPadre(input.getNmAmbienteObjectPadre());
            sessione.setNmVersPadre(input.getNmVersatoreObjectPadre());
            sessione.setCdKeyObjectPadre(input.getCdKeyObjectPadre());
            sessione.setDsObject(input.getDsObject());
            sessione.setNiTotObjectTrasf(input.getNiTotObjectFigli());
            sessione.setNiUnitaDocAttese(input.getNiUnitaDocAttese());
            sessione.setPgOggettoTrasf(input.getPgObjectFiglio());
            sessione.setCdVersGen(ioae.getCdVersGen());
        }
        /*
         * Il parametro di tipo gestione oggetti figli, dato che un oggetto DA_TRASFORMARE può essere inviato anche da
         * InvioOggettoAsincrono standard, deve essere gestito fuori dal controllo di sopra
         */
        sessione.setTiGestOggettiFigli(ioae.getTiGestOggettiFigli());
        if (cdErr != null) {
            // Se sono in stato di errore, allora setto il flag sessione verificata a false
            sessione.setFlSesErrVerif(Constants.DB_FALSE);
        }
        /*
         * MEV #12941 - WS Invio oggetto: estendere controlli semantici in riferimento al Tipo oggetto/Tipo SIP versato
         */
        controlliMev12941(sessione, ioae.getTiVersFile(), ioae.getNmTipoObject());
        try {
            entityManager.persist(sessione);
            entityManager.flush();
            risp.setrLong(sessione.getIdSessioneIngest());
        } catch (Exception e) {
            log.error(ERRORE_PERSISTENZA_OGGETTO, e);
            risp.setrBoolean(false);
            risp.setCodErr(MessaggiWSBundle.ERR_666);
            risp.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_SESSIONE));
        }
        return risp;
    }

    /*
     * MEV #12941 - WS Invio oggetto: estendere controlli semantici in riferimento al Tipo oggetto/Tipo SIP versato
     * Imposta gli oppurtuni flag della sessione secondo quanto richiesto dalla MEV.
     */
    private void controlliMev12941(PigSessioneIngest sessione, String tiVersFile, String nmTipoObject) {
        if (tiVersFile != null) {
            if ((tiVersFile.equals(Constants.TipoVersamento.DA_TRASFORMARE.name())
                    || tiVersFile.equals(Constants.TipoVersamento.ZIP_CON_XML_SACER.name())
                    || tiVersFile.equals(Constants.TipoVersamento.ZIP_NO_XML_SACER.name())
                    || tiVersFile.equals(Constants.TipoVersamento.NO_ZIP.name()))
                    && nmTipoObject.equals(Constants.TipiOggetto.STUDIO_DICOM.name())) {
                sessione.setFlFileCifrato(null);
                sessione.setFlForzaWarning(null);
                sessione.setFlForzaAccettazione(null);
                sessione.setDlMotivoForzaAccettazione(null);
            }
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public RispostaControlli creaStatoSessione(long idSessioneIngest, String statoSessione, Date dtRegStato) {
        RispostaControlli risp = new RispostaControlli();
        risp.setrBoolean(true);
        PigStatoSessioneIngest pigStatoSessione = new PigStatoSessioneIngest();
        final PigSessioneIngest pigSessioneIngest = entityManager.find(PigSessioneIngest.class, idSessioneIngest);
        pigStatoSessione.setPigSessioneIngest(pigSessioneIngest);
        pigStatoSessione.setIdVers(pigSessioneIngest.getPigVer().getIdVers());
        pigStatoSessione.setTiStato(statoSessione);
        pigStatoSessione.setTsRegStato(dtRegStato);
        try {
            entityManager.persist(pigStatoSessione);
            entityManager.flush();
            risp.setrLong(pigStatoSessione.getIdStatoSessioneIngest());

            pigSessioneIngest.setIdStatoSessioneIngestCor(new BigDecimal(pigStatoSessione.getIdStatoSessioneIngest()));
        } catch (Exception e) {
            log.error(ERRORE_PERSISTENZA_OGGETTO, e);
            risp.setrBoolean(false);
            risp.setCodErr(MessaggiWSBundle.ERR_666);
            risp.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_SESSIONE));
        }
        return risp;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public RispostaControlli creaXmlSessione(Long idSessione, String xml) {
        RispostaControlli risp = new RispostaControlli();
        risp.setrBoolean(true);

        PigXmlSessioneIngest xmlSessione = new PigXmlSessioneIngest();
        final PigSessioneIngest pigSessioneIngest = entityManager.find(PigSessioneIngest.class, idSessione);
        xmlSessione.setPigSessioneIngest(pigSessioneIngest);
        xmlSessione.setIdVers(pigSessioneIngest.getPigVer().getIdVers());
        xmlSessione.setBlXml(xml);

        try {
            entityManager.persist(xmlSessione);
            entityManager.flush();
        } catch (Exception e) {
            log.error(ERRORE_PERSISTENZA_OGGETTO, e);
            risp.setrBoolean(false);
            risp.setCodErr(MessaggiWSBundle.ERR_666);
            risp.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_SESSIONE));
        }

        return risp;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public RispostaControlli creaOggetto(InvioOggettoAsincronoExt ioaExt, Long idSessione, String username) {
        RispostaControlli risp = new RispostaControlli();
        risp.setrBoolean(true);

        PigObject obj = new PigObject();
        PigSessioneIngest sessione = entityManager.find(PigSessioneIngest.class, idSessione);
        obj.setIdLastSessioneIngest(new BigDecimal(idSessione));
        PigTipoObject pigTipoObject = entityManager.find(PigTipoObject.class, ioaExt.getIdTipoObject());
        obj.setPigTipoObject(pigTipoObject);
        obj.setPigVer(entityManager.find(PigVers.class, ioaExt.getIdVersatore()));
        obj.setTiStatoObject(sessione.getTiStato());
        obj.setCdKeyObject(ioaExt.getInvioOggettoAsincronoInput().getCdKeyObject());
        obj.setMmFirstSes(new BigDecimal(sdf.format(sessione.getDtApertura())));

        // MEV#27321 - Introduzione della priorità di versamento di un oggetto ZIP_CON_XML_SACER e NO_ZIP
        // Se la priorità di versamento viene passata in input usa quella per l'oggetto che si sta creando
        // altrimenti copia la priorita di versamento prelevata dal tipo Object
        if (ioaExt.getInvioOggettoAsincronoInput().getTiPrioritaVersamento() == null) {
            obj.impostaPrioritaVersamento(pigTipoObject.getTiPrioritaVersamento(), username);
        } else {
            obj.impostaPrioritaVersamento(it.eng.sacerasi.web.util.Constants.ComboFlagPrioVersType
                    .getValueByEnumName(ioaExt.getInvioOggettoAsincronoInput().getTiPrioritaVersamento()), username);
        }

        if (ioaExt.getInvioOggettoAsincronoInput() instanceof InvioOggettoAsincronoEstesoInput) {
            InvioOggettoAsincronoEstesoInput input = (InvioOggettoAsincronoEstesoInput) ioaExt
                    .getInvioOggettoAsincronoInput();
            obj.setDsObject(input.getDsObject());
            if (ioaExt.getIdOggettoPadre() != null) {
                obj.setPigObjectPadre(entityManager.find(PigObject.class, ioaExt.getIdOggettoPadre()));
            }
            obj.setPgOggettoTrasf(input.getPgObjectFiglio());
            obj.setNiUnitaDocAttese(input.getNiUnitaDocAttese());
            obj.setDsObject(input.getDsObject());
            obj.setCdVersGen(ioaExt.getCdVersGen());
            // RAMO INSERITO PER LA MAC #14809 - WS invio oggetto: non viene calcolato il versatore per cui generare
            // oggetti
            // Anche nel caso di invio oggetto ridotto quando il CD_GEN viene calcolato deve essere aggiornato il codice
            // sull'Oggetto

            // gestione della priorità della trasformazione MEV #19428
            if (input.getTiPriorita() != null) {
                obj.setTiPriorita(it.eng.sacerasi.web.util.Constants.ComboFlagPrioTrasfType
                        .valueOf(input.getTiPriorita()).getValue());
            } else if (ioaExt.getTiVersFile().equals(Constants.TipoVersamento.DA_TRASFORMARE.name())) {
                // MAC #20989: Se la priporità non è specificata controllare che esista sul tipoOggetto e settare
                // quella,
                // altrimenti errore se l'oggettto è trasformare.
                if (obj.getPigTipoObject().getTiPriorita() != null) {
                    obj.setTiPriorita(obj.getPigTipoObject().getTiPriorita());
                } else {
                    risp.setrBoolean(false);
                    risp.setCodErr(MessaggiWSBundle.ERR_666);
                    risp.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_TIPOOBJECT_PRIORITA_CHECK));
                }
            } else {
                /* SE LA PRIORITA' NON VIENE PASSATA DALL'ESTERNO IL SISTEMA LA PRELEVA DAL TIPO OGGETTO! */
                obj.setTiPriorita(pigTipoObject.getTiPriorita());
            }
        } else if (ioaExt.getInvioOggettoAsincronoInput() instanceof InvioOggettoAsincronoInput) {
            obj.setCdVersGen(ioaExt.getCdVersGen());
            /* LA PRIORITA' E' PRELEVATA PER DEFAULT DAL TIPO OGGETTO! */
            obj.setTiPriorita(pigTipoObject.getTiPriorita());
        }
        /*
         * Il parametro di tipo gestione oggetti figli, dato che un oggetto DA_TRASFORMARE può essere inviato anche da
         * InvioOggettoAsincrono standard, deve essere gestito fuori dal controllo di sopra
         */
        obj.setTiGestOggettiFigli(ioaExt.getTiGestOggettiFigli());
        try {
            IamUser user = getIamUser(username);
            obj.setIamUser(user);

            entityManager.persist(obj);
            entityManager.flush();

            risp.setrLong(obj.getIdObject());
        } catch (Exception e) {
            log.error(ERRORE_PERSISTENZA_OGGETTO, e);
            risp.setrBoolean(false);
            risp.setCodErr(MessaggiWSBundle.ERR_666);
            risp.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_OBJECT));
        }

        return risp;
    }

    @SuppressWarnings("unchecked")
    private IamUser getIamUser(String username) throws ParerInternalError {
        Query query = entityManager
                .createQuery("SELECT u FROM IamUser u WHERE u.nmUserid = :nmUserid AND u.flAttivo = '1'");
        query.setParameter("nmUserid", username);
        List<IamUser> userList = query.getResultList();
        IamUser user;
        if (userList != null && !userList.isEmpty()) {
            user = userList.get(0);
        } else {
            throw new ParerInternalError("Errore inatteso: Impossibile trovare l'utente " + username);
        }
        return user;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public RispostaControlli updateOggettoPadre(Long idObjectPadre, BigDecimal niTotObjectTrasf) {
        RispostaControlli risp = new RispostaControlli();
        risp.setrBoolean(true);
        try {
            PigObject obj = entityManager.find(PigObject.class, idObjectPadre);
            if (obj.getNiTotObjectTrasf() == null) {
                obj.setNiTotObjectTrasf(niTotObjectTrasf);
            }
            entityManager.flush();
        } catch (Exception e) {
            log.error(ERRORE_UPDATE_OGGETTO, e);
            risp.setrBoolean(false);
            risp.setCodErr(MessaggiWSBundle.ERR_666);
            risp.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_OBJECT));
        }
        return risp;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public RispostaControlli creaXmlObject(Long idObject, String xml, String cdVersioneXml) {
        RispostaControlli risp = new RispostaControlli();
        risp.setrBoolean(true);

        PigObject obj = entityManager.find(PigObject.class, idObject);

        PigXmlObject xmlObj = new PigXmlObject();
        xmlObj.setBlXml(xml);
        xmlObj.setPigObject(obj);
        xmlObj.setCdVersioneXmlVers(cdVersioneXml);
        xmlObj.setIdVers(obj.getPigVer().getIdVers());
        if (obj.getPigXmlObjects() == null) {
            obj.setPigXmlObjects(new ArrayList<>());
        }
        obj.getPigXmlObjects().add(xmlObj);

        try {
            entityManager.persist(xmlObj);
            entityManager.flush();

            risp.setrLong(xmlObj.getIdXmlObject());
        } catch (Exception e) {
            log.error(ERRORE_PERSISTENZA_OGGETTO, e);
            risp.setrBoolean(false);
            risp.setCodErr(MessaggiWSBundle.ERR_666);
            risp.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_OBJECT));
        }

        return risp;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public RispostaControlli modificaXmlObject(Long idObject, String xml, String cdVersioneXml) {
        RispostaControlli risp = new RispostaControlli();
        risp.setrBoolean(true);
        try {
            PigObject obj = entityManager.find(PigObject.class, idObject);
            // Può esserci solo un PigXmlObject collegato al PigObject
            PigXmlObject xmlObj = obj.getPigXmlObjects().get(0);
            xmlObj.setCdVersioneXmlVers(cdVersioneXml);
            xmlObj.setBlXml(xml);

            entityManager.flush();
        } catch (Exception e) {
            log.error(ERRORE_UPDATE_OGGETTO, e);
            risp.setrBoolean(false);
            risp.setCodErr(MessaggiWSBundle.ERR_666);
            risp.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_OBJECT));
        }
        return risp;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public RispostaControlli salvaInfoDicom(Long idObject, DatiSpecificiType datiSpec, Long idXsdDatiSpec) {
        RispostaControlli risp = new RispostaControlli();
        risp.setrBoolean(true);

        PigObject obj = entityManager.find(PigObject.class, idObject);
        PigInfoDicom infoDicom = new PigInfoDicom();
        boolean persist = true;
        if (obj.getPigInfoDicoms() != null && !obj.getPigInfoDicoms().isEmpty()
                && obj.getPigInfoDicoms().get(0) != null) {
            infoDicom = obj.getPigInfoDicoms().get(0);
            persist = false;
        } else {
            infoDicom.setPigObject(obj);
            obj.getPigInfoDicoms().add(infoDicom);
        }

        infoDicom.setPigXsdDatiSpec(entityManager.find(PigXsdDatiSpec.class, idXsdDatiSpec));
        infoDicom.setBlDcmHashTxt(
                new String(Base64.decodeBase64(datiSpec.getDCMHashDescrizione()), StandardCharsets.UTF_8));
        infoDicom.setBlGlobalHashTxt(
                new String(Base64.decodeBase64(datiSpec.getGLOBALHashDescrizione()), StandardCharsets.UTF_8));

        infoDicom.setCdAetNodoDicom(datiSpec.getAETNodoDicom());
        infoDicom.setCdEncodingDcmHash(datiSpec.getDCMHashEncoding());
        infoDicom.setCdEncodingFileHash(datiSpec.getFILEHashEncoding());
        infoDicom.setCdEncodingGlobalHash(datiSpec.getGLOBALHashEncoding());
        infoDicom.setCdPatientId(datiSpec.getPatientId());
        infoDicom.setCdPatientIdIssuer(datiSpec.getPatientIdIssuer());
        infoDicom.setCdVersioneDatiSpecDicom(datiSpec.getVersioneDatiSpecifici());

        infoDicom.setDlListaModalityInStudy(datiSpec.getModalityInStudyList() != null
                && datiSpec.getModalityInStudyList().getModalityInStudy() != null
                && !datiSpec.getModalityInStudyList().getModalityInStudy().isEmpty()
                        ? buildDatiSpecLists(datiSpec.getModalityInStudyList().getModalityInStudy()) : null);
        infoDicom.setDlListaSopClass(
                datiSpec.getSOPClassList() != null && datiSpec.getSOPClassList().getSOPClass() != null
                        && !datiSpec.getSOPClassList().getSOPClass().isEmpty()
                                ? buildDatiSpecLists(datiSpec.getSOPClassList().getSOPClass()) : null);
        infoDicom.setDlStudyDescription(datiSpec.getStudyDescription());
        infoDicom.setDsAccessionNumber(datiSpec.getAccessionNumber());
        infoDicom.setDsDcmHash(datiSpec.getDCMHash());
        infoDicom.setDsFileHash(datiSpec.getFILEHash());
        infoDicom.setDsGlobalHash(datiSpec.getGLOBALHash());
        infoDicom.setDsInstitutionName(datiSpec.getInstitutionName());
        infoDicom.setDsRefPhysicianName(datiSpec.getReferringPhysicianName());
        infoDicom.setDsPatientName(datiSpec.getPatientName());
        infoDicom.setDsStudyId(datiSpec.getStudyID());
        infoDicom.setDsStudyInstanceUid(datiSpec.getStudyInstanceUID());
        infoDicom.setDtPatientBirthDate(datiSpec.getPatientBirthDate() != null
                ? datiSpec.getPatientBirthDate().toGregorianCalendar().getTime() : null);
        infoDicom.setDtPresaInCarico(datiSpec.getDataPresaInCarico().toGregorianCalendar().getTime());
        infoDicom.setDtStudyDate(datiSpec.getStudyDate().toGregorianCalendar().getTime());
        infoDicom.setIdVers(new BigDecimal(obj.getPigVer().getIdVers()));
        infoDicom.setNiStudyRelatedImages(new BigDecimal(datiSpec.getNumberStudyRelatedImages()));
        infoDicom.setNiStudyRelatedSeries(new BigDecimal(datiSpec.getNumberStudyRelatedSeries()));
        infoDicom.setTiAlgoDcmHash(datiSpec.getDCMHashAlgo());
        infoDicom.setTiAlgoFileHash(datiSpec.getFILEHashAlgo());
        infoDicom.setTiAlgoGlobalHash(datiSpec.getGLOBALHashAlgo());
        infoDicom.setTiPatientSex(datiSpec.getPatientSex() != null ? datiSpec.getPatientSex() : null);

        try {
            if (persist) {
                entityManager.persist(infoDicom);
            }
            entityManager.flush();
        } catch (Exception e) {
            log.error(ERRORE_PERSISTENZA_OGGETTO, e);
            risp.setrBoolean(false);
            risp.setCodErr(MessaggiWSBundle.ERR_666);
            risp.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_OBJECT));
            if (StringUtils.isBlank(infoDicom.getDsPatientName())) {
                log.error(
                        "Eccezione nella persistenza dell'oggetto : \n" + "CD_AET_NODO_DICOM: {} \n"
                                + "CD_PATIENT_ID: {} \n" + "CD_PATIENT_ID_ISSUER: {} \n"
                                + "CD_VERSIONE_DATI_SPEC_DICOM: {} \n" + "DL_LISTA_MODALITY_IN_STUDY: {} \n"
                                + "DL_LISTA_SOP_CLASS: {} \n" + "DL_STUDY_DESCRIPTION: {} \n"
                                + "DS_ACCESSION_NUMBER: {} \n" + "DS_DCM_HASH: {} \n" + "DS_FILE_HASH: {} \n"
                                + "DS_GLOBAL_HASH: {} \n" + "DS_INSTITUTION_NAME: {} \n" + "DS_PATIENT_NAME: {} \n"
                                + "DS_REF_PHYSICIAN_NAME: {} \n" + "DS_STUDY_ID: {} \n" + "DS_STUDY_INSTANCE_UID: {} \n"
                                + "DT_PATIENT_BIRTH_DATE: {} \n" + "DT_PRESA_IN_CARICO: {} \n" + "DT_STUDY_DATE: {} \n"
                                + "ID_VERS: {} \n" + "NI_STUDY_RELATED_IMAGES: {} \n" + "NI_STUDY_RELATED_SERIES: {} \n"
                                + "ID_OBJECT: {} \n" + "ID_XSD_SPEC: {} \n" + "TI_PATIENT_SEX: {} \n"
                                + "ID_INFO_DICOM: {} \n" + "BL_DCM_HASH_TXT: {} \n" + "BL_GLOBAL_HASH_TXT: {}",
                        infoDicom.getCdAetNodoDicom(), infoDicom.getCdPatientId(), infoDicom.getCdPatientIdIssuer(),
                        infoDicom.getCdVersioneDatiSpecDicom(), infoDicom.getDlListaModalityInStudy(),
                        infoDicom.getDlListaSopClass(), infoDicom.getDlStudyDescription(),
                        infoDicom.getDsAccessionNumber(), infoDicom.getDsDcmHash(), infoDicom.getDsFileHash(),
                        infoDicom.getDsGlobalHash(), infoDicom.getDsInstitutionName(), infoDicom.getDsPatientName(),
                        infoDicom.getDsRefPhysicianName(), infoDicom.getDsStudyId(), infoDicom.getDsStudyInstanceUid(),
                        infoDicom.getDtPatientBirthDate(), infoDicom.getDtPresaInCarico(), infoDicom.getDtStudyDate(),
                        infoDicom.getIdVers(), infoDicom.getNiStudyRelatedImages(), infoDicom.getNiStudyRelatedSeries(),
                        idObject, idXsdDatiSpec, infoDicom.getTiPatientSex(), infoDicom.getIdInfoDicom(),
                        infoDicom.getBlDcmHashTxt(), infoDicom.getBlGlobalHashTxt());
            }
        }
        return risp;
    }

    private String buildDatiSpecLists(List<String> collection) {
        StringBuilder builder = new StringBuilder();
        for (String element : collection) {
            builder.append(element).append(Constants.DICOM_SEPARATOR);
        }
        // Elimino l'ultima virgola
        builder.deleteCharAt(builder.length() - 1);
        return (builder.toString().length() > Constants.MAX_BYTES_DICOM_SIZE)
                ? builder.substring(0, Constants.MAX_BYTES_DICOM_SIZE) : builder.toString();
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public RispostaControlli creaSessioneRecupero(RichiestaRestituzioneOggettoExt rroExt, String cdErr, String dsErr) {
        RispostaControlli risp = new RispostaControlli();
        risp.setrBoolean(true);
        PigSessioneRecup sessione = new PigSessioneRecup();
        sessione.setCdKeyObject(rroExt.getRichiestaRestituzioneOggettoInput().getCdKeyObject());
        sessione.setDtApertura(rroExt.getDtApertura());
        sessione.setDtChiusura(cdErr != null ? Calendar.getInstance().getTime() : null);
        sessione.setNmAmbienteVers(rroExt.getRichiestaRestituzioneOggettoInput().getNmAmbiente());
        sessione.setNmVers(rroExt.getRichiestaRestituzioneOggettoInput().getNmVersatore());
        if (rroExt.getIdObject() != null) {
            sessione.setPigObject(entityManager.find(PigObject.class, rroExt.getIdObject()));
        }
        if (rroExt.getIdVersatore() != null) {
            sessione.setPigVer(entityManager.find(PigVers.class, rroExt.getIdVersatore()));
        }
        sessione.setTiStato(rroExt.getStatoSessione().name());

        sessione.setCdErr(cdErr);
        sessione.setDlErr(dsErr);

        try {
            entityManager.persist(sessione);
            entityManager.flush();
            risp.setrLong(sessione.getIdSessioneRecup());
        } catch (Exception e) {
            log.error(ERRORE_PERSISTENZA_OGGETTO, e);
            risp.setrBoolean(false);
            risp.setCodErr(MessaggiWSBundle.ERR_666);
            risp.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_SESSIONE_RECUP));
        }
        return risp;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public RispostaControlli creaSessioneRecupero(NotificaPrelievoExt npExt, String errorCode, String errorMessage) {
        RispostaControlli risp = new RispostaControlli();
        risp.setrBoolean(true);
        PigSessioneRecup sessione = new PigSessioneRecup();
        sessione.setCdKeyObject(npExt.getNotificaPrelievoInput().getCdKeyObject());
        sessione.setDtApertura(npExt.getDtApertura());
        sessione.setDtChiusura(npExt.getDtChiusura());
        sessione.setNmAmbienteVers(npExt.getNotificaPrelievoInput().getNmAmbiente());
        sessione.setNmVers(npExt.getNotificaPrelievoInput().getNmVersatore());
        if (npExt.getIdObject() != null) {
            sessione.setPigObject(entityManager.find(PigObject.class, npExt.getIdObject()));
        }
        if (npExt.getIdVersatore() != null) {
            sessione.setPigVer(entityManager.find(PigVers.class, npExt.getIdVersatore()));
        }
        sessione.setTiStato(npExt.getStatoSessione().name());

        sessione.setCdErr(errorCode);
        sessione.setDlErr(errorMessage);

        try {
            entityManager.persist(sessione);
            entityManager.flush();
            risp.setrLong(sessione.getIdSessioneRecup());
        } catch (Exception e) {
            log.error(ERRORE_PERSISTENZA_OGGETTO, e);
            risp.setrBoolean(false);
            risp.setCodErr(MessaggiWSBundle.ERR_666);
            risp.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_NOTIF_PREL));
        }
        return risp;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public RispostaControlli modificaSessioneRecupero(NotificaPrelievoExt npExt, String cdErr, String dsErr) {
        RispostaControlli risp = new RispostaControlli();
        risp.setrBoolean(true);
        try {
            PigSessioneRecup sessione = entityManager.find(PigSessioneRecup.class, npExt.getIdSessioneRecup());
            if (npExt.getDtChiusura() != null && sessione.getDtChiusura() == null) {
                sessione.setDtChiusura(npExt.getDtChiusura());
            }
            if (npExt.getStatoSessione() != null) {
                sessione.setTiStato(npExt.getStatoSessione().name());
            }
            if (cdErr != null) {
                sessione.setCdErr(cdErr);
            }
            if (dsErr != null) {
                sessione.setDlErr(dsErr);
            }
            entityManager.flush();
        } catch (Exception e) {
            log.error(ERRORE_UPDATE_SESSIONE, e);
            risp.setrBoolean(false);
            risp.setCodErr(MessaggiWSBundle.ERR_666);
            risp.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_NOTIF_PREL,
                    String.join("\n", ExceptionUtils.getRootCauseStackTrace(e))));
        }
        return risp;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public RispostaControlli creaSessioneRecupero(NotificaInAttesaPrelievoExt niapExt, String errorCode,
            String errorMessage) {
        RispostaControlli risp = new RispostaControlli();
        risp.setrBoolean(true);
        PigSessioneRecup sessione = new PigSessioneRecup();
        sessione.setCdKeyObject(niapExt.getNotificaInAttesaPrelievoInput().getCdKeyObject());
        sessione.setDtApertura(niapExt.getDtApertura());
        sessione.setDtChiusura(niapExt.getDtChiusura());
        sessione.setNmAmbienteVers(niapExt.getNotificaInAttesaPrelievoInput().getNmAmbiente());
        sessione.setNmVers(niapExt.getNotificaInAttesaPrelievoInput().getNmVersatore());
        if (niapExt.getIdObject() != null) {
            sessione.setPigObject(entityManager.find(PigObject.class, niapExt.getIdObject()));
        }
        if (niapExt.getIdVersatore() != null) {
            sessione.setPigVer(entityManager.find(PigVers.class, niapExt.getIdVersatore()));
        }
        sessione.setTiStato(niapExt.getStatoSessione().name());

        sessione.setCdErr(errorCode);
        sessione.setDlErr(errorMessage);

        try {
            entityManager.persist(sessione);
            entityManager.flush();
            risp.setrLong(sessione.getIdSessioneRecup());
        } catch (Exception e) {
            log.error(ERRORE_PERSISTENZA_OGGETTO, e);
            risp.setrBoolean(false);
            risp.setCodErr(MessaggiWSBundle.ERR_666);
            risp.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_NOTIF_IN_ATTESA_PREL));
        }
        return risp;
    }

    // FIXME: Utilizzare la funzione generica sottostante che prende in input idSessione, stato, cdErr e dsErr
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public RispostaControlli modificaSessioneRecupero(NotificaInAttesaPrelievoExt niapExt, String cdErr, String dsErr) {
        RispostaControlli risp = new RispostaControlli();
        risp.setrBoolean(true);
        try {
            PigSessioneRecup sessione = entityManager.find(PigSessioneRecup.class, niapExt.getIdSessioneRecup());
            if (niapExt.getStatoSessione() != null) {
                sessione.setTiStato(niapExt.getStatoSessione().name());
            }
            if (cdErr != null) {
                sessione.setCdErr(cdErr);
            }
            if (dsErr != null) {
                sessione.setDlErr(dsErr);
            }
            entityManager.flush();
        } catch (Exception e) {
            log.error(ERRORE_UPDATE_SESSIONE, e);
            risp.setrBoolean(false);
            risp.setCodErr(MessaggiWSBundle.ERR_666);
            risp.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_NOTIF_IN_ATTESA_PREL,
                    String.join("\n", ExceptionUtils.getRootCauseStackTrace(e))));
        }
        return risp;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public RispostaControlli modificaSessioneRecupero(Long idSessioneRecup, Constants.StatoSessioneRecup stato,
            String cdErr, String dsErr) {
        RispostaControlli risp = new RispostaControlli();
        risp.setrBoolean(true);
        try {
            PigSessioneRecup sessione = entityManager.find(PigSessioneRecup.class, idSessioneRecup);
            if (stato != null) {
                sessione.setTiStato(stato.name());
            }
            if (cdErr != null) {
                sessione.setCdErr(cdErr);
            }
            if (dsErr != null) {
                sessione.setDlErr(dsErr);
            }
            entityManager.flush();
        } catch (Exception e) {
            log.error("Eccezione nell'update della sessione di recupero :", e);
            risp.setrBoolean(false);
            risp.setCodErr(MessaggiWSBundle.ERR_666);
        }
        return risp;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public RispostaControlli creaSessioneRecupero(PuliziaNotificatoExt pnExt, String errorCode, String errorMessage) {
        RispostaControlli risp = new RispostaControlli();
        risp.setrBoolean(true);
        PigSessioneRecup sessione = new PigSessioneRecup();
        sessione.setCdKeyObject(pnExt.getPuliziaNotificatoInput().getCdKeyObject());
        Calendar now = Calendar.getInstance();
        sessione.setDtApertura(now.getTime());
        sessione.setDtChiusura(now.getTime());
        sessione.setNmAmbienteVers(pnExt.getPuliziaNotificatoInput().getNmAmbiente());
        sessione.setNmVers(pnExt.getPuliziaNotificatoInput().getNmVersatore());
        if (pnExt.getIdObject() != null) {
            sessione.setPigObject(entityManager.find(PigObject.class, pnExt.getIdObject()));
        }
        if (pnExt.getIdVersatore() != null) {
            sessione.setPigVer(entityManager.find(PigVers.class, pnExt.getIdVersatore()));
        }
        sessione.setTiStato(Constants.StatoSessioneRecup.CHIUSO_ERR_ELIMINATO.name());

        sessione.setCdErr(errorCode);
        sessione.setDlErr(errorMessage);

        try {
            entityManager.persist(sessione);
            entityManager.flush();
            risp.setrLong(sessione.getIdSessioneRecup());
        } catch (Exception e) {
            log.error("Eccezione nella persistenza della sessione di recupero :", e);
            risp.setrBoolean(false);
            risp.setCodErr(MessaggiWSBundle.ERR_666);
            risp.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_PULIZIA_NOTIFICATO));
        }
        return risp;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public RispostaControlli cleanOggettoPadre(Long idObject) {
        RispostaControlli risp = new RispostaControlli();
        risp.setrBoolean(true);
        try {
            PigObject object = entityManager.find(PigObject.class, idObject);
            object.setNiTotObjectTrasf(null);
            for (PigObject figlio : object.getPigObjects()) {
                entityManager.remove(figlio);
            }
            for (PigObjectTrasf figlio : object.getPigObjectTrasfs()) {
                entityManager.remove(figlio);
            }
            entityManager.flush();
        } catch (Exception e) {
            log.error(ERRORE_UPDATE_OGGETTO, e);
            risp.setrBoolean(false);
            risp.setCodErr(MessaggiWSBundle.ERR_666);
            risp.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_PERSISTENCE,
                    String.join("\n", ExceptionUtils.getRootCauseStackTrace(e))));
        }
        return risp;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public RispostaControlli updateSessioniIngestErrate(String nmVers, String cdKeyObject, Long idObject) {
        RispostaControlli risp = new RispostaControlli();
        risp.setrBoolean(true);
        try {
            PigObject object = entityManager.find(PigObject.class, idObject);
            Query updateQuery = entityManager.createQuery(
                    "UPDATE PigSessioneIngest ses SET ses.pigObject = :pigObject WHERE ses.nmVers = :nmVers AND ses.cdKeyObject = :cdKeyObject AND ses.idSessioneIngest != :idSessioneIngest");
            updateQuery.setParameter("nmVers", nmVers);
            updateQuery.setParameter("cdKeyObject", cdKeyObject);
            updateQuery.setParameter("pigObject", object);
            updateQuery.setParameter("idSessioneIngest", HibernateUtils.longFrom(object.getIdLastSessioneIngest()));
            updateQuery.executeUpdate();
            entityManager.flush();
        } catch (Exception e) {
            log.error("Eccezione nell'update delle sessioni :", e);
            risp.setrBoolean(false);
            risp.setCodErr(MessaggiWSBundle.ERR_666);
            risp.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_PERSISTENCE,
                    String.join("\n", ExceptionUtils.getRootCauseStackTrace(e))));
        }
        return risp;
    }
}
