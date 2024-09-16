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

package it.eng.sacerasi.web.action;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.ejb.EJB;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;

import it.eng.parer.objectstorage.exceptions.ObjectStorageException;
import it.eng.sacerasi.common.Constants;
import it.eng.sacerasi.common.Constants.TipoVersatore;
import it.eng.sacerasi.entity.PigErrore;
import it.eng.sacerasi.entity.PigSisma;
import it.eng.sacerasi.entity.PigSismaDocumenti;
import it.eng.sacerasi.entity.PigSismaProgettiAg;
import it.eng.sacerasi.messages.MessaggiHelper;
import it.eng.sacerasi.sisma.dto.DatiAnagraficiDto;
import it.eng.sacerasi.sisma.dto.DocUploadDto;
import it.eng.sacerasi.sisma.dto.EsitoSalvataggioSisma;
import it.eng.sacerasi.sisma.dto.NavigazioneSismaDto;
import it.eng.sacerasi.sisma.dto.RicercaSismaDTO;
import it.eng.sacerasi.sisma.dto.SismaDto;
import it.eng.sacerasi.sisma.ejb.SismaEjb;
import it.eng.sacerasi.sisma.ejb.SismaHelper;
import it.eng.sacerasi.sisma.ejb.VerificaDocumentiSismaEjb;
import it.eng.sacerasi.slite.gen.Application;
import it.eng.sacerasi.slite.gen.action.SismaAbstractAction;
import it.eng.sacerasi.util.DateUtil;
import it.eng.sacerasi.web.helper.ConfigurationHelper;
import it.eng.sacerasi.web.util.ComboGetter;
import it.eng.sacerasi.web.util.WebConstants;
import it.eng.spagoCore.error.EMFError;
import it.eng.spagoLite.actions.form.ListAction;
import it.eng.spagoLite.db.base.BaseRowInterface;
import it.eng.spagoLite.db.base.BaseTableInterface;
import it.eng.spagoLite.db.base.sorting.SortingRule;
import it.eng.spagoLite.db.base.table.BaseTable;
import it.eng.spagoLite.db.decodemap.DecodeMapIF;
import it.eng.spagoLite.db.oracle.decode.DecodeMap;
import it.eng.spagoLite.form.base.BaseElements.Status;
import it.eng.spagoLite.form.fields.Field;
import it.eng.spagoLite.form.fields.Fields;
import it.eng.spagoLite.message.Message;
import it.eng.spagoLite.message.Message.MessageLevel;
import it.eng.spagoLite.message.MessageBox;
import it.eng.spagoLite.security.Secure;
import it.eng.spagoLite.security.SuppressLogging;

/**
 *
 * @author MIacolucci
 */
public class SismaAction extends SismaAbstractAction {

    private static final String CONTENT_DISPOSITION = "Content-Disposition";
    private static final String CAMPO_ARRAY = "array";
    private static final String CAMPO_VERIFICA_ATTIVATA = "verificaAttivata";
    private static final String CAMPO_NASCONDI_UPDATE = "nascondiUpdate";
    private static final String ID_SISMA = "id_sisma";
    private static final Logger log = LoggerFactory.getLogger(SismaAction.class);
    private static final String PING_ERRSISMA_20 = "PING-ERRSISMA20";

    @EJB(mappedName = "java:app/SacerAsync-ejb/SismaEjb")
    private SismaEjb sismaEjb;
    @EJB(mappedName = "java:app/SacerAsync-ejb/SismaHelper")
    private SismaHelper sismaHelper;
    @EJB(mappedName = "java:app/SacerAsync-ejb/ConfigurationHelper")
    private ConfigurationHelper configurationHelper;
    @EJB(mappedName = "java:app/SacerAsync-ejb/MessaggiHelper")
    private MessaggiHelper messaggiHelper;
    @EJB(mappedName = "java:app/SacerAsync-ejb/VerificaDocumentiSismaEjb")
    private VerificaDocumentiSismaEjb verificaDocumentiSismaEjb;

    @Override
    public void initOnClick() throws EMFError {
        //
    }

    @Override
    public void insertDettaglio() throws EMFError {
        //
    }

    @Override
    public void update(Fields<Field> fields) throws EMFError {
        getForm().getInserimentoWizard().reset();
    }

    @Override
    public void delete(Fields<Field> fields) throws EMFError {
        //
    }

    private boolean isUtenteAgenzia() {
        boolean flag = false;
        Constants.TipoVersatore tipo = (Constants.TipoVersatore) getSession()
                .getAttribute(Constants.TIPO_VERSATORE_SISMA_UTENTE);
        if (tipo.equals(TipoVersatore.AGENZIA)) {
            flag = true;
        }
        return flag;
    }

    @Override
    public void loadDettaglio() throws EMFError {
        if (getTableName().equals(getForm().getSismaList().getName())) {
            BigDecimal id = getForm().getSismaList().getTable().getCurrentRow().getBigDecimal(ID_SISMA);
            BigDecimal idVersatore = getForm().getSismaList().getTable().getCurrentRow().getBigDecimal("id_versatore");
            if (getNavigationEvent().equals(ListAction.NE_DETTAGLIO_UPDATE)) {
                // ma qui, quando ci passa?
                inizializzaWizard(idVersatore, id);
                SismaDto dto = loadDettaglioSisma(id);
                // Popola i finanziamanti relativi al versatore dell'utente loggato!
                getForm().getDatiGeneraliInput().getId_sisma_finanziamento()
                        .setDecodeMap(DecodeMap.Factory.newInstance(
                                sismaEjb.findPigSismaFinanziamentoByIdVersTB(dto.getIdVers()), "id_sisma_finanziamento",
                                "ds_tipo_finanziamento"));
                determinaStato(true);

            } else if (getNavigationEvent().equals(ListAction.NE_DETTAGLIO_VIEW)
                    || getNavigationEvent().equals(ListAction.NE_NEXT)
                    || getNavigationEvent().equals(ListAction.NE_PREV)) {
                inizializzaWizard(idVersatore, id);
                SismaDto dto = loadDettaglioSisma(id);
                // Popola i finanziamanti relativi al progetto caricato
                popolaLineaDiFinanziamento(dto.getIdVers());
                determinaStato(false);

            } else if (getNavigationEvent().equals(ListAction.NE_DETTAGLIO_INSERT)) {
                inizializzaWizard(idVersatore, null); // mi sa che da qui non ci passa mai!!!!!
            }

        }

    }

    private SismaDto loadDettaglioSisma(BigDecimal idSisma) throws EMFError {
        // SETTAGGIO DELLA PARTE DI DATI GENERALI DI OUTPUT di tutte le form di Sisma
        SismaDto dto = sismaEjb.getSismaById(idSisma);
        String stato = dto.getTiStato();
        getForm().getDatiGeneraliOutput().getDs_tipo_finanziamento_out().setValue(dto.getDsTipoFinanziamento());
        getForm().getDatiGeneraliOutput().getCodice_intervento_out().setValue(dto.getCodiceIntervento());
        getForm().getDatiGeneraliOutput().getDenominazione_intervento_out().setValue(dto.getDenominazioneIntervento());
        getForm().getDatiGeneraliOutput().getId_sisma_out().setValue(dto.getIdSisma() + "");
        getForm().getDatiGeneraliOutput().getDt_creazione_out()
                .setValue(DateUtil.formatDateWithSlash(dto.getDtCreazione()));
        getForm().getDatiGeneraliOutput().getCd_key_out().setValue(dto.getCdKey());
        getForm().getDatiGeneraliOutput().getTi_stato_out().setValue(stato);
        getForm().getDatiGeneraliOutput().getOggetto_out().setValue(dto.getOggetto());
        if (dto.getDtStato() != null) {
            getForm().getDatiGeneraliOutput().getDt_stato_out()
                    .setValue(DateUtil.formatDateWithSlashAndTime(dto.getDtStato()));
        }
        getForm().getDatiGeneraliOutput().getDs_fase_sisma_out().setValue(dto.getDsFaseSisma());
        getForm().getDatiGeneraliOutput().getDs_stato_progetto_out().setValue(dto.getDsStatoProgetto());
        getForm().getDatiGeneraliInput().getId_sisma_finanziamento()
                .setValue("" + dto.getIdTipoFinanziamento().longValueExact());
        getForm().getDatiGeneraliInput().getId_sisma_progetti_ag().setDecodeMap(DecodeMap.Factory.newInstance(
                sismaEjb.findPigSismaProgettiAgByIdEnteFinanziamentoTB(dto.getIdVers(), dto.getIdTipoFinanziamento()),
                "id_sisma_progetti_ag", "codice_intervento"));
        getForm().getDatiGeneraliInput().getId_sisma_progetti_ag()
                .setValue("" + dto.getIdSismaProgettiAg().longValueExact());
        getForm().getDatiGeneraliInput().getId_sisma_fase_progetto().setDecodeMap(
                DecodeMap.Factory.newInstance(sismaEjb.findPigSismaFaseByFinTB(dto.getIdTipoFinanziamento()),
                        "id_sisma_fase_progetto", "ds_fase_sisma"));
        getForm().getDatiGeneraliInput().getId_sisma_fase_progetto().setValue("" + dto.getIdSismaFaseProgetto());

        // MAC 29470 - popola la combo con lo stato progetto in base alla fase del progetto selezionata.
        DecodeMap mappa = DecodeMap.Factory.newInstance(
                sismaEjb.findPigSismaStatoProgettoByIdSismaFaseProgettoTB(dto.getIdSismaFaseProgetto()),
                "id_sisma_stato_progetto", "ds_stato_progetto");
        getForm().getDatiGeneraliInput().getId_sisma_stato_progetto().setDecodeMap(mappa);

        getForm().getDatiGeneraliInput().getId_sisma_stato_progetto()
                .setValue("" + dto.getIdSismaStatoProgetto().longValueExact());
        getForm().getDatiGeneraliInput().getId_sisma_val_atto().setDecodeMap(
                DecodeMap.Factory.newInstance(sismaEjb.findPigSismaValAtto(), "id_sisma_val_atto", "nm_tipo_atto"));
        getForm().getDatiGeneraliInput().getId_sisma_val_atto().setValue(dto.getIdSismaValAtto().longValueExact() + "");
        getForm().getDatiGeneraliInput().getNumero().setValue(dto.getNumero());
        getForm().getDatiGeneraliInput().getAnno().setValue(dto.getAnno().longValueExact() + "");
        getForm().getDatiGeneraliInput().getData().setValue(DateUtil.formatDateWithSlash(dto.getData()));
        getForm().getDatiGeneraliInput().getFl_intervento_soggetto_a_tutela()
                .setValue(dto.isFlInterventoSoggettoATutela() ? Constants.DB_TRUE : Constants.DB_FALSE);
        getForm().getDatiProfiloArchivistico().getClassifica().setValue(dto.getClassifica());
        getForm().getDatiProfiloArchivistico().getId_fascicolo().setValue(dto.getIdFascicolo());
        getForm().getDatiProfiloArchivistico().getId_sottofascicolo().setValue(dto.getIdSottofascicolo());
        getForm().getDatiProfiloArchivistico().getOggetto_fascicolo().setValue(dto.getOggettoFascicolo());
        getForm().getDatiProfiloArchivistico().getOggetto_sottofascicolo().setValue(dto.getOggettoSottofascicolo());
        getForm().getDatiAgenzia().getAnno_ag()
                .setValue((dto.getAnnoAg() == null) ? "" : dto.getAnnoAg().longValueExact() + "");
        getForm().getDatiAgenzia().getNumero_ag().setValue(dto.getNumeroAg());
        getForm().getDatiAgenzia().getRegistro_ag().setValue(dto.getRegistroAg());
        getForm().getDatiAgenzia().getData_ag().setValue(DateUtil.formatDateWithSlash(dto.getDataAg()));
        getForm().getDatiAgenzia().getClassifica_ag().setValue(dto.getClassificaAg());
        getForm().getDatiAgenzia().getId_fascicolo_ag().setValue(dto.getIdFascicoloAg());
        getForm().getDatiAgenzia().getId_sottofascicolo_ag().setValue(dto.getIdSottofascicoloAg());
        getForm().getDatiAgenzia().getOggetto_fascicolo_ag().setValue(dto.getOggettoFascicoloAg());
        getForm().getDatiAgenzia().getOggetto_sottofascicolo_ag().setValue(dto.getOggettoSottofascicoloAg());

        // Se si è in dettaglio mette in sola lettura la data
        if (!getNavigationEvent().equals(ListAction.NE_DETTAGLIO_UPDATE)) {
            getForm().getDatiGeneraliInput().getData().setReadonly(true);
            getForm().getDatiGeneraliInput().getData().setRequired(false);
            getForm().getDatiGeneraliInput().getData().setDescription("Data atto:");
        } else {
            getForm().getDatiGeneraliInput().getData().setReadonly(false);
            getForm().getDatiGeneraliInput().getData().setRequired(true);
        }
        getForm().getDatiGeneraliInput().getOggetto().setValue(dto.getOggetto());
        getForm().getDatiGeneraliInput().getDs_descrizione().setValue(dto.getDsDescrizione());
        if (stato.equals("ERRORE")) {
            stato += " - " + dto.getDsErr();
        }
        getForm().getDatiGeneraliOutput().getTi_stato_out().setValue(stato);
        // MEV29331
        getForm().getDatiGeneraliOutput().getTi_atto_out().setValue(dto.getNmTipoAtto());
        getForm().getDatiGeneraliOutput().getAnno_out().setValue(dto.getAnno().longValueExact() + "");
        getForm().getDatiGeneraliOutput().getNumero_out().setValue(dto.getNumero());

        /*
         * DETERMINA E METTE IN SESSIONE la tipologia di versatore associato al SISMA oppure lo annulla.
         */
        getSession().setAttribute(Constants.TIPO_VERSATORE_SISMA_SELEZIONATO,
                sismaEjb.getTipoVersatore(dto.getIdVers()));

        // FINE SETTAGGIO DATI DELLA PARTE DATI GENERALI OUTPUT E INPUT sia per Dettaglio che per MODIFICA
        // In caso di click sulla lente del dettaglio deve anche caricare i dati dinamici uguali all'ultima
        // pagina del wizard
        if (getNavigationEvent().equals(ListAction.NE_DETTAGLIO_VIEW) || getNavigationEvent().equals(ListAction.NE_NEXT)
                || getNavigationEvent().equals(ListAction.NE_PREV)) {
            inserimentoWizardRiepilogoOnEnter();
        } else {
            //
        }
        return dto;
    }

