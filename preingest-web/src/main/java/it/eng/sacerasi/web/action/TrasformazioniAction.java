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

import static org.apache.tika.metadata.TikaCoreProperties.RESOURCE_NAME_KEY;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.ejb.EJB;

import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.detect.Detector;
import org.apache.tika.exception.TikaException;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.parer.kettle.exceptions.KettleException;
import it.eng.parer.kettle.exceptions.KettleServiceException;
import it.eng.parer.kettle.model.EsitoStatusCodaTrasformazione;
import it.eng.parer.kettle.model.StatoTrasformazione;
import it.eng.sacerasi.entity.XfoTrasf;
import it.eng.sacerasi.exception.ParerUserError;
import it.eng.sacerasi.slite.gen.Application;
import it.eng.sacerasi.slite.gen.action.TrasformazioniAbstractAction;
import it.eng.sacerasi.slite.gen.form.AmministrazioneForm;
import it.eng.sacerasi.slite.gen.form.TrasformazioniForm;
import it.eng.sacerasi.slite.gen.tablebean.PigTipoObjectRowBean;
import it.eng.sacerasi.slite.gen.tablebean.PigTipoObjectTableBean;
import it.eng.sacerasi.slite.gen.tablebean.PigVersRowBean;
import it.eng.sacerasi.slite.gen.tablebean.PigVersTableBean;
import it.eng.sacerasi.slite.gen.tablebean.XfoParamTrasfRowBean;
import it.eng.sacerasi.slite.gen.tablebean.XfoParamTrasfTableBean;
import it.eng.sacerasi.slite.gen.tablebean.XfoSetParamTrasfRowBean;
import it.eng.sacerasi.slite.gen.tablebean.XfoSetParamTrasfTableBean;
import it.eng.sacerasi.slite.gen.tablebean.XfoStoricoTrasfRowBean;
import it.eng.sacerasi.slite.gen.tablebean.XfoStoricoTrasfTableBean;
import it.eng.sacerasi.slite.gen.tablebean.XfoTrasfRowBean;
import it.eng.sacerasi.slite.gen.tablebean.XfoTrasfTableBean;
import it.eng.sacerasi.web.ejb.AmministrazioneEjb;
import it.eng.sacerasi.web.helper.ConfigurationHelper;
import it.eng.sacerasi.web.validator.TypeValidator;
import it.eng.spagoCore.error.EMFError;
import it.eng.spagoLite.SessionManager;
import it.eng.spagoLite.actions.form.ListAction;
import it.eng.spagoLite.db.base.BaseRowInterface;
import it.eng.spagoLite.db.base.BaseTableInterface;
import it.eng.spagoLite.db.oracle.decode.DecodeMap;
import it.eng.spagoLite.form.base.BaseElements;
import it.eng.spagoLite.security.Secure;
import it.eng.xformer.dto.RicercaTrasformazioneBean;
import it.eng.xformer.helper.TrasformazioniHelper;
import it.eng.xformer.kettle.ejb.ParametersManager;
import it.eng.xformer.kettle.ejb.RepositoryManagerEjb;
import it.eng.xformer.web.util.ComboGetter;
import it.eng.xformer.web.util.Constants;
import it.eng.xformer.web.util.WebConstants;

/**
 * @author cek
 */
public class TrasformazioniAction extends TrasformazioniAbstractAction {

    private static final Logger logger = LoggerFactory.getLogger(TrasformazioniAction.class.getName());

    @EJB(mappedName = "java:app/SacerAsync-ejb/ConfigurationHelper")
    private ConfigurationHelper configurationHelper;

    @EJB(mappedName = "java:app/SacerAsync-ejb/RepositoryManagerEjb")
    private RepositoryManagerEjb repositoryManager;

    @EJB(mappedName = "java:app/SacerAsync-ejb/ParametersManager")
    private ParametersManager parametersManager;

    @EJB(mappedName = "java:app/SacerAsync-ejb/TrasformazioniHelper")
    private TrasformazioniHelper trasformazioniHelper;

    @EJB(mappedName = "java:app/SacerAsync-ejb/AmministrazioneEjb")
    private AmministrazioneEjb amministrazioneEjb;

    @Override
    public void initOnClick() throws EMFError {
        throw new UnsupportedOperationException("Not supported yet."); // To change body of generated methods, choose
        // Tools | Templates.
    }

    @Override
    public void insertDettaglio() throws EMFError {
        // segnala che arriviamo dall'inserimento e NON dobbiamo mostrare il tasto "indietro"
        getSession().setAttribute("navTableTrasformazioni", "disabled");

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        Calendar dIstituz = new GregorianCalendar();
        Calendar dSoppres = new GregorianCalendar(2444, Calendar.DECEMBER, 31);

        // setta al momento corrente
        dIstituz.setTime(new Date());

        // prepara la maschera con ora e data corrette
        getForm().getInserisciTrasformazione().reset();
        getForm().getInserisciTrasformazione().getDt_istituz().setValue(sdf.format(dIstituz.getTime()));
        getForm().getInserisciTrasformazione().getDt_soppres().setValue(sdf.format(dSoppres.getTime()));
        getForm().getInserisciTrasformazione().getOre_dt_ist()
                .setValue(String.format("%02d", dIstituz.get(Calendar.HOUR_OF_DAY)));
        getForm().getInserisciTrasformazione().getMinuti_dt_ist()
                .setValue(String.format("%02d", dIstituz.get(Calendar.MINUTE)));
        getForm().getInserisciTrasformazione().getOre_dt_sop()
                .setValue(String.format("%02d", dSoppres.get(Calendar.HOUR_OF_DAY)));
        getForm().getInserisciTrasformazione().getMinuti_dt_sop()
                .setValue(String.format("%02d", dSoppres.get(Calendar.MINUTE)));

        getForm().getInserisciTrasformazione().setEditMode();
        getForm().getInserisciTrasformazione().setStatus(BaseElements.Status.insert);

        forwardToPublisher(Application.Publisher.INSERIMENTO_TRASFORMAZIONI);
    }

    @Override
    public void loadDettaglio() throws EMFError {
        String navigationEvent = getNavigationEvent();
        if (navigationEvent.equals(ListAction.NE_DETTAGLIO_VIEW)
                || navigationEvent.equals(ListAction.NE_DETTAGLIO_UPDATE)
                || navigationEvent.equals(ListAction.NE_DETTAGLIO_CANCEL) || navigationEvent.equals(ListAction.NE_NEXT)
                || navigationEvent.equals(ListAction.NE_PREV)) {

            // Controllo per quale tabella è stato invocato il metodo
            String lista = getTableName();
            if (lista != null) {
                if (lista.equals(getForm().getTrasformazioniList().getName())) {
                    try {
                        BigDecimal idTrasf = getForm().getTrasformazioniList().getTable().getCurrentRow()
                                .getBigDecimal("id_trasf");

                        XfoTrasfRowBean xfoTrasRowBean = repositoryManager.getXfoTrasRowBean(idTrasf.longValue());
                        reloadTransformationDetail(xfoTrasRowBean);

                        // segnala che arriviamo dalla ricerca e dobbiamo mostrare il tasto "indietro"
                        getSession().setAttribute("navTableTrasformazioni",
                                getForm().getTrasformazioniList().getName());

                        // se ci spostiamo lungo una lista dobbiamo sempre tornare in view mode
                        getForm().getTrasformazioniList().setStatus(BaseElements.Status.view);
                        getForm().getTransformationDetail().setStatus(BaseElements.Status.view);
                        getForm().getTransformationDetail().setViewMode();

                        // se la trasformazione non ha un ID Kettle, non deve essere possibile inserire una nuova
                        // versione
                        if (xfoTrasRowBean.getBlTrasf() == null) {
                            getForm().getTransformationDetail().getDownloadPkg().setViewMode();
                            getForm().getTransformationDetail().getInsertParametersSet().setViewMode();
                            getForm().getTransformationDetail().getInsertDefaultParametersSet().setViewMode();
                            getForm().getTransformationDetail().getUpdateParametersSets().setViewMode();
                            getForm().getTransformationDetail().getInsertNewVersion().setViewMode();
                        } else {
                            getForm().getTransformationDetail().getDownloadPkg().setEditMode();
                            getForm().getTransformationDetail().getInsertParametersSet().setEditMode();
                            getForm().getTransformationDetail().getInsertDefaultParametersSet().setEditMode();
                            getForm().getTransformationDetail().getInsertNewVersion().setEditMode();
                            getForm().getTransformationDetail().getUpdateParametersSets().setEditMode();
                        }

                    } catch (ParerUserError ex) {
                        getMessageBox().addError(ex.getDescription());
                    }
                } else if (lista.equals(getForm().getTransformationDetail().getName())) {
                    try {
                        BigDecimal idTrasf = getForm().getTransformationDetail().getId_trasf().parse();

                        XfoTrasfRowBean xfoTrasRowBean = repositoryManager.getXfoTrasRowBean(idTrasf.longValue());
                        reloadTransformationDetail(xfoTrasRowBean);

                        // se ci spostiamo lungo una lista dobbiamo sempre tornare in view mode
                        getForm().getTrasformazioniList().setStatus(BaseElements.Status.view);
                        getForm().getTransformationDetail().setStatus(BaseElements.Status.view);
                        getForm().getTransformationDetail().setViewMode();

                        // se la trasformazione non ha un ID Kettle, non deve essere possibile inserire una nuova
                        // versione
                        if (xfoTrasRowBean.getBlTrasf() == null) {
                            getForm().getTransformationDetail().getDownloadPkg().setViewMode();
                            getForm().getTransformationDetail().getInsertParametersSet().setViewMode();
                            getForm().getTransformationDetail().getInsertDefaultParametersSet().setViewMode();
                            getForm().getTransformationDetail().getInsertNewVersion().setViewMode();
                        } else {
                            getForm().getTransformationDetail().getDownloadPkg().setEditMode();
                            getForm().getTransformationDetail().getInsertParametersSet().setEditMode();
                            getForm().getTransformationDetail().getInsertDefaultParametersSet().setEditMode();
                            getForm().getTransformationDetail().getInsertNewVersion().setEditMode();
                        }

                    } catch (ParerUserError ex) {
                        getMessageBox().addError(ex.getDescription());
                    }
                } else if (lista.equals(getForm().getParametersSetList().getName())) {
                    try {
                        BigDecimal idParamsSet = getForm().getParametersSetList().getTable().getCurrentRow()
                                .getBigDecimal("id_set_param_trasf");
                        reloadParameterSetDetail(idParamsSet);

                        // se ci spostiamo lungo una lista dobbiamo sempre tornare in view mode
                        if (navigationEvent.equals(ListAction.NE_DETTAGLIO_VIEW)
                                || navigationEvent.equals(ListAction.NE_NEXT)
                                || navigationEvent.equals(ListAction.NE_PREV)) {
                            getForm().getParametersSetDetail().setStatus(BaseElements.Status.view);
                        }

                        getForm().getParametersSetDetail().setViewMode();
                        getForm().getParametersSetDetail().getAddParameter().setEditMode();
                        getForm().getInserimentoParametro().setEditMode();
                        getForm().getParametersSetDetail().getUpdateParametersSetsFromSetDetail().setEditMode();

                    } catch (ParerUserError ex) {
                        getMessageBox().addError(ex.getDescription());
                    }
                }
            }
        }
    }

    @Override
    public void undoDettaglio() throws EMFError {
        if (getLastPublisher().equals(Application.Publisher.DETTAGLIO_TRASFORMAZIONE)) {
            loadDettaglio();
        } else if (getLastPublisher().equals(Application.Publisher.DETTAGLIO_SET_PARAMETRI)) {
            loadDettaglio();
        }
    }

