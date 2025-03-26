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
package it.eng.sacerasi.web.ejb;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xadisk.connector.outbound.XADiskConnection;
import org.xadisk.connector.outbound.XADiskConnectionFactory;
import org.xml.sax.SAXException;

import it.eng.paginator.util.HibernateUtils;
import it.eng.parer.sacerlog.ejb.SacerLogEjb;
import it.eng.parer.sacerlog.ejb.helper.ExportImportFotoHelper;
import it.eng.parer.sacerlog.ejb.util.ObjectsToLogBefore;
import it.eng.parer.sacerlog.util.LogParam;
import it.eng.sacerasi.aop.TransactionInterceptor;
import it.eng.sacerasi.common.Constants;
import it.eng.sacerasi.common.Constants.NomiJob;
import it.eng.sacerasi.common.Constants.TipiRegLogJob;
import it.eng.sacerasi.entity.IamEnteSiamDaAllinea;
import it.eng.sacerasi.entity.IamOrganizDaReplic;
import it.eng.sacerasi.entity.PigAmbienteVers;
import it.eng.sacerasi.entity.PigAttribDatiSpec;
import it.eng.sacerasi.entity.PigDichVersSacer;
import it.eng.sacerasi.entity.PigDichVersSacerTipoObj;
import it.eng.sacerasi.entity.PigFileObject;
import it.eng.sacerasi.entity.PigObject;
import it.eng.sacerasi.entity.PigObjectTrasf;
import it.eng.sacerasi.entity.PigParamApplic;
import it.eng.sacerasi.entity.PigSessioneIngest;
import it.eng.sacerasi.entity.PigSopClassDicom;
import it.eng.sacerasi.entity.PigSopClassDicomVers;
import it.eng.sacerasi.entity.PigStatoObject;
import it.eng.sacerasi.entity.PigStoricoVersAmbiente;
import it.eng.sacerasi.entity.PigTipoFileObject;
import it.eng.sacerasi.entity.PigTipoObject;
import it.eng.sacerasi.entity.PigValoreParamApplic;
import it.eng.sacerasi.entity.PigValoreParamTrasf;
import it.eng.sacerasi.entity.PigValoreSetParamTrasf;
import it.eng.sacerasi.entity.PigVers;
import it.eng.sacerasi.entity.PigVersTipoObjectDaTrasf;
import it.eng.sacerasi.entity.PigXmlObjectTrasf;
import it.eng.sacerasi.entity.PigXsdDatiSpec;
import it.eng.sacerasi.entity.XfoParamTrasf;
import it.eng.sacerasi.entity.XfoSetParamTrasf;
import it.eng.sacerasi.entity.XfoTrasf;
import it.eng.sacerasi.entity.constraint.SIOrgEnteSiam.TiEnte;
import it.eng.sacerasi.exception.IncoherenceException;
import it.eng.sacerasi.exception.ParerUserError;
import it.eng.sacerasi.grantEntity.OrgAmbiente;
import it.eng.sacerasi.grantEntity.OrgAppartCollegEnti;
import it.eng.sacerasi.grantEntity.OrgEnte;
import it.eng.sacerasi.grantEntity.OrgStrut;
import it.eng.sacerasi.grantEntity.OrgVRicEnteConvenzByEsterno;
import it.eng.sacerasi.grantEntity.OrgVRicEnteNonConvenz;
import it.eng.sacerasi.grantEntity.OrgVTreeAmbitoTerrit;
import it.eng.sacerasi.grantEntity.SIOrgAccordoEnte;
import it.eng.sacerasi.grantEntity.SIOrgAmbienteEnteConvenz;
import it.eng.sacerasi.grantEntity.SIOrgEnteConvenzOrg;
import it.eng.sacerasi.grantEntity.SIOrgEnteSiam;
import it.eng.sacerasi.grantEntity.SIUsrOrganizIam;
import it.eng.sacerasi.grantEntity.UsrVAbilAmbEnteConvenz;
import it.eng.sacerasi.job.allineamentoEntiConvezionati.ejb.AllineamentoEntiConvenzionatiEjb;
import it.eng.sacerasi.job.allineamentoOrganizzazioni.ejb.AllineamentoOrganizzazioniEjb;
import it.eng.sacerasi.job.ejb.JobLogger;
import it.eng.sacerasi.slite.gen.tablebean.PigAmbienteVersRowBean;
import it.eng.sacerasi.slite.gen.tablebean.PigAmbienteVersTableBean;
import it.eng.sacerasi.slite.gen.tablebean.PigAttribDatiSpecRowBean;
import it.eng.sacerasi.slite.gen.tablebean.PigAttribDatiSpecTableBean;
import it.eng.sacerasi.slite.gen.tablebean.PigDichVersSacerRowBean;
import it.eng.sacerasi.slite.gen.tablebean.PigDichVersSacerTableBean;
import it.eng.sacerasi.slite.gen.tablebean.PigDichVersSacerTipoObjRowBean;
import it.eng.sacerasi.slite.gen.tablebean.PigDichVersSacerTipoObjTableBean;
import it.eng.sacerasi.slite.gen.tablebean.PigParamApplicRowBean;
import it.eng.sacerasi.slite.gen.tablebean.PigParamApplicTableBean;
import it.eng.sacerasi.slite.gen.tablebean.PigParamApplicTableDescriptor;
import it.eng.sacerasi.slite.gen.tablebean.PigSopClassDicomRowBean;
import it.eng.sacerasi.slite.gen.tablebean.PigSopClassDicomTableBean;
import it.eng.sacerasi.slite.gen.tablebean.PigSopClassDicomVersTableBean;
import it.eng.sacerasi.slite.gen.tablebean.PigStatoObjectTableBean;
import it.eng.sacerasi.slite.gen.tablebean.PigStoricoVersAmbienteRowBean;
import it.eng.sacerasi.slite.gen.tablebean.PigStoricoVersAmbienteTableBean;
import it.eng.sacerasi.slite.gen.tablebean.PigTipoFileObjectRowBean;
import it.eng.sacerasi.slite.gen.tablebean.PigTipoFileObjectTableBean;
import it.eng.sacerasi.slite.gen.tablebean.PigTipoObjectRowBean;
import it.eng.sacerasi.slite.gen.tablebean.PigTipoObjectTableBean;
import it.eng.sacerasi.slite.gen.tablebean.PigValoreSetParamTrasfRowBean;
import it.eng.sacerasi.slite.gen.tablebean.PigVersRowBean;
import it.eng.sacerasi.slite.gen.tablebean.PigVersTableBean;
import it.eng.sacerasi.slite.gen.tablebean.PigVersTipoObjectDaTrasfRowBean;
import it.eng.sacerasi.slite.gen.tablebean.PigVersTipoObjectDaTrasfTableBean;
import it.eng.sacerasi.slite.gen.tablebean.PigXsdDatiSpecRowBean;
import it.eng.sacerasi.slite.gen.tablebean.PigXsdDatiSpecTableBean;
import it.eng.sacerasi.slite.gen.tablebean.SIOrgEnteConvenzOrgRowBean;
import it.eng.sacerasi.slite.gen.tablebean.SIOrgEnteConvenzOrgTableBean;
import it.eng.sacerasi.slite.gen.tablebean.XfoSetParamTrasfRowBean;
import it.eng.sacerasi.slite.gen.tablebean.XfoTrasfTableBean;
import it.eng.sacerasi.slite.gen.viewbean.OrgVRicEnteConvenzByEsternoTableBean;
import it.eng.sacerasi.slite.gen.viewbean.OrgVRicEnteNonConvenzTableBean;
import it.eng.sacerasi.slite.gen.viewbean.PigVRicVersRowBean;
import it.eng.sacerasi.slite.gen.viewbean.PigVRicVersTableBean;
import it.eng.sacerasi.slite.gen.viewbean.PigVValParamTrasfDefSpecRowBean;
import it.eng.sacerasi.slite.gen.viewbean.PigVValParamTrasfDefSpecTableBean;
import it.eng.sacerasi.slite.gen.viewbean.PigVValoreParamTrasfRowBean;
import it.eng.sacerasi.slite.gen.viewbean.PigVValoreParamTrasfTableBean;
import it.eng.sacerasi.slite.gen.viewbean.PigVValoreSetParamTrasfTableBean;
import it.eng.sacerasi.slite.gen.viewbean.UsrVAbilStrutSacerXpingTableBean;
import it.eng.sacerasi.util.DateUtil;
import it.eng.sacerasi.util.SacerLogConstants;
import it.eng.sacerasi.viewEntity.PigVChkDelDichverssacer;
import it.eng.sacerasi.viewEntity.PigVChkDelDichverssacerobj;
import it.eng.sacerasi.viewEntity.PigVRicVers;
import it.eng.sacerasi.viewEntity.PigVValParamTrasfDefSpec;
import it.eng.sacerasi.viewEntity.PigVValoreParamTrasf;
import it.eng.sacerasi.viewEntity.PigVValoreSetParamTrasf;
import it.eng.sacerasi.viewEntity.UsrVAbilAmbXver;
import it.eng.sacerasi.viewEntity.UsrVAbilStrutSacerXping;
import it.eng.sacerasi.viewEntity.UsrVChkCreaAmbSacer;
import it.eng.sacerasi.web.helper.AmministrazioneHelper;
import it.eng.sacerasi.web.helper.ConfigurationHelper;
import it.eng.sacerasi.web.helper.MonitoraggioHelper;
import it.eng.sacerasi.web.util.Transform;
import it.eng.spagoCore.error.EMFError;
import it.eng.spagoLite.db.base.BaseRowInterface;
import it.eng.spagoLite.db.base.BaseTableInterface;
import it.eng.spagoLite.db.base.row.BaseRow;
import it.eng.spagoLite.db.base.sorting.SortingRule;
import it.eng.spagoLite.db.base.table.BaseTable;
import it.eng.spagoLite.message.MessageBox;
import it.eng.xformer.helper.TrasformazioniHelper;
import java.io.StringReader;
import java.io.StringWriter;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

@Stateless(mappedName = "AmministrazioneEjb")
@LocalBean
public class AmministrazioneEjb {

    public AmministrazioneEjb() {
        /* sonar */
    }

    @EJB
    private AmministrazioneHelper amministrazioneHelper;
    @EJB
    private VersCache versCache;
    @EJB
    private AllineamentoOrganizzazioniEjb allineamentoOrganizzazioniEjb;
    @EJB
    private JobLogger jobLoggerEjb;
    @EJB
    private SacerLogEjb sacerLogEjb;
    @EJB
    private ConfigurationHelper configHelper;
    @EJB
    private MonitoraggioHelper monitoraggioHelper;
    @EJB(mappedName = "java:app/sacerlog-ejb/ExportImportFotoHelper")
    private ExportImportFotoHelper exportImportFotoHelper;
    @EJB
    private AllineamentoEntiConvenzionatiEjb aecEjb;
    @EJB
    private TrasformazioniHelper trasformazioniHelper;

    @Resource(mappedName = "jca/xadiskLocal")
    private XADiskConnectionFactory xadCf;

    @Resource
    private SessionContext context;
    private static final Logger log = LoggerFactory.getLogger(AmministrazioneEjb.class);

