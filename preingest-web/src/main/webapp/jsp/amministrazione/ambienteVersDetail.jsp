<%@ page import="it.eng.sacerasi.slite.gen.form.AmministrazioneForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head title="Dettaglio Ambiente" >
        <script type="text/javascript" src="<c:url value='/js/sips/customAmbienteVersMessageBox.js'/>" ></script>
    </sl:head>

    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia versatore"/>
        <sl:menu showChangePasswordBtn="true" />

        <sl:content>
            <slf:messageBox /> 
            
            <c:if test="${!empty requestScope.customBoxSalvataggioAmbienteVersControllo1}">
                <div class="messages customBoxSalvataggioAmbienteVersControllo1 ">
                    <ul>
                        <li class="message warning "><c:out value="${requestScope.customMessageSalvataggioAmbienteVers}"/> </li>
                    </ul>                   
                </div>
            </c:if>
            
             <c:if test="${!empty requestScope.customBoxSalvataggioAmbienteVersControllo2}">
                <div class="messages customBoxSalvataggioAmbienteVersControllo2 ">
                    <ul>
                        <li class="message warning "><c:out value="${requestScope.customMessageSalvataggioAmbienteVers}"/> </li>
                    </ul>                   
                </div>
            </c:if>
            
            <sl:contentTitle title="Dettaglio Ambiente "/>

            <c:if test="${sessionScope['###_FORM_CONTAINER']['ambienteVersList'].table['empty']}">
                <slf:fieldBarDetailTag name="<%=AmministrazioneForm.VisAmbienteVers.NAME%>" hideBackButton="true"/> 
            </c:if>   

            <c:if test="${!(sessionScope['###_FORM_CONTAINER']['ambienteVersList'].table['empty']) }">
                <slf:listNavBarDetail name="<%= AmministrazioneForm.AmbienteVersList.NAME%>" />  
            </c:if>

            <c:if test="${not empty sessionScope['###_FORM_CONTAINER']['versList']}">
                <c:set target="${sessionScope['###_FORM_CONTAINER']['versList']['dupVersatore']}" property="hidden" value="true" />
            </c:if>   

            <sl:newLine skipLine="true"/>

            <slf:fieldSet>
                <slf:section name="<%=AmministrazioneForm.AmbientePing.NAME%>" styleClass="importantContainer">  
                    <slf:lblField name="<%=AmministrazioneForm.AmbienteVers.ID_AMBIENTE_VERS%>" colSpan="4" controlWidth="w40" />
                    <sl:newLine />
                    <slf:lblField name="<%=AmministrazioneForm.AmbienteVers.NM_AMBIENTE_VERS%>" colSpan="4" controlWidth="w40" />
                    <sl:newLine />
                    <slf:lblField name="<%=AmministrazioneForm.AmbienteVers.DS_AMBIENTE_VERS%>" colSpan="4" controlWidth="w40" />
                    <sl:newLine />
                    <slf:lblField name="<%=AmministrazioneForm.AmbienteVers.DT_INI_VAL%>" colSpan="4" controlWidth="w40" />
                    <sl:newLine />
                    <slf:lblField name="<%=AmministrazioneForm.AmbienteVers.DT_FINE_VAL%>" colSpan="4" controlWidth="w40" />
                    <sl:newLine />
                    <slf:lblField name="<%=AmministrazioneForm.AmbienteVers.ID_AMBIENTE_ENTE_CONVENZ%>" colSpan="4" controlWidth="w40" />
                    <sl:newLine />
                    <c:choose>
                        <c:when test="${(sessionScope['###_FORM_CONTAINER']['ambienteVersList'].status eq 'view') || 
                                        ((sessionScope['###_FORM_CONTAINER']['ambienteVersList'].status eq 'update') && !(sessionScope['###_FORM_CONTAINER']['versList'].table['empty']))}">
                            <slf:lblField name="<%=AmministrazioneForm.AmbienteVers.NM_ENTE_GESTORE%>" colSpan="4" controlWidth="w40"/><sl:newLine />
                            <slf:lblField name="<%=AmministrazioneForm.AmbienteVers.NM_ENTE_CONSERV%>" colSpan="4" controlWidth="w40"/><sl:newLine />
                        </c:when>
                        <c:otherwise>
                            <slf:lblField name="<%=AmministrazioneForm.AmbienteVers.ID_ENTE_GESTORE%>" colSpan="4" controlWidth="w40"/><sl:newLine />
                            <slf:lblField name="<%=AmministrazioneForm.AmbienteVers.ID_ENTE_CONSERV%>" colSpan="4" controlWidth="w40"/><sl:newLine />
                        </c:otherwise>
                    </c:choose>
                    <sl:newLine />
                    <sl:newLine />
                <slf:lblField name="<%=AmministrazioneForm.AmbienteVers.DS_NOTE%>" colSpan="2"/>
                </slf:section>
            </slf:fieldSet>

            <sl:newLine skipLine="true"/>
            <sl:pulsantiera>
                <slf:lblField  name="<%=AmministrazioneForm.AmbienteVers.LOG_EVENTI_AMBIENTE%>" colSpan="1"/>
            </sl:pulsantiera>


            <c:if test="${(sessionScope['###_FORM_CONTAINER']['ambienteVers'].status eq 'view') }">

                <div class="livello1"><b>Versatori correlati all’Ambiente</b></div>
                <sl:newLine skipLine="true"/>

                <sl:newLine skipLine="true"/>
                <!--  piazzo la lista con i risultati -->
                <slf:list  name="<%= AmministrazioneForm.VersList.NAME%>"  />
                <slf:listNavBar  name="<%= AmministrazioneForm.VersList.NAME%>" />
            </c:if>

            <slf:section name="<%=AmministrazioneForm.ParametriAmministrazioneAmbienteSection.NAME%>" styleClass="noborder w100">
                <sl:pulsantiera>
                    <slf:lblField name="<%=AmministrazioneForm.ParametriAmbienteButtonList.PARAMETRI_AMMINISTRAZIONE_AMBIENTE_BUTTON%>" width="w50" controlWidth="w30" labelWidth="w40"/>
                </sl:pulsantiera>
                <slf:editableList name="<%= AmministrazioneForm.ParametriAmministrazioneAmbienteList.NAME%>" multiRowEdit="true"/>
                <slf:listNavBar  name="<%= AmministrazioneForm.ParametriAmministrazioneAmbienteList.NAME%>" />
            </slf:section>
            <slf:section name="<%=AmministrazioneForm.ParametriConservazioneAmbienteSection.NAME%>" styleClass="noborder w100">
                <sl:pulsantiera>
                    <slf:lblField name="<%=AmministrazioneForm.ParametriAmbienteButtonList.PARAMETRI_CONSERVAZIONE_AMBIENTE_BUTTON%>" width="w50" controlWidth="w30" labelWidth="w40"/>
                </sl:pulsantiera>
                <slf:editableList name="<%= AmministrazioneForm.ParametriConservazioneAmbienteList.NAME%>" multiRowEdit="true"/>
                <slf:listNavBar  name="<%= AmministrazioneForm.ParametriConservazioneAmbienteList.NAME%>" />
            </slf:section>
            <slf:section name="<%=AmministrazioneForm.ParametriGestioneAmbienteSection.NAME%>" styleClass="noborder w100">
                <sl:pulsantiera>
                    <slf:lblField name="<%=AmministrazioneForm.ParametriAmbienteButtonList.PARAMETRI_GESTIONE_AMBIENTE_BUTTON%>" width="w50" controlWidth="w30" labelWidth="w40"/>
                </sl:pulsantiera>
                <slf:editableList name="<%= AmministrazioneForm.ParametriGestioneAmbienteList.NAME%>" multiRowEdit="true"/>
                <slf:listNavBar  name="<%= AmministrazioneForm.ParametriGestioneAmbienteList.NAME%>" />
            </slf:section>

        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>
