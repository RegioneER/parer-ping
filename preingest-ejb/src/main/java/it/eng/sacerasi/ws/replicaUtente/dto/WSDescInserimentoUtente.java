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

package it.eng.sacerasi.ws.replicaUtente.dto;

import it.eng.sacerasi.ws.dto.IWSDesc;
import it.eng.sacerasi.ws.util.Costanti;

/**
 *
 * @author Gilioli_P
 */
public class WSDescInserimentoUtente implements IWSDesc {

    @Override
    public String getVersione() {
        return Costanti.WS_INSERIMENTO_UTENTE_VRSN;
    }

    @Override
    public String getNomeWs() {
        return Costanti.WS_INSERIMENTO_UTENTE_ASYNC;
    }

    @Override
    public String[] getCompatibilitaWS() {
        return Costanti.WS_INSERIMENTO_UTENTE_COMP;
    }
}
