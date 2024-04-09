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