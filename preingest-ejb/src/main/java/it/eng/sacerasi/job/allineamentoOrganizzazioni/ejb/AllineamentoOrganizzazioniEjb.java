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

package it.eng.sacerasi.job.allineamentoOrganizzazioni.ejb;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.naming.NamingException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.SOAPFaultException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.integriam.client.ws.IAMSoapClients;
import it.eng.integriam.client.ws.reporg.ListaTipiDato;
import it.eng.integriam.client.ws.reporg.ReplicaOrganizzazione;
import it.eng.integriam.client.ws.reporg.ReplicaOrganizzazioneRispostaAbstract;
import it.eng.integriam.client.ws.reporg.TipoDato;
import it.eng.sacerasi.common.Constants;
import it.eng.sacerasi.common.Constants.EsitoServizio;
import it.eng.sacerasi.entity.IamOrganizDaReplic;
import it.eng.sacerasi.entity.PigAmbienteVers;
import it.eng.sacerasi.entity.PigTipoObject;
import it.eng.sacerasi.entity.PigVers;
import it.eng.sacerasi.exception.ParerInternalError;
import it.eng.sacerasi.job.allineamentoOrganizzazioni.dto.ParametriInputOrganizzazioni;
import it.eng.sacerasi.job.ejb.JobLogger;
import it.eng.sacerasi.web.helper.ConfigurationHelper;

/**
 *
 * @author Gilioli_P
 */
@Stateless(mappedName = "AllineamentoOrganizzazioniEjb")
@LocalBean
@Interceptors({
	it.eng.sacerasi.aop.TransactionInterceptor.class })
public class AllineamentoOrganizzazioniEjb {

    Logger log = LoggerFactory.getLogger(AllineamentoOrganizzazioniEjb.class);
    @EJB
    private JobLogger jobLoggerEjb;
    @EJB
    private AllineamentoOrganizzazioniHelper aoHelper;
    @EJB
    private ConfigurationHelper configurationHelper;

    /**
     * Metodo chiamato per la duplicazione su IAM delle organizzazioni inserite, modificate o
     * cancellate su SACER_PING. La lista delle organizzazioni è recuperata dalla tabella
     * IAM_ORGANIZ_DA_REPLIC
     *
     * @throws ParerInternalError errore generico
     * @throws NamingException    errore generico
     */
    public void allineamentoOrganizzazioni() throws ParerInternalError, NamingException {
	allineamentoOrganizzazioni(null);
    }

