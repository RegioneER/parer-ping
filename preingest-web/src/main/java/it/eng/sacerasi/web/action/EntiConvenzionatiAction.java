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

import it.eng.parer.sacerlog.ejb.SacerLogEjb;
import it.eng.parer.sacerlog.util.LogParam;
import it.eng.parer.sacerlog.util.web.SpagoliteLogUtil;
import it.eng.sacerasi.common.Constants;
import it.eng.sacerasi.exception.ParerUserError;
import it.eng.sacerasi.slite.gen.Application;
import it.eng.sacerasi.slite.gen.action.EntiConvenzionatiAbstractAction;
import it.eng.sacerasi.slite.gen.tablebean.SIOrgEnteConvenzOrgRowBean;
import it.eng.sacerasi.slite.gen.viewbean.OrgVRicEnteConvenzByEsternoTableBean;
import it.eng.sacerasi.slite.gen.viewbean.OrgVRicEnteNonConvenzTableBean;
import it.eng.sacerasi.web.ejb.AmministrazioneEjb;
import it.eng.sacerasi.web.helper.ConfigurationHelper;
import it.eng.spagoCore.error.EMFError;
import it.eng.spagoLite.actions.form.ListAction;
import it.eng.spagoLite.db.base.table.BaseTable;
import it.eng.spagoLite.db.oracle.decode.DecodeMap;
import it.eng.spagoLite.form.base.BaseElements;
import it.eng.spagoLite.message.MessageBox;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.ejb.EJB;
import org.codehaus.jettison.json.JSONObject;

/**
 *
 * @author Bonora_L
 */
public class EntiConvenzionatiAction extends EntiConvenzionatiAbstractAction {

    @EJB(mappedName = "java:app/SacerAsync-ejb/ConfigurationHelper")
    private ConfigurationHelper configurationHelper;
    @EJB(mappedName = "java:app/sacerlog-ejb/SacerLogEjb")
    private SacerLogEjb sacerLogEjb;

    @EJB(mappedName = "java:app/SacerAsync-ejb/AmministrazioneEjb")
    private AmministrazioneEjb amministrazioneEjb;

    public static String checkDeleteEnteConvenzOrg(int numeroEntiConvenz) {
	return numeroEntiConvenz <= 1
		? "Il versatore è associato ad un solo ente siam. Non è possibile eseguire l'eliminazione"
		: null;
    }

    @Override
    public void initOnClick() throws EMFError {
	// empty
    }

    @Override
    public void loadDettaglio() throws EMFError {
	if (getNavigationEvent().equals(ListAction.NE_DETTAGLIO_VIEW)
		|| getNavigationEvent().equals(ListAction.NE_DETTAGLIO_UPDATE)
		|| getNavigationEvent().equals(ListAction.NE_NEXT)
		|| getNavigationEvent().equals(ListAction.NE_PREV)) {
	    if (getTableName().equals(getForm().getEnteConvenzOrgList().getName())) {
		// Recupero il record dell'associazione versatore - ente siam
		BigDecimal idEnteConvenzOrg = ((SIOrgEnteConvenzOrgRowBean) getForm()
			.getEnteConvenzOrgList().getTable().getCurrentRow())
			.getBigDecimal("id_ente_convenz_org");
		SIOrgEnteConvenzOrgRowBean enteConvenzOrgRowBean = amministrazioneEjb
			.getSIOrgEnteConvenzOrgRowBean(idEnteConvenzOrg);

		// Recupero gli ambienti a cui l'utente è abilitato
		BaseTable ambienteEnteTable = amministrazioneEjb.getUsrVAbilAmbEnteConvenzTableBean(
			new BigDecimal(getUser().getIdUtente()));
		DecodeMap mappaAmbienteEnte = new DecodeMap();
		mappaAmbienteEnte.populatedMap(ambienteEnteTable, "id_ambiente_ente_convenz",
			"nm_ambiente_ente_convenz");
		getForm().getEnteConvenzOrg().getId_ambiente_ente_convenz()
			.setDecodeMap(mappaAmbienteEnte);

		if (enteConvenzOrgRowBean.getString("convenzionato").equals("1")) {
		    // Recupero tutti gli enti convenzionati a cui è abilitato e setto quello
		    // specifico
		    BigDecimal idAmbienteEnteConvenz = enteConvenzOrgRowBean
			    .getBigDecimal("id_ambiente_ente_convenz");
		    OrgVRicEnteConvenzByEsternoTableBean ricEnteConvenz = amministrazioneEjb
			    .getOrgVRicEnteConvenzAbilTableBean(
				    BigDecimal.valueOf(getUser().getIdUtente()),
				    idAmbienteEnteConvenz);
		    DecodeMap mappaEnte = new DecodeMap();
		    mappaEnte.populatedMap(ricEnteConvenz, "id_ente_convenz", "nm_ente_convenz");
		    getForm().getEnteConvenzOrg().getId_ente_convenz().setDecodeMap(mappaEnte);
		    getForm().getEnteConvenzOrg().getId_ente_convenz()
			    .setValue("" + enteConvenzOrgRowBean.getIdEnteConvenz());
		    getForm().getEnteConvenzOrg().getId_ambiente_ente_convenz()
			    .setValue("" + idAmbienteEnteConvenz);
		} else {
		    BigDecimal idEnteSiam = enteConvenzOrgRowBean.getBigDecimal("id_ente_convenz");
		    OrgVRicEnteNonConvenzTableBean ricEnteNonConvenz = amministrazioneEjb
			    .getOrgVRicEnteNonConvenzAbilTableBean(
				    BigDecimal.valueOf(getUser().getIdUtente()), null);
		    DecodeMap mappaEnte = new DecodeMap();
		    mappaEnte.populatedMap(ricEnteNonConvenz, "id_ente_siam", "nm_ente_siam");
		    getForm().getEnteConvenzOrg().getId_ente_convenz().setDecodeMap(mappaEnte);
		    getForm().getEnteConvenzOrg().getId_ente_convenz().setValue("" + idEnteSiam);
		}

		DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		String dtIni = formatter.format(enteConvenzOrgRowBean.getDtIniVal());
		String dtFine = formatter.format(enteConvenzOrgRowBean.getDtFineVal());

		getForm().getEnteConvenzOrg().getDt_ini_val().setValue(dtIni);
		getForm().getEnteConvenzOrg().getDt_fine_val().setValue(dtFine);
		getForm().getEnteConvenzOrg().getId_ente_convenz_org()
			.setValue("" + enteConvenzOrgRowBean.getIdEnteConvenzOrg());

		getForm().getEnteConvenzOrg().setViewMode();
		getForm().getEnteConvenzOrgList().setStatus(BaseElements.Status.view);
		getForm().getEnteConvenzOrg().setStatus(BaseElements.Status.view);
	    }
	}
    }

