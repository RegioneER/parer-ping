<%@ page import="it.eng.sacerasi.slite.gen.form.AmministrazioneForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head title="<%= AmministrazioneForm.ConfigurationList.DESCRIPTION%>" />
    <sl:body>
        <sl:header />
        <sl:menu />
        <sl:content>
            <slf:messageBox />
            <sl:newLine skipLine="true"/>
            <sl:contentTitle title="<%= AmministrazioneForm.Configuration.DESCRIPTION%>" />
            <slf:fieldSet>
                <slf:lblField name="<%= AmministrazioneForm.Configuration.TI_PARAM_APPLIC_COMBO%>" colSpan="2" />
                <slf:lblField name="<%= AmministrazioneForm.Configuration.LOAD_CONFIG_LIST%>" width="w25" /><sl:newLine/>                
                <slf:lblField name="<%= AmministrazioneForm.Configuration.TI_GESTIONE_PARAM_COMBO%>" colSpan="2" />
                <slf:lblField name="<%= AmministrazioneForm.Configuration.EDIT_CONFIG %>" width="w25" /><sl:newLine/>
                <slf:lblField name="<%= AmministrazioneForm.Configuration.FL_APPART_APPLIC_COMBO%>" colSpan="2" />
                <slf:lblField name="<%= AmministrazioneForm.Configuration.ADD_CONFIG%>" width="w25" /><sl:newLine/>
                <slf:lblField name="<%= AmministrazioneForm.Configuration.FL_APPART_AMBIENTE_COMBO%>" colSpan="2" /><sl:newLine/>
                <slf:lblField name="<%= AmministrazioneForm.Configuration.FL_APPART_VERS_COMBO%>" colSpan="2" /><sl:newLine/>
                <slf:lblField name="<%= AmministrazioneForm.Configuration.FL_APPART_TIPO_OGGETTO_COMBO%>" colSpan="2" /><sl:newLine/>
            </slf:fieldSet>
            <sl:newLine skipLine="true"/>
            <%--<c:out value="${(sessionScope['###_FORM_CONTAINER']['configurationList']['table']!=null)}"/>
            <c:if test="${!(sessionScope['###_FORM_CONTAINER']['configurationList'].table['empty'])}">--%>
            <c:if test="${(sessionScope['###_FORM_CONTAINER']['configurationList']['table']!=null)}">
                Nel campo "valori possibili" occorre editare eventuali valori multipli accettati dal parametro separandoli dal carattere | (esempio: FIRMA|MARCA)
            </c:if>
            <sl:newLine skipLine="true"/>
            <!--  piazzo la lista con i risultati -->
            <%--<slf:listNavBar name="<%= AmministrazioneForm.ConfigurationList.NAME%>" pageSizeRelated="true"/>--%>
            <slf:editableList name="<%= AmministrazioneForm.ConfigurationList.NAME%>" multiRowEdit="true" />
            <slf:listNavBar  name="<%= AmministrazioneForm.ConfigurationList.NAME%>" />
            <sl:newLine skipLine="true"/>
            <sl:pulsantiera>
                <slf:lblField name="<%= AmministrazioneForm.Configuration.SAVE_CONFIG%>" width="w25" />
            </sl:pulsantiera>
        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>