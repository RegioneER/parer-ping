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

package it.eng.sacerasi.slite.gen.viewbean;

import java.math.BigDecimal;

import it.eng.sacerasi.viewEntity.MonVVisUnitaDocObject;
import it.eng.spagoLite.db.base.JEEBaseRowInterface;
import it.eng.spagoLite.db.base.row.BaseRow;
import it.eng.spagoLite.db.oracle.bean.column.TableDescriptor;
import java.sql.Timestamp;

/**
 * RowBean per la tabella Mon_V_Vis_Unita_Doc_Object
 *
 */
public class MonVVisUnitaDocObjectRowBean extends BaseRow implements JEEBaseRowInterface {

    private static final long serialVersionUID = 1L;

    /*
     * @Generated( value = "it.eg.dbtool.db.oracle.beangen.Oracle4JPAClientBeanGen$ViewBeanWriter", comments =
     * "This class was generated by OraTool", date = "Wednesday, 26 April 2017 11:18" )
     */

    public static MonVVisUnitaDocObjectTableDescriptor TABLE_DESCRIPTOR = new MonVVisUnitaDocObjectTableDescriptor();

    public MonVVisUnitaDocObjectRowBean() {
        super();
    }

    public TableDescriptor getTableDescriptor() {
        return TABLE_DESCRIPTOR;
    }

    public String getNmAmbienteVers() {
        return getString("nm_ambiente_vers");
    }

    public void setNmAmbienteVers(String nmAmbienteVers) {
        setObject("nm_ambiente_vers", nmAmbienteVers);
    }

    public String getNmVers() {
        return getString("nm_vers");
    }

    public void setNmVers(String nmVers) {
        setObject("nm_vers", nmVers);
    }

    public String getCdKeyObject() {
        return getString("cd_key_object");
    }

    public void setCdKeyObject(String cdKeyObject) {
        setObject("cd_key_object", cdKeyObject);
    }

    public String getNmTipoObject() {
        return getString("nm_tipo_object");
    }

    public void setNmTipoObject(String nmTipoObject) {
        setObject("nm_tipo_object", nmTipoObject);
    }

    public String getDsInfoObject() {
        return getString("ds_info_object");
    }

    public void setDsInfoObject(String dsInfoObject) {
        setObject("ds_info_object", dsInfoObject);
    }

    public String getTiStatoObject() {
        return getString("ti_stato_object");
    }

    public void setTiStatoObject(String tiStatoObject) {
        setObject("ti_stato_object", tiStatoObject);
    }

    public BigDecimal getIdUnitaDocObject() {
        return getBigDecimal("id_unita_doc_object");
    }

    public void setIdUnitaDocObject(BigDecimal idUnitaDocObject) {
        setObject("id_unita_doc_object", idUnitaDocObject);
    }

    public String getCdRegistroUnitaDocSacer() {
        return getString("cd_registro_unita_doc_sacer");
    }

    public void setCdRegistroUnitaDocSacer(String cdRegistroUnitaDocSacer) {
        setObject("cd_registro_unita_doc_sacer", cdRegistroUnitaDocSacer);
    }

    public BigDecimal getAaUnitaDocSacer() {
        return getBigDecimal("aa_unita_doc_sacer");
    }

    public void setAaUnitaDocSacer(BigDecimal aaUnitaDocSacer) {
        setObject("aa_unita_doc_sacer", aaUnitaDocSacer);
    }

    public String getCdKeyUnitaDocSacer() {
        return getString("cd_key_unita_doc_sacer");
    }

    public void setCdKeyUnitaDocSacer(String cdKeyUnitaDocSacer) {
        setObject("cd_key_unita_doc_sacer", cdKeyUnitaDocSacer);
    }

    public BigDecimal getNiSizeFileByte() {
        return getBigDecimal("ni_size_file_byte");
    }

    public void setNiSizeFileByte(BigDecimal niSizeFileByte) {
        setObject("ni_size_file_byte", niSizeFileByte);
    }

    public String getTiStatoUnitaDocObject() {
        return getString("ti_stato_unita_doc_object");
    }

    public void setTiStatoUnitaDocObject(String tiStatoUnitaDocObject) {
        setObject("ti_stato_unita_doc_object", tiStatoUnitaDocObject);
    }

    public String getCdErrSacer() {
        return getString("cd_err_sacer");
    }

    public void setCdErrSacer(String cdErrSacer) {
        setObject("cd_err_sacer", cdErrSacer);
    }

    public String getDlErrSacer() {
        return getString("dl_err_sacer");
    }

    public void setDlErrSacer(String dlErrSacer) {
        setObject("dl_err_sacer", dlErrSacer);
    }

    public String getBlXmlVersSacer() {
        return getString("bl_xml_vers_sacer");
    }

    public void setBlXmlVersSacer(String blXmlVersSacer) {
        setObject("bl_xml_vers_sacer", blXmlVersSacer);
    }

