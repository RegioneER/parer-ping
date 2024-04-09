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

package it.eng.sacerasi.ws.invioOggettoAsincrono.ejb;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.transaction.UserTransaction;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.sacerasi.common.Constants;
import it.eng.sacerasi.entity.PigAmbienteVers;
import it.eng.sacerasi.entity.PigObject;
import it.eng.sacerasi.entity.PigVers;
import it.eng.sacerasi.web.helper.ConfigurationHelper;
import it.eng.sacerasi.web.helper.MonitoraggioHelper;
import it.eng.sacerasi.ws.dto.IRispostaWS.ErrorTypeEnum;
import it.eng.sacerasi.ws.dto.IRispostaWS.SeverityEnum;
import it.eng.sacerasi.ws.dto.RispostaControlli;
import it.eng.sacerasi.ws.ejb.SalvataggioDati;
import it.eng.sacerasi.ws.invioOggettoAsincrono.dto.InvioOggettoAsincronoEstesoInput;
import it.eng.sacerasi.ws.invioOggettoAsincrono.dto.InvioOggettoAsincronoExt;
import it.eng.sacerasi.ws.invioOggettoAsincrono.dto.InvioOggettoAsincronoInput;
import it.eng.sacerasi.ws.invioOggettoAsincrono.dto.RispostaWSInvioOggettoAsincrono;
import it.eng.sacerasi.ws.invioOggettoAsincrono.dto.WSDescInvioOggettoAsincrono;
import it.eng.sacerasi.ws.response.InvioOggettoAsincronoEstesoRisposta;
import it.eng.sacerasi.ws.response.InvioOggettoAsincronoRisposta;
import it.eng.sacerasi.ws.util.WsTransactionManager;

@Stateless(mappedName = "InvioOggettoAsincronoEjb")
@LocalBean
@TransactionManagement(TransactionManagementType.BEAN)
public class InvioOggettoAsincronoEjb {

    @Resource
    private UserTransaction utx;
    @EJB
    private SalvataggioDati salvataggioDati;
    @EJB
    private MonitoraggioHelper monitoraggioHelper;
    @EJB
    private ConfigurationHelper configurationHelper;
    @EJB
    private InvioOggettoAsincronoParserEjb invioOggettoAsincronoParser;
    @EJB
    private InvioOggettoAsincronoCheckEjb invioOggettoAsincronoCheck;
    @EJB
    private ControlliInvioOggettoAsincrono controlliInvioOggettoAsincrono;

    private static final Logger log = LoggerFactory.getLogger(InvioOggettoAsincronoEjb.class);
    private WsTransactionManager wtm;

