/*
 * Engineering Ingegneria Informatica S.p.A.
 *
 * Copyright (C) 2023 Regione Emilia-Romagna <p/> This program is free software: you can
 * redistribute it and/or modify it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version. <p/> This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Affero General Public License for more details. <p/> You should
 * have received a copy of the GNU Affero General Public License along with this program. If not,
 * see <https://www.gnu.org/licenses/>.
 */

package it.eng.sacerasi.web.action;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.text.DateFormat;
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
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.ejb.EJB;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.http.entity.ContentType;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import it.eng.parer.jboss.timer.service.JbossTimerEjb;
import it.eng.parer.objectstorage.dto.BackendStorage;
import it.eng.parer.objectstorage.dto.ObjectStorageBackend;
import it.eng.parer.objectstorage.exceptions.ObjectStorageException;
import it.eng.parer.objectstorage.helper.SalvataggioBackendHelper;
import it.eng.sacerasi.annullamento.ejb.AnnullamentoEjb;
import it.eng.sacerasi.common.Constants;
import it.eng.sacerasi.common.ejb.CommonDb;
import it.eng.sacerasi.entity.PigKSInstance;
import it.eng.sacerasi.entity.PigObject;
import it.eng.sacerasi.entity.PigTipoObject;
import it.eng.sacerasi.exception.ParerInternalError;
import it.eng.sacerasi.exception.ParerUserError;
import it.eng.sacerasi.messages.MessaggiEjb;
import it.eng.sacerasi.slite.gen.Application;
import it.eng.sacerasi.slite.gen.action.MonitoraggioAbstractAction;
import it.eng.sacerasi.slite.gen.form.GestioneJobForm;
import it.eng.sacerasi.slite.gen.form.MonitoraggioForm;
import it.eng.sacerasi.slite.gen.form.MonitoraggioForm.FiltriJobSchedulati;
import it.eng.sacerasi.slite.gen.form.MonitoraggioForm.FiltriReplicaOrg;
import it.eng.sacerasi.slite.gen.form.MonitoraggioForm.SessioniErrateDetail;
import it.eng.sacerasi.slite.gen.tablebean.PigAmbienteVersRowBean;
import it.eng.sacerasi.slite.gen.tablebean.PigAmbienteVersTableBean;
import it.eng.sacerasi.slite.gen.tablebean.PigClasseErroreTableBean;
import it.eng.sacerasi.slite.gen.tablebean.PigErroreTableBean;
import it.eng.sacerasi.slite.gen.tablebean.PigInfoDicomRowBean;
import it.eng.sacerasi.slite.gen.tablebean.PigObjectRowBean;
import it.eng.sacerasi.slite.gen.tablebean.PigPrioritaObjectTableBean;
import it.eng.sacerasi.slite.gen.tablebean.PigSessioneIngestRowBean;
import it.eng.sacerasi.slite.gen.tablebean.PigStatoSessioneIngestTableBean;
import it.eng.sacerasi.slite.gen.tablebean.PigTipoObjectRowBean;
import it.eng.sacerasi.slite.gen.tablebean.PigTipoObjectTableBean;
import it.eng.sacerasi.slite.gen.tablebean.PigVersTableBean;
import it.eng.sacerasi.slite.gen.tablebean.PigXmlAnnulSessioneIngestRowBean;
import it.eng.sacerasi.slite.gen.viewbean.IamVLisOrganizDaReplicTableBean;
import it.eng.sacerasi.slite.gen.viewbean.MonVLisFileObjectRowBean;
import it.eng.sacerasi.slite.gen.viewbean.MonVLisFileObjectTableBean;
import it.eng.sacerasi.slite.gen.viewbean.MonVLisObjNonVersTableBean;
import it.eng.sacerasi.slite.gen.viewbean.MonVLisObjTrasfRowBean;
import it.eng.sacerasi.slite.gen.viewbean.MonVLisSesErrateRowBean;
import it.eng.sacerasi.slite.gen.viewbean.MonVLisSesErrateTableBean;
import it.eng.sacerasi.slite.gen.viewbean.MonVLisUnitaDocObjectTableBean;
import it.eng.sacerasi.slite.gen.viewbean.MonVLisUnitaDocObjectTableDescriptor;
import it.eng.sacerasi.slite.gen.viewbean.MonVLisUnitaDocSessioneRowBean;
import it.eng.sacerasi.slite.gen.viewbean.MonVLisUnitaDocSessioneTableBean;
import it.eng.sacerasi.slite.gen.viewbean.MonVLisUnitaDocSessioneTableDescriptor;
import it.eng.sacerasi.slite.gen.viewbean.MonVLisVersFallitiRowBean;
import it.eng.sacerasi.slite.gen.viewbean.MonVLisVersFallitiTableBean;
import it.eng.sacerasi.slite.gen.viewbean.MonVLisVersObjNonVersRowBean;
import it.eng.sacerasi.slite.gen.viewbean.MonVLisVersObjNonVersTableBean;
import it.eng.sacerasi.slite.gen.viewbean.MonVLisVersObjTableBean;
import it.eng.sacerasi.slite.gen.viewbean.MonVObjAnnulRangeDtTableBean;
import it.eng.sacerasi.slite.gen.viewbean.MonVObjNonVersRowBean;
import it.eng.sacerasi.slite.gen.viewbean.MonVObjNonVersTableBean;
import it.eng.sacerasi.slite.gen.viewbean.MonVObjRangeDtTableBean;
import it.eng.sacerasi.slite.gen.viewbean.MonVRiepVersTableBean;
import it.eng.sacerasi.slite.gen.viewbean.MonVSesRangeDtRowBean;
import it.eng.sacerasi.slite.gen.viewbean.MonVSesRangeDtTableBean;
import it.eng.sacerasi.slite.gen.viewbean.MonVVisLastSchedJobRowBean;
import it.eng.sacerasi.slite.gen.viewbean.MonVVisObjNonVersRowBean;
import it.eng.sacerasi.slite.gen.viewbean.MonVVisObjRowBean;
import it.eng.sacerasi.slite.gen.viewbean.MonVVisSesErrataRowBean;
import it.eng.sacerasi.slite.gen.viewbean.MonVVisUnitaDocObjectRowBean;
import it.eng.sacerasi.slite.gen.viewbean.MonVVisUnitaDocSessioneRowBean;
import it.eng.sacerasi.slite.gen.viewbean.MonVVisVersFallitoRowBean;
import it.eng.sacerasi.web.dto.MonitoraggioFiltriListaOggDerVersFallitiBean;
import it.eng.sacerasi.web.dto.MonitoraggioFiltriListaOggettiBean;
import it.eng.sacerasi.web.dto.MonitoraggioFiltriListaVersFallitiBean;
import it.eng.sacerasi.web.ejb.AmministrazioneEjb;
import it.eng.sacerasi.web.ejb.GestioneJobEjb;
import it.eng.sacerasi.web.ejb.MonitoraggioEjb;
import it.eng.sacerasi.web.helper.ComboHelper;
import it.eng.sacerasi.web.helper.MonitoraggioHelper;
import it.eng.sacerasi.web.util.CheckNumeric;
import it.eng.sacerasi.web.util.ComboGetter;
import it.eng.sacerasi.web.util.WebConstants;
import it.eng.sacerasi.web.util.WebConstants.SezioneMonitoraggio;
import it.eng.sacerasi.web.util.XmlPrettyPrintFormatter;
import it.eng.sacerasi.web.validator.MonitoraggioValidator;
import it.eng.sacerasi.ws.ejb.XmlContextCache;
import it.eng.sacerasi.xml.unitaDocumentaria.UnitaDocumentariaType;
import it.eng.sacerasi.xml.unitaDocumentaria.VersatoreType;
import it.eng.sacerasixml.xsd.util.Utils;
import it.eng.spagoCore.error.EMFError;
import it.eng.spagoLite.ExecutionHistory;
import it.eng.spagoLite.SessionManager;
import it.eng.spagoLite.db.base.BaseRowInterface;
import it.eng.spagoLite.db.base.BaseTableInterface;
import it.eng.spagoLite.db.base.row.BaseRow;
import it.eng.spagoLite.db.base.table.AbstractBaseTable;
import it.eng.spagoLite.db.base.table.BaseTable;
import it.eng.spagoLite.db.decodemap.DecodeMapIF;
import it.eng.spagoLite.db.oracle.decode.DecodeMap;
import it.eng.spagoLite.form.base.BaseElements.Status;
import it.eng.spagoLite.form.fields.Field;
import it.eng.spagoLite.form.fields.Fields;
import it.eng.spagoLite.form.fields.SingleValueField;
import it.eng.spagoLite.form.fields.impl.Button;
import it.eng.spagoLite.form.fields.impl.CheckBox;
import it.eng.spagoLite.form.fields.impl.ComboBox;
import it.eng.spagoLite.form.fields.impl.Input;
import it.eng.spagoLite.message.Message;
import it.eng.spagoLite.message.Message.MessageLevel;
import it.eng.spagoLite.message.MessageBox;
import it.eng.spagoLite.message.MessageBox.ViewMode;
import it.eng.spagoLite.security.Secure;
import it.eng.spagoLite.security.menu.impl.Menu;
import it.eng.xformer.helper.TrasformazioniHelper;

import java.util.logging.Level;
import javax.xml.XMLConstants;

import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

/**
 * @author Gilioli_P
 */
@SuppressWarnings({
	"rawtypes", "unchecked" })
public class MonitoraggioAction extends MonitoraggioAbstractAction {

    public static final String FROM_GESTIONE_JOB = "fromGestioneJob";

    public static final String ECCEZIONE_MSG = "Eccezione";
    private static Logger log = LoggerFactory.getLogger(MonitoraggioAction.class.getName());

    public static final String ERRORE_RECUPERO_FILE = "Errore nel recupero dei file per il download";
    private static final String WARNING_NO_JOB_SELEZIONATO = "Attenzione: nessun JOB selezionato";

    @EJB(mappedName = "java:app/SacerAsync-ejb/MonitoraggioHelper")
    private MonitoraggioHelper monitoraggioHelper;
    @EJB(mappedName = "java:app/SacerAsync-ejb/ComboHelper")
    private ComboHelper comboHelper;
    @EJB(mappedName = "java:app/SacerAsync-ejb/MonitoraggioEjb")
    private MonitoraggioEjb monitoraggioEjb;
    @EJB(mappedName = "java:app/SacerAsync-ejb/CommonDb")
    private CommonDb commonDb;
    @EJB(mappedName = "java:app/SacerAsync-ejb/MessaggiEjb")
    private MessaggiEjb messaggiEjb;
    @EJB(mappedName = "java:app/SacerAsync-ejb/AmministrazioneEjb")
    private AmministrazioneEjb amministrazioneEjb;
    @EJB(mappedName = "java:app/SacerAsync-ejb/AnnullamentoEjb")
    private AnnullamentoEjb annullamentoEjb;
    @EJB(mappedName = "java:app/JbossTimerWrapper-ejb/JbossTimerEjb")
    private JbossTimerEjb jbossTimerEjb;
    @EJB(mappedName = "java:app/SacerAsync-ejb/TrasformazioniHelper")
    private TrasformazioniHelper trasformazioniHelper;
    @EJB(mappedName = "java:app/SacerAsync-ejb/SalvataggioBackendHelper")
    private SalvataggioBackendHelper salvataggioBackendHelper;
    @EJB(mappedName = "java:app/SacerAsync-ejb/GestioneJobEjb")
    private GestioneJobEjb gestioneJobEjb;
    // singleton ejb di gestione cache dei parser Castor
    @EJB(mappedName = "java:app/SacerAsync-ejb/XmlContextCache")
    XmlContextCache xmlContextCache;

    private static final String GET_ID_OBJ_STACK = "getIdObjStack";
    private static final String ID_OBJECT = "idObject";
    private static final String ID_SESSIONE_INGEST = "id_sessione_ingest";
    private static final String STUDIO_DICOM = "StudioDicom";

    @Override
    public void initOnClick() throws EMFError {
	//
    }

    /**
     * Metodo che fornisce uno stack utilizzato per mantenere gli id delle unità  documentarie
     * visualizzate unitÃ  documentarie visualizzate
     *
     * @return lo stack di unità documentarie
     */
    public List<BigDecimal> getIdObjStack() {
	if (getSession().getAttribute(GET_ID_OBJ_STACK) == null) {
	    getSession().setAttribute(GET_ID_OBJ_STACK, new ArrayList<BigDecimal>());
	}
	return (List<BigDecimal>) getSession().getAttribute(GET_ID_OBJ_STACK);
    }

    /**
     * Carica i dati del dettaglio di un record selezionato da una lista
     *
     * @throws EMFError errore generico
     */
    @Override
    public void loadDettaglio() throws EMFError {
	String lista = getTableName();
	try {
	    if (lista != null) {
		XmlPrettyPrintFormatter formatter = new XmlPrettyPrintFormatter();
		/* Se ho cliccato sul dettaglio di una SESSIONE ERRATA */
		if (lista.equals(getForm().getSessioniErrateList().getName())) {
		    BigDecimal idSessione = ((MonVLisSesErrateRowBean) getForm()
			    .getSessioniErrateList().getTable().getCurrentRow())
			    .getIdSessioneIngest();
		    MonVVisSesErrataRowBean sesRowBean = monitoraggioHelper
			    .getMonVVisSesErrataRowBean(idSessione);
		    getForm().getSessioniErrateDetail().copyFromBean(sesRowBean);
		    getForm().getSessioniErrateDetail().getVersatore().setValue(
			    sesRowBean.getNmAmbienteVers() + " , " + sesRowBean.getNmVers());
		    if (sesRowBean.getNmTipoObject() != null
			    && sesRowBean.getNmTipoObject().equals(STUDIO_DICOM)) {
			getForm().getSessioniErrateDetail().getInfo_ogg()
				.setValue(calcInfoObj(sesRowBean.getBlXml()));
		    }

		    if (sesRowBean.getBlXml() == null) {
			getForm().getSessioniErrateDetail().getBl_xml().setHidden(true);
			getForm().getSessioniErrateDetail().getCd_versione_xml_vers()
				.setHidden(true);
		    } else {
			getForm().getSessioniErrateDetail().getBl_xml().setHidden(false);
			getForm().getSessioniErrateDetail().getCd_versione_xml_vers()
				.setHidden(false);
			getForm().getSessioniErrateDetail().getDownloadXmlSessione().setEditMode();
		    }

		    getForm().getSessioniErrateDetail().setStatus(Status.view);
		    getForm().getSessioniErrateDetail().setViewMode();
		    getForm().getSessioniErrateList().setStatus(Status.view);
		    /* Se ho cliccato sul dettaglio di un OGGETTO */
		} else if (getForm().getOggettiList().getName().equals(lista)
			|| getForm().getOggettoDetailOggettiDCMHashList().getName().equals(lista)) {
		    // Ottengo l'id oggetto e lo salvo in getSession()
		    BigDecimal idObject;
		    if (getForm().getOggettiList().getName().equals(lista)) {
			idObject = getForm().getOggettiList().getTable().getCurrentRow()
				.getBigDecimal("id_object");
		    } else {
			idObject = getForm().getOggettoDetailOggettiDCMHashList().getTable()
				.getCurrentRow().getBigDecimal("id_object");
		    }
		    if (getNavigationEvent().equals(NE_DETTAGLIO_VIEW)) {
			List<BigDecimal> idObjStack = getIdObjStack();
			idObjStack.add(idObject);
			getSession().setAttribute(GET_ID_OBJ_STACK, idObjStack);
		    }
		    getSession().setAttribute(ID_OBJECT, idObject);

		    loadDettaglioObject(idObject);
		    /*
		     * Se ho cliccato sul dettaglio di una UNITA' DOCUMENTARIA dal dettaglio oggetto
		     */
		} else if (getForm().getOggettoDetailUnitaDocList().getName().equals(lista)) {
		    getForm().getUnitaDocDetail().setStatus(Status.view);

		    // Ottengo l'id unita doc e lo salvo in getSession()
		    BigDecimal idUnitaDocObject = getForm().getOggettoDetailUnitaDocList()
			    .getTable().getCurrentRow().getBigDecimal("id_unita_doc_object");
		    getSession().setAttribute("idUnitaDocObject", idUnitaDocObject);
		    // Carico il rowbean del dettaglio unitÃ  doc.
		    MonVVisUnitaDocObjectRowBean objRB = monitoraggioHelper
			    .getMonVVisUnitaDocObjectRowBean(idUnitaDocObject);
		    getForm().getUnitaDocDetail().copyFromBean(objRB);
		    // MAC 29700 - se lo recupero dal form l'xml è troncato prima che possa essere
		    // formattato,
		    // impendendo una corretta formattazione. Ora lo recupero direttamente
		    // dall'entity e poi lo
		    // riimmetto nel form.
		    String xmlvers = objRB.getBlXmlVersSacer();
		    String xmlindice = getForm().getUnitaDocDetail().getBl_xml_indice_sacer()
			    .parse();
		    if (xmlvers != null) {
			xmlvers = formatter.prettyPrintWithDOM3LS(xmlvers);
			getForm().getUnitaDocDetail().getBl_xml_vers_sacer().setValue(xmlvers);
			getForm().getUnitaDocDetail().getBl_xml_vers_sacer().setViewMode();
		    }
		    if (xmlindice != null) {
			xmlindice = formatter.prettyPrintWithDOM3LS(xmlindice);
			getForm().getUnitaDocDetail().getBl_xml_indice_sacer().setValue(xmlindice);
		    }

		    // MEV 31639
		    getForm().getUnitaDocDetail().getFl_xml_modificato()
			    .setValue(objRB.getFlXmlMod());

		    // Se ho cliccato sul dettaglio di un Versamento Fallito venendo da più parti
		} else if (getForm().getVersamentiList().getName().equals(lista)
			|| getForm().getOggettoDaVersamentiFallitiDetailVersamentiList().getName()
				.equals(lista)
			|| getForm().getOggettoDetailSessioniList().getName().equals(lista)) {

		    BigDecimal idSessioneIngest;
		    // Ottengo l'id sessione e lo salvo in sessione
		    if (lista.equals(getForm().getVersamentiList().getName())) {
			idSessioneIngest = getForm().getVersamentiList().getTable().getCurrentRow()
				.getBigDecimal(ID_SESSIONE_INGEST);
		    } else if (lista.equals(getForm()
			    .getOggettoDaVersamentiFallitiDetailVersamentiList().getName())) {
			idSessioneIngest = getForm()
				.getOggettoDaVersamentiFallitiDetailVersamentiList().getTable()
				.getCurrentRow().getBigDecimal(ID_SESSIONE_INGEST);
		    } else {
			idSessioneIngest = getForm().getOggettoDetailSessioniList().getTable()
				.getCurrentRow().getBigDecimal(ID_SESSIONE_INGEST);
		    }

		    getSession().setAttribute("idSessioneIngest", idSessioneIngest);
		    // Carico il rowbean del dettaglio versamenti falliti
		    MonVVisVersFallitoRowBean versRB = monitoraggioHelper
			    .getMonVVisVersFallitoRowBean(idSessioneIngest);
		    getForm().getVersamentoDetail().copyFromBean(versRB);
		    getForm().getVersamentiButtonList().getFiltraUnitaDocVers().setEditMode();
		    getForm().getVersamentiButtonList().getFiltraUnitaDocVers().setHidden(true);

		    getForm().getVersamentoTabs().getFiltriUnitaDocVers().setHidden(true);
		    if (versRB.getIdObject() != null
			    && monitoraggioEjb.checkTiVersPigObject(versRB.getIdObject(),
				    Constants.TipoVersamento.ZIP_NO_XML_SACER.name(),
				    Constants.TipoVersamento.ZIP_CON_XML_SACER.name())
			    && monitoraggioEjb.checkTiStatoPigObject(versRB.getIdObject(),
				    Constants.StatoOggetto.CHIUSO_ERR_VERIFICA_HASH.name(),
				    Constants.StatoOggetto.CHIUSO_ERR_SCHED.name(),
				    Constants.StatoOggetto.IN_ATTESA_VERS.name(),
				    Constants.StatoOggetto.CHIUSO_ERR_CODA.name(),
				    Constants.StatoOggetto.IN_CODA_VERS.name(),
				    Constants.StatoOggetto.CHIUSO_ERR_VERS.name(),
				    Constants.StatoOggetto.CHIUSO_ERR_RECUPERABILE.name(),
				    Constants.StatoOggetto.CHIUSO_OK.name(),
				    Constants.StatoOggetto.ANNULLATO.name(),
				    Constants.StatoOggetto.IN_CORSO_ANNULLAMENTO.name())) {
			getForm().getVersamentoTabs().getFiltriUnitaDocVers().setHidden(false);
			getForm().getVersamentiButtonList().getFiltraUnitaDocVers()
				.setHidden(false);
			getForm().getFiltriUnitaDocVers().reset();
			getForm().getFiltriUnitaDocVers().setEditMode();

			getForm().getFiltriUnitaDocVers().getFl_struttura_non_definita()
				.setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());
			getForm().getFiltriUnitaDocVers().getFl_vers_simulato()
				.setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());

			getForm().getFiltriUnitaDocVers().getCd_registro_unita_doc_sacer()
				.setDecodeMap(DecodeMap.Factory.newInstance(monitoraggioHelper
					.getDistinctColumnFromMonVLisUnitaDocSessioneTableBean(
						versRB.getIdSessioneIngest(), String.class, null,
						MonVLisUnitaDocSessioneTableDescriptor.COL_CD_REGISTRO_UNITA_DOC_SACER),
					MonVLisUnitaDocSessioneTableDescriptor.COL_CD_REGISTRO_UNITA_DOC_SACER,
					MonVLisUnitaDocSessioneTableDescriptor.COL_CD_REGISTRO_UNITA_DOC_SACER));
			getForm().getFiltriUnitaDocVers().getAa_unita_doc_sacer()
				.setDecodeMap(DecodeMap.Factory.newInstance(monitoraggioHelper
					.getDistinctColumnFromMonVLisUnitaDocSessioneTableBean(
						versRB.getIdSessioneIngest(), BigDecimal.class,
						null,
						MonVLisUnitaDocSessioneTableDescriptor.COL_AA_UNITA_DOC_SACER),
					MonVLisUnitaDocSessioneTableDescriptor.COL_AA_UNITA_DOC_SACER,
					MonVLisUnitaDocSessioneTableDescriptor.COL_AA_UNITA_DOC_SACER
						+ "_str"));
			getForm().getFiltriUnitaDocVers().getTi_stato_unita_doc_object()
				.setDecodeMap(DecodeMap.Factory.newInstance(monitoraggioHelper
					.getDistinctColumnFromMonVLisUnitaDocSessioneTableBean(
						versRB.getIdSessioneIngest(), String.class, null,
						MonVLisUnitaDocSessioneTableDescriptor.COL_TI_STATO_UNITA_DOC_SESSIONE),
					MonVLisUnitaDocSessioneTableDescriptor.COL_TI_STATO_UNITA_DOC_SESSIONE,
					MonVLisUnitaDocSessioneTableDescriptor.COL_TI_STATO_UNITA_DOC_SESSIONE));
			// Nuovo metodo di gestione errori MAC#13039
			getForm().getFiltriUnitaDocVers().getCd_concat_dl_err_sacer()
				.setDecodeMap(DecodeMap.Factory.newInstance(
					monitoraggioHelper
						.getCdErrSacerFromMonVLisUnitaDocSessioneTableBean(
							versRB.getIdSessioneIngest()),
					MonVLisUnitaDocSessioneTableDescriptor.COL_CD_ERR_SACER,
					MonVLisUnitaDocSessioneTableDescriptor.COL_CD_CONCAT_DL_ERR_SACER));

			getForm().getFiltriUnitaDocVers().getNm_ambiente()
				.setDecodeMap(DecodeMap.Factory.newInstance(monitoraggioHelper
					.getDistinctColumnFromMonVLisUnitaDocSessioneTableBean(
						versRB.getIdSessioneIngest(), String.class, null,
						MonVLisUnitaDocSessioneTableDescriptor.COL_NM_AMBIENTE),
					MonVLisUnitaDocSessioneTableDescriptor.COL_NM_AMBIENTE,
					MonVLisUnitaDocSessioneTableDescriptor.COL_NM_AMBIENTE));
		    }
		    // Carico la lista delle unità  documentarie
		    MonVLisUnitaDocSessioneTableBean udSessioneTB = monitoraggioHelper
			    .getMonVLisUnitaDocSessioneTableBean(idSessioneIngest);
		    getForm().getUnitaDocDaVersamentiFallitiList().setTable(udSessioneTB);
		    getForm().getUnitaDocDaVersamentiFallitiList().getTable().setPageSize(10);
		    getForm().getUnitaDocDaVersamentiFallitiList().getTable().first();
		    // Carico la lista stati versamenti
		    PigStatoSessioneIngestTableBean statiTb = monitoraggioEjb
			    .getPigStatoSessioneIngestTableBean(idSessioneIngest);
		    getForm().getStatiVersamentiList().setTable(statiTb);
		    getForm().getStatiVersamentiList().getTable().setPageSize(10);
		    getForm().getStatiVersamentiList().getTable().first();

		    String report;
		    try {
			report = trasformazioniHelper.getSessionReport(idSessioneIngest);
			getForm().getVersamentoDetail().getReport_xml().setValue(report);
		    } catch (IOException | TransformerException | ObjectStorageException ex) {
			//
		    }
		} // Se ho cliccato sul dettaglio di un Oggetto derivante da versamenti falliti
		else if (getForm().getOggettiDaVersamentiFallitiList().getName().equals(lista)) {
		    // MAC#24891: Correzione della funzionalità per settare a non risolubile una
		    // sessione annullata
		    caricaDettaglioProvenendoDaListaVersamentiFalliti();
		} else if (getForm().getUnitaDocDaVersamentiFallitiList().getName().equals(lista)) {
		    MonVLisUnitaDocSessioneRowBean currentRow = (MonVLisUnitaDocSessioneRowBean) getForm()
			    .getUnitaDocDaVersamentiFallitiList().getTable().getCurrentRow();
		    BigDecimal idUnitaDocSessione = currentRow.getIdUnitaDocSessione();
		    MonVVisUnitaDocSessioneRowBean detailRow = monitoraggioEjb
			    .getMonVVisUnitaDocSessioneRowBean(idUnitaDocSessione);

		    getForm().getUnitaDocVersamentoDetail().copyFromBean(detailRow);
		    String xmlvers = detailRow.getBlXmlVersSacer();
		    String xmlindice = getForm().getUnitaDocVersamentoDetail()
			    .getBl_xml_indice_sacer().parse();
		    if (xmlvers != null) {
			xmlvers = formatter.prettyPrintWithDOM3LS(xmlvers);
			getForm().getUnitaDocVersamentoDetail().getBl_xml_vers_sacer()
				.setValue(xmlvers);
		    }
		    if (xmlindice != null) {
			xmlindice = formatter.prettyPrintWithDOM3LS(xmlindice);
			getForm().getUnitaDocVersamentoDetail().getBl_xml_indice_sacer()
				.setValue(xmlindice);
		    }
		    getForm().getUnitaDocVersamentoDetail().getDownloadXMLUnitaDocVers()
			    .setEditMode();
		}
	    }
	} catch (ParerUserError ex) {
	    getMessageBox().addError(ex.getDescription());
	}
    }

    private void caricaDettaglioProvenendoDaListaVersamentiFalliti() throws EMFError {
	BigDecimal idVers = getForm().getOggettiDaVersamentiFallitiList().getTable().getCurrentRow()
		.getBigDecimal("id_vers");
	String cdKeyObject = getForm().getOggettiDaVersamentiFallitiList().getTable()
		.getCurrentRow().getString("cd_key_object");
	/*
	 * 06/02/2014: tolgo il nmTipoObject. E' stato messo forse erroneamente, perché se ricerco
	 * usando come parametro anche questo valore poi nella lista dei versamenti falliti non
	 * compaiono quelli che hanno questo campo "null" (e può capitare se durante il versamento
	 * si verifica un errore PING-SENDOBJ-007 Il tipo di oggetto da versare immagine non è
	 * valorizzato o è valorizzato con un valore non definito nell'ambito del versatore)
	 * 10/02/2014: lo rimetto. E' corretto che non vengano considerati i versamenti falliti con
	 * nmTipoObject null (e dunque non c'è un oggetto associato)
	 */
	String nmTipoObject = getForm().getOggettiDaVersamentiFallitiList().getTable()
		.getCurrentRow().getString("nm_tipo_object");
	// Carico il rowbean del dettaglio oggetti derivanti da versamenti falliti
	MonVVisObjNonVersRowBean objNonVersRB = monitoraggioHelper
		.getMonVVisObjNonVersRowBean(idVers, cdKeyObject);
	if (objNonVersRB.getCdKeyObject() != null) {
	    getForm().getOggettoDaVersamentiFallitiDetail().copyFromBean(objNonVersRB);
	    getForm().getOggettoDaVersamentiFallitiDetail().setViewMode();
	    // Carico la lista versamenti
	    MonVLisVersObjNonVersTableBean versObjNonVersTB = monitoraggioHelper
		    .getMonVLisVersObjNonVersViewBean(idVers, cdKeyObject);
	    getForm().getOggettoDaVersamentiFallitiDetailVersamentiList()
		    .setTable(versObjNonVersTB);
	    getForm().getOggettoDaVersamentiFallitiDetailVersamentiList().getTable()
		    .setPageSize(10);
	    getForm().getOggettoDaVersamentiFallitiDetailVersamentiList().getTable().first();
	    try {
		/* Controlla se poter editare il flag fl_vers_sacer_da_recup */
		boolean isFlVersSacerDaRecupEditable = monitoraggioEjb.isFlVersSacerDaRecupEditable(
			idVers, cdKeyObject, nmTipoObject, objNonVersRB.getFlVerif());
		setStatusFlVersSacerDaRecup(isFlVersSacerDaRecupEditable);
	    } catch (Exception ex) {
		log.error("Errore nel controllo dell'area FTP"
			+ ExceptionUtils.getRootCauseMessage(ex), ex);
	    }

	    PigTipoObjectRowBean tipoObjRowBean = amministrazioneEjb
		    .getPigTipoObjectRowBean(nmTipoObject, idVers);
	    boolean tipoVersNoTrasf = false;

	    if ((tipoObjRowBean.getTiVersFile() != null) && (tipoObjRowBean.getTiVersFile()
		    .equals(Constants.TipoVersamento.NO_ZIP.name())
		    || tipoObjRowBean.getTiVersFile()
			    .equals(Constants.TipoVersamento.ZIP_CON_XML_SACER.name())
		    || tipoObjRowBean.getTiVersFile()
			    .equals(Constants.TipoVersamento.ZIP_NO_XML_SACER.name()))) {
		tipoVersNoTrasf = true;
	    }
	    /*
	     * Rendo visibile il bottone per impostare la verifica del versamenti se la lista dei
	     * versamenti falliti non è vuota e solo se oggetto non esiste o ha tipo versamento pari
	     * a NO_ZIP o ZIP_CON_XML_SACER o ZIP_NO_XML_SACER
	     */
	    if (versObjNonVersTB.size() != 0
		    && (objNonVersRB.getIdObjectNonVers() == null || tipoVersNoTrasf)) {
		getForm().getOggettiDerVersFallitiButtonList()
			.getImpostaVerificNonRisolubOggettiDaVersFalliti().setEditMode();
		getForm().getOggettiDerVersFallitiButtonList()
			.getImpostaTuttiVerificOggettiDaVersFalliti().setEditMode();
		getForm().getOggettiDerVersFallitiButtonList()
			.getImpostaTuttiNonRisolubOggettiDaVersFalliti().setEditMode();
	    } else {
		getForm().getOggettiDerVersFallitiButtonList()
			.getImpostaVerificNonRisolubOggettiDaVersFalliti().setViewMode();
		getForm().getOggettiDerVersFallitiButtonList()
			.getImpostaTuttiVerificOggettiDaVersFalliti().setViewMode();
		getForm().getOggettiDerVersFallitiButtonList()
			.getImpostaTuttiNonRisolubOggettiDaVersFalliti().setViewMode();
	    }

	    // Visualizzazione bottone ANNULLA_OGGETTO
	    getForm().getOggettiDerVersFallitiButtonList().getAnnullaOggettoDerVersFalliti()
		    .setEditMode();
	    getForm().getOggettiDerVersFallitiButtonList().getAnnullaOggettoDerVersFalliti()
		    .setHidden(true);
	    getForm().getOggettiDerVersFallitiButtonList().getAnnullaVersamentiUDDerVersFalliti()
		    .setEditMode();
	    getForm().getOggettiDerVersFallitiButtonList().getAnnullaVersamentiUDDerVersFalliti()
		    .setHidden(true);
	    // MEV 31134 Nuova logica di visualizzazione del pulsante ANNULLA OGGETTO
	    boolean isLastVerificata = monitoraggioHelper.isLastVerificata(idVers, cdKeyObject,
		    nmTipoObject);
	    boolean isLastRisolubile = monitoraggioHelper.isLastRisolubile(idVers, cdKeyObject,
		    nmTipoObject);
	    BigDecimal idOgg = objNonVersRB.getIdObject();
	    if (idOgg != null && tipoObjRowBean.getTiVersFile() != null) {
		if (!tipoObjRowBean.getTiVersFile()
			.equals(Constants.TipoVersamento.DA_TRASFORMARE.name())
			&& monitoraggioEjb.checkTiStatoPigObject(idOgg,
				Constants.StatoOggetto.CHIUSO_ERR_NOTIF.name(),
				Constants.StatoOggetto.CHIUSO_ERR_VERIFICA_HASH.name(),
				Constants.StatoOggetto.CHIUSO_ERR_SCHED.name(),
				Constants.StatoOggetto.CHIUSO_ERR_CODA.name(),
				Constants.StatoOggetto.CHIUSO_ERR_VERS.name())
			&& isLastVerificata && !isLastRisolubile) {

		    getForm().getOggettiDerVersFallitiButtonList().getAnnullaOggettoDerVersFalliti()
			    .setHidden(false);
		} else if (tipoObjRowBean.getTiVersFile()
			.equals(Constants.TipoVersamento.DA_TRASFORMARE.name())
			&& (monitoraggioEjb.checkTiStatoPigObject(idOgg,
				Constants.StatoOggetto.CHIUSO_ERR_TRASFORMAZIONE.name(),
				Constants.StatoOggetto.CHIUSO_ERR_VERIFICA_HASH.name(),
				Constants.StatoOggetto.CHIUSO_ERR_VERSAMENTO_A_PING.name()))) {
		    getForm().getOggettiDerVersFallitiButtonList().getAnnullaOggettoDerVersFalliti()
			    .setHidden(false);
		}
		// Nuova logica di visualizzazione del pulsante ANNULLA VERSAMENTI UD
		if (!tipoObjRowBean.getTiVersFile()
			.equals(Constants.TipoVersamento.DA_TRASFORMARE.name())
			&& monitoraggioEjb.checkTiStatoPigObject(idOgg,
				Constants.StatoOggetto.CHIUSO_ERR_CODA.name(),
				Constants.StatoOggetto.CHIUSO_ERR_VERS.name())
			&& isLastVerificata && !isLastRisolubile
			&& monitoraggioHelper.existsUDPerObjectVersataOkOrVersataErr(idOgg,
				Constants.COD_VERS_ERR_CHIAVE_DUPLICATA_NEW)) {
		    getForm().getOggettiDerVersFallitiButtonList()
			    .getAnnullaVersamentiUDDerVersFalliti().setHidden(false);
		}
	    }
	}
    }

    private void loadDettaglioObject(BigDecimal idObject) throws EMFError, ParerUserError {
	// Carico il rowbean del dettaglio oggetto
	MonVVisObjRowBean objRB = monitoraggioHelper.getMonVVisObjRowBean(idObject);
	getForm().getOggettoDetail().getTi_gest_oggetti_figli()
		.setDecodeMap(ComboGetter.getMappaSortedGenericEnum("ti_gest_oggetti_figli",
			Constants.TipoGestioneOggettiFigli.getTipoGestioneOggettiFigli()));
	getForm().getOggettoDetail().copyFromBean(objRB);

	getForm().getOggettoDetailButtonList().setEditMode();
	getForm().getOggettoDetailButtonList().hideAll();

	getForm().getOggettoTabs().setCurrentTab(getForm().getOggettoTabs().getDettaglioOggetto());
	getForm().getOggettoTabs().getXMLRichAnnul().setHidden(true);
	getForm().getOggettoTabs().getXMLRispAnnul().setHidden(true);
	getForm().getOggettoTabs().getFiltriUnitaDocObj().setHidden(true);
	getForm().getOggettoTabs().getStudioDICOM().setHidden(true);
	getForm().getOggettoTabs().getReportTrasformazione().setHidden(true);
	getForm().getOggettoSubTabs().getListaOggetti().setHidden(true);

	// Se il tipo oggetto vale Studio Dicom
	// allora carico il rowbean dello stesso e mostro il tab
	// in più mostro la lista oggetti con stesso DCM hash
	if (getForm().getOggettoDetail().getNm_tipo_object().getValue().equals(STUDIO_DICOM)) {
	    getForm().getOggettoTabs().getStudioDICOM().setHidden(false);
	    getForm().getOggettoSubTabs().getListaOggetti().setHidden(false);
	    PigInfoDicomRowBean infoDicomRB = monitoraggioHelper.getPigInfoDicomRowBean(idObject);
	    getForm().getStudioDICOM().copyFromBean(infoDicomRB);

	    // Setto la lista oggetti
	    getForm().getOggettoDetailOggettiDCMHashList()
		    .setTable(monitoraggioHelper.getMonVLisObjDCMHashViewBean(
			    getForm().getOggettoDetail().getId_object().parse(),
			    getUser().getIdOrganizzazioneFoglia(), infoDicomRB.getDsDcmHash()));
	    getForm().getOggettoDetailOggettiDCMHashList().getTable().setPageSize(10);
	    getForm().getOggettoDetailOggettiDCMHashList().setUserOperations(true, false, false,
		    false);
	    getForm().getOggettoDetailOggettiDCMHashList().setStatus(Status.view);
	    // Workaround in modo che la lista punti al primo record, non all'ultimo
	    getForm().getOggettoDetailOggettiDCMHashList().getTable().first();
	    getForm().getFiltriOggetti().setEditMode();
	}

	// MAC29594 - spostato qui per coprire tutti i tipi di oggetto.
	// Anche se la priorità di versamento non è valorizzata è necessario caricare il menu a
	// tendona con i valori
	getForm().getOggettoDetail().getTi_priorita_versamento()
		.setDecodeMap(ComboGetter.getMappaOrdinalGenericEnum("ti_priorita_versamento",
			it.eng.sacerasi.web.util.Constants.ComboFlagPrioVersType.values()));

	final String tiVers = getForm().getOggettoDetail().getTi_vers_file().getValue();
	if (tiVers.equals(Constants.TipoVersamento.DA_TRASFORMARE.name())) {
	    getForm().getOggettoSubTabs().getListaUnitaDoc().setHidden(true);
	    getForm().getOggettoSubTabs().getListaOggettiTrasf().setHidden(false);
	    getForm().getOggettoTabs().getReportTrasformazione().setHidden(false);
	    getForm().getOggettoSubTabs()
		    .setCurrentTab(getForm().getOggettoSubTabs().getListaOggettiTrasf());
	    // Setto la lista oggetti
	    getForm().getOggettoDetailOggettiTrasfList()
		    .setTable(monitoraggioEjb.getMonVLisObjTrasfTableBean(
			    getForm().getOggettoDetail().getId_object().parse()));
	    getForm().getOggettoDetailOggettiTrasfList().getTable().setPageSize(10);
	    getForm().getOggettoDetailOggettiTrasfList().getTable().first();
	    getForm().getNumUdSection().setHidden(true);

	    getForm().getOggettoDaTrasformareObjDetailSection().setHidden(true);

	    getForm().getOggettoDetail().getTi_priorita()
		    .setDecodeMap(ComboGetter.getMappaSortedGenericEnum("ti_priorita",
			    it.eng.sacerasi.web.util.Constants.ComboFlagPrioTrasfType.values()));
	    if (objRB.getTiPriorita() != null) {
		getForm().getOggettoDetail().getTi_priorita()
			.setValue(it.eng.sacerasi.web.util.Constants.ComboFlagPrioTrasfType
				.getEnumByString(objRB.getTiPriorita()));
	    }

	    // MAC29594
	    if (objRB.getTiPrioritaVersamento() != null) {
		getForm().getOggettoDetail().getTi_priorita_versamento()
			.setValue(it.eng.sacerasi.web.util.Constants.ComboFlagPrioVersType
				.getEnumByString(objRB.getTiPrioritaVersamento()));
	    }
	    getForm().getOggettoDetail().getTi_priorita_versamento().setViewMode();
	    getForm().getOggettoDetail().getTi_priorita_versamento().setHidden(true);

	    // MEV #22312
	    getForm().getOggettoDetail().getNm_ks_instance().setHidden(false);
	    getForm().getOggettoDetail().getNm_ks_instance().setValue("--");

	    PigKSInstance pigKSInstance = trasformazioniHelper
		    .getPigObjectKettleServerInstance(idObject.longValue());
	    if (pigKSInstance != null) {
		getForm().getOggettoDetail().getNm_ks_instance()
			.setValue(pigKSInstance.getNmKsInstance());
	    }

	    // MEV 26942
	    getForm().getOggettoDetailButtonList().getAnnullaOggettoDetail().setHidden(true);
	    getForm().getOggettoDetailButtonList().getAnnullaVersamentiUDDetail().setHidden(true);

	    if ((objRB.getTiStatoObject().equals(Constants.StatoOggetto.IN_ATTESA_FILE.name())
		    || objRB.getTiStatoObject().equals(Constants.StatoOggetto.DA_TRASFORMARE.name())
		    || objRB.getTiStatoObject()
			    .equals(Constants.StatoOggetto.TRASFORMAZIONE_NON_ATTIVA.name()))
		    || objRB.getTiStatoObject()
			    .equals(Constants.StatoOggetto.CHIUSO_ERR_VERIFICA_HASH.name())
		    // MEV#14652
		    || objRB.getTiStatoObject()
			    .equals(Constants.StatoOggetto.CHIUSO_ERR_TRASFORMAZIONE.name())
		    || objRB.getTiStatoObject()
			    .equals(Constants.StatoOggetto.CHIUSO_ERR_VERSAMENTO_A_PING.name())
		    // MAC#21308 - Impossibile settare a non risolubile una sessione annullata
		    || objRB.getTiStatoObject()
			    .equals(Constants.StatoOggetto.CHIUSO_ERR_NOTIF.name()) // MEV#14652
	    ) {
		getForm().getOggettoDetailButtonList().getAnnullaOggettoDetail().setHidden(false);
		// MEV#14652 - Nuovo ramo - Punto 50 dell'analisi
	    } else {
		String rootFtp = commonDb.getRootFtpParam();
		String pathInput = monitoraggioHelper.getDsPathInputFtp(objRB.getIdVers());

		if (objRB.getTiStatoObject().equals(Constants.StatoOggetto.ANNULLATO.name())
			&& (existsCartellaOggetto(rootFtp, pathInput, objRB.getCdKeyObject())
				|| monitoraggioHelper
					.areFileObjectsStoredInObjectStorage(idObject))) {
		    getForm().getOggettoDetailButtonList().getSettaDaTrasformareDetail()
			    .setHidden(false);
		}
		if (objRB.getTiStatoObject()
			.equals(Constants.StatoOggetto.TRASFORMAZIONE_IN_CORSO.name())) {
		    getForm().getOggettoDetailButtonList().getSettaErroreTrasformazioneDetail()
			    .setHidden(false);
		}
		// MEV 27802 - abilito la possibilità di mandare in errore anche per
		// PREPARAZIONE_OGG_IN_CORSO
		if (objRB.getTiStatoObject()
			.equals(Constants.StatoOggetto.PREPARAZIONE_OGG_IN_CORSO.name())) {
		    getForm().getOggettoDetailButtonList().getSettaErroreTrasformazioneDetail()
			    .setHidden(false);
		}

	    }

	    // MEV 28877 - abilito la possibilità di cambiare tipo oggetto se da trasformare o se
	    // trasforamzione non
	    // attiva
	    if (objRB.getTiStatoObject().equals(Constants.StatoOggetto.DA_TRASFORMARE.name())
		    || objRB.getTiStatoObject()
			    .equals(Constants.StatoOggetto.TRASFORMAZIONE_NON_ATTIVA.name())) {

		getForm().getOggettoDetailButtonList().getModificaTipoOggetto().setHidden(false);

	    }

	    try {
		// Carico l'eventuale report di trasformazione
		String report = trasformazioniHelper
			.getSessionReport(objRB.getIdLastSessioneIngest());
		getForm().getOggettoDetail().getReport_xml().setValue(report);
	    } catch (IOException | TransformerException | ObjectStorageException ex) {
		// TODO
	    }

	} else {
	    // NO ZIP E ZIP NO/CON XML
	    getForm().getNumUdSection().setHidden(false);
	    getForm().getOggettoSubTabs().getListaUnitaDoc().setHidden(false);
	    getForm().getOggettoSubTabs().getListaOggettiTrasf().setHidden(true);
	    getForm().getOggettoSubTabs()
		    .setCurrentTab(getForm().getOggettoSubTabs().getListaUnitaDoc());
	    if (getForm().getOggettoDetail().getId_object_padre().parse() != null) {
		getForm().getOggettoDaTrasformareObjDetailSection().setHidden(false);
		getForm().getOggettoDetailButtonList().getOggettoDaTrasformareObjDetail()
			.setHidden(false);
	    }
	    boolean filtriUd = false;
	    boolean inCorsoAnnul;

	    // MAC 25207
	    if (objRB.getTiPriorita() != null) {
		getForm().getOggettoDetail().getTi_priorita()
			.setDecodeMap(ComboGetter.getMappaSortedGenericEnum("ti_priorita",
				it.eng.sacerasi.web.util.Constants.ComboFlagPrioTrasfType
					.values()));
		getForm().getOggettoDetail().getTi_priorita()
			.setValue(it.eng.sacerasi.web.util.Constants.ComboFlagPrioTrasfType
				.getEnumByString(objRB.getTiPriorita()));
	    }

	    if (objRB.getTiPrioritaVersamento() != null) {
		getForm().getOggettoDetail().getTi_priorita_versamento()
			.setValue(it.eng.sacerasi.web.util.Constants.ComboFlagPrioVersType
				.getEnumByString(objRB.getTiPrioritaVersamento()));
	    }
	    getForm().getOggettoDetail().getTi_priorita_versamento().setViewMode();
	    getForm().getOggettoDetail().getTi_priorita_versamento().setHidden(false);
	    if ((inCorsoAnnul = objRB.getTiStatoObject()
		    .equals(Constants.StatoOggetto.IN_CORSO_ANNULLAMENTO.name()))
		    || objRB.getTiStatoObject().equals(Constants.StatoOggetto.ANNULLATO.name())) {
		getForm().getXmlAnnulRich().clear();
		BigDecimal idLastSessioneIngest = objRB.getIdLastSessioneIngest();
		PigXmlAnnulSessioneIngestRowBean richiesta = annullamentoEjb
			.getPigXmlSessioneIngestRowBean(idLastSessioneIngest,
				Constants.TipiXmlAnnul.RICHIESTA.name());
		if (richiesta != null) {
		    getForm().getOggettoTabs().getXMLRichAnnul().setHidden(false);
		    getForm().getXmlAnnulRich().copyFromBean(richiesta);
		}
		getForm().getXmlAnnulRisp().clear();
		PigXmlAnnulSessioneIngestRowBean risposta = annullamentoEjb
			.getPigXmlSessioneIngestRowBean(idLastSessioneIngest,
				Constants.TipiXmlAnnul.RISPOSTA.name());
		if (risposta != null) {
		    getForm().getOggettoTabs().getXMLRispAnnul().setHidden(false);
		    getForm().getXmlAnnulRisp().copyFromBean(risposta);

		    // MEV13062
		    if (risposta.getBlXmlAnnul() != null) {
			try {
			    String codiceEsito = null;
			    String codiceErrore = null;
			    String messaggioErrore = null;

			    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			    // XXE: This is the PRIMARY defense. If DTDs (doctypes) are disallowed,
			    // almost all XML entity attacks are prevented
			    final String FEATURE = "http://apache.org/xml/features/disallow-doctype-decl";
			    factory.setFeature(FEATURE, true);
			    factory.setFeature(
				    "http://xml.org/sax/features/external-general-entities", false);

			    factory.setFeature(
				    "http://xml.org/sax/features/external-parameter-entities",
				    false);
			    // ... and these as well, per Timothy Morgan's 2014 paper:
			    // "XML Schema, DTD, and Entity Attacks" (see reference below)
			    factory.setXIncludeAware(false);
			    factory.setExpandEntityReferences(false);
			    // As stated in the documentation, "Feature for Secure Processing (FSP)"
			    // is the central
			    // mechanism that will
			    // help you safeguard XML processing. It instructs XML processors, such
			    // as parsers,
			    // validators,
			    // and transformers, to try and process XML securely, and the FSP can be
			    // used as an
			    // alternative to
			    // dbf.setExpandEntityReferences(false); to allow some safe level of
			    // Entity Expansion
			    // Exists from JDK6.
			    factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
			    // ... and, per Timothy Morgan:
			    // "If for some reason support for inline DOCTYPEs are a requirement,
			    // then
			    // ensure the entity settings are disabled (as shown above) and beware
			    // that SSRF
			    // attacks
			    // (http://cwe.mitre.org/data/definitions/918.html) and denial
			    // of service attacks (such as billion laughs or decompression bombs via
			    // "jar:")
			    // are a risk."
			    DocumentBuilder builder = factory.newDocumentBuilder();
			    XPath xPath = XPathFactory.newInstance().newXPath();

			    String xmlAnnullRisp = risposta.getBlXmlAnnul();
			    InputSource is = new InputSource(new StringReader(xmlAnnullRisp));
			    Document xmlAnnullRispDoc = builder.parse(is);

			    XPathExpression expr = xPath.compile("//EsitoRichiesta/CodiceEsito");
			    NodeList nodes = (NodeList) expr.evaluate(xmlAnnullRispDoc,
				    XPathConstants.NODESET);
			    if (nodes.getLength() > 0) {
				codiceEsito = nodes.item(0).getTextContent().trim();
			    }

			    expr = xPath.compile("//EsitoRichiesta/CodiceErrore");
			    nodes = (NodeList) expr.evaluate(xmlAnnullRispDoc,
				    XPathConstants.NODESET);
			    if (nodes.getLength() > 0) {
				codiceErrore = nodes.item(0).getTextContent().trim();
			    }

			    expr = xPath.compile("//EsitoRichiesta/MessaggioErrore");
			    nodes = (NodeList) expr.evaluate(xmlAnnullRispDoc,
				    XPathConstants.NODESET);
			    if (nodes.getLength() > 0) {
				messaggioErrore = nodes.item(0).getTextContent().trim();
			    }

			    StringBuilder dsEsitoAnnullamento = new StringBuilder("");
			    if (codiceEsito != null) {
				dsEsitoAnnullamento.append(codiceEsito);
			    }

			    if (codiceErrore != null && messaggioErrore != null) {
				dsEsitoAnnullamento.append(codiceErrore).append(" | ")
					.append(messaggioErrore);
			    }

			    getForm().getOggettoDetail().getDs_esito_annullamento()
				    .setValue(dsEsitoAnnullamento.toString());
			} catch (XPathExpressionException | ParserConfigurationException
				| SAXException | IOException ex) {
			    log.warn("Errore nel parsing della risposta annullamento: {}",
				    ex.getMessage());
			}
		    }
		}
		// StatoOggetto accettato per visualizzare il tab dei filtri UD
		filtriUd = true;
		if (inCorsoAnnul) {
		    if (objRB.getFlRichAnnulTimeout().equals("1")) {
			getForm().getOggettoDetailButtonList().getVerificaAnnullamento()
				.setHidden(false);
		    } else if (risposta != null) {
			getForm().getOggettoDetailButtonList().getAccettaAnnullamentoFallito()
				.setHidden(false);
		    }
		}
	    } else if (objRB.getTiStatoObject().equals(Constants.StatoOggetto.CHIUSO_OK.name()) || // MAC
												   // 29616
												   // -
	    // Estensione
	    // annullamento
	    // oggetti in chiuso
	    // ok con warning
		    objRB.getTiStatoObject().startsWith(Constants.StatoOggetto.CHIUSO_OK.name()) || // MEV
												    // #14561
												    // -
		    // Estensione
		    // annullamento
		    // oggetti in errore
		    // (aggiunti 4 altri
		    // test in OR)
		    objRB.getTiStatoObject().equals(Constants.StatoOggetto.CHIUSO_ERR_VERS.name())
		    || objRB.getTiStatoObject()
			    .equals(Constants.StatoOggetto.CHIUSO_ERR_NOTIF.name())
		    || objRB.getTiStatoObject()
			    .equals(Constants.StatoOggetto.CHIUSO_ERR_VERIFICA_HASH.name())
		    || objRB.getTiStatoObject()
			    .equals(Constants.StatoOggetto.CHIUSO_ERR_SCHED.name())
		    || objRB.getTiStatoObject()
			    .equals(Constants.StatoOggetto.CHIUSO_ERR_CODA.name())) {
		// MEV 32543 tolto il controllo su sessione verificata e non risolubile.
		getForm().getOggettoDetailButtonList().getAnnullaOggettoDetail().setHidden(false);
	    }

	    // MEV 30208
	    if (objRB.getTiStatoObject()
		    .equals(Constants.StatoOggetto.CHIUSO_ERR_RECUPERABILE.name())) {
		getForm().getOggettoDetailButtonList().getSettaChiusoErrVersamento().setEditMode();
		getForm().getOggettoDetailButtonList().getSettaChiusoErrVersamento()
			.setHidden(false);
	    } else {
		getForm().getOggettoDetailButtonList().getSettaChiusoErrVersamento().setViewMode();
		getForm().getOggettoDetailButtonList().getSettaChiusoErrVersamento()
			.setHidden(true);
	    }

	    // MEV#14652 - Nuovo ramo IF per il pulsante di annullamento versamenti delle UD
	    if (objRB.getTiStatoObject().equals(Constants.StatoOggetto.CHIUSO_OK.name()) || // MAC
											    // 29616
											    // -
											    // Estensione
	    // annullamento oggetti in
	    // chiuso ok con warning
		    objRB.getTiStatoObject().startsWith(Constants.StatoOggetto.CHIUSO_OK.name())
		    || ((objRB.getTiStatoObject()
			    .equals(Constants.StatoOggetto.CHIUSO_ERR_VERS.name())
			    || objRB.getTiStatoObject()
				    .equals(Constants.StatoOggetto.CHIUSO_ERR_CODA.name()))
			    && monitoraggioHelper.existsUDPerObjectVersataOkOrVersataErr(idObject,
				    Constants.COD_VERS_ERR_CHIAVE_DUPLICATA_NEW))) {
		getForm().getOggettoDetailButtonList().getAnnullaVersamentiUDDetail()
			.setHidden(false);

		// MEV26216 - Calcolo dati per il popup di conferma annullamento UD
		// getRequest().getSession().setAttribute("ANNULLAMENTO_UD_NUM_UD", objRB)
	    }
	    if ((tiVers.equals(Constants.TipoVersamento.ZIP_CON_XML_SACER.name())
		    || tiVers.equals(Constants.TipoVersamento.ZIP_NO_XML_SACER.name()))
		    && (filtriUd
			    || objRB.getTiStatoObject()
				    .equals(Constants.StatoOggetto.CHIUSO_ERR_SCHED.name())
			    || objRB.getTiStatoObject()
				    .equals(Constants.StatoOggetto.CHIUSO_ERR_VERIFICA_HASH.name())
			    || objRB.getTiStatoObject()
				    .equals(Constants.StatoOggetto.IN_ATTESA_VERS.name())
			    || objRB.getTiStatoObject()
				    .equals(Constants.StatoOggetto.CHIUSO_ERR_CODA.name())
			    || objRB.getTiStatoObject()
				    .equals(Constants.StatoOggetto.IN_CODA_VERS.name())
			    || objRB.getTiStatoObject()
				    .equals(Constants.StatoOggetto.CHIUSO_ERR_VERS.name())
			    || objRB.getTiStatoObject()
				    .equals(Constants.StatoOggetto.CHIUSO_ERR_RECUPERABILE.name())
			    || objRB.getTiStatoObject()
				    .equals(Constants.StatoOggetto.CHIUSO_OK.name()))) {
		getForm().getOggettoTabs().getFiltriUnitaDocObj().setHidden(false);
		getForm().getOggettoDetailButtonList().getFiltraUnitaDocObj().setHidden(false);
		getForm().getFiltriUnitaDocObj().reset();
		getForm().getFiltriUnitaDocObj().setEditMode();

		getForm().getFiltriUnitaDocObj().getCd_registro_unita_doc_sacer()
			.setDecodeMap(DecodeMap.Factory.newInstance(monitoraggioHelper
				.getDistinctColumnFromMonVLisUnitaDocObjectTableBean(idObject,
					String.class,
					MonVLisUnitaDocObjectTableDescriptor.COL_CD_REGISTRO_UNITA_DOC_SACER),
				MonVLisUnitaDocObjectTableDescriptor.COL_CD_REGISTRO_UNITA_DOC_SACER,
				MonVLisUnitaDocObjectTableDescriptor.COL_CD_REGISTRO_UNITA_DOC_SACER));
		getForm().getFiltriUnitaDocObj().getAa_unita_doc_sacer()
			.setDecodeMap(DecodeMap.Factory.newInstance(monitoraggioHelper
				.getDistinctColumnFromMonVLisUnitaDocObjectTableBean(idObject,
					BigDecimal.class,
					MonVLisUnitaDocObjectTableDescriptor.COL_AA_UNITA_DOC_SACER),
				MonVLisUnitaDocObjectTableDescriptor.COL_AA_UNITA_DOC_SACER,
				MonVLisUnitaDocObjectTableDescriptor.COL_AA_UNITA_DOC_SACER
					+ "_str"));
		getForm().getFiltriUnitaDocObj().getTi_stato_unita_doc_object()
			.setDecodeMap(DecodeMap.Factory.newInstance(monitoraggioHelper
				.getDistinctColumnFromMonVLisUnitaDocObjectTableBean(idObject,
					String.class,
					MonVLisUnitaDocObjectTableDescriptor.COL_TI_STATO_UNITA_DOC_OBJECT),
				MonVLisUnitaDocObjectTableDescriptor.COL_TI_STATO_UNITA_DOC_OBJECT,
				MonVLisUnitaDocObjectTableDescriptor.COL_TI_STATO_UNITA_DOC_OBJECT));
		// nuovo codice MAC#13039
		getForm().getFiltriUnitaDocObj().getCd_concat_dl_err_sacer()
			.setDecodeMap(DecodeMap.Factory.newInstance(
				monitoraggioHelper
					.getCdErrSacerFromMonVLisUnitaDocObjectTableBean(idObject),
				MonVLisUnitaDocObjectTableDescriptor.COL_CD_ERR_SACER,
				MonVLisUnitaDocObjectTableDescriptor.COL_CD_CONCAT_DL_ERR_SACER));
	    }
	}

	// Visualizzo il bottone di download XML solo se lo stesso Ã¨ presente - Punto 2
	if (objRB.getBlXml() != null && objRB.getBlXml().length() > 0) {
	    getForm().getOggettoDetailButtonList().getDownloadXMLOggetto()
		    .setDisableHourGlass(true);
	    getForm().getOggettoDetailButtonList().getDownloadXMLOggetto().setEditMode();
	}
	// Punto 29 e punto 32
	if (objRB.getTiStatoObject().equals(Constants.StatoOggetto.WARNING_TRASFORMAZIONE.name())
		|| objRB.getTiStatoObject()
			.equals(Constants.StatoOggetto.ERRORE_TRASFORMAZIONE.name())) {
	    getForm().getOggettoDetailButtonList().getRecuperoErrTrasformazione().setHidden(false);
	} else if (objRB.getTiStatoObject()
		.equals(Constants.StatoOggetto.ERRORE_VERSAMENTO_A_PING.name())) {
	    getForm().getOggettoDetailButtonList().getRecuperoErrVersamentoPing().setHidden(false);
	}

	// Carico la lista file
	MonVLisFileObjectTableBean fileObjectTableBean = monitoraggioHelper
		.getMonVLisFileObjectTableBean(idObject);
	getForm().getFileList().setTable(fileObjectTableBean);
	getForm().getFileList().getTable().setPageSize(10);
	getForm().getFileList().getTable().first();
	getForm().getFileList().setStatus(Status.view);
	getForm().getFileList().setUserOperations(false, false, false, false);

	if (!fileObjectTableBean.isEmpty()) {
	    // Verifico se esiste la directory per il download
	    String rootDir = commonDb.getRootFtpParam();
	    String versInputPath = monitoraggioHelper.getDsPathInputFtp(objRB.getIdVers());
	    String dir = objRB.getCdKeyObject() + File.separator;

	    File fileInput = new File(rootDir + versInputPath + dir);

	    // MEV26012
	    Iterator<MonVLisFileObjectRowBean> fileObjectIt = fileObjectTableBean.iterator();
	    while (fileObjectIt.hasNext()) {
		MonVLisFileObjectRowBean fileObjectRow = fileObjectIt.next();
		final String cdFilePath = "cd_file_path";
		if (fileObjectRow.getCdKeyFile() != null) {
		    fileObjectRow.setObject(cdFilePath, fileObjectRow.getCdKeyFile());
		} else {
		    if (fileInput.exists()) {
			fileObjectRow.setObject(cdFilePath,
				versInputPath + dir + fileObjectRow.getNmFileObject());
		    } else {
			fileObjectRow.setObject(cdFilePath, "--");
		    }
		}
	    }

	    boolean areFileObjectsStoredInObjectStorage = monitoraggioHelper
		    .areFileObjectsStoredInObjectStorage(idObject);

	    if (areFileObjectsStoredInObjectStorage) {
		try {
		    // MEV 34843 - recuperiamo il nome backend per il primo file
		    MonVLisFileObjectRowBean fileObjectRow = fileObjectTableBean.getRow(0);
		    BackendStorage backend = salvataggioBackendHelper
			    .getBackend(fileObjectRow.getIdBackend().longValue());
		    // MEV 26012
		    getForm().getOggettoDetail().getTi_conservato_su_os()
			    .setValue(backend.getBackendName());
		} catch (ObjectStorageException ose) {
		    log.error(
			    "Errore nel controllo sull'esistenza backend in load dettaglio object.",
			    ose);
		    getForm().getOggettoDetail().getTi_conservato_su_os().setValue("--");
		}
	    } else {
		// MEV 26012
		getForm().getOggettoDetail().getTi_conservato_su_os().setValue("FILE_SYSTEM");
	    }

	    // MEV 21995 - i file potrebbero essere tutti su object storage
	    if (areFileObjectsStoredInObjectStorage) {
		try {
		    // MEV 34843 - testiamo se il primo file esiste su object storage (tutti gli
		    // altri dovrebbero
		    // esistere di conseguenza).
		    MonVLisFileObjectRowBean fileObjectRow = fileObjectTableBean.getRow(0);
		    BackendStorage backend = salvataggioBackendHelper
			    .getBackend(fileObjectRow.getIdBackend().longValue());

		    ObjectStorageBackend objectStorageConfigurationForVersamento = salvataggioBackendHelper
			    .getObjectStorageConfigurationForVersamento(backend.getBackendName(),
				    fileObjectRow.getNmBucket());
		    if (salvataggioBackendHelper.doesObjectExist(
			    objectStorageConfigurationForVersamento,
			    fileObjectRow.getCdKeyFile())) {
			getForm().getOggettoDetailButtonList().getDownloadFileOggettoObjDetail()
				.setHidden(false);
		    }
		} catch (ObjectStorageException ose) {
		    log.error("Errore nel controllo sull'esistenza file in load dettaglio object.",
			    ose);
		    getForm().getOggettoDetailButtonList().getDownloadFileOggettoObjDetail()
			    .setHidden(true);
		}
	    } // MEV 21995 - se la cartella ftp esiste o object storage è spento
	    else {
		getForm().getOggettoDetailButtonList().getDownloadFileOggettoObjDetail()
			.setHidden(!fileInput.exists());
	    }

	    // MEV 32542
	    if (tiVers.equals(Constants.TipoVersamento.ZIP_CON_XML_SACER.name())) {
		if (objRB.getTiStatoObject().equals(Constants.StatoOggetto.CHIUSO_ERR_VERS.name())
			&& (fileInput.exists() || areFileObjectsStoredInObjectStorage)) {
		    getForm().getOggettoDetailButtonList().getRecuperoChiusErrVers()
			    .setHidden(false);
		    getForm().getOggettoDetailButtonList().getRecuperoChiusErrVers().setEditMode();
		} else {
		    getForm().getOggettoDetailButtonList().getRecuperoChiusErrVers()
			    .setHidden(true);
		    getForm().getOggettoDetailButtonList().getRecuperoChiusErrVers().setViewMode();
		}
	    }

	    // MEV 37583
	    if (tiVers.equals(Constants.TipoVersamento.ZIP_CON_XML_SACER.name())) {
		if (objRB.getTiStatoObject().equals(Constants.StatoOggetto.CHIUSO_ERR_SCHED.name())
			&& (fileInput.exists() || areFileObjectsStoredInObjectStorage)) {
		    getForm().getOggettoDetailButtonList().getRecuperoChiusErrSched()
			    .setHidden(false);
		    getForm().getOggettoDetailButtonList().getRecuperoChiusErrSched().setEditMode();
		} else {
		    getForm().getOggettoDetailButtonList().getRecuperoChiusErrSched()
			    .setHidden(true);
		    getForm().getOggettoDetailButtonList().getRecuperoChiusErrSched().setViewMode();
		}
	    }
	}

	// Carico la lista unità doc.
	MonVLisUnitaDocObjectTableBean udObjectTB = monitoraggioHelper
		.getMonVLisUnitaDocObjectTableBean(idObject);
	getForm().getOggettoDetailUnitaDocList().setTable(udObjectTB);
	getForm().getOggettoDetailUnitaDocList().getTable().setPageSize(10);
	getForm().getOggettoDetailUnitaDocList().getTable().first();
	// Carico la lista versamenti
	MonVLisVersObjTableBean versTB = monitoraggioHelper.getMonVLisVersObjTableBean(idObject);
	getForm().getOggettoDetailSessioniList().setTable(versTB);
	getForm().getOggettoDetailSessioniList().getTable().setPageSize(10);
	getForm().getOggettoDetailSessioniList().getTable().first();
	// Carico la lista stati versamenti
	PigStatoSessioneIngestTableBean statiTb = monitoraggioEjb
		.getPigStatoSessioneIngestFromPigObjectTableBean(idObject);
	getForm().getOggettoDetailStatiVersamentiList().setTable(statiTb);
	getForm().getOggettoDetailStatiVersamentiList().getTable().setPageSize(10);
	getForm().getOggettoDetailStatiVersamentiList().getTable().first();
	// Carico la lista dei cambi di priorità
	PigPrioritaObjectTableBean prioritaTB = monitoraggioEjb
		.getPigPrioritaObjectTableBean(idObject);
	getForm().getOggettoDetailPrioritaVersamentoList().setTable(prioritaTB);
	getForm().getOggettoDetailPrioritaVersamentoList().getTable().setPageSize(10);
	getForm().getOggettoDetailPrioritaVersamentoList().getTable().first();
    }

    /**
     * Annulla le modifiche ad un dettaglio in fase di modifica, ripristinando i valori degli
     * eventuali campi modificati.
     *
     * @throws EMFError errore generico
     */
    @Override
    public void undoDettaglio() throws EMFError {
	String publisher = getLastPublisher();
	if (publisher.equals(Application.Publisher.VERSAMENTO_DETAIL)) {
	    // Se sto annullando, recupero i valori dalla request e li reimposto
	    getForm().getVersamentoDetail().getFl_verif()
		    .setValue((String) getSession().getAttribute("fl_verif_vers"));
	    getSession().removeAttribute("fl_verif_vers");
	    getForm().getVersamentoDetail().getFl_non_risolub()
		    .setValue((String) getSession().getAttribute("fl_non_risolub_vers"));
	    getSession().removeAttribute("fl_non_risolub_vers");
	    getForm().getVersamentoDetail().getFl_verif().setViewMode();
	    getForm().getVersamentoDetail().getFl_non_risolub().setViewMode();
	    /*
	     * Se sono nel dettaglio Versamenti Falliti e annullo la modifica dei flag verifica
	     * versamento fallito e non risolubile (sia che provenga da Lista Versamenti Falliti sia
	     * che provenga da Lista Versamenti Oggetti Non Versati sia che provenga dal Dettaglio
	     * oggetto
	     */
	    getForm().getOggettoDaVersamentiFallitiDetailVersamentiList().setStatus(Status.view);
	    getForm().getVersamentiList().setStatus(Status.view);
	    getForm().getOggettoDetailSessioniList().setStatus(Status.view);
	    forwardToPublisher(Application.Publisher.VERSAMENTO_DETAIL);
	} else if (publisher.equals(Application.Publisher.SESSIONE_ERRATA_DETAIL)) {
	    loadDettaglio();
	} else if (publisher.equals(Application.Publisher.OGGETTO_DA_VERSAMENTI_FALLITI_DETAIL)) {
	    /* Se sto annullando, recupero i valori dalla request e li reimposto */
	    getForm().getOggettoDaVersamentiFallitiDetail().getFl_vers_sacer_da_recup()
		    .setValue((String) getSession().getAttribute("fl_vers_sacer_da_recup"));
	    getSession().removeAttribute("fl_vers_sacer_da_recup");
	    /* Riporto il dettaglio e la lista relativa in viewMode */
	    getForm().getOggettoDaVersamentiFallitiDetail().getFl_vers_sacer_da_recup()
		    .setViewMode();
	    getForm().getOggettiDaVersamentiFallitiList().setStatus(Status.view);
	    /* Faccio il forward alla pagina stessa */
	    forwardToPublisher(Application.Publisher.OGGETTO_DA_VERSAMENTI_FALLITI_DETAIL);
	} else if (publisher.equals(Application.Publisher.OGGETTO_DETAIL)) {
	    setOggettoDetailViewMode();
	    forwardToPublisher(Application.Publisher.OGGETTO_DETAIL);
	} else if (publisher.equals(Application.Publisher.UNITA_DOC_DETAIL)) {
	    // MEV 31639
	    BigDecimal idUnitaDocObject = getForm().getUnitaDocDetail().getId_unita_doc_object()
		    .parse();
	    MonVVisUnitaDocObjectRowBean objRB = monitoraggioHelper
		    .getMonVVisUnitaDocObjectRowBean(idUnitaDocObject);
	    getForm().getUnitaDocDetail().copyFromBean(objRB);
	    getForm().getUnitaDocDetail().getBl_xml_vers_sacer()
		    .setValue(objRB.getBlXmlVersSacer());
	    getForm().getUnitaDocDetail().setStatus(Status.view);
	    getForm().getUnitaDocDetail().getBl_xml_vers_sacer().setViewMode();
	    forwardToPublisher(Application.Publisher.UNITA_DOC_DETAIL);
	} else {
	    goBack();
	}
    }

    private void setOggettoDetailViewMode() {
	getForm().getOggettiList().setStatus(Status.view);
	getForm().getOggettoDetail().setStatus(Status.view);
	getForm().getOggettoDetail().getNote().setViewMode();
	getForm().getOggettoDetail().getDs_info_object().setViewMode();
	getForm().getOggettoDetail().getTi_gest_oggetti_figli().setViewMode();
	getForm().getOggettoDetail().getTi_priorita().setViewMode();
	getForm().getOggettoDetail().getTi_priorita_versamento().setViewMode();
    }

    /**
     * Imposta lo status del flag fl_vers_sacer_da_recup: editabile o meno
     *
     * @param isEditable true se editabile
     *
     * @throws Exception errore generico
     */
    private void setStatusFlVersSacerDaRecup(boolean isEditable) {
	if (isEditable) {
	    getForm().getOggettiDaVersamentiFallitiList().setUserOperations(true, true, true,
		    false);
	    getForm().getOggettiDaVersamentiFallitiList().setViewMode();
	    getForm().getOggettiDaVersamentiFallitiList().setStatus(Status.view);
	} else {
	    getForm().getOggettiDaVersamentiFallitiList().setUserOperations(true, false, false,
		    false);
	    getForm().getOggettiDaVersamentiFallitiList().setViewMode();
	    getForm().getOggettiDaVersamentiFallitiList().setStatus(Status.view);
	}
    }

    @Override
    public void insertDettaglio() throws EMFError {
	throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void saveDettaglio() throws EMFError {
	String publisher = getLastPublisher();
	if (publisher.equals(Application.Publisher.SESSIONE_ERRATA_DETAIL)) {
	    salvaSessioneErrataFlVerificato();
	} else if (publisher.equals(Application.Publisher.VERSAMENTO_DETAIL)) {
	    if (getSession().getAttribute("provenienza").equals("VFdaVFList")
		    || getSession().getAttribute("provenienza").equals("VFdaOggettiVF")
		    || getSession().getAttribute("provenienza").equals("VFdaSessioniList")) {
		/*
		 * Salvo i valori dei campi del dettaglio versamento fallito tra cui i flaggozzi
		 * modificati (che sono gli unici campi che posso modificare)
		 */
		getForm().getVersamentoDetail().post(getRequest());
		boolean flagVerificato = getForm().getVersamentoDetail().getFl_verif().parse()
			.equals(Constants.DB_TRUE);
		boolean flagNonRisolubile = getForm().getVersamentoDetail().getFl_non_risolub()
			.parse().equals(Constants.DB_TRUE);
		try {
		    // NUOVO CODICE CENTRALIZZATO di gestione dei due flag verificato e non
		    // risolubile
		    BigDecimal idSessioneIngest = getForm().getVersamentoDetail()
			    .getId_sessione_ingest().parse();
		    String errore = monitoraggioEjb.impostaFlagVerificatoNonRisolubile(
			    idSessioneIngest, flagVerificato, flagNonRisolubile);
		    if (errore != null) {
			getMessageBox().addError(errore);
		    } else {
			// Aggiorno il campo note
			monitoraggioEjb.aggiornaNoteSessione(idSessioneIngest,
				getForm().getVersamentoDetail().getNote().parse());
			// Rimetto in viewmode i flaggozzi
			getForm().getVersamentoDetail().getFl_verif().setViewMode();
			getForm().getVersamentoDetail().getFl_non_risolub().setViewMode();
			getForm().getVersamentoDetail().getNote().setViewMode();
			// Mi salvo l'informazione che ho effettuato una modifica
			getSession().setAttribute("modificaVF", true);
			if (getRequest().getParameter("table") != null) {
			    if (getRequest().getParameter("table")
				    .equals(getForm().getVersamentiList().getName())) {
				getForm().getVersamentiList().setStatus(Status.view);
				getForm().getVersamentiList().setUserOperations(true, true, false,
					false); // MAC #15911
				// - Errore
				// su
				// "Indietro"
				// dopo
				// "modifica"
				// in
				// "dettaglio
				// sessione
				// fallita"
			    } else if (getRequest().getParameter("table")
				    .equals(getForm()
					    .getOggettoDaVersamentiFallitiDetailVersamentiList()
					    .getName())) {
				getForm().getOggettoDaVersamentiFallitiDetailVersamentiList()
					.setStatus(Status.view);
				getForm().getOggettoDaVersamentiFallitiDetailVersamentiList()
					.setUserOperations(true, true, false, false);
			    } else {
				getForm().getOggettoDetailSessioniList().setStatus(Status.view);
				getForm().getOggettoDetailSessioniList().setUserOperations(true,
					true, false, false);
			    }
			}
		    }
		} catch (ParerInternalError e) {
		    getMessageBox().addError(e.getDescription());
		} catch (Exception e) {
		    getMessageBox().addError(ExceptionUtils.getRootCauseMessage(e));
		}
		forwardToPublisher(Application.Publisher.VERSAMENTO_DETAIL);
	    }
	} else if (publisher.equals(Application.Publisher.OGGETTO_DA_VERSAMENTI_FALLITI_DETAIL)) {
	    /* Salvo nella tabella dell'oggetto (PigObject) il flag */
	    /*
	     * ATTENZIONE: siccome a seguito dell'esecuzione del job "recupera veramenti in errore"
	     * lo stato dell'oggetto potrebbe essere cambiato, bisogna controllarlo prima di salvare
	     */
	    BigDecimal idVers = getForm().getOggettoDaVersamentiFallitiDetail().getId_vers()
		    .parse();
	    String cdKeyObject = getForm().getOggettoDaVersamentiFallitiDetail().getCd_key_object()
		    .parse();
	    String[] flVersSacerDaRecup = getRequest().getParameterValues("Fl_vers_sacer_da_recup");
	    String tiStatoObject = monitoraggioHelper.getTiStatoObject(idVers, cdKeyObject);

	    if (tiStatoObject != null
		    && tiStatoObject.equals(Constants.StatoOggetto.CHIUSO_ERR_VERS.name())) {
		String flaggozzoVersSacerDaRecup = "0";
		// Se != da null, significa che ho spuntato il flag
		if (flVersSacerDaRecup != null) {
		    flaggozzoVersSacerDaRecup = "1";
		}

		// Imposto il flaggozzo nel front-end (in pratica ne faccio il post)
		getForm().getOggettoDaVersamentiFallitiDetail().getFl_vers_sacer_da_recup()
			.setChecked(!flaggozzoVersSacerDaRecup.equals("0"));

		try {
		    monitoraggioHelper.salvaFlVersSacerDaRecup(idVers, cdKeyObject,
			    flaggozzoVersSacerDaRecup);
		} catch (Exception e) {
		    getMessageBox().addMessage(
			    new Message(MessageLevel.ERR, "Errore nel salvataggio su DB!"));
		}
	    } else {
		getMessageBox().addError(
			"ATTENZIONE: job di recupero versamento eseguito, lo stato dell'oggetto Ã¨ cambiato e non Ã¨ possibile applicare modifiche");
	    }
	    // Rimetto in viewmode il flaggozzo e la lista
	    getForm().getOggettoDaVersamentiFallitiDetail().getFl_vers_sacer_da_recup()
		    .setViewMode();
	    getForm().getOggettiDaVersamentiFallitiList().setStatus(Status.view);
	    forwardToPublisher(Application.Publisher.OGGETTO_DA_VERSAMENTI_FALLITI_DETAIL);
	} else if (publisher.equals(Application.Publisher.OGGETTO_DETAIL)) {
	    getForm().getOggettoDetail().post(getRequest());
	    BigDecimal idObject = (BigDecimal) getSession().getAttribute(ID_OBJECT);
	    if ((getForm().getOggettoDetail().getTi_gest_oggetti_figli().getValue() == null
		    || getForm().getOggettoDetail().getTi_gest_oggetti_figli().getValue().trim()
			    .equals("")
		    || getForm().getOggettoDetail().getTi_priorita().getValue() == null
		    || getForm().getOggettoDetail().getTi_priorita().getValue().trim().equals(""))
		    && checkTiGestOggettiFigliEditabile(idObject)) {
		getMessageBox().addError(
			"ATTENZIONE: Il tipo gestione oggetti figli e la priorità devono essere valorizzate.");
	    } else {
		monitoraggioHelper.updatePigObject(idObject,
			getForm().getOggettoDetail().getNote().parse(),
			getForm().getOggettoDetail().getDs_info_object().parse(),
			getForm().getOggettoDetail().getTi_gest_oggetti_figli().parse(),
			getForm().getOggettoDetail().getNm_tipo_object().parse()
				.equalsIgnoreCase(STUDIO_DICOM),
			getForm().getOggettoDetail().getTi_priorita().parse(),
			getForm().getOggettoDetail().getTi_priorita_versamento().parse(),
			getUser().getUsername());
		setOggettoDetailViewMode();
		try {
		    getForm().getOggettoDetailPrioritaVersamentoList()
			    .setTable(monitoraggioEjb.getPigPrioritaObjectTableBean(idObject));
		} catch (ParerUserError e) {
		    getMessageBox().addError(e.getDescription());
		}
	    }
	    forwardToPublisher(Application.Publisher.OGGETTO_DETAIL);
	} else if (publisher.equals(Application.Publisher.UNITA_DOC_DETAIL)) {
	    // MEV 31639
	    getForm().getUnitaDocDetail().post(getRequest());

	    BigDecimal idUnitaDocObject = getForm().getUnitaDocDetail().getId_unita_doc_object()
		    .parse();
	    String xmlVers = getForm().getUnitaDocDetail().getBl_xml_vers_sacer().parse();

	    if (xmlVers != null) {
		try {
		    // controlla la bontà dell'xml inviato.
		    Unmarshaller tmpUnmarshaller = xmlContextCache
			    .getUnitaDocumentariaCtx_UnitaDocumentaria().createUnmarshaller();
		    tmpUnmarshaller.setSchema(xmlContextCache.getUnitaDocumentariaSchema());

		    Source saxSourceForUnmarshal = Utils.getSaxSourceForUnmarshal(xmlVers);
		    JAXBElement<UnitaDocumentariaType> udRoot = (JAXBElement<UnitaDocumentariaType>) tmpUnmarshaller
			    .unmarshal(saxSourceForUnmarshal);

		    VersatoreType versatoreType = udRoot.getValue().getIntestazione()
			    .getVersatore();
		    if (!monitoraggioEjb.checkVersatoreUnitaDocumentariaXml(idUnitaDocObject,
			    versatoreType)) {
			getMessageBox().addError(
				"ATTENZIONE: Il versatore dell'xml di versamento non è conforme a quello atteso.");
		    }

		} catch (JAXBException | SAXException ex) {
		    getMessageBox().addError("ATTENZIONE: l'xml di versamento non è conforme.");
		} catch (ParserConfigurationException ex) {
		    java.util.logging.Logger.getLogger(MonitoraggioAction.class.getName())
			    .log(Level.SEVERE, null, ex);
		}
	    } else {
		getMessageBox().addError("ATTENZIONE: l'xml di versamento non può essere vuoto.");
	    }

	    if (!getMessageBox().hasError()) {
		// salva il nuovo xml
		monitoraggioEjb.saveUnitaDocumentariaXmlVersamento(idUnitaDocObject, xmlVers);
		getForm().getUnitaDocDetail().setStatus(Status.view);
		getForm().getUnitaDocDetail().getBl_xml_vers_sacer().setViewMode();
		getForm().getUnitaDocDetail().getFl_xml_modificato().setValue(Constants.DB_TRUE);
	    }

	    forwardToPublisher(Application.Publisher.UNITA_DOC_DETAIL);
	}
    }

    @Override
    public void dettaglioOnClick() throws EMFError {
	String lista = getTableName();
	if (getForm().getSessioniErrateList().getName().equals(lista)) {
	    forwardToPublisher(Application.Publisher.SESSIONE_ERRATA_DETAIL);
	} else if (getForm().getOggettiList().getName().equals(lista)) {
	    getForm().getOggettiList().setUserOperations(true, true, false, false);
	    getForm().getOggettiList().setViewMode();
	    getForm().getOggettiList().setStatus(Status.view);
	    forwardToPublisher(Application.Publisher.OGGETTO_DETAIL);
	} else if (getForm().getOggettoDetailUnitaDocList().getName().equals(lista)) {
	    getForm().getOggettoDetailUnitaDocList().setUserOperations(true, false, false, false);
	    getForm().getOggettoDetailUnitaDocList().setViewMode();
	    getForm().getOggettoDetailUnitaDocList().setStatus(Status.view);
	    getForm().getUnitaDocDetail().getDownloadXMLUnitaDocObject().setDisableHourGlass(true);
	    getForm().getUnitaDocDetail().getDownloadXMLUnitaDocObject().setEditMode();

	    // MEV 33098 e MEV37583
	    getForm().getUnitaDocDetail().getEditUnitaDocumentaria().setViewMode();
	    getForm().getUnitaDocDetail().getUndoUnitaDocumentaria().setViewMode();
	    getForm().getUnitaDocDetail().getSaveUnitaDocumentaria().setViewMode();
	    getForm().getUnitaDocDetail().getEditUnitaDocumentaria().setHidden(false);
	    getForm().getUnitaDocDetail().getUndoUnitaDocumentaria().setHidden(true);
	    getForm().getUnitaDocDetail().getSaveUnitaDocumentaria().setHidden(true);
	    if (getForm().getOggettoDetail().getTi_stato_object().getValue()
		    .equals(Constants.StatoOggetto.CHIUSO_ERR_VERS.name())
		    || getForm().getOggettoDetail().getTi_stato_object().getValue()
			    .equals(Constants.StatoOggetto.CHIUSO_ERR_SCHED.name())) {
		getForm().getUnitaDocDetail().getEditUnitaDocumentaria().setEditMode();
	    }
	    forwardToPublisher(Application.Publisher.UNITA_DOC_DETAIL);
	} else if (getForm().getOggettoDetailSessioniList().getName().equals(lista)) {
	    getForm().getOggettoDetailSessioniList().setUserOperations(true, true, false, false);
	    getForm().getOggettoDetailSessioniList().setViewMode();
	    getForm().getOggettoDetailSessioniList().setStatus(Status.view);
	    // Quando entro nel dettaglio, mi assicuro comunque che i campi siano in view mode
	    getForm().getVersamentoDetail().setViewMode();
	    getForm().getVersamentoDetail().getDettaglioOggetto().setViewMode();
	    // Se Ã¨ presente l'id oggetto visualizzo il bottone per accedere al dettaglio
	    // dell'oggetto
	    if (getForm().getVersamentoDetail().getId_object().parse() != null) {
		getForm().getVersamentoDetail().getDettaglioOggetto().setEditMode();
	    }
	    getForm().getVersamentoDetail().getDownloadXMLVersamento().setDisableHourGlass(true);
	    getForm().getVersamentoDetail().getDownloadXMLVersamento().setEditMode();
	    getForm().getVersamentoDetail().getDownloadXMLVersamento().setHidden(
		    StringUtils.isBlank(getForm().getVersamentoDetail().getBl_xml().getValue()));
	    getForm().getVersamentoTabs()
		    .setCurrentTab(getForm().getVersamentoTabs().getDettaglioVersamento());
	    forwardToPublisher(Application.Publisher.VERSAMENTO_DETAIL);
	} else if (getForm().getOggettoDetailOggettiDCMHashList().getName().equals(lista)) {
	    getForm().getOggettoDetailOggettiDCMHashList().setUserOperations(true, false, false,
		    false);
	    getForm().getOggettoDetailOggettiDCMHashList().setViewMode();
	    getForm().getOggettoDetailOggettiDCMHashList().setStatus(Status.view);
	    getForm().getOggettoTabs()
		    .setCurrentTab(getForm().getOggettoTabs().getDettaglioOggetto());
	    forwardToPublisher(Application.Publisher.OGGETTO_DETAIL);
	} else if (getForm().getVersamentiList().getName().equals(lista)) {
	    getForm().getVersamentiList().setUserOperations(true, true, false, false);
	    getForm().getVersamentiList().setViewMode();
	    getForm().getVersamentiList().setStatus(Status.view);
	    // Quando entro nel dettaglio, mi assicuro comunque che i campi siano in view mode
	    getForm().getVersamentoDetail().setViewMode();
	    getForm().getVersamentoDetail().getDettaglioOggetto().setViewMode();
	    // Se Ã¨ presente l'id oggetto visualizzo il bottone per accedere al dettaglio
	    // dell'oggetto
	    if (getForm().getVersamentoDetail().getId_object().parse() != null) {
		getForm().getVersamentoDetail().getDettaglioOggetto().setEditMode();
	    }
	    getForm().getVersamentoDetail().getDownloadXMLVersamento().setDisableHourGlass(true);
	    getForm().getVersamentoDetail().getDownloadXMLVersamento().setEditMode();
	    getForm().getVersamentoDetail().getDownloadXMLVersamento().setHidden(
		    StringUtils.isBlank(getForm().getVersamentoDetail().getBl_xml().getValue()));
	    getForm().getVersamentoTabs()
		    .setCurrentTab(getForm().getVersamentoTabs().getDettaglioVersamento());
	    forwardToPublisher(Application.Publisher.VERSAMENTO_DETAIL);
	} else if (getForm().getOggettiDaVersamentiFallitiList().getName().equals(lista)) {
	    getForm().getVersamentoTabs()
		    .setCurrentTab(getForm().getVersamentoTabs().getDettaglioVersamento());
	    forwardToPublisher(Application.Publisher.OGGETTO_DA_VERSAMENTI_FALLITI_DETAIL);
	} else if (getForm().getOggettoDaVersamentiFallitiDetailVersamentiList().getName()
		.equals(lista)) {
	    getForm().getOggettoDaVersamentiFallitiDetailVersamentiList().setUserOperations(true,
		    true, false, false);
	    getForm().getOggettoDaVersamentiFallitiDetailVersamentiList().setViewMode();
	    getForm().getOggettoDaVersamentiFallitiDetailVersamentiList().setStatus(Status.view);
	    // Quando entro nel dettaglio, mi assicuro comunque che i campi siano in view mode
	    getForm().getVersamentoDetail().setViewMode();
	    getForm().getVersamentoDetail().getDettaglioOggetto().setViewMode();
	    // Se Ã¨ presente l'id oggetto visualizzo il bottone per accedere al dettaglio
	    // dell'oggetto
	    if (getForm().getVersamentoDetail().getId_object().parse() != null) {
		getForm().getVersamentoDetail().getDettaglioOggetto().setEditMode();
	    }
	    getForm().getVersamentoDetail().getDownloadXMLVersamento().setDisableHourGlass(true);
	    getForm().getVersamentoDetail().getDownloadXMLVersamento().setEditMode();
	    getForm().getVersamentoDetail().getDownloadXMLVersamento().setHidden(
		    StringUtils.isBlank(getForm().getVersamentoDetail().getBl_xml().getValue()));
	    getForm().getVersamentoTabs()
		    .setCurrentTab(getForm().getVersamentoTabs().getDettaglioVersamento());
	    forwardToPublisher(Application.Publisher.VERSAMENTO_DETAIL);
	} else if (getForm().getRiepilogoVersatoriList().getName().equals(lista)) {
	    BigDecimal idVers = getForm().getRiepilogoVersatoriList().getTable().getCurrentRow()
		    .getBigDecimal("id_vers");
	    // Ricavo ambiente versatore in base al versatore
	    PigAmbienteVersRowBean ambienteVersRB = monitoraggioHelper
		    .getAmbienteVersFromIdVers(idVers);
	    String nomeVers = monitoraggioHelper.getNomeVersFromId(idVers);

	    // Setto le combo di ambiente versatore e versatore con il valore ricavato
	    BaseTable bt = new BaseTable();
	    BaseRow br = new BaseRow();
	    br.setBigDecimal("id_ambiente_vers", ambienteVersRB.getIdAmbienteVers());
	    br.setString("nm_ambiente_vers", ambienteVersRB.getNmAmbienteVers());
	    bt.add(br);
	    DecodeMap dma = new DecodeMap();
	    dma.populatedMap(bt, "id_ambiente_vers", "nm_ambiente_vers");
	    getForm().getFiltriRiepilogoVersamenti().getId_ambiente_vers().setDecodeMap(dma);
	    getForm().getFiltriRiepilogoVersamenti().getId_ambiente_vers()
		    .setValue(ambienteVersRB.getIdAmbienteVers().toString());

	    bt.clear();

	    br.setBigDecimal("id_vers", idVers);
	    br.setString("nm_vers", nomeVers);
	    bt.add(br);
	    DecodeMap dme = new DecodeMap();
	    dme.populatedMap(bt, "id_vers", "nm_vers");
	    getForm().getFiltriRiepilogoVersamenti().getId_vers().setDecodeMap(dme);
	    getForm().getFiltriRiepilogoVersamenti().getId_vers().setValue(idVers.toString());

	    getForm().getFiltriRiepilogoVersamenti().getId_ambiente_vers().setViewMode();
	    getForm().getFiltriRiepilogoVersamenti().getId_vers().setViewMode();
	    getForm().getFiltriRiepilogoVersamenti().getId_tipo_object().setEditMode();
	    getForm().getFiltriRiepilogoVersamenti().getGeneraRiepilogoVersamenti().setEditMode();

	    // Ricavo il TableBean relativo ai tipi oggetto in base al versatore scelto
	    PigTipoObjectTableBean tipoObjectTableBean = comboHelper
		    .getTipoObjectFromVersatore(getUser().getIdUtente(), idVers);
	    DecodeMap mappaTipoObject = new DecodeMap();
	    mappaTipoObject.populatedMap(tipoObjectTableBean, "id_tipo_object", "nm_tipo_object");
	    getForm().getFiltriRiepilogoVersamenti().getId_tipo_object()
		    .setDecodeMap(mappaTipoObject);

	    try {
		// Calcola i totali di Riepilogo Versamenti
		calcolaTotaliRiepilogoVersamenti(ambienteVersRB.getIdAmbienteVers(), idVers, null);
	    } catch (ParerUserError ex) {
		getMessageBox().addError(ex.getDescription());
	    }
	    forwardToPublisher(Application.Publisher.RIEPILOGO_VERSAMENTI_DETAIL);
	} else if (getForm().getUnitaDocDaVersamentiFallitiList().getName().equals(lista)) {
	    if (!getMessageBox().hasError()) {
		forwardToPublisher(Application.Publisher.UNITA_DOC_VERSAMENTO_DETAIL);
	    } else {
		forwardToPublisher(getLastPublisher());
	    }
	}
    }

    @Override
    public void elencoOnClick() throws EMFError {
	if (getLastPublisher().equals(Application.Publisher.OGGETTO_DETAIL)
		&& !getIdObjStack().isEmpty()) {
	    try {
		List<BigDecimal> idObjStack = getIdObjStack();
		BigDecimal idObject = idObjStack.remove(idObjStack.size() - 1);
		BigDecimal lastIdObjectShowed = (BigDecimal) getSession().getAttribute(ID_OBJECT);
		boolean goBack = false;
		if (idObject.compareTo(lastIdObjectShowed) == 0 && !idObjStack.isEmpty()) {
		    idObject = idObjStack.get(idObjStack.size() - 1);
		} else {
		    goBack = true;
		}

		if (!goBack) {
		    getSession().setAttribute(GET_ID_OBJ_STACK, idObjStack);
		    getSession().setAttribute(ID_OBJECT, idObject);
		    loadDettaglioObject(idObject);

		    forwardToPublisher(Application.Publisher.OGGETTO_DETAIL);
		} else {
		    Menu menu = getUser().getMenu();
		    int lastIndex = menu.getSelectedPath("").size() - 1;
		    String lastMenuEntry = (menu.getSelectedPath("").get(lastIndex)).getCodice();
		    ExecutionHistory lastPage = SessionManager
			    .getLastExecutionHistory(getSession());
		    if (lastMenuEntry.contains("RiepilogoVersamenti")
			    && lastPage.getName().equals(Application.Publisher.OGGETTI_LIST)) {
			goBackTo(Application.Publisher.OGGETTI_LIST);
		    } else if (lastMenuEntry.contains("RiepilogoVersamenti")
			    && lastPage.getName().equals(Application.Publisher.VERSAMENTO_DETAIL)) {
			goBackTo(Application.Publisher.VERSAMENTO_DETAIL);
		    } else if (lastMenuEntry.contains("StatoVersamenti")) {
			goBackTo(Application.Publisher.VISUALIZZA_STATO_VERSAMENTI);
		    }
		}
	    } catch (ParerUserError ex) {
		getMessageBox().addError("Errore inatteso nel caricamento del dettaglio oggetto");
		goBack();
	    }
	} else {
	    goBack();
	}
    }

    @Override
    public void updateSessioniErrateList() {
	getForm().getSessioniErrateList().setStatus(Status.update);
	getForm().getSessioniErrateDetail().getFl_verif().setEditMode();
    }

    @Override
    public void delete(Fields<Field> fields) throws EMFError {
	throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void updateOggettiList() throws EMFError {
	getForm().getOggettiList().setStatus(Status.update);
	getForm().getOggettoDetail().setStatus(Status.update);
	getForm().getOggettoDetail().getNote().setEditMode();

	if (getForm().getOggettoDetail().getTi_vers_file().parse()
		.equalsIgnoreCase(Constants.TipoVersamento.ZIP_CON_XML_SACER.name())
		|| getForm().getOggettoDetail().getTi_vers_file().parse()
			.equalsIgnoreCase(Constants.TipoVersamento.NO_ZIP.name())) {
	    getForm().getOggettoDetail().getTi_priorita_versamento().setEditMode();
	}

	// Il campo info diventa modificabile solo se tipo object Ã¨ diverso da STUDIO_DICOM
	if (!getForm().getOggettoDetail().getNm_tipo_object().parse()
		.equalsIgnoreCase(STUDIO_DICOM)) {
	    getForm().getOggettoDetail().getDs_info_object().setEditMode();
	}
	BigDecimal idObject = (BigDecimal) getSession().getAttribute(ID_OBJECT);
	if (checkTiGestOggettiFigliEditabile(idObject)) {
	    getForm().getOggettoDetail().getTi_gest_oggetti_figli().setEditMode();
	    getForm().getOggettoDetail().getTi_priorita().setEditMode();
	}
    }

    private boolean checkTiGestOggettiFigliEditabile(BigDecimal idObject) {
	return monitoraggioEjb.checkTiVersFileAndInTiStatiSessione(idObject,
		Constants.TipoVersamento.DA_TRASFORMARE.name(),
		Constants.StatoSessioneIngest.TRASFORMAZIONE_NON_ATTIVA.name(),
		Constants.StatoSessioneIngest.ERRORE_TRASFORMAZIONE.name(),
		Constants.StatoSessioneIngest.DA_TRASFORMARE.name());
    }

    @Override
    public void updateOggettiDaVersamentiFallitiList() throws EMFError {
	BigDecimal idVers = getForm().getOggettoDaVersamentiFallitiDetail().getId_vers().parse();
	String cdKeyObject = getForm().getOggettoDaVersamentiFallitiDetail().getCd_key_object()
		.parse();
	String tiStatoObject = monitoraggioHelper.getTiStatoObject(idVers, cdKeyObject);
	/* Se l'oggetto derivante dai versamenti falliti Ã¨ definito con stato CHIUSO_ERR_VERS */
	if (tiStatoObject != null
		&& tiStatoObject.equals(Constants.StatoOggetto.CHIUSO_ERR_VERS.name())) {
	    getForm().getOggettoDaVersamentiFallitiDetail().getFl_vers_sacer_da_recup()
		    .setEditMode();
	    getForm().getOggettiDaVersamentiFallitiList().setStatus(Status.update);
	    /*
	     * Mi salvo in request il valore del flag: potrebbe servirmi in caso di modifica e
	     * successivo annullamento
	     */
	    getSession().setAttribute("fl_vers_sacer_da_recup", getForm()
		    .getOggettoDaVersamentiFallitiDetail().getFl_vers_sacer_da_recup().parse());
	    forwardToPublisher(Application.Publisher.OGGETTO_DA_VERSAMENTI_FALLITI_DETAIL);
	} else {
	    /*
	     * Riporto il dettaglio e la lista relativa in viewMode e torno indietro alla pagina
	     * della lista ricaricandola in quanto il job di recupera versamenti l'ha modificata
	     */
	    getForm().getOggettoDaVersamentiFallitiDetail().getFl_vers_sacer_da_recup()
		    .setViewMode();
	    getForm().getOggettiDaVersamentiFallitiList().setStatus(Status.view);
	    getMessageBox().addError(
		    "ATTENZIONE: l'oggetto é stato modificato in seguito all'esecuzione del job di recupero versamento e non é dunque possibile accedervi o modificarlo");
	    goBack();
	}
    }

    @Override
    public void updateVersamentiList() throws EMFError {
	getForm().getVersamentiList().setStatus(Status.update);
	/*
	 * Imposto in sessione l'info sulla provenienza: mi servirÃ  in fase di conferma salvataggio
	 * o annullamento
	 */
	getSession().setAttribute("provenienza", "VFdaVFList");
	updateDettaglioVersamentoCommon();
    }

    @Override
    public void updateOggettoDaVersamentiFallitiDetailVersamentiList() throws EMFError {
	getForm().getOggettoDaVersamentiFallitiDetailVersamentiList().setStatus(Status.update);
	/*
	 * Imposto in sessione l'info sula provenienza: mi servirÃ  in fase di conferma salvataggio
	 * o annullamento
	 */
	getSession().setAttribute("provenienza", "VFdaOggettiVF");
	updateDettaglioVersamentoCommon();
    }

    @Override
    public void updateOggettoDetailSessioniList() throws EMFError {
	getForm().getVersamentoDetail().getNote().setEditMode();
	getForm().getOggettoDetailSessioniList().setStatus(Status.update);
	/*
	 * Imposto in sessione l'info sula provenienza: mi servirÃ  in fase di conferma salvataggio
	 * o annullamento
	 */
	getSession().setAttribute("provenienza", "VFdaSessioniList");
	updateDettaglioVersamentoCommon();
    }

    // MEV33098 e MEV37583
    @Override
    public void updateUnitaDocDetail() throws EMFError {
	BigDecimal idObject = (BigDecimal) getSession().getAttribute(ID_OBJECT);
	MonVVisObjRowBean objRB = monitoraggioHelper.getMonVVisObjRowBean(idObject);

	if (objRB.getTiStatoObject().equals(Constants.StatoOggetto.CHIUSO_ERR_VERS.name()) || objRB
		.getTiStatoObject().equals(Constants.StatoOggetto.CHIUSO_ERR_SCHED.name())) {
	    getForm().getUnitaDocDetail().getBl_xml_vers_sacer().setValue("");
	    getForm().getUnitaDocDetail().getBl_xml_vers_sacer().setEditMode();
	    getForm().getUnitaDocDetail().setStatus(Status.update);
	}
    }

    // Codice a fattor comune
    private void updateDettaglioVersamentoCommon() throws EMFError {
	/* Setto in edit mode i campi del flag versamento fallito verificato e non risolubile */
	BigDecimal idSessioneIngest = getForm().getVersamentoDetail().getId_sessione_ingest()
		.parse();
	if (monitoraggioEjb.canModifyVerificataNonRisolubile(idSessioneIngest)) {
	    /*
	     * Setto in edit mode i campi del flag versamento fallito verificato e non risolubile a
	     * determinate condizioni
	     */
	    getForm().getVersamentoDetail().getFl_verif().setEditMode();
	    getForm().getVersamentoDetail().getFl_non_risolub().setEditMode();
	}
	/*
	 * Mi salvo in request il valore dei filtri: potrebbero servirmi in caso li modifichi e poi
	 * prema il tasto Annulla
	 */
	getSession().setAttribute("fl_verif_vers",
		getForm().getVersamentoDetail().getFl_verif().parse());
	getSession().setAttribute("fl_non_risolub_vers",
		getForm().getVersamentoDetail().getFl_non_risolub().parse());
	forwardToPublisher(Application.Publisher.VERSAMENTO_DETAIL);

    }

    /**
     * Ritorna la pagina di default della sezione Monitoraggio
     *
     * @return Application.Publisher.RIEPILOGO_VERSATORI_DETAIL
     */
    @Override
    protected String getDefaultPublsherName() {
	return Application.Publisher.RIEPILOGO_VERSAMENTI_DETAIL;
    }

    @Override
    public void reloadAfterGoBack(String publisher) {
	try {
	    switch (getLastPublisher()) {
	    case Application.Publisher.SESSIONE_ERRATA_DETAIL:
		int inizio = getForm().getSessioniErrateList().getTable().getFirstRowPageIndex();
		int paginaCorrente = getForm().getSessioniErrateList().getTable()
			.getCurrentPageIndex();
		MonVLisSesErrateTableBean listaSessErr = monitoraggioHelper.getSessioniErrateListTB(
			getForm().getFiltriSessione().getFl_sessione_err_verif().parse(), 1000);
		getForm().getSessioniErrateList().setTable(listaSessErr);
		getForm().getSessioniErrateList().getTable().setPageSize(10);
		getForm().getSessioniErrateList().getTable().first();
		// rieseguo la query se necessario
		this.lazyLoadGoPage(getForm().getSessioniErrateList(), paginaCorrente);
		// ritorno alla pagina
		getForm().getSessioniErrateList().getTable().setCurrentRowIndex(inizio);

		getForm().getSessioniErrateDetail().getDownloadXmlSessione().setViewMode();
		getForm().getSessioniErrateList().setStatus(Status.view);
		break;
	    case Application.Publisher.VERSAMENTO_DETAIL:
		try {
		    // Se da Versamenti Detail devo tornare in Versamenti List
		    if (getForm().getVersamentiList().getTable() != null) {
			getForm().getVersamentiList().setUserOperations(true, false, false, false);
			// Controllo se ci sono state modifiche: in caso rieseguo la query per la
			// lista versamenti
			// e i totali della pagina Riepilogo Versamenti relativi alla
			if (getSession().getAttribute("modificaVF") != null) {
			    // Rieseguo la ricerca della pagina precedente che avrÃ  anch'essa
			    // subito modifiche
			    int paginaCorrenteDocNonVers = getForm().getVersamentiList().getTable()
				    .getCurrentPageIndex();
			    int inizioDocNonVers = getForm().getVersamentiList().getTable()
				    .getFirstRowPageIndex();
			    MonVLisVersFallitiTableBean versFallitiTableBean = monitoraggioHelper
				    .getMonVLisVersFallitiViewBean(
					    (MonitoraggioFiltriListaVersFallitiBean) getSession()
						    .getAttribute("filtriListaVersFalliti"));
			    getForm().getVersamentiList().setTable(versFallitiTableBean);
			    getForm().getVersamentiList().setUserOperations(true, false, false,
				    false);
			    // Workaround in modo che la lista punti al primo record, non all'ultimo
			    getForm().getVersamentiList().getTable().first();
			    getForm().getVersamentiList().getTable().setPageSize(10);
			    // Rieseguo la query se necessario
			    this.lazyLoadGoPage(getForm().getVersamentiList(),
				    paginaCorrenteDocNonVers);
			    // Ritorno alla pagina
			    getForm().getVersamentiList().getTable()
				    .setCurrentRowIndex(inizioDocNonVers);

			    /*
			     * Rendo visibile il bottone per impostare la verifica del versamento se
			     * la lista dei versamenti falliti non Ã¨ vuota
			     */
			    if (versFallitiTableBean.size() != 0) {
				getForm().getSalvaVerificaButtonList().getImpostaVerificato()
					.setEditMode();
			    } else {
				getForm().getSalvaVerificaButtonList().getImpostaVerificato()
					.setViewMode();
			    }

			    calcolaTotaliRiepilogoVersamentiPerReload();
			    // Ora che ho ricaricato, rimuovo l'attributo
			    getSession().removeAttribute("modificaVF");
			}
		    } // Se da Versamenti Detail devo tornare in Versamenti List di Oggetti da
		      // versamenti falliti
		    if (getForm().getOggettoDaVersamentiFallitiDetailVersamentiList()
			    .getTable() != null) {
			// Controllo se ci sono state modifiche: in caso rieseguo la query per la
			// lista versamenti
			// e i totali della pagina Riepilogo Versamenti relativi alla
			if (getSession().getAttribute("modificaVF") != null) {
			    // Rieseguo la ricerca della pagina precedente che avrÃ  anch'essa
			    // subito modifiche
			    int paginaCorrenteDocNonVers = getForm()
				    .getOggettoDaVersamentiFallitiDetailVersamentiList().getTable()
				    .getCurrentPageIndex();
			    int inizioDocNonVers = getForm()
				    .getOggettoDaVersamentiFallitiDetailVersamentiList().getTable()
				    .getFirstRowPageIndex();

			    BigDecimal idVers = getForm().getOggettoDaVersamentiFallitiDetail()
				    .getId_vers().parse();
			    String cdKeyObject = getForm().getOggettoDaVersamentiFallitiDetail()
				    .getCd_key_object().parse();
			    // Carico la lista versamenti
			    MonVLisVersObjNonVersTableBean versObjNonVersTB = monitoraggioHelper
				    .getMonVLisVersObjNonVersViewBean(idVers, cdKeyObject);
			    getForm().getOggettoDaVersamentiFallitiDetailVersamentiList()
				    .setTable(versObjNonVersTB);
			    getForm().getOggettoDaVersamentiFallitiDetailVersamentiList()
				    .setUserOperations(true, false, false, false);
			    getForm().getOggettoDaVersamentiFallitiDetailVersamentiList().getTable()
				    .setPageSize(10);
			    getForm().getOggettoDaVersamentiFallitiDetailVersamentiList().getTable()
				    .first();

			    // Workaround in modo che la lista punti al primo record, non all'ultimo
			    getForm().getOggettoDaVersamentiFallitiDetailVersamentiList().getTable()
				    .first();
			    getForm().getOggettoDaVersamentiFallitiDetailVersamentiList().getTable()
				    .setPageSize(10);
			    // Rieseguo la query se necessario
			    this.lazyLoadGoPage(
				    getForm().getOggettoDaVersamentiFallitiDetailVersamentiList(),
				    paginaCorrenteDocNonVers);
			    // Ritorno alla pagina
			    getForm().getOggettoDaVersamentiFallitiDetailVersamentiList().getTable()
				    .setCurrentRowIndex(inizioDocNonVers);

			    /*
			     * Rendo visibile il bottone per impostare la verifica del versamento se
			     * la lista dei versamenti falliti non Ã¨ vuota
			     */
			    if (versObjNonVersTB.size() != 0) {
				getForm().getSalvaVerificaButtonList().getImpostaVerificato()
					.setEditMode();
			    } else {
				getForm().getSalvaVerificaButtonList().getImpostaVerificato()
					.setViewMode();
			    }

			    calcolaTotaliRiepilogoVersamentiPerReload();
			    // Ora che ho ricaricato, rimuovo l'attributo
			    getSession().removeAttribute("modificaVF");
			}
		    } // Se da Versamenti Detail devo tornare in Versamenti List (Sessioni List) di
		      // Dettaglio Oggetto
		    if (getForm().getOggettoDetailSessioniList().getTable() != null) {
			// Controllo se ci sono state modifiche: in caso rieseguo la query per la
			// lista versamenti
			// e i totali della pagina Riepilogo Versamenti relativi alla
			if (getSession().getAttribute("modificaVF") != null) {
			    // Rieseguo la ricerca della pagina precedente che avrÃ  anch'essa
			    // subito modifiche
			    int paginaCorrenteDocNonVers = getForm().getOggettoDetailSessioniList()
				    .getTable().getCurrentPageIndex();
			    int inizioDocNonVers = getForm().getOggettoDetailSessioniList()
				    .getTable().getFirstRowPageIndex();
			    int pageSize = getForm().getOggettoDetailSessioniList().getTable()
				    .getPageSize();
			    BigDecimal idObject = getForm().getVersamentoDetail().getId_object()
				    .parse();
			    // Carico la lista versamenti
			    MonVLisVersObjTableBean versObjNonVersTB = monitoraggioHelper
				    .getMonVLisVersObjTableBean(idObject);
			    getForm().getOggettoDetailSessioniList().setTable(versObjNonVersTB);
			    getForm().getOggettoDetailSessioniList().setUserOperations(true, false,
				    false, false);
			    getForm().getOggettoDetailSessioniList().getTable()
				    .setPageSize(pageSize);
			    getForm().getOggettoDetailSessioniList().getTable().first();
			    // Rieseguo la query se necessario
			    this.lazyLoadGoPage(getForm().getOggettoDetailSessioniList(),
				    paginaCorrenteDocNonVers);
			    // Ritorno alla pagina
			    getForm().getOggettoDetailSessioniList().getTable()
				    .setCurrentRowIndex(inizioDocNonVers);

			    // Ora che ho ricaricato, rimuovo l'attributo
			    getSession().removeAttribute("modificaVF");
			}
		    }
		} catch (ParerUserError ex) {
		    getMessageBox().addError(ex.getDescription());
		}
		break;
	    case Application.Publisher.OGGETTO_DA_VERSAMENTI_FALLITI_DETAIL:
		// Rieseguo la ricerca della pagina precedente che avrÃ  anch'essa subito modifiche
		int paginaCorrenteDocNonVers = getForm().getOggettiDaVersamentiFallitiList()
			.getTable().getCurrentPageIndex();
		int inizioDocNonVers = getForm().getOggettiDaVersamentiFallitiList().getTable()
			.getFirstRowPageIndex();
		int pageSize = getForm().getOggettiDaVersamentiFallitiList().getTable()
			.getPageSize();
		MonVLisObjNonVersTableBean objNonVersTableBean = monitoraggioHelper
			.getMonVLisObjNonVersViewBean(
				(MonitoraggioFiltriListaOggDerVersFallitiBean) getSession()
					.getAttribute("filtriListaOggDerVersFalliti"));
		getForm().getOggettiDaVersamentiFallitiList().setTable(objNonVersTableBean);
		getForm().getOggettiDaVersamentiFallitiList().setUserOperations(true, false, false,
			false);
		getForm().getOggettiDaVersamentiFallitiList().getTable().setPageSize(pageSize);
		getForm().getOggettiDaVersamentiFallitiList().getTable().first();
		// Rieseguo la query se necessario
		this.lazyLoadGoPage(getForm().getOggettiDaVersamentiFallitiList(),
			paginaCorrenteDocNonVers);
		// Ritorno alla pagina
		getForm().getOggettiDaVersamentiFallitiList().getTable()
			.setCurrentRowIndex(inizioDocNonVers);
		break;
	    case Application.Publisher.OGGETTI_DA_VERSAMENTI_FALLITI_LIST:
	    case Application.Publisher.OGGETTI_LIST:
		try {
		    // Rieseguo il calcolo dei totali di Riepilogo Versamenti
		    BigDecimal idAmbienteVers = getForm().getFiltriRiepilogoVersamenti()
			    .getId_ambiente_vers().parse();
		    BigDecimal idVers = getForm().getFiltriRiepilogoVersamenti().getId_vers()
			    .parse();
		    BigDecimal idTipoObject = getForm().getFiltriRiepilogoVersamenti()
			    .getId_tipo_object().parse();
		    // MEV 26979
		    BigDecimal idObject = getForm().getFiltriRiepilogoVersamenti().getId_object()
			    .parse();
		    String cdKeyObject = getForm().getFiltriRiepilogoVersamenti().getCd_key_object()
			    .parse();
		    calcolaTotaliRiepilogoVersamenti(idAmbienteVers, idVers, idTipoObject, idObject,
			    cdKeyObject);
		} catch (ParerUserError ex) {
		    getMessageBox().addError(ex.getDescription());
		}
		break;
	    case Application.Publisher.OGGETTO_DETAIL:
		if (publisher.equals(Application.Publisher.OGGETTI_LIST)) {
		    getForm().getOggettiList()
			    .setTable(monitoraggioHelper.getMonVLisObjViewBean(
				    (MonitoraggioFiltriListaOggettiBean) getSession()
					    .getAttribute("filtriListaOggetti")));
		    getForm().getOggettiList().getTable().setPageSize(10);
		    // Workaround in modo che la lista punti al primo record, non all'ultimo
		    getForm().getOggettiList().getTable().first();
		}
		break;
	    }
	} catch (EMFError ex) {
	    log.error("Errore inatteso nel caricamento della pagina " + publisher, ex);
	}
    }

    /**
     * Ritorna la classe action associata alla sezione Monitoraggio
     *
     * @return Application.Actions.MONITORAGGIO
     */
    @Override
    public String getControllerName() {
	return Application.Actions.MONITORAGGIO;
    }

    /**
     * Metodo di inizializzazione form di Riepilogo Versatore
     *
     * @throws EMFError  errore generico
     * @throws Exception errore generico
     */
    @Secure(action = "Menu.Monitoraggio.RiepilogoVersatore")
    public void riepilogoVersatore() throws Exception {
	getUser().getMenu().reset();
	getUser().getMenu().select("Menu.Monitoraggio.RiepilogoVersatore");

	// Ricavo i versatori cui l'utente Ã¨ abilitato
	Object[] versatori = comboHelper.getVersatori(getUser().getIdUtente());
	PigVersTableBean versatoriTB = (PigVersTableBean) versatori[0];
	List<Object> idVersList = versatoriTB.toList("id_vers");

	// Carico i dati nella pagina Riepilogo per Versatore
	MonVRiepVersTableBean listaMon = monitoraggioHelper.getMonVRiepVersViewBean(idVersList);
	getForm().getRiepilogoVersatoriList().setTable(listaMon);
	getForm().getRiepilogoVersatoriList().getTable().setPageSize(10);
	getForm().getRiepilogoVersatoriList().getTable().first();

	getSession().removeAttribute(GET_ID_OBJ_STACK);
	getSession().removeAttribute(ID_OBJECT);

	// Eseguo il forward alla pagina
	forwardToPublisher(Application.Publisher.RIEPILOGO_VERSATORI_LIST);
    }

    /**
     * Metodo di inizializzazione form di Riepilogo Versamenti
     *
     * @throws EMFError  errore generico
     * @throws Exception errore generico
     */
    // XXX IN CASO DI MODIFICHE DEL CODICE UNIVOCO DEL MENU, MODIFICARE IL METODO ElencoOnClick
    @Secure(action = "Menu.Monitoraggio.RiepilogoVersamenti")
    public void riepilogoVersamenti() throws Exception {
	getUser().getMenu().reset();
	getUser().getMenu().select("Menu.Monitoraggio.RiepilogoVersamenti");

	// Resetto tutti i campi di riepilogo versamenti (filtri, totali)
	getForm().getFiltriRiepilogoVersamenti().reset();
	getForm().getRiepilogoOggettiVersati().reset();
	getForm().getRiepilogoInviiOggettiFalliti().reset();
	getForm().getRiepilogoNotificheFileFallite().reset();
	getForm().getRiepilogoPreparazioniXMLFallite().reset();
	getForm().getRiepilogoRegistrazioniCodaFallite().reset();
	getForm().getRiepilogoVersamentiSacerFalliti().reset();
	getForm().getRiepilogoOggettiDerivantiVersamentiFalliti().reset();
	getForm().getRiepilogoOggettiAnnullatiInCorso().reset();
	getForm().getOggettoDaVersamentiFallitiDetailVersamentiList().setTable(null);
	getForm().getOggettoDetailSessioniList().setTable(null);
	getForm().getVersamentiList().setTable(null);

	// Ricavo id versatore ed ambiente attuali
	BigDecimal idVers = getUser().getIdOrganizzazioneFoglia();
	BigDecimal idAmbienteVers = monitoraggioHelper.getIdAmbienteVersatore(idVers);

	// Creo il tablebean riferito alla combo di Ambiente Versatore
	PigAmbienteVersTableBean ambienteVersTableBean = null;
	// Creo il tablebean riferito alla combo di Versatore
	PigVersTableBean versTableBean = null;
	// Creo il tablebean riferito alla combo di Tipo Object
	PigTipoObjectTableBean tipoObjectTableBean = null;

	try {
	    // Ricavo i valori della combo AMBIENTE dalla tabella PIG_AMBIENTE_VERS
	    ambienteVersTableBean = comboHelper
		    .getAmbienteVersatoreFromUtente(getUser().getIdUtente());
	    // Ricavo i valori della combo VERSATORE
	    versTableBean = comboHelper.getVersatoreFromAmbienteVersatore(getUser().getIdUtente(),
		    idAmbienteVers);
	    // Ricavo i valori della combo TIPO OBJECT
	    tipoObjectTableBean = comboHelper.getTipoObjectFromVersatore(getUser().getIdUtente(),
		    idVers);
	} catch (Exception ex) {
	    log.error("Errore durante il caricamento valori nelle combo: " + ex.getMessage(), ex);
	}

	DecodeMap mappaAmbienteVers = new DecodeMap();
	mappaAmbienteVers.populatedMap(ambienteVersTableBean, "id_ambiente_vers",
		"nm_ambiente_vers");
	getForm().getFiltriRiepilogoVersamenti().getId_ambiente_vers()
		.setDecodeMap(mappaAmbienteVers);
	getForm().getFiltriRiepilogoVersamenti().getId_ambiente_vers()
		.setValue(idAmbienteVers.toString());

	DecodeMap mappaVers = new DecodeMap();
	mappaVers.populatedMap(versTableBean, "id_vers", "nm_vers");
	getForm().getFiltriRiepilogoVersamenti().getId_vers().setDecodeMap(mappaVers);
	getForm().getFiltriRiepilogoVersamenti().getId_vers().setValue(idVers.toString());

	DecodeMap mappaTipoObject = new DecodeMap();
	mappaTipoObject.populatedMap(tipoObjectTableBean, "id_tipo_object", "nm_tipo_object");
	getForm().getFiltriRiepilogoVersamenti().getId_tipo_object().setDecodeMap(mappaTipoObject);

	// Imposto le combo in editMode
	getForm().getFiltriRiepilogoVersamenti().getId_ambiente_vers().setEditMode();
	getForm().getFiltriRiepilogoVersamenti().getId_vers().setEditMode();
	getForm().getFiltriRiepilogoVersamenti().getId_tipo_object().setEditMode();
	getForm().getFiltriRiepilogoVersamenti().getId_object().setEditMode();
	getForm().getFiltriRiepilogoVersamenti().getCd_key_object().setEditMode();

	// Imposto come visibile il bottone di genera riepilogo versamenti e disabilito la clessidra
	// (per IE)
	getForm().getFiltriRiepilogoVersamenti().getGeneraRiepilogoVersamenti().setEditMode();
	getForm().getFiltriRiepilogoVersamenti().getGeneraRiepilogoVersamenti()
		.setDisableHourGlass(true);

	// Calcolo i totali per l'attuale versatore
	calcolaTotaliRiepilogoVersamenti(idAmbienteVers, idVers, null);

	getSession().removeAttribute(GET_ID_OBJ_STACK);
	getSession().removeAttribute(ID_OBJECT);
	// Eseguo forward alla stessa pagina
	forwardToPublisher(Application.Publisher.RIEPILOGO_VERSAMENTI_DETAIL);
    }

    /**
     * Trigger su campo "Ambiente" generico. Viene richiamato quando viene selezionato un valore in
     * un campo "ambiente" piazzato in una qualunque jsp
     *
     * @param filtri  filtro di tipo {@link Fields}
     * @param sezione sezione monitoraggio di tipo {@link SezioneMonitoraggio}
     *
     * @return oggetto di tipo {@link Fields}
     *
     * @throws EMFError errore generico
     */
    public Fields triggerAmbienteVersatoreGenerico(Fields filtri, SezioneMonitoraggio sezione)
	    throws EMFError {
	filtri.post(getRequest());

	// Passaggio per riferimento del "campo"; le modifiche avranno effetto sui "Fields"
	ComboBox ambienteVersCombo = (ComboBox) filtri.getComponent("id_ambiente_vers");
	ComboBox versCombo = (ComboBox) filtri.getComponent("id_vers");
	ComboBox tipoObjectCombo = (ComboBox) filtri.getComponent("id_tipo_object");

	// Azzero i valori preimpostati delle varie combo
	versCombo.setValue("");
	if (tipoObjectCombo != null) {
	    tipoObjectCombo.setValue("");
	}

	BigDecimal idAmbienteVers = (!ambienteVersCombo.getValue().equals("")
		? new BigDecimal(ambienteVersCombo.getValue())
		: null);
	if (idAmbienteVers != null) {
	    // Ricavo il TableBean relativo ai versatori dipendenti dall'ambiente versatore scelto
	    PigVersTableBean tmpTableBeanVers = comboHelper
		    .getVersatoreFromAmbienteVersatore(getUser().getIdUtente(), idAmbienteVers);
	    DecodeMap mappaVers = new DecodeMap();
	    mappaVers.populatedMap(tmpTableBeanVers, "id_vers", "nm_vers");
	    versCombo.setDecodeMap(mappaVers);
	    // Se ho un solo versatore lo setto giÃ  impostato nella combo
	    if (tmpTableBeanVers.size() == 1) {
		versCombo.setValue(tmpTableBeanVers.getRow(0).getIdVers().toString());
		checkUniqueVersatoreInCombo(tmpTableBeanVers.getRow(0).getIdVers(), sezione);
	    } else if (tipoObjectCombo != null) {
		tipoObjectCombo.setDecodeMap(new DecodeMap());
	    }
	} else {
	    versCombo.setDecodeMap(new DecodeMap());
	    if (tipoObjectCombo != null) {
		tipoObjectCombo.setDecodeMap(new DecodeMap());
	    }
	}
	return filtri;
    }

    /**
     * Trigger su campo "Versatore" generico. Viene richiamato quando viene selezionato un valore in
     * un campo "versatore" piazzato in una qualunque jsp
     *
     * @param filtri filtro di tipo {@link Fields}
     *
     * @return oggetto di tipo {@link Fields}
     *
     * @throws EMFError errore generico
     */
    public Fields triggerVersatoreGenerico(Fields filtri) throws EMFError {
	filtri.post(getRequest());

	// Passaggio per riferimento del "campo"; le modifiche avranno effetto sui "Fields"
	ComboBox versCombo = (ComboBox) filtri.getComponent("id_vers");
	ComboBox tipoObjectCombo = (ComboBox) filtri.getComponent("id_tipo_object");

	// Azzero i valori preimpostati delle varie combo
	tipoObjectCombo.setValue("");

	BigDecimal idVers = (!versCombo.getValue().equals("") ? new BigDecimal(versCombo.getValue())
		: null);
	if (idVers != null) {
	    // Ricavo il TableBean relativo ai tipi object dipendenti dal versatore scelto
	    PigTipoObjectTableBean tmpTableBeanTipoObject = comboHelper
		    .getTipoObjectFromVersatore(getUser().getIdUtente(), idVers);
	    DecodeMap mappaTipoObject = new DecodeMap();
	    mappaTipoObject.populatedMap(tmpTableBeanTipoObject, "id_tipo_object",
		    "nm_tipo_object");
	    tipoObjectCombo.setDecodeMap(mappaTipoObject);
	    // Se ho un solo tipo object lo setto giÃ  impostato nella combo
	    if (tmpTableBeanTipoObject.size() == 1) {
		tipoObjectCombo
			.setValue(tmpTableBeanTipoObject.getRow(0).getIdTipoObject().toString());
	    }
	} else {
	    tipoObjectCombo.setDecodeMap(new DecodeMap());
	}
	return filtri;
    }

    /**
     * Controlla un versatore selezionato in una combo, caricando la relativa successiva combo dei
     * tipi oggetto
     *
     * @param idVers  id versamento
     * @param sezione sezione monitoraggio di tipo (enumerativo)
     *
     * @throws EMFError errore generico
     */
    public void checkUniqueVersatoreInCombo(BigDecimal idVers, Enum sezione) throws EMFError {
	if (idVers != null) {
	    // Ricavo il TableBean relativo ai tipi object del versatore scelto
	    PigTipoObjectTableBean tmpTableBeanTipoObject = comboHelper
		    .getTipoObjectFromVersatore(getUser().getIdUtente(), idVers);
	    DecodeMap mappaTipoObject = new DecodeMap();
	    mappaTipoObject.populatedMap(tmpTableBeanTipoObject, "id_tipo_object",
		    "nm_tipo_object");

	    if (sezione.equals(WebConstants.SezioneMonitoraggio.RIEPILOGO_VERSAMENTI)) {
		getForm().getFiltriRiepilogoVersamenti().getId_tipo_object()
			.setDecodeMap(mappaTipoObject);
	    }
	    // Se la combo tipo object ha un solo valore presente, lo imposto
	    if (tmpTableBeanTipoObject.size() == 1) {
		if (sezione.equals(WebConstants.SezioneMonitoraggio.RIEPILOGO_VERSAMENTI)) {
		    getForm().getFiltriRiepilogoVersamenti().getId_tipo_object().setValue(
			    tmpTableBeanTipoObject.getRow(0).getIdTipoObject().toString());
		}
	    }
	}
    }

    @Override
    public JSONObject triggerFiltriRiepilogoVersamentiId_ambiente_versOnTrigger() throws EMFError {
	// Azzero i totali delle tabelle della sezione Riepilogo Versamenti
	eliminaTotaliRiepilogoVersamenti();
	triggerAmbienteVersatoreGenerico(getForm().getFiltriRiepilogoVersamenti(),
		WebConstants.SezioneMonitoraggio.RIEPILOGO_VERSAMENTI);
	return getForm().getFiltriRiepilogoVersamenti().asJSON();
    }

    @Override
    public JSONObject triggerFiltriRiepilogoVersamentiId_versOnTrigger() throws EMFError {
	// Azzero i totali delle tabelle della sezione Riepilogo Versamenti
	eliminaTotaliRiepilogoVersamenti();
	triggerVersatoreGenerico(getForm().getFiltriRiepilogoVersamenti());
	return getForm().getFiltriRiepilogoVersamenti().asJSON();
    }

    /*
     * Elimino i totali del campi delle varie tabelle
     */
    private void eliminaTotaliRiepilogoVersamenti() {
	getForm().getRiepilogoOggettiVersati().getNi_chiuso_ok_tot().setValue("1111");
	forwardToPublisher(Application.Publisher.RIEPILOGO_VERSAMENTI_DETAIL);
    }

    @Override
    public void generaRiepilogoVersamenti() throws EMFError {
	// Eseguo la post dei filtri compilati
	getForm().getFiltriRiepilogoVersamenti().post(getRequest());
	BigDecimal idAmbienteVers = getForm().getFiltriRiepilogoVersamenti().getId_ambiente_vers()
		.parse();
	BigDecimal idVers = getForm().getFiltriRiepilogoVersamenti().getId_vers().parse();
	BigDecimal idTipoObject = getForm().getFiltriRiepilogoVersamenti().getId_tipo_object()
		.parse();
	// MEV 26979
	BigDecimal idObject = getForm().getFiltriRiepilogoVersamenti().getId_object().parse();
	String cdKeyObject = getForm().getFiltriRiepilogoVersamenti().getCd_key_object().parse();
	try {
	    // Eseguo il calcolo dei totali
	    calcolaTotaliRiepilogoVersamenti(idAmbienteVers, idVers, idTipoObject, idObject,
		    cdKeyObject);
	} catch (ParerUserError ex) {
	    getMessageBox().addError(ex.getDescription());
	}
    }

    // MEV 26979
    public void calcolaTotaliRiepilogoVersamenti(BigDecimal idAmbienteVers, BigDecimal idVers,
	    BigDecimal idTipoObject) throws EMFError, ParerUserError {
	calcolaTotaliRiepilogoVersamenti(idAmbienteVers, idVers, idTipoObject, null, null);
    }

    public void calcolaTotaliRiepilogoVersamenti(BigDecimal idAmbienteVers, BigDecimal idVers,
	    BigDecimal idTipoObject, BigDecimal idObject, String cdKeyObject)
	    throws EMFError, ParerUserError {
	/*
	 * CALCOLO TOTALI RIEPILOGO OGGETTI VERSATI
	 */
	if (getForm().getFiltriRiepilogoVersamenti().validate(getMessageBox())) {
	    // Comincia a calcolare i totali di RIEPILOGO OGGETTI VERSATI
	    MonVObjRangeDtTableBean contaOggettiTB = monitoraggioHelper.getMonVObjRangeDtTableBean(
		    getUser().getIdUtente(), idAmbienteVers, idVers, idTipoObject, idObject,
		    cdKeyObject);
	    Map<String, Map<Enum, Integer>> countersVersati = calcolaTotaliOggetti(contaOggettiTB,
		    Constants.TipoClasseVersamento.NON_DA_TRASFORMARE.name());

	    // Setto i valori nella form
	    // MEV26211
	    /* WARNING_CHIAVE_DUPLICATA */
	    Integer niChiaveDupCorr = countersVersati
		    .get(Constants.StatoOggetto.WARNING_CHIAVE_DUPLICATA.name()).get(HmKey.OGGI);
	    Integer niChiaveDup7 = countersVersati
		    .get(Constants.StatoOggetto.WARNING_CHIAVE_DUPLICATA.name())
		    .get(HmKey.ULTIMI_7);
	    Integer niChiaveDupTot = countersVersati
		    .get(Constants.StatoOggetto.WARNING_CHIAVE_DUPLICATA.name()).get(HmKey.TOT);
	    getForm().getRiepilogoOggettiVersati().getNi_chiave_dup_corr()
		    .setValue(String.valueOf(niChiaveDupCorr));
	    getForm().getRiepilogoOggettiVersati().getNi_chiave_dup_7()
		    .setValue(String.valueOf(niChiaveDup7));
	    getForm().getRiepilogoOggettiVersati().getNi_chiave_dup_tot()
		    .setValue(String.valueOf(niChiaveDupTot));

	    /* CHIUSO OK */
	    Integer niChiusoOkCorr = getCountTotaliOggetti(countersVersati, HmKey.OGGI,
		    Constants.StatoOggetto.CHIUSO_OK.name(),
		    Constants.StatoOggetto.WARNING_CHIAVE_DUPLICATA.name());
	    Integer niChiusoOk7 = getCountTotaliOggetti(countersVersati, HmKey.ULTIMI_7,
		    Constants.StatoOggetto.CHIUSO_OK.name(),
		    Constants.StatoOggetto.WARNING_CHIAVE_DUPLICATA.name());
	    Integer niChiusoOkTot = getCountTotaliOggetti(countersVersati, HmKey.TOT,
		    Constants.StatoOggetto.CHIUSO_OK.name(),
		    Constants.StatoOggetto.WARNING_CHIAVE_DUPLICATA.name());
	    getForm().getRiepilogoOggettiVersati().getNi_chiuso_ok_corr()
		    .setValue(String.valueOf(niChiusoOkCorr));
	    getForm().getRiepilogoOggettiVersati().getNi_chiuso_ok_7()
		    .setValue(String.valueOf(niChiusoOk7));
	    getForm().getRiepilogoOggettiVersati().getNi_chiuso_ok_tot()
		    .setValue(String.valueOf(niChiusoOkTot));

	    /* IN CODA HASH */ // MEV 31102
	    String inCodaHashOggi = (countersVersati.get(Constants.StatoOggetto.IN_CODA_HASH.name())
		    .get(HmKey.OGGI)).toString();
	    String inCodaHash7 = (countersVersati.get(Constants.StatoOggetto.IN_CODA_HASH.name())
		    .get(HmKey.ULTIMI_7)).toString();
	    String inCodaHashTot = (countersVersati.get(Constants.StatoOggetto.IN_CODA_HASH.name())
		    .get(HmKey.TOT)).toString();
	    getForm().getRiepilogoOggettiVersati().getNi_in_coda_hash_corr()
		    .setValue(inCodaHashOggi);
	    getForm().getRiepilogoOggettiVersati().getNi_in_coda_hash_7().setValue(inCodaHash7);
	    getForm().getRiepilogoOggettiVersati().getNi_in_coda_hash_tot().setValue(inCodaHashTot);

	    /* IN ATTESA FILE */
	    String inAttesaFileOggi = (countersVersati
		    .get(Constants.StatoOggetto.IN_ATTESA_FILE.name()).get(HmKey.OGGI)).toString();
	    String inAttesaFile7 = (countersVersati
		    .get(Constants.StatoOggetto.IN_ATTESA_FILE.name()).get(HmKey.ULTIMI_7))
		    .toString();
	    String inAttesaFileTot = (countersVersati
		    .get(Constants.StatoOggetto.IN_ATTESA_FILE.name()).get(HmKey.TOT)).toString();
	    getForm().getRiepilogoOggettiVersati().getNi_in_attesa_file_corr()
		    .setValue(inAttesaFileOggi);
	    getForm().getRiepilogoOggettiVersati().getNi_in_attesa_file_7().setValue(inAttesaFile7);
	    getForm().getRiepilogoOggettiVersati().getNi_in_attesa_file_tot()
		    .setValue(inAttesaFileTot);
	    /* IN ATTESA PREPARAZIONE XML VERS A SACER (IN ATTESA SCHED) */
	    String inAttesaSchedOggi = (countersVersati
		    .get(Constants.StatoOggetto.IN_ATTESA_SCHED.name()).get(HmKey.OGGI)).toString();
	    String inAttesaSched7 = (countersVersati
		    .get(Constants.StatoOggetto.IN_ATTESA_SCHED.name()).get(HmKey.ULTIMI_7))
		    .toString();
	    String inAttesaSchedTot = (countersVersati
		    .get(Constants.StatoOggetto.IN_ATTESA_SCHED.name()).get(HmKey.TOT)).toString();
	    getForm().getRiepilogoOggettiVersati().getNi_in_attesa_prep_xml_corr()
		    .setValue(inAttesaSchedOggi);
	    getForm().getRiepilogoOggettiVersati().getNi_in_attesa_prep_xml_7()
		    .setValue(inAttesaSched7);
	    getForm().getRiepilogoOggettiVersati().getNi_in_attesa_prep_xml_tot()
		    .setValue(inAttesaSchedTot);
	    /* IN ATTESA DI ENTRARE IN CODA DI VERSAMENTO (IN ATTESA VERS) */
	    String inAttesaVersOggi = (countersVersati
		    .get(Constants.StatoOggetto.IN_ATTESA_VERS.name()).get(HmKey.OGGI)).toString();
	    String inAttesaVers7 = (countersVersati
		    .get(Constants.StatoOggetto.IN_ATTESA_VERS.name()).get(HmKey.ULTIMI_7))
		    .toString();
	    String inAttesaVersTot = (countersVersati
		    .get(Constants.StatoOggetto.IN_ATTESA_VERS.name()).get(HmKey.TOT)).toString();
	    getForm().getRiepilogoOggettiVersati().getNi_in_attesa_coda_vers_corr()
		    .setValue(inAttesaVersOggi);
	    getForm().getRiepilogoOggettiVersati().getNi_in_attesa_coda_vers_7()
		    .setValue(inAttesaVers7);
	    getForm().getRiepilogoOggettiVersati().getNi_in_attesa_coda_vers_tot()
		    .setValue(inAttesaVersTot);
	    /* IN CODA PER ESSERE VERSATI (IN CODA VERS) */
	    String inCodaVersOggi = (countersVersati.get(Constants.StatoOggetto.IN_CODA_VERS.name())
		    .get(HmKey.OGGI)).toString();
	    String inCodaVers7 = (countersVersati.get(Constants.StatoOggetto.IN_CODA_VERS.name())
		    .get(HmKey.ULTIMI_7)).toString();
	    String inCodaVersTot = (countersVersati.get(Constants.StatoOggetto.IN_CODA_VERS.name())
		    .get(HmKey.TOT)).toString();
	    getForm().getRiepilogoOggettiVersati().getNi_coda_vers_corr().setValue(inCodaVersOggi);
	    getForm().getRiepilogoOggettiVersati().getNi_coda_vers_7().setValue(inCodaVers7);
	    getForm().getRiepilogoOggettiVersati().getNi_coda_vers_tot().setValue(inCodaVersTot);
	    /* VERSATI PARZIALMENTE */
	    String chiusoErrVersOggi = (countersVersati
		    .get(Constants.StatoOggetto.CHIUSO_ERR_VERS.name()).get(HmKey.OGGI)).toString();
	    String chiusoErrVers7 = (countersVersati
		    .get(Constants.StatoOggetto.CHIUSO_ERR_VERS.name()).get(HmKey.ULTIMI_7))
		    .toString();
	    String chiusoErrVersTot = (countersVersati
		    .get(Constants.StatoOggetto.CHIUSO_ERR_VERS.name()).get(HmKey.TOT)).toString();
	    getForm().getRiepilogoOggettiVersati().getNi_vers_parz_corr()
		    .setValue(chiusoErrVersOggi);
	    getForm().getRiepilogoOggettiVersati().getNi_vers_parz_7().setValue(chiusoErrVers7);
	    getForm().getRiepilogoOggettiVersati().getNi_vers_parz_tot().setValue(chiusoErrVersTot);
	    /* IN WARNING */
	    String warningOggi = (countersVersati.get(Constants.StatoOggetto.WARNING.name())
		    .get(HmKey.OGGI)).toString();
	    String warning7 = (countersVersati.get(Constants.StatoOggetto.WARNING.name())
		    .get(HmKey.ULTIMI_7)).toString();
	    String warningTot = (countersVersati.get(Constants.StatoOggetto.WARNING.name())
		    .get(HmKey.TOT)).toString();
	    getForm().getRiepilogoOggettiVersati().getNi_war_corr().setValue(warningOggi);
	    getForm().getRiepilogoOggettiVersati().getNi_war_7().setValue(warning7);
	    getForm().getRiepilogoOggettiVersati().getNi_war_tot().setValue(warningTot);
	    /* CHIUSO WARNING */
	    String chiusoWarningOggi = (countersVersati
		    .get(Constants.StatoOggetto.CHIUSO_WARNING.name()).get(HmKey.OGGI)).toString();
	    String chiusoWarning7 = (countersVersati
		    .get(Constants.StatoOggetto.CHIUSO_WARNING.name()).get(HmKey.ULTIMI_7))
		    .toString();
	    String chiusoWarningTot = (countersVersati
		    .get(Constants.StatoOggetto.CHIUSO_WARNING.name()).get(HmKey.TOT)).toString();
	    getForm().getRiepilogoOggettiVersati().getNi_chiuso_war_corr()
		    .setValue(chiusoWarningOggi);
	    getForm().getRiepilogoOggettiVersati().getNi_chiuso_war_7().setValue(chiusoWarning7);
	    getForm().getRiepilogoOggettiVersati().getNi_chiuso_war_tot()
		    .setValue(chiusoWarningTot);
	    /* TOTALE */
	    int totOggi = Integer.parseInt(inCodaHashOggi) + Integer.parseInt(inAttesaFileOggi)
		    + Integer.parseInt(inAttesaSchedOggi) + Integer.parseInt(inAttesaVersOggi)
		    + Integer.parseInt(inCodaVersOggi) + Integer.parseInt(chiusoErrVersOggi)
		    + Integer.parseInt(warningOggi) + Integer.parseInt(chiusoWarningOggi);
	    int totUltimi7 = Integer.parseInt(inCodaHash7) + Integer.parseInt(inAttesaFile7)
		    + Integer.parseInt(inAttesaSched7) + Integer.parseInt(inAttesaVers7)
		    + Integer.parseInt(inCodaVers7) + Integer.parseInt(chiusoErrVers7)
		    + Integer.parseInt(warning7) + Integer.parseInt(chiusoWarning7);
	    int tot = Integer.parseInt(inCodaHashTot) + Integer.parseInt(inAttesaFileTot)
		    + Integer.parseInt(inAttesaSchedTot) + Integer.parseInt(inAttesaVersTot)
		    + Integer.parseInt(inCodaVersTot) + Integer.parseInt(chiusoErrVersTot)
		    + Integer.parseInt(warningTot) + Integer.parseInt(chiusoWarningTot);

	    getForm().getRiepilogoOggettiVersati().getNi_ogg_corso_vers_tot_corr()
		    .setValue(String.valueOf(totOggi));
	    getForm().getRiepilogoOggettiVersati().getNi_ogg_corso_vers_tot_7()
		    .setValue(String.valueOf(totUltimi7));
	    getForm().getRiepilogoOggettiVersati().getNi_ogg_corso_vers_tot_tot()
		    .setValue(String.valueOf(tot));

	    /*
	     * CALCOLO TOTALI RIEPILOGO OGGETTI TRASFORMATI Per gestire meglio i contatori, creo una
	     * mappa Map<Stato, Map<hmKey, int>> in modo da ottenere piÃ¹ facilmente gli oggetti che
	     * saranno organizzati quindi per STATO e CONTEGGIO (OGGI, ULTIMI 7 e TOTALI)
	     */
	    Map<String, Map<Enum, Integer>> countersTrasformati = calcolaTotaliOggetti(
		    contaOggettiTB, Constants.TipoClasseVersamento.DA_TRASFORMARE.name());
	    // Setto i valori nella form
	    /* IN CODA HASH */ // MEV 31102
	    getForm().getRiepilogoOggettiVersati().getNi_in_coda_hash_trasf_corr()
		    .setValue((countersTrasformati.get(Constants.StatoOggetto.IN_CODA_HASH.name())
			    .get(HmKey.OGGI)).toString());
	    getForm().getRiepilogoOggettiVersati().getNi_in_coda_hash_trasf_7()
		    .setValue((countersTrasformati.get(Constants.StatoOggetto.IN_CODA_HASH.name())
			    .get(HmKey.ULTIMI_7)).toString());
	    getForm().getRiepilogoOggettiVersati().getNi_in_coda_hash_trasf_tot()
		    .setValue((countersTrasformati.get(Constants.StatoOggetto.IN_CODA_HASH.name())
			    .get(HmKey.TOT)).toString());
	    /* IN ATTESA FILE */
	    getForm().getRiepilogoOggettiVersati().getNi_in_attesa_file_trasf_corr()
		    .setValue((countersTrasformati.get(Constants.StatoOggetto.IN_ATTESA_FILE.name())
			    .get(HmKey.OGGI)).toString());
	    getForm().getRiepilogoOggettiVersati().getNi_in_attesa_file_trasf_7()
		    .setValue((countersTrasformati.get(Constants.StatoOggetto.IN_ATTESA_FILE.name())
			    .get(HmKey.ULTIMI_7)).toString());
	    getForm().getRiepilogoOggettiVersati().getNi_in_attesa_file_trasf_tot()
		    .setValue((countersTrasformati.get(Constants.StatoOggetto.IN_ATTESA_FILE.name())
			    .get(HmKey.TOT)).toString());
	    /* IN ATTESA DI TRASFORMAZIONE */
	    // Questi valori si ottengono unendo i contatori degli stati DA_TRASFORMARE,
	    // TRASFORMAZIONE_NON_ATTIVA
	    getForm().getRiepilogoOggettiVersati().getNi_in_attesa_trasf_corr()
		    .setValue((getCountTotaliOggetti(countersTrasformati, HmKey.OGGI,
			    Constants.StatoOggetto.DA_TRASFORMARE.name(),
			    Constants.StatoOggetto.TRASFORMAZIONE_NON_ATTIVA.name())).toString());
	    getForm().getRiepilogoOggettiVersati().getNi_in_attesa_trasf_7()
		    .setValue((getCountTotaliOggetti(countersTrasformati, HmKey.ULTIMI_7,
			    Constants.StatoOggetto.DA_TRASFORMARE.name(),
			    Constants.StatoOggetto.TRASFORMAZIONE_NON_ATTIVA.name())).toString());
	    getForm().getRiepilogoOggettiVersati().getNi_in_attesa_trasf_tot()
		    .setValue((getCountTotaliOggetti(countersTrasformati, HmKey.TOT,
			    Constants.StatoOggetto.DA_TRASFORMARE.name(),
			    Constants.StatoOggetto.TRASFORMAZIONE_NON_ATTIVA.name())).toString());
	    /* TRASFORMAZIONE IN CORSO */
	    // Questi valori si ottengono unendo i contatori degli stati TRASFORMAZIONE_IN_CORSO
	    getForm().getRiepilogoOggettiVersati().getNi_in_running_trasf_corr()
		    .setValue((getCountTotaliOggetti(countersTrasformati, HmKey.OGGI,
			    Constants.StatoOggetto.TRASFORMAZIONE_IN_CORSO.name())).toString());
	    getForm().getRiepilogoOggettiVersati().getNi_in_running_trasf_7()
		    .setValue((getCountTotaliOggetti(countersTrasformati, HmKey.ULTIMI_7,
			    Constants.StatoOggetto.TRASFORMAZIONE_IN_CORSO.name())).toString());
	    getForm().getRiepilogoOggettiVersati().getNi_in_running_trasf_tot()
		    .setValue((getCountTotaliOggetti(countersTrasformati, HmKey.TOT,
			    Constants.StatoOggetto.TRASFORMAZIONE_IN_CORSO.name())).toString());
	    /* TRASFORMAZIONE BLOCCATA */
	    // Questi valori si ottengono unendo i contatori degli stati ERRORE_TRASFORMAZIONE
	    getForm().getRiepilogoOggettiVersati().getNi_trasf_bloccata_corr()
		    .setValue((getCountTotaliOggetti(countersTrasformati, HmKey.OGGI,
			    Constants.StatoOggetto.ERRORE_TRASFORMAZIONE.name())).toString());
	    getForm().getRiepilogoOggettiVersati().getNi_trasf_bloccata_7()
		    .setValue((getCountTotaliOggetti(countersTrasformati, HmKey.ULTIMI_7,
			    Constants.StatoOggetto.ERRORE_TRASFORMAZIONE.name())).toString());
	    getForm().getRiepilogoOggettiVersati().getNi_trasf_bloccata_tot()
		    .setValue((getCountTotaliOggetti(countersTrasformati, HmKey.TOT,
			    Constants.StatoOggetto.ERRORE_TRASFORMAZIONE.name())).toString());
	    /* TRASFORMAZIONE IN WARNING */
	    // Questi valori si ottengono unendo i contatori degli stati WARNING_TRASFORMAZIONE
	    getForm().getRiepilogoOggettiVersati().getNi_in_warning_trasf_corr()
		    .setValue((getCountTotaliOggetti(countersTrasformati, HmKey.OGGI,
			    Constants.StatoOggetto.WARNING_TRASFORMAZIONE.name())).toString());
	    getForm().getRiepilogoOggettiVersati().getNi_in_warning_trasf_7()
		    .setValue((getCountTotaliOggetti(countersTrasformati, HmKey.ULTIMI_7,
			    Constants.StatoOggetto.WARNING_TRASFORMAZIONE.name())).toString());
	    getForm().getRiepilogoOggettiVersati().getNi_in_warning_trasf_tot()
		    .setValue((getCountTotaliOggetti(countersTrasformati, HmKey.TOT,
			    Constants.StatoOggetto.WARNING_TRASFORMAZIONE.name())).toString());
	    /* PREPARAZIONE OGGETTI */
	    getForm().getRiepilogoOggettiVersati().getNi_in_creatingobjs_trasf_corr()
		    .setValue((countersTrasformati
			    .get(Constants.StatoOggetto.PREPARAZIONE_OGG_IN_CORSO.name())
			    .get(HmKey.OGGI)).toString());
	    getForm().getRiepilogoOggettiVersati().getNi_in_creatingobjs_trasf_7()
		    .setValue((countersTrasformati
			    .get(Constants.StatoOggetto.PREPARAZIONE_OGG_IN_CORSO.name())
			    .get(HmKey.ULTIMI_7)).toString());
	    getForm().getRiepilogoOggettiVersati().getNi_in_creatingobjs_trasf_tot()
		    .setValue((countersTrasformati
			    .get(Constants.StatoOggetto.PREPARAZIONE_OGG_IN_CORSO.name())
			    .get(HmKey.TOT)).toString());
	    /* TRASFORMATI */
	    getForm().getRiepilogoOggettiVersati().getNi_trasformati_corr()
		    .setValue((countersTrasformati.get(Constants.StatoOggetto.TRASFORMATO.name())
			    .get(HmKey.OGGI)).toString());
	    getForm().getRiepilogoOggettiVersati().getNi_trasformati_7()
		    .setValue((countersTrasformati.get(Constants.StatoOggetto.TRASFORMATO.name())
			    .get(HmKey.ULTIMI_7)).toString());
	    getForm().getRiepilogoOggettiVersati().getNi_trasformati_tot()
		    .setValue((countersTrasformati.get(Constants.StatoOggetto.TRASFORMATO.name())
			    .get(HmKey.TOT)).toString());
	    /* VERSATI A PREINGEST */
	    getForm().getRiepilogoOggettiVersati().getNi_vers_ping_corr()
		    .setValue((countersTrasformati.get(Constants.StatoOggetto.VERSATO_A_PING.name())
			    .get(HmKey.OGGI)).toString());
	    getForm().getRiepilogoOggettiVersati().getNi_vers_ping_7()
		    .setValue((countersTrasformati.get(Constants.StatoOggetto.VERSATO_A_PING.name())
			    .get(HmKey.ULTIMI_7)).toString());
	    getForm().getRiepilogoOggettiVersati().getNi_vers_ping_tot()
		    .setValue((countersTrasformati.get(Constants.StatoOggetto.VERSATO_A_PING.name())
			    .get(HmKey.TOT)).toString());
	    /* VERSAMENTO A PREINGEST BLOCCATO */
	    getForm().getRiepilogoOggettiVersati().getNi_vers_ping_bloccato_corr()
		    .setValue((countersTrasformati
			    .get(Constants.StatoOggetto.ERRORE_VERSAMENTO_A_PING.name())
			    .get(HmKey.OGGI)).toString());
	    getForm().getRiepilogoOggettiVersati().getNi_vers_ping_bloccato_7()
		    .setValue((countersTrasformati
			    .get(Constants.StatoOggetto.ERRORE_VERSAMENTO_A_PING.name())
			    .get(HmKey.ULTIMI_7)).toString());
	    getForm().getRiepilogoOggettiVersati().getNi_vers_ping_bloccato_tot()
		    .setValue((countersTrasformati
			    .get(Constants.StatoOggetto.ERRORE_VERSAMENTO_A_PING.name())
			    .get(HmKey.TOT)).toString());
	    /* PROBLEMI NELLA PREPARAZIONE SIP */
	    getForm().getRiepilogoOggettiVersati().getNi_problema_prep_sip_corr()
		    .setValue((countersTrasformati
			    .get(Constants.StatoOggetto.PROBLEMA_PREPARAZIONE_SIP.name())
			    .get(HmKey.OGGI)).toString());
	    getForm().getRiepilogoOggettiVersati().getNi_problema_prep_sip_7()
		    .setValue((countersTrasformati
			    .get(Constants.StatoOggetto.PROBLEMA_PREPARAZIONE_SIP.name())
			    .get(HmKey.ULTIMI_7)).toString());
	    getForm().getRiepilogoOggettiVersati().getNi_problema_prep_sip_tot()
		    .setValue((countersTrasformati
			    .get(Constants.StatoOggetto.PROBLEMA_PREPARAZIONE_SIP.name())
			    .get(HmKey.TOT)).toString());
	    /* IN CORSO DI VERSAMENTO A SACER */
	    getForm().getRiepilogoOggettiVersati().getNi_in_corso_vers_sacer_corr()
		    .setValue((countersTrasformati
			    .get(Constants.StatoOggetto.IN_CORSO_VERS_SACER.name()).get(HmKey.OGGI))
			    .toString());
	    getForm().getRiepilogoOggettiVersati().getNi_in_corso_vers_sacer_7()
		    .setValue((countersTrasformati
			    .get(Constants.StatoOggetto.IN_CORSO_VERS_SACER.name())
			    .get(HmKey.ULTIMI_7)).toString());
	    getForm().getRiepilogoOggettiVersati().getNi_in_corso_vers_sacer_tot()
		    .setValue((countersTrasformati
			    .get(Constants.StatoOggetto.IN_CORSO_VERS_SACER.name()).get(HmKey.TOT))
			    .toString());
	    /* PROBLEMI NEL VERSAMENTO A SACER */
	    getForm().getRiepilogoOggettiVersati().getNi_problema_vers_sacer_corr()
		    .setValue((countersTrasformati
			    .get(Constants.StatoOggetto.PROBLEMA_VERS_SACER.name()).get(HmKey.OGGI))
			    .toString());
	    getForm().getRiepilogoOggettiVersati().getNi_problema_vers_sacer_7()
		    .setValue((countersTrasformati
			    .get(Constants.StatoOggetto.PROBLEMA_VERS_SACER.name())
			    .get(HmKey.ULTIMI_7)).toString());
	    getForm().getRiepilogoOggettiVersati().getNi_problema_vers_sacer_tot()
		    .setValue((countersTrasformati
			    .get(Constants.StatoOggetto.PROBLEMA_VERS_SACER.name()).get(HmKey.TOT))
			    .toString());
	    // MAC 27956
	    /* WARNING_CHIAVE_DUPLICATA */
	    Integer niTrasformatiChiaveDupCorr = countersTrasformati
		    .get(Constants.StatoOggetto.WARNING_CHIAVE_DUPLICATA.name()).get(HmKey.OGGI);
	    Integer niTrasformatiChiaveDup7 = countersTrasformati
		    .get(Constants.StatoOggetto.WARNING_CHIAVE_DUPLICATA.name())
		    .get(HmKey.ULTIMI_7);
	    Integer niTrasformatiChiaveDupTot = countersTrasformati
		    .get(Constants.StatoOggetto.WARNING_CHIAVE_DUPLICATA.name()).get(HmKey.TOT);
	    getForm().getRiepilogoOggettiVersati().getNi_trasformati_chiave_dup_corr()
		    .setValue(String.valueOf(niTrasformatiChiaveDupCorr));
	    getForm().getRiepilogoOggettiVersati().getNi_trasformati_chiave_dup_7()
		    .setValue(String.valueOf(niTrasformatiChiaveDup7));
	    getForm().getRiepilogoOggettiVersati().getNi_trasformati_chiave_dup_tot()
		    .setValue(String.valueOf(niTrasformatiChiaveDupTot));

	    /* VERSATI A SACER */
	    getForm().getRiepilogoOggettiVersati().getNi_vers_sacer_corr()
		    .setValue((getCountTotaliOggetti(countersTrasformati, HmKey.OGGI,
			    Constants.StatoOggetto.CHIUSO_OK.name(),
			    Constants.StatoOggetto.CHIUSO_ERR.name(),
			    Constants.StatoOggetto.WARNING_CHIAVE_DUPLICATA.name())).toString());
	    getForm().getRiepilogoOggettiVersati().getNi_vers_sacer_7()
		    .setValue((getCountTotaliOggetti(countersTrasformati, HmKey.ULTIMI_7,
			    Constants.StatoOggetto.CHIUSO_OK.name(),
			    Constants.StatoOggetto.CHIUSO_ERR.name(),
			    Constants.StatoOggetto.WARNING_CHIAVE_DUPLICATA.name())).toString());
	    getForm().getRiepilogoOggettiVersati().getNi_vers_sacer_tot()
		    .setValue((getCountTotaliOggetti(countersTrasformati, HmKey.TOT,
			    Constants.StatoOggetto.CHIUSO_OK.name(),
			    Constants.StatoOggetto.CHIUSO_ERR.name(),
			    Constants.StatoOggetto.WARNING_CHIAVE_DUPLICATA.name())).toString());

	    // CALCOLO DEI TOTALI IN CORSO DI VERSAMENTO == ESCLUSO CHIUSO_OK e CHIUSO_WARNING,
	    // IN_CORSO_ANNULLAMENTO e
	    // ANNULLATO
	    totOggi = countersTrasformati.get("TOTALI").get(HmKey.OGGI)
		    - (countersTrasformati.get(Constants.StatoOggetto.CHIUSO_OK.name())
			    .get(HmKey.OGGI))
		    - (countersTrasformati.get(Constants.StatoOggetto.ANNULLATO.name())
			    .get(HmKey.OGGI))
		    - (countersTrasformati.get(Constants.StatoOggetto.IN_CORSO_ANNULLAMENTO.name())
			    .get(HmKey.OGGI))
		    - (countersTrasformati
			    .get(Constants.StatoOggetto.WARNING_CHIAVE_DUPLICATA.name())
			    .get(HmKey.OGGI));
	    totUltimi7 = countersTrasformati.get("TOTALI").get(HmKey.ULTIMI_7)
		    - (countersTrasformati.get(Constants.StatoOggetto.CHIUSO_OK.name())
			    .get(HmKey.ULTIMI_7))
		    - (countersTrasformati.get(Constants.StatoOggetto.ANNULLATO.name())
			    .get(HmKey.ULTIMI_7))
		    - (countersTrasformati.get(Constants.StatoOggetto.IN_CORSO_ANNULLAMENTO.name())
			    .get(HmKey.ULTIMI_7))
		    - (countersTrasformati
			    .get(Constants.StatoOggetto.WARNING_CHIAVE_DUPLICATA.name())
			    .get(HmKey.ULTIMI_7));
	    tot = countersTrasformati.get("TOTALI").get(HmKey.TOT)
		    - (countersTrasformati.get(Constants.StatoOggetto.CHIUSO_OK.name())
			    .get(HmKey.TOT))
		    - (countersTrasformati.get(Constants.StatoOggetto.ANNULLATO.name())
			    .get(HmKey.TOT))
		    - (countersTrasformati.get(Constants.StatoOggetto.IN_CORSO_ANNULLAMENTO.name())
			    .get(HmKey.TOT))
		    - (countersTrasformati
			    .get(Constants.StatoOggetto.WARNING_CHIAVE_DUPLICATA.name())
			    .get(HmKey.TOT));
	    /* TOTALE */
	    getForm().getRiepilogoOggettiVersati().getNi_ogg_corso_trasf_tot_corr()
		    .setValue(String.valueOf(totOggi));
	    getForm().getRiepilogoOggettiVersati().getNi_ogg_corso_trasf_tot_7()
		    .setValue(String.valueOf(totUltimi7));
	    getForm().getRiepilogoOggettiVersati().getNi_ogg_corso_trasf_tot_tot()
		    .setValue(String.valueOf(tot));

	    /*
	     * CALCOLO TOTALI RIEPILOGO INVII FALLITI RIEPILOGO NOTIFICHE FALLITE RIEPILOGO
	     * PREPARAZIONI XML FALLITE RIEPILOGO REGISTRAZIONI IN CODA FALLITE RIEPILOGO VERSAMENTI
	     * A SACER FALLITI RIEPILOGO TRASFORMAZIONI FALLITE
	     *
	     * Per gestire meglio i contatori, creo una mappa Map<String, Map<Stato, Map<hmKey,
	     * int>>> in modo da ottenere più facilmente gli oggetti che saranno organizzati quindi
	     * per STATO, per STATO RISOLUZIONE e CONTEGGIO (OGGI, ULTIMI 7 e TOTALI)
	     *
	     */
	    // Ricava il tablebean della vista contenente i totali da rielaborare
	    String nmTipoObject = (getForm().getFiltriRiepilogoVersamenti().getId_tipo_object()
		    .getDecodedValue().equals("") ? null
			    : getForm().getFiltriRiepilogoVersamenti().getId_tipo_object()
				    .getDecodedValue());
	    MonVSesRangeDtTableBean contaInviiFallitiTB = monitoraggioHelper
		    .getMonVSesRangeDtTableBean(getUser().getIdUtente(), idAmbienteVers, idVers,
			    nmTipoObject);

	    Map<String, Map<String, Map<Enum, Integer>>> calcolaTotaliFalliti = calcolaTotaliFalliti(
		    contaInviiFallitiTB);

	    // Setto i valori nella form RiepilogoInviiOggettiFalliti
	    /* TOTALI */
	    getForm().getRiepilogoInviiOggettiFalliti().getNi_ogg_fall_tot_corr()
		    .setValue(calcolaTotaliFalliti
			    .get(Constants.StatoSessioneIngest.CHIUSO_ERR.name()).get("TOTALI")
			    .get(HmKey.OGGI).toString());
	    getForm().getRiepilogoInviiOggettiFalliti().getNi_ogg_fall_tot_7()
		    .setValue(calcolaTotaliFalliti
			    .get(Constants.StatoSessioneIngest.CHIUSO_ERR.name()).get("TOTALI")
			    .get(HmKey.ULTIMI_7).toString());
	    getForm().getRiepilogoInviiOggettiFalliti().getNi_ogg_fall_tot_tot()
		    .setValue(calcolaTotaliFalliti
			    .get(Constants.StatoSessioneIngest.CHIUSO_ERR.name()).get("TOTALI")
			    .get(HmKey.TOT).toString());
	    /* RISOLTI */
	    getForm().getRiepilogoInviiOggettiFalliti().getNi_ogg_fall_ris_ver_corr()
		    .setValue(calcolaTotaliFalliti
			    .get(Constants.StatoSessioneIngest.CHIUSO_ERR.name())
			    .get(WebConstants.tiStatoRisoluz.RISOLTO.name()).get(HmKey.OGGI)
			    .toString());
	    getForm().getRiepilogoInviiOggettiFalliti().getNi_ogg_fall_ris_ver_7()
		    .setValue(calcolaTotaliFalliti
			    .get(Constants.StatoSessioneIngest.CHIUSO_ERR.name())
			    .get(WebConstants.tiStatoRisoluz.RISOLTO.name()).get(HmKey.ULTIMI_7)
			    .toString());
	    getForm().getRiepilogoInviiOggettiFalliti().getNi_ogg_fall_ris_ver_tot()
		    .setValue(calcolaTotaliFalliti
			    .get(Constants.StatoSessioneIngest.CHIUSO_ERR.name())
			    .get(WebConstants.tiStatoRisoluz.RISOLTO.name()).get(HmKey.TOT)
			    .toString());
	    /* IN CORSO DI RISOLUZIONE */
	    getForm().getRiepilogoInviiOggettiFalliti().getNi_ogg_fall_corso_ris_ver_corr()
		    .setValue(calcolaTotaliFalliti
			    .get(Constants.StatoSessioneIngest.CHIUSO_ERR.name())
			    .get(WebConstants.tiStatoRisoluz.IN_CORSO.name()).get(HmKey.OGGI)
			    .toString());
	    getForm().getRiepilogoInviiOggettiFalliti().getNi_ogg_fall_corso_ris_ver_7()
		    .setValue(calcolaTotaliFalliti
			    .get(Constants.StatoSessioneIngest.CHIUSO_ERR.name())
			    .get(WebConstants.tiStatoRisoluz.IN_CORSO.name()).get(HmKey.ULTIMI_7)
			    .toString());
	    getForm().getRiepilogoInviiOggettiFalliti().getNi_ogg_fall_corso_ris_ver_tot()
		    .setValue(calcolaTotaliFalliti
			    .get(Constants.StatoSessioneIngest.CHIUSO_ERR.name())
			    .get(WebConstants.tiStatoRisoluz.IN_CORSO.name()).get(HmKey.TOT)
			    .toString());
	    /* IN WARNING */
	    getForm().getRiepilogoInviiOggettiFalliti().getNi_ogg_fall_war_ver_corr()
		    .setValue(calcolaTotaliFalliti
			    .get(Constants.StatoSessioneIngest.CHIUSO_ERR.name())
			    .get(WebConstants.tiStatoRisoluz.WARNING.name()).get(HmKey.OGGI)
			    .toString());
	    getForm().getRiepilogoInviiOggettiFalliti().getNi_ogg_fall_war_ver_7()
		    .setValue(calcolaTotaliFalliti
			    .get(Constants.StatoSessioneIngest.CHIUSO_ERR.name())
			    .get(WebConstants.tiStatoRisoluz.WARNING.name()).get(HmKey.ULTIMI_7)
			    .toString());
	    getForm().getRiepilogoInviiOggettiFalliti().getNi_ogg_fall_war_ver_tot()
		    .setValue(calcolaTotaliFalliti
			    .get(Constants.StatoSessioneIngest.CHIUSO_ERR.name())
			    .get(WebConstants.tiStatoRisoluz.WARNING.name()).get(HmKey.TOT)
			    .toString());
	    /* DA NON VERSARE */
	    getForm().getRiepilogoInviiOggettiFalliti().getNi_ogg_fall_novers_ver_corr()
		    .setValue(calcolaTotaliFalliti
			    .get(Constants.StatoSessioneIngest.CHIUSO_ERR.name())
			    .get(WebConstants.tiStatoRisoluz.CHIUSO_WARNING.name()).get(HmKey.OGGI)
			    .toString());
	    getForm().getRiepilogoInviiOggettiFalliti().getNi_ogg_fall_novers_ver_7()
		    .setValue(calcolaTotaliFalliti
			    .get(Constants.StatoSessioneIngest.CHIUSO_ERR.name())
			    .get(WebConstants.tiStatoRisoluz.CHIUSO_WARNING.name())
			    .get(HmKey.ULTIMI_7).toString());
	    getForm().getRiepilogoInviiOggettiFalliti().getNi_ogg_fall_novers_ver_tot()
		    .setValue(calcolaTotaliFalliti
			    .get(Constants.StatoSessioneIngest.CHIUSO_ERR.name())
			    .get(WebConstants.tiStatoRisoluz.CHIUSO_WARNING.name()).get(HmKey.TOT)
			    .toString());
	    /* NON RISOLUBILI */
	    getForm().getRiepilogoInviiOggettiFalliti().getNi_ogg_fall_norisolub_corr()
		    .setValue(calcolaTotaliFalliti
			    .get(Constants.StatoSessioneIngest.CHIUSO_ERR.name())
			    .get(WebConstants.tiStatoRisoluz.NON_RISOLUBILE.name()).get(HmKey.OGGI)
			    .toString());
	    getForm().getRiepilogoInviiOggettiFalliti().getNi_ogg_fall_norisolub_7()
		    .setValue(calcolaTotaliFalliti
			    .get(Constants.StatoSessioneIngest.CHIUSO_ERR.name())
			    .get(WebConstants.tiStatoRisoluz.NON_RISOLUBILE.name())
			    .get(HmKey.ULTIMI_7).toString());
	    getForm().getRiepilogoInviiOggettiFalliti().getNi_ogg_fall_norisolub_tot()
		    .setValue(calcolaTotaliFalliti
			    .get(Constants.StatoSessioneIngest.CHIUSO_ERR.name())
			    .get(WebConstants.tiStatoRisoluz.NON_RISOLUBILE.name()).get(HmKey.TOT)
			    .toString());
	    /* NON RISOLTI E VERIFICATI */
	    getForm().getRiepilogoInviiOggettiFalliti().getNi_ogg_fall_noris_ver_corr()
		    .setValue(calcolaTotaliFalliti
			    .get(Constants.StatoSessioneIngest.CHIUSO_ERR.name())
			    .get(WebConstants.tiStatoRisoluz.NON_RISOLTO_VERIFICATO.name())
			    .get(HmKey.OGGI).toString());
	    getForm().getRiepilogoInviiOggettiFalliti().getNi_ogg_fall_noris_ver_7()
		    .setValue(calcolaTotaliFalliti
			    .get(Constants.StatoSessioneIngest.CHIUSO_ERR.name())
			    .get(WebConstants.tiStatoRisoluz.NON_RISOLTO_VERIFICATO.name())
			    .get(HmKey.ULTIMI_7).toString());
	    getForm().getRiepilogoInviiOggettiFalliti().getNi_ogg_fall_noris_ver_tot()
		    .setValue(calcolaTotaliFalliti
			    .get(Constants.StatoSessioneIngest.CHIUSO_ERR.name())
			    .get(WebConstants.tiStatoRisoluz.NON_RISOLTO_VERIFICATO.name())
			    .get(HmKey.TOT).toString());
	    /* NON RISOLTI E NON VERIFICATI */
	    getForm().getRiepilogoInviiOggettiFalliti().getNi_ogg_fall_noris_nover_corr()
		    .setValue(calcolaTotaliFalliti
			    .get(Constants.StatoSessioneIngest.CHIUSO_ERR.name())
			    .get(WebConstants.tiStatoRisoluz.NON_RISOLTO_NON_VERIFICATO.name())
			    .get(HmKey.OGGI).toString());
	    getForm().getRiepilogoInviiOggettiFalliti().getNi_ogg_fall_noris_nover_7()
		    .setValue(calcolaTotaliFalliti
			    .get(Constants.StatoSessioneIngest.CHIUSO_ERR.name())
			    .get(WebConstants.tiStatoRisoluz.NON_RISOLTO_NON_VERIFICATO.name())
			    .get(HmKey.ULTIMI_7).toString());
	    getForm().getRiepilogoInviiOggettiFalliti().getNi_ogg_fall_noris_nover_tot()
		    .setValue(calcolaTotaliFalliti
			    .get(Constants.StatoSessioneIngest.CHIUSO_ERR.name())
			    .get(WebConstants.tiStatoRisoluz.NON_RISOLTO_NON_VERIFICATO.name())
			    .get(HmKey.TOT).toString());

	    // Setto i valori nella form RiepilogoNotificheFileFallite
	    /* TOTALI */
	    getForm().getRiepilogoNotificheFileFallite().getNi_not_file_tot_corr().setValue(
		    calcolaTotaliFalliti.get(Constants.StatoOggetto.CHIUSO_ERR_NOTIF.name())
			    .get("TOTALI").get(HmKey.OGGI).toString());
	    getForm().getRiepilogoNotificheFileFallite().getNi_not_file_tot_7().setValue(
		    calcolaTotaliFalliti.get(Constants.StatoOggetto.CHIUSO_ERR_NOTIF.name())
			    .get("TOTALI").get(HmKey.ULTIMI_7).toString());
	    getForm().getRiepilogoNotificheFileFallite().getNi_not_file_tot_tot().setValue(
		    calcolaTotaliFalliti.get(Constants.StatoOggetto.CHIUSO_ERR_NOTIF.name())
			    .get("TOTALI").get(HmKey.TOT).toString());
	    /* RISOLTI */
	    getForm().getRiepilogoNotificheFileFallite().getNi_not_file_ris_ver_corr().setValue(
		    calcolaTotaliFalliti.get(Constants.StatoOggetto.CHIUSO_ERR_NOTIF.name())
			    .get(WebConstants.tiStatoRisoluz.RISOLTO.name()).get(HmKey.OGGI)
			    .toString());
	    getForm().getRiepilogoNotificheFileFallite().getNi_not_file_ris_ver_7().setValue(
		    calcolaTotaliFalliti.get(Constants.StatoOggetto.CHIUSO_ERR_NOTIF.name())
			    .get(WebConstants.tiStatoRisoluz.RISOLTO.name()).get(HmKey.ULTIMI_7)
			    .toString());
	    getForm().getRiepilogoNotificheFileFallite().getNi_not_file_ris_ver_tot().setValue(
		    calcolaTotaliFalliti.get(Constants.StatoOggetto.CHIUSO_ERR_NOTIF.name())
			    .get(WebConstants.tiStatoRisoluz.RISOLTO.name()).get(HmKey.TOT)
			    .toString());
	    /* IN CORSO DI RISOLUZIONE */
	    getForm().getRiepilogoNotificheFileFallite().getNi_not_file_corso_ris_ver_corr()
		    .setValue(
			    calcolaTotaliFalliti.get(Constants.StatoOggetto.CHIUSO_ERR_NOTIF.name())
				    .get(WebConstants.tiStatoRisoluz.IN_CORSO.name())
				    .get(HmKey.OGGI).toString());
	    getForm().getRiepilogoNotificheFileFallite().getNi_not_file_corso_ris_ver_7().setValue(
		    calcolaTotaliFalliti.get(Constants.StatoOggetto.CHIUSO_ERR_NOTIF.name())
			    .get(WebConstants.tiStatoRisoluz.IN_CORSO.name()).get(HmKey.ULTIMI_7)
			    .toString());
	    getForm().getRiepilogoNotificheFileFallite().getNi_not_file_corso_ris_ver_tot()
		    .setValue(
			    calcolaTotaliFalliti.get(Constants.StatoOggetto.CHIUSO_ERR_NOTIF.name())
				    .get(WebConstants.tiStatoRisoluz.IN_CORSO.name()).get(HmKey.TOT)
				    .toString());
	    /* NON RISOLUBILI */
	    getForm().getRiepilogoNotificheFileFallite().getNi_not_file_norisolub_corr().setValue(
		    calcolaTotaliFalliti.get(Constants.StatoOggetto.CHIUSO_ERR_NOTIF.name())
			    .get(WebConstants.tiStatoRisoluz.NON_RISOLUBILE.name()).get(HmKey.OGGI)
			    .toString());
	    getForm().getRiepilogoNotificheFileFallite().getNi_not_file_norisolub_7().setValue(
		    calcolaTotaliFalliti.get(Constants.StatoOggetto.CHIUSO_ERR_NOTIF.name())
			    .get(WebConstants.tiStatoRisoluz.NON_RISOLUBILE.name())
			    .get(HmKey.ULTIMI_7).toString());
	    getForm().getRiepilogoNotificheFileFallite().getNi_not_file_norisolub_tot().setValue(
		    calcolaTotaliFalliti.get(Constants.StatoOggetto.CHIUSO_ERR_NOTIF.name())
			    .get(WebConstants.tiStatoRisoluz.NON_RISOLUBILE.name()).get(HmKey.TOT)
			    .toString());
	    /* NON RISOLTI E VERIFICATI */
	    getForm().getRiepilogoNotificheFileFallite().getNi_not_file_noris_ver_corr().setValue(
		    calcolaTotaliFalliti.get(Constants.StatoOggetto.CHIUSO_ERR_NOTIF.name())
			    .get(WebConstants.tiStatoRisoluz.NON_RISOLTO_VERIFICATO.name())
			    .get(HmKey.OGGI).toString());
	    getForm().getRiepilogoNotificheFileFallite().getNi_not_file_noris_ver_7().setValue(
		    calcolaTotaliFalliti.get(Constants.StatoOggetto.CHIUSO_ERR_NOTIF.name())
			    .get(WebConstants.tiStatoRisoluz.NON_RISOLTO_VERIFICATO.name())
			    .get(HmKey.ULTIMI_7).toString());
	    getForm().getRiepilogoNotificheFileFallite().getNi_not_file_noris_ver_tot().setValue(
		    calcolaTotaliFalliti.get(Constants.StatoOggetto.CHIUSO_ERR_NOTIF.name())
			    .get(WebConstants.tiStatoRisoluz.NON_RISOLTO_VERIFICATO.name())
			    .get(HmKey.TOT).toString());
	    /* NON RISOLTI E NON VERIFICATI */
	    getForm().getRiepilogoNotificheFileFallite().getNi_not_file_noris_nover_corr()
		    .setValue(calcolaTotaliFalliti
			    .get(Constants.StatoOggetto.CHIUSO_ERR_NOTIF.name())
			    .get(WebConstants.tiStatoRisoluz.NON_RISOLTO_NON_VERIFICATO.name())
			    .get(HmKey.OGGI).toString());
	    getForm().getRiepilogoNotificheFileFallite().getNi_not_file_noris_nover_7()
		    .setValue(calcolaTotaliFalliti
			    .get(Constants.StatoOggetto.CHIUSO_ERR_NOTIF.name())
			    .get(WebConstants.tiStatoRisoluz.NON_RISOLTO_NON_VERIFICATO.name())
			    .get(HmKey.ULTIMI_7).toString());
	    getForm().getRiepilogoNotificheFileFallite().getNi_not_file_noris_nover_tot()
		    .setValue(calcolaTotaliFalliti
			    .get(Constants.StatoOggetto.CHIUSO_ERR_NOTIF.name())
			    .get(WebConstants.tiStatoRisoluz.NON_RISOLTO_NON_VERIFICATO.name())
			    .get(HmKey.TOT).toString());

	    // MEV 31102 Setto i valori nella form RiepilogoVerfichehashfallite
	    /* TOTALI */
	    getForm().getRiepilogoVerificheHashFallite().getNi_ver_hash_fall_tot_corr()
		    .setValue(calcolaTotaliFalliti
			    .get(Constants.StatoOggetto.CHIUSO_ERR_VERIFICA_HASH.name())
			    .get("TOTALI").get(HmKey.OGGI).toString());
	    getForm().getRiepilogoVerificheHashFallite().getNi_ver_hash_fall_tot_7()
		    .setValue(calcolaTotaliFalliti
			    .get(Constants.StatoOggetto.CHIUSO_ERR_VERIFICA_HASH.name())
			    .get("TOTALI").get(HmKey.ULTIMI_7).toString());
	    getForm().getRiepilogoVerificheHashFallite().getNi_ver_hash_fall_tot_tot()
		    .setValue(calcolaTotaliFalliti
			    .get(Constants.StatoOggetto.CHIUSO_ERR_VERIFICA_HASH.name())
			    .get("TOTALI").get(HmKey.TOT).toString());
	    /* RISOLTI */
	    getForm().getRiepilogoVerificheHashFallite().getNi_ver_hash_fall_ris_ver_corr()
		    .setValue(calcolaTotaliFalliti
			    .get(Constants.StatoOggetto.CHIUSO_ERR_VERIFICA_HASH.name())
			    .get(WebConstants.tiStatoRisoluz.RISOLTO.name()).get(HmKey.OGGI)
			    .toString());
	    getForm().getRiepilogoVerificheHashFallite().getNi_ver_hash_fall_ris_ver_7()
		    .setValue(calcolaTotaliFalliti
			    .get(Constants.StatoOggetto.CHIUSO_ERR_VERIFICA_HASH.name())
			    .get(WebConstants.tiStatoRisoluz.RISOLTO.name()).get(HmKey.ULTIMI_7)
			    .toString());
	    getForm().getRiepilogoVerificheHashFallite().getNi_ver_hash_fall_ris_ver_tot()
		    .setValue(calcolaTotaliFalliti
			    .get(Constants.StatoOggetto.CHIUSO_ERR_VERIFICA_HASH.name())
			    .get(WebConstants.tiStatoRisoluz.RISOLTO.name()).get(HmKey.TOT)
			    .toString());
	    /* IN CORSO DI RISOLUZIONE */
	    getForm().getRiepilogoVerificheHashFallite().getNi_ver_hash_fall_corso_ris_ver_corr()
		    .setValue(calcolaTotaliFalliti
			    .get(Constants.StatoOggetto.CHIUSO_ERR_VERIFICA_HASH.name())
			    .get(WebConstants.tiStatoRisoluz.IN_CORSO.name()).get(HmKey.OGGI)
			    .toString());
	    getForm().getRiepilogoVerificheHashFallite().getNi_ver_hash_fall_corso_ris_ver_7()
		    .setValue(calcolaTotaliFalliti
			    .get(Constants.StatoOggetto.CHIUSO_ERR_VERIFICA_HASH.name())
			    .get(WebConstants.tiStatoRisoluz.IN_CORSO.name()).get(HmKey.ULTIMI_7)
			    .toString());
	    getForm().getRiepilogoVerificheHashFallite().getNi_ver_hash_fall_corso_ris_ver_tot()
		    .setValue(calcolaTotaliFalliti
			    .get(Constants.StatoOggetto.CHIUSO_ERR_VERIFICA_HASH.name())
			    .get(WebConstants.tiStatoRisoluz.IN_CORSO.name()).get(HmKey.TOT)
			    .toString());
	    /* NON RISOLUBILI */
	    getForm().getRiepilogoVerificheHashFallite().getNi_ver_hash_fall_norisolub_corr()
		    .setValue(calcolaTotaliFalliti
			    .get(Constants.StatoOggetto.CHIUSO_ERR_VERIFICA_HASH.name())
			    .get(WebConstants.tiStatoRisoluz.NON_RISOLUBILE.name()).get(HmKey.OGGI)
			    .toString());
	    getForm().getRiepilogoVerificheHashFallite().getNi_ver_hash_fall_norisolub_7()
		    .setValue(calcolaTotaliFalliti
			    .get(Constants.StatoOggetto.CHIUSO_ERR_VERIFICA_HASH.name())
			    .get(WebConstants.tiStatoRisoluz.NON_RISOLUBILE.name())
			    .get(HmKey.ULTIMI_7).toString());
	    getForm().getRiepilogoVerificheHashFallite().getNi_ver_hash_fall_norisolub_tot()
		    .setValue(calcolaTotaliFalliti
			    .get(Constants.StatoOggetto.CHIUSO_ERR_VERIFICA_HASH.name())
			    .get(WebConstants.tiStatoRisoluz.NON_RISOLUBILE.name()).get(HmKey.TOT)
			    .toString());
	    /* NON RISOLTI E VERIFICATI */
	    getForm().getRiepilogoVerificheHashFallite().getNi_ver_hash_fall_noris_ver_corr()
		    .setValue(calcolaTotaliFalliti
			    .get(Constants.StatoOggetto.CHIUSO_ERR_VERIFICA_HASH.name())
			    .get(WebConstants.tiStatoRisoluz.NON_RISOLTO_VERIFICATO.name())
			    .get(HmKey.OGGI).toString());
	    getForm().getRiepilogoVerificheHashFallite().getNi_ver_hash_fall_noris_ver_7()
		    .setValue(calcolaTotaliFalliti
			    .get(Constants.StatoOggetto.CHIUSO_ERR_VERIFICA_HASH.name())
			    .get(WebConstants.tiStatoRisoluz.NON_RISOLTO_VERIFICATO.name())
			    .get(HmKey.ULTIMI_7).toString());
	    getForm().getRiepilogoVerificheHashFallite().getNi_ver_hash_fall_noris_ver_tot()
		    .setValue(calcolaTotaliFalliti
			    .get(Constants.StatoOggetto.CHIUSO_ERR_VERIFICA_HASH.name())
			    .get(WebConstants.tiStatoRisoluz.NON_RISOLTO_VERIFICATO.name())
			    .get(HmKey.TOT).toString());
	    /* NON RISOLTI E NON VERIFICATI */
	    getForm().getRiepilogoVerificheHashFallite().getNi_ver_hash_fall_noris_nover_corr()
		    .setValue(calcolaTotaliFalliti
			    .get(Constants.StatoOggetto.CHIUSO_ERR_VERIFICA_HASH.name())
			    .get(WebConstants.tiStatoRisoluz.NON_RISOLTO_NON_VERIFICATO.name())
			    .get(HmKey.OGGI).toString());
	    getForm().getRiepilogoVerificheHashFallite().getNi_ver_hash_fall_noris_nover_7()
		    .setValue(calcolaTotaliFalliti
			    .get(Constants.StatoOggetto.CHIUSO_ERR_VERIFICA_HASH.name())
			    .get(WebConstants.tiStatoRisoluz.NON_RISOLTO_NON_VERIFICATO.name())
			    .get(HmKey.ULTIMI_7).toString());
	    getForm().getRiepilogoVerificheHashFallite().getNi_ver_hash_fall_noris_nover_tot()
		    .setValue(calcolaTotaliFalliti
			    .get(Constants.StatoOggetto.CHIUSO_ERR_VERIFICA_HASH.name())
			    .get(WebConstants.tiStatoRisoluz.NON_RISOLTO_NON_VERIFICATO.name())
			    .get(HmKey.TOT).toString());

	    // Setto i valori nella form RiepilogoPreparazioniXMLFallite
	    /* TOTALI */
	    getForm().getRiepilogoPreparazioniXMLFallite().getNi_prep_xml_fall_tot_corr().setValue(
		    calcolaTotaliFalliti.get(Constants.StatoOggetto.CHIUSO_ERR_SCHED.name())
			    .get("TOTALI").get(HmKey.OGGI).toString());
	    getForm().getRiepilogoPreparazioniXMLFallite().getNi_prep_xml_fall_tot_7().setValue(
		    calcolaTotaliFalliti.get(Constants.StatoOggetto.CHIUSO_ERR_SCHED.name())
			    .get("TOTALI").get(HmKey.ULTIMI_7).toString());
	    getForm().getRiepilogoPreparazioniXMLFallite().getNi_prep_xml_fall_tot_tot().setValue(
		    calcolaTotaliFalliti.get(Constants.StatoOggetto.CHIUSO_ERR_SCHED.name())
			    .get("TOTALI").get(HmKey.TOT).toString());
	    /* RISOLTI */
	    getForm().getRiepilogoPreparazioniXMLFallite().getNi_prep_xml_fall_ris_ver_corr()
		    .setValue(
			    calcolaTotaliFalliti.get(Constants.StatoOggetto.CHIUSO_ERR_SCHED.name())
				    .get(WebConstants.tiStatoRisoluz.RISOLTO.name()).get(HmKey.OGGI)
				    .toString());
	    getForm().getRiepilogoPreparazioniXMLFallite().getNi_prep_xml_fall_ris_ver_7().setValue(
		    calcolaTotaliFalliti.get(Constants.StatoOggetto.CHIUSO_ERR_SCHED.name())
			    .get(WebConstants.tiStatoRisoluz.RISOLTO.name()).get(HmKey.ULTIMI_7)
			    .toString());
	    getForm().getRiepilogoPreparazioniXMLFallite().getNi_prep_xml_fall_ris_ver_tot()
		    .setValue(
			    calcolaTotaliFalliti.get(Constants.StatoOggetto.CHIUSO_ERR_SCHED.name())
				    .get(WebConstants.tiStatoRisoluz.RISOLTO.name()).get(HmKey.TOT)
				    .toString());
	    /* IN CORSO DI RISOLUZIONE */
	    getForm().getRiepilogoPreparazioniXMLFallite().getNi_prep_xml_fall_corso_ris_ver_corr()
		    .setValue(
			    calcolaTotaliFalliti.get(Constants.StatoOggetto.CHIUSO_ERR_SCHED.name())
				    .get(WebConstants.tiStatoRisoluz.IN_CORSO.name())
				    .get(HmKey.OGGI).toString());
	    getForm().getRiepilogoPreparazioniXMLFallite().getNi_prep_xml_fall_corso_ris_ver_7()
		    .setValue(
			    calcolaTotaliFalliti.get(Constants.StatoOggetto.CHIUSO_ERR_SCHED.name())
				    .get(WebConstants.tiStatoRisoluz.IN_CORSO.name())
				    .get(HmKey.ULTIMI_7).toString());
	    getForm().getRiepilogoPreparazioniXMLFallite().getNi_prep_xml_fall_corso_ris_ver_tot()
		    .setValue(
			    calcolaTotaliFalliti.get(Constants.StatoOggetto.CHIUSO_ERR_SCHED.name())
				    .get(WebConstants.tiStatoRisoluz.IN_CORSO.name()).get(HmKey.TOT)
				    .toString());
	    /* NON RISOLUBILI */
	    getForm().getRiepilogoPreparazioniXMLFallite().getNi_prep_xml_fall_norisolub_corr()
		    .setValue(
			    calcolaTotaliFalliti.get(Constants.StatoOggetto.CHIUSO_ERR_SCHED.name())
				    .get(WebConstants.tiStatoRisoluz.NON_RISOLUBILE.name())
				    .get(HmKey.OGGI).toString());
	    getForm().getRiepilogoPreparazioniXMLFallite().getNi_prep_xml_fall_norisolub_7()
		    .setValue(
			    calcolaTotaliFalliti.get(Constants.StatoOggetto.CHIUSO_ERR_SCHED.name())
				    .get(WebConstants.tiStatoRisoluz.NON_RISOLUBILE.name())
				    .get(HmKey.ULTIMI_7).toString());
	    getForm().getRiepilogoPreparazioniXMLFallite().getNi_prep_xml_fall_norisolub_tot()
		    .setValue(
			    calcolaTotaliFalliti.get(Constants.StatoOggetto.CHIUSO_ERR_SCHED.name())
				    .get(WebConstants.tiStatoRisoluz.NON_RISOLUBILE.name())
				    .get(HmKey.TOT).toString());
	    /* NON RISOLTI E VERIFICATI */
	    getForm().getRiepilogoPreparazioniXMLFallite().getNi_prep_xml_fall_noris_ver_corr()
		    .setValue(
			    calcolaTotaliFalliti.get(Constants.StatoOggetto.CHIUSO_ERR_SCHED.name())
				    .get(WebConstants.tiStatoRisoluz.NON_RISOLTO_VERIFICATO.name())
				    .get(HmKey.OGGI).toString());
	    getForm().getRiepilogoPreparazioniXMLFallite().getNi_prep_xml_fall_noris_ver_7()
		    .setValue(
			    calcolaTotaliFalliti.get(Constants.StatoOggetto.CHIUSO_ERR_SCHED.name())
				    .get(WebConstants.tiStatoRisoluz.NON_RISOLTO_VERIFICATO.name())
				    .get(HmKey.ULTIMI_7).toString());
	    getForm().getRiepilogoPreparazioniXMLFallite().getNi_prep_xml_fall_noris_ver_tot()
		    .setValue(
			    calcolaTotaliFalliti.get(Constants.StatoOggetto.CHIUSO_ERR_SCHED.name())
				    .get(WebConstants.tiStatoRisoluz.NON_RISOLTO_VERIFICATO.name())
				    .get(HmKey.TOT).toString());
	    /* NON RISOLTI E NON VERIFICATI */
	    getForm().getRiepilogoPreparazioniXMLFallite().getNi_prep_xml_fall_noris_nover_corr()
		    .setValue(calcolaTotaliFalliti
			    .get(Constants.StatoOggetto.CHIUSO_ERR_SCHED.name())
			    .get(WebConstants.tiStatoRisoluz.NON_RISOLTO_NON_VERIFICATO.name())
			    .get(HmKey.OGGI).toString());
	    getForm().getRiepilogoPreparazioniXMLFallite().getNi_prep_xml_fall_noris_nover_7()
		    .setValue(calcolaTotaliFalliti
			    .get(Constants.StatoOggetto.CHIUSO_ERR_SCHED.name())
			    .get(WebConstants.tiStatoRisoluz.NON_RISOLTO_NON_VERIFICATO.name())
			    .get(HmKey.ULTIMI_7).toString());
	    getForm().getRiepilogoPreparazioniXMLFallite().getNi_prep_xml_fall_noris_nover_tot()
		    .setValue(calcolaTotaliFalliti
			    .get(Constants.StatoOggetto.CHIUSO_ERR_SCHED.name())
			    .get(WebConstants.tiStatoRisoluz.NON_RISOLTO_NON_VERIFICATO.name())
			    .get(HmKey.TOT).toString());

	    // Setto i valori nella form RiepilogoRegistrazioniCodaFallite
	    /* TOTALI */
	    getForm().getRiepilogoRegistrazioniCodaFallite().getNi_reg_coda_fall_tot_corr()
		    .setValue(
			    calcolaTotaliFalliti.get(Constants.StatoOggetto.CHIUSO_ERR_CODA.name())
				    .get("TOTALI").get(HmKey.OGGI).toString());
	    getForm().getRiepilogoRegistrazioniCodaFallite().getNi_reg_coda_fall_tot_7().setValue(
		    calcolaTotaliFalliti.get(Constants.StatoOggetto.CHIUSO_ERR_CODA.name())
			    .get("TOTALI").get(HmKey.ULTIMI_7).toString());
	    getForm().getRiepilogoRegistrazioniCodaFallite().getNi_reg_coda_fall_tot_tot().setValue(
		    calcolaTotaliFalliti.get(Constants.StatoOggetto.CHIUSO_ERR_CODA.name())
			    .get("TOTALI").get(HmKey.TOT).toString());
	    /* RISOLTI */
	    getForm().getRiepilogoRegistrazioniCodaFallite().getNi_reg_coda_fall_ris_ver_corr()
		    .setValue(
			    calcolaTotaliFalliti.get(Constants.StatoOggetto.CHIUSO_ERR_CODA.name())
				    .get(WebConstants.tiStatoRisoluz.RISOLTO.name()).get(HmKey.OGGI)
				    .toString());
	    getForm().getRiepilogoRegistrazioniCodaFallite().getNi_reg_coda_fall_ris_ver_7()
		    .setValue(
			    calcolaTotaliFalliti.get(Constants.StatoOggetto.CHIUSO_ERR_CODA.name())
				    .get(WebConstants.tiStatoRisoluz.RISOLTO.name())
				    .get(HmKey.ULTIMI_7).toString());
	    getForm().getRiepilogoRegistrazioniCodaFallite().getNi_reg_coda_fall_ris_ver_tot()
		    .setValue(
			    calcolaTotaliFalliti.get(Constants.StatoOggetto.CHIUSO_ERR_CODA.name())
				    .get(WebConstants.tiStatoRisoluz.RISOLTO.name()).get(HmKey.TOT)
				    .toString());
	    /* IN CORSO DI RISOLUZIONE */
	    getForm().getRiepilogoRegistrazioniCodaFallite()
		    .getNi_reg_coda_fall_corso_ris_ver_corr().setValue(
			    calcolaTotaliFalliti.get(Constants.StatoOggetto.CHIUSO_ERR_CODA.name())
				    .get(WebConstants.tiStatoRisoluz.IN_CORSO.name())
				    .get(HmKey.OGGI).toString());
	    getForm().getRiepilogoRegistrazioniCodaFallite().getNi_reg_coda_fall_corso_ris_ver_7()
		    .setValue(
			    calcolaTotaliFalliti.get(Constants.StatoOggetto.CHIUSO_ERR_CODA.name())
				    .get(WebConstants.tiStatoRisoluz.IN_CORSO.name())
				    .get(HmKey.ULTIMI_7).toString());
	    getForm().getRiepilogoRegistrazioniCodaFallite().getNi_reg_coda_fall_corso_ris_ver_tot()
		    .setValue(
			    calcolaTotaliFalliti.get(Constants.StatoOggetto.CHIUSO_ERR_CODA.name())
				    .get(WebConstants.tiStatoRisoluz.IN_CORSO.name()).get(HmKey.TOT)
				    .toString());
	    /* NON RISOLUBILI */
	    getForm().getRiepilogoRegistrazioniCodaFallite().getNi_reg_coda_fall_norisolub_corr()
		    .setValue(
			    calcolaTotaliFalliti.get(Constants.StatoOggetto.CHIUSO_ERR_CODA.name())
				    .get(WebConstants.tiStatoRisoluz.NON_RISOLUBILE.name())
				    .get(HmKey.OGGI).toString());
	    getForm().getRiepilogoRegistrazioniCodaFallite().getNi_reg_coda_fall_norisolub_7()
		    .setValue(
			    calcolaTotaliFalliti.get(Constants.StatoOggetto.CHIUSO_ERR_CODA.name())
				    .get(WebConstants.tiStatoRisoluz.NON_RISOLUBILE.name())
				    .get(HmKey.ULTIMI_7).toString());
	    getForm().getRiepilogoRegistrazioniCodaFallite().getNi_reg_coda_fall_norisolub_tot()
		    .setValue(
			    calcolaTotaliFalliti.get(Constants.StatoOggetto.CHIUSO_ERR_CODA.name())
				    .get(WebConstants.tiStatoRisoluz.NON_RISOLUBILE.name())
				    .get(HmKey.TOT).toString());
	    /* NON RISOLTI E VERIFICATI */
	    getForm().getRiepilogoRegistrazioniCodaFallite().getNi_reg_coda_fall_noris_ver_corr()
		    .setValue(
			    calcolaTotaliFalliti.get(Constants.StatoOggetto.CHIUSO_ERR_CODA.name())
				    .get(WebConstants.tiStatoRisoluz.NON_RISOLTO_VERIFICATO.name())
				    .get(HmKey.OGGI).toString());
	    getForm().getRiepilogoRegistrazioniCodaFallite().getNi_reg_coda_fall_noris_ver_7()
		    .setValue(
			    calcolaTotaliFalliti.get(Constants.StatoOggetto.CHIUSO_ERR_CODA.name())
				    .get(WebConstants.tiStatoRisoluz.NON_RISOLTO_VERIFICATO.name())
				    .get(HmKey.ULTIMI_7).toString());
	    getForm().getRiepilogoRegistrazioniCodaFallite().getNi_reg_coda_fall_noris_ver_tot()
		    .setValue(
			    calcolaTotaliFalliti.get(Constants.StatoOggetto.CHIUSO_ERR_CODA.name())
				    .get(WebConstants.tiStatoRisoluz.NON_RISOLTO_VERIFICATO.name())
				    .get(HmKey.TOT).toString());
	    /* NON RISOLTI E NON VERIFICATI */
	    getForm().getRiepilogoRegistrazioniCodaFallite().getNi_reg_coda_fall_noris_nover_corr()
		    .setValue(calcolaTotaliFalliti
			    .get(Constants.StatoOggetto.CHIUSO_ERR_CODA.name())
			    .get(WebConstants.tiStatoRisoluz.NON_RISOLTO_NON_VERIFICATO.name())
			    .get(HmKey.OGGI).toString());
	    getForm().getRiepilogoRegistrazioniCodaFallite().getNi_reg_coda_fall_noris_nover_7()
		    .setValue(calcolaTotaliFalliti
			    .get(Constants.StatoOggetto.CHIUSO_ERR_CODA.name())
			    .get(WebConstants.tiStatoRisoluz.NON_RISOLTO_NON_VERIFICATO.name())
			    .get(HmKey.ULTIMI_7).toString());
	    getForm().getRiepilogoRegistrazioniCodaFallite().getNi_reg_coda_fall_noris_nover_tot()
		    .setValue(calcolaTotaliFalliti
			    .get(Constants.StatoOggetto.CHIUSO_ERR_CODA.name())
			    .get(WebConstants.tiStatoRisoluz.NON_RISOLTO_NON_VERIFICATO.name())
			    .get(HmKey.TOT).toString());

	    // Setto i valori nella form RiepilogoVersamentiSacerFalliti
	    /* TOTALI */
	    getForm().getRiepilogoVersamentiSacerFalliti().getNi_vers_sacer_fall_tot_corr()
		    .setValue(getCountTotaliOggetti(calcolaTotaliFalliti, HmKey.OGGI, "TOTALI",
			    Constants.StatoOggetto.CHIUSO_ERR_VERS.name(),
			    Constants.StatoOggetto.CHIUSO_ERR_RECUPERABILE.name()).toString());
	    getForm().getRiepilogoVersamentiSacerFalliti().getNi_vers_sacer_fall_tot_7()
		    .setValue(getCountTotaliOggetti(calcolaTotaliFalliti, HmKey.ULTIMI_7, "TOTALI",
			    Constants.StatoOggetto.CHIUSO_ERR_VERS.name(),
			    Constants.StatoOggetto.CHIUSO_ERR_RECUPERABILE.name()).toString());
	    getForm().getRiepilogoVersamentiSacerFalliti().getNi_vers_sacer_fall_tot_tot()
		    .setValue(getCountTotaliOggetti(calcolaTotaliFalliti, HmKey.TOT, "TOTALI",
			    Constants.StatoOggetto.CHIUSO_ERR_VERS.name(),
			    Constants.StatoOggetto.CHIUSO_ERR_RECUPERABILE.name()).toString());
	    /* RISOLTI */
	    getForm().getRiepilogoVersamentiSacerFalliti().getNi_vers_sacer_fall_ris_ver_corr()
		    .setValue(getCountTotaliOggetti(calcolaTotaliFalliti, HmKey.OGGI,
			    WebConstants.tiStatoRisoluz.RISOLTO.name(),
			    Constants.StatoOggetto.CHIUSO_ERR_VERS.name(),
			    Constants.StatoOggetto.CHIUSO_ERR_RECUPERABILE.name()).toString());
	    getForm().getRiepilogoVersamentiSacerFalliti().getNi_vers_sacer_fall_ris_ver_7()
		    .setValue(getCountTotaliOggetti(calcolaTotaliFalliti, HmKey.ULTIMI_7,
			    WebConstants.tiStatoRisoluz.RISOLTO.name(),
			    Constants.StatoOggetto.CHIUSO_ERR_VERS.name(),
			    Constants.StatoOggetto.CHIUSO_ERR_RECUPERABILE.name()).toString());
	    getForm().getRiepilogoVersamentiSacerFalliti().getNi_vers_sacer_fall_ris_ver_tot()
		    .setValue(getCountTotaliOggetti(calcolaTotaliFalliti, HmKey.TOT,
			    WebConstants.tiStatoRisoluz.RISOLTO.name(),
			    Constants.StatoOggetto.CHIUSO_ERR_VERS.name(),
			    Constants.StatoOggetto.CHIUSO_ERR_RECUPERABILE.name()).toString());
	    /* IN CORSO DI RISOLUZIONE */
	    getForm().getRiepilogoVersamentiSacerFalliti()
		    .getNi_vers_sacer_fall_corso_ris_ver_corr()
		    .setValue(getCountTotaliOggetti(calcolaTotaliFalliti, HmKey.OGGI,
			    WebConstants.tiStatoRisoluz.IN_CORSO.name(),
			    Constants.StatoOggetto.CHIUSO_ERR_VERS.name(),
			    Constants.StatoOggetto.CHIUSO_ERR_RECUPERABILE.name()).toString());
	    getForm().getRiepilogoVersamentiSacerFalliti().getNi_vers_sacer_fall_corso_ris_ver_7()
		    .setValue(getCountTotaliOggetti(calcolaTotaliFalliti, HmKey.ULTIMI_7,
			    WebConstants.tiStatoRisoluz.IN_CORSO.name(),
			    Constants.StatoOggetto.CHIUSO_ERR_VERS.name(),
			    Constants.StatoOggetto.CHIUSO_ERR_RECUPERABILE.name()).toString());
	    getForm().getRiepilogoVersamentiSacerFalliti().getNi_vers_sacer_fall_corso_ris_ver_tot()
		    .setValue(getCountTotaliOggetti(calcolaTotaliFalliti, HmKey.TOT,
			    WebConstants.tiStatoRisoluz.IN_CORSO.name(),
			    Constants.StatoOggetto.CHIUSO_ERR_VERS.name(),
			    Constants.StatoOggetto.CHIUSO_ERR_RECUPERABILE.name()).toString());
	    /* NON RISOLUBILI */
	    getForm().getRiepilogoVersamentiSacerFalliti().getNi_vers_sacer_fall_norisolub_corr()
		    .setValue(getCountTotaliOggetti(calcolaTotaliFalliti, HmKey.OGGI,
			    WebConstants.tiStatoRisoluz.NON_RISOLUBILE.name(),
			    Constants.StatoOggetto.CHIUSO_ERR_VERS.name(),
			    Constants.StatoOggetto.CHIUSO_ERR_RECUPERABILE.name()).toString());
	    getForm().getRiepilogoVersamentiSacerFalliti().getNi_vers_sacer_fall_norisolub_7()
		    .setValue(getCountTotaliOggetti(calcolaTotaliFalliti, HmKey.ULTIMI_7,
			    WebConstants.tiStatoRisoluz.NON_RISOLUBILE.name(),
			    Constants.StatoOggetto.CHIUSO_ERR_VERS.name(),
			    Constants.StatoOggetto.CHIUSO_ERR_RECUPERABILE.name()).toString());
	    getForm().getRiepilogoVersamentiSacerFalliti().getNi_vers_sacer_fall_norisolub_tot()
		    .setValue(getCountTotaliOggetti(calcolaTotaliFalliti, HmKey.TOT,
			    WebConstants.tiStatoRisoluz.NON_RISOLUBILE.name(),
			    Constants.StatoOggetto.CHIUSO_ERR_VERS.name(),
			    Constants.StatoOggetto.CHIUSO_ERR_RECUPERABILE.name()).toString());
	    /* NON RISOLTI E VERIFICATI */
	    getForm().getRiepilogoVersamentiSacerFalliti().getNi_vers_sacer_fall_noris_ver_corr()
		    .setValue(getCountTotaliOggetti(calcolaTotaliFalliti, HmKey.OGGI,
			    WebConstants.tiStatoRisoluz.NON_RISOLTO_VERIFICATO.name(),
			    Constants.StatoOggetto.CHIUSO_ERR_VERS.name(),
			    Constants.StatoOggetto.CHIUSO_ERR_RECUPERABILE.name()).toString());
	    getForm().getRiepilogoVersamentiSacerFalliti().getNi_vers_sacer_fall_noris_ver_7()
		    .setValue(getCountTotaliOggetti(calcolaTotaliFalliti, HmKey.ULTIMI_7,
			    WebConstants.tiStatoRisoluz.NON_RISOLTO_VERIFICATO.name(),
			    Constants.StatoOggetto.CHIUSO_ERR_VERS.name(),
			    Constants.StatoOggetto.CHIUSO_ERR_RECUPERABILE.name()).toString());
	    getForm().getRiepilogoVersamentiSacerFalliti().getNi_vers_sacer_fall_noris_ver_tot()
		    .setValue(getCountTotaliOggetti(calcolaTotaliFalliti, HmKey.TOT,
			    WebConstants.tiStatoRisoluz.NON_RISOLTO_VERIFICATO.name(),
			    Constants.StatoOggetto.CHIUSO_ERR_VERS.name(),
			    Constants.StatoOggetto.CHIUSO_ERR_RECUPERABILE.name()).toString());
	    /* NON RISOLTI E NON VERIFICATI */
	    getForm().getRiepilogoVersamentiSacerFalliti().getNi_vers_sacer_fall_noris_nover_corr()
		    .setValue(getCountTotaliOggetti(calcolaTotaliFalliti, HmKey.OGGI,
			    WebConstants.tiStatoRisoluz.NON_RISOLTO_NON_VERIFICATO.name(),
			    Constants.StatoOggetto.CHIUSO_ERR_VERS.name(),
			    Constants.StatoOggetto.CHIUSO_ERR_RECUPERABILE.name()).toString());
	    getForm().getRiepilogoVersamentiSacerFalliti().getNi_vers_sacer_fall_noris_nover_7()
		    .setValue(getCountTotaliOggetti(calcolaTotaliFalliti, HmKey.ULTIMI_7,
			    WebConstants.tiStatoRisoluz.NON_RISOLTO_NON_VERIFICATO.name(),
			    Constants.StatoOggetto.CHIUSO_ERR_VERS.name(),
			    Constants.StatoOggetto.CHIUSO_ERR_RECUPERABILE.name()).toString());
	    getForm().getRiepilogoVersamentiSacerFalliti().getNi_vers_sacer_fall_noris_nover_tot()
		    .setValue(getCountTotaliOggetti(calcolaTotaliFalliti, HmKey.TOT,
			    WebConstants.tiStatoRisoluz.NON_RISOLTO_NON_VERIFICATO.name(),
			    Constants.StatoOggetto.CHIUSO_ERR_VERS.name(),
			    Constants.StatoOggetto.CHIUSO_ERR_RECUPERABILE.name()).toString());

	    // Setto i valori nella form RiepilogoTrasformazioniFallite
	    /* TOTALI */
	    getForm().getRiepilogoTrasformazioniFallite().getNi_trasf_fall_tot_corr()
		    .setValue(calcolaTotaliFalliti
			    .get(Constants.StatoOggetto.CHIUSO_ERR_TRASFORMAZIONE.name())
			    .get("TOTALI").get(HmKey.OGGI).toString());
	    getForm().getRiepilogoTrasformazioniFallite().getNi_trasf_fall_tot_7()
		    .setValue(calcolaTotaliFalliti
			    .get(Constants.StatoOggetto.CHIUSO_ERR_TRASFORMAZIONE.name())
			    .get("TOTALI").get(HmKey.ULTIMI_7).toString());
	    getForm().getRiepilogoTrasformazioniFallite().getNi_trasf_fall_tot_tot()
		    .setValue(calcolaTotaliFalliti
			    .get(Constants.StatoOggetto.CHIUSO_ERR_TRASFORMAZIONE.name())
			    .get("TOTALI").get(HmKey.TOT).toString());
	    /* RISOLTI */
	    getForm().getRiepilogoTrasformazioniFallite().getNi_trasf_fall_ris_ver_corr()
		    .setValue(calcolaTotaliFalliti
			    .get(Constants.StatoOggetto.CHIUSO_ERR_TRASFORMAZIONE.name())
			    .get(WebConstants.tiStatoRisoluz.RISOLTO.name()).get(HmKey.OGGI)
			    .toString());
	    getForm().getRiepilogoTrasformazioniFallite().getNi_trasf_fall_ris_ver_7()
		    .setValue(calcolaTotaliFalliti
			    .get(Constants.StatoOggetto.CHIUSO_ERR_TRASFORMAZIONE.name())
			    .get(WebConstants.tiStatoRisoluz.RISOLTO.name()).get(HmKey.ULTIMI_7)
			    .toString());
	    getForm().getRiepilogoTrasformazioniFallite().getNi_trasf_fall_ris_ver_tot()
		    .setValue(calcolaTotaliFalliti
			    .get(Constants.StatoOggetto.CHIUSO_ERR_TRASFORMAZIONE.name())
			    .get(WebConstants.tiStatoRisoluz.RISOLTO.name()).get(HmKey.TOT)
			    .toString());
	    /* IN CORSO DI RISOLUZIONE */
	    getForm().getRiepilogoTrasformazioniFallite().getNi_trasf_fall_corso_ris_ver_corr()
		    .setValue(calcolaTotaliFalliti
			    .get(Constants.StatoOggetto.CHIUSO_ERR_TRASFORMAZIONE.name())
			    .get(WebConstants.tiStatoRisoluz.IN_CORSO.name()).get(HmKey.OGGI)
			    .toString());
	    getForm().getRiepilogoTrasformazioniFallite().getNi_trasf_fall_corso_ris_ver_7()
		    .setValue(calcolaTotaliFalliti
			    .get(Constants.StatoOggetto.CHIUSO_ERR_TRASFORMAZIONE.name())
			    .get(WebConstants.tiStatoRisoluz.IN_CORSO.name()).get(HmKey.ULTIMI_7)
			    .toString());
	    getForm().getRiepilogoTrasformazioniFallite().getNi_trasf_fall_corso_ris_ver_tot()
		    .setValue(calcolaTotaliFalliti
			    .get(Constants.StatoOggetto.CHIUSO_ERR_TRASFORMAZIONE.name())
			    .get(WebConstants.tiStatoRisoluz.IN_CORSO.name()).get(HmKey.TOT)
			    .toString());
	    /* NON RISOLUBILI */
	    getForm().getRiepilogoTrasformazioniFallite().getNi_trasf_fall_norisolub_corr()
		    .setValue(calcolaTotaliFalliti
			    .get(Constants.StatoOggetto.CHIUSO_ERR_TRASFORMAZIONE.name())
			    .get(WebConstants.tiStatoRisoluz.NON_RISOLUBILE.name()).get(HmKey.OGGI)
			    .toString());
	    getForm().getRiepilogoTrasformazioniFallite().getNi_trasf_fall_norisolub_7()
		    .setValue(calcolaTotaliFalliti
			    .get(Constants.StatoOggetto.CHIUSO_ERR_TRASFORMAZIONE.name())
			    .get(WebConstants.tiStatoRisoluz.NON_RISOLUBILE.name())
			    .get(HmKey.ULTIMI_7).toString());
	    getForm().getRiepilogoTrasformazioniFallite().getNi_trasf_fall_norisolub_tot()
		    .setValue(calcolaTotaliFalliti
			    .get(Constants.StatoOggetto.CHIUSO_ERR_TRASFORMAZIONE.name())
			    .get(WebConstants.tiStatoRisoluz.NON_RISOLUBILE.name()).get(HmKey.TOT)
			    .toString());
	    /* NON RISOLTI E VERIFICATI */
	    getForm().getRiepilogoTrasformazioniFallite().getNi_trasf_fall_noris_ver_corr()
		    .setValue(calcolaTotaliFalliti
			    .get(Constants.StatoOggetto.CHIUSO_ERR_TRASFORMAZIONE.name())
			    .get(WebConstants.tiStatoRisoluz.NON_RISOLTO_VERIFICATO.name())
			    .get(HmKey.OGGI).toString());
	    getForm().getRiepilogoTrasformazioniFallite().getNi_trasf_fall_noris_ver_7()
		    .setValue(calcolaTotaliFalliti
			    .get(Constants.StatoOggetto.CHIUSO_ERR_TRASFORMAZIONE.name())
			    .get(WebConstants.tiStatoRisoluz.NON_RISOLTO_VERIFICATO.name())
			    .get(HmKey.ULTIMI_7).toString());
	    getForm().getRiepilogoTrasformazioniFallite().getNi_trasf_fall_noris_ver_tot()
		    .setValue(calcolaTotaliFalliti
			    .get(Constants.StatoOggetto.CHIUSO_ERR_TRASFORMAZIONE.name())
			    .get(WebConstants.tiStatoRisoluz.NON_RISOLTO_VERIFICATO.name())
			    .get(HmKey.TOT).toString());
	    /* NON RISOLTI E NON VERIFICATI */
	    getForm().getRiepilogoTrasformazioniFallite().getNi_trasf_fall_noris_nover_corr()
		    .setValue(calcolaTotaliFalliti
			    .get(Constants.StatoOggetto.CHIUSO_ERR_TRASFORMAZIONE.name())
			    .get(WebConstants.tiStatoRisoluz.NON_RISOLTO_NON_VERIFICATO.name())
			    .get(HmKey.OGGI).toString());
	    getForm().getRiepilogoTrasformazioniFallite().getNi_trasf_fall_noris_nover_7()
		    .setValue(calcolaTotaliFalliti
			    .get(Constants.StatoOggetto.CHIUSO_ERR_TRASFORMAZIONE.name())
			    .get(WebConstants.tiStatoRisoluz.NON_RISOLTO_NON_VERIFICATO.name())
			    .get(HmKey.ULTIMI_7).toString());
	    getForm().getRiepilogoTrasformazioniFallite().getNi_trasf_fall_noris_nover_tot()
		    .setValue(calcolaTotaliFalliti
			    .get(Constants.StatoOggetto.CHIUSO_ERR_TRASFORMAZIONE.name())
			    .get(WebConstants.tiStatoRisoluz.NON_RISOLTO_NON_VERIFICATO.name())
			    .get(HmKey.TOT).toString());

	    // Setto i valori nella form RiepilogoTrasformazioniFallite
	    /* TOTALI */
	    getForm().getRiepilogoVersamentiPingFalliti().getNi_vers_ping_fall_tot_corr()
		    .setValue(calcolaTotaliFalliti
			    .get(Constants.StatoOggetto.CHIUSO_ERR_VERSAMENTO_A_PING.name())
			    .get("TOTALI").get(HmKey.OGGI).toString());
	    getForm().getRiepilogoVersamentiPingFalliti().getNi_vers_ping_fall_tot_7()
		    .setValue(calcolaTotaliFalliti
			    .get(Constants.StatoOggetto.CHIUSO_ERR_VERSAMENTO_A_PING.name())
			    .get("TOTALI").get(HmKey.ULTIMI_7).toString());
	    getForm().getRiepilogoVersamentiPingFalliti().getNi_vers_ping_fall_tot_tot()
		    .setValue(calcolaTotaliFalliti
			    .get(Constants.StatoOggetto.CHIUSO_ERR_VERSAMENTO_A_PING.name())
			    .get("TOTALI").get(HmKey.TOT).toString());
	    /* RISOLTI */
	    getForm().getRiepilogoVersamentiPingFalliti().getNi_vers_ping_fall_ris_ver_corr()
		    .setValue(calcolaTotaliFalliti
			    .get(Constants.StatoOggetto.CHIUSO_ERR_VERSAMENTO_A_PING.name())
			    .get(WebConstants.tiStatoRisoluz.RISOLTO.name()).get(HmKey.OGGI)
			    .toString());
	    getForm().getRiepilogoVersamentiPingFalliti().getNi_vers_ping_fall_ris_ver_7()
		    .setValue(calcolaTotaliFalliti
			    .get(Constants.StatoOggetto.CHIUSO_ERR_VERSAMENTO_A_PING.name())
			    .get(WebConstants.tiStatoRisoluz.RISOLTO.name()).get(HmKey.ULTIMI_7)
			    .toString());
	    getForm().getRiepilogoVersamentiPingFalliti().getNi_vers_ping_fall_ris_ver_tot()
		    .setValue(calcolaTotaliFalliti
			    .get(Constants.StatoOggetto.CHIUSO_ERR_VERSAMENTO_A_PING.name())
			    .get(WebConstants.tiStatoRisoluz.RISOLTO.name()).get(HmKey.TOT)
			    .toString());
	    /* IN CORSO DI RISOLUZIONE */
	    getForm().getRiepilogoVersamentiPingFalliti().getNi_vers_ping_fall_corso_ris_ver_corr()
		    .setValue(calcolaTotaliFalliti
			    .get(Constants.StatoOggetto.CHIUSO_ERR_VERSAMENTO_A_PING.name())
			    .get(WebConstants.tiStatoRisoluz.IN_CORSO.name()).get(HmKey.OGGI)
			    .toString());
	    getForm().getRiepilogoVersamentiPingFalliti().getNi_vers_ping_fall_corso_ris_ver_7()
		    .setValue(calcolaTotaliFalliti
			    .get(Constants.StatoOggetto.CHIUSO_ERR_VERSAMENTO_A_PING.name())
			    .get(WebConstants.tiStatoRisoluz.IN_CORSO.name()).get(HmKey.ULTIMI_7)
			    .toString());
	    getForm().getRiepilogoVersamentiPingFalliti().getNi_vers_ping_fall_corso_ris_ver_tot()
		    .setValue(calcolaTotaliFalliti
			    .get(Constants.StatoOggetto.CHIUSO_ERR_VERSAMENTO_A_PING.name())
			    .get(WebConstants.tiStatoRisoluz.IN_CORSO.name()).get(HmKey.TOT)
			    .toString());
	    /* NON RISOLUBILI */
	    getForm().getRiepilogoVersamentiPingFalliti().getNi_vers_ping_fall_norisolub_corr()
		    .setValue(calcolaTotaliFalliti
			    .get(Constants.StatoOggetto.CHIUSO_ERR_VERSAMENTO_A_PING.name())
			    .get(WebConstants.tiStatoRisoluz.NON_RISOLUBILE.name()).get(HmKey.OGGI)
			    .toString());
	    getForm().getRiepilogoVersamentiPingFalliti().getNi_vers_ping_fall_norisolub_7()
		    .setValue(calcolaTotaliFalliti
			    .get(Constants.StatoOggetto.CHIUSO_ERR_VERSAMENTO_A_PING.name())
			    .get(WebConstants.tiStatoRisoluz.NON_RISOLUBILE.name())
			    .get(HmKey.ULTIMI_7).toString());
	    getForm().getRiepilogoVersamentiPingFalliti().getNi_vers_ping_fall_norisolub_tot()
		    .setValue(calcolaTotaliFalliti
			    .get(Constants.StatoOggetto.CHIUSO_ERR_VERSAMENTO_A_PING.name())
			    .get(WebConstants.tiStatoRisoluz.NON_RISOLUBILE.name()).get(HmKey.TOT)
			    .toString());
	    /* NON RISOLTI E VERIFICATI */
	    getForm().getRiepilogoVersamentiPingFalliti().getNi_vers_ping_fall_noris_ver_corr()
		    .setValue(calcolaTotaliFalliti
			    .get(Constants.StatoOggetto.CHIUSO_ERR_VERSAMENTO_A_PING.name())
			    .get(WebConstants.tiStatoRisoluz.NON_RISOLTO_VERIFICATO.name())
			    .get(HmKey.OGGI).toString());
	    getForm().getRiepilogoVersamentiPingFalliti().getNi_vers_ping_fall_noris_ver_7()
		    .setValue(calcolaTotaliFalliti
			    .get(Constants.StatoOggetto.CHIUSO_ERR_VERSAMENTO_A_PING.name())
			    .get(WebConstants.tiStatoRisoluz.NON_RISOLTO_VERIFICATO.name())
			    .get(HmKey.ULTIMI_7).toString());
	    getForm().getRiepilogoVersamentiPingFalliti().getNi_vers_ping_fall_noris_ver_tot()
		    .setValue(calcolaTotaliFalliti
			    .get(Constants.StatoOggetto.CHIUSO_ERR_VERSAMENTO_A_PING.name())
			    .get(WebConstants.tiStatoRisoluz.NON_RISOLTO_VERIFICATO.name())
			    .get(HmKey.TOT).toString());
	    /* NON RISOLTI E NON VERIFICATI */
	    getForm().getRiepilogoVersamentiPingFalliti().getNi_vers_ping_fall_noris_nover_corr()
		    .setValue(calcolaTotaliFalliti
			    .get(Constants.StatoOggetto.CHIUSO_ERR_VERSAMENTO_A_PING.name())
			    .get(WebConstants.tiStatoRisoluz.NON_RISOLTO_NON_VERIFICATO.name())
			    .get(HmKey.OGGI).toString());
	    getForm().getRiepilogoVersamentiPingFalliti().getNi_vers_ping_fall_noris_nover_7()
		    .setValue(calcolaTotaliFalliti
			    .get(Constants.StatoOggetto.CHIUSO_ERR_VERSAMENTO_A_PING.name())
			    .get(WebConstants.tiStatoRisoluz.NON_RISOLTO_NON_VERIFICATO.name())
			    .get(HmKey.ULTIMI_7).toString());
	    getForm().getRiepilogoVersamentiPingFalliti().getNi_vers_ping_fall_noris_nover_tot()
		    .setValue(calcolaTotaliFalliti
			    .get(Constants.StatoOggetto.CHIUSO_ERR_VERSAMENTO_A_PING.name())
			    .get(WebConstants.tiStatoRisoluz.NON_RISOLTO_NON_VERIFICATO.name())
			    .get(HmKey.TOT).toString());

	    /*
	     * CALCOLO TOTALI RIEPILOGO OGGETTI DERIVANTI DA VERSAMENTI FALLITI
	     */
	    // Azzero i totali
	    getForm().getRiepilogoOggettiDerivantiVersamentiFalliti()
		    .getNi_ogg_der_vers_fall_tot_tot().setValue("0");
	    getForm().getRiepilogoOggettiDerivantiVersamentiFalliti()
		    .getNi_ogg_der_vers_fall_norisolub_tot().setValue("0");
	    getForm().getRiepilogoOggettiDerivantiVersamentiFalliti()
		    .getNi_ogg_der_vers_fall_ver_tot().setValue("0");
	    getForm().getRiepilogoOggettiDerivantiVersamentiFalliti()
		    .getNi_ogg_der_vers_fall_nover_tot().setValue("0");
	    // Ricava il tablebean della vista contenente i totali da rielaborare
	    MonVObjNonVersTableBean contaOggettiDerivantiVersFallitiTB = monitoraggioHelper
		    .getMonVObjNonVersTableBean(getUser().getIdUtente(), idAmbienteVers, idVers,
			    nmTipoObject);
	    Integer totale = 0;
	    // Inizio procedura calcolo totali
	    for (MonVObjNonVersRowBean rb : contaOggettiDerivantiVersFallitiTB) {
		if (rb.getFlVerif().equals("1") && rb.getFlNonRisolub().equals("1")) {
		    getForm().getRiepilogoOggettiDerivantiVersamentiFalliti()
			    .getNi_ogg_der_vers_fall_norisolub_tot()
			    .setValue(rb.getString("ni_invii_fall"));
		} else if (rb.getFlVerif().equals("1") && rb.getFlNonRisolub().equals("0")) {
		    getForm().getRiepilogoOggettiDerivantiVersamentiFalliti()
			    .getNi_ogg_der_vers_fall_ver_tot()
			    .setValue(rb.getString("ni_invii_fall"));
		} else if (rb.getFlVerif().equals("0") && rb.getFlNonRisolub() == null) {
		    getForm().getRiepilogoOggettiDerivantiVersamentiFalliti()
			    .getNi_ogg_der_vers_fall_nover_tot()
			    .setValue(rb.getString("ni_invii_fall"));
		}
		totale = totale + Integer.parseInt(rb.getString("ni_invii_fall"));
	    }
	    getForm().getRiepilogoOggettiDerivantiVersamentiFalliti()
		    .getNi_ogg_der_vers_fall_tot_tot().setValue(totale.toString());

	    /*
	     * CALCOLO TOTALI RIEPILOGO OGGETTI ANNULLATI O IN CORSO DI ANNULLAMENTO Per gestire
	     * meglio i contatori, creo una mappa Map<Stato, Map<hmKey, int>> in modo da ottenere
	     * piÃ¹ facilmente gli oggetti che saranno organizzati quindi per STATO e CONTEGGIO
	     * (OGGI, ULTIMI 7 e TOTALI)
	     */
	    MonVObjAnnulRangeDtTableBean contaOggettiAnnullatiTB = monitoraggioHelper
		    .getMonVObjAnnulRangeDtTableBean(getUser().getIdUtente(), idAmbienteVers,
			    idVers, idTipoObject);
	    Map<String, Map<Enum, Integer>> countersAnnullati = calcolaTotaliOggetti(
		    contaOggettiAnnullatiTB, "");

	    // TOTALI - Li ottengo dalla somma dei counter degli oggetti trasformati e versati per
	    // entrambi gli stati
	    getForm().getRiepilogoOggettiAnnullatiInCorso().getNi_ogg_annul_tot_corr()
		    .setValue(String.valueOf(countersAnnullati.get("TOTALI").get(HmKey.OGGI)));
	    getForm().getRiepilogoOggettiAnnullatiInCorso().getNi_ogg_annul_tot_7()
		    .setValue(String.valueOf(countersAnnullati.get("TOTALI").get(HmKey.ULTIMI_7)));
	    getForm().getRiepilogoOggettiAnnullatiInCorso().getNi_ogg_annul_tot_tot()
		    .setValue(String.valueOf(countersAnnullati.get("TOTALI").get(HmKey.TOT)));
	    // IN_CORSO_ANNULLAMENTO - Li ottengo dalla somma dei counter degli oggetti trasformati
	    // e versati
	    getForm().getRiepilogoOggettiAnnullatiInCorso().getNi_ogg_corso_annul_corr()
		    .setValue(String.valueOf(countersAnnullati
			    .get(Constants.StatoOggetto.IN_CORSO_ANNULLAMENTO.name())
			    .get(HmKey.OGGI)));
	    getForm().getRiepilogoOggettiAnnullatiInCorso().getNi_ogg_corso_annul_7()
		    .setValue(String.valueOf(countersAnnullati
			    .get(Constants.StatoOggetto.IN_CORSO_ANNULLAMENTO.name())
			    .get(HmKey.ULTIMI_7)));
	    getForm().getRiepilogoOggettiAnnullatiInCorso().getNi_ogg_corso_annul_tot()
		    .setValue(String.valueOf(countersAnnullati
			    .get(Constants.StatoOggetto.IN_CORSO_ANNULLAMENTO.name())
			    .get(HmKey.TOT)));
	    // ANNULLATO - Li ottengo dalla somma dei counter degli oggetti trasformati e versati
	    getForm().getRiepilogoOggettiAnnullatiInCorso().getNi_ogg_annul_corr()
		    .setValue(String.valueOf(countersAnnullati
			    .get(Constants.StatoOggetto.ANNULLATO.name()).get(HmKey.OGGI)));
	    getForm().getRiepilogoOggettiAnnullatiInCorso().getNi_ogg_annul_7()
		    .setValue(String.valueOf(countersAnnullati
			    .get(Constants.StatoOggetto.ANNULLATO.name()).get(HmKey.ULTIMI_7)));
	    getForm().getRiepilogoOggettiAnnullatiInCorso().getNi_ogg_annul_tot()
		    .setValue(String.valueOf(countersAnnullati
			    .get(Constants.StatoOggetto.ANNULLATO.name()).get(HmKey.TOT)));
	}
    }

    private void initHashMapTotali(Map<Enum, Integer>... hmList) {
	for (Map hm : hmList) {
	    hm.clear();
	    hm.put(HmKey.OGGI, 0);
	    hm.put(HmKey.ULTIMI_6, 0);
	    hm.put(HmKey.ULTIMI_7, 0);
	    hm.put(HmKey.PREC_ULTIMI_6, 0);
	    hm.put(HmKey.TOT, 0);
	}
    }

    private Map<String, Map<Enum, Integer>> calcolaTotaliOggetti(
	    AbstractBaseTable<? extends BaseRow> contaOggettiTB, String tiClasseVersFile) {
	Map<String, Map<Enum, Integer>> totaliPerStato = new HashMap<>();
	Map<Enum, Integer> tmpTot = new HashMap<>();
	initHashMapTotali(tmpTot);
	totaliPerStato.put("TOTALI", tmpTot);
	for (Constants.StatoOggetto stato : Constants.StatoOggetto.values()) {
	    Map<Enum, Integer> tmp = new HashMap<>();
	    initHashMapTotali(tmp);
	    totaliPerStato.put(stato.name(), tmp);
	}

	if (tiClasseVersFile.equals(Constants.TipoClasseVersamento.NON_DA_TRASFORMARE.name())) {
	    contaOggettiTb(contaOggettiTB, totaliPerStato, tmpTot, "ni_ogg_vers");
	} else if (tiClasseVersFile.equals(Constants.TipoClasseVersamento.DA_TRASFORMARE.name())) {
	    contaOggettiTb(contaOggettiTB, totaliPerStato, tmpTot, "ni_ogg_trasformati_vers");
	} else {
	    contaOggettiTb(contaOggettiTB, totaliPerStato, tmpTot, "ni_ogg_vers");
	}

	return totaliPerStato;
    }

    private void contaOggettiTb(AbstractBaseTable<? extends BaseRow> contaOggettiTB,
	    Map<String, Map<Enum, Integer>> totaliPerStato, Map<Enum, Integer> tmpTot,
	    String countFieldName) throws IllegalArgumentException {
	for (BaseRow rb : contaOggettiTB) {
	    Map<Enum, Integer> totaliPerGiorno = totaliPerStato
		    .get(rb.getString("ti_stato_object"));
	    if (totaliPerGiorno == null) {
		totaliPerGiorno = new HashMap<>();
		initHashMapTotali(totaliPerGiorno);
	    }
	    HmKey key;
	    switch (rb.getString("ti_dt_creazione")) {
	    case WebConstants.CORR:
		key = HmKey.OGGI;
		break;
	    case WebConstants.SEI_GG_PREC_CORR:
		key = HmKey.ULTIMI_6;
		break;
	    case WebConstants.PREC_SEI_GG_PREC_CORR:
		key = HmKey.PREC_ULTIMI_6;
		break;
	    default:
		throw new IllegalArgumentException("Valore di TiDtCreazione non valido");
	    }

	    calcolaMappaTotale(totaliPerGiorno, key, rb.getBigDecimal(countFieldName).intValue());
	    calcolaMappaTotale(tmpTot, key, rb.getBigDecimal(countFieldName).intValue());
	    totaliPerStato.put(rb.getString("ti_stato_object"), totaliPerGiorno);
	    totaliPerStato.put("TOTALI", tmpTot);
	}
    }

    private Map<String, Map<String, Map<Enum, Integer>>> calcolaTotaliFalliti(
	    MonVSesRangeDtTableBean contaFallitiTB) throws ParerUserError {
	Map<String, Map<String, Map<Enum, Integer>>> totaliPerStato = new HashMap<>();
	for (Constants.StatoOggetto stato : Constants.StatoOggetto.values()) {
	    Map<String, Map<Enum, Integer>> totaliPerStatoRisoluz = new HashMap<>();

	    Map<Enum, Integer> tmpTot = new HashMap<>();
	    initHashMapTotali(tmpTot);
	    totaliPerStatoRisoluz.put("TOTALI", tmpTot);

	    for (WebConstants.tiStatoRisoluz statoRisoluz : WebConstants.tiStatoRisoluz
		    .getStatiCalcoloRiepilogo()) {
		Map<Enum, Integer> tmp = new HashMap<>();
		initHashMapTotali(tmp);
		totaliPerStatoRisoluz.put(statoRisoluz.name(), tmp);
	    }
	    totaliPerStato.put(stato.name(), totaliPerStatoRisoluz);
	}

	for (MonVSesRangeDtRowBean rb : contaFallitiTB) {
	    String tiStatoRisoluz = rb.getTiStatoRisoluz();
	    if (tiStatoRisoluz.equals(WebConstants.tiStatoRisoluz.NON_RISOLTO.name())) {
		if (rb.getFlVerif() == null) {
		    throw new ParerUserError(
			    "Eccezione inattesa nel calcolo monitoraggio: sono presenti sessioni fallite con flag verificato nullo");
		}
		if (rb.getFlVerif() != null && rb.getFlVerif().equals("1")) {
		    if (rb.getFlNonRisolub() != null && rb.getFlNonRisolub().equals("1")) {
			tiStatoRisoluz = WebConstants.tiStatoRisoluz.NON_RISOLUBILE.name();
		    } else {
			tiStatoRisoluz = WebConstants.tiStatoRisoluz.NON_RISOLTO_VERIFICATO.name();
		    }
		} else {
		    tiStatoRisoluz = WebConstants.tiStatoRisoluz.NON_RISOLTO_NON_VERIFICATO.name();
		}
	    }

	    Map<String, Map<Enum, Integer>> totaliPerStatoRisoluz = totaliPerStato
		    .get(rb.getTiStato());
	    Map<Enum, Integer> totaliPerGiorno = totaliPerStatoRisoluz.get(tiStatoRisoluz);
	    Map<Enum, Integer> tmpTot = totaliPerStatoRisoluz.get("TOTALI");

	    HmKey key;
	    switch (rb.getTiDtCreazione()) {
	    case WebConstants.CORR:
		key = HmKey.OGGI;
		break;
	    case WebConstants.SEI_GG_PREC_CORR:
		key = HmKey.ULTIMI_6;
		break;
	    case WebConstants.PREC_SEI_GG_PREC_CORR:
		key = HmKey.PREC_ULTIMI_6;
		break;
	    default:
		throw new IllegalArgumentException("Valore di TiDtCreazione non valido");
	    }
	    calcolaMappaTotale(totaliPerGiorno, key, rb.getBigDecimal("ni_invii_fall").intValue());
	    calcolaMappaTotale(tmpTot, key, rb.getBigDecimal("ni_invii_fall").intValue());
	    totaliPerStatoRisoluz.put(tiStatoRisoluz, totaliPerGiorno);
	    totaliPerStatoRisoluz.put("TOTALI", tmpTot);
	    totaliPerStato.put(rb.getTiStato(), totaliPerStatoRisoluz);
	}

	return totaliPerStato;
    }

    private void calcolaMappaTotale(Map<Enum, Integer> totali, HmKey key, int value) {
	Integer counter = totali.get(key);
	if (counter == null) {
	    counter = value;
	} else {
	    counter += value;
	}
	totali.put(key, counter);
	totali.put(HmKey.ULTIMI_7, totali.get(HmKey.OGGI) + totali.get(HmKey.ULTIMI_6));
	totali.put(HmKey.TOT, totali.get(HmKey.ULTIMI_7) + totali.get(HmKey.PREC_ULTIMI_6));
    }

    private Integer getCountTotaliOggetti(Map<String, Map<Enum, Integer>> totali, HmKey key,
	    String... stati) {
	Integer counter = 0;
	for (String stato : stati) {
	    counter += totali.get(stato).get(key);
	}
	return counter;
    }

    private Integer getCountTotaliOggetti(Map<String, Map<String, Map<Enum, Integer>>> totali,
	    HmKey key, String statoRisoluz, String... stati) {
	Integer counter = 0;
	for (String stato : stati) {
	    counter += totali.get(stato).get(statoRisoluz).get(key);
	}
	return counter;
    }

    @Override
    public void cercaSessioniErrate() throws EMFError {
	filtraSessioniVerificate();
    }

    @Override
    public void calcolaStrutturaVersante() throws EMFError {
	throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void impostaVerificato() throws Throwable {
	getForm().getFiltriSessione().post(getRequest());
	String flaggozzo = getForm().getFiltriSessione().getFl_sessione_err_verif().parse();
	// Ottengo i riferimenti ai componenti selezionati (spuntati) dalla lista
	String[] verificati = getRequest().getParameterValues("Fl_verif");
	int totVerificati = verificati != null ? verificati.length : 0;
	Set<BigDecimal> idSessioneHS = new HashSet<>();
	// Ricavo dei valori utili al fine della memorizzazione su DB dei flag
	int riga = 0;
	int inizio = getForm().getSessioniErrateList().getTable().getFirstRowPageIndex();
	int fine = getForm().getSessioniErrateList().getTable().getFirstRowPageIndex()
		+ getForm().getSessioniErrateList().getTable().getPageSize();
	int paginaCorrente = getForm().getSessioniErrateList().getTable().getCurrentPageIndex();
	int ultimaPagina = getForm().getSessioniErrateList().getTable().getPages();

	// Se mi trovo nell'ultima pagina, setto il valore di "fine" con le dimensioni della tabella
	if (paginaCorrente == ultimaPagina) {
	    fine = getForm().getSessioniErrateList().getTable().size();
	}

	/*
	 * Ricavo i valori delle checkbox prima di eventuali modifiche su di essi e li confronto per
	 * vedere se ci sono state modifiche
	 */
	MonVLisSesErrateTableBean monSesErr = (MonVLisSesErrateTableBean) getForm()
		.getSessioniErrateList().getTable();
	String[] verificatiPre = new String[fine - inizio];
	String[] verificatiPost = new String[fine - inizio];

	// Ricavo i valori delle checkbox "verificate" prima e dopo eventuali modifiche
	int count = 0;
	for (int i = inizio; i < fine; i++) {
	    verificatiPre[count] = monSesErr.getRow(i).getFlVerif();
	    verificatiPost[count] = "0";
	    count++;
	}
	for (int j = 0; j < totVerificati; j++) {
	    if (CheckNumeric.isNumeric(verificati[j])) {
		int posizione = Integer.parseInt(verificati[j]);
		verificatiPost[posizione - inizio] = "1";
	    }
	}
	boolean modificati = false;
	for (int k = 0; k < verificatiPre.length; k++) {
	    if (!verificatiPre[k].equals(verificatiPost[k])) {
		modificati = true;
		break;
	    }
	}

	// Se ci sono state modifiche, le salvo su DB
	if (modificati) {
	    // Ottengo gli id sessione dei record "verificati" con il flag spuntato (dopo eventuale
	    // modifica)
	    if (verificati != null) {
		for (String comp : verificati) {
		    if (StringUtils.isNotBlank(comp) && CheckNumeric.isNumeric(comp)) {
			idSessioneHS.add(getForm().getSessioniErrateList().getTable()
				.getRow(Integer.parseInt(comp)).getBigDecimal(ID_SESSIONE_INGEST));
		    }
		}
	    }

	    // Risetto i flag in base alle modifiche
	    for (int i = 0; i < fine - inizio; i++) {
		riga = inizio + i;
		BigDecimal idSesErr = getForm().getSessioniErrateList().getTable().getRow(riga)
			.getBigDecimal(ID_SESSIONE_INGEST);

		if (idSessioneHS.contains(idSesErr)) {
		    // metti il flag a 1 nella tabella VrsSessioneVers
		    monitoraggioHelper.saveFlVerificati(idSesErr, "1");
		    log.debug("Ho impostato il flaggozzo 'verificata' di sessione a 1");
		} else {
		    // metti il flag a 0
		    monitoraggioHelper.saveFlVerificati(idSesErr, "0");
		    log.debug("Ho impostato il flaggozzo 'verificata' di sessione a 0");
		}
	    }

	    try {
		/*
		 * Se non avevo il filtro impostato (su sÃ¬ o no, e dunque posso restare dov'ero)
		 * rieseguo la query
		 */
		MonVLisSesErrateTableBean listaSessErr = monitoraggioHelper
			.getSessioniErrateListTB(flaggozzo, 1000);
		getForm().getSessioniErrateList().setTable(listaSessErr);
		getForm().getSessioniErrateList().getTable().setPageSize(10);
		getForm().getSessioniErrateList().getTable().first();
		// rieseguo la query se necessario
		this.lazyLoadGoPage(getForm().getSessioniErrateList(), paginaCorrente);
		// ritorno alla pagina
		getForm().getSessioniErrateList().getTable().setCurrentRowIndex(inizio);
		// Segnalo l'avvenuta impostazione dei flaggozzi andata a buon fine
		getMessageBox().addMessage(
			new Message(MessageLevel.INF, "Aggiornamento effettuato con successo"));
		getMessageBox().setViewMode(ViewMode.plain);
	    } catch (Exception e) {
		log.error("Errore nell'impostazione del flag verificato");
		getMessageBox().addMessage(new Message(MessageLevel.ERR,
			"Errore nell'impostazione del flag verificato"));
	    } finally {
		forwardToPublisher(Application.Publisher.SESSIONI_ERRATE_LIST);
	    }
	} // Altrimenti segnalo il fatto di non aver apportato modifiche sul DB
	else {
	    getMessageBox().addMessage(new Message(MessageLevel.WAR,
		    "Aggiornamento non effettuato in quanto non sono state apportate modifiche ai record"));
	    getMessageBox().setViewMode(ViewMode.plain);
	    forwardToPublisher(Application.Publisher.SESSIONI_ERRATE_LIST);
	}
    }

    /**
     * Trigger sul filtro "Sessione Errata Verificata" della pagina Lista Sessioni Errate:
     * selezionando un valore della combo box viene rieseguita la ricerca con il nuovo filtro
     * impostato
     *
     * @throws EMFError errore generico
     */
    public void filtraSessioniVerificate() throws EMFError {
	getForm().getFiltriSessione().post(getRequest());
	// Setto la lista delle sessioni fallite
	MonVLisSesErrateTableBean listaSessErr = monitoraggioHelper.getSessioniErrateListTB(
		getForm().getFiltriSessione().getFl_sessione_err_verif().parse(), 1000);
	getForm().getSessioniErrateList().setTable(listaSessErr);
	getForm().getSessioniErrateList().getTable().setPageSize(10);
	getForm().getSessioniErrateList().getTable().first();
	forwardToPublisher(Application.Publisher.SESSIONI_ERRATE_LIST);
    }

    /**
     * Metodo di inizializzazione form di Sessioni Errate
     *
     * @throws EMFError errore generico
     */
    @Secure(action = "Menu.Logging.SessioniErrate")
    public void sessioniErrate() throws EMFError {
	getUser().getMenu().reset();
	getUser().getMenu().select("Menu.Logging.SessioniErrate");
	// Preparo la combo "Verificato"
	getForm().getFiltriSessione().getFl_sessione_err_verif().reset();
	getForm().getFiltriSessione().getFl_sessione_err_verif()
		.setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());
	getForm().getFiltriSessione().getFl_sessione_err_verif().setEditMode();
	// Setto editabile il bottone di ricerca, da utilizzare in caso di javascript disattivato
	getForm().getFiltriSessione().getCercaSessioniErrate().setEditMode();
	// Ricavo la lista delle sessioni errate senza filtraggio per flag verificato
	MonVLisSesErrateTableBean listaSessErr = monitoraggioHelper.getSessioniErrateListTB(null,
		1000);
	getForm().getSessioniErrateList().setTable(listaSessErr);
	getForm().getSessioniErrateList().getTable().setPageSize(10);
	getForm().getSessioniErrateList().getTable().first();

	/*
	 * Rendo visibile il bottone per impostare la verifica della sessione e quello per calcolare
	 * la struttura versante se la lista delle sessioni errate non Ã¨ vuota
	 */
	if (listaSessErr.size() != 0) {
	    getForm().getSalvaVerificaButtonList().getImpostaVerificato().setEditMode();

	} else {
	    getForm().getSalvaVerificaButtonList().getImpostaVerificato().setViewMode();

	}
	// Eseguo il forward alla stessa pagina
	forwardToPublisher(Application.Publisher.SESSIONI_ERRATE_LIST);
    }

    @Secure(action = "Menu.Monitoraggio.SchedulazioniJob")
    public void schedulazioniJob() {
	getUser().getMenu().reset();
	getUser().getMenu().select("Menu.Monitoraggio.SchedulazioniJob");
	getForm().getFiltriJobSchedulati().reset();
	getForm().getJobSchedulatiList().setTable(null);
	getForm().getInformazioniJob().reset();
	populateFiltriJob();
	getForm().getFiltriJobSchedulati().setEditMode();
	// Resetto i valori delle label
	getForm().getInformazioniJob().reset();
	getForm().getInformazioniJob().setViewMode();
	getForm().getInformazioniJob().post(getRequest());
	forwardToPublisher(Application.Publisher.SCHEDULAZIONI_JOB_LIST);
    }

    /**
     * Metodo invocato quando viene cliccato uno dei totali della pagina "Riepilogo Versamenti" e
     * che mi rimanda alla pagina con la relativa lista
     *
     * @throws EMFError errore generico
     */
    public void monitoraggioListe() throws EMFError {
	// Mi creo i bean locali per gestire i filtri
	MonitoraggioFiltriListaOggettiBean filtriListaOggetti = new MonitoraggioFiltriListaOggettiBean();
	MonitoraggioFiltriListaVersFallitiBean filtriListaVersFalliti = new MonitoraggioFiltriListaVersFallitiBean();
	MonitoraggioFiltriListaOggDerVersFallitiBean filtriListaOggDerVersFalliti = new MonitoraggioFiltriListaOggDerVersFallitiBean();

	/*
	 * Azzero i filtri delle pagine "Lista Oggetti" e "Lista Versamenti" che sono quelle alle
	 * quali posso arrivare
	 */
	getForm().getFiltriOggetti().reset();
	getForm().getFiltriVersamenti().reset();
	getForm().getFiltriOggettiDerVersFalliti().reset();

	/*
	 * Setto i filtri "generici" della pagina nella quale sto per essere ridirezionato per non
	 * eseguire i controlli due volte
	 */
	if (getForm().getFiltriRiepilogoVersamenti().getId_ambiente_vers().getValue() != null
		&& !getForm().getFiltriRiepilogoVersamenti().getId_ambiente_vers().getValue()
			.equals("")) {
	    filtriListaOggetti.setIdAmbienteVers(new BigDecimal(
		    getForm().getFiltriRiepilogoVersamenti().getId_ambiente_vers().getValue()));
	    filtriListaVersFalliti.setIdAmbienteVers(new BigDecimal(
		    getForm().getFiltriRiepilogoVersamenti().getId_ambiente_vers().getValue()));
	    filtriListaOggDerVersFalliti.setIdAmbienteVers(new BigDecimal(
		    getForm().getFiltriRiepilogoVersamenti().getId_ambiente_vers().getValue()));
	}
	if (getForm().getFiltriRiepilogoVersamenti().getId_vers().getValue() != null
		&& !getForm().getFiltriRiepilogoVersamenti().getId_vers().getValue().equals("")) {
	    filtriListaOggetti.setIdVers(new BigDecimal(
		    getForm().getFiltriRiepilogoVersamenti().getId_vers().getValue()));
	    filtriListaVersFalliti.setIdVers(new BigDecimal(
		    getForm().getFiltriRiepilogoVersamenti().getId_vers().getValue()));
	    filtriListaOggDerVersFalliti.setIdVers(new BigDecimal(
		    getForm().getFiltriRiepilogoVersamenti().getId_vers().getValue()));
	}
	if (getForm().getFiltriRiepilogoVersamenti().getId_tipo_object().getValue() != null
		&& !getForm().getFiltriRiepilogoVersamenti().getId_tipo_object().getValue()
			.equals("")) {
	    filtriListaOggetti.setIdTipoObject(new BigDecimal(
		    getForm().getFiltriRiepilogoVersamenti().getId_tipo_object().getValue()));
	    filtriListaVersFalliti.setIdTipoObject(new BigDecimal(
		    getForm().getFiltriRiepilogoVersamenti().getId_tipo_object().getValue()));
	    filtriListaVersFalliti.setNmTipoObject(
		    getForm().getFiltriRiepilogoVersamenti().getId_tipo_object().getDecodedValue());
	    filtriListaOggDerVersFalliti.setIdTipoObject(new BigDecimal(
		    getForm().getFiltriRiepilogoVersamenti().getId_tipo_object().getValue()));
	    filtriListaOggDerVersFalliti.setNmTipoObject(
		    getForm().getFiltriRiepilogoVersamenti().getId_tipo_object().getDecodedValue());
	}

	// MEV 26979
	if (getForm().getFiltriRiepilogoVersamenti().getId_object().getValue() != null
		&& !getForm().getFiltriRiepilogoVersamenti().getId_object().getValue().equals("")) {
	    filtriListaOggetti.setIdObject(new BigDecimal(
		    getForm().getFiltriRiepilogoVersamenti().getId_object().getValue()));
	    filtriListaVersFalliti.setIdObject(new BigDecimal(
		    getForm().getFiltriRiepilogoVersamenti().getId_object().getValue()));
	    filtriListaOggDerVersFalliti.setIdObject(new BigDecimal(
		    getForm().getFiltriRiepilogoVersamenti().getId_object().getValue()));
	}
	if (getForm().getFiltriRiepilogoVersamenti().getCd_key_object().getValue() != null
		&& !getForm().getFiltriRiepilogoVersamenti().getCd_key_object().getValue()
			.equals("")) {
	    filtriListaOggetti.setChiave(
		    getForm().getFiltriRiepilogoVersamenti().getCd_key_object().getValue());
	    filtriListaVersFalliti.setChiave(
		    getForm().getFiltriRiepilogoVersamenti().getCd_key_object().getValue());
	    filtriListaOggDerVersFalliti.setChiave(
		    getForm().getFiltriRiepilogoVersamenti().getCd_key_object().getValue());
	}

	// Inizializzo le combo settando la struttura corrente
	ComboBox ambienteVersCombo = getForm().getFiltriRiepilogoVersamenti().getId_ambiente_vers();
	DecodeMapIF mappaAmbienteVers = ambienteVersCombo.getDecodeMap();

	ComboBox versCombo = getForm().getFiltriRiepilogoVersamenti().getId_vers();
	DecodeMapIF mappaVers = versCombo.getDecodeMap();

	ComboBox tipoObjectCombo = getForm().getFiltriRiepilogoVersamenti().getId_tipo_object();
	DecodeMapIF mappaTipoObject = tipoObjectCombo.getDecodeMap();

	// MOSTRA LISTA OGGETTI
	if (getRequest().getParameter("pagina")
		.equals(WebConstants.vistaListaRiepilogoVersamenti.OBJ_RANGE_DT.name())) {
	    // Setto diversi valori dei filtri presi come parametri passati dalla request
	    filtriListaOggetti.setPeriodoVers(getRequest().getParameter("periodo"));
	    final String classeVers = getRequest().getParameter("classeVers");
	    List<String> valoriList = new ArrayList<>();
	    if (getRequest().getParameter("tiStato") != null) {
		valoriList.addAll(Arrays.asList(getRequest().getParameterValues("tiStato")));
	    } else {
		Constants.StatoOggetto[] statiOggettoMonitoraggioListaOggetti = Constants.StatoOggetto
			.getStatiOggettoMonitoraggioListaOggetti();
		if (classeVers != null) {
		    if (classeVers
			    .equals(Constants.TipoClasseVersamento.NON_DA_TRASFORMARE.name())) {
			statiOggettoMonitoraggioListaOggetti = Constants.StatoOggetto
				.getStatiNoTrasformazione();
		    } else {
			statiOggettoMonitoraggioListaOggetti = Constants.StatoOggetto
				.getStatiTrasformazione();
		    }
		}

		for (Constants.StatoOggetto stato : statiOggettoMonitoraggioListaOggetti) {
		    valoriList.add(stato.name());
		}
	    }
	    filtriListaOggetti.setStatoObject(valoriList);

	    if (classeVers != null) {
		if (classeVers.equals(Constants.TipoClasseVersamento.NON_DA_TRASFORMARE.name())) {
		    filtriListaOggetti
			    .setTiVersFile(Arrays.asList(Constants.TipoVersamento.NO_ZIP.name(),
				    Constants.TipoVersamento.ZIP_CON_XML_SACER.name(),
				    Constants.TipoVersamento.ZIP_NO_XML_SACER.name()));
		} else {
		    filtriListaOggetti.setTiVersFile(
			    Arrays.asList(Constants.TipoVersamento.DA_TRASFORMARE.name()));
		}
	    }
	    // Salvo in sessione i filtri
	    getSession().setAttribute("filtriListaOggetti", filtriListaOggetti);
	    // Setto la lista oggetti
	    getForm().getOggettiList()
		    .setTable(monitoraggioHelper
			    .getMonVLisObjViewBean((MonitoraggioFiltriListaOggettiBean) getSession()
				    .getAttribute("filtriListaOggetti")));

	    getForm().getOggettiList().getTable().setPageSize(10);
	    getForm().getOggettiList().setUserOperations(true, true, false, false);
	    // Workaround in modo che la lista punti al primo record, non all'ultimo
	    getForm().getOggettiList().getTable().first();
	    getForm().getFiltriOggetti().setEditMode();

	    getForm().getFiltriOggetti().getId_ambiente_vers().setDecodeMap(mappaAmbienteVers);
	    getForm().getFiltriOggetti().getId_ambiente_vers().setValue(
		    getForm().getFiltriRiepilogoVersamenti().getId_ambiente_vers().getValue());

	    getForm().getFiltriOggetti().getId_vers().setDecodeMap(mappaVers);
	    getForm().getFiltriOggetti().getId_vers()
		    .setValue(getForm().getFiltriRiepilogoVersamenti().getId_vers().getValue());

	    getForm().getFiltriOggetti().getId_tipo_object().setDecodeMap(mappaTipoObject);
	    getForm().getFiltriOggetti().getId_tipo_object().setValue(
		    getForm().getFiltriRiepilogoVersamenti().getId_tipo_object().getValue());

	    // MEV 26979
	    getForm().getFiltriOggetti().getId_object()
		    .setValue(getForm().getFiltriRiepilogoVersamenti().getId_object().getValue());
	    getForm().getFiltriOggetti().getFiltri_oggetti_cd_key_object().setValue(
		    getForm().getFiltriRiepilogoVersamenti().getCd_key_object().getValue());

	    // Preparo la combo "periodo versamento"
	    getForm().getFiltriOggetti().getPeriodo_vers().setDecodeMap(getMappaPeriodoVers());
	    getForm().getFiltriOggetti().getPeriodo_vers()
		    .setValue(getRequest().getParameter("periodo"));

	    // Preparo la multiselect "stato oggetto"
	    getForm().getFiltriOggetti().getTi_stato().setDecodeMap(getMappaStatoOggetto());
	    getForm().getFiltriOggetti().getTi_stato()
		    .setValues((valoriList.toArray(new String[valoriList.size()])));
	    getForm().getFiltriOggetti().getTi_vers_file().setDecodeMap(ComboGetter
		    .getMappaSortedGenericEnum("ti_vers_file", Constants.TipoVersamento.values()));
	    getForm().getFiltriOggetti().getTi_vers_file()
		    .setValues(filtriListaOggetti.getTiVersFile() != null
			    ? filtriListaOggetti.getTiVersFile()
				    .toArray(new String[filtriListaOggetti.getTiVersFile().size()])
			    : Arrays.asList(Constants.TipoVersamento.DA_TRASFORMARE.name(),
				    Constants.TipoVersamento.NO_ZIP.name(),
				    Constants.TipoVersamento.ZIP_CON_XML_SACER.name(),
				    Constants.TipoVersamento.ZIP_NO_XML_SACER.name())
				    .toArray(new String[Constants.TipoVersamento.values().length]));
	    forwardToPublisher(Application.Publisher.OGGETTI_LIST);
	} // MOSTRA LISTA VERSAMENTI FALLITI
	else if (getRequest().getParameter("pagina")
		.equals(WebConstants.vistaListaRiepilogoVersamenti.SES_RANGE_DT.name())) {
	    try {
		// Setto diversi valori dei filtri presi come parametri passati dalla request
		// Parametro TIPO ERRORE (INVIO_OGGETTO, NOTIFICA_FILE, PREPARAZIONE_XML,
		// REGISTRAZIONE_IN_CODA,
		// VERSAMENTO_SACER)
		if (getRequest().getParameter("tiStato") != null) {
		    filtriListaVersFalliti
			    .setStati(Arrays.asList(getRequest().getParameterValues("tiStato")));
		}
		// Parametro STATO RISOLUZIONE
		if (getRequest().getParameter("tiStatoRisoluz") != null) {
		    filtriListaVersFalliti
			    .setStatoRisoluzione(getRequest().getParameter("tiStatoRisoluz"));
		}
		// Parametro PERIODO
		filtriListaVersFalliti.setPeriodoVers(getRequest().getParameter("periodo"));

		// Parametro VERIFICATI
		if (getRequest().getParameter("flVerif") != null) {
		    filtriListaVersFalliti.setVerificati(getRequest().getParameter("flVerif"));
		}
		// Parametro NON RISOLUBILI
		if (getRequest().getParameter("flNonRisolub") != null) {
		    filtriListaVersFalliti
			    .setNonRisolubili(getRequest().getParameter("flNonRisolub"));
		}

		// Salvo i filtri in sessione
		getSession().setAttribute("filtriListaVersFalliti", filtriListaVersFalliti);
		List<String> stati = filtriListaVersFalliti.getStati();
		boolean showButton = true;
		// NB. Se vengono cambiati gli stati forse questo controllo va cambiato
		if (stati.contains(Constants.StatoOggetto.CHIUSO_ERR_TRASFORMAZIONE.name())
			|| stati.contains(Constants.StatoOggetto.CHIUSO_ERR_VERIFICA_HASH.name())
			|| stati.contains(
				Constants.StatoOggetto.CHIUSO_ERR_VERSAMENTO_A_PING.name())) {
		    // Se il filtro contiene questi due stati, non posso visualizzare il pulsante
		    // imposta verificati
		    showButton = false;
		}
		// Setto la lista dei versamenti falliti
		MonVLisVersFallitiTableBean versFallitiTableBean = monitoraggioHelper
			.getMonVLisVersFallitiViewBean(
				(MonitoraggioFiltriListaVersFallitiBean) getSession()
					.getAttribute("filtriListaVersFalliti"));
		getForm().getVersamentiList().setTable(versFallitiTableBean);
		getForm().getVersamentiList().getTable().setPageSize(10);
		getForm().getVersamentiList().setUserOperations(true, false, false, false);
		// Workaround in modo che la lista punti al primo record, non all'ultimo
		getForm().getVersamentiList().getTable().first();
		// Imposto editabili tutti i filtri
		getForm().getFiltriVersamenti().setEditMode();
		getForm().getVersamentiButtonList().getRicercaVersamentiFalliti().setEditMode();

		getForm().getFiltriVersamenti().getId_ambiente_vers()
			.setDecodeMap(mappaAmbienteVers);
		getForm().getFiltriVersamenti().getId_ambiente_vers().setValue(
			getForm().getFiltriRiepilogoVersamenti().getId_ambiente_vers().getValue());

		getForm().getFiltriVersamenti().getId_vers().setDecodeMap(mappaVers);
		getForm().getFiltriVersamenti().getId_vers()
			.setValue(getForm().getFiltriRiepilogoVersamenti().getId_vers().getValue());

		getForm().getFiltriVersamenti().getId_tipo_object().setDecodeMap(mappaTipoObject);
		getForm().getFiltriVersamenti().getId_tipo_object().setValue(
			getForm().getFiltriRiepilogoVersamenti().getId_tipo_object().getValue());

		// Preparo la combo "Tipo stato"
		getForm().getFiltriVersamenti().getVersamento_ti_stato()
			.setDecodeMap(ComboGetter.getMappaSortedGenericEnum("tiStato",
				Constants.StatoSessioneIngest.getStatiVersamentiFalliti()));
		getForm().getFiltriVersamenti().getVersamento_ti_stato()
			.setValues(getRequest().getParameterValues("tiStato"));
		getForm().getFiltriVersamenti().getVersamento_ti_stato().setEditMode();

		// Preparo la combo "StatoOggetto risoluzione"
		getForm().getFiltriVersamenti().getVersamento_ti_stato_risoluz()
			.setDecodeMap(getMappaStatoRisoluzione());
		getForm().getFiltriVersamenti().getVersamento_ti_stato_risoluz()
			.setValue(getRequest().getParameter("tiStatoRisoluz"));
		getForm().getFiltriVersamenti().getVersamento_ti_stato_risoluz().setEditMode();

		// Preparo la combo "Periodo versamento"
		getForm().getFiltriVersamenti().getPeriodo_vers()
			.setDecodeMap(getMappaPeriodoVers());
		getForm().getFiltriVersamenti().getPeriodo_vers()
			.setValue(getRequest().getParameter("periodo"));
		getForm().getFiltriVersamenti().getPeriodo_vers().setEditMode();

		// Preparo la combo "Classe errore"
		PigClasseErroreTableBean table = messaggiEjb.getPigClasseErroreTableBean(
			getForm().getFiltriVersamenti().getVersamento_ti_stato().parse());
		getForm().getFiltriVersamenti().getClasse_errore().setDecodeMap(DecodeMap.Factory
			.newInstance(table, "cd_classe_errore", "ds_classe_composita"));
		getForm().getFiltriVersamenti().getClasse_errore().setEditMode();

		// Preparo la combo "Classe errore"
		getForm().getFiltriVersamenti().getCd_err().setDecodeMap(new DecodeMap());
		getForm().getFiltriVersamenti().getCd_err().setEditMode();

		/*
		 * Metto inizialmente inmette in edit mode le checkbox Verificati e Non Risolubile
		 */
		getForm().getVersamentiList().getFl_verif().setReadonly(false);
		getForm().getVersamentiList().getFl_non_risolub().setReadonly(false);

		// preparo la combo "Verificato"
		getForm().getFiltriVersamenti().getVersamento_fl_verif()
			.setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());
		getForm().getFiltriVersamenti().getVersamento_fl_verif().setEditMode();

		if (getRequest().getParameter("flVerif") != null) {
		    if (getRequest().getParameter("flVerif").equals("1")) {
			getForm().getFiltriVersamenti().getVersamento_fl_verif().setValue("1");
		    } else {
			getForm().getFiltriVersamenti().getVersamento_fl_verif().setValue("0");
		    }
		}

		// Preparo la combo "Non risolubile"
		getForm().getFiltriVersamenti().getVersamento_fl_non_risolub()
			.setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());
		getForm().getFiltriVersamenti().getVersamento_fl_non_risolub().setEditMode();

		if (getRequest().getParameter("flNonRisolub") != null) {
		    if (getRequest().getParameter("flNonRisolub").equals("1")) {
			getForm().getFiltriVersamenti().getVersamento_fl_non_risolub()
				.setValue("1");
		    } else {
			getForm().getFiltriVersamenti().getVersamento_fl_non_risolub()
				.setValue("0");
		    }
		}

		/*
		 * Rendo visibile il bottone per impostare la verifica del versamenti se la lista
		 * dei versamenti falliti non Ã¨ vuota
		 */
		if (versFallitiTableBean.size() != 0 && showButton) {
		    getForm().getVersamentiButtonList().getImpostaVerificatoNonRisolubile()
			    .setEditMode();
		    getForm().getVersamentiButtonList().getImpostaTuttiVerificato().setEditMode();
		    getForm().getVersamentiButtonList().getImpostaTuttiNonRisolubile()
			    .setEditMode();
		} else {
		    getForm().getVersamentiButtonList().getImpostaVerificatoNonRisolubile()
			    .setViewMode();
		    getForm().getVersamentiButtonList().getImpostaTuttiVerificato().setViewMode();
		    getForm().getVersamentiButtonList().getImpostaTuttiNonRisolubile()
			    .setViewMode();
		}
		forwardToPublisher(Application.Publisher.VERSAMENTI_LIST);
	    } catch (ParerUserError ex) {
		getMessageBox().addError(ex.getDescription());
		forwardToPublisher(getLastPublisher());
	    }
	    // MOSTRA LISTA OGGETTI DERIVANTI DA VERSAMENTI FALLITI
	} else if (getRequest().getParameter("pagina")
		.equals(WebConstants.vistaListaRiepilogoVersamenti.OBJ_NON_VERS.name())) {
	    // Setto diversi valori dei filtri presi come parametri passati dalla request
	    // Parametro VERIFICATI
	    if (getRequest().getParameter("flVerif") != null) {
		filtriListaOggDerVersFalliti.setVerificati(getRequest().getParameter("flVerif"));
	    }
	    // Parametro NON RISOLUBILI
	    if (getRequest().getParameter("flNonRisolub") != null) {
		filtriListaOggDerVersFalliti
			.setNonRisolubili(getRequest().getParameter("flNonRisolub"));
	    }

	    // Salvo i filtri in sessione
	    getSession().setAttribute("filtriListaOggDerVersFalliti", filtriListaOggDerVersFalliti);
	    // Setto la lista degli oggetti derivanti da versamenti falliti
	    MonVLisObjNonVersTableBean objNonVersTableBean = monitoraggioHelper
		    .getMonVLisObjNonVersViewBean(
			    (MonitoraggioFiltriListaOggDerVersFallitiBean) getSession()
				    .getAttribute("filtriListaOggDerVersFalliti"));
	    getForm().getOggettiDaVersamentiFallitiList().setTable(objNonVersTableBean);
	    getForm().getOggettiDaVersamentiFallitiList().getTable().setPageSize(10);
	    getForm().getOggettiDaVersamentiFallitiList().setUserOperations(true, false, false,
		    false);
	    // Workaround in modo che la lista punti al primo record, non all'ultimo
	    getForm().getOggettiDaVersamentiFallitiList().getTable().first();
	    // Imposto editabili tutti i filtri
	    getForm().getFiltriOggettiDerVersFalliti().setEditMode();
	    getForm().getFiltriOggettiDerVersFalliti().getId_ambiente_vers()
		    .setDecodeMap(mappaAmbienteVers);
	    getForm().getFiltriOggettiDerVersFalliti().getId_ambiente_vers().setValue(
		    getForm().getFiltriRiepilogoVersamenti().getId_ambiente_vers().getValue());

	    getForm().getFiltriOggettiDerVersFalliti().getId_vers().setDecodeMap(mappaVers);
	    getForm().getFiltriOggettiDerVersFalliti().getId_vers()
		    .setValue(getForm().getFiltriRiepilogoVersamenti().getId_vers().getValue());

	    getForm().getFiltriOggettiDerVersFalliti().getId_tipo_object()
		    .setDecodeMap(mappaTipoObject);
	    getForm().getFiltriOggettiDerVersFalliti().getId_tipo_object().setValue(
		    getForm().getFiltriRiepilogoVersamenti().getId_tipo_object().getValue());

	    /*
	     * Metto inizialmente inmette in edit mode le checkbox Verificati e Non Risolubile
	     */
	    getForm().getFiltriOggettiDerVersFalliti().getOggetti_der_fl_verif().setReadonly(false);
	    getForm().getFiltriOggettiDerVersFalliti().getOggetti_der_fl_non_risolub()
		    .setReadonly(false);
	    /* Metto inizialmente in view mode la combo "da recuperare" */
	    getForm().getFiltriOggettiDerVersFalliti().getOggetti_der_fl_non_risolub()
		    .setViewMode();

	    // Preparo la combo "Verificato"
	    getForm().getFiltriOggettiDerVersFalliti().getOggetti_der_fl_verif()
		    .setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());
	    getForm().getFiltriOggettiDerVersFalliti().getOggetti_der_fl_verif().setEditMode();

	    if (getRequest().getParameter("flVerif") != null) {
		if (getRequest().getParameter("flVerif").equals("1")) {
		    getForm().getFiltriOggettiDerVersFalliti().getOggetti_der_fl_verif()
			    .setValue("1");
		} else {
		    getForm().getFiltriOggettiDerVersFalliti().getOggetti_der_fl_verif()
			    .setValue("0");
		}
	    }

	    // Preparo la combo "Non risolubile"
	    getForm().getFiltriOggettiDerVersFalliti().getOggetti_der_fl_non_risolub()
		    .setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());
	    getForm().getFiltriOggettiDerVersFalliti().getOggetti_der_fl_non_risolub()
		    .setEditMode();

	    if (getRequest().getParameter("flNonRisolub") != null) {
		if (getRequest().getParameter("flNonRisolub").equals("1")) {
		    getForm().getFiltriOggettiDerVersFalliti().getOggetti_der_fl_non_risolub()
			    .setValue("1");
		} else {
		    getForm().getFiltriOggettiDerVersFalliti().getOggetti_der_fl_non_risolub()
			    .setValue("0");
		}
	    }

	    // Preparo la combo "Da recuperare a SACER": puÃ² essere impostato solo se la combo dei
	    // verificati Ã¨
	    // settata
	    // a true
	    // e quella dei non risolubili Ã¨ settata a false
	    getForm().getFiltriOggettiDerVersFalliti().getOggetti_der_fl_vers_sacer_da_recup()
		    .setEditMode();
	    if ((getRequest().getParameter("flVerif") != null
		    && (getRequest().getParameter("flVerif").equals("1")
			    || getRequest().getParameter("flVerif").equals("0")))
		    && (getRequest().getParameter("flNonRisolub") == null)) {
		getForm().getFiltriOggettiDerVersFalliti().getOggetti_der_fl_vers_sacer_da_recup()
			.setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());
	    } else {
		getForm().getFiltriOggettiDerVersFalliti().getOggetti_der_fl_vers_sacer_da_recup()
			.setDecodeMap(new DecodeMap());
	    }

	    // Rendo visibile il bottone di "Ricerca"
	    getForm().getOggettiDerVersFallitiButtonList().getRicercaOggettiDaVersamentiFalliti()
		    .setEditMode();

	    forwardToPublisher(Application.Publisher.OGGETTI_DA_VERSAMENTI_FALLITI_LIST);
	}
    }

    private String calcInfoObj(String xmlDati) {
	String info = null;
	if (xmlDati != null) {
	    String nmPaz = "NON DEFINITO";
	    String dataNascita = "NON DEFINITO";
	    String dataStudio = "NON DEFINITO";

	    int nmPazStart = xmlDati.indexOf("<PatientName>");
	    int nmPazStop = xmlDati.indexOf("</PatientName>");
	    int dataNascitaStart = xmlDati.indexOf("<PatientBirthDate>");
	    int dataNascitaStop = xmlDati.indexOf("</PatientBirthDate>");
	    int dataStudioStart = xmlDati.indexOf("<StudyDate>");
	    int dataStudioStop = xmlDati.indexOf("</StudyDate>");

	    if (nmPazStart != -1 && nmPazStop != -1) {
		nmPaz = xmlDati.substring(nmPazStart + ("<PatientName>").length(), nmPazStop);
	    }
	    if (dataNascitaStart != -1 && dataNascitaStop != -1) {
		dataNascita = xmlDati.substring(dataNascitaStart + ("<PatientBirthDate>").length(),
			dataNascitaStop);
	    }
	    if (dataStudioStart != -1 && dataStudioStop != -1) {
		dataStudio = xmlDati.substring(dataStudioStart + ("<StudyDate>").length(),
			dataStudioStop);
	    }
	    info = "Paziente " + nmPaz + " nato il " + dataNascita + " Studio del " + dataStudio;
	}
	return info;
    }

    /**
     * Metodo attivato alla pressione del tasto relativo al download dei file xml nel dettaglio di
     * una sessione errata
     * <p>
     * * @throws EMFError errore generico
     */
    @Override
    public void downloadXmlSessione() throws EMFError {
	BigDecimal idSessioneVers = ((MonVLisSesErrateRowBean) getForm().getSessioniErrateList()
		.getTable().getCurrentRow()).getIdSessioneIngest();
	// Comincio a costruire lo zip che conterrÃ  i file
	String nomeZip = "vers_" + idSessioneVers;
	getResponse().setContentType("application/zip");
	getResponse().setHeader("Content-Disposition",
		"attachment; filename=\"" + nomeZip + ".zip");

	String filename = "";
	ZipOutputStream out = new ZipOutputStream(getServletOutputStream());
	byte[] blobXml = null;
	if (getForm().getSessioniErrateDetail().getBl_xml().getValue().length() < 1000000000) {
	    blobXml = getForm().getSessioniErrateDetail().getBl_xml().getValue().getBytes();
	} else {
	    blobXml = monitoraggioHelper.getXmlSesErr(idSessioneVers);
	}

	try {
	    filename = "vers_" + idSessioneVers + ".xml";
	    zipBlob(out, filename, blobXml);
	    out.flush();
	    out.close();
	    freeze();
	} catch (Exception e) {
	    getMessageBox().addMessage(
		    new Message(MessageLevel.ERR, "Errore nel recupero dei file da zippare"));
	    log.error(ECCEZIONE_MSG, e);
	}
    }

    /**
     * Metodo addetto alla compressione nel file zip del byte array contenente il file originale
     *
     * @param out      outputstream {@link ZipOutputStream}
     * @param filename nome file
     * @param blob     contenuto file
     *
     * @throws EMFError    errore genrico
     * @throws IOException errore generico di tipo IO
     */
    public void zipBlob(ZipOutputStream out, String filename, byte[] blob)
	    throws EMFError, IOException {
	// Ricavo lo stream di input
	InputStream is = new ByteArrayInputStream(blob);
	byte[] data = new byte[1024];
	int count;
	out.putNextEntry(new ZipEntry(filename));
	while ((count = is.read(data, 0, 1024)) != -1) {
	    out.write(data, 0, count);
	}
	out.closeEntry();
	is.close();
    }

    @Override
    public JSONObject triggerFiltriOggettiId_ambiente_versOnTrigger() throws EMFError {
	triggerAmbienteVersatoreGenerico(getForm().getFiltriOggetti(),
		WebConstants.SezioneMonitoraggio.RIEPILOGO_VERSAMENTI);
	/* Gestisco il campo cd_key_object */
	if (StringUtils.isNotBlank(getForm().getFiltriOggetti().getId_ambiente_vers().getValue())
		&& StringUtils.isNotBlank(getForm().getFiltriOggetti().getId_vers().getValue())) {
	    getForm().getFiltriOggetti().getFiltri_oggetti_cd_key_object().setHidden(false);
	} else {
	    getForm().getFiltriOggetti().getFiltri_oggetti_cd_key_object().setHidden(true);
	}
	return getForm().getFiltriOggetti().asJSON();
    }

    @Override
    public JSONObject triggerFiltriOggettiId_versOnTrigger() throws EMFError {
	triggerVersatoreGenerico(getForm().getFiltriOggetti());
	/* Gestisco il campo cd_key_object */
	if (StringUtils.isNotBlank(getForm().getFiltriOggetti().getId_ambiente_vers().getValue())
		&& StringUtils.isNotBlank(getForm().getFiltriOggetti().getId_vers().getValue())) {
	    getForm().getFiltriOggetti().getFiltri_oggetti_cd_key_object().setHidden(false);
	} else {
	    getForm().getFiltriOggetti().getFiltri_oggetti_cd_key_object().setHidden(true);
	}
	return getForm().getFiltriOggetti().asJSON();
    }

    @Override
    public void ricercaOggetti() throws EMFError {
	MonitoraggioForm.FiltriOggetti filtri = getForm().getFiltriOggetti();
	// Esegue la post dei filtri compilati
	filtri.post(getRequest());
	// Valida i filtri per verificare quelli obbligatori
	if (filtri.validate(getMessageBox())) {
	    // Aggiorno i filtri in sessione
	    MonitoraggioFiltriListaOggettiBean filtriListaOggetti = (MonitoraggioFiltriListaOggettiBean) getSession()
		    .getAttribute("filtriListaOggetti");
	    filtriListaOggetti.setIdAmbienteVers(filtri.getId_ambiente_vers().parse());
	    filtriListaOggetti.setIdVers(filtri.getId_vers().parse());
	    filtriListaOggetti.setIdTipoObject(filtri.getId_tipo_object().parse());
	    filtriListaOggetti.setPeriodoVers(filtri.getPeriodo_vers().parse());
	    filtriListaOggetti.setGiornoVersDa(filtri.getGiorno_vers_da().parse());
	    filtriListaOggetti.setOreVersDa(filtri.getOre_vers_da().parse());
	    filtriListaOggetti.setMinutiVersDa(filtri.getMinuti_vers_da().parse());
	    filtriListaOggetti.setGiornoVersA(filtri.getGiorno_vers_a().parse());
	    filtriListaOggetti.setOreVersA(filtri.getOre_vers_a().parse());
	    filtriListaOggetti.setMinutiVersA(filtri.getMinuti_vers_a().parse());
	    filtriListaOggetti.setStatoObject(filtri.getTi_stato().parse());
	    filtriListaOggetti.setRegistro(filtri.getCd_registro_unita_doc_sacer().parse());
	    filtriListaOggetti.setAnno(filtri.getAa_unita_doc_sacer().parse());
	    filtriListaOggetti.setCodice(filtri.getCd_key_unita_doc_sacer().parse());
	    filtriListaOggetti.setIdObject(filtri.getId_object().parse());
	    filtriListaOggetti.setChiave(filtri.getFiltri_oggetti_cd_key_object().parse());
	    filtriListaOggetti.setTiVersFile(filtri.getTi_vers_file().parse());

	    getSession().setAttribute("filtriListaOggetti", filtriListaOggetti);

	    /*
	     * Controllo su campi periodo versamento e giorno versamento: solo uno dei due puÃ²
	     * essere valorizzato
	     */
	    MonitoraggioValidator validator = new MonitoraggioValidator(getMessageBox());
	    // Prima validazione: controllo che siano stati compilati o l'uno (periodo versamento) o
	    // gli altri (range
	    // giorno versamento)
	    validator.validaSceltaPeriodoGiornoVersamento(filtri.getPeriodo_vers().parse(),
		    filtri.getGiorno_vers_da().parse(), filtri.getOre_vers_da().parse(),
		    filtri.getMinuti_vers_da().parse(), filtri.getGiorno_vers_a().parse(),
		    filtri.getOre_vers_a().parse(), filtri.getMinuti_vers_a().parse());

	    // Seconda validazione: controllo che il range di giorno versamento sia corretto e setto
	    // gli eventuali
	    // valori di default
	    Date[] dateValidate = validator.validaDate(filtri.getGiorno_vers_da().parse(),
		    filtri.getOre_vers_da().parse(), filtri.getMinuti_vers_da().parse(),
		    filtri.getGiorno_vers_a().parse(), filtri.getOre_vers_a().parse(),
		    filtri.getMinuti_vers_a().parse(),
		    filtri.getGiorno_vers_da().getHtmlDescription(),
		    filtri.getGiorno_vers_a().getHtmlDescription());

	    if (!getMessageBox().hasError()) {
		// Le eventuali date riferite al giorno di versamento vengono salvate in sessione
		if (dateValidate != null) {
		    filtriListaOggetti.setGiornoVersDaValidato(dateValidate[0]);
		    filtriListaOggetti.setGiornoVersAValidato(dateValidate[1]);
		    getSession().setAttribute("filtriListaOggetti", filtriListaOggetti);
		}

		// La validazione non ha riportato errori. Carico la tabella con i filtri impostati
		getForm().getOggettiList()
			.setTable(monitoraggioHelper.getMonVLisObjViewBean(
				(MonitoraggioFiltriListaOggettiBean) getSession()
					.getAttribute("filtriListaOggetti")));
		getForm().getOggettiList().getTable().setPageSize(10);
		// Workaround in modo che la lista punti al primo record, non all'ultimo
		getForm().getOggettiList().getTable().first();
	    }
	}
	forwardToPublisher(Application.Publisher.OGGETTI_LIST);
    }

    private void salvaSessioneErrataFlVerificato() {
	SessioniErrateDetail sesDetail = getForm().getSessioniErrateDetail();
	sesDetail.post(getRequest());

	MonVLisSesErrateRowBean session = ((MonVLisSesErrateRowBean) getForm()
		.getSessioniErrateList().getTable().getCurrentRow());
	BigDecimal idSes = session.getIdSessioneIngest();

	if (getRequest().getParameterMap().get("Fl_verif") != null) {
	    monitoraggioHelper.saveFlVerificati(idSes, "1");
	    sesDetail.getFl_verif().setChecked(true);
	} else {
	    monitoraggioHelper.saveFlVerificati(idSes, "0");
	}

	getForm().getSessioniErrateDetail().setStatus(Status.view);
	getForm().getSessioniErrateDetail().getFl_verif().setViewMode();
	getForm().getSessioniErrateList().setStatus(Status.view);

	getMessageBox().addInfo("Salvataggio sessione effettuato con successo");
	getMessageBox().setViewMode(MessageBox.ViewMode.plain);

	forwardToPublisher(Application.Publisher.SESSIONE_ERRATA_DETAIL);
    }

    @Override
    public void ricercaJobSchedulati() throws EMFError {
	getForm().getFiltriJobSchedulati().getRicercaJobSchedulati().setDisableHourGlass(true);
	FiltriJobSchedulati filtri = getForm().getFiltriJobSchedulati();

	// Esegue la post dei filtri compilati
	if (getSession().getAttribute(FROM_GESTIONE_JOB) != null) {
	    getSession().removeAttribute(FROM_GESTIONE_JOB);
	} else {
	    filtri.post(getRequest());
	}

	// Valida i filtri per verificare quelli obbligatori
	if (filtri.validate(getMessageBox())) {
	    // Valida in maniera piÃ¹ specifica i dati
	    String nmJob = filtri.getNm_job().parse();
	    Date datada = filtri.getDt_reg_log_job_da().parse();
	    Date dataa = filtri.getDt_reg_log_job_a().parse();
	    BigDecimal oreda = filtri.getOre_dt_reg_log_job_da().parse();
	    BigDecimal orea = filtri.getOre_dt_reg_log_job_a().parse();
	    BigDecimal minutida = filtri.getMinuti_dt_reg_log_job_da().parse();
	    BigDecimal minutia = filtri.getMinuti_dt_reg_log_job_a().parse();
	    String descrizioneDataDa = filtri.getDt_reg_log_job_da().getHtmlDescription();
	    String descrizioneDataA = filtri.getDt_reg_log_job_a().getHtmlDescription();

	    // Valida i campi di ricerca
	    MonitoraggioValidator validator = new MonitoraggioValidator(getMessageBox());
	    Date[] dateValidate = validator.validaDate(datada, oreda, minutida, dataa, orea,
		    minutia, descrizioneDataDa, descrizioneDataA);

	    if (!getMessageBox().hasError()) {
		// Setta la lista dei job in base ai filtri di ricerca
		getForm().getJobSchedulatiList().setTable(
			monitoraggioHelper.getMonVLisSchedJobViewBean(filtri, dateValidate));

		getForm().getJobSchedulatiList().getTable().setPageSize(10);
		// Workaround in modo che la lista punti al primo record, non all'ultimo
		getForm().getJobSchedulatiList().getTable().first();

		// Setto i campi di "Stato Job"
		setStatoJob(nmJob);
	    }
	}
	forwardToPublisher(Application.Publisher.SCHEDULAZIONI_JOB_LIST);
    }

    @Override
    public void tabDettaglioOggettoOnClick() throws EMFError {
	getForm().getOggettoTabs().setCurrentTab(getForm().getOggettoTabs().getDettaglioOggetto());
	forwardToPublisher(Application.Publisher.OGGETTO_DETAIL);
    }

    @Override
    public void tabOggettoXMLVersatoOnClick() throws EMFError {
	getForm().getOggettoTabs().setCurrentTab(getForm().getOggettoTabs().getOggettoXMLVersato());
	forwardToPublisher(Application.Publisher.OGGETTO_DETAIL);
    }

    @Override
    public void tabStudioDICOMOnClick() throws EMFError {
	getForm().getOggettoTabs().setCurrentTab(getForm().getOggettoTabs().getStudioDICOM());
	forwardToPublisher(Application.Publisher.OGGETTO_DETAIL);
    }

    @Override
    public void tabListaOggettiOnClick() throws EMFError {
	getForm().getOggettoSubTabs()
		.setCurrentTab(getForm().getOggettoSubTabs().getListaOggetti());
	forwardToPublisher(Application.Publisher.OGGETTO_DETAIL);
    }

    @Override
    public void tabListaUnitaDocOnClick() throws EMFError {
	getForm().getOggettoSubTabs()
		.setCurrentTab(getForm().getOggettoSubTabs().getListaUnitaDoc());
	forwardToPublisher(Application.Publisher.OGGETTO_DETAIL);
    }

    @Override
    public void tabListaOggettiTrasfOnClick() throws EMFError {
	getForm().getOggettoSubTabs()
		.setCurrentTab(getForm().getOggettoSubTabs().getListaOggettiTrasf());
	forwardToPublisher(Application.Publisher.OGGETTO_DETAIL);
    }

    @Override
    public void tabListaFileOnClick() throws EMFError {
	getForm().getOggettoSubTabs().setCurrentTab(getForm().getOggettoSubTabs().getListaFile());
	forwardToPublisher(Application.Publisher.OGGETTO_DETAIL);
    }

    @Override
    public void tabListaVersamentiOnClick() throws EMFError {
	getForm().getOggettoSubTabs()
		.setCurrentTab(getForm().getOggettoSubTabs().getListaVersamenti());
	forwardToPublisher(Application.Publisher.OGGETTO_DETAIL);
    }

    @Override
    public void tabListaStatiVersamentiOnClick() throws EMFError {
	getForm().getOggettoSubTabs()
		.setCurrentTab(getForm().getOggettoSubTabs().getListaStatiVersamenti());
	forwardToPublisher(Application.Publisher.OGGETTO_DETAIL);
    }

    @Override
    public void tabListaPrioritaVersamentoOnClick() throws EMFError {
	getForm().getOggettoSubTabs()
		.setCurrentTab(getForm().getOggettoSubTabs().getListaPrioritaVersamento());
	forwardToPublisher(Application.Publisher.OGGETTO_DETAIL);
    }

    @Override
    public void tabXMLRichAnnulOnClick() throws EMFError {
	getForm().getOggettoTabs().setCurrentTab(getForm().getOggettoTabs().getXMLRichAnnul());
	forwardToPublisher(Application.Publisher.OGGETTO_DETAIL);
    }

    @Override
    public void tabXMLRispAnnulOnClick() throws EMFError {
	getForm().getOggettoTabs().setCurrentTab(getForm().getOggettoTabs().getXMLRispAnnul());
	forwardToPublisher(Application.Publisher.OGGETTO_DETAIL);
    }

    @Override
    public void tabFiltriUnitaDocObjOnClick() throws EMFError {
	getForm().getOggettoTabs().setCurrentTab(getForm().getOggettoTabs().getFiltriUnitaDocObj());
	forwardToPublisher(Application.Publisher.OGGETTO_DETAIL);
    }

    @Override
    public void tabReportTrasformazioneOnClick() throws EMFError {
	getForm().getOggettoTabs()
		.setCurrentTab(getForm().getOggettoTabs().getReportTrasformazione());
	forwardToPublisher(Application.Publisher.OGGETTO_DETAIL);
    }

    @Override
    public void tabReportTrasformazionePerVersamnetoOnClick() throws EMFError {
	getForm().getVersamentoTabs().setCurrentTab(
		getForm().getVersamentoTabs().getReportTrasformazionePerVersamneto());
	forwardToPublisher(Application.Publisher.VERSAMENTO_DETAIL);
    }

    @Override
    public void pulisciJobSchedulati() throws EMFError {
	try {
	    this.schedulazioniJob();
	} catch (Exception ex) {
	    log.error(ECCEZIONE_MSG, ex);
	}
    }

    @Override
    public void downloadXMLOggetto() throws EMFError {
	BigDecimal idObject = getForm().getOggettoDetail().getId_object().parse();
	String nomeFile = "vers_oggetto_" + idObject;
	getResponse().setContentType(ContentType.APPLICATION_XML.getMimeType());
	getResponse().setHeader("Content-Disposition",
		"attachment; filename=\"" + nomeFile + ".xml");
	try {
	    // Ricavo lo stream di output
	    BufferedOutputStream out = new BufferedOutputStream(getServletOutputStream());
	    // Caccio il blobbo nel file xml
	    byte[] xmlOggetto = getForm().getOggettoDetail().getBl_xml().getValue().getBytes();
	    if (xmlOggetto != null && xmlOggetto.length > 0) {
		InputStream is = new ByteArrayInputStream(xmlOggetto);
		byte[] data = new byte[1024];
		int count;
		while ((count = is.read(data, 0, 1024)) != -1) {
		    out.write(data, 0, count);
		}
	    }
	    out.flush();
	    out.close();
	    freeze();
	} catch (Exception e) {
	    getMessageBox().addMessage(
		    new Message(MessageLevel.ERR, "Errore nel recupero dei file da zippare"));
	    log.error(ECCEZIONE_MSG, e);
	}
    }

    @Override
    public void downloadXMLUnitaDocObject() throws EMFError {
	String chiaveUd = getForm().getUnitaDocDetail().getChiave_ud().parse();
	//
	// MAC#28503 - Modifica del pulsante download xml unità documentaria nella pagina DETTAGLIO
	// UNITA' DOCUMENTARIA
	//
	// Ottengo l'id unita doc e lo salvo in getSession()
	BigDecimal idUnitaDocObject = getForm().getOggettoDetailUnitaDocList().getTable()
		.getCurrentRow().getBigDecimal("id_unita_doc_object");
	// Carico il rowbean del dettaglio unità  doc.
	MonVVisUnitaDocObjectRowBean objRB = monitoraggioHelper
		.getMonVVisUnitaDocObjectRowBean(idUnitaDocObject);
	getForm().getUnitaDocDetail().copyFromBean(objRB);
	String xmlvers = objRB.getBlXmlVersSacer();
	String xmlindice = objRB.getBlXmlIndiceSacer();
	XmlPrettyPrintFormatter formatter = new XmlPrettyPrintFormatter();
	if (xmlvers != null) {
	    xmlvers = formatter.prettyPrintWithDOM3LS(xmlvers);
	}
	if (xmlindice != null) {
	    xmlindice = formatter.prettyPrintWithDOM3LS(xmlindice);
	}

	downloadXMLUnitaDoc(chiaveUd, xmlvers.getBytes(StandardCharsets.UTF_8),
		xmlindice.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public void downloadXMLUnitaDocVers() throws EMFError {
	String chiaveUd = getForm().getUnitaDocVersamentoDetail().getChiave_ud().parse();
	byte[] xmlVers = getForm().getUnitaDocVersamentoDetail().getBl_xml_vers_sacer().getValue()
		.getBytes();
	byte[] xmlIndice = getForm().getUnitaDocVersamentoDetail().getBl_xml_indice_sacer()
		.getValue().getBytes();
	downloadXMLUnitaDoc(chiaveUd, xmlVers, xmlIndice);
    }

    public void downloadXMLUnitaDoc(String chiaveUd, byte[] xmlVersamento, byte[] xmlIndice)
	    throws EMFError {
	// Comincio a costruire lo zippone che conterrÃ  i file
	String fileNamePart = chiaveUd.replaceAll(" ", "");
	String nomeZippone = fileNamePart + "_preingest";
	getResponse().setContentType("application/zip");
	getResponse().setHeader("Content-Disposition",
		"attachment; filename=\"" + nomeZippone + ".zip");
	// Ricavo lo stream di output
	ZipOutputStream out = new ZipOutputStream(getServletOutputStream());
	String filename = "";
	try {
	    // Caccio dentro lo zippone il file xml di versamento
	    if (xmlVersamento != null && xmlVersamento.length > 0) {
		filename = "vers_ud.xml";
		zipBlob(out, filename, xmlVersamento);
	    }
	    // Caccio dentro lo zippone il file xml di indice
	    if (xmlIndice != null && xmlIndice.length > 0) {
		filename = "indice_file_ud.xml";
		zipBlob(out, filename, xmlIndice);
	    }
	    out.flush();
	    out.close();
	    freeze();
	} catch (Exception e) {
	    getMessageBox().addMessage(new Message(MessageLevel.ERR,
		    "Errore nel recupero dei file da comprimere per il download"));
	    log.error(ECCEZIONE_MSG, e);
	    forwardToPublisher(getLastPublisher());
	}
    }

    @Override
    public JSONObject triggerFiltriVersamentiId_ambiente_versOnTrigger() throws EMFError {
	triggerAmbienteVersatoreGenerico(getForm().getFiltriVersamenti(),
		WebConstants.SezioneMonitoraggio.FILTRI_VERSAMENTI);
	return getForm().getFiltriVersamenti().asJSON();
    }

    @Override
    public JSONObject triggerFiltriVersamentiId_versOnTrigger() throws EMFError {
	triggerVersatoreGenerico(getForm().getFiltriVersamenti());
	return getForm().getFiltriVersamenti().asJSON();
    }

    @Override
    public void ricercaVersamentiFalliti() throws Throwable {
	MonitoraggioForm.FiltriVersamenti filtri = getForm().getFiltriVersamenti();
	// Esegue la post dei filtri compilati
	filtri.post(getRequest());
	// Recupero i filtri dalla sessione e setto i filtri con il nuovo valore
	MonitoraggioFiltriListaVersFallitiBean filtriListaVersFalliti = (MonitoraggioFiltriListaVersFallitiBean) getSession()
		.getAttribute("filtriListaVersFalliti");
	filtriListaVersFalliti.setIdAmbienteVers(filtri.getId_ambiente_vers().parse());
	filtriListaVersFalliti.setIdVers(filtri.getId_vers().parse());
	filtriListaVersFalliti.setIdTipoObject(filtri.getId_tipo_object().parse());
	filtriListaVersFalliti.setNmTipoObject(filtri.getId_tipo_object().parse() != null
		? filtri.getId_tipo_object().getDecodedValue()
		: null);
	filtriListaVersFalliti.setStatoRisoluzione(filtri.getVersamento_ti_stato_risoluz().parse());
	filtriListaVersFalliti.setPeriodoVers(filtri.getPeriodo_vers().parse());
	filtriListaVersFalliti.setGiornoVersDa(filtri.getGiorno_vers_da().parse());
	filtriListaVersFalliti.setOreVersDa(filtri.getOre_vers_da().parse());
	filtriListaVersFalliti.setMinutiVersDa(filtri.getMinuti_vers_da().parse());
	filtriListaVersFalliti.setGiornoVersA(filtri.getGiorno_vers_a().parse());
	filtriListaVersFalliti.setOreVersA(filtri.getOre_vers_a().parse());
	filtriListaVersFalliti.setMinutiVersA(filtri.getMinuti_vers_a().parse());
	filtriListaVersFalliti.setErrore(filtri.getCd_err().parse());
	filtriListaVersFalliti.setClasseErrore(filtri.getClasse_errore().parse());
	filtriListaVersFalliti.setVerificati(filtri.getVersamento_fl_verif().parse());
	filtriListaVersFalliti.setNonRisolubili(filtri.getVersamento_fl_non_risolub().parse());
	filtriListaVersFalliti.setGiornoVersDaValidato(null);
	filtriListaVersFalliti.setGiornoVersAValidato(null);

	getSession().setAttribute("filtriListaVersFalliti", filtriListaVersFalliti);

	/*
	 * Controllo su campi periodo versamento e giorno versamento: solo uno dei due puÃ² essere
	 * valorizzato
	 */
	MonitoraggioValidator validator = new MonitoraggioValidator(getMessageBox());
	// Prima validazione: controllo che siano stati compilati o l'uno (periodo versamento) o gli
	// altri (range giorno
	// versamento)
	validator.validaSceltaPeriodoGiornoVersamento(filtri.getPeriodo_vers().parse(),
		filtri.getGiorno_vers_da().parse(), filtri.getOre_vers_da().parse(),
		filtri.getMinuti_vers_da().parse(), filtri.getGiorno_vers_a().parse(),
		filtri.getOre_vers_a().parse(), filtri.getMinuti_vers_a().parse());

	// Seconda validazione: controllo che il range di giorno versamento sia corretto e setto gli
	// eventuali valori di
	// default
	Date[] dateValidate = validator.validaDate(filtri.getGiorno_vers_da().parse(),
		filtri.getOre_vers_da().parse(), filtri.getMinuti_vers_da().parse(),
		filtri.getGiorno_vers_a().parse(), filtri.getOre_vers_a().parse(),
		filtri.getMinuti_vers_a().parse(), filtri.getGiorno_vers_da().getHtmlDescription(),
		filtri.getGiorno_vers_a().getHtmlDescription());

	// Valida i filtri per verificare quelli obbligatori
	if (filtri.validate(getMessageBox())) {
	    if (!getMessageBox().hasError()) {
		// Le eventuali date riferite al giorno di versamento vengono salvate in sessione
		if (dateValidate != null) {
		    filtriListaVersFalliti.setGiornoVersDaValidato(dateValidate[0]);
		    filtriListaVersFalliti.setGiornoVersAValidato(dateValidate[1]);
		    getSession().setAttribute("filtriListaVersFalliti", filtriListaVersFalliti);
		}

		// Setto la lista dei versamenti falliti
		getForm().getVersamentiList()
			.setTable(monitoraggioHelper.getMonVLisVersFallitiViewBean(
				(MonitoraggioFiltriListaVersFallitiBean) getSession()
					.getAttribute("filtriListaVersFalliti")));
		getForm().getVersamentiList().getTable().setPageSize(10);
		getForm().getVersamentiList().setUserOperations(true, false, false, false);
		// Workaround in modo che la lista punti al primo record, non all'ultimo
		getForm().getVersamentiList().getTable().first();
	    }

	    /*
	     * Metto inizialmente inmette in edit mode le checkbox Verificati e Non Risolubile
	     */
	    getForm().getVersamentiList().getFl_verif().setReadonly(false);
	    getForm().getVersamentiList().getFl_non_risolub().setReadonly(false);
	}
	forwardToPublisher(Application.Publisher.VERSAMENTI_LIST);
    }

    /**
     * Salva su DB i valori dei flag "Verificato" e "Non risolubile" impostati nella lista dei
     * versamenti falliti. Verifica le condizioni di coerenza tra i due flag e con il flag
     * "Versamento a SACER da recuperare
     *
     * @throws Throwable errore generico
     */
    @Override
    public void impostaVerificatoNonRisolubile() throws Throwable {
	getForm().getFiltriVersamenti().post(getRequest());
	String flaggozzo = getForm().getFiltriVersamenti().getVersamento_fl_verif().parse();
	/*
	 * Ottengo l'indice dei componenti selezionati del campo "Verificato" dalla lista risultato
	 */
	String[] indiceAssolutoVerificatiSettati = getRequest().getParameterValues("Fl_verif");
	/*
	 * Ottengo l'indice dei componenti selezionati del campo "Non risolubile" dalla lista
	 * risultato
	 */
	String[] indiceAssolutoNonRisolubiliSettati = getRequest()
		.getParameterValues("Fl_non_risolub");

	int totVerificati = indiceAssolutoVerificatiSettati != null
		? indiceAssolutoVerificatiSettati.length
		: 0;
	int totNonRisolubili = indiceAssolutoNonRisolubiliSettati != null
		? indiceAssolutoNonRisolubiliSettati.length
		: 0;

	/* Ricavo dei valori utili al fine della memorizzazione su DB dei flag */
	int paginaCorrente = getForm().getVersamentiList().getTable().getCurrentPageIndex();
	int inizio = getForm().getVersamentiList().getTable().getFirstRowPageIndex();
	int fine = getForm().getVersamentiList().getTable().getFirstRowPageIndex()
		+ getForm().getVersamentiList().getTable().getPageSize();
	int ultimaPagina = getForm().getVersamentiList().getTable().getPages();

	/*
	 * Se mi trovo nell'ultima pagina, setto il valore di "fine" con le dimensioni della tabella
	 */
	if (paginaCorrente == ultimaPagina) {
	    fine = getForm().getVersamentiList().getTable().size();
	}

	////////////////////////////////////////////////////////////////////////////
	///// Ricavo i valori delle checkbox "Verificati" e "Non Risolubili" ///////
	///// PRIMA e DOPO eventuali modifiche. ////////////////////////////////////
	///// Quindi li confronto per vedere se ci sono state modifiche ////////////
	////////////////////////////////////////////////////////////////////////////
	int numRecordPerPagina = fine - inizio;
	MonVLisVersFallitiTableBean monVersErr = (MonVLisVersFallitiTableBean) getForm()
		.getVersamentiList().getTable();
	String[] verificatiPre = new String[numRecordPerPagina];
	String[] verificatiPost = new String[numRecordPerPagina];
	String[] nonRisolubiliPre = new String[numRecordPerPagina];
	String[] nonRisolubiliPost = new String[numRecordPerPagina];
	String[] statoRisoluzione = new String[numRecordPerPagina];
	/*
	 * Contiene gli indici assoluti dei record in cui sono stati modificati il flag "Verificato"
	 * o il flag "Non Risolubile" o entrambi
	 */
	Set<Integer> verificatiNonRisolubiliModificati = new HashSet();

	/* 1) Inserisco tutti i valori dei PRE e tutti "0" nei POST */
	int count = 0;
	for (int i = inizio; i < fine; i++) {
	    verificatiPre[count] = monVersErr.getRow(i).getFlVerif() != null
		    ? monVersErr.getRow(i).getFlVerif()
		    : "0";
	    nonRisolubiliPre[count] = monVersErr.getRow(i).getFlNonRisolub() != null
		    ? monVersErr.getRow(i).getFlNonRisolub()
		    : "0";
	    verificatiPost[count] = "0";
	    nonRisolubiliPost[count] = "0";
	    statoRisoluzione[count] = monVersErr.getRow(i).getTiStatoRisoluz();
	    count++;
	}

	/* 2) Ora nei POST metto gli "1" dove vanno messi */
	for (int j = 0; j < totVerificati; j++) {
	    if (CheckNumeric.isNumeric(indiceAssolutoVerificatiSettati[j])) {
		int posizione = Integer.parseInt(indiceAssolutoVerificatiSettati[j]);
		verificatiPost[posizione - inizio] = "1";
	    }
	}

	/*
	 * 3) Ora che ho i record prima e dopo le eventuali modifiche, ricavo gli indici dei record
	 * "Verificati" modificati
	 */
	for (int k = 0; k < verificatiPre.length; k++) {
	    if (!verificatiPre[k].equals(verificatiPost[k])) {
		BigDecimal idSessioneIngest = getForm().getVersamentiList().getTable()
			.getRow(k + inizio).getBigDecimal(ID_SESSIONE_INGEST);
		if (monitoraggioEjb.checkSessionObjectDaVerif(idSessioneIngest, true)) {
		    verificatiNonRisolubiliModificati.add(k + inizio);
		}
	    }
	}

	/* Idem con patate per i "Non risolubili" */
	for (int j = 0; j < totNonRisolubili; j++) {
	    if (CheckNumeric.isNumeric(indiceAssolutoNonRisolubiliSettati[j])) {
		int posizione = Integer.parseInt(indiceAssolutoNonRisolubiliSettati[j]);
		nonRisolubiliPost[posizione - inizio] = "1";
	    }
	}

	/* Idem con patate per i "Non risolubili" */
	for (int k = 0; k < nonRisolubiliPre.length; k++) {
	    if (!nonRisolubiliPre[k].equals(nonRisolubiliPost[k])) {
		BigDecimal idSessioneIngest = getForm().getVersamentiList().getTable()
			.getRow(k + inizio).getBigDecimal(ID_SESSIONE_INGEST);
		if (monitoraggioEjb.checkSessionObjectDaVerif(idSessioneIngest, true)) {
		    verificatiNonRisolubiliModificati.add(k + inizio);
		}
	    }
	}

	int effettivamenteModificati = 0;
	for (int integer : verificatiNonRisolubiliModificati) {
	    BigDecimal idSessioneIngest = getForm().getVersamentiList().getTable().getRow(integer)
		    .getBigDecimal(ID_SESSIONE_INGEST);
	    // Punto 6 dell'analisi
	    if (monitoraggioEjb.canModifyVerificataNonRisolubile(idSessioneIngest)) {
		effettivamenteModificati++;
		boolean flagVerificato = verificatiPost[integer - inizio].equals(Constants.DB_TRUE);
		boolean flagNonRisolubile = nonRisolubiliPost[integer - inizio]
			.equals(Constants.DB_TRUE);
		// punto a) dell'analisi
		String errore = monitoraggioEjb.impostaFlagVerificatoNonRisolubile(idSessioneIngest,
			flagVerificato, flagNonRisolubile);
		if (errore != null) {
		    getMessageBox().addError(errore);
		    break;
		}
	    }
	}

	/*
	 * CONTROLLI DI COERENZA SOLO SUGLI ELEMENTI MODIFICATI 1) Tra i "Verificati" e "Non
	 * Risolubili 2) Tra i "Verificati" e "Non Risolubili" con il flag del versamento a SACER da
	 * recuperare
	 */
	if (!getMessageBox().hasError()) {
	    /* Se ci sono state modifiche, le salvo su DB */
	    if (!verificatiNonRisolubiliModificati.isEmpty()) {
		// Ottengo gli id sessione dei record "verificati" MODIFICATI
		try {
		    /*
		     * Aggiorno i filtri in sessione e rifaccio la query tenendo conto del filtro
		     * flaggozzo
		     */
		    MonitoraggioFiltriListaVersFallitiBean filtriListaVersFalliti = (MonitoraggioFiltriListaVersFallitiBean) getSession()
			    .getAttribute("filtriListaVersFalliti");
		    filtriListaVersFalliti.setVerificati(flaggozzo);
		    int pageSize = getForm().getVersamentiList().getTable().getPageSize();
		    getSession().setAttribute("filtriListaVersFalliti", filtriListaVersFalliti);
		    MonVLisVersFallitiTableBean listaVersErr = monitoraggioHelper
			    .getMonVLisVersFallitiViewBean(
				    (MonitoraggioFiltriListaVersFallitiBean) getSession()
					    .getAttribute("filtriListaVersFalliti"));
		    getForm().getVersamentiList().setTable(listaVersErr);
		    getForm().getVersamentiList().getTable().setPageSize(pageSize);
		    getForm().getVersamentiList().getTable().first();
		    // rieseguo la query se necessario
		    this.lazyLoadGoPage(getForm().getVersamentiList(), paginaCorrente);
		    // ritorno alla pagina
		    getForm().getVersamentiList().getTable().setCurrentRowIndex(inizio);
		    refreshLista(getForm().getVersamentiList(), listaVersErr);

		    // Rieseguo anche la query di Riepilogo Versamenti
		    calcolaTotaliRiepilogoVersamenti(filtriListaVersFalliti.getIdAmbienteVers(),
			    filtriListaVersFalliti.getIdVers(),
			    filtriListaVersFalliti.getIdTipoObject());

		    // Segnalo l'avvenuta impostazione dei flaggozzi andata a buon fine solo se ne
		    // ho salvato almeno uno
		    if (effettivamenteModificati > 0) {
			getMessageBox().addMessage(new Message(MessageLevel.INF,
				"Aggiornamento effettuato con successo"));
		    }
		    getMessageBox().setViewMode(ViewMode.plain);
		} catch (ParerUserError e) {
		    getMessageBox().addMessage(new Message(MessageLevel.ERR, e.getDescription()));
		} finally {
		    forwardToPublisher(Application.Publisher.VERSAMENTI_LIST);
		}
	    } // Altrimenti segnalo il fatto di non aver apportato modifiche sul DB
	    else {
		getMessageBox().addMessage(new Message(MessageLevel.WAR,
			"Aggiornamento non effettuato in quanto non sono state apportate modifiche ai record"));
		getMessageBox().setViewMode(ViewMode.plain);
		forwardToPublisher(Application.Publisher.VERSAMENTI_LIST);
	    }
	}
	forwardToPublisher(Application.Publisher.VERSAMENTI_LIST);
    }

    /**
     * Data un lista col relativo tablebean che la riempia, viene ricaricato il contenuto sulla base
     * delle precedenti infortmazioni di paginazione (numero di record visualizzati per pagina,
     * pagina in cui mi trovavo...)
     *
     * @param lista lista elementi di tipo {@link SingleValueField}
     * @param tb    tabella
     *
     * @throws EMFError errore generico
     */
    public void refreshLista(it.eng.spagoLite.form.list.List<SingleValueField<?>> lista,
	    AbstractBaseTable tb) throws EMFError {
	int paginaCorrente = lista.getTable().getCurrentPageIndex();
	int inizio = lista.getTable().getFirstRowPageIndex();
	int pageSize = lista.getTable().getPageSize();
	lista.setTable(tb);
	lista.getTable().setPageSize(pageSize);
	lista.getTable().first();
	// rieseguo la query se necessario
	this.lazyLoadGoPage(lista, paginaCorrente);
	// ritorno alla pagina
	lista.getTable().setCurrentRowIndex(inizio);
    }

    @Override
    public void tabDettaglioVersamentoOnClick() throws EMFError {
	getForm().getVersamentoTabs()
		.setCurrentTab(getForm().getVersamentoTabs().getDettaglioVersamento());
	forwardToPublisher(Application.Publisher.VERSAMENTO_DETAIL);
    }

    @Override
    public void tabVersamentoXMLVersatoOnClick() throws EMFError {
	getForm().getVersamentoTabs()
		.setCurrentTab(getForm().getVersamentoTabs().getVersamentoXMLVersato());
	forwardToPublisher(Application.Publisher.VERSAMENTO_DETAIL);
    }

    @Override
    public void tabFiltriUnitaDocVersOnClick() throws EMFError {
	getForm().getVersamentoTabs()
		.setCurrentTab(getForm().getVersamentoTabs().getFiltriUnitaDocVers());
	forwardToPublisher(Application.Publisher.VERSAMENTO_DETAIL);
    }

    @Override
    public void tabUnitaDocListTabOnClick() throws EMFError {
	getForm().getVersamentoSubTabs()
		.setCurrentTab(getForm().getVersamentoSubTabs().getUnitaDocListTab());
	forwardToPublisher(Application.Publisher.VERSAMENTO_DETAIL);
    }

    @Override
    public void tabStatiListTabOnClick() throws EMFError {
	getForm().getVersamentoSubTabs()
		.setCurrentTab(getForm().getVersamentoSubTabs().getStatiListTab());
	forwardToPublisher(Application.Publisher.VERSAMENTO_DETAIL);
    }

    @Override
    public void downloadXMLVersamento() throws EMFError {
	BigDecimal idSessioneIngest = getForm().getVersamentoDetail().getId_sessione_ingest()
		.parse();
	String nomeFile = "vers_sessione_" + idSessioneIngest;
	getResponse().setContentType(ContentType.APPLICATION_XML.getMimeType());
	getResponse().setHeader("Content-Disposition",
		"attachment; filename=\"" + nomeFile + ".xml");
	try {
	    // Ricavo lo stream di output
	    BufferedOutputStream out = new BufferedOutputStream(getServletOutputStream());
	    // Caccio il blobbo nel file xml
	    byte[] xmlVersamento = null;
	    if (getForm().getVersamentoDetail().getBl_xml().getValue().length() < 1000000000) {
		xmlVersamento = getForm().getVersamentoDetail().getBl_xml().getValue().getBytes();
	    } else {
		xmlVersamento = monitoraggioHelper.getXmlVersErr(idSessioneIngest);
	    }
	    if (xmlVersamento != null && xmlVersamento.length > 0) {
		InputStream is = new ByteArrayInputStream(xmlVersamento);
		byte[] data = new byte[1024];
		int count;
		while ((count = is.read(data, 0, 1024)) != -1) {
		    out.write(data, 0, count);
		}
	    }
	    out.flush();
	    out.close();
	    freeze();
	} catch (Exception e) {
	    getMessageBox().addMessage(
		    new Message(MessageLevel.ERR, "Errore nel recupero dei file da zippare"));
	    log.error(ECCEZIONE_MSG, e);
	}
    }

    /**
     * Verifica se l'oggetto derivante dal versamento fallito è da recuperare: se lo è, verifica la
     * coerenza con i flag verificato e nonRisolubile
     *
     * @param idSessioneIngest id sessione versamento
     * @param verificato       verifica
     * @param nonRisolubile    non risolubile
     *
     * @throws EMFError errore generico
     */
    public void checkIsOggettoDaRecuperare(BigDecimal idSessioneIngest, String verificato,
	    String nonRisolubile) throws EMFError {
	/* Ricava il versamento fallito e l'oggetto derivante da esso */
	PigSessioneIngestRowBean pigSessioneIngestRB = monitoraggioHelper
		.getPigSessioneIngestRowBean(idSessioneIngest);
	PigObjectRowBean pigObjectRB = monitoraggioHelper.getPigObjectRowBean(
		pigSessioneIngestRB.getIdVers(), pigSessioneIngestRB.getCdKeyObject());
	/*
	 * E controlla se l'oggetto Ã¨ da recuperare (Versamenti a SACER da recuperare) considerando
	 * i flag verificato e non risolubile dati in ingresso
	 */
	boolean flVersSacerDaRecupIsChecked = pigObjectRB.getFlVersSacerDaRecup() != null
		&& pigObjectRB.getFlVersSacerDaRecup().equals("1");
	if (flVersSacerDaRecupIsChecked && verificato.equals("0")) {
	    getMessageBox().addMessage(new Message(MessageLevel.ERR,
		    "Non \u00E8 possibile resettare l'indicazione verificato per la sessione "
			    + idSessioneIngest + ", "
			    + "perchÃ¨ il relativo oggetto ha l'indicazione che il versamento a SACER deve essere recuperato"));
	}
	if (flVersSacerDaRecupIsChecked && nonRisolubile != null && nonRisolubile.equals("1")) {
	    getMessageBox().addMessage(new Message(MessageLevel.ERR,
		    "Non \u00E8 possibile settare l'indicazione di non risolubile per la sessione "
			    + idSessioneIngest + ", "
			    + "perch\u00E9 il relativo oggetto ha l'indicazione che il versamento a SACER deve essere recuperato"));
	}
    }

    @Override
    public void dettaglioOggetto() throws EMFError {
	dettaglioOggettoDaVersamento();
	forwardToPublisher(Application.Publisher.OGGETTO_DETAIL);
    }

    public void dettaglioOggettoDaVersamento() throws EMFError {
	// Ottengo l'id oggetto da versamento
	BigDecimal idObject = getForm().getVersamentoDetail().getId_object().parse();
	try {
	    if (idObject != null) {
		List<BigDecimal> idObjStack = getIdObjStack();
		idObjStack.add(idObject);
		getSession().setAttribute(GET_ID_OBJ_STACK, idObjStack);
		getSession().setAttribute(ID_OBJECT, idObject);

		loadDettaglioObject(idObject);
		forwardToPublisher(Application.Publisher.OGGETTO_DETAIL);
	    } else {
		getMessageBox().addError("Errore inatteso nella visualizzazione dell'oggetto");
		forwardToPublisher(getLastPublisher());
	    }
	} catch (ParerUserError ex) {
	    getMessageBox().addError(ex.getDescription());
	    forwardToPublisher(getLastPublisher());
	}
    }

    @Override
    public JSONObject triggerFiltriOggettiDerVersFallitiId_ambiente_versOnTrigger()
	    throws EMFError {
	triggerAmbienteVersatoreGenerico(getForm().getFiltriOggettiDerVersFalliti(),
		WebConstants.SezioneMonitoraggio.RIEPILOGO_VERSAMENTI);
	return getForm().getFiltriOggettiDerVersFalliti().asJSON();
    }

    @Override
    public JSONObject triggerFiltriOggettiDerVersFallitiId_versOnTrigger() throws EMFError {
	triggerVersatoreGenerico(getForm().getFiltriOggettiDerVersFalliti());
	return getForm().getFiltriOggettiDerVersFalliti().asJSON();
    }

    @Override
    public void ricercaOggettiDaVersamentiFalliti() throws Throwable {
	MonitoraggioForm.FiltriOggettiDerVersFalliti filtri = getForm()
		.getFiltriOggettiDerVersFalliti();
	filtri.post(getRequest());
	// Aggiorno i filtri in sessione
	MonitoraggioFiltriListaOggDerVersFallitiBean filtriListaOggDerVersFalliti = (MonitoraggioFiltriListaOggDerVersFallitiBean) getSession()
		.getAttribute("filtriListaOggDerVersFalliti");
	filtriListaOggDerVersFalliti.setIdAmbienteVers(filtri.getId_ambiente_vers().parse());
	filtriListaOggDerVersFalliti.setIdVers(filtri.getId_vers().parse());
	filtriListaOggDerVersFalliti.setIdTipoObject(filtri.getId_tipo_object().parse());
	filtriListaOggDerVersFalliti.setNmTipoObject(filtri.getId_tipo_object().parse() != null
		? filtri.getId_tipo_object().getDecodedValue()
		: null);
	filtriListaOggDerVersFalliti.setVerificati(filtri.getOggetti_der_fl_verif().parse());
	filtriListaOggDerVersFalliti
		.setNonRisolubili(filtri.getOggetti_der_fl_non_risolub().parse());
	filtriListaOggDerVersFalliti
		.setDaRecuperare(filtri.getOggetti_der_fl_vers_sacer_da_recup().parse());
	getSession().setAttribute("filtriListaOggDerVersFalliti", filtriListaOggDerVersFalliti);
	getForm().getOggettiDaVersamentiFallitiList()
		.setTable(monitoraggioHelper.getMonVLisObjNonVersViewBean(
			(MonitoraggioFiltriListaOggDerVersFallitiBean) getSession()
				.getAttribute("filtriListaOggDerVersFalliti")));
	getForm().getOggettiDaVersamentiFallitiList().getTable().setPageSize(10);
	// Workaround in modo che la lista punti al primo record, non all'ultimo
	getForm().getOggettiDaVersamentiFallitiList().getTable().first();
	forwardToPublisher(Application.Publisher.OGGETTI_DA_VERSAMENTI_FALLITI_LIST);
    }

    // MEV 26715
    @Override
    public void impostaVerificNonRisolubOggettiDaVersFalliti() throws Throwable {
	if (!getForm().getOggettiDaVersamentiFallitiList().getStatus().equals(Status.update)) {
	    /* Ottengo i componenti selezionati del campo "Verificato" dalla lista risultato */
	    String[] indiceAssolutoVerificatiSettati = getRequest().getParameterValues("Fl_verif");
	    /* Ottengo i componenti selezionati del campo "Non risolubile" dalla lista risultato */
	    String[] indiceAssolutoNonRisolubiliSettati = getRequest()
		    .getParameterValues("Fl_non_risolub");

	    // MAC 34104
	    boolean showNonRisolubilePopup = false;
	    if (indiceAssolutoNonRisolubiliSettati != null
		    && indiceAssolutoNonRisolubiliSettati.length > 0) {
		for (String position : indiceAssolutoNonRisolubiliSettati) {
		    if (CheckNumeric.isNumeric(position)) {
			int positionNum = Integer.parseInt(position);
			if (positionNum == 0) {
			    // se il primo flag è settato a non risolubile allora mostro il popup
			    showNonRisolubilePopup = true;
			}
		    }
		}
	    }

	    if (!showNonRisolubilePopup) {
		// procedi lungo la strada standard
		impostaVerificNonRisolubOggettiDaVersFallitiCommon();
	    } else {
		// apri un popup sul browser
		getRequest().setAttribute("confermaImpostaVerificNonRisolubOggettiDaVersFalliti",
			true);

		/*
		 * Ricavo dei valori utili per ricaricare la lista dei versamenti falliti a seguito
		 * della modifica dei flag
		 */
		int paginaCorrente = getForm().getOggettoDaVersamentiFallitiDetailVersamentiList()
			.getTable().getCurrentPageIndex();
		int inizio = getForm().getOggettoDaVersamentiFallitiDetailVersamentiList()
			.getTable().getFirstRowPageIndex();
		int fine = getForm().getOggettoDaVersamentiFallitiDetailVersamentiList().getTable()
			.getFirstRowPageIndex()
			+ getForm().getOggettoDaVersamentiFallitiDetailVersamentiList().getTable()
				.getPageSize();
		int ultimaPagina = getForm().getOggettoDaVersamentiFallitiDetailVersamentiList()
			.getTable().getPages();

		/*
		 * Se mi trovo nell'ultima pagina, setto il valore di "fine" con le dimensioni della
		 * tabella
		 */
		if (paginaCorrente == ultimaPagina) {
		    fine = getForm().getOggettoDaVersamentiFallitiDetailVersamentiList().getTable()
			    .size();
		}

		MonVLisVersObjNonVersTableBean table = (MonVLisVersObjNonVersTableBean) getForm()
			.getOggettoDaVersamentiFallitiDetailVersamentiList().getTable();
		for (int i = inizio; i < fine; i++) {
		    table.getRow(i).setFlNonRisolub("0");
		    table.getRow(i).setFlVerif("0");
		}

		for (int i = 0; i < indiceAssolutoNonRisolubiliSettati.length; i++) {
		    if (CheckNumeric.isNumeric(indiceAssolutoNonRisolubiliSettati[i])) {
			int posizione = Integer.parseInt(indiceAssolutoNonRisolubiliSettati[i]);
			table.getRow(posizione - inizio).setFlNonRisolub("1");
		    }
		}

		if (indiceAssolutoVerificatiSettati != null) {
		    for (int i = 0; i < indiceAssolutoVerificatiSettati.length; i++) {
			if (CheckNumeric.isNumeric(indiceAssolutoVerificatiSettati[i])) {
			    int posizione = Integer.parseInt(indiceAssolutoVerificatiSettati[i]);
			    table.getRow(posizione - inizio).setFlVerif("1");
			}
		    }
		}

		getForm().getOggettoDaVersamentiFallitiDetailVersamentiList().setTable(table);
	    }
	} else {
	    getMessageBox().addMessage(new Message(MessageLevel.WAR,
		    "Attenzione: non è possibile impostare i versamenti verificati/non risolubili "
			    + "finché si sta modificando il dettaglio oggetto!"));
	}

	forwardToPublisher(getLastPublisher());
    }

    public void impostaVerificNonRisolubOggettiDaVersFallitiAction() throws Throwable {
	/* Recupero informazioni sull'oggetto */
	BigDecimal idVers = getForm().getOggettoDaVersamentiFallitiDetail().getId_vers().parse();
	String cdKeyObject = getForm().getOggettoDaVersamentiFallitiDetail().getCd_key_object()
		.parse();
	// MEV 26715 - Ricarico la lista versamenti per riportarlo allo stato originale
	// dopo essere passati per impostaVerificNonRisolubOggettiDaVersFalliti()
	MonVLisVersObjNonVersTableBean versObjNonVersTB = monitoraggioHelper
		.getMonVLisVersObjNonVersViewBean(idVers, cdKeyObject);
	getForm().getOggettoDaVersamentiFallitiDetailVersamentiList().setTable(versObjNonVersTB);

	impostaVerificNonRisolubOggettiDaVersFallitiCommon();
    }

    public void impostaVerificNonRisolubOggettiDaVersFallitiCommon() throws Throwable {
	/* Se NON sono in status di "update" su altri campi */
	if (!getForm().getOggettiDaVersamentiFallitiList().getStatus().equals(Status.update)) {
	    /* Recupero informazioni sull'oggetto */
	    BigDecimal idVers = getForm().getOggettoDaVersamentiFallitiDetail().getId_vers()
		    .parse();
	    String cdKeyObject = getForm().getOggettoDaVersamentiFallitiDetail().getCd_key_object()
		    .parse();
	    String nmTipoObject = getForm().getOggettoDaVersamentiFallitiDetail()
		    .getNm_tipo_object().parse();

	    ////////////////////////////////////////////////////////////////////////////////////////
	    // AGGIORNAMENTO FLAG "VERIFICATO" E "NON RISOLUBILE" DEI VERSAMENTI FALLITI IN LISTA //
	    ////////////////////////////////////////////////////////////////////////////////////////
	    /* Ottengo i componenti selezionati del campo "Verificato" dalla lista risultato */
	    String[] indiceAssolutoVerificatiSettati = getRequest().getParameterValues("Fl_verif");
	    /* Ottengo i componenti selezionati del campo "Non risolubile" dalla lista risultato */
	    String[] indiceAssolutoNonRisolubiliSettati = getRequest()
		    .getParameterValues("Fl_non_risolub");

	    int totVerificati = indiceAssolutoVerificatiSettati != null
		    ? indiceAssolutoVerificatiSettati.length
		    : 0;
	    int totNonRisolubili = indiceAssolutoNonRisolubiliSettati != null
		    ? indiceAssolutoNonRisolubiliSettati.length
		    : 0;

	    /*
	     * Ricavo dei valori utili per ricaricare la lista dei versamenti falliti a seguito
	     * della modifica dei flag
	     */
	    int paginaCorrente = getForm().getOggettoDaVersamentiFallitiDetailVersamentiList()
		    .getTable().getCurrentPageIndex();
	    int inizio = getForm().getOggettoDaVersamentiFallitiDetailVersamentiList().getTable()
		    .getFirstRowPageIndex();
	    int fine = getForm().getOggettoDaVersamentiFallitiDetailVersamentiList().getTable()
		    .getFirstRowPageIndex()
		    + getForm().getOggettoDaVersamentiFallitiDetailVersamentiList().getTable()
			    .getPageSize();
	    int ultimaPagina = getForm().getOggettoDaVersamentiFallitiDetailVersamentiList()
		    .getTable().getPages();

	    /*
	     * Se mi trovo nell'ultima pagina, setto il valore di "fine" con le dimensioni della
	     * tabella
	     */
	    if (paginaCorrente == ultimaPagina) {
		fine = getForm().getOggettoDaVersamentiFallitiDetailVersamentiList().getTable()
			.size();
	    }

	    ////////////////////////////////////////////////////////////////////////////
	    ///// Ricavo i valori delle checkbox "Verificati" e "Non Risolubili" ///////
	    ///// PRIMA e DOPO eventuali modifiche. ////////////////////////////////////
	    ///// Quindi li confronto per vedere se ci sono state modifiche ////////////
	    ////////////////////////////////////////////////////////////////////////////
	    int numRecordPerPagina = fine - inizio;
	    MonVLisVersObjNonVersTableBean monObjVersErr = (MonVLisVersObjNonVersTableBean) getForm()
		    .getOggettoDaVersamentiFallitiDetailVersamentiList().getTable();
	    String[] verificatiPre = new String[numRecordPerPagina];
	    String[] verificatiPost = new String[numRecordPerPagina];
	    String[] nonRisolubiliPre = new String[numRecordPerPagina];
	    String[] nonRisolubiliPost = new String[numRecordPerPagina];
	    BigDecimal[] idSessioneIngest = new BigDecimal[numRecordPerPagina];
	    /*
	     * Contiene gli indici assoluti dei record in cui sono stati modificati il flag
	     * "Verificato" o il flag "Non Risolubile" o entrambi
	     */
	    Set<Integer> verificatiNonRisolubiliModificati = new HashSet<>();

	    /* 1) Inserisco tutti i valori dei PRE e tutti "0" nei POST */
	    int count = 0;
	    for (int i = inizio; i < fine; i++) {
		verificatiPre[count] = monObjVersErr.getRow(i).getFlVerif() != null
			? monObjVersErr.getRow(i).getFlVerif()
			: "0";
		nonRisolubiliPre[count] = monObjVersErr.getRow(i).getFlNonRisolub() != null
			? monObjVersErr.getRow(i).getFlNonRisolub()
			: "0";
		idSessioneIngest[count] = monObjVersErr.getRow(i).getIdSessioneIngest();
		verificatiPost[count] = "0";
		nonRisolubiliPost[count] = "0";
		count++;
	    }

	    /* 2) Ora nei POST metto gli "1" dove vanno messi */
	    for (int j = 0; j < totVerificati; j++) {
		if (CheckNumeric.isNumeric(indiceAssolutoVerificatiSettati[j])) {
		    int posizione = Integer.parseInt(indiceAssolutoVerificatiSettati[j]);
		    verificatiPost[posizione - inizio] = "1";
		}
	    }

	    /*
	     * 3) Ora che ho i record prima e dopo le eventuali modifiche, ricavo gli indici dei
	     * record "Verificati" modificati
	     */
	    for (int k = 0; k < verificatiPre.length; k++) {
		if (!verificatiPre[k].equals(verificatiPost[k])) {
		    BigDecimal idSessione = idSessioneIngest[k];
		    if (monitoraggioEjb.checkSessionObjectDaVerif(idSessione, false)) {
			verificatiNonRisolubiliModificati.add(k + inizio);
		    }
		}
	    }

	    /* Idem con patate per i "Non risolubili" */
	    for (int j = 0; j < totNonRisolubili; j++) {
		if (CheckNumeric.isNumeric(indiceAssolutoNonRisolubiliSettati[j])) {
		    int posizione = Integer.parseInt(indiceAssolutoNonRisolubiliSettati[j]);
		    nonRisolubiliPost[posizione - inizio] = "1";
		}
	    }

	    /* Idem con patate per i "Non risolubili" */
	    for (int k = 0; k < nonRisolubiliPre.length; k++) {
		if (!nonRisolubiliPre[k].equals(nonRisolubiliPost[k])) {
		    BigDecimal idSessione = idSessioneIngest[k];
		    if (monitoraggioEjb.checkSessionObjectDaVerif(idSessione, false)) {
			verificatiNonRisolubiliModificati.add(k + inizio);
		    }
		}
	    }

	    int effettivamenteModificati = 0;
	    for (int integer : verificatiNonRisolubiliModificati) {
		BigDecimal idSessione = getForm()
			.getOggettoDaVersamentiFallitiDetailVersamentiList().getTable()
			.getRow(integer).getBigDecimal(ID_SESSIONE_INGEST);
		// Punto 6 dell'analisi
		if (monitoraggioEjb.canModifyVerificataNonRisolubile(idSessione)) {
		    effettivamenteModificati++;
		    boolean flagVerificato = verificatiPost[integer - inizio]
			    .equals(Constants.DB_TRUE);
		    boolean flagNonRisolubile = nonRisolubiliPost[integer - inizio]
			    .equals(Constants.DB_TRUE);
		    // punto a) dell'analisi

		    String errore = monitoraggioEjb.impostaFlagVerificatoNonRisolubile(idSessione,
			    flagVerificato, flagNonRisolubile);
		    if (errore != null) {
			getMessageBox().addError(errore);
			break;
		    }
		}
	    }

	    /* CONTROLLI DI COERENZA SOLO SUGLI ELEMENTI MODIFICATI */
	    if (!getMessageBox().hasError()) {
		/* Se ci sono state modifiche, le salvo su DB */
		if (!verificatiNonRisolubiliModificati.isEmpty()) {
		    /* Ottengo gli id sessione dei record "verificati" MODIFICATI */
		    String flVerifAggiornato = monitoraggioHelper.getObjNonVersFlVerif(idVers,
			    cdKeyObject);
		    /* Controllo se adesso devo rendere editabile il flag fl_vers_sacer_da_recup */
		    boolean isFlVersSacerDaRecupEditable = monitoraggioEjb
			    .isFlVersSacerDaRecupEditable(idVers, cdKeyObject, nmTipoObject,
				    flVerifAggiornato);
		    // Setta lo status (editabile o meno) del flag versamento a SACER da recuperare
		    setStatusFlVersSacerDaRecup(isFlVersSacerDaRecupEditable);

		    // Carico il rowbean del dettaglio oggetti derivanti da versamenti falliti
		    MonVVisObjNonVersRowBean objNonVersRB = monitoraggioHelper
			    .getMonVVisObjNonVersRowBean(idVers, cdKeyObject);
		    getForm().getOggettoDaVersamentiFallitiDetail().copyFromBean(objNonVersRB);

		    // Carico la lista versamenti
		    MonVLisVersObjNonVersTableBean versObjNonVersTB = monitoraggioHelper
			    .getMonVLisVersObjNonVersViewBean(idVers, cdKeyObject);
		    getForm().getOggettoDaVersamentiFallitiDetailVersamentiList()
			    .setTable(versObjNonVersTB);
		    getForm().getOggettoDaVersamentiFallitiDetailVersamentiList().getTable()
			    .setPageSize(10);
		    getForm().getOggettoDaVersamentiFallitiDetailVersamentiList().getTable()
			    .first();

		    // Segnalo l'avvenuta impostazione dei flaggozzi andata a buon fine solo se ne
		    // ho salvato almeno uno
		    if (effettivamenteModificati > 0) {
			getMessageBox().addMessage(new Message(MessageLevel.INF,
				"Aggiornamento effettuato con successo"));
		    }
		    getMessageBox().setViewMode(ViewMode.plain);
		    forwardToPublisher(Application.Publisher.OGGETTO_DA_VERSAMENTI_FALLITI_DETAIL);
		} // Altrimenti segnalo il fatto di non aver apportato modifiche sul DB
		else {
		    getMessageBox().addMessage(new Message(MessageLevel.WAR,
			    "Aggiornamento non effettuato in quanto non sono state apportate modifiche ai record"));
		    getMessageBox().setViewMode(ViewMode.plain);
		    forwardToPublisher(Application.Publisher.OGGETTO_DA_VERSAMENTI_FALLITI_DETAIL);
		}
	    }
	    // MAC#24891: Correzione della funzionalità per settare a non risolubile una sessione
	    // annullata
	    // Non ricaricava i dati nuovi della maschera e il tasto annulla poteva non comparire
	    caricaDettaglioProvenendoDaListaVersamentiFalliti();
	} else {
	    getMessageBox().addMessage(new Message(MessageLevel.WAR,
		    "Attenzione: non è possibile impostare i versamenti verificati/non risolubili "
			    + "finché si sta modificando il dettaglio oggetto!"));
	}
	forwardToPublisher(Application.Publisher.OGGETTO_DA_VERSAMENTI_FALLITI_DETAIL);
    }

    @Override
    public JSONObject triggerFiltriVersamentiVersamento_ti_statoOnTrigger() throws EMFError {
	getForm().getFiltriVersamenti().post(getRequest());
	List<String> statiList = getForm().getFiltriVersamenti().getVersamento_ti_stato().parse();
	try {
	    PigClasseErroreTableBean table = messaggiEjb.getPigClasseErroreTableBean(statiList);
	    getForm().getFiltriVersamenti().getClasse_errore().setDecodeMap(DecodeMap.Factory
		    .newInstance(table, "cd_classe_errore", "ds_classe_composita"));
	} catch (ParerUserError ex) {
	    getMessageBox().addError(ex.getDescription());
	}

	return getForm().getFiltriVersamenti().asJSON();
    }

    @Override
    public JSONObject triggerFiltriVersamentiClasse_erroreOnTrigger() throws EMFError {
	getForm().getFiltriVersamenti().post(getRequest());
	String classe = getForm().getFiltriVersamenti().getClasse_errore().parse();
	try {
	    PigErroreTableBean table = messaggiEjb.getPigErroreTableBean(classe);
	    getForm().getFiltriVersamenti().getCd_err().setDecodeMap(
		    DecodeMap.Factory.newInstance(table, "cd_errore", "ds_errore_composito"));
	} catch (ParerUserError ex) {
	    getMessageBox().addError(ex.getDescription());
	}
	return getForm().getFiltriVersamenti().asJSON();
    }

    @Override
    public JSONObject triggerFiltriOggettiDerVersFallitiOggetti_der_fl_verifOnTrigger()
	    throws EMFError {
	return triggerFiltriOggettiDerVersFallitiOggetti_der_fl_non_risolubOnTrigger();

    }

    @Override
    public JSONObject triggerFiltriOggettiDerVersFallitiOggetti_der_fl_non_risolubOnTrigger()
	    throws EMFError {
	MonitoraggioForm.FiltriOggettiDerVersFalliti filtri = getForm()
		.getFiltriOggettiDerVersFalliti();
	// Esegue la post dei filtri compilati
	filtri.post(getRequest());
	if ((filtri.getOggetti_der_fl_verif() != null
		&& (filtri.getOggetti_der_fl_verif().getValue().equals("1")
			|| filtri.getOggetti_der_fl_verif().getValue().equals("0"))
		&& (filtri.getOggetti_der_fl_non_risolub() != null
			&& filtri.getOggetti_der_fl_non_risolub().getValue().equals("")))) {
	    getForm().getFiltriOggettiDerVersFalliti().getOggetti_der_fl_vers_sacer_da_recup()
		    .setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());
	} else {
	    getForm().getFiltriOggettiDerVersFalliti().getOggetti_der_fl_vers_sacer_da_recup()
		    .setDecodeMap(new DecodeMap());
	}
	return getForm().getFiltriOggettiDerVersFalliti().asJSON();
    }

    private void downloadFileOggetto(String cdKeyObject,
	    BaseTableInterface<? extends BaseRowInterface> table, BigDecimal idVers)
	    throws EMFError {
	if (!table.isEmpty()) {
	    getResponse().setContentType("application/zip");
	    getResponse().setHeader("Content-Disposition",
		    "attachment; filename=\"" + cdKeyObject + ".zip");
	    ZipOutputStream out = null;
	    try {
		out = new ZipOutputStream(new BufferedOutputStream(getServletOutputStream()));
		String dir = cdKeyObject + File.separator;
		out.putNextEntry(new ZipEntry(dir));

		String completePath;
		String rootDir;
		String versInputPath;
		if (table instanceof MonVLisFileObjectTableBean) {
		    // Lista file
		    rootDir = commonDb.getRootFtpParam();
		    versInputPath = monitoraggioHelper.getDsPathInputFtp(idVers);
		} else {
		    // oggetto trasformato
		    rootDir = commonDb.getRootTrasfParam();
		    versInputPath = monitoraggioHelper.getDsPathTrasf(idVers);
		}

		for (BaseRowInterface row : table) {
		    String path;
		    if (table instanceof MonVLisFileObjectTableBean) {
			path = row.getString("nm_file_object");
			completePath = rootDir + versInputPath + dir + path;
		    } else {
			path = row.getString("ds_path");
			completePath = rootDir + versInputPath + path;
		    }
		    File fileInput = new File(completePath);
		    if (fileInput.exists()) {
			if (fileInput.isFile()) {
			    try (InputStream input = FileUtils.openInputStream(fileInput)) {
				byte[] data = new byte[1024];
				int count;
				out.putNextEntry(new ZipEntry(dir + fileInput.getName()));
				while ((count = input.read(data, 0, 1024)) != -1) {
				    out.write(data, 0, count);
				}
				out.closeEntry();
			    }
			} else if (fileInput.isDirectory()) {
			    for (File file : FileUtils.listFiles(fileInput, null, true)) {
				try (InputStream input = FileUtils.openInputStream(file)) {
				    byte[] data = new byte[1024];
				    int count;
				    out.putNextEntry(new ZipEntry(dir + file.getName()));
				    while ((count = input.read(data, 0, 1024)) != -1) {
					out.write(data, 0, count);
				    }
				    out.closeEntry();
				}
			    }
			}
		    } // MEV 21995 - il file potrebbe essere su object storage
		    else {
			BigDecimal idBucket = row.getBigDecimal("id_backend");
			String nmBucket = row.getString("nm_bucket");
			String cdKeyFile = row.getString("cd_key_file");

			if (idBucket != null && nmBucket != null && cdKeyFile != null) {
			    BackendStorage backend = salvataggioBackendHelper
				    .getBackend(idBucket.longValue());

			    ObjectStorageBackend config = salvataggioBackendHelper
				    .getObjectStorageConfigurationForVersamento(
					    backend.getBackendName(), nmBucket);
			    ResponseInputStream<GetObjectResponse> ogg = salvataggioBackendHelper
				    .getObject(config, cdKeyFile);
			    byte[] data = new byte[1024];
			    int count;
			    out.putNextEntry(new ZipEntry(dir + fileInput.getName()));
			    while ((count = ogg.read(data, 0, 1024)) != -1) {
				out.write(data, 0, count);
			    }
			    out.closeEntry();
			}
		    }

		    // MEV 27039 - aggiungo l'eventuale indice oggetto allo zip
		    byte[] xmlVersamento = getForm().getOggettoDetail().getBl_xml().getValue()
			    .getBytes();
		    String versioneXml = getForm().getOggettoDetail().getCd_versione_xml_vers()
			    .getValue();
		    if (xmlVersamento != null && xmlVersamento.length > 0) {
			InputStream is = new ByteArrayInputStream(xmlVersamento);
			byte[] data = new byte[1024];
			int count;

			out.putNextEntry(
				new ZipEntry(dir + cdKeyObject + "-" + versioneXml + ".xml"));
			while ((count = is.read(data, 0, 1024)) != -1) {
			    out.write(data, 0, count);
			}
			out.closeEntry();

			while ((count = is.read(data, 0, 1024)) != -1) {
			    out.write(data, 0, count);
			}
		    }

		}
		out.flush();
	    } catch (Exception e) {
		getMessageBox().addMessage(new Message(MessageLevel.ERR, ERRORE_RECUPERO_FILE));
		log.error(ERRORE_RECUPERO_FILE, e);
	    } finally {
		if (out != null) {
		    IOUtils.closeQuietly(out);
		    if (!getMessageBox().hasError()) {
			freeze();
		    }
		}
	    }

	    forwardToPublisher(getLastPublisher());
	}
    }

    private void oggettoDaTrasformare(BigDecimal idObjectPadre) throws EMFError {
	try {
	    if (idObjectPadre != null) {
		List<BigDecimal> idObjStack = getIdObjStack();
		idObjStack.add(idObjectPadre);
		getSession().setAttribute(GET_ID_OBJ_STACK, idObjStack);
		getSession().setAttribute(ID_OBJECT, idObjectPadre);
		loadDettaglioObject(idObjectPadre);
		forwardToPublisher(Application.Publisher.OGGETTO_DETAIL);
	    } else {
		getMessageBox().addError(
			"Errore inatteso nella visualizzazione dell'oggetto versato a PING");
		forwardToPublisher(getLastPublisher());
	    }
	} catch (ParerUserError ex) {
	    getMessageBox().addError(ex.getDescription());
	    forwardToPublisher(getLastPublisher());
	}
    }

    // MEV28877
    @Override
    public void modificaTipoOggetto() throws EMFError {
	// Modifica Tipo oggetto in dettaglio oggetto per gli oggetti da trasformare
	PigTipoObjectTableBean tipoObjectTableBean = comboHelper.getTipoObjectFromVersatore(
		getUser().getIdUtente(), getForm().getOggettoDetail().getId_vers().parse(),
		Constants.TipoVersamento.DA_TRASFORMARE.name());
	getForm().getOggettoDetail().getId_tipo_object().setDecodeMap(DecodeMap.Factory
		.newInstance(tipoObjectTableBean, "id_tipo_object", "nm_tipo_object"));
	getForm().getOggettoDetail().getId_tipo_object().setValue(
		getForm().getOggettoDetail().getId_tipo_object().parse().longValueExact() + "");
	PigTipoObject pigTipoObject = monitoraggioHelper.findById(PigTipoObject.class,
		getForm().getOggettoDetail().getId_tipo_object().parse());
	if (pigTipoObject.getFlNoVisibVersOgg() == null
		|| pigTipoObject.getFlNoVisibVersOgg().equals("0")) {
	    getForm().getOggettoDetail().getId_tipo_object().setEditMode();
	}
	getRequest().setAttribute("confermaModificaTipoOggetto", true);
	forwardToPublisher(Application.Publisher.OGGETTO_DETAIL);
    }

    @Override
    public void recuperoErrTrasformazione() throws EMFError {
	/*
	 * Se lo stato dell'oggetto vale ERRORE_TRASFORMAZIONE, eseguo il recupero con stato
	 * CHIUSO_ERR_TRASFORMAZIONE Altrimenti se lo stato vale WARNING_TRASFORMAZIONE, mostro un
	 * popup con gli stati possibili (DA_TRASFORMARE, TRASFORMATO, CHIUSO_ERR_TRASFORMAZIONE)
	 */
	getForm().getOggettoDetail().getTi_stato_popup().setEditMode();
	if (getForm().getOggettoDetail().getTi_stato_object().parse()
		.equals(Constants.StatoOggetto.ERRORE_TRASFORMAZIONE.name())) {
	    // MEV #14585 - Recupero errori di trasformazione: prevedere nuovi stati
	    getForm().getOggettoDetail().getTi_stato_popup()
		    .setDecodeMap(ComboGetter.getMappaSortedGenericEnum("ti_stato",
			    Constants.StatoOggetto.CHIUSO_ERR_TRASFORMAZIONE,
			    Constants.StatoOggetto.DA_TRASFORMARE));
	} else {
	    getForm().getOggettoDetail().getTi_stato_popup()
		    .setDecodeMap(ComboGetter.getMappaSortedGenericEnum("ti_stato",
			    Constants.StatoOggetto.DA_TRASFORMARE,
			    Constants.StatoOggetto.TRASFORMATO,
			    Constants.StatoOggetto.CHIUSO_ERR_TRASFORMAZIONE));
	}

	/*
	 * MEV#15178 - Modifica Tipo oggetto in dettaglio oggetto per gli oggetti da trasformare
	 *
	 */
	PigTipoObjectTableBean tipoObjectTableBean = comboHelper.getTipoObjectFromVersatore(
		getUser().getIdUtente(), getForm().getOggettoDetail().getId_vers().parse(),
		Constants.TipoVersamento.DA_TRASFORMARE.name());
	getForm().getOggettoDetail().getId_tipo_object().setDecodeMap(DecodeMap.Factory
		.newInstance(tipoObjectTableBean, "id_tipo_object", "nm_tipo_object"));
	getForm().getOggettoDetail().getId_tipo_object().setValue(
		getForm().getOggettoDetail().getId_tipo_object().parse().longValueExact() + "");
	PigTipoObject pigTipoObject = monitoraggioHelper.findById(PigTipoObject.class,
		getForm().getOggettoDetail().getId_tipo_object().parse());
	if (pigTipoObject.getFlNoVisibVersOgg() == null
		|| pigTipoObject.getFlNoVisibVersOgg().equals("0")) {
	    getForm().getOggettoDetail().getId_tipo_object().setEditMode();
	}
	getForm().getOggettoDetail().getTi_recupero()
		.setValue(TiRecuperoErrore.TRASFORMAZIONE.name());
	getRequest().setAttribute("confermaRecuperoErrore", true);
	forwardToPublisher(Application.Publisher.OGGETTO_DETAIL);
    }

    @Override
    public void downloadFileOggettoObjDetail() throws EMFError {
	String cdKeyObject = getForm().getOggettoDetail().getCd_key_object().getValue();
	BigDecimal idVers = getForm().getOggettoDetail().getId_vers().parse();
	downloadFileOggetto(cdKeyObject, getForm().getFileList().getTable(), idVers);
    }

    @Override
    public void oggettoDaTrasformareObjDetail() throws EMFError {
	BigDecimal idObjectPadre = getForm().getOggettoDetail().getId_object_padre().parse();
	oggettoDaTrasformare(idObjectPadre);
    }

    public void downloadFileOggettoObjGenTrasf() throws EMFError {
	String riga = getRequest().getParameter("riga");
	Integer nr = Integer.valueOf(riga);

	String cdKeyObject = getForm().getOggettoDetailOggettiTrasfList().getTable().getRow(nr)
		.getString("cd_key_object_trasf");
	BigDecimal idVers = getForm().getOggettoDetailOggettiTrasfList().getTable().getRow(nr)
		.getBigDecimal("id_vers_trasf");

	BaseTable tb = new BaseTable();
	BaseRow row = new BaseRow();
	getForm().getOggettoDetailOggettiTrasfList().copyToBean(row);
	tb.add(row);
	downloadFileOggetto(cdKeyObject, tb, idVers);
    }

    // MEV26398
    @Override
    public void annullaOggettoDetail() throws EMFError {
	BigDecimal idObject = getForm().getOggettoDetail().getId_object().parse();
	getRequest().setAttribute("confermaAnnullamentoOggetto", true);

	if (annullamentoEjb.controllaSeSimsmaNonAnnullabile(idObject)) {
	    getRequest().setAttribute("confermaAnnullamentoOggettoSisma", true);
	}

	forwardToPublisher(getLastPublisher());
    }

    public void annullaOggettoDetailAction() throws EMFError {
	BigDecimal idObject = getForm().getOggettoDetail().getId_object().parse();
	try {
	    // MEV 32543 - impostiamo l'ultima sessione a verificata e non risolubile.
	    String errore = monitoraggioEjb.preAnnullamentoSetup(idObject);

	    if (errore != null) {
		getMessageBox().addError(errore);
	    } else {
		annullaOggetto(idObject);
	    }
	} catch (Exception ex) {
	    throw new EMFError(EMFError.ERROR, ex.getMessage());
	}
	redirectToAjax(getForm().getOggettoDetail().asJSON());
    }

    /*
     * Nuovo pulsante per MEV#14652 e SUE26200
     */
    @Override
    public void annullaVersamentiUDDetail() throws EMFError {
	getRequest().setAttribute("confermaAnnullamentoUD", true);

	BigDecimal idObject = getForm().getOggettoDetail().getId_object().parse();
	Long countPigUnitaDocObjectDuplicate = monitoraggioHelper
		.countPigUnitaDocObjectDuplicate(idObject);

	if (annullamentoEjb.controllaSeSimsmaNonAnnullabile(idObject)) {
	    getRequest().setAttribute("confermaAnnullamentoOggettoSisma", true);
	}

	getRequest().setAttribute("ni_unita_doc_vers",
		getForm().getOggettoDetail().getNi_unita_doc_vers().parse());
	getRequest().setAttribute("ni_unita_doc_vers_dup", countPigUnitaDocObjectDuplicate);
	forwardToPublisher(getLastPublisher());
    }

    public void annullaVersamentiUDDetailAction() throws EMFError {
	BigDecimal idObject = getForm().getOggettoDetail().getId_object().parse();
	boolean richiestoAnnullamentoVersamentiUDDuplicati = getRequest()
		.getParameter("Ti_annullamento_ud").equals("1");
	// MEV 27691
	String motivazioneAnnullamento = getRequest().getParameter("ds_annullamento_ud");

	if (motivazioneAnnullamento != null && motivazioneAnnullamento.length() > 2000) {
	    getMessageBox().addError(
		    "La motivazione annullamento non può essere più lunga di 2000 caratteri.");
	    redirectToAjax(getForm().getOggettoDetail().asJSON());
	    return;
	}

	motivazioneAnnullamento = StringEscapeUtils.escapeHtml4(motivazioneAnnullamento);
	motivazioneAnnullamento = StringEscapeUtils.escapeEcmaScript(motivazioneAnnullamento);
	motivazioneAnnullamento = StringEscapeUtils.escapeXml10(motivazioneAnnullamento);

	try {
	    // MEV 32543 - impostiamo l'ultima sessione a verificata e non risolubile.
	    String errore = monitoraggioEjb.preAnnullamentoSetup(idObject);

	    if (errore != null) {
		getMessageBox().addError(errore);
	    } else {
		// richiede cancellazione UD versate
		annullaOggetto(idObject, true, richiestoAnnullamentoVersamentiUDDuplicati,
			motivazioneAnnullamento);
	    }
	} catch (Exception ex) {
	    throw new EMFError(EMFError.ERROR, ex.getMessage());
	}

	redirectToAjax(getForm().getOggettoDetail().asJSON());
    }

    @Override
    public void annullaOggettoDerVersFalliti() throws EMFError {
	BigDecimal idVers = getForm().getOggettiDaVersamentiFallitiList().getTable().getCurrentRow()
		.getBigDecimal("id_vers");
	String cdKeyObject = getForm().getOggettiDaVersamentiFallitiList().getTable()
		.getCurrentRow().getString("cd_key_object");
	PigObject obj = monitoraggioHelper.getPigObject(idVers, cdKeyObject);
	// richiede cancellazione UD
	annullaOggetto(new BigDecimal(obj.getIdObject()), false, false, "");
	forwardToPublisher(getLastPublisher());
    }

    private void annullaOggetto(BigDecimal idObject) throws EMFError {
	annullaOggetto(idObject, false, false, "");
    }

    // SUE26200 - Aggiunta opzione per escludere dall'annullamento le UD già versate da un'altro
    // oggetto (Errore
    // UD-002-001)
    private void annullaOggetto(BigDecimal idObject, boolean richiestoAnnullamentoVersamentiUD,
	    boolean richiestoAnnullamentoVersamentiUDDuplicati, String motivazioneAnnullamento)
	    throws EMFError {
	try {

	    String username = getUser().getUsername();
	    annullamentoEjb.annullaOggetto(idObject, richiestoAnnullamentoVersamentiUD,
		    richiestoAnnullamentoVersamentiUDDuplicati, motivazioneAnnullamento, username);
	} catch (ParerUserError | ParerInternalError ex) {
	    getMessageBox().addError(ex.getDescription());
	}

	try {
	    loadDettaglioObject(idObject);
	} catch (ParerUserError ex) {
	    getMessageBox().addError(ex.getDescription());
	}
    }

    @Override
    public void verificaAnnullamento() throws EMFError {
	BigDecimal idObject = getForm().getOggettoDetail().getId_object().parse();
	try {
	    String username = getUser().getUsername();
	    annullamentoEjb.verificaAnnullamentoTerminato(idObject, username);
	} catch (ParerUserError | ParerInternalError ex) {
	    getMessageBox().addError(ex.getDescription());
	}
	try {
	    loadDettaglioObject(idObject);
	} catch (ParerUserError ex) {
	    getMessageBox().addError(ex.getDescription());
	}
	forwardToPublisher(getLastPublisher());
    }

    @Override
    public void accettaAnnullamentoFallito() throws Throwable {
	BigDecimal idObject = getForm().getOggettoDetail().getId_object().parse();
	annullamentoEjb.accettaAnnullamentoFallito(idObject);
	try {
	    loadDettaglioObject(idObject);
	} catch (ParerUserError ex) {
	    getMessageBox().addError(ex.getDescription());
	}
	forwardToPublisher(getLastPublisher());
    }

    @Override
    public void filtraUnitaDocObj() throws EMFError {
	BigDecimal idObject = getForm().getOggettoDetail().getId_object().parse();
	if (idObject != null && getForm().getFiltriUnitaDocObj().postAndValidate(getRequest(),
		getMessageBox())) {
	    BigDecimal aaUnitaDoc = getForm().getFiltriUnitaDocObj().getAa_unita_doc_sacer()
		    .parse();
	    String cdKeyUnitaDoc = getForm().getFiltriUnitaDocObj().getCd_key_unita_doc_sacer()
		    .parse();
	    String cdRegistroUnitaDoc = getForm().getFiltriUnitaDocObj()
		    .getCd_registro_unita_doc_sacer().parse();
	    String tiStatoUnitaDoc = getForm().getFiltriUnitaDocObj().getTi_stato_unita_doc_object()
		    .parse();
	    String cdErrore = getForm().getFiltriUnitaDocObj().getCd_concat_dl_err_sacer().parse();

	    int pageSize = 10;
	    if (getForm().getOggettoDetailUnitaDocList().getTable() != null) {
		pageSize = getForm().getOggettoDetailUnitaDocList().getTable().getPageSize();
	    }
	    // Carico la lista unitÃ  doc.
	    MonVLisUnitaDocObjectTableBean udObjectTB = monitoraggioHelper
		    .getMonVLisUnitaDocObjectTableBean(idObject, cdRegistroUnitaDoc, aaUnitaDoc,
			    cdKeyUnitaDoc, tiStatoUnitaDoc, cdErrore);
	    getForm().getOggettoDetailUnitaDocList().setTable(udObjectTB);
	    getForm().getOggettoDetailUnitaDocList().getTable().setPageSize(pageSize);
	    getForm().getOggettoDetailUnitaDocList().getTable().first();
	}
	forwardToPublisher(getLastPublisher());
    }

    @Override
    public JSONObject triggerFiltriUnitaDocVersNm_ambienteOnTrigger() throws EMFError {
	getForm().getFiltriUnitaDocVers().post(getRequest());
	String nmAmbiente = getForm().getFiltriUnitaDocVers().getNm_ambiente().parse();
	BigDecimal idSessioneIngest = getForm().getVersamentoDetail().getId_sessione_ingest()
		.parse();
	if (StringUtils.isNotBlank(nmAmbiente)) {
	    getForm().getFiltriUnitaDocVers().getNm_ente()
		    .setDecodeMap(DecodeMap.Factory.newInstance(monitoraggioHelper
			    .getDistinctColumnFromMonVLisUnitaDocSessioneTableBean(idSessioneIngest,
				    String.class, "AND u.nmAmbiente = '" + nmAmbiente + "'",
				    MonVLisUnitaDocSessioneTableDescriptor.COL_NM_ENTE),
			    MonVLisUnitaDocSessioneTableDescriptor.COL_NM_ENTE,
			    MonVLisUnitaDocSessioneTableDescriptor.COL_NM_ENTE));
	}
	return getForm().getFiltriUnitaDocVers().asJSON();
    }

    @Override
    public JSONObject triggerFiltriUnitaDocVersNm_enteOnTrigger() throws EMFError {
	getForm().getFiltriUnitaDocVers().post(getRequest());
	String nmEnte = getForm().getFiltriUnitaDocVers().getNm_ente().parse();
	BigDecimal idSessioneIngest = getForm().getVersamentoDetail().getId_sessione_ingest()
		.parse();
	if (StringUtils.isNotBlank(nmEnte)) {
	    getForm().getFiltriUnitaDocVers().getNm_strut()
		    .setDecodeMap(DecodeMap.Factory.newInstance(monitoraggioHelper
			    .getDistinctColumnFromMonVLisUnitaDocSessioneTableBean(idSessioneIngest,
				    String.class, "AND u.nmEnte = '" + nmEnte + "'",
				    MonVLisUnitaDocSessioneTableDescriptor.COL_NM_STRUT),
			    MonVLisUnitaDocSessioneTableDescriptor.COL_NM_STRUT,
			    MonVLisUnitaDocSessioneTableDescriptor.COL_NM_STRUT));
	}
	return getForm().getFiltriUnitaDocVers().asJSON();
    }

    @Override
    public void annullaVersamentiUDDerVersFalliti() throws EMFError {
	getRequest().setAttribute("confermaAnnullamentoUD", true);

	BigDecimal idVers = getForm().getOggettiDaVersamentiFallitiList().getTable().getCurrentRow()
		.getBigDecimal("id_vers");
	String cdKeyObject = getForm().getOggettiDaVersamentiFallitiList().getTable()
		.getCurrentRow().getString("cd_key_object");

	PigObject obj = monitoraggioHelper.getPigObject(idVers, cdKeyObject);
	BigDecimal idObject = BigDecimal.valueOf(obj.getIdObject());
	Long countPigUnitaDocObject = monitoraggioHelper.countPigUnitaDocObject(idObject);
	Long countPigUnitaDocObjectDuplicate = monitoraggioHelper
		.countPigUnitaDocObjectDuplicate(idObject);

	getRequest().setAttribute("ni_unita_doc_vers", countPigUnitaDocObject);
	getRequest().setAttribute("ni_unita_doc_vers_dup", countPigUnitaDocObjectDuplicate);

	forwardToPublisher(getLastPublisher());
    }

    public void annullaVersamentiUDDerVersFallitiAction() throws Throwable {
	BigDecimal idVers = getForm().getOggettiDaVersamentiFallitiList().getTable().getCurrentRow()
		.getBigDecimal("id_vers");
	String cdKeyObject = getForm().getOggettiDaVersamentiFallitiList().getTable()
		.getCurrentRow().getString("cd_key_object");

	boolean richiestoAnnullamentoVersamentiUDDuplicati = getRequest()
		.getParameter("Ti_annullamento_ud").equals("1");

	// MEV 27691
	String motivazioneAnnullamento = getRequest().getParameter("ds_annullamento_ud");
	motivazioneAnnullamento = StringEscapeUtils.escapeHtml4(motivazioneAnnullamento);
	motivazioneAnnullamento = StringEscapeUtils.escapeEcmaScript(motivazioneAnnullamento);
	motivazioneAnnullamento = StringEscapeUtils.escapeXml10(motivazioneAnnullamento);

	PigObject obj = monitoraggioHelper.getPigObject(idVers, cdKeyObject);
	// richiede cancellazione UD
	annullaOggetto(new BigDecimal(obj.getIdObject()), true,
		richiestoAnnullamentoVersamentiUDDuplicati, motivazioneAnnullamento);
	forwardToPublisher(getLastPublisher());
    }

    @Override
    public void recuperoChiusErrVers() throws Throwable {
	BigDecimal idObject = getForm().getOggettoDetail().getId_object().parse();
	BigDecimal idTipoObject = getForm().getOggettoDetail().getId_tipo_object().parse();
	try {
	    monitoraggioEjb.recuperaErrore(idObject,
		    Constants.StatoOggetto.CHIUSO_ERR_RECUPERABILE.name(),
		    Constants.StatoOggetto.CHIUSO_ERR_VERS.name(), idTipoObject);
	    loadDettaglioObject(idObject);
	} catch (ParerUserError ex) {
	    getMessageBox().addError(ex.getDescription());
	}
	forwardToPublisher(getLastPublisher());
    }

    // MEV 37583
    @Override
    public void recuperoChiusErrSched() throws Throwable {
	BigDecimal idObject = getForm().getOggettoDetail().getId_object().parse();
	BigDecimal idTipoObject = getForm().getOggettoDetail().getId_tipo_object().parse();
	try {
	    monitoraggioEjb.recuperaErrore(idObject, Constants.StatoOggetto.IN_ATTESA_SCHED.name(),
		    Constants.StatoOggetto.IN_ATTESA_SCHED.name(), idTipoObject);
	    loadDettaglioObject(idObject);
	} catch (ParerUserError ex) {
	    getMessageBox().addError(ex.getDescription());
	}
	forwardToPublisher(getLastPublisher());
    }

    @Override
    public void impostaTuttiVerificato() throws Throwable {
	getForm().getFiltriVersamenti().post(getRequest());

	MonVLisVersFallitiTableBean listaVersErr = monitoraggioHelper
		.getMonVLisVersFallitiViewBean((MonitoraggioFiltriListaVersFallitiBean) getSession()
			.getAttribute("filtriListaVersFalliti"));

	int modificati = 0;

	Iterator<MonVLisVersFallitiRowBean> it = listaVersErr.iterator();
	while (it.hasNext()) {
	    MonVLisVersFallitiRowBean row = it.next();
	    BigDecimal idSessioneIngest = row.getBigDecimal(ID_SESSIONE_INGEST);
	    if (monitoraggioEjb.checkSessionObjectDaVerif(idSessioneIngest, true)
		    && monitoraggioEjb.canModifyVerificataNonRisolubile(idSessioneIngest)) {
		boolean flagVerificato = true;
		boolean flagNonRisolubile = row.getFlNonRisolub() != null
			&& row.getFlNonRisolub().equals(Constants.DB_TRUE);
		// punto a) dell'analisi
		String errore = monitoraggioEjb.impostaFlagVerificatoNonRisolubile(idSessioneIngest,
			flagVerificato, flagNonRisolubile);
		if (errore != null) {
		    getMessageBox().addMessage(new Message(MessageLevel.INF, errore));
		} else {
		    modificati++;
		}
	    } else {
		getMessageBox().addMessage(new Message(MessageLevel.INF,
			"Sessione " + idSessioneIngest + " non modificata."));
	    }
	}

	if (!getMessageBox().hasError()) {
	    reloadListaVersFalliti(modificati);
	}

	getMessageBox().setViewMode(ViewMode.plain);
	forwardToPublisher(Application.Publisher.VERSAMENTI_LIST);
    }

    @Override
    public void impostaTuttiNonRisolubile() throws Throwable {
	getForm().getFiltriVersamenti().post(getRequest());

	MonVLisVersFallitiTableBean listaVersErr = monitoraggioHelper
		.getMonVLisVersFallitiViewBean((MonitoraggioFiltriListaVersFallitiBean) getSession()
			.getAttribute("filtriListaVersFalliti"));

	int modificati = 0;

	Iterator<MonVLisVersFallitiRowBean> it = listaVersErr.iterator();
	while (it.hasNext()) {
	    MonVLisVersFallitiRowBean row = it.next();
	    BigDecimal idSessioneIngest = row.getBigDecimal(ID_SESSIONE_INGEST);
	    if (monitoraggioEjb.checkSessionObjectDaVerif(idSessioneIngest, true)
		    && monitoraggioEjb.canModifyVerificataNonRisolubile(idSessioneIngest)) {
		boolean flagVerificato = row.getFlVerif() != null
			&& row.getFlVerif().equals(Constants.DB_TRUE);
		boolean flagNonRisolubile = true;
		// punto a) dell'analisi
		String errore = monitoraggioEjb.impostaFlagVerificatoNonRisolubile(idSessioneIngest,
			flagVerificato, flagNonRisolubile);
		if (errore != null) {
		    getMessageBox().addMessage(new Message(MessageLevel.INF, errore));
		} else {
		    modificati++;
		}
	    } else {
		getMessageBox().addMessage(new Message(MessageLevel.INF,
			"Sessione " + idSessioneIngest + " non modificata."));
	    }
	}

	if (!getMessageBox().hasError()) {
	    reloadListaVersFalliti(modificati);
	}

	getMessageBox().setViewMode(ViewMode.plain);
	forwardToPublisher(Application.Publisher.VERSAMENTI_LIST);
    }

    private void reloadListaVersFalliti(int modificati) throws EMFError {
	try {
	    String flVerif = getForm().getFiltriVersamenti().getVersamento_fl_verif().parse();
	    int paginaCorrente = getForm().getVersamentiList().getTable().getCurrentPageIndex();
	    int inizio = getForm().getVersamentiList().getTable().getFirstRowPageIndex();

	    /*
	     * Aggiorno i filtri in sessione e rifaccio la query tenendo conto del filtro flaggozzo
	     */
	    MonitoraggioFiltriListaVersFallitiBean filtriListaVersFalliti = (MonitoraggioFiltriListaVersFallitiBean) getSession()
		    .getAttribute("filtriListaVersFalliti");
	    filtriListaVersFalliti.setVerificati(flVerif);
	    int pageSize = getForm().getVersamentiList().getTable().getPageSize();
	    getSession().setAttribute("filtriListaVersFalliti", filtriListaVersFalliti);
	    MonVLisVersFallitiTableBean listaVersErr = monitoraggioHelper
		    .getMonVLisVersFallitiViewBean(
			    (MonitoraggioFiltriListaVersFallitiBean) getSession()
				    .getAttribute("filtriListaVersFalliti"));
	    getForm().getVersamentiList().setTable(listaVersErr);
	    getForm().getVersamentiList().getTable().setPageSize(pageSize);
	    getForm().getVersamentiList().getTable().first();
	    // rieseguo la query se necessario
	    this.lazyLoadGoPage(getForm().getVersamentiList(), paginaCorrente);
	    // ritorno alla pagina
	    getForm().getVersamentiList().getTable().setCurrentRowIndex(inizio);
	    refreshLista(getForm().getVersamentiList(), listaVersErr);

	    // Rieseguo anche la query di Riepilogo Versamenti
	    calcolaTotaliRiepilogoVersamenti(filtriListaVersFalliti.getIdAmbienteVers(),
		    filtriListaVersFalliti.getIdVers(), filtriListaVersFalliti.getIdTipoObject());

	    getMessageBox().addMessage(
		    new Message(MessageLevel.INF, "Aggiornate " + modificati + " sessioni."));
	} catch (ParerUserError e) {
	    getMessageBox().addMessage(new Message(MessageLevel.ERR, e.getDescription()));
	}
    }

    private void reloadListaVersFallitiDaVersFalliti(BigDecimal idVers, String cdKeyObject,
	    String nmTipoObject, int modificati) throws EMFError {
	try {
	    String flVerifAggiornato = monitoraggioHelper.getObjNonVersFlVerif(idVers, cdKeyObject);
	    /* Controllo se adesso devo rendere editabile il flag fl_vers_sacer_da_recup */
	    boolean isFlVersSacerDaRecupEditable = monitoraggioEjb.isFlVersSacerDaRecupEditable(
		    idVers, cdKeyObject, nmTipoObject, flVerifAggiornato);
	    // Setta lo status (editabile o meno) del flag versamento a SACER da recuperare
	    setStatusFlVersSacerDaRecup(isFlVersSacerDaRecupEditable);

	    // Carico il rowbean del dettaglio oggetti derivanti da versamenti falliti
	    MonVVisObjNonVersRowBean objNonVersRB = monitoraggioHelper
		    .getMonVVisObjNonVersRowBean(idVers, cdKeyObject);
	    getForm().getOggettoDaVersamentiFallitiDetail().copyFromBean(objNonVersRB);

	    // Carico la lista versamenti
	    MonVLisVersObjNonVersTableBean versObjNonVersTB = monitoraggioHelper
		    .getMonVLisVersObjNonVersViewBean(idVers, cdKeyObject);
	    getForm().getOggettoDaVersamentiFallitiDetailVersamentiList()
		    .setTable(versObjNonVersTB);
	    getForm().getOggettoDaVersamentiFallitiDetailVersamentiList().getTable()
		    .setPageSize(10);
	    getForm().getOggettoDaVersamentiFallitiDetailVersamentiList().getTable().first();

	    getMessageBox().addMessage(
		    new Message(MessageLevel.INF, "Aggiornate " + modificati + " sessioni."));
	} catch (ParerUserError e) {
	    getMessageBox().addMessage(new Message(MessageLevel.ERR, e.getDescription()));
	} catch (Exception ex) {
	    log.error("reloadListaVersFallitiDaVersFalliti - " + ex.getMessage(), ex);
	    getMessageBox().addMessage(new Message(MessageLevel.ERR, ex.getMessage()));
	}
    }

    // MEV 26715
    @Override
    public void impostaTuttiNonRisolubOggettiDaVersFalliti() throws Throwable {
	if (!getForm().getOggettiDaVersamentiFallitiList().getStatus().equals(Status.update)) {
	    getRequest().setAttribute("confermaImpostaTuttiNonRisolubOggettiDaVersFallitiAction",
		    true);
	} else {
	    getMessageBox().addMessage(new Message(MessageLevel.WAR,
		    "Attenzione: non è possibile impostare i versamenti verificati/non risolubili "
			    + "finché si sta modificando il dettaglio oggetto!"));
	}

	forwardToPublisher(getLastPublisher());
    }

    public void impostaTuttiNonRisolubOggettiDaVersFallitiAction() throws Throwable {
	/* Se NON sono in status di "update" su altri campi */
	if (!getForm().getOggettiDaVersamentiFallitiList().getStatus().equals(Status.update)) {
	    /* Recupero informazioni sull'oggetto */
	    BigDecimal idVers = getForm().getOggettoDaVersamentiFallitiDetail().getId_vers()
		    .parse();
	    String cdKeyObject = getForm().getOggettoDaVersamentiFallitiDetail().getCd_key_object()
		    .parse();
	    String nmTipoObject = getForm().getOggettoDaVersamentiFallitiDetail()
		    .getNm_tipo_object().parse();

	    MonVLisVersObjNonVersTableBean versObjNonVersTB = monitoraggioHelper
		    .getMonVLisVersObjNonVersViewBean(idVers, cdKeyObject);
	    int modificati = 0;

	    Iterator<MonVLisVersObjNonVersRowBean> it = versObjNonVersTB.iterator();
	    while (it.hasNext()) {
		MonVLisVersObjNonVersRowBean row = it.next();

		BigDecimal idSessioneIngest = row.getBigDecimal(ID_SESSIONE_INGEST);
		if (monitoraggioEjb.checkSessionObjectDaVerif(idSessioneIngest, true)
			&& monitoraggioEjb.canModifyVerificataNonRisolubile(idSessioneIngest)) {
		    boolean flagVerificato = row.getFlVerif() != null
			    && row.getFlVerif().equals(Constants.DB_TRUE);
		    boolean flagNonRisolubile = true;
		    // punto a) dell'analisi
		    String errore = monitoraggioEjb.impostaFlagVerificatoNonRisolubile(
			    idSessioneIngest, flagVerificato, flagNonRisolubile);
		    if (errore != null) {
			getMessageBox().addMessage(new Message(MessageLevel.INF, errore));
		    } else {
			modificati++;
		    }
		} else {
		    getMessageBox().addMessage(new Message(MessageLevel.INF,
			    "Sessione " + idSessioneIngest + " non modificata."));
		}
	    }

	    /* CONTROLLI DI COERENZA SOLO SUGLI ELEMENTI MODIFICATI */
	    if (!getMessageBox().hasError()) {
		reloadListaVersFallitiDaVersFalliti(idVers, cdKeyObject, nmTipoObject, modificati);
	    }
	    // MAC#24891: Correzione della funzionalità per settare a non risolubile una sessione
	    // annullata
	    // Non ricaricava i dati nuovi della maschera e il tasto annulla poteva non comparire
	    caricaDettaglioProvenendoDaListaVersamentiFalliti();
	} else {
	    getMessageBox().addMessage(new Message(MessageLevel.WAR,
		    "Attenzione: non è possibile impostare i versamenti verificati/non risolubili "
			    + "finché si sta modificando il dettaglio oggetto!"));
	}

	getMessageBox().setViewMode(ViewMode.plain);
	forwardToPublisher(Application.Publisher.OGGETTO_DA_VERSAMENTI_FALLITI_DETAIL);
    }

    @Override
    public void impostaTuttiVerificOggettiDaVersFalliti() throws Throwable {
	/* Se NON sono in status di "update" su altri campi */
	if (!getForm().getOggettiDaVersamentiFallitiList().getStatus().equals(Status.update)) {
	    /* Recupero informazioni sull'oggetto */
	    BigDecimal idVers = getForm().getOggettoDaVersamentiFallitiDetail().getId_vers()
		    .parse();
	    String cdKeyObject = getForm().getOggettoDaVersamentiFallitiDetail().getCd_key_object()
		    .parse();
	    String nmTipoObject = getForm().getOggettoDaVersamentiFallitiDetail()
		    .getNm_tipo_object().parse();

	    MonVLisVersObjNonVersTableBean versObjNonVersTB = monitoraggioHelper
		    .getMonVLisVersObjNonVersViewBean(idVers, cdKeyObject);
	    int modificati = 0;

	    Iterator<MonVLisVersObjNonVersRowBean> it = versObjNonVersTB.iterator();
	    while (it.hasNext()) {
		MonVLisVersObjNonVersRowBean row = it.next();

		BigDecimal idSessioneIngest = row.getBigDecimal(ID_SESSIONE_INGEST);
		if (monitoraggioEjb.checkSessionObjectDaVerif(idSessioneIngest, true)
			&& monitoraggioEjb.canModifyVerificataNonRisolubile(idSessioneIngest)) {
		    boolean flagVerificato = true;
		    boolean flagNonRisolubile = row.getFlNonRisolub() != null
			    && row.getFlNonRisolub().equals(Constants.DB_TRUE);
		    // punto a) dell'analisi
		    String errore = monitoraggioEjb.impostaFlagVerificatoNonRisolubile(
			    idSessioneIngest, flagVerificato, flagNonRisolubile);
		    if (errore != null) {
			getMessageBox().addMessage(new Message(MessageLevel.INF, errore));
		    } else {
			modificati++;
		    }
		} else {
		    getMessageBox().addMessage(new Message(MessageLevel.INF,
			    "Sessione " + idSessioneIngest + " non modificata."));
		}
	    }

	    /* CONTROLLI DI COERENZA SOLO SUGLI ELEMENTI MODIFICATI */
	    if (!getMessageBox().hasError()) {
		reloadListaVersFallitiDaVersFalliti(idVers, cdKeyObject, nmTipoObject, modificati);
	    }
	    // MAC#24891: Correzione della funzionalità per settare a non risolubile una sessione
	    // annullata
	    // Non ricaricava i dati nuovi della maschera e il tasto annulla poteva non comparire
	    caricaDettaglioProvenendoDaListaVersamentiFalliti();
	} else {
	    getMessageBox().addMessage(new Message(MessageLevel.WAR,
		    "Attenzione: non è possibile impostare i versamenti verificati/non risolubili "
			    + "finché si sta modificando il dettaglio oggetto!"));
	}

	getMessageBox().setViewMode(ViewMode.plain);
	forwardToPublisher(Application.Publisher.OGGETTO_DA_VERSAMENTI_FALLITI_DETAIL);

    }

    private enum TiRecuperoErrore {
	TRASFORMAZIONE, VERSAMENTO_A_PING
    }

    public void confermaModificaTipoOggetto() throws EMFError {
	getForm().getOggettoDetail().getId_tipo_object().post(getRequest());

	BigDecimal idObject = getForm().getOggettoDetail().getId_object().parse();
	BigDecimal idTipoObject = getForm().getOggettoDetail().getId_tipo_object().parse();
	monitoraggioEjb.modificaTipoOggetto(idObject, idTipoObject);
    }

    public void confermaRecuperoErrore() throws EMFError {
	getForm().getOggettoDetail().getTi_stato_popup().post(getRequest());
	getForm().getOggettoDetail().getTi_recupero().post(getRequest());
	getForm().getOggettoDetail().getId_tipo_object().post(getRequest());
	recuperoErrore();
    }

    public void confermaSetAnnullatoDaTrasformare() throws EMFError {
	getForm().getOggettoDetail().getTi_stato_popup().post(getRequest());
	getForm().getOggettoDetail().getId_tipo_object().post(getRequest());
	BigDecimal idObject = getForm().getOggettoDetail().getId_object().parse();
	BigDecimal idTipoObject = getForm().getOggettoDetail().getId_tipo_object().parse();
	try {
	    annullamentoEjb.setAnnullatoInDaTrasformare(idObject, idTipoObject);
	    loadDettaglioObject(idObject);
	} catch (Exception ex) {
	    getMessageBox().addError(ex.getMessage());
	}
	redirectToAjax(getForm().getOggettoDetail().asJSON());
    }

    private void recuperoErrore() throws EMFError {
	String tiStato = getForm().getOggettoDetail().getTi_stato_popup().parse();
	String tiRecupero = getForm().getOggettoDetail().getTi_recupero().parse();
	BigDecimal idObject = getForm().getOggettoDetail().getId_object().parse();
	BigDecimal idTipoObject = getForm().getOggettoDetail().getId_tipo_object().parse();
	try {
	    if (tiRecupero.equals(TiRecuperoErrore.TRASFORMAZIONE.name())) {
		if (StringUtils.isNotBlank(tiStato)) {
		    monitoraggioEjb.recuperaErrore(idObject, tiStato,
			    Constants.StatoOggetto.CHIUSO_ERR_TRASFORMAZIONE.name(), idTipoObject);
		}
	    } else if (tiRecupero.equals(TiRecuperoErrore.VERSAMENTO_A_PING.name())) {
		if (StringUtils.isNotBlank(tiStato)) {
		    monitoraggioEjb.recuperaErrore(idObject, tiStato,
			    Constants.StatoOggetto.CHIUSO_ERR_VERSAMENTO_A_PING.name(),
			    idTipoObject);
		}
	    }
	    loadDettaglioObject(idObject);
	} catch (ParerUserError ex) {
	    getMessageBox().addError(ex.getDescription());
	}
	// non venivano aggiornate le liste ma solo il dettaglio
	redirectToAjax(getForm().getOggettoDetail().asJSON());
    }

    @Override
    public void recuperoErrVersamentoPing() throws EMFError {
	/*
	 * mostro un popup con gli stati possibili (DA_TRASFORMARE, TRASFORMATO,
	 * CHIUSO_ERR_VERSAMENTO_A_PING)
	 */
	getForm().getOggettoDetail().getTi_stato_popup().setEditMode();
	getForm().getOggettoDetail().getTi_recupero()
		.setValue(TiRecuperoErrore.VERSAMENTO_A_PING.name());
	if (getForm().getOggettoDetail().getTi_gest_oggetti_figli().parse()
		.equals(Constants.TipoGestioneOggettiFigli.AUTOMATICA.name())) {
	    getForm().getOggettoDetail().getTi_stato_popup()
		    .setDecodeMap(ComboGetter.getMappaSortedGenericEnum("ti_stato",
			    Constants.StatoOggetto.DA_TRASFORMARE,
			    Constants.StatoOggetto.TRASFORMATO,
			    Constants.StatoOggetto.CHIUSO_ERR_VERSAMENTO_A_PING));
	} else {
	    getForm().getOggettoDetail().getTi_stato_popup()
		    .setDecodeMap(ComboGetter.getMappaSortedGenericEnum("ti_stato",
			    Constants.StatoOggetto.DA_TRASFORMARE,
			    Constants.StatoOggetto.CHIUSO_ERR_VERSAMENTO_A_PING));
	}
	getRequest().setAttribute("confermaRecuperoErrore", true);
	forwardToPublisher(Application.Publisher.OGGETTO_DETAIL);

    }

    private enum HmKey {

	OGGI, ULTIMI_6, ULTIMI_7, PREC_ULTIMI_6, TOT
    }

    private DecodeMap getMappaPeriodoVers() {
	BaseTable bt = new BaseTable();
	BaseRow br = new BaseRow();
	BaseRow br1 = new BaseRow();
	BaseRow br2 = new BaseRow();
	DecodeMap mappaPeriodoVers = new DecodeMap();
	br.setString("periodo_k", "OGGI");
	br.setString("periodo_v", "Oggi");
	bt.add(br);
	br1.setString("periodo_k", "ULTIMI7");
	br1.setString("periodo_v", "Ultimi 7 giorni");
	bt.add(br1);
	br2.setString("periodo_k", "TUTTI");
	br2.setString("periodo_v", "Tutti");
	bt.add(br2);
	mappaPeriodoVers.populatedMap(bt, "periodo_k", "periodo_v");
	return mappaPeriodoVers;
    }

    private DecodeMap getMappaStatoOggetto() {
	return ComboGetter.getMappaSortedGenericEnum("ti_stato_doc",
		Constants.StatoOggetto.getStatiOggettoMonitoraggioListaOggetti());
    }

    private DecodeMap getMappaStatoRisoluzione() {
	return ComboGetter.getMappaOrdinalGenericEnum("ti_stato_risoluz",
		WebConstants.tiStatoRisoluz.getStatiRisoluzione());
    }

    private void populateFiltriJob() {
	getForm().getFiltriJobSchedulati().getNm_job().setDecodeMap(ComboGetter
		.getMappaSortedGenericEnum("nm_job", Constants.NomiJob.getComboSchedulazioniJob()));

	// Inserisco il valore di default nel campo data
	Calendar cal = Calendar.getInstance();
	SimpleDateFormat df = new SimpleDateFormat(Constants.DATE_FORMAT);
	getForm().getFiltriJobSchedulati().getDt_reg_log_job_da()
		.setValue(df.format(cal.getTime()));
    }

    @Override
    public void ricercaRepliche() throws EMFError {
	getForm().getFiltriReplicaOrg().getRicercaRepliche().setDisableHourGlass(true);
	FiltriReplicaOrg filtri = getForm().getFiltriReplicaOrg();
	filtri.post(getRequest());
	if (filtri.validate(getMessageBox())) {

	    IamVLisOrganizDaReplicTableBean organizReplicaTB = monitoraggioHelper
		    .getIamVLisOrganizDaReplicTableBean(filtri);
	    getForm().getReplicaOrgList().setTable(organizReplicaTB);
	    getForm().getReplicaOrgList().getTable().setPageSize(10);
	    getForm().getReplicaOrgList().getTable().first();
	}
	forwardToPublisher(Application.Publisher.REPLICA_ORG_LIST);
    }

    @Override
    public void pulisciRepliche() throws EMFError {
	resetReplicaOrganizzazioniPage();
	forwardToPublisher(Application.Publisher.REPLICA_ORG_LIST);
    }

    @Secure(action = "Menu.Monitoraggio.VisualizzaReplicheOrganizzazioni")
    public void replicaOrganizzazioniPage() throws EMFError {
	getUser().getMenu().reset();
	getUser().getMenu().select("Menu.Monitoraggio.VisualizzaReplicheOrganizzazioni");

	resetReplicaOrganizzazioniPage();
	forwardToPublisher(Application.Publisher.REPLICA_ORG_LIST);
    }

    public void resetReplicaOrganizzazioniPage() throws EMFError {
	getForm().getFiltriReplicaOrg().setEditMode();
	getForm().getFiltriReplicaOrg().reset();
	// Creo il tablebean riferito alla combo di Ambiente Versatore
	PigAmbienteVersTableBean ambienteVersTableBean = null;

	try {
	    // Ricavo i valori della combo AMBIENTE_VERS dalla tabella ORG_AMBIENTE
	    ambienteVersTableBean = comboHelper
		    .getAmbienteVersatoreFromUtente(getUser().getIdUtente());
	} catch (Exception ex) {
	    log.error("Errore nel recupero ambiente versatore", ex);
	}
	DecodeMap mappaAmbienteVers = new DecodeMap();
	mappaAmbienteVers.populatedMap(ambienteVersTableBean, "id_ambiente_vers",
		"nm_ambiente_vers");
	getForm().getFiltriReplicaOrg().getId_ambiente_vers().setDecodeMap(mappaAmbienteVers);

	/*
	 * Se ho un solo ambiente lo setto giÃ  impostato nella combo e procedo con i controlli
	 * successivi
	 */
	if (ambienteVersTableBean.size() == 1) {
	    getForm().getFiltriReplicaOrg().getId_ambiente_vers()
		    .setValue(ambienteVersTableBean.getRow(0).getIdAmbienteVers().toString());
	    BigDecimal idAmbiente = ambienteVersTableBean.getRow(0).getIdAmbienteVers();
	    checkUniqueAmbienteVersatoreInCombo(idAmbiente,
		    WebConstants.SezioneMonitoraggio.REPLICA_ORG);
	} /*
	   * altrimenti imposto la combo ambiente versatore con i diversi valori ma senza averne
	   * selezionato uno in particolare e imposto vuota la combo versatore
	   */ else {
	    getForm().getFiltriReplicaOrg().getId_vers().setDecodeMap(new DecodeMap());
	}

	// Popolo le combo "Tipo operatore" e "StatoOggetto replica"
	getForm().getFiltriReplicaOrg().getTi_oper_replic()
		.setDecodeMap(ComboGetter.getMappaTiOperReplic());
	getForm().getFiltriReplicaOrg().getTi_stato_replic()
		.setDecodeMap(ComboGetter.getMappaTiStatoReplic());

	getForm().getReplicaOrgList().clear();
    }

    /**
     * Metodo utilizzato per controllare il valore nella combo ambiente versatore quando questo è
     * l'unico presente e settare di conseguenza la combo versatore
     *
     * @param idAmbienteVers id ambiente versamento
     * @param sezione        sesione (enumerativo)
     *
     * @throws EMFError errore generico
     */
    public void checkUniqueAmbienteVersatoreInCombo(BigDecimal idAmbienteVers, Enum sezione)
	    throws EMFError {
	if (idAmbienteVers != null) {
	    // Ricavo il TableBean relativo ai versatori dipendenti dall'ambiente versatore scelto
	    PigVersTableBean tmpTableBeanVers = comboHelper
		    .getVersatoreFromAmbienteVersatore(getUser().getIdUtente(), idAmbienteVers);
	    DecodeMap mappaVers = new DecodeMap();
	    mappaVers.populatedMap(tmpTableBeanVers, "id_vers", "nm_vers");

	    if (sezione.equals(WebConstants.SezioneMonitoraggio.REPLICA_ORG)) {
		getForm().getFiltriReplicaOrg().getId_vers().setDecodeMap(mappaVers);
	    }

	    // Se la combo versatore ha un solo valore presente, lo imposto e faccio controllo su di
	    // essa
	    if (tmpTableBeanVers.size() == 1) {
		if (sezione.equals(WebConstants.SezioneMonitoraggio.REPLICA_ORG)) {
		    getForm().getFiltriReplicaOrg().getId_vers()
			    .setValue(tmpTableBeanVers.getRow(0).getIdVers().toString());
		}
	    }
	}
    }

    @Override
    public JSONObject triggerFiltriReplicaOrgId_ambiente_versOnTrigger() throws EMFError {
	triggerAmbienteVersatoreGenerico(getForm().getFiltriReplicaOrg(),
		WebConstants.SezioneMonitoraggio.REPLICA_ORG);
	return getForm().getFiltriReplicaOrg().asJSON();
    }

    public void showOggettoVersatoAPing() throws EMFError {
	setTableName(getForm().getOggettoDetailOggettiTrasfList().getName());
	setRiga(getRequest().getParameter("riga"));

	getForm().getOggettoDetailOggettiTrasfList().getTable()
		.setCurrentRowIndex(Integer.parseInt(getRiga()));
	MonVLisObjTrasfRowBean row = (MonVLisObjTrasfRowBean) getForm()
		.getOggettoDetailOggettiTrasfList().getTable().getCurrentRow();
	loadOggettoVersatoAPing(row.getIdVersTrasf(), row.getCdKeyObjectTrasf());
    }

    private void loadOggettoVersatoAPing(BigDecimal idVersTrasf, String cdKeyObjectTrasf)
	    throws EMFError {
	PigObjectRowBean oggettoVersato = monitoraggioHelper.getPigObjectRowBean(idVersTrasf,
		cdKeyObjectTrasf);
	try {
	    if (oggettoVersato != null && oggettoVersato.getIdObject() != null) {
		List<BigDecimal> idObjStack = getIdObjStack();
		idObjStack.add(oggettoVersato.getIdObject());
		getSession().setAttribute(GET_ID_OBJ_STACK, idObjStack);
		getSession().setAttribute(ID_OBJECT, oggettoVersato.getIdObject());

		loadDettaglioObject(oggettoVersato.getIdObject());
		forwardToPublisher(Application.Publisher.OGGETTO_DETAIL);
	    } else {
		getMessageBox().addError(
			"Errore inatteso nella visualizzazione dell'oggetto versato a PING");
		forwardToPublisher(getLastPublisher());
	    }
	} catch (ParerUserError ex) {
	    getMessageBox().addError(ex.getDescription());
	    forwardToPublisher(getLastPublisher());
	}
    }

    @Override
    public void filtraUnitaDocVers() throws EMFError {
	BigDecimal idSessioneIngest = getForm().getVersamentoDetail().getId_sessione_ingest()
		.parse();
	if (idSessioneIngest != null && getForm().getFiltriUnitaDocVers()
		.postAndValidate(getRequest(), getMessageBox())) {
	    BigDecimal aaUnitaDoc = getForm().getFiltriUnitaDocVers().getAa_unita_doc_sacer()
		    .parse();
	    String cdKeyUnitaDoc = getForm().getFiltriUnitaDocVers().getCd_key_unita_doc_sacer()
		    .parse();
	    String cdRegistroUnitaDoc = getForm().getFiltriUnitaDocVers()
		    .getCd_registro_unita_doc_sacer().parse();
	    String tiStatoUnitaDoc = getForm().getFiltriUnitaDocVers()
		    .getTi_stato_unita_doc_object().parse();
	    String cdErrore = getForm().getFiltriUnitaDocVers().getCd_concat_dl_err_sacer().parse();
	    String flStrutturaNonDefinita = getForm().getFiltriUnitaDocVers()
		    .getFl_struttura_non_definita().parse();
	    String flVersSimulato = getForm().getFiltriUnitaDocVers().getFl_vers_simulato().parse();
	    String nmStrut = getForm().getFiltriUnitaDocVers().getNm_strut().parse();

	    int pageSize = 10;
	    if (getForm().getUnitaDocDaVersamentiFallitiList().getTable() != null) {
		pageSize = getForm().getUnitaDocDaVersamentiFallitiList().getTable().getPageSize();
	    }
	    // Carico la lista unità  doc.
	    MonVLisUnitaDocSessioneTableBean udSesTB = monitoraggioHelper
		    .getMonVLisUnitaDocSessioneTableBean(idSessioneIngest, cdRegistroUnitaDoc,
			    aaUnitaDoc, cdKeyUnitaDoc, tiStatoUnitaDoc, cdErrore, nmStrut,
			    flStrutturaNonDefinita, flVersSimulato);
	    getForm().getUnitaDocDaVersamentiFallitiList().setTable(udSesTB);
	    getForm().getUnitaDocDaVersamentiFallitiList().getTable().setPageSize(pageSize);
	    getForm().getUnitaDocDaVersamentiFallitiList().getTable().first();
	}
	forwardToPublisher(getLastPublisher());
    }

    @Override
    public void settaDaTrasformareDetail() throws EMFError {
	/*
	 * MEV#15178 - Modifica Tipo oggetto in dettaglio oggetto per gli oggetti da trasformare
	 *
	 */
	PigTipoObjectTableBean tipoObjectTableBean = comboHelper.getTipoObjectFromVersatore(
		getUser().getIdUtente(), getForm().getOggettoDetail().getId_vers().parse(),
		Constants.TipoVersamento.DA_TRASFORMARE.name());
	getForm().getOggettoDetail().getId_tipo_object().setDecodeMap(DecodeMap.Factory
		.newInstance(tipoObjectTableBean, "id_tipo_object", "nm_tipo_object"));
	BigDecimal idTipoObject = getForm().getOggettoDetail().getId_tipo_object().parse();
	getForm().getOggettoDetail().getId_tipo_object()
		.setValue(idTipoObject.longValueExact() + "");

	getRequest().setAttribute("setAnnullatoInDaTrasformare", true);
	forwardToPublisher(Application.Publisher.OGGETTO_DETAIL);

	getForm().getOggettoDetail().getTi_stato_popup().setEditMode();
	getForm().getOggettoDetail().getTi_stato_popup().setDecodeMap(ComboGetter
		.getMappaSortedGenericEnum("ti_stato", Constants.StatoOggetto.DA_TRASFORMARE));

	PigTipoObject pigTipoObject = monitoraggioHelper.findById(PigTipoObject.class,
		idTipoObject);
	if (pigTipoObject.getFlNoVisibVersOgg() == null
		|| pigTipoObject.getFlNoVisibVersOgg().equals("0")) {
	    getForm().getOggettoDetail().getId_tipo_object().setEditMode();
	}

    }

    @Override
    public void update(Fields<Field> fields) throws EMFError {
	updateOggettiList();
	forwardToPublisher(Application.Publisher.OGGETTO_DETAIL);

    }

    @Override
    public void editUnitaDocumentaria() throws EMFError {
	updateUnitaDocDetail();
	getForm().getUnitaDocDetail().getEditUnitaDocumentaria().setViewMode();
	getForm().getUnitaDocDetail().getEditUnitaDocumentaria().setHidden(true);
	getForm().getUnitaDocDetail().getUndoUnitaDocumentaria().setEditMode();
	getForm().getUnitaDocDetail().getSaveUnitaDocumentaria().setEditMode();
	getForm().getUnitaDocDetail().getUndoUnitaDocumentaria().setHidden(false);
	getForm().getUnitaDocDetail().getSaveUnitaDocumentaria().setHidden(false);

	forwardToPublisher(Application.Publisher.UNITA_DOC_DETAIL);
    }

    // MEV 33098
    @Override
    public void undoUnitaDocumentaria() throws EMFError {
	undoDettaglio();
	getForm().getUnitaDocDetail().getEditUnitaDocumentaria().setEditMode();
	getForm().getUnitaDocDetail().getUndoUnitaDocumentaria().setViewMode();
	getForm().getUnitaDocDetail().getSaveUnitaDocumentaria().setViewMode();
	getForm().getUnitaDocDetail().getEditUnitaDocumentaria().setHidden(false);
	getForm().getUnitaDocDetail().getUndoUnitaDocumentaria().setHidden(true);
	getForm().getUnitaDocDetail().getSaveUnitaDocumentaria().setHidden(true);
    }

    // MEV 33098
    @Override
    public void saveUnitaDocumentaria() throws EMFError {
	saveDettaglio();
	getForm().getUnitaDocDetail().getEditUnitaDocumentaria().setEditMode();
	getForm().getUnitaDocDetail().getUndoUnitaDocumentaria().setViewMode();
	getForm().getUnitaDocDetail().getSaveUnitaDocumentaria().setViewMode();
	getForm().getUnitaDocDetail().getEditUnitaDocumentaria().setHidden(false);
	getForm().getUnitaDocDetail().getUndoUnitaDocumentaria().setHidden(true);
	getForm().getUnitaDocDetail().getSaveUnitaDocumentaria().setHidden(true);
    }

    public void scaricaReport() {
	String report = "";

	BigDecimal idSessione = getForm().getOggettoDetailSessioniList().getTable().getCurrentRow()
		.getBigDecimal(ID_SESSIONE_INGEST);
	try {
	    report = trasformazioniHelper.getSessionReport(idSessione);
	} catch (IOException | TransformerException | ObjectStorageException ex) {
	    getMessageBox().addError("Report non disponibile.");
	}

	if (!getMessageBox().hasError()) {
	    String filename = "report-" + idSessione.toPlainString() + ".xml";

	    File tmpFile = new File(System.getProperty("java.io.tmpdir"), filename);
	    FileOutputStream file = null;
	    InputStream is = null;
	    try {
		byte[] fileBytes = report.getBytes();
		if (fileBytes != null) {

		    file = new FileOutputStream(tmpFile);
		    is = new ByteArrayInputStream(fileBytes);
		    byte[] data = new byte[1024];
		    int count;
		    while ((count = is.read(data, 0, 1024)) != -1) {
			file.write(data, 0, count);
		    }

		    getRequest().setAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_ACTION.name(),
			    getControllerName());
		    getSession().setAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_FILENAME.name(),
			    tmpFile.getName());
		    getSession().setAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_FILEPATH.name(),
			    tmpFile.getPath());
		    getSession().setAttribute(
			    WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_DELETEFILE.name(),
			    Boolean.toString(true));
		    getSession().setAttribute(
			    WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_CONTENTTYPE.name(),
			    ContentType.APPLICATION_XML.getMimeType());
		}
	    } catch (IOException ex) {
		log.error("Errore in download " + ExceptionUtils.getRootCauseMessage(ex), ex);
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

    public void download() throws EMFError, IOException {
	String filename = (String) getSession().getAttribute(
		it.eng.xformer.web.util.WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_FILENAME.name());
	String path = (String) getSession().getAttribute(
		it.eng.xformer.web.util.WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_FILEPATH.name());
	Boolean deleteFile = Boolean.parseBoolean((String) getSession().getAttribute(
		it.eng.xformer.web.util.WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_DELETEFILE.name()));
	String contentType = (String) getSession().getAttribute(
		it.eng.xformer.web.util.WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_CONTENTTYPE.name());
	if (path != null && filename != null) {
	    File fileToDownload = new File(path);
	    if (fileToDownload.exists()) {
		/*
		 * Definiamo l'output previsto che sarÃ  un file in formato zip di cui si occuperÃ 
		 * la servlet per fare il download
		 */
		OutputStream outUD = getServletOutputStream();
		getResponse().setContentType(
			StringUtils.isBlank(contentType) ? "application/zip" : contentType);
		getResponse().setHeader("Content-Disposition",
			"attachment; filename=\"" + filename);

		FileInputStream inputStream = null;
		try {
		    getResponse().setHeader("Content-Length",
			    String.valueOf(fileToDownload.length()));
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
		    FileUtils.deleteQuietly(fileToDownload);
		}
	    } else {
		getMessageBox()
			.addError("Errore durante il tentativo di download. File non trovato");
		forwardToPublisher(getLastPublisher());
	    }
	} else {
	    getMessageBox().addError("Errore durante il tentativo di download. File non trovato");
	    forwardToPublisher(getLastPublisher());
	}
	getSession().removeAttribute(
		it.eng.xformer.web.util.WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_FILENAME.name());
	getSession().removeAttribute(
		it.eng.xformer.web.util.WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_FILEPATH.name());
	getSession().removeAttribute(
		it.eng.xformer.web.util.WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_DELETEFILE.name());
	getSession().removeAttribute(
		it.eng.xformer.web.util.WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_CONTENTTYPE.name());
    }

    @Override
    public void settaErroreTrasformazioneDetail() throws Throwable {
	getRequest().setAttribute("setErroreTrasformazione", true);
	forwardToPublisher(Application.Publisher.OGGETTO_DETAIL);
    }

    public void confermaSettaErroreTrasformazione() throws EMFError {
	BigDecimal idObject = getForm().getOggettoDetail().getId_object().parse();
	try {
	    annullamentoEjb.setErroreTrasformazione(idObject);
	    loadDettaglioObject(idObject);
	} catch (Exception ex) {
	    getMessageBox().addError(ex.getMessage());
	}
	redirectToAjax(getForm().getOggettoDetail().asJSON());
    }

    // MEV 30208
    @Override
    public void settaChiusoErrVersamento() throws Throwable {
	getRequest().setAttribute("setChiusoErrVersamento", true);
	forwardToPublisher(Application.Publisher.OGGETTO_DETAIL);
    }

    public void confermaSettaChiusoErrVersamento() throws EMFError {
	BigDecimal idObject = getForm().getOggettoDetail().getId_object().parse();
	try {
	    annullamentoEjb.setChiusoErroreVers(idObject);
	    loadDettaglioObject(idObject);
	} catch (Exception ex) {
	    getMessageBox().addError(ex.getMessage());
	}
	redirectToAjax(getForm().getOggettoDetail().asJSON());
    }

    // MAC27423 - spostata qui per comodità
    private void calcolaTotaliRiepilogoVersamentiPerReload() throws ParerUserError, EMFError {
	// Rieseguo il calcolo dei totali di Riepilogo Versamenti
	BigDecimal idAmbienteVers = getForm().getFiltriRiepilogoVersamenti().getId_ambiente_vers()
		.parse();
	BigDecimal idVers = getForm().getFiltriRiepilogoVersamenti().getId_vers().parse();
	BigDecimal idTipoObject = getForm().getFiltriRiepilogoVersamenti().getId_tipo_object()
		.parse();
	calcolaTotaliRiepilogoVersamenti(idAmbienteVers, idVers, idTipoObject);
    }

    public void ricercaJobSchedulatiDaGestioneJob() throws EMFError {
	ricercaJobSchedulati();
    }

    private boolean existsCartellaOggetto(String rootDir, String ftpPath, String ftpDir) {
	return new File(rootDir + ftpPath + ftpDir).exists();
    }

    @Override
    public void gestioneJobPage() throws EMFError {
	GestioneJobForm form = new GestioneJobForm();
	form.getGestioneJobRicercaFiltri().setEditMode();
	form.getGestioneJobRicercaFiltri().reset();
	form.getGestioneJobRicercaList().setTable(null);
	BaseTable ambitoTableBean = gestioneJobEjb.getAmbitoJob();
	form.getGestioneJobRicercaFiltri().getNm_ambito().setDecodeMap(
		DecodeMap.Factory.newInstance(ambitoTableBean, "nm_ambito", "nm_ambito"));
	form.getGestioneJobRicercaFiltri().getTi_stato_job()
		.setDecodeMap(ComboGetter.getMappaTiStatoJob());
	getForm().getFiltriJobSchedulati().post(getRequest());
	String nmJob = getForm().getFiltriJobSchedulati().getNm_job().parse();
	if (nmJob != null) {
	    String dsJob = gestioneJobEjb.getDsJob(nmJob);
	    form.getGestioneJobRicercaFiltri().getDs_job().setValue(dsJob);
	}
	getSession().setAttribute("fromSchedulazioniJob", true);
	form.getGestioneJobRicercaFiltri().setEditMode();
	redirectToAction(Application.Actions.GESTIONE_JOB, "?operation=ricercaGestioneJob", form);
    }

    private enum OPERAZIONE {
	START("lancio il timer"), ESECUZIONE_SINGOLA("esecuzione singola"), STOP("stop");

	protected String desc;

	OPERAZIONE(String desc) {
	    this.desc = desc;
	}

	public String description() {
	    return desc;
	}
    }

    @Override
    public void startJobSchedulati() throws EMFError {
	// Eseguo lo start del job interessato
	getForm().getFiltriJobSchedulati().post(getRequest());
	String nmJob = getForm().getFiltriJobSchedulati().getNm_job().parse();
	if (nmJob != null) {
	    String dsJob = gestioneJobEjb.getDsJob(nmJob);
	    startGestioneJobOperation(nmJob, dsJob);
	} else {
	    getMessageBox().addWarning(WARNING_NO_JOB_SELEZIONATO);
	    forwardToPublisher(getLastPublisher());
	}
    }

    @Override
    public void stopJobSchedulati() throws EMFError {
	// Eseguo lo start del job interessato
	getForm().getFiltriJobSchedulati().post(getRequest());
	String nmJob = getForm().getFiltriJobSchedulati().getNm_job().parse();
	if (nmJob != null) {
	    String dsJob = gestioneJobEjb.getDsJob(nmJob);
	    stopGestioneJobOperation(nmJob, dsJob);
	} else {
	    getMessageBox().addWarning(WARNING_NO_JOB_SELEZIONATO);
	    forwardToPublisher(getLastPublisher());
	}
    }

    @Override
    public void esecuzioneSingolaJobSchedulati() throws EMFError {
	// Eseguo lo start del job interessato
	getForm().getFiltriJobSchedulati().post(getRequest());
	String nmJob = getForm().getFiltriJobSchedulati().getNm_job().parse();
	if (nmJob != null) {
	    String dsJob = gestioneJobEjb.getDsJob(nmJob);
	    esecuzioneSingolaGestioneJobOperation(nmJob, dsJob);
	} else {
	    getMessageBox().addWarning(WARNING_NO_JOB_SELEZIONATO);
	    forwardToPublisher(getLastPublisher());
	}
    }

    public void startGestioneJobOperation(String nmJob, String dsJob) {
	// Se il JOB è di tipo NO_TIMER in ogni caso il tasto di START va inibito
	if (gestioneJobEjb.isNoTimerJob(nmJob)) {
	    getMessageBox().addWarning(
		    "Attenzione: si sta tentando di schedulare un JOB di tipo NO_TIMER. Operazione non consentita");
	    forwardToPublisher(getLastPublisher());
	} else {
	    eseguiNuovo(nmJob, dsJob, null, OPERAZIONE.START);
	    setStatoJob(nmJob);
	}
    }

    public void stopGestioneJobOperation(String nmJob, String dsJob) {
	// Se il JOB è di tipo NO_TIMER in ogni caso il tasto di STOP va inibito
	if (gestioneJobEjb.isNoTimerJob(nmJob)) {
	    getMessageBox().addWarning(
		    "Attenzione: si sta tentando di stoppare un JOB di tipo NO_TIMER. Operazione non consentita");
	    forwardToPublisher(getLastPublisher());
	} else {
	    eseguiNuovo(nmJob, dsJob, null, OPERAZIONE.STOP);
	    setStatoJob(nmJob);
	}
    }

    public void esecuzioneSingolaGestioneJobOperation(String nmJob, String dsJob) {
	eseguiNuovo(nmJob, dsJob, null, OPERAZIONE.ESECUZIONE_SINGOLA);
	setStatoJob(nmJob);
    }

    private void eseguiNuovo(String nomeJob, String descrizioneJob, String nomeApplicazione,
	    OPERAZIONE operazione) {
	// Messaggio sul logger di sistema
	StringBuilder info = new StringBuilder(descrizioneJob);
	info.append(": ").append(operazione.description()).append(" [").append(nomeJob);
	if (nomeApplicazione != null) {
	    info.append("_").append(nomeApplicazione);
	}
	info.append("]");
	log.info(info.toString());

	String message = "Errore durante la schedulazione del job";

	switch (operazione) {
	case START:
	    jbossTimerEjb.start(nomeJob, null);
	    message = descrizioneJob
		    + ": job correttamente schedulato. L'operazione richiesta verrà schedulata correttamente entro il prossimo minuto.";
	    break;
	case ESECUZIONE_SINGOLA:
	    jbossTimerEjb.esecuzioneSingola(nomeJob, null);
	    message = descrizioneJob
		    + ": job correttamente schedulato per esecuzione singola. L'operazione richiesta verrà effettuata entro il prossimo minuto.";
	    break;
	case STOP:
	    jbossTimerEjb.stop(nomeJob);
	    message = descrizioneJob
		    + ": schedulazione job annullata. L'operazione richiesta diventerà effettiva entro il prossimo minuto.";
	    break;
	}

	// Segnalo l'avvenuta operazione sul job
	getMessageBox().addMessage(new Message(MessageLevel.INF, message));
	getMessageBox().setViewMode(ViewMode.plain);
    }

    private void setStatoJob(String nmJob) {
	Timestamp dataAttivazioneJob = getActivationDateJob(nmJob);
	StatoJob job = new StatoJob(nmJob, getForm().getInformazioniJob().getFl_data_accurata(),
		getForm().getInformazioniJob().getStartJobSchedulati(),
		getForm().getInformazioniJob().getEsecuzioneSingolaJobSchedulati(),
		getForm().getInformazioniJob().getStopJobSchedulati(),
		getForm().getInformazioniJob().getDt_prossima_attivazione(),
		getForm().getInformazioniJob().getAttivo(),
		getForm().getInformazioniJob().getDt_reg_log_job_ini(), dataAttivazioneJob);

	gestisciStatoJobNuovo(job);
	forwardToPublisher(Application.Publisher.SCHEDULAZIONI_JOB_LIST);
    }

    private Timestamp getActivationDateJob(String jobName) {
	Timestamp res = null;

	MonVVisLastSchedJobRowBean rb = monitoraggioHelper.getMonVVisLastSchedJob(jobName);
	if (rb.getFlJobAttivo() != null) {
	    if (rb.getFlJobAttivo().equals("1")) {
		res = rb.getDtRegLogJobIni();
	    }
	}
	return res;
    }

    private void gestisciStatoJobNuovo(StatoJob statoJob) {
	// se non è ancora passato un minuto da quando è stato premuto un pulsante non posso fare
	// nulla
	boolean operazioneInCorso = jbossTimerEjb.isEsecuzioneInCorso(statoJob.getNomeJob());

	statoJob.getFlagDataAccurata().setViewMode();
	statoJob.getFlagDataAccurata()
		.setValue("L'operazione richiesta verrà effettuata entro il prossimo minuto.");
	statoJob.getFlagDataAccurata().setHidden(!operazioneInCorso);

	// Posso operare sulla pagina
	Date nextActivation = jbossTimerEjb.getDataProssimaAttivazione(statoJob.getNomeJob());
	boolean dataAccurata = jbossTimerEjb
		.isDataProssimaAttivazioneAccurata(statoJob.getNomeJob());
	DateFormat formato = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

	/*
	 * Se il job è già schedulato o in esecuzione singola nascondo il pulsante Start/esecuzione
	 * singola, mostro Stop e visualizzo la prossima attivazione. Viceversa se è fermo mostro
	 * Start e nascondo Stop
	 */
	if (nextActivation != null) {
	    statoJob.getStart().setViewMode();
	    statoJob.getEsecuzioneSingola().setViewMode();
	    statoJob.getStop().setEditMode();
	    statoJob.getStart().setHidden(true);
	    statoJob.getEsecuzioneSingola().setHidden(true);
	    statoJob.getStop().setHidden(false);
	    statoJob.getDataProssimaAttivazione().setValue(formato.format(nextActivation));
	} else {
	    statoJob.getStart().setEditMode();
	    statoJob.getEsecuzioneSingola().setEditMode();
	    statoJob.getStop().setViewMode();
	    statoJob.getStart().setHidden(false);
	    statoJob.getEsecuzioneSingola().setHidden(false);
	    statoJob.getStop().setHidden(true);
	    statoJob.getDataProssimaAttivazione().setValue(null);
	}

	boolean flagHidden = nextActivation == null || dataAccurata;
	// se la data c'è ma non è accurata non visualizzare la "data prossima attivazione"
	statoJob.getDataProssimaAttivazione().setHidden(!flagHidden);

	if (statoJob.getDataAttivazione() != null) {
	    statoJob.getCheckAttivo().setChecked(true);
	    statoJob.getDataRegistrazioneJob()
		    .setValue(formato.format(new Date(statoJob.getDataAttivazione().getTime())));
	} else {
	    statoJob.getCheckAttivo().setChecked(false);
	    statoJob.getDataRegistrazioneJob().setValue(null);
	}

	// Se il JOB è di tipo NO_TIMER in ogni caso il tasto di START va inibito
	if (gestioneJobEjb.isNoTimerJob(statoJob.getNomeJob())) {
	    statoJob.getStart().setViewMode();
	    statoJob.getStart().setHidden(true);
	}

    }

    // <editor-fold defaultstate="collapsed" desc="Classe che mappa lo stato dei job">

    /**
     * Astrazione dei componenti della pagina Schedulazioni Job
     */
    public static final class StatoJob {

	private final String nomeJob;
	private final Input<String> flagDataAccurata;
	private final Button<String> start;
	private final Button<String> esecuzioneSingola;
	private final Button<String> stop;
	private final Input<Timestamp> dataProssimaAttivazione;
	private final CheckBox<String> checkAttivo;
	private final Input<Timestamp> dataRegistrazioneJob;
	private final Timestamp dataAttivazione;

	// Mi serve per evitare una null pointer Exception
	private static final Button<String> NULL_BUTTON = new Button<>(null, "EMPTY_BUTTON",
		"Pulsante vuoto", null, null, null, false, true, true, false);

	public StatoJob(String nomeJob, Input<String> flagDataAccurata, Button<String> start,
		Button<String> esecuzioneSingola, Button<String> stop,
		Input<Timestamp> dataProssimaAttivazione, CheckBox<String> checkAttivo,
		Input<Timestamp> dataRegistrazioneJob, Timestamp dataAttivazione) {
	    this.nomeJob = nomeJob;
	    this.flagDataAccurata = flagDataAccurata;
	    this.start = start;
	    this.esecuzioneSingola = esecuzioneSingola;
	    this.stop = stop;
	    this.dataProssimaAttivazione = dataProssimaAttivazione;
	    this.checkAttivo = checkAttivo;
	    this.dataRegistrazioneJob = dataRegistrazioneJob;
	    this.dataAttivazione = dataAttivazione;
	}

	public String getNomeJob() {
	    return nomeJob;
	}

	public Input<String> getFlagDataAccurata() {
	    return flagDataAccurata;
	}

	public Button<String> getStart() {
	    if (start == null) {
		return NULL_BUTTON;
	    }
	    return start;
	}

	public Button<String> getEsecuzioneSingola() {
	    return esecuzioneSingola;
	}

	public Button<String> getStop() {
	    if (stop == null) {
		return NULL_BUTTON;
	    }
	    return stop;
	}

	public Input<Timestamp> getDataProssimaAttivazione() {
	    return dataProssimaAttivazione;
	}

	public CheckBox<String> getCheckAttivo() {
	    return checkAttivo;
	}

	public Input<Timestamp> getDataRegistrazioneJob() {
	    return dataRegistrazioneJob;
	}

	public Timestamp getDataAttivazione() {
	    return dataAttivazione;
	}
    }
    // </editor-fold>

}
