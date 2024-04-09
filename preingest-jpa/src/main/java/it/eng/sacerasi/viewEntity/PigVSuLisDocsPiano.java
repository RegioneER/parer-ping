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

package it.eng.sacerasi.viewEntity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * The persistent class for the PIG_V_SU_LIS_DOCS_PIANO database table.
 */
@Entity
@Table(name = "PIG_V_SU_LIS_DOCS_PIANO")
@NamedQuery(name = "PigVSuLisDocsPiano.findAll", query = "SELECT p FROM PigVSuLisDocsPiano p")
public class PigVSuLisDocsPiano implements Serializable {

    private static final long serialVersionUID = 1L;

    private String flDocObbligatorio;

    private String flDocPrincipale;

    private String tiTipoAttoComune;

    private String tiTipoAttoUnione;

    public PigVSuLisDocsPiano() {
    }

    @Column(name = "FL_DOC_OBBLIGATORIO", columnDefinition = "char")
    public String getFlDocObbligatorio() {
        return flDocObbligatorio;
    }

    public void setFlDocObbligatorio(String flDocObbligatorio) {
        this.flDocObbligatorio = flDocObbligatorio;
    }

    @Column(name = "FL_DOC_PRINCIPALE", columnDefinition = "char")
    public String getFlDocPrincipale() {
        return flDocPrincipale;
    }

    public void setFlDocPrincipale(String flDocPrincipale) {
        this.flDocPrincipale = flDocPrincipale;
    }

    @Column(name = "TI_TIPO_ATTO_COMUNE")
    public String getTiTipoAttoComune() {
        return tiTipoAttoComune;
    }

    public void setTiTipoAttoComune(String tiTipoAttoComune) {
        this.tiTipoAttoComune = tiTipoAttoComune;
    }

    @Column(name = "TI_TIPO_ATTO_UNIONE")
    public String getTiTipoAttoUnione() {
        return tiTipoAttoUnione;
    }

    public void setTiTipoAttoUnione(String tiTipoAttoUnione) {
        this.tiTipoAttoUnione = tiTipoAttoUnione;
    }

    private PigVSuLisDocsPianoId pigVSuLisDocsPianoId;

    @EmbeddedId()
    public PigVSuLisDocsPianoId getPigVSuLisDocsPianoId() {
        return pigVSuLisDocsPianoId;
    }

    public void setPigVSuLisDocsPianoId(PigVSuLisDocsPianoId pigVSuLisDocsPianoId) {
        this.pigVSuLisDocsPianoId = pigVSuLisDocsPianoId;
    }
}
