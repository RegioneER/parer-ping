package it.eng.sacerasi.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.id.enhanced.SequenceStyleGenerator;

/**
 *
 * @author Cappelli_F
 */
@Entity
@Table(name = "PIG_STRUM_URB_STORICO_STATI")
@NamedQuery(name = "PigStrumUrbStoricoStati.findAll", query = "SELECT p FROM PigStrumUrbStoricoStati p")
public class PigStrumUrbStoricoStati implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long idStato;
    private String tiStato;
    private Date tsRegStato;
    private String descrizione;
    private PigStrumentiUrbanistici pigStrumentiUrbanistici;

    public PigStrumUrbStoricoStati() {
        // non usato
    }

    @Id
    @GenericGenerator(name = "PIG_STRUM_URB_STORICO_STATI_IDSTATO_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
            @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SPIG_STRUM_URB_STORICO_STATI"),
            @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PIG_STRUM_URB_STORICO_STATI_IDSTATO_GENERATOR")
    @Column(name = "ID_STATO")
    public Long getIdStato() {
        return idStato;
    }

    public void setIdStato(Long idStato) {
        this.idStato = idStato;
    }

    @Column(name = "TI_STATO")
    public String getTiStato() {
        return tiStato;
    }

    public void setTiStato(String tiStato) {
        this.tiStato = tiStato;
    }

    @Column(name = "TS_REG_STATO")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getTsRegStato() {
        return tsRegStato;
    }

    public void setTsRegStato(Date tsRegStato) {
        this.tsRegStato = tsRegStato;
    }

    @Column(name = "CD_DESC")
    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_STRUMENTI_URBANISTICI")
    public PigStrumentiUrbanistici getPigStrumentiUrbanistici() {
        return pigStrumentiUrbanistici;
    }

    public void setPigStrumentiUrbanistici(PigStrumentiUrbanistici pigStrumentiUrbanistici) {
        this.pigStrumentiUrbanistici = pigStrumentiUrbanistici;
    }
}
