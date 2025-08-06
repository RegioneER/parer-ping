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

/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */
package it.eng.sacerasi.ws.ricerca.ricercaRecuperati.ejb;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import it.eng.sacerasi.ws.ricerca.ricercaRecuperati.dto.ListaOggRicRecuperatiType;
import it.eng.sacerasi.ws.ricerca.ricercaRecuperati.dto.OggRicRecuperatiType;

/**
 *
 * @author Filippini_M
 */
@Stateless(mappedName = "RicercaRecuperatiExecuteQueries")
@LocalBean
public class RicercaRecuperatiExecuteQueries {

    @PersistenceContext(unitName = "SacerAsiJPA")
    private EntityManager entityManager;

    public ListaOggRicRecuperatiType querySearch(String nmAmbienteVers, String nmVersatore) {

	ListaOggRicRecuperatiType listaOggType = new ListaOggRicRecuperatiType();
	List<OggRicRecuperatiType> listaOgg = new ArrayList<>();

	StringBuilder queryStr = new StringBuilder(
		"SELECT sessioneRecup.cdKeyObject FROM PigSessioneRecup sessioneRecup "
			+ " WHERE sessioneRecup.tiStato = 'RECUPERATO' AND sessioneRecup.nmAmbienteVers = :nmAmbienteVers "
			+ " AND sessioneRecup.nmVers = :nmVersatore");

	Query query = entityManager.createQuery(queryStr.toString());
	query.setParameter("nmAmbienteVers", nmAmbienteVers);
	query.setParameter("nmVersatore", nmVersatore);

	List<String> listaSess = query.getResultList();

	for (String cdKey : listaSess) {
	    OggRicRecuperatiType ogg = new OggRicRecuperatiType();
	    ogg.setCdKeyObject(cdKey);
	    listaOgg.add(ogg);
	}
	listaOggType.setOggetto(listaOgg);

	return listaOggType;
    }

}
