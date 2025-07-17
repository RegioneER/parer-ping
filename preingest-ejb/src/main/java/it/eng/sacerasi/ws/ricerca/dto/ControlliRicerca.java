/*
 * Engineering Ingegneria Informatica S.p.A.
 *
 * Copyright (C) 2023 Regione Emilia-Romagna <p/> This program is free software: you can
 * redistribute it and/or modify it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version. <p/> This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Affero General Public License for more details. <p/> You should
 * have received a copy of the GNU Affero General Public License along with this program. If not,
 * see <https://www.gnu.org/licenses/>.
 */

package it.eng.sacerasi.ws.ricerca.dto;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.sacerasi.entity.PigTipoObject;
import it.eng.sacerasi.ws.dto.RispostaControlli;
import it.eng.sacerasi.ws.util.Costanti.AttribDatiSpecDataType;

/**
 *
 * @author Gilioli_P
 */
@Stateless(mappedName = "ControlliRicerca")
@LocalBean
public class ControlliRicerca {

    private static final Logger log = LoggerFactory.getLogger(ControlliRicerca.class);
    @PersistenceContext(unitName = "SacerAsiJPA")
    private EntityManager entityManager;

    /**
     * Verifica che se il nome del tipo oggetto non è definito in input, allora non possono essere
     * definiti i vari XML
     *
     * @param nmTipoObject      nome tipo oggetto
     * @param xmlDatiSpecFiltri xml dati specifici (filtri)
     * @param xmlDatiSpecOutput xml dati specifici
     * @param xmlDatiSpecOrder  xml dati specifici (ordinamento)
     *
     * @return rispostaControlli, l'esito della verifica
     */
    public RispostaControlli verificaNomeTipoObjectConXML(String nmTipoObject,
	    String xmlDatiSpecFiltri, String xmlDatiSpecOutput, String xmlDatiSpecOrder) {
	RispostaControlli rispostaControlli = new RispostaControlli();
	rispostaControlli.setrBoolean(true);
	if (StringUtils.isBlank(nmTipoObject) && (StringUtils.isNotBlank(xmlDatiSpecFiltri)
		|| StringUtils.isNotBlank(xmlDatiSpecOutput)
		|| StringUtils.isNotBlank(xmlDatiSpecOrder))) {
	    rispostaControlli.setrBoolean(false);
	}
	return rispostaControlli;
    }

