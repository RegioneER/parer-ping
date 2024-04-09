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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.eng.sacerasi.ws.notificaTrasferimento.helper;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.parer.objectstorage.exceptions.ObjectStorageException;
import it.eng.parer.objectstorage.helper.SalvataggioBackendHelper;
import it.eng.sacerasi.common.Constants;
import it.eng.sacerasi.common.ejb.CommonDb;
import it.eng.sacerasi.messages.MessaggiWSBundle;
import it.eng.sacerasi.ws.dto.IRispostaWS.SeverityEnum;
import it.eng.sacerasi.ws.dto.RispostaControlli;
import it.eng.sacerasi.ws.ejb.ControlliWS;
import it.eng.sacerasi.ws.notificaTrasferimento.dto.FileDepositatoRespType;
import it.eng.sacerasi.ws.notificaTrasferimento.dto.FileDepositatoType;
import it.eng.sacerasi.ws.notificaTrasferimento.dto.NotificaTrasferimentoExt;
import it.eng.sacerasi.ws.notificaTrasferimento.dto.RispostaNotificaWS;
import it.eng.sacerasi.ws.notificaTrasferimento.ejb.ControlliNotificaTrasferimento;

@Stateless(mappedName = "NotificaTrasferimentoCheckHelper")
@LocalBean
public class NotificaTrasferimentoCheckHelper {

    private static final Logger log = LoggerFactory.getLogger(NotificaTrasferimentoCheckHelper.class);

    @EJB
    private ControlliWS controlliWS;
    @EJB
    private ControlliNotificaTrasferimento controlliNotif;
    @EJB
    private CommonDb commonDb;
    @EJB
    private SalvataggioBackendHelper salvataggioBackendHelper;

