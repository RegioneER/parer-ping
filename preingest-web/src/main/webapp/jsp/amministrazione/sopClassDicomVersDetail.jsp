<%@ page import="it.eng.sacerasi.slite.gen.form.AmministrazioneForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head title="Associazione Sop Class - Versatore" />

    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia versatore"/>
        <sl:menu showChangePasswordBtn="true" />

        <sl:content>
            <slf:messageBox /> 
            <sl:contentTitle title="Associazione Sop Class - Versatore"/>
            <slf:fieldBarDetailTag name="<%= AmministrazioneForm.SopClassVers.NAME%>" /> 
            <sl:newLine skipLine="true"/>
            
            
            <slf:fieldSet>
                <slf:section name="<%=AmministrazioneForm.VersatoriPing.NAME%>" styleClass="importantContainer">  
                    <slf:lblField name="<%=AmministrazioneForm.Vers.NM_VERS%>" colSpan="4" controlWidth="w40" />
                    <sl:newLine />
                    <slf:lblField name="<%=AmministrazioneForm.Vers.DS_VERS%>" colSpan="4" controlWidth="w40" />
                    <sl:newLine />
                    <slf:lblField name="<%=AmministrazioneForm.Vers.ID_AMBIENTE_VERS%>" colSpan="4" controlWidth="w40" />
                    <<sl:newLine />
                    <slf:lblField name="<%=AmministrazioneForm.Vers.DS_PATH_INPUT_FTP%>" colSpan="4" controlWidth="w40" />
                    <sl:newLine />
                    <slf:lblField name="<%=AmministrazioneForm.Vers.DS_PATH_OUTPUT_FTP%>" colSpan="4" controlWidth="w40" />
                    <sl:newLine />
                </slf:section>
            </slf:fieldSet>
            <sl:newLine skipLine="true"/>
             <sl:pulsantiera>
                <slf:buttonList name="<%=AmministrazioneForm.ButtonAllList.NAME%>" />
            </sl:pulsantiera>
            <sl:newLine skipLine="true"/>
            
            <div class="livello1"><b>Sop Class Disponibili</b></div>
            <slf:selectList name="<%=AmministrazioneForm.SopClassDispList.NAME%>" addList="true" />
            <slf:listNavBar name="<%=AmministrazioneForm.SopClassDispList.NAME%>" />
            <sl:newLine skipLine="true"/>
            <sl:newLine skipLine="true"/>
            <div class="livello1"><b>Sop Class Associate</b></div>
            <slf:selectList name="<%=AmministrazioneForm.SopClassToVersList.NAME%>" addList="false" />
            <slf:listNavBar name="<%=AmministrazioneForm.SopClassToVersList.NAME%>" />

        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>