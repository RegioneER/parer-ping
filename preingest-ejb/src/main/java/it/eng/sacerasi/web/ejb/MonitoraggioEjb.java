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

package it.eng.sacerasi.web.ejb;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;
import javax.resource.ResourceException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xadisk.connector.outbound.XADiskConnection;
import org.xadisk.connector.outbound.XADiskConnectionFactory;
import org.xadisk.filesystem.exceptions.DirectoryNotEmptyException;
import org.xadisk.filesystem.exceptions.FileNotExistsException;
import org.xadisk.filesystem.exceptions.FileUnderUseException;
import org.xadisk.filesystem.exceptions.InsufficientPermissionOnFileException;
import org.xadisk.filesystem.exceptions.LockingFailedException;
import org.xadisk.filesystem.exceptions.NoTransactionAssociatedException;

import it.eng.sacerasi.common.Constants;
import it.eng.sacerasi.common.ejb.CommonDb;
import it.eng.sacerasi.entity.PigObject;
import it.eng.sacerasi.entity.PigObjectTrasf;
import it.eng.sacerasi.entity.PigSessioneIngest;
import it.eng.sacerasi.entity.PigStatoSessioneIngest;
import it.eng.sacerasi.entity.PigTipoObject;
import it.eng.sacerasi.exception.ParerErrorSeverity;
import it.eng.sacerasi.exception.ParerInternalError;
import it.eng.sacerasi.exception.ParerUserError;
import it.eng.sacerasi.job.coda.ejb.PayloadManagerEjb;
import it.eng.sacerasi.messages.MessaggiWSBundle;
import it.eng.sacerasi.slite.gen.tablebean.PigObjectRowBean;
import it.eng.sacerasi.slite.gen.tablebean.PigObjectTableBean;
import it.eng.sacerasi.slite.gen.tablebean.PigObjectTableDescriptor;
import it.eng.sacerasi.slite.gen.tablebean.PigStatoSessioneIngestTableBean;
import it.eng.sacerasi.slite.gen.tablebean.PigVersTipoObjectDaTrasfTableBean;
import it.eng.sacerasi.slite.gen.viewbean.MonVLisObjTrasfRowBean;
import it.eng.sacerasi.slite.gen.viewbean.MonVLisObjTrasfTableBean;
import it.eng.sacerasi.slite.gen.viewbean.MonVLisVersFallitiTableBean;
import it.eng.sacerasi.slite.gen.viewbean.MonVLisVersObjNonVersTableBean;
import it.eng.sacerasi.slite.gen.viewbean.MonVVisObjTrasfRowBean;
import it.eng.sacerasi.slite.gen.viewbean.MonVVisUnitaDocSessioneRowBean;
import it.eng.sacerasi.viewEntity.MonVLisObjTrasf;
import it.eng.sacerasi.viewEntity.MonVVisObjTrasf;
import it.eng.sacerasi.viewEntity.MonVVisUnitaDocSessione;
import it.eng.sacerasi.viewEntity.MonVVisVersFallito;
import it.eng.sacerasi.web.helper.ConfigurationHelper;
import it.eng.sacerasi.web.helper.MonitoraggioHelper;
import it.eng.sacerasi.web.util.Transform;
import it.eng.sacerasi.ws.util.Util;
import it.eng.spagoLite.db.base.table.AbstractBaseTable;

/**
 *
 * @author Gilioli_P
 */
@Stateless(mappedName = "MonitoraggioEjb")
@LocalBean
public class MonitoraggioEjb {

    @Resource
    SessionContext ctx;
    @Resource(mappedName = "jca/xadiskLocal")
    private XADiskConnectionFactory xadCf;
    @EJB(mappedName = "java:app/SacerAsync-ejb/MonitoraggioHelper")
    private MonitoraggioHelper monitoraggioHelper;
    @EJB
    private CommonDb commonDb;
    @EJB
    private ConfigurationHelper configurationHelper;
    @EJB
    private AmministrazioneEjb amministrazioneEjb;
    @EJB
    private PayloadManagerEjb payloadManagerHelper;

    private static final Logger log = LoggerFactory.getLogger(MonitoraggioEjb.class);

    /**
     * Verifica se una cartella avente come nome "cdKeyObject" è presente nel percorso nel percorso ricavato
     *
     * @param idVers
     *            id versamento
     * @param cdKeyObject
     *            chiave oggetto
     *
     * @return true se presente
     *
     * @throws Exception
     *             errore generico
     */
    public boolean isCartellaPresente(BigDecimal idVers, String cdKeyObject) throws Exception {
        XADiskConnection xadConn = null;
        boolean cartellaPresente = false;
        try {
            /* Ricavo il path */
            String rootFtp = commonDb.getRootFtpParam();
            String pathInput = monitoraggioHelper.getDsPathInputFtp(idVers);
            xadConn = xadCf.getConnection();
            File cartella = new File(rootFtp + pathInput + cdKeyObject);
            cartellaPresente = xadConn.fileExists(cartella);
        } finally {
            if (xadConn != null) {
                xadConn.close();
                log.info("Chiusura connessione xaDisk effettuata");
            }
        }
        return cartellaPresente;
    }

    /**
     * Cancello la cartella avente come nome "cdKeyObject" e percorso quello ricavato
     *
     * @param idVers
     *            id versamento
     * @param cdKeyObject
     *            chiave oggetto
     *
     * @throws ParerInternalError
     *             errore generico
     */
    public void deleteDir(BigDecimal idVers, String cdKeyObject) throws ParerInternalError {
        try {
            /* Ricavo il path */
            String rootFtp = commonDb.getRootFtpParam();
            String pathInput = monitoraggioHelper.getDsPathInputFtp(idVers);
            deleteDir(rootFtp, pathInput, cdKeyObject);
        } catch (FileNotExistsException | LockingFailedException | NoTransactionAssociatedException
                | InterruptedException | InsufficientPermissionOnFileException | DirectoryNotEmptyException
                | FileUnderUseException | ResourceException ex) {
            log.error("Errore inatteso nella gestione del file ", ex);
            throw new ParerInternalError(ParerErrorSeverity.ERROR, "Errore inatteso nella gestione del file ", ex);
        }
    }

    public void deleteDirTrasf(BigDecimal idVers, String dsPath) throws ParerInternalError {
        try {
            /* Ricavo il path */
            String rootFtp = commonDb.getRootTrasfParam();
            String pathInput = monitoraggioHelper.getDsPathTrasf(idVers);
            deleteDir(rootFtp, pathInput, dsPath);
        } catch (FileNotExistsException | LockingFailedException | NoTransactionAssociatedException
                | InterruptedException | InsufficientPermissionOnFileException | DirectoryNotEmptyException
                | FileUnderUseException | ResourceException ex) {
            log.error("Errore inatteso nella gestione del file ", ex);
            throw new ParerInternalError(ParerErrorSeverity.ERROR, "Errore inatteso nella gestione del file ", ex);
        }
    }

