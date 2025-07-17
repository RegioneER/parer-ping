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

/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */
package it.eng.sacerasi.ws.invioOggettoAsincrono.ejb;

import java.io.File;
import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.sacerasi.common.Constants;
import it.eng.sacerasi.common.Constants.EsitoServizio;
import it.eng.sacerasi.common.ejb.CommonDb;
import it.eng.sacerasi.messages.MessaggiWSBundle;
import it.eng.sacerasi.ws.dto.IRispostaWS.SeverityEnum;
import it.eng.sacerasi.ws.dto.RispostaControlli;
import it.eng.sacerasi.ws.ejb.ControlliWS;
import it.eng.sacerasi.ws.invioOggettoAsincrono.dto.InvioOggettoAsincronoEstesoInput;
import it.eng.sacerasi.ws.invioOggettoAsincrono.dto.InvioOggettoAsincronoExt;
import it.eng.sacerasi.ws.invioOggettoAsincrono.dto.InvioOggettoAsincronoInput;
import it.eng.sacerasi.ws.invioOggettoAsincrono.dto.RispostaWSInvioOggettoAsincrono;
import it.eng.sacerasi.ws.response.InvioOggettoAsincronoEstesoRisposta;
import it.eng.sacerasi.ws.response.InvioOggettoAsincronoRisposta;

@Stateless(mappedName = "InvioOggettoAsincronoCheckEjb")
@LocalBean
public class InvioOggettoAsincronoCheckEjb {

    private static final Logger log = LoggerFactory.getLogger(InvioOggettoAsincronoCheckEjb.class);

    @EJB
    private ControlliWS controlliWS;
    @EJB
    private ControlliInvioOggettoAsincrono controlliInvioOggettoAsincrono;
    @EJB
    private CommonDb commonDb;

