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
    <sl:head  title="Monitoraggio - Dettaglio sessione errata" >
    	<script src="<c:url value='/js/help/inithighlightingjs.js' />" type="text/javascript"></script> 
    </sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia versatore"/>
        <sl:menu showChangePasswordBtn="true" />
        <sl:content>
            <slf:messageBox  />
     
            <sl:contentTitle title="Dettaglio sessione errata"/>
            <sl:newLine skipLine="true"/>
            <%--<slf:fieldBarDetailTag name="<%= MonitoraggioForm.SessioniErrateDetail.NAME%>" />  --%>
            <slf:listNavBarDetail name="<%= MonitoraggioForm.SessioniErrateList.NAME%>" />
            <sl:newLine skipLine="true"/>
            <slf:fieldSet  borderHidden="false">

                <slf:lblField name="<%=MonitoraggioForm.SessioniErrateDetail.ID_SESSIONE_INGEST%>" colSpan="1"/>
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.SessioniErrateDetail.DT_APERTURA%>" colSpan="2"/>
                <slf:lblField name="<%=MonitoraggioForm.SessioniErrateDetail.DT_CHIUSURA%>" colSpan="2"/>
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.SessioniErrateDetail.VERSATORE%>" colSpan="4"/>
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.SessioniErrateDetail.NM_TIPO_OBJECT%>" colSpan="1"/>
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.SessioniErrateDetail.CD_KEY_OBJECT%>" colSpan="4"/>
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.SessioniErrateDetail.INFO_OGG%>" colSpan="4"/>
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.SessioniErrateDetail.TI_STATO%>" colSpan="1"/>
                <sl:newLine />  
                <slf:lblField name="<%=MonitoraggioForm.SessioniErrateDetail.CD_ERR%>" colSpan="2"/>
                <sl:newLine />  
                <slf:lblField name="<%=MonitoraggioForm.SessioniErrateDetail.DL_ERR%>" colSpan="4"/>
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.SessioniErrateDetail.FL_VERIF%>" colSpan="1"/>
                <slf:lblField name="<%=MonitoraggioForm.SessioniErrateDetail.FL_FORZA_WARNING%>" colSpan="1"/>
                <slf:lblField name="<%=MonitoraggioForm.SessioniErrateDetail.FL_FORZA_ACCETTAZIONE%>" colSpan="1"/>
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.SessioniErrateDetail.DL_MOTIVO_FORZA_ACCETTAZIONE%>" colSpan="1"/>
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.SessioniErrateDetail.DL_MOTIVO_CHIUSO_WARNING%>" colSpan="1"/>
                    
                <sl:newLine skipLine="true"/>
                
                <slf:lblField name="<%=MonitoraggioForm.SessioniErrateDetail.CD_VERSIONE_XML_VERS%>" colSpan="1"/><sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.SessioniErrateDetail.BL_XML%>" colSpan="4" controlWidth="w100"/>

            </slf:fieldSet>
            
            <slf:lblField name="<%=MonitoraggioForm.SessioniErrateDetail.DOWNLOAD_XML_SESSIONE %>" colSpan="1"/>
            
        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>
