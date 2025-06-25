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
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.eng.sacerasi.strumentiUrbanistici.ejb;

import it.eng.paginator.util.HibernateUtils;
import it.eng.sacerasi.entity.PigStrumUrbAtto;
import it.eng.sacerasi.entity.PigStrumUrbDocumenti;
import it.eng.sacerasi.entity.PigStrumUrbPianoDocReq;
import it.eng.sacerasi.entity.PigStrumUrbPianoStato;
import it.eng.sacerasi.entity.PigStrumUrbStoricoStati;
import it.eng.sacerasi.entity.PigStrumUrbValDoc;
import it.eng.sacerasi.entity.PigStrumentiUrbanistici;
import it.eng.sacerasi.entity.PigStrumentiUrbanistici.TiStato;
import it.eng.sacerasi.entity.PigVers;
import it.eng.sacerasi.helper.GenericHelper;
import it.eng.sacerasi.strumentiUrbanistici.dto.RicercaStrumentiUrbanisticiDTO;
import it.eng.sacerasi.viewEntity.PigVSuCalcolaAmbitoTerr;
import it.eng.sacerasi.viewEntity.PigVSuCheck;
import it.eng.sacerasi.viewEntity.PigVSuLisDocsPiano;
import static it.eng.sacerasi.web.util.ComboGetter.CAMPO_FLAG;
import static it.eng.sacerasi.web.util.ComboGetter.CAMPO_VALORE;
import static it.eng.sacerasi.web.util.ComboGetter.CAMPO_ANNO;
import it.eng.spagoLite.db.base.row.BaseRow;
import it.eng.spagoLite.db.base.table.BaseTable;
import it.eng.spagoLite.db.decodemap.DecodeMapIF;
import it.eng.spagoLite.db.oracle.decode.DecodeMap;
import java.math.BigDecimal;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.Query;
import org.apache.commons.lang3.time.DateUtils;

/**
 *
 * @author MIacolucci
 */
@SuppressWarnings("unchecked")
@Stateless
@LocalBean
public class StrumentiUrbanisticiHelper extends GenericHelper {

    public List<String> findTipiStrumentiUrbanistici() {
        String queryStr = "SELECT DISTINCT p.nmTipoStrumentoUrbanistico FROM PigVSuList p ORDER BY p.nmTipoStrumentoUrbanistico";
        Query query = getEntityManager().createQuery(queryStr);
        return query.getResultList();
    }

    public Query findSUByVersAndStates(RicercaStrumentiUrbanisticiDTO rDTO, BigDecimal idVers, Set<TiStato> set) {
        String queryStr = "SELECT s, p FROM PigStrumentiUrbanistici s JOIN s.pigStrumUrbPianoStato p WHERE s.pigVer.idVers=:idVers AND s.tiStato IN :set ";

        if (rDTO.isTiStrumentoUrbanistico()) {
            queryStr += " AND p.nmTipoStrumentoUrbanistico = :nmTipoStrumentoUrbanistico ";
        }

        if (rDTO.isNmFaseElaborazione()) {
            queryStr += " AND p.tiFaseStrumento = :tiFaseStrumento ";
        }

        if (rDTO.isDtCreazione()) {
            queryStr += " AND s.data >= :dtCreazioneStart AND s.data < :dtCreazioneEnd";
        }

        if (rDTO.isCdOggetto()) {
            queryStr += " AND LOWER(s.dsDescrizione) LIKE LOWER(:oggetto) ";
        }

        if (rDTO.isAnno()) {
            queryStr += " AND s.anno = :anno ";
        }

        if (rDTO.isCdNumero()) {
            queryStr += " AND LOWER(s.cdKey) LIKE LOWER(:numero) ";
        }

        if (rDTO.isNmStato()) {
            set = EnumSet.of(TiStato.valueOf(rDTO.getNmStato()));
        }

        queryStr += " ORDER BY s.dtCreazione DESC";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idVers", HibernateUtils.longFrom(idVers));
        query.setParameter("set", set);

        if (rDTO.isTiStrumentoUrbanistico()) {
            query.setParameter("nmTipoStrumentoUrbanistico", rDTO.getTiStrumentoUrbanistico());
        }

        if (rDTO.isNmFaseElaborazione()) {
            query.setParameter("tiFaseStrumento", rDTO.getNmFaseElaborazione());
        }

        if (rDTO.isDtCreazione()) {
            query.setParameter("dtCreazioneStart", rDTO.getDtCreazione());
            query.setParameter("dtCreazioneEnd", DateUtils.addDays(rDTO.getDtCreazione(), 1));
        }

        if (rDTO.isCdOggetto()) {
            query.setParameter("oggetto", "%" + rDTO.getCdOggetto() + "%");
        }

        if (rDTO.isAnno()) {
            query.setParameter("anno", new BigDecimal(rDTO.getAnno()));
        }

        if (rDTO.isCdNumero()) {
            query.setParameter("numero", "%" + rDTO.getCdNumero() + "%");
        }

        return query;
    }

