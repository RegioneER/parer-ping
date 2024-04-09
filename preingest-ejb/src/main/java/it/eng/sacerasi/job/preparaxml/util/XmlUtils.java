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

package it.eng.sacerasi.job.preparaxml.util;

import java.io.StringReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author sinatti_s
 */
public class XmlUtils {

    private XmlUtils() {
        throw new IllegalStateException("Utility class");
    }

    // #MAC #15042
    public static Charset getXmlEcondingDeclaration(String xmlSip)
            throws XMLStreamException, FactoryConfigurationError {
        XMLStreamReader xmlStreamReader = XMLInputFactory.newInstance().createXMLStreamReader(new StringReader(xmlSip));
        String encodingFromXMLDeclaration = xmlStreamReader.getCharacterEncodingScheme();
        return StringUtils.isNotBlank(encodingFromXMLDeclaration) ? Charset.forName(encodingFromXMLDeclaration)
                : StandardCharsets.UTF_8;
    }

    /**
     * Gestisce la conversione del set di caratteri non gestiti da ISO8859-1 per evitare problemi di trasmissione a
     * sacer. La conversione restituisce la relativa entità HTML.
     * 
     * @param str
     *            valore da convertire
     * 
     * @return valore convertito
     */
    // MAC #21830
    public static String convertToHTMLCodes(String str) {
        StringBuilder sb = new StringBuilder();
        int len = str.length();
        for (int i = 0; i < len; ++i) {
            char c = str.charAt(i);
            if (c > 127) {
                sb.append("&#");
                sb.append(Integer.toString(c, 10));
                sb.append(";");
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

}
