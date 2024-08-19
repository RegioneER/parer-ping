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

package it.eng.sacerasi.ws.ejb;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.sacerasi.common.Constants;
import it.eng.sacerasi.common.Constants.StatoOggetto;
import it.eng.sacerasi.common.Constants.TipiHash;
import it.eng.sacerasi.entity.PigObject;
import it.eng.sacerasi.entity.PigTipoObject;
import it.eng.sacerasi.entity.PigVers;
import it.eng.sacerasi.messages.MessaggiWSBundle;
import it.eng.sacerasi.slite.gen.tablebean.PigVersTipoObjectDaTrasfTableBean;
import it.eng.sacerasi.web.ejb.AmministrazioneEjb;
import it.eng.sacerasi.web.helper.ConfigurationHelper;
import it.eng.sacerasi.ws.dto.RispostaControlli;
import it.eng.sacerasi.ws.invioOggettoAsincrono.dto.InvioOggettoAsincronoEstesoInput;
import it.eng.sacerasi.ws.invioOggettoAsincrono.dto.InvioOggettoAsincronoExt;
import it.eng.sacerasi.ws.invioOggettoAsincrono.ejb.ControlliInvioOggettoAsincrono;

/**
 *
 * @author Gilioli_P
 */
@SuppressWarnings("unchecked")
@Stateless(mappedName = "ControlliWS")
@LocalBean
public class ControlliWS {

    private static final Logger log = LoggerFactory.getLogger(ControlliWS.class);
    @PersistenceContext(unitName = "SacerAsiJPA")
    private EntityManager entityManager;
    @EJB(mappedName = "java:app/SacerAsync-ejb/AmministrazioneEjb")
    private AmministrazioneEjb amministrazioneEjb;
    @EJB(mappedName = "java:app/SacerAsync-ejb/ControlliInvioOggettoAsincrono")
    private ControlliInvioOggettoAsincrono controlliInvioOggettoAsincrono;
    @EJB(mappedName = "java:app/SacerAsync-ejb/ConfigurationHelper")
    private ConfigurationHelper configurationHelper;

