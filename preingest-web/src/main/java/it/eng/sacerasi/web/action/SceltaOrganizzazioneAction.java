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

import it.eng.parer.sacerlog.ejb.helper.SacerLogHelper;
import it.eng.parer.sacerlog.entity.constraint.ConstLogEventoLoginUser;
import it.eng.sacerasi.grantEntity.UsrUser;
import it.eng.sacerasi.slite.gen.Application;
import it.eng.sacerasi.slite.gen.action.SceltaOrganizzazioneAbstractAction;
import it.eng.sacerasi.slite.gen.tablebean.PigAmbienteVersTableBean;
import it.eng.sacerasi.slite.gen.tablebean.PigVersTableBean;
import it.eng.sacerasi.web.helper.AmministrazioneHelper;
import it.eng.sacerasi.web.helper.ComboHelper;
import it.eng.sacerasi.web.helper.ConfigurationHelper;
import it.eng.sacerasi.web.helper.LoginLogHelper;
import it.eng.sacerasi.web.security.PreingestAuthenticator;
import it.eng.sacerasi.web.util.AuditSessionListener;
import it.eng.sacerasi.web.util.WebConstants;
import it.eng.spagoCore.error.EMFError;
import it.eng.spagoLite.db.oracle.decode.DecodeMap;
import it.eng.spagoLite.message.MessageBox.ViewMode;
import it.eng.spagoLite.security.User;
import it.eng.spagoLite.security.auth.PwdUtil;
import it.eng.util.EncryptionUtil;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import it.eng.sacerasi.common.Constants;

/**
 *
 * @author Gilioli_P
 */
public class SceltaOrganizzazioneAction extends SceltaOrganizzazioneAbstractAction {

    private static final Logger log = LoggerFactory
	    .getLogger(SceltaOrganizzazioneAction.class.getName());

    @Autowired
    private PreingestAuthenticator autenticator;
    @EJB(mappedName = "java:app/SacerAsync-ejb/ComboHelper")
    private ComboHelper comboHelper;
    @EJB(mappedName = "java:app/SacerAsync-ejb/ConfigurationHelper")
    private ConfigurationHelper configHelper;
    @EJB(mappedName = "java:app/SacerAsync-ejb/LoginLogHelper")
    private LoginLogHelper loginLogHelper;
    @EJB(mappedName = "java:app/SacerAsync-ejb/AmministrazioneHelper")
    private AmministrazioneHelper amministrazioneHelper;
    @EJB(mappedName = "java:app/sacerlog-ejb/SacerLogHelper")
    private SacerLogHelper sacerLogHelper;

    @Override
    public void process() throws EMFError {
	User utente = getUser();
	// MEV#23905 - Associazione utente SPID con anagrafica utenti
	if (utente.isUtenteDaAssociare()) {
	    gestisciUtenteDaAssociare(utente);
	} else {
	    // loggo, se necessario, l'avvenuto login nell'applicativo
	    this.loginLogger(utente);
	    PigAmbienteVersTableBean ambientiTb = comboHelper
		    .getAmbienteVersatoreFromUtente(getUser().getIdUtente());
	    DecodeMap mappaAmbiente = new DecodeMap();
	    mappaAmbiente.populatedMap(ambientiTb, "id_ambiente_vers", "nm_ambiente_vers");
	    getForm().getVersatori().getNm_ambiente_vers().setDecodeMap(mappaAmbiente);
	    if (ambientiTb.size() == 1) {
		getForm().getVersatori().getNm_ambiente_vers()
			.setValue(ambientiTb.getRow(0).getIdAmbienteVers().toString());
		PigVersTableBean versatoriTb = comboHelper.getVersatoreFromAmbienteVersatore(
			getUser().getIdUtente(), ambientiTb.getRow(0).getIdAmbienteVers());
		getForm().getVersatori().getNm_vers().setDecodeMap(
			DecodeMap.Factory.newInstance(versatoriTb, "id_vers", "nm_vers"));
		// Se ho un solo versatore lo setto già impostato nella combo
		if (versatoriTb.size() == 1) {
		    getForm().getVersatori().getNm_vers()
			    .setValue(versatoriTb.getRow(0).getIdVers().toString());
		}
	    } else {
		getForm().getVersatori().getNm_vers().setDecodeMap(new DecodeMap());
	    }
	    getForm().getVersatori().setEditMode();
	    forwardToPublisher(Application.Publisher.SCELTA_VERSATORE);
	}
    }

