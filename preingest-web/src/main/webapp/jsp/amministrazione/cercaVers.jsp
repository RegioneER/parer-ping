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
    <sl:head title="Ricerca Versatore" />

    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia versatore"/>
        <sl:menu showChangePasswordBtn="true" />

        <sl:content>
            <slf:messageBox /> 
            <sl:contentTitle title="Ricerca Versatore "/>

            <sl:newLine skipLine="true"/>

            <slf:fieldSet>
                <slf:section name="<%=AmministrazioneForm.VersatoriPingRic.NAME%>" styleClass="importantContainer">     
                    <slf:lblField name="<%=AmministrazioneForm.VisVers.NM_VERS%>" colSpan="4" controlWidth="w40" />
                    <sl:newLine />
                    <slf:lblField name="<%=AmministrazioneForm.VisVers.NM_AMBIENTE_VERS%>" colSpan="2" controlWidth="w40" />
                    <slf:lblField name="<%=AmministrazioneForm.VisVers.NM_TIPO_VERSATORE%>" colSpan="2" controlWidth="w40" />
                    <sl:newLine />
                    <slf:lblField name="<%=AmministrazioneForm.VisVers.ID_VERS%>" colSpan="4" controlWidth="w40" />
                </slf:section>
                <slf:section name="<%=AmministrazioneForm.CorrispondenzaSacer.NAME%>" styleClass="importantContainer"  >  
                    <slf:lblField name="<%=AmministrazioneForm.VisVers.NM_AMBIENTE_SACER%>" colSpan="2" controlWidth="w40" />
                    <sl:newLine />
                    <slf:lblField name="<%=AmministrazioneForm.VisVers.NM_ENTE_SACER%>" colSpan="2" controlWidth="w40" />
                    <sl:newLine />
                    <slf:lblField name="<%=AmministrazioneForm.VisVers.NM_STRUT_SACER%>" colSpan="2" controlWidth="w40" />
                    <sl:newLine />
                    <slf:lblField name="<%=AmministrazioneForm.VisVers.NM_USERID_SACER%>" colSpan="2" controlWidth="w40" />
                    <sl:newLine />
                </slf:section>
                <slf:section name="<%=AmministrazioneForm.EnteSiamSection.NAME%>" styleClass="importantContainer"  >
                    <slf:lblField name="<%=AmministrazioneForm.VisVers.NM_AMBIENTE_ENTE_CONVENZ%>" colSpan="2" controlWidth="w40" />
                    <sl:newLine />
                    <slf:lblField name="<%=AmministrazioneForm.VisVers.NM_ENTE_CONVENZ%>" colSpan="2" controlWidth="w40" />
                    <sl:newLine />
                </slf:section>
                
            </slf:fieldSet>

            <sl:pulsantiera>

                <slf:lblField  name="<%=AmministrazioneForm.VisVers.VIS_VERS_BUTTON%>" colSpan="2" />
                <slf:lblField  name="<%=AmministrazioneForm.VisVers.IMPORTA_VERSATORE_BUTTON%>" colSpan="2" />
                <%-- <slf:lblField  name="<%=AmministrazioneForm.Vers.IMPORTA_XML_VERS_BUTTON%>" colSpan="2" /> --%>

            </sl:pulsantiera>

            <sl:newLine skipLine="true"/>
            <!--  piazzo la lista con i risultati -->
            <c:if test="${not empty sessionScope['###_FORM_CONTAINER']['versList']}">
                <c:set target="${sessionScope['###_FORM_CONTAINER']['versList']['dupVersatore']}" property="hidden" value="false" />
            </c:if>   
            <slf:list   name="<%= AmministrazioneForm.VersList.NAME%>" />
            <slf:listNavBar  name="<%= AmministrazioneForm.VersList.NAME%>" />

        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>
