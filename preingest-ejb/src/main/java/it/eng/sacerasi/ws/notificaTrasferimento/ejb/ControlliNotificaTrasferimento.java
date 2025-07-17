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

package it.eng.sacerasi.ws.notificaTrasferimento.ejb;

import it.eng.sacerasi.common.Constants;
import it.eng.sacerasi.entity.*;
import it.eng.sacerasi.ws.dto.RispostaControlli;
import it.eng.sacerasi.ws.notificaTrasferimento.dto.FileDepositatoType;
import it.eng.sacerasi.ws.notificaTrasferimento.dto.ListaFileDepositatoType;
import it.eng.sacerasi.messages.MessaggiWSBundle;
import java.io.File;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import it.eng.parer.objectstorage.helper.SalvataggioBackendHelper;
import it.eng.parer.objectstorage.dto.ObjectStorageBackend;
import it.eng.parer.objectstorage.exceptions.ObjectStorageException;

/**
 *
 * @author Bonora_L
 */
@SuppressWarnings("unchecked")
@Stateless(mappedName = "NotificaTrasferimento")
@LocalBean
// @TransactionManagement(TransactionManagementType.BEAN)
public class ControlliNotificaTrasferimento {

    private static final Logger log = LoggerFactory.getLogger(ControlliNotificaTrasferimento.class);
    @PersistenceContext(unitName = "SacerAsiJPA")
    private EntityManager entityManager;

    @EJB
    private SalvataggioBackendHelper salvataggioBackendHelper;

