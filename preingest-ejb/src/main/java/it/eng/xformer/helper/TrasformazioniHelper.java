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

package it.eng.xformer.helper;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.StreamUtils;
import it.eng.parer.objectstorage.helper.SalvataggioBackendHelper;
import it.eng.parer.objectstorage.dto.ObjectStorageBackend;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import it.eng.parer.objectstorage.exceptions.ObjectStorageException;

import it.eng.paginator.util.HibernateUtils;
import it.eng.sacerasi.entity.PigKSInstance;
import it.eng.sacerasi.entity.PigObject;
import it.eng.sacerasi.entity.PigObjectTrasf;
import it.eng.sacerasi.entity.PigSessioneIngest;
import it.eng.sacerasi.entity.PigTipoObject;
import it.eng.sacerasi.entity.PigVersTipoObjectDaTrasf;
import it.eng.sacerasi.entity.XfoFileTrasf;
import it.eng.sacerasi.entity.XfoParamTrasf;
import it.eng.sacerasi.entity.XfoSetParamTrasf;
import it.eng.sacerasi.entity.XfoStoricoTrasf;
import it.eng.sacerasi.entity.XfoTrasf;
import it.eng.sacerasi.viewEntity.PigVValoreParamTrasf;
import it.eng.sacerasi.web.helper.ConfigurationHelper;
import it.eng.spagoLite.db.base.BaseTableInterface;
import it.eng.spagoLite.db.base.row.BaseRow;
import it.eng.spagoLite.db.base.table.BaseTable;
import it.eng.xformer.common.Constants;
import it.eng.xformer.dto.RicercaTrasformazioneBean;

@SuppressWarnings("unchecked")
@Stateless
@LocalBean
public class TrasformazioniHelper extends TrasformazioniQueryHelper {

    @PersistenceContext(unitName = "SacerAsiJPA")
    private EntityManager entityManager;

    @EJB(mappedName = "java:app/SacerAsync-ejb/ConfigurationHelper")
    private ConfigurationHelper configurationHelper;

    @EJB
    private SalvataggioBackendHelper salvataggioBackendHelper;

    public List<XfoTrasf> searchXfoTrasf(RicercaTrasformazioneBean filtri) {
        String queryStr = "SELECT x FROM XfoTrasf x";
        StringBuilder whereClause = new StringBuilder("");

        if (StringUtils.isNotBlank(filtri.getCd_trasf())) {
            if (whereClause.length() != 0) {
                whereClause.append(" AND ");
            }

            whereClause.append("UPPER(x.cdTrasf) LIKE :cdTrasf");
        }

        if (StringUtils.isNotBlank(filtri.getDs_trasf())) {
            if (whereClause.length() != 0) {
                whereClause.append(" AND ");
            }

            whereClause.append("UPPER(x.dsTrasf) LIKE :dsTrasf");
        }

        if (StringUtils.isNotBlank(filtri.getCd_versione_cor())) {
            if (whereClause.length() != 0) {
                whereClause.append(" AND ");
            }

            whereClause.append("UPPER(x.cdVersioneCor) LIKE :cdVersioneCor");
        }

        if (StringUtils.isNotBlank(filtri.getDs_versione_cor())) {
            if (whereClause.length() != 0) {
                whereClause.append(" AND ");
            }

            whereClause.append("UPPER(x.dsVersioneCor) LIKE :dsVersioneCor");
        }

        if (StringUtils.isNotBlank(filtri.getFl_attiva())) {
            if (whereClause.length() != 0) {
                whereClause.append(" AND ");
            }

            whereClause.append("x.flAttiva = :flAttiva");
        }

        if (filtri.getDt_istituz() != null) {
            if (whereClause.length() != 0) {
                whereClause.append(" AND ");
            }

            whereClause.append("x.dtIstituz >= :dtIstituz");
        }

        if (filtri.getDt_soppres() != null) {
            if (whereClause.length() != 0) {
                whereClause.append(" AND ");
            }

            whereClause.append("x.dtSoppres <= :dtSoppres");
        }

        if (whereClause.length() != 0) {
            whereClause.insert(0, " WHERE ");
        }

        Query query = getEntityManager().createQuery(queryStr + whereClause + " ORDER BY x.cdTrasf ASC");
        if (StringUtils.isNotBlank(filtri.getCd_trasf())) {
            query.setParameter("cdTrasf", "%" + filtri.getCd_trasf().toUpperCase() + "%");
        }

        if (StringUtils.isNotBlank(filtri.getDs_trasf())) {
            query.setParameter("dsTrasf", "%" + filtri.getDs_trasf().toUpperCase() + "%");
        }

        if (StringUtils.isNotBlank(filtri.getCd_versione_cor())) {
            query.setParameter("cdVersioneCor", "%" + filtri.getCd_versione_cor().toUpperCase() + "%");
        }

        if (StringUtils.isNotBlank(filtri.getDs_versione_cor())) {
            query.setParameter("dsVersioneCor", "%" + filtri.getDs_versione_cor().toUpperCase() + "%");
        }

        if (StringUtils.isNotBlank(filtri.getFl_attiva())) {
            query.setParameter("flAttiva", filtri.getFl_attiva());
        }

        if (filtri.getDt_istituz() != null) {
            query.setParameter("dtIstituz", filtri.getDt_istituz());
        }

        if (filtri.getDt_soppres() != null) {
            query.setParameter("dtSoppres", filtri.getDt_soppres());
        }

        return query.getResultList();
    }

