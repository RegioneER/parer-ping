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

import it.eng.sacerasi.entity.PigTipoObject;
import it.eng.sacerasi.entity.PigVers;
import it.eng.sacerasi.entity.XfoTrasf;
import it.eng.spagoLite.db.base.JEEBaseRowInterface;
import it.eng.spagoLite.db.base.row.BaseRow;
import it.eng.spagoLite.db.oracle.bean.column.TableDescriptor;

/**
 * RowBean per la tabella Pig_Tipo_Object
 *
 */
public class PigTipoObjectRowBean extends BaseRow implements JEEBaseRowInterface {

    private static final long serialVersionUID = 1L;

    /*
     * @Generated( value = "it.eg.dbtool.db.oracle.beangen.Oracle4JPAClientBeanGen$TableBeanWriter", comments =
     * "This class was generated by OraTool", date = "Wednesday, 24 May 2017 11:06" )
     */

    public static PigTipoObjectTableDescriptor TABLE_DESCRIPTOR = new PigTipoObjectTableDescriptor();

    public PigTipoObjectRowBean() {
        super();
    }

    public TableDescriptor getTableDescriptor() {
        return TABLE_DESCRIPTOR;
    }

    // getter e setter
    public BigDecimal getIdTipoObject() {
        return getBigDecimal("id_tipo_object");
    }

    public void setIdTipoObject(BigDecimal idTipoObject) {
        setObject("id_tipo_object", idTipoObject);
    }

    public BigDecimal getIdVers() {
        return getBigDecimal("id_vers");
    }

    public void setIdVers(BigDecimal idVers) {
        setObject("id_vers", idVers);
    }

    public String getNmTipoObject() {
        return getString("nm_tipo_object");
    }

    public void setNmTipoObject(String nmTipoObject) {
        setObject("nm_tipo_object", nmTipoObject);
    }

    public String getDsTipoObject() {
        return getString("ds_tipo_object");
    }

    public void setDsTipoObject(String dsTipoObject) {
        setObject("ds_tipo_object", dsTipoObject);
    }

    public String getTiVersFile() {
        return getString("ti_vers_file");
    }

    public void setTiVersFile(String tiVersFile) {
        setObject("ti_vers_file", tiVersFile);
    }

    public String getTiCalcKeyUnitaDoc() {
        return getString("ti_calc_key_unita_doc");
    }

    public void setTiCalcKeyUnitaDoc(String tiCalcKeyUnitaDoc) {
        setObject("ti_calc_key_unita_doc", tiCalcKeyUnitaDoc);
    }

    public String getFlContrHash() {
        return getString("fl_contr_hash");
    }

    public void setFlContrHash(String flContrHash) {
        setObject("fl_contr_hash", flContrHash);
    }

    public String getCdRegistroUnitaDocSacer() {
        return getString("cd_registro_unita_doc_sacer");
    }

    public void setCdRegistroUnitaDocSacer(String cdRegistroUnitaDocSacer) {
        setObject("cd_registro_unita_doc_sacer", cdRegistroUnitaDocSacer);
    }

    public String getNmTipoUnitaDocSacer() {
        return getString("nm_tipo_unita_doc_sacer");
    }

    public void setNmTipoUnitaDocSacer(String nmTipoUnitaDocSacer) {
        setObject("nm_tipo_unita_doc_sacer", nmTipoUnitaDocSacer);
    }

    public String getFlForzaAccettazioneSacer() {
        return getString("fl_forza_accettazione_sacer");
    }

    public void setFlForzaAccettazioneSacer(String flForzaAccettazioneSacer) {
        setObject("fl_forza_accettazione_sacer", flForzaAccettazioneSacer);
    }

    public String getFlForzaConservazione() {
        return getString("fl_forza_conservazione");
    }

    public void setFlForzaConservazione(String flForzaConservazione) {
        setObject("fl_forza_conservazione", flForzaConservazione);
    }

    public String getFlForzaCollegamento() {
        return getString("fl_forza_collegamento");
    }

    public void setFlForzaCollegamento(String flForzaCollegamento) {
        setObject("fl_forza_collegamento", flForzaCollegamento);
    }

    public String getTiConservazione() {
        return getString("ti_conservazione");
    }

    public void setTiConservazione(String tiConservazione) {
        setObject("ti_conservazione", tiConservazione);
    }

    public BigDecimal getIdTrasf() {
        return getBigDecimal("id_trasf");
    }

