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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;

import org.apache.commons.io.FileUtils;
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
import org.springframework.web.client.RestTemplate;

import it.eng.parer.objectstorage.exceptions.ObjectStorageException;
import it.eng.sacerasi.common.Constants;
import it.eng.sacerasi.entity.PigErrore;
import it.eng.sacerasi.entity.PigStrumUrbAtto;
import it.eng.sacerasi.entity.PigStrumUrbDocumenti;
import it.eng.sacerasi.entity.PigStrumentiUrbanistici;
import it.eng.sacerasi.job.verificaDocumentiSU.ejb.VerificaDocumentiSUEjb;
import it.eng.sacerasi.messages.MessaggiHelper;
import it.eng.sacerasi.slite.gen.Application;
import it.eng.sacerasi.slite.gen.action.StrumentiUrbanisticiAbstractAction;
import it.eng.sacerasi.strumentiUrbanistici.dto.RicercaStrumentiUrbanisticiDTO;
import it.eng.sacerasi.strumentiUrbanistici.ejb.StrumentiUrbanisticiEjb;
import it.eng.sacerasi.strumentiUrbanistici.ejb.StrumentiUrbanisticiEjb.DocUploadDto;
import it.eng.sacerasi.strumentiUrbanistici.ejb.StrumentiUrbanisticiEjb.SUDto;
import it.eng.sacerasi.strumentiUrbanistici.ejb.StrumentiUrbanisticiHelper;
import it.eng.sacerasi.util.DateUtil;
import it.eng.sacerasi.web.helper.ConfigurationHelper;
import it.eng.sacerasi.web.util.ComboGetter;
import it.eng.sacerasi.web.util.WebConstants;
import it.eng.spagoCore.error.EMFError;
import it.eng.spagoLite.actions.form.ListAction;
import it.eng.spagoLite.db.base.BaseTableInterface;
import it.eng.spagoLite.db.base.sorting.SortingRule;
import it.eng.spagoLite.db.decodemap.DecodeMapIF;
import it.eng.spagoLite.db.oracle.decode.DecodeMap;
import it.eng.spagoLite.form.base.BaseElements;
import it.eng.spagoLite.form.base.BaseElements.Status;
import it.eng.spagoLite.form.fields.Field;
import it.eng.spagoLite.form.fields.Fields;
import it.eng.spagoLite.message.Message;
import it.eng.spagoLite.message.MessageBox;
import it.eng.spagoLite.security.Secure;
import it.eng.spagoLite.security.SuppressLogging;

/**
 *
 * @author MIacolucci
 */
public class StrumentiUrbanisticiAction extends StrumentiUrbanisticiAbstractAction {

    private static final String CONTENT_DISPOSITION = "Content-Disposition";
    private static final String ID_STRUMENTI_URBANISTICI = "id_strumenti_urbanistici";
    private static final String VERIFICA_ATTIVATA = "verificaAttivata";

    private static final String STATO_BOZZA = "BOZZA";
    private static final String STATO_ERRORE = "ERRORE";
    private static final String STATO_VERSATO = "VERSATO";
    private static final String STATO_IN_ELABORAZIONE = "IN_ELABORAZIONE";
    private static final String STATO_IN_VERSAMENTO = "IN_VERSAMENTO";
    private static final String STATO_IN_TRASFORMAZIONE = "IN_TRASFORMAZIONE";
    private static final String STATO_RICHIESTA_INVIO = "RICHIESTA_INVIO";
    private static final String STATO_ANNULLATO = "ANNULLATO";

    private static final Logger log = LoggerFactory.getLogger(StrumentiUrbanisticiAction.class);

    @EJB(mappedName = "java:app/SacerAsync-ejb/StrumentiUrbanisticiEjb")
    private StrumentiUrbanisticiEjb strumentiUrbanisticiEjb;
    @EJB(mappedName = "java:app/SacerAsync-ejb/StrumentiUrbanisticiHelper")
    private StrumentiUrbanisticiHelper strumentiUrbanisticiHelper;
    @EJB(mappedName = "java:app/SacerAsync-ejb/ConfigurationHelper")
    private ConfigurationHelper configurationHelper;
    @EJB(mappedName = "java:app/SacerAsync-ejb/MessaggiHelper")
    private MessaggiHelper messaggiHelper;
    @EJB(mappedName = "java:app/SacerAsync-ejb/VerificaDocumentiSUEjb")
    private VerificaDocumentiSUEjb verificaDocumentiSUEjb;

    @Override
    public void initOnClick() throws EMFError {
        // Non utilizzato
    }

    @Override
    public void insertDettaglio() throws EMFError {
        // Non utilizzato
    }

    @Override
    public void update(Fields<Field> fields) throws EMFError {
        // Non utilizzato
    }

    @Override
    public void delete(Fields<Field> fields) throws EMFError {
        // Non utilizzato
    }

    @Override
    public void loadDettaglio() throws EMFError {
        if (getNavigationEvent().equals(ListAction.NE_DETTAGLIO_UPDATE)
                || getNavigationEvent().equals(ListAction.NE_DETTAGLIO_VIEW)
                || getNavigationEvent().equals(ListAction.NE_NEXT) || getNavigationEvent().equals(ListAction.NE_PREV)) {
            if (getTableName().equals(getForm().getStrumentiUrbanisticiList().getName())) {
                // ----
                // getForm().getStrumentiUrbanisticiList().setStatus(Status.update);
                // ----
                BigDecimal id = getForm().getStrumentiUrbanisticiList().getTable().getCurrentRow()
                        .getBigDecimal(ID_STRUMENTI_URBANISTICI);
                loadDettaglioStrumentoUrbanistico(id);
            }
        } else if (getNavigationEvent().equals(ListAction.NE_DETTAGLIO_INSERT)) {
            inizializzaWizard();
        }
    }