    // MEV#23905 - Associazione utente SPID con anagrafica utenti
    private void gestisciUtenteDaAssociare(User utente) throws EMFError {
	this.freeze();
	String username = "NON_PRESENTE";
	/*
	 * MEV#22913 - Logging accessi SPID non autorizzati In caso di utente SPID lo username non
	 * c'è ancora perché deve essere ancora associato Quindi si prende il suo codice fiscale se
	 * presente, altrimenti una stringa fissa come username
	 */
	if (utente.getCodiceFiscale() != null && !utente.getCodiceFiscale().isEmpty()) {
	    username = utente.getCodiceFiscale().toUpperCase();
	}
	sacerLogHelper.insertEventoLoginUser(username, getIpClient(), new Date(),
		ConstLogEventoLoginUser.TipoEvento.BAD_CF.name(),
		"SACERPING - " + ConstLogEventoLoginUser.DS_EVENTO_BAD_CF_SPID, utente.getCognome(),
		utente.getNome(), utente.getCodiceFiscale(), utente.getExternalId(),
		utente.getEmail());
	String retURL = configHelper.getUrlBackAssociazioneUtenteCf();
	String salt = Base64.encodeBase64URLSafeString(PwdUtil.generateSalt());
	byte[] cfCriptato = EncryptionUtil.aesCrypt(utente.getCodiceFiscale(),
		EncryptionUtil.Aes.BIT_256);
	String f = Base64.encodeBase64URLSafeString(cfCriptato);
	byte[] cogCriptato = EncryptionUtil.aesCrypt(utente.getCognome(),
		EncryptionUtil.Aes.BIT_256);
	String c = Base64.encodeBase64URLSafeString(cogCriptato);
	byte[] nomeCriptato = EncryptionUtil.aesCrypt(utente.getNome(), EncryptionUtil.Aes.BIT_256);
	String n = Base64.encodeBase64URLSafeString(nomeCriptato);
	String hmac = EncryptionUtil.getHMAC(retURL + ":" + utente.getCodiceFiscale() + ":" + salt);
	final String url = configHelper
		.getValoreParamApplicByApplic(Constants.URL_ASSOCIAZIONE_UTENTE_CF);
	try {
	    this.getResponse()
		    .sendRedirect(url + "?r=" + Base64.encodeBase64URLSafeString(retURL.getBytes())
			    + "&h=" + hmac + "&s=" + salt + "&f=" + f + "&c=" + c + "&n=" + n);
	} catch (IOException ex) {
	    throw new EMFError("ERROR", "Errore nella sendRedirect verso Iam");
	}

    }

    @Override
    public void initOnClick() throws EMFError {
	//
    }

    public void backFromAssociation() throws EMFError {
	User user = getUser();
	if (user.getCodiceFiscale() != null && !user.getCodiceFiscale().isEmpty()) {
	    List<UsrUser> l = amministrazioneHelper.findByCodiceFiscale(user.getCodiceFiscale());
	    if (l.size() == 1) {
		UsrUser us = l.iterator().next();
		user.setUtenteDaAssociare(false);
		user.setUsername(us.getNmUserid());
		user.setIdUtente(us.getIdUserIam());
		process();
		getMessageBox().addInfo(
			"L'utente loggato è stato ricondotto con successo all'utenza Parer.");
		return;
	    }
	}
	/*
	 * Per sicurezza se qualcuno forza l'accesso con la URL senza provenire da IAM lo butto
	 * fuori!
	 */
	log.error(
		"Chiamata al metodo beckFromAssociation non autorizzata! Effettuo il logout forzato!");
	redirectToAction(Application.Actions.LOGOUT);
    }

