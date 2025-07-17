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

package it.eng.sacerasi.ws.richiestaSopClassList.dto;

import java.util.EnumSet;
import java.util.List;

import it.eng.sacerasi.entity.PigSopClassDicom;
import it.eng.sacerasi.ws.dto.ISoapWSBase;
import it.eng.sacerasi.ws.dto.IWSDesc;
import it.eng.sacerasi.ws.dto.RispostaControlli;
import it.eng.sacerasi.ws.util.Costanti.ModificatoriWS;

/**
 *
 * @author Bonora_L
 */
public class RichiestaSopClassListExt implements ISoapWSBase {

    private IWSDesc descrizione;
    private RichiestaSopClassListInput richiestaSopClassListInput;
    private Long idVersatore;
    private List<PigSopClassDicom> listaSopClass;

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
     * @return the richiestaSopClassListInput
     */
    public RichiestaSopClassListInput getRichiestaSopClassListInput() {
	return richiestaSopClassListInput;
    }

    /**
     * @param richiestaSopClassListInput the richiestaSopClassListInput to set
     */
    public void setRichiestaSopClassListInput(
	    RichiestaSopClassListInput richiestaSopClassListInput) {
	this.richiestaSopClassListInput = richiestaSopClassListInput;
    }

    /**
     * @return the idVersatore
     */
    public Long getIdVersatore() {
	return idVersatore;
    }

    /**
     * @param idVersatore the idVersatore to set
     */
    public void setIdVersatore(Long idVersatore) {
	this.idVersatore = idVersatore;
    }

    /**
     * @return the listaSopClass
     */
    public List<PigSopClassDicom> getListaSopClass() {
	return listaSopClass;
    }

    /**
     * @param listaSopClass the listaSopClass to set
     */
    public void setListaSopClass(List<PigSopClassDicom> listaSopClass) {
	this.listaSopClass = listaSopClass;
    }

}