    private void loadDettaglioStrumentoUrbanistico(BigDecimal idStrumentoUrbanistico) throws EMFError {
        SUDto dto = strumentiUrbanisticiEjb.getSUById(idStrumentoUrbanistico);
        getForm().getDatiGeneraliOutput().getId_strumenti_urbanistici_out()
                .setValue(dto.getIdStrumentiUrbanistici() + "");
        getForm().getDatiGeneraliInput().getNm_tipo_strumento_urbanistico()
                .setValue(dto.getNmTipoStrumentoUrbanistico());
        getForm().getDatiGeneraliOutput().getNm_tipo_strumento_urbanistico_out()
                .setValue(dto.getNmTipoStrumentoUrbanistico());
        getForm().getDatiGeneraliInput().getTi_fase_strumento().setDecodeMap(DecodeMap.Factory.newInstance(
                strumentiUrbanisticiEjb.findPigStrumUrbPianoStatoByNomeTipoTB(dto.getNmTipoStrumentoUrbanistico()),
                "ti_fase_strumento", "ti_fase_strumento"));
        getForm().getDatiGeneraliOutput().getDt_creazione_out()
                .setValue(DateUtil.formatDateWithSlash(dto.getDtCreazione()));
        getForm().getDatiGeneraliInput().getTi_fase_strumento().setValue(dto.getTiFaseStrumento());
        getForm().getDatiGeneraliOutput().getTi_fase_strumento_out().setValue(dto.getTiFaseStrumento());
        getForm().getDatiGeneraliInput().getNumero().setValue(dto.getNumero());
        getForm().getDatiGeneraliOutput().getNumero_out().setValue(dto.getNumero());
        getForm().getDatiGeneraliInput().getAnno().setValue(dto.getAnno().longValueExact() + "");
        getForm().getDatiGeneraliOutput().getAnno_out().setValue(dto.getAnno().longValueExact() + "");
        getForm().getDatiGeneraliInput().getData().setValue(DateUtil.formatDateWithSlash(dto.getData()));
        // Se si è in dettaglio mette in sola lettura la data
        if (!getNavigationEvent().equals(ListAction.NE_DETTAGLIO_UPDATE)) {
            getForm().getDatiGeneraliInput().getData().setReadonly(true);
            getForm().getDatiGeneraliInput().getData().setRequired(false);
            getForm().getDatiGeneraliInput().getData().setDescription("Data strumento:");
        } else {
            getForm().getDatiGeneraliInput().getData().setReadonly(false);
            getForm().getDatiGeneraliInput().getData().setRequired(true);
        }
        getForm().getDatiGeneraliInput().getOggetto().setValue(dto.getOggetto());
        getForm().getDatiGeneraliInput().getDs_descrizione().setValue(dto.getDsDescrizione());
        getForm().getDatiGeneraliOutput().getCd_key_out().setValue(dto.getCdKey());
        String strStato = dto.getTiStato();
        if (strStato.equals(STATO_ERRORE)) {
            strStato += " - " + dto.getDsErr();
        }
        getForm().getDatiGeneraliOutput().getTi_stato_out().setValue(strStato);
        if (dto.getDtStato() != null) {
            getForm().getDatiGeneraliOutput().getDt_stato_out()
                    .setValue(DateUtil.formatDateWithSlashAndTime(dto.getDtStato()));
        }

        // Collegamenti
        // MEV 29495 - solo gli anni in cui esistono documenti collegabili
        if (!StringUtils.isEmpty(dto.getFaseCollegata1())) {
            DecodeMap dm = strumentiUrbanisticiHelper.getSUVersatiAnnoByPianoStato(dto.getNmTipoStrumentoUrbanistico(),
                    dto.getFaseCollegata1());
            getForm().getDatiGeneraliInput().getAnnoCollegato1().setDecodeMap(dm);
        } else {
            // MEV 29495 - riempio tutti i valori possibili per aggirare il comportamneto del framework,
            // i dati vengono poi manipolati dal js.
            DateTime oggi = new DateTime();
            DecodeMapIF dm = ComboGetter.getRangeAnni(2010, oggi.getYear());
            getForm().getDatiGeneraliInput().getAnnoCollegato1().setDecodeMap(dm);
        }

        if (!StringUtils.isEmpty(dto.getFaseCollegata2())) {
            DecodeMap dm = strumentiUrbanisticiHelper.getSUVersatiAnnoByPianoStato(dto.getNmTipoStrumentoUrbanistico(),
                    dto.getFaseCollegata1());
            getForm().getDatiGeneraliInput().getAnnoCollegato2().setDecodeMap(dm);
        } else {
            // MEV 29495 - riempio tutti i valori possibili per aggirare il comportamneto del framework,
            // i dati vengono poi manipolati dal js.
            DateTime oggi = new DateTime();
            DecodeMapIF dm = ComboGetter.getRangeAnni(2010, oggi.getYear());
            getForm().getDatiGeneraliInput().getAnnoCollegato2().setDecodeMap(dm);
        }

        if (dto.getAnnoCollegato1() != null) {
            getForm().getDatiGeneraliInput().getAnnoCollegato1()
                    .setValue(dto.getAnnoCollegato1().longValueExact() + "");
            getForm().getDatiGeneraliOutput().getAnnoCollegato1_out()
                    .setValue(dto.getAnnoCollegato1().longValueExact() + "");

        } else {
            getForm().getDatiGeneraliInput().getAnnoCollegato1().setValue("");
            getForm().getDatiGeneraliOutput().getAnnoCollegato1_out().setValue("");
        }

        if (dto.getAnnoCollegato2() != null) {
            getForm().getDatiGeneraliInput().getAnnoCollegato2()
                    .setValue(dto.getAnnoCollegato2().longValueExact() + "");
            getForm().getDatiGeneraliOutput().getAnnoCollegato2_out()
                    .setValue(dto.getAnnoCollegato2().longValueExact() + "");
        } else {
            getForm().getDatiGeneraliInput().getAnnoCollegato2().setValue("");
            getForm().getDatiGeneraliOutput().getAnnoCollegato2_out().setValue("");
        }

        getForm().getDatiGeneraliInput().getIdentificativoCollegato1().setValue(dto.getIdentificativoCollegato1());
        getForm().getDatiGeneraliOutput().getIdentificativoCollegato1_out().setValue(dto.getIdentificativoCollegato1());

        getForm().getDatiGeneraliInput().getIdentificativoCollegato2().setValue(dto.getIdentificativoCollegato2());
        getForm().getDatiGeneraliOutput().getIdentificativoCollegato2_out().setValue(dto.getIdentificativoCollegato2());

        getForm().getDatiGeneraliInput().getFaseCollegata1().setValue(dto.getFaseCollegata1());
        getForm().getDatiGeneraliOutput().getFaseCollegata1_out().setValue(dto.getFaseCollegata1());

        getForm().getDatiGeneraliInput().getFaseCollegata2().setValue(dto.getFaseCollegata2());
        getForm().getDatiGeneraliOutput().getFaseCollegata2_out().setValue(dto.getFaseCollegata2());

        // MEV 26936 - riempio la decodemap in modo da poter visualizzare il dato.
        if (dto.getAnnoCollegato1() != null && dto.getFaseCollegata1() != null) {
            DecodeMap ids = strumentiUrbanisticiEjb.findNumeriByVersAnnoTipoSUFaseSoloVersati(
                    getUser().getIdOrganizzazioneFoglia(), new BigDecimal(dto.getAnnoCollegato1().longValueExact()),
                    dto.getNmTipoStrumentoUrbanistico(), dto.getFaseCollegata1());

            getForm().getDatiGeneraliInput().getIdentificativoCollegato1().setDecodeMap(ids);
        } else {
            getForm().getDatiGeneraliInput().getIdentificativoCollegato1().setDecodeMap(new DecodeMap());
        }

        if (dto.getAnnoCollegato2() != null && dto.getFaseCollegata2() != null) {
            DecodeMap ids = strumentiUrbanisticiEjb.findNumeriByVersAnnoTipoSUFaseSoloVersati(
                    getUser().getIdOrganizzazioneFoglia(), new BigDecimal(dto.getAnnoCollegato2().longValueExact()),
                    dto.getNmTipoStrumentoUrbanistico(), dto.getFaseCollegata2());

            getForm().getDatiGeneraliInput().getIdentificativoCollegato2().setDecodeMap(ids);
        } else {
            getForm().getDatiGeneraliInput().getIdentificativoCollegato2().setDecodeMap(new DecodeMap());
        }

        // MEV 26936
        StrumentiUrbanisticiHelper.DatiAnagraficiDto datiAnagraficiDto = strumentiUrbanisticiHelper
                .getDatiAnagraficiByIdVers(getUser().getIdOrganizzazioneFoglia());
        DecodeMap pigStrumUrbAttoDecodeMap = strumentiUrbanisticiHelper
                .getPigStrumUrbAttoDecodeMap(datiAnagraficiDto.getTipologia());
        getForm().getDatiGeneraliInput().getTi_atto().setDecodeMap(pigStrumUrbAttoDecodeMap);
        getForm().getDatiGeneraliInput().getTi_atto().setValue(dto.getTiAtto().getIdAtto().toString());
        getForm().getDatiGeneraliOutput().getTi_atto_out().setValue(dto.getTiAtto().getDsDescrizione());

        // In caso di click sulla lente del dettaglio deve anche caricare i dati dinamici uguali all'ultima
        // pagina del wizard
        if (getNavigationEvent().equals(ListAction.NE_DETTAGLIO_VIEW) || getNavigationEvent().equals(ListAction.NE_NEXT)
                || getNavigationEvent().equals(ListAction.NE_PREV)) {

            // MEV26936
            loadDatiAggiuntivi();

            if (dto.getTiStato().equals(STATO_ANNULLATO)) {
                getForm().getDettaglioButtonList().getRiportaInBozza().setEditMode();
            } else {
                getForm().getDettaglioButtonList().getRiportaInBozza().setViewMode();
            }
            // MEV#24085 - Strumenti urbanistici - rendere modificabile il campo "Descrizione" anche dopo il versamento
            if (dto.getTiStato().equals(STATO_VERSATO)) {
                // Non fa sparire il pulsante modifica/salva
            } else if (!dto.getTiStato().equals(STATO_BOZZA)) {
                getRequest().setAttribute("nascondiUpdate", "true");
            }
        } else {
            getForm().getRiepilogoButtonList().getVersaSU().setEditMode();
        }
        getForm().getRiepilogoButtonList().setEditMode();

        getForm().getRiepilogoButtonList().getVersaSU().setEditMode();
        // Condizioni di invio per mostrare il bottone "Versamento"
        if (strumentiUrbanisticiEjb.existsCondizioniInvio(idStrumentoUrbanistico)) {
            // MAC#23771 - Pulsante versamento presente anche su Strumenti urbanistici già versati
            // Il pulsante versamento appariva anche se il SU era in stato VERSATO
            String statoRiga = getForm().getStrumentiUrbanisticiList().getTable().getCurrentRow().getString("ti_stato");
            if (statoRiga.equals(STATO_RICHIESTA_INVIO)) {
                getForm().getRiepilogoButtonList().getVersaSU().setReadonly(true);
            } else {
                // MAC#24782 - Strumenti urbanistici: disabilitazione pulsante versamento
                // MAC 31090 - aggiunti i nuovi stati.
                if (statoRiga.equals(STATO_VERSATO) || statoRiga.equals(STATO_BOZZA)
                        || statoRiga.equals(STATO_IN_ELABORAZIONE) || statoRiga.equals(STATO_IN_VERSAMENTO)
                        || statoRiga.equals(STATO_IN_TRASFORMAZIONE) || statoRiga.equals(STATO_ERRORE)) {
                    getForm().getRiepilogoButtonList().getVersaSU().setViewMode();
                } else {
                    getForm().getRiepilogoButtonList().getVersaSU().setReadonly(false);
                }
            }
        } else {
            getForm().getRiepilogoButtonList().getVersaSU().setReadonly(true);
        }

        getForm().getDettaglioButtonList().getRecuperaErrori().setEditMode();
        // Condizioni per mostrare il bottone "Errore"
        // MAC#25781 - Correggere la visualizzazione del pulsante recupera errori su strumenti urbanistici
        if (dto.getTiStato().equals(STATO_ERRORE)) {
            getForm().getDettaglioButtonList().getRecuperaErrori().setEditMode();
        } else {
            getForm().getDettaglioButtonList().getRecuperaErrori().setViewMode();
        }
    }

