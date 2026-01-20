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

<%@page import="it.eng.sacerasi.slite.gen.form.TrasformazioniForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head  title="<%= TrasformazioniForm.FiltriRicercaTrasformazioni.DESCRIPTION %>" ></sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia versatore"/>
        <sl:menu showChangePasswordBtn="true" />
        <sl:content>
            <slf:messageBox />
            <sl:contentTitle title="<%=TrasformazioniForm.RicercaTrasformazioneSection.DESCRIPTION %>"/>
            <sl:newLine skipLine="true"/>
            <slf:fieldSet borderHidden="false">
                <slf:lblField name="<%=TrasformazioniForm.FiltriRicercaTrasformazioni.CD_TRASF_SEARCH %>" width="w100" controlWidth="w30" labelWidth="w20" />
                <sl:newLine />
                <slf:lblField name="<%=TrasformazioniForm.FiltriRicercaTrasformazioni.DS_TRANSF_SEARCH %>" width="w100" controlWidth="w30" labelWidth="w20" />
                <sl:newLine />
                <slf:lblField name="<%=TrasformazioniForm.FiltriRicercaTrasformazioni.CD_VERSIONE_COR_SEARCH %>" width="w100" controlWidth="w30" labelWidth="w20" />
                <sl:newLine />
                <slf:lblField name="<%=TrasformazioniForm.FiltriRicercaTrasformazioni.DS_VERSIONE_COR_SEARCH %>" width="w100" controlWidth="w30" labelWidth="w20" />
                <sl:newLine />
                <slf:lblField name="<%=TrasformazioniForm.FiltriRicercaTrasformazioni.FL_ATTIVA_SEARCH %>" width="w100" controlWidth="w30" labelWidth="w20" />
                <sl:newLine />
                <slf:lblField name="<%=TrasformazioniForm.FiltriRicercaTrasformazioni.DT_ISTITUZ_SEARCH %>" width="w50" controlWidth="w20" labelWidth="w40" />
                <slf:lblField name="<%=TrasformazioniForm.FiltriRicercaTrasformazioni.DT_SOPPRES_SEARCH %>" width="w50" controlWidth="w20" labelWidth="w40" />
                <sl:newLine />
            </slf:fieldSet>                
            <sl:newLine skipLine="true"/>
            <sl:pulsantiera>
                <slf:lblField  name="<%=TrasformazioniForm.FiltriRicercaTrasformazioni.RICERCA_TRASFORMAZIONE %>"  width="w50" />
            </sl:pulsantiera> 
            <sl:newLine skipLine="true"/>
            
            <!--  lista con i risultati -->
            <slf:listNavBar name="<%= TrasformazioniForm.TrasformazioniList.NAME %>" pageSizeRelated="true"/>
            <slf:list name="<%= TrasformazioniForm.TrasformazioniList.NAME%>" />
            <slf:listNavBar  name="<%= TrasformazioniForm.TrasformazioniList.NAME%>" />

        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>
