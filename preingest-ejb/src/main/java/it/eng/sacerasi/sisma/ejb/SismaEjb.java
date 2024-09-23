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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import it.eng.paginator.helper.LazyListHelper;
import it.eng.parer.objectstorage.dto.ObjectStorageBackend;
import it.eng.parer.objectstorage.exceptions.ObjectStorageException;
import it.eng.parer.objectstorage.helper.SalvataggioBackendHelper;
import it.eng.sacerasi.common.Constants;
import it.eng.sacerasi.entity.IamUser;
import it.eng.sacerasi.entity.PigAmbienteVers;
import it.eng.sacerasi.entity.PigErrore;
import it.eng.sacerasi.entity.PigSisma;
import it.eng.sacerasi.entity.PigSismaDocEntry;
import it.eng.sacerasi.entity.PigSismaDocumenti;
import it.eng.sacerasi.entity.PigSismaFaseProgetto;
import it.eng.sacerasi.entity.PigSismaFinanziamento;
import it.eng.sacerasi.entity.PigSismaPianoDocReq;
import it.eng.sacerasi.entity.PigSismaProgettiAg;
import it.eng.sacerasi.entity.PigSismaStatoProgetto;
import it.eng.sacerasi.entity.PigSismaValAtto;
import it.eng.sacerasi.entity.PigSismaValDoc;
import it.eng.sacerasi.entity.PigVers;
import it.eng.sacerasi.messages.MessaggiHelper;
import it.eng.sacerasi.sisma.dto.DatiAnagraficiDto;
import it.eng.sacerasi.sisma.dto.DocSismaDto;
import it.eng.sacerasi.sisma.dto.DocUploadDto;
import it.eng.sacerasi.sisma.dto.EsitoSalvataggioSisma;
import it.eng.sacerasi.sisma.dto.NavigazioneSismaDto;
import it.eng.sacerasi.sisma.dto.RicercaSismaDTO;
import it.eng.sacerasi.sisma.dto.SismaDto;
import it.eng.sacerasi.util.DateUtil;
import it.eng.sacerasi.viewEntity.PigVSismaChecks;
import it.eng.sacerasi.web.helper.ConfigurationHelper;
import it.eng.sacerasi.web.util.ComboGetter;
import it.eng.sacerasi.web.util.Utils;
import it.eng.sacerasi.ws.replicaUtente.ejb.ModificaUtenteEjb;
import it.eng.spagoLite.db.base.BaseTableInterface;
import it.eng.spagoLite.db.base.row.BaseRow;
import it.eng.spagoLite.db.base.table.BaseTable;
import it.eng.spagoLite.db.decodemap.DecodeMapIF;

/**
 *
 * @author MIacolucci
 */
@SuppressWarnings("rawtypes")
@Stateless
@LocalBean
public class SismaEjb {

    @EJB
    private SismaHelper sismaHelper;
    @EJB
    private ModificaUtenteEjb modificaUtenteHelper;
    @EJB
    private MessaggiHelper messaggiHelper;
    @EJB
    private ConfigurationHelper configurationHelper;
    @EJB
    private SalvataggioBackendHelper salvataggioBackendHelper;
    @EJB
    private LazyListHelper lazyListHelper;

    public BaseTable findSismaByVers(RicercaSismaDTO rDTO, BigDecimal idVers) {
        return lazyListHelper.getTableBean(sismaHelper.findSismaByVers(rDTO, idVers), this::findSismaByStatesTB);
    }

    public BaseTable findSisma() {
        return lazyListHelper.getTableBean(sismaHelper.findSisma(), this::findSismaByStatesTB);
    }

    public BaseTable findSismaTranne(RicercaSismaDTO rDTO, PigSisma.TiStato... stati) {
        return lazyListHelper.getTableBean(sismaHelper.findSismaTranne(rDTO, stati), this::findSismaByStatesTB);
    }

    private BaseTable findSismaByStatesTB(List<Object[]> sismas) {
        BaseTable strumUrbTable = new BaseTable();
        if (sismas != null && !sismas.isEmpty()) {
            for (Object[] sisma : sismas) {
                PigSisma pigSisma = (PigSisma) sisma[0];
                PigVers pigVers = (PigVers) sisma[1];
                // MEV26290
                Enum<Constants.TipoVersatore> tipoVersatore = sismaHelper.getTipoVersatore(pigVers);
                PigSismaFinanziamento pigSismaFinanziamento = (PigSismaFinanziamento) sisma[2];
                PigSismaProgettiAg pigSismaProgettiAg = (PigSismaProgettiAg) sisma[3];
                PigSismaFaseProgetto pigSismaFaseProgetto = (PigSismaFaseProgetto) sisma[4];
                PigSismaStatoProgetto statoProgetto = (PigSismaStatoProgetto) sisma[6];

                BaseRow riga = new BaseRow();
                riga.setBigDecimal("id_sisma", new BigDecimal(pigSisma.getIdSisma()));
                riga.setString("codice_intervento", pigSismaProgettiAg.getCodiceIntervento());
                riga.setString("ds_fase_sisma", pigSismaFaseProgetto.getDsFaseSisma());
                riga.setBigDecimal("anno", new BigDecimal(pigSisma.getAnno().longValueExact()));
                riga.setString("cd_key", pigSisma.getCdKey());
                riga.setString("ti_stato", pigSisma.getTiStato().name());
                riga.setString("stato_progetto", statoProgetto.getDsStatoProgetto());
                riga.setString("oggetto", pigSisma.getOggetto());
                riga.setTimestamp("dt_creazione", new java.sql.Timestamp(pigSisma.getDtCreazione().getTime()));
                riga.setBigDecimal("dimensione", sismaHelper.getDimensioneDocumentiBySisma(pigSisma));
                riga.setBigDecimal("id_versatore", new BigDecimal(pigSisma.getPigVer().getIdVers()));
                riga.setString("nm_versatore", pigSisma.getPigVer().getNmVers());
                riga.setString("nm_sa", pigSismaProgettiAg.getSoggettoAttuatore());
                riga.setString("ds_tipo_finanziamento", pigSismaFinanziamento.getDsTipoFinanziamento());

                // Accende o meno il download del rapporto di versamento
                if (!tipoVersatore.equals(Constants.TipoVersatore.SA_PRIVATO)
                        && (pigSisma.getTiStato().name().equals(PigSisma.TiStato.VERSATO.name())
                                || pigSisma.getTiStato().name().equals(PigSisma.TiStato.COMPLETATO.name()))) {
                    riga.setString("download", "download");
                }
                if (pigSisma.getTiStato().name().equals(PigSisma.TiStato.COMPLETATO.name())) {
                    riga.setString("download_agenzia", "download");
                }
                // In tutti gli stati consente di scaricare la lista di versamento
                riga.setString("download_lista", "download");
                if (pigSisma.getRegistroAg() != null) {
                    riga.setString("id_versamento_agenzia",
                            pigSisma.getRegistroAg() + "-" + pigSisma.getAnnoAg() + "-" + pigSisma.getNumeroAg());
                }
                strumUrbTable.add(riga);
            }

        }

        return strumUrbTable;
    }

