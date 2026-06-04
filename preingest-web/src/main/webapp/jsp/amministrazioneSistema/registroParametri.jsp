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
    <sl:head title="<%= AmministrazioneForm.ConfigurationList.DESCRIPTION%>" >

        <style>
            th:nth-of-type(2){
                width: 10%;
            }
            td:nth-of-type(2) input {width:100%;}

            th:nth-of-type(4){
                width: 20%;
            }
            td:nth-of-type(4) input {width:100%;}

            th:nth-of-type(5){
                width: 20%;
            }
            td:nth-of-type(5) input {width:100%;}

            td:nth-of-type(6) input {width:100%;}

            td:nth-of-type(9) input {width:100%;}

        </style>

    </sl:head>
    <sl:body>
        <sl:header />
        <sl:menu />
        <sl:content>
            <slf:messageBox />
            <sl:newLine skipLine="true"/>
            <sl:contentTitle title="<%= AmministrazioneForm.Configuration.DESCRIPTION%>" />
            <slf:fieldSet>
                <slf:lblField name="<%= AmministrazioneForm.Configuration.TI_PARAM_APPLIC_COMBO%>" colSpan="2" /><sl:newLine/>
                <slf:lblField name="<%= AmministrazioneForm.Configuration.TI_GESTIONE_PARAM_COMBO%>" colSpan="2" />
                <slf:lblField name="<%= AmministrazioneForm.Configuration.FL_APPART_APPLIC_COMBO%>" colSpan="2" /><sl:newLine/>
                <slf:lblField name="<%= AmministrazioneForm.Configuration.FL_APPART_AMBIENTE_COMBO%>" colSpan="2" />
                <slf:lblField name="<%= AmministrazioneForm.Configuration.FL_APPART_VERS_COMBO%>" colSpan="2" /><sl:newLine/>
                <slf:lblField name="<%= AmministrazioneForm.Configuration.FL_APPART_TIPO_OGGETTO_COMBO%>" colSpan="2" /><sl:newLine/>
                <slf:lblField name="<%= AmministrazioneForm.Configuration.CD_VERSIONE_APP_INI%>" colSpan="2" />
                <slf:lblField name="<%= AmministrazioneForm.Configuration.CD_VERSIONE_APP_FINE%>" colSpan="2" /><sl:newLine/>
            </slf:fieldSet>
            <sl:newLine skipLine="true"/>
            <sl:pulsantiera>
                <slf:lblField name="<%= AmministrazioneForm.Configuration.LOAD_CONFIG_LIST%>" width="w25" />
            </sl:pulsantiera>
            <sl:newLine skipLine="true"/>
            <slf:list name="<%= AmministrazioneForm.ConfigurationList.NAME%>" />
            <slf:listNavBar  name="<%= AmministrazioneForm.ConfigurationList.NAME%>" />
        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>