    /**
     * Metodo chiamato per la duplicazione su IAM delle organizzazioni inserite, modificate o
     * cancellate su SACER_PING
     *
     * @param organizList Lista deelle organizzazioni da replicare su IAM
     *
     * @return ritorna l'esito della lavorazione dell'ultimo elemento della lista passata
     *         comeparametro
     *
     * @throws ParerInternalError errore generico
     * @throws NamingException    errore generico
     */
    public String allineamentoOrganizzazioni(List<IamOrganizDaReplic> organizList)
	    throws ParerInternalError, NamingException {
	String result = EsitoServizio.OK.name();
	boolean arrivoDaOnLine = false;
	/*
	 * Determino l'insieme delle registrazioni nel log delle organizzazioni da allineare con
	 * stato DA_REPLICARE, REPLICA_IN_TIMEOUT o REPLICA_IN_ERRORE
	 */
	if (organizList == null) {
	    organizList = aoHelper.getIamOrganizDaReplic();
	} else {
	    arrivoDaOnLine = true;
	}

	/* Ricavo l'url del ws */
	String url = configurationHelper.getValoreParamApplicByApplic(Constants.URL_REPLICA_ORG);

	/* Mi tengo una variabile che mi dice se la replica è andata o meno a buon fine */
	boolean replicaOK = true;

	log.info("Replica Organizzazioni SACER PREINGEST - ottenute {} organizzazioni da replicare",
		organizList.size());

	/* Per ogni registrazione determinata */
	for (IamOrganizDaReplic organizDaReplic : organizList) {
	    try {
		ParametriInputOrganizzazioni pa = getParametriInputOrganizzazione(organizDaReplic);
		ReplicaOrganizzazioneRispostaAbstract resp = new ReplicaOrganizzazioneRispostaAbstract() {
		};

		/* Se l'organizzazione è presente */
		ReplicaOrganizzazione client = IAMSoapClients
			.replicaOrganizzazioneClient(pa.getNmUserid(), pa.getCdPsw(), url);

		if (client != null) {
		    /* PREPARAZIONE ATTIVAZIONE SERVIZIO */
		    log.info(
			    "Replica Organizzazioni SACER PREINGEST - Preparazione attivazione servizio per l'organizzazione {}",
			    organizDaReplic.getNmOrganiz());
		    if (organizDaReplic.getTiOperReplic().equals(Constants.TiOperReplic.INS.name())
			    || organizDaReplic.getTiOperReplic()
				    .equals(Constants.TiOperReplic.MOD.name())) {
			if (pa.isOrgPresente()) {
			    GregorianCalendar c = new GregorianCalendar();
			    XMLGregorianCalendar dtIniValGreg = null;
			    XMLGregorianCalendar dtFineValGreg = null;
			    if (pa.getDtIniVal() != null) {
				c.setTime(pa.getDtIniVal());
				dtIniValGreg = DatatypeFactory.newInstance()
					.newXMLGregorianCalendar(c);
			    }
			    if (pa.getDtFineVal() != null) {
				c.setTime(pa.getDtFineVal());
				dtFineValGreg = DatatypeFactory.newInstance()
					.newXMLGregorianCalendar(c);
			    }

			    /* Se tipo operazione è INSERIMENTO */
			    if (organizDaReplic.getTiOperReplic()
				    .equals(Constants.TiOperReplic.INS.name())) {
				log.info(
					"Replica Organizzazioni SACER PREINGEST - Chiamo il ws di Inserimento Organizzazione");
				resp = client.inserimentoOrganizzazione(pa.getNmApplic(),
					pa.getIdOrganizApplic(), pa.getNmTipoOrganiz(),
					pa.getIdEnteConserv(), pa.getIdEnteGestore(),
					pa.getIdOrganizApplicPadre(), pa.getNmTipoOrganizPadre(),
					pa.getNmOrganiz(), pa.getDsOrganiz(), pa.getIdEnteConvenz(),
					dtIniValGreg, dtFineValGreg, pa.getListaTipiDato());
			    } /* Se tipo operazione è MODIFICA */ else {
				log.info(
					"Replica Organizzazioni SACER PREINGEST - Chiamo il ws di Modifica Organizzazione");
				resp = client.modificaOrganizzazione(pa.getNmApplic(),
					pa.getIdOrganizApplic(), pa.getNmTipoOrganiz(),
					pa.getIdEnteConserv(), pa.getIdEnteGestore(),
					pa.getNmOrganiz(), pa.getDsOrganiz(),
					pa.getIdOrganizApplicPadre(), pa.getNmTipoOrganizPadre(),
					pa.getIdEnteConvenz(), dtIniValGreg, dtFineValGreg,
					pa.getListaTipiDato());
			    }
			} else {
			    resp.setCdEsito(it.eng.integriam.client.ws.reporg.EsitoServizio.OK);
			}
		    } /* Se tipo operazione è CANCELLAZIONE */ else {
			log.info(
				"Replica Organizzazioni SACER PREINGEST - Chiamo il ws di Cancellazione Organizzazione");
			resp = client.cancellaOrganizzazione(pa.getNmApplic(),
				pa.getIdOrganizApplic(), pa.getNmTipoOrganiz());
		    }

		    /* Il sistema verifica la risposta del servizio di replica organizzazione */
		    EsitoServizio esitoServizio = resp.getCdEsito().name()
			    .equals(Constants.EsitoServizio.OK.name()) ? Constants.EsitoServizio.OK
				    : Constants.EsitoServizio.KO;
		    /* Scrivo l'esito della singola replica organizzazione */
		    aoHelper.writeEsitoIamOrganizDaReplic(organizDaReplic.getIdOrganizDaReplic(),
			    esitoServizio, resp.getCdErr(), resp.getDsErr());

		    String posNeg = esitoServizio.name().equals(Constants.EsitoServizio.OK.name())
			    ? "positiva"
			    : "negativa";
		    log.info(
			    "Replica Organizzazioni SACER PREINGEST - Risposta WS {} per l'organizzazione {}",
			    posNeg, organizDaReplic.getNmOrganiz());

		    if (!esitoServizio.name().equals(Constants.EsitoServizio.OK.name())
			    && !resp.getCdErr().equals(Constants.SERVIZI_ORG_002)) {
			replicaOK = false;
			result = Constants.EsitoServizio.KO.name();
		    }

		} else {
		    /* Se il client è null, ci sono stati problemi */
		    aoHelper.writeEsitoIamOrganizDaReplic(organizDaReplic.getIdOrganizDaReplic(),
			    Constants.EsitoServizio.KO, Constants.SERVIZI_ORG_001,
			    "Errore nella creazione del client per la chiamata al WS di ReplicaOrganizzazioni");
		    log.error(
			    "Replica Organizzazioni SACER PREINGEST - Risposta WS negativa per l'organizzazione {}",
			    organizDaReplic.getNmOrganiz());
		    break;
		}

	    } catch (SOAPFaultException e) {
		aoHelper.writeEsitoIamOrganizDaReplic(organizDaReplic.getIdOrganizDaReplic(),
			Constants.EsitoServizio.KO, Constants.SERVIZI_ORG_007,
			e.getFault().getFaultCode() + ": " + e.getFault().getFaultString());
		log.error(
			"Replica Organizzazioni SACER PREINGEST - Risposta WS negativa per l'organizzazione {} "
				+ Constants.SERVIZI_ORG_007
				+ " - Utente che attiva il servizio non riconosciuto o non abilitato",
			organizDaReplic.getNmOrganiz(), e);
		replicaOK = false;
		break;
	    } catch (WebServiceException e) {
		/* Se non risponde o si verifica qualche errore */
		aoHelper.writeEsitoIamOrganizDaReplic(organizDaReplic.getIdOrganizDaReplic(),
			Constants.EsitoServizio.NO_RISPOSTA, Constants.REPLICA_ORG_001,
			"Il servizio di replica organizzazione non risponde");
		log.error(
			"Replica Organizzazioni SACER PREINGEST - Risposta WS negativa per l'organizzazione "
				+ Constants.REPLICA_ORG_001
				+ " - Il servizio di replica organizzazione non risponde",
			organizDaReplic.getNmOrganiz(), e);
		replicaOK = false;
		break;
	    } catch (Exception e) {
		aoHelper.writeEsitoIamOrganizDaReplic(organizDaReplic.getIdOrganizDaReplic(),
			Constants.EsitoServizio.KO, Constants.REPLICA_ORG_001, e.getMessage());
		log.error(
			"Replica Organizzazioni SACER PREINGEST - Risposta WS negativa per l'organizzazione "
				+ organizDaReplic.getNmOrganiz() + " " + Constants.REPLICA_ORG_001,
			e);
		replicaOK = false;
		break;
	    }
	} // End organizDaReplic

	/* Scrivo nel log del job l'esito finale */
	if (!arrivoDaOnLine) {
	    if (replicaOK) {
		jobLoggerEjb.writeAtomicLog(Constants.NomiJob.ALLINEAMENTO_ORGANIZZAZIONI,
			Constants.TipiRegLogJob.FINE_SCHEDULAZIONE, null);
	    } else {
		jobLoggerEjb.writeAtomicLog(Constants.NomiJob.ALLINEAMENTO_ORGANIZZAZIONI,
			Constants.TipiRegLogJob.ERRORE,
			"Errore durante la chiamata al WS di replica organizzazione");
	    }
	}

	return result;
    }

