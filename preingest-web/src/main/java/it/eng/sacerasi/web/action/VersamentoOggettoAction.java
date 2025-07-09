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

import it.eng.parer.objectstorage.dto.BackendStorage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.jws.WebService;

import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.codehaus.jettison.json.JSONObject;

import it.eng.sacerasi.common.Constants;
import it.eng.sacerasi.common.Constants.TipiEncBinari;
import it.eng.sacerasi.common.Constants.TipiHash;
import it.eng.sacerasi.common.ejb.CommonDb;
import it.eng.sacerasi.exception.ParamApplicNotFoundException;
import it.eng.sacerasi.exception.ParerInternalError;
import it.eng.sacerasi.exception.ParerUserError;
import it.eng.sacerasi.messages.MessaggiWSBundle;
import it.eng.sacerasi.slite.gen.Application;
import it.eng.sacerasi.slite.gen.action.VersamentoOggettoAbstractAction;
import it.eng.sacerasi.slite.gen.form.MonitoraggioForm;
import it.eng.sacerasi.slite.gen.form.VersamentoOggettoForm;
import it.eng.sacerasi.slite.gen.tablebean.PigAmbienteVersRowBean;
import it.eng.sacerasi.slite.gen.tablebean.PigAmbienteVersTableBean;
import it.eng.sacerasi.slite.gen.tablebean.PigObjectRowBean;
import it.eng.sacerasi.slite.gen.tablebean.PigTipoFileObjectTableBean;
import it.eng.sacerasi.slite.gen.tablebean.PigTipoObjectRowBean;
import it.eng.sacerasi.slite.gen.tablebean.PigTipoObjectTableBean;
import it.eng.sacerasi.slite.gen.tablebean.PigVersRowBean;
import it.eng.sacerasi.slite.gen.tablebean.PigVersTableBean;
import it.eng.sacerasi.slite.gen.tablebean.PigVersTipoObjectDaTrasfRowBean;
import it.eng.sacerasi.slite.gen.tablebean.PigVersTipoObjectDaTrasfTableBean;
import it.eng.sacerasi.slite.gen.tablebean.PigXsdDatiSpecTableBean;
import it.eng.sacerasi.slite.gen.viewbean.MonVLisStatoVersTableBean;
import it.eng.sacerasi.util.BinEncUtility;
import it.eng.sacerasi.versamento.ejb.VersamentoOggettoEjb;
import it.eng.sacerasi.web.ejb.AmministrazioneEjb;
import it.eng.sacerasi.web.helper.ComboHelper;
import it.eng.sacerasi.web.helper.ConfigurationHelper;
import it.eng.sacerasi.web.util.ComboGetter;
import it.eng.sacerasi.web.util.WebConstants;
import it.eng.sacerasi.ws.InvioOggettoAsincrono;
import it.eng.sacerasi.ws.NotificaTrasferimento;
import it.eng.sacerasi.ws.invioOggettoAsincrono.ejb.InvioOggettoAsincronoEjb;
import it.eng.sacerasi.ws.notificaTrasferimento.dto.FileDepositatoType;
import it.eng.sacerasi.ws.notificaTrasferimento.dto.ListaFileDepositatoType;
import it.eng.sacerasi.ws.notificaTrasferimento.ejb.NotificaTrasferimentoEjb;
import it.eng.sacerasi.ws.response.InvioOggettoAsincronoRisposta;
import it.eng.sacerasi.ws.response.NotificaTrasferimentoRisposta;
import it.eng.sacerasi.ws.util.WsHelper;
import it.eng.spagoCore.error.EMFError;
import it.eng.spagoLite.actions.form.ListAction;
import it.eng.spagoLite.db.base.BaseRowInterface;
import it.eng.spagoLite.db.base.BaseTableInterface;
import it.eng.spagoLite.db.base.sorting.SortingRule;
import it.eng.spagoLite.db.oracle.decode.DecodeMap;
import it.eng.spagoLite.form.base.BaseElements.Status;
import it.eng.spagoLite.form.fields.Field;
import it.eng.spagoLite.form.fields.Fields;
import it.eng.spagoLite.form.fields.impl.Button;
import it.eng.spagoLite.form.fields.impl.ComboBox;
import it.eng.spagoLite.form.fields.impl.Input;
import it.eng.spagoLite.form.fields.impl.MultiSelect;
import it.eng.spagoLite.message.MessageBox;
import it.eng.spagoLite.security.Secure;
import it.eng.spagoLite.security.exception.AuthWSException;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import it.eng.parer.objectstorage.exceptions.ObjectStorageException;
import it.eng.parer.objectstorage.helper.SalvataggioBackendHelper;
import it.eng.sacerasi.web.helper.S3ServletHelper;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 *
 * @author Bonora_L
 */
@SuppressWarnings("unchecked")
public class VersamentoOggettoAction extends VersamentoOggettoAbstractAction {

    private static final Logger log = LoggerFactory.getLogger(VersamentoOggettoAction.class);

    @EJB(mappedName = "java:app/SacerAsync-ejb/ComboHelper")
    private ComboHelper comboHelper;
    @EJB(mappedName = "java:app/SacerAsync-ejb/CommonDb")
    private CommonDb commonDb;
    @EJB(mappedName = "java:app/SacerAsync-ejb/AmministrazioneEjb")
    private AmministrazioneEjb amministrazioneEjb;
    @EJB(mappedName = "java:app/SacerAsync-ejb/ConfigurationHelper")
    private ConfigurationHelper configurationHelper;
    @EJB(mappedName = "java:app/SacerAsync-ejb/InvioOggettoAsincronoEjb")
    private InvioOggettoAsincronoEjb invioOggettoAsincronoEjb;
    @EJB(mappedName = "java:app/SacerAsync-ejb/NotificaTrasferimentoEjb")
    private NotificaTrasferimentoEjb notificaTrasferimentoEjb;
    @EJB(mappedName = "java:app/SacerAsync-ejb/WsHelper")
    private WsHelper wsHelper;
    @EJB(mappedName = "java:app/SacerAsync-ejb/VersamentoOggettoEjb")
    private VersamentoOggettoEjb versamentoOggettoEjb;
    @EJB(mappedName = "java:app/SacerAsync-ejb/SalvataggioBackendHelper")
    private SalvataggioBackendHelper salvataggioBackendHelper;
    @EJB(mappedName = "java:app/SacerAsync-ejb/S3ServletHelper")
    private S3ServletHelper servletHelper;

    @Override
    public void initOnClick() throws EMFError {
        /* empty */
    }

    private void initForm(Fields<Field> formFields, String ambienteComponent, String versComponent,
            String tipoObjectComponent, String tiStatoObjectComponent, String tiVersFileComponent,
            String... tipoObjectVersamento) {
        // Caricamento delle combo
        BigDecimal idVers = getUser().getIdOrganizzazioneFoglia();
        BigDecimal idAmbienteVers = comboHelper.getIdAmbienteVersatore(idVers);
        final ComboBox<BigDecimal> ambienteCombo = (ComboBox<BigDecimal>) formFields.getComponent(ambienteComponent);
        final ComboBox<BigDecimal> versCombo = (ComboBox<BigDecimal>) formFields.getComponent(versComponent);
        final ComboBox<BigDecimal> tipoObjectCombo = (ComboBox<BigDecimal>) formFields
                .getComponent(tipoObjectComponent);
        final MultiSelect<String> tiStatoObjectMultiSelect = (MultiSelect<String>) formFields
                .getComponent(tiStatoObjectComponent);
        final MultiSelect<String> tiVersFileMultiSelect = (MultiSelect<String>) formFields
                .getComponent(tiVersFileComponent);

        // Ricavo i valori della combo AMBIENTE dalla tabella PIG_AMBIENTE_VERS
        PigAmbienteVersTableBean ambienteVersTableBean = comboHelper
                .getAmbienteVersatoreFromUtente(getUser().getIdUtente());
        // Ricavo i valori della combo VERSATORE
        PigVersTableBean versTableBean = comboHelper.getVersatoreFromAmbienteVersatore(getUser().getIdUtente(),
                idAmbienteVers);
        // Ricavo i valori della combo TIPO OBJECT
        PigTipoObjectTableBean tipoObjectTableBean = comboHelper.getTipoObjectFromVersatore(getUser().getIdUtente(),
                idVers, tipoObjectVersamento);

        ambienteCombo.setDecodeMap(
                DecodeMap.Factory.newInstance(ambienteVersTableBean, "id_ambiente_vers", "nm_ambiente_vers"));
        ambienteCombo.setValue(idAmbienteVers.toPlainString());
        versCombo.setDecodeMap(DecodeMap.Factory.newInstance(versTableBean, "id_vers", "nm_vers"));
        versCombo.setValue(idVers.toPlainString());
        setTipoObjectDecodeMap(tipoObjectCombo, tipoObjectTableBean);
        tiStatoObjectMultiSelect.setDecodeMap(ComboGetter.getMappaSortedGenericEnum("ti_stato_doc",
                Constants.StatoOggetto.getStatiOggettoMonitoraggioListaOggetti()));
        tiVersFileMultiSelect
                .setDecodeMap(ComboGetter.getMappaSortedGenericEnum("ti_vers_file", Constants.TipoVersamento.values()));
        formFields.setEditMode();
    }

    @Secure(action = "Menu.Versamenti.VersamentoOggetto")
    public void loadVersamentoOggetto() {
        getUser().getMenu().reset();
        getUser().getMenu().select("Menu.Versamenti.VersamentoOggetto");

        // Inizializzo le combo per l'oggetto da inviare
        getForm().getVersamentoOggettoDetail().reset();
        getForm().getVersamentoOggettoDetail().clear();
        setReadonlyVersamentoOggettoDaTrasformare(false);
        // Ricavo i valori della combo TIPO OBJECT
        BigDecimal idVers = getUser().getIdOrganizzazioneFoglia();
        PigTipoObjectTableBean tipoObjectTableBean = comboHelper.getTipoObjectFromVersatore(getUser().getIdUtente(),
                idVers, Constants.TipoVersamento.DA_TRASFORMARE.name(),
                Constants.TipoVersamento.ZIP_CON_XML_SACER.name());
        setTipoObjectDecodeMap(getForm().getVersamentoOggettoDetail().getNm_tipo_object(), tipoObjectTableBean);

        // MEV#27321
        getForm().getVersamentoOggettoDetail().getTi_priorita_versamento()
                .setDecodeMap(ComboGetter.getMappaOrdinalGenericEnum("ti_priorita_versamento",
                        it.eng.sacerasi.web.util.Constants.ComboFlagPrioVersType.values()));
        getForm().getVersamentoOggettoDetail().getTi_priorita_versamento().setValue(null);
        getForm().getVersamentoOggettoDetail().getTi_priorita_versamento().setHidden(true);
        initVersamentoOggetto();
        postLoad();
        forwardToPublisher(getDefaultPublsherName());
    }

    @Secure(action = "Menu.Versamenti.VersamentoOggettoDaTrasf")
    public void loadVersamentoOggettoDaTrasf() {
        getUser().getMenu().reset();
        getUser().getMenu().select("Menu.Versamenti.VersamentoOggettoDaTrasf");

        // Inizializzo le combo per l'oggetto da inviare
        getForm().getVersamentoOggettoDetail().reset();
        getForm().getVersamentoOggettoDetail().clear();
        setReadonlyVersamentoOggettoDaTrasformare(true);
        // Ricavo i valori della combo TIPO OBJECT
        BigDecimal idVers = getUser().getIdOrganizzazioneFoglia();
        PigTipoObjectTableBean tipoObjectTableBean = comboHelper.getTipoObjectFromVersatoreNoFleggati(
                getUser().getIdUtente(), idVers, Constants.TipoVersamento.DA_TRASFORMARE.name());
        setTipoObjectDecodeMap(getForm().getVersamentoOggettoDetail().getNm_tipo_object(), tipoObjectTableBean);

        getForm().getVersamentoOggettoDetail().getTi_priorita().setHidden(false);
        getForm().getVersamentoOggettoDetail().getTi_priorita().setDecodeMap(ComboGetter.getMappaOrdinalGenericEnum(
                "ti_priorita", it.eng.sacerasi.web.util.Constants.ComboFlagPrioTrasfType.values()));
        if (tipoObjectTableBean.getCurrentRow() != null) {
            getForm().getVersamentoOggettoDetail().getTi_priorita()
                    .setValue(it.eng.sacerasi.web.util.Constants.ComboFlagPrioTrasfType
                            .getEnumByString(tipoObjectTableBean.getCurrentRow().getTiPriorita()));
        }

        initVersamentoOggetto();
        postLoad();

        forwardToPublisher(Application.Publisher.VERSAMENTO_OGGETTO_DA_TRASFORMARE);
    }

