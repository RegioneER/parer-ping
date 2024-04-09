<%@page import="it.eng.sacerasi.slite.gen.form.TrasformazioniForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head  title="<%= TrasformazioniForm.MonitoraggioServerTrasformazioniSection.DESCRIPTION%>" ></sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia versatore"/>
        <sl:menu showChangePasswordBtn="true" />
        <sl:content>
            <slf:messageBox />
            
            <slf:fieldBarDetailTag name="<%=TrasformazioniForm.MonitoraggioServerTrasformazioniDetail.NAME%>" hideBackButton="true" />
            <sl:contentTitle title="<%=TrasformazioniForm.MonitoraggioServerTrasformazioniSection.DESCRIPTION%>"/>

             <slf:fieldSet borderHidden="false">
                <slf:lblField name="<%=TrasformazioniForm.MonitoraggioServerTrasformazioniDetail.FL_SET_SERVER%>" width="w100" controlWidth="w30" labelWidth="w20"/><sl:newLine />
             </slf:fieldSet>
            
            <sl:newLine skipLine="true"/>
            <sl:pulsantiera>
                <slf:lblField  name="<%=TrasformazioniForm.MonitoraggioServerTrasformazioniDetail.UPDATE_KETTLE_SERVERS_STATUS %>"  width="w50" />
            </sl:pulsantiera> 
            <sl:newLine skipLine="true"/>
            
            <sl:contentTitle title="<%=TrasformazioniForm.MonitoraggioServerTrasformazioniInCorsoSection.DESCRIPTION%>"/>
            <!--  lista con i risultati -->
            <slf:listNavBar name="<%= TrasformazioniForm.StatoTrasformazioniInCorsoList.NAME %>" pageSizeRelated="true"/>
            <slf:list name="<%= TrasformazioniForm.StatoTrasformazioniInCorsoList.NAME%>" />
            <slf:listNavBar  name="<%= TrasformazioniForm.StatoTrasformazioniInCorsoList.NAME%>" />
            
            <sl:newLine skipLine="true"/>
            
            <sl:contentTitle title="<%=TrasformazioniForm.MonitoraggioServerTrasformazioniInCodaSection.DESCRIPTION%>"/>
            <slf:listNavBar name="<%= TrasformazioniForm.StatoTrasformazioniInCodaList.NAME %>" pageSizeRelated="true"/>
            <slf:list name="<%= TrasformazioniForm.StatoTrasformazioniInCodaList.NAME%>" />
            <slf:listNavBar  name="<%= TrasformazioniForm.StatoTrasformazioniInCodaList.NAME%>" />
            
            <sl:newLine skipLine="true"/>
            
            <sl:contentTitle title="<%=TrasformazioniForm.MonitoraggioServerStoricoTrasformazioniSection.DESCRIPTION%>"/>
            <slf:listNavBar name="<%= TrasformazioniForm.StatoTrasformazioniStoricoList.NAME %>" pageSizeRelated="true"/>
            <slf:list name="<%= TrasformazioniForm.StatoTrasformazioniStoricoList.NAME%>" />
            <slf:listNavBar  name="<%= TrasformazioniForm.StatoTrasformazioniStoricoList.NAME%>" />
            
        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>
