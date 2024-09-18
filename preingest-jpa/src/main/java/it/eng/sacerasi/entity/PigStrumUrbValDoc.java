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
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * The persistent class for the PIG_STRUM_URB_VAL_DOC database table.
 *
 */
@Entity
@Table(name = "PIG_STRUM_URB_VAL_DOC")
public class PigStrumUrbValDoc implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long idStrumUrbValDoc;
    private String flDocPrincipale;
    private String nmTipoDocumento;
    private List<PigStrumUrbDocumenti> pigStrumUrbDocumentis = new ArrayList<>();
    private List<PigStrumUrbPianoDocReq> pigStrumUrbPianoDocReqs = new ArrayList<>();

    public PigStrumUrbValDoc() {
        // for Hibernate
    }

    @Id
    @Column(name = "ID_STRUM_URB_VAL_DOC")
    public Long getIdStrumUrbValDoc() {
        return this.idStrumUrbValDoc;
    }

    public void setIdStrumUrbValDoc(Long idStrumUrbValDoc) {
        this.idStrumUrbValDoc = idStrumUrbValDoc;
    }

    @Column(name = "FL_DOC_PRINCIPALE", columnDefinition = "char")
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

    // bi-directional many-to-one association to PigStrumUrbDocumenti
    @OneToMany(mappedBy = "pigStrumUrbValDoc")
    public List<PigStrumUrbDocumenti> getPigStrumUrbDocumentis() {
        return this.pigStrumUrbDocumentis;
    }

    public void setPigStrumUrbDocumentis(List<PigStrumUrbDocumenti> pigStrumUrbDocumentis) {
        this.pigStrumUrbDocumentis = pigStrumUrbDocumentis;
    }

    public PigStrumUrbDocumenti addPigStrumUrbDocumenti(PigStrumUrbDocumenti pigStrumUrbDocumenti) {
        getPigStrumUrbDocumentis().add(pigStrumUrbDocumenti);
        pigStrumUrbDocumenti.setPigStrumUrbValDoc(this);

        return pigStrumUrbDocumenti;
    }

    public PigStrumUrbDocumenti removePigStrumUrbDocumenti(PigStrumUrbDocumenti pigStrumUrbDocumenti) {
        getPigStrumUrbDocumentis().remove(pigStrumUrbDocumenti);
        pigStrumUrbDocumenti.setPigStrumUrbValDoc(null);

        return pigStrumUrbDocumenti;
    }

    // bi-directional many-to-one association to PigStrumUrbPianoDocReq
    @OneToMany(mappedBy = "pigStrumUrbValDoc")
    public List<PigStrumUrbPianoDocReq> getPigStrumUrbPianoDocReqs() {
        return this.pigStrumUrbPianoDocReqs;
    }

    public void setPigStrumUrbPianoDocReqs(List<PigStrumUrbPianoDocReq> pigStrumUrbPianoDocReqs) {
        this.pigStrumUrbPianoDocReqs = pigStrumUrbPianoDocReqs;
    }

    public PigStrumUrbPianoDocReq addPigStrumUrbPianoDocReq(PigStrumUrbPianoDocReq pigStrumUrbPianoDocReq) {
        getPigStrumUrbPianoDocReqs().add(pigStrumUrbPianoDocReq);
        pigStrumUrbPianoDocReq.setPigStrumUrbValDoc(this);

        return pigStrumUrbPianoDocReq;
    }

    public PigStrumUrbPianoDocReq removePigStrumUrbPianoDocReq(PigStrumUrbPianoDocReq pigStrumUrbPianoDocReq) {
        getPigStrumUrbPianoDocReqs().remove(pigStrumUrbPianoDocReq);
        pigStrumUrbPianoDocReq.setPigStrumUrbValDoc(null);

        return pigStrumUrbPianoDocReq;
    }

}
