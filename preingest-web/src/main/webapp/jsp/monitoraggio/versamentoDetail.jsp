<%@ page import="it.eng.sacerasi.slite.gen.form.MonitoraggioForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>
<!-- Setto l'attributo navTable che mi servirà per gestire il tasto "indietro" a seconda della mia provenienza
     quando dovrò scorrere la FileList in fondo pagina-->
<c:set scope="request" var="navTable" value="${(empty param.mainNavTable) ? fn:escapeXml(param.table) : fn:escapeXml(param.mainNavTable)  }" />
<sl:html>
    <sl:head  title="Monitoraggio - Dettaglio versamento" > 
    	<script src="<c:url value='/js/help/inithighlightingjs.js' />" type="text/javascript"></script> 
    </sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia versatore"/>
        <sl:menu showChangePasswordBtn="true" />
        <sl:content>
            <slf:messageBox  />
            <sl:newLine skipLine="true"/>

            <sl:contentTitle title="DETTAGLIO VERSAMENTO"/>
            <c:choose>
                <c:when test='<%= request.getAttribute("navTable").equals(MonitoraggioForm.VersamentiList.NAME)%>'>
                    <slf:listNavBarDetail name="<%= MonitoraggioForm.VersamentiList.NAME%>"/>
                </c:when>
                <c:when test='<%= request.getAttribute("navTable").equals(MonitoraggioForm.OggettoDaVersamentiFallitiDetailVersamentiList.NAME)%>'>
                    <slf:listNavBarDetail name="<%= MonitoraggioForm.OggettoDaVersamentiFallitiDetailVersamentiList.NAME%>" />
                </c:when>
                <c:when test='<%= request.getAttribute("navTable").equals(MonitoraggioForm.OggettoDetailSessioniList.NAME)%>'>
                    <slf:listNavBarDetail name="<%= MonitoraggioForm.OggettoDetailSessioniList.NAME%>" />
                </c:when>
                <c:otherwise>
                    <slf:fieldBarDetailTag name="<%= MonitoraggioForm.OggettoDetail.NAME%>" hideOperationButton="true"/>
                </c:otherwise>
            </c:choose>

            <slf:tab name="<%= MonitoraggioForm.VersamentoTabs.NAME%>" tabElement="DettaglioVersamento">
                <slf:fieldSet  borderHidden="false">
                    <slf:lblField name="<%=MonitoraggioForm.VersamentoDetail.ID_SESSIONE_INGEST%>" colSpan="4"/>
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioForm.VersamentoDetail.DT_APERTURA%>" colSpan="4"/>
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioForm.VersamentoDetail.DT_CHIUSURA%>" colSpan="4"/>
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioForm.VersamentoDetail.VERSATORE%>" colSpan="4" />
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioForm.VersamentoDetail.CD_KEY_OBJECT%>" colSpan="4"/>
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioForm.VersamentoDetail.INFO%>" colSpan="4"/>
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioForm.VersamentoDetail.CD_VERS_GEN%>" colSpan="4"/>
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioForm.VersamentoDetail.CD_TRASF%>" colSpan="4"/>
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioForm.VersamentoDetail.CD_VERSIONE_TRASF%>" colSpan="4"/>
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioForm.VersamentoDetail.TI_GEST_OGGETTI_FIGLI%>" colSpan="4"/>
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioForm.VersamentoDetail.TI_STATO%>" colSpan="2"/>
                    <slf:lblField name="<%=MonitoraggioForm.VersamentoDetail.DT_STATO_COR%>" colSpan="2"/>
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioForm.VersamentoDetail.TI_STATO_VERIFICA_HASH%>" colSpan="4"/>
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioForm.VersamentoDetail.ERRORE%>" colSpan="4"/>
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioForm.VersamentoDetail.TI_STATO_RISOLUZ%>" colSpan="4"/>
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioForm.VersamentoDetail.FL_VERIF%>" colSpan="1"/>
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioForm.VersamentoDetail.FL_NON_RISOLUB%>" colSpan="1"/>
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioForm.VersamentoDetail.FL_FORZA_WARNING%>" colSpan="4"/>
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioForm.VersamentoDetail.FL_FORZA_ACCETTAZIONE%>" colSpan="2"/>
                    <slf:lblField name="<%=MonitoraggioForm.VersamentoDetail.DL_MOTIVO_FORZA_ACCETTAZIONE%>" colSpan="2"/>
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioForm.VersamentoDetail.DL_MOTIVO_CHIUSO_WARNING%>" colSpan="4"/>
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioForm.VersamentoDetail.WARNING%>" colSpan="4"/>
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioForm.VersamentoDetail.NI_UNITA_DOC_ATTESE%>" colSpan="2"/>
                    <slf:lblField name="<%=MonitoraggioForm.VersamentoDetail.NI_UNITA_DOC_DA_VERS%>" colSpan="1"/>
                    <slf:lblField name="<%=MonitoraggioForm.VersamentoDetail.NI_UNITA_DOC_VERS%>" colSpan="1"/>
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioForm.VersamentoDetail.NI_UNITA_DOC_VERS__OK%>" colSpan="2"/>
                    <slf:lblField name="<%=MonitoraggioForm.VersamentoDetail.NI_UNITA_DOC_VERS_ERR%>" colSpan="1"/>
                    <slf:lblField name="<%=MonitoraggioForm.VersamentoDetail.NI_UNITA_DOC_VERS_TIMEOUT%>" colSpan="1"/>
                    <slf:lblField name="<%=MonitoraggioForm.VersamentoDetail.PG_OGGETTO_TRASF%>" colSpan="2"/>
                    <sl:newLine skipLine="true"/>
                    <slf:lblField name="<%=MonitoraggioForm.VersamentoDetail.NOTE%>" colSpan="4" controlWidth="w80" />
                    <sl:newLine skipLine="true"/>
                    <slf:section name="<%=MonitoraggioForm.OggettoDaTrasformareSection.NAME%>" styleClass="importantContainer">
                        <slf:lblField name="<%=MonitoraggioForm.VersamentoDetail.NM_AMBIENTE_VERS_PADRE%>" colSpan="4"/>
                        <sl:newLine />
                        <slf:lblField name="<%=MonitoraggioForm.VersamentoDetail.NM_VERS_PADRE%>" colSpan="4"/>
                        <sl:newLine />
                        <slf:lblField name="<%=MonitoraggioForm.VersamentoDetail.CD_KEY_OBJECT_PADRE%>" colSpan="4"/>
                        <sl:newLine />
                        <slf:lblField name="<%=MonitoraggioForm.VersamentoDetail.NI_TOT_OBJECT_TRASF%>" colSpan="4"/>
                    </slf:section>
                </slf:fieldSet>

                <sl:newLine skipLine="true" />
                <sl:pulsantiera>
                    <slf:lblField name="<%=MonitoraggioForm.VersamentoDetail.DETTAGLIO_OGGETTO%>" width="w50" controlWidth="w30" labelWidth="w40"/>
                </sl:pulsantiera>

            </slf:tab>
            <slf:tab name="<%= MonitoraggioForm.VersamentoTabs.NAME%>" tabElement="VersamentoXMLVersato">
                <slf:fieldSet  borderHidden="false">
                    <slf:lblField name="<%=MonitoraggioForm.VersamentoDetail.CD_VERSIONE_XML_VERS%>" colSpan="4" />
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioForm.VersamentoDetail.BL_XML%>" colSpan="4" controlWidth="w100"/>
                </slf:fieldSet>
                <sl:newLine skipLine="true" />

                <sl:pulsantiera>
                    <slf:lblField name="<%=MonitoraggioForm.VersamentoDetail.DOWNLOAD_XMLVERSAMENTO%>" width="w50" controlWidth="w30" labelWidth="w40"/>
                    <slf:lblField name="<%=MonitoraggioForm.VersamentoDetail.DETTAGLIO_OGGETTO%>" width="w50" controlWidth="w30" labelWidth="w40"/>
                </sl:pulsantiera>
            </slf:tab>
            <slf:tab name="<%= MonitoraggioForm.VersamentoTabs.NAME%>" tabElement="<%= MonitoraggioForm.VersamentoTabs.filtri_unita_doc_vers%>">
                <slf:fieldSet  borderHidden="false">
                    <slf:lblField name="<%=MonitoraggioForm.FiltriUnitaDocVers.NM_AMBIENTE%>" colSpan="2"/><sl:newLine />              
                    <slf:lblField name="<%=MonitoraggioForm.FiltriUnitaDocVers.NM_ENTE%>" colSpan="2"/><sl:newLine />              
                    <slf:lblField name="<%=MonitoraggioForm.FiltriUnitaDocVers.NM_STRUT%>" colSpan="2"/><sl:newLine />              
                    <slf:lblField name="<%=MonitoraggioForm.FiltriUnitaDocVers.FL_STRUTTURA_NON_DEFINITA%>" colSpan="2"/><sl:newLine />              
                    <slf:lblField name="<%=MonitoraggioForm.FiltriUnitaDocVers.CD_REGISTRO_UNITA_DOC_SACER%>" colSpan="2"/><sl:newLine />              
                    <slf:lblField name="<%=MonitoraggioForm.FiltriUnitaDocVers.AA_UNITA_DOC_SACER%>" colSpan="2" /><sl:newLine />
                    <slf:lblField name="<%=MonitoraggioForm.FiltriUnitaDocVers.CD_KEY_UNITA_DOC_SACER%>" colSpan="2" /><sl:newLine />
                    <slf:lblField name="<%=MonitoraggioForm.FiltriUnitaDocVers.TI_STATO_UNITA_DOC_OBJECT%>" colSpan="2" /><sl:newLine />
                    <slf:lblField name="<%=MonitoraggioForm.FiltriUnitaDocVers.CD_CONCAT_DL_ERR_SACER%>" colSpan="2" /><sl:newLine />
                    <slf:lblField name="<%=MonitoraggioForm.FiltriUnitaDocVers.FL_VERS_SIMULATO%>" colSpan="2" />
                    <sl:pulsantiera>
                        <slf:lblField name="<%=MonitoraggioForm.VersamentiButtonList.FILTRA_UNITA_DOC_VERS%>"/>
                    </sl:pulsantiera>
                </slf:fieldSet>
            </slf:tab>
            <slf:tab name="<%= MonitoraggioForm.VersamentoTabs.NAME%>" tabElement="<%= MonitoraggioForm.VersamentoTabs.report_trasformazione_per_versamneto %>">
                <slf:lblField name="<%=MonitoraggioForm.VersamentoDetail.REPORT_XML%>" colSpan="4" controlWidth="w100"/>
            </slf:tab>
            <sl:newLine skipLine="true"/>

            <slf:tab name="<%= MonitoraggioForm.VersamentoSubTabs.NAME%>" tabElement="<%= MonitoraggioForm.VersamentoSubTabs.unita_doc_list_tab%>">
                <slf:list   name="<%= MonitoraggioForm.UnitaDocDaVersamentiFallitiList.NAME%>" />
                <slf:listNavBar  name="<%= MonitoraggioForm.UnitaDocDaVersamentiFallitiList.NAME%>" />
            </slf:tab>
            <slf:tab name="<%= MonitoraggioForm.VersamentoSubTabs.NAME%>" tabElement="<%= MonitoraggioForm.VersamentoSubTabs.stati_list_tab%>">
                <slf:list   name="<%= MonitoraggioForm.StatiVersamentiList.NAME%>" />
                <slf:listNavBar  name="<%= MonitoraggioForm.StatiVersamentiList.NAME%>" />
            </slf:tab>

            <!-- Risetto il parametro table -->        
            <div>
                <input name="table" type="hidden" value="${fn:escapeXml(param.table)}"/>
            </div>

            <!-- Mantengo il valore di mainNavTable quando navigo tra i tab -->
            <div><input name="mainNavTable" type="hidden" value="${(empty param.mainNavTable) ? fn:escapeXml(param.table) : fn:escapeXml(param.mainNavTable)  }" /></div>

        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>
