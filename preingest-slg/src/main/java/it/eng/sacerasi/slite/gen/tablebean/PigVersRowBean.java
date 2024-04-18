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
import java.sql.Timestamp;

import it.eng.sacerasi.entity.PigAmbienteVers;
import it.eng.sacerasi.entity.PigVers;
import it.eng.spagoLite.db.base.JEEBaseRowInterface;
import it.eng.spagoLite.db.base.row.BaseRow;
import it.eng.spagoLite.db.oracle.bean.column.TableDescriptor;

/**
 * RowBean per la tabella Pig_Vers
 *
 */
public class PigVersRowBean extends BaseRow implements JEEBaseRowInterface {

    private static final long serialVersionUID = 1L;

    /*
     * @Generated( value = "it.eg.dbtool.db.oracle.beangen.Oracle4JPAClientBeanGen$TableBeanWriter", comments =
     * "This class was generated by OraTool", date = "Tuesday, 30 April 2019 11:01" )
     */
    public static PigVersTableDescriptor TABLE_DESCRIPTOR = new PigVersTableDescriptor();

    public PigVersRowBean() {
        super();
    }

    public TableDescriptor getTableDescriptor() {
        return TABLE_DESCRIPTOR;
    }

    // getter e setter
    public BigDecimal getIdVers() {
        return getBigDecimal("id_vers");
    }

    public void setIdVers(BigDecimal idVers) {
        setObject("id_vers", idVers);
    }

    public BigDecimal getIdAmbienteVers() {
        return getBigDecimal("id_ambiente_vers");
    }

    public void setIdAmbienteVers(BigDecimal idAmbienteVers) {
        setObject("id_ambiente_vers", idAmbienteVers);
    }

    public String getNmVers() {
        return getString("nm_vers");
    }

    public void setNmVers(String nmVers) {
        setObject("nm_vers", nmVers);
    }

    public String getDsVers() {
        return getString("ds_vers");
    }

    public void setDsVers(String dsVers) {
        setObject("ds_vers", dsVers);
    }

    public String getDsPathInputFtp() {
        return getString("ds_path_input_ftp");
    }

    public void setDsPathInputFtp(String dsPathInputFtp) {
        setObject("ds_path_input_ftp", dsPathInputFtp);
    }

    public String getDsPathOutputFtp() {
        return getString("ds_path_output_ftp");
    }

    public void setDsPathOutputFtp(String dsPathOutputFtp) {
        setObject("ds_path_output_ftp", dsPathOutputFtp);
    }

    public String getDsPathTrasf() {
        return getString("ds_path_trasf");
    }

    public void setDsPathTrasf(String dsPathTrasf) {
        setObject("ds_path_trasf", dsPathTrasf);
    }

    public Timestamp getDtIniValAppartAmbiente() {
        return getTimestamp("dt_ini_val_appart_ambiente");
    }

    public void setDtIniValAppartAmbiente(Timestamp dtIniValAppartAmbiente) {
        setObject("dt_ini_val_appart_ambiente", dtIniValAppartAmbiente);
    }

    public Timestamp getDtFinValAppartAmbiente() {
        return getTimestamp("dt_fin_val_appart_ambiente");
    }

    public void setDtFinValAppartAmbiente(Timestamp dtFinValAppartAmbiente) {
        setObject("dt_fin_val_appart_ambiente", dtFinValAppartAmbiente);
    }

    public BigDecimal getIdEnteConvenz() {
        return getBigDecimal("id_ente_convenz");
    }

    public void setIdEnteConvenz(BigDecimal idEnteConvenz) {
        setObject("id_ente_convenz", idEnteConvenz);
    }

    public BigDecimal getIdEnteFornitEstern() {
        return getBigDecimal("id_ente_fornit_estern");
    }

    public void setIdEnteFornitEstern(BigDecimal idEnteFornitEstern) {
        setObject("id_ente_fornit_estern", idEnteFornitEstern);
    }

    public Timestamp getDtIniValAppartEnteSiam() {
        return getTimestamp("dt_ini_val_appart_ente_siam");
    }

    public void setDtIniValAppartEnteSiam(Timestamp dtIniValAppartEnteSiam) {
        setObject("dt_ini_val_appart_ente_siam", dtIniValAppartEnteSiam);
    }

    public Timestamp getDtFineValAppartEnteSiam() {
        return getTimestamp("dt_fine_val_appart_ente_siam");
    }

    public void setDtFineValAppartEnteSiam(Timestamp dtFineValAppartEnteSiam) {
        setObject("dt_fine_val_appart_ente_siam", dtFineValAppartEnteSiam);
    }

