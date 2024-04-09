<%@ page import="it.eng.sacerasi.slite.gen.form.AmministrazioneForm"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head title="Dettaglio parametri tipo oggetto" >
    </sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia versatore" description="Tipo oggetto - Parametri tipo oggetto"/>
        <sl:menu />
        <sl:content>
            <slf:messageBox /> 
            <sl:contentTitle title="Dettaglio parametri tipo oggetto"/>
            <slf:fieldBarDetailTag name="<%=AmministrazioneForm.TipoObject.NAME%>" hideBackButton="true" />
            <slf:section name="<%=AmministrazioneForm.NomeTipoOggettoSection.NAME%>" styleClass="importantContainer">  
                <slf:lblField name="<%=AmministrazioneForm.TipoObject.NM_AMBIENTE_VERS%>"  colSpan= "2" labelWidth="w20" controlWidth="w70" />
                <sl:newLine />
                <slf:lblField name="<%=AmministrazioneForm.TipoObject.NM_VERS%>"  colSpan= "2" labelWidth="w20" controlWidth="w70" />
                <sl:newLine />
                <slf:lblField name="<%=AmministrazioneForm.TipoObject.NM_TIPO_OBJECT%>"  colSpan= "2" labelWidth="w20" controlWidth="w70" />
            </slf:section>
            <sl:newLine skipLine="true"/>
            <sl:pulsantiera>
                <slf:lblField name="<%=AmministrazioneForm.RicercaParametriTipoOggetto.FUNZIONE%>" colSpan="2"/>                              
                <slf:lblField name="<%=AmministrazioneForm.RicercaParametriTipoOggetto.RICERCA_PARAMETRI_TIPO_OGGETTO_BUTTON%>" colSpan="2"/>                              
            </sl:pulsantiera>                     
            <sl:newLine skipLine="true"/>
            <slf:fieldSet >
                <slf:section name="<%=AmministrazioneForm.ParametriAmministrazioneTipoOggettoSection.NAME%>" styleClass="noborder w100">
                    <slf:editableList name="<%= AmministrazioneForm.ParametriAmministrazioneTipoOggettoList.NAME%>" multiRowEdit="true"/>
                    <slf:listNavBar  name="<%= AmministrazioneForm.ParametriAmministrazioneTipoOggettoList.NAME%>" />
                </slf:section>
                <slf:section name="<%=AmministrazioneForm.ParametriConservazioneTipoOggettoSection.NAME%>" styleClass="noborder w100">
                    <slf:editableList name="<%= AmministrazioneForm.ParametriConservazioneTipoOggettoList.NAME%>" multiRowEdit="true"/>
                    <slf:listNavBar  name="<%= AmministrazioneForm.ParametriConservazioneTipoOggettoList.NAME%>" />
                </slf:section>
                <slf:section name="<%=AmministrazioneForm.ParametriGestioneTipoOggettoSection.NAME%>" styleClass="noborder w100">
                    <slf:editableList name="<%= AmministrazioneForm.ParametriGestioneTipoOggettoList.NAME%>" multiRowEdit="true"/>
                    <slf:listNavBar  name="<%= AmministrazioneForm.ParametriGestioneTipoOggettoList.NAME%>" />
                </slf:section>
            </slf:fieldSet>
        </sl:content>
        <sl:footer />
    </sl:body>

</sl:html>

