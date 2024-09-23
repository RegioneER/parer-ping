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
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.eng.sacerasi.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GenericDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private ArrayList<String> infoMessages = new ArrayList<>();
    private ArrayList<String> warnMessages = new ArrayList<>();

    public boolean existsMessages() {
        return !infoMessages.isEmpty() || !warnMessages.isEmpty();
    }

    public boolean existsInfoMessages() {
        return !infoMessages.isEmpty();
    }

    public boolean existsWarnMessages() {
        return !warnMessages.isEmpty();
    }

    public List<String> getInfoMessages() {
        return infoMessages;
    }

    public List<String> getWarnMessages() {
        return warnMessages;
    }

    public void addInfoMessage(String message) {
        infoMessages.add(message);
    }

    public void addWarnMessage(String message) {
        warnMessages.add(message);
    }

    public String getWarnMessage() {
        if (!warnMessages.isEmpty()) {
            return warnMessages.get(0);
        } else {
            return null;
        }
    }

    public String getInfoMessage() {
        if (!infoMessages.isEmpty()) {
            return infoMessages.get(0);
        } else {
            return null;
        }
    }

}
