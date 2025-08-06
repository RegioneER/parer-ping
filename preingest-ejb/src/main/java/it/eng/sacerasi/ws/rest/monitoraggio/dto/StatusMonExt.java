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
 * To change this license header, choose License Headers in Project Properties. To change this
 * template file, choose Tools | Templates and open the template in the editor.
 */
package it.eng.sacerasi.ws.rest.monitoraggio.dto;

import it.eng.sacerasi.ws.dto.IWSDesc;
import it.eng.sacerasi.ws.dto.RispostaControlli;
import it.eng.sacerasi.ws.util.Costanti;
import it.eng.spagoLite.security.User;
import java.util.EnumSet;

/**
 *
 * @author fioravanti_f
 */
public class StatusMonExt implements IStatusMonExt {

    private User utente;
    private String loginName;
    private IWSDesc descrizione;

    public User getUtente() {
	return utente;
    }

    public void setUtente(User utente) {
	this.utente = utente;
    }

    @Override
    public String getDatiXml() {
	throw new UnsupportedOperationException("Metodo non implementato");
    }

    @Override
    public void setDatiXml(String datiXml) {
	throw new UnsupportedOperationException("Metodo non implementato");
    }

    @Override
    public IWSDesc getDescrizione() {
	return descrizione;
    }

    @Override
    public void setDescrizione(IWSDesc descrizione) {
	this.descrizione = descrizione;
    }

    @Override
    public String getLoginName() {
	return loginName;
    }

    @Override
    public void setLoginName(String loginName) {
	this.loginName = loginName;
    }

    @Override
    public String getVersioneWsChiamata() {
	throw new UnsupportedOperationException("Metodo non implementato");
    }

    @Override
    public void setVersioneWsChiamata(String versioneWsChiamata) {
	throw new UnsupportedOperationException("Metodo non implementato");
    }

    @Override
    public RispostaControlli checkVersioneRequest(String versione) {
	throw new UnsupportedOperationException("Metodo non implementato");
    }

    @Override
    public String getVersioneCalc() {
	throw new UnsupportedOperationException("Metodo non implementato");
    }

    @Override
    public EnumSet<Costanti.ModificatoriWS> getModificatoriWSCalc() {
	throw new UnsupportedOperationException("Metodo non implementato");
    }

}
