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

package it.eng.sacerasi.ws.invioOggettoAsincrono.dto;

import java.math.BigDecimal;

/**
 *
 * @author Bonora_L
 */
public class InvioOggettoAsincronoEstesoInput extends InvioOggettoAsincronoInput {

    private String dsObject;
    private String nmAmbienteObjectPadre;
    private String nmVersatoreObjectPadre;
    private String cdKeyObjectPadre;
    private BigDecimal niTotObjectFigli;
    private BigDecimal pgObjectFiglio;
    private BigDecimal niUnitaDocAttese;
    private String cdVersGen;
    private String tiGestOggettiFigli;
    private String tiPriorita;

    public InvioOggettoAsincronoEstesoInput(String nmAmbiente, String nmVersatore, String cdKeyObject, String dsObject,
            String nmTipoObject, boolean flFileCifrato, boolean flForzaWarning, boolean flForzaAccettazione,
            String dlMotivazione, String cdVersioneXml, String xml, String nmAmbienteObjectPadre,
            String nmVersatoreObjectPadre, String cdKeyObjectPadre, BigDecimal niTotObjectFigli,
            BigDecimal pgObjectFiglio, BigDecimal niUnitaDocAttese, String cdVersGen, String tiGestOggettiFigli,
            String tiPriorita, String tiPrioritaVersamento) {
        super(nmAmbiente, nmVersatore, cdKeyObject, nmTipoObject, flFileCifrato, flForzaWarning, flForzaAccettazione,
                dlMotivazione, cdVersioneXml, xml, tiPrioritaVersamento);
        this.dsObject = dsObject;
        this.nmAmbienteObjectPadre = nmAmbienteObjectPadre;
        this.nmVersatoreObjectPadre = nmVersatoreObjectPadre;
        this.cdKeyObjectPadre = cdKeyObjectPadre;
        this.niTotObjectFigli = niTotObjectFigli;
        this.pgObjectFiglio = pgObjectFiglio;
        this.niUnitaDocAttese = niUnitaDocAttese;
        this.cdVersGen = cdVersGen;
        this.tiGestOggettiFigli = tiGestOggettiFigli;
        this.tiPriorita = tiPriorita;
    }

    /**
     * @return the dsObject
     */
    public String getDsObject() {
        return dsObject;
    }

    /**
     * @param dsObject
     *            the dsObject to set
     */
    public void setDsObject(String dsObject) {
        this.dsObject = dsObject;
    }

    /**
     * @return the nmAmbienteObjectPadre
     */
    public String getNmAmbienteObjectPadre() {
        return nmAmbienteObjectPadre;
    }

    /**
     * @param nmAmbienteObjectPadre
     *            the nmAmbienteObjectPadre to set
     */
    public void setNmAmbienteObjectPadre(String nmAmbienteObjectPadre) {
        this.nmAmbienteObjectPadre = nmAmbienteObjectPadre;
    }

    /**
     * @return the nmVersatoreObjectPadre
     */
    public String getNmVersatoreObjectPadre() {
        return nmVersatoreObjectPadre;
    }

    /**
     * @param nmVersatoreObjectPadre
     *            the nmVersatoreObjectPadre to set
     */
    public void setNmVersatoreObjectPadre(String nmVersatoreObjectPadre) {
        this.nmVersatoreObjectPadre = nmVersatoreObjectPadre;
    }

    /**
     * @return the cdKeyObjectPadre
     */
    public String getCdKeyObjectPadre() {
        return cdKeyObjectPadre;
    }

    /**
     * @param cdKeyObjectPadre
     *            the cdKeyObjectPadre to set
     */
    public void setCdKeyObjectPadre(String cdKeyObjectPadre) {
        this.cdKeyObjectPadre = cdKeyObjectPadre;
    }

    /**
     * @return the niTotObjectFigli
     */
    public BigDecimal getNiTotObjectFigli() {
        return niTotObjectFigli;
    }

    /**
     * @param niTotObjectFigli
     *            the niTotObjectFigli to set
     */
    public void setNiTotObjectFigli(BigDecimal niTotObjectFigli) {
        this.niTotObjectFigli = niTotObjectFigli;
    }

    /**
     * @return the pgObjectFiglio
     */
    public BigDecimal getPgObjectFiglio() {
        return pgObjectFiglio;
    }

    /**
     * @param pgObjectFiglio
     *            the pgObjectFiglio to set
     */
    public void setPgObjectFiglio(BigDecimal pgObjectFiglio) {
        this.pgObjectFiglio = pgObjectFiglio;
    }

    /**
     * @return the niUnitaDocAttese
     */
    public BigDecimal getNiUnitaDocAttese() {
        return niUnitaDocAttese;
    }

    /**
     * @param niUnitaDocAttese
     *            the niUnitaDocAttese to set
     */
    public void setNiUnitaDocAttese(BigDecimal niUnitaDocAttese) {
        this.niUnitaDocAttese = niUnitaDocAttese;
    }

    /**
     *
     * @return cdVersGen the cdVersGen
     */
    public String getCdVersGen() {
        return cdVersGen;
    }

    /**
     *
     * @param cdVersGen
     *            the cdVersGen to set
     */
    public void setCdVersGen(String cdVersGen) {
        this.cdVersGen = cdVersGen;
    }

    /**
     * @return the tiGestOggettiFigli
     */
    public String getTiGestOggettiFigli() {
        return tiGestOggettiFigli;
    }

    /**
     * @param tiGestOggettiFigli
     *            the tiGestOggettiFigli to set
     */
    public void setTiGestOggettiFigli(String tiGestOggettiFigli) {
        this.tiGestOggettiFigli = tiGestOggettiFigli;
    }

    public String getTiPriorita() {
        return tiPriorita;
    }

    public void setTiPriorita(String tiPriorita) {
        this.tiPriorita = tiPriorita;
    }
}
