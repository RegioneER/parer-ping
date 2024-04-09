<%@ page import="it.eng.sacerasi.slite.gen.form.SismaForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head title="Sisma" >
        <script type="text/javascript">
            $(function () {
                // Cerca l'area download del rapporto di versamento
                $('.Download').each(function (indice) {
                    // Torna indietro di 4 per prendere lo stato rispetto al download
                    var statoSu = $(this).parent().prev().prev().prev().prev().prev().prev().prev().prev().text();
                    // se non è bozza disabilita la cancellazione e la modifica!
                    if (statoSu === 'BOZZA') {
                        //
                    } else if (statoSu === 'DA_RIVEDERE' || statoSu === 'DA_VERIFICARE') {
                        $(this).parent().next().next().next().empty(); // Disabilita solo la Cancellazione
                    } else {
                        // Disabilita tutto l'edit per tutti gli altri stati
                        $(this).parent().next().next().empty(); // Disabilita modifica
                        $(this).parent().next().next().next().empty(); // Disabilita Cancellazione
                    }
                    // Se non è COMPLETATO disabilità la possibilità di scaricare il rapporto di versamento
//                    if ((statoSu != 'COMPLETATO') && (statoSu != 'VERSATO')) {
//                        $(this).parent().empty();
//                    }
                });
                // Cambia l'icona informativa dall'eventuale messaggio di info in check (baffetto)
                $('.ui-icon-info').toggleClass('ui-icon-info').addClass('ui-icon-check');
            });
        </script>    
    </sl:head>  

    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia versatore"/>
        <sl:menu showChangePasswordBtn="true" />
        <sl:content>
            <slf:messageBox />
            <sl:contentTitle title="Progetti ricostruzione "/>
            <slf:fieldSet borderHidden="false">
            <slf:fieldSet  borderHidden="false" styleClass="importantContainer containerLeft w70">
                <!-- piazzo i campi del filtro di ricerca -->
                <slf:lblField name="<%=SismaForm.FiltriSisma.ID_SOGGETTO_ATT%>" colSpan="2" />
                <slf:lblField name="<%=SismaForm.FiltriSisma.ID_LINEA_FIN%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=SismaForm.FiltriSisma.NM_INTERVENTO%>" colSpan="2" />
                <slf:lblField name="<%=SismaForm.FiltriSisma.NM_FASE_PROG%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=SismaForm.FiltriSisma.NM_STATO_PROG%>" colSpan="2" />
                <slf:lblField name="<%=SismaForm.FiltriSisma.NM_STATO%>" colSpan="2" />
                <slf:lblField name="<%=SismaForm.FiltriSisma.ANNO%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=SismaForm.FiltriSisma.CD_IDENTIFICATIVO%>" colSpan="2" />
                <slf:lblField name="<%=SismaForm.FiltriSisma.DT_CREAZIONE%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=SismaForm.FiltriSisma.CD_OGGETTO%>" colSpan="4" />
                <sl:newLine />
            </slf:fieldSet>
            <slf:section name="<%=SismaForm.DatiAgenziaSection.NAME%>" styleClass="importantContainer containerRight w30">
                <slf:lblField name="<%=SismaForm.FiltriSisma.CD_REGISTRO_AGENZIA%>" colSpan="4"/>
                <sl:newLine />
                <slf:lblField name="<%=SismaForm.FiltriSisma.ANNO_AGENZIA%>" colSpan="4"/>
                <sl:newLine />
                <slf:lblField name="<%=SismaForm.FiltriSisma.CD_NUM_AGENZIA%>" colSpan="4"/>
                <sl:newLine />
            </slf:section>
            </slf:fieldSet>

            <sl:pulsantiera>
                <slf:lblField name="<%=SismaForm.FiltriSisma.RICERCA_SISMA%>" colSpan="2" />
            </sl:pulsantiera>
            <sl:newLine skipLine="true"/>

            <slf:list  name="<%=SismaForm.SismaList.NAME%>"  />
            <slf:listNavBar  name="<%=SismaForm.SismaList.NAME%>" />
            <sl:newLine skipLine="true"/>
            <sl:pulsantiera >
                <slf:buttonList name="<%= SismaForm.SismaButtonList.NAME%>"/>
            </sl:pulsantiera>
        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>