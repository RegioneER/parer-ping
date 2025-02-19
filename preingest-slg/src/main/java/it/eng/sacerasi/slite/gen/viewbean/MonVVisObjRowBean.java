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
import java.sql.Timestamp;
import java.util.Calendar;

import it.eng.sacerasi.viewEntity.MonVVisObj;
import it.eng.spagoLite.db.base.JEEBaseRowInterface;
import it.eng.spagoLite.db.base.row.BaseRow;
import it.eng.spagoLite.db.oracle.bean.column.TableDescriptor;

/**
 * RowBean per la tabella Mon_V_Vis_Obj
 *
 */
public class MonVVisObjRowBean extends BaseRow implements JEEBaseRowInterface {

    private static final long serialVersionUID = 1L;

    /*
     * @Generated( value = "it.eg.dbtool.db.oracle.beangen.Oracle4JPAClientBeanGen$ViewBeanWriter", comments =
     * "This class was generated by OraTool", date = "Wednesday, 9 August 2017 09:47" )
     */
    public static MonVVisObjTableDescriptor TABLE_DESCRIPTOR = new MonVVisObjTableDescriptor();

    public MonVVisObjRowBean() {
        super();
    }

    public TableDescriptor getTableDescriptor() {
        return TABLE_DESCRIPTOR;
    }

    public BigDecimal getIdAmbienteVers() {
        return getBigDecimal("id_ambiente_vers");
    }

    public void setIdAmbienteVers(BigDecimal idAmbienteVers) {
        setObject("id_ambiente_vers", idAmbienteVers);
    }

    public String getNmAmbienteVers() {
        return getString("nm_ambiente_vers");
    }

    public void setNmAmbienteVers(String nmAmbienteVers) {
        setObject("nm_ambiente_vers", nmAmbienteVers);
    }

    public String getNote() {
        return getString("note");
    }

    public void setNote(String note) {
        setObject("note", note);
    }

    public BigDecimal getIdVers() {
        return getBigDecimal("id_vers");
    }

    public void setIdVers(BigDecimal idVers) {
        setObject("id_vers", idVers);
    }

    public String getNmVers() {
        return getString("nm_vers");
    }

    public void setNmVers(String nmVers) {
        setObject("nm_vers", nmVers);
    }

    public BigDecimal getIdObject() {
        return getBigDecimal("id_object");
    }

    public void setIdObject(BigDecimal idObject) {
        setObject("id_object", idObject);
    }

    public String getCdKeyObject() {
        return getString("cd_key_object");
    }

    public void setCdKeyObject(String cdKeyObject) {
        setObject("cd_key_object", cdKeyObject);
    }

    public BigDecimal getIdTipoObject() {
        return getBigDecimal("id_tipo_object");
    }

