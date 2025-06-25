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

package it.eng.sacerasi.sisma.ejb;

import static it.eng.sacerasi.web.util.ComboGetter.CAMPO_FLAG;
import static it.eng.sacerasi.web.util.ComboGetter.CAMPO_VALORE;

import java.math.BigDecimal;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.Query;

import it.eng.paginator.util.HibernateUtils;
import it.eng.sacerasi.common.Constants;
import it.eng.sacerasi.entity.PigSisma;
import it.eng.sacerasi.entity.PigSisma.TiStato;
import it.eng.sacerasi.entity.PigSismaDocumenti;
import it.eng.sacerasi.entity.PigSismaFaseProgetto;
import it.eng.sacerasi.entity.PigSismaFinanziamento;
import it.eng.sacerasi.entity.PigSismaPianoDocReq;
import it.eng.sacerasi.entity.PigSismaProgettiAg;
import it.eng.sacerasi.entity.PigSismaStatoProgetto;
import it.eng.sacerasi.entity.PigSismaStoricoStati;
import it.eng.sacerasi.entity.PigSismaValAtto;
import it.eng.sacerasi.entity.PigSismaValDoc;
import it.eng.sacerasi.entity.PigUnitaDocObject;
import it.eng.sacerasi.entity.PigVers;
import it.eng.sacerasi.grantEntity.OrgAmbiente;
import it.eng.sacerasi.grantEntity.OrgEnte;
import it.eng.sacerasi.grantEntity.OrgStrut;
import it.eng.sacerasi.grantEntity.SIOrgEnteSiam;
import it.eng.sacerasi.helper.GenericHelper;
import it.eng.sacerasi.sisma.dto.DatiAnagraficiDto;
import it.eng.sacerasi.sisma.dto.RicercaSismaDTO;
import it.eng.sacerasi.viewEntity.PigVSismaChecks;
import it.eng.sacerasi.web.helper.ConfigurationHelper;
import it.eng.spagoLite.db.base.row.BaseRow;
import it.eng.spagoLite.db.base.table.BaseTable;
import it.eng.spagoLite.db.oracle.decode.DecodeMap;

/**
 *
 * @author MIacolucci
 */

@SuppressWarnings("unchecked")
@Stateless
@LocalBean
public class SismaHelper extends GenericHelper {

    @EJB
    private ConfigurationHelper configurationHelper;
    private static final String COSTANTE_ID_VERS = "idVers";
    private static final String COSTANTE_ID_SISMA = "idSisma";
    private static final String COSTANTE_ID_SISMA_FINANZIAMENTO = "idSismaFinanziamento";

    public Set<TiStato> getTuttiGliStatiSisma() {
        return EnumSet.of(TiStato.BOZZA, TiStato.DA_VERIFICARE, TiStato.DA_RIVEDERE, TiStato.VERIFICATO,
                TiStato.RICHIESTA_INVIO, TiStato.INVIO_IN_CORSO, TiStato.ERRORE, TiStato.IN_ELABORAZIONE,
                TiStato.IN_TRASFORMAZIONE, TiStato.IN_VERSAMENTO, TiStato.IN_ELABORAZIONE_SA,
                TiStato.IN_TRASFORMAZIONE_SA, TiStato.IN_VERSAMENTO_SA, TiStato.VERSATO, TiStato.COMPLETATO,
                TiStato.ANNULLATO);
    }

    /* Torna un enumset con tutti gli stati tranne quelli passati come argomento */
    public Set<TiStato> getTuttiGliStatiSismaTranne(PigSisma.TiStato... stati) {
        EnumSet<TiStato> set = EnumSet.of(TiStato.BOZZA, TiStato.DA_VERIFICARE, TiStato.DA_RIVEDERE, TiStato.VERIFICATO,
                TiStato.RICHIESTA_INVIO, TiStato.INVIO_IN_CORSO, TiStato.ERRORE, TiStato.IN_ELABORAZIONE,
                TiStato.IN_TRASFORMAZIONE, TiStato.IN_VERSAMENTO, TiStato.IN_ELABORAZIONE_SA,
                TiStato.IN_TRASFORMAZIONE_SA, TiStato.IN_VERSAMENTO_SA, TiStato.VERSATO, TiStato.COMPLETATO,
                TiStato.ANNULLATO);
        for (TiStato tiStato : stati) {
            set.remove(tiStato);
        }
        return set;
    }

    // MEV 26165
    public Set<TiStato> getStatiSismaTranne(Set<TiStato> elencoStati, PigSisma.TiStato... stati) {
        for (TiStato tiStato : stati) {
            elencoStati.remove(tiStato);
        }
        return elencoStati;
    }

    public Query findSismaByVers(RicercaSismaDTO rDTO, BigDecimal idVers) {
        Set<TiStato> set = getTuttiGliStatiSisma();

        if (rDTO.isNmStato()) {
            set = EnumSet.of(PigSisma.TiStato.valueOf(rDTO.getNmStato()));
        }

        return findSismaByVersAndStates(rDTO, idVers, set);
    }

    public Query findSisma() {
        Set<TiStato> set = getTuttiGliStatiSisma();
        return findSismaByVersAndStates(new RicercaSismaDTO(), null, set);
    }