    public List<PigStrumentiUrbanistici> findSUByVersAndCdKey(PigVers pigVer, String cdKey) {
        String queryStr = "SELECT s FROM PigStrumentiUrbanistici s WHERE s.pigVer=:pigVer AND s.cdKey=:cdKey";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("pigVer", pigVer);
        query.setParameter("cdKey", cdKey);
        return query.getResultList();
    }

    // MEV29495 - ora elenca solo gli SU in stato VERSATO
    public List<PigStrumentiUrbanistici> findNumeriByVersAnnoTipoSUFase(PigVers pigVer, BigDecimal anno,
            String nmTipoStrumento, String fase) {
        String queryStr = "SELECT s FROM PigStrumentiUrbanistici s JOIN s.pigStrumUrbPianoStato ps WHERE s.pigVer=:pigVer AND s.anno=:anno AND ps.nmTipoStrumentoUrbanistico=:nmTipoStrumento AND ps.tiFaseStrumento = :fase AND s.tiStato = 'VERSATO'";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("pigVer", pigVer);
        query.setParameter("anno", anno);
        query.setParameter("nmTipoStrumento", nmTipoStrumento);
        query.setParameter("fase", fase);
        return query.getResultList();
    }

    public PigStrumentiUrbanistici getSUByVersAndCdKey(PigVers pigVer, String cdKey) {
        List<PigStrumentiUrbanistici> l = findSUByVersAndCdKey(pigVer, cdKey);
        return l.isEmpty() ? null : l.iterator().next();
    }

    public List<PigStrumUrbPianoStato> findPigStrumUrbPianoStatoByNomeTipo(String nomeTipo) {
        String queryStr = "SELECT p FROM PigStrumUrbPianoStato p WHERE p.nmTipoStrumentoUrbanistico=:nome ORDER BY p.idStrumUrbPianoStato";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("nome", nomeTipo);
        return query.getResultList();
    }

    public PigStrumUrbPianoStato getPigStrumUrbPianoStatoByNomeTipoByTipoAndFase(String nmTipoStrumentoUrbanistico,
            String tiFaseStrumento) {
        String queryStr = "SELECT p FROM PigStrumUrbPianoStato p WHERE p.nmTipoStrumentoUrbanistico=:nmTipoStrumentoUrbanistico AND p.tiFaseStrumento=:tiFaseStrumento";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("nmTipoStrumentoUrbanistico", nmTipoStrumentoUrbanistico);
        query.setParameter("tiFaseStrumento", tiFaseStrumento);
        List<PigStrumUrbPianoStato> list = query.getResultList();
        if (!list.isEmpty()) {
            return list.get(0);
        } else {
            return null;
        }
    }

    public PigStrumUrbPianoDocReq getPigStrumUrbPianoDocReq(PigStrumUrbPianoStato pigStrumUrbPianoStato,
            PigStrumUrbValDoc pigStrumUrbValDoc) {
        String queryStr = "SELECT r FROM PigStrumUrbPianoDocReq r WHERE r.pigStrumUrbPianoStato=:pigStrumUrbPianoStato AND r.pigStrumUrbValDoc=:pigStrumUrbValDoc";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("pigStrumUrbPianoStato", pigStrumUrbPianoStato);
        query.setParameter("pigStrumUrbValDoc", pigStrumUrbValDoc);
        List<PigStrumUrbPianoDocReq> list = query.getResultList();
        if (!list.isEmpty()) {
            return list.get(0);
        } else {
            return null;
        }
    }