    private void deleteDir(String rootFtp, String path, String pathSuffix)
            throws FileNotExistsException, LockingFailedException, NoTransactionAssociatedException,
            InterruptedException, InsufficientPermissionOnFileException, DirectoryNotEmptyException,
            FileUnderUseException, ResourceException {
        XADiskConnection xadConn = null;
        try {
            File cartellaDaCancellare = new File(rootFtp + path + pathSuffix);
            if (cartellaDaCancellare.exists() && cartellaDaCancellare.isDirectory()) {
                xadConn = xadCf.getConnection();
                Util.rimuoviDirRicorsivamente(cartellaDaCancellare, xadConn);
            }
        } finally {
            if (xadConn != null) {
                xadConn.close();
                log.info("Chiusura connessione xaDisk effettuata");
            }
        }
    }

    public boolean writeFlagsDeleteDirOggettiDerivanti(BigDecimal idVers, String cdKeyObject, String nmTipoObject,
            Set<Integer> verificatiNonRisolubiliModificati, Set<BigDecimal> idSessioneHS,
            Set<BigDecimal> idSessioneHSNoRis, MonVLisVersObjNonVersTableBean tb)
            throws ParerUserError, ParerInternalError {
        boolean isFlVersSacerRecupEditable = false;
        try {
            /* AGGIORNA I FLAG "VERIFICATO" E "NON RISOLUBILE" DELLE SESSIONI */
            aggiornaSessioni(verificatiNonRisolubiliModificati, idSessioneHS, idSessioneHSNoRis, tb);
            /* AGGIORNA FLAG "VERSAMENTO A SACER DA RECUPERARE" ****/
            /* (modifica automatica a seconda di varie condizioni) */
            String tiStatoObject = monitoraggioHelper.getTiStatoObject(idVers, cdKeyObject);
            /* Se l'oggetto derivante dai versamenti falliti è definito con stato CHIUSO_ERR_VERS */
            // MEV#14100
            impostaValoreFlVersSacerDaRecupDaDettOggListaVersFalliti(idVers, cdKeyObject, nmTipoObject, tiStatoObject);
            /* RICAVA INFO SE FLAG ""VERSAMENTO A SACER DA RECUPERARE" */
            /*
             * ************** ANDRA' MESSO EDITABILE ******************
             */
            /*
             * Controlla se adesso il flag "Verificato" dell'oggetto derivante da versamenti falliti vale 0 o 1, (può
             * essere cambiato in base alle modifiche di aggiornamento sessioni)
             */
            String flVerifAggiornato = monitoraggioHelper.getObjNonVersFlVerif(idVers, cdKeyObject);
            /* Controllo se adesso devo rendere editabile il flag fl_vers_sacer_da_recup */
            isFlVersSacerRecupEditable = isFlVersSacerDaRecupEditable(idVers, cdKeyObject, nmTipoObject,
                    flVerifAggiornato);

            boolean allNonRisolubili = monitoraggioHelper.areAllNonRisolubili(idVers, cdKeyObject, nmTipoObject);
            if (tiStatoObject != null
                    && (tiStatoObject.equals(Constants.StatoOggetto.CHIUSO_ERR_VERS.name())
                            || tiStatoObject.equals(Constants.StatoOggetto.CHIUSO_ERR_SCHED.name())
                            || tiStatoObject.equals(Constants.StatoOggetto.CHIUSO_ERR_CODA.name()))
                    && allNonRisolubili) {

                PigObjectRowBean tmpRow = monitoraggioHelper.getPigObjectRowBean(idVers, cdKeyObject);
                List<BigDecimal> ids = new ArrayList<>();
                ids.add(tmpRow.getIdObject());

                List<PigObject> pigObjectPadriTB = monitoraggioHelper.getPigObjectsPadri(ids);
                // Date now = Calendar.getInstance().getTime();
                for (PigObject padre : pigObjectPadriTB) {
                    // MEV#14100 - DefinisciStatoOggetto padre
                    payloadManagerHelper.definisciStatoOggettoPadre(padre.getIdObject());
                    // gestisciPadreVersFallito(padre, now);
                }
            }
        } catch (ParerInternalError e) {
            ctx.setRollbackOnly();
            throw e;
        } catch (Exception e) {
            /*
             * Il rollback va settato visto che sono in modalità cmt in modo tale da gestire le eccezioni non di tipo
             * RuntimeException (che vengono gestite automaticamente)
             */
            ctx.setRollbackOnly();
            log.error(e.getMessage());
            throw new ParerUserError(
                    "Attenzione: l'operazione non è stata eseguita perchè si è verificato un errore a runtime durante il salvataggio dei flag");
        }
        return isFlVersSacerRecupEditable;
    }

    public boolean writeFlagsDeleteDir(Set<Integer> verificatiNonRisolubiliModificati, Set<BigDecimal> idSessioneHS,
            Set<BigDecimal> idSessioneHSNoRis, MonVLisVersFallitiTableBean tb)
            throws ParerUserError, ParerInternalError {
        boolean isFlVersSacerRecupEditable = false;
        try {
            /* AGGIORNA I FLAG "VERIFICATO" E "NON RISOLUBILE" DELLE SESSIONI */
            Set<BigDecimal> idSesModificate = aggiornaSessioni(verificatiNonRisolubiliModificati, idSessioneHS,
                    idSessioneHSNoRis, tb);

            /*
             * AGGIORNO IL FLAG "VERSAMENTO A SACER DA RECUPERARE" PER GLI OGGETTI IN QUESTIONE Ricavo gli oggetti
             * associati alle sessioni modificate: le sessioni che mi interessa considerare devono avere stato
             * NON_RISOLTO e nmTipoObject != null (questo lo controlla già la vista). Gli oggetti, se ci sono
             */
            PigObjectTableBean pigObjectTB = monitoraggioHelper.getPigObjectsFromSessions(idSesModificate);
            if (!pigObjectTB.isEmpty()) {
                List<BigDecimal> ids = Arrays.asList(pigObjectTB.toList(PigObjectTableDescriptor.COL_ID_OBJECT)
                        .toArray(new BigDecimal[pigObjectTB.toList(PigObjectTableDescriptor.COL_ID_OBJECT).size()]));
                /*
                 * OTTENGO GLI OGGETTI PADRI DEI FIGLI CON STATO CHIUSO_ERR_VERS, CHIUSO_ERR_SCHED E CHIUSO_ERR_CODA, E
                 * GESTISCO IL PADRE
                 */
                List<PigObject> pigObjectPadriTB = monitoraggioHelper.getPigObjectsPadri(ids);

                for (PigObjectRowBean pigObjectRB : pigObjectTB) {
                    // MEV#14100 - Tolto il controllo dello stato = CHIUSO_ERR_VERS
                    impostaValoreFlVersSacerDaRecup(pigObjectRB.getIdVers(), pigObjectRB.getCdKeyObject(),
                            pigObjectRB.getString("nm_tipo_object"), pigObjectRB.getTiStatoObject());
                }
                // Date now = Calendar.getInstance().getTime();
                for (PigObject padre : pigObjectPadriTB) {
                    // MEV#14100 - DefinisciStatoOggetto padre
                    payloadManagerHelper.definisciStatoOggettoPadre(padre.getIdObject());
                }
            }
        } catch (ParerInternalError e) {
            ctx.setRollbackOnly();
            throw e;
        } catch (Exception e) {
            /*
             * Il rollback va settato visto che sono in modalità cmt in modo tale da gestire le eccezioni non di tipo
             * RuntimeException (che vengono gestite automaticamente)
             */
            ctx.setRollbackOnly();
            log.error(e.getMessage());
            throw new ParerUserError(
                    "Attenzione: l'operazione non è stata eseguita perchè si è verificato un errore a runtime durante il salvataggio dei flag");
        }
        return isFlVersSacerRecupEditable;
    }

