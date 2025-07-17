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

package it.eng.sacerasi.exception;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Un'istanza di <code>ParerInternalError</code> rappresenta un errore non codificato. Questo
 * significa che non esiste nessun repository contenente il riferimento a questo errore.
 *
 * @version 1.0, 06/03/2002
 *
 * @author Luigi Bellio
 */
public class ParerInternalError extends ParerAbstractError {

    private static final long serialVersionUID = 1L;
    public static final String INTERNAL_ERROR_ELEMENT = "INTERNAL_ERROR";
    public static final String ERROR_NATIVE_EXCEPTION = "NATIVE_EXCEPTION";
    private Exception _nativeException = null;
    private static final Logger logger = LoggerFactory.getLogger(ParerInternalError.class);

    public ParerInternalError(String severity, Exception ex) {
	super();
	init(severity, null, ex);
    } // public ParerInternalError(String severity, Exception ex)

    public ParerInternalError(Exception ex) {
	super();
	init(ParerErrorSeverity.ERROR, null, ex);
    }

    public ParerInternalError(String description) {
	super();
	init(ParerErrorSeverity.ERROR, description, null);
    }

    /**
     * Costruisce un oggetto di tipo <code>ParerInternalError</code> identificandolo tramite una
     * severity , una descrizione e un'eccezione.
     *
     * @param severity    severity dell'errore.
     * @param description descrizione dell'errore.
     * @param ex          eccezione.
     */
    public ParerInternalError(String severity, String description, Exception ex) {
	super();
	init(severity, description, ex);
    } // public ParerInternalError(String severity, String description, Exception ex)

    /**
     * Questo metodo ha il compito di inizializzare lo stato dell'oggetto,viene invocato da tutti i
     * costruttori di <code>ParerInternalError</code>.
     */
    private void init(String severity, String description, Exception ex) {
	logger.debug("ParerInternalError::init: invocato");
	setSeverity(severity);
	logger.debug("ParerInternalError::init: severity [{0}]", getSeverity());
	_nativeException = null;
	if (ex != null) {
	    _nativeException = ex;
	    StringWriter exStringWriter = new StringWriter();
	    PrintWriter exPrintWriter = new PrintWriter(exStringWriter);
	    ex.printStackTrace(exPrintWriter);
	    if (description == null)
		description = exStringWriter.toString();
	    else
		description += "\n" + exStringWriter.toString();
	} // if (ex != null)
	setDescription(description);
	logger.debug("ParerInternalError::init: description [" + getDescription() + "]");
    }

    public String getCategory() {
	return ParerErrorCategory.INTERNAL_ERROR;
    }

    /**
     * Se l'oggetto è stato costruito ricevendo come parametro un' eccezione questa viene ritornata.
     *
     * @return <code>Exception</code> parametro passato in input.
     */
    public Exception getNativeException() {
	return _nativeException;
    } // public Exception getNativeException()

} // public class ParerInternalError extends EMFAbstractError implements Serializable