    /**
     * Verifica l'esistenza dell'ambiente
     *
     * @param nmAmbiente
     *            nome ambiente
     *
     * @return RispostaControlli.isrBoolean() == true in caso la verifica dia esito positivo
     */
    public RispostaControlli verificaNomeAmbiente(String nmAmbiente) {
        RispostaControlli rispostaControlli = new RispostaControlli();
        rispostaControlli.setrBoolean(false);
        try {
            if (StringUtils.isNotBlank(nmAmbiente)) {
                String queryStr = "SELECT COUNT(u) FROM PigAmbienteVers u WHERE u.nmAmbienteVers = :nmAmbiente";
                javax.persistence.Query query = entityManager.createQuery(queryStr);
                query.setParameter("nmAmbiente", nmAmbiente);
                long presente = (Long) query.getSingleResult();
                if (presente > 0) {
                    rispostaControlli.setrBoolean(true);
                }
            }
        } catch (Exception e) {
            rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
            rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
                    String.join("\n", ExceptionUtils.getRootCauseStackTrace(e))));
            log.error("Eccezione nella lettura della tabella degli ambienti ", e);
        }
        return rispostaControlli;
    }

    /**
     * Verifica l'esistenza del versatore per l'ambiente
     *
     * @param nmAmbiente
     *            nome ambiente
     * @param nmVersatore
     *            nome versatore
     *
     * @return RispostaControlli.isrBoolean() == true in caso la verifica dia esito positivo
     */
    public RispostaControlli verificaNomeVersatore(String nmAmbiente, String nmVersatore) {
        RispostaControlli rispostaControlli = new RispostaControlli();
        rispostaControlli.setrBoolean(false);
        try {
            String queryStr = "SELECT u FROM PigVers u WHERE u.pigAmbienteVer.nmAmbienteVers = :nmAmbiente AND u.nmVers = :nmVersatore";
            javax.persistence.Query query = entityManager.createQuery(queryStr);
            query.setParameter("nmAmbiente", nmAmbiente);
            query.setParameter("nmVersatore", nmVersatore);

            List<PigVers> pvList = query.getResultList();
            if (!pvList.isEmpty()) {
                rispostaControlli.setrBoolean(true);
                rispostaControlli.setrLong(pvList.get(0).getIdVers());
                rispostaControlli.setrString(pvList.get(0).getDsPathInputFtp());
                rispostaControlli.setrObject(pvList.get(0).getDsPathOutputFtp());
                // BigDecimal idAmbienteVers =
                // BigDecimal.valueOf(pvList.get(0).getPigAmbienteVer().getIdAmbienteVers());
                // BigDecimal idVers = BigDecimal.valueOf(pvList.get(0).getIdVers());
                // rispostaControlli.setrString(configurationHelper.getValoreParamApplic("DS_PATH_INPUT_FTP",
                // idAmbienteVers, idVers, null, Constants.TipoPigVGetValAppart.VERS));
                // rispostaControlli.setrObject(configurationHelper.getValoreParamApplic("DS_PATH_OUTPUT_FTP",
                // idAmbienteVers, idVers, null, Constants.TipoPigVGetValAppart.VERS));
            }
        } catch (Exception e) {
            rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
            rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
                    String.join("\n", ExceptionUtils.getRootCauseStackTrace(e))));
            log.error("Eccezione nella lettura  della tabella dei versatori ", e);
        }
        return rispostaControlli;
    }

    /**
     * Verifica la correttezza della password
     *
     * @param idVers
     *            id versamento
     * @param cdPasswordVers
     *            password
     *
     * @return RispostaControlli.isrBoolean() == true in caso la verifica dia esito positivo
     */
    public RispostaControlli verificaPassword(long idVers, String cdPasswordVers) {
        RispostaControlli rispostaControlli = new RispostaControlli();
        rispostaControlli.setrBoolean(false);

        String encodedPwd = encodePassword(cdPasswordVers);

        try {
            String queryStr = "SELECT COUNT(u) FROM PigVers u WHERE u.idVers = :idVers AND u.cdPasswordVers = :cdPasswordVers";
            javax.persistence.Query query = entityManager.createQuery(queryStr);
            query.setParameter("idVers", idVers);
            query.setParameter("cdPasswordVers", encodedPwd);

            long presente = (Long) query.getSingleResult();
            if (presente > 0) {
                rispostaControlli.setrBoolean(true);
            }
        } catch (Exception e) {
            rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
            rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
                    String.join("\n", ExceptionUtils.getRootCauseStackTrace(e))));
            log.error("Eccezione nella lettura  della tabella dei versatori ", e);
        }
        return rispostaControlli;
    }

    /**
     * Verifica la presenza della chiave oggetto
     *
     * @param cdKeyObject
     *            numero oggetto
     * @param codErr
     *            il codice di errore del messaggio da inviare in caso di errore
     *
     * @return RispostaControlli.isrBoolean() == true in caso la verifica dia esito positivo
     */
    public RispostaControlli verificaNotBlankCdKeyObject(String cdKeyObject, String codErr) {
        RispostaControlli rispostaControlli = new RispostaControlli();
        rispostaControlli.setrBoolean(false);
        if (StringUtils.isNotBlank(cdKeyObject)) {
            rispostaControlli.setrBoolean(true);
        } else {
            rispostaControlli.setCodErr(codErr);
            rispostaControlli.setDsErr(MessaggiWSBundle.getString(codErr, cdKeyObject));
        }
        return rispostaControlli;
    }

    /**
     * Verifica che la lunghezza della chiave oggetto non sia superiore a (100-4) caratteri
     *
     * NB: Gli ultimi 4 caratteri serviranno per l'estensione del file in certi casi
     *
     * @param cdKeyObject
     *            numero oggetto
     * @param codErr
     *            il codice di errore del messaggio da inviare in caso di errore
     *
     * @return RispostaControlli.isrBoolean() == true in caso la verifica dia esito positivo
     */
    public RispostaControlli verificaCdKeyObjectLength(String cdKeyObject, String codErr) {
        RispostaControlli rispostaControlli = new RispostaControlli();
        rispostaControlli.setrBoolean(false);

        // MEV 27880 - i figli generati da trasformazione hanno un nome più lungo di 38 caratteri rispetto al padre
        // e non ho il tipo di versamento, quindi controllo il nome con una regex.
        if (cdKeyObject.matches(".*_[A-F0-9]{32}_[0-9]{4}$") && cdKeyObject.length() <= (138 - 4)) {
            rispostaControlli.setrBoolean(true);
        } else if (cdKeyObject.length() <= (100 - 4)) {
            rispostaControlli.setrBoolean(true);
        } else {
            rispostaControlli.setCodErr(codErr);
            rispostaControlli.setDsErr(MessaggiWSBundle.getString(codErr, cdKeyObject));
        }
        return rispostaControlli;
    }

    /**
     * Verifica l'esistenza di un oggetto con la chiave oggetto data come parametro
     *
     * @param nmAmbiente
     *            nome ambiente
     * @param nmVersatore
     *            nome versatore
     * @param cdKeyObject
     *            chiave oggetto
     *
     * @return RispostaControlli.isrBoolean() == true in caso la verifica dia esito positivo
     */
    public RispostaControlli verificaCdKeyObject(String nmAmbiente, String nmVersatore, String cdKeyObject) {
        RispostaControlli rispostaControlli = new RispostaControlli();
        rispostaControlli.setrBoolean(false);

        String queryStr = "SELECT obj FROM PigObject obj INNER JOIN obj.pigVer vers "
                + "WHERE vers.pigAmbienteVer.nmAmbienteVers = :nmAmbiente AND vers.nmVers = :nmVers AND obj.cdKeyObject = :cdKey";
        javax.persistence.Query query = entityManager.createQuery(queryStr);
        query.setParameter("nmAmbiente", nmAmbiente);
        query.setParameter("nmVers", nmVersatore);
        query.setParameter("cdKey", cdKeyObject);
        List<PigObject> lista = query.getResultList();
        try {
            if (!lista.isEmpty()) {
                rispostaControlli.setrBoolean(true);
                rispostaControlli.setrLong(lista.get(0).getIdObject());
                rispostaControlli.setrString(lista.get(0).getTiStatoObject());
                rispostaControlli.setrObject(lista.get(0).getNiTotObjectTrasf());
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
     * Verifica che il nome del tipo oggetto sia definito nell'ambito del versatore; In caso di errore setta il codice
     * errore e la descrizione in base al codice passato come parametro
     *
     * @param idVers
     *            idVersatore
     * @param nmTipoObject
     *            nome tipo oggetto
     * @param codErr
     *            codice errore
     *
     * @return RispostaControlli.isrBoolean() == true in caso la verifica dia esito positivo
     */
    public RispostaControlli verificaNomeTipoObject(long idVers, String nmTipoObject, String codErr) {
        RispostaControlli rispostaControlli = new RispostaControlli();
        rispostaControlli.setrBoolean(false);
        try {
            String queryStr = "SELECT u FROM PigTipoObject u WHERE u.pigVer.idVers = :idVers AND u.nmTipoObject = :nmTipoObject";
            javax.persistence.Query query = entityManager.createQuery(queryStr);
            query.setParameter("idVers", idVers);
            query.setParameter("nmTipoObject", nmTipoObject);

            List<PigTipoObject> ptoList = query.getResultList();
            if (!ptoList.isEmpty()) {
                rispostaControlli.setrBoolean(true);
                rispostaControlli.setrLong(ptoList.get(0).getIdTipoObject());
                rispostaControlli.setrString(ptoList.get(0).getTiVersFile());
                rispostaControlli.setrObject(ptoList.get(0).getDsRegExpCdVers());
            } else {
                rispostaControlli.setCodErr(codErr);
                rispostaControlli.setDsErr(MessaggiWSBundle.getString(codErr, nmTipoObject));
            }
        } catch (Exception e) {
            rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
            rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
                    String.join("\n", ExceptionUtils.getRootCauseStackTrace(e))));
            log.error("Eccezione nella lettura della tabella dei tipi oggetto ", e);
        }
        return rispostaControlli;
    }

    /**
     * Verifica che l'algoritmo sia SHA-1/256/etc In caso di errore setta il codice errore e la descrizione in base al
     * codice passato come parametro
     *
     * @param hashAlgo
     *            algoritmo hash
     * @param errorCode
     *            codice di errore
     *
     * @return RispostaControlli.isrBoolean() == true in caso la verifica dia esito positivo
     */
    public RispostaControlli verificaAlgoritmo(String hashAlgo, String errorCode) {
        RispostaControlli rispostaControlli = new RispostaControlli();
        rispostaControlli.setrBoolean(true);

        if (Constants.TipiHash.evaluateByDesc(hashAlgo).equals(TipiHash.SCONOSCIUTO)) {
            rispostaControlli.setrBoolean(false);
            rispostaControlli.setCodErr(errorCode);
            rispostaControlli.setDsErr(MessaggiWSBundle.getString(errorCode));
        }

        return rispostaControlli;
    }

    /**
     * Verifica che l'encoding sia HexBinary In caso di errore setta il codice errore e la descrizione in base al codice
     * passato come parametro
     *
     * @param hashEncoding
     *            encoding hash
     * @param errorCode
     *            codice di errore
     *
     * @return RispostaControlli.isrBoolean() == true in caso la verifica dia esito positivo
     */
    public RispostaControlli verificaEncoding(String hashEncoding, String errorCode) {
        RispostaControlli rispostaControlli = new RispostaControlli();
        rispostaControlli.setrBoolean(true);

        if (Constants.TipiEncBinari.evaluateByDesc(hashEncoding).equals(TipiHash.SCONOSCIUTO)) {
            rispostaControlli.setrBoolean(false);
            rispostaControlli.setCodErr(errorCode);
            rispostaControlli.setDsErr(MessaggiWSBundle.getString(errorCode));
        }

        return rispostaControlli;
    }

    private String encodePassword(String password) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-1");
            md.update(password.getBytes("UTF-8"), 0, password.length());
        } catch (NoSuchAlgorithmException ex) {
            log.error("Algoritmo SHA-1 non supportato");
        } catch (UnsupportedEncodingException ex) {
            log.error("Algoritmo UTF-8 non supportato");
        }
        byte[] pwdHash = md.digest();
        return new String(Base64.encodeBase64(pwdHash));
    }

    public RispostaControlli verificaStatoOggetto(Long idObject, StatoOggetto stato, String errorCode) {
        RispostaControlli rispostaControlli = new RispostaControlli();
        rispostaControlli.setrBoolean(true);

        PigObject obj = entityManager.find(PigObject.class, idObject);
        if (!obj.getTiStatoObject().equals(stato.name())) {
            rispostaControlli.setrBoolean(false);
            rispostaControlli.setCodErr(errorCode);
            rispostaControlli.setDsErr(MessaggiWSBundle.getString(errorCode, obj.getCdKeyObject()));
        } else {
            rispostaControlli.setrLong(obj.getIdLastSessioneIngest().longValue());
        }
        return rispostaControlli;
    }

    /*
     * Gruppo di controlli inseriti per la MAC #14809 - WS invio oggetto: non viene calcolato il versatore per cui
     * generare oggetti
     */
    public RispostaControlli verificaCdVersGenCasoNonEsteso(InvioOggettoAsincronoExt invioOggettoAsincronoExt) {
        RispostaControlli rispostaControlli = new RispostaControlli();
        rispostaControlli.setrBoolean(false); // Imposta il default ad errore!
        try {
            PigVersTipoObjectDaTrasfTableBean tb = amministrazioneEjb
                    .getPigVersTipoObjectDaTrasfTableBean(new BigDecimal(invioOggettoAsincronoExt.getIdTipoObject()));
            if (tb == null || tb.size() == 0) {
                // CASO in cui non esistano versatori per cui generare oggetti
                rispostaControlli.setCodErr(MessaggiWSBundle.PING_SENDOBJ_024);
                rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.PING_SENDOBJ_024));
            } else if (tb.size() == 1) {
                // CASO in cui esiste UN solo versatore per cui generare oggetti
                invioOggettoAsincronoExt.setCdVersGen(tb.getRow(0).getCdVersGen());
            } else {
                // CASO in cui esistono più di un versatore per cui generare oggetti
                // usa la regexp presente sul tipo oggetto
                String regExp = invioOggettoAsincronoExt.getDsRegExpCdVers();
                if (StringUtils.isBlank(regExp)) {
                    rispostaControlli.setCodErr(MessaggiWSBundle.PING_SENDOBJ_021);
                    rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.PING_SENDOBJ_021,
                            invioOggettoAsincronoExt.getNmTipoObject()));
                } else {
                    Pattern regxp = Pattern.compile(regExp);
                    Matcher matcher = regxp
                            .matcher(invioOggettoAsincronoExt.getInvioOggettoAsincronoInput().getCdKeyObject());
                    if (matcher.matches()) {
                        String cdVersGen = matcher.group(1);
                        invioOggettoAsincronoExt.setCdVersGen(cdVersGen);
                    } else {
                        rispostaControlli.setCodErr(MessaggiWSBundle.PING_SENDOBJ_022);
                        rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.PING_SENDOBJ_022));
                    }
                }
            }
            // Se il cdVersGen è stato determinato passa di qui altrimenti esce ma avrà sicuramente impostato un errore
            // in uscita!
            if (!StringUtils.isBlank(invioOggettoAsincronoExt.getCdVersGen())) {
                rispostaControlli = controlliInvioOggettoAsincrono.verificaCdVersGen(
                        invioOggettoAsincronoExt.getIdTipoObject(), invioOggettoAsincronoExt.getCdVersGen());
                if (!rispostaControlli.isrBoolean()) {
                    rispostaControlli.setCodErr(MessaggiWSBundle.PING_SENDOBJ_023);
                    rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.PING_SENDOBJ_023));
                } else {
                    rispostaControlli.setrBoolean(true);
                }
            }
        } catch (Exception e) {
            rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
            rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
                    String.join("\n", ExceptionUtils.getRootCauseStackTrace(e))));
            log.error("Eccezione nella lettura della tabella dei PigVersTipoObjectDaTrasf", e);
        }

        return rispostaControlli;
    }

    /**
     * Determina il valore di cdVersGen nel caso in cui non fosse passato già valorizzato
     *
     * Se il cdVersGen non è valorizzato, se il tipo vers file = "DA_TRASFORMARE" e la regExpr non è definita verifica
     * se esistono tipi oggetto da trasformare; se ne esiste uno solo estrae il suo cdVersGen e valorizza il parametro
     * di input con quello estratto.
     *
     * @param invOgg
     *            oggetto contentnte tutti i parametri di input
     */
    public void determinaCdVersGen(InvioOggettoAsincronoExt invOgg) {
        if (invOgg.getInvioOggettoAsincronoInput() instanceof InvioOggettoAsincronoEstesoInput) {
            InvioOggettoAsincronoEstesoInput oggExtInput = (InvioOggettoAsincronoEstesoInput) invOgg
                    .getInvioOggettoAsincronoInput();
            String cdVersGen = oggExtInput.getCdVersGen();
            if (cdVersGen == null || cdVersGen.trim().equals("")) {
                if (invOgg.getTiVersFile().equals(Constants.TipoVersamento.DA_TRASFORMARE.name())
                        && (invOgg.getDsRegExpCdVers() == null || invOgg.getDsRegExpCdVers().equals(""))) {
                    PigVersTipoObjectDaTrasfTableBean tipoObjDaTrasfTB = amministrazioneEjb
                            .getPigVersTipoObjectDaTrasfTableBean(new BigDecimal(invOgg.getIdTipoObject()));
                    if (tipoObjDaTrasfTB != null && tipoObjDaTrasfTB.size() == 1) {
                        oggExtInput.setCdVersGen(tipoObjDaTrasfTB.getRow(0).getCdVersGen());
                    }
                }
            }
        }
    }
}
