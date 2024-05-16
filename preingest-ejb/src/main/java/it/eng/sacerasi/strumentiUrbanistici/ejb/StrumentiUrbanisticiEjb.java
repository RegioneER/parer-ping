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

package it.eng.sacerasi.strumentiUrbanistici.ejb;

import it.eng.paginator.helper.LazyListHelper;
import it.eng.parer.objectstorage.dto.ObjectStorageBackend;
import it.eng.parer.objectstorage.helper.SalvataggioBackendHelper;
import it.eng.sacerasi.common.Constants;
import it.eng.sacerasi.entity.IamUser;
import it.eng.sacerasi.entity.PigAmbienteVers;
import it.eng.sacerasi.entity.PigErrore;
import it.eng.sacerasi.entity.PigStrumUrbAtto;
import it.eng.sacerasi.entity.PigStrumUrbCollegamenti;
import it.eng.sacerasi.entity.PigStrumUrbDocumenti;
import it.eng.sacerasi.entity.PigStrumUrbPianoDocReq;
import it.eng.sacerasi.entity.PigStrumUrbPianoStato;
import it.eng.sacerasi.entity.PigStrumUrbValDoc;
import it.eng.sacerasi.entity.PigStrumentiUrbanistici;
import it.eng.sacerasi.entity.PigVers;
import it.eng.sacerasi.grantEntity.OrgAmbiente;
import it.eng.sacerasi.grantEntity.OrgEnte;
import it.eng.sacerasi.grantEntity.OrgStrut;
import it.eng.sacerasi.grantEntity.SIOrgEnteSiam;
import it.eng.sacerasi.messages.MessaggiHelper;
import it.eng.sacerasi.strumentiUrbanistici.dto.RicercaStrumentiUrbanisticiDTO;
import it.eng.sacerasi.util.DateUtil;
import it.eng.sacerasi.util.GenericDto;
import it.eng.sacerasi.viewEntity.PigVSuCheck;
import it.eng.sacerasi.viewEntity.PigVSuLisDocsPiano;
import it.eng.sacerasi.web.helper.ConfigurationHelper;
import it.eng.sacerasi.web.util.ComboGetter;
import static it.eng.sacerasi.web.util.ComboGetter.CAMPO_FLAG;
import static it.eng.sacerasi.web.util.ComboGetter.CAMPO_VALORE;
import it.eng.sacerasi.web.util.Utils;
import it.eng.sacerasi.ws.replicaUtente.ejb.ModificaUtenteEjb;
import it.eng.spagoLite.db.base.BaseTableInterface;
import it.eng.spagoLite.db.base.row.BaseRow;
import it.eng.spagoLite.db.base.table.BaseTable;
import it.eng.spagoLite.db.decodemap.DecodeMapIF;
import it.eng.spagoLite.db.oracle.decode.DecodeMap;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import it.eng.parer.objectstorage.exceptions.ObjectStorageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author MIacolucci
 */
@SuppressWarnings("rawtypes")
@Stateless
@LocalBean
public class StrumentiUrbanisticiEjb {

    private static final String BACKED_STRUMENTI_URBANISTICI = "STR_URBANISTICI";
    Logger log = LoggerFactory.getLogger(StrumentiUrbanisticiEjb.class);

    public static final String TIPO_UNIONE = "UNIONE";
    @EJB
    private StrumentiUrbanisticiHelper strumentiUrbanisticiHelper;
    @EJB
    private ModificaUtenteEjb modificaUtenteHelper;
    @EJB
    private MessaggiHelper messaggiHelper;
    @EJB
    private SalvataggioBackendHelper salvataggioBackendHelper;
    @EJB
    private ConfigurationHelper configurationHelper;
    @EJB
    private LazyListHelper lazyListHelper;

    public BaseTableInterface findTipiStrumentiUrbanisticiTB() {
        BaseTable tab = new BaseTable();
        List<String> valori = strumentiUrbanisticiHelper.findTipiStrumentiUrbanistici();
        for (String str : valori) {
            BaseRow riga = new BaseRow();
            riga.setString("nm_tipo_strumento_urbanistico", str);
            tab.add(riga);
        }
        return tab;
    }

    public BaseTable findSUByVersAndStatesTB(RicercaStrumentiUrbanisticiDTO rDTO, BigDecimal idVers) {
        EnumSet set = EnumSet.of(PigStrumentiUrbanistici.TiStato.BOZZA, PigStrumentiUrbanistici.TiStato.ERRORE,
                PigStrumentiUrbanistici.TiStato.INVIO_IN_CORSO, PigStrumentiUrbanistici.TiStato.IN_ELABORAZIONE,
                PigStrumentiUrbanistici.TiStato.RICHIESTA_INVIO, PigStrumentiUrbanistici.TiStato.VERSATO,
                PigStrumentiUrbanistici.TiStato.ANNULLATO, PigStrumentiUrbanistici.TiStato.IN_TRASFORMAZIONE,
                PigStrumentiUrbanistici.TiStato.IN_VERSAMENTO);

        return lazyListHelper.getTableBean(strumentiUrbanisticiHelper.findSUByVersAndStates(rDTO, idVers, set),
                this::suResultsToTable);
    }

    private BaseTable suResultsToTable(List<Object[]> sus) {
        BaseTable strumUrbTable = new BaseTable();

        if (sus != null && !sus.isEmpty()) {
            for (Object[] su : sus) {
                PigStrumentiUrbanistici pigStrumentiUrbanistici = (PigStrumentiUrbanistici) su[0];
                PigStrumUrbPianoStato pigStrumUrbPianoStato = (PigStrumUrbPianoStato) su[1];

                BaseRow riga = new BaseRow();
                riga.setBigDecimal("id_strumenti_urbanistici",
                        new BigDecimal(pigStrumentiUrbanistici.getIdStrumentiUrbanistici()));
                riga.setString("nm_tipo_strumento_urbanistico", pigStrumUrbPianoStato.getNmTipoStrumentoUrbanistico());
                riga.setString("ti_fase_strumento", pigStrumUrbPianoStato.getTiFaseStrumento());
                riga.setBigDecimal("anno", new BigDecimal(pigStrumentiUrbanistici.getAnno().longValueExact()));
                riga.setString("cd_key", pigStrumentiUrbanistici.getCdKey());
                riga.setString("ti_stato", pigStrumentiUrbanistici.getTiStato().name());
                riga.setString("ds_descrizione", pigStrumentiUrbanistici.getDsDescrizione());
                riga.setTimestamp("dt_creazione",
                        new java.sql.Timestamp(pigStrumentiUrbanistici.getDtCreazione().getTime()));
                riga.setBigDecimal("dimensione",
                        strumentiUrbanisticiHelper.getDimensioneDocumentiBySU(pigStrumentiUrbanistici));
                // Accende o meno il download del rapporto di versamento
                if (pigStrumentiUrbanistici.getTiStato().name()
                        .equals(PigStrumentiUrbanistici.TiStato.VERSATO.name())) {
                    riga.setString("download", "download");
                }
                strumUrbTable.add(riga);
            }
        }

        return strumUrbTable;
    }

