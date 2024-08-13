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

import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.Query;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;

import it.eng.sacerasi.entity.*;
import it.eng.sacerasi.web.util.Constants.ComboFlagPrioVersType;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.paginator.util.HibernateUtils;
import it.eng.sacerasi.common.Constants;
import it.eng.sacerasi.helper.GenericHelper;
import it.eng.sacerasi.slite.gen.form.MonitoraggioForm.FiltriJobSchedulati;
import it.eng.sacerasi.slite.gen.form.MonitoraggioForm.FiltriReplicaOrg;
import it.eng.sacerasi.slite.gen.tablebean.PigAmbienteVersRowBean;
import it.eng.sacerasi.slite.gen.tablebean.PigInfoDicomRowBean;
import it.eng.sacerasi.slite.gen.tablebean.PigObjectRowBean;
import it.eng.sacerasi.slite.gen.tablebean.PigObjectTableBean;
import it.eng.sacerasi.slite.gen.tablebean.PigSessioneIngestRowBean;
import it.eng.sacerasi.slite.gen.viewbean.IamVLisOrganizDaReplicRowBean;
import it.eng.sacerasi.slite.gen.viewbean.IamVLisOrganizDaReplicTableBean;
import it.eng.sacerasi.slite.gen.viewbean.MonVLisFileObjectTableBean;
import it.eng.sacerasi.slite.gen.viewbean.MonVLisObjNonVersRowBean;
import it.eng.sacerasi.slite.gen.viewbean.MonVLisObjNonVersTableBean;
import it.eng.sacerasi.slite.gen.viewbean.MonVLisObjRowBean;
import it.eng.sacerasi.slite.gen.viewbean.MonVLisObjTableBean;
import it.eng.sacerasi.slite.gen.viewbean.MonVLisSchedJobRowBean;
import it.eng.sacerasi.slite.gen.viewbean.MonVLisSchedJobTableBean;
import it.eng.sacerasi.slite.gen.viewbean.MonVLisSesErrateRowBean;
import it.eng.sacerasi.slite.gen.viewbean.MonVLisSesErrateTableBean;
import it.eng.sacerasi.slite.gen.viewbean.MonVLisUnitaDocObjectRowBean;
import it.eng.sacerasi.slite.gen.viewbean.MonVLisUnitaDocObjectTableBean;
import it.eng.sacerasi.slite.gen.viewbean.MonVLisUnitaDocObjectTableDescriptor;
import it.eng.sacerasi.slite.gen.viewbean.MonVLisUnitaDocSessioneRowBean;
import it.eng.sacerasi.slite.gen.viewbean.MonVLisUnitaDocSessioneTableBean;
import it.eng.sacerasi.slite.gen.viewbean.MonVLisUnitaDocSessioneTableDescriptor;
import it.eng.sacerasi.slite.gen.viewbean.MonVLisVersFallitiRowBean;
import it.eng.sacerasi.slite.gen.viewbean.MonVLisVersFallitiTableBean;
import it.eng.sacerasi.slite.gen.viewbean.MonVLisVersObjNonVersTableBean;
import it.eng.sacerasi.slite.gen.viewbean.MonVLisVersObjRowBean;
import it.eng.sacerasi.slite.gen.viewbean.MonVLisVersObjTableBean;
import it.eng.sacerasi.slite.gen.viewbean.MonVObjAnnulRangeDtRowBean;
import it.eng.sacerasi.slite.gen.viewbean.MonVObjAnnulRangeDtTableBean;
import it.eng.sacerasi.slite.gen.viewbean.MonVObjNonVersTableBean;
import it.eng.sacerasi.slite.gen.viewbean.MonVObjRangeDtRowBean;
import it.eng.sacerasi.slite.gen.viewbean.MonVObjRangeDtTableBean;
import it.eng.sacerasi.slite.gen.viewbean.MonVRiepVersRowBean;
import it.eng.sacerasi.slite.gen.viewbean.MonVRiepVersTableBean;
import it.eng.sacerasi.slite.gen.viewbean.MonVSesRangeDtRowBean;
import it.eng.sacerasi.slite.gen.viewbean.MonVSesRangeDtTableBean;
import it.eng.sacerasi.slite.gen.viewbean.MonVVisLastSchedJobRowBean;
import it.eng.sacerasi.slite.gen.viewbean.MonVVisObjNonVersRowBean;
import it.eng.sacerasi.slite.gen.viewbean.MonVVisObjRowBean;
import it.eng.sacerasi.slite.gen.viewbean.MonVVisSesErrataRowBean;
import it.eng.sacerasi.slite.gen.viewbean.MonVVisUnitaDocObjectRowBean;
import it.eng.sacerasi.slite.gen.viewbean.MonVVisVersFallitoRowBean;
import it.eng.sacerasi.viewEntity.IamVLisOrganizDaReplic;
import it.eng.sacerasi.viewEntity.MonVLisFileObject;
import it.eng.sacerasi.viewEntity.MonVLisObj;
import it.eng.sacerasi.viewEntity.MonVLisObjNonVers;
import it.eng.sacerasi.viewEntity.MonVLisObjTrasf;
import it.eng.sacerasi.viewEntity.MonVLisSchedJob;
import it.eng.sacerasi.viewEntity.MonVLisSesErrate;
import it.eng.sacerasi.viewEntity.MonVLisUnitaDocObject;
import it.eng.sacerasi.viewEntity.MonVLisUnitaDocSessione;
import it.eng.sacerasi.viewEntity.MonVLisVersFalliti;
import it.eng.sacerasi.viewEntity.MonVLisVersObj;
import it.eng.sacerasi.viewEntity.MonVLisVersObjNonVers;
import it.eng.sacerasi.viewEntity.MonVRiepVers;
import it.eng.sacerasi.viewEntity.MonVVisLastSchedJob;
import it.eng.sacerasi.viewEntity.MonVVisObj;
import it.eng.sacerasi.viewEntity.MonVVisObjNonVers;
import it.eng.sacerasi.viewEntity.MonVVisSesErrata;
import it.eng.sacerasi.viewEntity.MonVVisUnitaDocObject;
import it.eng.sacerasi.viewEntity.MonVVisVersFallito;
import it.eng.sacerasi.web.dto.MonitoraggioFiltriListaOggDerVersFallitiBean;
import it.eng.sacerasi.web.dto.MonitoraggioFiltriListaOggettiBean;
import it.eng.sacerasi.web.dto.MonitoraggioFiltriListaVersFallitiBean;
import it.eng.sacerasi.web.util.Transform;
import it.eng.sacerasi.web.util.Utils;
import it.eng.spagoCore.error.EMFError;
import it.eng.spagoLite.db.base.BaseTableInterface;
import it.eng.spagoLite.db.base.row.BaseRow;
import it.eng.spagoLite.db.base.sorting.SortingRule;
import it.eng.spagoLite.db.base.table.BaseTable;

/**
 *
 * @author Gilioli_P
 */
@SuppressWarnings("unchecked")
@Stateless
@LocalBean
public class MonitoraggioHelper extends GenericHelper {

    Logger log = LoggerFactory.getLogger(MonitoraggioHelper.class);
    StringReader tmpReader;

    private static final String CD_KEY_OBJECT = "cdKeyObject";

