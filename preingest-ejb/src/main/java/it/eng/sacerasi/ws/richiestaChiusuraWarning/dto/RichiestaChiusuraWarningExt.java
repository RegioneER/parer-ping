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
package it.eng.sacerasi.ws.richiestaChiusuraWarning.dto;

import it.eng.sacerasi.ws.dto.ISoapWSBase;
import it.eng.sacerasi.ws.dto.IWSDesc;
import it.eng.sacerasi.ws.dto.RispostaControlli;
import it.eng.sacerasi.ws.util.Costanti.ModificatoriWS;
import java.util.EnumSet;

/**
 *
 * @author Bonora_L
 */
public class RichiestaChiusuraWarningExt implements ISoapWSBase {

    private IWSDesc descrizione;
    private RichiestaChiusuraWarningInput richiestaChiusuraWarningInput;
    private Long idObject;
    private Long idLastSession;

    @Override
    public IWSDesc getDescrizione() {
	return descrizione;
    }

    @Override
    public void setDescrizione(IWSDesc descrizione) {
	this.descrizione = descrizione;
    }

    @Override
    public RispostaControlli checkVersioneRequest(String versione) {
	throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getVersioneCalc() {
	throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public EnumSet<ModificatoriWS> getModificatoriWSCalc() {
	throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * @return the richiestaChiusuraWarningInput
     */
    public RichiestaChiusuraWarningInput getRichiestaChiusuraWarningInput() {
	return richiestaChiusuraWarningInput;
    }

    /**
     * @param richiestaChiusuraWarningInput the richiestaChiusuraWarningInput to set
     */
    public void setRichiestaChiusuraWarningInput(
	    RichiestaChiusuraWarningInput richiestaChiusuraWarningInput) {
	this.richiestaChiusuraWarningInput = richiestaChiusuraWarningInput;
    }

    /**
     * @return the idObject
     */
    public Long getIdObject() {
	return idObject;
    }

    /**
     * @param idObject the idObject to set
     */
    public void setIdObject(Long idObject) {
	this.idObject = idObject;
    }

    /**
     * @return the idLastSession
     */
    public Long getIdLastSession() {
	return idLastSession;
    }

    /**
     * @param idLastSession the idLastSession to set
     */
    public void setIdLastSession(Long idLastSession) {
	this.idLastSession = idLastSession;
    }
}
