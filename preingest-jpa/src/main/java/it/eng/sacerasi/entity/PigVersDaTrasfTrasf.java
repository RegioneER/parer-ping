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
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.id.enhanced.SequenceStyleGenerator;

/**
 * The persistent class for the PIG_VERS_DA_TRASF_TRASF database table.
 *
 */
/*
 * @Entity
 * 
 * @Table(name = "PIG_VERS_DA_TRASF_TRASF")
 * 
 * @NamedQuery(name = "PigVersDaTrasfTrasf.findAll", query = "SELECT p FROM PigVersDaTrasfTrasf p")
 *
 */

/**
 * @deprecated non più usata
 */
@Deprecated
public class PigVersDaTrasfTrasf implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long idVersDaTrasfTrasf;
    private PigVers pigVersDaTrasf;
    private PigVers pigVersTrasf;

    public PigVersDaTrasfTrasf() {
        // hibernate
    }

    @Id
    @GenericGenerator(name = "PIG_VERS_DA_TRASF_TRASF_IDVERSDATRASFTRASF_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
            @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SPIG_VERS_DA_TRASF_TRASF"),
            @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PIG_VERS_DA_TRASF_TRASF_IDVERSDATRASFTRASF_GENERATOR")
    @Column(name = "ID_VERS_DA_TRASF_TRASF")
    public Long getIdVersDaTrasfTrasf() {
        return this.idVersDaTrasfTrasf;
    }

    public void setIdVersDaTrasfTrasf(Long idVersDaTrasfTrasf) {
        this.idVersDaTrasfTrasf = idVersDaTrasfTrasf;
    }

    // bi-directional many-to-one association to PigVer
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_VERS_DA_TRASF")
    public PigVers getPigVersDaTrasf() {
        return this.pigVersDaTrasf;
    }

    public void setPigVersDaTrasf(PigVers pigVersDaTrasf) {
        this.pigVersDaTrasf = pigVersDaTrasf;
    }

    // bi-directional many-to-one association to PigVer
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_VERS_TRASF")
    public PigVers getPigVersTrasf() {
        return this.pigVersTrasf;
    }

    public void setPigVersTrasf(PigVers pigVersTrasf) {
        this.pigVersTrasf = pigVersTrasf;
    }

}
