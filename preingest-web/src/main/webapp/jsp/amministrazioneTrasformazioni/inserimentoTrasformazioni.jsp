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
    <sl:head  title="<%= TrasformazioniForm.InserimentoTrasformazioniSection.DESCRIPTION%>" ></sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia versatore"/>
        <sl:menu showChangePasswordBtn="true" />
        <sl:content multipartForm="true" >
            <slf:messageBox />

            <slf:fieldBarDetailTag name="<%=TrasformazioniForm.InserisciTrasformazione.NAME%>" hideBackButton="true" />
            <sl:contentTitle title="<%= TrasformazioniForm.InserimentoTrasformazioniSection.DESCRIPTION%>"/>

            <slf:fieldSet borderHidden="false">
                <slf:lblField name="<%=TrasformazioniForm.InserisciTrasformazione.TRANS_NAME%>" colSpan="4"/><sl:newLine />
                <slf:lblField name="<%=TrasformazioniForm.InserisciTrasformazione.TRANS_DESCRIPTION%>" colSpan="4"/><sl:newLine />
                <slf:lblField name="<%=TrasformazioniForm.InserisciTrasformazione.TRANS_VERSION%>" colSpan="2"/><sl:newLine />
                <slf:lblField name="<%=TrasformazioniForm.InserisciTrasformazione.TRANS_VERSION_DESCRIPTION%>" colSpan="4"/><sl:newLine />
                <slf:lblField name="<%=TrasformazioniForm.InserisciTrasformazione.DT_ISTITUZ %>" colSpan="1" />
                <slf:doubleLblField name="<%=TrasformazioniForm.InserisciTrasformazione.ORE_DT_IST %>" name2="<%=TrasformazioniForm.InserisciTrasformazione.MINUTI_DT_IST %>" controlWidth="w15" controlWidth2="w15" colSpan="1" />
                <sl:newLine />
                <slf:lblField name="<%=TrasformazioniForm.InserisciTrasformazione.DT_SOPPRES %>" colSpan="1"/>
                <slf:doubleLblField name="<%=TrasformazioniForm.InserisciTrasformazione.ORE_DT_SOP %>" name2="<%=TrasformazioniForm.InserisciTrasformazione.MINUTI_DT_SOP %>" controlWidth="w15" controlWidth2="w15" colSpan="1" />
                <sl:newLine />
                <slf:lblField name="<%=TrasformazioniForm.InserisciTrasformazione.TRANS_ENABLED%>" colSpan="2"/><sl:newLine />
                <slf:lblField name="<%=TrasformazioniForm.InserisciTrasformazione.TRANS_BLOB%>" colSpan="2"/><sl:newLine />
            </slf:fieldSet>
        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>