    // Cosidera solo i documenti caricati NON CANCELLATI !
    public List<DocSismaDto> findDocumentiCaricatiPerIdSismaDto(PigSisma pigSisma) {
        ArrayList<DocSismaDto> al = new ArrayList<>();
        PigSismaFaseProgetto pigSismaFaseProgetto = pigSisma.getPigSismaFaseProgetto();
        List<PigSismaDocumenti> listaDoc = pigSisma.getPigSismaDocumentis();
        for (PigSismaDocumenti pigSismaDocumenti : listaDoc) {
            if (pigSismaDocumenti.getFlDeleted().equals(Constants.DB_FALSE)) {
                DocSismaDto dto = new DocSismaDto();
                dto.setIdSismaDocumenti(new BigDecimal(pigSismaDocumenti.getIdSismaDocumenti()));
                dto.setDimensione(pigSismaDocumenti.getDimensione());
                dto.setNumFileCaricati(
                        pigSismaDocumenti.getNumFiles() == null ? 0 : pigSismaDocumenti.getNumFiles().intValueExact());
                dto.setNmFileOrig(pigSismaDocumenti.getNmFileOrig());
                PigSismaValDoc pigSismaValDoc = pigSismaDocumenti.getPigSismaValDoc();
                dto.setNmTipoDocumento(pigSismaValDoc.getNmTipoDocumento());
                PigSismaPianoDocReq pigSismaPianoDocReq = sismaHelper
                        .getPigSismaPianoDocReqByValDoc(pigSismaFaseProgetto, pigSismaValDoc);
                dto.setObbligatorio(pigSismaPianoDocReq.getFlDocObbligatorio().equals(Constants.DB_TRUE));
                dto.setCdErr(pigSismaDocumenti.getCdErr());
                dto.setDsErr(pigSismaDocumenti.getDsErr());
                dto.setFlEsitoVerifica(pigSismaDocumenti.getFlEsitoVerifica().equals(Constants.DB_TRUE));
                dto.setTiVerificaAgenzia(pigSismaDocumenti.getTiVerificaAgenzia());
                dto.setDtCaricamento(pigSismaDocumenti.getDtCaricamento());
                if (pigSismaDocumenti.getFlDocRicaricato().equals(Constants.DB_TRUE)) {
                    dto.setFlDocRicaricato(true);
                }
                // MEV26267 - Aggiunto l'eventuale report di verifica file.
                dto.setBlReport(pigSismaDocumenti.getBlReport());
                al.add(dto);
            }
        }
        return al;
    }

    /*
     * Torna un array du tre Object dove il primo è una BaseTableInterface, il secondo una stringa e il terzo un
     * ArrayList di stringhe con i messaggi per i doc obbligatori non caricati
     */
    public Object[] findDocumentiCaricatiPerIdSismaTB(BigDecimal idSisma) {
        StringBuilder sb = new StringBuilder();
        BaseTable table = new BaseTable();
        ArrayList<String> alObbNonCaricati = new ArrayList<>();
        ArrayList<String> alDocInErrore = new ArrayList<>();
        PigSisma pigSisma = sismaHelper.findById(PigSisma.class, idSisma);
        List<DocUploadDto> listaUpload = findPigVSismaLisDocsPianoByTipoSismaFase(pigSisma);
        List<DocSismaDto> lCaricati = findDocumentiCaricatiPerIdSismaDto(pigSisma);
        /*
         * Se il doc è stato caricato lo mette nel TableBean altrimenti accoda alla stringa da visualizzare per gli
         * altri documenti facoltativi ancora da caricare
         */
        for (DocUploadDto docUploadDto : listaUpload) {
            BaseRow riga = null;
            boolean trovatoTraICaricati = false;
            DocSismaDto dto = null;
            // Cerca il doc tra i caricati...
            for (DocSismaDto docStrumDto : lCaricati) {
                if (docStrumDto.getNmTipoDocumento().equals(docUploadDto.getNmTipoDocumento())) {
                    trovatoTraICaricati = true;
                    dto = docStrumDto;
                    break;
                }
            } // Uscito dall'analisi della ricerca nei caricati...
            if (trovatoTraICaricati) { // Lo mette nella lista dei caricati obbligatori
                riga = new BaseRow();
                riga.setBigDecimal("id_sisma_documenti", dto.getIdSismaDocumenti());
                riga.setString("nm_tipo_documento", dto.getNmTipoDocumento());
                riga.setString("nm_file_orig", dto.getNmFileOrig());
                riga.setString("fl_obbligatorio", (dto.isObbligatorio() ? Constants.DB_TRUE : Constants.DB_FALSE));
                riga.setBigDecimal("num_files", new BigDecimal(dto.getNumFileCaricati()));
                riga.setString("dimensione", Utils.convertBytesToFormattedString(dto.getDimensione()));
                riga.setString("cd_err", dto.getCdErr());
                riga.setString("ds_err", dto.getDsErr());
                riga.setString("fl_esito_verifica", (dto.isFlEsitoVerifica() ? Constants.DB_TRUE : Constants.DB_FALSE));
                riga.setString("ti_verifica_agenzia", dto.getTiVerificaAgenzia());
                riga.setTimestamp("dt_caricamento", new java.sql.Timestamp(dto.getDtCaricamento().getTime()));

                // MEV26267 - se è presente un report della verifica fatta sui file, permettine il download
                if (dto.getBlReport() != null) {
                    riga.setString("download_bl_report", "download");
                }

                table.add(riga);
                if (dto.getDsErr() != null && !dto.getDsErr().equals("")) {
                    alDocInErrore.add(dto.getDsErr());
                }
            } else { // Aggiunge alla lista dei messaggi da stampare per i doc obbligatori non caricati
                if (docUploadDto.isObbligatorio()) {
                    alObbNonCaricati.add(
                            String.format("Il file zip %s non è stato caricato. Impossibile procedere al versamento.",
                                    docUploadDto.getNmTipoDocumento()));
                } else {
                    sb.append(docUploadDto.getNmTipoDocumento()).append(", ");
                }
            }
        }
        // Oggetti di ritorno...
        Object[] ogg = new Object[4];
        ogg[0] = table;
        // toglie l'ultima virgola!
        if (sb.length() > 0) {
            ogg[1] = sb.substring(0, sb.length() - 2);
        } else {
            ogg[1] = sb.toString();
        }
        ogg[2] = alObbNonCaricati;
        ogg[3] = alDocInErrore;
        return ogg;
    }

    public BaseTableInterface findPigSismaProgettiAgByIdEnteFinanziamentoTB(BigDecimal idEnteSiam,
            BigDecimal idSismaFinanziamento) {
        List<PigSismaProgettiAg> l = sismaHelper.getPigSismaProgettiAgByIdEnteFinanziamento(idEnteSiam,
                idSismaFinanziamento);
        BaseTable tab = new BaseTable();
        for (PigSismaProgettiAg pigSismaProgettiAg : l) {
            BaseRow riga = new BaseRow();
            riga.setBigDecimal("id_sisma_progetti_ag", new BigDecimal(pigSismaProgettiAg.getIdSismaProgettiAg()));
            riga.setString("codice_intervento", pigSismaProgettiAg.getCodiceIntervento());
            tab.add(riga);
        }
        return tab;
    }

    public BaseTableInterface findPigSismaFinanziamentoTB() {
        BaseTable tab = new BaseTable();
        List<PigSismaFinanziamento> valori = sismaHelper.findPigSismaFinanziamento();
        for (PigSismaFinanziamento str : valori) {
            BaseRow riga = new BaseRow();
            riga.setBigDecimal("id_sisma_finanziamento", new BigDecimal(str.getIdSismaFinanziamento()));
            riga.setString("ds_tipo_finanziamento", str.getDsTipoFinanziamento());
            tab.add(riga);
        }
        return tab;
    }

    public BaseTableInterface findPigSismaFinanziamentoByIdVersTB(BigDecimal idVers) {
        BaseTable tab = new BaseTable();
        List<PigSismaFinanziamento> valori = sismaHelper.findPigSismaFinanziamentoByIdVers(idVers);
        for (PigSismaFinanziamento str : valori) {
            BaseRow riga = new BaseRow();
            riga.setBigDecimal("id_sisma_finanziamento", new BigDecimal(str.getIdSismaFinanziamento()));
            riga.setString("ds_tipo_finanziamento", str.getDsTipoFinanziamento());
            tab.add(riga);
        }
        return tab;
    }

    public BaseTableInterface findPigSismaFaseByFinTB(BigDecimal idSismaFinanziamento) {
        List<PigSismaFaseProgetto> valori = sismaHelper.findPigSismaFaseProgettoByFin(idSismaFinanziamento);
        return findPigSismaFaseTB(valori);
    }

    private BaseTableInterface findPigSismaFaseTB(List<PigSismaFaseProgetto> valori) {
        BaseTable tab = new BaseTable();
        for (PigSismaFaseProgetto str : valori) {
            BaseRow riga = new BaseRow();
            riga.setBigDecimal("id_sisma_fase_progetto", new BigDecimal(str.getIdSismaFaseProgetto()));
            riga.setString("ds_fase_sisma", str.getDsFaseSisma());
            tab.add(riga);
        }
        return tab;
    }

