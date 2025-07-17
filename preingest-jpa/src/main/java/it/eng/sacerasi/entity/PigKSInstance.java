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

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 *
 * @author Cappelli_F
 */
@Entity
@Table(name = "PIG_KS_INSTANCE")
public class PigKSInstance implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long idKsInstance;
    private String nmKsInstance;
    private String urlKsInstance;
    private String dirKsInstance;

    @Id
    @SequenceGenerator(name = "PIG_KS_INSTANCE_IDKS_INSTANCE_GENERATOR", sequenceName = "SPIG_KS_INSTANCE", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PIG_KS_INSTANCE_IDKS_INSTANCE_GENERATOR")
    @Column(name = "ID_KS_INSTANCE")
    public Long getIdKsInstance() {
	return this.idKsInstance;
    }

    public void setIdKsInstance(Long idKsInstance) {
	this.idKsInstance = idKsInstance;
    }

    @Column(name = "NM_KS_INSTANCE")
    public String getNmKsInstance() {
	return nmKsInstance;
    }

    public void setNmKsInstance(String nmKsInstance) {
	this.nmKsInstance = nmKsInstance;
    }

    @Column(name = "URL_KS_INSTANCE")
    public String getUrlKsInstance() {
	return urlKsInstance;
    }

    public void setUrlKsInstance(String urlKsInstance) {
	this.urlKsInstance = urlKsInstance;
    }

    @Column(name = "DIR_KS_INSTANCE")
    public String getDirKsInstance() {
	return dirKsInstance;
    }

    public void setDirKsInstance(String dirKsInstance) {
	this.dirKsInstance = dirKsInstance;
    }
}
