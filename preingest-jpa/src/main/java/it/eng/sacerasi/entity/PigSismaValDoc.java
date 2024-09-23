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
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * The persistent class for the PIG_SISMA_VAL_DOC database table.
 *
 */
@Entity
@Table(name = "PIG_SISMA_VAL_DOC")
public class PigSismaValDoc implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long idSismaValDoc;
    private String flDocPrincipale;
    private String nmTipoDocumento;
    private List<PigSismaDocumenti> pigSismaDocumentis = new ArrayList<>();
    private List<PigSismaPianoDocReq> pigSismaPianoDocReqs = new ArrayList<>();

    public PigSismaValDoc() {
        // non usato
    }

    @Id
    @SequenceGenerator(name = "PIG_SISMA_VAL_DOC_IDSISMAVALDOC_GENERATOR", sequenceName = "SPIG_SISMA_VAL_DOC", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PIG_SISMA_VAL_DOC_IDSISMAVALDOC_GENERATOR")
    @Column(name = "ID_SISMA_VAL_DOC")
    public Long getIdSismaValDoc() {
        return this.idSismaValDoc;
    }

    public void setIdSismaValDoc(Long idSismaValDoc) {
        this.idSismaValDoc = idSismaValDoc;
    }

    @Column(name = "FL_DOC_PRINCIPALE", columnDefinition = "CHAR")
    public String getFlDocPrincipale() {
        return this.flDocPrincipale;
    }

    public void setFlDocPrincipale(String flDocPrincipale) {
        this.flDocPrincipale = flDocPrincipale;
    }

    @Column(name = "NM_TIPO_DOCUMENTO")
    public String getNmTipoDocumento() {
        return this.nmTipoDocumento;
    }

    public void setNmTipoDocumento(String nmTipoDocumento) {
        this.nmTipoDocumento = nmTipoDocumento;
    }

    // bi-directional many-to-one association to PigSismaDocumenti
    @OneToMany(mappedBy = "pigSismaValDoc")
    public List<PigSismaDocumenti> getPigSismaDocumentis() {
        return this.pigSismaDocumentis;
    }

    public void setPigSismaDocumentis(List<PigSismaDocumenti> pigSismaDocumentis) {
        this.pigSismaDocumentis = pigSismaDocumentis;
    }

    public PigSismaDocumenti addPigSismaDocumenti(PigSismaDocumenti pigSismaDocumenti) {
        getPigSismaDocumentis().add(pigSismaDocumenti);
        pigSismaDocumenti.setPigSismaValDoc(this);

        return pigSismaDocumenti;
    }

    public PigSismaDocumenti removePigSismaDocumenti(PigSismaDocumenti pigSismaDocumenti) {
        getPigSismaDocumentis().remove(pigSismaDocumenti);
        pigSismaDocumenti.setPigSismaValDoc(null);

        return pigSismaDocumenti;
    }

    // bi-directional many-to-one association to PigSismaPianoDocReq
    @OneToMany(mappedBy = "pigSismaValDoc")
    public List<PigSismaPianoDocReq> getPigSismaPianoDocReqs() {
        return this.pigSismaPianoDocReqs;
    }

    public void setPigSismaPianoDocReqs(List<PigSismaPianoDocReq> pigSismaPianoDocReqs) {
        this.pigSismaPianoDocReqs = pigSismaPianoDocReqs;
    }

    public PigSismaPianoDocReq addPigSismaPianoDocReq(PigSismaPianoDocReq pigSismaPianoDocReq) {
        getPigSismaPianoDocReqs().add(pigSismaPianoDocReq);
        pigSismaPianoDocReq.setPigSismaValDoc(this);

        return pigSismaPianoDocReq;
    }

    public PigSismaPianoDocReq removePigSismaPianoDocReq(PigSismaPianoDocReq pigSismaPianoDocReq) {
        getPigSismaPianoDocReqs().remove(pigSismaPianoDocReq);
        pigSismaPianoDocReq.setPigSismaValDoc(null);

        return pigSismaPianoDocReq;
    }

}
