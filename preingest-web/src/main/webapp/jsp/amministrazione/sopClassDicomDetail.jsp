<%@ page import="it.eng.sacerasi.slite.gen.form.AmministrazioneForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head title="Dettaglio Sop Class" />

    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia versatore"/>
        <sl:menu showChangePasswordBtn="true" />

        <sl:content>
            <slf:messageBox /> 
            <sl:contentTitle title="Dettaglio Sop Class"/>
            
            <c:if test="${sessionScope['###_FORM_CONTAINER']['sopClassList'].table['empty']}">
                <slf:fieldBarDetailTag name="<%=AmministrazioneForm.SopClass.NAME%>" hideBackButton="true"/> 
            </c:if>   

            <c:if test="${!(sessionScope['###_FORM_CONTAINER']['sopClassList'].table['empty']) }">
                <slf:listNavBarDetail name="<%= AmministrazioneForm.SopClassList.NAME%>" />  
            </c:if>
            
            <sl:newLine skipLine="true"/>
            
            <slf:fieldSet>
                <slf:section name="<%=AmministrazioneForm.SopClassDicom.NAME%>" styleClass="importantContainer">  
                    <slf:lblField name="<%=AmministrazioneForm.SopClass.CD_SOP_CLASS_DICOM%>" colSpan="4" controlWidth="w40" />
                    <sl:newLine />
                    <slf:lblField name="<%=AmministrazioneForm.SopClass.DS_SOP_CLASS_DICOM%>" colSpan="4" controlWidth="w40" />
                    <sl:newLine />
                    
                </slf:section>
                
            </slf:fieldSet>
            
  <%--          <c:if test="${(sessionScope['###_FORM_CONTAINER']['sopClass'].status eq 'view') }">

                <div class="livello1"><b>Elenco versatori</b></div>
                <sl:newLine skipLine="true"/>

                <sl:newLine skipLine="true"/>
                <!--  piazzo la lista con i risultati -->
                <slf:list  name="<%= AmministrazioneForm.VersList.NAME%>"  />
                <slf:listNavBar  name="<%= AmministrazioneForm.VersList.NAME%>" />


            </c:if> --%>

        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>