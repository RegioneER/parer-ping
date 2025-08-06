/*
 * Engineering Ingegneria Informatica S.p.A.
 *
 * Copyright (C) 2023 Regione Emilia-Romagna <p/> This program is free software: you can
 * redistribute it and/or modify it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version. <p/> This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Affero General Public License for more details. <p/> You should
 * have received a copy of the GNU Affero General Public License along with this program. If not,
 * see <https://www.gnu.org/licenses/>.
 */

package it.eng.sacerasi.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * The persistent class for the PIG_OBJECT database table.
 *
 */
@Entity
@Table(name = "PIG_PRIORITA_OBJECT")
public class PigPrioritaObject implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long idPrioritaObject;
    private PigObject pigObject;
    private LocalDateTime dtModifica;
    private String nmUser;
    private String tiPrioritaVersamento;

    public PigPrioritaObject() {
	// for Hibernate
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_PRIORITA_OBJECT")
    public Long getIdPrioritaObject() {
	return this.idPrioritaObject;
    }

    public void setIdPrioritaObject(Long idPrioritaObject) {
	this.idPrioritaObject = idPrioritaObject;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_OBJECT")
    public PigObject getPigObject() {
	return this.pigObject;
    }

    public void setPigObject(PigObject pigObject) {
	this.pigObject = pigObject;
    }

    @Column(name = "DT_MODIFICA")
    public LocalDateTime getDtModifica() {
	return dtModifica;
    }

    public void setDtModifica(LocalDateTime dtModifica) {
	this.dtModifica = dtModifica;
    }

    @Column(name = "NM_USER")
    public String getNmUser() {
	return nmUser;
    }

    public void setNmUser(String nmUser) {
	this.nmUser = nmUser;
    }

    @Column(name = "TI_PRIORITA_VERSAMENTO")
    public String getTiPrioritaVersamento() {
	return tiPrioritaVersamento;
    }

    public void setTiPrioritaVersamento(String tiPrioritaVersamento) {
	this.tiPrioritaVersamento = tiPrioritaVersamento;
    }
}
