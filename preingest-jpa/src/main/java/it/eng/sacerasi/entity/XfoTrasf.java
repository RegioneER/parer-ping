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

package it.eng.sacerasi.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.id.enhanced.SequenceStyleGenerator;

/**
 *
 * @author cek
 */
@Entity
@Table(name = "XFO_TRASF")
@NamedQueries({ @NamedQuery(name = "XfoTrasf.findAll", query = "SELECT x FROM XfoTrasf x"),
        @NamedQuery(name = "XfoTrasf.findByIdTrasf", query = "SELECT x FROM XfoTrasf x WHERE x.idTrasf = :idTrasf"),
        @NamedQuery(name = "XfoTrasf.findByCdTrasf", query = "SELECT x FROM XfoTrasf x WHERE x.cdTrasf = :cdTrasf"),
        @NamedQuery(name = "XfoTrasf.findByDsTrasf", query = "SELECT x FROM XfoTrasf x WHERE x.dsTrasf = :dsTrasf"),
        @NamedQuery(name = "XfoTrasf.findByFlAttiva", query = "SELECT x FROM XfoTrasf x WHERE x.flAttiva = :flAttiva"),
        @NamedQuery(name = "XfoTrasf.findByCdVersioneCor", query = "SELECT x FROM XfoTrasf x WHERE x.cdVersioneCor = :cdVersioneCor"),
        @NamedQuery(name = "XfoTrasf.findByDsVersioneCor", query = "SELECT x FROM XfoTrasf x WHERE x.dsVersioneCor = :dsVersioneCor"),
        @NamedQuery(name = "XfoTrasf.findByDtIstituz", query = "SELECT x FROM XfoTrasf x WHERE x.dtIstituz = :dtIstituz"),
        @NamedQuery(name = "XfoTrasf.findByDtSoppres", query = "SELECT x FROM XfoTrasf x WHERE x.dtSoppres = :dtSoppres") })
public class XfoTrasf implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long idTrasf;
    private String cdTrasf;
    private String dsTrasf;
    private String flAttiva;
    private String cdVersioneCor;
    private String dsVersioneCor;
    private Date dtIstituz;
    private Date dtSoppres;
    private byte[] blTrasf;
    private String cdKettleId;
    private List<XfoSetParamTrasf> xfoSetParamTrasfList = new ArrayList<>();
    private List<XfoStoricoTrasf> xfoStoricoTrasfList = new ArrayList<>();

    public XfoTrasf() {
    }

    public XfoTrasf(long idTrasf) {
        this.idTrasf = idTrasf;
    }

    public XfoTrasf(long idTrasf, String cdTrasf, String dsTrasf, String flAttiva, String cdVersioneCor,
            String dsVersioneCor, Date dtIstituz, Date dtSoppres, byte[] blTrasf) {
        this.idTrasf = idTrasf;
        this.cdTrasf = cdTrasf;
        this.dsTrasf = dsTrasf;
        this.flAttiva = flAttiva;
        this.cdVersioneCor = cdVersioneCor;
        this.dsVersioneCor = dsVersioneCor;
        this.dtIstituz = dtIstituz;
        this.dtSoppres = dtSoppres;
        this.blTrasf = blTrasf;
    }

    @Id
    @GenericGenerator(name = "XFO_TRASF_IDTRASF_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
            @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SXFO_TRASF"),
            @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "XFO_TRASF_IDTRASF_GENERATOR")
    @Column(name = "ID_TRASF")
    public Long getIdTrasf() {
        return idTrasf;
    }

    public void setIdTrasf(Long idTrasf) {
        this.idTrasf = idTrasf;
    }

    @Column(name = "CD_TRASF")
    public String getCdTrasf() {
        return cdTrasf;
    }

    public void setCdTrasf(String cdTrasf) {
        this.cdTrasf = cdTrasf;
    }

    @Column(name = "DS_TRASF")
    public String getDsTrasf() {
        return dsTrasf;
    }

    public void setDsTrasf(String dsTrasf) {
        this.dsTrasf = dsTrasf;
    }

    public void setFlAttiva(String flAttiva) {
        this.flAttiva = flAttiva;
    }

    @Column(name = "FL_ATTIVA", columnDefinition = "char")
    public String getFlAttiva() {
        return flAttiva;
    }

    @Lob
    @Column(name = "BL_TRASF")
    public byte[] getBlTrasf() {
        return blTrasf;
    }

    @Column(name = "CD_VERSIONE_COR")
    public String getCdVersioneCor() {
        return cdVersioneCor;
    }

    public void setCdVersioneCor(String cdVersioneCor) {
        this.cdVersioneCor = cdVersioneCor;
    }

    @Column(name = "DS_VERSIONE_COR")
    public String getDsVersioneCor() {
        return dsVersioneCor;
    }

    public void setDsVersioneCor(String dsVersioneCor) {
        this.dsVersioneCor = dsVersioneCor;
    }

    @Column(name = "DT_ISTITUZ")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getDtIstituz() {
        return dtIstituz;
    }

    public void setDtIstituz(Date dtIstituz) {
        this.dtIstituz = dtIstituz;
    }

    @Column(name = "DT_SOPPRES")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getDtSoppres() {
        return dtSoppres;
    }

    public void setDtSoppres(Date dtSoppres) {
        this.dtSoppres = dtSoppres;
    }

    public void setBlTrasf(byte[] blTrasf) {
        this.blTrasf = blTrasf;
    }

    @Column(name = "CD_KETTLE_ID")
    public String getCdKettleId() {
        return cdKettleId;
    }

    public void setCdKettleId(String cdKettleId) {
        this.cdKettleId = cdKettleId;
    }

    @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "xfoTrasf", fetch = FetchType.LAZY)
    public List<XfoSetParamTrasf> getXfoSetParamTrasfList() {
        return xfoSetParamTrasfList;
    }

    public void setXfoSetParamTrasfList(List<XfoSetParamTrasf> xfoSetParamTrasfList) {
        this.xfoSetParamTrasfList = xfoSetParamTrasfList;
    }

    @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "xfoTrasf", fetch = FetchType.LAZY)
    public List<XfoStoricoTrasf> getXfoStoricoTrasfList() {
        return xfoStoricoTrasfList;
    }

    public void setXfoStoricoTrasfList(List<XfoStoricoTrasf> xfoStoricoTrasfList) {
        this.xfoStoricoTrasfList = xfoStoricoTrasfList;
    }
}
