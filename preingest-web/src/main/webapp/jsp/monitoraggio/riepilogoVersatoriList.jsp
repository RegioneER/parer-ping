<%@ page import="it.eng.sacerasi.slite.gen.form.MonitoraggioForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head  title="Riepilogo per versatore" >
    </sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia versatore"/>
        <sl:menu showChangePasswordBtn="true" />
        <sl:content>
            <slf:messageBox  />
            <sl:newLine skipLine="true"/>

            <sl:contentTitle title="RIEPILOGO PER VERSATORE"/>

            <sl:newLine skipLine="true"/>
            
            <!--  piazzo la lista con i risultati -->
            <slf:list   name="<%= MonitoraggioForm.RiepilogoVersatoriList.NAME%>" />
            <slf:listNavBar  name="<%= MonitoraggioForm.RiepilogoVersatoriList.NAME%>" />

        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>