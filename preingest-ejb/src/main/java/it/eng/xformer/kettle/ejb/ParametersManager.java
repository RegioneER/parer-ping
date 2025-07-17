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

package it.eng.xformer.kettle.ejb;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.sacerasi.entity.XfoParamTrasf;
import it.eng.sacerasi.entity.XfoSetParamTrasf;
import it.eng.sacerasi.entity.XfoTrasf;
import it.eng.sacerasi.exception.ParerUserError;
import it.eng.sacerasi.slite.gen.tablebean.XfoParamTrasfRowBean;
import it.eng.sacerasi.slite.gen.tablebean.XfoParamTrasfTableBean;
import it.eng.sacerasi.slite.gen.tablebean.XfoSetParamTrasfRowBean;
import it.eng.sacerasi.slite.gen.tablebean.XfoSetParamTrasfTableBean;
import it.eng.sacerasi.web.util.Transform;
import it.eng.xformer.helper.TrasformazioniHelper;

/**
 * @author cek
 */
@Stateless(mappedName = "ParametersManager")
@LocalBean
@Interceptors({
	it.eng.sacerasi.aop.TransactionInterceptor.class })
public class ParametersManager {

    private final Logger logger = LoggerFactory.getLogger(ParametersManager.class);

    @EJB(mappedName = "java:app/SacerAsync-ejb/TrasformazioniHelper")
    private TrasformazioniHelper helper;

    public long insertNewParametersSet(String name, String description, String type,
	    long idParentTrasf) {

	XfoTrasf xfoTrasf = helper.findById(XfoTrasf.class, idParentTrasf);

	XfoSetParamTrasf xfoSetParamTrasf = new XfoSetParamTrasf();
	xfoSetParamTrasf.setNmSetParamTrasf(name);
	xfoSetParamTrasf.setDsSetParamTrasf(description);
	xfoSetParamTrasf.setFlSetParamArk(type);
	xfoSetParamTrasf.setXfoTrasf(xfoTrasf);

	helper.insertEntity(xfoSetParamTrasf, true);

	return xfoSetParamTrasf.getIdSetParamTrasf();
    }

    public long insertNewParameter(String name, String description, String type,
	    String defaultValue, long idParentParamsSet) {
	XfoSetParamTrasf xfoSetParamTrasf = helper.findById(XfoSetParamTrasf.class,
		idParentParamsSet);

	XfoParamTrasf xfoParamTrasf = new XfoParamTrasf();
	xfoParamTrasf.setNmParamTrasf(name);
	xfoParamTrasf.setDsParamTrasf(description);
	xfoParamTrasf.setTiParamTrasf(type);
	xfoParamTrasf.setDsValoreParam(defaultValue);
	xfoParamTrasf.setXfoSetParamTrasf(xfoSetParamTrasf);

	helper.insertEntity(xfoParamTrasf, true);

	return xfoParamTrasf.getIdParamTrasf();
    }

    public XfoSetParamTrasfTableBean searchParametersSetsByTransformation(long idTrasf)
	    throws ParerUserError {
	XfoSetParamTrasfTableBean table = new XfoSetParamTrasfTableBean();

	List<XfoSetParamTrasf> paramtersSets = helper.searchXfoSetParamTrasfbyXfoTrasf(idTrasf);
	if (paramtersSets != null && !paramtersSets.isEmpty()) {
	    try {
		table = (XfoSetParamTrasfTableBean) Transform.entities2TableBean(paramtersSets);
	    } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException
		    | IllegalAccessException | IllegalArgumentException
		    | InvocationTargetException ex) {
		logger.error("Errore durante il recupero delle trasformazioni: "
			+ ExceptionUtils.getRootCauseMessage(ex), ex);
		throw new ParerUserError("Errore durante il recupero dei parametri: "
			+ ExceptionUtils.getRootCauseMessage(ex));
	    }
	}

