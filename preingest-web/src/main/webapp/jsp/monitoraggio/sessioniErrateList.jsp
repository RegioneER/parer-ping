<%@ page import="it.eng.sacerasi.slite.gen.form.MonitoraggioForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head  title="Monitoraggio - Lista sessioni errate" >
    <%--    <script type="text/javascript" src="<c:url value="/js/sips/customCalcStrutVersMessageBox.js"/>" ></script> --%>
        <script type='text/javascript' src="<c:url value="/js/sips/customCheckBoxSesVerif.js"/>" ></script>

        <script type='text/javascript'>
            $(document).ready(function() {
                checkVerificati = $('table.list td > input[name="Fl_verif"]:checked');
                $('#Fl_sessione_err_verif').change( 
                function() { 
                    window.location = "Monitoraggio.html?operation=filtraSessioniVerificate&Fl_sessione_err_verif="+this.value; 
                });
            });
        </script>

    </sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia versatore"/>
        <sl:menu showChangePasswordBtn="true" />
        <sl:content>
            <slf:messageBox  />
            <!--  Bottoni per custom MessageBox in caso javascript sia disabilitato -->
       <%--     <div class="pulsantieraMB">
                <sl:pulsantiera >
                    <slf:buttonList name="<%= MonitoraggioForm.SessioniErrateCustomMessageButtonList.NAME%>"/>
                </sl:pulsantiera>
            </div> --%>
           
            <sl:newLine skipLine="true"/>
            <sl:contentTitle title="LISTA SESSIONI ERRATE"/>
            <sl:newLine skipLine="true"/>
            <slf:fieldSet  borderHidden="false">
                <!-- piazzo i campi del filtro di ricerca -->
                <slf:lblField name="<%=MonitoraggioForm.FiltriSessione.FL_SESSIONE_ERR_VERIF%>" colSpan="1"/>
                <!-- Bottone cerca in caso di javascript disattivato -->
                <noscript><slf:lblField name="<%=MonitoraggioForm.FiltriSessione.CERCA_SESSIONI_ERRATE%>" colSpan="1"/></noscript>    
            </slf:fieldSet>

            <sl:newLine skipLine="true"/>
            <!--  piazzo la lista con i risultati -->
            <slf:list   name="<%= MonitoraggioForm.SessioniErrateList.NAME%>" />
            <slf:listNavBar  name="<%= MonitoraggioForm.SessioniErrateList.NAME%>" />

            <sl:pulsantiera>
                <slf:buttonList name="<%=MonitoraggioForm. SalvaVerificaButtonList.NAME%>">
                    <slf:lblField name="<%=MonitoraggioForm.SalvaVerificaButtonList.IMPOSTA_VERIFICATO%>" width="w50" controlWidth="w30" labelWidth="w40"/>
                </slf:buttonList>
                <slf:lblField name="<%=MonitoraggioForm.FiltriSessione.CALCOLA_STRUTTURA_VERSANTE%>" width="w50" controlWidth="w30" labelWidth="w40"/>
            </sl:pulsantiera>

        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>