    public List<XfoStoricoTrasf> searchXfoStoricoTrasfbyXfoTrasf(long idTrasf) {
        String queryStr = "SELECT x FROM XfoStoricoTrasf x WHERE x.xfoTrasf.idTrasf = :idTrasf ORDER BY x.dtIstituz DESC";

        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idTrasf", idTrasf);

        return query.getResultList();
    }

    public List<XfoSetParamTrasf> searchXfoSetParamTrasfbyXfoTrasf(long idTrasf) {
        String queryStr = "SELECT x FROM XfoSetParamTrasf x WHERE x.xfoTrasf.idTrasf = :idTrasf";

        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idTrasf", idTrasf);

        return query.getResultList();
    }

    public List<XfoParamTrasf> searchXfoParamTrasfbySet(long idSetParamTrasf) {
        String queryStr = "SELECT x FROM XfoParamTrasf x WHERE x.xfoSetParamTrasf.idSetParamTrasf = :idSetParamTrasf ORDER BY x.nmParamTrasf ASC";

        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idSetParamTrasf", idSetParamTrasf);

        return query.getResultList();
    }

    public boolean transformationNameExists(String transformationName) {
        String queryStr = "SELECT x FROM XfoTrasf x WHERE x.cdTrasf = :cdTrasf";

        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("cdTrasf", transformationName);

        List<XfoTrasf> transfs = query.getResultList();

        return !transfs.isEmpty();
    }

    public boolean parametersSetExists(String parametersSetName, long idTrasf) {
        String queryStr = "SELECT x FROM XfoSetParamTrasf x WHERE x.nmSetParamTrasf = :nmSetParamTrasf AND x.xfoTrasf.idTrasf = :idTrasf";

        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("nmSetParamTrasf", parametersSetName);
        query.setParameter("idTrasf", idTrasf);

        List<XfoSetParamTrasf> parameters = query.getResultList();

        return !parameters.isEmpty();
    }

    public XfoSetParamTrasf getParametersSet(String parametersSetName, long idTrasf) {
        String queryStr = "SELECT x FROM XfoSetParamTrasf x WHERE x.nmSetParamTrasf = :nmSetParamTrasf AND x.xfoTrasf.idTrasf = :idTrasf";

        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("nmSetParamTrasf", parametersSetName);
        query.setParameter("idTrasf", idTrasf);

        List<XfoSetParamTrasf> parameters = query.getResultList();
        if (parameters.isEmpty()) {
            return null;
        } else {
            return parameters.get(0);
        }
    }

