package it.eng.sacerasi.slite.gen.tablebean;

import it.eng.sacerasi.entity.PigSisma;
import it.eng.sacerasi.entity.PigSismaStoricoStati;
import it.eng.spagoLite.db.base.BaseRowInterface;
import it.eng.spagoLite.db.base.JEEBaseRowInterface;
import it.eng.spagoLite.db.base.row.BaseRow;
import it.eng.spagoLite.db.oracle.bean.column.TableDescriptor;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * RowBean per la tabella Pig_Sisma_Storico_Stati
 *
 */
public class PigSismaStoricoStatiRowBean extends BaseRow implements BaseRowInterface, JEEBaseRowInterface {

    /*
     * @Generated( value = "it.eg.dbtool.db.oracle.beangen.Oracle4JPAClientBeanGen$TableBeanWriter", comments =
     * "This class was generated by OraTool", date = "Wednesday, 15 May 2024 12:32" )
     */

    public static PigSismaStoricoStatiTableDescriptor TABLE_DESCRIPTOR = new PigSismaStoricoStatiTableDescriptor();

    public PigSismaStoricoStatiRowBean() {
        super();
    }

    public TableDescriptor getTableDescriptor() {
        return TABLE_DESCRIPTOR;
    }

    // getter e setter
    public BigDecimal getIdStato() {
        return getBigDecimal("id_stato");
    }

    public void setIdStato(BigDecimal idStato) {
        setObject("id_stato", idStato);
    }

    public BigDecimal getIdSisma() {
        return getBigDecimal("id_sisma");
    }

    public void setIdSisma(BigDecimal idSisma) {
        setObject("id_sisma", idSisma);
    }

    public String getTiStato() {
        return getString("ti_stato");
    }

    public void setTiStato(String tiStato) {
        setObject("ti_stato", tiStato);
    }

    public Timestamp getTsRegStato() {
        return getTimestamp("ts_reg_stato");
    }

    public void setTsRegStato(Timestamp tsRegStato) {
        setObject("ts_reg_stato", tsRegStato);
    }

    public String getCdDesc() {
        return getString("cd_desc");
    }

    public void setCdDesc(String cdDesc) {
        setObject("cd_desc", cdDesc);
    }

    @Override
    public void entityToRowBean(Object obj) {
        PigSismaStoricoStati entity = (PigSismaStoricoStati) obj;
        this.setIdStato(entity.getIdStato() == null ? null : BigDecimal.valueOf(entity.getIdStato()));
        if (entity.getPigSisma() != null) {
            this.setIdSisma(entity.getPigSisma().getIdSisma() == null ? null
                    : BigDecimal.valueOf(entity.getPigSisma().getIdSisma()));
        }
        this.setTiStato(entity.getTiStato());
        this.setTsRegStato(new Timestamp(entity.getTsRegStato().getTime()));
        this.setCdDesc(entity.getDescrizione());
    }

    @Override
    public PigSismaStoricoStati rowBeanToEntity() {
        PigSismaStoricoStati entity = new PigSismaStoricoStati();
        if (this.getIdStato() != null) {
            entity.setIdStato(this.getIdStato().longValue());
        }
        if (this.getIdSisma() != null) {
            if (entity.getPigSisma() == null) {
                entity.setPigSisma(new PigSisma());
            }
            entity.getPigSisma().setIdSisma(this.getIdSisma().longValue());
        }
        entity.setTiStato(this.getTiStato());
        entity.setTsRegStato(this.getTsRegStato());
        entity.setDescrizione(this.getCdDesc());
        return entity;
    }

    // gestione della paginazione
    public void setRownum(Integer rownum) {
        setObject("rownum", rownum);
    }

    public Integer getRownum() {
        return Integer.valueOf(getObject("rownum").toString());
    }

    public void setRnum(Integer rnum) {
        setObject("rnum", rnum);
    }

    public Integer getRnum() {
        return Integer.valueOf(getObject("rnum").toString());
    }

    public void setNumrecords(Integer numRecords) {
        setObject("numrecords", numRecords);
    }

    public Integer getNumrecords() {
        return Integer.valueOf(getObject("numrecords").toString());
    }

}