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
package it.eng.sacerasi.ws.invioOggettoAsincrono.ejb;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.MarshalException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationException;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.sacerasi.common.Constants;
import it.eng.sacerasi.messages.MessaggiWSBundle;
import it.eng.sacerasi.ws.dto.IRispostaWS;
import it.eng.sacerasi.ws.dto.IRispostaWS.SeverityEnum;
import it.eng.sacerasi.ws.dto.RispostaControlli;
import it.eng.sacerasi.ws.ejb.ControlliWS;
import it.eng.sacerasi.ws.ejb.XmlContextCache;
import it.eng.sacerasi.ws.invioOggettoAsincrono.dto.InvioOggettoAsincronoExt;
import it.eng.sacerasi.ws.invioOggettoAsincrono.dto.RispostaWSInvioOggettoAsincrono;
import it.eng.sacerasi.ws.xml.datiSpecDicom.DatiSpecificiType;
import it.eng.sacerasixml.xsd.util.Utils;

@Stateless(mappedName = "DatiSpecificiParserEjb")
@LocalBean
public class DatiSpecificiParserEjb {

    private static final Logger log = LoggerFactory.getLogger(DatiSpecificiParserEjb.class);
    // singleton ejb di gestione cache dei parser Castor
    @EJB
    private XmlContextCache xmlContextCache;
    @EJB
    private ControlliInvioOggettoAsincrono controlliInvioOggettoAsincrono;
    @EJB
    private ControlliWS controlliWS;