    /**
     * Verifica lo stato dell'oggetto
     *
     * @param nmAmbiente  nome ambiente
     * @param nmVersatore nome versatore
     * @param cdKeyObject numero oggetto
     *
     * @return RispostaControlli.isrBoolean() == true in caso la verifica dia esito positivo
     */
    public RispostaControlli verificaStatoOggetto(String nmAmbiente, String nmVersatore,
	    String cdKeyObject) {
	RispostaControlli rispostaControlli = new RispostaControlli();
	rispostaControlli.setrBoolean(true);
	try {
	    String queryStr = "SELECT obj FROM PigObject obj INNER JOIN obj.pigVer vers "
		    + "WHERE vers.pigAmbienteVer.nmAmbienteVers = :nmAmbiente AND vers.nmVers = :nmVers AND obj.cdKeyObject = :cdKey";

	    javax.persistence.Query query = entityManager.createQuery(queryStr);
	    query.setParameter("nmAmbiente", nmAmbiente);
	    query.setParameter("nmVers", nmVersatore);
	    query.setParameter("cdKey", cdKeyObject);
	    List<PigObject> objs = query.getResultList();
	    if (!objs.isEmpty()) {
		rispostaControlli.setrLong(objs.get(0).getIdLastSessioneIngest().longValue());
		if (!objs.get(0).getTiStatoObject()
			.equalsIgnoreCase(Constants.StatoOggetto.IN_ATTESA_FILE.name())) {
		    rispostaControlli.setrBoolean(false);
		    rispostaControlli.setCodErr(MessaggiWSBundle.PING_NOT_005);
		    rispostaControlli
			    .setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.PING_NOT_005));
		}
	    }
	} catch (Exception e) {
	    rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
	    rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
		    String.join("\n", ExceptionUtils.getRootCauseStackTrace(e))));
	    log.error("Eccezione nella lettura  della tabella degli oggetti ", e);
	}
	return rispostaControlli;
    }

    /**
     * Verifica la presenza della directory dato il suo path
     *
     * @param ftpPath path ftp
     *
     * @return RispostaControlli.isrBoolean() == true in caso la verifica dia esito positivo
     */
    public RispostaControlli verificaPresenzaDirFtp(String ftpPath) {
	RispostaControlli rispostaControlli = new RispostaControlli();
	rispostaControlli.setrBoolean(false);

	try {
	    File file = new File(ftpPath);
	    if (file.exists() && file.isDirectory()) {
		rispostaControlli.setrBoolean(true);
		rispostaControlli.setrString(ftpPath);
	    } else {
		rispostaControlli.setCodErr(MessaggiWSBundle.PING_NOT_006);
		rispostaControlli
			.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.PING_NOT_006));
		// registro sul log applicativo questo evento come un errore; pur non essendo
		// un'eccezione
		log.error("{}:{} ; il path non trovato è:{}", rispostaControlli.getCodErr(),
			rispostaControlli.getDsErr(), ftpPath);
	    }
	} catch (Exception e) {
	    rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
	    rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
		    String.join("\n", ExceptionUtils.getRootCauseStackTrace(e))));
	    log.error("Eccezione nella lettura della tabella dei parametri ", e);
	}
	return rispostaControlli;
    }

    /**
     * Verifica la coerenza dei dati rispetto al tipo di versamento eseguito
     *
     * @param nmAmbiente          nome ambiente
     * @param nmVersatore         nome versatore
     * @param cdKeyObject         numero oggetto
     * @param listaFileDepositati lista file depositati di tipo {@link FileDepositatoType}
     *
     * @return RispostaControlli.isrBoolean() == true in caso la verifica dia esito positivo
     */
    public RispostaControlli verificaCoerenzaTipoVersamentoFile(String nmAmbiente,
	    String nmVersatore, String cdKeyObject, List<FileDepositatoType> listaFileDepositati) {
	RispostaControlli rispostaControlli = new RispostaControlli();
	rispostaControlli.setrBoolean(false);
	try {
	    String queryStr = "SELECT obj FROM PigObject obj INNER JOIN obj.pigVer vers "
		    + "WHERE vers.pigAmbienteVer.nmAmbienteVers = :nmAmbiente AND vers.nmVers = :nmVers AND obj.cdKeyObject = :cdKey";
	    javax.persistence.Query query = entityManager.createQuery(queryStr);
	    query.setParameter("nmAmbiente", nmAmbiente);
	    query.setParameter("nmVers", nmVersatore);
	    query.setParameter("cdKey", cdKeyObject);
	    List<PigObject> lista = query.getResultList();
	    if (!lista.isEmpty()) {
		PigObject obj = lista.get(0);
		String tipo = obj.getPigTipoObject().getTiVersFile();
		if (!tipo.equals(Constants.TipoVersamento.NO_ZIP.name())
			&& !tipo.equals(Constants.TipoVersamento.DA_TRASFORMARE.name())
			&& listaFileDepositati.size() > 1) {
		    rispostaControlli.setCodErr(MessaggiWSBundle.PING_NOT_007);
		    rispostaControlli
			    .setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.PING_NOT_007));
		} else {
		    rispostaControlli.setrBoolean(true);
		    rispostaControlli.setrString(tipo);
		}
	    }
	} catch (Exception e) {
	    rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
	    rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
		    String.join("\n", ExceptionUtils.getRootCauseStackTrace(e))));
	    log.error("Eccezione nella lettura  della tabella degli oggetti ", e);
	}
	return rispostaControlli;
    }

    /**
     * Verifica la coerenza del numero di file contenuti nella directory ftp rispetto ai file
     * dichiarati
     *
     * @param ftpPath path ftp
     * @param size    dimensione file
     *
     * @return RispostaControlli.isrBoolean() == true in caso la verifica dia esito positivo
     */
    public RispostaControlli verificaCoerenzaNumeroFile(String ftpPath, int size) {
	RispostaControlli rispostaControlli = new RispostaControlli();
	rispostaControlli.setrBoolean(false);
	File file = new File(ftpPath);
	if (file.exists() && file.isDirectory()) {
	    String[] list = file.list();
	    if (list.length == size) {
		rispostaControlli.setrBoolean(true);
	    } else {
		rispostaControlli.setCodErr(MessaggiWSBundle.PING_NOT_008);
		rispostaControlli
			.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.PING_NOT_008));
	    }
	}

	return rispostaControlli;
    }

    /**
     * Verifica nel caso che il tipo versamento sia NO_ZIP il numero di documenti principali
     *
     * @param idObj      id oggetto
     * @param listaFiles file di tipo {@link ListaFileDepositatoType}
     *
     * @return RispostaControlli.isrBoolean() == true in caso la verifica dia esito positivo
     */
    public RispostaControlli verificaDocPrincipaleSuNoZip(Long idObj,
	    ListaFileDepositatoType listaFiles) {
	RispostaControlli rispostaControlli = new RispostaControlli();
	rispostaControlli.setrBoolean(true);

	PigObject obj = entityManager.find(PigObject.class, idObj);

	if (obj.getPigTipoObject().getTiVersFile().equals(Constants.TipoVersamento.NO_ZIP.name())) {
	    int docPrincCounter = 0;
	    PigTipoFileObject tipoFilePrinc = null;
	    for (PigTipoFileObject tiFiObj : obj.getPigTipoObject().getPigTipoFileObjects()) {
		if (tiFiObj.getTiDocSacer().equals(Constants.DocTypeEnum.PRINCIPALE.name())) {
		    tipoFilePrinc = tiFiObj;
		    break;
		}
	    }
	    for (FileDepositatoType file : listaFiles.getFileDepositato()) {
		if (file.getNmTipoFile().equals(tipoFilePrinc.getNmTipoFileObject())) {
		    docPrincCounter++;
		}
	    }
	    if (docPrincCounter != 1) {
		rispostaControlli.setrBoolean(false);
		rispostaControlli.setCodErr(MessaggiWSBundle.PING_NOT_010);
		rispostaControlli
			.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.PING_NOT_010));
	    }
	}

	return rispostaControlli;
    }

    /**
     * Verifica il valore del tipo di file
     *
     * @param idObj               id oggetto
     * @param listaFileDepositati lista file di tipo {@link FileDepositatoType}
     *
     * @return RispostaControlli.isrBoolean() == true in caso la verifica dia esito positivo
     */
    public RispostaControlli verificaTipoFile(Long idObj,
	    List<FileDepositatoType> listaFileDepositati) {
	RispostaControlli rispostaControlli = new RispostaControlli();
	rispostaControlli.setrBoolean(false);

	PigObject obj = entityManager.find(PigObject.class, idObj);

	for (FileDepositatoType fileDep : listaFileDepositati) {
	    String tipoFile = fileDep.getNmTipoFile();
	    int counter = 0;
	    // Controllo per prima cosa che il tipo file non sia valorizzato,
	    // Nel qual caso vado direttamente all'errore
	    if (StringUtils.isNotBlank(tipoFile)) {
		// Verifico che tra i tipiFileObject esista quel tipo file
		for (PigTipoFileObject tiFileObj : obj.getPigTipoObject().getPigTipoFileObjects()) {
		    if (tiFileObj.getNmTipoFileObject().equalsIgnoreCase(tipoFile)) {
			counter++;
		    }
		}
		if (counter > 0) {
		    rispostaControlli.setrBoolean(true);
		    rispostaControlli.setrString(obj.getPigTipoObject().getNmTipoObject());
		}
	    }
	}

	if (!rispostaControlli.isrBoolean()) {
	    rispostaControlli.setCodErr(MessaggiWSBundle.PING_NOT_009);
	    rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.PING_NOT_009));
	}

	return rispostaControlli;
    }

    /**
     * Verifica la presenza di ogni nome file
     *
     * @param listaFileDepositati lista file di tipo {@link FileDepositatoType}
     *
     * @return RispostaControlli.isrBoolean() == true in caso la verifica dia esito positivo
     */
    public RispostaControlli verificaNomeFile(List<FileDepositatoType> listaFileDepositati) {
	RispostaControlli rispostaControlli = new RispostaControlli();
	rispostaControlli.setrBoolean(false);

	int countFiles = 0;
	for (FileDepositatoType fileDep : listaFileDepositati) {
	    if (StringUtils.isNotBlank(fileDep.getNmNomeFile())) {
		countFiles++;
	    }
	}

	if (countFiles == listaFileDepositati.size()) {
	    rispostaControlli.setrBoolean(true);
	} else {
	    rispostaControlli.setCodErr(MessaggiWSBundle.PING_NOT_011);
	    rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.PING_NOT_011));
	}

	return rispostaControlli;
    }

    /**
     * Verifica se l'hash dei file debbano essere presenti obbligatoriamente
     *
     * @param idObj    id oggetto
     * @param tipoFile tipo file
     *
     * @return RispostaControlli.isrBoolean() == True se è obbligatoria la presenza degli hash
     */
    public RispostaControlli verificaDisponibilitaHash(Long idObj, String tipoFile) {
	RispostaControlli rispostaControlli = new RispostaControlli();
	rispostaControlli.setrBoolean(false);

	PigObject obj = entityManager.find(PigObject.class, idObj);

	String queryStr = "SELECT tiFileObj FROM PigTipoFileObject tiFileObj INNER JOIN tiFileObj.pigTipoObject tipoObj "
		+ "WHERE tipoObj.idTipoObject = :tipoObj AND tiFileObj.nmTipoFileObject = :tipoFile";

	javax.persistence.Query query = entityManager.createQuery(queryStr);
	query.setParameter("tipoObj", obj.getPigTipoObject().getIdTipoObject());
	query.setParameter("tipoFile", tipoFile);
	List<PigTipoFileObject> tipi = query.getResultList();
	if (!tipi.isEmpty()) {
	    if (obj.getPigTipoObject().getFlContrHash().equals(Constants.DB_TRUE)
		    || (tipi.get(0).getFlCalcHashSacer() != null
			    && tipi.get(0).getFlCalcHashSacer().equals(Constants.DB_FALSE))) {
		rispostaControlli.setrBoolean(true);
	    }
	    rispostaControlli.setrLong(tipi.get(0).getIdTipoFileObject());
	}

	return rispostaControlli;
    }

    /**
     * Verifica, nel caso sia necessario, gli hash forniti
     *
     * @param idObj       id oggetto
     * @param idTiFileObj id tipo file
     * @param fileDep     file depositato {@link FileDepositatoType}
     *
     * @return RispostaControlli.isrBoolean() == true in caso la verifica dia esito positivo
     */
    public RispostaControlli verificaHash(Long idObj, Long idTiFileObj,
	    FileDepositatoType fileDep) {
	RispostaControlli rispostaControlli = new RispostaControlli();
	rispostaControlli.setrBoolean(false);

	PigObject obj = entityManager.find(PigObject.class, idObj);
	PigTipoFileObject tiFileObj = entityManager.find(PigTipoFileObject.class, idTiFileObj);

	if (tiFileObj.getTiCalcHashSacer() != null) {
	    if (tiFileObj.getTiCalcHashSacer()
		    .equals(Constants.HashCalcType.FILE_HASH_DICOM.name())) {
		String queryStr = "SELECT info FROM PigInfoDicom info INNER JOIN info.pigObject obj "
			+ "WHERE obj.idObject = :obj";

		javax.persistence.Query query = entityManager.createQuery(queryStr);
		query.setParameter("obj", idObj);
		List<PigInfoDicom> infoDicom = query.getResultList();
		if (!infoDicom.isEmpty()) {
		    PigInfoDicom info = infoDicom.get(0);
		    if (StringUtils.isNotBlank(info.getDsFileHash())
			    && StringUtils.isNotBlank(info.getCdEncodingFileHash())
			    && StringUtils.isNotBlank(info.getTiAlgoFileHash())) {
			rispostaControlli.setrBoolean(true);
			fileDep.setDsHashFile(info.getDsFileHash());
			fileDep.setCdEncoding(info.getCdEncodingFileHash());
			fileDep.setTiAlgoritmoHash(info.getTiAlgoFileHash());
		    } else {
			if (obj.getPigTipoObject().getFlContrHash().equals(Constants.DB_TRUE)) {
			    rispostaControlli.setCodErr(MessaggiWSBundle.PING_NOT_012);
			    rispostaControlli.setDsErr(
				    MessaggiWSBundle.getString(MessaggiWSBundle.PING_NOT_012));
			} else if (tiFileObj.getFlCalcHashSacer().equals(Constants.DB_FALSE)) {
			    rispostaControlli.setCodErr(MessaggiWSBundle.PING_NOT_013);
			    rispostaControlli.setDsErr(
				    MessaggiWSBundle.getString(MessaggiWSBundle.PING_NOT_013));
			}
		    }
		}
	    } else if (tiFileObj.getTiCalcHashSacer()
		    .equals(Constants.HashCalcType.NOTIFICATO.name())) {
		if (StringUtils.isNotBlank(fileDep.getDsHashFile())
			&& StringUtils.isNotBlank(fileDep.getCdEncoding())
			&& StringUtils.isNotBlank(fileDep.getTiAlgoritmoHash())) {
		    rispostaControlli.setrBoolean(true);
		} else {
		    if (obj.getPigTipoObject().getFlContrHash().equals(Constants.DB_TRUE)) {
			rispostaControlli.setCodErr(MessaggiWSBundle.PING_NOT_012);
			rispostaControlli.setDsErr(
				MessaggiWSBundle.getString(MessaggiWSBundle.PING_NOT_012));
		    } else if (tiFileObj.getFlCalcHashSacer().equals(Constants.DB_FALSE)) {
			rispostaControlli.setCodErr(MessaggiWSBundle.PING_NOT_013);
			rispostaControlli.setDsErr(
				MessaggiWSBundle.getString(MessaggiWSBundle.PING_NOT_013));
		    }
		}
	    }
	} else {
	    rispostaControlli.setCodErr(MessaggiWSBundle.PING_NOT_021);
	    rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.PING_NOT_021));
	}
	return rispostaControlli;
    }

    /**
     * Verifica la presenza del file fornito come parametro all'interno della directory ftp
     *
     * @param ftpPath  path ftp
     * @param nomeFile nome del file
     *
     * @return RispostaControlli.isrBoolean() == true in caso la verifica dia esito positivo
     */
    public RispostaControlli verificaNomeFileFtp(String ftpPath, String nomeFile) {
	RispostaControlli rispostaControlli = new RispostaControlli();
	rispostaControlli.setrBoolean(false);
	File file = new File(ftpPath + File.separator + nomeFile);
	if (file.exists() && file.isFile()) {
	    rispostaControlli.setrBoolean(true);
	} else {
	    rispostaControlli.setCodErr(MessaggiWSBundle.PING_NOT_014);
	    rispostaControlli
		    .setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.PING_NOT_014, nomeFile));
	}

	return rispostaControlli;
    }

    /**
     * MEV21995
     *
     * Verifica la presenza del file fornito come parametro all'interno dell'Object Storage
     *
     * @param config     configurazione del backend os
     * @param nomeFileOs chiave dell'oggetto su OS
     *
     * @throws ObjectStorageException in caso di errore
     *
     * @return RispostaControlli.isrBoolean() == true in caso la verifica dia esito positivo
     */
    public RispostaControlli verificaNomeFileObjectStorage(ObjectStorageBackend config,
	    String nomeFileOs) throws ObjectStorageException {
	RispostaControlli rispostaControlli = new RispostaControlli();
	rispostaControlli.setrBoolean(false);

	if (salvataggioBackendHelper.doesObjectExist(config, nomeFileOs)) {
	    rispostaControlli.setrBoolean(true);
	} else {
	    rispostaControlli.setCodErr(MessaggiWSBundle.PING_NOT_014);
	    rispostaControlli.setDsErr(
		    MessaggiWSBundle.getString(MessaggiWSBundle.PING_NOT_014, nomeFileOs));
	}

	return rispostaControlli;
    }

    /**
     * Verifica che la data creazione del file sia coerente con la sessione
     *
     * @param ftpPath    path ftp
     * @param nomeFile   nome file
     * @param idSessione id sessione
     *
     * @return RispostaControlli.isrBoolean() == true in caso la verifica dia esito positivo
     */
    public RispostaControlli verificaDataCreazioneFile(String ftpPath, String nomeFile,
	    Long idSessione) {
	RispostaControlli rispostaControlli = new RispostaControlli();
	rispostaControlli.setrBoolean(false);

	File file = new File(ftpPath + File.separator + nomeFile);
	if (file.exists() && file.isFile()) {
	    Date creationDate = new Date(file.lastModified());
	    Date now = new Date();
	    PigSessioneIngest sessione = entityManager.find(PigSessioneIngest.class, idSessione);
	    if (creationDate.after(sessione.getDtApertura()) && creationDate.before(now)) {
		rispostaControlli.setrBoolean(true);
	    } else {
		log.debug("Data in sessione: " + sessione.getDtApertura()
			+ " ; Data creazione file : " + creationDate + " ; Data attuale: " + now);
		rispostaControlli.setCodErr(MessaggiWSBundle.PING_NOT_015);
		rispostaControlli
			.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.PING_NOT_015));
	    }
	}

	return rispostaControlli;
    }
}
