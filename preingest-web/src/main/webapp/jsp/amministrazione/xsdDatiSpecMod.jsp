<%--
 Engineering Ingegneria Informatica S.p.A.

 Copyright (C) 2023 Regione Emilia-Romagna
 <p/>
 This program is free software: you can redistribute it and/or modify it under the terms of
 the GNU Affero General Public License as published by the Free Software Foundation,
 either version 3 of the License, or (at your option) any later version.
 <p/>
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 See the GNU Affero General Public License for more details.
 <p/>
 You should have received a copy of the GNU Affero General Public License along with this program.
 If not, see <https://www.gnu.org/licenses/>.
 --%>

<%@ page import="it.eng.sacerasi.slite.gen.form.AmministrazioneForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>


<sl:html>

    <sl:head title="Dettaglio Xsd" />
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia versatore"/>
        <sl:menu showChangePasswordBtn="true" />
        <slf:messageBox />    
        <div id="content">

           

            <c:if test="${sessionScope['###_FORM_CONTAINER']['xsdDatiSpec'].status eq 'insert'}">

				<sl:form id="multipartForm" multipartForm="true">
                <!-- <form id="multipartForm" action="Amministrazione.html" method="post" enctype="multipart/form-data" > -->
                    <div><input type="hidden" name="table" value="${fn:escapeXml(param.table)}" /></div>

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
	
				</sl:form>
<!--                 </form> -->

                <sl:newLine />
                <sl:newLine skipLine="true"/>

            </c:if>

            <c:if test="${(sessionScope['###_FORM_CONTAINER']['xsdDatiSpec'].status eq 'update') }"> 

	
				 <sl:form id="multipartForm" multipartForm="true">
               <!--  <form id="multipartForm" action="Amministrazione.html" method="post" enctype="multipart/form-data" > -->
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

