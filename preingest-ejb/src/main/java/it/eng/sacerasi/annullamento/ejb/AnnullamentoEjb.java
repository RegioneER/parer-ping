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

package it.eng.sacerasi.annullamento.ejb;

import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import it.eng.parer.ws.xml.richAnnullVers.RichiestaAnnullamentoVersamenti;
import it.eng.parer.ws.xml.richAnnullVers.RichiestaAnnullamentoVersamenti.VersamentiDaAnnullare;
import it.eng.parer.ws.xml.richAnnullVers.TipoVersamentoType;
import it.eng.parer.ws.xml.richAnnullVers.VersamentoDaAnnullareType;
import it.eng.sacerasi.annullamento.helper.AnnullamentoHelper;
import it.eng.sacerasi.common.Constants;
import it.eng.sacerasi.corrispondenzeVers.helper.CorrispondenzeVersHelper;
import it.eng.sacerasi.entity.PigObject;
import it.eng.sacerasi.entity.PigSessioneIngest;
import it.eng.sacerasi.entity.PigSisma;
import it.eng.sacerasi.entity.PigStatoSessioneIngest;
import it.eng.sacerasi.entity.PigStrumentiUrbanistici;
import it.eng.sacerasi.entity.PigStrumentiUrbanistici.TiStato;
import it.eng.sacerasi.entity.PigTipoObject;
import it.eng.sacerasi.entity.PigUnitaDocObject;
import it.eng.sacerasi.entity.PigUnitaDocSessione;
import it.eng.sacerasi.entity.PigVers;
import it.eng.sacerasi.entity.PigXmlAnnulSessioneIngest;
import it.eng.sacerasi.entity.PigXmlSacerUnitaDoc;
import it.eng.sacerasi.entity.PigXmlSacerUnitaDocSes;
import it.eng.sacerasi.exception.ParerInternalError;
import it.eng.sacerasi.exception.ParerUserError;
import it.eng.sacerasi.helper.RichiestaSacerHelper;
import it.eng.sacerasi.job.coda.ejb.PayloadManagerEjb;
import it.eng.sacerasi.job.dto.EsitoConnessione;
import it.eng.sacerasi.job.dto.RichiestaSacerInput;
import it.eng.sacerasi.sisma.ejb.SismaHelper;
import it.eng.sacerasi.slite.gen.tablebean.PigXmlAnnulSessioneIngestRowBean;
import it.eng.sacerasi.strumentiUrbanistici.ejb.StrumentiUrbanisticiHelper;
import it.eng.sacerasi.viewEntity.UsrVAbilStrutSacerXping;
import it.eng.sacerasi.web.helper.ConfigurationHelper;
import it.eng.sacerasi.web.helper.MonitoraggioHelper;
import it.eng.sacerasi.web.util.Transform;
import it.eng.sacerasi.ws.ejb.XmlContextCache;

/**
 *
 * @author Bonora_L
 */
@Stateless
@LocalBean
@Interceptors({ it.eng.sacerasi.aop.TransactionInterceptor.class })
public class AnnullamentoEjb {

    private static final Logger logger = LoggerFactory.getLogger(AnnullamentoEjb.class);
    private static final String UD_002_001 = "UD-002-001";

    @EJB
    private AnnullamentoHelper helper;
    @EJB
    private MonitoraggioHelper monitoraggioHelper;
    @EJB
    private XmlContextCache xmlContextCache;
    @EJB
    private CorrispondenzeVersHelper corVersHelper;
    @EJB
    private ConfigurationHelper configurationHelper;
    @EJB
    private StrumentiUrbanisticiHelper strumentiUrbanisticiHelper;
    @EJB
    private SismaHelper sismaHelper;
    @EJB
    private PayloadManagerEjb payloadManagerHelper;
    @EJB
    private RichiestaSacerHelper richiesta;

    @Resource
    private SessionContext context;