    @Override
    public void selezionaVersatore() throws EMFError {
	// testo (di nuovo!) che l'utente possa usare quella struttura
	DecodeMap mappaTipoVersatore = (DecodeMap) getForm().getVersatori().getNm_vers()
		.getDecodeMap();

	if (mappaTipoVersatore == null) {
	    process();
	    mappaTipoVersatore = (DecodeMap) getForm().getVersatori().getNm_vers().getDecodeMap();
	}
	getForm().getVersatori().post(getRequest());
	User user = getUser();
	String nmAmbienteVers = getForm().getVersatori().getNm_ambiente_vers().getDecodedValue();
	BigDecimal idVers = getForm().getVersatori().getNm_vers().parse();
	String nmVers = getForm().getVersatori().getNm_vers().getDecodedValue();
	if (idVers == null) {
	    getMessageBox().setViewMode(ViewMode.plain);
	    getMessageBox().addError("Selezionare il versatore su cui operare");
	    forwardToPublisher(Application.Publisher.SCELTA_VERSATORE);
	} else if (!mappaTipoVersatore.containsKey(idVers)) {
	    getMessageBox().setViewMode(ViewMode.plain);
	    getMessageBox().addError("Versatore non esistente o utente non abilitato al versatore");
	    forwardToPublisher(Application.Publisher.SCELTA_VERSATORE);
	} else {
	    user.setIdOrganizzazioneFoglia(idVers);
	    Map<String, String> organizzazione = new LinkedHashMap<>();
	    organizzazione.put(WebConstants.organizzazione.AMBIENTE.name(), nmAmbienteVers);
	    organizzazione.put(WebConstants.organizzazione.VERSATORE.name(), nmVers);
	    user.setOrganizzazioneMap(organizzazione);
	    user.setConfigurazione(configHelper.getConfiguration());
	    autenticator.recuperoAutorizzazioni(getSession());
	    redirectToAction(Application.Actions.HOME + "?cleanhistory=true");
	}
    }

    @Override
    public void insertDettaglio() throws EMFError {
	//
    }

    @Override
    public void loadDettaglio() throws EMFError {
	//
    }

    @Override
    public void undoDettaglio() throws EMFError {
	//
    }

    @Override
    public void saveDettaglio() throws EMFError {
	//
    }

    @Override
    public void dettaglioOnClick() throws EMFError {
	//
    }

    @Override
    public void elencoOnClick() throws EMFError {
	//
    }

    @Override
    protected String getDefaultPublsherName() {
	return Application.Publisher.SCELTA_VERSATORE;
    }

    @Override
    public String getControllerName() {
	return Application.Actions.SCELTA_ORGANIZZAZIONE;
    }

    // Da non rimuovere! Necessario perché tutti gli utenti devono poter scegliere la struttura.
    @Override
    public boolean isAuthorized(String destination) {
	return true;
    }

    @Override
    public void reloadAfterGoBack(String publisherName) {
	//
    }

    private void loginLogger(User utente) {
	if (utente.getConfigurazione() == null && utente.getOrganizzazioneMap() == null) {
	    // è un login iniziale e non un ritorno sulla form per un cambio struttura.
	    // Se fosse un cambio di struttura, queste variabili sarebbero valorizzate
	    // poiché riportano i dati relativi alla struttura su cui l'utente
	    // sta operando.
	    HttpServletRequest request = getRequest();
	    String ipVers = request.getHeader("X-FORWARDED-FOR");
	    if (ipVers == null || ipVers.isEmpty()) {
		ipVers = request.getRemoteAddr();
	    }
	    log.debug("Indirizzo da cui l'utente si connette: " + ipVers);
	    loginLogHelper.writeLogEvento(utente, ipVers, LoginLogHelper.TipiEvento.LOGIN);
	    getSession().setAttribute(AuditSessionListener.CLIENT_IP_ADDRESS, ipVers);
	}
    }

    @Override
    public JSONObject triggerVersatoriNm_ambiente_versOnTrigger() throws EMFError {
	getForm().getVersatori().post(getRequest());
	BigDecimal idAmbienteVers = getForm().getVersatori().getNm_ambiente_vers().parse();
	if (idAmbienteVers != null) {
	    PigVersTableBean versatoriTb = comboHelper
		    .getVersatoreFromAmbienteVersatore(getUser().getIdUtente(), idAmbienteVers);
	    getForm().getVersatori().getNm_vers()
		    .setDecodeMap(DecodeMap.Factory.newInstance(versatoriTb, "id_vers", "nm_vers"));
	    // Se ho un solo versatore lo setto già impostato nella combo
	    if (versatoriTb.size() == 1) {
		getForm().getVersatori().getNm_vers()
			.setValue(versatoriTb.getRow(0).getIdVers().toString());
	    }
	} else {
	    getForm().getVersatori().getNm_vers().setDecodeMap(new DecodeMap());
	}
	return getForm().getVersatori().asJSON();
    }
}
