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

package it.eng.sacerasi.viewEntity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * The persistent class for the PIG_V_VALORE_PARAM_TRASF database table.
 */
@Entity
@Table(name = "PIG_V_VAL_PARAM_TRASF_DEF_SPEC")
public class PigVValParamTrasfDefSpec implements Serializable {

    private static final long serialVersionUID = 1L;

    private String dsParamTrasf;

    private BigDecimal idSetParamTrasf;

    private BigDecimal idValoreParamTrasf;

    private BigDecimal idValoreSetParamTrasf;

    private String nmParamTrasf;

    private String nmSetParamTrasf;

    private String tiParamTrasf;

    private String valParam;

    private String tiValParam;

    public PigVValParamTrasfDefSpec() {
    }

    @Column(name = "DS_PARAM_TRASF")
    public String getDsParamTrasf() {
	return this.dsParamTrasf;
    }

    public void setDsParamTrasf(String dsParamTrasf) {
	this.dsParamTrasf = dsParamTrasf;
    }

    @Column(name = "ID_SET_PARAM_TRASF")
    public BigDecimal getIdSetParamTrasf() {
	return this.idSetParamTrasf;
    }

    public void setIdSetParamTrasf(BigDecimal idSetParamTrasf) {
	this.idSetParamTrasf = idSetParamTrasf;
    }

    @Column(name = "ID_VALORE_PARAM_TRASF")
    public BigDecimal getIdValoreParamTrasf() {
	return this.idValoreParamTrasf;
    }

    public void setIdValoreParamTrasf(BigDecimal idValoreParamTrasf) {
	this.idValoreParamTrasf = idValoreParamTrasf;
    }

    @Column(name = "ID_VALORE_SET_PARAM_TRASF")
    public BigDecimal getIdValoreSetParamTrasf() {
	return this.idValoreSetParamTrasf;
    }

    public void setIdValoreSetParamTrasf(BigDecimal idValoreSetParamTrasf) {
	this.idValoreSetParamTrasf = idValoreSetParamTrasf;
    }

    @Column(name = "NM_PARAM_TRASF")
    public String getNmParamTrasf() {
	return this.nmParamTrasf;
    }

    public void setNmParamTrasf(String nmParamTrasf) {
	this.nmParamTrasf = nmParamTrasf;
    }

    @Column(name = "NM_SET_PARAM_TRASF")
    public String getNmSetParamTrasf() {
	return this.nmSetParamTrasf;
    }

    public void setNmSetParamTrasf(String nmSetParamTrasf) {
	this.nmSetParamTrasf = nmSetParamTrasf;
    }

    @Column(name = "TI_PARAM_TRASF")
    public String getTiParamTrasf() {
	return this.tiParamTrasf;
    }

    public void setTiParamTrasf(String tiParamTrasf) {
	this.tiParamTrasf = tiParamTrasf;
    }

    @Column(name = "VAL_PARAM")
    public String getValParam() {
	return this.valParam;
    }

    public void setValParam(String valParam) {
	this.valParam = valParam;
    }

    @Column(name = "TI_VAL_PARAM")
    public String getTiValParam() {
	return this.tiValParam;
    }

    public void setTiValParam(String tiValParam) {
	this.tiValParam = tiValParam;
    }

    private PigVValParamTrasfDefSpecId pigVValParamTrasfDefSpecId;

    @EmbeddedId()
    public PigVValParamTrasfDefSpecId getPigVValParamTrasfDefSpecId() {
	return pigVValParamTrasfDefSpecId;
    }

    public void setPigVValParamTrasfDefSpecId(
	    PigVValParamTrasfDefSpecId pigVValParamTrasfDefSpecId) {
	this.pigVValParamTrasfDefSpecId = pigVValParamTrasfDefSpecId;
    }
}
