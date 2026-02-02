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
    <sl:head title="<%=AmministrazioneForm.CorrispondenzaSacerTipoOggettoDetail.DESCRIPTION%>" >
    </sl:head>

    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia versatore"/>
        <sl:menu showChangePasswordBtn="true" />

        <sl:content>
            <slf:messageBox /> 
            <sl:contentTitle title="<%=AmministrazioneForm.CorrispondenzaSacerTipoOggettoDetail.DESCRIPTION%>" />

            <c:if test="${sessionScope['###_FORM_CONTAINER']['corrispondenzeSacerTipoOggettoList'].table['empty']}">
                <slf:fieldBarDetailTag name="<%=AmministrazioneForm.CorrispondenzaSacerTipoOggettoDetail.NAME%>" hideBackButton="true"/> 
            </c:if>   

            <c:if test="${!(sessionScope['###_FORM_CONTAINER']['corrispondenzeSacerTipoOggettoList'].table['empty']) }">
                <slf:listNavBarDetail name="<%= AmministrazioneForm.CorrispondenzeSacerTipoOggettoList.NAME%>" />  
            </c:if>

            <sl:newLine skipLine="true"/>

            <slf:fieldSet>
                <slf:lblField name="<%=AmministrazioneForm.Vers.NM_VERS%>" colSpan="2" controlWidth="w40" />
                <sl:newLine />
                <slf:lblField name="<%=AmministrazioneForm.Vers.DS_VERS%>" colSpan="2" controlWidth="w40" />
                <sl:newLine />
                <slf:lblField name="<%=AmministrazioneForm.Vers.ID_AMBIENTE_VERS%>" colSpan="2" controlWidth="w40" />
                <sl:newLine />
                <slf:lblField name="<%=AmministrazioneForm.CorrispondenzaSacerTipoOggettoDetail.ID_DICH_VERS_SACER_TIPO_OBJ%>" colSpan="2" controlWidth="w40" />
                <sl:newLine />
                <slf:lblField name="<%=AmministrazioneForm.CorrispondenzaSacerTipoOggettoDetail.TI_DICH_VERS%>" colSpan="2" controlWidth="w40" />
                <sl:newLine />
                <slf:lblField name="<%=AmministrazioneForm.CorrispondenzaSacerTipoOggettoDetail.ID_ORGANIZ_IAM%>" colSpan="2" controlWidth="w40" />
                <sl:newLine />
                <slf:lblField name="<%=AmministrazioneForm.CorrispondenzaSacerTipoOggettoDetail.NM_USERID_SACER%>" colSpan="2" controlWidth="w40" />
                <sl:newLine />
                <slf:lblField name="<%=AmministrazioneForm.CorrispondenzaSacerTipoOggettoDetail.CD_PASSWORD_SACER%>" colSpan="2" controlWidth="w40" />
            </slf:fieldSet>

        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>
