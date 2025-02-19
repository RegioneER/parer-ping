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
package it.eng.sacerasi.ws.invioOggettoAsincrono.ejb;

import it.eng.paginator.util.HibernateUtils;
import it.eng.sacerasi.common.Constants;
import it.eng.sacerasi.common.Constants.StatoOggetto;
import it.eng.sacerasi.common.Constants.TipoCalcolo;
import it.eng.sacerasi.common.Constants.TipoVersamento;
import it.eng.sacerasi.entity.PigObject;
import it.eng.sacerasi.entity.PigSessioneIngest;
import it.eng.sacerasi.entity.PigTipoFileObject;
import it.eng.sacerasi.entity.PigTipoObject;
import it.eng.sacerasi.entity.PigVersTipoObjectDaTrasf;
import it.eng.sacerasi.entity.PigXsdDatiSpec;
import it.eng.sacerasi.messages.MessaggiWSBundle;
import it.eng.sacerasi.ws.dto.RispostaControlli;
import it.eng.sacerasi.ws.xml.datiSpecDicom.DatiSpecificiType.ModalityInStudyList;
import it.eng.sacerasi.ws.xml.datiSpecDicom.DatiSpecificiType.SOPClassList;
import java.io.File;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import javax.annotation.Resource;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.resource.ResourceException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xadisk.connector.outbound.XADiskConnection;
import org.xadisk.connector.outbound.XADiskConnectionFactory;
import org.xadisk.filesystem.exceptions.InsufficientPermissionOnFileException;
import org.xadisk.filesystem.exceptions.LockingFailedException;
import org.xadisk.filesystem.exceptions.NoTransactionAssociatedException;

/**
 *
 * @author Gilioli_P
 */
@SuppressWarnings("unchecked")
@Stateless(mappedName = "ControlliInvioOggettoAsincrono")
@LocalBean
public class ControlliInvioOggettoAsincrono {

    private static final Logger log = LoggerFactory.getLogger(ControlliInvioOggettoAsincrono.class);
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
    @PersistenceContext(unitName = "SacerAsiJPA")
    private EntityManager entityManager;
    @Resource(mappedName = "jca/xadiskLocal")
    private XADiskConnectionFactory xadCf;