    @Secure(action = "Menu.Versamenti.VersamentoUnitaDoc")
    public void loadVersamentoUnitaDoc() {
        getUser().getMenu().reset();
        getUser().getMenu().select("Menu.Versamenti.VersamentoUnitaDoc");

        // Inizializzo le combo per l'oggetto da inviare
        getForm().getVersamentoOggettoDetail().reset();
        getForm().getVersamentoOggettoDetail().clear();
        setReadonlyVersamentoOggettoDaTrasformare(false);
        // Ricavo i valori della combo TIPO OBJECT
        BigDecimal idVers = getUser().getIdOrganizzazioneFoglia();
        PigTipoObjectTableBean tipoObjectTableBean = comboHelper.getTipoObjectFromVersatoreNoFleggati(
                getUser().getIdUtente(), idVers, Constants.TipoVersamento.ZIP_CON_XML_SACER.name());
        setTipoObjectDecodeMap(getForm().getVersamentoOggettoDetail().getNm_tipo_object(), tipoObjectTableBean);
        initVersamentoOggetto();
        postLoad();

        forwardToPublisher(Application.Publisher.VERSAMENTO_UNITA_DOCUMENTARIE);
    }

    // MEV32647
    @Secure(action = "Menu.Versamenti.VersamentoArchivio")
    public void loadVersamentoArchivio() {
        getUser().getMenu().reset();
        getUser().getMenu().select("Menu.Versamenti.VersamentoArchivio");

        // Inizializzo le combo per l'oggetto da inviare
        getForm().getVersamentoOggettoDetail().reset();
        getForm().getVersamentoOggettoDetail().clear();
        setReadonlyVersamentoOggettoDaTrasformare(false);
        // Ricavo i valori della combo TIPO OBJECT
        BigDecimal idVers = getUser().getIdOrganizzazioneFoglia();
        PigTipoObjectTableBean tipoObjectTableBean = comboHelper.getTipoObjectFromVersatore(getUser().getIdUtente(),
                idVers, Constants.TipoVersamento.ZIP_CON_XML_SACER.name(),
                Constants.TipoVersamento.DA_TRASFORMARE.name());
        setTipoObjectDecodeMap(getForm().getVersamentoOggettoDetail().getNm_tipo_object(), tipoObjectTableBean);

        // MEV27321
        getForm().getVersamentoOggettoDetail().getTi_priorita_versamento()
                .setDecodeMap(ComboGetter.getMappaOrdinalGenericEnum("ti_priorita_versamento",
                        it.eng.sacerasi.web.util.Constants.ComboFlagPrioVersType.values()));
        getForm().getVersamentoOggettoDetail().getTi_priorita_versamento().setValue(null);
        getForm().getVersamentoOggettoDetail().getTi_priorita_versamento().setHidden(true);

        PigVersRowBean versRow = amministrazioneEjb.getPigVersRowBean(idVers);

        String prefisso = configurationHelper.getValoreParamApplicByIdVers(
                it.eng.sacerasi.common.Constants.DS_PREFISSO_PATH, versRow.getIdAmbienteVers(), versRow.getIdVers());
        String dsPathArchivio = File.separator + prefisso + versRow.getNmVers() + "/DA_VERSARE/";
        getForm().getVersamentoOggettoDetail().getDs_path_archivio().setValue(dsPathArchivio);

        getForm().getVersamentoOggettoDetail().getDs_path_archivio_object().setEditMode();
        getForm().getVersamentoOggettoDetail().getDs_path_archivio_object().setHidden(false);

        getForm().getVersamentoOggettoDetail().getFile_to_upload().setHidden(true);
        getForm().getVersamentoOggettoDetail().getCd_versione_xml().setHidden(true);
        getForm().getVersamentoOggettoDetail().getXml_to_upload().setHidden(true);

        initVersamentoOggetto();

        getForm().getVersamentoOggettoDetail().getFl_trasm_ftp().setValue("1");
        getForm().getVersamentoOggettoDetail().getFl_trasm_ftp().setHidden(true);

        postLoad();
        forwardToPublisher(Application.Publisher.VERSAMENTO_DA_ARCHIVIO);
    }

    private void initVersamentoOggetto() {
        getForm().getVersamentoOggettoDetail().getNm_ambiente_vers()
                .setValue(getUser().getOrganizzazioneMap().get("AMBIENTE"));
        getForm().getVersamentoOggettoDetail().getNm_vers().setValue(getUser().getOrganizzazioneMap().get("VERSATORE"));
        getForm().getVersamentoOggettoDetail().setStatus(Status.update);
        getForm().getVersamentoOggettoDetail().setEditMode();
        // Inizializzo la combo per l'oggetto padre
        PigAmbienteVersTableBean ambienteVersTableBean = comboHelper
                .getAmbienteVersatoreFromUtente(getUser().getIdUtente());
        getForm().getVersamentoOggettoDetail().getNm_ambiente_vers_padre().setDecodeMap(
                DecodeMap.Factory.newInstance(ambienteVersTableBean, "id_ambiente_vers", "nm_ambiente_vers"));
        getForm().getVersamentoOggettoDetail().getNm_vers_padre().setDecodeMap(new DecodeMap());
        getForm().getVersamentoOggettoDetail().getNm_tipo_object_padre().setDecodeMap(new DecodeMap());
        getForm().getVersamentoOggettoDetail().getCd_key_object_padre().setDecodeMap(new DecodeMap());
        getForm().getVersamentoOggettoDetail().getFl_trasm_ftp().setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());
        getForm().getVersamentoOggettoDetail().getFl_trasm_ftp().setValue("0");
        getForm().getVersamentoOggettoDetail().getTi_gest_oggetti_figli().setDecodeMap(new DecodeMap());

        getForm().getVersamentoOggettoDetail().getDs_path_ftp().setValue(null);

        getForm().getVersamentoOggettoDetail().getCd_key_object().setHidden(true);

