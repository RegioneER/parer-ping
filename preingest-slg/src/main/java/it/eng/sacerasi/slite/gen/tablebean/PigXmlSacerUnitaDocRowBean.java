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

package it.eng.sacerasi.slite.gen.tablebean;

import java.math.BigDecimal;
import java.util.Objects;

import it.eng.sacerasi.entity.PigUnitaDocObject;
import it.eng.sacerasi.entity.PigXmlSacerUnitaDoc;
import it.eng.spagoLite.db.base.JEEBaseRowInterface;
import it.eng.spagoLite.db.base.row.BaseRow;
import it.eng.spagoLite.db.oracle.bean.column.TableDescriptor;

/**
 * RowBean per la tabella Pig_Xml_Sacer_Unita_Doc
 *
 */
public class PigXmlSacerUnitaDocRowBean extends BaseRow implements JEEBaseRowInterface {

    private static final long serialVersionUID = 1L;

    /*
     * @Generated( value = "it.eg.dbtool.db.oracle.beangen.Oracle4JPAClientBeanGen$TableBeanWriter", comments =
     * "This class was generated by OraTool", date = "Monday, 17 March 2014 15:12" )
     */

    public static PigXmlSacerUnitaDocTableDescriptor TABLE_DESCRIPTOR = new PigXmlSacerUnitaDocTableDescriptor();

    public PigXmlSacerUnitaDocRowBean() {
        super();
    }

    public TableDescriptor getTableDescriptor() {
        return TABLE_DESCRIPTOR;
    }

    // getter e setter
    public BigDecimal getIdXmlSacerUnitaDoc() {
        return getBigDecimal("id_xml_sacer_unita_doc");
    }

    public void setIdXmlSacerUnitaDoc(BigDecimal idXmlSacerUnitaDoc) {
        setObject("id_xml_sacer_unita_doc", idXmlSacerUnitaDoc);
    }

    public BigDecimal getIdUnitaDocObject() {
        return getBigDecimal("id_unita_doc_object");
    }

    public void setIdUnitaDocObject(BigDecimal idUnitaDocObject) {
        setObject("id_unita_doc_object", idUnitaDocObject);
    }

    public String getTiXmlSacer() {
        return getString("ti_xml_sacer");
    }

    public void setTiXmlSacer(String tiXmlSacer) {
        setObject("ti_xml_sacer", tiXmlSacer);
    }

    public String getBlXmlSacer() {
        return getString("bl_xml_sacer");
    }

    public void setBlXmlSacer(String blXmlSacer) {
        setObject("bl_xml_sacer", blXmlSacer);
    }

    public BigDecimal getIdVers() {
        return getBigDecimal("id_vers");
    }

    public void setIdVers(BigDecimal idVers) {
        setObject("id_vers", idVers);
    }

    @Override
    public void entityToRowBean(Object obj) {
        PigXmlSacerUnitaDoc entity = (PigXmlSacerUnitaDoc) obj;

        this.setIdXmlSacerUnitaDoc(new BigDecimal(entity.getIdXmlSacerUnitaDoc()));
        if (entity.getPigUnitaDocObject() != null) {
            this.setIdUnitaDocObject(new BigDecimal(entity.getPigUnitaDocObject().getIdUnitaDocObject()));

        }
        this.setTiXmlSacer(entity.getTiXmlSacer());
        this.setBlXmlSacer(entity.getBlXmlSacer());
        this.setIdVers(Objects.nonNull(entity.getIdVers()) ? BigDecimal.valueOf(entity.getIdVers()) : null);
    }

    @Override
    public PigXmlSacerUnitaDoc rowBeanToEntity() {
        PigXmlSacerUnitaDoc entity = new PigXmlSacerUnitaDoc();
        if (this.getIdXmlSacerUnitaDoc() != null) {
            entity.setIdXmlSacerUnitaDoc(this.getIdXmlSacerUnitaDoc().longValue());
        }
        if (this.getIdUnitaDocObject() != null) {
            if (entity.getPigUnitaDocObject() == null) {
                entity.setPigUnitaDocObject(new PigUnitaDocObject());
            }
            entity.getPigUnitaDocObject().setIdUnitaDocObject(this.getIdUnitaDocObject().longValue());
        }
        entity.setTiXmlSacer(this.getTiXmlSacer());
        entity.setBlXmlSacer(this.getBlXmlSacer());
        entity.setIdVers(Objects.nonNull(this.getIdVers()) ? this.getIdVers().longValue() : null);
        return entity;
    }

    // gestione della paginazione
    public void setRownum(Integer rownum) {
        setObject("rownum", rownum);
    }

    public Integer getRownum() {
        return Integer.parseInt(getObject("rownum").toString());
    }

    public void setRnum(Integer rnum) {
        setObject("rnum", rnum);
    }

    public Integer getRnum() {
        return Integer.parseInt(getObject("rnum").toString());
    }

    public void setNumrecords(Integer numRecords) {
        setObject("numrecords", numRecords);
    }

    public Integer getNumrecords() {
        return Integer.parseInt(getObject("numrecords").toString());
    }

}