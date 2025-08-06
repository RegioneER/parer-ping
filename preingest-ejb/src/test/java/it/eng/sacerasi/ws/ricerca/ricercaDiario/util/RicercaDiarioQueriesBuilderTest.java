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

package it.eng.sacerasi.ws.ricerca.ricercaDiario.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import it.eng.sacerasi.common.Constants;
import it.eng.sacerasi.ws.ricerca.dto.DatoSpecFiltroConNomeColonna;
import it.eng.sacerasi.ws.ricerca.dto.DatoSpecOrderConNomeColonna;
import it.eng.sacerasi.ws.ricerca.ricercaDiario.dto.RicercaDiarioExt;
import it.eng.sacerasi.ws.ricerca.ricercaDiario.dto.RicercaDiarioInput;
import it.eng.sacerasi.ws.util.Costanti;
import it.eng.sacerasi.ws.xml.datiSpecFiltri.FiltroType;
import it.eng.sacerasi.ws.xml.datiSpecOrder.OrderType;

class RicercaDiarioQueriesBuilderTest {

    @Test
    void buildQueryConClausolaIN() {
	RicercaDiarioExt ricercaDiarioExt = new RicercaDiarioExt();
	ricercaDiarioExt.setRicercaDiarioInput(new RicercaDiarioInput("AMBIENTE", "VERSATORE",
		Constants.STUDIO_DICOM, "KEY OBJECT", 0L, "STATO OBJECT", true, 1, 1, "",
		"<xml></xml>", "<xml></xml>"));
	ricercaDiarioExt.setDatiSpecFiltriConNomeColonna(new ArrayList<>());

	ricercaDiarioExt.setDatiSpecOrderConNomeColonna(new ArrayList<>());
	OrderType orderType = new OrderType();
	orderType.setDatoSpecifico("COL_ORDER");
	orderType.setTipoOrder("DESC");
	DatoSpecOrderConNomeColonna datoSpecOrderConNomeColonna = new DatoSpecOrderConNomeColonna(
		orderType, "COL_NAME_ORDER");
	ricercaDiarioExt.getDatiSpecOrderConNomeColonna().add(datoSpecOrderConNomeColonna);
	FiltroType filtroType = new FiltroType();
	FiltroType.FiltroUnValore filtroUnValore = new FiltroType.FiltroUnValore();
	filtroUnValore.setDatoSpecifico("DATO_SPEC");
	filtroUnValore.setOperatore("IN");
	filtroUnValore.setValore("VALORE");
	filtroType.setFiltroUnValore(filtroUnValore);
	DatoSpecFiltroConNomeColonna datoSpecifico = new DatoSpecFiltroConNomeColonna(filtroType,
		"COL_IN_TEST");
	datoSpecifico.setDataType(Costanti.AttribDatiSpecDataType.ALFANUMERICO);
	ricercaDiarioExt.getDatiSpecFiltriConNomeColonna().add(datoSpecifico);

	RicercaDiarioQueriesBuilder builder = new RicercaDiarioQueriesBuilder(ricercaDiarioExt);
	final Object[] objects = builder.buildQueries();
	assertEquals(
		"SELECT COUNT(oggetto) FROM PigObject oggetto JOIN oggetto.pigSessioneIngests sessione_ingest JOIN oggetto.pigInfoDicoms info_dicom WHERE oggetto.idObject = sessione_ingest.pigObject.idObject AND sessione_ingest.nmAmbienteVers = :valore0 AND sessione_ingest.nmVers = :valore1 AND oggetto.pigTipoObject.nmTipoObject = :valore2 AND oggetto.cdKeyObject = :valore3 AND sessione_ingest.idSessioneIngest = :valore4 AND oggetto.tiStatoObject = :valore5 AND (UPPER(info_dicom.COLInTest) IN (:valore6))",
		objects[0].toString().trim());
	assertEquals(
		"SELECT oggetto.idObject, oggetto.cdKeyObject, sessione_ingest.tiStato, sessione_ingest.dtApertura, sessione_ingest.dtChiusura, sessione_ingest.cdErr, sessione_ingest.dlErr, sessione_ingest.idSessioneIngest, oggetto.tiStatoObject, sessione_ingest.flForzaAccettazione, sessione_ingest.dlMotivoForzaAccettazione, sessione_ingest.flForzaWarning, sessione_ingest.dlMotivoChiusoWarning FROM PigObject oggetto JOIN oggetto.pigSessioneIngests sessione_ingest JOIN oggetto.pigInfoDicoms info_dicom WHERE oggetto.idObject = sessione_ingest.pigObject.idObject AND sessione_ingest.nmAmbienteVers = :valore0 AND sessione_ingest.nmVers = :valore1 AND oggetto.pigTipoObject.nmTipoObject = :valore2 AND oggetto.cdKeyObject = :valore3 AND sessione_ingest.idSessioneIngest = :valore4 AND oggetto.tiStatoObject = :valore5 AND (UPPER(info_dicom.COLInTest) IN (:valore6)) ORDER BY info_dicom.COLNameOrder DESC, sessione_ingest.dtApertura DESC",
		objects[1].toString().trim());
    }
}