    public BaseTableInterface findPigSismaValAtto() {
        BaseTable t = new BaseTable();
        List<PigSismaValAtto> l = sismaHelper.findPigSismaValAtto();
        for (PigSismaValAtto pigSismaValAtto : l) {
            BaseRow r = new BaseRow();
            r.setBigDecimal("id_sisma_val_atto", new BigDecimal(pigSismaValAtto.getIdSismaValAtto()));
            r.setString("nm_tipo_atto", pigSismaValAtto.getNmTipoAtto());
            t.add(r);
        }
        return t;
    }

    public BaseTableInterface findPigSismaStatoProgettoByIdSismaFaseProgettoTB(BigDecimal idSismaFaseProgetto) {
        BaseTable t = new BaseTable();
        List<PigSismaStatoProgetto> l = sismaHelper.findPigSismaStatoProgettoByIdSismaFaseProgetto(idSismaFaseProgetto);
        for (PigSismaStatoProgetto pigSismaStatoProgetto : l) {
            BaseRow r = new BaseRow();
            r.setBigDecimal("id_sisma_stato_progetto", new BigDecimal(pigSismaStatoProgetto.getIdSismaStatoProgetto()));
            r.setString("ds_stato_progetto", pigSismaStatoProgetto.getDsStatoProgetto());
            t.add(r);
        }
        return t;
    }

    public BaseTableInterface findPigSismaStatoProgettoTB() {
        BaseTable t = new BaseTable();
        List<PigSismaStatoProgetto> l = sismaHelper.findPigSismaStatoProgetto();
        for (PigSismaStatoProgetto p : l) {
            BaseRow r = new BaseRow();
            r.setBigDecimal("id_sisma_stato_progetto", new BigDecimal(p.getIdSismaStatoProgetto()));
            r.setString("ds_stato_progetto", p.getDsStatoProgetto());
            t.add(r);
        }
        return t;
    }

    /*
     * NUOVO METODO!!!
     */
    public String getXmlRichiestaRappVersByIdSisma(BigDecimal idSisma, boolean estraiRapportoAgenzia) {
        String xml = null;
        String idVersatoreAgenzia = configurationHelper.getValoreParamApplicByApplic(Constants.ID_VERSATORE_AGENZIA);
        SismaHelper.DatiRecuperoDto datiRecupero = sismaHelper.findDatiPerRecuperoByIdSisma(idSisma,
                estraiRapportoAgenzia, new BigDecimal(idVersatoreAgenzia));
        String versione = configurationHelper
                .getValoreParamApplicByApplic(Constants.NmParamApplic.VERSIONE_XML_RECUP_UD.name());
        String loginname = configurationHelper
                .getValoreParamApplicByApplic(Constants.NmParamApplic.USERID_RECUP_UD.name());
        xml = "<Recupero>\n" + "  <Versione>" + versione + "</Versione>\n" + "  <Versatore>\n" + "    <Ambiente>"
                + datiRecupero.getNomeAmbiente() + "</Ambiente>\n" + "    <Ente>" + datiRecupero.getNomeEnte()
                + "</Ente>\n" + "    <Struttura>" + datiRecupero.getNomeStruttura() + "</Struttura>\n" + "    <UserID>"
                + loginname + "</UserID>\n" + "  </Versatore>\n" + "  <Chiave>\n" + "    <Numero>"
                + datiRecupero.getNumero() + "</Numero>\n" + "    <Anno>" + datiRecupero.getAnno() + "</Anno>\n"
                + "    <TipoRegistro>" + datiRecupero.getNomeTipoRegistro() + "</TipoRegistro>    \n" + "  </Chiave>\n"
                + "</Recupero> \n";
        return xml;
    }

    public String getListaVersamentoString(BigDecimal id) {
        String str = "Lista di versamento\n\n";
        SismaDto dto = getSismaById(id);
        PigSisma pigSisma = sismaHelper.findById(PigSisma.class, id);
        List<DocSismaDto> docs = findDocumentiCaricatiPerIdSismaDto(pigSisma);
        DatiAnagraficiDto dtoDatiAnagrafici = getDatiVersatoreByIdVers(new BigDecimal(pigSisma.getPigVer().getIdVers()),
                pigSisma);
        str += "Soggetto attuatore        : " + dtoDatiAnagrafici.getSoggettoAttuatore() + "\n";
        str += "Natura soggetto attuatore : " + dtoDatiAnagrafici.getNaturaSoggettoAttuatore() + "\n";
        str += "Ente proprietario         : " + dtoDatiAnagrafici.getEnteProprietario() + "\n";
        str += "Natura ente proprietario  : " + dtoDatiAnagrafici.getNaturaEnteProprietario() + "\n";
        str += "Ubicazione comune         : " + dtoDatiAnagrafici.getUbicazioneComune() + "\n";
        str += "Ubicazione provincia      : " + dtoDatiAnagrafici.getUbicazioneProvincia() + "\n";
        str += "Linea di finanziamento    : " + dto.getDsTipoFinanziamento() + "\n";
        str += "Codice intervento         : " + dto.getCodiceIntervento() + "\n";
        str += "Denominazione intervento  : " + dto.getDenominazioneIntervento() + "\n";
        str += "Fase progettuale          : " + dto.getDsFaseSisma() + "\n";
        str += "Stato progetto            : " + dto.getDsStatoProgetto() + "\n";
        str += "Atto                      : " + dto.getNmTipoAtto() + "\n";
        str += "Oggetto                   : " + dto.getOggetto() + "\n";
        str += "Descrizione               : " + (dto.getDsDescrizione() != null ? dto.getDsDescrizione() : "") + "\n";
        str += "Identificativo Sisma      : " + dto.getCdKey() + "\n";
        str += "Stato                     : " + dto.getTiStato() + "\n";
        str += "Data                      : " + DateUtil.formatDateWithSlash(dto.getData()) + "\n";
        str += "Data creazione            : " + DateUtil.formatDateWithSlash(dto.getDtCreazione()) + "\n";
        str += "Data stato corrente       : " + DateUtil.formatDateWithSlash(dto.getDtStato()) + "\n\n";
        str += "Lista files" + "\n" + "\n";
        for (DocSismaDto doc : docs) {
            str += doc.getNmTipoDocumento() + ": " + doc.getNmFileOrig() + "\n";
            PigSismaDocumenti pigSismaDocumenti = sismaHelper.getEntityManager().find(PigSismaDocumenti.class,
                    doc.getIdSismaDocumenti().longValueExact());
            List<PigSismaDocEntry> lEntry = pigSismaDocumenti.getPigSismaDocEntrys();
            for (PigSismaDocEntry pigSismaDocEntry : lEntry) {
                str += "\t- " + pigSismaDocEntry.getNmEntry() + "\n";
            }
        }
        return str;
    }