    public Query findSismaTranne(RicercaSismaDTO rDTO, PigSisma.TiStato... stati) {
        EnumSet<TiStato> elencoStati = EnumSet.of(TiStato.BOZZA, TiStato.DA_VERIFICARE, TiStato.DA_RIVEDERE,
                TiStato.VERIFICATO, TiStato.RICHIESTA_INVIO, TiStato.INVIO_IN_CORSO, TiStato.ERRORE,
                TiStato.IN_ELABORAZIONE, TiStato.IN_TRASFORMAZIONE, TiStato.IN_VERSAMENTO, TiStato.IN_ELABORAZIONE_SA,
                TiStato.IN_TRASFORMAZIONE_SA, TiStato.IN_VERSAMENTO_SA, TiStato.VERSATO, TiStato.COMPLETATO,
                TiStato.ANNULLATO);

        if (rDTO.isNmStato()) {
            elencoStati = EnumSet.of(PigSisma.TiStato.valueOf(rDTO.getNmStato()));
        }

        Set<TiStato> set = getStatiSismaTranne(elencoStati, stati);

        return findSismaByVersAndStates(rDTO, null, set);
    }

    public Query findSismaByVersAndStates(RicercaSismaDTO rDTO, BigDecimal idVers, Set<TiStato> set) {
        String queryStr = "SELECT s, s.pigVer , fin, prog, fase, atto, stato FROM PigSisma s "
                + "JOIN s.pigSismaProgettiAg prog " + "JOIN s.pigSismaProgettiAg.pigSismaFinanziamento fin "
                + "JOIN s.pigSismaFaseProgetto fase " + "JOIN s.pigSismaValAtto atto "
                + "JOIN s.pigSismaStatoProgetto stato " + "WHERE s.tiStato IN :set ";

        if (idVers != null) {
            queryStr += " AND s.pigVer.idVers=:idVers";
        }

        if (rDTO.isSoggettoAttuatore()) {
            queryStr += " AND prog.soggettoAttuatore = :soggettoAttuatore";
        }

        if (rDTO.isIdLineaFin()) {
            queryStr += " AND fin.idSismaFinanziamento = :idSismaFinanziamento";
        }

        if (rDTO.isNmIntervento()) {
            queryStr += " AND prog.codiceIntervento = :codiceIntervento";
        }

        if (rDTO.isNmFaseProg()) {
            queryStr += " AND fase.dsFaseSisma = :dsFaseSisma";
        }

        if (rDTO.isNmStatoProg()) {
            queryStr += " AND stato.dsStatoProgetto = :dsStatoProgetto";
        }

        if (rDTO.isAnno()) {
            queryStr += " AND s.anno = :anno";
        }

        if (rDTO.isCdIdentificativo()) {
            queryStr += " AND LOWER(s.cdKey) LIKE LOWER(:cdKey)";
        }

        if (rDTO.isDtCreazione()) {
            queryStr += " AND s.data = :dtCreazione";
        }

        if (rDTO.isCdOggetto()) {
            queryStr += " AND LOWER(s.oggetto) LIKE LOWER(:cdOggetto)";
        }

        if (rDTO.isAgenzia()) {
            if (rDTO.isCdRegistroAgenzia()) {
                queryStr += " AND s.registroAg = :registroAg";
            }

            if (rDTO.isAnnoAgenzia()) {
                queryStr += " AND s.annoAg = :annoAg";
            }

            if (rDTO.isCdNumeroAgenzia()) {
                queryStr += " AND s.numeroAg LIKE :numeroAg";
            }
        }

        queryStr += " ORDER BY s.dtCreazione DESC";

        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("set", set);

        if (idVers != null) {
            query.setParameter(COSTANTE_ID_VERS, HibernateUtils.longFrom(idVers));
        }

        if (rDTO.isSoggettoAttuatore()) {
            query.setParameter("soggettoAttuatore", rDTO.getSoggettoAttuatore());
        }

        if (rDTO.isIdLineaFin()) {
            query.setParameter(COSTANTE_ID_SISMA_FINANZIAMENTO, HibernateUtils.longFrom(rDTO.getIdLineaFin()));
        }

        if (rDTO.isNmIntervento()) {
            query.setParameter("codiceIntervento", rDTO.getNmIntervento());
        }

        if (rDTO.isNmFaseProg()) {
            query.setParameter("dsFaseSisma", rDTO.getNmFaseProg());
        }

        if (rDTO.isNmStatoProg()) {
            query.setParameter("dsStatoProgetto", rDTO.getNmStatoProg());
        }

        if (rDTO.isAnno()) {
            query.setParameter("anno", rDTO.getAnno());
        }

        if (rDTO.isCdIdentificativo()) {
            query.setParameter("cdKey", "%" + rDTO.getCdIdentificativo() + "%");
        }

        if (rDTO.isDtCreazione()) {
            query.setParameter("dtCreazione", rDTO.getDtCreazione());
        }

        if (rDTO.isCdOggetto()) {
            query.setParameter("cdOggetto", "%" + rDTO.getCdOggetto() + "%");
        }

        if (rDTO.isAgenzia()) {
            if (rDTO.isCdRegistroAgenzia()) {
                query.setParameter("registroAg", rDTO.getCdRegistroAgenzia());

            }

            if (rDTO.isAnnoAgenzia()) {
                query.setParameter("annoAg", rDTO.getAnnoAgenzia());
            }

            if (rDTO.isCdNumeroAgenzia()) {
                query.setParameter("numeroAg", "%" + rDTO.getCdNumeroAgenzia() + "%");
            }
        }

        return query;
    }

    public List<Object[]> findSismaByStates(Set<TiStato> set) {
        String queryStr = "SELECT s, s.pigVer, fin, prog, fase, atto, stato FROM PigSisma s "
                + "JOIN s.pigSismaProgettiAg prog " + "JOIN s.pigSismaProgettiAg.pigSismaFinanziamento fin "
                + "JOIN s.pigSismaFaseProgetto fase " + "JOIN s.pigSismaValAtto atto "
                + "JOIN s.pigSismaStatoProgetto stato " + "WHERE s.tiStato IN :set ORDER BY s.dtCreazione DESC";

        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("set", set);
        return query.getResultList();
    }

