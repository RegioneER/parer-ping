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

package it.eng.sacerasi.ws.notificaTrasferimento.dto;

import it.eng.sacerasi.ws.dto.ISoapWSBase;
import it.eng.sacerasi.ws.dto.IWSDesc;
import it.eng.sacerasi.ws.dto.RispostaControlli;
import it.eng.sacerasi.ws.util.Costanti.ModificatoriWS;
import java.util.EnumSet;
import java.util.Map;

/**
 *
 * @author Bonora_L
 */
public class NotificaTrasferimentoExt implements ISoapWSBase {

    private IWSDesc descrizione;
    private NotificaTrasferimentoInput notificaTrasf;
    private boolean flAggiornaOggetto = false;
    private boolean flCancellaFile = false;
    private Long idObject;
    private Long idLastSession;
    private Long idVersatore;
    private Map<String, Long> tipoFileObjects;
    private String ftpPath;
    private String tipoObject;

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
     * @return the notificaTrasf
     */
    public NotificaTrasferimentoInput getNotificaTrasf() {
	return notificaTrasf;
    }

    /**
     * @param notificaTrasf the notificaTrasf to set
     */
    public void setNotificaTrasf(NotificaTrasferimentoInput notificaTrasf) {
	this.notificaTrasf = notificaTrasf;
    }

    /**
     * @return the flAggiornaOggetto
     */
    public boolean isFlAggiornaOggetto() {
	return flAggiornaOggetto;
    }

    /**
     * @param flAggiornaOggetto the flAggiornaOggetto to set
     */
    public void setFlAggiornaOggetto(boolean flAggiornaOggetto) {
	this.flAggiornaOggetto = flAggiornaOggetto;
    }

    /**
     * @return the flCancellaFile
     */
    public boolean isFlCancellaFile() {
	return flCancellaFile;
    }

    /**
     * @param flCancellaFile the flCancellaFile to set
     */
    public void setFlCancellaFile(boolean flCancellaFile) {
	this.flCancellaFile = flCancellaFile;
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
     * @return the tipoFileObjects
     */
    public Map<String, Long> getTipoFileObjects() {
	return tipoFileObjects;
    }

    /**
     * @param tipoFileObjects the tipoFileObjects to set
     */
    public void setTipoFileObjects(Map<String, Long> tipoFileObjects) {
	this.tipoFileObjects = tipoFileObjects;
    }

    /**
     * @return the ftpPath
     */
    public String getFtpPath() {
	return ftpPath;
    }

    /**
     * @param ftpPath the ftpPath to set
     */
    public void setFtpPath(String ftpPath) {
	this.ftpPath = ftpPath;
    }

    /**
     *
     * @return the tipoObject
     */
    public String getTipoObject() {
	return tipoObject;
    }

    /**
     *
     * @param tipoObject the tipoObject to set
     */
    public void setTipoObject(String tipoObject) {
	this.tipoObject = tipoObject;
    }

}
