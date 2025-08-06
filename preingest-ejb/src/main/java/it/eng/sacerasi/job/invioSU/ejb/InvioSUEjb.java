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
package it.eng.sacerasi.job.invioSU.ejb;

import it.eng.parer.objectstorage.dto.BackendStorage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.ejb.ApplicationException;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.parer.objectstorage.dto.ObjectStorageBackend;
import it.eng.parer.objectstorage.exceptions.ObjectStorageException;
import it.eng.parer.objectstorage.helper.SalvataggioBackendHelper;
import it.eng.sacerasi.common.Constants;
import it.eng.sacerasi.entity.PigErrore;
import it.eng.sacerasi.entity.PigObject;
import it.eng.sacerasi.entity.PigStrumUrbCollegamenti;
import it.eng.sacerasi.entity.PigStrumUrbDocumenti;
import it.eng.sacerasi.entity.PigStrumUrbDocumentiStorage;
import it.eng.sacerasi.entity.PigStrumentiUrbanistici;
import it.eng.sacerasi.entity.PigStrumentiUrbanistici.TiStato;
import it.eng.sacerasi.entity.PigTipoObject;
import it.eng.sacerasi.entity.PigVers;
import it.eng.sacerasi.grantEntity.SIOrgEnteSiam;
import it.eng.sacerasi.helper.GenericHelper;
import it.eng.sacerasi.job.ejb.JobLogger;
import it.eng.sacerasi.job.util.NfsUtils;
import it.eng.sacerasi.messages.MessaggiHelper;
import it.eng.sacerasi.strumentiUrbanistici.ejb.StrumentiUrbanisticiEjb;
import it.eng.sacerasi.strumentiUrbanistici.ejb.StrumentiUrbanisticiHelper;
import it.eng.sacerasi.su.xml.invioSU.StrumentiUrbanistici;
import it.eng.sacerasi.su.xml.invioSU.StrumentiUrbanistici.Collegamenti;
import it.eng.sacerasi.su.xml.invioSU.StrumentiUrbanistici.Collegamenti.Collegamento;
import it.eng.sacerasi.su.xml.invioSU.StrumentiUrbanistici.Ente;
import it.eng.sacerasi.su.xml.invioSU.StrumentiUrbanistici.TipiDocumento;
import it.eng.sacerasi.su.xml.invioSU.StrumentiUrbanistici.TipiDocumento.TipoDocumento;
import it.eng.sacerasi.su.xml.invioSU.StrumentiUrbanistici.TipoDocumentoPrincipale;
import it.eng.sacerasi.su.xml.invioSU.TipoDocumentoEnumeration;
import it.eng.sacerasi.su.xml.invioSU.TipoDocumentoPrincipaleEnumeration;
import it.eng.sacerasi.versamento.ejb.VersamentoOggettoEjb;
import it.eng.sacerasi.viewEntity.PigVSuCheck;
import it.eng.sacerasi.web.helper.AmministrazioneHelper;
import it.eng.sacerasi.web.helper.ConfigurationHelper;
import it.eng.sacerasi.web.util.Utils;
import it.eng.sacerasi.ws.ejb.XmlContextCache;
import it.eng.sacerasi.ws.invioOggettoAsincrono.ejb.InvioOggettoAsincronoEjb;
import it.eng.sacerasi.ws.notificaTrasferimento.dto.FileDepositatoType;
import it.eng.sacerasi.ws.notificaTrasferimento.dto.ListaFileDepositatoType;
import it.eng.sacerasi.ws.notificaTrasferimento.ejb.NotificaTrasferimentoEjb;
import it.eng.sacerasi.ws.response.InvioOggettoAsincronoRisposta;
import it.eng.sacerasi.ws.response.NotificaTrasferimentoRisposta;
import java.util.Optional;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

/**
 *
 * @author Gilioli_P
 */
@Stateless(mappedName = "InvioSUEjb")
@LocalBean
public class InvioSUEjb {

    Logger log = LoggerFactory.getLogger(InvioSUEjb.class);
    @EJB
    private InvioSUHelper invioSUHelper;
    @EJB
    private JobLogger jobLoggerEjb;
    @EJB
    private InvioOggettoAsincronoEjb invioOggettoAsincronoEjb;
    @EJB
    private NotificaTrasferimentoEjb notificaTrasferimentoEjb;
    @EJB
    private StrumentiUrbanisticiEjb strumentiUrbanisticiEjb;
    @EJB
    private GenericHelper genericHelper;
    @EJB
    private XmlContextCache xmlContextCache;
    @EJB
    private MessaggiHelper messaggiHelper;
    @EJB
    private ConfigurationHelper configurationHelper;
    @EJB
    private InvioSUEjb me;
    @EJB
    private AmministrazioneHelper amministrazioneHelper;
    @EJB
    private StrumentiUrbanisticiHelper strumentiUrbanisticiHelper;
    @EJB
    private SalvataggioBackendHelper salvataggioBackendHelper;
    @EJB
    private VersamentoOggettoEjb versamentoOggettoEjb;

