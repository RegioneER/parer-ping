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

package it.eng.sacerasi.web.helper;

import it.eng.paginator.util.HibernateUtils;
import it.eng.sacerasi.common.Constants;
import it.eng.sacerasi.common.Constants.TiAppart;
import it.eng.sacerasi.common.Constants.TipoPigVGetValAppart;
import it.eng.sacerasi.exception.ParamApplicNotFoundException;
import it.eng.sacerasi.web.helper.dto.PigVGetValParamDto;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringSubstitutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("unchecked")
@Stateless
@LocalBean
public class ConfigurationHelper {

    private static final String PIGVGETVALPARAMBYCOL = "PigVGetvalParamByCol";
    private static final String PIGVGETVALPARAMBY = "PigVGetvalParamBy";
    private static final String FLAPLPARAMAPPLICAPPART = "flPigParamApplicAppart";
    private static final String IDAPLVGETVALPARAMBY = "idPigVGetvalParamBy";

    // Parti comuni
    public static final String OBJECT_STORAGE_ADDR = "OBJECT_STORAGE_ADDR";
    public static final String TENANT_OBJECT_STORAGE = "TENANT_OBJECT_STORAGE";
    // Strumenti Urbanistici
    public static final String BUCKET_VERIFICA_STRUMENTI_URBANISTICI = "BUCKET_VERIFICA_STRUMENTI_URBANISTICI";
    public static final String BUCKET_STRUMENTI_URBANISTICI_TRASFORMATI = "BUCKET_STRUMENTI_URBANISTICI_TRASFORMATI";
    public static final String SU_S3_ACCESS_KEY_ID = "SU_S3_ACCESS_KEY_ID";
    public static final String SU_S3_SECRET_KEY = "SU_S3_SECRET_KEY";
    // Versamento Oggetto
    public static final String VO_S3_ACCESS_KEY_ID = "VO_S3_ACCESS_KEY_ID";
    public static final String VO_S3_SECRET_KEY = "VO_S3_SECRET_KEY";
    public static final String BUCKET_VERSAMENTO_OGGETTO = "BUCKET_VERSAMENTO_OGGETTO";
    // Sisma
    public static final String BUCKET_VERIFICA_SISMA = "BUCKET_VERIFICA_SISMA";
    public static final String BUCKET_SISMA_TRASFORMATI = "BUCKET_SISMA_TRASFORMATI";
    public static final String SISMA_S3_ACCESS_KEY_ID = "SISMA_S3_ACCESS_KEY_ID";
    public static final String SISMA_S3_SECRET_KEY = "SISMA_S3_SECRET_KEY";

    public static final String URL_ASSOCIAZIONE_UTENTE_CF = "URL_ASSOCIAZIONE_UTENTE_CF";
    public static final String URL_BACK_ASSOCIAZIONE_UTENTE_CF = "URL_BACK_ASSOCIAZIONE_UTENTE_CF";

    /**
     * Default constructor.
     */
    public ConfigurationHelper() {
        // Non utilizzato
    }

    private static final Logger log = LoggerFactory.getLogger(ConfigurationHelper.class.getName());
    @PersistenceContext(unitName = "SacerAsiJPA")
    private EntityManager entityManager;

    public Map<String, String> getConfiguration() {
        String queryStr = "SELECT paramApplic.nmParamApplic, valoreParamApplic.dsValoreParamApplic "
                + "FROM PigValoreParamApplic valoreParamApplic " + "JOIN valoreParamApplic.pigParamApplic paramApplic "
                + "WHERE valoreParamApplic.tiAppart = 'APPLIC' ";
        Query query = entityManager.createQuery(queryStr);

        List<Object[]> configurazioni = query.getResultList();
        Map<String, String> config = new HashMap<>();
        for (Object[] configurazione : configurazioni) {
            config.put((String) configurazione[0], (String) configurazione[1]);
        }
        return config;
    }

    /*
     * Restituisce il nome dell'applicazione configurato sulla tabella dei parametri
     */
    public String getParamApplicApplicationName() {
        String queryStr = "SELECT valoreParamApplic.dsValoreParamApplic "
                + "FROM PigValoreParamApplic valoreParamApplic " + "JOIN valoreParamApplic.pigParamApplic paramApplic "
                + "WHERE paramApplic.nmParamApplic = 'NM_APPLIC' ";
        javax.persistence.Query query = entityManager.createQuery(queryStr);
        List<String> paramList = query.getResultList();
        if (paramList != null && !paramList.isEmpty()) {
            return paramList.get(0);
        } else {
            return null;
        }
    }

