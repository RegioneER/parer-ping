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
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.eng.sacerasi.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.id.enhanced.SequenceStyleGenerator;

/**
 *
 * @author cek
 */
@Entity
@Table(name = "XFO_SET_PARAM_TRASF")
@NamedQueries({ @NamedQuery(name = "XfoSetParamTrasf.findAll", query = "SELECT x FROM XfoSetParamTrasf x"),
        @NamedQuery(name = "XfoSetParamTrasf.findByIdSetParamTrasf", query = "SELECT x FROM XfoSetParamTrasf x WHERE x.idSetParamTrasf = :idSetParamTrasf"),
        @NamedQuery(name = "XfoSetParamTrasf.findByNmSetParamTrasf", query = "SELECT x FROM XfoSetParamTrasf x WHERE x.nmSetParamTrasf = :nmSetParamTrasf"),
        @NamedQuery(name = "XfoSetParamTrasf.findByDsSetParamTrasf", query = "SELECT x FROM XfoSetParamTrasf x WHERE x.dsSetParamTrasf = :dsSetParamTrasf"),
        @NamedQuery(name = "XfoSetParamTrasf.findByFlSetParamArk", query = "SELECT x FROM XfoSetParamTrasf x WHERE x.flSetParamArk = :flSetParamArk") })
public class XfoSetParamTrasf implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idSetParamTrasf;
    private String nmSetParamTrasf;
    private String dsSetParamTrasf;
    private String flSetParamArk;
    private List<XfoParamTrasf> xfoParamTrasfList = new ArrayList<>();
    private XfoTrasf xfoTrasf;

    public XfoSetParamTrasf() {
    }

    public XfoSetParamTrasf(long idSetParamTrasf) {
        this.idSetParamTrasf = idSetParamTrasf;
    }

    public XfoSetParamTrasf(long idSetParamTrasf, String nmSetParamTrasf, String dsSetParamTrasf,
            String flSetParamArk) {
        this.idSetParamTrasf = idSetParamTrasf;
        this.nmSetParamTrasf = nmSetParamTrasf;
        this.dsSetParamTrasf = dsSetParamTrasf;
        this.flSetParamArk = flSetParamArk;
    }

    @Id
    @GenericGenerator(name = "XFO_PARAM_TRASF_IDPARAMTRASF_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
            @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SXFO_PARAM_TRASF"),
            @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "XFO_PARAM_TRASF_IDPARAMTRASF_GENERATOR")
    @Column(name = "ID_SET_PARAM_TRASF")
    public Long getIdSetParamTrasf() {
        return idSetParamTrasf;
    }

    public void setIdSetParamTrasf(Long idSetParamTrasf) {
        this.idSetParamTrasf = idSetParamTrasf;
    }

    @Column(name = "NM_SET_PARAM_TRASF")
    public String getNmSetParamTrasf() {
        return nmSetParamTrasf;
    }

    public void setNmSetParamTrasf(String nmSetParamTrasf) {
        this.nmSetParamTrasf = nmSetParamTrasf;
    }

    @Column(name = "DS_SET_PARAM_TRASF")
    public String getDsSetParamTrasf() {
        return dsSetParamTrasf;
    }

    public void setDsSetParamTrasf(String dsSetParamTrasf) {
        this.dsSetParamTrasf = dsSetParamTrasf;
    }

    @Column(name = "FL_SET_PARAM_ARK", columnDefinition = "char")
    public String getFlSetParamArk() {
        return flSetParamArk;
    }

    public void setFlSetParamArk(String flSetParamArk) {
        this.flSetParamArk = flSetParamArk;
    }

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "xfoSetParamTrasf", fetch = FetchType.LAZY)
    public List<XfoParamTrasf> getXfoParamTrasfList() {
        return xfoParamTrasfList;
    }

    public void setXfoParamTrasfList(List<XfoParamTrasf> xfoParamTrasfList) {
        this.xfoParamTrasfList = xfoParamTrasfList;
    }

    @JoinColumn(name = "ID_TRASF", referencedColumnName = "ID_TRASF")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    public XfoTrasf getXfoTrasf() {
        return xfoTrasf;
    }

    public void setXfoTrasf(XfoTrasf xfoTrasf) {
        this.xfoTrasf = xfoTrasf;
    }

    @Override
    public String toString() {
        return "it.eng.xformer.entity.XfoSetParamTrasf[ idSetParamTrasf=" + idSetParamTrasf + " ]";
    }

}
