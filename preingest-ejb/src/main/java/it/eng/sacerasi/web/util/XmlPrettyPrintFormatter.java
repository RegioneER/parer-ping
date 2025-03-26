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

package it.eng.sacerasi.web.util;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;
import org.w3c.dom.ls.LSSerializerFilter;
import org.w3c.dom.traversal.NodeFilter;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * @author Gilioli_P
 */
public class XmlPrettyPrintFormatter {

    Logger log = LoggerFactory.getLogger(XmlPrettyPrintFormatter.class);

    public XmlPrettyPrintFormatter() {
    }

    static LSSerializerFilter skipWhitespaceFilter = new LSSerializerFilter() {
        public short acceptNode(Node n) {
            if (n.getNodeType() == Node.TEXT_NODE) {
                return (n.getNodeValue().trim().length() == 0) ? FILTER_REJECT : FILTER_ACCEPT;
            }
            return FILTER_ACCEPT;
        }

        public int getWhatToShow() {
            return NodeFilter.SHOW_ALL;
        }

        public short startElement(Element elem) {
            return FILTER_ACCEPT;
        }
    };

    public String prettyPrintWithDOM3LS(String unformattedXml) {
        // Pretty-prints a DOM document to XML using DOM Load and Save's LSSerializer.
        // Note that the "format-pretty-print" DOM configuration parameter can only be set in JDK 1.6+.
        // unformattedXml = unformattedXml.replaceAll(" ", "");
        Document document = parseXmlFile(unformattedXml);
        if (document != null) {
            DOMImplementation domImplementation = document.getImplementation();
            if (domImplementation.hasFeature("LS", "3.0") && domImplementation.hasFeature("Core", "2.0")) {
                DOMImplementationLS domImplementationLS = (DOMImplementationLS) domImplementation.getFeature("LS",
                        "3.0");
                LSSerializer lsSerializer = domImplementationLS.createLSSerializer();
                lsSerializer.setFilter(skipWhitespaceFilter);
                DOMConfiguration domConfiguration = lsSerializer.getDomConfig();
                if (domConfiguration.canSetParameter("format-pretty-print", Boolean.TRUE)) {
                    lsSerializer.getDomConfig().setParameter("format-pretty-print", Boolean.TRUE);
                    LSOutput lsOutput = domImplementationLS.createLSOutput();
                    lsOutput.setEncoding("UTF-8");
                    StringWriter stringWriter = new StringWriter();
                    lsOutput.setCharacterStream(stringWriter);
                    lsSerializer.write(document, lsOutput);
                    return stringWriter.toString();
                } else {
                    throw new RuntimeException("DOMConfiguration 'format-pretty-print' parameter isn't settable.");
                }
            } else {
                throw new RuntimeException("DOM 3.0 LS and/or DOM 2.0 Core not supported.");
            }
        } else {
            return unformattedXml;
        }
    }

    private Document parseXmlFile(String in) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            // XXE: This is the PRIMARY defense. If DTDs (doctypes) are disallowed,
            // almost all XML entity attacks are prevented
            final String FEATURE = "http://apache.org/xml/features/disallow-doctype-decl";
            dbf.setFeature(FEATURE, true);
            dbf.setFeature("http://xml.org/sax/features/external-general-entities", false);

            dbf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            // ... and these as well, per Timothy Morgan's 2014 paper:
            // "XML Schema, DTD, and Entity Attacks" (see reference below)
            dbf.setXIncludeAware(false);
            dbf.setExpandEntityReferences(false);
            // As stated in the documentation, "Feature for Secure Processing (FSP)" is the central mechanism that will
            // help you safeguard XML processing. It instructs XML processors, such as parsers, validators,
            // and transformers, to try and process XML securely, and the FSP can be used as an alternative to
            // dbf.setExpandEntityReferences(false); to allow some safe level of Entity Expansion
            // Exists from JDK6.
            dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            // ... and, per Timothy Morgan:
            // "If for some reason support for inline DOCTYPEs are a requirement, then
            // ensure the entity settings are disabled (as shown above) and beware that SSRF
            // attacks
            // (http://cwe.mitre.org/data/definitions/918.html) and denial
            // of service attacks (such as billion laughs or decompression bombs via "jar:")
            // are a risk."
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(in));
            return db.parse(is);
        } catch (ParserConfigurationException e) {
            return null;
        } catch (SAXException e) {
            return null;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