    @Override
    public void undoDettaglio() throws EMFError {
	elencoOnClick();
    }

    @Override
    public void insertDettaglio() throws EMFError {
	if (getTableName().equals(getForm().getEnteConvenzOrgList().getName())) {
	    getForm().getEnteConvenzOrg().clear();

	    BaseTable ambienteEnteTable = amministrazioneEjb
		    .getUsrVAbilAmbEnteConvenzTableBean(new BigDecimal(getUser().getIdUtente()));
	    DecodeMap mappaAmbienteEnte = new DecodeMap();
	    mappaAmbienteEnte.populatedMap(ambienteEnteTable, "id_ambiente_ente_convenz",
		    "nm_ambiente_ente_convenz");
	    getForm().getEnteConvenzOrg().getId_ambiente_ente_convenz()
		    .setDecodeMap(mappaAmbienteEnte);

	    OrgVRicEnteNonConvenzTableBean ricEnteNonConvenz = amministrazioneEjb
		    .getOrgVRicEnteNonConvenzAbilTableBean(
			    BigDecimal.valueOf(getUser().getIdUtente()), null);
	    DecodeMap mappaEnte = new DecodeMap();
	    mappaEnte.populatedMap(ricEnteNonConvenz, "id_ente_siam", "nm_ente_siam");
	    getForm().getEnteConvenzOrg().getId_ente_convenz().setDecodeMap(mappaEnte);

	    getForm().getEnteConvenzOrg().setEditMode();
	    getForm().getEnteConvenzOrgList().setStatus(BaseElements.Status.insert);
	    getForm().getEnteConvenzOrg().setStatus(BaseElements.Status.insert);

	    forwardToPublisher(getDefaultPublsherName());
	}
    }

