<%@page import="it.eng.spagoCore.configuration.ConfigSingleton"%>
<%@ page import="it.eng.sacerasi.slite.gen.form.SceltaOrganizzazioneForm" pageEncoding="UTF-8"%>
<%@ include file="../include.jsp"%>

<sl:html>
    <sl:head  title="Scelta versatore" />
    <sl:body>
        <sl:header showHomeBtn="false" description=""/>
        <div class="toolBar">

            <h2 class="floatLeft">Scelta versatore</h2>
            <div class="right"> <h2><a title="Logout" href="Logout.html">
                        <img title="Logout" alt="Logout" src="<c:url value='/img/base/IconaLogout.png' />" style="padding-right: 5px;">Logout</a></h2></div>
        </div>

        <sl:content> 
            <slf:messageBox  />
            <sl:newLine skipLine="true"/>

            <div class="center">
                <div class="floatLeft" >
                    <sl:contentTitle title="SCEGLI VERSATORE"/>
                </div>
                <sl:newLine skipLine="true" />                
                <slf:fieldSet  borderHidden="true" >
                    <slf:lblField colSpan="4" width="w80" labelWidth="w20" name="<%= SceltaOrganizzazioneForm.Versatori.NM_AMBIENTE_VERS%>" />
                    <sl:newLine skipLine="true"/>
                    <slf:lblField colSpan="4" width="w80" labelWidth="w20" name="<%= SceltaOrganizzazioneForm.Versatori.NM_VERS%>" />
                    <sl:newLine skipLine="true"/>
                    <slf:lblField position="left" width="auto" name="<%= SceltaOrganizzazioneForm.Versatori.SELEZIONA_VERSATORE%>"/>
                </slf:fieldSet>
            </div>
        </sl:content>
        <!--Footer-->
        <sl:footer />
    </sl:body>
</sl:html>
