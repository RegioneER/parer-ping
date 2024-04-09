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
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * The persistent class for the PIG_V_VALORE_SET_PARAM_TRASF database table.
 */
@Entity
@Table(name = "PIG_V_VALORE_SET_PARAM_TRASF")
@NamedQuery(name = "PigVValoreSetParamTrasf.findAll", query = "SELECT p FROM PigVValoreSetParamTrasf p")
public class PigVValoreSetParamTrasf implements Serializable {

    private static final long serialVersionUID = 1L;

    private String dsListaValoreParam;

    private BigDecimal idValoreSetParamTrasf;

    private String nmSetParamTrasf;

    public PigVValoreSetParamTrasf() {
    }

    @Column(name = "DS_LISTA_VALORE_PARAM")
    public String getDsListaValoreParam() {
        return this.dsListaValoreParam;
    }

    public void setDsListaValoreParam(String dsListaValoreParam) {
        this.dsListaValoreParam = dsListaValoreParam;
    }

    @Column(name = "ID_VALORE_SET_PARAM_TRASF")
    public BigDecimal getIdValoreSetParamTrasf() {
        return this.idValoreSetParamTrasf;
    }

    public void setIdValoreSetParamTrasf(BigDecimal idValoreSetParamTrasf) {
        this.idValoreSetParamTrasf = idValoreSetParamTrasf;
    }

    @Column(name = "NM_SET_PARAM_TRASF")
    public String getNmSetParamTrasf() {
        return this.nmSetParamTrasf;
    }

    public void setNmSetParamTrasf(String nmSetParamTrasf) {
        this.nmSetParamTrasf = nmSetParamTrasf;
    }

    private PigVValoreSetParamTrasfId pigVValoreSetParamTrasfId;

    @EmbeddedId()
    public PigVValoreSetParamTrasfId getPigVValoreSetParamTrasfId() {
        return pigVValoreSetParamTrasfId;
    }

    public void setPigVValoreSetParamTrasfId(PigVValoreSetParamTrasfId pigVValoreSetParamTrasfId) {
        this.pigVValoreSetParamTrasfId = pigVValoreSetParamTrasfId;
    }
}
