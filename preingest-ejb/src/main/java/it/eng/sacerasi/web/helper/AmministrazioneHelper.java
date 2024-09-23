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

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.paginator.util.HibernateUtils;
import it.eng.sacerasi.common.Constants;
import it.eng.sacerasi.entity.IamEnteSiamDaAllinea;
import it.eng.sacerasi.entity.IamOrganizDaReplic;
import it.eng.sacerasi.entity.PigAmbienteVers;
import it.eng.sacerasi.entity.PigAttribDatiSpec;
import it.eng.sacerasi.entity.PigDichVersSacer;
import it.eng.sacerasi.entity.PigDichVersSacerTipoObj;
import it.eng.sacerasi.entity.PigFileObject;
import it.eng.sacerasi.entity.PigInfoDicom;
import it.eng.sacerasi.entity.PigObject;
import it.eng.sacerasi.entity.PigObjectTrasf;
import it.eng.sacerasi.entity.PigParamApplic;
import it.eng.sacerasi.entity.PigSopClassDicom;
import it.eng.sacerasi.entity.PigSopClassDicomVers;
import it.eng.sacerasi.entity.PigStatoObject;
import it.eng.sacerasi.entity.PigStoricoVersAmbiente;
import it.eng.sacerasi.entity.PigTipoFileObject;
import it.eng.sacerasi.entity.PigTipoObject;
import it.eng.sacerasi.entity.PigValoreParamApplic;
import it.eng.sacerasi.entity.PigValoreSetParamTrasf;
import it.eng.sacerasi.entity.PigVers;
import it.eng.sacerasi.entity.PigVersTipoObjectDaTrasf;
import it.eng.sacerasi.entity.PigXsdDatiSpec;
import it.eng.sacerasi.entity.XfoTrasf;
import it.eng.sacerasi.exception.IncoherenceException;
import it.eng.sacerasi.grantEntity.OrgAppartCollegEnti;
import it.eng.sacerasi.grantEntity.OrgVRicEnteConvenzByEsterno;
import it.eng.sacerasi.grantEntity.OrgVRicEnteNonConvenz;
import it.eng.sacerasi.grantEntity.SIOrgAccordoEnte;
import it.eng.sacerasi.grantEntity.SIOrgAmbienteEnteConvenz;
import it.eng.sacerasi.grantEntity.SIOrgEnteConvenzOrg;
import it.eng.sacerasi.grantEntity.SIOrgEnteSiam;
import it.eng.sacerasi.grantEntity.SIUsrOrganizIam;
import it.eng.sacerasi.grantEntity.UsrUser;
import it.eng.sacerasi.grantEntity.UsrVAbilAmbEnteConvenz;
import it.eng.sacerasi.helper.GenericHelper;
import it.eng.sacerasi.job.allineamentoEntiConvenzionati.util.CostantiAllineaEntiConv;
import it.eng.sacerasi.slite.gen.tablebean.PigTipoObjectRowBean;
import it.eng.sacerasi.slite.gen.tablebean.PigVersRowBean;
import it.eng.sacerasi.viewEntity.PigVRicVers;
import it.eng.sacerasi.viewEntity.PigVValParamTrasfDefSpec;
import it.eng.sacerasi.viewEntity.PigVValoreParamTrasf;
import it.eng.sacerasi.viewEntity.PigVValoreSetParamTrasf;
import it.eng.sacerasi.viewEntity.UsrVAbilAmbXver;
import it.eng.sacerasi.viewEntity.UsrVAbilStrutSacerXping;
import it.eng.sacerasi.viewEntity.UsrVChkCreaAmbSacer;

@SuppressWarnings("unchecked")
@Stateless
@LocalBean
public class AmministrazioneHelper extends GenericHelper {

    public AmministrazioneHelper() {
        /**
         *
         */
    }

    private static final Logger log = LoggerFactory.getLogger(AmministrazioneHelper.class);

    public void updatePig(Object o) {
        o = getEntityManager().merge(o);
        getEntityManager().flush();
        getEntityManager().refresh(o);
    }

    /*
     * AMBIENTI
     */
    public List<PigAmbienteVers> getPigAmbienteVersAbilitatiList(String nmAmbienteVers, Long idUtente,
            String nmApplic) {

        StringBuilder queryStr = new StringBuilder("SELECT DISTINCT vers.pigAmbienteVer "
                + "FROM UsrVAbilOrganiz abilOrganiz, PigVers vers " + "WHERE abilOrganiz.idOrganizApplic = vers.idVers "
                + "AND abilOrganiz.id.idUserIam = :idUtente " + "AND abilOrganiz.nmApplic = :nmApplic ");

        if (nmAmbienteVers != null) {
            queryStr.append("AND UPPER(vers.pigAmbienteVer.nmAmbienteVers) LIKE :ambiente ESCAPE :char ");
        }
        // creazione query dalla stringa
        Query query = getEntityManager().createQuery(queryStr.toString());
        query.setParameter("idUtente", HibernateUtils.bigDecimalFrom(idUtente));
        query.setParameter("nmApplic", nmApplic);
        if (nmAmbienteVers != null) {
            query.setParameter("ambiente", "%" + nmAmbienteVers.toUpperCase() + "%");
            query.setParameter("char", '\\');
        }
        return query.getResultList();
    }

    // MEV25815 - trova anche gli ambienti senza alcun versatore associato.
    public List<PigAmbienteVers> getPigAmbienteVersList(String nmAmbienteVers, Long idUtente, String nmApplic) {
        StringBuilder queryStr = new StringBuilder("SELECT DISTINCT vers.pigAmbienteVer "
                + "FROM UsrVAbilOrganiz abilOrganiz, PigVers vers " + "WHERE abilOrganiz.idOrganizApplic = vers.idVers "
                + "AND abilOrganiz.id.idUserIam = :idUtente " + "AND abilOrganiz.nmApplic = :nmApplic ");

        if (nmAmbienteVers != null) {
            queryStr.append("AND UPPER(vers.pigAmbienteVer.nmAmbienteVers) LIKE :ambiente ESCAPE :char ");
        }
        // creazione query dalla stringa
        Query query = getEntityManager().createQuery(queryStr.toString());
        query.setParameter("idUtente", HibernateUtils.bigDecimalFrom(idUtente));
        query.setParameter("nmApplic", nmApplic);
        if (nmAmbienteVers != null) {
            query.setParameter("ambiente", "%" + nmAmbienteVers.toUpperCase() + "%");
            query.setParameter("char", '\\');
        }
        List<PigAmbienteVers> list = query.getResultList();

        queryStr = new StringBuilder("SELECT DISTINCT amb FROM PigAmbienteVers amb WHERE amb.pigVers IS EMPTY");

        if (nmAmbienteVers != null) {
            queryStr.append(" AND UPPER(amb.nmAmbienteVers) LIKE :ambiente ESCAPE :char ");
        }

        query = getEntityManager().createQuery(queryStr.toString());
        if (nmAmbienteVers != null) {
            query.setParameter("ambiente", "%" + nmAmbienteVers.toUpperCase() + "%");
            query.setParameter("char", '\\');
        }

        List<PigAmbienteVers> emptyPigAmbienteVers = query.getResultList();

        list.addAll(emptyPigAmbienteVers);

        return list;
    }