    private ParametriInputOrganizzazioni getParametriInputOrganizzazione(
	    IamOrganizDaReplic organizDaReplic) {
	/*
	 * Creo il bean contenente i parametri di input per il WS e lo popolo diversamente a seconda
	 * che sia inserimento, modifica o cancellazione
	 */
	ParametriInputOrganizzazioni parametriInputOrganizzazioni = new ParametriInputOrganizzazioni();
	parametriInputOrganizzazioni.setNmUserid(
		configurationHelper.getValoreParamApplicByApplic(Constants.USERID_REPLICA_ORG));
	parametriInputOrganizzazioni.setCdPsw(
		configurationHelper.getValoreParamApplicByApplic(Constants.PSW_REPLICA_ORG));
	parametriInputOrganizzazioni
		.setNmApplic(configurationHelper.getValoreParamApplicByApplic(Constants.NM_APPLIC));
	parametriInputOrganizzazioni
		.setIdOrganizApplic((organizDaReplic.getIdOrganizApplic().intValue()));
	parametriInputOrganizzazioni.setNmTipoOrganiz(organizDaReplic.getNmTipoOrganiz());
	parametriInputOrganizzazioni.setOrgPresente(false);

	/* Preparo le lista dei tipi dato dell'organizzazione */
	List<PigTipoObject> listaTipiObj = new ArrayList<>();
	List<Long> idVers = new ArrayList<>();

	if (organizDaReplic.getTiOperReplic().equals(Constants.TiOperReplic.INS.name())
		|| organizDaReplic.getTiOperReplic().equals(Constants.TiOperReplic.MOD.name())) {
	    ListaTipiDato lista = new ListaTipiDato();
	    /*
	     * Devo recuperare l'organizzazione da passare al WS, se non c'è (esempio è stata
	     * cancellata da SACER_PREINGEST) non posso passare nulla
	     */
	    switch (organizDaReplic.getNmTipoOrganiz()) {
	    case "AMBIENTE":
		PigAmbienteVers ambiente = aoHelper
			.getPigAmbienteVers(organizDaReplic.getIdOrganizApplic());
		if (ambiente != null) {
		    parametriInputOrganizzazioni.setIdOrganizApplicPadre(null);
		    parametriInputOrganizzazioni.setNmTipoOrganizPadre(null);
		    parametriInputOrganizzazioni.setNmOrganiz(ambiente.getNmAmbienteVers());
		    parametriInputOrganizzazioni.setDsOrganiz(ambiente.getDsAmbienteVers());
		    parametriInputOrganizzazioni.setOrgPresente(true);
		    parametriInputOrganizzazioni
			    .setIdEnteConserv(ambiente.getIdEnteConserv().intValue());
		    parametriInputOrganizzazioni
			    .setIdEnteGestore(ambiente.getIdEnteGestore().intValue());
		    // /* Recupero la lista dei tipi dato dell'organizzazione */
		    // List<PigVers> versatori = ambiente.getPigVers();
		    // for (PigVers vers : versatori) {
		    // idVers.add(vers.getIdVers());
		    // }
		}
		break;
	    case "VERSATORE":
		PigVers vers = aoHelper.getPigVers(organizDaReplic.getIdOrganizApplic());
		if (vers != null) {
		    parametriInputOrganizzazioni.setIdOrganizApplicPadre(
			    vers.getPigAmbienteVer().getIdAmbienteVers().intValue());
		    parametriInputOrganizzazioni.setNmTipoOrganizPadre("AMBIENTE");
		    parametriInputOrganizzazioni.setNmOrganiz(vers.getNmVers());
		    parametriInputOrganizzazioni.setDsOrganiz(vers.getDsVers());
		    parametriInputOrganizzazioni.setOrgPresente(true);
		    parametriInputOrganizzazioni.setIdEnteConserv(null);
		    parametriInputOrganizzazioni.setIdEnteGestore(null);
		    idVers.add(vers.getIdVers());
		}
		break;
	    }

	    if (parametriInputOrganizzazioni.isOrgPresente() && !idVers.isEmpty()) {
		/* Recupero la lista dei tipi dato dell'organizzazione */
		listaTipiObj = aoHelper.getPigTipoObjectList(idVers);

		if (listaTipiObj != null) {
		    for (PigTipoObject tipoObj : listaTipiObj) {
			TipoDato tipoDato = new TipoDato();
			tipoDato.setNmClasseTipoDato(Constants.TipoDato.TIPO_OBJECT.name());
			tipoDato.setIdTipoDatoApplic(tipoObj.getIdTipoObject().intValue());
			tipoDato.setNmTipoDato(tipoObj.getNmTipoObject());
			tipoDato.setDsTipoDato(tipoObj.getDsTipoObject());
			lista.getTipoDato().add(tipoDato);
		    }
		}
		parametriInputOrganizzazioni.setListaTipiDato(lista);
	    } else {
		parametriInputOrganizzazioni.setListaTipiDato(new ListaTipiDato());
	    }
	    Map<String, Object> mappa = aoHelper
		    .getEnteConvenzInfo((organizDaReplic.getIdOrganizApplic()));
	    Integer idEnteConvenz = mappa.get("idEnteConvenz") != null
		    ? ((BigDecimal) mappa.get("idEnteConvenz")).intValue()
		    : null;
	    Integer idEnteFornitEstern = mappa.get("idEnteFornitEstern") != null
		    ? ((BigDecimal) mappa.get("idEnteFornitEstern")).intValue()
		    : null;
	    Date dtIniVal = mappa.get("dtIniVal") != null ? (Date) mappa.get("dtIniVal") : null;
	    Date dtFineVal = mappa.get("dtFineVal") != null ? (Date) mappa.get("dtFineVal") : null;
	    if (idEnteConvenz != null) {
		parametriInputOrganizzazioni.setIdEnteConvenz(idEnteConvenz);
	    }
	    if (idEnteFornitEstern != null) {
		parametriInputOrganizzazioni.setIdEnteConvenz(idEnteFornitEstern);
	    }
	    if (dtIniVal != null) {
		parametriInputOrganizzazioni.setDtIniVal(dtIniVal);
	    }
	    if (dtFineVal != null) {
		parametriInputOrganizzazioni.setDtFineVal(dtFineVal);
	    }
	}
	return parametriInputOrganizzazioni;
    }
}