    // Cosidera solo i documenti caricati NON CANCELLATI !
    public List<DocStrumDto> findDocumentiCaricatiPerIdSUDto(PigStrumentiUrbanistici pigStrumentiUrbanistici) {
        ArrayList<DocStrumDto> al = new ArrayList<>();
        PigStrumUrbPianoStato pigStrumUrbPianoStato = pigStrumentiUrbanistici.getPigStrumUrbPianoStato();
        List<PigStrumUrbDocumenti> listaDoc = pigStrumentiUrbanistici.getPigStrumUrbDocumentis();
        for (PigStrumUrbDocumenti pigStrumUrbDocumenti : listaDoc) {
            if (pigStrumUrbDocumenti.getFlDeleted().equals(Constants.DB_FALSE)) {
                DocStrumDto dto = new DocStrumDto();
                dto.setDimensione(pigStrumUrbDocumenti.getDimensione());
                dto.setNumFileCaricati(pigStrumUrbDocumenti.getNumFiles() == null ? 0
                        : pigStrumUrbDocumenti.getNumFiles().intValueExact());
                dto.setNmFileOrig(pigStrumUrbDocumenti.getNmFileOrig());
                PigStrumUrbValDoc pigStrumUrbValDoc = pigStrumUrbDocumenti.getPigStrumUrbValDoc();
                dto.setNmTipoDocumento(pigStrumUrbValDoc.getNmTipoDocumento());
                PigStrumUrbPianoDocReq pigStrumUrbPianoDocReq = strumentiUrbanisticiHelper
                        .getPigStrumUrbPianoDocReq(pigStrumUrbPianoStato, pigStrumUrbValDoc);
                dto.setObbligatorio(pigStrumUrbPianoDocReq.getFlDocObbligatorio().equals(Constants.DB_TRUE));
                dto.setCdErr(pigStrumUrbDocumenti.getCdErr());
                dto.setDsErr(pigStrumUrbDocumenti.getDsErr());
                dto.setFlEsitoVerifica(pigStrumUrbDocumenti.getFlEsitoVerifica().equals(Constants.DB_TRUE));
                dto.setDtCaricamento(pigStrumUrbDocumenti.getDtCaricamento());
                // MEV25704 - Aggiunto l'eventuale report di verifica file.
                dto.setBlReport(pigStrumUrbDocumenti.getBlReport());
                al.add(dto);
            }
        }
        return al;
    }