	return table;
    }

    public XfoSetParamTrasfRowBean getXfoSetParamTrasfRowBean(long idSetParamTrasf)
	    throws ParerUserError {
	XfoSetParamTrasfRowBean rowBean = null;

	XfoSetParamTrasf xfoSetParamTrasf = helper.findById(XfoSetParamTrasf.class,
		idSetParamTrasf);

	try {
	    rowBean = (XfoSetParamTrasfRowBean) Transform.entity2RowBean(xfoSetParamTrasf);
	} catch (ClassNotFoundException | NoSuchMethodException | InstantiationException
		| IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
	    String messaggio = "Eccezione imprevista nell recupero della trasformazione ";
	    messaggio += ExceptionUtils.getRootCauseMessage(e);
	    logger.error(messaggio, e);
	    throw new ParerUserError(messaggio);
	}

	return rowBean;
    }

    public XfoParamTrasfTableBean searchParametersBySet(long idSetParamTrasf)
	    throws ParerUserError {
	XfoParamTrasfTableBean table = new XfoParamTrasfTableBean();

	List<XfoParamTrasf> parameters = helper.searchXfoParamTrasfbySet(idSetParamTrasf);
	if (parameters != null && !parameters.isEmpty()) {
	    try {
		for (XfoParamTrasf paramTrasf : parameters) {
		    XfoParamTrasfRowBean row = (XfoParamTrasfRowBean) Transform
			    .entity2RowBean(paramTrasf);
		    if (row.getDsValoreParam() == null) {
			row.setDsValoreParam("--");
		    }

		    table.add(row);
		}
	    } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException
		    | IllegalAccessException | IllegalArgumentException
		    | InvocationTargetException ex) {
		logger.error("Errore durante il recupero dei parametri: "
			+ ExceptionUtils.getRootCauseMessage(ex), ex);
		throw new ParerUserError("Errore durante il recupero dei parametri: "
			+ ExceptionUtils.getRootCauseMessage(ex));
	    }
	}

	return table;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void updateParametersSet(long idParametersSet, String name, String description,
	    String type) throws ParerUserError {
	XfoSetParamTrasf xfoSetParamTrasf = helper.findById(XfoSetParamTrasf.class,
		idParametersSet);

	if (xfoSetParamTrasf != null) {
	    xfoSetParamTrasf.setNmSetParamTrasf(name);
	    xfoSetParamTrasf.setDsSetParamTrasf(description);
	    xfoSetParamTrasf.setFlSetParamArk(type);
	} else {
	    String messaggio = "Eccezione imprevista nell'aggiornamento del set di parametri.";
	    logger.error(messaggio);
	    throw new ParerUserError(messaggio);
	}
    }

    public void updateParameter(long idParameterTrasf, String newDescription, String newType,
	    String newValue) throws ParerUserError {
	XfoParamTrasf xfoParamTrasf = helper.findById(XfoParamTrasf.class, idParameterTrasf);
	if (xfoParamTrasf != null) {
	    xfoParamTrasf.setDsParamTrasf(newDescription);
	    xfoParamTrasf.setTiParamTrasf(newType);
	    xfoParamTrasf.setDsValoreParam(newValue);
	} else {
	    String messaggio = "Eccezione imprevista nell'aggiornamento del parametro.";
	    logger.error(messaggio);
	    throw new ParerUserError(messaggio);
	}
    }

    public void updateParameterByName(long idtrasf, String name, String newDescription,
	    String newType, String newValue) throws ParerUserError {
	XfoParamTrasf xfoParamTrasf = helper.searchXfoParamTrasfbyName(name, idtrasf);
	if (xfoParamTrasf != null) {
	    xfoParamTrasf.setDsParamTrasf(newDescription);
	    xfoParamTrasf.setTiParamTrasf(newType);
	    xfoParamTrasf.setDsValoreParam(newValue);
	} else {
	    String messaggio = "Eccezione imprevista nell'aggiornamento del parametro.";
	    logger.error(messaggio);
	    throw new ParerUserError(messaggio);
	}
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void deleteParametersSet(long idSetParameterTrasf) throws ParerUserError {
	logger.debug("Eseguo l'eliminazione del set di parametri.");
	XfoSetParamTrasf xfoSetParamTrasf = helper.findById(XfoSetParamTrasf.class,
		idSetParameterTrasf);
	helper.removeEntity(xfoSetParamTrasf, true);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void deleteParameter(long idParameterTrasf) throws ParerUserError {
	logger.debug("Eseguo l'eliminazione del set di parametri.");
	XfoParamTrasf xfoParamTrasf = helper.findById(XfoParamTrasf.class, idParameterTrasf);
	helper.removeEntity(xfoParamTrasf, true);
    }

    public boolean parametersSetExists(String parametersSetName, long idTrasf) {
	return helper.parametersSetExists(parametersSetName, idTrasf);
    }

    public boolean isParameterAssigned(String parameter, long idTrasf) {
	return helper.isParameterAssigned(parameter, idTrasf);
    }

    public XfoSetParamTrasf getParametersSet(String parametersSetName, long idTrasf) {
	return helper.getParametersSet(parametersSetName, idTrasf);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void updateParametersInParametersSets(long idTrasf,
	    Map<String, String> updatedParameters, String defaultSetName) throws ParerUserError {

	logger.debug("updateParametersInParametersSet: Aggiornamento parametri...");
	List<XfoParamTrasf> parameters = helper.gettAllXfoParamTrasfbyTrasf(idTrasf);

	// elimina i parametri non presenti nel nuovo set
	for (XfoParamTrasf param : parameters) {
	    if (!updatedParameters.containsKey(param.getNmParamTrasf())) {
		deleteParameter(param.getIdParamTrasf());
		logger.debug("updateParametersInParametersSet: rimosso parametro "
			+ param.getNmParamTrasf());
	    }
	}

	// aggiunge i parametri non presenti nel set esistente o aggiorna i valori standard se
	// diversi da quelli attuali
	for (Map.Entry<String, String> updatedParam : updatedParameters.entrySet()) {
	    if (isParameterAssigned(updatedParam.getKey(), idTrasf)) {
		updateParameterByName(idTrasf, updatedParam.getKey(), "--", "ALFANUMERICO",
			updatedParam.getValue());
		logger.debug("updateParametersInParametersSet: aggiornato parametro "
			+ updatedParam.getKey() + " con valore " + updatedParam.getValue());
	    } else {
		XfoSetParamTrasf defaultParametersSet = getParametersSet(defaultSetName, idTrasf);

		if (defaultParametersSet == null) {
		    long idParametersSet = insertNewParametersSet(defaultSetName, "--", "1",
			    idTrasf);
		    defaultParametersSet = helper.findById(XfoSetParamTrasf.class, idParametersSet);
		}

		insertNewParameter(updatedParam.getKey(), "--", "ALFANUMERICO",
			updatedParam.getValue(), defaultParametersSet.getIdSetParamTrasf());
		logger.debug("updateParametersInParametersSet: inserito parametro "
			+ updatedParam.getKey() + " con valore " + updatedParam.getValue());
	    }
	}
    }
}
