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
                        $(this).parent().next().next().empty(); // Disabilita Cancellazione
                        $(this).parent().next().empty(); // Disabilita modifica
                    }
                    // Se non è VERSATO disabilità la possibilità di scaricare il rapporto di versamento
                    if (statoSu != 'VERSATO') {
                        $(this).parent().empty();
                    }
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
            <slf:fieldSet  borderHidden="false" styleClass="importantContainer">
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