        getForm().getVersamentoOggettoDetail().getFile_to_upload().setHidden(false);
        getForm().getVersamentoOggettoDetail().getCd_versione_xml().setHidden(false);
        getForm().getVersamentoOggettoDetail().getXml_to_upload().setHidden(false);
    }

    public void versamentoOggettoSuObjectStorageCompletato() {
        getForm().getVersamentoOggettoDetail().post(getRequest());

        getMessageBox().addInfo("Fase 1 completata: l'invio oggetto a PreIngest \u00E8 terminato con successo");
        getMessageBox().addInfo("Fase 2 completata: l'oggetto \u00E8 stato trasferito nell'area di salvataggio");
        getMessageBox().addInfo("Versamento oggetto completato con successo");
        getMessageBox().setViewMode(MessageBox.ViewMode.plain);

        getForm().getVersamentoOggettoDetail().setStatus(Status.view);
        getForm().getVersamentoOggettoDetail().setViewMode();
        postLoad();

        forwardToPublisher(getLastPublisher());
    }

    public void getSupportedArchiveFormatList() throws ParerInternalError {
        getForm().getVersamentoOggettoDetail().post(getRequest());
        String estensioni = configurationHelper.getValoreParamApplicByApplic(Constants.ESTENSIONI_FILE_DA_TRASF);

        String[] estensioniAmmesseLowerCase = estensioni.split(",");

        JSONObject result = new JSONObject();
        JSONArray estensioniJson = new JSONArray();
        for (String estensioneAmmessa : estensioniAmmesseLowerCase) {
            estensioniJson.put(estensioneAmmessa);
        }

        try {

            result.put("estensioni_ammesse", estensioniJson);

        } catch (JSONException je) {
            throw new ParerInternalError(je);
        }

        redirectToAjax(result);
    }

    // ME 32647
    @Override
    public void versaOggettoDaArchivio() throws EMFError {
        getForm().getVersamentoOggettoDetail().getFl_trasm_ftp().setValue("1"); // fatto qui per passare la validazione
        // sotto.

        if (getForm().getVersamentoOggettoDetail().validate(getMessageBox())) {
            try {
                BigDecimal idVers = getUser().getIdOrganizzazioneFoglia();
                BigDecimal idTipoObject = getForm().getVersamentoOggettoDetail().getNm_tipo_object().parse();
                String rootFtp = commonDb.getRootFtpParam();

                String dsPathArchivio = getForm().getVersamentoOggettoDetail().getDs_path_archivio().parse();
                String dsPathArchivioObject = getForm().getVersamentoOggettoDetail().getDs_path_archivio_object()
                        .parse();

                if (dsPathArchivioObject == null || dsPathArchivioObject.isEmpty()) {
                    throw new ParerUserError("Il nome oggetto deve essere compilato.");
                }

                String fileName = new File(dsPathArchivioObject).getName();
                String cdKeyObjectForm = fileName.substring(0, fileName.indexOf("."));

                Path ftpPath = Paths.get(getFtpFilePath(rootFtp, idVers, cdKeyObjectForm));
                Path fileInArchivio = Paths.get(rootFtp, dsPathArchivio, dsPathArchivioObject);

                Path ftpFilePath = Paths.get(ftpPath.toString(), fileName);

                if (!Files.exists(fileInArchivio)) {
                    throw new ParerUserError("Il file richiesto non è stato trovato.");
                }

                // prima di spostarlo, eseguo i controlli che non saranno esguiti quando si simularà il versamento via
                // ftp
                PigTipoObjectRowBean pigTipoObjectRowBean = amministrazioneEjb.getPigTipoObjectRowBean(idTipoObject);
                boolean oggettoDaTrasformare = pigTipoObjectRowBean.getTiVersFile()
                        .equals(Constants.TipoVersamento.DA_TRASFORMARE.name());
                checkFile(fileName, fileInArchivio.toFile(), oggettoDaTrasformare,
                        Constants.DIM_MAX_FILE_DA_VERSARE_ARCH);

                try {
                    Files.createDirectories(ftpPath);
                } catch (IOException ex) {
                    log.error("Impossibile creare la cartella per la migrazione su FTP.", ex);
                    throw new ParerUserError("Impossibile creare la cartella per la migrazione su FTP.");
                }

                try {
                    Files.move(fileInArchivio, ftpFilePath, StandardCopyOption.ATOMIC_MOVE);
                } catch (Exception ex) {
                    log.error("Impossibile spostare il file per la migrazione su FTP.", ex);
                    throw new ParerUserError("Impossibile spostare il file per la migrazione su FTP.");
                }

                getMessageBox().addInfo("File " + fileName + " spostato sull'FTP.");

                // prepara i valori precalcolabili
                initVersamentoOggetto();

                setTiGestOggettiFigliDecodeMap(pigTipoObjectRowBean, Constants.TipoGestioneOggettiFigli.MANUALE.name());

                if (pigTipoObjectRowBean.getTiVersFile().equals(Constants.TipoVersamento.DA_TRASFORMARE.name())) {
                    triggerVersamentoOggettoDetailNm_tipo_objectOnTriggerCommonDaTrasformare(pigTipoObjectRowBean,
                            false);
                    getForm().getVersamentoOggettoDetail().getTi_gest_oggetti_figli()
                            .setValue(Constants.TipoGestioneOggettiFigli.AUTOMATICA.name());
                } else {
                    triggerVersamentoOggettoDetailNm_tipo_objectOnTriggerCommonNonDaTrasformare(pigTipoObjectRowBean,
                            false);
                }

                getUser().getMenu().reset();
                getUser().getMenu().select("Menu.Versamenti.VersamentoOggetto");

                PigVersRowBean versRow = amministrazioneEjb.getPigVersRowBean(idVers);
                getForm().getVersamentoOggettoDetail().getDs_path_ftp().setViewMode();
                getForm().getVersamentoOggettoDetail().getDs_path_ftp().setHidden(false);
                getForm().getVersamentoOggettoDetail().getDs_path_ftp().setValue(versRow.getDsPathInputFtp());

                getForm().getVersamentoOggettoDetail().getCd_key_object().setViewMode();
                getForm().getVersamentoOggettoDetail().getCd_key_object().setHidden(false);
                getForm().getVersamentoOggettoDetail().getCd_key_object().setValue(cdKeyObjectForm);

                getForm().getVersamentoOggettoDetail().getFl_trasm_ftp().setViewMode();
                getForm().getVersamentoOggettoDetail().getFl_trasm_ftp().setHidden(false);
                getForm().getVersamentoOggettoDetail().getFl_trasm_ftp().setValue("1");

                getForm().getVersamentoOggettoDetail().getFile_to_upload().setHidden(true);
                getForm().getVersamentoOggettoDetail().getCd_versione_xml().setHidden(true);
                getForm().getVersamentoOggettoDetail().getXml_to_upload().setHidden(true);

                // per bloccare il trigger js su questo campo al primo caricamento
                getSession().setAttribute("nm_tipo_object_dont_trigger", true);

                forwardToPublisher(getDefaultPublsherName());

            } catch (ParerUserError ex) {
                getMessageBox().addError(ex.getDescription());
                forwardToPublisher(Application.Publisher.VERSAMENTO_DA_ARCHIVIO);
            }

        } else {
            forwardToPublisher(getLastPublisher());
        }
    }

    @Override
    public void versaOggetto() throws EMFError {
        if (getForm().getVersamentoOggettoDetail().validate(getMessageBox())) {
            boolean errorIOFtp = false;
            BigDecimal idObjTrasf = null;
            boolean mettereInViewMode = false;
            try {
                String ambiente = getForm().getVersamentoOggettoDetail().getNm_ambiente_vers().parse();
                BigDecimal idVers = getUser().getIdOrganizzazioneFoglia();
                String versatore = getForm().getVersamentoOggettoDetail().getNm_vers().parse();
                BigDecimal idTipoObject = getForm().getVersamentoOggettoDetail().getNm_tipo_object().parse();
                String dsObject = getForm().getVersamentoOggettoDetail().getDs_object().parse();
                String dsHashFileVers = getForm().getVersamentoOggettoDetail().getDs_hash_file_vers().parse();
                String flTrasmFtp = getForm().getVersamentoOggettoDetail().getFl_trasm_ftp().parse();
                String cdKeyObjectForm = getForm().getVersamentoOggettoDetail().getCd_key_object().parse();
                String tiPriorita = getForm().getVersamentoOggettoDetail().getTi_priorita().parse();
                String tiPrioritaVersamento = getForm().getVersamentoOggettoDetail().getTi_priorita_versamento()
                        .parse();
                String nmTipoFile;

                // Verifico che l'utente sia abilitato ai servizi
                WebService annotationInvio = InvioOggettoAsincrono.class.getAnnotation(WebService.class);
                WebService annotationNotif = NotificaTrasferimento.class.getAnnotation(WebService.class);
                wsHelper.checkAuthorizations(ambiente, versatore, getUser().getUsername(),
                        annotationInvio.serviceName());
                wsHelper.checkAuthorizations(ambiente, versatore, getUser().getUsername(),
                        annotationNotif.serviceName());

                // MEV 34843
                BigDecimal idAmbiente = servletHelper.getIdAmbienteVersatore(idVers);
                BackendStorage backendVersamento = salvataggioBackendHelper.getBackendForVersamento(idAmbiente, idVers,
                        idTipoObject);

                PigTipoObjectRowBean pigTipoObjectRowBean = amministrazioneEjb.getPigTipoObjectRowBean(idTipoObject);
                PigTipoFileObjectTableBean pigTipoFileObjectTableBean = amministrazioneEjb
                        .getPigTipoFileObjectTableBean(idTipoObject);
                PigXsdDatiSpecTableBean pigXsdDatiSpecTableBean = amministrazioneEjb
                        .getPigXsdDatiSpecTableBean(idTipoObject, null);
                if (pigTipoFileObjectTableBean.isEmpty()) {
                    throw new ParerUserError(
                            "Nella configurazione dell'oggetto manca la definizione del tipo file. Non \u00E8 possibile eseguire il versamento");
                } else if (pigTipoFileObjectTableBean.size() > 1) {
                    throw new ParerUserError(
                            "Il tipo di oggetto deve presentare un solo tipo di file per eseguire il versamento");
                } else {
                    nmTipoFile = pigTipoFileObjectTableBean.getRow(0).getNmTipoFileObject();
                }

                String ftpPath = null;
                InvioOggettoAsincronoRisposta invioRisposta;
                NotificaTrasferimentoRisposta notificaRisposta;
                String basePath = System.getProperty("java.io.tmpdir");
                String rootFtp = commonDb.getRootFtpParam();
                if (StringUtils.isBlank(rootFtp)) {
                    getMessageBox().addError(
                            "Errore inatteso nel caricamento del file: errata configurazione della directory FTP di PreIngest");
                }
                String cdKeyObjectFile = getForm().getVersamentoOggettoDetail().getFile_to_upload().parse();

                String cdKeyObject = null;
                String estensioneDedotta = Constants.ZIP_EXTENSION;
                File file = null;
                if (flTrasmFtp.equals("1") && StringUtils.isBlank(cdKeyObjectForm)) {
                    getMessageBox().addError("Definire il nome oggetto del file indicato come trasmesso via FTP");
                } else if (flTrasmFtp.equals("0") && StringUtils.isBlank(cdKeyObjectFile)) {
                    getMessageBox().addError("Selezionare il file di cui eseguire l'upload");
                } else if (flTrasmFtp.equals("1") && StringUtils.isNotBlank(cdKeyObjectForm)) {
                    cdKeyObject = cdKeyObjectForm;
                    cdKeyObjectFile = cdKeyObjectForm + estensioneDedotta;
                } else if (flTrasmFtp.equals("0")) {
                    cdKeyObject = cdKeyObjectFile.substring(0, cdKeyObjectFile.indexOf("."));
                    estensioneDedotta = cdKeyObjectFile.substring(cdKeyObjectFile.indexOf("."));
                    // Eseguo il salvataggio del file in una directory temporanea
                    file = new File(basePath + File.separator + cdKeyObjectFile);
                    getForm().getVersamentoOggettoDetail().getFile_to_upload().writeUploadedFile(file);
                    boolean oggettoDaTrasformare = pigTipoObjectRowBean.getTiVersFile()
                            .equals(Constants.TipoVersamento.DA_TRASFORMARE.name());
                    checkFile(cdKeyObjectFile, file, oggettoDaTrasformare, Constants.DIM_MAX_FILE_DA_VERSARE_FTP);
                    if (pigTipoObjectRowBean.getFlContrHash().equals(WebConstants.DB_TRUE)
                            && StringUtils.isBlank(dsHashFileVers)) {
                        getMessageBox().addError("Il tipo oggetto prevede che sia controllato lo hash del file");
                    }
                }

                // se il tipo oggetto è "da trasformare" allora la priorità di trasformazione deve essere valorizzata
                if (pigTipoObjectRowBean.getTiVersFile().equals(Constants.TipoVersamento.DA_TRASFORMARE.name())
                        && tiPriorita == null) {
                    if (pigTipoObjectRowBean.getTiPriorita() != null) {
                        tiPriorita = it.eng.sacerasi.web.util.Constants.ComboFlagPrioTrasfType
                                .getEnumByString(pigTipoObjectRowBean.getTiPriorita());
                    } else {
                        getMessageBox().addError("Impossibile versare, priorità non impostata sul Tipo Oggetto.");
                    }
                }
                if ((pigTipoObjectRowBean.getTiVersFile().equals(Constants.TipoVersamento.ZIP_CON_XML_SACER.name())
                        || pigTipoObjectRowBean.getTiVersFile().equals(Constants.TipoVersamento.NO_ZIP.name()))
                        && tiPrioritaVersamento == null && pigTipoObjectRowBean.getTiPrioritaVersamento() != null) {
                    tiPrioritaVersamento = it.eng.sacerasi.web.util.Constants.ComboFlagPrioVersType
                            .getEnumByString(pigTipoObjectRowBean.getTiPrioritaVersamento());
                }

                String xmlFileName = getForm().getVersamentoOggettoDetail().getXml_to_upload().parse();
                String cdVersioneXml = getForm().getVersamentoOggettoDetail().getCd_versione_xml().parse();
                String xml = null;
                if (!getMessageBox().hasError()) {
                    boolean fileUploaded = StringUtils.isNotBlank(xmlFileName);

                    if (!pigXsdDatiSpecTableBean.isEmpty()) {
                        if (!fileUploaded || StringUtils.isBlank(cdVersioneXml)) {
                            getMessageBox().addError(
                                    "Per questo tipo oggetto è necessario allegare un indice oggetto e specificare una versione xml.");
                        }
                    }

                    if (fileUploaded && StringUtils.isBlank(cdVersioneXml)) {
                        getMessageBox().addError(
                                "Per l'upload del file XML \u00E8 necessario definire anche la versione del xml");
                    } else if (StringUtils.isBlank(xmlFileName) && StringUtils.isNotBlank(cdVersioneXml)) {
                        getMessageBox()
                                .addError("\u00C8 stato definita la versione del xml senza eseguire l'upload del file");
                    } else if (fileUploaded && pigXsdDatiSpecTableBean.isEmpty()) {
                        getMessageBox().addError(
                                "Per l'upload del file XML \u00E8 necessario definire per il tipo di oggetto lo XSD di validazione");
                    } else if (fileUploaded) {
                        // Eseguo il salvataggio del file in una directory temporanea
                        File xmlFile = new File(basePath + File.separator + xmlFileName);
                        getForm().getVersamentoOggettoDetail().getXml_to_upload().writeUploadedFile(xmlFile);
                        xml = FileUtils.readFileToString(xmlFile, StandardCharsets.UTF_8);
                    }
                }
                // algo hash
                String tiHashFileVers = null;
                if (StringUtils.isNotBlank(dsHashFileVers)) {
                    if (!BinEncUtility.isHexString(dsHashFileVers)) {
                        getMessageBox().addError("Il formato dell'hash non è coerente");
                    } else {
                        // check algo type
                        tiHashFileVers = this.impostaAlgoHash(dsHashFileVers).descrivi();
                        if (tiHashFileVers.equals(TipiHash.SCONOSCIUTO.descrivi())) {
                            getMessageBox().addError(
                                    "Il formato dell'hash non è tra quelli supportati (" + TipiHash.alldesc() + ")");
                        }
                    }
                } else {
                    tiHashFileVers = TipiHash.SHA_256.descrivi();// default
                }

                // Controlli su un eventuale oggetto di tipo DA_TRASFORMARE padre
                BigDecimal idObjectPadre = getForm().getVersamentoOggettoDetail().getCd_key_object_padre().parse();
                String cdKeyObjectPadre = null;
                String ambientePadre = StringUtils.trimToNull(
                        getForm().getVersamentoOggettoDetail().getNm_ambiente_vers_padre().getDecodedValue());
                String versatorePadre = StringUtils
                        .trimToNull(getForm().getVersamentoOggettoDetail().getNm_vers_padre().getDecodedValue());
                String cdVersGen = getForm().getVersamentoOggettoDetail().getCd_vers_gen().parse();
                BigDecimal niTotObjectTrasf = getForm().getVersamentoOggettoDetail().getNi_tot_object_trasf().parse();
                BigDecimal pgOggettoTrasf = getForm().getVersamentoOggettoDetail().getPg_oggetto_trasf().parse();
                String tiGestOggettiFigli = getForm().getVersamentoOggettoDetail().getTi_gest_oggetti_figli().parse();
                boolean createPigObjectTrasf = false;
                if (!getMessageBox().hasError() && idObjectPadre != null) {
                    cdKeyObjectPadre = getForm().getVersamentoOggettoDetail().getCd_key_object_padre()
                            .getDecodedValue();
                    if (niTotObjectTrasf == null || niTotObjectTrasf.compareTo(BigDecimal.ZERO) <= 0) {
                        throw new ParerUserError(
                                "Per l'oggetto da trasformare deve essere definito il numero di oggetti generati da trasformazione");
                    }
                    if (pgOggettoTrasf == null || pgOggettoTrasf.compareTo(BigDecimal.ZERO) <= 0) {
                        throw new ParerUserError(
                                "Per l'oggetto da versare deve essere definito il progressivo dell'oggetto generato da trasformazione");
                    }
                    if (pgOggettoTrasf.compareTo(niTotObjectTrasf) > 0) {
                        throw new ParerUserError(
                                "Per l'oggetto da versare il progressivo dell'oggetto generato da trasformazione deve essere minore o uguale al numero di oggetti generati");
                    }
                    BigDecimal idTipoObjectPadre = getForm().getVersamentoOggettoDetail().getNm_tipo_object_padre()
                            .parse();
                    if (idTipoObjectPadre != null) {
                        PigVersTipoObjectDaTrasfRowBean pigVersTipoObjectDaTrasfRowBean = amministrazioneEjb
                                .getPigVersTipoObjectDaTrasfRowBean(idTipoObjectPadre, null, idVers);
                        if (pigVersTipoObjectDaTrasfRowBean == null
                                || !pigVersTipoObjectDaTrasfRowBean.getIdTipoObjectGen().equals(idTipoObject)) {
                            throw new ParerUserError(
                                    "Per il tipo oggetto da trasformare non \u00E8 definito il versatore e/o il tipo oggetto a cui appartiene l'oggetto da versare");
                        }
                    }
                    idObjTrasf = amministrazioneEjb.checkPigObjectTrasf(idObjectPadre, idTipoObject, idVers,
                            cdKeyObject, pgOggettoTrasf);
                    if (idObjTrasf == null) {
                        createPigObjectTrasf = true;
                    }
                }
                if (!getMessageBox().hasError()) {
                    ftpPath = getFtpFilePath(rootFtp, idVers, cdKeyObject);
                    if (flTrasmFtp.equals("1")) {
                        File tmp = null;
                        /* ****************************************** */
                        // *** FORSE SERVE OPPURE NO *** ///
                        // Se versamento da trasformare
                        boolean tipoVersDaTrasformare = pigTipoObjectRowBean.getTiVersFile()
                                .equals(Constants.TipoVersamento.DA_TRASFORMARE.name());
                        String estensioni = null;
                        String[] estensioniAmmesse = null;
                        if (tipoVersDaTrasformare) {
                            estensioni = configurationHelper
                                    .getValoreParamApplicByApplic(Constants.ESTENSIONI_FILE_DA_TRASF);
                            estensioniAmmesse = estensioni.split(",");
                        } else {
                            estensioni = Constants.ZIP_EXTENSION;
                            estensioniAmmesse = new String[1];
                            estensioniAmmesse[0] = Constants.ZIP_EXTENSION;
                        }
                        for (String est : estensioniAmmesse) {
                            tmp = new File(ftpPath + cdKeyObject + est);
                            if (tmp.exists() && tmp.isFile()) {
                                // servirà poi per passarla alla notifica del trasferimento file
                                estensioneDedotta = est;
                                break;
                            }
                        }
                        if (!tmp.exists() || !tmp.isFile()) {
                            throw new ParerUserError(
                                    "File non presente nell'area FTP nonostante sia stato indicato come trasmesso via FTP");
                        }
                    }
                }
                if (!getMessageBox().hasError() && idObjectPadre != null) {
                    if (createPigObjectTrasf) {
                        long idObjTrasfLong = amministrazioneEjb.createPigObjectTrasf(idObjectPadre, cdKeyObject,
                                dsObject, idVers, idTipoObject, Constants.DS_PATH_TRASF_NON_DEFINITO, dsHashFileVers,
                                tiHashFileVers, TipiEncBinari.HEX_BINARY.descrivi(), pgOggettoTrasf, cdVersioneXml,
                                xml);
                        idObjTrasf = new BigDecimal(idObjTrasfLong);
                    } else {
                        amministrazioneEjb.updatePigObjectTrasf(idObjTrasf, dsObject,
                                Constants.DS_PATH_TRASF_NON_DEFINITO, dsHashFileVers, tiHashFileVers,
                                TipiEncBinari.HEX_BINARY.descrivi(), pgOggettoTrasf, cdVersioneXml, xml);
                    }
                }
                /*
                 * MEV#13040 - Se non è definita la regular Expression
                 */
                if (!getMessageBox().hasError()) {
                    if (pigTipoObjectRowBean.getTiVersFile().equals(Constants.TipoVersamento.DA_TRASFORMARE.name())
                            && (pigTipoObjectRowBean.getDsRegExpCdVers() == null
                                    || pigTipoObjectRowBean.getDsRegExpCdVers().equals(""))) {
                        PigVersTipoObjectDaTrasfTableBean tipoObjDaTrasfTB = amministrazioneEjb
                                .getPigVersTipoObjectDaTrasfTableBean(pigTipoObjectRowBean.getIdTipoObject());
                        if (tipoObjDaTrasfTB != null && tipoObjDaTrasfTB.size() == 1) {
                            cdVersGen = tipoObjDaTrasfTB.getRow(0).getCdVersGen();
                        }
                    }
                }
                if (!getMessageBox().hasError()) {
                    // Eseguo l'invioOggettoAsincrono
                    String tipoObject = getForm().getVersamentoOggettoDetail().getNm_tipo_object().getDecodedValue();

                    invioRisposta = executeInvioOggettoAsincrono(getUser().getUsername(), ambiente, versatore,
                            tipoObject, cdKeyObject, dsObject, cdVersioneXml, xml, ambientePadre, versatorePadre,
                            cdKeyObjectPadre, niTotObjectTrasf, pgOggettoTrasf, cdVersGen, tiGestOggettiFigli,
                            tiPriorita, tiPrioritaVersamento);
                    // Controllo della risposta
                    if (invioRisposta.getCdEsito() == Constants.EsitoServizio.OK
                            || invioRisposta.getCdErr().equals(MessaggiWSBundle.PING_SENDOBJ_OBJ_010)) {
                        // Messaggio per l'utente
                        getMessageBox().addInfo(
                                "Fase 1 completata: l'invio oggetto a PreIngest \u00E8 terminato con successo");
                    } else {
                        if (idObjTrasf != null) {
                            amministrazioneEjb.updatePigObjectTrasf(idObjTrasf, invioRisposta.getCdErr(),
                                    invioRisposta.getDsErr());
                        }
                        if (idObjectPadre != null) {
                            amministrazioneEjb.checkPigObjectFigliAndUpdate(idObjectPadre);
                        }
                        getMessageBox()
                                .addError("Errore " + invioRisposta.getCdErr() + ": " + invioRisposta.getDsErr());
                    }
                    getMessageBox().setViewMode(MessageBox.ViewMode.plain);
                }
                if (!getMessageBox().hasError() && flTrasmFtp.equals("0")) {
                    // Da questo momento in poi si può accendere il pulsante "nuovo versametno"
                    mettereInViewMode = true;
                    errorIOFtp = true;
                    File ftpPathDir = new File(ftpPath);
                    // MEV#14100 - De il file è già presente nella destinazione lo elimina così la move non esplode
                    File fileDestinazione = FileUtils.getFile(ftpPathDir, file.getName());
                    if (fileDestinazione != null) {
                        FileUtils.deleteQuietly(fileDestinazione);
                    }
                    FileUtils.moveFileToDirectory(file, ftpPathDir, true);
                    // Messaggio per l'utente
                    getMessageBox()
                            .addInfo("Fase 2 completata: l'oggetto \u00E8 stato trasferito nell'area di salvataggio");
                    getMessageBox().setViewMode(MessageBox.ViewMode.plain);
                }
                if (!getMessageBox().hasError()) {
                    // Eseguo la NotificaTrasferimentoFile
                    notificaRisposta = executeNotificaTrasferimento(ambiente, versatore, nmTipoFile, cdKeyObject,
                            dsHashFileVers, estensioneDedotta, tiHashFileVers, backendVersamento.getBackendId());

                    // Messaggio per l'utente
                    if (notificaRisposta.getCdEsito().equals(Constants.EsitoServizio.OK.name())) {
                        getMessageBox().addInfo("Versamento oggetto completato con successo");
                    } else {
                        if (idObjTrasf != null) {
                            amministrazioneEjb.updatePigObjectTrasf(idObjTrasf, notificaRisposta.getCdErr(),
                                    notificaRisposta.getDsErr());
                        }
                        getMessageBox()
                                .addError("Errore " + notificaRisposta.getCdErr() + ": " + notificaRisposta.getDsErr());
                        mettereInViewMode = false;
                    }
                    if (idObjectPadre != null) {
                        amministrazioneEjb.checkPigObjectFigliAndUpdate(idObjectPadre);
                    }
                    getMessageBox().setViewMode(MessageBox.ViewMode.plain);
                }
            } catch (AuthWSException ex) {
                log.error("Errore di autenticazione : " + ex.getCodiceErrore() + " - " + ex.getDescrizioneErrore(), ex);
                getMessageBox()
                        .addError("L'utente che ha eseguito il login non \u00E8 abilitato ai servizi di versamento");
                mettereInViewMode = false;
            } catch (ParerUserError ex) {
                getMessageBox().addError(ex.getDescription());
            } catch (ParerInternalError ex) {
                getMessageBox().addError(
                        "Errore inatteso nel caricamento del file: errata configurazione della directory FTP di PreIngest");
                mettereInViewMode = false;
            } catch (IOException ex) {
                log.error("Errore inatteso nel salvataggio del file :{}", ExceptionUtils.getRootCauseMessage(ex));
                if (errorIOFtp && idObjTrasf != null) {
                    amministrazioneEjb.updatePigObjectTrasf(idObjTrasf, "TRASFERIMENTO_FTP",
                            "Il trasferimento nell'area FTP fallisce");
                }
                getMessageBox().addError("Errore inatteso nel salvataggio del file");
                mettereInViewMode = false;
            } catch (Exception ex) {
                log.error("Errore inatteso durante il versamento : {}", ExceptionUtils.getRootCauseMessage(ex));
                getMessageBox().addError("Errore inatteso durante il versamento");
                mettereInViewMode = false;
            }

            if (mettereInViewMode) {
                getForm().getVersamentoOggettoDetail().setStatus(Status.view);
                getForm().getVersamentoOggettoDetail().setViewMode();
                postLoad();
            }

        }

        forwardToPublisher(getLastPublisher());
    }

    private void checkFile(String cdKeyObjectFile, File tmpFile, boolean tipoVersDaTrasformare,
            String nmParamMaxFileDim) throws ParerUserError {
        final int maxLength = getForm().getVersamentoOggettoDetail().getFile_to_upload().getMaxLength();
        String dim = configurationHelper.getValoreParamApplicByApplic(nmParamMaxFileDim);
        BigDecimal maxDim;
        if (!StringUtils.isNumeric(dim)) {
            throw new ParerUserError(
                    "Errore inatteso di PreIngest: Errata configurazione della dimensione massima del file da versare");
        } else {
            maxDim = new BigDecimal(dim);
        }
        String estensioni = null;
        String[] estensioniAmmesseLowerCase = null;
        // Se versamento da trasformare
        if (tipoVersDaTrasformare) {
            estensioni = configurationHelper.getValoreParamApplicByApplic(Constants.ESTENSIONI_FILE_DA_TRASF)
                    .toLowerCase();
            estensioniAmmesseLowerCase = estensioni.split(",");
        } else {
            estensioni = Constants.ZIP_EXTENSION;
            estensioniAmmesseLowerCase = new String[1];
            estensioniAmmesseLowerCase[0] = Constants.ZIP_EXTENSION;
        }
        if (estensioniAmmesseLowerCase == null) {
            throw new ParerUserError(
                    "Errore inatteso di PreIngest: Errata configurazione delle estensioni ammesse per i file da trasformare");
        }
        if (cdKeyObjectFile.length() > maxLength) {
            throw new ParerUserError("Il nome del file da versare (compresa estensione) non pu\u00F2 superare i "
                    + maxLength + " caratteri");
            // MEV15373 - Tolto di nuovo il controllo sui più punti nel nome file
        } else if (StringUtils.countMatches(cdKeyObjectFile, ".") > 1) {
            throw new ParerUserError("Il nome del file pu\u00F2 contenere al massimo un solo carattere '.'");
        } else if (!StringUtils.endsWithAny(cdKeyObjectFile.toLowerCase(), estensioniAmmesseLowerCase)) {
            throw new ParerUserError("L'estensione del file deve essere una delle seguenti: " + estensioni);
        } else if (StringUtils.isBlank(StringUtils.substringBefore(cdKeyObjectFile, "."))) {
            throw new ParerUserError("Il nome del file deve contenere almeno un carattere");
        } else if (FileUtils.sizeOf(tmpFile) > maxDim.longValue()) {
            throw new ParerUserError(
                    "La dimensione del file non pu\u00F2 superare la dimensione massima prevista che \u00E8 pari a "
                            + dim + " Byte.");
        }
    }

    private InvioOggettoAsincronoRisposta executeInvioOggettoAsincrono(String username, String nmAmbiente,
            String nmVersatore, String nmTipoObject, String cdKeyObject, String dsObject, String cdVersioneXml,
            String xml, String nmAmbienteObjectPadre, String nmVersatoreObjectPadre, String cdKeyObjectPadre,
            BigDecimal niTotObjectFigli, BigDecimal pgObjectFiglio, String cdVersGen, String tiGestOggettiFigli,
            String tiPriorita, String tiPrioritaVersamento) {
        return invioOggettoAsincronoEjb.invioOggettoAsincronoEsteso(username, nmAmbiente, nmVersatore, cdKeyObject,
                dsObject, nmTipoObject, false, false, false, null, cdVersioneXml, xml, nmAmbienteObjectPadre,
                nmVersatoreObjectPadre, cdKeyObjectPadre, niTotObjectFigli, pgObjectFiglio, null, cdVersGen,
                tiGestOggettiFigli, tiPriorita, tiPrioritaVersamento);
    }

    private NotificaTrasferimentoRisposta executeNotificaTrasferimento(String nmAmbiente, String nmVersatore,
            String nmTipoFile, String cdKeyObject, String dsHashFileVers, String estensioneFileCaricato,
            String tiHashFileVers, Long backendId) throws ObjectStorageException {
        ListaFileDepositatoType listaFile = new ListaFileDepositatoType();
        listaFile.setFileDepositato(new ArrayList<>());

        FileDepositatoType file = new FileDepositatoType();
        file.setNmNomeFile(cdKeyObject + estensioneFileCaricato);
        file.setNmTipoFile(nmTipoFile);

        // MEV34843 - qui può essere solo backend su file, quindi non setto il nome del file per OS.

        if (StringUtils.isNotBlank(dsHashFileVers)) {
            file.setCdEncoding(TipiEncBinari.HEX_BINARY.descrivi());
            file.setTiAlgoritmoHash(tiHashFileVers);
            file.setDsHashFile(dsHashFileVers);
        } else {
            file.setCdEncoding(null);
            file.setTiAlgoritmoHash(null);
            file.setDsHashFile(null);
        }
        listaFile.getFileDepositato().add(file);

        return notificaTrasferimentoEjb.notificaAvvenutoTrasferimentoFile(nmAmbiente, nmVersatore, cdKeyObject,
                listaFile);
    }

    private String getFtpFilePath(String rootFtp, BigDecimal idVers, String cdKeyObject) throws ParerUserError {
        // COSTRUISCO IL PATH DOVE SALVARE IL FILE NELL'AREA FTP
        // ftpPath = rootFtp + inputFtp + File.separator + cdKeyObject + File.separator + cdKeyObject.zip
        StringBuilder tmpPath = new StringBuilder(rootFtp);
        PigVersRowBean versRowBean = amministrazioneEjb.getPigVersRowBean(idVers);
        String inputFtp = versRowBean.getDsPathInputFtp();
        if (StringUtils.isBlank(inputFtp)) {
            throw new ParerUserError(
                    "Errore inatteso nel caricamento del file: errata configurazione della directory FTP in INPUT per il versatore");
        }
        tmpPath.append(inputFtp).append(File.separator);
        tmpPath.append(cdKeyObject).append(File.separator);
        return tmpPath.toString();
    }

    @Override
    public JSONObject triggerFiltriVersamentiOggettoNm_ambiente_versOnTrigger() throws EMFError {
        String ambienteComponent = getForm().getFiltriVersamentiOggetto().getNm_ambiente_vers().getName();
        String versComponent = getForm().getFiltriVersamentiOggetto().getNm_vers().getName();
        String tipoObjectComponent = getForm().getFiltriVersamentiOggetto().getNm_tipo_object().getName();
        return triggerFormNm_ambiente_versOnTrigger(getForm().getFiltriVersamentiOggetto(), ambienteComponent,
                versComponent, tipoObjectComponent, Constants.TipoVersamento.DA_TRASFORMARE.name(),
                Constants.TipoVersamento.ZIP_CON_XML_SACER.name());
    }

    @Override
    public JSONObject triggerFiltriVersamentiOggettoNm_versOnTrigger() throws EMFError {
        String versComponent = getForm().getFiltriVersamentiOggetto().getNm_vers().getName();
        String tipoObjectComponent = getForm().getFiltriVersamentiOggetto().getNm_tipo_object().getName();
        return triggerFormNm_versOnTrigger(getForm().getFiltriVersamentiOggetto(), versComponent, tipoObjectComponent,
                Constants.TipoVersamento.DA_TRASFORMARE.name(), Constants.TipoVersamento.ZIP_CON_XML_SACER.name());
    }

    private JSONObject triggerFormNm_ambiente_versOnTrigger(Fields<Field> formFields, String ambienteComponent,
            String versComponent, String tipoObjectComponent, String... tipoObjectVersamento) throws EMFError {
        formFields.post(getRequest());
        final ComboBox<BigDecimal> ambienteCombo = (ComboBox<BigDecimal>) formFields.getComponent(ambienteComponent);
        final ComboBox<BigDecimal> versCombo = (ComboBox<BigDecimal>) formFields.getComponent(versComponent);
        final ComboBox<BigDecimal> tipoObjectCombo = (ComboBox<BigDecimal>) formFields
                .getComponent(tipoObjectComponent);

        BigDecimal idAmbiente = ambienteCombo.parse();
        if (idAmbiente != null) {
            PigVersTableBean versTableBean = comboHelper.getVersatoreFromAmbienteVersatore(getUser().getIdUtente(),
                    idAmbiente);
            versCombo.setDecodeMap(DecodeMap.Factory.newInstance(versTableBean, "id_vers", "nm_vers"));
            if (versTableBean.size() == 1) {
                BigDecimal idVers = versTableBean.getRow(0).getIdVers();
                versCombo.setValue(idVers.toPlainString());
                PigTipoObjectTableBean tipoObjectTableBean = comboHelper
                        .getTipoObjectFromVersatore(getUser().getIdUtente(), idVers, tipoObjectVersamento);
                tipoObjectCombo.setDecodeMap(
                        DecodeMap.Factory.newInstance(tipoObjectTableBean, "id_tipo_object", "nm_tipo_object"));
                if (tipoObjectTableBean.size() == 1) {
                    final PigTipoObjectRowBean row = tipoObjectTableBean.getRow(0);
                    tipoObjectCombo.setValue(row.getIdTipoObject().toPlainString());
                }
            } else {
                tipoObjectCombo.setDecodeMap(new DecodeMap());
            }
        } else {
            versCombo.setDecodeMap(new DecodeMap());
            tipoObjectCombo.setDecodeMap(new DecodeMap());
        }

        BigDecimal idTipoObject = getForm().getVersamentoOggettoDetail().getNm_tipo_object().parse();

        // MAC26744
        if (idTipoObject != null) {
            try {
                PigTipoObjectRowBean pigTipoObjectRowBean = amministrazioneEjb.getPigTipoObjectRowBean(idTipoObject);

                // MEV34843
                BigDecimal idVers = pigTipoObjectRowBean.getIdVers();
                PigAmbienteVersRowBean pigAmbienteVersRowBean = amministrazioneEjb.getPigAmbienteVersByVers(idVers);
                BigDecimal idAmbienteVers = pigAmbienteVersRowBean.getIdAmbienteVers();

                BackendStorage backendStorage = salvataggioBackendHelper.getBackendForVersamento(idAmbienteVers, idVers,
                        pigTipoObjectRowBean.getIdTipoObject());

                Input<String> fileToUpload = (Input<String>) formFields
                        .getComponent(getForm().getVersamentoOggettoDetail().getFile_to_upload().getName());
                ComboBox<String> flTrasmFtp = (ComboBox<String>) formFields
                        .getComponent(getForm().getVersamentoOggettoDetail().getFl_trasm_ftp().getName());

                if (fileToUpload != null) {
                    // MAC34454 nascosto anche se si trasmette via ftp.
                    fileToUpload.setHidden(
                            backendStorage.isObjectStorage() || WebConstants.DB_TRUE.equals(flTrasmFtp.getValue()));
                }

                if (flTrasmFtp != null) {
                    flTrasmFtp.setHidden(backendStorage.isObjectStorage());
                }

                Input<String> dsPathFtp = (Input<String>) formFields
                        .getComponent(getForm().getVersamentoOggettoDetail().getDs_path_ftp().getName());
                if (dsPathFtp != null) {
                    dsPathFtp.setHidden(backendStorage.isObjectStorage());
                }

                Button<String> versaOggettoButton = (Button<String>) formFields
                        .getComponent(getForm().getVersamentoOggettoDetail().getVersaOggetto().getName());
                if (versaOggettoButton != null) {
                    versaOggettoButton.setHidden(backendStorage.isObjectStorage());
                }
            } catch (ObjectStorageException ex) {
                throw new EMFError(EMFError.BLOCKING, ex.getMessage());
            }
        }

        return formFields.asJSON();
    }

    private void setTipoObjectDecodeMap(final ComboBox<BigDecimal> tipoObjectCombo,
            PigTipoObjectTableBean tipoObjectTableBean) {
        tipoObjectCombo
                .setDecodeMap(DecodeMap.Factory.newInstance(tipoObjectTableBean, "id_tipo_object", "nm_tipo_object"));
        if (tipoObjectTableBean.size() == 1) {
            final PigTipoObjectRowBean row = tipoObjectTableBean.getRow(0);
            tipoObjectCombo.setValue(row.getIdTipoObject().toPlainString());
            setTiGestOggettiFigliDecodeMap(row, Constants.TipoGestioneOggettiFigli.MANUALE.name());
        }
    }

    private JSONObject triggerFormNm_versOnTrigger(Fields<Field> formFields, String versComponent,
            String tipoObjectComponent, String... tipoObjectVersamento) throws EMFError {
        formFields.post(getRequest());
        final ComboBox<BigDecimal> versCombo = (ComboBox<BigDecimal>) formFields.getComponent(versComponent);
        final ComboBox<BigDecimal> tipoObjectCombo = (ComboBox<BigDecimal>) formFields
                .getComponent(tipoObjectComponent);

        BigDecimal idVers = versCombo.parse();
        if (idVers != null) {
            PigTipoObjectTableBean tipoObjectTableBean = comboHelper.getTipoObjectFromVersatore(getUser().getIdUtente(),
                    idVers, tipoObjectVersamento);
            tipoObjectCombo.setDecodeMap(
                    DecodeMap.Factory.newInstance(tipoObjectTableBean, "id_tipo_object", "nm_tipo_object"));
            if (tipoObjectTableBean.size() == 1) {
                final PigTipoObjectRowBean row = tipoObjectTableBean.getRow(0);
                tipoObjectCombo.setValue(row.getIdTipoObject().toPlainString());
            }
        } else {
            tipoObjectCombo.setDecodeMap(new DecodeMap());
        }
        return formFields.asJSON();
    }

    @Override
    public void insertDettaglio() throws EMFError {
        /* empty */
    }

    @Override
    public void loadDettaglio() throws EMFError {
        /* empty */
    }

    @Override
    public void undoDettaglio() throws EMFError {
        /* empty */
    }

    @Override
    public void saveDettaglio() throws EMFError {
        /* empty */
    }

    @Override
    public void dettaglioOnClick() throws EMFError {
        if (getNavigationEvent().equals(ListAction.NE_DETTAGLIO_VIEW) || getNavigationEvent().equals(ListAction.NE_NEXT)
                || getNavigationEvent().equals(ListAction.NE_PREV)) {
            if (getTableName().equals(getForm().getVersamentiOggettoList().getName())) {
                MonitoraggioForm form = new MonitoraggioForm();
                final BaseTableInterface<? extends BaseRowInterface> tb = getForm().getVersamentiOggettoList()
                        .getTable();
                form.getOggettiList().setTable(tb);
                form.getOggettiList().getTable().setCurrentRowIndex(tb.getCurrentRowIndex());
                redirectToAction(Application.Actions.MONITORAGGIO,
                        "?operation=listNavigationOnClick&navigationEvent=" + ListAction.NE_DETTAGLIO_VIEW + "&table="
                                + MonitoraggioForm.OggettiList.NAME + "&riga=" + tb.getCurrentRowIndex(),
                        form);
            }
        }
    }

    @Override
    public void elencoOnClick() throws EMFError {
        /* empty */
    }

    @Override
    protected String getDefaultPublsherName() {
        return Application.Publisher.VERSAMENTO_OGGETTO;
    }

    @Override
    public void process() throws EMFError {
        boolean isMultipart = ServletFileUpload.isMultipartContent(getRequest());
        if (isMultipart) {
            if (getLastPublisher().equals(Application.Publisher.VERSAMENTO_OGGETTO)
                    || getLastPublisher().equals(Application.Publisher.VERSAMENTO_OGGETTO_DA_TRASFORMARE)
                    || getLastPublisher().equals(Application.Publisher.VERSAMENTO_UNITA_DOCUMENTARIE)
                    || getLastPublisher().equals(Application.Publisher.VERSAMENTO_DA_ARCHIVIO)) {
                BigDecimal size = null;
                try {
                    String dim = configurationHelper
                            .getValoreParamApplicByApplic(Constants.DIM_MAX_FILE_DA_VERSARE_FTP);

                    // MEV 34015
                    if (getLastPublisher().equals(Application.Publisher.VERSAMENTO_DA_ARCHIVIO)) {
                        dim = configurationHelper.getValoreParamApplicByApplic(Constants.DIM_MAX_FILE_DA_VERSARE_ARCH);
                    }

                    size = new BigDecimal(dim);

                    String[] a = getForm().getVersamentoOggettoDetail().postMultipart(getRequest(), size.longValue());
                    if (a != null) {
                        String operationMethod = a[0];
                        String[] navigationParams = Arrays.copyOfRange(a, 1, a.length);

                        if (navigationParams != null && navigationParams.length > 0) {
                            Method method = VersamentoOggettoAction.class.getMethod(operationMethod, String[].class);
                            method.invoke(this, (Object) navigationParams);

                        } else {
                            Method method = VersamentoOggettoAction.class.getMethod(operationMethod);
                            method.invoke(this);
                        }
                    }
                } catch (FileUploadBase.FileSizeLimitExceededException ex) {
                    getMessageBox().addError(
                            "La dimensione del file non pu\u00F2 superare la dimensione massima prevista che \u00E8 pari a "
                                    + size.toPlainString() + " Byte.");
                } catch (FileUploadException | NoSuchMethodException | SecurityException | IllegalAccessException
                        | IllegalArgumentException | InvocationTargetException | ParamApplicNotFoundException ex) {
                    log.error("Errore nell'invocazione del metodo di upload :" + ExceptionUtils.getRootCauseMessage(ex),
                            ex);
                    getMessageBox().addError(ExceptionUtils.getRootCauseMessage(ex));
                    forwardToPublisher(getDefaultPublsherName());
                }
            }
        }
    }

    @Override
    public void reloadAfterGoBack(String publisherName) {
        try {
            switch (publisherName) {
            case Application.Publisher.VISUALIZZA_STATO_VERSAMENTI:
                Date dataDa = getForm().getFiltriVersamentiOggetto().getDt_vers_da().parse();
                Date dataA = getForm().getFiltriVersamentiOggetto().getDt_vers_a().parse();
                Calendar tmpCal = Calendar.getInstance();
                if (dataDa == null && dataA != null) {
                    // setto DATA_DA all' 01/01/2000
                    tmpCal.set(2000, Calendar.JANUARY, 1, 0, 0, 0);
                    tmpCal.set(Calendar.MILLISECOND, 0);
                    dataDa = tmpCal.getTime();
                } else if (dataDa != null && dataA == null) {
                    // setto DATA_A ad oggi alle 23.59
                    tmpCal.set(Calendar.HOUR_OF_DAY, 23);
                    tmpCal.set(Calendar.MINUTE, 59);
                    tmpCal.set(Calendar.SECOND, 59);
                    tmpCal.set(Calendar.MILLISECOND, 999);
                    dataA = tmpCal.getTime();
                } else if (dataA != null) {
                    // se comunque sono popolati (E in questo caso sono sicuramente popolati entrambi) setto la dataA
                    // alle 23.59
                    tmpCal.setTime(dataA);
                    tmpCal.set(Calendar.HOUR_OF_DAY, 23);
                    tmpCal.set(Calendar.MINUTE, 59);
                    tmpCal.set(Calendar.SECOND, 59);
                    tmpCal.set(Calendar.MILLISECOND, 999);
                    dataA = tmpCal.getTime();
                }
                BigDecimal idAmbiente = getForm().getFiltriVersamentiOggetto().getNm_ambiente_vers().parse();
                BigDecimal idVers = getForm().getFiltriVersamentiOggetto().getNm_vers().parse();
                BigDecimal idTipoOggetto = getForm().getFiltriVersamentiOggetto().getNm_tipo_object().parse();
                BigDecimal idOggetto = getForm().getFiltriVersamentiOggetto().getId_object().parse();
                String cdKeyObject = getForm().getFiltriVersamentiOggetto().getCd_key_object().parse();
                String dsObject = getForm().getFiltriVersamentiOggetto().getDs_object().parse();
                String tiStatoEsterno = getForm().getFiltriVersamentiOggetto().getTi_stato_esterno().parse();
                List<String> tiStatoObject = getForm().getFiltriVersamentiOggetto().getTi_stato_object().parse();
                List<String> tiVersFile = getForm().getFiltriVersamentiOggetto().getTi_vers_file().parse();
                // MEV 30343
                String note = getForm().getFiltriVersamentiOggetto().getNote().parse();

                MonVLisStatoVersTableBean monVLisStatoVersTableBean = new MonVLisStatoVersTableBean();
                try {
                    monVLisStatoVersTableBean = versamentoOggettoEjb.getMonVLisStatoVersTableBean(
                            getUser().getIdUtente(), idAmbiente, idVers, idTipoOggetto, idOggetto, cdKeyObject,
                            dsObject, dataDa, dataA, tiStatoEsterno, tiStatoObject, tiVersFile, note);
                } catch (ParerUserError e) {
                    getMessageBox().addError(e.getDescription());
                }
                int pageSize = getForm().getVersamentiOggettoList().getTable().getPageSize();
                int rowIndex = getForm().getVersamentiOggettoList().getTable().getCurrentRowIndex();

                getForm().getVersamentiOggettoList().setTable(monVLisStatoVersTableBean);
                getForm().getVersamentiOggettoList().getTable()
                        .setPageSize(pageSize == 0 ? WebConstants.DEFAULT_PAGE_SIZE : pageSize);
                getForm().getVersamentiOggettoList().getTable().setCurrentRowIndex(rowIndex);
                break;
            }
            postLoad();
        } catch (EMFError ex) {
            log.error("Errore inatteso nel caricamento della pagina " + publisherName, ex);
        }
    }

    @Override
    public String getControllerName() {
        return Application.Actions.VERSAMENTO_OGGETTO;
    }

    // XXX IN CASO DI MODIFICHE DEL CODICE UNIVOCO DEL MENU, MODIFICARE IL METODO ElencoOnClick DI MonitoraggioAction
    @Secure(action = "Menu.Versamenti.StatoVersamenti")
    public void loadStatoVersamenti() {
        getUser().getMenu().reset();
        getUser().getMenu().select("Menu.Versamenti.StatoVersamenti");

        String ambienteComponent = getForm().getFiltriVersamentiOggetto().getNm_ambiente_vers().getName();
        String versComponent = getForm().getFiltriVersamentiOggetto().getNm_vers().getName();
        String tipoObjectComponent = getForm().getFiltriVersamentiOggetto().getNm_tipo_object().getName();
        String tiStatoObjectComponent = getForm().getFiltriVersamentiOggetto().getTi_stato_object().getName();
        String tiVersFileComponent = getForm().getFiltriVersamentiOggetto().getTi_vers_file().getName();
        getForm().getFiltriVersamentiOggetto().reset();
        initForm(getForm().getFiltriVersamentiOggetto(), ambienteComponent, versComponent, tipoObjectComponent,
                tiStatoObjectComponent, tiVersFileComponent, Constants.TipoVersamento.DA_TRASFORMARE.name(),
                Constants.TipoVersamento.ZIP_CON_XML_SACER.name());
        getForm().getVersamentiOggettoList().clear();
        forwardToPublisher(Application.Publisher.VISUALIZZA_STATO_VERSAMENTI);
    }

    @Override
    public void ricercaVersamentiOggetto() throws EMFError {
        if (getForm().getFiltriVersamentiOggetto().postAndValidate(getRequest(), getMessageBox())) {
            Date dataDa = getForm().getFiltriVersamentiOggetto().getDt_vers_da().parse();
            Date dataA = getForm().getFiltriVersamentiOggetto().getDt_vers_a().parse();
            Calendar tmpCal = Calendar.getInstance();
            if (dataDa == null && dataA != null) {
                // setto DATA_DA all' 01/01/2000
                tmpCal.set(2000, Calendar.JANUARY, 1, 0, 0, 0);
                tmpCal.set(Calendar.MILLISECOND, 0);
                dataDa = tmpCal.getTime();
            } else if (dataDa != null && dataA == null) {
                // setto DATA_A ad oggi alle 23.59
                tmpCal.set(Calendar.HOUR_OF_DAY, 23);
                tmpCal.set(Calendar.MINUTE, 59);
                tmpCal.set(Calendar.SECOND, 59);
                tmpCal.set(Calendar.MILLISECOND, 999);
                dataA = tmpCal.getTime();
            } else if (dataA != null) {
                // se comunque sono popolati (E in questo caso sono sicuramente popolati entrambi) setto la dataA alle
                // 23.59
                tmpCal.setTime(dataA);
                tmpCal.set(Calendar.HOUR_OF_DAY, 23);
                tmpCal.set(Calendar.MINUTE, 59);
                tmpCal.set(Calendar.SECOND, 59);
                tmpCal.set(Calendar.MILLISECOND, 999);
                dataA = tmpCal.getTime();
            }
            BigDecimal idAmbiente = getForm().getFiltriVersamentiOggetto().getNm_ambiente_vers().parse();
            BigDecimal idVers = getForm().getFiltriVersamentiOggetto().getNm_vers().parse();
            BigDecimal idTipoOggetto = getForm().getFiltriVersamentiOggetto().getNm_tipo_object().parse();
            BigDecimal idOggetto = getForm().getFiltriVersamentiOggetto().getId_object().parse();
            String cdKeyObject = getForm().getFiltriVersamentiOggetto().getCd_key_object().parse();
            String dsObject = getForm().getFiltriVersamentiOggetto().getDs_object().parse();
            String tiStatoEsterno = getForm().getFiltriVersamentiOggetto().getTi_stato_esterno().parse();
            List<String> tiStatoObject = getForm().getFiltriVersamentiOggetto().getTi_stato_object().parse();
            List<String> tiVersFile = getForm().getFiltriVersamentiOggetto().getTi_vers_file().parse();
            // MEV 30343
            String note = getForm().getFiltriVersamentiOggetto().getNote().parse();

            MonVLisStatoVersTableBean monVLisStatoVersTableBean = new MonVLisStatoVersTableBean();
            try {
                monVLisStatoVersTableBean = versamentoOggettoEjb.getMonVLisStatoVersTableBean(getUser().getIdUtente(),
                        idAmbiente, idVers, idTipoOggetto, idOggetto, cdKeyObject, dsObject, dataDa, dataA,
                        tiStatoEsterno, tiStatoObject, tiVersFile, note);
            } catch (ParerUserError e) {
                getMessageBox().addError(e.getDescription());
            }
            getForm().getVersamentiOggettoList().setTable(monVLisStatoVersTableBean);
            getForm().getVersamentiOggettoList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
            getForm().getVersamentiOggettoList().getTable().first();
        }
        forwardToPublisher(Application.Publisher.VISUALIZZA_STATO_VERSAMENTI);
    }

    @Override
    public JSONObject triggerVersamentoOggettoDetailFl_trasm_ftpOnTrigger() throws EMFError {
        getForm().getVersamentoOggettoDetail().post(getRequest());
        String flag = getForm().getVersamentoOggettoDetail().getFl_trasm_ftp().parse();
        BigDecimal idVers = getUser().getIdOrganizzazioneFoglia();
        getForm().getVersamentoOggettoDetail().getCd_key_object().clear();
        getForm().getVersamentoOggettoDetail().getFile_to_upload().clear();
        getForm().getVersamentoOggettoDetail().getFile_to_upload().setFileItem(null);
        getForm().getVersamentoOggettoDetail().getCd_versione_xml().clear();
        getForm().getVersamentoOggettoDetail().getXml_to_upload().clear();
        getForm().getVersamentoOggettoDetail().getXml_to_upload().setFileItem(null);
        if (StringUtils.isNotBlank((flag)) && flag.equals("1") && idVers != null) {
            PigVersRowBean versRow = amministrazioneEjb.getPigVersRowBean(idVers);
            getForm().getVersamentoOggettoDetail().getDs_path_ftp().setValue(versRow.getDsPathInputFtp());

            getForm().getVersamentoOggettoDetail().getCd_key_object().setEditMode();
            getForm().getVersamentoOggettoDetail().getCd_key_object().setHidden(false);

            getForm().getVersamentoOggettoDetail().getFile_to_upload().setHidden(true);
            getForm().getVersamentoOggettoDetail().getCd_versione_xml().setHidden(true);
            getForm().getVersamentoOggettoDetail().getXml_to_upload().setHidden(true);
        } else {
            getForm().getVersamentoOggettoDetail().getDs_path_ftp().setValue(null);

            getForm().getVersamentoOggettoDetail().getCd_key_object().setHidden(true);

            getForm().getVersamentoOggettoDetail().getFile_to_upload().setHidden(false);
            getForm().getVersamentoOggettoDetail().getCd_versione_xml().setHidden(false);
            getForm().getVersamentoOggettoDetail().getXml_to_upload().setHidden(false);
        }
        return getForm().getVersamentoOggettoDetail().asJSON();
    }

    @Override
    public JSONObject triggerVersamentoOggettoDetailNm_ambiente_vers_padreOnTrigger() throws EMFError {
        String ambienteComponent = getForm().getVersamentoOggettoDetail().getNm_ambiente_vers_padre().getName();
        String versComponent = getForm().getVersamentoOggettoDetail().getNm_vers_padre().getName();
        String tipoObjectComponent = getForm().getVersamentoOggettoDetail().getNm_tipo_object_padre().getName();
        return triggerFormNm_ambiente_versOnTrigger(getForm().getVersamentoOggettoDetail(), ambienteComponent,
                versComponent, tipoObjectComponent, Constants.TipoVersamento.DA_TRASFORMARE.name());
    }

    @Override
    public JSONObject triggerVersamentoOggettoDetailNm_vers_padreOnTrigger() throws EMFError {
        String versComponent = getForm().getVersamentoOggettoDetail().getNm_vers_padre().getName();
        String tipoObjectComponent = getForm().getVersamentoOggettoDetail().getNm_tipo_object_padre().getName();
        triggerFormNm_versOnTrigger(getForm().getVersamentoOggettoDetail(), versComponent, tipoObjectComponent,
                Constants.TipoVersamento.DA_TRASFORMARE.name());
        BigDecimal idTipoObjectPadre = getForm().getVersamentoOggettoDetail().getNm_tipo_object_padre().parse();
        if (idTipoObjectPadre != null) {
            final BaseTableInterface<?> columnFromPigObjectTableBean = versamentoOggettoEjb
                    .getColumnFromPigObjectTableBean(idTipoObjectPadre, String[].class, "id_object", "cd_key_object",
                            "ds_object");
            columnFromPigObjectTableBean.addSortingRule("cd_key_object", SortingRule.ASC);
            columnFromPigObjectTableBean.sort();
            getForm().getVersamentoOggettoDetail().getCd_key_object_padre().setDecodeMap(
                    DecodeMap.Factory.newInstance(columnFromPigObjectTableBean, "id_object", "cd_key_object"));
        } else {
            getForm().getVersamentoOggettoDetail().getCd_key_object_padre().setDecodeMap(new DecodeMap());
        }
        return getForm().getVersamentoOggettoDetail().asJSON();
    }

    @Override
    public JSONObject triggerVersamentoOggettoDetailNm_tipo_object_padreOnTrigger() throws EMFError {
        getForm().getVersamentoOggettoDetail().post(getRequest());
        BigDecimal idTipoObjectPadre = getForm().getVersamentoOggettoDetail().getNm_tipo_object_padre().parse();

        if (idTipoObjectPadre != null) {
            final BaseTableInterface<?> columnFromPigObjectTableBean = versamentoOggettoEjb
                    .getColumnFromPigObjectTableBean(idTipoObjectPadre, String[].class, "id_object", "cd_key_object",
                            "ds_object");
            columnFromPigObjectTableBean.addSortingRule("cd_key_object", SortingRule.ASC);
            columnFromPigObjectTableBean.sort();
            getForm().getVersamentoOggettoDetail().getCd_key_object_padre().setDecodeMap(
                    DecodeMap.Factory.newInstance(columnFromPigObjectTableBean, "id_object", "cd_key_object"));
        } else {
            getForm().getVersamentoOggettoDetail().getCd_key_object_padre().setDecodeMap(new DecodeMap());
        }
        return getForm().getVersamentoOggettoDetail().asJSON();
    }

    @Override
    public JSONObject triggerVersamentoOggettoDetailCd_key_object_padreOnTrigger() throws EMFError {
        getForm().getVersamentoOggettoDetail().post(getRequest());
        BigDecimal idObjectPadre = getForm().getVersamentoOggettoDetail().getCd_key_object_padre().parse();
        if (idObjectPadre != null) {
            try {
                PigObjectRowBean object = versamentoOggettoEjb.getPigObjectRowBean(idObjectPadre);
                getForm().getVersamentoOggettoDetail().getDs_object_padre().setValue(object.getDsObject());
                if (object.getNiTotObjectTrasf() != null) {
                    getForm().getVersamentoOggettoDetail().getNi_tot_object_trasf()
                            .setValue(object.getNiTotObjectTrasf().toPlainString());
                    getForm().getVersamentoOggettoDetail().getNi_tot_object_trasf().setReadonly(true);
                } else {
                    getForm().getVersamentoOggettoDetail().getNi_tot_object_trasf().setValue(null);
                    getForm().getVersamentoOggettoDetail().getNi_tot_object_trasf().setReadonly(false);
                    getForm().getVersamentoOggettoDetail().getNi_tot_object_trasf().setEditMode();
                }
            } catch (ParerUserError ex) {
                getMessageBox().addError("Errore inatteso nel caricamento dei dati");
            }
        } else {
            getForm().getVersamentoOggettoDetail().getDs_object_padre().setValue(null);
            getForm().getVersamentoOggettoDetail().getNi_tot_object_trasf().setValue(null);
            getForm().getVersamentoOggettoDetail().getNi_tot_object_trasf().setReadonly(false);
            getForm().getVersamentoOggettoDetail().getNi_tot_object_trasf().setEditMode();
        }
        return getForm().getVersamentoOggettoDetail().asJSON();
    }

    public void triggerVersamentoOggettoDetailNm_tipo_objectOnTrigger() throws EMFError {
        // MEV 32647 - per bloccare il trigger js su questo campo al primo caricamento
        if (getSession().getAttribute("nm_tipo_object_dont_trigger") == null) {

            getForm().getVersamentoOggettoDetail().post(getRequest());
            BigDecimal idTipoObject = getForm().getVersamentoOggettoDetail().getNm_tipo_object().parse();

            getForm().getVersamentoOggettoDetail().getCd_key_object().setHidden(true);
            getForm().getVersamentoOggettoDetail().getFl_trasm_ftp().setValue("0");
            getForm().getVersamentoOggettoDetail().getDs_path_ftp().setValue("");

            getForm().getVersamentoOggettoDetail().getTi_priorita_versamento().setValue("");
            getForm().getVersamentoOggettoDetail().getTi_priorita_versamento().setHidden(true);

            if (idTipoObject != null) {
                try {
                    getForm().getVersamentoOggettoDetail().getTi_priorita_versamento().setHidden(true);

                    PigTipoObjectRowBean pigTipoObjectRowBean = amministrazioneEjb
                            .getPigTipoObjectRowBean(idTipoObject);
                    setReadonlyVersamentoOggettoDaTrasformare(pigTipoObjectRowBean.getTiVersFile()
                            .equals(Constants.TipoVersamento.DA_TRASFORMARE.name()));
                    setTiGestOggettiFigliDecodeMap(pigTipoObjectRowBean,
                            Constants.TipoGestioneOggettiFigli.AUTOMATICA.name());

                    // MEV34843
                    BigDecimal idVers = pigTipoObjectRowBean.getIdVers();
                    PigAmbienteVersRowBean pigAmbienteVersRowBean = amministrazioneEjb.getPigAmbienteVersByVers(idVers);
                    BigDecimal idAmbienteVers = pigAmbienteVersRowBean.getIdAmbienteVers();

                    BackendStorage backendStorage = salvataggioBackendHelper.getBackendForVersamento(idAmbienteVers,
                            idVers, pigTipoObjectRowBean.getIdTipoObject());

                    // MEV27034
                    DecodeMap cdVersioneXmlDecodeMap = DecodeMap.Factory.newInstance(
                            amministrazioneEjb.getPigXsdDatiSpecTableBean(idTipoObject, null), "cd_versione_xsd",
                            "cd_versione_xsd");
                    getForm().getVersamentoOggettoDetail().getCd_versione_xml().setDecodeMap(cdVersioneXmlDecodeMap);

                    if (pigTipoObjectRowBean.getTiVersFile().equals(Constants.TipoVersamento.DA_TRASFORMARE.name())) {

                        triggerVersamentoOggettoDetailNm_tipo_objectOnTriggerCommonDaTrasformare(pigTipoObjectRowBean,
                                backendStorage.isObjectStorage());

                        JSONObject jsonForm = getForm().getVersamentoOggettoDetail().asJSON();
                        try {
                            // MEV#21995 aggiungi l'informazione su Object Storage alla risposta.
                            jsonForm.put("useObjectStorage", backendStorage.isObjectStorage());
                            // MEV#25602
                            jsonForm.put("isDaTrasformare", true);

                        } catch (Exception ex) {
                            log.warn("VERSAMENTO_SU_OBJECT_STORAGE configurato."
                                    + getForm().getVersamentoOggettoDetail().getNm_tipo_object());
                        }

                        redirectToAjax(jsonForm);

                        return;

                    } else {

                        triggerVersamentoOggettoDetailNm_tipo_objectOnTriggerCommonNonDaTrasformare(
                                pigTipoObjectRowBean, backendStorage.isObjectStorage());

                        JSONObject jsonForm = getForm().getVersamentoOggettoDetail().asJSON();
                        try {

                            // MEV#21995 aggiungi l'informazione su Object Storage alla risposta.
                            jsonForm.put("useObjectStorage", backendStorage.isObjectStorage());

                            // MEV#25602
                            jsonForm.put("isDaTrasformare", false);

                        } catch (Exception ex) {
                            log.warn("VERSAMENTO_SU_OBJECT_STORAGE configurato."
                                    + getForm().getVersamentoOggettoDetail().getNm_tipo_object());
                        }

                        redirectToAjax(jsonForm);

                        return;
                    }
                } catch (ObjectStorageException ex) {
                    throw new EMFError(EMFError.BLOCKING, ex.getMessage());
                }

            } else {
                setReadonlyVersamentoOggettoDaTrasformare(false);
                getForm().getVersamentoOggettoDetail().getTi_gest_oggetti_figli().setDecodeMap(new DecodeMap());
                getForm().getVersamentoOggettoDetail().getTi_gest_oggetti_figli().setValue(null);
                getForm().getVersamentoOggettoDetail().getTi_priorita_versamento().setHidden(true);
                getForm().getVersamentoOggettoDetail().getTi_priorita().setHidden(true);
                getForm().getVersamentoOggettoDetail().getFile_to_upload().setHidden(true);
                getForm().getVersamentoOggettoDetail().getFl_trasm_ftp().setHidden(false);
                getForm().getVersamentoOggettoDetail().getDs_path_ftp().setHidden(false);
            }
            redirectToAjax(getForm().getVersamentoOggettoDetail().asJSON());
        } else {
            getSession().removeAttribute("nm_tipo_object_dont_trigger");
        }
    }

    private void triggerVersamentoOggettoDetailNm_tipo_objectOnTriggerCommonDaTrasformare(
            PigTipoObjectRowBean pigTipoObjectRowBean, boolean useObjectStorage) throws EMFError {
        getForm().getVersamentoOggettoDetail().getTi_priorita().setHidden(false);
        getForm().getVersamentoOggettoDetail().getTi_priorita().setDecodeMap(ComboGetter.getMappaOrdinalGenericEnum(
                "ti_priorita", it.eng.sacerasi.web.util.Constants.ComboFlagPrioTrasfType.values()));
        getForm().getVersamentoOggettoDetail().getTi_priorita()
                .setValue(it.eng.sacerasi.web.util.Constants.ComboFlagPrioTrasfType
                        .getEnumByString(pigTipoObjectRowBean.getTiPriorita()));

        // MEV26969
        getForm().getVersamentoOggettoDetail().getCd_versione_xml().setHidden(false);
        getForm().getVersamentoOggettoDetail().getXml_to_upload().setHidden(false);
        // MEV25601
        setReadonlyVersamentoOggettoDaTrasformare(true);

        getForm().getVersamentoOggettoDetail().getFile_to_upload().setHidden(useObjectStorage);
        getForm().getVersamentoOggettoDetail().getFl_trasm_ftp().setHidden(useObjectStorage);
        getForm().getVersamentoOggettoDetail().getDs_path_ftp().setHidden(useObjectStorage);

        // MAC 33955
        getForm().getVersamentoOggettoDetail().getTi_gest_oggetti_figli().setHidden(false);
    }

    private void triggerVersamentoOggettoDetailNm_tipo_objectOnTriggerCommonNonDaTrasformare(
            PigTipoObjectRowBean pigTipoObjectRowBean, boolean useObjectStorage) throws EMFError {
        getForm().getVersamentoOggettoDetail().getTi_priorita().setHidden(true);
        getForm().getVersamentoOggettoDetail().getTi_priorita().setValue("");

        // MEV25601
        getForm().getVersamentoOggettoDetail().getCd_versione_xml().setHidden(false);
        getForm().getVersamentoOggettoDetail().getXml_to_upload().setHidden(false);
        setReadonlyVersamentoOggettoDaTrasformare(false);

        // MEV25602
        getForm().getVersamentoOggettoDetail().getFile_to_upload().setHidden(useObjectStorage);
        getForm().getVersamentoOggettoDetail().getFl_trasm_ftp().setHidden(useObjectStorage);
        getForm().getVersamentoOggettoDetail().getDs_path_ftp().setHidden(useObjectStorage);

        // MEV#27321
        if (pigTipoObjectRowBean.getTiVersFile().equals(Constants.TipoVersamento.NO_ZIP.name())
                || pigTipoObjectRowBean.getTiVersFile().equals(Constants.TipoVersamento.ZIP_CON_XML_SACER.name())) {
            getForm().getVersamentoOggettoDetail().getTi_priorita_versamento().setHidden(false);
            getForm().getVersamentoOggettoDetail().getTi_priorita_versamento()
                    .setValue(it.eng.sacerasi.web.util.Constants.ComboFlagPrioVersType
                            .getEnumByString(pigTipoObjectRowBean.getTiPrioritaVersamento()));
        }

        getForm().getVersamentoOggettoDetail().getTi_gest_oggetti_figli().setHidden(true);
    }

    private void setTiGestOggettiFigliDecodeMap(PigTipoObjectRowBean pigTipoObjectRowBean, String defaultTiGest) {
        if (pigTipoObjectRowBean.getTiVersFile().equals(Constants.TipoVersamento.DA_TRASFORMARE.name())) {
            getForm().getVersamentoOggettoDetail().getTi_gest_oggetti_figli().setDecodeMap(ComboGetter
                    .getMappaSortedGenericEnum("ti_gest_oggetti_figli", Constants.TipoGestioneOggettiFigli.values()));
            getForm().getVersamentoOggettoDetail().getTi_gest_oggetti_figli().setValue(defaultTiGest);
        } else {
            getForm().getVersamentoOggettoDetail().getTi_gest_oggetti_figli().setDecodeMap(new DecodeMap());
            getForm().getVersamentoOggettoDetail().getTi_gest_oggetti_figli().setValue(null);
        }
    }

    private void setReadonlyVersamentoOggettoDaTrasformare(boolean readonly) {
        getForm().getVersamentoOggettoDetail().getNm_ambiente_vers_padre().setReadonly(readonly);
        getForm().getVersamentoOggettoDetail().getNm_ambiente_vers_padre().clear();
        getForm().getVersamentoOggettoDetail().getNm_vers_padre().setReadonly(readonly);
        getForm().getVersamentoOggettoDetail().getNm_vers_padre().clear();
        getForm().getVersamentoOggettoDetail().getNm_tipo_object_padre().setReadonly(readonly);
        getForm().getVersamentoOggettoDetail().getNm_tipo_object_padre().clear();
        getForm().getVersamentoOggettoDetail().getCd_key_object_padre().setReadonly(readonly);
        getForm().getVersamentoOggettoDetail().getCd_key_object_padre().clear();
        getForm().getVersamentoOggettoDetail().getDs_object_padre().setReadonly(readonly);
        getForm().getVersamentoOggettoDetail().getDs_object_padre().reset();
        getForm().getVersamentoOggettoDetail().getNi_tot_object_trasf().setReadonly(readonly);
        getForm().getVersamentoOggettoDetail().getNi_tot_object_trasf().reset();
        getForm().getVersamentoOggettoDetail().getPg_oggetto_trasf().setReadonly(readonly);
        getForm().getVersamentoOggettoDetail().getPg_oggetto_trasf().reset();
    }

    @Override
    public void nuovoVersamento() throws EMFError {
        /*
         * MAC#15954 - Versamento oggetto: malfunzionamento del pulsante Nuovo versamento
         */
        if (getLastPublisher().equals(Application.Publisher.VERSAMENTO_OGGETTO)) {
            loadVersamentoOggetto();
        } else if (getLastPublisher().equals(Application.Publisher.VERSAMENTO_OGGETTO_DA_TRASFORMARE)) {
            loadVersamentoOggettoDaTrasf();
        } else if (getLastPublisher().equals(Application.Publisher.VERSAMENTO_UNITA_DOCUMENTARIE)) {
            loadVersamentoUnitaDoc();
        }
    }

    @Override
    protected void postLoad() {
        super.postLoad();
        if (getForm() instanceof VersamentoOggettoForm) {
            if (getForm().getVersamentoOggettoDetail().getStatus() != null) {
                if (getForm().getVersamentoOggettoDetail().getStatus().equals(Status.view)) {
                    getForm().getVersamentoOggettoDetail().getNuovoVersamento().setEditMode();
                    getForm().getVersamentoOggettoDetail().getNuovoVersamento().setHidden(false);
                } else {
                    getForm().getVersamentoOggettoDetail().getNuovoVersamento().setViewMode();
                    getForm().getVersamentoOggettoDetail().getNuovoVersamento().setHidden(true);
                }
            }
        }
    }

    private TipiHash impostaAlgoHash(String hash) {
        TipiHash trovato = null;
        switch (TipiHash.evaluateByLenght(BinEncUtility.decodeUTF8HexString(hash).length)) {
        case MD5:
            trovato = TipiHash.MD5;
            break;
        case SHA_1:
            trovato = TipiHash.SHA_1;
            break;
        case SHA_224:
            trovato = TipiHash.SHA_224;
            break;
        case SHA_256:
            trovato = TipiHash.SHA_256;
            break;
        case SHA_384:
            trovato = TipiHash.SHA_384;
            break;
        case SHA_512:
            trovato = TipiHash.SHA_512;
            break;
        default:
            trovato = TipiHash.SCONOSCIUTO;
            break;
        }
        return trovato;
    }

    // private boolean isUsingObjectStorage(PigTipoObjectRowBean pigTipoObjectRowBean) throws EMFError {
    // // MEV#21995/#25602 - se il tipo oggetto/versatore/ambiente sono configurati per OS allora attiviamo la
    // // modalità corretta
    // BigDecimal idVers = pigTipoObjectRowBean.getIdVers();
    // PprivateigAmbienteVersRowBean pigAmbienteVersRowBean = amministrazioneEjb.getPigAmbienteVersByVers(idVers);
    // BigDecimal idAmbienteVers = pigAmbienteVersRowBean.getIdAmbienteVers();
    //
    // // MEV25601
    // Boolean useObjectStorage = configurationHelper
    // .getValoreParamApplicByTipoObj(Constants.VERSAMENTO_SU_OBJECT_STORAGE, idAmbienteVers, idVers,
    // pigTipoObjectRowBean.getIdTipoObject())
    // .equals(Constants.PARAMETRO_OBJECT_STORAGE_ATTIVO);
    //
    // return useObjectStorage;
    // }
}
