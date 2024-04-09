<%@ page import="it.eng.sacerasi.slite.gen.form.AmministrazioneForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head title="Ricerca Sop Class" />

    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia versatore"/>
        <sl:menu showChangePasswordBtn="true" />

        <sl:content>
            <slf:messageBox /> 
            <sl:contentTitle title="Ricerca Sop Class"/>
            
            <sl:newLine skipLine="true"/>
            <slf:section name="<%=AmministrazioneForm.SopClassDicom.NAME %>" styleClass="importantContainer">  
                <slf:fieldSet>
                    <slf:lblField name="<%=AmministrazioneForm.VisSopClass.CD_SOP_CLASS_DICOM%>" colSpan="4" controlWidth="w40" />
                    <slf:lblField name="<%=AmministrazioneForm.VisSopClass.DS_SOP_CLASS_DICOM%>" colSpan="4" controlWidth="w40" />
                </slf:fieldSet>
            </slf:section>
            <sl:pulsantiera>
                
                <slf:lblField  name="<%=AmministrazioneForm.VisSopClass.VIS_SOP_CLASS_BUTTON%>" colSpan="4" />
                
            </sl:pulsantiera>

            <sl:newLine skipLine="true"/>
            <!--  piazzo la lista con i risultati -->
            <slf:list   name="<%= AmministrazioneForm.SopClassList.NAME %>" />
            <slf:listNavBar  name="<%= AmministrazioneForm.SopClassList.NAME %>" />


        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>