    @Override
    public void saveDettaglio() throws EMFError {
        String lista = getTableName();
        if (lista != null) {
            if (lista.equals(getForm().getTrasformazioniList().getName())) {
                if (getForm().getTransformationDetail().validate(getMessageBox())) {
                    saveDettaglioTransormationsList();
                }

                if (!getMessageBox().hasError()) {
                    getForm().getTrasformazioniList().setStatus(BaseElements.Status.view);
                    getForm().getTransformationDetail().setStatus(BaseElements.Status.view);
                    getForm().getTransformationDetail().setViewMode();

                    // se la trasformazione non ha un ID Kettle, non deve essere possibile inserire una nuova versione
                    // FIXME se torno da una modifica dove non ho caricato un file la condizione sotto fallisce sempre
                    // anceh se la trasformazione ha già un blob attaccato.
                    if (getForm().getInserisciTrasformazione().getTrans_blob().getFileBytes() == null) {
                        getForm().getTransformationDetail().getDownloadPkg().setViewMode();
                        getForm().getTransformationDetail().getInsertParametersSet().setViewMode();
                        getForm().getTransformationDetail().getInsertDefaultParametersSet().setViewMode();
                        getForm().getTransformationDetail().getUpdateParametersSets().setViewMode();
                        getForm().getTransformationDetail().getInsertNewVersion().setViewMode();
                    } else {
                        getForm().getTransformationDetail().getDownloadPkg().setEditMode();
                        getForm().getTransformationDetail().getInsertParametersSet().setEditMode();
                        getForm().getTransformationDetail().getInsertDefaultParametersSet().setEditMode();
                        getForm().getTransformationDetail().getUpdateParametersSets().setEditMode();
                        getForm().getTransformationDetail().getInsertNewVersion().setEditMode();
                    }

                    forwardToPublisher(Application.Publisher.DETTAGLIO_TRASFORMAZIONE);
                } else {
                    forwardToPublisher(getLastPublisher());
                }
            } else if (lista.equals(getForm().getInserimentoSetParametri().getName())
                    && getForm().getInserimentoSetParametri().getStatus() == BaseElements.Status.insert) {
                if (getForm().getInserimentoSetParametri().validate(getMessageBox())) {
                    saveDettaglioInsertSetParameters();
                }

                if (!getMessageBox().hasError()) {
                    getForm().getInserimentoSetParametri().setStatus(BaseElements.Status.view);
                    getForm().getInserimentoSetParametri().setViewMode();
                    getForm().getParametersSetDetail().setStatus(BaseElements.Status.view);
                    getForm().getParametersSetDetail().setViewMode();
                    getForm().getParametersSetDetail().getAddParameter().setEditMode();

                    getForm().getInserimentoParametro().setEditMode();

                    forwardToPublisher(Application.Publisher.DETTAGLIO_SET_PARAMETRI);
                } else {
                    forwardToPublisher(getLastPublisher());
                }
            } else if (lista.equals(getForm().getParametersSetDetail().getName())) {
                if (getForm().getParametersSetDetail().postAndValidate(getRequest(), getMessageBox())) {
                    saveDettaglioParametersSetDetail();
                }

                if (!getMessageBox().hasError()) {
                    getForm().getInserimentoSetParametri().setStatus(BaseElements.Status.view);
                    getForm().getInserimentoSetParametri().setViewMode();
                    getForm().getParametersSetDetail().setStatus(BaseElements.Status.view);
                    getForm().getParametersSetDetail().setViewMode();
                    getForm().getParametersSetDetail().getAddParameter().setEditMode();

                    forwardToPublisher(Application.Publisher.DETTAGLIO_SET_PARAMETRI);
                } else {
                    forwardToPublisher(getLastPublisher());
                }

            } else if (lista.equals(getForm().getTransformationDetail().getName())) {
                if (getForm().getTransformationDetail().validate(getMessageBox())) {
                    saveDettaglioTransformationDetail();
                }

                if (!getMessageBox().hasError()) {
                    getForm().getTransformationDetail().setStatus(BaseElements.Status.view);
                    getForm().getTransformationDetail().setViewMode();

                    // se la trasformazione non ha un ID Kettle, non deve essere possibile inserire una nuova versione
                    if (getForm().getTransformationDetail().getTrans_blob().getFileBytes() == null) {
                        getForm().getTransformationDetail().getDownloadPkg().setViewMode();
                        getForm().getTransformationDetail().getInsertParametersSet().setViewMode();
                        getForm().getTransformationDetail().getInsertDefaultParametersSet().setViewMode();
                        getForm().getTransformationDetail().getInsertNewVersion().setViewMode();
                    } else {
                        getForm().getTransformationDetail().getDownloadPkg().setEditMode();
                        getForm().getTransformationDetail().getInsertParametersSet().setEditMode();
                        getForm().getTransformationDetail().getInsertDefaultParametersSet().setEditMode();
                        getForm().getTransformationDetail().getInsertNewVersion().setEditMode();
                    }
                }

                forwardToPublisher(Application.Publisher.DETTAGLIO_TRASFORMAZIONE);

            } else if (lista.equals(getForm().getInserimentoNuovaVersione().getName())) {
                if (getForm().getInserimentoNuovaVersione().validate(getMessageBox())) {
                    saveDettaglioInserimentoNuovaVersione();
                }

                if (!getMessageBox().hasError()) {
                    getForm().getTrasformazioniList().setStatus(BaseElements.Status.view);
                    getForm().getTransformationDetail().setViewMode();
                    getForm().getTransformationDetail().getDownloadPkg().setEditMode();
                    getForm().getTransformationDetail().getInsertParametersSet().setEditMode();
                    getForm().getTransformationDetail().getInsertDefaultParametersSet().setEditMode();
                    getForm().getTransformationDetail().getInsertNewVersion().setEditMode();

                    forwardToPublisher(Application.Publisher.DETTAGLIO_TRASFORMAZIONE);
                } else {
                    forwardToPublisher(getLastPublisher());
                }
            } else if (lista.equals(getForm().getInserisciTrasformazione().getName())
                    && getForm().getInserisciTrasformazione().getStatus() == BaseElements.Status.insert) {
                if (getForm().getInserisciTrasformazione().validate(getMessageBox())) {
                    saveDettaglioInserimentoNuovaTrasformazione();
                }

                if (!getMessageBox().hasError()) {
                    getForm().getTransformationDetail().setStatus(BaseElements.Status.view);
                    getForm().getTransformationDetail().setViewMode();

                    // se la trasformazione non ha un ID Kettle, non deve essere possibile inserire una nuova versione
                    if (getForm().getTransformationDetail().getTrans_blob().getFileBytes() == null) {
                        getForm().getTransformationDetail().getDownloadPkg().setViewMode();
                        getForm().getTransformationDetail().getInsertParametersSet().setViewMode();
                        getForm().getTransformationDetail().getInsertDefaultParametersSet().setViewMode();
                        getForm().getTransformationDetail().getInsertNewVersion().setViewMode();
                    } else {
                        getForm().getTransformationDetail().getDownloadPkg().setEditMode();
                        getForm().getTransformationDetail().getInsertParametersSet().setEditMode();
                        getForm().getTransformationDetail().getInsertDefaultParametersSet().setEditMode();
                        getForm().getTransformationDetail().getInsertNewVersion().setEditMode();
                    }

                    forwardToPublisher(Application.Publisher.DETTAGLIO_TRASFORMAZIONE);
                } else {
                    forwardToPublisher(getLastPublisher());
                }
            }
        }
    }

    @Override
    public void dettaglioOnClick() throws EMFError {
        String navigationEvent = getNavigationEvent();
        if (navigationEvent.equals(ListAction.NE_DETTAGLIO_VIEW) || navigationEvent.equals(ListAction.NE_NEXT)
                || navigationEvent.equals(ListAction.NE_PREV)) {
            String lista = getTableName();
            if (lista != null) {
                if (lista.equals(getForm().getTrasformazioniList().getName())) {
                    forwardToPublisher(Application.Publisher.DETTAGLIO_TRASFORMAZIONE);
                } else if (lista.equals(getForm().getParametersSetList().getName())) {
                    forwardToPublisher(Application.Publisher.DETTAGLIO_SET_PARAMETRI);
                }
            }
        }
    }

    @Override
    public void elencoOnClick() throws EMFError {
        goBack();
    }

    @Override
    protected String getDefaultPublsherName() {
        return Application.Publisher.DETTAGLIO_TRASFORMAZIONE;
    }

    @Override
    public void process() throws EMFError {
        boolean isMultipart = ServletFileUpload.isMultipartContent(getRequest());
        if (isMultipart) {
            String[] a = null;
            String dim = configurationHelper.getValoreParamApplicByApplic(Constants.DIM_MAX_FILE_DA_VERSARE);
            BigDecimal size = new BigDecimal(dim);

            try {
                if (getLastPublisher().equals(Application.Publisher.DETTAGLIO_TRASFORMAZIONE)) {
                    a = getForm().getTransformationDetail().postMultipart(getRequest(), size.intValue());
                } else if (getLastPublisher().equals(Application.Publisher.INSERIMENTO_SET_PARAMETRI)) {
                    a = getForm().getInserimentoSetParametri().postMultipart(getRequest(), size.intValue());
                } else if (getLastPublisher().equals(Application.Publisher.INSERIMENTO_NUOVA_VERSIONE)) {
                    a = getForm().getInserimentoNuovaVersione().postMultipart(getRequest(), size.intValue());
                } else if (getLastPublisher().equals(Application.Publisher.INSERIMENTO_TRASFORMAZIONI)) {
                    a = getForm().getInserisciTrasformazione().postMultipart(getRequest(), size.intValue());
                }
            } catch (FileUploadException | SecurityException | IllegalArgumentException ex) {
                logger.error("Errore nella processazione del form: " + ExceptionUtils.getRootCauseMessage(ex), ex);
                getMessageBox().addError(ExceptionUtils.getRootCauseMessage(ex));
                forwardToPublisher(getLastPublisher());
            }

            if (a != null) {
                String operationMethod = a[0];
                String[] navigationParams = Arrays.copyOfRange(a, 1, a.length);
                try {
                    if (navigationParams != null && navigationParams.length > 0) {
                        Method method = TrasformazioniAction.class.getMethod(operationMethod, String[].class);
                        method.invoke(this, (Object) navigationParams);
                    } else {
                        Method method = TrasformazioniAction.class.getMethod(operationMethod);
                        method.invoke(this);
                    }
                } catch (IllegalAccessException | IllegalArgumentException | NoSuchMethodException | SecurityException
                        | InvocationTargetException ex) {
                    logger.error("Errore nella processazione del form: " + ExceptionUtils.getRootCauseMessage(ex), ex);
                    getMessageBox().addError(ExceptionUtils.getRootCauseMessage(ex));
                    forwardToPublisher(getLastPublisher());
                }
            }
        }
    }

    @Override
    public void reloadAfterGoBack(String publisherName) {
        try {
            if (publisherName.equals(Application.Publisher.RICERCA_TRASFORMAZIONI)) {
                RicercaTrasformazioneBean bean = new RicercaTrasformazioneBean(
                        getForm().getFiltriRicercaTrasformazioni());

                // TODO aggiungere la validazione dei campi (vedi TypeValidator.java)
                // e riempire il messageBox per il controllo sotto.
                if (!getMessageBox().hasError()) {
                    try {
                        XfoTrasfTableBean ricercaTrasformazioniTableBean = repositoryManager
                                .getRicercaTrasformazioniTableBean(bean);
                        getForm().getTrasformazioniList().setTable(ricercaTrasformazioniTableBean);
                        getForm().getTrasformazioniList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
                        getForm().getTrasformazioniList().getTable().first();
                    } catch (ParerUserError ex) {
                        getMessageBox().addError(ex.getDescription());
                    }
                }
            }
            if (publisherName.equals(Application.Publisher.DETTAGLIO_TRASFORMAZIONE)) {
                try {
                    BigDecimal idTrasf = getForm().getTrasformazioniList().getTable().getCurrentRow()
                            .getBigDecimal("id_trasf");

                    XfoTrasfRowBean xfoTrasRowBean = repositoryManager.getXfoTrasRowBean(idTrasf.longValue());
                    reloadTransformationDetail(xfoTrasRowBean);
                } catch (ParerUserError ex) {
                    getMessageBox().addError(ex.getDescription());
                }
            }
        } catch (EMFError ex) {
            logger.error("Errore nel ricaricamento del dettaglio serie", ex);
            getMessageBox().addError("Errore nel ricaricamento del dettaglio serie");
        }
    }

    @Override
    public String getControllerName() {
        return Application.Actions.TRASFORMAZIONI;
    }

    @Override
    public void ricercaTrasformazione() throws EMFError {
        if (getForm().getFiltriRicercaTrasformazioni().postAndValidate(getRequest(), getMessageBox())) {
            RicercaTrasformazioneBean bean = new RicercaTrasformazioneBean(getForm().getFiltriRicercaTrasformazioni());

            // TODO aggiungere la validazione dei campi (vedi TypeValidator.java)
            // e riempire il messageBox per il controllo sotto.
            if (!getMessageBox().hasError()) {
                try {
                    XfoTrasfTableBean ricercaTrasformazioniTableBean = repositoryManager
                            .getRicercaTrasformazioniTableBean(bean);
                    getForm().getTrasformazioniList().setTable(ricercaTrasformazioniTableBean);
                    getForm().getTrasformazioniList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
                    getForm().getTrasformazioniList().getTable().first();
                } catch (ParerUserError ex) {
                    getMessageBox().addError(ex.getDescription());
                }
            }
        }

        forwardToPublisher(Application.Publisher.RICERCA_TRASFORMAZIONI);
    }

    @Secure(action = "Menu.AmministrazioneTrasformazioni.ricercaTrasformazioni")
    public void ricercaTrasformazioni() throws EMFError {
        getUser().getMenu().reset();
        getUser().getMenu().select("Menu.AmministrazioneTrasformazioni.ricercaTrasformazioni");

        getSession().removeAttribute("navTableTrasformazioni");

        getForm().getFiltriRicercaTrasformazioni().reset();

        getForm().getFiltriRicercaTrasformazioni().getFl_attiva_search()
                .setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());
        getForm().getFiltriRicercaTrasformazioni().setEditMode();

