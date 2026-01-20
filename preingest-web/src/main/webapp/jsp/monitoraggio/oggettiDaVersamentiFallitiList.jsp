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
    <sl:head  title="Monitoraggio - Riepilogo oggetti derivanti da versamenti falliti" >        
    </sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia versatore"/>
        <sl:menu showChangePasswordBtn="true" />
        <sl:content>
            <slf:messageBox  />

            <sl:newLine skipLine="true"/>

            <sl:contentTitle title="RIEPILOGO OGGETTI DERIVANTI DA VERSAMENTI FALLITI"/>
            <slf:fieldBarDetailTag name="<%= MonitoraggioForm.FiltriRiepilogoVersamenti.NAME%>" hideOperationButton="true"/>

            <slf:fieldSet  borderHidden="false">
                <!-- piazzo i campi del filtro di ricerca -->
                <slf:lblField name="<%=MonitoraggioForm.FiltriOggettiDerVersFalliti.ID_AMBIENTE_VERS%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.FiltriOggettiDerVersFalliti.ID_VERS%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.FiltriOggettiDerVersFalliti.ID_TIPO_OBJECT%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.FiltriOggettiDerVersFalliti.OGGETTI_DER_FL_VERIF%>" colSpan="4" />
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.FiltriOggettiDerVersFalliti.OGGETTI_DER_FL_NON_RISOLUB%>" colSpan="4" />
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.FiltriOggettiDerVersFalliti.OGGETTI_DER_FL_VERS_SACER_DA_RECUP%>" colSpan="4" />
            </slf:fieldSet>

            <sl:newLine skipLine="true" />

            <sl:pulsantiera>
                <slf:lblField name="<%=MonitoraggioForm.OggettiDerVersFallitiButtonList.RICERCA_OGGETTI_DA_VERSAMENTI_FALLITI%>" colSpan="2" />
            </sl:pulsantiera>

            <sl:newLine skipLine="true" />

            <!--  piazzo la lista con i risultati -->
            <slf:list   name="<%= MonitoraggioForm.OggettiDaVersamentiFallitiList.NAME%>" />
            <slf:listNavBar  name="<%= MonitoraggioForm.OggettiDaVersamentiFallitiList.NAME%>" />

            <sl:newLine skipLine="true" />

        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>
