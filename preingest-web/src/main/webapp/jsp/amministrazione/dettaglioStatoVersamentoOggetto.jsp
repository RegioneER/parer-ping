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
    <sl:head title="Dettaglio stato versamento oggetto" >
    </sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia versatore"/>
        <sl:menu showChangePasswordBtn="true" />
        <sl:content>
            <slf:messageBox /> 
            <sl:newLine skipLine="true"/>
            <sl:contentTitle title="Dettaglio stato versamento oggetto"/>
            <slf:fieldBarDetailTag name="<%=AmministrazioneForm.StatoVersamentoObjectDetail.NAME%>" hideBackButton="true"/> 
            <sl:newLine skipLine="true"/>
            <slf:section name="<%=AmministrazioneForm.StatoVersamentoOggettoSection.NAME%>" styleClass="importantContainer">  
                <slf:fieldSet>
                    <slf:lblField name="<%=AmministrazioneForm.StatoVersamentoObjectDetail.TI_STATO_OBJECT%>" colSpan="4" />
                    <sl:newLine />
                    <slf:lblField name="<%=AmministrazioneForm.StatoVersamentoObjectDetail.DS_TI_STATO_OBJECT%>" colSpan="4" />
                </slf:fieldSet>
            </slf:section>
        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>