    public List<XfoParamTrasf> gettAllXfoParamTrasfbyTrasf(long idTrasf) {
        String queryStr = "SELECT x FROM XfoParamTrasf x WHERE x.xfoSetParamTrasf.xfoTrasf.idTrasf = :idTrasf";

        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idTrasf", idTrasf);

        List<XfoParamTrasf> results = query.getResultList();

        return results;
    }

    public XfoParamTrasf searchXfoParamTrasfbyName(String paramenterName, long idTrasf) {
        String queryStr = "SELECT x FROM XfoParamTrasf x WHERE x.nmParamTrasf = :nmParamTrasf AND x.xfoSetParamTrasf.xfoTrasf.idTrasf = :idTrasf";

        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("nmParamTrasf", paramenterName);
        query.setParameter("idTrasf", idTrasf);

        List<XfoParamTrasf> results = query.getResultList();

        if (results.isEmpty()) {
            return null;
        } else {
            return results.get(0);
        }
    }

    public PigVValoreParamTrasf searchPigVValoreParamTrasfByName(String paramenterName, long idVersTipoObjectDaTrasf) {
        String queryStr = "SELECT x FROM PigVValoreParamTrasf x WHERE x.nmParamTrasf = :nmParamTrasf AND x.id.idVersTipoObjectDaTrasf = :idVersTipoObjectDaTrasf";

        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("nmParamTrasf", paramenterName);
        query.setParameter("idVersTipoObjectDaTrasf", HibernateUtils.bigDecimalFrom(idVersTipoObjectDaTrasf));

        List<PigVValoreParamTrasf> results = query.getResultList();

        if (results.isEmpty()) {
            return null;
        } else {
            return results.get(0);
        }
    }

    public PigVersTipoObjectDaTrasf getPigVersTipoObjectDaTrasf(PigObject object) {
        String queryStr = "SELECT x FROM PigVersTipoObjectDaTrasf x WHERE x.cdVersGen= :cdVersGen AND x.pigTipoObjectDaTrasf.idTipoObject = :idTipoObject";

        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("cdVersGen", object.getCdVersGen());
        query.setParameter("idTipoObject", object.getPigTipoObject().getIdTipoObject());

        List<PigVersTipoObjectDaTrasf> results = query.getResultList();

        if (results.isEmpty()) {
            return null;
        } else {
            return results.get(0);
        }
    }

    public List<PigObjectTrasf> searchGeneratedPigObjects(PigObject object) {
        String queryStr = "SELECT x FROM PigObjectTrasf x WHERE x.pigObject.idObject = :idFatherObject";

        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idFatherObject", object.getIdObject());

        return (List<PigObjectTrasf>) query.getResultList();
    }