    @Override
    public void saveDettaglio() throws EMFError {
	try {
	    if (getTableName().equals(getForm().getEnteConvenzOrgList().getName())
		    || getTableName().equals(getForm().getEnteConvenzOrg().getName())) {
		BigDecimal idVers = getForm().getVersRif().getId_vers().parse();
		if (getForm().getEnteConvenzOrg().postAndValidate(getRequest(), getMessageBox())) {
		    BigDecimal idEnteConvenz = getForm().getEnteConvenzOrg().getId_ente_convenz()
			    .parse();
		    Date dtIniVal = getForm().getEnteConvenzOrg().getDt_ini_val().parse();
		    Date dtFineVal = getForm().getEnteConvenzOrg().getDt_fine_val().parse();

		    if (dtIniVal.after(dtFineVal)) {
			getMessageBox().addError(
				"Attenzione: data di inizio validità superiore a data di fine validità");
		    }

		    if (!getMessageBox().hasError()) {

			/*
			 * Codice aggiuntivo per il logging...
			 */
			LogParam param = SpagoliteLogUtil.getLogParam(
				configurationHelper
					.getValoreParamApplicByApplic(Constants.NM_APPLIC),
				getUser().getUsername(), SpagoliteLogUtil.getPageName(this));
			param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
			if (getForm().getEnteConvenzOrg().getStatus()
				.equals(BaseElements.Status.insert)) {
			    param.setNomeAzione(SpagoliteLogUtil.getToolbarSave(false));
			    BigDecimal idEnteConvenzOrg = amministrazioneEjb.insertEnteConvenzOrg(
				    param, idVers, idEnteConvenz, dtIniVal, dtFineVal);
			    getForm().getEnteConvenzOrg().getId_ente_convenz_org()
				    .setValue(idEnteConvenzOrg.toPlainString());

			    SIOrgEnteConvenzOrgRowBean row = new SIOrgEnteConvenzOrgRowBean();
			    getForm().getEnteConvenzOrg().copyToBean(row);
			    row.setBigDecimal("id_ambiente_ente_convenz", getForm()
				    .getEnteConvenzOrg().getId_ambiente_ente_convenz().parse());
			    row.setString("nm_ambiente_ente_convenz", getForm().getEnteConvenzOrg()
				    .getId_ambiente_ente_convenz().getDecodedValue());
			    row.setString("nm_ente_siam", getForm().getEnteConvenzOrg()
				    .getId_ente_convenz().getDecodedValue());

			    getForm().getEnteConvenzOrgList().getTable().last();
			    getForm().getEnteConvenzOrgList().getTable().add(row);
			} else if (getForm().getEnteConvenzOrg().getStatus()
				.equals(BaseElements.Status.update)) {
			    param.setNomeAzione(SpagoliteLogUtil.getToolbarSave(true));
			    BigDecimal idEnteConvenzOrg = getForm().getEnteConvenzOrg()
				    .getId_ente_convenz_org().parse();
			    // La modifica, se interessa anche l'ente convenzionato, porta ad una
			    // modifica dell'id
			    // dell'associazione
			    BigDecimal idEnteConvenzOrgNew = amministrazioneEjb
				    .updateEnteConvenzOrg(param, idEnteConvenzOrg, idEnteConvenz,
					    idVers, dtIniVal, dtFineVal);

			    if (idEnteConvenzOrg.compareTo(idEnteConvenzOrgNew) != 0) {
				getForm().getEnteConvenzOrg().getId_ente_convenz_org()
					.setValue(idEnteConvenzOrgNew.toPlainString());
				getForm().getEnteConvenzOrgList().getTable().getCurrentRow()
					.setBigDecimal("id_ente_convenz_org", idEnteConvenzOrgNew);
			    }
			}
			getMessageBox().addInfo(
				"Associazione con l'ente convenzionato salvata con successo");
			getMessageBox().setViewMode(MessageBox.ViewMode.plain);
			loadDettaglio();
		    }
		}

		if (!getMessageBox().hasError()) {
		    getForm().getEnteConvenzOrgList().setStatus(BaseElements.Status.view);
		    getForm().getEnteConvenzOrg().setStatus(BaseElements.Status.view);
		    getForm().getEnteConvenzOrg().setViewMode();
		}
		forwardToPublisher(Application.Publisher.ENTE_CONVENZIONATO_DETAIL);
	    }
	} catch (ParerUserError ex) {
	    getMessageBox().addError(ex.getDescription());
	    forwardToPublisher(getLastPublisher());
	}
    }

    @Override
    public void dettaglioOnClick() throws EMFError {
	if (getNavigationEvent().equals(ListAction.NE_DETTAGLIO_VIEW)
		|| getNavigationEvent().equals(ListAction.NE_DETTAGLIO_UPDATE)
		|| getNavigationEvent().equals(ListAction.NE_NEXT)
		|| getNavigationEvent().equals(ListAction.NE_PREV)) {
	    if (getTableName().equals(getForm().getEnteConvenzOrgList().getName())) {
		forwardToPublisher(getDefaultPublsherName());
	    }
	}
    }

    @Override
    public void elencoOnClick() throws EMFError {
	goBack();
    }

    @Override
    protected String getDefaultPublsherName() {
	return Application.Publisher.ENTE_CONVENZIONATO_DETAIL;
    }

    @Override
    public void reloadAfterGoBack(String publisherName) {
	// per ora non serve
    }

    @Override
    public String getControllerName() {
	return Application.Actions.ENTI_CONVENZIONATI;
    }

