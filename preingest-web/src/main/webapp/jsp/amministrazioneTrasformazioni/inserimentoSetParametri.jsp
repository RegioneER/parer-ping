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

<%@ page import="it.eng.sacerasi.slite.gen.form.TrasformazioniForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head  title="<%= TrasformazioniForm.InserimentoSetParametri.DESCRIPTION%>" ></sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia versatore"/>
        <sl:menu showChangePasswordBtn="true" />
        <sl:content multipartForm="true" >
            <slf:messageBox />

            <slf:fieldBarDetailTag name="<%=TrasformazioniForm.InserimentoSetParametri.NAME%>" hideBackButton="false" />
            <sl:contentTitle title="<%= TrasformazioniForm.InserimentoSetParametri.DESCRIPTION%>"/>
            <sl:newLine skipLine="true"/>

            <slf:fieldSet borderHidden="false">
                <slf:lblField name="<%=TrasformazioniForm.InserimentoSetParametri.NM_SET_PARAM_TRASF%>" colSpan="4"/>
                <sl:newLine />
                <slf:lblField name="<%=TrasformazioniForm.InserimentoSetParametri.DS_SET_PARAM_TRASF%>" colSpan="8"/>
                <sl:newLine />
                <slf:lblField name="<%=TrasformazioniForm.InserimentoSetParametri.FL_SET_PARAM_ARK%>" colSpan="4"/>
                <sl:newLine />
            </slf:fieldSet>
        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>