    public void invioSU() throws IOException {
	log.info("{} --- Chiamata JOB per invio strumenti urbanistici",
		InvioSUEjb.class.getSimpleName());
	List<Long> idStrumentiUrbanisticiDaInviare = invioSUHelper
		.getIdStrumentiUrbanisticiDaInviare();
	log.info("Recuperati {} strumenti urbanistici da inviare",
		idStrumentiUrbanisticiDaInviare.size());

	for (Long idStrumentoUrbanisticoDaInviare : idStrumentiUrbanisticiDaInviare) {
	    // Apro una transazione (recupero un proxy per invocare il metodo con una nuova
	    // transazione)
	    try {
		me.gestisciInvioStrumentoUrbanistico(idStrumentoUrbanisticoDaInviare);
	    } catch (InvioSUException e) {
		registraErroreStrumentoUrbanistico(idStrumentoUrbanisticoDaInviare, e.getCdErr(),
			e.getDsErr(), TiStato.ERRORE);
	    } catch (Exception ex) {
		PigErrore errore = messaggiHelper.retrievePigErrore("PING-ERRSU23");
		registraErroreStrumentoUrbanistico(idStrumentoUrbanisticoDaInviare,
			errore.getCdErrore(), errore.getDsErrore(), TiStato.ERRORE);
	    }
	}

	jobLoggerEjb.writeAtomicLog(Constants.NomiJob.INVIO_STRUMENTI_URBANISTICI,
		Constants.TipiRegLogJob.FINE_SCHEDULAZIONE, null);
	log.info("{} --- FINE chiamata per invio strumenti urbanistici",
		InvioSUEjb.class.getSimpleName());
    }

    @ApplicationException(rollback = true)
    private class InvioSUException extends Exception {

	private static final long serialVersionUID = 1L;

	private long idStrumentoUrbanisticoDaInviare;
	private String cdErr;
	private String dsErr;

	public InvioSUException(final long idStrumentoUrbanisticoDaInviare, final String cdErr,
		final String dsErr) {
	    this.idStrumentoUrbanisticoDaInviare = idStrumentoUrbanisticoDaInviare;
	    this.cdErr = cdErr;
	    this.dsErr = dsErr;
	}

	public long getIdStrumentoUrbanisticoDaInviare() {
	    return idStrumentoUrbanisticoDaInviare;
	}

	public String getCdErr() {
	    return cdErr;
	}

