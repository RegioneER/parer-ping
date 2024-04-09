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

import java.util.ArrayList;

/**
 *
 * @author MIacolucci
 */
public class GenericDto {
    private ArrayList<String> infoMessages = new ArrayList<>();
    private ArrayList<String> warnMessages = new ArrayList<>();

    public boolean existsMessages() {
        if (infoMessages.isEmpty() && warnMessages.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    public boolean existsInfoMessages() {
        return (infoMessages.isEmpty() ? false : true);
    }

    public boolean existsWarnMessages() {
        return (warnMessages.isEmpty() ? false : true);
    }

    public ArrayList<String> getInfoMessages() {
        return infoMessages;
    }

    public ArrayList<String> getWarnMessages() {
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