    public List<PigSisma> findSismaByVersAndCdKey(PigVers pigVer, String cdKey) {
        String queryStr = "SELECT s FROM PigSisma s WHERE s.pigVer=:pigVer AND s.cdKey=:cdKey";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("pigVer", pigVer);
        query.setParameter("cdKey", cdKey);
        return query.getResultList();
    }

    public List<PigSisma> findSismaCdKey(String cdKey) {
        String queryStr = "SELECT s FROM PigSisma s WHERE s.cdKey=:cdKey";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("cdKey", cdKey);
        return query.getResultList();
    }

    public List<PigSismaValAtto> findPigSismaValAtto() {
        String queryStr = "SELECT s FROM PigSismaValAtto s";
        Query query = getEntityManager().createQuery(queryStr);
        return query.getResultList();
    }

    public List<PigSismaStatoProgetto> findPigSismaStatoProgetto() {
        String queryStr = "SELECT s FROM PigSismaStatoProgetto s";
        Query query = getEntityManager().createQuery(queryStr);
        return query.getResultList();
    }

    public List<PigSismaStatoProgetto> findPigSismaStatoProgettoByIdSismaFaseProgetto(BigDecimal idSismaFaseProgetto) {
        String queryStr = "SELECT f.pigSismaStatoProgetto FROM PigSismaFaseStatoProgetto f WHERE f.pigSismaFaseProgetto.idSismaFaseProgetto=:idSismaFaseProgetto";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idSismaFaseProgetto", HibernateUtils.longFrom(idSismaFaseProgetto));
        return query.getResultList();
    }

    public PigSisma getPigSismaByCdKeyAndTiStato(String cdKey, PigSisma.TiStato tiStato) {
        String queryStr = "SELECT p FROM PigSisma p WHERE p.cdKey=:cdKey AND p.tiStato=:tiStato";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("cdKey", cdKey);
        query.setParameter("tiStato", tiStato);
        List<PigSisma> list = query.getResultList();
        if (!list.isEmpty()) {
            return list.get(0);
        } else {
            return null;
        }
    }

    public PigSisma getSismaByVersAndCdKey(PigVers pigVer, String cdKey) {
        List<PigSisma> l = findSismaByVersAndCdKey(pigVer, cdKey);
        return l.isEmpty() ? null : l.iterator().next();
    }

    public PigSisma getSismaCdKey(String cdKey) {
        List<PigSisma> l = findSismaCdKey(cdKey);
        return l.isEmpty() ? null : l.iterator().next();
    }

    public List<PigSismaFinanziamento> findPigSismaFinanziamento() {
        String queryStr = "SELECT p FROM PigSismaFinanziamento p ORDER BY p.cdTipoFinanziamento";
        Query query = getEntityManager().createQuery(queryStr);
        return query.getResultList();
    }

    public List<PigSismaFaseProgetto> findPigSismaFaseProgetto() {
        String queryStr = "SELECT p FROM PigSismaFaseProgetto p ORDER BY p.dsFaseSisma";
        Query query = getEntityManager().createQuery(queryStr);
        return query.getResultList();
    }

    public List<PigSismaStatoProgetto> findPigSismaStatoProgettoOrdered() {
        String queryStr = "SELECT p FROM PigSismaStatoProgetto p ORDER BY p.dsStatoProgetto";
        Query query = getEntityManager().createQuery(queryStr);
        return query.getResultList();
    }

    public List<String> findPigSismaSoggettiAttuatore() {
        String queryStr = "SELECT DISTINCT(pspa.soggettoAttuatore) FROM PigSismaProgettiAg pspa order by pspa.soggettoAttuatore";
        Query query = getEntityManager().createQuery(queryStr);
        return query.getResultList();
    }

    public List<String> findPigSismaCodiceIntervento() {
        String queryStr = "SELECT DISTINCT(pspa.codiceIntervento) FROM PigSismaProgettiAg pspa order by pspa.codiceIntervento";
        Query query = getEntityManager().createQuery(queryStr);
        return query.getResultList();
    }

    public List<BigDecimal> findPigSismaAnno() {
        String queryStr = "SELECT DISTINCT(s.anno) FROM PigSisma s order by s.anno";
        Query query = getEntityManager().createQuery(queryStr);
        return query.getResultList();
    }

    public List<BigDecimal> findPigSismaAnnoAgenzia() {
        String queryStr = "SELECT DISTINCT(s.annoAg) FROM PigSisma s order by s.annoAg";
        Query query = getEntityManager().createQuery(queryStr);
        return query.getResultList();
    }

    public List<String> findPigSismaRegistroAgenzia() {
        String queryStr = "SELECT DISTINCT(s.registroAg) FROM PigSisma s order by s.registroAg";
        Query query = getEntityManager().createQuery(queryStr);
        return query.getResultList();
    }

    public List<String> findPigSismaNumeroAgenzia() {
        String queryStr = "SELECT DISTINCT(s.numeroAg) FROM PigSisma s order by s.numeroAg";
        Query query = getEntityManager().createQuery(queryStr);
        return query.getResultList();
    }

    public List<TiStato> findPigSismaStato() {
        String queryStr = "SELECT DISTINCT(s.tiStato) FROM PigSisma s order by s.tiStato";
        Query query = getEntityManager().createQuery(queryStr);
        return query.getResultList();
    }

    public List<PigSismaFinanziamento> findPigSismaFinanziamentoByIdVers(BigDecimal idVers) {
        String queryStr = "SELECT DISTINCT prog.pigSismaFinanziamento FROM PigSismaProgettiAg prog WHERE prog.idEnteSiam=:idVers ORDER BY prog.pigSismaFinanziamento.dsTipoFinanziamento";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter(COSTANTE_ID_VERS, idVers);
        return query.getResultList();
    }

