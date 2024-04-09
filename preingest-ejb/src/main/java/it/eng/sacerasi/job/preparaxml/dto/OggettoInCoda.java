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
package it.eng.sacerasi.job.preparaxml.dto;

import java.math.BigDecimal;
import java.util.ArrayList;

import it.eng.sacerasi.common.Constants;
import it.eng.sacerasi.common.Constants.TipiOggetto;
import it.eng.sacerasi.common.Constants.TipoCalcolo;
import it.eng.sacerasi.common.Constants.TipoVersamento;
import it.eng.sacerasi.entity.PigObject;
import it.eng.sacerasi.ws.dto.IRispostaWS;
import it.eng.sacerasi.ws.xml.invioAsync.ListaUnitaDocumentarieType;

/**
 *
 * @author Fioravanti_F
 */
public class OggettoInCoda implements IRispostaWS {

    private static final long serialVersionUID = 1662780183312046228L;

    public class ContUnitaDocSacer {

        private BigDecimal valore;
        private long rifIdPigContUnitaDocSacer;
        private long rifIdPigTipoObject;
        private BigDecimal annoUnitaDocSacer;

        public BigDecimal getValore() {
            return valore;
        }

        public void setValore(BigDecimal valore) {
            this.valore = valore;
        }

        public long getRifIdPigContUnitaDocSacer() {
            return rifIdPigContUnitaDocSacer;
        }

        public void setRifIdPigContUnitaDocSacer(long rifIdPigContUnitaDocSacer) {
            this.rifIdPigContUnitaDocSacer = rifIdPigContUnitaDocSacer;
        }

        public long getRifIdPigTipoObject() {
            return rifIdPigTipoObject;
        }

        public void setRifIdPigTipoObject(long rifIdPigTipoObject) {
            this.rifIdPigTipoObject = rifIdPigTipoObject;
        }

        public BigDecimal getAnnoUnitaDocSacer() {
            return annoUnitaDocSacer;
        }

        public void setAnnoUnitaDocSacer(BigDecimal annoUnitaDocSacer) {
            this.annoUnitaDocSacer = annoUnitaDocSacer;
        }
    }

    //
    private SeverityEnum severity = SeverityEnum.OK;
    private ErrorTypeEnum errorType;
    private String errorCode;
    private String errorMessage;
    //
    private ListaUnitaDocumentarieType parsedListaUnitaDoc;
    //
    private Constants.TipoVersamento tipoVersamento;
    private Constants.TipiOggetto tipoOggetto;
    private Constants.TipoCalcolo tipoCalcolo;
    private PigObject rifPigObject;
    private ArrayList<FileObjectExt> listaFileObjectExt;
    private ArrayList<UnitaDocObject> listaUnitaDocObject; // lista delle UnitaDoc che dovranno essere gestite

    private String urnDirectoryOgg;
    //
    private ContUnitaDocSacer valoreContUnitaDocSacer;

    @Override
    public SeverityEnum getSeverity() {
        return severity;
    }

    @Override
    public void setSeverity(SeverityEnum severity) {
        this.severity = severity;
    }

    @Override
    public ErrorTypeEnum getErrorType() {
        return errorType;
    }

    @Override
    public void setErrorType(ErrorTypeEnum errorType) {
        this.errorType = errorType;
    }

    @Override
    public String getErrorCode() {
        return errorCode;
    }

    @Override
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    @Override
    public String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    //
    public ListaUnitaDocumentarieType getParsedListaUnitaDoc() {
        return parsedListaUnitaDoc;
    }

    public void setParsedListaUnitaDoc(ListaUnitaDocumentarieType parsedListaUnitaDoc) {
        this.parsedListaUnitaDoc = parsedListaUnitaDoc;
    }

    public TipoVersamento getTipoVersamento() {
        return tipoVersamento;
    }

    public void setTipoVersamento(TipoVersamento tipoVersamento) {
        this.tipoVersamento = tipoVersamento;
    }

    public PigObject getRifPigObject() {
        return rifPigObject;
    }

    public TipiOggetto getTipoOggetto() {
        return tipoOggetto;
    }

    public void setTipoOggetto(TipiOggetto tipoOggetto) {
        this.tipoOggetto = tipoOggetto;
    }

    public TipoCalcolo getTipoCalcolo() {
        return tipoCalcolo;
    }

    public void setTipoCalcolo(TipoCalcolo tipoCalcolo) {
        this.tipoCalcolo = tipoCalcolo;
    }

    public void setRifPigObject(PigObject rifPigObject) {
        this.rifPigObject = rifPigObject;
    }

    public ArrayList<FileObjectExt> getListaFileObjectExt() {
        return listaFileObjectExt;
    }

    public void setListaFileObjectExt(ArrayList<FileObjectExt> listaFileObjectExt) {
        this.listaFileObjectExt = listaFileObjectExt;
    }

    public ArrayList<UnitaDocObject> getListaUnitaDocObject() {
        return listaUnitaDocObject;
    }

    public void setListaUnitaDocObject(ArrayList<UnitaDocObject> listaUnitaDocObject) {
        this.listaUnitaDocObject = listaUnitaDocObject;
    }

    public String getUrnDirectoryOgg() {
        return urnDirectoryOgg;
    }

    public void setUrnDirectoryOgg(String urnDirectoryOgg) {
        this.urnDirectoryOgg = urnDirectoryOgg;
    }

    public ContUnitaDocSacer getValoreContUnitaDocSacer() {
        return valoreContUnitaDocSacer;
    }

    public void setValoreContUnitaDocSacer(ContUnitaDocSacer valoreContUnitaDocSacer) {
        this.valoreContUnitaDocSacer = valoreContUnitaDocSacer;
    }
}
