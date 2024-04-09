<%@page import="it.eng.sacerasi.slite.gen.form.TrasformazioniForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head  title="<%= TrasformazioniForm.TransformationDetail.DESCRIPTION%>" ></sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia versatore"/>
        <sl:menu showChangePasswordBtn="true" />
        <sl:content multipartForm="true">
            <slf:messageBox />
            <sl:contentTitle title="<%=TrasformazioniForm.TrasformazioneSection.DESCRIPTION%>"/>
            <sl:newLine skipLine="true"/>
            <c:choose>
                <c:when test='<%= session.getAttribute("navTableTrasformazioni").equals(TrasformazioniForm.TrasformazioniList.NAME)%>'>
                    <slf:listNavBarDetail name="<%= TrasformazioniForm.TrasformazioniList.NAME%>" />
                </c:when>
                <c:otherwise>
                    <slf:fieldBarDetailTag name="<%=TrasformazioniForm.TransformationDetail.NAME%>" hideBackButton="true" hideDeleteButton="false" hideUpdateButton="false" />
                </c:otherwise>
            </c:choose>

            <sl:newLine />

            <slf:fieldSet borderHidden="false">
                <slf:lblField name="<%=TrasformazioniForm.TransformationDetail.ID_TRASF%>" colSpan="2"/>
                <sl:newLine />
                <slf:lblField name="<%=TrasformazioniForm.TransformationDetail.CD_TRASF%>" colSpan="2"/>
                <sl:newLine />
                <slf:lblField name="<%=TrasformazioniForm.TransformationDetail.DS_TRASF%>" colSpan="2"/>
                <sl:newLine />
                <slf:lblField name="<%=TrasformazioniForm.TransformationDetail.CD_VERSIONE_COR%>" colSpan="2"/>
                <sl:newLine />
                <slf:lblField name="<%=TrasformazioniForm.TransformationDetail.DS_VERSIONE_COR%>" colSpan="2"/>
                <sl:newLine />
                <slf:lblField name="<%=TrasformazioniForm.TransformationDetail.FL_ATTIVA%>" colSpan="2"/>
                <sl:newLine />
                
                <slf:lblField name="<%=TrasformazioniForm.TransformationDetail.DT_ISTITUZ%>" colSpan="1"/>
                <slf:doubleLblField name="<%=TrasformazioniForm.TransformationDetail.ORE_DT_IST %>" name2="<%=TrasformazioniForm.TransformationDetail.MINUTI_DT_IST %>" controlWidth="w15" controlWidth2="w15" colSpan="1" />
                <sl:newLine />
                <slf:lblField name="<%=TrasformazioniForm.TransformationDetail.DT_SOPPRES %>" colSpan="1"/>
                <slf:doubleLblField name="<%=TrasformazioniForm.TransformationDetail.ORE_DT_SOP %>" name2="<%=TrasformazioniForm.TransformationDetail.MINUTI_DT_SOP %>" controlWidth="w15" controlWidth2="w15" colSpan="1" />
                <sl:newLine />
                
                <slf:lblField name="<%=TrasformazioniForm.TransformationDetail.CD_KETTLE_ID%>" colSpan="2"/>
                <sl:newLine />
                
                <c:if test="${(sessionScope['###_FORM_CONTAINER']['trasformazioniList'].status eq 'update') || (sessionScope['###_FORM_CONTAINER']['transformationDetail'].status eq 'update') }"> 
                    <slf:lblField name="<%=TrasformazioniForm.TransformationDetail.TRANS_BLOB%>" colSpan="2"/>
                </c:if>
                <sl:newLine />
            </slf:fieldSet>
            
            <sl:pulsantiera>
                <slf:lblField name="<%=TrasformazioniForm.TransformationDetail.INSERT_NEW_VERSION%>" />
                <slf:lblField name="<%=TrasformazioniForm.TransformationDetail.INSERT_DEFAULT_PARAMETERS_SET%>" />
                <slf:lblField name="<%=TrasformazioniForm.TransformationDetail.DOWNLOAD_PKG%>" />
                <slf:lblField name="<%=TrasformazioniForm.TransformationDetail.INSERT_PARAMETERS_SET%>" />
                <slf:lblField name="<%=TrasformazioniForm.TransformationDetail.UPDATE_PARAMETERS_SETS%>" />
            </sl:pulsantiera>
            
            <sl:newLine />

            <!--  lista con i set di parametri-->
            <c:if test="${(sessionScope['###_FORM_CONTAINER']['trasformazioniList'].status eq 'view') }">
                <slf:section name="<%= TrasformazioniForm.ParametersListSection.NAME%>">
                <slf:listNavBar name="<%= TrasformazioniForm.ParametersSetList.NAME%>" pageSizeRelated="true"/>
                <slf:list name="<%= TrasformazioniForm.ParametersSetList.NAME%>" />
                <slf:listNavBar  name="<%= TrasformazioniForm.ParametersSetList.NAME%>" />
                </slf:section>
                
                <sl:newLine />
                
                <slf:section name="<%= TrasformazioniForm.TipiOggettoListSection.NAME%>">
                <slf:listNavBar name="<%= TrasformazioniForm.TipiOggettoList.NAME%>" pageSizeRelated="true"/>
                <slf:list name="<%= TrasformazioniForm.TipiOggettoList.NAME%>" />
                <slf:listNavBar  name="<%= TrasformazioniForm.TipiOggettoList.NAME%>" />
                </slf:section>
                
                <sl:newLine />
                
                <slf:section name="<%= TrasformazioniForm.VersioniListSection.NAME %>">
                <slf:listNavBar name="<%= TrasformazioniForm.VersionsList.NAME%>" pageSizeRelated="true"/>
                <slf:list name="<%= TrasformazioniForm.VersionsList.NAME%>" />
                <slf:listNavBar  name="<%= TrasformazioniForm.VersionsList.NAME%>" />
                </slf:section>
            </c:if>
        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>