    public Timestamp getDtIniValVers() {
        return getTimestamp("dt_ini_val_vers");
    }

    public void setDtIniValVers(Timestamp dtIniValVers) {
        setObject("dt_ini_val_vers", dtIniValVers);
    }

    public Timestamp getDtFineValVers() {
        return getTimestamp("dt_fine_val_vers");
    }

    public void setDtFineValVers(Timestamp dtFineValVers) {
        setObject("dt_fine_val_vers", dtFineValVers);
    }

    public String getFlCessato() {
        return getString("fl_cessato");
    }

    public void setFlCessato(String flCessato) {
        setObject("fl_cessato", flCessato);
    }

    public String getFlArchivioRestituito() {
        return getString("fl_archivio_restituito");
    }

    public void setFlArchivioRestituito(String flArchivioRestituito) {
        setObject("fl_archivio_restituito", flArchivioRestituito);
    }

    @Override
    public void entityToRowBean(Object obj) {
        PigVers entity = (PigVers) obj;

        this.setIdVers(new BigDecimal(entity.getIdVers()));
        if (entity.getPigAmbienteVer() != null) {
            this.setIdAmbienteVers(new BigDecimal(entity.getPigAmbienteVer().getIdAmbienteVers()));
        }

        this.setNmVers(entity.getNmVers());
        this.setDsVers(entity.getDsVers());
        this.setDsPathInputFtp(entity.getDsPathInputFtp());
        this.setDsPathOutputFtp(entity.getDsPathOutputFtp());
        this.setDsPathTrasf(entity.getDsPathTrasf());
        if (entity.getDtIniValAppartAmbiente() != null) {
            this.setDtIniValAppartAmbiente(new Timestamp(entity.getDtIniValAppartAmbiente().getTime()));
        }
        if (entity.getDtFinValAppartAmbiente() != null) {
            this.setDtFinValAppartAmbiente(new Timestamp(entity.getDtFinValAppartAmbiente().getTime()));
        }
        this.setIdEnteConvenz(entity.getIdEnteConvenz());
        this.setIdEnteFornitEstern(entity.getIdEnteFornitEstern());
        if (entity.getDtIniValAppartEnteSiam() != null) {
            this.setDtIniValAppartEnteSiam(new Timestamp(entity.getDtIniValAppartEnteSiam().getTime()));
        }
        if (entity.getDtFineValAppartEnteSiam() != null) {
            this.setDtFineValAppartEnteSiam(new Timestamp(entity.getDtFineValAppartEnteSiam().getTime()));
        }
        if (entity.getDtIniValVers() != null) {
            this.setDtIniValVers(new Timestamp(entity.getDtIniValVers().getTime()));
        }
        if (entity.getDtFineValVers() != null) {
            this.setDtFineValVers(new Timestamp(entity.getDtFineValVers().getTime()));
        }
        this.setFlCessato(entity.getFlCessato());
        this.setFlArchivioRestituito(entity.getFlArchivioRestituito());
    }

    @Override
    public PigVers rowBeanToEntity() {
        PigVers entity = new PigVers();
        if (this.getIdVers() != null) {
            entity.setIdVers(this.getIdVers().longValue());
        }
        if (this.getIdAmbienteVers() != null) {
            if (entity.getPigAmbienteVer() == null) {
                entity.setPigAmbienteVer(new PigAmbienteVers());
            }
            entity.getPigAmbienteVer().setIdAmbienteVers(this.getIdAmbienteVers().longValue());
        }
        entity.setNmVers(this.getNmVers());
        entity.setDsVers(this.getDsVers());
        entity.setDsPathInputFtp(this.getDsPathInputFtp());
        entity.setDsPathOutputFtp(this.getDsPathOutputFtp());
        entity.setDsPathTrasf(this.getDsPathTrasf());
        entity.setDtIniValAppartAmbiente(this.getDtIniValAppartAmbiente());
        entity.setDtFinValAppartAmbiente(this.getDtFinValAppartAmbiente());
        entity.setIdEnteConvenz(this.getIdEnteConvenz());
        entity.setIdEnteFornitEstern(this.getIdEnteFornitEstern());
        entity.setDtIniValAppartEnteSiam(this.getDtIniValAppartEnteSiam());
        entity.setDtFineValAppartEnteSiam(this.getDtFineValAppartEnteSiam());
        entity.setDtIniValVers(this.getDtIniValVers());
        entity.setDtFineValVers(this.getDtFineValVers());
        entity.setFlCessato(this.getFlCessato());
        entity.setFlArchivioRestituito(this.getFlArchivioRestituito());
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