    /**
     * Verifica l'xml relativo ai filtri in maniera tale che ogni filtro sia previsto da almeno una
     * versione dell'XSD dei dati specifici per il dato tipo di oggetto e che lo stesso filtro possa
     * essere utilizzato come tale. Se ogni filtro ricavo anche il nome colonna corrispondente nel
     * DB
     *
     * @param idTipoObject                      id tipo oggetto
     * @param listaDatiSpecFiltriConNomeColonna lista dati specifici di tipo
     *                                          {@link DatoSpecFiltroConNomeColonna}
     * @param attribDatiSpecBean                lista attributi {@link AttribDatiSpecBean}
     *
     * @return rispostaControlli, l'esito della verifica e l'insieme dei filtri con l'aggiunta del
     *         nome colonna corrispondente su DB
     */
    public RispostaControlli verificaXMLDatiSpecFiltri(long idTipoObject,
	    List<DatoSpecFiltroConNomeColonna> listaDatiSpecFiltriConNomeColonna,
	    List<AttribDatiSpecBean> attribDatiSpecBean) {
	RispostaControlli rispostaControlli = new RispostaControlli();
	rispostaControlli.setrBoolean(true);

	/*
	 * Creo tre liste per 3 controlli: una contenente gli eventuali filtri non trovati, una
	 * contenente gli eventuali attributi non filtri ed una contenente gli eventuali attributi
	 * con nome colonna DB uguale a null
	 */
	List<String> attributiNonTrovati = new ArrayList<>();
	List<String> attributiNonFiltri = new ArrayList<>();
	List<String> attributiConCampoColonnaDBNullo = new ArrayList<>();

	// Confronto le due liste: i filtri con gli attributi dati spec.
	for (DatoSpecFiltroConNomeColonna filtroTypeConNomeColonna : listaDatiSpecFiltriConNomeColonna) {
	    boolean trovato = false;
	    // Controllo quale tipo di filtro sto trattando: se un valore, nullo oppure a due valori
	    if (filtroTypeConNomeColonna.getFiltro().getFiltroUnValore() != null) {
		for (AttribDatiSpecBean ab : attribDatiSpecBean) {
		    if (filtroTypeConNomeColonna.getFiltro().getFiltroUnValore().getDatoSpecifico()
			    .equals(ab.getNmAttribDatiSpec())) {
			trovato = true;
			if (ab.getFlFiltroDiario().equals("0")) {
			    attributiNonFiltri.add(ab.getNmAttribDatiSpec());
			} else {
			    // Tutto OK: ricavo il nome colonna da DB, controllando che esso sia
			    // diverso da null
			    if (ab.getNomeColonna() != null) {
				filtroTypeConNomeColonna.setColumnName(ab.getNomeColonna());
				filtroTypeConNomeColonna.setDataType(ab.getDataType());
			    } else {
				attributiConCampoColonnaDBNullo.add(ab.getNmAttribDatiSpec());
			    }
			    break;
			}
		    }
		}
		if (!trovato) {
		    attributiNonTrovati.add(filtroTypeConNomeColonna.getFiltro().getFiltroUnValore()
			    .getDatoSpecifico());
		}
	    } else if (filtroTypeConNomeColonna.getFiltro().getFiltroNullo() != null) {
		for (AttribDatiSpecBean ab : attribDatiSpecBean) {
		    if (filtroTypeConNomeColonna.getFiltro().getFiltroNullo().getDatoSpecifico()
			    .equals(ab.getNmAttribDatiSpec())) {
			trovato = true;
			if (ab.getFlFiltroDiario().equals("0")) {
			    attributiNonFiltri.add(ab.getNmAttribDatiSpec());
			} else {
			    // Tutto OK: ricavo il nome colonna da DB, controllando che esso sia
			    // diverso da null
			    if (ab.getNomeColonna() != null) {
				filtroTypeConNomeColonna.setColumnName(ab.getNomeColonna());
				filtroTypeConNomeColonna.setDataType(ab.getDataType());
			    } else {
				attributiConCampoColonnaDBNullo.add(ab.getNmAttribDatiSpec());
			    }
			    break;
			}
		    }
		}
		if (!trovato) {
		    attributiNonTrovati.add(filtroTypeConNomeColonna.getFiltro().getFiltroNullo()
			    .getDatoSpecifico());
		}
	    } else if (filtroTypeConNomeColonna.getFiltro().getFiltroDueValori() != null) {
		for (AttribDatiSpecBean ab : attribDatiSpecBean) {
		    if (filtroTypeConNomeColonna.getFiltro().getFiltroDueValori().getDatoSpecifico()
			    .equals(ab.getNmAttribDatiSpec())) {
			trovato = true;
			if (ab.getFlFiltroDiario().equals("0")) {
			    attributiNonFiltri.add(ab.getNmAttribDatiSpec());
			} else {
			    // Tutto OK: ricavo il nome colonna da DB, controllando che esso sia
			    // diverso da null
			    if (ab.getNomeColonna() != null) {
				filtroTypeConNomeColonna.setColumnName(ab.getNomeColonna());
				filtroTypeConNomeColonna.setDataType(ab.getDataType());
			    } else {
				attributiConCampoColonnaDBNullo.add(ab.getNmAttribDatiSpec());
			    }
			    break;
			}
		    }
		}
		if (!trovato) {
		    attributiNonTrovati.add(filtroTypeConNomeColonna.getFiltro()
			    .getFiltroDueValori().getDatoSpecifico());
		}
	    }
	}
	// Primo controllo: verifico che siano stati trovati tutti i filtri
	if (!attributiNonTrovati.isEmpty()) {
	    rispostaControlli.setrBoolean(false);
	    rispostaControlli.setCodErr("006");
	    rispostaControlli.setrObject(attributiNonTrovati);
	    rispostaControlli.setrString(
		    (entityManager.find(PigTipoObject.class, idTipoObject)).getNmTipoObject());
	} // Altrimenti, secondo controllo: verifico se siano tutti di tipo filtro
	else if (!attributiNonFiltri.isEmpty()) {
	    rispostaControlli.setrBoolean(false);
	    rispostaControlli.setCodErr("007");
	    rispostaControlli.setrObject(attributiNonFiltri);
	    rispostaControlli.setrString(
		    (entityManager.find(PigTipoObject.class, idTipoObject)).getNmTipoObject());
	} // Altrimenti, terzo controllo: verifico che abbiano tutti il campo colonna DB valorizzato
	else if (!attributiConCampoColonnaDBNullo.isEmpty()) {
	    rispostaControlli.setrBoolean(false);
	    rispostaControlli.setCodErr("666");
	    rispostaControlli.setrObject(attributiConCampoColonnaDBNullo);
	    rispostaControlli.setrString(" - Il nome colonna del/dei filtro/i su DB è nullo ");
	} // Altrimenti, restituisco i filtri con l'aggiunta del nome colonna del DB
	else {
	    rispostaControlli.setrObject(listaDatiSpecFiltriConNomeColonna);
	}
	return rispostaControlli;
    }

