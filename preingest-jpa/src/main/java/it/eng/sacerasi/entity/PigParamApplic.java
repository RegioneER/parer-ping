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
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.id.enhanced.SequenceStyleGenerator;

/**
 * The persistent class for the PIG_PARAM_APPLIC database table.
 *
 */
@Entity
@Table(name = "PIG_PARAM_APPLIC")
public class PigParamApplic implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long idParamApplic;
    private String cdVersioneAppIni;
    private String cdVersioneAppFine;
    private String dmParamApplic;
    private String dsListaValoriAmmessi;
    private String dsParamApplic;
    private String flAppartAmbiente;
    private String flAppartApplic;
    private String flAppartTipoOggetto;
    private String flAppartVers;
    private String nmParamApplic;
    private String tiGestioneParam;
    private String tiParamApplic;
    private String tiValoreParamApplic;
    private List<PigValoreParamApplic> pigValoreParamApplics = new ArrayList<>();

    public PigParamApplic() {
        /** empty **/
    }

    @Id
    @GenericGenerator(name = "PIG_PARAM_APPLIC_IDPARAMAPPLIC_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
            @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SPIG_PARAM_APPLIC"),
            @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PIG_PARAM_APPLIC_IDPARAMAPPLIC_GENERATOR")
    @Column(name = "ID_PARAM_APPLIC")
    public Long getIdParamApplic() {
        return this.idParamApplic;
    }

    public void setIdParamApplic(Long idParamApplic) {
        this.idParamApplic = idParamApplic;
    }

    @Column(name = "CD_VERSIONE_APP_INI")
    public String getCdVersioneAppIni() {
        return this.cdVersioneAppIni;
    }

    public void setCdVersioneAppIni(String cdVersioneAppIni) {
        this.cdVersioneAppIni = cdVersioneAppIni;
    }

    @Column(name = "CD_VERSIONE_APP_FINE")
    public String getCdVersioneAppFine() {
        return this.cdVersioneAppFine;
    }

    public void setCdVersioneAppFine(String cdVersioneAppFine) {
        this.cdVersioneAppFine = cdVersioneAppFine;
    }

    @Column(name = "DM_PARAM_APPLIC")
    public String getDmParamApplic() {
        return this.dmParamApplic;
    }

    public void setDmParamApplic(String dmParamApplic) {
        this.dmParamApplic = dmParamApplic;
    }

    @Column(name = "DS_LISTA_VALORI_AMMESSI")
    public String getDsListaValoriAmmessi() {
        return this.dsListaValoriAmmessi;
    }

    public void setDsListaValoriAmmessi(String dsListaValoriAmmessi) {
        this.dsListaValoriAmmessi = dsListaValoriAmmessi;
    }

    @Column(name = "DS_PARAM_APPLIC")
    public String getDsParamApplic() {
        return this.dsParamApplic;
    }

    public void setDsParamApplic(String dsParamApplic) {
        this.dsParamApplic = dsParamApplic;
    }

    @Column(name = "FL_APPART_AMBIENTE", columnDefinition = "char")
    public String getFlAppartAmbiente() {
        return this.flAppartAmbiente;
    }

    public void setFlAppartAmbiente(String flAppartAmbiente) {
        this.flAppartAmbiente = flAppartAmbiente;
    }

    @Column(name = "FL_APPART_APPLIC", columnDefinition = "char")
    public String getFlAppartApplic() {
        return this.flAppartApplic;
    }

    public void setFlAppartApplic(String flAppartApplic) {
        this.flAppartApplic = flAppartApplic;
    }

    @Column(name = "FL_APPART_TIPO_OGGETTO", columnDefinition = "char")
    public String getFlAppartTipoOggetto() {
        return this.flAppartTipoOggetto;
    }

    public void setFlAppartTipoOggetto(String flAppartTipoOggetto) {
        this.flAppartTipoOggetto = flAppartTipoOggetto;
    }

    @Column(name = "FL_APPART_VERS", columnDefinition = "char")
    public String getFlAppartVers() {
        return this.flAppartVers;
    }

    public void setFlAppartVers(String flAppartVers) {
        this.flAppartVers = flAppartVers;
    }

    @Column(name = "NM_PARAM_APPLIC")
    public String getNmParamApplic() {
        return this.nmParamApplic;
    }

    public void setNmParamApplic(String nmParamApplic) {
        this.nmParamApplic = nmParamApplic;
    }

    @Column(name = "TI_GESTIONE_PARAM")
    public String getTiGestioneParam() {
        return this.tiGestioneParam;
    }

    public void setTiGestioneParam(String tiGestioneParam) {
        this.tiGestioneParam = tiGestioneParam;
    }

    @Column(name = "TI_PARAM_APPLIC")
    public String getTiParamApplic() {
        return this.tiParamApplic;
    }

    public void setTiParamApplic(String tiParamApplic) {
        this.tiParamApplic = tiParamApplic;
    }

    // MEV22933
    @Column(name = "TI_VALORE_PARAM_APPLIC")
    public String getTiValoreParamApplic() {
        return tiValoreParamApplic;
    }

    public void setTiValoreParamApplic(String tiValoreParamApplic) {
        this.tiValoreParamApplic = tiValoreParamApplic;
    }

    // bi-directional many-to-one association to PigValoreParamApplic
    @OneToMany(mappedBy = "pigParamApplic")
    public List<PigValoreParamApplic> getPigValoreParamApplics() {
        return this.pigValoreParamApplics;
    }

    public void setPigValoreParamApplics(List<PigValoreParamApplic> pigValoreParamApplics) {
        this.pigValoreParamApplics = pigValoreParamApplics;
    }

    public PigValoreParamApplic addPigValoreParamApplic(PigValoreParamApplic pigValoreParamApplic) {
        getPigValoreParamApplics().add(pigValoreParamApplic);
        pigValoreParamApplic.setPigParamApplic(this);

        return pigValoreParamApplic;
    }

    public PigValoreParamApplic removePigValoreParamApplic(PigValoreParamApplic pigValoreParamApplic) {
        getPigValoreParamApplics().remove(pigValoreParamApplic);
        pigValoreParamApplic.setPigParamApplic(null);

        return pigValoreParamApplic;
    }

}