    /**
     * Dato un tablebean contenente una list di sessioni, ne modifica i flag "Verificato" e "NonRisolubile"
     *
     * @param verificatiNonRisolubiliModificati
     *            verificati non risolubili
     * @param idSessioneHS
     *            id sessione HS
     * @param idSessioneHSNoRis
     *            id sessione non risolubili
     * @param tb
     *            tabella
     *
     * @return idSesModificate, il set di id delle sessioni modificate (flag "Verificato" o "Non risolubile"
     */
    private Set<BigDecimal> aggiornaSessioni(Set<Integer> verificatiNonRisolubiliModificati,
            Set<BigDecimal> idSessioneHS, Set<BigDecimal> idSessioneHSNoRis, AbstractBaseTable<?> tb) throws Exception {
        Set<BigDecimal> idSesModificate = new HashSet<>();
        /* Scorro i flag (Verificato o Non risolubile) modificati */
        for (Integer index : verificatiNonRisolubiliModificati) {
            BigDecimal idSesErr = tb.getRow(index).getBigDecimal("id_sessione_ingest");
            idSesModificate.add(idSesErr);

            // Se ho impostato a "1" il flag "verificato"
            if (idSessioneHS.contains(idSesErr)) {
                // Se ho impostato a "1" il flag "non risolubile"
                if (idSessioneHSNoRis.contains(idSesErr)) {
                    monitoraggioHelper.saveFlVerificatiNonRisolubili(idSesErr, "1", "1");
                } else {
                    monitoraggioHelper.saveFlVerificatiNonRisolubili(idSesErr, "1", "0");
                }
            } else {
                // Metti il flag flNonRisolubile a "null" visto che è l'unica opzione consentita
                monitoraggioHelper.saveFlVerificatiNonRisolubili(idSesErr, "0", null);
            }
        }
        return idSesModificate;
    }

    /**
     * Imposto su DB il valore del flag Versamento da SACER da recuperare in base ai flag "Verificato" e "Non
     * risolubile" dei versamenti falliti. Se i versamenti sono tutti "Non risolubili", cancello da filesystem (area
     * FTP) la cartella con il file. METODO INVOCATO dalla pressione del pulsante "Imposta versamenti verificati/non
     * risolubili" nella finestra "lista versametni falliti"
     *
     * @param idVers
     *            id versamento
     * @param cdKeyObject
     *            chiave oggetto
     * @param nmTipoObject
     *            nome tipo oggetto
     * @param tiStatoObject
     *            tipo stato oggetto
     *
     * @throws Exception
     *             errore generico
     */
    public void impostaValoreFlVersSacerDaRecup(BigDecimal idVers, String cdKeyObject, String nmTipoObject,
            String tiStatoObject) throws Exception {
        boolean allVerificate = monitoraggioHelper.areAllVerificate(idVers, cdKeyObject, nmTipoObject);
        boolean allNonRisolubili = monitoraggioHelper.areAllNonRisolubili(idVers, cdKeyObject, nmTipoObject);

        // MEV#14100
        if (tiStatoObject.equals(Constants.StatoOggetto.CHIUSO_ERR_VERS.name())) {
            /* Gestione "Verificato" */
            if (allVerificate) {
                monitoraggioHelper.salvaFlVersSacerDaRecup(idVers, cdKeyObject, "0");
            } else {
                monitoraggioHelper.salvaFlVersSacerDaRecup(idVers, cdKeyObject, null);
            }
        }
        /* Gestione "Non risolubile" */
        if (allNonRisolubili) {
            monitoraggioHelper.salvaFlVersSacerDaRecup(idVers, cdKeyObject, null);
            /* Cancello la cartella nell'area FTP */
            deleteDir(idVers, cdKeyObject);
        } /*
           * Se tutte le sessioni hanno l'indicatore "Non risolubile" NON settato (condizione necessaria affinchè
           * l'oggetto sia nel complesso "risolubile"
           */ else {
            boolean tutteSessioniRisolubili = monitoraggioHelper.areAllRisolubili(idVers, cdKeyObject, nmTipoObject);
            if (tutteSessioniRisolubili) {
                monitoraggioHelper.salvaFlVersSacerDaRecup(idVers, cdKeyObject, "0");
            }
        }

    }

    /**
     * Imposto su DB il valore del flag Versamento da SACER da recuperare in base ai flag "Verificato" e "Non
     * risolubile" dei versamenti falliti. Se i versamenti sono tutti "Non risolubili", cancello da filesystem (area
     * FTP) la cartella con il file. Metodo invocato dalla lista versamenti falliti.
     *
     * @param idVers
     *            id versamento
     * @param cdKeyObject
     *            chiave oggetto
     * @param nmTipoObject
     *            nome tipo oggetto
     * @param tiStatoObject
     *            tipo stato oggetto
     *
     * @throws Exception
     *             errore generico
     */
    public void impostaValoreFlVersSacerDaRecupDaDettOggListaVersFalliti(BigDecimal idVers, String cdKeyObject,
            String nmTipoObject, String tiStatoObject) throws Exception {
        boolean allNonRisolubili = monitoraggioHelper.areAllNonRisolubili(idVers, cdKeyObject, nmTipoObject);

        /* Gestione "Verificato" */
        // MEV#14100
        if (tiStatoObject != null && tiStatoObject.equals(Constants.StatoOggetto.CHIUSO_ERR_VERS.name())) {
            boolean allVerificate = monitoraggioHelper.areAllVerificate(idVers, cdKeyObject, nmTipoObject);
            if (allVerificate) {
                monitoraggioHelper.salvaFlVersSacerDaRecup(idVers, cdKeyObject, "0");
            } else {
                monitoraggioHelper.salvaFlVersSacerDaRecup(idVers, cdKeyObject, null);
            }
        }
        /* Gestione "Non risolubile" */
        if (allNonRisolubili) {
            monitoraggioHelper.salvaFlVersSacerDaRecup(idVers, cdKeyObject, null);
            /* Cancello la cartella nell'area FTP */
            deleteDir(idVers, cdKeyObject);
        } /*
           * Se tutte le sessioni hanno l'indicatore "Non risolubile" NON settato (condizione necessaria affinchè
           * l'oggetto sia nel complesso "risolubile"
           */ else {
            // MEV#14100
            if (tiStatoObject != null && tiStatoObject.equals(Constants.StatoOggetto.CHIUSO_ERR_VERS.name())) {
                boolean tutteSessioniRisolubili = monitoraggioHelper.areAllRisolubili(idVers, cdKeyObject,
                        nmTipoObject);
                if (tutteSessioniRisolubili) {
                    monitoraggioHelper.salvaFlVersSacerDaRecup(idVers, cdKeyObject, "0");
                }
            }
        }

    }

