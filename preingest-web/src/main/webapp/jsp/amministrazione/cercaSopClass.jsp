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

<%@ page import="it.eng.sacerasi.slite.gen.form.AmministrazioneForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head title="Ricerca Sop Class" />

    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia versatore"/>
        <sl:menu showChangePasswordBtn="true" />

        <sl:content>
            <slf:messageBox /> 
            <sl:contentTitle title="Ricerca Sop Class"/>
            
            <sl:newLine skipLine="true"/>
            <slf:section name="<%=AmministrazioneForm.SopClassDicom.NAME %>" styleClass="importantContainer">  
                <slf:fieldSet>
                    <slf:lblField name="<%=AmministrazioneForm.VisSopClass.CD_SOP_CLASS_DICOM%>" colSpan="4" controlWidth="w40" />
                    <slf:lblField name="<%=AmministrazioneForm.VisSopClass.DS_SOP_CLASS_DICOM%>" colSpan="4" controlWidth="w40" />
                </slf:fieldSet>
            </slf:section>
            <sl:pulsantiera>
                
                <slf:lblField  name="<%=AmministrazioneForm.VisSopClass.VIS_SOP_CLASS_BUTTON%>" colSpan="4" />
                
            </sl:pulsantiera>

            <sl:newLine skipLine="true"/>
            <!--  piazzo la lista con i risultati -->
            <slf:list   name="<%= AmministrazioneForm.SopClassList.NAME %>" />
            <slf:listNavBar  name="<%= AmministrazioneForm.SopClassList.NAME %>" />


        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>