    public PigXmlAnnulSessioneIngestRowBean getPigXmlSessioneIngestRowBean(BigDecimal idSessioneIngest,
            String tiXmlAnnul) throws ParerUserError {
        PigXmlAnnulSessioneIngestRowBean rowBean = null;
        List<PigXmlAnnulSessioneIngest> xmlAnnuls = helper.retrievePigXmlAnnulSessioneIngests(idSessioneIngest,
                tiXmlAnnul);
        if (xmlAnnuls != null && !xmlAnnuls.isEmpty()) {
            if (xmlAnnuls.size() == 1) {
                PigXmlAnnulSessioneIngest xmlAnnul = xmlAnnuls.get(0);
                try {
                    rowBean = (PigXmlAnnulSessioneIngestRowBean) Transform.entity2RowBean(xmlAnnul);
                } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException
                        | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                    String msg = "Errore inatteso nel recupero dello xml di " + tiXmlAnnul.toLowerCase();
                    logger.error(msg, ex);
                    throw new ParerUserError(msg);
                }
            } else {
                throw new ParerUserError("Errore inatteso nel recupero dello xml di " + tiXmlAnnul.toLowerCase());
            }
        }
        return rowBean;
    }

    /**
     * Esegue l'annullamento dell'oggetto di PreIngest e, se necessario, l'invio della richiesta di annullamento a Sacer
     *
     * @param idObject
     *            id object
     * @param richiestoAnnullamentoVersamentiUD
     *            true se richiesto annullamento unita doc
     *
     * @param richiestoAnnullamentoVersamentiUDDuplicati
     *            true se richiesto annullamento ud duplicati
     * @param motivazioneAnnullamento
     *            motivazione dell'annullamento
     * @param username
     *            nome dell'utente che richiede l'annullamento
     *
     * @throws ParerUserError
     *             errore generico
     * @throws ParerInternalError
     *             errore generico
     */
    public void annullaOggetto(BigDecimal idObject, boolean richiestoAnnullamentoVersamentiUD,
            boolean richiestoAnnullamentoVersamentiUDDuplicati, String motivazioneAnnullamento, String username)
            throws ParerUserError, ParerInternalError {
        RichiestaSacerInput input = context.getBusinessObject(AnnullamentoEjb.class).eseguiAnnullamentoPing(idObject,
                richiestoAnnullamentoVersamentiUD, richiestoAnnullamentoVersamentiUDDuplicati, motivazioneAnnullamento,
                username);
        if (input != null) {
            // E' stato generato un xml di invio richiesta di annullamento a Sacer, eseguo l'attivazione del servizio
            // Chiamata a richiesta sacer RecuperoUnitaDocumentariaSync
            EsitoConnessione esitoConnAnnul = richiesta.upload(input);
            context.getBusinessObject(AnnullamentoEjb.class).gestisciRispostaAnnullamento(esitoConnAnnul, idObject);
            if (esitoConnAnnul.isErroreConnessione()) {
                // Il servizio non ha risposto per un errore di connessione
                throw new ParerUserError(
                        "Il servizio InvioRichiestaAnnullamentoVersamenti non risponde; verificare pi\u00F9 tardi se l'annullamento in Sacer \u00E8 terminato");
            }
        }
    }

    public void setAnnullatoInDaTrasformare(BigDecimal idObject, BigDecimal idTipoObject) {
        PigObject object = helper.findByIdWithLock(PigObject.class, idObject);
        // *** MEV#15178 - Cambio del tipo object
        PigTipoObject pigTipoObject = helper.findById(PigTipoObject.class, idTipoObject);
        object.setPigTipoObject(pigTipoObject);
        // ***
        BigDecimal idSessioneCorrente = object.getIdLastSessioneIngest();
        PigSessioneIngest sessioneCorrente = helper.findById(PigSessioneIngest.class, idSessioneCorrente);
        Date now = new Date();
        PigSessioneIngest nuovaSessione = monitoraggioHelper.creaSessione(sessioneCorrente, object, now,
                Constants.StatoSessioneIngest.DA_TRASFORMARE.name());
        monitoraggioHelper.creaStatoSessione(nuovaSessione, Constants.StatoSessioneIngest.DA_TRASFORMARE.name(), now);
        object.setTiStatoObject(Constants.StatoOggetto.DA_TRASFORMARE.name());
        object.setIdLastSessioneIngest(new BigDecimal(sessioneCorrente.getIdSessioneIngest()));
        object.setNiTotObjectTrasf(null);
        nuovaSessione.setNiTotObjectTrasf(null);
        object.setIdLastSessioneIngest(new BigDecimal(nuovaSessione.getIdSessioneIngest()));
        monitoraggioHelper.bulkDeletePigObjectTrasf(object.getIdObject());
        List<PigObject> l = object.getPigObjects();
        for (PigObject figlio : l) {
            figlio.setPgOggettoTrasf(null);
            figlio.setPigObjectPadre(null);
        }

        // MEV 31816 - correggo lo stato di un eventuale SISMA
        PigSisma pigSisma = sismaHelper.getPigSismaByCdKeyAndTiStato(object.getCdKeyObject(),
                PigSisma.TiStato.ANNULLATO);

        if (pigSisma != null) {
            // Setta lo stato di PigSisma
            Enum<Constants.TipoVersatore> tipo = sismaHelper.getTipoVersatore(pigSisma.getPigVer());
            if (tipo.equals(Constants.TipoVersatore.SA_PUBBLICO)
                    && pigSisma.getFlInviatoAEnte().equals(Constants.DB_FALSE)) {
                sismaHelper.aggiornaStato(pigSisma, PigSisma.TiStato.IN_TRASFORMAZIONE_SA);
            } else {
                sismaHelper.aggiornaStato(pigSisma, PigSisma.TiStato.IN_TRASFORMAZIONE);
            }
        }

        // MEV 31651 - correggo lo stato di un eventuale Strumento Urbanistico
        PigStrumentiUrbanistici pigStrumentiUrbanistici = strumentiUrbanisticiHelper
                .getPigStrumUrbByCdKeyAndTiStato(object.getCdKeyObject(), PigStrumentiUrbanistici.TiStato.ANNULLATO);
        if (pigStrumentiUrbanistici != null) {
            strumentiUrbanisticiHelper.aggiornaStato(pigStrumentiUrbanistici,
                    PigStrumentiUrbanistici.TiStato.IN_TRASFORMAZIONE);
        }
    }

    public void setErroreTrasformazione(BigDecimal idObject) {
        PigObject object = helper.findByIdWithLock(PigObject.class, idObject);
        BigDecimal idSessioneCorrente = object.getIdLastSessioneIngest();
        PigSessioneIngest sessioneCorrente = helper.findById(PigSessioneIngest.class, idSessioneCorrente);
        Date now = new Date();
        // MAC #23128 - non bisogna aprire una nuova sessione se si va in errore.
        monitoraggioHelper.creaStatoSessione(sessioneCorrente,
                Constants.StatoSessioneIngest.ERRORE_TRASFORMAZIONE.name(), now);
        object.setTiStatoObject(Constants.StatoOggetto.ERRORE_TRASFORMAZIONE.name());
        object.setIdLastSessioneIngest(new BigDecimal(sessioneCorrente.getIdSessioneIngest()));
    }

    public void setChiusoErroreVers(BigDecimal idObject) {
        PigObject object = helper.findByIdWithLock(PigObject.class, idObject);
        BigDecimal idSessioneCorrente = object.getIdLastSessioneIngest();
        PigSessioneIngest sessioneCorrente = helper.findById(PigSessioneIngest.class, idSessioneCorrente);
        Date now = new Date();

        monitoraggioHelper.creaStatoSessione(sessioneCorrente, Constants.StatoSessioneIngest.CHIUSO_ERR_VERS.name(),
                now);

        sessioneCorrente.setDlErr("Intervento operatore su CHIUSO_ERR_TIMEOUT");

        object.setTiStatoObject(Constants.StatoOggetto.CHIUSO_ERR_VERS.name());
        object.setIdLastSessioneIngest(new BigDecimal(sessioneCorrente.getIdSessioneIngest()));

        // 30208 -gestione sisma e SU
        if (object.getPigObjectPadre() != null) {
            PigSisma pigSisma = sismaHelper.getPigSismaByCdKeyAndTiStato(object.getPigObjectPadre().getCdKeyObject(),
                    PigSisma.TiStato.IN_VERSAMENTO);

            if (pigSisma == null) {
                pigSisma = sismaHelper.getPigSismaByCdKeyAndTiStato(object.getPigObjectPadre().getCdKeyObject(),
                        PigSisma.TiStato.IN_VERSAMENTO_SA);
            }

            if (pigSisma != null) {
                // Setta lo stato di PigSisma
                sismaHelper.aggiornaStato(pigSisma, PigSisma.TiStato.ERRORE);
            }
        }

        if (object.getPigObjectPadre() != null) {
            PigStrumentiUrbanistici pigStrumentiUrbanistici = strumentiUrbanisticiHelper
                    .getPigStrumUrbByCdKeyAndTiStato(object.getPigObjectPadre().getCdKeyObject(),
                            PigStrumentiUrbanistici.TiStato.IN_VERSAMENTO);
            if (pigStrumentiUrbanistici != null) {
                strumentiUrbanisticiHelper.aggiornaStato(pigStrumentiUrbanistici,
                        PigStrumentiUrbanistici.TiStato.ERRORE);
            }
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public RichiestaSacerInput eseguiAnnullamentoPing(BigDecimal idObject, boolean richiestoAnnullamentoVersamentiUD,
            boolean richiestoAnnullamentoVersamentiUDDuplicati, String motivazioneAnnullamento, String username)
            throws ParerUserError {
        PigObject object = helper.findByIdWithLock(PigObject.class, idObject);
        PigTipoObject tipoObject = object.getPigTipoObject();
        final String tiVers = tipoObject.getTiVersFile();
        final Date now = Calendar.getInstance().getTime();
        RichiestaSacerInput input = null;

        if (tiVers.equals(Constants.TipoVersamento.DA_TRASFORMARE.name())) {
            // Punto 4 e 5 dell'analisi
            if (!(object.getTiStatoObject().equals(Constants.StatoOggetto.IN_ATTESA_FILE.name())
                    || object.getTiStatoObject().equals(Constants.StatoOggetto.DA_TRASFORMARE.name())
                    // MEV #14561 - Estensione annullamento oggetti in errore
                    || object.getTiStatoObject().equals(Constants.StatoOggetto.CHIUSO_ERR_NOTIF.name())
                    || object.getTiStatoObject().equals(Constants.StatoOggetto.TRASFORMAZIONE_NON_ATTIVA.name())
                    || object.getTiStatoObject().equals(Constants.StatoOggetto.CHIUSO_ERR_VERIFICA_HASH.name())
                    || object.getTiStatoObject().equals(Constants.StatoOggetto.CHIUSO_ERR_TRASFORMAZIONE.name())
                    || object.getTiStatoObject().equals(Constants.StatoOggetto.CHIUSO_ERR_VERSAMENTO_A_PING.name())
                    || !object.getPigObjectTrasfs().isEmpty())) {
                // se il controllo fallisce...
                throw new ParerUserError("L'oggetto di tipo SIP " + tiVers + " e stato " + object.getTiStatoObject()
                        + " non \u00E8 annullabile");
                // MEV#14652
            } else if ((!richiestoAnnullamentoVersamentiUD)
                    && tiVers.equals(Constants.TipoVersamento.DA_TRASFORMARE.name())
                    && (object.getTiStatoObject().equals(Constants.StatoOggetto.IN_ATTESA_FILE.name())
                            // MEV #14561 - Estensione annullamento oggetti in errore
                            || object.getTiStatoObject().equals(Constants.StatoOggetto.CHIUSO_ERR_NOTIF.name())
                            || object.getTiStatoObject().equals(Constants.StatoOggetto.DA_TRASFORMARE.name())
                            || object.getTiStatoObject().equals(Constants.StatoOggetto.TRASFORMAZIONE_NON_ATTIVA.name())
                            || object.getTiStatoObject().equals(Constants.StatoOggetto.CHIUSO_ERR_VERIFICA_HASH.name())
                            || object.getTiStatoObject().equals(Constants.StatoOggetto.CHIUSO_ERR_TRASFORMAZIONE.name())
                            || object.getTiStatoObject()
                                    .equals(Constants.StatoOggetto.CHIUSO_ERR_VERSAMENTO_A_PING.name()))) {

                logger.debug(
                        "Aggiorna l'oggetto con stato ANNULLATO e registra un nuovo stato corrente della sessione");
                // Aggiorna l'oggetto con stato ANNULLATO e registra un nuovo stato corrente della sessione
                object.setTiStatoObject(Constants.StatoOggetto.ANNULLATO.name());
                monitoraggioHelper.creaStatoSessione(object.getIdLastSessioneIngest(),
                        Constants.StatoSessioneIngest.ANNULLATA.name(), now);
                annullamentoEventualeStrumentoUrbanistico(object, tipoObject.getTiVersFile());
                annullamentoEventualeSisma(object, tipoObject.getTiVersFile());
            }
        } else {
            // NO ZIP E ZIP NO/CON XML
            BigDecimal idVers = new BigDecimal(object.getPigVer().getIdVers());
            boolean isLastVerificata = monitoraggioHelper.isLastVerificata(idVers, object.getCdKeyObject(),
                    tipoObject.getNmTipoObject());
            boolean isLastNonRisolubile = monitoraggioHelper.isLastNonRisolubile(idVers, object.getCdKeyObject(),
                    tipoObject.getNmTipoObject());
            // Punto 3 dell'analisi
            if (!(object.getTiStatoObject().equals(Constants.StatoOggetto.CHIUSO_OK.name()) || // MEV #14561 -
                                                                                               // Estensione
                                                                                               // annullamento oggetti
                                                                                               // in errore (aggiunti 4
                                                                                               // altri test in OR)
                    ((object.getTiStatoObject().equals(Constants.StatoOggetto.CHIUSO_ERR_NOTIF.name())
                            || object.getTiStatoObject().equals(Constants.StatoOggetto.CHIUSO_ERR_VERIFICA_HASH.name())
                            || object.getTiStatoObject().equals(Constants.StatoOggetto.CHIUSO_ERR_SCHED.name())
                            || object.getTiStatoObject().equals(Constants.StatoOggetto.CHIUSO_ERR_CODA.name())
                            || object.getTiStatoObject().equals(Constants.StatoOggetto.CHIUSO_ERR_VERS.name()))
                            && isLastVerificata && isLastNonRisolubile))) {
                throw new ParerUserError("L'oggetto di tipo SIP " + tiVers + " e stato " + object.getTiStatoObject()
                        + " non \u00E8 annullabile");
            } else if (richiestoAnnullamentoVersamentiUD
                    && (object.getTiStatoObject().equals(Constants.StatoOggetto.CHIUSO_ERR_VERS.name())
                            || object.getTiStatoObject().equals(Constants.StatoOggetto.CHIUSO_OK.name())
                            || object.getTiStatoObject().equals(Constants.StatoOggetto.CHIUSO_ERR_CODA.name()))
                    && // MAC #13060 - Chiamata al servizio di richiesta annullamento di un oggetto contenente solo UD
                       // in
                       // stato VERSATA_ERR
                    esisteUdStatoChiusoOErr(object)) {

                logger.debug(
                        "STATO CHIUSO_ERR_VERS, CHIUSO_OK - Aggiorna tutte le unità documentarie dell'oggetto e della sessione con stato VERSATA_OK assegnando stato IN_CORSO_ANNULLAMENTO");
                // Aggiorna tutte le unità  documentarie dell'oggetto e della sessione con stato VERSATA_OK assegnando
                // stato IN_CORSO_ANNULLAMENTO
                helper.updateUnitaDocSessione(object.getIdLastSessioneIngest(),
                        Constants.StatoUnitaDocSessione.VERSATA_OK.name(),
                        Constants.StatoUnitaDocSessione.IN_CORSO_ANNULLAMENTO.name());
                helper.updateUnitaDocObject(idObject, Constants.StatoUnitaDocObject.VERSATA_OK.name(),
                        Constants.StatoUnitaDocObject.IN_CORSO_ANNULLAMENTO.name());

                // SUE26200 - se flag impostata a false allora NON annulleremo le ud con errore UD-002-001
                if (richiestoAnnullamentoVersamentiUDDuplicati) {
                    helper.updateUnitaDocSessioneWithError(object.getIdLastSessioneIngest(),
                            Constants.StatoUnitaDocSessione.VERSATA_ERR.name(), UD_002_001,
                            Constants.StatoUnitaDocSessione.IN_CORSO_ANNULLAMENTO.name());
                    helper.updateUnitaDocObjectWithError(idObject, Constants.StatoUnitaDocObject.VERSATA_ERR.name(),
                            UD_002_001, Constants.StatoUnitaDocObject.IN_CORSO_ANNULLAMENTO.name());
                }

                logger.debug("STATO CHIUSO_ERR_VERS, CHIUSO_OK - Prepara XML di richiesta annullamento");

                // SUE26200 - Se l'oggetto contiene solo ud in errore UD-002-001 potrebbe non essere necessario chiamare
                // il servizio di annullamento di sacer, in tal caso annulliamo direttamente anche il PigObject cone nel
                // caso più sotto.
                if (helper.countPigUnitaDocObject(object.getIdObject(),
                        Constants.StatoUnitaDocObject.IN_CORSO_ANNULLAMENTO.name()) > 0) {
                    logger.debug(
                            "STATO CHIUSO_ERR_VERS, CHIUSO_OK - Aggiorna l'oggetto con stato IN_CORSO_ANNULLAMENTO e registra un nuovo stato corrente della sessione");
                    // Aggiorna l'oggetto con stato IN_CORSO_ANNULLAMENTO e registra un nuovo stato corrente della
                    // sessione
                    object.setTiStatoObject(Constants.StatoOggetto.IN_CORSO_ANNULLAMENTO.name());
                    monitoraggioHelper.creaStatoSessione(object.getIdLastSessioneIngest(),
                            Constants.StatoSessioneIngest.IN_CORSO_ANNULLAMENTO.name(), now);

                    input = generaRichiestaAnnullamentoVersamenti(object, motivazioneAnnullamento, username);
                    logger.debug("STATO CHIUSO_ERR_VERS, CHIUSO_OK - Salvo il record XML di richiesta annullamento");
                    PigSessioneIngest pigSessioneIngest = helper.findById(PigSessioneIngest.class,
                            object.getIdLastSessioneIngest());
                    PigXmlAnnulSessioneIngest xmlAnnul = new PigXmlAnnulSessioneIngest();
                    xmlAnnul.setBlXmlAnnul(input.getXmlRichiestaSacer());
                    xmlAnnul.setCdVersioneXmlAnnul(
                            configurationHelper.getValoreParamApplicByApplic(Constants.VERSIONE_XML_ANNUL));
                    xmlAnnul.setDtRegXmlAnnul(now);
                    xmlAnnul.setPigSessioneIngest(pigSessioneIngest);
                    xmlAnnul.setTiXmlAnnul(Constants.TipiXmlAnnul.RICHIESTA.name());
                    helper.insertEntity(xmlAnnul, true);
                } else {
                    object.setTiStatoObject(Constants.StatoOggetto.ANNULLATO.name());
                    PigSessioneIngest pigSessioneIngest = helper.findById(PigSessioneIngest.class,
                            object.getIdLastSessioneIngest());
                    monitoraggioHelper.creaStatoSessione(pigSessioneIngest,
                            Constants.StatoSessioneIngest.ANNULLATA.name(), now);
                }

                object.setFlRichAnnulTimeout("0");
                // MAC #13060 - Chiamata al servizio di richiesta annullamento di un oggetto contenente solo UD in stato
                // VERSATA_ERR
                // Punto 7 dell'analisi
            } else if (richiestoAnnullamentoVersamentiUD
                    && ((object.getTiStatoObject().equals(Constants.StatoOggetto.CHIUSO_ERR_VERS.name()) || // MEV#14561
                                                                                                            // -
                                                                                                            // Estensione
                                                                                                            // annullamento
                                                                                                            // oggetti
                                                                                                            // in errore
                            object.getTiStatoObject().equals(Constants.StatoOggetto.CHIUSO_ERR_NOTIF.name())
                            || object.getTiStatoObject().equals(Constants.StatoOggetto.CHIUSO_ERR_VERIFICA_HASH.name())
                            || object.getTiStatoObject().equals(Constants.StatoOggetto.CHIUSO_ERR_SCHED.name())
                            || object.getTiStatoObject().equals(Constants.StatoOggetto.CHIUSO_ERR_CODA.name())) && // MEV#14100
                                                                                                                   // -
                                                                                                                   // Tolto
                                                                                                                   // il
                                                                                                                   // check
                                                                                                                   // su
                                                                                                                   // CHIUSO_OK
                                                                                                                   // ||
                                                                                                                   // object.getTiStatoObject().equals(Constants.StatoOggetto.CHIUSO_OK.name()))
                                                                                                                   // &&
                            (!esisteUdStatoChiusoOErr(object)))) {

                object.setTiStatoObject(Constants.StatoOggetto.ANNULLATO.name());
                PigSessioneIngest pigSessioneIngest = helper.findById(PigSessioneIngest.class,
                        object.getIdLastSessioneIngest());
                monitoraggioHelper.creaStatoSessione(pigSessioneIngest, Constants.StatoSessioneIngest.ANNULLATA.name(),
                        now);
                List<PigUnitaDocSessione> lud = pigSessioneIngest.getPigUnitaDocSessiones();
                if (lud != null) {
                    for (PigUnitaDocSessione pigUnitaDocSessione : lud) {
                        if (pigUnitaDocSessione.getTiStatoUnitaDocSessione()
                                .equals(Constants.StatoUnitaDocSessione.VERSATA_ERR.name())
                                && (!(pigUnitaDocSessione.getCdErrSacer()
                                        .equals(Constants.COD_VERS_ERR_CHIAVE_DUPLICATA_NEW)
                                        && richiestoAnnullamentoVersamentiUDDuplicati))) {
                            // SUE26200 - Non annullo le sessioni delle UD in errore UD-002-001 perchè richiesto
                            // dall'utente.
                            pigUnitaDocSessione
                                    .setTiStatoUnitaDocSessione(Constants.StatoUnitaDocSessione.ANNULLATA.name());
                        }
                    }
                }
                List<PigUnitaDocObject> ll = object.getPigUnitaDocObjects();
                if (ll != null) {
                    for (PigUnitaDocObject pigUnitaDocObject : ll) {
                        if (pigUnitaDocObject.getTiStatoUnitaDocObject()
                                .equals(Constants.StatoUnitaDocObject.VERSATA_ERR.name())) {
                            // SUE26200 - Non annullo le sessioni delle UD in errore UD-002-001 perchè richiesto
                            // dall'utente.
                            if (!(pigUnitaDocObject.getCdErrSacer().equals(Constants.COD_VERS_ERR_CHIAVE_DUPLICATA_NEW)
                                    && richiestoAnnullamentoVersamentiUDDuplicati)) {
                                pigUnitaDocObject
                                        .setTiStatoUnitaDocObject(Constants.StatoUnitaDocObject.ANNULLATA.name());
                            }
                        }
                    }
                }
                // MEV#20819
                annullamentoEventualeStrumentoUrbanistico(object, tipoObject.getTiVersFile());
                annullamentoEventualeSisma(object, tipoObject.getTiVersFile());

            } else if (!richiestoAnnullamentoVersamentiUD) {
                object.setTiStatoObject(Constants.StatoOggetto.ANNULLATO.name());
                monitoraggioHelper.creaStatoSessione(object.getIdLastSessioneIngest(),
                        Constants.StatoSessioneIngest.ANNULLATA.name(), now);
                // MEV#14100 punto 8.4.1 dell'analisi - Definisci oggetto padre
                PigObject oggPadre = object.getPigObjectPadre();
                if (oggPadre != null) {
                    try {
                        payloadManagerHelper.definisciStatoOggettoPadre(oggPadre.getIdObject());
                    } catch (Exception ex) {
                        throw new ParerUserError(ex.getMessage());
                    }
                }
                // MEV#20819
                annullamentoEventualeStrumentoUrbanistico(object, tipoObject.getTiVersFile());
                annullamentoEventualeSisma(object, tipoObject.getTiVersFile());

            }
        }
        return input;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void gestisciRispostaAnnullamento(EsitoConnessione esitoConnAnnul, BigDecimal idObject)
            throws ParerUserError, IllegalStateException {
        final Date now = Calendar.getInstance().getTime();
        String codiceErroreAnnul = esitoConnAnnul.getCodiceErrore();
        String codiceEsitoAnnul = esitoConnAnnul.getCodiceEsito();

        PigObject object = helper.findById(PigObject.class, idObject);
        if (esitoConnAnnul.isErroreConnessione()) {
            // Il servizio non ha risposto per un errore di connessione
            object.setFlRichAnnulTimeout("1");
            // MEV13062

            logger.debug("RISPOSTA ANNULLAMENTO - TimeOut di Connessione");
        } else {
            logger.debug("RISPOSTA ANNULLAMENTO - Salvo il record XML di risposta");
            List<PigXmlAnnulSessioneIngest> xmlResps = helper.retrievePigXmlAnnulSessioneIngests(
                    object.getIdLastSessioneIngest(), Constants.TipiXmlAnnul.RISPOSTA.name());
            PigXmlAnnulSessioneIngest xmlAnnul;
            PigSessioneIngest pigSessioneIngest = helper.findById(PigSessioneIngest.class,
                    object.getIdLastSessioneIngest());
            if (xmlResps.isEmpty()) {
                xmlAnnul = new PigXmlAnnulSessioneIngest();
                xmlAnnul.setPigSessioneIngest(pigSessioneIngest);
                xmlAnnul.setTiXmlAnnul(Constants.TipiXmlAnnul.RISPOSTA.name());
            } else {
                xmlAnnul = xmlResps.get(0);
            }
            xmlAnnul.setBlXmlAnnul(esitoConnAnnul.getXmlResponse());
            xmlAnnul.setCdVersioneXmlAnnul(
                    configurationHelper.getValoreParamApplicByApplic(Constants.VERSIONE_XML_ANNUL));
            xmlAnnul.setDtRegXmlAnnul(now);
            if (xmlResps.isEmpty()) {
                helper.insertEntity(xmlAnnul, true);
            }
            logger.debug("RISPOSTA ANNULLAMENTO - Richiesta non in timeout");
            object.setFlRichAnnulTimeout("0");

            List<PigUnitaDocSessione> listaUDSessione = null;
            if (codiceEsitoAnnul.equals(Constants.EsitoVersamento.POSITIVO.name())
                    || codiceEsitoAnnul.equals(Constants.EsitoVersamento.WARNING.name())
                    || (codiceEsitoAnnul.equals(Constants.EsitoVersamento.NEGATIVO.name()) && codiceErroreAnnul != null
                            && codiceErroreAnnul.equals("RICH-ANN-VERS-015"))) {
                // Oggetto annullato
                logger.debug("Aggiorno lo stato dell'oggetto ad ANNULLATO");
                object.setTiStatoObject(Constants.StatoOggetto.ANNULLATO.name());
                monitoraggioHelper.creaStatoSessione(object.getIdLastSessioneIngest(),
                        Constants.StatoUnitaDocObject.ANNULLATA.name(), now);
                // Aggiorna tutte le unità  documentarie dell'oggetto e della sessione con stato IN_CORSO_ANNULLAMENTO
                // assegnando stato ANNULLATA
                logger.debug(
                        "Aggiorna tutte le unità documentarie dell'oggetto e della sessione con stato DA_VERSARE assegnando stato annullata");
                // MEV#14663 - Punto 4.4 dell'analisi
                if (xmlAnnul.getCdVersioneXmlAnnul().equals("1.0") || xmlAnnul.getCdVersioneXmlAnnul().equals("1.1")) {
                    // vecchio comportamento (per poter funzionare anche con una versione di SACER vecchia)
                    helper.updateUnitaDocSessione(object.getIdLastSessioneIngest(),
                            Constants.StatoUnitaDocSessione.IN_CORSO_ANNULLAMENTO.name(),
                            Constants.StatoUnitaDocSessione.ANNULLATA.name());
                    helper.updateUnitaDocObject(idObject, Constants.StatoUnitaDocObject.IN_CORSO_ANNULLAMENTO.name(),
                            Constants.StatoUnitaDocObject.ANNULLATA.name());
                } else {
                    // nuovo comportamento
                    try {
                        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                        DocumentBuilder builder = factory.newDocumentBuilder();
                        // Estrae l'XMl di risposta annullamento sessione
                        String strXml = null;
                        List<PigXmlAnnulSessioneIngest> listaAnnul = pigSessioneIngest.getPigXmlAnnulSessioneIngests();
                        for (PigXmlAnnulSessioneIngest pigXmlAnnulSessioneIngest : listaAnnul) {
                            if (pigXmlAnnulSessioneIngest.getTiXmlAnnul()
                                    .equals(Constants.TipiXmlAnnul.RISPOSTA.name())) {
                                strXml = pigXmlAnnulSessioneIngest.getBlXmlAnnul();
                                break;
                            }
                        }
                        InputSource is = new InputSource(new StringReader(strXml));
                        Document doc = builder.parse(is);
                        XPath xPath = XPathFactory.newInstance().newXPath();
                        // Punto 4.5.1, DETERMINA TAG XML ...
                        listaUDSessione = pigSessioneIngest.getPigUnitaDocSessiones();
                        if (listaUDSessione != null) {
                            for (PigUnitaDocSessione pigUnitaDocSessione : listaUDSessione) {
                                if (pigSessioneIngest.getTiStato()
                                        .equals(Constants.StatoUnitaDocSessione.IN_CORSO_ANNULLAMENTO.name())) {
                                    // Determina l'XML di versamento della UD
                                    List<PigXmlSacerUnitaDocSes> listaPigXmlSacerUnitaDocSes = pigUnitaDocSessione
                                            .getPigXmlSacerUnitaDocSes();
                                    for (PigXmlSacerUnitaDocSes pigXmlSacerUnitaDocSes : listaPigXmlSacerUnitaDocSes) {
                                        if (pigXmlSacerUnitaDocSes.getTiXmlSacer()
                                                .equals(Constants.TipiXmlSacer.XML_VERS.name())) {
                                            ChiaveUd chiaveUd = estraiXPathPerEsitoAnnullamento(
                                                    pigXmlSacerUnitaDocSes.getBlXmlSacer());
                                            XPathExpression expr = xPath.compile(
                                                    "//EsitoRichiestaAnnullamentoVersamenti/VersamentiDaAnnullare/VersamentoDaAnnullare[TipoVersamento=\"UNITA' DOCUMENTARIA\" and TipoRegistro='"
                                                            + chiaveUd.getRegistro() + "' and Anno='"
                                                            + chiaveUd.getAnno() + "' and Numero='"
                                                            + chiaveUd.getNumero()
                                                            + "' and (Stato='ANNULLATO' or (Stato='NON_ANNULLABILE' and contains(ErroriRilevati/text(),'ITEM_GIA_ANNULLATO')) )]");
                                            NodeList nodes = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
                                            if (nodes.getLength() > 0) {
                                                pigUnitaDocSessione.setTiStatoUnitaDocSessione(
                                                        Constants.StatoUnitaDocSessione.ANNULLATA.name());
                                            }
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                        // MEV#14663 - Punto 4.5.3, DETERMINA TAG XML ...
                        List<PigUnitaDocObject> listaUDObject = object.getPigUnitaDocObjects();
                        if (listaUDObject != null) {
                            for (PigUnitaDocObject pigUnitaDocObject : listaUDObject) {
                                if (pigUnitaDocObject.getTiStatoUnitaDocObject()
                                        .equals(Constants.StatoUnitaDocObject.IN_CORSO_ANNULLAMENTO.name())) {
                                    // Determina l'XML di versamento della UD
                                    List<PigXmlSacerUnitaDoc> listaPigXmlSacerUnitaDoc = pigUnitaDocObject
                                            .getPigXmlSacerUnitaDocs();
                                    for (PigXmlSacerUnitaDoc pigXmlSacerUnitaDoc : listaPigXmlSacerUnitaDoc) {
                                        if (pigXmlSacerUnitaDoc.getTiXmlSacer()
                                                .equals(Constants.TipiXmlSacer.XML_VERS.name())) {
                                            ChiaveUd chiaveUd = estraiXPathPerEsitoAnnullamento(
                                                    pigXmlSacerUnitaDoc.getBlXmlSacer());
                                            XPathExpression expr = xPath.compile(
                                                    "//EsitoRichiestaAnnullamentoVersamenti/VersamentiDaAnnullare/VersamentoDaAnnullare[TipoVersamento=\"UNITA' DOCUMENTARIA\" and TipoRegistro='"
                                                            + chiaveUd.getRegistro() + "' and Anno='"
                                                            + chiaveUd.getAnno() + "' and Numero='"
                                                            + chiaveUd.getNumero()
                                                            + "' and (Stato='ANNULLATO' or (Stato='NON_ANNULLABILE' and contains(ErroriRilevati/text(),'ITEM_GIA_ANNULLATO')) )]");
                                            NodeList nodes = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
                                            if (nodes.getLength() > 0) {
                                                pigUnitaDocObject.setTiStatoUnitaDocObject(
                                                        Constants.StatoUnitaDocObject.ANNULLATA.name());
                                            }
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    } catch (Throwable ex) {
                        throw new ParerUserError(ex.getMessage());
                    }
                    // MEV#14663 - Punto 4.5.5 dell'analisi
                    listaUDSessione = pigSessioneIngest.getPigUnitaDocSessiones();
                    if (listaUDSessione != null) {
                        for (PigUnitaDocSessione pigUnitaDocSessione : listaUDSessione) {
                            // MAC#15507
                            if (pigUnitaDocSessione.getTiStatoUnitaDocSessione()
                                    .equals(Constants.StatoUnitaDocSessione.IN_CORSO_ANNULLAMENTO.name())) {
                                if (pigUnitaDocSessione.getCdErrSacer() != null && pigUnitaDocSessione.getCdErrSacer()
                                        .equals(Constants.COD_VERS_ERR_CHIAVE_DUPLICATA_NEW)) {
                                    pigUnitaDocSessione.setTiStatoUnitaDocSessione(
                                            Constants.StatoUnitaDocSessione.VERSATA_ERR.name());
                                } else {
                                    pigUnitaDocSessione.setTiStatoUnitaDocSessione(
                                            Constants.StatoUnitaDocSessione.VERSATA_OK.name());
                                }
                            }
                        }
                    }
                    // MEV#14663 - Punto 4.5.6 dell'analisi
                    List<PigUnitaDocObject> listaUDObject = object.getPigUnitaDocObjects();
                    if (listaUDObject != null) {
                        for (PigUnitaDocObject pigUnitaDocObject : listaUDObject) {
                            if (pigUnitaDocObject.getTiStatoUnitaDocObject()
                                    .equals(Constants.StatoUnitaDocObject.IN_CORSO_ANNULLAMENTO.name())) {
                                if (pigUnitaDocObject.getCdErrSacer() != null && pigUnitaDocObject.getCdErrSacer()
                                        .equals(Constants.COD_VERS_ERR_CHIAVE_DUPLICATA_NEW)) {
                                    pigUnitaDocObject
                                            .setTiStatoUnitaDocObject(Constants.StatoUnitaDocObject.VERSATA_ERR.name());
                                } else {
                                    pigUnitaDocObject
                                            .setTiStatoUnitaDocObject(Constants.StatoUnitaDocObject.VERSATA_OK.name());
                                }
                            }
                        }
                    }
                } // fine else
                  // -----
                PigTipoObject tipoObject = object.getPigTipoObject();
                final String tiVers = tipoObject.getTiVersFile();
                if (tiVers.equals(Constants.TipoVersamento.NO_ZIP.name())
                        || tiVers.equals(Constants.TipoVersamento.ZIP_NO_XML_SACER.name())
                        || tiVers.equals(Constants.TipoVersamento.ZIP_CON_XML_SACER.name())) {
                    // L'oggetto da annullare si riferisce a un oggetto padre e tutti i suoi figli hanno stato ANNULLATO
                    PigObject oggPadre = object.getPigObjectPadre();
                    if (oggPadre != null) {
                        try {
                            // MEV#14100
                            payloadManagerHelper.definisciStatoOggettoPadre(oggPadre.getIdObject());
                        } catch (Exception ex) {
                            throw new ParerUserError(ex.getMessage());
                        }
                    }
                }
                // MEV#20819
                annullamentoEventualeStrumentoUrbanistico(object, tipoObject.getTiVersFile());
                annullamentoEventualeSisma(object, tipoObject.getTiVersFile());
            }
        }
    }

    /*
     * tiVers = tiVersFile di Tipo Object
     * 
     * Annulla un eventuale Strumento urbanistico legato all'oggetto passato
     */
    private void annullamentoEventualeStrumentoUrbanistico(PigObject object, String tiVers) {
        // MEV#20819 - (Punto 4.7 dell'analisi)
        if (tiVers.equals(Constants.TipoVersamento.ZIP_CON_XML_SACER.name())) {
            PigObject oggPadre = object.getPigObjectPadre();
            if (oggPadre != null) {
                PigVers v = oggPadre.getPigVer();
                PigStrumentiUrbanistici s = strumentiUrbanisticiHelper.getSUByVersAndCdKey(v,
                        oggPadre.getCdKeyObject());
                if (s != null && (s.getTiStato().equals(TiStato.IN_ELABORAZIONE)
                        || s.getTiStato().equals(TiStato.VERSATO) || s.getTiStato().equals(TiStato.ERRORE))) {
                    // Nuova transazione
                    strumentiUrbanisticiHelper.aggiornaStatoInNuovaTransazione(s,
                            PigStrumentiUrbanistici.TiStato.ANNULLATO);
                }
            }
        } else if (tiVers.equals(Constants.TipoVersamento.DA_TRASFORMARE.name())) {
            PigVers v = object.getPigVer();
            PigStrumentiUrbanistici s = strumentiUrbanisticiHelper.getSUByVersAndCdKey(v, object.getCdKeyObject());
            if (s != null && (s.getTiStato().equals(TiStato.IN_ELABORAZIONE) || s.getTiStato().equals(TiStato.VERSATO)
                    || s.getTiStato().equals(TiStato.ERRORE))) {
                // Nuova transazione
                strumentiUrbanisticiHelper.aggiornaStatoInNuovaTransazione(s,
                        PigStrumentiUrbanistici.TiStato.ANNULLATO);
            }
        }
    }

    /*
     * tiVers = tiVersFile di Tipo Object
     * 
     * Annulla un eventuale Sisma legato all'oggetto passato #####DA USARE PER ANULLARE SISMA COME per STRUMENTI
     * URBANISTICI, cercare chiamate a eseguiAnnullamentoEventualeStrumentoUrbanistico!
     */
    // MEV 26398
    private void annullamentoEventualeSisma(PigObject object, String tiVers) {
        String cdKeyObject = object.getCdKeyObject();

        if (tiVers.equals(Constants.TipoVersamento.ZIP_CON_XML_SACER.name()) && object.getPigObjectPadre() != null) {
            cdKeyObject = object.getPigObjectPadre().getCdKeyObject();
        }

        PigSisma pigSisma = sismaHelper.getSismaCdKey(cdKeyObject);
        if (pigSisma != null) {
            Enum<Constants.TipoVersatore> tipoVersatore = sismaHelper.getTipoVersatore(pigSisma.getPigVer());
            if (tipoVersatore.equals(Constants.TipoVersatore.SA_PRIVATO)
                    && (pigSisma.getTiStato().equals(PigSisma.TiStato.COMPLETATO)
                            || pigSisma.getTiStato().equals(PigSisma.TiStato.IN_ELABORAZIONE))) {

                // MEV29704 - porta in da_verificare e non in annullato
                sismaHelper.aggiornaStatoInNuovaTransazione(pigSisma, PigSisma.TiStato.DA_VERIFICARE);
                sismaHelper.pulisciFlagVerificaDocumenti(pigSisma);

            } else if (tipoVersatore.equals(Constants.TipoVersatore.SA_PUBBLICO)
                    && pigSisma.getTiStato().equals(PigSisma.TiStato.VERSATO)) {
                sismaHelper.aggiornaStatoInNuovaTransazione(pigSisma, PigSisma.TiStato.ANNULLATO);
                // MAC27281
                sismaHelper.aggiornaInviatoEnteInNuovaTransazione(pigSisma, false);
            } else if (tipoVersatore.equals(Constants.TipoVersatore.SA_PUBBLICO)
                    && (pigSisma.getTiStato().equals(PigSisma.TiStato.COMPLETATO)
                            || pigSisma.getTiStato().equals(PigSisma.TiStato.IN_ELABORAZIONE))) {

                // se ho annullato il versamento del SA pubblico ma non quello dell'agenzia rimango in stato completato
                String idVersatoreAgenzia = configurationHelper
                        .getValoreParamApplicByApplic(Constants.ID_VERSATORE_AGENZIA);
                PigObject pigObjectAgenzia = monitoraggioHelper.getPigObject(new BigDecimal(idVersatoreAgenzia),
                        cdKeyObject);
                if (pigObjectAgenzia.getTiStatoObject().equals(Constants.StatoOggetto.ANNULLATO.name())) {
                    sismaHelper.aggiornaStatoInNuovaTransazione(pigSisma, PigSisma.TiStato.VERSATO);
                }
            }
        }
    }

    // MEV 26398
    public boolean controllaSeSimsmaNonAnnullabile(BigDecimal idObject) {
        PigObject object = helper.findByIdWithLock(PigObject.class, idObject);
        String cdKeyObject = object.getCdKeyObject();
        String tiVers = object.getPigTipoObject().getTiVersFile();
        PigVers pigVers = object.getPigVer();

        if (tiVers.equals(Constants.TipoVersamento.ZIP_CON_XML_SACER.name()) && object.getPigObjectPadre() != null) {
            cdKeyObject = object.getPigObjectPadre().getCdKeyObject();
            pigVers = object.getPigObjectPadre().getPigVer();
        }

        // il problema si pone se il sisma è in stato completato e noi annulliamo i pacchetti del'attuatore pubblico.
        PigSisma pigSisma = sismaHelper.getSismaCdKey(cdKeyObject);
        if (pigSisma != null) {
            Enum<Constants.TipoVersatore> tipoVersatore = sismaHelper.getTipoVersatore(pigSisma.getPigVer());
            if (tipoVersatore.equals(Constants.TipoVersatore.SA_PUBBLICO)
                    && pigSisma.getTiStato().equals(PigSisma.TiStato.COMPLETATO)
                    && sismaHelper.getTipoVersatore(pigVers).equals(Constants.TipoVersatore.SA_PUBBLICO)) {
                return true;
            }
        }

        return false;
    }

    // MEV 27691 - aggiunto un campo contenente la motivazione dell'annullamento UD.
    private RichiestaSacerInput generaRichiestaAnnullamentoVersamenti(PigObject object, String motivazioneAnnullamento,
            String username) throws ParerUserError {
        RichiestaAnnullamentoVersamenti richAnnulVers = new RichiestaAnnullamentoVersamenti();
        richAnnulVers.setVersioneXmlRichiesta(
                configurationHelper.getValoreParamApplicByApplic(Constants.VERSIONE_XML_ANNUL));
        richAnnulVers.setVersatore(new it.eng.parer.ws.xml.richAnnullVers.VersatoreType());
        richAnnulVers.setRichiesta(new it.eng.parer.ws.xml.richAnnullVers.RichiestaType());

        richAnnulVers.setVersamentiDaAnnullare(new VersamentiDaAnnullare());

        final BigDecimal idOrganizIamFromUdObject = helper
                .getIdOrganizIamFromMonVLisUnitaDocObject(object.getIdObject());

        final String cdKeyObject = object.getCdKeyObject();
        final BigDecimal idSessioneIngest = object.getIdLastSessioneIngest();
        richAnnulVers.getRichiesta()
                .setCodice("Annullamento oggetto " + cdKeyObject + " e sessione " + idSessioneIngest.toPlainString());
        richAnnulVers.getRichiesta().setDescrizione("Annullamento oggetto " + cdKeyObject);
        if (motivazioneAnnullamento != null && !motivazioneAnnullamento.isEmpty()) {
            richAnnulVers.getRichiesta().setMotivazione(
                    "Annullamento in PreIngest dell'oggetto " + cdKeyObject + " - " + motivazioneAnnullamento);
        } else {
            richAnnulVers.getRichiesta().setMotivazione("Annullamento in PreIngest dell'oggetto " + cdKeyObject);
        }
        richAnnulVers.getRichiesta().setImmediata(true);
        richAnnulVers.getRichiesta().setForzaAnnullamento(true);
        richAnnulVers.getRichiesta().setRichiestaDaPreIngest(true);

        List<PigUnitaDocObject> uds = helper.retrievePigUnitaDocObject(object.getIdObject(),
                Constants.StatoOggetto.IN_CORSO_ANNULLAMENTO.name());
        for (PigUnitaDocObject ud : uds) {
            VersamentoDaAnnullareType udVers = new VersamentoDaAnnullareType();
            udVers.setTipoVersamento(TipoVersamentoType.UNITA_DOCUMENTARIA);

            // Modifica: i dati della chiave UD li prende dall'XMl vi versamento sacer
            List<PigXmlSacerUnitaDoc> listaPigXmlSacerUnitaDoc = ud.getPigXmlSacerUnitaDocs();
            for (PigXmlSacerUnitaDoc pigXmlSacerUnitaDoc : listaPigXmlSacerUnitaDoc) {
                if (pigXmlSacerUnitaDoc.getTiXmlSacer().equals(Constants.TipiXmlSacer.XML_VERS.name())) {
                    ChiaveUd chiaveUd = estraiXPathPerEsitoAnnullamento(pigXmlSacerUnitaDoc.getBlXmlSacer());
                    udVers.setTipoRegistro(chiaveUd.getRegistro());
                    udVers.setAnno((int) ud.getAaUnitaDocSacer().longValue());
                    udVers.setNumero(chiaveUd.getNumero());
                    break;
                }
            }
            richAnnulVers.getVersamentiDaAnnullare().getVersamentoDaAnnullare().add(udVers);
        }
        // Per motivi di sicurezza i dati di user e pwd li ottengo per ultimi
        RichiestaSacerInput input = null;
        BigDecimal idAmbienteVers = BigDecimal
                .valueOf(object.getPigTipoObject().getPigVer().getPigAmbienteVer().getIdAmbienteVers());
        BigDecimal idVers = BigDecimal.valueOf(object.getPigTipoObject().getPigVer().getIdVers());
        BigDecimal idTipoObject = BigDecimal.valueOf(object.getPigTipoObject().getIdTipoObject());
        String nmUseridSacer = configurationHelper.getValoreParamApplicByTipoObj("USERID_USER_VERS", idAmbienteVers,
                idVers, idTipoObject);
        String cdPswSacer = configurationHelper.getValoreParamApplicByTipoObj("PSW_USER_VERS", idAmbienteVers, idVers,
                idTipoObject);

        richAnnulVers.getVersatore().setUserID(nmUseridSacer);
        richAnnulVers.getVersatore().setUtente(username);

        UsrVAbilStrutSacerXping strutturaAbilitata = corVersHelper.getStrutturaAbilitata(idOrganizIamFromUdObject,
                nmUseridSacer);
        richAnnulVers.getVersatore().setAmbiente(strutturaAbilitata.getNmAmbiente());
        richAnnulVers.getVersatore().setEnte(strutturaAbilitata.getNmEnte());
        richAnnulVers.getVersatore().setStruttura(strutturaAbilitata.getNmStrut());

        StringWriter tmpWriter = new StringWriter();
        // Eseguo il marshalling degli oggetti creati per salvarli poi nell'oggetto UnitaDocObject
        try {
            Marshaller recMarshaller = xmlContextCache.getRichAnnVersCtx_RichiestaAnnullamentoVersamenti()
                    .createMarshaller();
            recMarshaller.setSchema(xmlContextCache.getSchemaOfRichAnnVers());
            recMarshaller.marshal(richAnnulVers, tmpWriter);
            tmpWriter.flush();
        } catch (JAXBException ex) {
            logger.error("Eccezione", ex);
            throw new ParerUserError("Errore inatteso nella creazione della richiesta di annullamento");
        }
        Integer timeout = new Integer(configurationHelper.getValoreParamApplicByApplic(Constants.TIMEOUT_VERS_SACER));

        input = new RichiestaSacerInput(RichiestaSacerInput.TipoRichiestaSacer.ANNULLAMENTO,
                configurationHelper.getValoreParamApplicByApplic(Constants.VERSIONE_XML_ANNUL),
                configurationHelper.getValoreParamApplicByApplic(Constants.URL_ANNUL_VERS), tmpWriter.toString(), null,
                nmUseridSacer, cdPswSacer, timeout);
        return input;
    }

    /**
     * Verifica lo stato della richiesta di annullamento a Sacer
     *
     * @param idObject
     *            id object
     * @param username
     *            nome dell'utente che ha richiesto l'annullamento
     * 
     * @throws ParerUserError
     *             errore generico
     * @throws ParerInternalError
     *             errore generico
     */
    public void verificaAnnullamentoTerminato(BigDecimal idObject, String username)
            throws ParerUserError, ParerInternalError {
        RichiestaSacerInput input = context.getBusinessObject(AnnullamentoEjb.class).initVerificaAnnullamento(idObject,
                username);
        if (input != null) {
            // E' stato generato un xml di invio richiesta di annullamento a Sacer, eseguo l'attivazione del servizio
            // Chiamata a richiesta sacer RecuperoUnitaDocumentariaSync
            EsitoConnessione esitoConnAnnul = richiesta.upload(input);
            context.getBusinessObject(AnnullamentoEjb.class).gestisciRispostaAnnullamento(esitoConnAnnul, idObject);
            if (esitoConnAnnul.isErroreConnessione()) {
                // Il servizio non ha risposto per un errore di connessione
                throw new ParerUserError(
                        "Il servizio InvioRichiestaAnnullamentoVersamenti non risponde; verificare pi\u00F9 tardi se l'annullamento in Sacer \u00E8 terminato");
            }
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public RichiestaSacerInput initVerificaAnnullamento(BigDecimal idObject, String username) throws ParerUserError {
        PigObject object = helper.findByIdWithLock(PigObject.class, idObject);
        object.setFlRichAnnulTimeout("0");

        List<PigXmlAnnulSessioneIngest> xmlResps = helper.retrievePigXmlAnnulSessioneIngests(
                object.getIdLastSessioneIngest(), Constants.TipiXmlAnnul.RICHIESTA.name());
        PigXmlAnnulSessioneIngest xmlAnnul;
        if (!xmlResps.isEmpty()) {
            xmlAnnul = xmlResps.get(0);
        } else {
            throw new ParerUserError("Errore inatteso nella verifica: xml di richiesta non registrato");
        }

        // MEV 27691 - controllo che non ci sia una motivazione di annullamento custom
        String motivazioneAnnullamento = "";
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(xmlAnnul.getBlXmlAnnul()));
            Document doc = builder.parse(is);
            XPath xPath = XPathFactory.newInstance().newXPath();

            XPathExpression expr = xPath.compile("//RichiestaAnnullamentoVersamenti/Richiesta/Motivazione");
            NodeList nodes = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
            if (nodes.getLength() > 0) {
                String oldMotivazione = nodes.item(0).getTextContent();
                int motivazioneCustomIndex = oldMotivazione.indexOf("-");
                if (motivazioneCustomIndex != -1) {
                    motivazioneAnnullamento = oldMotivazione.substring(motivazioneCustomIndex + 1).trim();
                }
            }
        } catch (Exception ex) {
            throw new ParerUserError("Errore inatteso nella verifica : " + ex.getMessage());
        }

        RichiestaSacerInput input = generaRichiestaAnnullamentoVersamenti(object, motivazioneAnnullamento, username);
        if (input != null) {
            xmlAnnul.setBlXmlAnnul(input.getXmlRichiestaSacer());
            xmlAnnul.setDtRegXmlAnnul(Calendar.getInstance().getTime());
        }
        return input;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void accettaAnnullamentoFallito(BigDecimal idObject) {
        PigObject object = helper.findByIdWithLock(PigObject.class, idObject);
        PigSessioneIngest lastSessione = helper.findById(PigSessioneIngest.class, object.getIdLastSessioneIngest());
        PigStatoSessioneIngest lastStato = helper.findById(PigStatoSessioneIngest.class,
                lastSessione.getIdStatoSessioneIngestCor());
        logger.debug("Accetta annullamento fallito - Elimino lo stato IN_CORSO_ANNULLAMENTO");
        helper.removeEntity(lastStato, true);
        List<PigStatoSessioneIngest> descStati = monitoraggioHelper
                .retrievePigStatoSessioneIngest(object.getIdLastSessioneIngest());
        lastStato = descStati.get(0);
        logger.debug("Accetta annullamento fallito - Aggiorno la sessione con lo stato precedente {}",
                lastStato.getTiStato());
        lastSessione.setIdStatoSessioneIngestCor(new BigDecimal(lastStato.getIdStatoSessioneIngest()));
        lastSessione.setTiStato(lastStato.getTiStato());
        logger.debug("Accetta annullamento fallito - Aggiorno l'oggetto con lo stato della sessione");
        object.setTiStatoObject(lastStato.getTiStato());
        object.setFlRichAnnulTimeout(null);
        logger.debug(
                "Accetta annullamento fallito - Aggiorna tutte le unit\u00E0 documentarie dell'oggetto e della sessione con stato IN_CORSO_ANNULLAMENTO");
        // Aggiorna tutte le unitÃ  documentarie dell'oggetto e della sessione con stato IN_CORSO_ANNULLAMENTO e codice
        // errore NULLO assegnando stato VERSATA_OK
        helper.updateUnitaDocSessioneNoError(object.getIdLastSessioneIngest(),
                Constants.StatoUnitaDocSessione.IN_CORSO_ANNULLAMENTO.name(),
                Constants.StatoUnitaDocSessione.VERSATA_OK.name());
        helper.updateUnitaDocObjectNoError(idObject, Constants.StatoUnitaDocObject.IN_CORSO_ANNULLAMENTO.name(),
                Constants.StatoUnitaDocObject.VERSATA_OK.name());
        // Aggiorna tutte le unitÃ  documentarie dell'oggetto e della sessione con stato IN_CORSO_ANNULLAMENTO e codice
        // errore NON NULLO assegnando stato VERSATA_ERR
        helper.updateUnitaDocSessioneWithError(object.getIdLastSessioneIngest(),
                Constants.StatoUnitaDocSessione.IN_CORSO_ANNULLAMENTO.name(), null,
                Constants.StatoUnitaDocSessione.VERSATA_ERR.name());
        helper.updateUnitaDocObjectWithError(idObject, Constants.StatoUnitaDocObject.IN_CORSO_ANNULLAMENTO.name(), null,
                Constants.StatoUnitaDocObject.VERSATA_ERR.name());
        // Elimina lo XML di richiesta e quello di risposta di annullamento registrati per la sessione corrente
        // dell'oggetto
        logger.debug(
                "Accetta annullamento fallito - Elimina lo XML di richiesta e quello di risposta di annullamento registrati per la sessione corrente dell'oggetto");
        if (lastSessione.getPigXmlAnnulSessioneIngests() != null) {
            for (PigXmlAnnulSessioneIngest pigXmlAnnulSessioneIngest : lastSessione.getPigXmlAnnulSessioneIngests()) {
                helper.removeEntity(pigXmlAnnulSessioneIngest, true);
            }
        }
    }

    private boolean esisteUdStatoChiusoOErr(PigObject oggetto) {
        boolean esiste = false;

        List<PigUnitaDocObject> l = oggetto.getPigUnitaDocObjects();
        if (l != null) {
            for (Iterator<PigUnitaDocObject> iterator = l.iterator(); iterator.hasNext();) {
                PigUnitaDocObject ud = iterator.next();
                if (ud.getTiStatoUnitaDocObject().equals(Constants.StatoUnitaDocObject.VERSATA_OK.name())
                        || (ud.getTiStatoUnitaDocObject().equals(Constants.StatoUnitaDocObject.VERSATA_ERR.name())
                                && ud.getCdErrSacer() != null && ud.getCdErrSacer().equals(UD_002_001))) {
                    esiste = true;
                    break;
                }
            }
        }
        return esiste;
    }

    /*
     * Estrae dall'XML di versamento UD i dati della chiave UD e compone la stringa xpath per valutare l'XMl di
     * annullamento
     */
    private ChiaveUd estraiXPathPerEsitoAnnullamento(String xml) throws ParerUserError {
        ChiaveUd chiaveUd = null;

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(xml));
            Document doc = builder.parse(is);
            XPath xPath = XPathFactory.newInstance().newXPath();
            String query = "//UnitaDocumentaria/Intestazione/Chiave";
            XPathExpression expr = xPath.compile(query);
            NodeList nodes = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
            for (int i = 0; i < nodes.getLength(); i++) {
                NodeList nodiFigli = nodes.item(i).getChildNodes();
                String registroFiglio = null;
                String numeroFiglio = null;
                String annoFiglio = null;
                if (nodiFigli != null) {
                    for (int f = 0; f < nodiFigli.getLength(); f++) {
                        switch (nodiFigli.item(f).getNodeName()) {
                        case "TipoRegistro":
                            registroFiglio = nodiFigli.item(f).getTextContent();
                            break;
                        case "Anno":
                            annoFiglio = nodiFigli.item(f).getTextContent();
                            break;
                        case "Numero":
                            numeroFiglio = nodiFigli.item(f).getTextContent();
                            break;
                        }
                    }
                    chiaveUd = new ChiaveUd(annoFiglio, numeroFiglio, registroFiglio);
                }
            }
        } catch (Throwable ex) {
            throw new ParerUserError(ex.getMessage());
        }

        return chiaveUd;

    }

    private class ChiaveUd {

        private String anno;
        private String numero;
        private String registro;

        public ChiaveUd(String anno, String numero, String registro) {
            this.anno = anno;
            this.numero = numero;
            this.registro = registro;
        }

        public String getAnno() {
            return anno;
        }

        public void setAnno(String anno) {
            this.anno = anno;
        }

        public String getNumero() {
            return numero;
        }

        public void setNumero(String numero) {
            this.numero = numero;
        }

        public String getRegistro() {
            return registro;
        }

        public void setRegistro(String registro) {
            this.registro = registro;
        }

    }
}
