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

import java.io.UnsupportedEncodingException;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author sinatti_s
 */
public class BinEncUtility {

    private static final Logger logger = LoggerFactory.getLogger(BinEncUtility.class);

    public static boolean isBase64String(String utf8str) {
        return Base64.isBase64(utf8str.getBytes());
    }

    public static String encodeUTF8Base64String(byte[] barray) {
        if (barray != null && barray.length > 0) {
            try {
                return new String(Base64.encodeBase64(barray), "UTF-8");
            } catch (UnsupportedEncodingException ex) {
                logger.error("Encoding UTF-8 non supportato");
                throw new RuntimeException(ex.getMessage());
            }
        } else {
            return "";
        }
    }

    public static byte[] decodeUTF8Base64String(String utf8str) {
        try {
            return Base64.decodeBase64(utf8str.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException ex) {
            logger.error("Encoding UTF-8 non supportato");
            throw new RuntimeException(ex.getMessage());
        }
    }

    public static boolean isHexString(String utf8str) {
        return (utf8str.length() > 1 && utf8str.length() % 2 == 0) && utf8str.matches("^[0-9a-fA-F]+$");
    }

    public static String encodeUTF8HexString(byte[] barray) {
        if (barray != null && barray.length > 0) {
            return Hex.encodeHexString(barray);
        } else {
            return "";
        }
    }

    public static byte[] decodeUTF8HexString(String hexstr) {
        try {
            return Hex.decodeHex(hexstr.toCharArray());
        } catch (DecoderException ex) {
            logger.error("La stringa non rappresenta un numero in base 16");
            throw new RuntimeException(ex.getMessage());
        }
    }

}
