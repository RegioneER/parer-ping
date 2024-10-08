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

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Lob;

/**
 * Auto-generated by: org.apache.openjpa.jdbc.meta.ReverseMappingTool$AnnotatedCodeGenerator edited by fioravanti_f
 */
// @Entity
// @Table(schema = "SACER_IAM", name = "DEC_QUERY_MODELLO_COMUNIC")
@Deprecated
public class SIDecQueryModelloComunic implements Serializable {
    private String blQuery;
    // private List<SIDecColQueryModelloComunic> decColQueryModelloComunics = new ArrayList<>();
    // private SIDecModelloComunic siDecModelloComunic;
    private Long idQueryModelloComunic;
    private String nmQuery;
    private String tiResultSet;
    private String tiUsoQuery;

    public SIDecQueryModelloComunic() {
    }

    public SIDecQueryModelloComunic(long idQueryModelloComunic) {
        this.idQueryModelloComunic = idQueryModelloComunic;
    }

    @Basic
    @Column(name = "BL_QUERY", nullable = false)
    @Lob
    public String getBlQuery() {
        return blQuery;
    }

    public void setBlQuery(String blQuery) {
        this.blQuery = blQuery;
    }

    // @OneToMany(mappedBy = "siDecQueryModelloComunic", cascade = CascadeType.MERGE)
    // public List<SIDecColQueryModelloComunic> getDecColQueryModelloComunics() {
    // return decColQueryModelloComunics;
    // }

    // public void setDecColQueryModelloComunics(List<SIDecColQueryModelloComunic> decColQueryModelloComunics) {
    // this.decColQueryModelloComunics = decColQueryModelloComunics;
    // }

    // @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    // @JoinColumn(name = "ID_MODELLO_COMUNIC", nullable = false)
    // public SIDecModelloComunic getSiDecModelloComunic() {
    // return siDecModelloComunic;
    // }

    // public void setSiDecModelloComunic(SIDecModelloComunic siDecModelloComunic) {
    // this.siDecModelloComunic = siDecModelloComunic;
    // }

    @Id
    @Column(name = "ID_QUERY_MODELLO_COMUNIC")
    public Long getIdQueryModelloComunic() {
        return idQueryModelloComunic;
    }

    public void setIdQueryModelloComunic(Long idQueryModelloComunic) {
        this.idQueryModelloComunic = idQueryModelloComunic;
    }

    @Basic
    @Column(name = "NM_QUERY", nullable = false, length = 100)
    public String getNmQuery() {
        return nmQuery;
    }

    public void setNmQuery(String nmQuery) {
        this.nmQuery = nmQuery;
    }

    @Basic
    @Column(name = "TI_RESULT_SET", nullable = false, length = 20)
    public String getTiResultSet() {
        return tiResultSet;
    }

    public void setTiResultSet(String tiResultSet) {
        this.tiResultSet = tiResultSet;
    }

    @Basic
    @Column(name = "TI_USO_QUERY", nullable = false, length = 30)
    public String getTiUsoQuery() {
        return tiUsoQuery;
    }

    public void setTiUsoQuery(String tiUsoQuery) {
        this.tiUsoQuery = tiUsoQuery;
    }
}
