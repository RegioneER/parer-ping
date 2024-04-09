<%@ page import="it.eng.sacerasi.slite.gen.form.AmministrazioneForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head title="Descrizione degli stati dell’oggetto versato" />
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia versatore"/>
        <sl:menu showChangePasswordBtn="true" />
        <sl:content>
            <slf:messageBox /> 
            <sl:contentTitle title="Descrizione degli stati dell’oggetto versato"/>
            <sl:newLine skipLine="true"/>
            <slf:list   name="<%= AmministrazioneForm.StatiVersamentoObjectList.NAME %>" />
            <slf:listNavBar  name="<%= AmministrazioneForm.StatiVersamentoObjectList.NAME %>" />
        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>