    @Override
    public void undoDettaglio() throws EMFError {
        //
    }

    @Override
    public void saveDettaglio() throws EMFError {
        String stato = getForm().getDatiGeneraliOutput().getTi_stato_out().parse();
        BigDecimal idSisma = getForm().getSismaList().getTable().getCurrentRow().getBigDecimal(ID_SISMA);
        getForm().getSismaList().getTable().getCurrentRow().getBigDecimal("id_versatore");
        if (stato.equals(PigSisma.TiStato.VERSATO.name())) {
            getForm().getDatiAgenzia().postAndValidate(getRequest(), getMessageBox());
            getForm().getDatiProfiloArchivistico().post(getRequest());
            getForm().getDocumentiCaricatiList().post(getRequest());

            if (getMessageBox().isEmpty()) {
                if (!sismaEjb.controllaUnivocitaDatiAgenzia(idSisma,
                        getUser().getIdOrganizzazioneFoglia().longValueExact(),
                        getForm().getDatiAgenzia().getRegistro_ag().parse(),
                        new BigDecimal(getForm().getDatiAgenzia().getAnno_ag().parse()),
                        getForm().getDatiAgenzia().getNumero_ag().parse())) {
                    getMessageBox().addError(String.format(
                            messaggiHelper.retrievePigErrore(PING_ERRSISMA_20).getDsErrore().replace("\\{0\\}", "%s"),
                            getForm().getDatiAgenzia().getRegistro_ag().parse() + "-"
                                    + getForm().getDatiAgenzia().getAnno_ag().parse() + "-"
                                    + getForm().getDatiAgenzia().getNumero_ag().parse()));
                }
            }

            if (getMessageBox().isEmpty()) {
                salvaDatiAgenzia(idSisma.longValueExact());

                if (isDatiAgenziaComplete()) {
                    getForm().getDettaglioButtonList().getVersaInAgenzia().setEditMode();
                }
            }

        } else if (stato.equals(PigSisma.TiStato.DA_VERIFICARE.name())
                || stato.equals(PigSisma.TiStato.VERIFICATO.name())) {
            SismaDto sismaDto = new SismaDto();

            getForm().getDocumentiCaricatiList().post(getRequest());

            // MEV 30691 - Se devo andare in stato DA_RIVEDERE mostro un pop-up.
            int numFlagValorizzati = 0;
            int numFlagValorizzatiADaRivedere = 0;

            for (Iterator<? extends BaseRowInterface> iterator = getForm().getDocumentiCaricatiList().getTable()
                    .iterator(); iterator.hasNext();) {
                BaseRowInterface riga = iterator.next();
                String valore = riga.getString("ti_verifica_agenzia");

                if (!valore.isEmpty()) {
                    numFlagValorizzati++;

                    if (valore.equals(Constants.DB_FALSE)) {
                        numFlagValorizzatiADaRivedere++;
                    }
                }
            }

            String popUpDaRivedereMostrato = (String) getRequest().getAttribute("popUpDaRivedereMostrato");
            if (numFlagValorizzati == getForm().getDocumentiCaricatiList().getTable().size()
                    && numFlagValorizzatiADaRivedere > 0 && popUpDaRivedereMostrato == null) {
                // mostrerò un pop-up, se non già mostrato
                getRequest().setAttribute("popUpDaRivedere", Constants.DB_TRUE);
                forwardToPublisher(Application.Publisher.DETTAGLIO_SISMA);
                return;
            }

            // MAC#24999 - correzione errore in fase di salvataggio dei dati dell'agenzia di un progetto
            if (stato.equals(PigSisma.TiStato.VERIFICATO.name())) {
                // MAC 29844 - se non tutti i documenti sono verificati ignora il cotrollo qui sotto
                boolean tuttiDocumentiInVerificaOk = true;

                for (Iterator<? extends BaseRowInterface> iterator = getForm().getDocumentiCaricatiList().getTable()
                        .iterator(); iterator.hasNext();) {
                    BaseRowInterface riga = iterator.next();
                    String valore = riga.getString("ti_verifica_agenzia");
                    if (valore.equals(Constants.DB_FALSE)) {
                        tuttiDocumentiInVerificaOk = false;
                        break;
                    }
                }

                if (tuttiDocumentiInVerificaOk
                        && getForm().getDatiGeneraliOutput().getNatura_soggetto_attuatore_out().getValue()
                                .equals("PRIVATO")
                        && getForm().getDatiAgenzia().postAndValidate(getRequest(), getMessageBox())) {
                    if (getMessageBox().isEmpty()) {

                        if (!sismaEjb.controllaUnivocitaDatiAgenzia(idSisma,
                                getUser().getIdOrganizzazioneFoglia().longValueExact(),
                                getForm().getDatiAgenzia().getRegistro_ag().parse(),
                                new BigDecimal(getForm().getDatiAgenzia().getAnno_ag().parse()),
                                getForm().getDatiAgenzia().getNumero_ag().parse())) {
                            getMessageBox().addError(String.format(
                                    messaggiHelper.retrievePigErrore(PING_ERRSISMA_20).getDsErrore().replace("\\{0\\}",
                                            "%s"),
                                    getForm().getDatiAgenzia().getRegistro_ag().parse() + "-"
                                            + getForm().getDatiAgenzia().getAnno_ag().parse() + "-"
                                            + getForm().getDatiAgenzia().getNumero_ag().parse()));
                        }
                    }

                    if (getMessageBox().isEmpty()) {
                        sismaDto = salvaDatiAgenzia(idSisma.longValueExact());
                    }
                }
            }

            if (getMessageBox().isEmpty()) {
                saveDettaglio1(sismaDto, idSisma);
            }
        }

        forwardToPublisher(Application.Publisher.DETTAGLIO_SISMA);
    }

    // MEV 30691
    public void confermaStatoDaRivedere() throws EMFError {
        getRequest().setAttribute("popUpDaRivedereMostrato", Constants.DB_TRUE);
        saveDettaglio();
        forwardToPublisher(Application.Publisher.DETTAGLIO_SISMA);
    }

    private void saveDettaglio1(SismaDto sismaDto, BigDecimal idSisma) throws EMFError {
        getForm().getDocumentiCaricatiList().post(getRequest());
        HashMap<BigDecimal, String> mappa = new HashMap<>();
        for (Iterator<? extends BaseRowInterface> iterator = getForm().getDocumentiCaricatiList().getTable()
                .iterator(); iterator.hasNext();) {
            BaseRowInterface riga = iterator.next();
            BigDecimal idRiga = riga.getBigDecimal("id_sisma_documenti");
            String valore = riga.getString("ti_verifica_agenzia");
            mappa.put(idRiga, valore);
        }
        PigSisma.TiStato nuovoStato = sismaEjb.salvaSismaAgenzia(idSisma, mappa, sismaDto);
        if (getForm().getDatiGeneraliOutput().getTi_stato_out().getValue().equals(nuovoStato.name())) {
            // non fa nulla
        } else {
            // Se lo stato è cambiato dopo il salvataggio ricarica e ricalcola tutto
            getMessageBox().addInfo("Il progetto ha cambiato stato in " + nuovoStato.name());
            getForm().getDatiGeneraliOutput().getTi_stato_out().setValue(nuovoStato.name());
            getMessageBox().setViewMode(MessageBox.ViewMode.plain);
            if (nuovoStato.equals(PigSisma.TiStato.DA_RIVEDERE)) {
                getMessageBox().addMessage(new Message(MessageLevel.INF,
                        "Per completare l’iter è necessario predisporre la PEC da inoltrare al soggetto attuatore, comunicando l’avvenuto rigetto degli elaborati caricati, le motivazioni e le operazioni da compiere per perfezionare il caricamento della documentazione corretta."));
            }
        }

        determinaStato(false);
    }

    /*
     * Richiamato sia dal saveDettaglio() che dal pulsante versa in agenzia
     */
    private SismaDto salvaDatiAgenzia(long idSisma) throws EMFError {
        SismaDto dto = new SismaDto();
        dto.setIdSisma(idSisma);
        dto.setDataAg(getForm().getDatiAgenzia().getData_ag().parse());
        if (getForm().getDatiAgenzia().getAnno_ag().parse() != null) {
            dto.setAnnoAg(new BigDecimal(getForm().getDatiAgenzia().getAnno_ag().parse()));
        }
        dto.setNumeroAg(getForm().getDatiAgenzia().getNumero_ag().getValue());
        dto.setRegistroAg(getForm().getDatiAgenzia().getRegistro_ag().getValue());
        dto.setClassificaAg(getForm().getDatiAgenzia().getClassifica_ag().parse());
        dto.setIdFascicoloAg(getForm().getDatiAgenzia().getId_fascicolo_ag().parse());
        dto.setIdSottofascicoloAg(getForm().getDatiAgenzia().getId_sottofascicolo_ag().parse());
        dto.setOggettoFascicoloAg(getForm().getDatiAgenzia().getOggetto_fascicolo_ag().parse());
        dto.setOggettoSottofascicoloAg(getForm().getDatiAgenzia().getOggetto_sottofascicolo_ag().parse());
        sismaEjb.salvaSismaAgenzia(dto);
        return dto;
    }