    /**
     * Recupera il table bean con le info di Riepilogo per Versatore
     *
     * @param idVersList
     *            lista elementi di tipo Object
     *
     * @return entity bean {@link MonVRiepVersTableBean}
     */
    public MonVRiepVersTableBean getMonVRiepVersViewBean(List<Object> idVersList) {
        String queryStr = "SELECT u FROM MonVRiepVers u "// WHERE u.idUsoUserApplic = :idusouserapplic "
                + "WHERE u.idVers IN (:idVersList) AND u.flCessato != 1 " + "ORDER BY u.nmAmbienteVers, u.nmVers";

        // CREO LA QUERY ATTRAVERSO L'ENTITY MANAGER
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idVersList", idVersList.stream().map(BigDecimal.class::cast).collect(Collectors.toList()));

        // ESEGUO LA QUERY E PIAZZO I RISULTATI IN UNA LISTA
        List<MonVRiepVers> listaMon = query.getResultList();
        MonVRiepVersTableBean monTableBean = new MonVRiepVersTableBean();

        try {
            if (listaMon != null && !listaMon.isEmpty()) {
                monTableBean = (MonVRiepVersTableBean) Transform.entities2TableBean(listaMon);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        // Ridefinisco il campo versatore affinchÃ© contenga ambiente versatore e versatore
        for (MonVRiepVersRowBean rb : monTableBean) {
            rb.setNmVers(rb.getNmAmbienteVers() + ", " + rb.getNmVers());
        }
        return monTableBean;
    }

    public MonVObjRangeDtTableBean getMonVObjRangeDtTableBean(Long idUser, BigDecimal idAmbienteVers, BigDecimal idVers,
            BigDecimal idTipoObject, BigDecimal idObject, String cdKeyObject, String tiClasseVersFile) {
        StringBuilder queryStr = new StringBuilder("SELECT vista.tiStatoObject, vista.tiDtCreazione, count(vista) "
                + "from IamAbilOrganiz abilOrganiz, MonVObjRangeDt vista, PigVers vers "
                + "WHERE vista.idVers = abilOrganiz.idOrganizApplic " + "AND vers.idVers = vista.idVers "
                + "AND abilOrganiz.iamUser.idUserIam = :idUser " + "AND vista.tiClasseVersFile = :tiClasseVersFile ");

        // Se ho solo il campo idAmbieteVers settato
        if (idAmbienteVers != null && idVers == null && idTipoObject == null) {
            queryStr.append("and vers.pigAmbienteVer.idAmbienteVers = :idAmbienteVers ");
        } else if (idAmbienteVers != null && idVers != null && idTipoObject == null) {
            queryStr.append("and abilOrganiz.idOrganizApplic = :idVers ");
        } else {
            queryStr.append("and abilOrganiz.idOrganizApplic = :idVers ");
            queryStr.append("and vista.idTipoObject = :idTipoObject ");
        }

        // MEV 26979
        if (idObject != null) {
            queryStr.append("and vista.idObject = :idObject ");
        }
        // MEV 26979
        if (cdKeyObject != null && !cdKeyObject.isEmpty()) {
            queryStr.append("and LOWER(vista.cdKeyObject) like LOWER(:cdKeyObject) ");
        }

        queryStr.append("GROUP BY vista.tiStatoObject, vista.tiDtCreazione ");

        Query query = getEntityManager().createQuery(queryStr.toString());
        query.setParameter("idUser", idUser);
        query.setParameter("tiClasseVersFile", tiClasseVersFile);

        if (idAmbienteVers != null && idVers == null && idTipoObject == null) {
            query.setParameter("idAmbienteVers", HibernateUtils.longFrom(idAmbienteVers));
        } else if (idAmbienteVers != null && idVers != null && idTipoObject == null) {
            query.setParameter("idVers", idVers);
        } else {
            query.setParameter("idVers", idVers);
            query.setParameter("idTipoObject", idTipoObject);
        }

        // MEV 26979
        if (idObject != null) {
            query.setParameter("idObject", idObject);
        }
        // MEV 26979
        if (cdKeyObject != null && !cdKeyObject.isEmpty()) {
            query.setParameter(CD_KEY_OBJECT, "%" + cdKeyObject + "%");
        }

        List<Object[]> contaOggetti = query.getResultList();
        MonVObjRangeDtTableBean contaOggettiTableBean = new MonVObjRangeDtTableBean();

        try {
            // trasformo la lista di Object[] (risultante della query) in un tablebean
            for (Object[] row : contaOggetti) {
                MonVObjRangeDtRowBean rowBean = new MonVObjRangeDtRowBean();
                rowBean.setTiStatoObject(row[0] != null ? row[0].toString() : null);
                rowBean.setTiDtCreazione(row[1] != null ? row[1].toString() : null);
                rowBean.setBigDecimal("ni_ogg_vers", row[2] != null ? new BigDecimal((Long) row[2]) : BigDecimal.ZERO);
                contaOggettiTableBean.add(rowBean);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return contaOggettiTableBean;
    }

    public MonVObjAnnulRangeDtTableBean getMonVObjAnnulRangeDtTableBean(Long idUser, BigDecimal idAmbienteVers,
            BigDecimal idVers, BigDecimal idTipoObject) {
        StringBuilder queryStr = new StringBuilder("SELECT vista.tiStatoObject, vista.tiDtCreazione, count(vista) "
                + "from IamAbilOrganiz abilOrganiz, MonVObjAnnulRangeDt vista, PigVers vers "
                + "WHERE vista.idVers = abilOrganiz.idOrganizApplic " + "AND vers.idVers = vista.idVers "
                + "AND abilOrganiz.iamUser.idUserIam = :idUser ");

        // Se ho solo il campo idAmbieteVers settato
        if (idAmbienteVers != null && idVers == null && idTipoObject == null) {
            queryStr.append("and vers.pigAmbienteVer.idAmbienteVers = :idAmbienteVers ");
        } else if (idAmbienteVers != null && idVers != null && idTipoObject == null) {
            queryStr.append("and abilOrganiz.idOrganizApplic = :idVers ");
        } else {
            queryStr.append("and abilOrganiz.idOrganizApplic = :idVers ");
            queryStr.append("and vista.idTipoObject = :idTipoObject ");
        }

        queryStr.append("GROUP BY vista.tiStatoObject, vista.tiDtCreazione ");

        Query query = getEntityManager().createQuery(queryStr.toString());
        query.setParameter("idUser", idUser);

        if (idAmbienteVers != null && idVers == null && idTipoObject == null) {
            query.setParameter("idAmbienteVers", HibernateUtils.longFrom(idAmbienteVers));
        } else if (idAmbienteVers != null && idVers != null && idTipoObject == null) {
            query.setParameter("idVers", idVers);
        } else {
            query.setParameter("idVers", idVers);
            query.setParameter("idTipoObject", idTipoObject);
        }

        List<Object[]> contaOggetti = query.getResultList();
        MonVObjAnnulRangeDtTableBean contaOggettiTableBean = new MonVObjAnnulRangeDtTableBean();

        try {
            // trasformo la lista di Object[] (risultante della query) in un tablebean
            for (Object[] row : contaOggetti) {
                MonVObjAnnulRangeDtRowBean rowBean = new MonVObjAnnulRangeDtRowBean();
                rowBean.setTiStatoObject(row[0] != null ? row[0].toString() : null);
                rowBean.setTiDtCreazione(row[1] != null ? row[1].toString() : null);
                rowBean.setBigDecimal("ni_ogg_vers", row[2] != null ? new BigDecimal((Long) row[2]) : BigDecimal.ZERO);
                contaOggettiTableBean.add(rowBean);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return contaOggettiTableBean;
    }

    public MonVSesRangeDtTableBean getMonVSesRangeDtTableBean(Long idUser, BigDecimal idAmbienteVers, BigDecimal idVers,
            String nmTipoObject) {

        StringBuilder queryStr = new StringBuilder(
                "SELECT vista.tiStato, vista.tiStatoRisoluz, vista.flVerif, vista.flNonRisolub, vista.tiDtCreazione, count(vista) "
                        + "from IamAbilOrganiz abilOrganiz, MonVSesRangeDt vista, PigVers vers "
                        + "WHERE vista.idVers = abilOrganiz.idOrganizApplic " + "AND vers.idVers = vista.idVers "
                        + "AND abilOrganiz.iamUser.idUserIam = :idUser ");

        // Se ho solo il campo idAmbieteVers settato
        if (idAmbienteVers != null && idVers == null && nmTipoObject == null) {
            queryStr.append("and vers.pigAmbienteVer.idAmbienteVers = :idAmbienteVers ");
        } else if (idAmbienteVers != null && idVers != null && nmTipoObject == null) {
            queryStr.append("and abilOrganiz.idOrganizApplic = :idVers ");
        } else {
            queryStr.append("and abilOrganiz.idOrganizApplic = :idVers ");
            queryStr.append("and vista.nmTipoObject = :nmTipoObject ");
        }

        queryStr.append(
                "GROUP BY vista.tiStato, vista.tiStatoRisoluz, vista.flVerif, vista.flNonRisolub, vista.tiDtCreazione ");

        Query query = getEntityManager().createQuery(queryStr.toString());
        query.setParameter("idUser", idUser);

        if (idAmbienteVers != null && idVers == null && nmTipoObject == null) {
            query.setParameter("idAmbienteVers", HibernateUtils.longFrom(idAmbienteVers));
        } else if (idAmbienteVers != null && idVers != null && nmTipoObject == null) {
            query.setParameter("idVers", idVers);
        } else {
            query.setParameter("idVers", idVers);
            query.setParameter("nmTipoObject", nmTipoObject);
        }

        List<Object[]> contaOggetti = query.getResultList();
        MonVSesRangeDtTableBean contaInviiFallitiTableBean = new MonVSesRangeDtTableBean();

        try {
            // trasformo la lista di Object[] (risultante della query) in un tablebean
            for (Object[] row : contaOggetti) {
                MonVSesRangeDtRowBean rowBean = new MonVSesRangeDtRowBean();
                rowBean.setTiStato(row[0] != null ? row[0].toString() : null);
                rowBean.setTiStatoRisoluz(row[1] != null ? row[1].toString() : null);
                rowBean.setFlVerif(row[2] != null ? row[2].toString() : null);
                rowBean.setFlNonRisolub(row[3] != null ? row[3].toString() : null);
                rowBean.setTiDtCreazione(row[4] != null ? row[4].toString() : null);
                rowBean.setBigDecimal("ni_invii_fall",
                        row[5] != null ? new BigDecimal((Long) row[5]) : BigDecimal.ZERO);
                contaInviiFallitiTableBean.add(rowBean);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return contaInviiFallitiTableBean;
    }

    /**
     * Restituisce in un tablebean la lista delle Sessioni Errate dell'omonima pagina in base ai filtri di ricerca
     *
     * @param flVerificato
     *            l'unico filtro di ricerca utilizzato
     * @param maxResults
     *            massimo risultati
     *
     * @return entity bean {@link MonVLisSesErrateTableBean}
     */
    public MonVLisSesErrateTableBean getSessioniErrateListTB(String flVerificato, int maxResults) {
        MonVLisSesErrateTableBean sessioniErrateTableBean = new MonVLisSesErrateTableBean();

        StringBuilder queryStr = new StringBuilder("SELECT ses FROM MonVLisSesErrate ses");

        // Inserimento nella query del filtro flVerificato
        if (flVerificato != null) {
            queryStr.append(" WHERE ses.flVerif = :flVerificato ");
        }
        queryStr.append(" ORDER BY ses.dtApertura DESC ");
        Query query = getEntityManager().createQuery(queryStr.toString());
        if (flVerificato != null) {
            query.setParameter("flVerificato", flVerificato);
        }
        query.setMaxResults(maxResults);
        List<MonVLisSesErrate> listSession = query.getResultList();

        if (listSession != null && !listSession.isEmpty()) {
            try {
                sessioniErrateTableBean = (MonVLisSesErrateTableBean) Transform.entities2TableBean(listSession);
            } catch (Exception ex) {
                log.error(ex.getMessage(), ex);
            }

            sessioniErrateTableBean.first();
            int c = 0;
            for (MonVLisSesErrateRowBean row : sessioniErrateTableBean) {

                row.setString("versatore", row.getNmAmbienteVers() + " , " + row.getNmVers());
                String xmlDati = listSession.get(c).getBlXml();

                /*
                 * Quindi andrÃ  aggiunto il controllo sul nome del tipo oggetto, per verificare se si tratta di uno
                 * StudioDicom Aggiungere anche un ramo else all'if sullo xml per inserire NON CALCOLABILE nei campi nel
                 * caso fossimo in presenza di uno StudioDicom senza xml
                 */
                if (xmlDati != null) {

                    String nmPaz = "NON DEFINITO";
                    String dataNascita = "NON DEFINITO";
                    String dataStudio = "NON DEFINITO";

                    tmpReader = new StringReader(xmlDati);

                    int nmPazStart = xmlDati.indexOf("<PatientName>");
                    int nmPazStop = xmlDati.indexOf("</PatientName>");
                    int dataNascitaStart = xmlDati.indexOf("<PatientBirthDate>");
                    int dataNascitaStop = xmlDati.indexOf("</PatientBirthDate>");
                    int dataStudioStart = xmlDati.indexOf("<StudyDate>");
                    int dataStudioStop = xmlDati.indexOf("</StudyDate>");

                    if (nmPazStart != -1 && nmPazStop != -1) {
                        nmPaz = xmlDati.substring(nmPazStart + ("<PatientName>").length(), nmPazStop);
                    }
                    if (dataNascitaStart != -1 && dataNascitaStop != -1) {
                        dataNascita = xmlDati.substring(dataNascitaStart + ("<PatientBirthDate>").length(),
                                dataNascitaStop);
                    }
                    if (dataStudioStart != -1 && dataStudioStop != -1) {
                        dataStudio = xmlDati.substring(dataStudioStart + ("<StudyDate>").length(), dataStudioStop);
                    }

                    row.setString("info_ogg",
                            "Paziente " + nmPaz + " nato il " + dataNascita + " Studio del " + dataStudio);
                }
                c++;
            }
        }
        return sessioniErrateTableBean;
    }

    public void saveFlVerificati(BigDecimal idSesErr, String flSesErrVerif) {

        String queryStr = "SELECT ses FROM  PigSessioneIngest ses WHERE ses.idSessioneIngest = :idSessioneIngest";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idSessioneIngest", idSesErr.longValue());

        List<PigSessioneIngest> sessioneVers = query.getResultList();
        PigSessioneIngest pigSessioneIngest = sessioneVers.get(0);
        pigSessioneIngest.setFlSesErrVerif(flSesErrVerif);

        try {
            getEntityManager().merge(pigSessioneIngest);
            getEntityManager().flush();
        } catch (RuntimeException re) {
            log.error("Eccezione nella persistenza del ", re);
        }
    }

    /**
     * Recupera il tablebean con i dati da visualizzare nella pagina Lista Documenti
     *
     * @param filtri
     *            i filtri di ricerca riportati dalla pagina precedente
     *
     * @return entity bean {@link MonVLisObjTableBean}
     */
    public MonVLisObjTableBean getMonVLisObjViewBean(MonitoraggioFiltriListaOggettiBean filtri) {
        String whereWord = "WHERE ";
        StringBuilder queryStr = new StringBuilder("SELECT DISTINCT new it.eng.sacerasi.viewEntity.MonVLisObj "
                + "(vista.nmAmbienteVers, vista.nmVers, vista.idObject, vista.tiStatoObject, vista.tiStatoObjectVis, vista.tiStatoVerificaHash, "
                + "vista.cdKeyObject, vista.dtVers, vista.dsInfoObject, vista.dtStatoCor, vista.niSizeFileVers, vista.nmTipoObject, vista.note, "
                + "vista.trasformazioneUtilizzata, vista.niUdProdotte, vista.tiVersFile, vista.dsKeyOrd, vista.tiGestOggettiFigli) "
                + "FROM MonVLisObj vista, IamAbilOrganiz abilOrganiz ");

        // Inserimento nella query del filtro id ambiente versatore
        BigDecimal idAmbienteVers = filtri.getIdAmbienteVers();
        if (idAmbienteVers != null) {
            queryStr.append(whereWord).append("vista.idAmbienteVers = :idAmbienteVers ");
            whereWord = "AND ";
        }
        // Inserimento nella query del filtro id versatore
        BigDecimal idVers = filtri.getIdVers();
        if (idVers != null) {
            queryStr.append(whereWord).append("vista.idVers = :idVers ");
            whereWord = "AND ";
        }
        // Inserimento nella query del filtro id tipo object
        BigDecimal idTipoObject = filtri.getIdTipoObject();
        if (idTipoObject != null) {
            queryStr.append(whereWord).append("vista.idTipoObject = :idTipoObject ");
            whereWord = "AND ";
        }

        // GESTIONE PERIODO - GIORNO
        Calendar dataDBa = Calendar.getInstance();
        Calendar dataDBda = Calendar.getInstance();
        dataDBda.set(Calendar.HOUR_OF_DAY, 0);
        dataDBda.set(Calendar.MINUTE, 0);
        dataDBda.set(Calendar.SECOND, 0);
        dataDBda.set(Calendar.MILLISECOND, 0);
        dataDBa.set(Calendar.HOUR_OF_DAY, 23);
        dataDBa.set(Calendar.MINUTE, 59);
        dataDBa.set(Calendar.SECOND, 59);
        dataDBa.set(Calendar.MILLISECOND, 999);

        // Inserimento nella query del filtro periodo versamento
        String periodoVers = filtri.getPeriodoVers();
        if (periodoVers != null) {
            if (periodoVers.equals("ULTIMI7")) {
                dataDBda.add(Calendar.DATE, -6);
                queryStr.append(whereWord).append("vista.dtStatoCor between :datada AND :dataa ");
            } else if (periodoVers.equals("OGGI")) {
                queryStr.append(whereWord).append("vista.dtStatoCor between :datada AND :dataa ");
            } else {
                queryStr.append(whereWord).append("vista.dtStatoCor < :dataa ");
            }
            whereWord = "AND ";
        }

        // Ricavo le date per eventuale inserimento nella query del filtro giorno versamento
        Date dataOrarioDa = (filtri.getGiornoVersDaValidato() != null ? filtri.getGiornoVersDaValidato() : null);
        Date dataOrarioA = (filtri.getGiornoVersAValidato() != null ? filtri.getGiornoVersAValidato() : null);

        // Inserimento nella query del filtro data giÃ  impostato con data e ora
        if ((dataOrarioDa != null) && (dataOrarioA != null)) {
            queryStr.append(whereWord).append("vista.dtStatoCor between :datada AND :dataa ");
            whereWord = "AND ";
        }
        // Copio i valori del filtro lista in un'altra lista per non alterare l'originale
        List<String> statoObjectList = new ArrayList<>();
        statoObjectList.addAll(filtri.getStatoObject());
        if (!statoObjectList.isEmpty()) {
            queryStr.append(whereWord).append("vista.tiStatoObject IN (:statoObjectList) ");
            whereWord = "AND ";
        }

        String registro = filtri.getRegistro();
        if (registro != null) {
            queryStr.append(whereWord).append("UPPER(vista.cdRegistroUnitaDocSacer) = :registro ");
            whereWord = "AND ";
        }
        BigDecimal anno = filtri.getAnno();
        if (anno != null) {
            queryStr.append(whereWord).append("vista.aaUnitaDocSacer = :anno ");
            whereWord = "AND ";
        }
        String codice = filtri.getCodice();
        if (codice != null) {
            queryStr.append(whereWord).append("UPPER(vista.cdKeyUnitaDocSacer) = :codice ");
            whereWord = "AND ";
        }
        String chiave = filtri.getChiave();
        if (chiave != null) {
            queryStr.append(whereWord).append("LOWER(vista.cdKeyObject) like LOWER(:chiave) ");
            whereWord = "AND ";
        }
        // MEV 26979
        BigDecimal idObject = filtri.getIdObject();
        if (idObject != null) {
            queryStr.append("and vista.idObject = :idObject ");
            whereWord = "AND ";
        }

        List<String> tiVersFile = filtri.getTiVersFile();
        if (tiVersFile != null && !tiVersFile.isEmpty()) {
            if (tiVersFile.size() > 1) {
                queryStr.append(whereWord).append("vista.tiVersFile IN (:tiVersFile) ");
            } else {
                queryStr.append(whereWord).append("vista.tiVersFile = :tiVersFile ");
            }
            whereWord = "AND ";
        }

        queryStr.append(whereWord).append("vista.idVers = abilOrganiz.idOrganizApplic ");
        // ordina
        // queryStr.append("ORDER BY vista.nmAmbienteVers, vista.nmVers, vista.dsKeyOrd ASC")
        // CREO LA QUERY ATTRAVERSO L'ENTITY MANAGER
        Query query = getEntityManager().createQuery(queryStr.toString());

        if (idAmbienteVers != null) {
            query.setParameter("idAmbienteVers", idAmbienteVers);
        }

        if (idVers != null) {
            query.setParameter("idVers", idVers);
        }

        if (idTipoObject != null) {
            query.setParameter("idTipoObject", idTipoObject);
        }

        if (periodoVers != null) {
            if (!periodoVers.equals("TUTTI")) {
                query.setParameter("datada", dataDBda.getTime(), TemporalType.TIMESTAMP);
            }
            query.setParameter("dataa", dataDBa.getTime(), TemporalType.TIMESTAMP);
        }

        if (dataOrarioDa != null && dataOrarioA != null) {
            query.setParameter("datada", dataOrarioDa, TemporalType.TIMESTAMP);
            query.setParameter("dataa", dataOrarioA, TemporalType.TIMESTAMP);
        }
        if (statoObjectList != null && !statoObjectList.isEmpty()) {
            query.setParameter("statoObjectList", statoObjectList);
        }
        if (registro != null) {
            query.setParameter("registro", registro.toUpperCase());
        }
        if (anno != null) {
            query.setParameter("anno", anno);
        }
        if (codice != null) {
            query.setParameter("codice", codice.toUpperCase());
        }
        if (chiave != null) {
            query.setParameter("chiave", "%" + chiave + "%");
        }

        // MEV 26979
        if (idObject != null) {
            query.setParameter("idObject", idObject);
        }

        if (tiVersFile != null && !tiVersFile.isEmpty()) {
            if (tiVersFile.size() > 1) {
                query.setParameter("tiVersFile", tiVersFile);
            } else {
                query.setParameter("tiVersFile", tiVersFile.get(0));
            }
        }
        List<MonVLisObj> resultList = query.getResultList();
        // ordino
        List<MonVLisObj> listaObj = resultList
                .stream().sorted(Comparator.comparing(MonVLisObj::getNmAmbienteVers)
                        .thenComparing(MonVLisObj::getNmVers).thenComparing(MonVLisObj::getDsKeyOrd))
                .collect(Collectors.toList());

        MonVLisObjTableBean monTableBean = new MonVLisObjTableBean();

        try {
            if (listaObj != null && !listaObj.isEmpty()) {
                monTableBean = (MonVLisObjTableBean) Transform.entities2TableBean(listaObj);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        // "Elaboro" il campo versatore e num UD prodotte
        for (MonVLisObjRowBean row : monTableBean) {
            row.setString("versatore", row.getNmAmbienteVers() + ", " + row.getNmVers());

            if (row.getTiVersFile().equals(Constants.TipoVersamento.ZIP_CON_XML_SACER.name())) {
                row.setBigDecimal("ni_ud_prodotte", row.getNiUdProdotte());
                // MEV 26891
                row.setString("ti_gestione_figli", "--");
            } else {
                row.setBigDecimal("ni_ud_prodotte", new BigDecimal(0));
                row.setString("ti_gestione_figli", row.getTiGestOggettiFigli());
            }
        }

        // MEV 25555
        monTableBean.addSortingRule("dt_stato_cor", SortingRule.DESC);
        monTableBean.sort();

        return monTableBean;
    }

    /**
     * Restituisce la lista di oggetti con stesso DCM Hash di un altro oggetto riferiti allo stesso ambiente e versatore
     *
     * @param idObject
     *            id oggetto
     * @param idVers
     *            id versamento
     * @param dsDcmHash
     *            descrizione hash
     *
     * @return entity bean {@link MonVLisObjTableBean}
     */
    public MonVLisObjTableBean getMonVLisObjDCMHashViewBean(BigDecimal idObject, BigDecimal idVers, String dsDcmHash) {
        String whereWord = "WHERE ";
        StringBuilder queryStr = new StringBuilder("SELECT DISTINCT new it.eng.sacerasi.viewEntity.MonVLisObj "
                + "(vista.nmAmbienteVers, vista.nmVers, vista.idObject, vista.tiStatoObject,vista.tiStatoObjectVis,"
                + " vista.tiStatoVerificaHash, vista.cdKeyObject, vista.dtVers, vista.dsInfoObject, vista.dtStatoCor, vista.niSizeFileVers, "
                + " vista.nmTipoObject, vista.note, vista.trasformazioneUtilizzata, vista.niUdProdotte, vista.tiVersFile,vista.dsKeyOrd, vista.tiGestOggettiFigli) "
                + "FROM MonVLisObj vista, PigInfoDicom pig ");

        // Inserimento nella query del filtro id versatore
        if (idVers != null) {
            queryStr.append(whereWord).append("vista.idVers = :idVers ");
            whereWord = "AND ";
        }
        // Inserimento nella query del filtro DCM Hash
        if (idVers != null) {
            queryStr.append(whereWord).append("pig.dsDcmHash = :dsDcmHash ");
            whereWord = "AND ";
        }

        queryStr.append(whereWord).append("vista.idObject = pig.pigObject.idObject AND vista.idObject <> :idObject ");

        // ordina
        // queryStr.append("ORDER BY vista.nmAmbienteVers, vista.nmVers, vista.dsKeyOrd ASC")
        // CREO LA QUERY ATTRAVERSO L'ENTITY MANAGER
        Query query = getEntityManager().createQuery(queryStr.toString());

        if (idVers != null) {
            query.setParameter("idVers", idVers);
        }

        if (dsDcmHash != null) {
            query.setParameter("dsDcmHash", dsDcmHash);
        }

        query.setParameter("idObject", idObject);

        // ESEGUO LA QUERY E PIAZZO I RISULTATI IN UNA LISTA
        List<MonVLisObj> resultList = query.getResultList();

        List<MonVLisObj> listaObj = resultList
                .stream().sorted(Comparator.comparing(MonVLisObj::getNmAmbienteVers)
                        .thenComparing(MonVLisObj::getNmVers).thenComparing(MonVLisObj::getDsKeyOrd))
                .collect(Collectors.toList());

        MonVLisObjTableBean monTableBean = new MonVLisObjTableBean();

        try {
            if (listaObj != null && !listaObj.isEmpty()) {
                monTableBean = (MonVLisObjTableBean) Transform.entities2TableBean(listaObj);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        // "Elaboro" il campo versatore
        for (MonVLisObjRowBean row : monTableBean) {
            row.setString("versatore", row.getNmAmbienteVers() + ", " + row.getNmVers());
        }

        return monTableBean;
    }

    public MonVVisSesErrataRowBean getMonVVisSesErrataRowBean(BigDecimal idSessione) {
        MonVVisSesErrataRowBean sesRowBean = new MonVVisSesErrataRowBean();

        StringBuilder queryStr = new StringBuilder("SELECT ses FROM MonVVisSesErrata ses");
        if (idSessione != null && idSessione != BigDecimal.ZERO) {
            queryStr.append(" WHERE ses.idSessioneIngest = :idSessione");
        }
        Query query = getEntityManager().createQuery(queryStr.toString());
        if (idSessione != null && idSessione != BigDecimal.ZERO) {
            query.setParameter("idSessione", idSessione);
        }
        List<MonVVisSesErrata> sessionList = query.getResultList();
        try {
            if (sessionList != null && !sessionList.isEmpty()) {
                MonVVisSesErrata ses = sessionList.get(0);
                sesRowBean = (MonVVisSesErrataRowBean) Transform.entity2RowBean(ses);
            }
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
        return sesRowBean;
    }

    /**
     * Metodo che restituisce un viewbean con i record trovati in base ai filtri di ricerca passati in ingresso
     *
     * @param filtriJS
     *            filtri job schedulati {@link FiltriJobSchedulati}
     * @param dateValidate
     *            array con date da validare
     *
     * @return entity bean {@link MonVLisSchedJobTableBean}
     *
     * @throws EMFError
     *             errore generico
     */
    public MonVLisSchedJobTableBean getMonVLisSchedJobViewBean(FiltriJobSchedulati filtriJS, Date[] dateValidate)
            throws EMFError {
        return getMonVLisSchedJobViewBean(dateValidate, filtriJS.getNm_job().parse());
    }

    /**
     * Metodo che restituisce un viewbean con i record trovati in base ai filtri di ricerca passati in ingresso
     *
     * @param dateValidate
     *            le date
     * @param nomeJob
     *            nome del job
     *
     * @return {@link MonVLisSchedJobTableBean} table bean per la UI
     */
    public MonVLisSchedJobTableBean getMonVLisSchedJobViewBean(Date[] dateValidate, String nomeJob) {
        String whereWord = "WHERE ";
        StringBuilder queryStr = new StringBuilder("SELECT u FROM MonVLisSchedJob u ");
        // Inserimento nella query del filtro nome job
        if (nomeJob != null) {
            queryStr.append(whereWord).append("u.nmJob = :nmJob ");
            whereWord = "AND ";
        }
        Date dataOrarioDa = (dateValidate != null ? dateValidate[0] : null);
        Date dataOrarioA = (dateValidate != null ? dateValidate[1] : null);
        // Inserimento nella query del filtro data giÃ  impostato con data e ora
        if ((dataOrarioDa != null) && (dataOrarioA != null)) {
            queryStr.append(whereWord).append("(u.dtRegLogJobIni between :datada AND :dataa) ");
        }
        queryStr.append("ORDER BY u.dtRegLogJobIni DESC ");
        Query query = getEntityManager().createQuery(queryStr.toString());
        if (nomeJob != null) {
            if (nomeJob.equals("ALLINEAMENTO_LOG")) {
                nomeJob = "ALLINEAMENTO_LOG_SACER_PREINGEST";
            } else if (nomeJob.equals("INIZIALIZZAZIONE_LOG")) {
                nomeJob = "INIZIALIZZAZIONE_LOG_SACER_PREINGEST";
            }

            query.setParameter("nmJob", nomeJob);
        }
        if (dataOrarioDa != null && dataOrarioA != null) {
            query.setParameter("datada", dataOrarioDa, TemporalType.TIMESTAMP);
            query.setParameter("dataa", dataOrarioA, TemporalType.TIMESTAMP);
        }

        // eseguo la query e metto i risulati in una lista
        List<MonVLisSchedJob> listaSched = query.getResultList();
        MonVLisSchedJobTableBean schedTableBean = new MonVLisSchedJobTableBean();
        try {
            if (listaSched != null && !listaSched.isEmpty()) {
                schedTableBean = (MonVLisSchedJobTableBean) Transform.entities2TableBean(listaSched);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        // Creo un nuovo campo concatenandone altri giÃ  esistenti
        for (int i = 0; i < schedTableBean.size(); i++) {
            MonVLisSchedJobRowBean row = schedTableBean.getRow(i);
            if (row.getDtRegLogJobFine() != null) {
                String durata = row.getDurataGg() + "-" + row.getDurataOre() + ":" + row.getDurataMin() + ":"
                        + row.getDurataSec();
                row.setString("durata", durata);
            }
        }
        return schedTableBean;
    }

    /**
     * Restituisce un rowbean contenente le informazioni sull'ultima schedulazione di un determinato job
     *
     * @param nomeJob
     *            nome job
     *
     * @return entity bean {@link MonVVisLastSchedJobRowBean}
     */
    public MonVVisLastSchedJobRowBean getMonVVisLastSchedJob(String nomeJob) {
        final long start = System.currentTimeMillis();
        String queryStr = "SELECT u FROM MonVVisLastSchedJob u WHERE u.nmJob = :nomeJob ";
        TypedQuery<MonVVisLastSchedJob> query = getEntityManager().createQuery(queryStr, MonVVisLastSchedJob.class)
                .setParameter("nomeJob", nomeJob);
        List<MonVVisLastSchedJob> listaLog = query.getResultList();
        MonVVisLastSchedJobRowBean logRowBean = new MonVVisLastSchedJobRowBean();
        try {
            if (listaLog != null && !listaLog.isEmpty()) {
                logRowBean = (MonVVisLastSchedJobRowBean) Transform.entity2RowBean(listaLog.get(0));
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        long end = System.currentTimeMillis() - start;
        log.debug("La chiamata alla vista MonVVisLastSchedJob filtrata per nome job [{}] è durata {} ms", nomeJob,
                +end);
        return logRowBean;
    }

    public MonVVisObjRowBean getMonVVisObjRowBean(BigDecimal idObject) {
        MonVVisObj monVVisObj = getEntityManager().find(MonVVisObj.class, idObject);
        MonVVisObjRowBean objRB = new MonVVisObjRowBean();
        try {
            if (monVVisObj != null) {
                objRB = (MonVVisObjRowBean) Transform.entity2RowBean(monVVisObj);
            }
        } catch (Exception e) {
            log.error("Errore nel recupero del dettaglio dell'oggetto {}", e.getMessage(), e);
        }
        // Concateno alcuni campi per il front-end
        String ambienteVers = objRB.getNmAmbienteVers() != null ? objRB.getNmAmbienteVers() : "";
        String vers = objRB.getNmVers() != null ? ", " + objRB.getNmVers() : "";
        objRB.setString("versatore", ambienteVers + vers);
        return objRB;
    }

    public PigInfoDicomRowBean getPigInfoDicomRowBean(BigDecimal idObject) {
        PigInfoDicomRowBean infoDicomRowBean = new PigInfoDicomRowBean();
        String queryStr = "SELECT u FROM PigInfoDicom u WHERE u.pigObject.idObject = :idObject";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idObject", HibernateUtils.longFrom(idObject));
        List<PigInfoDicom> infoDicomList = query.getResultList();
        try {
            if (infoDicomList != null && !infoDicomList.isEmpty()) {
                infoDicomRowBean = (PigInfoDicomRowBean) Transform.entity2RowBean(infoDicomList.get(0));
            }
        } catch (Exception e) {
            log.error("Errore nel recupero delle informazioni DICOM {}", e.getMessage(), e);
        }
        return infoDicomRowBean;
    }

    public MonVLisUnitaDocObjectTableBean getMonVLisUnitaDocObjectTableBean(BigDecimal idObject) {
        Query query = getEntityManager()
                .createQuery("SELECT u FROM MonVLisUnitaDocObject u WHERE u.idObject = :idObject "
                        + " ORDER BY u.cdRegistroUnitaDocSacer, u.aaUnitaDocSacer, lpad(u.cdKeyUnitaDocSacer, 13, '0')");
        query.setParameter("idObject", idObject);
        List<MonVLisUnitaDocObject> udObjectList = query.getResultList();
        return createMonVLisUnitaDocObjectTableBean(udObjectList);
    }

    public MonVLisUnitaDocObjectTableBean getMonVLisUnitaDocObjectTableBean(BigDecimal idObject,
            String cdRegistroUnitaDoc, BigDecimal aaKeyUnitaDoc, String cdKeyUnitaDoc, String tiStatoUnitaDocObject,
            String cdErr) {
        StringBuilder queryStr = new StringBuilder(
                "SELECT u FROM MonVLisUnitaDocObject u WHERE u.idObject = :idObject ");
        if (StringUtils.isNotBlank(cdRegistroUnitaDoc)) {
            queryStr.append("AND UPPER(u.cdRegistroUnitaDocSacer) = :cdRegistroUnitaDoc ");
        }
        if (aaKeyUnitaDoc != null) {
            queryStr.append("AND u.aaUnitaDocSacer = :aaKeyUnitaDoc ");
        }
        if (StringUtils.isNotBlank(cdKeyUnitaDoc)) {
            queryStr.append("AND UPPER(u.cdKeyUnitaDocSacer) = :cdKeyUnitaDoc ");
        }
        if (StringUtils.isNotBlank(tiStatoUnitaDocObject)) {
            queryStr.append("AND u.tiStatoUnitaDocObject = :tiStatoUnitaDocObject ");
        }
        if (StringUtils.isNotBlank(cdErr)) {
            queryStr.append("AND u.cdErrSacer = :cdErr ");
        }
        queryStr.append("ORDER BY u.cdRegistroUnitaDocSacer, u.aaUnitaDocSacer, lpad(u.cdKeyUnitaDocSacer, 12, '0')");
        Query query = getEntityManager().createQuery(queryStr.toString());
        query.setParameter("idObject", idObject);
        if (StringUtils.isNotBlank(cdRegistroUnitaDoc)) {
            query.setParameter("cdRegistroUnitaDoc", cdRegistroUnitaDoc.toUpperCase());
        }
        if (aaKeyUnitaDoc != null) {
            query.setParameter("aaKeyUnitaDoc", aaKeyUnitaDoc);
        }
        if (StringUtils.isNotBlank(cdKeyUnitaDoc)) {
            query.setParameter("cdKeyUnitaDoc", cdKeyUnitaDoc.toUpperCase());
        }
        if (StringUtils.isNotBlank(tiStatoUnitaDocObject)) {
            query.setParameter("tiStatoUnitaDocObject", tiStatoUnitaDocObject);
        }
        if (StringUtils.isNotBlank(cdErr)) {
            query.setParameter("cdErr", cdErr);
        }
        List<MonVLisUnitaDocObject> udObjectList = query.getResultList();
        return createMonVLisUnitaDocObjectTableBean(udObjectList);
    }

    private MonVLisUnitaDocObjectTableBean createMonVLisUnitaDocObjectTableBean(
            List<MonVLisUnitaDocObject> udObjectList) {
        MonVLisUnitaDocObjectTableBean udObjectTableBean = new MonVLisUnitaDocObjectTableBean();
        try {
            if (udObjectList != null && !udObjectList.isEmpty()) {
                for (MonVLisUnitaDocObject monVLisUnitaDocObject : udObjectList) {
                    MonVLisUnitaDocObjectRowBean rb = (MonVLisUnitaDocObjectRowBean) Transform
                            .entity2RowBean(monVLisUnitaDocObject);
                    if (rb.getNiSizeFileByte() == null) {
                        rb.setNiSizeFileByte(BigDecimal.ZERO);
                    }

                    String chiaveUd = "";
                    if (rb.getCdRegistroUnitaDocSacer() != null) {
                        chiaveUd = rb.getCdRegistroUnitaDocSacer();
                        if (rb.getAaUnitaDocSacer() != null) {
                            chiaveUd = chiaveUd + " - " + rb.getAaUnitaDocSacer();
                            if (rb.getCdKeyUnitaDocSacer() != null) {
                                chiaveUd = chiaveUd + " - " + rb.getCdKeyUnitaDocSacer();
                            }
                        }
                    }
                    rb.setString("chiave_ud", chiaveUd);
                    udObjectTableBean.add(rb);
                }
            }
        } catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException | InstantiationException
                | NoSuchMethodException | InvocationTargetException e) {
            throw new IllegalStateException(e);
        }
        return udObjectTableBean;
    }

    public <T> BaseTableInterface getDistinctColumnFromMonVLisUnitaDocObjectTableBean(BigDecimal idObject,
            Class<T> resultClass, String... columns) {
        BaseTableInterface<?> tmpTableBean = new BaseTable();
        StringBuilder builder = new StringBuilder("SELECT DISTINCT ");
        String concatenated = "";
        int i = 0;
        String firstColumn = "";
        for (String column : columns) {
            String columnCamelCase = Utils.convertSnakeCaseToCamelCase(column);
            if (i > 0) {
                builder.append(",");
                concatenated += ",";
            } else {
                firstColumn = "u." + columnCamelCase;
            }
            builder.append("u.").append(columnCamelCase);
            concatenated += "u." + columnCamelCase;
            i++;
        }
        builder.append(" FROM MonVLisUnitaDocObject u WHERE u.idObject = :idObject AND ").append(firstColumn)
                .append(" IS NOT NULL").append(" ORDER BY ").append(concatenated);
        List<T> resultList = getEntityManager().createQuery(builder.toString(), resultClass)
                .setParameter("idObject", idObject).getResultList();
        for (T value : resultList) {
            BaseRow row = new BaseRow();
            if (value instanceof String) {
                row.setObject(columns[0], value);
            } else if (value instanceof BigDecimal) {
                row.setObject(columns[0], value);
                row.setString(columns[0] + "_str", String.valueOf(value));
            } else if (value instanceof Object[]) {
                for (int index = 0; index < columns.length; index++) {
                    String column = columns[index];
                    Object[] res = (Object[]) value;
                    row.setObject(column, res[index]);
                }
            }
            tmpTableBean.add(row);
        }

        return tmpTableBean;
    }

    /*
     * METODO MODIFICATO PER LA GESTIONE DEGLI ERRORI
     */
    public BaseTableInterface getCdErrSacerFromMonVLisUnitaDocObjectTableBean(BigDecimal idObject) {
        BaseTableInterface table = new BaseTable();
        Query q = getEntityManager().createNamedQuery("MonVLisUnitaDocObject.findByPigObjectId");
        q.setParameter("idObject", idObject);
        List<Object[]> l = q.getResultList();
        for (Object[] objects : l) {
            BaseRow row = new BaseRow();
            row.setObject(MonVLisUnitaDocObjectTableDescriptor.COL_CD_ERR_SACER, objects[0]);
            row.setObject(MonVLisUnitaDocObjectTableDescriptor.COL_DL_ERR_SACER, objects[1]);
            row.setObject(MonVLisUnitaDocObjectTableDescriptor.COL_CD_CONCAT_DL_ERR_SACER, objects[2]);
            table.add(row);
        }
        return table;
    }

    public MonVLisVersObjTableBean getMonVLisVersObjTableBean(BigDecimal idObject) {
        String queryStr = "SELECT u FROM MonVLisVersObj u WHERE u.idObject = :idObject ORDER BY u.dtApertura DESC";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idObject", idObject);
        List<MonVLisVersObj> versList = query.getResultList();
        MonVLisVersObjTableBean versTableBean = new MonVLisVersObjTableBean();
        try {
            if (versList != null && !versList.isEmpty()) {

                for (MonVLisVersObj lvo : versList) {
                    MonVLisVersObjRowBean row = (MonVLisVersObjRowBean) Transform.entity2RowBean(lvo);

                    if (row.getNmReportTrasfOS() != null) {
                        row.setString("scaricaReport", "Scarica report");
                    }

                    versTableBean.add(row);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return versTableBean;
    }

    public MonVLisFileObjectTableBean getMonVLisFileObjectTableBean(BigDecimal idObject) {
        String queryStr = "SELECT u FROM MonVLisFileObject u WHERE u.idObject = :idObject ORDER BY u.nmFileObject";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idObject", idObject);
        List<MonVLisFileObject> fileObjectEntityList = query.getResultList();
        MonVLisFileObjectTableBean fileObjectTableBean = new MonVLisFileObjectTableBean();
        try {
            if (fileObjectEntityList != null && !fileObjectEntityList.isEmpty()) {
                fileObjectTableBean = (MonVLisFileObjectTableBean) Transform.entities2TableBean(fileObjectEntityList);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return fileObjectTableBean;
    }

    public boolean areFileObjectsStoredInObjectStorage(BigDecimal idObject) {
        String queryStr = "SELECT u FROM MonVLisFileObject u WHERE u.idObject = :idObject AND u.nmBucket IS NOT NULL AND u.cdKeyFile IS NOT NULL";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idObject", idObject);

        return !query.getResultList().isEmpty();
    }

    public MonVVisUnitaDocObjectRowBean getMonVVisUnitaDocObjectRowBean(BigDecimal idObject) {
        MonVVisUnitaDocObject monVVisUnitaDocObject = getEntityManager().find(MonVVisUnitaDocObject.class, idObject);
        MonVVisUnitaDocObjectRowBean udObjRB = new MonVVisUnitaDocObjectRowBean();
        try {
            if (monVVisUnitaDocObject != null) {
                udObjRB = (MonVVisUnitaDocObjectRowBean) Transform.entity2RowBean(monVVisUnitaDocObject);
            }
        } catch (Exception e) {
            log.error("Errore nel recupero del dettaglio dell'unita documentaria {}", e.getMessage(), e);
        }

        // Concateno alcuni campi per il front-end
        String ambienteVers = udObjRB.getNmAmbienteVers() != null ? udObjRB.getNmAmbienteVers() : "";
        String vers = udObjRB.getNmVers() != null ? ", " + udObjRB.getNmVers() : "";
        String registro = udObjRB.getCdRegistroUnitaDocSacer() != null ? udObjRB.getCdRegistroUnitaDocSacer() : "";
        String anno = udObjRB.getAaUnitaDocSacer() != null ? " - " + udObjRB.getAaUnitaDocSacer().toString() : "";
        String numero = udObjRB.getCdKeyUnitaDocSacer() != null ? " - " + udObjRB.getCdKeyUnitaDocSacer() : "";
        udObjRB.setString("versatore", ambienteVers + vers);
        udObjRB.setString("chiave_ud", registro + anno + numero);
        // Formatto col "." e assegno il valore ad un campo stringa
        if (udObjRB.getNiSizeFileByte() == null) {
            udObjRB.setNiSizeFileByte(BigDecimal.ZERO);
        }
        return udObjRB;
    }

    public MonVLisVersFallitiTableBean getMonVLisVersFallitiViewBean(
            MonitoraggioFiltriListaVersFallitiBean filtriVers) {
        String whereWord = "WHERE ";
        StringBuilder queryStr = new StringBuilder("SELECT u FROM MonVLisVersFalliti u ");
        // Inserimento nella query del filtro id ambiente versatore
        BigDecimal idAmbienteVers = filtriVers.getIdAmbienteVers();
        if (idAmbienteVers != null) {
            queryStr.append(whereWord).append("u.idAmbienteVers = :idAmbienteVers ");
            whereWord = "AND ";
        }
        // Inserimento nella query del filtro id versatore
        BigDecimal idVers = filtriVers.getIdVers();
        if (idVers != null) {
            queryStr.append(whereWord).append("u.idVers = :idVers ");
            whereWord = "AND ";
        }
        // Inserimento nella query del filtro nome tipo oggetto
        String nmTipoObject = filtriVers.getNmTipoObject();
        if (nmTipoObject != null) {
            queryStr.append(whereWord).append("u.nmTipoObject = :nmTipoObject ");
            whereWord = "AND ";
        }
        // Inserimento nella query del filtro stato risoluzione
        String statoRisoluzione = filtriVers.getStatoRisoluzione();
        if (statoRisoluzione != null) {
            queryStr.append(whereWord).append("u.tiStatoRisoluz = :statoRisoluzione ");
            whereWord = "AND ";
        }
        // Inserimento nella query del filtro stato
        List<String> stati = filtriVers.getStati();
        if (stati != null && !stati.isEmpty()) {
            if (stati.size() > 1) {
                queryStr.append(whereWord).append("u.tiStato IN (:stato) ");
            } else {
                queryStr.append(whereWord).append("u.tiStato = :stato ");
            }
            whereWord = "AND ";
        }
        // gestione periodo - giorno
        Calendar dataDBa = Calendar.getInstance();
        Calendar dataDBda = Calendar.getInstance();
        dataDBda.set(Calendar.HOUR_OF_DAY, 0);
        dataDBda.set(Calendar.MINUTE, 0);
        dataDBda.set(Calendar.SECOND, 0);
        dataDBda.set(Calendar.MILLISECOND, 0);
        dataDBa.set(Calendar.HOUR_OF_DAY, 23);
        dataDBa.set(Calendar.MINUTE, 59);
        dataDBa.set(Calendar.SECOND, 59);
        dataDBa.set(Calendar.MILLISECOND, 999);

        // Inserimento nella query del filtro periodo versamento
        String periodoVers = filtriVers.getPeriodoVers();
        if (periodoVers != null) {
            if (periodoVers.equals("ULTIMI7")) {
                dataDBda.add(Calendar.DATE, -6);
                queryStr.append(whereWord).append("u.dtStatoCor between :datada AND :dataa ");
            } else if (periodoVers.equals("OGGI")) {
                queryStr.append(whereWord).append("u.dtStatoCor between :datada AND :dataa ");
            } else {
                queryStr.append(whereWord).append("u.dtStatoCor < :dataa ");
            }
            whereWord = "AND ";
        }

        // Ricavo le date per eventuale inserimento nella query del filtro giorno versamento
        Date dataOrarioDa = (filtriVers.getGiornoVersDaValidato() != null ? filtriVers.getGiornoVersDaValidato()
                : null);
        Date dataOrarioA = (filtriVers.getGiornoVersAValidato() != null ? filtriVers.getGiornoVersAValidato() : null);

        // Inserimento nella query del filtro data giÃ  impostato con data e ora
        if ((dataOrarioDa != null) && (dataOrarioA != null)) {
            queryStr.append(whereWord).append("u.dtStatoCor between :datada AND :dataa ");
            whereWord = "AND ";
        }
        // Gestione filtri codice errore
        String classeErrore = filtriVers.getClasseErrore() != null ? filtriVers.getClasseErrore().replace("_", "-")
                : null;
        String codiceErrore = filtriVers.getErrore() != null ? filtriVers.getErrore().replace("_", "-") : null;
        if (codiceErrore != null) {
            queryStr.append(whereWord).append("u.cdErr = :cdErr ");
            whereWord = "AND ";
        } else if (classeErrore != null) {
            queryStr.append(whereWord).append("u.cdErr LIKE :cdErr ");
            whereWord = "AND ";
        }
        String flVerif = filtriVers.getVerificati();
        if (flVerif != null) {
            queryStr.append(whereWord).append("u.flVerif = :flVerif ");
            whereWord = "AND ";
        }
        String flNonRisolub = filtriVers.getNonRisolubili();
        if (flNonRisolub != null) {
            queryStr.append(whereWord).append("u.flNonRisolub = :flNonRisolub ");
        }
        queryStr.append("ORDER BY u.dtApertura DESC");
        Query query = getEntityManager().createQuery(queryStr.toString());
        if (idAmbienteVers != null) {
            query.setParameter("idAmbienteVers", idAmbienteVers);
        }
        if (idVers != null) {
            query.setParameter("idVers", idVers);
        }
        if (nmTipoObject != null) {
            query.setParameter("nmTipoObject", nmTipoObject);
        }
        if (stati != null && !stati.isEmpty()) {
            if (stati.size() > 1) {
                query.setParameter("stato", stati);
            } else {
                query.setParameter("stato", stati.get(0));
            }
        }
        if (statoRisoluzione != null) {
            query.setParameter("statoRisoluzione", statoRisoluzione);
        }
        if (periodoVers != null) {
            if (!periodoVers.equals("TUTTI")) {
                query.setParameter("datada", dataDBda.getTime(), TemporalType.TIMESTAMP);
            }
            query.setParameter("dataa", dataDBa.getTime(), TemporalType.TIMESTAMP);
        }
        if (dataOrarioDa != null && dataOrarioA != null) {
            query.setParameter("datada", dataOrarioDa, TemporalType.TIMESTAMP);
            query.setParameter("dataa", dataOrarioA, TemporalType.TIMESTAMP);
        }
        if (flVerif != null) {
            query.setParameter("flVerif", flVerif);
        }
        if (flNonRisolub != null) {
            query.setParameter("flNonRisolub", flNonRisolub);
        }
        if (codiceErrore != null) {
            query.setParameter("cdErr", codiceErrore);
        } else if (classeErrore != null) {
            query.setParameter("cdErr", classeErrore + "-0" + '%');
        }
        // eseguo la query e metto i risulati in una lista
        List<MonVLisVersFalliti> listaVersFalliti = query.getResultList();
        MonVLisVersFallitiTableBean versFallitiTableBean = new MonVLisVersFallitiTableBean();
        try {
            if (listaVersFalliti != null && !listaVersFalliti.isEmpty()) {
                versFallitiTableBean = (MonVLisVersFallitiTableBean) Transform.entities2TableBean(listaVersFalliti);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        // Definisco il campo versatore affinchÃ© contenga ambiente versatore e versatore
        // e il campo Info che va calcolato
        for (MonVLisVersFallitiRowBean rb : versFallitiTableBean) {
            rb.setString("versatore", rb.getNmAmbienteVers() + ", " + rb.getNmVers());
            if (rb.getNmTipoObject() != null) {
                // Se il tipo oggetto Ã¨ Studio Dicom
                if (rb.getNmTipoObject().equals("StudioDicom")) {
                    String xmlDati = rb.getBlXml();
                    if (xmlDati != null) {
                        String nmPaz = "NON DEFINITO";
                        String dataNascita = "NON DEFINITO";
                        String dataStudio = "NON DEFINITO";

                        tmpReader = new StringReader(xmlDati);

                        int nmPazStart = xmlDati.indexOf("<PatientName>");
                        int nmPazStop = xmlDati.indexOf("</PatientName>");
                        int dataNascitaStart = xmlDati.indexOf("<PatientBirthDate>");
                        int dataNascitaStop = xmlDati.indexOf("</PatientBirthDate>");
                        int dataStudioStart = xmlDati.indexOf("<StudyDate>");
                        int dataStudioStop = xmlDati.indexOf("</StudyDate>");

                        if (nmPazStart != -1 && nmPazStop != -1) {
                            nmPaz = xmlDati.substring(nmPazStart + ("<PatientName>").length(), nmPazStop);
                        }
                        if (dataNascitaStart != -1 && dataNascitaStop != -1) {
                            dataNascita = xmlDati.substring(dataNascitaStart + ("<PatientBirthDate>").length(),
                                    dataNascitaStop);
                        }
                        if (dataStudioStart != -1 && dataStudioStop != -1) {
                            dataStudio = xmlDati.substring(dataStudioStart + ("<StudyDate>").length(), dataStudioStop);
                        }
                        rb.setString("info",
                                "Paziente " + nmPaz + " nato il " + dataNascita + " Studio del " + dataStudio);
                    } else {
                        rb.setString("info", "NON CALCOLABILE");
                    }
                } else {
                    rb.setString("info", "Tipo oggetto " + rb.getNmTipoObject());
                }
            }
        }
        return versFallitiTableBean;
    }

    /**
     * Salva nella tabella relativa alle sessioni di versamento le modifiche apportate nella pagina al flag "verificato"
     * e al flag "non risolubile" di Lista Versamenti Falliti
     *
     * @param idSessioneIngest
     *            id sessione versamento
     * @param flSessioneErrVerif
     *            flag 1/0 (true/false)
     * @param flSessioneErrNonRisolub
     *            flag 1/0 (true/false)
     */
    public void saveFlVerificatiNonRisolubili(BigDecimal idSessioneIngest, String flSessioneErrVerif,
            String flSessioneErrNonRisolub) {
        PigSessioneIngest pigSessioneIngest = getEntityManager().getReference(PigSessioneIngest.class,
                idSessioneIngest.longValue());
        if (flSessioneErrVerif != null) {
            pigSessioneIngest.setFlSesErrVerif(flSessioneErrVerif);
        }
        pigSessioneIngest.setFlSesErrNonRisolub(flSessioneErrNonRisolub);
    }

    public MonVObjNonVersTableBean getMonVObjNonVersTableBean(Long idUser, BigDecimal idAmbienteVers, BigDecimal idVers,
            String nmTipoObject) {
        StringBuilder queryStr = new StringBuilder("SELECT vista.flVerif, vista.flNonRisolub, count(vista) "
                + "from IamAbilOrganiz abilOrganiz, MonVObjNonVers vista, PigVers vers  "
                + "WHERE vista.id.idVers = abilOrganiz.idOrganizApplic " + "AND vers.idVers = vista.id.idVers "
                + "AND abilOrganiz.iamUser.idUserIam = :idUser ");

        // Se ho solo il campo idAmbieteVers settato
        if (idAmbienteVers != null && idVers == null && nmTipoObject == null) {
            queryStr.append("and vers.pigAmbienteVer.idAmbienteVers = :idAmbienteVers ");
        } else if (idAmbienteVers != null && idVers != null && nmTipoObject == null) {
            queryStr.append("and abilOrganiz.idOrganizApplic = :idVers ");
        } else {
            queryStr.append("and abilOrganiz.idOrganizApplic = :idVers ");
            queryStr.append("and vista.nmTipoObject = :nmTipoObject ");
        }
        queryStr.append("GROUP BY vista.flVerif, vista.flNonRisolub ");

        Query query = getEntityManager().createQuery(queryStr.toString());
        query.setParameter("idUser", idUser);

        if (idAmbienteVers != null && idVers == null && nmTipoObject == null) {
            query.setParameter("idAmbienteVers", HibernateUtils.longFrom(idAmbienteVers));
        } else if (idAmbienteVers != null && idVers != null && nmTipoObject == null) {
            query.setParameter("idVers", idVers);
        } else {
            query.setParameter("idVers", idVers);
            query.setParameter("nmTipoObject", nmTipoObject);
        }

        List<Object[]> contaOggettiList = query.getResultList();
        MonVObjNonVersTableBean contaOggettiTableBean = new MonVObjNonVersTableBean();

        try {
            // trasformo la lista di Object[] (risultante della query) in un tablebean
            for (Object[] row : contaOggettiList) {
                MonVSesRangeDtRowBean rowBean = new MonVSesRangeDtRowBean();
                rowBean.setFlVerif(row[0] != null ? row[0].toString() : null);
                rowBean.setFlNonRisolub(row[1] != null ? row[1].toString() : null);
                rowBean.setString("ni_invii_fall", row[2] != null ? row[2].toString() : "0");
                contaOggettiTableBean.add(rowBean);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return contaOggettiTableBean;
    }

    public MonVVisVersFallitoRowBean getMonVVisVersFallitoRowBean(BigDecimal idSessioneIngest) {
        MonVVisVersFallito monVVisVersFallito = getEntityManager().find(MonVVisVersFallito.class, idSessioneIngest);
        MonVVisVersFallitoRowBean versRB = new MonVVisVersFallitoRowBean();
        try {
            if (monVVisVersFallito != null) {
                versRB = (MonVVisVersFallitoRowBean) Transform.entity2RowBean(monVVisVersFallito);
            }
        } catch (Exception e) {
            log.error("Errore nel recupero del dettaglio del versamento {}", e.getMessage(), e);
        }

        // Concateno alcuni campi per il front-end
        String ambienteVers = versRB.getNmAmbienteVers() != null ? versRB.getNmAmbienteVers() : "";
        String vers = versRB.getNmVers() != null ? ", " + versRB.getNmVers() : "";
        versRB.setString("versatore", ambienteVers + vers);
        String errore = (versRB.getCdErr() != null ? versRB.getCdErr() : "") + " - "
                + (versRB.getDlErr() != null ? versRB.getDlErr() : "");
        versRB.setString("errore", errore);

        String warning = (versRB.getCdWarn() != null ? versRB.getCdWarn() : "") + " - "
                + (versRB.getDlWarn() != null ? versRB.getDlWarn() : "");
        versRB.setString("warning", warning);

        if (versRB.getNmTipoObject() != null) {
            // Se il tipo oggetto ï¿½ Studio Dicom
            if (versRB.getNmTipoObject().equals("StudioDicom")) {
                String xmlDati = versRB.getBlXml();

                if (xmlDati != null) {

                    String nmPaz = "NON DEFINITO";
                    String dataNascita = "NON DEFINITO";
                    String dataStudio = "NON DEFINITO";

                    tmpReader = new StringReader(xmlDati);

                    int nmPazStart = xmlDati.indexOf("<PatientName>");
                    int nmPazStop = xmlDati.indexOf("</PatientName>");
                    int dataNascitaStart = xmlDati.indexOf("<PatientBirthDate>");
                    int dataNascitaStop = xmlDati.indexOf("</PatientBirthDate>");
                    int dataStudioStart = xmlDati.indexOf("<StudyDate>");
                    int dataStudioStop = xmlDati.indexOf("</StudyDate>");

                    if (nmPazStart != -1 && nmPazStop != -1) {
                        nmPaz = xmlDati.substring(nmPazStart + ("<PatientName>").length(), nmPazStop);
                    }
                    if (dataNascitaStart != -1 && dataNascitaStop != -1) {
                        dataNascita = xmlDati.substring(dataNascitaStart + ("<PatientBirthDate>").length(),
                                dataNascitaStop);
                    }
                    if (dataStudioStart != -1 && dataStudioStop != -1) {
                        dataStudio = xmlDati.substring(dataStudioStart + ("<StudyDate>").length(), dataStudioStop);
                    }
                    versRB.setString("info",
                            "Paziente " + nmPaz + " nato il " + dataNascita + " Studio del " + dataStudio);
                } else {
                    versRB.setString("info", "NON CALCOLABILE");
                }
            } else {
                versRB.setString("info", "Tipo oggetto " + versRB.getNmTipoObject());
            }
        }
        return versRB;
    }

    public void salvaDettaglioVersamento(BigDecimal idSessioneIngest, String flSessioneErrVerif,
            String flSessioneErrNonRisolub) {
        String queryStr = "SELECT u FROM PigSessioneIngest u WHERE u.idSessioneIngest = :idSessioneIngest";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idSessioneIngest", idSessioneIngest.longValue());

        List<PigSessioneIngest> sessioneIng = query.getResultList();
        PigSessioneIngest pigSessioneIngest = sessioneIng.get(0);
        if (flSessioneErrVerif != null) {
            pigSessioneIngest.setFlSesErrVerif(flSessioneErrVerif);
        }
        pigSessioneIngest.setFlSesErrNonRisolub(flSessioneErrNonRisolub);
        try {
            getEntityManager().merge(pigSessioneIngest);
            getEntityManager().flush();
        } catch (RuntimeException re) {
            log.error("Eccezione nella persistenza del ", re);
        }
    }

    public void salvaFlVersSacerDaRecup(BigDecimal idVers, String cdKeyObject, String flVersSacerDaRecup) {
        String queryStr = "SELECT peppaPigObject FROM PigObject peppaPigObject "
                + "WHERE peppaPigObject.pigVer.idVers = :idVers " + "AND peppaPigObject.cdKeyObject = :cdKeyObject ";

        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idVers", HibernateUtils.longFrom(idVers));
        query.setParameter(CD_KEY_OBJECT, cdKeyObject);

        List<PigObject> peppaPigObject = query.getResultList();
        PigObject pigObject = null;
        if (!peppaPigObject.isEmpty()) {
            pigObject = peppaPigObject.get(0);
            pigObject.setFlVersSacerDaRecup(flVersSacerDaRecup);
        }
    }

    /*
     * Priorità viene passato come ALTA;IMMEDIATA ecc.., poi all'interno viene trascodificata
     */
    public void updatePigObject(BigDecimal idObject, String note, String info, String tipoGestioneFigli,
            boolean isStudioDicom, String sPriorita, String sPrioritaVersamento, String username) {
        PigObject pigObject = getEntityManager().find(PigObject.class, idObject.longValueExact());
        pigObject.setNote(note);
        // Aggiorna solo se diverso da studio dicom
        if (!isStudioDicom) {
            pigObject.setDsObject(info);
        }
        PigSessioneIngest pigSessioneIngest = getEntityManager().find(PigSessioneIngest.class,
                pigObject.getIdLastSessioneIngest().longValueExact());
        pigSessioneIngest.setTiGestOggettiFigli(tipoGestioneFigli);
        pigObject.setTiGestOggettiFigli(tipoGestioneFigli);
        if (sPriorita != null) {
            String pri = it.eng.sacerasi.web.util.Constants.ComboFlagPrioTrasfType.valueOf(sPriorita).getValue();
            pigObject.setTiPriorita(pri);
        } else {
            pigObject.setTiPriorita(null);
        }
        if (sPrioritaVersamento != null) {
            String pri = ComboFlagPrioVersType.valueOf(sPrioritaVersamento).getValue();
            pigObject.impostaPrioritaVersamento(pri, username);

        } else {
            pigObject.impostaPrioritaVersamento(null, null);
        }
    }

    public String getDsPathInputFtp(BigDecimal idVers) {
        PigVers vers = getEntityManager().getReference(PigVers.class, idVers.longValue());
        return vers.getDsPathInputFtp();
    }

    public String getDsPathTrasf(BigDecimal idVers) {
        PigVers vers = getEntityManager().getReference(PigVers.class, idVers.longValue());
        return vers.getDsPathTrasf();
    }

    /**
     * Restituisce il table bean riferito alla lista degli oggetti derivanti da versamenti falliti
     *
     * @param filtriObj
     *            filtro {@link MonitoraggioFiltriListaOggDerVersFallitiBean}
     *
     * @return entity bean {@link MonVLisObjNonVersTableBean}
     */
    public MonVLisObjNonVersTableBean getMonVLisObjNonVersViewBean(
            MonitoraggioFiltriListaOggDerVersFallitiBean filtriObj) {
        String whereWord = "WHERE ";
        StringBuilder queryStr = new StringBuilder("SELECT u FROM MonVLisObjNonVers u ");

        BigDecimal idAmbienteVers = filtriObj.getIdAmbienteVers();
        if (idAmbienteVers != null) {
            queryStr.append(whereWord).append("u.idAmbienteVers = :idAmbienteVers ");
            whereWord = "AND ";
        }
        BigDecimal idVers = filtriObj.getIdVers();
        if (idVers != null) {
            queryStr.append(whereWord).append("u.id.idVers = :idVers ");
            whereWord = "AND ";
        }
        String nmTipoObject = filtriObj.getNmTipoObject();
        if (nmTipoObject != null) {
            queryStr.append(whereWord).append("u.nmTipoObject = :nmTipoObject ");
            whereWord = "AND ";
        }
        String flVerif = filtriObj.getVerificati();
        if (flVerif != null) {
            queryStr.append(whereWord).append("u.flVerif = :flVerif ");
            whereWord = "AND ";
        }
        String flNonRisolub = filtriObj.getNonRisolubili();
        if (flNonRisolub != null) {
            queryStr.append(whereWord).append("u.flNonRisolub = :flNonRisolub ");
            whereWord = "AND ";
        }
        String flVersSacerDaRecup = filtriObj.getDaRecuperare();
        if (flVersSacerDaRecup != null) {
            queryStr.append(whereWord).append("u.flVersSacerDaRecup = :flVersSacerDaRecup ");
        }
        // MEV#13754
        queryStr.append("ORDER BY u.nmAmbienteVers, u.nmVers, u.dtLastSesErr DESC, u.id.cdKeyObject");
        Query query = getEntityManager().createQuery(queryStr.toString());
        if (idAmbienteVers != null) {
            query.setParameter("idAmbienteVers", idAmbienteVers);
        }
        if (idVers != null) {
            query.setParameter("idVers", idVers);
        }
        if (nmTipoObject != null) {
            query.setParameter("nmTipoObject", nmTipoObject);
        }
        if (flVerif != null) {
            query.setParameter("flVerif", flVerif);
        }
        if (flNonRisolub != null) {
            query.setParameter("flNonRisolub", flNonRisolub);
        }
        if (flVersSacerDaRecup != null) {
            query.setParameter("flVersSacerDaRecup", flVersSacerDaRecup);
        }
        // eseguo la query e metto i risulati in una lista
        List<MonVLisObjNonVers> listaObjNonVers = query.getResultList();
        MonVLisObjNonVersTableBean objNonVersTableBean = new MonVLisObjNonVersTableBean();
        try {
            if (listaObjNonVers != null && !listaObjNonVers.isEmpty()) {
                objNonVersTableBean = (MonVLisObjNonVersTableBean) Transform.entities2TableBean(listaObjNonVers);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        // Definisco il campo versatore affinchÃ© contenga ambiente versatore e versatore
        // e il campo Info che va calcolato
        for (MonVLisObjNonVersRowBean rb : objNonVersTableBean) {
            rb.setString("versatore", rb.getNmAmbienteVers() + ", " + rb.getNmVers());
        }
        return objNonVersTableBean;
    }

    public MonVVisObjNonVersRowBean getMonVVisObjNonVersRowBean(BigDecimal idVers, String cdKeyObject) throws EMFError {
        String queryStr = "SELECT u FROM MonVVisObjNonVers u "
                + "WHERE u.id.idVers = :idVers AND u.id.cdKeyObject = :cdKeyObject";

        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idVers", idVers);
        query.setParameter(CD_KEY_OBJECT, cdKeyObject);
        // ESEGUO LA QUERY E PIAZZO I RISULTATI IN UNA LISTA
        List<MonVVisObjNonVers> recordList = query.getResultList();
        MonVVisObjNonVersRowBean objNonVersRB = new MonVVisObjNonVersRowBean();
        if (!recordList.isEmpty()) {
            MonVVisObjNonVers monVVisObjNonVers = recordList.get(0);
            try {
                if (monVVisObjNonVers != null) {
                    objNonVersRB = (MonVVisObjNonVersRowBean) Transform.entity2RowBean(monVVisObjNonVers);
                }
            } catch (Exception e) {
                log.error("Errore nel recupero del dettaglio dell'oggetto derivante da versamenti falliti {}",
                        e.getMessage(), e);
                throw new EMFError(EMFError.ERROR, e.getMessage());
            }
            // Concateno o risetto alcuni campi per il front-end
            String ambienteVers = objNonVersRB.getNmAmbienteVers() != null ? objNonVersRB.getNmAmbienteVers() : "";
            String vers = objNonVersRB.getNmVers() != null ? ", " + objNonVersRB.getNmVers() : "";
            objNonVersRB.setString("versatore", ambienteVers + vers);
            // Assegno i valori ad altri campi per non avere conflitti sui nomi
            // con i campi di una lista nella stessa jsp
            objNonVersRB.setString("oggetto_fl_verif", objNonVersRB.getFlVerif());
            objNonVersRB.setString("oggetto_fl_non_risolub", objNonVersRB.getFlNonRisolub());
        }
        return objNonVersRB;
    }

    public String getObjNonVersFlVerif(BigDecimal idVers, String cdKeyObject) throws EMFError {
        String queryStr = "SELECT u FROM MonVVisObjNonVers u "
                + "WHERE u.id.idVers = :idVers AND u.id.cdKeyObject = :cdKeyObject";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idVers", idVers);
        query.setParameter(CD_KEY_OBJECT, cdKeyObject);
        // ESEGUO LA QUERY E PIAZZO I RISULTATI IN UNA LISTA
        MonVVisObjNonVers monVVisObjNonVers = (MonVVisObjNonVers) query.getResultList().get(0);
        MonVVisObjNonVersRowBean objNonVersRB = new MonVVisObjNonVersRowBean();
        try {
            if (monVVisObjNonVers != null) {
                objNonVersRB = (MonVVisObjNonVersRowBean) Transform.entity2RowBean(monVVisObjNonVers);
            }
        } catch (Exception e) {
            log.error("Errore nel recupero del dettaglio dell'oggetto derivante da versamenti falliti {}",
                    e.getMessage(), e);
            throw new EMFError(EMFError.ERROR, e.getMessage());
        }
        return objNonVersRB.getFlVerif();
    }

    public PigObject getPigObject(BigDecimal idVers, String cdKeyObject) {
        Query query = getEntityManager().createQuery(
                "SELECT u FROM PigObject u WHERE u.pigVer.idVers = :idVers AND u.cdKeyObject = :cdKeyObject");
        query.setParameter("idVers", HibernateUtils.longFrom(idVers));
        query.setParameter(CD_KEY_OBJECT, cdKeyObject);

        PigObject obj = null;
        List<PigObject> lista = query.getResultList();
        if (!lista.isEmpty()) {
            obj = lista.get(0);
        }
        return obj;
    }

    public PigObjectRowBean getPigObjectRowBean(BigDecimal idVers, String cdKeyObject) {
        String queryStr = "SELECT u FROM PigObject u "
                + "WHERE u.pigVer.idVers = :idVers AND u.cdKeyObject = :cdKeyObject";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idVers", HibernateUtils.longFrom(idVers));
        query.setParameter(CD_KEY_OBJECT, cdKeyObject);
        List<PigObject> lista = query.getResultList();
        PigObjectRowBean po = new PigObjectRowBean();
        try {
            if (!lista.isEmpty()) {
                po = (PigObjectRowBean) Transform.entity2RowBean(lista.get(0));
            }
        } catch (Exception e) {
            log.error("Errore nel recupero dell'oggetto derivante da versamento fallito {}", e.getMessage(), e);
        }
        return po;
    }

    /**
     * Recupera la lista dei versamenti falliti relativi ad un oggetto non versato. Cerca per id versatore, chiave
     * oggetto
     *
     * @param idVers
     *            id versamento
     * @param cdKeyObject
     *            chiave oggetto
     *
     * @return entity bean {@link MonVLisVersObjNonVersTableBean}
     *
     * @throws EMFError
     *             errore generico
     */
    public MonVLisVersObjNonVersTableBean getMonVLisVersObjNonVersViewBean(BigDecimal idVers, String cdKeyObject)
            throws EMFError {
        String queryStr = "SELECT u FROM MonVLisVersObjNonVers u "
                + "WHERE u.idVers = :idVers AND u.cdKeyObject = :cdKeyObject " + "ORDER BY u.dtApertura DESC ";

        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idVers", idVers);
        query.setParameter(CD_KEY_OBJECT, cdKeyObject);
        // ESEGUO LA QUERY E PIAZZO I RISULTATI IN UNA LISTA
        List<MonVLisVersObjNonVers> listaVersObjNonVers = query.getResultList();
        MonVLisVersObjNonVersTableBean versObjNonVersTB = new MonVLisVersObjNonVersTableBean();
        try {
            if (listaVersObjNonVers != null && !listaVersObjNonVers.isEmpty()) {
                versObjNonVersTB = (MonVLisVersObjNonVersTableBean) Transform.entities2TableBean(listaVersObjNonVers);
            }
        } catch (Exception e) {
            log.error("Errore nel recupero del dettaglio dell'oggetto derivante da versamenti falliti {}",
                    e.getMessage(), e);
            throw new EMFError(EMFError.ERROR, e.getMessage());
        }
        return versObjNonVersTB;
    }

    // MEV 31104 - controllo solo l'ultima sessione
    public boolean isLastRisolubile(BigDecimal idVers, String cdKeyObject, String nmTipoObject) {
        String queryStr = "SELECT count(u) FROM PigObject u "
                + "WHERE u.pigVer.idVers = :idVers AND u.cdKeyObject = :cdKeyObject " + "AND NOT EXISTS "
                + "(SELECT v FROM PigSessioneIngest v " + "WHERE v.flSesErrNonRisolub = '1' "
                + "AND u.pigVer.idVers = v.pigVer.idVers AND u.cdKeyObject = v.cdKeyObject AND v.nmTipoObject = :nmTipoObject "
                + "AND v.idSessioneIngest = u.idLastSessioneIngest) ";

        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idVers", HibernateUtils.longFrom(idVers));
        query.setParameter(CD_KEY_OBJECT, cdKeyObject);
        query.setParameter("nmTipoObject", nmTipoObject);
        Long pig = (Long) query.getSingleResult();
        /* Se lo trova, significa che è risolubile */
        return pig == 1;
    }

    // MEV 31104 - controllo solo l'ultima sessione
    public boolean isLastVerificata(BigDecimal idVers, String cdKeyObject, String nmTipoObject) {
        String queryStr = "SELECT count(u) FROM PigObject u "
                + "WHERE u.pigVer.idVers = :idVers AND u.cdKeyObject = :cdKeyObject " + "AND NOT EXISTS "
                + "(SELECT v FROM PigSessioneIngest v " + "WHERE v.flSesErrVerif = '0' "
                + "AND u.pigVer.idVers = v.pigVer.idVers AND u.cdKeyObject = v.cdKeyObject AND v.nmTipoObject = :nmTipoObject "
                + "AND v.idSessioneIngest = u.idLastSessioneIngest) ";

        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idVers", HibernateUtils.longFrom(idVers));
        query.setParameter(CD_KEY_OBJECT, cdKeyObject);
        query.setParameter("nmTipoObject", nmTipoObject);
        Long pig = (Long) query.getSingleResult();
        /* Se lo trova, significa che è verificato */
        return pig == 1;
    }

    // MEV 31104 - controllo solo l'ultima sessione
    public boolean isLastNonRisolubile(BigDecimal idVers, String cdKeyObject, String nmTipoObject) {
        boolean retCode = false;
        PigObject oggetto = getPigObject(idVers, cdKeyObject);
        PigSessioneIngest v = getEntityManager().find(PigSessioneIngest.class,
                oggetto.getIdLastSessioneIngest().longValueExact());
        if (v != null && v.getNmTipoObject().equals(nmTipoObject) && v.getCdKeyObject().equals(cdKeyObject)
                && v.getPigVer().getIdVers() == idVers.longValueExact() && v.getFlSesErrNonRisolub() != null
                && v.getFlSesErrNonRisolub().equals("1")) {
            retCode = true;
        }
        return retCode;
    }

    public PigAmbienteVersRowBean getAmbienteVersFromIdVers(BigDecimal idVers) {
        String queryStr = "SELECT u.pigAmbienteVer FROM PigVers u WHERE u.idVers = :idVers ";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idVers", idVers.longValue());
        PigAmbienteVers ambienteVersEntity = (PigAmbienteVers) query.getSingleResult();
        PigAmbienteVersRowBean ambienteVersRB = new PigAmbienteVersRowBean();
        try {
            if (ambienteVersEntity != null) {
                ambienteVersRB = (PigAmbienteVersRowBean) Transform.entity2RowBean(ambienteVersEntity);
            }
        } catch (Exception e) {
            log.error("Errore nel recupero dell'ambiente versatore {}", e.getMessage(), e);
        }
        return ambienteVersRB;
    }

    public String getNomeVersFromId(BigDecimal idVers) {
        PigVers versEntity = getEntityManager().find(PigVers.class, idVers.longValue());
        return versEntity.getNmVers();
    }

    public BigDecimal getIdAmbienteVersatore(BigDecimal idVers) {
        String queryStr = "SELECT u.pigAmbienteVer.idAmbienteVers FROM PigVers u WHERE u.idVers = :idVers ";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idVers", HibernateUtils.longFrom(idVers));
        Long res = (Long) query.getSingleResult();
        return new BigDecimal(res);
    }

    public byte[] getXmlVersErr(BigDecimal idSessioneIngest) {
        MonVVisVersFallito monVVisVersFallito = getEntityManager().find(MonVVisVersFallito.class, idSessioneIngest);
        MonVVisVersFallitoRowBean versRB = new MonVVisVersFallitoRowBean();
        try {
            if (monVVisVersFallito != null) {
                versRB = (MonVVisVersFallitoRowBean) Transform.entity2RowBean(monVVisVersFallito);
            }
        } catch (Exception e) {
            log.error("Errore nel recupero del dettaglio del versamento {}", e.getMessage(), e);
        }
        return versRB.getBlXml().getBytes();
    }

    public byte[] getXmlSesErr(BigDecimal idSessione) {
        MonVVisSesErrataRowBean sesRowBean = new MonVVisSesErrataRowBean();
        StringBuilder queryStr = new StringBuilder("SELECT ses FROM MonVVisSesErrata ses");
        if (idSessione != null && idSessione != BigDecimal.ZERO) {
            queryStr.append(" WHERE ses.idSessioneIngest = :idSessione");
        }
        Query query = getEntityManager().createQuery(queryStr.toString());
        if (idSessione != null && idSessione != BigDecimal.ZERO) {
            query.setParameter("idSessione", idSessione);
        }
        List<MonVVisSesErrata> sessionList = query.getResultList();
        try {
            if (sessionList != null && !sessionList.isEmpty()) {
                MonVVisSesErrata ses = sessionList.get(0);
                sesRowBean = (MonVVisSesErrataRowBean) Transform.entity2RowBean(ses);
            }
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
        return sesRowBean.getBlXml().getBytes();
    }

    public MonVLisUnitaDocSessioneTableBean getMonVLisUnitaDocSessioneTableBean(BigDecimal idSessioneIngest) {
        Query query = getEntityManager().createQuery(
                "SELECT u FROM MonVLisUnitaDocSessione u WHERE u.idSessioneIngest = :idSessioneIngest ORDER BY u.cdRegistroUnitaDocSacer, u.aaUnitaDocSacer, lpad( u.cdKeyUnitaDocSacer, 13, '0')");
        query.setParameter("idSessioneIngest", idSessioneIngest);
        List<MonVLisUnitaDocSessione> udSessioneList = query.getResultList();
        return createMonVLisUnitaDocSessioneTableBean(udSessioneList);
    }

    public MonVLisUnitaDocSessioneTableBean getMonVLisUnitaDocSessioneTableBean(BigDecimal idSessioneIngest,
            String cdRegistroUnitaDoc, BigDecimal aaKeyUnitaDoc, String cdKeyUnitaDoc, String tiStatoUnitaDocSessione,
            String cdErr, String nmStrut, String flStrutturaNonDefinita, String flVersSimulato) {
        StringBuilder queryStr = new StringBuilder(
                "SELECT u FROM MonVLisUnitaDocSessione u WHERE u.idSessioneIngest = :idSessioneIngest ");
        if (StringUtils.isNotBlank(cdRegistroUnitaDoc)) {
            queryStr.append("AND UPPER(u.cdRegistroUnitaDocSacer) = :cdRegistroUnitaDoc ");
        }
        if (aaKeyUnitaDoc != null) {
            queryStr.append("AND u.aaUnitaDocSacer = :aaKeyUnitaDoc ");
        }
        if (StringUtils.isNotBlank(cdKeyUnitaDoc)) {
            queryStr.append("AND UPPER(u.cdKeyUnitaDocSacer) = :cdKeyUnitaDoc ");
        }
        if (StringUtils.isNotBlank(tiStatoUnitaDocSessione)) {
            queryStr.append("AND u.tiStatoUnitaDocSessione = :tiStatoUnitaDocSessione ");
        }
        if (StringUtils.isNotBlank(cdErr)) {
            queryStr.append("AND u.cdErrSacer = :cdErr ");
        }
        if (StringUtils.isNotBlank(nmStrut) && StringUtils.isNotBlank(flStrutturaNonDefinita)
                && flStrutturaNonDefinita.equals("1")) {
            queryStr.append("AND (u.nmStrut = :nmStrut OR u.idOrganizIam IS NULL) ");
        } else if (StringUtils.isNotBlank(nmStrut)) {
            queryStr.append("AND u.nmStrut = :nmStrut ");
        } else if (StringUtils.isNotBlank(flStrutturaNonDefinita) && flStrutturaNonDefinita.equals("1")) {
            queryStr.append("AND u.idOrganizIam IS NULL ");
        }
        if (StringUtils.isNotBlank(flVersSimulato)) {
            queryStr.append("AND u.flVersSimulato = :flVersSimulato ");
        }
        queryStr.append("ORDER BY u.cdRegistroUnitaDocSacer, u.aaUnitaDocSacer, lpad( u.cdKeyUnitaDocSacer, 12, '0')");
        Query query = getEntityManager().createQuery(queryStr.toString());
        query.setParameter("idSessioneIngest", idSessioneIngest);
        if (StringUtils.isNotBlank(cdRegistroUnitaDoc)) {
            query.setParameter("cdRegistroUnitaDoc", cdRegistroUnitaDoc.toUpperCase());
        }
        if (aaKeyUnitaDoc != null) {
            query.setParameter("aaKeyUnitaDoc", aaKeyUnitaDoc);
        }
        if (StringUtils.isNotBlank(cdKeyUnitaDoc)) {
            query.setParameter("cdKeyUnitaDoc", cdKeyUnitaDoc.toUpperCase());
        }
        if (StringUtils.isNotBlank(tiStatoUnitaDocSessione)) {
            query.setParameter("tiStatoUnitaDocSessione", tiStatoUnitaDocSessione);
        }
        if (StringUtils.isNotBlank(cdErr)) {
            query.setParameter("cdErr", cdErr);
        }
        if (StringUtils.isNotBlank(nmStrut)) {
            query.setParameter("nmStrut", nmStrut);
        }
        if (StringUtils.isNotBlank(flVersSimulato)) {
            query.setParameter("flVersSimulato", flVersSimulato);
        }
        List<MonVLisUnitaDocSessione> udSessioneList = query.getResultList();
        return createMonVLisUnitaDocSessioneTableBean(udSessioneList);
    }

    private MonVLisUnitaDocSessioneTableBean createMonVLisUnitaDocSessioneTableBean(
            List<MonVLisUnitaDocSessione> udObjectList) {
        MonVLisUnitaDocSessioneTableBean udObjectTableBean = new MonVLisUnitaDocSessioneTableBean();
        try {
            if (udObjectList != null && !udObjectList.isEmpty()) {
                for (MonVLisUnitaDocSessione monVLisUnitaDocSessione : udObjectList) {
                    MonVLisUnitaDocSessioneRowBean rb = (MonVLisUnitaDocSessioneRowBean) Transform
                            .entity2RowBean(monVLisUnitaDocSessione);
                    if (rb.getNiSizeFileByte() == null) {
                        rb.setNiSizeFileByte(BigDecimal.ZERO);
                    }
                    String chiaveUd = "";
                    if (rb.getCdRegistroUnitaDocSacer() != null) {
                        chiaveUd = rb.getCdRegistroUnitaDocSacer();
                        if (rb.getAaUnitaDocSacer() != null) {
                            chiaveUd = chiaveUd + " - " + rb.getAaUnitaDocSacer();
                            if (rb.getCdKeyUnitaDocSacer() != null) {
                                chiaveUd = chiaveUd + " - " + rb.getCdKeyUnitaDocSacer();
                            }
                        }
                    }
                    rb.setString("chiave_ud", chiaveUd);
                    udObjectTableBean.add(rb);
                }
            }
        } catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException | InstantiationException
                | NoSuchMethodException | InvocationTargetException e) {
            throw new IllegalStateException(e);
        }
        return udObjectTableBean;
    }

    public <T> BaseTableInterface getDistinctColumnFromMonVLisUnitaDocSessioneTableBean(BigDecimal idSessioneIngest,
            Class<T> resultClass, String addClause, String... columns) {
        BaseTableInterface<?> tmpTableBean = new BaseTable();
        StringBuilder builder = new StringBuilder("SELECT DISTINCT ");
        String concatenated = "";
        int i = 0;
        String firstColumn = "";
        for (String column : columns) {
            String columnCamelCase = Utils.convertSnakeCaseToCamelCase(column);
            if (i > 0) {
                builder.append(",");
                concatenated += ",";
            } else {
                firstColumn = "u." + columnCamelCase;
            }
            builder.append("u.").append(columnCamelCase);
            concatenated += "u." + columnCamelCase;
            i++;
        }
        builder.append(" FROM MonVLisUnitaDocSessione u WHERE u.idSessioneIngest = :idSessioneIngest AND ")
                .append(firstColumn).append(" IS NOT NULL").append(StringUtils.defaultString(addClause))
                .append(" ORDER BY ").append(concatenated);
        List<T> resultList = getEntityManager().createQuery(builder.toString(), resultClass)
                .setParameter("idSessioneIngest", idSessioneIngest).getResultList();
        for (T value : resultList) {
            BaseRow row = new BaseRow();
            if (value instanceof String) {
                row.setObject(columns[0], value);
            } else if (value instanceof BigDecimal) {
                row.setObject(columns[0], value);
                row.setString(columns[0] + "_str", String.valueOf(value));
            } else if (value instanceof Object[]) {
                for (int index = 0; index < columns.length; index++) {
                    String column = columns[index];
                    Object[] res = (Object[]) value;
                    row.setObject(column, res[index]);
                }
            }
            tmpTableBean.add(row);
        }

        return tmpTableBean;
    }

    /*
     * METODO MODIFICATO PER LA GESTIONE DEGLI ERRORI
     */
    public BaseTableInterface getCdErrSacerFromMonVLisUnitaDocSessioneTableBean(BigDecimal idSessioneIngest) {
        BaseTableInterface table = new BaseTable();
        Query q = getEntityManager().createNamedQuery("MonVLisUnitaDocSessione.findByIdSessioneIngest");
        q.setParameter("idSessioneIngest", idSessioneIngest);
        List<Object[]> l = q.getResultList();
        for (Object[] objects : l) {
            BaseRow row = new BaseRow();
            row.setObject(MonVLisUnitaDocSessioneTableDescriptor.COL_CD_ERR_SACER, objects[0]);
            row.setObject(MonVLisUnitaDocSessioneTableDescriptor.COL_DL_ERR_SACER, objects[1]);
            row.setObject(MonVLisUnitaDocSessioneTableDescriptor.COL_CD_CONCAT_DL_ERR_SACER, objects[2]);
            table.add(row);
        }
        return table;
    }

    public String getTiStatoObject(BigDecimal idVers, String cdKeyObject) {
        String queryStr = "SELECT obj.tiStatoObject FROM PigObject obj " + "WHERE obj.pigVer.idVers = :idVers "
                + "AND obj.cdKeyObject = :cdKeyObject ";

        Query q = getEntityManager().createQuery(queryStr);
        q.setParameter("idVers", HibernateUtils.longFrom(idVers));
        q.setParameter(CD_KEY_OBJECT, cdKeyObject);
        List<String> tiStatoObjectList = q.getResultList();
        if (!tiStatoObjectList.isEmpty()) {
            return tiStatoObjectList.get(0);
        } else {
            return null;
        }
    }

    public PigSessioneIngestRowBean getPigSessioneIngestRowBean(BigDecimal idSessioneIngest) {
        PigSessioneIngest sessioneIngest = getEntityManager().getReference(PigSessioneIngest.class,
                idSessioneIngest.longValue());
        PigSessioneIngestRowBean sessioneIngestRB = new PigSessioneIngestRowBean();
        try {
            if (sessioneIngest != null) {
                sessioneIngestRB = (PigSessioneIngestRowBean) Transform.entity2RowBean(sessioneIngest);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return sessioneIngestRB;
    }

    /**
     * Restituisce la lista degli oggetti associati a sessioni con stato di risoluzione uguale a 'NON_RISOLTO'
     *
     * @param idSessioneSet
     *            id sessione (set)
     *
     * @return entity bean {@link PigObjectTableBean}
     */
    public PigObjectTableBean getPigObjectsFromSessions(Set<BigDecimal> idSessioneSet) {
        String queryStr = "SELECT DISTINCT v FROM MonVLisVersFalliti u, PigObject v "
                + "WHERE u.idSessioneIngest IN (:idSessioneSet) " + "AND u.tiStatoRisoluz = 'NON_RISOLTO' "
                + "AND v.tiStatoObject IN ('CHIUSO_ERR_VERS','CHIUSO_ERR_SCHED','CHIUSO_ERR_CODA') "
                + "AND u.idVers = v.pigVer.idVers " + "AND u.cdKeyObject = v.cdKeyObject "
                + "ORDER BY v.tiStatoObject DESC";

        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idSessioneSet", idSessioneSet);
        // ESEGUO LA QUERY E PIAZZO I RISULTATI IN UNA LISTA
        List<PigObject> listaPigObject = query.getResultList();
        PigObjectTableBean pigObjectTB = new PigObjectTableBean();
        PigObjectRowBean pigObjectRB = new PigObjectRowBean();
        try {
            if (listaPigObject != null) {
                for (PigObject pig : listaPigObject) {
                    pigObjectRB = (PigObjectRowBean) Transform.entity2RowBean(pig);
                    pigObjectRB.setString("nm_tipo_object", pig.getPigTipoObject().getNmTipoObject());
                    pigObjectTB.add(pigObjectRB);
                }
            }
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
        return pigObjectTB;
    }

    /**
     * Recupera il tablebean con i dati da visualizzare nella pagina Visualizza Repliche Organizzazioni
     *
     * @param filtri
     *            i filtri di ricerca riportati dalla pagina precedente
     *
     * @return entity bean {@link IamVLisOrganizDaReplicTableBean}
     *
     * @throws EMFError
     *             errore generico
     */
    public IamVLisOrganizDaReplicTableBean getIamVLisOrganizDaReplicTableBean(FiltriReplicaOrg filtri) throws EMFError {
        return getIamVLisOrganizDaReplicTableBean(filtri.getId_ambiente_vers().parse(), filtri.getId_vers().parse(),
                filtri.getTi_oper_replic().parse(), filtri.getTi_stato_replic().parse());
    }

    /**
     * Recupera il tablebean con i dati da visualizzare nella pagina Visualizza Repliche Organizzazioni
     *
     * @param idAmbiente
     *            ambiente
     * @param idVers
     *            versatore
     * @param tiOper
     *            operazione
     * @param tiStato
     *            stato
     *
     * @return table bean per la UI {@link IamVLisOrganizDaReplicTableBean}
     */
    public IamVLisOrganizDaReplicTableBean getIamVLisOrganizDaReplicTableBean(BigDecimal idAmbiente, BigDecimal idVers,
            String tiOper, String tiStato) {
        String whereWord = "WHERE ";
        StringBuilder queryStr = new StringBuilder("SELECT u FROM IamVLisOrganizDaReplic u ");

        // Inserimento nella query del filtro id ambiente
        if (idAmbiente != null) {
            queryStr.append(whereWord).append("u.idAmbiente = :idAmbiente ");
            whereWord = "AND ";
        }
        // Inserimento nella query del filtro id ente
        if (idVers != null) {
            queryStr.append(whereWord).append("u.idVers = :idVers ");
            whereWord = "AND ";
        }

        // Inserimento nella query del filtro id tipo unita doc
        if (tiOper != null) {
            queryStr.append(whereWord).append("u.tiOperReplic = :tiOper ");
            whereWord = "AND ";
        }
        // Inserimento nella query del filtro id tipo unita doc
        if (tiStato != null) {
            queryStr.append(whereWord).append("u.tiStatoReplic = :tiStato ");
        }

        // ordina per descrizione
        queryStr.append("ORDER BY u.dsOrdOrganiz");

        // CREO LA QUERY ATTRAVERSO L'ENTITY MANAGER
        Query query = getEntityManager().createQuery(queryStr.toString());

        // non avendo passato alla query i parametri di ricerca, devo passarli ora
        if (idAmbiente != null) {
            query.setParameter("idAmbiente", idAmbiente);
        }

        if (idVers != null) {
            query.setParameter("idVers", idVers);
        }

        if (tiOper != null) {
            query.setParameter("tiOper", tiOper);
        }

        if (tiStato != null) {
            query.setParameter("tiStato", tiStato);
        }

        // ESEGUO LA QUERY E PIAZZO I RISULTATI IN UNA LISTA
        List<IamVLisOrganizDaReplic> listaIam = query.getResultList();

        IamVLisOrganizDaReplicTableBean iamTableBean = new IamVLisOrganizDaReplicTableBean();

        try {
            if (listaIam != null && !listaIam.isEmpty()) {
                iamTableBean = (IamVLisOrganizDaReplicTableBean) Transform.entities2TableBean(listaIam);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        /*
         * "Rielaboro" il campo Errore per presentarlo a video con codice, messaggio e data
         */
        for (IamVLisOrganizDaReplicRowBean row : iamTableBean) {
            if (row.getCdErr() != null) {
                row.setString("errore", row.getCdErr() + " - " + (row.getDsMsgErr() != null ? row.getDsMsgErr() : "")
                        + " del " + (row.getDtErr() != null ? row.getDtErr() : ""));
            }
        }

        return iamTableBean;
    }

    public List<MonVLisObjTrasf> retrieveMonVLisObjTrasf(BigDecimal idObject) {
        Query query = getEntityManager().createQuery(
                "SELECT m FROM MonVLisObjTrasf m WHERE m.idObjectDaTrasfPing = :idObject ORDER BY m.pgOggettoTrasf");
        query.setParameter("idObject", idObject);
        return query.getResultList();
    }

    public List<PigStatoSessioneIngest> retrievePigStatoSessioneIngestFromPigObject(BigDecimal idObject) {
        Query query = getEntityManager().createQuery(
                "SELECT s FROM PigStatoSessioneIngest s WHERE s.pigSessioneIngest.pigObject.idObject = :idObject ORDER BY s.pigSessioneIngest.dtApertura DESC, s.tsRegStato DESC");
        query.setParameter("idObject", HibernateUtils.longFrom(idObject));
        return query.getResultList();
    }

    public List<PigStatoSessioneIngest> retrievePigStatoSessioneIngest(BigDecimal idSessioneIngest) {
        Query query = getEntityManager().createQuery(
                "SELECT s FROM PigStatoSessioneIngest s WHERE s.pigSessioneIngest.idSessioneIngest = :idSessioneIngest ORDER BY s.tsRegStato DESC");
        query.setParameter("idSessioneIngest", HibernateUtils.longFrom(idSessioneIngest));
        return query.getResultList();
    }

    /**
     * determina l'insieme degli oggetti "padri" degli oggetti di cui è dato l'id come parametro, per i quali tutti gli
     * oggetti "figli" hanno stato = CHIUSO_ERR_VERS o CHIUSO_OK; per gli oggetti "figli" con stato = CHIUSO_ERR_VERS,
     * tutte le sessioni devono avere l'indicatore di non risolubile settato
     *
     * @param ids
     *            lista id oggetti padre
     *
     * @return lista oggetti
     */
    public List<PigObject> getPigObjectsPadri(List<BigDecimal> ids) {
        Query query = getEntityManager().createQuery(
                "SELECT DISTINCT o.pigObjectPadre FROM PigObject o WHERE o.pigObjectPadre IS NOT NULL AND o.idObject IN (:idObjects) "
                        + "AND NOT EXISTS (SELECT figli FROM PigObject figli WHERE figli.pigObjectPadre = o.pigObjectPadre AND figli.tiStatoObject NOT IN ('CHIUSO_ERR_VERS' , 'CHIUSO_OK', 'CHIUSO_ERR_SCHED','CHIUSO_ERR_CODA', 'ANNULLATO' ))"
                        + "AND NOT EXISTS (SELECT figli2 FROM PigObject figli2 WHERE figli2.pigObjectPadre = o.pigObjectPadre AND figli2.tiStatoObject IN ('CHIUSO_ERR_VERS' , 'CHIUSO_ERR_SCHED','CHIUSO_ERR_CODA') "
                        + "AND EXISTS (SELECT v FROM PigSessioneIngest v WHERE v.pigObject.idObject = figli2.idObject AND (v.flSesErrNonRisolub IS NULL OR v.flSesErrNonRisolub = '0') )"
                        + ")");
        query.setParameter("idObjects", HibernateUtils.longListFrom(ids));
        return query.getResultList();
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public PigSessioneIngest creaSessione(PigSessioneIngest oldSession, PigObject object, Date now, String tiStato) {
        PigSessioneIngest sessione = new PigSessioneIngest();

        sessione.setTiStato(tiStato);
        sessione.setPigVer(object.getPigVer());
        sessione.setDtApertura(now);
        sessione.setDtChiusura(null);
        sessione.setCdKeyObject(object.getCdKeyObject());
        sessione.setNmAmbienteVers(object.getPigVer().getPigAmbienteVer().getNmAmbienteVers());
        sessione.setNmVers(object.getPigVer().getNmVers());
        sessione.setNmTipoObject(object.getPigTipoObject().getNmTipoObject());
        sessione.setPigObject(object);
        sessione.setFlFileCifrato(oldSession.getFlFileCifrato());
        sessione.setFlForzaAccettazione(oldSession.getFlForzaAccettazione());
        sessione.setFlForzaWarning(oldSession.getFlForzaWarning());
        sessione.setCdVersioneXmlVers(oldSession.getCdVersioneXmlVers());
        sessione.setTiStatoVerificaHash(oldSession.getTiStatoVerificaHash());
        sessione.setDsObject(oldSession.getDsObject());
        // questi due dati mancavano!
        sessione.setCdVersGen(oldSession.getCdVersGen());
        sessione.setTiGestOggettiFigli(oldSession.getTiGestOggettiFigli());

        getEntityManager().persist(sessione);
        getEntityManager().flush();
        return sessione;
    }

    public void creaStatoSessione(BigDecimal idSessioneIngest, String statoSessione, Date dtRegStato) {
        PigSessioneIngest pigSessioneIngest = getEntityManager().find(PigSessioneIngest.class,
                idSessioneIngest.longValue());
        creaStatoSessione(pigSessioneIngest, statoSessione, dtRegStato);
    }

    public void creaStatoSessione(PigSessioneIngest pigSessioneIngest, String statoSessione, Date dtRegStato) {
        PigStatoSessioneIngest pigStatoSessione = new PigStatoSessioneIngest();
        pigStatoSessione.setPigSessioneIngest(pigSessioneIngest);
        pigStatoSessione.setIdVers(pigSessioneIngest.getPigVer().getIdVers());
        pigStatoSessione.setTiStato(statoSessione);
        // Non aggiornava la data chiusura in caso di annullamento
        if (statoSessione.equals(Constants.StatoSessioneIngest.ANNULLATA.name())) {
            pigSessioneIngest.setDtChiusura(dtRegStato);
        }
        pigStatoSessione.setTsRegStato(dtRegStato);
        getEntityManager().persist(pigStatoSessione);
        pigSessioneIngest.setIdStatoSessioneIngestCor(new BigDecimal(pigStatoSessione.getIdStatoSessioneIngest()));
        pigSessioneIngest.setTiStato(statoSessione);
        getEntityManager().flush();
    }

    // SUE26200
    public Long countPigUnitaDocObjectDuplicate(BigDecimal idObject) {
        String queryStr = "SELECT COUNT(pigUd) FROM PigUnitaDocObject pigUd WHERE pigUd.pigObject.idObject = :idObject AND pigUd.tiStatoUnitaDocObject = 'VERSATA_ERR'"
                + " AND pigUd.cdErrSacer = 'UD-002-001'";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idObject", HibernateUtils.longFrom(idObject));
        return (Long) query.getSingleResult();
    }

    public Long countPigUnitaDocObject(BigDecimal idObject, String... statiUdObject) {
        StringBuilder queryStr = new StringBuilder(
                "SELECT COUNT(pigUd) FROM PigUnitaDocObject pigUd WHERE pigUd.pigObject.idObject = :idObject ");
        List<String> statiUd = new ArrayList<>();
        if (statiUdObject != null) {
            statiUd = Arrays.asList(statiUdObject);
        }
        if (!statiUd.isEmpty()) {
            if (statiUd.size() > 1) {
                queryStr.append("AND pigUd.tiStatoUnitaDocObject IN (:statiUDObject)");
            } else {
                queryStr.append("AND pigUd.tiStatoUnitaDocObject = :statiUDObject");
            }
        }
        Query query = getEntityManager().createQuery(queryStr.toString());
        query.setParameter("idObject", HibernateUtils.longFrom(idObject));
        if (!statiUd.isEmpty()) {
            if (statiUd.size() > 1) {
                query.setParameter("statiUDObject", statiUd);
            } else {
                query.setParameter("statiUDObject", statiUd.get(0));
            }
        }
        return (Long) query.getSingleResult();
    }

    /*
     * Nuovo metodo per la MEV#14652
     */
    public boolean existsUDPerObjectVersataOkOrVersataErr(BigDecimal idObject, String cdErrSacer) {
        StringBuilder queryStr = new StringBuilder(
                "SELECT 1 FROM PigUnitaDocObject pigUd WHERE pigUd.pigObject.idObject = :idObject ");
        queryStr.append("AND (pigUd.tiStatoUnitaDocObject = :statoOk ");
        queryStr.append("OR (pigUd.tiStatoUnitaDocObject = :statoErr AND pigUd.cdErrSacer = :cdErrSacer))");
        Query query = getEntityManager().createQuery(queryStr.toString());
        query.setParameter("idObject", HibernateUtils.longFrom(idObject));
        query.setParameter("statoOk", Constants.StatoUnitaDocObject.VERSATA_OK.name());
        query.setParameter("statoErr", Constants.StatoUnitaDocObject.VERSATA_ERR.name());
        query.setParameter("cdErrSacer", cdErrSacer);
        List<PigUnitaDocObject> l = query.getResultList();
        return l != null && !l.isEmpty();
    }

    /*
     * 16) se lâ€™oggetto ha tipo versamento pari a (ZIP_NO_XML_SACER o ZIP_CON_XML_SACER o NO_ZIP) e se ha stato
     * (CHIUSO_ERR_CODA o CHIUSO_ERR_VERS) e se tutte le sessioni sono verificate e non risolubili ed (esiste almeno una
     * unitÃ  doc con stato = VERSATA_OK oppure con stato = VERSATA_ERR e codice di errore = UD-002-001))),
     * lâ€™amministratore puÃ² scegliere di annullare i versamenti delle unitÃ  doc (bottone â€œAnnulla versamenti
     * unitÃ  documentarieâ€�)
     */
    /**
     * Ritorna gli oggetti figli del padre di id <code>idObjectPadre</code> il cui stato Ã¨ dato come parametro
     *
     * @param idObjectPadre
     *            id padre
     * @param stati
     *            lista stati (opzionale)
     *
     * @return la lista di oggetti figli
     */
    public List<PigObject> getFigliWithStatus(long idObjectPadre, String... stati) {
        Query q = getEntityManager().createQuery(
                "SELECT figli FROM PigObject figli WHERE figli.pigObjectPadre.idObject = :idObjectPadre AND figli.tiStatoObject IN (:stati)");
        q.setParameter("idObjectPadre", idObjectPadre);
        q.setParameter("stati", Arrays.asList(stati));
        return q.getResultList();
    }

    /**
     * Ritorna tutti gli oggetti figli del padre di id <code>idObjectPadre</code>
     *
     * @param idObjectPadre
     *            id oggetto padre
     *
     * @return la lista di oggetti figli
     */
    public List<PigObject> getTuttiFigli(long idObjectPadre) {
        Query q = getEntityManager()
                .createQuery("SELECT figli FROM PigObject figli WHERE figli.pigObjectPadre.idObject = :idObjectPadre");
        q.setParameter("idObjectPadre", idObjectPadre);
        return q.getResultList();
    }

    public void bulkDeletePigObjectTrasf(long idObject) {
        String queryStr = "DELETE FROM PigObjectTrasf p " + "WHERE p.pigObject.idObject = :idObject ";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idObject", idObject);
        query.executeUpdate();
        getEntityManager().flush();
    }

    public void bulkDeletePigUnitaDocObject(long idObject) {
        String queryStr = "DELETE FROM PigUnitaDocObject p " + "WHERE p.pigObject.idObject = :idObject ";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idObject", idObject);
        query.executeUpdate();
        getEntityManager().flush();
    }

    // MEV 31639
    public List<PigXmlSacerUnitaDoc> getPigXmlSacerUnitaDocVersamento(long idUnitaDocObject) {
        String queryStr = "SELECT pxsud FROM PigXmlSacerUnitaDoc pxsud WHERE pxsud.tiXmlSacer = 'XML_VERS' AND pxsud.pigUnitaDocObject.idUnitaDocObject = :idUnitaDocObject";
        Query q = getEntityManager().createQuery(queryStr);
        q.setParameter("idUnitaDocObject", idUnitaDocObject);

        return q.getResultList();
    }

    public List<PigPrioritaObject> retrievePigPrioritaObject(Long idObject) {
        String queryStr = "SELECT p FROM PigPrioritaObject p WHERE p.pigObject.idObject = :idObject ORDER BY p.dtModifica DESC";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idObject", idObject);
        return query.getResultList();
    }
}
