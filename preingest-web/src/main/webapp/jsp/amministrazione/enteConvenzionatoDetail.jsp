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

<%@ page import="it.eng.sacerasi.slite.gen.form.EntiConvenzionatiForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head title="<%= EntiConvenzionatiForm.EnteConvenzOrg.DESCRIPTION%>" >
    </sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
        <sl:content >
            <slf:messageBox />
            <sl:contentTitle title="<%= EntiConvenzionatiForm.EnteConvenzOrg.DESCRIPTION%>"/>
            <c:if test="${sessionScope['###_FORM_CONTAINER']['enteConvenzOrgList'].table['empty']}">
                <slf:fieldBarDetailTag name="<%= EntiConvenzionatiForm.EnteConvenzOrg.NAME%>" hideBackButton="${sessionScope['###_FORM_CONTAINER']['enteConvenzOrgList'].status eq 'insert'}"/> 
            </c:if>   
            <c:if test="${!(sessionScope['###_FORM_CONTAINER']['enteConvenzOrgList'].table['empty']) }">
                <slf:listNavBarDetail name="<%= EntiConvenzionatiForm.EnteConvenzOrgList.NAME%>" />  
            </c:if>
            <sl:newLine skipLine="true"/>
            <slf:fieldSet borderHidden="false">
                <slf:section name="<%=EntiConvenzionatiForm.VersatoreSection.NAME%>" styleClass="importantContainer"> 
                <slf:lblField name="<%=EntiConvenzionatiForm.VersRif.VERSATORE%>" width="w100" controlWidth="w40" labelWidth="w20"/>
                </slf:section> 
                <sl:newLine skipLine="true"/>
                <slf:section name="<%=EntiConvenzionatiForm.EnteSection.NAME%>" styleClass="importantContainer"> 
                <slf:lblField name="<%=EntiConvenzionatiForm.EnteConvenzOrg.ID_ENTE_CONVENZ_ORG%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                <slf:lblField name="<%=EntiConvenzionatiForm.EnteConvenzOrg.ID_AMBIENTE_ENTE_CONVENZ%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                <slf:lblField name="<%=EntiConvenzionatiForm.EnteConvenzOrg.ID_ENTE_CONVENZ%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                <slf:lblField name="<%=EntiConvenzionatiForm.EnteConvenzOrg.DT_INI_VAL%>" width="w100" controlWidth="w20" labelWidth="w20"/><sl:newLine />
                <slf:lblField name="<%=EntiConvenzionatiForm.EnteConvenzOrg.DT_FINE_VAL%>" width="w100" controlWidth="w20" labelWidth="w20"/><sl:newLine />
                </slf:section> 
                <sl:newLine />
            </slf:fieldSet>
        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>
