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
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * The persistent class for the PIG_V_SU_CALCOLA_AMBITO_TERR database table.
 *
 */
@Entity
@Table(name = "PIG_V_SU_CALCOLA_AMBITO_TERR")
public class PigVSuCalcolaAmbitoTerr implements Serializable {

    public enum Tipologia {
	COMUNE, UNIONE
    }

    private static final long serialVersionUID = 1L;

    private BigDecimal idVers;
    private String denominazione;
    private Tipologia tipologia;
    private String unione;
    private String provincia;

    public PigVSuCalcolaAmbitoTerr() {
    }

    @Id
    @Column(name = "ID_VERS")
    public BigDecimal getIdVers() {
	return idVers;
    }

    public void setIdVers(BigDecimal idVers) {
	this.idVers = idVers;
    }

    @Column(name = "DENOMINAZIONE")
    public String getDenominazione() {
	return denominazione;
    }

    public void setDenominazione(String denominazione) {
	this.denominazione = denominazione;
    }

    @Column(name = "UNIONE")
    public String getUnione() {
	return unione;
    }

    public void setUnione(String unione) {
	this.unione = unione;
    }

    @Column(name = "TIPOLOGIA", columnDefinition = "char")
    @Enumerated(EnumType.STRING)
    public Tipologia getTipologia() {
	return tipologia;
    }

    public void setTipologia(Tipologia tipologia) {
	this.tipologia = tipologia;
    }

    @Column(name = "PROVINCIA")
    public String getProvincia() {
	return provincia;
    }

    public void setProvincia(String provincia) {
	this.provincia = provincia;
    }

}