    public SismaDto getSismaById(BigDecimal id) {
        SismaDto dto = new SismaDto();
        PigSisma su = sismaHelper.findById(PigSisma.class, id);
        PigSismaProgettiAg pigSismaProgettiAg = su.getPigSismaProgettiAg();
        PigSismaFinanziamento pigSismaFinanziamento = pigSismaProgettiAg.getPigSismaFinanziamento();
        PigSismaFaseProgetto pigSismaFaseProgetto = su.getPigSismaFaseProgetto();
        PigSismaValAtto pigSismaValAtto = su.getPigSismaValAtto();
        PigSismaStatoProgetto pigSismaStatoProgetto = su.getPigSismaStatoProgetto();
        dto.setIdSismaValAtto(new BigDecimal(pigSismaValAtto.getIdSismaValAtto()));
        dto.setNmTipoAtto(pigSismaValAtto.getNmTipoAtto());
        dto.setTiTipoAtto(pigSismaValAtto.getTiTipoAtto()); // DA PIANO STATO
        dto.setCodiceIntervento(pigSismaProgettiAg.getCodiceIntervento());
        dto.setDenominazioneIntervento(pigSismaProgettiAg.getDenominazioneIntervento());
        dto.setIdSisma(su.getIdSisma());
        dto.setAnno(su.getAnno());
        dto.setData(su.getData());
        dto.setDtCreazione(su.getDtCreazione());
        dto.setDtStato(su.getDtStato());
        dto.setCdKey(su.getCdKey());
        dto.setNumero(su.getNumero());
        dto.setOggetto(su.getOggetto());
        dto.setDsDescrizione(su.getDsDescrizione());
        dto.setTiFaseSisma(pigSismaFaseProgetto.getTiFaseSisma());
        dto.setDsFaseSisma(pigSismaFaseProgetto.getDsFaseSisma());
        dto.setTiStato(su.getTiStato().name());
        dto.setDtStato(su.getDtStato());
        dto.setCdTipoFinanziamento(pigSismaFinanziamento.getCdTipoFinanziamento());
        dto.setDsTipoFinanziamento(pigSismaFinanziamento.getDsTipoFinanziamento());
        dto.setCdErr(su.getCdErr());
        dto.setDsErr(su.getDsErr());
        dto.setFlInterventoSoggettoATutela(su.getFlInterventoSoggettoATutela().equals(Constants.DB_TRUE));
        PigVers vers = su.getPigVer();
        dto.setIdVers(new BigDecimal(vers.getIdVers()));
        dto.setIdTipoFinanziamento(new BigDecimal(pigSismaFinanziamento.getIdSismaFinanziamento()));
        dto.setIdSismaProgettiAg(new BigDecimal(pigSismaProgettiAg.getIdSismaProgettiAg()));
        dto.setIdSismaFaseProgetto(new BigDecimal(pigSismaFaseProgetto.getIdSismaFaseProgetto()));
        dto.setIdSismaStatoProgetto(new BigDecimal(pigSismaStatoProgetto.getIdSismaStatoProgetto()));
        dto.setDsStatoProgetto(pigSismaStatoProgetto.getDsStatoProgetto());
        dto.setIdSismaValAtto(new BigDecimal(pigSismaValAtto.getIdSismaValAtto()));
        dto.setDataAg(su.getDataAg());
        dto.setRegistroAg(su.getRegistroAg());
        dto.setAnnoAg(su.getAnnoAg());
        dto.setNumeroAg(su.getNumeroAg());
        dto.setClassifica(su.getClassifica());
        dto.setIdFascicolo(su.getIdFascicolo());
        dto.setIdSottofascicolo(su.getIdSottofascicolo());
        dto.setOggettoFascicolo(su.getOggettoFascicolo());
        dto.setOggettoSottofascicolo(su.getOggettoSottofascicolo());
        dto.setClassificaAg(su.getClassificaAg());
        dto.setIdFascicoloAg(su.getIdFascicoloAg());
        dto.setIdSottofascicoloAg(su.getIdSottofascicoloAg());
        dto.setOggettoFascicoloAg(su.getOggettoFascicoloAg());
        dto.setOggettoSottofascicoloAg(su.getOggettoSottofascicoloAg());
        return dto;
    }

    public DatiAnagraficiDto getDatiVersatoreByIdVers(BigDecimal id) {
        return sismaHelper.getDatiAnagraficiByIdVers(id, null);
    }

    public DatiAnagraficiDto getDatiVersatoreByIdVers(BigDecimal id, BigDecimal idSisma) {
        PigSisma pigSisma = null;
        if (idSisma != null) {
            pigSisma = sismaHelper.getEntityManager().find(PigSisma.class, idSisma.longValueExact());
        }
        return sismaHelper.getDatiAnagraficiByIdVers(id, pigSisma);
    }

    public DatiAnagraficiDto getDatiVersatoreByIdVers(BigDecimal id, PigSisma pigSisma) {
        return sismaHelper.getDatiAnagraficiByIdVers(id, pigSisma);
    }

    /* Se sisma è da versare in agenzia torna VERO altrimento FALSO */
    public boolean isSismaDaVersareInAgenzia(BigDecimal idSisma) {
        PigSisma pigSisma = sismaHelper.getEntityManager().find(PigSisma.class, idSisma.longValueExact());
        return pigSisma.getFlInviatoAEnte().equals(Constants.DB_TRUE);
    }

    public SismaDto inserisciSisma(SismaDto dto) {
        PigVers pigVers = sismaHelper.findById(PigVers.class, dto.getIdVers());
        PigSisma pigSisma = new PigSisma();
        PigSismaProgettiAg pigSismaProgettiAg = sismaHelper.getEntityManager().find(PigSismaProgettiAg.class,
                dto.getIdSismaProgettiAg().longValueExact());
        PigSismaFaseProgetto pigSismaFaseProgetto = sismaHelper.getEntityManager().find(PigSismaFaseProgetto.class,
                dto.getIdSismaFaseProgetto().longValueExact());
        PigSismaStatoProgetto pigSismaStatoProgetto = sismaHelper.getEntityManager().find(PigSismaStatoProgetto.class,
                dto.getIdSismaStatoProgetto().longValueExact());
        PigSismaValAtto pigSismaValAtto = sismaHelper.getPigSismaValAttoById(dto.getIdSismaValAtto());
        pigSisma.setCdKey(calcolaIdentificativo(dto.getAnno(), pigSismaProgettiAg.getCodiceIntervento(),
                pigSismaValAtto.getTiTipoAtto(), dto.getNumero()));
        PigSisma esisteAltroSisma = sismaHelper.getSismaByVersAndCdKey(pigVers, pigSisma.getCdKey());
        if (esisteAltroSisma != null) {
            dto.addWarnMessage(StringUtils.replace(messaggiHelper.retrievePigErrore("PING-ERRSISMA20").getDsErrore(),
                    "{0}", esisteAltroSisma.getCdKey()));
        } else {
            PigAmbienteVers pigAmbienteVers = pigVers.getPigAmbienteVer();
            pigSisma.setPigVer(pigVers);
            pigSisma.setPigSismaProgettiAg(pigSismaProgettiAg);
            pigSisma.setPigSismaFaseProgetto(pigSismaFaseProgetto);
            pigSisma.setPigSismaStatoProgetto(pigSismaStatoProgetto);
            pigSisma.setPigSismaValAtto(pigSismaValAtto);
            pigSisma.setTiStato(PigSisma.TiStato.BOZZA);
            pigSisma.setDsDescrizione(dto.getDsDescrizione());
            dto.setDtCreazione(new Date());
            dto.setDsFaseSisma(pigSismaFaseProgetto.getDsFaseSisma());
            dto.setTiFaseSisma(pigSismaFaseProgetto.getTiFaseSisma());
            pigSisma.setDtCreazione(dto.getDtCreazione());
            pigSisma.setData(dto.getData());
            pigSisma.setIamUser(modificaUtenteHelper.getIamUser(dto.getIdUserIam()));
            pigSisma.setAnno(dto.getAnno());
            pigSisma.setNumero(dto.getNumero());
            pigSisma.setCdKeyOs(
                    calcolaCdKeyOS(pigAmbienteVers.getNmAmbienteVers(), pigVers.getNmVers(), pigSisma.getCdKey()));
            pigSisma.setOggetto(calcolaOggetto(pigSisma));
            dto.setDtStato(new Date());
            pigSisma.setFlInterventoSoggettoATutela(
                    dto.isFlInterventoSoggettoATutela() ? Constants.DB_TRUE : Constants.DB_FALSE);
            pigSisma.setDtStato(dto.getDtStato());
            pigSisma.setClassifica(dto.getClassifica());
            pigSisma.setIdFascicolo(dto.getIdFascicolo());
            pigSisma.setIdSottofascicolo(dto.getIdSottofascicolo());
            pigSisma.setOggettoFascicolo(dto.getOggettoFascicolo());
            pigSisma.setOggettoSottofascicolo(dto.getOggettoSottofascicolo());
            pigSisma.setFlInviatoAEnte(dto.isFlInviatoAEnte() ? Constants.DB_TRUE : Constants.DB_FALSE);
            sismaHelper.insertEntity(pigSisma, true);
            dto.setIdSisma(pigSisma.getIdSisma());
            dto.setCdKey(pigSisma.getCdKey());
            dto.setOggetto(pigSisma.getOggetto());
            dto.setDsDescrizione(pigSisma.getDsDescrizione());
            dto.setIdSismaValAtto(new BigDecimal(pigSismaValAtto.getIdSismaValAtto()));
            dto.setTiTipoAtto(pigSismaValAtto.getTiTipoAtto());
            dto.setNmTipoAtto(pigSismaValAtto.getNmTipoAtto());
            dto.setTiStato(pigSisma.getTiStato().name());
            dto.setDsStatoProgetto(pigSismaStatoProgetto.getDsStatoProgetto());
        }
        return dto;
    }

