/*
 * Engineering Ingegneria Informatica S.p.A.
 *
 * Copyright (C) 2023 Regione Emilia-Romagna
 * <p/>
 * This program is free software: you can redistribute it and/or modify it under the terms of
 * the GNU Affero General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU Affero General Public License along with this program.
 * If not, see <https://www.gnu.org/licenses/>.
 */

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.eng.sacerasi.web.validator;

import java.math.BigDecimal;
import java.util.Date;

import it.eng.spagoLite.form.base.BaseElements.Status;
import it.eng.spagoLite.message.MessageBox;

/**
 *
 * @author iacolucci_M
 */
public class AmministrazioneValidator extends TypeValidator {

    public AmministrazioneValidator(MessageBox messageBox) {
        super(messageBox);
    }

    public void validaDatiVersatoreBase(Status status, String tipologia, BigDecimal idAmbienteVers, String nmVers,
            String dsVers, Date dtIniValVers, Date dtFineValVers, Date dtIniValAmbienteVers, Date dtFineValAmbienteVers,
            String dsPathInputFtp, String dsPathOutputFtp, String dsPathTrasf) {
        if (status.equals(Status.insert)) {
            if (tipologia == null) {
                getMessageBox().addError("Errore di compilazione form: tipologia non inserita<br/>");
            }
        }
        if (idAmbienteVers == null) {
            getMessageBox().addError("Errore di compilazione form: Ambiente Versatore non inserito<br/>");
        }
        if (nmVers == null) {
            getMessageBox().addError("Errore di compilazione form: Nome Versatore non inserito<br/>");
        }
        if (dsVers == null) {
            getMessageBox().addError("Errore di compilazione form: descrizione versatore non inserito<br/>");
        }
        if (dsPathInputFtp == null) {
            getMessageBox().addError("Errore di compilazione form: path input non inserito<br/>");
        }
        if (dsPathOutputFtp == null) {
            getMessageBox().addError("Errore di compilazione form: path output non inserito<br/>");
        }
        if (dsPathTrasf == null) {
            getMessageBox().addError("Errore di compilazione form: path trasformazione non inserito<br/>");
        }
        if (dtIniValVers == null) {
            getMessageBox()
                    .addError("Errore di compilazione form: data di inizio validità versatore non inserita<br/>");
        }
        if (dtFineValVers == null) {
            getMessageBox().addError("Errore di compilazione form: data di fine validità versatore non inserita<br/>");
        }
        if (dtIniValAmbienteVers == null) {
            getMessageBox().addError(
                    "Errore di compilazione form: data di inizio validità appartenenza ambiente versatore non inserita<br/>");
        }
        if (dtFineValAmbienteVers == null) {
            getMessageBox().addError(
                    "Errore di compilazione form: data di fine validità appartenenza ambiente versatore non inserita<br/>");
        }
    }

}