    public String getBlXmlIndiceSacer() {
        return getString("bl_xml_indice_sacer");
    }

    public void setBlXmlIndiceSacer(String blXmlIndiceSacer) {
        setObject("bl_xml_indice_sacer", blXmlIndiceSacer);
    }

    public BigDecimal getIdOrganizIam() {
        return getBigDecimal("id_organiz_iam");
    }

    public void setIdOrganizIam(BigDecimal idOrganizIam) {
        setObject("id_organiz_iam", idOrganizIam);
    }

    public String getDlCompositoOrganiz() {
        return getString("dl_composito_organiz");
    }

    public void setDlCompositoOrganiz(String dlCompositoOrganiz) {
        setObject("dl_composito_organiz", dlCompositoOrganiz);
    }

    public String getFlVersSimulato() {
        return getString("fl_vers_simulato");
    }

    public void setFlVersSimulato(String flVersSimulato) {
        setObject("fl_vers_simulato", flVersSimulato);
    }

    public String getFlXmlMod() {
        return getString("fl_xml_mod");
    }

    public void setFlXmlMod(String flXmlMod) {
        setObject("fl_xml_mod", flXmlMod);
    }

    // MEV 27407
    public Timestamp getDtStato() {
        return getTimestamp("dt_stato");
    }

    public void setDtStato(Timestamp dtStato) {
        setObject("dt_stato", dtStato);
    }

    @Override
    public void entityToRowBean(Object obj) {
        MonVVisUnitaDocObject entity = (MonVVisUnitaDocObject) obj;
        this.setNmAmbienteVers(entity.getNmAmbienteVers());
        this.setNmVers(entity.getNmVers());
        this.setCdKeyObject(entity.getCdKeyObject());
        this.setNmTipoObject(entity.getNmTipoObject());
        this.setDsInfoObject(entity.getDsInfoObject());
        this.setTiStatoObject(entity.getTiStatoObject());
        this.setIdUnitaDocObject(entity.getIdUnitaDocObject());
        this.setCdRegistroUnitaDocSacer(entity.getCdRegistroUnitaDocSacer());
        this.setAaUnitaDocSacer(entity.getAaUnitaDocSacer());
        this.setCdKeyUnitaDocSacer(entity.getCdKeyUnitaDocSacer());
        this.setNiSizeFileByte(entity.getNiSizeFileByte());
        this.setTiStatoUnitaDocObject(entity.getTiStatoUnitaDocObject());
        this.setCdErrSacer(entity.getCdErrSacer());
        this.setDlErrSacer(entity.getDlErrSacer());
        this.setBlXmlVersSacer(entity.getBlXmlVersSacer());
        this.setBlXmlIndiceSacer(entity.getBlXmlIndiceSacer());
        this.setIdOrganizIam(entity.getIdOrganizIam());
        this.setDlCompositoOrganiz(entity.getDlCompositoOrganiz());
        this.setFlVersSimulato(entity.getFlVersSimulato());
        this.setFlXmlMod(entity.getFlXMlMod());

        if (entity.getDtStato() != null) {
            this.setDtStato(new Timestamp(entity.getDtStato().getTime()));
        }
    }

    @Override
    public MonVVisUnitaDocObject rowBeanToEntity() {
        MonVVisUnitaDocObject entity = new MonVVisUnitaDocObject();
        entity.setNmAmbienteVers(this.getNmAmbienteVers());
        entity.setNmVers(this.getNmVers());
        entity.setCdKeyObject(this.getCdKeyObject());
        entity.setNmTipoObject(this.getNmTipoObject());
        entity.setDsInfoObject(this.getDsInfoObject());
        entity.setTiStatoObject(this.getTiStatoObject());
        entity.setIdUnitaDocObject(this.getIdUnitaDocObject());
        entity.setCdRegistroUnitaDocSacer(this.getCdRegistroUnitaDocSacer());
        entity.setAaUnitaDocSacer(this.getAaUnitaDocSacer());
        entity.setCdKeyUnitaDocSacer(this.getCdKeyUnitaDocSacer());
        entity.setNiSizeFileByte(this.getNiSizeFileByte());
        entity.setTiStatoUnitaDocObject(this.getTiStatoUnitaDocObject());
        entity.setCdErrSacer(this.getCdErrSacer());
        entity.setDlErrSacer(this.getDlErrSacer());
        entity.setBlXmlVersSacer(this.getBlXmlVersSacer());
        entity.setBlXmlIndiceSacer(this.getBlXmlIndiceSacer());
        entity.setIdOrganizIam(this.getIdOrganizIam());
        entity.setDlCompositoOrganiz(this.getDlCompositoOrganiz());
        entity.setFlVersSimulato(this.getFlVersSimulato());
        entity.setFlXMlMod(this.getFlXmlMod());
        entity.setDtStato(this.getDtStato());
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