    @SuppressWarnings("unchecked")
    public void parseXML(InvioOggettoAsincronoExt invioOggettoAsincronoExt,
            RispostaWSInvioOggettoAsincrono rispostaWs) {
        //
        RispostaControlli rispostaControlli = new RispostaControlli();
        // l'istanza dell'unità documentaria decodificata dall'XML di versamento
        DatiSpecificiType parsedDatiSpec = null;

        if (rispostaWs.getSeverity() != IRispostaWS.SeverityEnum.ERROR) {
            try {
                Unmarshaller tmpUnmarshaller = xmlContextCache.getVersReqAsyncDSDicomCtx_DatiSpecifici()
                        .createUnmarshaller();
                tmpUnmarshaller.setSchema(xmlContextCache.getDatiSpecificiDicomSchema());
                JAXBElement<DatiSpecificiType> elemento = (JAXBElement<DatiSpecificiType>) tmpUnmarshaller
                        .unmarshal(Utils.getSaxSourceForUnmarshal(invioOggettoAsincronoExt.getDatiSpecDicom()));
                parsedDatiSpec = elemento.getValue();
            } catch (MarshalException e) {
                rispostaWs.setSeverity(IRispostaWS.SeverityEnum.ERROR);
                rispostaWs.setErrorCode(MessaggiWSBundle.PING_SENDOBJ_XML_004);
                String msg = MessaggiWSBundle.getString(MessaggiWSBundle.PING_SENDOBJ_XML_004,
                        invioOggettoAsincronoExt.getVersioneDatiSpecifici(),
                        /* String.join("\n", ExceptionUtils.getRootCauseStackTrace(e)) */"Errore in unmarshall");
                rispostaWs.setErrorMessage(msg);
                rispostaWs.getInvioOggettoAsincronoRisposta().setCdEsito(Constants.EsitoServizio.KO);
                rispostaWs.getInvioOggettoAsincronoRisposta().setCdErr(MessaggiWSBundle.PING_SENDOBJ_XML_004);
                rispostaWs.getInvioOggettoAsincronoRisposta().setDsErr(msg);
                invioOggettoAsincronoExt.setFlRegistraObject(false);
                invioOggettoAsincronoExt.setFlRegistraXMLObject(false);
                invioOggettoAsincronoExt.setFlRegistraDatiSpecDicom(false);
                log.error("Errore in unmarshall: ", e);
            } catch (ValidationException e) {
                rispostaWs.setSeverity(IRispostaWS.SeverityEnum.ERROR);
                rispostaWs.setErrorCode(MessaggiWSBundle.ERR_XML_MALFORMED);
                rispostaWs.setErrorMessage(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_XML_MALFORMED,
                        String.join("\n", ExceptionUtils.getRootCauseStackTrace(e))));
                rispostaWs.getInvioOggettoAsincronoRisposta().setCdEsito(Constants.EsitoServizio.KO);
                rispostaWs.getInvioOggettoAsincronoRisposta().setCdErr(MessaggiWSBundle.ERR_XML_MALFORMED);
                rispostaWs.getInvioOggettoAsincronoRisposta()
                        .setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_XML_MALFORMED,
                                /*
                                 * String.join("\n", ExceptionUtils.getRootCauseStackTrace(e))
                                 */"Errore: XML malformato nel blocco di dati generali."));
                invioOggettoAsincronoExt.setFlRegistraObject(false);
                invioOggettoAsincronoExt.setFlRegistraXMLObject(false);
                invioOggettoAsincronoExt.setFlRegistraDatiSpecDicom(false);
                log.error("Errore: XML malformato nel blocco di dati generali.", e);
            } catch (Exception e) {
                rispostaWs.setSeverity(IRispostaWS.SeverityEnum.ERROR);
                rispostaWs.setErrorCode(MessaggiWSBundle.PING_SENDOBJ_XML_004);
                String msg = MessaggiWSBundle.getString(MessaggiWSBundle.PING_SENDOBJ_XML_004,
                        invioOggettoAsincronoExt.getVersioneDatiSpecifici(),
                        /* String.join("\n", ExceptionUtils.getRootCauseStackTrace(e)) */"Errore applicativo generico");
                rispostaWs.setErrorMessage(msg);
                rispostaWs.getInvioOggettoAsincronoRisposta().setCdEsito(Constants.EsitoServizio.KO);
                rispostaWs.getInvioOggettoAsincronoRisposta().setCdErr(MessaggiWSBundle.PING_SENDOBJ_XML_004);
                rispostaWs.getInvioOggettoAsincronoRisposta().setDsErr(msg);
                invioOggettoAsincronoExt.setFlRegistraObject(false);
                invioOggettoAsincronoExt.setFlRegistraXMLObject(false);
                invioOggettoAsincronoExt.setFlRegistraDatiSpecDicom(false);
                log.error("Errore in unmarshall: ", e);
            }
        }

        if (rispostaWs.getSeverity() != SeverityEnum.ERROR) {
            invioOggettoAsincronoExt.setDcmDatiSpecifici(parsedDatiSpec);
            invioOggettoAsincronoExt.setDcmHashDicom(parsedDatiSpec.getDCMHash());
        }

        if (rispostaWs.getSeverity() != SeverityEnum.ERROR) {
            rispostaControlli.reset();
            rispostaControlli = controlliInvioOggettoAsincrono.verificaSopClassList(parsedDatiSpec.getSOPClassList());
            if (!rispostaControlli.isrBoolean()) {
                setRispostaWsError(invioOggettoAsincronoExt, rispostaWs, rispostaControlli);
            }
        }

        if (rispostaWs.getSeverity() != SeverityEnum.ERROR) {
            rispostaControlli.reset();
            rispostaControlli = controlliInvioOggettoAsincrono
                    .verificaModalityStudio(parsedDatiSpec.getModalityInStudyList());
            if (!rispostaControlli.isrBoolean()) {
                setRispostaWsError(invioOggettoAsincronoExt, rispostaWs, rispostaControlli);
            }
        }

        if (rispostaWs.getSeverity() != SeverityEnum.ERROR) {
            rispostaControlli.reset();
            rispostaControlli = controlliWS.verificaAlgoritmo(parsedDatiSpec.getDCMHashAlgo(),
                    MessaggiWSBundle.PING_SENDOBJ_DICOM_003);
            if (!rispostaControlli.isrBoolean()) {
                setRispostaWsError(invioOggettoAsincronoExt, rispostaWs, rispostaControlli);
            }
        }

        if (rispostaWs.getSeverity() != SeverityEnum.ERROR) {
            rispostaControlli.reset();
            rispostaControlli = controlliWS.verificaEncoding(parsedDatiSpec.getDCMHashEncoding(),
                    MessaggiWSBundle.PING_SENDOBJ_DICOM_004);
            if (!rispostaControlli.isrBoolean()) {
                setRispostaWsError(invioOggettoAsincronoExt, rispostaWs, rispostaControlli);
            }
        }

        if (rispostaWs.getSeverity() != SeverityEnum.ERROR) {
            rispostaControlli.reset();
            rispostaControlli = controlliWS.verificaAlgoritmo(parsedDatiSpec.getGLOBALHashAlgo(),
                    MessaggiWSBundle.PING_SENDOBJ_DICOM_005);
            if (!rispostaControlli.isrBoolean()) {
                setRispostaWsError(invioOggettoAsincronoExt, rispostaWs, rispostaControlli);
            }
        }

        if (rispostaWs.getSeverity() != SeverityEnum.ERROR) {
            rispostaControlli.reset();
            rispostaControlli = controlliWS.verificaEncoding(parsedDatiSpec.getGLOBALHashEncoding(),
                    MessaggiWSBundle.PING_SENDOBJ_DICOM_006);
            if (!rispostaControlli.isrBoolean()) {
                setRispostaWsError(invioOggettoAsincronoExt, rispostaWs, rispostaControlli);
            }
        }

        if (rispostaWs.getSeverity() != SeverityEnum.ERROR) {
            rispostaControlli.reset();
            rispostaControlli = controlliWS.verificaAlgoritmo(parsedDatiSpec.getFILEHashAlgo(),
                    MessaggiWSBundle.PING_SENDOBJ_DICOM_007);
            if (!rispostaControlli.isrBoolean()) {
                setRispostaWsError(invioOggettoAsincronoExt, rispostaWs, rispostaControlli);
            }
        }

        if (rispostaWs.getSeverity() != SeverityEnum.ERROR) {
            rispostaControlli.reset();
            rispostaControlli = controlliWS.verificaEncoding(parsedDatiSpec.getFILEHashEncoding(),
                    MessaggiWSBundle.PING_SENDOBJ_DICOM_008);
            if (!rispostaControlli.isrBoolean()) {
                setRispostaWsError(invioOggettoAsincronoExt, rispostaWs, rispostaControlli);
            }
        }
    }

    private void setRispostaWsError(InvioOggettoAsincronoExt invioOggettoAsincronoExt,
            RispostaWSInvioOggettoAsincrono rispostaWs, RispostaControlli rispostaControlli) {
        rispostaWs.setSeverity(SeverityEnum.ERROR);
        rispostaWs.setErrorCode(rispostaControlli.getCodErr());
        rispostaWs.setErrorMessage(rispostaControlli.getDsErr());
        rispostaWs.getInvioOggettoAsincronoRisposta().setCdEsito(Constants.EsitoServizio.KO);
        rispostaWs.getInvioOggettoAsincronoRisposta().setCdErr(rispostaControlli.getCodErr());
        rispostaWs.getInvioOggettoAsincronoRisposta().setDsErr(rispostaControlli.getDsErr());
        invioOggettoAsincronoExt.setFlRegistraObject(false);
        invioOggettoAsincronoExt.setFlRegistraXMLObject(false);
        invioOggettoAsincronoExt.setFlRegistraDatiSpecDicom(false);
    }
}
