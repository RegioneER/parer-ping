<%@ page import="it.eng.sacerasi.slite.gen.form.AmministrazioneForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head title="<%=AmministrazioneForm.VersatoreGenerazioneOggettiDetail.DESCRIPTION%>" >
    </sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia versatore"/>
        <sl:menu showChangePasswordBtn="true" />
        <sl:content>
            <slf:messageBox /> 
            <sl:contentTitle title="<%=AmministrazioneForm.VersatoreGenerazioneOggettiDetail.DESCRIPTION%>" />

            <c:if test="${sessionScope['###_FORM_CONTAINER']['versatoriGenerazioneOggettiList'].table['empty']}">
                <slf:fieldBarDetailTag name="<%=AmministrazioneForm.VersatoreGenerazioneOggettiDetail.NAME%>" hideBackButton="true"/> 
            </c:if>   
            <c:if test="${!(sessionScope['###_FORM_CONTAINER']['versatoriGenerazioneOggettiList'].table['empty']) }">
                <slf:listNavBarDetail name="<%= AmministrazioneForm.VersatoriGenerazioneOggettiList.NAME%>" />  
            </c:if>
            
            <slf:lblField name="<%=AmministrazioneForm.VersatoreGenerazioneOggettiDetail.ID_VERS_TIPO_OBJECT_DA_TRASF%>" colSpan="2" controlWidth="w40" />
            
            <sl:newLine skipLine="true"/>
            <slf:section name="<%=AmministrazioneForm.TipoOggettoDaTrasfSection.NAME%>" styleClass="importantContainer">
                <slf:fieldSet>
                    <slf:lblField name="<%=AmministrazioneForm.VersatoreGenerazioneOggettiDetail.VERSATORE_TRASF%>" colSpan="2" />
                    <sl:newLine />
                    <slf:lblField name="<%=AmministrazioneForm.VersatoreGenerazioneOggettiDetail.ID_TIPO_OBJECT_DA_TRASF%>" colSpan="2" />
                    <sl:newLine />
                    <slf:lblField name="<%=AmministrazioneForm.VersatoreGenerazioneOggettiDetail.NM_TIPO_OBJECT_DA_TRASF%>" colSpan="2" />
                    <sl:newLine />
                    <slf:lblField name="<%=AmministrazioneForm.VersatoreGenerazioneOggettiDetail.CD_TRASF%>" colSpan="2" />
                </slf:fieldSet>
            </slf:section>
            <sl:newLine skipLine="true"/>
            <slf:section name="<%=AmministrazioneForm.VersatoreSection.NAME%>" styleClass="importantContainer">
                <slf:fieldSet>
                    <slf:lblField name="<%=AmministrazioneForm.VersatoreGenerazioneOggettiDetail.ID_VERS_GEN%>" colSpan="2" />
                    <sl:newLine />
                    <slf:lblField name="<%=AmministrazioneForm.VersatoreGenerazioneOggettiDetail.ID_TIPO_OBJECT_GEN%>" colSpan="2" />
                    <sl:newLine />
                    <slf:lblField name="<%=AmministrazioneForm.VersatoreGenerazioneOggettiDetail.CD_VERS_GEN%>" colSpan="2" controlWidth="w60" />
                </slf:fieldSet>
            </slf:section>
            <sl:newLine skipLine="true"/>
            <slf:section name="<%=AmministrazioneForm.ParametriSection.NAME%>" styleClass="importantContainer">
                <slf:list  name="<%= AmministrazioneForm.SetParametriVersatoreList.NAME%>"  />
                <slf:listNavBar  name="<%= AmministrazioneForm.SetParametriVersatoreList.NAME%>" />
            </slf:section>
        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>
