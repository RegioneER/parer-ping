<%-- <%@ page import="it.eng.sacerasi.slite.gen.form.AmministrazioneForm" pageEncoding="UTF-8"%> --%>
<%@ page import="it.eng.sacerasi.slite.gen.form.AmministrazioneForm" %>
<%@ include file="../../include.jsp"%>


<sl:html>
        
   <sl:head title='${requestScope.titolo}' >
        <script src="<c:url value='/js/help/inithighlightingjs.js' />" type="text/javascript"></script> 
    </sl:head>
        
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia versatore"/>
        <sl:menu showChangePasswordBtn="true" />
         <sl:contentTitle title="${requestScope.titolo}"/>
        <slf:messageBox />    
        <div id="content">

            <c:if test="${(sessionScope['###_FORM_CONTAINER']['xsdDatiSpec'].status eq 'view') }"> 
                
               <!--  <form id="spagoLiteAppForm" action="Amministrazione.html" method="post" > -->
                    <sl:form> 
                    
                    <sl:newLine skipLine="true"/>
                    <div><input type="hidden" name="table" value="${param.table}" /></div>
                        <c:if test="${sessionScope['###_FORM_CONTAINER']['xsdDatiSpecList'].table['empty']}">
                            <slf:fieldBarDetailTag name="<%= AmministrazioneForm.XsdDatiSpec.NAME%>" hideInsertButton="true" hideBackButton="true"/> 
                        </c:if>   
                            
                    <c:if test="${!(sessionScope['###_FORM_CONTAINER']['xsdDatiSpecList'].table['empty']) }">
                        <slf:listNavBarDetail name="<%= AmministrazioneForm.XsdDatiSpecList.NAME%>"   />  
                    </c:if>
                        
                    <sl:newLine />
                    <slf:fieldSet>
                        
                        <%@ include file="xsdDatiSpec.jspf"%>
                            
                        <slf:lblField name="<%=AmministrazioneForm.XsdDatiSpec.CD_VERSIONE_XSD%>" colSpan="4" controlWidth="w40"/>
                        <sl:newLine skipLine="true"/>
                        <slf:lblField name="<%=AmministrazioneForm.XsdDatiSpec.BL_XSD%>" colSpan="4" controlWidth="w100"  />
                        <sl:newLine />
                            
                    </slf:fieldSet>
                    <sl:newLine skipLine="true"/>
                    <sl:pulsantiera>
                        <slf:lblField name="<%=AmministrazioneForm.XsdDatiSpec.SCARICA_XSD_BUTTON%>" width="w50" />
                    </sl:pulsantiera>
                        
                    <div class="livello1"><b>Attributi</b></div>
                    <slf:list  name="<%= AmministrazioneForm.AttribDatiSpecList.NAME%>"  />
                    <slf:listNavBar  name="<%= AmministrazioneForm.AttribDatiSpecList.NAME%>" />
                        
                <!-- </form> -->
                </sl:form>
                    
            </c:if> 
                
            <c:if test="${sessionScope['###_FORM_CONTAINER']['xsdDatiSpec'].status eq 'insert'}">

             <!--    <form id="multipartForm" action="Amministrazione.html" method="post" enctype="multipart/form-data" > -->
                  <sl:form id="multipartForm" multipartForm="true">
                     
                    <div><input type="hidden" name="table" value="${param.table}" /></div>

                    <c:if test="${sessionScope['###_FORM_CONTAINER']['xsdDatiSpecList'].table['empty']}">
                        <slf:fieldBarDetailTag name="<%= AmministrazioneForm.XsdDatiSpec.NAME%>" hideInsertButton="true" hideBackButton="true"/> 
                    </c:if>   

                    <c:if test="${!(sessionScope['###_FORM_CONTAINER']['xsdDatiSpecList'].table['empty']) }">
                        <slf:listNavBarDetail name="<%= AmministrazioneForm.XsdDatiSpecList.NAME%>"   />  
                    </c:if>

                    <sl:newLine />
                    <slf:fieldSet>
                        <%@ include file="xsdDatiSpec.jspf"%>
                        <slf:lblField name="<%=AmministrazioneForm.XsdDatiSpec.CD_VERSIONE_XSD%>" colSpan="4" controlWidth="w40"/><sl:newLine />

                        <label class="slLabel wlbl" for="BL_XSD" >File Xsd&nbsp;</label>

                        <div class="containerLeft w4ctr">                        
                            <div><input type="file" id="BL_XSD"  name="BL_XSD" /></div>
                        </div> 
                    </slf:fieldSet>
                
                <!-- </form> -->
                </sl:form>

                <sl:newLine />
                <sl:newLine skipLine="true"/>

            </c:if>

            <c:if test="${(sessionScope['###_FORM_CONTAINER']['xsdDatiSpec'].status eq 'update') }"> 

 					<sl:form id="multipartForm" multipartForm="true">
            <!--     <form id="multipartForm" action="Amministrazione.html" method="post" enctype="multipart/form-data" > -->
                    <div><input type="hidden" name="table" value="${fn:escapeXml(param.table)}" /></div>
                        <c:if test="${sessionScope['###_FORM_CONTAINER']['xsdDatiSpecList'].table['empty']}">
                            <slf:fieldBarDetailTag name="<%= AmministrazioneForm.XsdDatiSpec.NAME%>" hideInsertButton="true"/> 
                        </c:if>   

                    <c:if test="${!(sessionScope['###_FORM_CONTAINER']['xsdDatiSpecList'].table['empty']) }">
                        <slf:listNavBarDetail name="<%= AmministrazioneForm.XsdDatiSpecList.NAME%>"   />  
                    </c:if>
                    <sl:newLine />

                    <slf:fieldSet>
                        <%@ include file="xsdDatiSpec.jspf"%> 
                        <slf:lblField name="<%=AmministrazioneForm.XsdDatiSpec.CD_VERSIONE_XSD%>" colSpan="4" controlWidth="w40"/><sl:newLine />

                        <label class="slLabel wlbl" for="BL_XSD" >File Xsd&nbsp;</label>

                        <div class="containerLeft w4ctr">                        
                            <div><input type="file" id="BL_XSD"  name="BL_XSD"/></div>
                        </div> 
                    </slf:fieldSet>

				</sl:form>
                <!-- </form> -->

                <sl:newLine />
                <sl:newLine skipLine="true"/>

            </c:if>

        </div>
        <sl:footer />
    </sl:body>

</sl:html>