    /**
     * Ottieni il valore del parametro indicato dal codice in input. Il valore viene ottenuto filtrando per tipologia
     * <em>APPLIC</em> {@link TipoPigVGetValAppart#APPLIC}
     *
     * @param nmParamApplic
     *            codice del parametro
     *
     * @return valore del parametro filtrato per tipologia <em>APPLIC</em> .
     */
    public String getValoreParamApplicByApplic(String nmParamApplic) {
        return getValoreParamApplic(nmParamApplic, BigDecimal.valueOf(Integer.MIN_VALUE),
                BigDecimal.valueOf(Integer.MIN_VALUE), BigDecimal.valueOf(Integer.MIN_VALUE),
                TipoPigVGetValAppart.APPLIC);
    }

    /**
     * Ottieni il valore del parametro indicato dal codice in input. Il valore viene ottenuto filtrando per tipologia
     * <em>STRUT</em> {@link TipoPigVGetValAppart#AMBIENTEVERS}
     *
     * @param nmParamApplic
     *            codice del parametro
     * @param idAmbienteVers
     *            id ambiente
     *
     *
     * @return valore del parametro filtrato per tipologia <em>AMBIENTEVERS</em> .
     */
    public String getValoreParamApplicByAmbienteVers(String nmParamApplic, BigDecimal idAmbienteVers) {
        return getValoreParamApplic(nmParamApplic, idAmbienteVers, BigDecimal.valueOf(Integer.MIN_VALUE),
                BigDecimal.valueOf(Integer.MIN_VALUE), TipoPigVGetValAppart.AMBIENTEVERS);
    }

    /**
     * Ottieni il valore del parametro indicato dal codice in input. Il valore viene ottenuto filtrando per tipologia
     * <em>STRUT</em> {@link TipoPigVGetValAppart#VERS}
     *
     * @param nmParamApplic
     *            codice del parametro
     * @param idAmbienteVers
     *            id ambiente
     * @param idVers
     *            id versatore
     *
     *
     * @return valore del parametro filtrato per tipologia <em>VERS</em> .
     */
    public String getValoreParamApplicByIdVers(String nmParamApplic, BigDecimal idAmbienteVers, BigDecimal idVers) {
        return getValoreParamApplic(nmParamApplic, idAmbienteVers, idVers, BigDecimal.valueOf(Integer.MIN_VALUE),
                TipoPigVGetValAppart.VERS);
    }

    /**
     * Ottieni il valore del parametro indicato dal codice in input. Il valore viene ottenuto filtrando per tipologia
     * <em>TIPOOBJECT</em> {@link TipoPigVGetValAppart#TIPOOBJECT}
     *
     * @param nmParamApplic
     *            codice del parametro
     * @param idAmbienteVers
     *            id ambiente
     * @param idVers
     *            id versatore
     * @param idTipoObject
     *            id tipo object
     *
     *
     * @return valore del parametro filtrato per tipologia <em>TIPOOBJECT</em> .
     */
    public String getValoreParamApplicByTipoObj(String nmParamApplic, BigDecimal idAmbienteVers, BigDecimal idVers,
            BigDecimal idTipoObject) {
        return getValoreParamApplic(nmParamApplic, idAmbienteVers, idVers, idTipoObject,
                TipoPigVGetValAppart.TIPOOBJECT);
    }

