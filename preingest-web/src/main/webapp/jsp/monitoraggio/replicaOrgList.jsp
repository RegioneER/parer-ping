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

<%@ page import="it.eng.sacerasi.slite.gen.form.MonitoraggioForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head  title="Ricerca repliche organizzazioni" >
    </sl:head>
    <sl:body>
        <sl:header showChangeOrganizationBtn="false" />
        <sl:menu showChangePasswordBtn="true" />
        <sl:content>
            <slf:messageBox  />
            <sl:newLine skipLine="true"/>

            <sl:contentTitle title="RICERCA REPLICHE ORGANIZZAZIONI "/>
            <slf:fieldBarDetailTag name="<%= MonitoraggioForm.FiltriReplicaOrg.NAME%>" hideDeleteButton="true" hideUpdateButton="true"/>
            <sl:newLine skipLine="true"/>

            <slf:fieldSet  borderHidden="false">
                <!-- piazzo i campi del filtro di ricerca -->
                <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioForm.FiltriReplicaOrg.ID_AMBIENTE_VERS%>" colSpan="1" controlWidth="w70" />                
                    <slf:lblField name="<%=MonitoraggioForm.FiltriReplicaOrg.ID_VERS%>" colSpan="1" controlWidth="w70"  />
                    <slf:lblField name="<%=MonitoraggioForm.FiltriReplicaOrg.TI_OPER_REPLIC%>" colSpan="1" controlWidth="w70"  />
                    <slf:lblField name="<%=MonitoraggioForm.FiltriReplicaOrg.TI_STATO_REPLIC%>" colSpan="1" controlWidth="w70"  />
                <sl:newLine />
            </slf:fieldSet>
            <sl:newLine skipLine="true" />

            <sl:pulsantiera>
                <!-- piazzo il bottone -->
                <slf:lblField name="<%=MonitoraggioForm.FiltriReplicaOrg.RICERCA_REPLICHE%>" width="w25" />
                <slf:lblField name="<%=MonitoraggioForm.FiltriReplicaOrg.PULISCI_REPLICHE%>" width="w25" />
            </sl:pulsantiera>
            <sl:newLine skipLine="true"/>

            <!--  piazzo la lista con i risultati -->
            <slf:list   name="<%= MonitoraggioForm.ReplicaOrgList.NAME%>" />
            <slf:listNavBar  name="<%= MonitoraggioForm.ReplicaOrgList.NAME%>" />

        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>