    /*
     * Torna un array du tre Object dove il primo è una BaseTableInterface, il secondo una stringa e il terzo un
     * ArrayList di stringhe con i messaggi per i doc obbligatori non caricati
     */
    public Object[] findDocumentiCaricatiPerIdSUTB(BigDecimal idStrum) {
        StringBuilder sb = new StringBuilder();
        BaseTable table = new BaseTable();
        ArrayList<String> alObbNonCaricati = new ArrayList<>();
        ArrayList<String> alDocInErrore = new ArrayList<>();

        PigStrumentiUrbanistici pigStrumentiUrbanistici = strumentiUrbanisticiHelper
                .findById(PigStrumentiUrbanistici.class, idStrum);
        PigStrumUrbPianoStato pigStrumUrbPianoStato = pigStrumentiUrbanistici.getPigStrumUrbPianoStato();
        List<DocUploadDto> listaUpload = findPigVSuLisDocsPianoByTipoStrumentoFase(pigStrumentiUrbanistici,
                pigStrumUrbPianoStato.getNmTipoStrumentoUrbanistico(), pigStrumUrbPianoStato.getTiFaseStrumento());
        List<DocStrumDto> lCaricati = findDocumentiCaricatiPerIdSUDto(pigStrumentiUrbanistici);
        /*
         * Se il doc è stato caricato lo mette nel TableBean altrimenti accoda alla stringa da visualizzare per gli
         * altri documenti facoltativi ancora da caricare
         */
        for (DocUploadDto docUploadDto : listaUpload) {
            BaseRow riga = null;
            boolean trovatoTraICaricati = false;
            DocStrumDto dto = null;
            // Cerca il doc tra i caricati...
            for (DocStrumDto docStrumDto : lCaricati) {
                if (docStrumDto.getNmTipoDocumento().equals(docUploadDto.getNmTipoDocumento())) {
                    trovatoTraICaricati = true;
                    dto = docStrumDto;
                    break;
                }
            } // Uscito dall'analisi della ricerca nei caricati...
            if (trovatoTraICaricati) { // Lo mette nella lista dei caricati obbligatori
                riga = new BaseRow();
                riga.setBigDecimal("id_strum_urb_documenti", dto.getIdStrumUrbDocumenti());
                riga.setString("nm_tipo_documento", dto.getNmTipoDocumento());
                riga.setString("nm_file_orig", dto.getNmFileOrig());
                riga.setString("fl_obbligatorio", (dto.isObbligatorio() ? Constants.DB_TRUE : Constants.DB_FALSE));
                riga.setBigDecimal("num_files", new BigDecimal(dto.getNumFileCaricati()));
                riga.setString("dimensione", Utils.convertBytesToFormattedString(dto.getDimensione()));
                riga.setString("cd_err", dto.getCdErr());
                riga.setString("ds_err", dto.getDsErr());
                riga.setString("fl_esito_verifica", (dto.isFlEsitoVerifica() ? Constants.DB_TRUE : Constants.DB_FALSE));
                riga.setTimestamp("dt_caricamento", new java.sql.Timestamp(dto.getDtCaricamento().getTime()));

                // MEV25704 - se è presente un report della verifica fatta sui file, permettine il download
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

    public boolean existsSUByVersAndCdKey(PigVers pigVer, String cdKey) {
        return strumentiUrbanisticiHelper.getSUByVersAndCdKey(pigVer, cdKey) != null;
    }

    public BaseTableInterface findPigStrumUrbPianoStatoByNomeTipoTB(String nomeTipo) {
        BaseTable tab = new BaseTable();
        List<PigStrumUrbPianoStato> valori = strumentiUrbanisticiHelper.findPigStrumUrbPianoStatoByNomeTipo(nomeTipo);
        for (PigStrumUrbPianoStato str : valori) {
            BaseRow riga = new BaseRow();
            riga.setString("ti_fase_Strumento", str.getTiFaseStrumento());
            tab.add(riga);
        }
        return tab;
    }

    public Long getIdPigStrumUrbPianoStatoByNomeTipoByTipoAndFase(String nmTipoStrumentoUrbanistico,
            String tiFaseStrumento) {
        Long id = null;
        PigStrumUrbPianoStato p = strumentiUrbanisticiHelper
                .getPigStrumUrbPianoStatoByNomeTipoByTipoAndFase(nmTipoStrumentoUrbanistico, tiFaseStrumento);
        if (p != null) {
            id = p.getIdStrumUrbPianoStato();
        }
        return id;
    }

    public String getXmlRichiestaRappVersByIdStrumUrb(BigDecimal id) {
        String xml = null;
        Object[] ogg = strumentiUrbanisticiHelper.findDatiAmbienteByIdSU(id);
        PigStrumentiUrbanistici pigStrumentiUrbanistici = (PigStrumentiUrbanistici) ogg[0];
        OrgStrut orgStrut = (OrgStrut) ogg[1];
        OrgEnte orgEnte = (OrgEnte) ogg[2];
        OrgAmbiente orgAmbiente = (OrgAmbiente) ogg[3];
        String versione = configurationHelper
                .getValoreParamApplicByApplic(Constants.NmParamApplic.VERSIONE_XML_RECUP_UD.name());
        String loginname = configurationHelper
                .getValoreParamApplicByApplic(Constants.NmParamApplic.USERID_RECUP_UD.name());

        xml = "<Recupero>\n" + "  <Versione>" + versione + "</Versione>\n" + "  <Versatore>\n" + "    <Ambiente>"
                + orgAmbiente.getNmAmbiente() + "</Ambiente>\n" + "    <Ente>" + orgEnte.getNmEnte() + "</Ente>\n"
                + "    <Struttura>" + orgStrut.getNmStrut() + "</Struttura>\n" + "    <UserID>" + loginname
                + "</UserID>    \n" + "  </Versatore>\n" + "  <Chiave>\n" + "    <Numero>"
                + strumentiUrbanisticiHelper.estraiAttoDaIdentificativo(pigStrumentiUrbanistici.getCdKey()) + "_"
                + pigStrumentiUrbanistici.getNumero() + "</Numero>\n" + "    <Anno>"
                + pigStrumentiUrbanistici.getAnno().longValueExact() + "</Anno>\n"
                + "    <TipoRegistro>STRUMENTI URBANISTICI</TipoRegistro>    \n" + "  </Chiave>\n" + "</Recupero> \n"
                + " ";
        return xml;
    }

    public SUDto getSUById(BigDecimal id) {
        PigStrumentiUrbanistici su = strumentiUrbanisticiHelper.findById(PigStrumentiUrbanistici.class, id);
        PigStrumUrbPianoStato ps = su.getPigStrumUrbPianoStato();
        SUDto dto = new SUDto();
        dto.setIdStrumentiUrbanistici(su.getIdStrumentiUrbanistici());
        dto.setAnno(su.getAnno());
        dto.setData(su.getData());
        dto.setDtCreazione(su.getDtCreazione());
        dto.setDtStato(su.getDtStato());
        dto.setCdKey(su.getCdKey());
        dto.setNmTipoStrumentoUrbanistico(ps.getNmTipoStrumentoUrbanistico());
        dto.setNumero(su.getNumero());
        dto.setOggetto(su.getOggetto());
        dto.setDsDescrizione(su.getDsDescrizione());
        dto.setTiFaseStrumento(ps.getTiFaseStrumento());
        dto.setTiStato(su.getTiStato().name());
        dto.setDtStato(su.getDtStato());
        StrumentiUrbanisticiHelper.DatiAnagraficiDto datiDto = getDatiVersatoreByIdVers(
                new BigDecimal(su.getPigVer().getIdVers()));
        // MEV26936
        PigStrumUrbAtto pigStrumUrbAtto = strumentiUrbanisticiHelper.findPigStrumUrbAtto(datiDto.getTipologia(),
                strumentiUrbanisticiHelper.estraiAttoDaIdentificativo(su.getCdKey()));
        dto.setTiAtto(pigStrumUrbAtto);

        dto.setCdErr(su.getCdErr());
        dto.setDsErr(su.getDsErr());
        List<PigStrumUrbCollegamenti> lista = su.getPigStrumUrbCollegamentis();
        int t = 0;
        for (PigStrumUrbCollegamenti pigStrumUrbCollegamenti : lista) {
            switch (t) {
            case 0:
                dto.setAnnoCollegato1(pigStrumUrbCollegamenti.getAnno());
                dto.setIdentificativoCollegato1(pigStrumUrbCollegamenti.getNumero());
                dto.setFaseCollegata1(pigStrumUrbCollegamenti.getPigStrumUrbPianoStato().getTiFaseStrumento());
                break;
            case 1:
                dto.setAnnoCollegato2(pigStrumUrbCollegamenti.getAnno());
                dto.setIdentificativoCollegato2(pigStrumUrbCollegamenti.getNumero());
                dto.setFaseCollegata2(pigStrumUrbCollegamenti.getPigStrumUrbPianoStato().getTiFaseStrumento());
            }
            t++;
        }
        return dto;
    }

    public StrumentiUrbanisticiHelper.DatiAnagraficiDto getDatiVersatoreByIdVers(BigDecimal id) {
        return strumentiUrbanisticiHelper.getDatiAnagraficiByIdVers(id);
    }

    public SUDto inserisciStrumentoUrbanistico(SUDto dto) {
        PigVers pigVers = strumentiUrbanisticiHelper.findById(PigVers.class, dto.getIdVers());

        PigStrumentiUrbanistici pigStrumentiUrbanistici = new PigStrumentiUrbanistici();

        PigStrumUrbPianoStato pigStrumUrbPianoStato = strumentiUrbanisticiHelper
                .getPigStrumUrbPianoStatoByNomeTipoByTipoAndFase(dto.getNmTipoStrumentoUrbanistico(),
                        dto.getTiFaseStrumento());

        // MEV 26936
        pigStrumentiUrbanistici.setCdKey(String.format("%d_%s_%s", dto.getAnno().longValueExact(),
                dto.getTiAtto().getCdNome(), Utils.normalizzaNomeFile(dto.getNumero())));

        PigStrumentiUrbanistici esisteAltroSu = strumentiUrbanisticiHelper.getSUByVersAndCdKey(pigVers,
                pigStrumentiUrbanistici.getCdKey());
        if (esisteAltroSu != null) {
            dto.addWarnMessage(StringUtils.replace(messaggiHelper.retrievePigErrore("PING-ERRSU20").getDsErrore(),
                    "{0}", esisteAltroSu.getCdKey()));
        } else {
            PigAmbienteVers pigAmbienteVers = pigVers.getPigAmbienteVer();
            pigStrumentiUrbanistici.setPigVer(pigVers);
            pigStrumentiUrbanistici.setPigStrumUrbPianoStato(pigStrumUrbPianoStato);
            pigStrumentiUrbanistici.setTiStato(PigStrumentiUrbanistici.TiStato.BOZZA);
            pigStrumentiUrbanistici.setDsDescrizione(dto.getDsDescrizione());
            dto.setDtCreazione(new Date());
            pigStrumentiUrbanistici.setDtCreazione(dto.getDtCreazione());
            pigStrumentiUrbanistici.setData(dto.getData());
            pigStrumentiUrbanistici.setIamUser(modificaUtenteHelper.getIamUser(dto.getIdUserIam()));
            pigStrumentiUrbanistici.setAnno(dto.getAnno());
            pigStrumentiUrbanistici.setNumero(dto.getNumero());
            pigStrumentiUrbanistici.setCdKeyOs(String.format("%s/%s/%s", pigAmbienteVers.getNmAmbienteVers(),
                    pigVers.getNmVers(), pigStrumentiUrbanistici.getCdKey()));
            pigStrumentiUrbanistici.setOggetto(String.format("Strumento urbanistico %s - %s di %s del %s",
                    dto.getNmTipoStrumentoUrbanistico(), pigStrumentiUrbanistici.getCdKey(), pigVers.getNmVers(),
                    DateUtil.formatDateWithSlash(pigStrumentiUrbanistici.getData())));

            pigStrumentiUrbanistici.setCdKeyOs(String.format("%s/%s/%s", pigAmbienteVers.getNmAmbienteVers(),
                    pigVers.getNmVers(), pigStrumentiUrbanistici.getCdKey()));
            // MEV #20062: Strumenti urbanistici: modifiche all'interfaccia di versamento
            pigStrumentiUrbanistici
                    .setOggetto(calcolaOggetto(pigVers, pigStrumentiUrbanistici, dto.getNmTipoStrumentoUrbanistico()));
            dto.setDtStato(new Date());
            pigStrumentiUrbanistici.setDtStato(dto.getDtStato());
            strumentiUrbanisticiHelper.insertEntity(pigStrumentiUrbanistici, true);
            dto.setIdStrumentiUrbanistici(pigStrumentiUrbanistici.getIdStrumentiUrbanistici());
            dto.setCdKey(pigStrumentiUrbanistici.getCdKey());
            dto.setOggetto(pigStrumentiUrbanistici.getOggetto());
            dto.setDsDescrizione(pigStrumentiUrbanistici.getDsDescrizione());
            dto.setTiStato(pigStrumentiUrbanistici.getTiStato().name());
            // Inserisce gli eventuali nuovi collegamenti
            creaCollegamenti(dto, pigStrumentiUrbanistici, pigStrumUrbPianoStato);
        }
        return dto;
    }

    public SUDto modificaStrumentoUrbanistico(SUDto dto) throws ObjectStorageException {
        PigStrumentiUrbanistici pigStrumentiUrbanistici = strumentiUrbanisticiHelper
                .findByIdWithLock(PigStrumentiUrbanistici.class, dto.getIdStrumentiUrbanistici());
        PigStrumUrbPianoStato pigStrumUrbPianoStato = strumentiUrbanisticiHelper
                .getPigStrumUrbPianoStatoByNomeTipoByTipoAndFase(dto.getNmTipoStrumentoUrbanistico(),
                        dto.getTiFaseStrumento());

        String newCdKey = String.format("%d_%s_%s", dto.getAnno().longValueExact(), dto.getTiAtto().getCdNome(),
                Utils.normalizzaNomeFile(dto.getNumero()));

        String oldCdKey = pigStrumentiUrbanistici.getCdKey();
        PigVers pigVers = pigStrumentiUrbanistici.getPigVer();
        if (!oldCdKey.equals(newCdKey) && strumentiUrbanisticiHelper.getSUByVersAndCdKey(pigVers, newCdKey) != null) {
            dto.addWarnMessage(messaggiHelper.retrievePigErrore("PING-ERRSU20").getDsErrore());
        }

        if (!dto.existsMessages()) {
            // MEV 27352 - cancella i documenti se tipo progetto o fase progetto sono stati modificati
            if (!pigStrumentiUrbanistici.getPigStrumUrbPianoStato().getNmTipoStrumentoUrbanistico()
                    .equals(dto.getNmTipoStrumentoUrbanistico())
                    || !pigStrumentiUrbanistici.getPigStrumUrbPianoStato().getTiFaseStrumento()
                            .equals(dto.getTiFaseStrumento())) {
                ObjectStorageBackend config = salvataggioBackendHelper
                        .getObjectStorageConfiguration(BACKED_STRUMENTI_URBANISTICI, configurationHelper
                                .getValoreParamApplicByApplic(Constants.BUCKET_VERIFICA_STRUMENTI_URBANISTICI));

                // Inizia a rimuovere i doc da SO fleggandoli come cancellati
                List<PigStrumUrbDocumenti> l = pigStrumentiUrbanistici.getPigStrumUrbDocumentis();
                for (PigStrumUrbDocumenti pigStrumUrbDocumenti : l) {
                    if (this.salvataggioBackendHelper.isActive()) {
                        this.salvataggioBackendHelper.deleteObject(config, pigStrumUrbDocumenti.getNmFileOs());
                        PigStrumUrbDocumenti pigStrumUrbDocumentiLock = strumentiUrbanisticiHelper.findByIdWithLock(
                                PigStrumUrbDocumenti.class, pigStrumUrbDocumenti.getIdStrumUrbDocumenti());
                        pigStrumUrbDocumentiLock.setFlDeleted(Constants.DB_TRUE);
                    }
                }
            }

            pigStrumentiUrbanistici.setPigStrumUrbPianoStato(pigStrumUrbPianoStato);
            if (pigStrumentiUrbanistici.getTiStato().equals(PigStrumentiUrbanistici.TiStato.ERRORE)) {
                pigStrumentiUrbanistici.setTiStato(PigStrumentiUrbanistici.TiStato.BOZZA);
            }
            pigStrumentiUrbanistici.setData(dto.getData());
            pigStrumentiUrbanistici.setIamUser(modificaUtenteHelper.getIamUser(dto.getIdUserIam()));
            pigStrumentiUrbanistici.setAnno(dto.getAnno());
            pigStrumentiUrbanistici.setNumero(dto.getNumero());
            pigStrumentiUrbanistici.setCdKey(newCdKey);
            pigStrumentiUrbanistici.setOggetto(String.format("Strumento urbanistico %s - %s di %s del %s",
                    dto.getNmTipoStrumentoUrbanistico(), pigStrumentiUrbanistici.getCdKey(), pigVers.getNmVers(),
                    DateUtil.formatDateWithSlash(pigStrumentiUrbanistici.getData())));
            pigStrumentiUrbanistici.setDsDescrizione(dto.getDsDescrizione());
            pigStrumentiUrbanistici
                    .setOggetto(calcolaOggetto(pigVers, pigStrumentiUrbanistici, dto.getNmTipoStrumentoUrbanistico()));
            dto.setDtStato(new Date());
            dto.setDtCreazione(pigStrumentiUrbanistici.getDtCreazione());
            pigStrumentiUrbanistici.setDtStato(dto.getDtStato());
            // Rimuove i vecchi collegamenti
            List<PigStrumUrbCollegamenti> cols = pigStrumentiUrbanistici.getPigStrumUrbCollegamentis();
            for (PigStrumUrbCollegamenti col : cols) {
                strumentiUrbanisticiHelper.removeEntity(col, true);
            }
            // Inserisce gli eventuali nuovi collegamenti
            creaCollegamenti(dto, pigStrumentiUrbanistici, pigStrumUrbPianoStato);

            dto.setCdKey(pigStrumentiUrbanistici.getCdKey());
            dto.setOggetto(pigStrumentiUrbanistici.getOggetto());
            dto.setTiStato(pigStrumentiUrbanistici.getTiStato().name());
        }
        return dto;
    }

    private String calcolaOggetto(PigVers pigVers, PigStrumentiUrbanistici pigStrumentiUrbanistici,
            String nomeStrumentoUrbanistico) {
        BigDecimal idEnteSiam = pigVers.getIdEnteConvenz();
        SIOrgEnteSiam sIOrgEnteSiam = strumentiUrbanisticiHelper.findById(SIOrgEnteSiam.class, idEnteSiam);
        return String.format("%s - %s %s del %s", sIOrgEnteSiam.getNmEnteSiam(), nomeStrumentoUrbanistico,
                pigStrumentiUrbanistici.getCdKey(), DateUtil.formatDateWithSlash(pigStrumentiUrbanistici.getData()));
    }

    private void creaCollegamenti(SUDto dto, PigStrumentiUrbanistici pigStrumentiUrbanistici,
            PigStrumUrbPianoStato pigStrumUrbPianoStato) {
        if (dto.getAnnoCollegato1() != null) {
            PigStrumUrbCollegamenti col = new PigStrumUrbCollegamenti();
            col.setAnno(dto.getAnnoCollegato1());
            col.setNumero(dto.getIdentificativoCollegato1());
            col.setPigStrumentiUrbanistici(pigStrumentiUrbanistici);
            PigStrumUrbPianoStato piano = strumentiUrbanisticiHelper.getPigStrumUrbPianoStatoByNomeTipoByTipoAndFase(
                    pigStrumUrbPianoStato.getNmTipoStrumentoUrbanistico(), dto.getFaseCollegata1());
            col.setPigStrumUrbPianoStato(piano);
            strumentiUrbanisticiHelper.insertEntity(col, true);
        }
        if (dto.getAnnoCollegato2() != null) {
            PigStrumUrbCollegamenti col = new PigStrumUrbCollegamenti();
            col.setAnno(dto.getAnnoCollegato2());
            col.setNumero(dto.getIdentificativoCollegato2());
            col.setPigStrumentiUrbanistici(pigStrumentiUrbanistici);
            PigStrumUrbPianoStato piano = strumentiUrbanisticiHelper.getPigStrumUrbPianoStatoByNomeTipoByTipoAndFase(
                    pigStrumUrbPianoStato.getNmTipoStrumentoUrbanistico(), dto.getFaseCollegata2());
            col.setPigStrumUrbPianoStato(piano);
            strumentiUrbanisticiHelper.insertEntity(col, true);
        }
    }

    public void cancellaSU(BigDecimal idStrumentoUrbanistico) throws ObjectStorageException {
        ObjectStorageBackend config = salvataggioBackendHelper.getObjectStorageConfiguration(
                BACKED_STRUMENTI_URBANISTICI,
                configurationHelper.getValoreParamApplicByApplic(Constants.BUCKET_VERIFICA_STRUMENTI_URBANISTICI));

        PigStrumentiUrbanistici su = strumentiUrbanisticiHelper.findById(PigStrumentiUrbanistici.class,
                idStrumentoUrbanistico);
        // Rimuove i doc da SO
        List<PigStrumUrbDocumenti> l = su.getPigStrumUrbDocumentis();
        if (this.salvataggioBackendHelper.isActive()) {
            for (PigStrumUrbDocumenti pigStrumUrbDocumenti : l) {
                this.salvataggioBackendHelper.deleteObject(config, pigStrumUrbDocumenti.getNmFileOs());
            }
            strumentiUrbanisticiHelper.removeEntity(su, true);
        }
    }

    public boolean versaStrumentoUrbanistico(BigDecimal idStrumentoUrbanistico, long idUserIamCorrente) {
        boolean retOk = false;
        // Locca SU e tutti i suoi DOC!!
        PigStrumentiUrbanistici pigStrumentiUrbanistici = strumentiUrbanisticiHelper
                .findByIdWithLock(PigStrumentiUrbanistici.class, idStrumentoUrbanistico);
        List<PigStrumUrbDocumenti> docs = pigStrumentiUrbanistici.getPigStrumUrbDocumentis();
        for (PigStrumUrbDocumenti doc : docs) {
            strumentiUrbanisticiHelper.findByIdWithLock(PigStrumUrbDocumenti.class, doc.getIdStrumUrbDocumenti());
        }
        PigVSuCheck pigVSuCheck = strumentiUrbanisticiHelper.getDatiNavigazionePerSU(idStrumentoUrbanistico);
        if (pigVSuCheck.getFlFileMancante().equals(Constants.DB_FALSE)
                && pigVSuCheck.getFlVerificaErrata().equals(Constants.DB_FALSE)
                && pigVSuCheck.getFlVerificaInCorso().equals(Constants.DB_FALSE)) {
            pigStrumentiUrbanistici.setTiStato(PigStrumentiUrbanistici.TiStato.RICHIESTA_INVIO);
            if (pigStrumentiUrbanistici.getIamUser().getIdUserIam() != idUserIamCorrente) {
                pigStrumentiUrbanistici
                        .setIamUser(strumentiUrbanisticiHelper.findById(IamUser.class, idUserIamCorrente));
            }
            retOk = true;
        }
        pigStrumentiUrbanistici.setDtStato(new Date());
        return retOk;
    }

    public void riportaInBozza(BigDecimal idStrumentoUrbanistico) {
        // Locca SU e tutti i suoi DOC!!
        PigStrumentiUrbanistici pigStrumentiUrbanistici = strumentiUrbanisticiHelper
                .findByIdWithLock(PigStrumentiUrbanistici.class, idStrumentoUrbanistico);
        pigStrumentiUrbanistici.setTiStato(PigStrumentiUrbanistici.TiStato.BOZZA); // RIMETTE IN BOZZA
        pigStrumentiUrbanistici.setDtStato(new Date());
    }

    // MEV29495 - ora elenca solo gli SU in stato VERSATO
    public DecodeMap findNumeriByVersAnnoTipoSUFaseSoloVersati(BigDecimal idPigVers, BigDecimal anno,
            String nmTipoStrumento, String fase) {
        PigVers pigVers = strumentiUrbanisticiHelper.findById(PigVers.class, idPigVers);
        List<PigStrumentiUrbanistici> strumenti = strumentiUrbanisticiHelper.findNumeriByVersAnnoTipoSUFase(pigVers,
                anno, nmTipoStrumento, fase);

        DecodeMap strumentiDecodeMap = new DecodeMap();
        BaseTable bt = new BaseTable();
        for (PigStrumentiUrbanistici strumento : strumenti) {
            BaseRow br = new BaseRow();
            br.setString(CAMPO_FLAG, strumento.getCdKey());
            br.setString(CAMPO_VALORE, strumento.getCdKey());
            bt.add(br);
        }
        strumentiDecodeMap.populatedMap(bt, CAMPO_VALORE, CAMPO_FLAG);

        return strumentiDecodeMap;
    }

    public String cancellaDoc(BigDecimal idStrumentoUrbanistico, String nmFileOrig) throws ObjectStorageException {
        String str = null;
        PigStrumentiUrbanistici pigStrumentiUrbanistici = strumentiUrbanisticiHelper
                .findById(PigStrumentiUrbanistici.class, idStrumentoUrbanistico);
        if (pigStrumentiUrbanistici.getTiStato().equals(PigStrumentiUrbanistici.TiStato.BOZZA)
                || pigStrumentiUrbanistici.getTiStato().equals(PigStrumentiUrbanistici.TiStato.ERRORE)) {
            ObjectStorageBackend config = salvataggioBackendHelper.getObjectStorageConfiguration(
                    BACKED_STRUMENTI_URBANISTICI,
                    configurationHelper.getValoreParamApplicByApplic(Constants.BUCKET_VERIFICA_STRUMENTI_URBANISTICI));

            pigStrumentiUrbanistici.setTiStato(PigStrumentiUrbanistici.TiStato.BOZZA); // RIMETTE IN BOZZA
            PigStrumUrbDocumenti pigStrumUrbDocumenti = strumentiUrbanisticiHelper
                    .getPigStrumUrbDocumentiBySuNmFileOrig(pigStrumentiUrbanistici, nmFileOrig);
            pigStrumUrbDocumenti = strumentiUrbanisticiHelper.findByIdWithLock(PigStrumUrbDocumenti.class,
                    pigStrumUrbDocumenti.getIdStrumUrbDocumenti());
            pigStrumUrbDocumenti.setFlDeleted(Constants.DB_TRUE);
            if (this.salvataggioBackendHelper.isActive()) {
                salvataggioBackendHelper.deleteObject(config, pigStrumUrbDocumenti.getNmFileOs());
            }
        } else {
            str = "Non è possibile eliminare un file di uno strumento urbanistico già inviato";
        }
        return str;
    }

    public List<DocUploadDto> findPigVSuLisDocsPianoByTipoStrumentoFase(PigStrumentiUrbanistici pigStrumentiUrbanistici,
            String nmTipoStrumento, String tiFaseStrumento) {
        List<PigStrumUrbDocumenti> listaDoc = pigStrumentiUrbanistici.getPigStrumUrbDocumentis();
        ArrayList<DocUploadDto> al = new ArrayList<>();
        List<PigVSuLisDocsPiano> l = strumentiUrbanisticiHelper
                .findPigVSuLisDocsPianoByTipoStrumentoFase(nmTipoStrumento, tiFaseStrumento);
        for (PigVSuLisDocsPiano pigVSuLisDocsPiano : l) {
            DocUploadDto dto = new DocUploadDto();
            dto.setNmTipoDocumento(pigVSuLisDocsPiano.getPigVSuLisDocsPianoId().getNmTipoDocumento());
            dto.setObbligatorio(pigVSuLisDocsPiano.getFlDocObbligatorio().equals(Constants.DB_TRUE));
            dto.setPrincipale(pigVSuLisDocsPiano.getFlDocPrincipale().equals(Constants.DB_TRUE));
            al.add(dto);
            // determina se esiste un doc già inserito
            for (PigStrumUrbDocumenti pigStrumUrbDocumenti : listaDoc) {
                if (pigStrumUrbDocumenti.getPigStrumUrbValDoc().getNmTipoDocumento().equals(dto.getNmTipoDocumento())
                        && pigStrumUrbDocumenti.getFlDeleted().equals(Constants.DB_FALSE)) {
                    dto.setNmFileOrig(pigStrumUrbDocumenti.getNmFileOrig());
                    dto.setCdErr(pigStrumUrbDocumenti.getCdErr());
                    dto.setDsErr(pigStrumUrbDocumenti.getDsErr());
                    dto.setDimensione(pigStrumUrbDocumenti.getDimensione());
                    dto.setDimensioneStringa(Utils.convertBytesToFormattedString(dto.getDimensione()));
                    dto.setDataDoc(DateUtil.formatDateWithSlashAndTime(pigStrumUrbDocumenti.getDtCaricamento()));
                    if (pigStrumUrbDocumenti.getFlEsitoVerifica() != null) {
                        dto.setFlEsitoVerifica((pigStrumUrbDocumenti.getFlEsitoVerifica().equals("S")));
                    }
                    break;
                }
            }
        }
        return al;

    }

    public List<DocUploadDto> findPigVSuLisDocsPianoByTipoStrumentoFase(BigDecimal idStrumentiUrbanistici,
            String nmTipoStrumento, String tiFaseStrumento) {
        PigStrumentiUrbanistici pigStrumentiUrbanistici = strumentiUrbanisticiHelper
                .findById(PigStrumentiUrbanistici.class, idStrumentiUrbanistici);
        return findPigVSuLisDocsPianoByTipoStrumentoFase(pigStrumentiUrbanistici, nmTipoStrumento, tiFaseStrumento);
    }

    /* Determina il nome del file secondo Object Storage */
    public String getFileOsNameBySU(BigDecimal idStrumentiUrbanistici, String nomeFileOriginale) {
        PigStrumentiUrbanistici pigStrumentiUrbanistici = strumentiUrbanisticiHelper
                .findById(PigStrumentiUrbanistici.class, idStrumentiUrbanistici);
        return pigStrumentiUrbanistici.getCdKeyOs() + "_" + Utils.eliminaPunteggiatureSpaziNomeFile(nomeFileOriginale);
    }

    public NavigazioneStrumDto getDatiNavigazionePerSU(BigDecimal idSu) {
        NavigazioneStrumDto dto = new NavigazioneStrumDto();
        PigVSuCheck pigVSuCheck = strumentiUrbanisticiHelper.getDatiNavigazionePerSU(idSu);
        if (pigVSuCheck != null) {
            dto.setFileMancante((pigVSuCheck.getFlFileMancante() != null
                    && pigVSuCheck.getFlFileMancante().equals(Constants.DB_TRUE)));
            dto.setVerificaErrata((pigVSuCheck.getFlVerificaErrata() != null
                    && pigVSuCheck.getFlVerificaErrata().equals(Constants.DB_TRUE)));
            dto.setVerificaInCorso((pigVSuCheck.getFlVerificaInCorso() != null
                    && pigVSuCheck.getFlVerificaInCorso().equals(Constants.DB_TRUE)));
        }
        return dto;
    }

    public JSONObject retrievePigErroreLikeAsJsonString(String codLike) {
        JSONObject jsonObject = new JSONObject();
        try {
            List<PigErrore> l = messaggiHelper.retrievePigErroreLike(codLike);
            for (PigErrore pigErrore : l) {
                jsonObject.put(pigErrore.getCdErrore(), pigErrore.getDsErrore());
            }
        } catch (JSONException ex) {
            log.error("Errore nella composizione del JSONObject", ex);
        }
        return jsonObject;
    }

    public void salvaDescrizioneStrumento(BigDecimal idStrumento, String descrizione) {
        PigStrumentiUrbanistici pigStrumentiUrbanistici = strumentiUrbanisticiHelper
                .findById(PigStrumentiUrbanistici.class, idStrumento);
        pigStrumentiUrbanistici.setDsDescrizione(descrizione);
    }

    /* DTOs */
    public DocStrumDto salvaTipoDocumento(DocStrumDto dto) {
        PigStrumUrbDocumenti pigStrumUrbDocumenti = new PigStrumUrbDocumenti();
        PigStrumentiUrbanistici pigStrumentiUrbanistici = strumentiUrbanisticiHelper
                .findById(PigStrumentiUrbanistici.class, dto.getIdStrumentiUrbanistici());
        if (pigStrumentiUrbanistici.getTiStato().equals(PigStrumentiUrbanistici.TiStato.ERRORE)) {
            pigStrumentiUrbanistici.setTiStato(PigStrumentiUrbanistici.TiStato.BOZZA);
        }
        PigStrumUrbValDoc pigStrumUrbValDoc = strumentiUrbanisticiHelper
                .getPigStrumUrbValDocByNomeTipoDoc(dto.getNmTipoDocumento());
        pigStrumUrbDocumenti.setPigStrumentiUrbanistici(pigStrumentiUrbanistici);
        pigStrumUrbDocumenti.setPigStrumUrbValDoc(pigStrumUrbValDoc);
        pigStrumUrbDocumenti.setNmFileOrig(dto.getNmFileOrig());
        pigStrumUrbDocumenti.setNmFileOs(dto.getNmFileOs());
        pigStrumUrbDocumenti.setDimensione(dto.getDimensione());
        pigStrumUrbDocumenti.setDtCaricamento(new Date());
        pigStrumUrbDocumenti.setFlDeleted(Constants.DB_FALSE);
        pigStrumUrbDocumenti.setFlEsitoVerifica(Constants.DB_FALSE);
        strumentiUrbanisticiHelper.insertEntity(pigStrumUrbDocumenti, true);
        dto.setIdStrumUrbDocumenti(new BigDecimal(pigStrumUrbDocumenti.getIdStrumUrbDocumenti()));
        return dto;
    }

    // Torna la deodemap con i due stati per il recupero errori
    public DecodeMapIF getNuoviStatiPerRecuperoErroriDM() {
        return ComboGetter.getMappaOrdinalGenericEnum("ti_nuovo_stato", PigStrumentiUrbanistici.TiStato.BOZZA,
                PigStrumentiUrbanistici.TiStato.RICHIESTA_INVIO);
    }

    public Date recuperoErroreSU(BigDecimal idSu, String nuovoStato) {
        PigStrumentiUrbanistici pigStrumentiUrbanistici = strumentiUrbanisticiHelper
                .findById(PigStrumentiUrbanistici.class, idSu);
        pigStrumentiUrbanistici.setTiStato(PigStrumentiUrbanistici.TiStato.valueOf(nuovoStato));
        pigStrumentiUrbanistici.setDtStato(new Date());
        return pigStrumentiUrbanistici.getDtStato();
    }

    public String getOggettoStrumentoUrbanistico(BigDecimal idStrumentoUrbanistico) {
        return (strumentiUrbanisticiHelper.findById(PigStrumentiUrbanistici.class, idStrumentoUrbanistico))
                .getOggetto();
    }

    public boolean existsCondizioniInvio(BigDecimal idStrumentoUrbanistico) {
        PigVSuCheck check = strumentiUrbanisticiHelper.findViewById(PigVSuCheck.class, idStrumentoUrbanistico);
        // Controlli sui documenti dello strumento urbanistico attraverso i valori della vista
        if (check.getFlVerificaErrata().equals("1") || check.getFlVerificaInCorso().equals("1")
                || check.getFlFileMancante().equals("1")) {
            return false;
        }
        return true;
    }

    public boolean existsVerificaInCorso(BigDecimal idStrumentoUrbanistico) {
        PigVSuCheck check = strumentiUrbanisticiHelper.findViewById(PigVSuCheck.class, idStrumentoUrbanistico);
        // Controlli sui documenti dello strumento urbanistico attraverso i valori della vista
        return check.getFlVerificaInCorso().equals("1");
    }

    public boolean existsPigStrumUrbDocumenti(BigDecimal idStrumentoUrbanistico) {
        return strumentiUrbanisticiHelper.existsPigStrumUrbDocumenti(idStrumentoUrbanistico);
    }

    public static class NavigazioneStrumDto extends GenericDto {

        private boolean verificaInCorso;
        private boolean verificaErrata;
        private boolean fileMancante;

        public boolean isVerificaInCorso() {
            return verificaInCorso;
        }

        public void setVerificaInCorso(boolean verificaInCorso) {
            this.verificaInCorso = verificaInCorso;
        }

        public boolean isVerificaErrata() {
            return verificaErrata;
        }

        public void setVerificaErrata(boolean verificaErrata) {
            this.verificaErrata = verificaErrata;
        }

        public boolean isFileMancante() {
            return fileMancante;
        }

        public void setFileMancante(boolean fileMancante) {
            this.fileMancante = fileMancante;
        }

    }

    public static class DocStrumDto extends GenericDto {

        private BigDecimal idStrumUrbDocumenti;
        private BigDecimal idStrumentiUrbanistici;
        private BigDecimal dimensione;
        private int numFileCaricati;
        private String nmFileOrig;
        private String nmFileOs;
        private String nmTipoDocumento;
        private boolean obbligatorio;
        private String cdErr;
        private String dsErr;
        private boolean flEsitoVerifica;
        private Date dtCaricamento;
        private String blReport;

        public String getBlReport() {
            return blReport;
        }

        public void setBlReport(String blReport) {
            this.blReport = blReport;
        }

        public BigDecimal getIdStrumUrbDocumenti() {
            return idStrumUrbDocumenti;
        }

        public void setIdStrumUrbDocumenti(BigDecimal idStrumUrbDocumenti) {
            this.idStrumUrbDocumenti = idStrumUrbDocumenti;
        }

        public BigDecimal getIdStrumentiUrbanistici() {
            return idStrumentiUrbanistici;
        }

        public void setIdStrumentiUrbanistici(BigDecimal idStrumentiUrbanistici) {
            this.idStrumentiUrbanistici = idStrumentiUrbanistici;
        }

        public int getNumFileCaricati() {
            return numFileCaricati;
        }

        public void setNumFileCaricati(int numFileCaricati) {
            this.numFileCaricati = numFileCaricati;
        }

        public String getNmFileOrig() {
            return nmFileOrig;
        }

        public void setNmFileOrig(String nmFileOrig) {
            this.nmFileOrig = nmFileOrig;
        }

        public String getNmFileOs() {
            return nmFileOs;
        }

        public void setNmFileOs(String nmFileOs) {
            this.nmFileOs = nmFileOs;
        }

        public String getNmTipoDocumento() {
            return nmTipoDocumento;
        }

        public void setNmTipoDocumento(String nmTipoDocumento) {
            this.nmTipoDocumento = nmTipoDocumento;
        }

        public BigDecimal getDimensione() {
            return dimensione;
        }

        public void setDimensione(BigDecimal dimensione) {
            this.dimensione = dimensione;
        }

        public boolean isObbligatorio() {
            return obbligatorio;
        }

        public void setObbligatorio(boolean obbligatorio) {
            this.obbligatorio = obbligatorio;
        }

        public String getCdErr() {
            return cdErr;
        }

        public void setCdErr(String cdErr) {
            this.cdErr = cdErr;
        }

        public String getDsErr() {
            return dsErr;
        }

        public void setDsErr(String dsErr) {
            this.dsErr = dsErr;
        }

        public boolean isFlEsitoVerifica() {
            return flEsitoVerifica;
        }

        public void setFlEsitoVerifica(boolean flEsitoVerifica) {
            this.flEsitoVerifica = flEsitoVerifica;
        }

        public Date getDtCaricamento() {
            return dtCaricamento;
        }

        public void setDtCaricamento(Date dtCaricamento) {
            this.dtCaricamento = dtCaricamento;
        }

    }

    public static class DocUploadDto extends GenericDto {

        private String nmTipoDocumento;
        private boolean obbligatorio;
        private boolean principale;
        private String nmFileOrig;
        private String cdErr;
        private String dsErr;
        private boolean flEsitoVerifica;
        private BigDecimal dimensione;
        private String dimensioneStringa;
        private String dataDoc;

        public String getNmTipoDocumento() {
            return nmTipoDocumento;
        }

        public void setNmTipoDocumento(String nmTipoDocumento) {
            this.nmTipoDocumento = nmTipoDocumento;
        }

        public String getCdErr() {
            return cdErr;
        }

        public void setCdErr(String cdErr) {
            this.cdErr = cdErr;
        }

        public String getDsErr() {
            return dsErr;
        }

        public void setDsErr(String dsErr) {
            this.dsErr = dsErr;
        }

        public boolean isFlEsitoVerifica() {
            return flEsitoVerifica;
        }

        public void setFlEsitoVerifica(boolean flEsitoVerifica) {
            this.flEsitoVerifica = flEsitoVerifica;
        }

        public boolean isObbligatorio() {
            return obbligatorio;
        }

        public void setObbligatorio(boolean obbligatorio) {
            this.obbligatorio = obbligatorio;
        }

        public boolean isPrincipale() {
            return principale;
        }

        public void setPrincipale(boolean principale) {
            this.principale = principale;
        }

        public String getNmFileOrig() {
            return nmFileOrig;
        }

        public void setNmFileOrig(String nmFileOrig) {
            this.nmFileOrig = nmFileOrig;
        }

        public BigDecimal getDimensione() {
            return dimensione;
        }

        public void setDimensione(BigDecimal dimensione) {
            this.dimensione = dimensione;
        }

        public String getDimensioneStringa() {
            return dimensioneStringa;
        }

        public void setDimensioneStringa(String dimensioneStringa) {
            this.dimensioneStringa = dimensioneStringa;
        }

        public String getDataDoc() {
            return dataDoc;
        }

        public void setDataDoc(String dataDoc) {
            this.dataDoc = dataDoc;
        }

    }

    public static class SUDto extends GenericDto {

        private long idStrumentiUrbanistici;
        private BigDecimal idVers;
        private String tiStato;
        private String dsDescrizione;
        private Date dtStato;
        private Date dtCreazione;
        private Date data;
        private BigDecimal priorita;
        private long idUserIam;
        private BigDecimal anno;
        private String numero;
        private String nmAmbienteVers;
        private String nmTipoStrumentoUrbanistico;
        private String tiFaseStrumento;
        private String cdKey;
        private String oggetto;
        private PigStrumUrbAtto tiAtto;
        private BigDecimal annoCollegato1;
        private BigDecimal annoCollegato2;
        private String identificativoCollegato1;
        private String identificativoCollegato2;
        private String faseCollegata1;
        private String faseCollegata2;
        private String cdErr;
        private String dsErr;

        public SUDto() {
        }

        public String getFaseCollegata1() {
            return faseCollegata1;
        }

        public void setFaseCollegata1(String faseCollegata1) {
            this.faseCollegata1 = faseCollegata1;
        }

        public String getFaseCollegata2() {
            return faseCollegata2;
        }

        public void setFaseCollegata2(String faseCollegata2) {
            this.faseCollegata2 = faseCollegata2;
        }

        public Date getDtStato() {
            return dtStato;
        }

        public void setDtStato(Date dtStato) {
            this.dtStato = dtStato;
        }

        public String getDsDescrizione() {
            return dsDescrizione;
        }

        public void setDsDescrizione(String dsDescrizione) {
            this.dsDescrizione = dsDescrizione;
        }

        public String getCdKey() {
            return cdKey;
        }

        public void setCdKey(String cdKey) {
            this.cdKey = cdKey;
        }

        public Date getData() {
            return data;
        }

        public void setData(Date data) {
            this.data = data;
        }

        public long getIdStrumentiUrbanistici() {
            return idStrumentiUrbanistici;
        }

        public void setIdStrumentiUrbanistici(long idStrumentiUrbanistici) {
            this.idStrumentiUrbanistici = idStrumentiUrbanistici;
        }

        public BigDecimal getIdVers() {
            return idVers;
        }

        public void setIdVers(BigDecimal idVers) {
            this.idVers = idVers;
        }

        public String getTiStato() {
            return tiStato;
        }

        public void setTiStato(String tiStato) {
            this.tiStato = tiStato;
        }

        public Date getDtCreazione() {
            return dtCreazione;
        }

        public void setDtCreazione(Date dtCreazione) {
            this.dtCreazione = dtCreazione;
        }

        public BigDecimal getPriorita() {
            return priorita;
        }

        public void setPriorita(BigDecimal priorita) {
            this.priorita = priorita;
        }

        public long getIdUserIam() {
            return idUserIam;
        }

        public void setIdUserIam(long idUserIam) {
            this.idUserIam = idUserIam;
        }

        public BigDecimal getAnno() {
            return anno;
        }

        public void setAnno(BigDecimal anno) {
            this.anno = anno;
        }

        public String getNumero() {
            return numero;
        }

        public void setNumero(String numero) {
            this.numero = numero;
        }

        public String getNmAmbienteVers() {
            return nmAmbienteVers;
        }

        public void setNmAmbienteVers(String nmAmbienteVers) {
            this.nmAmbienteVers = nmAmbienteVers;
        }

        public String getNmTipoStrumentoUrbanistico() {
            return nmTipoStrumentoUrbanistico;
        }

        public void setNmTipoStrumentoUrbanistico(String nmTipoStrumentoUrbanistico) {
            this.nmTipoStrumentoUrbanistico = nmTipoStrumentoUrbanistico;
        }

        public String getTiFaseStrumento() {
            return tiFaseStrumento;
        }

        public void setTiFaseStrumento(String tiFaseStrumento) {
            this.tiFaseStrumento = tiFaseStrumento;
        }

        public String getOggetto() {
            return oggetto;
        }

        public void setOggetto(String oggetto) {
            this.oggetto = oggetto;
        }

        public BigDecimal getAnnoCollegato1() {
            return annoCollegato1;
        }

        public void setAnnoCollegato1(BigDecimal annoCollegato1) {
            this.annoCollegato1 = annoCollegato1;
        }

        public BigDecimal getAnnoCollegato2() {
            return annoCollegato2;
        }

        public void setAnnoCollegato2(BigDecimal annoCollegato2) {
            this.annoCollegato2 = annoCollegato2;
        }

        public String getIdentificativoCollegato1() {
            return identificativoCollegato1;
        }

        public void setIdentificativoCollegato1(String identificativoCollegato1) {
            this.identificativoCollegato1 = identificativoCollegato1;
        }

        public String getIdentificativoCollegato2() {
            return identificativoCollegato2;
        }

        public void setIdentificativoCollegato2(String identificativoCollegato2) {
            this.identificativoCollegato2 = identificativoCollegato2;
        }

        public String getCdErr() {
            return cdErr;
        }

        public void setCdErr(String cdErr) {
            this.cdErr = cdErr;
        }

        public String getDsErr() {
            return dsErr;
        }

        public void setDsErr(String dsErr) {
            this.dsErr = dsErr;
        }

        public PigStrumUrbAtto getTiAtto() {
            return tiAtto;
        }

        public void setTiAtto(PigStrumUrbAtto tiAtto) {
            this.tiAtto = tiAtto;
        }
    }

    public static class ListaSUDto {

        private long idStrumentiUrbanistici;
        private String nmTipoStrumentoUrbanistico;
        private String tiFaseStrumento;
        private long anno;
        private String cdKey;
        private String tiStato;
        private String dsDescrizione;
        private Date dtCreazione;
        private BigDecimal dimensione;

        public long getIdStrumentiUrbanistici() {
            return idStrumentiUrbanistici;
        }

        public void setIdStrumentiUrbanistici(long idStrumentiUrbanistici) {
            this.idStrumentiUrbanistici = idStrumentiUrbanistici;
        }

        public String getNmTipoStrumentoUrbanistico() {
            return nmTipoStrumentoUrbanistico;
        }

        public void setNmTipoStrumentoUrbanistico(String nmTipoStrumentoUrbanistico) {
            this.nmTipoStrumentoUrbanistico = nmTipoStrumentoUrbanistico;
        }

        public String getTiFaseStrumento() {
            return tiFaseStrumento;
        }

        public void setTiFaseStrumento(String tiFaseStrumento) {
            this.tiFaseStrumento = tiFaseStrumento;
        }

        public long getAnno() {
            return anno;
        }

        public void setAnno(long anno) {
            this.anno = anno;
        }

        public String getCdKey() {
            return cdKey;
        }

        public void setCdKey(String cdKey) {
            this.cdKey = cdKey;
        }

        public String getTiStato() {
            return tiStato;
        }

        public void setTiStato(String tiStato) {
            this.tiStato = tiStato;
        }

        public String getDsDescrizione() {
            return dsDescrizione;
        }

        public void setDsDescrizione(String dsDescrizione) {
            this.dsDescrizione = dsDescrizione;
        }

        public Date getDtCreazione() {
            return dtCreazione;
        }

        public void setDtCreazione(Date dtCreazione) {
            this.dtCreazione = dtCreazione;
        }

        public BigDecimal getDimensione() {
            return dimensione;
        }

        public void setDimensione(BigDecimal dimensione) {
            this.dimensione = dimensione;
        }
    }
}
