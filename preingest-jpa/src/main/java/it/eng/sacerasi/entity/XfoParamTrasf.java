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
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.id.enhanced.SequenceStyleGenerator;

/**
 *
 * @author cek
 */
@Entity
@Table(name = "XFO_PARAM_TRASF")
@NamedQueries({ @NamedQuery(name = "XfoParamTrasf.findAll", query = "SELECT x FROM XfoParamTrasf x"),
        @NamedQuery(name = "XfoParamTrasf.findByIdParamTrasf", query = "SELECT x FROM XfoParamTrasf x WHERE x.idParamTrasf = :idParamTrasf"),
        @NamedQuery(name = "XfoParamTrasf.findByNmParamTrasf", query = "SELECT x FROM XfoParamTrasf x WHERE x.nmParamTrasf = :nmParamTrasf"),
        @NamedQuery(name = "XfoParamTrasf.findByDsParamTrasf", query = "SELECT x FROM XfoParamTrasf x WHERE x.dsParamTrasf = :dsParamTrasf"),
        @NamedQuery(name = "XfoParamTrasf.findByTiParamTrasf", query = "SELECT x FROM XfoParamTrasf x WHERE x.tiParamTrasf = :tiParamTrasf"),
        @NamedQuery(name = "XfoParamTrasf.findByDsValoreParam", query = "SELECT x FROM XfoParamTrasf x WHERE x.dsValoreParam = :dsValoreParam") })
public class XfoParamTrasf implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idParamTrasf;
    private String nmParamTrasf;
    private String dsParamTrasf;
    private String tiParamTrasf;
    private String dsValoreParam;
    private XfoSetParamTrasf xfoSetParamTrasf;

    public XfoParamTrasf() {
    }

    public XfoParamTrasf(long idParamTrasf) {
        this.idParamTrasf = idParamTrasf;
    }

    public XfoParamTrasf(long idParamTrasf, String nmParamTrasf, String dsParamTrasf, String tiParamTrasf) {
        this.idParamTrasf = idParamTrasf;
        this.nmParamTrasf = nmParamTrasf;
        this.dsParamTrasf = dsParamTrasf;
        this.tiParamTrasf = tiParamTrasf;
    }

    @Id
    @GenericGenerator(name = "XFO_PARAM_TRASF_IDVALOREPARAM_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
            @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SXFO_PARAM_TRASF"),
            @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "XFO_PARAM_TRASF_IDVALOREPARAM_GENERATOR")
    @Column(name = "ID_PARAM_TRASF")
    public Long getIdParamTrasf() {
        return idParamTrasf;
    }

    public void setIdParamTrasf(Long idParamTrasf) {
        this.idParamTrasf = idParamTrasf;
    }

    @Column(name = "NM_PARAM_TRASF")
    public String getNmParamTrasf() {
        return nmParamTrasf;
    }

    public void setNmParamTrasf(String nmParamTrasf) {
        this.nmParamTrasf = nmParamTrasf;
    }

    @Column(name = "DS_PARAM_TRASF")
    public String getDsParamTrasf() {
        return dsParamTrasf;
    }

    public void setDsParamTrasf(String dsParamTrasf) {
        this.dsParamTrasf = dsParamTrasf;
    }

    @Column(name = "TI_PARAM_TRASF")
    public String getTiParamTrasf() {
        return tiParamTrasf;
    }

    public void setTiParamTrasf(String tiParamTrasf) {
        this.tiParamTrasf = tiParamTrasf;
    }

    @Column(name = "DS_VALORE_PARAM")
    public String getDsValoreParam() {
        return dsValoreParam;
    }

    public void setDsValoreParam(String dsValoreParam) {
        this.dsValoreParam = dsValoreParam;
    }

    @JoinColumn(name = "ID_SET_PARAM_TRASF", referencedColumnName = "ID_SET_PARAM_TRASF")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    public XfoSetParamTrasf getXfoSetParamTrasf() {
        return xfoSetParamTrasf;
    }

    public void setXfoSetParamTrasf(XfoSetParamTrasf idSetParamTrasf) {
        this.xfoSetParamTrasf = idSetParamTrasf;
    }

    @Override
    public String toString() {
        return "it.eng.xformer.entity.XfoParamTrasf[ idParamTrasf=" + idParamTrasf + " ]";
    }

}