    // MEV#27321 - Introduzione della priorità di versamento di un oggetto ZIP_CON_XML_SACER e NO_ZIP
    // aggiunto il parametro della priorita di versamento
    public InvioOggettoAsincronoRisposta invioOggettoAsincrono(String userName, String nmAmbiente, String nmVersatore,
            String cdKeyObject, String nmTipoObject, boolean flFileCifrato, boolean flForzaWarning,
            boolean flForzaAccettazione, String dlMotivazione, String cdVersioneXml, String xml,
            String tiPrioritaVersamento) {

        if (log.isDebugEnabled()) {
            log.debug(
                    "Ricevuta richiesta di InvioOggettoAsincrono con i parametri : nmAmbiente = {} , "
                            + "nmVersatore = {} , cdKeyObject = {} , nmTipoObject = {} , flFileCifrato = {} , "
                            + "flForzaWarning = {} , flForzaAccettazione = {} , " + "dlMotivazione = {}, "
                            + "cdVersioneXml = {} , tiPrioritaVersamento =  {} , " + "xml = {}",
                    nmAmbiente, nmVersatore, cdKeyObject, nmTipoObject, flFileCifrato, flForzaWarning,
                    flForzaAccettazione, dlMotivazione, cdVersioneXml, tiPrioritaVersamento,
                    StringUtils.abbreviate(xml, 10000));
        }
        RispostaWSInvioOggettoAsincrono rispostaWs = initRispostaWS(new InvioOggettoAsincronoRisposta());
        // Istanzio l'oggetto che contiene i parametri ricevuti
        InvioOggettoAsincronoInput inputParameters = new InvioOggettoAsincronoInput(nmAmbiente, nmVersatore,
                cdKeyObject, nmTipoObject, flFileCifrato, flForzaWarning, flForzaAccettazione, dlMotivazione,
                cdVersioneXml, xml, tiPrioritaVersamento);
        InvioOggettoAsincronoExt ioaExt = initInvioOggettoAsincronoExt(inputParameters);
        checkAndSaveInvioOggettoAsincrono(rispostaWs, ioaExt, userName);

        /*
         * MEV #15914: Accettare in automatico gli studi in warning, tenendo traccia del warning
         * 
         * Nel caso in cui il parametro ACCETTA_STUDI_IN_WARNING (sul versatore o sull'ambiente o sull'applicazione) sia
         * impostato a TRUE, se la prima chiamata genera un warning allora riverso lo stesso oggetto con una forzatura e
         * una motivazione. La stessa cosa che dovrebbe fare il DPI facendolo a mano dall'applicazione.
         */
        log.debug("Esito risposta: {}", rispostaWs.getInvioOggettoAsincronoRisposta().getCdEsito());
        if (rispostaWs.getInvioOggettoAsincronoRisposta().getCdEsito().equals(Constants.EsitoServizio.WARN)) {
            PigVers pigVers = monitoraggioHelper.findById(PigVers.class, ioaExt.getIdVersatore());
            PigAmbienteVers ambienteVers = pigVers.getPigAmbienteVer();
            String parametroAccettaStudi = configurationHelper.getValoreParamApplicByIdVers(
                    Constants.ACCETTA_STUDI_IN_WARNING, new BigDecimal(ambienteVers.getIdAmbienteVers()),
                    new BigDecimal(pigVers.getIdVers()));
            log.debug("Parametro ACCETTA_STUDI_IN_WARNING: {}", parametroAccettaStudi);
            if (parametroAccettaStudi != null && parametroAccettaStudi.equalsIgnoreCase("true")) {
                rispostaWs = initRispostaWS(new InvioOggettoAsincronoRisposta());
                // Istanzio l'oggetto che contiene i parametri ricevuti
                String parametroMotivazione = configurationHelper.getValoreParamApplicByIdVers(
                        "MOTIVAZIONE_ACCETTA_STUDI_WARNING", new BigDecimal(ambienteVers.getIdAmbienteVers()),
                        new BigDecimal(pigVers.getIdVers()));
                log.debug("Parametro MOTIVAZIONE_ACCETTA_STUDI_WARNING: {}", parametroAccettaStudi);
                inputParameters = new InvioOggettoAsincronoInput(nmAmbiente, nmVersatore, cdKeyObject, nmTipoObject,
                        flFileCifrato, false, true, parametroMotivazione, cdVersioneXml, xml, tiPrioritaVersamento);
                ioaExt = initInvioOggettoAsincronoExt(inputParameters);
                checkAndSaveInvioOggettoAsincrono(rispostaWs, ioaExt, userName);
            }
        }

        return rispostaWs.getInvioOggettoAsincronoRisposta();
    }

