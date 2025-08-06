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

package it.eng.sacerasi.job.recuperoSacer.ejb;

import it.eng.parer.ws.xml.versReqStato.Recupero;
import it.eng.parer.ws.xml.versReqStato.VersatoreType;
import it.eng.sacerasi.common.Constants;
import it.eng.sacerasi.corrispondenzeVers.helper.CorrispondenzeVersHelper;
import it.eng.sacerasi.entity.PigSessioneRecup;
import it.eng.sacerasi.entity.PigUnitaDocObject;
import it.eng.sacerasi.exception.ParerInternalError;
import it.eng.sacerasi.helper.RichiestaSacerHelper;
import it.eng.sacerasi.job.dto.EsitoConnessione;
import it.eng.sacerasi.job.dto.RichiestaSacerInput;
import it.eng.sacerasi.job.ejb.JobHelper;
import it.eng.sacerasi.job.ejb.JobLogger;
import it.eng.sacerasi.messages.MessaggiWSBundle;
import it.eng.sacerasi.viewEntity.UsrVAbilStrutSacerXping;
import it.eng.sacerasi.web.helper.ConfigurationHelper;
import it.eng.sacerasi.ws.ejb.XmlContextCache;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Bonora_L
 */
@Stateless(mappedName = "RecuperoSacerEjb")
@LocalBean
@Interceptors({
	it.eng.sacerasi.aop.TransactionInterceptor.class })
public class RecuperoSacerEjb {

    Logger log = LoggerFactory.getLogger(RecuperoSacerEjb.class);
    @EJB
    private JobLogger jobLoggerEjb;
    @EJB
    private JobHelper helper;
    @EJB
    private SalvaDati salvaDati;
    // Singleton Ejb di gestione cache dei parser Castor
    @EJB
    XmlContextCache xmlContextCache = null;
    @EJB
    private CorrispondenzeVersHelper corVersHelper;
    @EJB
    private ConfigurationHelper configurationHelper;
    @EJB
    private RichiestaSacerHelper richiestaSacerHelper;

