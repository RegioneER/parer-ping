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

import static it.eng.spagoCore.configuration.ConfigProperties.StandardProperty.IMPORT_VERSATORE_MAX_FILE_SIZE;
import static it.eng.spagoCore.configuration.ConfigProperties.StandardProperty.LOAD_XSD_APP_MAX_FILE_SIZE;
import static it.eng.spagoCore.configuration.ConfigProperties.StandardProperty.LOAD_XSD_APP_MAX_REQUEST_SIZE;
import static it.eng.spagoCore.configuration.ConfigProperties.StandardProperty.LOAD_XSD_APP_UPLOAD_DIR;
import static it.eng.spagoCore.configuration.ConfigProperties.StandardProperty.PARAMS_CSV_MAX_FILE_SIZE;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.ejb.EJB;
import javax.jms.JMSException;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.csvreader.CsvReader;

import it.eng.parer.sacerlog.ejb.SacerLogEjb;
import it.eng.parer.sacerlog.ejb.common.helper.ParamApplicHelper;
import it.eng.parer.sacerlog.slite.gen.form.GestioneLogEventiForm;
import it.eng.parer.sacerlog.util.LogParam;
import it.eng.parer.sacerlog.util.web.SpagoliteLogUtil;
import it.eng.sacerasi.exception.IncoherenceException;
import it.eng.sacerasi.exception.ParamApplicNotFoundException;
import it.eng.sacerasi.exception.ParerInternalError;
import it.eng.sacerasi.exception.ParerUserError;
import it.eng.sacerasi.job.coda.dto.InfoCoda;
import it.eng.sacerasi.slite.gen.Application;
import it.eng.sacerasi.slite.gen.Application.Publisher;
import it.eng.sacerasi.slite.gen.action.AmministrazioneAbstractAction;
import it.eng.sacerasi.slite.gen.form.AmministrazioneForm;
import it.eng.sacerasi.slite.gen.form.AmministrazioneForm.AmbienteVers;
import it.eng.sacerasi.slite.gen.form.AmministrazioneForm.AttribDatiSpec;
import it.eng.sacerasi.slite.gen.form.AmministrazioneForm.SopClass;
import it.eng.sacerasi.slite.gen.form.AmministrazioneForm.TipoFileObject;
import it.eng.sacerasi.slite.gen.form.AmministrazioneForm.TipoObject;
import it.eng.sacerasi.slite.gen.form.AmministrazioneForm.Vers;
import it.eng.sacerasi.slite.gen.form.AmministrazioneForm.VisAmbienteVers;
import it.eng.sacerasi.slite.gen.form.AmministrazioneForm.VisCoda;
import it.eng.sacerasi.slite.gen.form.AmministrazioneForm.VisSopClass;
import it.eng.sacerasi.slite.gen.form.AmministrazioneForm.VisVers;
import it.eng.sacerasi.slite.gen.form.EntiConvenzionatiForm;
import it.eng.sacerasi.slite.gen.tablebean.PigAmbienteVersRowBean;
import it.eng.sacerasi.slite.gen.tablebean.PigAmbienteVersTableBean;
import it.eng.sacerasi.slite.gen.tablebean.PigAmbienteVersTableDescriptor;
import it.eng.sacerasi.slite.gen.tablebean.PigAttribDatiSpecRowBean;
import it.eng.sacerasi.slite.gen.tablebean.PigAttribDatiSpecTableBean;
import it.eng.sacerasi.slite.gen.tablebean.PigDichVersSacerRowBean;
import it.eng.sacerasi.slite.gen.tablebean.PigDichVersSacerTipoObjRowBean;
import it.eng.sacerasi.slite.gen.tablebean.PigParamApplicRowBean;
import it.eng.sacerasi.slite.gen.tablebean.PigParamApplicTableBean;
import it.eng.sacerasi.slite.gen.tablebean.PigParamApplicTableDescriptor;
import it.eng.sacerasi.slite.gen.tablebean.PigSopClassDicomRowBean;
import it.eng.sacerasi.slite.gen.tablebean.PigSopClassDicomTableBean;
import it.eng.sacerasi.slite.gen.tablebean.PigSopClassDicomTableDescriptor;
import it.eng.sacerasi.slite.gen.tablebean.PigStatoObjectRowBean;
import it.eng.sacerasi.slite.gen.tablebean.PigStoricoVersAmbienteTableBean;
import it.eng.sacerasi.slite.gen.tablebean.PigTipoFileObjectRowBean;
import it.eng.sacerasi.slite.gen.tablebean.PigTipoFileObjectTableBean;
import it.eng.sacerasi.slite.gen.tablebean.PigTipoObjectRowBean;
import it.eng.sacerasi.slite.gen.tablebean.PigTipoObjectTableBean;
import it.eng.sacerasi.slite.gen.tablebean.PigValoreSetParamTrasfRowBean;
import it.eng.sacerasi.slite.gen.tablebean.PigVersRowBean;
import it.eng.sacerasi.slite.gen.tablebean.PigVersTableBean;
import it.eng.sacerasi.slite.gen.tablebean.PigVersTipoObjectDaTrasfRowBean;
import it.eng.sacerasi.slite.gen.tablebean.PigVersTipoObjectDaTrasfTableBean;
import it.eng.sacerasi.slite.gen.tablebean.PigXsdDatiSpecRowBean;
import it.eng.sacerasi.slite.gen.tablebean.PigXsdDatiSpecTableBean;
import it.eng.sacerasi.slite.gen.tablebean.SIOrgEnteConvenzOrgRowBean;
import it.eng.sacerasi.slite.gen.tablebean.SIOrgEnteConvenzOrgTableBean;
import it.eng.sacerasi.slite.gen.tablebean.XfoSetParamTrasfRowBean;
import it.eng.sacerasi.slite.gen.viewbean.PigVRicVersRowBean;
import it.eng.sacerasi.slite.gen.viewbean.PigVRicVersTableBean;
import it.eng.sacerasi.slite.gen.viewbean.PigVValParamTrasfDefSpecRowBean;
import it.eng.sacerasi.slite.gen.viewbean.PigVValParamTrasfDefSpecTableBean;
import it.eng.sacerasi.slite.gen.viewbean.PigVValoreSetParamTrasfRowBean;
import it.eng.sacerasi.slite.gen.viewbean.PigVValoreSetParamTrasfTableBean;
import it.eng.sacerasi.slite.gen.viewbean.UsrVAbilStrutSacerXpingTableBean;
import it.eng.sacerasi.test.MonitorCoda;
import it.eng.sacerasi.util.DateUtil;
import it.eng.sacerasi.util.SacerLogConstants;
import it.eng.sacerasi.web.ejb.AmministrazioneEjb;
import it.eng.sacerasi.web.helper.AmministrazioneHelper;
import it.eng.sacerasi.web.helper.ConfigurationHelper;
import it.eng.sacerasi.web.util.ComboGetter;
import it.eng.sacerasi.web.util.Constants;
import it.eng.sacerasi.web.util.Constants.ComboValueParamentersType;
import it.eng.sacerasi.web.util.Constants.NomeCoda;
import it.eng.sacerasi.web.util.Constants.TipoSelettore;
import it.eng.sacerasi.web.util.Util;
import it.eng.sacerasi.web.util.WebConstants;
import it.eng.sacerasi.web.validator.AmministrazioneValidator;
import it.eng.spagoCore.configuration.ConfigSingleton;
import it.eng.spagoCore.error.EMFError;
import it.eng.spagoLite.ExecutionHistory;
import it.eng.spagoLite.SessionManager;
import it.eng.spagoLite.actions.form.ListAction;
import it.eng.spagoLite.db.base.BaseRowInterface;
import it.eng.spagoLite.db.base.BaseTableInterface;
import it.eng.spagoLite.db.base.row.BaseRow;
import it.eng.spagoLite.db.base.sorting.SortingRule;
import it.eng.spagoLite.db.base.table.BaseTable;
import it.eng.spagoLite.db.oracle.decode.DecodeMap;
import it.eng.spagoLite.db.oracle.decode.DecodeMap.Factory;
import it.eng.spagoLite.form.base.BaseElements.Status;
import it.eng.spagoLite.message.Message;
import it.eng.spagoLite.message.Message.MessageLevel;
import it.eng.spagoLite.message.MessageBox;
import it.eng.spagoLite.message.MessageBox.ViewMode;
import it.eng.spagoLite.security.Secure;

public class AmministrazioneAction extends AmministrazioneAbstractAction {

    @EJB(mappedName = "java:app/SacerAsync-ejb/AmministrazioneEjb")
    private AmministrazioneEjb amministrazioneEjb;
    @EJB(mappedName = "java:app/SacerAsync-ejb/AmministrazioneHelper")
    private AmministrazioneHelper amministrazioneHelper;
    @EJB(mappedName = "java:app/SacerAsync-ejb/MonitorCoda")
    private MonitorCoda monitorCoda;
    @EJB(mappedName = "java:app/SacerAsync-ejb/ConfigurationHelper")
    private ConfigurationHelper configHelper;
    @EJB(mappedName = "java:app/sacerlog-ejb/ParamApplicHelper")
    private ParamApplicHelper paramApplicHelper;
    @EJB(mappedName = "java:app/sacerlog-ejb/SacerLogEjb")
    private SacerLogEjb sacerLogEjb;
    private static final Logger log = LoggerFactory.getLogger(AmministrazioneAction.class);
    @EJB(mappedName = "java:app/SacerAsync-ejb/ConfigurationHelper")
    private ConfigurationHelper configurationHelper;

    private static final String AMMINISTRAZIONE = "amministrazione";
    private static final String CONSERVAZIONE = "conservazione";
    private static final String GESTIONE = "gestione";

    @Override
    public void initOnClick() throws EMFError {
        // per ora non serve
    }

    private class MsgCounters {

        int msgConsumedNum = 0;
        int msgDeliveredNum = 0;
        int totMsg = 0;

        List<String> deliveredMsgs = new ArrayList<>();
        List<String> consumedMsgs = new ArrayList<>();

        public List<String> getDeliveredMsgs() {
            return deliveredMsgs;
        }

        public List<String> getConsumedMsgs() {
            return consumedMsgs;
        }

        public void addDeliveredMsg(String idMsg) {
            deliveredMsgs.add(idMsg);
        }

        public void addConsumedMsg(String idMsg) {
            consumedMsgs.add(idMsg);
        }

        public void setTotMsg(int totMsg) {
            this.totMsg = totMsg;
        }

        public int getTotMsg() {
            return totMsg;
        }

        public void increaseTotMsg() {
            totMsg += 1;
        }

        public void increaseMsgConsumedNum(int increase) {
            msgConsumedNum += increase;
        }

        public void increaseMsgDeliveredNum(int increase) {
            msgDeliveredNum += increase;
        }

        public int getMsgConsumedNum() {
            return msgConsumedNum;
        }

        public void setMsgConsumedNum(int msgConsumedNum) {
            this.msgConsumedNum = msgConsumedNum;
        }

        public int getMsgDeliveredNum() {
            return msgDeliveredNum;
        }

        public void setMsgDeliveredNum(int msgDeliveredNum) {
            this.msgDeliveredNum = msgDeliveredNum;
        }
    }

    public void loadVersDaMenu() {
        SessionManager.clearActionHistory(getSession());

        AmministrazioneForm form = new AmministrazioneForm();

        BigDecimal idVers = getUser().getIdOrganizzazioneFoglia();
        PigVersRowBean row = amministrazioneEjb.getPigVersRowBean(idVers);
        PigVersTableBean table = new PigVersTableBean();
        table.add(row);
        form.getVersList().setTable(table);

        getSession().setAttribute("loadVersDaMenu", true);

        setLastPublisher(Application.Publisher.VERS_DETAIL);
        setTableName(form.getVersList().getName());
        setForm(form);
        SessionManager.addPrevExecutionToHistory(this.getSession(), false, true, null);
        SessionManager.setCurrentAction(this.getSession(), Application.Actions.AMMINISTRAZIONE);
        forwardToAction(Application.Actions.AMMINISTRAZIONE + "?operation=listNavigationOnClick&navigationEvent="
                + NE_DETTAGLIO_VIEW + "&table=" + form.getVersList().getName() + "&riga=0");
    }

    @Override
    public void process() throws EMFError {

        boolean isMultipart = ServletFileUpload.isMultipartContent(getRequest());

        if (isMultipart) {

            if (getLastPublisher().equals(Application.Publisher.XSD_DATI_SPEC_DETAIL)) {
                readXsdDatiSpecForm();
            }
            if (getLastPublisher().equals(Application.Publisher.DUPLICA_VERS_DETAIL)) {
                gestisciImportXml();
            }
            if (getLastPublisher().equals(Application.Publisher.DETTAGLIO_SET_PARAMETRI_VERSATORE)) {
                try {
                    int fileSize = ConfigSingleton.getInstance().getIntValue(PARAMS_CSV_MAX_FILE_SIZE.name());
                    // FIXME alcuni di questi input sono dentro la sezione SetParametriVersatoreDetail anche se
                    // dovrebbero essere
                    // da un'altra parte ma causa bug postMultipart può essere chimata solo su una sezione poi cancella
                    // i dati
                    getForm().getSetParametriVersatoreDetail().postMultipart(getRequest(), fileSize);
                } catch (FileUploadException ex) {
                    throw new EMFError(EMFError.BLOCKING, "Errore nell'upload del file CSV", ex);
                }

                String action = getForm().getSetParametriVersatoreDetail().getButton_action().parse();

                if (action != null && action.equals("caricaParametriDaCSV")) {
                    processParametriDaCSV();
                } else if (action != null && action.equals("eliminaSetParametriVersatore")) {
                    eliminaSetParametriVersatore();
                }

                forwardToPublisher(getLastPublisher());
            }
        }
    }

    @Override
    public String getControllerName() {
        return Application.Actions.AMMINISTRAZIONE;
    }

    @Override
    protected String getDefaultPublsherName() {
        return Application.Publisher.CERCA_AMBIENTE_VERS;
    }

    @Override
    public void loadDettaglio() throws EMFError {
        try {
            String lista = getTableName();
            String action = getNavigationEvent();
            /* Eseguo il caricamenento dei dati solo se non sono in INSERT */
            if (lista != null && (action != null && !action.equals(NE_DETTAGLIO_INSERT))) {
                /* Se ho cliccato su Lista Ambiente Versatore */
                if (lista.equals(getForm().getAmbienteVersList().getName())
                        && (getForm().getAmbienteVersList().getTable() != null)
                        && (getForm().getAmbienteVersList().getTable().size() > 0)) {
                    initAmbienteCombo();
                    BigDecimal idAmbienteVers = ((PigAmbienteVersRowBean) getForm().getAmbienteVersList().getTable()
                            .getCurrentRow()).getIdAmbienteVers();
                    loadAmbienteVers(idAmbienteVers);

                } /* Se ho cliccato su Lista Versatore */ else if (lista.equals(getForm().getVersList().getName())
                        && (getForm().getVersList().getTable() != null)
                        && (getForm().getVersList().getTable().size() > 0)) {

                    populateComboVers();
                    BigDecimal idVers = ((BaseRowInterface) getForm().getVersList().getTable().getCurrentRow())
                            .getBigDecimal("id_vers");

                    loadVers(idVers);
                } /* Se ho cliccato su Lista Tipo Object */ else if (lista.equals(
                        getForm().getTipoObjectList().getName()) && (getForm().getTipoObjectList().getTable() != null)
                        && (getForm().getTipoObjectList().getTable().size() > 0)) {

                    BigDecimal idTipoObj = ((PigTipoObjectRowBean) getForm().getTipoObjectList().getTable()
                            .getCurrentRow()).getIdTipoObject();

                    loadTipoOggetto(idTipoObj);

                    // MEV25814
                    if (action.equals(NE_DETTAGLIO_UPDATE)) {
                        // se diverso da NO_ZIP
                        if (!getForm().getTipoObject().getTi_vers_file().parse()
                                .equals(it.eng.sacerasi.common.Constants.TipoVersamento.NO_ZIP.name())) {
                            getForm().getTipoObject().getFl_crea_tipofile().setHidden(false);
                            getForm().getTipoObject().getFl_crea_tipofile().setEditMode();
                            getForm().getTipoObject().getFl_crea_tipofile().setValue("0");
                        } else {
                            getForm().getTipoObject().getFl_crea_tipofile().setHidden(true);
                            getForm().getTipoObject().getFl_crea_tipofile().setViewMode();
                            getForm().getTipoObject().getFl_crea_tipofile().setValue("0");
                        }
                    } else {
                        getForm().getTipoObject().getFl_crea_tipofile().setHidden(true);
                        getForm().getTipoObject().getFl_crea_tipofile().setViewMode();
                    }

                } /* Se ho cliccato su Lista Tipo File Object */ else if (lista
                        .equals(getForm().getTipoFileObjectList().getName())
                        && (getForm().getTipoFileObjectList().getTable() != null)) {

                    getForm().getTipoFileObject().setViewMode();
                    getForm().getTipoObject().getFl_contr_hash().setHidden(true); // perchè è qui?

                    if (!getForm().getTipoObject().getTi_vers_file().parse()
                            .equals(it.eng.sacerasi.common.Constants.TipoVersamento.NO_ZIP.name())) {
                        getForm().getConfigurazioneInSacer().setHidden(true);
                        hideFieldsTipoFileObj(true);
                        getForm().getTipoFileObject().getFl_vers_sacer_asinc().setValue("SI");
                    } else {
                        getForm().getConfigurazioneInSacer().setHidden(false);
                        hideFieldsTipoFileObj(false);
                        getForm().getTipoFileObject().getFl_vers_sacer_asinc().setValue("NO");
                    }

                    if (getForm().getTipoFileObjectList().getTable().size() > 0) {

                        BigDecimal idTipoFileObj = ((PigTipoFileObjectRowBean) getForm().getTipoFileObjectList()
                                .getTable().getCurrentRow()).getIdTipoFileObject();
                        PigTipoFileObjectRowBean tipoFileObjRowBean = amministrazioneEjb
                                .getPigTipoFileObjectRowBean(idTipoFileObj);

                        // Azzero idTipoObject ed imposto idTipoFileObject in IdList
                        getForm().getIdList().getId_tipo_object().clear();
                        getForm().getIdList().getId_tipo_file_object().setValue(idTipoFileObj.toString());

                        getForm().getTipoFileObject().copyFromBean(tipoFileObjRowBean);
                        populateComboTipoFileObj();

                        getForm().getTipoFileObject().setStatus(Status.view);
                        getForm().getTipoFileObjectList().setStatus(Status.view);
                        getForm().getTipoFileObject().getTi_vers_file().setValue(
                                ((PigTipoObjectRowBean) getForm().getTipoObjectList().getTable().getCurrentRow())
                                        .getTiVersFile());
                        getForm().getTipoFileObject().getTi_vers_file().setHidden(true);

                        PigXsdDatiSpecTableBean xsdDatiSpecTableBean = amministrazioneEjb
                                .getPigXsdDatiSpecTableBean(null, idTipoFileObj);

                        getForm().getXsdDatiSpecList().setTable(xsdDatiSpecTableBean);
                        getForm().getXsdDatiSpecList().getTable().first();
                        getForm().getXsdDatiSpecList().setStatus(Status.view);
                        getForm().getXsdDatiSpecList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
                    }
                } /* Se ho cliccato su Lista Sop Class */ else if (lista.equals(getForm().getSopClassList().getName())
                        && (getForm().getSopClassList().getTable() != null)
                        && (getForm().getSopClassList().getTable().size() > 0)) {

                    getForm().getSopClass().setViewMode();

                    BigDecimal idSopClass = ((PigSopClassDicomRowBean) getForm().getSopClassList().getTable()
                            .getCurrentRow()).getIdSopClassDicom();
                    PigSopClassDicomRowBean sopClassDicomRowBean = amministrazioneEjb
                            .getPigSopClassDicomRowBean(idSopClass);

                    getForm().getSopClass().copyFromBean(sopClassDicomRowBean);
                    getForm().getSopClass().setStatus(Status.view);
                    getForm().getSopClassList().setStatus(Status.view);

                } /* Se ho cliccato su Lista Xsd Dati Spec */ else if (lista.equals(
                        getForm().getXsdDatiSpecList().getName()) && (getForm().getXsdDatiSpecList().getTable() != null)
                        && (getForm().getXsdDatiSpecList().getTable().size() > 0)) {

                    getForm().getXsdDatiSpec().setViewMode();
                    getForm().getXsdDatiSpec().getScaricaXsdButton().setEditMode();
                    getForm().getXsdDatiSpec().getScaricaXsdButton().setDisableHourGlass(true);

                    BigDecimal idXsdDatiSpec = ((PigXsdDatiSpecRowBean) getForm().getXsdDatiSpecList().getTable()
                            .getCurrentRow()).getIdXsdSpec();
                    PigXsdDatiSpecRowBean xsdDatiSpecRowBean = amministrazioneEjb
                            .getPigXsdDatiSpecRowBean(idXsdDatiSpec);
                    getForm().getXsdDatiSpec().copyFromBean(xsdDatiSpecRowBean);

                    if (xsdDatiSpecRowBean.getIdTipoObject() != null) {
                        getForm().getIdList().getId_tipo_object()
                                .setValue(xsdDatiSpecRowBean.getIdTipoObject().toString());
                    } else if (xsdDatiSpecRowBean.getIdTipoFileObject() != null) {
                        getForm().getIdList().getId_tipo_file_object()
                                .setValue(xsdDatiSpecRowBean.getIdTipoFileObject().toString());
                    }

                    getForm().getXsdDatiSpec().setStatus(Status.view);
                    getForm().getXsdDatiSpecList().setStatus(Status.view);

                    getSession().removeAttribute("pagePrec");
                    if (getLastPublisher().equals(Application.Publisher.TIPO_OBJECT_DETAIL)) {
                        getSession().setAttribute("pagePrec", "tipoObject");
                    } else if (getLastPublisher().equals(Application.Publisher.TIPO_FILE_OBJECT_DETAIL)) {
                        getSession().setAttribute("pagePrec", "tipoFileObject");
                    }

                    PigAttribDatiSpecTableBean attribDatiSpecTableBean = amministrazioneEjb
                            .getPigAttribDatiSpecTableBean(idXsdDatiSpec);
                    getForm().getAttribDatiSpecList().setTable(attribDatiSpecTableBean);
                    getForm().getAttribDatiSpecList().getTable().first();
                    getForm().getAttribDatiSpecList().setStatus(Status.view);
                    getForm().getAttribDatiSpecList().setUserOperations(true, true, false, false);
                    getForm().getAttribDatiSpecList().getFl_filtro_diario().setReadonly(true);
                    getForm().getAttribDatiSpecList().getFl_vers_sacer().setReadonly(true);
                    getForm().getAttribDatiSpecList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);

                } /* Se ho cliccato su Lista Attributi Dati Specifici */ else if (lista
                        .equals(getForm().getAttribDatiSpecList().getName())
                        && (getForm().getAttribDatiSpecList().getTable() != null)
                        && (getForm().getAttribDatiSpecList().getTable().size() > 0)) {

                    getForm().getAttribDatiSpec().setViewMode();

                    PigAttribDatiSpecRowBean attribDatiSpecRowBean = ((PigAttribDatiSpecRowBean) getForm()
                            .getAttribDatiSpecList().getTable().getCurrentRow());
                    populateComboAttribDatiSpec();
                    getForm().getAttribDatiSpec().copyFromBean(attribDatiSpecRowBean);
                    getForm().getAttribDatiSpec().getTi_datatype_col()
                            .setValue(attribDatiSpecRowBean.getTiDatatypeCol());

                    getForm().getAttribDatiSpec().setStatus(Status.view);
                    getForm().getAttribDatiSpecList().setStatus(Status.view);
                } /* Se ho cliccato su Lista Stati Versamento Oggetto */ else if (lista
                        .equals(getForm().getStatiVersamentoObjectList().getName())
                        && (getForm().getStatiVersamentoObjectList().getTable() != null)
                        && (getForm().getStatiVersamentoObjectList().getTable().size() > 0)) {
                    PigStatoObjectRowBean statoObjectRowBean = ((PigStatoObjectRowBean) getForm()
                            .getStatiVersamentoObjectList().getTable().getCurrentRow());
                    getForm().getStatoVersamentoObjectDetail().copyFromBean(statoObjectRowBean);
                } /* Se ho cliccato su Lista Versatori per cui si generano oggetti */ else if (lista
                        .equals(getForm().getVersatoriGenerazioneOggettiList().getName())
                        && (getForm().getVersatoriGenerazioneOggettiList().getTable() != null)
                        && (getForm().getVersatoriGenerazioneOggettiList().getTable().size() > 0)) {

                    // Ricavo i dati di dettaglio
                    BigDecimal idVersTipoObjectDaTrasf = ((PigVersTipoObjectDaTrasfRowBean) getForm()
                            .getVersatoriGenerazioneOggettiList().getTable().getCurrentRow())
                                    .getIdVersTipoObjectDaTrasf();

                    loadVersatoreGenerazioneOggettiDetail(idVersTipoObjectDaTrasf);

                } /* Se ho cliccato su Lista Set Parametri versatore per cui si generano oggetti */ else if (lista
                        .equals(getForm().getSetParametriVersatoreList().getName())
                        && (getForm().getSetParametriVersatoreList().getTable() != null)
                        && (getForm().getSetParametriVersatoreList().getTable().size() > 0)) {

                    getForm().getSetParametriVersatoreDetail().setViewMode();
                    getForm().getSetParametriVersatoreDetail().getEliminaSetParametriVersatore().setEditMode();
                    getForm().getSetParametriVersatoreDetail().setStatus(Status.view);
                    getForm().getSetParametriVersatoreList().setStatus(Status.view);

                    BigDecimal idSetParamTrasf = ((PigVValoreSetParamTrasfRowBean) getForm()
                            .getSetParametriVersatoreList().getTable().getCurrentRow()).getIdSetParamTrasf();
                    BigDecimal idVersTipoObjectDaTrasf = ((PigVValoreSetParamTrasfRowBean) getForm()
                            .getSetParametriVersatoreList().getTable().getCurrentRow()).getIdVersTipoObjectDaTrasf();

                    // Set parametri
                    XfoSetParamTrasfRowBean setParamTrasfRowBean = amministrazioneEjb
                            .getXfoSetParamTrasfRowBean(idSetParamTrasf);
                    getForm().getSetParametriVersatoreDetail().copyFromBean(setParamTrasfRowBean);

                    // Valore parametri
                    loadValoriParametriVersatore(idSetParamTrasf, idVersTipoObjectDaTrasf);

                    getForm().getSetParametriVersatoreDetail().getDs_file_csv_parameters().setEditMode();
                    getForm().getSetParametriVersatoreDetail().getCaricaParametriDaCSV().setEditMode();

                    getForm().getSetParametriVersatoreDetail().getButton_action().setEditMode();
                }
            } else {
                /* Se ho cliccato su Lista Tipo Object */
                if (lista != null && lista.equals(getForm().getTipoObjectList().getName())
                        && (getForm().getTipoObjectList().getTable() != null)
                        && (getForm().getTipoObjectList().getTable().size() > 0)) {
                    getForm().getTipoObject().getFl_crea_tipofile().setHidden(false);
                    getForm().getTipoObject().getFl_crea_tipofile().setEditMode();
                    getForm().getTipoObject().getFl_crea_tipofile().setValue("1");
                }
            }
        } catch (ParerUserError ex) {
            getMessageBox().addError(ex.getDescription());
        }
    }

    private void loadVersatoreGenerazioneOggettiDetail(BigDecimal idVersTipoObjectDaTrasf) throws EMFError {
        getForm().getVersatoreGenerazioneOggettiDetail().setViewMode();
        getForm().getVersatoreGenerazioneOggettiDetail().setStatus(Status.view);
        getForm().getVersatoriGenerazioneOggettiList().setStatus(Status.view);

        PigVersTipoObjectDaTrasfRowBean versTipoObjectDaTrasfRowBean = amministrazioneEjb
                .getPigVersTipoObjectDaTrasfRowBean(idVersTipoObjectDaTrasf);

        getForm().getVersatoreGenerazioneOggettiDetail().getId_vers_gen()
                .setDecodeMap(DecodeMap.Factory.newInstance(
                        amministrazioneEjb.getPigVersAbilitatiTableBean(null, getUser().getIdUtente()), "id_vers_gen",
                        "nm_vers_gen"));
        getForm().getVersatoreGenerazioneOggettiDetail().getId_tipo_object_gen()
                .setDecodeMap(DecodeMap.Factory.newInstance(
                        amministrazioneEjb.getPigTipoObjectNoDaTrasfAbilitatiTableBean(
                                versTipoObjectDaTrasfRowBean.getIdVersGen(), getUser().getIdUtente()),
                        "id_tipo_object", "nm_tipo_object"));

        getForm().getVersatoreGenerazioneOggettiDetail().copyFromBean(versTipoObjectDaTrasfRowBean);

        // Set parametri
        loadSetParametriVersatore(idVersTipoObjectDaTrasf);
    }

    private void loadAmbienteVers(BigDecimal idAmbienteVers) throws EMFError, ParerUserError {
        getForm().getAmbienteVers().setViewMode();
        PigAmbienteVersRowBean ambienteVersRowBean = amministrazioneEjb.getPigAmbienteVersRowBean(idAmbienteVers);
        getForm().getAmbienteVers().copyFromBean(ambienteVersRowBean);
        getForm().getAmbienteVers().setStatus(Status.view);
        getForm().getAmbienteVersList().setStatus(Status.view);

        if (ambienteVersRowBean.getIdEnteGestore() != null) {
            // Ricavo il valore dell'ambiente ente gestore e lo setto nella combo
            BigDecimal idAmbienteEnteConvenz = amministrazioneEjb
                    .getIdAmbienteEnteConvenz(ambienteVersRowBean.getIdEnteGestore());
            getForm().getAmbienteVers().getId_ambiente_ente_convenz().setValue("" + idAmbienteEnteConvenz);

            // Popolo di conseguenza la combo ENTE GESTORE
            getForm().getAmbienteVers().getId_ente_gestore().reset();
            BaseTable enteConvenzTable = amministrazioneEjb.getEntiGestoreAbilitatiTableBean(
                    new BigDecimal(getUser().getIdUtente()),
                    getForm().getAmbienteVers().getId_ambiente_ente_convenz().parse());
            DecodeMap mappaEnte = new DecodeMap();
            mappaEnte.populatedMap(enteConvenzTable, "id_ente_gestore", "nm_ente_gestore");
            getForm().getAmbienteVers().getId_ente_gestore().setDecodeMap(mappaEnte);
            getForm().getAmbienteVers().getId_ente_gestore().setValue("" + ambienteVersRowBean.getIdEnteGestore());
            BaseRow enteSiamGestore = amministrazioneEjb.getSIOrgEnteSiam(ambienteVersRowBean.getIdEnteGestore());
            getForm().getAmbienteVers().getNm_ente_gestore().setValue(enteSiamGestore.getString("nm_ente_siam"));

            // Popolo di conseguenza la combo ENTE CONSERVATORE
            if (getForm().getAmbienteVers().getId_ente_gestore() != null) {
                getForm().getAmbienteVers().getId_ente_conserv().reset();
                BaseTable enteConservTable = amministrazioneEjb.getEntiConservatori(getUser().getIdUtente(),
                        getForm().getAmbienteVers().getId_ente_gestore().parse());
                DecodeMap mappaEnteConserv = new DecodeMap();
                mappaEnteConserv.populatedMap(enteConservTable, "id_ente_siam", "nm_ente_siam");
                getForm().getAmbienteVers().getId_ente_conserv().setDecodeMap(mappaEnteConserv);
                getForm().getAmbienteVers().getId_ente_conserv().setValue("" + ambienteVersRowBean.getIdEnteConserv());
                BaseRow enteSiamConservatore = amministrazioneEjb
                        .getSIOrgEnteSiam(ambienteVersRowBean.getIdEnteConserv());
                getForm().getAmbienteVers().getNm_ente_conserv()
                        .setValue(enteSiamConservatore.getString("nm_ente_siam"));
            }
        }

        PigVersRowBean versRowBean = new PigVersRowBean();
        versRowBean.setIdAmbienteVers(idAmbienteVers);
        PigVersTableBean versTableBean = amministrazioneEjb.getPigVersTableBean(versRowBean);

        getForm().getVersList().setTable(versTableBean);
        getForm().getVersList().getTable().first();
        getForm().getVersList().setStatus(Status.view);
        getForm().getVersList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
        getForm().getVersList().setUserOperations(false, false, false, false);
        getForm().getVersList().getNm_ambiente_vers().setHidden(true);

        // Parametri
        loadListeParametriAmbiente(idAmbienteVers, null, false, false, false, false, true);

        getForm().getParametriAmbienteButtonList().getParametriAmministrazioneAmbienteButton().setEditMode();
        getForm().getParametriAmbienteButtonList().getParametriConservazioneAmbienteButton().setEditMode();
        getForm().getParametriAmbienteButtonList().getParametriGestioneAmbienteButton().setEditMode();

    }

    private void loadListeParametriAmbiente(BigDecimal idAmbienteVers, List<String> funzione, boolean hideDeleteButtons,
            boolean editModeAmministrazione, boolean editModeConservazione, boolean editModeGestione,
            boolean showButtons) throws ParerUserError {
        Object[] parametriObj = amministrazioneEjb.getPigParamApplicAmbiente(idAmbienteVers, funzione);

        // MEV22933
        PigParamApplicTableBean parametriAmministrazione = (PigParamApplicTableBean) parametriObj[0];
        PigParamApplicTableBean parametriGestione = (PigParamApplicTableBean) parametriObj[1];
        PigParamApplicTableBean parametriConservazione = (PigParamApplicTableBean) parametriObj[2];

        if (!editModeAmministrazione) {
            parametriAmministrazione = obfuscatePasswordParamApplic(parametriAmministrazione);
        }

        if (!editModeGestione) {
            parametriGestione = obfuscatePasswordParamApplic(parametriGestione);
        }

        if (!editModeConservazione) {
            parametriConservazione = obfuscatePasswordParamApplic(parametriConservazione);
        }

        getForm().getParametriAmministrazioneAmbienteList().setTable(parametriAmministrazione);
        getForm().getParametriAmministrazioneAmbienteList().getTable().setPageSize(300);
        getForm().getParametriAmministrazioneAmbienteList().getTable().first();
        getForm().getParametriGestioneAmbienteList().setTable(parametriGestione);
        getForm().getParametriGestioneAmbienteList().getTable().setPageSize(300);
        getForm().getParametriGestioneAmbienteList().getTable().first();
        getForm().getParametriConservazioneAmbienteList().setTable(parametriConservazione);
        getForm().getParametriConservazioneAmbienteList().getTable().setPageSize(300);
        getForm().getParametriConservazioneAmbienteList().getTable().first();
        if (editModeAmministrazione) {
            getForm().getParametriAmministrazioneAmbienteList().getDs_valore_param_applic_ambiente_amm().setEditMode();
        } else {
            getForm().getParametriAmministrazioneAmbienteList().getDs_valore_param_applic_ambiente_amm().setViewMode();
        }
        if (editModeConservazione) {
            getForm().getParametriConservazioneAmbienteList().getDs_valore_param_applic_ambiente_cons().setEditMode();
        } else {
            getForm().getParametriConservazioneAmbienteList().getDs_valore_param_applic_ambiente_cons().setViewMode();
        }
        if (editModeGestione) {
            getForm().getParametriGestioneAmbienteList().getDs_valore_param_applic_ambiente_gest().setEditMode();
        } else {
            getForm().getParametriGestioneAmbienteList().getDs_valore_param_applic_ambiente_gest().setViewMode();
        }

        if (showButtons) {
            getForm().getParametriAmbienteButtonList().getParametriAmministrazioneAmbienteButton().setEditMode();
            getForm().getParametriAmbienteButtonList().getParametriConservazioneAmbienteButton().setEditMode();
            getForm().getParametriAmbienteButtonList().getParametriGestioneAmbienteButton().setEditMode();
        } else {
            getForm().getParametriAmbienteButtonList().getParametriAmministrazioneAmbienteButton().setViewMode();
            getForm().getParametriAmbienteButtonList().getParametriConservazioneAmbienteButton().setViewMode();
            getForm().getParametriAmbienteButtonList().getParametriGestioneAmbienteButton().setViewMode();
        }

        getForm().getParametriAmministrazioneAmbienteSection().setLoadOpened(false);
        getForm().getParametriConservazioneAmbienteSection().setLoadOpened(false);
        getForm().getParametriGestioneAmbienteSection().setLoadOpened(false);
    }

    private void loadVers(BigDecimal idVers) throws EMFError, ParerUserError {
        getForm().getVers().setViewMode();
        getForm().getVersatoreCustomMessageButtonList().setViewMode();
        getForm().getVersTab().setCurrentTab(getForm().getVersTab().getPigTipoObject());

        getForm().getIdList().getId_vers().setValue(idVers.toString());

        PigVersRowBean versRowBean = amministrazioneEjb.getPigVersRowBean(idVers);
        PigAmbienteVersRowBean ambienteRowBean = amministrazioneEjb.getPigAmbienteVersRowBeanFromVers(idVers);

        getForm().getVers().copyFromBean(versRowBean);
        getForm().getVers().getAssociaSopClassButton().setEditMode();

        // BaseRowInterface
        // Valorizzo il campo tipologia e visualizzo o meno la corrispondenza
        if (versRowBean.getIdEnteConvenz() != null) {
            getForm().getVers().getTipologia().setValue("PRODUTTORE");
            getForm().getCorrispondenzaSacerSection().setHidden(false);
        } else {
            String tipologia = amministrazioneEjb.getTipologiaEnteNonConvenz(versRowBean.getIdEnteFornitEstern());
            getForm().getVers().getTipologia().setValue(tipologia);
            getForm().getCorrispondenzaSacerSection().setHidden(true);
        }

        // Bottone cessazione versatore
        getForm().getVers().getCessaVersatore().setEditMode();
        if (getForm().getVers().getFl_cessato().parse() != null
                && getForm().getVers().getFl_cessato().parse().equals("1")
                && getForm().getVers().getTipologia().parse().equals("PRODUTTORE")) {
            getForm().getVers().getCessaVersatore().setReadonly(false);
        } else {
            getForm().getVers().getCessaVersatore().setReadonly(true);
        }

        getForm().getVers().getId_ambiente_vers().setValue(ambienteRowBean.getIdAmbienteVers().toString());
        getForm().getVers().setStatus(Status.view);
        getForm().getVersList().setStatus(Status.view);
        // Carico la corrispondenza sacer
        PigDichVersSacerRowBean dichVersRowBean = amministrazioneEjb.getPigDichVersSacerFromVers(idVers);
        getForm().getVers().getTi_dich_vers().setValue(dichVersRowBean.getTiDichVers());
        if (StringUtils.isNoneBlank(dichVersRowBean.getTiDichVers())) {
            try {
                getForm().getVers().getId_organiz_iam()
                        .setDecodeMap(getMappaDlCompositoOrganiz(dichVersRowBean.getTiDichVers()));
                getForm().getVers().getId_organiz_iam().setValue(dichVersRowBean.getIdOrganizIam().toString());
            } catch (Exception e) {
                getMessageBox().addError(e.getMessage());
            }
        } else {
            getForm().getVers().getId_organiz_iam().setDecodeMap(new DecodeMap());
        }

        // MEV 30790 - verifico l'esistenza delle cartelle per il versatore
        getForm().getVers().getTi_stato_cartelle().setHidden(false);
        getForm().getVers().getTi_stato_cartelle().setValue("OK");
        String prefisso = configHelper.getValoreParamApplicByIdVers(it.eng.sacerasi.common.Constants.DS_PREFISSO_PATH,
                ambienteRowBean.getIdAmbienteVers(), versRowBean.getIdVers());
        File basePath = new File(configHelper.getValoreParamApplicByApplic(it.eng.xformer.common.Constants.ROOT_FTP)
                + File.separator + prefisso + versRowBean.getNmVers());

        File path = new File(basePath + "/INPUT_FOLDER/");
        if (!path.exists() || !path.isDirectory()) {
            getForm().getVers().getTi_stato_cartelle().setValue("KO");
        }

        path = new File(basePath + "/OUTPUT_FOLDER/");
        if (!path.exists() || !path.isDirectory()) {
            getForm().getVers().getTi_stato_cartelle().setValue("KO");
        }

        path = new File(basePath + "/TRASFORMATI/");
        if (!path.exists() || !path.isDirectory()) {
            getForm().getVers().getTi_stato_cartelle().setValue("KO");
        }

        mostraNascondiFlagArchivioRestituitoCessato();

        loadListeVersatore(idVers);
    }

    private void loadTipoOggetto(BigDecimal idTipoObj) throws EMFError, ParerUserError {
        getForm().getTipoObjTab().setCurrentTab(getForm().getTipoObjTab().getPigTipoFileObject());
        getForm().getTipoObject().setViewMode();

        PigTipoObjectRowBean tipoObjRowBean = amministrazioneEjb.getPigTipoObjectRowBean(idTipoObj);
        String tiVers = tipoObjRowBean.getTiVersFile();
        if (tiVers != null && !tiVers.equals(it.eng.sacerasi.common.Constants.TipoVersamento.ZIP_CON_XML_SACER.name())
                && !tiVers.equals(it.eng.sacerasi.common.Constants.TipoVersamento.NO_ZIP.name())) {
            tipoObjRowBean.setTiPrioritaVersamento("");
        }

        // Azzero idTipoFileObject ed imposto idTipoObject in IdList
        getForm().getIdList().getId_tipo_file_object().clear();
        getForm().getIdList().getId_tipo_object().setValue(idTipoObj.toString());

        String nmVers = ((BaseRowInterface) getForm().getVersList().getTable().getCurrentRow()).getString("nm_vers");

        // MAC #23081 - nella pagina di Dettaglio tipo oggetto non è valorizzato il campo Versatore a inizio pagina
        if (getForm().getVers().getNm_vers().getValue() == null
                || getForm().getVers().getNm_vers().getValue().isEmpty()) {
            getForm().getVers().getNm_vers().setValue(nmVers);
        }

        getForm().getTipoObject().copyFromBean(tipoObjRowBean);
        populateComboTipoObj();

        try {
            if (getForm().getTipoObject().getTi_vers_file().parse()
                    .equals(it.eng.sacerasi.common.Constants.TipoVersamento.DA_TRASFORMARE.name())) {
                getForm().getTipoObject().getId_trasf().setDecodeMap(DecodeMap.Factory
                        .newInstance(amministrazioneEjb.getXfoTrasfTableBean(), "id_trasf", "cd_trasf"));
                getForm().getTipoObject().getId_trasf().setValue("" + tipoObjRowBean.getIdTrasf());

                getForm().getTipoObject().getTi_priorita().setDecodeMap(ComboGetter
                        .getMappaOrdinalGenericEnum("ti_priorita", Constants.ComboFlagPrioTrasfType.values()));
                getForm().getTipoObject().getTi_priorita()
                        .setValue(Constants.ComboFlagPrioTrasfType.getEnumByString(tipoObjRowBean.getTiPriorita()));
            } else {
                getForm().getTipoObject().getId_trasf().setDecodeMap(new DecodeMap());
                getForm().getTipoObject().getTi_priorita().setDecodeMap(new DecodeMap());
            }
            // MEV#27321 - Introduzione della priorità di versamento di un oggetto ZIP_CON_XML_SACER e NO_ZIP
            getForm().getTipoObject().getTi_priorita_versamento().setDecodeMap(ComboGetter
                    .getMappaOrdinalGenericEnum("ti_priorita_versamento", Constants.ComboFlagPrioVersType.values()));
            getForm().getTipoObject().getTi_priorita_versamento().setValue(
                    Constants.ComboFlagPrioVersType.getEnumByString(tipoObjRowBean.getTiPrioritaVersamento()));
            // Fine
        } catch (Exception ex) {
            getMessageBox().addError("Errore durante il recupero dei dati della tabella XFO_TRASF");
        }

        getForm().getTipoObject().getTi_vers_file().setValue(tipoObjRowBean.getTiVersFile());
        getForm().getTipoObject().getTi_calc_key_unita_doc().setValue(tipoObjRowBean.getTiCalcKeyUnitaDoc());
        getForm().getTipoObject().getTi_conservazione().setValue(tipoObjRowBean.getTiConservazione());
        getForm().getTipoObject().getNm_vers().setValue(nmVers);

        hideFieldsTipoObj(false);
        getForm().getTipoObject().setStatus(Status.view);
        getForm().getTipoObjectList().setStatus(Status.view);

        PigTipoFileObjectRowBean tipoFileObjectRowBean = new PigTipoFileObjectRowBean();
        tipoFileObjectRowBean.setIdTipoObject(idTipoObj);
        PigTipoFileObjectTableBean tipoFileObjTableBean = amministrazioneEjb.getPigTipoFileObjectTableBean(idTipoObj);

        getForm().getTipoFileObjectList().setTable(tipoFileObjTableBean);
        getForm().getTipoFileObjectList().getTable().first();
        getForm().getTipoFileObjectList().setStatus(Status.view);
        getForm().getTipoFileObjectList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);

        // Corrispondenze in Sacer per tipologia oggetto
        if (tipoObjRowBean.getTiVersFile().equals(it.eng.sacerasi.common.Constants.TipoVersamento.NO_ZIP.name())
                || tipoObjRowBean.getTiVersFile()
                        .equals(it.eng.sacerasi.common.Constants.TipoVersamento.ZIP_CON_XML_SACER.name())
                || tipoObjRowBean.getTiVersFile()
                        .equals(it.eng.sacerasi.common.Constants.TipoVersamento.ZIP_NO_XML_SACER.name())) {
            getForm().getTipoObject().getTi_dich_vers().setDecodeMap(ComboGetter.getMappaTiDichVers());
            PigDichVersSacerTipoObjRowBean dichVersSacerTipoObjRowBean = amministrazioneEjb
                    .getPigDichVersSacerTipoObjFromIdTipoObj(idTipoObj);
            getForm().getTipoObject().getTi_dich_vers().setValue(dichVersSacerTipoObjRowBean.getTiDichVers());
            if (StringUtils.isNoneBlank(dichVersSacerTipoObjRowBean.getTiDichVers())) {
                try {
                    getForm().getTipoObject().getId_organiz_iam()
                            .setDecodeMap(getMappaDlCompositoOrganiz(dichVersSacerTipoObjRowBean.getTiDichVers()));
                    getForm().getTipoObject().getId_organiz_iam()
                            .setValue(dichVersSacerTipoObjRowBean.getIdOrganizIam().toString());
                } catch (Exception e) {
                    getMessageBox().addError(e.getMessage());
                }
            } else {
                getForm().getTipoObject().getId_organiz_iam().setDecodeMap(new DecodeMap());
            }
        } else {
            getForm().getTipoObject().getTi_dich_vers().setViewMode();
            getForm().getTipoObject().getId_organiz_iam().setViewMode();
            getForm().getTipoObject().getTi_dich_vers().setDecodeMap(new DecodeMap());
            getForm().getTipoObject().getId_organiz_iam().setDecodeMap(new DecodeMap());
        }

        // Versatori per cui si generano oggetti
        if (tipoObjRowBean.getTiVersFile().equals(it.eng.sacerasi.common.Constants.TipoVersamento.DA_TRASFORMARE.name())
                && tipoObjRowBean.getIdTrasf() != null) {
            getForm().getTipoObjTab().getVersatoriGenerazioneOggettiTab().setHidden(false);
            PigVersTipoObjectDaTrasfTableBean versTipoObjectDaTrasfTableBean = amministrazioneEjb
                    .getPigVersTipoObjectDaTrasfTableBean(idTipoObj);
            getForm().getVersatoriGenerazioneOggettiList().setTable(versTipoObjectDaTrasfTableBean);
            getForm().getVersatoriGenerazioneOggettiList().getTable().first();
            getForm().getVersatoriGenerazioneOggettiList().setStatus(Status.view);
            getForm().getVersatoriGenerazioneOggettiList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
        } else {
            getForm().getTipoObjTab().getVersatoriGenerazioneOggettiTab().setHidden(true);
        }

        // Parametri
        PigAmbienteVersRowBean pigAmbienteVersByVers = amministrazioneEjb
                .getPigAmbienteVersByVers(tipoObjRowBean.getIdVers());
        loadListeParametriTipoOggetto(pigAmbienteVersByVers.getIdAmbienteVers(), tipoObjRowBean.getIdVers(), idTipoObj,
                null, false, false, false, false, true);

        getForm().getParametriTipoOggettoButtonList().getParametriAmministrazioneTipoOggettoButton().setEditMode();
        getForm().getParametriTipoOggettoButtonList().getParametriConservazioneTipoOggettoButton().setEditMode();
        getForm().getParametriTipoOggettoButtonList().getParametriGestioneTipoOggettoButton().setEditMode();
    }

    private void loadListeParametriTipoOggetto(BigDecimal idAmbienteVers, BigDecimal idVers, BigDecimal idTipoObject,
            List<String> funzione, boolean hideDeleteButtons, boolean editModeAmministrazione,
            boolean editModeConservazione, boolean editModeGestione, boolean showButtons) throws ParerUserError {
        Object[] parametriObj = amministrazioneEjb.getPigParamApplicTipoOggetto(idAmbienteVers, idVers, idTipoObject,
                funzione);

        // MEV22933
        PigParamApplicTableBean parametriAmministrazione = (PigParamApplicTableBean) parametriObj[0];
        PigParamApplicTableBean parametriGestione = (PigParamApplicTableBean) parametriObj[1];
        PigParamApplicTableBean parametriConservazione = (PigParamApplicTableBean) parametriObj[2];

        if (!editModeAmministrazione) {
            parametriAmministrazione = obfuscatePasswordParamApplic(parametriAmministrazione);
        }

        if (!editModeGestione) {
            parametriGestione = obfuscatePasswordParamApplic(parametriGestione);
        }

        if (!editModeConservazione) {
            parametriConservazione = obfuscatePasswordParamApplic(parametriConservazione);
        }

        getForm().getParametriAmministrazioneTipoOggettoList().setTable(parametriAmministrazione);
        getForm().getParametriAmministrazioneTipoOggettoList().getTable().setPageSize(300);
        getForm().getParametriAmministrazioneTipoOggettoList().getTable().first();
        getForm().getParametriGestioneTipoOggettoList().setTable(parametriGestione);
        getForm().getParametriGestioneTipoOggettoList().getTable().setPageSize(300);
        getForm().getParametriGestioneTipoOggettoList().getTable().first();
        getForm().getParametriConservazioneTipoOggettoList().setTable(parametriConservazione);
        getForm().getParametriConservazioneTipoOggettoList().getTable().setPageSize(300);
        getForm().getParametriConservazioneTipoOggettoList().getTable().first();
        if (editModeAmministrazione) {
            getForm().getParametriAmministrazioneTipoOggettoList().getDs_valore_param_applic_tipo_oggetto_amm()
                    .setEditMode();
        } else {
            getForm().getParametriAmministrazioneTipoOggettoList().getDs_valore_param_applic_tipo_oggetto_amm()
                    .setViewMode();
        }

        if (editModeConservazione) {
            getForm().getParametriConservazioneTipoOggettoList().getDs_valore_param_applic_tipo_oggetto_cons()
                    .setEditMode();
        } else {
            getForm().getParametriConservazioneTipoOggettoList().getDs_valore_param_applic_tipo_oggetto_cons()
                    .setViewMode();
        }

        if (editModeGestione) {
            getForm().getParametriGestioneTipoOggettoList().getDs_valore_param_applic_tipo_oggetto_gest().setEditMode();
        } else {
            getForm().getParametriGestioneTipoOggettoList().getDs_valore_param_applic_tipo_oggetto_gest().setViewMode();
        }

        if (showButtons) {
            getForm().getParametriTipoOggettoButtonList().getParametriAmministrazioneTipoOggettoButton().setEditMode();
            getForm().getParametriTipoOggettoButtonList().getParametriConservazioneTipoOggettoButton().setEditMode();
            getForm().getParametriTipoOggettoButtonList().getParametriGestioneTipoOggettoButton().setEditMode();
        } else {
            getForm().getParametriTipoOggettoButtonList().getParametriAmministrazioneTipoOggettoButton().setViewMode();
            getForm().getParametriTipoOggettoButtonList().getParametriConservazioneTipoOggettoButton().setViewMode();
            getForm().getParametriTipoOggettoButtonList().getParametriGestioneTipoOggettoButton().setViewMode();
        }

        getForm().getParametriAmministrazioneTipoOggettoSection().setLoadOpened(false);
        getForm().getParametriConservazioneTipoOggettoSection().setLoadOpened(false);
        getForm().getParametriGestioneTipoOggettoSection().setLoadOpened(false);
    }

    private void loadListeVersatore(BigDecimal idVers) throws ParerUserError {
        // Precedenti appartenenze ad ambienti
        PigStoricoVersAmbienteTableBean storicoVersAmbienteTableBean = amministrazioneEjb
                .getPigStoricoVersAmbienteTableBean(idVers);
        getForm().getPrecedentiAppartenenzeAmbientiList().setTable(storicoVersAmbienteTableBean);
        getForm().getPrecedentiAppartenenzeAmbientiList().getTable().sort();

        // Enti Siam
        SIOrgEnteConvenzOrgTableBean enteConvenzOrgTableBean = amministrazioneEjb
                .getSIOrgEnteConvenzOrgTableBean(idVers);
        getForm().getEnteConvenzOrgList().setTable(enteConvenzOrgTableBean);
        getForm().getEnteConvenzOrgList().getTable().addSortingRule("dt_ini_val", SortingRule.DESC);
        getForm().getEnteConvenzOrgList().getTable().sort();

        // Tipi oggetto
        PigTipoObjectTableBean versTableBean = amministrazioneEjb.getPigTipoObjectTableBean(idVers);
        getForm().getTipoObjectList().setTable(versTableBean);
        getForm().getTipoObjectList().getTable().first();
        getForm().getTipoObjectList().setStatus(Status.view);
        getForm().getTipoObjectList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
        getForm().getParametriAmministrazioneTipoOggettoSection().setLoadOpened(false);
        getForm().getParametriConservazioneTipoOggettoSection().setLoadOpened(false);
        getForm().getParametriGestioneTipoOggettoSection().setLoadOpened(false);
        // Parametri
        PigAmbienteVersRowBean pigAmbienteVersByVers = amministrazioneEjb.getPigAmbienteVersByVers(idVers);
        loadListeParametriVersatore(pigAmbienteVersByVers.getIdAmbienteVers(), idVers, null, false, false, false, false,
                true);

        getForm().getParametriVersatoreButtonList().getParametriAmministrazioneVersatoreButton().setEditMode();
        getForm().getParametriVersatoreButtonList().getParametriConservazioneVersatoreButton().setEditMode();
        getForm().getParametriVersatoreButtonList().getParametriGestioneVersatoreButton().setEditMode();

    }

    private void loadListeParametriVersatore(BigDecimal idAmbienteVers, BigDecimal idVers, List<String> funzione,
            boolean hideDeleteButtons, boolean editModeAmministrazione, boolean editModeConservazione,
            boolean editModeGestione, boolean showButtons) throws ParerUserError {
        Object[] parametriObj = amministrazioneEjb.getPigParamApplicVers(idAmbienteVers, idVers, funzione);

        // MEV22933
        PigParamApplicTableBean parametriAmministrazione = (PigParamApplicTableBean) parametriObj[0];
        PigParamApplicTableBean parametriGestione = (PigParamApplicTableBean) parametriObj[1];
        PigParamApplicTableBean parametriConservazione = (PigParamApplicTableBean) parametriObj[2];

        if (!editModeAmministrazione) {
            parametriAmministrazione = obfuscatePasswordParamApplic(parametriAmministrazione);
        }

        if (!editModeGestione) {
            parametriGestione = obfuscatePasswordParamApplic(parametriGestione);
        }

        if (!editModeConservazione) {
            parametriConservazione = obfuscatePasswordParamApplic(parametriConservazione);
        }

        getForm().getParametriAmministrazioneVersatoreList().setTable(parametriAmministrazione);
        getForm().getParametriAmministrazioneVersatoreList().getTable().setPageSize(300);
        getForm().getParametriAmministrazioneVersatoreList().getTable().first();
        getForm().getParametriGestioneVersatoreList().setTable(parametriGestione);
        getForm().getParametriGestioneVersatoreList().getTable().setPageSize(300);
        getForm().getParametriGestioneVersatoreList().getTable().first();
        getForm().getParametriConservazioneVersatoreList().setTable(parametriConservazione);
        getForm().getParametriConservazioneVersatoreList().getTable().setPageSize(300);
        getForm().getParametriConservazioneVersatoreList().getTable().first();
        if (editModeAmministrazione) {
            getForm().getParametriAmministrazioneVersatoreList().getDs_valore_param_applic_vers_amm().setEditMode();
        } else {
            getForm().getParametriAmministrazioneVersatoreList().getDs_valore_param_applic_vers_amm().setViewMode();
        }

        if (editModeConservazione) {
            getForm().getParametriConservazioneVersatoreList().getDs_valore_param_applic_vers_cons().setEditMode();
        } else {
            getForm().getParametriConservazioneVersatoreList().getDs_valore_param_applic_vers_cons().setViewMode();
        }

        if (editModeGestione) {
            getForm().getParametriGestioneVersatoreList().getDs_valore_param_applic_vers_gest().setEditMode();
        } else {
            getForm().getParametriGestioneVersatoreList().getDs_valore_param_applic_vers_gest().setViewMode();
        }

        if (showButtons) {
            getForm().getParametriVersatoreButtonList().getParametriAmministrazioneVersatoreButton().setEditMode();
            getForm().getParametriVersatoreButtonList().getParametriConservazioneVersatoreButton().setEditMode();
            getForm().getParametriVersatoreButtonList().getParametriGestioneVersatoreButton().setEditMode();
        } else {
            getForm().getParametriVersatoreButtonList().getParametriAmministrazioneVersatoreButton().setViewMode();
            getForm().getParametriVersatoreButtonList().getParametriConservazioneVersatoreButton().setViewMode();
            getForm().getParametriVersatoreButtonList().getParametriGestioneVersatoreButton().setViewMode();
        }

        getForm().getParametriAmministrazioneVersatoreSection().setLoadOpened(false);
        getForm().getParametriConservazioneVersatoreSection().setLoadOpened(false);
        getForm().getParametriGestioneVersatoreSection().setLoadOpened(false);
    }

    private void loadValoriParametriVersatore(BigDecimal idSetParamTrasf, BigDecimal idVersTipoObjectDaTrasf) {
        // Valori parametri
        PigVValParamTrasfDefSpecTableBean valoreParamTrasfViewBean = amministrazioneEjb
                .getPigVValParamTrasfDefSpecTableBean(idSetParamTrasf, idVersTipoObjectDaTrasf);
        getForm().getValoreParametriVersatoreList().setTable(valoreParamTrasfViewBean);
        getForm().getValoreParametriVersatoreList().getTable().first();
        getForm().getValoreParametriVersatoreList().setStatus(Status.view);
        getForm().getValoreParametriVersatoreList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
    }

    private void loadSetParametriVersatore(BigDecimal idVersTipoObjectDaTrasf) {
        getForm().getParametriSection().setHidden(false);
        PigVValoreSetParamTrasfTableBean valoreSetParamTrasfViewBean = amministrazioneEjb
                .getPigVValoreSetParamTrasfTableBean(idVersTipoObjectDaTrasf);
        getForm().getSetParametriVersatoreList().setTable(valoreSetParamTrasfViewBean);
        getForm().getSetParametriVersatoreList().getTable().first();
        getForm().getSetParametriVersatoreList().setStatus(Status.view);
        getForm().getSetParametriVersatoreList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
    }

    @Override
    public void undoDettaglio() throws EMFError {
        try {
            // la lista si ricava con getTableName
            if (getLastPublisher().equals(Application.Publisher.VERS_DETAIL) && (getForm().getVers().getStatus() == null
                    || getForm().getVers().getStatus().equals(Status.insert))) {
                goBack();
            } else if (getLastPublisher().equals(Application.Publisher.AMBIENTE_VERS_DETAIL)
                    && (getForm().getAmbienteVers().getStatus() == null
                            || getForm().getAmbienteVers().getStatus().equals(Status.insert))) {
                goBack();
            } else if (getLastPublisher().equals(Application.Publisher.TIPO_OBJECT_DETAIL)
                    && (getForm().getTipoObject().getStatus().equals(Status.insert))) {
                goBack();
            } else if (getLastPublisher().equals(Application.Publisher.TIPO_FILE_OBJECT_DETAIL)
                    && (getForm().getTipoFileObject().getStatus().equals(Status.insert))) {
                getForm().getTipoObject().getFl_contr_hash().setHidden(false);
                goBack();
            } else if (getLastPublisher().equals(Application.Publisher.XSD_DATI_SPEC_DETAIL)
                    && (getForm().getXsdDatiSpec().getStatus().equals(Status.insert))) {
                goBack();
            } else if (getLastPublisher().equals(Application.Publisher.SOP_CLASS_DICOM_DETAIL)
                    && (getForm().getSopClass().getStatus().equals(Status.insert))) {
                goBack();
            } else if (getLastPublisher().equals(Application.Publisher.SOP_CLASS_DICOM_VERS_DETAIL)) {
                goBack();
            } else if (getLastPublisher().equals(Application.Publisher.DUPLICA_VERS_DETAIL)) {
                goBack();
            } else if (getLastPublisher().equals(Application.Publisher.DETTAGLIO_STATO_VERSAMENTO_OGGETTO)) {
                goBack();
            } else if (getLastPublisher().equals(Application.Publisher.DETTAGLIO_CORRISPONDENZA_SACER_PER_VERSATORE)) {
                goBack();
            } else if (getLastPublisher()
                    .equals(Application.Publisher.DETTAGLIO_CORRISPONDENZA_SACER_PER_TIPO_OGGETTO)) {
                goBack();
            } else if (getLastPublisher().equals(Application.Publisher.DETTAGLIO_VERSATORE_GENERAZIONE_OGGETTI)) {
                goBack();
            } else if (getLastPublisher().equals(Application.Publisher.PARAMETRI_AMBIENTE_VERS)
                    && getForm().getAmbienteVers().getStatus() != null
                    && getForm().getAmbienteVers().getStatus().toString().equals("update")) {
                getForm().getAmbienteVers().setStatus(Status.view);
                getForm().getParametriAmministrazioneAmbienteList().setViewMode();
                getForm().getParametriConservazioneAmbienteList().setViewMode();
                getForm().getParametriGestioneAmbienteList().setViewMode();
                BigDecimal idAmbienteVers = ((BaseRowInterface) getForm().getAmbienteVersList().getTable()
                        .getCurrentRow()).getBigDecimal("id_ambiente_vers");
                loadAmbienteVers(idAmbienteVers);
                goBack();
            } else if (getLastPublisher().equals(Application.Publisher.PARAMETRI_VERSATORE)
                    && getForm().getVers().getStatus() != null
                    && getForm().getVers().getStatus().toString().equals("update")) {
                getForm().getVers().setStatus(Status.view);
                getForm().getParametriAmministrazioneVersatoreList().setViewMode();
                getForm().getParametriConservazioneVersatoreList().setViewMode();
                getForm().getParametriGestioneVersatoreList().setViewMode();
                BigDecimal idVers = ((BaseRowInterface) getForm().getVersList().getTable().getCurrentRow())
                        .getBigDecimal("id_vers");
                loadVers(idVers);
                goBackTo(Application.Publisher.VERS_DETAIL);
            } else if (getLastPublisher().equals(Application.Publisher.PARAMETRI_TIPO_OBJECT)
                    && getForm().getTipoObject().getStatus() != null
                    && getForm().getTipoObject().getStatus().toString().equals("update")) {
                getForm().getTipoObject().setStatus(Status.view);
                getForm().getParametriAmministrazioneTipoOggettoList().setViewMode();
                getForm().getParametriConservazioneTipoOggettoList().setViewMode();
                getForm().getParametriGestioneTipoOggettoList().setViewMode();
                BigDecimal idTipoObject = ((BaseRowInterface) getForm().getTipoObjectList().getTable().getCurrentRow())
                        .getBigDecimal("id_tipo_object");
                loadTipoOggetto(idTipoObject);
                goBackTo(Application.Publisher.TIPO_OBJECT_DETAIL);
            } else {
                loadDettaglio();
            }
        } catch (ParerUserError ie) {
            log.error(ie.getDescription());
            getMessageBox().addError(ie.getDescription());
        }
    }

    @Override
    public void saveDettaglio() throws EMFError {
        String publisher = getLastPublisher();
        try {
            if (publisher.equals(Application.Publisher.AMBIENTE_VERS_DETAIL)) {
                salvaAmbienteVers();
            } else if (publisher.equals(Application.Publisher.VERS_DETAIL)) {
                salvaVers();
            } else if (publisher.equals(Application.Publisher.TIPO_OBJECT_DETAIL)) {
                salvaTipoObj();
            } else if (publisher.equals(Application.Publisher.TIPO_FILE_OBJECT_DETAIL)) {
                salvaTipoFileObj();
            } else if (publisher.equals(Application.Publisher.SOP_CLASS_DICOM_DETAIL)) {
                salvaSopClass();
            } else if (publisher.equals(Application.Publisher.SOP_CLASS_DICOM_VERS_DETAIL)) {
                salvaSopClassVers();
            } else if (publisher.equals(Application.Publisher.ATTRIB_DATI_SPEC_DETAIL)) {
                salvaAttribDatiSpec();
            } else if (publisher.equals(Application.Publisher.DETTAGLIO_STATO_VERSAMENTO_OGGETTO)) {
                salvaDettaglioStatoVersamento();
            } else if (publisher.equals(Application.Publisher.DETTAGLIO_VERSATORE_GENERAZIONE_OGGETTI)) {
                salvaVersatoreGenerazioneOggetti();
            } else if (Application.Publisher.PARAMETRI_AMBIENTE_VERS.equals(publisher)) {
                salvaParametriAmbiente();
            } else if (Application.Publisher.PARAMETRI_VERSATORE.equals(publisher)) {
                salvaParametriVersatore();
            } else if (Application.Publisher.PARAMETRI_TIPO_OBJECT.equals(publisher)) {
                salvaParametriTipoOggetto();
            }

        } catch (IncoherenceException ie) {
            log.error(ie.getMessage());
            getMessageBox().addError(ie.getMessage());
            forwardToPublisher(publisher);
        } catch (ParerUserError ie) {
            log.error(ie.getDescription());
            getMessageBox().addError(ie.getDescription());
            forwardToPublisher(publisher);
        }
    }

    @Override
    public void dettaglioOnClick() throws EMFError {
        String lista = getTableName();
        String action = getRequest().getParameter("navigationEvent");

        if (action != null && !action.equals(NE_DETTAGLIO_DELETE)) {
            if (getForm().getAmbienteVersList().getName().equals(lista)) {
                forwardToPublisher(Application.Publisher.AMBIENTE_VERS_DETAIL);
            } else if (getForm().getVersList().getName().equals(lista)) {
                forwardToPublisher(Application.Publisher.VERS_DETAIL);
            } else if (getForm().getTipoObjectList().getName().equals(lista)) {
                forwardToPublisher(Application.Publisher.TIPO_OBJECT_DETAIL);
            } else if (getForm().getTipoFileObjectList().getName().equals(lista)) {
                forwardToPublisher(Application.Publisher.TIPO_FILE_OBJECT_DETAIL);
            } else if (getForm().getSopClassList().getName().equals(lista)) {
                forwardToPublisher(Application.Publisher.SOP_CLASS_DICOM_DETAIL);
            } else if (getForm().getXsdDatiSpecList().getName().equals(lista)) {
                if (getLastPublisher().equals(Application.Publisher.TIPO_OBJECT_DETAIL)) {
                    getRequest().setAttribute("lastPage", "tipoObject");
                    getRequest().setAttribute("titolo", "Dettaglio versione XSD del Tipo Oggetto");
                } else if (getLastPublisher().equals(Application.Publisher.TIPO_FILE_OBJECT_DETAIL)) {
                    getRequest().setAttribute("lastPage", "tipoFileObject");
                    getRequest().setAttribute("titolo", "Dettaglio versione XSD del Tipo File");
                }
                forwardToPublisher(Application.Publisher.XSD_DATI_SPEC_DETAIL);
            } else if (getForm().getVersatoriGenerazioneOggettiList().getName().equals(lista)) {
                forwardToPublisher(Application.Publisher.DETTAGLIO_VERSATORE_GENERAZIONE_OGGETTI);
            } else if (getForm().getSetParametriVersatoreList().getName().equals(lista)) {
                forwardToPublisher(Application.Publisher.DETTAGLIO_SET_PARAMETRI_VERSATORE);
            } else if (lista.equals(getForm().getEnteConvenzOrgList().getName())
                    && action.equals(ListAction.NE_DETTAGLIO_VIEW)) {
                redirectToEnteConvenzPage(action);
            }

        }
    }

    @Override
    public void elencoOnClick() throws EMFError {
        String lista = getTableName();
        String action = getNavigationEvent();
        ExecutionHistory lastExecutionHistory = SessionManager.getLastExecutionHistory(getSession());
        if (getForm().getVersList().getName().equals(lista) && NE_ELENCO.equals(action)
                && lastExecutionHistory.isAction()) {
            ricercaVers();
        } else if (getForm().getVersList().getName().equals(lista) && NE_ELENCO.equals(action)
                && !lastExecutionHistory.isAction()) {
            goBackTo(Application.Publisher.CERCA_VERS);
        } else {
            goBack();
        }
    }

    public void changePwd() {
        redirectToAction("Login.html", "?operation=fwdChangePwd", null);
    }

    @Override
    public void insertDettaglio() throws EMFError {
        try {
            String lista = getTableName();

            if (lista.equals(getForm().getAmbienteVersList().getName())) {

                getForm().getAmbienteVers().clear();
                getForm().getAmbienteVers().setEditMode();

                loadListeParametriAmbiente(null, null, true, true, true, true, false);

                // Date precompilate
                String dataOdierna = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
                getForm().getAmbienteVers().getDt_ini_val().setValue(dataOdierna);
                Calendar cal = Calendar.getInstance();
                cal.set(2444, Calendar.DECEMBER, 31, 0, 0, 0);
                String dataFine = new SimpleDateFormat("dd/MM/yyyy").format(cal.getTime());
                getForm().getAmbienteVers().getDt_fine_val().setValue(dataFine);

                initAmbienteCombo();

                getForm().getAmbienteVers().setStatus(Status.insert);
                getForm().getAmbienteVersList().setStatus(Status.insert);

            } else if (lista.equals(getForm().getVersList().getName())) {

                getForm().getVers().clear();
                getForm().getVers().setEditMode();

                getForm().getVers().getDs_path_input_ftp().setViewMode();
                getForm().getVers().getDs_path_output_ftp().setViewMode();
                getForm().getVers().getDs_path_trasf().setViewMode();
                populateComboVers();
                setDateStandard();

                // 30790
                getForm().getVers().getTi_stato_cartelle().setHidden(true);

                getForm().getVers().setStatus(Status.insert);
                getForm().getVers().getAssociaSopClassButton().setViewMode();
                getForm().getVersList().setStatus(Status.insert);

                mostraNascondiFlagArchivioRestituitoCessato();

                if (getForm().getVers().getTipologia().parse().equals("PRODUTTORE")) {
                    getForm().getCorrispondenzaSacerSection().setHidden(false);
                } else {
                    getForm().getCorrispondenzaSacerSection().setHidden(true);
                }

                loadListeParametriVersatore(null, null, null, true, true, true, true, false);

            } else if (lista.equals(getForm().getTipoObjectList().getName())) {

                getForm().getTipoObject().clear();
                getForm().getTipoObject().setEditMode();
                populateComboTipoObj();
                getForm().getTipoObject().getTi_priorita_versamento()
                        .setValue(Constants.ComboFlagPrioVersType.NORMALE.name());
                getForm().getTipoObject().getTi_dich_vers().setDecodeMap(new DecodeMap());
                getForm().getTipoObject().getId_organiz_iam().setDecodeMap(new DecodeMap());
                hideFieldsTipoObj(false);
                getForm().getTipoObject().getFl_crea_tipofile().setHidden(false);
                getForm().getTipoObject().getFl_crea_tipofile().setEditMode();
                getForm().getTipoObject().getFl_crea_tipofile().setValue("1");
                getForm().getTipoObject().setStatus(Status.insert);
                getForm().getTipoObjectList().setStatus(Status.insert);
                BigDecimal idVers = getForm().getVers().getId_vers().parse();
                PigAmbienteVersRowBean pigAmbienteVersByVers = amministrazioneEjb.getPigAmbienteVersByVers(idVers);

                loadListeParametriTipoOggetto(pigAmbienteVersByVers.getIdAmbienteVers(), idVers, null, null, true, true,
                        true, true, false);

            } else if (lista.equals(getForm().getTipoFileObjectList().getName())) {

                getForm().getTipoFileObject().clear();
                getForm().getTipoFileObject().setEditMode();
                populateComboTipoFileObj();
                String tiVersFile = getForm().getTipoObject().getTi_vers_file().parse();
                getForm().getTipoFileObject().getTi_vers_file().setValue(tiVersFile);
                getForm().getTipoFileObject().setStatus(Status.insert);
                getForm().getTipoFileObjectList().setStatus(Status.insert);
                getForm().getTipoObject().getFl_contr_hash().setHidden(true);

                if (it.eng.sacerasi.common.Constants.TipoVersamento.NO_ZIP.name().equals(tiVersFile)) {
                    getForm().getTipoFileObject().getFl_vers_sacer_asinc().setValue("SI");
                    hideFieldsTipoFileObj(false);
                } else {
                    getForm().getTipoFileObject().getFl_vers_sacer_asinc().setValue("NO");
                    hideFieldsTipoFileObj(true);

                }
                getForm().getTipoFileObject().getTi_vers_file().setViewMode();
                getForm().getTipoFileObject().getFl_vers_sacer_asinc().setViewMode();

            } else if (lista.equals(getForm().getSopClassList().getName())) {

                getForm().getSopClass().clear();
                getForm().getSopClass().setEditMode();
                getForm().getSopClass().setStatus(Status.insert);
                getForm().getSopClassList().setStatus(Status.insert);

            } else if (lista.equals(getForm().getXsdDatiSpecList().getName())) {
                /*
                 * Inserimento di un record della lista XsdDatiSpec ATTENZIONE: posso provenire da TipoObject o da
                 * TipoFileObject. A seconda dei casi, imposto un id o l'altro in IdList Va fatto qui, perché essendo in
                 * insert non ero passato per loadDettaglio del Tipo Object o del Tipo File Object
                 */
                if (getRequest().getAttribute("lastPage") != null) {
                    if (getRequest().getAttribute("lastPage").equals("tipoObject")) {
                        getForm().getIdList().getId_tipo_object().setValue(
                                ((PigTipoObjectRowBean) getForm().getTipoObjectList().getTable().getCurrentRow())
                                        .getIdTipoObject().toString());
                        getForm().getIdList().getId_tipo_file_object().clear();
                    } else if (getRequest().getAttribute("lastPage").equals("tipoFileObject")) {
                        getForm().getIdList().getId_tipo_file_object().setValue(((PigTipoFileObjectRowBean) getForm()
                                .getTipoFileObjectList().getTable().getCurrentRow()).getIdTipoFileObject().toString());
                        getForm().getIdList().getId_tipo_object().clear();
                    }
                }

                getForm().getXsdDatiSpec().clear();
                getForm().getXsdDatiSpec().setEditMode();
                getForm().getXsdDatiSpec().getScaricaXsdButton().setViewMode();
                getForm().getXsdDatiSpec().setStatus(Status.insert);
                getForm().getXsdDatiSpecList().setStatus(Status.insert);

            } else if (lista.equals(getForm().getVersatoriGenerazioneOggettiList().getName())) {
                getForm().getVersatoreGenerazioneOggettiDetail().clear();
                getForm().getVersatoreGenerazioneOggettiDetail().getId_vers_gen().setEditMode();
                getForm().getVersatoreGenerazioneOggettiDetail().getId_tipo_object_gen().setEditMode();
                getForm().getVersatoreGenerazioneOggettiDetail().getCd_vers_gen().setEditMode();
                // Nascondo la lista dei parametri
                getForm().getParametriSection().setHidden(true);

                // Carico i dati del versatore e tipo oggetto da trasformare
                getForm().getVersatoreGenerazioneOggettiDetail().getVersatore_trasf()
                        .setValue(getForm().getVers().getId_ambiente_vers().getDecodedValue() + " - "
                                + getForm().getVers().getNm_vers().parse());
                getForm().getVersatoreGenerazioneOggettiDetail().getId_tipo_object_da_trasf()
                        .setValue(getForm().getTipoObject().getId_tipo_object().parse().toPlainString());
                getForm().getVersatoreGenerazioneOggettiDetail().getNm_tipo_object_da_trasf()
                        .setValue(getForm().getTipoObject().getNm_tipo_object().parse());
                getForm().getVersatoreGenerazioneOggettiDetail().getCd_trasf()
                        .setValue(getForm().getTipoObject().getId_trasf().getDecodedValue());

                // Carico la combo con gli ambienti/versatori escludendo quelli già inseriti
                PigVersTipoObjectDaTrasfTableBean tb = (PigVersTipoObjectDaTrasfTableBean) getForm()
                        .getVersatoriGenerazioneOggettiList().getTable();
                List<BigDecimal> idVersGenGiaDefiniti = new ArrayList<>();
                for (PigVersTipoObjectDaTrasfRowBean rb : tb) {
                    idVersGenGiaDefiniti.add(rb.getIdVersGen());
                }
                getForm().getVersatoreGenerazioneOggettiDetail().getId_vers_gen()
                        .setDecodeMap(DecodeMap.Factory.newInstance(amministrazioneEjb.getPigVersAbilitatiTableBean(
                                idVersGenGiaDefiniti, getUser().getIdUtente()), "id_vers_gen", "nm_vers_gen"));
                getForm().getVersatoreGenerazioneOggettiDetail().getId_tipo_object_gen().setDecodeMap(new DecodeMap());

                getForm().getVersatoreGenerazioneOggettiDetail().setStatus(Status.insert);
                getForm().getVersatoriGenerazioneOggettiList().setStatus(Status.insert);
            } else if (lista.equals(getForm().getEnteConvenzOrgList().getName())) {
                redirectToEnteConvenzPage(NE_DETTAGLIO_INSERT);
            }
        } catch (ParerUserError ex) {
            getMessageBox().addError(ex.getDescription());
        }
    }

    private void redirectToEnteConvenzPage(String action) throws EMFError {
        // Qualsiasi azione sia, la gestirò nell'action
        EntiConvenzionatiForm form = new EntiConvenzionatiForm();
        form.getEnteConvenzOrgList().setTable(getForm().getEnteConvenzOrgList().getTable());
        int riga = getForm().getEnteConvenzOrgList().getTable().getCurrentRowIndex();
        // form = form nuova
        form.getVersRif().getVersatore().setValue(
                getForm().getVers().getNm_vers().parse() + " ( " + getForm().getVers().getDs_vers().parse() + ")");
        form.getVersRif().getNm_ambiente_vers().setValue(getForm().getVers().getId_ambiente_vers().getDecodedValue());
        form.getVersRif().getId_vers().setValue(((BaseRowInterface) getForm().getVersList().getTable().getCurrentRow())
                .getBigDecimal("id_vers").toPlainString());
        form.getVersRif().getId_ente_convenz().setValue("" + getForm().getVers().getId_ambiente_vers().parse());

        redirectToAction(Application.Actions.ENTI_CONVENZIONATI, "?operation=listNavigationOnClick&navigationEvent="
                + action + "&table=" + form.getEnteConvenzOrgList().getName() + "&riga=" + riga, form);
    }

    private void initAmbienteCombo() {
        BaseTable ambienteEnteTable = amministrazioneEjb
                .getUsrVAbilAmbEnteConvenzTableBean(new BigDecimal(getUser().getIdUtente()));
        DecodeMap mappaAmbienteEnte = new DecodeMap();
        mappaAmbienteEnte.populatedMap(ambienteEnteTable, "id_ambiente_ente_convenz", "nm_ambiente_ente_convenz");
        getForm().getAmbienteVers().getId_ambiente_ente_convenz().setDecodeMap(mappaAmbienteEnte);
        getForm().getAmbienteVers().getId_ente_gestore().setDecodeMap(new DecodeMap());
        getForm().getAmbienteVers().getId_ente_conserv().setDecodeMap(new DecodeMap());
    }

    @Override
    public void reloadAfterGoBack(String publisherName) {

        try {
            if (getLastPublisher().equals(Application.Publisher.AMBIENTE_VERS_DETAIL)) {

                PigAmbienteVersRowBean ambienteVersRowBean = new PigAmbienteVersRowBean();
                VisAmbienteVers visAmbiente = getForm().getVisAmbienteVers();
                ambienteVersRowBean.setNmAmbienteVers(visAmbiente.getNm_ambiente_vers().parse());

                PigAmbienteVersTableBean ambienteVersTableBean = amministrazioneEjb
                        .getPigAmbienteVersAbilitatiTableBean(ambienteVersRowBean, getUser().getIdUtente(), true);

                ambienteVersTableBean.addSortingRule(PigAmbienteVersTableDescriptor.COL_NM_AMBIENTE_VERS,
                        SortingRule.ASC);
                ambienteVersTableBean.sort();

                getForm().getAmbienteVersList().setTable(ambienteVersTableBean);
                getForm().getAmbienteVersList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
                getForm().getAmbienteVersList().getTable().first();

            } else if (getLastPublisher().equals(Application.Publisher.VERS_DETAIL)
                    || getLastPublisher().equals(Application.Publisher.DUPLICA_VERS_DETAIL)) {

                populateComboVisVers();

                if (getForm().getVersList().getTable() != null && !getForm().getVersList().getTable().isEmpty()) {
                    // Rieseguo la ricerca della pagina precedente che avr\u00e0 anch'essa subito modifiche
                    int paginaCorrenteDocNonVers = getForm().getVersList().getTable().getCurrentPageIndex();
                    int inizioDocNonVers = getForm().getVersList().getTable().getFirstRowPageIndex();

                    String nmVers = getForm().getVisVers().getNm_vers().parse();
                    String nmAmbienteVers = getForm().getVisVers().getNm_ambiente_vers().parse();
                    String nmAmbienteSacer = getForm().getVisVers().getNm_ambiente_sacer().getDecodedValue();
                    String nmEnteSacer = getForm().getVisVers().getNm_ente_sacer().getDecodedValue();
                    String nmStrutSacer = getForm().getVisVers().getNm_strut_sacer().getDecodedValue();
                    String nmUseridSacer = getForm().getVisVers().getNm_userid_sacer().parse();
                    String nmTipoVersatore = getForm().getVisVers().getNm_tipo_versatore().parse();

                    // MEV 27543
                    String nmAmbienteEnteConvenz = getForm().getVisVers().getNm_ambiente_ente_convenz().parse();
                    String nmEnteConvenz = getForm().getVisVers().getNm_ente_convenz().parse();

                    // MEV26162
                    Long idVers = null;
                    if (getForm().getVisVers().getId_vers().parse() != null) {
                        idVers = getForm().getVisVers().getId_vers().parse().longValue();
                    }

                    PigVRicVersTableBean ricVersTableBean = amministrazioneEjb.getPigVRicVersTableBean(idVers, nmVers,
                            nmAmbienteVers, nmAmbienteSacer, nmEnteSacer, nmStrutSacer, nmUseridSacer,
                            nmAmbienteEnteConvenz, nmEnteConvenz, getUser().getIdUtente(), nmTipoVersatore);

                    getForm().getVersList().setTable(ricVersTableBean);
                    int pageSize = getForm().getVersList().getTable().getPageSize();
                    getForm().getVersList().getTable().setPageSize(pageSize);
                    // Rieseguo la query se necessario
                    this.lazyLoadGoPage(getForm().getVersList(), paginaCorrenteDocNonVers);
                    // Ritorno alla pagina
                    getForm().getVersList().getTable().setCurrentRowIndex(inizioDocNonVers);

                }

            } else if (getLastPublisher().equals(Application.Publisher.TIPO_OBJECT_DETAIL)) {

                getForm().getVers().setViewMode();

                BigDecimal idVers = ((BaseRowInterface) getForm().getVersList().getTable().getCurrentRow())
                        .getBigDecimal("id_vers");
                PigTipoObjectTableBean tipoObjTableBean = amministrazioneEjb.getPigTipoObjectTableBean(idVers);

                getForm().getTipoObjectList().setTable(tipoObjTableBean);
                getForm().getTipoObjectList().getTable().first();
                getForm().getTipoObjectList().setStatus(Status.view);
                getForm().getTipoObjectList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);

            } else if (getLastPublisher().equals(Application.Publisher.TIPO_FILE_OBJECT_DETAIL)) {
                getForm().getTipoObject().getFl_contr_hash().setHidden(false);
                getForm().getTipoObject().setViewMode();

                BigDecimal idTipoObject = ((PigTipoObjectRowBean) getForm().getTipoObjectList().getTable()
                        .getCurrentRow()).getIdTipoObject();
                PigTipoFileObjectTableBean tipoFileObjTableBean = amministrazioneEjb
                        .getPigTipoFileObjectTableBean(idTipoObject);

                getForm().getTipoFileObjectList().setTable(tipoFileObjTableBean);
                getForm().getTipoFileObjectList().getTable().first();
                getForm().getTipoFileObjectList().setStatus(Status.view);
                getForm().getTipoFileObjectList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);

            } else if (getLastPublisher().equals(Application.Publisher.SOP_CLASS_DICOM_DETAIL)) {

                getForm().getSopClass().setViewMode();
                getForm().getVisSopClass().clear();
                PigSopClassDicomRowBean sopClassDicomRowBean = new PigSopClassDicomRowBean();
                PigSopClassDicomTableBean sopClassDicomTableBean = amministrazioneEjb
                        .getPigSopClassDicomTableBean(sopClassDicomRowBean.getCdSopClassDicom(), null);

                sopClassDicomTableBean.addSortingRule(PigSopClassDicomTableDescriptor.COL_CD_SOP_CLASS_DICOM,
                        SortingRule.ASC);
                sopClassDicomTableBean.sort();

                getForm().getSopClassList().setTable(sopClassDicomTableBean);
                getForm().getSopClassList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
                getForm().getSopClassList().getTable().first();

            } else if (getLastPublisher().equals(Application.Publisher.SOP_CLASS_DICOM_VERS_DETAIL)) {

                getForm().getVersTab().setCurrentTab(getForm().getVersTab().getPigSopClassDicomVers());

                BigDecimal idVers = ((BaseRowInterface) getForm().getVersList().getTable().getCurrentRow())
                        .getBigDecimal("id_vers");
                PigSopClassDicomTableBean pigSopClassDicomTableBean = amministrazioneEjb
                        .getPigSopClassVersTableBean(idVers);

                pigSopClassDicomTableBean.addSortingRule(PigSopClassDicomTableDescriptor.COL_CD_SOP_CLASS_DICOM,
                        SortingRule.ASC);
                pigSopClassDicomTableBean.sort();

                getForm().getSopClassList().setTable(pigSopClassDicomTableBean);
                getForm().getSopClassList().getTable().first();
                getForm().getSopClassList().setStatus(Status.view);
                getForm().getSopClassList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);

            } else if (getLastPublisher().equals(Application.Publisher.XSD_DATI_SPEC_DETAIL)) {
                BigDecimal idTipoObject = null;
                BigDecimal idTipoFileObject = null;
                if (publisherName.equals(Application.Publisher.TIPO_OBJECT_DETAIL)) {
                    getForm().getTipoObjTab().setCurrentTab(getForm().getTipoObjTab().getPigXsdDatiSpec());
                    idTipoObject = ((PigTipoObjectRowBean) getForm().getTipoObjectList().getTable().getCurrentRow())
                            .getIdTipoObject();
                }
                if (publisherName.equals(Application.Publisher.TIPO_FILE_OBJECT_DETAIL)) {
                    idTipoFileObject = ((PigTipoFileObjectRowBean) getForm().getTipoFileObjectList().getTable()
                            .getCurrentRow()).getIdTipoFileObject();
                }

                PigXsdDatiSpecTableBean pigXsdDatiSpecTableBean = amministrazioneEjb
                        .getPigXsdDatiSpecTableBean(idTipoObject, idTipoFileObject);

                getForm().getXsdDatiSpecList().setTable(pigXsdDatiSpecTableBean);
                getForm().getXsdDatiSpecList().getTable().first();
                getForm().getXsdDatiSpecList().setStatus(Status.view);
                getForm().getXsdDatiSpecList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);

            } else if (getLastPublisher().equals(Application.Publisher.ATTRIB_DATI_SPEC_DETAIL)) {

                PigXsdDatiSpecRowBean xsdDatiSpecRowBean = ((PigXsdDatiSpecRowBean) getForm().getXsdDatiSpecList()
                        .getTable().getCurrentRow());
                PigAttribDatiSpecTableBean attribDatiSpecTableBean = amministrazioneEjb
                        .getPigAttribDatiSpecTableBean(xsdDatiSpecRowBean.getIdXsdSpec());

                getForm().getAttribDatiSpecList().setTable(attribDatiSpecTableBean);
                getForm().getAttribDatiSpecList().getTable().first();
                getForm().getAttribDatiSpecList().setStatus(Status.view);
                getForm().getAttribDatiSpecList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
            } else if (getLastPublisher().equals(Application.Publisher.DETTAGLIO_STATO_VERSAMENTO_OGGETTO)) {
                getForm().getStatiVersamentoObjectList().setStatus(Status.view);
                getForm().getStatoVersamentoObjectDetail().setStatus(Status.view);
                getForm().getStatoVersamentoObjectDetail().setViewMode();
            } else if (getLastPublisher().equals(Application.Publisher.DETTAGLIO_VERSATORE_GENERAZIONE_OGGETTI)) {
                BigDecimal idTipoObj = ((PigTipoObjectRowBean) getForm().getTipoObjectList().getTable().getCurrentRow())
                        .getIdTipoObject();
                String tiVersFile = ((PigTipoObjectRowBean) getForm().getTipoObjectList().getTable().getCurrentRow())
                        .getTiVersFile();
                BigDecimal idTrasf = ((PigTipoObjectRowBean) getForm().getTipoObjectList().getTable().getCurrentRow())
                        .getIdTrasf();
                // Corrispondenze in Sacer per tipologia oggetto
                if (tiVersFile.equals(it.eng.sacerasi.common.Constants.TipoVersamento.NO_ZIP.name())
                        || tiVersFile.equals(it.eng.sacerasi.common.Constants.TipoVersamento.ZIP_CON_XML_SACER.name())
                        || tiVersFile.equals(it.eng.sacerasi.common.Constants.TipoVersamento.ZIP_NO_XML_SACER.name())) {
                    getForm().getTipoObject().getTi_dich_vers().setDecodeMap(ComboGetter.getMappaTiDichVers());
                    PigDichVersSacerTipoObjRowBean dichVersSacerTipoObjRowBean = amministrazioneEjb
                            .getPigDichVersSacerTipoObjFromIdTipoObj(idTipoObj);
                    getForm().getTipoObject().getTi_dich_vers().setValue(dichVersSacerTipoObjRowBean.getTiDichVers());
                    if (StringUtils.isNoneBlank(dichVersSacerTipoObjRowBean.getTiDichVers())) {
                        try {
                            getForm().getTipoObject().getId_organiz_iam().setDecodeMap(
                                    getMappaDlCompositoOrganiz(dichVersSacerTipoObjRowBean.getTiDichVers()));
                            getForm().getTipoObject().getId_organiz_iam()
                                    .setValue(dichVersSacerTipoObjRowBean.getIdOrganizIam().toString());
                        } catch (Exception e) {
                            getMessageBox().addError(e.getMessage());
                        }
                    } else {
                        getForm().getTipoObject().getTi_dich_vers().setViewMode();
                        getForm().getTipoObject().getId_organiz_iam().setViewMode();
                        getForm().getTipoObject().getTi_dich_vers().setDecodeMap(new DecodeMap());
                        getForm().getTipoObject().getId_organiz_iam().setDecodeMap(new DecodeMap());
                    }
                } else {
                    getForm().getTipoObject().getTi_dich_vers().setViewMode();
                    getForm().getTipoObject().getId_organiz_iam().setViewMode();
                    getForm().getTipoObject().getTi_dich_vers().setDecodeMap(new DecodeMap());
                    getForm().getTipoObject().getId_organiz_iam().setDecodeMap(new DecodeMap());
                }

                // Versatori per cui si generano oggetti
                if (tiVersFile.equals(it.eng.sacerasi.common.Constants.TipoVersamento.DA_TRASFORMARE.name())
                        && idTrasf != null) {
                    getForm().getTipoObjTab().getVersatoriGenerazioneOggettiTab().setHidden(false);
                    PigVersTipoObjectDaTrasfTableBean versTipoObjectDaTrasfTableBean = amministrazioneEjb
                            .getPigVersTipoObjectDaTrasfTableBean(idTipoObj);
                    getForm().getVersatoriGenerazioneOggettiList().setTable(versTipoObjectDaTrasfTableBean);
                    getForm().getVersatoriGenerazioneOggettiList().getTable().first();
                    getForm().getVersatoriGenerazioneOggettiList().setStatus(Status.view);
                    getForm().getVersatoriGenerazioneOggettiList().getTable()
                            .setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
                } else {
                    getForm().getTipoObjTab().getVersatoriGenerazioneOggettiTab().setHidden(true);
                }

            } else if (publisherName.equals(Application.Publisher.VERS_DETAIL)) {
                BigDecimal idVers = getForm().getVers().getId_vers().parse();
                if (idVers != null) {
                    loadVers(idVers);
                }
            } else if (getLastPublisher().equals(Application.Publisher.DETTAGLIO_SET_PARAMETRI_VERSATORE)) {
                BigDecimal idVersTipoObjectDaTrasf = ((PigVValoreSetParamTrasfRowBean) getForm()
                        .getSetParametriVersatoreList().getTable().getCurrentRow()).getIdVersTipoObjectDaTrasf();

                // ricarico i parametri della trasformazione per questo versatore
                loadSetParametriVersatore(idVersTipoObjectDaTrasf);
            }

            postLoad();
        } catch (EMFError emf) {
            log.error(emf.getMessage());
        } catch (ParerUserError ex) {
            log.error(ex.getMessage());
        }
    }

    @Override
    public void visAmbienteButton() throws EMFError {

        PigAmbienteVersRowBean ambienteVersRowBean = new PigAmbienteVersRowBean();
        VisAmbienteVers ambienteVers = getForm().getVisAmbienteVers();
        ambienteVers.post(getRequest());

        ambienteVers.copyToBean(ambienteVersRowBean);

        PigAmbienteVersTableBean ambienteVersTableBean = amministrazioneEjb
                .getPigAmbienteVersAbilitatiTableBean(ambienteVersRowBean, getUser().getIdUtente(), true);

        ambienteVersTableBean.addSortingRule(PigAmbienteVersTableDescriptor.COL_NM_AMBIENTE_VERS, SortingRule.ASC);
        ambienteVersTableBean.sort();

        getForm().getAmbienteVersList().setTable(ambienteVersTableBean);
        getForm().getAmbienteVersList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
        getForm().getAmbienteVersList().getTable().first();

        getForm().getParametriAmministrazioneAmbienteSection().setLoadOpened(false);
        getForm().getParametriConservazioneAmbienteSection().setLoadOpened(false);
        getForm().getParametriGestioneAmbienteSection().setLoadOpened(false);
    }

    @Override
    public void visVersButton() throws EMFError {
        getForm().getVisVers().post(getRequest());

        String nmVers = getForm().getVisVers().getNm_vers().parse();
        String nmAmbienteVers = getForm().getVisVers().getNm_ambiente_vers().parse();
        String nmAmbienteSacer = getForm().getVisVers().getNm_ambiente_sacer().getDecodedValue();
        String nmEnteSacer = getForm().getVisVers().getNm_ente_sacer().getDecodedValue();
        String nmStrutSacer = getForm().getVisVers().getNm_strut_sacer().getDecodedValue();
        String nmUseridSacer = getForm().getVisVers().getNm_userid_sacer().parse();
        String nmTipoVersatore = getForm().getVisVers().getNm_tipo_versatore().parse();

        // MEV 27543
        String nmAmbienteEnteConvenz = getForm().getVisVers().getNm_ambiente_ente_convenz().parse();
        String nmEnteConvenz = getForm().getVisVers().getNm_ente_convenz().parse();

        // MEV26162
        Long idVers = null;
        if (getForm().getVisVers().getId_vers().parse() != null) {
            idVers = getForm().getVisVers().getId_vers().parse().longValue();
        }

        PigVRicVersTableBean ricVersTableBean = amministrazioneEjb.getPigVRicVersTableBean(idVers, nmVers,
                nmAmbienteVers, nmAmbienteSacer, nmEnteSacer, nmStrutSacer, nmUseridSacer, nmAmbienteEnteConvenz,
                nmEnteConvenz, getUser().getIdUtente(), nmTipoVersatore);
        getForm().getVersList().setTable(ricVersTableBean);
        getForm().getVersList().getTable().first();

        getForm().getVersList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
        getForm().getVersList().getTable().first();
        getForm().getVersList().setUserOperations(true, true, true, true);

        getForm().getParametriAmministrazioneVersatoreSection().setLoadOpened(false);
        getForm().getParametriConservazioneVersatoreSection().setLoadOpened(false);
        getForm().getParametriGestioneVersatoreSection().setLoadOpened(false);

        forwardToPublisher(Application.Publisher.CERCA_VERS);
    }

    @Override
    public void visSopClassButton() throws EMFError {

        PigSopClassDicomRowBean sopClassDicomRowBean = new PigSopClassDicomRowBean();

        VisSopClass sopClass = getForm().getVisSopClass();
        sopClass.post(getRequest());

        sopClass.copyToBean(sopClassDicomRowBean);

        PigSopClassDicomTableBean sopClassDicomTableBean = amministrazioneEjb.getPigSopClassDicomTableBean(
                sopClassDicomRowBean.getCdSopClassDicom(), sopClassDicomRowBean.getDsSopClassDicom());

        sopClassDicomTableBean.addSortingRule(PigSopClassDicomTableDescriptor.COL_CD_SOP_CLASS_DICOM, SortingRule.ASC);
        sopClassDicomTableBean.sort();

        getForm().getSopClassList().setTable(sopClassDicomTableBean);
        getForm().getSopClassList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
        getForm().getSopClassList().getTable().first();
        getForm().getSopClassList().setUserOperations(true, true, true, true);
        forwardToPublisher(Application.Publisher.CERCA_SOP_CLASS);
    }

    @Secure(action = "Menu.AmministrazioneVersatori.GestioneAmbienti")
    public void ricercaAmbienteVers() throws EMFError {

        getUser().getMenu().reset();
        getUser().getMenu().select("Menu.AmministrazioneVersatori.GestioneAmbienti");

        getForm().getAmbienteVersList().clear();
        getForm().getVisAmbienteVers().getNm_ambiente_vers().clear();
        getForm().getVisAmbienteVers().getNm_ambiente_vers().setEditMode();
        getForm().getVisAmbienteVers().getVisAmbienteButton().setEditMode();

        // Abilita la possibilità di inserire un ambiente versatore
        getForm().getAmbienteVersList().setHideInsertButton(!amministrazioneEjb
                .isCreaAmbienteActive(getUser().getIdUtente(), configHelper.getParamApplicApplicationName()));

        visAmbienteButton();

        forwardToPublisher(Application.Publisher.CERCA_AMBIENTE_VERS);
    }

    @Secure(action = "Menu.AmministrazioneVersatori.GestioneVersatori")
    // Corrisponde a strutturaRicerca.jsp
    public void ricercaVers() throws EMFError {

        getUser().getMenu().reset();
        getUser().getMenu().select("Menu.AmministrazioneVersatori.GestioneVersatori");

        // MAC 27920
        getSession().removeAttribute("loadVersDaMenu");

        getForm().getVersList().clear();
        getForm().getVisVers().reset();
        getForm().getVisVers().setEditMode();
        populateComboVisVers();
        postLoad();
        forwardToPublisher(Application.Publisher.CERCA_VERS);
    }

    @Secure(action = "Menu.AmministrazioneSistema.GestioneSopClass")
    public void ricercaSopClassDicom() {

        getUser().getMenu().reset();
        getUser().getMenu().select("Menu.AmministrazioneSistema.GestioneSopClass");

        getForm().getVisSopClass().clear();
        getForm().getVisSopClass().setEditMode();
        getForm().getSopClassList().clear();

        forwardToPublisher(Application.Publisher.CERCA_SOP_CLASS);
    }

    private void salvaAmbienteVers() throws EMFError, IncoherenceException, ParerUserError {

        getForm().getParametriAmministrazioneAmbienteList().post(getRequest());
        getForm().getParametriConservazioneAmbienteList().post(getRequest());
        getForm().getParametriGestioneAmbienteList().post(getRequest());

        getMessageBox().clear();

        AmbienteVers ambienteVers = getForm().getAmbienteVers();
        ambienteVers.post(getRequest());

        PigAmbienteVersRowBean ambienteVersRowBean = new PigAmbienteVersRowBean();
        ambienteVers.copyToBean(ambienteVersRowBean);

        String nmAmb = ambienteVers.getNm_ambiente_vers().parse();

        if (ambienteVers.getNm_ambiente_vers().parse() != null) {
            // se in insert controllo che il nome non esista già
            if (amministrazioneEjb.getPigAmbienteVersRowBean(nmAmb) != null
                    && (getForm().getAmbienteVersList().getStatus().equals(Status.insert))) {
                getMessageBox().addError("Nome Ambiente gi\u00E0 utilizzato all'interno del database. <br/>");
            }
        } else {
            // il nome non deve essere nullo
            getMessageBox().addError("Errore di compilazione form: Nome ambiente non inserito. <br/>");
        }

        if (ambienteVers.getDs_ambiente_vers().parse() == null) {
            getMessageBox().addError("Errore di compilazione form: descrizione ambiente non inserito. <br/>");
        }

        // MAC25995
        if (ambienteVers.getDt_ini_val().parse() == null || ambienteVers.getDt_fine_val().parse() == null) {
            getMessageBox()
                    .addError("Errore di compilazione form: data inizio e fine validità ambiente non inserite. <br/>");
        } else if (ambienteVers.getDt_fine_val().parse().before(ambienteVers.getDt_ini_val().parse())) {
            getMessageBox().addError(
                    "La data di inizio validità deve essere minore o uguale alla data di fine validità.</br>");
        }

        if (ambienteVers.getId_ambiente_ente_convenz().parse() == null) {
            getMessageBox().addError("Errore di compilazione form: nome ambiente versatore non inserito. <br/>");
        }

        if (ambienteVers.getId_ente_conserv().parse() == null) {
            getMessageBox().addError("Errore di compilazione form: nome ente conservatore non inserito. <br/>");
        }

        if (ambienteVers.getId_ente_gestore().parse() == null) {
            getMessageBox().addError("Errore di compilazione form: nome ente gestore non inserito. <br/>");
        }

        PigParamApplicTableBean parametriAmministrazione = (PigParamApplicTableBean) getForm()
                .getParametriAmministrazioneAmbienteList().getTable();
        PigParamApplicTableBean parametriConservazione = (PigParamApplicTableBean) getForm()
                .getParametriConservazioneAmbienteList().getTable();
        PigParamApplicTableBean parametriGestione = (PigParamApplicTableBean) getForm()
                .getParametriGestioneAmbienteList().getTable();

        // Controllo valori possibili su ente
        for (PigParamApplicRowBean paramApplicRowBean : parametriAmministrazione) {
            if (paramApplicRowBean.getString("ds_lista_valori_ammessi") != null
                    && !paramApplicRowBean.getString("ds_lista_valori_ammessi").equals("")) {
                if (paramApplicRowBean.getString("ds_valore_param_applic_ambiente_amm") != null
                        && !paramApplicRowBean.getString("ds_valore_param_applic_ambiente_amm").equals("")) {
                    if (!amministrazioneEjb.inValoriPossibili(
                            paramApplicRowBean.getString("ds_valore_param_applic_ambiente_amm"),
                            paramApplicRowBean.getString("ds_lista_valori_ammessi"))) {
                        getMessageBox()
                                .addError("Il valore del parametro non è compreso tra i valori ammessi sul parametro");
                    }
                }
            }
        }

        for (PigParamApplicRowBean paramApplicRowBean : parametriConservazione) {
            if (paramApplicRowBean.getString("ds_lista_valori_ammessi") != null
                    && !paramApplicRowBean.getString("ds_lista_valori_ammessi").equals("")) {
                if (paramApplicRowBean.getString("ds_valore_param_applic_ambiente_cons") != null
                        && !paramApplicRowBean.getString("ds_valore_param_applic_ambiente_cons").equals("")) {
                    if (!amministrazioneEjb.inValoriPossibili(
                            paramApplicRowBean.getString("ds_valore_param_applic_ambiente_cons"),
                            paramApplicRowBean.getString("ds_lista_valori_ammessi"))) {
                        getMessageBox()
                                .addError("Il valore del parametro non è compreso tra i valori ammessi sul parametro");
                    }
                }
            }
        }

        for (PigParamApplicRowBean paramApplicRowBean : parametriGestione) {
            if (paramApplicRowBean.getString("ds_lista_valori_ammessi") != null
                    && !paramApplicRowBean.getString("ds_lista_valori_ammessi").equals("")) {
                if (paramApplicRowBean.getString("ds_valore_param_applic_ambiente_gest") != null
                        && !paramApplicRowBean.getString("ds_valore_param_applic_ambiente_gest").equals("")) {
                    if (!amministrazioneEjb.inValoriPossibili(
                            paramApplicRowBean.getString("ds_valore_param_applic_ambiente_gest"),
                            paramApplicRowBean.getString("ds_lista_valori_ammessi"))) {
                        getMessageBox()
                                .addError("Il valore del parametro non è compreso tra i valori ammessi sul parametro");
                    }
                }
            }
        }

        if (getMessageBox().isEmpty()) {
            /*
             * Codice aggiuntivo per il logging...
             */
            LogParam param = SpagoliteLogUtil.getLogParam(
                    paramApplicHelper.getApplicationName().getDsValoreParamApplic(), getUser().getUsername(),
                    SpagoliteLogUtil.getPageName(this));
            param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
            BigDecimal idAmb = null;
            if (getForm().getAmbienteVersList().getStatus().equals(Status.update)) {
                param.setNomeAzione(SpagoliteLogUtil.getToolbarUpdate());
                idAmb = ((PigAmbienteVersRowBean) getForm().getAmbienteVersList().getTable().getCurrentRow())
                        .getIdAmbienteVers();
                Date dataOdierna = DateUtil.getDataOdiernaNoTime();
                if (ambienteVersRowBean.getDtIniVal().compareTo(dataOdierna) > 0
                        || ambienteVersRowBean.getDtFineVal().compareTo(dataOdierna) < 0) {
                    checkAndSaveModificaAmbiente(param, ambienteVersRowBean, parametriAmministrazione,
                            parametriConservazione, parametriGestione);
                } else {
                    eseguiModificaSalvataggioAmbienteVersatore(param, idAmb, ambienteVersRowBean,
                            parametriAmministrazione, parametriConservazione, parametriGestione);
                }
            } else if (getForm().getAmbienteVersList().getStatus().equals(Status.insert)) {
                param.setNomeAzione(SpagoliteLogUtil.getToolbarInsert());
                PigAmbienteVersTableBean ambienteVersTableBean = new PigAmbienteVersTableBean();
                ambienteVers.copyToBean(ambienteVersRowBean);
                idAmb = amministrazioneEjb.insertPigAmbiente(param, ambienteVersRowBean, parametriAmministrazione,
                        parametriConservazione, parametriGestione);
                ambienteVersTableBean.add(ambienteVersRowBean);
                ambienteVersTableBean.setPageSize(1);
                ambienteVersTableBean.setCurrentRowIndex(0);
                getForm().getAmbienteVersList().setTable(ambienteVersTableBean);
                getMessageBox().addInfo("Nuovo ambiente salvato con successo");
                reloadDettaglioAmbiente(idAmb);
            }
        }
        forwardToPublisher(Application.Publisher.AMBIENTE_VERS_DETAIL);
    }

    private void reloadDettaglioAmbiente(BigDecimal idAmb) throws EMFError, ParerUserError {
        getMessageBox().setViewMode(MessageBox.ViewMode.alert);

        loadAmbienteVers(idAmb);

        getForm().getAmbienteVers().setViewMode();
        getForm().getAmbienteVers().setStatus(Status.view);
        getForm().getAmbienteVersList().setStatus(Status.view);
    }

    public void confermaEseguiModificaSalvataggioAmbienteVersatore() {
        if (getSession().getAttribute("salvataggioAttributesAmbienteVers") != null) {
            try {
                Object[] attrAmbiente = (Object[]) getSession().getAttribute("salvataggioAttributesAmbienteVers");
                LogParam param = (LogParam) attrAmbiente[0];
                PigAmbienteVersRowBean ambienteVersRowBean = (PigAmbienteVersRowBean) attrAmbiente[1];
                PigParamApplicTableBean parametriAmministrazione = (PigParamApplicTableBean) attrAmbiente[2];
                PigParamApplicTableBean parametriConservazione = (PigParamApplicTableBean) attrAmbiente[3];
                PigParamApplicTableBean parametriGestione = (PigParamApplicTableBean) attrAmbiente[4];
                BigDecimal idAmb = ambienteVersRowBean.getIdAmbienteVers();
                eseguiModificaSalvataggioAmbienteVersatore(param, idAmb, ambienteVersRowBean, parametriAmministrazione,
                        parametriConservazione, parametriGestione);
            } catch (IncoherenceException | ParerUserError | EMFError ex) {
                getMessageBox().addError(ex.getMessage());
            }
        }
        forwardToPublisher(Application.Publisher.AMBIENTE_VERS_DETAIL);
    }

    public void annullaEseguiModificaSalvataggioAmbienteVersatore() throws Throwable {
        getSession().removeAttribute("salvataggioAttributesAmbienteVers");
        BigDecimal idAmbienteVers = getForm().getAmbienteVers().getId_ambiente_vers().parse();
        loadAmbienteVers(idAmbienteVers);
        forwardToPublisher(Application.Publisher.AMBIENTE_VERS_DETAIL);
    }

    public void eseguiModificaSalvataggioAmbienteVersatore(LogParam param, BigDecimal idAmb,
            PigAmbienteVersRowBean ambienteVersRowBean, PigParamApplicTableBean parametriAmministrazione,
            PigParamApplicTableBean parametriConservazione, PigParamApplicTableBean parametriGestione)
            throws IncoherenceException, EMFError, ParerUserError {
        amministrazioneEjb.updatePigAmbienteRowBean(param, idAmb, ambienteVersRowBean, parametriAmministrazione,
                parametriConservazione, parametriGestione);
        ambienteVersRowBean.setIdAmbienteVers(idAmb);
        getMessageBox().addInfo("Update ambiente effettuato con successo");
        reloadDettaglioAmbiente(idAmb);
    }

    private void checkAndSaveModificaAmbiente(LogParam param, PigAmbienteVersRowBean ambienteVersRowBean,
            PigParamApplicTableBean parametriAmministrazione, PigParamApplicTableBean parametriConservazione,
            PigParamApplicTableBean parametriGestione) throws IncoherenceException, EMFError, ParerUserError {
        Object[] attrAmbiente = new Object[5];
        attrAmbiente[0] = param;
        attrAmbiente[1] = ambienteVersRowBean;
        attrAmbiente[2] = parametriAmministrazione;
        attrAmbiente[3] = parametriConservazione;
        attrAmbiente[4] = parametriGestione;
        getSession().setAttribute("salvataggioAttributesAmbienteVers", attrAmbiente);
        String customMessage = "";
        // Se esistono versatori appartenenti all’ambiente validi alla data
        if (amministrazioneEjb.existPigVersValidiDataOdierna(ambienteVersRowBean.getIdAmbienteVers())) {
            customMessage = "Attenzione: esiste almeno un versatore valido appartenente all’ambiente. Confermare la modifica dell’ambiente?";
            getRequest().setAttribute("customMessageSalvataggioAmbienteVers", customMessage);
            getRequest().setAttribute("customBoxSalvataggioAmbienteVersControllo1", true);
        } else {
            secondCheckChangeStatusAndSaveAmbienteVers();
        }
    }

    public void secondCheckChangeStatusAndSaveAmbienteVers() throws IncoherenceException, EMFError, ParerUserError {
        if (getSession().getAttribute("salvataggioAttributesAmbienteVers") != null) {
            Object[] attrAmbiente = (Object[]) getSession().getAttribute("salvataggioAttributesAmbienteVers");
            LogParam param = (LogParam) attrAmbiente[0];
            PigAmbienteVersRowBean ambienteVersRowBean = (PigAmbienteVersRowBean) attrAmbiente[1];
            PigParamApplicTableBean parametriAmministrazione = (PigParamApplicTableBean) attrAmbiente[2];
            PigParamApplicTableBean parametriConservazione = (PigParamApplicTableBean) attrAmbiente[3];
            PigParamApplicTableBean parametriGestione = (PigParamApplicTableBean) attrAmbiente[4];
            String customMessage = "";
            // Se esistono utenti con abilitazione = UN_AMBIENTE per l’ambiente oggetto di modifica attivi alla data
            // corrente
            if (amministrazioneEjb.existUtentiUnAmbiente(ambienteVersRowBean.getIdAmbienteVers())) {
                customMessage = "Attenzione: esiste almeno un utente attivo con abilitazione all’ambiente. Confermare la modifica dell’ambiente?";
                getRequest().setAttribute("customMessageSalvataggioAmbienteVers", customMessage);
                getRequest().setAttribute("customBoxSalvataggioAmbienteVersControllo2", true);
            } else {
                eseguiModificaSalvataggioAmbienteVersatore(param, ambienteVersRowBean.getIdAmbienteVers(),
                        ambienteVersRowBean, parametriAmministrazione, parametriConservazione, parametriGestione);
            }
        }
        forwardToPublisher(Application.Publisher.AMBIENTE_VERS_DETAIL);
    }

    private void salvaVers() throws EMFError, ParerUserError {
        getMessageBox().clear();
        Vers vers = getForm().getVers();
        vers.post(getRequest());
        String tipologia = vers.getTipologia().parse();
        String nmVers = vers.getNm_vers().parse();
        String dsVers = vers.getDs_vers().parse();
        String dsPathInputFtp = vers.getDs_path_input_ftp().parse();
        String dsPathOutputFtp = vers.getDs_path_output_ftp().parse();
        String dsPathTrasf = vers.getDs_path_trasf().parse();
        BigDecimal idAmb = vers.getId_ambiente_vers().parse();
        BigDecimal idEnteConvenzEc = vers.getId_ente_convenz_ec().parse();
        BigDecimal idEnteFornitEstern = vers.getId_ente_convenz_fe().parse();
        Date dtIniValAppartAmbiente = vers.getDt_ini_val_appart_ambiente().parse();
        Date dtFineValAppartAmbiente = vers.getDt_fin_val_appart_ambiente().parse();
        Date dtIniValAppartEnteSiam = vers.getDt_ini_val_appart_ente_siam().parse();
        Date dtFineValAppartEnteSiam = vers.getDt_fine_val_appart_ente_siam().parse();
        Date dtIniValVers = vers.getDt_ini_val_vers().parse();
        Date dtFineValVers = vers.getDt_fine_val_vers().parse();
        String tiDichVers = vers.getTi_dich_vers().parse();
        BigDecimal idOrganizIam = vers.getId_organiz_iam().parse();
        String flRestituzioneArchivio = vers.getFl_archivio_restituito().parse();
        String flCessato = vers.getFl_cessato().parse();

        getForm().getParametriAmministrazioneVersatoreList().post(getRequest());
        getForm().getParametriConservazioneVersatoreList().post(getRequest());
        getForm().getParametriGestioneVersatoreList().post(getRequest());

        PigVersRowBean versRowBean = new PigVersRowBean();
        vers.copyToBean(versRowBean);

        PigParamApplicTableBean parametriAmministrazione = (PigParamApplicTableBean) getForm()
                .getParametriAmministrazioneVersatoreList().getTable();
        PigParamApplicTableBean parametriConservazione = (PigParamApplicTableBean) getForm()
                .getParametriConservazioneVersatoreList().getTable();
        PigParamApplicTableBean parametriGestione = (PigParamApplicTableBean) getForm()
                .getParametriGestioneVersatoreList().getTable();

        if (!getMessageBox().hasError() && vers.getStatus().equals(Status.update)) {
            Date dataOdierna = DateUtil.getDataOdiernaNoTime();
            // Controllo coerenza flag archivio restituito e date validità versatore
            if (dtIniValVers.compareTo(dataOdierna) <= 0 && dataOdierna.compareTo(dtFineValVers) <= 0
                    && flRestituzioneArchivio.equals("1")) {
                getMessageBox().addError(
                        "Attenzione: il versatore risulta essere valido per cui non è possibile settare il flag di archivio restituito");
            }
        }

        try {
            BigDecimal idVers = null;

            if (validaPerSalvataggioVersatore(vers.getStatus(), idAmb, nmVers, dsVers, dtIniValVers, dtFineValVers,
                    dsPathInputFtp, dsPathOutputFtp, dsPathTrasf, dtIniValAppartAmbiente, dtFineValAppartAmbiente,
                    tipologia, flRestituzioneArchivio, flCessato, idEnteConvenzEc, dtIniValAppartEnteSiam,
                    dtFineValAppartEnteSiam, idEnteFornitEstern, tiDichVers, idOrganizIam, parametriAmministrazione,
                    parametriConservazione, parametriGestione)) {
                /*
                 * Codice aggiuntivo per il logging...
                 */
                LogParam param = SpagoliteLogUtil.getLogParam(
                        paramApplicHelper.getApplicationName().getDsValoreParamApplic(), getUser().getUsername(),
                        Application.Publisher.VERS_DETAIL);
                param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
                if (getForm().getVersList().getStatus().equals(Status.update)) {
                    param.setNomeAzione(SpagoliteLogUtil.getToolbarUpdate());
                    idVers = ((BaseRowInterface) getForm().getVersList().getTable().getCurrentRow())
                            .getBigDecimal("id_vers");
                    Date dataOdierna = DateUtil.getDataOdiernaNoTime();
                    if (versRowBean.getDtIniValVers().compareTo(dataOdierna) > 0
                            || versRowBean.getDtFineValVers().compareTo(dataOdierna) < 0) {
                        checkAndSaveModificaVersatore(param, versRowBean, parametriAmministrazione,
                                parametriConservazione, parametriGestione, tiDichVers, idOrganizIam);
                    } else {
                        eseguiUpdateVersatore(param, idVers, versRowBean, parametriAmministrazione,
                                parametriConservazione, parametriGestione, tiDichVers, idOrganizIam);
                    }
                } else if (getForm().getVersList().getStatus().equals(Status.insert)) {
                    param.setNomeAzione(SpagoliteLogUtil.getToolbarInsert());
                    vers.copyToBean(versRowBean);
                    amministrazioneEjb.insertPigVers(param, getUser().getIdUtente(), versRowBean,
                            parametriAmministrazione, parametriConservazione, parametriGestione, tipologia,
                            idEnteConvenzEc, idEnteFornitEstern, dtIniValAppartEnteSiam, dtFineValAppartEnteSiam,
                            tiDichVers, idOrganizIam);
                    getMessageBox().addInfo("Nuovo versatore salvato con successo");
                    PigVersTableBean table = new PigVersTableBean();
                    table.add(versRowBean);
                    table.setPageSize(1);
                    table.setCurrentRowIndex(0);
                    getForm().getVersList().setTable(table);

                    VisVers visVers = getForm().getVisVers();
                    visVers.clear();

                    PigAmbienteVersRowBean ambienteVers = amministrazioneEjb.getPigAmbienteVersRowBean(idAmb);
                    visVers.getNm_ambiente_vers().setValue(ambienteVers.getNmAmbienteVers());
                    visVers.getNm_vers().setValue(versRowBean.getNmVers());

                    aggiornaDettaglioVersatore();
                }
            }
        } catch (IncoherenceException e) {
            getMessageBox().addError(e.getMessage());
        }
        forwardToPublisher(Application.Publisher.VERS_DETAIL);
    }

    private void checkAndSaveModificaVersatore(LogParam param, PigVersRowBean versRowBean,
            PigParamApplicTableBean parametriAmministrazione, PigParamApplicTableBean parametriConservazione,
            PigParamApplicTableBean parametriGestione, String tiDichVers, BigDecimal idOrganizIam)
            throws IncoherenceException, EMFError, ParerUserError {
        Object[] attrVers = new Object[7];
        attrVers[0] = param;
        attrVers[1] = versRowBean;
        attrVers[2] = parametriAmministrazione;
        attrVers[3] = parametriConservazione;
        attrVers[4] = parametriGestione;
        attrVers[5] = tiDichVers;
        attrVers[6] = idOrganizIam;
        getSession().setAttribute("salvataggioAttributesVers", attrVers);
        String customMessage = "";
        // Se esistono utenti con abilitazione = VERSATORE per il versatore oggetto di modifica attivi alla data
        // corrente
        if (amministrazioneEjb.existUtentiUnVersatore(versRowBean.getIdVers())) {
            customMessage = "Attenzione: esiste almeno un utente attivo con abilitazione al versatore. Confermare la modifica del versatore?";
            getRequest().setAttribute("customMessageSalvataggioVers", customMessage);
            getRequest().setAttribute("customBoxSalvataggioVers", true);
        } else {
            eseguiUpdateVersatore(param, versRowBean.getIdVers(), versRowBean, parametriAmministrazione,
                    parametriConservazione, parametriGestione, tiDichVers, idOrganizIam);
            forwardToPublisher(Application.Publisher.VERS_DETAIL);
        }
    }

    public void confermaEseguiModificaSalvataggioVersatore() {
        if (getSession().getAttribute("salvataggioAttributesVers") != null) {
            try {
                Object[] attrVers = (Object[]) getSession().getAttribute("salvataggioAttributesVers");
                LogParam param = (LogParam) attrVers[0];
                PigVersRowBean versRowBean = (PigVersRowBean) attrVers[1];
                PigParamApplicTableBean parametriAmministrazione = (PigParamApplicTableBean) attrVers[2];
                PigParamApplicTableBean parametriConservazione = (PigParamApplicTableBean) attrVers[3];
                PigParamApplicTableBean parametriGestione = (PigParamApplicTableBean) attrVers[4];
                String tiDichVers = (String) attrVers[5];
                BigDecimal idOrganizIam = (BigDecimal) attrVers[6];
                BigDecimal idVers = versRowBean.getIdVers();
                eseguiUpdateVersatore(param, idVers, versRowBean, parametriAmministrazione, parametriConservazione,
                        parametriGestione, tiDichVers, idOrganizIam);
            } catch (IncoherenceException | EMFError ex) {
                getMessageBox().addError(ex.getMessage());
            } catch (ParerUserError ex) {
                getMessageBox().addError(ex.getDescription());
            }
        }
        forwardToPublisher(Application.Publisher.VERS_DETAIL);
    }

    public void annullaEseguiModificaSalvataggioVersatore() throws Throwable {
        getSession().removeAttribute("salvataggioAttributesVers");
        BigDecimal idVers = getForm().getVers().getId_vers().parse();
        loadVers(idVers);
        forwardToPublisher(Application.Publisher.VERS_DETAIL);
    }

    @Override
    public void confermaModificheVersatore() {
        if (getSession().getAttribute("attributiUpdateVersatore") != null) {
            Object[] attributi = (Object[]) getSession().getAttribute("attributiUpdateVersatore");
            BigDecimal idVers = (BigDecimal) attributi[0];
            PigVersRowBean rowBean = (PigVersRowBean) attributi[1];
            PigParamApplicTableBean parametriAmministrazione = (PigParamApplicTableBean) attributi[2];
            PigParamApplicTableBean parametriConservazione = (PigParamApplicTableBean) attributi[3];
            PigParamApplicTableBean parametriGestione = (PigParamApplicTableBean) attributi[4];
            String tiDichVers = (String) attributi[5];
            BigDecimal idOrganizIam = (BigDecimal) attributi[6];

            try {
                /*
                 * Codice aggiuntivo per il logging...
                 */
                LogParam param = SpagoliteLogUtil.getLogParam(
                        paramApplicHelper.getApplicationName().getDsValoreParamApplic(), getUser().getUsername(),
                        Application.Publisher.VERS_DETAIL, SpagoliteLogUtil.getToolbarUpdate());
                param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
                eseguiUpdateVersatore(param, idVers, rowBean, parametriAmministrazione, parametriConservazione,
                        parametriGestione, tiDichVers, idOrganizIam);
            } catch (Exception e) {
                log.error(e.getMessage());
                getMessageBox().addError("Errore durante il salvataggio del versatore");
            }
            getSession().removeAttribute("attributiUpdateVersatore");
            forwardToPublisher(Application.Publisher.VERS_DETAIL);
        }
    }

    @Override
    public void annullaModificheVersatore() throws Throwable {
        // Nascondo i bottoni con javascript disattivato
        getForm().getVersatoreCustomMessageButtonList().setViewMode();
        getSession().removeAttribute("attributiUpdateVersatore");
        goBack();
    }

    private void eseguiUpdateVersatore(LogParam param, BigDecimal idVers, PigVersRowBean versRowBean,
            PigParamApplicTableBean parametriAmministrazione, PigParamApplicTableBean parametriConservazione,
            PigParamApplicTableBean parametriGestione, String tiDichVers, BigDecimal idOrganizIam)
            throws EMFError, IncoherenceException, ParerUserError {
        amministrazioneEjb.updatePigVers(param, getUser().getIdUtente(), idVers, versRowBean, parametriAmministrazione,
                parametriConservazione, parametriGestione, tiDichVers, idOrganizIam);
        versRowBean.setIdVers(idVers);
        getMessageBox().addInfo("Update versatore effettuato con successo");
        aggiornaDettaglioVersatore();
    }

    private void salvaDuplicaVers() throws EMFError {
        getMessageBox().clear();
        Vers vers = getForm().getVers();
        vers.post(getRequest());
        PigVersRowBean versRowBean = new PigVersRowBean();
        vers.copyToBean(versRowBean);
        PigVRicVersRowBean riga = (PigVRicVersRowBean) getForm().getVersList().getTable().getCurrentRow();
        BigDecimal idAmb = vers.getId_ambiente_vers().parse();
        BigDecimal idVers = riga.getIdVers();
        String nmAmbiente = getForm().getVers().getId_ambiente_vers().getDecodedValue();
        String nmVers = vers.getNm_vers().parse();
        String dsVers = vers.getDs_vers().parse();
        String tipologia = vers.getTipologia().parse();
        BigDecimal idEnteConvenzEc = vers.getId_ente_convenz_ec().parse();
        BigDecimal idEnteFornitEstern = vers.getId_ente_convenz_fe().parse();
        String nmEnteConvenz = vers.getId_ente_convenz_ec().getDecodedValue();
        String nmEnteFornitEstern = vers.getId_ente_convenz_fe().getDecodedValue();
        Date dtIniValAppartEnteSiam = vers.getDt_ini_val_appart_ente_siam().parse();
        Date dtFineValAppartEnteSiam = vers.getDt_fine_val_appart_ente_siam().parse();
        Date dtIniValAppartAmbiente = vers.getDt_ini_val_appart_ambiente().parse();
        Date dtFinValAppartAmbiente = vers.getDt_fin_val_appart_ambiente().parse();
        Date dtIniValVers = vers.getDt_ini_val_vers().parse();
        Date dtFineValVers = vers.getDt_fine_val_vers().parse();
        String dsPathInputFtp = vers.getDs_path_input_ftp().parse();
        String dsPathOutputFtp = vers.getDs_path_output_ftp().parse();
        String dsPathTrasf = vers.getDs_path_trasf().parse();
        String tiDichVers = vers.getTi_dich_vers().parse();
        BigDecimal idOrganizIam = vers.getId_organiz_iam().parse();
        getForm().getParametriAmministrazioneVersatoreList().post(getRequest());
        getForm().getParametriConservazioneVersatoreList().post(getRequest());
        getForm().getParametriGestioneVersatoreList().post(getRequest());
        PigParamApplicTableBean parametriAmministrazione = (PigParamApplicTableBean) getForm()
                .getParametriAmministrazioneVersatoreList().getTable();
        PigParamApplicTableBean parametriConservazione = (PigParamApplicTableBean) getForm()
                .getParametriConservazioneVersatoreList().getTable();
        PigParamApplicTableBean parametriGestione = (PigParamApplicTableBean) getForm()
                .getParametriGestioneVersatoreList().getTable();

        if (validaPerSalvataggioVersatore(vers.getStatus(), idAmb, nmVers, dsVers, dtIniValVers, dtFineValVers,
                dsPathInputFtp, dsPathOutputFtp, dsPathTrasf, dtIniValAppartAmbiente, dtFinValAppartAmbiente, tipologia,
                "0", "0", idEnteConvenzEc, dtIniValAppartEnteSiam, dtFineValAppartEnteSiam, idEnteFornitEstern,
                tiDichVers, idOrganizIam, parametriAmministrazione, parametriConservazione, parametriGestione)) {
            try {
                /*
                 * Codice aggiuntivo per il logging...
                 */
                LogParam param = SpagoliteLogUtil.getLogParam(
                        paramApplicHelper.getApplicationName().getDsValoreParamApplic(), getUser().getUsername(),
                        SpagoliteLogUtil.getPageName(this));
                param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
                param.setNomeAzione(SpagoliteLogUtil.getButtonActionName(getForm(), getForm().getVers(),
                        getForm().getVers().getDuplicaVersatore().getName()));
                Object[] dati = amministrazioneEjb.duplicaVersatore(param, idAmb, idVers, nmAmbiente, nmVers, dsVers,
                        idEnteConvenzEc, nmEnteConvenz, idEnteFornitEstern, nmEnteFornitEstern, dtIniValAppartEnteSiam,
                        dtFineValAppartEnteSiam, dtIniValVers, dtFineValVers, dtIniValAppartAmbiente,
                        dtFinValAppartAmbiente, dsPathInputFtp, dsPathOutputFtp, dsPathTrasf, tiDichVers, idOrganizIam,
                        getUser().getIdUtente());
                /* Si prepara tutto per andare nel dettaglio del versatore appena inserito */
                BigDecimal idOggetto = (BigDecimal) dati[0];
                String msg = (String) dati[1];
                PigVRicVersTableBean table = new PigVRicVersTableBean();
                versRowBean.setIdVers(idOggetto);
                table.add(versRowBean);
                table.setPageSize(1);
                table.setCurrentRowIndex(0);
                getForm().getVersList().setTable(table);
                aggiornaDettaglioVersatore(idOggetto);
                if (msg == null) {
                    getMessageBox().addInfo("Versatore duplicato con successo");
                } else {
                    getMessageBox().addWarning("Versatore duplicato con successo. " + msg);
                }
                setLastPublisher(Application.Publisher.CERCA_VERS);
                forwardToPublisher(Application.Publisher.VERS_DETAIL);
            } catch (ParerUserError e) {
                getMessageBox().addError(e.getDescription());
                forwardToPublisher(getLastPublisher());
            } catch (Exception ex) {
                getMessageBox().addError(ex.getMessage());
                log.error(ex.getMessage());
                forwardToPublisher(getLastPublisher());
            }
        } else {
            forwardToPublisher(getLastPublisher());
        }
    }

    /*
     * Validazione campi salvataggio versatore per inserimento/modifica/importa/duplica
     */
    private boolean validaPerSalvataggioVersatore(Status status, BigDecimal idAmbienteVers, String nmVers,
            String dsVers, Date dtIniValVers, Date dtFineValVers, String dsPathInputFtp, String dsPathOutputFtp,
            String dsPathTrasf, Date dtIniValAppartAmbiente, Date dtFineValAppartAmbiente, String tipologia,
            String flArchivioRestituito, String flCessato, BigDecimal idEnteConvenzEc, Date dtIniValAppartEnteSiam,
            Date dtFineValAppartEnteSiam, BigDecimal idEnteFornitEstern, String tiDichVers, BigDecimal idOrganizIam,
            PigParamApplicTableBean parametriAmministrazione, PigParamApplicTableBean parametriConservazione,
            PigParamApplicTableBean parametriGestione) {
        AmministrazioneValidator valid = new AmministrazioneValidator(getMessageBox());
        valid.validaDatiVersatoreBase(status, tipologia, idAmbienteVers, nmVers, dsVers, dtIniValVers, dtFineValVers,
                dtIniValAppartAmbiente, dtFineValAppartAmbiente, dsPathInputFtp, dsPathOutputFtp, dsPathTrasf);

        // MEV 27921
        if (!getMessageBox().hasError()) {
            Pattern regxp = Pattern.compile("^[a-zA-Z0-9_-]+$");
            Matcher matcher = regxp.matcher(nmVers);
            if (!matcher.matches()) {
                getMessageBox().addError("Si possono inserire soltanto lettere, numeri, trattini e trattini bassi.");
            }
        }

        // Controllo valori possibili su ente
        if (parametriAmministrazione != null) {
            for (PigParamApplicRowBean paramApplicRowBean : parametriAmministrazione) {
                if (paramApplicRowBean.getString("ds_lista_valori_ammessi") != null
                        && !paramApplicRowBean.getString("ds_lista_valori_ammessi").equals("")) {
                    if (paramApplicRowBean.getString("ds_valore_param_applic_vers_amm") != null
                            && !paramApplicRowBean.getString("ds_valore_param_applic_vers_amm").equals("")) {
                        if (!amministrazioneEjb.inValoriPossibili(
                                paramApplicRowBean.getString("ds_valore_param_applic_vers_amm"),
                                paramApplicRowBean.getString("ds_lista_valori_ammessi"))) {
                            getMessageBox().addError(
                                    "Il valore del parametro non è compreso tra i valori ammessi sul parametro</br>");
                        }
                    }
                }
            }
        }

        if (parametriConservazione != null) {
            for (PigParamApplicRowBean paramApplicRowBean : parametriConservazione) {
                if (paramApplicRowBean.getString("ds_lista_valori_ammessi") != null
                        && !paramApplicRowBean.getString("ds_lista_valori_ammessi").equals("")) {
                    if (paramApplicRowBean.getString("ds_valore_param_applic_vers_cons") != null
                            && !paramApplicRowBean.getString("ds_valore_param_applic_vers_cons").equals("")) {
                        if (!amministrazioneEjb.inValoriPossibili(
                                paramApplicRowBean.getString("ds_valore_param_applic_vers_cons"),
                                paramApplicRowBean.getString("ds_lista_valori_ammessi"))) {
                            getMessageBox().addError(
                                    "Il valore del parametro non è compreso tra i valori ammessi sul parametro</br>");
                        }
                    }
                }
            }
        }

        if (parametriGestione != null) {
            for (PigParamApplicRowBean paramApplicRowBean : parametriGestione) {
                if (paramApplicRowBean.getString("ds_lista_valori_ammessi") != null
                        && !paramApplicRowBean.getString("ds_lista_valori_ammessi").equals("")) {
                    if (paramApplicRowBean.getString("ds_valore_param_applic_vers_gest") != null
                            && !paramApplicRowBean.getString("ds_valore_param_applic_vers_gest").equals("")) {
                        if (!amministrazioneEjb.inValoriPossibili(
                                paramApplicRowBean.getString("ds_valore_param_applic_vers_gest"),
                                paramApplicRowBean.getString("ds_lista_valori_ammessi"))) {
                            getMessageBox().addError(
                                    "Il valore del parametro non è compreso tra i valori ammessi sul parametro</br>");
                        }
                    }
                }
            }
        }

        String errore = null;
        if (!getMessageBox().hasError()) {
            // Controlli ordine validità date versatore
            if ((errore = DateUtil.ucContrDate("del versatore", dtIniValVers, dtFineValVers)) != null) {
                getMessageBox().addError(errore);
            }
        }

        if (!getMessageBox().hasError()) {
            // Controlli ordine validità date appartenenza versatore all'ambiente
            if ((errore = DateUtil.ucContrDate("dell'appartenenza del versatore", dtIniValAppartAmbiente,
                    dtFineValAppartAmbiente)) != null) {
                getMessageBox().addError(errore);
            }
        }

        if (!getMessageBox().hasError()) {
            if (getForm().getVersList().getStatus().equals(Status.insert)) {
                if (idEnteConvenzEc != null || idEnteFornitEstern != null) {
                    // Controlli ordine validità date ente
                    if ((errore = DateUtil.ucContrDate("dell'ente", dtIniValAppartEnteSiam,
                            dtFineValAppartEnteSiam)) != null) {
                        getMessageBox().addError(errore);
                    }
                }
            }
        }

        if (!getMessageBox().hasError()) {
            Date[] dateValAmb = amministrazioneEjb.getDateValiditaAmbiente(idAmbienteVers);
            if ((errore = DateUtil.ucContrInclusione("di appartenenza del versatore all'ambiente", "dell'ambiente",
                    dtIniValAppartAmbiente, dtFineValAppartAmbiente, dateValAmb[0], dateValAmb[1])) != null) {
                getMessageBox().addError(errore);
            }
        }

        // Controlli conformità flag
        if (flArchivioRestituito.equals("0") && flCessato.equals("1")) {
            getMessageBox().addError(
                    "Attenzione: l'archivio risulta essere cessato, non è possibile modificare il flag di archivio restituito");
        }

        if (status.equals(Status.insert)) {
            // Controlli inserimento ente convenzionato PRODUTTORE
            if (tipologia.equals("PRODUTTORE")) {
                if (tiDichVers == null) {
                    getMessageBox().addError("Errore di compilazione form: tipo dichiarazione non inserita<br/>");
                }
                if (idOrganizIam == null) {
                    getMessageBox().addError("Errore di compilazione form: organizzazione non inserita<br/>");
                }

                // Controllo di aver selezionato l'ente
                if (idEnteConvenzEc == null) {
                    getMessageBox().addError("Attenzione: non è stato selezionato l'ente convenzionato</br>");
                }

                if (!getMessageBox().hasError()) {
                    // Controlli sugli accordi
                    if (amministrazioneEjb.checkAccordoEnteGestore(idAmbienteVers, idEnteConvenzEc)) {
                        getMessageBox().addError(
                                "Sull’accordo dell’ente convenzionato è definito un ente gestore diverso da quello definito sull’ambiente di appartenenza del versatore</br>");
                    }

                    // Controllo sulle date di validità
                    if (amministrazioneEjb.notExistPeriodoValiditaAssociazioneEnteConvenzVersAccordoValido(
                            idEnteConvenzEc, dtIniValAppartEnteSiam, dtFineValAppartEnteSiam)) {
                        getMessageBox().addError(
                                "L’intervallo di validità dell’accordo corrente definito sull’ente convenzionato non è incluso nelle date di inizio – fine validità indicate</br>");
                    }
                }

            } else {
                if (idEnteFornitEstern == null) {
                    getMessageBox().addError("Attenzione: non è stato selezionato l'ente fornitore esterno</br>");
                }
            }
        } else {
            if (tipologia.equals("PRODUTTORE")) {
                if (tiDichVers == null) {
                    getMessageBox().addError("Errore di compilazione form: tipo dichiarazione non inserita<br/>");
                }
                if (idOrganizIam == null) {
                    getMessageBox().addError("Errore di compilazione form: organizzazione non inserita<br/>");
                }
            }
        }
        return getMessageBox().isEmpty();
    }

    private void salvaTipoObj() throws EMFError, IncoherenceException {

        getForm().getParametriAmministrazioneTipoOggettoList().post(getRequest());
        getForm().getParametriConservazioneTipoOggettoList().post(getRequest());
        getForm().getParametriGestioneTipoOggettoList().post(getRequest());

        getMessageBox().clear();

        TipoObject tipoObj = getForm().getTipoObject();
        tipoObj.post(getRequest());
        PigTipoObjectRowBean tipoObjRowBean = new PigTipoObjectRowBean();
        tipoObj.copyToBean(tipoObjRowBean);

        final String tiVersFile = tipoObj.getTi_vers_file().parse();

        if (tipoObj.getNm_tipo_object().parse() == null) {
            getMessageBox().addError("Errore di compilazione form: nome tipo non inserito<br/>");
        }
        if (tipoObj.getDs_tipo_object().parse() == null) {
            getMessageBox().addError("Errore di compilazione form: descrizione tipo non inserito<br/>");
        }

        if (tiVersFile == null) {
            getMessageBox().addError("Errore di compilazione form: tipo SIP non inserito<br/>");
        } else if (tiVersFile.equals(it.eng.sacerasi.common.Constants.TipoVersamento.NO_ZIP.name())) {
            if (tipoObj.getNm_tipo_unita_doc_sacer().parse() == null
                    || tipoObj.getNm_tipo_unita_doc_sacer().parse().isEmpty()) {
                getMessageBox().addError(
                        "Errore di compilazione form: Nome Tipologia Unit\u00E0 Documentaria in Sacer non inserito<br/>");

            }
            if (tipoObj.getFl_forza_accettazione_sacer().parse() == null
                    || tipoObj.getFl_forza_accettazione_sacer().parse().isEmpty()) {
                getMessageBox().addError("Errore di compilazione form: Forza Accettazione non inserito<br/>");

            }
            if (tipoObj.getFl_forza_conservazione().parse() == null
                    || tipoObj.getFl_forza_conservazione().parse().isEmpty()) {
                getMessageBox().addError("Errore di compilazione form: Forza Conservazione non inserito<br/>");

            }
            if (tipoObj.getFl_forza_collegamento().parse() == null
                    || tipoObj.getFl_forza_collegamento().parse().isEmpty()) {
                getMessageBox().addError("Errore di compilazione form: Forza Collegamento non inserito<br/>");

            }
            if (tipoObj.getTi_conservazione().parse() == null || tipoObj.getTi_conservazione().parse().isEmpty()) {
                getMessageBox().addError("Errore di compilazione form: Tipo Conservazione non inserito<br/>");

            }
        } else if (tiVersFile.equals(it.eng.sacerasi.common.Constants.TipoVersamento.DA_TRASFORMARE.name())) {
            if (tipoObj.getId_trasf().parse() == null) {
                getMessageBox().addError("Errore di compilazione form: Trasformazione non inserita<br/>");
            }

            if (tipoObj.getId_trasf().parse() == null) {
                getMessageBox().addError("Errore di compilazione form: Priorità trasformazione non inserita<br/>");
            }
        }
        // MEV#27321 - Introduzione della priorità di versamento di un oggetto ZIP_CON_XML_SACER e NO_ZIP
        if ((tipoObj.getTi_priorita_versamento().parse() == null
                || tipoObj.getTi_priorita_versamento().parse().equals("")) && tiVersFile != null
                && (tiVersFile.equals(it.eng.sacerasi.common.Constants.TipoVersamento.NO_ZIP.name()) || tiVersFile
                        .equals(it.eng.sacerasi.common.Constants.TipoVersamento.ZIP_CON_XML_SACER.name()))) {
            getMessageBox().addError("Non è possibile salvare senza aver impostato la priorità di versamento<br/>");
        }

        if (!getMessageBox().hasError()) {
            if (tiVersFile.equals(it.eng.sacerasi.common.Constants.TipoVersamento.DA_TRASFORMARE.name())) {
                if (tipoObj.getTi_priorita().getValue() != null && !tipoObj.getTi_priorita().getValue().isEmpty()) {
                    tipoObjRowBean.setTiPriorita(
                            (Constants.ComboFlagPrioTrasfType.valueOf(tipoObj.getTi_priorita().getValue()).getValue()));
                } else {
                    getMessageBox().addError(
                            "Per i tipi oggetto \"DA TRASFORMARE\" è obbligatorio selezionare una priorità<br/>");
                }
            } else {
                tipoObjRowBean.setTiPriorita("");
            }
            // MEV#27321 - Introduzione della priorità di versamento di un oggetto ZIP_CON_XML_SACER e NO_ZIP
            if (tipoObj.getTi_priorita_versamento().getValue() != null
                    && !tipoObj.getTi_priorita_versamento().getValue().isEmpty()) {
                tipoObjRowBean.setTiPrioritaVersamento((Constants.ComboFlagPrioVersType
                        .valueOf(tipoObj.getTi_priorita_versamento().getValue()).getValue()));
            }
        }

        if (!getMessageBox().hasError()
                && !tiVersFile.equals(it.eng.sacerasi.common.Constants.TipoVersamento.DA_TRASFORMARE.name())) {
            if (tipoObj.getTi_calc_key_unita_doc().parse() == null
                    || tipoObj.getTi_calc_key_unita_doc().parse().isEmpty()) {
                getMessageBox().addError(
                        "Errore di compilazione form: Tipo Calcolo Unit\u00E0 Documentaria non inserito<br/>");

            }
        }

        if (tipoObj.getFl_contr_hash().parse() == null || tipoObj.getFl_contr_hash().parse().isEmpty()) {
            getMessageBox().addError("Errore di compilazione form: Controllo Hash non inserito<br/>");

        }

        PigParamApplicTableBean parametriAmministrazione = (PigParamApplicTableBean) getForm()
                .getParametriAmministrazioneTipoOggettoList().getTable();
        PigParamApplicTableBean parametriConservazione = (PigParamApplicTableBean) getForm()
                .getParametriConservazioneTipoOggettoList().getTable();
        PigParamApplicTableBean parametriGestione = (PigParamApplicTableBean) getForm()
                .getParametriGestioneTipoOggettoList().getTable();

        // Controllo valori possibili su ente
        for (PigParamApplicRowBean paramApplicRowBean : parametriAmministrazione) {
            if (paramApplicRowBean.getString("ds_lista_valori_ammessi") != null
                    && !paramApplicRowBean.getString("ds_lista_valori_ammessi").equals("")) {
                if (paramApplicRowBean.getString("ds_valore_param_applic_tipo_oggetto_amm") != null
                        && !paramApplicRowBean.getString("ds_valore_param_applic_tipo_oggetto_amm").equals("")) {
                    if (!amministrazioneEjb.inValoriPossibili(
                            paramApplicRowBean.getString("ds_valore_param_applic_tipo_oggetto_amm"),
                            paramApplicRowBean.getString("ds_lista_valori_ammessi"))) {
                        getMessageBox()
                                .addError("Il valore del parametro non è compreso tra i valori ammessi sul parametro");
                    }
                }
            }
        }

        for (PigParamApplicRowBean paramApplicRowBean : parametriConservazione) {
            if (paramApplicRowBean.getString("ds_lista_valori_ammessi") != null
                    && !paramApplicRowBean.getString("ds_lista_valori_ammessi").equals("")) {
                if (paramApplicRowBean.getString("ds_valore_param_applic_tipo_oggetto_cons") != null
                        && !paramApplicRowBean.getString("ds_valore_param_applic_tipo_oggetto_cons").equals("")) {
                    if (!amministrazioneEjb.inValoriPossibili(
                            paramApplicRowBean.getString("ds_valore_param_applic_tipo_oggetto_cons"),
                            paramApplicRowBean.getString("ds_lista_valori_ammessi"))) {
                        getMessageBox()
                                .addError("Il valore del parametro non è compreso tra i valori ammessi sul parametro");
                    }
                }
            }
        }

        for (PigParamApplicRowBean paramApplicRowBean : parametriGestione) {
            if (paramApplicRowBean.getString("ds_lista_valori_ammessi") != null
                    && !paramApplicRowBean.getString("ds_lista_valori_ammessi").equals("")) {
                if (paramApplicRowBean.getString("ds_valore_param_applic_tipo_oggetto_gest") != null
                        && !paramApplicRowBean.getString("ds_valore_param_applic_tipo_oggetto_gest").equals("")) {
                    if (!amministrazioneEjb.inValoriPossibili(
                            paramApplicRowBean.getString("ds_valore_param_applic_tipo_oggetto_gest"),
                            paramApplicRowBean.getString("ds_lista_valori_ammessi"))) {
                        getMessageBox()
                                .addError("Il valore del parametro non è compreso tra i valori ammessi sul parametro");
                    }
                }
            }
        }

        BigDecimal idVers = ((BaseRowInterface) getForm().getVersList().getTable().getCurrentRow())
                .getBigDecimal("id_vers");
        tipoObjRowBean.setIdVers(idVers);

        String tiDichVers = getForm().getTipoObject().getTi_dich_vers().parse();
        BigDecimal idOrganizIam = getForm().getTipoObject().getId_organiz_iam().parse();

        try {
            if (tiVersFile != null) {
                String control = doFormControl(tipoObjRowBean);
                if (control != null) {
                    getMessageBox().addError(control);
                }
            }

            if (!getMessageBox().hasError()) {

                if (tiVersFile.equals(it.eng.sacerasi.common.Constants.TipoVersamento.ZIP_CON_XML_SACER.name())
                        || tiVersFile.equals(it.eng.sacerasi.common.Constants.TipoVersamento.DA_TRASFORMARE.name())) {
                    tipoObjRowBean.setFlForzaAccettazioneSacer(null);
                    tipoObjRowBean.setFlForzaCollegamento(null);
                    tipoObjRowBean.setFlForzaConservazione(null);
                }
                /*
                 * Codice aggiuntivo per il logging...
                 */
                LogParam param = SpagoliteLogUtil.getLogParam(
                        paramApplicHelper.getApplicationName().getDsValoreParamApplic(), getUser().getUsername(),
                        SpagoliteLogUtil.getPageName(this));
                param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
                if (getForm().getTipoObjectList().getStatus().equals(Status.update)) {
                    param.setNomeAzione(SpagoliteLogUtil.getToolbarUpdate());
                    BigDecimal idTipoObject = null;
                    idTipoObject = ((PigTipoObjectRowBean) getForm().getTipoObjectList().getTable().getCurrentRow())
                            .getIdTipoObject();
                    amministrazioneEjb.updatePigTipoObj(param, getUser().getIdUtente(), idTipoObject, tipoObjRowBean,
                            (PigTipoFileObjectTableBean) getForm().getTipoFileObjectList().getTable(),
                            parametriAmministrazione, parametriConservazione, parametriGestione, tiDichVers,
                            idOrganizIam);
                    getMessageBox().addInfo("Update tipo oggetto effettuato con successo");

                    // MEV25814
                    if (getForm().getTipoObject().getFl_crea_tipofile().getValue().equals(WebConstants.DB_TRUE)) {
                        if (!amministrazioneHelper.existsPigTipoFileObjectByTipoObjectAndName(idTipoObject,
                                tipoObjRowBean.getNmTipoObject())) {
                            // crea un tipo file object di default
                            PigTipoFileObjectRowBean tipoFileObjRowBean = prepareAutomaticFileObject(tipoObjRowBean);

                            param.setNomeAzione(SpagoliteLogUtil.getToolbarInsert());
                            amministrazioneEjb.insertPigTipoFileObj(param, tipoFileObjRowBean);
                            getMessageBox().addInfo("Nuovo tipo file oggetto salvato con successo");

                            getForm().getTipoFileObject().copyFromBean(tipoFileObjRowBean);
                            populateComboTipoFileObj();
                            PigTipoFileObjectTableBean table = new PigTipoFileObjectTableBean();
                            table.add(tipoFileObjRowBean);
                            table.setPageSize(1);
                            table.setCurrentRowIndex(0);
                            getForm().getTipoFileObjectList().setTable(table);
                        } else {
                            getMessageBox().addInfo("Tipo file oggetto di default non creato perché già esistente");
                        }
                    }

                    // Aggiorno il dettaglio che varia a seconda dei dati appena modificati
                    aggiornaDettaglioTipoOggetto();

                } else if (getForm().getTipoObjectList().getStatus().equals(Status.insert)) {
                    param.setNomeAzione(SpagoliteLogUtil.getToolbarInsert());
                    amministrazioneEjb.insertPigTipoObj(param, getUser().getIdUtente(), tipoObjRowBean,
                            parametriAmministrazione, parametriConservazione, parametriGestione, tiDichVers,
                            idOrganizIam);
                    getMessageBox().addInfo("Nuovo tipo oggetto salvato con successo");

                    // MEV25814
                    if (getForm().getTipoObject().getFl_crea_tipofile().getValue().equals(WebConstants.DB_TRUE)) {
                        // crea un tipo file object di default
                        PigTipoFileObjectRowBean tipoFileObjRowBean = prepareAutomaticFileObject(tipoObjRowBean);

                        param.setNomeAzione(SpagoliteLogUtil.getToolbarInsert());
                        amministrazioneEjb.insertPigTipoFileObj(param, tipoFileObjRowBean);
                        getMessageBox().addInfo("Nuovo tipo file oggetto salvato con successo");

                        getForm().getTipoFileObject().copyFromBean(tipoFileObjRowBean);
                        populateComboTipoFileObj();
                        PigTipoFileObjectTableBean table = new PigTipoFileObjectTableBean();
                        table.add(tipoFileObjRowBean);
                        table.setPageSize(1);
                        table.setCurrentRowIndex(0);
                        getForm().getTipoFileObjectList().setTable(table);
                    }

                    PigTipoObjectTableBean table = new PigTipoObjectTableBean();
                    table.add(tipoObjRowBean);
                    table.setPageSize(1);
                    table.setCurrentRowIndex(0);
                    getForm().getTipoObjectList().setTable(table);
                    getForm().getTipoObject().getId_tipo_object()
                            .setValue(tipoObjRowBean.getIdTipoObject().toPlainString());
                    // Aggiorno il dettaglio che varia a seconda dei dati appena inseriti
                    aggiornaDettaglioTipoOggetto();
                }

                getMessageBox().setViewMode(MessageBox.ViewMode.plain);

                getForm().getTipoObject().setViewMode();
                getForm().getTipoObject().setStatus(Status.view);
                getForm().getTipoObjectList().setStatus(Status.view);

                // MEV25814
                getForm().getTipoObject().getFl_crea_tipofile().setHidden(true);

            }
        } catch (IncoherenceException e) {
            getMessageBox().addError(e.getMessage());
        } catch (ParerUserError e) {
            getMessageBox().addError(e.getDescription());
        }

        if (!getLastPublisher().equals(Application.Publisher.TIPO_OBJECT_DETAIL)) {
            goBack();
        } else {
            forwardToPublisher(Application.Publisher.TIPO_OBJECT_DETAIL);
        }
    }

    // MEV25814
    private PigTipoFileObjectRowBean prepareAutomaticFileObject(PigTipoObjectRowBean tipoObjRowBean) {
        PigTipoFileObjectRowBean tipoFileObjRowBean = new PigTipoFileObjectRowBean();
        tipoFileObjRowBean.setIdTipoObject(tipoObjRowBean.getIdTipoObject());
        tipoFileObjRowBean.setNmTipoFileObject(tipoObjRowBean.getNmTipoObject());
        tipoFileObjRowBean.setDsTipoFileObject(tipoObjRowBean.getNmTipoObject());
        tipoFileObjRowBean.setFlVersSacerAsinc(WebConstants.DB_FALSE);

        String tiVersFile = tipoObjRowBean.getTiVersFile();
        String flContrHash = tipoObjRowBean.getFlContrHash();

        if (tiVersFile.equals(it.eng.sacerasi.common.Constants.TipoVersamento.ZIP_CON_XML_SACER.name())
                || tiVersFile.equals(it.eng.sacerasi.common.Constants.TipoVersamento.ZIP_NO_XML_SACER.name())
                || tiVersFile.equals(it.eng.sacerasi.common.Constants.TipoVersamento.DA_TRASFORMARE.name())) {
            if (flContrHash.equals(WebConstants.DB_TRUE)) {
                tipoFileObjRowBean.setTiCalcHashSacer(it.eng.sacerasi.common.Constants.HashCalcType.NOTIFICATO.name());
            } else {
                tipoFileObjRowBean.setTiCalcHashSacer(null);
            }
        }

        return tipoFileObjRowBean;
    }

    private void salvaDettaglioStatoVersamento() throws EMFError {
        getForm().getStatoVersamentoObjectDetail().post(getRequest());
        if (getForm().getStatoVersamentoObjectDetail().validate(getMessageBox())) {
            String tiStatoObject = getForm().getStatoVersamentoObjectDetail().getTi_stato_object().parse();
            String dsTiStatoObject = getForm().getStatoVersamentoObjectDetail().getDs_ti_stato_object().parse();

            if (dsTiStatoObject == null) {
                getMessageBox().addError("La descrizione dello stato " + tiStatoObject + " non \u00E8 stata compilata");
            }

            if (!getMessageBox().hasError()) {
                if (getForm().getStatiVersamentoObjectList().getStatus().equals(Status.update)) {
                    amministrazioneEjb.updateStatoVersamentoOggetto(tiStatoObject, dsTiStatoObject);
                    getForm().getStatiVersamentoObjectList().getTable().getCurrentRow().setString("ds_ti_stato_object",
                            dsTiStatoObject);
                    getMessageBox().addInfo("Update descrizione dettaglio stato versamento effettuato con successo");
                }
                getMessageBox().setViewMode(MessageBox.ViewMode.plain);
                goBack();
            } else {
                forwardToPublisher(Application.Publisher.DETTAGLIO_STATO_VERSAMENTO_OGGETTO);
            }
        }
    }

    private void salvaTipoFileObj() throws EMFError, IncoherenceException {

        getMessageBox().clear();
        getMessageBox().setViewMode(MessageBox.ViewMode.alert);

        TipoFileObject tipoFileObj = getForm().getTipoFileObject();
        tipoFileObj.post(getRequest());

        PigTipoFileObjectRowBean tipoFileObjRowBean = new PigTipoFileObjectRowBean();
        tipoFileObj.copyToBean(tipoFileObjRowBean);

        if (tipoFileObj.getNm_tipo_file_object().parse() == null) {
            getMessageBox().addError("Errore di compilazione form: nome tipo non inserito<br/>");
        }
        if (tipoFileObj.getDs_tipo_file_object().parse() == null) {
            getMessageBox().addError("Errore di compilazione form: descrizione tipo non inserito<br/>");
        }

        if (tipoFileObj.getTi_calc_hash_sacer().parse() != null
                && tipoFileObj.getFl_calc_hash_sacer().parse() == null) {
            getMessageBox().addError(
                    "Errore di compilazione form: inserito il Tipo Calcolo Hash senza aver selezionato il valore in Calcolo Hash in Sacer<br/>");
        }
        /*
         * 
         * nm_tipo_doc_sacer OBBLIGATORIO NO_ZIP
         * 
         * ti_doc_sacer obbligatorio no_zip
         * 
         * nm_tipo_strut_doc_sacer obbligatorio no_zip
         * 
         * nm_tipo_comp_doc_sacer obbligatorio no_zip
         */
        String tiVers = getForm().getTipoObject().getTi_vers_file().parse();
        String flContrHash = getForm().getTipoObject().getFl_contr_hash().parse();

        if (tiVers.equals(it.eng.sacerasi.common.Constants.TipoVersamento.NO_ZIP.name())) {

            if (tipoFileObj.getNm_tipo_doc_sacer().parse() == null) {
                getMessageBox().addError("Errore di compilazione form: Nome Tipo Documento non inserito<br/>");

            }
            if (tipoFileObj.getTi_doc_sacer().parse() == null) {
                getMessageBox().addError("Errore di compilazione form: Tipo Documento non inserito<br/>");

            }
            if (tipoFileObj.getNm_tipo_strut_doc_sacer().parse() == null) {
                getMessageBox().addError("Errore di compilazione form: Tipo Struttura Documento non inserito<br/>");

            }
            if (tipoFileObj.getNm_tipo_comp_doc_sacer().parse() == null) {
                getMessageBox().addError("Errore di compilazione form: Tipo Componente non inserito<br/>");

            }

        }
        BigDecimal idTipoObj = ((PigTipoObjectRowBean) getForm().getTipoObjectList().getTable().getCurrentRow())
                .getIdTipoObject();
        tipoFileObjRowBean.setIdTipoObject(idTipoObj);

        // Setto il valore di tiCalcHashSacer
        if (tiVers.equals(it.eng.sacerasi.common.Constants.TipoVersamento.ZIP_CON_XML_SACER.name())
                || tiVers.equals(it.eng.sacerasi.common.Constants.TipoVersamento.ZIP_NO_XML_SACER.name())
                || tiVers.equals(it.eng.sacerasi.common.Constants.TipoVersamento.DA_TRASFORMARE.name())) {
            if (flContrHash.equals(WebConstants.DB_TRUE)) {
                tipoFileObjRowBean.setTiCalcHashSacer(it.eng.sacerasi.common.Constants.HashCalcType.NOTIFICATO.name());
            } else {
                tipoFileObjRowBean.setTiCalcHashSacer(null);
            }
        } else if (tiVers.equals(it.eng.sacerasi.common.Constants.TipoVersamento.NO_ZIP.name())) {
            if ((flContrHash.equals(WebConstants.DB_TRUE) || (tipoFileObjRowBean.getFlCalcHashSacer() != null
                    && tipoFileObjRowBean.getFlCalcHashSacer().equals(WebConstants.DB_FALSE)))
                    && (tipoFileObjRowBean.getTiCalcHashSacer() == null)) {
                getMessageBox().addError("Errore di compilazione form: Tipo Calcolo Hash non selezionato<br/>");
            }
        }

        try {
            String siNoValue = tipoFileObj.getFl_vers_sacer_asinc().parse() != null
                    ? (tipoFileObj.getFl_vers_sacer_asinc().parse().equalsIgnoreCase("SI") ? "1" : "0") : null;
            tipoFileObjRowBean.setFlVersSacerAsinc(siNoValue);

            if (tiVers.equals(it.eng.sacerasi.common.Constants.TipoVersamento.ZIP_CON_XML_SACER.name())
                    || tiVers.equals(it.eng.sacerasi.common.Constants.TipoVersamento.DA_TRASFORMARE.name())) {
                tipoFileObj.getFl_calc_hash_sacer().setValue(null);
                tipoFileObj.getFl_ver_firma_fmt_sacer().setValue(null);
            }

            if (getMessageBox().isEmpty()) {
                BigDecimal idTipoFileObject = null;

                /*
                 * Codice aggiuntivo per il logging...
                 */
                LogParam param = SpagoliteLogUtil.getLogParam(
                        paramApplicHelper.getApplicationName().getDsValoreParamApplic(), getUser().getUsername(),
                        SpagoliteLogUtil.getPageName(this));
                param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
                if (getForm().getTipoFileObjectList().getStatus().equals(Status.update)) {
                    param.setNomeAzione(SpagoliteLogUtil.getToolbarUpdate());
                    idTipoFileObject = ((PigTipoFileObjectRowBean) getForm().getTipoFileObjectList().getTable()
                            .getCurrentRow()).getIdTipoFileObject();
                    amministrazioneEjb.updatePigTipoFileObj(param, idTipoFileObject, tipoFileObjRowBean);
                    getMessageBox().addInfo("Update tipo file oggetto effettuato con successo");

                } else if (getForm().getTipoFileObjectList().getStatus().equals(Status.insert)) {
                    param.setNomeAzione(SpagoliteLogUtil.getToolbarInsert());
                    amministrazioneEjb.insertPigTipoFileObj(param, tipoFileObjRowBean);
                    getMessageBox().addInfo("Nuovo tipo file oggetto salvato con successo");

                    getForm().getTipoFileObject().copyFromBean(tipoFileObjRowBean);
                    populateComboTipoFileObj();
                    PigTipoFileObjectTableBean table = new PigTipoFileObjectTableBean();
                    table.add(tipoFileObjRowBean);
                    table.setPageSize(1);
                    table.setCurrentRowIndex(0);
                    getForm().getTipoFileObjectList().setTable(table);
                    getForm().getTipoFileObject().setStatus(Status.view);
                    getForm().getTipoFileObjectList().setStatus(Status.view);
                    getForm().getTipoFileObject().getTi_vers_file()
                            .setValue(((PigTipoObjectRowBean) getForm().getTipoObjectList().getTable().getCurrentRow())
                                    .getTiVersFile());
                    getForm().getTipoFileObject().getTi_vers_file().setHidden(true);

                    idTipoFileObject = tipoFileObjRowBean.getIdTipoFileObject();
                    PigXsdDatiSpecTableBean xsdDatiSpecTableBean = amministrazioneEjb.getPigXsdDatiSpecTableBean(null,
                            idTipoFileObject);

                    getForm().getXsdDatiSpecList().setTable(xsdDatiSpecTableBean);
                    getForm().getXsdDatiSpecList().getTable().first();
                    getForm().getXsdDatiSpecList().setStatus(Status.view);
                    getForm().getXsdDatiSpecList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);

                }
                // Aggiorno l'idTipoObject e idTipoFileObject in IdList
                getForm().getIdList().getId_tipo_object().clear();
                getForm().getIdList().getId_tipo_file_object().setValue(idTipoFileObject.toString());

                getMessageBox().setViewMode(MessageBox.ViewMode.plain);

                getForm().getTipoFileObject().setViewMode();
                getForm().getTipoFileObject().setStatus(Status.view);
                getForm().getTipoFileObjectList().setStatus(Status.view);

            }
            forwardToPublisher(Application.Publisher.TIPO_FILE_OBJECT_DETAIL);
        } catch (IncoherenceException e) {
            getMessageBox().addError(e.getMessage());
            forwardToPublisher(Application.Publisher.TIPO_FILE_OBJECT_DETAIL);
        }
    }

    private void salvaSopClass() throws EMFError {

        getMessageBox().clear();

        SopClass sopClass = getForm().getSopClass();
        sopClass.post(getRequest());

        PigSopClassDicomRowBean sopClassRowBean = new PigSopClassDicomRowBean();
        sopClass.copyToBean(sopClassRowBean);

        if (sopClass.getCd_sop_class_dicom().parse() == null) {
            getMessageBox().addError("Errore di compilazione form: codice Sop Class non inserito<br/>");
        }
        if (sopClass.getDs_sop_class_dicom().parse() == null) {
            getMessageBox().addError("Errore di compilazione form: descrizione Sop Class tipo non inserita<br/>");
        }

        try {

            if (getMessageBox().isEmpty()) {

                if (getForm().getSopClassList().getStatus().equals(Status.update)) {

                    BigDecimal idSopClass = ((PigSopClassDicomRowBean) getForm().getSopClassList().getTable()
                            .getCurrentRow()).getIdSopClassDicom();
                    sopClassRowBean.setIdSopClassDicom(idSopClass);
                    amministrazioneEjb.updatePigSopClassDicom(idSopClass, sopClassRowBean);
                    getMessageBox().addInfo("Update tipo file oggetto effettuato con successo");

                } else if (getForm().getSopClassList().getStatus().equals(Status.insert)) {
                    amministrazioneEjb.insertPigSopClassDicom(sopClassRowBean);
                    getMessageBox().addInfo("Nuova Sop Class salvato con successo");
                }

                goBack();
                getMessageBox().setViewMode(MessageBox.ViewMode.plain);

                getForm().getSopClass().setViewMode();
                getForm().getSopClass().setStatus(Status.view);
                getForm().getSopClassList().setStatus(Status.view);

            } else {
                forwardToPublisher(Application.Publisher.SOP_CLASS_DICOM_DETAIL);
            }
        } catch (IncoherenceException e) {
            getMessageBox().addError(e.getMessage());
            forwardToPublisher(Application.Publisher.SOP_CLASS_DICOM_DETAIL);
        }
    }

    private void salvaSopClassVers() {
        getMessageBox().clear();

        PigSopClassDicomTableBean sopClassDispTable = (PigSopClassDicomTableBean) getForm().getSopClassDispList()
                .getTable();
        PigSopClassDicomTableBean sopClassToVersTable = (PigSopClassDicomTableBean) getForm().getSopClassToVersList()
                .getTable();
        BigDecimal idVers = ((BaseRowInterface) getForm().getVersList().getTable().getCurrentRow())
                .getBigDecimal("id_vers");
        /*
         * Codice aggiuntivo per il logging...
         */
        LogParam param = SpagoliteLogUtil.getLogParam(paramApplicHelper.getApplicationName().getDsValoreParamApplic(),
                getUser().getUsername(), SpagoliteLogUtil.getPageName(this), SpagoliteLogUtil.getToolbarInsert());
        param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
        amministrazioneEjb.removeAndAddSopClassDicomVers(param, sopClassDispTable, sopClassToVersTable, idVers);
        goBack();
    }

    private void salvaXsdDatiSpec(String string, String clob) throws IncoherenceException, EMFError {

        getMessageBox().clear();
        PigXsdDatiSpecRowBean xsdDatiSpecRowBean = new PigXsdDatiSpecRowBean();

        if (string == null) {
            getMessageBox().addError("Codice Versione non inserito");
        }

        try {

            if (getMessageBox().isEmpty()) {

                Calendar data = Calendar.getInstance();
                // Inizializzo la data a oggi
                data.set(Calendar.MILLISECOND, 0);
                data.set(Calendar.SECOND, 0);
                data.set(Calendar.MINUTE, 0);
                data.set(Calendar.HOUR_OF_DAY, 0);

                Date today = data.getTime();

                /*
                 * Codice aggiuntivo per il logging...
                 */
                LogParam param = SpagoliteLogUtil.getLogParam(
                        paramApplicHelper.getApplicationName().getDsValoreParamApplic(), getUser().getUsername(),
                        SpagoliteLogUtil.getPageName(this));
                param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
                if (getForm().getXsdDatiSpec().getStatus().equals(Status.insert)) {
                    param.setNomeAzione(SpagoliteLogUtil.getToolbarInsert());
                    xsdDatiSpecRowBean.setDtVersioneXsd(new Timestamp(today.getTime()));
                    BigDecimal idVers = new BigDecimal(getForm().getIdList().getId_vers().parse().intValue());
                    if (getForm().getIdList().getId_tipo_object().parse() != null) {
                        xsdDatiSpecRowBean.setIdTipoObject(getForm().getIdList().getId_tipo_object().parse());
                        xsdDatiSpecRowBean.setTiEntita("OBJ");
                    } else if (getForm().getIdList().getId_tipo_file_object().parse() != null) {
                        xsdDatiSpecRowBean.setIdTipoFileObject(getForm().getIdList().getId_tipo_file_object().parse());
                        xsdDatiSpecRowBean.setTiEntita("FILE");
                    }
                    xsdDatiSpecRowBean.setCdVersioneXsd(string);
                    xsdDatiSpecRowBean.setBlXsd(clob);
                    xsdDatiSpecRowBean.setIdVers(idVers);
                    amministrazioneEjb.saveXsdDatiSpec(param, xsdDatiSpecRowBean);
                    PigXsdDatiSpecTableBean table = new PigXsdDatiSpecTableBean();
                    table.add(xsdDatiSpecRowBean);
                    table.setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
                    table.setCurrentRowIndex(0);
                    getForm().getXsdDatiSpecList().setTable(table);
                    aggiornaDettaglioXsd();
                }

                if (getForm().getXsdDatiSpec().getStatus().equals(Status.update)) {
                    param.setNomeAzione(SpagoliteLogUtil.getToolbarUpdate());
                    BigDecimal idXsdDatiSpec = ((PigXsdDatiSpecRowBean) getForm().getXsdDatiSpecList().getTable()
                            .getCurrentRow()).getIdXsdSpec();
                    xsdDatiSpecRowBean = amministrazioneEjb.getPigXsdDatiSpecRowBean(idXsdDatiSpec);
                    xsdDatiSpecRowBean.setDtVersioneXsd(new Timestamp(today.getTime()));
                    xsdDatiSpecRowBean.setCdVersioneXsd(string);
                    xsdDatiSpecRowBean.setBlXsd(clob);
                    amministrazioneEjb.updatePigXsdDatiSpec(param, xsdDatiSpecRowBean);
                }
                getMessageBox().setViewMode(MessageBox.ViewMode.plain);

                getForm().getXsdDatiSpec().setViewMode();
                getForm().getXsdDatiSpec().setStatus(Status.view);
                getForm().getXsdDatiSpecList().setStatus(Status.view);

            }
            forwardToPublisher(Application.Publisher.XSD_DATI_SPEC_DETAIL);
        } catch (Exception e) {
            getMessageBox().addError(e.getMessage());
            forwardToPublisher(Application.Publisher.XSD_DATI_SPEC_DETAIL);
        }
    }

    private void salvaAttribDatiSpec() throws EMFError {

        getMessageBox().clear();

        AttribDatiSpec attribDatiSpec = getForm().getAttribDatiSpec();
        attribDatiSpec.post(getRequest());

        PigAttribDatiSpecRowBean newAttribRowBean = new PigAttribDatiSpecRowBean();
        attribDatiSpec.copyToBean(newAttribRowBean);

        if (attribDatiSpec.getNm_attrib_dati_spec().parse() == null) {
            getMessageBox().addError("Errore. Nome non valorizzato<br/>");
        }
        if (attribDatiSpec.getCd_datatype_xsd().parse() == null) {
            getMessageBox().addError("Errore. Tipo dato non valorizzato<br/>");
        }
        if (attribDatiSpec.getNm_col_dati_spec().parse() == null) {
            getMessageBox().addError("Errore. Colonna non valorizzato<br/>");
        }
        if (attribDatiSpec.getTi_datatype_col().parse() == null) {
            getMessageBox().addError("Errore. Tipo non valorizzato<br/>");
        }

        if (getMessageBox().isEmpty()) {
            BigDecimal idXsd = ((PigXsdDatiSpecRowBean) getForm().getXsdDatiSpecList().getTable().getCurrentRow())
                    .getIdXsdSpec();

            Map<String, String[]> map = getRequest().getParameterMap();
            Iterator<Map.Entry<String, String[]>> iterator = map.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String[]> next = iterator.next();
                String key = next.getKey();
                if (next.getKey().contains("Fl_")) {
                    newAttribRowBean.setObject(key, "1");
                }
            }

            PigXsdDatiSpecRowBean xsdDatiSpecRowBean = amministrazioneEjb.getPigXsdDatiSpecRowBean(idXsd);

            if (getForm().getIdList().getId_tipo_object().parse() != null) {
                xsdDatiSpecRowBean.setIdTipoObject(getForm().getIdList().getId_tipo_object().parse());
            } else if (getForm().getIdList().getId_tipo_file_object().parse() != null) {
                xsdDatiSpecRowBean.setIdTipoFileObject(getForm().getIdList().getId_tipo_file_object().parse());
            }
            /*
             * Codice aggiuntivo per il logging...
             */
            LogParam param = SpagoliteLogUtil.getLogParam(
                    paramApplicHelper.getApplicationName().getDsValoreParamApplic(), getUser().getUsername(),
                    SpagoliteLogUtil.getPageName(this), SpagoliteLogUtil.getToolbarUpdate());
            param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
            amministrazioneEjb.updateAttribName(param, xsdDatiSpecRowBean,
                    attribDatiSpec.getNm_attrib_dati_spec().parse(), newAttribRowBean);
            goBack();
            getMessageBox().setViewMode(MessageBox.ViewMode.plain);

            getForm().getAttribDatiSpec().setViewMode();
            getForm().getAttribDatiSpec().setStatus(Status.view);
            getForm().getAttribDatiSpecList().setStatus(Status.view);
        } else {
            forwardToPublisher(Application.Publisher.ATTRIB_DATI_SPEC_DETAIL);
        }
    }

    @Override
    public void deleteAmbienteVersList() throws EMFError {

        getMessageBox().clear();
        String lastPublisher = getLastPublisher();
        PigAmbienteVersRowBean ambienteVersRowBean = (PigAmbienteVersRowBean) getForm().getAmbienteVersList().getTable()
                .getCurrentRow();

        try {
            /*
             * Codice aggiuntivo per il logging...
             */
            LogParam param = SpagoliteLogUtil.getLogParam(
                    paramApplicHelper.getApplicationName().getDsValoreParamApplic(), getUser().getUsername(),
                    SpagoliteLogUtil.getPageName(this));
            if (param.getNomePagina().equalsIgnoreCase(Application.Publisher.AMBIENTE_VERS_DETAIL)) {
                param.setNomeAzione(SpagoliteLogUtil.getToolbarDelete());
            } else {
                param.setNomeAzione(
                        SpagoliteLogUtil.getDetailActionNameDelete(getForm(), getForm().getAmbienteVersList()));
            }
            param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
            amministrazioneEjb.removeAmbienteVers(param, ambienteVersRowBean);
            ambienteVersRowBean = new PigAmbienteVersRowBean();
            PigAmbienteVersTableBean ambienteVersTableBean = amministrazioneEjb
                    .getPigAmbienteVersAbilitatiTableBean(ambienteVersRowBean, getUser().getIdUtente(), true);
            ambienteVersTableBean.addSortingRule(PigAmbienteVersTableDescriptor.COL_NM_AMBIENTE_VERS, SortingRule.ASC);
            ambienteVersTableBean.sort();
            getForm().getAmbienteVersList().setTable(ambienteVersTableBean);
            getForm().getAmbienteVersList().getTable().first();
            getForm().getAmbienteVersList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
            getMessageBox().addInfo("Ambiente eliminato con successo");
            forwardToPublisher(Application.Publisher.CERCA_AMBIENTE_VERS);
        } catch (IncoherenceException ex) {
            getMessageBox().addError(ex.getMessage());
            forwardToPublisher(lastPublisher);
        }
    }

    @Override
    public void deleteVersList() throws EMFError {
        BigDecimal idVers = ((BaseRowInterface) (getForm().getVersList().getTable().getCurrentRow()))
                .getBigDecimal("id_vers");
        PigTipoObjectRowBean tipoObjectRowBean = new PigTipoObjectRowBean();
        tipoObjectRowBean.setIdVers(idVers);
        try {
            /*
             * Codice aggiuntivo per il logging...
             */
            LogParam param = SpagoliteLogUtil.getLogParam(
                    paramApplicHelper.getApplicationName().getDsValoreParamApplic(), getUser().getUsername(),
                    SpagoliteLogUtil.getPageName(this));
            if (param.getNomePagina().equals(Application.Publisher.VERS_DETAIL)) {
                param.setNomeAzione(SpagoliteLogUtil.getToolbarDelete());
            } else {
                param.setNomeAzione(
                        SpagoliteLogUtil.getDetailActionNameDelete(this.getForm(), getForm().getVersList()));
            }
            param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
            amministrazioneEjb.removeVers(param, idVers);
            getForm().getVersList().getTable().remove();
            getMessageBox().addInfo("Eliminazione del versatore effettuata con successo");
            if (getLastPublisher().equals(Application.Publisher.VERS_DETAIL)) {
                goBackTo(Application.Publisher.CERCA_VERS);
            } else {
                forwardToPublisher(getLastPublisher());
            }
        } catch (IncoherenceException ex) {
            getMessageBox().addError(ex.getMessage());
            if (!getLastPublisher().equals(Application.Publisher.VERS_DETAIL)) {
                forwardToPublisher(Application.Publisher.CERCA_VERS);
            }
        }
    }

    @Override
    public void deleteTipoObjectList() throws EMFError {

        PigTipoObjectRowBean tipoObjectRowBean = amministrazioneEjb.getPigTipoObjectRowBean(
                ((PigTipoObjectRowBean) (getForm().getTipoObjectList().getTable().getCurrentRow())).getIdTipoObject());

        try {
            /*
             * Codice aggiuntivo per il logging...
             */
            LogParam param = SpagoliteLogUtil.getLogParam(
                    paramApplicHelper.getApplicationName().getDsValoreParamApplic(), getUser().getUsername(),
                    SpagoliteLogUtil.getPageName(this));
            if (param.getNomePagina().equalsIgnoreCase(Application.Publisher.TIPO_OBJECT_DETAIL)) {
                param.setNomeAzione(SpagoliteLogUtil.getToolbarDelete());
            } else {
                param.setNomeAzione(
                        SpagoliteLogUtil.getDetailActionNameDelete(getForm(), getForm().getTipoObjectList()));
            }
            int riga = getForm().getTipoObjectList().getTable().getCurrentRowIndex();
            param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
            amministrazioneEjb.removeTipoObj(param, tipoObjectRowBean);
            getForm().getTipoObjectList().getTable().remove(riga);
            getMessageBox().addInfo("Eliminazione del tipo oggetto effettuata con successo");
        } catch (IncoherenceException ex) {
            getMessageBox().addError(ex.getMessage());
        }
        if (!getMessageBox().hasError() && getLastPublisher().equals(Application.Publisher.TIPO_OBJECT_DETAIL)) {
            goBack();
        } else {
            forwardToPublisher(getLastPublisher());
        }
    }

    @Override
    public void deleteTipoFileObjectList() throws EMFError {
        String lastPublisher = getLastPublisher();
        PigTipoFileObjectRowBean tipoFileObjectRowBean = amministrazioneEjb.getPigTipoFileObjectRowBean(
                ((PigTipoFileObjectRowBean) (getForm().getTipoFileObjectList().getTable().getCurrentRow()))
                        .getIdTipoFileObject());

        try {
            /*
             * Codice aggiuntivo per il logging...
             */
            LogParam param = SpagoliteLogUtil.getLogParam(
                    paramApplicHelper.getApplicationName().getDsValoreParamApplic(), getUser().getUsername(),
                    SpagoliteLogUtil.getPageName(this));
            if (param.getNomePagina().equalsIgnoreCase(Application.Publisher.TIPO_FILE_OBJECT_DETAIL)) {
                param.setNomeAzione(SpagoliteLogUtil.getToolbarDelete());
            } else {
                param.setNomeAzione(
                        SpagoliteLogUtil.getDetailActionNameDelete(getForm(), getForm().getTipoFileObjectList()));
            }
            param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
            amministrazioneEjb.removeTipoFileObj(param, tipoFileObjectRowBean);
        } catch (IncoherenceException ex) {
            getMessageBox().addError(ex.getMessage());
            if (!lastPublisher.equals(Application.Publisher.TIPO_FILE_OBJECT_DETAIL)) {
                goBack();
            } else {
                forwardToPublisher(lastPublisher);
            }
        }

        if (getMessageBox().isEmpty()) {

            PigTipoFileObjectTableBean tipoFileObjectTableBean = amministrazioneEjb.getPigTipoFileObjectTableBean(
                    ((PigTipoObjectRowBean) (getForm().getTipoObjectList().getTable().getCurrentRow()))
                            .getIdTipoObject());

            getForm().getTipoFileObjectList().setTable(tipoFileObjectTableBean);
            getForm().getTipoFileObjectList().getTable().first();
            getForm().getTipoFileObjectList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
            getMessageBox().addInfo("Eliminazione del tipo file oggetto effettuata con successo");
            if (getLastPublisher().equals(Application.Publisher.TIPO_FILE_OBJECT_DETAIL)) {
                goBack();
            }
        }
    }

    @Override
    public void deleteSopClassList() throws EMFError {

        getMessageBox().clear();
        PigSopClassDicomRowBean sopClassDicomRowBean = (PigSopClassDicomRowBean) getForm().getSopClassList().getTable()
                .getCurrentRow();

        try {
            amministrazioneEjb.removeSopClassDicom(sopClassDicomRowBean);
        } catch (IncoherenceException ex) {
            getMessageBox().addError(ex.getMessage());
        }

        if (getLastPublisher().equals(Application.Publisher.SOP_CLASS_DICOM_DETAIL)) {
            goBack();
        } else {
            forwardToPublisher(getLastPublisher());
        }

        if (getMessageBox().isEmpty()) {

            sopClassDicomRowBean = new PigSopClassDicomRowBean();
            PigSopClassDicomTableBean sopClassDicomTableBean = amministrazioneEjb
                    .getPigSopClassDicomTableBean(sopClassDicomRowBean.getCdSopClassDicom(), null);

            getForm().getSopClassList().setTable(sopClassDicomTableBean);
            getForm().getSopClassList().getTable().first();
            getForm().getSopClassList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
        }
    }

    @Override
    public void deleteXsdDatiSpecList() throws EMFError {

        getMessageBox().clear();
        PigXsdDatiSpecRowBean xsdDatiSpecRowBean = (PigXsdDatiSpecRowBean) getForm().getXsdDatiSpecList().getTable()
                .getCurrentRow();
        /*
         * Codice aggiuntivo per il logging...
         */
        LogParam param = SpagoliteLogUtil.getLogParam(paramApplicHelper.getApplicationName().getDsValoreParamApplic(),
                getUser().getUsername(), SpagoliteLogUtil.getPageName(this));
        if (param.getNomePagina().equalsIgnoreCase(Application.Publisher.XSD_DATI_SPEC_DETAIL)) {
            param.setNomeAzione(SpagoliteLogUtil.getToolbarDelete());
        } else {
            param.setNomeAzione(SpagoliteLogUtil.getDetailActionNameDelete(getForm(), getForm().getXsdDatiSpecList()));
        }
        param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
        try {
            if (xsdDatiSpecRowBean.getIdTipoObject() != null) {
                amministrazioneEjb.removeXsdDatiSpec(param, xsdDatiSpecRowBean);
            } else if (xsdDatiSpecRowBean.getIdTipoFileObject() != null) {
                amministrazioneEjb.removeXsdDatiSpec(param, xsdDatiSpecRowBean);
            }
        } catch (IncoherenceException ie) {
            getMessageBox().addError(ie.getMessage());
        }

        if (getLastPublisher().equals(Application.Publisher.XSD_DATI_SPEC_DETAIL)) {
            goBack();
        } else {
            forwardToPublisher(getLastPublisher());
        }

        if (getMessageBox().isEmpty()) {

            PigXsdDatiSpecTableBean xsdDatiSpecTableBean = amministrazioneEjb.getPigXsdDatiSpecTableBean(
                    xsdDatiSpecRowBean.getIdTipoObject(), xsdDatiSpecRowBean.getIdTipoFileObject());

            getForm().getXsdDatiSpecList().setTable(xsdDatiSpecTableBean);
            getForm().getXsdDatiSpecList().getTable().first();
            getForm().getXsdDatiSpecList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
        }
    }

    @Override
    public void deleteVersatoriGenerazioneOggettiList() throws EMFError {
        BigDecimal idVersTipoObjectDaTrasf = ((PigVersTipoObjectDaTrasfRowBean) getForm()
                .getVersatoriGenerazioneOggettiList().getTable().getCurrentRow()).getIdVersTipoObjectDaTrasf();
        int index = getForm().getVersatoriGenerazioneOggettiList().getTable().getCurrentRowIndex();
        try {
            /*
             * Codice aggiuntivo per il logging...
             */
            LogParam param = SpagoliteLogUtil.getLogParam(
                    paramApplicHelper.getApplicationName().getDsValoreParamApplic(), getUser().getUsername(),
                    SpagoliteLogUtil.getPageName(this));
            if (param.getNomePagina().equalsIgnoreCase(Application.Publisher.DETTAGLIO_VERSATORE_GENERAZIONE_OGGETTI)) {
                param.setNomeAzione(SpagoliteLogUtil.getToolbarDelete());
            } else {
                param.setNomeAzione(SpagoliteLogUtil.getDetailActionNameDelete(getForm(),
                        getForm().getVersatoriGenerazioneOggettiList()));
            }
            param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());

            amministrazioneEjb.deletePigVersTipoObjectDaTrasf(param, idVersTipoObjectDaTrasf);
            getForm().getVersatoriGenerazioneOggettiList().getTable().remove(index);
            getMessageBox().addInfo("Versatore per cui si generano oggetti eliminato con successo!");
            if (getLastPublisher().equals(Application.Publisher.DETTAGLIO_VERSATORE_GENERAZIONE_OGGETTI)) {
                goBackTo(Application.Publisher.TIPO_OBJECT_DETAIL);
            } else {
                forwardToPublisher(getLastPublisher());
            }
        } catch (Exception ex) {
            getMessageBox().addError("Errore durante la cancellazione del versatore per cui si generano oggetti");
        }
    }

    @Override
    public void deleteCodaList() throws EMFError {
        getMessageBox().clear();
        BaseRow rigaCoda = (BaseRow) getForm().getCodaList().getTable().getCurrentRow();
        int rowIndex = getForm().getCodaList().getTable().getCurrentRowIndex();
        try {
            String msgId = monitorCoda.deleteMsgFromQueue(rigaCoda.getString("message_id"),
                    rigaCoda.getString("nm_coda"));
            getMessageBox().addMessage(
                    new Message(MessageLevel.INF, "Cancellazione messaggio " + msgId + " effettuata con successo"));
            getMessageBox().setViewMode(ViewMode.plain);
            getForm().getCodaList().getTable().remove(rowIndex);
            checkDuplicatiInList(((BaseTable) getForm().getCodaList().getTable()),
                    ((BaseTable) getForm().getCodaList().getTable()).toList("object_id"));
        } catch (JMSException ex) {
            getMessageBox().addMessage(new Message(MessageLevel.ERR, ex.getMessage()));
            getMessageBox().setViewMode(ViewMode.plain);
            log.error(ex.getMessage());
        }
        forwardToPublisher(Application.Publisher.CERCA_MSG_IN_CODA);
    }

    @Override
    public void updateAmbienteVersList() throws EMFError {
        PigAmbienteVersRowBean ambienteRowBean = amministrazioneEjb.getPigAmbienteVersRowBean(
                ((PigAmbienteVersRowBean) (getForm().getAmbienteVersList().getTable().getCurrentRow()))
                        .getIdAmbienteVers());
        PigVersRowBean versRowBean = new PigVersRowBean();
        versRowBean.setIdAmbienteVers(ambienteRowBean.getIdAmbienteVers());

        if (amministrazioneEjb.getPigVersTableBean(versRowBean).isEmpty()) {
            getForm().getAmbienteVers().setEditMode();
        } else {
            getForm().getAmbienteVers().setViewMode();
            getForm().getAmbienteVers().getDs_ambiente_vers().setEditMode();
            getForm().getAmbienteVers().getDs_note().setEditMode();
            getForm().getAmbienteVers().getDt_fine_val().setEditMode();
        }

        getForm().getVisAmbienteVers().setStatus(Status.update);
        getForm().getAmbienteVers().setStatus(Status.update);
        getForm().getAmbienteVersList().setStatus(Status.update);

    }

    @Override
    public void updateVersList() throws EMFError {
        BigDecimal idVers = ((BaseRowInterface) (getForm().getVersList().getTable().getCurrentRow()))
                .getBigDecimal("id_vers");
        boolean isSessioniPresenti = controllaPresenzaSessioni(idVers);

        if (!isSessioniPresenti) {
            getForm().getVers().setEditMode();
        } else {
            getForm().getVers().setEditMode();
            getForm().getVers().getNm_vers().setViewMode();
        }

        getForm().getVers().getTipologia().setViewMode();

        PigAmbienteVersRowBean ambienteVers = amministrazioneEjb.getPigAmbienteVersRowBeanFromVers(idVers);
        getForm().getVers().getId_ambiente_vers().setValue(ambienteVers.getIdAmbienteVers().toString());
        getForm().getVisVers().setStatus(Status.update);
        getForm().getVers().setStatus(Status.update);
        getForm().getVersList().setStatus(Status.update);

        // Mostra/nascondi flag archivio restituito/cessato
        mostraNascondiFlagArchivioRestituitoCessato();

        // 30790
        getForm().getVers().getTi_stato_cartelle().setHidden(true);
    }

    private void mostraNascondiFlagArchivioRestituitoCessato() throws EMFError {
        Date dataOdierna = DateUtil.getDataOdiernaNoTime();
        Date dtIniValVers = getForm().getVers().getDt_ini_val_vers().parse();
        Date dtFineValVers = getForm().getVers().getDt_fine_val_vers().parse();
        getForm().getVers().getFl_archivio_restituito().setHidden(true);
        getForm().getVers().getFl_cessato().setHidden(true);
        switch (getForm().getVers().getStatus()) {
        case update:
            if (!(dtIniValVers.compareTo(dataOdierna) <= 0 && dtFineValVers.compareTo(dataOdierna) >= 0)) {
                getForm().getVers().getFl_archivio_restituito().setHidden(false);
                getForm().getVers().getFl_cessato().setHidden(true);
                getForm().getVers().getFl_archivio_restituito().setEditMode();
                if (getForm().getVers().getFl_archivio_restituito().parse() != null
                        && getForm().getVers().getFl_archivio_restituito().parse().equals("1")) {
                    getForm().getVers().getFl_cessato().setHidden(false);
                    getForm().getVers().getFl_cessato().setViewMode();
                } else {
                    getForm().getVers().getFl_cessato().setHidden(true);
                    getForm().getVers().getFl_cessato().setViewMode();
                }
            }
            break;
        case view:
            // Bottone cessazione versatore
            getForm().getVers().getCessaVersatore().setEditMode();
            if (getForm().getVers().getFl_cessato().parse() != null
                    && getForm().getVers().getFl_cessato().parse().equals("0")
                    && getForm().getVers().getTipologia().parse() != null
                    && getForm().getVers().getTipologia().parse().equals("PRODUTTORE")) {
                getForm().getVers().getCessaVersatore().setReadonly(false);
            } else {
                getForm().getVers().getCessaVersatore().setReadonly(true);
            }
            if (!(dtIniValVers.compareTo(dataOdierna) <= 0 && dtFineValVers.compareTo(dataOdierna) >= 0)) {
                getForm().getVers().getFl_archivio_restituito().setHidden(false);
                getForm().getVers().getFl_archivio_restituito().setViewMode();
                if (getForm().getVers().getFl_archivio_restituito().parse() != null
                        && getForm().getVers().getFl_archivio_restituito().parse().equals("1")) {
                    getForm().getVers().getFl_cessato().setHidden(false);
                    getForm().getVers().getFl_cessato().setViewMode();
                } else {
                    getForm().getVers().getFl_cessato().setHidden(true);
                    getForm().getVers().getFl_cessato().setViewMode();
                }
            }
            break;
        case insert:
            getForm().getVers().getFl_archivio_restituito().setValue("0");
            getForm().getVers().getFl_cessato().setValue("0");
            getForm().getVers().getCessaVersatore().setViewMode();
            break;
        default:
            break;
        }
    }

    @Override
    public void updateTipoObjectList() throws EMFError {
        getForm().getTipoObject().setEditMode();
        getForm().getTipoObject().setStatus(Status.update);
        getForm().getTipoObjectList().setStatus(Status.update);
        // MEV#14347: CONTROLLO CHE RENDE IMMODIFICABILE IL NOME DEL TIPO OGGETTO
        if (amministrazioneHelper.esisteOggettoPerIdTipo(getForm().getTipoObject().getId_tipo_object().parse())) {
            getForm().getTipoObject().getNm_tipo_object().setViewMode();
        } else {
            getForm().getTipoObject().getNm_tipo_object().setEditMode();
        }
    }

    @Override
    public void updateTipoFileObjectList() throws EMFError {
        String tiVersFile = getForm().getTipoObject().getTi_vers_file().parse();
        getForm().getTipoFileObject().getTi_vers_file().setValue(tiVersFile);

        if (it.eng.sacerasi.common.Constants.TipoVersamento.NO_ZIP.name().equals(tiVersFile)) {
            getForm().getTipoFileObject().getFl_vers_sacer_asinc().setValue("SI");
            hideFieldsTipoFileObj(false);
        } else {
            getForm().getTipoFileObject().getFl_vers_sacer_asinc().setValue("NO");
            getForm().getTipoFileObject().getFl_calc_hash_sacer().setValue(WebConstants.DB_TRUE);
            hideFieldsTipoFileObj(true);
        }

        getForm().getTipoFileObject().setEditMode();
        getForm().getTipoFileObject().getFl_vers_sacer_asinc().setViewMode();
        getForm().getTipoFileObject().setStatus(Status.update);
        getForm().getTipoFileObject().setStatus(Status.update);
        getForm().getTipoFileObjectList().setStatus(Status.update);
    }

    @Override
    public void updateSopClassList() throws EMFError {

        PigSopClassDicomRowBean sopClassDicomRowBean = amministrazioneEjb.getPigSopClassDicomRowBean(
                ((PigSopClassDicomRowBean) (getForm().getSopClassList().getTable().getCurrentRow()))
                        .getIdSopClassDicom());

        if (amministrazioneEjb.getPigSopClassDicomVersTableBean(sopClassDicomRowBean.getIdSopClassDicom(), null)
                .isEmpty()) {
            getForm().getSopClass().setEditMode();
        } else {
            getForm().getSopClass().setViewMode();
            getForm().getSopClass().getDs_sop_class_dicom().setEditMode();
        }
        getForm().getButtonAllList().getSelect_all().setEditMode();
        getForm().getButtonAllList().getDeselect_all().setEditMode();
        getForm().getSopClass().setStatus(Status.update);
        getForm().getSopClassList().setStatus(Status.update);
    }

    @Override
    public void updateXsdDatiSpecList() throws EMFError {

        getMessageBox().clear();

        PigXsdDatiSpecRowBean xsdDatiSpecRowBean = amministrazioneEjb.getPigXsdDatiSpecRowBean(
                ((PigXsdDatiSpecRowBean) (getForm().getXsdDatiSpecList().getTable().getCurrentRow())).getIdXsdSpec());
        PigXsdDatiSpecRowBean lastXsd = new PigXsdDatiSpecRowBean();

        if (xsdDatiSpecRowBean.getIdTipoObject() != null) {
            lastXsd = amministrazioneEjb.getLastXsdDatiSpec(xsdDatiSpecRowBean.getIdTipoObject(), null);
        } else if (xsdDatiSpecRowBean.getIdTipoFileObject() != null) {
            lastXsd = amministrazioneEjb.getLastXsdDatiSpec(null, xsdDatiSpecRowBean.getIdTipoFileObject());
        }

        if (!lastXsd.getDtVersioneXsd().equals(xsdDatiSpecRowBean.getDtVersioneXsd())) {
            getMessageBox().addError("Versione non modificabile. ");
        }

        if (getMessageBox().isEmpty()) {
            getForm().getXsdDatiSpec().getCd_versione_xsd().setViewMode();
            getForm().getXsdDatiSpec().getBl_xsd().setViewMode();
            getForm().getXsdDatiSpec().getScaricaXsdButton().setViewMode();
            getForm().getXsdDatiSpec().setStatus(Status.update);
            getForm().getXsdDatiSpecList().setStatus(Status.update);
        } else {
            forwardToPublisher(getLastPublisher());
        }
    }

    @Override
    public void updateAttribDatiSpecList() throws EMFError {

        getMessageBox().clear();

        BigDecimal idXsdDatiSpec = ((PigXsdDatiSpecRowBean) getForm().getXsdDatiSpecList().getTable().getCurrentRow())
                .getIdXsdSpec();
        PigXsdDatiSpecRowBean xsdDatiSpecRowBean = amministrazioneEjb.getPigXsdDatiSpecRowBean(idXsdDatiSpec);
        PigXsdDatiSpecRowBean lastXsd = amministrazioneEjb.getLastXsdDatiSpec(xsdDatiSpecRowBean.getIdTipoObject(),
                xsdDatiSpecRowBean.getIdTipoFileObject());

        if (!lastXsd.getDtVersioneXsd().equals(xsdDatiSpecRowBean.getDtVersioneXsd())) {
            getMessageBox().addError("Attributo non modificabile");
        }

        if (getMessageBox().isEmpty()) {
            getForm().getAttribDatiSpec().getNm_col_dati_spec().setEditMode();
            getForm().getAttribDatiSpec().getCd_datatype_xsd().setEditMode();
            getForm().getAttribDatiSpec().getTi_datatype_col().setEditMode();
            getForm().getAttribDatiSpec().getFl_vers_sacer().setEditMode();
            getForm().getAttribDatiSpec().getFl_filtro_diario().setEditMode();
            getForm().getAttribDatiSpec().setStatus(Status.update);
            getForm().getAttribDatiSpecList().setStatus(Status.update);
        }
    }

    public void populateComboTipoObj() {

        BaseTable bt = new BaseTable();

        BaseRow br = new BaseRow();
        BaseRow br1 = new BaseRow();
        BaseRow br2 = new BaseRow();
        DecodeMap map = new DecodeMap();
        DecodeMap mapCalc = new DecodeMap();
        DecodeMap mapCons = new DecodeMap();
        for (Enum<?> row : Util.sortEnum(it.eng.sacerasi.common.Constants.TipoVersamento.values())) {
            br.setString("ti_vers_file", row.name());
            bt.add(br);
        }

        map.populatedMap(bt, "ti_vers_file", "ti_vers_file");
        getForm().getTipoObject().getTi_vers_file().setDecodeMap(map);
        bt.clear();

        for (Enum<?> row : Util.sortEnum(it.eng.sacerasi.common.Constants.TipoCalcolo.values())) {
            br1.setString("ti_calc_key_unita_doc", row.name());
            bt.add(br1);
        }

        mapCalc.populatedMap(bt, "ti_calc_key_unita_doc", "ti_calc_key_unita_doc");
        getForm().getTipoObject().getTi_calc_key_unita_doc().setDecodeMap(mapCalc);
        bt.clear();

        for (Enum<?> row : Util.sortEnum(WebConstants.conservazione.values())) {
            br2.setString("ti_conservazione", row.name());
            bt.add(br2);
        }

        mapCons.populatedMap(bt, "ti_conservazione", "ti_conservazione");
        getForm().getTipoObject().getTi_conservazione().setDecodeMap(mapCons);
        DecodeMap siNoMap = Util.getFlagComboDecodeMap();
        getForm().getTipoObject().getFl_contr_hash().setDecodeMap(siNoMap);
        getForm().getTipoObject().getFl_forza_accettazione_sacer().setDecodeMap(siNoMap);
        getForm().getTipoObject().getFl_forza_collegamento().setDecodeMap(siNoMap);
        getForm().getTipoObject().getFl_forza_conservazione().setDecodeMap(siNoMap);
        getForm().getTipoObject().getFl_no_visib_vers_ogg().setDecodeMap(siNoMap);
        getForm().getTipoObject().getFl_crea_tipofile().setDecodeMap(siNoMap);
        String tiVers = getForm().getTipoObject().getTi_vers_file().getValue();
        if (tiVers != null && (tiVers.equals(it.eng.sacerasi.common.Constants.TipoVersamento.ZIP_CON_XML_SACER.name())
                || tiVers.equals(it.eng.sacerasi.common.Constants.TipoVersamento.NO_ZIP.name()))) {
            getForm().getTipoObject().getTi_priorita_versamento().setDecodeMap(ComboGetter
                    .getMappaOrdinalGenericEnum("ti_priorita_versamento", Constants.ComboFlagPrioVersType.values()));
        }
    }

    private void populateComboTipoFileObj() {

        BaseTable bt = new BaseTable();
        BaseRow br = new BaseRow();
        BaseRow br1 = new BaseRow();
        DecodeMap mapDoc = new DecodeMap();
        DecodeMap mapCalc = new DecodeMap();

        for (Enum<?> row : it.eng.sacerasi.common.Constants.DocTypeEnum.values()) {
            br.setString("ti_doc_sacer", row.name());
            bt.add(br);
        }

        mapDoc.populatedMap(bt, "ti_doc_sacer", "ti_doc_sacer");
        getForm().getTipoFileObject().getTi_doc_sacer().setDecodeMap(mapDoc);
        bt.clear();

        for (Enum<?> row : Util.sortEnum(it.eng.sacerasi.common.Constants.HashCalcType.values())) {
            br1.setString("ti_calc_hash_sacer", row.name());
            bt.add(br1);
        }

        mapCalc.populatedMap(bt, "ti_calc_hash_sacer", "ti_calc_hash_sacer");
        getForm().getTipoFileObject().getTi_calc_hash_sacer().setDecodeMap(mapCalc);
        bt.clear();
        DecodeMap siNoMap = Util.getFlagComboDecodeMap();
        getForm().getTipoFileObject().getFl_calc_hash_sacer().setDecodeMap(siNoMap);
        getForm().getTipoFileObject().getFl_ver_firma_fmt_sacer().setDecodeMap(siNoMap);
    }

    public void populateComboVers() {
        DecodeMap mappaAmbienti = new DecodeMap();
        BaseTableInterface<?> ambienteTableBean = amministrazioneEjb
                .getUsrVAbilAmbXverTableBean(getUser().getIdUtente(), configHelper.getParamApplicApplicationName());
        mappaAmbienti.populatedMap(ambienteTableBean, "id_ambiente", "nm_ambiente");
        getForm().getVers().getId_ambiente_vers().setDecodeMap(mappaAmbienti);

        // Tipologia
        getForm().getVers().getTipologia().setDecodeMap(ComboGetter.getMappaTipologia());

        // Combo corrispondenza in Sacer
        getForm().getVers().getTi_dich_vers().setDecodeMap(ComboGetter.getMappaTiDichVers());
        getForm().getVers().getId_organiz_iam().setDecodeMap(new DecodeMap());
    }

    public void populateComboVisVers() throws EMFError {
        PigAmbienteVersTableBean ambienteVersTableBean = amministrazioneEjb.getPigAmbienteVersAbilitatiTableBean(null,
                getUser().getIdUtente(), false);
        BaseTable strutVersSacerTableBean = amministrazioneEjb.getNmUseridSacerByPigVLisStrutVersSacerTableBean();

        // Combo ambiente versatore
        DecodeMap mappaAmbVers = new DecodeMap();
        mappaAmbVers.populatedMap(ambienteVersTableBean, "nm_ambiente_vers", "nm_ambiente_vers");
        getForm().getVisVers().getNm_ambiente_vers().setDecodeMap(mappaAmbVers);

        // Combo Tipo versatore MEV#25727 - Aggiungere colonna tipo versatore in gestione versatori
        getForm().getVisVers().getNm_tipo_versatore().setDecodeMap(ComboGetter.getTipiVersatore());

        // Combo ambiente, ente, struttura
        try {
            UsrVAbilStrutSacerXpingTableBean ambientiTb = amministrazioneEjb
                    .getNmAmbientiSacer(getUser().getIdUtente());
            getForm().getVisVers().getNm_ambiente_sacer()
                    .setDecodeMap(DecodeMap.Factory.newInstance(ambientiTb, "id_ambiente", "nm_ambiente"));
            getForm().getVisVers().getNm_ente_sacer().setDecodeMap(new DecodeMap());
            getForm().getVisVers().getNm_strut_sacer().setDecodeMap(new DecodeMap());
        } catch (ParerUserError ex) {
            getMessageBox().addError(ex.getDescription());
        }

        // Combo UserID Sacer
        DecodeMap mappaNmUseridSacer = new DecodeMap();
        mappaNmUseridSacer.populatedMap(strutVersSacerTableBean, "nm_userid_sacer", "nm_userid_sacer");
        getForm().getVisVers().getNm_userid_sacer().setDecodeMap(mappaNmUseridSacer);

        // MEV27543
        DecodeMap mappaNmAmbienteEnteConvenz = new DecodeMap();
        mappaNmAmbienteEnteConvenz.populatedMap(
                amministrazioneEjb.getPigVRicVersAmbientiIamTableBean(getUser().getIdUtente()),
                "nm_ambiente_ente_convenz", "nm_ambiente_ente_convenz");
        getForm().getVisVers().getNm_ambiente_ente_convenz().setDecodeMap(mappaNmAmbienteEnteConvenz);
    }

    private void populateComboAttribDatiSpec() {

        BaseTable bt = new BaseTable();
        BaseRow br = new BaseRow();

        DecodeMap mapDatatype = new DecodeMap();

        for (Enum<?> row : WebConstants.tiDatatype.values()) {
            br.setString("ti_datatype_col", row.name());
            bt.add(br);
        }

        mapDatatype.populatedMap(bt, "ti_datatype_col", "ti_datatype_col");
        getForm().getAttribDatiSpec().getTi_datatype_col().setDecodeMap(mapDatatype);

    }

    private String doFormControl(PigTipoObjectRowBean tipoObjRowBean) {

        String result = null;

        if (tipoObjRowBean.getTiCalcKeyUnitaDoc() != null) {

            String tiCalcKeyUD = tipoObjRowBean.getTiCalcKeyUnitaDoc();

            if (tiCalcKeyUD.equals(it.eng.sacerasi.common.Constants.TipoCalcolo.CARTELLA_ZIP.name())) {
                if (tipoObjRowBean.getTiVersFile()
                        .equals(it.eng.sacerasi.common.Constants.TipoVersamento.NO_ZIP.name())) {
                    result = "Tipo SIP non compatibile con il Tipo Calcolo Unit\u00E0 Documentaria";
                } else if (tipoObjRowBean.getTiVersFile()
                        .equals(it.eng.sacerasi.common.Constants.TipoVersamento.ZIP_CON_XML_SACER.name())
                        || tipoObjRowBean.getTiVersFile()
                                .equals(it.eng.sacerasi.common.Constants.TipoVersamento.DA_TRASFORMARE.name())) {
                    if ((tipoObjRowBean.getTiConservazione() != null)
                            || (tipoObjRowBean.getCdRegistroUnitaDocSacer() != null)
                            || (tipoObjRowBean.getNmTipoUnitaDocSacer() != null)) {
                        result = "I seguenti campi sono obbligatori : Tipo Conservazione, Nome Registro in Sacer, Nome Tipologia Unit\u00E0 Documentaria in Sacer";
                    }

                } else if (tipoObjRowBean.getTiVersFile()
                        .equals(it.eng.sacerasi.common.Constants.TipoVersamento.ZIP_NO_XML_SACER.name())) {
                    if ((tipoObjRowBean.getTiConservazione() == null)
                            || (tipoObjRowBean.getNmTipoUnitaDocSacer() == null)) {

                        result = "I seguenti campi sono obbligatori : Tipo Conservazione, Nome Tipologia Unit\u00E0 Documentaria in Sacer";
                    }
                }

            } else if (tiCalcKeyUD.equals(it.eng.sacerasi.common.Constants.TipoCalcolo.CALC_DICOM.name())) {
                if (!tipoObjRowBean.getTiVersFile()
                        .equals(it.eng.sacerasi.common.Constants.TipoVersamento.NO_ZIP.name())) {
                    result = "Tipo SIP non compatibile con il Tipo Calcolo Unit\u00E0 Documentaria";
                } else {
                    if (tipoObjRowBean.getTiConservazione() == null
                            || tipoObjRowBean.getCdRegistroUnitaDocSacer() == null
                            || tipoObjRowBean.getNmTipoUnitaDocSacer() == null) {

                        result = "I seguenti campi sono obbligatori : Tipo Conservazione, Nome Registro in Sacer, Nome Tipologia Unit\u00E0 Documentaria in Sacer";
                    }
                }

            }
        }
        return result;
    }

    @Override
    public void tabPigTipoFileObjectOnClick() throws EMFError {

        getForm().getTipoObjTab().setCurrentTab(getForm().getTipoObjTab().getPigTipoFileObject());
        PigTipoFileObjectTableBean tipoFileObjTableBean = amministrazioneEjb.getPigTipoFileObjectTableBean(
                ((PigTipoObjectRowBean) getForm().getTipoObjectList().getTable().getCurrentRow()).getIdTipoObject());

        getForm().getTipoFileObjectList().setTable(tipoFileObjTableBean);
        getForm().getTipoFileObjectList().getTable().first();
        getForm().getTipoFileObjectList().setStatus(Status.view);
        getForm().getTipoFileObjectList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);

        forwardToPublisher(Application.Publisher.TIPO_OBJECT_DETAIL);
    }

    @Override
    public void tabPigXsdDatiSpecOnClick() throws EMFError {
        getForm().getTipoObjTab().setCurrentTab(getForm().getTipoObjTab().getPigXsdDatiSpec());
        PigTipoObjectRowBean tipoObjRowBean = ((PigTipoObjectRowBean) getForm().getTipoObjectList().getTable()
                .getCurrentRow());

        PigXsdDatiSpecTableBean xsdDatiSpecTableBean = amministrazioneEjb
                .getPigXsdDatiSpecTableBean(tipoObjRowBean.getIdTipoObject(), null);

        getForm().getXsdDatiSpecList().setTable(xsdDatiSpecTableBean);
        getForm().getXsdDatiSpecList().getTable().first();
        getForm().getXsdDatiSpecList().setStatus(Status.view);
        getForm().getXsdDatiSpecList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);

        forwardToPublisher(Application.Publisher.TIPO_OBJECT_DETAIL);
    }

    @Override
    public void associaSopClassButton() throws EMFError {

        BigDecimal idVers = ((BaseRowInterface) getForm().getVersList().getTable().getCurrentRow())
                .getBigDecimal("id_vers");
        // Carico lista disponibili
        PigSopClassDicomTableBean sopClassDispTable = amministrazioneEjb.getPigSopClassDispTableBean(idVers);
        getForm().getSopClassDispList().setTable(sopClassDispTable);
        getForm().getSopClassDispList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
        getForm().getSopClassDispList().getTable()
                .addSortingRule(PigSopClassDicomTableDescriptor.COL_CD_SOP_CLASS_DICOM, SortingRule.ASC);
        getForm().getSopClassDispList().getTable().sort();

        // Carico lista già associati
        PigSopClassDicomTableBean sopClassToVersTable = amministrazioneEjb.getPigSopClassVersTableBean(idVers);
        getForm().getSopClassToVersList().setTable(sopClassToVersTable);
        getForm().getSopClassToVersList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
        getForm().getSopClassToVersList().getTable()
                .addSortingRule(PigSopClassDicomTableDescriptor.COL_CD_SOP_CLASS_DICOM, SortingRule.ASC);
        getForm().getSopClassToVersList().getTable().sort();

        getForm().getButtonAllList().setEditMode();
        getForm().getButtonAllList().getSelect_all().setEditMode();
        getForm().getButtonAllList().getDeselect_all().setEditMode();
        getForm().getSopClassVers().setStatus(Status.insert);
        forwardToPublisher(Application.Publisher.SOP_CLASS_DICOM_VERS_DETAIL);
    }

    @Override
    public void selectSopClassDispList() {

        PigSopClassDicomRowBean currentRow = (PigSopClassDicomRowBean) getForm().getSopClassDispList().getTable()
                .getCurrentRow();
        int index = getForm().getSopClassDispList().getTable().getCurrentRowIndex();

        getForm().getSopClassDispList().getTable().remove(index);
        getForm().getSopClassDispList().getTable().sort();
        getForm().getSopClassToVersList().add(currentRow);
        getForm().getSopClassToVersList().getTable().sort();
        forwardToPublisher(Application.Publisher.SOP_CLASS_DICOM_VERS_DETAIL);
    }

    @Override
    public void selectSopClassToVersList() {

        PigSopClassDicomRowBean currentRow = (PigSopClassDicomRowBean) getForm().getSopClassToVersList().getTable()
                .getCurrentRow();
        int index = getForm().getSopClassToVersList().getTable().getCurrentRowIndex();

        getForm().getSopClassToVersList().getTable().remove(index);
        getForm().getSopClassToVersList().getTable().sort();
        getForm().getSopClassDispList().add(currentRow);
        getForm().getSopClassDispList().getTable().sort();
        forwardToPublisher(Application.Publisher.SOP_CLASS_DICOM_VERS_DETAIL);
    }

    @Override
    public void select_all() throws Throwable {
        PigSopClassDicomTableBean dispTable = (PigSopClassDicomTableBean) getForm().getSopClassDispList().getTable();

        for (PigSopClassDicomRowBean row : dispTable) {
            getForm().getSopClassToVersList().getTable().add(row);
        }
        dispTable.removeAll();
        forwardToPublisher(Application.Publisher.SOP_CLASS_DICOM_VERS_DETAIL);
    }

    @Override
    public void deselect_all() throws Throwable {
        PigSopClassDicomTableBean toVersTable = (PigSopClassDicomTableBean) getForm().getSopClassToVersList()
                .getTable();

        for (PigSopClassDicomRowBean row : toVersTable) {
            getForm().getSopClassDispList().getTable().add(row);
        }
        toVersTable.removeAll();

        forwardToPublisher(Application.Publisher.SOP_CLASS_DICOM_VERS_DETAIL);
    }

    @Override
    public void tabPigTipoObjectOnClick() throws EMFError {
        getForm().getVersTab().setCurrentTab(getForm().getVersTab().getPigTipoObject());

        PigTipoObjectTableBean versTableBean = amministrazioneEjb.getPigTipoObjectTableBean(
                ((BaseRowInterface) getForm().getVersList().getTable().getCurrentRow()).getBigDecimal("id_vers"));

        getForm().getTipoObjectList().setTable(versTableBean);
        getForm().getTipoObjectList().getTable().first();
        getForm().getTipoObjectList().setStatus(Status.view);
        getForm().getTipoObjectList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);

        forwardToPublisher(Application.Publisher.VERS_DETAIL);
    }

    @Override
    public void tabPigSopClassDicomVersOnClick() throws EMFError {
        getForm().getVersTab().setCurrentTab(getForm().getVersTab().getPigSopClassDicomVers());

        BigDecimal idVers = ((BaseRowInterface) getForm().getVersList().getTable().getCurrentRow())
                .getBigDecimal("id_vers");
        PigSopClassDicomTableBean pigSopClassDicomTableBean = amministrazioneEjb.getPigSopClassVersTableBean(idVers);

        pigSopClassDicomTableBean.addSortingRule(PigSopClassDicomTableDescriptor.COL_CD_SOP_CLASS_DICOM,
                SortingRule.ASC);
        pigSopClassDicomTableBean.sort();

        getForm().getSopClassList().setTable(pigSopClassDicomTableBean);
        getForm().getSopClassList().getTable().first();
        getForm().getSopClassList().setStatus(Status.view);
        getForm().getSopClassList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
        getForm().getSopClassList().setUserOperations(false, false, false, false);

        forwardToPublisher(Application.Publisher.VERS_DETAIL);
    }

    @Override
    public void tabXsdAssociatiTabOnClick() throws EMFError {
        getForm().getTipoFileObjTab().setCurrentTab(getForm().getTipoFileObjTab().getXsdAssociatiTab());
        forwardToPublisher(Application.Publisher.TIPO_FILE_OBJECT_DETAIL);
    }

    @Override
    public void scaricaXsdButton() throws EMFError {

        PigXsdDatiSpecRowBean currentRow = (PigXsdDatiSpecRowBean) getForm().getXsdDatiSpecList().getTable()
                .getCurrentRow();
        String nomeTipo = "";
        if (getForm().getIdList().getId_tipo_object().parse() != null) {
            nomeTipo = amministrazioneEjb.getPigTipoObjectRowBean(currentRow.getIdTipoObject()).getNmTipoObject();
        } else if (getForm().getIdList().getId_tipo_file_object().parse() != null) {
            nomeTipo = amministrazioneEjb.getPigTipoFileObjectRowBean(currentRow.getIdTipoFileObject())
                    .getNmTipoFileObject();
        }
        String codiceVersione = currentRow.getCdVersioneXsd();
        String filename = nomeTipo + "_xsd_" + codiceVersione;
        getResponse().setContentType("application/zip");
        getResponse().setHeader("Content-Disposition", "attachment; filename=\"" + filename + ".zip");
        // definiamo l'output previsto che sar? un file in formato zip
        // di cui si occuperà la servlet per fare il download
        ZipOutputStream out = new ZipOutputStream(getServletOutputStream());
        try {
            zipXsd(out, currentRow, filename);
            out.flush();
            out.close();
            freeze();
        } catch (Exception e) {
            getMessageBox()
                    .addMessage(new Message(Message.MessageLevel.ERR, "Errore nel recupero dei file da zippare"));
            log.error("Eccezione", e);
        }
    }

    private void zipXsd(ZipOutputStream out, PigXsdDatiSpecRowBean xsdRowBean, String filename) throws IOException {
        // definiamo il buffer per lo stream di bytes
        byte[] data = new byte[1000];
        InputStream is = null;
        // MAC#21777 - Problema scarico xsd dati specifici tipo oggetto
        if (xsdRowBean != null && xsdRowBean.getBlXsd() != null) {
            byte[] blob = xsdRowBean.getBlXsd().getBytes();
            if (blob != null) {
                is = new ByteArrayInputStream(blob);
                int count;
                out.putNextEntry(new ZipEntry(filename + ".xsd"));
                while ((count = is.read(data, 0, 1000)) != -1) {
                    out.write(data, 0, count);
                }
                out.closeEntry();
            }
        }
        // MAC#21777 - Problema scarico xsd dati specifici tipo oggetto
        if (is != null) {
            is.close();
        }
    }

    @Override
    public void caricaXsdButton() throws EMFError {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private void readXsdDatiSpecForm() throws EMFError {

        getMessageBox().clear();
        int sizeMb = WebConstants.FILESIZE * WebConstants.FILESIZE;

        try {
            // Create a factory for disk-based file items
            DiskFileItemFactory factory = new DiskFileItemFactory();
            // maximum size that will be stored in memory
            factory.setSizeThreshold(sizeMb);
            // the location for saving data that is larger than
            factory.setRepository(
                    new File(ConfigSingleton.getInstance().getStringValue(LOAD_XSD_APP_UPLOAD_DIR.name())));
            // Create a new file upload handler
            ServletFileUpload upload = new ServletFileUpload(factory);
            // maximum size before a FileUploadException will be thrown
            upload.setSizeMax(ConfigSingleton.getInstance().getLongValue(LOAD_XSD_APP_MAX_REQUEST_SIZE.name()));
            upload.setFileSizeMax(ConfigSingleton.getInstance().getLongValue(LOAD_XSD_APP_MAX_FILE_SIZE.name()));
            List<FileItem> items = upload.parseRequest(getRequest());
            Iterator<FileItem> iter = items.iterator();

            DiskFileItem tmpFileItem = null;
            DiskFileItem tmpOperation = null;
            DiskFileItem tmpCdVersione = null;

            while (iter.hasNext()) {

                FileItem item = (FileItem) iter.next();
                if (!item.isFormField()) {
                    // se ? un FormField, ? sicuramente il nome file
                    tmpFileItem = (DiskFileItem) item;
                } else {
                    if (item.getFieldName().equals("Cd_versione_xsd")) {
                        getForm().getXsdDatiSpec().getCd_versione_xsd().setValue(item.getString());
                        tmpCdVersione = (DiskFileItem) item;

                        // se non ? cd_versione_xsd e non ? table, allora ? l'operation
                    } else if (!item.getFieldName().equals("table")) {
                        String fieldName = item.getFieldName();
                        if (fieldName.contains(NE_DETTAGLIO_CANCEL) || fieldName.contains(NE_DETTAGLIO_SAVE)) {
                            tmpOperation = (DiskFileItem) item;
                        }
                    }
                }
            }

            if (tmpOperation.getFieldName().contains(NE_DETTAGLIO_CANCEL)) {

                getForm().getXsdDatiSpec().setViewMode();
                getForm().getXsdDatiSpec().getScaricaXsdButton().setEditMode();
                getForm().getXsdDatiSpec().getScaricaXsdButton().setDisableHourGlass(true);

                if (getForm().getXsdDatiSpec().getStatus().equals(Status.insert)) {

                    goBack();

                } else {

                    BigDecimal idXsdDatiSpec = ((PigXsdDatiSpecRowBean) getForm().getXsdDatiSpecList().getTable()
                            .getCurrentRow()).getIdXsdSpec();
                    PigXsdDatiSpecRowBean xsdDatiSpecRowBean = amministrazioneEjb
                            .getPigXsdDatiSpecRowBean(idXsdDatiSpec);
                    getForm().getXsdDatiSpec().copyFromBean(xsdDatiSpecRowBean);

                    if (xsdDatiSpecRowBean.getIdTipoObject() != null) {
                        getForm().getIdList().getId_tipo_object()
                                .setValue(xsdDatiSpecRowBean.getIdTipoObject().toString());
                    } else if (xsdDatiSpecRowBean.getIdTipoFileObject() != null) {
                        getForm().getIdList().getId_tipo_file_object()
                                .setValue(xsdDatiSpecRowBean.getIdTipoFileObject().toString());
                    }

                    getForm().getXsdDatiSpec().setStatus(Status.view);
                    getForm().getXsdDatiSpecList().setStatus(Status.view);
                    PigAttribDatiSpecTableBean attribDatiSpecTableBean = amministrazioneEjb
                            .getPigAttribDatiSpecTableBean(idXsdDatiSpec);

                    getForm().getAttribDatiSpecList().setTable(attribDatiSpecTableBean);
                    getForm().getAttribDatiSpecList().getTable().first();
                    getForm().getAttribDatiSpecList().setStatus(Status.view);
                    getForm().getAttribDatiSpecList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);

                    forwardToPublisher(Application.Publisher.XSD_DATI_SPEC_DETAIL);
                }

            } else if (tmpOperation.getFieldName().contains(NE_DETTAGLIO_SAVE)) {

                // controllo esistenza del file
                if (tmpFileItem.getName().equals("")) {
                    getMessageBox().addError("Nessun file selezionato");
                }
                // controllo esistenza codice versione
                if (tmpCdVersione.getString().equals("")) {
                    getMessageBox().addError("Versione non inserita");
                }
                if (getMessageBox().isEmpty()) {

                    // conversione in stringa
                    String clob = new String(tmpFileItem.get());
                    getSession().setAttribute("fileXsd", clob);
                    getForm().getXsdDatiSpec().getFile_xsd().setValue("File " + tmpFileItem.getName() + " caricato");
                    salvaXsdDatiSpec(tmpCdVersione.getString(), clob);

                } else {
                    forwardToPublisher(Application.Publisher.XSD_DATI_SPEC_DETAIL);
                }
            }
        } catch (FileUploadException fue) {
            log.error("Eccezione nell'upload dei file", fue);
            getMessageBox().addError("Eccezione nell'upload dei file", fue);
            goBack();
        } catch (IncoherenceException ie) {
            log.error("Eccezione nell'upload dei file", ie);
            getMessageBox().addError("Eccezione nell'upload dei file", ie);
            goBack();
        }
    }

    public void duplicaVersatoreOperation() throws EMFError {
        populateComboVers();

        String riga = getRequest().getParameter("riga");
        Integer nr = Integer.parseInt(riga);
        getForm().getVersList().getTable().setCurrentRowIndex(nr);

        getForm().getVers().setStatus(Status.insert);
        getForm().getVers().setEditMode();
        getForm().getVersList().setStatus(Status.insert);

        getForm().getVers().clear();
        setDateStandard();
        getSession().setAttribute("duplicaVersDetail", "duplica");

        if (getForm().getVers().getTipologia().parse().equals("PRODUTTORE")) {
            getForm().getCorrispondenzaSacerSection().setHidden(false);
        } else {
            getForm().getCorrispondenzaSacerSection().setHidden(true);
        }

        mostraNascondiFlagArchivioRestituitoCessato();

        forwardToPublisher(Application.Publisher.DUPLICA_VERS_DETAIL);
    }

    private void setDateStandard() {
        getForm().getVers().getTipologia().setValue("PRODUTTORE");
        // Date precompilate
        String dataOdierna = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
        getForm().getVers().getDt_ini_val_appart_ambiente().setValue(dataOdierna);
        getForm().getVers().getDt_ini_val_vers().setValue(dataOdierna);
        Calendar cal = Calendar.getInstance();
        cal.set(2444, Calendar.DECEMBER, 31, 0, 0, 0);
        String dataFine = new SimpleDateFormat("dd/MM/yyyy").format(cal.getTime());
        getForm().getVers().getDt_fin_val_appart_ambiente().setValue(dataFine);
        getForm().getVers().getDt_fine_val_vers().setValue(dataFine);
        //
        getForm().getVers().getId_ambiente_ente_convenz_ec()
                .setDecodeMap(DecodeMap.Factory.newInstance(
                        amministrazioneEjb
                                .getUsrVAbilAmbEnteConvenzTableBean(BigDecimal.valueOf(getUser().getIdUtente())),
                        "id_ambiente_ente_convenz", "nm_ambiente_ente_convenz"));
        getForm().getVers().getId_ente_convenz_ec().setDecodeMap(new DecodeMap());
        getForm().getVers().getId_ente_convenz_fe().setDecodeMap(new DecodeMap());
    }

    public void changeViewTipoObj() {
        getForm().getTipoObject().post(getRequest());
        try {
            String tipoVers = getForm().getTipoObject().getTi_vers_file().parse();

            if (tipoVers != null && (tipoVers
                    .equals(it.eng.sacerasi.common.Constants.TipoVersamento.ZIP_CON_XML_SACER.name())
                    || tipoVers.equals(it.eng.sacerasi.common.Constants.TipoVersamento.DA_TRASFORMARE.name()))) {
                hideFieldsTipoObj(true);
            } else {
                hideFieldsTipoObj(false);
            }

        } catch (EMFError ex) {
            log.error("Eccezione nell'upload dei file", ex);
        }
        forwardToPublisher(Application.Publisher.TIPO_OBJECT_DETAIL);
    }

    private void hideFieldsTipoObj(boolean flHide) throws EMFError {
        getForm().getTipoObject().getCd_registro_unita_doc_sacer().setHidden(flHide);
        getForm().getTipoObject().getNm_tipo_unita_doc_sacer().setHidden(flHide);
        getForm().getTipoObject().getFl_forza_accettazione_sacer().setHidden(flHide);
        getForm().getTipoObject().getFl_forza_collegamento().setHidden(flHide);
        getForm().getTipoObject().getFl_forza_conservazione().setHidden(flHide);
        getForm().getTipoObject().getTi_conservazione().setHidden(flHide);
    }

    private void hideFieldsTipoFileObj(boolean flHide) {
        getForm().getTipoFileObject().getNm_tipo_doc_sacer().setHidden(flHide);
        getForm().getTipoFileObject().getTi_doc_sacer().setHidden(flHide);
        getForm().getTipoFileObject().getNm_tipo_strut_doc_sacer().setHidden(flHide);
        getForm().getTipoFileObject().getNm_tipo_comp_doc_sacer().setHidden(flHide);
        getForm().getTipoFileObject().getNm_fmt_file_calc_sacer().setHidden(flHide);
        getForm().getTipoFileObject().getNm_fmt_file_vers_sacer().setHidden(flHide);
        getForm().getTipoFileObject().getFl_ver_firma_fmt_sacer().setHidden(flHide);
        getForm().getTipoFileObject().getDs_fmt_rappr_calc_sacer().setHidden(flHide);
        getForm().getTipoFileObject().getDs_fmt_rappr_esteso_calc_sacer().setHidden(flHide);
        getForm().getTipoFileObject().getFl_calc_hash_sacer().setHidden(flHide);
        getForm().getTipoFileObject().getTi_calc_hash_sacer().setHidden(flHide);
    }

    @Secure(action = "Menu.AmministrazioneSistema.GestioneCoda")
    public void ricercaMsgInCoda() {

        getUser().getMenu().reset();
        getUser().getMenu().select("Menu.AmministrazioneSistema.GestioneCoda");

        getForm().getVisCoda().clear();
        getForm().getVisCoda().setEditMode();
        getForm().getCodaList().clear();

        getForm().getVisCoda().getNm_coda().setDecodeMap(getMappaNmCoda());
        getForm().getVisCoda().getTipo_selettore().setDecodeMap(getMappaTipoSelettore());

        getForm().getCodaList().getSelect_msg().setHidden(true);
        getForm().getVisCoda().getInviaSelezionati().setHidden(true);
        getForm().getVisCoda().getInviaTutti().setHidden(true);

        forwardToPublisher(Application.Publisher.CERCA_MSG_IN_CODA);
    }

    @Override
    public void visCodaButton() throws EMFError {
        eseguiRicercaMsgCoda(null, null);
    }

    public void eseguiRicercaMsgCoda(String nomeCoda, String selettore) throws EMFError {
        String nmCoda = nomeCoda;
        String tipoSelettore = selettore;
        if (nomeCoda == null) {
            VisCoda coda = getForm().getVisCoda();
            coda.post(getRequest());

            nmCoda = coda.getNm_coda().parse();
            tipoSelettore = coda.getTipo_selettore().parse();
        }

        List<InfoCoda> infoCodaList = new ArrayList<>();
        try {
            if (getForm().getVisCoda().validate(getMessageBox())) {
                infoCodaList = monitorCoda.retrieveMsgInQueue(nmCoda, tipoSelettore);
                // MQ - NASCONDO IL BOTTONE IN OGNI CASO
                // Mostra i campi se e solo se la coda selezionata non \u00E8 producerCodaVersQueue
                getForm().getCodaList().getInviaMessaggio()
                        .setHidden(nmCoda.equals(Constants.NomeCoda.producerCodaVersQueue.name()));
                getForm().getCodaList().getSelect_msg()
                        .setHidden(nmCoda.equals(Constants.NomeCoda.producerCodaVersQueue.name()));
                getForm().getVisCoda().getInviaSelezionati()
                        .setHidden(nmCoda.equals(Constants.NomeCoda.producerCodaVersQueue.name()));
                getForm().getVisCoda().getInviaTutti()
                        .setHidden(nmCoda.equals(Constants.NomeCoda.producerCodaVersQueue.name()));
            }
        } catch (JMSException ex) {
            log.error(ex.getMessage(), ex);
            throw new EMFError("Errore nella ricerca code", ex.getMessage());
        }

        BaseTable tabella = new BaseTable();
        for (InfoCoda infoCodaElement : infoCodaList) {
            BaseRow riga = new BaseRow();
            riga.setBigDecimal("object_id", new BigDecimal(infoCodaElement.getObjectId()));
            riga.setBigDecimal("unita_doc_session_id", new BigDecimal(infoCodaElement.getUnitaDocSessionId()));
            riga.setString("cd_registro_unita_doc_sacer", infoCodaElement.getCdRegistroUnitaDocSacer());
            riga.setBigDecimal("aa_unita_doc_sacer", infoCodaElement.getAaUnitaDocSacer());
            riga.setString("cd_key_unita_doc_sacer", infoCodaElement.getCdKeyUnitaDocSacer());
            riga.setString("message_selector", infoCodaElement.getMessageSelector());
            riga.setTimestamp("sent_timestamp", infoCodaElement.getSentTimestamp() != null
                    ? new Timestamp(infoCodaElement.getSentTimestamp().getTime()) : null);
            riga.setString("undelivered_comment", infoCodaElement.getUndeliveredComment());
            riga.setString("undelivered_reason", infoCodaElement.getUndeliveredReason());
            riga.setTimestamp("undelivered_timestamp", infoCodaElement.getUndeliveredTimestamp() != null
                    ? new Timestamp(infoCodaElement.getUndeliveredTimestamp().getTime()) : null);
            riga.setBigDecimal("delivery_count", new BigDecimal(infoCodaElement.getDeliveryCount()));
            riga.setString("message_id", infoCodaElement.getMessageID());
            riga.setString("nm_coda", nmCoda);
            tabella.add(riga);
        }
        checkDuplicatiInList(tabella, tabella.toList("object_id"));

        tabella.addSortingRule("object_id", SortingRule.ASC);
        tabella.sort();

        getForm().getCodaList().setTable(tabella);
        getForm().getCodaList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
        getForm().getCodaList().getTable().first();

        forwardToPublisher(Application.Publisher.CERCA_MSG_IN_CODA);
    }

    private void checkDuplicatiInList(BaseTable table, List<Object> objectListIds) {
        Set<BigDecimal> objectIds = new HashSet<>();
        Set<BigDecimal> duplicatedObjectIds = new HashSet<>();
        // Mi scorro due volte la lista per verificare i doppioni. Mezzo becero ma non ho trovato di meglio.
        for (Object element : objectListIds) {
            BigDecimal id = (BigDecimal) element;
            if (!objectIds.add(id)) {
                duplicatedObjectIds.add(id);
            }
        }
        for (BaseRow row : table) {
            BigDecimal rowId = row.getBigDecimal("object_id");
            row.setString("object_duplicato", duplicatedObjectIds.contains(rowId) ? "1" : "0");
        }
    }

    private DecodeMap getMappaNmCoda() {
        BaseTable bt = new BaseTable();
        for (NomeCoda n : NomeCoda.values()) {
            BaseRow br = new BaseRow();
            br.setString("nm_coda", n.name());
            bt.add(br);
        }
        DecodeMap mappaNmCoda = new DecodeMap();
        mappaNmCoda.populatedMap(bt, "nm_coda", "nm_coda");
        return mappaNmCoda;
    }

    private DecodeMap getMappaTipoSelettore() {
        BaseTable bt = new BaseTable();
        for (TipoSelettore t : TipoSelettore.values()) {
            BaseRow br = new BaseRow();
            br.setString("tipo_selettore", t.name());
            bt.add(br);
        }
        DecodeMap mappaTipoSelettore = new DecodeMap();
        mappaTipoSelettore.populatedMap(bt, "tipo_selettore", "tipo_selettore");
        return mappaTipoSelettore;
    }

    public void sendDmqMessage() throws EMFError {
        Integer riga = Integer.parseInt(getRequest().getParameter("riga"));
        String messageSelector = getForm().getCodaList().getTable().getRow(riga).getString("message_selector");
        MsgCounters counters = new MsgCounters();
        counters.setTotMsg(1);
        sendMsg(riga, counters);
        String userMsg;
        try {
            userMsg = monitorCoda.buildSingleUserMsg(counters.getMsgConsumedNum(), counters.getMsgDeliveredNum(),
                    messageSelector);
            log.info(userMsg);
            getMessageBox().addInfo(userMsg);
            getMessageBox().setViewMode(ViewMode.plain);
        } catch (JMSException ex) {
            log.error(ex.getMessage());
            getMessageBox().addError(ex.getMessage());
        }

        // Rieseguo la ricerca
        eseguiRicercaMsgCoda(getForm().getVisCoda().getNm_coda().parse(),
                getForm().getVisCoda().getTipo_selettore().parse());
        forwardToPublisher(Application.Publisher.CERCA_MSG_IN_CODA);
    }

    private void sendMsg(Integer riga, MsgCounters counters) throws EMFError {
        String messageId = getForm().getCodaList().getTable().getRow(riga).getString("message_id");
        String messageSelector = getForm().getCodaList().getTable().getRow(riga).getString("message_selector");
        String nomeCoda = getForm().getCodaList().getTable().getRow(riga).getString("nm_coda");
        BigDecimal unitaDocSessionId = getForm().getCodaList().getTable().getRow(riga)
                .getBigDecimal("unita_doc_session_id");
        BigDecimal idObject = getForm().getCodaList().getTable().getRow(riga).getBigDecimal("object_id");
        int msgConsumedNum = 0;
        int msgDeliveredNum = 0;
        try {
            String msgId = monitorCoda.redeliveryMsg(messageId, nomeCoda);
            if (TipoSelettore.CODA1.name().equals(messageSelector) || TipoSelettore.CODA2.name().equals(messageSelector)
                    || TipoSelettore.CODA3.name().equals(messageSelector)) {
                msgConsumedNum = monitorCoda.checkMsgConsumed(messageSelector, unitaDocSessionId);
                if (msgConsumedNum != 1) {
                    msgDeliveredNum = monitorCoda.checkMsgInQueue(messageSelector, unitaDocSessionId);
                }
            } else if (TipoSelettore.CODA_VER_HASH.name().equals(messageSelector)) {
                msgConsumedNum = monitorCoda.checkMsgConsumed(messageSelector, idObject);
                if (msgConsumedNum != 1) {
                    msgDeliveredNum = monitorCoda.checkMsgInQueue(messageSelector, idObject);
                }
            }
            counters.increaseMsgConsumedNum(msgConsumedNum);
            counters.increaseMsgDeliveredNum(msgDeliveredNum);

            if (msgConsumedNum > 0) {
                counters.addConsumedMsg(msgId);
            }
            if (msgDeliveredNum > 0) {
                counters.addDeliveredMsg(msgId);
            }

            log.info("id message = {}", msgId);
        } catch (JMSException | ParerInternalError ex) {
            log.error(ex.getMessage());
            throw new EMFError("Errore nell'invio del messaggio", ex.getMessage());
        }
    }

    @Override
    public void inviaSelezionati() throws EMFError {
        // Ottengo i componenti selezionati dalla lista
        String[] msgSelezionati = getRequest().getParameterValues(getForm().getCodaList().getSelect_msg().getName());
        if (msgSelezionati != null) {
            MsgCounters counters = new MsgCounters();
            for (String msg : msgSelezionati) {
                Integer riga = tryParse(msg);
                if (riga != null) {
                    counters.increaseTotMsg();
                    sendMsg(riga, counters);
                }
            }
            try {
                String userMsg = monitorCoda.buildMultipleUserMsg(counters.getTotMsg(), counters.getMsgConsumedNum(),
                        counters.getMsgDeliveredNum());
                log.info(userMsg);
                log.info("Messaggi inviati : {}", Arrays.toString(counters.getDeliveredMsgs().toArray()));
                log.info("Messaggi consumati : {}", Arrays.toString(counters.getConsumedMsgs().toArray()));
                getMessageBox().addInfo(userMsg);
                getMessageBox().setViewMode(ViewMode.plain);
            } catch (JMSException ex) {
                log.error(ex.getMessage());
                getMessageBox().addError(ex.getMessage());
            }
        } else {
            getMessageBox().addInfo("Seleziona almeno un messaggio");
            getMessageBox().setViewMode(ViewMode.plain);
        }
        eseguiRicercaMsgCoda(getForm().getVisCoda().getNm_coda().parse(),
                getForm().getVisCoda().getTipo_selettore().parse());
        forwardToPublisher(Application.Publisher.CERCA_MSG_IN_CODA);
    }

    public Integer tryParse(String obj) {
        Integer retVal;
        try {
            retVal = Integer.parseInt(obj);
        } catch (NumberFormatException nfe) {
            retVal = null;
        }
        return retVal;
    }

    @Override
    public void inviaTutti() throws EMFError {
        if (getForm().getCodaList().getTable() != null && !getForm().getCodaList().getTable().isEmpty()) {
            MsgCounters counters = new MsgCounters();
            counters.setTotMsg(getForm().getCodaList().getTable().size());
            for (int i = 0; i < getForm().getCodaList().getTable().size(); i++) {
                sendMsg(i, counters);
            }
            try {
                String userMsg = monitorCoda.buildMultipleUserMsg(counters.getTotMsg(), counters.getMsgConsumedNum(),
                        counters.getMsgDeliveredNum());
                log.info(userMsg);
                log.info("Messaggi inviati : {}", Arrays.toString(counters.getDeliveredMsgs().toArray()));
                log.info("Messaggi consumati : {}", Arrays.toString(counters.getConsumedMsgs().toArray()));
                getMessageBox().addInfo(userMsg);
                getMessageBox().setViewMode(ViewMode.plain);
            } catch (JMSException ex) {
                log.error(ex.getMessage());
                getMessageBox().addError(ex.getMessage());
            }
        } else {
            getMessageBox().addInfo("Seleziona almeno un messaggio");
            getMessageBox().setViewMode(ViewMode.plain);
        }
        eseguiRicercaMsgCoda(getForm().getVisCoda().getNm_coda().parse(),
                getForm().getVisCoda().getTipo_selettore().parse());
        forwardToPublisher(Application.Publisher.CERCA_MSG_IN_CODA);
    }

    private void aggiornaDettaglioXsd() throws EMFError {
        getForm().getXsdDatiSpec().setViewMode();
        getForm().getXsdDatiSpec().getScaricaXsdButton().setEditMode();
        getForm().getXsdDatiSpec().getScaricaXsdButton().setDisableHourGlass(true);

        BigDecimal idXsdDatiSpec = ((PigXsdDatiSpecRowBean) getForm().getXsdDatiSpecList().getTable().getCurrentRow())
                .getIdXsdSpec();
        PigXsdDatiSpecRowBean xsdDatiSpecRowBean = amministrazioneEjb.getPigXsdDatiSpecRowBean(idXsdDatiSpec);
        getForm().getXsdDatiSpec().copyFromBean(xsdDatiSpecRowBean);

        if (xsdDatiSpecRowBean.getIdTipoObject() != null) {
            getForm().getIdList().getId_tipo_object().setValue(xsdDatiSpecRowBean.getIdTipoObject().toString());
        } else if (xsdDatiSpecRowBean.getIdTipoFileObject() != null) {
            getForm().getIdList().getId_tipo_file_object()
                    .setValue(xsdDatiSpecRowBean.getIdTipoFileObject().toString());
        }

        getForm().getXsdDatiSpec().setStatus(Status.view);
        getForm().getXsdDatiSpecList().setStatus(Status.view);

        PigAttribDatiSpecTableBean attribDatiSpecTableBean = amministrazioneEjb
                .getPigAttribDatiSpecTableBean(idXsdDatiSpec);
        getForm().getAttribDatiSpecList().setTable(attribDatiSpecTableBean);
        getForm().getAttribDatiSpecList().getTable().first();
        getForm().getAttribDatiSpecList().setStatus(Status.view);
        getForm().getAttribDatiSpecList().setUserOperations(true, true, false, false);
        getForm().getAttribDatiSpecList().getFl_filtro_diario().setReadonly(true);
        getForm().getAttribDatiSpecList().getFl_vers_sacer().setReadonly(true);
        getForm().getAttribDatiSpecList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
    }

    private boolean controllaPresenzaSessioni(BigDecimal idVers) {
        boolean result = false;
        result = amministrazioneEjb.isSessioniPresentiPerVersatore(idVers);
        return result;
    }

    @Override
    public JSONObject triggerTipoObjectTi_vers_fileOnTrigger() throws EMFError {
        BaseTable bt = new BaseTable();
        BaseRow br1 = new BaseRow();
        DecodeMap mapCalc = new DecodeMap();

        TipoObject form = getForm().getTipoObject();
        // FIXME: Secondo me sta roba di ripopolare le combo è una [cit]***ata pazzesca[/cit]
        String oldTipoSip = form.getTi_vers_file().parse();
        form.post(getRequest());
        // MAC#29391 - Gestione della priorità di versamento del JOB PREPARA XML SACER
        populateComboTipoObj();

        String tiVersFile = form.getTi_vers_file().parse();
        if (tiVersFile != null && (tiVersFile.equals(it.eng.sacerasi.common.Constants.TipoVersamento.NO_ZIP.name())
                || tiVersFile.equals(it.eng.sacerasi.common.Constants.TipoVersamento.ZIP_CON_XML_SACER.name())
                || tiVersFile.equals(it.eng.sacerasi.common.Constants.TipoVersamento.ZIP_NO_XML_SACER.name()))) {
            if (getForm().getTipoObject().getStatus().equals(Status.insert)) {
                getForm().getTipoObject().getTi_dich_vers().setDecodeMap(ComboGetter.getMappaTiDichVers());
                getForm().getTipoObject().getId_organiz_iam().setDecodeMap(new DecodeMap());
            } else {
                if (oldTipoSip == null
                        || oldTipoSip.equals(it.eng.sacerasi.common.Constants.TipoVersamento.DA_TRASFORMARE.name())) {
                    getForm().getTipoObject().getTi_dich_vers().setDecodeMap(ComboGetter.getMappaTiDichVers());
                    getForm().getTipoObject().getId_organiz_iam().setDecodeMap(new DecodeMap());
                }
            }
        } else {
            getForm().getTipoObject().getTi_dich_vers().setDecodeMap(new DecodeMap());
            getForm().getTipoObject().getId_organiz_iam().setDecodeMap(new DecodeMap());
        }

        String noZip = it.eng.sacerasi.common.Constants.TipoVersamento.NO_ZIP.name();
        String tipoVersamentoFile = form.getTi_vers_file().getDecodedValue();
        form.getNm_tipo_unita_doc_sacer().setEditMode();
        if (noZip.equalsIgnoreCase(tipoVersamentoFile)) {
            for (Enum<?> row : Util.sortEnum(it.eng.sacerasi.common.Constants.TipoCalcolo.values())) {
                if (!row.equals(it.eng.sacerasi.common.Constants.TipoCalcolo.CARTELLA_ZIP)) {
                    br1.setString("ti_calc_key_unita_doc", row.name());
                    bt.add(br1);
                }
            }
            form.getCd_registro_unita_doc_sacer().setEditMode();

            // MAC 28344
            form.getFl_crea_tipofile().setValue("0");
            form.getFl_crea_tipofile().setHidden(true);

        } else {
            bt = new BaseTable();
            br1 = new BaseRow();
            br1.setString("ti_calc_key_unita_doc", it.eng.sacerasi.common.Constants.TipoCalcolo.CARTELLA_ZIP.name());
            bt.add(br1);
            form.getNm_tipo_unita_doc_sacer().setValue(null);

            // MAC 28344
            form.getFl_crea_tipofile().setValue("1");
            form.getFl_crea_tipofile().setHidden(false);

            if (it.eng.sacerasi.common.Constants.TipoVersamento.ZIP_CON_XML_SACER.name()
                    .equalsIgnoreCase(tipoVersamentoFile)
                    || it.eng.sacerasi.common.Constants.TipoVersamento.DA_TRASFORMARE.name()
                            .equalsIgnoreCase(tipoVersamentoFile)) {
                form.getCd_registro_unita_doc_sacer().setValue(null);
                form.getFl_forza_accettazione_sacer().setValue(null);
                form.getFl_forza_collegamento().setValue(null);
                form.getFl_forza_conservazione().setValue(null);
            }
        }
        mapCalc.populatedMap(bt, "ti_calc_key_unita_doc", "ti_calc_key_unita_doc");
        form.getTi_calc_key_unita_doc().setDecodeMap(mapCalc);

        // MEV#27321 - Introduzione della priorità di versamento di un oggetto ZIP_CON_XML_SACER e NO_ZIP
        if (tiVersFile != null && !tiVersFile.equals(it.eng.sacerasi.common.Constants.TipoVersamento.NO_ZIP.name())
                && !tiVersFile.equals(it.eng.sacerasi.common.Constants.TipoVersamento.ZIP_CON_XML_SACER.name())) {
            form.getTi_priorita_versamento().setValue("");
        } else {
            form.getTi_priorita_versamento()
                    .setValue(it.eng.sacerasi.web.util.Constants.ComboFlagPrioTrasfType.NORMALE.name());
        }

        try {
            if (tipoVersamentoFile.equals(it.eng.sacerasi.common.Constants.TipoVersamento.DA_TRASFORMARE.name())) {
                getForm().getTipoObject().getDs_reg_exp_cd_vers().setEditMode();
                getForm().getTipoObject().getId_trasf().setDecodeMap(DecodeMap.Factory
                        .newInstance(amministrazioneEjb.getXfoTrasfTableBean(), "id_trasf", "cd_trasf"));

                getForm().getTipoObject().getTi_priorita().setDecodeMap(ComboGetter
                        .getMappaOrdinalGenericEnum("ti_priorita", Constants.ComboFlagPrioTrasfType.values()));
            } else {
                getForm().getTipoObject().getDs_reg_exp_cd_vers().setViewMode();
                getForm().getTipoObject().getDs_reg_exp_cd_vers().clear();
                getForm().getTipoObject().getId_trasf().setDecodeMap(new DecodeMap());
                getForm().getTipoObject().getTi_priorita().setDecodeMap(new DecodeMap());
            }
        } catch (Exception ex) {
            getMessageBox().addError("Errore durante il recupero dei dati della tabella XFO_TRASF");
        }

        return form.asJSON();
    }

    @Override
    public JSONObject triggerTipoObjectTi_dich_versOnTrigger() throws EMFError {
        getForm().getTipoObject().post(getRequest());
        String tiVersFile = getForm().getTipoObject().getTi_vers_file().parse();
        if (tiVersFile != null && (tiVersFile.equals(it.eng.sacerasi.common.Constants.TipoVersamento.NO_ZIP.name())
                || tiVersFile.equals(it.eng.sacerasi.common.Constants.TipoVersamento.ZIP_CON_XML_SACER.name())
                || tiVersFile.equals(it.eng.sacerasi.common.Constants.TipoVersamento.ZIP_NO_XML_SACER.name()))) {

            String tiDichVers = getForm().getTipoObject().getTi_dich_vers().parse();
            if (StringUtils.isNoneBlank(tiDichVers)) {
                try {
                    getForm().getTipoObject().getId_organiz_iam().setDecodeMap(getMappaDlCompositoOrganiz(tiDichVers));
                    return getForm().getTipoObject().asJSON();
                } catch (Exception e) {
                    getForm().getTipoObject().asJSON(e.getMessage());
                }
            } else {
                getForm().getTipoObject().getTi_dich_vers().setDecodeMap(new DecodeMap());
                getForm().getTipoObject().getId_organiz_iam().setDecodeMap(new DecodeMap());
            }
        } else {
            getForm().getTipoObject().getTi_dich_vers().setViewMode();
            getForm().getTipoObject().getId_organiz_iam().setViewMode();
            getForm().getTipoObject().getTi_dich_vers().setDecodeMap(new DecodeMap());
            getForm().getTipoObject().getId_organiz_iam().setDecodeMap(new DecodeMap());
        }

        return getForm().getTipoObject().asJSON();
    }

    @Override
    public JSONObject triggerTipoObjectId_trasfOnTrigger() throws EMFError {
        getForm().getTipoObject().post(getRequest());
        BigDecimal idTrasf = getForm().getTipoObject().getId_trasf().parse();
        if (idTrasf != null) {
            getForm().getTipoObject().getDs_reg_exp_cd_vers().setEditMode();
            getForm().getTipoObject().getTi_priorita().setEditMode();
        } else {
            getForm().getTipoObject().getDs_reg_exp_cd_vers().setViewMode();
            getForm().getTipoObject().getTi_priorita().setViewMode();
        }
        return getForm().getTipoObject().asJSON();
    }

    @Override
    public JSONObject triggerVersatoreTrasfNm_ambiente_vers_trasfOnTrigger() throws EMFError {
        getForm().getVersatoreTrasf().post(getRequest());
        try {
            BigDecimal idAmbienteVers = getForm().getVersatoreTrasf().getNm_ambiente_vers_trasf().parse();
            BigDecimal idVers = getForm().getVersatoreCorrente().getId_vers_da_trasf().parse();
            if (idAmbienteVers != null) {
                PigVersTableBean vers = amministrazioneEjb.getPigVersTrasfComboTableBean(idAmbienteVers, idVers);
                getForm().getVersatoreTrasf().getNm_vers_trasf()
                        .setDecodeMap(DecodeMap.Factory.newInstance(vers, "id_vers", "nm_vers"));
            } else {
                getForm().getVersatoreTrasf().getNm_vers_trasf().setDecodeMap(new DecodeMap());
            }
        } catch (ParerUserError ex) {
            getMessageBox().addError(ex.getDescription());
        }
        return getForm().getVersatoreTrasf().asJSON();
    }

    @Override
    public JSONObject triggerVersatoreTrasfNm_vers_trasfOnTrigger() throws EMFError {
        getForm().getVersatoreTrasf().post(getRequest());
        BigDecimal idVers = getForm().getVersatoreTrasf().getNm_vers_trasf().parse();
        if (idVers != null) {
            PigVersRowBean vers = amministrazioneEjb.getPigVersRowBean(idVers);
            getForm().getVersatoreTrasf().getDs_vers_trasf().setValue(vers.getDsVers());
        } else {
            getForm().getVersatoreTrasf().getDs_vers_trasf().setValue(null);
        }
        return getForm().getVersatoreTrasf().asJSON();
    }

    private void aggiornaDettaglioVersatore() throws EMFError {
        BigDecimal idVers = ((BaseRowInterface) getForm().getVersList().getTable().getCurrentRow())
                .getBigDecimal("id_vers");
        aggiornaDettaglioVersatore(idVers);
    }

    private void aggiornaDettaglioVersatore(BigDecimal idVers) throws EMFError {
        getForm().getVers().setViewMode();
        getForm().getVersTab().setCurrentTab(getForm().getVersTab().getPigTipoObject());
        getForm().getIdList().getId_vers().setValue(idVers.toString());
        PigVersRowBean versRowBean = amministrazioneEjb.getPigVersRowBean(idVers);
        PigAmbienteVersRowBean ambienteRowBean = amministrazioneEjb.getPigAmbienteVersRowBeanFromVers(idVers);
        populateComboVers();
        getForm().getVers().copyFromBean(versRowBean);
        // Carico la corrispondenza sacer
        PigDichVersSacerRowBean dichVersRowBean = amministrazioneEjb.getPigDichVersSacerFromVers(idVers);
        getForm().getVers().getTi_dich_vers().setValue(dichVersRowBean.getTiDichVers());
        if (StringUtils.isNoneBlank(dichVersRowBean.getTiDichVers())) {
            try {
                getForm().getVers().getId_organiz_iam()
                        .setDecodeMap(getMappaDlCompositoOrganiz(dichVersRowBean.getTiDichVers()));
                getForm().getVers().getId_organiz_iam().setValue(dichVersRowBean.getIdOrganizIam().toString());
            } catch (Exception e) {
                getForm().getVers().asJSON(e.getMessage());
            }
        } else {
            getForm().getVers().getId_organiz_iam().setDecodeMap(new DecodeMap());
        }
        getForm().getVers().getAssociaSopClassButton().setEditMode();
        getForm().getVersatoreCustomMessageButtonList().setViewMode();
        getForm().getVers().setStatus(Status.view);
        getForm().getVersList().setStatus(Status.view);

        // Precedenti appartenenze ad ambienti
        PigStoricoVersAmbienteTableBean storicoVersAmbienteTableBean = amministrazioneEjb
                .getPigStoricoVersAmbienteTableBean(idVers);
        getForm().getPrecedentiAppartenenzeAmbientiList().setTable(storicoVersAmbienteTableBean);
        getForm().getPrecedentiAppartenenzeAmbientiList().getTable().sort();

        // Ricostruisco la tipologia
        if (versRowBean.getIdEnteConvenz() != null) {
            getForm().getVers().getTipologia().setValue("PRODUTTORE");
            getForm().getCorrispondenzaSacerSection().setHidden(false);
        } else {
            String tipologia = amministrazioneEjb.getTipologiaEnteNonConvenz(versRowBean.getIdEnteFornitEstern());
            getForm().getVers().getTipologia().setValue(tipologia);
            getForm().getCorrispondenzaSacerSection().setHidden(true);
        }

        // Enti Siam
        SIOrgEnteConvenzOrgTableBean enteConvenzOrgTableBean = amministrazioneEjb
                .getSIOrgEnteConvenzOrgTableBean(idVers);
        getForm().getEnteConvenzOrgList().setTable(enteConvenzOrgTableBean);
        getForm().getEnteConvenzOrgList().getTable().addSortingRule("dt_ini_val", SortingRule.DESC);
        getForm().getEnteConvenzOrgList().getTable().sort();
        //
        PigTipoObjectTableBean versTableBean = amministrazioneEjb.getPigTipoObjectTableBean(idVers);
        getForm().getTipoObjectList().setTable(versTableBean);
        getForm().getTipoObjectList().getTable().first();
        getForm().getTipoObjectList().setStatus(Status.view);
        getForm().getTipoObjectList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);

        mostraNascondiFlagArchivioRestituitoCessato();

        // MEV 30790 - verifico l'esistenza delle cartelle per il versatore
        getForm().getVers().getTi_stato_cartelle().setHidden(false);
        getForm().getVers().getTi_stato_cartelle().setValue("OK");
        String prefisso = configHelper.getValoreParamApplicByIdVers(it.eng.sacerasi.common.Constants.DS_PREFISSO_PATH,
                ambienteRowBean.getIdAmbienteVers(), versRowBean.getIdVers());
        File basePath = new File(configHelper.getValoreParamApplicByApplic(it.eng.xformer.common.Constants.ROOT_FTP)
                + File.separator + prefisso + versRowBean.getNmVers());

        File path = new File(basePath + "/INPUT_FOLDER/");
        if (!path.exists() || !path.isDirectory()) {
            getForm().getVers().getTi_stato_cartelle().setValue("KO");
        }

        path = new File(basePath + "/OUTPUT_FOLDER/");
        if (!path.exists() || !path.isDirectory()) {
            getForm().getVers().getTi_stato_cartelle().setValue("KO");
        }

        path = new File(basePath + "/TRASFORMATI/");
        if (!path.exists() || !path.isDirectory()) {
            getForm().getVers().getTi_stato_cartelle().setValue("KO");
        }

        postLoad();

        try {
            loadListeParametriVersatore(versRowBean.getIdAmbienteVers(), idVers, null, false, false, false, false,
                    true);
        } catch (ParerUserError ex) {
            getMessageBox().addError(ex.getMessage());
        }
    }

    private void aggiornaDettaglioTipoOggetto() throws EMFError {

        getForm().getTipoFileObject().setViewMode();

        if (getForm().getTipoObject().getTi_vers_file().parse()
                .equals(it.eng.sacerasi.common.Constants.TipoVersamento.ZIP_CON_XML_SACER.name())
                || getForm().getTipoObject().getTi_vers_file().parse()
                        .equals(it.eng.sacerasi.common.Constants.TipoVersamento.DA_TRASFORMARE.name())) {
            hideFieldsTipoObj(true);
        } else {
            hideFieldsTipoObj(false);
        }

        PigTipoObjectRowBean tipoObject = (PigTipoObjectRowBean) getForm().getTipoObjectList().getTable()
                .getCurrentRow();
        if (tipoObject != null && tipoObject.getIdTipoObject() != null) {
            // Aggiorno l'idTipoObject e idTipoFileObject in IdList
            getForm().getIdList().getId_tipo_object().setValue(tipoObject.getIdTipoObject().toString());
            getForm().getIdList().getId_tipo_file_object().clear();
            // Ricarico la lista Tipo File Object associata al Tipo Object (metodo "alla vecchia",
            // come veniva fatto una volta passando al metodo il rowBean
            PigTipoFileObjectRowBean tipoFileObjectRowBean = new PigTipoFileObjectRowBean();
            tipoFileObjectRowBean.setIdTipoObject(tipoObject.getIdTipoObject());
            PigTipoFileObjectTableBean tipoFileObjTableBean = amministrazioneEjb
                    .getPigTipoFileObjectTableBean(tipoObject.getIdTipoObject());
            getForm().getTipoFileObjectList().setTable(tipoFileObjTableBean);
            getForm().getTipoFileObjectList().getTable().first();
            getForm().getTipoFileObjectList().setStatus(Status.view);
            getForm().getTipoFileObjectList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
            // Ricarico la lista Xsd Dati Spec associata al Tipo Object (metodo "alla vecchia",
            // come veniva fatto una volta passando al metodo il rowBean
            PigXsdDatiSpecTableBean xsdDatiSpecTableBean = amministrazioneEjb
                    .getPigXsdDatiSpecTableBean(tipoObject.getIdTipoObject(), null);
            getForm().getXsdDatiSpecList().setTable(xsdDatiSpecTableBean);
            getForm().getXsdDatiSpecList().getTable().first();
            getForm().getXsdDatiSpecList().setStatus(Status.view);
            getForm().getXsdDatiSpecList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
            String tiVersFile = getForm().getTipoObject().getTi_vers_file().parse();
            // Corrispondenze in Sacer per tipologia oggetto
            if (tiVersFile.equals(it.eng.sacerasi.common.Constants.TipoVersamento.NO_ZIP.name())
                    || tiVersFile.equals(it.eng.sacerasi.common.Constants.TipoVersamento.ZIP_CON_XML_SACER.name())
                    || tiVersFile.equals(it.eng.sacerasi.common.Constants.TipoVersamento.ZIP_NO_XML_SACER.name())) {
                getForm().getTipoObject().getTi_dich_vers().setDecodeMap(ComboGetter.getMappaTiDichVers());
                PigDichVersSacerTipoObjRowBean dichVersSacerTipoObjRowBean = amministrazioneEjb
                        .getPigDichVersSacerTipoObjFromIdTipoObj(tipoObject.getIdTipoObject());
                getForm().getTipoObject().getTi_dich_vers().setValue(dichVersSacerTipoObjRowBean.getTiDichVers());
                if (StringUtils.isNoneBlank(dichVersSacerTipoObjRowBean.getTiDichVers())) {
                    try {
                        getForm().getTipoObject().getId_organiz_iam()
                                .setDecodeMap(getMappaDlCompositoOrganiz(dichVersSacerTipoObjRowBean.getTiDichVers()));
                        getForm().getTipoObject().getId_organiz_iam()
                                .setValue(dichVersSacerTipoObjRowBean.getIdOrganizIam().toString());
                    } catch (Exception e) {
                        getMessageBox().addError(e.getMessage());
                    }
                } else {
                    getForm().getTipoObject().getTi_dich_vers().setViewMode();
                    getForm().getTipoObject().getId_organiz_iam().setViewMode();
                    getForm().getTipoObject().getTi_dich_vers().setDecodeMap(new DecodeMap());
                    getForm().getTipoObject().getId_organiz_iam().setDecodeMap(new DecodeMap());
                }
            } else {
                getForm().getTipoObject().getTi_dich_vers().setViewMode();
                getForm().getTipoObject().getId_organiz_iam().setViewMode();
                getForm().getTipoObject().getTi_dich_vers().setDecodeMap(new DecodeMap());
                getForm().getTipoObject().getId_organiz_iam().setDecodeMap(new DecodeMap());
            }

            // Versatori per cui si generano oggetti
            if (getForm().getTipoObject().getTi_vers_file().parse()
                    .equals(it.eng.sacerasi.common.Constants.TipoVersamento.DA_TRASFORMARE.name())
                    && tipoObject.getIdTrasf() != null) {
                getForm().getTipoObjTab().getVersatoriGenerazioneOggettiTab().setHidden(false);
                PigVersTipoObjectDaTrasfTableBean versTipoObjectDaTrasfTableBean = amministrazioneEjb
                        .getPigVersTipoObjectDaTrasfTableBean(tipoObject.getIdTipoObject());
                getForm().getVersatoriGenerazioneOggettiList().setTable(versTipoObjectDaTrasfTableBean);
                getForm().getVersatoriGenerazioneOggettiList().getTable().first();
                getForm().getVersatoriGenerazioneOggettiList().setStatus(Status.view);
                getForm().getVersatoriGenerazioneOggettiList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
            } else {
                getForm().getTipoObjTab().getVersatoriGenerazioneOggettiTab().setHidden(true);
            }
        }

        // Parametri
        PigAmbienteVersRowBean pigAmbienteVersByVers = amministrazioneEjb
                .getPigAmbienteVersByVers(tipoObject.getIdVers());
        try {
            loadListeParametriTipoOggetto(pigAmbienteVersByVers.getIdAmbienteVers(), tipoObject.getIdVers(),
                    tipoObject.getIdTipoObject(), null, false, false, false, false, true);
        } catch (ParerUserError ex) {
            getMessageBox().addError(ex.getMessage());
        }

    }

    @Override
    public JSONObject triggerTipoFileObjectFl_ver_firma_fmt_sacerOnTrigger() throws EMFError {
        TipoFileObject form = getForm().getTipoFileObject();
        form.post(getRequest());
        populateComboTipoFileObj();
        //
        if (WebConstants.DB_TRUE.equals(form.getFl_ver_firma_fmt_sacer().getValue())) {
            form.getNm_fmt_file_calc_sacer().setValue(null);
            form.getDs_fmt_rappr_calc_sacer().setValue(null);
            form.getDs_fmt_rappr_esteso_calc_sacer().setValue(null);
            form.getNm_fmt_file_calc_sacer().setHidden(true);
            form.getDs_fmt_rappr_calc_sacer().setHidden(true);
            form.getDs_fmt_rappr_esteso_calc_sacer().setHidden(true);
        } else {
            form.getNm_tipo_comp_doc_sacer().setHidden(false);
            form.getNm_fmt_file_calc_sacer().setHidden(false);
            form.getDs_fmt_rappr_calc_sacer().setHidden(false);
            form.getDs_fmt_rappr_esteso_calc_sacer().setHidden(false);
        }
        return form.asJSON();
    }

    @Secure(action = "Menu.AmministrazioneSistema.StatiVersamentoOggetto")
    public void statiVersamentoOggettoPage() {
        getUser().getMenu().reset();
        getUser().getMenu().select("Menu.AmministrazioneSistema.StatiVersamentoOggetto");
        getForm().getStatiVersamentoObjectList().setTable(amministrazioneEjb.getPigStatoObjectTableBean());
        getForm().getStatiVersamentoObjectList().getTable().first();
        getForm().getStatiVersamentoObjectList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
        forwardToPublisher(Application.Publisher.LISTA_STATI_VERSAMENTO_OGGETTO);
    }

    @Override
    public void updateStatiVersamentoObjectList() throws EMFError {
        getForm().getStatoVersamentoObjectDetail().getDs_ti_stato_object().setEditMode();
        getForm().getStatoVersamentoObjectDetail().setStatus(Status.update);
        getForm().getStatiVersamentoObjectList().setStatus(Status.update);
        forwardToPublisher(Application.Publisher.DETTAGLIO_STATO_VERSAMENTO_OGGETTO);
    }

    private void salvaVersatoreGenerazioneOggetti() throws EMFError {
        if (getForm().getVersatoreGenerazioneOggettiDetail().postAndValidate(getRequest(), getMessageBox())) {
            BigDecimal idTipoObjectGen = getForm().getVersatoreGenerazioneOggettiDetail().getId_tipo_object_gen()
                    .parse();
            String nmVersGen = getForm().getVersatoreGenerazioneOggettiDetail().getId_vers_gen().getDecodedValue();
            String nmTipoObjectGen = getForm().getVersatoreGenerazioneOggettiDetail().getId_tipo_object_gen()
                    .getDecodedValue();
            String cdVersGen = getForm().getVersatoreGenerazioneOggettiDetail().getCd_vers_gen().parse();

            try {
                /*
                 * Codice aggiuntivo per il logging...
                 */
                LogParam param = SpagoliteLogUtil.getLogParam(
                        paramApplicHelper.getApplicationName().getDsValoreParamApplic(), getUser().getUsername(),
                        SpagoliteLogUtil.getPageName(this));
                param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());

                if (getForm().getVersatoriGenerazioneOggettiList().getStatus().equals(Status.insert)) {
                    param.setNomeAzione(SpagoliteLogUtil.getToolbarInsert());
                    BigDecimal idTipoObjectDaTrasf = getForm().getVersatoreGenerazioneOggettiDetail()
                            .getId_tipo_object_da_trasf().parse();
                    BigDecimal idVersGen = getForm().getVersatoreGenerazioneOggettiDetail().getId_vers_gen().parse();
                    Long idVersTipoObjectDaTrasf = amministrazioneEjb.saveVersatoreGenerazioneOggetti(param,
                            idTipoObjectDaTrasf, idVersGen, idTipoObjectGen, cdVersGen);
                    getForm().getVersatoreGenerazioneOggettiDetail().getId_vers_tipo_object_da_trasf()
                            .setValue(idVersTipoObjectDaTrasf.toString());
                    PigVersTipoObjectDaTrasfRowBean row = new PigVersTipoObjectDaTrasfRowBean();
                    row.setIdVersTipoObjectDaTrasf(new BigDecimal(idVersTipoObjectDaTrasf));
                    row.setCdVersGen(cdVersGen);
                    row.setString("nm_vers_gen", nmVersGen);
                    row.setString("nm_tipo_object_gen", nmTipoObjectGen);
                    getForm().getVersatoriGenerazioneOggettiList().getTable().last();
                    getForm().getVersatoriGenerazioneOggettiList().getTable().add(row);
                    getMessageBox().addInfo("Versatore per cui si generano oggetti inserito con successo");
                } else if (getForm().getVersatoriGenerazioneOggettiList().getStatus().equals(Status.update)) {
                    param.setNomeAzione(SpagoliteLogUtil.getToolbarUpdate());
                    BigDecimal idVersTipoObjectDaTrasf = getForm().getVersatoreGenerazioneOggettiDetail()
                            .getId_vers_tipo_object_da_trasf().parse();
                    BigDecimal idTipoObjectDaTrasf = getForm().getVersatoreGenerazioneOggettiDetail()
                            .getId_tipo_object_da_trasf().parse();
                    amministrazioneEjb.saveVersatoreGenerazioneOggetti(param, idVersTipoObjectDaTrasf, idTipoObjectGen,
                            cdVersGen, idTipoObjectDaTrasf);
                    getMessageBox().addInfo("Versatore per cui si generano oggetti modificato con successo");
                }
                // Carico il set di parametri
                loadSetParametriVersatore(
                        getForm().getVersatoreGenerazioneOggettiDetail().getId_vers_tipo_object_da_trasf().parse());
                getForm().getVersatoreGenerazioneOggettiDetail().setViewMode();
                getForm().getVersatoriGenerazioneOggettiList().setStatus(Status.view);
                getForm().getVersatoreGenerazioneOggettiDetail().setStatus(Status.view);
                getMessageBox().setViewMode(ViewMode.plain);
            } catch (ParerUserError ex) {
                getMessageBox().addError(ex.getDescription());
            }
        }
        forwardToPublisher(Application.Publisher.DETTAGLIO_VERSATORE_GENERAZIONE_OGGETTI);
    }

    @Override
    public JSONObject triggerVisVersNm_ambiente_sacerOnTrigger() throws EMFError {
        getForm().getVisVers().post(getRequest());
        BigDecimal idAmbiente = getForm().getVisVers().getNm_ambiente_sacer().parse();
        try {
            if (idAmbiente != null) {
                UsrVAbilStrutSacerXpingTableBean entiTb = amministrazioneEjb.getNmEntiSacer(getUser().getIdUtente(),
                        idAmbiente);
                getForm().getVisVers().getNm_ente_sacer()
                        .setDecodeMap(DecodeMap.Factory.newInstance(entiTb, "id_ente", "nm_ente"));
            } else {
                getForm().getVisVers().getNm_ente_sacer().setDecodeMap(new DecodeMap());
            }
            getForm().getVisVers().getNm_strut_sacer().setDecodeMap(new DecodeMap());
        } catch (ParerUserError ex) {
            getMessageBox().addError(ex.getDescription());
        }
        return getForm().getVisVers().asJSON();
    }

    @Override
    public JSONObject triggerVisVersNm_ente_sacerOnTrigger() throws EMFError {
        getForm().getVisVers().post(getRequest());
        BigDecimal idEnte = getForm().getVisVers().getNm_ente_sacer().parse();
        try {
            if (idEnte != null) {
                UsrVAbilStrutSacerXpingTableBean strutTb = amministrazioneEjb.getNmStrutSacer2(getUser().getIdUtente(),
                        idEnte);
                getForm().getVisVers().getNm_strut_sacer()
                        .setDecodeMap(DecodeMap.Factory.newInstance(strutTb, "id_strut", "nm_strut"));
            } else {
                getForm().getVisVers().getNm_strut_sacer().setDecodeMap(new DecodeMap());
            }
        } catch (ParerUserError ex) {
            getMessageBox().addError(ex.getDescription());
        }
        return getForm().getVisVers().asJSON();
    }

    @Override
    public void logEventi() throws EMFError {
        GestioneLogEventiForm form = new GestioneLogEventiForm();
        form.getOggettoDetail().getNmApp().setValue(paramApplicHelper.getApplicationName().getDsValoreParamApplic());
        form.getOggettoDetail().getNm_tipo_oggetto().setValue(SacerLogConstants.TIPO_OGGETTO_VERSATORE);
        BigDecimal idVers = ((BaseRowInterface) getForm().getVersList().getTable().getCurrentRow())
                .getBigDecimal("id_vers");
        form.getOggettoDetail().getIdOggetto().setValue(idVers.toPlainString());
        redirectToAction(it.eng.parer.sacerlog.slite.gen.Application.Actions.GESTIONE_LOG_EVENTI,
                "?operation=inizializzaLogEventi", form);
    }

    @Override
    public void logEventiTipoOggetto() throws EMFError {
        GestioneLogEventiForm form = new GestioneLogEventiForm();
        form.getOggettoDetail().getNmApp().setValue(paramApplicHelper.getApplicationName().getDsValoreParamApplic());
        form.getOggettoDetail().getNm_tipo_oggetto().setValue(SacerLogConstants.TIPO_OGGETTO_TIPO_OGGETTO_VERSABILE);
        PigTipoObjectRowBean riga = (PigTipoObjectRowBean) getForm().getTipoObjectList().getTable().getCurrentRow();
        form.getOggettoDetail().getIdOggetto().setValue(riga.getIdTipoObject().toPlainString());
        redirectToAction(it.eng.parer.sacerlog.slite.gen.Application.Actions.GESTIONE_LOG_EVENTI,
                "?operation=inizializzaLogEventi", form);
    }

    @Override
    public void logEventiAmbiente() throws EMFError {
        GestioneLogEventiForm form = new GestioneLogEventiForm();
        form.getOggettoDetail().getNmApp().setValue(paramApplicHelper.getApplicationName().getDsValoreParamApplic());
        form.getOggettoDetail().getNm_tipo_oggetto().setValue(SacerLogConstants.TIPO_OGGETTO_AMBIENTE_VERSATORE);
        PigAmbienteVersRowBean riga = (PigAmbienteVersRowBean) getForm().getAmbienteVersList().getTable()
                .getCurrentRow();
        form.getOggettoDetail().getIdOggetto().setValue(riga.getIdAmbienteVers().toPlainString());
        redirectToAction(it.eng.parer.sacerlog.slite.gen.Application.Actions.GESTIONE_LOG_EVENTI,
                "?operation=inizializzaLogEventi", form);
    }

    @Override
    protected void postLoad() {
        super.postLoad();
        Object ogg = getForm();
        if (ogg instanceof AmministrazioneForm) {
            if (getForm().getVersList().getStatus().equals(Status.view)) {
                getForm().getVers().getLogEventi().setEditMode();
                getForm().getVers().getEsportaVersatore().setEditMode();
            } else {
                getForm().getVers().getLogEventi().setViewMode();
                getForm().getVers().getEsportaVersatore().setViewMode();
            }
            if (getForm().getTipoObjectList().getStatus().equals(Status.view)) {
                getForm().getTipoObject().getLogEventiTipoOggetto().setEditMode();
            } else {
                getForm().getTipoObject().getLogEventiTipoOggetto().setViewMode();
            }
            if (getForm().getAmbienteVersList().getStatus().equals(Status.view)) {
                getForm().getAmbienteVers().getLogEventiAmbiente().setEditMode();
            } else {
                getForm().getAmbienteVers().getLogEventiAmbiente().setViewMode();
            }
            getForm().getVisVers().getImportaVersatoreButton().setEditMode();
            getForm().getVers().getImportaVersatore().setEditMode();
            getForm().getVers().getDuplicaVersatore().setEditMode();
        }
    }

    private DecodeMap getMappaDlCompositoOrganiz(String tiDichVers) throws ParerUserError {
        UsrVAbilStrutSacerXpingTableBean tb = new UsrVAbilStrutSacerXpingTableBean();
        String idType = null;
        switch (tiDichVers) {
        case "AMBIENTE":
            tb = amministrazioneEjb.getDlCompositoOrganizAmbienti(getUser().getIdUtente());
            idType = "id_organiz_iam_ambiente";
            break;
        case "ENTE":
            tb = amministrazioneEjb.getDlCompositoOrganizEnti(getUser().getIdUtente());
            idType = "id_organiz_iam_ente";
            break;
        case "STRUTTURA":
            tb = amministrazioneEjb.getDlCompositoOrganizStrutture(getUser().getIdUtente());
            idType = "id_organiz_iam_strut";
            break;
        }
        DecodeMap mappa = new DecodeMap();
        mappa.populatedMap(tb, idType, "dl_composito_organiz");
        return mappa;
    }

    @Override
    public void tabVersatoriGenerazioneOggettiTabOnClick() throws EMFError {
        getForm().getTipoObjTab().setCurrentTab(getForm().getTipoObjTab().getVersatoriGenerazioneOggettiTab());
        forwardToPublisher(Application.Publisher.TIPO_OBJECT_DETAIL);
    }

    @Override
    public void updateVersatoriGenerazioneOggettiList() {
        getForm().getVersatoreGenerazioneOggettiDetail().getId_tipo_object_gen().setEditMode();
        getForm().getVersatoreGenerazioneOggettiDetail().getCd_vers_gen().setEditMode();
        getForm().getVersatoreGenerazioneOggettiDetail().setStatus(Status.update);
        getForm().getVersatoriGenerazioneOggettiList().setStatus(Status.update);
    }

    @Override
    public void updateValoreParametriVersatoreList() throws EMFError {
        try {
            // Ricavo il valore da modificare e me li salvo in una mappa in sessione
            Map<String, String> mappaValoreParametroDaModificare = new HashMap<>();
            BigDecimal idValoreParamTrasf = ((PigVValParamTrasfDefSpecTableBean) getForm()
                    .getValoreParametriVersatoreList().getTable()).getCurrentRow().getIdValoreParamTrasf();
            BigDecimal idParamTrasf = ((PigVValParamTrasfDefSpecTableBean) getForm().getValoreParametriVersatoreList()
                    .getTable()).getCurrentRow().getIdParamTrasf();
            BigDecimal idSetParamTrasf = ((PigVValParamTrasfDefSpecTableBean) getForm()
                    .getValoreParametriVersatoreList().getTable()).getCurrentRow().getIdSetParamTrasf();
            BigDecimal idVersTipoObjectDaTrasf = ((PigVValParamTrasfDefSpecTableBean) getForm()
                    .getValoreParametriVersatoreList().getTable()).getCurrentRow().getIdVersTipoObjectDaTrasf();
            // Il valore di idValoreSetParamTrasf lo prendo dalla lista precedente in quanto, se esiste, è il padre di
            // tutti i valori
            PigValoreSetParamTrasfRowBean rb = amministrazioneEjb.getPigValoreSetParamTrasfRowBean(idSetParamTrasf,
                    idVersTipoObjectDaTrasf);
            BigDecimal idValoreSetParamTrasf = rb != null ? rb.getIdValoreSetParamTrasf() : null;

            String nmParamTrasf = ((PigVValParamTrasfDefSpecTableBean) getForm().getValoreParametriVersatoreList()
                    .getTable()).getCurrentRow().getNmParamTrasf();
            String tiParamTrasf = ((PigVValParamTrasfDefSpecTableBean) getForm().getValoreParametriVersatoreList()
                    .getTable()).getCurrentRow().getTiParamTrasf();
            String dsValoreParam = ((PigVValParamTrasfDefSpecTableBean) getForm().getValoreParametriVersatoreList()
                    .getTable()).getCurrentRow().getValParam();
            mappaValoreParametroDaModificare.put("idValoreParamTrasf",
                    idValoreParamTrasf != null ? idValoreParamTrasf.toPlainString() : null);
            mappaValoreParametroDaModificare.put("idParamTrasf", idParamTrasf.toPlainString());
            mappaValoreParametroDaModificare.put("nmParamTrasf", nmParamTrasf);
            mappaValoreParametroDaModificare.put("tiParamTrasf", tiParamTrasf);
            mappaValoreParametroDaModificare.put("dsValoreParam", dsValoreParam);
            mappaValoreParametroDaModificare.put("idSetParamTrasf", idSetParamTrasf.toPlainString());
            mappaValoreParametroDaModificare.put("idValoreSetParamTrasf",
                    idValoreSetParamTrasf != null ? idValoreSetParamTrasf.toPlainString() : null);
            getSession().setAttribute("mappaValoreParametroDaModificare", mappaValoreParametroDaModificare);

            // Inserisco i campi del valore nei campi in preparazione della popup
            getForm().getValoreParametroVersatoreDetail().getNm_param_trasf().setValue(nmParamTrasf);
            getForm().getValoreParametroVersatoreDetail().getDs_valore_param().setValue(dsValoreParam);
            getForm().getValoreParametroVersatoreDetail().getDs_valore_param().setEditMode();

            // Ricarico la pagina con la popup
            getSession().setAttribute("customBoxUpdateParametroVersatore", true);

        } catch (ParerUserError ex) {
            getMessageBox().addError(ex.getMessage());
        }

        forwardToPublisher(Application.Publisher.DETTAGLIO_SET_PARAMETRI_VERSATORE);
    }

    @Override
    public void eliminaSetParametriVersatore() throws EMFError {
        // Se esiste il record in PIG_VALORE_SET_PARAM_TRASF, lo cancello
        BigDecimal idValoreSetParamTrasf = null;
        for (PigVValParamTrasfDefSpecRowBean riga : (PigVValParamTrasfDefSpecTableBean) getForm()
                .getValoreParametriVersatoreList().getTable()) {
            if (riga.getIdValoreSetParamTrasf() != null) {
                idValoreSetParamTrasf = riga.getIdValoreSetParamTrasf();
                break;
            }
        }

        BigDecimal idVersTipoObjectDaTrasf = ((PigVValoreSetParamTrasfRowBean) getForm().getSetParametriVersatoreList()
                .getTable().getCurrentRow()).getIdVersTipoObjectDaTrasf();
        BigDecimal idSetParamTrasf = ((PigVValParamTrasfDefSpecRowBean) getForm().getValoreParametriVersatoreList()
                .getTable().getCurrentRow()).getIdSetParamTrasf();
        try {
            if (idValoreSetParamTrasf != null) {
                /*
                 * Codice aggiuntivo per il logging...
                 */
                LogParam param = SpagoliteLogUtil.getLogParam(
                        paramApplicHelper.getApplicationName().getDsValoreParamApplic(), getUser().getUsername(),
                        SpagoliteLogUtil.getPageName(this));
                param.setNomeAzione(SpagoliteLogUtil.getButtonActionName(getForm(),
                        getForm().getSetParametriVersatoreDetail(),
                        getForm().getSetParametriVersatoreDetail().getEliminaSetParametriVersatore().getName()));
                param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
                amministrazioneEjb.deletePigValoreSetParamTrasf(param, idValoreSetParamTrasf);
                loadValoriParametriVersatore(idSetParamTrasf, idVersTipoObjectDaTrasf);
            }
            getMessageBox().addInfo("Set parametri per versatore resettato con successo!");
            forwardToPublisher(getLastPublisher());
        } catch (Exception ex) {
            getMessageBox().addError("Errore durante il reset del set parametri per versatore");
        }
    }

    @Secure(action = "detail/AmministrazioneForm#ValoreParametriVersatoreList/eliminaValoreParametroVersatore")
    public void eliminaValoreParametroVersatore() {
        BigDecimal idSetParamTrasf = ((PigVValParamTrasfDefSpecRowBean) getForm().getValoreParametriVersatoreList()
                .getTable().getCurrentRow()).getIdSetParamTrasf();
        BigDecimal idVersTipoObjectDaTrasf = ((PigVValoreSetParamTrasfRowBean) getForm().getSetParametriVersatoreList()
                .getTable().getCurrentRow()).getIdVersTipoObjectDaTrasf();

        String riga = getRequest().getParameter("riga");
        Integer nr = Integer.parseInt(riga);
        BigDecimal idValoreParamTrasf = ((PigVValParamTrasfDefSpecTableBean) (getForm()
                .getValoreParametriVersatoreList().getTable())).getRow(nr).getIdValoreParamTrasf();

        try {
            if (idValoreParamTrasf != null) {
                /*
                 * Codice aggiuntivo per il logging...
                 */
                LogParam param = SpagoliteLogUtil.getLogParam(
                        paramApplicHelper.getApplicationName().getDsValoreParamApplic(), getUser().getUsername(),
                        SpagoliteLogUtil.getPageName(this));
                param.setNomeAzione(SpagoliteLogUtil.getDetailActionName(getForm(),
                        getForm().getValoreParametriVersatoreList(),
                        getForm().getValoreParametriVersatoreList().getEliminaValoreParametroVersatore().getName()));
                param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());

                amministrazioneEjb.deletePigValoreParamTrasf(param, idValoreParamTrasf);
                // Valore parametri
                loadValoriParametriVersatore(idSetParamTrasf, idVersTipoObjectDaTrasf);
            }
            getMessageBox().addInfo("Valore parametro per versatore resettato con successo!");
            forwardToPublisher(getLastPublisher());
        } catch (Exception ex) {
            getMessageBox().addError("Errore durante il reset del valore parametro per versatore");
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void confermaModificaValoreParametroVersatore() throws EMFError {
        String dsValoreParamMod = getRequest().getParameter("dsValoreParamMod");
        Map<String, String> mappaValoreParametroDaModificare = (Map<String, String>) getSession()
                .getAttribute("mappaValoreParametroDaModificare");
        BigDecimal idVersTipoObjectDaTrasf = getForm().getVersatoreGenerazioneOggettiDetail()
                .getId_vers_tipo_object_da_trasf().parse();

        // Controllo che il valore fornito sia coerente con il tipo di parametro
        String tiParamTrasf = mappaValoreParametroDaModificare.get("tiParamTrasf");
        String idValoreParamTrasf = mappaValoreParametroDaModificare.get("idValoreParamTrasf");
        String idParamTrasf = mappaValoreParametroDaModificare.get("idParamTrasf");
        String idSetParamTrasf = mappaValoreParametroDaModificare.get("idSetParamTrasf");
        String idValoreSetParamTrasf = mappaValoreParametroDaModificare.get("idValoreSetParamTrasf");

        if (dsValoreParamMod != null) {
            checkCoerenzaParametro(tiParamTrasf, dsValoreParamMod);
        }

        // Se non ci sono errori formali, procedo al salvataggio
        if (!getMessageBox().hasError()) {
            try {
                /*
                 * Codice aggiuntivo per il logging...
                 */
                LogParam param = SpagoliteLogUtil.getLogParam(
                        paramApplicHelper.getApplicationName().getDsValoreParamApplic(), getUser().getUsername(),
                        SpagoliteLogUtil.getPageName(this));
                param.setNomeAzione(SpagoliteLogUtil.getButtonActionName(getForm(),
                        getForm().getValoreParametroVersatoreDetail(), getForm().getValoreParametroVersatoreDetail()
                                .getConfermaModificaValoreParametroVersatore().getName()));
                param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());

                amministrazioneEjb.saveValoreParametroVersatore(param, idVersTipoObjectDaTrasf,
                        new BigDecimal(idSetParamTrasf), new BigDecimal(idParamTrasf),
                        idValoreSetParamTrasf != null ? new BigDecimal(idValoreSetParamTrasf) : null,
                        idValoreParamTrasf != null ? new BigDecimal(idValoreParamTrasf) : null, dsValoreParamMod);
                // Cancello l'attributo in sessione
                getSession().removeAttribute("mappaValoreParametroDaModificare");
                getSession().removeAttribute("customBoxUpdateParametroVersatore");
                getMessageBox().addInfo("Valore parametro modificato con successo!");
                loadValoriParametriVersatore(new BigDecimal(idSetParamTrasf), idVersTipoObjectDaTrasf);
            } catch (ParerUserError ex) {
                getMessageBox().addError(ex.getDescription());
            }
        }
        getMessageBox().setViewMode(ViewMode.alert);
        forwardToPublisher(getLastPublisher());
    }

    @Override
    public void annullaModificaValoreParametroVersatore() throws EMFError {
        // Cancello l'attributo in sessione
        getSession().removeAttribute("mappaValoreParametroDaModificare");
        getSession().removeAttribute("customBoxUpdateParametroVersatore");
        forwardToPublisher(getLastPublisher());
    }

    /**
     * Validazione necessaria in quanto i dsParamTrasf che ricevo sono tutte stringhe
     *
     * @param tiParamTrasf
     *            tipo parametro
     * @param dsParamTrasf
     *            descrizione parametro
     */
    private void checkCoerenzaParametro(String tiParamTrasf, String dsParamTrasf) {
        switch (tiParamTrasf) {
        case "DATA":
            String regex = "^(3[01]|[12][0-9]|0[1-9])/(1[0-2]|0[1-9])/[0-9]{4}$";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(dsParamTrasf);
            if (!matcher.matches()) {
                getMessageBox().addError(
                        "Attenzione: il campo di tipo DATA non è formalmente corretto (inserire una data nel formato dd/mm/yyyy) ");
            }
            break;
        case "FLAG":
            if (!dsParamTrasf.equals("S") && !dsParamTrasf.equals("N") && !dsParamTrasf.equals("")) {
                getMessageBox()
                        .addError("Attenzione: il campo di tipo FLAG non è formalmente corretto (inserire S o N)");
            }
            break;
        case "NUMERICO":
            try {
                Double.parseDouble(dsParamTrasf);
            } catch (NumberFormatException nfe) {
                getMessageBox().addError("Attenzione: il campo di tipo NUMERICO non è formalmente corretto");
            }
            break;
        }
    }

    @Override
    public JSONObject triggerVersatoreGenerazioneOggettiDetailId_vers_genOnTrigger() throws EMFError {
        getForm().getVersatoreGenerazioneOggettiDetail().post(getRequest());
        BigDecimal idVers = getForm().getVersatoreGenerazioneOggettiDetail().getId_vers_gen().parse();
        if (idVers != null) {
            getForm().getVersatoreGenerazioneOggettiDetail().getId_tipo_object_gen()
                    .setDecodeMap(DecodeMap.Factory.newInstance(amministrazioneEjb
                            .getPigTipoObjectNoDaTrasfAbilitatiTableBean(idVers, getUser().getIdUtente()),
                            "id_tipo_object", "nm_tipo_object"));
        } else {
            getForm().getVersatoreGenerazioneOggettiDetail().getId_tipo_object_gen().setDecodeMap(new DecodeMap());
        }
        return getForm().getVersatoreGenerazioneOggettiDetail().asJSON();
    }

    @Override
    public void importaVersatoreButton() throws EMFError {
        populateComboVers();
        getForm().getVers().setStatus(Status.insert);
        getForm().getVers().setEditMode();
        getForm().getVersList().setStatus(Status.insert);
        getForm().getVers().clear();
        setDateStandard();
        getSession().setAttribute("duplicaVersDetail", "importa");
        if (getForm().getVers().getTipologia().parse().equals("PRODUTTORE")) {
            getForm().getCorrispondenzaSacerSection().setHidden(false);
        } else {
            getForm().getCorrispondenzaSacerSection().setHidden(true);
        }

        mostraNascondiFlagArchivioRestituitoCessato();
        forwardToPublisher(Application.Publisher.DUPLICA_VERS_DETAIL);
    }

    @Override
    public void esportaVersatore() throws EMFError {
        File tmpFile = null;
        FileOutputStream out = null;
        try {
            BigDecimal id = getForm().getVersList().getTable().getCurrentRow().getBigDecimal("id_vers");
            String nomeVersatore = getForm().getVersList().getTable().getCurrentRow().getString("nm_vers");
            String nomeFile = "versatore_" + nomeVersatore + ".xml";
            String foto = amministrazioneEjb.esportaVersatore(id);
            if (foto == null) {
                getMessageBox().addError("Non è stato estratto alcun versatore<br/>");
            } else {
                tmpFile = new File(System.getProperty("java.io.tmpdir"), nomeFile);
                out = new FileOutputStream(tmpFile);
                IOUtils.write(foto, out, StandardCharsets.UTF_8);
                getRequest().setAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_ACTION.name(), getControllerName());
                getSession().setAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_FILENAME.name(), tmpFile.getName());
                getSession().setAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_FILEPATH.name(), tmpFile.getPath());
                getSession().setAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_DELETEFILE.name(),
                        Boolean.toString(true));
                getSession().setAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_CONTENTTYPE.name(),
                        WebConstants.MIME_TYPE_GENERIC);
            }
        } catch (Exception ex) {
            log.error("Errore in download " + ExceptionUtils.getRootCauseMessage(ex), ex);
            getMessageBox().addError("Errore inatteso nella preparazione del download<br/>");
        } finally {
            IOUtils.closeQuietly(out);
        }

        if (getMessageBox().hasError()) {
            forwardToPublisher(getLastPublisher());
        } else {
            forwardToPublisher(Application.Publisher.DOWNLOAD_PAGE);
        }

    }

    public void download() throws EMFError {
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
                getResponse().setHeader("Content-Disposition", "attachment; filename=\"" + filename);

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
                    fileToDownload.delete();
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

    private void gestisciImportXml() throws EMFError {
        try {
            int fileSize = ConfigSingleton.getInstance().getIntValue(IMPORT_VERSATORE_MAX_FILE_SIZE.name());
            getForm().getVers().postMultipart(getRequest(), fileSize);
        } catch (FileUploadException ex) {
            log.error(ex.getMessage(), ex);
            throw new EMFError(EMFError.BLOCKING, "Errore nell'upload del file XML", ex);
        }
        getMessageBox().clear();

        Vers vers = getForm().getVers();
        BigDecimal idAmb = vers.getId_ambiente_vers().parse();
        String nmAmbiente = vers.getId_ambiente_vers().getDecodedValue();
        String nmVers = vers.getNm_vers().parse();
        String dsVers = vers.getDs_vers().parse();
        String tipologia = vers.getTipologia().parse();
        BigDecimal idEnteConvenz = vers.getId_ente_convenz_ec().parse();
        BigDecimal idEnteFornitEstern = vers.getId_ente_convenz_fe().parse();
        String nmEnteConvenz = vers.getId_ente_convenz_ec().getDecodedValue();
        String nmEnteFornitEstern = vers.getId_ente_convenz_fe().getDecodedValue();
        Date dtIniValAppartEnteSiam = vers.getDt_ini_val_appart_ente_siam().parse();
        Date dtFineValAppartEnteSiam = vers.getDt_fine_val_appart_ente_siam().parse();
        Date dtIniValVers = vers.getDt_ini_val_vers().parse();
        Date dtFineValVers = vers.getDt_fine_val_vers().parse();
        Date dtIniValAppartAmbiente = vers.getDt_ini_val_appart_ambiente().parse();
        Date dtFinValAppartAmbiente = vers.getDt_fin_val_appart_ambiente().parse();
        String dsPathInputFtp = vers.getDs_path_input_ftp().parse();
        String dsPathOutputFtp = vers.getDs_path_output_ftp().parse();
        String dsPathTrasf = vers.getDs_path_trasf().parse();
        String tiDichVers = vers.getTi_dich_vers().parse();
        BigDecimal idOrganizIam = vers.getId_organiz_iam().parse();
        getForm().getParametriAmministrazioneVersatoreList().post(getRequest());
        getForm().getParametriConservazioneVersatoreList().post(getRequest());
        getForm().getParametriGestioneVersatoreList().post(getRequest());
        PigParamApplicTableBean parametriAmministrazione = (PigParamApplicTableBean) getForm()
                .getParametriAmministrazioneVersatoreList().getTable();
        PigParamApplicTableBean parametriConservazione = (PigParamApplicTableBean) getForm()
                .getParametriConservazioneVersatoreList().getTable();
        PigParamApplicTableBean parametriGestione = (PigParamApplicTableBean) getForm()
                .getParametriGestioneVersatoreList().getTable();
        byte[] fileBlob = getForm().getVers().getDs_file_xml_versatore().getFileBytes();
        if (fileBlob == null) {
            getMessageBox().addError("Errore di compilazione form: selezionare il file da importare<br/>");
        }
        if (validaPerSalvataggioVersatore(vers.getStatus(), idAmb, nmVers, dsVers, dtIniValVers, dtFineValVers,
                dsPathInputFtp, dsPathOutputFtp, dsPathTrasf, dtIniValAppartAmbiente, dtFinValAppartAmbiente, tipologia,
                "0", "0", idEnteConvenz, dtIniValAppartEnteSiam, dtFineValAppartEnteSiam, idEnteFornitEstern,
                tiDichVers, idOrganizIam, parametriAmministrazione, parametriConservazione, parametriGestione)) {
            String strXml = new String(fileBlob, StandardCharsets.UTF_8);
            try {
                /*
                 * Codice aggiuntivo per il logging...
                 */
                LogParam param = SpagoliteLogUtil.getLogParam(
                        paramApplicHelper.getApplicationName().getDsValoreParamApplic(), getUser().getUsername(),
                        SpagoliteLogUtil.getPageName(this));
                param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
                param.setNomeAzione(SpagoliteLogUtil.getButtonActionName(getForm(), getForm().getVers(),
                        getForm().getVers().getImportaVersatore().getName()));
                Object[] dati = amministrazioneEjb.importaVersatore(param, idAmb, strXml, nmAmbiente, nmVers, dsVers,
                        idEnteConvenz, nmEnteConvenz, idEnteFornitEstern, nmEnteFornitEstern, dtIniValAppartEnteSiam,
                        dtFineValAppartEnteSiam, dtIniValVers, dtFineValVers, dtIniValAppartAmbiente,
                        dtFinValAppartAmbiente, dsPathInputFtp, dsPathOutputFtp, dsPathTrasf, tiDichVers, idOrganizIam,
                        getUser().getIdUtente());

                /* Si prepara tutto per andare nel dettaglio del versatore appena inserito */
                BigDecimal idOggetto = (BigDecimal) dati[0];
                String msg = (String) dati[1];
                PigVRicVersRowBean versRowBean = new PigVRicVersRowBean();
                vers.copyToBean(versRowBean);
                PigVRicVersTableBean table = new PigVRicVersTableBean();
                versRowBean.setIdVers(idOggetto);
                table.add(versRowBean);
                table.setPageSize(1);
                table.setCurrentRowIndex(0);
                getForm().getVersList().setTable(table);
                aggiornaDettaglioVersatore(idOggetto);
                if (msg == null) {
                    getMessageBox().addInfo("Versatore '" + nmVers + "' importato con successo nell'ambiente '"
                            + nmAmbiente + "'<br/>");
                } else {
                    getMessageBox().addWarning("Versatore '" + nmVers + "' importato con successo nell'ambiente '"
                            + nmAmbiente + "'. " + msg);
                }
                setLastPublisher(Application.Publisher.CERCA_VERS);
                forwardToPublisher(Application.Publisher.VERS_DETAIL);
            } catch (ParerUserError e) {
                getMessageBox().addError(e.getDescription());
                forwardToPublisher(getLastPublisher());
            } catch (Exception ex) {
                String errore = "Errore nell'importazione del versatore '" + nmVers + "' nell'ambiente '" + nmAmbiente
                        + "'";
                log.error(errore, ex);
                getMessageBox().addError(errore + "<br/>");
                forwardToPublisher(getLastPublisher());
            }
        } else {
            forwardToPublisher(getLastPublisher());
        }
    }

    @Override
    public void importaVersatore() throws EMFError {
        // template
    }

    @Override
    public void duplicaVersatore() throws EMFError {
        salvaDuplicaVers();
    }

    @Override
    public void deleteEnteConvenzOrgList() throws EMFError {
        SIOrgEnteConvenzOrgRowBean row = (SIOrgEnteConvenzOrgRowBean) getForm().getEnteConvenzOrgList().getTable()
                .getCurrentRow();
        BigDecimal idEnteConvenzOrg = row.getIdEnteConvenzOrg();
        int riga = getForm().getEnteConvenzOrgList().getTable().getCurrentRowIndex();
        String error = EntiConvenzionatiAction
                .checkDeleteEnteConvenzOrg(getForm().getEnteConvenzOrgList().getTable().size());
        // Controllo che l'associazione non sia l'unica presente. In tal caso, l'eliminazione non è consentita
        if (error != null) {
            getMessageBox().addError(error);
        }

        /*
         * Codice aggiuntivo per il logging...
         */
        LogParam param = SpagoliteLogUtil.getLogParam(
                configurationHelper.getValoreParamApplicByApplic(Constants.NM_APPLIC), getUser().getUsername(),
                SpagoliteLogUtil.getPageName(this));
        param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
        param.setNomeAzione(SpagoliteLogUtil.getDetailActionNameDelete(getForm(), getForm().getEnteConvenzOrgList()));
        if (!getMessageBox().hasError() && idEnteConvenzOrg != null) {
            try {
                amministrazioneEjb.deleteEnteConvenzOrg(param, idEnteConvenzOrg);
                getForm().getEnteConvenzOrgList().getTable().remove(riga);

                getMessageBox().addInfo("Associazione all'ente siam eliminata con successo");
                getMessageBox().setViewMode(MessageBox.ViewMode.plain);
            } catch (ParerUserError ex) {
                getMessageBox().addError(ex.getDescription());
            }
        }
        goBack();
    }

    @Override
    public void updateEnteConvenzOrgList() throws EMFError {
        redirectToEnteConvenzPage(NE_DETTAGLIO_UPDATE);
    }

    @Override
    public void parametriAmministrazioneAmbienteButton() throws Throwable {
        BigDecimal idAmbienteVers = ((BaseRowInterface) getForm().getAmbienteVersList().getTable().getCurrentRow())
                .getBigDecimal("id_ambiente_vers");
        loadListeParametriAmbiente(idAmbienteVers, null, false, true, true, true, true);
        prepareRicercaParametriAmbiente(AMMINISTRAZIONE);
    }

    @Override
    public void parametriConservazioneAmbienteButton() throws Throwable {
        BigDecimal idAmbienteVers = ((BaseRowInterface) getForm().getAmbienteVersList().getTable().getCurrentRow())
                .getBigDecimal("id_ambiente_vers");
        loadListeParametriAmbiente(idAmbienteVers, null, false, false, true, true, true);
        prepareRicercaParametriAmbiente(CONSERVAZIONE);
    }

    @Override
    public void parametriGestioneAmbienteButton() throws Throwable {
        BigDecimal idAmbienteVers = ((BaseRowInterface) getForm().getAmbienteVersList().getTable().getCurrentRow())
                .getBigDecimal("id_ambiente_vers");
        loadListeParametriAmbiente(idAmbienteVers, null, false, false, false, true, true);
        prepareRicercaParametriAmbiente(GESTIONE);
    }

    private void prepareRicercaParametriAmbiente(String tipo) {
        getForm().getAmbienteVers().setStatus(Status.update);
        getForm().getRicercaParametriAmbiente().setEditMode();
        BaseTable tb = amministrazioneEjb.getFunzioneParametriTableBean();
        getForm().getRicercaParametriAmbiente().getFunzione().reset();
        getForm().getRicercaParametriAmbiente().getFunzione()
                .setDecodeMap(DecodeMap.Factory.newInstance(tb, "funzione", "funzione"));
        getSession().setAttribute("provenienzaParametri", tipo);
        forwardToPublisher(Application.Publisher.PARAMETRI_AMBIENTE_VERS);
    }

    @Override
    public void ricercaParametriAmbienteButton() throws EMFError {
        getForm().getRicercaParametriAmbiente().post(getRequest());
        List<String> funzione = getForm().getRicercaParametriAmbiente().getFunzione().parse();
        BigDecimal idAmbienteVers = ((BaseRowInterface) getForm().getAmbienteVersList().getTable().getCurrentRow())
                .getBigDecimal("id_ambiente_vers");
        try {
            getForm().getAmbienteVers().setStatus(Status.update);
            if (getSession().getAttribute("provenienzaParametri") != null) {
                String provenzienzaParametri = (String) getSession().getAttribute("provenienzaParametri");
                switch (provenzienzaParametri) {
                case AMMINISTRAZIONE:
                    loadListeParametriAmbiente(idAmbienteVers, funzione, false, true, true, true, true);
                    break;
                case CONSERVAZIONE:
                    loadListeParametriAmbiente(idAmbienteVers, funzione, false, false, true, true, true);
                    break;
                case GESTIONE:
                    loadListeParametriAmbiente(idAmbienteVers, funzione, false, false, false, true, true);
                    break;
                default:
                    break;
                }
            }
        } catch (ParerUserError ex) {
            getMessageBox().addError("Errore durante il caricamento dei parametri ambiente");
        }
        forwardToPublisher(Application.Publisher.PARAMETRI_AMBIENTE_VERS);
    }

    private void salvaParametriAmbiente() throws EMFError {
        getForm().getParametriAmministrazioneAmbienteList().post(getRequest());
        getForm().getParametriConservazioneAmbienteList().post(getRequest());
        getForm().getParametriGestioneAmbienteList().post(getRequest());

        BigDecimal idAmbienteVers = ((BaseRowInterface) getForm().getAmbienteVersList().getTable().getCurrentRow())
                .getBigDecimal("id_ambiente_vers");

        // Controllo valori possibili su ambiente
        PigParamApplicTableBean parametriAmministrazione = (PigParamApplicTableBean) getForm()
                .getParametriAmministrazioneAmbienteList().getTable();
        PigParamApplicTableBean parametriConservazione = (PigParamApplicTableBean) getForm()
                .getParametriConservazioneAmbienteList().getTable();
        PigParamApplicTableBean parametriGestione = (PigParamApplicTableBean) getForm()
                .getParametriGestioneAmbienteList().getTable();
        String error = amministrazioneEjb.checkParametriAmmessi("ambiente", parametriAmministrazione,
                parametriConservazione, parametriGestione);
        if (error != null) {
            getMessageBox().addError(error);
        }

        if (!getMessageBox().hasError()) {
            amministrazioneEjb.saveParametriAmbiente(parametriAmministrazione, parametriConservazione,
                    parametriGestione, idAmbienteVers);
            getMessageBox().addInfo("Parametri ambiente versatore salvati con successo");
            getMessageBox().setViewMode(ViewMode.plain);
            getForm().getAmbienteVers().setViewMode();
            getForm().getAmbienteVers().setStatus(Status.view);
            getForm().getParametriAmministrazioneAmbienteList().setViewMode();
            getForm().getParametriConservazioneAmbienteList().setViewMode();
            getForm().getParametriGestioneAmbienteList().setViewMode();
            try {
                loadAmbienteVers(idAmbienteVers);
                forwardToPublisher(Application.Publisher.AMBIENTE_VERS_DETAIL);
            } catch (ParerUserError e) {
                getMessageBox().addError(e.getDescription());
            }
        } else {
            forwardToPublisher(getLastPublisher());
        }
    }

    @Override
    public void parametriAmministrazioneVersatoreButton() throws Throwable {
        BigDecimal idAmbienteVers = getForm().getVers().getId_ambiente_vers().parse();
        BigDecimal idVers = getForm().getVersList().getTable().getCurrentRow().getBigDecimal("id_vers");
        loadListeParametriVersatore(idAmbienteVers, idVers, null, false, true, true, true, true);
        prepareRicercaParametriVersatore(AMMINISTRAZIONE);
    }

    @Override
    public void parametriConservazioneVersatoreButton() throws Throwable {
        BigDecimal idAmbienteVers = getForm().getVers().getId_ambiente_vers().parse();
        BigDecimal idVers = getForm().getVersList().getTable().getCurrentRow().getBigDecimal("id_vers");
        loadListeParametriVersatore(idAmbienteVers, idVers, null, false, false, true, true, true);
        prepareRicercaParametriVersatore(CONSERVAZIONE);
    }

    @Override
    public void parametriGestioneVersatoreButton() throws Throwable {
        BigDecimal idAmbienteVers = getForm().getVers().getId_ambiente_vers().parse();
        BigDecimal idVers = getForm().getVersList().getTable().getCurrentRow().getBigDecimal("id_vers");
        loadListeParametriVersatore(idAmbienteVers, idVers, null, false, false, false, true, true);
        prepareRicercaParametriVersatore(GESTIONE);
    }

    private void prepareRicercaParametriVersatore(String tipo) {
        getForm().getVers().setStatus(Status.update);
        getForm().getRicercaParametriVersatore().setEditMode();
        BaseTable tb = amministrazioneEjb.getFunzioneParametriTableBean();
        getForm().getRicercaParametriVersatore().getFunzione().reset();
        getForm().getRicercaParametriVersatore().getFunzione()
                .setDecodeMap(Factory.newInstance(tb, "funzione", "funzione"));
        getSession().setAttribute("provenienzaParametri", tipo);
        forwardToPublisher(Publisher.PARAMETRI_VERSATORE);
    }

    @Override
    public void ricercaParametriVersatoreButton() throws EMFError {
        getForm().getRicercaParametriVersatore().post(getRequest());
        List<String> funzione = getForm().getRicercaParametriVersatore().getFunzione().parse();
        BigDecimal idAmbienteVers = getForm().getVers().getId_ambiente_vers().parse();
        BigDecimal idVers = getForm().getVersList().getTable().getCurrentRow().getBigDecimal("id_vers");
        try {
            getForm().getVers().setStatus(Status.update);
            if (getSession().getAttribute("provenienzaParametri") != null) {
                String provenzienzaParametri = (String) getSession().getAttribute("provenienzaParametri");
                switch (provenzienzaParametri) {
                case AMMINISTRAZIONE:
                    loadListeParametriVersatore(idAmbienteVers, idVers, funzione, false, true, true, true, true);
                    break;
                case CONSERVAZIONE:
                    loadListeParametriVersatore(idAmbienteVers, idVers, funzione, false, false, true, true, true);
                    break;
                case GESTIONE:
                    loadListeParametriVersatore(idAmbienteVers, idVers, funzione, false, false, false, true, true);
                    break;
                default:
                    break;
                }
            }
        } catch (ParerUserError ex) {
            getMessageBox().addError("Errore durante il caricamento dei parametri versatore");
        }
        forwardToPublisher(Publisher.PARAMETRI_VERSATORE);
    }

    private void salvaParametriVersatore() throws EMFError {
        getForm().getParametriAmministrazioneVersatoreList().post(getRequest());
        getForm().getParametriConservazioneVersatoreList().post(getRequest());
        getForm().getParametriGestioneVersatoreList().post(getRequest());

        BigDecimal idVers = getForm().getVersList().getTable().getCurrentRow().getBigDecimal("id_vers");

        // Controllo valori possibili su ambiente
        PigParamApplicTableBean parametriAmministrazione = (PigParamApplicTableBean) getForm()
                .getParametriAmministrazioneVersatoreList().getTable();
        PigParamApplicTableBean parametriConservazione = (PigParamApplicTableBean) getForm()
                .getParametriConservazioneVersatoreList().getTable();
        PigParamApplicTableBean parametriGestione = (PigParamApplicTableBean) getForm()
                .getParametriGestioneVersatoreList().getTable();
        String error = amministrazioneEjb.checkParametriAmmessi("vers", parametriAmministrazione,
                parametriConservazione, parametriGestione);
        if (error != null) {
            getMessageBox().addError(error);
        }

        if (!getMessageBox().hasError()) {
            /*
             * Codice aggiuntivo per il logging...
             */
            LogParam param = SpagoliteLogUtil.getLogParam(
                    configHelper.getValoreParamApplicByApplic(Constants.NM_APPLIC), getUser().getUsername(),
                    SpagoliteLogUtil.getPageName(this),
                    SpagoliteLogUtil.getToolbarSave(getForm().getVers().getStatus().equals(Status.update)));
            param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
            amministrazioneEjb.saveParametriVersatore(param, parametriAmministrazione, parametriConservazione,
                    parametriGestione, idVers);
            getMessageBox().addInfo("Parametri versatore salvati con successo");
            getMessageBox().setViewMode(ViewMode.plain);
            getForm().getVers().setViewMode();
            getForm().getVers().setStatus(Status.view);
            getForm().getParametriAmministrazioneVersatoreList().setViewMode();
            getForm().getParametriConservazioneVersatoreList().setViewMode();
            getForm().getParametriGestioneVersatoreList().setViewMode();
            try {
                loadVers(idVers);
                forwardToPublisher(Publisher.VERS_DETAIL);
            } catch (ParerUserError e) {
                getMessageBox().addError(e.getDescription());
            }
        } else {
            forwardToPublisher(getLastPublisher());
        }
    }

    @Override
    public void parametriAmministrazioneTipoOggettoButton() throws Throwable {
        BigDecimal idAmbienteVers = getForm().getVers().getId_ambiente_vers().parse();
        BigDecimal idVers = getForm().getVers().getId_vers().parse();
        BigDecimal idTipoObject = getForm().getTipoObjectList().getTable().getCurrentRow()
                .getBigDecimal("id_tipo_object");
        loadListeParametriTipoOggetto(idAmbienteVers, idVers, idTipoObject, null, false, true, true, true, true);
        prepareRicercaParametriTipoOggetto(AMMINISTRAZIONE);
    }

    @Override
    public void parametriConservazioneTipoOggettoButton() throws Throwable {
        BigDecimal idAmbienteVers = getForm().getVers().getId_ambiente_vers().parse();
        BigDecimal idVers = getForm().getVers().getId_vers().parse();
        BigDecimal idTipoObject = getForm().getTipoObjectList().getTable().getCurrentRow()
                .getBigDecimal("id_tipo_object");
        loadListeParametriTipoOggetto(idAmbienteVers, idVers, idTipoObject, null, false, false, true, true, true);
        prepareRicercaParametriTipoOggetto(CONSERVAZIONE);
    }

    @Override
    public void parametriGestioneTipoOggettoButton() throws Throwable {
        BigDecimal idAmbienteVers = getForm().getVers().getId_ambiente_vers().parse();
        BigDecimal idVers = getForm().getVers().getId_vers().parse();
        BigDecimal idTipoObject = getForm().getTipoObjectList().getTable().getCurrentRow()
                .getBigDecimal("id_tipo_object");
        loadListeParametriTipoOggetto(idAmbienteVers, idVers, idTipoObject, null, false, false, false, true, true);
        prepareRicercaParametriTipoOggetto(GESTIONE);
    }

    private void prepareRicercaParametriTipoOggetto(String tipo) {
        getForm().getTipoObject().setStatus(Status.update);
        getForm().getRicercaParametriTipoOggetto().setEditMode();
        BaseTable tb = amministrazioneEjb.getFunzioneParametriTableBean();
        getForm().getRicercaParametriTipoOggetto().getFunzione().reset();
        getForm().getRicercaParametriTipoOggetto().getFunzione()
                .setDecodeMap(Factory.newInstance(tb, "funzione", "funzione"));
        getSession().setAttribute("provenienzaParametri", tipo);
        forwardToPublisher(Publisher.PARAMETRI_TIPO_OBJECT);
    }

    @Override
    public void ricercaParametriTipoOggettoButton() throws EMFError {
        getForm().getRicercaParametriTipoOggetto().post(getRequest());
        List<String> funzione = getForm().getRicercaParametriTipoOggetto().getFunzione().parse();
        BigDecimal idTipoObject = getForm().getTipoObjectList().getTable().getCurrentRow()
                .getBigDecimal("id_tipo_object");
        BigDecimal idVers = getForm().getVers().getId_vers().parse();
        BigDecimal idAmbienteVers = getForm().getVers().getId_ambiente_vers().parse();
        try {
            getForm().getTipoObject().setStatus(Status.update);
            if (getSession().getAttribute("provenienzaParametri") != null) {
                String provenzienzaParametri = (String) getSession().getAttribute("provenienzaParametri");
                switch (provenzienzaParametri) {
                case AMMINISTRAZIONE:
                    loadListeParametriTipoOggetto(idAmbienteVers, idVers, idTipoObject, funzione, false, true, true,
                            true, true);
                    break;
                case CONSERVAZIONE:
                    loadListeParametriTipoOggetto(idAmbienteVers, idVers, idTipoObject, funzione, false, false, true,
                            true, true);
                    break;
                case GESTIONE:
                    loadListeParametriTipoOggetto(idAmbienteVers, idVers, idTipoObject, funzione, false, false, false,
                            true, true);
                    break;
                default:
                    break;
                }
            }
        } catch (ParerUserError ex) {
            getMessageBox().addError("Errore durante il caricamento dei parametri tipo oggetto");
        }
        forwardToPublisher(Publisher.PARAMETRI_TIPO_OBJECT);
    }

    private void salvaParametriTipoOggetto() throws EMFError {
        getForm().getParametriAmministrazioneTipoOggettoList().post(getRequest());
        getForm().getParametriConservazioneTipoOggettoList().post(getRequest());
        getForm().getParametriGestioneTipoOggettoList().post(getRequest());

        BigDecimal idTipoObject = getForm().getTipoObjectList().getTable().getCurrentRow()
                .getBigDecimal("id_tipo_object");

        // Controllo valori possibili su ambiente
        PigParamApplicTableBean parametriAmministrazione = (PigParamApplicTableBean) getForm()
                .getParametriAmministrazioneTipoOggettoList().getTable();
        PigParamApplicTableBean parametriConservazione = (PigParamApplicTableBean) getForm()
                .getParametriConservazioneTipoOggettoList().getTable();
        PigParamApplicTableBean parametriGestione = (PigParamApplicTableBean) getForm()
                .getParametriGestioneTipoOggettoList().getTable();
        String error = amministrazioneEjb.checkParametriAmmessi("tipo_oggetto", parametriAmministrazione,
                parametriConservazione, parametriGestione);
        if (error != null) {
            getMessageBox().addError(error);
        }

        if (!getMessageBox().hasError()) {
            /*
             * Codice aggiuntivo per il logging...
             */
            LogParam param = SpagoliteLogUtil.getLogParam(
                    configHelper.getValoreParamApplicByApplic(Constants.NM_APPLIC), getUser().getUsername(),
                    SpagoliteLogUtil.getPageName(this),
                    SpagoliteLogUtil.getToolbarSave(getForm().getTipoObject().getStatus().equals(Status.update)));
            param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
            amministrazioneEjb.saveParametriTipoOggetto(param, parametriAmministrazione, parametriConservazione,
                    parametriGestione, idTipoObject);
            getMessageBox().addInfo("Parametri tipo oggetto versatore salvati con successo");
            getMessageBox().setViewMode(ViewMode.plain);
            getForm().getTipoObject().setViewMode();
            getForm().getTipoObject().setStatus(Status.view);
            getForm().getParametriAmministrazioneTipoOggettoList().setViewMode();
            getForm().getParametriConservazioneTipoOggettoList().setViewMode();
            getForm().getParametriGestioneTipoOggettoList().setViewMode();
            try {
                loadTipoOggetto(idTipoObject);
                forwardToPublisher(Publisher.TIPO_OBJECT_DETAIL);
            } catch (ParerUserError e) {
                getMessageBox().addError(e.getDescription());
            }
        } else {
            forwardToPublisher(getLastPublisher());
        }
    }

    // <editor-fold defaultstate="collapsed" desc="Gestione parametri">
    /**
     * Carica la pagina di lista parametri SACER PING
     *
     * @throws EMFError
     *             errore generico
     */
    @Secure(action = "Menu.AmministrazioneSistema.ListaConfigurazioni")
    public void loadListaConfigurazioni() throws EMFError {
        getUser().getMenu().reset();
        getUser().getMenu().select("Menu.AmministrazioneSistema.ListaConfigurazioni");
        getForm().getConfiguration().clear();
        getForm().getConfigurationList().clear();

        initConfigurationCombo();

        getForm().getConfiguration().getTi_param_applic_combo().setEditMode();
        getForm().getConfiguration().getTi_gestione_param_combo().setEditMode();
        getForm().getConfiguration().getFl_appart_applic_combo().setEditMode();
        getForm().getConfiguration().getFl_appart_ambiente_combo().setEditMode();
        getForm().getConfiguration().getFl_appart_vers_combo().setEditMode();
        getForm().getConfiguration().getFl_appart_tipo_oggetto_combo().setEditMode();

        getForm().getConfiguration().getLoad_config_list().setEditMode();

        getForm().getConfiguration().getEdit_config().setViewMode(); // MEV 25594
        getForm().getConfiguration().getAdd_config().setViewMode();
        getForm().getConfiguration().getSave_config().setViewMode();

        // Carico la lista dei configurazioni
        forwardToPublisher(Publisher.REGISTRO_PARAMETRI);
    }

    /**
     * MEV 25594 - Imposta la modalità di modifica della lista parametri
     *
     * @throws EMFError
     *             errore generico
     */
    @Override
    public void edit_config() throws EMFError {
        // Recupero i valori dai filtri ma NON riparsiamo la request!
        String tiParamApplic = getForm().getConfiguration().getTi_param_applic_combo().parse();
        String tiGestioneParam = getForm().getConfiguration().getTi_gestione_param_combo().parse();
        String flAppartApplic = getForm().getConfiguration().getFl_appart_applic_combo().parse();
        String flAppartAmbiente = getForm().getConfiguration().getFl_appart_ambiente_combo().parse();
        String flAppartVers = getForm().getConfiguration().getFl_appart_vers_combo().parse();
        String flAppartTipoOggetto = getForm().getConfiguration().getFl_appart_tipo_oggetto_combo().parse();

        // Carico i valori delle combo della lista
        getForm().getConfigurationList().getTi_gestione_param().setDecodeMap(ComboGetter.getMappaTiGestioneParam());
        getForm().getConfigurationList().getTi_valore_param_applic()
                .setDecodeMap(ComboGetter.getTiValoreParamApplicCombo());

        // Carico i valori della lista configurazioni
        PigParamApplicTableBean paramApplicTableBean = amministrazioneEjb.getPigParamApplicTableBean(tiParamApplic,
                tiGestioneParam, flAppartApplic, flAppartAmbiente, flAppartVers, flAppartTipoOggetto);

        getForm().getConfigurationList().setTable(paramApplicTableBean);
        getForm().getConfigurationList().getTable().setPageSize(300);
        getForm().getConfigurationList().getTable().first();

        // Rendo visibili i bottoni di aggiunta/salvataggio configurazione
        getForm().getConfiguration().getEdit_config().setViewMode();
        getForm().getConfiguration().getAdd_config().setEditMode();
        getForm().getConfiguration().getSave_config().setEditMode();

        // Rendo editabili i campi della lista
        getForm().getConfigurationList().getTi_param_applic().setEditMode();
        getForm().getConfigurationList().getTi_gestione_param().setEditMode();
        getForm().getConfigurationList().getNm_param_applic().setEditMode();
        getForm().getConfigurationList().getDm_param_applic().setEditMode();
        getForm().getConfigurationList().getDs_param_applic().setEditMode();
        getForm().getConfigurationList().getTi_valore_param_applic().setEditMode();
        getForm().getConfigurationList().getDs_lista_valori_ammessi().setEditMode();
        getForm().getConfigurationList().getDs_valore_param_applic().setEditMode();
        getForm().getConfigurationList().getFl_appart_applic().setEditMode();
        getForm().getConfigurationList().getFl_appart_ambiente().setEditMode();
        getForm().getConfigurationList().getFl_appart_vers().setEditMode();
        getForm().getConfigurationList().getFl_appart_tipo_oggetto().setEditMode();
        getForm().getConfigurationList().getFl_appart_applic().setReadonly(false);
        getForm().getConfigurationList().getFl_appart_ambiente().setReadonly(false);
        getForm().getConfigurationList().getFl_appart_vers().setReadonly(false);
        getForm().getConfigurationList().getFl_appart_tipo_oggetto().setReadonly(false);

        forwardToPublisher(Application.Publisher.REGISTRO_PARAMETRI);
    }

    /**
     * Carica la lista dei parametri in base ai filtri scelti
     *
     * @throws EMFError
     *             errore generico
     */
    @Override
    public void load_config_list() throws EMFError {
        // MEV 25594 - carico la lista in modalità non editabile

        // Recupero i valori dai filtri
        getForm().getConfiguration().post(getRequest());
        String tiParamApplic = getForm().getConfiguration().getTi_param_applic_combo().parse();
        String tiGestioneParam = getForm().getConfiguration().getTi_gestione_param_combo().parse();
        String flAppartApplic = getForm().getConfiguration().getFl_appart_applic_combo().parse();
        String flAppartAmbiente = getForm().getConfiguration().getFl_appart_ambiente_combo().parse();
        String flAppartVers = getForm().getConfiguration().getFl_appart_vers_combo().parse();
        String flAppartTipoOggetto = getForm().getConfiguration().getFl_appart_tipo_oggetto_combo().parse();

        // Carico i valori delle combo della lista
        getForm().getConfigurationList().getTi_gestione_param().setDecodeMap(ComboGetter.getMappaTiGestioneParam());
        getForm().getConfigurationList().getTi_valore_param_applic()
                .setDecodeMap(ComboGetter.getTiValoreParamApplicCombo());

        // Carico i valori della lista configurazioni
        PigParamApplicTableBean paramApplicTableBean = amministrazioneEjb.getPigParamApplicTableBean(tiParamApplic,
                tiGestioneParam, flAppartApplic, flAppartAmbiente, flAppartVers, flAppartTipoOggetto);

        paramApplicTableBean = obfuscatePasswordParamApplic(paramApplicTableBean);

        getForm().getConfigurationList().setTable(paramApplicTableBean);

        setConfigListReadOnly();

        // se non ho trovato risultati nascondo il pulsate "Edita"
        if (paramApplicTableBean.isEmpty()) {
            getForm().getConfiguration().getEdit_config().setViewMode();
        }

        forwardToPublisher(Publisher.REGISTRO_PARAMETRI);
    }

    /**
     * Aggiunge un nuovo parametro
     *
     * @throws EMFError
     *             errore generico
     */
    @Override
    public void add_config() throws EMFError {
        getForm().getConfigurationList().getTable().last();
        getForm().getConfigurationList().getTable().add(new PigParamApplicRowBean());
        forwardToPublisher(Publisher.REGISTRO_PARAMETRI);
    }

    /**
     * Esegue un controllo sui campi e inserisce i parametri nel database
     *
     * @throws EMFError
     *             errore generico
     */
    @Override
    public void save_config() throws EMFError {
        String idParamApplicName = getForm().getConfigurationList().getId_param_applic().getName();
        String tiParamApplicName = getForm().getConfigurationList().getTi_param_applic().getName();
        String tiGestioneParamName = getForm().getConfigurationList().getTi_gestione_param().getName();
        String nmParamApplicName = getForm().getConfigurationList().getNm_param_applic().getName();
        String dsParamApplicName = getForm().getConfigurationList().getDs_param_applic().getName();
        String dsListaValoriAmmessiName = getForm().getConfigurationList().getDs_lista_valori_ammessi().getName();
        String dsValoreParamApplicName = getForm().getConfigurationList().getDs_valore_param_applic().getName();
        String flAppartApplicName = getForm().getConfigurationList().getFl_appart_applic().getName();
        String tiValoreParamApplic = getForm().getConfigurationList().getTi_valore_param_applic().getName();
        Set<Integer> completeRows = new HashSet<>();
        Set<String> nmParamApplicSet = new HashSet<>();
        // Tiro su i dati i request di tutti i record della lista
        getForm().getConfigurationList().post(getRequest());
        // Scorro tutte le righe della tabella per effettuare i controlli
        for (int i = 0; i < getForm().getConfigurationList().getTable().size(); i++) {
            BaseRowInterface r = getForm().getConfigurationList().getTable().getRow(i);
            BigDecimal idParamApplicValue = r.getBigDecimal(idParamApplicName);
            String tiParamApplicValue = r.getString(tiParamApplicName);
            String tiGestioneParamValue = r.getString(tiGestioneParamName);
            String nmParamApplicValue = r.getString(nmParamApplicName);
            String dsParamApplicValue = r.getString(dsParamApplicName);
            String dsListaValoriAmmessiValue = r.getString(dsListaValoriAmmessiName);
            String dsValoreParamApplicValue = r.getString(dsValoreParamApplicName);
            String tiValoreParamApplicValue = r.getString(tiValoreParamApplic);
            String flAppartApplicValue = r.getString(flAppartApplicName);
            if (StringUtils.isNotBlank(tiParamApplicValue) && StringUtils.isNotBlank(tiGestioneParamValue)
                    && StringUtils.isNotBlank(nmParamApplicValue) && StringUtils.isNotBlank(dsParamApplicValue)
                    && StringUtils.isNotBlank(tiValoreParamApplicValue) // &&
            ) {
                if (StringUtils.isNotBlank(dsValoreParamApplicValue)) {
                    if (flAppartApplicValue.equals("1")) {
                        completeRows.add(i);
                    } else {
                        getMessageBox().addError(
                                "Il valore del parametro può essere indicato solo se il parametro ha il flag Applicazione alzato");
                        getMessageBox().setViewMode(ViewMode.plain);
                    }
                } else {
                    completeRows.add(i);
                }
            } else {
                getMessageBox().addError("Almeno un parametro non ha tutti i campi obbligatori valorizzati");
                getMessageBox().setViewMode(ViewMode.plain);
            }

            nmParamApplicSet.add(nmParamApplicValue);

            // Controllo che il parametro non esista già su DB
            if (amministrazioneEjb.checkParamApplic(nmParamApplicValue, idParamApplicValue)) {
                getMessageBox().addError("Attenzione: parametro " + nmParamApplicValue + " già presente nel sistema");
            }

            // Controllo valori possibili su ente
            if (dsListaValoriAmmessiValue != null && !dsListaValoriAmmessiValue.equals("")
                    && dsValoreParamApplicValue != null && !dsValoreParamApplicValue.equals("")
                    && !amministrazioneEjb.inValoriPossibili(dsValoreParamApplicValue, dsListaValoriAmmessiValue)) {
                getMessageBox().addError("Il valore del parametro non è compreso tra i valori ammessi sul parametro");
            }
        }

        // Controllo che il nome-parametro non sia ripetuto per motivi di univocità
        if (nmParamApplicSet.size() != getForm().getConfigurationList().getTable().size()) {
            getMessageBox().addError("Attenzione: esistono uno o più parametri con lo stesso nome parametro");
        }

        if (!getMessageBox().hasError()) {
            for (Integer rowIndex : completeRows) {
                PigParamApplicRowBean row = ((PigParamApplicTableBean) getForm().getConfigurationList().getTable())
                        .getRow(rowIndex);

                // MEV 22933 - non sovrascrivere con il valore offuscato il valore originale.
                if (row.getTiValoreParamApplic().equals(ComboValueParamentersType.PASSWORD.name())
                        && row.getString("ds_valore_param_applic").equals(Constants.OBFUSCATED_STRING)) {
                    continue;
                }

                if (!amministrazioneEjb.saveConfiguration(row)) {
                    getMessageBox().addError("Errore durante il salvataggio della configurazione");
                }
            }
            if (!getMessageBox().hasError()) {
                getMessageBox().addInfo("Configurazione salvata con successo");
                getMessageBox().setViewMode(ViewMode.plain);

                initConfigurationCombo();

                // MEV 25594
                PigParamApplicTableBean paramApplicTableBean = (PigParamApplicTableBean) getForm()
                        .getConfigurationList().getTable();
                paramApplicTableBean = obfuscatePasswordParamApplic(paramApplicTableBean);
                getForm().getConfigurationList().setTable(paramApplicTableBean);
                setConfigListReadOnly();
            }
        }

        forwardToPublisher(Publisher.REGISTRO_PARAMETRI);
    }

    /**
     * MEV 25594 - Imposta la modalità di read-only della lista parametri
     *
     *
     */
    private void setConfigListReadOnly() {
        getForm().getConfigurationList().getTable().setPageSize(100);
        getForm().getConfigurationList().getTable().first();

        // Rendo visibili i bottoni di aggiunta/salvataggio configurazione
        getForm().getConfiguration().getEdit_config().setEditMode();
        getForm().getConfiguration().getAdd_config().setViewMode();
        getForm().getConfiguration().getSave_config().setViewMode();

        // Rendo non modificabili i campi della lista
        getForm().getConfigurationList().getTi_param_applic().setViewMode();
        getForm().getConfigurationList().getTi_gestione_param().setViewMode();
        getForm().getConfigurationList().getNm_param_applic().setViewMode();
        getForm().getConfigurationList().getDm_param_applic().setViewMode();
        getForm().getConfigurationList().getDs_param_applic().setViewMode();
        getForm().getConfigurationList().getTi_valore_param_applic().setViewMode();
        getForm().getConfigurationList().getDs_lista_valori_ammessi().setViewMode();
        getForm().getConfigurationList().getDs_valore_param_applic().setViewMode();
        getForm().getConfigurationList().getFl_appart_applic().setEditMode();
        getForm().getConfigurationList().getFl_appart_ambiente().setEditMode();
        getForm().getConfigurationList().getFl_appart_vers().setEditMode();
        getForm().getConfigurationList().getFl_appart_tipo_oggetto().setEditMode();
        getForm().getConfigurationList().getFl_appart_applic().setReadonly(true);
        getForm().getConfigurationList().getFl_appart_ambiente().setReadonly(true);
        getForm().getConfigurationList().getFl_appart_vers().setReadonly(true);
        getForm().getConfigurationList().getFl_appart_tipo_oggetto().setReadonly(true);
    }

    /**
     * Inizializza la combo dei tipi parametro
     *
     * @throws EMFError
     *             errore generico
     */
    private void initConfigurationCombo() {
        BaseTable tiParamApplic = amministrazioneEjb.getTiParamApplicBaseTable();
        DecodeMap mappaTiParamApplic = Factory.newInstance(tiParamApplic,
                PigParamApplicTableDescriptor.COL_TI_PARAM_APPLIC, PigParamApplicTableDescriptor.COL_TI_PARAM_APPLIC);
        getForm().getConfiguration().getTi_param_applic_combo().setDecodeMap(mappaTiParamApplic);

        getForm().getConfiguration().getTi_gestione_param_combo().setDecodeMap(ComboGetter.getMappaTiGestioneParam());

        getForm().getConfiguration().getFl_appart_applic_combo().setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());
        getForm().getConfiguration().getFl_appart_ambiente_combo().setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());
        getForm().getConfiguration().getFl_appart_vers_combo().setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());
        getForm().getConfiguration().getFl_appart_tipo_oggetto_combo()
                .setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());
    }

    /**
     * Elimina un parametro dalla lista
     *
     * @throws EMFError
     *             errore generico
     */
    @Override
    public void deleteConfigurationList() throws EMFError {
        PigParamApplicRowBean row = (PigParamApplicRowBean) getForm().getConfigurationList().getTable().getCurrentRow();
        int deletedRowIndex = getForm().getConfigurationList().getTable().getCurrentRowIndex();
        getForm().getConfigurationList().getTable().remove(deletedRowIndex);
        if (row.getIdParamApplic() != null && amministrazioneEjb.deletePigParamApplicRowBean(row)) {
            getMessageBox().addInfo("Configurazione eliminata con successo");
            getMessageBox().setViewMode(ViewMode.plain);
        }

        forwardToPublisher(Publisher.REGISTRO_PARAMETRI);
    }

    /**
     * Elimina un parametro di amministrazione dalla lista nel dettaglio versatore
     *
     * @throws EMFError
     *             errore generico
     */
    @Override
    public void deleteParametriAmministrazioneVersatoreList() throws EMFError {
        PigParamApplicRowBean row = (PigParamApplicRowBean) getForm().getParametriAmministrazioneVersatoreList()
                .getTable().getCurrentRow();
        BigDecimal idValoreParamApplic = row.getBigDecimal("id_valore_param_applic");
        /*
         * Codice aggiuntivo per il logging...
         */
        LogParam param = SpagoliteLogUtil.getLogParam(configHelper.getParamApplicApplicationName(),
                getUser().getUsername(), SpagoliteLogUtil.getPageName(this));
        param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
        param.setNomeAzione(SpagoliteLogUtil.getDetailActionNameDelete(getForm(),
                getForm().getParametriAmministrazioneVersatoreList()));
        if (idValoreParamApplic != null) {
            if (amministrazioneEjb.deleteParametroVersatore(param, idValoreParamApplic)) {
                getMessageBox().addInfo("Parametro di amministrazione eliminato con successo");
                getMessageBox().setViewMode(ViewMode.plain);
            }
        } else {
            getMessageBox().addWarning("Valore sul versatore non presente: nessuna cancellazione effettuata");
        }
        aggiornaDettaglioVersatore(getForm().getVers().getId_vers().parse());
        forwardToPublisher(Publisher.VERS_DETAIL);
    }

    /**
     * Elimina un parametro di conservazione dalla lista nel dettaglio versatore
     *
     * @throws EMFError
     *             errore generico
     */
    @Override
    public void deleteParametriConservazioneVersatoreList() throws EMFError {
        PigParamApplicRowBean row = (PigParamApplicRowBean) getForm().getParametriConservazioneVersatoreList()
                .getTable().getCurrentRow();
        BigDecimal idValoreParamApplic = row.getBigDecimal("id_valore_param_applic");
        /*
         * Codice aggiuntivo per il logging...
         */
        LogParam param = SpagoliteLogUtil.getLogParam(configHelper.getParamApplicApplicationName(),
                getUser().getUsername(), SpagoliteLogUtil.getPageName(this));
        param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
        param.setNomeAzione(SpagoliteLogUtil.getDetailActionNameDelete(getForm(),
                getForm().getParametriConservazioneVersatoreList()));
        if (idValoreParamApplic != null) {
            if (amministrazioneEjb.deleteParametroVersatore(param, idValoreParamApplic)) {
                getMessageBox().addInfo("Parametro di conservazione eliminato con successo");
                getMessageBox().setViewMode(ViewMode.plain);
            }
        } else {
            getMessageBox().addWarning("Valore sul versatore non presente: nessuna cancellazione effettuata");
        }
        aggiornaDettaglioVersatore(getForm().getVers().getId_vers().parse());
        forwardToPublisher(Publisher.VERS_DETAIL);
    }

    /**
     * Elimina un parametro di gestione dalla lista nel dettaglio versatore
     *
     * @throws EMFError
     *             errore generico
     */
    @Override
    public void deleteParametriGestioneVersatoreList() throws EMFError {
        PigParamApplicRowBean row = (PigParamApplicRowBean) getForm().getParametriGestioneVersatoreList().getTable()
                .getCurrentRow();
        BigDecimal idValoreParamApplic = row.getBigDecimal("id_valore_param_applic");
        /*
         * Codice aggiuntivo per il logging...
         */
        LogParam param = SpagoliteLogUtil.getLogParam(configHelper.getParamApplicApplicationName(),
                getUser().getUsername(), SpagoliteLogUtil.getPageName(this));
        param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
        param.setNomeAzione(
                SpagoliteLogUtil.getDetailActionNameDelete(getForm(), getForm().getParametriGestioneVersatoreList()));
        if (idValoreParamApplic != null) {
            if (amministrazioneEjb.deleteParametroVersatore(param, idValoreParamApplic)) {
                getMessageBox().addInfo("Parametro di gestione eliminato con successo");
                getMessageBox().setViewMode(ViewMode.plain);
            }
        } else {
            getMessageBox().addWarning("Valore sul versatore non presente: nessuna cancellazione effettuata");
        }
        aggiornaDettaglioVersatore(getForm().getVers().getId_vers().parse());
        forwardToPublisher(Publisher.VERS_DETAIL);
    }

    /**
     * Elimina un parametro di amministrazione dalla lista nel dettaglio ambiente
     *
     * @throws EMFError
     *             errore generico
     */
    @Override
    public void deleteParametriAmministrazioneAmbienteList() throws EMFError {
        PigParamApplicRowBean row = (PigParamApplicRowBean) getForm().getParametriAmministrazioneAmbienteList()
                .getTable().getCurrentRow();
        BigDecimal idValoreParamApplic = row.getBigDecimal("id_valore_param_applic");
        if (idValoreParamApplic != null) {
            if (amministrazioneEjb.deleteParametroAmbiente(idValoreParamApplic)) {
                getMessageBox().addInfo("Parametro di amministrazione eliminato con successo");
                getMessageBox().setViewMode(ViewMode.plain);
            }
        } else {
            getMessageBox().addWarning("Valore sull'ambiente versatore non presente: nessuna cancellazione effettuata");
        }
        try {
            loadAmbienteVers(getForm().getAmbienteVers().getId_ambiente_vers().parse());
        } catch (ParerUserError ex) {
            getMessageBox().addError("Errore durante il caricamento dell'ambiente versatore");
        }
        forwardToPublisher(Publisher.AMBIENTE_VERS_DETAIL);
    }

    /**
     * Elimina un parametro di conservazione dalla lista nel dettaglio ambiente
     *
     * @throws EMFError
     *             errore generico
     */
    @Override
    public void deleteParametriConservazioneAmbienteList() throws EMFError {
        PigParamApplicRowBean row = (PigParamApplicRowBean) getForm().getParametriConservazioneAmbienteList().getTable()
                .getCurrentRow();
        BigDecimal idValoreParamApplic = row.getBigDecimal("id_valore_param_applic");
        if (idValoreParamApplic != null) {
            if (amministrazioneEjb.deleteParametroAmbiente(idValoreParamApplic)) {
                getMessageBox().addInfo("Parametro di conservazione eliminato con successo");
                getMessageBox().setViewMode(ViewMode.plain);
            }
        } else {
            getMessageBox().addWarning("Valore sull'ambiente versatore non presente: nessuna cancellazione effettuata");
        }
        try {
            loadAmbienteVers(getForm().getAmbienteVers().getId_ambiente_vers().parse());
        } catch (ParerUserError ex) {
            getMessageBox().addError("Errore durante il caricamento dell'ambiente versatore");
        }
        forwardToPublisher(Publisher.AMBIENTE_VERS_DETAIL);
    }

    /**
     * Elimina un parametro di gestione dalla lista nel dettaglio ambiente
     *
     * @throws EMFError
     *             errore generico
     */
    @Override
    public void deleteParametriGestioneAmbienteList() throws EMFError {
        PigParamApplicRowBean row = (PigParamApplicRowBean) getForm().getParametriGestioneAmbienteList().getTable()
                .getCurrentRow();
        BigDecimal idValoreParamApplic = row.getBigDecimal("id_valore_param_applic");
        if (idValoreParamApplic != null) {
            if (amministrazioneEjb.deleteParametroAmbiente(idValoreParamApplic)) {
                getMessageBox().addInfo("Parametro di gestione eliminato con successo");
                getMessageBox().setViewMode(ViewMode.plain);
            }
        } else {
            getMessageBox().addWarning("Valore sull'ambiente versatore non presente: nessuna cancellazione effettuata");
        }
        try {
            loadAmbienteVers(getForm().getAmbienteVers().getId_ambiente_vers().parse());
        } catch (ParerUserError ex) {
            getMessageBox().addError("Errore durante il caricamento dell'ambiente versatore");
        }
        forwardToPublisher(Publisher.AMBIENTE_VERS_DETAIL);
    }

    /**
     * Elimina un parametro di amministrazione dalla lista nel dettaglio tipo oggetto
     *
     * @throws EMFError
     *             errore generico
     */
    @Override
    public void deleteParametriAmministrazioneTipoOggettoList() throws EMFError {
        PigParamApplicRowBean row = (PigParamApplicRowBean) getForm().getParametriAmministrazioneTipoOggettoList()
                .getTable().getCurrentRow();
        BigDecimal idValoreParamApplic = row.getBigDecimal("id_valore_param_applic");
        LogParam param = SpagoliteLogUtil.getLogParam(configHelper.getParamApplicApplicationName(),
                getUser().getUsername(), SpagoliteLogUtil.getPageName(this));
        param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
        param.setNomeAzione(SpagoliteLogUtil.getDetailActionNameDelete(getForm(),
                getForm().getParametriAmministrazioneTipoOggettoList()));
        if (idValoreParamApplic != null) {
            if (amministrazioneEjb.deleteParametroTipoOggetto(param, idValoreParamApplic)) {
                getMessageBox().addInfo("Parametro di amministrazione eliminato con successo");
                getMessageBox().setViewMode(ViewMode.plain);
            }
        } else {
            getMessageBox().addWarning("Valore sul tipo oggetto non presente: nessuna cancellazione effettuata");
        }
        try {
            loadTipoOggetto(getForm().getTipoObject().getId_tipo_object().parse());
        } catch (ParerUserError ex) {
            getMessageBox().addError("Errore durante il caricamento del tipo oggetto");
        }
        forwardToPublisher(Publisher.TIPO_OBJECT_DETAIL);
    }

    /**
     * Elimina un parametro di conservazione dalla lista nel dettaglio tipo oggetto
     *
     * @throws EMFError
     *             errore generico
     */
    @Override
    public void deleteParametriConservazioneTipoOggettoList() throws EMFError {
        PigParamApplicRowBean row = (PigParamApplicRowBean) getForm().getParametriConservazioneTipoOggettoList()
                .getTable().getCurrentRow();
        BigDecimal idValoreParamApplic = row.getBigDecimal("id_valore_param_applic");
        LogParam param = SpagoliteLogUtil.getLogParam(configHelper.getParamApplicApplicationName(),
                getUser().getUsername(), SpagoliteLogUtil.getPageName(this));
        param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
        param.setNomeAzione(SpagoliteLogUtil.getDetailActionNameDelete(getForm(),
                getForm().getParametriConservazioneTipoOggettoList()));
        if (idValoreParamApplic != null) {
            if (amministrazioneEjb.deleteParametroTipoOggetto(param, idValoreParamApplic)) {
                getMessageBox().addInfo("Parametro di conservazione eliminato con successo");
                getMessageBox().setViewMode(ViewMode.plain);
            }
        } else {
            getMessageBox().addWarning("Valore sul tipo oggetto non presente: nessuna cancellazione effettuata");
        }
        try {
            loadTipoOggetto(getForm().getTipoObject().getId_tipo_object().parse());
        } catch (ParerUserError ex) {
            getMessageBox().addError("Errore durante il caricamento del tipo oggetto");
        }
        forwardToPublisher(Publisher.TIPO_OBJECT_DETAIL);
    }

    /**
     * Elimina un parametro di gestione dalla lista nel dettaglio tipo oggetto
     *
     * @throws EMFError
     *             errore generico
     */
    @Override
    public void deleteParametriGestioneTipoOggettoList() throws EMFError {
        PigParamApplicRowBean row = (PigParamApplicRowBean) getForm().getParametriGestioneTipoOggettoList().getTable()
                .getCurrentRow();
        BigDecimal idValoreParamApplic = row.getBigDecimal("id_valore_param_applic");
        LogParam param = SpagoliteLogUtil.getLogParam(configHelper.getParamApplicApplicationName(),
                getUser().getUsername(), SpagoliteLogUtil.getPageName(this));
        param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
        param.setNomeAzione(
                SpagoliteLogUtil.getDetailActionNameDelete(getForm(), getForm().getParametriGestioneTipoOggettoList()));
        if (idValoreParamApplic != null) {
            if (amministrazioneEjb.deleteParametroTipoOggetto(param, idValoreParamApplic)) {
                getMessageBox().addInfo("Parametro di gestione eliminato con successo");
                getMessageBox().setViewMode(ViewMode.plain);
            }
        } else {
            getMessageBox().addWarning("Valore sul tipo oggetto non presente: nessuna cancellazione effettuata");
        }
        try {
            loadTipoOggetto(getForm().getTipoObject().getId_tipo_object().parse());
        } catch (ParerUserError ex) {
            getMessageBox().addError("Errore durante il caricamento del tipo oggetto");
        }
        forwardToPublisher(Publisher.TIPO_OBJECT_DETAIL);
    }

    @Override
    public JSONObject triggerAmbienteVersId_ambiente_ente_convenzOnTrigger() throws EMFError {
        getForm().getAmbienteVers().post(getRequest());

        if (getForm().getAmbienteVers().getId_ambiente_ente_convenz().parse() != null) {
            BaseTable enteConvenzTable = amministrazioneEjb.getEntiGestoreAbilitatiTableBean(
                    new BigDecimal(getUser().getIdUtente()),
                    getForm().getAmbienteVers().getId_ambiente_ente_convenz().parse());
            DecodeMap mappaEnte = new DecodeMap();
            mappaEnte.populatedMap(enteConvenzTable, "id_ente_gestore", "nm_ente_gestore");
            getForm().getAmbienteVers().getId_ente_gestore().setDecodeMap(mappaEnte);
            getForm().getAmbienteVers().getNm_ente_conserv().setValue("");
        } else {
            DecodeMap map = new DecodeMap();
            getForm().getAmbienteVers().getId_ente_gestore().setDecodeMap(map);
            getForm().getAmbienteVers().getNm_ente_conserv().setValue("");
        }

        return getForm().getAmbienteVers().asJSON();
    }
    // </editor-fold>

    @Override
    public JSONObject triggerAmbienteVersId_ente_gestoreOnTrigger() throws EMFError {
        getForm().getAmbienteVers().post(getRequest());

        if (getForm().getAmbienteVers().getId_ente_gestore().parse() != null) {

            BaseTable enteConservTable = amministrazioneEjb.getEntiConservatori(getUser().getIdUtente(),
                    getForm().getAmbienteVers().getId_ente_gestore().parse());
            DecodeMap mappaEnte = new DecodeMap();
            mappaEnte.populatedMap(enteConservTable, "id_ente_siam", "nm_ente_siam");
            getForm().getAmbienteVers().getId_ente_conserv().setDecodeMap(mappaEnte);
            getForm().getAmbienteVers().getNm_ente_conserv().setValue("");
        } else {
            getForm().getAmbienteVers().getId_ente_conserv().setDecodeMap(null);
            getForm().getAmbienteVers().getNm_ente_conserv().setValue("");
        }

        return getForm().getAmbienteVers().asJSON();
    }

    @Override
    public JSONObject triggerVersTipologiaOnTrigger() throws EMFError {
        getForm().getVers().post(getRequest());

        getForm().getVers().getDt_ini_val_appart_ente_siam().clear();
        getForm().getVers().getDt_fine_val_appart_ente_siam().clear();

        String tipologia = getForm().getVers().getTipologia().parse();
        // Date precompilate
        Calendar cal = Calendar.getInstance();
        cal.set(2444, Calendar.DECEMBER, 31, 0, 0, 0);
        if (tipologia != null) {
            if (tipologia.equals("PRODUTTORE")) {
                getForm().getVers().getId_ambiente_ente_convenz_ec()
                        .setDecodeMap(Factory.newInstance(
                                amministrazioneEjb.getUsrVAbilAmbEnteConvenzTableBean(
                                        BigDecimal.valueOf(getUser().getIdUtente())),
                                "id_ambiente_ente_convenz", "nm_ambiente_ente_convenz"));
                getForm().getVers().getId_ente_convenz_ec().setDecodeMap(new DecodeMap());
            } else if (tipologia.equals("FORNITORE_ESTERNO")) {
                getForm().getVers().getId_ente_convenz_fe().clear();
                getForm().getVers().getId_ente_convenz_fe()
                        .setDecodeMap(Factory.newInstance(
                                amministrazioneEjb.getOrgVRicEnteNonConvenzAbilTableBean(
                                        BigDecimal.valueOf(getUser().getIdUtente()), "FORNITORE_ESTERNO"),
                                "id_ente_siam", "nm_ente_siam"));
            } else if (tipologia.equals("SOGGETTO_ATTUATORE")) {
                getForm().getVers().getId_ente_convenz_fe().clear();
                getForm().getVers().getId_ente_convenz_fe()
                        .setDecodeMap(Factory.newInstance(
                                amministrazioneEjb.getOrgVRicEnteNonConvenzAbilTableBean(
                                        BigDecimal.valueOf(getUser().getIdUtente()), "SOGGETTO_ATTUATORE"),
                                "id_ente_siam", "nm_ente_siam"));
            }
        } else {
            getForm().getVers().getId_ambiente_ente_convenz_ec().setDecodeMap(new DecodeMap());
            getForm().getVers().getId_ente_convenz_ec().setDecodeMap(new DecodeMap());
            getForm().getVers().getId_ente_convenz_fe().setDecodeMap(new DecodeMap());
        }

        return getForm().getVers().asJSON();
    }

    @Override
    public JSONObject triggerVersId_ente_convenz_ecOnTrigger() throws EMFError {
        getForm().getVers().post(getRequest());
        BigDecimal idEnteConvenz = getForm().getVers().getId_ente_convenz_ec().parse();
        Date[] dtEnteAccordoValido = amministrazioneEjb.getDatePerEnteProduttore(idEnteConvenz);
        String dtDecAccordoValido = new SimpleDateFormat("dd/MM/yyyy").format(dtEnteAccordoValido[0]);
        String dataFineValEnteSiam = new SimpleDateFormat("dd/MM/yyyy").format(dtEnteAccordoValido[1]);
        getForm().getVers().getDt_ini_val_appart_ente_siam().setValue(dtDecAccordoValido);
        getForm().getVers().getDt_fine_val_appart_ente_siam().setValue(dataFineValEnteSiam);
        return getForm().getVers().asJSON();
    }

    @Override
    public JSONObject triggerVersId_ente_convenz_feOnTrigger() throws EMFError {
        getForm().getVers().post(getRequest());
        BigDecimal idEnteSiam = getForm().getVers().getId_ente_convenz_fe().parse();
        Date[] datePerEnteFornitore = amministrazioneEjb.getDatePerEnteFornitore(idEnteSiam);
        String dtIniVal = new SimpleDateFormat("dd/MM/yyyy").format(datePerEnteFornitore[0]);
        String dtCessazione = new SimpleDateFormat("dd/MM/yyyy").format(datePerEnteFornitore[1]);
        getForm().getVers().getDt_ini_val_appart_ente_siam().setValue(dtIniVal);
        getForm().getVers().getDt_fine_val_appart_ente_siam().setValue(dtCessazione);
        return getForm().getVers().asJSON();
    }

    @Override
    public JSONObject triggerVersTi_dich_versOnTrigger() throws EMFError {
        getForm().getVers().post(getRequest());
        String tiDichVers = getForm().getVers().getTi_dich_vers().parse();
        if (StringUtils.isNoneBlank(tiDichVers))
            try {
                getForm().getVers().getId_organiz_iam().setDecodeMap(getMappaDlCompositoOrganiz(tiDichVers));
                return getForm().getVers().asJSON();
            } catch (Exception e) {
                getForm().getVers().asJSON(e.getMessage());
            }
        else {
            getForm().getVers().getId_organiz_iam().setDecodeMap(new DecodeMap());
        }
        return getForm().getVers().asJSON();
    }

    @Override
    public JSONObject triggerVersId_ambiente_ente_convenz_ecOnTrigger() throws EMFError {
        getForm().getVers().post(getRequest());

        if (getForm().getVers().getId_ambiente_ente_convenz_ec().parse() != null) {
            BaseTable enteConvenzTable = amministrazioneEjb.getEntiGestoreAbilitatiGenericiTableBean(
                    new BigDecimal(getUser().getIdUtente()),
                    getForm().getVers().getId_ambiente_ente_convenz_ec().parse());
            DecodeMap mappaEnte = new DecodeMap();
            mappaEnte.populatedMap(enteConvenzTable, "id_ente_convenz", "nm_ente_convenz");
            getForm().getVers().getId_ente_convenz_ec().setDecodeMap(mappaEnte);
            getForm().getVers().getId_ente_convenz_fe().setDecodeMap(new DecodeMap());
        } else {
            DecodeMap map = new DecodeMap();
            getForm().getVers().getId_ente_convenz_ec().setDecodeMap(map);
            getForm().getVers().getId_ente_convenz_fe().setDecodeMap(new DecodeMap());
        }

        return getForm().getVers().asJSON();
    }

    @Override
    public JSONObject triggerVersNm_versOnTrigger() throws EMFError {
        getForm().getVers().post(getRequest());
        String nmVers = getForm().getVers().getNm_vers().parse();
        if (nmVers != null) {
            BigDecimal idAmbienteVers = getForm().getVers().getId_ambiente_vers().parse();
            String dsPathInputFtp = nmVers + "/INPUT_FOLDER/";
            String dsPathOutputFtp = nmVers + "/OUTPUT_FOLDER/";
            String dsPathTrasf = nmVers + "/TRASFORMATI/";
            if (idAmbienteVers != null) {
                BigDecimal idVers = getForm().getVers().getId_vers().parse();
                try {
                    String dsPrefissoPath = configHelper.getValoreParamApplicByIdVers(Constants.DS_PREFISSO_PATH,
                            idAmbienteVers, idVers);
                    if (dsPrefissoPath != null) {
                        dsPathInputFtp = dsPrefissoPath + dsPathInputFtp;
                        dsPathOutputFtp = dsPrefissoPath + dsPathOutputFtp;
                        dsPathTrasf = dsPrefissoPath + dsPathTrasf;
                    } else {
                        getMessageBox().addError("Definire il valore del parametro DS_PREFISSO_PATH");
                    }
                } catch (ParamApplicNotFoundException e) {
                    getMessageBox().addError(ExceptionUtils.getRootCauseMessage(e));
                }
            }

            if (!getMessageBox().hasError()) {
                getForm().getVers().getDs_path_input_ftp().setValue(dsPathInputFtp);
                getForm().getVers().getDs_path_output_ftp().setValue(dsPathOutputFtp);
                getForm().getVers().getDs_path_trasf().setValue(dsPathTrasf);
            }
        }
        return getForm().getVers().asJSON();
    }

    @Override
    public JSONObject triggerVersId_ambiente_versOnTrigger() throws EMFError {
        getForm().getVers().post(getRequest());
        BigDecimal idAmbienteVers = getForm().getVers().getId_ambiente_vers().parse();
        if (idAmbienteVers != null) {
            String dsPathInputFtp = null;
            String dsPathOutputFtp = null;
            String dsPathTrasf = null;
            BigDecimal idVers = getForm().getVers().getId_vers().parse();
            try {
                String dsPrefissoPath = configHelper.getValoreParamApplicByIdVers(Constants.DS_PREFISSO_PATH,
                        idAmbienteVers, idVers);

                if (dsPrefissoPath != null) {
                    String nmVers = getForm().getVers().getNm_vers().parse();
                    if (nmVers != null) {
                        dsPathInputFtp = dsPrefissoPath + nmVers + "/INPUT_FOLDER/";
                        dsPathOutputFtp = dsPrefissoPath + nmVers + "/OUTPUT_FOLDER/";
                        dsPathTrasf = dsPrefissoPath + nmVers + "/TRASFORMATI/";
                    }
                } else {
                    getMessageBox().addError("Definire il valore del parametro DS_PREFISSO_PATH");
                }

                if (!getMessageBox().hasError()) {
                    getForm().getVers().getDs_path_input_ftp().setValue(dsPathInputFtp);
                    getForm().getVers().getDs_path_output_ftp().setValue(dsPathOutputFtp);
                    getForm().getVers().getDs_path_trasf().setValue(dsPathTrasf);
                }
                return getForm().getVers().asJSON();
            } catch (Exception e) {
            }
        }
        return getForm().getVers().asJSON();
    }

    // Questo metodo esiste solo perchè abbiamo creato il pulsante, ma non viene usato.
    @Override
    public void caricaParametriDaCSV() throws EMFError {
        // template
    }

    public void processParametriDaCSV() throws EMFError {
        getMessageBox().clear();

        BigDecimal idVersTipoObjectDaTrasf = ((PigVValoreSetParamTrasfRowBean) getForm().getSetParametriVersatoreList()
                .getTable().getCurrentRow()).getIdVersTipoObjectDaTrasf();
        BigDecimal idSetParamTrasf = ((PigVValParamTrasfDefSpecRowBean) getForm().getValoreParametriVersatoreList()
                .getTable().getCurrentRow()).getIdSetParamTrasf();

        try {
            if (getForm().getSetParametriVersatoreDetail().getDs_file_csv_parameters().parse() == null) {
                getMessageBox().addError("Nessun file selezionato");
            }

            if (!getMessageBox().hasError()) {

                byte[] fileCsvParametri = getForm().getSetParametriVersatoreDetail().getDs_file_csv_parameters()
                        .getFileBytes();

                CsvReader csvReader = new CsvReader(new ByteArrayInputStream(fileCsvParametri), ';',
                        StandardCharsets.UTF_8);
                csvReader.setSkipEmptyRecords(true);
                csvReader.readHeaders();

                // controllo che ci siano le colonne che mi servono
                List<String> headers = Arrays.asList(csvReader.getHeaders());
                if (!headers.contains("Parametro") && !headers.contains("Valore")) {
                    String messaggio = "Errore: il csv dei parametri deve contenere almeno le colonne \"Parametro\" e \"Valore\".";
                    getMessageBox().addError(messaggio);
                }

                if (!getMessageBox().hasError()) {
                    LogParam logParam = SpagoliteLogUtil.getLogParam(
                            paramApplicHelper.getApplicationName().getDsValoreParamApplic(), getUser().getUsername(),
                            SpagoliteLogUtil.getPageName(this));
                    logParam.setNomeAzione(
                            SpagoliteLogUtil.getButtonActionName(getForm(), getForm().getSetParametriVersatoreDetail(),
                                    getForm().getSetParametriVersatoreDetail().getCaricaParametriDaCSV().getName()));
                    logParam.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());

                    List<Map<String, String>> parameters = new ArrayList<>();

                    while (csvReader.readRecord()) {
                        String parametro = csvReader.get("Parametro");
                        String valore = csvReader.get("Valore");

                        Map<String, String> parameterMap = new HashMap<>();
                        parameterMap.put("parametro", parametro);
                        parameterMap.put("valore", valore);

                        if (!amministrazioneHelper.existPigVValParamTrasfDefSpec(idSetParamTrasf, parametro)) {
                            String messaggio = "Errore: Il parametro " + parametro + " non esiste nel set corrente.";
                            getMessageBox().addError(messaggio);
                        } else {
                            String tiParamTrasf = amministrazioneHelper.getPigVValParamTrasfDefSpecTypeByName(
                                    idSetParamTrasf, idVersTipoObjectDaTrasf, parametro);
                            if (tiParamTrasf != null) {
                                checkCoerenzaParametro(parametro, valore);
                            }

                            parameters.add(parameterMap);
                        }
                    }

                    if (!getMessageBox().hasError()) {
                        amministrazioneEjb.saveListOfValoreParametroVersatore(logParam, parameters, idSetParamTrasf,
                                idVersTipoObjectDaTrasf);
                        getMessageBox().addInfo("Aggiornati " + parameters.size() + " parametri.");
                    }
                }
            }

            loadValoriParametriVersatore(idSetParamTrasf, idVersTipoObjectDaTrasf);
            forwardToPublisher(getLastPublisher());
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            getMessageBox().addError("Eccezione imprevista nel salvataggio del file CSV dei parametri.");
        }
    }

    @Override
    public void cessaVersatore() throws EMFError {
        try {
            BigDecimal idVers = getForm().getVers().getId_vers().parse();
            PigVersRowBean versRB = amministrazioneEjb.getPigVersRowBean(idVers);
            BigDecimal idEnteSiam = versRB.getIdEnteConvenz();
            String flArchivioRestituito = versRB.getFlArchivioRestituito();
            // Se il versatore è di tipologia PRODUTTORE (in quanto se è FORNITORE_ESTERNO non può essere cessato
            // sì accordo valido, non posso cessare
            // no accordo valido, posso cessare
            if (idEnteSiam != null) {
                if (amministrazioneEjb.notExistAccordoValido(idEnteSiam)) {
                    if (flArchivioRestituito != null && flArchivioRestituito.equals("1")) {
                        /*
                         * Codice aggiuntivo per il logging...
                         */
                        class Local {
                        }
                        String nomeMetodo = Local.class.getEnclosingMethod().getName();
                        LogParam param = SpagoliteLogUtil.getLogParam(null, getUser().getUsername(),
                                SpagoliteLogUtil.getPageName(this), SpagoliteLogUtil.getButtonActionName(this.getForm(),
                                        this.getForm().getVers(), nomeMetodo));
                        param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
                        amministrazioneEjb.cessaVersatore(param, idVers);
                        getMessageBox().addInfo("Versatore cessato con successo");
                        aggiornaDettaglioVersatore(idVers);
                    } else {
                        getMessageBox().addError(
                                "E’ possibile cessare un versatore solo dopo aver eseguito la restituzione dell’archivio");
                    }
                } else {
                    getMessageBox().addError(
                            "E’ possibile cessare un versatore solo se il suo ente di appartenenza non ha un accordo valido");
                }
            } else {
                getMessageBox().addError("Non è possibile cessare un versatore diverso da PRODUTTORE");
            }
        } catch (ParerUserError e) {
            getMessageBox().addError(e.getDescription());
        }
        forwardToPublisher(Publisher.VERS_DETAIL);
    }

    // MEV 27543
    @Override
    public JSONObject triggerVisVersNm_ambiente_ente_convenzOnTrigger() throws EMFError {
        getForm().getVisVers().post(getRequest());
        String nmAmbienteEnteConvenz = getForm().getVisVers().getNm_ambiente_ente_convenz().parse();
        if (nmAmbienteEnteConvenz != null) {
            BaseTable pigVRicVersEnteIamTableBean = amministrazioneEjb
                    .getPigVRicVersEnteIamTableBean(nmAmbienteEnteConvenz, getUser().getIdUtente());

            DecodeMap mappaEntiIam = new DecodeMap();
            mappaEntiIam.populatedMap(pigVRicVersEnteIamTableBean, "nm_ente_convenz", "nm_ente_convenz");
            getForm().getVisVers().getNm_ente_convenz().setDecodeMap(mappaEntiIam);
        } else {
            getForm().getVisVers().getNm_ente_convenz().setDecodeMap(new DecodeMap());
        }
        return getForm().getVisVers().asJSON();
    }

    private PigParamApplicTableBean obfuscatePasswordParamApplic(PigParamApplicTableBean paramApplicTableBean) {
        // MEV22933 - offusca le password
        Iterator<PigParamApplicRowBean> rowIt = paramApplicTableBean.iterator();
        while (rowIt.hasNext()) {
            PigParamApplicRowBean rowBean = rowIt.next();
            if (rowBean.getTiValoreParamApplic().equals(ComboValueParamentersType.PASSWORD.name())) {
                rowBean.setString("ds_valore_param_applic", Constants.OBFUSCATED_STRING);

                if (rowBean.getString("ds_valore_param_applic_applic") != null) {
                    rowBean.setString("ds_valore_param_applic_applic", Constants.OBFUSCATED_STRING);
                }

                if (rowBean.getString("ds_valore_param_applic_ambiente") != null) {
                    rowBean.setString("ds_valore_param_applic_ambiente", Constants.OBFUSCATED_STRING);
                }

                if (rowBean.getString("ds_valore_param_applic_vers") != null) {
                    rowBean.setString("ds_valore_param_applic_vers", Constants.OBFUSCATED_STRING);
                }

                if (rowBean.getString("ds_valore_param_applic_vers_amm") != null) {
                    rowBean.setString("ds_valore_param_applic_vers_amm", Constants.OBFUSCATED_STRING);
                }

                if (rowBean.getString("ds_valore_param_applic_vers_gest") != null) {
                    rowBean.setString("ds_valore_param_applic_vers_gest", Constants.OBFUSCATED_STRING);
                }

                if (rowBean.getString("ds_valore_param_applic_vers_cons") != null) {
                    rowBean.setString("ds_valore_param_applic_vers_cons", Constants.OBFUSCATED_STRING);
                }

                if (rowBean.getString("ds_valore_param_applic_ambiente_amm") != null) {
                    rowBean.setString("ds_valore_param_applic_ambiente_amm", Constants.OBFUSCATED_STRING);
                }

                if (rowBean.getString("ds_valore_param_applic_ambiente_gest") != null) {
                    rowBean.setString("ds_valore_param_applic_ambiente_gest", Constants.OBFUSCATED_STRING);
                }

                if (rowBean.getString("ds_valore_param_applic_ambiente_cons") != null) {
                    rowBean.setString("ds_valore_param_applic_ambiente_cons", Constants.OBFUSCATED_STRING);
                }

                if (rowBean.getString("ds_valore_param_applic_tipo_oggetto_amm") != null) {
                    rowBean.setString("ds_valore_param_applic_tipo_oggetto_amm", Constants.OBFUSCATED_STRING);
                }

                if (rowBean.getString("ds_valore_param_applic_tipo_oggetto_gest") != null) {
                    rowBean.setString("ds_valore_param_applic_tipo_oggetto_gest", Constants.OBFUSCATED_STRING);
                }

                if (rowBean.getString("ds_valore_param_applic_tipo_oggetto_cons") != null) {
                    rowBean.setString("ds_valore_param_applic_tipo_oggetto_cons", Constants.OBFUSCATED_STRING);
                }
            }
        }

        return paramApplicTableBean;
    }
}