    public void aggiornaNoteSessione(BigDecimal idSessioneIngest, String note) {
        PigSessioneIngest pigSessioneIngest = monitoraggioHelper.getEntityManager().find(PigSessioneIngest.class,
                idSessioneIngest.longValueExact());
        pigSessioneIngest.setNote(note);
    }

    /*
     * Nuova funzione centralizzata di impostazione dei due flag "Verificato" e "Non risolubile" utilizzata da tre punti
     * dell'interfaccia utente.
     */
    public String impostaFlagVerificatoNonRisolubile(BigDecimal idSessioneIngest, boolean flagVerificata,
            boolean flagNonRisolubile) throws Exception {
        String errore = null;
        MonVVisVersFallito monVVisVersFallito = monitoraggioHelper.findViewById(MonVVisVersFallito.class,
                idSessioneIngest);
        PigObject pigObject = null;
        if (monVVisVersFallito.getIdObject() != null) {
            pigObject = monitoraggioHelper.findById(PigObject.class, monVVisVersFallito.getIdObject());
        }
        // punto 1.1 dell'analisi
        if (flagVerificata == false && pigObject != null) {
            String flagDaRecuperare = pigObject.getFlVersSacerDaRecup();
            if (!(flagDaRecuperare == null || flagDaRecuperare.equals(Constants.DB_FALSE))) {
                errore = "Non e' possibile resettare l'indicazione  di sessione verificata, perche' l'oggetto "
                        + pigObject.getCdKeyObject()
                        + " ha l'indicazione che il versamento a SACER deve essere recuperato";
                return errore;
            }
        }
        // punto 1.2 dell'analisi
        if (flagNonRisolubile == true && (monVVisVersFallito.getTiStatoRisoluz() == null
                || !monVVisVersFallito.getTiStatoRisoluz().equals("NON_RISOLTO"))) {
            errore = "La sessione " + idSessioneIngest
                    + " puo' essere definita non risolubile solo se ha stato di risoluzione NON RISOLTO";
            return errore;
        }
        // punto 1.3 dell'analisi
        if (flagVerificata == false && flagNonRisolubile == true) {
            errore = "La sessione " + idSessioneIngest
                    + " puo' essere definita non risolubile o risolubile solo se e' stata verificata";
            return errore;
        }
        // punto 1.4 dell'analisi
        if (flagNonRisolubile == true && pigObject != null) {
            String flagDaRecuperare = pigObject.getFlVersSacerDaRecup();
            if (!(flagDaRecuperare == null || flagDaRecuperare.equals(Constants.DB_FALSE))) {
                errore = "Non e' possibile resettare l'indicazione di sessione verificata, perche' l'oggetto "
                        + pigObject.getCdKeyObject()
                        + " ha l'indicazione che il versamento a SACER deve essere recuperato";
                return errore;
            }
        }
        // se tutti i controlli sono passati prosegue ad impostare il resto dal punto 2, 3, 4 dell'analisi
        PigSessioneIngest pigSessioneIngest = monitoraggioHelper.findById(PigSessioneIngest.class, idSessioneIngest);
        if (flagVerificata == true) {
            pigSessioneIngest.setFlSesErrNonRisolub(Constants.DB_FALSE);
            pigSessioneIngest.setFlSesErrVerif(Constants.DB_TRUE);
        } else {
            pigSessioneIngest.setFlSesErrNonRisolub(null);
            pigSessioneIngest.setFlSesErrVerif(Constants.DB_FALSE);
        }
        // Punto 5
        if (flagNonRisolubile == true) {
            pigSessioneIngest.setFlSesErrNonRisolub(Constants.DB_TRUE);
        } else {
            pigSessioneIngest.setFlSesErrNonRisolub(Constants.DB_FALSE);
        }
        // Punto 6 e 7 dell'analisi
        if (pigObject != null) {
            String statoOggetto = pigObject.getTiStatoObject();
            if (statoOggetto.equals(Constants.StatoOggetto.CHIUSO_ERR_VERS.name())) {
                boolean allVerificate = monitoraggioHelper.areAllVerificate(
                        new BigDecimal(pigObject.getPigVer().getIdVers()), pigSessioneIngest.getCdKeyObject(),
                        pigSessioneIngest.getNmTipoObject());
                if (allVerificate == true) { // Punto 7.1 dell'analisi
                    pigObject.setFlVersSacerDaRecup(Constants.DB_FALSE);
                    boolean allNonRisolubili = monitoraggioHelper.areAllNonRisolubili(
                            new BigDecimal(pigObject.getPigVer().getIdVers()), pigSessioneIngest.getCdKeyObject(),
                            pigSessioneIngest.getNmTipoObject());
                    if (allNonRisolubili == true) {
                        pigObject.setFlVersSacerDaRecup(null);
                        /* Cancello la cartella nell'area FTP */
                        deleteDir(new BigDecimal(pigObject.getPigVer().getIdVers()),
                                pigSessioneIngest.getCdKeyObject());
                    } else {
                        pigObject.setFlVersSacerDaRecup(Constants.DB_FALSE);
                    }
                } else { // Punto 7.2 dell'analisi
                    pigObject.setFlVersSacerDaRecup(null);
                }
                // Punto 8 dell'analisi
            } else if (statoOggetto.equals(Constants.StatoOggetto.CHIUSO_ERR.name())
                    || statoOggetto.equals(Constants.StatoOggetto.CHIUSO_ERR_NOTIF.name())
                    || statoOggetto.equals(Constants.StatoOggetto.CHIUSO_ERR_SCHED.name())
                    || statoOggetto.equals(Constants.StatoOggetto.CHIUSO_ERR_CODA.name())) {
                boolean allVerificate = monitoraggioHelper.areAllVerificate(
                        new BigDecimal(pigObject.getPigVer().getIdVers()), pigSessioneIngest.getCdKeyObject(),
                        pigSessioneIngest.getNmTipoObject());
                boolean allNonRisolubili = monitoraggioHelper.areAllNonRisolubili(
                        new BigDecimal(pigObject.getPigVer().getIdVers()), pigSessioneIngest.getCdKeyObject(),
                        pigSessioneIngest.getNmTipoObject());
                if (allVerificate && allNonRisolubili) {
                    /* Cancello la cartella nell'area FTP */
                    deleteDir(new BigDecimal(pigObject.getPigVer().getIdVers()), pigSessioneIngest.getCdKeyObject());
                }
            }

            // Punto 9 dell'analisi
            PigObject oggPadre = pigObject.getPigObjectPadre();
            if (oggPadre != null) {
                payloadManagerHelper.definisciStatoOggettoPadre(oggPadre.getIdObject());
            }
        }
        return errore;
    }