    @Override
    public void dettaglioOnClick() throws EMFError {
        String lista = getTableName();
        String action = getRequest().getParameter("navigationEvent");

        if (action != null && getForm().getSismaList().getName().equals(lista)) {
            if (action.equals(NE_DETTAGLIO_INSERT)) {
                getForm().getDatiGeneraliInput().setStatus(Status.insert);
                forwardToPublisher(Application.Publisher.SISMA_WIZARD);
            } else if (action.equals(NE_DETTAGLIO_VIEW)) {
                forwardToPublisher(Application.Publisher.DETTAGLIO_SISMA);
            } else {
                getForm().getDatiGeneraliInput().setStatus(Status.update);
                forwardToPublisher(Application.Publisher.SISMA_WIZARD);
            }
        } else if (getNavigationEvent().equals(ListAction.NE_DETTAGLIO_UPDATE)) {
            getForm().getDatiGeneraliInput().setStatus(Status.update);
            determinaStato(true);
            forwardToPublisher(Application.Publisher.SISMA_WIZARD);
        }
    }

    @Override
    public void elencoOnClick() throws EMFError {
        goBack();
    }

    @Override
    protected String getDefaultPublsherName() {
        return Application.Publisher.SISMA;
    }

    @Override
    public void reloadAfterGoBack(String publisherName) {
        if (publisherName.equals(Application.Publisher.SISMA)) {
            int paginaCorrente = getForm().getSismaList().getTable().getCurrentPageIndex();
            int inizio = getForm().getSismaList().getTable().getFirstRowPageIndex();
            try {
                popolaListaSisma();
                // Rieseguo la query se necessario
                this.lazyLoadGoPage(getForm().getSismaList(), paginaCorrente);
                // Ritorno alla pagina
                getForm().getSismaList().getTable().setCurrentRowIndex(inizio);
            } catch (EMFError ex) {
                log.error(ex.getDescription(), ex);
            }
        }
    }

    @Override
    public String getControllerName() {
        return Application.Actions.SISMA;
    }

