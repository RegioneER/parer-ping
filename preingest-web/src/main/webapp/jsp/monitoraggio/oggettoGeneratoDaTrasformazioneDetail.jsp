<%@ page import="it.eng.sacerasi.slite.gen.form.MonitoraggioForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>
<c:set scope="request" var="navTable" value="${(empty param.mainNavTable) ? fn:escapeXml(param.table) : fn:escapeXml(param.mainNavTable)  }" />
<sl:html>
    <sl:head  title="" >
    </sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia versatore"/>
        <sl:menu showChangePasswordBtn="true" />
        <sl:content>
            <slf:messageBox  />
            <sl:newLine skipLine="true"/>
            <sl:contentTitle title="<%= MonitoraggioForm.OggettoGeneratoTrasfDetail.DESCRIPTION%>"/>
            <c:choose>
                <c:when test='<%= request.getAttribute("navTable").equals(MonitoraggioForm.OggettoDetailOggettiTrasfList.NAME)%>'>
                    <slf:listNavBarDetail name="<%= MonitoraggioForm.OggettoDetailOggettiTrasfList.NAME%>" hideOperationButton="true"/>
                </c:when>
                <c:otherwise>
                    <slf:fieldBarDetailTag name="<%= MonitoraggioForm.OggettoGeneratoTrasfDetail.NAME%>" hideOperationButton="true"/>
                </c:otherwise>
            </c:choose>
            <sl:newLine skipLine="true"/>
            <slf:section name="<%=MonitoraggioForm.OggettoDaTrasformareSection.NAME%>" styleClass="importantContainer">
                <slf:lblField name="<%=MonitoraggioForm.OggettoGeneratoTrasfDetail.NM_AMBIENTE_VERS_DA_TRASF%>" colSpan="4"/>
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.OggettoGeneratoTrasfDetail.NM_VERS_DA_TRASF%>" colSpan="4"/>
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.OggettoGeneratoTrasfDetail.CD_KEY_OBJECT_DA_TRASF%>" colSpan="4"/>
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.OggettoGeneratoTrasfDetail.NM_TIPO_OBJECT_DA_TRASF%>" colSpan="4"/>
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.OggettoGeneratoTrasfDetail.DS_OBJECT_DA_TRASF%>" colSpan="4"/>
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.OggettoGeneratoTrasfDetail.NI_TOT_OBJECT_TRASF%>" colSpan="4"/>
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.OggettoGeneratoTrasfDetail.TI_STATO_OBJECT_DA_TRASF%>" colSpan="4"/>
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.OggettoGeneratoTrasfDetail.DT_STATO_COR_DA_TRASF%>" colSpan="4"/>
            </slf:section>
            <sl:newLine skipLine="true"/>
            <slf:section name="<%=MonitoraggioForm.OggettoTrasformatoSection.NAME%>" styleClass="importantContainer">
                <slf:lblField name="<%=MonitoraggioForm.OggettoGeneratoTrasfDetail.NM_AMBIENTE_VERS_TRASF%>" colSpan="4"/>
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.OggettoGeneratoTrasfDetail.NM_VERS_TRASF%>" colSpan="4"/>
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.OggettoGeneratoTrasfDetail.PG_OGGETTO_TRASF%>" colSpan="4"/>
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.OggettoGeneratoTrasfDetail.CD_KEY_OBJECT_TRASF%>" colSpan="4"/>
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.OggettoGeneratoTrasfDetail.NM_TIPO_OBJECT_TRASF%>" colSpan="4"/>
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.OggettoGeneratoTrasfDetail.DS_OBJECT_TRASF%>" colSpan="4"/>
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.OggettoGeneratoTrasfDetail.DS_PATH%>" colSpan="4"/>
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.OggettoGeneratoTrasfDetail.DS_HASH_FILE_VERS%>" colSpan="4"/>
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.OggettoGeneratoTrasfDetail.TI_ALGO_HASH_FILE_VERS%>" colSpan="4"/>
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.OggettoGeneratoTrasfDetail.CD_ENCODING_HASH_FILE_VERS%>" colSpan="4"/>
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.OggettoGeneratoTrasfDetail.TI_STATO_TRASF%>" colSpan="4"/>
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.OggettoGeneratoTrasfDetail.CD_ERR%>" colSpan="4"/>
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.OggettoGeneratoTrasfDetail.DL_ERR%>" colSpan="4"/>
            </slf:section>
            <sl:pulsantiera>
                <slf:lblField name="<%=MonitoraggioForm.OggettoGeneratoTrasfDetail.DOWNLOAD_FILE_OGGETTO_OBJ_GEN_TRASF%>" width="w20"/>
                <slf:lblField name="<%=MonitoraggioForm.OggettoGeneratoTrasfDetail.OGGETTO_DA_TRASFORMARE_OBJ_GEN_TRASF%>" width="w20"/>
                <slf:lblField name="<%=MonitoraggioForm.OggettoGeneratoTrasfDetail.OGGETTO_VERSATO_PING_OBJ_GEN_TRASF%>" width="w20"/>
            </sl:pulsantiera>
        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>
