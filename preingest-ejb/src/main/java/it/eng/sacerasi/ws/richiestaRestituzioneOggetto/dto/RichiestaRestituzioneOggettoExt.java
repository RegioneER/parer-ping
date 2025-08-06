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

package it.eng.sacerasi.ws.richiestaRestituzioneOggetto.dto;

import it.eng.sacerasi.common.Constants;
import it.eng.sacerasi.ws.dto.ISoapWSBase;
import it.eng.sacerasi.ws.dto.IWSDesc;
import it.eng.sacerasi.ws.dto.RispostaControlli;
import it.eng.sacerasi.ws.util.Costanti;
import java.util.Date;
import java.util.EnumSet;

/**
 *
 * @author Bonora_L
 */
public class RichiestaRestituzioneOggettoExt implements ISoapWSBase {

    private IWSDesc descrizione;
    private RichiestaRestituzioneOggettoInput richiestaRestituzioneOggettoInput;
    private Constants.StatoSessioneRecup statoSessione;
    private Long idVersatore;
    private Long idObject;
    private Date dtApertura;

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
    public EnumSet<Costanti.ModificatoriWS> getModificatoriWSCalc() {
	throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * @return the richiestaRestituzioneOggettoInput
     */
    public RichiestaRestituzioneOggettoInput getRichiestaRestituzioneOggettoInput() {
	return richiestaRestituzioneOggettoInput;
    }

    /**
     * @param richiestaRestituzioneOggettoInput the richiestaRestituzioneOggettoInput to set
     */
    public void setRichiestaRestituzioneOggettoInput(
	    RichiestaRestituzioneOggettoInput richiestaRestituzioneOggettoInput) {
	this.richiestaRestituzioneOggettoInput = richiestaRestituzioneOggettoInput;
    }

    /**
     * @return the statoSessione
     */
    public Constants.StatoSessioneRecup getStatoSessione() {
	return statoSessione;
    }

    /**
     * @param statoSessione the statoSessione to set
     */
    public void setStatoSessione(Constants.StatoSessioneRecup statoSessione) {
	this.statoSessione = statoSessione;
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
     * @return the dtApertura
     */
    public Date getDtApertura() {
	return dtApertura;
    }

    /**
     * @param dtApertura the dtApertura to set
     */
    public void setDtApertura(Date dtApertura) {
	this.dtApertura = dtApertura;
    }
}