    /* Funzione centralizzata per il calcolo dell'identificativo */
    public String calcolaIdentificativo(BigDecimal anno, String codiceIntervento, String tiTipoAtto, String numero) {
        return String.format("%d_%s_%s_%s", anno.longValueExact(), codiceIntervento, tiTipoAtto,
                Utils.normalizzaNomeFile(numero));
    }

    /* Funzione centralizzata per il calcolo del cdKEy OS */
    public String calcolaCdKeyOS(String nmAmbiente, String nmVersatore, String cdKey) {
        return String.format("%s/%s/%s", nmAmbiente, nmVersatore, cdKey);
    }

    public SismaDto modificaSisma(SismaDto dto) throws ObjectStorageException {
        PigSisma pigSisma = sismaHelper.findByIdWithLock(PigSisma.class, dto.getIdSisma());
        PigSismaValAtto pigSismaValAtto = pigSisma.getPigSismaValAtto();
        PigSismaFaseProgetto pigSismaFaseProgetto = pigSisma.getPigSismaFaseProgetto();
        PigSismaProgettiAg pigSismaProgettiAg = pigSisma.getPigSismaProgettiAg();
        PigSismaStatoProgetto pigSismaStatoProgetto = pigSisma.getPigSismaStatoProgetto();

        PigSismaValAtto pigSismaValAttoUpdated = sismaHelper.getPigSismaValAttoById(dto.getIdSismaValAtto());
        PigSismaProgettiAg pigSismaProgettiAgUpdated = sismaHelper.findById(PigSismaProgettiAg.class,
                dto.getIdSismaProgettiAg().longValueExact());

        String newCdKey = calcolaIdentificativo(dto.getAnno(), pigSismaProgettiAgUpdated.getCodiceIntervento(),
                pigSismaValAttoUpdated.getTiTipoAtto(), dto.getNumero());
        String oldCdKey = pigSisma.getCdKey();
        PigVers pigVers = pigSisma.getPigVer();

        // MEV 29667 - Se cambia l'identificativo controlliamo solo che non sia duplicato.
        if (!oldCdKey.equals(newCdKey) && sismaHelper.getSismaByVersAndCdKey(pigVers, newCdKey) != null) {
            dto.addWarnMessage(messaggiHelper.retrievePigErrore("PING-ERRSISMA20").getDsErrore());
        }

        if (!dto.existsMessages()) {
            /*
             * MEV 29667 - Se cambia il finanziamento oppure l'intervento oppure la fase vengono cancellati tutti i
             * documenti caricati in precedenza
             */
            if (pigSismaProgettiAg.getPigSismaFinanziamento().getIdSismaFinanziamento() != dto.getIdTipoFinanziamento()
                    .longValueExact()
                    || pigSismaProgettiAg.getIdSismaProgettiAg() != dto.getIdSismaProgettiAg().longValueExact()
                    || pigSisma.getPigSismaFaseProgetto().getIdSismaFaseProgetto() != dto.getIdSismaFaseProgetto()
                            .longValueExact()) {

                ObjectStorageBackend config = salvataggioBackendHelper.getObjectStorageConfiguration("SISMA",
                        configurationHelper.getValoreParamApplicByApplic(Constants.BUCKET_VERIFICA_SISMA));
                // Inizia a rimuovere i doc da SO flaggandoli come cancellati e togliendoli anche da S3
                List<PigSismaDocumenti> l = pigSisma.getPigSismaDocumentis();
                for (PigSismaDocumenti pigSismaDocumenti : l) {
                    if (salvataggioBackendHelper.isActive()) {
                        salvataggioBackendHelper.deleteObject(config, pigSismaDocumenti.getNmFileOs());
                        PigSismaDocumenti pigSismaDocumentiLock = sismaHelper.findByIdWithLock(PigSismaDocumenti.class,
                                pigSismaDocumenti.getIdSismaDocumenti());
                        pigSismaDocumentiLock.setFlDeleted(Constants.DB_TRUE);
                    }
                }
            }

            if (pigSismaProgettiAg.getIdSismaProgettiAg() != dto.getIdSismaProgettiAg().longValueExact()) {
                dto.setDenominazioneIntervento(pigSismaProgettiAgUpdated.getDenominazioneIntervento());
                dto.setCodiceIntervento(pigSismaProgettiAgUpdated.getCodiceIntervento());
                pigSisma.setPigSismaProgettiAg(pigSismaProgettiAgUpdated);
            } else {
                dto.setDenominazioneIntervento(pigSismaProgettiAg.getDenominazioneIntervento());
                dto.setCodiceIntervento(pigSismaProgettiAg.getCodiceIntervento());
            }
            if (pigSisma.getPigSismaFaseProgetto().getIdSismaFaseProgetto() != dto.getIdSismaFaseProgetto()
                    .longValueExact()) {
                PigSismaFaseProgetto fase = sismaHelper.findById(PigSismaFaseProgetto.class,
                        dto.getIdSismaFaseProgetto().longValueExact());
                dto.setDsFaseSisma(fase.getDsFaseSisma());
                dto.setTiFaseSisma(fase.getTiFaseSisma());
                pigSisma.setPigSismaFaseProgetto(fase);
            } else {
                dto.setDsFaseSisma(pigSismaFaseProgetto.getDsFaseSisma());
                dto.setTiFaseSisma(pigSismaFaseProgetto.getTiFaseSisma());
            }
            if (pigSismaValAtto.getIdSismaValAtto() != dto.getIdSismaValAtto().longValueExact()) {
                dto.setTiTipoAtto(pigSismaValAttoUpdated.getTiTipoAtto());
                pigSisma.setPigSismaValAtto(pigSismaValAttoUpdated);
                dto.setNmTipoAtto(pigSismaValAttoUpdated.getNmTipoAtto());
            } else {
                dto.setNmTipoAtto(pigSismaValAtto.getNmTipoAtto());
                dto.setTiTipoAtto(pigSismaValAtto.getTiTipoAtto());
            }
            if (pigSismaStatoProgetto.getIdSismaStatoProgetto() != dto.getIdSismaStatoProgetto().longValueExact()) {
                PigSismaStatoProgetto stato = sismaHelper.findById(PigSismaStatoProgetto.class,
                        dto.getIdSismaStatoProgetto().longValueExact());
                dto.setDsStatoProgetto(stato.getDsStatoProgetto());
                pigSisma.setPigSismaStatoProgetto(stato);
            } else {
                dto.setDsStatoProgetto(pigSismaStatoProgetto.getDsStatoProgetto());
            }
            pigSisma.setData(dto.getData());
            pigSisma.setIamUser(modificaUtenteHelper.getIamUser(dto.getIdUserIam()));
            pigSisma.setAnno(dto.getAnno());
            pigSisma.setNumero(dto.getNumero());
            pigSisma.setCdKey(newCdKey);
            pigSisma.setDsDescrizione(dto.getDsDescrizione());
            pigSisma.setFlInviatoAEnte(dto.isFlInviatoAEnte() ? Constants.DB_TRUE : Constants.DB_FALSE);
            dto.setDtStato(new Date());
            dto.setDtCreazione(pigSisma.getDtCreazione());
            dto.setCdKey(pigSisma.getCdKey());
            dto.setTiStato(pigSisma.getTiStato().name());
            pigSisma.setFlInterventoSoggettoATutela(
                    dto.isFlInterventoSoggettoATutela() ? Constants.DB_TRUE : Constants.DB_FALSE);
            pigSisma.setOggetto(calcolaOggetto(pigSisma));
            pigSisma.setDtStato(dto.getDtStato());
            pigSisma.setClassifica(dto.getClassifica());
            pigSisma.setIdFascicolo(dto.getIdFascicolo());
            pigSisma.setIdSottofascicolo(dto.getIdSottofascicolo());
            pigSisma.setOggettoFascicolo(dto.getOggettoFascicolo());
            pigSisma.setOggettoSottofascicolo(dto.getOggettoSottofascicolo());
            dto.setOggetto(pigSisma.getOggetto());
        }
        return dto;
    }