    public PigAmbienteVers getPigAmbienteVersById(BigDecimal idAmbienteVers) {
        String queryStr = "SELECT amb FROM PigAmbienteVers amb  WHERE amb.idAmbienteVers= :idAmb";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idAmb", HibernateUtils.longFrom(idAmbienteVers));
        List<PigAmbienteVers> list = query.getResultList();
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    public PigAmbienteVers getPigAmbienteVersByName(String nmAmbienteVers) {
        String queryStr = "SELECT amb FROM PigAmbienteVers amb  WHERE amb.nmAmbienteVers= :nmAmbienteVers";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("nmAmbienteVers", nmAmbienteVers);
        List<PigAmbienteVers> list = query.getResultList();
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    public List<PigVers> getPigVersList(BigDecimal idAmbienteVers) {
        StringBuilder queryStr = new StringBuilder("SELECT vers FROM PigVers vers ");
        if (idAmbienteVers != null) {
            queryStr.append("WHERE vers.pigAmbienteVer.idAmbienteVers = :idAmbienteVers");
        }
        Query query = getEntityManager().createQuery(queryStr.toString());
        if (idAmbienteVers != null) {
            query.setParameter("idAmbienteVers", HibernateUtils.longFrom(idAmbienteVers));
        }
        return query.getResultList();
    }

    public boolean existUtentiUnAmbiente(BigDecimal idAmbienteVers, String nmApplic) {
        String queryStr = "SELECT organizNolastLiv FROM UsrVAbilOrganizNolastLiv organizNoLastliv "
                + "WHERE organizNoLastliv.idOrganizApplic = :idAmbienteVers "
                + "AND organizNoLastliv.nmTipoOrganiz = 'AMBIENTE' " + "AND organizNoLastliv.nmApplic = :nmApplic "
                + "AND EXISTS (SELECT user FROM UsrUser user WHERE user.flAttivo = '1' )";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idAmbienteVers", idAmbienteVers);
        query.setParameter("nmApplic", nmApplic);
        return !query.getResultList().isEmpty();
    }

    /*
     * VERSATORI
     */
    public boolean existPigVersValidiDataOdierna(BigDecimal idAmbienteVers) {
        StringBuilder queryStr = new StringBuilder("SELECT vers FROM PigVers vers ");
        String whereWord = " WHERE ";
        if (idAmbienteVers != null) {
            queryStr.append(whereWord).append("vers.pigAmbienteVer.idAmbienteVers = :idAmbienteVers ");
            whereWord = " AND ";
        }
        queryStr.append(whereWord).append(":dtOdierna BETWEEN vers.dtIniValVers AND vers.dtFineValVers ");

        Query query = getEntityManager().createQuery(queryStr.toString());
        if (idAmbienteVers != null) {
            query.setParameter("idAmbienteVers", HibernateUtils.longFrom(idAmbienteVers));
        }
        query.setParameter("dtOdierna", new Date());
        return !query.getResultList().isEmpty();
    }

    public List<PigVers> getPigVersListFromKey(String nmVers, BigDecimal idAmbienteVers) {
        String queryStr = "SELECT vers FROM PigVers vers WHERE vers.nmVers = :nmVers AND vers.pigAmbienteVer.idAmbienteVers = :idAmbienteVers";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("nmVers", nmVers);
        query.setParameter("idAmbienteVers", HibernateUtils.longFrom(idAmbienteVers));
        return query.getResultList();
    }

    public List<PigVers> getPigVersListFromCombo(PigVers vers, Long idUserIam) {

        StringBuilder queryStr = new StringBuilder("SELECT vers " + "FROM IamAbilOrganiz iao, PigVers vers "
                + "WHERE iao.idOrganizApplic = vers.idVers " + "AND iao.iamUser.idUserIam = :idUserIam ");

        String whereWord = "AND ";
        if (vers.getNmVers() != null) {
            queryStr.append(whereWord);
            queryStr.append(" UPPER (vers.nmVers) LIKE :nmVers ESCAPE :char");
            whereWord = " AND ";
        }
        if (vers.getPigAmbienteVer() != null && vers.getPigAmbienteVer().getNmAmbienteVers() != null) {
            queryStr.append(whereWord);
            queryStr.append(" UPPER (vers.pigAmbienteVer.nmAmbienteVers) = :nmAmbienteVers ");
        }
        queryStr.append(" ORDER BY vers.pigAmbienteVer.nmAmbienteVers, vers.nmVers ");
        Query query = getEntityManager().createQuery(queryStr.toString());
        if (vers.getNmVers() != null) {
            query.setParameter("nmVers", "%" + vers.getNmVers().toUpperCase() + "%");
            query.setParameter("char", '\\');
        }
        if (vers.getPigAmbienteVer() != null && vers.getPigAmbienteVer().getNmAmbienteVers() != null) {
            query.setParameter("nmAmbienteVers", vers.getPigAmbienteVer().getNmAmbienteVers().toUpperCase());
        }
        query.setParameter("idUserIam", idUserIam);
        return query.getResultList();
    }

    public List<PigVRicVers> getPigVRicVersList(Long idVers, String nmVers, String nmAmbienteVers,
            String nmAmbienteSacer, String nmEnteSacer, String nmStrutSacer, String nmUseridSacer,
            String nmAmbienteEnteConvenz, String nmEnteConvenz, Long idUserIam, String nmTipoVersatore) {

        StringBuilder queryStr = new StringBuilder(
                "SELECT DISTINCT new it.eng.sacerasi.viewEntity.PigVRicVers(ricVers.idAmbienteVers, "
                        + "ricVers.nmAmbienteVers, ricVers.id.idVers, ricVers.nmVers, ricVers.dsListaDichStrutSacer, "
                        + "ricVers.nmAmbienteEnteConvenz, ricVers.nmEnteConvenz, ricVers.nmTipoVersatore ) "
                        + "FROM IamAbilOrganiz iao, PigVRicVers ricVers "
                        + "WHERE iao.idOrganizApplic = ricVers.id.idVers " + "AND iao.iamUser.idUserIam = :idUserIam ");

        String whereWord = "AND ";
        if (idVers != null) {
            queryStr.append(whereWord);
            queryStr.append(" ricVers.id.idVers = :idVers ");
            whereWord = " AND ";
        }
        if (nmVers != null) {
            queryStr.append(whereWord);
            queryStr.append(" UPPER (ricVers.nmVers) LIKE :nmVers ");
            whereWord = " AND ";
        }
        if (nmAmbienteVers != null) {
            queryStr.append(whereWord);
            queryStr.append(" ricVers.nmAmbienteVers = :nmAmbienteVers");
            whereWord = " AND ";
        }
        if (StringUtils.isNotBlank(nmAmbienteSacer)) {
            queryStr.append(whereWord);
            queryStr.append(" ricVers.nmOrganizIamAmbiente = :nmOrganizIamAmbiente ");
            whereWord = " AND ";
        }
        if (StringUtils.isNotBlank(nmEnteSacer)) {
            queryStr.append(whereWord);
            queryStr.append(" ricVers.nmOrganizIamEnte = :nmOrganizIamEnte");
            whereWord = " AND ";
        }
        if (StringUtils.isNotBlank(nmStrutSacer)) {
            queryStr.append(whereWord);
            queryStr.append(" ricVers.nmOrganizIamStrut = :nmOrganizIamStrut");
            whereWord = " AND ";
        }
        if (StringUtils.isNotBlank(nmTipoVersatore)) {
            queryStr.append(whereWord);
            queryStr.append(" ricVers.nmTipoVersatore = :nmTipoVersatore");
            whereWord = " AND ";
        }

        // MEV 27543
        whereWord = " AND ";
        if (StringUtils.isNotBlank(nmAmbienteEnteConvenz)) {
            queryStr.append(whereWord);
            queryStr.append(" ricVers.nmAmbienteEnteConvenz = :nmAmbienteEnteConvenz");
        }
        if (StringUtils.isNotBlank(nmEnteConvenz)) {
            queryStr.append(whereWord);
            queryStr.append(" ricVers.nmEnteConvenz = :nmEnteConvenz");
        }

        if (nmUseridSacer != null) {
            queryStr.append(whereWord);
            queryStr.append(" ricVers.nmUseridSacer = :nmUseridSacer ");
        }
        queryStr.append(" ORDER BY ricVers.nmAmbienteVers, ricVers.nmVers ");
        Query query = getEntityManager().createQuery(queryStr.toString());

        if (idVers != null) {
            query.setParameter("idVers", HibernateUtils.bigDecimalFrom(idVers));
        }
        if (nmVers != null) {
            query.setParameter("nmVers", "%" + nmVers.toUpperCase() + "%");
        }
        if (nmAmbienteVers != null) {
            query.setParameter("nmAmbienteVers", nmAmbienteVers);
        }
        if (StringUtils.isNotBlank(nmAmbienteSacer)) {
            query.setParameter("nmOrganizIamAmbiente", nmAmbienteSacer);
        }
        if (StringUtils.isNotBlank(nmEnteSacer)) {
            query.setParameter("nmOrganizIamEnte", nmEnteSacer);
        }
        if (StringUtils.isNotBlank(nmStrutSacer)) {
            query.setParameter("nmOrganizIamStrut", nmStrutSacer);
        }
        if (StringUtils.isNotBlank(nmTipoVersatore)) {
            query.setParameter("nmTipoVersatore", nmTipoVersatore);
        }
        if (nmUseridSacer != null) {
            query.setParameter("nmUseridSacer", nmUseridSacer);
        }

        // MEV 27543
        if (StringUtils.isNotBlank(nmAmbienteEnteConvenz)) {
            query.setParameter("nmAmbienteEnteConvenz", nmAmbienteEnteConvenz);
        }
        if (StringUtils.isNotBlank(nmEnteConvenz)) {
            query.setParameter("nmEnteConvenz", nmEnteConvenz);
        }

        query.setParameter("idUserIam", idUserIam);
        return query.getResultList();
    }

    // MEV 27543
    public List<String> getPigVRicVersAmbientiIam(Long idUserIam) {
        String queryStr = "SELECT DISTINCT b.nmAmbienteEnteConvenz FROM SIOrgEnteSiam a JOIN SIOrgAmbienteEnteConvenz b ON (a.siOrgAmbienteEnteConvenz.idAmbienteEnteConvenz = b.idAmbienteEnteConvenz)"
                + " where a.nmEnteSiam IN( SELECT DISTINCT ricVers.nmEnteConvenz FROM IamAbilOrganiz iao, PigVRicVers ricVers"
                + " WHERE iao.idOrganizApplic = ricVers.id.idVers" + " AND iao.iamUser.idUserIam = :idUserIam)"
                + " ORDER BY b.nmAmbienteEnteConvenz";

        Query query = getEntityManager().createQuery(queryStr);

        query.setParameter("idUserIam", HibernateUtils.longFrom(idUserIam));
        return query.getResultList();
    }

    // MEV 27543
    public List<String> getPigVRicVersEntiIam(String nmAmbienteEnteConvenz, Long idUserIam) {
        StringBuilder queryStr = new StringBuilder(
                "SELECT DISTINCT ricVers.nmEnteConvenz " + "FROM IamAbilOrganiz iao, PigVRicVers ricVers "
                        + "WHERE iao.idOrganizApplic = ricVers.id.idVers " + "AND iao.iamUser.idUserIam = :idUserIam "
                        + "AND ricVers.nmAmbienteEnteConvenz = :nmAmbienteEnteConvenz");

        queryStr.append(" ORDER BY ricVers.nmEnteConvenz ");
        Query query = getEntityManager().createQuery(queryStr.toString());

        query.setParameter("idUserIam", HibernateUtils.longFrom(idUserIam));
        query.setParameter("nmAmbienteEnteConvenz", nmAmbienteEnteConvenz);
        return query.getResultList();
    }

    public PigVers getPigVersByName(String nmVers, BigDecimal idAmb) {
        StringBuilder queryStr = new StringBuilder("SELECT vers  FROM PigVers vers  WHERE vers.nmVers= :nmVers ");
        if (idAmb != null) {
            queryStr.append(" AND vers.pigAmbienteVer.idAmbienteVers=:idAmbienteVers");
        }
        Query query = getEntityManager().createQuery(queryStr.toString());
        query.setParameter("nmVers", nmVers);
        if (idAmb != null) {
            query.setParameter("idAmbienteVers", HibernateUtils.longFrom(idAmb));
        }
        List<PigVers> list = query.getResultList();
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    public PigVers getPigVersById(BigDecimal idVers) {
        Query query = getEntityManager().createQuery("SELECT vers FROM PigVers vers  WHERE vers.idVers= :idVers");
        query.setParameter("idVers", HibernateUtils.longFrom(idVers));
        List<PigVers> list = query.getResultList();
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    // MEV25814
    public boolean existsPigTipoFileObjectByTipoObjectAndName(BigDecimal idTipoObj, String pigTipoFileName) {
        StringBuilder queryStr = new StringBuilder("SELECT tipoFileObj FROM PigTipoFileObject tipoFileObj ");
        queryStr.append(
                "WHERE tipoFileObj.pigTipoObject.idTipoObject = :idTipoObj AND tipoFileObj.nmTipoFileObject = :pigTipoFileName");
        Query query = getEntityManager().createQuery(queryStr.toString());
        query.setParameter("idTipoObj", HibernateUtils.longFrom(idTipoObj));
        query.setParameter("pigTipoFileName", pigTipoFileName);

        return !query.getResultList().isEmpty();
    }

    /**
     * @deprecated
     *
     * @param idVersDaTrasf
     *            versatore da
     * @param idVersTrasf
     *            versatore
     *
     * @return vero se esiste, falso altrimenti
     */
    @Deprecated
    public boolean existPigVersDaTrasfTrasf(BigDecimal idVersDaTrasf, BigDecimal idVersTrasf) {
        Query query = getEntityManager().createQuery(
                "SELECT COUNT(p) FROM PigVersDaTrasfTrasf p WHERE p.pigVersDaTrasf.idVers = :idVersDaTrasf AND p.pigVersTrasf.idVers = :idVersTrasf");
        query.setParameter("idVersDaTrasf", idVersDaTrasf);
        query.setParameter("idVersTrasf", idVersTrasf);
        Long count = (Long) query.getSingleResult();
        return count > 0L;
    }

    /**
     * Ritorna la lista dei versatori per cui generare oggetti, escludendo il versatore del parametro dato in input (che
     * è chi genera) e le sue associazioni
     *
     * @deprecated PigVersDaTrasfTrasf is not mapped
     *
     * @param idAmbienteVers
     *            id ambiente
     * @param idVers
     *            id versamento
     *
     * @return lista elementi di tipo {@link PigVers}
     */
    @Deprecated
    public List<PigVers> getPigVersTrasfCombo(BigDecimal idAmbienteVers, BigDecimal idVers) {
        StringBuilder queryStr = new StringBuilder("SELECT vers FROM PigVers vers WHERE ");
        if (idAmbienteVers != null) {
            queryStr.append("vers.pigAmbienteVer.idAmbienteVers = :idAmbienteVers").append(" AND ");
        }
        queryStr.append(
                "(vers.idVers != :idVers AND NOT EXISTS( SELECT versTrasf FROM PigVersDaTrasfTrasf versTrasf WHERE versTrasf.pigVersDaTrasf.idVers = :idVers AND versTrasf.pigVersTrasf.idVers = vers.idVers )) ORDER BY vers.nmVers");
        Query query = getEntityManager().createQuery(queryStr.toString());
        if (idAmbienteVers != null) {
            query.setParameter("idAmbienteVers", idAmbienteVers);
        }
        query.setParameter("idVers", idVers);
        return query.getResultList();
    }

    public List<PigVers> getPigVersAbilitatiList(List<BigDecimal> idVersList, Long idUserIam) {
        StringBuilder queryStr = new StringBuilder("SELECT vers FROM IamAbilOrganiz iao, PigVers vers "
                + "WHERE iao.idOrganizApplic = vers.idVers " + "AND iao.iamUser.idUserIam = :idUserIam ");
        if (idVersList != null && !idVersList.isEmpty()) {
            queryStr.append("AND vers.idVers NOT IN (:idVersList) ");
        }
        queryStr.append("ORDER BY vers.pigAmbienteVer.nmAmbienteVers, vers.nmVers ");
        Query query = getEntityManager().createQuery(queryStr.toString());
        query.setParameter("idUserIam", idUserIam);
        if (idVersList != null && !idVersList.isEmpty()) {
            query.setParameter("idVersList", HibernateUtils.longListFrom(idVersList));
        }
        return query.getResultList();
    }

    public boolean existUtentiUnVersatore(BigDecimal idVers, String nmApplic) {
        String queryStr = "SELECT organiz FROM UsrVAbilOrganiz organiz " + "WHERE organiz.idOrganizApplic = :idVers "
                + "AND organiz.nmTipoOrganiz = 'VERSATORE' " + "AND organiz.nmApplic = :nmApplic "
                + "AND EXISTS (SELECT user FROM UsrUser user WHERE user.flAttivo = '1' )";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idVers", idVers);
        query.setParameter("nmApplic", nmApplic);
        return !query.getResultList().isEmpty();
    }

    /*
     * TIPO OBJ
     *
     */
    public List<PigTipoObject> getPigTipoObjectList(BigDecimal idVers) {
        StringBuilder queryStr = new StringBuilder("SELECT tipoObject FROM PigTipoObject tipoObject ");
        if (idVers != null) {
            queryStr.append("WHERE tipoObject.pigVer.idVers = :idVers");
        }
        queryStr.append(" ORDER BY tipoObject.nmTipoObject ");

        Query query = getEntityManager().createQuery(queryStr.toString());
        if (idVers != null) {
            query.setParameter("idVers", HibernateUtils.longFrom(idVers));
        }
        return query.getResultList();
    }

    public PigTipoObject getPigTipoObjectByName(String nmTipoObj, BigDecimal idVers) {
        StringBuilder queryStr = new StringBuilder(
                "SELECT tipoObj  FROM PigTipoObject tipoObj  WHERE tipoObj.nmTipoObject= :nmTipoObj");
        if (idVers != null) {
            queryStr.append(" AND tipoObj.pigVer.idVers=:idVers");
        }
        Query query = getEntityManager().createQuery(queryStr.toString());
        query.setParameter("nmTipoObj", nmTipoObj);
        if (idVers != null) {
            query.setParameter("idVers", HibernateUtils.longFrom(idVers));
        }
        List<PigTipoObject> list = query.getResultList();
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    public PigTipoObject getPigTipoObjectById(BigDecimal idTipoObj) {
        String queryStr = "SELECT tipoObj FROM PigTipoObject tipoObj  WHERE tipoObj.idTipoObject= :idTipoObj ";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idTipoObj", HibernateUtils.longFrom(idTipoObj));
        List<PigTipoObject> list = query.getResultList();
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    public List<PigTipoObject> getPigTipoObjectNoDaTrasfAbilitatiList(BigDecimal idVers, long idUtente) {
        String queryStr = "SELECT tipoObject FROM PigTipoObject tipoObject JOIN tipoObject.pigVer vers, IamAbilTipoDato iatd "
                + "WHERE iatd.idTipoDatoApplic = tipoObject.idTipoObject "
                + "AND iatd.nmClasseTipoDato = 'TIPO_OBJECT' "
                + "AND iatd.iamAbilOrganiz.iamUser.idUserIam = :idUtente " + "AND vers.idVers = :idVers "
                + "AND tipoObject.tiVersFile IN ('ZIP_CON_XML_SACER', 'ZIP_NO_XML_SACER', 'NO_ZIP') "
                + "ORDER BY tipoObject.nmTipoObject ";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idUtente", idUtente);
        query.setParameter("idVers", HibernateUtils.longFrom(idVers));
        return query.getResultList();
    }

    /*
     * CORRISPONDENZE IN SACER
     */
    public List<Object[]> getPigDichVersSacerList(BigDecimal idVers) {
        StringBuilder queryStr = new StringBuilder(
                "SELECT dichVersSacer, treeOrganizIam.dlCompositoOrganiz FROM PigDichVersSacer dichVersSacer, UsrVTreeOrganizIam treeOrganizIam "
                        + "WHERE dichVersSacer.idOrganizIam = treeOrganizIam.idOrganizIam ");
        if (idVers != null) {
            queryStr.append("AND dichVersSacer.pigVer.idVers = :idVers");
        }
        queryStr.append(" ORDER BY treeOrganizIam.dlCompositoOrganiz ");

        Query query = getEntityManager().createQuery(queryStr.toString());
        if (idVers != null) {
            query.setParameter("idVers", HibernateUtils.longFrom(idVers));
        }
        return query.getResultList();
    }

    public PigDichVersSacer getPigDichVersSacer(BigDecimal idVers, BigDecimal idOrganizIam) {
        String queryStr = "SELECT dichVersSacer FROM PigDichVersSacer dichVersSacer "
                + "WHERE dichVersSacer.pigVer.idVers = :idVers ";

        if (idOrganizIam != null) {
            queryStr = queryStr + "AND dichVersSacer.idOrganizIam = :idOrganizIam ";
        }

        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idVers", HibernateUtils.longFrom(idVers));
        if (idOrganizIam != null) {
            query.setParameter("idOrganizIam", idOrganizIam);
        }
        List<PigDichVersSacer> list = query.getResultList();
        if (!list.isEmpty()) {
            return list.get(0);
        }
        return null;
    }

    public PigDichVersSacerTipoObj getPigDichVersSacerTipoObj(BigDecimal idTipoObject, BigDecimal idOrganizIam) {
        String queryStr = "SELECT dichVersSacer FROM PigDichVersSacerTipoObj dichVersSacer "
                + "WHERE dichVersSacer.pigTipoObject.idTipoObject = :idTipoObject ";

        if (idOrganizIam != null) {
            queryStr = queryStr + "AND dichVersSacer.idOrganizIam = :idOrganizIam ";
        }

        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idTipoObject", HibernateUtils.longFrom(idTipoObject));
        if (idOrganizIam != null) {
            query.setParameter("idOrganizIam", idOrganizIam);
        }
        List<PigDichVersSacerTipoObj> list = query.getResultList();
        if (!list.isEmpty()) {
            return list.get(0);
        }
        return null;
    }

    /*
     * PIG OBJ
     */
    public List<PigObject> getPigObjectListByTipoObj(BigDecimal idTipoObj) {
        StringBuilder queryStr = new StringBuilder("SELECT obj FROM PigObject obj ");
        if (idTipoObj != null) {
            queryStr.append("WHERE obj.pigTipoObject.idTipoObject = :idTipoObj");
        }
        Query query = getEntityManager().createQuery(queryStr.toString());
        if (idTipoObj != null) {
            query.setParameter("idTipoObj", HibernateUtils.longFrom(idTipoObj));
        }
        return query.getResultList();
    }

    // verifica se esiste almeno un oggetto di un determinato tipo
    public boolean esisteOggettoPerIdTipo(BigDecimal idTipoOggetto) {
        boolean esiste = false;
        try {
            Query query = getEntityManager().createNamedQuery("PigObject.findByIdTipoOggetto", PigObject.class);
            query.setMaxResults(1);
            query.setParameter("idTipoOggetto", HibernateUtils.longFrom(idTipoOggetto));
            List<PigObject> l = query.getResultList();
            if (l != null && !l.isEmpty()) {
                esiste = true;
            }
        } catch (RuntimeException ex) {
            log.error("Errore nell'estrazione di PigObject per idTipoOggetto [{" + idTipoOggetto + "}]", ex);
            throw ex;
        }
        return esiste;
    }

    /*
     * PIG TIPO FILE OBJ
     */
    public List<PigTipoFileObject> getPigTipoFileObjectList(BigDecimal idtipoObject) {
        StringBuilder queryStr = new StringBuilder("SELECT tipoFileObject FROM PigTipoFileObject tipoFileObject ");
        if (idtipoObject != null) {
            queryStr.append("WHERE tipoFileObject.pigTipoObject.idTipoObject = :idTipoObject");
        }
        Query query = getEntityManager().createQuery(queryStr.toString());
        if (idtipoObject != null) {
            query.setParameter("idTipoObject", HibernateUtils.longFrom(idtipoObject));
        }
        return query.getResultList();
    }

    public PigTipoFileObject getPigTipoFileObjectByName(String nmTipoFileObj, BigDecimal idTipoObj) {
        StringBuilder queryStr = new StringBuilder(
                "SELECT tipoFileObj  FROM PigTipoFileObject tipoFileObj  WHERE tipoFileObj.nmTipoFileObject= :nmTipoFileObj");
        if (idTipoObj != null) {
            queryStr.append(" AND tipoFileObj.pigTipoObject.idTipoObject=:idTipoObject");
        }
        Query query = getEntityManager().createQuery(queryStr.toString());
        query.setParameter("nmTipoFileObj", nmTipoFileObj);
        if (idTipoObj != null) {
            query.setParameter("idTipoObject", HibernateUtils.longFrom(idTipoObj));
        }
        List<PigTipoFileObject> list = query.getResultList();
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    public PigTipoFileObject getPigTipoFileObjectById(BigDecimal idTipoFileObj) {
        String queryStr = "SELECT tipoFileObj FROM PigTipoFileObject tipoFileObj  WHERE tipoFileObj.idTipoFileObject= :idTipoFileObj";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idTipoFileObj", HibernateUtils.longFrom(idTipoFileObj));
        List<PigTipoFileObject> list = query.getResultList();
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    public List<PigFileObject> getPigFileObjectListByTipoFileObject(BigDecimal idTipoFileObj) {
        StringBuilder queryStr = new StringBuilder("SELECT fileObj FROM PigFileObject fileObj ");
        if (idTipoFileObj != null) {
            queryStr.append("WHERE fileObj.pigTipoFileObject.idTipoFileObject = :idTipoFileObj");
        }
        Query query = getEntityManager().createQuery(queryStr.toString());
        if (idTipoFileObj != null) {
            query.setParameter("idTipoFileObj", HibernateUtils.longFrom(idTipoFileObj));
        }
        return query.getResultList();
    }

    /*
     * SOP CLASS
     */
    public List<PigSopClassDicom> getPigSopClassDicomList(String cdSopClassDicom, String dsSopClassDicom) {
        StringBuilder queryStr = new StringBuilder("SELECT sopClass FROM PigSopClassDicom sopClass ");
        String whereWord = "WHERE ";
        if (cdSopClassDicom != null) {
            queryStr.append(whereWord);
            queryStr.append(" UPPER(sopClass.cdSopClassDicom) LIKE :cdSopClass ESCAPE :char ");
            whereWord = " AND ";
        }
        if (dsSopClassDicom != null) {
            queryStr.append(whereWord);
            queryStr.append(" UPPER(sopClass.dsSopClassDicom) LIKE :dsSopClass ESCAPE :charDs ");
        }
        Query query = getEntityManager().createQuery(queryStr.toString());
        if (cdSopClassDicom != null) {
            query.setParameter("cdSopClass", "%" + cdSopClassDicom.toUpperCase() + "%");
            query.setParameter("char", '\\');
        }
        if (dsSopClassDicom != null) {
            query.setParameter("dsSopClass", "%" + dsSopClassDicom.toUpperCase() + "%");
            query.setParameter("charDs", '\\');
        }
        return query.getResultList();
    }

    public PigSopClassDicom getPigSopClassDicomByName(String nmSopClass) {
        String queryStr = "SELECT sopClass FROM PigSopClassDicom sopClass  WHERE sopClass.cdSopClassDicom= :cdAmbienteVers";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("cdAmbienteVers", nmSopClass);
        List<PigSopClassDicom> list = query.getResultList();
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    public PigSopClassDicom getPigSopClassDicomById(BigDecimal idSopClass) {
        String queryStr = "SELECT sopClass FROM PigSopClassDicom sopClass  WHERE sopClass.idSopClassDicom= :idSopClass";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idSopClass", HibernateUtils.longFrom(idSopClass));
        List<PigSopClassDicom> list = query.getResultList();
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    /*
     * SOPCLASSDICOMVERS
     */
    public List<PigSopClassDicomVers> getPigSopClassDicomVersList(BigDecimal idSopClassDicom, BigDecimal idVers) {
        StringBuilder queryStr = new StringBuilder(
                "SELECT sopClassDicomVers FROM PigSopClassDicomVers sopClassDicomVers ");
        String whereWord = "WHERE ";
        if (idSopClassDicom != null) {
            queryStr.append(whereWord);
            queryStr.append(" sopClassDicomVers.pigSopClassDicom.idSopClassDicom = :idSopClassDicom");
            whereWord = " AND ";
        }
        if (idVers != null) {
            queryStr.append(whereWord);
            queryStr.append(" sopClassDicomVers.pigVer.idVers = :idVers");
        }
        Query query = getEntityManager().createQuery(queryStr.toString());
        if (idSopClassDicom != null) {
            query.setParameter("idSopClassDicom", HibernateUtils.longFrom(idSopClassDicom));
        }
        if (idVers != null) {
            query.setParameter("idVers", HibernateUtils.longFrom(idVers));
        }
        return query.getResultList();
    }

    public List<PigSopClassDicom> getPigSopClassDicomDispList(PigVers vers) {
        StringBuilder queryStr = new StringBuilder(
                "SELECT sopClassDicom FROM PigSopClassDicomVers sopClassDicomVers JOIN sopClassDicomVers.pigSopClassDicom sopClassDicom ");
        if (vers != null && vers.getIdVers() != 0) {
            queryStr.append(" WHERE sopClassDicomVers.pigVer.idVers != :idVers");
        }
        Query query = getEntityManager().createQuery(queryStr.toString());
        if (vers != null && vers.getIdVers() != 0) {
            query.setParameter("idVers", vers.getIdVers());
        }
        return query.getResultList();
    }

    public PigSopClassDicomVers getPigSopClassDicomVers(BigDecimal idSopClassDicom, BigDecimal idVers) {
        StringBuilder queryStr = new StringBuilder("SELECT sopClassVers FROM PigSopClassDicomVers sopClassVers ");
        if (idSopClassDicom != null && idVers != null) {
            queryStr.append(
                    " WHERE sopClassVers.pigSopClassDicom.idSopClassDicom = :idSopClassDicom AND sopClassVers.pigVer.idVers = :idVers");
        }
        Query query = getEntityManager().createQuery(queryStr.toString());
        if (idSopClassDicom != null && idVers != null) {
            query.setParameter("idSopClassDicom", HibernateUtils.longFrom(idSopClassDicom));
            query.setParameter("idVers", HibernateUtils.longFrom(idVers));
        }
        List<PigSopClassDicomVers> list = query.getResultList();
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    public List<PigXsdDatiSpec> getPigXsdDatiSpecList(BigDecimal idTipoObj, BigDecimal idTipoFileObj) {
        StringBuilder queryStr = new StringBuilder("SELECT xsdDatiSpec FROM PigXsdDatiSpec xsdDatiSpec ");
        if (idTipoObj != null) {
            queryStr.append(" WHERE xsdDatiSpec.pigTipoObject.idTipoObject = :idTipoObj");
        } else if (idTipoFileObj != null) {
            queryStr.append(" WHERE xsdDatiSpec.pigTipoFileObject.idTipoFileObject = :idTipoFileObj");
        }
        Query query = getEntityManager().createQuery(queryStr.toString());
        if (idTipoObj != null) {
            query.setParameter("idTipoObj", HibernateUtils.longFrom(idTipoObj));
        } else if (idTipoFileObj != null) {
            query.setParameter("idTipoFileObj", HibernateUtils.longFrom(idTipoFileObj));
        }
        return query.getResultList();
    }

    public PigXsdDatiSpec getPigXsdDatiSpecById(BigDecimal idXsdDatiSpec) {
        String queryStr = "SELECT xsdDatiSpec FROM PigXsdDatiSpec xsdDatiSpec  WHERE xsdDatiSpec.idXsdSpec= :idXsdDatiSpec";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idXsdDatiSpec", HibernateUtils.longFrom(idXsdDatiSpec));
        List<PigXsdDatiSpec> list = query.getResultList();
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    public PigXsdDatiSpec getFullPigXsdDatiSpecById(BigDecimal idXsdDatiSpec) {
        String queryStr = "SELECT xsdDatiSpec, xsdDatiSpec.pigTipoObject, xsdDatiSpec.pigTipoFileObject FROM PigXsdDatiSpec xsdDatiSpec  WHERE xsdDatiSpec.idXsdSpec= :idXsdDatiSpec";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idXsdDatiSpec", HibernateUtils.longFrom(idXsdDatiSpec));
        List<Object[]> list = query.getResultList();
        if (list.isEmpty()) {
            return null;
        }
        Object[] xsdFull = list.get(0);
        PigXsdDatiSpec xsdDatiSpec = (PigXsdDatiSpec) xsdFull[0];
        xsdDatiSpec.setPigTipoObject((PigTipoObject) xsdFull[1]);
        xsdDatiSpec.setPigTipoFileObject((PigTipoFileObject) xsdFull[1]);
        return xsdDatiSpec;
    }

    public PigXsdDatiSpec getLastXsdDatiSpec(BigDecimal idTipoObj, BigDecimal idTipoFileObj) {
        StringBuilder queryStr = new StringBuilder("SELECT xsdDatiSpec FROM PigXsdDatiSpec xsdDatiSpec ");
        if (idTipoObj != null) {
            queryStr.append(" WHERE xsdDatiSpec.pigTipoObject.idTipoObject = :idTipoObj");
        } else if (idTipoFileObj != null) {
            queryStr.append(" WHERE xsdDatiSpec.pigTipoFileObject.idTipoFileObject = :idTipoFileObj");
        }
        queryStr.append(" ORDER BY  xsdDatiSpec.dtVersioneXsd DESC");
        Query query = getEntityManager().createQuery(queryStr.toString());
        if (idTipoObj != null) {
            query.setParameter("idTipoObj", HibernateUtils.longFrom(idTipoObj));
        } else if (idTipoFileObj != null) {
            query.setParameter("idTipoFileObj", idTipoFileObj);
        }
        List<PigXsdDatiSpec> list = query.getResultList();
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    public List<PigXsdDatiSpec> getOrdPigXsdDatiSpecList(BigDecimal idTipoObj, BigDecimal idTipoFileObj) {
        StringBuilder queryStr = new StringBuilder("SELECT xsdDatiSpec FROM PigXsdDatiSpec xsdDatiSpec ");
        if (idTipoObj != null) {
            queryStr.append(" WHERE xsdDatiSpec.pigTipoObject.idTipoObject = :idTipoObj");
        } else if (idTipoFileObj != null) {
            queryStr.append(" WHERE xsdDatiSpec.pigTipoFileObject.idTipoFileObject = :idTipoFileObj");
        }
        queryStr.append(" ORDER BY  xsdDatiSpec.dtVersioneXsd DESC");
        Query query = getEntityManager().createQuery(queryStr.toString());
        if (idTipoObj != null) {
            query.setParameter("idTipoObj", HibernateUtils.longFrom(idTipoObj));
        } else if (idTipoFileObj != null) {
            query.setParameter("idTipoFileObj", idTipoFileObj);
        }
        return query.getResultList();
    }

    public PigXsdDatiSpec getSecondLastXsdDatiSpec(BigDecimal idTipoObj, BigDecimal idTipoFileObj) {
        StringBuilder queryStr = new StringBuilder("SELECT xsdDatiSpec FROM PigXsdDatiSpec xsdDatiSpec ");
        if (idTipoObj != null) {
            queryStr.append(" WHERE xsdDatiSpec.pigTipoObject.idTipoObject = :idTipoObj");
        } else if (idTipoFileObj != null) {
            queryStr.append(" WHERE xsdDatiSpec.pigTipoFileObject.idTipoFileObject = :idTipoFileObj");
        }
        queryStr.append(" ORDER BY  xsdDatiSpec.dtVersioneXsd DESC");
        Query query = getEntityManager().createQuery(queryStr.toString());
        if (idTipoObj != null) {
            query.setParameter("idTipoObj", HibernateUtils.longFrom(idTipoObj));
        } else if (idTipoFileObj != null) {
            query.setParameter("idTipoFileObj", idTipoFileObj);
        }
        List<PigXsdDatiSpec> list = query.getResultList();
        if (list.isEmpty()) {
            return null;
        }
        if (list.get(1) == null) {
            return null;
        }
        return list.get(1);
    }

    /*
     * ATTRIBDATISPEC
     */
    public List<PigAttribDatiSpec> getPigAttribDatiSpecList(BigDecimal idXsdDatiSpec) {
        StringBuilder queryStr = new StringBuilder("SELECT attribDatiSpec FROM PigAttribDatiSpec attribDatiSpec ");
        if (idXsdDatiSpec != null) {
            queryStr.append("WHERE attribDatiSpec.pigXsdDatiSpec.idXsdSpec = :idXsdDatiSpec");
        }
        Query query = getEntityManager().createQuery(queryStr.toString());
        if (idXsdDatiSpec != null) {
            query.setParameter("idXsdDatiSpec", HibernateUtils.longFrom(idXsdDatiSpec));
        }
        return query.getResultList();
    }

    public List<PigInfoDicom> getPigInfoDicomListByXsd(BigDecimal idXsdDatiSpec) {
        StringBuilder queryStr = new StringBuilder("SELECT infoDicom FROM PigInfoDicom infoDicom");
        if (idXsdDatiSpec != null) {
            queryStr.append(" WHERE infoDicom.pigXsdDatiSpec.idXsdSpec = :idXsdDatiSpec");
        }
        Query query = getEntityManager().createQuery(queryStr.toString());
        if (idXsdDatiSpec != null) {
            query.setParameter("idXsdDatiSpec", HibernateUtils.longFrom(idXsdDatiSpec));
        }
        return query.getResultList();
    }

    public <T extends Serializable> IamOrganizDaReplic insertEntityIamOrganizDaReplic(T entity,
            Constants.TiOperReplic tipoOperazione) throws IncoherenceException {
        String tipoOrganiz = null;
        String nomeOrganiz = null;
        BigDecimal idOrganiz = null;
        if (entity instanceof PigAmbienteVers) {
            PigAmbienteVers amb = (PigAmbienteVers) entity;
            tipoOrganiz = Constants.NmOrganizReplic.AMBIENTE.name();
            nomeOrganiz = amb.getNmAmbienteVers();
            idOrganiz = new BigDecimal(amb.getIdAmbienteVers());
        } else if (entity instanceof PigVers) {
            PigVers vers = (PigVers) entity;
            tipoOrganiz = Constants.NmOrganizReplic.VERSATORE.name();
            nomeOrganiz = vers.getNmVers();
            idOrganiz = new BigDecimal(vers.getIdVers());
        }

        if (tipoOrganiz != null && nomeOrganiz != null && idOrganiz != null) {
            IamOrganizDaReplic replica = new IamOrganizDaReplic();
            replica.setIdOrganizApplic(idOrganiz);
            replica.setNmTipoOrganiz(tipoOrganiz);
            replica.setNmOrganiz(nomeOrganiz);
            replica.setTiOperReplic(tipoOperazione.name());
            replica.setTiStatoReplic(Constants.TiStatoReplic.DA_REPLICARE.name());
            replica.setDtLogOrganizDaReplic(new Date());
            getEntityManager().persist(replica);
            getEntityManager().flush();
            return replica;
        } else {
            throw new IncoherenceException(
                    "Errore imprevisto in fase di inserimento di un record di replica di AMBIENTE o VERSATORE");
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public <T extends Serializable> IamOrganizDaReplic insertEntityIamOrganizDaReplicNewTx(T entity,
            Constants.TiOperReplic tipoOperazione) throws IncoherenceException {
        return insertEntityIamOrganizDaReplic(entity, tipoOperazione);
    }

    public PigVers updatePigVers(BigDecimal idVers, PigVersRowBean rowBean) {
        if (idVers != null) {
            PigVers vers = getEntityManager().find(PigVers.class, idVers.longValue());
            PigAmbienteVers ambienteVers = getEntityManager().find(PigAmbienteVers.class,
                    rowBean.getIdAmbienteVers().longValue());
            vers.setDsVers(rowBean.getDsVers());
            vers.setPigAmbienteVer(ambienteVers);
            vers.setNmVers(rowBean.getNmVers());
            vers.setDtIniValAppartAmbiente(rowBean.getDtIniValAppartAmbiente());
            vers.setDtFinValAppartAmbiente(rowBean.getDtFinValAppartAmbiente());
            vers.setDtIniValVers(rowBean.getDtIniValVers());
            vers.setDtFineValVers(rowBean.getDtFineValVers());
            vers.setDsPathInputFtp(rowBean.getDsPathInputFtp());
            vers.setDsPathOutputFtp(rowBean.getDsPathOutputFtp());
            vers.setDsPathTrasf(rowBean.getDsPathTrasf());
            vers.setFlArchivioRestituito(rowBean.getFlArchivioRestituito());
            vers.setFlCessato(rowBean.getFlCessato());
            return vers;
        }
        return null;
    }

    public void updateTipoObj(BigDecimal idTipoObj, PigTipoObjectRowBean tipoObjRowBean) {
        if (idTipoObj != null) {
            PigTipoObject tipoObj = getEntityManager().find(PigTipoObject.class, idTipoObj.longValue());
            // Controllo se devo eliminare le corrispondenze o i versatori a seconda della modifica del tipo versamento
            // file
            if (tipoObj.getTiVersFile().equals(it.eng.sacerasi.common.Constants.TipoVersamento.DA_TRASFORMARE.name())
                    && !tipoObjRowBean.getTiVersFile()
                            .equals(it.eng.sacerasi.common.Constants.TipoVersamento.DA_TRASFORMARE.name())) {
                bulkDeletePigVersTipoObjectDaTrasf(tipoObj.getIdTipoObject());
            } else if (!tipoObj.getTiVersFile()
                    .equals(it.eng.sacerasi.common.Constants.TipoVersamento.DA_TRASFORMARE.name())
                    && tipoObjRowBean.getTiVersFile()
                            .equals(it.eng.sacerasi.common.Constants.TipoVersamento.DA_TRASFORMARE.name())) {
                bulkDeletePigDichVersSacerTipoObj(tipoObj.getIdTipoObject());
            }

            tipoObj.setNmTipoObject(tipoObjRowBean.getNmTipoObject());
            tipoObj.setDsTipoObject(tipoObjRowBean.getDsTipoObject());
            tipoObj.setTiCalcKeyUnitaDoc(tipoObjRowBean.getTiCalcKeyUnitaDoc());
            tipoObj.setTiVersFile(tipoObjRowBean.getTiVersFile());
            tipoObj.setFlContrHash(tipoObjRowBean.getFlContrHash());
            tipoObj.setCdRegistroUnitaDocSacer(tipoObjRowBean.getCdRegistroUnitaDocSacer());
            tipoObj.setNmTipoUnitaDocSacer(tipoObjRowBean.getNmTipoUnitaDocSacer());
            tipoObj.setFlForzaAccettazioneSacer(tipoObjRowBean.getFlForzaAccettazioneSacer());
            tipoObj.setFlForzaCollegamento(tipoObjRowBean.getFlForzaCollegamento());
            tipoObj.setFlForzaConservazione(tipoObjRowBean.getFlForzaConservazione());
            tipoObj.setTiConservazione(tipoObjRowBean.getTiConservazione());
            tipoObj.setDsRegExpCdVers(tipoObjRowBean.getDsRegExpCdVers());
            tipoObj.setFlNoVisibVersOgg(tipoObjRowBean.getFlNoVisibVersOgg());
            if (tipoObjRowBean.getIdTrasf() != null) {
                XfoTrasf xfoTrasf = findById(XfoTrasf.class, tipoObjRowBean.getIdTrasf());
                tipoObj.setXfoTrasf(xfoTrasf);
            } else {
                tipoObj.setXfoTrasf(null);
            }
            if (tipoObjRowBean.getTiPriorita() != null) {
                tipoObj.setTiPriorita(tipoObjRowBean.getTiPriorita());
            } else {
                tipoObj.setTiPriorita(null);
            }
            if (tipoObjRowBean.getTiPrioritaVersamento() != null) {
                tipoObj.setTiPrioritaVersamento(tipoObjRowBean.getTiPrioritaVersamento());
            } else {
                tipoObj.setTiPrioritaVersamento(null);
            }
        }
    }

    public List<PigStatoObject> getPigStatoObjectList() {
        return getEntityManager().createNamedQuery("PigStatoObject.findAll").getResultList();
    }

    public void updatePigStatoObject(String tiStatoObject, String dsTiStatoObject) {
        Query q = getEntityManager().createQuery(
                "SELECT statoObject FROM PigStatoObject statoObject WHERE statoObject.tiStatoObject = :tiStatoObject ");
        q.setParameter("tiStatoObject", tiStatoObject);
        PigStatoObject statoObject = (PigStatoObject) q.getSingleResult();
        statoObject.setDsTiStatoObject(dsTiStatoObject);
    }

    /*
     * CORRISPONDENZE IN SACER TIPO OBJ
     */
    public List<Object[]> getPigDichVersSacerTipoObjList(BigDecimal idTipoObject) {
        StringBuilder queryStr = new StringBuilder("SELECT dichVersSacerTipoObj, treeOrganizIam.dlCompositoOrganiz "
                + "FROM PigDichVersSacerTipoObj dichVersSacerTipoObj, UsrVTreeOrganizIam treeOrganizIam "
                + "WHERE dichVersSacerTipoObj.idOrganizIam = treeOrganizIam.idOrganizIam ");
        if (idTipoObject != null) {
            queryStr.append("AND dichVersSacerTipoObj.pigTipoObject.idTipoObject = :idTipoObject ");
        }
        queryStr.append(" ORDER BY treeOrganizIam.dlCompositoOrganiz ");

        Query query = getEntityManager().createQuery(queryStr.toString());
        if (idTipoObject != null) {
            query.setParameter("idTipoObject", HibernateUtils.longFrom(idTipoObject));
        }
        return query.getResultList();
    }

    /*
     * VERSATORI PER CUI SI GENERANO OGGETTI
     */
    public List<PigVersTipoObjectDaTrasf> retrievePigVersTipoObjectDaTrasfList(BigDecimal idTipoObject) {
        StringBuilder queryStr = new StringBuilder(
                "SELECT versTipoObjectDaTrasf FROM PigVersTipoObjectDaTrasf versTipoObjectDaTrasf ");
        if (idTipoObject != null) {
            queryStr.append("WHERE versTipoObjectDaTrasf.pigTipoObjectDaTrasf.idTipoObject = :idTipoObject");
        }

        Query query = getEntityManager().createQuery(queryStr.toString());
        if (idTipoObject != null) {
            query.setParameter("idTipoObject", HibernateUtils.longFrom(idTipoObject));
        }
        return query.getResultList();
    }

    public PigVersTipoObjectDaTrasf getPigVersTipoObjectDaTrasf(BigDecimal idTipoObjectDaTrasf, String cdVersGen,
            BigDecimal idVersGen) {
        StringBuilder queryStr = new StringBuilder(
                "SELECT versTipoObjectDaTrasf FROM PigVersTipoObjectDaTrasf versTipoObjectDaTrasf WHERE versTipoObjectDaTrasf.pigTipoObjectDaTrasf.idTipoObject = :idTipoObjectDaTrasf ");
        if (StringUtils.isNotBlank(cdVersGen)) {
            queryStr.append("AND versTipoObjectDaTrasf.cdVersGen = :cdVersGen ");
        } else if (idVersGen != null) {
            queryStr.append("AND versTipoObjectDaTrasf.pigVersGen.idVers = :idVersGen ");
        } else {
            throw new IllegalArgumentException(
                    "Id versatore generato o codice versatore generato nulli, impossibile eseguire la query correttamente");
        }

        PigVersTipoObjectDaTrasf object = null;
        Query query = getEntityManager().createQuery(queryStr.toString());
        query.setParameter("idTipoObjectDaTrasf", HibernateUtils.longFrom(idTipoObjectDaTrasf));
        if (StringUtils.isNotBlank(cdVersGen)) {
            query.setParameter("cdVersGen", cdVersGen);
        } else if (idVersGen != null) {
            query.setParameter("idVersGen", HibernateUtils.longFrom(idVersGen));
        }
        List<PigVersTipoObjectDaTrasf> list = query.getResultList();
        if (list != null && !list.isEmpty()) {
            // Può esserci un solo elemento
            object = list.get(0);
        }
        return object;
    }

    public boolean existsPigObjectGen(Long idVers, Long idTipoObject) {
        String queryStr = "SELECT COUNT(obj) FROM PigObject obj "
                + "WHERE obj.pigTipoObject.idTipoObject = :idTipoObject " + "AND obj.pigVer.idVers = :idVers ";

        Query query = getEntityManager().createQuery(queryStr);

        query.setParameter("idTipoObject", idTipoObject);
        query.setParameter("idVers", idVers);
        return (Long) query.getSingleResult() > 0;
    }

    public boolean existsPigObjectDaTrasformare(String cdVersGen, Long idTipoObjectTrasf) {
        Query query = getEntityManager()
                .createQuery("SELECT COUNT(objs) FROM PigVersTipoObjectDaTrasf versTipoObjectDaTrasf "
                        + "JOIN versTipoObjectDaTrasf.pigTipoObjectDaTrasf tipoObjectDaTrasf "
                        + "JOIN tipoObjectDaTrasf.pigObjects objs "
                        + "WHERE versTipoObjectDaTrasf.cdVersGen = :cdVersGen "
                        + "AND tipoObjectDaTrasf.idTipoObject = :idTipoObjectTrasf");
        query.setParameter("cdVersGen", cdVersGen);
        query.setParameter("idTipoObjectTrasf", idTipoObjectTrasf);
        return (Long) query.getSingleResult() > 0;
    }

    /*
     * SET PARAMETRI VERSATORE PER CUI SI GENERANO OGGETTI
     */
    public List<PigVValoreSetParamTrasf> getPigVValoreSetParamTrasfList(BigDecimal idVersTipoObjectDaTrasf) {
        StringBuilder queryStr = new StringBuilder(
                "SELECT DISTINCT valoreSetParamTrasf FROM PigVValoreSetParamTrasf valoreSetParamTrasf ");
        if (idVersTipoObjectDaTrasf != null) {
            queryStr.append("WHERE valoreSetParamTrasf.id.idVersTipoObjectDaTrasf = :idVersTipoObjectDaTrasf");
        }
        Query query = getEntityManager().createQuery(queryStr.toString());
        if (idVersTipoObjectDaTrasf != null) {
            query.setParameter("idVersTipoObjectDaTrasf", idVersTipoObjectDaTrasf);
        }
        return query.getResultList();
    }

    public List<PigVValoreSetParamTrasf> getPigVValoreSetParamTrasfListByIdSetParamTrasf(BigDecimal idSetParamTrasf) {
        StringBuilder queryStr = new StringBuilder(
                "SELECT valoreSetParamTrasf FROM PigVValoreSetParamTrasf valoreSetParamTrasf ");
        if (idSetParamTrasf != null) {
            queryStr.append("WHERE valoreSetParamTrasf.id.idSetParamTrasf = :idSetParamTrasf");
        }

        Query query = getEntityManager().createQuery(queryStr.toString());
        if (idSetParamTrasf != null) {
            query.setParameter("idSetParamTrasf", idSetParamTrasf);
        }
        return query.getResultList();
    }

    public List<PigStoricoVersAmbiente> getPigStoricoVersAmbienteList(BigDecimal idVers) {
        String queryStr = "SELECT storicoVersAmbiente FROM PigStoricoVersAmbiente storicoVersAmbiente "
                + "WHERE storicoVersAmbiente.pigVer.idVers = :idVers";

        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idVers", HibernateUtils.longFrom(idVers));
        return query.getResultList();
    }

    public List<PigVValoreParamTrasf> getPigVValoreParamTrasfList(BigDecimal idSetParamTrasf,
            BigDecimal idVersTipoObjectDaTrasf) {
        List<PigVValoreParamTrasf> list = new ArrayList<>();
        if (idSetParamTrasf != null && idVersTipoObjectDaTrasf != null) {
            String queryStr = "SELECT DISTINCT valoreParamTrasf FROM PigVValoreParamTrasf valoreParamTrasf "
                    + "WHERE valoreParamTrasf.idSetParamTrasf = :idSetParamTrasf "
                    + "AND valoreParamTrasf.id.idVersTipoObjectDaTrasf = :idVersTipoObjectDaTrasf "
                    + "ORDER BY valoreParamTrasf.nmParamTrasf ";
            Query query = getEntityManager().createQuery(queryStr);
            query.setParameter("idSetParamTrasf", idSetParamTrasf);
            query.setParameter("idVersTipoObjectDaTrasf", idVersTipoObjectDaTrasf);
            list = query.getResultList();
        }
        return list;
    }

    public List<PigVValParamTrasfDefSpec> getPigVValParamTrasfDefSpecList(BigDecimal idSetParamTrasf,
            BigDecimal idVersTipoObjectDaTrasf) {
        List<PigVValParamTrasfDefSpec> list = new ArrayList<>();
        if (idSetParamTrasf != null && idVersTipoObjectDaTrasf != null) {
            String queryStr = "SELECT DISTINCT valoreParamTrasf FROM PigVValParamTrasfDefSpec valoreParamTrasf "
                    + "WHERE valoreParamTrasf.idSetParamTrasf = :idSetParamTrasf "
                    + "AND valoreParamTrasf.id.idVersTipoObjectDaTrasf = :idVersTipoObjectDaTrasf "
                    + "ORDER BY valoreParamTrasf.nmParamTrasf ";
            Query query = getEntityManager().createQuery(queryStr);
            query.setParameter("idSetParamTrasf", idSetParamTrasf);
            query.setParameter("idVersTipoObjectDaTrasf", idVersTipoObjectDaTrasf);
            list = query.getResultList();
        }
        return list;
    }

    public String getPigVValParamTrasfDefSpecTypeByName(BigDecimal idSetParamTrasf, BigDecimal idVersTipoObjectDaTrasf,
            String nmParamTrasf) {

        String tiValParam = null;

        if (idSetParamTrasf != null && idVersTipoObjectDaTrasf != null && nmParamTrasf != null) {
            String queryStr = "SELECT valoreParamTrasf.tiParamTrasf FROM PigVValParamTrasfDefSpec valoreParamTrasf "
                    + "WHERE valoreParamTrasf.idSetParamTrasf = :idSetParamTrasf "
                    + "AND valoreParamTrasf.id.idVersTipoObjectDaTrasf = :idVersTipoObjectDaTrasf "
                    + "AND valoreParamTrasf.nmParamTrasf = :nmParamTrasf";
            Query query = getEntityManager().createQuery(queryStr);
            query.setParameter("idSetParamTrasf", idSetParamTrasf);
            query.setParameter("idVersTipoObjectDaTrasf", idVersTipoObjectDaTrasf);
            query.setParameter("nmParamTrasf", nmParamTrasf);
            tiValParam = (String) query.getSingleResult();
        }
        return tiValParam;
    }

    public PigVValParamTrasfDefSpec getPigVValParamTrasfDefSpecByName(BigDecimal idSetParamTrasf,
            BigDecimal idVersTipoObjectDaTrasf, String nmParamTrasf) {

        PigVValParamTrasfDefSpec pigVValParamTrasfDefSpec = null;

        if (idSetParamTrasf != null && idVersTipoObjectDaTrasf != null && nmParamTrasf != null) {
            String queryStr = "SELECT valoreParamTrasf FROM PigVValParamTrasfDefSpec valoreParamTrasf "
                    + "WHERE valoreParamTrasf.idSetParamTrasf = :idSetParamTrasf "
                    + "AND valoreParamTrasf.id.idVersTipoObjectDaTrasf = :idVersTipoObjectDaTrasf "
                    + "AND valoreParamTrasf.nmParamTrasf = :nmParamTrasf";
            Query query = getEntityManager().createQuery(queryStr);
            query.setParameter("idSetParamTrasf", idSetParamTrasf);
            query.setParameter("idVersTipoObjectDaTrasf", idVersTipoObjectDaTrasf);
            query.setParameter("nmParamTrasf", nmParamTrasf);
            pigVValParamTrasfDefSpec = (PigVValParamTrasfDefSpec) query.getSingleResult();
        }

        return pigVValParamTrasfDefSpec;
    }

    public PigValoreSetParamTrasf getPigValoreSetParamTrasf(BigDecimal idSetParamTrasf,
            BigDecimal idVersTipoObjectDaTrasf) {
        PigValoreSetParamTrasf valore = null;
        if (idSetParamTrasf != null && idVersTipoObjectDaTrasf != null) {
            String queryStr = "SELECT valoreSetParamTrasf FROM PigValoreSetParamTrasf valoreSetParamTrasf "
                    + "WHERE valoreSetParamTrasf.pigVersTipoObjectDaTrasf.idVersTipoObjectDaTrasf = :idVersTipoObjectDaTrasf "
                    + "AND valoreSetParamTrasf.xfoSetParamTrasf.idSetParamTrasf = :idSetParamTrasf ";
            Query query = getEntityManager().createQuery(queryStr);
            query.setParameter("idSetParamTrasf", HibernateUtils.longFrom(idSetParamTrasf));
            query.setParameter("idVersTipoObjectDaTrasf", HibernateUtils.longFrom(idVersTipoObjectDaTrasf));
            List<PigValoreSetParamTrasf> valoreList = query.getResultList();
            if (!valoreList.isEmpty()) {
                valore = valoreList.get(0);
            }
        }
        return valore;
    }

    /**
     * Ritorna la lista delle trasformazioni definite nella tabella XfoTrasf
     *
     * @return lista elementi di tipo {@link XfoTrasf}
     */
    public List<XfoTrasf> getXfoTrasf() {
        Query q = getEntityManager().createQuery("SELECT trasf FROM XfoTrasf trasf ORDER BY trasf.cdTrasf");
        List<XfoTrasf> list = q.getResultList();
        return list;
    }

    public List<UsrVAbilStrutSacerXping> getAmbientiFromUsrVAbilStrutSacerXping(long idUserIam) {
        TypedQuery<UsrVAbilStrutSacerXping> q = getEntityManager().createQuery(
                "SELECT DISTINCT new it.eng.sacerasi.viewEntity.UsrVAbilStrutSacerXping(organiz.id.idUserIam, 'AMBIENTE', organiz.idAmbiente, organiz.nmAmbiente) FROM UsrVAbilStrutSacerXping organiz WHERE organiz.id.idUserIam = :idUserIam ORDER BY organiz.nmAmbiente",
                UsrVAbilStrutSacerXping.class);
        q.setParameter("idUserIam", HibernateUtils.bigDecimalFrom(idUserIam));
        return q.getResultList();
    }

    public UsrVAbilStrutSacerXping getAmbienteFromUsrVAbilStrutSacerXping(long idUserIam, String nmAmbiente) {
        TypedQuery<UsrVAbilStrutSacerXping> q = getEntityManager().createQuery(
                "SELECT new it.eng.sacerasi.viewEntity.UsrVAbilStrutSacerXping(organiz.id.idUserIam, 'AMBIENTE', organiz.idAmbiente, organiz.nmAmbiente) FROM UsrVAbilStrutSacerXping organiz WHERE organiz.id.idUserIam = :idUserIam AND organiz.nmAmbiente = :nmAmbiente ORDER BY organiz.nmAmbiente",
                UsrVAbilStrutSacerXping.class);
        q.setParameter("idUserIam", HibernateUtils.bigDecimalFrom(idUserIam));
        q.setParameter("nmAmbiente", nmAmbiente);
        return q.getSingleResult();
    }

    public List<UsrVAbilStrutSacerXping> getEntiFromUsrVAbilStrutSacerXping(long idUserIam, BigDecimal idAmbiente) {
        String query = "SELECT DISTINCT new it.eng.sacerasi.viewEntity.UsrVAbilStrutSacerXping(organiz.id.idUserIam, 'ENTE', organiz.idEnte, organiz.nmEnte) FROM UsrVAbilStrutSacerXping organiz WHERE organiz.id.idUserIam = :idUserIam "
                + (idAmbiente != null ? "AND organiz.idAmbiente = :idAmbiente" : "") + " ORDER BY organiz.nmEnte";
        TypedQuery<UsrVAbilStrutSacerXping> q = getEntityManager().createQuery(query, UsrVAbilStrutSacerXping.class);
        q.setParameter("idUserIam", HibernateUtils.bigDecimalFrom(idUserIam));
        if (idAmbiente != null) {
            q.setParameter("idAmbiente", idAmbiente);
        }
        return q.getResultList();
    }

    public List<UsrVAbilStrutSacerXping> getEntiFromUsrVAbilStrutSacerXping(long idUserIam, String nmAmbiente) {
        String query = "SELECT DISTINCT new it.eng.sacerasi.viewEntity.UsrVAbilStrutSacerXping(organiz.id.idUserIam, 'ENTE', organiz.idEnte, organiz.nmEnte) FROM UsrVAbilStrutSacerXping organiz WHERE organiz.id.idUserIam = :idUserIam "
                + (StringUtils.isNotBlank(nmAmbiente) ? "AND organiz.nmAmbiente = :nmAmbiente" : "")
                + " ORDER BY organiz.nmEnte";
        TypedQuery<UsrVAbilStrutSacerXping> q = getEntityManager().createQuery(query, UsrVAbilStrutSacerXping.class);
        q.setParameter("idUserIam", HibernateUtils.bigDecimalFrom(idUserIam));
        if (StringUtils.isNotBlank(nmAmbiente)) {
            q.setParameter("nmAmbiente", nmAmbiente);
        }
        return q.getResultList();
    }

    public List<UsrVAbilStrutSacerXping> getStruttureFromUsrVAbilStrutSacerXping(long idUserIam, BigDecimal idEnte) {
        String query = "SELECT DISTINCT new it.eng.sacerasi.viewEntity.UsrVAbilStrutSacerXping(organiz.id.idUserIam, 'STRUTTURA', organiz.id.idStrut, organiz.nmStrut) FROM UsrVAbilStrutSacerXping organiz WHERE organiz.id.idUserIam = :idUserIam "
                + (idEnte != null ? "AND organiz.idEnte = :idEnte" : "") + " ORDER BY organiz.nmStrut";
        TypedQuery<UsrVAbilStrutSacerXping> q = getEntityManager().createQuery(query, UsrVAbilStrutSacerXping.class);
        q.setParameter("idUserIam", HibernateUtils.bigDecimalFrom(idUserIam));
        if (idEnte != null) {
            q.setParameter("idEnte", idEnte);
        }
        return q.getResultList();
    }

    public List<UsrVAbilStrutSacerXping> getStruttureFromUsrVAbilStrutSacerXping(long idUserIam, String nmAmbiente,
            String nmEnte) {
        String query = "SELECT DISTINCT new it.eng.sacerasi.viewEntity.UsrVAbilStrutSacerXping(organiz.id.idUserIam, 'STRUTTURA', organiz.id.idStrut, organiz.nmStrut) FROM UsrVAbilStrutSacerXping organiz WHERE organiz.id.idUserIam = :idUserIam "
                + (StringUtils.isNotBlank(nmAmbiente) ? "AND organiz.nmAmbiente = :nmAmbiente " : "")
                + (StringUtils.isNotBlank(nmEnte) ? "AND organiz.nmEnte = :nmEnte" : "") + " ORDER BY organiz.nmStrut";
        TypedQuery<UsrVAbilStrutSacerXping> q = getEntityManager().createQuery(query, UsrVAbilStrutSacerXping.class);
        q.setParameter("idUserIam", HibernateUtils.bigDecimalFrom(idUserIam));
        if (StringUtils.isNotBlank(nmAmbiente)) {
            q.setParameter("nmAmbiente", nmAmbiente);
        }
        if (StringUtils.isNotBlank(nmEnte)) {
            q.setParameter("nmEnte", nmEnte);
        }
        return q.getResultList();
    }

    public UsrVChkCreaAmbSacer getUsrVChkCreaAmbSacer(long idUser, String nmApplic) {
        Query query = getEntityManager().createQuery(
                "SELECT u FROM UsrVChkCreaAmbSacer u WHERE u.id.idUserIam = :idUserIam AND u.nmApplic = :nmApplic");
        query.setParameter("idUserIam", HibernateUtils.bigDecimalFrom(idUser));
        query.setParameter("nmApplic", nmApplic);
        return (UsrVChkCreaAmbSacer) query.getSingleResult();
    }

    public List<UsrVAbilAmbXver> getUsrVAbilAmbXverList(long idUser, String nmApplic) {
        Query query = getEntityManager().createQuery(
                "SELECT u FROM UsrVAbilAmbXver u WHERE u.id.idUserIam = :idUserIam AND u.nmApplic = :nmApplic ORDER BY u.nmOrganiz");
        query.setParameter("idUserIam", HibernateUtils.bigDecimalFrom(idUser));
        query.setParameter("nmApplic", nmApplic);
        return query.getResultList();
    }

    public List<String> getNmUseridSacerByPigVLisStrutVersSacer() {
        Query query = getEntityManager().createQuery("SELECT DISTINCT(strutVersSacer.nmUseridSacer) "
                + "FROM PigVLisStrutVersSacer strutVersSacer " + "ORDER BY strutVersSacer.nmUseridSacer ");
        return query.getResultList();
    }

    public List<UsrVAbilStrutSacerXping> getOrganizIamFromUsrVAbilStrutSacerXping(long idUserIam, BigDecimal idEnte) {
        String query = "SELECT DISTINCT organiz FROM UsrVAbilStrutSacerXping organiz "
                + "WHERE organiz.id.idUserIam = :idUserIam " + (idEnte != null ? "AND organiz.idEnte = :idEnte" : "")
                + " " + "ORDER BY organiz.nmStrut";
        TypedQuery<UsrVAbilStrutSacerXping> q = getEntityManager().createQuery(query, UsrVAbilStrutSacerXping.class);
        q.setParameter("idUserIam", HibernateUtils.bigDecimalFrom(idUserIam));
        if (idEnte != null) {
            q.setParameter("idEnte", idEnte);
        }
        return q.getResultList();
    }

    public List<UsrVAbilStrutSacerXping> getDlCompositoOrganizAmbienti(long idUserIam) {
        TypedQuery<UsrVAbilStrutSacerXping> q = getEntityManager().createQuery("SELECT DISTINCT "
                + "new it.eng.sacerasi.viewEntity.UsrVAbilStrutSacerXping(organiz.idOrganizIamAmbiente, 'AMBIENTE', organiz.nmAmbiente) "
                + "FROM UsrVAbilStrutSacerXping organiz " + "WHERE organiz.id.idUserIam = :idUserIam "
                + "ORDER BY organiz.nmAmbiente ", UsrVAbilStrutSacerXping.class);
        q.setParameter("idUserIam", HibernateUtils.bigDecimalFrom(idUserIam));
        return q.getResultList();
    }

    public List<UsrVAbilStrutSacerXping> getDlCompositoOrganizEnti(long idUserIam) {
        TypedQuery<UsrVAbilStrutSacerXping> q = getEntityManager().createQuery("SELECT DISTINCT "
                + "new it.eng.sacerasi.viewEntity.UsrVAbilStrutSacerXping(organiz.idOrganizIamEnte, 'ENTE', CONCAT(organiz.nmAmbiente, ' / ', organiz.nmEnte) ) "
                + "FROM UsrVAbilStrutSacerXping organiz " + "WHERE organiz.id.idUserIam = :idUserIam ",
                UsrVAbilStrutSacerXping.class);
        q.setParameter("idUserIam", HibernateUtils.bigDecimalFrom(idUserIam));
        return q.getResultList().stream().sorted(Comparator.comparing(UsrVAbilStrutSacerXping::getDlCompositoOrganiz))
                .collect(Collectors.toList());
    }

    public List<UsrVAbilStrutSacerXping> getDlCompositoOrganizStrutture(long idUserIam) {
        TypedQuery<UsrVAbilStrutSacerXping> q = getEntityManager().createQuery("SELECT DISTINCT "
                + "new it.eng.sacerasi.viewEntity.UsrVAbilStrutSacerXping(organiz.idOrganizIamStrut, 'STRUTTURA', organiz.dlCompositoOrganiz) "
                + "FROM UsrVAbilStrutSacerXping organiz " + "WHERE organiz.id.idUserIam = :idUserIam "
                + "ORDER BY organiz.dlCompositoOrganiz ", UsrVAbilStrutSacerXping.class);
        q.setParameter("idUserIam", HibernateUtils.bigDecimalFrom(idUserIam));
        return q.getResultList();
    }

    public boolean existPigValoreSetParamTrasf(BigDecimal idVersTipoObjectDaTrasf, BigDecimal idSetParamTrasf) {
        Query query = getEntityManager().createQuery("SELECT COUNT(p) FROM PigValoreSetParamTrasf p "
                + "WHERE p.pigVersTipoObjectDaTrasf.idVersTipoObjectDaTrasf = :idVersTipoObjectDaTrasf "
                + "AND p.xfoSetParamTrasf.idSetParamTrasf = :idSetParamTrasf");
        query.setParameter("idVersTipoObjectDaTrasf", HibernateUtils.longFrom(idVersTipoObjectDaTrasf));
        query.setParameter("idSetParamTrasf", HibernateUtils.longFrom(idSetParamTrasf));
        Long count = (Long) query.getSingleResult();
        return count > 0L;
    }

    public boolean existPigValoreParamTrasf(BigDecimal idValoreSetParamTrasf, BigDecimal idParamTrasf) {
        Query query = getEntityManager().createQuery("SELECT COUNT(p) FROM PigValoreParamTrasf p "
                + "WHERE p.pigValoreSetParamTrasf.idValoreSetParamTrasf = :idValoreSetParamTrasf "
                + "AND p.xfoParamTrasf.idParamTrasf = :idParamTrasf");
        query.setParameter("idValoreSetParamTrasf", HibernateUtils.longFrom(idValoreSetParamTrasf));
        query.setParameter("idParamTrasf", HibernateUtils.longFrom(idParamTrasf));
        Long count = (Long) query.getSingleResult();
        return count > 0L;
    }

    public boolean existPigVValParamTrasfDefSpec(BigDecimal idValoreSetParamTrasf, String nmParamTrasf) {
        Query query = getEntityManager().createQuery("SELECT COUNT(p) FROM PigVValParamTrasfDefSpec p "
                + "WHERE p.idSetParamTrasf = :idValoreSetParamTrasf " + "AND p.nmParamTrasf = :nmParamTrasf");
        query.setParameter("idValoreSetParamTrasf", idValoreSetParamTrasf);
        query.setParameter("nmParamTrasf", nmParamTrasf);
        Long count = (Long) query.getSingleResult();
        return count > 0L;
    }

    /**
     * Verifica l'esistenza di un record data la chiave unique su idObject e cdKeyObjectTrasf
     *
     * @param idObject
     *            id oggetto
     * @param cdKeyObjectTrasf
     *            chiave oggetto
     *
     * @return entity {@link PigObjectTrasf}
     */
    public PigObjectTrasf getPigObjectTrasf(BigDecimal idObject, String cdKeyObjectTrasf) {
        PigObjectTrasf result = null;
        Query query = getEntityManager().createQuery(
                "SELECT t FROM PigObjectTrasf t WHERE t.pigObject.idObject = :idObject AND t.cdKeyObjectTrasf = :cdKeyObjectTrasf ");
        query.setParameter("idObject", HibernateUtils.longFrom(idObject));
        query.setParameter("cdKeyObjectTrasf", cdKeyObjectTrasf);
        List<PigObjectTrasf> list = query.getResultList();
        if (list != null && !list.isEmpty()) {
            result = list.get(0);
        }
        return result;
    }

    /**
     * Verifica l'esistenza di un record data la chiave unique su idObject e pgObjectTrasf
     *
     * @param idObject
     *            id oggetto
     * @param pgOggettoTrasf
     *            progressivo
     *
     * @return entity {@link PigObjectTrasf}
     */
    public PigObjectTrasf getPigObjectTrasf(BigDecimal idObject, BigDecimal pgOggettoTrasf) {
        PigObjectTrasf result = null;
        Query query = getEntityManager().createQuery(
                "SELECT t FROM PigObjectTrasf t WHERE t.pigObject.idObject = :idObject AND t.pgOggettoTrasf = :pgOggettoTrasf ");
        query.setParameter("idObject", HibernateUtils.longFrom(idObject));
        query.setParameter("pgOggettoTrasf", pgOggettoTrasf);
        List<PigObjectTrasf> list = query.getResultList();
        if (list != null && !list.isEmpty()) {
            result = list.get(0);
        }
        return result;
    }

    /**
     * Verifica l'esistenza di un record data la chiave unique su idVers e cdKeyObjectTrasf
     *
     * @param idVers
     *            id versamento
     * @param cdKeyObjectTrasf
     *            chiave oggetto
     *
     * @return entity {@link PigObjectTrasf}
     */
    public PigObjectTrasf getPigObjectTrasf(long idVers, String cdKeyObjectTrasf) {
        PigObjectTrasf result = null;
        Query query = getEntityManager().createQuery(
                "SELECT t FROM PigObjectTrasf t WHERE t.pigVer.idVers = :idVers AND t.cdKeyObjectTrasf = :cdKeyObjectTrasf ");
        query.setParameter("idVers", idVers);
        query.setParameter("cdKeyObjectTrasf", cdKeyObjectTrasf);
        List<PigObjectTrasf> list = query.getResultList();
        if (list != null && !list.isEmpty()) {
            result = list.get(0);
        }
        return result;
    }

    public Long countPigObjectFigli(BigDecimal idObjectPadre, String tiStato) {
        String queryStr = "SELECT COUNT(f) FROM PigObject f WHERE f.pigObjectPadre.idObject = :idObjectPadre ";
        if (StringUtils.isNotBlank(tiStato)) {
            queryStr += "AND f.tiStatoObject = :tiStato";
        }
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idObjectPadre", HibernateUtils.longFrom(idObjectPadre));
        if (StringUtils.isNotBlank(tiStato)) {
            query.setParameter("tiStato", tiStato);
        }
        return (Long) query.getSingleResult();
    }

    public boolean existPigDichVersSacer(BigDecimal idVers, BigDecimal idOrganizIam) {
        Query query = getEntityManager().createQuery("SELECT COUNT(dichVersSacer) "
                + "FROM PigDichVersSacer dichVersSacer " + "WHERE dichVersSacer.pigVer.idVers = :idVers "
                + "AND dichVersSacer.idOrganizIam = :idOrganizIam ");
        query.setParameter("idVers", HibernateUtils.longFrom(idVers));
        query.setParameter("idOrganizIam", idOrganizIam);
        Long count = (Long) query.getSingleResult();
        return count > 0L;
    }

    public boolean existPigDichVersSacerTipoObj(BigDecimal idTipoObject, BigDecimal idOrganizIam) {
        Query query = getEntityManager().createQuery(
                "SELECT COUNT(dichVersSacerTipoObj) " + "FROM PigDichVersSacerTipoObj dichVersSacerTipoObj "
                        + "WHERE dichVersSacerTipoObj.pigTipoObject.idTipoObject = :idTipoObject "
                        + "AND dichVersSacerTipoObj.idOrganizIam = :idOrganizIam ");
        query.setParameter("idTipoObject", HibernateUtils.longFrom(idTipoObject));
        query.setParameter("idOrganizIam", idOrganizIam);
        Long count = (Long) query.getSingleResult();
        return count > 0L;
    }

    public void bulkDeletePigDichVersSacerTipoObj(long idTipoObject) {
        String queryStr = "DELETE FROM PigDichVersSacerTipoObj dichVersSacerTipoObj "
                + "WHERE dichVersSacerTipoObj.pigTipoObject.idTipoObject = :idTipoObject ";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idTipoObject", idTipoObject);
        query.executeUpdate();
        getEntityManager().flush();
    }

    public void bulkDeletePigVersTipoObjectDaTrasf(long idTipoObject) {
        String queryStr = "DELETE FROM PigVersTipoObjectDaTrasf dichVersTipoObjectDaTrasf "
                + "WHERE dichVersTipoObjectDaTrasf.pigTipoObjectDaTrasf.idTipoObject = :idTipoObject ";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idTipoObject", idTipoObject);
        query.executeUpdate();
        getEntityManager().flush();
    }

    public UsrUser findUser(String username) {
        Query q = getEntityManager().createQuery("SELECT u FROM UsrUser u WHERE u.nmUserid = :username");
        q.setParameter("username", username);
        return (UsrUser) q.getSingleResult();
    }

    /*
     * Introdotto per l'itegrazione con SPID Puglia dove a fronte del codice fiscale arrivato da SPID andiamo a cercare
     * sulla usruser un utente avente come username il codice fiscale ignorando il case.
     */
    public List<UsrUser> findUtentiPerUsernameCaseInsensitive(String username) {
        Query q = getEntityManager()
                .createQuery("SELECT u FROM UsrUser u WHERE lower(u.nmUserid) = :username  AND u.flAttivo='1'");
        q.setParameter("username", username.toLowerCase());
        return q.getResultList();
    }

    /* Introdotta per lo SPID **/
    public List<UsrUser> findByCodiceFiscale(String codiceFiscale) throws NoResultException {
        Query q = getEntityManager().createQuery(
                "SELECT u FROM UsrUser u WHERE (u.cdFisc = :codiceFiscaleL OR u.cdFisc = :codiceFiscaleU) AND u.flAttivo='1'");
        q.setParameter("codiceFiscaleL", codiceFiscale.toLowerCase());
        q.setParameter("codiceFiscaleU", codiceFiscale.toUpperCase());
        return q.getResultList();
    }

    /**
     * Metodo che ritorna i parametri di configurazione
     *
     * @param tiParamApplic
     *            tipo parametro
     * @param tiGestioneParam
     *            tipo gestione parametro
     * @param flAppartApplic
     *            flag 1/0 (true/false)
     * @param flAppartVers
     *            flag 1/0 (true/false)
     * @param flAppartAmbiente
     *            flag 1/0 (true/false)
     * @param flAppartTipoOggetto
     *            flag 1/0 (true/false)
     *
     * @return lista elementi di tipi {@link PigParamApplic}
     */
    public List<PigParamApplic> getPigParamApplicList(String tiParamApplic, String tiGestioneParam,
            String flAppartApplic, String flAppartAmbiente, String flAppartVers, String flAppartTipoOggetto) {
        StringBuilder queryStr = new StringBuilder("SELECT paramApplic FROM PigParamApplic paramApplic ");
        String whereWord = " WHERE ";
        if (tiParamApplic != null) {
            queryStr.append(whereWord).append("paramApplic.tiParamApplic = :tiParamApplic ");
            whereWord = "AND ";
        }
        if (tiGestioneParam != null) {
            queryStr.append(whereWord).append("paramApplic.tiGestioneParam = :tiGestioneParam ");
            whereWord = "AND ";
        }
        if (flAppartApplic != null) {
            queryStr.append(whereWord).append("paramApplic.flAppartApplic = :flAppartApplic ");
            whereWord = "AND ";
        }
        if (flAppartAmbiente != null) {
            queryStr.append(whereWord).append("paramApplic.flAppartAmbiente = :flAppartAmbiente ");
            whereWord = "AND ";
        }
        if (flAppartVers != null) {
            queryStr.append(whereWord).append("paramApplic.flAppartVers = :flAppartVers ");
            whereWord = "AND ";
        }
        if (flAppartTipoOggetto != null) {
            queryStr.append(whereWord).append("paramApplic.flAppartTipoOggetto = :flAppartTipoOggetto ");
        }
        queryStr.append("ORDER BY paramApplic.tiParamApplic, paramApplic.nmParamApplic ");
        Query q = getEntityManager().createQuery(queryStr.toString());
        if (tiParamApplic != null) {
            q.setParameter("tiParamApplic", tiParamApplic);
        }
        if (tiGestioneParam != null) {
            q.setParameter("tiGestioneParam", tiGestioneParam);
        }
        if (flAppartApplic != null) {
            q.setParameter("flAppartApplic", flAppartApplic);
        }
        if (flAppartAmbiente != null) {
            q.setParameter("flAppartAmbiente", flAppartAmbiente);
        }
        if (flAppartVers != null) {
            q.setParameter("flAppartVers", flAppartVers);
        }
        if (flAppartTipoOggetto != null) {
            q.setParameter("flAppartTipoOggetto", flAppartTipoOggetto);
        }
        return q.getResultList();
    }

    // MEV 32650
    public List<PigParamApplic> getPigParamApplicListAmbiente(List<String> funzione, String tiGestioneParam,
            boolean filterValid) {
        String queryStr = "SELECT paramApplic FROM PigParamApplic paramApplic "
                + "WHERE paramApplic.flAppartAmbiente = '1' ";

        if (funzione != null && !funzione.isEmpty()) {
            queryStr = queryStr + "AND paramApplic.tiParamApplic IN (:funzione) ";
        }
        if (tiGestioneParam != null) {
            queryStr = queryStr + "AND paramApplic.tiGestioneParam = :tiGestioneParam ";
        }
        if (filterValid) {
            queryStr = queryStr + "AND paramApplic.cdVersioneAppFine IS NULL ";
        }
        queryStr = queryStr + "ORDER BY paramApplic.tiParamApplic, paramApplic.nmParamApplic";

        Query q = getEntityManager().createQuery(queryStr);
        if (funzione != null && !funzione.isEmpty()) {
            q.setParameter("funzione", funzione);
        }
        if (tiGestioneParam != null) {
            q.setParameter("tiGestioneParam", tiGestioneParam);
        }
        return q.getResultList();

    }

    public List<PigParamApplic> getPigParamApplicListVers(List<String> funzione, String tiGestioneParam,
            boolean filterValid) {
        String queryStr = "SELECT paramApplic FROM PigParamApplic paramApplic "
                + "WHERE paramApplic.flAppartVers = '1' ";

        if (funzione != null && !funzione.isEmpty()) {
            queryStr = queryStr + "AND paramApplic.tiParamApplic IN (:funzione) ";
        }
        if (tiGestioneParam != null) {
            queryStr = queryStr + "AND paramApplic.tiGestioneParam = :tiGestioneParam ";
        }
        if (filterValid) {
            queryStr = queryStr + "AND paramApplic.cdVersioneAppFine IS NULL ";
        }

        queryStr = queryStr + "ORDER BY paramApplic.tiParamApplic, paramApplic.nmParamApplic";

        Query q = getEntityManager().createQuery(queryStr);

        if (funzione != null && !funzione.isEmpty()) {
            q.setParameter("funzione", funzione);
        }
        if (tiGestioneParam != null) {
            q.setParameter("tiGestioneParam", tiGestioneParam);
        }

        return q.getResultList();
    }

    public List<PigParamApplic> getPigParamApplicListTipoOggetto(List<String> funzione, String tiGestioneParam,
            boolean filterValid) {
        String queryStr = "SELECT paramApplic FROM PigParamApplic paramApplic "
                + "WHERE paramApplic.flAppartTipoOggetto = '1' ";

        if (funzione != null && !funzione.isEmpty()) {
            queryStr = queryStr + "AND paramApplic.tiParamApplic IN (:funzione) ";
        }
        if (tiGestioneParam != null) {
            queryStr = queryStr + "AND paramApplic.tiGestioneParam = :tiGestioneParam ";
        }
        if (filterValid) {
            queryStr = queryStr + "AND paramApplic.cdVersioneAppFine IS NULL ";
        }

        queryStr = queryStr + "ORDER BY paramApplic.tiParamApplic, paramApplic.nmParamApplic";

        Query q = getEntityManager().createQuery(queryStr);

        if (funzione != null && !funzione.isEmpty()) {
            q.setParameter("funzione", funzione);
        }
        if (tiGestioneParam != null) {
            q.setParameter("tiGestioneParam", tiGestioneParam);
        }

        return q.getResultList();
    }

    /**
     * Metodo che ritorna i parametri di configurazione
     *
     * @param tiParamApplic
     *            tipo parametro
     * @param tiGestioneParam
     *            tipo gestione parametro
     * @param flAppartApplic
     *            flag 1/0 (true/false)
     * @param flAppartVers
     *            flag 1/0 (true/false)
     * @param flAppartAmbiente
     *            flag 1/0 (true/false)
     * @param flAppartTipoOggetto
     *            flag 1/0 (true/false)
     * @param filterValid
     *            true o false per filtrare i parametri attivi (sulla base della versione applicativo)
     *
     * @return lista elementi di tipi {@link PigParamApplic}
     */
    public List<PigParamApplic> getPigParamApplicList(String tiParamApplic, String tiGestioneParam,
            String flAppartApplic, String flAppartAmbiente, String flAppartVers, String flAppartTipoOggetto,
            boolean filterValid) {
        StringBuilder queryStr = new StringBuilder("SELECT paramApplic FROM PigParamApplic paramApplic ");
        String whereWord = " WHERE ";
        if (tiParamApplic != null) {
            queryStr.append(whereWord).append("paramApplic.tiParamApplic = :tiParamApplic ");
            whereWord = "AND ";
        }
        if (tiGestioneParam != null) {
            queryStr.append(whereWord).append("paramApplic.tiGestioneParam = :tiGestioneParam ");
            whereWord = "AND ";
        }
        if (flAppartApplic != null) {
            queryStr.append(whereWord).append("paramApplic.flAppartApplic = :flAppartApplic ");
            whereWord = "AND ";
        }
        if (flAppartAmbiente != null) {
            queryStr.append(whereWord).append("paramApplic.flAppartAmbiente = :flAppartAmbiente ");
            whereWord = "AND ";
        }
        if (flAppartVers != null) {
            queryStr.append(whereWord).append("paramApplic.flAppartVers = :flAppartVers ");
            whereWord = "AND ";
        }
        if (flAppartTipoOggetto != null) {
            queryStr.append(whereWord).append("paramApplic.flAppartTipoOggetto = :flAppartTipoOggetto ");
        }
        if (filterValid) {
            queryStr.append(whereWord).append("paramApplic.cdVersioneAppFine IS NULL ");
        }

        queryStr.append("ORDER BY paramApplic.tiParamApplic, paramApplic.nmParamApplic ");
        Query q = getEntityManager().createQuery(queryStr.toString());
        if (tiParamApplic != null) {
            q.setParameter("tiParamApplic", tiParamApplic);
        }
        if (tiGestioneParam != null) {
            q.setParameter("tiGestioneParam", tiGestioneParam);
        }
        if (flAppartApplic != null) {
            q.setParameter("flAppartApplic", flAppartApplic);
        }
        if (flAppartAmbiente != null) {
            q.setParameter("flAppartAmbiente", flAppartAmbiente);
        }
        if (flAppartVers != null) {
            q.setParameter("flAppartVers", flAppartVers);
        }
        if (flAppartTipoOggetto != null) {
            q.setParameter("flAppartTipoOggetto", flAppartTipoOggetto);
        }
        return q.getResultList();
    }

    public boolean existsPigParamApplic(String nmParamApplic, BigDecimal idParamApplic) {
        Query q = getEntityManager().createQuery("SELECT paramApplic FROM PigParamApplic paramApplic "
                + "WHERE paramApplic.nmParamApplic = :nmParamApplic "
                + "AND paramApplic.idParamApplic != :idParamApplic ");
        q.setParameter("nmParamApplic", nmParamApplic);
        q.setParameter("idParamApplic", HibernateUtils.longFrom(idParamApplic));
        return !q.getResultList().isEmpty();
    }

    public PigValoreParamApplic getPigValoreParamApplic(long idParamApplic, String tiAppart) {
        Query q = getEntityManager().createQuery("SELECT valoreParamApplic FROM PigValoreParamApplic valoreParamApplic "
                + "WHERE valoreParamApplic.pigParamApplic.idParamApplic = :idParamApplic "
                + "AND valoreParamApplic.tiAppart = :tiAppart ");
        q.setParameter("idParamApplic", idParamApplic);
        q.setParameter("tiAppart", tiAppart);
        List<PigValoreParamApplic> lista = q.getResultList();
        if (!lista.isEmpty()) {
            return lista.get(0);
        }
        return null;
    }

    /**
     * Metodo che ritorna i tipi di parametri di configurazione
     *
     * @return il tablebean contenente la lista di tipi parametri di configurazione
     */
    public List<String> getTiParamApplic() {
        String queryStr = "SELECT DISTINCT config.tiParamApplic FROM PigParamApplic config ORDER BY config.tiParamApplic ";
        Query q = getEntityManager().createQuery(queryStr);
        return q.getResultList();
    }

    public PigAmbienteVers retrievePigAmbienteVersByVers(BigDecimal idVers) {
        Query query = getEntityManager().createQuery("SELECT ambienteVers FROM PigVers vers "
                + "JOIN vers.pigAmbienteVer ambienteVers " + "WHERE vers.idVers = :idVers ");
        query.setParameter("idVers", HibernateUtils.longFrom(idVers));
        return ((List<PigAmbienteVers>) query.getResultList()).get(0);
    }

    public List<PigParamApplic> getPigParamApplicListVers(List<String> funzione) {
        String queryStr = "SELECT paramApplic FROM PigParamApplic paramApplic "
                + "WHERE paramApplic.flAppartVers = '1' ";

        if (funzione != null && !funzione.isEmpty()) {
            queryStr = queryStr + "AND paramApplic.tiParamApplic IN (:funzione) ";
        }

        queryStr = queryStr + "ORDER BY paramApplic.tiParamApplic, paramApplic.nmParamApplic";

        Query q = getEntityManager().createQuery(queryStr);

        if (funzione != null && !funzione.isEmpty()) {
            q.setParameter("funzione", funzione);
        }

        return q.getResultList();
    }

    public PigValoreParamApplic getPigValoreParamApplic(BigDecimal idParamApplic, String tiAppart,
            BigDecimal idAmbienteVers, BigDecimal idVers, BigDecimal idTipoObject) {
        StringBuilder queryStr = new StringBuilder(
                "SELECT valoreParamApplic FROM PigValoreParamApplic valoreParamApplic "
                        + "WHERE valoreParamApplic.tiAppart = :tiAppart "
                        + "AND valoreParamApplic.pigParamApplic.idParamApplic = :idParamApplic ");

        if (idAmbienteVers != null) {
            queryStr.append("AND valoreParamApplic.pigAmbienteVer.idAmbienteVers = :idAmbienteVers ");
        }
        if (idVers != null) {
            queryStr.append("AND valoreParamApplic.pigVer.idVers = :idVers ");
        }
        if (idTipoObject != null) {
            queryStr.append("AND valoreParamApplic.pigTipoObject.idTipoObject = :idTipoObject ");
        }

        Query q = getEntityManager().createQuery(queryStr.toString());
        q.setParameter("tiAppart", tiAppart);
        q.setParameter("idParamApplic", HibernateUtils.longFrom(idParamApplic));
        if (idAmbienteVers != null) {
            q.setParameter("idAmbienteVers", HibernateUtils.longFrom(idAmbienteVers));
        }
        if (idVers != null) {
            q.setParameter("idVers", HibernateUtils.longFrom(idVers));
        }
        if (idTipoObject != null) {
            q.setParameter("idTipoObject", HibernateUtils.longFrom(idTipoObject));
        }
        List<PigValoreParamApplic> lista = q.getResultList();
        if (!lista.isEmpty()) {
            return lista.get(0);
        }
        return null;
    }

    public List<PigParamApplic> getPigParamApplicListAmbiente(List<String> funzione) {
        String queryStr = "SELECT paramApplic FROM PigParamApplic paramApplic "
                + "WHERE paramApplic.flAppartAmbiente = '1' ";

        if (funzione != null && !funzione.isEmpty()) {
            queryStr = queryStr + "AND paramApplic.tiParamApplic IN (:funzione) ";
        }

        queryStr = queryStr + "ORDER BY paramApplic.tiParamApplic, paramApplic.nmParamApplic";

        Query q = getEntityManager().createQuery(queryStr);

        if (funzione != null && !funzione.isEmpty()) {
            q.setParameter("funzione", funzione);
        }

        return q.getResultList();
    }

    public List<PigParamApplic> getPigParamApplicListTipoOggetto(List<String> funzione) {
        String queryStr = "SELECT paramApplic FROM PigParamApplic paramApplic "
                + "WHERE paramApplic.flAppartTipoOggetto = '1' ";

        if (funzione != null && !funzione.isEmpty()) {
            queryStr = queryStr + "AND paramApplic.tiParamApplic IN (:funzione) ";
        }

        queryStr = queryStr + "ORDER BY paramApplic.tiParamApplic, paramApplic.nmParamApplic";

        Query q = getEntityManager().createQuery(queryStr);

        if (funzione != null && !funzione.isEmpty()) {
            q.setParameter("funzione", funzione);
        }

        return q.getResultList();
    }

    public List<UsrVAbilAmbEnteConvenz> retrieveAmbientiEntiConvenzAbilitati(BigDecimal idUserIam) {
        Query query = getEntityManager()
                .createQuery("SELECT abilAmbEnteConvenz FROM UsrVAbilAmbEnteConvenz abilAmbEnteConvenz "
                        + "WHERE abilAmbEnteConvenz.id.idUserIam = :idUserIam "
                        + "ORDER BY abilAmbEnteConvenz.nmAmbienteEnteConvenz ");
        query.setParameter("idUserIam", idUserIam);
        return query.getResultList();
    }

    public List<OrgVRicEnteConvenzByEsterno> getOrgVRicEnteConvenzList(BigDecimal idUserIamCor,
            BigDecimal idAmbienteEnteConvenz, String tiEnteConvenz, String flNonConvenz) {
        StringBuilder queryStr = new StringBuilder(
                "SELECT ricEnteConvenz FROM OrgVRicEnteConvenzByEsterno ricEnteConvenz "
                        + "WHERE ricEnteConvenz.id.idUserIamCor = :idUserIamCor "
                        + "AND ricEnteConvenz.idAmbienteEnteConvenz = :idAmbienteEnteConvenz ");
        if (tiEnteConvenz != null) {
            queryStr.append("AND ricEnteConvenz.tiEnteConvenz != :tiEnteConvenz ");
        }
        if (flNonConvenz != null) {
            queryStr.append("AND ricEnteConvenz.flNonConvenz = :flNonConvenz ");
        }
        Query query = getEntityManager().createQuery(queryStr.toString());
        if (tiEnteConvenz != null) {
            query.setParameter("tiEnteConvenz", tiEnteConvenz);
        }
        if (flNonConvenz != null) {
            query.setParameter("flNonConvenz", flNonConvenz);
        }
        queryStr.append("ORDER BY ricEnteConvenz.nmEnteConvenz ");
        query.setParameter("idUserIamCor", idUserIamCor);
        query.setParameter("idAmbienteEnteConvenz", idAmbienteEnteConvenz);
        return query.getResultList();
    }

    public List<OrgVRicEnteConvenzByEsterno> getOrgVRicEnteConvenzList(BigDecimal idUserIamCor,
            BigDecimal idAmbienteEnteConvenz, String flNonConvenz) {
        StringBuilder queryStr = new StringBuilder(
                "SELECT ricEnteConvenz FROM OrgVRicEnteConvenzByEsterno ricEnteConvenz "
                        + "WHERE ricEnteConvenz.id.idUserIamCor = :idUserIamCor "
                        + "AND ricEnteConvenz.idAmbienteEnteConvenz = :idAmbienteEnteConvenz ");
        if (flNonConvenz != null) {
            queryStr.append("AND ricEnteConvenz.flNonConvenz = :flNonConvenz ");
        }
        queryStr.append("ORDER BY ricEnteConvenz.nmEnteConvenz ");
        Query query = getEntityManager().createQuery(queryStr.toString());
        if (flNonConvenz != null) {
            query.setParameter("flNonConvenz", flNonConvenz);
        }
        query.setParameter("idUserIamCor", idUserIamCor);
        query.setParameter("idAmbienteEnteConvenz", idAmbienteEnteConvenz);
        return query.getResultList();
    }

    public SIOrgEnteSiam getEnteConvenzConserv(BigDecimal idEnteSiamGestore) {
        Query query = getEntityManager().createQuery("SELECT accordoEnte FROM SIOrgAccordoEnte accordoEnte "
                + "JOIN accordoEnte.siOrgEnteConvenz enteConvenz "
                // + "JOIN accordoEnte.siOrgEnteConvenz enteConvenz "
                + "WHERE enteConvenz.idEnteSiam = :idEnteSiamGestore "
                + "AND :dtCorrente BETWEEN accordoEnte.dtDecAccordo AND accordoEnte.dtScadAccordo ");
        query.setParameter("idEnteSiamGestore", HibernateUtils.longFrom(idEnteSiamGestore));
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        Date dtCorrente = c.getTime();
        query.setParameter("dtCorrente", dtCorrente);
        List<SIOrgAccordoEnte> lista = query.getResultList();
        if (!lista.isEmpty()) {
            return lista.get(0).getOrgEnteSiamByIdEnteConvenzConserv();
        } else {
            return null;
        }
    }

    public SIOrgAmbienteEnteConvenz getSIOrgAmbienteEnteConvenzByEnteConvenz(BigDecimal idEnteConvenz) {
        Query query = getEntityManager().createQuery("SELECT ambienteEnteConvenz FROM SIOrgEnteSiam enteSiam "
                + "JOIN enteSiam.siOrgAmbienteEnteConvenz ambienteEnteConvenz "
                + "WHERE enteSiam.idEnteSiam = :idEnteConvenz ");
        query.setParameter("idEnteConvenz", HibernateUtils.longFrom(idEnteConvenz));
        return (SIOrgAmbienteEnteConvenz) query.getSingleResult();
    }

    public List<SIOrgEnteConvenzOrg> retrieveSIOrgEnteConvenzOrg(BigDecimal idVers) {
        Query query = getEntityManager().createQuery("SELECT enteOrg FROM SIOrgEnteConvenzOrg enteOrg "
                + "WHERE enteOrg.siUsrOrganizIam.idOrganizApplic = :idVers "
                + "AND enteOrg.siUsrOrganizIam.siAplTipoOrganiz.nmTipoOrganiz = 'VERSATORE' "
                + "ORDER BY enteOrg.dtIniVal DESC");
        query.setParameter("idVers", HibernateUtils.longFrom(idVers));
        return query.getResultList();
    }

    public List<OrgVRicEnteNonConvenz> retrieveEntiNonConvenzAbilitati(BigDecimal idUserIam, String tiEnteNonConvenz) {
        StringBuilder queryStr = new StringBuilder(
                "SELECT ricEnteNonConvenz FROM OrgVRicEnteNonConvenz ricEnteNonConvenz ");
        String whereWord = " WHERE ";
        if (idUserIam != null) {
            queryStr.append(whereWord).append("ricEnteNonConvenz.idUserIamCor = :idUserIam ");
            whereWord = " AND ";
        }
        if (tiEnteNonConvenz != null) {
            queryStr.append(whereWord).append("ricEnteNonConvenz.tiEnteNonConvenz = :tiEnteNonConvenz ");
        }
        queryStr.append("ORDER BY ricEnteNonConvenz.nmEnteSiam");
        Query query = getEntityManager().createQuery(queryStr.toString());
        if (idUserIam != null) {
            query.setParameter("idUserIam", idUserIam);
        }
        if (tiEnteNonConvenz != null) {
            query.setParameter("tiEnteNonConvenz", tiEnteNonConvenz);
        }
        return query.getResultList();
    }

    public SIUsrOrganizIam getSIUsrOrganizIam(BigDecimal idVers) {
        List<SIUsrOrganizIam> siuoi;

        String queryStr = "select t from SIUsrOrganizIam t " + "where t.sIAplApplic.nmApplic = :nmApplic "
                + "and t.siAplTipoOrganiz.nmTipoOrganiz = :nmTipoOrganiz "
                + "and t.idOrganizApplic = :idOrganizApplic ";
        javax.persistence.Query query = getEntityManager().createQuery(queryStr, SIUsrOrganizIam.class);
        query.setParameter("nmApplic", "SACER_PREINGEST");
        query.setParameter("nmTipoOrganiz", "VERSATORE");
        query.setParameter("idOrganizApplic", HibernateUtils.longFrom(idVers));
        siuoi = query.getResultList();
        if (siuoi.size() != 1) {
            return null;
        } else {
            return siuoi.get(0);
        }
    }

    /**
     * Controlla che nel periodo compreso tra data di inizio e di fine validità l'ente convenzionato associato al
     * versatore abbia un accordo valido (compreso tra data di inizio e data di fine validità)
     *
     * @param idEnteConvenz
     *            id ente convenzionato
     * @param dtIniVal
     *            data inizio validita
     * @param dtFineVal
     *            data fine validita
     *
     * @return true se esiste accordo valido
     */
    public boolean checkEsistenzaPeriodoValiditaAssociazioneEnteConvenzVers(BigDecimal idEnteConvenz, Date dtIniVal,
            Date dtFineVal) {
        String queryStr = "SELECT enteSiam FROM SIOrgEnteSiam enteSiam " + "WHERE enteSiam.idEnteSiam = :idEnteConvenz "
                + "AND EXISTS (SELECT accordoEnte FROM SIOrgAccordoEnte accordoEnte "
                + "WHERE (accordoEnte.dtDecAccordo <= :dtIniVal AND :dtIniVal <= accordoEnte.dtFineValidAccordo) "
                + "AND (accordoEnte.dtDecAccordo <= :dtFineVal AND :dtFineVal <= accordoEnte.dtFineValidAccordo) "
                + "AND accordoEnte.siOrgEnteConvenz = enteSiam) ";

        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idEnteConvenz", HibernateUtils.longFrom(idEnteConvenz));
        query.setParameter("dtIniVal", dtIniVal);
        query.setParameter("dtFineVal", dtFineVal);
        List<SIOrgEnteConvenzOrg> list = query.getResultList();
        return !list.isEmpty();
    }

    public boolean existsPeriodoValiditaAssociazioneEnteConvenzVersAccordoValido(BigDecimal idEnteConvenz,
            Date dtIniVal, Date dtFineVal) {
        Query query = getEntityManager().createQuery("SELECT accordo FROM SIOrgAccordoEnte accordo "
                + "JOIN  accordo.siOrgEnteConvenz enteSiam " + "WHERE enteSiam.idEnteSiam = :idEnteConvenz "
                + "AND accordo.dtDecAccordo <= :dataOdierna AND accordo.dtFineValidAccordo >= :dataOdierna "
                + "AND accordo.dtDecAccordo <= :dtIniVal AND enteSiam.dtCessazione >= :dtFineVal ");
        query.setParameter("idEnteConvenz", HibernateUtils.longFrom(idEnteConvenz));
        query.setParameter("dataOdierna", new Date());
        query.setParameter("dtIniVal", dtIniVal);
        query.setParameter("dtFineVal", dtFineVal);
        List<SIOrgAccordoEnte> accordoList = query.getResultList();
        return !accordoList.isEmpty();
    }

    public boolean existsAccordoValido(BigDecimal idEnteConvenz) {
        Query query = getEntityManager().createQuery("SELECT accordo FROM SIOrgAccordoEnte accordo "
                + "JOIN  accordo.siOrgEnteConvenz enteSiam " + "WHERE enteSiam.idEnteSiam = :idEnteConvenz "
                + "AND accordo.dtDecAccordo <= :dataOdierna AND accordo.dtFineValidAccordo >= :dataOdierna ");
        query.setParameter("idEnteConvenz", HibernateUtils.longFrom(idEnteConvenz));
        query.setParameter("dataOdierna", new Date());
        List<SIOrgAccordoEnte> accordoList = query.getResultList();
        return !accordoList.isEmpty();
    }

    public List<SIOrgEnteSiam> retrieveSiOrgEnteConvenz(BigDecimal idAmbienteEnteConvenz) {
        String queryStr = "SELECT enteSiam FROM SIOrgEnteSiam enteSiam "
                + "WHERE enteSiam.siOrgAmbienteEnteConvenz.idAmbienteEnteConvenz = :idAmbienteEnteConvenz ORDER BY enteSiam.nmEnteSiam";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idAmbienteEnteConvenz", HibernateUtils.longFrom(idAmbienteEnteConvenz));
        return query.getResultList();
    }

    /**
     * Controlla che nel periodo compreso tra data di inizio e di fine validità il versatore non sia già associato ad un
     * altro ente convenzionato ricercando sulla tabella dell'associazione ovvero SIOrgEnteConvenzOrg
     *
     * @param nmApplic
     *            nome applicazione
     * @param idVers
     *            id versamento
     * @param dtIniVal
     *            data inizio validita
     * @param dtFineVal
     *            data fine validita
     * @param idEnteConvenzOrg
     *            id ente convenzionato per organizzazione
     *
     * @return true se esiste già l'associazione
     */
    public boolean checkEsistenzaAssociazioneEnteConvenzVers(String nmApplic, BigDecimal idVers, Date dtIniVal,
            Date dtFineVal, BigDecimal idEnteConvenzOrg) {
        String queryStr = "SELECT enteConvenzOrg FROM SIOrgEnteConvenzOrg enteConvenzOrg "
                + "WHERE enteConvenzOrg.siUsrOrganizIam.idOrganizApplic = :idVers "
                + "AND enteConvenzOrg.siUsrOrganizIam.siAplTipoOrganiz.nmTipoOrganiz = 'VERSATORE' "
                + "AND enteConvenzOrg.siUsrOrganizIam.sIAplApplic.nmApplic = :nmApplic "
                // O è dentro l'intervallo la data di inizio
                + "AND ((enteConvenzOrg.dtIniVal <= :dtIniVal AND enteConvenzOrg.dtFineVal >= :dtIniVal) "
                // O è dentro l'intervallo la data di fine
                + "OR (enteConvenzOrg.dtIniVal <= :dtFineVal AND enteConvenzOrg.dtFineVal >= :dtFineVal) "
                // Oppure ancora entrambe le date sono esterne all'intervallo ma si sovrappongono ad un altro periodo
                + "OR (enteConvenzOrg.dtIniVal >= :dtIniVal AND enteConvenzOrg.dtFineVal <= :dtFineVal))";

        if (idEnteConvenzOrg != null) {
            queryStr = queryStr + "AND enteConvenzOrg.idEnteConvenzOrg != :idEnteConvenzOrgDaEscludere ";
        }
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idVers", HibernateUtils.longFrom(idVers));
        query.setParameter("nmApplic", nmApplic);
        query.setParameter("dtIniVal", dtIniVal);
        query.setParameter("dtFineVal", dtFineVal);
        if (idEnteConvenzOrg != null) {
            query.setParameter("idEnteConvenzOrgDaEscludere", HibernateUtils.longFrom(idEnteConvenzOrg));
        }
        List<SIOrgEnteConvenzOrg> list = query.getResultList();
        return !list.isEmpty();
    }

    public SIOrgEnteConvenzOrg getSIOrgEnteConvenzOrg(BigDecimal idVers, BigDecimal idEnteConvenz, Date dtIniVal) {
        Query query = getEntityManager().createQuery("SELECT enteOrg FROM SIOrgEnteConvenzOrg enteOrg "
                + "WHERE enteOrg.siUsrOrganizIam.idOrganizApplic = :idVers "
                + "AND enteOrg.siUsrOrganizIam.siAplTipoOrganiz.nmTipoOrganiz = 'VERSATORE' "
                + "AND enteOrg.dtIniVal = :dtIniVal " + "AND enteOrg.siOrgEnteConvenz.idEnteSiam = :idEnteConvenz");
        query.setParameter("idVers", HibernateUtils.longFrom(idVers));
        query.setParameter("idEnteConvenz", HibernateUtils.longFrom(idEnteConvenz));
        query.setParameter("dtIniVal", dtIniVal);
        return (SIOrgEnteConvenzOrg) query.getSingleResult();
    }

    public List<SIOrgEnteConvenzOrg> getSIOrgEnteConvenzOrg(BigDecimal idVers) {
        Query query = getEntityManager().createQuery("SELECT enteOrg FROM SIOrgEnteConvenzOrg enteOrg "
                + "WHERE enteOrg.siUsrOrganizIam.idOrganizApplic = :idVers "
                + "AND enteOrg.siUsrOrganizIam.siAplTipoOrganiz.nmTipoOrganiz = 'VERSATORE' "
                + "ORDER BY enteOrg.dtFineVal DESC ");
        query.setParameter("idVers", HibernateUtils.longFrom(idVers));
        return query.getResultList();
    }

    /**
     * @deprecated enteConvenzDaAllinea non esiste
     *
     * @return lista di {@link IamEnteSiamDaAllinea} enti da allineare
     */
    @Deprecated
    public List<IamEnteSiamDaAllinea> getIamEnteSiamDaAllinea() {
        List<IamEnteSiamDaAllinea> entiList;
        String queryStr = "SELECT enteConvenzDaAllinea FROM IamEnteSiamDaAllinea enteSiamDaAllinea "
                + "WHERE enteSiamDaAllinea.tiStatoAllinea "
                + "IN ('DA_ALLINEARE', 'ALLINEA_IN_TIMEOUT', 'ALLINEA_IN_ERRORE') "
                + "ORDER BY enteSiamDaAllinea.dtLogEnteSiamDaAllinea ";
        javax.persistence.Query query = getEntityManager().createQuery(queryStr);
        entiList = query.getResultList();
        return entiList;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void writeEsitoIamEnteSiamDaAllinea(Long idEnteSiamDaAllinea,
            CostantiAllineaEntiConv.EsitoServizio esitoServizio, String cdErr, String dsErr) {
        IamEnteSiamDaAllinea enteSiamDaAllinea = getEntityManager().find(IamEnteSiamDaAllinea.class,
                idEnteSiamDaAllinea);
        if (esitoServizio != null) {
            switch (esitoServizio) {
            case OK:
                enteSiamDaAllinea.setTiStatoAllinea(CostantiAllineaEntiConv.TiStatoAllinea.ALLINEA_OK.name());
                enteSiamDaAllinea.setCdErr(null);
                enteSiamDaAllinea.setDsMsgErr(null);
                enteSiamDaAllinea.setDtErr(null);
                break;
            case KO:
                switch (cdErr) {
                case CostantiAllineaEntiConv.SERVIZI_ENTE_001:
                case CostantiAllineaEntiConv.SERVIZI_ENTE_002:
                case CostantiAllineaEntiConv.ALLINEA_ENTE_001:
                case CostantiAllineaEntiConv.ERR_666:
                    enteSiamDaAllinea
                            .setTiStatoAllinea(CostantiAllineaEntiConv.TiStatoAllinea.ALLINEA_IN_ERRORE.name());
                    enteSiamDaAllinea.setCdErr(cdErr);
                    enteSiamDaAllinea.setDsMsgErr(dsErr);
                    enteSiamDaAllinea.setDtErr(new Date());
                    break;
                default:
                    break;
                }
                break;
            case NO_RISPOSTA:
                enteSiamDaAllinea.setTiStatoAllinea(CostantiAllineaEntiConv.TiStatoAllinea.ALLINEA_IN_TIMEOUT.name());
                enteSiamDaAllinea.setCdErr(cdErr);
                enteSiamDaAllinea.setDsMsgErr(dsErr);
                enteSiamDaAllinea.setDtErr(new Date());
                break;
            default:
                break;
            }
        }
    }

    /**
     * @deprecated OrgVRicEnteConvenz is not mapped
     *
     * @param idUserIamCor
     *            user
     * @param idAmbienteEnteConvenz
     *            ambiente convenzionato
     *
     * @return lista di {@link SIOrgEnteSiam} enti convenzionati abilitati
     */
    @Deprecated
    public List<SIOrgEnteSiam> getEntiConvenzionatiAbilitati(long idUserIamCor, BigDecimal idAmbienteEnteConvenz) {
        List<SIOrgEnteSiam> entiSiamList = new ArrayList<>();
        String queryStr = "SELECT ricEnteConvenz FROM OrgVRicEnteConvenz ricEnteConvenz "
                + "WHERE ricEnteConvenz.idUserIamCor = :idUserIamCor "
                + "AND ricEnteConvenz.idAmbienteEnteConvenz = :idAmbienteEnteConvenz "
                + "ORDER BY ricEnteConvenz.nmEnteConvenz ";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idAmbienteEnteConvenz", idAmbienteEnteConvenz);
        query.setParameter("idUserIamCor", idUserIamCor);
        List<OrgVRicEnteConvenzByEsterno> ricEnteConvenzList = query.getResultList();

        for (OrgVRicEnteConvenzByEsterno ricEnteConvenz : ricEnteConvenzList) {
            entiSiamList.add(this.findById(SIOrgEnteSiam.class,
                    ricEnteConvenz.getOrgVRicEnteConvenzByEsternoId().getIdEnteConvenz()));
        }
        return entiSiamList;
    }

    public List<OrgVRicEnteConvenzByEsterno> retrieveEntiConvenzAbilitatiAmbiente(BigDecimal idUserIamCor,
            BigDecimal idAmbienteEnteConvenz, String flNonConvenz) {
        String queryStr = "SELECT ricEnteConvenz FROM OrgVRicEnteConvenzByEsterno ricEnteConvenz "
                + "WHERE ricEnteConvenz.id.idUserIamCor = :idUserIamCor AND ricEnteConvenz.idAmbienteEnteConvenz = :idAmbienteEnteConvenz ";
        if (flNonConvenz != null) {
            queryStr = queryStr + "AND ricEnteConvenz.flNonConvenz = :flNonConvenz ";
        }
        queryStr = queryStr + "ORDER BY ricEnteConvenz.nmEnteConvenz ";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idUserIamCor", idUserIamCor);
        query.setParameter("idAmbienteEnteConvenz", idAmbienteEnteConvenz);
        if (flNonConvenz != null) {
            query.setParameter("flNonConvenz", flNonConvenz);
        }
        return query.getResultList();
    }

    public boolean isStoricoPresente(long idVers, long idAmbienteVersExcluded, Date dtIniValAppartAmbiente,
            Date dtFinValAppartAmbiente) {
        String queryStr = "SELECT storicoVersAmbiente FROM PigStoricoVersAmbiente storicoVersAmbiente "
                + "WHERE storicoVersAmbiente.pigVer.idVers = :idVers "
                + "AND storicoVersAmbiente.pigAmbienteVer.idAmbienteVers != :idAmbienteVersExcluded "
                + "AND ((storicoVersAmbiente.dtIniVal <= :dtIniValAppartAmbiente AND storicoVersAmbiente.dtFineVal >= :dtIniValAppartAmbiente) "
                + "OR (storicoVersAmbiente.dtFineVal <= :dtFinValAppartAmbiente AND storicoVersAmbiente.dtFineVal >= :dtFinValAppartAmbiente)) ";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idVers", idVers);
        query.setParameter("idAmbienteVersExcluded", idAmbienteVersExcluded);
        query.setParameter("dtIniValAppartAmbiente", dtIniValAppartAmbiente);
        query.setParameter("dtFinValAppartAmbiente", dtFinValAppartAmbiente);
        return !query.getResultList().isEmpty();
    }

    /**
     * Ritorna la lista degli enti convenzionati collegati all'ente passato in input (escluso se stesso) cui l'utente
     * corrente è abilitato
     *
     * @param idUserIamCor
     *            id user Iam
     * @param idEnteConvenz
     *            id ente convenzionato
     *
     * @return lista elementi di tipo {@link SIOrgEnteSiam}
     */
    public List<SIOrgEnteSiam> getOrgEnteConvenzCollegUserAbilList(BigDecimal idUserIamCor, BigDecimal idEnteConvenz) {
        String queryStr = "SELECT appart2.orgEnteSiam FROM UsrVAbilEnteConvenz abilEnteConvenz, SIOrgEnteSiam ente, "
                + "OrgAppartCollegEnti appart1, OrgAppartCollegEnti appart2 "
                + "WHERE abilEnteConvenz.id.idUserIam = :idUserIamCor "
                + "AND abilEnteConvenz.id.idEnteConvenz = ente.idEnteSiam "
                + "AND appart1.orgCollegEntiConvenz = appart2.orgCollegEntiConvenz "
                + "AND appart1.orgEnteSiam.idEnteSiam = ente.idEnteSiam "
                + "AND appart2.orgEnteSiam <> appart1.orgEnteSiam " + "AND ente.idEnteSiam = :idEnteConvenz "
                + "ORDER BY ente.nmEnteSiam";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idUserIamCor", idUserIamCor);
        query.setParameter("idEnteConvenz", HibernateUtils.longFrom(idEnteConvenz));
        return query.getResultList();
    }

    public List<OrgAppartCollegEnti> retrieveOrgAppartCollegEntiByIdEnteConvenz(BigDecimal idEnteConvenz) {
        String queryStr = "SELECT appartCollegEnti FROM OrgAppartCollegEnti appartCollegEnti "
                + "WHERE appartCollegEnti.orgEnteSiam.idEnteSiam = :idEnteConvenz ";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idEnteConvenz", HibernateUtils.longFrom(idEnteConvenz));
        return query.getResultList();
    }

    public List<String> getFunzioneParametri() {
        Query query = getEntityManager().createQuery(
                "SELECT DISTINCT paramApplic.tiParamApplic FROM PigParamApplic paramApplic ORDER BY paramApplic.tiParamApplic");
        return query.getResultList();
    }

    public List<SIOrgEnteSiam> getEnteConvenzConservList(long idUserIamCor, BigDecimal idEnteSiamGestore) {
        Query query = getEntityManager().createQuery(
                "SELECT DISTINCT enteConvenzConserv FROM OrgVRicEnteConvenzByEsterno ricEnteConvenzByEst, SIOrgAccordoEnte accordoEnte "
                        + "JOIN accordoEnte.siOrgEnteConvenz enteConvenz "
                        + "JOIN accordoEnte.orgEnteSiamByIdEnteConvenzGestore enteConvenzGestore "
                        + "JOIN accordoEnte.orgEnteSiamByIdEnteConvenzConserv enteConvenzConserv "
                        + "WHERE ricEnteConvenzByEst.id.idEnteConvenz = enteConvenz.idEnteSiam "
                        + "AND ricEnteConvenzByEst.id.idUserIamCor = :idUserIamCor "
                        + "AND enteConvenzGestore.idEnteSiam = :idEnteSiamGestore "
                        + "AND :dtCorrente BETWEEN accordoEnte.dtDecAccordo AND accordoEnte.dtFineValidAccordo ");
        query.setParameter("idUserIamCor", HibernateUtils.bigDecimalFrom(idUserIamCor));
        query.setParameter("idEnteSiamGestore", HibernateUtils.longFrom(idEnteSiamGestore));
        query.setParameter("dtCorrente", new Date());
        return query.getResultList();
    }

    /**
     * Ritorna l'accordo valido alla data corrente per l'ente convenzionato dato in input
     *
     * @param idEnteConvenz
     *            id ente convenzionato
     *
     * @return la lista di accordi
     */
    public SIOrgAccordoEnte retrieveOrgAccordoEnteValido(BigDecimal idEnteConvenz) {
        Query query = getEntityManager()
                .createQuery("SELECT s FROM SIOrgAccordoEnte s WHERE s.siOrgEnteConvenz.idEnteSiam = :idEnteConvenz "
                        + "AND s.dtDecAccordo <= :dataOdierna AND s.dtFineValidAccordo >= :dataOdierna ");
        query.setParameter("idEnteConvenz", HibernateUtils.longFrom(idEnteConvenz));
        query.setParameter("dataOdierna", new Date());
        List<SIOrgAccordoEnte> accordoList = query.getResultList();
        if (!accordoList.isEmpty() && accordoList.get(0) != null) {
            return accordoList.get(0);
        }
        return null;
    }

    public List<UsrVAbilStrutSacerXping> getOrganizzazioniSacerFromUsrVAbilStrutSacerXping(long idUserIam,
            String tiDichVers) {
        String chiave = tiDichVers.equals("STRUTTURA") ? "organiz.idOrganizIamStrut"
                : tiDichVers.equals("ENTE") ? "organiz.idOrganizIamEnte" : "organiz.idOrganizIamAmbiente";
        StringBuilder queryStr = new StringBuilder(
                "SELECT new it.eng.sacerasi.viewEntity.UsrVAbilStrutSacerXping(" + chiave + " ");
        String orderType = "";
        switch (tiDichVers) {
        case "AMBIENTE":
            queryStr.append(" ,'AMBIENTE', organiz.dlCompositoOrganiz) ");
            orderType = " organiz.nmAmbiente ";
            break;
        case "ENTE":
            queryStr.append(" ,'ENTE', organiz.dlCompositoOrganiz) ");
            orderType = " organiz.nmEnte ";
            break;
        case "STRUTTURA":
            queryStr.append(" ,'STRUTTURA', organiz.dlCompositoOrganiz) ");
            orderType = " organiz.nmStrut ";
            break;
        default:
            break;
        }

        queryStr.append(
                "FROM UsrVAbilStrutSacerXping organiz WHERE organiz.id.idUserIam = :idUserIam ORDER BY :orderType ");

        Query q = getEntityManager().createQuery(queryStr.toString());
        q.setParameter("idUserIam", HibernateUtils.bigDecimalFrom(idUserIam));
        q.setParameter("orderType", orderType);
        return q.getResultList();
    }

    /**
     * Conto quante righe diverse ho in base agli idEnteConvenz
     *
     * @deprecated
     *
     * @param idEnte
     *            id ente
     *
     * @return conteggio
     */
    @Deprecated
    public Integer countStrutConStessoEnte(long idEnte) {
        Query query = getEntityManager().createQuery(
                "SELECT COUNT(ente) FROM OrgEnte ente WHERE ente.idEnte = :idEnte GROUP BY ente.orgStrut.idEnteConvenz ");
        query.setParameter("idEnte", idEnte);
        return (Integer) query.getSingleResult();
    }

    public List<OrgAppartCollegEnti> retrieveOrgAppartCollegEnti(BigDecimal idCollegEntiConvenz) {
        String queryStr = "SELECT appartCollegEnti FROM OrgAppartCollegEnti appartCollegEnti "
                + "WHERE appartCollegEnti.orgCollegEntiConvenz.idCollegEntiConvenz = :idCollegEntiConvenz ";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idCollegEntiConvenz", HibernateUtils.longFrom(idCollegEntiConvenz));
        return query.getResultList();
    }

    public OrgVRicEnteConvenzByEsterno findDistinctByIdEnteConvenz(BigDecimal idEnteConvenz) {
        final Query query = getEntityManager().createQuery(
                "SELECT DISTINCT new it.eng.sacerasi.grantEntity.OrgVRicEnteConvenzByEsterno(ente.id.idEnteConvenz, ente.nmEnteConvenz,"
                        + " ente.idCategEnte," + "ente.idAmbitoTerrit," + "ente.dtCessazione," + "ente.dtIniVal,"
                        + "ente.flRecesso," + "ente.enteAttivo," + "ente.idAmbienteEnteConvenz," + "ente.dtDecAccordo,"
                        + "ente.dtScadAccordo," + "ente.flInCorsoConvenz," + "ente.flNonConvenz,"
                        + "ente.tiEnteConvenz," + "ente.idEnteConserv," + "ente.idEnteGestore,"
                        + "ente.dtFineValidAccordo)"
                        + " FROM OrgVRicEnteConvenzByEsterno ente WHERE ente.id.idEnteConvenz = :idEnteConvenz",
                OrgVRicEnteConvenzByEsterno.class);
        query.setParameter("idEnteConvenz", idEnteConvenz);
        List<OrgVRicEnteConvenzByEsterno> list = query.getResultList();
        if (list.size() == 1) {
            return list.get(0);
        } else {
            if (list.isEmpty()) {
                return null;
            } else {
                throw new RuntimeException(
                        "Sono presenti più record OrgVRicEnteConvenzByEsterno per idEnteConvenz " + idEnteConvenz);
            }
        }
    }
}
