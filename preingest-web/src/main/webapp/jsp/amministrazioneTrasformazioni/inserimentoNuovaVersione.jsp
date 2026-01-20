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
    <sl:head  title="<%= TrasformazioniForm.InserimentoNuovaVersioneSection.DESCRIPTION%>" ></sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia versatore"/>
        <sl:menu showChangePasswordBtn="true" />
        <sl:content multipartForm="true" >
            <slf:messageBox />

            <slf:fieldBarDetailTag name="<%=TrasformazioniForm.InserimentoNuovaVersione.NAME%>" hideBackButton="false" />
            <sl:contentTitle title="<%= TrasformazioniForm.InserimentoNuovaVersione.DESCRIPTION%>"/>
            <sl:newLine skipLine="true"/>

            <slf:fieldSet borderHidden="false">
                <slf:lblField name="<%=TrasformazioniForm.InserimentoNuovaVersione.CD_TRASF %>" colSpan="4"/><sl:newLine />
                <slf:lblField name="<%=TrasformazioniForm.InserimentoNuovaVersione.CD_VERSIONE_COR %>" colSpan="2"/><sl:newLine />
                <slf:lblField name="<%=TrasformazioniForm.InserimentoNuovaVersione.DS_VERSIONE_COR %>" colSpan="4"/>
                <sl:newLine />
                
                <slf:lblField name="<%=TrasformazioniForm.InserimentoNuovaVersione.DT_ISTITUZ %>" colSpan="2"/>
                <slf:doubleLblField name="<%=TrasformazioniForm.InserimentoNuovaVersione.ORE_DT_IST %>" name2="<%=TrasformazioniForm.InserimentoNuovaVersione.MINUTI_DT_IST %>" controlWidth="w15" controlWidth2="w15" colSpan="1" />
                <sl:newLine />
                <slf:lblField name="<%=TrasformazioniForm.InserimentoNuovaVersione.DT_SOPPRES %>" colSpan="2"/>
                <slf:doubleLblField name="<%=TrasformazioniForm.InserimentoNuovaVersione.ORE_DT_SOP %>" name2="<%=TrasformazioniForm.InserimentoNuovaVersione.MINUTI_DT_SOP %>" controlWidth="w15" controlWidth2="w15" colSpan="1" />
                <sl:newLine />
                
                <slf:lblField name="<%=TrasformazioniForm.InserimentoNuovaVersione.FL_ATTIVA %>" colSpan="2"/><sl:newLine />
                <slf:lblField name="<%=TrasformazioniForm.InserimentoNuovaVersione.TRANS_BLOB %>" colSpan="2"/><sl:newLine />
            </slf:fieldSet>
        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>
