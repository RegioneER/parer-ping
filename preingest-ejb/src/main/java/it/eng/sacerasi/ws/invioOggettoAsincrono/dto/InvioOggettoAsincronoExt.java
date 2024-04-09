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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.eng.sacerasi.ws.invioOggettoAsincrono.dto;

import it.eng.sacerasi.common.Constants;
import it.eng.sacerasi.ws.dto.ISoapWSBase;
import it.eng.sacerasi.ws.dto.IWSDesc;
import it.eng.sacerasi.ws.dto.RispostaControlli;
import it.eng.sacerasi.ws.util.Costanti.ModificatoriWS;
import it.eng.sacerasi.ws.xml.datiSpecDicom.DatiSpecificiType;
import java.util.Date;
import java.util.EnumSet;

/**
 *
 * @author Gilioli_P
 */
public class InvioOggettoAsincronoExt implements ISoapWSBase {

    private IWSDesc descrizione;
    private InvioOggettoAsincronoInput invioOggettoAsincronoInput;
    private boolean flRegistraObject = true;
    private boolean flRegistraXMLObject = true;
    private boolean flRegistraDatiSpecDicom = true;
    private Long idVersatore;
    private Long idTipoObject;
    private Long idOggettoPadre;
    private Long idXsdDatiSpec;
    private String nmTipoObject;
    private String tiVersFile;
    private String datiSpecDicom;
    private String dcmHashDicom;
    private Date dtApertura;
    private Date dtChiusura;
    private Constants.StatoSessioneIngest statoSessione;
    private DatiSpecificiType dcmDatiSpecifici;
    private String versioneDatiSpecifici;
    private String ftpPath;
    private String dsRegExpCdVers;
    private String cdVersGen;
    private String tiGestOggettiFigli;

    @Override
    public IWSDesc getDescrizione() {
        return descrizione;
    }

    @Override
    public void setDescrizione(IWSDesc descrizione) {
        this.descrizione = descrizione;
    }

    @Override
    public RispostaControlli checkVersioneRequest(String versione) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getVersioneCalc() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public EnumSet<ModificatoriWS> getModificatoriWSCalc() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * @return the invioOggettoAsincronoInput
     */
    public InvioOggettoAsincronoInput getInvioOggettoAsincronoInput() {
        return invioOggettoAsincronoInput;
    }

    /**
     * @param invioOggettoAsincronoInput
     *            the invioOggettoAsincronoInput to set
     */
    public void setInvioOggettoAsincronoInput(InvioOggettoAsincronoInput invioOggettoAsincronoInput) {
        this.invioOggettoAsincronoInput = invioOggettoAsincronoInput;
    }

    /**
     * @return the flRegistraObject
     */
    public boolean isFlRegistraObject() {
        return flRegistraObject;
    }

    /**
     * @param flRegistraObject
     *            the flRegistraObject to set
     */
    public void setFlRegistraObject(boolean flRegistraObject) {
        this.flRegistraObject = flRegistraObject;
    }

    /**
     * @return the flRegistraXMLObject
     */
    public boolean isFlRegistraXMLObject() {
        return flRegistraXMLObject;
    }

    /**
     * @param flRegistraXMLObject
     *            the flRegistraXMLObject to set
     */
    public void setFlRegistraXMLObject(boolean flRegistraXMLObject) {
        this.flRegistraXMLObject = flRegistraXMLObject;
    }

    /**
     * @return the flRegistraDatiSpecDicom
     */
    public boolean isFlRegistraDatiSpecDicom() {
        return flRegistraDatiSpecDicom;
    }

    /**
     * @param flRegistraDatiSpecDicom
     *            the flRegistraDatiSpecDicom to set
     */
    public void setFlRegistraDatiSpecDicom(boolean flRegistraDatiSpecDicom) {
        this.flRegistraDatiSpecDicom = flRegistraDatiSpecDicom;
    }

    /**
     * @return the idVersatore
     */
    public Long getIdVersatore() {
        return idVersatore;
    }

    /**
     * @param idVersatore
     *            the idVersatore to set
     */
    public void setIdVersatore(Long idVersatore) {
        this.idVersatore = idVersatore;
    }

    /**
     * @return the idTipoObject
     */
    public Long getIdTipoObject() {
        return idTipoObject;
    }

    /**
     * @param idTipoObject
     *            the idTipoObject to set
     */
    public void setIdTipoObject(Long idTipoObject) {
        this.idTipoObject = idTipoObject;
    }

    /**
     * @return the nmTipoObject
     */
    public String getNmTipoObject() {
        return nmTipoObject;
    }