    public List<PigSismaFaseProgetto> getFasiInseriteFinVers(BigDecimal idSismaFinanziamento, BigDecimal idVers) {
        String strQuery1 = "SELECT f FROM PigSisma p JOIN p.pigSismaFaseProgetto f "
                + "WHERE p.pigVer.idVers=:idVers AND p.pigSismaFaseProgetto.pigSismaFinanziamento.idSismaFinanziamento=:idSismaFinanziamento "
                + "ORDER BY p.pigSismaFaseProgetto.dsFaseSisma DESC ";
        Query query1 = getEntityManager().createQuery(strQuery1);
        query1.setParameter(COSTANTE_ID_VERS, HibernateUtils.longFrom(idVers));
        query1.setParameter(COSTANTE_ID_SISMA_FINANZIAMENTO, HibernateUtils.longFrom(idSismaFinanziamento));
        return query1.getResultList();
    }

    /* Estrae le fasi per il tipo di finanziamento */
    public List<PigSismaFaseProgetto> findPigSismaFaseProgettoByFin(BigDecimal idSismaFinanziamento) {
        String strQuery2 = "SELECT p FROM PigSismaFaseProgetto p "
                + "WHERE p.pigSismaFinanziamento.idSismaFinanziamento=:idSismaFinanziamento " + "ORDER BY p.niOrd ";
        Query query2 = getEntityManager().createQuery(strQuery2);
        query2.setParameter(COSTANTE_ID_SISMA_FINANZIAMENTO, HibernateUtils.longFrom(idSismaFinanziamento));
        return query2.getResultList();
    }

    public List<PigSismaProgettiAg> getPigSismaProgettiAgByIdEnteFinanziamento(BigDecimal idEnteSiam,
            BigDecimal idSismaFinanziamento) {
        String queryStr = "SELECT p FROM PigSismaProgettiAg p "
                + "WHERE p.idEnteSiam=:idEnteSiam AND p.pigSismaFinanziamento.idSismaFinanziamento=:idSismaFinanziamento";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idEnteSiam", idEnteSiam);
        query.setParameter(COSTANTE_ID_SISMA_FINANZIAMENTO, HibernateUtils.longFrom(idSismaFinanziamento));
        return query.getResultList();
    }

    public PigSismaValAtto getPigSismaValAttoById(BigDecimal idSismaValAtto) {
        return getEntityManager().find(PigSismaValAtto.class, idSismaValAtto.longValueExact());
    }

    public PigVers getPigVersById(BigDecimal idVers) {
        return getPigVersById(idVers.longValueExact());
    }

    public PigVers getPigVersById(long idVers) {
        return getEntityManager().find(PigVers.class, idVers);
    }

    public PigVers getPigVersAgByIdSisma(long idSisma) {
        String queryStr = "SELECT s.pigVerAg FROM PigSisma s WHERE s.idSisma = :idSisma";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idSisma", idSisma);
        List<PigVers> list = query.getResultList();
        if (list.size() > 0) {
            return list.get(0);
        } else {
            return null;
        }
    }

    public SIOrgEnteSiam getSIOrgEnteSiamById(BigDecimal id) {
        return getEntityManager().find(SIOrgEnteSiam.class, id.longValueExact());
    }

    public PigSismaPianoDocReq getPigSismaPianoDocReqByValDoc(PigSismaFaseProgetto pigSismaFaseProgetto,
            PigSismaValDoc pigSismaValDoc) {
        String queryStr = "SELECT r FROM PigSismaPianoDocReq r WHERE r.pigSismaFaseProgetto=:pigSismaFaseProgetto AND r.pigSismaValDoc=:pigSismaValDoc";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("pigSismaFaseProgetto", pigSismaFaseProgetto);
        query.setParameter("pigSismaValDoc", pigSismaValDoc);
        List<PigSismaPianoDocReq> list = query.getResultList();
        if (!list.isEmpty()) {
            return list.get(0);
        } else {
            return null;
        }
    }

    public PigSismaValDoc getPigSismaValDocByNomeTipoDoc(String nmTipoDocumento) {
        String queryStr = "SELECT p FROM PigSismaValDoc p WHERE p.nmTipoDocumento=:nmTipoDocumento";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("nmTipoDocumento", nmTipoDocumento);
        List<PigSismaValDoc> list = query.getResultList();
        if (!list.isEmpty()) {
            return list.get(0);
        } else {
            return null;
        }
    }

    public void cancellaEntryDocumenti(PigSismaDocumenti pigSismaDocumenti) {
        String queryStr = "DELETE FROM PigSismaDocEntry p WHERE p.pigSismaDocumenti=:pigSismaDocumenti";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("pigSismaDocumenti", pigSismaDocumenti);
        query.executeUpdate();
    }

