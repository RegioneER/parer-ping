<%--
 Engineering Ingegneria Informatica S.p.A.

 Copyright (C) 2023 Regione Emilia-Romagna
 <p/>
 This program is free software: you can redistribute it and/or modify it under the terms of
 the GNU Affero General Public License as published by the Free Software Foundation,
 either version 3 of the License, or (at your option) any later version.
 <p/>
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 See the GNU Affero General Public License for more details.
 <p/>
 You should have received a copy of the GNU Affero General Public License along with this program.
 If not, see <https://www.gnu.org/licenses/>.
 --%>

<%@ page import="it.eng.sacerasi.slite.gen.form.VersamentoOggettoForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head title="<%= VersamentoOggettoForm.FiltriVersamentiOggetto.DESCRIPTION%>" />
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia versatore"/>
        <sl:menu showChangePasswordBtn="true" />
        <sl:content>
            <slf:messageBox />
            <sl:contentTitle title="<%= VersamentoOggettoForm.FiltriVersamentiOggetto.DESCRIPTION%>" />
            <slf:fieldBarDetailTag name="<%=VersamentoOggettoForm.FiltriVersamentiOggetto.NAME%>" hideBackButton="${!((fn:length(sessionScope['###_NAVHIS_CONTAINER'])) gt 1 )}" />
            <slf:fieldSet borderHidden="true">
                <slf:lblField name="<%=VersamentoOggettoForm.FiltriVersamentiOggetto.NM_AMBIENTE_VERS%>" colSpan="2"/><sl:newLine />
                <slf:lblField name="<%=VersamentoOggettoForm.FiltriVersamentiOggetto.NM_VERS%>" colSpan="2"/><sl:newLine />
                <slf:lblField name="<%=VersamentoOggettoForm.FiltriVersamentiOggetto.NM_TIPO_OBJECT%>" colSpan="2"/><sl:newLine />
                <slf:lblField name="<%=VersamentoOggettoForm.FiltriVersamentiOggetto.ID_OBJECT%>" colSpan="2"/><sl:newLine />
                <slf:lblField name="<%=VersamentoOggettoForm.FiltriVersamentiOggetto.CD_KEY_OBJECT%>" colSpan="2"/><sl:newLine />
                <slf:lblField name="<%=VersamentoOggettoForm.FiltriVersamentiOggetto.DS_OBJECT%>" colSpan="4"/><sl:newLine />
                <slf:lblField name="<%=VersamentoOggettoForm.FiltriVersamentiOggetto.NOTE%>" colSpan="4"/><sl:newLine />
                <slf:lblField name="<%=VersamentoOggettoForm.FiltriVersamentiOggetto.TI_CONTENUTO_OGGETTO%>" colSpan="2"/><sl:newLine />
                <slf:lblField name="<%=VersamentoOggettoForm.FiltriVersamentiOggetto.ID_TRASFORMAZIONE%>" colSpan="2"/><sl:newLine />
                <slf:lblField name="<%=VersamentoOggettoForm.FiltriVersamentiOggetto.DT_VERS_DA%>" colSpan="2"/>
                <slf:lblField name="<%=VersamentoOggettoForm.FiltriVersamentiOggetto.DT_VERS_A%>" colSpan="2"/><sl:newLine />
                <slf:lblField name="<%=VersamentoOggettoForm.FiltriVersamentiOggetto.TI_STATO_ESTERNO%>" colSpan="2"/><sl:newLine />
                <slf:lblField name="<%=VersamentoOggettoForm.FiltriVersamentiOggetto.TI_STATO_OBJECT%>" colSpan="4"/><sl:newLine />
                <slf:lblField name="<%=VersamentoOggettoForm.FiltriVersamentiOggetto.TI_VERS_FILE%>" colSpan="4"/><sl:newLine />
                <sl:newLine skipLine="true" />
            </slf:fieldSet>
            <sl:pulsantiera>
                <slf:lblField  name="<%=VersamentoOggettoForm.FiltriVersamentiOggetto.RICERCA_VERSAMENTI_OGGETTO%>" width="w50" />
            </sl:pulsantiera>

            <slf:listNavBar name="<%=VersamentoOggettoForm.VersamentiOggettoList.NAME%>" pageSizeRelated="true"/>
            <slf:list name="<%=VersamentoOggettoForm.VersamentiOggettoList.NAME%>" abbrLongList="true"/>
            <slf:listNavBar name="<%=VersamentoOggettoForm.VersamentiOggettoList.NAME%>" /> 

        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>