    /**
     * Verifica l'xml relativo ai dati specifici da presentare in output
     *
     * @param idTipoObject            id tipo oggetto
     * @param listaDatiSpecOutput     lista dati specifici
     * @param listaAttribDatiSpecBean lista attributi dati specifici di tipo
     *                                {@link AttribDatiSpecBean}
     *
     * @return rispostaControlli, l'esito della verifica e l'insieme dei dati spec di output con
     *         l'aggiunta del nome colonna corrispondente su DB
     */
    public RispostaControlli verificaXMLDatiSpecOutput(long idTipoObject,
	    List<String> listaDatiSpecOutput, List<AttribDatiSpecBean> listaAttribDatiSpecBean) {
	RispostaControlli rispostaControlli = new RispostaControlli();
	rispostaControlli.setrBoolean(true);
	List<String> attributiNonTrovati = new ArrayList<>();
	List<String> attributiConCampoColonnaDBNullo = new ArrayList<>();

	/*
	 * Creo una LinkedHashMap che conterrà come chiave il dato specifico e come valore il suo
	 * nome colonna nel DB
	 */
	// LinkedHashMap<String, String> hsDatiSpecOutput = new LinkedHashMap<>();

	/*
	 * Creo una lista che conterr� il dato specifico che voglio in output con l'aggiunta del
	 * nome colonna del DB
	 */
	List<DatoSpecOutputConNomeColonna> listaDatiSpecOutputConNomeColonna = new ArrayList<>();

	// Confronto le due liste: i dati specifici di output con gli attributi dati spec.
	for (String datoSpecOut : listaDatiSpecOutput) {
	    boolean trovato = false;
	    for (AttribDatiSpecBean ab : listaAttribDatiSpecBean) {
		if (datoSpecOut.equals(ab.getNmAttribDatiSpec())) {
		    trovato = true;
		    // Tutto OK: ricavo il nome colonna da DB, controllando che esso sia diverso da
		    // null
		    if (ab.getNomeColonna() != null) {
			DatoSpecOutputConNomeColonna dato = new DatoSpecOutputConNomeColonna();
			dato.setDatoSpecificoOutput(datoSpecOut);
			dato.setColumnName(ab.getNomeColonna());
			dato.setDataType(ab.getDataType());
			listaDatiSpecOutputConNomeColonna.add(dato);
		    } else {
			attributiConCampoColonnaDBNullo.add(ab.getNmAttribDatiSpec());
		    }
		    break;
		}
	    }
	    if (!trovato) {
		attributiNonTrovati.add(datoSpecOut);
	    }
	}

	// Verifico che siano stati trovati tutti i dati specifici di output...
	if (attributiNonTrovati.size() > 0) {
	    rispostaControlli.setrBoolean(false);
	    rispostaControlli.setCodErr("008");
	    rispostaControlli.setrObject(attributiNonTrovati);
	    rispostaControlli.setrString(
		    (entityManager.find(PigTipoObject.class, idTipoObject)).getNmTipoObject());
	} // ...quindi che abbiano il nome colonna DB diverso da null...
	else if (attributiConCampoColonnaDBNullo.size() > 0) {
	    rispostaControlli.setrBoolean(false);
	    rispostaControlli.setCodErr("666");
	    rispostaControlli.setrObject(attributiConCampoColonnaDBNullo);
	    rispostaControlli.setrString(" - Il nome colonna del/dei filtro/i su DB � nullo ");
	} // ...e in caso affermativo restituisco i dati specifici di output con l'aggiunta del nome
	  // colonna del DB
	else {
	    rispostaControlli.setrObject(listaDatiSpecOutputConNomeColonna);
	}
	return rispostaControlli;
    }

