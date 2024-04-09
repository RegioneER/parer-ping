<%@ page import="it.eng.sacerasi.slite.gen.form.AmministrazioneForm"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head title="Dettaglio parametri ambiente versatore" >
    </sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia versatore" description="Ambiente versatore - Parametri ambiente versatore"/>
        <sl:menu />
        <sl:content>
            <slf:messageBox /> 
            <sl:contentTitle title="Dettaglio parametri ambiente versatore"/>
            <slf:fieldBarDetailTag name="<%=AmministrazioneForm.AmbienteVers.NAME%>" hideBackButton="true" />
            <slf:section name="<%=AmministrazioneForm.NomeAmbienteSection.NAME%>" styleClass="importantContainer">  
                <slf:lblField name="<%=AmministrazioneForm.AmbienteVers.NM_AMBIENTE_VERS%>"  colSpan= "2" labelWidth="w20" controlWidth="w70" />
            </slf:section>
            <sl:newLine skipLine="true"/>
            <sl:pulsantiera>
                <slf:lblField name="<%=AmministrazioneForm.RicercaParametriAmbiente.FUNZIONE%>" colSpan="2"/>                              
                <slf:lblField name="<%=AmministrazioneForm.RicercaParametriAmbiente.RICERCA_PARAMETRI_AMBIENTE_BUTTON%>" colSpan="2"/>                              
            </sl:pulsantiera>                     
            <sl:newLine skipLine="true"/>
            <slf:fieldSet >
                <slf:section name="<%=AmministrazioneForm.ParametriAmministrazioneAmbienteSection.NAME%>" styleClass="noborder w100">
                    <slf:editableList name="<%= AmministrazioneForm.ParametriAmministrazioneAmbienteList.NAME%>" multiRowEdit="true"/>
                    <slf:listNavBar  name="<%= AmministrazioneForm.ParametriAmministrazioneAmbienteList.NAME%>" />
                </slf:section>
                <slf:section name="<%=AmministrazioneForm.ParametriConservazioneAmbienteSection.NAME%>" styleClass="noborder w100">
                    <slf:editableList name="<%= AmministrazioneForm.ParametriConservazioneAmbienteList.NAME%>" multiRowEdit="true"/>
                    <slf:listNavBar  name="<%= AmministrazioneForm.ParametriConservazioneAmbienteList.NAME%>" />
                </slf:section>
                <slf:section name="<%=AmministrazioneForm.ParametriGestioneAmbienteSection.NAME%>" styleClass="noborder w100">
                    <slf:editableList name="<%= AmministrazioneForm.ParametriGestioneAmbienteList.NAME%>" multiRowEdit="true"/>
                    <slf:listNavBar  name="<%= AmministrazioneForm.ParametriGestioneAmbienteList.NAME%>" />
                </slf:section>
            </slf:fieldSet>
        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>