    @Override
    public void undoDettaglio() throws EMFError {
        // MEV#24085 - Strumenti urbanistici - rendere modificabile il campo "Descrizione" anche dopo il versamento
        String stato = getForm().getStrumentiUrbanisticiList().getTable().getCurrentRow().getString("ti_stato");
        if (stato != null && stato.equals(STATO_VERSATO)) {
            getForm().getDatiGeneraliOutput().getDs_descrizione_out().setViewMode();
            getForm().getDatiGeneraliOutput().getDs_descrizione_out().setReadonly(true);
            getForm().getDatiGeneraliInput().getData().setViewMode();
            getForm().getStrumentiUrbanisticiList().setStatus(Status.view);
        }
    }

    @Override
    public void saveDettaglio() throws EMFError {
        // MEV#24085 - Strumenti urbanistici - rendere modificabile il campo "Descrizione" anche dopo il versamento
        String stato = getForm().getStrumentiUrbanisticiList().getTable().getCurrentRow().getString("ti_stato");
        if (stato != null && stato.equals(STATO_VERSATO)) {
            getForm().getDatiGeneraliOutput().post(getRequest());
            BigDecimal idStrumento = getForm().getStrumentiUrbanisticiList().getTable().getCurrentRow()
                    .getBigDecimal(ID_STRUMENTI_URBANISTICI);
            String descrizione = getForm().getDatiGeneraliOutput().getDs_descrizione_out().getValue();
            strumentiUrbanisticiEjb.salvaDescrizioneStrumento(idStrumento, descrizione);
            getForm().getStrumentiUrbanisticiList().setStatus(Status.view);
            getForm().getDatiGeneraliOutput().getDs_descrizione_out().setViewMode();
            getForm().getDatiGeneraliOutput().getDs_descrizione_out().setReadonly(true);
            getMessageBox().addInfo("Descrizione aggiornata con successo.");
            getMessageBox().setViewMode(MessageBox.ViewMode.plain);
        }
        forwardToPublisher(Application.Publisher.DETTAGLIO_STRUMENTI_URBANISTICI);
    }

    @Override
    public void dettaglioOnClick() throws EMFError {
        String lista = getTableName();
        String action = getRequest().getParameter("navigationEvent");

        if (action != null && getForm().getStrumentiUrbanisticiList().getName().equals(lista)) {
            if (action.equals(NE_DETTAGLIO_INSERT)) {
                getForm().getDatiGeneraliInput().setStatus(Status.insert);
                forwardToPublisher(Application.Publisher.STRUMENTI_URBANISTICI_WIZARD);
            } else if (action.equals(NE_DETTAGLIO_VIEW)) {
                getForm().getDatiGeneraliOutput().getDs_descrizione_out().setViewMode();
                getForm().getDatiGeneraliOutput().getDs_descrizione_out().setReadonly(true);
                getForm().getDatiGeneraliInput().getData().setViewMode();
                forwardToPublisher(Application.Publisher.DETTAGLIO_STRUMENTI_URBANISTICI);
            } else {
                getForm().getDatiGeneraliInput().setStatus(Status.update);
                forwardToPublisher(Application.Publisher.STRUMENTI_URBANISTICI_WIZARD);
            }
        } else if (getNavigationEvent().equals(ListAction.NE_DETTAGLIO_UPDATE)) {
            // MEV#24085 - Strumenti urbanistici - rendere modificabile il campo "Descrizione" anche dopo il versamento
            String stato = getForm().getStrumentiUrbanisticiList().getTable().getCurrentRow().getString("ti_stato");
            if (stato != null && stato.equals(STATO_VERSATO)) {
                getForm().getStrumentiUrbanisticiList().setStatus(Status.update);
                getForm().getDatiGeneraliOutput().getDs_descrizione_out().setEditMode();
                getForm().getDatiGeneraliOutput().getDs_descrizione_out().setReadonly(false);
                getForm().getDatiGeneraliInput().getData().setViewMode();
                forwardToPublisher(Application.Publisher.DETTAGLIO_STRUMENTI_URBANISTICI);
            } else {
                getForm().getDatiGeneraliInput().setStatus(Status.update);
                forwardToPublisher(Application.Publisher.STRUMENTI_URBANISTICI_WIZARD);
            }
        }
    }

    @Override
    public void elencoOnClick() throws EMFError {
        goBack();
    }

    @Override
    protected String getDefaultPublsherName() {
        return Application.Publisher.STRUMENTI_URBANISTICI;
    }

    @Override
    public void reloadAfterGoBack(String publisherName) {
        if (publisherName.equals(Application.Publisher.STRUMENTI_URBANISTICI)) {
            int paginaCorrente = getForm().getStrumentiUrbanisticiList().getTable().getCurrentPageIndex();
            int inizio = getForm().getStrumentiUrbanisticiList().getTable().getFirstRowPageIndex();

            try {
                // MEV26278
                popolaStrumentiUrbanisticiList();

                // Rieseguo la query se necessario
                this.lazyLoadGoPage(getForm().getStrumentiUrbanisticiList(), paginaCorrente);
                // Ritorno alla pagina
                getForm().getStrumentiUrbanisticiList().getTable().setCurrentRowIndex(inizio);
            } catch (EMFError ex) {
                log.error(ex.getDescription(), ex);
            }
        }
    }

    @Override
    public String getControllerName() {
        return Application.Actions.STRUMENTI_URBANISTICI;
    }