    /**
     * Verifica che la versione dell'XML sia pari ad una delle versioni supportate da SACER Pre Ingest
     *
     * @param cdVersioneXml
     *            codice versione xml
     * @param compat
     *            lista elementi di tipo String
     *
     * @return RispostaControlli.isrBoolean() == true in caso la verifica dia esito positivo
     */
    public RispostaControlli verificaVersioneXML(String cdVersioneXml, String[] compat) {
        RispostaControlli rispostaControlli = new RispostaControlli();
        rispostaControlli.setrBoolean(false);
        if (cdVersioneXml != null) {
            for (String tmpString : compat) {
                if (cdVersioneXml.equals(tmpString)) {
                    rispostaControlli.setrBoolean(true);
                }
            }
        }
        if (!rispostaControlli.isrBoolean()) {
            rispostaControlli.setCodErr(MessaggiWSBundle.PING_SENDOBJ_005);
            rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.PING_SENDOBJ_005, cdVersioneXml));
        }
        return rispostaControlli;
    }

    /**
     * Verifica se è settato il flag di forza accettazione in input e se è definita la motivazione dell'accettazione
     *
     * @param idTipoObject
     *            id tipo oggetto
     * @param flForzaAccettazione
     *            flag 1/0 (true/false)
     * @param dlMotivazione
     *            descrizione motivazione
     *
     * @return RispostaControlli.isrBoolean() == true in caso la verifica dia esito positivo
     */
    public RispostaControlli verificaAccettazioneMotivazione(Long idTipoObject, boolean flForzaAccettazione,
            String dlMotivazione) {
        RispostaControlli rispostaControlli = new RispostaControlli();
        rispostaControlli.setrBoolean(true);
        /*
         * MEV#12941 - WS Invio oggetto: estendere controlli semantici in riferimento al Tipo oggetto/Tipo SIP versato
         */
        PigTipoObject to = entityManager.find(PigTipoObject.class, idTipoObject);
        if (to.getTiVersFile().equals(TipoVersamento.NO_ZIP.name())
                && to.getNmTipoObject().equalsIgnoreCase(Constants.STUDIO_DICOM)) {
            if (flForzaAccettazione && StringUtils.isBlank(dlMotivazione)) {
                rispostaControlli.setrBoolean(false);
                rispostaControlli.setCodErr(MessaggiWSBundle.PING_SENDOBJ_010);
                rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.PING_SENDOBJ_010));
            }
        }
        return rispostaControlli;
    }

    /**
     * Verifica che il codice versione XML sia definito e che sia presente un file XML
     *
     * @param cdVersioneXml
     *            codice versione
     * @param xml
     *            contenuto xml
     *
     * @return RispostaControlli.isrBoolean() == true in caso la verifica dia esito positivo
     */
    public RispostaControlli verificaPresenzaXml(String cdVersioneXml, String xml) {
        RispostaControlli rispostaControlli = new RispostaControlli();
        rispostaControlli.setrBoolean(true);
        if ((StringUtils.isBlank(cdVersioneXml) && StringUtils.isNotBlank(xml))
                || (StringUtils.isNotBlank(cdVersioneXml) && StringUtils.isBlank(xml))) {
            rispostaControlli.setrBoolean(false);
            rispostaControlli.setCodErr(MessaggiWSBundle.PING_SENDOBJ_004);
            rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.PING_SENDOBJ_004));
        }
        return rispostaControlli;
    }

    /**
     * Verifica che a seconda del tipo di versamento file sia definito o meno l'XML
     *
     * @param idTipoObject
     *            id tipo oggetto
     * @param xml
     *            contenuto xml
     *
     * @return RispostaControlli.isrBoolean() == true in caso la verifica dia esito positivo
     */
    public RispostaControlli verificaTipoVersamento(Long idTipoObject, String xml) {
        RispostaControlli rispostaControlli = new RispostaControlli();
        rispostaControlli.setrBoolean(true);
        try {
            PigTipoObject to = entityManager.find(PigTipoObject.class, idTipoObject);
            List<PigXsdDatiSpec> pigXsdDatiSpecs = to.getPigXsdDatiSpecs();
            if (to.getTiVersFile().equals(TipoVersamento.ZIP_CON_XML_SACER.name())
                    || to.getTiVersFile().equals(TipoVersamento.DA_TRASFORMARE.name())) {
                // Se è presente l'XML e non è presente l'XSD dati spec
                if (pigXsdDatiSpecs.isEmpty() && StringUtils.isNotBlank(xml)) {
                    rispostaControlli.setrBoolean(false);
                    rispostaControlli.setCodErr(MessaggiWSBundle.PING_SENDOBJ_008);
                    rispostaControlli.setDsErr(
                            MessaggiWSBundle.getString(MessaggiWSBundle.PING_SENDOBJ_008, to.getNmTipoObject()));
                } else if (to.getTiVersFile().equals(TipoVersamento.ZIP_CON_XML_SACER.name())
                        && !pigXsdDatiSpecs.isEmpty() && StringUtils.isBlank(xml)) {
                    rispostaControlli.setrBoolean(false);
                    rispostaControlli.setCodErr(MessaggiWSBundle.PING_SENDOBJ_020);
                    rispostaControlli.setDsErr(
                            MessaggiWSBundle.getString(MessaggiWSBundle.PING_SENDOBJ_020, to.getNmTipoObject()));
                }
            } else if (to.getTiVersFile().equals(TipoVersamento.NO_ZIP.name())
                    && to.getTiCalcKeyUnitaDoc().equals(TipoCalcolo.XML_VERS.name())) {
                if (StringUtils.isBlank(xml)) {
                    rispostaControlli.setrBoolean(false);
                    rispostaControlli.setCodErr(MessaggiWSBundle.PING_SENDOBJ_009);
                    rispostaControlli.setDsErr(
                            MessaggiWSBundle.getString(MessaggiWSBundle.PING_SENDOBJ_009, to.getNmTipoObject()));
                }
            }
        } catch (Exception e) {
            rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
            rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
                    String.join("\n", ExceptionUtils.getRootCauseStackTrace(e))));
            log.error("Eccezione nella lettura della tabella dei tipi oggetto {}", e.getMessage());
        }
        return rispostaControlli;
    }

    /**
     * Verifica nel caso l'oggetto sia di tipo StudioDicom che l'xml esista
     *
     * @param idTipoObject
     *            id tipo oggetto
     * @param xml
     *            contenuto xml
     * @param cdKeyObject
     *            chiave oggetto
     *
     * @return RispostaControlli.isrBoolean() == true in caso la verifica dia esito positivo
     */
    public RispostaControlli verificaStudioDicom(Long idTipoObject, String xml, String cdKeyObject) {
        RispostaControlli rispostaControlli = new RispostaControlli();
        rispostaControlli.setrBoolean(true);
        PigTipoObject tipoObj = entityManager.find(PigTipoObject.class, idTipoObject);
        if (tipoObj.getNmTipoObject().equalsIgnoreCase(Constants.STUDIO_DICOM) && StringUtils.isBlank(xml)) {
            rispostaControlli.setrBoolean(false);
            rispostaControlli.setCodErr(MessaggiWSBundle.PING_SENDOBJ_011);
            rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.PING_SENDOBJ_011, cdKeyObject));
        }
        rispostaControlli.setrString(tipoObj.getNmTipoObject());
        return rispostaControlli;
    }

    /**
     * Verifica che la versione dei dati specifici sia presente e corretta
     *
     * @param idTipoObject
     *            id tipo oggetto
     * @param versioneDatiSpecifici
     *            versione dati specifici
     *
     * @return RispostaControlli.isrBoolean() == true in caso la verifica dia esito positivo
     */
    // MEV27034 - rinominata da verificaVersioneDatiSpecifici(...)
    public RispostaControlli verificaVersioneXsd(long idTipoObject, String versioneDatiSpecifici) {
        RispostaControlli rispostaControlli = new RispostaControlli();
        rispostaControlli.setrBoolean(true);

        PigTipoObject tipoObject = entityManager.find(PigTipoObject.class, idTipoObject);
        if (tipoObject.getPigXsdDatiSpecs() != null && !tipoObject.getPigXsdDatiSpecs().isEmpty()) {
            if (versioneDatiSpecifici == null) {
                rispostaControlli.setrBoolean(false);
                rispostaControlli.setCodErr(MessaggiWSBundle.PING_SENDOBJ_XML_002);
                rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.PING_SENDOBJ_XML_002,
                        tipoObject.getNmTipoObject()));
            } else {
                boolean vrsDatiSpecFound = false;
                for (int j = 0; j < tipoObject.getPigXsdDatiSpecs().size(); j++) {
                    if (tipoObject.getPigXsdDatiSpecs().get(j).getCdVersioneXsd().equals(versioneDatiSpecifici)) {
                        vrsDatiSpecFound = true;
                        // Mi faccio ritornare il blocco xsd
                        rispostaControlli.setrString(tipoObject.getPigXsdDatiSpecs().get(j).getBlXsd());
                        rispostaControlli.setrLong(tipoObject.getPigXsdDatiSpecs().get(j).getIdXsdSpec());
                        break;
                    }
                }
                if (!vrsDatiSpecFound) {
                    rispostaControlli.setrBoolean(false);
                    rispostaControlli.setCodErr(MessaggiWSBundle.PING_SENDOBJ_XML_003);
                    rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.PING_SENDOBJ_XML_003,
                            versioneDatiSpecifici, tipoObject.getNmTipoObject()));
                }
            }
        }
        return rispostaControlli;
    }

    private PigTipoFileObject getTipoFileObject(long idTipoObject, String nmTipoFileObject) {
        PigTipoFileObject ptfo = null;
        try {
            String queryStr = "SELECT u FROM PigTipoFileObject u WHERE u.pigTipoObject.idTipoObject = :idTipoObject "
                    + "AND u.nmTipoFileObject = :nmTipoFileObject";
            javax.persistence.Query query = entityManager.createQuery(queryStr);
            query.setParameter("idTipoObject", idTipoObject);
            query.setParameter("nmTipoFileObject", nmTipoFileObject);
            List<PigTipoFileObject> ptfoList = query.getResultList();
            if (!ptfoList.isEmpty()) {
                ptfo = ptfoList.get(0);
            }
        } catch (Exception e) {
            log.error("Eccezione nella lettura della tabella dei tipi file oggetto {}", e.getMessage());
        }
        return ptfo;
    }

    /**
     * Verifica che il tipo di file sia definito
     *
     * @param idTipoObject
     *            id tipo oggetto
     * @param nmTipoFileObject
     *            nome tipo file
     * @param versioneDatiSpecificiFile
     *            versione dati specifici
     *
     * @return RispostaControlli.isrBoolean() == true in caso la verifica dia esito positivo
     */
    public RispostaControlli verificaTipoFileObject(Long idTipoObject, String nmTipoFileObject,
            String versioneDatiSpecificiFile) {
        RispostaControlli rispostaControlli = new RispostaControlli();
        rispostaControlli.setrBoolean(true);

        PigTipoObject tipoObj = entityManager.find(PigTipoObject.class, idTipoObject);
        PigTipoFileObject tipoFileObject = getTipoFileObject(idTipoObject, nmTipoFileObject);
        if (tipoFileObject == null) {
            rispostaControlli.setrBoolean(false);
            rispostaControlli.setCodErr(MessaggiWSBundle.PING_SENDOBJ_XML_005);
            rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.PING_SENDOBJ_XML_005,
                    nmTipoFileObject, tipoObj.getNmTipoObject()));
        } else {
            // Ricavo l'info se per il tipo oggetto specificato in input
            // sono definite le versioni del XSD dei dati specifici
            // e in caso verifico se una di esse è prevista
            if (tipoFileObject.getPigXsdDatiSpecs() != null && !tipoFileObject.getPigXsdDatiSpecs().isEmpty()) {
                if (versioneDatiSpecificiFile == null) {
                    rispostaControlli.setrBoolean(false);
                    rispostaControlli.setCodErr(MessaggiWSBundle.PING_SENDOBJ_XML_006);
                    rispostaControlli.setDsErr(
                            MessaggiWSBundle.getString(MessaggiWSBundle.PING_SENDOBJ_XML_006, nmTipoFileObject));
                } else {
                    boolean vrsDatiSpecFound = false;
                    for (int j = 0; j < tipoFileObject.getPigXsdDatiSpecs().size(); j++) {
                        if (tipoFileObject.getPigXsdDatiSpecs().get(j).getCdVersioneXsd()
                                .equals(versioneDatiSpecificiFile)) {
                            vrsDatiSpecFound = true;
                            rispostaControlli.setrString(tipoFileObject.getPigXsdDatiSpecs().get(j).getBlXsd());
                            break;
                        }
                    }
                    if (!vrsDatiSpecFound) {
                        rispostaControlli.setrBoolean(false);
                        rispostaControlli.setCodErr(MessaggiWSBundle.PING_SENDOBJ_XML_007);
                        rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.PING_SENDOBJ_XML_007,
                                versioneDatiSpecificiFile, nmTipoFileObject));
                    }
                }
            } // end if
        }

        return rispostaControlli;
    }

    /**
     * Verifica il numero di unità documentarie contenute nell'xml
     *
     * @param idTipoObject
     *            id tipo oggetto
     * @param sizeUnitaDocs
     *            numero documento unita doc
     *
     * @return RispostaControlli.isrBoolean() == true in caso la verifica dia esito positivo
     */
    public RispostaControlli verificaUdInXml(Long idTipoObject, int sizeUnitaDocs) {
        RispostaControlli rispostaControlli = new RispostaControlli();
        rispostaControlli.setrBoolean(true);

        PigTipoObject tipoObj = entityManager.find(PigTipoObject.class, idTipoObject);
        if (tipoObj.getTiVersFile().equals(TipoVersamento.NO_ZIP.name()) && sizeUnitaDocs > 1) {
            rispostaControlli.setrBoolean(false);
            rispostaControlli.setCodErr(MessaggiWSBundle.PING_SENDOBJ_XML_010);
            rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.PING_SENDOBJ_XML_010));
        }

        return rispostaControlli;
    }

    /**
     * Verifica che esista la chiave dell'unità documentaria
     *
     * @param idTipoObject
     *            id tipo oggetto
     * @param chiave
     *            valore chiave
     *
     * @return RispostaControlli.isrBoolean() == true in caso la verifica dia esito positivo
     */
    public RispostaControlli verificaChiave(Long idTipoObject, it.eng.sacerasi.ws.xml.invioAsync.ChiaveType chiave) {
        RispostaControlli rispostaControlli = new RispostaControlli();
        rispostaControlli.setrBoolean(true);

        PigTipoObject tipoObj = entityManager.find(PigTipoObject.class, idTipoObject);
        if (tipoObj.getTiVersFile().equals(TipoVersamento.NO_ZIP.name())
                && (tipoObj.getTiCalcKeyUnitaDoc().equals(TipoCalcolo.XML_VERS.name())) && chiave == null) {
            rispostaControlli.setrBoolean(false);
            rispostaControlli.setCodErr(MessaggiWSBundle.PING_SENDOBJ_XML_009);
            rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.PING_SENDOBJ_XML_009));
        }

        return rispostaControlli;
    }

    /**
     * Verifica che esista la chiave dell'unità documentaria
     *
     * @param idTipoObject
     *            id tipo oggetto
     * @param files
     *            file di tipo {@link it.eng.sacerasi.ws.xml.invioAsync.UnitaDocumentariaType.Files}
     *
     * @return RispostaControlli.isrBoolean() == true in caso la verifica dia esito positivo
     */
    public RispostaControlli verificaFiles(Long idTipoObject,
            it.eng.sacerasi.ws.xml.invioAsync.UnitaDocumentariaType.Files files) {
        RispostaControlli rispostaControlli = new RispostaControlli();
        rispostaControlli.setrBoolean(true);

        PigTipoObject tipoObj = entityManager.find(PigTipoObject.class, idTipoObject);
        if (tipoObj.getTiVersFile().equals(TipoVersamento.NO_ZIP.name())
                && (tipoObj.getTiCalcKeyUnitaDoc().equals(TipoCalcolo.XML_VERS.name())) && files == null) {
            rispostaControlli.setrBoolean(false);
            rispostaControlli.setCodErr(MessaggiWSBundle.PING_SENDOBJ_XML_012);
            rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.PING_SENDOBJ_XML_012));
        }

        return rispostaControlli;
    }

    /*
     * //////////////////////////// CONTROLLI DICOM //////////////////////////////
     **/
    /**
     * Verifica la dimensione della lista SopClass
     *
     * @param sopClassList
     *            elemento di tipo {@link SOPClassList}
     *
     * @return RispostaControlli.isrBoolean() == true in caso la verifica dia esito positivo
     */
    public RispostaControlli verificaSopClassList(SOPClassList sopClassList) {
        RispostaControlli rispostaControlli = new RispostaControlli();
        rispostaControlli.setrBoolean(true);

        if (checkDatiSpecLists(sopClassList.getSOPClass())) {
            rispostaControlli.setrBoolean(false);
            rispostaControlli.setCodErr(MessaggiWSBundle.PING_SENDOBJ_DICOM_001);
            rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.PING_SENDOBJ_DICOM_001));
        }

        return rispostaControlli;
    }

    /**
     * Verifica la dimensione della lista ModalityInStudy
     *
     * @param modalityInStudyList
     *            elemento di tipo {@link ModalityInStudyList}
     *
     * @return RispostaControlli.isrBoolean() == true in caso la verifica dia esito positivo
     */
    public RispostaControlli verificaModalityStudio(ModalityInStudyList modalityInStudyList) {
        RispostaControlli rispostaControlli = new RispostaControlli();
        rispostaControlli.setrBoolean(true);

        if (checkDatiSpecLists(modalityInStudyList.getModalityInStudy())) {
            rispostaControlli.setrBoolean(false);
            rispostaControlli.setCodErr(MessaggiWSBundle.PING_SENDOBJ_DICOM_002);
            rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.PING_SENDOBJ_DICOM_002));
        }

        return rispostaControlli;
    }

    private boolean checkDatiSpecLists(List<String> collection) {
        StringBuilder builder = new StringBuilder();
        for (String element : collection) {
            builder.append(element).append(Constants.DICOM_SEPARATOR);
        }
        // MAC #19255 - Controllo dimensione lista modalityInStudy e lista SOPClass
        if (builder.length() > 0) {
            // Elimino l'ultima virgola
            builder.deleteCharAt(builder.length() - 1);
        }
        return (builder.toString().length() > Constants.MAX_BYTES_DICOM_SIZE) ? true : false;
    }

    /**
     * //////////////////////////// CONTROLLI OBJECT //////////////////////////////
     */
    /**
     * Verifica lo stato dell'oggetto versato
     *
     * @param idObject
     *            id oggetto
     * @param flForzaAccettazione
     *            flag 1/0 (true/false)
     * @param ftpPath
     *            path ftp
     * @param tiVersFile
     *            tipo file versamento
     *
     * @return RispostaControlli.isrBoolean() == true in caso la verifica dia esito positivo
     */
    public RispostaControlli verificaStatoOggetto(Long idObject, boolean flForzaAccettazione, String ftpPath,
            String tiVersFile) {
        RispostaControlli rispostaControlli = new RispostaControlli();
        rispostaControlli.setrBoolean(true);

        PigObject obj = entityManager.find(PigObject.class, idObject);
        TipoVersamento tipoVersamentoEnum = Constants.TipoVersamento.valueOf(tiVersFile);

        if (obj.getTiStatoObject().equals(StatoOggetto.CHIUSO_ERR_VERS.name())
                && Arrays.asList(Constants.TipoVersamento.getTipoVersamentoNoTrasf()).contains(tipoVersamentoEnum)) {
            // Verifica la presenza della directory ftp
            XADiskConnection xadConn = null;
            try {
                xadConn = xadCf.getConnection();
                File path = new File(ftpPath);
                if (xadConn.fileExistsAndIsDirectory(path)) {
                    rispostaControlli.setrBoolean(false);
                    rispostaControlli.setCodErr(MessaggiWSBundle.PING_SENDOBJ_OBJ_011);
                    rispostaControlli.setDsErr(
                            MessaggiWSBundle.getString(MessaggiWSBundle.PING_SENDOBJ_OBJ_011, obj.getCdKeyObject()));
                }
            } catch (ResourceException | LockingFailedException | NoTransactionAssociatedException
                    | InsufficientPermissionOnFileException | InterruptedException e) {
                rispostaControlli.setrBoolean(false);
                rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
                rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
                        String.join("\n", ExceptionUtils.getRootCauseStackTrace(e))));
                log.error("Eccezione nella gestione della risorsa XADisk: " + e.getMessage(), e);
            } finally {
                if (xadConn != null) {
                    xadConn.close();
                    log.info("close effettuato");
                }
            }
        } else if (obj.getTiStatoObject().equals(StatoOggetto.CHIUSO_OK.name())) {
            rispostaControlli.setrBoolean(false);
            rispostaControlli.setCodErr(MessaggiWSBundle.PING_SENDOBJ_OBJ_001);
            rispostaControlli
                    .setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.PING_SENDOBJ_OBJ_001, obj.getCdKeyObject()));
        } else if (obj.getTiStatoObject().equals(StatoOggetto.IN_ATTESA_SCHED.name())
                || obj.getTiStatoObject().equals(StatoOggetto.IN_ATTESA_VERS.name())
                || obj.getTiStatoObject().equals(StatoOggetto.IN_CODA_VERS.name())
                || obj.getTiStatoObject().equals(StatoOggetto.CHIUSO_ERR_CODA.name())
                || obj.getTiStatoObject().equals(StatoOggetto.CHIUSO_ERR_RECUPERABILE.name())
                || obj.getTiStatoObject().equals(StatoOggetto.DA_TRASFORMARE.name())
                || obj.getTiStatoObject().equals(StatoOggetto.TRASFORMAZIONE_NON_ATTIVA.name())
                || obj.getTiStatoObject().equals(StatoOggetto.TRASFORMAZIONE_IN_CORSO.name())
                || obj.getTiStatoObject().equals(StatoOggetto.ERRORE_TRASFORMAZIONE.name())
                || obj.getTiStatoObject().equals(StatoOggetto.WARNING_TRASFORMAZIONE.name())
                || obj.getTiStatoObject().equals(StatoOggetto.TRASFORMATO.name())
                || obj.getTiStatoObject().equals(StatoOggetto.VERSATO_A_PING.name())) {
            rispostaControlli.setrBoolean(false);
            rispostaControlli.setCodErr(MessaggiWSBundle.PING_SENDOBJ_OBJ_002);
            rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.PING_SENDOBJ_OBJ_002,
                    obj.getCdKeyObject(), obj.getTiStatoObject()));
        } else if (obj.getTiStatoObject().equals(StatoOggetto.IN_ATTESA_FILE.name())) {
            rispostaControlli.setrBoolean(false);
            rispostaControlli.setCodErr(MessaggiWSBundle.PING_SENDOBJ_OBJ_010);
            rispostaControlli
                    .setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.PING_SENDOBJ_OBJ_010, obj.getCdKeyObject()));
        } else if (obj.getPigTipoObject().getNmTipoObject().equalsIgnoreCase(Constants.STUDIO_DICOM)) {
            if (obj.getTiStatoObject().equals(StatoOggetto.WARNING.name()) && !flForzaAccettazione) {
                rispostaControlli.setrBoolean(false);
                rispostaControlli.setCodErr(MessaggiWSBundle.PING_SENDOBJ_OBJ_003);
                rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.PING_SENDOBJ_OBJ_003));
            } else if (obj.getTiStatoObject().equals(StatoOggetto.CHIUSO_WARNING.name())) {
                rispostaControlli.setrBoolean(false);
                rispostaControlli.setCodErr(MessaggiWSBundle.PING_SENDOBJ_OBJ_004);
                rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.PING_SENDOBJ_OBJ_004));
            }
        }
        return rispostaControlli;
    }

    /**
     * Verifica che il tipo di oggetto non sia stato modificato tra versamenti del medesimo oggetto
     *
     * @param idObject
     *            id oggetto
     * @param nmTipoObject
     *            nome tipo oggetto
     * @param tiVersFile
     *            tipo file versamento
     *
     * @return RispostaControlli.isrBoolean() == true in caso la verifica dia esito positivo
     */
    public RispostaControlli verificaModificaTipoOggetto(Long idObject, String nmTipoObject, String tiVersFile) {
        RispostaControlli rispostaControlli = new RispostaControlli();
        rispostaControlli.setrBoolean(true);

        PigObject obj = entityManager.find(PigObject.class, idObject);
        if (obj.getTiStatoObject().equals(StatoOggetto.CHIUSO_ERR_NOTIF.name())
                || obj.getTiStatoObject().equals(StatoOggetto.CHIUSO_ERR_VERIFICA_HASH.name())
                || obj.getTiStatoObject().equals(StatoOggetto.CHIUSO_ERR_SCHED.name())
                || obj.getTiStatoObject().equals(StatoOggetto.CHIUSO_ERR_VERS.name())
                || obj.getTiStatoObject().equals(StatoOggetto.CHIUSO_ERR_CRASH_DPI.name())
                || obj.getTiStatoObject().equals(StatoOggetto.CHIUSO_ERR_CRASH_FTP.name())
                || obj.getTiStatoObject().equals(StatoOggetto.CHIUSO_ERR_CRASH_FS_PRIM.name())
                || obj.getTiStatoObject().equals(StatoOggetto.CHIUSO_ERR_CRASH_FS_SECOND.name())
                || obj.getTiStatoObject().equals(StatoOggetto.ANNULLATO.name())
                || obj.getTiStatoObject().equals(StatoOggetto.CHIUSO_ERR_TRASFORMAZIONE.name())
                || obj.getTiStatoObject().equals(StatoOggetto.CHIUSO_ERR_VERSAMENTO_A_PING.name())) {
            if (!obj.getPigTipoObject().getNmTipoObject().equals(nmTipoObject)) {
                // MEV#15178 - Modifica Tipo oggetto in dettaglio oggetto per gli oggetti da trasformare
                String tipoVersEsistente = obj.getPigTipoObject().getTiVersFile();
                // MEV 34105 - disabilitato controllo su cambiamento tipo oggetto anche per oggetti generati da una
                // trasformazione e non.
                if (tiVersFile != null && tiVersFile.equals(tipoVersEsistente)
                        && (tiVersFile.equals(Constants.TipoVersamento.DA_TRASFORMARE.name())
                                || tiVersFile.equals(Constants.TipoVersamento.ZIP_CON_XML_SACER.name()))) {
                    // Se il tipoVersFile dell'oggetto precedente e quello che si sta versando sono uguali e
                    // DA_TRASFORMARE
                    // allora è possibile andare avanti altrimenti messaggio di errore.
                } else {
                    rispostaControlli.setrBoolean(false);
                    rispostaControlli.setCodErr(MessaggiWSBundle.PING_SENDOBJ_OBJ_012);
                    rispostaControlli.setDsErr(
                            MessaggiWSBundle.getString(MessaggiWSBundle.PING_SENDOBJ_OBJ_012, obj.getCdKeyObject()));
                }
            }
        }
        return rispostaControlli;
    }

    /**
     * Verifica lo stato dell'oggetto nel caso sia Dicom
     *
     * @param idTipoObj
     *            id tipo oggetto
     * @param dcmHash
     *            hash
     * @param flForzaWarning
     *            flag 1/0 (true/false)
     *
     * @return RispostaControlli.isrBoolean() == true in caso la verifica dia esito positivo
     */
    public RispostaControlli verificaOggettoDicom(Long idTipoObj, String dcmHash, boolean flForzaWarning) {
        RispostaControlli rispostaControlli = new RispostaControlli();
        rispostaControlli.setrBoolean(true);

        PigTipoObject tipoObj = entityManager.find(PigTipoObject.class, idTipoObj);
        if (!flForzaWarning) {
            PigObject obj = getDicomObjectsByDcmHash(dcmHash, tipoObj.getPigVer().getIdVers());
            if (obj != null) {
                if (obj.getTiStatoObject().equals(StatoOggetto.CHIUSO_OK.name())) {
                    rispostaControlli.setrBoolean(false);
                    rispostaControlli.setCodErr(MessaggiWSBundle.PING_SENDOBJ_OBJ_005);
                    rispostaControlli.setDsErr(
                            MessaggiWSBundle.getString(MessaggiWSBundle.PING_SENDOBJ_OBJ_005, obj.getCdKeyObject()));
                } else if (obj.getTiStatoObject().equals(StatoOggetto.IN_ATTESA_FILE.name())
                        || obj.getTiStatoObject().equals(StatoOggetto.IN_ATTESA_SCHED.name())
                        || obj.getTiStatoObject().equals(StatoOggetto.IN_ATTESA_VERS.name())
                        || obj.getTiStatoObject().equals(StatoOggetto.IN_CODA_VERS.name())
                        || obj.getTiStatoObject().equals(StatoOggetto.CHIUSO_ERR_CODA.name())
                        || obj.getTiStatoObject().equals(StatoOggetto.CHIUSO_ERR_RECUPERABILE.name())) {
                    rispostaControlli.setrBoolean(false);
                    rispostaControlli.setCodErr(MessaggiWSBundle.PING_SENDOBJ_OBJ_006);
                    rispostaControlli.setDsErr(
                            MessaggiWSBundle.getString(MessaggiWSBundle.PING_SENDOBJ_OBJ_006, obj.getCdKeyObject()));
                } else if (obj.getTiStatoObject().equals(StatoOggetto.WARNING.name())
                        || obj.getTiStatoObject().equals(StatoOggetto.CHIUSO_WARNING.name())) {
                    rispostaControlli.setrBoolean(false);
                    rispostaControlli.setCodErr(MessaggiWSBundle.PING_SENDOBJ_OBJ_007);
                    rispostaControlli.setDsErr(
                            MessaggiWSBundle.getString(MessaggiWSBundle.PING_SENDOBJ_OBJ_007, obj.getCdKeyObject()));
                } else if (obj.getTiStatoObject().equals(StatoOggetto.CHIUSO_ERR_NOTIF.name())
                        || obj.getTiStatoObject().equals(StatoOggetto.CHIUSO_ERR_VERIFICA_HASH.name())
                        || obj.getTiStatoObject().equals(StatoOggetto.CHIUSO_ERR_SCHED.name())
                        || obj.getTiStatoObject().equals(StatoOggetto.CHIUSO_ERR_VERS.name())) {
                    rispostaControlli.setrBoolean(false);
                    rispostaControlli.setCodErr(MessaggiWSBundle.PING_SENDOBJ_OBJ_008);
                    rispostaControlli.setDsErr(
                            MessaggiWSBundle.getString(MessaggiWSBundle.PING_SENDOBJ_OBJ_008, obj.getCdKeyObject()));
                }
            }
        } else {
            rispostaControlli.setrBoolean(false);
            rispostaControlli.setCodErr(MessaggiWSBundle.PING_SENDOBJ_OBJ_009);
            rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.PING_SENDOBJ_OBJ_009));
        }
        return rispostaControlli;
    }

    private PigObject getDicomObjectsByDcmHash(String dcmHash, Long idVers) {
        PigObject obj = null;
        try {
            String queryStr = "SELECT obj "
                    + "FROM PigInfoDicom dcm INNER JOIN dcm.pigObject obj INNER JOIN obj.pigSessioneIngests ses "
                    + "WHERE dcm.dsDcmHash = :dcmhash AND dcm.idVers = :vers "
                    + "AND ses.idSessioneIngest = obj.idLastSessioneIngest";
            javax.persistence.Query query = entityManager.createQuery(queryStr);
            query.setParameter("dcmhash", dcmHash);
            query.setParameter("vers", HibernateUtils.bigDecimalFrom(idVers));
            List<PigObject> objList = query.getResultList();
            if (!objList.isEmpty()) {
                obj = objList.get(0);
            }
        } catch (Exception e) {
            log.error("Eccezione nella lettura della tabella dei tipi file oggetto {}", e);
        }
        return obj;
    }

    /**
     * Verifica lo stato dell'ultima sessione dell'oggetto
     *
     * @param idObject
     *            id oggetto
     * @param stato
     *            stato oggetto
     *
     * @return RispostaControlli.isrBoolean() == true in caso la verifica dia esito positivo
     */
    public RispostaControlli verificaUltimaSessioneOggetto(Long idObject, Constants.StatoOggetto stato) {
        RispostaControlli rispostaControlli = new RispostaControlli();
        rispostaControlli.setrBoolean(false);

        PigObject obj = entityManager.find(PigObject.class, idObject);
        PigSessioneIngest lastSession = entityManager.find(PigSessioneIngest.class,
                obj.getIdLastSessioneIngest().longValue());
        if (lastSession.getTiStato().equals(stato.name())) {
            rispostaControlli.setrBoolean(true);
            rispostaControlli.setrLong(lastSession.getIdSessioneIngest());
        }
        return rispostaControlli;
    }

    public boolean isVerificaPartizioneOn() {
        String queryStr = "SELECT valoreParamApplic.dsValoreParamApplic "
                + "FROM PigValoreParamApplic valoreParamApplic " + "JOIN valoreParamApplic.pigParamApplic paramApplic "
                + "WHERE paramApplic.nmParamApplic = 'VERIFICA_PARTIZIONI' ";
        Query query = entityManager.createQuery(queryStr);

        String res = (String) query.getSingleResult();
        return new Boolean(res);
    }

    /**
     * Verifica l'esistenza di un oggetto figlio dell'oggetto padre specificato con progressivo pari a pgObjectFiglio
     *
     * @param idObjectPadre
     *            id padre
     * @param pgObjectFiglio
     *            id figlio
     *
     * @return RispostaControlli.isrBoolean() == true in caso la verifica dia esito positivo
     */
    public RispostaControlli verificaOggettoFiglio(Long idObjectPadre, BigDecimal pgObjectFiglio) {
        RispostaControlli rispostaControlli = new RispostaControlli();
        rispostaControlli.setrBoolean(false);

        String queryStr = "SELECT obj FROM PigObject obj WHERE obj.pigObjectPadre.idObject = :idObjectPadre AND obj.pgOggettoTrasf = :pgObjectFiglio AND obj.tiStatoObject NOT IN (:statiObject)";
        javax.persistence.Query query = entityManager.createQuery(queryStr);
        query.setParameter("idObjectPadre", idObjectPadre);
        query.setParameter("pgObjectFiglio", pgObjectFiglio);
        query.setParameter("statiObject",
                Arrays.asList(StatoOggetto.CHIUSO_ERR_NOTIF.name(), StatoOggetto.CHIUSO_ERR_SCHED.name(),
                        StatoOggetto.CHIUSO_ERR_VERIFICA_HASH.name(), StatoOggetto.CHIUSO_ERR_CODA.name(),
                        StatoOggetto.CHIUSO_ERR_VERS.name(), StatoOggetto.CHIUSO_ERR_CRASH_DPI.name(),
                        StatoOggetto.CHIUSO_ERR_CRASH_FTP.name(), StatoOggetto.CHIUSO_ERR_CRASH_FS_PRIM.name(),
                        StatoOggetto.CHIUSO_ERR_CRASH_FS_SECOND.name(), StatoOggetto.ANNULLATO.name()));

        List<PigObject> lista = query.getResultList();
        try {
            if (!lista.isEmpty()) {
                rispostaControlli.setrBoolean(true);
                rispostaControlli.setrLong(lista.get(0).getIdObject());
            }
        } catch (Exception ex) {
            rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
            rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
                    String.join("\n", ExceptionUtils.getRootCauseStackTrace(ex))));
            log.error("Errore nella lettura della tabella PigObject ", ex);
        }
        return rispostaControlli;
    }

    public RispostaControlli verificaCdVersGen(Long idTipoObject, String cdVersGen) {
        RispostaControlli rispostaControlli = new RispostaControlli();
        rispostaControlli.setrBoolean(false);
        Query query = entityManager.createQuery(
                "SELECT p FROM PigVersTipoObjectDaTrasf p WHERE p.cdVersGen = :cdVersGen AND p.pigTipoObjectDaTrasf.idTipoObject = :idTipoObject");
        query.setParameter("idTipoObject", idTipoObject);
        query.setParameter("cdVersGen", cdVersGen);
        try {
            List<PigVersTipoObjectDaTrasf> lista = query.getResultList();
            if (!lista.isEmpty()) {
                rispostaControlli.setrBoolean(true);
            }
        } catch (Exception ex) {
            rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
            rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
                    String.join("\n", ExceptionUtils.getRootCauseStackTrace(ex))));
            log.error("Errore nella lettura della tabella PigObject ", ex);
        }
        return rispostaControlli;
    }

    /**
     * Verifica che, se l'oggetto è 'DA_TRASFORMARE', per gli oggetti figli esista una unità doc con stato = VERSATA_OK
     * o VERSATA_TIMEOUT:
     *
     * @param idObject
     *            id oggetto
     *
     * @return true se esiste almeno una unità doc
     */
    public RispostaControlli verificaFigliVersati(Long idObject) {
        RispostaControlli rispostaControlli = new RispostaControlli();
        rispostaControlli.setrBoolean(false);
        Query query = entityManager.createQuery(
                "SELECT udObj FROM PigUnitaDocObject udObj JOIN udObj.pigObject obj JOIN obj.pigObjectPadre padre WHERE padre.idObject = :idObject AND udObj.tiStatoUnitaDocObject IN ('VERSATA_OK','VERSATA_TIMEOUT')");
        query.setParameter("idObject", idObject);
        try {
            List<PigVersTipoObjectDaTrasf> lista = query.getResultList();
            if (!lista.isEmpty()) {
                rispostaControlli.setrBoolean(true);
            }
        } catch (Exception ex) {
            rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
            rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
                    String.join("\n", ExceptionUtils.getRootCauseStackTrace(ex))));
            log.error("Errore nella lettura della tabella PigObject ", ex);
        }
        return rispostaControlli;
    }

    public RispostaControlli verificaOggettoPadre(Long idObject, Long idOggettoPadre) {
        RispostaControlli rispostaControlli = new RispostaControlli();
        rispostaControlli.setrBoolean(true);
        try {
            PigObject obj = entityManager.find(PigObject.class, idObject);
            if (obj.getPigObjectPadre() != null
                    && (idOggettoPadre == null || obj.getPigObjectPadre().getIdObject() != idOggettoPadre)) {
                rispostaControlli.setrBoolean(false);
                rispostaControlli.setCodErr(MessaggiWSBundle.PING_SENDOBJ_OBJ_013);
                rispostaControlli.setDsErr(
                        MessaggiWSBundle.getString(MessaggiWSBundle.PING_SENDOBJ_OBJ_013, obj.getCdKeyObject()));
            }
        } catch (Exception ex) {
            rispostaControlli.setrBoolean(false);
            rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
            rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
                    String.join("\n", ExceptionUtils.getRootCauseStackTrace(ex))));
            log.error("Errore nella lettura della tabella PigObject ", ex);
        }
        return rispostaControlli;
    }

}
