<%@ page import="it.eng.sacerasi.slite.gen.form.AmministrazioneForm"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head title="Dettaglio parametri versatore" >
    </sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia versatore" description="Versatori - Parametri versatore"/>
        <sl:menu />
        <sl:content>
            <slf:messageBox /> 
            <sl:contentTitle title="Dettaglio parametri versatore"/>
            <slf:fieldBarDetailTag name="<%=AmministrazioneForm.Vers.NAME%>" hideBackButton="true" />
            <slf:section name="<%=AmministrazioneForm.NomeVersatoreSection.NAME%>" styleClass="importantContainer">  
                <slf:lblField name="<%=AmministrazioneForm.Vers.NM_AMBIENTE_VERS%>"  colSpan= "2" labelWidth="w20" controlWidth="w70" />
                <sl:newLine />
                <slf:lblField name="<%=AmministrazioneForm.Vers.NM_VERS%>"  colSpan= "2" labelWidth="w20" controlWidth="w70" />
            </slf:section>
            <sl:newLine skipLine="true"/>
            <sl:pulsantiera>
                <slf:lblField name="<%=AmministrazioneForm.RicercaParametriVersatore.FUNZIONE%>" colSpan="2"/>                              
                <slf:lblField name="<%=AmministrazioneForm.RicercaParametriVersatore.RICERCA_PARAMETRI_VERSATORE_BUTTON%>" colSpan="2"/>                              
            </sl:pulsantiera>                     
            <sl:newLine skipLine="true"/>
            <slf:fieldSet >
                <slf:section name="<%=AmministrazioneForm.ParametriAmministrazioneVersatoreSection.NAME%>" styleClass="noborder w100">
                    <slf:editableList name="<%= AmministrazioneForm.ParametriAmministrazioneVersatoreList.NAME%>" multiRowEdit="true"/>
                    <slf:listNavBar  name="<%= AmministrazioneForm.ParametriAmministrazioneVersatoreList.NAME%>" />
                </slf:section>
                <slf:section name="<%=AmministrazioneForm.ParametriConservazioneVersatoreSection.NAME%>" styleClass="noborder w100">
                    <slf:editableList name="<%= AmministrazioneForm.ParametriConservazioneVersatoreList.NAME%>" multiRowEdit="true"/>
                    <slf:listNavBar  name="<%= AmministrazioneForm.ParametriConservazioneVersatoreList.NAME%>" />
                </slf:section>
                <slf:section name="<%=AmministrazioneForm.ParametriGestioneVersatoreSection.NAME%>" styleClass="noborder w100">
                    <slf:editableList name="<%= AmministrazioneForm.ParametriGestioneVersatoreList.NAME%>" multiRowEdit="true"/>
                    <slf:listNavBar  name="<%= AmministrazioneForm.ParametriGestioneVersatoreList.NAME%>" />
                </slf:section>
            </slf:fieldSet>
        </sl:content>
        <sl:footer />
    </sl:body>

</sl:html>

