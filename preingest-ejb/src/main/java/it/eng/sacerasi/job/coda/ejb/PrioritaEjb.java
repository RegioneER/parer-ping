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

package it.eng.sacerasi.job.coda.ejb;

import it.eng.sacerasi.aop.TransactionInterceptor;
import it.eng.sacerasi.entity.*;
import it.eng.sacerasi.job.coda.helper.CodaHelper;
import it.eng.sacerasi.web.helper.ConfigurationHelper;
import it.eng.sacerasi.web.util.Constants.ComboFlagPrioVersType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.*;
import javax.interceptor.Interceptors;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static it.eng.sacerasi.common.Constants.*;
import static it.eng.sacerasi.web.util.Utils.DATE_TIME_FORMATTER;

@Stateless(mappedName = "PrioritaEjb")
@LocalBean
@Interceptors({
	TransactionInterceptor.class })
public class PrioritaEjb {
    Logger log = LoggerFactory.getLogger(PrioritaEjb.class);
    @EJB
    private ConfigurationHelper configurationHelper;
    @EJB
    private CodaHelper codaHelper;

    /**
     * Dato un oggetto verifica se è necessario effettuare un'escalation di priorità in base al
     * tempo di permanenza nello stato corrente.
     *
     * @param object {@link PigObject} oggetto da analizzare
     */
    public void valutaEscalation(PigObject object) {
	ComboFlagPrioVersType prioritaAttuale = ComboFlagPrioVersType
		.getByString(object.getTiPrioritaVersamento());
	log.debug("Verifico se serve aumentare la priorità id_object={} priorita={}",
		object.getIdObject(), prioritaAttuale);
	// procedo solo se non sono già alla massima priorità
	if (prioritaAttuale.getNext() != null) {
	    Integer numGiorniEscalation = Integer.parseInt(
		    configurationHelper.getValoreParamApplicByTipoObj(NUM_GIORNI_ESCALATION,
			    BigDecimal.valueOf(
				    object.getPigVer().getPigAmbienteVer().getIdAmbienteVers()),
			    BigDecimal.valueOf(object.getPigVer().getIdVers()),
			    BigDecimal.valueOf(object.getPigTipoObject().getIdTipoObject())));
	    log.debug("Parametro numGiorniEscalation={} ambiente={} versatore={} tipo_object={}",
		    numGiorniEscalation, object.getPigVer().getPigAmbienteVer().getNmAmbienteVers(),
		    object.getPigVer().getNmVers(), object.getPigTipoObject().getNmTipoObject());
	    Optional<LocalDateTime> ultimaModifica = object.getPigPrioritaObjects().stream()
		    .map(PigPrioritaObject::getDtModifica).max(LocalDateTime::compareTo);
	    if (ultimaModifica.isPresent()) {
		LocalDateTime dataProssimaEscalation = ultimaModifica.get()
			.plus(numGiorniEscalation, ChronoUnit.DAYS);
		log.debug("ultima_modifica_priorita={} prossima_escalation={}", ultimaModifica,
			dataProssimaEscalation);
		if (LocalDateTime.now().isAfter(dataProssimaEscalation)) {
		    log.info(
			    "Escalation id_obbject={} num_giorni_escalation={} ultimo_aggiornamento={} priorita_attuale={} priorita_escalation={}",
			    object.getIdObject(), numGiorniEscalation,
			    ultimaModifica.get().format(DATE_TIME_FORMATTER),
			    object.getTiPrioritaVersamento(), prioritaAttuale.getNext().getValue());
		    codaHelper.updatePrioritaOggetto(object.getIdObject(),
			    prioritaAttuale.getNext().getValue(), "escalation");
		}
	    }
	}
    }
}
