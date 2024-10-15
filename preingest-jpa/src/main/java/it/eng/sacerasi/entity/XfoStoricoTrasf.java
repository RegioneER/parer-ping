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
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.id.enhanced.SequenceStyleGenerator;

/**
 *
 * @author cek
 */
@Entity
@Table(name = "XFO_STORICO_TRASF")
@XmlRootElement
public class XfoStoricoTrasf implements Serializable {

    private static final long serialVersionUID = 1L;
    // @Max(value=?) @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce
    // field validation
    private Long idStoricoTrasf;
    private String cdTrasf;
    private String cdVersione;
    private String dsVersione;
    private Date dtIstituz;
    private Date dtSoppres;
    private byte[] blTrasf;
    private XfoTrasf xfoTrasf;

    public XfoStoricoTrasf() {
    }

    public XfoStoricoTrasf(long idStoricoTrasf) {
        this.idStoricoTrasf = idStoricoTrasf;
    }

    public XfoStoricoTrasf(long idStoricoTrasf, String cdTrasf, String cdVersione, String dsVersione, Date dtIstituz,
            Date dtSoppres, byte[] blTrasf) {
        this.idStoricoTrasf = idStoricoTrasf;
        this.cdTrasf = cdTrasf;
        this.cdVersione = cdVersione;
        this.dsVersione = dsVersione;
        this.dtIstituz = dtIstituz;
        this.dtSoppres = dtSoppres;
        this.blTrasf = blTrasf;
    }

    @Id
    @GenericGenerator(name = "XFO_STORICO_TRASF_IDTRASF_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
            @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SXFO_STORICO_TRASF"),
            @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "XFO_STORICO_TRASF_IDTRASF_GENERATOR")
    @Column(name = "ID_STORICO_TRASF")
    public Long getIdStoricoTrasf() {
        return idStoricoTrasf;
    }

    public void setIdStoricoTrasf(Long idStoricoTrasf) {
        this.idStoricoTrasf = idStoricoTrasf;
    }

    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "CD_TRASF")
    public String getCdTrasf() {
        return cdTrasf;
    }

    public void setCdTrasf(String cdTrasf) {
        this.cdTrasf = cdTrasf;
    }

    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "CD_VERSIONE")
    public String getCdVersione() {
        return cdVersione;
    }

    public void setCdVersione(String cdVersione) {
        this.cdVersione = cdVersione;
    }

    @NotNull
    @Size(min = 1, max = 4000)
    @Column(name = "DS_VERSIONE")
    public String getDsVersione() {
        return dsVersione;
    }

    public void setDsVersione(String dsVersione) {
        this.dsVersione = dsVersione;
    }

    @NotNull
    @Column(name = "DT_ISTITUZ")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getDtIstituz() {
        return dtIstituz;
    }

    public void setDtIstituz(Date dtIstituz) {
        this.dtIstituz = dtIstituz;
    }

    @NotNull
    @Column(name = "DT_SOPPRES")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getDtSoppres() {
        return dtSoppres;
    }

    public void setDtSoppres(Date dtSoppres) {
        this.dtSoppres = dtSoppres;
    }

    @Lob
    @Column(name = "BL_TRASF")
    public byte[] getBlTrasf() {
        return blTrasf;
    }

    public void setBlTrasf(byte[] blTrasf) {
        this.blTrasf = blTrasf;
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
        return "it.eng.xformer.entity.XfoStoricoTrasf[ idStoricoTrasf=" + idStoricoTrasf + " ]";
    }

}
