<%@ page import="it.eng.sacerasi.slite.gen.form.MonitoraggioForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head  title="Monitoraggio - Lista versamenti falliti" >
        <script type='text/javascript' src="<c:url value="/js/sips/customCheckBoxSesVerif.js"/>" ></script>
        <script type='text/javascript'>
            $(document).ready(function() {
                checkVerificati = $('table.list td > input[name="Fl_verif"]:checked');
                checkNonRisolubili = $('table.list td > input[name="Fl_non_risolub"]:checked');
                
                $('input[name="operation__impostaTuttiVerificato"]').click(function() {
                    return window.confirm("Sei sicuro di voler procedere?");
                });
                
                $('input[name="operation__impostaTuttiNonRisolubile"]').click(function() {
                    return window.confirm("Sei sicuro di voler procedere?");
                });
            });
        </script>
    </sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia versatore"/>
        <sl:menu showChangePasswordBtn="true" />
        <sl:content>
            <slf:messageBox  />

            <sl:newLine skipLine="true"/>

            <sl:contentTitle title="LISTA VERSAMENTI FALLITI"/>
            <slf:fieldBarDetailTag name="<%= MonitoraggioForm.FiltriRiepilogoVersamenti.NAME%>" hideOperationButton="true"/>

            <slf:fieldSet  borderHidden="false">
                <!-- piazzo i campi del filtro di ricerca -->
                <slf:lblField name="<%=MonitoraggioForm.FiltriVersamenti.ID_AMBIENTE_VERS%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.FiltriVersamenti.ID_VERS%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.FiltriVersamenti.ID_TIPO_OBJECT%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.FiltriVersamenti.VERSAMENTO_TI_STATO%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.FiltriVersamenti.VERSAMENTO_TI_STATO_RISOLUZ%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.FiltriVersamenti.PERIODO_VERS%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.FiltriVersamenti.GIORNO_VERS_DA%>" colSpan="1" />
                <slf:doubleLblField name="<%=MonitoraggioForm.FiltriVersamenti.ORE_VERS_DA%>" name2="<%=MonitoraggioForm.FiltriVersamenti.MINUTI_VERS_DA%>" controlWidth="w15" controlWidth2="w15" colSpan="1" />
                <slf:lblField name="<%=MonitoraggioForm.FiltriVersamenti.GIORNO_VERS_A%>" colSpan="1" />
                <slf:doubleLblField name="<%=MonitoraggioForm.FiltriVersamenti.ORE_VERS_A%>" name2="<%=MonitoraggioForm.FiltriVersamenti.MINUTI_VERS_A%>" controlWidth="w15" controlWidth2="w15" colSpan="1" />
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.FiltriVersamenti.CLASSE_ERRORE%>" colSpan="4" />
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.FiltriVersamenti.CD_ERR%>" colSpan="4" />
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.FiltriVersamenti.VERSAMENTO_FL_VERIF%>" colSpan="4" />
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.FiltriVersamenti.VERSAMENTO_FL_NON_RISOLUB%>" colSpan="4" />
            </slf:fieldSet>

            <sl:newLine skipLine="true" />
            <sl:pulsantiera>
                <slf:lblField name="<%=MonitoraggioForm.VersamentiButtonList.RICERCA_VERSAMENTI_FALLITI%>" colSpan="2" />
            </sl:pulsantiera>
            <sl:newLine skipLine="true" />

            <!--  piazzo la lista con i risultati -->
            <slf:list   name="<%= MonitoraggioForm.VersamentiList.NAME%>" />
            <slf:listNavBar  name="<%= MonitoraggioForm.VersamentiList.NAME%>" />

            <sl:newLine skipLine="true" />

            <sl:pulsantiera>
                <slf:lblField name="<%=MonitoraggioForm.VersamentiButtonList.IMPOSTA_VERIFICATO_NON_RISOLUBILE%>" colSpan="2"/>
                <slf:lblField name="<%=MonitoraggioForm.VersamentiButtonList.IMPOSTA_TUTTI_VERIFICATO%>" colSpan="2"/>
                <slf:lblField name="<%=MonitoraggioForm.VersamentiButtonList.IMPOSTA_TUTTI_NON_RISOLUBILE%>" colSpan="2"/>
            </sl:pulsantiera>

        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>