    public void setIdTipoObject(BigDecimal idTipoObject) {
        setObject("id_tipo_object", idTipoObject);
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

    public String getFlRichAnnulTimeout() {
        return getString("fl_rich_annul_timeout");
    }

    public void setFlRichAnnulTimeout(String flRichAnnulTimeout) {
        setObject("fl_rich_annul_timeout", flRichAnnulTimeout);
    }

    public BigDecimal getIdLastSessioneIngest() {
        return getBigDecimal("id_last_sessione_ingest");
    }

    public void setIdLastSessioneIngest(BigDecimal idLastSessioneIngest) {
        setObject("id_last_sessione_ingest", idLastSessioneIngest);
    }

    public BigDecimal getNiUnitaDocDaVers() {
        return getBigDecimal("ni_unita_doc_da_vers");
    }

    public void setNiUnitaDocDaVers(BigDecimal niUnitaDocDaVers) {
        setObject("ni_unita_doc_da_vers", niUnitaDocDaVers);
    }

    public BigDecimal getNiUnitaDocVers() {
        return getBigDecimal("ni_unita_doc_vers");
    }

    public void setNiUnitaDocVers(BigDecimal niUnitaDocVers) {
        setObject("ni_unita_doc_vers", niUnitaDocVers);
    }

    public BigDecimal getNiUnitaDocVersOk() {
        return getBigDecimal("ni_unita_doc_vers_ok");
    }

    public void setNiUnitaDocVersOk(BigDecimal niUnitaDocVersOk) {
        setObject("ni_unita_doc_vers_ok", niUnitaDocVersOk);
    }

    public BigDecimal getNiUnitaDocVersErr() {
        return getBigDecimal("ni_unita_doc_vers_err");
    }

    public void setNiUnitaDocVersErr(BigDecimal niUnitaDocVersErr) {
        setObject("ni_unita_doc_vers_err", niUnitaDocVersErr);
    }

    public BigDecimal getNiUnitaDocVersTimeout() {
        return getBigDecimal("ni_unita_doc_vers_timeout");
    }

    public void setNiUnitaDocVersTimeout(BigDecimal niUnitaDocVersTimeout) {
        setObject("ni_unita_doc_vers_timeout", niUnitaDocVersTimeout);
    }

    public Timestamp getDtApertura() {
        return getTimestamp("dt_apertura");
    }

    public void setDtApertura(Timestamp dtApertura) {
        setObject("dt_apertura", dtApertura);
    }

    public Timestamp getDtChiusura() {
        return getTimestamp("dt_chiusura");
    }

    public void setDtChiusura(Timestamp dtChiusura) {
        setObject("dt_chiusura", dtChiusura);
    }

    public String getFlForzaWarning() {
        return getString("fl_forza_warning");
    }

    public void setFlForzaWarning(String flForzaWarning) {
        setObject("fl_forza_warning", flForzaWarning);
    }

    public String getFlForzaAccettazione() {
        return getString("fl_forza_accettazione");
    }

    public void setFlForzaAccettazione(String flForzaAccettazione) {
        setObject("fl_forza_accettazione", flForzaAccettazione);
    }

    public String getDlMotivoForzaAccettazione() {
        return getString("dl_motivo_forza_accettazione");
    }

    public void setDlMotivoForzaAccettazione(String dlMotivoForzaAccettazione) {
        setObject("dl_motivo_forza_accettazione", dlMotivoForzaAccettazione);
    }

    public String getDlMotivoChiusoWarning() {
        return getString("dl_motivo_chiuso_warning");
    }

    public void setDlMotivoChiusoWarning(String dlMotivoChiusoWarning) {
        setObject("dl_motivo_chiuso_warning", dlMotivoChiusoWarning);
    }

    public String getCdVersioneXmlVers() {
        return getString("cd_versione_xml_vers");
    }

    public void setCdVersioneXmlVers(String cdVersioneXmlVers) {
        setObject("cd_versione_xml_vers", cdVersioneXmlVers);
    }

    public String getBlXml() {
        return getString("bl_xml");
    }

    public void setBlXml(String blXml) {
        setObject("bl_xml", blXml);
    }

    public String getTiStatoVerificaHash() {
        return getString("ti_stato_verifica_hash");
    }

    public void setTiStatoVerificaHash(String tiStatoVerificaHash) {
        setObject("ti_stato_verifica_hash", tiStatoVerificaHash);
    }

    public String getCdVersGen() {
        return getString("cd_vers_gen");
    }

    public void setCdVersGen(String cdVersGen) {
        setObject("cd_vers_gen", cdVersGen);
    }

    public String getCdTrasf() {
        return getString("cd_trasf");
    }

    public void setCdTrasf(String cdTrasf) {
        setObject("cd_trasf", cdTrasf);
    }

    public String getCdVersioneTrasf() {
        return getString("cd_versione_trasf");
    }

    public void setCdVersioneTrasf(String cdVersioneTrasf) {
        setObject("cd_versione_trasf", cdVersioneTrasf);
    }

    public String getTiGestOggettiFigli() {
        return getString("ti_gest_oggetti_figli");
    }

    public void setTiGestOggettiFigli(String tiGestOggettiFigli) {
        setObject("ti_gest_oggetti_figli", tiGestOggettiFigli);
    }

    public BigDecimal getNiUnitaDocAttese() {
        return getBigDecimal("ni_unita_doc_attese");
    }

    public void setNiUnitaDocAttese(BigDecimal niUnitaDocAttese) {
        setObject("ni_unita_doc_attese", niUnitaDocAttese);
    }

    public BigDecimal getPgOggettoTrasf() {
        return getBigDecimal("pg_oggetto_trasf");
    }

    public void setPgOggettoTrasf(BigDecimal pgOggettoTrasf) {
        setObject("pg_oggetto_trasf", pgOggettoTrasf);
    }

    public String getTiVersFile() {
        return getString("ti_vers_file");
    }

    public void setTiVersFile(String tiVersFile) {
        setObject("ti_vers_file", tiVersFile);
    }

    public BigDecimal getIdAmbienteVersPadre() {
        return getBigDecimal("id_ambiente_vers_padre");
    }

    public void setIdAmbienteVersPadre(BigDecimal idAmbienteVersPadre) {
        setObject("id_ambiente_vers_padre", idAmbienteVersPadre);
    }

    public String getNmAmbienteVersPadre() {
        return getString("nm_ambiente_vers_padre");
    }

    public void setNmAmbienteVersPadre(String nmAmbienteVersPadre) {
        setObject("nm_ambiente_vers_padre", nmAmbienteVersPadre);
    }

    public BigDecimal getIdVersPadre() {
        return getBigDecimal("id_vers_padre");
    }

    public void setIdVersPadre(BigDecimal idVersPadre) {
        setObject("id_vers_padre", idVersPadre);
    }

    public String getNmVersPadre() {
        return getString("nm_vers_padre");
    }

    public void setNmVersPadre(String nmVersPadre) {
        setObject("nm_vers_padre", nmVersPadre);
    }

    public BigDecimal getIdObjectPadre() {
        return getBigDecimal("id_object_padre");
    }

    public void setIdObjectPadre(BigDecimal idObjectPadre) {
        setObject("id_object_padre", idObjectPadre);
    }

    public String getCdKeyObjectPadre() {
        return getString("cd_key_object_padre");
    }

    public void setCdKeyObjectPadre(String cdKeyObjectPadre) {
        setObject("cd_key_object_padre", cdKeyObjectPadre);
    }

    public String getDsObjectPadre() {
        return getString("ds_object_padre");
    }

    public void setDsObjectPadre(String dsObjectPadre) {
        setObject("ds_object_padre", dsObjectPadre);
    }

    public BigDecimal getIdTipoObjectPadre() {
        return getBigDecimal("id_tipo_object_padre");
    }

    public void setIdTipoObjectPadre(BigDecimal idTipoObjectPadre) {
        setObject("id_tipo_object_padre", idTipoObjectPadre);
    }

    public String getNmTipoObjectPadre() {
        return getString("nm_tipo_object_padre");
    }

    public void setNmTipoObjectPadre(String nmTipoObjectPadre) {
        setObject("nm_tipo_object_padre", nmTipoObjectPadre);
    }

    public String getTiStatoObjectPadre() {
        return getString("ti_stato_object_padre");
    }

    public void setTiStatoObjectPadre(String tiStatoObjectPadre) {
        setObject("ti_stato_object_padre", tiStatoObjectPadre);
    }

    public Timestamp getDtStatoCorPadre() {
        return getTimestamp("dt_stato_cor_padre");
    }

    public void setDtStatoCorPadre(Timestamp dtStatoCorPadre) {
        setObject("dt_stato_cor_padre", dtStatoCorPadre);
    }

    public BigDecimal getNiTotObjectTrasf() {
        return getBigDecimal("ni_tot_object_trasf");
    }

    public void setNiTotObjectTrasf(BigDecimal niTotObjectTrasf) {
        setObject("ni_tot_object_trasf", niTotObjectTrasf);
    }

    public BigDecimal getIdObjectTrasf() {
        return getBigDecimal("id_object_trasf");
    }

    public void setIdObjectTrasf(BigDecimal idObjectTrasf) {
        setObject("id_object_trasf", idObjectTrasf);
    }

    public String getTiPriorita() {
        return getString("ti_priorita");
    }

    public void setTiPriorita(String tiPriorita) {
        setObject("ti_priorita", tiPriorita);
    }

    public String getTiPrioritaVersamento() {
        return getString("ti_priorita_versamento");
    }

    public void setTiPrioritaVersamento(String tiPrioritaVersamento) {
        setObject("ti_priorita_versamento", tiPrioritaVersamento);
    }

    public String getNmUseridVers() {
        return getString("nm_userid_vers");
    }

    public void setNmUseridVers(String nmUseridVers) {
        setObject("nm_userid_vers", nmUseridVers);
    }

    @Override
    public void entityToRowBean(Object obj) {
        MonVVisObj entity = (MonVVisObj) obj;
        this.setIdAmbienteVers(entity.getIdAmbienteVers());
        this.setNmAmbienteVers(entity.getNmAmbienteVers());
        this.setIdVers(entity.getIdVers());
        this.setNmVers(entity.getNmVers());
        this.setNote(entity.getNote());
        this.setIdObject(entity.getIdObject());
        this.setCdKeyObject(entity.getCdKeyObject());
        this.setIdTipoObject(entity.getIdTipoObject());
        this.setNmTipoObject(entity.getNmTipoObject());
        this.setDsInfoObject(entity.getDsInfoObject());
        this.setTiStatoObject(entity.getTiStatoObject());
        this.setFlRichAnnulTimeout(entity.getFlRichAnnulTimeout());
        this.setIdLastSessioneIngest(entity.getIdLastSessioneIngest());
        this.setNiUnitaDocDaVers(entity.getNiUnitaDocDaVers());
        this.setNiUnitaDocVers(entity.getNiUnitaDocVers());
        this.setNiUnitaDocVersOk(entity.getNiUnitaDocVersOk());
        this.setNiUnitaDocVersErr(entity.getNiUnitaDocVersErr());
        this.setNiUnitaDocVersTimeout(entity.getNiUnitaDocVersTimeout());
        if (entity.getDtApertura() != null) {
            this.setDtApertura(new Timestamp(entity.getDtApertura().getTime()));
        }
        if (entity.getDtChiusura() != null) {
            this.setDtChiusura(new Timestamp(entity.getDtChiusura().getTime()));
        }
        this.setFlForzaWarning(entity.getFlForzaWarning());
        this.setFlForzaAccettazione(entity.getFlForzaAccettazione());
        this.setDlMotivoForzaAccettazione(entity.getDlMotivoForzaAccettazione());
        this.setDlMotivoChiusoWarning(entity.getDlMotivoChiusoWarning());
        this.setCdVersioneXmlVers(entity.getCdVersioneXmlVers());
        this.setBlXml(entity.getBlXml());
        this.setTiStatoVerificaHash(entity.getTiStatoVerificaHash());
        this.setCdVersGen(entity.getCdVersGen());
        this.setCdTrasf(entity.getCdTrasf());
        this.setCdVersioneTrasf(entity.getCdVersioneTrasf());
        this.setTiGestOggettiFigli(entity.getTiGestOggettiFigli());
        this.setNiUnitaDocAttese(entity.getNiUnitaDocAttese());
        this.setPgOggettoTrasf(entity.getPgOggettoTrasf());
        this.setTiVersFile(entity.getTiVersFile());
        this.setIdAmbienteVersPadre(entity.getIdAmbienteVersPadre());
        this.setNmAmbienteVersPadre(entity.getNmAmbienteVersPadre());
        this.setIdVersPadre(entity.getIdVersPadre());
        this.setNmVersPadre(entity.getNmVersPadre());
        this.setIdObjectPadre(entity.getIdObjectPadre());
        this.setCdKeyObjectPadre(entity.getCdKeyObjectPadre());
        this.setDsObjectPadre(entity.getDsObjectPadre());
        this.setIdTipoObjectPadre(entity.getIdTipoObjectPadre());
        this.setNmTipoObjectPadre(entity.getNmTipoObjectPadre());
        this.setTiStatoObjectPadre(entity.getTiStatoObjectPadre());
        if (entity.getDtStatoCorPadre() != null) {
            this.setDtStatoCorPadre(new Timestamp(entity.getDtStatoCorPadre().getTime()));
        }
        this.setNiTotObjectTrasf(entity.getNiTotObjectTrasf());
        this.setIdObjectTrasf(entity.getIdObjectTrasf());
        this.setTiPriorita(entity.getTiPriorita());
        this.setTiPrioritaVersamento(entity.getTiPrioritaVersamento());
        this.setNmUseridVers(entity.getNmUseridVers());
    }

    @Override
    public MonVVisObj rowBeanToEntity() {
        MonVVisObj entity = new MonVVisObj();
        entity.setIdAmbienteVers(this.getIdAmbienteVers());
        entity.setNmAmbienteVers(this.getNmAmbienteVers());
        entity.setIdVers(this.getIdVers());
        entity.setNmVers(this.getNmVers());
        entity.setIdObject(this.getIdObject());
        entity.setCdKeyObject(this.getCdKeyObject());
        entity.setIdTipoObject(this.getIdTipoObject());
        entity.setNmTipoObject(this.getNmTipoObject());
        entity.setNote(this.getNote());
        entity.setDsInfoObject(this.getDsInfoObject());
        entity.setTiStatoObject(this.getTiStatoObject());
        entity.setFlRichAnnulTimeout(this.getFlRichAnnulTimeout());
        entity.setIdLastSessioneIngest(this.getIdLastSessioneIngest());
        entity.setNiUnitaDocDaVers(this.getNiUnitaDocDaVers());
        entity.setNiUnitaDocVers(this.getNiUnitaDocVers());
        entity.setNiUnitaDocVersOk(this.getNiUnitaDocVersOk());
        entity.setNiUnitaDocVersErr(this.getNiUnitaDocVersErr());
        entity.setNiUnitaDocVersTimeout(this.getNiUnitaDocVersTimeout());
        entity.setDtApertura(this.getDtApertura());
        entity.setDtChiusura(this.getDtChiusura());
        entity.setFlForzaWarning(this.getFlForzaWarning());
        entity.setFlForzaAccettazione(this.getFlForzaAccettazione());
        entity.setDlMotivoForzaAccettazione(this.getDlMotivoForzaAccettazione());
        entity.setDlMotivoChiusoWarning(this.getDlMotivoChiusoWarning());
        entity.setCdVersioneXmlVers(this.getCdVersioneXmlVers());
        entity.setBlXml(this.getBlXml());
        entity.setTiStatoVerificaHash(this.getTiStatoVerificaHash());
        entity.setCdVersGen(this.getCdVersGen());
        entity.setCdTrasf(this.getCdTrasf());
        entity.setCdVersioneTrasf(this.getCdVersioneTrasf());
        entity.setTiGestOggettiFigli(this.getTiGestOggettiFigli());
        entity.setNiUnitaDocAttese(this.getNiUnitaDocAttese());
        entity.setPgOggettoTrasf(this.getPgOggettoTrasf());
        entity.setTiVersFile(this.getTiVersFile());
        entity.setIdAmbienteVersPadre(this.getIdAmbienteVersPadre());
        entity.setNmAmbienteVersPadre(this.getNmAmbienteVersPadre());
        entity.setIdVersPadre(this.getIdVersPadre());
        entity.setNmVersPadre(this.getNmVersPadre());
        entity.setIdObjectPadre(this.getIdObjectPadre());
        entity.setCdKeyObjectPadre(this.getCdKeyObjectPadre());
        entity.setDsObjectPadre(this.getDsObjectPadre());
        entity.setIdTipoObjectPadre(this.getIdTipoObjectPadre());
        entity.setNmTipoObjectPadre(this.getNmTipoObjectPadre());
        entity.setTiStatoObjectPadre(this.getTiStatoObjectPadre());
        if (this.getDtStatoCorPadre() != null) {
            Calendar tmp = Calendar.getInstance();
            tmp.setTimeInMillis(this.getDtStatoCorPadre().getTime());
            entity.setDtStatoCorPadre(tmp.getTime());
        }
        entity.setNiTotObjectTrasf(this.getNiTotObjectTrasf());
        entity.setIdObjectTrasf(this.getIdObjectTrasf());
        entity.setTiPriorita(this.getTiPriorita());
        entity.setTiPrioritaVersamento(this.getTiPrioritaVersamento());
        entity.setNmUseridVers(this.getNmUseridVers());
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
