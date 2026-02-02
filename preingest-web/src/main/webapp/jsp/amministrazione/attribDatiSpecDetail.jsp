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
    <sl:head title="Dettaglio Tipo Oggetto" />

    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia versatore"/>
        <sl:menu showChangePasswordBtn="true" />

        <sl:content>
            <slf:messageBox /> 
            <sl:contentTitle title="Ricerca Tipo Oggetto "/>

            
                <slf:listNavBarDetail name="<%= AmministrazioneForm.AttribDatiSpecList.NAME%>" />  
         
            <sl:newLine skipLine="true"/>

            <slf:fieldSet>
                <slf:section name="<%=AmministrazioneForm.AttribDati.NAME%>" styleClass="importantContainer">  
                    <slf:lblField name="<%=AmministrazioneForm.AttribDatiSpec.NM_ATTRIB_DATI_SPEC%>" colSpan="4" controlWidth="w40" />
                    <sl:newLine />
                    <slf:lblField name="<%=AmministrazioneForm.AttribDatiSpec.NI_ORD%>" colSpan="4" controlWidth="w40" />
                    <sl:newLine />
                    <slf:lblField name="<%=AmministrazioneForm.AttribDatiSpec.NM_COL_DATI_SPEC%>" colSpan="4" controlWidth="w40" />
                    <sl:newLine />
                    <slf:lblField name="<%=AmministrazioneForm.AttribDatiSpec.CD_DATATYPE_XSD%>" colSpan="4" controlWidth="w40" />
                    <sl:newLine />
                    <slf:lblField name="<%=AmministrazioneForm.AttribDatiSpec.TI_DATATYPE_COL%>" colSpan="4" controlWidth="w40" />
                    <sl:newLine />
                    <slf:lblField name="<%=AmministrazioneForm.AttribDatiSpec.FL_FILTRO_DIARIO%>" colSpan="4" controlWidth="w40" />
                    <sl:newLine />
                    <slf:lblField name="<%=AmministrazioneForm.AttribDatiSpec.FL_VERS_SACER%>" colSpan="4" controlWidth="w40" />
                    <sl:newLine />
                </slf:section>
                <sl:newLine skipLine="true"/>


            </slf:fieldSet>

        
        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>
