<%@ page import="it.eng.sacerasi.slite.gen.form.AmministrazioneForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head title="Ricerca Ambiente" />

    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia versatore"/>
        <sl:menu showChangePasswordBtn="true" />

        <sl:content>
            <slf:messageBox /> 
            <sl:contentTitle title="Ricerca Ambiente "/>
            
            <sl:newLine skipLine="true"/>
            <slf:section name="<%=AmministrazioneForm.GestioneAmbienti.NAME%>" >  
                <slf:fieldSet>
                    <slf:lblField name="<%=AmministrazioneForm.VisAmbienteVers.NM_AMBIENTE_VERS%>" colSpan="4" controlWidth="w40" />

                </slf:fieldSet>
            </slf:section>
            <sl:pulsantiera>
                
                <slf:lblField  name="<%=AmministrazioneForm.VisAmbienteVers.VIS_AMBIENTE_BUTTON%>" colSpan="4" />
                
            </sl:pulsantiera>

            <sl:newLine skipLine="true"/>
            <!--  piazzo la lista con i risultati -->
            <slf:list   name="<%= AmministrazioneForm.AmbienteVersList.NAME %>" />
            <slf:listNavBar  name="<%= AmministrazioneForm.AmbienteVersList.NAME %>" />


        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>