    public void setIdTrasf(BigDecimal idTrasf) {
        setObject("id_trasf", idTrasf);
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

    public void setTiPrioritaVersamento(String tiPriorita) {
        setObject("ti_priorita_versamento", tiPriorita);
    }

    public String getDsRegExpCdVers() {
        return getString("ds_reg_exp_cd_vers");
    }

    public void setDsRegExpCdVers(String dsRegExpCdVers) {
        setObject("ds_reg_exp_cd_vers", dsRegExpCdVers);
    }

    public String getFlNoVisibVersOgg() {
        return getString("fl_no_visib_vers_ogg");
    }

    public void setFlNoVisibVersOgg(String flNoVisibVersOgg) {
        setObject("fl_no_visib_vers_ogg", flNoVisibVersOgg);
    }

    @Override
    public void entityToRowBean(Object obj) {
        PigTipoObject entity = (PigTipoObject) obj;

        this.setIdTipoObject(new BigDecimal(entity.getIdTipoObject()));
        if (entity.getPigVer() != null) {
            this.setIdVers(new BigDecimal(entity.getPigVer().getIdVers()));

        }
        this.setNmTipoObject(entity.getNmTipoObject());
        this.setDsTipoObject(entity.getDsTipoObject());
        this.setTiVersFile(entity.getTiVersFile());
        this.setTiCalcKeyUnitaDoc(entity.getTiCalcKeyUnitaDoc());
        this.setFlContrHash(entity.getFlContrHash());
        this.setCdRegistroUnitaDocSacer(entity.getCdRegistroUnitaDocSacer());
        this.setNmTipoUnitaDocSacer(entity.getNmTipoUnitaDocSacer());
        this.setFlForzaAccettazioneSacer(entity.getFlForzaAccettazioneSacer());
        this.setFlForzaConservazione(entity.getFlForzaConservazione());
        this.setFlForzaCollegamento(entity.getFlForzaCollegamento());
        this.setTiConservazione(entity.getTiConservazione());
        this.setTiPriorita(entity.getTiPriorita());
        this.setTiPrioritaVersamento(entity.getTiPrioritaVersamento());

        if (entity.getXfoTrasf() != null) {
            this.setIdTrasf(new BigDecimal(entity.getXfoTrasf().getIdTrasf()));

        }
        this.setDsRegExpCdVers(entity.getDsRegExpCdVers());
        this.setFlNoVisibVersOgg(entity.getFlNoVisibVersOgg());
    }

    @Override
    public PigTipoObject rowBeanToEntity() {
        PigTipoObject entity = new PigTipoObject();
        if (this.getIdTipoObject() != null) {
            entity.setIdTipoObject(this.getIdTipoObject().longValue());
        }
        if (this.getIdVers() != null) {
            if (entity.getPigVer() == null) {
                entity.setPigVer(new PigVers());
            }
            entity.getPigVer().setIdVers(this.getIdVers().longValue());
        }
        entity.setNmTipoObject(this.getNmTipoObject());
        entity.setDsTipoObject(this.getDsTipoObject());
        entity.setTiVersFile(this.getTiVersFile());
        entity.setTiCalcKeyUnitaDoc(this.getTiCalcKeyUnitaDoc());
        entity.setFlContrHash(this.getFlContrHash());
        entity.setCdRegistroUnitaDocSacer(this.getCdRegistroUnitaDocSacer());
        entity.setNmTipoUnitaDocSacer(this.getNmTipoUnitaDocSacer());
        entity.setFlForzaAccettazioneSacer(this.getFlForzaAccettazioneSacer());
        entity.setFlForzaConservazione(this.getFlForzaConservazione());
        entity.setFlForzaCollegamento(this.getFlForzaCollegamento());
        entity.setTiPriorita(this.getTiPriorita());
        entity.setTiPrioritaVersamento(this.getTiPrioritaVersamento());
        entity.setTiConservazione(this.getTiConservazione());
        if (this.getIdTrasf() != null) {
            if (entity.getXfoTrasf() == null) {
                entity.setXfoTrasf(new XfoTrasf());
            }
            entity.getXfoTrasf().setIdTrasf(this.getIdTrasf().longValue());
        }
        entity.setDsRegExpCdVers(this.getDsRegExpCdVers());
        entity.setFlNoVisibVersOgg(this.getFlNoVisibVersOgg());
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