    /**
     * Verifica l'xml relativo all'ordine con cui presentare i dati specifici in output
     *
     * @param idTipoObject                     id tipo oggetto
     * @param listaDatiSpecOrderConNomeColonna lista dati specifici ordinati per nome colonna di
     *                                         tipo {@link DatoSpecOrderConNomeColonna}
     * @param listaAttribDatiSpecBean          lista attributi dati specifici di tipo
     *                                         {@link AttribDatiSpecBean}
     *
     * @return rispostaControlli, l'esito della verifica e l'insieme dei dati spec di output con
     *         relativo ordine, con l'aggiunta del nome colonna corrispondente su DB
     */
    public RispostaControlli verificaXMLDatiSpecOrder(long idTipoObject,
	    List<DatoSpecOrderConNomeColonna> listaDatiSpecOrderConNomeColonna,
	    List<AttribDatiSpecBean> listaAttribDatiSpecBean) {
	RispostaControlli rispostaControlli = new RispostaControlli();
	rispostaControlli.setrBoolean(true);
	List<String> attributiNonTrovati = new ArrayList<>();
	List<String> attributiConCampoColonnaDBNullo = new ArrayList<>();

	// Confronto le due liste: i dati specifici per l'ordine con gli attributi dati spec.
	for (DatoSpecOrderConNomeColonna datoSpecOrder : listaDatiSpecOrderConNomeColonna) {
	    boolean trovato = false;
	    for (AttribDatiSpecBean ab : listaAttribDatiSpecBean) {
		if (datoSpecOrder.getDatoSpecificoOrder().getDatoSpecifico()
			.equals(ab.getNmAttribDatiSpec())) {
		    trovato = true;
		    // Tutto OK: ricavo il nome colonna da DB, controllando che esso sia diverso da
		    // null
		    if (ab.getNomeColonna() != null) {
			datoSpecOrder.setColumnName(ab.getNomeColonna());
		    } else {
			attributiConCampoColonnaDBNullo.add(ab.getNmAttribDatiSpec());
		    }
		    break;
		}
	    }
	    if (!trovato) {
		attributiNonTrovati.add(datoSpecOrder.getDatoSpecificoOrder().getDatoSpecifico());
	    }
	}

	// Verifico che siano stati trovati tutti i dati specifici di ordine...
	if (attributiNonTrovati.size() > 0) {
	    rispostaControlli.setrBoolean(false);
	    rispostaControlli.setCodErr("009");
	    rispostaControlli.setrObject(attributiNonTrovati);
	    rispostaControlli.setrString(
		    (entityManager.find(PigTipoObject.class, idTipoObject)).getNmTipoObject());
	} // ...quindi che abbiano il nome colonna DB diverso da null...
	else if (attributiConCampoColonnaDBNullo.size() > 0) {
	    rispostaControlli.setrBoolean(false);
	    rispostaControlli.setCodErr("666");
	    rispostaControlli.setrObject(attributiConCampoColonnaDBNullo);
	    rispostaControlli.setrString(" - Il nome colonna del/dei filtro/i su DB � nullo ");
	} // ...e in caso affermativo restituisco i dati specifici per l'ordine con l'aggiunta del
	  // nome colonna del DB
	else {
	    rispostaControlli.setrObject(listaDatiSpecOrderConNomeColonna);
	}
	return rispostaControlli;
    }

    /**
     * Ricavo gli attributi dati specifici relativi ad un determinato tipo object
     *
     * @param idTipoObject id tipo oggetto
     *
     * @return listaAttribDatiSpecBean, la lista degli attributi dati specifici
     *         {@link AttribDatiSpecBean}
     */
    @SuppressWarnings("unchecked")
    public List<AttribDatiSpecBean> getAttribDatiSpecBean(long idTipoObject) {
	List<AttribDatiSpecBean> listaAttribDatiSpecBean = new ArrayList<>();
	try {
	    String queryStr = "SELECT DISTINCT attrib_dati_spec.nmAttribDatiSpec, "
		    + "attrib_dati_spec.flFiltroDiario, " + "attrib_dati_spec.nmColDatiSpec, "
		    + "attrib_dati_spec.tiDatatypeCol " + "FROM PigTipoObject tipo_object "
		    + "JOIN tipo_object.pigXsdDatiSpecs xsd_dati_spec "
		    + "JOIN xsd_dati_spec.pigAttribDatiSpecs attrib_dati_spec "
		    + "WHERE tipo_object.idTipoObject = :idTipoObject ";

	    javax.persistence.Query query = entityManager.createQuery(queryStr);
	    query.setParameter("idTipoObject", idTipoObject);

	    List<Object[]> attribDatiSpecObjectList = query.getResultList();

	    for (Object[] o : attribDatiSpecObjectList) {
		AttribDatiSpecBean bean = new AttribDatiSpecBean();
		bean.setNmAttribDatiSpec((String) o[0]);
		bean.setFlFiltroDiario((String) o[1]);
		bean.setNomeColonna((String) o[2]);
		String dataType = (String) o[3];
		bean.setDataType(AttribDatiSpecDataType.valueOf(dataType));
		listaAttribDatiSpecBean.add(bean);
	    }

	} catch (Exception e) {
	    log.error(
		    "Eccezione nel join tra le tabelle PigTipoObject, PigXsdDatiSpec e PigAttribDatiSpec",
		    e);
	}
	return listaAttribDatiSpecBean;
    }
}