    @Secure(action = "Menu.StrumentiUrbanistici.VersamentiStrumentiUrbanistici")
    public void loadStrumentiUrbanistici() throws EMFError {
        getUser().getMenu().reset();
        getUser().getMenu().select("Menu.StrumentiUrbanistici.VersamentiStrumentiUrbanistici");
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
            inizializzaWizard();

            // MEV26278 - attivo i campi di ricerca
            getForm().getFiltriStrumentiUrbanistici().setEditMode();
            popolaFiltriRicercaStrumentiUrbanistici();

            popolaStrumentiUrbanisticiList();

            // Popola i dati generali
            StrumentiUrbanisticiHelper.DatiAnagraficiDto datiAnagraficiDto = strumentiUrbanisticiHelper
                    .getDatiAnagraficiByIdVers(getUser().getIdOrganizzazioneFoglia());
            getForm().getDatiGeneraliInput().getNm_tipo_strumento_urbanistico().setDecodeMap(
                    DecodeMap.Factory.newInstance(strumentiUrbanisticiEjb.findTipiStrumentiUrbanisticiTB(),
                            "nm_tipo_strumento_urbanistico", "nm_tipo_strumento_urbanistico"));
            getForm().getDatiGeneraliInput().setEditMode();
            getForm().getDatiGeneraliInput().getSalvaBozza().setEditMode();
            getForm().getDatiGeneraliInput().getTi_fase_strumento().setDecodeMap(new DecodeMap());

            DateTime oggi = new DateTime();
            DecodeMapIF dm = ComboGetter.getRangeAnni(2010, oggi.getYear());
            getForm().getDatiGeneraliInput().getAnno().setDecodeMap(dm);

            // MEV 26936
            DecodeMap pigStrumUrbAttoDecodeMap = strumentiUrbanisticiHelper
                    .getPigStrumUrbAttoDecodeMap(datiAnagraficiDto.getTipologia());
            getForm().getDatiGeneraliInput().getTi_atto().setDecodeMap(pigStrumUrbAttoDecodeMap);
            //
            getForm().getStrumentiUrbanisticiButtonList().setEditMode();
        } else {
            // L'utente non può operare con gli strumetni urbanistici!
            getMessageBox().addMessage(new Message(Message.MessageLevel.INF,
                    "La funzione Strumenti urbanistici non può essere utilizzata per assenza dell'ObjectStorage nell'ambiente."));

        }
        forwardToPublisher(Application.Publisher.STRUMENTI_URBANISTICI);
    }

    private void inizializzaWizard() {
        getForm().getDatiGeneraliOutput().clear();
        getForm().getDatiGeneraliInput().clear();
        // rimette readonly false e required a true perché potrebbero essere stati reimpostati dal dettaglio
        getForm().getDatiGeneraliInput().getData().setReadonly(false);
        getForm().getDatiGeneraliInput().getData().setRequired(true);
        getForm().getInserimentoWizard().reset();
        StrumentiUrbanisticiHelper.DatiAnagraficiDto dto = strumentiUrbanisticiEjb
                .getDatiVersatoreByIdVers(getUser().getIdOrganizzazioneFoglia());
        getForm().getDatiGeneraliOutput().getNm_provincia_out().setValue((dto == null ? "" : dto.getProvincia()));
        getForm().getDatiGeneraliOutput().getTi_stato_out().setValue(STATO_BOZZA);
        getForm().getDatiGeneraliOutput().getNm_ente_out().setValue((dto == null ? "" : dto.getDenominazione()));
        getForm().getDatiGeneraliOutput().getNm_unione_out().setValue((dto == null ? "" : dto.getUnione()));

        // MAC 29947 - riempio tutti i valori possibili per aggirare il comportamneto del framework,
        // i dati vengono poi manipolati dal js.
        DateTime oggi = new DateTime();
        DecodeMapIF dm = ComboGetter.getRangeAnni(2010, oggi.getYear());
        getForm().getDatiGeneraliInput().getAnnoCollegato1().setDecodeMap(dm);
        oggi = new DateTime();
        dm = ComboGetter.getRangeAnni(2010, oggi.getYear());
        getForm().getDatiGeneraliInput().getAnnoCollegato2().setDecodeMap(dm);
    }

    @Override
    public boolean inserimentoWizardOnSave() throws EMFError {
        return true;
    }

    @Override
    public void inserimentoWizardOnCancel() throws EMFError {
        loadStrumentiUrbanistici();
    }

    @Override
    public String getDefaultInserimentoWizardPublisher() throws EMFError {
        return Application.Publisher.STRUMENTI_URBANISTICI_WIZARD;
    }

    @Override
    public void inserimentoWizardStrumentoUrbanisticoOnEnter() throws EMFError {
        getForm().getDatiGeneraliInput().getModificato().setValue("N");
    }

    @Override
    public boolean inserimentoWizardStrumentoUrbanisticoOnExit() throws EMFError {
        try {
            return salvaStep1();
        } catch (ObjectStorageException e) {
            return false;
        }
    }

    @Override
    public void inserimentoWizardUploadDocumentiOnEnter() throws EMFError {
        List<DocUploadDto> l = strumentiUrbanisticiEjb.findPigVSuLisDocsPianoByTipoStrumentoFase(
                getForm().getDatiGeneraliOutput().getId_strumenti_urbanistici_out().parse(),
                getForm().getDatiGeneraliOutput().getNm_tipo_strumento_urbanistico_out().parse(),
                getForm().getDatiGeneraliOutput().getTi_fase_strumento_out().parse());
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
    }

    @Override
    public boolean inserimentoWizardUploadDocumentiOnExit() throws EMFError {
        return true;
    }

    @Override
    public void inserimentoWizardRiepilogoOnEnter() throws EMFError {
        getRequest().setAttribute("terzoStep", true);
        getSession().removeAttribute(VERIFICA_ATTIVATA);

        // MEV26936
        loadDatiAggiuntivi();

        getForm().getDatiGeneraliOutput().getFaseCollegata1_out()
                .setValue(getForm().getDatiGeneraliInput().getFaseCollegata1().getValue());
        getForm().getDatiGeneraliOutput().getFaseCollegata2_out()
                .setValue(getForm().getDatiGeneraliInput().getFaseCollegata2().getValue());
        getForm().getDatiGeneraliOutput().getAnnoCollegato1_out()
                .setValue(getForm().getDatiGeneraliInput().getAnnoCollegato1().getValue());
        getForm().getDatiGeneraliOutput().getAnnoCollegato2_out()
                .setValue(getForm().getDatiGeneraliInput().getAnnoCollegato2().getValue());
        // MEV 26936
        getForm().getDatiGeneraliOutput().getIdentificativoCollegato1_out()
                .setValue(getForm().getDatiGeneraliInput().getIdentificativoCollegato1().getValue());
        getForm().getDatiGeneraliOutput().getIdentificativoCollegato2_out()
                .setValue(getForm().getDatiGeneraliInput().getIdentificativoCollegato2().getValue());
    }

    public void reloadThirdStepStrumentoUrbanistico() throws EMFError {
        inserimentoWizardRiepilogoOnEnter();
        Object[] ogg = strumentiUrbanisticiEjb.findDocumentiCaricatiPerIdSUTB(
                getForm().getDatiGeneraliOutput().getId_strumenti_urbanistici_out().parse());
        // Se anche la verifica è andata bene ma gli obbligatori non sono stati inseriti tutti è tutto disabilitato
        if (!((List<?>) ogg[2]).isEmpty()) {
            getForm().getRiepilogoButtonList().getVersaSU().setReadonly(true);
            getForm().getRiepilogoButtonList().getVerificaDocumentiSU().setReadonly(true);
        }
        // Se uno degli obbligatori è in errore sia Verifica che Versamento sono disabilitati dopo la verifica
        if (!((List<?>) ogg[3]).isEmpty()) {
            getForm().getRiepilogoButtonList().getVersaSU().setReadonly(true);
            getForm().getRiepilogoButtonList().getVerificaDocumentiSU().setReadonly(true);
        }
        forwardToPublisher(Application.Publisher.STRUMENTI_URBANISTICI_WIZARD);
    }

    @Override
    public boolean inserimentoWizardRiepilogoOnExit() throws EMFError {
        return true;
    }

    @Override
    public void salvaBozza() throws EMFError {
        try {
            if (salvaStep1()) {
                getForm().getDatiGeneraliInput().getModificato().setValue("N");
            }
            forwardToPublisher(getLastPublisher());
        } catch (ObjectStorageException e) {

        }
    }

    // MEV26936 logica comune a terzo step e dettaglio strumenti urbanistici.
    private void loadDatiAggiuntivi() throws EMFError {
        Object[] ogg = strumentiUrbanisticiEjb.findDocumentiCaricatiPerIdSUTB(
                getForm().getDatiGeneraliOutput().getId_strumenti_urbanistici_out().parse());
        BaseTableInterface<?> tb = (BaseTableInterface<?>) ogg[0];
        tb.addSortingRule("fl_obbligatorio", SortingRule.DESC);
        tb.sort();
        getForm().getDocumentiCaricatiList().setTable(tb);
        getForm().getDocumentiCaricatiList().getTable().first();
        getForm().getDocumentiCaricatiList().setStatus(BaseElements.Status.view);

        getForm().getDocumentiCaricatiList().getDownload_bl_report().setEditMode();

        getRequest().setAttribute("strDocFacolativi", ogg[1]);
        getRequest().setAttribute("alObbNonCaricati", ogg[2]);
        getRequest().setAttribute("alDocInErrore", ogg[3]);
        getRequest().setAttribute("statoSU", getForm().getDatiGeneraliOutput().getTi_stato_out().parse());

        getForm().getDatiGeneraliOutput().getOggetto_out()
                .setValue(getForm().getDatiGeneraliInput().getOggetto().getValue());
        getForm().getDatiGeneraliOutput().getDs_descrizione_out()
                .setValue(getForm().getDatiGeneraliInput().getDs_descrizione().getValue());
        getForm().getRiepilogoButtonList().getVersaSU().setReadonly(true);
        getForm().getRiepilogoButtonList().getVersaSU().setEditMode();
        getForm().getRiepilogoButtonList().getVerificaDocumentiSU().setReadonly(true);
        getForm().getRiepilogoButtonList().getVerificaDocumentiSU().setEditMode();
        if (verificaDocumentiSUEjb
                .verificaInCorso(getForm().getDatiGeneraliOutput().getId_strumenti_urbanistici_out().parse())) {
            getForm().getRiepilogoButtonList().getVersaSU().setReadonly(true);
            getForm().getRiepilogoButtonList().getVerificaDocumentiSU().setReadonly(true);
            getMessageBox().addWarning("Attenzione: verifica documenti in corso");
        } else if (strumentiUrbanisticiEjb.existsPigStrumUrbDocumenti(
                getForm().getDatiGeneraliOutput().getId_strumenti_urbanistici_out().parse())) {
            getForm().getRiepilogoButtonList().getVersaSU().setReadonly(true);
            getForm().getRiepilogoButtonList().getVerificaDocumentiSU().setReadonly(false);
        } else if (verificaDocumentiSUEjb.existsDocumentiDaVerificarePerStrumentoUrbanisticoByVista(
                getForm().getDatiGeneraliOutput().getId_strumenti_urbanistici_out().parse())) {
            getForm().getRiepilogoButtonList().getVerificaDocumentiSU().setReadonly(false);
            getForm().getRiepilogoButtonList().getVersaSU().setReadonly(true);
        } else {
            getForm().getRiepilogoButtonList().getVersaSU().setReadonly(true);
            getForm().getRiepilogoButtonList().getVerificaDocumentiSU().setReadonly(true);
        }

        // Condizioni di invio per mostrare il bottone "Versamento"
        if (getForm().getDatiGeneraliOutput().getId_strumenti_urbanistici_out() != null) {
            BigDecimal idSu = getForm().getDatiGeneraliOutput().getId_strumenti_urbanistici_out().parse();
            StrumentiUrbanisticiEjb.NavigazioneStrumDto dto = strumentiUrbanisticiEjb.getDatiNavigazionePerSU(idSu);
            if (dto.isFileMancante() == false && dto.isVerificaErrata() == false && dto.isVerificaInCorso() == false) {
                getForm().getRiepilogoButtonList().getVersaSU().setReadonly(false);
                getForm().getRiepilogoButtonList().getVerificaDocumentiSU().setReadonly(true);
            }
        }
    }

    private boolean controlloCoerenzaAnniCollegati() {
        boolean retCode = true;
        String annoCollegato1 = getForm().getDatiGeneraliInput().getAnnoCollegato1().getValue();
        String identificativoCollegato1 = getForm().getDatiGeneraliInput().getIdentificativoCollegato1().getValue();
        String annoCollegato2 = getForm().getDatiGeneraliInput().getAnnoCollegato2().getValue();
        String identificativoCollegato2 = getForm().getDatiGeneraliInput().getIdentificativoCollegato2().getValue();
        if (annoCollegato1 != null && !annoCollegato1.equals("")) {
            if (identificativoCollegato1 == null || identificativoCollegato1.equals("")) {
                getMessageBox().addWarning("Inserire l'identificativo per la fase collegata "
                        + getForm().getDatiGeneraliInput().getFaseCollegata1().getValue());
                retCode = false;
            }
        } else {
            if (identificativoCollegato1 != null && !identificativoCollegato1.equals("")) {
                getMessageBox().addWarning("Inserire l'anno per la fase collegata "
                        + getForm().getDatiGeneraliInput().getFaseCollegata1().getValue());
                retCode = false;
            }
        }
        if (annoCollegato2 != null && !annoCollegato2.equals("")) {
            if (identificativoCollegato2 == null || identificativoCollegato2.equals("")) {
                getMessageBox().addWarning("Inserire l'identificativo per la fase collegata "
                        + getForm().getDatiGeneraliInput().getFaseCollegata2().getValue());
                retCode = false;
            }
        } else {
            if (identificativoCollegato2 != null && !identificativoCollegato2.equals("")) {
                getMessageBox().addWarning("Inserire l'anno per la fase collegata "
                        + getForm().getDatiGeneraliInput().getFaseCollegata2().getValue());
                retCode = false;
            }
        }

        // MEV #20382 Controllo date collegamenti
        // RIMOSSO da MEV29936
        return retCode;
    }

    private boolean salvaStep1() throws EMFError, ObjectStorageException {
        if (getForm().getDatiGeneraliOutput().getTi_stato_out().getValue() != null
                && (getForm().getDatiGeneraliOutput().getTi_stato_out().getValue().equals(STATO_BOZZA)
                        || getForm().getDatiGeneraliOutput().getTi_stato_out().getValue().equals(STATO_ERRORE))) {
            getMessageBox().clear();
            if (getForm().getDatiGeneraliInput().postAndValidate(getRequest(), getMessageBox())) {

                // MEV 27970
                String numero = getForm().getDatiGeneraliInput().getNumero().parse();
                if (!StringUtils.isNumeric(numero)) {
                    getMessageBox().addWarning("Nel campo numero è possibile inserire solo valori numerici.");
                    return false;
                }

                // Fa tutto solo se l'utente ha modificato qualcosa nella form!
                if (getForm().getDatiGeneraliInput().getModificato() != null
                        && getForm().getDatiGeneraliInput().getModificato().getValue().equals("S")) {
                    // Controllo coerenza collegamenti
                    if (!controlloCoerenzaAnniCollegati()) {
                        return false;
                    }
                    SUDto dto = new SUDto();
                    dto.setIdVers(getUser().getIdOrganizzazioneFoglia());
                    dto.setIdUserIam(getUser().getIdUtente());
                    dto.setAnno(new BigDecimal(getForm().getDatiGeneraliInput().getAnno().parse()));

                    dto.setNumero(numero);
                    dto.setDsDescrizione(getForm().getDatiGeneraliInput().getDs_descrizione().parse());
                    dto.setNmTipoStrumentoUrbanistico(
                            getForm().getDatiGeneraliInput().getNm_tipo_strumento_urbanistico().parse());
                    dto.setTiFaseStrumento(getForm().getDatiGeneraliInput().getTi_fase_strumento().parse());
                    dto.setData(getForm().getDatiGeneraliInput().getData().parse());

                    // MEV 26936
                    PigStrumUrbAtto pigStrumUrbAtto = strumentiUrbanisticiHelper.findById(PigStrumUrbAtto.class,
                            getForm().getDatiGeneraliInput().getTi_atto().parse());
                    dto.setTiAtto(pigStrumUrbAtto);
                    getForm().getDatiGeneraliOutput().getTi_fase_strumento_out()
                            .setValue(getForm().getDatiGeneraliInput().getTi_fase_strumento().parse());

                    boolean isInserimento = getForm().getDatiGeneraliInput().getStatus().equals(Status.insert);

                    // Dati collegamenti...
                    String annoCollegato1 = getForm().getDatiGeneraliInput().getAnnoCollegato1().getValue();
                    if (annoCollegato1 != null && !annoCollegato1.equals("")) {
                        dto.setAnnoCollegato1(new BigDecimal(annoCollegato1));
                    }
                    String annoCollegato2 = getForm().getDatiGeneraliInput().getAnnoCollegato2().getValue();
                    if (annoCollegato2 != null && !annoCollegato2.equals("")) {
                        dto.setAnnoCollegato2(new BigDecimal(annoCollegato2));
                    }
                    String identificativoCollegato2 = getForm().getDatiGeneraliInput().getIdentificativoCollegato2()
                            .getValue();
                    if (identificativoCollegato2 != null && !identificativoCollegato2.equals("")) {
                        dto.setIdentificativoCollegato2(identificativoCollegato2);
                    }
                    String identificativoCollegato1 = getForm().getDatiGeneraliInput().getIdentificativoCollegato1()
                            .getValue();
                    if (identificativoCollegato1 != null && !identificativoCollegato1.equals("")) {
                        dto.setIdentificativoCollegato1(identificativoCollegato1);
                    }
                    String faseCollegata1 = getForm().getDatiGeneraliInput().getFaseCollegata1().getValue();
                    if (faseCollegata1 != null && !faseCollegata1.equals("")) {
                        dto.setFaseCollegata1(faseCollegata1);
                    }
                    String faseCollegata2 = getForm().getDatiGeneraliInput().getFaseCollegata2().getValue();
                    if (faseCollegata2 != null && !faseCollegata2.equals("")) {
                        dto.setFaseCollegata2(faseCollegata2);
                    }

                    if (isInserimento) {
                        dto = strumentiUrbanisticiEjb.inserisciStrumentoUrbanistico(dto);
                    } else {
                        dto.setIdStrumentiUrbanistici(getForm().getDatiGeneraliOutput()
                                .getId_strumenti_urbanistici_out().parse().longValueExact());
                        dto = strumentiUrbanisticiEjb.modificaStrumentoUrbanistico(dto);
                    }

                    if (dto.existsWarnMessages()) {
                        getMessageBox().addWarning(dto.getWarnMessage());
                        return false;
                    } else {
                        getForm().getDatiGeneraliOutput().getCd_key_out().setValue(dto.getCdKey());
                        getForm().getDatiGeneraliOutput().getId_strumenti_urbanistici_out()
                                .setValue(Long.toString(dto.getIdStrumentiUrbanistici()));
                        getForm().getDatiGeneraliOutput().getTi_stato_out().setValue(dto.getTiStato());
                        getForm().getDatiGeneraliOutput().getCd_key_out().setValue(dto.getCdKey());
                        getForm().getDatiGeneraliInput().getOggetto().setValue(dto.getOggetto());
                        getForm().getDatiGeneraliInput().getDs_descrizione().setValue(dto.getDsDescrizione());
                        getForm().getDatiGeneraliOutput().getDt_stato_out()
                                .setValue(DateUtil.formatDateWithSlashAndTime(dto.getDtStato()));
                        getForm().getDatiGeneraliOutput().getDt_creazione_out()
                                .setValue(DateUtil.formatDateWithSlashAndTime(dto.getDtCreazione()));

                        // MEV 26936
                        getForm().getDatiGeneraliOutput().getNumero_out().setValue(dto.getNumero());
                        getForm().getDatiGeneraliOutput().getAnno_out().setValue(dto.getAnno().longValueExact() + "");
                        getForm().getDatiGeneraliOutput().getTi_atto_out().setValue(dto.getTiAtto().getDsDescrizione());

                        getMessageBox().addInfo("Strumento urbanistico " + (isInserimento ? "inserito" : "modificato")
                                + " con successo");
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
    public void deleteStrumentiUrbanisticiList() throws EMFError {
        int riga = getForm().getStrumentiUrbanisticiList().getTable().getCurrentRowIndex();
        BigDecimal idStrumentiUrbanistici = getForm().getStrumentiUrbanisticiList().getTable().getCurrentRow()
                .getBigDecimal(ID_STRUMENTI_URBANISTICI);
        SUDto dto = strumentiUrbanisticiEjb.getSUById(idStrumentiUrbanistici);
        if (dto.getTiStato().equals(PigStrumentiUrbanistici.TiStato.BOZZA.name())) {
            try {
                strumentiUrbanisticiEjb.cancellaSU(idStrumentiUrbanistici);
            } catch (ObjectStorageException e) {

            }
            getForm().getStrumentiUrbanisticiList().getTable().remove(riga);
            getMessageBox().addInfo("Strumento urbanistico cancellato con successo");
        } else {
            PigErrore err = messaggiHelper.retrievePigErrore("PING_ERRSU23");
            getMessageBox().addWarning(err.getDsErrore());
        }
        forwardToPublisher(getLastPublisher());
    }

    public void rimuoviFile() throws EMFError, ObjectStorageException {
        JSONObject result = new JSONObject();
        try {
            String nmFileOrig = getRequest().getParameter("nmFileOrig");
            BigDecimal idStrumentiUrbanistici = getForm().getDatiGeneraliOutput().getId_strumenti_urbanistici_out()
                    .parse();
            String mex = strumentiUrbanisticiEjb.cancellaDoc(idStrumentiUrbanistici, nmFileOrig);
            if (mex == null) {
                result.put("codice", "0");
            } else {
                result.put("codice", -1);
                result.put("messaggio", mex);
            }
        } catch (JSONException ex) {
            //
        }
        redirectToAjax(result);
    }

    public void tipoStrumentoModificatoJson() throws EMFError {
        DecodeMap mappa = null;
        String nmTipoStrumento = getRequest().getParameter("nmTipoStrumento");
        getForm().getDatiGeneraliOutput().getNm_tipo_strumento_urbanistico_out().setValue(nmTipoStrumento);
        getForm().getDatiGeneraliOutput().getTi_fase_strumento_out().clear();
        mappa = DecodeMap.Factory.newInstance(
                strumentiUrbanisticiEjb.findPigStrumUrbPianoStatoByNomeTipoTB(nmTipoStrumento), "ti_fase_strumento",
                "ti_fase_strumento");
        getForm().getDatiGeneraliInput().getTi_fase_strumento().setDecodeMap(mappa);
        // Rimuovere tutti i collegamenti!
        getForm().getDatiGeneraliInput().getAnnoCollegato1().clear();
        getForm().getDatiGeneraliInput().getIdentificativoCollegato1().clear();
        getForm().getDatiGeneraliInput().getFaseCollegata1().clear();
        getForm().getDatiGeneraliInput().getAnnoCollegato2().clear();
        getForm().getDatiGeneraliInput().getIdentificativoCollegato2().clear();
        getForm().getDatiGeneraliInput().getFaseCollegata2().clear();
        redirectToAjax(mappa.asJSON());
    }

    public void faseCollagataModificataJson() {
        String nmTipoStrumento = getRequest().getParameter("nmTipoStrumento");
        String nmFaseCollegata = getRequest().getParameter("nmFaseCollegata");

        DecodeMap anni = strumentiUrbanisticiHelper.getSUVersatiAnnoByPianoStato(nmTipoStrumento, nmFaseCollegata);

        redirectToAjax(anni.asJSON());
    }

    public void getErroriJson() throws EMFError, JSONException {
        String codErroreLike = getRequest().getParameter("codErroreLike");
        // Torna un JSON con gli errori di PING-ERRSU%
        JSONObject json = strumentiUrbanisticiEjb.retrievePigErroreLikeAsJsonString(codErroreLike);
        redirectToAjax(json);
    }

    @Override
    public void versaSU() throws Throwable {
        if (strumentiUrbanisticiEjb.versaStrumentoUrbanistico(
                getForm().getDatiGeneraliOutput().getId_strumenti_urbanistici_out().parse(),
                getUser().getIdUtente()) == false) {
            PigErrore err = messaggiHelper.retrievePigErrore("PING-ERRSU23");
            getMessageBox().addWarning(err.getDsErrore());
            forwardToPublisher(getLastPublisher());
        } else {
            loadStrumentiUrbanistici();
            forwardToPublisher(Application.Publisher.STRUMENTI_URBANISTICI);
        }
    }

    public void downloadSUOperation() throws EMFError {
        String riga = getRequest().getParameter("riga");
        Integer nr = Integer.parseInt(riga);

        BigDecimal idStrumentiUrbanistici = getForm().getStrumentiUrbanisticiList().getTable().getRow(nr)
                .getBigDecimal(ID_STRUMENTI_URBANISTICI);
        SUDto dto = strumentiUrbanisticiEjb.getSUById(idStrumentiUrbanistici);
        if (dto.getTiStato().equals(PigStrumentiUrbanistici.TiStato.VERSATO.name())) {
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
            RestTemplate restTemplate = new RestTemplate(clientHttpRequestFactory);
            try {
                // Croo l'header della richiesta
                HttpHeaders header = new HttpHeaders();
                header.setContentType(MediaType.MULTIPART_FORM_DATA);
                // Creo i parametri della richiesta
                MultiValueMap<String, Object> multipartRequest = new LinkedMultiValueMap<>();
                multipartRequest.add("VERSIONE", versione);
                multipartRequest.add("LOGINNAME", loginname);
                multipartRequest.add("PASSWORD", password);
                multipartRequest.add("XMLSIP",
                        strumentiUrbanisticiEjb.getXmlRichiestaRappVersByIdStrumUrb(idStrumentiUrbanistici));
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
                // Li setto nella response che utilizzerò per il ServletOutputStream
                getResponse().setContentType(contentType.toString());
                getResponse().setHeader(CONTENT_DISPOSITION, fileName);
                if (contentType.getSubtype().equals("zip")) {
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
            } finally {
                freeze();
            }
        } else {
            PigErrore err = messaggiHelper.retrievePigErrore("PING-ERRSU25");
            getMessageBox().addWarning(err.getDsErrore());
            forwardToPublisher(getLastPublisher());
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
                if (deleteFile.booleanValue()) {
                    FileUtils.deleteQuietly(fileToDownload);
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
        // MAC 29611
        getRequest().setAttribute("nascondiUpdate", "true");
        DecodeMapIF mappaStati = strumentiUrbanisticiEjb.getNuoviStatiPerRecuperoErroriDM();
        getForm().getRecuperoErrori().getTi_nuovo_stato().setDecodeMap(mappaStati);
        getForm().getRecuperoErrori().setEditMode();
        getForm().getRecuperoErrori().getTi_nuovo_stato().setEditMode();
        forwardToPublisher(getLastPublisher());
    }

    @Override
    public void confermaRecuperoErrore() throws EMFError {
        String isFromAjax = getRequest().getParameter("isFromJavaScript");
        if (Boolean.parseBoolean(isFromAjax)) {
            String tiNuovoStato = (String) getRequest().getParameter("ti_nuovo_stato");
            if (tiNuovoStato != null && !tiNuovoStato.equals("")
                    && getForm().getDatiGeneraliOutput().getId_strumenti_urbanistici_out() != null) {
                BigDecimal idSu = getForm().getDatiGeneraliOutput().getId_strumenti_urbanistici_out().parse();
                Date dataStato = strumentiUrbanisticiEjb.recuperoErroreSU(idSu, tiNuovoStato);
                getForm().getDatiGeneraliOutput().getTi_stato_out().setValue(tiNuovoStato);
                getForm().getDatiGeneraliOutput().getDt_stato_out()
                        .setValue(DateUtil.formatDateWithSlashAndTime(dataStato));
                getMessageBox().addInfo(
                        String.format("Lo strumento urbanistico è stato riportato allo stato '%s'.", tiNuovoStato));
                setNavigationEvent(NE_DETTAGLIO_VIEW);
                loadDettaglioStrumentoUrbanistico(idSu);
                getMessageBox().setViewMode(MessageBox.ViewMode.plain);
                getForm().getRecuperoErrori().setViewMode();
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
        strumentiUrbanisticiEjb
                .riportaInBozza(getForm().getDatiGeneraliOutput().getId_strumenti_urbanistici_out().parse());

        // MAC 27380 - Gli diciamo che vogliamo tornare al dettaglio
        setNavigationEvent(ListAction.NE_DETTAGLIO_VIEW);
        BigDecimal id = getForm().getDatiGeneraliOutput().getId_strumenti_urbanistici_out().parse();
        loadDettaglioStrumentoUrbanistico(id);

        forwardToPublisher(getLastPublisher());
    }

    @Override
    public void verificaDocumentiSU() throws Throwable {
        // Recupero lo strumento urbanistico di cui devo verificare i documenti
        BigDecimal idStrumentoUrbanistico = getForm().getDatiGeneraliOutput().getId_strumenti_urbanistici_out().parse();
        // Controllo non ci sia una verifica in corso
        boolean verificaInCorso = verificaDocumentiSUEjb.verificaInCorso(idStrumentoUrbanistico);
        // Se la verifica non è in corso, procedo
        if (!verificaInCorso) {
            getMessageBox().addInfo("Verifica documenti lanciata con successo!");
            verificaDocumentiSUEjb.callVerificaDocumentiAsync(idStrumentoUrbanistico);
            getForm().getRiepilogoButtonList().getVerificaDocumentiSU().setReadonly(true);
            getSession().setAttribute(VERIFICA_ATTIVATA, true);
        } else {
            getMessageBox().addWarning("Attenzione: verifica documenti in corso");
        }
        forwardToPublisher(Application.Publisher.STRUMENTI_URBANISTICI_WIZARD);
    }

    @SuppressLogging
    public void checkDocumentiSU() throws IOException, EMFError {
        try {
            // Recupero lo strumento urbanistico di cui devo verificare i documenti
            BigDecimal idStrumentoUrbanistico = getForm().getDatiGeneraliOutput().getId_strumenti_urbanistici_out()
                    .parse();
            String oggetto = strumentiUrbanisticiEjb.getOggettoStrumentoUrbanistico(idStrumentoUrbanistico);
            // Mi creo gli oggetti Ajax per gestire il polling
            JSONObject listObject = new JSONObject();
            JSONArray array = new JSONArray();
            List<String> listaDocumentiConErrori = new ArrayList<>();
            // // Controllo che non ci sia una verifica in corso tramite DB
            boolean verificaInCorso = verificaDocumentiSUEjb.verificaInCorso(idStrumentoUrbanistico);
            /*
             * Se non ci sono verifiche in corso e non ne erano partite prima, non devo fare nulla, lascio andare il
             * poll senza mostrare alcun messaggio
             */
            if (!verificaInCorso) {
                if (getSession().getAttribute(VERIFICA_ATTIVATA) == null) {
                    JSONObject strumentoUrbanisticoObject = new JSONObject();
                    strumentoUrbanisticoObject.put(
                            WebConstants.PARAMETER_JSON_VERIFICA_DOCUMENTI_STRUM_URB.ID_STRUMENTO_URBANISTICO.name(),
                            idStrumentoUrbanistico);
                    strumentoUrbanisticoObject
                            .put(WebConstants.PARAMETER_JSON_VERIFICA_DOCUMENTI_STRUM_URB.OGGETTO.name(), oggetto);
                    strumentoUrbanisticoObject
                            .put(WebConstants.PARAMETER_JSON_VERIFICA_DOCUMENTI_STRUM_URB.STOP_POLL.name(), "NO");
                    strumentoUrbanisticoObject.put(
                            WebConstants.PARAMETER_JSON_VERIFICA_DOCUMENTI_STRUM_URB.LISTA_DOCUMENTI_CON_ERRORI.name(),
                            "");
                    strumentoUrbanisticoObject.put(
                            WebConstants.PARAMETER_JSON_VERIFICA_DOCUMENTI_STRUM_URB.DOCUMENTI_CON_ERRORI.name(), "NO");
                    array.put(strumentoUrbanisticoObject);
                    listObject.put("array", array);
                    redirectToAjax(listObject);
                } // Se non ci sono verifihe in corso ma evidentemente prima erano state fatte partire, devo mostrare i
                  // risultati
                else if (getSession().getAttribute(VERIFICA_ATTIVATA) != null) {
                    getSession().removeAttribute(VERIFICA_ATTIVATA);
                    // Se esistono documenti con errore
                    boolean existsDocumentiDaVerifByTabellaConErrore = verificaDocumentiSUEjb
                            .existsDocumentiDaVerificareConErrorePerStrumentoUrbanistico(idStrumentoUrbanistico);

                    if (existsDocumentiDaVerifByTabellaConErrore) {
                        // Cerco se tra quelli rimasti in lista di verifica, ci siano degli errori
                        listaDocumentiConErrori = verificaDocumentiSUEjb
                                .getDocumentiVerificatiConErrorePerStrumentoUrbanistico(idStrumentoUrbanistico);
                    }

                    String documentiConErrori = "NO";
                    if (listaDocumentiConErrori != null && !listaDocumentiConErrori.isEmpty()) {
                        documentiConErrori = "SI";
                    }

                    JSONObject strumentoUrbanisticoObject = new JSONObject();
                    strumentoUrbanisticoObject.put(
                            WebConstants.PARAMETER_JSON_VERIFICA_DOCUMENTI_STRUM_URB.ID_STRUMENTO_URBANISTICO.name(),
                            idStrumentoUrbanistico);
                    strumentoUrbanisticoObject
                            .put(WebConstants.PARAMETER_JSON_VERIFICA_DOCUMENTI_STRUM_URB.OGGETTO.name(), oggetto);
                    strumentoUrbanisticoObject
                            .put(WebConstants.PARAMETER_JSON_VERIFICA_DOCUMENTI_STRUM_URB.STOP_POLL.name(), "SI");
                    strumentoUrbanisticoObject.put(
                            WebConstants.PARAMETER_JSON_VERIFICA_DOCUMENTI_STRUM_URB.LISTA_DOCUMENTI_CON_ERRORI.name(),
                            listaDocumentiConErrori.toString());
                    strumentoUrbanisticoObject.put(
                            WebConstants.PARAMETER_JSON_VERIFICA_DOCUMENTI_STRUM_URB.DOCUMENTI_CON_ERRORI.name(),
                            documentiConErrori);
                    array.put(strumentoUrbanisticoObject);
                    listObject.put("array", array);
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
    public void caricaStrumentoUrbanistico() throws Throwable {
        setNavigationEvent(ListAction.NE_DETTAGLIO_INSERT);
        getForm().getDatiGeneraliInput().setStatus(Status.insert);
        inizializzaWizard();
        forwardToPublisher(Application.Publisher.STRUMENTI_URBANISTICI_WIZARD);
    }

    // MEV25704 - scarica il report della verififca sui file
    public void downloadVerificaReport() throws EMFError {
        String riga = getRequest().getParameter("riga");
        Integer nr = Integer.parseInt(riga);

        BigDecimal idStrumentiUrbanistici = getForm().getDatiGeneraliOutput().getId_strumenti_urbanistici_out().parse();
        PigStrumentiUrbanistici su = strumentiUrbanisticiHelper.findById(PigStrumentiUrbanistici.class,
                idStrumentiUrbanistici);

        String nomeDocumentoSisma = getForm().getDocumentiCaricatiList().getTable().getRow(nr)
                .getString("nm_file_orig");
        PigStrumUrbDocumenti documentoSU = strumentiUrbanisticiHelper.getPigStrumUrbDocumentiBySuNmFileOrig(su,
                nomeDocumentoSisma);

        getResponse().setContentType("application/text");
        getResponse().setHeader("Content-Disposition", "attachment; filename=\"" + nomeDocumentoSisma + "_rv.txt");
        try ( // Ricavo lo stream di output
                BufferedOutputStream out = new BufferedOutputStream(getServletOutputStream())) {
            // Caccio il blobbo nel file xml
            byte[] report = documentoSU.getBlReport().getBytes();
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
            getMessageBox()
                    .addMessage(new Message(Message.MessageLevel.ERR, "Errore nel recupero del report di verifica."));
            log.error("downloadVerificaReport(): ", e);
        }
    }

    // MEV26278
    @Override
    public void ricercaStrumentiUrbanistici() throws EMFError {
        popolaStrumentiUrbanisticiList();

        forwardToPublisher(Application.Publisher.STRUMENTI_URBANISTICI);
    }

    // MEV 26936
    @Override
    public JSONObject triggerDatiGeneraliInputAnnoCollegato1OnTrigger() throws EMFError {
        getForm().getDatiGeneraliInput().post(getRequest());

        String faseCollegata1 = getForm().getDatiGeneraliInput().getFaseCollegata1().getValue();
        String nomeTipo = getForm().getDatiGeneraliInput().getNm_tipo_strumento_urbanistico().getValue();

        String annoCollegato1 = getForm().getDatiGeneraliInput().getAnnoCollegato1().getDecodedValue();

        if (!annoCollegato1.equals("") && !faseCollegata1.equals("")) {
            DecodeMap ids = strumentiUrbanisticiEjb.findNumeriByVersAnnoTipoSUFaseSoloVersati(
                    getUser().getIdOrganizzazioneFoglia(), new BigDecimal(annoCollegato1), nomeTipo, faseCollegata1);

            getForm().getDatiGeneraliInput().getIdentificativoCollegato1().setDecodeMap(ids);
        } else {
            getForm().getDatiGeneraliInput().getIdentificativoCollegato1().setDecodeMap(new DecodeMap());
        }

        return getForm().getDatiGeneraliInput().getIdentificativoCollegato1().asJSON();
    }

    @Override
    public JSONObject triggerDatiGeneraliInputAnnoCollegato2OnTrigger() throws EMFError {
        getForm().getDatiGeneraliInput().post(getRequest());
        String annoCollegato2 = getForm().getDatiGeneraliInput().getAnnoCollegato2().getValue();
        String faseCollegata2 = getForm().getDatiGeneraliInput().getFaseCollegata2().getValue();
        String nomeTipo = getForm().getDatiGeneraliInput().getNm_tipo_strumento_urbanistico().getValue();

        if (!annoCollegato2.equals("") && !faseCollegata2.equals("")) {
            DecodeMap numeri = strumentiUrbanisticiEjb.findNumeriByVersAnnoTipoSUFaseSoloVersati(
                    getUser().getIdOrganizzazioneFoglia(), new BigDecimal(annoCollegato2), nomeTipo, faseCollegata2);

            getForm().getDatiGeneraliInput().getIdentificativoCollegato2().setDecodeMap(numeri);
        } else {
            getForm().getDatiGeneraliInput().getIdentificativoCollegato2().setDecodeMap(new DecodeMap());
        }

        return getForm().getDatiGeneraliInput().getIdentificativoCollegato2().asJSON();
    }

    private void popolaFiltriRicercaStrumentiUrbanistici() {
        DecodeMap tipoStrumentoMap = DecodeMap.Factory.newInstance(
                strumentiUrbanisticiEjb.findTipiStrumentiUrbanisticiTB(), "nm_tipo_strumento_urbanistico",
                "nm_tipo_strumento_urbanistico");
        getForm().getFiltriStrumentiUrbanistici().getTi_strumento_urbanistico().setDecodeMap(tipoStrumentoMap);

        getForm().getFiltriStrumentiUrbanistici().getNi_anno()
                .setDecodeMap(strumentiUrbanisticiHelper.getStrumentiUrbanisticiAnno());

        DecodeMap statoMap = ComboGetter.getMappaSortedGenericEnum("nm_stato",
                PigStrumentiUrbanistici.TiStato.values());
        getForm().getFiltriStrumentiUrbanistici().getNm_stato().setDecodeMap(statoMap);

        DecodeMap fasiMap = strumentiUrbanisticiHelper.getFaseStrumentoMap();
        getForm().getFiltriStrumentiUrbanistici().getNm_fase_elaborazione().setDecodeMap(fasiMap);
    }

    @Override
    public void pulisciRicercaStrumentiUrbanistici() throws EMFError {
        getForm().getFiltriStrumentiUrbanistici().clear();
        getForm().getStrumentiUrbanisticiList().getTable().clear();

        forwardToPublisher(Application.Publisher.STRUMENTI_URBANISTICI);
    }

    private void popolaStrumentiUrbanisticiList() throws EMFError {
        if (getForm().getFiltriStrumentiUrbanistici().postAndValidate(getRequest(), getMessageBox())) {
            RicercaStrumentiUrbanisticiDTO ricercaStrumentiUrbanisticiDTO = new RicercaStrumentiUrbanisticiDTO(
                    getForm().getFiltriStrumentiUrbanistici());

            // POPOLA LA LISTA STRUMENTI URBANISTICI
            BaseTableInterface<?> strumUrbTable = strumentiUrbanisticiEjb
                    .findSUByVersAndStatesTB(ricercaStrumentiUrbanisticiDTO, getUser().getIdOrganizzazioneFoglia());
            getForm().getStrumentiUrbanisticiList().setTable(strumUrbTable);
            getForm().getStrumentiUrbanisticiList().getTable().first();
            getForm().getStrumentiUrbanisticiList().setStatus(BaseElements.Status.view);
        }
    }
}