    /**
     * @param nmTipoObject
     *            the nmTipoObject to set
     */
    public void setNmTipoObject(String nmTipoObject) {
        this.nmTipoObject = nmTipoObject;
    }

    /**
     * @return the datiSpecDicom
     */
    public String getDatiSpecDicom() {
        return datiSpecDicom;
    }

    /**
     * @param datiSpecDicom
     *            the datiSpecDicom to set
     */
    public void setDatiSpecDicom(String datiSpecDicom) {
        this.datiSpecDicom = datiSpecDicom;
    }

    /**
     * @return the dcmHashDicom
     */
    public String getDcmHashDicom() {
        return dcmHashDicom;
    }

    /**
     * @param dcmHashDicom
     *            the dcmHashDicom to set
     */
    public void setDcmHashDicom(String dcmHashDicom) {
        this.dcmHashDicom = dcmHashDicom;
    }

    /**
     * @return the dtApertura
     */
    public Date getDtApertura() {
        return dtApertura;
    }

    /**
     * @param dtApertura
     *            the dtApertura to set
     */
    public void setDtApertura(Date dtApertura) {
        this.dtApertura = dtApertura;
    }

    /**
     * @return the dtChiusura
     */
    public Date getDtChiusura() {
        return dtChiusura;
    }

    /**
     * @param dtChiusura
     *            the dtChiusura to set
     */
    public void setDtChiusura(Date dtChiusura) {
        this.dtChiusura = dtChiusura;
    }

    /**
     * @return the statoSessione
     */
    public Constants.StatoSessioneIngest getStatoSessione() {
        return statoSessione;
    }

    /**
     * @param statoSessione
     *            the statoSessione to set
     */
    public void setStatoSessione(Constants.StatoSessioneIngest statoSessione) {
        this.statoSessione = statoSessione;
    }

    /**
     * @return the dcmDatiSpecifici
     */
    public DatiSpecificiType getDcmDatiSpecifici() {
        return dcmDatiSpecifici;
    }

    /**
     * @param dcmDatiSpecifici
     *            the dcmDatiSpecifici to set
     */
    public void setDcmDatiSpecifici(DatiSpecificiType dcmDatiSpecifici) {
        this.dcmDatiSpecifici = dcmDatiSpecifici;
    }

    /**
     * @return the versioneDatiSpecifici
     */
    public String getVersioneDatiSpecifici() {
        return versioneDatiSpecifici;
    }

    /**
     * @param versioneDatiSpecifici
     *            the versioneDatiSpecifici to set
     */
    public void setVersioneDatiSpecifici(String versioneDatiSpecifici) {
        this.versioneDatiSpecifici = versioneDatiSpecifici;
    }

    /**
     * @return the idXsdDatiSpec
     */
    public Long getIdXsdDatiSpec() {
        return idXsdDatiSpec;
    }

    /**
     * @param idXsdDatiSpec
     *            the idXsdDatiSpec to set
     */
    public void setIdXsdDatiSpec(Long idXsdDatiSpec) {
        this.idXsdDatiSpec = idXsdDatiSpec;
    }

    /**
     * @return the ftpPath
     */
    public String getFtpPath() {
        return ftpPath;
    }

    /**
     * @param ftpPath
     *            the ftpPath to set
     */
    public void setFtpPath(String ftpPath) {
        this.ftpPath = ftpPath;
    }

    /**
     * @return the tiVersFile
     */
    public String getTiVersFile() {
        return tiVersFile;
    }

    /**
     * @param tiVersFile
     *            the tiVersFile to set
     */
    public void setTiVersFile(String tiVersFile) {
        this.tiVersFile = tiVersFile;
    }

    /**
     * @return the idOggettoPadre
     */
    public Long getIdOggettoPadre() {
        return idOggettoPadre;
    }

    /**
     * @param idOggettoPadre
     *            the idOggettoPadre to set
     */
    public void setIdOggettoPadre(Long idOggettoPadre) {
        this.idOggettoPadre = idOggettoPadre;
    }

    public String getDsRegExpCdVers() {
        return dsRegExpCdVers;
    }

    public void setDsRegExpCdVers(String dsRegExpCdVers) {
        this.dsRegExpCdVers = dsRegExpCdVers;
    }

    public String getCdVersGen() {
        return cdVersGen;
    }

    public void setCdVersGen(String cdVersGen) {
        this.cdVersGen = cdVersGen;
    }

    public String getTiGestOggettiFigli() {
        return tiGestOggettiFigli;
    }

    public void setTiGestOggettiFigli(String tiGestOggettiFigli) {
        this.tiGestOggettiFigli = tiGestOggettiFigli;
    }
}