	public String getDsErr() {
	    return dsErr;
	}
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void gestisciInvioStrumentoUrbanistico(long idStrumentoUrbanisticoDaInviare)
	    throws InvioSUException, ObjectStorageException, IOException {

	String nmTipoObject = "StrumentoUrbanistico";

	// Locko lo strumento urbanistico
	PigStrumentiUrbanistici strumentoUrbanisticoDaInviare = genericHelper
		.findByIdWithLock(PigStrumentiUrbanistici.class, idStrumentoUrbanisticoDaInviare);
	PigVers vers = strumentoUrbanisticoDaInviare.getPigVer();

	if (!invioSUHelper.existsPigObjectPerVersatoreNoStrumUrb(
		strumentoUrbanisticoDaInviare.getPigVer().getIdVers(),
		strumentoUrbanisticoDaInviare.getCdKey())) {

	    // Verifico se lo stato è ancora "RICHIESTA_INVIO"
	    if (strumentoUrbanisticoDaInviare.getTiStato().equals(TiStato.RICHIESTA_INVIO)) {
		strumentoUrbanisticoDaInviare.setCdErr(null);
		strumentoUrbanisticoDaInviare.setDsErr(null);

		// A quel punto locko pure i figli
		for (PigStrumUrbDocumenti strumUrbDocumenti : strumentoUrbanisticoDaInviare
			.getPigStrumUrbDocumentis()) {
		    genericHelper.findByIdWithLock(PigStrumUrbDocumenti.class,
			    strumUrbDocumenti.getIdStrumUrbDocumenti());
		}

		// Controlli tramite vista PIG_V_SU_CHECK
		PigVSuCheck check = genericHelper.findViewById(PigVSuCheck.class, BigDecimal
			.valueOf(strumentoUrbanisticoDaInviare.getIdStrumentiUrbanistici()));
		// Controlli sui documenti dello strumento urbanistico attraverso i valori della
		// vista
		if (check.getFlVerificaErrata().equals("1")
			|| check.getFlVerificaInCorso().equals("1")
			|| check.getFlFileMancante().equals("1")) {
		    log.error("{} --- ERRORE condizioni invio strumenti urbanistici",
			    InvioSUEjb.class.getSimpleName());
		    PigErrore errore = messaggiHelper.retrievePigErrore("PING-ERRSU01");
		    throw new InvioSUException(idStrumentoUrbanisticoDaInviare,
			    errore.getCdErrore(), errore.getDsErrore());
		}

		/**
		 * ******** AVVIO IL PROCESSO DI INVIO ********
		 */
		// Setta lo stato di PigStrumentiUrbanistici
		// MEV 31096
		strumentiUrbanisticiHelper.creaStatoStorico(strumentoUrbanisticoDaInviare,
			strumentoUrbanisticoDaInviare.getTiStato().name(),
			strumentoUrbanisticoDaInviare.getDtStato(), "");

		strumentoUrbanisticoDaInviare
			.setTiStato(PigStrumentiUrbanistici.TiStato.INVIO_IN_CORSO);
		strumentoUrbanisticoDaInviare.setDtStato(new Date());
		genericHelper.getEntityManager().flush();

		String xmlVersamento = "";
		try {
		    // Genero l'xml di versamento
		    xmlVersamento = creaXml(strumentoUrbanisticoDaInviare);
		} catch (Exception ex) {
		    log.error("{} --- ERRORE creazione XML invio strumenti urbanistici: ",
			    InvioSUEjb.class.getSimpleName(), ex);
		    PigErrore errore = messaggiHelper.retrievePigErroreNewTx("PING-ERRSU14");
		    throw new InvioSUException(idStrumentoUrbanisticoDaInviare,
			    errore.getCdErrore(), errore.getDsErrore());
		}

		// Preparo il file zip contenente tutti i files relativi allo strumento urbanistico
		// +
		// StrumentiUrbanistici.xml
		File fileXmlVersamento = null;

		String nomeFilePacchetto = strumentoUrbanisticoDaInviare.getCdKey() + ".zip";
		String rootFtp = configurationHelper
			.getValoreParamApplicByApplic(Constants.ROOT_FTP);
		String dsPathInputFtp = vers.getDsPathInputFtp();
		String dirCompletaFtp = rootFtp + dsPathInputFtp;
		File fileTemporaneoGenerale = null;
		try {
		    fileTemporaneoGenerale = new File(dirCompletaFtp + nomeFilePacchetto + ".TEMP");
		    try (FileOutputStream fos = new FileOutputStream(fileTemporaneoGenerale);
			    ZipOutputStream zos = new ZipOutputStream(fos)) {
			// Creo il file con l'xml
			fileXmlVersamento = File.createTempFile("StrumentiUrbanistici.xml", "",
				new File(dirCompletaFtp));
			FileUtils.writeStringToFile(fileXmlVersamento, xmlVersamento,
				StandardCharsets.UTF_8);
			// Lo aggiungo allo zip
			addToZipFile(fileXmlVersamento, zos, "StrumentiUrbanistici.xml");
			// Recupero i file dall'object storage
			List<PigStrumUrbDocumenti> documentiDaInviare = invioSUHelper
				.getDocumentiDaInviare(
					strumentoUrbanisticoDaInviare.getIdStrumentiUrbanistici());
			//

			for (PigStrumUrbDocumenti strumUrbDocumenti : documentiDaInviare) {
			    // MEV 34843
			    PigStrumUrbDocumentiStorage pigStrumUrbDocumentiStorage = strumUrbDocumenti
				    .getPigStrumUrbDocumentiStorage();
			    BackendStorage backend = salvataggioBackendHelper
				    .getBackend(pigStrumUrbDocumentiStorage.getIdDecBackend());
			    ObjectStorageBackend config = salvataggioBackendHelper
				    .getObjectStorageConfigurationForStrumentiUrbanistici(
					    backend.getBackendName(),
					    pigStrumUrbDocumentiStorage.getNmBucket());

			    String nmFileOs = pigStrumUrbDocumentiStorage.getCdKeyFile();
			    String nmFileOrig = strumUrbDocumenti.getNmFileOrig();
			    // Chiamata di tipo HEAD (non contiene il body in quanto ho bisogno di
			    // una sola
			    // informazione
			    // stile ack)
			    boolean doesObjectExist = salvataggioBackendHelper
				    .doesObjectExist(config, nmFileOs);
			    if (doesObjectExist) {
				File tempFile = File.createTempFile(nmFileOrig, "",
					new File(dirCompletaFtp));
				try (FileOutputStream fosTemp = new FileOutputStream(tempFile);) {
				    ResponseInputStream<GetObjectResponse> objectContent = salvataggioBackendHelper
					    .getObject(config, nmFileOs);
				    // Partendo dall'input stream S3 Amazon, recupero il file
				    IOUtils.copy(objectContent, fosTemp);
				    String nomeFileLowerCase = strumUrbDocumenti
					    .getPigStrumUrbValDoc().getNmTipoDocumento()
					    .replace(" ", "_").toLowerCase() + ".zip";
				    // Aggiungo il file scaricato dall'OS al file zip
				    addToZipFile(tempFile, zos, nomeFileLowerCase);
				} finally {
				    if (tempFile != null) {
					FileUtils.deleteQuietly(tempFile);
				    }
				}
			    } else {
				log.error("{} --- ERRORE creazione ZIP invio strumenti urbanistici",
					InvioSUEjb.class.getSimpleName());
				PigErrore errore = messaggiHelper.retrievePigErrore("PING-ERRSU04");
				String dsErrore = StringUtils.replace(errore.getDsErrore(), "{0}",
					strumUrbDocumenti.getPigStrumUrbDocumentiStorage()
						.getCdKeyFile());
				throw new InvioSUException(idStrumentoUrbanisticoDaInviare,
					errore.getCdErrore(), dsErrore);
			    }
			}
		    } catch (Exception ex) {
			log.error("{} --- ERRORE creazione ZIP invio strumenti urbanistici",
				InvioSUEjb.class.getSimpleName(), ex);
			PigErrore errore = messaggiHelper.retrievePigErrore("PING-ERRSU15");
			throw new InvioSUException(idStrumentoUrbanisticoDaInviare,
				errore.getCdErrore(), errore.getDsErrore());
		    }
		    String nmUserid = strumentoUrbanisticoDaInviare.getIamUser().getNmUserid();
		    String nmAmbienteVers = vers.getPigAmbienteVer().getNmAmbienteVers();
		    String nmVers = vers.getNmVers();
		    String cdKeyObject = strumentoUrbanisticoDaInviare.getCdKey();
		    // Controlla che nel sistema non esista già l’ oggetto in fase di invio
		    if ((!invioSUHelper.existsPigObjectPerVersatore(vers.getIdVers(),
			    strumentoUrbanisticoDaInviare.getCdKey())) || // Ora include anche gli
									  // oggetti in stato
									  // annullato per
									  // reinviarli
			    invioSUHelper.existsPigObjectPerVersatoreStrumUrbAnnullato(
				    vers.getIdVers(), strumentoUrbanisticoDaInviare.getCdKey())) {
			// Chiama il servizio NotificaInvioOggetto (metodo invioOggettoAsincrono)
			PigTipoObject pigTipoObject = amministrazioneHelper.getPigTipoObjectByName(
				nmTipoObject, new BigDecimal(vers.getIdVers()));
			boolean flFileCifrato = false;
			boolean flForzaWarning = false;
			boolean flForzaAccettazione = false;
			String cdVersioneXML = "1.0";
			InvioOggettoAsincronoRisposta risposta = null;
			try {
			    String priorita = null;
			    if (pigTipoObject != null && pigTipoObject.getTiPriorita() != null) {
				priorita = it.eng.sacerasi.web.util.Constants.ComboFlagPrioTrasfType
					.getEnumByString(pigTipoObject.getTiPriorita());
			    }
			    risposta = invioOggettoAsincronoEjb.invioOggettoAsincronoEsteso(
				    nmUserid, nmAmbienteVers, nmVers, cdKeyObject, null,
				    nmTipoObject, flFileCifrato, flForzaWarning,
				    flForzaAccettazione, null, cdVersioneXML, xmlVersamento, null,
				    null, null, null, null, null, null, null, priorita, null);

			} catch (Exception e) {
			    log.error("{} --- ERRORE invio strumenti urbanistici",
				    InvioSUEjb.class.getSimpleName());
			    PigErrore errore = messaggiHelper
				    .retrievePigErroreNewTx("PING-ERRSU17");
			    throw new InvioSUException(idStrumentoUrbanisticoDaInviare,
				    errore.getCdErrore(), errore.getDsErrore() + e);
			}
			if (risposta.getCdEsito().equals(Constants.EsitoServizio.KO)) {
			    PigErrore errore = messaggiHelper
				    .retrievePigErrore(risposta.getCdErr());
			    throw new InvioSUException(idStrumentoUrbanisticoDaInviare,
				    errore.getCdErrore(), errore.getDsErrore());
			}
		    } else if (invioSUHelper.existsPigObjectPerVersatoreStrumUrbInAttesaFile(
			    vers.getIdVers(), strumentoUrbanisticoDaInviare.getCdKey())) {
			//
		    } else {
			log.error("{} --- ERRORE invio strumenti urbanistici",
				InvioSUEjb.class.getSimpleName());
			PigErrore errore = messaggiHelper.retrievePigErrore("PING-ERRSU01"); // TODO
											     // DA
											     // MODIFICARE
											     // IN
											     // 21
			throw new InvioSUException(idStrumentoUrbanisticoDaInviare,
				errore.getCdErrore(), errore.getDsErrore());
		    }

		    // MEV 34843
		    PigTipoObject pigTipoObject = amministrazioneHelper
			    .getPigTipoObjectByName(nmTipoObject, new BigDecimal(vers.getIdVers()));
		    BigDecimal idAmbiente = BigDecimal
			    .valueOf(vers.getPigAmbienteVer().getIdAmbienteVers());
		    BigDecimal idVers = BigDecimal.valueOf(vers.getIdVers());
		    BigDecimal idTipoObject = BigDecimal.valueOf(pigTipoObject.getIdTipoObject());
		    BackendStorage backendVersamento = salvataggioBackendHelper
			    .getBackendForVersamento(idAmbiente, idVers, idTipoObject);
		    String nmFileOs = null;

		    try {
			if (backendVersamento.isFile()) {
			    // Effettua lo scarico del nuovo file da trasformare nella cartella ftp
			    // di SacerPing
			    // dell'utente
			    // relativo nella cartella INPUT_FOLDER
			    NfsUtils.createEmptyDir(dirCompletaFtp + cdKeyObject);
			    Files.move(Paths.get(dirCompletaFtp + nomeFilePacchetto + ".TEMP"),
				    Paths.get(dirCompletaFtp + cdKeyObject + "/" + cdKeyObject
					    + it.eng.xformer.common.Constants.STANDARD_PACKAGE_EXTENSION));
			} else if (backendVersamento.isObjectStorage()) {
			    // MEV 34843
			    ObjectStorageBackend config = salvataggioBackendHelper
				    .getObjectStorageConfigurationForVersamento(
					    backendVersamento.getBackendName());
			    nmFileOs = versamentoOggettoEjb.computeOsFileKey(idVers, cdKeyObject,
				    VersamentoOggettoEjb.OS_KEY_POSTFIX.PIGOBJECT.name());
			    salvataggioBackendHelper.putS3Object(config, nmFileOs,
				    fileTemporaneoGenerale, Optional.empty());
			}
		    } catch (IOException | ObjectStorageException ex) {
			log.error(InvioSUEjb.class.getSimpleName()
				+ " --- ERRORE invio strumenti urbanistici", ex);
			PigErrore errore = messaggiHelper.retrievePigErrore("PING-ERRSU15");
			throw new InvioSUException(idStrumentoUrbanisticoDaInviare,
				errore.getCdErrore(), errore.getDsErrore());
		    }

		    invioSUHelper.getEntityManager().flush();

		    // Chiama il servizio NotificaTrasferimentoFile (metodo
		    // notificaAvvenutoTrasferimentoFile)
		    PigObject obj = invioSUHelper
			    .getPigObjectPerVersatoreStrumUrbInNewTx(vers.getIdVers(), cdKeyObject);
		    if (obj == null) {
			log.error("{} --- ERRORE invio strumenti urbanistici",
				InvioSUEjb.class.getSimpleName());
			PigErrore errore = messaggiHelper.retrievePigErrore("PING-ERRSU01");
			throw new InvioSUException(idStrumentoUrbanisticoDaInviare,
				errore.getCdErrore(), errore.getDsErrore());
		    } else if (obj.getTiStatoObject().equals("DA_TRASFORMARE")) {
			return;
		    } else if (!obj.getTiStatoObject().equals("IN_ATTESA_FILE")) {
			log.error("{} --- ERRORE invio strumenti urbanistici",
				InvioSUEjb.class.getSimpleName());
			PigErrore errore = messaggiHelper.retrievePigErrore("PING-ERRSU20");
			String dsErrore = StringUtils.replace(errore.getDsErrore(), "{0}",
				"" + strumentoUrbanisticoDaInviare.getIdStrumentiUrbanistici());
			throw new InvioSUException(idStrumentoUrbanisticoDaInviare,
				errore.getCdErrore(), dsErrore);
		    } else if (obj.getTiStatoObject().equals("IN_ATTESA_FILE")) {
			NotificaTrasferimentoRisposta risposta = null;
			try {
			    ListaFileDepositatoType listaFileDepositatoType = new ListaFileDepositatoType();
			    FileDepositatoType fileDepositatoType = new FileDepositatoType();
			    fileDepositatoType.setNmTipoFile("StrumentoUrbanistico");
			    fileDepositatoType.setNmNomeFile(nomeFilePacchetto);

			    // MEV 34843
			    fileDepositatoType.setIdBackend(backendVersamento.getBackendId());

			    if (backendVersamento.isObjectStorage()) {
				ObjectStorageBackend config = salvataggioBackendHelper
					.getObjectStorageConfigurationForVersamento(
						backendVersamento.getBackendName());
				String tenantOs = configurationHelper.getValoreParamApplicByApplic(
					it.eng.sacerasi.common.Constants.TENANT_OBJECT_STORAGE);

				fileDepositatoType.setNmOsBucket(config.getBucket());
				fileDepositatoType.setNmOsTenant(tenantOs);
				fileDepositatoType.setNmNomeFileOs(nmFileOs);
			    }
			    listaFileDepositatoType.getFileDepositato().add(fileDepositatoType);
			    risposta = notificaTrasferimentoEjb
				    .notificaAvvenutoTrasferimentoFileInNewTx(nmAmbienteVers,
					    nmVers, cdKeyObject, listaFileDepositatoType);
			} catch (Exception e) {
			    log.error("{} --- ERRORE invio strumenti urbanistici",
				    InvioSUEjb.class.getSimpleName());
			    PigErrore errore = messaggiHelper
				    .retrievePigErroreNewTx("PING-ERRSU18");
			    throw new InvioSUException(idStrumentoUrbanisticoDaInviare,
				    errore.getCdErrore(), errore.getDsErrore());
			}
			if (risposta.getCdEsito().equals(Constants.EsitoServizio.KO.name())) {
			    PigErrore errore = messaggiHelper
				    .retrievePigErrore(risposta.getCdErr());
			    throw new InvioSUException(idStrumentoUrbanisticoDaInviare,
				    errore.getCdErrore(), errore.getDsErrore());
			}
		    }

		    // Il sistema effettua il caricamento del file ZipStrumentiUrbanistici nel
		    // Bucket
		    // BUCKET_STRUMENTI_URBANISTICI_TRASFORMATI
		    try {
			BackendStorage backend = salvataggioBackendHelper
				.getBackendForStrumentiUrbanistici();

			if (backend.isObjectStorage()) {
			    ObjectStorageBackend config = salvataggioBackendHelper
				    .getObjectStorageConfigurationForStrumentiUrbanisticiTrasformati(
					    backend.getBackendName());

			    if (fileTemporaneoGenerale.exists()) { // il backend è su object storage
								   // e
								   // fileTemporaneoGenerale esiste
								   // ancora.
				salvataggioBackendHelper.putS3Object(config,
					strumentoUrbanisticoDaInviare.getCdKeyOs() + ".zip",
					fileTemporaneoGenerale, Optional.empty());
			    } else { // il backend è su disco e fileTemporaneoGenerale è stato
				     // spostato nella sua
				     // posizione definitiva.
				salvataggioBackendHelper.putS3Object(config,
					strumentoUrbanisticoDaInviare.getCdKeyOs() + ".zip",
					Paths.get(dirCompletaFtp + cdKeyObject + "/" + cdKeyObject
						+ it.eng.xformer.common.Constants.STANDARD_PACKAGE_EXTENSION)
						.toFile(),
					Optional.empty());
			    }
			}

		    } catch (Exception e) {
			log.error("{} --- ERRORE invio strumenti urbanistici: ",
				InvioSUEjb.class.getSimpleName(), e);
			PigErrore errore = messaggiHelper.retrievePigErroreNewTx("PING-ERRSU19");
			throw new InvioSUException(idStrumentoUrbanisticoDaInviare,
				errore.getCdErrore(), errore.getDsErrore());
		    }
		    // Setta lo stato di PigStrumentiUrbanistici
		    // MEV 31096
		    strumentiUrbanisticiHelper.creaStatoStorico(strumentoUrbanisticoDaInviare,
			    strumentoUrbanisticoDaInviare.getTiStato().name(),
			    strumentoUrbanisticoDaInviare.getDtStato(), "");

		    strumentoUrbanisticoDaInviare.setTiStato(TiStato.IN_ELABORAZIONE);
		    strumentoUrbanisticoDaInviare.setDtStato(new Date());
		    genericHelper.getEntityManager().flush();
		} finally {
		    if (fileXmlVersamento != null) {
			FileUtils.deleteQuietly(fileXmlVersamento);
		    }
		    if (fileTemporaneoGenerale != null) {
			FileUtils.deleteQuietly(fileTemporaneoGenerale);
		    }
		}
	    }
	} else {
	    log.error("{} --- ERRORE invio strumenti urbanistici",
		    InvioSUEjb.class.getSimpleName());
	    PigErrore errore = messaggiHelper.retrievePigErrore("PING-SENDOBJ-OBJ-001");
	    throw new InvioSUException(idStrumentoUrbanisticoDaInviare, errore.getCdErrore(),
		    errore.getDsErrore());
	}
    }