    public void checkSessione(InvioOggettoAsincronoExt invioOggettoAsincronoExt,
	    RispostaWSInvioOggettoAsincrono rispostaWs) {
	RispostaControlli rispostaControlli = new RispostaControlli();
	//
	String inputFtp = "";
	String rootFtp = "";
	if (rispostaWs.getSeverity() != SeverityEnum.ERROR) {
	    try {
		rootFtp = commonDb.getRootFtpParam();
	    } catch (Exception ex) {
		log.error(ex.getMessage());
		rispostaControlli.reset();
		rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
		rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
			String.join("\n", ExceptionUtils.getRootCauseStackTrace(ex))));
		setRispostaWsError(rispostaWs, SeverityEnum.ERROR, Constants.EsitoServizio.KO,
			rispostaControlli);
	    }
	}
	// Verifica Nome Ambiente
	if (rispostaWs.getSeverity() != SeverityEnum.ERROR) {
	    rispostaControlli.reset();
	    rispostaControlli = controlliWS.verificaNomeAmbiente(
		    invioOggettoAsincronoExt.getInvioOggettoAsincronoInput().getNmAmbiente());
	    if (!rispostaControlli.isrBoolean()) {
		if (rispostaControlli.getCodErr() == null) {
		    rispostaControlli.setCodErr(MessaggiWSBundle.PING_SENDOBJ_001);
		    rispostaControlli.setDsErr(MessaggiWSBundle
			    .getString(MessaggiWSBundle.PING_SENDOBJ_001, invioOggettoAsincronoExt
				    .getInvioOggettoAsincronoInput().getNmAmbiente()));
		    setDefaultError(invioOggettoAsincronoExt, rispostaWs, rispostaControlli);
		} else {
		    // Errore 666
		    setRispostaWsError(rispostaWs, SeverityEnum.ERROR, Constants.EsitoServizio.KO,
			    rispostaControlli);
		}
	    } else {
		rispostaWs.getInvioOggettoAsincronoRisposta().setNmAmbiente(
			invioOggettoAsincronoExt.getInvioOggettoAsincronoInput().getNmAmbiente());
	    }
	}

	// Verifica nome versatore nell'ambito dell'ambiente
	Long idVersatore = null;
	if (rispostaWs.getSeverity() != SeverityEnum.ERROR) {
	    rispostaControlli.reset();
	    rispostaControlli = controlliWS.verificaNomeVersatore(
		    invioOggettoAsincronoExt.getInvioOggettoAsincronoInput().getNmAmbiente(),
		    invioOggettoAsincronoExt.getInvioOggettoAsincronoInput().getNmVersatore());
	    if (!rispostaControlli.isrBoolean()) {
		if (rispostaControlli.getCodErr() == null) {
		    rispostaControlli.setCodErr(MessaggiWSBundle.PING_SENDOBJ_002);
		    rispostaControlli.setDsErr(MessaggiWSBundle
			    .getString(MessaggiWSBundle.PING_SENDOBJ_002, invioOggettoAsincronoExt
				    .getInvioOggettoAsincronoInput().getNmVersatore()));
		    setDefaultError(invioOggettoAsincronoExt, rispostaWs, rispostaControlli);
		} else {
		    // Errore 666
		    setRispostaWsError(rispostaWs, SeverityEnum.ERROR, Constants.EsitoServizio.KO,
			    rispostaControlli);
		}
	    } else {
		idVersatore = rispostaControlli.getrLong();
		inputFtp = rispostaControlli.getrString();
		rispostaWs.getInvioOggettoAsincronoRisposta().setNmVersatore(
			invioOggettoAsincronoExt.getInvioOggettoAsincronoInput().getNmVersatore());
	    }
	}
	invioOggettoAsincronoExt.setIdVersatore(idVersatore);

	if (rispostaWs.getSeverity() != SeverityEnum.ERROR) {
	    invioOggettoAsincronoExt.setFtpPath(StringUtils.stripToEmpty(rootFtp)
		    .concat(StringUtils.stripToEmpty(inputFtp)).concat(File.separator)
		    .concat(StringUtils.stripToEmpty(invioOggettoAsincronoExt
			    .getInvioOggettoAsincronoInput().getCdKeyObject())));
	}

	// Verifica presenza XML
	if (rispostaWs.getSeverity() != SeverityEnum.ERROR) {
	    rispostaControlli.reset();
	    rispostaControlli = controlliInvioOggettoAsincrono.verificaPresenzaXml(
		    invioOggettoAsincronoExt.getInvioOggettoAsincronoInput().getCdVersioneXml(),
		    invioOggettoAsincronoExt.getInvioOggettoAsincronoInput().getXml());
	    if (!rispostaControlli.isrBoolean()) {
		setDefaultError(invioOggettoAsincronoExt, rispostaWs, rispostaControlli);
	    }
	}

	// Verifica che chiave object sia diverso da stringa vuota e spazi
	if (rispostaWs.getSeverity() != SeverityEnum.ERROR) {
	    rispostaControlli.reset();
	    rispostaControlli = controlliWS.verificaNotBlankCdKeyObject(
		    invioOggettoAsincronoExt.getInvioOggettoAsincronoInput().getCdKeyObject(),
		    MessaggiWSBundle.PING_SENDOBJ_006);
	    if (!rispostaControlli.isrBoolean()) {
		setDefaultError(invioOggettoAsincronoExt, rispostaWs, rispostaControlli);
	    } else {
		rispostaWs.getInvioOggettoAsincronoRisposta().setCdKeyObject(
			invioOggettoAsincronoExt.getInvioOggettoAsincronoInput().getCdKeyObject());
	    }
	}
	// Verifica che chiave object sia lungo meno di 96 caratteri
	if (rispostaWs.getSeverity() != SeverityEnum.ERROR) {
	    rispostaControlli.reset();
	    rispostaControlli = controlliWS.verificaCdKeyObjectLength(
		    invioOggettoAsincronoExt.getInvioOggettoAsincronoInput().getCdKeyObject(),
		    MessaggiWSBundle.PING_SENDOBJ_019);
	    if (!rispostaControlli.isrBoolean()) {
		setDefaultError(invioOggettoAsincronoExt, rispostaWs, rispostaControlli);
	    }
	}
	Long idTipoObject = null;
	String tiVersFile = null;
	String dsRegExp = null;
	// Verifica nome tipo object
	if (rispostaWs.getSeverity() != SeverityEnum.ERROR) {
	    rispostaControlli.reset();
	    rispostaControlli = controlliWS.verificaNomeTipoObject(idVersatore,
		    invioOggettoAsincronoExt.getInvioOggettoAsincronoInput().getNmTipoObject(),
		    MessaggiWSBundle.PING_SENDOBJ_007);
	    if (!rispostaControlli.isrBoolean()) {
		setDefaultError(invioOggettoAsincronoExt, rispostaWs, rispostaControlli);
	    } else {
		rispostaWs.getInvioOggettoAsincronoRisposta().setNmTipoObject(
			invioOggettoAsincronoExt.getInvioOggettoAsincronoInput().getNmTipoObject());
		invioOggettoAsincronoExt.setNmTipoObject(
			invioOggettoAsincronoExt.getInvioOggettoAsincronoInput().getNmTipoObject());
	    }
	    idTipoObject = rispostaControlli.getrLong();
	    tiVersFile = rispostaControlli.getrString();
	    dsRegExp = (String) rispostaControlli.getrObject();
	}
	// Setto l'id tipo object
	invioOggettoAsincronoExt.setIdTipoObject(idTipoObject);
	invioOggettoAsincronoExt.setTiVersFile(tiVersFile);
	invioOggettoAsincronoExt.setDsRegExpCdVers(dsRegExp);

	// MEV#14041 Gestione codice versatore per cui si generano oggetti - servizio di versamento
	// Controllo valorizzazione cdVersGen
	if (rispostaWs.getSeverity() != SeverityEnum.ERROR) {
	    controlliWS.determinaCdVersGen(invioOggettoAsincronoExt);
	}

	// Verifica tipo versamento
	if (rispostaWs.getSeverity() != SeverityEnum.ERROR) {
	    rispostaControlli.reset();
	    rispostaControlli = controlliInvioOggettoAsincrono.verificaTipoVersamento(idTipoObject,
		    invioOggettoAsincronoExt.getInvioOggettoAsincronoInput().getXml());
	    if (!rispostaControlli.isrBoolean()) {
		setDefaultError(invioOggettoAsincronoExt, rispostaWs, rispostaControlli);
	    }
	}

	// Verifica versione XML (Se e solo se è stata verificata la presenza dell'xml
	// MEV27034 - questo controllo ora è fatto solo su alcuni tipi di oggetto per mantenere
	// compatibilità con il passato. Inoltre spostato più in basso nella catena per avere
	// il tipo versamento valorizzato.
	if (rispostaWs.getSeverity() != SeverityEnum.ERROR
		&& StringUtils.isNotBlank(
			invioOggettoAsincronoExt.getInvioOggettoAsincronoInput().getXml())
		&& (invioOggettoAsincronoExt.getTiVersFile()
			.equals(Constants.TipoVersamento.NO_ZIP.name())
			|| invioOggettoAsincronoExt.getTiVersFile()
				.equals(Constants.TipoVersamento.ZIP_NO_XML_SACER.name()))) {
	    rispostaControlli.reset();
	    rispostaControlli = controlliInvioOggettoAsincrono.verificaVersioneXML(
		    invioOggettoAsincronoExt.getInvioOggettoAsincronoInput().getCdVersioneXml(),
		    invioOggettoAsincronoExt.getDescrizione().getCompatibilitaWS());
	    if (!rispostaControlli.isrBoolean()) {
		setDefaultError(invioOggettoAsincronoExt, rispostaWs, rispostaControlli);
	    }
	}

	// Verifica accettazione motivazione
	if (rispostaWs.getSeverity() != SeverityEnum.ERROR) {
	    rispostaControlli.reset();
	    rispostaControlli = controlliInvioOggettoAsincrono.verificaAccettazioneMotivazione(
		    idTipoObject,
		    invioOggettoAsincronoExt.getInvioOggettoAsincronoInput()
			    .isFlForzaAccettazione(),
		    invioOggettoAsincronoExt.getInvioOggettoAsincronoInput().getDlMotivazione());
	    if (!rispostaControlli.isrBoolean()) {
		setDefaultError(invioOggettoAsincronoExt, rispostaWs, rispostaControlli);
	    }
	}
	rispostaWs.getInvioOggettoAsincronoRisposta().setFlForzaAccettazione(
		invioOggettoAsincronoExt.getInvioOggettoAsincronoInput().isFlForzaAccettazione());
	rispostaWs.getInvioOggettoAsincronoRisposta().setFlForzaWarning(
		invioOggettoAsincronoExt.getInvioOggettoAsincronoInput().isFlForzaWarning());
	rispostaWs.getInvioOggettoAsincronoRisposta().setFlFileCifrato(
		invioOggettoAsincronoExt.getInvioOggettoAsincronoInput().isFlFileCifrato());

	// Verifica tipo oggetto Studio Dicom
	if (rispostaWs.getSeverity() != SeverityEnum.ERROR) {
	    rispostaControlli.reset();
	    rispostaControlli = controlliInvioOggettoAsincrono.verificaStudioDicom(
		    invioOggettoAsincronoExt.getIdTipoObject(),
		    invioOggettoAsincronoExt.getInvioOggettoAsincronoInput().getXml(),
		    invioOggettoAsincronoExt.getInvioOggettoAsincronoInput().getCdKeyObject());
	    if (!rispostaControlli.isrBoolean()) {
		setDefaultError(invioOggettoAsincronoExt, rispostaWs, rispostaControlli);
	    }
	}
	if (rispostaWs.getSeverity() != SeverityEnum.ERROR) {
	    if (invioOggettoAsincronoExt
		    .getInvioOggettoAsincronoInput() instanceof InvioOggettoAsincronoEstesoInput
		    && rispostaWs
			    .getInvioOggettoAsincronoRisposta() instanceof InvioOggettoAsincronoEstesoRisposta) {
		InvioOggettoAsincronoEstesoInput input = (InvioOggettoAsincronoEstesoInput) invioOggettoAsincronoExt
			.getInvioOggettoAsincronoInput();
		InvioOggettoAsincronoEstesoRisposta risposta = (InvioOggettoAsincronoEstesoRisposta) rispostaWs
			.getInvioOggettoAsincronoRisposta();
		rispostaControlli.reset();
		if (StringUtils.isNotBlank(input.getNmAmbienteObjectPadre())
			&& StringUtils.isNotBlank(input.getNmVersatoreObjectPadre())
			&& StringUtils.isNotBlank(input.getCdKeyObjectPadre())) {
		    // Gestione estesa
		    rispostaControlli = controlliWS.verificaCdKeyObject(
			    input.getNmAmbienteObjectPadre(), input.getNmVersatoreObjectPadre(),
			    input.getCdKeyObjectPadre());
		    if (!rispostaControlli.isrBoolean()
			    || (StringUtils.isNotBlank(rispostaControlli.getrString())
				    && rispostaControlli.getrString()
					    .equals(Constants.StatoOggetto.ANNULLATO.name()))) {
			rispostaControlli.setCodErr(MessaggiWSBundle.PING_SENDOBJ_013);
			rispostaControlli.setDsErr(
				MessaggiWSBundle.getString(MessaggiWSBundle.PING_SENDOBJ_013));
			setDefaultError(invioOggettoAsincronoExt, rispostaWs, rispostaControlli);
		    } else if (input.getNiTotObjectFigli() == null
			    || input.getPgObjectFiglio() == null) {
			rispostaControlli.setCodErr(MessaggiWSBundle.PING_SENDOBJ_014);
			rispostaControlli.setDsErr(
				MessaggiWSBundle.getString(MessaggiWSBundle.PING_SENDOBJ_014));
			setDefaultError(invioOggettoAsincronoExt, rispostaWs, rispostaControlli);
		    } else {
			Long idObjectPadre = rispostaControlli.getrLong();
			BigDecimal niTotObjectDb = (BigDecimal) rispostaControlli.getrObject();
			invioOggettoAsincronoExt.setIdOggettoPadre(idObjectPadre);
			rispostaControlli.reset();
			rispostaControlli = controlliInvioOggettoAsincrono
				.verificaOggettoFiglio(idObjectPadre, input.getPgObjectFiglio());
			if (rispostaControlli.isrBoolean()) {
			    rispostaControlli.setCodErr(MessaggiWSBundle.PING_SENDOBJ_015);
			    rispostaControlli.setDsErr(
				    MessaggiWSBundle.getString(MessaggiWSBundle.PING_SENDOBJ_015));
			    setDefaultError(invioOggettoAsincronoExt, rispostaWs,
				    rispostaControlli);
			} else if (niTotObjectDb != null
				&& niTotObjectDb.compareTo(input.getNiTotObjectFigli()) != 0) {
			    rispostaControlli.setCodErr(MessaggiWSBundle.PING_SENDOBJ_016);
			    rispostaControlli.setDsErr(
				    MessaggiWSBundle.getString(MessaggiWSBundle.PING_SENDOBJ_016));
			    setDefaultError(invioOggettoAsincronoExt, rispostaWs,
				    rispostaControlli);
			} else if (niTotObjectDb != null
				&& input.getPgObjectFiglio().compareTo(niTotObjectDb) > 0) {
			    rispostaControlli.setCodErr(MessaggiWSBundle.PING_SENDOBJ_017);
			    rispostaControlli.setDsErr(
				    MessaggiWSBundle.getString(MessaggiWSBundle.PING_SENDOBJ_017));
			    setDefaultError(invioOggettoAsincronoExt, rispostaWs,
				    rispostaControlli);
			} else if (tiVersFile != null && tiVersFile
				.equals(Constants.TipoVersamento.DA_TRASFORMARE.name())) {
			    rispostaControlli.setCodErr(MessaggiWSBundle.PING_SENDOBJ_018);
			    rispostaControlli.setDsErr(
				    MessaggiWSBundle.getString(MessaggiWSBundle.PING_SENDOBJ_018));
			    setDefaultError(invioOggettoAsincronoExt, rispostaWs,
				    rispostaControlli);
			}
		    }

		    risposta.setDsObject(input.getDsObject());
		    risposta.setNmAmbienteObjectPadre(input.getNmAmbienteObjectPadre());
		    risposta.setNmVersatoreObjectPadre(input.getNmVersatoreObjectPadre());
		    risposta.setCdKeyObjectPadre(input.getCdKeyObjectPadre());
		    risposta.setNiTotObjectFigli(input.getNiTotObjectFigli());
		    risposta.setPgObjectFiglio(input.getPgObjectFiglio());
		    risposta.setNiUnitaDocAttese(input.getNiUnitaDocAttese());
		    risposta.setCdVersGen(input.getCdVersGen());
		    risposta.setTiGestOggettiFigli(input.getTiGestOggettiFigli());
		} else if (StringUtils.isBlank(input.getNmAmbienteObjectPadre())
			&& StringUtils.isBlank(input.getNmVersatoreObjectPadre())
			&& StringUtils.isBlank(input.getCdKeyObjectPadre())) {
		    // Gestione invio 'standard'
		    // NON FA NIENTE, MA VA CONTROLLATO
		} else {
		    // Errore PING_SENDOBJ_012
		    rispostaControlli.setCodErr(MessaggiWSBundle.PING_SENDOBJ_012);
		    rispostaControlli.setDsErr(
			    MessaggiWSBundle.getString(MessaggiWSBundle.PING_SENDOBJ_012));
		    setDefaultError(invioOggettoAsincronoExt, rispostaWs, rispostaControlli);
		}
		if (rispostaWs.getSeverity() != SeverityEnum.ERROR) {
		    rispostaControlli.reset();
		    String cdVersGen = input.getCdVersGen();
		    invioOggettoAsincronoExt.setCdVersGen(cdVersGen);
		    if (tiVersFile != null
			    && tiVersFile.equals(Constants.TipoVersamento.DA_TRASFORMARE.name())) {
			invioOggettoAsincronoExt
				.setTiGestOggettiFigli(input.getTiGestOggettiFigli());
			if (StringUtils.isBlank(cdVersGen)) {
			    if (StringUtils.isBlank(invioOggettoAsincronoExt.getDsRegExpCdVers())) {
				rispostaControlli.setCodErr(MessaggiWSBundle.PING_SENDOBJ_021);
				rispostaControlli.setDsErr(MessaggiWSBundle.getString(
					MessaggiWSBundle.PING_SENDOBJ_021,
					input.getNmTipoObject()));
				setDefaultError(invioOggettoAsincronoExt, rispostaWs,
					rispostaControlli);
			    } else {
				Pattern regxp = Pattern
					.compile(invioOggettoAsincronoExt.getDsRegExpCdVers());
				Matcher matcher = regxp.matcher(invioOggettoAsincronoExt
					.getInvioOggettoAsincronoInput().getCdKeyObject());
				if (matcher.matches()) {
				    cdVersGen = matcher.group(1);
				    invioOggettoAsincronoExt.setCdVersGen(cdVersGen);
				} else {
				    rispostaControlli.setCodErr(MessaggiWSBundle.PING_SENDOBJ_022);
				    rispostaControlli.setDsErr(MessaggiWSBundle
					    .getString(MessaggiWSBundle.PING_SENDOBJ_022));
				    setDefaultError(invioOggettoAsincronoExt, rispostaWs,
					    rispostaControlli);
				}
			    }
			}
			if (StringUtils.isBlank(rispostaControlli.getCodErr())) {
			    if (StringUtils.isNotBlank(cdVersGen)) {
				rispostaControlli = controlliInvioOggettoAsincrono
					.verificaCdVersGen(
						invioOggettoAsincronoExt.getIdTipoObject(),
						cdVersGen);
				if (!rispostaControlli.isrBoolean()) {
				    rispostaControlli.setCodErr(MessaggiWSBundle.PING_SENDOBJ_023);
				    rispostaControlli.setDsErr(MessaggiWSBundle
					    .getString(MessaggiWSBundle.PING_SENDOBJ_023));
				    setDefaultError(invioOggettoAsincronoExt, rispostaWs,
					    rispostaControlli);
				}
			    } else {
				rispostaControlli.setCodErr(MessaggiWSBundle.PING_SENDOBJ_024);
				rispostaControlli.setDsErr(MessaggiWSBundle
					.getString(MessaggiWSBundle.PING_SENDOBJ_024));
				setDefaultError(invioOggettoAsincronoExt, rispostaWs,
					rispostaControlli);
			    }
			}
		    } else {
			// Tipo di versamento diverso da DA_TRASFORMARE
			rispostaControlli.reset();
			if (StringUtils.isNotBlank(input.getTiGestOggettiFigli())) {
			    rispostaControlli.setCodErr(MessaggiWSBundle.PING_SENDOBJ_025);
			    rispostaControlli.setDsErr(
				    MessaggiWSBundle.getString(MessaggiWSBundle.PING_SENDOBJ_025));
			    setDefaultError(invioOggettoAsincronoExt, rispostaWs,
				    rispostaControlli);
			}
		    }
		}
		// Risoluzione della MAC #14809 - WS invio oggetto: non viene calcolato il versatore
		// per cui generare
		// oggetti
		// Nuovo ramo IF aggiunto
	    } else if (invioOggettoAsincronoExt
		    .getInvioOggettoAsincronoInput() instanceof InvioOggettoAsincronoInput
		    && rispostaWs
			    .getInvioOggettoAsincronoRisposta() instanceof InvioOggettoAsincronoRisposta) {
		rispostaControlli.reset();
		if (tiVersFile != null
			&& tiVersFile.equals(Constants.TipoVersamento.DA_TRASFORMARE.name())) {
		    rispostaControlli = controlliWS
			    .verificaCdVersGenCasoNonEsteso(invioOggettoAsincronoExt);
		    if (!rispostaControlli.isrBoolean()) {
			setDefaultError(invioOggettoAsincronoExt, rispostaWs, rispostaControlli);
		    }
		}
	    }
	}
	// Verifica valore di default tiGestOggettiFigli
	if (rispostaWs.getSeverity() != SeverityEnum.ERROR) {
	    if (tiVersFile != null
		    && tiVersFile.equals(Constants.TipoVersamento.DA_TRASFORMARE.name())
		    && StringUtils.isBlank(invioOggettoAsincronoExt.getTiGestOggettiFigli())) {
		invioOggettoAsincronoExt.setTiGestOggettiFigli(
			Constants.TipoGestioneOggettiFigli.AUTOMATICA.name());
	    }
	}

	// MEV 30939 Verifica esistenza della partizione rimossa da questo punto
    }

    public Long checkObject(InvioOggettoAsincronoExt invioOggettoAsincronoExt,
	    RispostaWSInvioOggettoAsincrono rispostaWs) {
	RispostaControlli rispostaControlli = new RispostaControlli();
	//
	Long idObject = null;
	if (rispostaWs.getSeverity() != SeverityEnum.ERROR) {
	    rispostaControlli.reset();
	    rispostaControlli = controlliWS.verificaCdKeyObject(
		    invioOggettoAsincronoExt.getInvioOggettoAsincronoInput().getNmAmbiente(),
		    invioOggettoAsincronoExt.getInvioOggettoAsincronoInput().getNmVersatore(),
		    invioOggettoAsincronoExt.getInvioOggettoAsincronoInput().getCdKeyObject());
	    if (!rispostaControlli.isrBoolean()) {
		if (invioOggettoAsincronoExt.getNmTipoObject()
			.equalsIgnoreCase(Constants.STUDIO_DICOM)) {
		    rispostaControlli.reset();
		    rispostaControlli = controlliInvioOggettoAsincrono.verificaOggettoDicom(
			    invioOggettoAsincronoExt.getIdTipoObject(),
			    invioOggettoAsincronoExt.getDcmHashDicom(), invioOggettoAsincronoExt
				    .getInvioOggettoAsincronoInput().isFlForzaWarning());
		    if (!rispostaControlli.isrBoolean()) {
			invioOggettoAsincronoExt.setFlRegistraObject(true);
			invioOggettoAsincronoExt.setFlRegistraXMLObject(true);
			invioOggettoAsincronoExt.setFlRegistraDatiSpecDicom(true);
			setRispostaWsError(rispostaWs, SeverityEnum.WARNING,
				Constants.EsitoServizio.WARN, rispostaControlli);
		    }
		}
	    } else {
		idObject = rispostaControlli.getrLong();
		rispostaControlli.reset();
		/*
		 * MEV#15178 - Modifica Tipo oggetto in dettaglio oggetto per gli oggetti da
		 * trasformare Inserito il controllo che se un oggetto esiste già ed il tipo oggetto
		 * è cambiato, la modifica è possibile se i due oggetti sono tutti e due dello
		 * stesso tipo (DA_TRASFORMARE)
		 */
		rispostaControlli = controlliInvioOggettoAsincrono.verificaModificaTipoOggetto(
			idObject,
			invioOggettoAsincronoExt.getInvioOggettoAsincronoInput().getNmTipoObject(),
			invioOggettoAsincronoExt.getTiVersFile());
		if (!rispostaControlli.isrBoolean()) {
		    setDefaultError(invioOggettoAsincronoExt, rispostaWs, rispostaControlli);
		}
		if (rispostaWs.getSeverity() != SeverityEnum.ERROR) {
		    rispostaControlli.reset();
		    rispostaControlli = controlliInvioOggettoAsincrono.verificaStatoOggetto(
			    idObject,
			    invioOggettoAsincronoExt.getInvioOggettoAsincronoInput()
				    .isFlForzaAccettazione(),
			    invioOggettoAsincronoExt.getFtpPath(),
			    invioOggettoAsincronoExt.getTiVersFile());
		    if (!rispostaControlli.isrBoolean()) {
			if (rispostaControlli.getCodErr()
				.equals(MessaggiWSBundle.PING_SENDOBJ_OBJ_004)) {
			    invioOggettoAsincronoExt.setFlRegistraObject(true);
			    invioOggettoAsincronoExt.setFlRegistraXMLObject(true);
			    invioOggettoAsincronoExt.setFlRegistraDatiSpecDicom(true);
			    setRispostaWsError(rispostaWs, SeverityEnum.WARNING,
				    Constants.EsitoServizio.WARN, rispostaControlli);
			} else {
			    setDefaultError(invioOggettoAsincronoExt, rispostaWs,
				    rispostaControlli);
			}
		    }
		}
		if (rispostaWs.getSeverity() != SeverityEnum.ERROR) {
		    rispostaControlli.reset();
		    rispostaControlli = controlliInvioOggettoAsincrono.verificaOggettoPadre(
			    idObject, invioOggettoAsincronoExt.getIdOggettoPadre());
		    if (!rispostaControlli.isrBoolean()) {
			setDefaultError(invioOggettoAsincronoExt, rispostaWs, rispostaControlli);
		    }
		}
	    }
	}

	// MEV 30939 Verifica esistenza della partizione rimossa da questo punto

	return idObject;
    }

    private void setRispostaWsError(RispostaWSInvioOggettoAsincrono rispostaWs, SeverityEnum sev,
	    EsitoServizio esito, RispostaControlli rispostaControlli) {
	rispostaWs.setSeverity(sev);
	rispostaWs.setErrorCode(rispostaControlli.getCodErr());
	rispostaWs.setErrorMessage(rispostaControlli.getDsErr());
	rispostaWs.getInvioOggettoAsincronoRisposta().setCdEsito(esito);
	rispostaWs.getInvioOggettoAsincronoRisposta().setCdErr(rispostaControlli.getCodErr());
	rispostaWs.getInvioOggettoAsincronoRisposta().setDsErr(rispostaControlli.getDsErr());
    }

    private void setDefaultError(InvioOggettoAsincronoExt invioOggettoAsincronoExt,
	    RispostaWSInvioOggettoAsincrono rispostaWs, RispostaControlli rispostaControlli) {
	invioOggettoAsincronoExt.setFlRegistraObject(false);
	invioOggettoAsincronoExt.setFlRegistraXMLObject(false);
	invioOggettoAsincronoExt.setFlRegistraDatiSpecDicom(false);
	setRispostaWsError(rispostaWs, SeverityEnum.ERROR, Constants.EsitoServizio.KO,
		rispostaControlli);
    }

}