    public InvioOggettoAsincronoEstesoRisposta invioOggettoAsincronoEsteso(String userName, String nmAmbiente,
            String nmVersatore, String cdKeyObject, String dsObject, String nmTipoObject, boolean flFileCifrato,
            boolean flForzaWarning, boolean flForzaAccettazione, String dlMotivazione, String cdVersioneXml, String xml,
            String nmAmbienteObjectPadre, String nmVersatoreObjectPadre, String cdKeyObjectPadre,
            BigDecimal niTotObjectFigli, BigDecimal pgObjectFiglio, BigDecimal niUnitaDocAttese, String cdVersGen,
            String tiGestOggettiFigli, String tiPriorita, String tiPrioritaVersamento) {
        //
        if (log.isDebugEnabled()) {
            log.debug("Ricevuta richiesta di InvioOggettoAsincronoEsteso con i parametri : nmAmbiente = {}  , "
                    + "nmVersatore = {} , cdKeyObject = {} , nmTipoObject = {} , dsObject = {} , "
                    + "flFileCifrato = {} , flForzaWarning = {} , flForzaAccettazione = {}  , "
                    + "dlMotivazione = {} , cdVersioneXml = {} , " + "xml = {} , "
                    + "nmAmbienteObjectPadre = {} , nmVersatoreObjectPadre = {} , " + "cdKeyObjectPadre = {} , "
                    + "niTotObjectFigli = {}  , pgObjectFiglio = {} , niUnitaDocAttese = {} " + " , "
                    + "cdVersGen = {} , " + "tiGestOggettiFigli = {} , tiPriorita = {} , tiPrioritaVersamento = {}",
                    nmAmbiente, nmVersatore, cdKeyObject, nmTipoObject, dsObject, flFileCifrato, flForzaWarning,
                    flForzaAccettazione, dlMotivazione, cdVersioneXml, StringUtils.abbreviate(xml, 100),
                    nmAmbienteObjectPadre, nmVersatoreObjectPadre, cdKeyObjectPadre, niTotObjectFigli, pgObjectFiglio,
                    niUnitaDocAttese, cdVersGen, tiGestOggettiFigli, tiPriorita, tiPrioritaVersamento);
        }
        RispostaWSInvioOggettoAsincrono rispostaWs = initRispostaWS(new InvioOggettoAsincronoEstesoRisposta());

        // Istanzio l'oggetto che contiene i parametri ricevuti
        InvioOggettoAsincronoInput inputParameters = new InvioOggettoAsincronoEstesoInput(nmAmbiente, nmVersatore,
                cdKeyObject, dsObject, nmTipoObject, flFileCifrato, flForzaWarning, flForzaAccettazione, dlMotivazione,
                cdVersioneXml, xml, nmAmbienteObjectPadre, nmVersatoreObjectPadre, cdKeyObjectPadre, niTotObjectFigli,
                pgObjectFiglio, niUnitaDocAttese, cdVersGen, tiGestOggettiFigli, tiPriorita, tiPrioritaVersamento);
        // Istanzio l'Ext con l'oggetto creato
        InvioOggettoAsincronoExt ioaExt = initInvioOggettoAsincronoExt(inputParameters);

        checkAndSaveInvioOggettoAsincrono(rispostaWs, ioaExt, userName);

        return (InvioOggettoAsincronoEstesoRisposta) rispostaWs.getInvioOggettoAsincronoRisposta();
    }

    private RispostaWSInvioOggettoAsincrono initRispostaWS(InvioOggettoAsincronoRisposta risposta) {
        // Istanzio la risposta
        RispostaWSInvioOggettoAsincrono rispostaWs = new RispostaWSInvioOggettoAsincrono();
        rispostaWs.setInvioOggettoAsincronoRisposta(risposta);
        // Imposto l'esito della risposta di default OK
        rispostaWs.getInvioOggettoAsincronoRisposta().setCdEsito(Constants.EsitoServizio.OK);
        return rispostaWs;
    }

    private InvioOggettoAsincronoExt initInvioOggettoAsincronoExt(InvioOggettoAsincronoInput inputParameters) {
        InvioOggettoAsincronoExt ioaExt = new InvioOggettoAsincronoExt();
        ioaExt.setDescrizione(new WSDescInvioOggettoAsincrono());
        ioaExt.setInvioOggettoAsincronoInput(inputParameters);
        ioaExt.setDtApertura(Calendar.getInstance().getTime());
        return ioaExt;
    }