    public PigStrumUrbValDoc getPigStrumUrbValDocByNomeTipoDoc(String nmTipoDocumento) {
        String queryStr = "SELECT p FROM PigStrumUrbValDoc p WHERE p.nmTipoDocumento=:nmTipoDocumento";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("nmTipoDocumento", nmTipoDocumento);
        List<PigStrumUrbValDoc> list = query.getResultList();
        if (!list.isEmpty()) {
            return list.get(0);
        } else {
            return null;
        }
    }

    public void cancellaSU(PigStrumentiUrbanistici pigStrumentiUrbanistici) {
        removeEntity(pigStrumentiUrbanistici, true);
    }

    public PigStrumUrbDocumenti getPigStrumUrbDocumentiBySuNmFileOrig(PigStrumentiUrbanistici pigStrumentiUrbanistici,
            String nmFileOrig) {
        String queryStr = "SELECT p FROM PigStrumUrbDocumenti p WHERE p.pigStrumentiUrbanistici=:pigStrumentiUrbanistici AND p.nmFileOrig=:nmFileOrig AND p.flDeleted='0'";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("pigStrumentiUrbanistici", pigStrumentiUrbanistici);
        query.setParameter("nmFileOrig", nmFileOrig);
        List<PigStrumUrbDocumenti> list = query.getResultList();
        if (!list.isEmpty()) {
            return list.get(0);
        } else {
            return null;
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public PigStrumentiUrbanistici aggiornaStatoInNuovaTransazione(PigStrumentiUrbanistici su,
            PigStrumentiUrbanistici.TiStato tiStato) {
        return aggiornaStato(su, tiStato);
    }

    public PigStrumentiUrbanistici aggiornaStato(PigStrumentiUrbanistici su, PigStrumentiUrbanistici.TiStato tiStato) {
        PigStrumentiUrbanistici pigStrumentiUrbanistici = null;
        pigStrumentiUrbanistici = getEntityManager().find(PigStrumentiUrbanistici.class,
                su.getIdStrumentiUrbanistici());

        // MEV 31096
        creaStatoStorico(pigStrumentiUrbanistici, pigStrumentiUrbanistici.getTiStato().name(),
                pigStrumentiUrbanistici.getDtStato(), pigStrumentiUrbanistici.getCdErr() != null
                        ? pigStrumentiUrbanistici.getCdErr() + " - " + pigStrumentiUrbanistici.getDsErr() : "");

        pigStrumentiUrbanistici.setTiStato(tiStato);
        pigStrumentiUrbanistici.setDtStato(new Date());

        return pigStrumentiUrbanistici;
    }

    public PigStrumentiUrbanistici getPigStrumUrbByCdKeyAndTiStato(String cdKey,
            PigStrumentiUrbanistici.TiStato tiStato) {
        String queryStr = "SELECT p FROM PigStrumentiUrbanistici p WHERE p.cdKey=:cdKey AND p.tiStato=:tiStato";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("cdKey", cdKey);
        query.setParameter("tiStato", tiStato);
        List<PigStrumentiUrbanistici> list = query.getResultList();
        if (!list.isEmpty()) {
            return list.get(0);
        } else {
            return null;
        }
    }

    // MEV 27442
    public List<BigDecimal> findPigStrumentiUrbanisticiAnno() {
        String queryStr = "SELECT DISTINCT(p.anno) FROM PigStrumentiUrbanistici p order by p.anno";
        Query query = getEntityManager().createQuery(queryStr);
        return query.getResultList();
    }

    // MEV 27442
    public DecodeMapIF getStrumentiUrbanisticiAnno() {
        List<BigDecimal> anni = this.findPigStrumentiUrbanisticiAnno();
        DecodeMap anniDecodeMap = new DecodeMap();
        BaseTable bt = new BaseTable();
        for (BigDecimal anno : anni) {
            if (anno != null) {
                BaseRow row = new BaseRow();
                row.setString(CAMPO_ANNO, anno.toString());
                bt.add(row);
            }
        }
        anniDecodeMap.populatedMap(bt, CAMPO_ANNO, CAMPO_ANNO);

        return anniDecodeMap;
    }

    public PigVSuCheck getDatiNavigazionePerSU(BigDecimal idStrumentiUrbanistici) {
        String queryStr = "SELECT p FROM PigVSuCheck p WHERE p.idStrumentiUrbanistici=:idStrumentiUrbanistici";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idStrumentiUrbanistici", idStrumentiUrbanistici);
        List<PigVSuCheck> list = query.getResultList();
        if (!list.isEmpty()) {
            return list.get(0);
        } else {
            return null;
        }
    }

    public List<PigVSuLisDocsPiano> findPigVSuLisDocsPianoByTipoStrumentoFase(String nmTipoStrumento,
            String tiFaseStrumento) {
        String queryStr = "SELECT s FROM PigVSuLisDocsPiano s WHERE s.id.nmTipoStrumento=:nmTipoStrumento AND s.id.tiFaseStrumento=:tiFaseStrumento";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("nmTipoStrumento", nmTipoStrumento);
        query.setParameter("tiFaseStrumento", tiFaseStrumento);
        return query.getResultList();
    }

    public DatiAnagraficiDto getDatiAnagraficiByVers(PigVers vers) {
        return getDatiAnagraficiByIdVers(new BigDecimal(vers.getIdVers()));
    }

    public DatiAnagraficiDto getDatiAnagraficiByIdVers(BigDecimal idVers) {
        DatiAnagraficiDto dto = null;
        String queryStr = "select p FROM PigVSuCalcolaAmbitoTerr p WHERE p.idVers=:idVers";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idVers", idVers);
        List<PigVSuCalcolaAmbitoTerr> l = query.getResultList();
        if (!l.isEmpty()) {
            PigVSuCalcolaAmbitoTerr p = l.get(0);
            dto = new DatiAnagraficiDto();
            dto.setIdEnteSiam(p.getIdVers());
            dto.setDenominazione(p.getDenominazione());
            if (p.getTipologia() != null) {
                dto.setTipologia(p.getTipologia().name());
            }
            dto.setUnione(p.getUnione());
            dto.setProvincia(p.getProvincia());
        }
        return dto;
    }

    public BigDecimal getDimensioneDocumentiBySU(PigStrumentiUrbanistici pigStrumentiUrbanistici) {
        BigDecimal dimensione = null;
        String queryStr = "SELECT SUM(d.dimensione) FROM PigStrumUrbDocumenti d WHERE d.pigStrumentiUrbanistici=:pigStrumentiUrbanistici AND d.flDeleted='0'";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("pigStrumentiUrbanistici", pigStrumentiUrbanistici);
        List<BigDecimal> l = query.getResultList();
        if (!l.isEmpty()) {
            dimensione = l.iterator().next();
        }
        return dimensione;
    }

    public Object[] findDatiAmbienteByIdSU(BigDecimal id) {
        Object[] ogg = null;
        // per correggere il problema della SUE#26429 - [problema rapporto di versamento] COMGUASTALLA_AOO1_SU
        // inserita join con pigVer
        String queryStr = "select su, str, en, amb "
                + "from   PigStrumentiUrbanistici su, PigObject po, PigObject son, PigUnitaDocObject udo, "
                + "       SIUsrOrganizIam orgIam, OrgStrut str, OrgEnte en, OrgAmbiente amb "
                + "where  po.pigVer = su.pigVer and po.cdKeyObject = su.cdKey "
                + "and    udo.pigObject.idObject = son.idObject "
                + "and    orgIam.idOrganizIam = udo.idOrganizIam and son.pigObjectPadre.idObject = po.idObject "
                + "and    str.idStrut = orgiam.idOrganizApplic and en.idEnte = str.orgEnte.idEnte "
                + "and    amb.idAmbiente = en.orgAmbiente.idAmbiente and su.idStrumentiUrbanistici=:id";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("id", HibernateUtils.longFrom(id));
        List<Object[]> l = query.getResultList();
        if (!l.isEmpty()) {
            ogg = l.iterator().next();
        }
        return ogg;
    }

    public static class DatiAnagraficiDto {

        private BigDecimal idEnteSiam;
        private String denominazione;
        private String tipologia;
        private String unione;
        private String provincia;

        public BigDecimal getIdEnteSiam() {
            return idEnteSiam;
        }

        public void setIdEnteSiam(BigDecimal idEnteSiam) {
            this.idEnteSiam = idEnteSiam;
        }

        public String getDenominazione() {
            return denominazione;
        }

        public void setDenominazione(String denominazione) {
            this.denominazione = denominazione;
        }

        public String getTipologia() {
            return tipologia;
        }

        public void setTipologia(String tipologia) {
            this.tipologia = tipologia;
        }

        public String getUnione() {
            return unione;
        }

        public void setUnione(String unione) {
            this.unione = unione;
        }

        public String getProvincia() {
            return provincia;
        }

        public void setProvincia(String provincia) {
            this.provincia = provincia;
        }

    }

    public boolean existsPigStrumUrbDocumenti(BigDecimal idStrumentoUrbanistico) {
        Query query = getEntityManager()
                .createQuery("SELECT strumentiUrbanistici FROM PigStrumentiUrbanistici strumentiUrbanistici "
                        + "WHERE strumentiUrbanistici.idStrumentiUrbanistici = :idStrumentoUrbanistico "
                        + "AND EXISTS (SELECT strumUrbDocumenti FROM PigStrumUrbDocumenti strumUrbDocumenti "
                        + "WHERE strumUrbDocumenti.pigStrumentiUrbanistici = strumentiUrbanistici "
                        + "AND strumUrbDocumenti.flEsitoVerifica = '0' AND strumUrbDocumenti.flDeleted != '1')");
        query.setParameter("idStrumentoUrbanistico", HibernateUtils.longFrom(idStrumentoUrbanistico));
        List<PigStrumentiUrbanistici> lista = query.getResultList();
        return !lista.isEmpty();
    }

    // MEV 26278
    public List<String> findPigStrumUrbFaseStrumento() {
        String queryStr = "SELECT DISTINCT(p.tiFaseStrumento) FROM PigStrumUrbPianoStato p ORDER BY p.tiFaseStrumento";
        Query query = getEntityManager().createQuery(queryStr);
        return query.getResultList();
    }

    // MEV 26278
    public DecodeMap getFaseStrumentoMap() {
        List<String> fasi = this.findPigStrumUrbFaseStrumento();
        BaseTable bt = new BaseTable();
        for (String fase : fasi) {
            BaseRow br = new BaseRow();
            br.setString(CAMPO_FLAG, fase);
            br.setString(CAMPO_VALORE, fase);
            bt.add(br);
        }

        DecodeMap fasiDecodeMap = new DecodeMap();
        fasiDecodeMap.populatedMap(bt, CAMPO_VALORE, CAMPO_FLAG);

        return fasiDecodeMap;
    }

    // MEV 26936
    public DecodeMap getPigStrumUrbAttoDecodeMap(String tipo) {
        List<PigStrumUrbAtto> atti = this.findAllPigStrumUrbAtto(tipo);
        DecodeMap attiDecodeMap = new DecodeMap();
        BaseTable bt = new BaseTable();
        for (PigStrumUrbAtto psua : atti) {
            BaseRow br = new BaseRow();
            br.setString(CAMPO_FLAG, psua.getCdNome() + " - " + psua.getDsDescrizione());
            br.setBigDecimal(CAMPO_VALORE, new BigDecimal(psua.getIdAtto()));
            bt.add(br);
        }
        attiDecodeMap.populatedMap(bt, CAMPO_VALORE, CAMPO_FLAG);

        return attiDecodeMap;
    }

    // MEV 26936
    public List<PigStrumUrbAtto> findAllPigStrumUrbAtto(String tipo) {
        String queryStr = "SELECT a FROM PigStrumUrbAtto a WHERE a.tiAtto = :tipo ORDER BY a.cdNome";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("tipo", tipo);
        return query.getResultList();
    }

    // MEV 26936
    public PigStrumUrbAtto findPigStrumUrbAtto(String tipo, String nome) {
        String queryStr = "SELECT a FROM PigStrumUrbAtto a WHERE a.tiAtto = :tipo and a.cdNome = :nome";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("tipo", tipo);
        query.setParameter("nome", nome);
        return (PigStrumUrbAtto) query.getSingleResult();
    }

    // MEV 26936
    public String estraiAttoDaIdentificativo(String cdKey) {
        String[] parts = cdKey.split("_");
        if (parts.length >= 2) {
            return parts[1];
        } else {
            return null;
        }
    }

    // MEV 26936
    public String estraiNumeroCollegamento(String cdKey) {
        return cdKey.substring(5);
    }

    // MEV 29495
    public DecodeMap getSUVersatiAnnoByPianoStato(String nmTipoStrumentoUrbanistico, String tiFaseStrumento) {
        String queryStr = "SELECT DISTINCT(s.anno) FROM PigStrumentiUrbanistici s WHERE s.tiStato = 'VERSATO' "
                + "AND s.pigStrumUrbPianoStato.nmTipoStrumentoUrbanistico = :nmTipoStrumentoUrbanistico "
                + "AND s.pigStrumUrbPianoStato.tiFaseStrumento = :tiFaseStrumento ORDER BY s.anno";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("nmTipoStrumentoUrbanistico", nmTipoStrumentoUrbanistico);
        query.setParameter("tiFaseStrumento", tiFaseStrumento);

        List<BigDecimal> anni = query.getResultList();
        DecodeMap anniDecodeMap = new DecodeMap();
        BaseTable bt = new BaseTable();
        for (BigDecimal anno : anni) {
            if (anno != null) {
                BaseRow br = new BaseRow();
                br.setString(CAMPO_FLAG, anno.toString());
                br.setString(CAMPO_VALORE, anno.toString());
                bt.add(br);
            }
        }
        anniDecodeMap.populatedMap(bt, CAMPO_VALORE, CAMPO_FLAG);

        return anniDecodeMap;
    }

    // MEV 31096
    public List<PigStrumUrbStoricoStati> PigStrumUrbStoricoStatiFromStrumentoUrbanistico(BigDecimal idStrumento) {
        Query query = getEntityManager().createQuery(
                "SELECT s FROM PigStrumUrbStoricoStati s WHERE s.pigStrumentiUrbanistici.idStrumentiUrbanistici = :idStrumento ORDER BY s.tsRegStato DESC");
        query.setParameter("idStrumento", HibernateUtils.longFrom(idStrumento));
        return query.getResultList();
    }

    // MEV 31096
    public void creaStatoStorico(PigStrumentiUrbanistici pigStrumentiUrbanistici, String stato, Date dtRegStato,
            String descrizione) {
        PigStrumUrbStoricoStati pigStrumUrbStoricoStati = new PigStrumUrbStoricoStati();
        pigStrumUrbStoricoStati.setPigStrumentiUrbanistici(pigStrumentiUrbanistici);
        pigStrumUrbStoricoStati.setTiStato(stato);
        pigStrumUrbStoricoStati.setTsRegStato(dtRegStato);
        pigStrumUrbStoricoStati.setDescrizione(descrizione);
        getEntityManager().persist(pigStrumUrbStoricoStati);
    }

    // MEV 34843
    public PigStrumUrbDocumenti getPigStrumUrbDocumentiByName(String nomefile, BigDecimal idStrumento) {
        Query query = getEntityManager().createQuery(
                "SELECT psud FROM PigStrumUrbDocumenti psud WHERE psud.pigStrumentiUrbanistici.idStrumentiUrbanistici = :idStrumento AND"
                        + " psud.nmFileOrig = :nomefile");
        query.setParameter("idStrumento", HibernateUtils.longFrom(idStrumento));
        query.setParameter("nomefile", nomefile);

        return (PigStrumUrbDocumenti) query.getSingleResult();
    }
}