    public PigSismaDocumenti getPigSismaDocumentiBySismaNmFileOrig(PigSisma pigSisma, String nmFileOrig) {
        String queryStr = "SELECT p FROM PigSismaDocumenti p WHERE p.pigSisma=:pigSisma AND p.nmFileOrig=:nmFileOrig AND p.flDeleted='0'";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("pigSisma", pigSisma);
        query.setParameter("nmFileOrig", nmFileOrig);
        List<PigSismaDocumenti> list = query.getResultList();
        if (!list.isEmpty()) {
            return list.get(0);
        } else {
            return null;
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public PigSisma aggiornaStatoInNuovaTransazione(PigSisma su, PigSisma.TiStato tiStato) {
        return aggiornaStato(su, tiStato);
    }

    // MAC27281
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public PigSisma aggiornaInviatoEnteInNuovaTransazione(PigSisma su, boolean isInviato) {
        PigSisma pigSisma = getEntityManager().find(PigSisma.class, su.getIdSisma());
        if (isInviato) {
            pigSisma.setFlInviatoAEnte(Constants.DB_TRUE);
        } else {
            pigSisma.setFlInviatoAEnte(Constants.DB_FALSE);
        }

        return pigSisma;
    }

    // MEV29704 pulisci verifica documenti
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public PigSisma pulisciFlagVerificaDocumenti(PigSisma su) {
        PigSisma pigSisma = getEntityManager().find(PigSisma.class, su.getIdSisma());
        // pulisci lo stato di verifica dei documenti
        for (PigSismaDocumenti doc : pigSisma.getPigSismaDocumentis()) {
            // doc.setFlEsitoVerifica("0");
            doc.setTiVerificaAgenzia(null);
        }

        return pigSisma;
    }

    public PigSisma aggiornaStato(PigSisma su, PigSisma.TiStato tiStato) {
        PigSisma pigSisma = getEntityManager().find(PigSisma.class, su.getIdSisma());

        // MEV 30936
        creaStatoStorico(pigSisma, pigSisma.getTiStato().name(), pigSisma.getDtStato(),
                pigSisma.getCdErr() != null ? pigSisma.getCdErr() + " - " + pigSisma.getDsErr() : "");

        pigSisma.setTiStato(tiStato);
        pigSisma.setDtStato(new Date());
        return pigSisma;
    }

    public PigVSismaChecks getDatiNavigazionePerSisma(BigDecimal idSisma) {
        String queryStr = "SELECT p FROM PigVSismaChecks p WHERE p.idSisma=:idSisma";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter(COSTANTE_ID_SISMA, idSisma);
        List<PigVSismaChecks> list = query.getResultList();
        if (!list.isEmpty()) {
            return list.get(0);
        } else {
            return null;
        }
    }

    // MEV27950
    public List<PigSismaPianoDocReq> findPigSismaPianoDocReq(PigSismaFaseProgetto pigSismaFaseProgetto) {
        String queryStr = "SELECT s FROM PigSismaPianoDocReq s WHERE s.pigSismaFaseProgetto=:pigSismaFaseProgetto";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("pigSismaFaseProgetto", pigSismaFaseProgetto);
        return query.getResultList();
    }

    /* Estrae i documenti da ricaricare che l'agenzia a fleggato come da modificare e ricaricare */
    public List<PigSismaDocumenti> findPigSismaDocumentiDaRicaricareByidSisma(BigDecimal idSisma) {
        String queryStr = "SELECT 1 FROM PigSismaDocumenti docs WHERE docs.pigSisma.idSisma=:idSisma "
                + "AND docs.flDeleted<>'1' AND docs.flEsitoVerifica='0' AND docs.tiVerificaAgenzia='0' AND docs.flDocRicaricato='0'";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter(COSTANTE_ID_SISMA, HibernateUtils.longFrom(idSisma));
        return query.getResultList();
    }

    public DatiAnagraficiDto getDatiAnagraficiByIdVers(BigDecimal idVers, PigSisma pigSisma) {
        DatiAnagraficiDto dto;
        PigVers pigVers = getPigVersById(idVers);
        SIOrgEnteSiam orgEnteSiam = getOrgEnteSiamByPigVers(pigVers);
        dto = new DatiAnagraficiDto();
        dto.setIdEnteSiam(new BigDecimal(orgEnteSiam.getIdEnteSiam()));

        if (pigVers.getIdEnteFornitEstern() != null) {
            dto.setNaturaSoggettoAttuatore("PRIVATO");
        } else {
            dto.setNaturaSoggettoAttuatore("PUBBLICO");
        }
        if (pigSisma != null) {
            PigSismaProgettiAg p = pigSisma.getPigSismaProgettiAg();
            dto.setEnteProprietario(p.getEnteProprietario());
            dto.setNaturaEnteProprietario(p.getNaturaEnteProprietario());
            dto.setUbicazioneComune(p.getUbicazioneComune());
            dto.setUbicazioneProvincia(p.getUbicazioneProvincia());
            String soggettoATutela = p.getFlInterventoSoggettoATutela();
            dto.setSoggettoATutela(soggettoATutela != null && soggettoATutela.equals(Constants.DB_TRUE));

            // MEV 30023
            dto.setSoggettoAttuatore(p.getSoggettoAttuatore());

        }
        return dto;
    }

    /* Torna il corretto OrgEnteSiam in base a se è valorizzato idEnteConvenz o idFornitoreEsterno */
    public SIOrgEnteSiam getOrgEnteSiamByPigVers(PigVers pigVers) {
        BigDecimal id = pigVers.getIdEnteConvenz();
        SIOrgEnteSiam orgEnteSiam = null;
        if (id == null) {
            id = pigVers.getIdEnteFornitEstern();
        }
        if (id != null) {
            orgEnteSiam = getEntityManager().find(SIOrgEnteSiam.class, id.longValueExact());
        }
        return orgEnteSiam;
    }

    public BigDecimal getDimensioneDocumentiBySisma(PigSisma pigSisma) {
        BigDecimal dimensione = null;
        String queryStr = "SELECT SUM(d.dimensione) FROM PigSismaDocumenti d WHERE d.pigSisma=:pigSisma AND d.flDeleted='0'";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("pigSisma", pigSisma);
        List<BigDecimal> l = query.getResultList();
        if (!l.isEmpty()) {
            dimensione = l.iterator().next();
        }
        return dimensione;
    }

    public Object[] findDatiAmbienteByIdSisma(BigDecimal id) {
        Object[] ogg = null;
        String queryStr = "select su, str, en, amb, udo "
                + "from   PigSisma su, PigObject po, PigObject son, PigUnitaDocObject udo, "
                + "       SIUsrOrganizIam orgIam, OrgStrut str, OrgEnte en, OrgAmbiente amb "
                + "where  po.cdKeyObject = su.cdKey and    udo.pigObject.idObject = son.idObject "
                + "and    orgIam.idOrganizIam = udo.idOrganizIam " + "and    son.pigObjectPadre.idObject = po.idObject "
                + "and    str.idStrut = orgiam.idOrganizApplic " + "and    en.idEnte = str.orgEnte.idEnte "
                + "and    amb.idAmbiente = en.orgAmbiente.idAmbiente " + "and    su.idSisma=:id";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("id", HibernateUtils.longFrom(id));
        List<Object[]> l = query.getResultList();
        if (!l.isEmpty()) {
            ogg = l.iterator().next(); // legge il record..
            PigSisma pigSisma = (PigSisma) ogg[0];
            PigVers pigVersAg = pigSisma.getPigVerAg();
            if (pigVersAg != null) {
                ogg = l.iterator().next(); // legge il secondo record ed esce...
            }
        }
        return ogg;
    }

    public DatiRecuperoDto findDatiPerRecuperoByIdSisma(BigDecimal id, boolean estraiAgenzia, BigDecimal idAgenzia) {
        DatiRecuperoDto dto = null;
        Object[] ogg;
        String queryStr = "select sisma, ogg, udo, vers, orgIam, str, en, amb "
                + "from   PigSisma sisma, PigObject ogg, PigUnitaDocObject udo, PigVers vers, "
                + "       SIUsrOrganizIam orgIam, OrgStrut str, OrgEnte en, OrgAmbiente amb "
                + "where  ogg.cdKeyObject LIKE concat(sisma.cdKey, :percento) "
                + "and    ogg.idObject = udo.pigObject.idObject " + "and    ogg.pigVer.idVers = vers.idVers "
                + "and    sisma.idSisma=:id " + "and    udo.idOrganizIam=orgIam.idOrganizIam "
                + "and    orgIam.idOrganizApplic=str.idStrut " + "and    en.idEnte = str.orgEnte.idEnte "
                + "and    amb.idAmbiente = en.orgAmbiente.idAmbiente ";
        if (estraiAgenzia) {
            queryStr += "and vers.idVers=:idAgenzia ";
        } else {
            queryStr += "and vers.idVers<>:idAgenzia ";
        }
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("id", HibernateUtils.longFrom(id));
        query.setParameter("idAgenzia", HibernateUtils.longFrom(idAgenzia));
        query.setParameter("percento", "%");
        List<Object[]> l = query.getResultList();
        if (!l.isEmpty()) {
            ogg = l.iterator().next(); // legge il record..
            PigSisma pigSisma = (PigSisma) ogg[0];
            PigUnitaDocObject pigUnitaDocObject = (PigUnitaDocObject) ogg[2];
            OrgAmbiente orgAmbiente = (OrgAmbiente) ogg[7];
            OrgEnte orgEnte = (OrgEnte) ogg[6];
            OrgStrut orgStrut = (OrgStrut) ogg[5];
            dto = new DatiRecuperoDto();
            dto.setNomeAmbiente(orgAmbiente.getNmAmbiente());
            dto.setNomeEnte(orgEnte.getNmEnte());
            dto.setNomeStruttura(orgStrut.getNmStrut());
            dto.setAnno(pigSisma.getAnno().longValueExact());
            if (estraiAgenzia) {
                // Il progetto deve essere inviato per forza solo in agenzia
                dto.setNomeTipoRegistro(
                        pigSisma.getPigSismaProgettiAg().getPigSismaFinanziamento().getDsTipoRegistroAgenzia());
                dto.setNumero(pigSisma.getRegistroAg() + "_" + pigSisma.getNumeroAg());
                dto.setAnno(pigSisma.getAnnoAg().longValueExact());
            } else {
                // Il rapporto di versamento è quello dell'ente perché è stato inviato all'ente appunto
                dto.setNomeTipoRegistro(
                        pigSisma.getPigSismaProgettiAg().getPigSismaFinanziamento().getDsTipoRegistroSaPubblico());
                dto.setNumero(pigUnitaDocObject.getCdKeyUnitaDocSacer());
                dto.setAnno(pigSisma.getAnno().longValueExact());
            }
        }
        return dto;
    }

    /*
     * Ritorna il tipo di versatore pesasto come parametro
     */
    public Enum<Constants.TipoVersatore> getTipoVersatore(PigVers pigVers) {
        Enum<Constants.TipoVersatore> tipoVersatore = null;
        BigDecimal idEnteConvenzionato = pigVers.getIdEnteConvenz();
        BigDecimal idEnteFornitEstern = pigVers.getIdEnteFornitEstern();

        String idVersatoreAgenzia = configurationHelper.getValoreParamApplicByApplic(Constants.ID_VERSATORE_AGENZIA);
        BigDecimal id = new BigDecimal(pigVers.getIdVers());
        if (id.equals(new BigDecimal(idVersatoreAgenzia))) {
            tipoVersatore = Constants.TipoVersatore.AGENZIA;
        } else {
            if (idEnteConvenzionato != null) {
                // Logica usata da PAOLO per determinare se un versatore è produttore o meno
                tipoVersatore = Constants.TipoVersatore.SA_PUBBLICO;
            } else if (idEnteFornitEstern != null) {
                SIOrgEnteSiam orgEnteSiam = getSIOrgEnteSiamById(idEnteFornitEstern);
                if (orgEnteSiam.getTiEnteNonConvenz()
                        .equals(it.eng.sacerasi.entity.constraint.SIOrgEnteSiam.TiEnteNonConvenz.SOGGETTO_ATTUATORE)) {
                    tipoVersatore = Constants.TipoVersatore.SA_PRIVATO;
                }
            }
        }
        return tipoVersatore;
    }

    public boolean existsPigSismaDocumentiDaVerificare(BigDecimal idSisma) {
        Query query = getEntityManager().createQuery("SELECT s FROM PigSisma s WHERE s.idSisma = :idSisma "
                + "AND EXISTS (SELECT d FROM PigSismaDocumenti d " + "WHERE d.pigSisma = s "
                + "AND d.flEsitoVerifica = :flEsitoVerifica AND d.flDeleted != :flDeleted)");
        query.setParameter(COSTANTE_ID_SISMA, HibernateUtils.longFrom(idSisma));
        query.setParameter("flEsitoVerifica", Constants.DB_FALSE);
        query.setParameter("flDeleted", Constants.DB_TRUE);
        List<PigSisma> lista = query.getResultList();
        return !lista.isEmpty();
    }

    public List<PigSisma> findPigSismaByVersAndDatiAgenzia(BigDecimal idSisma, PigVers agenzia, String registroAg,
            BigDecimal annoAg, String numeroAg) {
        String queryStr = "SELECT s FROM PigSisma s WHERE s.pigVerAg = :agenzia AND s.registroAg = :registroAg "
                + " AND s.annoAg = :annoAg AND s.numeroAg = :numeroAg AND NOT s.tiStato = :tiStato AND NOT s.idSisma = :idSisma ";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("agenzia", agenzia);
        query.setParameter("registroAg", registroAg);
        query.setParameter("annoAg", annoAg);
        query.setParameter("numeroAg", numeroAg);
        query.setParameter("tiStato", TiStato.ANNULLATO);
        query.setParameter(COSTANTE_ID_SISMA, HibernateUtils.longFrom(idSisma));
        return query.getResultList();
    }

    // MEV 26165
    public DecodeMap getLineaFinanziamentiDecodeMap() {
        List<PigSismaFinanziamento> finanziamenti = this.findPigSismaFinanziamento();
        DecodeMap finanziamentiDecodeMap = new DecodeMap();
        BaseTable bt = new BaseTable();
        for (PigSismaFinanziamento psf : finanziamenti) {
            BaseRow br = new BaseRow();
            br.setString(CAMPO_FLAG, psf.getDsTipoFinanziamento());
            br.setBigDecimal(CAMPO_VALORE, new BigDecimal(psf.getIdSismaFinanziamento()));
            bt.add(br);
        }
        finanziamentiDecodeMap.populatedMap(bt, CAMPO_VALORE, CAMPO_FLAG);

        return finanziamentiDecodeMap;
    }

    // MEV 26165
    public DecodeMap getSoggettiAttuatoreDecodeMap() {
        List<String> soggetti = this.findPigSismaSoggettiAttuatore();
        DecodeMap soggettiAttuatoreDecodeMap = new DecodeMap();
        BaseTable bt = new BaseTable();
        for (String soggetto : soggetti) {
            if (soggetto != null && !soggetto.isEmpty()) {
                BaseRow br = new BaseRow();
                br.setString(CAMPO_FLAG, soggetto);
                br.setString(CAMPO_VALORE, soggetto);
                bt.add(br);
            }
        }
        soggettiAttuatoreDecodeMap.populatedMap(bt, CAMPO_VALORE, CAMPO_FLAG);

        return soggettiAttuatoreDecodeMap;
    }

    // MEV 26165
    public DecodeMap getCodiceIntervento() {
        List<String> codiciIntervento = this.findPigSismaCodiceIntervento();
        DecodeMap codiciInterventoDecodeMap = new DecodeMap();
        BaseTable bt = new BaseTable();
        for (String codice : codiciIntervento) {
            if (codice != null && !codice.isEmpty()) {
                BaseRow br = new BaseRow();
                br.setString(CAMPO_FLAG, codice);
                br.setString(CAMPO_VALORE, codice);
                bt.add(br);
            }
        }
        codiciInterventoDecodeMap.populatedMap(bt, CAMPO_VALORE, CAMPO_FLAG);

        return codiciInterventoDecodeMap;
    }

    // MEV 26165
    public DecodeMap getSismaFaseProgetto() {
        List<PigSismaFaseProgetto> fasi = this.findPigSismaFaseProgetto();
        DecodeMap fasiDecodeMap = new DecodeMap();
        BaseTable bt = new BaseTable();
        for (PigSismaFaseProgetto fase : fasi) {
            BaseRow br = new BaseRow();
            br.setString(CAMPO_FLAG, fase.getDsFaseSisma());
            br.setString(CAMPO_VALORE, fase.getDsFaseSisma());
            bt.add(br);
        }
        fasiDecodeMap.populatedMap(bt, CAMPO_VALORE, CAMPO_FLAG);

        return fasiDecodeMap;
    }

    // MEV 26165
    public DecodeMap getSismaStatoProgetto() {
        List<PigSismaStatoProgetto> stati = this.findPigSismaStatoProgettoOrdered();
        DecodeMap statiProgettoDecodeMap = new DecodeMap();
        BaseTable bt = new BaseTable();
        for (PigSismaStatoProgetto stato : stati) {

            BaseRow br = new BaseRow();
            br.setString(CAMPO_FLAG, stato.getDsStatoProgetto());
            br.setString(CAMPO_VALORE, stato.getDsStatoProgetto());
            bt.add(br);
        }
        statiProgettoDecodeMap.populatedMap(bt, CAMPO_VALORE, CAMPO_FLAG);

        return statiProgettoDecodeMap;
    }

    // MEV 26165
    public DecodeMap getSismaStato(boolean isAgenzia) {
        List<TiStato> stati = this.findPigSismaStato();
        DecodeMap statiInterventoDecodeMap = new DecodeMap();
        BaseTable bt = new BaseTable();
        for (TiStato stato : stati) {
            // MAC 27254 - Agenzia non può cercare i sisma in stato bozza. Non lo inseriamo nella decode map
            if (isAgenzia && stato.name().equals("BOZZA")) {
                continue;
            }

            if (stato != null) {
                BaseRow br = new BaseRow();
                br.setString(CAMPO_FLAG, stato.name());
                br.setString(CAMPO_VALORE, stato.name());
                bt.add(br);
            }
        }
        statiInterventoDecodeMap.populatedMap(bt, CAMPO_VALORE, CAMPO_FLAG);

        return statiInterventoDecodeMap;
    }

    // MEV 26165
    public DecodeMap getSismaAnno() {
        List<BigDecimal> anni = this.findPigSismaAnno();
        DecodeMap anniDecodeMap = new DecodeMap();
        BaseTable bt = new BaseTable();
        for (BigDecimal anno : anni) {
            if (anno != null) {
                BaseRow br = new BaseRow();
                br.setString(CAMPO_FLAG, anno.toString());
                br.setBigDecimal(CAMPO_VALORE, anno);
                bt.add(br);
            }
        }
        anniDecodeMap.populatedMap(bt, CAMPO_VALORE, CAMPO_FLAG);

        return anniDecodeMap;
    }

    // MEV 26165
    public DecodeMap getSismaAnnoAgenzia() {
        List<BigDecimal> anni = this.findPigSismaAnnoAgenzia();
        DecodeMap anniDecodeMap = new DecodeMap();
        BaseTable bt = new BaseTable();
        for (BigDecimal anno : anni) {
            if (anno != null) {
                BaseRow br = new BaseRow();
                br.setString(CAMPO_FLAG, anno.toString());
                br.setBigDecimal(CAMPO_VALORE, anno);
                bt.add(br);
            }
        }
        anniDecodeMap.populatedMap(bt, CAMPO_VALORE, CAMPO_FLAG);

        return anniDecodeMap;
    }

    // MEV 26165
    public DecodeMap getSismaRegistroAgenzia() {
        List<String> registri = this.findPigSismaRegistroAgenzia();
        DecodeMap registriDecodeMap = new DecodeMap();
        BaseTable bt = new BaseTable();
        for (String registro : registri) {
            if (registro != null && !registro.isEmpty()) {
                BaseRow br = new BaseRow();
                br.setString(CAMPO_FLAG, registro);
                br.setString(CAMPO_VALORE, registro);
                bt.add(br);
            }
        }
        registriDecodeMap.populatedMap(bt, CAMPO_VALORE, CAMPO_FLAG);

        return registriDecodeMap;
    }

    // MEV 30936
    public List<PigSismaStoricoStati> getPigSismaStoricoStatiFromSisma(BigDecimal idSisma) {
        Query query = getEntityManager().createQuery(
                "SELECT s FROM PigSismaStoricoStati s WHERE s.pigSisma.idSisma = :idSisma ORDER BY s.tsRegStato DESC");
        query.setParameter("idSisma", HibernateUtils.longFrom(idSisma));
        return query.getResultList();
    }

    // MEV 30936
    public void creaStatoStorico(PigSisma pigSisma, String stato, Date dtRegStato, String descrizione) {
        PigSismaStoricoStati pigSismaStoricoStati = new PigSismaStoricoStati();
        pigSismaStoricoStati.setPigSisma(pigSisma);
        pigSismaStoricoStati.setTiStato(stato);
        pigSismaStoricoStati.setTsRegStato(dtRegStato);
        pigSismaStoricoStati.setDescrizione(descrizione);
        getEntityManager().persist(pigSismaStoricoStati);
    }

    // MEV 34843
    public PigSismaDocumenti getPigSismaDocumentiByName(String nomefile, BigDecimal idSisma) {
        Query query = getEntityManager()
                .createQuery("SELECT psud FROM PigSismaDocumenti psud WHERE psud.pigSisma.idSisma = :idSisma AND"
                        + " psud.nmFileOrig = :nomefile");
        query.setParameter("idSisma", HibernateUtils.longFrom(idSisma));
        query.setParameter("nomefile", nomefile);

        return (PigSismaDocumenti) query.getSingleResult();
    }

    public static class DatiRecuperoDto {

        private String nomeAmbiente;
        private String nomeEnte;
        private String nomeStruttura;
        private long anno;
        private String numero;
        private String nomeTipoRegistro;

        public String getNomeAmbiente() {
            return nomeAmbiente;
        }

        public void setNomeAmbiente(String nomeAmbiente) {
            this.nomeAmbiente = nomeAmbiente;
        }

        public String getNomeEnte() {
            return nomeEnte;
        }

        public void setNomeEnte(String nomeEnte) {
            this.nomeEnte = nomeEnte;
        }

        public String getNomeStruttura() {
            return nomeStruttura;
        }

        public void setNomeStruttura(String nomeStruttura) {
            this.nomeStruttura = nomeStruttura;
        }

        public long getAnno() {
            return anno;
        }

        public void setAnno(long anno) {
            this.anno = anno;
        }

        public String getNumero() {
            return numero;
        }

        public void setNumero(String numero) {
            this.numero = numero;
        }

        public String getNomeTipoRegistro() {
            return nomeTipoRegistro;
        }

        public void setNomeTipoRegistro(String nomeTipoRegistro) {
            this.nomeTipoRegistro = nomeTipoRegistro;
        }

    }

}
