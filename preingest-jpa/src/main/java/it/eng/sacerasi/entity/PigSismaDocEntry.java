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
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * The persistent class for the PIG_SISMA_DOC_ENTRY database table.
 *
 */
@Entity
@Table(name = "PIG_SISMA_DOC_ENTRY")
public class PigSismaDocEntry implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idSismaDocEntry;
    private String nmEntry;
    private PigSismaDocumenti pigSismaDocumenti;

    public PigSismaDocEntry() {
    }

    @Id
    @SequenceGenerator(name = "PIG_SISMA_DOC_ENTRY_IDSISMADOCENTRY_GENERATOR", sequenceName = "SPIG_SISMA_DOC_ENTRY", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PIG_SISMA_DOC_ENTRY_IDSISMADOCENTRY_GENERATOR")
    @Column(name = "ID_SISMA_DOC_ENTRY")
    public Long getIdSismaDocEntry() {
	return idSismaDocEntry;
    }

    public void setIdSismaDocEntry(Long idSismaDocEntry) {
	this.idSismaDocEntry = idSismaDocEntry;
    }

    @Column(name = "NM_ENTRY")
    public String getNmEntry() {
	return nmEntry;
    }

    public void setNmEntry(String nmEntry) {
	this.nmEntry = nmEntry;
    }

    // bi-directional many-to-one association to PigSisma
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_SISMA_DOCUMENTI")
    public PigSismaDocumenti getPigSismaDocumenti() {
	return pigSismaDocumenti;
    }

    public void setPigSismaDocumenti(PigSismaDocumenti pigSismaDocumenti) {
	this.pigSismaDocumenti = pigSismaDocumenti;
    }

}