    // MEGABUG - serve anche il versatore perchè si incasina se cdkeyobject è doppio.
    public PigObjectTrasf findGeneratedPigObjectTrasf(String cdKeyObjectTrasf, PigObject object) {
        String queryStr = "SELECT x FROM PigObjectTrasf x WHERE x.cdKeyObjectTrasf = :cdKeyObjectTrasf AND x.pigObject.idObject = :idFatherObject";

        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idFatherObject", object.getIdObject());
        query.setParameter("cdKeyObjectTrasf", cdKeyObjectTrasf);

        List<PigObjectTrasf> results = query.getResultList();

        if (results.isEmpty()) {
            return null;
        } else {
            return results.get(0);
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public PigObject searchPigObjectTrasfInPigObjects(PigObjectTrasf pot) {
        String queryStr = "SELECT x FROM PigObject x WHERE x.cdKeyObject = :cdKeyObjectTrasf AND x.pigVer.idVers = :idVersTrasf AND x.pigObjectPadre.idObject = :idFatherObject";

        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("cdKeyObjectTrasf", pot.getCdKeyObjectTrasf());
        query.setParameter("idVersTrasf", pot.getPigVer().getIdVers());
        query.setParameter("idFatherObject", pot.getPigObject().getIdObject());

        List<PigObject> results = query.getResultList();

        if (results.isEmpty()) {
            return null;
        } else {
            return results.get(0);
        }
    }

    // MEV 31255
    public List<PigVersTipoObjectDaTrasf> searchAssignedPigTipoObjects(long idTrasf) {
        String queryString = "SELECT x FROM PigVersTipoObjectDaTrasf x WHERE x.pigTipoObjectDaTrasf.xfoTrasf.idTrasf = :idTrasf";

        Query query = getEntityManager().createQuery(queryString);
        query.setParameter("idTrasf", idTrasf);

        return (List<PigVersTipoObjectDaTrasf>) query.getResultList();
    }

    public boolean isTransformationAssigned(long idTrasf) {
        String queryString = "SELECT x FROM PigTipoObject x WHERE x.xfoTrasf.idTrasf = :idTrasf";

        Query query = getEntityManager().createQuery(queryString);
        query.setParameter("idTrasf", idTrasf);

        List<PigTipoObject> results = query.getResultList();

        return !results.isEmpty();
    }

    public XfoFileTrasf insertNewAuxiliaryFile(XfoTrasf transformation, String filename, byte[] fileblob) {
        XfoFileTrasf xfoFileTrasf = new XfoFileTrasf();
        xfoFileTrasf.setXfoTrasf(transformation);
        xfoFileTrasf.setNmFileTrasf(filename);
        xfoFileTrasf.setBlFileTrasf(fileblob);

        entityManager.persist(xfoFileTrasf);

        return xfoFileTrasf;
    }

    public boolean isParameterAssigned(String parameter, long idTrasf) {
        XfoParamTrasf paramTrasf = searchXfoParamTrasfbyName(parameter, idTrasf);
        return paramTrasf != null;
    }

    public String saveReport(String idOggetto, String report) throws ObjectStorageException {
        String timestamp = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String nomeOggetto = idOggetto + "/reports/" + timestamp + "/" + UUID.randomUUID();
        ObjectStorageBackend config = salvataggioBackendHelper.getObjectStorageConfiguration("XF",
                configurationHelper.getValoreParamApplicByApplic(Constants.BUCKET_REPORT_XFORMER));

        if (salvataggioBackendHelper.isActive()) {
            salvataggioBackendHelper.putS3Object(config, nomeOggetto, report);
        }

        return nomeOggetto;
    }

    public String loadReport(String idReport) throws IOException, ObjectStorageException {
        String report = "";

        ObjectStorageBackend config = salvataggioBackendHelper.getObjectStorageConfiguration("XF",
                configurationHelper.getValoreParamApplicByApplic(Constants.BUCKET_REPORT_XFORMER));

        ResponseInputStream<GetObjectResponse> is = salvataggioBackendHelper.getObject(config, idReport);

        report = StreamUtils.copyToString(is, StandardCharsets.UTF_8);

        return report;
    }

    public void saveReportIntoPigSession(PigObject po, String report) throws ObjectStorageException {
        String reportIdOS = this.saveReport(String.valueOf(po.getIdObject()), report);

        for (PigSessioneIngest psi : po.getPigSessioneIngests()) {
            if (po.getIdLastSessioneIngest().longValue() == psi.getIdSessioneIngest()) {
                psi.setNmReportTrasfOS(reportIdOS);
                entityManager.persist(psi);
            }
        }
    }

    public String getSessionReport(BigDecimal sessionId)
            throws IOException, TransformerException, ObjectStorageException {
        PigSessioneIngest psi = this.findById(PigSessioneIngest.class, sessionId);
        String report = "";
        if (psi.getNmReportTrasfOS() != null) {
            report = loadReport(psi.getNmReportTrasfOS());

            TransformerFactory transformerFactory = TransformerFactory.newInstance();

            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");

            StringWriter stringWriter = new StringWriter();
            StreamResult xmlOutput = new StreamResult(stringWriter);

            Source xmlInput = new StreamSource(new StringReader(report));
            transformer.transform(xmlInput, xmlOutput);

            report = xmlOutput.getWriter().toString();
        }

        return report;
    }

    public List<PigKSInstance> getPigKSInstances() {
        String queryString = "SELECT x FROM PigKSInstance x";

        Query query = getEntityManager().createQuery(queryString);

        return query.getResultList();
    }

    public PigKSInstance getPigKSInstanceByName(String nmKsInstance) {
        String queryString = "SELECT x FROM PigKSInstance x WHERE x.nmKsInstance = :nmKsInstance";

        Query query = getEntityManager().createQuery(queryString);
        query.setParameter("nmKsInstance", nmKsInstance);

        List<PigKSInstance> results = query.getResultList();

        if (results.isEmpty()) {
            return null;
        } else {
            return results.get(0);
        }
    }

    public PigKSInstance getPigObjectKettleServerInstance(long idObject) {
        PigObject po = findById(PigObject.class, idObject);
        BigDecimal idAmbienteVers = new BigDecimal(po.getPigVer().getPigAmbienteVer().getIdAmbienteVers());
        BigDecimal idVers = new BigDecimal(po.getPigVer().getIdVers());
        BigDecimal idTipoObject = new BigDecimal(po.getPigTipoObject().getIdTipoObject());

        String nmKsInstance = configurationHelper.getValoreParamApplicByTipoObj(Constants.NM_INSTANZA_KETTLE_SERVER,
                idAmbienteVers, idVers, idTipoObject);

        return getPigKSInstanceByName(nmKsInstance);
    }

    public List<PigKSInstance> getAllPigObjectKettleServerInstance() {
        String queryString = "SELECT x FROM PigKSInstance x";
        Query query = getEntityManager().createQuery(queryString);
        return query.getResultList();
    }

    @SuppressWarnings("rawtypes")
    public BaseTableInterface getAllPigObjectKettleServerInstanceTable() {
        BaseTable table = new BaseTable();
        List<PigKSInstance> instances = getAllPigObjectKettleServerInstance();
        for (PigKSInstance instance : instances) {
            BaseRow row = new BaseRow();
            row.setString("id_istanza", Long.toString(instance.getIdKsInstance()));
            row.setString("nm_istanza", instance.getNmKsInstance());
            row.setString("url_istanza", instance.getUrlKsInstance());
            table.add(row);
        }
        return table;
    }

    public List<Long> getFirstPigObjectByInstance(String nmKsInstance, List<Long> pigObjectsIds) {
        List<Long> poIds = new ArrayList<>();

        for (Long pigObjectId : pigObjectsIds) {
            PigObject po = findById(PigObject.class, pigObjectId);
            BigDecimal idAmbienteVers = new BigDecimal(po.getPigVer().getPigAmbienteVer().getIdAmbienteVers());
            BigDecimal idVers = new BigDecimal(po.getPigVer().getIdVers());
            BigDecimal idTipoObject = new BigDecimal(po.getPigTipoObject().getIdTipoObject());

            String poNmKsInstance = configurationHelper.getValoreParamApplicByTipoObj(
                    Constants.NM_INSTANZA_KETTLE_SERVER, idAmbienteVers, idVers, idTipoObject);

            if (poNmKsInstance.equals(nmKsInstance)) {
                poIds.add(pigObjectId);
            }
        }

        return poIds;
    }

    public boolean isTransformationAlreadyPresent(String kettleId) {
        String queryStr = "SELECT x FROM XfoTrasf x WHERE x.cdKettleId = :cdKettleId";

        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("cdKettleId", kettleId);

        List<XfoTrasf> transfs = query.getResultList();
        return !transfs.isEmpty();
    }

    public PigObject findPigObjectById(Long id) {
        PigObject po = getEntityManager().find(PigObject.class, id);
        return po;
    }
}
