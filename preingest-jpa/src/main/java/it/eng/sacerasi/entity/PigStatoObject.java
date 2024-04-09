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
 * The persistent class for the PIG_STATO_OBJECT database table.
 *
 */
@Entity
@Table(name = "PIG_STATO_OBJECT")
@NamedQuery(name = "PigStatoObject.findAll", query = "SELECT p FROM PigStatoObject p")
public class PigStatoObject implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long idStatoObject;
    private String dsTiStatoObject;
    private String tiStatoObject;
    private List<PigStatoClasseErrore> pigStatoClasseErrores = new ArrayList<>();

    public PigStatoObject() {
        // for Hibernate
    }

    @Id
    @GenericGenerator(name = "PIG_STATO_OBJECT_IDSTATOOBJECT_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
            @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SPIG_STATO_OBJECT"),
            @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PIG_STATO_OBJECT_IDSTATOOBJECT_GENERATOR")
    @Column(name = "ID_STATO_OBJECT")
    public Long getIdStatoObject() {
        return this.idStatoObject;
    }

    public void setIdStatoObject(Long idStatoObject) {
        this.idStatoObject = idStatoObject;
    }

    @Column(name = "DS_TI_STATO_OBJECT")
    public String getDsTiStatoObject() {
        return this.dsTiStatoObject;
    }

    public void setDsTiStatoObject(String dsTiStatoObject) {
        this.dsTiStatoObject = dsTiStatoObject;
    }

    @Column(name = "TI_STATO_OBJECT")
    public String getTiStatoObject() {
        return this.tiStatoObject;
    }

    public void setTiStatoObject(String tiStatoObject) {
        this.tiStatoObject = tiStatoObject;
    }

    // bi-directional many-to-one association to PigStatoClasseErrore
    @OneToMany(mappedBy = "pigStatoObject")
    public List<PigStatoClasseErrore> getPigStatoClasseErrores() {
        return this.pigStatoClasseErrores;
    }

    public void setPigStatoClasseErrores(List<PigStatoClasseErrore> pigStatoClasseErrores) {
        this.pigStatoClasseErrores = pigStatoClasseErrores;
    }

    public PigStatoClasseErrore addPigStatoClasseErrore(PigStatoClasseErrore pigStatoClasseErrore) {
        getPigStatoClasseErrores().add(pigStatoClasseErrore);
        pigStatoClasseErrore.setPigStatoObject(this);

        return pigStatoClasseErrore;
    }

    public PigStatoClasseErrore removePigStatoClasseErrore(PigStatoClasseErrore pigStatoClasseErrore) {
        getPigStatoClasseErrores().remove(pigStatoClasseErrore);
        pigStatoClasseErrore.setPigStatoObject(null);

        return pigStatoClasseErrore;
    }

}
