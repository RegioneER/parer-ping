<%@ page import="it.eng.sacerasi.slite.gen.form.AmministrazioneForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head title="Dettaglio Tipo Oggetto" />

    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia versatore"/>
        <sl:menu showChangePasswordBtn="true" />

        <sl:content>
            <slf:messageBox /> 
            <sl:contentTitle title="Ricerca Tipo Oggetto "/>

            
                <slf:listNavBarDetail name="<%= AmministrazioneForm.AttribDatiSpecList.NAME%>" />  
         
            <sl:newLine skipLine="true"/>

            <slf:fieldSet>
                <slf:section name="<%=AmministrazioneForm.AttribDati.NAME%>" styleClass="importantContainer">  
                    <slf:lblField name="<%=AmministrazioneForm.AttribDatiSpec.NM_ATTRIB_DATI_SPEC%>" colSpan="4" controlWidth="w40" />
                    <sl:newLine />
                    <slf:lblField name="<%=AmministrazioneForm.AttribDatiSpec.NI_ORD%>" colSpan="4" controlWidth="w40" />
                    <sl:newLine />
                    <slf:lblField name="<%=AmministrazioneForm.AttribDatiSpec.NM_COL_DATI_SPEC%>" colSpan="4" controlWidth="w40" />
                    <sl:newLine />
                    <slf:lblField name="<%=AmministrazioneForm.AttribDatiSpec.CD_DATATYPE_XSD%>" colSpan="4" controlWidth="w40" />
                    <sl:newLine />
                    <slf:lblField name="<%=AmministrazioneForm.AttribDatiSpec.TI_DATATYPE_COL%>" colSpan="4" controlWidth="w40" />
                    <sl:newLine />
                    <slf:lblField name="<%=AmministrazioneForm.AttribDatiSpec.FL_FILTRO_DIARIO%>" colSpan="4" controlWidth="w40" />
                    <sl:newLine />
                    <slf:lblField name="<%=AmministrazioneForm.AttribDatiSpec.FL_VERS_SACER%>" colSpan="4" controlWidth="w40" />
                    <sl:newLine />
                </slf:section>
                <sl:newLine skipLine="true"/>


            </slf:fieldSet>

        
        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>