    /* IN QUESTO METODO ASSURDO VIENE CREATO ANCHE L'OGGETTO IN pig_object */
    private void checkAndSaveInvioOggettoAsincrono(RispostaWSInvioOggettoAsincrono rispostaWs,
            InvioOggettoAsincronoExt ioaExt, String userName) {
        log.debug("Inizio controlli di sessione");
        // Chiamo la classe (EJB) InvioOggettoAsincronoCheck che gestisce i controlli di sessione e di oggetto e popola
        // la
        // rispostaWs
        invioOggettoAsincronoCheck.checkSessione(ioaExt, rispostaWs);

        log.debug("Fine controlli di sessione");
        wtm = new WsTransactionManager(utx);

        if (rispostaWs.getSeverity() != SeverityEnum.ERROR
                && StringUtils.isNotBlank(ioaExt.getInvioOggettoAsincronoInput().getXml())) {
            log.debug("Inizio parsing xml");
            // Chiamo la classe InvioOggettoAsincronoParser che esegue il parse dell'xml, se presente
            invioOggettoAsincronoParser.parseXML(ioaExt, rispostaWs);
            log.debug("Fine parsing xml");
        }
        final String xml = ioaExt.getInvioOggettoAsincronoInput().getXml();
        final String cdVersioneXml = ioaExt.getInvioOggettoAsincronoInput().getCdVersioneXml();
        final String nmTipoObject = ioaExt.getInvioOggettoAsincronoInput().getNmTipoObject();
        rispostaWs.getInvioOggettoAsincronoRisposta().setXml(xml);
        rispostaWs.getInvioOggettoAsincronoRisposta().setCdVersioneXML(cdVersioneXml);

        Long idObject = null;
        if (rispostaWs.getSeverity() != SeverityEnum.ERROR) {
            log.debug("Inizio controlli oggetto");
            idObject = invioOggettoAsincronoCheck.checkObject(ioaExt, rispostaWs);
            log.debug("Fine controlli oggetto - idObject = {}", idObject);
        }
        boolean prosegui = true;
        RispostaControlli tmpRispostaControlli = new RispostaControlli();
        Long idSessione;

        if (StringUtils.isNotBlank(ioaExt.getInvioOggettoAsincronoInput().getNmAmbiente())
                && StringUtils.isNotBlank(ioaExt.getInvioOggettoAsincronoInput().getNmVersatore())
                && StringUtils.isNotBlank(ioaExt.getInvioOggettoAsincronoInput().getCdKeyObject())) {
            // Inizio transazione sessione
            log.debug("Apertura transazione");
            wtm.beginTrans(rispostaWs);

            switch (rispostaWs.getSeverity()) {
            case OK:
                // Nessun errore nei controlli
                ioaExt.setStatoSessione(Constants.StatoSessioneIngest.IN_ATTESA_FILE);
                ioaExt.setDtChiusura(null);
                break;
            case WARNING:
                ioaExt.setStatoSessione(Constants.StatoSessioneIngest.WARNING);
                ioaExt.setDtChiusura(null);
                break;
            case ERROR:
                ioaExt.setStatoSessione(Constants.StatoSessioneIngest.CHIUSO_ERR);
                ioaExt.setDtChiusura(Calendar.getInstance().getTime());
                break;
            }
            if (log.isDebugEnabled()) {
                log.debug("Sessione in creazione con Data apertura : {} Data chiusura : {}  Stato : {}",
                        String.valueOf(ioaExt.getDtApertura()), String.valueOf(ioaExt.getDtChiusura()),
                        ioaExt.getStatoSessione().name());
            }
            tmpRispostaControlli.reset();
            tmpRispostaControlli = salvataggioDati.creaSessione(ioaExt, rispostaWs.getErrorCode(),
                    rispostaWs.getErrorMessage());
            if (tmpRispostaControlli.getCodErr() != null) {
                prosegui = false;
                setRispostaWsError(rispostaWs, tmpRispostaControlli, SeverityEnum.ERROR, Constants.EsitoServizio.KO);
            }
            idSessione = tmpRispostaControlli.getrLong();
            tmpRispostaControlli.reset();
            if (StringUtils.isNotBlank(xml) && prosegui) {
                tmpRispostaControlli = salvataggioDati.creaXmlSessione(idSessione, xml);
                if (tmpRispostaControlli.getCodErr() != null) {
                    prosegui = false;
                    setRispostaWsError(rispostaWs, tmpRispostaControlli, SeverityEnum.ERROR,
                            Constants.EsitoServizio.KO);
                }
            }
            tmpRispostaControlli.reset();
            if (prosegui) {
                log.debug("Creazione stato sessione");
                tmpRispostaControlli = salvataggioDati.creaStatoSessione(idSessione, ioaExt.getStatoSessione().name(),
                        ioaExt.getDtApertura());
                if (tmpRispostaControlli.getCodErr() != null) {
                    prosegui = false;
                    setRispostaWsError(rispostaWs, tmpRispostaControlli, SeverityEnum.ERROR,
                            Constants.EsitoServizio.KO);
                }
            }
            Long idOggettoPadre = null;
            BigDecimal pgOggettoTrasf = null;
            BigDecimal niUnitaDocAttese = null;
            String dsObject = null;
            String cdVersGen = null;
            if (ioaExt.getInvioOggettoAsincronoInput() instanceof InvioOggettoAsincronoEstesoInput) {
                InvioOggettoAsincronoEstesoInput input = (InvioOggettoAsincronoEstesoInput) ioaExt
                        .getInvioOggettoAsincronoInput();
                idOggettoPadre = ioaExt.getIdOggettoPadre();
                pgOggettoTrasf = input.getPgObjectFiglio();
                niUnitaDocAttese = input.getNiUnitaDocAttese();
                dsObject = input.getDsObject();
                cdVersGen = ioaExt.getCdVersGen();

                if (idOggettoPadre != null && prosegui) {
                    tmpRispostaControlli.reset();
                    log.debug("Controllo oggetto padre");
                    tmpRispostaControlli = salvataggioDati.updateOggettoPadre(ioaExt.getIdOggettoPadre(),
                            ((InvioOggettoAsincronoEstesoInput) ioaExt.getInvioOggettoAsincronoInput())
                                    .getNiTotObjectFigli());
                    if (tmpRispostaControlli.getCodErr() != null) {
                        prosegui = false;
                        setRispostaWsError(rispostaWs, tmpRispostaControlli, SeverityEnum.ERROR,
                                Constants.EsitoServizio.KO);
                    }
                }
                // RAMO INSERITO PER LA MAC #14809 - WS invio oggetto: non viene calcolato il versatore per cui generare
                // oggetti
                // Anche nel caso di invio oggetto ridotto quando il CD_GEN viene calcolato deve essere aggiornato il
                // codice sull'Oggetto
            } else if (ioaExt.getInvioOggettoAsincronoInput() instanceof InvioOggettoAsincronoInput) {
                cdVersGen = ioaExt.getCdVersGen();
            }

            if (prosegui) {
                if (ioaExt.isFlRegistraObject()) {
                    if (idObject != null) {
                        /*
                         * Caso in cui l'oggetto ha stato CHIUSO_WARNING e viene nuovamente versato, oppure ha stato
                         * WARNING e viene nuovamente versato forzando, oppure ha stato CHIUSO_ERR_NOTIF o
                         * CHIUSO_ERR_SCHED o CHIUSO_ERR_VERS o CHIUSO_ERR_CRASH_DPI o CHIUSO_ERR_CRASH_FTP o
                         * CHIUSO_ERR_CRASH_FS_PRIM o CHIUSO_ERR_CRASH_FS_SECOND o ANNULLATO o CHIUSO_ERR_XFORMER o
                         * CHIUSO_ERR_VESAMENTO_A_PING e viene nuovamente versato
                         */

                        /*
                         * Modifica in InvioOggettoPreIngest per gestire come errore il caso in cui l'oggetto inviato
                         * esista già ed è IN_ATTESA_FILE (la modifica si rende necessaria perché non si spiega la
                         * ragione per cui non dovesse essere un errore l'esistennza di un oggetto con stato =
                         * IN_ATTESA_FILE e perché la sua gestione come un non errore provoca concorrenza sul file nel
                         * DPI fra il job che fa invio dell'oggetto e quello che fa la notifica dell'avvenuto
                         * trasferimento file
                         */
                        // verificaFtpDir = true;
                        log.debug("Oggetto già esistente con id {}", idObject);
                        // MEV#14652
                        PigObject oggetto = monitoraggioHelper.findById(PigObject.class, idObject);
                        String tipoVersamentoFile = oggetto.getPigTipoObject().getTiVersFile();
                        String statoOggetto = oggetto.getTiStatoObject();
                        // Punto 1 dell'analisi
                        if (tipoVersamentoFile.equals(Constants.TipoVersamento.DA_TRASFORMARE.name())
                                && statoOggetto.equals(Constants.StatoOggetto.ANNULLATO.name())) {
                            oggetto.setNiTotObjectTrasf(null);
                            monitoraggioHelper.bulkDeletePigObjectTrasf(idObject);
                            List<PigObject> oggettiFigli = monitoraggioHelper.getTuttiFigli(idObject);
                            if (oggettiFigli != null) {
                                for (PigObject figlio : oggettiFigli) {
                                    figlio.setPgOggettoTrasf(null);
                                    figlio.setPigObjectPadre(null);
                                }
                            }
                            // Punto 2 dell'analisi
                        } else if ((tipoVersamentoFile.equals(Constants.TipoVersamento.ZIP_CON_XML_SACER.name())
                                || tipoVersamentoFile.equals(Constants.TipoVersamento.ZIP_NO_XML_SACER.name())
                                || tipoVersamentoFile.equals(Constants.TipoVersamento.NO_ZIP.name()))
                                && statoOggetto.equals(Constants.StatoOggetto.ANNULLATO.name())) {
                            monitoraggioHelper.bulkDeletePigUnitaDocObject(idObject);
                        }
                        // ------------------
                        tmpRispostaControlli.reset();
                        // Punto 3
                        tmpRispostaControlli = controlliInvioOggettoAsincrono.verificaUltimaSessioneOggetto(idObject,
                                Constants.StatoOggetto.WARNING);
                        if (tmpRispostaControlli.isrBoolean()) {
                            /*
                             * se sessione identificata da id_last_sessione_ingest specificato dall'oggetto ha stato =
                             * WARNING, aggiorna tale sessione assegnando ti_stato = CHIUSO_FORZATA
                             */
                            log.debug(
                                    "Ultima sessione oggetto in WARNING - modifico lo stato della sessione precedente in CHIUSO_FORZATA");
                            Long idLastSessionObject = tmpRispostaControlli.getrLong();
                            tmpRispostaControlli.reset();
                            tmpRispostaControlli = salvataggioDati.modificaSessione(idLastSessionObject,
                                    Constants.StatoSessioneIngest.CHIUSO_FORZATA, null, null);
                            if (tmpRispostaControlli.getCodErr() != null) {
                                prosegui = false;
                                setRispostaWsError(rispostaWs, tmpRispostaControlli, SeverityEnum.ERROR,
                                        Constants.EsitoServizio.KO);
                            }
                            Date now = tmpRispostaControlli.getrDate();
                            log.debug("Creazione stato sessione");
                            tmpRispostaControlli.reset();
                            tmpRispostaControlli = salvataggioDati.creaStatoSessione(idLastSessionObject,
                                    Constants.StatoSessioneIngest.CHIUSO_FORZATA.name(), now);
                            if (tmpRispostaControlli.getCodErr() != null) {
                                prosegui = false;
                                setRispostaWsError(rispostaWs, tmpRispostaControlli, SeverityEnum.ERROR,
                                        Constants.EsitoServizio.KO);
                            }
                        }
                        // Credo punto 4
                        if (prosegui) {
                            log.debug("Modifico lo stato oggetto in {} e gli assegno la sessione creata",
                                    ioaExt.getStatoSessione());
                            tmpRispostaControlli.reset();
                            tmpRispostaControlli = salvataggioDati.modificaOggetto(idObject, ioaExt.getStatoSessione(),
                                    idSessione, userName, idOggettoPadre, pgOggettoTrasf, niUnitaDocAttese, dsObject,
                                    cdVersGen, ioaExt.getTiGestOggettiFigli());
                            if (tmpRispostaControlli.getCodErr() != null) {
                                prosegui = false;
                                setRispostaWsError(rispostaWs, tmpRispostaControlli, SeverityEnum.ERROR,
                                        Constants.EsitoServizio.KO);
                            }
                        }
                        // Punto 5
                        if (StringUtils.isNotBlank(xml) && ioaExt.isFlRegistraXMLObject() && prosegui) {
                            log.debug("Modifico lo xml object");
                            tmpRispostaControlli.reset();
                            tmpRispostaControlli = salvataggioDati.modificaXmlObject(idObject, xml, cdVersioneXml);
                            if (tmpRispostaControlli.getCodErr() != null) {
                                prosegui = false;
                                setRispostaWsError(rispostaWs, tmpRispostaControlli, SeverityEnum.ERROR,
                                        Constants.EsitoServizio.KO);
                            }
                        }
                        // Punto 6
                        if (ioaExt.getInvioOggettoAsincronoInput().getNmTipoObject().equalsIgnoreCase(
                                Constants.STUDIO_DICOM) && ioaExt.isFlRegistraDatiSpecDicom() && prosegui) {
                            log.debug("Salvataggio dati DICOM");
                            tmpRispostaControlli.reset();
                            tmpRispostaControlli = salvataggioDati.salvaInfoDicom(idObject,
                                    ioaExt.getDcmDatiSpecifici(), ioaExt.getIdXsdDatiSpec());
                            if (tmpRispostaControlli.getCodErr() != null) {
                                prosegui = false;
                                setRispostaWsError(rispostaWs, tmpRispostaControlli, SeverityEnum.ERROR,
                                        Constants.EsitoServizio.KO);
                            }
                        }
                        if (ioaExt.getTiVersFile().equals(Constants.TipoVersamento.DA_TRASFORMARE.name())) {
                            tmpRispostaControlli.reset();
                            /*
                             * se l'oggetto versato ha tipo che prevede tipo versamento = DA_TRASFORMARE e per gli
                             * oggetti figli non esiste una unità doc con stato = VERSATA_OK o VERSATA_TIMEOUT
                             */
                            /*
                             * MEV#14652 - Eliminazione punto 7 dell'analisi tmpRispostaControlli =
                             * checker.checkOggettiFigli(idObject); if (!tmpRispostaControlli.isrBoolean()) { // Se non
                             * esistono, pulisco l'oggetto DA_TRASFORMARE salvataggioDati.cleanOggettoPadre(idObject); }
                             */
                        }
                    } else {
                        tmpRispostaControlli = salvataggioDati.creaOggetto(ioaExt, idSessione, userName);
                        if (tmpRispostaControlli.getCodErr() != null) {
                            prosegui = false;
                            setRispostaWsError(rispostaWs, tmpRispostaControlli, SeverityEnum.ERROR,
                                    Constants.EsitoServizio.KO);
                        }
                        idObject = tmpRispostaControlli.getrLong();
                        log.debug("Creazione nuovo oggetto PigObject - id = {}", idObject);
                        if (StringUtils.isNotBlank(xml) && ioaExt.isFlRegistraXMLObject() && prosegui) {
                            tmpRispostaControlli.reset();
                            log.debug("Creazione xml object");
                            tmpRispostaControlli = salvataggioDati.creaXmlObject(idObject, xml, cdVersioneXml);
                            if (tmpRispostaControlli.getCodErr() != null) {
                                prosegui = false;
                                setRispostaWsError(rispostaWs, tmpRispostaControlli, SeverityEnum.ERROR,
                                        Constants.EsitoServizio.KO);
                            }
                        }
                        if (nmTipoObject.equalsIgnoreCase(Constants.STUDIO_DICOM) && ioaExt.isFlRegistraDatiSpecDicom()
                                && prosegui) {
                            tmpRispostaControlli.reset();
                            log.debug("Salvataggio dati DICOM");
                            tmpRispostaControlli = salvataggioDati.salvaInfoDicom(idObject,
                                    ioaExt.getDcmDatiSpecifici(), ioaExt.getIdXsdDatiSpec());
                            if (tmpRispostaControlli.getCodErr() != null) {
                                prosegui = false;
                                setRispostaWsError(rispostaWs, tmpRispostaControlli, SeverityEnum.ERROR,
                                        Constants.EsitoServizio.KO);
                            }
                        }
                        log.debug(
                                "Aggiorno tutte le sessioni errate precedenti alla creazione dell'oggetto, assegnandogli l'oggetto creato");
                        salvataggioDati.updateSessioniIngestErrate(
                                ioaExt.getInvioOggettoAsincronoInput().getNmVersatore(),
                                ioaExt.getInvioOggettoAsincronoInput().getCdKeyObject(), idObject);
                    }
                    if (idObject != null && prosegui) {
                        tmpRispostaControlli.reset();
                        log.debug("Assegno alla sessione l'oggetto PigObject");
                        // Punto IV
                        tmpRispostaControlli = salvataggioDati.modificaSessione(idSessione, idObject);
                        if (tmpRispostaControlli.getCodErr() != null) {
                            prosegui = false;
                            setRispostaWsError(rispostaWs, tmpRispostaControlli, SeverityEnum.ERROR,
                                    Constants.EsitoServizio.KO);
                        }
                    }
                }
            }
            // Fine transazione oggetto
            log.debug("Fine transazione - COMMIT");
            if (prosegui && rispostaWs.getErrorType() != ErrorTypeEnum.DB_FATAL) {
                wtm.commit(rispostaWs);
            }
        }
    }

