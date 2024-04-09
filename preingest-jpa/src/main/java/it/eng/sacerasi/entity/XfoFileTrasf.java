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

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.id.enhanced.SequenceStyleGenerator;

/**
 * @author cek
 */
@Entity
@Table(name = "XFO_FILE_TRASF")
public class XfoFileTrasf implements Serializable {

    private Long idFileTrasf;
    private String nmFileTrasf;
    private byte[] blFileTrasf;
    private XfoTrasf xfoTrasf;

    @Id
    @GenericGenerator(name = "XFO_FILE_TRASF_IDFILETRASF_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
            @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SXFO_FILE_TRASF"),
            @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "XFO_FILE_TRASF_IDFILETRASF_GENERATOR")
    @Column(name = "ID_FILE_TRASF")
    public Long getIdTrasf() {
        return this.idFileTrasf;
    }

    public void setIdTrasf(Long idTrasf) {
        this.idFileTrasf = idTrasf;
    }

    @JoinColumn(name = "ID_TRASF", referencedColumnName = "ID_TRASF")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    public XfoTrasf getXfoTrasf() {
        return xfoTrasf;
    }

    public void setXfoTrasf(XfoTrasf xfoTrasf) {
        this.xfoTrasf = xfoTrasf;
    }

    @Column(name = "NM_FILE_TRASF")
    public String getNmFileTrasf() {
        return nmFileTrasf;
    }

    public void setNmFileTrasf(String nmFileTrasf) {
        this.nmFileTrasf = nmFileTrasf;
    }

    @Lob
    @Column(name = "BL_FILE_TRASF")
    public byte[] getBlFileTrasf() {
        return blFileTrasf;
    }

    public void setBlFileTrasf(byte[] blFileTrasf) {
        this.blFileTrasf = blFileTrasf;
    }
}