    public void check(NotificaTrasferimentoExt notificaTrasferimentoExt, RispostaNotificaWS rispostaWs)
            throws ObjectStorageException {
        RispostaControlli rispostaControlli = new RispostaControlli();
        //
        String ambiente = null;
        String nmVersatore = null;
        String cdKeyObject = null;
        String inputFtp = null;
        String ftpPath = null;
        String rootFtp = null;

        if (rispostaWs.getSeverity() != SeverityEnum.ERROR) {
            try {
                rootFtp = commonDb.getRootFtpParam();
            } catch (Exception ex) {
                log.error(ex.getMessage());
                rispostaControlli.reset();
                rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
                rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
                        String.join("\n", ExceptionUtils.getRootCauseStackTrace(ex))));
                setRispostaWsError(rispostaWs, rispostaControlli);
            }
        }

        if (rispostaWs.getSeverity() != SeverityEnum.ERROR) {
            rispostaControlli.reset();
            rispostaControlli = controlliWS
                    .verificaNomeAmbiente(notificaTrasferimentoExt.getNotificaTrasf().getNmAmbiente());
            if (!rispostaControlli.isrBoolean()) {
                if (rispostaControlli.getCodErr() == null) {
                    rispostaControlli.setCodErr(MessaggiWSBundle.PING_NOT_001);
                    rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.PING_NOT_001));
                    setRispostaWsError(rispostaWs, rispostaControlli);
                } else {
                    // Errore 666
                    setRispostaWsError(rispostaWs, rispostaControlli);
                }
            } else {
                ambiente = notificaTrasferimentoExt.getNotificaTrasf().getNmAmbiente();
                rispostaWs.getNotificaResponse().setNmAmbiente(ambiente);
            }
        }

        Long idVersatore = null;
        if (rispostaWs.getSeverity() != SeverityEnum.ERROR) {
            rispostaControlli.reset();
            rispostaControlli = controlliWS.verificaNomeVersatore(ambiente,
                    notificaTrasferimentoExt.getNotificaTrasf().getNmVersatore());
            if (!rispostaControlli.isrBoolean()) {
                if (rispostaControlli.getCodErr() == null) {
                    rispostaControlli.setCodErr(MessaggiWSBundle.PING_NOT_002);
                    rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.PING_NOT_002));
                    setRispostaWsError(rispostaWs, rispostaControlli);
                } else {
                    // Errore 666
                    setRispostaWsError(rispostaWs, rispostaControlli);
                }
            } else {
                idVersatore = rispostaControlli.getrLong();
                nmVersatore = notificaTrasferimentoExt.getNotificaTrasf().getNmVersatore();
                inputFtp = rispostaControlli.getrString();
                rispostaWs.getNotificaResponse().setNmVersatore(nmVersatore);
            }
        }
        notificaTrasferimentoExt.setIdVersatore(idVersatore);

        if (rispostaWs.getSeverity() != SeverityEnum.ERROR) {
            ftpPath = rootFtp.concat(inputFtp).concat(File.separator)
                    .concat(notificaTrasferimentoExt.getNotificaTrasf().getCdKeyObject());
        }
        notificaTrasferimentoExt.setFtpPath(ftpPath);
        log.debug("Path FTP : {}", ftpPath);

        // Verifica che chiave object sia lungo meno di 96 caratteri
        if (rispostaWs.getSeverity() != SeverityEnum.ERROR) {
            rispostaControlli.reset();
            rispostaControlli = controlliWS.verificaCdKeyObjectLength(
                    notificaTrasferimentoExt.getNotificaTrasf().getCdKeyObject(), MessaggiWSBundle.PING_NOT_019);
            if (!rispostaControlli.isrBoolean()) {
                setRispostaWsError(rispostaWs, rispostaControlli);
            }
        }

        Long idObject = null;
        if (rispostaWs.getSeverity() != SeverityEnum.ERROR) {
            rispostaControlli.reset();
            rispostaControlli = controlliWS.verificaCdKeyObject(ambiente, nmVersatore,
                    notificaTrasferimentoExt.getNotificaTrasf().getCdKeyObject());
            if (!rispostaControlli.isrBoolean()) {
                if (rispostaControlli.getCodErr() == null) {
                    rispostaControlli.setCodErr(MessaggiWSBundle.PING_NOT_004);
                    rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.PING_NOT_004));
                    setRispostaWsError(rispostaWs, rispostaControlli);
                } else {
                    // Errore 666
                    setRispostaWsError(rispostaWs, rispostaControlli);
                }
            } else {
                cdKeyObject = notificaTrasferimentoExt.getNotificaTrasf().getCdKeyObject();
                rispostaWs.getNotificaResponse().setCdKeyObject(cdKeyObject);
                idObject = rispostaControlli.getrLong();
            }
        }
        notificaTrasferimentoExt.setIdObject(idObject);

        Long lastSessionId = null;
        if (rispostaWs.getSeverity() != SeverityEnum.ERROR) {
            rispostaControlli.reset();
            rispostaControlli = controlliNotif.verificaStatoOggetto(ambiente, nmVersatore, cdKeyObject);
            notificaTrasferimentoExt.setFlCancellaFile(false);
            if (!rispostaControlli.isrBoolean()) {
                setRispostaWsError(rispostaWs, rispostaControlli);
            } else {
                notificaTrasferimentoExt.setFlAggiornaOggetto(true);
            }
            lastSessionId = rispostaControlli.getrLong();
        }
        notificaTrasferimentoExt.setIdLastSession(lastSessionId);

        // MEV 21995: il file potrebbe essere su object storage e non su ftp
        boolean isObjectStorageOnly = true;
        for (FileDepositatoType fileDep : notificaTrasferimentoExt.getNotificaTrasf().getFileDepositati()
                .getFileDepositato()) {
            // se almeno un file non è su object storage eseguirò il controllo sottostante
            if (fileDep.getNmOsBucket() == null || fileDep.getNmOsBucket().isEmpty()
                    || fileDep.getNmNomeFileOs() == null || fileDep.getNmNomeFileOs().isEmpty()) {
                isObjectStorageOnly = false;
            }
        }

        if (rispostaWs.getSeverity() != SeverityEnum.ERROR && !isObjectStorageOnly) {
            // MEV21995 TODO che controllo posso usare per os?
            rispostaControlli.reset();
            rispostaControlli = controlliNotif.verificaPresenzaDirFtp(ftpPath);
            notificaTrasferimentoExt.setFlCancellaFile(false);
            if (!rispostaControlli.isrBoolean()) {
                setRispostaWsError(rispostaWs, rispostaControlli);
            }
        }
        // NOT_007
        if (rispostaWs.getSeverity() != SeverityEnum.ERROR) {
            rispostaControlli.reset();
            rispostaControlli = controlliNotif.verificaCoerenzaTipoVersamentoFile(ambiente, nmVersatore, cdKeyObject,
                    notificaTrasferimentoExt.getNotificaTrasf().getFileDepositati().getFileDepositato());
            if (!rispostaControlli.isrBoolean()) {
                setRispostaWsError(rispostaWs, rispostaControlli);
            } else {
                notificaTrasferimentoExt.setTipoObject(rispostaControlli.getrString());
            }
        }
        // NOT_008
        if (rispostaWs.getSeverity() != SeverityEnum.ERROR && !isObjectStorageOnly) {
            // MEV21995 TODO modificare verificaCoerenzaNumeroFile per eseguire controlli su OS se necessario
            rispostaControlli.reset();
            rispostaControlli = controlliNotif.verificaCoerenzaNumeroFile(ftpPath,
                    notificaTrasferimentoExt.getNotificaTrasf().getFileDepositati().getFileDepositato().size());
            if (!rispostaControlli.isrBoolean()) {
                setRispostaWsError(rispostaWs, rispostaControlli);
            }
        }
        // NOT_010
        if (rispostaWs.getSeverity() != SeverityEnum.ERROR) {
            rispostaControlli.reset();
            rispostaControlli = controlliNotif.verificaDocPrincipaleSuNoZip(idObject,
                    notificaTrasferimentoExt.getNotificaTrasf().getFileDepositati());
            if (!rispostaControlli.isrBoolean()) {
                setRispostaWsError(rispostaWs, rispostaControlli);
            }
        }
        // NOT_009
        String nmTipoObject = null;
        if (rispostaWs.getSeverity() != SeverityEnum.ERROR) {
            rispostaControlli.reset();
            rispostaControlli = controlliNotif.verificaTipoFile(idObject,
                    notificaTrasferimentoExt.getNotificaTrasf().getFileDepositati().getFileDepositato());
            if (!rispostaControlli.isrBoolean()) {
                setRispostaWsError(rispostaWs, rispostaControlli);
            } else {
                nmTipoObject = rispostaControlli.getrString();
            }
        }
        // NOT_011
        if (rispostaWs.getSeverity() != SeverityEnum.ERROR) {
            rispostaControlli.reset();
            rispostaControlli = controlliNotif.verificaNomeFile(
                    notificaTrasferimentoExt.getNotificaTrasf().getFileDepositati().getFileDepositato());
            if (!rispostaControlli.isrBoolean()) {
                setRispostaWsError(rispostaWs, rispostaControlli);
            }
        }
        // NOT_012 e NOT_013
        if (rispostaWs.getSeverity() != SeverityEnum.ERROR) {
            rispostaControlli.reset();
            Map<String, Long> tipiFileObj = new HashMap<>();
            Set<String> controlloUnivocitaTiFileObj = new HashSet<>();
            for (FileDepositatoType fileDep : notificaTrasferimentoExt.getNotificaTrasf().getFileDepositati()
                    .getFileDepositato()) {
                rispostaControlli = controlliNotif.verificaDisponibilitaHash(idObject, fileDep.getNmTipoFile());
                Long idTiFileObj = rispostaControlli.getrLong();
                tipiFileObj.put(fileDep.getNmNomeFile(), idTiFileObj);
                if (rispostaControlli.isrBoolean()) {
                    rispostaControlli.reset();
                    rispostaControlli = controlliNotif.verificaHash(idObject, idTiFileObj, fileDep);
                    if (!rispostaControlli.isrBoolean()) {
                        setRispostaWsError(rispostaWs, rispostaControlli);
                        break;
                    }
                }

                if (rispostaWs.getSeverity() != SeverityEnum.ERROR) {
                    rispostaControlli.reset();
                    if (!controlloUnivocitaTiFileObj.add(fileDep.getNmTipoFile())) {
                        rispostaControlli.setrBoolean(false);
                        rispostaControlli.setCodErr(MessaggiWSBundle.PING_NOT_018);
                        rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.PING_NOT_018));
                        setRispostaWsError(rispostaWs, rispostaControlli);
                        break;
                    }
                }
            }
            notificaTrasferimentoExt.setTipoFileObjects(tipiFileObj);
        }
        // NOT_014
        if (rispostaWs.getSeverity() != SeverityEnum.ERROR) {
            rispostaControlli.reset();
            for (FileDepositatoType fileDep : notificaTrasferimentoExt.getNotificaTrasf().getFileDepositati()
                    .getFileDepositato()) {
                // MEV21995 se il file non è su Objec Storage controllo su ftp
                if (fileDep.getNmOsBucket() == null || fileDep.getNmOsBucket().isEmpty()
                        || fileDep.getNmNomeFileOs() == null || fileDep.getNmNomeFileOs().isEmpty()) {
                    rispostaControlli = controlliNotif.verificaNomeFileFtp(ftpPath, fileDep.getNmNomeFile());
                } else {
                    rispostaControlli = controlliNotif.verificaNomeFileObjectStorage(fileDep.getNmOsBucket(),
                            fileDep.getNmNomeFileOs());
                }

                if (!rispostaControlli.isrBoolean()) {
                    setRispostaWsError(rispostaWs, rispostaControlli);
                    break;
                }

                if (StringUtils.isNotBlank(fileDep.getTiAlgoritmoHash())) {
                    rispostaControlli.reset();
                    rispostaControlli = controlliWS.verificaAlgoritmo(fileDep.getTiAlgoritmoHash(),
                            MessaggiWSBundle.PING_NOT_016);
                    if (!rispostaControlli.isrBoolean()) {
                        setRispostaWsError(rispostaWs, rispostaControlli);
                        break;
                    }
                }

                if (StringUtils.isNotBlank(fileDep.getCdEncoding())) {
                    rispostaControlli.reset();
                    rispostaControlli = controlliWS.verificaEncoding(fileDep.getCdEncoding(),
                            MessaggiWSBundle.PING_NOT_017);
                    if (!rispostaControlli.isrBoolean()) {
                        setRispostaWsError(rispostaWs, rispostaControlli);
                        break;
                    }
                }

                // Completo la risposta del ws
                FileDepositatoRespType fileDepResp = new FileDepositatoRespType();
                fileDepResp.setNmTipoObject(nmTipoObject);
                fileDepResp.setNmNomeFile(fileDep.getNmNomeFile());
                fileDepResp.setCdEncoding(fileDep.getCdEncoding());
                fileDepResp.setTiAlgoritmoHash(fileDep.getTiAlgoritmoHash());
                fileDepResp.setDsHashFile(fileDep.getDsHashFile());
                // MEV21995 aggiungo le informazioni eventuali sul salvataggio su object storage
                fileDepResp.setNmOsBucket(fileDep.getNmOsBucket());
                fileDepResp.setNmNomeFileOs(fileDep.getNmNomeFileOs());

                rispostaWs.getNotificaResponse().getListaFileDepositati().getFileDepositato().add(fileDepResp);
            }
        }

    }

    private void setRispostaWsError(RispostaNotificaWS rispostaWs, RispostaControlli rispostaControlli) {
        rispostaWs.setSeverity(SeverityEnum.ERROR);
        log.debug("Rilevato errore {} - {}", rispostaControlli.getCodErr(), rispostaControlli.getDsErr());
        rispostaWs.setErrorCode(rispostaControlli.getCodErr());
        rispostaWs.setErrorMessage(rispostaControlli.getDsErr());
        rispostaWs.getNotificaResponse().setCdEsito(Constants.EsitoServizio.KO.name());
        rispostaWs.getNotificaResponse().setCdErr(rispostaControlli.getCodErr());
        rispostaWs.getNotificaResponse().setDsErr(rispostaControlli.getDsErr());
    }

}