    /*
     * AMBIENTI
     */
    public PigAmbienteVersTableBean getPigAmbienteVersAbilitatiTableBean(PigAmbienteVersRowBean ambienteVersRowBean,
            Long idUtente, boolean includiAmbientiVuoti) throws EMFError {

        PigAmbienteVersTableBean ambientiVersTableBean = new PigAmbienteVersTableBean();
        String nmAmbienteVers = null;
        if (ambienteVersRowBean != null && ambienteVersRowBean.getNmAmbienteVers() != null) {
            nmAmbienteVers = ambienteVersRowBean.getNmAmbienteVers().replace("_", "\\_");
        }
        String nmApplic = configHelper.getParamApplicApplicationName();
        List<PigAmbienteVers> listaAmbienti;
        if (includiAmbientiVuoti) {
            listaAmbienti = amministrazioneHelper.getPigAmbienteVersList(nmAmbienteVers, idUtente, nmApplic);
        } else {
            listaAmbienti = amministrazioneHelper.getPigAmbienteVersAbilitatiList(nmAmbienteVers, idUtente, nmApplic);
        }
        try {
            for (PigAmbienteVers ambiente : listaAmbienti) {
                // trasformo la lista in un tableBean.
                PigAmbienteVersRowBean ambienteRowBean = new PigAmbienteVersRowBean();
                ambienteRowBean = (PigAmbienteVersRowBean) Transform.entity2RowBean(ambiente);

                // MAC 25995 - potrebbero essere stati inseriti ambienti con questo campo vuoto
                if (ambiente.getIdEnteConserv() != null) {
                    ambienteRowBean.setString("nm_ente_conserv", amministrazioneHelper
                            .findById(SIOrgEnteSiam.class, ambiente.getIdEnteConserv()).getNmEnteSiam());
                } else {
                    ambienteRowBean.setString("nm_ente_conserv", "--");
                }

                // MAC 25995 - potrebbero essere stati inseriti ambienti con questo campo vuoto
                if (ambiente.getIdEnteConserv() != null) {
                    ambienteRowBean.setString("nm_ente_gestore", amministrazioneHelper
                            .findById(SIOrgEnteSiam.class, ambiente.getIdEnteGestore()).getNmEnteSiam());
                } else {
                    ambienteRowBean.setString("nm_ente_gestore", "--");
                }

                ambientiVersTableBean.add(ambienteRowBean);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return ambientiVersTableBean;
    }

    public PigAmbienteVersRowBean getPigAmbienteVersRowBean(BigDecimal idAmbienteVers) throws EMFError {
        return (getPigAmbienteVers(idAmbienteVers, null));
    }

    public PigAmbienteVersRowBean getPigAmbienteVersRowBean(String nmAmb) throws EMFError {
        return (getPigAmbienteVers(BigDecimal.ZERO, nmAmb));
    }

    private PigAmbienteVersRowBean getPigAmbienteVers(BigDecimal idAmbienteVers, String nmAmbienteVers)
            throws EMFError {
        PigAmbienteVersRowBean ambienteVersRowBean = null;
        PigAmbienteVers ambienteVers = new PigAmbienteVers();
        if (idAmbienteVers == BigDecimal.ZERO && nmAmbienteVers != null) {
            ambienteVers = amministrazioneHelper.getPigAmbienteVersByName(nmAmbienteVers);
        }
        if (idAmbienteVers != BigDecimal.ZERO && nmAmbienteVers == null) {
            ambienteVers = amministrazioneHelper.getPigAmbienteVersById(idAmbienteVers);
        }
        if (ambienteVers != null) {
            try {
                ambienteVersRowBean = (PigAmbienteVersRowBean) Transform.entity2RowBean(ambienteVers);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
        return ambienteVersRowBean;
    }

    public void updatePigAmbienteRowBean(LogParam param, BigDecimal idAmb, PigAmbienteVersRowBean ambienteVersRowBean,
            PigParamApplicTableBean parametriAmministrazioneAmbiente,
            PigParamApplicTableBean parametriConservazioneAmbiente, PigParamApplicTableBean parametriGestioneAmbiente)
            throws IncoherenceException {
        AmministrazioneEjb me = context.getBusinessObject(AmministrazioneEjb.class);
        IamOrganizDaReplic replic = me.saveAmbiente(param, idAmb, ambienteVersRowBean, true,
                parametriAmministrazioneAmbiente, parametriConservazioneAmbiente, parametriGestioneAmbiente);
        me.replicateToIam(replic);
    }

    public BigDecimal insertPigAmbiente(LogParam param, PigAmbienteVersRowBean ambienteVersRowBean,
            PigParamApplicTableBean parametriAmministrazioneAmbiente,
            PigParamApplicTableBean parametriConservazioneAmbiente, PigParamApplicTableBean parametriGestioneAmbiente)
            throws IncoherenceException {
        AmministrazioneEjb me = context.getBusinessObject(AmministrazioneEjb.class);
        IamOrganizDaReplic replic = me.saveAmbiente(param, null, ambienteVersRowBean, false,
                parametriAmministrazioneAmbiente, parametriConservazioneAmbiente, parametriGestioneAmbiente);
        me.replicateToIam(replic);
        return replic.getIdOrganizApplic();
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public IamOrganizDaReplic saveAmbiente(LogParam param, BigDecimal idAmb, PigAmbienteVersRowBean ambienteVersRowBean,
            boolean update, PigParamApplicTableBean parametriAmministrazioneAmbiente,
            PigParamApplicTableBean parametriConservazioneAmbiente, PigParamApplicTableBean parametriGestioneAmbiente)
            throws IncoherenceException {
        PigAmbienteVers ambienteVers;
        Constants.TiOperReplic tiOper;
        if (update) {
            ambienteVers = (PigAmbienteVers) Transform.rowBean2Entity(ambienteVersRowBean);
            ambienteVers.setIdAmbienteVers(idAmb.longValue());
            ambienteVers.setDsNote(ambienteVersRowBean.getDsNote());
            ambienteVers.setDtIniVal(ambienteVersRowBean.getDtIniVal());
            ambienteVers.setDtFineVal(ambienteVersRowBean.getDtFineVal());
            ambienteVers.setIdEnteConserv(ambienteVersRowBean.getIdEnteConserv());
            ambienteVers.setIdEnteGestore(ambienteVersRowBean.getIdEnteGestore());

            amministrazioneHelper.updatePig(ambienteVers);
            // Gestione parametri amministrazione
            manageParametriPerAmbiente(parametriAmministrazioneAmbiente, "ds_valore_param_applic_ambiente_amm",
                    ambienteVers);
            // Gestione parametri conservazione
            manageParametriPerAmbiente(parametriConservazioneAmbiente, "ds_valore_param_applic_ambiente_cons",
                    ambienteVers);
            // Gestione parametri gestione
            manageParametriPerAmbiente(parametriGestioneAmbiente, "ds_valore_param_applic_ambiente_gest", ambienteVers);

            tiOper = Constants.TiOperReplic.MOD;
        } else {
            if (amministrazioneHelper.getPigAmbienteVersByName(ambienteVersRowBean.getNmAmbienteVers()) != null) {
                throw new IncoherenceException("Nome Ambiente già presente nel database.");
            }
            ambienteVers = (PigAmbienteVers) Transform.rowBean2Entity(ambienteVersRowBean);

            amministrazioneHelper.insertEntity(ambienteVers, true);
            ambienteVersRowBean.setIdAmbienteVers(new BigDecimal(ambienteVers.getIdAmbienteVers()));

            // Gestione parametri
            for (PigParamApplicRowBean paramApplicRowBean : parametriAmministrazioneAmbiente) {
                if (paramApplicRowBean.getString("ds_valore_param_applic_ambiente_amm") != null
                        && !paramApplicRowBean.getString("ds_valore_param_applic_ambiente_amm").equals("")) {
                    insertPigValoreParamApplic(ambienteVers, null, null, paramApplicRowBean.getIdParamApplic(),
                            "AMBIENTE", paramApplicRowBean.getString("ds_valore_param_applic_ambiente_amm"));
                }
            }
            for (PigParamApplicRowBean paramApplicRowBean : parametriConservazioneAmbiente) {
                if (paramApplicRowBean.getString("ds_valore_param_applic_ambiente_cons") != null
                        && !paramApplicRowBean.getString("ds_valore_param_applic_ambiente_cons").equals("")) {
                    insertPigValoreParamApplic(ambienteVers, null, null, paramApplicRowBean.getIdParamApplic(),
                            "AMBIENTE", paramApplicRowBean.getString("ds_valore_param_applic_ambiente_cons"));
                }
            }
            for (PigParamApplicRowBean paramApplicRowBean : parametriGestioneAmbiente) {
                if (paramApplicRowBean.getString("ds_valore_param_applic_ambiente_gest") != null
                        && !paramApplicRowBean.getString("ds_valore_param_applic_ambiente_gest").equals("")) {
                    insertPigValoreParamApplic(ambienteVers, null, null, paramApplicRowBean.getIdParamApplic(),
                            "AMBIENTE", paramApplicRowBean.getString("ds_valore_param_applic_ambiente_gest"));
                }
            }

            tiOper = Constants.TiOperReplic.INS;
        }
        return amministrazioneHelper.insertEntityIamOrganizDaReplic(ambienteVers, tiOper);
    }

    public void removeAmbienteVers(LogParam param, PigAmbienteVersRowBean ambienteVersRowBean)
            throws IncoherenceException {
        AmministrazioneEjb me = context.getBusinessObject(AmministrazioneEjb.class);
        IamOrganizDaReplic replic = me.deleteAmbiente(param, ambienteVersRowBean);
        me.replicateToIam(replic);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public IamOrganizDaReplic deleteAmbiente(LogParam param, PigAmbienteVersRowBean ambienteVersRowBean)
            throws IncoherenceException {
        PigAmbienteVers ambienteVers = amministrazioneHelper
                .getPigAmbienteVersById(ambienteVersRowBean.getIdAmbienteVers());
        if (!ambienteVers.getPigVers().isEmpty()) {
            throw new IncoherenceException("Rimozione ambiente non riuscita: versatori associati all'ambiente");
        }
        amministrazioneHelper.removeEntity(ambienteVers, true);

        return amministrazioneHelper.insertEntityIamOrganizDaReplic(ambienteVers, Constants.TiOperReplic.CANC);
    }

    public boolean isCreaAmbienteActive(long idUser, String nmApplic) {
        UsrVChkCreaAmbSacer usr = amministrazioneHelper.getUsrVChkCreaAmbSacer(idUser, nmApplic);
        return usr.getFlCreaAmbiente().equals("1");
    }

    /*
     * VERSATORI
     */
    public PigVersTableBean getPigVersTableBean(PigVersRowBean versRowBean) {
        PigVersTableBean pigVersTableBean = new PigVersTableBean();
        BigDecimal idAmbienteVers = null;
        if (versRowBean != null && versRowBean.getIdAmbienteVers() != null) {
            idAmbienteVers = versRowBean.getIdAmbienteVers();
        }
        List<PigVers> list = amministrazioneHelper.getPigVersList(idAmbienteVers);
        try {
            if (!list.isEmpty()) {
                pigVersTableBean = (PigVersTableBean) Transform.entities2TableBean(list);
                pigVersTableBean.addSortingRule("nmVers", SortingRule.ASC);
                pigVersTableBean.sort();
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return pigVersTableBean;
    }

    public boolean existPigVersValidiDataOdierna(BigDecimal idAmbienteVers) {
        return amministrazioneHelper.existPigVersValidiDataOdierna(idAmbienteVers);
    }

    public boolean existUtentiUnAmbiente(BigDecimal idAmbienteVers) {
        return amministrazioneHelper.existUtentiUnAmbiente(idAmbienteVers,
                configHelper.getParamApplicApplicationName());
    }

    public boolean existUtentiUnVersatore(BigDecimal idVers) {
        return amministrazioneHelper.existUtentiUnVersatore(idVers, configHelper.getParamApplicApplicationName());
    }

    public PigVersTableBean getPigVersTableBeanFromCombo(PigVersRowBean versRowBean,
            PigAmbienteVersRowBean ambienteVersRowBean, Long idUserIam) {
        PigVersTableBean pigVersTableBean = new PigVersTableBean();
        PigVers vers = new PigVers();
        PigAmbienteVers ambienteVers = new PigAmbienteVers();
        if (versRowBean != null && versRowBean.getNmVers() != null) {
            versRowBean.setNmVers(versRowBean.getNmVers().replace("_", "\\_"));
        }
        try {
            vers = (PigVers) Transform.rowBean2Entity(versRowBean);
            ambienteVers = (PigAmbienteVers) Transform.rowBean2Entity(ambienteVersRowBean);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        vers.setPigAmbienteVer(ambienteVers);
        List<PigVers> list = amministrazioneHelper.getPigVersListFromCombo(vers, idUserIam);
        try {
            if (!list.isEmpty()) {
                pigVersTableBean = (PigVersTableBean) Transform.entities2TableBean(list);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return pigVersTableBean;
    }

    public PigVRicVersTableBean getPigVRicVersTableBean(Long idVers, String nmVers, String nmAmbienteVers,
            String nmAmbienteSacer, String nmEnteSacer, String nmStrutSacer, String nmUseridSacer,
            String nmAmbienteEnteConvenz, String nmEnteConvenz, Long idUserIam, String nmTipoVersatore) {
        PigVRicVersTableBean pigVRicVersTableBean = new PigVRicVersTableBean();

        List<PigVRicVers> list = amministrazioneHelper.getPigVRicVersList(idVers, nmVers, nmAmbienteVers,
                nmAmbienteSacer, nmEnteSacer, nmStrutSacer, nmUseridSacer, nmAmbienteEnteConvenz, nmEnteConvenz,
                idUserIam, nmTipoVersatore);

        try {
            if (!list.isEmpty()) {
                for (PigVRicVers vers : list) {
                    PigVRicVersRowBean pigVRicVersRowBean = (PigVRicVersRowBean) Transform.entity2RowBean(vers);

                    if (vers.getNmAmbienteEnteConvenz() != null && vers.getNmEnteConvenz() != null) {
                        pigVRicVersRowBean.setString("ds_ambiente_ente_convenz",
                                vers.getNmAmbienteEnteConvenz() + " / " + vers.getNmEnteConvenz());
                    } else {
                        pigVRicVersRowBean.setString("ds_ambiente_ente_convenz", "");
                    }

                    pigVRicVersTableBean.add(pigVRicVersRowBean);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return pigVRicVersTableBean;
    }

    public PigVersTableBean getPigVersTableBeanFromKey(String nmVers, BigDecimal idAmbienteVers) {
        PigVersTableBean pigVersTableBean = new PigVersTableBean();
        List<PigVers> list = amministrazioneHelper.getPigVersListFromKey(nmVers, idAmbienteVers);
        try {
            if (!list.isEmpty()) {
                pigVersTableBean = (PigVersTableBean) Transform.entities2TableBean(list);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return pigVersTableBean;
    }

    public PigVersTableBean getPigVersAbilitatiTableBean(List<BigDecimal> idVersGenGiaDef, Long idUserIam) {
        PigVersTableBean pigVersTableBean = new PigVersTableBean();
        List<PigVers> list = amministrazioneHelper.getPigVersAbilitatiList(idVersGenGiaDef, idUserIam);
        try {
            for (PigVers vers : list) {
                PigVersRowBean pigVersRowBean = (PigVersRowBean) Transform.entity2RowBean(vers);
                pigVersRowBean.setBigDecimal("id_vers_gen", new BigDecimal(vers.getIdVers()));
                pigVersRowBean.setString("nm_vers_gen",
                        vers.getPigAmbienteVer().getNmAmbienteVers() + " - " + vers.getNmVers());
                pigVersTableBean.add(pigVersRowBean);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return pigVersTableBean;
    }

    public PigVersRowBean getPigVersRowBean(BigDecimal idVers) {
        return (getPigVers(idVers, null, null));
    }

    public PigVersRowBean getPigVersRowBean(String nmVers, BigDecimal idAmbienteVers) {
        return (getPigVers(BigDecimal.ZERO, nmVers, idAmbienteVers));
    }

    private PigVersRowBean getPigVers(BigDecimal idVers, String nmVers, BigDecimal idAmbienteVers) {
        PigVersRowBean versRowBean = null;
        PigVers vers = null;
        if (idVers == BigDecimal.ZERO && nmVers != null) {
            vers = amministrazioneHelper.getPigVersByName(nmVers, idAmbienteVers);
        }
        if (idVers != BigDecimal.ZERO && nmVers == null) {
            vers = amministrazioneHelper.getPigVersById(idVers);
        }
        if (vers != null) {
            try {
                versRowBean = (PigVersRowBean) Transform.entity2RowBean(vers);
                versRowBean.setString("nm_ambiente_vers", vers.getPigAmbienteVer().getNmAmbienteVers() + " - "
                        + vers.getPigAmbienteVer().getDsAmbienteVers());
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
        return versRowBean;
    }

    public PigVersRowBean getPigVersSoloNomeRowBean(BigDecimal idVers) {
        PigVersRowBean versRowBean = new PigVersRowBean();
        PigVers vers = amministrazioneHelper.getPigVersById(idVers);
        if (vers != null) {
            try {
                versRowBean = (PigVersRowBean) Transform.entity2RowBean(vers);
                versRowBean.setString("nm_ambiente_vers", vers.getPigAmbienteVer().getNmAmbienteVers());
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
        return versRowBean;
    }

    public PigAmbienteVersRowBean getPigAmbienteVersRowBeanFromVers(BigDecimal idVers) {
        PigAmbienteVersRowBean ambienteVersRowBean = null;
        if (idVers != null) {
            PigVers vers = amministrazioneHelper.getPigVersById(idVers);
            PigAmbienteVers ambienteVers = amministrazioneHelper
                    .getPigAmbienteVersById(new BigDecimal(vers.getPigAmbienteVer().getIdAmbienteVers()));
            if (ambienteVers != null) {
                try {
                    ambienteVersRowBean = (PigAmbienteVersRowBean) Transform.entity2RowBean(ambienteVers);
                } catch (Exception e) {
                    log.error(e.getMessage());
                }
            }
        }
        return ambienteVersRowBean;
    }

    public PigDichVersSacerRowBean getPigDichVersSacerFromVers(BigDecimal idVers) {
        PigDichVersSacerRowBean dichVersRowBean = new PigDichVersSacerRowBean();
        if (idVers != null) {
            PigDichVersSacer dichVers = amministrazioneHelper.getPigDichVersSacer(idVers, null);
            try {
                dichVersRowBean = (PigDichVersSacerRowBean) Transform.entity2RowBean(dichVers);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
        return dichVersRowBean;
    }

    public void updatePigVers(LogParam param, long idUserIamCor, BigDecimal idVers, PigVersRowBean versRowBean,
            PigParamApplicTableBean parametriAmministrazione, PigParamApplicTableBean parametriConservazione,
            PigParamApplicTableBean parametriGestione, String tiDichVers, BigDecimal idOrganizIam)
            throws IncoherenceException, ParerUserError {
        AmministrazioneEjb me = context.getBusinessObject(AmministrazioneEjb.class);
        IamOrganizDaReplic replic = me.saveVers(param, idUserIamCor, idVers, versRowBean, true,
                parametriAmministrazione, parametriConservazione, parametriGestione, null, null, null, null, null,
                tiDichVers, idOrganizIam);
        me.replicateToIam(replic);
    }

    public void insertPigVers(LogParam param, long idUserIamCor, PigVersRowBean versRowBean,
            PigParamApplicTableBean parametriAmministrazione, PigParamApplicTableBean parametriConservazione,
            PigParamApplicTableBean parametriGestione, String tipologia, BigDecimal idEnteConvenzEc,
            BigDecimal idEnteFornitEstern, Date dtIniVal, Date dtFineVal, String tiDichVers, BigDecimal idOrganizIam)
            throws IncoherenceException, ParerUserError {
        AmministrazioneEjb me = context.getBusinessObject(AmministrazioneEjb.class);
        IamOrganizDaReplic replic = me.saveVers(param, idUserIamCor, null, versRowBean, false, parametriAmministrazione,
                parametriConservazione, parametriGestione, tipologia, idEnteConvenzEc, idEnteFornitEstern, dtIniVal,
                dtFineVal, tiDichVers, idOrganizIam);
        me.replicateToIam(replic);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public IamOrganizDaReplic saveVers(LogParam param, long idUserIamCor, BigDecimal idVers, PigVersRowBean versRowBean,
            boolean update, PigParamApplicTableBean parametriAmministrazione,
            PigParamApplicTableBean parametriConservazione, PigParamApplicTableBean parametriGestione, String tipologia,
            BigDecimal idEnteConvenzEc, BigDecimal idEnteFornitEstern, Date dtIniValAppartEnteSiam,
            Date dtFineValAppartEnteSiam, String tiDichVers, BigDecimal idOrganizIam)
            throws IncoherenceException, ParerUserError {
        PigVers vers;
        Constants.TiOperReplic tiOper;

        XADiskConnection xadConn = null;

        try {
            // MODIFICA
            if (update) {
                PigVers dbVers = amministrazioneHelper.findById(PigVers.class, idVers);
                if (dbVers != null && dbVers.getIdVers() != idVers.longValue()) {
                    throw new IncoherenceException(
                            "Nome versatore gi\u00E0 associato ad ambiente, utilizzare altro nome");
                }

                if (dbVers.getPigDichVersSacers() == null) {
                    dbVers.setPigDichVersSacers(new ArrayList<>());
                }

                if (versRowBean.getString("tipologia").equals("PRODUTTORE")) {
                    // Se ho modificato la Corrispondenza Sacer...
                    if (!dbVers.getPigDichVersSacers().isEmpty()
                            && dbVers.getPigDichVersSacers().get(0).getIdOrganizIam().compareTo(idOrganizIam) != 0) {
                        // ...controllo se era possibile
                        BigDecimal oldIdOrganizIam = dbVers.getPigDichVersSacers().get(0).getIdOrganizIam();
                        PigDichVersSacer dichVersSacer = amministrazioneHelper.getPigDichVersSacer(idVers,
                                oldIdOrganizIam);
                        if (dichVersSacer != null) {
                            PigVChkDelDichverssacer del = amministrazioneHelper.findViewById(
                                    PigVChkDelDichverssacer.class,
                                    BigDecimal.valueOf(dichVersSacer.getIdDichVersSacer()));
                            if (!(del != null && del.getFlDelDchVersSacerOk() != null
                                    && del.getFlDelDchVersSacerOk().equals("1"))) {
                                throw new IncoherenceException(
                                        "La corrispondenza a Sacer non e’ modificabile perché sono presenti oggetti con stato = IN_ATTESA_FILE o IN_ATTESA_SCHED che potrebbero contenere unità documentarie da versare in strutture giustificate dalla corrispondenza");
                            } else {
                                controlliCorrispondenzaSacer(idUserIamCor, dbVers.getIdEnteConvenz(), idOrganizIam,
                                        tiDichVers);
                                // // Se ho modificato la corrispondenza ed è andato tutto bene, cancello la "vecchia"
                                dichVersSacer.setTiDichVers(tiDichVers);
                                dichVersSacer.setIdOrganizIam(idOrganizIam);
                            }
                        }
                    } // Se invece prima non c'era la corrispondenza e adesso c'è, la sto inserendo nuova
                    else if (dbVers.getPigDichVersSacers().isEmpty()) {
                        controlliCorrispondenzaSacer(idUserIamCor, dbVers.getIdEnteConvenz(), idOrganizIam, tiDichVers);
                        // Inserisco la corrispondenza
                        PigDichVersSacer dichVersSacerNew = new PigDichVersSacer();
                        dichVersSacerNew.setIdOrganizIam(idOrganizIam);
                        dichVersSacerNew.setTiDichVers(tiDichVers);
                        dichVersSacerNew.setPigVer(dbVers);
                        amministrazioneHelper.insertEntity(dichVersSacerNew, false);
                    }
                }

                // SE SI MODIFICA L'AMBIENTE
                if (versRowBean.getIdAmbienteVers().longValue() != dbVers.getPigAmbienteVer().getIdAmbienteVers()) {

                    // Se sono state modificate le date di inizio / fine validità di appartenenza del versatore
                    // all’ambiente
                    if (versRowBean.getDtIniValAppartAmbiente().compareTo(dbVers.getDtIniValAppartAmbiente()) != 0
                            || versRowBean.getDtFinValAppartAmbiente()
                                    .compareTo(dbVers.getDtFinValAppartAmbiente()) != 0) {

                        if (isStessoIntervallo(versRowBean.getDtIniValAppartAmbiente(),
                                versRowBean.getDtFinValAppartAmbiente(), dbVers.getDtIniValAppartAmbiente(),
                                dbVers.getDtFinValAppartAmbiente())) {
                            throw new ParerUserError(
                                    "Le date di inizio e di fine validità di appartenenza all’ambiente si sovrappongono a quelle definite sull’attuale appartenenza");
                        }

                        if (isStoricoPresente(idVers.longValue(), dbVers.getPigAmbienteVer().getIdAmbienteVers(),
                                versRowBean.getDtIniValAppartAmbiente(), versRowBean.getDtFinValAppartAmbiente())) {
                            throw new ParerUserError(
                                    "Le date di inizio e di fine validità di appartenenza all’ambiente si sovrappongono a quelle definite su una precedente appartenenza");
                        }

                        if (versRowBean.getDtIniValAppartAmbiente().compareTo(dbVers.getDtIniValAppartAmbiente()) < 0
                                && versRowBean.getDtFinValAppartAmbiente()
                                        .compareTo(dbVers.getDtFinValAppartAmbiente()) < 0) {
                            throw new ParerUserError(
                                    "Le date di inizio e fine validità impostate sull’ambiente sono inferiori a quelle precedentemente inserite");
                        }

                    }

                    if (versRowBean.getDtIniValAppartAmbiente().compareTo(dbVers.getDtIniValAppartAmbiente()) != 0
                            || versRowBean.getDtFinValAppartAmbiente()
                                    .compareTo(dbVers.getDtFinValAppartAmbiente()) != 0) {
                        PigStoricoVersAmbiente storicoVersAmbiente = new PigStoricoVersAmbiente();
                        storicoVersAmbiente.setPigVer(dbVers);
                        storicoVersAmbiente.setPigAmbienteVer(dbVers.getPigAmbienteVer());
                        storicoVersAmbiente.setDtIniVal(dbVers.getDtIniValAppartAmbiente());
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(versRowBean.getDtIniValAppartAmbiente());
                        cal.add(Calendar.DATE, -1);
                        Date nuovaData = dbVers.getDtFinValAppartAmbiente()
                                .before(versRowBean.getDtIniValAppartAmbiente()) ? dbVers.getDtFinValAppartAmbiente()
                                        : cal.getTime();
                        storicoVersAmbiente.setDtFineVal(nuovaData);
                        amministrazioneHelper.insertEntity(storicoVersAmbiente, true);
                    } else {
                        versRowBean.setString("infoMessage",
                                "Le date di appartenenza del versatore all’ambiente coincidono con quelle indicate sul precedente ambiente: è stata eseguita la modifica del versatore senza eseguire la storicizzazione");
                    }
                }

                // AGGIORNO IL VERSATORE
                vers = amministrazioneHelper.updatePigVers(idVers, versRowBean);

                // Gestione parametri amministrazione
                manageParametriPerVers(parametriAmministrazione, "ds_valore_param_applic_vers_amm", vers);
                // Gestione parametri conservazione
                manageParametriPerVers(parametriConservazione, "ds_valore_param_applic_vers_cons", vers);
                // Gestione parametri gestione
                manageParametriPerVers(parametriGestione, "ds_valore_param_applic_vers_gest", vers);

                amministrazioneHelper.getEntityManager().flush();

                // Aggiorno il parametro del prefisso
                String prefisso = configHelper.getValoreParamApplicByIdVers(Constants.DS_PREFISSO_PATH,
                        BigDecimal.valueOf(vers.getPigAmbienteVer().getIdAmbienteVers()),
                        BigDecimal.valueOf(vers.getIdVers()));

                vers.setDsPathInputFtp(prefisso + vers.getNmVers() + "/INPUT_FOLDER/");
                vers.setDsPathOutputFtp(prefisso + vers.getNmVers() + "/OUTPUT_FOLDER/");
                vers.setDsPathTrasf(prefisso + vers.getNmVers() + "/TRASFORMATI/");

                tiOper = Constants.TiOperReplic.MOD;
            } // INSERIMENTO
            else {
                BigDecimal idAmb = versRowBean.getIdAmbienteVers();
                PigAmbienteVers ambienteVers = amministrazioneHelper.getPigAmbienteVersById(idAmb);
                if (amministrazioneHelper.getPigVersByName(versRowBean.getNmVers(), idAmb) != null) {
                    throw new IncoherenceException("Nome Versatore gi\u00E0 utilizzato nel database.");
                }
                vers = (PigVers) Transform.rowBean2Entity(versRowBean);
                vers.setPigAmbienteVer(ambienteVers);

                vers.setDtIniValAppartEnteSiam(dtIniValAppartEnteSiam);
                vers.setDtFineValAppartEnteSiam(dtFineValAppartEnteSiam);

                if (versRowBean.getString("tipologia").equals("PRODUTTORE")) {
                    controlliCorrispondenzaSacer(idUserIamCor, idEnteConvenzEc, idOrganizIam, tiDichVers);
                    // MAC 29861 - ho passato il controllo, creo il versatore
                    amministrazioneHelper.insertEntity(vers, false);
                    // Inserisco la corrispondenza
                    PigDichVersSacer dichVersSacer = new PigDichVersSacer();
                    dichVersSacer.setIdOrganizIam(idOrganizIam);
                    dichVersSacer.setTiDichVers(tiDichVers);
                    dichVersSacer.setPigVer(vers);
                    amministrazioneHelper.insertEntity(dichVersSacer, false);
                } else {
                    // MAC 29861 - nessun il controllo, creo il versatore
                    amministrazioneHelper.insertEntity(vers, false);
                }

                amministrazioneHelper.getEntityManager().flush();

                if (tipologia.equals("PRODUTTORE")) {
                    vers.setIdEnteConvenz(idEnteConvenzEc);
                    vers.setIdEnteFornitEstern(null);
                    // Bug sistemato con Paolo, mancava SOGGETTO_ATTUATORE
                } else if (tipologia.equals("FORNITORE_ESTERNO") || tipologia.equals("SOGGETTO_ATTUATORE")) {
                    vers.setIdEnteConvenz(null);
                    vers.setIdEnteFornitEstern(idEnteFornitEstern);
                }

                tiOper = Constants.TiOperReplic.INS;
                versRowBean.setIdVers(new BigDecimal(vers.getIdVers()));

                // Gestione parametri
                for (PigParamApplicRowBean paramApplicRowBean : parametriAmministrazione) {
                    if (paramApplicRowBean.getString("ds_valore_param_applic_vers_amm") != null
                            && !paramApplicRowBean.getString("ds_valore_param_applic_vers_amm").equals("")) {
                        insertPigValoreParamApplic(null, vers, null, paramApplicRowBean.getIdParamApplic(), "VERS",
                                paramApplicRowBean.getString("ds_valore_param_applic_vers_amm"));
                    }
                }
                for (PigParamApplicRowBean paramApplicRowBean : parametriConservazione) {
                    if (paramApplicRowBean.getString("ds_valore_param_applic_vers_cons") != null
                            && !paramApplicRowBean.getString("ds_valore_param_applic_vers_cons").equals("")) {
                        insertPigValoreParamApplic(null, vers, null, paramApplicRowBean.getIdParamApplic(), "VERS",
                                paramApplicRowBean.getString("ds_valore_param_applic_vers_cons"));
                    }
                }
                for (PigParamApplicRowBean paramApplicRowBean : parametriGestione) {
                    if (paramApplicRowBean.getString("ds_valore_param_applic_vers_gest") != null
                            && !paramApplicRowBean.getString("ds_valore_param_applic_vers_gest").equals("")) {
                        insertPigValoreParamApplic(null, vers, null, paramApplicRowBean.getIdParamApplic(), "VERS",
                                paramApplicRowBean.getString("ds_valore_param_applic_vers_gest"));
                    }
                }
            }

            IamOrganizDaReplic insertedIamOrganizDaReplic = amministrazioneHelper.insertEntityIamOrganizDaReplic(vers,
                    tiOper);

            // Aggiorno il parametro del prefisso
            String prefisso = configHelper.getValoreParamApplicByIdVers(Constants.DS_PREFISSO_PATH,
                    BigDecimal.valueOf(vers.getPigAmbienteVer().getIdAmbienteVers()),
                    BigDecimal.valueOf(vers.getIdVers()));

            vers.setDsPathInputFtp(prefisso + vers.getNmVers() + "/INPUT_FOLDER/");
            vers.setDsPathOutputFtp(prefisso + vers.getNmVers() + "/OUTPUT_FOLDER/");
            vers.setDsPathTrasf(prefisso + vers.getNmVers() + "/TRASFORMATI/");

            // MEV 30790 - creo le cartelle necessarie su filesystem se non esistono.
            try {
                File basePath = new File(
                        configHelper.getValoreParamApplicByApplic(it.eng.xformer.common.Constants.ROOT_FTP)
                                + File.separator + prefisso + vers.getNmVers());

                xadConn = xadCf.getConnection();

                if (!xadConn.fileExists(basePath)) {
                    xadConn.createFile(basePath, true);
                }

                File path = new File(basePath + "/INPUT_FOLDER/");
                if (!xadConn.fileExists(path)) {
                    xadConn.createFile(path, true);
                }

                path = new File(basePath + "/OUTPUT_FOLDER/");
                if (!xadConn.fileExists(path)) {
                    xadConn.createFile(path, true);
                }

                path = new File(basePath + "/TRASFORMATI/");
                if (!xadConn.fileExists(path)) {
                    xadConn.createFile(path, true);
                }
            } catch (Exception ex) {
                log.error("Errore durante la creazione delle cartelle per il versatore " + vers.getNmVers() + " : "
                        + ex.getMessage());
                throw new ParerUserError("Errore tecnico nella creazione delle cartelle per il versatore.");
            }

            sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                    param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_VERSATORE, new BigDecimal(vers.getIdVers()),
                    param.getNomePagina());
            return insertedIamOrganizDaReplic;
        } finally {
            if (xadConn != null) {
                xadConn.close();
            }
        }
    }

    private boolean isStoricoPresente(long idVers, long idAmbienteVersExcluded, Date dtIniValAppartAmbiente,
            Date dtFinValAppartAmbiente) {
        return amministrazioneHelper.isStoricoPresente(idVers, idAmbienteVersExcluded, dtIniValAppartAmbiente,
                dtFinValAppartAmbiente);
    }

    private boolean isStessoIntervallo(Date dtIniValAppartAmbiente, Date dtFinValAppartAmbiente,
            Date dtIniValAppartAmbienteDB, Date dtFinValAppartAmbienteDB) {
        return (dtIniValAppartAmbiente.compareTo(dtIniValAppartAmbienteDB) >= 0
                && dtIniValAppartAmbiente.compareTo(dtFinValAppartAmbienteDB) <= 0)
                || (dtFinValAppartAmbiente.compareTo(dtIniValAppartAmbienteDB) >= 0
                        && dtFinValAppartAmbiente.compareTo(dtFinValAppartAmbienteDB) <= 0);
    }

    public long insertOrgEnteConvezOrg(BigDecimal idVers, BigDecimal idEnteSiam, Date dtIniVal, Date dtFineVal) {
        SIOrgEnteConvenzOrg enteConvenzOrg = new SIOrgEnteConvenzOrg();
        SIOrgEnteSiam enteSiam = amministrazioneHelper.findById(SIOrgEnteSiam.class, idEnteSiam);
        SIUsrOrganizIam organizIam = amministrazioneHelper.getSIUsrOrganizIam(idVers);
        enteConvenzOrg.setSiOrgEnteConvenz(enteSiam);
        enteConvenzOrg.setSiUsrOrganizIam(organizIam);
        enteConvenzOrg.setDtIniVal(dtIniVal);
        enteConvenzOrg.setDtFineVal(dtFineVal);
        amministrazioneHelper.insertEntity(enteConvenzOrg, true);
        return enteConvenzOrg.getIdEnteConvenzOrg();
    }

    private void manageParametriPerVers(PigParamApplicTableBean paramApplicTableBean, String nomeCampoValoreParamApplic,
            PigVers vers) {
        for (PigParamApplicRowBean paramApplicRowBean : paramApplicTableBean) {
            // Cancello il parametro se eliminato
            if (paramApplicRowBean.getBigDecimal("id_valore_param_applic") != null
                    && (paramApplicRowBean.getString(nomeCampoValoreParamApplic) == null
                            || paramApplicRowBean.getString(nomeCampoValoreParamApplic).equals(""))) {
                PigValoreParamApplic parametro = amministrazioneHelper.findById(PigValoreParamApplic.class,
                        paramApplicRowBean.getBigDecimal("id_valore_param_applic"));
                amministrazioneHelper.removeEntity(parametro, true);
            } // Modifico il parametro se modificato
            else if (paramApplicRowBean.getBigDecimal("id_valore_param_applic") != null
                    && paramApplicRowBean.getString(nomeCampoValoreParamApplic) != null
                    && !paramApplicRowBean.getString(nomeCampoValoreParamApplic).equals("")
                    // MEV22933
                    && !paramApplicRowBean.getString(nomeCampoValoreParamApplic)
                            .equals(it.eng.sacerasi.web.util.Constants.OBFUSCATED_STRING)) {
                PigValoreParamApplic parametro = amministrazioneHelper.findById(PigValoreParamApplic.class,
                        paramApplicRowBean.getBigDecimal("id_valore_param_applic"));
                parametro.setDsValoreParamApplic(paramApplicRowBean.getString(nomeCampoValoreParamApplic));
            } // Inserisco il parametro se nuovo
            else if (paramApplicRowBean.getBigDecimal("id_valore_param_applic") == null
                    && paramApplicRowBean.getString(nomeCampoValoreParamApplic) != null
                    && !paramApplicRowBean.getString(nomeCampoValoreParamApplic).equals("")) {
                insertPigValoreParamApplic(null, vers, null, paramApplicRowBean.getBigDecimal("id_param_applic"),
                        "VERS", paramApplicRowBean.getString(nomeCampoValoreParamApplic));
            }
        }
    }

    private void manageParametriPerAmbiente(PigParamApplicTableBean paramApplicTableBean,
            String nomeCampoValoreParamApplic, PigAmbienteVers ambienteVers) {
        for (PigParamApplicRowBean paramApplicRowBean : paramApplicTableBean) {
            // Cancello il parametro se eliminato
            if (paramApplicRowBean.getBigDecimal("id_valore_param_applic") != null
                    && (paramApplicRowBean.getString(nomeCampoValoreParamApplic) == null
                            || paramApplicRowBean.getString(nomeCampoValoreParamApplic).equals(""))) {
                PigValoreParamApplic parametro = amministrazioneHelper.findById(PigValoreParamApplic.class,
                        paramApplicRowBean.getBigDecimal("id_valore_param_applic"));
                amministrazioneHelper.removeEntity(parametro, true);
            } // Modifico il parametro se modificato
            else if (paramApplicRowBean.getBigDecimal("id_valore_param_applic") != null
                    && paramApplicRowBean.getString(nomeCampoValoreParamApplic) != null
                    && !paramApplicRowBean.getString(nomeCampoValoreParamApplic).equals("")) {
                PigValoreParamApplic parametro = amministrazioneHelper.findById(PigValoreParamApplic.class,
                        paramApplicRowBean.getBigDecimal("id_valore_param_applic"));
                parametro.setDsValoreParamApplic(paramApplicRowBean.getString(nomeCampoValoreParamApplic));
            } // Inserisco il parametro se nuovo
            else if (paramApplicRowBean.getBigDecimal("id_valore_param_applic") == null
                    && paramApplicRowBean.getString(nomeCampoValoreParamApplic) != null
                    && !paramApplicRowBean.getString(nomeCampoValoreParamApplic).equals("")) {
                insertPigValoreParamApplic(ambienteVers, null, null,
                        paramApplicRowBean.getBigDecimal("id_param_applic"), "AMBIENTE",
                        paramApplicRowBean.getString(nomeCampoValoreParamApplic));
            }
        }
    }

    private void manageParametriPerTipoOggetto(PigParamApplicTableBean paramApplicTableBean,
            String nomeCampoValoreParamApplic, PigTipoObject tipoObject) {
        for (PigParamApplicRowBean paramApplicRowBean : paramApplicTableBean) {
            // Cancello il parametro se eliminato
            if (paramApplicRowBean.getBigDecimal("id_valore_param_applic") != null
                    && (paramApplicRowBean.getString(nomeCampoValoreParamApplic) == null
                            || paramApplicRowBean.getString(nomeCampoValoreParamApplic).equals(""))) {
                PigValoreParamApplic parametro = amministrazioneHelper.findById(PigValoreParamApplic.class,
                        paramApplicRowBean.getBigDecimal("id_valore_param_applic"));
                amministrazioneHelper.removeEntity(parametro, true);
            } // Modifico il parametro se modificato
            else if (paramApplicRowBean.getBigDecimal("id_valore_param_applic") != null
                    && paramApplicRowBean.getString(nomeCampoValoreParamApplic) != null
                    && !paramApplicRowBean.getString(nomeCampoValoreParamApplic).equals("")) {
                PigValoreParamApplic parametro = amministrazioneHelper.findById(PigValoreParamApplic.class,
                        paramApplicRowBean.getBigDecimal("id_valore_param_applic"));
                parametro.setDsValoreParamApplic(paramApplicRowBean.getString(nomeCampoValoreParamApplic));
            } // Inserisco il parametro se nuovo
            else if (paramApplicRowBean.getBigDecimal("id_valore_param_applic") == null
                    && paramApplicRowBean.getString(nomeCampoValoreParamApplic) != null
                    && !paramApplicRowBean.getString(nomeCampoValoreParamApplic).equals("")) {
                insertPigValoreParamApplic(null, null, tipoObject, paramApplicRowBean.getBigDecimal("id_param_applic"),
                        "TIPO_OGGETTO", paramApplicRowBean.getString(nomeCampoValoreParamApplic));
            }
        }
    }

    /**
     * Rimuove la riga di parametri definita nel rowBean
     *
     * @param idValoreParamApplic
     *            id valore parametro applicativio
     *
     * @return true se eliminato con successo
     */
    public boolean deleteParametroAmbiente(BigDecimal idValoreParamApplic) {
        PigValoreParamApplic parametro;
        boolean result = false;
        try {
            parametro = amministrazioneHelper.findById(PigValoreParamApplic.class, idValoreParamApplic);
            // Rimuovo il record
            amministrazioneHelper.getEntityManager().remove(parametro);
            amministrazioneHelper.getEntityManager().flush();
            result = true;
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
        return result;
    }

    /**
     * Rimuove la riga di parametri definita nel rowBean
     *
     * @param param
     *            parametri per il logging
     * @param idValoreParamApplic
     *            id valore parametro applicativo
     *
     * @return true se eliminato con successo
     */
    public boolean deleteParametroVersatore(LogParam param, BigDecimal idValoreParamApplic) {
        PigValoreParamApplic parametro;
        boolean result = false;
        try {
            parametro = amministrazioneHelper.findById(PigValoreParamApplic.class, idValoreParamApplic);
            Long idVers = parametro.getPigVer().getIdVers();
            // Rimuovo il record
            amministrazioneHelper.getEntityManager().remove(parametro);
            amministrazioneHelper.getEntityManager().flush();
            sacerLogEjb.log(param.getTransactionLogContext(), configHelper.getParamApplicApplicationName(),
                    param.getNomeUtente(), param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_VERSATORE,
                    new BigDecimal(idVers), param.getNomePagina());
            result = true;
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
        return result;
    }

    /**
     * Rimuove la riga di parametri definita nel rowBean
     *
     * @param param
     *            parametri per il logging
     * @param idValoreParamApplic
     *            id valore parametro applicativo
     *
     * @return true se eliminato con successo
     */
    public boolean deleteParametroTipoOggetto(LogParam param, BigDecimal idValoreParamApplic) {
        PigValoreParamApplic parametro;
        boolean result = false;
        try {
            parametro = amministrazioneHelper.findById(PigValoreParamApplic.class, idValoreParamApplic);
            Long idTipoObject = parametro.getPigTipoObject().getIdTipoObject();
            // Rimuovo il record
            amministrazioneHelper.getEntityManager().remove(parametro);
            amministrazioneHelper.getEntityManager().flush();
            sacerLogEjb.log(param.getTransactionLogContext(), configHelper.getParamApplicApplicationName(),
                    param.getNomeUtente(), param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_TIPO_OGGETTO_VERSABILE,
                    new BigDecimal(idTipoObject), param.getNomePagina());
            result = true;
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
        return result;
    }

    private void insertPigValoreParamApplic(PigAmbienteVers ambienteVers, PigVers vers, PigTipoObject tipoObject,
            BigDecimal idParamApplic, String tiAppart, String dsValoreParamApplic) {
        PigValoreParamApplic valoreParamApplic = new PigValoreParamApplic();
        valoreParamApplic.setPigParamApplic(amministrazioneHelper.findById(PigParamApplic.class, idParamApplic));
        valoreParamApplic.setDsValoreParamApplic(dsValoreParamApplic);
        valoreParamApplic.setTiAppart(tiAppart);
        valoreParamApplic.setPigVer(vers);
        valoreParamApplic.setPigAmbienteVer(ambienteVers);
        valoreParamApplic.setPigTipoObject(tipoObject);
        amministrazioneHelper.insertEntity(valoreParamApplic, true);
    }

    public void removeVers(LogParam param, BigDecimal idVers) throws IncoherenceException {
        AmministrazioneEjb me = context.getBusinessObject(AmministrazioneEjb.class);
        IamOrganizDaReplic replic = me.deleteVers(param, idVers);
        me.replicateToIam(replic);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public IamOrganizDaReplic deleteVers(LogParam param, BigDecimal idVers) throws IncoherenceException {
        PigVers vers = amministrazioneHelper.getPigVersById(idVers);
        if (vers.getPigTipoObjects() != null && !vers.getPigTipoObjects().isEmpty()) {
            throw new IncoherenceException("Rimozione versatore non riuscita: tipi oggetto associati al versatore");
        }
        if ((vers.getPigSessioneIngests() != null && !vers.getPigSessioneIngests().isEmpty())
                || (vers.getPigSessioneRecups() != null && !vers.getPigSessioneRecups().isEmpty())) {
            throw new IncoherenceException(
                    "Eliminazione del versatore non possibile: il versatore è stato utilizzato per precedenti versamenti");
        }
        List<ObjectsToLogBefore> listaOggetti = sacerLogEjb.logBefore(param.getTransactionLogContext(),
                param.getNomeApplicazione(), param.getNomeUtente(), param.getNomeAzione(),
                SacerLogConstants.TIPO_OGGETTO_VERSATORE, new BigDecimal(vers.getIdVers()), param.getNomePagina());
        List<ObjectsToLogBefore> listaOggettiModify = ObjectsToLogBefore.filterObjectsForModifying(listaOggetti);
        List<ObjectsToLogBefore> listaOggettiDelete = ObjectsToLogBefore.filterObjectsForDeletion(listaOggetti);
        sacerLogEjb.logAfter(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                param.getNomeAzione(), listaOggettiDelete, param.getNomePagina());
        sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_VERSATORE, new BigDecimal(vers.getIdVers()),
                param.getNomePagina());
        amministrazioneHelper.removeEntity(vers, true);
        sacerLogEjb.logAfter(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                param.getNomeAzione(), listaOggettiModify, param.getNomePagina());

        return amministrazioneHelper.insertEntityIamOrganizDaReplic(vers, Constants.TiOperReplic.CANC);
    }

    public BaseTableInterface getUsrVAbilAmbXverTableBean(long idUser, String nmApplic) {
        BaseTable table = new BaseTable();
        List<UsrVAbilAmbXver> ambientiAbilitati = amministrazioneHelper.getUsrVAbilAmbXverList(idUser, nmApplic);
        for (UsrVAbilAmbXver ambiente : ambientiAbilitati) {
            BaseRow row = new BaseRow();
            row.setBigDecimal("id_ambiente", ambiente.getUsrVAbilAmbXverId().getIdOrganizApplic());
            row.setString("nm_ambiente", ambiente.getNmOrganiz());
            table.add(row);
        }
        return table;
    }

    /*
     * TIPO OBJ
     */
    public PigTipoObjectTableBean getPigTipoObjectTableBean(BigDecimal idVers) {
        PigTipoObjectTableBean pigTipoObjectTableBean = new PigTipoObjectTableBean();
        List<PigTipoObject> list = amministrazioneHelper.getPigTipoObjectList(idVers);
        try {
            if (!list.isEmpty()) {
                pigTipoObjectTableBean = (PigTipoObjectTableBean) Transform.entities2TableBean(list);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return pigTipoObjectTableBean;
    }

    public PigTipoObjectRowBean getPigTipoObjectRowBean(BigDecimal idTipoObj) throws EMFError {
        return (getTipoObject(idTipoObj, null, null));
    }

    public PigTipoObjectRowBean getPigTipoObjectRowBean(String nmTipoObj, BigDecimal idVers) throws EMFError {
        return (getTipoObject(BigDecimal.ZERO, nmTipoObj, idVers));
    }

    private PigTipoObjectRowBean getTipoObject(BigDecimal idTipoObj, String nmTipoObj, BigDecimal idVers)
            throws EMFError {
        PigTipoObjectRowBean tipoObjectRowBean = null;
        PigTipoObject tipoObject = new PigTipoObject();
        if (idTipoObj == BigDecimal.ZERO && nmTipoObj != null && idVers != null) {
            tipoObject = amministrazioneHelper.getPigTipoObjectByName(nmTipoObj, idVers);
        }
        if (idTipoObj != BigDecimal.ZERO && nmTipoObj == null) {
            tipoObject = amministrazioneHelper.getPigTipoObjectById(idTipoObj);
        }
        if (tipoObject != null) {
            try {
                tipoObjectRowBean = (PigTipoObjectRowBean) Transform.entity2RowBean(tipoObject);
                tipoObjectRowBean.setString("nm_ambiente_vers",
                        tipoObject.getPigVer().getPigAmbienteVer().getNmAmbienteVers());
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
        return tipoObjectRowBean;
    }

    public PigTipoObjectTableBean getPigTipoObjectNoDaTrasfAbilitatiTableBean(BigDecimal idVers, Long idUser) {
        PigTipoObjectTableBean table = new PigTipoObjectTableBean();
        List<PigTipoObject> list = amministrazioneHelper.getPigTipoObjectNoDaTrasfAbilitatiList(idVers, idUser);
        if (list != null && !list.isEmpty()) {
            try {
                table = (PigTipoObjectTableBean) Transform.entities2TableBean(list);
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                    | IllegalArgumentException | InvocationTargetException ex) {
                String msg = "Errore durante il recupero della lista dei tipi oggetto (esclusi quelli DA_TRASFORMARE) "
                        + ExceptionUtils.getRootCauseMessage(ex);
                log.error(msg, ex);
            }
        }
        return table;
    }

    public void updatePigTipoObj(LogParam param, long idUserIamCor, BigDecimal idTipoObj,
            PigTipoObjectRowBean tipoObjRowBean, PigTipoFileObjectTableBean tipoFileObjectTableBean,
            PigParamApplicTableBean parametriAmministrazioneTipoOggetto,
            PigParamApplicTableBean parametriConservazioneTipoOggetto,
            PigParamApplicTableBean parametriGestioneTipoOggetto, String tiDichVers, BigDecimal idOrganizIam)
            throws IncoherenceException, ParerUserError {
        AmministrazioneEjb me = context.getBusinessObject(AmministrazioneEjb.class);
        IamOrganizDaReplic replic = me.saveTipoObj(param, idUserIamCor, idTipoObj, tipoObjRowBean,
                tipoFileObjectTableBean, true, parametriAmministrazioneTipoOggetto, parametriConservazioneTipoOggetto,
                parametriGestioneTipoOggetto, tiDichVers, idOrganizIam);
        me.replicateToIam(replic);
    }

    public void insertPigTipoObj(LogParam param, long idUserIamCor, PigTipoObjectRowBean tipoObjRowBean,
            PigParamApplicTableBean parametriAmministrazioneTipoOggetto,
            PigParamApplicTableBean parametriConservazioneTipoOggetto,
            PigParamApplicTableBean parametriGestioneTipoOggetto, String tiDichVers, BigDecimal idOrganizIam)
            throws IncoherenceException, ParerUserError {
        AmministrazioneEjb me = context.getBusinessObject(AmministrazioneEjb.class);
        IamOrganizDaReplic replic = me.saveTipoObj(param, idUserIamCor, null, tipoObjRowBean, null, false,
                parametriAmministrazioneTipoOggetto, parametriConservazioneTipoOggetto, parametriGestioneTipoOggetto,
                tiDichVers, idOrganizIam);
        me.replicateToIam(replic);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public IamOrganizDaReplic saveTipoObj(LogParam param, long idUserIamCor, BigDecimal idTipoObj,
            PigTipoObjectRowBean tipoObjRowBean, PigTipoFileObjectTableBean tipoFileObjectTableBean, boolean update,
            PigParamApplicTableBean parametriAmministrazioneTipoOggetto,
            PigParamApplicTableBean parametriConservazioneTipoOggetto,
            PigParamApplicTableBean parametriGestioneTipoOggetto, String tiDichVers, BigDecimal idOrganizIam)
            throws IncoherenceException, ParerUserError {
        PigVers vers = amministrazioneHelper.getPigVersById(tipoObjRowBean.getIdVers());
        PigTipoObject tipoObj = null;
        if (update) {
            PigTipoObject tipoObjDB = amministrazioneHelper.findById(PigTipoObject.class, idTipoObj);
            tipoObj = amministrazioneHelper.getPigTipoObjectByName(tipoObjRowBean.getNmTipoObject(),
                    tipoObjRowBean.getIdVers());
            if (tipoObj != null && tipoObjDB.getIdTipoObject() != tipoObj.getIdTipoObject()) {
                throw new IncoherenceException(
                        "Nome tipo oggetto già associato a questo versatore, utilizzare altro nome");
            } else {
                tipoObj = amministrazioneHelper.findById(PigTipoObject.class, idTipoObj);
            }

            // Se ho modificato la Corrispondenza Sacer...
            if (tipoObj.getPigDichVersSacerTipoObjs() != null) {
                BigDecimal oldIdOrganizIam = null;
                if (tipoObj.getPigDichVersSacerTipoObjs().isEmpty()) {
                    oldIdOrganizIam = BigDecimal.ZERO;
                } else {
                    oldIdOrganizIam = tipoObj.getPigDichVersSacerTipoObjs().get(0).getIdOrganizIam();
                }

                if (idOrganizIam == null) {
                    idOrganizIam = BigDecimal.ZERO;
                }

                // Se c'è stata una qualunque modifica (inserimento, modifica, cancellazione) sulla corrispondenza
                if (oldIdOrganizIam.compareTo(idOrganizIam) != 0) {
                    // ...controllo se era possibile
                    PigDichVersSacerTipoObj dichVersSacer = amministrazioneHelper.getPigDichVersSacerTipoObj(idTipoObj,
                            oldIdOrganizIam);
                    // Se ho cancellato
                    if (oldIdOrganizIam.compareTo(BigDecimal.ZERO) != 0
                            && idOrganizIam.compareTo(BigDecimal.ZERO) == 0) {
                        amministrazioneHelper.removeEntity(dichVersSacer, false);
                    } else {
                        if (dichVersSacer != null) {
                            PigVChkDelDichverssacerobj del = amministrazioneHelper.findViewById(
                                    PigVChkDelDichverssacerobj.class,
                                    BigDecimal.valueOf(dichVersSacer.getIdDichVersSacerTipoObj()));
                            if (!(del != null && del.getFlDelDchVersSacerTiobjOk() != null
                                    && del.getFlDelDchVersSacerTiobjOk().equals("1"))) {
                                throw new IncoherenceException(
                                        "“La corrispondenza a Sacer non e’ modificabile perché esistono oggetti con stato IN_ATTESA_FILE o IN_ATTESA_SCHED che potrebbero contenere unità documentarie da versare in strutture giustificate dalla corrispondenza");
                            } else {
                                // Se ho modificato la corrispondenza ed è andato tutto bene
                                if (idOrganizIam.compareTo(BigDecimal.ZERO) != 0) {
                                    controlliCorrispondenzaSacer(idUserIamCor, vers.getIdEnteConvenz(), idOrganizIam,
                                            tiDichVers);
                                    checkCorrispondenzaConVersatore(BigDecimal.valueOf(tipoObj.getPigVer().getIdVers()),
                                            idOrganizIam, tiDichVers);
                                    dichVersSacer.setIdOrganizIam(idOrganizIam);
                                    dichVersSacer.setTiDichVers(tiDichVers);
                                }
                            }
                        } // Se invece prima non c'era la corrispondenza e adesso c'è, la sto inserendo nuova
                        else {
                            controlliCorrispondenzaSacer(idUserIamCor, vers.getIdEnteConvenz(), idOrganizIam,
                                    tiDichVers);
                            checkCorrispondenzaConVersatore(BigDecimal.valueOf(tipoObj.getPigVer().getIdVers()),
                                    idOrganizIam, tiDichVers);
                            // Inserisco la corrispondenza
                            PigDichVersSacerTipoObj dichVersSacerNew = new PigDichVersSacerTipoObj();
                            dichVersSacerNew.setIdOrganizIam(idOrganizIam);
                            dichVersSacerNew.setTiDichVers(tiDichVers);
                            dichVersSacerNew.setPigTipoObject(tipoObj);
                            amministrazioneHelper.insertEntity(dichVersSacerNew, false);
                        }
                    }
                }
            }

            amministrazioneHelper.updateTipoObj(idTipoObj, tipoObjRowBean);

            // Gestione parametri amministrazione
            manageParametriPerTipoOggetto(parametriAmministrazioneTipoOggetto,
                    "ds_valore_param_applic_tipo_oggetto_amm", tipoObj);
            // Gestione parametri conservazione
            manageParametriPerTipoOggetto(parametriConservazioneTipoOggetto, "ds_valore_param_applic_tipo_oggetto_cons",
                    tipoObj);
            // Gestione parametri gestione
            manageParametriPerTipoOggetto(parametriGestioneTipoOggetto, "ds_valore_param_applic_tipo_oggetto_gest",
                    tipoObj);

            /*
             * GESTIONE DEI FILE TIPO OBJECT, CHE VANNO MODIFICATI A SECONDA DEI VALORI DI "Tipo Versamento File" e
             * "Controllo Hash"
             */
            if (tipoFileObjectTableBean != null) {
                for (PigTipoFileObjectRowBean tipoFileObjectRowBean : tipoFileObjectTableBean) {
                    updateTipoFileObjectDaTipoObject(tipoObjRowBean.getTiVersFile(), tipoObjRowBean.getFlContrHash(),
                            tipoFileObjectRowBean.getIdTipoFileObject());
                }
            }
            amministrazioneHelper.getEntityManager().flush();
        } else {
            tipoObj = new PigTipoObject();
            if (amministrazioneHelper.getPigTipoObjectByName(tipoObjRowBean.getNmTipoObject(),
                    tipoObjRowBean.getIdVers()) != null) {
                throw new IncoherenceException("Nome Tipo Oggetto già associato a questo Versatore nel database.");
            }
            try {
                tipoObj = (PigTipoObject) Transform.rowBean2Entity(tipoObjRowBean);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
            if (tipoObjRowBean.getIdTrasf() != null) {
                XfoTrasf xfoTrasf = amministrazioneHelper.findById(XfoTrasf.class, tipoObjRowBean.getIdTrasf());
                tipoObj.setXfoTrasf(xfoTrasf);
            }
            tipoObj.setPigVer(vers);

            // controllo la corrispondenza
            if (tiDichVers != null && idOrganizIam != null) {
                controlliCorrispondenzaSacer(idUserIamCor, vers.getIdEnteConvenz(), idOrganizIam, tiDichVers);
                checkCorrispondenzaConVersatore(BigDecimal.valueOf(tipoObj.getPigVer().getIdVers()), idOrganizIam,
                        tiDichVers);
            }

            amministrazioneHelper.insertEntity(tipoObj, true);
            tipoObjRowBean.setIdTipoObject(new BigDecimal(tipoObj.getIdTipoObject()));

            amministrazioneHelper.insertEntity(tipoObj, true);
            tipoObjRowBean.setIdTipoObject(new BigDecimal(tipoObj.getIdTipoObject()));

            // Inserisco la corrispondenza
            if (tiDichVers != null && idOrganizIam != null) {
                PigDichVersSacerTipoObj dichVersSacer = new PigDichVersSacerTipoObj();
                dichVersSacer.setIdOrganizIam(idOrganizIam);
                dichVersSacer.setTiDichVers(tiDichVers);
                dichVersSacer.setPigTipoObject(tipoObj);
                amministrazioneHelper.insertEntity(dichVersSacer, false);
            }

            // Gestione parametri
            for (PigParamApplicRowBean paramApplicRowBean : parametriAmministrazioneTipoOggetto) {
                if (paramApplicRowBean.getString("ds_valore_param_applic_tipo_oggetto_amm") != null
                        && !paramApplicRowBean.getString("ds_valore_param_applic_tipo_oggetto_amm").equals("")) {
                    insertPigValoreParamApplic(null, null, tipoObj, paramApplicRowBean.getIdParamApplic(),
                            "TIPO_OGGETTO", paramApplicRowBean.getString("ds_valore_param_applic_tipo_oggetto_amm"));
                }
            }
            for (PigParamApplicRowBean paramApplicRowBean : parametriConservazioneTipoOggetto) {
                if (paramApplicRowBean.getString("ds_valore_param_applic_tipo_oggetto_cons") != null
                        && !paramApplicRowBean.getString("ds_valore_param_applic_tipo_oggetto_cons").equals("")) {
                    insertPigValoreParamApplic(null, null, tipoObj, paramApplicRowBean.getIdParamApplic(),
                            "TIPO_OGGETTO", paramApplicRowBean.getString("ds_valore_param_applic_tipo_oggetto_cons"));
                }
            }
            for (PigParamApplicRowBean paramApplicRowBean : parametriGestioneTipoOggetto) {
                if (paramApplicRowBean.getString("ds_valore_param_applic_tipo_oggetto_gest") != null
                        && !paramApplicRowBean.getString("ds_valore_param_applic_tipo_oggetto_gest").equals("")) {
                    insertPigValoreParamApplic(null, null, tipoObj, paramApplicRowBean.getIdParamApplic(),
                            "TIPO_OGGETTO", paramApplicRowBean.getString("ds_valore_param_applic_tipo_oggetto_gest"));
                }
            }

        }
        sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_TIPO_OGGETTO_VERSABILE,
                update ? idTipoObj : new BigDecimal(tipoObj.getIdTipoObject()), param.getNomePagina());
        return amministrazioneHelper.insertEntityIamOrganizDaReplic(vers, Constants.TiOperReplic.MOD);
    }

    public void updateTipoFileObjectDaTipoObject(String tiVersFile, String flContrHash, BigDecimal idTipoFileObject) {
        PigTipoFileObject tipoFileObject = amministrazioneHelper.getPigTipoFileObjectById(idTipoFileObject);
        if (tiVersFile.equals(it.eng.sacerasi.common.Constants.TipoVersamento.ZIP_CON_XML_SACER.name())
                || tiVersFile.equals(it.eng.sacerasi.common.Constants.TipoVersamento.ZIP_NO_XML_SACER.name())
                || tiVersFile.equals(it.eng.sacerasi.common.Constants.TipoVersamento.DA_TRASFORMARE.name())) {

            tipoFileObject.setNmTipoDocSacer(null);
            tipoFileObject.setTiDocSacer(null);
            tipoFileObject.setNmTipoStrutDocSacer(null);
            tipoFileObject.setNmTipoCompDocSacer(null);
            tipoFileObject.setNmFmtFileVersSacer(null);
            tipoFileObject.setFlVerFirmaFmtSacer(null);
            tipoFileObject.setNmFmtFileCalcSacer(null);
            tipoFileObject.setDsFmtRapprEstesoCalcSacer(null);
            tipoFileObject.setDsFmtRapprCalcSacer(null);
            tipoFileObject.setFlCalcHashSacer(null);

            if (flContrHash.equals("1")) {
                tipoFileObject.setTiCalcHashSacer(it.eng.sacerasi.common.Constants.HashCalcType.NOTIFICATO.name());
            } else {
                tipoFileObject.setTiCalcHashSacer(null);
            }
        } else if (tiVersFile.equals(it.eng.sacerasi.common.Constants.TipoVersamento.NO_ZIP.name())) {
            if (flContrHash.equals("0") && tipoFileObject.getFlCalcHashSacer() != null
                    && tipoFileObject.getFlCalcHashSacer().equals("1")) {
                tipoFileObject.setTiCalcHashSacer(null);
            }
        }
    }

    public void removeTipoObj(LogParam param, PigTipoObjectRowBean tipoObjectRowBean) throws IncoherenceException {
        AmministrazioneEjb me = context.getBusinessObject(AmministrazioneEjb.class);

        // MAC 28491 - rimuovo anche il tipo file oggetto.
        PigTipoFileObjectTableBean pigTipoFileObjectTableBean = me
                .getPigTipoFileObjectTableBean(tipoObjectRowBean.getIdTipoObject());
        Iterator<PigTipoFileObjectRowBean> tFOIterator = pigTipoFileObjectTableBean.iterator();
        while (tFOIterator.hasNext()) {
            PigTipoFileObjectRowBean row = tFOIterator.next();
            me.removeTipoFileObj(param, row);
        }

        IamOrganizDaReplic replic = me.deleteTipoObj(param, tipoObjectRowBean);
        me.replicateToIam(replic);
    }

    /*
     * CORRISPONDENZE IN SACER PER VERSATORE
     */
    public PigDichVersSacerTableBean getPigDichVersSacerTableBean(BigDecimal idVers) {
        PigDichVersSacerTableBean dichVersSacerTableBean = new PigDichVersSacerTableBean();
        List<Object[]> list = amministrazioneHelper.getPigDichVersSacerList(idVers);
        try {
            for (Object[] objects : list) {
                PigDichVersSacerRowBean dichVersSacerRowBean = (PigDichVersSacerRowBean) Transform
                        .entity2RowBean(objects[0]);
                dichVersSacerRowBean.setString("dl_composito_organiz", (String) objects[1]);
                dichVersSacerTableBean.add(dichVersSacerRowBean);
            }
        } catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException | InstantiationException
                | NoSuchMethodException | InvocationTargetException e) {
            log.error(e.getMessage());
        }
        return dichVersSacerTableBean;
    }

    public PigDichVersSacerRowBean getPigDichVersSacerRowBean(BigDecimal idDichVersSacer) {
        PigDichVersSacerRowBean dichVersSacerRowBean = new PigDichVersSacerRowBean();
        PigDichVersSacer dichVersSacer = amministrazioneHelper.findById(PigDichVersSacer.class, idDichVersSacer);
        try {
            dichVersSacerRowBean = (PigDichVersSacerRowBean) Transform.entity2RowBean(dichVersSacer);
        } catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException | InstantiationException
                | NoSuchMethodException | InvocationTargetException e) {
            log.error(e.getMessage());
        }
        return dichVersSacerRowBean;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void deletePigDichVersSacer(LogParam param, BigDecimal idDichVersSacer) throws ParerUserError {
        // Verifico se la corrispondenza è eliminabile
        PigVChkDelDichverssacer check = amministrazioneHelper.findViewById(PigVChkDelDichverssacer.class,
                idDichVersSacer);
        if (check.getFlDelDchVersSacerOk().equals("0")) {
            throw new ParerUserError(
                    "La corrispondenza a Sacer non è eliminabile perché sono presenti oggetti con stato = IN_ATTESA_FILE o IN_ATTESA_SCHED che potrebbero contenere unità documentarie da versare in strutture giustificate dalla corrispondenza");
        }
        PigDichVersSacer dichVersSacer = amministrazioneHelper.findById(PigDichVersSacer.class, idDichVersSacer);
        BigDecimal idVers = new BigDecimal(dichVersSacer.getPigVer().getIdVers());
        amministrazioneHelper.removeEntity(dichVersSacer, true);
        sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_VERSATORE, idVers, param.getNomePagina());
    }

    /*
     * TIPO FILE OBJ
     */
    public PigTipoFileObjectTableBean getPigTipoFileObjectTableBean(BigDecimal idTipoObj) {
        PigTipoFileObjectTableBean pigTipoFileObjectTableBean = new PigTipoFileObjectTableBean();
        List<PigTipoFileObject> list = amministrazioneHelper.getPigTipoFileObjectList(idTipoObj);
        try {
            if (!list.isEmpty()) {
                pigTipoFileObjectTableBean = (PigTipoFileObjectTableBean) Transform.entities2TableBean(list);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return pigTipoFileObjectTableBean;
    }

    public PigTipoFileObjectRowBean getPigTipoFileObjectRowBean(BigDecimal idTipoFileObj) {
        return (getTipoFileObject(idTipoFileObj, null, null));
    }

    public PigTipoFileObjectRowBean getPigTipoFileObjectRowBean(String nmTipoFileObj, BigDecimal idTipoObj) {
        return (getTipoFileObject(BigDecimal.ZERO, nmTipoFileObj, idTipoObj));
    }

    private PigTipoFileObjectRowBean getTipoFileObject(BigDecimal idTipoFileObj, String nmTipoFileObj,
            BigDecimal idTipoObj) {
        PigTipoFileObjectRowBean tipoFileObjectRowBean = null;
        PigTipoFileObject tipoFileObject = new PigTipoFileObject();
        if (idTipoFileObj == BigDecimal.ZERO && nmTipoFileObj != null) {
            tipoFileObject = amministrazioneHelper.getPigTipoFileObjectByName(nmTipoFileObj, idTipoObj);
        }
        if (idTipoFileObj != BigDecimal.ZERO && nmTipoFileObj == null) {
            tipoFileObject = amministrazioneHelper.getPigTipoFileObjectById(idTipoFileObj);
        }
        if (tipoFileObject != null) {
            try {
                tipoFileObjectRowBean = (PigTipoFileObjectRowBean) Transform.entity2RowBean(tipoFileObject);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
        return tipoFileObjectRowBean;
    }

    public void updatePigTipoFileObj(LogParam param, BigDecimal idTipoFileObj,
            PigTipoFileObjectRowBean tipoFileObjRowBean) throws EMFError, IncoherenceException {
        PigTipoFileObject dbTipoFileObj = amministrazioneHelper.getPigTipoFileObjectByName(
                tipoFileObjRowBean.getNmTipoFileObject(), tipoFileObjRowBean.getIdTipoObject());
        if (dbTipoFileObj != null && dbTipoFileObj.getIdTipoFileObject() != idTipoFileObj.longValue()) {
            throw new IncoherenceException("Nome tipo file già associato a questo tipo oggetto, utilizzare altro nome");
        }
        PigTipoObject tipoObj = amministrazioneHelper.getPigTipoObjectById(tipoFileObjRowBean.getIdTipoObject());
        PigTipoFileObject tipoFileObj = new PigTipoFileObject();
        try {
            tipoFileObj = (PigTipoFileObject) Transform.rowBean2Entity(tipoFileObjRowBean);
            tipoFileObj.setIdTipoFileObject(idTipoFileObj.longValue());
            tipoFileObj.setPigTipoObject(tipoObj);
        } catch (Exception e) {
            throw new EMFError(EMFError.ERROR, e);
        }
        amministrazioneHelper.updatePig(tipoFileObj);
        amministrazioneHelper.getEntityManager().flush();
        sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_TIPO_OGGETTO_VERSABILE,
                new BigDecimal(tipoObj.getIdTipoObject()), param.getNomePagina());
    }

    public void insertPigTipoFileObj(LogParam param, PigTipoFileObjectRowBean tipoFileObjRowBean)
            throws IncoherenceException {
        PigTipoFileObject tipoFileObj = new PigTipoFileObject();
        PigTipoObject tipoObj = amministrazioneHelper.getPigTipoObjectById(tipoFileObjRowBean.getIdTipoObject());
        if (amministrazioneHelper.getPigTipoFileObjectByName(tipoFileObjRowBean.getNmTipoFileObject(),
                tipoFileObjRowBean.getIdTipoObject()) != null) {
            throw new IncoherenceException("Nome Versatore già presente nel database.");
        }
        try {
            tipoFileObj = (PigTipoFileObject) Transform.rowBean2Entity(tipoFileObjRowBean);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        tipoFileObj.setPigTipoObject(tipoObj);
        amministrazioneHelper.insertEntity(tipoFileObj, true);
        tipoFileObjRowBean.setIdTipoFileObject(new BigDecimal(tipoFileObj.getIdTipoFileObject()));
        amministrazioneHelper.getEntityManager().flush();
        sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_TIPO_OGGETTO_VERSABILE,
                new BigDecimal(tipoObj.getIdTipoObject()), param.getNomePagina());
    }

    public void removeTipoFileObj(LogParam param, PigTipoFileObjectRowBean tipoFileObjectRowBean)
            throws IncoherenceException {
        PigTipoFileObject tipoFileObj = amministrazioneHelper
                .getPigTipoFileObjectById(tipoFileObjectRowBean.getIdTipoFileObject());
        List<PigFileObject> lista = amministrazioneHelper
                .getPigFileObjectListByTipoFileObject(tipoFileObjectRowBean.getIdTipoFileObject());
        if (!lista.isEmpty()) {
            throw new IncoherenceException("Rimozione versatore non riuscita: tipo oggetto utilizzato in versamenti");
        }
        amministrazioneHelper.removeEntity(tipoFileObj, true);
        sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_TIPO_OGGETTO_VERSABILE,
                new BigDecimal(tipoFileObj.getPigTipoObject().getIdTipoObject()), param.getNomePagina());
    }

    /*
     * SOP CLASS
     */
    public PigSopClassDicomTableBean getPigSopClassDicomTableBean(String cdSopClassDicom, String dsSopClassDicom) {
        PigSopClassDicomTableBean sopClassDicomTableBean = new PigSopClassDicomTableBean();
        List<PigSopClassDicom> listaSopClass = amministrazioneHelper.getPigSopClassDicomList(cdSopClassDicom,
                dsSopClassDicom);
        try {
            if (!listaSopClass.isEmpty()) {
                sopClassDicomTableBean = (PigSopClassDicomTableBean) Transform.entities2TableBean(listaSopClass);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return sopClassDicomTableBean;
    }

    public PigSopClassDicomRowBean getPigSopClassDicomRowBean(BigDecimal idSopClass) {
        return (getPigSopClassDicom(idSopClass, null));
    }

    public PigSopClassDicomRowBean getPigSopClassDicomRowBean(String nmSopClass) {
        return (getPigSopClassDicom(BigDecimal.ZERO, nmSopClass));
    }

    private PigSopClassDicomRowBean getPigSopClassDicom(BigDecimal idSopClass, String nmSopClass) {
        PigSopClassDicomRowBean sopClassDicomRowBean = null;
        PigSopClassDicom sopClassDicom = new PigSopClassDicom();
        if (idSopClass == BigDecimal.ZERO && nmSopClass != null) {
            sopClassDicom = amministrazioneHelper.getPigSopClassDicomByName(nmSopClass);
        }
        if (idSopClass != BigDecimal.ZERO && nmSopClass == null) {
            sopClassDicom = amministrazioneHelper.getPigSopClassDicomById(idSopClass);
        }
        if (idSopClass != null) {
            try {
                sopClassDicomRowBean = (PigSopClassDicomRowBean) Transform.entity2RowBean(sopClassDicom);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
        return sopClassDicomRowBean;
    }

    public void updatePigSopClassDicom(BigDecimal idSopClass, PigSopClassDicomRowBean sopClassRowBean) throws EMFError {
        PigSopClassDicom sopClassDicom = amministrazioneHelper.getPigSopClassDicomById(idSopClass);
        try {
            sopClassDicom.setCdSopClassDicom(sopClassRowBean.getCdSopClassDicom());
            sopClassDicom.setDsSopClassDicom(sopClassRowBean.getDsSopClassDicom());
        } catch (Exception e) {
            throw new EMFError(EMFError.ERROR, e);
        }
        amministrazioneHelper.updatePig(sopClassDicom);
    }

    public void insertPigSopClassDicom(PigSopClassDicomRowBean sopClassRowBean) throws IncoherenceException {
        PigSopClassDicom sopClassDicom = new PigSopClassDicom();
        if (amministrazioneHelper.getPigSopClassDicomByName(sopClassRowBean.getCdSopClassDicom()) != null) {
            throw new IncoherenceException("Codice SopClass già presente nel database");
        }
        try {
            sopClassDicom = (PigSopClassDicom) Transform.rowBean2Entity(sopClassRowBean);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        amministrazioneHelper.insertEntity(sopClassDicom, true);
    }

    /*
     * SOPCLASSDICOMVERS
     */
    public PigSopClassDicomVersTableBean getPigSopClassDicomVersTableBean(BigDecimal idSopClassDicom,
            BigDecimal idVers) {
        PigSopClassDicomVersTableBean sopClassDicomVersTableBean = new PigSopClassDicomVersTableBean();
        List<PigSopClassDicomVers> listaSopClassDicomVers = amministrazioneHelper
                .getPigSopClassDicomVersList(idSopClassDicom, idVers);
        try {
            if (!listaSopClassDicomVers.isEmpty()) {
                sopClassDicomVersTableBean = (PigSopClassDicomVersTableBean) Transform
                        .entities2TableBean(listaSopClassDicomVers);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return sopClassDicomVersTableBean;
    }

    public void removeSopClassDicom(PigSopClassDicomRowBean sopClassDicomRowBean) throws IncoherenceException {
        PigSopClassDicom sopClassDicom = amministrazioneHelper
                .getPigSopClassDicomById(sopClassDicomRowBean.getIdSopClassDicom());
        if (!sopClassDicom.getPigSopClassDicomVers().isEmpty()) {
            throw new IncoherenceException("Rimozione Sop Class non riuscita: classe utilizzata da versatori");
        }
        amministrazioneHelper.removeEntity(sopClassDicom, true);
    }

    public PigSopClassDicomTableBean getPigSopClassDispTableBean(BigDecimal idVers) {
        PigSopClassDicomTableBean sopClassDispTable = new PigSopClassDicomTableBean();
        List<PigSopClassDicomVers> list = amministrazioneHelper.getPigSopClassDicomVersList(null, idVers);
        List<Long> idList = new ArrayList<>();
        for (PigSopClassDicomVers sopClassDicomVers : list) {
            idList.add(sopClassDicomVers.getPigSopClassDicom().getIdSopClassDicom());
        }
        List<PigSopClassDicom> sopClassList = amministrazioneHelper.getPigSopClassDicomList(null, null);
        List<PigSopClassDicom> sopClassDispList = new ArrayList<>();
        for (PigSopClassDicom sopClassRow : sopClassList) {
            if (!idList.contains(sopClassRow.getIdSopClassDicom())) {
                sopClassDispList.add(sopClassRow);
            }
        }
        try {
            sopClassDispTable = (PigSopClassDicomTableBean) Transform.entities2TableBean(sopClassDispList);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return sopClassDispTable;
    }

    public PigSopClassDicomTableBean getPigSopClassVersTableBean(BigDecimal idVers) {
        PigSopClassDicomTableBean sopClassVersTable = new PigSopClassDicomTableBean();
        List<PigSopClassDicomVers> list = amministrazioneHelper.getPigSopClassDicomVersList(null, idVers);
        List<Long> idList = new ArrayList<>();
        for (PigSopClassDicomVers sopClassDicomVers : list) {
            idList.add(sopClassDicomVers.getPigSopClassDicom().getIdSopClassDicom());
        }
        List<PigSopClassDicom> sopClassList = amministrazioneHelper.getPigSopClassDicomList(null, null);
        List<PigSopClassDicom> sopClassVersList = new ArrayList<>();
        for (PigSopClassDicom sopClassRow : sopClassList) {
            if (idList.contains(sopClassRow.getIdSopClassDicom())) {
                sopClassVersList.add(sopClassRow);
            }
        }
        try {
            sopClassVersTable = (PigSopClassDicomTableBean) Transform.entities2TableBean(sopClassVersList);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return sopClassVersTable;
    }

    protected void removeSopClassDicomVers(BigDecimal idSopClassDispRow, BigDecimal idVers) {
        PigSopClassDicom pigSopClass = amministrazioneHelper.getPigSopClassDicomById(idSopClassDispRow);
        PigVers vers = amministrazioneHelper.getPigVersById(idVers);
        PigSopClassDicomVers sopClassVersDicom = amministrazioneHelper.getPigSopClassDicomVers(idSopClassDispRow,
                idVers);
        // se c'è l'associazione la rimuovo
        if (sopClassVersDicom != null) {
            sopClassVersDicom.setPigVer(vers);
            sopClassVersDicom.setPigSopClassDicom(pigSopClass);
            amministrazioneHelper.removeEntity(sopClassVersDicom, true);
        }
    }

    protected void addSopClassDicomVers(BigDecimal idSopClassToVersRow, BigDecimal idVers) {
        PigSopClassDicom pigSopClass = amministrazioneHelper.getPigSopClassDicomById(idSopClassToVersRow);
        PigVers vers = amministrazioneHelper.getPigVersById(idVers);
        PigSopClassDicomVers sopClassVersDicom = amministrazioneHelper.getPigSopClassDicomVers(idSopClassToVersRow,
                idVers);
        // se non c'è l'associazione la aggiungo
        if (sopClassVersDicom == null) {
            sopClassVersDicom = new PigSopClassDicomVers();
            sopClassVersDicom.setPigVer(vers);
            sopClassVersDicom.setPigSopClassDicom(pigSopClass);
            amministrazioneHelper.insertEntity(sopClassVersDicom, true);
        }
    }

    public void removeAndAddSopClassDicomVers(LogParam param, PigSopClassDicomTableBean sopClassDispTable,
            PigSopClassDicomTableBean sopClassToVersTable, BigDecimal idVers) {
        for (PigSopClassDicomRowBean sopClassDispRow : sopClassDispTable) {
            removeSopClassDicomVers(sopClassDispRow.getIdSopClassDicom(), idVers);
        }
        for (PigSopClassDicomRowBean sopClassToVersRow : sopClassToVersTable) {
            addSopClassDicomVers(sopClassToVersRow.getIdSopClassDicom(), idVers);
        }
        sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_VERSATORE, idVers, param.getNomePagina());
    }

    /*
     * XSD DATI SPEC
     */
    public PigXsdDatiSpecTableBean getPigXsdDatiSpecTableBean(BigDecimal idTipoObject, BigDecimal idTipoFileObject) {
        PigXsdDatiSpecTableBean xsdDatiSpecTableBean = new PigXsdDatiSpecTableBean();
        try {
            List<PigXsdDatiSpec> list = amministrazioneHelper.getPigXsdDatiSpecList(idTipoObject, idTipoFileObject);
            if (list != null && !list.isEmpty()) {
                xsdDatiSpecTableBean = (PigXsdDatiSpecTableBean) Transform.entities2TableBean(list);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return xsdDatiSpecTableBean;
    }

    public PigXsdDatiSpecRowBean getPigXsdDatiSpecRowBean(BigDecimal idXsdDatiSpec) {
        PigXsdDatiSpecRowBean xsdDatiSpecRowBean = new PigXsdDatiSpecRowBean();
        PigXsdDatiSpec xsdDatiSpec = amministrazioneHelper.getPigXsdDatiSpecById(idXsdDatiSpec);
        try {
            xsdDatiSpecRowBean = (PigXsdDatiSpecRowBean) Transform.entity2RowBean(xsdDatiSpec);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return xsdDatiSpecRowBean;
    }

    public PigXsdDatiSpecRowBean getFullPigXsdDatiSpecRowBean(BigDecimal idXsdDatiSpec) {
        PigXsdDatiSpecRowBean xsdDatiSpecRowBean = new PigXsdDatiSpecRowBean();
        PigXsdDatiSpec xsdDatiSpec = amministrazioneHelper.getFullPigXsdDatiSpecById(idXsdDatiSpec);
        try {
            xsdDatiSpecRowBean = (PigXsdDatiSpecRowBean) Transform.entity2RowBean(xsdDatiSpec);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return xsdDatiSpecRowBean;
    }

    public List<String[]> parseStringaXsd(String stringaFile) throws IncoherenceException {
        List<String[]> attributes = new ArrayList<>();
        ByteArrayInputStream bais = null;
        if (!stringaFile.isEmpty()) {
            bais = new ByteArrayInputStream(stringaFile.getBytes());
        }
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            // XXE: This is the PRIMARY defense. If DTDs (doctypes) are disallowed,
            // almost all XML entity attacks are prevented
            final String FEATURE = "http://apache.org/xml/features/disallow-doctype-decl";
            dbf.setFeature(FEATURE, true);
            dbf.setFeature("http://xml.org/sax/features/external-general-entities", false);

            dbf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            // ... and these as well, per Timothy Morgan's 2014 paper:
            // "XML Schema, DTD, and Entity Attacks" (see reference below)
            dbf.setXIncludeAware(false);
            dbf.setExpandEntityReferences(false);
            // As stated in the documentation, "Feature for Secure Processing (FSP)" is the central mechanism that will
            // help you safeguard XML processing. It instructs XML processors, such as parsers, validators,
            // and transformers, to try and process XML securely, and the FSP can be used as an alternative to
            // dbf.setExpandEntityReferences(false); to allow some safe level of Entity Expansion
            // Exists from JDK6.
            dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            // ... and, per Timothy Morgan:
            // "If for some reason support for inline DOCTYPEs are a requirement, then
            // ensure the entity settings are disabled (as shown above) and beware that SSRF
            // attacks
            // (http://cwe.mitre.org/data/definitions/918.html) and denial
            // of service attacks (such as billion laughs or decompression bombs via "jar:")
            // are a risk."
            dbf.setNamespaceAware(true);
            DocumentBuilder db;
            db = dbf.newDocumentBuilder();

            // compilazione schema
            // 1. Lookup a factory for the W3C XML Schema language
            // anche in questo caso l'eccezione non deve mai verificarsi, a meno di non aver caricato
            // nel database un xsd danneggiato...
            // 2. Compile the schema.
            Document doc;

            doc = db.parse(bais);
            NodeList nl = doc.getElementsByTagNameNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "element");

            for (int it = 0; it < nl.getLength(); it++) {
                Node n = nl.item(it);
                NamedNodeMap map = n.getAttributes();
                Node parent = n.getParentNode();
                if (!(map.getNamedItem("name").getNodeValue().equals("VersioneDatiSpecifici"))
                        && !(map.getNamedItem("name").getNodeValue().equals("DatiSpecifici"))
                        && !(map.getNamedItem("name").getNodeValue().equals("DatiSpecificiMigrazione"))) {
                    // ANDAVA IN NULL POINTER EXCEPTION !!
                    if ((parent.getParentNode().getParentNode() != null)
                            && parent.getParentNode().getParentNode().getNodeName().contains("xs:schema")) {
                        String[] attr = new String[2];
                        attr[0] = map.getNamedItem("name").getNodeValue();
                        if (map.getNamedItem("type") != null) {
                            attr[1] = map.getNamedItem("type").getNodeValue();
                        }
                        attributes.add(attr);
                    }
                }
            }
        } catch (SAXException e) {
            throw new IncoherenceException(
                    "Operazione non effettuata: file non ben formato " + e.getLocalizedMessage());
        } catch (IOException e) {
            throw new IncoherenceException("Errore IO - Operazione non effettuata: " + e.toString());
        } catch (ParserConfigurationException e) {
            throw new IncoherenceException("Errore ParserConfiguration - Operazione non effettuata: " + e.toString());
        }
        return attributes;
    }

    public void saveXsdDatiSpec(LogParam param, PigXsdDatiSpecRowBean xsdDatiSpecRowBean) throws IncoherenceException {
        BigDecimal idLastXsd = null;
        PigXsdDatiSpec lastXsd = new PigXsdDatiSpec();
        if (xsdDatiSpecRowBean.getIdTipoObject() != null) {
            lastXsd = amministrazioneHelper.getLastXsdDatiSpec(xsdDatiSpecRowBean.getIdTipoObject(), null);
        } else if (xsdDatiSpecRowBean.getIdTipoFileObject() != null) {
            lastXsd = amministrazioneHelper.getLastXsdDatiSpec(null, xsdDatiSpecRowBean.getIdTipoFileObject());
        }
        if (lastXsd != null && lastXsd.getIdXsdSpec() != 0) {
            if (lastXsd.getDtVersioneXsd().equals(xsdDatiSpecRowBean.getDtVersioneXsd())) {
                throw new IncoherenceException(
                        "Xsd gi� inseriti precedentemente in questa data. Impossibile completare l'operazione.");
            }
            idLastXsd = new BigDecimal(lastXsd.getIdXsdSpec());
        }
        BigDecimal idXsdDatiSpec = insertXsdDatiSpec(xsdDatiSpecRowBean);
        xsdDatiSpecRowBean.setIdXsdSpec(idXsdDatiSpec);
        try {
            saveXsdAttribDatiSpecList(xsdDatiSpecRowBean, idLastXsd);
        } catch (Exception ex) {
            throw new IncoherenceException(ex.getMessage());
        }
        amministrazioneHelper.getEntityManager().flush();
        BigDecimal idTipoObject = null;
        if (xsdDatiSpecRowBean.getIdTipoObject() == null) {
            PigXsdDatiSpec xsd = amministrazioneHelper.findById(PigXsdDatiSpec.class, idXsdDatiSpec);
            PigTipoObject tipoObject = xsd.getPigTipoFileObject().getPigTipoObject();
            idTipoObject = new BigDecimal(tipoObject.getIdTipoObject());
        } else {
            idTipoObject = xsdDatiSpecRowBean.getIdTipoObject();
        }
        sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_TIPO_OGGETTO_VERSABILE, idTipoObject,
                param.getNomePagina());
    }

    private BigDecimal insertXsdDatiSpec(PigXsdDatiSpecRowBean xsdDatiSpecRowBean) {
        PigXsdDatiSpec xsdDatiSpec = new PigXsdDatiSpec();
        // inizializzati vuoti
        PigTipoObject tipoObject = amministrazioneHelper.getPigTipoObjectById(xsdDatiSpecRowBean.getIdTipoObject());
        PigTipoFileObject tipoFileObject = amministrazioneHelper
                .getPigTipoFileObjectById(xsdDatiSpecRowBean.getIdTipoFileObject());
        PigVers vers = amministrazioneHelper.getPigVersById(xsdDatiSpecRowBean.getIdVers());
        try {
            xsdDatiSpec = (PigXsdDatiSpec) Transform.rowBean2Entity(xsdDatiSpecRowBean);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        xsdDatiSpec.setPigVer(vers);
        xsdDatiSpec.setPigTipoObject(tipoObject);
        xsdDatiSpec.setPigTipoFileObject(tipoFileObject);
        amministrazioneHelper.insertEntity(xsdDatiSpec, true);
        return new BigDecimal(xsdDatiSpec.getIdXsdSpec());
    }

    /*
     * CORRISPONDENZE IN SACER PER TIPO OBJ
     */
    public PigDichVersSacerTipoObjTableBean getPigDichVersSacerTipoObjTableBean(BigDecimal idTipoObject) {
        PigDichVersSacerTipoObjTableBean dichVersSacerTipoObjTableBean = new PigDichVersSacerTipoObjTableBean();
        List<Object[]> list = amministrazioneHelper.getPigDichVersSacerTipoObjList(idTipoObject);
        try {
            for (Object[] objects : list) {
                PigDichVersSacerTipoObjRowBean dichVersSacerTipoObjRowBean = (PigDichVersSacerTipoObjRowBean) Transform
                        .entity2RowBean(objects[0]);
                dichVersSacerTipoObjRowBean.setString("dl_composito_organiz", (String) objects[1]);
                dichVersSacerTipoObjTableBean.add(dichVersSacerTipoObjRowBean);
            }
        } catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException | InstantiationException
                | NoSuchMethodException | InvocationTargetException e) {
            log.error(e.getMessage());
        }
        return dichVersSacerTipoObjTableBean;
    }

    public PigDichVersSacerTipoObjRowBean getPigDichVersSacerTipoObjFromIdTipoObj(BigDecimal idTipoObj) {
        PigDichVersSacerTipoObjRowBean dichVersRowBean = new PigDichVersSacerTipoObjRowBean();
        if (idTipoObj != null) {
            PigDichVersSacerTipoObj dichVers = amministrazioneHelper.getPigDichVersSacerTipoObj(idTipoObj, null);
            try {
                dichVersRowBean = (PigDichVersSacerTipoObjRowBean) Transform.entity2RowBean(dichVers);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
        return dichVersRowBean;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void deletePigDichVersSacerTipoObj(LogParam param, BigDecimal idDichVersSacerTipoObj) throws ParerUserError {
        // Verifico se la corrispondenza è eliminabile
        PigVChkDelDichverssacerobj check = amministrazioneHelper.findViewById(PigVChkDelDichverssacerobj.class,
                idDichVersSacerTipoObj);
        if (check.getFlDelDchVersSacerTiobjOk().equals("0")) {
            throw new ParerUserError(
                    "La corrispondenza a Sacer non è eliminabile perché esistono oggetti con stato IN_ATTESA_FILE o IN_ATTESA_SCHED che potrebbero contenere unità documentarie da versare in strutture giustificate dalla corrispondenza");
        }
        PigDichVersSacerTipoObj dichVersSacerTipoObj = amministrazioneHelper.findById(PigDichVersSacerTipoObj.class,
                idDichVersSacerTipoObj);
        BigDecimal idTipoOggetto = new BigDecimal(dichVersSacerTipoObj.getPigTipoObject().getIdTipoObject());
        amministrazioneHelper.removeEntity(dichVersSacerTipoObj, true);
        sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_TIPO_OGGETTO_VERSABILE, idTipoOggetto,
                param.getNomePagina());
    }

    /*
     * VERSATORI PER CUI SI GENERANO OGGETTI
     */
    public PigVersTipoObjectDaTrasfTableBean getPigVersTipoObjectDaTrasfTableBean(BigDecimal idTipoObject) {
        PigVersTipoObjectDaTrasfTableBean versTipoObjectDaTrasfTableBean = new PigVersTipoObjectDaTrasfTableBean();
        List<PigVersTipoObjectDaTrasf> list = amministrazioneHelper.retrievePigVersTipoObjectDaTrasfList(idTipoObject);
        try {
            for (PigVersTipoObjectDaTrasf versTipoObjectDaTrasf : list) {
                PigVersTipoObjectDaTrasfRowBean versTipoObjectDaTrasfRowBean = new PigVersTipoObjectDaTrasfRowBean();
                versTipoObjectDaTrasfRowBean.setBigDecimal("id_vers_tipo_object_da_trasf",
                        new BigDecimal(versTipoObjectDaTrasf.getIdVersTipoObjectDaTrasf()));
                versTipoObjectDaTrasfRowBean.setString("versatore_trasf",
                        versTipoObjectDaTrasf.getPigTipoObjectDaTrasf().getPigVer().getPigAmbienteVer()
                                .getNmAmbienteVers() + " - "
                                + versTipoObjectDaTrasf.getPigTipoObjectDaTrasf().getPigVer().getNmVers());
                versTipoObjectDaTrasfRowBean.setString("nm_tipo_object_da_trasf",
                        versTipoObjectDaTrasf.getPigTipoObjectDaTrasf().getNmTipoObject());
                versTipoObjectDaTrasfRowBean.setString("nm_vers_gen",
                        versTipoObjectDaTrasf.getPigVersGen().getPigAmbienteVer().getNmAmbienteVers() + " - "
                                + versTipoObjectDaTrasf.getPigVersGen().getNmVers());
                versTipoObjectDaTrasfRowBean.setBigDecimal("id_vers_gen",
                        new BigDecimal(versTipoObjectDaTrasf.getPigVersGen().getIdVers()));
                versTipoObjectDaTrasfRowBean.setString("nm_tipo_object_gen",
                        versTipoObjectDaTrasf.getPigTipoObjectGen().getNmTipoObject());
                versTipoObjectDaTrasfRowBean.setCdVersGen(versTipoObjectDaTrasf.getCdVersGen());
                versTipoObjectDaTrasfTableBean.add(versTipoObjectDaTrasfRowBean);
            }
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage());
        }
        return versTipoObjectDaTrasfTableBean;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void deletePigVersTipoObjectDaTrasf(LogParam param, BigDecimal idVersTipoObjectDaTrasf) {
        PigVersTipoObjectDaTrasf versTipoObjectDaTrasf = amministrazioneHelper.findById(PigVersTipoObjectDaTrasf.class,
                idVersTipoObjectDaTrasf);
        BigDecimal idTipoObject = new BigDecimal(versTipoObjectDaTrasf.getPigTipoObjectDaTrasf().getIdTipoObject());

        amministrazioneHelper.removeEntity(versTipoObjectDaTrasf, true);
        sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_TIPO_OGGETTO_VERSABILE, idTipoObject,
                param.getNomePagina());
    }

    /**
     * Inserimento versatore per cui si generano oggetti
     *
     * @param param
     *            parametri per logging
     * @param idTipoObjectDaTrasf
     *            id tipo oggetto da traferire
     * @param idVersGen
     *            id versione
     * @param idTipoObjectGen
     *            id tipo oggettto
     * @param cdVersGen
     *            codice versione
     *
     * @return pk
     *
     * @throws ParerUserError
     *             errore generico
     */
    @Interceptors({ TransactionInterceptor.class })
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public long saveVersatoreGenerazioneOggetti(LogParam param, BigDecimal idTipoObjectDaTrasf, BigDecimal idVersGen,
            BigDecimal idTipoObjectGen, String cdVersGen) throws ParerUserError {
        PigVersTipoObjectDaTrasf existingVersTipoObjectDaTrasf = amministrazioneHelper
                .getPigVersTipoObjectDaTrasf(idTipoObjectDaTrasf, cdVersGen, null);
        if (existingVersTipoObjectDaTrasf != null) {
            throw new ParerUserError(
                    "Attenzione: impossibile procedere al salvataggio in quanto il codice versatore generato non è univoco nell'ambito del tipo oggetto da trasformare");
        }
        try {
            PigVersTipoObjectDaTrasf versTipoObjectDaTrasf = new PigVersTipoObjectDaTrasf();
            versTipoObjectDaTrasf
                    .setPigTipoObjectDaTrasf(amministrazioneHelper.findById(PigTipoObject.class, idTipoObjectDaTrasf));
            versTipoObjectDaTrasf.setPigVersGen(amministrazioneHelper.findById(PigVers.class, idVersGen));
            versTipoObjectDaTrasf
                    .setPigTipoObjectGen(amministrazioneHelper.findById(PigTipoObject.class, idTipoObjectGen));
            versTipoObjectDaTrasf.setCdVersGen(cdVersGen);
            amministrazioneHelper.insertEntity(versTipoObjectDaTrasf, true);
            sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                    param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_TIPO_OGGETTO_VERSABILE, idTipoObjectDaTrasf,
                    param.getNomePagina());
            return versTipoObjectDaTrasf.getIdVersTipoObjectDaTrasf();
        } catch (Exception ex) {
            log.error("Errore inatteso al salvataggio del versatore per cui si generano oggetti"
                    + ExceptionUtils.getRootCauseMessage(ex), ex);
            throw new ParerUserError("Errore inatteso al salvataggio del versatore per cui si generano oggetti");
        }
    }

    /**
     * Modifica versatore per cui si generano oggetti
     *
     * @param param
     *            parametri per il loggin
     * @param idVersTipoObjectDaTrasf
     *            id versamento tipo oggetto da trasferire
     * @param idTipoObjectGen
     *            id tipo oggetto generato
     * @param cdVersGen
     *            versione generata
     * @param idTipoObjectDaTrasf
     *            id tipo oggetto da trasferire
     *
     * @throws ParerUserError
     *             errore generico
     */
    @Interceptors({ TransactionInterceptor.class })
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void saveVersatoreGenerazioneOggetti(LogParam param, BigDecimal idVersTipoObjectDaTrasf,
            BigDecimal idTipoObjectGen, String cdVersGen, BigDecimal idTipoObjectDaTrasf) throws ParerUserError {
        PigVersTipoObjectDaTrasf versTipoObjectDaTrasf = amministrazioneHelper.findById(PigVersTipoObjectDaTrasf.class,
                idVersTipoObjectDaTrasf);
        String cdVersGenDB = versTipoObjectDaTrasf.getCdVersGen();
        if (!cdVersGenDB.equals(cdVersGen)) {
            PigVersTipoObjectDaTrasf existingVersTipoObjectDaTrasf = amministrazioneHelper
                    .getPigVersTipoObjectDaTrasf(idTipoObjectDaTrasf, cdVersGen, null);
            if (existingVersTipoObjectDaTrasf != null) {
                throw new ParerUserError(
                        "Attenzione: impossibile procedere al salvataggio in quanto il codice versatore generato non è univoco nell'ambito del tipo oggetto da trasformare");
            }
        }
        versTipoObjectDaTrasf.setPigTipoObjectGen(amministrazioneHelper.findById(PigTipoObject.class, idTipoObjectGen));
        versTipoObjectDaTrasf.setCdVersGen(cdVersGen);
        amministrazioneHelper.getEntityManager().flush();
        sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_TIPO_OGGETTO_VERSABILE, idTipoObjectDaTrasf,
                param.getNomePagina());
    }

    public PigVersTipoObjectDaTrasfRowBean getPigVersTipoObjectDaTrasfRowBean(BigDecimal idTipoObjectDaTrasf,
            String cdVersGen, BigDecimal idVersGen) throws ParerUserError {
        PigVersTipoObjectDaTrasf versTipoObjectDaTrasf = amministrazioneHelper
                .getPigVersTipoObjectDaTrasf(idTipoObjectDaTrasf, cdVersGen, idVersGen);
        PigVersTipoObjectDaTrasfRowBean rowBean = null;
        try {
            if (versTipoObjectDaTrasf != null) {
                rowBean = (PigVersTipoObjectDaTrasfRowBean) Transform.entity2RowBean(versTipoObjectDaTrasf);
            }
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                | IllegalArgumentException | InvocationTargetException ex) {
            log.error("Errore durante il recupero del versatore a cui appartiene l'oggetto versato "
                    + ExceptionUtils.getRootCauseMessage(ex), ex);
            throw new ParerUserError("Errore durante il recupero del versatore a cui appartiene l'oggetto versato");
        }
        return rowBean;
    }

    public PigVersTipoObjectDaTrasfRowBean getPigVersTipoObjectDaTrasfRowBean(BigDecimal idVersTipoObjectDaTrasf) {
        PigVersTipoObjectDaTrasfRowBean versTipoObjectDaTrasfRowBean = new PigVersTipoObjectDaTrasfRowBean();
        PigVersTipoObjectDaTrasf versTipoObjectDaTrasf = amministrazioneHelper.findById(PigVersTipoObjectDaTrasf.class,
                HibernateUtils.longFrom(idVersTipoObjectDaTrasf));
        versTipoObjectDaTrasfRowBean.setBigDecimal("id_vers_tipo_object_da_trasf",
                new BigDecimal(versTipoObjectDaTrasf.getIdVersTipoObjectDaTrasf()));
        versTipoObjectDaTrasfRowBean.setString("versatore_trasf",
                versTipoObjectDaTrasf.getPigTipoObjectDaTrasf().getPigVer().getPigAmbienteVer().getNmAmbienteVers()
                        + " - " + versTipoObjectDaTrasf.getPigTipoObjectDaTrasf().getPigVer().getNmVers());
        versTipoObjectDaTrasfRowBean.setBigDecimal("id_tipo_object_da_trasf",
                new BigDecimal(versTipoObjectDaTrasf.getPigTipoObjectDaTrasf().getIdTipoObject()));
        versTipoObjectDaTrasfRowBean.setString("nm_tipo_object_da_trasf",
                versTipoObjectDaTrasf.getPigTipoObjectDaTrasf().getNmTipoObject());
        versTipoObjectDaTrasfRowBean.setString("cd_trasf",
                versTipoObjectDaTrasf.getPigTipoObjectDaTrasf().getXfoTrasf().getCdTrasf());
        versTipoObjectDaTrasfRowBean.setBigDecimal("id_vers_gen",
                new BigDecimal(versTipoObjectDaTrasf.getPigVersGen().getIdVers()));
        versTipoObjectDaTrasfRowBean.setBigDecimal("id_tipo_object_gen",
                new BigDecimal(versTipoObjectDaTrasf.getPigTipoObjectGen().getIdTipoObject()));
        versTipoObjectDaTrasfRowBean.setCdVersGen(versTipoObjectDaTrasf.getCdVersGen());
        return versTipoObjectDaTrasfRowBean;
    }

    /*
     * SET PARAMETRI VERSATORE PER CUI SI GENERANO OGGETTI
     */
    public PigVValoreSetParamTrasfTableBean getPigVValoreSetParamTrasfTableBean(BigDecimal idVersTipoObjectDaTrasf) {
        PigVValoreSetParamTrasfTableBean valoreSetParamTrasfTableBean = new PigVValoreSetParamTrasfTableBean();
        List<PigVValoreSetParamTrasf> list = amministrazioneHelper
                .getPigVValoreSetParamTrasfList(idVersTipoObjectDaTrasf);
        try {
            if (!list.isEmpty()) {
                valoreSetParamTrasfTableBean = (PigVValoreSetParamTrasfTableBean) Transform.entities2TableBean(list);
            }
        } catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException | InstantiationException
                | NoSuchMethodException | InvocationTargetException e) {
            log.error(e.getMessage());
        }
        return valoreSetParamTrasfTableBean;
    }

    public PigVValoreSetParamTrasfTableBean getPigVValoreSetParamTrasfTableBeanByIdSetParamTrasf(
            BigDecimal idSetParamTrasf) {
        PigVValoreSetParamTrasfTableBean valoreSetParamTrasfTableBean = new PigVValoreSetParamTrasfTableBean();
        List<PigVValoreSetParamTrasf> list = amministrazioneHelper
                .getPigVValoreSetParamTrasfListByIdSetParamTrasf(idSetParamTrasf);
        try {
            if (!list.isEmpty()) {
                valoreSetParamTrasfTableBean = (PigVValoreSetParamTrasfTableBean) Transform.entities2TableBean(list);
            }
        } catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException | InstantiationException
                | NoSuchMethodException | InvocationTargetException e) {
            log.error(e.getMessage());
        }
        return valoreSetParamTrasfTableBean;
    }

    public PigStoricoVersAmbienteTableBean getPigStoricoVersAmbienteTableBean(BigDecimal idVers) {
        PigStoricoVersAmbienteTableBean storicoVersAmbienteTableBean = new PigStoricoVersAmbienteTableBean();
        List<PigStoricoVersAmbiente> list = amministrazioneHelper.getPigStoricoVersAmbienteList(idVers);
        try {
            for (PigStoricoVersAmbiente storico : list) {
                PigStoricoVersAmbienteRowBean storicoVersAmbienteRowBean = (PigStoricoVersAmbienteRowBean) Transform
                        .entity2RowBean(storico);
                storicoVersAmbienteRowBean.setString("nm_ambiente_vers",
                        storico.getPigAmbienteVer().getNmAmbienteVers());
                storicoVersAmbienteRowBean.setString("nm_ente_conserv", amministrazioneHelper
                        .findById(SIOrgEnteSiam.class, storico.getPigAmbienteVer().getIdEnteConserv()).getNmEnteSiam());
                storicoVersAmbienteRowBean.setString("nm_ente_gestore", amministrazioneHelper
                        .findById(SIOrgEnteSiam.class, storico.getPigAmbienteVer().getIdEnteGestore()).getNmEnteSiam());
                storicoVersAmbienteTableBean.add(storicoVersAmbienteRowBean);

            }
        } catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException | InstantiationException
                | NoSuchMethodException | InvocationTargetException e) {
            log.error(e.getMessage());
        }
        return storicoVersAmbienteTableBean;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void deletePigValoreSetParamTrasf(LogParam param, BigDecimal idValoreSetParamTrasf) {
        PigValoreSetParamTrasf valoreSetParamTrasf = amministrazioneHelper.findById(PigValoreSetParamTrasf.class,
                idValoreSetParamTrasf);
        BigDecimal idTipoObject = new BigDecimal(
                valoreSetParamTrasf.getPigVersTipoObjectDaTrasf().getPigTipoObjectDaTrasf().getIdTipoObject());
        amministrazioneHelper.removeEntity(valoreSetParamTrasf, true);
        sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_TIPO_OGGETTO_VERSABILE, idTipoObject,
                param.getNomePagina());
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void deletePigValoreParamTrasf(LogParam param, BigDecimal idValoreParamTrasf) {
        PigValoreParamTrasf valoreParamTrasf = amministrazioneHelper.findById(PigValoreParamTrasf.class,
                idValoreParamTrasf);
        PigValoreSetParamTrasf valoreSetParamTrasf = valoreParamTrasf.getPigValoreSetParamTrasf();
        BigDecimal idTipoObject = new BigDecimal(
                valoreSetParamTrasf.getPigVersTipoObjectDaTrasf().getPigTipoObjectDaTrasf().getIdTipoObject());
        amministrazioneHelper.removeEntity(valoreParamTrasf, true);
        // Se non ho più figli, elimino anche il padre
        if (valoreSetParamTrasf.getPigValoreParamTrasfs().isEmpty()) {
            amministrazioneHelper.removeEntity(valoreSetParamTrasf, true);
        }
        sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_TIPO_OGGETTO_VERSABILE, idTipoObject,
                param.getNomePagina());
    }

    public XfoSetParamTrasfRowBean getXfoSetParamTrasfRowBean(BigDecimal idSetParamTrasf) {
        XfoSetParamTrasfRowBean setParamTrasfRowBean = new XfoSetParamTrasfRowBean();
        XfoSetParamTrasf setParamTrasf = amministrazioneHelper.findById(XfoSetParamTrasf.class, idSetParamTrasf);
        if (setParamTrasf != null) {
            try {
                setParamTrasfRowBean = (XfoSetParamTrasfRowBean) Transform.entity2RowBean(setParamTrasf);
            } catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException | InstantiationException
                    | NoSuchMethodException | InvocationTargetException e) {
                log.error(e.getMessage());
            }
        }
        return setParamTrasfRowBean;
    }

    public PigVValoreParamTrasfTableBean getPigVValoreParamTrasfTableBean(BigDecimal idSetParamTrasf,
            BigDecimal idVersTipoObjectDaTrasf) {
        PigVValoreParamTrasfTableBean valoreParamTrasfViewBean = new PigVValoreParamTrasfTableBean();
        List<PigVValoreParamTrasf> list = amministrazioneHelper.getPigVValoreParamTrasfList(idSetParamTrasf,
                idVersTipoObjectDaTrasf);
        try {
            for (PigVValoreParamTrasf valore : list) {
                PigVValoreParamTrasfRowBean valoreParamTrasfRowBean = (PigVValoreParamTrasfRowBean) Transform
                        .entity2RowBean(valore);
                // -- MAC#17975
                String dsValoreParam = valoreParamTrasfRowBean.getDsValoreParam();
                if (dsValoreParam == null) {
                    dsValoreParam = "";
                }
                // -- fine MAC
                String dsValoreParamDefault = (amministrazioneHelper.findById(XfoParamTrasf.class,
                        valoreParamTrasfRowBean.getIdParamTrasf())).getDsValoreParam();
                dsValoreParamDefault = dsValoreParamDefault != null ? dsValoreParamDefault : "";

                // MAC#19309 - Compariva erroneamente il pulsante del reset a valore di default quando il default era
                // nullo
                if (!dsValoreParamDefault.equals(dsValoreParam)) {
                    valoreParamTrasfRowBean.setString("is_not_default", "1");
                    valoreParamTrasfRowBean.setString("eliminaValoreParametroVersatore", "Reset valore parametro");
                } else {
                    valoreParamTrasfRowBean.setString("is_not_default", "0");
                    valoreParamTrasfRowBean.setString("eliminaValoreParametroVersatore", null);
                }

                valoreParamTrasfViewBean.add(valoreParamTrasfRowBean);
            }
        } catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException | InstantiationException
                | NoSuchMethodException | InvocationTargetException e) {
            log.error(e.getMessage());
        }
        return valoreParamTrasfViewBean;
    }

    public PigVValParamTrasfDefSpecTableBean getPigVValParamTrasfDefSpecTableBean(BigDecimal idSetParamTrasf,
            BigDecimal idVersTipoObjectDaTrasf) {
        PigVValParamTrasfDefSpecTableBean valoreParamTrasfViewBean = new PigVValParamTrasfDefSpecTableBean();
        List<PigVValParamTrasfDefSpec> list = amministrazioneHelper.getPigVValParamTrasfDefSpecList(idSetParamTrasf,
                idVersTipoObjectDaTrasf);
        try {
            for (PigVValParamTrasfDefSpec valore : list) {
                PigVValParamTrasfDefSpecRowBean valoreParamTrasfRowBean = (PigVValParamTrasfDefSpecRowBean) Transform
                        .entity2RowBean(valore);
                // -- MAC#17975
                String valParam = valoreParamTrasfRowBean.getValParam();
                if (valParam == null) {
                    valoreParamTrasfRowBean.setValParam("nullo");
                }
                // -- fine MAC

                if (!valore.getTiValParam().equals("DEFAULT")) {
                    valoreParamTrasfRowBean.setString("is_not_default", "1");
                    valoreParamTrasfRowBean.setString("eliminaValoreParametroVersatore", "Reset valore parametro");
                } else {
                    valoreParamTrasfRowBean.setString("is_not_default", "0");
                    valoreParamTrasfRowBean.setString("eliminaValoreParametroVersatore", null);
                }

                valoreParamTrasfViewBean.add(valoreParamTrasfRowBean);
            }
        } catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException | InstantiationException
                | NoSuchMethodException | InvocationTargetException e) {
            log.error(e.getMessage());
        }
        return valoreParamTrasfViewBean;
    }

    public PigVValParamTrasfDefSpecRowBean getPigVValParamTrasfDefSpecRowBean(BigDecimal idSetParamTrasf,
            BigDecimal idVersTipoObjectDaTrasf, String nmParamTrasf) {
        PigVValParamTrasfDefSpecRowBean valoreParamTrasfRowBean = null;
        try {
            PigVValParamTrasfDefSpec pigVValParamTrasfDefSpec = amministrazioneHelper
                    .getPigVValParamTrasfDefSpecByName(idSetParamTrasf, idVersTipoObjectDaTrasf, nmParamTrasf);
            if (pigVValParamTrasfDefSpec != null) {
                valoreParamTrasfRowBean = (PigVValParamTrasfDefSpecRowBean) Transform
                        .entity2RowBean(pigVValParamTrasfDefSpec);
            }

        } catch (Exception ex) {
            LoggerFactory.getLogger(AmministrazioneEjb.class.getName()).error("Eccezione", ex);
        }

        return valoreParamTrasfRowBean;
    }

    @Interceptors({ TransactionInterceptor.class })
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void saveListOfValoreParametroVersatore(LogParam logParam, List<Map<String, String>> parameters,
            BigDecimal idSetParamTrasf, BigDecimal idVersTipoObjectDaTrasf) {
        AmministrazioneEjb me = context.getBusinessObject(AmministrazioneEjb.class);

        // cerco di capire se esiste già un set per i valori modificati.
        PigValoreSetParamTrasf pigValoreSetParamTrasf = amministrazioneHelper.getPigValoreSetParamTrasf(idSetParamTrasf,
                idVersTipoObjectDaTrasf);
        PigVersTipoObjectDaTrasf versTipoObjectDaTrasf = amministrazioneHelper.findById(PigVersTipoObjectDaTrasf.class,
                idVersTipoObjectDaTrasf);
        BigDecimal idTipoObject = new BigDecimal(versTipoObjectDaTrasf.getPigTipoObjectDaTrasf().getIdTipoObject());

        if (pigValoreSetParamTrasf == null) {
            pigValoreSetParamTrasf = new PigValoreSetParamTrasf();
            pigValoreSetParamTrasf.setPigVersTipoObjectDaTrasf(versTipoObjectDaTrasf);
            pigValoreSetParamTrasf
                    .setXfoSetParamTrasf(amministrazioneHelper.findById(XfoSetParamTrasf.class, idSetParamTrasf));
            amministrazioneHelper.insertEntity(pigValoreSetParamTrasf, true);
        }

        // poi li modifico davvero.
        for (Map<String, String> parameterMap : parameters) {
            PigVValParamTrasfDefSpecRowBean pigVValParamTrasfDefSpecRowBean = me.getPigVValParamTrasfDefSpecRowBean(
                    idSetParamTrasf, idVersTipoObjectDaTrasf, parameterMap.get("parametro"));

            BigDecimal idParamTrasf = pigVValParamTrasfDefSpecRowBean.getIdParamTrasf();
            BigDecimal idValoreParamTrasf = pigVValParamTrasfDefSpecRowBean.getIdValoreParamTrasf() != null
                    ? pigVValParamTrasfDefSpecRowBean.getIdValoreParamTrasf() : null;

            // Prima di tutto controllo di stare ad inserire un valore DIVERSO da quello di default
            XfoParamTrasf paramTrasf = amministrazioneHelper.findById(XfoParamTrasf.class, idParamTrasf);
            String dsValoreParamXfoParamTrasf = paramTrasf.getDsValoreParam() != null ? paramTrasf.getDsValoreParam()
                    : "";

            if (!org.apache.commons.lang3.StringUtils.equals(dsValoreParamXfoParamTrasf, parameterMap.get("valore"))) {
                // Se per il parametro è già definito un valore specifico, il sistema aggiorna il record...
                if (idValoreParamTrasf != null) {
                    PigValoreParamTrasf valoreParamTrasf = amministrazioneHelper.findById(PigValoreParamTrasf.class,
                            idValoreParamTrasf);
                    valoreParamTrasf.setDsValoreParam(parameterMap.get("valore"));
                } else {
                    // // ...altrimenti, inserisco un nuovo record in PIG_VALORE_PARAM_TRASF
                    PigValoreParamTrasf valoreParamTrasf = new PigValoreParamTrasf();
                    valoreParamTrasf.setDsValoreParam(parameterMap.get("valore"));
                    valoreParamTrasf.setPigValoreSetParamTrasf(pigValoreSetParamTrasf);
                    valoreParamTrasf.setXfoParamTrasf(paramTrasf);
                    amministrazioneHelper.insertEntity(valoreParamTrasf, true);
                }
            } else if (idValoreParamTrasf != null) {
                PigValoreParamTrasf valoreParamTrasf = amministrazioneHelper.findById(PigValoreParamTrasf.class,
                        idValoreParamTrasf);
                // resetta il parametro
                amministrazioneHelper.removeEntity(valoreParamTrasf, true);
                // Se non ho più figli, elimino anche il padre
                if (pigValoreSetParamTrasf.getPigValoreParamTrasfs().isEmpty()) {
                    amministrazioneHelper.removeEntity(pigValoreSetParamTrasf, true);
                }
            }
        }

        sacerLogEjb.log(logParam.getTransactionLogContext(), logParam.getNomeApplicazione(), logParam.getNomeUtente(),
                logParam.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_TIPO_OGGETTO_VERSABILE, idTipoObject,
                logParam.getNomePagina());
    }

    /**
     * Modifica valore parametro versatore
     *
     * @param param
     *            parametri per il logging
     * @param idVersTipoObjectDaTrasf
     *            id tipo oggetto versamento da trasferire
     * @param idSetParamTrasf
     *            id parametro (set)
     * @param idParamTrasf
     *            id parametro
     * @param idValoreSetParamTrasf
     *            id valore (set) parametro
     * @param idValoreParamTrasf
     *            id valore parametro
     * @param dsValoreParam
     *            descrizione valore
     *
     * @throws ParerUserError
     *             errore generico
     */
    @Interceptors({ TransactionInterceptor.class })
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void saveValoreParametroVersatore(LogParam param, BigDecimal idVersTipoObjectDaTrasf,
            BigDecimal idSetParamTrasf, BigDecimal idParamTrasf, BigDecimal idValoreSetParamTrasf,
            BigDecimal idValoreParamTrasf, String dsValoreParam) throws ParerUserError {

        // Prima di tutto controllo di stare ad inserire un valore DIVERSO da quello di default
        XfoParamTrasf paramTrasf = amministrazioneHelper.findById(XfoParamTrasf.class, idParamTrasf);
        String dsValoreParamXfoParamTrasf = paramTrasf.getDsValoreParam() != null ? paramTrasf.getDsValoreParam() : "";
        if (!StringUtils.equals(dsValoreParamXfoParamTrasf, dsValoreParam)) {

            try {
                PigVersTipoObjectDaTrasf versTipoObjectDaTrasf = amministrazioneHelper
                        .findById(PigVersTipoObjectDaTrasf.class, idVersTipoObjectDaTrasf);
                BigDecimal idTipoObject = new BigDecimal(
                        versTipoObjectDaTrasf.getPigTipoObjectDaTrasf().getIdTipoObject());

                // Se è il primo parametro del set corrente per il quale viene definito un VALORE SPECIFICO (e dunque è
                // diverso da quello di default)
                // allora inserisco il record in PIG_VALORE_SET_PARAM_TRASF
                PigValoreSetParamTrasf valoreSetParamTrasf = null;
                if (idValoreSetParamTrasf == null) {
                    valoreSetParamTrasf = new PigValoreSetParamTrasf();
                    valoreSetParamTrasf.setPigVersTipoObjectDaTrasf(versTipoObjectDaTrasf);
                    valoreSetParamTrasf.setXfoSetParamTrasf(
                            amministrazioneHelper.findById(XfoSetParamTrasf.class, idSetParamTrasf));
                    amministrazioneHelper.insertEntity(valoreSetParamTrasf, true);
                } else {
                    valoreSetParamTrasf = amministrazioneHelper.findById(PigValoreSetParamTrasf.class,
                            idValoreSetParamTrasf);
                }

                // Se per il parametro è già definito un valore specifico, il sistema aggiorna il record...
                if (idValoreParamTrasf != null) {
                    PigValoreParamTrasf valoreParamTrasf = amministrazioneHelper.findById(PigValoreParamTrasf.class,
                            idValoreParamTrasf);
                    valoreParamTrasf.setDsValoreParam(dsValoreParam);
                } else {
                    // // ...altrimenti, inserisco un nuovo record in PIG_VALORE_PARAM_TRASF
                    PigValoreParamTrasf valoreParamTrasf = new PigValoreParamTrasf();
                    valoreParamTrasf.setDsValoreParam(dsValoreParam);
                    valoreParamTrasf.setPigValoreSetParamTrasf(valoreSetParamTrasf);
                    valoreParamTrasf.setXfoParamTrasf(paramTrasf);
                    amministrazioneHelper.insertEntity(valoreParamTrasf, true);
                }
                sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                        param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_TIPO_OGGETTO_VERSABILE, idTipoObject,
                        param.getNomePagina());

            } catch (Exception ex) {
                log.error("Errore inatteso al salvataggio del valore parametro del versatore "
                        + ExceptionUtils.getRootCauseMessage(ex), ex);
                throw new ParerUserError("Errore inatteso al salvataggio del valore parametro del versatore");
            }
        } else {
            throw new ParerUserError(
                    "Attenzione: stai cercando di inserire il valore default, utilizza la funzione di Reset");
        }
    }

    public PigValoreSetParamTrasfRowBean getPigValoreSetParamTrasfRowBean(BigDecimal idSetParamTrasf,
            BigDecimal idVersTipoObjectDaTrasf) throws ParerUserError {
        PigValoreSetParamTrasf valoreSetParamTrasf = amministrazioneHelper.getPigValoreSetParamTrasf(idSetParamTrasf,
                idVersTipoObjectDaTrasf);
        PigValoreSetParamTrasfRowBean rowBean = null;
        try {
            if (valoreSetParamTrasf != null) {
                rowBean = (PigValoreSetParamTrasfRowBean) Transform.entity2RowBean(valoreSetParamTrasf);
            }
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                | IllegalArgumentException | InvocationTargetException ex) {
            log.error("Errore durante il recupero del valore set parametri " + ExceptionUtils.getRootCauseMessage(ex),
                    ex);
            throw new ParerUserError("Errore durante il recupero del valore set parametri");
        }
        return rowBean;
    }

    /*
     * ATTRIB DATI SPEC
     */
    private void saveXsdAttribDatiSpecList(PigXsdDatiSpecRowBean xsdDatiSpecRowBean, BigDecimal idLastXsd)
            throws IncoherenceException {
        String blob = xsdDatiSpecRowBean.getBlXsd();
        List<String[]> attributes;
        attributes = parseStringaXsd(blob);
        List<PigAttribDatiSpec> lastVersionAttr = null;
        if (idLastXsd != null) {
            lastVersionAttr = amministrazioneHelper.getPigAttribDatiSpecList(idLastXsd);
        }
        PigXsdDatiSpec xsdDatiSpec = amministrazioneHelper.getPigXsdDatiSpecById(xsdDatiSpecRowBean.getIdXsdSpec());
        List<String> controlList = new ArrayList<>();
        int nrOrd = 10;
        if (xsdDatiSpec != null) {
            for (String[] attr : attributes) {
                if (controlList.contains(attr[0])) {
                    throw new IncoherenceException("Xsd con attributi duplicati. Impossibile salvare");
                }
                PigAttribDatiSpec oldAttribDatiSpec = listContainsAttribute(lastVersionAttr, attr[0]);
                PigAttribDatiSpec newAttribDatiSpec = new PigAttribDatiSpec();
                if (oldAttribDatiSpec == null) {
                    newAttribDatiSpec.setFlFiltroDiario("1");
                    newAttribDatiSpec.setFlVersSacer("1");
                    newAttribDatiSpec.setNmAttribDatiSpec(attr[0]);
                    newAttribDatiSpec.setNmColDatiSpec(null);
                    if (attr[1] != null) {
                        String cd = attr[1];
                        if (attr[1].contains("xs:")) {
                            cd = attr[1].substring(attr[1].indexOf(':') + 1, attr[1].length());
                        }
                        newAttribDatiSpec.setCdDatatypeXsd(cd);
                    } else {
                        newAttribDatiSpec.setCdDatatypeXsd("string");
                    }
                    // sia per date che per dateTime
                    if (newAttribDatiSpec.getCdDatatypeXsd().contains("date")) {
                        newAttribDatiSpec.setTiDatatypeCol("DATA");
                    } else {
                        newAttribDatiSpec.setTiDatatypeCol("ALFANUMERICO");
                    }
                } else {
                    newAttribDatiSpec.setFlFiltroDiario(oldAttribDatiSpec.getFlFiltroDiario());
                    newAttribDatiSpec.setFlVersSacer(oldAttribDatiSpec.getFlVersSacer());
                    newAttribDatiSpec.setNmAttribDatiSpec(attr[0]);
                    newAttribDatiSpec.setNmColDatiSpec(oldAttribDatiSpec.getNmColDatiSpec());
                    newAttribDatiSpec.setCdDatatypeXsd(oldAttribDatiSpec.getCdDatatypeXsd());
                    newAttribDatiSpec.setTiDatatypeCol(oldAttribDatiSpec.getTiDatatypeCol());
                }
                newAttribDatiSpec.setNiOrd(new BigDecimal(nrOrd));
                newAttribDatiSpec.setPigXsdDatiSpec(xsdDatiSpec);
                amministrazioneHelper.insertEntity(newAttribDatiSpec, true);
                controlList.add(newAttribDatiSpec.getNmAttribDatiSpec());
                nrOrd += 10;
            }
        }
    }

    private PigAttribDatiSpec listContainsAttribute(List<PigAttribDatiSpec> lastVersionAttr, String nmAttr) {
        if (lastVersionAttr != null) {
            for (PigAttribDatiSpec attr : lastVersionAttr) {
                if (attr.getNmAttribDatiSpec().equals(nmAttr)) {
                    return attr;
                }
            }
        }
        return null;
    }

    public PigAttribDatiSpecTableBean getPigAttribDatiSpecTableBean(BigDecimal idXsdDatiSpec) {
        PigAttribDatiSpecTableBean pigAttribDatiSpecTableBean = new PigAttribDatiSpecTableBean();
        List<PigAttribDatiSpec> list = amministrazioneHelper.getPigAttribDatiSpecList(idXsdDatiSpec);
        try {
            if (!list.isEmpty()) {
                pigAttribDatiSpecTableBean = (PigAttribDatiSpecTableBean) Transform.entities2TableBean(list);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return pigAttribDatiSpecTableBean;
    }

    public PigXsdDatiSpecRowBean getLastXsdDatiSpec(BigDecimal idTipoObject, BigDecimal idTipoFileObject) {
        PigXsdDatiSpecRowBean lastXsdRowBean = new PigXsdDatiSpecRowBean();
        PigXsdDatiSpec lastXsd = amministrazioneHelper.getLastXsdDatiSpec(idTipoObject, idTipoFileObject);
        if (lastXsd != null) {
            try {
                lastXsdRowBean = (PigXsdDatiSpecRowBean) Transform.entity2RowBean(lastXsd);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
        return lastXsdRowBean;
    }

    public void updatePigXsdDatiSpec(LogParam param, PigXsdDatiSpecRowBean xsdDatiSpecRowBean)
            throws IncoherenceException, XPathExpressionException, TransformerException {
        BigDecimal idLastXsd = null;
        PigXsdDatiSpec secondLastXsd = new PigXsdDatiSpec();
        List<PigXsdDatiSpec> list = null;
        if (xsdDatiSpecRowBean.getIdTipoObject() != null) {
            list = amministrazioneHelper.getOrdPigXsdDatiSpecList(xsdDatiSpecRowBean.getIdTipoObject(), null);
        } else if (xsdDatiSpecRowBean.getIdTipoFileObject() != null) {
            list = amministrazioneHelper.getOrdPigXsdDatiSpecList(null, xsdDatiSpecRowBean.getIdTipoFileObject());
        }
        if (list.size() > 1) {
            secondLastXsd = list.get(1);
        }
        if (secondLastXsd != null && secondLastXsd.getIdXsdSpec() != 0) {
            if (secondLastXsd.getDtVersioneXsd().equals(xsdDatiSpecRowBean.getDtVersioneXsd())) {
                throw new IncoherenceException(
                        "Xsd già inseriti precedentemente in questa data. Impossibile completare l'operazione.");
            }
            idLastXsd = new BigDecimal(secondLastXsd.getIdXsdSpec());
        }
        PigXsdDatiSpec oldXsd = amministrazioneHelper.getPigXsdDatiSpecById(xsdDatiSpecRowBean.getIdXsdSpec());
        oldXsd.setBlXsd(xsdDatiSpecRowBean.getBlXsd());
        oldXsd.setDtVersioneXsd(xsdDatiSpecRowBean.getDtVersioneXsd());
        removeXsdAttribList(xsdDatiSpecRowBean.getIdXsdSpec());
        // questa funzione può
        saveXsdAttribDatiSpecList(xsdDatiSpecRowBean, idLastXsd);
        amministrazioneHelper.getEntityManager().flush();
        BigDecimal idTipoObject = xsdDatiSpecRowBean.getIdTipoObject();
        if (xsdDatiSpecRowBean.getIdTipoFileObject() != null) {
            idTipoObject = new BigDecimal(
                    amministrazioneHelper.getPigTipoFileObjectById(xsdDatiSpecRowBean.getIdTipoFileObject())
                            .getPigTipoObject().getIdTipoObject());
        }
        sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_TIPO_OGGETTO_VERSABILE, idTipoObject,
                param.getNomePagina());
    }

    private void removeXsdAttribList(BigDecimal idXsdSpec) {
        List<PigAttribDatiSpec> list = amministrazioneHelper.getPigAttribDatiSpecList(idXsdSpec);
        for (PigAttribDatiSpec row : list) {
            amministrazioneHelper.removeEntity(row, true);
        }
    }

    public void removeXsdDatiSpec(LogParam param, PigXsdDatiSpecRowBean xsdDatiSpecRowBean)
            throws IncoherenceException {
        if (xsdDatiSpecRowBean.getIdTipoObject() != null
                && !(amministrazioneHelper.getPigInfoDicomListByXsd(xsdDatiSpecRowBean.getIdXsdSpec())).isEmpty()) {
            throw new IncoherenceException("Xsd associato a versamenti, impossibile cancellare");
        } else if (xsdDatiSpecRowBean.getIdTipoFileObject() != null
                && !(amministrazioneHelper.getPigInfoDicomListByXsd(xsdDatiSpecRowBean.getIdXsdSpec())).isEmpty()) {
            throw new IncoherenceException("Xsd associato a versamenti, impossibile cancellare");
        }
        PigXsdDatiSpec xsdDatiSpec = amministrazioneHelper.getPigXsdDatiSpecById(xsdDatiSpecRowBean.getIdXsdSpec());
        amministrazioneHelper.removeEntity(xsdDatiSpec, true);
        BigDecimal idTipoObject = xsdDatiSpecRowBean.getIdTipoObject();
        if (idTipoObject == null) {
            PigTipoFileObject fileObj = amministrazioneHelper.findById(PigTipoFileObject.class,
                    xsdDatiSpecRowBean.getIdTipoFileObject());
            idTipoObject = new BigDecimal(fileObj.getPigTipoObject().getIdTipoObject());
        }
        sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_TIPO_OGGETTO_VERSABILE, idTipoObject,
                param.getNomePagina());
    }

    public void updateAttribName(LogParam param, PigXsdDatiSpecRowBean xsdDatiSpecRowBean, String nmAttr,
            PigAttribDatiSpecRowBean newAttribRowBean) {
        List<PigXsdDatiSpec> xsdTable = amministrazioneHelper
                .getPigXsdDatiSpecList(xsdDatiSpecRowBean.getIdTipoObject(), xsdDatiSpecRowBean.getIdTipoFileObject());
        for (PigXsdDatiSpec row : xsdTable) {
            BigDecimal idXsdRow = new BigDecimal(row.getIdXsdSpec());
            List<PigAttribDatiSpec> attribTable = amministrazioneHelper.getPigAttribDatiSpecList(idXsdRow);
            for (PigAttribDatiSpec attr : attribTable) {
                if (attr.getNmAttribDatiSpec().equals(nmAttr)) {
                    attr.setNmColDatiSpec(newAttribRowBean.getNmColDatiSpec());
                    attr.setCdDatatypeXsd(newAttribRowBean.getCdDatatypeXsd());
                    attr.setTiDatatypeCol(newAttribRowBean.getTiDatatypeCol());
                    attr.setFlFiltroDiario(newAttribRowBean.getFlFiltroDiario());
                    attr.setFlVersSacer(newAttribRowBean.getFlVersSacer());
                }
            }
        }
        amministrazioneHelper.getEntityManager().flush();
        BigDecimal idTipoObject = xsdDatiSpecRowBean.getIdTipoObject();
        if (idTipoObject == null) {
            PigTipoFileObject fileObj = amministrazioneHelper.findById(PigTipoFileObject.class,
                    xsdDatiSpecRowBean.getIdTipoFileObject());
            idTipoObject = new BigDecimal(fileObj.getPigTipoObject().getIdTipoObject());
        }
        sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_TIPO_OGGETTO_VERSABILE, idTipoObject,
                param.getNomePagina());
    }

    public PigVersRowBean versToRowBean(UUID uuid) {
        PigVers vers = versCache.getPigVers(uuid);
        PigVersRowBean versRowBean = null;
        try {
            versRowBean = (PigVersRowBean) Transform.entity2RowBean(vers);
        } catch (Exception a) {
            log.error(a.getMessage(), a);
        }
        return versRowBean;
    }

    /**
     * Metodo che esegue la chiamata di allineamento organizzazioni
     *
     * @param <T>
     *            oggetto generico da replicare
     * @param organizDaReplic
     *            array di record da replicare
     *
     * @throws IncoherenceException
     *             Eccezione con rollback in caso di entity diversa dalle suddette o di errore imprevisto da parte
     *             dell'allineamento
     */
    public <T extends Serializable> void replicateToIam(IamOrganizDaReplic... organizDaReplic)
            throws IncoherenceException {
        List<IamOrganizDaReplic> orgDaReplic = (organizDaReplic != null ? Arrays.asList(organizDaReplic)
                : new ArrayList<IamOrganizDaReplic>());
        try {
            allineamentoOrganizzazioniEjb.allineamentoOrganizzazioni(orgDaReplic);
        } catch (Exception ex) {
            log.error("Errore imprevisto del servizio di replica : " + ex.getMessage(), ex);
            jobLoggerEjb.writeAtomicLog(Constants.NomiJob.ALLINEAMENTO_ORGANIZZAZIONI, Constants.TipiRegLogJob.ERRORE,
                    "Errore imprevisto del servizio di replica");
            throw new IncoherenceException("Errore imprevisto del servizio di replica");
        }
    }

    public boolean isSessioniPresentiPerVersatore(BigDecimal idVers) {
        boolean result = false;
        PigVers vers = amministrazioneHelper.getPigVersById(idVers);
        result = (vers.getPigSessioneIngests() != null && !vers.getPigSessioneIngests().isEmpty())
                || (vers.getPigSessioneRecups() != null && !vers.getPigSessioneRecups().isEmpty());
        return result;
    }

    public PigStatoObjectTableBean getPigStatoObjectTableBean() {
        PigStatoObjectTableBean statoObjectTableBean = new PigStatoObjectTableBean();
        List<PigStatoObject> statoObjectList = amministrazioneHelper.getPigStatoObjectList();
        try {
            statoObjectTableBean = (PigStatoObjectTableBean) Transform.entities2TableBean(statoObjectList);
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
        return statoObjectTableBean;
    }

    public void updateStatoVersamentoOggetto(String tiStatoObject, String dsTiStatoObject) {
        amministrazioneHelper.updatePigStatoObject(tiStatoObject, dsTiStatoObject);
    }

    /**
     * Ritorna il tableBean dei versatori per cui generare oggetti, escludendo il versatore del parametro dato in input
     * (che è chi genera) e le sue associazioni
     *
     * @param idAmbienteVers
     *            id ambiente versamento
     * @param idVers
     *            id versamento
     *
     * @return il tablebean
     *
     * @throws ParerUserError
     *             errore generico
     */
    public PigVersTableBean getPigVersTrasfComboTableBean(BigDecimal idAmbienteVers, BigDecimal idVers)
            throws ParerUserError {
        List<PigVers> list = amministrazioneHelper.getPigVersTrasfCombo(idAmbienteVers, idVers);
        PigVersTableBean table = new PigVersTableBean();
        if (!list.isEmpty()) {
            try {
                table = (PigVersTableBean) Transform.entities2TableBean(list);
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                    | IllegalArgumentException | InvocationTargetException ex) {
                log.error("Errore durante il recupero dei versatori per cui generare oggetti "
                        + ExceptionUtils.getRootCauseMessage(ex), ex);
                throw new ParerUserError("Errore durante il recupero dei versatori per cui generare oggetti");
            }
        }
        return table;
    }

    /**
     * Ritorna il tableBean delle trasformazioni per generare oggetti
     *
     * @return il tablebean
     *
     * @throws ParerUserError
     *             errore generico
     */
    public XfoTrasfTableBean getXfoTrasfTableBean() throws ParerUserError {
        List<XfoTrasf> list = amministrazioneHelper.getXfoTrasf();
        XfoTrasfTableBean table = new XfoTrasfTableBean();
        if (!list.isEmpty()) {
            try {
                table = (XfoTrasfTableBean) Transform.entities2TableBean(list);
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                    | IllegalArgumentException | InvocationTargetException ex) {
                log.error("Errore durante il recupero delle trasformazioni per la generazione degli oggetti"
                        + ExceptionUtils.getRootCauseMessage(ex), ex);
                throw new ParerUserError(
                        "Errore durante il recupero delle trasformazioni per la generazione degli oggetti");
            }
        }
        return table;
    }

    public UsrVAbilStrutSacerXpingTableBean getNmAmbientiSacer(long idUtente) throws ParerUserError {
        List<UsrVAbilStrutSacerXping> list = amministrazioneHelper.getAmbientiFromUsrVAbilStrutSacerXping(idUtente);
        UsrVAbilStrutSacerXpingTableBean table = new UsrVAbilStrutSacerXpingTableBean();
        if (!list.isEmpty()) {
            try {
                table = (UsrVAbilStrutSacerXpingTableBean) Transform.entities2TableBean(list);
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                    | IllegalArgumentException | InvocationTargetException ex) {
                log.error(
                        "Errore durante il recupero degli ambienti di Sacer " + ExceptionUtils.getRootCauseMessage(ex),
                        ex);
                throw new ParerUserError("Errore durante il recupero degli ambienti di Sacer ");
            }
        }
        return table;
    }

    public UsrVAbilStrutSacerXpingTableBean getNmEntiSacer(long idUtente, BigDecimal idAmbiente) throws ParerUserError {
        List<UsrVAbilStrutSacerXping> list = amministrazioneHelper.getEntiFromUsrVAbilStrutSacerXping(idUtente,
                idAmbiente);
        UsrVAbilStrutSacerXpingTableBean table = new UsrVAbilStrutSacerXpingTableBean();
        if (!list.isEmpty()) {
            try {
                table = (UsrVAbilStrutSacerXpingTableBean) Transform.entities2TableBean(list);
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                    | IllegalArgumentException | InvocationTargetException ex) {
                log.error("Errore durante il recupero degli enti di Sacer " + ExceptionUtils.getRootCauseMessage(ex),
                        ex);
                throw new ParerUserError("Errore durante il recupero degli enti di Sacer ");
            }
        }
        return table;
    }

    public UsrVAbilStrutSacerXpingTableBean getNmEntiSacer(long idUtente, String nmAmbiente) throws ParerUserError {
        List<UsrVAbilStrutSacerXping> list = amministrazioneHelper.getEntiFromUsrVAbilStrutSacerXping(idUtente,
                nmAmbiente);
        UsrVAbilStrutSacerXpingTableBean table = new UsrVAbilStrutSacerXpingTableBean();
        if (!list.isEmpty()) {
            try {
                table = (UsrVAbilStrutSacerXpingTableBean) Transform.entities2TableBean(list);
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                    | IllegalArgumentException | InvocationTargetException ex) {
                log.error("Errore durante il recupero degli enti di Sacer " + ExceptionUtils.getRootCauseMessage(ex),
                        ex);
                throw new ParerUserError("Errore durante il recupero degli enti di Sacer ");
            }
        }
        return table;
    }

    public UsrVAbilStrutSacerXpingTableBean getNmStrutSacer(long idUtente, BigDecimal idEnte) throws ParerUserError {
        List<UsrVAbilStrutSacerXping> list = amministrazioneHelper.getStruttureFromUsrVAbilStrutSacerXping(idUtente,
                idEnte);
        UsrVAbilStrutSacerXpingTableBean table = new UsrVAbilStrutSacerXpingTableBean();
        if (!list.isEmpty()) {
            try {
                table = (UsrVAbilStrutSacerXpingTableBean) Transform.entities2TableBean(list);
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                    | IllegalArgumentException | InvocationTargetException ex) {
                log.error(
                        "Errore durante il recupero delle strutture di Sacer " + ExceptionUtils.getRootCauseMessage(ex),
                        ex);
                throw new ParerUserError("Errore durante il recupero delle strutture di Sacer ");
            }
        }
        return table;
    }

    public UsrVAbilStrutSacerXpingTableBean getNmStrutSacer(long idUtente, String nmAmbiente, String nmEnte)
            throws ParerUserError {
        List<UsrVAbilStrutSacerXping> list = amministrazioneHelper.getStruttureFromUsrVAbilStrutSacerXping(idUtente,
                nmAmbiente, nmEnte);
        UsrVAbilStrutSacerXpingTableBean table = new UsrVAbilStrutSacerXpingTableBean();
        if (!list.isEmpty()) {
            try {
                table = (UsrVAbilStrutSacerXpingTableBean) Transform.entities2TableBean(list);
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                    | IllegalArgumentException | InvocationTargetException ex) {
                log.error(
                        "Errore durante il recupero delle strutture di Sacer " + ExceptionUtils.getRootCauseMessage(ex),
                        ex);
                throw new ParerUserError("Errore durante il recupero delle strutture di Sacer ");
            }
        }
        return table;
    }

    public UsrVAbilStrutSacerXpingTableBean getOrganizazioniSacer(long idUtente, String tiDichVers)
            throws ParerUserError {
        List<UsrVAbilStrutSacerXping> list = amministrazioneHelper
                .getOrganizzazioniSacerFromUsrVAbilStrutSacerXping(idUtente, tiDichVers);
        UsrVAbilStrutSacerXpingTableBean table = new UsrVAbilStrutSacerXpingTableBean();
        if (!list.isEmpty()) {
            try {
                table = (UsrVAbilStrutSacerXpingTableBean) Transform.entities2TableBean(list);
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                    | IllegalArgumentException | InvocationTargetException ex) {
                log.error("Errore durante il recupero delle organizzazioni di Sacer "
                        + ExceptionUtils.getRootCauseMessage(ex), ex);
                throw new ParerUserError("Errore durante il recupero delle organizzazioni di Sacer ");
            }
        }
        return table;
    }

    public BaseTable getNmUseridSacerByPigVLisStrutVersSacerTableBean() {
        List<String> list = amministrazioneHelper.getNmUseridSacerByPigVLisStrutVersSacer();
        BaseTable table = new BaseTable();
        if (!list.isEmpty()) {
            for (String nmUserid : list) {
                // MAC 26019
                if (nmUserid != null) {
                    BaseRow r = new BaseRow();
                    r.setString("nm_userid_sacer", nmUserid);
                    table.add(r);
                }
            }
        }
        return table;
    }

    public UsrVAbilStrutSacerXpingTableBean getNmStrutSacer2(long idUtente, BigDecimal idEnte) throws ParerUserError {
        List<UsrVAbilStrutSacerXping> list = amministrazioneHelper.getOrganizIamFromUsrVAbilStrutSacerXping(idUtente,
                idEnte);
        UsrVAbilStrutSacerXpingTableBean table = new UsrVAbilStrutSacerXpingTableBean();
        if (!list.isEmpty()) {
            try {
                table = (UsrVAbilStrutSacerXpingTableBean) Transform.entities2TableBean(list);
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                    | IllegalArgumentException | InvocationTargetException ex) {
                log.error(
                        "Errore durante il recupero delle strutture di Sacer " + ExceptionUtils.getRootCauseMessage(ex),
                        ex);
                throw new ParerUserError("Errore durante il recupero delle strutture di Sacer ");
            }
        }
        return table;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public IamOrganizDaReplic deleteTipoObj(LogParam param, PigTipoObjectRowBean tipoObjectRowBean)
            throws IncoherenceException {
        PigTipoObject tipoObj = amministrazioneHelper.getPigTipoObjectById(tipoObjectRowBean.getIdTipoObject());
        List<PigObject> lista = amministrazioneHelper.getPigObjectListByTipoObj(tipoObjectRowBean.getIdTipoObject());
        if (!lista.isEmpty()) {
            throw new IncoherenceException("Rimozione versatore non riuscita: tipo oggetto utilizzato in versamenti");
        }
        PigVers vers = tipoObj.getPigVer();
        sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_TIPO_OGGETTO_VERSABILE,
                new BigDecimal(tipoObj.getIdTipoObject()), param.getNomePagina());
        amministrazioneHelper.removeEntity(tipoObj, true);
        return amministrazioneHelper.insertEntityIamOrganizDaReplic(vers, Constants.TiOperReplic.MOD);
    }

    public UsrVAbilStrutSacerXpingTableBean getDlCompositoOrganizAmbienti(long idUtente) throws ParerUserError {
        List<UsrVAbilStrutSacerXping> list = amministrazioneHelper.getDlCompositoOrganizAmbienti(idUtente);
        UsrVAbilStrutSacerXpingTableBean table = new UsrVAbilStrutSacerXpingTableBean();
        if (!list.isEmpty()) {
            try {
                table = (UsrVAbilStrutSacerXpingTableBean) Transform.entities2TableBean(list);
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                    | IllegalArgumentException | InvocationTargetException ex) {
                log.error(
                        "Errore durante il recupero degli ambienti di Sacer " + ExceptionUtils.getRootCauseMessage(ex),
                        ex);
                throw new ParerUserError("Errore durante il recupero degli ambienti di Sacer ");
            }
        }
        return table;
    }

    public UsrVAbilStrutSacerXpingTableBean getDlCompositoOrganizEnti(long idUtente) throws ParerUserError {
        List<UsrVAbilStrutSacerXping> list = amministrazioneHelper.getDlCompositoOrganizEnti(idUtente);
        UsrVAbilStrutSacerXpingTableBean table = new UsrVAbilStrutSacerXpingTableBean();
        if (!list.isEmpty()) {
            try {
                table = (UsrVAbilStrutSacerXpingTableBean) Transform.entities2TableBean(list);
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                    | IllegalArgumentException | InvocationTargetException ex) {
                log.error("Errore durante il recupero degli enti di Sacer " + ExceptionUtils.getRootCauseMessage(ex),
                        ex);
                throw new ParerUserError("Errore durante il recupero degli enti di Sacer ");
            }
        }
        return table;
    }

    public UsrVAbilStrutSacerXpingTableBean getDlCompositoOrganizStrutture(long idUtente) throws ParerUserError {
        List<UsrVAbilStrutSacerXping> list = amministrazioneHelper.getDlCompositoOrganizStrutture(idUtente);
        UsrVAbilStrutSacerXpingTableBean table = new UsrVAbilStrutSacerXpingTableBean();
        if (!list.isEmpty()) {
            try {
                table = (UsrVAbilStrutSacerXpingTableBean) Transform.entities2TableBean(list);
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                    | IllegalArgumentException | InvocationTargetException ex) {
                log.error(
                        "Errore durante il recupero delle strutture di Sacer " + ExceptionUtils.getRootCauseMessage(ex),
                        ex);
                throw new ParerUserError("Errore durante il recupero delle strutture di Sacer ");
            }
        }
        return table;
    }

    public BigDecimal checkPigObjectTrasf(BigDecimal idObject, BigDecimal idTipoObject, BigDecimal idVers,
            String cdKeyObjectTrasf, BigDecimal pgOggettoTrasf) throws ParerUserError {
        Long idObjTrasf = null;
        PigObjectTrasf objTrasfUniqueObj = amministrazioneHelper.getPigObjectTrasf(idObject, cdKeyObjectTrasf);
        PigObjectTrasf objTrasfUniqueVers = amministrazioneHelper.getPigObjectTrasf(idVers.longValue(),
                cdKeyObjectTrasf);
        PigObjectTrasf objTrasfUniquePg = amministrazioneHelper.getPigObjectTrasf(idObject, pgOggettoTrasf);
        if (objTrasfUniqueObj != null) {
            // Verifica se l'oggetto appartiene allo stesso versatore che si sta cercando di versare
            if (objTrasfUniqueObj.getPigVer().getIdVers() != idVers.longValue()) {
                throw new ParerUserError(
                        "L'oggetto da versare \u00E8 gi\u00E0 presente come oggetto generato dall'oggetto da trasformare ma appartiene ad un versatore diverso da quello definito in input");
            }
            if (objTrasfUniqueObj.getPigTipoObject().getIdTipoObject() != idTipoObject.longValue()) {
                throw new ParerUserError(
                        "L'oggetto da versare \u00E8 gi\u00E0 presente come oggetto generato dall'oggetto da trasformare ma \u00E8 di tipo diverso da quello definito in input");
            }
            if (objTrasfUniqueObj.getPgOggettoTrasf().compareTo(pgOggettoTrasf) != 0) {
                throw new ParerUserError(
                        "L'oggetto da versare \u00E8 gi\u00E0 presente come oggetto generato dall'oggetto da trasformare ma ha progressivo diverso da quello definito in input");
            }
            idObjTrasf = objTrasfUniqueObj.getIdObjectTrasf();
        }
        if (idObjTrasf == null && objTrasfUniquePg != null) {
            throw new ParerUserError(
                    "L'oggetto da versare non \u00E8 gi\u00E0 presente come oggetto generato dall'oggetto da trasformare ma ha progressivo coincidente con quello di un altro oggetto generato");
        }
        if (idObjTrasf == null && objTrasfUniqueVers != null) {
            throw new ParerUserError("L'oggetto da versare \u00E8 gi\u00E0 presente per il versatore a cui appartiene");
        }

        return (idObjTrasf != null ? BigDecimal.valueOf(idObjTrasf) : null);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public long createPigObjectTrasf(BigDecimal idObjectPadre, String cdKeyObject, String dsObject, BigDecimal idVers,
            BigDecimal idTipoObject, String dsPath, String dsHashFileVers, String tiAlgo, String cdEncoding,
            BigDecimal pgOggettoTrasf, String cdVersioneXml, String xml) {
        PigObject pigObjectPadre = amministrazioneHelper.findById(PigObject.class, idObjectPadre);
        PigObjectTrasf pigObjectTrasf = new PigObjectTrasf();
        pigObjectTrasf.setCdKeyObjectTrasf(cdKeyObject);
        pigObjectTrasf.setDsObjectTrasf(dsObject);
        pigObjectTrasf.setPigVer(amministrazioneHelper.findById(PigVers.class, idVers));
        pigObjectTrasf.setPigTipoObject(amministrazioneHelper.findById(PigTipoObject.class, idTipoObject));
        pigObjectTrasf.setDsPath(dsPath);
        pigObjectTrasf.setDsHashFileVers(dsHashFileVers);
        pigObjectTrasf.setTiAlgoHashFileVers(tiAlgo);
        pigObjectTrasf.setCdEncodingHashFileVers(cdEncoding);
        pigObjectTrasf.setPgOggettoTrasf(pgOggettoTrasf);
        pigObjectPadre.addPigObjectTrasf(pigObjectTrasf);

        if (StringUtils.isNotBlank(xml) && StringUtils.isNotBlank(cdVersioneXml)) {
            context.getBusinessObject(AmministrazioneEjb.class).createPigXmlObjectTrasf(pigObjectTrasf, cdVersioneXml,
                    xml);
        }
        amministrazioneHelper.insertEntity(pigObjectTrasf, true);
        return pigObjectTrasf.getIdObjectTrasf();
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void updatePigObjectTrasf(BigDecimal idObjTrasf, String dsObject, String dsPath, String dsHashFileVers,
            String tiAlgo, String cdEncoding, BigDecimal pgOggettoTrasf, String cdVersioneXml, String xml) {
        PigObjectTrasf pigObjectTrasf = amministrazioneHelper.findById(PigObjectTrasf.class, idObjTrasf);
        pigObjectTrasf.setDsObjectTrasf(dsObject);
        pigObjectTrasf.setDsPath(dsPath);
        pigObjectTrasf.setDsHashFileVers(dsHashFileVers);
        pigObjectTrasf.setTiAlgoHashFileVers(tiAlgo);
        pigObjectTrasf.setCdEncodingHashFileVers(cdEncoding);
        pigObjectTrasf.setPgOggettoTrasf(pgOggettoTrasf);
        pigObjectTrasf.setCdErr(null);
        pigObjectTrasf.setDlErr(null);

        if (StringUtils.isNotBlank(xml) && StringUtils.isNotBlank(cdVersioneXml)) {
            if (!pigObjectTrasf.getPigXmlObjectTrasfs().isEmpty()) {
                PigXmlObjectTrasf xmlTrasf = pigObjectTrasf.getPigXmlObjectTrasfs().get(0);
                xmlTrasf.setCdVersioneXmlVers(cdVersioneXml);
                xmlTrasf.setBlXml(xml);
            } else {
                context.getBusinessObject(AmministrazioneEjb.class).createPigXmlObjectTrasf(pigObjectTrasf,
                        cdVersioneXml, xml);
            }
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void checkPigObjectPadreAndUpdate(BigDecimal idObjectPadre, String tiStato) {
        PigObject pigObjectPadre = amministrazioneHelper.findById(PigObject.class, idObjectPadre);
        checkPigObjectPadreAndUpdate(pigObjectPadre, tiStato);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void checkPigObjectPadreAndUpdate(PigObject pigObjectPadre, String tiStato) {
        BigDecimal niTotObjectTrasf = pigObjectPadre.getNiTotObjectTrasf();
        if (niTotObjectTrasf != null && niTotObjectTrasf.intValue() == pigObjectPadre.getPigObjectTrasfs().size()) {
            PigSessioneIngest lastSession = amministrazioneHelper.findById(PigSessioneIngest.class,
                    pigObjectPadre.getIdLastSessioneIngest());
            lastSession.setTiStato(tiStato);
            monitoraggioHelper.creaStatoSessione(pigObjectPadre.getIdLastSessioneIngest(), tiStato,
                    Calendar.getInstance().getTime());
            pigObjectPadre.setTiStatoObject(tiStato);
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void createPigXmlObjectTrasf(PigObjectTrasf pigObjectTrasf, String cdVersioneXml, String xml) {
        PigXmlObjectTrasf xmlTrasf = new PigXmlObjectTrasf();
        xmlTrasf.setCdVersioneXmlVers(cdVersioneXml);
        xmlTrasf.setBlXml(xml);
        if (pigObjectTrasf.getPigXmlObjectTrasfs() == null) {
            pigObjectTrasf.setPigXmlObjectTrasfs(new ArrayList<>());
        }
        pigObjectTrasf.addPigXmlObjectTrasf(xmlTrasf);
    }

    public void checkPigObjectFigliAndUpdate(BigDecimal idObjectPadre) {
        PigObject pigObjectPadre = amministrazioneHelper.findById(PigObject.class, idObjectPadre);
        Long countAll = amministrazioneHelper.countPigObjectFigli(idObjectPadre, null);
        Long countInCodaHash = amministrazioneHelper.countPigObjectFigli(idObjectPadre,
                Constants.StatoOggetto.IN_CODA_HASH.name());
        Long countInAttesaSched = amministrazioneHelper.countPigObjectFigli(idObjectPadre,
                Constants.StatoOggetto.IN_ATTESA_SCHED.name());
        Long countChiusoOk = amministrazioneHelper.countPigObjectFigli(idObjectPadre,
                Constants.StatoOggetto.CHIUSO_OK.name());
        Long countChiusoErrNotif = amministrazioneHelper.countPigObjectFigli(idObjectPadre,
                Constants.StatoOggetto.CHIUSO_ERR_NOTIF.name());
        Long countStati = countInCodaHash + countInAttesaSched + countChiusoOk;

        // Se ho versato tutti gli oggetti figli
        if (pigObjectPadre.getPigObjectTrasfs() != null && pigObjectPadre.getNiTotObjectTrasf() != null) {
            if (pigObjectPadre.getPigObjectTrasfs().size() == pigObjectPadre.getNiTotObjectTrasf().intValue()) {
                // Se tutti gli oggetti figli hanno stato IN_ATTESA_SCHED o CHIUSO_OK
                if (pigObjectPadre.getPigObjectTrasfs().size() == countAll.intValue()) {
                    if (countAll.intValue() == countStati.intValue()) {
                        context.getBusinessObject(AmministrazioneEjb.class).checkPigObjectPadreAndUpdate(idObjectPadre,
                                Constants.StatoOggetto.VERSATO_A_PING.name());
                    } else if (countChiusoErrNotif > 0L) {
                        context.getBusinessObject(AmministrazioneEjb.class).checkPigObjectPadreAndUpdate(idObjectPadre,
                                Constants.StatoOggetto.ERRORE_VERSAMENTO_A_PING.name());
                    }
                } else {
                    context.getBusinessObject(AmministrazioneEjb.class).checkPigObjectPadreAndUpdate(idObjectPadre,
                            Constants.StatoOggetto.ERRORE_VERSAMENTO_A_PING.name());
                }
            }
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void updatePigObjectTrasf(BigDecimal idObjTrasf, String cdErr, String dsErr) {
        PigObjectTrasf pigObjectTrasf = amministrazioneHelper.findById(PigObjectTrasf.class, idObjTrasf);
        pigObjectTrasf.setCdErr(cdErr);
        pigObjectTrasf.setDlErr(dsErr);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Object[] importaVersatore(LogParam param, BigDecimal idAmb, String strXml, String nmAmbiente, String nmVers,
            String dsVers, BigDecimal idEnteConvenz, String nmEnteConvenz, BigDecimal idEnteFornitEstern,
            String nmEnteFornitEstern, Date dtIniValAppartEnteSiam, Date dtFineValAppartEnteSiam, Date dtIniValVers,
            Date dtFineValVers, Date dtIniValAppartAmbiente, Date dtFinValAppartAmbiente, String dsPathInputFtp,
            String dsPathOutputFtp, String dsPathTrasf, String tiDichVers, BigDecimal idOrganizIam, long idUserIamCor)
            throws ParerUserError {

        XADiskConnection xadConn = null;

        long idOggetto = 0;
        HashMap mappa = new HashMap<>();
        mappa.put("NM_AMBIENTE_VERS", nmAmbiente);
        mappa.put("NM_VERS", nmVers);
        mappa.put("DS_VERS", dsVers);
        mappa.put("NM_ENTE_CONVENZ", StringUtils.isNotBlank(nmEnteConvenz) ? nmEnteConvenz : "null");
        mappa.put("NM_ENTE_FORNIT_ESTERN", StringUtils.isNotBlank(nmEnteFornitEstern) ? nmEnteFornitEstern : "null");
        mappa.put("DT_INI_VAL_APPART_ENTE_SIAM", DateUtil.formatDateWithSlash(dtIniValAppartEnteSiam));
        mappa.put("DT_FINE_VAL_APPART_ENTE_SIAM", DateUtil.formatDateWithSlash(dtFineValAppartEnteSiam));
        mappa.put("DT_INI_VAL_VERS", DateUtil.formatDateWithSlash(dtIniValVers));
        mappa.put("DT_FINE_VAL_VERS", DateUtil.formatDateWithSlash(dtFineValVers));
        mappa.put("DT_INI_VAL_APPART_AMBIENTE", DateUtil.formatDateWithSlash(dtIniValAppartAmbiente));
        mappa.put("DT_FIN_VAL_APPART_AMBIENTE", DateUtil.formatDateWithSlash(dtFinValAppartAmbiente));
        mappa.put("DS_PATH_INPUT_FTP", dsPathInputFtp);
        mappa.put("DS_PATH_OUTPUT_FTP", dsPathOutputFtp);
        mappa.put("DS_PATH_TRASF", dsPathTrasf == null ? "null" : dsPathTrasf);

        if (amministrazioneHelper.getPigVersByName(nmVers, idAmb) != null) {
            throw new ParerUserError("Nome Versatore gi\u00E0 utilizzato nel database.");
        }

        strXml = exportImportFotoHelper.sostituisciTutto(strXml, mappa);
        // Recupera info su presenza corrispondenze in Sacer per tipi oggetto
        String msg = null;
        if (existsCorrispondenzeSacerTipoObj(strXml)) {
            msg = "Esiste almeno un tipo oggetto per il quale occorre concludere la configurazione indicando la corrispondenza con l'organizzazione";
        }
        try {
            // Controlli corrispondenza
            if (idEnteConvenz != null) {
                controlliCorrispondenzaSacer(idUserIamCor, idEnteConvenz, idOrganizIam, tiDichVers);
            }
            idOggetto = exportImportFotoHelper.importFoto(strXml, "SACER_PING.IMPORTA_FOTO_VERSATORE");
            loggaVersatoreETipiObject(param, new BigDecimal(idOggetto));
            log.info("Oggetto importato con ID [{}]", idOggetto);
            PigVers versatore = amministrazioneHelper.findById(PigVers.class, idOggetto);
            // Aggiorno il parametro del prefisso
            String prefisso = configHelper.getValoreParamApplicByIdVers(Constants.DS_PREFISSO_PATH, idAmb,
                    BigDecimal.valueOf(versatore.getIdVers()));

            versatore.setDsPathInputFtp(prefisso + versatore.getNmVers() + "/INPUT_FOLDER/");
            versatore.setDsPathOutputFtp(prefisso + versatore.getNmVers() + "/OUTPUT_FOLDER/");
            versatore.setDsPathTrasf(prefisso + versatore.getNmVers() + "/TRASFORMATI/");

            // MEV 30790 - creo le cartelle necessarie su filesystem se non esistono.
            try {
                // Aggiorno il parametro del prefisso
                File basePath = new File(
                        configHelper.getValoreParamApplicByApplic(it.eng.xformer.common.Constants.ROOT_FTP)
                                + File.separator + prefisso + versatore.getNmVers());

                xadConn = xadCf.getConnection();

                if (!xadConn.fileExists(basePath)) {
                    xadConn.createFile(basePath, true);
                }

                File path = new File(basePath + "/INPUT_FOLDER/");
                if (!xadConn.fileExists(path)) {
                    xadConn.createFile(path, true);
                }

                path = new File(basePath + "/OUTPUT_FOLDER/");
                if (!xadConn.fileExists(path)) {
                    xadConn.createFile(path, true);
                }

                path = new File(basePath + "/TRASFORMATI/");
                if (!xadConn.fileExists(path)) {
                    xadConn.createFile(path, true);
                }
            } catch (Exception ex) {
                log.error("Errore durante la creazione delle cartelle per il versatore " + versatore.getNmVers() + " : "
                        + ex.getMessage());
                throw new ParerUserError("Errore tecnico nella creazione delle cartelle per il versatore.");
            }

            if (idEnteConvenz != null) {
                // Inserisco la corrisponza a Sacer del Versatore
                insertPigDichVersSacer(BigDecimal.valueOf(idOggetto), tiDichVers, idOrganizIam);
            }
            it.eng.sacerasi.common.Constants.TiOperReplic tiOper = it.eng.sacerasi.common.Constants.TiOperReplic.INS;
            IamOrganizDaReplic replic = amministrazioneHelper.insertEntityIamOrganizDaReplicNewTx(versatore, tiOper);
            replicateToIam(replic);
            Object[] dati = new Object[2];
            dati[0] = BigDecimal.valueOf(idOggetto);
            dati[1] = msg;
            return dati;
        } catch (ParerUserError e) {
            throw e;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public String esportaVersatore(BigDecimal idVers) {
        String strXml = null;
        try {
            strXml = exportImportFotoHelper.exportFoto(idVers, "SACER_PING.ESPORTA_FOTO_VERSATORE");
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        return strXml;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Object[] duplicaVersatore(LogParam param, BigDecimal idAmb, BigDecimal idVers, String nmAmbiente,
            String nmVers, String dsVers, BigDecimal idEnteConvenz, String nmEnteConvenz, BigDecimal idEnteFornitEstern,
            String nmEnteFornitEstern, Date dtIniValAppartEnteSiam, Date dtFineValAppartEnteSiam, Date dtIniValVers,
            Date dtFineValVers, Date dtIniValAppartAmbiente, Date dtFinValAppartAmbiente, String dsPathInputFtp,
            String dsPathOutputFtp, String dsPathTrasf, String tiDichVers, BigDecimal idOrganizIam, long idUserIamCor)
            throws ParerUserError {
        XADiskConnection xadConn = null;

        long idOggetto = 0;
        try {

            String strXml = esportaVersatore(idVers);
            HashMap<String, String> mappa = new HashMap<>();

            // Recupera info su presenza corrispondenze in Sacer per tipi oggetto
            String msg = null;
            if (existsCorrispondenzeSacerTipoObj(strXml)) {
                msg = "Esiste almeno un tipo oggetto per il quale occorre concludere la configurazione indicando la corrispondenza con l'organizzazione";
            }

            // Controlli corrispondenza
            if (idEnteConvenz != null) {
                controlliCorrispondenzaSacer(idUserIamCor, idEnteConvenz, idOrganizIam, tiDichVers);
            }

            mappa.put("NM_AMBIENTE_VERS", nmAmbiente);
            mappa.put("NM_VERS", nmVers);
            mappa.put("DS_VERS", dsVers);
            mappa.put("NM_ENTE_CONVENZ", StringUtils.isNotBlank(nmEnteConvenz) ? nmEnteConvenz : "null");
            mappa.put("NM_ENTE_FORNIT_ESTERN",
                    StringUtils.isNotBlank(nmEnteFornitEstern) ? nmEnteFornitEstern : "null");
            mappa.put("DT_INI_VAL_APPART_ENTE_SIAM", DateUtil.formatDateWithSlash(dtIniValAppartEnteSiam));
            mappa.put("DT_FINE_VAL_APPART_ENTE_SIAM", DateUtil.formatDateWithSlash(dtFineValAppartEnteSiam));
            mappa.put("DT_INI_VAL_VERS", DateUtil.formatDateWithSlash(dtIniValVers));
            mappa.put("DT_FINE_VAL_VERS", DateUtil.formatDateWithSlash(dtFineValVers));
            mappa.put("DT_INI_VAL_APPART_AMBIENTE", DateUtil.formatDateWithSlash(dtIniValAppartAmbiente));
            mappa.put("DT_FIN_VAL_APPART_AMBIENTE", DateUtil.formatDateWithSlash(dtFinValAppartAmbiente));
            mappa.put("DS_PATH_INPUT_FTP", dsPathInputFtp);
            mappa.put("DS_PATH_OUTPUT_FTP", dsPathOutputFtp);
            mappa.put("DS_PATH_TRASF", dsPathTrasf == null ? "null" : dsPathTrasf);

            if (amministrazioneHelper.getPigVersByName(nmVers, idAmb) != null) {
                throw new ParerUserError("Nome Versatore gi\u00E0 utilizzato nel database.");
            }

            strXml = exportImportFotoHelper.sostituisciTutto(strXml, mappa);
            idOggetto = exportImportFotoHelper.importFoto(strXml, "SACER_PING.IMPORTA_FOTO_VERSATORE");
            loggaVersatoreETipiObject(param, new BigDecimal(idOggetto));
            log.info("Oggetto importato con ID [{}]", idOggetto);

            PigVers versatore = amministrazioneHelper.findById(PigVers.class, idOggetto);
            // Aggiorno il parametro del prefisso
            String prefisso = configHelper.getValoreParamApplicByIdVers(Constants.DS_PREFISSO_PATH, idAmb,
                    BigDecimal.valueOf(versatore.getIdVers()));

            versatore.setDsPathInputFtp(prefisso + versatore.getNmVers() + "/INPUT_FOLDER/");
            versatore.setDsPathOutputFtp(prefisso + versatore.getNmVers() + "/OUTPUT_FOLDER/");
            versatore.setDsPathTrasf(prefisso + versatore.getNmVers() + "/TRASFORMATI/");
            versatore.setDsPathTrasf(prefisso + versatore.getNmVers() + "/DA_VERSARE/");

            // MEV 30790 - creo le cartelle necessarie su filesystem se non esistono.
            try {
                // Aggiorno il parametro del prefisso
                File basePath = new File(
                        configHelper.getValoreParamApplicByApplic(it.eng.xformer.common.Constants.ROOT_FTP)
                                + File.separator + prefisso + versatore.getNmVers());

                xadConn = xadCf.getConnection();

                if (!xadConn.fileExists(basePath)) {
                    xadConn.createFile(basePath, true);
                }

                File path = new File(basePath + "/INPUT_FOLDER/");
                if (!xadConn.fileExists(path)) {
                    xadConn.createFile(path, true);
                }

                path = new File(basePath + "/OUTPUT_FOLDER/");
                if (!xadConn.fileExists(path)) {
                    xadConn.createFile(path, true);
                }

                path = new File(basePath + "/TRASFORMATI/");
                if (!xadConn.fileExists(path)) {
                    xadConn.createFile(path, true);
                }
            } catch (Exception ex) {
                log.error("Errore durante la creazione delle cartelle per il versatore " + versatore.getNmVers() + " : "
                        + ex.getMessage());
                throw new ParerUserError("Errore tecnico nella creazione delle cartelle per il versatore.");
            }

            if (idEnteConvenz != null) {
                // Inserisco la corrisponza a Sacer del Versatore
                insertPigDichVersSacer(BigDecimal.valueOf(idOggetto), tiDichVers, idOrganizIam);
            }
            it.eng.sacerasi.common.Constants.TiOperReplic tiOper = it.eng.sacerasi.common.Constants.TiOperReplic.INS;
            IamOrganizDaReplic replic = amministrazioneHelper.insertEntityIamOrganizDaReplicNewTx(versatore, tiOper);
            replicateToIam(replic);
            Object[] dati = new Object[2];
            dati[0] = BigDecimal.valueOf(idOggetto);
            dati[1] = msg;
            return dati;
        } catch (ParerUserError e) {
            throw e;
        } catch (Exception ex) {
            throw new RuntimeException("Errore nella duplicazione del versatore", ex);
        }
    }

    // Usato dalla duplicazione versatore e importazione
    private void loggaVersatoreETipiObject(LogParam param, BigDecimal idVers) {
        sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_VERSATORE, idVers, param.getNomePagina());
        List<PigTipoObject> l = amministrazioneHelper.getPigTipoObjectList(idVers);
        if (l != null) {
            for (PigTipoObject pigTipoObject : l) {
                sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                        param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_TIPO_OGGETTO_VERSABILE,
                        new BigDecimal(pigTipoObject.getIdTipoObject()), param.getNomePagina());
            }
        }
    }

    public PigParamApplicTableBean getPigParamApplicTableBean(String tiParamApplic, String tiGestioneParam,
            String flAppartApplic, String flAppartAmbiente, String flAppartVers, String flAppartTipoOggetto) {
        PigParamApplicTableBean paramApplicTableBean = new PigParamApplicTableBean();
        List<PigParamApplic> paramApplicList = amministrazioneHelper.getPigParamApplicList(tiParamApplic,
                tiGestioneParam, flAppartApplic, flAppartAmbiente, flAppartVers, flAppartTipoOggetto);

        try {
            if (paramApplicList != null && !paramApplicList.isEmpty()) {
                for (PigParamApplic paramApplic : paramApplicList) {
                    PigParamApplicRowBean paramApplicRowBean = (PigParamApplicRowBean) Transform
                            .entity2RowBean(paramApplic);
                    paramApplicRowBean.setString("ds_valore_param_applic", "");
                    for (PigValoreParamApplic valoreParamApplic : paramApplic.getPigValoreParamApplics()) {
                        if (valoreParamApplic.getTiAppart().equals("APPLIC")) {
                            paramApplicRowBean.setString("ds_valore_param_applic",
                                    valoreParamApplic.getDsValoreParamApplic());
                        }
                    }
                    paramApplicTableBean.add(paramApplicRowBean);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return paramApplicTableBean;
    }

    // MEV 32650
    public PigParamApplicTableBean getPigParamApplicTableBean(String tiParamApplic, String tiGestioneParam,
            String flAppartApplic, String flAppartAmbiente, String flAppartVers, String flAppartTipoOggetto,
            boolean filterValid) {
        PigParamApplicTableBean paramApplicTableBean = new PigParamApplicTableBean();
        List<PigParamApplic> paramApplicList = amministrazioneHelper.getPigParamApplicList(tiParamApplic,
                tiGestioneParam, flAppartApplic, flAppartAmbiente, flAppartVers, flAppartTipoOggetto, filterValid);

        try {
            if (paramApplicList != null && !paramApplicList.isEmpty()) {
                for (PigParamApplic paramApplic : paramApplicList) {
                    PigParamApplicRowBean paramApplicRowBean = (PigParamApplicRowBean) Transform
                            .entity2RowBean(paramApplic);
                    paramApplicRowBean.setString("ds_valore_param_applic", "");
                    for (PigValoreParamApplic valoreParamApplic : paramApplic.getPigValoreParamApplics()) {
                        if (valoreParamApplic.getTiAppart().equals("APPLIC")) {
                            paramApplicRowBean.setString("ds_valore_param_applic",
                                    valoreParamApplic.getDsValoreParamApplic());
                        }
                    }
                    paramApplicTableBean.add(paramApplicRowBean);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return paramApplicTableBean;
    }
    // end MEV 32650

    public boolean checkParamApplic(String nmParamApplic, BigDecimal idParamApplic) {
        return amministrazioneHelper.existsPigParamApplic(nmParamApplic, idParamApplic);
    }

    /**
     * Esegue il salvataggio del rowBean del parametro di configurazione
     *
     * @param row
     *            il rowBean da salvare su DB
     *
     * @return true in mancanza di eccezioni
     */
    public boolean saveConfiguration(PigParamApplicRowBean row) {
        boolean result = false;
        PigParamApplic config;
        boolean newRow;

        try {

            if (row.getIdParamApplic() != null) {
                config = amministrazioneHelper.findById(PigParamApplic.class, row.getIdParamApplic().longValue());
                newRow = false;
            } else {
                config = new PigParamApplic();
                newRow = true;
            }

            config.setTiParamApplic(row.getTiParamApplic());
            config.setTiGestioneParam(row.getTiGestioneParam());
            config.setNmParamApplic(row.getNmParamApplic());
            config.setDmParamApplic(row.getDmParamApplic());
            config.setDsListaValoriAmmessi(row.getDsListaValoriAmmessi());
            config.setDsParamApplic(row.getDsParamApplic());
            config.setTiValoreParamApplic(row.getTiValoreParamApplic());
            config.setFlAppartApplic(row.getFlAppartApplic());
            config.setFlAppartAmbiente(row.getFlAppartAmbiente());
            config.setFlAppartVers(row.getFlAppartVers());
            config.setFlAppartTipoOggetto(row.getFlAppartTipoOggetto());
            config.setCdVersioneAppIni(row.getCdVersioneAppIni());
            config.setCdVersioneAppFine(row.getCdVersioneAppFine());

            if (newRow) {
                amministrazioneHelper.getEntityManager().persist(config);
                row.setIdParamApplic(BigDecimal.valueOf(config.getIdParamApplic()));
            }
            result = true;
            amministrazioneHelper.getEntityManager().flush();

            // GESTIONE DS_VALORE_PARAM_APPLIC
            // Se è una nuova riga di PigParamApplic, nel caso sia stato inserito il valore, vai a persisterlo
            if (newRow) {
                if (row.getString("ds_valore_param_applic") != null
                        && !row.getString("ds_valore_param_applic").equals("")) {
                    PigValoreParamApplic valore = new PigValoreParamApplic();
                    valore.setPigParamApplic(config);
                    valore.setTiAppart("APPLIC");
                    valore.setPigAmbienteVer(null);
                    valore.setPigVer(null);
                    valore.setDsValoreParamApplic(row.getString("ds_valore_param_applic"));
                    amministrazioneHelper.getEntityManager().persist(valore);
                }
            } else {
                // Se invece la riga di PigParamApplic già esisteva:
                // Se c'è un valore parametro di tipo APPLIC, modificalo
                PigValoreParamApplic valoreParamApplic = amministrazioneHelper
                        .getPigValoreParamApplic(config.getIdParamApplic(), "APPLIC");
                if (valoreParamApplic != null) {
                    if (row.getString("ds_valore_param_applic") != null
                            && !row.getString("ds_valore_param_applic").equals("")) {
                        valoreParamApplic.setDsValoreParamApplic(row.getString("ds_valore_param_applic"));
                    } else {
                        amministrazioneHelper.removeEntity(valoreParamApplic, true);
                    }
                } else {
                    if (row.getString("ds_valore_param_applic") != null
                            && !row.getString("ds_valore_param_applic").equals("")) {
                        PigValoreParamApplic valore = new PigValoreParamApplic();
                        valore.setPigParamApplic(config);
                        valore.setTiAppart("APPLIC");
                        valore.setPigAmbienteVer(null);
                        valore.setPigVer(null);
                        valore.setDsValoreParamApplic(row.getString("ds_valore_param_applic"));
                        amministrazioneHelper.getEntityManager().persist(valore);
                    }
                }
            }

        } catch (Exception ex) {
            log.error(ex.getMessage());
            result = false;
        }
        return result;
    }

    public BaseTable getTiParamApplicBaseTable() {
        BaseTable table = new BaseTable();
        List<String> tiParamApplicList = amministrazioneHelper.getTiParamApplic();
        if (tiParamApplicList != null && !tiParamApplicList.isEmpty()) {
            try {
                for (String row : tiParamApplicList) {
                    BaseRowInterface r = new BaseRow();
                    r.setString(PigParamApplicTableDescriptor.COL_TI_PARAM_APPLIC, row);
                    table.add(r);
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
        return table;
    }

    public PigAmbienteVersRowBean getPigAmbienteVersByVers(BigDecimal idVers) {
        PigAmbienteVersRowBean ambienteVersRowBean = new PigAmbienteVersRowBean();
        try {
            PigAmbienteVers ambienteVers = amministrazioneHelper.retrievePigAmbienteVersByVers(idVers);
            ambienteVersRowBean = (PigAmbienteVersRowBean) Transform.entity2RowBean(ambienteVers);
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                | IllegalArgumentException | InvocationTargetException e) {
            log.error("Errore durante il recupero dell'ambiente versatore " + ExceptionUtils.getRootCauseMessage(e), e);
        }
        return ambienteVersRowBean;
    }

    /**
     * Restituisce un array di object con i tablebean dei parametri
     *
     * @param idAmbienteVers
     *            id ambiente di versamento
     * @param idVers
     *            id versamento
     * @param funzione
     *            lista funzioni
     *
     * @return il tablebean
     *
     * @throws ParerUserError
     *             errore generico
     */
    public Object[] getPigParamApplicVers(BigDecimal idAmbienteVers, BigDecimal idVers, List<String> funzione)
            throws ParerUserError {
        Object[] parametriObj = new Object[3];
        PigParamApplicTableBean paramApplicAmministrazioneTableBean = new PigParamApplicTableBean();
        PigParamApplicTableBean paramApplicGestioneTableBean = new PigParamApplicTableBean();
        PigParamApplicTableBean paramApplicConservazioneTableBean = new PigParamApplicTableBean();
        // Ricavo la lista dei parametri definiti per l'ENTE
        List<PigParamApplic> paramApplicList = amministrazioneHelper.getPigParamApplicListVers(funzione);
        if (!paramApplicList.isEmpty()) {
            try {
                // Per ogni parametro, popolo i valori su applicazione, ambiente ed ente ricavandoli da
                // IAM_VALORE_PARAM_APPLIC
                for (PigParamApplic paramApplic : paramApplicList) {
                    PigParamApplicRowBean paramApplicRowBean = (PigParamApplicRowBean) Transform
                            .entity2RowBean(paramApplic);
                    populateParamApplicVersatoreRowBean(paramApplicRowBean, idAmbienteVers, idVers,
                            paramApplic.getTiGestioneParam());
                    switch (paramApplic.getTiGestioneParam()) {
                    case "amministrazione":
                        paramApplicAmministrazioneTableBean.add(paramApplicRowBean);
                        break;
                    case "gestione":
                        paramApplicGestioneTableBean.add(paramApplicRowBean);
                        break;
                    case "conservazione":
                        paramApplicConservazioneTableBean.add(paramApplicRowBean);
                        break;
                    default:
                        break;
                    }
                }

            } catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException | InstantiationException
                    | NoSuchMethodException | InvocationTargetException e) {
                log.error("Errore durante il recupero dei parametri sul versatore "
                        + ExceptionUtils.getRootCauseMessage(e), e);
                throw new ParerUserError("Errore durante il recupero dei parametri sul versatore");
            }
        }
        parametriObj[0] = paramApplicAmministrazioneTableBean;
        parametriObj[1] = paramApplicGestioneTableBean;
        parametriObj[2] = paramApplicConservazioneTableBean;
        return parametriObj;
    }

    private void populateParamApplicVersatoreRowBean(PigParamApplicRowBean paramApplicRowBean,
            BigDecimal idAmbienteVers, BigDecimal idVers, String tiGestioneParam) {
        String nomeCampoVers = tiGestioneParam.equals("amministrazione") ? "ds_valore_param_applic_vers_amm"
                : tiGestioneParam.equals("gestione") ? "ds_valore_param_applic_vers_gest"
                        : "ds_valore_param_applic_vers_cons";

        // Determino i valori su applicazione, ambiente e versatore
        PigValoreParamApplic valoreParamApplicApplic = amministrazioneHelper
                .getPigValoreParamApplic(paramApplicRowBean.getIdParamApplic(), "APPLIC", null, null, null);
        if (valoreParamApplicApplic != null) {
            paramApplicRowBean.setString("ds_valore_param_applic_applic",
                    valoreParamApplicApplic.getDsValoreParamApplic());
        }

        if (idAmbienteVers != null) {
            PigValoreParamApplic valoreParamApplicAmbiente = amministrazioneHelper.getPigValoreParamApplic(
                    paramApplicRowBean.getIdParamApplic(), "AMBIENTE", idAmbienteVers, null, null);
            if (valoreParamApplicAmbiente != null) {
                paramApplicRowBean.setString("ds_valore_param_applic_ambiente",
                        valoreParamApplicAmbiente.getDsValoreParamApplic());
            }
        }

        if (idVers != null) {
            PigValoreParamApplic valoreParamApplicVers = amministrazioneHelper
                    .getPigValoreParamApplic(paramApplicRowBean.getIdParamApplic(), "VERS", null, idVers, null);
            if (valoreParamApplicVers != null) {
                paramApplicRowBean.setString(nomeCampoVers, valoreParamApplicVers.getDsValoreParamApplic());
                paramApplicRowBean.setBigDecimal("id_valore_param_applic",
                        BigDecimal.valueOf(valoreParamApplicVers.getIdValoreParamApplic()));
            }
        }
    }

    /**
     * Restituisce un array di object con i tablebean dei parametri
     *
     * @param idAmbienteVers
     *            id ambiente di versamento
     * @param funzione
     *            lista funzioni
     *
     * @return il tablebean
     *
     * @throws ParerUserError
     *             errore generico
     */
    public Object[] getPigParamApplicAmbiente(BigDecimal idAmbienteVers, List<String> funzione) throws ParerUserError {
        Object[] parametriObj = new Object[3];
        PigParamApplicTableBean paramApplicAmministrazioneTableBean = new PigParamApplicTableBean();
        PigParamApplicTableBean paramApplicGestioneTableBean = new PigParamApplicTableBean();
        PigParamApplicTableBean paramApplicConservazioneTableBean = new PigParamApplicTableBean();
        // Ricavo la lista dei parametri definiti per l'AMBIENTE
        List<PigParamApplic> paramApplicList = amministrazioneHelper.getPigParamApplicListAmbiente(funzione);
        if (!paramApplicList.isEmpty()) {
            try {
                // Per ogni parametro, popolo i valori su applicazione ed ambiente ricavandoli da
                // PIG_VALORE_PARAM_APPLIC
                for (PigParamApplic paramApplic : paramApplicList) {
                    PigParamApplicRowBean paramApplicRowBean = (PigParamApplicRowBean) Transform
                            .entity2RowBean(paramApplic);
                    populateParamApplicAmbienteRowBean(paramApplicRowBean, idAmbienteVers,
                            paramApplic.getTiGestioneParam());
                    switch (paramApplic.getTiGestioneParam()) {
                    case "amministrazione":
                        paramApplicAmministrazioneTableBean.add(paramApplicRowBean);
                        break;
                    case "gestione":
                        paramApplicGestioneTableBean.add(paramApplicRowBean);
                        break;
                    case "conservazione":
                        paramApplicConservazioneTableBean.add(paramApplicRowBean);
                        break;
                    default:
                        break;
                    }
                }
            } catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException | InstantiationException
                    | NoSuchMethodException | InvocationTargetException e) {
                log.error("Errore durante il recupero dei parametri sull'ambiente versatore "
                        + ExceptionUtils.getRootCauseMessage(e), e);
                throw new ParerUserError("Errore durante il recupero dei parametri sull'ambiente versatore");
            }
        }
        parametriObj[0] = paramApplicAmministrazioneTableBean;
        parametriObj[1] = paramApplicGestioneTableBean;
        parametriObj[2] = paramApplicConservazioneTableBean;
        return parametriObj;
    }

    private void populateParamApplicAmbienteRowBean(PigParamApplicRowBean paramApplicRowBean, BigDecimal idAmbienteVers,
            String tiGestioneParam) {
        String nomeCampoAmbiente = tiGestioneParam.equals("amministrazione") ? "ds_valore_param_applic_ambiente_amm"
                : tiGestioneParam.equals("gestione") ? "ds_valore_param_applic_ambiente_gest"
                        : "ds_valore_param_applic_ambiente_cons";

        // Determino i valori su applicazione ed ambiente
        PigValoreParamApplic valoreParamApplicApplic = amministrazioneHelper
                .getPigValoreParamApplic(paramApplicRowBean.getIdParamApplic(), "APPLIC", null, null, null);
        if (valoreParamApplicApplic != null) {
            paramApplicRowBean.setString("ds_valore_param_applic_applic",
                    valoreParamApplicApplic.getDsValoreParamApplic());
        }

        if (idAmbienteVers != null) {
            PigValoreParamApplic valoreParamApplicAmbiente = amministrazioneHelper.getPigValoreParamApplic(
                    paramApplicRowBean.getIdParamApplic(), "AMBIENTE", idAmbienteVers, null, null);
            if (valoreParamApplicAmbiente != null) {
                paramApplicRowBean.setString(nomeCampoAmbiente, valoreParamApplicAmbiente.getDsValoreParamApplic());
                paramApplicRowBean.setBigDecimal("id_valore_param_applic",
                        BigDecimal.valueOf(valoreParamApplicAmbiente.getIdValoreParamApplic()));
            }
        }
    }

    /**
     * Restituisce un array di object con i tablebean dei parametri
     *
     * @param idAmbienteVers
     *            id ambiente di versamento
     * @param idVers
     *            id versamento
     * @param idTipoObject
     *            id tipo oggetto
     * @param funzione
     *            lista funzioni
     *
     * @return il tablebean
     *
     * @throws ParerUserError
     *             errore generico
     */
    public Object[] getPigParamApplicTipoOggetto(BigDecimal idAmbienteVers, BigDecimal idVers, BigDecimal idTipoObject,
            List<String> funzione) throws ParerUserError {
        Object[] parametriObj = new Object[3];
        PigParamApplicTableBean paramApplicAmministrazioneTableBean = new PigParamApplicTableBean();
        PigParamApplicTableBean paramApplicGestioneTableBean = new PigParamApplicTableBean();
        PigParamApplicTableBean paramApplicConservazioneTableBean = new PigParamApplicTableBean();
        // Ricavo la lista dei parametri definiti per il tipo oggetto
        List<PigParamApplic> paramApplicList = amministrazioneHelper.getPigParamApplicListTipoOggetto(funzione);
        if (!paramApplicList.isEmpty()) {
            try {
                // Per ogni parametro, popolo i valori su applicazione, ambiente, versatore e tipo oggetto ricavandoli
                // da PIG_VALORE_PARAM_APPLIC
                for (PigParamApplic paramApplic : paramApplicList) {
                    PigParamApplicRowBean paramApplicRowBean = (PigParamApplicRowBean) Transform
                            .entity2RowBean(paramApplic);
                    populateParamApplicTipoOggettoRowBean(paramApplicRowBean, idAmbienteVers, idVers, idTipoObject,
                            paramApplic.getTiGestioneParam());
                    switch (paramApplic.getTiGestioneParam()) {
                    case "amministrazione":
                        paramApplicAmministrazioneTableBean.add(paramApplicRowBean);
                        break;
                    case "gestione":
                        paramApplicGestioneTableBean.add(paramApplicRowBean);
                        break;
                    case "conservazione":
                        paramApplicConservazioneTableBean.add(paramApplicRowBean);
                        break;
                    default:
                        break;
                    }
                }

            } catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException | InstantiationException
                    | NoSuchMethodException | InvocationTargetException e) {
                log.error("Errore durante il recupero dei parametri sul tipo oggetto "
                        + ExceptionUtils.getRootCauseMessage(e), e);
                throw new ParerUserError("Errore durante il recupero dei parametri sul tipo oggetto");
            }
        }
        parametriObj[0] = paramApplicAmministrazioneTableBean;
        parametriObj[1] = paramApplicGestioneTableBean;
        parametriObj[2] = paramApplicConservazioneTableBean;
        return parametriObj;
    }

    private void populateParamApplicTipoOggettoRowBean(PigParamApplicRowBean paramApplicRowBean,
            BigDecimal idAmbienteVers, BigDecimal idVers, BigDecimal idTipoObject, String tiGestioneParam) {
        String nomeCampoVers = tiGestioneParam.equals("amministrazione") ? "ds_valore_param_applic_tipo_oggetto_amm"
                : tiGestioneParam.equals("gestione") ? "ds_valore_param_applic_tipo_oggetto_gest"
                        : "ds_valore_param_applic_tipo_oggetto_cons";

        // Determino i valori su applicazione, ambiente, versatore e tipo oggetto
        PigValoreParamApplic valoreParamApplicApplic = amministrazioneHelper
                .getPigValoreParamApplic(paramApplicRowBean.getIdParamApplic(), "APPLIC", null, null, null);
        if (valoreParamApplicApplic != null) {
            paramApplicRowBean.setString("ds_valore_param_applic_applic",
                    valoreParamApplicApplic.getDsValoreParamApplic());
        }

        if (idAmbienteVers != null) {
            PigValoreParamApplic valoreParamApplicAmbiente = amministrazioneHelper.getPigValoreParamApplic(
                    paramApplicRowBean.getIdParamApplic(), "AMBIENTE", idAmbienteVers, null, null);
            if (valoreParamApplicAmbiente != null) {
                paramApplicRowBean.setString("ds_valore_param_applic_ambiente",
                        valoreParamApplicAmbiente.getDsValoreParamApplic());
            }
        }

        if (idVers != null) {
            PigValoreParamApplic valoreParamApplicVers = amministrazioneHelper
                    .getPigValoreParamApplic(paramApplicRowBean.getIdParamApplic(), "VERS", null, idVers, null);
            if (valoreParamApplicVers != null) {
                paramApplicRowBean.setString("ds_valore_param_applic_vers",
                        valoreParamApplicVers.getDsValoreParamApplic());
            }
        }

        if (idTipoObject != null) {
            PigValoreParamApplic valoreParamApplicVers = amministrazioneHelper.getPigValoreParamApplic(
                    paramApplicRowBean.getIdParamApplic(), "TIPO_OGGETTO", null, null, idTipoObject);
            if (valoreParamApplicVers != null) {
                paramApplicRowBean.setString(nomeCampoVers, valoreParamApplicVers.getDsValoreParamApplic());
                paramApplicRowBean.setBigDecimal("id_valore_param_applic",
                        BigDecimal.valueOf(valoreParamApplicVers.getIdValoreParamApplic()));
            }
        }
    }

    /**
     * Rimuove la riga di parametri definita nel rowBean
     *
     * @param row
     *            il parametro da eliminare
     *
     * @return true se eliminato con successo
     */
    public boolean deletePigParamApplicRowBean(PigParamApplicRowBean row) {
        PigParamApplic config;
        boolean result = false;
        try {
            config = amministrazioneHelper.findById(PigParamApplic.class, row.getIdParamApplic().longValue());
            // Rimuovo il record
            amministrazioneHelper.getEntityManager().remove(config);
            amministrazioneHelper.getEntityManager().flush();
            result = true;
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
        return result;
    }

    public BaseTable getUsrVAbilAmbEnteConvenzTableBean(BigDecimal idUserIam) {
        BaseTable abilAmbEnteConvenzTableBean = new BaseTable();
        List<UsrVAbilAmbEnteConvenz> abilAmbEnteConvenzList = amministrazioneHelper
                .retrieveAmbientiEntiConvenzAbilitati(idUserIam);
        if (!abilAmbEnteConvenzList.isEmpty()) {
            try {
                for (UsrVAbilAmbEnteConvenz abilAmbEnteConvenz : abilAmbEnteConvenzList) {
                    BaseRow riga = new BaseRow();
                    riga.setBigDecimal("id_ambiente_ente_convenz",
                            abilAmbEnteConvenz.getUsrVAbilAmbEnteConvenzId().getIdAmbienteEnteConvenz());
                    riga.setString("nm_ambiente_ente_convenz", abilAmbEnteConvenz.getNmAmbienteEnteConvenz());
                    abilAmbEnteConvenzTableBean.add(riga);
                }
            } catch (Exception e) {
                log.error(
                        "Errore nel recupero degli ambienti enti convenzionati" + ExceptionUtils.getRootCauseMessage(e),
                        e);
                throw new IllegalStateException("Errore inatteso nel recupero degli ambienti enti convenzionati");
            }
        }
        return abilAmbEnteConvenzTableBean;
    }

    public BaseTable getEntiGestoreAbilitatiTableBean(BigDecimal idUserIamCor, BigDecimal idAmbienteEnteConvenz) {
        BaseTable ricEnteConvenzTableBean = new BaseTable();
        List<OrgVRicEnteConvenzByEsterno> ricEnteConvenzList = amministrazioneHelper
                .getOrgVRicEnteConvenzList(idUserIamCor, idAmbienteEnteConvenz, "PRODUTTORE", "0");
        try {
            for (OrgVRicEnteConvenzByEsterno ricEnteConvenz : ricEnteConvenzList) {
                BaseRow riga = new BaseRow();
                riga.setBigDecimal("id_ente_gestore", ricEnteConvenz.getIdEnteGestore());
                riga.setString("nm_ente_gestore", amministrazioneHelper
                        .findById(SIOrgEnteSiam.class, ricEnteConvenz.getIdEnteGestore()).getNmEnteSiam());
                ricEnteConvenzTableBean.add(riga);
            }
        } catch (Exception e) {
            log.error("Errore nel recupero degli enti gestori: " + ExceptionUtils.getRootCauseMessage(e), e);
            throw new IllegalStateException("Errore inatteso nel recupero degli enti gestori");
        }
        return ricEnteConvenzTableBean;
    }

    public BaseRow getSIOrgEnteSiam(BigDecimal idEnteSiam) {
        BaseRow enteSiamRowBean = new BaseRow();
        SIOrgEnteSiam enteSiam = amministrazioneHelper.findById(SIOrgEnteSiam.class, idEnteSiam);
        enteSiamRowBean.setBigDecimal("id_ente_siam", BigDecimal.valueOf(enteSiam.getIdEnteSiam()));
        enteSiamRowBean.setString("nm_ente_siam", enteSiam.getNmEnteSiam());
        return enteSiamRowBean;
    }

    public BaseTable getEntiGestoreAbilitatiGenericiTableBean(BigDecimal idUserIamCor,
            BigDecimal idAmbienteEnteConvenz) {
        BaseTable ricEnteConvenzTableBean = new BaseTable();
        List<OrgVRicEnteConvenzByEsterno> ricEnteConvenzList = amministrazioneHelper
                .getOrgVRicEnteConvenzList(idUserIamCor, idAmbienteEnteConvenz, "0");
        try {
            for (OrgVRicEnteConvenzByEsterno ricEnteConvenz : ricEnteConvenzList) {
                BaseRow riga = new BaseRow();
                riga.setBigDecimal("id_ente_convenz",
                        ricEnteConvenz.getOrgVRicEnteConvenzByEsternoId().getIdEnteConvenz());
                riga.setString("nm_ente_convenz", ricEnteConvenz.getNmEnteConvenz());
                ricEnteConvenzTableBean.add(riga);
            }
        } catch (Exception e) {
            log.error("Errore nel recupero degli enti convenzionati: " + ExceptionUtils.getRootCauseMessage(e), e);
            throw new IllegalStateException("Errore inatteso nel recupero degli enti convenzionati");
        }
        return ricEnteConvenzTableBean;
    }

    public BaseRow getEnteConservatore(BigDecimal idEnteSiamGestore) {
        BaseRow riga = null;
        // Ricerco l’ente convenzionato avente id_ente_siam = id_ente_siam scelto come gestore
        // e da esso ricerco l'accordo valido alla data. Una volta trovato l'accordo, ricavo l'ente convenz conserv
        SIOrgEnteSiam enteConvenzConserv = amministrazioneHelper.getEnteConvenzConserv(idEnteSiamGestore);
        if (enteConvenzConserv != null) {
            riga = new BaseRow();
            riga.setBigDecimal("id_ente_siam", BigDecimal.valueOf(enteConvenzConserv.getIdEnteSiam()));
            riga.setString("nm_ente_siam", enteConvenzConserv.getNmEnteSiam());
        }
        return riga;
    }

    public BaseTable getEntiConservatori(long idUserIamCor, BigDecimal idEnteSiamGestore) {
        BaseTable tabella = new BaseTable();
        List<SIOrgEnteSiam> entiConvenzConserv = amministrazioneHelper.getEnteConvenzConservList(idUserIamCor,
                idEnteSiamGestore);
        for (SIOrgEnteSiam ente : entiConvenzConserv) {
            BaseRow riga = new BaseRow();
            riga.setBigDecimal("id_ente_siam", BigDecimal.valueOf(ente.getIdEnteSiam()));
            riga.setString("nm_ente_siam", ente.getNmEnteSiam());
            tabella.add(riga);
        }
        return tabella;
    }

    /**
     * Restituisce il rowbean contenente il dettaglio dell'accordo valido alla data corrente per l'ente convenzionato
     * dato in input
     *
     * @param idEnteConvenz
     *            l'id dell'accordo da recuperare su DB
     *
     * @return le date di interesse
     */
    public Date[] getDatePerEnteProduttore(BigDecimal idEnteConvenz) {
        Date[] datePerEnteProduttore = new Date[2];
        datePerEnteProduttore[0] = new Date();
        Calendar cal = Calendar.getInstance();
        cal.set(2444, Calendar.DECEMBER, 31, 0, 0, 0);
        datePerEnteProduttore[1] = cal.getTime();
        SIOrgAccordoEnte accordoEnte = amministrazioneHelper.retrieveOrgAccordoEnteValido(idEnteConvenz);
        if (accordoEnte != null) {
            datePerEnteProduttore[0] = accordoEnte.getDtDecAccordo();
            datePerEnteProduttore[1] = accordoEnte.getSiOrgEnteConvenz().getDtCessazione();
        }
        return datePerEnteProduttore;
    }

    /**
     * Restituisce il rowbean contenente il dettaglio dell'accordo valido alla data corrente per l'ente siam dato in
     * input
     *
     * @param idEnteSiam
     *            l'id dell'accordo da recuperare su DB
     *
     * @return le date di interesse
     */
    public Date[] getDatePerEnteFornitore(BigDecimal idEnteSiam) {
        Date[] datePerEnteFornitore = new Date[2];
        datePerEnteFornitore[0] = new Date();
        Calendar cal = Calendar.getInstance();
        cal.set(2444, Calendar.DECEMBER, 31, 0, 0, 0);
        datePerEnteFornitore[1] = cal.getTime();
        SIOrgEnteSiam enteSiam = amministrazioneHelper.findById(SIOrgEnteSiam.class, idEnteSiam);
        if (enteSiam != null) {
            datePerEnteFornitore[0] = enteSiam.getDtIniVal();
            datePerEnteFornitore[1] = enteSiam.getDtCessazione();
        }
        return datePerEnteFornitore;
    }

    public BigDecimal getIdAmbienteEnteConvenz(BigDecimal idEnteConvenz) {
        SIOrgAmbienteEnteConvenz ambienteEnteConvenz = amministrazioneHelper
                .getSIOrgAmbienteEnteConvenzByEnteConvenz(idEnteConvenz);
        return BigDecimal.valueOf(ambienteEnteConvenz.getIdAmbienteEnteConvenz());
    }

    public SIOrgEnteConvenzOrgTableBean getSIOrgEnteConvenzOrgTableBean(BigDecimal idVers) {
        SIOrgEnteConvenzOrgTableBean siEnteConvenzTableBean = new SIOrgEnteConvenzOrgTableBean();
        List<SIOrgEnteConvenzOrg> siEnteConvenzOrgList = amministrazioneHelper.retrieveSIOrgEnteConvenzOrg(idVers);
        if (!siEnteConvenzOrgList.isEmpty()) {
            try {
                for (SIOrgEnteConvenzOrg orgEnteConvenzOrg : siEnteConvenzOrgList) {
                    SIOrgEnteConvenzOrgRowBean row = new SIOrgEnteConvenzOrgRowBean();
                    row.setBigDecimal("id_ente_convenz_org", new BigDecimal(orgEnteConvenzOrg.getIdEnteConvenzOrg()));
                    if (orgEnteConvenzOrg.getSiOrgEnteConvenz().getSiOrgAmbienteEnteConvenz() != null) {
                        row.setBigDecimal("id_ambiente_ente_convenz", new BigDecimal(orgEnteConvenzOrg
                                .getSiOrgEnteConvenz().getSiOrgAmbienteEnteConvenz().getIdAmbienteEnteConvenz()));
                        row.setString("nm_ambiente_ente_convenz", orgEnteConvenzOrg.getSiOrgEnteConvenz()
                                .getSiOrgAmbienteEnteConvenz().getNmAmbienteEnteConvenz());
                    }
                    row.setBigDecimal("id_ente_siam",
                            new BigDecimal(orgEnteConvenzOrg.getSiOrgEnteConvenz().getIdEnteSiam()));
                    row.setString("nm_ente_siam", orgEnteConvenzOrg.getSiOrgEnteConvenz().getNmEnteSiam());
                    row.setTimestamp("dt_ini_val", orgEnteConvenzOrg.getDtIniVal() != null
                            ? new Timestamp(orgEnteConvenzOrg.getDtIniVal().getTime()) : null);
                    row.setTimestamp("dt_fine_val", orgEnteConvenzOrg.getDtFineVal() != null
                            ? new Timestamp(orgEnteConvenzOrg.getDtFineVal().getTime()) : null);
                    // MEV#20767 Appilicato il comportamento come in Sacer per l'ambiente
                    BigDecimal idAmbitoTerrit = orgEnteConvenzOrg.getSiOrgEnteConvenz().getIdAmbitoTerrit();
                    if (idAmbitoTerrit != null) {
                        OrgVTreeAmbitoTerrit ambitoTerrit = amministrazioneHelper
                                .findViewById(OrgVTreeAmbitoTerrit.class, idAmbitoTerrit);
                        row.setString("ds_tree_cd_ambito_territ", ambitoTerrit.getDsTreeCdAmbitoTerrit());
                    }
                    // end MEV#20767

                    siEnteConvenzTableBean.add(row);
                }
            } catch (Exception e) {
                log.error("Errore nel recupero degli enti convenzionati per il versatore : "
                        + ExceptionUtils.getRootCauseMessage(e), e);
                throw new IllegalStateException(
                        "Errore inatteso nel recupero degli enti convenzionati per il versatore");
            }
        }
        return siEnteConvenzTableBean;
    }

    /**
     * Recupera la lista dei nomi degli enti NON convenzionati cui l’utente è abilitato
     *
     * @param idUserIam
     *            id user Iam
     * @param tiEnteNonConvenz
     *            tipo ente convenzionato
     *
     * @return entity {@link OrgVRicEnteNonConvenzTableBean}
     */
    public OrgVRicEnteNonConvenzTableBean getOrgVRicEnteNonConvenzAbilTableBean(BigDecimal idUserIam,
            String tiEnteNonConvenz) {
        OrgVRicEnteNonConvenzTableBean ricEnteNonConvenzTableBean = new OrgVRicEnteNonConvenzTableBean();
        List<OrgVRicEnteNonConvenz> ricEnteNonConvenzList = amministrazioneHelper
                .retrieveEntiNonConvenzAbilitati(idUserIam, tiEnteNonConvenz);
        if (!ricEnteNonConvenzList.isEmpty()) {
            try {
                ricEnteNonConvenzTableBean = (OrgVRicEnteNonConvenzTableBean) Transform
                        .entities2TableBean(ricEnteNonConvenzList);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
        return ricEnteNonConvenzTableBean;
    }

    /**
     * Recupera la lista dei nomi degli enti convenzionati cui l’utente è abilitato
     *
     * @param idUserIamCor
     *            id user Iam
     * @param idAmbienteEnteConvenz
     *            id ambiente convenzionato
     *
     * @return entity bean {@link OrgVRicEnteConvenzByEsternoTableBean}
     */
    public OrgVRicEnteConvenzByEsternoTableBean getOrgVRicEnteConvenzAbilTableBean(BigDecimal idUserIamCor,
            BigDecimal idAmbienteEnteConvenz) {
        OrgVRicEnteConvenzByEsternoTableBean ricEnteConvenzTableBean = new OrgVRicEnteConvenzByEsternoTableBean();
        List<OrgVRicEnteConvenzByEsterno> ricEnteConvenzList = amministrazioneHelper
                .retrieveEntiConvenzAbilitatiAmbiente(idUserIamCor, idAmbienteEnteConvenz, "0");
        if (!ricEnteConvenzList.isEmpty()) {
            try {
                ricEnteConvenzTableBean = (OrgVRicEnteConvenzByEsternoTableBean) Transform
                        .entities2TableBean(ricEnteConvenzList);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
        return ricEnteConvenzTableBean;
    }

    public boolean checkRangeDateAmbiente(BigDecimal idAmbienteVers, Date dtIniValAppartAmbiente,
            Date dtFineValAppartAmbiente) {
        PigAmbienteVers ambienteVers = amministrazioneHelper.findById(PigAmbienteVers.class, idAmbienteVers);
        return ambienteVers.getDtIniVal().compareTo(dtIniValAppartAmbiente) <= 0
                && dtIniValAppartAmbiente.compareTo(ambienteVers.getDtFineVal()) <= 0
                && ambienteVers.getDtIniVal().compareTo(dtFineValAppartAmbiente) <= 0
                && dtFineValAppartAmbiente.compareTo(ambienteVers.getDtFineVal()) <= 0;
    }

    public Date[] getDateValiditaAmbiente(BigDecimal idAmbienteVers) {
        Date[] dateVal = new Date[2];
        PigAmbienteVers ambienteVers = amministrazioneHelper.findById(PigAmbienteVers.class, idAmbienteVers);
        dateVal[0] = ambienteVers.getDtIniVal();
        dateVal[1] = ambienteVers.getDtFineVal();
        return dateVal;
    }

    public boolean checkAccordoEnteGestore(BigDecimal idAmbienteVers, BigDecimal idEnteConvenz) {
        PigAmbienteVers ambienteVers = amministrazioneHelper.findById(PigAmbienteVers.class, idAmbienteVers);
        /* Controllo sull'ente convenzionato scelto */
        OrgVRicEnteConvenzByEsterno ricEnteConvenz = amministrazioneHelper.findDistinctByIdEnteConvenz(idEnteConvenz);
        /*
         * Controllo che id_ente_gestore corrisponda a quello definito sull’ambiente di appartenenza dell’ente cui la
         * struttura è legata
         */
        return ambienteVers.getIdEnteGestore().compareTo(ricEnteConvenz.getIdEnteGestore()) != 0;

    }

    public boolean checkValiditaAccordo(BigDecimal idEnteConvenz, Date dtIniVal, Date dtFineVal) {
        // Controllo periodo di validità
        return !amministrazioneHelper.checkEsistenzaPeriodoValiditaAssociazioneEnteConvenzVers(idEnteConvenz, dtIniVal,
                dtFineVal);
    }

    public boolean notExistPeriodoValiditaAssociazioneEnteConvenzVersAccordoValido(BigDecimal idEnteConvenz,
            Date dtIniVal, Date dtFineVal) {
        // Controllo periodo di validità
        return !amministrazioneHelper.existsPeriodoValiditaAssociazioneEnteConvenzVersAccordoValido(idEnteConvenz,
                dtIniVal, dtFineVal);
    }

    public boolean notExistAccordoValido(BigDecimal idEnteConvenz) {
        // Controllo periodo di validità
        return !amministrazioneHelper.existsAccordoValido(idEnteConvenz);
    }

    public SIOrgEnteConvenzOrgRowBean getSIOrgEnteConvenzOrgRowBean(BigDecimal idEnteConvenzOrg) {
        SIOrgEnteConvenzOrgRowBean siEnteConvenzRowBean = new SIOrgEnteConvenzOrgRowBean();
        SIOrgEnteConvenzOrg siEnteConvenzOrg = amministrazioneHelper.findById(SIOrgEnteConvenzOrg.class,
                idEnteConvenzOrg);
        if (siEnteConvenzOrg != null) {
            try {
                siEnteConvenzRowBean = new SIOrgEnteConvenzOrgRowBean();
                siEnteConvenzRowBean.setBigDecimal("id_ente_convenz_org",
                        new BigDecimal(siEnteConvenzOrg.getIdEnteConvenzOrg()));
                if (siEnteConvenzOrg.getSiOrgEnteConvenz().getSiOrgAmbienteEnteConvenz() != null) {
                    siEnteConvenzRowBean.setBigDecimal("id_ambiente_ente_convenz", new BigDecimal(siEnteConvenzOrg
                            .getSiOrgEnteConvenz().getSiOrgAmbienteEnteConvenz().getIdAmbienteEnteConvenz()));
                    siEnteConvenzRowBean.setString("nm_ambiente_ente_convenz", siEnteConvenzOrg.getSiOrgEnteConvenz()
                            .getSiOrgAmbienteEnteConvenz().getNmAmbienteEnteConvenz());
                }
                siEnteConvenzRowBean.setBigDecimal("id_ente_convenz",
                        new BigDecimal(siEnteConvenzOrg.getSiOrgEnteConvenz().getIdEnteSiam()));
                siEnteConvenzRowBean.setString("convenzionato",
                        siEnteConvenzOrg.getSiOrgEnteConvenz().getSiOrgAmbienteEnteConvenz() != null ? "1" : "0");
                siEnteConvenzRowBean.setString("nm_ente_convenz",
                        siEnteConvenzOrg.getSiOrgEnteConvenz().getNmEnteSiam());
                siEnteConvenzRowBean.setTimestamp("dt_ini_val", siEnteConvenzOrg.getDtIniVal() != null
                        ? new Timestamp(siEnteConvenzOrg.getDtIniVal().getTime()) : null);
                siEnteConvenzRowBean.setTimestamp("dt_fine_val", siEnteConvenzOrg.getDtFineVal() != null
                        ? new Timestamp(siEnteConvenzOrg.getDtFineVal().getTime()) : null);
            } catch (Exception e) {
                log.error("Errore nel recupero dell'associazione versatore - ente siam: "
                        + ExceptionUtils.getRootCauseMessage(e), e);
                throw new IllegalStateException("Errore inatteso nel recupero dell'associazione versatore - ente siam");
            }
        }
        return siEnteConvenzRowBean;
    }

    public BaseTable getSIOrgEnteSiamTableBean(BigDecimal idAmbienteEnteConvenz) {
        BaseTable entiTableBean = new BaseTable();
        List<SIOrgEnteSiam> listaEnti = amministrazioneHelper.retrieveSiOrgEnteConvenz(idAmbienteEnteConvenz);
        try {
            for (SIOrgEnteSiam ente : listaEnti) {
                BaseRow riga = new BaseRow();
                riga.setBigDecimal("id_ente_siam", new BigDecimal(ente.getIdEnteSiam()));
                riga.setString("nm_ente_siam", ente.getNmEnteSiam());
                entiTableBean.add(riga);
            }
        } catch (Exception e) {
            log.error("Errore nel recupero degli enti siam" + ExceptionUtils.getRootCauseMessage(e), e);
            throw new IllegalStateException("Errore inatteso nel recupero degli enti siam");
        }
        return entiTableBean;
    }

    /**
     * Inserisce una nuova associazione versatore - ente convenzionato. In caso di problemi sull'allineamento ente
     * convenzionato (metodo alignsEnteConvenzToIam) viene eseguita rollback solo per l'allineamento e non
     * sull'inserimento associazione
     *
     * @param param
     *            parametri per il logging
     * @param idVers
     *            id versamento
     * @param idEnteConvenz
     *            id ente convenzionato
     * @param dtIniVal
     *            data inizio validita
     * @param dtFineVal
     *            data fine validita
     *
     * @return pk
     *
     * @throws ParerUserError
     *             errore generico
     */
    public BigDecimal insertEnteConvenzOrg(LogParam param, BigDecimal idVers, BigDecimal idEnteConvenz, Date dtIniVal,
            Date dtFineVal) throws ParerUserError {

        IamEnteSiamDaAllinea enteSiamDaAllinea = context.getBusinessObject(AmministrazioneEjb.class)
                .saveEnteConvenzOrg(param, idVers, idEnteConvenz, dtIniVal, dtFineVal);

        if (enteSiamDaAllinea != null) {
            List<IamEnteSiamDaAllinea> enteSiamDaAllineaList = new ArrayList<>();
            enteSiamDaAllineaList.add(enteSiamDaAllinea);
            // Chiamata al WS di ALLINEA_ENTE_CONVENZIONATO DI IAM su ente dell'associazione
            alignsEnteSiamToIam(enteSiamDaAllineaList);
        }

        SIOrgEnteConvenzOrg enteSalvato = amministrazioneHelper.getSIOrgEnteConvenzOrg(idVers, idEnteConvenz, dtIniVal);
        return BigDecimal.valueOf(enteSalvato.getIdEnteConvenzOrg());
    }

    /**
     * Inserimento di una nuova associazione versatore - ente convenzionato dati i parametri in input
     *
     * @param param
     *            parametri per il logging
     * @param idVers
     *            id del versatore su cui eseguire l'associazione
     * @param idEnteConvenz
     *            id dell'ente convenzionato
     * @param dtIniVal
     *            data inizio validità
     * @param dtFineVal
     *            data fine validità
     *
     * @return entity {@link IamEnteSiamDaAllinea}
     *
     * @throws ParerUserError
     *             errore generico
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public IamEnteSiamDaAllinea saveEnteConvenzOrg(LogParam param, BigDecimal idVers, BigDecimal idEnteConvenz,
            Date dtIniVal, Date dtFineVal) throws ParerUserError {
        log.debug("Eseguo l'inserimento di una nuova associazione versatore - ente convenzionato");
        IamEnteSiamDaAllinea allinea = null;
        SIOrgEnteConvenzOrg siOrgEnteConvenzOrg;
        try {
            String nmApplic = configHelper.getParamApplicApplicationName();
            PigVers vers = amministrazioneHelper.findById(PigVers.class, idVers);
            SIOrgEnteSiam enteSiam = amministrazioneHelper.findById(SIOrgEnteSiam.class, idEnteConvenz);

            if (amministrazioneHelper.checkEsistenzaAssociazioneEnteConvenzVers(nmApplic, idVers, dtIniVal, dtFineVal,
                    null)) {
                throw new ParerUserError(
                        "Nel periodo indicato il versatore risulta gi\u00E0 associato ad un altro ente siam: impossibile eseguire la modifica");
            }

            String tipologia = enteSiam.getSiOrgAmbienteEnteConvenz() != null ? "PRODUTTORE" : "FORNITORE_ESTERNO";
            BigDecimal idAmbienteVersatore = BigDecimal.valueOf(vers.getPigAmbienteVer().getIdAmbienteVers());
            controlliAssociazioneVersatoreEnte(tipologia, idAmbienteVersatore, idEnteConvenz, idEnteConvenz, dtIniVal,
                    dtFineVal);

            SIUsrOrganizIam organiz = amministrazioneHelper.getSIUsrOrganizIam(idVers);

            // Recupero l'organizzazione su IAM, se esiste altrimenti mando in errore
            if (organiz == null) {
                throw new ParerUserError(
                        "Attenzione: il versatore non risulta replicato in IAM, è dunque impossibile procedere con l'operazione");
            }

            // Salvo l'associazione in SACER_IAM.ORG_ENTE_CONVENZ_ORG
            siOrgEnteConvenzOrg = new SIOrgEnteConvenzOrg();
            SIOrgEnteSiam enteConvenz = amministrazioneHelper.findById(SIOrgEnteSiam.class, idEnteConvenz);
            siOrgEnteConvenzOrg.setSiOrgEnteConvenz(enteConvenz);
            siOrgEnteConvenzOrg.setSiUsrOrganizIam(organiz);
            siOrgEnteConvenzOrg.setDtIniVal(dtIniVal);
            siOrgEnteConvenzOrg.setDtFineVal(dtFineVal);
            amministrazioneHelper.insertEntity(siOrgEnteConvenzOrg, false);

            // Salvo in PIG_VERS l'ente convenzionato associato se è quello più recente
            List<SIOrgEnteConvenzOrg> siEnteConvenzOrgList = amministrazioneHelper.retrieveSIOrgEnteConvenzOrg(idVers);
            SIOrgEnteSiam enteSiamOld = siEnteConvenzOrgList.get(0).getSiOrgEnteConvenz();
            if (enteSiamOld.getTiEnte().equals(TiEnte.CONVENZIONATO)) {
                vers.setIdEnteConvenz(BigDecimal.valueOf(enteSiam.getIdEnteSiam()));
                vers.setIdEnteFornitEstern(null);
            } else if (enteSiamOld.getTiEnte().equals(TiEnte.NON_CONVENZIONATO)) {
                vers.setIdEnteConvenz(null);
                vers.setIdEnteFornitEstern(BigDecimal.valueOf(enteSiam.getIdEnteSiam()));
            }
            vers.setDtIniValAppartEnteSiam(siEnteConvenzOrgList.get(0).getDtIniVal());
            vers.setDtFineValAppartEnteSiam(siEnteConvenzOrgList.get(0).getDtFineVal());
            //
            amministrazioneHelper.getEntityManager().flush();

            // Inserito per loggare la foto del versatore modificato
            sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                    param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_VERSATORE, idVers, param.getNomePagina());
            // Salvo il record dell'associazione da salvare in IAM
            allinea = insertIamEnteSiamDaAllinea(BigDecimal.valueOf(enteConvenz.getIdEnteSiam()),
                    enteConvenz.getNmEnteSiam());
        } catch (ParerUserError ex) {
            throw ex;
        } catch (Exception e) {
            String messaggio = "Eccezione imprevista nel salvataggio dell'ente convenzionato associato alla struttura ";
            messaggio += ExceptionUtils.getRootCauseMessage(e);
            log.error(messaggio, e);
            throw new ParerUserError(messaggio);
        }
        return allinea;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public IamEnteSiamDaAllinea insertIamEnteSiamDaAllinea(BigDecimal idEnteConvenz, String nmEnteConvenz) {
        IamEnteSiamDaAllinea allinea = new IamEnteSiamDaAllinea();
        allinea.setIdEnteSiam(idEnteConvenz);
        allinea.setNmEnteSiam(nmEnteConvenz);
        allinea.setTiOperAllinea("ALLINEA");
        allinea.setTiStatoAllinea("DA_ALLINEARE");
        allinea.setDtLogEnteSiamDaAllinea(new Date());
        amministrazioneHelper.insertEntity(allinea, true);
        return allinea;
    }

    /**
     * Esegue il metodo dell'ejb per la chiamata al WS di allineamento enti convenzionati
     *
     * @param <T>
     *            tipo generico
     * @param enteSiamDaAllineaList
     *            lista elementi di tipo {@link IamEnteSiamDaAllinea}
     *
     * @throws ParerUserError
     *             errore generico
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public <T extends Serializable> void alignsEnteSiamToIam(List<IamEnteSiamDaAllinea> enteSiamDaAllineaList)
            throws ParerUserError {
        try {
            aecEjb.allineaEntiConvenzionati(enteSiamDaAllineaList);
        } catch (Exception ex) {
            log.error("Errore imprevisto del servizio di allineamento ente convenzionato : " + ex.getMessage(), ex);
            jobLoggerEjb.writeAtomicLog(NomiJob.ALLINEA_ENTI_CONVENZIONATI, TipiRegLogJob.ERRORE,
                    "Errore imprevisto del servizio di allineamento ente convenzionato");
            throw new ParerUserError("Errore imprevisto del servizio di allineamento ente convenzionato");
        }
    }

    /**
     * Aggiorna l'associazione struttura - ente convenzionato. In caso di problemi sull'allineamento ente convenzionato
     * (metodo alignsEnteConvenzToIam) viene eseguita rollback solo per l'allineamento e non sulla modifica associazione
     *
     * @param param
     *            parametri per il logging
     * @param idEnteConvenzOrg
     *            id ente convenzionato per organizzazione
     * @param idEnteConvenz
     *            id ente convenzionato
     * @param idVers
     *            id versamento
     * @param dtIniVal
     *            data inizio validita
     * @param dtFineVal
     *            data fine validita
     *
     * @return pk
     *
     * @throws ParerUserError
     *             errore generico
     */
    public BigDecimal updateEnteConvenzOrg(LogParam param, BigDecimal idEnteConvenzOrg, BigDecimal idEnteConvenz,
            BigDecimal idVers, Date dtIniVal, Date dtFineVal) throws ParerUserError {

        List<IamEnteSiamDaAllinea> enteSiamDaAllineaList = context.getBusinessObject(AmministrazioneEjb.class)
                .saveEnteConvenzOrg(param, idEnteConvenzOrg, idVers, idEnteConvenz, dtIniVal, dtFineVal);

        if (!enteSiamDaAllineaList.isEmpty()) {
            // Chiamata al WS di ALLINEA_ENTE_CONVENZIONATO DI IAM su ente dell'associazione
            alignsEnteSiamToIam(enteSiamDaAllineaList);
        }

        SIOrgEnteConvenzOrg enteSalvato = amministrazioneHelper.getSIOrgEnteConvenzOrg(idVers, idEnteConvenz, dtIniVal);
        return BigDecimal.valueOf(enteSalvato.getIdEnteConvenzOrg());
    }

    /**
     * Modifica di una associazione struttura - ente convenzionato dati i parametri in input
     *
     * @param param
     *            parametri per logging
     * @param idEnteConvenzOrg
     *            id dell'associazione da modificare
     * @param idEnteConvenz
     *            id dell'ente convenzionato
     * @param idVers
     *            id versamento
     * @param dtIniVal
     *            data inizio validità
     * @param dtFineVal
     *            data fine validità
     *
     * @return L'oggetto IamOrganizDaReplic con cui eseguire la replica a SacerIam
     *
     * @throws ParerUserError
     *             errore generico
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public List<IamEnteSiamDaAllinea> saveEnteConvenzOrg(LogParam param, BigDecimal idEnteConvenzOrg, BigDecimal idVers,
            BigDecimal idEnteConvenz, Date dtIniVal, Date dtFineVal) throws ParerUserError {
        log.debug("Eseguo il salvataggio dell'ente convenzionato");
        List<IamEnteSiamDaAllinea> enteSiamDaAllineaList = new ArrayList<>();
        SIOrgEnteConvenzOrg enteConvenzOrg = null;
        try {
            String nmApplic = configHelper.getParamApplicApplicationName();
            boolean gestisciOld = false;
            boolean gestisciNew = false;

            PigVers vers = amministrazioneHelper.findById(PigVers.class, idVers);

            // Recupero la "vecchia" associazione ed eventuale "nuovo" ente convenzionato associato
            enteConvenzOrg = amministrazioneHelper.findById(SIOrgEnteConvenzOrg.class, idEnteConvenzOrg);
            long idEnteConvenzOld = enteConvenzOrg.getSiOrgEnteConvenz().getIdEnteSiam();
            String nmEnteConvenzOld = enteConvenzOrg.getSiOrgEnteConvenz().getNmEnteSiam();
            Date dtIniValOld = enteConvenzOrg.getDtIniVal();
            Date dtFineValOld = enteConvenzOrg.getDtFineVal();
            if (enteConvenzOrg.getSiOrgEnteConvenz().getTiEnte().equals(TiEnte.CONVENZIONATO)) {
                gestisciOld = true;
            }

            // Controllo se si sovrappongono periodi, escludendo ovviamente quello che sto trattando
            if (amministrazioneHelper.checkEsistenzaAssociazioneEnteConvenzVers(nmApplic, idVers, dtIniVal, dtFineVal,
                    idEnteConvenzOrg)) {
                throw new ParerUserError(
                        "Nel periodo indicato il versatore risulta gi\u00E0 associato ad un altro ente convenzionato: impossibile eseguire la modifica");
            }

            SIOrgEnteSiam enteSiam = amministrazioneHelper.findById(SIOrgEnteSiam.class, idEnteConvenz);
            String tipologia = enteSiam.getSiOrgAmbienteEnteConvenz() != null ? "PRODUTTORE" : "FORNITORE_ESTERNO";
            BigDecimal idAmbienteVersatore = BigDecimal.valueOf(vers.getPigAmbienteVer().getIdAmbienteVers());
            controlliAssociazioneVersatoreEnte(tipologia, idAmbienteVersatore, idEnteConvenz, idEnteConvenz, dtIniVal,
                    dtFineVal);

            // CASO 1: ho modificato ANCHE l'ente convenzionato
            if (idEnteConvenzOld != idEnteConvenz.longValue()) {

                // Recupero l'organizzazione su IAM se esiste, altrimenti mando in errore
                SIUsrOrganizIam organiz = amministrazioneHelper.getSIUsrOrganizIam(idVers);
                if (organiz == null) {
                    throw new ParerUserError(
                            "Attenzione: il versatore non risulta replicato in IAM, è dunque impossibile procedere con l'operazione");
                }

                // Cancello l'associazione
                amministrazioneHelper.removeEntity(enteConvenzOrg, true);

                // Inserisco quella nuova
                enteConvenzOrg = new SIOrgEnteConvenzOrg();
                SIOrgEnteSiam enteSiamNew = amministrazioneHelper.findById(SIOrgEnteSiam.class, idEnteConvenz);
                enteConvenzOrg.setSiOrgEnteConvenz(enteSiamNew);
                enteConvenzOrg.setSiUsrOrganizIam(organiz);
                enteConvenzOrg.setDtIniVal(dtIniVal);
                enteConvenzOrg.setDtFineVal(dtFineVal);
                amministrazioneHelper.insertEntity(enteConvenzOrg, true);
                if (enteSiamNew.getTiEnte().equals(TiEnte.CONVENZIONATO)) {
                    gestisciNew = true;
                }

                // Salvo in PIG_VERS l'ente convenzionato associato se è quello più recente
                updatePigVersWithMostRecenteEnteConvenz(vers);

                // Salvo gli enti convenzionati interessati da allineare in IAM
                if (gestisciOld) {
                    enteSiamDaAllineaList
                            .add(insertIamEnteSiamDaAllinea(BigDecimal.valueOf(idEnteConvenzOld), nmEnteConvenzOld));
                }
                if (gestisciNew) {
                    enteSiamDaAllineaList.add(insertIamEnteSiamDaAllinea(idEnteConvenz,
                            enteConvenzOrg.getSiOrgEnteConvenz().getNmEnteSiam()));
                }

            } // CASO 2: ho modificato SOLO le date
            else if (dtIniValOld.compareTo(dtIniVal) != 0 || dtFineValOld.compareTo(dtFineVal) != 0) {
                // Modifico le date
                enteConvenzOrg.setDtIniVal(dtIniVal);
                enteConvenzOrg.setDtFineVal(dtFineVal);

                // Salvo in PIG_VERS l'ente convenzionato associato più recente
                updatePigVersWithMostRecenteEnteConvenz(vers);

                if (enteConvenzOrg.getSiOrgEnteConvenz().getTiEnte().equals(TiEnte.CONVENZIONATO)) {
                    enteSiamDaAllineaList
                            .add(insertIamEnteSiamDaAllinea(BigDecimal.valueOf(idEnteConvenzOld), nmEnteConvenzOld));
                }
            }

            amministrazioneHelper.getEntityManager().flush();

            // Inserito per loggare la foto della struttura modificata
            sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                    param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_VERSATORE, idVers, param.getNomePagina());
            return enteSiamDaAllineaList;
        } catch (ParerUserError ex) {
            throw ex;
        } catch (Exception e) {
            String messaggio = "Eccezione imprevista nel salvataggio dell'ente siam associato al versatore ";
            messaggio += ExceptionUtils.getRootCauseMessage(e);
            log.error(messaggio, e);
            throw new ParerUserError(messaggio);
        }
    }

    private void updatePigVersWithMostRecenteEnteConvenz(PigVers vers) {
        List<SIOrgEnteConvenzOrg> siEnteConvenzOrgList = amministrazioneHelper
                .retrieveSIOrgEnteConvenzOrg(BigDecimal.valueOf(vers.getIdVers()));
        SIOrgEnteSiam enteSiam = siEnteConvenzOrgList.get(0).getSiOrgEnteConvenz();
        if (enteSiam.getTiEnte().equals(TiEnte.CONVENZIONATO)) {
            vers.setIdEnteConvenz(BigDecimal.valueOf(enteSiam.getIdEnteSiam()));
            vers.setIdEnteFornitEstern(null);
        } else if (enteSiam.getTiEnte().equals(TiEnte.NON_CONVENZIONATO)) {
            vers.setIdEnteConvenz(null);
            vers.setIdEnteFornitEstern(BigDecimal.valueOf(enteSiam.getIdEnteSiam()));
        }
        vers.setDtIniValAppartEnteSiam(siEnteConvenzOrgList.get(0).getDtIniVal());
        vers.setDtFineValAppartEnteSiam(siEnteConvenzOrgList.get(0).getDtFineVal());
    }

    /**
     * Ricava il tablebean contenente gli enti convenzionati validi (ovvero esiste almeno un accordo valido alla data
     * odierna) dell'ambiente ente passato in input
     *
     * @param idUserUamCor
     *            is user Iam
     * @param idAmbienteEnteConvenz
     *            id ambiente convenzionato
     *
     * @return bean {@link BaseTable}
     */
    public BaseTable getSIOrgEnteConvenzAccordoValidoTableBean(long idUserUamCor, BigDecimal idAmbienteEnteConvenz) {
        BaseTable entiTableBean = new BaseTable();
        List<SIOrgEnteSiam> listaEnti = amministrazioneHelper.getEntiConvenzionatiAbilitati(idUserUamCor,
                idAmbienteEnteConvenz);
        try {
            for (SIOrgEnteSiam ente : listaEnti) {
                BaseRow riga = new BaseRow();
                riga.setBigDecimal("id_ente_siam", new BigDecimal(ente.getIdEnteSiam()));
                riga.setString("nm_ente_siam", ente.getNmEnteSiam());
                entiTableBean.add(riga);
            }
        } catch (Exception e) {
            log.error("Errore nel recupero degli enti siam" + ExceptionUtils.getRootCauseMessage(e), e);
            throw new IllegalStateException("Errore inatteso nel recupero degli enti siam");
        }
        return entiTableBean;
    }

    /**
     * Business method per la cancellazione di una singola associazione struttura - ente convenzionato. passata come
     * parametro. Il metodo chiama a sua volta il metodo "deleteEnteConvenzOrgTx" all'interno dello stesso contesto
     * transazionale. In caso ci siano problemi, si rilancia l'eccezione e l'annotation
     *
     * @param param
     *            parametri per il logging
     * @param idEnteConvenzOrg
     *            Interceptors sul metodo procede ad eseguire il rollback. Se invece il metodo non genera eccezioni
     *            viene chiamata l'allineamento su Iam. In questo caso, dovesse verificarsi un errore, la rollback
     *            verrebbe gestita tramite ParerUserError (del metodo alignsEnteConvenzToIam) ed essendo stato un creato
     *            un nuovo contesto transazionale (REQUIRES_NEW) la rollback avrebbe effetto solo sulla replica (non
     *            voglio rollbackare tutto...)
     *
     * @throws ParerUserError
     *             errore generico
     */
    public void deleteEnteConvenzOrg(LogParam param, BigDecimal idEnteConvenzOrg) throws ParerUserError {
        IamEnteSiamDaAllinea enteSiamDaAllinea = context.getBusinessObject(AmministrazioneEjb.class)
                .deleteEnteConvenzOrgTx(param, idEnteConvenzOrg);

        if (enteSiamDaAllinea != null) {
            List<IamEnteSiamDaAllinea> enteSiamDaAllineaList = new ArrayList<>();
            enteSiamDaAllineaList.add(enteSiamDaAllinea);
            // Chiamata al WS di ALLINEA_ENTE_CONVENZIONATO DI IAM su ente dell'associazione
            alignsEnteSiamToIam(enteSiamDaAllineaList);
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public IamEnteSiamDaAllinea deleteEnteConvenzOrgTx(LogParam param, BigDecimal idEnteConvenzOrg) {
        log.debug("Eseguo la delete dell'ente convenzionato");
        SIOrgEnteConvenzOrg siOrgEnteConvenzOrg = amministrazioneHelper.findById(SIOrgEnteConvenzOrg.class,
                idEnteConvenzOrg);
        PigVers vers = amministrazioneHelper.findById(PigVers.class,
                siOrgEnteConvenzOrg.getSiUsrOrganizIam().getIdOrganizApplic());
        BigDecimal idEnteConvenz = BigDecimal.valueOf(siOrgEnteConvenzOrg.getSiOrgEnteConvenz().getIdEnteSiam());
        String nmEnteConvenz = siOrgEnteConvenzOrg.getSiOrgEnteConvenz().getNmEnteSiam();

        // Cancello l'associazione
        amministrazioneHelper.removeEntity(siOrgEnteConvenzOrg, true);
        // Salvo in PIG_VERS l'ente siam associato più recente tra quelli rimasti
        updatePigVersWithMostRecenteEnteConvenz(vers);

        sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_VERSATORE,
                BigDecimal.valueOf(siOrgEnteConvenzOrg.getSiUsrOrganizIam().getIdOrganizApplic()),
                param.getNomePagina());
        return insertIamEnteSiamDaAllinea(idEnteConvenz, nmEnteConvenz);
    }

    public BaseTable getFunzioneParametriTableBean() {
        BaseTable funzioneTB = new BaseTable();
        List<String> funzioni = amministrazioneHelper.getFunzioneParametri();
        if (funzioni != null && !funzioni.isEmpty()) {
            for (String funzione : funzioni) {
                BaseRow funzioneRB = new BaseRow();
                funzioneRB.setString("funzione", funzione);
                funzioneTB.add(funzioneRB);
            }
        }
        return funzioneTB;
    }

    public String checkParametriAmmessi(String nomeCampoPart, PigParamApplicTableBean parametriAmministrazione,
            PigParamApplicTableBean parametriConservazione, PigParamApplicTableBean parametriGestione) {
        String error = "Il valore del parametro non è compreso tra i valori ammessi sul parametro";
        boolean errorFound = false;

        // Controllo valori possibili su ente
        for (PigParamApplicRowBean paramApplicRowBean : parametriAmministrazione) {
            if (paramApplicRowBean.getString("ds_lista_valori_ammessi") != null
                    && !paramApplicRowBean.getString("ds_lista_valori_ammessi").equals("")) {
                if (paramApplicRowBean.getString("ds_valore_param_applic_" + nomeCampoPart + "_amm") != null
                        && !paramApplicRowBean.getString("ds_valore_param_applic_" + nomeCampoPart + "_amm")
                                .equals("")) {
                    if (!inValoriPossibili(
                            paramApplicRowBean.getString("ds_valore_param_applic_" + nomeCampoPart + "_amm"),
                            paramApplicRowBean.getString("ds_lista_valori_ammessi"))) {
                        errorFound = true;
                    }
                }
            }
        }

        for (PigParamApplicRowBean paramApplicRowBean : parametriConservazione) {
            if (paramApplicRowBean.getString("ds_lista_valori_ammessi") != null
                    && !paramApplicRowBean.getString("ds_lista_valori_ammessi").equals("")) {
                if (paramApplicRowBean.getString("ds_valore_param_applic_" + nomeCampoPart + "_cons") != null
                        && !paramApplicRowBean.getString("ds_valore_param_applic_" + nomeCampoPart + "_cons")
                                .equals("")) {
                    if (!inValoriPossibili(
                            paramApplicRowBean.getString("ds_valore_param_applic_" + nomeCampoPart + "_cons"),
                            paramApplicRowBean.getString("ds_lista_valori_ammessi"))) {
                        errorFound = true;
                    }
                }
            }
        }

        for (PigParamApplicRowBean paramApplicRowBean : parametriGestione) {
            if (paramApplicRowBean.getString("ds_lista_valori_ammessi") != null
                    && !paramApplicRowBean.getString("ds_lista_valori_ammessi").equals("")) {
                if (paramApplicRowBean.getString("ds_valore_param_applic_" + nomeCampoPart + "_gest") != null
                        && !paramApplicRowBean.getString("ds_valore_param_applic_" + nomeCampoPart + "_gest")
                                .equals("")) {
                    if (!inValoriPossibili(
                            paramApplicRowBean.getString("ds_valore_param_applic_" + nomeCampoPart + "_gest"),
                            paramApplicRowBean.getString("ds_lista_valori_ammessi"))) {
                        errorFound = true;
                    }
                }
            }
        }

        if (errorFound) {
            return error;
        } else {
            return null;
        }
    }

    public boolean inValoriPossibili(String dsValoreParamApplicEnte, String dsListaValoriAmmessi) {
        String[] tokens = dsListaValoriAmmessi.split("\\|");
        Set<String> mySet = new HashSet<>(Arrays.asList(tokens));
        return mySet.contains(dsValoreParamApplicEnte);
    }

    public void saveParametriAmbiente(PigParamApplicTableBean parametriAmministrazioneAmbiente,
            PigParamApplicTableBean parametriConservazioneAmbiente, PigParamApplicTableBean parametriGestioneAmbiente,
            BigDecimal idAmbiente) {
        PigAmbienteVers ambiente = amministrazioneHelper.findById(PigAmbienteVers.class, idAmbiente);
        gestioneParametriAmbiente(parametriAmministrazioneAmbiente, parametriConservazioneAmbiente,
                parametriGestioneAmbiente, ambiente);
    }

    private void gestioneParametriAmbiente(PigParamApplicTableBean parametriAmministrazioneAmbiente,
            PigParamApplicTableBean parametriConservazioneAmbiente, PigParamApplicTableBean parametriGestioneAmbiente,
            PigAmbienteVers ambiente) {
        // Gestione parametri amministrazione
        manageParametriPerAmbiente(parametriAmministrazioneAmbiente, "ds_valore_param_applic_ambiente_amm", ambiente);
        // Gestione parametri conservazione
        manageParametriPerAmbiente(parametriConservazioneAmbiente, "ds_valore_param_applic_ambiente_cons", ambiente);
        // Gestione parametri gestione
        manageParametriPerAmbiente(parametriGestioneAmbiente, "ds_valore_param_applic_ambiente_gest", ambiente);
    }

    public void saveParametriVersatore(LogParam param, PigParamApplicTableBean parametriAmministrazioneVersatore,
            PigParamApplicTableBean parametriConservazioneVersatore, PigParamApplicTableBean parametriGestioneVersatore,
            BigDecimal idVers) {
        PigVers versatore = amministrazioneHelper.findById(PigVers.class, idVers);
        gestioneParametriVersatore(parametriAmministrazioneVersatore, parametriConservazioneVersatore,
                parametriGestioneVersatore, versatore);
        sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_VERSATORE, idVers, param.getNomePagina());
    }

    private void gestioneParametriVersatore(PigParamApplicTableBean parametriAmministrazioneVersatore,
            PigParamApplicTableBean parametriConservazioneVersatore, PigParamApplicTableBean parametriGestioneVersatore,
            PigVers versatore) {
        // Gestione parametri amministrazione
        manageParametriPerVers(parametriAmministrazioneVersatore, "ds_valore_param_applic_vers_amm", versatore);
        // Gestione parametri conservazione
        manageParametriPerVers(parametriConservazioneVersatore, "ds_valore_param_applic_vers_cons", versatore);
        // Gestione parametri gestione
        manageParametriPerVers(parametriGestioneVersatore, "ds_valore_param_applic_vers_gest", versatore);

        // Aggiorno il parametro del prefisso
        String prefisso = configHelper.getValoreParamApplicByIdVers(Constants.DS_PREFISSO_PATH,
                BigDecimal.valueOf(versatore.getPigAmbienteVer().getIdAmbienteVers()),
                BigDecimal.valueOf(versatore.getIdVers()));

        versatore.setDsPathInputFtp(prefisso + versatore.getNmVers() + "/INPUT_FOLDER/");
        versatore.setDsPathOutputFtp(prefisso + versatore.getNmVers() + "/OUTPUT_FOLDER/");
        versatore.setDsPathTrasf(prefisso + versatore.getNmVers() + "/TRASFORMATI/");
    }

    public void saveParametriTipoOggetto(LogParam param, PigParamApplicTableBean parametriAmministrazioneTipoOggetto,
            PigParamApplicTableBean parametriConservazioneTipoOggetto,
            PigParamApplicTableBean parametriGestioneTipoOggetto, BigDecimal idTipoObject) {
        PigTipoObject tipoOggetto = amministrazioneHelper.findById(PigTipoObject.class, idTipoObject);
        gestioneParametriTipoOggetto(parametriAmministrazioneTipoOggetto, parametriConservazioneTipoOggetto,
                parametriGestioneTipoOggetto, tipoOggetto);
        sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_TIPO_OGGETTO_VERSABILE, idTipoObject,
                param.getNomePagina());
    }

    private void gestioneParametriTipoOggetto(PigParamApplicTableBean parametriAmministrazioneTipoOggetto,
            PigParamApplicTableBean parametriConservazioneTipoOggetto,
            PigParamApplicTableBean parametriGestioneTipoOggetto, PigTipoObject tipoOggetto) {
        // Gestione parametri amministrazione
        manageParametriPerTipoOggetto(parametriAmministrazioneTipoOggetto, "ds_valore_param_applic_tipo_oggetto_amm",
                tipoOggetto);
        // Gestione parametri conservazione
        manageParametriPerTipoOggetto(parametriConservazioneTipoOggetto, "ds_valore_param_applic_tipo_oggetto_cons",
                tipoOggetto);
        // Gestione parametri gestione
        manageParametriPerTipoOggetto(parametriGestioneTipoOggetto, "ds_valore_param_applic_tipo_oggetto_gest",
                tipoOggetto);
    }

    public void controlliCorrispondenzaSacer(long idUserIamCor, BigDecimal idEnteSiamVers, BigDecimal idOrganizIam,
            String tiDichVers) throws ParerUserError {
        SIUsrOrganizIam organizIam = amministrazioneHelper.findById(SIUsrOrganizIam.class, idOrganizIam);
        List<BigDecimal> idEnteSiamCorrispList = new ArrayList<>();
        switch (tiDichVers) {
        case "AMBIENTE":
            OrgAmbiente ambiente = amministrazioneHelper.findById(OrgAmbiente.class, organizIam.getIdOrganizApplic());
            for (OrgEnte ente : ambiente.getOrgEntes()) {
                for (OrgStrut strut : ente.getOrgStruts()) {
                    if (strut.getIdEnteConvenz() != null) {
                        idEnteSiamCorrispList.add(strut.getIdEnteConvenz());
                    }
                }
            }
            break;
        case "ENTE":
            OrgEnte ente = amministrazioneHelper.findById(OrgEnte.class, organizIam.getIdOrganizApplic());
            for (OrgStrut strut : ente.getOrgStruts()) {
                if (strut.getIdEnteConvenz() != null) {
                    idEnteSiamCorrispList.add(strut.getIdEnteConvenz());
                }
            }
            break;
        case "STRUTTURA":
            OrgStrut strut = amministrazioneHelper.findById(OrgStrut.class, organizIam.getIdOrganizApplic());
            if (strut.getIdEnteConvenz() != null) {
                idEnteSiamCorrispList.add(strut.getIdEnteConvenz());
            }
            break;
        }

        for (BigDecimal idEnteSiamCorrisp : idEnteSiamCorrispList) {
            if (idEnteSiamCorrisp.compareTo(idEnteSiamVers) != 0) {
                boolean collegamento = false;
                List<BigDecimal> idEntiCollegatiList = new ArrayList<>();

                List<OrgAppartCollegEnti> listaAppartCollegEnti = amministrazioneHelper
                        .retrieveOrgAppartCollegEntiByIdEnteConvenz(idEnteSiamVers);

                for (OrgAppartCollegEnti appart : listaAppartCollegEnti) {
                    // Prendo i collegamenti
                    BigDecimal idCollegEntiConvenz = BigDecimal
                            .valueOf(appart.getOrgCollegEntiConvenz().getIdCollegEntiConvenz());
                    List<OrgAppartCollegEnti> appart2List = amministrazioneHelper
                            .retrieveOrgAppartCollegEnti(idCollegEntiConvenz);
                    for (OrgAppartCollegEnti appart2 : appart2List) {
                        SIOrgEnteSiam enteSiamColl = appart2.getOrgEnteSiam();
                        idEntiCollegatiList.add(BigDecimal.valueOf(enteSiamColl.getIdEnteSiam()));
                    }
                }

                for (BigDecimal idEnteSiamColleg : idEntiCollegatiList) {
                    if (idEnteSiamColleg.compareTo(idEnteSiamCorrisp) == 0) {
                        collegamento = true;
                    }
                }

                if (!collegamento) {
                    // ERRORE
                    throw new ParerUserError(
                            "L’ente produttore della corrispondenza selezionata non corrisponde a quello del versatore o a enti collegati al versatore");
                }
            }
        }
    }

    private boolean existsCorrispondenzeSacerTipoObj(String strXml) {
        int inizioFlag = strXml.indexOf("fl_corr_tipo_obj");
        String valore = strXml.substring(inizioFlag + 101, inizioFlag + 102);
        return valore.equals("t");
    }

    private void checkCorrispondenzaConVersatore(BigDecimal idVers, BigDecimal idOrganizIamTipoObj,
            String tiDichVersTipoObj) throws ParerUserError {
        PigDichVersSacer versSacer = amministrazioneHelper.getPigDichVersSacer(idVers, null);
        if (versSacer != null && versSacer.getTiDichVers().equals(tiDichVersTipoObj)
                && versSacer.getIdOrganizIam().compareTo(idOrganizIamTipoObj) == 0) {
            throw new ParerUserError(
                    "Sul tipo oggetto è stata indicata la stessa corrispondenza presente sul versatore");
        }
    }

    public long insertPigDichVersSacer(BigDecimal idVers, String tiDichVers, BigDecimal idOrganizIam) {
        PigDichVersSacer versSacer = new PigDichVersSacer();
        versSacer.setIdOrganizIam(idOrganizIam);
        versSacer.setPigVer(amministrazioneHelper.findById(PigVers.class, idVers));
        versSacer.setTiDichVers(tiDichVers);
        amministrazioneHelper.insertEntity(versSacer, true);
        return versSacer.getIdDichVersSacer();
    }

    /**
     *
     *
     * @param idVers
     *            id versamento
     * @param idEnteSiamNuovo
     *            id ente
     *
     * @return array con elementi di tipi {@link Date}
     */
    public Date[] getDatePerAssociazioneEnteVersatore(BigDecimal idVers, BigDecimal idEnteSiamNuovo) {
        Date[] datePerAssociazioneEnteVersatore = new Date[2];
        datePerAssociazioneEnteVersatore[0] = new Date();
        List<SIOrgEnteConvenzOrg> associazioniPrecedenti = amministrazioneHelper.getSIOrgEnteConvenzOrg(idVers);
        Date inizio = associazioniPrecedenti.get(0).getDtFineVal();
        Calendar c = Calendar.getInstance();
        c.setTime(inizio);
        c.add(Calendar.DATE, 1);
        SIOrgEnteSiam enteSiam = amministrazioneHelper.findById(SIOrgEnteSiam.class, idEnteSiamNuovo);
        datePerAssociazioneEnteVersatore[0] = c.getTime();
        datePerAssociazioneEnteVersatore[1] = enteSiam.getDtCessazione();
        return datePerAssociazioneEnteVersatore;
    }

    public void controlliAssociazioneVersatoreEnte(String tipologia, BigDecimal idAmbienteVersatore,
            BigDecimal idEnteConvenzEc, BigDecimal idEnteFornitEstern, Date dtIniValAppartEnteSiam,
            Date dtFineValAppartEnteSiam) throws ParerUserError {
        // Controlli inserimento ente convenzionato PRODUTTORE
        if (tipologia.equals("PRODUTTORE")) {
            // Controllo di aver selezionato l'ente
            if (idEnteConvenzEc == null) {
                throw new ParerUserError("Attenzione: non è stato selezionato l'ente convenzionato</br>");
            }

            // Controlli sugli accordi
            if (checkAccordoEnteGestore(idAmbienteVersatore, idEnteConvenzEc)) {
                throw new ParerUserError(
                        "Sull’accordo dell’ente convenzionato è definito un ente gestore diverso da quello definito sull’ambiente di appartenenza del versatore</br>");
            }
            // Controllo sulle date di validità
            if (notExistPeriodoValiditaAssociazioneEnteConvenzVersAccordoValido(idEnteConvenzEc, dtIniValAppartEnteSiam,
                    dtFineValAppartEnteSiam)) {
                throw new ParerUserError(
                        "L’intervallo di validità dell’accordo corrente definito sull’ente convenzionato non è incluso nelle date di inizio – fine validità indicate</br>");
            }

        } else {
            if (idEnteFornitEstern == null) {
                throw new ParerUserError("Attenzione: non è stato selezionato l'ente siam</br>");
            }
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void cessaVersatore(LogParam param, BigDecimal idVers) throws ParerUserError {
        PigVers vers = amministrazioneHelper.findById(PigVers.class, idVers);
        vers.setFlCessato("1");
        amministrazioneHelper.getEntityManager().flush();
        sacerLogEjb.log(param.getTransactionLogContext(), configHelper.getParamApplicApplicationName(),
                param.getNomeUtente(), param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_VERSATORE, idVers,
                param.getNomePagina());
    }

    public String getTipologiaEnteNonConvenz(BigDecimal idEnteSiam) {
        SIOrgEnteSiam enteSiam = amministrazioneHelper.findById(SIOrgEnteSiam.class, idEnteSiam);
        return enteSiam.getTiEnteNonConvenz().name();
    }

    // MEV 27543
    public BaseTable getPigVRicVersAmbientiIamTableBean(Long idUserIam) {
        List<String> list = amministrazioneHelper.getPigVRicVersAmbientiIam(idUserIam);
        BaseTable table = new BaseTable();
        if (!list.isEmpty()) {
            for (String nmAmbienteEnteConvenz : list) {
                if (nmAmbienteEnteConvenz != null && !nmAmbienteEnteConvenz.isEmpty()) {
                    BaseRow r = new BaseRow();
                    r.setString("nm_ambiente_ente_convenz", nmAmbienteEnteConvenz);
                    table.add(r);
                }
            }
        }
        return table;
    }

    // MEV 27543
    public BaseTable getPigVRicVersEnteIamTableBean(String nmAmbienteEnteConvenz, Long idUserIam) {
        List<String> list = amministrazioneHelper.getPigVRicVersEntiIam(nmAmbienteEnteConvenz, idUserIam);
        BaseTable table = new BaseTable();
        if (!list.isEmpty()) {
            for (String enteConvenz : list) {
                if (enteConvenz != null && !enteConvenz.isEmpty()) {
                    BaseRow r = new BaseRow();
                    r.setString("nm_ente_convenz", enteConvenz);
                    table.add(r);
                }
            }
        }
        return table;
    }

    // MEV 33041
    public String creaCartellaDaVersare(BigDecimal idVers) throws ParerUserError {
        XADiskConnection xadConn = null;

        PigVers vers = amministrazioneHelper.findById(PigVers.class, idVers);

        String prefisso = configHelper.getValoreParamApplicByIdVers(it.eng.sacerasi.common.Constants.DS_PREFISSO_PATH,
                BigDecimal.valueOf(vers.getPigAmbienteVer().getIdAmbienteVers()), BigDecimal.valueOf(vers.getIdVers()));

        try {
            File basePath = new File(configHelper.getValoreParamApplicByApplic(it.eng.xformer.common.Constants.ROOT_FTP)
                    + File.separator + prefisso + vers.getNmVers());

            xadConn = xadCf.getConnection();

            if (!xadConn.fileExists(basePath)) {
                xadConn.createFile(basePath, true);
            }

            File path = new File(basePath + "/DA_VERSARE/");
            if (!xadConn.fileExists(path)) {
                xadConn.createFile(path, true);
            }

            return prefisso + vers.getNmVers() + "/DA_VERSARE/";

        } catch (Exception ex) {
            log.error("Errore durante la creazione delle cartelle per il versatore " + vers.getNmVers() + " : "
                    + ex.getMessage());
            throw new ParerUserError("Errore tecnico nella creazione delle cartelle per il versatore.");
        } finally {
            if (xadConn != null) {
                xadConn.close();
            }
        }
    }

    //
    // MEV 32650
    /**
     * Restituisce un array di object con i tablebean dei parametri di amministrazione dell'ambiente versatore
     *
     * @param idAmbienteVers
     *            id ambiente versatore
     * @param funzione
     *            lista funzioni
     * @param filterValid
     *            visualizzare o meno i record parametri cessati
     *
     * @return il tablebean
     *
     * @throws ParerUserError
     *             errore generico
     */
    public PigParamApplicTableBean getPigParamApplicAmministrazioneAmbiente(BigDecimal idAmbienteVers,
            List<String> funzione, boolean filterValid) throws ParerUserError {
        PigParamApplicTableBean paramApplicAmministrazioneTableBean = new PigParamApplicTableBean();

        // Ricavo la lista dei parametri di amministrazione definiti per l'AMBIENTE VERSATORE
        List<PigParamApplic> paramApplicList = amministrazioneHelper.getPigParamApplicListAmbiente(funzione,
                "amministrazione", filterValid);
        if (!paramApplicList.isEmpty()) {
            try {
                // Per ogni parametro, popolo i valori su applicazione e ambiente ricavandoli da
                // PIG_VALORE_PARAM_APPLIC
                for (PigParamApplic paramApplic : paramApplicList) {
                    PigParamApplicRowBean paramApplicRowBean = (PigParamApplicRowBean) Transform
                            .entity2RowBean(paramApplic);
                    populateParamApplicAmbienteRowBean(paramApplicRowBean, idAmbienteVers,
                            paramApplic.getTiGestioneParam());
                    paramApplicAmministrazioneTableBean.add(paramApplicRowBean);
                }
            } catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException | InstantiationException
                    | NoSuchMethodException | InvocationTargetException e) {
                log.error("Errore durante il recupero dei parametri di amministrazione sull'ambiente versatore "
                        + ExceptionUtils.getRootCauseMessage(e), e);
                throw new ParerUserError(
                        "Errore durante il recupero dei parametri di amministrazione sull'ambiente versatore");
            }
        }
        return paramApplicAmministrazioneTableBean;
    }

    /**
     * Restituisce un array di object con i tablebean dei parametri di gestione dell'ambiente versatore
     *
     * @param idAmbienteVers
     *            id ambiente versatore
     * @param funzione
     *            lista funzioni
     * @param filterValid
     *            visualizzare o meno i record parametri cessati
     *
     * @return il tablebean
     *
     * @throws ParerUserError
     *             errore generico
     */
    public PigParamApplicTableBean getAplParamApplicGestioneAmbiente(BigDecimal idAmbienteVers, List<String> funzione,
            boolean filterValid) throws ParerUserError {
        PigParamApplicTableBean paramApplicGestioneTableBean = new PigParamApplicTableBean();

        // Ricavo la lista dei parametri di gestione definiti per l'AMBIENTE VERSATORE
        List<PigParamApplic> paramApplicList = amministrazioneHelper.getPigParamApplicListAmbiente(funzione, "gestione",
                filterValid);
        if (!paramApplicList.isEmpty()) {
            try {
                // Per ogni parametro, popolo i valori su applicazione e ambiente ricavandoli da
                // PIG_VALORE_PARAM_APPLIC
                for (PigParamApplic paramApplic : paramApplicList) {
                    PigParamApplicRowBean paramApplicRowBean = (PigParamApplicRowBean) Transform
                            .entity2RowBean(paramApplic);
                    populateParamApplicAmbienteRowBean(paramApplicRowBean, idAmbienteVers,
                            paramApplic.getTiGestioneParam());
                    paramApplicGestioneTableBean.add(paramApplicRowBean);
                }
            } catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException | InstantiationException
                    | NoSuchMethodException | InvocationTargetException e) {
                log.error("Errore durante il recupero dei parametri di gestione sull'ambiente versatore "
                        + ExceptionUtils.getRootCauseMessage(e), e);
                throw new ParerUserError(
                        "Errore durante il recupero dei parametri di gestione sull'ambiente versatore");
            }
        }
        return paramApplicGestioneTableBean;
    }

    /**
     * Restituisce un array di object con i tablebean dei parametri di conservazione sull'ambiente versatore
     *
     * @param idAmbienteVers
     *            id ambiente versatore
     * @param funzione
     *            lista funzioni
     * @param filterValid
     *            visualizzare o meno i record parametri cessati
     *
     * @return il tablebean
     *
     * @throws ParerUserError
     *             errore generico
     */
    public PigParamApplicTableBean getAplParamApplicConservazioneAmbiente(BigDecimal idAmbienteVers,
            List<String> funzione, boolean filterValid) throws ParerUserError {
        PigParamApplicTableBean paramApplicConservazioneTableBean = new PigParamApplicTableBean();

        // Ricavo la lista dei parametri di conservazione definiti per l'AMBIENTE VERSATORE
        List<PigParamApplic> paramApplicList = amministrazioneHelper.getPigParamApplicListAmbiente(funzione,
                "conservazione", filterValid);
        if (!paramApplicList.isEmpty()) {
            try {
                // Per ogni parametro, popolo i valori su applicazione e ambiente ricavandoli da
                // PIG_VALORE_PARAM_APPLIC
                for (PigParamApplic paramApplic : paramApplicList) {
                    PigParamApplicRowBean paramApplicRowBean = (PigParamApplicRowBean) Transform
                            .entity2RowBean(paramApplic);
                    populateParamApplicAmbienteRowBean(paramApplicRowBean, idAmbienteVers,
                            paramApplic.getTiGestioneParam());
                    paramApplicConservazioneTableBean.add(paramApplicRowBean);
                }
            } catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException | InstantiationException
                    | NoSuchMethodException | InvocationTargetException e) {
                log.error("Errore durante il recupero dei parametri di conservazione sull'ambiente versatore "
                        + ExceptionUtils.getRootCauseMessage(e), e);
                throw new ParerUserError(
                        "Errore durante il recupero dei parametri di conservazione sull'ambiente versatore");
            }
        }
        return paramApplicConservazioneTableBean;
    }

    /**
     * Restituisce un array di object con i tablebean dei parametri di amministrazione del versatore
     *
     * @param idAmbienteVers
     *            id ambiente versatore
     * @param idVers
     *            id versatore
     * @param funzione
     *            lista funzioni
     * @param filterValid
     *            visualizzare o meno i record parametri cessati
     *
     * @return il tablebean
     *
     * @throws ParerUserError
     *             errore generico
     */
    public PigParamApplicTableBean getPigParamApplicAmministrazioneVersatore(BigDecimal idAmbienteVers,
            BigDecimal idVers, List<String> funzione, boolean filterValid) throws ParerUserError {
        PigParamApplicTableBean paramApplicAmministrazioneTableBean = new PigParamApplicTableBean();

        // Ricavo la lista dei parametri di amministrazione definiti per il VERSATORE
        List<PigParamApplic> paramApplicList = amministrazioneHelper.getPigParamApplicListVers(funzione,
                "amministrazione", filterValid);
        if (!paramApplicList.isEmpty()) {
            try {
                // Per ogni parametro, popolo i valori su applicazione, ambiente e versatore ricavandoli da
                // PIG_VALORE_PARAM_APPLIC
                for (PigParamApplic paramApplic : paramApplicList) {
                    PigParamApplicRowBean paramApplicRowBean = (PigParamApplicRowBean) Transform
                            .entity2RowBean(paramApplic);
                    populateParamApplicVersatoreRowBean(paramApplicRowBean, idAmbienteVers, idVers,
                            paramApplic.getTiGestioneParam());
                    paramApplicAmministrazioneTableBean.add(paramApplicRowBean);
                }
            } catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException | InstantiationException
                    | NoSuchMethodException | InvocationTargetException e) {
                log.error("Errore durante il recupero dei parametri di amministrazione sul versatore "
                        + ExceptionUtils.getRootCauseMessage(e), e);
                throw new ParerUserError("Errore durante il recupero dei parametri di amministrazione sul versatore");
            }
        }
        return paramApplicAmministrazioneTableBean;
    }

    /**
     * Restituisce un array di object con i tablebean dei parametri di gestione del versatore
     *
     * @param idAmbienteVers
     *            id ambiente versatore
     * @param idVers
     *            id versatore
     * @param funzione
     *            lista funzioni
     * @param filterValid
     *            visualizzare o meno i record parametri cessati
     *
     * @return il tablebean
     *
     * @throws ParerUserError
     *             errore generico
     */
    public PigParamApplicTableBean getPigParamApplicGestioneVersatore(BigDecimal idAmbienteVers, BigDecimal idVers,
            List<String> funzione, boolean filterValid) throws ParerUserError {
        PigParamApplicTableBean paramApplicGestioneTableBean = new PigParamApplicTableBean();

        // Ricavo la lista dei parametri di gestione definiti per il VERSATORE
        List<PigParamApplic> paramApplicList = amministrazioneHelper.getPigParamApplicListAmbiente(funzione, "gestione",
                filterValid);
        if (!paramApplicList.isEmpty()) {
            try {
                // Per ogni parametro, popolo i valori su applicazione, ambiente e versatore ricavandoli da
                // PIG_VALORE_PARAM_APPLIC
                for (PigParamApplic paramApplic : paramApplicList) {
                    PigParamApplicRowBean paramApplicRowBean = (PigParamApplicRowBean) Transform
                            .entity2RowBean(paramApplic);
                    populateParamApplicVersatoreRowBean(paramApplicRowBean, idAmbienteVers, idVers,
                            paramApplic.getTiGestioneParam());
                    paramApplicGestioneTableBean.add(paramApplicRowBean);
                }
            } catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException | InstantiationException
                    | NoSuchMethodException | InvocationTargetException e) {
                log.error("Errore durante il recupero dei parametri di gestione sul versatore "
                        + ExceptionUtils.getRootCauseMessage(e), e);
                throw new ParerUserError("Errore durante il recupero dei parametri di gestione sul versatore");
            }
        }
        return paramApplicGestioneTableBean;
    }

    /**
     * Restituisce un array di object con i tablebean dei parametri di conservazione sul versatore
     *
     * @param idAmbienteVers
     *            id ambiente versatore
     * @param idVers
     *            id vers
     * @param funzione
     *            lista funzioni
     * @param filterValid
     *            visualizzare o meno i record parametri cessati
     *
     * @return il tablebean
     *
     * @throws ParerUserError
     *             errore generico
     */
    public PigParamApplicTableBean getPigParamApplicConservazioneVersatore(BigDecimal idAmbienteVers, BigDecimal idVers,
            List<String> funzione, boolean filterValid) throws ParerUserError {
        PigParamApplicTableBean paramApplicConservazioneTableBean = new PigParamApplicTableBean();

        // Ricavo la lista dei parametri di conservazione definiti per il VERSATORE
        List<PigParamApplic> paramApplicList = amministrazioneHelper.getPigParamApplicListVers(funzione,
                "conservazione", filterValid);
        if (!paramApplicList.isEmpty()) {
            try {
                // Per ogni parametro, popolo i valori su applicazione, ambiente e versatore ricavandoli da
                // PIG_VALORE_PARAM_APPLIC
                for (PigParamApplic paramApplic : paramApplicList) {
                    PigParamApplicRowBean paramApplicRowBean = (PigParamApplicRowBean) Transform
                            .entity2RowBean(paramApplic);
                    populateParamApplicVersatoreRowBean(paramApplicRowBean, idAmbienteVers, idVers,
                            paramApplic.getTiGestioneParam());
                    paramApplicConservazioneTableBean.add(paramApplicRowBean);
                }
            } catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException | InstantiationException
                    | NoSuchMethodException | InvocationTargetException e) {
                log.error("Errore durante il recupero dei parametri di conservazione sul versatore "
                        + ExceptionUtils.getRootCauseMessage(e), e);
                throw new ParerUserError("Errore durante il recupero dei parametri di conservazione sul versatore");
            }
        }
        return paramApplicConservazioneTableBean;//
    }

    /**
     * Restituisce un array di object con i tablebean dei parametri di amministrazione del tipo oggetto
     *
     * @param idAmbienteVers
     *            id ambiente versatore
     * @param idVers
     *            id versatore
     * @param idTipoObject
     *            id tipo oggetto
     * @param funzione
     *            lista funzioni
     * @param filterValid
     *            visualizzare o meno i record parametri cessati
     *
     * @return il tablebean
     *
     * @throws ParerUserError
     *             errore generico
     */
    public PigParamApplicTableBean getPigParamApplicAmministrazioneTipoOggetto(BigDecimal idAmbienteVers,
            BigDecimal idVers, BigDecimal idTipoObject, List<String> funzione, boolean filterValid)
            throws ParerUserError {
        PigParamApplicTableBean paramApplicAmministrazioneTableBean = new PigParamApplicTableBean();

        // Ricavo la lista dei parametri di amministrazione definiti per il TIPO OGGETTO
        List<PigParamApplic> paramApplicList = amministrazioneHelper.getPigParamApplicListTipoOggetto(funzione,
                "amministrazione", filterValid);
        if (!paramApplicList.isEmpty()) {
            try {
                // Per ogni parametro, popolo i valori su applicazione, ambiente, versatore e tipo oggetto ricavandoli
                // da
                // PIG_VALORE_PARAM_APPLIC
                for (PigParamApplic paramApplic : paramApplicList) {
                    PigParamApplicRowBean paramApplicRowBean = (PigParamApplicRowBean) Transform
                            .entity2RowBean(paramApplic);
                    populateParamApplicTipoOggettoRowBean(paramApplicRowBean, idAmbienteVers, idVers, idTipoObject,
                            paramApplic.getTiGestioneParam());
                    paramApplicAmministrazioneTableBean.add(paramApplicRowBean);
                }
            } catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException | InstantiationException
                    | NoSuchMethodException | InvocationTargetException e) {
                log.error("Errore durante il recupero dei parametri di amministrazione sul tipo oggetto "
                        + ExceptionUtils.getRootCauseMessage(e), e);
                throw new ParerUserError(
                        "Errore durante il recupero dei parametri di amministrazione sul tipo oggetto");
            }
        }
        return paramApplicAmministrazioneTableBean;
    }////

    /**
     * Restituisce un array di object con i tablebean dei parametri di gestione del tipo oggetto
     *
     * @param idAmbienteVers
     *            id ambiente versatore
     * @param idVers
     *            id versatore
     * @param idTipoObject
     *            id tipo oggetto
     * @param funzione
     *            lista funzioni
     * @param filterValid
     *            visualizzare o meno i record parametri cessati
     *
     * @return il tablebean
     *
     * @throws ParerUserError
     *             errore generico
     */
    public PigParamApplicTableBean getPigParamApplicGestioneTipoOggetto(BigDecimal idAmbienteVers, BigDecimal idVers,
            BigDecimal idTipoObject, List<String> funzione, boolean filterValid) throws ParerUserError {
        PigParamApplicTableBean paramApplicGestioneTableBean = new PigParamApplicTableBean();

        // Ricavo la lista dei parametri di gestione definiti per il TIPO OGGETTO
        List<PigParamApplic> paramApplicList = amministrazioneHelper.getPigParamApplicListTipoOggetto(funzione,
                "gestione", filterValid);
        if (!paramApplicList.isEmpty()) {
            try {
                // Per ogni parametro, popolo i valori su applicazione, ambiente, versatore e tipo oggetto ricavandoli
                // da
                // PIG_VALORE_PARAM_APPLIC
                for (PigParamApplic paramApplic : paramApplicList) {
                    PigParamApplicRowBean paramApplicRowBean = (PigParamApplicRowBean) Transform
                            .entity2RowBean(paramApplic);
                    populateParamApplicTipoOggettoRowBean(paramApplicRowBean, idAmbienteVers, idVers, idTipoObject,
                            paramApplic.getTiGestioneParam());
                    paramApplicGestioneTableBean.add(paramApplicRowBean);
                }
            } catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException | InstantiationException
                    | NoSuchMethodException | InvocationTargetException e) {
                log.error("Errore durante il recupero dei parametri di gestione sul tipo oggetto "
                        + ExceptionUtils.getRootCauseMessage(e), e);
                throw new ParerUserError("Errore durante il recupero dei parametri di gestione sul tipo oggetto");
            }
        }
        return paramApplicGestioneTableBean;
    }

    /**
     * Restituisce un array di object con i tablebean dei parametri di conservazione sul tipo oggetto
     *
     * @param idAmbienteVers
     *            id ambiente versatore
     * @param idVers
     *            id vers
     * @param idTipoObject
     *            id tipo oggetto
     * @param funzione
     *            lista funzioni
     * @param filterValid
     *            visualizzare o meno i record parametri cessati
     *
     * @return il tablebean
     *
     * @throws ParerUserError
     *             errore generico
     */
    public PigParamApplicTableBean getPigParamApplicConservazioneTipoOggetto(BigDecimal idAmbienteVers,
            BigDecimal idVers, BigDecimal idTipoObject, List<String> funzione, boolean filterValid)
            throws ParerUserError {
        PigParamApplicTableBean paramApplicConservazioneTableBean = new PigParamApplicTableBean();

        // Ricavo la lista dei parametri di conservazione definiti per il TIPO OGGETTO
        List<PigParamApplic> paramApplicList = amministrazioneHelper.getPigParamApplicListTipoOggetto(funzione,
                "conservazione", filterValid);
        if (!paramApplicList.isEmpty()) {
            try {
                // Per ogni parametro, popolo i valori su applicazione, ambiente, versatore e tipo oggetto ricavandoli
                // da
                // PIG_VALORE_PARAM_APPLIC
                for (PigParamApplic paramApplic : paramApplicList) {
                    PigParamApplicRowBean paramApplicRowBean = (PigParamApplicRowBean) Transform
                            .entity2RowBean(paramApplic);
                    populateParamApplicTipoOggettoRowBean(paramApplicRowBean, idAmbienteVers, idVers, idTipoObject,
                            paramApplic.getTiGestioneParam());
                    paramApplicConservazioneTableBean.add(paramApplicRowBean);
                }
            } catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException | InstantiationException
                    | NoSuchMethodException | InvocationTargetException e) {
                log.error("Errore durante il recupero dei parametri di conservazione sul tipo oggetto "
                        + ExceptionUtils.getRootCauseMessage(e), e);
                throw new ParerUserError("Errore durante il recupero dei parametri di conservazione sul tipo oggetto");
            }
        }
        return paramApplicConservazioneTableBean;
    }

    // MEV 33260
    public String checkTrasfForTipoObject(String versXml, MessageBox msgBox) throws Exception {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            DocumentBuilder builder = factory.newDocumentBuilder();

            // Parse the XML file
            XPath xPath = XPathFactory.newInstance().newXPath();
            Document document = builder.parse(new InputSource(new StringReader(versXml)));
            NodeList listaTipiOggetto = (NodeList) xPath
                    .compile("/fotoOggetto/recordChild[tipoRecord=\"Tipo oggetto versabile\"]/child")
                    .evaluate(document, XPathConstants.NODESET);
            for (int i = 0; i < listaTipiOggetto.getLength(); i++) {
                Element tipoOggettoElement = (Element) listaTipiOggetto.item(i);
                NodeList cdTrasfList = (NodeList) xPath.compile("./datoRecord[colonnaDato=\"cd_trasf\"]/valoreDato")
                        .evaluate(tipoOggettoElement, XPathConstants.NODESET);
                // è sempre una sola
                if (cdTrasfList.getLength() > 0) {
                    String trasfName = cdTrasfList.item(0).getTextContent();
                    if (!trasfName.equals("null")
                            && !trasformazioniHelper.isTransformationPresentByCdTrasf(trasfName)) {

                        String tipoOggettoName = ((Element) xPath
                                .compile("./keyRecord/datoKey[colonnaKey=\"nm_tipo_object\"]/valoreKey")
                                .evaluate(tipoOggettoElement, XPathConstants.NODE)).getTextContent();
                        msgBox.addWarning("Non è stato possibile creare il tipo oggetto " + tipoOggettoName
                                + ", trasformazione " + trasfName + " non trovata.");

                        tipoOggettoElement.getParentNode().removeChild(tipoOggettoElement);
                    }
                }
            }

            // Setup pretty print options
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            transformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            transformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");

            // Return pretty print xml string
            StringWriter stringWriter = new StringWriter();
            transformer.transform(new DOMSource(document), new StreamResult(stringWriter));
            return stringWriter.toString();
        } catch (Exception ex) {
            log.error("Errore parsando xml di importazione del versatore: " + ex.getMessage(), ex);
            throw new ParerUserError("Errore parsando xml di importazione del versatore: " + ex.getMessage());
        }
    }
}