        if (getForm().getTrasformazioniList().getTable() != null) {
            getForm().getTrasformazioniList().setTable(null);
        }

        forwardToPublisher(Application.Publisher.RICERCA_TRASFORMAZIONI);
    }

    @Override
    public void downloadPkg() throws EMFError {
        XfoTrasfRowBean xfoTrasRowBean = null;
        try {
            BigDecimal idTrasf = getForm().getTransformationDetail().getId_trasf().parse();

            xfoTrasRowBean = repositoryManager.getXfoTrasRowBean(idTrasf.longValue());
            getForm().getTransformationDetail().copyFromBean(xfoTrasRowBean);

        } catch (ParerUserError ex) {
            getMessageBox().addError(ex.getDescription());
        }

        if (!getMessageBox().hasError()) {
            String cdTrasf = getForm().getTransformationDetail().getCd_trasf().parse();
            String version = getForm().getTransformationDetail().getCd_versione_cor().parse();
            String filename = cdTrasf.replace(' ', '_') + "-" + version.replace(' ', '_') + ".zip";

            File tmpFile = new File(System.getProperty("java.io.tmpdir"), filename);
            FileOutputStream file = null;
            InputStream is = null;
            try {
                byte[] fileBytes = xfoTrasRowBean.getBlTrasf();
                if (fileBytes != null) {

                    file = new FileOutputStream(tmpFile);
                    is = new ByteArrayInputStream(fileBytes);
                    byte[] data = new byte[1024];
                    int count;
                    while ((count = is.read(data, 0, 1024)) != -1) {
                        file.write(data, 0, count);
                    }

                    getRequest().setAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_ACTION.name(), getControllerName());
                    getSession().setAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_FILENAME.name(), tmpFile.getName());
                    getSession().setAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_FILEPATH.name(), tmpFile.getPath());
                    getSession().setAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_DELETEFILE.name(),
                            Boolean.toString(true));
                    getSession().setAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_CONTENTTYPE.name(),
                            "application/zip");
                }
            } catch (IOException ex) {
                logger.error("Errore in download " + ExceptionUtils.getRootCauseMessage(ex), ex);
                getMessageBox().addError("Errore inatteso nella preparazione del download");
            } finally {
                IOUtils.closeQuietly(is);
                IOUtils.closeQuietly(file);
            }

            if (getMessageBox().hasError()) {
                forwardToPublisher(getLastPublisher());
            } else {
                forwardToPublisher(Application.Publisher.DOWNLOAD_PAGE);
            }
        }
    }

    @Override
    public void updateTrasformazioniList() throws EMFError {
        TrasformazioniForm.TransformationDetail transformationDetail = getForm().getTransformationDetail();
        transformationDetail.getCd_trasf().setEditMode();
        transformationDetail.getDs_trasf().setEditMode();
        transformationDetail.getCd_versione_cor().setEditMode();
        transformationDetail.getDs_versione_cor().setEditMode();
        transformationDetail.getFl_attiva().setEditMode();
        transformationDetail.getDt_istituz().setEditMode();
        transformationDetail.getDt_soppres().setEditMode();
        transformationDetail.getOre_dt_ist().setEditMode();
        transformationDetail.getMinuti_dt_ist().setEditMode();
        transformationDetail.getOre_dt_sop().setEditMode();
        transformationDetail.getMinuti_dt_sop().setEditMode();
        transformationDetail.getTrans_blob().setEditMode();
        transformationDetail.getDownloadPkg().setViewMode();
        transformationDetail.getInsertParametersSet().setViewMode();
        transformationDetail.getInsertDefaultParametersSet().setViewMode();
        transformationDetail.getUpdateParametersSets().setViewMode();
        transformationDetail.getInsertNewVersion().setViewMode();

        getForm().getTrasformazioniList().setStatus(BaseElements.Status.update);

        forwardToPublisher(getDefaultPublsherName());
    }

    @Override
    public void updateTransformationDetail() throws EMFError {
        TrasformazioniForm.TransformationDetail transformationDetail = getForm().getTransformationDetail();
        transformationDetail.getCd_trasf().setEditMode();
        transformationDetail.getDs_trasf().setEditMode();
        transformationDetail.getCd_versione_cor().setEditMode();
        transformationDetail.getDs_versione_cor().setEditMode();
        transformationDetail.getFl_attiva().setEditMode();
        transformationDetail.getDt_istituz().setEditMode();
        transformationDetail.getDt_soppres().setEditMode();
        transformationDetail.getOre_dt_ist().setEditMode();
        transformationDetail.getMinuti_dt_ist().setEditMode();
        transformationDetail.getOre_dt_sop().setEditMode();
        transformationDetail.getMinuti_dt_sop().setEditMode();
        transformationDetail.getTrans_blob().setEditMode();
        transformationDetail.getCd_kettle_id().setViewMode();
        transformationDetail.getDownloadPkg().setViewMode();
        transformationDetail.getInsertParametersSet().setViewMode();
        transformationDetail.getInsertDefaultParametersSet().setViewMode();
        transformationDetail.getUpdateParametersSets().setViewMode();
        transformationDetail.getInsertNewVersion().setViewMode();

        getForm().getTransformationDetail().setStatus(BaseElements.Status.update);

        forwardToPublisher(getDefaultPublsherName());
    }

    @Override
    public void updateInserisciTrasformazione() throws EMFError {
        // FIXME: si può togliere credo.
        TrasformazioniForm.InserisciTrasformazione transformationDetail = getForm().getInserisciTrasformazione();
        transformationDetail.getTrans_name().setEditMode();
        transformationDetail.getTrans_description().setEditMode();
        transformationDetail.getTrans_version().setEditMode();
        transformationDetail.getTrans_version_description().setEditMode();
        transformationDetail.getTrans_enabled().setEditMode();
        transformationDetail.getDt_istituz().setEditMode();
        transformationDetail.getDt_soppres().setEditMode();
        transformationDetail.getOre_dt_ist().setEditMode();
        transformationDetail.getMinuti_dt_ist().setEditMode();
        transformationDetail.getOre_dt_sop().setEditMode();
        transformationDetail.getMinuti_dt_sop().setEditMode();
        transformationDetail.getCd_kettle_id().setViewMode();
        transformationDetail.getTrans_blob().setEditMode();
    }

    @Override
    public void updateParametersSetList() throws EMFError {
        TrasformazioniForm.ParametersSetDetail parametersSetDetail = getForm().getParametersSetDetail();
        parametersSetDetail.getNm_set_param_trasf().setEditMode();
        parametersSetDetail.getDs_set_param_trasf().setEditMode();
        parametersSetDetail.getFl_set_param_ark().setEditMode();

        parametersSetDetail.getNm_xfo_trasf().setViewMode();
        parametersSetDetail.getAddParameter().setViewMode();

        getForm().getParametersSetDetail().setStatus(BaseElements.Status.update);

        forwardToPublisher(Application.Publisher.DETTAGLIO_SET_PARAMETRI);
    }

    @Override
    public void updateParametersSetDetail() throws EMFError {
        updateParametersSetList();
    }

    private void deleteTransformation() throws EMFError {
        BigDecimal idTrasf = getForm().getTransformationDetail().getId_trasf().parse();

        String lista = getTableName();
        if (lista != null && getTableName().equals(getForm().getTrasformazioniList().getName())) {
            XfoTrasfRowBean currentRow = (XfoTrasfRowBean) getForm().getTrasformazioniList().getTable().getCurrentRow();
            idTrasf = currentRow.getIdTrasf();
        }

        if (idTrasf != null) {
            try {
                boolean result = repositoryManager.deleteTransformation(idTrasf.longValue());
                if (result) {
                    getMessageBox().addInfo("Trasformazione eliminata con successo");
                    // se arrivo dalla ricerca e non dall'inserimento devo eliminare il risultato dalla tabella.
                    if (lista != null && getTableName().equals(getForm().getTrasformazioniList().getName())) {
                        int riga = getForm().getTrasformazioniList().getTable().getCurrentRowIndex();
                        getForm().getTrasformazioniList().getTable().remove(riga);
                    }
                } else {
                    getMessageBox().addInfo("Impossibile eliminare una trasformazione assegnata ad un tipo oggetto.");
                }
            } catch (ParerUserError ex) {
                getMessageBox().addError(ex.getDescription());
            }
        }
    }

    @Override
    public void deleteTransformationDetail() throws EMFError {
        this.deleteTransformation();

        if (!getMessageBox().hasError()) {
            forwardToPublisher(Application.Publisher.RICERCA_TRASFORMAZIONI);
        } else {
            goBack();
        }
    }

    @Override
    public void deleteTrasformazioniList() throws EMFError {
        this.deleteTransformation();

        if (!getMessageBox().hasError() && getLastPublisher().equals(Application.Publisher.DETTAGLIO_TRASFORMAZIONE)) {
            goBack();
        } else {
            getForm().getTrasformazioniList().setHideUpdateButton(false);
            getForm().getTrasformazioniList().setHideDeleteButton(false);

            forwardToPublisher(getLastPublisher());
        }
    }

    @Override
    public void deleteParametersSetList() throws EMFError {
        String lista = getTableName();
        if (lista != null) {
            if (getTableName().equals(getForm().getParametersSetList().getName())) {
                XfoSetParamTrasfRowBean currentRow = (XfoSetParamTrasfRowBean) getForm().getParametersSetList()
                        .getTable().getCurrentRow();
                BigDecimal idSetParamTrasf = currentRow.getIdSetParamTrasf();

                int riga = getForm().getParametersSetList().getTable().getCurrentRowIndex();
                getForm().getParametersSetList().getTable().remove(riga);

                if (idSetParamTrasf != null) {
                    try {
                        parametersManager.deleteParametersSet(idSetParamTrasf.longValue());
                        getMessageBox().addInfo("Set di parametri eliminato con successo");
                    } catch (ParerUserError ex) {
                        getMessageBox().addError(ex.getDescription());
                    }
                }
            }
        }

        getForm().getParametersSetList().setHideUpdateButton(false);
        getForm().getParametersSetList().setHideDeleteButton(false);

        forwardToPublisher(Application.Publisher.DETTAGLIO_TRASFORMAZIONE);
    }

    @Override
    public void deleteParametersList() throws EMFError {
        String lista = getTableName();
        if (lista != null) {
            if (getTableName().equals(getForm().getParametersList().getName())) {
                XfoParamTrasfRowBean currentRow = (XfoParamTrasfRowBean) getForm().getParametersList().getTable()
                        .getCurrentRow();
                BigDecimal idParamTrasf = currentRow.getIdParamTrasf();
                BigDecimal idParamsSet = currentRow.getIdSetParamTrasf();

                int riga = getForm().getParametersList().getTable().getCurrentRowIndex();
                getForm().getParametersList().getTable().remove(riga);

                if (idParamTrasf != null) {
                    try {
                        parametersManager.deleteParameter(idParamTrasf.longValue());

                        reloadParameterSetDetail(idParamsSet);
                    } catch (ParerUserError ex) {
                        getMessageBox().addError(ex.getDescription());
                    }
                }
            }
        }

        getForm().getParametersList().setHideUpdateButton(false);
        getForm().getParametersList().setHideDeleteButton(false);

        forwardToPublisher(Application.Publisher.DETTAGLIO_SET_PARAMETRI);
    }

    @Override
    public void deleteParametersSetDetail() throws EMFError {
        BigDecimal idSetParamTrasf = getForm().getParametersSetDetail().getId_set_param_trasf().parse();
        if (idSetParamTrasf != null) {
            try {
                parametersManager.deleteParametersSet(idSetParamTrasf.longValue());
                getMessageBox().addInfo("Set di parametri eliminato con successo");
            } catch (ParerUserError ex) {
                getMessageBox().addError(ex.getDescription());
            }
        }

        if (!getMessageBox().hasError()) {
            goBack();
        }
    }

    public void download() throws EMFError, IOException {
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
                 * Definiamo l'output previsto che sarà un file in formato zip di cui si occuperà la servlet per fare il
                 * download
                 */
                OutputStream outUD = getServletOutputStream();
                getResponse().setContentType(StringUtils.isBlank(contentType) ? "application/zip" : contentType);
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
                    logger.error("Eccezione nel recupero del documento ", e);
                    getMessageBox().addError("Eccezione nel recupero del documento");
                } finally {
                    IOUtils.closeQuietly(inputStream);
                    IOUtils.closeQuietly(outUD);
                    inputStream = null;
                    outUD = null;
                    freeze();
                }
                // Nel caso sia stato richiesto, elimina il file
                if (deleteFile) {
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
    public void insertParametersSet() throws EMFError {
        try {
            XfoTrasfRowBean xfoTrasfRowBean = repositoryManager
                    .getXfoTrasRowBean(Long.parseLong(getForm().getTransformationDetail().getId_trasf().getValue()));
            List<String> extractedParametersList = extractParametersList(xfoTrasfRowBean);

            if (!extractedParametersList.isEmpty()) {
                getForm().getInserimentoSetParametri().reset();

                getForm().getInserimentoSetParametri().setEditMode();
                getForm().getInserimentoSetParametri().getFl_set_param_ark()
                        .setDecodeMap(ComboGetter.getMappaParametersSetType());
                getForm().getInserimentoSetParametri().setStatus(BaseElements.Status.insert);

                forwardToPublisher(Application.Publisher.INSERIMENTO_SET_PARAMETRI);
            } else {
                if (xfoTrasfRowBean.getBlTrasf() != null) {
                    getMessageBox()
                            .addInfo("Questa trasformazione non richiede altri parametri configurabili dall'utente.");
                } else {
                    getMessageBox().addInfo("Nessun pacchetto caricato per questa trasformazione.");
                }
                forwardToPublisher(getLastPublisher());
            }
        } catch (ParerUserError ex) {
            getMessageBox().addError(ex.getDescription());
            forwardToPublisher(getLastPublisher());
        }
    }

    @Override
    public void updateParametersSetsFromSetDetail() throws EMFError {
        try {

            XfoSetParamTrasfRowBean xfoSetParamTrasfRowBean = parametersManager.getXfoSetParamTrasfRowBean(
                    Long.parseLong(getForm().getParametersSetDetail().getId_set_param_trasf().getValue()));
            long idTrasf = xfoSetParamTrasfRowBean.getIdTrasf().longValue();
            XfoTrasfRowBean xfoTrasfRowBean = repositoryManager.getXfoTrasRowBean(idTrasf);

            Map<String, String> extractedParametersMap = extractParametersMap(xfoTrasfRowBean, true);

            if (!extractedParametersMap.isEmpty()) {
                parametersManager.updateParametersInParametersSets(idTrasf, extractedParametersMap,
                        Constants.DEFAULT_PARAMENTER_SET_NAME);

                XfoTrasfRowBean xfoTrasRowBean = repositoryManager.getXfoTrasRowBean(idTrasf);
                reloadTransformationDetail(xfoTrasRowBean);

                getMessageBox().addInfo(
                        "Parametri aggiornati con successo. ATTENZIONE: altri set di parametri potrebbero essere stati modificati.");
            } else {
                if (xfoTrasfRowBean.getBlTrasf() != null) {
                    getMessageBox().addInfo("Nessun parametro da aggiornare.");
                } else {
                    getMessageBox().addInfo("Nessun pacchetto caricato per questa trasformazione.");
                }
            }

            reloadParameterSetDetail(xfoSetParamTrasfRowBean.getIdSetParamTrasf());

            getForm().getInserimentoSetParametri().setStatus(BaseElements.Status.view);
            getForm().getInserimentoSetParametri().setViewMode();
            getForm().getParametersSetDetail().setStatus(BaseElements.Status.view);
            getForm().getParametersSetDetail().setViewMode();
            getForm().getParametersSetDetail().getAddParameter().setEditMode();
            getForm().getParametersSetDetail().getUpdateParametersSetsFromSetDetail().setEditMode();

            getForm().getInserimentoParametro().setEditMode();

            forwardToPublisher(Application.Publisher.DETTAGLIO_SET_PARAMETRI);

        } catch (ParerUserError ex) {
            getMessageBox().addError(ex.getDescription());
            forwardToPublisher(getLastPublisher());
        }
    }

    @Override
    public void updateParametersSets() throws EMFError {
        try {
            long idTrasf = Long.parseLong(getForm().getTransformationDetail().getId_trasf().getValue());
            XfoTrasfRowBean xfoTrasfRowBean = repositoryManager.getXfoTrasRowBean(idTrasf);

            Map<String, String> extractedParametersMap = extractParametersMap(xfoTrasfRowBean, true);

            if (!extractedParametersMap.isEmpty()) {
                parametersManager.updateParametersInParametersSets(idTrasf, extractedParametersMap,
                        Constants.DEFAULT_PARAMENTER_SET_NAME);

                XfoTrasfRowBean xfoTrasRowBean = repositoryManager.getXfoTrasRowBean(idTrasf);
                reloadTransformationDetail(xfoTrasRowBean);

                getMessageBox().addInfo("Parametri aggiornati con successo.");
            } else {
                if (xfoTrasfRowBean.getBlTrasf() != null) {
                    getMessageBox().addInfo("Nessun parametro da aggiornare.");
                } else {
                    getMessageBox().addInfo("Nessun pacchetto caricato per questa trasformazione.");
                }
            }

        } catch (ParerUserError ex) {
            getMessageBox().addError(ex.getDescription());
            forwardToPublisher(getLastPublisher());
        }
    }

    @Override
    public void insertDefaultParametersSet() throws EMFError {
        try {

            // FIXME: il nome dei set di parametri deve davvero essere univoco?
            if (parametersManager.parametersSetExists(Constants.DEFAULT_PARAMENTER_SET_NAME,
                    Long.parseLong(getForm().getTransformationDetail().getId_trasf().getValue()))) {
                getMessageBox()
                        .addError("Il set di parametri di default è già stato creato. Rimuoverlo prima di riprovare.");
            } else {

                XfoTrasfRowBean xfoTrasfRowBean = repositoryManager.getXfoTrasRowBean(
                        Long.parseLong(getForm().getTransformationDetail().getId_trasf().getValue()));
                Map<String, String> extractedParametersMap = extractParametersMap(xfoTrasfRowBean, false);

                if (!extractedParametersMap.isEmpty()) {
                    long idParametersSet = parametersManager.insertNewParametersSet(
                            Constants.DEFAULT_PARAMENTER_SET_NAME, Constants.DEFAULT_PARAMENTER_SET_DESCRIPTION,
                            Constants.ComboFlagParametersSetType.ARCHIVISTICO.getValue(),
                            Long.parseLong(getForm().getTransformationDetail().getId_trasf().getValue()));
                    XfoSetParamTrasfRowBean xfoSetParamTrasfRowBean = parametersManager
                            .getXfoSetParamTrasfRowBean(idParametersSet);

                    for (Map.Entry<String, String> parameter : extractedParametersMap.entrySet()) {
                        parametersManager.insertNewParameter(parameter.getKey(), "--",
                                Constants.ComboFlagParametersType.ALFANUMERICO.name(), parameter.getValue(),
                                idParametersSet);
                    }

                    reloadParameterSetDetail(xfoSetParamTrasfRowBean.getIdSetParamTrasf());

                    getForm().getInserimentoSetParametri().setStatus(BaseElements.Status.view);
                    getForm().getInserimentoSetParametri().setViewMode();
                    getForm().getParametersSetDetail().setStatus(BaseElements.Status.view);
                    getForm().getParametersSetDetail().setViewMode();
                    getForm().getParametersSetDetail().getAddParameter().setEditMode();
                    getForm().getParametersSetDetail().getUpdateParametersSetsFromSetDetail().setEditMode();

                    getForm().getInserimentoParametro().setEditMode();

                    forwardToPublisher(Application.Publisher.DETTAGLIO_SET_PARAMETRI);
                } else {
                    if (xfoTrasfRowBean.getBlTrasf() != null) {
                        getMessageBox().addInfo(
                                "Questa trasformazione non richiede altri parametri configurabili dall'utente.");
                    } else {
                        getMessageBox().addInfo("Nessun pacchetto caricato per questa trasformazione.");
                    }
                    forwardToPublisher(getLastPublisher());
                }
            }

        } catch (ParerUserError ex) {
            getMessageBox().addError(ex.getDescription());
            forwardToPublisher(getLastPublisher());
        }
    }

    @Override
    public void addParameter() throws EMFError {
        if (getForm().getInserimentoParametro().postAndValidate(getRequest(), getMessageBox())) {

            String nmParamTrsf = getForm().getInserimentoParametro().getNm_param_trasf().parse();
            String dsParamTrasf = getForm().getInserimentoParametro().getDs_param_trasf().parse();
            String tiParamTrasf = getForm().getInserimentoParametro().getTi_param_trasf().parse();
            String dsValoreTrasf = getForm().getInserimentoParametro().getDs_valore_trasf().parse();

            if (nmParamTrsf.length() > getForm().getInserimentoParametro().getNm_param_trasf().getMaxLength()) {
                getMessageBox().addError("Il nome del parametro non deve superare i "
                        + getForm().getInserimentoParametro().getNm_param_trasf().getMaxLength() + " caratteri.");
            }

            if (StringUtils.isBlank(dsParamTrasf)) {
                dsParamTrasf = "--";
            }

            if (dsParamTrasf.length() > getForm().getInserimentoParametro().getDs_param_trasf().getMaxLength()) {
                getMessageBox().addError("La descrizione del parametro non deve superare i "
                        + getForm().getInserimentoParametro().getDs_param_trasf().getMaxLength() + " caratteri.");
            }

            if (StringUtils.isBlank(dsValoreTrasf)) {
                dsValoreTrasf = null;
            }

            if (dsValoreTrasf != null && dsValoreTrasf.length() > getForm().getInserimentoParametro()
                    .getDs_valore_trasf().getMaxLength()) {
                getMessageBox().addError("Il valore di dafault del parametro non deve superare i "
                        + getForm().getInserimentoParametro().getDs_valore_trasf().getMaxLength() + " caratteri.");
            }

            BigDecimal idParamsSetTrasf = getForm().getParametersSetDetail().getId_set_param_trasf().parse();

            if (!getMessageBox().hasError()) {
                try {
                    parametersManager.insertNewParameter(nmParamTrsf, dsParamTrasf, tiParamTrasf, dsValoreTrasf,
                            idParamsSetTrasf.longValue());

                    getForm().getInserimentoParametro().reset();

                    reloadParameterSetDetail(idParamsSetTrasf);

                } catch (ParerUserError ex) {
                    getMessageBox().addError(ex.getDescription());
                }
            }
        }

        forwardToPublisher(Application.Publisher.DETTAGLIO_SET_PARAMETRI);
    }

    @Override
    public void insertNewVersion() throws EMFError {

        try {
            BigDecimal idTrasf = getForm().getTransformationDetail().getId_trasf().parse();
            XfoTrasfRowBean xfoTrasRowBean = repositoryManager.getXfoTrasRowBean(idTrasf.longValue());

            // I campi hanno lo stesso nome del form di dettaglio trasformazione , quindi posso copiare direttamente il
            // bean
            getForm().getInserimentoNuovaVersione().copyFromBean(xfoTrasRowBean);

            // rimuovi il vecchio blob della trasformazione dal form!
            getForm().getInserimentoNuovaVersione().getTrans_blob().reset();

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

            Calendar dIstituz = new GregorianCalendar();
            Calendar dSoppres = new GregorianCalendar(2444, Calendar.DECEMBER, 31);

            // setta al momento corrente
            dIstituz.setTime(new Date());

            getForm().getInserimentoNuovaVersione().getDt_istituz().setValue(sdf.format(dIstituz.getTime()));
            getForm().getInserimentoNuovaVersione().getDt_soppres().setValue(sdf.format(dSoppres.getTime()));
            getForm().getInserimentoNuovaVersione().getOre_dt_ist()
                    .setValue(String.format("%02d", dIstituz.get(Calendar.HOUR_OF_DAY)));
            getForm().getInserimentoNuovaVersione().getMinuti_dt_ist()
                    .setValue(String.format("%02d", dIstituz.get(Calendar.MINUTE)));
            getForm().getInserimentoNuovaVersione().getOre_dt_sop()
                    .setValue(String.format("%02d", dSoppres.get(Calendar.HOUR_OF_DAY)));
            getForm().getInserimentoNuovaVersione().getMinuti_dt_sop()
                    .setValue(String.format("%02d", dSoppres.get(Calendar.MINUTE)));

            getForm().getInserimentoNuovaVersione().getCd_versione_cor().setEditMode();
            getForm().getInserimentoNuovaVersione().getDs_versione_cor().setEditMode();
            getForm().getInserimentoNuovaVersione().getDt_istituz().setEditMode();
            getForm().getInserimentoNuovaVersione().getDt_soppres().setEditMode();
            getForm().getInserimentoNuovaVersione().getFl_attiva().setEditMode();
            getForm().getInserimentoNuovaVersione().getTrans_blob().setEditMode();
            getForm().getInserimentoNuovaVersione().getOre_dt_ist().setEditMode();
            getForm().getInserimentoNuovaVersione().getMinuti_dt_ist().setEditMode();
            getForm().getInserimentoNuovaVersione().getOre_dt_sop().setEditMode();
            getForm().getInserimentoNuovaVersione().getMinuti_dt_sop().setEditMode();

            getForm().getInserimentoNuovaVersione().setStatus(BaseElements.Status.insert);

        } catch (ParerUserError ex) {
            getMessageBox().addError(ex.getDescription());
        }

        forwardToPublisher(Application.Publisher.INSERIMENTO_NUOVA_VERSIONE);
    }

    public void scaricaTrasformazione() throws EMFError {
        XfoStoricoTrasfRowBean xfoStoricoTrasfRowBean = null;
        try {

            Integer currentRow = Integer.parseInt(getRequest().getParameter("riga"));
            BigDecimal idStoricoTrasf = getForm().getVersionsList().getTable().getRow(currentRow)
                    .getBigDecimal("id_storico_trasf");

            xfoStoricoTrasfRowBean = repositoryManager.getXfoStoricoTrasRowBean(idStoricoTrasf.longValue());
        } catch (ParerUserError ex) {
            getMessageBox().addError(ex.getDescription());
        }

        if (!getMessageBox().hasError()) {
            String cdTrasf = xfoStoricoTrasfRowBean.getCdTrasf();
            String version = xfoStoricoTrasfRowBean.getCdVersione();
            String filename = cdTrasf.replace(' ', '_') + "-" + version.replace(' ', '_') + ".zip";

            File tmpFile = new File(System.getProperty("java.io.tmpdir"), filename);
            FileOutputStream file = null;
            InputStream is = null;
            try {
                byte[] fileBytes = xfoStoricoTrasfRowBean.getBlTrasf();
                if (fileBytes != null) {

                    file = new FileOutputStream(tmpFile);
                    is = new ByteArrayInputStream(fileBytes);
                    byte[] data = new byte[1024];
                    int count;
                    while ((count = is.read(data, 0, 1024)) != -1) {
                        file.write(data, 0, count);
                    }

                    getRequest().setAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_ACTION.name(), getControllerName());
                    getSession().setAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_FILENAME.name(), tmpFile.getName());
                    getSession().setAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_FILEPATH.name(), tmpFile.getPath());
                    getSession().setAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_DELETEFILE.name(),
                            Boolean.toString(true));
                    getSession().setAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_CONTENTTYPE.name(),
                            "application/zip");
                }
            } catch (IOException ex) {
                logger.error("Errore in download " + ExceptionUtils.getRootCauseMessage(ex), ex);
                getMessageBox().addError("Errore inatteso nella preparazione del download");
            } finally {
                IOUtils.closeQuietly(is);
                IOUtils.closeQuietly(file);
            }

            if (getMessageBox().hasError()) {
                forwardToPublisher(getLastPublisher());
            } else {
                forwardToPublisher(Application.Publisher.DOWNLOAD_PAGE);
            }
        }

    }

    private AmministrazioneForm computeLinksInfo(BigDecimal idTipoOggetto) throws EMFError {
        AmministrazioneForm form = new AmministrazioneForm();

        Integer currentRow = Integer.valueOf(getRequest().getParameter("riga"));

        PigTipoObjectRowBean row = amministrazioneEjb.getPigTipoObjectRowBean(idTipoOggetto);

        PigTipoObjectTableBean table = new PigTipoObjectTableBean();
        table.add(row);
        form.getTipoObjectList().setTable(table);

        BigDecimal idVers = getForm().getTipiOggettoList().getTable().getRow(currentRow).getBigDecimal("id_vers");

        PigVersRowBean rowVers = amministrazioneEjb.getPigVersRowBean(idVers);

        PigVersTableBean tableVers = new PigVersTableBean();
        tableVers.add(rowVers);
        form.getVersList().setTable(tableVers);

        SessionManager.addPrevExecutionToHistory(getSession(), true, false);

        return form;
    }

    public void vaiAlVersatore() throws EMFError {
        Integer currentRow = Integer.valueOf(getRequest().getParameter("riga"));
        BigDecimal idTipoOggetto = getForm().getTipiOggettoList().getTable().getRow(currentRow)
                .getBigDecimal("id_tipo_object");
        AmministrazioneForm form = computeLinksInfo(idTipoOggetto);

        redirectToAction(Application.Actions.AMMINISTRAZIONE, "?operation=listNavigationOnClick&navigationEvent="
                + NE_DETTAGLIO_VIEW + "&table=" + form.getVersList().getName() + "&riga=0", form);
    }

    public void vaiAlTipoOggetto() throws EMFError {
        Integer currentRow = Integer.valueOf(getRequest().getParameter("riga"));
        BigDecimal idTipoOggetto = getForm().getTipiOggettoList().getTable().getRow(currentRow)
                .getBigDecimal("id_tipo_object");
        AmministrazioneForm form = computeLinksInfo(idTipoOggetto);

        redirectToAction(Application.Actions.AMMINISTRAZIONE, "?operation=listNavigationOnClick&navigationEvent="
                + NE_DETTAGLIO_VIEW + "&table=" + form.getTipoObjectList().getName() + "&riga=0", form);
    }

    public void vaiAlTipoOggettoGenerato() throws EMFError {
        Integer currentRow = Integer.valueOf(getRequest().getParameter("riga"));
        BigDecimal idTipoOggetto = getForm().getTipiOggettoList().getTable().getRow(currentRow)
                .getBigDecimal("id_tipo_object_generato");
        AmministrazioneForm form = computeLinksInfo(idTipoOggetto);

        redirectToAction(Application.Actions.AMMINISTRAZIONE, "?operation=listNavigationOnClick&navigationEvent="
                + NE_DETTAGLIO_VIEW + "&table=" + form.getTipoObjectList().getName() + "&riga=0", form);
    }

    @Secure(action = "Menu.AmministrazioneTrasformazioni.MonitoraggioServerTrasformazioni")
    public void loadKettleServersStatus() throws EMFError {
        getUser().getMenu().reset();
        getUser().getMenu().select("Menu.AmministrazioneTrasformazioni.MonitoraggioServerTrasformazioni");

        getForm().getMonitoraggioServerTrasformazioniDetail().setEditMode();

        DecodeMap serversMap = new DecodeMap();
        BaseTableInterface<?> allPigObjectKettleServerInstanceTable = trasformazioniHelper
                .getAllPigObjectKettleServerInstanceTable();
        serversMap.populatedMap(allPigObjectKettleServerInstanceTable, "url_istanza", "nm_istanza");
        getForm().getMonitoraggioServerTrasformazioniDetail().getFl_set_server().setDecodeMap(serversMap);

        BaseRowInterface firstRow = allPigObjectKettleServerInstanceTable.getRow(0);
        if (firstRow != null) {
            updateStatoTrasformazioniTables(firstRow.getObject("url_istanza").toString());
        }

        forwardToPublisher(Application.Publisher.MONITORAGGIO_SERVER_TRASFORMAZIONI);
    }

    private void updateStatoTrasformazioniTables(String urlIstanza) {
        EsitoStatusCodaTrasformazione statusCodaTrasformazioni = repositoryManager
                .getStatusCodaTrasformazioni(urlIstanza);

        List<StatoTrasformazione> statiTrasformazioni = statusCodaTrasformazioni.getTrasformazioniInCorso() != null
                ? statusCodaTrasformazioni.getTrasformazioniInCorso() : new ArrayList<>();

        BaseTableInterface<?> trasformazioniInCorsoTable = repositoryManager
                .createStatoTrasformazioniTable(statiTrasformazioni);
        getForm().getStatoTrasformazioniInCorsoList().setTable(trasformazioniInCorsoTable);
        getForm().getStatoTrasformazioniInCorsoList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
        getForm().getStatoTrasformazioniInCorsoList().getTable().first();

        statiTrasformazioni = statusCodaTrasformazioni.getTrasformazioniInCoda() != null
                ? statusCodaTrasformazioni.getTrasformazioniInCoda() : new ArrayList<>();

        BaseTableInterface<?> trasformazioniInCodaTable = repositoryManager
                .createStatoTrasformazioniTable(statiTrasformazioni);
        getForm().getStatoTrasformazioniInCodaList().setTable(trasformazioniInCodaTable);
        getForm().getStatoTrasformazioniInCodaList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
        getForm().getStatoTrasformazioniInCodaList().getTable().first();

        statiTrasformazioni = statusCodaTrasformazioni.getStoricoTrasformazioni() != null
                ? statusCodaTrasformazioni.getStoricoTrasformazioni() : new ArrayList<>();

        BaseTableInterface<?> trasformazioniStoricoTable = repositoryManager
                .createStatoTrasformazioniTable(statiTrasformazioni);
        getForm().getStatoTrasformazioniStoricoList().setTable(trasformazioniStoricoTable);
        getForm().getStatoTrasformazioniStoricoList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
        getForm().getStatoTrasformazioniStoricoList().getTable().first();
    }

    @Override
    public void updateKettleServersStatus() throws EMFError {
        if (getForm().getMonitoraggioServerTrasformazioniDetail().postAndValidate(getRequest(), getMessageBox())) {

            String urlIstanza = getForm().getMonitoraggioServerTrasformazioniDetail().getFl_set_server().parse();
            updateStatoTrasformazioniTables(urlIstanza);
        }

        forwardToPublisher(Application.Publisher.MONITORAGGIO_SERVER_TRASFORMAZIONI);
    }

    private Map<String, String> extractParametersMap(XfoTrasfRowBean xfoTrasfRowBean,
            boolean keepAlreadyAssignedParameters) {
        Map<String, String> parametersMap = new HashMap<String, String>();

        if (xfoTrasfRowBean.getBlTrasf() != null) {
            ZipInputStream zipInputStream = new ZipInputStream(new ByteArrayInputStream(xfoTrasfRowBean.getBlTrasf()));
            try {
                ZipEntry entry = zipInputStream.getNextEntry();
                if (entry != null && entry.isDirectory()) {

                    Map<String, String> parameters = repositoryManager.listJobParameters(entry.getName());
                    for (Map.Entry<String, String> parameter : parameters.entrySet()) {
                        switch (parameter.getKey()) {
                        case it.eng.xformer.common.Constants.XF_AMBIENTE:
                        case it.eng.xformer.common.Constants.XF_ENTE:
                        case it.eng.xformer.common.Constants.XF_STRUTTURA:
                        case it.eng.xformer.common.Constants.XF_UTENTE:
                        case it.eng.xformer.common.Constants.XF_INPUT_FILE_NAME:
                        case it.eng.xformer.common.Constants.XF_OUTPUT_DIR:
                        case it.eng.xformer.common.Constants.XF_OUTPUT_FILE_BASE_NAME:
                        case it.eng.xformer.common.Constants.XF_FORZA_ACCETTAZIONE:
                        case it.eng.xformer.common.Constants.XF_FORZA_COLLEGAMENTO:
                        case it.eng.xformer.common.Constants.XF_FORZA_CONSERVAZIONE:
                        case it.eng.xformer.common.Constants.XF_AUXILIARY_FILES_DIR:
                        case it.eng.xformer.common.Constants.XF_TMP_DIR:
                        case it.eng.xformer.common.Constants.XF_REPORT_ID:
                        case it.eng.xformer.common.Constants.XF_KETTLE_DB_HOST:
                        case it.eng.xformer.common.Constants.XF_KETTLE_DB_PORT:
                        case it.eng.xformer.common.Constants.XF_KETTLE_DB_NAME:
                        case it.eng.xformer.common.Constants.XF_KETTLE_DB_USER:
                        case it.eng.xformer.common.Constants.XF_KETTLE_DB_PASSWORD:
                        case it.eng.xformer.common.Constants.XF_DB_TABLE_ID:
                            break;
                        default:
                            if (keepAlreadyAssignedParameters
                                    || !parametersManager.isParameterAssigned(parameter.getKey(),
                                            xfoTrasfRowBean.getIdTrasf().longValue())) {
                                parametersMap.put(parameter.getKey(), parameter.getValue());
                            }
                            break;
                        }
                    }
                }
            } catch (KettleServiceException | KettleException | IOException ex) {
                getMessageBox().addError(ex.getMessage());
            }
        }

        return parametersMap;
    }

    private List<String> extractParametersList(XfoTrasfRowBean xfoTrasfRowBean) {
        List<String> paramtersList = new ArrayList<>();

        if (xfoTrasfRowBean.getBlTrasf() != null) {
            ZipInputStream zipInputStream = new ZipInputStream(new ByteArrayInputStream(xfoTrasfRowBean.getBlTrasf()));
            try {
                ZipEntry entry = zipInputStream.getNextEntry();
                if (entry != null && entry.isDirectory()) {

                    Map<String, String> parameters = repositoryManager.listJobParameters(entry.getName());
                    for (Map.Entry<String, String> parameter : parameters.entrySet()) {
                        switch (parameter.getKey()) {
                        case it.eng.xformer.common.Constants.XF_AMBIENTE:
                        case it.eng.xformer.common.Constants.XF_ENTE:
                        case it.eng.xformer.common.Constants.XF_STRUTTURA:
                        case it.eng.xformer.common.Constants.XF_UTENTE:
                        case it.eng.xformer.common.Constants.XF_INPUT_FILE_NAME:
                        case it.eng.xformer.common.Constants.XF_OUTPUT_DIR:
                        case it.eng.xformer.common.Constants.XF_OUTPUT_FILE_BASE_NAME:
                        case it.eng.xformer.common.Constants.XF_FORZA_ACCETTAZIONE:
                        case it.eng.xformer.common.Constants.XF_FORZA_COLLEGAMENTO:
                        case it.eng.xformer.common.Constants.XF_FORZA_CONSERVAZIONE:
                        case it.eng.xformer.common.Constants.XF_AUXILIARY_FILES_DIR:
                        case it.eng.xformer.common.Constants.XF_TMP_DIR:
                        case it.eng.xformer.common.Constants.XF_REPORT_ID:
                        case it.eng.xformer.common.Constants.XF_KETTLE_DB_HOST:
                        case it.eng.xformer.common.Constants.XF_KETTLE_DB_PORT:
                        case it.eng.xformer.common.Constants.XF_KETTLE_DB_NAME:
                        case it.eng.xformer.common.Constants.XF_KETTLE_DB_USER:
                        case it.eng.xformer.common.Constants.XF_KETTLE_DB_PASSWORD:
                        case it.eng.xformer.common.Constants.XF_DB_TABLE_ID:
                            break;
                        default:
                            if (!parametersManager.isParameterAssigned(parameter.getKey(),
                                    xfoTrasfRowBean.getIdTrasf().longValue())) {
                                paramtersList.add(parameter.getKey());
                            }
                            break;
                        }
                    }
                }
            } catch (KettleServiceException | KettleException | IOException ex) {
                getMessageBox().addError(ex.getMessage());
            }
        }
        return paramtersList;
    }

    private void reloadParameterSetDetail(BigDecimal idParamsSet) throws ParerUserError, EMFError {
        // BigDecimal idParamsSet =
        // getForm().getParametersSetList().getTable().getCurrentRow().getBigDecimal("id_set_param_trasf");

        XfoSetParamTrasfRowBean xfoSetParamTrasfRowBean = parametersManager
                .getXfoSetParamTrasfRowBean(idParamsSet.longValue());
        XfoTrasfRowBean xfoTrasfRowBean = repositoryManager
                .getXfoTrasRowBean(xfoSetParamTrasfRowBean.getIdTrasf().longValue());

        getForm().getParametersSetDetail().getFl_set_param_ark().setDecodeMap(ComboGetter.getMappaParametersSetType());
        getForm().getParametersSetDetail().copyFromBean(xfoSetParamTrasfRowBean);
        getForm().getParametersSetDetail().getNm_xfo_trasf().setValue(xfoTrasfRowBean.getCdTrasf());

        long idParamsSetTrasf = getForm().getParametersSetDetail().getId_set_param_trasf().parse().longValue();

        XfoParamTrasfTableBean paramTrasfTableBean = parametersManager.searchParametersBySet(idParamsSetTrasf);
        getForm().getParametersList().setTable(paramTrasfTableBean);
        getForm().getParametersList().getTable().setPageSize(WebConstants.ONE_HUNDRED_PAGE_SIZE);
        getForm().getParametersList().getTable().first();

        List<String> parameters = extractParametersList(xfoTrasfRowBean);

        // ora rimuovi i parametri già assegnati dalla lista
        for (XfoParamTrasfRowBean row : paramTrasfTableBean) {
            int index = parameters.indexOf(row.getNmParamTrasf());
            if (index != -1) {
                parameters.remove(index);
            }
        }

        if (parameters.isEmpty()) {
            getRequest().setAttribute("hideInsertParameter", true);
        }

        getForm().getInserimentoParametro().getNm_param_trasf()
                .setDecodeMap(ComboGetter.getMappaSortedGenericStringList("NM_PARAM_TRASF", parameters));
        getForm().getInserimentoParametro().getTi_param_trasf().setDecodeMap(
                ComboGetter.getMappaSortedGenericEnum("TI_PARAM_TRASF", Constants.ComboFlagParametersType.values()));
    }

    private void saveDettaglioTransormationsList() throws EMFError {
        try {
            BigDecimal idTrasf = getForm().getTransformationDetail().getId_trasf().parse();
            String cdTrasf = getForm().getTransformationDetail().getCd_trasf().parse();
            String dsTrasf = getForm().getTransformationDetail().getDs_trasf().parse();
            String version = getForm().getTransformationDetail().getCd_versione_cor().parse();
            String dsVersion = getForm().getTransformationDetail().getDs_versione_cor().parse();
            String flAttiva = getForm().getTransformationDetail().getFl_attiva().parse();
            Timestamp dtIstituz = getForm().getTransformationDetail().getDt_istituz().parse();
            Timestamp dtSoppres = getForm().getTransformationDetail().getDt_soppres().parse();
            String oraIstituz = getForm().getTransformationDetail().getOre_dt_ist().parse();
            String oraSoppres = getForm().getTransformationDetail().getOre_dt_sop().parse();
            String minutiIstituz = getForm().getTransformationDetail().getMinuti_dt_ist().parse();
            String minutiSoppres = getForm().getTransformationDetail().getMinuti_dt_sop().parse();

            byte[] zipPackage = getForm().getTransformationDetail().getTrans_blob().getFileBytes();

            // validazione input
            // il nome è obbligatorio...
            if (StringUtils.isBlank(cdTrasf)) {
                getMessageBox().addError("Il campo \"nome\" della trasformazione è obbligatorio.");
            }

            if (zipPackage == null && flAttiva.equals(Constants.ComboFlag.SI.getValue())) {
                try {
                    XfoTrasfRowBean xfoTrasRowBean = repositoryManager.getXfoTrasRowBean(idTrasf.longValue());
                    if (xfoTrasRowBean.getBlTrasf() == null) {
                        getForm().getTransformationDetail().getFl_attiva().setValue(Constants.ComboFlag.NO.getValue());
                        flAttiva = Constants.ComboFlag.NO.getValue();
                        getMessageBox()
                                .addInfo("Una trasformazione senza pacchetto non può essere in stato\"attivo\".");
                    }
                } catch (ParerUserError ex) {
                    logger.error("Errore durante la lettura della trasformazione: " + ex.getMessage());
                    getMessageBox().addError("Errore durante la lettura della trasformazione: " + ex.getMessage());
                }
            }
            // la versione è obbligatoria
            if (StringUtils.isBlank(version)) {
                getMessageBox().addError("Il campo \"versione\" della trasformazione è obbligatorio.");
            }

            if (!trasformazioniHelper.isVersionUnique(idTrasf.longValue(), version)) {
                getMessageBox().addError("La versione della trasformazione è già presente nello storico.");
            }

            if (cdTrasf != null
                    && cdTrasf.length() > getForm().getTransformationDetail().getCd_trasf().getMaxLength()) {
                getMessageBox().addError("Il nome della trasformazione non deve superare i "
                        + getForm().getTransformationDetail().getCd_trasf().getMaxLength() + "caratteri.");
            }

            if (StringUtils.isBlank(dsTrasf)) {
                dsTrasf = "--";
            }

            if (dsTrasf != null
                    && dsTrasf.length() > getForm().getTransformationDetail().getDs_trasf().getMaxLength()) {
                getMessageBox().addError("La descrizione della versione della trasformazione non deve superare i "
                        + getForm().getTransformationDetail().getDs_trasf().getMaxLength() + "caratteri.");
            }

            if (version != null
                    && version.length() > getForm().getTransformationDetail().getCd_versione_cor().getMaxLength()) {
                getMessageBox().addError("La versione della trasformazione non deve superare i "
                        + getForm().getTransformationDetail().getCd_versione_cor().getMaxLength() + "caratteri.");
            }

            if (StringUtils.isBlank(dsVersion)) {
                dsVersion = "--";
            }

            if (dsVersion.length() > getForm().getTransformationDetail().getDs_versione_cor().getMaxLength()) {
                getMessageBox().addError("La descrizione della versione della trasformazione non deve superare i "
                        + getForm().getTransformationDetail().getDs_versione_cor().getMaxLength() + "caratteri.");
            }

            if (dtIstituz != null && !dtIstituz.before(dtSoppres)) {
                getMessageBox().addError("La data di inizio validità deve anteriore alla data di fine validità.");
            }

            if (StringUtils.isBlank(oraIstituz) || StringUtils.isBlank(minutiIstituz)) {
                getMessageBox().addError("L'orario di inizio validità è obbligatorio.");
            }

            if (StringUtils.isBlank(oraSoppres) || StringUtils.isBlank(minutiSoppres)) {
                getMessageBox().addError("L'orario di fine validità è obbligatorio.");
            }

            BigDecimal oraIstituzBD = null;
            BigDecimal minutiIstituzBD = null;
            BigDecimal oraSoppresBD = null;
            BigDecimal minutiSoppresBD = null;
            Date dIstituz = null;
            Date dSoppres = null;

            if (!StringUtils.isBlank(oraIstituz) && !StringUtils.isBlank(minutiIstituz)
                    && !StringUtils.isBlank(oraSoppres) && !StringUtils.isBlank(minutiSoppres)) {
                try {
                    oraIstituzBD = new BigDecimal(oraIstituz);
                    minutiIstituzBD = new BigDecimal(minutiIstituz);
                    oraSoppresBD = new BigDecimal(oraSoppres);
                    minutiSoppresBD = new BigDecimal(minutiSoppres);
                } catch (Exception ex) {
                    getMessageBox().addError("Data e ora devono essere espressi in numeri.");
                }

                TypeValidator validator = new TypeValidator(getMessageBox());
                dIstituz = validator.validateSingleDate(dtIstituz, oraIstituzBD, minutiIstituzBD,
                        getForm().getTransformationDetail().getDt_istituz().getDescription());
                dSoppres = validator.validateSingleDate(dtSoppres, oraSoppresBD, minutiSoppresBD,
                        getForm().getTransformationDetail().getDt_soppres().getDescription());
            }

            if (!trasformazioniHelper.isVersionDateUnique(idTrasf.longValue(), dIstituz)) {
                getMessageBox().addError(
                        "La data di inizio validità \"" + dIstituz + "\" è uguale ad una versione precedente.");
            }

            if (trasformazioniHelper.isDateIstituzOverlapping(idTrasf.longValue(), dIstituz)) {
                getMessageBox().addError("La data di inizio validità \"" + dIstituz
                        + "\" è precedente alla data di fine validità della versione precedente.");
            }

            String kettleId = "";

            if (!getMessageBox().hasError() && zipPackage != null) {
                try (ByteArrayInputStream bis = new ByteArrayInputStream(zipPackage);
                        InputStream stream = TikaInputStream.get(bis);) {
                    // controlla che il pacchetto ricevuto sia uno zip
                    Detector detector = TikaConfig.getDefaultConfig().getDetector();
                    Metadata metadata = new Metadata();
                    metadata.set(RESOURCE_NAME_KEY, null);
                    String mediaType = detector.detect(stream, metadata).getBaseType().toString();
                    if (!mediaType.contains(Constants.TRANSFORMATION_PKG_MIME_TYPE)) {
                        logger.error(
                                "Errore nell'inserimento della trasformazione: il pacchetto non è un archivio ZIP.");
                        getMessageBox().addError("Il pacchetto della trasformazione deve essere un archivio ZIP.");
                    }

                    ZipInputStream zipInputStream = new ZipInputStream(bis);
                    ZipEntry entry;
                    int directoryCount = 0;
                    boolean mainJobFound = false;

                    while ((entry = zipInputStream.getNextEntry()) != null) {
                        if (entry.isDirectory()) {
                            directoryCount++;
                            kettleId = entry.getName();
                        } else if (entry.getName().equals(kettleId + Constants.XFO_MAIN_JOB_FILENAME)) {
                            mainJobFound = true;
                        }
                    }

                    if (directoryCount != 1) {
                        logger.error(
                                "Errore nell'inserimento della trasformazione: il pacchetto non contiene la cartella della trasformazione o ne contiene più di una.");
                        getMessageBox().addError(
                                "Errore nell'inserimento della trasformazione: il pacchetto non contiene la cartella della trasformazione o ne contiene più di una.");
                    }

                    if (!mainJobFound) {
                        logger.error(
                                "Errore nell'inserimento della trasformazione: il pacchetto non contiene il job d'avvio.");
                        getMessageBox().addError(
                                "Errore nell'inserimento della trasformazione: il pacchetto non contiene il job d'avvio");
                    }

                    String oldKettleId = trasformazioniHelper.retriveKettleId(idTrasf.longValue());

                    // controlla che la nuova trasformazione abbia lo stesso nome di quella precedente
                    if (!kettleId.isEmpty() && !oldKettleId.isEmpty() && !kettleId.equals(oldKettleId)) {
                        logger.error("Errore nell'inserimento della trasformazione: l'id kettle " + kettleId
                                + " è differente dal precedente id " + oldKettleId + ".");
                        getMessageBox().addError("Errore nell'inserimento della trasformazione: l'id kettle " + kettleId
                                + " è differente dal precedente id " + oldKettleId + ".");
                    }

                    // controlla che la trasformazione non esista "system-wide"
                    if (oldKettleId.isEmpty() && !getMessageBox().hasError()) {
                        try {
                            if (repositoryManager.isTransformationAlreadyPresent(kettleId)) {
                                logger.error(
                                        "Errore nell'inserimento della trasformazione: la trasformazione con identificativo "
                                                + kettleId + " è già presente nel repository.");
                                getMessageBox().addError(
                                        "Errore nell'inserimento della trasformazione: la trasformazione con identificativo "
                                                + kettleId + " è già presente nel repository.");
                            }
                        } catch (KettleException ex) {
                            logger.error(
                                    "Errore nell'inserimento della trasformazione: impossibile contattare il server Kettle.");
                            getMessageBox().addError(
                                    "Errore nell'inserimento della trasformazione: impossibile contattare il server Kettle.",
                                    ex);
                        }
                    }
                } catch (IOException ex) {
                    logger.error("Errore durante l'apertura del pacchetto della trasformazione: " + ex.getMessage());
                    getMessageBox().addError(
                            "Errore durante l'apertura del pacchetto della trasformazione: " + ex.getMessage());
                }
            }

            if (!getMessageBox().hasError()) {
                repositoryManager.updateTransformation(idTrasf.longValue(), cdTrasf, dsTrasf, flAttiva, version,
                        dsVersion, dIstituz, dSoppres, zipPackage, kettleId);
                XfoTrasfRowBean xfoTrasRowBean = repositoryManager.getXfoTrasRowBean(idTrasf.longValue());
                reloadTransformationDetail(xfoTrasRowBean);
            }
        } catch (ParerUserError ex) {
            getMessageBox().addError(ex.getDescription());
        }
    }

    private void saveDettaglioInsertSetParameters() throws EMFError {
        String setName = getForm().getInserimentoSetParametri().getNm_set_param_trasf().parse();
        String setDescription = getForm().getInserimentoSetParametri().getDs_set_param_trasf().parse();
        String setType = getForm().getInserimentoSetParametri().getFl_set_param_ark().parse();

        // il nome è obbligatorio...
        if (StringUtils.isBlank(setName)) {
            getMessageBox().addError("Il campo \"nome\" del set di parametri è obbligatorio.");
        }

        // FIXME: il nome dei set di parametri deve davvero essere univoco?
        if (parametersManager.parametersSetExists(setName,
                Long.parseLong(getForm().getTransformationDetail().getId_trasf().getValue()))) {
            getMessageBox().addError("Un set di parametri con questo nome esiste già.");
        }

        if (StringUtils.isBlank(setType)) {
            getMessageBox().addError("Il campo \"tipologia\" del set di parametri è obbligatorio.");
        }

        if (setName != null
                && setName.length() > getForm().getInserimentoSetParametri().getNm_set_param_trasf().getMaxLength()) {
            getMessageBox().addError("Il nome del set non deve superare i "
                    + getForm().getInserimentoSetParametri().getNm_set_param_trasf().getMaxLength() + "caratteri.");
        }

        if (StringUtils.isBlank(setDescription)) {
            setDescription = "--";
        }

        if (setDescription.length() > getForm().getInserimentoSetParametri().getDs_set_param_trasf().getMaxLength()) {
            getMessageBox().addError("La descrizione del set non deve superare i "
                    + getForm().getInserimentoSetParametri().getDs_set_param_trasf().getMaxLength() + "caratteri.");
        }

        try {
            if (!getMessageBox().hasError()) {
                long idParametersSet = parametersManager.insertNewParametersSet(setName, setDescription, setType,
                        Long.parseLong(getForm().getTransformationDetail().getId_trasf().getValue()));
                XfoSetParamTrasfRowBean xfoSetParamTrasfRowBean = parametersManager
                        .getXfoSetParamTrasfRowBean(idParametersSet);

                reloadParameterSetDetail(xfoSetParamTrasfRowBean.getIdSetParamTrasf());
            }

        } catch (ParerUserError ex) {
            getMessageBox().addError(ex.getDescription());
        }
    }

    private void saveDettaglioParametersSetDetail() throws EMFError {
        String setName = getForm().getParametersSetDetail().getNm_set_param_trasf().parse();
        String setDescription = getForm().getParametersSetDetail().getDs_set_param_trasf().parse();
        String setType = getForm().getParametersSetDetail().getFl_set_param_ark().parse();

        // il nome è obbligatorio...
        if (StringUtils.isBlank(setName)) {
            getMessageBox().addError("Il campo \"nome\" del set di parametri è obbligatorio.");
        }

        if (StringUtils.isBlank(setType)) {
            getMessageBox().addError("Il campo \"tipologia\" del set di parametri è obbligatorio.");
        }

        if (setName.length() > getForm().getParametersSetDetail().getNm_set_param_trasf().getMaxLength()) {
            getMessageBox().addError("Il nome del set non deve superare i "
                    + getForm().getParametersSetDetail().getNm_set_param_trasf().getMaxLength() + "caratteri.");
        }

        if (setDescription.length() > getForm().getParametersSetDetail().getDs_set_param_trasf().getMaxLength()) {
            getMessageBox().addError("La descrizione del set non deve superare i "
                    + getForm().getParametersSetDetail().getDs_set_param_trasf().getMaxLength() + "caratteri.");
        }

        if (StringUtils.isBlank(setDescription)) {
            setDescription = "--";
        }

        if (!getMessageBox().hasError()) {
            try {
                parametersManager.updateParametersSet(
                        Long.parseLong(getForm().getParametersSetDetail().getId_set_param_trasf().getValue()), setName,
                        setDescription, setType);
                reloadParameterSetDetail(BigDecimal.valueOf(
                        Long.parseLong(getForm().getParametersSetDetail().getId_set_param_trasf().getValue())));

            } catch (ParerUserError ex) {
                getMessageBox().addError(ex.getDescription());
            }
        }
    }

    private void saveDettaglioTransformationDetail() throws EMFError {
        saveDettaglioTransormationsList();
    }

    private void saveDettaglioInserimentoNuovaVersione() throws EMFError {
        try {
            BigDecimal idTrasf = getForm().getInserimentoNuovaVersione().getId_trasf().parse();
            String cdTrasf = getForm().getInserimentoNuovaVersione().getCd_trasf().parse();
            String version = getForm().getInserimentoNuovaVersione().getCd_versione_cor().parse();
            String dsVersion = getForm().getInserimentoNuovaVersione().getDs_versione_cor().parse();
            Timestamp dtIstituz = getForm().getInserimentoNuovaVersione().getDt_istituz().parse();
            Timestamp dtSoppres = getForm().getInserimentoNuovaVersione().getDt_soppres().parse();
            String oraIstituz = getForm().getInserimentoNuovaVersione().getOre_dt_ist().parse();
            String oraSoppres = getForm().getInserimentoNuovaVersione().getOre_dt_sop().parse();
            String minutiIstituz = getForm().getInserimentoNuovaVersione().getMinuti_dt_ist().parse();
            String minutiSoppres = getForm().getInserimentoNuovaVersione().getMinuti_dt_sop().parse();
            String flAttiva = getForm().getInserimentoNuovaVersione().getFl_attiva().parse();
            byte[] zipPackage = getForm().getInserimentoNuovaVersione().getTrans_blob().getFileBytes();

            if (flAttiva == null) {
                flAttiva = Constants.ComboFlag.NO.getValue();
            }

            XfoTrasfRowBean xfoTrasRowBean = repositoryManager.getXfoTrasRowBean(idTrasf.longValue());

            // TODO constrolli di validità sui dati
            // la versione è obbligatoria
            if (StringUtils.isBlank(version)) {
                getMessageBox().addError("Il campo \"versione\" della trasformazione è obbligatorio.");
            }

            // la versione è obbligatoria
            if (version.equals(xfoTrasRowBean.getCdVersioneCor())) {
                getMessageBox().addError(
                        "Il campo \"versione\" è identico a quello della versione corrente, occorre modificarlo");
            }

            if (version.length() > getForm().getInserimentoNuovaVersione().getCd_versione_cor().getMaxLength()) {
                getMessageBox().addError("La versione della trasformazione non deve superare i "
                        + getForm().getInserimentoNuovaVersione().getCd_versione_cor().getMaxLength() + "caratteri.");
            }

            if (!trasformazioniHelper.isVersionUnique(idTrasf.longValue(), version)) {
                getMessageBox().addError("La versione \"" + version + "\" di questa trasformazione esiste già.");
            }

            if (StringUtils.isBlank(dsVersion)) {
                dsVersion = "--";
            }

            if (dsVersion.length() > getForm().getTransformationDetail().getDs_versione_cor().getMaxLength()) {
                getMessageBox().addError("La descrizione della versione della trasformazione non deve superare i "
                        + getForm().getInserimentoNuovaVersione().getDs_versione_cor().getMaxLength() + " caratteri.");
            }

            BigDecimal oraIstituzBD = null;
            BigDecimal minutiIstituzBD = null;
            BigDecimal oraSoppresBD = null;
            BigDecimal minutiSoppresBD = null;
            Date dIstituz = null;
            Date dSoppres = null;

            if (!StringUtils.isBlank(oraIstituz) && !StringUtils.isBlank(minutiIstituz)
                    && !StringUtils.isBlank(oraSoppres) && !StringUtils.isBlank(minutiSoppres)) {
                try {
                    oraIstituzBD = new BigDecimal(oraIstituz);
                    minutiIstituzBD = new BigDecimal(minutiIstituz);
                    oraSoppresBD = new BigDecimal(oraSoppres);
                    minutiSoppresBD = new BigDecimal(minutiSoppres);
                } catch (Exception ex) {
                    getMessageBox().addError("Data e ora devono essere espressi in numeri.");
                }

                TypeValidator validator = new TypeValidator(getMessageBox());
                dIstituz = validator.validateSingleDate(dtIstituz, oraIstituzBD, minutiIstituzBD,
                        getForm().getInserimentoNuovaVersione().getDt_istituz().getDescription());
                dSoppres = validator.validateSingleDate(dtSoppres, oraSoppresBD, minutiSoppresBD,
                        getForm().getInserimentoNuovaVersione().getDt_soppres().getDescription());

                if (dIstituz.before(xfoTrasRowBean.getDtIstituz()) || dIstituz.equals(xfoTrasRowBean.getDtIstituz())) {
                    getMessageBox().addError(
                            "La data di inizio validità deve essere maggiore della data di inizio validità dell'attuale versione corrente.");
                }

                if (dSoppres.before(dIstituz)) {
                    getMessageBox().addError(
                            "La data di fine validità deve essere maggiore della data di inizio validità dell'attuale versione corrente.");
                }

                if (!trasformazioniHelper.isVersionDateUnique(idTrasf.longValue(), dIstituz)) {
                    getMessageBox().addError(
                            "La data di inizio validità \"" + dtIstituz + "\" è uguale ad una versione precedente.");
                }

            }
            String kettleId = "";

            if (!getMessageBox().hasError()) {
                if (zipPackage != null) {
                    try (ByteArrayInputStream bis = new ByteArrayInputStream(zipPackage)) {
                        // controlla che il pacchetto ricevuto sia uno zip
                        TikaConfig tikaConfig = new TikaConfig();
                        Detector detector = tikaConfig.getDetector();
                        Metadata metadata = new Metadata();
                        String mediaType = detector.detect(new ByteArrayInputStream(zipPackage), metadata).getBaseType()
                                .toString();
                        if (!mediaType.contains(Constants.TRANSFORMATION_PKG_MIME_TYPE)) {
                            logger.error(
                                    "Errore nell'inserimento della trasformazione: il pacchetto non è un archivio ZIP.");
                            getMessageBox().addError("Il pacchetto della trasformazione deve essere un archivio ZIP.");
                        }

                        ZipInputStream zipInputStream = new ZipInputStream(bis);
                        ZipEntry entry;
                        int directoryCount = 0;
                        boolean mainJobFound = false;

                        while ((entry = zipInputStream.getNextEntry()) != null) {
                            if (entry.isDirectory()) {
                                directoryCount++;
                                kettleId = entry.getName();
                            } else if (entry.getName().equals(kettleId + Constants.XFO_MAIN_JOB_FILENAME)) {
                                mainJobFound = true;
                            }
                        }

                        if (directoryCount != 1) {
                            logger.error(
                                    "Errore nell'inserimento della trasformazione: il pacchetto non contiene la cartella della trasformazione o ne contiene più di una.");
                            getMessageBox().addError(
                                    "Errore nell'inserimento della trasformazione: il pacchetto non contiene la cartella della trasformazione o ne contiene più di una.");
                        }

                        if (!mainJobFound) {
                            logger.error(
                                    "Errore nell'inserimento della trasformazione: il pacchetto non contiene il job d'avvio.");
                            getMessageBox().addError(
                                    "Errore nell'inserimento della trasformazione: il pacchetto non contiene il job d'avvio");
                        }

                        String oldKettleId = trasformazioniHelper.retriveKettleId(idTrasf.longValue());

                        // controlla che la nuova trasformazione abbia lo stesso nome di quella precedente
                        if (!kettleId.isEmpty() && !oldKettleId.isEmpty() && !kettleId.equals(oldKettleId)) {
                            logger.error("Errore nell'inserimento della trasformazione: l'id kettle " + kettleId
                                    + " è differente dal precedente id " + oldKettleId + ".");
                            getMessageBox().addError("Errore nell'inserimento della trasformazione: l'id kettle "
                                    + kettleId + " è differente dal precedente id " + oldKettleId + ".");
                        }
                    } catch (TikaException | IOException ex) {
                        logger.error(
                                "Errore durante l'apertura del pacchetto della trasformazione: " + ex.getMessage());
                        getMessageBox().addError(
                                "Errore durante l'apertura del pacchetto della trasformazione: " + ex.getMessage());
                    }
                } else {
                    // se la nuova versione non ha un pacchetto, l'operazione non viene effettuata.
                    logger.error(
                            "Errore nell'inserimento della trasformazione: il pacchetto della trasformazione è necessario.");
                    getMessageBox().addError(
                            "Errore nell'inserimento della trasformazione: il pacchetto della trasformazione è necessario.");
                }
            }

            if (!getMessageBox().hasError()) {
                XfoTrasf oldTrasf = trasformazioniHelper.findById(XfoTrasf.class, idTrasf);
                repositoryManager.storicizeVersion(oldTrasf, dIstituz);
                repositoryManager.updateTransformationNoNewTransition(idTrasf.longValue(), cdTrasf,
                        oldTrasf.getDsTrasf(), flAttiva, version, dsVersion, dIstituz, dSoppres, zipPackage, kettleId);

                xfoTrasRowBean = repositoryManager.getXfoTrasRowBean(idTrasf.longValue());
                reloadTransformationDetail(xfoTrasRowBean);

            }
        } catch (ParerUserError ex) {
            getMessageBox().addError(ex.getDescription());
        }
    }

    private void saveDettaglioInserimentoNuovaTrasformazione() throws EMFError {
        try {
            byte[] fileBytes = getForm().getInserisciTrasformazione().getTrans_blob().getFileBytes();
            String fileName = getForm().getInserisciTrasformazione().getTrans_blob().getValue();
            String name = getForm().getInserisciTrasformazione().getTrans_name().getValue().trim();
            String description = getForm().getInserisciTrasformazione().getTrans_description().getValue();
            String version = getForm().getInserisciTrasformazione().getTrans_version().getValue().trim();
            String versionDescription = getForm().getInserisciTrasformazione().getTrans_version_description()
                    .getValue();
            String enabled = getForm().getInserisciTrasformazione().getTrans_enabled().parse();
            Timestamp dtIstituz = getForm().getInserisciTrasformazione().getDt_istituz().parse();
            Timestamp dtSoppres = getForm().getInserisciTrasformazione().getDt_soppres().parse();
            String oraIstituz = getForm().getInserisciTrasformazione().getOre_dt_ist().parse();
            String oraSoppres = getForm().getInserisciTrasformazione().getOre_dt_sop().parse();
            String minutiIstituz = getForm().getInserisciTrasformazione().getMinuti_dt_ist().parse();
            String minutiSoppres = getForm().getInserisciTrasformazione().getMinuti_dt_sop().parse();

            // FIXME causa bug nella getsione dei checkbox in un post multipart su spago (manca un clear() facciamo
            // così.
            if (enabled == null) {
                enabled = Constants.ComboFlag.NO.getValue();
            }

            if (fileBytes == null && enabled.equals(Constants.ComboFlag.SI.getValue())) {
                getMessageBox().addError("Una trasformazione senza pacchetto non può essere in stato\"attivo\".");
            }
            // il nome è obbligatorio...
            if (StringUtils.isBlank(name)) {
                getMessageBox().addError("Il campo \"nome\" della trasformazione è obbligatorio.");
            } // ... e deve essere univoco
            else if (trasformazioniHelper.transformationNameExists(name)) {
                getMessageBox().addError("La trasformazione " + name + " esiste già.");
            }

            if (StringUtils.isBlank(description)) {
                description = "--";
            }

            // la versione è obbligatoria
            if (StringUtils.isBlank(version)) {
                getMessageBox().addError("Il campo \"versione\" della trasformazione è obbligatorio.");
            }

            if (StringUtils.isBlank(versionDescription)) {
                versionDescription = "--";
            }

            if (name.length() > getForm().getInserisciTrasformazione().getTrans_name().getMaxLength()) {
                getMessageBox().addError("Il nome della trasformazione non deve superare i "
                        + getForm().getInserisciTrasformazione().getTrans_name().getMaxLength() + "caratteri.");
            }

            if (description.length() > getForm().getInserisciTrasformazione().getTrans_description().getMaxLength()) {
                getMessageBox().addError("La descrizione della versione della trasformazione non deve superare i "
                        + getForm().getInserisciTrasformazione().getTrans_description().getMaxLength() + "caratteri.");
            }

            if (version.length() > getForm().getInserisciTrasformazione().getTrans_version().getMaxLength()) {
                getMessageBox().addError("La versione della trasformazione non deve superare i "
                        + getForm().getInserisciTrasformazione().getTrans_version().getMaxLength() + "caratteri.");
            }

            if (versionDescription.length() > getForm().getInserisciTrasformazione().getTrans_version_description()
                    .getMaxLength()) {
                getMessageBox().addError("La descrizione della versione della trasformazione non deve superare i "
                        + getForm().getInserisciTrasformazione().getTrans_version_description().getMaxLength()
                        + "caratteri.");
            }

            BigDecimal oraIstituzBD = null;
            BigDecimal minutiIstituzBD = null;
            BigDecimal oraSoppresBD = null;
            BigDecimal minutiSoppresBD = null;
            Date dIstituz = null;
            Date dSoppres = null;

            if (!StringUtils.isBlank(oraIstituz) && !StringUtils.isBlank(minutiIstituz)
                    && !StringUtils.isBlank(oraSoppres) && !StringUtils.isBlank(minutiSoppres)) {
                try {
                    oraIstituzBD = new BigDecimal(oraIstituz);
                    minutiIstituzBD = new BigDecimal(minutiIstituz);
                    oraSoppresBD = new BigDecimal(oraSoppres);
                    minutiSoppresBD = new BigDecimal(minutiSoppres);
                } catch (Exception ex) {
                    getMessageBox().addError("Data e ora devono essere espressi in numeri.");
                }

                TypeValidator validator = new TypeValidator(getMessageBox());
                dIstituz = validator.validateSingleDate(dtIstituz, oraIstituzBD, minutiIstituzBD,
                        getForm().getInserimentoNuovaVersione().getDt_istituz().getDescription());
                dSoppres = validator.validateSingleDate(dtSoppres, oraSoppresBD, minutiSoppresBD,
                        getForm().getInserimentoNuovaVersione().getDt_soppres().getDescription());

                if (dSoppres.before(dIstituz)) {
                    getMessageBox().addError("La data di inizio validità deve anteriore alla data di fine validità.");
                }
            } else {
                if (StringUtils.isBlank(oraIstituz) || StringUtils.isBlank(minutiIstituz)) {
                    getMessageBox().addError("L'ora di inizio validità è obbligatoria.");
                }

                if (StringUtils.isBlank(oraSoppres) || StringUtils.isBlank(minutiSoppres)) {
                    getMessageBox().addError("L'ora di fine validità è obbligatoria.");
                }
            }

            String kettleId = "";

            if (!getMessageBox().hasError() && fileBytes != null) {
                try (ByteArrayInputStream bis = new ByteArrayInputStream(fileBytes)) {
                    // controlla che il pacchetto ricevuto sia uno zip
                    TikaConfig tikaConfig = new TikaConfig();
                    Detector detector = tikaConfig.getDetector();
                    Metadata metadata = new Metadata();
                    String mediaType = detector.detect(bis, metadata).getBaseType().toString();
                    if (!mediaType.contains(Constants.TRANSFORMATION_PKG_MIME_TYPE)) {
                        logger.error(
                                "Errore nell'inserimento della trasformazione: il pacchetto non è un archivio ZIP.");
                        getMessageBox().addError("Il pacchetto della trasformazione deve essere un archivio ZIP.");
                    }

                    ZipInputStream zipInputStream = new ZipInputStream(bis);
                    ZipEntry entry;
                    int directoryCount = 0;
                    boolean mainJobFound = false;

                    while ((entry = zipInputStream.getNextEntry()) != null) {
                        if (entry.isDirectory()) {
                            directoryCount++;
                            kettleId = entry.getName();
                        } else if (entry.getName().equals(kettleId + Constants.XFO_MAIN_JOB_FILENAME)) {
                            mainJobFound = true;
                        }
                    }

                    if (directoryCount != 1) {
                        logger.error(
                                "Errore nell'inserimento della trasformazione: il pacchetto non contiene la cartella della trasformazione o ne contiene più di una.");
                        getMessageBox().addError(
                                "Errore nell'inserimento della trasformazione: il pacchetto non contiene la cartella della trasformazione o ne contiene più di una.");
                    }

                    if (!mainJobFound) {
                        logger.error(
                                "Errore nell'inserimento della trasformazione: il pacchetto non contiene il job d'avvio.");
                        getMessageBox().addError(
                                "Errore nell'inserimento della trasformazione: il pacchetto non contiene il job d'avvio");
                    }

                    try {
                        if (repositoryManager.isTransformationAlreadyPresent(kettleId)) {
                            logger.error(
                                    "Errore nell'inserimento della trasformazione: la trasformazione con identificativo "
                                            + kettleId + " è già presente nel repository.");
                            getMessageBox().addError(
                                    "Errore nell'inserimento della trasformazione: la trasformazione con identificativo "
                                            + kettleId + " è già presente nel repository.");
                        }
                    } catch (KettleException ex) {
                        logger.error(
                                "Errore nell'inserimento della trasformazione: impossibile contattare il server Kettle.");
                        getMessageBox().addError(
                                "Errore nell'inserimento della trasformazione: impossibile contattare il server Kettle.",
                                ex);
                    }
                } catch (TikaException | IOException ex) {
                    logger.error("Errore durante l'apertura del pacchetto della trasformazione: " + ex.getMessage());
                    getMessageBox().addError(
                            "Errore durante l'apertura del pacchetto della trasformazione: " + ex.getMessage());
                }
            }

            if (!getMessageBox().hasError()) {
                // crea il nuovo record su XFO_TRANS e salva i jobs e le trasformazioni sul repository kettle
                long transformationId = repositoryManager.insertNewTransformation(name, description, enabled, version,
                        versionDescription, dIstituz, dSoppres, fileBytes, kettleId);
                XfoTrasfRowBean xfoTrasRowBean = repositoryManager.getXfoTrasRowBean(transformationId);
                reloadTransformationDetail(xfoTrasRowBean);
            }
        } catch (ParerUserError ex) {
            getMessageBox().addError(ex.getDescription());
        } catch (Exception ex) {
            logger.error(
                    "Errore generico durante l'inserimento del pacchetto della trasformazione: " + ex.getMessage());
            getMessageBox().addError("Errore generico durante l'inserimento del pacchetto della trasformazione.");
        }
    }

    private void reloadTransformationDetail(XfoTrasfRowBean xfoTrasRowBean) throws EMFError, ParerUserError {
        getForm().getTransformationDetail().copyFromBean(xfoTrasRowBean);

        SimpleDateFormat sdf = new SimpleDateFormat("HH");
        getForm().getTransformationDetail().getOre_dt_ist().setValue(sdf.format(xfoTrasRowBean.getDtIstituz()));
        getForm().getTransformationDetail().getOre_dt_sop().setValue(sdf.format(xfoTrasRowBean.getDtSoppres()));

        sdf = new SimpleDateFormat("mm");
        getForm().getTransformationDetail().getMinuti_dt_ist().setValue(sdf.format(xfoTrasRowBean.getDtIstituz()));
        getForm().getTransformationDetail().getMinuti_dt_sop().setValue(sdf.format(xfoTrasRowBean.getDtSoppres()));

        long idTrasf = xfoTrasRowBean.getIdTrasf().longValue();

        XfoSetParamTrasfTableBean tableBean = parametersManager.searchParametersSetsByTransformation(idTrasf);
        getForm().getParametersSetList().getFl_set_param_ark().setDecodeMap(ComboGetter.getMappaParametersSetType());
        getForm().getParametersSetList().setTable(tableBean);
        getForm().getParametersSetList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
        getForm().getParametersSetList().getTable().first();

        XfoStoricoTrasfTableBean storicoTableBean = repositoryManager.searchVersionsByTransformation(idTrasf);
        getForm().getVersionsList().setTable(storicoTableBean);
        getForm().getVersionsList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
        getForm().getVersionsList().getTable().first();

        PigTipoObjectTableBean tipiObjectsTableBean = repositoryManager.searchAssignedPigTipoObjects(idTrasf);
        getForm().getTipiOggettoList().setTable(tipiObjectsTableBean);
        getForm().getTipiOggettoList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
        getForm().getTipiOggettoList().getTable().first();
    }
}
