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

<%@ page import="it.eng.sacerasi.slite.gen.form.StrumentiUrbanisticiForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head title="Strumenti Urbanistici" >
        <script type="text/javascript">
            $(function () {
                $('.Download').each(function (indice) {
                    var statoSu = $(this).parent().prev().prev().text();
                    // se non è bozza disabilita la cancellazione e la modifica!
                    if (statoSu != 'BOZZA') {
                        $(this).parent().next().next().next().empty(); // Disabilita Cancellazione
                        $(this).parent().next().next().empty(); // Disabilita modifica
                    }
                     // Cambia l'icona informativa dall'eventuale messaggio di info in check (baffetto)
                     $('.ui-icon-info').toggleClass('ui-icon-info').addClass('ui-icon-check');
                });
            });
        </script>    
    </sl:head>    

    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia versatore"/>
        <sl:menu showChangePasswordBtn="true" />
        <sl:content>
            <slf:messageBox /> 
            <sl:contentTitle title="Strumenti Urbanistici "/>
            <slf:fieldSet borderHidden="false">
            <slf:fieldSet  borderHidden="false" styleClass="importantContainer containerLeft w60">
                <!-- piazzo i campi del filtro di ricerca -->
                <slf:lblField name="<%=StrumentiUrbanisticiForm.FiltriStrumentiUrbanistici.TI_STRUMENTO_URBANISTICO%>" colSpan="4" />
                <sl:newLine />
                <slf:lblField name="<%=StrumentiUrbanisticiForm.FiltriStrumentiUrbanistici.NM_FASE_ELABORAZIONE %>" colSpan="4" />
                <sl:newLine />
                <slf:lblField name="<%=StrumentiUrbanisticiForm.FiltriStrumentiUrbanistici.DT_CREAZIONE%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=StrumentiUrbanisticiForm.FiltriStrumentiUrbanistici.CD_OGGETTO%>" colSpan="4" />
                <sl:newLine />
                <slf:lblField name="<%=StrumentiUrbanisticiForm.FiltriStrumentiUrbanistici.NI_ANNO%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=StrumentiUrbanisticiForm.FiltriStrumentiUrbanistici.CD_NUMERO%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=StrumentiUrbanisticiForm.FiltriStrumentiUrbanistici.NM_STATO%>" colSpan="2" />
                <sl:newLine />
            </slf:fieldSet>
            <slf:section name="<%=StrumentiUrbanisticiForm.DatiUfficioUrbanisticaSection.NAME%>" styleClass="importantContainer containerRight w40">
                <slf:lblField name="<%=StrumentiUrbanisticiForm.FiltriStrumentiUrbanistici.ID_PUC_FILTRO%>" colSpan="5"/>
                <sl:newLine />
                <slf:lblField name="<%=StrumentiUrbanisticiForm.FiltriStrumentiUrbanistici.NR_BURERT_FILTRO%>" colSpan="5"/>
                <sl:newLine />
                <slf:lblField name="<%=StrumentiUrbanisticiForm.FiltriStrumentiUrbanistici.DT_BURERT_FILTRO%>" colSpan="5"/>
                <sl:newLine />
                <slf:lblField name="<%=StrumentiUrbanisticiForm.FiltriStrumentiUrbanistici.CD_REPERTORIO_FILTRO%>" colSpan="5"/>
                <sl:newLine />
                <slf:lblField name="<%=StrumentiUrbanisticiForm.FiltriStrumentiUrbanistici.ANNO_PROTOCOLLO_FILTRO%>" colSpan="5"/>
                <sl:newLine />
                <slf:lblField name="<%=StrumentiUrbanisticiForm.FiltriStrumentiUrbanistici.CD_PROTOCOLLO_FILTRO%>" colSpan="5"/>
                <sl:newLine />
                <slf:lblField name="<%=StrumentiUrbanisticiForm.FiltriStrumentiUrbanistici.DT_PROTOCOLLO_FILTRO%>" colSpan="5"/>
                <sl:newLine />
            </slf:section>
            </slf:fieldSet>

            <sl:pulsantiera>
                <slf:lblField name="<%=StrumentiUrbanisticiForm.FiltriStrumentiUrbanistici.RICERCA_STRUMENTI_URBANISTICI%>" colSpan="2" />
                <slf:lblField name="<%=StrumentiUrbanisticiForm.FiltriStrumentiUrbanistici.PULISCI_RICERCA_STRUMENTI_URBANISTICI%>" colSpan="2" />
            </sl:pulsantiera>
                
            <sl:newLine skipLine="true"/>
            <slf:list  name="<%=StrumentiUrbanisticiForm.StrumentiUrbanisticiList.NAME%>"  />
            <slf:listNavBar  name="<%=StrumentiUrbanisticiForm.StrumentiUrbanisticiList.NAME%>" />
            <sl:newLine skipLine="true"/>
            <sl:pulsantiera >
                <slf:buttonList name="<%= StrumentiUrbanisticiForm.StrumentiUrbanisticiButtonList.NAME%>"/>
            </sl:pulsantiera>
        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>
