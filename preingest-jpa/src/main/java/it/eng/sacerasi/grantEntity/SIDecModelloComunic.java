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

package it.eng.sacerasi.grantEntity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Auto-generated by: org.apache.openjpa.jdbc.meta.ReverseMappingTool$AnnotatedCodeGenerator edited by fioravanti_f
 */
// @Entity
// @Table(schema = "SACER_IAM", name = "DEC_MODELLO_COMUNIC")
@Deprecated
public class SIDecModelloComunic implements Serializable {

    private String blTestoComunic;
    private String cdModelloComunic;
    // private List<SIDecQueryModelloComunic> decQueryModelloComunics = new ArrayList<>();
    // private List<SIDecUsoModelloComunic> decUsoModelloComunics = new ArrayList<>();
    private String dsModelloComunic;
    private String dsOggettoComunic;
    private Date dtIstituz;
    private Date dtSoppres;
    private Long idModelloComunic;
    private String nmMittente;
    private String tiComunic;
    private String tiOggettoQuery;
    private String tiStatoTrigComunic;

    public SIDecModelloComunic() {
    }

    public SIDecModelloComunic(long idModelloComunic) {
        this.idModelloComunic = idModelloComunic;
    }

    @Basic
    @Column(name = "BL_TESTO_COMUNIC", nullable = false)
    @Lob
    public String getBlTestoComunic() {
        return blTestoComunic;
    }

    public void setBlTestoComunic(String blTestoComunic) {
        this.blTestoComunic = blTestoComunic;
    }

    @Basic
    @Column(name = "CD_MODELLO_COMUNIC", nullable = false, length = 100)
    public String getCdModelloComunic() {
        return cdModelloComunic;
    }

    public void setCdModelloComunic(String cdModelloComunic) {
        this.cdModelloComunic = cdModelloComunic;
    }

    /*
     * @OneToMany(mappedBy = "siDecModelloComunic", cascade = CascadeType.MERGE) public List<SIDecQueryModelloComunic>
     * getDecQueryModelloComunics() { return decQueryModelloComunics; }
     *
     * public void setDecQueryModelloComunics(List<SIDecQueryModelloComunic> decQueryModelloComunics) {
     * this.decQueryModelloComunics = decQueryModelloComunics; }
     */
    // @OneToMany(mappedBy = "siDecModelloComunic", cascade = CascadeType.MERGE)
    // public List<SIDecUsoModelloComunic> getDecUsoModelloComunics() {
    // return decUsoModelloComunics;
    // }
    //
    // public void setDecUsoModelloComunics(List<SIDecUsoModelloComunic> decUsoModelloComunics) {
    // this.decUsoModelloComunics = decUsoModelloComunics;
    // }

    @Basic
    @Column(name = "DS_MODELLO_COMUNIC", nullable = false, length = 254)
    public String getDsModelloComunic() {
        return dsModelloComunic;
    }

    public void setDsModelloComunic(String dsModelloComunic) {
        this.dsModelloComunic = dsModelloComunic;
    }

    @Basic
    @Column(name = "DS_OGGETTO_COMUNIC", length = 254)
    public String getDsOggettoComunic() {
        return dsOggettoComunic;
    }

    public void setDsOggettoComunic(String dsOggettoComunic) {
        this.dsOggettoComunic = dsOggettoComunic;
    }

    @Basic
    @Column(name = "DT_ISTITUZ")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getDtIstituz() {
        return dtIstituz;
    }

    public void setDtIstituz(Date dtIstituz) {
        this.dtIstituz = dtIstituz;
    }

    @Basic
    @Column(name = "DT_SOPPRES")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getDtSoppres() {
        return dtSoppres;
    }

    public void setDtSoppres(Date dtSoppres) {
        this.dtSoppres = dtSoppres;
    }

    @Id
    @Column(name = "ID_MODELLO_COMUNIC")
    public Long getIdModelloComunic() {
        return idModelloComunic;
    }

    public void setIdModelloComunic(Long idModelloComunic) {
        this.idModelloComunic = idModelloComunic;
    }

    @Basic
    @Column(name = "NM_MITTENTE", length = 100)
    public String getNmMittente() {
        return nmMittente;
    }

    public void setNmMittente(String nmMittente) {
        this.nmMittente = nmMittente;
    }

    @Basic
    @Column(name = "TI_COMUNIC", nullable = false, length = 20)
    public String getTiComunic() {
        return tiComunic;
    }

    public void setTiComunic(String tiComunic) {
        this.tiComunic = tiComunic;
    }

    @Basic
    @Column(name = "TI_OGGETTO_QUERY", nullable = false, length = 30)
    public String getTiOggettoQuery() {
        return tiOggettoQuery;
    }

    public void setTiOggettoQuery(String tiOggettoQuery) {
        this.tiOggettoQuery = tiOggettoQuery;
    }

    @Basic
    @Column(name = "TI_STATO_TRIG_COMUNIC", length = 30)
    public String getTiStatoTrigComunic() {
        return tiStatoTrigComunic;
    }

    public void setTiStatoTrigComunic(String tiStatoTrigComunic) {
        this.tiStatoTrigComunic = tiStatoTrigComunic;
    }
}
