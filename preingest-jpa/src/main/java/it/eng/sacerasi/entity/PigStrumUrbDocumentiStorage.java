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
import javax.persistence.OneToOne;
import javax.persistence.Table;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.id.enhanced.SequenceStyleGenerator;

@Entity
@Table(name = "PIG_STRUM_URB_DOCUMENTI_STORAGE")
public class PigStrumUrbDocumentiStorage implements Serializable {

    private static final long serialVersionUID = 37568565653844L;

    private Long idPigStrumUrbDocumentiStorage;
    private Long idDecBackend;
    private String nmTenant;
    private String nmBucket;
    private String cdKeyFile;
    private PigStrumUrbDocumenti pigStrumUrbDocumenti;

    public PigStrumUrbDocumentiStorage() {
	super();
    }

    @Id
    @GenericGenerator(name = "PIG_STRUM_URB_DOCUMENTI_STORAGE_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
	    @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SPIG_STRUM_URB_DOCUMENTI_STORAGE"),
	    @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PIG_STRUM_URB_DOCUMENTI_STORAGE_GENERATOR")
    @Column(name = "ID_PIG_STRUM_URB_DOCUMENTI_STORAGE")
    public Long getIdPigStrumUrbDocumentiStorage() {
	return idPigStrumUrbDocumentiStorage;
    }

    public void setIdPigStrumUrbDocumentiStorage(Long idPigStrumUrbDocumentiStorage) {
	this.idPigStrumUrbDocumentiStorage = idPigStrumUrbDocumentiStorage;
    }

    @Column(name = "ID_DEC_BACKEND")
    public Long getIdDecBackend() {
	return idDecBackend;
    }

    public void setIdDecBackend(Long idDecBackend) {
	this.idDecBackend = idDecBackend;
    }

    @Column(name = "NM_TENANT")
    public String getNmTenant() {
	return nmTenant;
    }

    public void setNmTenant(String nmTenant) {
	this.nmTenant = nmTenant;
    }

    @Column(name = "NM_BUCKET")
    public String getNmBucket() {
	return nmBucket;
    }

    public void setNmBucket(String nmBucket) {
	this.nmBucket = nmBucket;
    }

    @Column(name = "CD_KEY_FILE")
    public String getCdKeyFile() {
	return cdKeyFile;
    }

    public void setCdKeyFile(String cdKeyFile) {
	this.cdKeyFile = cdKeyFile;
    }

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_STRUM_URB_DOCUMENTI")
    public PigStrumUrbDocumenti getPigStrumUrbDocumenti() {
	return pigStrumUrbDocumenti;
    }

    public void setPigStrumUrbDocumenti(PigStrumUrbDocumenti pigStrumUrbDocumenti) {
	this.pigStrumUrbDocumenti = pigStrumUrbDocumenti;
    }
}