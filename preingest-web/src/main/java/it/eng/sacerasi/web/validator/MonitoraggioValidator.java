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
package it.eng.sacerasi.web.validator;

import it.eng.spagoCore.error.EMFError;
import it.eng.spagoLite.message.Message;
import it.eng.spagoLite.message.Message.MessageLevel;
import it.eng.spagoLite.message.MessageBox;
import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author Gilioli_P
 */
public class MonitoraggioValidator extends TypeValidator {

    public MonitoraggioValidator(MessageBox messageBox) {
	super(messageBox);
    }

    /**
     * Metodo di validazione della chiave unita documentaria personalizzato per le sezioni
     * Versamento Fallito e Sessione Errata
     *
     * @param registro valore registro
     * @param anno     anno
     * @param numero   numero
     *
     * @throws EMFError errore generico
     */
    public void validaChiaveUnitaDoc(String registro, BigDecimal anno, String numero)
	    throws EMFError {
	// Se almeno uno dei 3 campi è diverso da null e almeno uno è uguale a null genera errore
	// Per essere corretto o tutti sono diversi da null o tutti sono uguali a null
	if ((registro != null || anno != null || numero != null)
		&& (registro == null || anno == null || numero == null)) {
	    getMessageBox().addMessage(new Message(MessageLevel.ERR,
		    "Uno dei campi della chiave unit� documentaria non è stato impostato"));
	}
    }

    public void validaSceltaPeriodoGiornoVersamento(String periodo, Date data) {
	if (periodo != null && data != null) {
	    getMessageBox().addMessage(new Message(MessageLevel.ERR,
		    "Filtri periodo e giorno versamento entrambi valorizzati!"));
	} else if (periodo == null && data == null) {
	    getMessageBox().addMessage(new Message(MessageLevel.ERR,
		    "Attenzione: E' necessario valorizzare almeno uno tra i campi periodo e giorno versamento"));
	}
    }

    public void validaSceltaPeriodoGiornoVersamento(String periodo, Date giorno_vers_da,
	    BigDecimal ore_vers_da, BigDecimal minuti_vers_da, Date giorno_vers_a,
	    BigDecimal ore_vers_a, BigDecimal minuti_vers_a) {
	if (periodo != null
		&& (giorno_vers_da != null || ore_vers_da != null || minuti_vers_da != null
			|| giorno_vers_a != null || ore_vers_a != null || minuti_vers_a != null)) {
	    getMessageBox().addMessage(new Message(MessageLevel.ERR,
		    "Campi periodo e giorno versamento entrambi valorizzati!"));
	} else if (periodo == null && giorno_vers_da == null && ore_vers_da == null
		&& minuti_vers_da == null && giorno_vers_a == null && ore_vers_a == null
		&& minuti_vers_a == null) {
	    getMessageBox().addMessage(new Message(MessageLevel.ERR,
		    "Attenzione: E' necessario valorizzare almeno uno tra i campi periodo e giorno versamento"));
	}
    }

    public void validaSceltaCodiceGruppoErrori(String codiceErrore, String gruppoErrori) {
	if (codiceErrore != null && gruppoErrori != null) {
	    getMessageBox().addMessage(new Message(MessageLevel.ERR,
		    "Filtri codice errore e gruppo errori entrambi valorizzati!"));
	}
    }

    /**
     * Controllo di coerenza tra i flag "Verificato" e "Non risolubile" e, in caso vada considerato,
     * con lo stato NON_RISOLTO
     *
     * @param verificato    flag 1/0 (true/false)
     * @param nonRisolubile flag 1/0 (true/false)
     * @param nonRisolto    true/false
     */
    public void validaFlagVerificatoNonRisolubile(String verificato, String nonRisolubile,
	    Boolean nonRisolto) {
	if (nonRisolubile != null) {
	    if ((verificato.equals("0") && nonRisolubile.equals("1"))) {
		getMessageBox().addMessage(new Message(MessageLevel.ERR,
			"Una sessione può essere definita non risolubile o risolubile solo se è stata verificata"));
	    }
	    if (nonRisolto != null && !nonRisolto && nonRisolubile.equals("1")) {
		getMessageBox().addMessage(new Message(MessageLevel.ERR,
			"Una sessione può essere definita non risolubile solo se ha stato di risoluzione NON_RISOLTO"));
	    }
	}
    }
}
