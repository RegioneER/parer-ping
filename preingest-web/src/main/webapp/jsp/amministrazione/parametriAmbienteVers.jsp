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

<%@ page import="it.eng.sacerasi.slite.gen.form.AmministrazioneForm"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head title="Dettaglio parametri ambiente versatore" >
    </sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia versatore" description="Ambiente versatore - Parametri ambiente versatore"/>
        <sl:menu />
        <sl:content>
            <slf:messageBox /> 
            <sl:contentTitle title="Dettaglio parametri ambiente versatore"/>
            <slf:fieldBarDetailTag name="<%=AmministrazioneForm.AmbienteVers.NAME%>" hideBackButton="true" />
            <slf:section name="<%=AmministrazioneForm.NomeAmbienteSection.NAME%>" styleClass="importantContainer">  
                <slf:lblField name="<%=AmministrazioneForm.AmbienteVers.NM_AMBIENTE_VERS%>"  colSpan= "2" labelWidth="w20" controlWidth="w70" />
            </slf:section>
            <sl:newLine skipLine="true"/>
            <sl:pulsantiera>
                <slf:lblField name="<%=AmministrazioneForm.RicercaParametriAmbiente.FUNZIONE%>" colSpan="2"/>                              
                <slf:lblField name="<%=AmministrazioneForm.RicercaParametriAmbiente.RICERCA_PARAMETRI_AMBIENTE_BUTTON%>" colSpan="2"/>                              
            </sl:pulsantiera>                     
            <sl:newLine skipLine="true"/>
            <slf:fieldSet >
                <slf:section name="<%=AmministrazioneForm.ParametriAmministrazioneAmbienteSection.NAME%>" styleClass="noborder w100">
                    <slf:editableList name="<%= AmministrazioneForm.ParametriAmministrazioneAmbienteList.NAME%>" multiRowEdit="true"/>
                    <slf:listNavBar  name="<%= AmministrazioneForm.ParametriAmministrazioneAmbienteList.NAME%>" />
                </slf:section>
                <slf:section name="<%=AmministrazioneForm.ParametriConservazioneAmbienteSection.NAME%>" styleClass="noborder w100">
                    <slf:editableList name="<%= AmministrazioneForm.ParametriConservazioneAmbienteList.NAME%>" multiRowEdit="true"/>
                    <slf:listNavBar  name="<%= AmministrazioneForm.ParametriConservazioneAmbienteList.NAME%>" />
                </slf:section>
                <slf:section name="<%=AmministrazioneForm.ParametriGestioneAmbienteSection.NAME%>" styleClass="noborder w100">
                    <slf:editableList name="<%= AmministrazioneForm.ParametriGestioneAmbienteList.NAME%>" multiRowEdit="true"/>
                    <slf:listNavBar  name="<%= AmministrazioneForm.ParametriGestioneAmbienteList.NAME%>" />
                </slf:section>
            </slf:fieldSet>
        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>

