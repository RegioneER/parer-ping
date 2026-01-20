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

<%@ page import="it.eng.sacerasi.slite.gen.form.TrasformazioniForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head  title="<%= TrasformazioniForm.ParametersSetDetail.DESCRIPTION%>" ></sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia versatore"/>
        <sl:menu showChangePasswordBtn="true" />
        <sl:content>
            <slf:messageBox />
            <sl:contentTitle title="<%=TrasformazioniForm.ParametersSetSection.DESCRIPTION%>"/>
            <sl:newLine skipLine="true"/>

            <slf:fieldBarDetailTag name="<%=TrasformazioniForm.ParametersSetDetail.NAME%>" hideBackButton="false" hideDeleteButton="false" hideUpdateButton="false" />

            <sl:newLine />

            <slf:fieldSet borderHidden="false">
                <slf:lblField name="<%=TrasformazioniForm.ParametersSetDetail.NM_SET_PARAM_TRASF%>" colSpan="2" controlWidth="w100" labelWidth="w20"/>
                <sl:newLine />
                <slf:lblField name="<%=TrasformazioniForm.ParametersSetDetail.DS_SET_PARAM_TRASF%>" colSpan="2" controlWidth="w100" labelWidth="w20"/>
                <sl:newLine />
                <slf:lblField name="<%=TrasformazioniForm.ParametersSetDetail.FL_SET_PARAM_ARK%>" colSpan="2" controlWidth="w100" labelWidth="w20"/>
                <sl:newLine />
                <slf:lblField name="<%=TrasformazioniForm.ParametersSetDetail.NM_XFO_TRASF%>" colSpan="2" controlWidth="w100" labelWidth="w20"/>
                <sl:newLine />
            </slf:fieldSet>
            <sl:pulsantiera>
                <slf:lblField name="<%=TrasformazioniForm.ParametersSetDetail.UPDATE_PARAMETERS_SETS_FROM_SET_DETAIL%>" />
            </sl:pulsantiera>

            <c:if test="${(sessionScope['###_FORM_CONTAINER']['parametersSetDetail'].status eq 'view') && requestScope.hideInsertParameter ne true}">
                <slf:fieldSet  borderHidden="false">
                    <sl:contentTitle title="<%=TrasformazioniForm.InserimentoParametro.DESCRIPTION%>"/>
                    <slf:lblField name="<%=TrasformazioniForm.InserimentoParametro.NM_PARAM_TRASF%>" colSpan="2" controlWidth="w100" labelWidth="w20"/>
                    <sl:newLine />
                    <slf:lblField name="<%=TrasformazioniForm.InserimentoParametro.DS_PARAM_TRASF%>" colSpan="2" controlWidth="w100" labelWidth="w20"/>
                    <sl:newLine />
                    <slf:lblField name="<%=TrasformazioniForm.InserimentoParametro.TI_PARAM_TRASF%>" colSpan="2" controlWidth="w100" labelWidth="w20"/>
                    <sl:newLine />
                    <slf:lblField name="<%=TrasformazioniForm.InserimentoParametro.DS_VALORE_TRASF%>" colSpan="2" controlWidth="w100" labelWidth="w20"/>
                    <sl:newLine />
                </slf:fieldSet>
                <sl:pulsantiera>
                    <slf:lblField name="<%=TrasformazioniForm.ParametersSetDetail.ADD_PARAMETER%>" />
                </sl:pulsantiera>
            </c:if>
            <sl:newLine />

            <c:if test="${(sessionScope['###_FORM_CONTAINER']['parametersSetDetail'].status eq 'view') }">
                <!--  lista con i parametri-->
                <slf:listNavBar name="<%= TrasformazioniForm.ParametersList.NAME%>" pageSizeRelated="true"/>
                <slf:list name="<%= TrasformazioniForm.ParametersList.NAME%>" />
                <slf:listNavBar  name="<%= TrasformazioniForm.ParametersList.NAME%>" />
            </c:if>
        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>