    /* Funzone centralizzata di calcolo dell'oggetto */
    private String calcolaOggetto(PigSisma pigSisma) {
        PigSismaProgettiAg pigSismaProgettiAg = pigSisma.getPigSismaProgettiAg();
        return String.format("%s - %s - %s", pigSismaProgettiAg.getCodiceIntervento(),
                pigSisma.getPigSismaFaseProgetto().getDsFaseSisma(), pigSismaProgettiAg.getDenominazioneIntervento());
    }

    public void cancellaSisma(BigDecimal idSisma) throws ObjectStorageException {
        //
        ObjectStorageBackend config = salvataggioBackendHelper.getObjectStorageConfiguration("SISMA",
                configurationHelper.getValoreParamApplicByApplic(Constants.BUCKET_VERIFICA_SISMA));
        PigSisma su = sismaHelper.findById(PigSisma.class, idSisma);
        // Rimuove i doc da SO
        List<PigSismaDocumenti> l = su.getPigSismaDocumentis();
        for (PigSismaDocumenti pigSismaDocumenti : l) {
            if (salvataggioBackendHelper.isActive()) {
                salvataggioBackendHelper.deleteObject(config, pigSismaDocumenti.getNmFileOs());
            }
        }
        sismaHelper.removeEntity(su, true);
    }

    public EsitoSalvataggioSisma versaSisma(BigDecimal idSisma, long idUserIamCorrente, BigDecimal idVers) {
        EsitoSalvataggioSisma retOk = new EsitoSalvataggioSisma();
        // Locca Sisma e tutti i suoi DOC!!
        PigSisma pigSisma = sismaHelper.findByIdWithLock(PigSisma.class, idSisma);
        retOk.setStato(pigSisma.getTiStato());
        List<PigSismaDocumenti> docs = pigSisma.getPigSismaDocumentis();
        for (PigSismaDocumenti doc : docs) {
            sismaHelper.findByIdWithLock(PigSismaDocumenti.class, doc.getIdSismaDocumenti());
        }
        PigVSismaChecks pigVSismaChecks = sismaHelper.getDatiNavigazionePerSisma(idSisma);
        if (pigVSismaChecks.getFlFileMancante().equals(Constants.DB_FALSE)
                && pigVSismaChecks.getFlVerificaErrata().equals(Constants.DB_FALSE)
                && pigVSismaChecks.getFlVerificaInCorso().equals(Constants.DB_FALSE)) {
            pigSisma.setTiStato(PigSisma.TiStato.RICHIESTA_INVIO);
            retOk.setStato(pigSisma.getTiStato());
            retOk.setOk(true);
            if (pigSisma.getIamUser().getIdUserIam() != idUserIamCorrente) {
                pigSisma.setIamUser(sismaHelper.findById(IamUser.class, idUserIamCorrente));
            }
        }
        PigVers pigVers = sismaHelper.getEntityManager().find(PigVers.class, idVers.longValueExact());
        pigSisma.setPigVerAg(pigVers); // Setta l'agenzia che effettua il versamento
        pigSisma.setDtStato(new Date());
        return retOk;
    }

    public void riportaInBozza(BigDecimal idSisma) {
        // Locca Sisma e tutti i suoi DOC!!
        PigSisma pigSisma = sismaHelper.findByIdWithLock(PigSisma.class, idSisma);
        pigSisma.setTiStato(PigSisma.TiStato.BOZZA); // RIMETTE IN BOZZA
        pigSisma.setDtStato(new Date());

        // MEV28570 - pulisco lo stato della verifica agenzia sui documenti
        List<PigSismaDocumenti> pigSismaDocumentis = pigSisma.getPigSismaDocumentis();
        for (PigSismaDocumenti documento : pigSismaDocumentis) {
            documento.setTiVerificaAgenzia("");
        }
    }

    public String cancellaDoc(BigDecimal idSisma, String nmFileOrig) throws ObjectStorageException {
        String str = null;
        PigSisma pigSisma = sismaHelper.findById(PigSisma.class, idSisma);
        if (pigSisma.getTiStato().equals(PigSisma.TiStato.BOZZA)
                || pigSisma.getTiStato().equals(PigSisma.TiStato.ERRORE)
                || pigSisma.getTiStato().equals(PigSisma.TiStato.DA_RIVEDERE)) {
            if (pigSisma.getTiStato().equals(PigSisma.TiStato.ERRORE)) {
                pigSisma.setTiStato(PigSisma.TiStato.BOZZA); // RIMETTE IN BOZZA
            } // Altrimenti lascia lo stato che aveva trovato (BOZZA, DA_RIVEDERE ecc.
            PigSismaDocumenti pigSismaDocumenti = sismaHelper.getPigSismaDocumentiBySismaNmFileOrig(pigSisma,
                    nmFileOrig);
            pigSismaDocumenti = sismaHelper.findByIdWithLock(PigSismaDocumenti.class,
                    pigSismaDocumenti.getIdSismaDocumenti());
            pigSismaDocumenti.setFlDeleted(Constants.DB_TRUE);
            //
            ObjectStorageBackend config = salvataggioBackendHelper.getObjectStorageConfiguration("SISMA",
                    configurationHelper.getValoreParamApplicByApplic(Constants.BUCKET_VERIFICA_SISMA));
            //
            if (salvataggioBackendHelper.isActive()) {
                salvataggioBackendHelper.deleteObject(config, pigSismaDocumenti.getNmFileOs());
            }
        } else {
            str = "Non è possibile eliminare un file di uno sisma già inviato";
        }
        return str;
    }

    public List<DocUploadDto> findPigVSismaLisDocsPianoByTipoSismaFase(PigSisma pigSisma) {
        List<PigSismaDocumenti> listaDoc = pigSisma.getPigSismaDocumentis();
        ArrayList<DocUploadDto> al = new ArrayList<>();
        List<PigSismaPianoDocReq> l = sismaHelper.findPigSismaPianoDocReq(pigSisma.getPigSismaFaseProgetto());
        for (PigSismaPianoDocReq pigSismaPianoDocReq : l) {
            DocUploadDto dto = new DocUploadDto();
            PigSismaValDoc pigSismaValDoc = pigSismaPianoDocReq.getPigSismaValDoc();
            dto.setNmTipoDocumento(pigSismaValDoc.getNmTipoDocumento());
            dto.setObbligatorio(pigSismaPianoDocReq.getFlDocObbligatorio().equals(Constants.DB_TRUE));
            dto.setPrincipale(pigSismaValDoc.getFlDocPrincipale().equals(Constants.DB_TRUE));
            // determina se esiste un doc già inserito
            for (PigSismaDocumenti pigSismaDocumenti : listaDoc) {
                if (pigSismaDocumenti.getPigSismaValDoc().getNmTipoDocumento().equals(dto.getNmTipoDocumento())
                        && pigSismaDocumenti.getFlDeleted().equals(Constants.DB_FALSE)) {
                    dto.setNmFileOrig(pigSismaDocumenti.getNmFileOrig());
                    dto.setCdErr(pigSismaDocumenti.getCdErr());
                    dto.setDsErr(pigSismaDocumenti.getDsErr());
                    dto.setDimensione(pigSismaDocumenti.getDimensione());
                    dto.setDimensioneStringa(Utils.convertBytesToFormattedString(dto.getDimensione()));
                    dto.setDataDoc(DateUtil.formatDateWithSlashAndTime(pigSismaDocumenti.getDtCaricamento()));
                    dto.setTiVerificaAgenzia(pigSismaDocumenti.getTiVerificaAgenzia());
                    if (pigSismaDocumenti.getFlEsitoVerifica() != null) {
                        dto.setFlEsitoVerifica((pigSismaDocumenti.getFlEsitoVerifica().equals(Constants.DB_TRUE)));
                    }
                    if (pigSismaDocumenti.getFlDocRicaricato().equals(Constants.DB_TRUE)) {
                        dto.setFlDocRicaricato(true);
                    }
                    break;
                }
            }
            al.add(dto);
        }
        return al;

    }