    private void addToZipFile(File file, ZipOutputStream zos, String nomeFileLowerCase)
	    throws IOException {
	try (FileInputStream fis = new FileInputStream(file)) {
	    ZipEntry zipEntry = new ZipEntry(nomeFileLowerCase);
	    zos.putNextEntry(zipEntry);
	    byte[] bytes = new byte[1024];
	    int length;
	    while ((length = fis.read(bytes)) >= 0) {
		zos.write(bytes, 0, length);
	    }
	    zos.closeEntry();
	}
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    private String creaXml(PigStrumentiUrbanistici strumentoUrbanisticoDaInviare)
	    throws JAXBException {

	StrumentiUrbanistici strumentiUrbanistici = new StrumentiUrbanistici();
	// Popolo l'ente
	strumentiUrbanistici.setEnte(new Ente());
	String nmEnteSiam = genericHelper
		.findById(SIOrgEnteSiam.class,
			strumentoUrbanisticoDaInviare.getPigVer().getIdEnteConvenz())
		.getNmEnteSiam();
	strumentiUrbanistici.getEnte().setEnteConvenzionato(nmEnteSiam);
	strumentiUrbanistici.getEnte()
		.setDenominazione(strumentoUrbanisticoDaInviare.getPigVer().getNmVers());
	StrumentiUrbanisticiHelper.DatiAnagraficiDto dto = strumentiUrbanisticiEjb
		.getDatiVersatoreByIdVers(
			BigDecimal.valueOf(strumentoUrbanisticoDaInviare.getPigVer().getIdVers()));
	if (dto != null) {
	    strumentiUrbanistici.getEnte().setTipologia(dto.getTipologia()); // tipo ente
	    if (dto.getTipologia() != null) {
		strumentiUrbanistici.getEnte().setUnioneComuni(dto.getUnione());
	    }
	    String provincia = dto.getProvincia();
	    if (provincia != null && provincia.equals("FORLI-CESENA")) {
		provincia = "FORLI_CESENA";
	    } else if (provincia != null && provincia.equals("REGGIO NELL'EMILIA")) {
		provincia = "REGGIO_EMILIA";
	    }
	    strumentiUrbanistici.getEnte().setProvincia(provincia);
	} else {
	    strumentiUrbanistici.getEnte().setTipologia("COMUNE");
	}
	// Dati generici
	// MEV 26936
	strumentiUrbanistici.setNumero(strumentiUrbanisticiHelper
		.estraiAttoDaIdentificativo(strumentoUrbanisticoDaInviare.getCdKey()) + "_"
		+ strumentoUrbanisticoDaInviare.getNumero());

	strumentiUrbanistici.setAnno(strumentoUrbanisticoDaInviare.getAnno().intValue());
	strumentiUrbanistici.setTipoRegistro("STRUMENTI URBANISTICI");
	Date date = strumentoUrbanisticoDaInviare.getData();
	DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	String strDate = dateFormat.format(date);
	strumentiUrbanistici.setData(strDate);
	strumentiUrbanistici.setOggetto(strumentoUrbanisticoDaInviare.getOggetto());
	String tiFaseStrumento = strumentoUrbanisticoDaInviare.getPigStrumUrbPianoStato()
		.getTiFaseStrumento();
	tiFaseStrumento = tiFaseStrumento.replaceAll(" ", "_");
	strumentiUrbanistici.setFaseStrumento(tiFaseStrumento);
	String tipoUnitaDocumentaria = strumentoUrbanisticoDaInviare.getPigStrumUrbPianoStato()
		.getNmTipoStrumentoUrbanistico();
	strumentiUrbanistici.setTipoUnitaDocumentaria(tipoUnitaDocumentaria);
	// Documenti
	// PRINCIPALE
	List<Object[]> documenti = invioSUHelper
		.getDocumenti(strumentoUrbanisticoDaInviare.getIdStrumentiUrbanistici());
	TipoDocumentoPrincipale tipoDocumentoPrincipale = new TipoDocumentoPrincipale();
	Object[] documentoPrincipale = documenti.get(0);
	tipoDocumentoPrincipale.setNomeTipoDocumento(
		TipoDocumentoPrincipaleEnumeration.fromValue((String) documentoPrincipale[0]));
	tipoDocumentoPrincipale.setNomeFileOriginale((String) documentoPrincipale[1]);
	// Calcolo il nome file versato nel seguente modo: nome tipo documento in lower case con gli
	// underscore +
	// .zip
	String nomeFileVersato = ((String) documentoPrincipale[0]).replaceAll(" ", "_")
		.toLowerCase();
	tipoDocumentoPrincipale.setNomeFileVersato(nomeFileVersato + ".zip"); //
	strumentiUrbanistici.setTipoDocumentoPrincipale(tipoDocumentoPrincipale);
	// ALTRI DOCUMENTI
	TipiDocumento tipiDocumento = new TipiDocumento();
	for (int i = 1; i < documenti.size(); i++) {
	    Object[] documento = documenti.get(i);
	    TipoDocumento tipoDocumento = new TipoDocumento();
	    tipoDocumento.setNomeTipoDocumento(
		    TipoDocumentoEnumeration.fromValue((String) documento[0]));
	    tipoDocumento.setNomeFileOriginale((String) documento[1]);
	    nomeFileVersato = ((String) documento[0]).replaceAll(" ", "_").toLowerCase();
	    tipoDocumento.setNomeFileVersato(nomeFileVersato + ".zip");
	    tipiDocumento.getTipoDocumento().add(tipoDocumento);
	}
	if (tipiDocumento.getTipoDocumento().size() > 0) {
	    strumentiUrbanistici.setTipiDocumento(tipiDocumento);
	}
	strumentiUrbanistici.setTipiDocumento(tipiDocumento);

	// Collegamenti
	List<PigStrumUrbCollegamenti> collegamentiList = invioSUHelper
		.getCollegamenti(strumentoUrbanisticoDaInviare.getIdStrumentiUrbanistici());
	if (!collegamentiList.isEmpty()) {
	    Collegamenti collegamenti = new Collegamenti();
	    for (PigStrumUrbCollegamenti strumUrbCollegamenti : collegamentiList) {
		Collegamento collegamento = new Collegamento();
		collegamento.setAnno(strumUrbCollegamenti.getAnno().intValue());

		tiFaseStrumento = strumUrbCollegamenti.getPigStrumUrbPianoStato()
			.getTiFaseStrumento();
		tiFaseStrumento = tiFaseStrumento.replaceAll(" ", "_");

		// MEV 26936 - rimuovo l'anno e '_' dall'identificativo salvato nel campo numero.
		String tipoAttoNumeroStr = strumentiUrbanisticiHelper
			.estraiNumeroCollegamento(strumUrbCollegamenti.getNumero());

		collegamento.setFaseStrumento(tiFaseStrumento);
		collegamento.setNumero(tipoAttoNumeroStr);
		collegamento.setTipoRegistro("STRUMENTI URBANISTICI");
		collegamenti.getCollegamento().add(collegamento);
	    }
	    strumentiUrbanistici.setCollegamenti(collegamenti);
	}

	log.info("{} --- Creazione XML invio strumento urbanistico",
		InvioSUEjb.class.getSimpleName());
	return marshallXmlInvioSU(strumentiUrbanistici);
    }

    private String marshallXmlInvioSU(StrumentiUrbanistici strumentiUrbanistici)
	    throws JAXBException {
	StringWriter tmpWriter = new StringWriter();
	// Eseguo il marshalling degli oggetti creati
	Marshaller udMarshaller = xmlContextCache.getInvioSUCtx_InvioSU().createMarshaller();
	udMarshaller.marshal(strumentiUrbanistici, tmpWriter);
	tmpWriter.flush();
	return tmpWriter.toString();
    }

    private void registraErroreStrumentoUrbanistico(long idStrumentoUrbanisticoDaInviare,
	    String cdErr, String dsErr, TiStato tiStato) {
	PigStrumentiUrbanistici strumentiUrbanistici = genericHelper
		.findById(PigStrumentiUrbanistici.class, idStrumentoUrbanisticoDaInviare);

	// MEV 31096
	strumentiUrbanisticiHelper.creaStatoStorico(strumentiUrbanistici,
		strumentiUrbanistici.getTiStato().name(), strumentiUrbanistici.getDtStato(), "");

	strumentiUrbanistici.setCdErr(cdErr);
	strumentiUrbanistici.setDsErr(dsErr);
	strumentiUrbanistici.setTiStato(tiStato);
	strumentiUrbanistici.setDtStato(new Date());
    }
}
