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
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * The persistent class for the PIG_V_LIS_STRUT_VERS_SACER database table.
 */
@Entity
@Table(name = "PIG_V_LIS_STRUT_VERS_SACER")
public class PigVLisStrutVersSacer implements Serializable {

    private static final long serialVersionUID = 1L;

    private String cdPasswordSacer;

    private String dlCompositoOrganizStrut;

    private BigDecimal idOrganizIamDich;

    private BigDecimal idTipoObject;

    private BigDecimal idVers;

    private String nmOrganizIamStrut;

    private String nmUseridSacer;

    private String tiDichVers;

    public PigVLisStrutVersSacer() {
    }

    public PigVLisStrutVersSacer(BigDecimal idOrganizIamStrut, String nmUseridSacer, String cdPasswordSacer) {
        this.pigVLisStrutVersSacerId = new PigVLisStrutVersSacerId();
        this.cdPasswordSacer = cdPasswordSacer;
        this.pigVLisStrutVersSacerId.setIdOrganizIamStrut(idOrganizIamStrut);
        this.nmUseridSacer = nmUseridSacer;
    }

    public PigVLisStrutVersSacer(BigDecimal idOrganizIamStrut, String nmUseridSacer) {
        this.pigVLisStrutVersSacerId = new PigVLisStrutVersSacerId();
        this.pigVLisStrutVersSacerId.setIdOrganizIamStrut(idOrganizIamStrut);
        this.nmUseridSacer = nmUseridSacer;
    }

    @Column(name = "CD_PASSWORD_SACER")
    public String getCdPasswordSacer() {
        return this.cdPasswordSacer;
    }

    public void setCdPasswordSacer(String cdPasswordSacer) {
        this.cdPasswordSacer = cdPasswordSacer;
    }

    @Column(name = "DL_COMPOSITO_ORGANIZ_STRUT")
    public String getDlCompositoOrganizStrut() {
        return this.dlCompositoOrganizStrut;
    }

    public void setDlCompositoOrganizStrut(String dlCompositoOrganizStrut) {
        this.dlCompositoOrganizStrut = dlCompositoOrganizStrut;
    }

    @Column(name = "ID_ORGANIZ_IAM_DICH")
    public BigDecimal getIdOrganizIamDich() {
        return this.idOrganizIamDich;
    }

    public void setIdOrganizIamDich(BigDecimal idOrganizIamDich) {
        this.idOrganizIamDich = idOrganizIamDich;
    }

    @Column(name = "ID_TIPO_OBJECT")
    public BigDecimal getIdTipoObject() {
        return this.idTipoObject;
    }

    public void setIdTipoObject(BigDecimal idTipoObject) {
        this.idTipoObject = idTipoObject;
    }

    @Column(name = "ID_VERS")
    public BigDecimal getIdVers() {
        return this.idVers;
    }

    public void setIdVers(BigDecimal idVers) {
        this.idVers = idVers;
    }

    @Column(name = "NM_ORGANIZ_IAM_STRUT")
    public String getNmOrganizIamStrut() {
        return this.nmOrganizIamStrut;
    }

    public void setNmOrganizIamStrut(String nmOrganizIamStrut) {
        this.nmOrganizIamStrut = nmOrganizIamStrut;
    }

    @Column(name = "NM_USERID_SACER")
    public String getNmUseridSacer() {
        return this.nmUseridSacer;
    }

    public void setNmUseridSacer(String nmUseridSacer) {
        this.nmUseridSacer = nmUseridSacer;
    }

    @Column(name = "TI_DICH_VERS")
    public String getTiDichVers() {
        return this.tiDichVers;
    }

    public void setTiDichVers(String tiDichVers) {
        this.tiDichVers = tiDichVers;
    }

    private PigVLisStrutVersSacerId pigVLisStrutVersSacerId;

    @EmbeddedId()
    public PigVLisStrutVersSacerId getPigVLisStrutVersSacerId() {
        return pigVLisStrutVersSacerId;
    }

    public void setPigVLisStrutVersSacerId(PigVLisStrutVersSacerId pigVLisStrutVersSacerId) {
        this.pigVLisStrutVersSacerId = pigVLisStrutVersSacerId;
    }
}
