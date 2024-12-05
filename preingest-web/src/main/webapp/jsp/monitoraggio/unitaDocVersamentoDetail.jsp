<%@ page import="it.eng.sacerasi.slite.gen.form.MonitoraggioForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head title="Monitoraggio - Dettaglio unità documentaria versamento" >  
   		 <script src="<c:url value='/js/help/inithighlightingjs.js' />" type="text/javascript"></script> 
    </sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia versatore"/>
        <sl:menu showChangePasswordBtn="true" />
        <sl:content>
            <slf:messageBox  />
            <sl:newLine skipLine="true"/>

            <sl:contentTitle title="DETTAGLIO UNITA' DOCUMENTARIA VERSAMENTO"/>
            <slf:listNavBarDetail name="<%= MonitoraggioForm.UnitaDocDaVersamentiFallitiList.NAME%>"/>
            <sl:newLine skipLine="true"/>
            <slf:fieldSet  borderHidden="false">
                <slf:lblField name="<%=MonitoraggioForm.UnitaDocVersamentoDetail.ID_UNITA_DOC_SESSIONE%>" colSpan="4" />
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.UnitaDocVersamentoDetail.ID_SESSIONE_INGEST%>" colSpan="4" />
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.UnitaDocVersamentoDetail.DT_APERTURA%>" colSpan="4"/>
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.UnitaDocVersamentoDetail.DT_CHIUSURA%>" colSpan="4"/>
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.UnitaDocVersamentoDetail.VERSATORE%>" colSpan="4"/>
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.UnitaDocVersamentoDetail.CD_KEY_OBJECT%>" colSpan="4" />
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.UnitaDocVersamentoDetail.DS_OBJECT%>" colSpan="4"/>
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.UnitaDocVersamentoDetail.DL_COMPOSITO_ORGANIZ%>" colSpan="4"/>
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.UnitaDocVersamentoDetail.CHIAVE_UD%>" colSpan="4"/>
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.UnitaDocVersamentoDetail.NI_SIZE_FILE_BYTE%>" colSpan="4"/>
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.UnitaDocVersamentoDetail.TI_STATO_UNITA_DOC_OBJECT%>" colSpan="4"/>
                <sl:newLine />
                 <slf:lblField name="<%=MonitoraggioForm.UnitaDocVersamentoDetail.DT_STATO%>" colSpan="4"/>
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.UnitaDocVersamentoDetail.CD_ERR_SACER%>" colSpan="2"/>
                <slf:lblField name="<%=MonitoraggioForm.UnitaDocVersamentoDetail.DL_ERR_SACER%>" colSpan="2"/>
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.UnitaDocVersamentoDetail.BL_XML_VERS_SACER%>" colSpan="4" controlWidth="w100"/>
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.UnitaDocVersamentoDetail.BL_XML_INDICE_SACER%>" colSpan="4" controlWidth="w100"/>
            </slf:fieldSet>
            <sl:newLine skipLine="true" />
            <sl:pulsantiera>
                <slf:lblField name="<%=MonitoraggioForm.UnitaDocVersamentoDetail.DOWNLOAD_XMLUNITA_DOC_VERS%>" width="w50" controlWidth="w30" labelWidth="w40"/>
            </sl:pulsantiera>
        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>