    public List<DocUploadDto> findPigVSismaLisDocsPianoByTipoSismaFase(BigDecimal idSisma) {
        PigSisma pigSisma = sismaHelper.findById(PigSisma.class, idSisma);
        return findPigVSismaLisDocsPianoByTipoSismaFase(pigSisma);
    }

    /* Determina il nome del file secondo Object Storage */
    public String getFileOsNameBySisma(BigDecimal idSisma, String nomeFileOriginale) {
        PigSisma pigSisma = sismaHelper.findById(PigSisma.class, idSisma);
        return pigSisma.getCdKeyOs() + "_" + Utils.eliminaPunteggiatureSpaziNomeFile(nomeFileOriginale);
    }

    public NavigazioneSismaDto getDatiNavigazionePerSisma(BigDecimal idSu) {
        NavigazioneSismaDto dto = new NavigazioneSismaDto();
        PigVSismaChecks pigVSismaChecks = sismaHelper.getDatiNavigazionePerSisma(idSu);
        if (pigVSismaChecks != null) {
            dto.setFileMancante((pigVSismaChecks.getFlFileMancante() != null
                    && pigVSismaChecks.getFlFileMancante().equals(Constants.DB_TRUE)));
            dto.setVerificaErrata((pigVSismaChecks.getFlVerificaErrata() != null
                    && pigVSismaChecks.getFlVerificaErrata().equals(Constants.DB_TRUE)));
            dto.setVerificaInCorso((pigVSismaChecks.getFlVerificaInCorso() != null
                    && pigVSismaChecks.getFlVerificaInCorso().equals(Constants.DB_TRUE)));
        }
        return dto;
    }

    /*
     * Torna True se ci sono documenti flaggati dall'agenzia come da ricaricare e che non sono ancora stati ricaricati
     */
    public boolean existsDocDaRicaricare(BigDecimal idSu) {
        List<PigSismaDocumenti> l = sismaHelper.findPigSismaDocumentiDaRicaricareByidSisma(idSu);
        return !l.isEmpty();
    }

    public JSONObject retrievePigErroreLikeAsJsonString(String codLike) {
        JSONObject jsonObject = new JSONObject();
        try {
            List<PigErrore> l = messaggiHelper.retrievePigErroreLike(codLike);
            for (PigErrore pigErrore : l) {
                jsonObject.put(pigErrore.getCdErrore(), pigErrore.getDsErrore());
            }
        } catch (JSONException ex) {
            //
        }
        return jsonObject;
    }

    /* DTOs */
    public DocSismaDto salvaTipoDocumento(DocSismaDto dto) {
        PigSismaDocumenti pigSismaDocumenti = new PigSismaDocumenti();
        PigSisma pigSisma = sismaHelper.findById(PigSisma.class, dto.getIdSisma());
        if (pigSisma.getTiStato().equals(PigSisma.TiStato.ERRORE)) {
            pigSisma.setTiStato(PigSisma.TiStato.DA_VERIFICARE);
        }
        PigSismaValDoc pigSismaValDoc = sismaHelper.getPigSismaValDocByNomeTipoDoc(dto.getNmTipoDocumento());
        pigSismaDocumenti.setPigSisma(pigSisma);
        pigSismaDocumenti.setPigSismaValDoc(pigSismaValDoc);
        pigSismaDocumenti.setNmFileOrig(dto.getNmFileOrig());
        pigSismaDocumenti.setNmFileOs(dto.getNmFileOs());
        pigSismaDocumenti.setDimensione(dto.getDimensione());
        pigSismaDocumenti.setDtCaricamento(new Date());
        pigSismaDocumenti.setFlDeleted(Constants.DB_FALSE);
        pigSismaDocumenti.setFlEsitoVerifica(Constants.DB_FALSE);
        pigSismaDocumenti.setTiVerificaAgenzia(dto.getTiVerificaAgenzia());
        if (dto.isFlDocRicaricato()) {
            pigSismaDocumenti.setFlDocRicaricato(Constants.DB_TRUE);
        } else {
            pigSismaDocumenti.setFlDocRicaricato(Constants.DB_FALSE);
        }
        sismaHelper.insertEntity(pigSismaDocumenti, true);
        dto.setIdSismaDocumenti(new BigDecimal(pigSismaDocumenti.getIdSismaDocumenti()));
        return dto;
    }

    // Salvataggio dell'agenzia quando in stato VERSATO
    public SismaDto salvaSismaAgenzia(SismaDto dto) {
        PigSisma pigSisma = sismaHelper.getEntityManager().find(PigSisma.class, dto.getIdSisma());
        pigSisma.setAnnoAg(dto.getAnnoAg());
        pigSisma.setNumeroAg(dto.getNumeroAg());
        pigSisma.setDataAg(dto.getDataAg());
        pigSisma.setRegistroAg(dto.getRegistroAg());
        pigSisma.setClassificaAg(dto.getClassificaAg());
        pigSisma.setIdFascicoloAg(dto.getIdFascicoloAg());
        pigSisma.setIdSottofascicoloAg(dto.getIdSottofascicoloAg());
        pigSisma.setOggettoFascicoloAg(dto.getOggettoFascicoloAg());
        pigSisma.setOggettoSottofascicoloAg(dto.getOggettoSottofascicoloAg());
        return dto;
    }