    @Secure(action = "Menu.Sisma.VersamentiSisma")
    public void loadSisma() throws EMFError {
        getUser().getMenu().reset();
        getUser().getMenu().select("Menu.Sisma.VersamentiSisma");
        /*
         * MEV #24582 - Aggiunta proprietà di sistema per gestire abilitazione/disabilitazione di ObjectStorage e
         * funzionalità collegate Controlla che l'object storage sia abilitato nell'ambiente
         */
        final String isActiveSystemValue = System.getProperty(Constants.OBJECT_STORAGE_ENABLED);
        boolean isActiveFlag = true;
        if (isActiveSystemValue != null) {
            isActiveFlag = !isActiveSystemValue.toLowerCase().trim().equals(Constants.OBJECT_STORAGE_DISATTIVO);
        }
        if (isActiveFlag) {
            /*
             * DETERMINA E METTE IN SESSIONE la tipologia di versatore dell'Utente oppure lo annulla.
             */
            Enum<Constants.TipoVersatore> tipo = sismaEjb.getTipoVersatore(getVersatoreDellUtenteLoggato());
            if (tipo == null) {
                // L'utente non può operare con sisma!
                getMessageBox().addMessage(new Message(MessageLevel.INF,
                        "Versatore non autorizzato ad utilizzare le funzioni Progetti ricostruzione."));
            } else {
                getSession().setAttribute(Constants.TIPO_VERSATORE_SISMA_UTENTE,
                        sismaEjb.getTipoVersatore(getVersatoreDellUtenteLoggato()));

                // MEV26165 - attivo i campi di ricerca
                getForm().getFiltriSisma().setEditMode();
                popolaFiltriRicercaSisma();

                popolaListaSisma();

                // Popola i finanziamanti
                getForm().getDatiGeneraliInput().setEditMode();
                getForm().getDatiGeneraliInput().getSalvaBozza().setEditMode();
                DateTime oggi = new DateTime();
                DecodeMapIF dm = ComboGetter.getRangeAnniReversed(2010, oggi.getYear());
                getForm().getDatiGeneraliInput().getAnno().setDecodeMap(dm);
                getForm().getDatiGeneraliInput().getFl_intervento_soggetto_a_tutela()
                        .setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());
                getForm().getDatiAgenzia().getAnno_ag().setDecodeMap(dm);
                getForm().getDatiAgenzia().getRegistro_ag().setDecodeMap(ComboGetter.getValoriRegistroAg());
            }
        } else {
            // L'utente non può operare con sisma!
            getMessageBox().addMessage(new Message(MessageLevel.INF,
                    "La funzione Progetti ricostruzione non può essere utilizzata per assenza dell'ObjectStorage nell'ambiente."));
        }
        forwardToPublisher(Application.Publisher.SISMA);
    }

    private void popolaFiltriRicercaSisma() {
        DecodeMap finanziamentiDecodeMap = sismaHelper.getLineaFinanziamentiDecodeMap();
        getForm().getFiltriSisma().getId_linea_fin().setDecodeMap(finanziamentiDecodeMap);

        DecodeMap codiceInterventoDecodeMap = sismaHelper.getCodiceIntervento();
        getForm().getFiltriSisma().getNm_intervento().setDecodeMap(codiceInterventoDecodeMap);

        DecodeMap sismaFaseProgettoDecodeMap = sismaHelper.getSismaFaseProgetto();
        getForm().getFiltriSisma().getNm_fase_prog().setDecodeMap(sismaFaseProgettoDecodeMap);

        DecodeMap soggettiAttuatoreDecodeMap = sismaHelper.getSoggettiAttuatoreDecodeMap();
        getForm().getFiltriSisma().getId_soggetto_att().setDecodeMap(soggettiAttuatoreDecodeMap);

        DecodeMap statiProgettoDecodeMap = sismaHelper.getSismaStatoProgetto();
        getForm().getFiltriSisma().getNm_stato_prog().setDecodeMap(statiProgettoDecodeMap);

        DecodeMap statiDecodeMap = sismaHelper.getSismaStato(isUtenteAgenzia());
        getForm().getFiltriSisma().getNm_stato().setDecodeMap(statiDecodeMap);

        DecodeMap anniDecodeMap = sismaHelper.getSismaAnno();
        getForm().getFiltriSisma().getAnno().setDecodeMap(anniDecodeMap);

        if (isUtenteAgenzia()) {
            getForm().getFiltriSisma().getCd_registro_agenzia().setReadonly(false);
            getForm().getFiltriSisma().getAnno_agenzia().setReadonly(false);
            getForm().getFiltriSisma().getCd_num_agenzia().setReadonly(false);

            DecodeMap registriDecodeMap = sismaHelper.getSismaRegistroAgenzia();
            getForm().getFiltriSisma().getCd_registro_agenzia().setDecodeMap(registriDecodeMap);

            DecodeMap anniAGDecodeMap = sismaHelper.getSismaAnnoAgenzia();
            getForm().getFiltriSisma().getAnno_agenzia().setDecodeMap(anniAGDecodeMap);
        } else {
            getForm().getFiltriSisma().getCd_registro_agenzia().setReadonly(true);
            getForm().getFiltriSisma().getAnno_agenzia().setReadonly(true);
            getForm().getFiltriSisma().getCd_num_agenzia().setReadonly(true);
        }
    }

    private void popolaListaSisma() throws EMFError {
        if (getForm().getFiltriSisma().postAndValidate(getRequest(), getMessageBox())) {

            RicercaSismaDTO ricercaSismaDTO = new RicercaSismaDTO(getForm().getFiltriSisma(), isUtenteAgenzia());

            // POPOLA LA LISTA SISMA
            BaseTableInterface<?> sismaTable;
            if (isUtenteAgenzia()) {
                // Mostra tutti i progetti in tutti gli stati tranne quelli in BOZZA
                sismaTable = sismaEjb.findSismaTranne(ricercaSismaDTO, PigSisma.TiStato.BOZZA);
                getForm().getSismaList().setHideDeleteButton(true);
                getForm().getSismaList().setHideUpdateButton(true);
                getForm().getSismaList().setHideInsertButton(true);
                getForm().getSismaButtonList().setViewMode();
            } else {
                sismaTable = sismaEjb.findSismaByVers(ricercaSismaDTO, getVersatoreDellUtenteLoggato());
                getForm().getSismaList().setHideDeleteButton(false);
                getForm().getSismaList().setHideUpdateButton(false);
                getForm().getSismaList().setHideInsertButton(false);
                getForm().getSismaButtonList().setEditMode();
            }
            getForm().getSismaList().setTable(sismaTable);
            getForm().getSismaList().getTable().first();
            getForm().getSismaList().setStatus(Status.view);
        }
    }

    private DatiAnagraficiDto inizializzaWizard(BigDecimal idVersatore, BigDecimal idSisma) {
        getForm().getDatiGeneraliOutput().clear();
        getForm().getDatiGeneraliInput().clear();
        getForm().getDatiProfiloArchivistico().clear();
        // rimette readonly false e required a true perché potrebbero essere stati reimpostati dal dettaglio
        getForm().getDatiGeneraliInput().getData().setReadonly(false);
        getForm().getDatiGeneraliInput().getData().setRequired(true);
        getForm().getInserimentoWizard().reset();

        return inizializzaDatiGenerali(idVersatore, idSisma);
    }

    /*
     * se siEntraNelWizard == true significa che si vuole entrare in modifica nel wizard, altrimenti nel dettaglio
     */
    public void determinaStato(boolean siEntraNelWizard) throws EMFError {
        // Gestione form dettaglio
        String statoSisma = getForm().getDatiGeneraliOutput().getTi_stato_out().getValue();
        if (isUtenteAgenzia()) {
            // In tutti i casi un'agenzia non può entrare in modifica,cancellare o inserire un progetto
            getRequest().setAttribute(CAMPO_NASCONDI_UPDATE, "true");
            getForm().getDettaglioButtonList().setViewMode();
            if (statoSisma.equals(PigSisma.TiStato.BOZZA.name())
                    || statoSisma.equals(PigSisma.TiStato.DA_RIVEDERE.name())
                    || statoSisma.equals(PigSisma.TiStato.COMPLETATO.name())
                    || statoSisma.equals(PigSisma.TiStato.RICHIESTA_INVIO.name())
                    || statoSisma.equals(PigSisma.TiStato.INVIO_IN_CORSO.name())
                    || statoSisma.equals(PigSisma.TiStato.IN_ELABORAZIONE.name())) {
                getForm().getDatiAgenzia().setStatus(Status.view);
                getForm().getDatiAgenzia().setViewMode();
                getForm().getDatiProfiloArchivistico().setStatus(Status.view);
                getForm().getDatiProfiloArchivistico().setViewMode();
                getForm().getDocumentiCaricatiList().setViewMode();
            } else if (statoSisma.equals(PigSisma.TiStato.DA_VERIFICARE.name())) {
                getRequest().setAttribute(CAMPO_NASCONDI_UPDATE, "false");
                getForm().getDatiAgenzia().setStatus(Status.update); // Accende il salva
                getForm().getDatiAgenzia().setViewMode(); // ma non permette editing dei campi
                getForm().getDatiProfiloArchivistico().setStatus(Status.update);
                getForm().getDatiProfiloArchivistico().setViewMode();
                getForm().getDocumentiCaricatiList().getTi_verifica_agenzia().setEditMode(); // Abilita editing della
                // lista
            } else if (statoSisma.equals(PigSisma.TiStato.VERIFICATO.name())) {
                getRequest().setAttribute(CAMPO_NASCONDI_UPDATE, "false");
                getForm().getDatiAgenzia().setStatus(Status.update);
                getForm().getDatiAgenzia().setViewMode();
                getForm().getDatiProfiloArchivistico().setStatus(Status.update);
                getForm().getDatiProfiloArchivistico().setViewMode();
                getForm().getDocumentiCaricatiList().getTi_verifica_agenzia().setEditMode(); // Abilita editing della
                if (isVersatoreSelezionatoSAPrivato()) {
                    getForm().getDatiAgenzia().setEditMode();
                    if (isDatiAgenziaComplete()) {
                        getForm().getDettaglioButtonList().getVersaInAgenzia().setEditMode();
                    }
                } else if (isVersatoreSelezionatoSAPubblico()) {
                    getForm().getDettaglioButtonList().getVersaSisma().setEditMode();
                }
            } else if (statoSisma.equals(PigSisma.TiStato.VERSATO.name())) {
                getForm().getDatiAgenzia().setStatus(Status.update);
                getRequest().setAttribute(CAMPO_NASCONDI_UPDATE, "false");
                getForm().getDatiAgenzia().setEditMode();
                getForm().getDatiProfiloArchivistico().setStatus(Status.update);
                getForm().getDatiProfiloArchivistico().setEditMode();
                getForm().getDocumentiCaricatiList().setViewMode(); // Disabilita editing della lista
                if (isDatiAgenziaComplete()) {
                    getForm().getDettaglioButtonList().getVersaInAgenzia().setEditMode();
                }
            } else if (statoSisma.equals(PigSisma.TiStato.ANNULLATO.name())) {
                getForm().getDatiAgenzia().setViewMode();
                getForm().getDatiProfiloArchivistico().setViewMode();
                getForm().getDocumentiCaricatiList().setViewMode(); // Disabilita editing della lista
                getRequest().setAttribute(CAMPO_NASCONDI_UPDATE, "false");
            } else if (statoSisma.startsWith(PigSisma.TiStato.ERRORE.name())) { // MEV27430 - lo stato di ERRORE preso
                // dal form contiene anche una
                // descrizione, per questo controllo con
                // startWith e non equals.
                getForm().getDatiAgenzia().setViewMode();
                getForm().getDatiProfiloArchivistico().setViewMode();
                getForm().getDocumentiCaricatiList().setViewMode(); // Disabilita editing della lista
                getForm().getDettaglioButtonList().getRecuperaErrori().setEditMode();
                // Condizioni per mostrare il bottone "Errore"
            }

        } else { // UTENTE <> AGENZIA
            getForm().getDatiAgenzia().setStatus(Status.view);
            getForm().getDatiAgenzia().setViewMode();
            if (siEntraNelWizard) {
                getForm().getDatiProfiloArchivistico().setEditMode();
            } else {
                getForm().getDatiProfiloArchivistico().setStatus(Status.view);
                getForm().getDatiProfiloArchivistico().setViewMode();
            }
            getForm().getDettaglioButtonList().setViewMode();
            // può inserire, modificare o cancellare un sisma SOLO se BOZZA, DA_RIVEDERE
            if (!statoSisma.equals(PigSisma.TiStato.BOZZA.name())
                    && !statoSisma.equals(PigSisma.TiStato.DA_RIVEDERE.name())) {
                getRequest().setAttribute(CAMPO_NASCONDI_UPDATE, "true");

            }

            // MEV 28570 - il pulsante per riportare in bozza il sisma è sempre acceso.
            if (statoSisma.equals(PigSisma.TiStato.DA_RIVEDERE.name())) {
                getForm().getDettaglioButtonList().getRiportaInBozza().setEditMode();
            } // MEV26398
            else if (statoSisma.equals(PigSisma.TiStato.ANNULLATO.name())) {
                getForm().getDettaglioButtonList().getRiportaInBozza().setEditMode();
            } else {
                getForm().getDettaglioButtonList().getRiportaInBozza().setViewMode();
            }
        }
        // Gestione WIZARD 1 2 3
    }

    public boolean isUtenteSA() {
        boolean flag = false;
        Constants.TipoVersatore tipo = (Constants.TipoVersatore) getSession()
                .getAttribute(Constants.TIPO_VERSATORE_SISMA_UTENTE);
        if (tipo.equals(Constants.TipoVersatore.SA_PUBBLICO) || tipo.equals(Constants.TipoVersatore.SA_PRIVATO)) {
            flag = true;
        }
        return flag;
    }

    public boolean isUtenteSAPrivato() {
        boolean flag = false;
        Constants.TipoVersatore tipo = (Constants.TipoVersatore) getSession()
                .getAttribute(Constants.TIPO_VERSATORE_SISMA_UTENTE);
        if (tipo.equals(Constants.TipoVersatore.SA_PRIVATO)) {
            flag = true;
        }
        return flag;
    }

    public boolean isUtenteSAPubblico() {
        boolean flag = false;
        Constants.TipoVersatore tipo = (Constants.TipoVersatore) getSession()
                .getAttribute(Constants.TIPO_VERSATORE_SISMA_UTENTE);
        if (tipo.equals(Constants.TipoVersatore.SA_PUBBLICO)) {
            flag = true;
        }
        return flag;
    }

    public boolean isVersatoreSelezionatoSAPubblico() {
        Constants.TipoVersatore tipo = (Constants.TipoVersatore) getSession()
                .getAttribute(Constants.TIPO_VERSATORE_SISMA_SELEZIONATO);
        return tipo.equals(Constants.TipoVersatore.SA_PUBBLICO);
    }

    public boolean isVersatoreSelezionatoSAPrivato() {
        Constants.TipoVersatore tipo = (Constants.TipoVersatore) getSession()
                .getAttribute(Constants.TIPO_VERSATORE_SISMA_SELEZIONATO);
        return tipo.equals(Constants.TipoVersatore.SA_PRIVATO);
    }

    @Override
    public boolean inserimentoWizardOnSave() throws EMFError {
        return true;
    }

    @Override
    public void inserimentoWizardOnCancel() throws EMFError {
        loadSisma();
    }

    @Override
    public String getDefaultInserimentoWizardPublisher() throws EMFError {
        return Application.Publisher.SISMA_WIZARD;
    }

    @Override
    public void inserimentoWizardSismaOnEnter() throws EMFError {
        getForm().getDatiGeneraliInput().getModificato().setValue("N");

    }

    @Override
    public boolean inserimentoWizardSismaOnExit() throws EMFError {
        try {
            return salvaStep1(false);
        } catch (ObjectStorageException e) {
            return false;
        }
    }

    @Override
    public void inserimentoWizardUploadDocumentiOnEnter() throws EMFError {
        List<DocUploadDto> l = sismaEjb
                .findPigVSismaLisDocsPianoByTipoSismaFase(getForm().getDatiGeneraliOutput().getId_sisma_out().parse());
        ArrayList<DocUploadDto> alObbligatori = new ArrayList<>();
        ArrayList<DocUploadDto> alFacoltativi = new ArrayList<>();
        for (DocUploadDto docUploadDto : l) {
            if (docUploadDto.isObbligatorio()) {
                alObbligatori.add(docUploadDto);
            } else {
                alFacoltativi.add(docUploadDto);
            }
        }
        getSession().setAttribute("LISTA_DOC_UPLOAD_OBB", alObbligatori);
        getSession().setAttribute("LISTA_DOC_UPLOAD_FAC", alFacoltativi);
        getRequest().setAttribute("STATO_SISMA", getForm().getDatiGeneraliOutput().getTi_stato_out().getValue());

        BigDecimal idSisma = getForm().getSismaList().getTable().getCurrentRow().getBigDecimal(ID_SISMA);
        BigDecimal idVersatore = getForm().getSismaList().getTable().getCurrentRow().getBigDecimal("id_versatore");
        inizializzaDatiGenerali(idVersatore, idSisma);

    }

    @Override
    public boolean inserimentoWizardUploadDocumentiOnExit() throws EMFError {
        return true;
    }

    @Override
    public void inserimentoWizardRiepilogoOnEnter() throws EMFError {
        getRequest().setAttribute("terzoStep", true);
        getSession().removeAttribute(CAMPO_VERIFICA_ATTIVATA);

        Object[] ogg = sismaEjb
                .findDocumentiCaricatiPerIdSismaTB(getForm().getDatiGeneraliOutput().getId_sisma_out().parse());
        BaseTable tb = (BaseTable) ogg[0];
        tb.addSortingRule("fl_obbligatorio", SortingRule.DESC);
        tb.sort();
        getForm().getDocumentiCaricatiList().setTable(tb);
        getForm().getDocumentiCaricatiList().getTable().first();
        getForm().getDocumentiCaricatiList().getTi_verifica_agenzia()
                .setDecodeMap(ComboGetter.getMappaGenericFlagVerificaOkDaRivedere());
        getRequest().setAttribute("strDocFacolativi", ogg[1]);
        getRequest().setAttribute("alObbNonCaricati", ogg[2]);
        getRequest().setAttribute("alDocInErrore", ogg[3]);
        String statoSisma = getForm().getDatiGeneraliOutput().getTi_stato_out().parse();
        getRequest().setAttribute("statoSU", statoSisma);
        getForm().getDatiGeneraliOutput().getOggetto_out()
                .setValue(getForm().getDatiGeneraliInput().getOggetto().getValue());
        getForm().getRiepilogoButtonList().getVerificaDocumentiSisma().setReadonly(true);
        getForm().getRiepilogoButtonList().getVerificaAgenzia().setReadonly(true);
        getForm().getRiepilogoButtonList().getVerificaDocumentiSisma().setEditMode();
        getForm().getRiepilogoButtonList().getVerificaAgenzia().setEditMode();

        // MAC 30844
        getForm().getDatiGeneraliOutput().getTi_atto_out()
                .setValue(getForm().getDatiGeneraliInput().getId_sisma_val_atto().getDecodedValue());

        BigDecimal idSisma = getForm().getDatiGeneraliOutput().getId_sisma_out().parse();

        // VERIFICA SE il Job di verifica documenti è in corso
        if (verificaDocumentiSismaEjb.verificaInCorso(idSisma)) {
            getForm().getRiepilogoButtonList().getVerificaDocumentiSisma().setReadonly(true);
            getMessageBox().addWarning("Attenzione: verifica documenti in corso");
        } else {
            // TUTTI I PULSANTI SONO GIA' READONLY !!!
            if (statoSisma.equals(PigSisma.TiStato.BOZZA.name())) {
                NavigazioneSismaDto dto = sismaEjb.getDatiNavigazionePerSisma(idSisma);
                if (dto.isFileMancante()) {
                    // Lascia tutti i pulsanti disabilitati
                } else if (sismaEjb.existsPigSismaDocumentiDaVerificare(idSisma)) {
                    // SE CI SONO DOC ANCORA DA VERIFICARE ACCENDE LA VERIFICA
                    getForm().getRiepilogoButtonList().getVerificaDocumentiSisma().setReadonly(false);
                } else {
                    // SE I DOCUMENTI SONO TUTTI VERIFICATI ACCENDE LA VERIFICA AGENZIA
                    getForm().getRiepilogoButtonList().getVerificaAgenzia().setReadonly(false);
                }
            } else if (statoSisma.equals(PigSisma.TiStato.DA_RIVEDERE.name())) {
                /*
                 * Se ci sono ancora documenti da rivedere dall'agenzia non permette di fare la verifica dei documenti!
                 */
                if (sismaEjb.existsDocDaRicaricare(idSisma)) {
                    // Tutti i pulsanti rimangono spenti...
                } else if (sismaEjb.existsPigSismaDocumentiDaVerificare(idSisma)) {
                    // SE CI SONO DOC ANCORA DA VERIFICARE ACCENDE LA VERIFICA
                    getForm().getRiepilogoButtonList().getVerificaDocumentiSisma().setReadonly(false);
                } else {
                    // SE I DOCUMENTI SONO TUTTI VERIFICATI ACCENDE LA VERIFICA AGENZIA
                    getForm().getRiepilogoButtonList().getVerificaAgenzia().setReadonly(false);
                }
            }
        }
    }

    public void reloadThirdStepSisma() throws EMFError {
        inserimentoWizardRiepilogoOnEnter();
        Object[] ogg = sismaEjb
                .findDocumentiCaricatiPerIdSismaTB(getForm().getDatiGeneraliOutput().getId_sisma_out().parse());
        // Se anche la verifica è andata bene ma gli obbligatori non sono stati inseriti tutti è tutto disabilitato
        if (!((List<?>) ogg[2]).isEmpty()) {
            getForm().getRiepilogoButtonList().getVerificaDocumentiSisma().setReadonly(true);
        }
        // Se uno degli obbligatori è in errore sia Verifica che Versamento sono disabilitati dopo la verifica
        if (!((List<?>) ogg[3]).isEmpty()) {
            getForm().getRiepilogoButtonList().getVerificaDocumentiSisma().setReadonly(true);
        }
        forwardToPublisher(Application.Publisher.SISMA_WIZARD);
    }

    @Override
    public boolean inserimentoWizardRiepilogoOnExit() throws EMFError {
        return true;
    }

    @Override
    public void salvaBozza() throws EMFError {
        try {
            if (salvaStep1(true)) {
                getForm().getDatiGeneraliInput().getModificato().setValue("N");
            }
        } catch (ObjectStorageException e) {
            log.error(e.getMessage());
        }
        forwardToPublisher(getLastPublisher());
    }

    private boolean salvaStep1(boolean salvataggioDaTastoBozza) throws EMFError, ObjectStorageException {
        if (getForm().getDatiGeneraliOutput().getTi_stato_out().getValue() != null
                && (getForm().getDatiGeneraliOutput().getTi_stato_out().getValue()
                        .equals(PigSisma.TiStato.BOZZA.name()))
                || getForm().getDatiGeneraliOutput().getTi_stato_out().getValue().equals(PigSisma.TiStato.ERRORE.name())
                || getForm().getDatiGeneraliOutput().getTi_stato_out().getValue()
                        .equals(PigSisma.TiStato.DA_RIVEDERE.name())) {
            getMessageBox().clear();
            if (getForm().getDatiGeneraliInput().postAndValidate(getRequest(), getMessageBox())) {
                getForm().getDatiProfiloArchivistico().post(getRequest());
                // Fa tutto solo se l'utente ha modificato qualcosa nella form!
                if (getForm().getDatiGeneraliInput().getModificato() != null
                        && getForm().getDatiGeneraliInput().getModificato().getValue().equals("S")) {
                    SismaDto dto = new SismaDto();
                    dto.setIdVers(getVersatoreDellUtenteLoggato());
                    dto.setIdUserIam(getUser().getIdUtente());
                    dto.setAnno(new BigDecimal(getForm().getDatiGeneraliInput().getAnno().parse()));
                    dto.setNumero(getForm().getDatiGeneraliInput().getNumero().parse());
                    dto.setDsDescrizione(getForm().getDatiGeneraliInput().getDs_descrizione().parse());
                    dto.setIdTipoFinanziamento(getForm().getDatiGeneraliInput().getId_sisma_finanziamento().parse());
                    dto.setIdSismaProgettiAg(getForm().getDatiGeneraliInput().getId_sisma_progetti_ag().parse());
                    dto.setIdSismaFaseProgetto(getForm().getDatiGeneraliInput().getId_sisma_fase_progetto().parse());
                    dto.setIdSismaStatoProgetto(getForm().getDatiGeneraliInput().getId_sisma_stato_progetto().parse());
                    dto.setIdSismaValAtto(getForm().getDatiGeneraliInput().getId_sisma_val_atto().parse());
                    dto.setFlInterventoSoggettoATutela(getForm().getDatiGeneraliInput()
                            .getFl_intervento_soggetto_a_tutela().parse().equals(Constants.DB_TRUE));
                    dto.setClassifica(getForm().getDatiProfiloArchivistico().getClassifica().parse());
                    dto.setIdFascicolo(getForm().getDatiProfiloArchivistico().getId_fascicolo().parse());
                    dto.setIdSottofascicolo(getForm().getDatiProfiloArchivistico().getId_sottofascicolo().parse());
                    dto.setOggettoFascicolo(getForm().getDatiProfiloArchivistico().getOggetto_fascicolo().parse());
                    dto.setOggettoSottofascicolo(
                            getForm().getDatiProfiloArchivistico().getOggetto_sottofascicolo().parse());
                    dto.setData(getForm().getDatiGeneraliInput().getData().parse());
                    boolean isInserimento = getForm().getDatiGeneraliInput().getStatus().equals(Status.insert);
                    if (isInserimento) {
                        dto = sismaEjb.inserisciSisma(dto);
                    } else {
                        dto.setIdSisma(getForm().getDatiGeneraliOutput().getId_sisma_out().parse().longValueExact());
                        dto = sismaEjb.modificaSisma(dto);
                    }
                    if (dto.existsWarnMessages()) {
                        getMessageBox().addWarning(dto.getWarnMessage());
                        return false;
                    } else {
                        getForm().getDatiGeneraliOutput().getCd_key_out().setValue(dto.getCdKey());
                        getForm().getDatiGeneraliOutput().getId_sisma_out().setValue(Long.toString(dto.getIdSisma()));
                        getForm().getDatiGeneraliOutput().getTi_stato_out().setValue(dto.getTiStato());
                        getForm().getDatiGeneraliOutput().getCd_key_out().setValue(dto.getCdKey());
                        getForm().getDatiGeneraliInput().getOggetto().setValue(dto.getOggetto());
                        getForm().getDatiGeneraliOutput().getOggetto_out().setValue(dto.getOggetto());
                        getForm().getDatiGeneraliOutput().getDs_fase_sisma_out().setValue(dto.getDsFaseSisma());
                        getForm().getDatiGeneraliOutput().getDs_stato_progetto_out().setValue(dto.getDsStatoProgetto());
                        getForm().getDatiGeneraliInput().getDs_descrizione().setValue(dto.getDsDescrizione());
                        getForm().getDatiGeneraliInput().getId_sisma_val_atto().setValue(dto.getIdSismaValAtto() + "");
                        getForm().getDatiGeneraliOutput().getDt_stato_out()
                                .setValue(DateUtil.formatDateWithSlashAndTime(dto.getDtStato()));
                        getForm().getDatiGeneraliOutput().getDt_creazione_out()
                                .setValue(DateUtil.formatDateWithSlashAndTime(dto.getDtCreazione()));
                        // MEV29331
                        getForm().getDatiGeneraliOutput().getTi_atto_out().setValue(dto.getNmTipoAtto());
                        getForm().getDatiGeneraliOutput().getAnno_out().setValue(dto.getAnno().longValueExact() + "");
                        getForm().getDatiGeneraliOutput().getNumero_out().setValue(dto.getNumero());

                        // MAC 30844
                        // I dati del versatore sono quelli del versatore dell'utente SA di qualsiasi tipo
                        DatiAnagraficiDto dtoDA = sismaEjb.getDatiVersatoreByIdVers(getVersatoreDellUtenteLoggato(),
                                BigDecimal.valueOf(dto.getIdSisma()));
                        getForm().getDatiGeneraliOutput().getSoggetto_attuatore_out()
                                .setValue(dtoDA.getSoggettoAttuatore());

                        Message msg = new Message();
                        if (salvataggioDaTastoBozza) {
                            msg.setText("Progetto " + (isInserimento ? "inserito" : "modificato")
                                    + " con successo. Continuare la compilazione selezionando il tasto “Avanti”.");
                        } else {
                            msg.setText("Progetto " + (isInserimento ? "inserito" : "modificato") + " con successo.");
                        }
                        getMessageBox().addMessage(msg);
                        getForm().getDatiGeneraliInput().setStatus(Status.update);
                        return true;
                    }
                } else {
                    return true;
                }
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    @Override
    public void deleteSismaList() throws EMFError {
        int riga = getForm().getSismaList().getTable().getCurrentRowIndex();
        BigDecimal idSisma = getForm().getSismaList().getTable().getCurrentRow().getBigDecimal(ID_SISMA);
        SismaDto dto = sismaEjb.getSismaById(idSisma);
        if (dto.getTiStato().equals(PigSisma.TiStato.BOZZA.name())) {
            try {
                sismaEjb.cancellaSisma(idSisma);
                getForm().getSismaList().getTable().remove(riga);
                getMessageBox().addInfo("Progetto cancellato con successo");
            } catch (ObjectStorageException e) {
                log.error(e.getMessage());
            }
        } else {
            PigErrore err = messaggiHelper.retrievePigErrore("PING-ERRSISMA24");
            getMessageBox().addWarning(err.getDsErrore());
        }
        forwardToPublisher(getLastPublisher());
    }

    public void rimuoviFile() throws EMFError {
        JSONObject result = new JSONObject();
        try {
            String nmFileOrig = getRequest().getParameter("nmFileOrig");
            BigDecimal idSisma = getForm().getDatiGeneraliOutput().getId_sisma_out().parse();
            String mex = sismaEjb.cancellaDoc(idSisma, nmFileOrig);
            if (mex == null) {
                result.put("codice", "0");
            } else {
                result.put("codice", -1);
                result.put("messaggio", mex);
            }
        } catch (JSONException | ObjectStorageException ex) {
            //
        }
        redirectToAjax(result);
    }

    public void tipoFinanziamentoModificatoJson() {
        DecodeMap mappa = null;
        String idSismaFinanziamento = getRequest().getParameter("idSismaFinanziamento");
        if (idSismaFinanziamento.trim().equals("")) {
            idSismaFinanziamento = "0";
        }
        String dsTipoFinanziamento = getRequest().getParameter("dsTipoFinanziamento");
        getForm().getDatiGeneraliOutput().getDs_tipo_finanziamento_out().setValue(dsTipoFinanziamento);
        mappa = DecodeMap.Factory.newInstance(
                sismaEjb.findPigSismaProgettiAgByIdEnteFinanziamentoTB(getVersatoreDellUtenteLoggato(),
                        new BigDecimal(Long.valueOf(idSismaFinanziamento))),
                "id_sisma_progetti_ag", "codice_intervento");
        getForm().getDatiGeneraliInput().getId_sisma_finanziamento().setValue("" + idSismaFinanziamento);
        // Svuota tutte le combo successive
        getForm().getDatiGeneraliInput().getId_sisma_progetti_ag().setDecodeMap(mappa);
        getForm().getDatiGeneraliInput().getId_sisma_progetti_ag().setValue("");
        getForm().getDatiGeneraliInput().getId_sisma_fase_progetto().setDecodeMap(new DecodeMap());
        getForm().getDatiGeneraliInput().getId_sisma_fase_progetto().setValue("");
        getForm().getDatiGeneraliInput().getId_sisma_stato_progetto().setDecodeMap(new DecodeMap());
        getForm().getDatiGeneraliInput().getId_sisma_stato_progetto().setValue("");
        // svuota i campi dei dati generali output
        getForm().getDatiGeneraliOutput().getCodice_intervento_out().clear();
        getForm().getDatiGeneraliOutput().getDenominazione_intervento_out().clear();
        redirectToAjax(mappa.asJSON());
    }

    /* Torna il versatore dell'utente attualmente loggato */
    public BigDecimal getVersatoreDellUtenteLoggato() {
        return getUser().getIdOrganizzazioneFoglia();
    }

    public void tipoInterventoModificatoJson() {
        DecodeMap mappa;
        BigDecimal idSismaProgettiAg = new BigDecimal(getRequest().getParameter("idSismaProgettiAg"));
        BigDecimal idSismaFinanziamento = new BigDecimal(getRequest().getParameter("idSismaFinanziamento"));
        mappa = DecodeMap.Factory.newInstance(sismaEjb.findPigSismaFaseByFinTB(idSismaFinanziamento),
                "id_sisma_fase_progetto", "ds_fase_sisma"); //
        getForm().getDatiGeneraliInput().getId_sisma_progetti_ag().setValue("" + idSismaProgettiAg);
        getForm().getDatiGeneraliInput().getId_sisma_fase_progetto().setDecodeMap(mappa);
        getForm().getDatiGeneraliInput().getId_sisma_fase_progetto().setValue("");
        getForm().getDatiGeneraliInput().getId_sisma_stato_progetto().setDecodeMap(new DecodeMap());
        getForm().getDatiGeneraliInput().getId_sisma_stato_progetto().setValue("");
        JSONObject oggPadre = new JSONObject();
        JSONObject ogg = mappa.asJSON();
        PigSismaProgettiAg pigSismaProgettiAg = sismaHelper.getEntityManager().find(PigSismaProgettiAg.class,
                idSismaProgettiAg.longValueExact());
        try {
            // Metto in un oggetto JSON una proprietà con il valore della denominazione intervento e nella proprietà
            // "array" L'array con il contenuto della combo completa
            if (pigSismaProgettiAg != null) {
                oggPadre.put("denominazioneIntervento", pigSismaProgettiAg.getDenominazioneIntervento());
                // MODIFICA MAC#25468: Progetti ricostruzione: correzione modalità di valorizzazione dei dati
                // dell'intervento
                oggPadre.put("ubicazioneComune", pigSismaProgettiAg.getUbicazioneComune());
                oggPadre.put("ubicazioneProvincia", pigSismaProgettiAg.getUbicazioneProvincia());
                oggPadre.put("enteProprietario", pigSismaProgettiAg.getEnteProprietario());
                oggPadre.put("naturaEnteProprietario", pigSismaProgettiAg.getNaturaEnteProprietario());
                getForm().getDatiGeneraliOutput().getUbicazione_comune_out()
                        .setValue(pigSismaProgettiAg.getUbicazioneComune());
                getForm().getDatiGeneraliOutput().getUbicazione_provincia_out()
                        .setValue(pigSismaProgettiAg.getUbicazioneProvincia());
                getForm().getDatiGeneraliOutput().getEnte_proprietario_out()
                        .setValue(pigSismaProgettiAg.getEnteProprietario());
                getForm().getDatiGeneraliOutput().getNatura_ente_proprietario_out()
                        .setValue(pigSismaProgettiAg.getNaturaEnteProprietario());
                //
                // Eventuale reimpostazione della combo Soggetto a Tutela !
                oggPadre.put("flInterventoSoggettoATutela", pigSismaProgettiAg.getFlInterventoSoggettoATutela());
                getForm().getDatiGeneraliOutput().getCodice_intervento_out()
                        .setValue(pigSismaProgettiAg.getCodiceIntervento());
                getForm().getDatiGeneraliOutput().getDenominazione_intervento_out()
                        .setValue(pigSismaProgettiAg.getDenominazioneIntervento());
                getForm().getDatiGeneraliInput().getFl_intervento_soggetto_a_tutela()
                        .setValue(pigSismaProgettiAg.getFlInterventoSoggettoATutela());
            } else {
                oggPadre.put("denominazioneIntervento", "");
                getForm().getDatiGeneraliInput().getFl_intervento_soggetto_a_tutela().setValue("");
            }
            oggPadre.put(CAMPO_ARRAY, ogg);
        } catch (JSONException ex) {
            //
        }
        redirectToAjax(oggPadre);
    }

    // Torna il combo degli STATI
    public void tipoFaseModificataJson() {
        DecodeMap mappa;

        if (!getRequest().getParameter("idSismaFaseProgetto").isEmpty()) {
            BigDecimal idSismaFaseProgetto = new BigDecimal(getRequest().getParameter("idSismaFaseProgetto"));
            mappa = DecodeMap.Factory.newInstance(
                    sismaEjb.findPigSismaStatoProgettoByIdSismaFaseProgettoTB(idSismaFaseProgetto),
                    "id_sisma_stato_progetto", "ds_stato_progetto");
        } else {
            // MAC 29470 se idSismaFaseProgetto pulisco la decodemap
            mappa = new DecodeMap();
        }

        getForm().getDatiGeneraliInput().getId_sisma_stato_progetto().setDecodeMap(mappa);

        getForm().getDatiGeneraliInput().getId_sisma_stato_progetto().setValue("");
        redirectToAjax(mappa.asJSON());

    }

    public void getErroriJson() {
        String codErroreLike = getRequest().getParameter("codErroreLike");
        // Torna un JSON con gli errori di PING-ERRSISMA%
        JSONObject json = sismaEjb.retrievePigErroreLikeAsJsonString(codErroreLike);
        redirectToAjax(json);
    }

    @Override
    public void versaSisma() throws Throwable {
        EsitoSalvataggioSisma esito = sismaEjb.versaSisma(getForm().getDatiGeneraliOutput().getId_sisma_out().parse(),
                getUser().getIdUtente(), getUser().getIdOrganizzazioneFoglia());
        if (esito.isOk()) {
            loadSisma();
            forwardToPublisher(Application.Publisher.SISMA);
        } else {
            PigErrore err = messaggiHelper.retrievePigErrore("PING-ERRSISMA23");
            getMessageBox().addWarning(err.getDsErrore());
            forwardToPublisher(getLastPublisher());
        }
    }

    @Override
    public void versaInAgenzia() throws Throwable {
        getForm().getDatiAgenzia().postAndValidate(getRequest(), getMessageBox());

        BigDecimal idSisma = getForm().getSismaList().getTable().getCurrentRow().getBigDecimal(ID_SISMA);

        if (getMessageBox().isEmpty()) {
            if (!sismaEjb.controllaUnivocitaDatiAgenzia(idSisma, getUser().getIdOrganizzazioneFoglia().longValueExact(),
                    getForm().getDatiAgenzia().getRegistro_ag().parse(),
                    new BigDecimal(getForm().getDatiAgenzia().getAnno_ag().parse()),
                    getForm().getDatiAgenzia().getNumero_ag().parse())) {
                getMessageBox().addError(String.format(
                        messaggiHelper.retrievePigErrore(PING_ERRSISMA_20).getDsErrore().replace("\\{0\\}", "%s"),
                        getForm().getDatiAgenzia().getRegistro_ag().parse() + "-"
                                + getForm().getDatiAgenzia().getAnno_ag().parse() + "-"
                                + getForm().getDatiAgenzia().getNumero_ag().parse()));
            }
        } else {
            forwardToPublisher(getLastPublisher());
        }

        if (getMessageBox().isEmpty()) {
            salvaDatiAgenzia(getForm().getDatiGeneraliOutput().getId_sisma_out().parse().longValueExact());
            EsitoSalvataggioSisma esitoSalvataggio = sismaEjb.versaSisma(
                    getForm().getDatiGeneraliOutput().getId_sisma_out().parse(), getUser().getIdUtente(),
                    getUser().getIdOrganizzazioneFoglia());
            if (esitoSalvataggio.isOk()) {
                loadSisma();
                if (esitoSalvataggio.getStato().equals(PigSisma.TiStato.RICHIESTA_INVIO)) {
                    getMessageBox().setViewMode(MessageBox.ViewMode.plain);
                    getMessageBox().addMessage(new Message(MessageLevel.INF,
                            "Per completare il procedimento è necessario predisporre la PEC da inoltrare al soggetto attuatore ed agli enti coinvolti "
                                    + "nell’istruttoria, comunicando l’avvenuta validazione degli elaborati caricati, il versamento in conservazione, il Rapporto"
                                    + " di Versamento e la comunicazione di avvio del procedimento di istruttoria."));
                }
                forwardToPublisher(Application.Publisher.SISMA);
            } else {
                PigErrore err = messaggiHelper.retrievePigErrore("PING-ERRSISMA23");
                getMessageBox().addWarning(err.getDsErrore());
                forwardToPublisher(getLastPublisher());
            }
        } else {
            forwardToPublisher(getLastPublisher());
        }
    }

    public void downloadSismaRapportoAgenzia() throws EMFError {
        String riga = getRequest().getParameter("riga");
        Integer nr = Integer.parseInt(riga);

        BigDecimal idSisma = getForm().getSismaList().getTable().getRow(nr).getBigDecimal("id_Sisma");
        downloadSismaOperation(idSisma, true);
    }

    public void downloadSismaRapportoVersamento() throws EMFError {
        String riga = getRequest().getParameter("riga");
        Integer nr = Integer.parseInt(riga);

        BigDecimal idSisma = getForm().getSismaList().getTable().getRow(nr).getBigDecimal("id_Sisma");

        downloadSismaOperation(idSisma, false);
    }

    public void downloadSismaOperation(BigDecimal idSisma, boolean estraiRapportoAgenzia) throws EMFError {
        SismaDto dto = sismaEjb.getSismaById(idSisma);
        if (dto.getTiStato().equals(PigSisma.TiStato.VERSATO.name())
                || dto.getTiStato().equals(PigSisma.TiStato.COMPLETATO.name())) {
            /* CHIAMARE IL WS per il download del rapporto di versamento */
            // NUOVA ROBA DA IAM
            String versione = configurationHelper
                    .getValoreParamApplicByApplic(Constants.NmParamApplic.VERSIONE_XML_RECUP_UD.name());
            String loginname = configurationHelper
                    .getValoreParamApplicByApplic(Constants.NmParamApplic.USERID_RECUP_UD.name());
            String password = configurationHelper
                    .getValoreParamApplicByApplic(Constants.NmParamApplic.PSW_RECUP_UD.name());
            Integer timeout = Integer.parseInt(
                    configurationHelper.getValoreParamApplicByApplic(Constants.NmParamApplic.TIMEOUT_RECUP_UD.name()));
            String url = configurationHelper
                    .getValoreParamApplicByApplic(Constants.NmParamApplic.URL_RECUP_RAPP_VERS.name());
            // Inizio chiamata al WS
            HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory();
            clientHttpRequestFactory.setConnectTimeout(timeout);
            try {
                // Creo l'header della richiesta
                HttpHeaders header = new HttpHeaders();
                header.setContentType(MediaType.MULTIPART_FORM_DATA);
                // Creo i parametri della richiesta
                MultiValueMap<String, Object> multipartRequest = new LinkedMultiValueMap<>();
                multipartRequest.add("VERSIONE", versione);
                multipartRequest.add("LOGINNAME", loginname);
                multipartRequest.add("PASSWORD", password);
                multipartRequest.add("XMLSIP",
                        sismaEjb.getXmlRichiestaRappVersByIdSisma(idSisma, estraiRapportoAgenzia));
                // Creo la richiesta
                HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(multipartRequest, header);
                // Mi faccio restituire la risposta dalla chiamata al WS
                HttpEntity<Resource> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity,
                        Resource.class);
                // Recupero l'inputStream col flusso del file da scaricare dal corpo della response
                InputStream is = response.getBody().getInputStream();
                // Dall'header recupero il Content-Disposition e successivamente da esso il filename
                String fileName = response.getHeaders().getFirst(CONTENT_DISPOSITION);
                MediaType contentType = response.getHeaders().getContentType();
                if (contentType != null && "zip".equals(contentType.getSubtype())) {
                    // Li setto nella response che utilizzerò per il ServletOutputStream
                    getResponse().setContentType(contentType.toString());
                    getResponse().setHeader(CONTENT_DISPOSITION, fileName);
                    try ( // try-with-resource senza bisogno di close su outStream
                            OutputStream outStream = getServletOutputStream()) {
                        // Leggo dall'inputStream e "riempio" l'outputstream
                        byte[] buffer = new byte[8 * 1024];
                        int bytesRead;
                        while ((bytesRead = is.read(buffer)) != -1) {
                            outStream.write(buffer, 0, bytesRead);
                        }
                        outStream.flush();
                    }
                    is.close();
                    forwardToPublisher(getLastPublisher());
                } else {
                    getMessageBox().addError(
                            "Errore durante il tentativo di download del file: impossibile recuperare il nome file");
                    // leggo l'input stream e recupero l'xml col messaggio di errore
                    StringBuilder sb = new StringBuilder();
                    try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
                        String sCurrentLine;
                        while ((sCurrentLine = br.readLine()) != null) {
                            sb.append(sCurrentLine);
                        }
                    }
                    int codiceErroreStart = sb.indexOf("<CodiceErrore>");
                    int codiceErroreStop = sb.indexOf("</CodiceErrore>");
                    int dsErroreStart = sb.indexOf("<MessaggioErrore>");
                    int dsErroreStop = sb.indexOf("</MessaggioErrore>");
                    String messaggioErrore = null;

                    if (codiceErroreStart != -1 && codiceErroreStop != -1) {
                        messaggioErrore = sb.substring(codiceErroreStart + 14, codiceErroreStop);
                    }
                    if (dsErroreStart != -1 && dsErroreStop != -1) {
                        messaggioErrore = messaggioErrore + " - " + sb.substring(dsErroreStart + 17, dsErroreStop);
                    }
                    if (messaggioErrore == null) {
                        messaggioErrore = "Errore durante il tentativo di download del file: impossibile recuperare il nome file";
                    }
                    getMessageBox().addError(messaggioErrore);
                    forwardToPublisher(getLastPublisher());
                }
            } catch (ResourceAccessException ex) {
                getMessageBox().addError("Errore durante il tentativo di download del file: timeout scaduto");
                forwardToPublisher(getLastPublisher());
            } catch (HttpClientErrorException ex) {
                getMessageBox().addError("Errore durante la chiamata al ws per il download del file");
                forwardToPublisher(getLastPublisher());
            } catch (IOException | RestClientException ex) {
                getMessageBox().addError("Errore durante il tentativo di download del file");
                forwardToPublisher(getLastPublisher());
            }
        } else {
            PigErrore err = messaggiHelper.retrievePigErrore("PING-ERRSISMA25");
            getMessageBox().addWarning(err.getDsErrore());
            forwardToPublisher(getLastPublisher());
        }
    }

    public void downloadSismaListOperation() {
        File tmpFile = null;
        FileOutputStream out = null;
        try {
            String riga = getRequest().getParameter("riga");
            Integer nr = Integer.parseInt(riga);
            BigDecimal idSisma = getForm().getSismaList().getTable().getRow(nr).getBigDecimal("id_Sisma");

            Date dat = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss");
            String nomeFile = "lista_versamento_" + sdf.format(dat) + ".txt";

            tmpFile = new File(System.getProperty("java.io.tmpdir"), nomeFile);
            String str = sismaEjb.getListaVersamentoString(idSisma);
            out = new FileOutputStream(tmpFile);
            IOUtils.write(str, out, StandardCharsets.UTF_8);
            getRequest().setAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_ACTION.name(), getControllerName());
            getSession().setAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_FILENAME.name(), tmpFile.getName());
            getSession().setAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_FILEPATH.name(), tmpFile.getPath());
            getSession().setAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_DELETEFILE.name(), Boolean.toString(true));
            getSession().setAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_CONTENTTYPE.name(),
                    WebConstants.MIME_TYPE_GENERIC);
        } catch (Exception ex) {
            log.error("Errore in download " + ExceptionUtils.getRootCauseMessage(ex), ex);
            getMessageBox().addError("Errore inatteso nella preparazione del download<br/>");
        } finally {
            IOUtils.closeQuietly(out);
        }

        if (getMessageBox().hasError() || getMessageBox().hasWarning()) {
            forwardToPublisher(getLastPublisher());
        } else {
            forwardToPublisher(Application.Publisher.DOWNLOAD_PAGE);
        }
    }

    public void download() throws EMFError, IOException {
        log.debug(">>>DOWNLOAD");
        String filename = (String) getSession().getAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_FILENAME.name());
        String path = (String) getSession().getAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_FILEPATH.name());
        Boolean deleteFile = Boolean.parseBoolean(
                (String) getSession().getAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_DELETEFILE.name()));
        String contentType = (String) getSession()
                .getAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_CONTENTTYPE.name());
        if (path != null && filename != null) {
            File fileToDownload = new File(path);
            if (fileToDownload.exists()) {
                /*
                 * Definiamo l'output previsto che sarà  un file in formato zip di cui si occuperà  la servlet per fare
                 * il download
                 */
                OutputStream outUD = getServletOutputStream();
                getResponse().setContentType(
                        StringUtils.isBlank(contentType) ? WebConstants.MIME_TYPE_GENERIC : contentType);
                getResponse().setHeader(CONTENT_DISPOSITION, "attachment; filename=\"" + filename);

                FileInputStream inputStream = null;
                try {
                    getResponse().setHeader("Content-Length", String.valueOf(fileToDownload.length()));
                    inputStream = new FileInputStream(fileToDownload);
                    byte[] bytes = new byte[8000];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(bytes)) != -1) {
                        outUD.write(bytes, 0, bytesRead);
                    }
                    outUD.flush();
                } catch (IOException e) {
                    log.error("Eccezione nel recupero del documento ", e);
                    getMessageBox().addError("Eccezione nel recupero del documento");
                } finally {
                    IOUtils.closeQuietly(inputStream);
                    IOUtils.closeQuietly(outUD);
                    inputStream = null;
                    outUD = null;
                    freeze();
                }
                // Nel caso sia stato richiesto, elimina il file
                if (Boolean.TRUE.equals(deleteFile)) {
                    Files.delete(fileToDownload.toPath());
                }
            } else {
                getMessageBox().addError("Errore durante il tentativo di download. File non trovato");
                forwardToPublisher(getLastPublisher());
            }
        } else {
            getMessageBox().addError("Errore durante il tentativo di download. File non trovato");
            forwardToPublisher(getLastPublisher());
        }
        getSession().removeAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_FILENAME.name());
        getSession().removeAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_FILEPATH.name());
        getSession().removeAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_DELETEFILE.name());
        getSession().removeAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_CONTENTTYPE.name());
    }

    @Override
    public void recuperaErrori() throws Throwable {
        getRequest().setAttribute("customBoxRecuperoErrori", true);
        getRequest().setAttribute(CAMPO_NASCONDI_UPDATE, "true");
        BigDecimal idSisma = getForm().getSismaList().getTable().getCurrentRow().getBigDecimal(ID_SISMA);
        DecodeMapIF mappaStati = sismaEjb.getNuoviStatiPerRecuperoErroriDM(idSisma);
        getForm().getRecuperoErrori().getTi_nuovo_stato().setDecodeMap(mappaStati);
        getForm().getRecuperoErrori().setEditMode();
        getForm().getRecuperoErrori().getTi_nuovo_stato().setEditMode();
        forwardToPublisher(getLastPublisher());
    }

    @Override
    public void confermaRecuperoErrore() throws EMFError {
        String isFromAjax = getRequest().getParameter("isFromJavaScript");
        if (Boolean.parseBoolean(isFromAjax)) {
            String tiNuovoStato = getRequest().getParameter("ti_nuovo_stato");
            if (tiNuovoStato != null && !tiNuovoStato.equals("")) {
                if (getForm().getDatiGeneraliOutput().getId_sisma_out() != null) {
                    BigDecimal idSisma = getForm().getDatiGeneraliOutput().getId_sisma_out().parse();
                    Date dataStato = sismaEjb.recuperoErroreSisma(idSisma, tiNuovoStato);
                    getForm().getDatiGeneraliOutput().getTi_stato_out().setValue(tiNuovoStato);
                    getForm().getDatiGeneraliOutput().getDt_stato_out()
                            .setValue(DateUtil.formatDateWithSlashAndTime(dataStato));
                    getMessageBox()
                            .addInfo(String.format("Il progetto è stato riportato allo stato '%s'.", tiNuovoStato));
                    setNavigationEvent(NE_DETTAGLIO_VIEW);
                    loadDettaglioSisma(idSisma);
                    determinaStato(false);
                    getMessageBox().setViewMode(MessageBox.ViewMode.plain);
                    getForm().getRecuperoErrori().setViewMode();
                }
            }
        }
        forwardToPublisher(getLastPublisher());
    }

    // Praticamente non viene mai chiamato
    @Override
    public void annullaRecuperoErrore() throws EMFError {
        forwardToPublisher(getLastPublisher());
    }

    @Override
    public void riportaInBozza() throws Throwable {
        sismaEjb.riportaInBozza(getForm().getDatiGeneraliOutput().getId_sisma_out().parse());

        // MAC 27381 - Gli diciamo che vogliamo tornare al dettaglio
        setNavigationEvent(ListAction.NE_DETTAGLIO_VIEW);
        BigDecimal id = getForm().getDatiGeneraliOutput().getId_sisma_out().parse();
        loadDettaglioSisma(id);
        determinaStato(false);

        // MEV28750 - svuoto i campi di verifica dei file.
        for (Iterator<? extends BaseRowInterface> iterator = getForm().getDocumentiCaricatiList().getTable()
                .iterator(); iterator.hasNext();) {
            BaseRowInterface riga = iterator.next();
            riga.setString("ti_verifica_agenzia", "");
        }

        forwardToPublisher(getLastPublisher());
    }

    @Override
    public void riportaInStatoVersato() throws Throwable {
        sismaEjb.cambiaStatoSisma(getForm().getDatiGeneraliOutput().getId_sisma_out().parse(),
                PigSisma.TiStato.VERSATO);
        loadSisma();
    }

    @Override
    public void verificaDocumentiSisma() throws Throwable {
        // Recupero il sisma di cui devo verificare i documenti
        BigDecimal idSisma = getForm().getDatiGeneraliOutput().getId_sisma_out().parse();
        // Controllo non ci sia una verifica in corso
        boolean verificaInCorso = verificaDocumentiSismaEjb.verificaInCorso(idSisma);
        // Se la verifica non è in corso, procedo
        if (!verificaInCorso) {
            getMessageBox().addInfo("Verifica documenti avviata con successo.");
            verificaDocumentiSismaEjb.callVerificaDocumentiAsync(idSisma);
            getForm().getRiepilogoButtonList().getVerificaDocumentiSisma().setReadonly(true);
            getSession().setAttribute(CAMPO_VERIFICA_ATTIVATA, true);
        } else {
            getMessageBox().addWarning("Attenzione: verifica documenti in corso");
        }
        forwardToPublisher(Application.Publisher.SISMA_WIZARD);
    }

    @Override
    public void verificaAgenzia() throws Throwable {
        BigDecimal idSisma = getForm().getDatiGeneraliOutput().getId_sisma_out().parse();
        sismaEjb.cambiaStatoSisma(idSisma, PigSisma.TiStato.DA_VERIFICARE);
        getForm().getDatiGeneraliOutput().getTi_stato_out().setValue(PigSisma.TiStato.DA_VERIFICARE.name());
        getForm().getRiepilogoButtonList().getVerificaAgenzia().setReadonly(true);
        // MAC 31483
        setNavigationEvent(ListAction.NE_DETTAGLIO_VIEW);
        loadSisma();
        forwardToPublisher(Application.Publisher.SISMA);
    }

    @SuppressLogging
    public void checkDocumentiSisma() throws EMFError {
        try {
            // Recupero il sisma di cui devo verificare i documenti
            BigDecimal idSisma = getForm().getDatiGeneraliOutput().getId_sisma_out().parse();
            String oggetto = sismaEjb.getOggettoSisma(idSisma);
            // Mi creo gli oggetti Ajax per gestire il polling
            JSONObject listObject = new JSONObject();
            JSONArray array = new JSONArray();
            List<String> listaDocumentiConErrori = new ArrayList<>();
            // Controllo che non ci sia una verifica in corso tramite DB
            boolean verificaInCorso = verificaDocumentiSismaEjb.verificaInCorso(idSisma);
            // Se non ci sono verifiche in corso e non ne erano partite prima, non devo fare nulla, lascio andare il
            // poll senza mostrare alcun messaggio
            if (!verificaInCorso) {
                if (getSession().getAttribute(CAMPO_VERIFICA_ATTIVATA) == null) {
                    JSONObject sismaObject = new JSONObject();
                    sismaObject.put(WebConstants.PARAMETER_JSON_VERIFICA_DOCUMENTI_SISMA.ID_SISMA.name(), idSisma);
                    sismaObject.put(WebConstants.PARAMETER_JSON_VERIFICA_DOCUMENTI_SISMA.OGGETTO.name(), oggetto);
                    sismaObject.put(WebConstants.PARAMETER_JSON_VERIFICA_DOCUMENTI_SISMA.STOP_POLL.name(), "NO");
                    sismaObject.put(
                            WebConstants.PARAMETER_JSON_VERIFICA_DOCUMENTI_SISMA.LISTA_DOCUMENTI_CON_ERRORI.name(), "");
                    sismaObject.put(WebConstants.PARAMETER_JSON_VERIFICA_DOCUMENTI_SISMA.DOCUMENTI_CON_ERRORI.name(),
                            "NO");
                    array.put(sismaObject);
                    listObject.put(CAMPO_ARRAY, array);
                    redirectToAjax(listObject);
                } // Se non ci sono verifihe in corso ma evidentemente prima erano state fatte partire, devo mostrare i
                  // risultati
                else if (getSession().getAttribute(CAMPO_VERIFICA_ATTIVATA) != null) {
                    getSession().removeAttribute(CAMPO_VERIFICA_ATTIVATA);
                    // Se esistono documenti con errore
                    boolean existsDocumentiDaVerifByTabellaConErrore = verificaDocumentiSismaEjb
                            .existsDocumentiDaVerificareConErrorePerSisma(idSisma);

                    if (existsDocumentiDaVerifByTabellaConErrore) {
                        // Cerco se tra quelli rimasti in lista di verifica, ci siano degli errori
                        listaDocumentiConErrori = verificaDocumentiSismaEjb
                                .getDocumentiVerificatiConErrorePerSisma(idSisma);
                    }
                    String documentiConErrori = "NO";
                    if (listaDocumentiConErrori != null && !listaDocumentiConErrori.isEmpty()) {
                        documentiConErrori = "SI";
                    }
                    JSONObject sismaObject = new JSONObject();
                    sismaObject.put(WebConstants.PARAMETER_JSON_VERIFICA_DOCUMENTI_SISMA.ID_SISMA.name(), idSisma);
                    sismaObject.put(WebConstants.PARAMETER_JSON_VERIFICA_DOCUMENTI_SISMA.OGGETTO.name(), oggetto);
                    sismaObject.put(WebConstants.PARAMETER_JSON_VERIFICA_DOCUMENTI_SISMA.STOP_POLL.name(), "SI");
                    sismaObject.put(
                            WebConstants.PARAMETER_JSON_VERIFICA_DOCUMENTI_SISMA.LISTA_DOCUMENTI_CON_ERRORI.name(),
                            listaDocumentiConErrori.toString());
                    sismaObject.put(WebConstants.PARAMETER_JSON_VERIFICA_DOCUMENTI_SISMA.DOCUMENTI_CON_ERRORI.name(),
                            documentiConErrori);
                    array.put(sismaObject);
                    listObject.put(CAMPO_ARRAY, array);
                    redirectToAjax(listObject);
                }
            }
            // NON FACCIO NULLA FUORI DALL'IF (NIENTE ELSE): NON AGISCO SUL POLLING CHE PARTE IN AUTOMATICO DALLA JSP
        } catch (JSONException ex) {
            getMessageBox().addError(ExceptionUtils.getRootCauseMessage(ex));
            forwardToPublisher(getLastPublisher());
        }
    }

    @Override
    public void caricaSisma() throws Throwable {
        setNavigationEvent(ListAction.NE_DETTAGLIO_INSERT);
        getForm().getDatiGeneraliInput().setStatus(Status.insert);
        getForm().getDatiProfiloArchivistico().setStatus(Status.insert);
        getForm().getDatiProfiloArchivistico().setEditMode();
        if (inizializzaWizard(getVersatoreDellUtenteLoggato(), null) != null) {
            // Popola i finanziamanti relativi al versatore dell'utente loggato!
            popolaLineaDiFinanziamento(getVersatoreDellUtenteLoggato());

            // MEV 26937
            DecodeMap mappa = DecodeMap.Factory.newInstance(sismaEjb.findPigSismaValAtto(), "id_sisma_val_atto",
                    "nm_tipo_atto");
            getForm().getDatiGeneraliInput().getId_sisma_val_atto().setDecodeMap(mappa);

            forwardToPublisher(Application.Publisher.SISMA_WIZARD);
        } else {
            getMessageBox().addInfo("Non ci sono interventi configurati per il versatore.");
            forwardToPublisher(Application.Publisher.SISMA);
        }
    }

    // MEV26267 - scarica il report della verififca sui file
    public void downloadVerificaReport() throws EMFError {
        String riga = getRequest().getParameter("riga");
        Integer nr = Integer.parseInt(riga);

        BigDecimal id = getForm().getDatiGeneraliOutput().getId_sisma_out().parse();
        PigSisma sisma = sismaHelper.findById(PigSisma.class, id);

        String nomeDocumentoSisma = getForm().getDocumentiCaricatiList().getTable().getRow(nr)
                .getString("nm_file_orig");
        PigSismaDocumenti documentoSisma = sismaHelper.getPigSismaDocumentiBySismaNmFileOrig(sisma, nomeDocumentoSisma);

        getResponse().setContentType("application/text");
        getResponse().setHeader(CONTENT_DISPOSITION, "attachment; filename=\"" + nomeDocumentoSisma + "_rv.txt");
        try ( // Ricavo lo stream di output
                BufferedOutputStream out = new BufferedOutputStream(getServletOutputStream())) {
            // Caccio il blobbo nel file xml
            byte[] report = documentoSisma.getBlReport().getBytes();
            if (report != null && report.length > 0) {
                InputStream is = new ByteArrayInputStream(report);
                byte[] data = new byte[1024];
                int count;
                while ((count = is.read(data, 0, 1024)) != -1) {
                    out.write(data, 0, count);
                }
            }
            out.flush();
            freeze();
        } catch (Exception e) {
            getMessageBox().addMessage(new Message(MessageLevel.ERR, "Errore nel recupero del report di verifica."));
            log.error("downloadVerificaReport(): ", e);
        }
    }

    @Override
    public void ricercaSisma() throws EMFError {
        popolaListaSisma();

        forwardToPublisher(Application.Publisher.SISMA);
    }

    @Override
    public void pulisciRicercaSisma() throws EMFError {
        getForm().getFiltriSisma().clear();
        getForm().getSismaList().getTable().clear();

        forwardToPublisher(Application.Publisher.SISMA);
    }

    private void popolaLineaDiFinanziamento(BigDecimal idVersatore) {
        getForm().getDatiGeneraliInput().getId_sisma_finanziamento()
                .setDecodeMap(DecodeMap.Factory.newInstance(sismaEjb.findPigSismaFinanziamentoByIdVersTB(idVersatore),
                        "id_sisma_finanziamento", "ds_tipo_finanziamento"));
    }

    // MAC26867
    private boolean isDatiAgenziaComplete() throws EMFError {

        String rag = getForm().getDatiAgenzia().getRegistro_ag().parse();
        String aag = getForm().getDatiAgenzia().getAnno_ag().parse();
        String nag = getForm().getDatiAgenzia().getNumero_ag().parse();
        Timestamp dag = getForm().getDatiAgenzia().getData_ag().parse();

        if (rag == null || rag.isEmpty()) {
            return false;
        }

        if (aag == null || aag.isEmpty()) {
            return false;
        }

        if (nag == null || nag.isEmpty()) {
            return false;
        }

        if (dag == null) {
            return false;
        }

        return true;
    }

    // MAC 30844
    private DatiAnagraficiDto inizializzaDatiGenerali(BigDecimal idVersatore, BigDecimal idSisma) {
        // I dati del versatore sono quelli del versatore dell'utente SA di qualsiasi tipo
        DatiAnagraficiDto dto = sismaEjb.getDatiVersatoreByIdVers(idVersatore, idSisma);
        getForm().getDatiGeneraliOutput().getTi_stato_out().setValue(PigSisma.TiStato.BOZZA.name());
        if (dto != null) {
            getForm().getDatiGeneraliOutput().getEnte_proprietario_out().setValue(dto.getEnteProprietario());
            getForm().getDatiGeneraliOutput().getNatura_ente_proprietario_out()
                    .setValue(dto.getNaturaEnteProprietario());
            getForm().getDatiGeneraliOutput().getSoggetto_attuatore_out().setValue(dto.getSoggettoAttuatore());
            getForm().getDatiGeneraliOutput().getNatura_soggetto_attuatore_out()
                    .setValue(dto.getNaturaSoggettoAttuatore());
            getForm().getDatiGeneraliOutput().getUbicazione_comune_out().setValue(dto.getUbicazioneComune());
            getForm().getDatiGeneraliOutput().getUbicazione_provincia_out().setValue(dto.getUbicazioneProvincia());
        }

        return dto;
    }
}
