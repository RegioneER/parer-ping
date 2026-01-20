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

package it.eng.sacerasi.job.preparaxml.dto;

import it.eng.parer.ws.xml.versReq.UnitaDocumentaria;
import it.eng.parer.ws.xml.versReqMultiMedia.IndiceMM;
import it.eng.sacerasi.common.Chiave;
import it.eng.sacerasi.common.Constants;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;

/**
 *
 * @author Fioravanti_F
 */
public class UnitaDocObject extends DocObject implements Serializable {

    private static final long serialVersionUID = 1L;
    private transient UnitaDocumentaria unitaDocumentariaXmlBean;
    private String unitaDocumentariaXml;
    private transient IndiceMM indiceMMXmlBean;
    private String indiceMMXml;
    private ArrayList<FileUnitaDoc> listaFileUnitaDoc;

    public UnitaDocumentaria getUnitaDocumentariaXmlBean() {
        return unitaDocumentariaXmlBean;
    }

    public void setUnitaDocumentariaXmlBean(UnitaDocumentaria unitaDocumentariaXmlBean) {
        this.unitaDocumentariaXmlBean = unitaDocumentariaXmlBean;
    }

    public IndiceMM getIndiceMMXmlBean() {
        return indiceMMXmlBean;
    }

    public void setIndiceMMXmlBean(IndiceMM indiceMMXmlBean) {
        this.indiceMMXmlBean = indiceMMXmlBean;
    }

    public String getIndiceMMXml() {
        return indiceMMXml;
    }

    public void setIndiceMMXml(String indiceMMXml) {
        this.indiceMMXml = indiceMMXml;
    }

    public ArrayList<FileUnitaDoc> getListaFileUnitaDoc() {
        return listaFileUnitaDoc;
    }

    public void setListaFileUnitaDoc(ArrayList<FileUnitaDoc> listaFileUnitaDoc) {
        this.listaFileUnitaDoc = listaFileUnitaDoc;
    }
}
