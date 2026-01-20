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
    <sl:head  title="Monitoraggio - Dettaglio unità documentaria" >
<script src="<c:url value='/js/help/inithighlightingjs.js' />" type="text/javascript"></script>    
    </sl:head>
    <sl:body>
        <c:set value="${sessionScope['###_FORM_CONTAINER']}" var="form" />

        <sl:header changeOrganizationBtnDescription="Cambia versatore"/>
        <sl:menu showChangePasswordBtn="true" />
        <sl:content>
            <slf:messageBox  />
            <sl:newLine skipLine="true"/>

            <sl:contentTitle title="DETTAGLIO UNITA' DOCUMENTARIA"/>
            <c:choose>
                <c:when test="${(form.oggettoDetail.ti_stato_object.value eq 'CHIUSO_ERR_VERS')}">
                    <slf:fieldBarDetailTag name="<%= MonitoraggioForm.UnitaDocDetail.NAME%>" hideInsertButton="true"/>
                </c:when>
                <c:otherwise>
                    <slf:listNavBarDetail name="<%= MonitoraggioForm.OggettoDetailUnitaDocList.NAME%>"/>
                </c:otherwise>
            </c:choose>
            <sl:newLine skipLine="true"/>
            <slf:fieldSet  borderHidden="false">
                <slf:lblField name="<%=MonitoraggioForm.UnitaDocDetail.VERSATORE%>" colSpan="4"/>
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.UnitaDocDetail.CD_KEY_OBJECT%>" colSpan="4" />
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.UnitaDocDetail.NM_TIPO_OBJECT%>" colSpan="4"/>
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.UnitaDocDetail.DS_INFO_OBJECT%>" colSpan="4"/>
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.UnitaDocDetail.TI_STATO_OBJECT%>" colSpan="4"/>
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.UnitaDocDetail.DL_COMPOSITO_ORGANIZ%>" colSpan="4"/>
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.UnitaDocDetail.CHIAVE_UD%>" colSpan="4"/>
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.UnitaDocDetail.NI_SIZE_FILE_BYTE%>" colSpan="4"/>
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.UnitaDocDetail.TI_STATO_UNITA_DOC_OBJECT%>" colSpan="4"/>
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.UnitaDocDetail.DT_STATO%>" colSpan="4"/>
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.UnitaDocDetail.CD_ERR_SACER%>" colSpan="2"/>
                <slf:lblField name="<%=MonitoraggioForm.UnitaDocDetail.DL_ERR_SACER%>" colSpan="2"/>
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.UnitaDocDetail.FL_VERS_SIMULATO%>" colSpan="4"/>
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.UnitaDocDetail.FL_XML_MODIFICATO %>" colSpan="4"/>
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.UnitaDocDetail.BL_XML_VERS_SACER%>" colSpan="4" controlWidth="w100"/>
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.UnitaDocDetail.BL_XML_INDICE_SACER%>" colSpan="4" controlWidth="w100"/>
            </slf:fieldSet>

            <sl:newLine skipLine="true" />

            <sl:pulsantiera>
                <slf:lblField name="<%=MonitoraggioForm.UnitaDocDetail.DOWNLOAD_XMLUNITA_DOC_OBJECT%>" width="w50" controlWidth="w30" labelWidth="w40"/>
                <slf:lblField name="<%=MonitoraggioForm.UnitaDocDetail.EDIT_UNITA_DOCUMENTARIA%>" width="w50" controlWidth="w30" labelWidth="w40"/>
                <slf:lblField name="<%=MonitoraggioForm.UnitaDocDetail.UNDO_UNITA_DOCUMENTARIA%>" width="w50" controlWidth="w30" labelWidth="w40"/>
                <slf:lblField name="<%=MonitoraggioForm.UnitaDocDetail.SAVE_UNITA_DOCUMENTARIA%>" width="w50" controlWidth="w30" labelWidth="w40"/>
            </sl:pulsantiera>

        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>