    /**
     *
     * @param nmParamApplic
     *            nome parametro
     * @param idAmbienteVers
     *            id ambiente
     * @param idVers
     *            id versamento
     * @param idTipoObject
     *            id tipo oggetto
     * @param tipoPigVGetValAppart
     *            tipo valore
     *
     * @return il valore del parametro
     */
    private String getValoreParamApplic(String nmParamApplic, BigDecimal idAmbienteVers, BigDecimal idVers,
            BigDecimal idTipoObject, TipoPigVGetValAppart tipoPigVGetValAppart) {

        long id = Integer.MIN_VALUE;// su questo id non troverò alcun elemento value sicuramente null
        List<PigVGetValParamDto> result = null;

        // base query (template)
        Map<String, String> queryData = new HashMap<>();
        String queryStr = null;

        // query template -> create DTO
        String queryStrTempl = "SELECT NEW it.eng.sacerasi.web.helper.dto.PigVGetValParamDto (${" + PIGVGETVALPARAMBYCOL
                + "}) " + "FROM PigParamApplic paramApplic, ${" + PIGVGETVALPARAMBY + "} getvalParam  "
                + "WHERE paramApplic.nmParamApplic = :nmParamApplic "
                + "AND getvalParam.nmParamApplic = paramApplic.nmParamApplic " + "AND paramApplic.${"
                + FLAPLPARAMAPPLICAPPART + "} = :flAppart ${" + IDAPLVGETVALPARAMBY + "} ";

        // tipo appartenenza
        TiAppart tiAppart = null;

        switch (tipoPigVGetValAppart) {
        case TIPOOBJECT:
            //
            id = idTipoObject != null ? idTipoObject.longValue() : Integer.MIN_VALUE;
            //
            tiAppart = TiAppart.TIPO_OGGETTO;
            //
            queryData.put(PIGVGETVALPARAMBYCOL, "getvalParam.dsValoreParamApplic, getvalParam.tiAppart");
            queryData.put(PIGVGETVALPARAMBY, "PigVGetvalParamByTiogg");
            queryData.put(FLAPLPARAMAPPLICAPPART, "flAppartTipoOggetto");
            queryData.put(IDAPLVGETVALPARAMBY, "AND getvalParam.idTipoObject = :id");
            // replace
            queryStr = StringSubstitutor.replace(queryStrTempl, queryData);
            break;
        case VERS:
            //
            id = idVers != null ? idVers.longValue() : Integer.MIN_VALUE;
            //
            tiAppart = TiAppart.VERS;
            //
            queryData.put(PIGVGETVALPARAMBYCOL, "getvalParam.dsValoreParamApplic, getvalParam.tiAppart");
            queryData.put(PIGVGETVALPARAMBY, "PigVGetvalParamByVer");
            queryData.put(FLAPLPARAMAPPLICAPPART, "flAppartVers");
            queryData.put(IDAPLVGETVALPARAMBY, "AND getvalParam.idVers = :id");
            // replace
            queryStr = StringSubstitutor.replace(queryStrTempl, queryData);
            break;
        case AMBIENTEVERS:
            //
            id = idAmbienteVers != null ? idAmbienteVers.longValue() : Integer.MIN_VALUE;
            //
            tiAppart = TiAppart.AMBIENTE;
            //
            queryData.put(PIGVGETVALPARAMBYCOL, "getvalParam.dsValoreParamApplic, getvalParam.tiAppart");
            queryData.put(PIGVGETVALPARAMBY, "PigVGetvalParamByAmb");
            queryData.put(FLAPLPARAMAPPLICAPPART, "flAppartAmbiente");
            queryData.put(IDAPLVGETVALPARAMBY, "AND getvalParam.idAmbienteVers = :id");
            // replace
            queryStr = StringSubstitutor.replace(queryStrTempl, queryData);
            break;
        default:
            //
            tiAppart = TiAppart.APPLIC;
            //
            queryData.put(PIGVGETVALPARAMBYCOL, "getvalParam.dsValoreParamApplic");
            queryData.put(PIGVGETVALPARAMBY, "PigVGetvalParamByApl");
            queryData.put(FLAPLPARAMAPPLICAPPART, "flAppartApplic");
            queryData.put(IDAPLVGETVALPARAMBY, "");
            // replace
            queryStr = StringSubstitutor.replace(queryStrTempl, queryData);
            break;
        }

        try {
            TypedQuery<PigVGetValParamDto> query = entityManager.createQuery(queryStr, PigVGetValParamDto.class);
            query.setParameter("nmParamApplic", nmParamApplic);
            query.setParameter("flAppart", "1");// fixed
            // solo nel caso in cui contenga la condition sull'ID
            if (StringUtils.isNotBlank(queryData.get(IDAPLVGETVALPARAMBY))) {
                query.setParameter("id", HibernateUtils.bigDecimalFrom(id));
            }
            // get result
            result = query.getResultList();
        } catch (Exception e) {
            // throws Exception
            final String msg = "Errore nella lettura del parametro " + nmParamApplic;
            log.error(msg);
            throw new ParamApplicNotFoundException(msg, nmParamApplic);
        }

        if (result != null && !result.isEmpty()) {
            /*
             * if more than one ....
             */
            if (result.size() > 1) {
                /*
                 * Ordine / Priorità TiAppart idAaTipoFascicolo -> idTipoUnitaDoc -> idStrut -> idAmbiente ->
                 * applicazione ======= Ordine / Priorità TiAppart idAaTipoFascicolo -> idTipoUnitaDoc -> idStrut ->
                 * idAmbiente -> applicazione
                 */
                // filter by getTiAppart
                return getDsValoreParamApplicByTiAppart(nmParamApplic, result, tiAppart);
            } else {
                return result.get(0).getDsValoreParamApplic(); // one is expected
            }
        } else if (Constants.TipoPigVGetValAppart.next(tipoPigVGetValAppart) != null) {
            /*
             * Ordine / Priorità Viste idTipoObject -> idVers -> -> idAmbienteVers -> applicazione
             */
            return getValoreParamApplic(nmParamApplic, idAmbienteVers, idVers, idTipoObject,
                    Constants.TipoPigVGetValAppart.next(tipoPigVGetValAppart));
        } else {
            // thorws Exception
            final String msg = String.format(
                    "Parametro %s non definito o non valorizzato: tipo %s idAmbienteVers %s idVers %s idTipoObject %s",
                    nmParamApplic, tipoPigVGetValAppart, idAmbienteVers, idVers, idTipoObject);
            log.error(msg);
            throw new ParamApplicNotFoundException(msg, nmParamApplic);
        }
    }

