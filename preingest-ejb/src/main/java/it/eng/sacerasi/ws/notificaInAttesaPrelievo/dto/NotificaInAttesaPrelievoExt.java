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

package it.eng.sacerasi.ws.notificaInAttesaPrelievo.dto;

import it.eng.sacerasi.common.Constants;
import it.eng.sacerasi.ws.dto.ISoapWSBase;
import it.eng.sacerasi.ws.dto.IWSDesc;
import it.eng.sacerasi.ws.dto.RispostaControlli;
import it.eng.sacerasi.ws.util.Costanti;
import java.util.Date;
import java.util.EnumSet;

/**
 *
 * @author Gilioli_P
 */
public class NotificaInAttesaPrelievoExt implements ISoapWSBase {

    private IWSDesc descrizione;
    private NotificaInAttesaPrelievoInput notificaInAttesaPrelievoInput;
    private Constants.StatoSessioneRecup statoSessione;
    private Date dtApertura;
    private Date dtChiusura;
    private Long idVersatore;
    private Long idObject;
    private Long idSessioneRecup;
    private String ftpOutput;

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

    public NotificaInAttesaPrelievoInput getNotificaInAttesaPrelievoInput() {
	return notificaInAttesaPrelievoInput;
    }

    public void setNotificaInAttesaPrelievoInput(
	    NotificaInAttesaPrelievoInput notificaInAttesaPrelievoInput) {
	this.notificaInAttesaPrelievoInput = notificaInAttesaPrelievoInput;
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

    /**
     * @return the dtChiusura
     */
    public Date getDtChiusura() {
	return dtChiusura;
    }

    /**
     * @param dtChiusura the dtChiusura to set
     */
    public void setDtChiusura(Date dtChiusura) {
	this.dtChiusura = dtChiusura;
    }

    /**
     * @return the idSessioneRecup
     */
    public Long getIdSessioneRecup() {
	return idSessioneRecup;
    }

    /**
     * @param idSessioneRecup the idSessioneRecup to set
     */
    public void setIdSessioneRecup(Long idSessioneRecup) {
	this.idSessioneRecup = idSessioneRecup;
    }

    /**
     * @return the ftpOutput
     */
    public String getFtpOutput() {
	return ftpOutput;
    }

    /**
     * @param ftpOutput the ftpOutput to set
     */
    public void setFtpOutput(String ftpOutput) {
	this.ftpOutput = ftpOutput;
    }
}