    /**
     * Controlla se deve essere impostato editabile il flag fl_vers_sacer_da_recup. Le condizioni sono: 1) lo stato
     * dell'oggetto deve essere CHIUSO_ERR_VERS 2) il flag "Verificato" = 1 3) tutti i flag "Non risolubile" della lista
     * sono uguali a 0 4) nell'area FTP è presente la cartella corrispondente alla chiave dell'oggetto derivante dai
     * versamenti falliti
     *
     * @param idVers
     *            id versamento
     * @param cdKeyObject
     *            chiave oggetto
     * @param nmTipoObject
     *            nome tipo oggetto
     * @param flVerif
     *            flag 1/0 (true/false)
     *
     * @return true se recupero editabile
     *
     * @throws Exception
     *             errore generico
     */
    public boolean isFlVersSacerDaRecupEditable(BigDecimal idVers, String cdKeyObject, String nmTipoObject,
            String flVerif) throws Exception {
        String tiStatoObject = monitoraggioHelper.getTiStatoObject(idVers, cdKeyObject);
        boolean allRisolubili = monitoraggioHelper.areAllRisolubili(idVers, cdKeyObject, nmTipoObject);
        boolean cartellaPresente = isCartellaPresente(idVers, cdKeyObject);
        return tiStatoObject != null && tiStatoObject.equals(Constants.StatoOggetto.CHIUSO_ERR_VERS.name())
                && flVerif.equals(Constants.DB_TRUE) && allRisolubili && cartellaPresente;
    }

    public boolean isChiusoErrVersAndAllVerifRisol(BigDecimal idVers, String cdKeyObject, String nmTipoObject) {
        String tiStatoObject = monitoraggioHelper.getTiStatoObject(idVers, cdKeyObject);
        boolean allNonRisolubili = monitoraggioHelper.areAllNonRisolubili(idVers, cdKeyObject, nmTipoObject);
        boolean allVerificate = monitoraggioHelper.areAllVerificate(idVers, cdKeyObject, nmTipoObject);
        return tiStatoObject != null && tiStatoObject.equals(Constants.StatoOggetto.CHIUSO_ERR_VERS.name())
                && allNonRisolubili && allVerificate;
    }

    /**
     * Ritorna il tableBean contenente la lista di oggetti generati da trasformazione
     *
     * @param idObject
     *            id modello
     *
     * @return il tableBean della lista
     *
     * @throws ParerUserError
     *             errore generico
     */
    public MonVLisObjTrasfTableBean getMonVLisObjTrasfTableBean(BigDecimal idObject) throws ParerUserError {
        MonVLisObjTrasfTableBean table = new MonVLisObjTrasfTableBean();
        List<MonVLisObjTrasf> list = monitoraggioHelper.retrieveMonVLisObjTrasf(idObject);
        if (list != null && !list.isEmpty()) {
            try {
                for (MonVLisObjTrasf row : list) {
                    MonVLisObjTrasfRowBean rowBean = (MonVLisObjTrasfRowBean) Transform.entity2RowBean(row);
                    rowBean.setString("fl_exist_pg", row.getPgOggettoTrasf() != null ? "1" : "0");
                    boolean versatoAPing = StringUtils.isNotBlank(row.getTiStatoTrasf());
                    rowBean.setString("fl_versato_ping", versatoAPing ? "1" : "0");
                    rowBean.setString("showOggettoVersatoAPing", versatoAPing ? "Visualizza oggetto" : null);
                    // MEV 27037
                    rowBean.setBigDecimal("ni_ud_prodotte", row.getNiUdProdotte());
                    table.add(rowBean);
                }
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                    | IllegalArgumentException | InvocationTargetException ex) {
                String msg = "Errore durante il recupero della lista di oggetti generati da trasformazione "
                        + ExceptionUtils.getRootCauseMessage(ex);
                log.error(msg, ex);
                throw new ParerUserError(msg);
            }
        }
        return table;
    }

    public PigStatoSessioneIngestTableBean getPigStatoSessioneIngestFromPigObjectTableBean(BigDecimal idObject)
            throws ParerUserError {
        PigStatoSessioneIngestTableBean table = new PigStatoSessioneIngestTableBean();
        List<PigStatoSessioneIngest> list = monitoraggioHelper.retrievePigStatoSessioneIngestFromPigObject(idObject);
        if (list != null && !list.isEmpty()) {
            try {
                table = (PigStatoSessioneIngestTableBean) Transform.entities2TableBean(list);
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                    | IllegalArgumentException | InvocationTargetException ex) {
                String msg = "Errore durante il recupero della lista di stati della sessione "
                        + ExceptionUtils.getRootCauseMessage(ex);
                log.error(msg, ex);
                throw new ParerUserError(msg);
            }
        }
        return table;
    }

