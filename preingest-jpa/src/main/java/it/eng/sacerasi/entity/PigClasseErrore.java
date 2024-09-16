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
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.id.enhanced.SequenceStyleGenerator;

/**
 * The persistent class for the PIG_CLASSE_ERRORE database table.
 *
 */
@Entity
@Table(name = "PIG_CLASSE_ERRORE")
@NamedQuery(name = "PigClasseErrore.findAll", query = "SELECT p FROM PigClasseErrore p")
public class PigClasseErrore implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long idClasseErrore;
    private String cdClasseErrore;
    private String dsClasseErrore;
    private List<PigErrore> pigErrores = new ArrayList<>();
    private List<PigStatoClasseErrore> pigStatoClasseErrores = new ArrayList<>();

    public PigClasseErrore() {
        // for Hibernate
    }

    @Id
    @GenericGenerator(name = "PIG_CLASSE_ERRORE_IDCLASSEERRORE_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
            @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SPIG_CLASSE_ERRORE"),
            @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PIG_CLASSE_ERRORE_IDCLASSEERRORE_GENERATOR")
    @Column(name = "ID_CLASSE_ERRORE")
    public Long getIdClasseErrore() {
        return this.idClasseErrore;
    }

    public void setIdClasseErrore(Long idClasseErrore) {
        this.idClasseErrore = idClasseErrore;
    }

    @Column(name = "CD_CLASSE_ERRORE")
    public String getCdClasseErrore() {
        return this.cdClasseErrore;
    }

    public void setCdClasseErrore(String cdClasseErrore) {
        this.cdClasseErrore = cdClasseErrore;
    }

    @Column(name = "DS_CLASSE_ERRORE")
    public String getDsClasseErrore() {
        return this.dsClasseErrore;
    }

    public void setDsClasseErrore(String dsClasseErrore) {
        this.dsClasseErrore = dsClasseErrore;
    }

    // bi-directional many-to-one association to PigErrore
    @OneToMany(mappedBy = "pigClasseErrore")
    public List<PigErrore> getPigErrores() {
        return this.pigErrores;
    }

    public void setPigErrores(List<PigErrore> pigErrores) {
        this.pigErrores = pigErrores;
    }

    public PigErrore addPigErrore(PigErrore pigErrore) {
        getPigErrores().add(pigErrore);
        pigErrore.setPigClasseErrore(this);

        return pigErrore;
    }

    public PigErrore removePigErrore(PigErrore pigErrore) {
        getPigErrores().remove(pigErrore);
        pigErrore.setPigClasseErrore(null);

        return pigErrore;
    }

    // bi-directional many-to-one association to PigStatoClasseErrore
    @OneToMany(mappedBy = "pigClasseErrore")
    public List<PigStatoClasseErrore> getPigStatoClasseErrores() {
        return this.pigStatoClasseErrores;
    }

    public void setPigStatoClasseErrores(List<PigStatoClasseErrore> pigStatoClasseErrores) {
        this.pigStatoClasseErrores = pigStatoClasseErrores;
    }

    public PigStatoClasseErrore addPigStatoClasseErrore(PigStatoClasseErrore pigStatoClasseErrore) {
        getPigStatoClasseErrores().add(pigStatoClasseErrore);
        pigStatoClasseErrore.setPigClasseErrore(this);

        return pigStatoClasseErrore;
    }

    public PigStatoClasseErrore removePigStatoClasseErrore(PigStatoClasseErrore pigStatoClasseErrore) {
        getPigStatoClasseErrores().remove(pigStatoClasseErrore);
        pigStatoClasseErrore.setPigClasseErrore(null);

        return pigStatoClasseErrore;
    }

}
