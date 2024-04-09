<%@ page import="it.eng.sacerasi.slite.gen.form.AmministrazioneForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head title="Ricerca messaggi in Coda" >
        <script type='text/javascript' src="<c:url value="/js/sips/custumDeleteQueueMessageBox.js"/>" ></script>
        <script type='text/javascript' src="<c:url value="/js/sips/customCheckBoxSesVerif.js"/>" ></script>

        <script type='text/javascript'>
            $(document).ready(function() {
                checkSelected = $('table.list td > input[name="Select_msg"]:checked');
            });
        </script>
    </sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia versatore"/>

        <sl:menu showChangePasswordBtn="true" />

        <sl:content>
            <slf:messageBox /> 
            <sl:newLine skipLine="true"/>
            <sl:contentTitle title="Ricerca messaggi in Coda "/>

            <sl:newLine skipLine="true"/>
            <slf:section name="<%=AmministrazioneForm.FiltriRicercaCode.NAME%>" styleClass="importantContainer">  
                <slf:fieldSet>
                    <slf:lblField name="<%=AmministrazioneForm.VisCoda.NM_CODA%>" colSpan="4" />
                    <sl:newLine />
                    <slf:lblField name="<%=AmministrazioneForm.VisCoda.TIPO_SELETTORE%>" colSpan="4" />
                </slf:fieldSet>
            </slf:section>
            <sl:pulsantiera>
                <slf:lblField  name="<%=AmministrazioneForm.VisCoda.VIS_CODA_BUTTON%>" colSpan="4" />
            </sl:pulsantiera>

            <sl:newLine skipLine="true"/>
            <!--  piazzo la lista con i risultati -->
            <slf:list   name="<%= AmministrazioneForm.CodaList.NAME%>" />
            <slf:listNavBar  name="<%= AmministrazioneForm.CodaList.NAME%>" />

            <sl:pulsantiera>
                <slf:lblField  name="<%=AmministrazioneForm.VisCoda.INVIA_SELEZIONATI%>" colSpan="1" />
                <slf:lblField  name="<%=AmministrazioneForm.VisCoda.INVIA_TUTTI%>" colSpan="1" />
            </sl:pulsantiera>

        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>