    @Override
    public void updateEnteConvenzOrgList() throws EMFError {
	getForm().getEnteConvenzOrg().setEditMode();
	getForm().getEnteConvenzOrgList().setStatus(BaseElements.Status.update);
	getForm().getEnteConvenzOrg().setStatus(BaseElements.Status.update);
    }

    @Override
    public void updateEnteConvenzOrg() throws EMFError {
	updateEnteConvenzOrgList();
    }

    @Override
    public JSONObject triggerEnteConvenzOrgId_ambiente_ente_convenzOnTrigger() throws EMFError {
	getForm().getEnteConvenzOrg().post(getRequest());
	BigDecimal idAmbienteEnteConvenz = getForm().getEnteConvenzOrg()
		.getId_ambiente_ente_convenz().parse();
	DecodeMap mappa = new DecodeMap();
	if (idAmbienteEnteConvenz != null) {
	    OrgVRicEnteConvenzByEsternoTableBean ricEnteConvenz = amministrazioneEjb
		    .getOrgVRicEnteConvenzAbilTableBean(BigDecimal.valueOf(getUser().getIdUtente()),
			    idAmbienteEnteConvenz);
	    mappa.populatedMap(ricEnteConvenz, "id_ente_convenz", "nm_ente_convenz");
	} else {
	    OrgVRicEnteNonConvenzTableBean ricEnteNonConvenz = amministrazioneEjb
		    .getOrgVRicEnteNonConvenzAbilTableBean(
			    BigDecimal.valueOf(getUser().getIdUtente()), null);
	    mappa.populatedMap(ricEnteNonConvenz, "id_ente_siam", "nm_ente_siam");
	}
	getForm().getEnteConvenzOrg().getId_ente_convenz().setDecodeMap(mappa);
	return getForm().getEnteConvenzOrg().asJSON();

    }

    @Override
    public void deleteEnteConvenzOrgList() throws EMFError {
	deleteEnteConvenzOrg();
    }

    @Override
    public void deleteEnteConvenzOrg() throws EMFError {
	BigDecimal idEnteConvenzOrg = getForm().getEnteConvenzOrg().getId_ente_convenz_org()
		.parse();
	BigDecimal idEnteConvenzOrgFromList = ((SIOrgEnteConvenzOrgRowBean) getForm()
		.getEnteConvenzOrgList().getTable().getCurrentRow()).getIdEnteConvenzOrg();
	// Eseguo giusto un controllo per verificare che io stia prendendo la riga giusta se sono
	// nel dettaglio
	if (getLastPublisher().equals(Application.Publisher.ENTE_CONVENZIONATO_DETAIL)) {
	    if (!idEnteConvenzOrg.equals(idEnteConvenzOrgFromList)) {
		getMessageBox().addError(
			"Eccezione imprevista nell'eliminazione dell'associazione all'ente siam");
	    }
	}
	String error = checkDeleteEnteConvenzOrg(
		getForm().getEnteConvenzOrgList().getTable().size());
	if (error != null) {
	    getMessageBox().addError(error);
	}
	LogParam param = SpagoliteLogUtil.getLogParam(
		configurationHelper.getValoreParamApplicByApplic(Constants.NM_APPLIC),
		getUser().getUsername(), SpagoliteLogUtil.getPageName(this));
	param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
	param.setNomeAzione(SpagoliteLogUtil.getToolbarDelete());
	if (!getMessageBox().hasError() && idEnteConvenzOrg != null) {
	    try {
		amministrazioneEjb.deleteEnteConvenzOrg(param, idEnteConvenzOrg);
		getMessageBox().addInfo("Associazione all'ente siam eliminata con successo");
		getMessageBox().setViewMode(MessageBox.ViewMode.plain);
	    } catch (ParerUserError ex) {
		getMessageBox().addError(ex.getDescription());
	    }
	}
	goBack();
    }

    @Override
    public JSONObject triggerEnteConvenzOrgId_ente_convenzOnTrigger() throws EMFError {
	getForm().getEnteConvenzOrg().post(getRequest());
	BigDecimal idVers = getForm().getVersRif().getId_vers().parse();
	BigDecimal idEnteSiam = getForm().getEnteConvenzOrg().getId_ente_convenz().parse();
	if (idEnteSiam != null) {
	    Date[] dateAssociazione = amministrazioneEjb.getDatePerAssociazioneEnteVersatore(idVers,
		    idEnteSiam);
	    DateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
	    getForm().getEnteConvenzOrg().getDt_ini_val()
		    .setValue(formato.format(dateAssociazione[0]));
	    getForm().getEnteConvenzOrg().getDt_fine_val()
		    .setValue(formato.format(dateAssociazione[1]));
	}
	return getForm().getEnteConvenzOrg().asJSON();
    }
}