    public PigStatoSessioneIngestTableBean getPigStatoSessioneIngestTableBean(BigDecimal idSessioneIngest)
            throws ParerUserError {
        PigStatoSessioneIngestTableBean table = new PigStatoSessioneIngestTableBean();
        List<PigStatoSessioneIngest> list = monitoraggioHelper.retrievePigStatoSessioneIngest(idSessioneIngest);
        if (list != null && !list.isEmpty()) {
            try {
                table = (PigStatoSessioneIngestTableBean) Transform.entities2TableBean(list);
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                    | IllegalArgumentException | InvocationTargetException ex) {
                String msg = "Errore durante il recupero della lista di stati della sessione "
                        + ExceptionUtils.getRootCauseMessage(ex);
                log.error(msg, ex);
                throw new ParerUserError(msg);
            }
        }
        return table;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    @Interceptors({ it.eng.sacerasi.aop.TransactionInterceptor.class })
    public void recuperaErrore(BigDecimal idObject, String tiStato, String tiStatoChiusura, BigDecimal idTipoObject)
            throws ParerUserError {
        PigObject object = monitoraggioHelper.findById(PigObject.class, idObject);
        PigSessioneIngest session = monitoraggioHelper.findById(PigSessioneIngest.class,
                object.getIdLastSessioneIngest());
        PigSessioneIngest newSession;
        BigDecimal idSessione;
        Date now = Calendar.getInstance().getTime();
        Constants.StatoOggetto stato = Constants.StatoOggetto.valueOf(tiStato);
        try {

            switch (stato) {
            case DA_TRASFORMARE:
                // CHIUDE LA SESSIONE
                monitoraggioHelper.creaStatoSessione(object.getIdLastSessioneIngest(), tiStatoChiusura, now);
                session.setDtChiusura(now);
                session.setTiStato(tiStatoChiusura);
                session.setFlSesErrVerif("1");
                session.setFlSesErrNonRisolub("0");

                // Crea una nuova sessione con lo stato dato come parametro
                newSession = monitoraggioHelper.creaSessione(session, object, now, tiStato);
                idSessione = new BigDecimal(newSession.getIdSessioneIngest());
                monitoraggioHelper.creaStatoSessione(idSessione, tiStato, now);

                // Aggiorna l'oggetto con il nuovo stato
                object.setTiStatoObject(tiStato);
                object.setIdLastSessioneIngest(idSessione);
                object.setNiTotObjectTrasf(null);
                // MEV #15178 - Modifica Tipo oggetto in dettaglio oggetto per gli oggetti da trasformare
                PigTipoObject pigTipoObject = monitoraggioHelper.findById(PigTipoObject.class, idTipoObject);
                object.setPigTipoObject(pigTipoObject);
                // Aggiorna anche tutte le occorrenze di PigVersTipoObjectDaTrasf altrimenti dse cambia il tipo oggetto
                // esplode la trasformazione!
                // MAC #22991: Modifica Tipo oggetto in dettaglio oggetto non viene recepito dal sistema
                // Riassegna il CD_VERS_GEN con lo stesso criterio con cui lo assegna in fase di versamento
                PigVersTipoObjectDaTrasfTableBean tipoObjDaTrasfTB = amministrazioneEjb
                        .getPigVersTipoObjectDaTrasfTableBean(idTipoObject);
                if (tipoObjDaTrasfTB != null && tipoObjDaTrasfTB.size() == 1) {
                    String cdVersGen = tipoObjDaTrasfTB.getRow(0).getCdVersGen();
                    object.setCdVersGen(cdVersGen);
                }

                // MEV#14653
                // object.getPigTipoObject().getXfoTrasf().setFlAttiva("0");
                //
                // for (PigObject figlio : object.getPigObjects()) {
                // deleteDir(new BigDecimal(figlio.getPigVer().getIdVers()), figlio.getCdKeyObject());
                // monitoraggioHelper.removeEntity(figlio, false);
                // }
                for (PigObjectTrasf figlio : object.getPigObjectTrasfs()) {
                    deleteDirTrasf(new BigDecimal(figlio.getPigVer().getIdVers()), figlio.getDsPath());
                    monitoraggioHelper.removeEntity(figlio, false);
                }
                for (PigObjectTrasf objectFiglio : object.getPigObjectTrasfs()) {
                    payloadManagerHelper.deleteDirTrasf(objectFiglio.getPigVer().getDsPathTrasf(),
                            objectFiglio.getDsPath());
                    // BigDecimal idAmbienteVers = BigDecimal.valueOf(
                    // objectFiglio.getPigTipoObject().getPigVer().getPigAmbienteVer().getIdAmbienteVers());
                    // BigDecimal idVers = BigDecimal.valueOf(objectFiglio.getPigTipoObject().getPigVer().getIdVers());
                    // BigDecimal idTipoObject = BigDecimal.valueOf(objectFiglio.getPigTipoObject().getIdTipoObject());
                    // util.deleteDirTrasf(configurationHelper.getValoreParamApplic("DS_PATH_TRASF", idAmbienteVers,
                    // idVers, idTipoObject, Constants.TipoPigVGetValAppart.TIPOOBJECT), objectFiglio.getDsPath());
                }
                break;
            case TRASFORMATO:
                // CHIUDE LA SESSIONE
                // MAC #32121 se arrivo da stato WARNING_TRASFORMAZIONE non devo chiudere la sessione
                if (!object.getTiStatoObject().equals(Constants.StatoOggetto.WARNING_TRASFORMAZIONE.name())) {
                    monitoraggioHelper.creaStatoSessione(object.getIdLastSessioneIngest(), tiStatoChiusura, now);
                    session.setDtChiusura(now);
                    session.setTiStato(tiStatoChiusura);
                    session.setFlSesErrVerif("1");
                    session.setFlSesErrNonRisolub("0");

                    // Crea una nuova sessione con lo stato dato come parametro
                    newSession = monitoraggioHelper.creaSessione(session, object, now, tiStato);
                    idSessione = new BigDecimal(newSession.getIdSessioneIngest());
                    monitoraggioHelper.creaStatoSessione(idSessione, tiStato, now);
                } else {
                    idSessione = object.getIdLastSessioneIngest();
                    monitoraggioHelper.creaStatoSessione(idSessione, Constants.StatoOggetto.TRASFORMATO.name(), now);

                }

                // Aggiorna l'oggetto con il nuovo stato
                object.setTiStatoObject(tiStato);
                object.setIdLastSessioneIngest(idSessione);
                break;
            case CHIUSO_ERR_TRASFORMAZIONE:
            case CHIUSO_ERR_VERSAMENTO_A_PING:
            case IN_ATTESA_SCHED:
                // MAC #23440: inserisce il nuovo stato nella sessione corrente
                idSessione = object.getIdLastSessioneIngest();
                monitoraggioHelper.creaStatoSessione(idSessione, tiStatoChiusura, now);
                session.setDtChiusura(now);
                session.setTiStato(tiStatoChiusura);
                session.setFlSesErrVerif("1");
                session.setFlSesErrNonRisolub("0");

                object.setIdLastSessioneIngest(idSessione);
                object.setTiStatoObject(tiStato);
                object.setNiTotObjectTrasf(null);

                /* Cancello la cartella nell'area FTP */
                // MEV#14653
                // deleteDir(new BigDecimal(object.getPigVer().getIdVers()), object.getCdKeyObject());
                // for (PigObject figlio : object.getPigObjects()) {
                // deleteDir(new BigDecimal(figlio.getPigVer().getIdVers()), figlio.getCdKeyObject());
                // monitoraggioHelper.removeEntity(figlio, false);
                // }
                for (PigObjectTrasf objectFiglio : object.getPigObjectTrasfs()) {
                    payloadManagerHelper.deleteDirTrasf(objectFiglio.getPigVer().getDsPathTrasf(),
                            objectFiglio.getDsPath());
                    // BigDecimal idAmbienteVers = BigDecimal.valueOf(
                    // objectFiglio.getPigTipoObject().getPigVer().getPigAmbienteVer().getIdAmbienteVers());
                    // BigDecimal idVers = BigDecimal.valueOf(objectFiglio.getPigTipoObject().getPigVer().getIdVers());
                    // BigDecimal idTipoObject = BigDecimal.valueOf(objectFiglio.getPigTipoObject().getIdTipoObject());
                    // util.deleteDirTrasf(configurationHelper.getValoreParamApplic("DS_PATH_TRASF", idAmbienteVers,
                    // idVers, idTipoObject, Constants.TipoPigVGetValAppart.TIPOOBJECT), objectFiglio.getDsPath());
                    // deleteDirTrasf(new BigDecimal(figlio.getPigVer().getIdVers()), figlio.getDsPath());
                    // MEV#14653
                    // monitoraggioHelper.removeEntity(objectFiglio, false);
                }
                break;
            default:
                throw new ParerUserError("Errore inaspettato nella scelta dello stato oggetto");
            }
            // 29/11/2017 Aggiornamento analisi recupero errore versamento a Ping
            // Ottieni i figli con stato IN_ATTESA_FILE e IN_ATTESA_SCHED
            List<PigObject> figli = monitoraggioHelper.getFigliWithStatus(object.getIdObject(),
                    Constants.StatoOggetto.IN_ATTESA_FILE.name(), Constants.StatoOggetto.IN_ATTESA_SCHED.name());
            for (PigObject figlio : figli) {
                PigSessioneIngest sessioneFiglio = monitoraggioHelper.findById(PigSessioneIngest.class,
                        figlio.getIdLastSessioneIngest());
                Constants.StatoOggetto statoFiglio = Constants.StatoOggetto.valueOf(figlio.getTiStatoObject());
                switch (statoFiglio) {
                case IN_ATTESA_FILE:
                    monitoraggioHelper.creaStatoSessione(figlio.getIdLastSessioneIngest(),
                            Constants.StatoSessioneIngest.CHIUSO_ERR_NOTIF.name(), now);
                    sessioneFiglio.setDtChiusura(now);
                    sessioneFiglio.setTiStato(Constants.StatoSessioneIngest.CHIUSO_ERR_NOTIF.name());
                    sessioneFiglio.setFlSesErrVerif("1");
                    sessioneFiglio.setFlSesErrNonRisolub("0");
                    sessioneFiglio.setCdErr(MessaggiWSBundle.PING_NOT_020);
                    sessioneFiglio.setDlErr(MessaggiWSBundle.getString(MessaggiWSBundle.PING_NOT_020,
                            object.getCdKeyObject(), tiStato));
                    figlio.setTiStatoObject(Constants.StatoOggetto.CHIUSO_ERR_NOTIF.name());
                    break;
                case IN_ATTESA_SCHED:
                    monitoraggioHelper.creaStatoSessione(figlio.getIdLastSessioneIngest(),
                            Constants.StatoSessioneIngest.CHIUSO_ERR_SCHED.name(), now);
                    sessioneFiglio.setDtChiusura(now);
                    sessioneFiglio.setTiStato(Constants.StatoSessioneIngest.CHIUSO_ERR_SCHED.name());
                    sessioneFiglio.setFlSesErrVerif("1");
                    sessioneFiglio.setFlSesErrNonRisolub("0");
                    sessioneFiglio.setCdErr(MessaggiWSBundle.PING_PREPXML_FILE_020);
                    sessioneFiglio.setDlErr(MessaggiWSBundle.getString(MessaggiWSBundle.PING_PREPXML_FILE_020,
                            object.getCdKeyObject(), tiStato));
                    figlio.setTiStatoObject(Constants.StatoOggetto.CHIUSO_ERR_SCHED.name());
                    break;
                default:
                    throw new ParerUserError("Errore inatteso nel controllo degli oggetti figli di " + idObject);
                }
                // MEV#14653
                // il sistema elimina i file dall'area FTP
                // deleteDir(new BigDecimal(figlio.getPigVer().getIdVers()), figlio.getCdKeyObject());
            }
            // Punto 3.7 dell'analisi - tutti i figli dell'oggetto vengono disconnessi dal padre e azzerato il loro
            // progressivo
            if (stato.equals(Constants.StatoOggetto.DA_TRASFORMARE)) {
                figli = monitoraggioHelper.getTuttiFigli(object.getIdObject());
                for (PigObject figlio : figli) {
                    figlio.setPgOggettoTrasf(null);
                    figlio.setPigObjectPadre(null);
                }
            }

        } catch (ParerUserError ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Errore inaspettato nel recupero dell'errore : " + ExceptionUtils.getRootCauseMessage(ex), ex);
            throw new ParerUserError("Errore inaspettato nel recupero dell'errore");
        }
    }

    public MonVVisObjTrasfRowBean getMonVVisObjTrasfRowBean(BigDecimal idObjectTrasf) throws ParerUserError {
        MonVVisObjTrasf objTrasf = monitoraggioHelper.findViewById(MonVVisObjTrasf.class, idObjectTrasf);
        MonVVisObjTrasfRowBean row = null;
        if (objTrasf != null) {
            try {
                row = (MonVVisObjTrasfRowBean) Transform.entity2RowBean(objTrasf);
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                    | IllegalArgumentException | InvocationTargetException ex) {
                String msg = "Errore durante il recupero del dettaglio dell'oggetto generato "
                        + ExceptionUtils.getRootCauseMessage(ex);
                log.error(msg, ex);
                throw new ParerUserError(msg);
            }
        }
        return row;
    }

    /**
     * Verifica, per eseguire il reset dei flag verificato/non risolubile della sessione in versamenti falliti, se si
     * tratta di una sessione con stato CHIUSO_ERR_VERS di un oggetto il cui tipo versamento non è NO_ZIP o
     * ZIP_CON_XML_SACER o ZIP_NO_XML_SACER. Nel caso siamo nel dettaglio oggetto derivante da versamenti falliti, il
     * controllo sullo stato non deve essere fatto
     *
     * In questi caso, la sessione non deve essere considerata
     *
     * @param idSessione
     *            id sessione
     * @param checkStato
     *            true per verificare lo stato
     *
     * @return true se la sessione può essere resettata
     */
    public boolean checkSessionObjectDaVerif(BigDecimal idSessione, boolean checkStato) {
        PigSessioneIngest sessione = monitoraggioHelper.findById(PigSessioneIngest.class, idSessione);
        boolean result = true;
        if (sessione.getPigVer() != null && (!checkStato
                || (checkStato && sessione.getTiStato().equals(Constants.StatoOggetto.CHIUSO_ERR_VERS.name())))) {
            PigObject obj = monitoraggioHelper.getPigObject(new BigDecimal(sessione.getPigVer().getIdVers()),
                    sessione.getCdKeyObject());
            if (obj != null && !obj.getPigTipoObject().getTiVersFile().equals(Constants.TipoVersamento.NO_ZIP.name())
                    && !obj.getPigTipoObject().getTiVersFile().equals(Constants.TipoVersamento.ZIP_CON_XML_SACER.name())
                    && !obj.getPigTipoObject().getTiVersFile()
                            .equals(Constants.TipoVersamento.ZIP_NO_XML_SACER.name())) {
                result = false;
            }
        }
        return result;
    }

    public boolean checkTiVersPigObject(BigDecimal idObject, String... tiVersFile) {
        PigObject object = monitoraggioHelper.findById(PigObject.class, idObject);
        List<String> tiVersList = Arrays.asList(tiVersFile);
        return tiVersList.contains(object.getPigTipoObject().getTiVersFile());
    }

    public boolean checkTiStatoPigObject(BigDecimal idObject, String... tiStatoObject) {
        PigObject object = monitoraggioHelper.findById(PigObject.class, idObject);
        List<String> tiStatoList = Arrays.asList(tiStatoObject);
        return tiStatoList.contains(object.getTiStatoObject());
    }

    /*
     * Torna vero se il tipo vers file (tipo SIP) == tiVersFile e lo stato della sessione corrente in (tiStatoSessione)
     */
    public boolean checkTiVersFileAndInTiStatiSessione(BigDecimal idObject, String tiVersFile,
            String... tiStatiSessione) {
        PigObject object = monitoraggioHelper.findById(PigObject.class, idObject);
        PigTipoObject pigTipoObject = object.getPigTipoObject();
        PigSessioneIngest pigSessioneIngest = monitoraggioHelper.findById(PigSessioneIngest.class,
                object.getIdLastSessioneIngest());
        return (pigTipoObject.getTiVersFile().equals(tiVersFile)
                && Arrays.asList(tiStatiSessione).contains(pigSessioneIngest.getTiStato()));
    }

    /*
     * Torna false se l'oggetto non esiste oppure se lo stato dell'oggetto non è compreso tra quelli passati.
     */
    public boolean checkTiStatoPigObjectByVersAndKey(BigDecimal idVers, String cdKeyObject, String... tiStatoObject) {
        PigObject object = monitoraggioHelper.getPigObject(idVers, cdKeyObject);
        if (object == null) {
            return false;
        } else {
            List<String> tiStatoList = Arrays.asList(tiStatoObject);
            return tiStatoList.contains(object.getTiStatoObject());
        }
    }

    public boolean checkTiStatoPigSessioneIngest(BigDecimal idSessioneIngest, String... tiStato) {
        PigSessioneIngest ses = monitoraggioHelper.findById(PigSessioneIngest.class, idSessioneIngest);
        /* MEV#15910 - se la sessione ha stato CHIUSO_ERR_RECUPERABILE la sessione non deve essere l'ultima! */
        if (ses.getTiStato().equals(Constants.StatoSessioneIngest.CHIUSO_ERR_RECUPERABILE.name())) {
            PigObject ogg = ses.getPigObject();
            if (ogg == null) {
                return false;
            } else {
                // Se è l'ultima non va bene altrimenti si
                if (ogg.getIdLastSessioneIngest().longValueExact() == idSessioneIngest.longValueExact()) {
                    return false;
                } else {
                    return true;
                }
            }
        }
        // Nel caso != da CHIUSO_ERR_RECUPERABILE tutto come prima
        List<String> tiStatoList = Arrays.asList(tiStato);
        return tiStatoList.contains(ses.getTiStato());
    }

    public MonVVisUnitaDocSessioneRowBean getMonVVisUnitaDocSessioneRowBean(BigDecimal idUnitaDocSessione)
            throws ParerUserError {
        MonVVisUnitaDocSessioneRowBean row = null;
        try {
            MonVVisUnitaDocSessione record = monitoraggioHelper.findViewById(MonVVisUnitaDocSessione.class,
                    idUnitaDocSessione);
            row = (MonVVisUnitaDocSessioneRowBean) Transform.entity2RowBean(record);

            String ambienteVers = row.getNmAmbienteVers() != null ? row.getNmAmbienteVers() : "";
            String vers = row.getNmVers() != null ? ", " + row.getNmVers() : "";
            String registro = row.getCdRegistroUnitaDocSacer() != null ? row.getCdRegistroUnitaDocSacer() : "";
            String anno = row.getAaUnitaDocSacer() != null ? " - " + row.getAaUnitaDocSacer().toString() : "";
            String numero = row.getCdKeyUnitaDocSacer() != null ? " - " + row.getCdKeyUnitaDocSacer().toString() : "";
            row.setString("versatore", ambienteVers + vers);
            row.setString("chiave_ud", registro + anno + numero);
            // Formatto col "." e assegno il valore ad un campo stringa
            if (row.getNiSizeFileByte() == null) {
                row.setNiSizeFileByte(BigDecimal.ZERO);
            }
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                | IllegalArgumentException | InvocationTargetException ex) {
            String msg = "Errore durante il recupero dell'unità documentaria versamento "
                    + ExceptionUtils.getRootCauseMessage(ex);
            log.error(msg, ex);
            throw new ParerUserError(msg);
        }
        return row;
    }

    /*
     * Verifica se per una sessione di versamento è possibile modificate i flag "verificato" e "Non risolubile"
     */
    public boolean canModifyVerificataNonRisolubile(BigDecimal idSessioneIngest) {
        boolean checkStatoSessione = checkTiStatoPigSessioneIngest(idSessioneIngest,
                Constants.StatoSessioneIngest.CHIUSO_ERR.name(), Constants.StatoSessioneIngest.CHIUSO_ERR_NOTIF.name(),
                Constants.StatoSessioneIngest.CHIUSO_ERR_SCHED.name(),
                Constants.StatoSessioneIngest.CHIUSO_ERR_CODA.name(),
                Constants.StatoSessioneIngest.CHIUSO_ERR_VERS.name(), Constants.StatoSessioneIngest.ANNULLATA.name(),
                Constants.StatoSessioneIngest.CHIUSO_ERR_RECUPERABILE.name());
        boolean checkTipoVersamentoFile = false;
        BigDecimal idObject = null;
        PigSessioneIngest pigSessioneIngest = monitoraggioHelper.findById(PigSessioneIngest.class, idSessioneIngest);
        PigObject pigObject = pigSessioneIngest.getPigObject();
        if (pigObject != null) {
            idObject = new BigDecimal(pigObject.getIdObject());
        }
        if (idObject != null) {
            checkTipoVersamentoFile = checkTiVersPigObject(idObject, Constants.TipoVersamento.NO_ZIP.name(),
                    Constants.TipoVersamento.ZIP_CON_XML_SACER.name(),
                    Constants.TipoVersamento.ZIP_NO_XML_SACER.name());
        }
        /*
         * Setto in edit mode i campi del flag versamento fallito verificato e non risolubile a determinate condizioni
         */
        if ((checkStatoSessione && idObject != null && checkTipoVersamentoFile) || idObject == null) {
            return true;
        } else {
            return false;
        }
    }

    // MEV 28877
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    @Interceptors({ it.eng.sacerasi.aop.TransactionInterceptor.class })
    public void modificaTipoOggetto(BigDecimal idObject, BigDecimal idTipoObject) {
        PigObject object = monitoraggioHelper.findById(PigObject.class, idObject);
        PigTipoObject pigTipoObject = monitoraggioHelper.findById(PigTipoObject.class, idTipoObject);

        object.setPigTipoObject(pigTipoObject);
    }

}