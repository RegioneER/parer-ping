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
    <sl:head title="Dettaglio configurazione" />
    <sl:body>
        <sl:header />
        <sl:menu />
        <sl:content>
            <slf:messageBox />
            <sl:contentTitle title="Dettaglio configurazione" />

            <c:if test="${!(sessionScope['###_FORM_CONTAINER']['configurationList'].table['empty'])}">
                <slf:listNavBarDetail name="<%= AmministrazioneForm.ConfigurationList.NAME%>" />
            </c:if>

            <sl:newLine skipLine="true"/>
            <slf:fieldSet>
                <slf:lblField name="<%= AmministrazioneForm.ConfigurationDetail.ID_PARAM_APPLIC%>" colSpan="2" /><sl:newLine/>
                <slf:lblField name="<%= AmministrazioneForm.ConfigurationDetail.TI_PARAM_APPLIC%>" colSpan="2" /><sl:newLine/>
                <slf:lblField name="<%= AmministrazioneForm.ConfigurationDetail.TI_GESTIONE_PARAM%>" colSpan="2" /><sl:newLine/>

                <slf:lblField name="<%= AmministrazioneForm.ConfigurationDetail.NM_PARAM_APPLIC%>" colSpan="2" /><sl:newLine/>
                <slf:lblField name="<%= AmministrazioneForm.ConfigurationDetail.DM_PARAM_APPLIC%>" colSpan="2" /><sl:newLine/>

                <slf:lblField name="<%= AmministrazioneForm.ConfigurationDetail.DS_PARAM_APPLIC%>" colSpan="4" /><sl:newLine/>
                <slf:lblField name="<%= AmministrazioneForm.ConfigurationDetail.TI_VALORE_PARAM_APPLIC%>" colSpan="2" /><sl:newLine/>
                <slf:lblField name="<%= AmministrazioneForm.ConfigurationDetail.DS_LISTA_VALORI_AMMESSI%>" colSpan="2" /><sl:newLine/>

                <slf:lblField name="<%= AmministrazioneForm.ConfigurationDetail.DS_VALORE_PARAM_APPLIC%>" colSpan="4" /><sl:newLine/>

                <slf:lblField name="<%= AmministrazioneForm.ConfigurationDetail.CD_VERSIONE_APP_INI%>" colSpan="2" /><sl:newLine/>
                <slf:lblField name="<%= AmministrazioneForm.ConfigurationDetail.CD_VERSIONE_APP_FINE%>" colSpan="2" /><sl:newLine/>

                <slf:lblField name="<%= AmministrazioneForm.ConfigurationDetail.FL_APPART_APPLIC%>" colSpan="1" />
                <slf:lblField name="<%= AmministrazioneForm.ConfigurationDetail.FL_APPART_AMBIENTE%>" colSpan="1" />
                <slf:lblField name="<%= AmministrazioneForm.ConfigurationDetail.FL_APPART_VERS%>" colSpan="1" />
                <slf:lblField name="<%= AmministrazioneForm.ConfigurationDetail.FL_APPART_TIPO_OGGETTO%>" colSpan="1" />
            </slf:fieldSet>
            <c:if test="${sessionScope['###_FORM_CONTAINER']['configurationList'].status eq 'insert' or sessionScope['###_FORM_CONTAINER']['configurationList'].status eq 'update'}">
                <sl:newLine skipLine="true"/>
                Nel campo "valori possibili" occorre editare eventuali valori multipli accettati dal parametro separandoli dal carattere | (esempio: FIRMA|MARCA)
            </c:if>
        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>