    public void recuperaOggettiSacer() throws ParerInternalError {
	String errorMessage = null;
	// Recupero la lista di id dei versatori
	List<Long> versatori = helper.getListaVersatori();
	// In base a quella lista, recupero le sessioni di recupero con stato IN_ATTESA_RECUP
	List<PigSessioneRecup> tmpSessioni = helper.getListaSessioni(versatori,
		Constants.StatoSessioneRecup.IN_ATTESA_RECUP);
	if (!tmpSessioni.isEmpty()) {
	    // Integer timeout = new
	    // Integer(helper.getParamFromParamApplic(Constants.TIMEOUT_VERS_SACER));
	    Integer timeout = new Integer(
		    configurationHelper.getValoreParamApplicByApplic(Constants.TIMEOUT_VERS_SACER));
	    // Ogni sessione è correlata a un oggetto PigObject, che a sua volta contiene un unica
	    // unità documentaria
	    for (PigSessioneRecup sessioneRec : tmpSessioni) {
		PigUnitaDocObject unitaDocObj = sessioneRec.getPigObject().getPigUnitaDocObjects()
			.get(0);
		// Inizializzo l'oggetto di cui poi dovrò eseguire il marshall per fare la richiesta
		// a Sacer
		Recupero recupero = new Recupero();

		recupero.setVersione(Constants.VERSIONE_XML_MM);
		recupero.setVersatore(new VersatoreType());
		recupero.setChiave(new it.eng.parer.ws.xml.versReqStato.ChiaveType());

		String nmAmbienteSacer = null;
		String nmEnteSacer = null;
		String nmStrutSacer = null;
		String nmUseridSacer = null;
		String cdPswSacer = null;
		// List<PigVLisStrutVersSacer> idOrganizIamStruts =
		// corVersHelper.getIdOrganizIamStrut(
		// unitaDocObj.getPigObject().getPigTipoObject().getIdTipoObject(),
		// unitaDocObj.getIdOrganizIam());
		// if (idOrganizIamStruts == null || idOrganizIamStruts.isEmpty() ||
		// idOrganizIamStruts.size() > 1) {
		// throw new ParerInternalError(
		// "Errore inatteso nel recupero dell'organizzazione per il recupero dell'oggetto");
		// } else {
		// PigVLisStrutVersSacer strut = idOrganizIamStruts.get(0);
		BigDecimal idAmbienteVers = BigDecimal.valueOf(sessioneRec.getPigObject()
			.getPigTipoObject().getPigVer().getPigAmbienteVer().getIdAmbienteVers());
		BigDecimal idVers = BigDecimal.valueOf(
			sessioneRec.getPigObject().getPigTipoObject().getPigVer().getIdVers());
		BigDecimal idTipoObject = BigDecimal
			.valueOf(sessioneRec.getPigObject().getPigTipoObject().getIdTipoObject());
		nmUseridSacer = configurationHelper.getValoreParamApplicByTipoObj(
			"USERID_USER_VERS", idAmbienteVers, idVers, idTipoObject);
		cdPswSacer = configurationHelper.getValoreParamApplicByTipoObj("PSW_USER_VERS",
			idAmbienteVers, idVers, idTipoObject);

		// nmUseridSacer = strut.getNmUseridSacer();
		// cdPswSacer = strut.getCdPasswordSacer();
		UsrVAbilStrutSacerXping strutturaAbilitata = corVersHelper
			.getStrutturaAbilitata(unitaDocObj.getIdOrganizIam(), nmUseridSacer);
		nmAmbienteSacer = strutturaAbilitata.getNmAmbiente();
		nmEnteSacer = strutturaAbilitata.getNmEnte();
		nmStrutSacer = strutturaAbilitata.getNmStrut();
		// strut = null;
		// idOrganizIamStruts = null;
		// }
		recupero.getVersatore().setAmbiente(nmAmbienteSacer);
		recupero.getVersatore().setEnte(nmEnteSacer);
		recupero.getVersatore().setStruttura(nmStrutSacer);
		recupero.getVersatore().setUserID(nmUseridSacer);

		recupero.getChiave()
			.setAnno(BigInteger.valueOf(unitaDocObj.getAaUnitaDocSacer().longValue()));
		recupero.getChiave().setNumero(unitaDocObj.getCdKeyUnitaDocSacer());
		recupero.getChiave().setTipoRegistro(unitaDocObj.getCdRegistroUnitaDocSacer());

		StringWriter tmpWriter = new StringWriter();
		// Eseguo il marshalling degli oggetti creati per salvarli poi nell'oggetto
		// UnitaDocObject
		try {
		    Marshaller recMarshaller = xmlContextCache.getVersReqStatoCtx_Recupero()
			    .createMarshaller();
		    recMarshaller.marshal(recupero, tmpWriter);
		    tmpWriter.flush();
		} catch (JAXBException ex) {
		    log.error("Eccezione", ex);
		    throw new ParerInternalError(ex);
		}

		// Inizializzo l'oggetto con cui dovrò fare il marshall per definire l'output per il
		// recupero
		it.eng.parer.ws.xml.versReqStatoMM.IndiceMM indice = new it.eng.parer.ws.xml.versReqStatoMM.IndiceMM();
		indice.setVersione(Constants.VERSIONE_XML_MM);
		indice.setApplicativoChiamante(Constants.APP_CHIAMANTE);
		// indice.setOutputPath(sessioneRec.getPigVer().getDsPathOutputFtp());
		// Long idAmbienteVers =
		// sessioneRec.getPigObject().getPigTipoObject().getPigVer().getPigAmbienteVer()
		// .getIdAmbienteVers();
		// Long idVers =
		// sessioneRec.getPigObject().getPigTipoObject().getPigVer().getIdVers();
		// Long idTipoObject =
		// sessioneRec.getPigObject().getPigTipoObject().getIdTipoObject();
		indice.setOutputPath(sessioneRec.getPigObject().getPigTipoObject().getPigVer()
			.getDsPathOutputFtp());
		// indice.setOutputPath(configurationHelper.getValoreParamApplic("DS_PATH_OUTPUT_FTP",
		// BigDecimal.valueOf(idAmbienteVers), BigDecimal.valueOf(idVers),
		// BigDecimal.valueOf(idTipoObject), Constants.TipoPigVGetValAppart.TIPOOBJECT));
		StringWriter indexWriter = new StringWriter();
		// Eseguo il marshalling dell'oggetto in uno stringWriter
		try {
		    Marshaller recMarshaller = xmlContextCache.getVersReqStatoMMCtx_IndiceMM()
			    .createMarshaller();
		    recMarshaller.marshal(indice, indexWriter);
		    tmpWriter.flush();
		} catch (JAXBException ex) {
		    log.error("Eccezione", ex);
		    throw new ParerInternalError(ex);
		}
		// Preparo la chiamata a richiesta sacer RecuperoUnitaDocumentariaSync
		// String urlRecuperoUdSacer =
		// sessioneRec.getPigVer().getPigAmbienteVer().getDsUrlServRecup();
		String urlRecuperoUdSacer = configurationHelper.getValoreParamApplicByTipoObj(
			"DS_URL_SERV_RECUP", idAmbienteVers, idVers, idTipoObject);
		// Creo l'oggetto contenente i suoi parametri (TipoRichiesta = RECUPERO)
		RichiestaSacerInput input = new RichiestaSacerInput(
			RichiestaSacerInput.TipoRichiestaSacer.RECUPERO, Constants.VERSIONE_XML_MM,
			urlRecuperoUdSacer, tmpWriter.toString(), indexWriter.toString(),
			nmUseridSacer, cdPswSacer, timeout);
		// Chiamata a richiesta sacer RecuperoUnitaDocumentariaSync
		EsitoConnessione esitoConnRecUD = richiestaSacerHelper.upload(input);
		String codiceErroreRecupero = esitoConnRecUD.getCodiceErrore();
		String codiceEsitoRecupero = esitoConnRecUD.getCodiceEsito();
		String messaggioErroreRecupero = esitoConnRecUD.getMessaggioErrore();

		if (esitoConnRecUD.isErroreConnessione()) {
		    log.error(Constants.ServizioRecupero.RECUPERO_UD_SERVICE.getValue() + " - "
			    + esitoConnRecUD.getDescrErrConnessione());
		    // Il servizio non ha risposto per un errore di connessione
		    // Registro l'errore e chiudo il job
		    errorMessage = MessaggiWSBundle.getString(MessaggiWSBundle.ERR_TIMEOUT_RECUPERO,
			    Constants.ServizioRecupero.RECUPERO_UD_SERVICE.getValue(),
			    sessioneRec.getPigVer().getPigAmbienteVer().getNmAmbienteVers(),
			    sessioneRec.getPigVer().getNmVers(),
			    unitaDocObj.getCdRegistroUnitaDocSacer(),
			    unitaDocObj.getAaUnitaDocSacer().toString(),
			    unitaDocObj.getCdKeyUnitaDocSacer());
		    jobLoggerEjb.writeAtomicLog(Constants.NomiJob.RECUPERO_SACER,
			    Constants.TipiRegLogJob.ERRORE, errorMessage);
		    break;
		}

		if (codiceEsitoRecupero.equals(Constants.EsitoVersamento.NEGATIVO.name())) {
		    // se il risultato è stato inaspettatamente NEGATIVO registro la sessione con
		    // stato di errore e
		    // chiudo il job
		    salvaDati.elaboraErrore(sessioneRec,
			    Constants.ServizioRecupero.RECUPERO_UD_SERVICE, null,
			    codiceErroreRecupero, messaggioErroreRecupero);

		    errorMessage = MessaggiWSBundle.getString(
			    MessaggiWSBundle.ERR_NEGATIVO_RECUPERO_UD,
			    sessioneRec.getPigVer().getPigAmbienteVer().getNmAmbienteVers(),
			    sessioneRec.getPigVer().getNmVers(),
			    unitaDocObj.getCdRegistroUnitaDocSacer(),
			    unitaDocObj.getAaUnitaDocSacer().toString(),
			    unitaDocObj.getCdKeyUnitaDocSacer(), codiceErroreRecupero,
			    messaggioErroreRecupero);
		    jobLoggerEjb.writeAtomicLog(Constants.NomiJob.RECUPERO_SACER,
			    Constants.TipiRegLogJob.ERRORE, errorMessage);
		    break;
		} else if (codiceEsitoRecupero.equals(Constants.EsitoVersamento.WARNING.name())) {
		    // In caso la risposta di SACER sia WARNING vado a gestire le sessioni
		    // successive
		    continue;
		}

		// MAC 12248 - rimossa chiamata a richiesta sacer RecProveConservSync perchè non
		// più supportata.
		// Preparo la chiamata a richiesta sacer RecProveConservSync
		// L'xml è basato sugli stessi elementi
		// FIXME : Verificare cosa cambia nell'xml tra RecuperoUd e RecProveConserv (se
		// cambia, altrimenti
		// cambia solo l'url e l'xml rimane invariato)
		// String urlRecuperoPCSacer =
		// configurationHelper.getValoreParamApplic("DS_URL_SERV_PROVE",
		// idAmbienteVers, idVers, idTipoObject, Constants.TipoPigVGetValAppart.TIPOOBJECT);
		// input = new RichiestaSacerInput(RichiestaSacerInput.TipoRichiestaSacer.RECUPERO,
		// Constants.VERSIONE_XML_MM, urlRecuperoPCSacer, tmpWriter.toString(),
		// indexWriter.toString(),
		// nmUseridSacer, cdPswSacer, timeout);
		// // Creo l'oggetto contenente i suoi parametri (TipoRichiesta = RECUPERO)
		// // Chiamata a richiesta sacer RecuperoUnitaDocumentariaSync
		// EsitoConnessione esitoConnRecPC = richiesta.upload(input);
		// codiceErroreRecupero = esitoConnRecPC.getCodiceErrore();
		// codiceEsitoRecupero = esitoConnRecPC.getCodiceEsito();
		// messaggioErroreRecupero = esitoConnRecPC.getMessaggioErrore();
		//
		// if (esitoConnRecPC.isErroreConnessione()) {
		// log.error(Constants.ServizioRecupero.RECUPERO_PC_SERVICE.getValue() + " - "
		// + esitoConnRecPC.getDescrErrConnessione());
		// // Il servizio non ha risposto per un errore di connessione
		// // Registro l'errore e chiudo il job
		// errorMessage = MessaggiWSBundle.getString(MessaggiWSBundle.ERR_TIMEOUT_RECUPERO,
		// Constants.ServizioRecupero.RECUPERO_PC_SERVICE.getValue(),
		// sessioneRec.getPigVer().getPigAmbienteVer().getNmAmbienteVers(),
		// sessioneRec.getPigVer().getNmVers(), unitaDocObj.getCdRegistroUnitaDocSacer(),
		// unitaDocObj.getAaUnitaDocSacer().toString(),
		// unitaDocObj.getCdKeyUnitaDocSacer());
		// jobLoggerEjb.writeAtomicLog(Constants.NomiJob.RECUPERO_SACER,
		// Constants.TipiRegLogJob.ERRORE,
		// errorMessage);
		// break;
		// }
		//
		// if (codiceEsitoRecupero.equals(Constants.EsitoVersamento.NEGATIVO.name())) {
		// // se il risultato è stato inaspettatamente NEGATIVO registro la sessione con
		// stato di errore e
		// // chiudo il job
		// salvaDati.elaboraErrore(sessioneRec,
		// Constants.ServizioRecupero.RECUPERO_PC_SERVICE, unitaDocObj,
		// codiceErroreRecupero, messaggioErroreRecupero);
		//
		// errorMessage =
		// MessaggiWSBundle.getString(MessaggiWSBundle.ERR_NEGATIVO_RECUPERO_PC,
		// sessioneRec.getPigVer().getPigAmbienteVer().getNmAmbienteVers(),
		// sessioneRec.getPigVer().getNmVers(), unitaDocObj.getCdRegistroUnitaDocSacer(),
		// unitaDocObj.getAaUnitaDocSacer().toString(), unitaDocObj.getCdKeyUnitaDocSacer(),
		// codiceErroreRecupero, messaggioErroreRecupero);
		// jobLoggerEjb.writeAtomicLog(Constants.NomiJob.RECUPERO_SACER,
		// Constants.TipiRegLogJob.ERRORE,
		// errorMessage);
		// break;
		// }

		// In base alla risposta dei due oggetti apro la transazione
		salvaDati.elabora(sessioneRec, unitaDocObj);
	    }
	}
	// Quando ho elaborato l'ultima sessione, scrivo sul log la fine esecuzione del job
	jobLoggerEjb.writeAtomicLog(Constants.NomiJob.RECUPERO_SACER,
		Constants.TipiRegLogJob.FINE_SCHEDULAZIONE, null);
    }
}