    private void setRispostaWsError(RispostaWSInvioOggettoAsincrono rispostaWs, RispostaControlli rispostaControlli,
            SeverityEnum sev, Constants.EsitoServizio esito) {
        /*
         * 
         * rispostaWs.getSeverity().equals(SeverityEnum.WARNING)
         * 
         * switch (rispostaWs.getSeverity()) { case OK: // Nessun errore nei controlli
         * ioaExt.setStatoSessione(Constants.StatoSessioneIngest.IN_ATTESA_FILE); ioaExt.setDtChiusura(null); break;
         * case WARNING: ioaExt.setStatoSessione(Constants.StatoSessioneIngest.WARNING); ioaExt.setDtChiusura(null);
         * break;
         */

        rispostaWs.setSeverity(sev);
        rispostaWs.setErrorCode(rispostaControlli.getCodErr());
        rispostaWs.setErrorMessage(rispostaControlli.getDsErr());
        rispostaWs.getInvioOggettoAsincronoRisposta().setCdEsito(esito);
        rispostaWs.getInvioOggettoAsincronoRisposta().setCdErr(rispostaControlli.getCodErr());
        rispostaWs.getInvioOggettoAsincronoRisposta().setDsErr(rispostaControlli.getDsErr());
        log.debug("Errore Invio : {} - {}", rispostaControlli.getCodErr(), rispostaControlli.getDsErr());
        log.debug("Fine transazione - ROLLBACK");
        wtm.rollback(rispostaWs);
    }

}
