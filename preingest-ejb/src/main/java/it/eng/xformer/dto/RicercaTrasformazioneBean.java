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

package it.eng.xformer.dto;

import java.util.Date;

import it.eng.sacerasi.slite.gen.form.TrasformazioniForm;
import it.eng.spagoCore.error.EMFError;

/**
 * @author Cappelli_F
 */
public class RicercaTrasformazioneBean {
    private String cd_trasf;
    private String ds_trasf;
    private String cd_versione_cor;
    private String ds_versione_cor;
    private Date dt_istituz;
    private Date dt_soppres;
    private String fl_attiva;

    public RicercaTrasformazioneBean() {

    }

    public RicercaTrasformazioneBean(TrasformazioniForm.FiltriRicercaTrasformazioni filtri) throws EMFError {
        this.cd_trasf = filtri.getCd_trasf_search().parse();
        this.ds_trasf = filtri.getDs_transf_search().parse();
        this.cd_versione_cor = filtri.getCd_versione_cor_search().parse();
        this.ds_versione_cor = filtri.getDs_versione_cor_search().parse();
        this.dt_istituz = filtri.getDt_istituz_search().parse();
        this.dt_soppres = filtri.getDt_soppres_search().parse();
        this.fl_attiva = filtri.getFl_attiva_search().parse();
    }

    public String getCd_trasf() {
        return cd_trasf;
    }

    public void setCd_trasf(String cd_trasf) {
        this.cd_trasf = cd_trasf;
    }

    public String getDs_trasf() {
        return ds_trasf;
    }

    public void setDs_trasf(String ds_trasf) {
        this.ds_trasf = ds_trasf;
    }

    public String getCd_versione_cor() {
        return cd_versione_cor;
    }

    public void setCd_versione_cor(String cd_versione_cor) {
        this.cd_versione_cor = cd_versione_cor;
    }

    public String getDs_versione_cor() {
        return ds_versione_cor;
    }

    public void setDs_versione_cor(String ds_versione_cor) {
        this.ds_versione_cor = ds_versione_cor;
    }

    public Date getDt_istituz() {
        return dt_istituz;
    }

    public void setDt_istituz(Date dt_istituz) {
        this.dt_istituz = dt_istituz;
    }

    public Date getDt_soppres() {
        return dt_soppres;
    }

    public void setDt_soppres(Date dt_soppres) {
        this.dt_soppres = dt_soppres;
    }

    public String getFl_attiva() {
        return fl_attiva;
    }

    public void setFl_attiva(String fl_attiva) {
        this.fl_attiva = fl_attiva;
    }
}
