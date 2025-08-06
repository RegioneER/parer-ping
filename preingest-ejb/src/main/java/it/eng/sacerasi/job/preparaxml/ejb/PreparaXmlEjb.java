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

/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */
package it.eng.sacerasi.job.preparaxml.ejb;

import it.eng.sacerasi.common.Constants.NomiJob;
import it.eng.sacerasi.common.Constants.TipiRegLogJob;
import it.eng.sacerasi.common.ejb.CommonDb;
import it.eng.sacerasi.entity.PigObject;
import it.eng.sacerasi.exception.ParerInternalError;
import it.eng.sacerasi.job.ejb.JobLogger;
import it.eng.sacerasi.job.preparaxml.dto.OggettoInCoda;
import it.eng.sacerasi.job.coda.ejb.PrioritaEjb;
import it.eng.sacerasi.messages.MessaggiWSBundle;
import java.math.BigDecimal;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;

import it.eng.sacerasi.ws.dto.IRispostaWS.SeverityEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import it.eng.parer.objectstorage.exceptions.ObjectStorageException;

/**
 *
 * @author Fioravanti_F
 */
@Stateless(mappedName = "PreparaXmlEjb")
@LocalBean
@Interceptors({
	it.eng.sacerasi.aop.TransactionInterceptor.class })
public class PreparaXmlEjb {

    Logger log = LoggerFactory.getLogger(PreparaXmlEjb.class);
    @EJB
    private CommonDb commonDb;
    @EJB
    private ControlliPrepXml controlli;
    @EJB
    private SalvaErrorePrepXml salvaErrore;
    @EJB
    private SalvataggioPrepXml salvataggioPrepXml;
    @EJB
    private PreparaXmlEjb me;
    @EJB
    private PreparazioneXmlEjb preparazioneXml;
    @EJB
    private ProduzioneXmlEjb produzioneXml;
    @EJB
    private PrioritaEjb prioritaEjb;

    public void preparaXml() throws ParerInternalError, ObjectStorageException {
	List<PigObject> tmpOggetti = null;
	String rootFtpValue;

	rootFtpValue = commonDb.getRootFtpParam();
	tmpOggetti = controlli.getListaObjectDaVersPostHash();
	log.info("Preparazione XML:: oggetti da processare dopo il calcolo hash: {}",
		tmpOggetti.size());

	for (PigObject tmpObject : tmpOggetti) {
	    OggettoInCoda tmpOggInCoda = new OggettoInCoda();
	    tmpOggInCoda.setRifPigObject(tmpObject);
	    me.elabora(tmpOggInCoda, rootFtpValue);
	}
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void elabora(OggettoInCoda oggetto, String rootFtpValue)
	    throws ParerInternalError, ObjectStorageException {
	preparazioneXml.prepara(oggetto, rootFtpValue);
	if (oggetto.getSeverity() != SeverityEnum.ERROR) {
	    produzioneXml.produci(oggetto);
	    salvataggioPrepXml.salvaTutto(oggetto);

	    // Controllo della valorizzazione di tutte le organizzazione a cui versare in Sacer
	    BigDecimal idObject = new BigDecimal(oggetto.getRifPigObject().getIdObject());
	    if (!controlli.checkStrutturaPigVChkOrgVersSacer(idObject)) {
		oggetto.setSeverity(SeverityEnum.ERROR);
		oggetto.setErrorCode(MessaggiWSBundle.PING_PREPXML_FILE_015);
		oggetto.setErrorMessage(
			MessaggiWSBundle.getString(MessaggiWSBundle.PING_PREPXML_FILE_015));
	    } else if (!controlli.checkSimulazionePigVChkSimulaVersSacer(idObject)) {
		oggetto.setSeverity(SeverityEnum.ERROR);
		oggetto.setErrorCode(MessaggiWSBundle.PING_PREPXML_FILE_016);
		oggetto.setErrorMessage(
			MessaggiWSBundle.getString(MessaggiWSBundle.PING_PREPXML_FILE_016));
	    }
	    if (oggetto.getSeverity() == SeverityEnum.ERROR) {
		// gestione errore a posteriori
		salvaErrore.chiudiInErrore(rootFtpValue, oggetto, false);
	    }
	} else {
	    // gestione errore
	    salvaErrore.chiudiInErrore(rootFtpValue, oggetto, false);
	}
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void gestisciAging() {
	List<PigObject> objects = controlli.getListaObjectDaVersPostHash();
	objects.stream().forEach(prioritaEjb::valutaEscalation);
    }
}
