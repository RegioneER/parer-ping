<%@ page import="it.eng.sacerasi.slite.gen.form.AmministrazioneForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head title="<%=AmministrazioneForm.CorrispondenzaSacerVersatoreDetail.DESCRIPTION%>" >
    </sl:head>

    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia versatore"/>
        <sl:menu showChangePasswordBtn="true" />

        <sl:content>
            <slf:messageBox /> 
            <sl:contentTitle title="<%=AmministrazioneForm.CorrispondenzaSacerVersatoreDetail.DESCRIPTION%>" />

            <c:if test="${sessionScope['###_FORM_CONTAINER']['corrispondenzeSacerVersatoreList'].table['empty']}">
                <slf:fieldBarDetailTag name="<%=AmministrazioneForm.CorrispondenzaSacerVersatoreDetail.NAME%>" hideBackButton="true"/> 
            </c:if>   

            <c:if test="${!(sessionScope['###_FORM_CONTAINER']['corrispondenzeSacerVersatoreList'].table['empty']) }">
                <slf:listNavBarDetail name="<%= AmministrazioneForm.CorrispondenzeSacerVersatoreList.NAME%>" />  
            </c:if>

            <sl:newLine skipLine="true"/>

            <slf:fieldSet>
                <slf:lblField name="<%=AmministrazioneForm.Vers.NM_VERS%>" colSpan="2" controlWidth="w40" />
                <sl:newLine />
                <slf:lblField name="<%=AmministrazioneForm.Vers.DS_VERS%>" colSpan="2" controlWidth="w40" />
                <sl:newLine />
                <slf:lblField name="<%=AmministrazioneForm.Vers.ID_AMBIENTE_VERS%>" colSpan="2" controlWidth="w40" />
                <sl:newLine />
                <slf:lblField name="<%=AmministrazioneForm.CorrispondenzaSacerVersatoreDetail.ID_DICH_VERS_SACER%>" colSpan="2" controlWidth="w40" />
                <sl:newLine />
                <slf:lblField name="<%=AmministrazioneForm.CorrispondenzaSacerVersatoreDetail.TI_DICH_VERS%>" colSpan="2" controlWidth="w40" />
                <sl:newLine />
                <slf:lblField name="<%=AmministrazioneForm.CorrispondenzaSacerVersatoreDetail.ID_ORGANIZ_IAM%>" colSpan="2" controlWidth="w40" />
                <sl:newLine />
                <slf:lblField name="<%=AmministrazioneForm.CorrispondenzaSacerVersatoreDetail.NM_USERID_SACER%>" colSpan="2" controlWidth="w40" />
                <sl:newLine />
                <slf:lblField name="<%=AmministrazioneForm.CorrispondenzaSacerVersatoreDetail.CD_PASSWORD_SACER%>" colSpan="2" controlWidth="w40" />
            </slf:fieldSet>

        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>