    private String getDsValoreParamApplicByTiAppart(String nmParamApplic, List<PigVGetValParamDto> result,
            final TiAppart tiAppart) {
        List<PigVGetValParamDto> resultFiltered = new ArrayList<>();
        for (PigVGetValParamDto valParam : result) {
            if (valParam.getTiAppart().equals(tiAppart.name())) {
                resultFiltered.add(valParam);
                break;
            }
        }

        /* questa condizione non dovrebbe mai verificarsi */
        if (tiAppart.name().equals(TiAppart.APPLIC.name()) && resultFiltered.isEmpty()) {
            // thorws Exception
            final String msg = "Parametro " + nmParamApplic + " non definito o non valorizzato";
            log.error(msg);
            throw new ParamApplicNotFoundException(msg, nmParamApplic);
        }

        if (resultFiltered.isEmpty()) {
            TiAppart nextTiAppart = null;
            switch (tiAppart) {
            case TIPO_OGGETTO:
                nextTiAppart = TiAppart.VERS;
                break;
            case VERS:
                nextTiAppart = TiAppart.AMBIENTE;
                break;
            default:
                nextTiAppart = TiAppart.APPLIC;
                break;
            }
            return getDsValoreParamApplicByTiAppart(nmParamApplic, result, nextTiAppart);
        } else {
            return resultFiltered.get(0).getDsValoreParamApplic();// expected one
        }
    }

    /**
     * Ottieni la system property contentente la <em>access_key_id</em> S3 per accedere ai bucket degli Strumenti
     * urbanistici.
     *
     * @return access key id per s3 (SU)
     */
    public String getSUAccessKeyIdystemProp() {
        return getValoreParamApplic(SU_S3_ACCESS_KEY_ID, null, null, null, Constants.TipoPigVGetValAppart.APPLIC);
    }

    /**
     * Ottieni la system property contentente la <em>secret_key</em> S3 per accedere ai bucket degli Strumenti
     * urbanistici.
     *
     * @return secret key per s3 (SU)
     */
    public String getSUSecretKeyIdSystemProp() {
        return getValoreParamApplic(SU_S3_SECRET_KEY, null, null, null, Constants.TipoPigVGetValAppart.APPLIC);
    }

    /**
     * Ottieni la system property contentente la <em>access_key_id</em> S3 per accedere ai bucket degli Strumenti
     * urbanistici.
     *
     * @return access key id per s3 (SU)
     */
    public String getVersamentoOggettoAccessKeyIdystemProp() {
        return getValoreParamApplic(VO_S3_ACCESS_KEY_ID, null, null, null, Constants.TipoPigVGetValAppart.APPLIC);
    }

    /**
     * Ottieni la system property contentente la <em>secret_key</em> S3 per accedere ai bucket degli Strumenti
     * urbanistici.
     *
     * @return secret key per s3 (SU)
     */
    public String getVersamentoOggettoSecretKeyIdSystemProp() {
        return getValoreParamApplic(VO_S3_SECRET_KEY, null, null, null, Constants.TipoPigVGetValAppart.APPLIC);
    }

    /**
     * MEV24582 Nome della system property che regola la presenza o meno delle funzionalità basate su object storage
     *
     * @return valore bucket versamento oggetto
     */
    public String getValoreBucketVersamentoOggetto() {
        return getValoreParamApplic(BUCKET_VERSAMENTO_OGGETTO, null, null, null, Constants.TipoPigVGetValAppart.APPLIC);
    }

    public String getSismaAccessKeyIdystemProp() {
        return getValoreParamApplic(SISMA_S3_ACCESS_KEY_ID, null, null, null, Constants.TipoPigVGetValAppart.APPLIC);
    }

    public String getSismaSecretKeyIdSystemProp() {
        return getValoreParamApplic(SISMA_S3_SECRET_KEY, null, null, null, Constants.TipoPigVGetValAppart.APPLIC);
    }

    public String getUrlAssociazioneUtenteCf() {
        return getValoreParamApplic(URL_ASSOCIAZIONE_UTENTE_CF, null, null, null,
                Constants.TipoPigVGetValAppart.APPLIC);
    }

    public String getUrlBackAssociazioneUtenteCf() {
        return getValoreParamApplic(URL_BACK_ASSOCIAZIONE_UTENTE_CF, null, null, null,
                Constants.TipoPigVGetValAppart.APPLIC);
    }

}