    // Salvataggio dell'agenzia quando in stato DA_VERIFICARE o VERIFICATO
    public PigSisma.TiStato salvaSismaAgenzia(BigDecimal idSisma, Map<BigDecimal, String> mappa, SismaDto sismaDto) {
        PigSisma pigSisma = sismaHelper.getEntityManager().find(PigSisma.class, idSisma.longValueExact());
        PigSisma.TiStato vecchioStato = pigSisma.getTiStato();
        PigSisma.TiStato nuovoStato = vecchioStato;
        int numFlagValorizzati = 0;
        int numFlagValorizzatiAOk = 0;
        for (Map.Entry<BigDecimal, String> entry : mappa.entrySet()) {
            BigDecimal key = entry.getKey();
            String value = entry.getValue();
            PigSismaDocumenti pigSismaDocumenti = sismaHelper.getEntityManager().find(PigSismaDocumenti.class,
                    key.longValueExact());
            String vecchioFlag = pigSismaDocumenti.getTiVerificaAgenzia() == null ? ""
                    : pigSismaDocumenti.getTiVerificaAgenzia();
            if (value.equals(Constants.DB_TRUE)) {
                numFlagValorizzatiAOk++;
                numFlagValorizzati++;
            } else if (value.equals(Constants.DB_FALSE)) {
                numFlagValorizzati++;
            }
            if (vecchioFlag.equals(value)) {
                // Il valore NON è cambiato rispetto a prima quindi non fa nulla.
            } else {
                // Azzera eventuali dati pregressi tipo flag ecc. e imposta il nuovo valore agenzia
                if (value.equals(Constants.DB_TRUE)) {
                    pigSismaDocumenti.setTiVerificaAgenzia(value);
                } else if (value.equals(Constants.DB_FALSE)) {
                    pigSismaDocumenti.setTiVerificaAgenzia(value);
                    // pigSismaDocumenti.setFlEsitoVerifica(Constants.DB_FALSE); // Annulla esito verifica del Job di
                    // verifica documenti
                } else {
                    pigSismaDocumenti.setTiVerificaAgenzia(null);
                    pigSismaDocumenti.setFlEsitoVerifica(Constants.DB_FALSE); // Annulla esito verifica del Job di
                    // verifica documenti
                }
                pigSismaDocumenti.setFlDocRicaricato(Constants.DB_FALSE);
            }
        }
        // se si trova nello stato DA_VERIFICARE vanno immessi i flag e verificato di passare eventualmente a VERIFICATO
        if (vecchioStato.equals(PigSisma.TiStato.DA_VERIFICARE)) {
            // Se Agenzia ha valorizzato tutti i flag
            if (mappa.size() == numFlagValorizzati) {
                // e se sono tutto OK allora passa allo stato VERIFICATO
                if (mappa.size() == numFlagValorizzatiAOk) {
                    nuovoStato = PigSisma.TiStato.VERIFICATO;
                } else {
                    nuovoStato = PigSisma.TiStato.DA_RIVEDERE;
                }
                pigSisma.setTiStato(nuovoStato);
            }
            // Se invece si era in VERIFICATO l'agenzia può ancora modificare i dati e far retrocedere lo stato a
            // DA_VERIFICARE
        } else if (vecchioStato.equals(PigSisma.TiStato.VERIFICATO)) {
            // se si sta salvando il dato agenzia e sisma è di un SA privato allora si salvano i dati
            if (sismaHelper.getTipoVersatore(pigSisma.getPigVer()).equals(Constants.TipoVersatore.SA_PRIVATO)) {
                pigSisma.setAnnoAg(sismaDto.getAnnoAg());
                pigSisma.setNumeroAg(sismaDto.getNumeroAg());
                pigSisma.setDataAg(sismaDto.getDataAg());
                pigSisma.setRegistroAg(sismaDto.getRegistroAg());
                pigSisma.setClassificaAg(sismaDto.getClassificaAg());
                pigSisma.setIdFascicoloAg(sismaDto.getIdFascicoloAg());
                pigSisma.setIdSottofascicoloAg(sismaDto.getIdSottofascicoloAg());
                pigSisma.setOggettoFascicoloAg(sismaDto.getOggettoFascicoloAg());
                pigSisma.setOggettoSottofascicoloAg(sismaDto.getOggettoSottofascicoloAg());
            }
            if (mappa.size() == numFlagValorizzati) {
                if (numFlagValorizzati != numFlagValorizzatiAOk) {
                    // Può aver messo a KO almeno un flag quindi retrocede a DA_RIVEDERE
                    nuovoStato = PigSisma.TiStato.DA_RIVEDERE;
                } else {
                    // Se sono tutti a OK passa oltre senza cambiare stato
                }
            } else {
                // Retrocede a DA_VERIFICARE perché agenzia potrebbe aver ANNULLATO almeno un flag
                nuovoStato = PigSisma.TiStato.DA_VERIFICARE;
            }
            pigSisma.setTiStato(nuovoStato);
        }
        return nuovoStato;
    }

    // Torna la decodemap con i vari stati per il recupero errori
    public DecodeMapIF getNuoviStatiPerRecuperoErroriDM(BigDecimal idSisma) {
        PigSisma pigSisma = sismaHelper.getEntityManager().find(PigSisma.class, idSisma.longValueExact());
        if (pigSisma.getFlInviatoAEnte().equals(Constants.DB_TRUE)) {
            // E' già stato inviato all'ente quindi si sta versando in agenzia
            return ComboGetter.getMappaOrdinalGenericEnum("ti_nuovo_stato", PigSisma.TiStato.RICHIESTA_INVIO,
                    PigSisma.TiStato.VERSATO);
        } else {
            // Non ancora inviato a ente oppure Agenzia per un SA Privato
            return ComboGetter.getMappaOrdinalGenericEnum("ti_nuovo_stato", PigSisma.TiStato.BOZZA,
                    PigSisma.TiStato.RICHIESTA_INVIO, PigSisma.TiStato.VERIFICATO);
        }
    }

    public Date recuperoErroreSisma(BigDecimal idSu, String nuovoStato) {
        PigSisma pigSisma = sismaHelper.findById(PigSisma.class, idSu);
        pigSisma.setTiStato(PigSisma.TiStato.valueOf(nuovoStato));
        pigSisma.setDtStato(new Date());

        if (nuovoStato.equals(PigSisma.TiStato.BOZZA.name())) {
            // MEV 27430 - pulisco lo stato della verifica agenzia sui documenti
            List<PigSismaDocumenti> pigSismaDocumentis = pigSisma.getPigSismaDocumentis();
            for (PigSismaDocumenti documento : pigSismaDocumentis) {
                documento.setTiVerificaAgenzia("");
            }
        }

        return pigSisma.getDtStato();
    }

    public String getOggettoSisma(BigDecimal idSisma) {
        return (sismaHelper.findById(PigSisma.class, idSisma)).getOggetto();
    }

    public boolean existsCondizioniInvio(BigDecimal idSisma) {
        PigVSismaChecks check = sismaHelper.findViewById(PigVSismaChecks.class, idSisma);
        // Controlli sui documenti di sisma attraverso i valori della vista
        return (check.getFlVerificaErrata().equals("1") || check.getFlVerificaInCorso().equals("1")
                || check.getFlFileMancante().equals("1"));
    }

    public boolean existsVerificaInCorso(BigDecimal idSisma) {
        PigVSismaChecks check = sismaHelper.findViewById(PigVSismaChecks.class, idSisma);
        // Controlli sui documenti di sisma attraverso i valori della vista
        return check.getFlVerificaInCorso().equals("1");
    }

    public void cambiaStatoSisma(BigDecimal idSisma, PigSisma.TiStato tiStato) {
        PigSisma pigSisma = sismaHelper.findViewById(PigSisma.class, idSisma.longValueExact());
        // Controlli sui documenti di sisma attraverso i valori della vista
        pigSisma.setTiStato(tiStato);
        pigSisma.setDtStato(new Date());
    }

    public boolean existsPigSismaDocumentiDaVerificare(BigDecimal idSisma) {
        return sismaHelper.existsPigSismaDocumentiDaVerificare(idSisma);
    }

    /*
     * Determina lo stato finale di "Inviato a Sacer" che può essere VERSATO se non è ancora stato inviato all'ente
     * oppure COMPLETATO se era giò stato inviato all'ente. Ovviamente se il progetto è di un SA Privato allora lo stato
     * finale può essere sempre e solo COMPLETATO in quanto non prevede l'invio precedente all'ente come per il SA
     * pubblico
     */
    public PigSisma aggiornaStatoInviatoASacer(PigSisma pigSisma) {
        if (pigSisma.getFlInviatoAEnte().equals(Constants.DB_FALSE)) {
            Enum<Constants.TipoVersatore> tipo = sismaHelper.getTipoVersatore(pigSisma.getPigVer());
            if (tipo.equals(Constants.TipoVersatore.SA_PUBBLICO)) {
                pigSisma.setTiStato(PigSisma.TiStato.VERSATO);
            } else {
                pigSisma.setTiStato(PigSisma.TiStato.COMPLETATO);
            }
            // In questo caso flegga come effettuato il primo invio all'ente!
            pigSisma.setFlInviatoAEnte(Constants.DB_TRUE);
        } else {
            pigSisma.setTiStato(PigSisma.TiStato.COMPLETATO);
        }
        pigSisma.setDtStato(new Date());
        return pigSisma;
    }

    /*
     * Determina se il versatore passato è di tipo AGENZIA, SA_PUBBLICO, SA_PRIVATO oppure nessuno dei tre (NULL)
     */
    public Enum<Constants.TipoVersatore> getTipoVersatore(BigDecimal id) {
        PigVers pigVers = sismaHelper.getPigVersById(id);
        return sismaHelper.getTipoVersatore(pigVers);
    }

    public boolean controllaUnivocitaDatiAgenzia(BigDecimal idSisma, long idVersAg, String registroAg,
            BigDecimal annoAg, String numeroAg) {
        if (registroAg == null || annoAg == null || numeroAg == null) {
            return true;
        }

        PigVers agenzia = sismaHelper.getEntityManager().find(PigVers.class, idVersAg);
        List<PigSisma> pigSismas = sismaHelper.findPigSismaByVersAndDatiAgenzia(idSisma, agenzia, registroAg, annoAg,
                numeroAg);

        return pigSismas.isEmpty();
    }

}
