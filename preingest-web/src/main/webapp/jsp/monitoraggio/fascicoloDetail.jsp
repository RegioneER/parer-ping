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

            <sl:contentTitle title="DETTAGLIO FASCICOLO"/>
            <c:choose>
                <c:when test="${(form.oggettoDetail.ti_stato_object.value eq 'CHIUSO_ERR_VERS')}">
                    <slf:fieldBarDetailTag name="<%= MonitoraggioForm.FascicoloDetail.NAME%>" hideInsertButton="true"/>
                </c:when>
                <c:otherwise>
                    <slf:listNavBarDetail name="<%= MonitoraggioForm.OggettoDetailFascicoliList.NAME%>"/>
                </c:otherwise>
            </c:choose>
            <sl:newLine skipLine="true"/>
            <slf:fieldSet  borderHidden="false">
                <slf:lblField name="<%=MonitoraggioForm.FascicoloDetail.VERSATORE%>" colSpan="4"/>
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.FascicoloDetail.CD_KEY_OBJECT%>" colSpan="4" />
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.FascicoloDetail.NM_TIPO_OBJECT%>" colSpan="4"/>
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.FascicoloDetail.DS_INFO_OBJECT%>" colSpan="4"/>
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.FascicoloDetail.TI_STATO_OBJECT%>" colSpan="4"/>
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.FascicoloDetail.DL_COMPOSITO_ORGANIZ%>" colSpan="4"/>
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.FascicoloDetail.CHIAVE_FASCICOLO%>" colSpan="4"/>
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.FascicoloDetail.NI_SIZE_FILE_BYTE%>" colSpan="4"/>
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.FascicoloDetail.TI_STATO_FASCICOLO_OBJECT%>" colSpan="4"/>
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.FascicoloDetail.CD_ERR_SACER%>" colSpan="2"/>
                <slf:lblField name="<%=MonitoraggioForm.FascicoloDetail.DL_ERR_SACER%>" colSpan="2"/>
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.FascicoloDetail.FL_VERS_SIMULATO%>" colSpan="4"/>
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.FascicoloDetail.BL_XML_VERS_SACER%>" colSpan="4" controlWidth="w100"/>
            </slf:fieldSet>

            <sl:newLine skipLine="true" />

            <sl:pulsantiera>
                <slf:lblField name="<%=MonitoraggioForm.FascicoloDetail.DOWNLOAD_XMLFASCICOLO_OBJECT%>" width="w50" controlWidth="w30" labelWidth="w40"/>
            </sl:pulsantiera>

        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>
