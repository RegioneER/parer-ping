<%@page import="it.eng.spagoLite.security.menu.MenuEntry"%>
<%@page import="it.eng.spagoLite.security.User"%>
<%@page import="it.eng.sacerasi.slite.gen.form.MonitoraggioForm" pageEncoding="UTF-8"%>
<%@include file="../../include.jsp" %>
<c:set scope="request" var="table" value="${!empty param.table}" />

<sl:html>
    <sl:head title="Monitoraggio - Riepilogo versamenti" >
        <style>
            table.grid {
                width : 50%;
            }

            tr:nth-child(odd) { background-color: #ffffff; }
            tr:nth-child(even) { background-color: #e3e3e3; }
        </style>
    </sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia versatore"/>
        <sl:menu showChangePasswordBtn="true" />
        <sl:content>
            <slf:messageBox />
            <sl:newLine skipLine="true"/>
            <sl:contentTitle title="RIEPILOGO VERSAMENTI"/>

            <% User u = (User) request.getSession().getAttribute("###_USER_CONTAINER");
                int lastIndex = u.getMenu().getSelectedPath("").size() - 1;
                String lastMenuEntry = ((MenuEntry) u.getMenu().getSelectedPath("").get(lastIndex)).getCodice();
                if (!lastMenuEntry.contains("RiepilogoVersamenti")) {%> 
            <slf:fieldBarDetailTag name="<%= MonitoraggioForm.FiltriRiepilogoVersamenti.NAME%>" hideOperationButton="true" /> 
            <% }%>

            <!-- inserire da riepilogo vers di sacer --> 

            <sl:newLine skipLine="true"/>

            <slf:fieldSet borderHidden="false">
                <!-- piazzo i campi di selezione -->
                <slf:lblField name="<%=MonitoraggioForm.FiltriRiepilogoVersamenti.ID_AMBIENTE_VERS%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.FiltriRiepilogoVersamenti.ID_VERS%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.FiltriRiepilogoVersamenti.ID_TIPO_OBJECT%>" colSpan="2" />
            </slf:fieldSet>
            <sl:newLine skipLine="true" />

            <sl:pulsantiera>
                <!-- piazzo il bottone -->
                <slf:lblField name="<%=MonitoraggioForm.FiltriRiepilogoVersamenti.GENERA_RIEPILOGO_VERSAMENTI%>" />
            </sl:pulsantiera>
            <sl:newLine skipLine="true"/>


            <%-- Tabella RIEPILOGO OGGETTI VERSATI costruita "a manoni" --%>
            <table class="grid oggettiVersati">
                <caption>
                    <div class="livello1"><b>RIEPILOGO OGGETTI VERSATI</b></div>
                </caption>
                <thead>
                    <tr>
                        <th></th>
                        <th></th>
                        <th>&nbsp;&nbsp;&nbsp;&nbsp;Oggi&nbsp;&nbsp;&nbsp;&nbsp;</th>
                        <th>&nbsp;&nbsp;&nbsp;&nbsp;Ultimi 7 giorni&nbsp;&nbsp;&nbsp;&nbsp;</th>
                        <th>&nbsp;&nbsp;&nbsp;&nbsp;Totale&nbsp;&nbsp;&nbsp;&nbsp;</th>
                    </tr>
                </thead>
                <tbody class="livello2">
                    <tr>
                        <td style="color: #055122;"><b>Oggetti completati con successo</b></td>
                        <td><b>Oggetti versati a Sacer</b></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;periodo=OGGI&amp;tiStato=CHIUSO_OK&amp;tiStato=WARNING_CHIAVE_DUPLICATA&amp;pagina=OBJ_RANGE_DT&amp;classeVers=NON_DA_TRASFORMARE""><slf:field name="<%= MonitoraggioForm.RiepilogoOggettiVersati.NI_CHIUSO_OK_CORR%>" controlWidth="w100"/></a></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;periodo=ULTIMI7&amp;tiStato=CHIUSO_OK&amp;tiStato=WARNING_CHIAVE_DUPLICATA&amp;pagina=OBJ_RANGE_DT&amp;classeVers=NON_DA_TRASFORMARE""><slf:field name="<%= MonitoraggioForm.RiepilogoOggettiVersati.NI_CHIUSO_OK_7%>" controlWidth="w100"/></a></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;periodo=TUTTI&amp;tiStato=CHIUSO_OK&amp;tiStato=WARNING_CHIAVE_DUPLICATA&amp;pagina=OBJ_RANGE_DT&amp;classeVers=NON_DA_TRASFORMARE""><slf:field name="<%= MonitoraggioForm.RiepilogoOggettiVersati.NI_CHIUSO_OK_TOT%>" controlWidth="w100"/></a></td>
                    </tr>
                    <tr>
                        <td></td>
                        <td style="padding-left: 5%"><i>dei quali con chiave duplicata</i></td>
                        <td style="font-style: italic;"><a href="Monitoraggio.html?operation=monitoraggioListe&amp;periodo=OGGI&amp;tiStato=WARNING_CHIAVE_DUPLICATA&amp;pagina=OBJ_RANGE_DT&amp;classeVers=NON_DA_TRASFORMARE"><slf:field name="<%= MonitoraggioForm.RiepilogoOggettiVersati.NI_CHIAVE_DUP_CORR%>" controlWidth="w100"/></a></td>
                        <td style="font-style: italic;"><a href="Monitoraggio.html?operation=monitoraggioListe&amp;periodo=ULTIMI7&amp;tiStato=WARNING_CHIAVE_DUPLICATA&amp;pagina=OBJ_RANGE_DT&amp;classeVers=NON_DA_TRASFORMARE"><slf:field name="<%= MonitoraggioForm.RiepilogoOggettiVersati.NI_CHIAVE_DUP_7%>" controlWidth="w100"/></a></td>
                        <td style="font-style: italic;"><a href="Monitoraggio.html?operation=monitoraggioListe&amp;periodo=TUTTI&amp;tiStato=WARNING_CHIAVE_DUPLICATA&amp;pagina=OBJ_RANGE_DT&amp;classeVers=NON_DA_TRASFORMARE"><slf:field name="<%= MonitoraggioForm.RiepilogoOggettiVersati.NI_CHIAVE_DUP_TOT%>" controlWidth="w100"/></a></td>
                    </tr>
                    <tr>
                        <td></td>
                        <td><b>Oggetti da trasformare completati</b></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;periodo=OGGI&amp;tiStato=CHIUSO_OK&amp;tiStato=WARNING_CHIAVE_DUPLICATA&amp;pagina=OBJ_RANGE_DT&amp;classeVers=DA_TRASFORMARE"><slf:field name="<%= MonitoraggioForm.RiepilogoOggettiVersati.NI_VERS_SACER_CORR%>" controlWidth="w100"/></a></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;periodo=ULTIMI7&amp;tiStato=CHIUSO_OK&amp;tiStato=WARNING_CHIAVE_DUPLICATA&amp;pagina=OBJ_RANGE_DT&amp;classeVers=DA_TRASFORMARE"><slf:field name="<%= MonitoraggioForm.RiepilogoOggettiVersati.NI_VERS_SACER_7%>" controlWidth="w100"/></a></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;periodo=TUTTI&amp;tiStato=CHIUSO_OK&amp;tiStato=WARNING_CHIAVE_DUPLICATA&amp;pagina=OBJ_RANGE_DT&amp;classeVers=DA_TRASFORMARE"><slf:field name="<%= MonitoraggioForm.RiepilogoOggettiVersati.NI_VERS_SACER_TOT%>" controlWidth="w100"/></a></td>
                    </tr>
                    <tr>
                        <td></td>
                        <td style="padding-left: 5%"><i>dei quali con chiave duplicata</i></td>
                        <td style="font-style: italic;"><a href="Monitoraggio.html?operation=monitoraggioListe&amp;periodo=OGGI&amp;tiStato=WARNING_CHIAVE_DUPLICATA&amp;pagina=OBJ_RANGE_DT&amp;classeVers=DA_TRASFORMARE"><slf:field name="<%= MonitoraggioForm.RiepilogoOggettiVersati.NI_TRASFORMATI_CHIAVE_DUP_CORR%>" controlWidth="w100"/></a></td>
                        <td style="font-style: italic;"><a href="Monitoraggio.html?operation=monitoraggioListe&amp;periodo=ULTIMI7&amp;tiStato=WARNING_CHIAVE_DUPLICATA&amp;pagina=OBJ_RANGE_DT&amp;classeVers=DA_TRASFORMARE"><slf:field name="<%= MonitoraggioForm.RiepilogoOggettiVersati.NI_TRASFORMATI_CHIAVE_DUP_7%>" controlWidth="w100"/></a></td>
                        <td style="font-style: italic;"><a href="Monitoraggio.html?operation=monitoraggioListe&amp;periodo=TUTTI&amp;tiStato=WARNING_CHIAVE_DUPLICATA&amp;pagina=OBJ_RANGE_DT&amp;classeVers=DA_TRASFORMARE"><slf:field name="<%= MonitoraggioForm.RiepilogoOggettiVersati.NI_TRASFORMATI_CHIAVE_DUP_TOT%>" controlWidth="w100"/></a></td>
                    </tr>
                    <tr></tr>
                    <tr>
                        <slf:tableSection name="<%= MonitoraggioForm.OggettiInCorsoDiVersamento.NAME%>" />
                        <td><b>Totale</b></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;periodo=OGGI&amp;pagina=OBJ_RANGE_DT&amp;classeVers=NON_DA_TRASFORMARE"><slf:field name="<%= MonitoraggioForm.RiepilogoOggettiVersati.NI_OGG_CORSO_VERS_TOT_CORR%>" controlWidth="w100"/></a></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;periodo=ULTIMI7&amp;pagina=OBJ_RANGE_DT&amp;classeVers=NON_DA_TRASFORMARE"><slf:field name="<%= MonitoraggioForm.RiepilogoOggettiVersati.NI_OGG_CORSO_VERS_TOT_7%>" controlWidth="w100"/></a></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;periodo=TUTTI&amp;pagina=OBJ_RANGE_DT&amp;classeVers=NON_DA_TRASFORMARE"><slf:field name="<%= MonitoraggioForm.RiepilogoOggettiVersati.NI_OGG_CORSO_VERS_TOT_TOT%>" controlWidth="w100"/></a></td>
                    </tr>
                    <tr class="nascondiRiga">
                        <td></td>
                        <td><b>In attesa file</b></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;periodo=OGGI&amp;tiStato=IN_ATTESA_FILE&amp;pagina=OBJ_RANGE_DT&amp;classeVers=NON_DA_TRASFORMARE"><slf:field name="<%= MonitoraggioForm.RiepilogoOggettiVersati.NI_IN_ATTESA_FILE_CORR%>" controlWidth="w100"/></a></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;periodo=ULTIMI7&amp;tiStato=IN_ATTESA_FILE&amp;pagina=OBJ_RANGE_DT&amp;classeVers=NON_DA_TRASFORMARE"><slf:field name="<%= MonitoraggioForm.RiepilogoOggettiVersati.NI_IN_ATTESA_FILE_7%>" controlWidth="w100"/></a></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;periodo=TUTTI&amp;tiStato=IN_ATTESA_FILE&amp;pagina=OBJ_RANGE_DT&amp;classeVers=NON_DA_TRASFORMARE"><slf:field name="<%= MonitoraggioForm.RiepilogoOggettiVersati.NI_IN_ATTESA_FILE_TOT%>" controlWidth="w100"/></a></td>
                    </tr>
                    <tr class="nascondiRiga">
                        <td></td>
                        <td><b>In attesa preparazione XML vers a SACER</b></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;periodo=OGGI&amp;tiStato=IN_ATTESA_SCHED&amp;pagina=OBJ_RANGE_DT&amp;classeVers=NON_DA_TRASFORMARE"><slf:field name="<%= MonitoraggioForm.RiepilogoOggettiVersati.NI_IN_ATTESA_PREP_XML_CORR%>" controlWidth="w100"/></a></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;periodo=ULTIMI7&amp;tiStato=IN_ATTESA_SCHED&amp;pagina=OBJ_RANGE_DT&amp;classeVers=NON_DA_TRASFORMARE"><slf:field name="<%= MonitoraggioForm.RiepilogoOggettiVersati.NI_IN_ATTESA_PREP_XML_7%>" controlWidth="w100"/></a></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;periodo=TUTTI&amp;tiStato=IN_ATTESA_SCHED&amp;pagina=OBJ_RANGE_DT&amp;classeVers=NON_DA_TRASFORMARE"><slf:field name="<%= MonitoraggioForm.RiepilogoOggettiVersati.NI_IN_ATTESA_PREP_XML_TOT%>" controlWidth="w100"/></a></td>
                    </tr>
                    <tr class="nascondiRiga">
                        <td></td>
                        <td><b>In attesa di entrare in coda di versamento</b></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;periodo=OGGI&amp;tiStato=IN_ATTESA_VERS&amp;pagina=OBJ_RANGE_DT&amp;classeVers=NON_DA_TRASFORMARE"><slf:field name="<%= MonitoraggioForm.RiepilogoOggettiVersati.NI_IN_ATTESA_CODA_VERS_CORR%>" controlWidth="w100"/></a></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;periodo=ULTIMI7&amp;tiStato=IN_ATTESA_VERS&amp;pagina=OBJ_RANGE_DT&amp;classeVers=NON_DA_TRASFORMARE"><slf:field name="<%= MonitoraggioForm.RiepilogoOggettiVersati.NI_IN_ATTESA_CODA_VERS_7%>" controlWidth="w100"/></a></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;periodo=TUTTI&amp;tiStato=IN_ATTESA_VERS&amp;pagina=OBJ_RANGE_DT&amp;classeVers=NON_DA_TRASFORMARE"><slf:field name="<%= MonitoraggioForm.RiepilogoOggettiVersati.NI_IN_ATTESA_CODA_VERS_TOT%>" controlWidth="w100"/></a></td>
                    </tr>
                    <tr class="nascondiRiga">
                        <td></td>
                        <td><b>In coda per essere versati</b></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;periodo=OGGI&amp;tiStato=IN_CODA_VERS&amp;pagina=OBJ_RANGE_DT&amp;classeVers=NON_DA_TRASFORMARE"><slf:field name="<%= MonitoraggioForm.RiepilogoOggettiVersati.NI_CODA_VERS_CORR%>" controlWidth="w100"/></a></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;periodo=ULTIMI7&amp;tiStato=IN_CODA_VERS&amp;pagina=OBJ_RANGE_DT&amp;classeVers=NON_DA_TRASFORMARE"><slf:field name="<%= MonitoraggioForm.RiepilogoOggettiVersati.NI_CODA_VERS_7%>" controlWidth="w100"/></a></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;periodo=TUTTI&amp;tiStato=IN_CODA_VERS&amp;pagina=OBJ_RANGE_DT&amp;classeVers=NON_DA_TRASFORMARE"><slf:field name="<%= MonitoraggioForm.RiepilogoOggettiVersati.NI_CODA_VERS_TOT%>" controlWidth="w100"/></a></td>
                    </tr>
                    <tr class="nascondiRiga">
                        <td></td>
                        <td><b>In warning</b></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;periodo=OGGI&amp;tiStato=WARNING&amp;pagina=OBJ_RANGE_DT&amp;classeVers=NON_DA_TRASFORMARE"><slf:field name="<%= MonitoraggioForm.RiepilogoOggettiVersati.NI_WAR_CORR%>" controlWidth="w100"/></a></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;periodo=ULTIMI7&amp;tiStato=WARNING&amp;pagina=OBJ_RANGE_DT&amp;classeVers=NON_DA_TRASFORMARE"><slf:field name="<%= MonitoraggioForm.RiepilogoOggettiVersati.NI_WAR_7%>" controlWidth="w100"/></a></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;periodo=TUTTI&amp;tiStato=WARNING&amp;pagina=OBJ_RANGE_DT&amp;classeVers=NON_DA_TRASFORMARE"><slf:field name="<%= MonitoraggioForm.RiepilogoOggettiVersati.NI_WAR_TOT%>" controlWidth="w100"/></a></td>
                    </tr>
                    <tr>
                        <td style="color: #055122;"><b>Oggetti da non versare</b></td>
                        <td><b>Totale</b></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;periodo=OGGI&amp;tiStato=CHIUSO_WARNING&amp;pagina=OBJ_RANGE_DT&amp;classeVers=NON_DA_TRASFORMARE"><slf:field name="<%= MonitoraggioForm.RiepilogoOggettiVersati.NI_CHIUSO_WAR_CORR%>" controlWidth="w100"/></a></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;periodo=ULTIMI7&amp;tiStato=CHIUSO_WARNING&amp;pagina=OBJ_RANGE_DT&amp;classeVers=NON_DA_TRASFORMARE"><slf:field name="<%= MonitoraggioForm.RiepilogoOggettiVersati.NI_CHIUSO_WAR_7%>" controlWidth="w100"/></a></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;periodo=TUTTI&amp;tiStato=CHIUSO_WARNING&amp;pagina=OBJ_RANGE_DT&amp;classeVers=NON_DA_TRASFORMARE"><slf:field name="<%= MonitoraggioForm.RiepilogoOggettiVersati.NI_CHIUSO_WAR_TOT%>" controlWidth="w100"/></a></td>
                    </tr>
                    <tr></tr>
                    <tr>
                        <slf:tableSection name="<%= MonitoraggioForm.OggettiInCorsoDiTrasformazione.NAME%>" />
                        <td><b>Totale</b></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;periodo=OGGI&amp;pagina=OBJ_RANGE_DT&amp;classeVers=DA_TRASFORMARE"><slf:field name="<%= MonitoraggioForm.RiepilogoOggettiVersati.NI_OGG_CORSO_TRASF_TOT_CORR%>" controlWidth="w100"/></a></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;periodo=ULTIMI7&amp;pagina=OBJ_RANGE_DT&amp;classeVers=DA_TRASFORMARE"><slf:field name="<%= MonitoraggioForm.RiepilogoOggettiVersati.NI_OGG_CORSO_TRASF_TOT_7%>" controlWidth="w100"/></a></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;periodo=TUTTI&amp;pagina=OBJ_RANGE_DT&amp;classeVers=DA_TRASFORMARE"><slf:field name="<%= MonitoraggioForm.RiepilogoOggettiVersati.NI_OGG_CORSO_TRASF_TOT_TOT%>" controlWidth="w100"/></a></td>
                    </tr>
                    <tr class="nascondiRiga">
                        <td></td>
                        <td><b>In attesa file</b></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;periodo=OGGI&amp;tiStato=IN_ATTESA_FILE&amp;pagina=OBJ_RANGE_DT&amp;classeVers=DA_TRASFORMARE"><slf:field name="<%= MonitoraggioForm.RiepilogoOggettiVersati.NI_IN_ATTESA_FILE_TRASF_CORR%>" controlWidth="w100"/></a></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;periodo=ULTIMI7&amp;tiStato=IN_ATTESA_FILE&amp;pagina=OBJ_RANGE_DT&amp;classeVers=DA_TRASFORMARE"><slf:field name="<%= MonitoraggioForm.RiepilogoOggettiVersati.NI_IN_ATTESA_FILE_TRASF_7%>" controlWidth="w100"/></a></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;periodo=TUTTI&amp;tiStato=IN_ATTESA_FILE&amp;pagina=OBJ_RANGE_DT&amp;classeVers=DA_TRASFORMARE"><slf:field name="<%= MonitoraggioForm.RiepilogoOggettiVersati.NI_IN_ATTESA_FILE_TRASF_TOT%>" controlWidth="w100"/></a></td>
                    </tr>
                    <tr class="nascondiRiga">
                        <td></td>
                        <td><b>In attesa di trasformazione</b></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;periodo=OGGI&amp;tiStato=DA_TRASFORMARE&amp;tiStato=TRASFORMAZIONE_NON_ATTIVA&amp;pagina=OBJ_RANGE_DT&amp;classeVers=DA_TRASFORMARE"><slf:field name="<%= MonitoraggioForm.RiepilogoOggettiVersati.NI_IN_ATTESA_TRASF_CORR%>" controlWidth="w100"/></a></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;periodo=ULTIMI7&amp;tiStato=DA_TRASFORMARE&amp;tiStato=TRASFORMAZIONE_NON_ATTIVA&amp;pagina=OBJ_RANGE_DT&amp;classeVers=DA_TRASFORMARE"><slf:field name="<%= MonitoraggioForm.RiepilogoOggettiVersati.NI_IN_ATTESA_TRASF_7%>" controlWidth="w100"/></a></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;periodo=TUTTI&amp;tiStato=DA_TRASFORMARE&amp;tiStato=TRASFORMAZIONE_NON_ATTIVA&amp;pagina=OBJ_RANGE_DT&amp;classeVers=DA_TRASFORMARE"><slf:field name="<%= MonitoraggioForm.RiepilogoOggettiVersati.NI_IN_ATTESA_TRASF_TOT%>" controlWidth="w100"/></a></td>
                    </tr>
                    <tr class="nascondiRiga">
                        <td></td>
                        <td><b>Trasformazione in corso</b></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;periodo=OGGI&amp;tiStato=TRASFORMAZIONE_IN_CORSO&amp;pagina=OBJ_RANGE_DT&amp;classeVers=DA_TRASFORMARE"><slf:field name="<%= MonitoraggioForm.RiepilogoOggettiVersati.NI_IN_RUNNING_TRASF_CORR%>" controlWidth="w100"/></a></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;periodo=ULTIMI7&amp;tiStato=TRASFORMAZIONE_IN_CORSO&amp;pagina=OBJ_RANGE_DT&amp;classeVers=DA_TRASFORMARE"><slf:field name="<%= MonitoraggioForm.RiepilogoOggettiVersati.NI_IN_RUNNING_TRASF_7%>" controlWidth="w100"/></a></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;periodo=TUTTI&amp;tiStato=TRASFORMAZIONE_IN_CORSO&amp;pagina=OBJ_RANGE_DT&amp;classeVers=DA_TRASFORMARE"><slf:field name="<%= MonitoraggioForm.RiepilogoOggettiVersati.NI_IN_RUNNING_TRASF_TOT%>" controlWidth="w100"/></a></td>
                    </tr>
                    <tr class="nascondiRiga">
                        <td></td>
                        <td><b>Preparazione oggetti</b></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;periodo=OGGI&amp;tiStato=PREPARAZIONE_OGG_IN_CORSO&amp;pagina=OBJ_RANGE_DT&amp;classeVers=DA_TRASFORMARE"><slf:field name="<%= MonitoraggioForm.RiepilogoOggettiVersati.NI_IN_CREATINGOBJS_TRASF_CORR%>" controlWidth="w100"/></a></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;periodo=ULTIMI7&amp;tiStato=PREPARAZIONE_OGG_IN_CORSO&amp;pagina=OBJ_RANGE_DT&amp;classeVers=DA_TRASFORMARE"><slf:field name="<%= MonitoraggioForm.RiepilogoOggettiVersati.NI_IN_CREATINGOBJS_TRASF_7%>" controlWidth="w100"/></a></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;periodo=TUTTI&amp;tiStato=PREPARAZIONE_OGG_IN_CORSO&amp;pagina=OBJ_RANGE_DT&amp;classeVers=DA_TRASFORMARE"><slf:field name="<%= MonitoraggioForm.RiepilogoOggettiVersati.NI_IN_CREATINGOBJS_TRASF_TOT%>" controlWidth="w100"/></a></td>
                    </tr>
                    <tr class="nascondiRiga">
                        <td></td>
                        <td><b>Trasformazione bloccata</b></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;periodo=OGGI&amp;tiStato=ERRORE_TRASFORMAZIONE&amp;pagina=OBJ_RANGE_DT&amp;classeVers=DA_TRASFORMARE"><slf:field name="<%= MonitoraggioForm.RiepilogoOggettiVersati.NI_TRASF_BLOCCATA_CORR%>" controlWidth="w100"/></a></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;periodo=ULTIMI7&amp;tiStato=ERRORE_TRASFORMAZIONE&amp;pagina=OBJ_RANGE_DT&amp;classeVers=DA_TRASFORMARE"><slf:field name="<%= MonitoraggioForm.RiepilogoOggettiVersati.NI_TRASF_BLOCCATA_7%>" controlWidth="w100"/></a></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;periodo=TUTTI&amp;tiStato=ERRORE_TRASFORMAZIONE&amp;pagina=OBJ_RANGE_DT&amp;classeVers=DA_TRASFORMARE"><slf:field name="<%= MonitoraggioForm.RiepilogoOggettiVersati.NI_TRASF_BLOCCATA_TOT%>" controlWidth="w100"/></a></td>
                    </tr>
                    <tr class="nascondiRiga">
                        <td></td>
                        <td><b>Trasformazione in warning</b></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;periodo=OGGI&amp;tiStato=WARNING_TRASFORMAZIONE&amp;pagina=OBJ_RANGE_DT&amp;classeVers=DA_TRASFORMARE"><slf:field name="<%= MonitoraggioForm.RiepilogoOggettiVersati.NI_IN_WARNING_TRASF_CORR%>" controlWidth="w100"/></a></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;periodo=ULTIMI7&amp;tiStato=WARNING_TRASFORMAZIONE&amp;pagina=OBJ_RANGE_DT&amp;classeVers=DA_TRASFORMARE"><slf:field name="<%= MonitoraggioForm.RiepilogoOggettiVersati.NI_IN_WARNING_TRASF_7%>" controlWidth="w100"/></a></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;periodo=TUTTI&amp;tiStato=WARNING_TRASFORMAZIONE&amp;pagina=OBJ_RANGE_DT&amp;classeVers=DA_TRASFORMARE"><slf:field name="<%= MonitoraggioForm.RiepilogoOggettiVersati.NI_IN_WARNING_TRASF_TOT%>" controlWidth="w100"/></a></td>
                    </tr>
                    <tr class="nascondiRiga">
                        <td></td>
                        <td><b>Trasformati</b></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;periodo=OGGI&amp;tiStato=TRASFORMATO&amp;pagina=OBJ_RANGE_DT&amp;classeVers=DA_TRASFORMARE"><slf:field name="<%= MonitoraggioForm.RiepilogoOggettiVersati.NI_TRASFORMATI_CORR%>" controlWidth="w100"/></a></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;periodo=ULTIMI7&amp;tiStato=TRASFORMATO&amp;pagina=OBJ_RANGE_DT&amp;classeVers=DA_TRASFORMARE"><slf:field name="<%= MonitoraggioForm.RiepilogoOggettiVersati.NI_TRASFORMATI_7%>" controlWidth="w100"/></a></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;periodo=TUTTI&amp;tiStato=TRASFORMATO&amp;pagina=OBJ_RANGE_DT&amp;classeVers=DA_TRASFORMARE"><slf:field name="<%= MonitoraggioForm.RiepilogoOggettiVersati.NI_TRASFORMATI_TOT%>" controlWidth="w100"/></a></td>
                    </tr>
                    <tr class="nascondiRiga">
                        <td></td>
                        <td><b>Versati a preingest</b></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;periodo=OGGI&amp;tiStato=VERSATO_A_PING&amp;pagina=OBJ_RANGE_DT&amp;classeVers=DA_TRASFORMARE"><slf:field name="<%= MonitoraggioForm.RiepilogoOggettiVersati.NI_VERS_PING_CORR%>" controlWidth="w100"/></a></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;periodo=ULTIMI7&amp;tiStato=VERSATO_A_PING&amp;pagina=OBJ_RANGE_DT&amp;classeVers=DA_TRASFORMARE"><slf:field name="<%= MonitoraggioForm.RiepilogoOggettiVersati.NI_VERS_PING_7%>" controlWidth="w100"/></a></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;periodo=TUTTI&amp;tiStato=VERSATO_A_PING&amp;pagina=OBJ_RANGE_DT&amp;classeVers=DA_TRASFORMARE"><slf:field name="<%= MonitoraggioForm.RiepilogoOggettiVersati.NI_VERS_PING_TOT%>" controlWidth="w100"/></a></td>
                    </tr>
                    <tr class="nascondiRiga">
                        <td></td>
                        <td><b>Versamento a preingest bloccato</b></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;periodo=OGGI&amp;tiStato=ERRORE_VERSAMENTO_A_PING&amp;pagina=OBJ_RANGE_DT&amp;classeVers=DA_TRASFORMARE"><slf:field name="<%= MonitoraggioForm.RiepilogoOggettiVersati.NI_VERS_PING_BLOCCATO_CORR%>" controlWidth="w100"/></a></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;periodo=ULTIMI7&amp;tiStato=ERRORE_VERSAMENTO_A_PING&amp;pagina=OBJ_RANGE_DT&amp;classeVers=DA_TRASFORMARE"><slf:field name="<%= MonitoraggioForm.RiepilogoOggettiVersati.NI_VERS_PING_BLOCCATO_7%>" controlWidth="w100"/></a></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;periodo=TUTTI&amp;tiStato=ERRORE_VERSAMENTO_A_PING&amp;pagina=OBJ_RANGE_DT&amp;classeVers=DA_TRASFORMARE"><slf:field name="<%= MonitoraggioForm.RiepilogoOggettiVersati.NI_VERS_PING_BLOCCATO_TOT%>" controlWidth="w100"/></a></td>
                    </tr>
                    <tr class="nascondiRiga">
                        <td></td>
                        <td><b>Problemi nella preparazione SIP</b></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;periodo=OGGI&amp;tiStato=PROBLEMA_PREPARAZIONE_SIP&amp;pagina=OBJ_RANGE_DT&amp;classeVers=DA_TRASFORMARE"><slf:field name="<%= MonitoraggioForm.RiepilogoOggettiVersati.NI_PROBLEMA_PREP_SIP_CORR%>" controlWidth="w100"/></a></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;periodo=ULTIMI7&amp;tiStato=PROBLEMA_PREPARAZIONE_SIP&amp;pagina=OBJ_RANGE_DT&amp;classeVers=DA_TRASFORMARE"><slf:field name="<%= MonitoraggioForm.RiepilogoOggettiVersati.NI_PROBLEMA_PREP_SIP_7%>" controlWidth="w100"/></a></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;periodo=TUTTI&amp;tiStato=PROBLEMA_PREPARAZIONE_SIP&amp;pagina=OBJ_RANGE_DT&amp;classeVers=DA_TRASFORMARE"><slf:field name="<%= MonitoraggioForm.RiepilogoOggettiVersati.NI_PROBLEMA_PREP_SIP_TOT%>" controlWidth="w100"/></a></td>
                    </tr>
                    <tr class="nascondiRiga">
                        <td></td>
                        <td><b>In corso di versamento a Sacer</b></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;periodo=OGGI&amp;tiStato=IN_CORSO_VERS_SACER&amp;pagina=OBJ_RANGE_DT&amp;classeVers=DA_TRASFORMARE"><slf:field name="<%= MonitoraggioForm.RiepilogoOggettiVersati.NI_IN_CORSO_VERS_SACER_CORR%>" controlWidth="w100"/></a></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;periodo=ULTIMI7&amp;tiStato=IN_CORSO_VERS_SACER&amp;pagina=OBJ_RANGE_DT&amp;classeVers=DA_TRASFORMARE"><slf:field name="<%= MonitoraggioForm.RiepilogoOggettiVersati.NI_IN_CORSO_VERS_SACER_7%>" controlWidth="w100"/></a></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;periodo=TUTTI&amp;tiStato=IN_CORSO_VERS_SACER&amp;pagina=OBJ_RANGE_DT&amp;classeVers=DA_TRASFORMARE"><slf:field name="<%= MonitoraggioForm.RiepilogoOggettiVersati.NI_IN_CORSO_VERS_SACER_TOT%>" controlWidth="w100"/></a></td>
                    </tr>
                    <tr class="nascondiRiga">
                        <td></td>
                        <td><b>Problemi nel versamento a Sacer</b></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;periodo=OGGI&amp;tiStato=PROBLEMA_VERS_SACER&amp;pagina=OBJ_RANGE_DT&amp;classeVers=DA_TRASFORMARE"><slf:field name="<%= MonitoraggioForm.RiepilogoOggettiVersati.NI_PROBLEMA_VERS_SACER_CORR%>" controlWidth="w100"/></a></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;periodo=ULTIMI7&amp;tiStato=PROBLEMA_VERS_SACER&amp;pagina=OBJ_RANGE_DT&amp;classeVers=DA_TRASFORMARE"><slf:field name="<%= MonitoraggioForm.RiepilogoOggettiVersati.NI_PROBLEMA_VERS_SACER_7%>" controlWidth="w100"/></a></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;periodo=TUTTI&amp;tiStato=PROBLEMA_VERS_SACER&amp;pagina=OBJ_RANGE_DT&amp;classeVers=DA_TRASFORMARE"><slf:field name="<%= MonitoraggioForm.RiepilogoOggettiVersati.NI_PROBLEMA_VERS_SACER_TOT%>" controlWidth="w100"/></a></td>
                    </tr>
                </tbody>
            </table>

            <sl:newLine skipLine="true"/>
            <sl:newLine skipLine="true"/>
            <%-- Tabella RIEPILOGO INVII OGGETTI FALLITI costruita "a manoni" --%>
            <table class="grid" >
                <caption>
                    <div class="livello1"><b>RIEPILOGO INVII OGGETTI FALLITI</b></div>
                </caption>
                <thead>
                    <tr>
                        <th></th>
                        <th></th>
                        <th>&nbsp;&nbsp;&nbsp;&nbsp;Oggi&nbsp;&nbsp;&nbsp;&nbsp;</th>
                        <th>&nbsp;&nbsp;&nbsp;&nbsp;Ultimi 7 giorni&nbsp;&nbsp;&nbsp;&nbsp;</th>
                        <th>&nbsp;&nbsp;&nbsp;&nbsp;Totale&nbsp;&nbsp;&nbsp;&nbsp;</th>
                    </tr>
                </thead>
                <tbody class="livello2">
                    <tr>
                        <slf:tableSection name="<%= MonitoraggioForm.InviiOggettiFalliti.NAME%>" />
                        <td><b>Totale</b></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;tipoErrore=INVIO_OGGETTO&amp;periodo=OGGI&amp;tiStato=CHIUSO_ERR&amp;pagina=SES_RANGE_DT"><slf:field name="<%= MonitoraggioForm.RiepilogoInviiOggettiFalliti.NI_OGG_FALL_TOT_CORR%>" controlWidth="w100"/></a></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;tipoErrore=INVIO_OGGETTO&amp;periodo=ULTIMI7&amp;tiStato=CHIUSO_ERR&amp;pagina=SES_RANGE_DT"><slf:field name="<%= MonitoraggioForm.RiepilogoInviiOggettiFalliti.NI_OGG_FALL_TOT_7%>" controlWidth="w100"/></a></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;tipoErrore=INVIO_OGGETTO&amp;periodo=TUTTI&amp;tiStato=CHIUSO_ERR&amp;pagina=SES_RANGE_DT"><slf:field name="<%= MonitoraggioForm.RiepilogoInviiOggettiFalliti.NI_OGG_FALL_TOT_TOT%>" controlWidth="w100"/></a></td>
                    </tr>
                    <tr class="nascondiRiga">
                        <td></td>
                        <td><b>Risolti</b></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;tipoErrore=INVIO_OGGETTO&amp;periodo=OGGI&amp;tiStato=CHIUSO_ERR&amp;tiStatoRisoluz=RISOLTO&amp;pagina=SES_RANGE_DT"><slf:field name="<%= MonitoraggioForm.RiepilogoInviiOggettiFalliti.NI_OGG_FALL_RIS_VER_CORR%>" controlWidth="w100"/></a></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;tipoErrore=INVIO_OGGETTO&amp;periodo=ULTIMI7&amp;tiStato=CHIUSO_ERR&amp;tiStatoRisoluz=RISOLTO&amp;pagina=SES_RANGE_DT"><slf:field name="<%= MonitoraggioForm.RiepilogoInviiOggettiFalliti.NI_OGG_FALL_RIS_VER_7%>" controlWidth="w100"/></a></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;tipoErrore=INVIO_OGGETTO&amp;periodo=TUTTI&amp;tiStato=CHIUSO_ERR&amp;tiStatoRisoluz=RISOLTO&amp;pagina=SES_RANGE_DT"><slf:field name="<%= MonitoraggioForm.RiepilogoInviiOggettiFalliti.NI_OGG_FALL_RIS_VER_TOT%>" controlWidth="w100"/></a></td>
                    </tr>
                    <tr class="nascondiRiga">
                        <td></td>
                        <td><b>In corso di risoluzione</b></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;tipoErrore=INVIO_OGGETTO&amp;periodo=OGGI&amp;tiStato=CHIUSO_ERR&amp;tiStatoRisoluz=IN_CORSO&amp;pagina=SES_RANGE_DT"><slf:field name="<%= MonitoraggioForm.RiepilogoInviiOggettiFalliti.NI_OGG_FALL_CORSO_RIS_VER_CORR%>" controlWidth="w100"/></a></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;tipoErrore=INVIO_OGGETTO&amp;periodo=ULTIMI7&amp;tiStato=CHIUSO_ERR&amp;tiStatoRisoluz=IN_CORSO&amp;pagina=SES_RANGE_DT"><slf:field name="<%= MonitoraggioForm.RiepilogoInviiOggettiFalliti.NI_OGG_FALL_CORSO_RIS_VER_7%>" controlWidth="w100"/></a></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;tipoErrore=INVIO_OGGETTO&amp;periodo=TUTTI&amp;tiStato=CHIUSO_ERR&amp;tiStatoRisoluz=IN_CORSO&amp;pagina=SES_RANGE_DT"><slf:field name="<%= MonitoraggioForm.RiepilogoInviiOggettiFalliti.NI_OGG_FALL_CORSO_RIS_VER_TOT%>" controlWidth="w100"/></a></td>
                    </tr>
                    <tr class="nascondiRiga">
                        <td></td>
                        <td><b>In warning</b></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;tipoErrore=INVIO_OGGETTO&amp;periodo=OGGI&amp;tiStato=CHIUSO_ERR&amp;tiStatoRisoluz=WARNING&amp;pagina=SES_RANGE_DT"><slf:field name="<%= MonitoraggioForm.RiepilogoInviiOggettiFalliti.NI_OGG_FALL_WAR_VER_CORR%>" controlWidth="w100"/></a></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;tipoErrore=INVIO_OGGETTO&amp;periodo=ULTIMI7&amp;tiStato=CHIUSO_ERR&amp;tiStatoRisoluz=WARNING&amp;pagina=SES_RANGE_DT"><slf:field name="<%= MonitoraggioForm.RiepilogoInviiOggettiFalliti.NI_OGG_FALL_WAR_VER_7%>" controlWidth="w100"/></a></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;tipoErrore=INVIO_OGGETTO&amp;periodo=TUTTI&amp;tiStato=CHIUSO_ERR&amp;tiStatoRisoluz=WARNING&amp;pagina=SES_RANGE_DT"><slf:field name="<%= MonitoraggioForm.RiepilogoInviiOggettiFalliti.NI_OGG_FALL_WAR_VER_TOT%>" controlWidth="w100"/></a></td>
                    </tr>
                    <tr class="nascondiRiga">
                        <td></td>
                        <td><b>Da non versare</b></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;tipoErrore=INVIO_OGGETTO&amp;periodo=OGGI&amp;tiStato=CHIUSO_ERR&amp;tiStatoRisoluz=CHIUSO_WARNING&amp;pagina=SES_RANGE_DT"><slf:field name="<%= MonitoraggioForm.RiepilogoInviiOggettiFalliti.NI_OGG_FALL_NOVERS_VER_CORR%>" controlWidth="w100"/></a></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;tipoErrore=INVIO_OGGETTO&amp;periodo=ULTIMI7&amp;tiStato=CHIUSO_ERR&amp;tiStatoRisoluz=CHIUSO_WARNING&amp;pagina=SES_RANGE_DT"><slf:field name="<%= MonitoraggioForm.RiepilogoInviiOggettiFalliti.NI_OGG_FALL_NOVERS_VER_7%>" controlWidth="w100"/></a></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;tipoErrore=INVIO_OGGETTO&amp;periodo=TUTTI&amp;tiStato=CHIUSO_ERR&amp;tiStatoRisoluz=CHIUSO_WARNING&amp;pagina=SES_RANGE_DT"><slf:field name="<%= MonitoraggioForm.RiepilogoInviiOggettiFalliti.NI_OGG_FALL_NOVERS_VER_TOT%>" controlWidth="w100"/></a></td>
                    </tr>
                    <tr class="nascondiRiga">
                        <td></td>
                        <td><b>Non risolubili</b></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;tipoErrore=INVIO_OGGETTO&amp;periodo=OGGI&amp;tiStato=CHIUSO_ERR&amp;tiStatoRisoluz=NON_RISOLTO&amp;flVerif=1&amp;flNonRisolub=1&amp;pagina=SES_RANGE_DT"><slf:field name="<%= MonitoraggioForm.RiepilogoInviiOggettiFalliti.NI_OGG_FALL_NORISOLUB_CORR%>" controlWidth="w100"/></a></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;tipoErrore=INVIO_OGGETTO&amp;periodo=ULTIMI7&amp;tiStato=CHIUSO_ERR&amp;tiStatoRisoluz=NON_RISOLTO&amp;flVerif=1&amp;flNonRisolub=1&amp;pagina=SES_RANGE_DT"><slf:field name="<%= MonitoraggioForm.RiepilogoInviiOggettiFalliti.NI_OGG_FALL_NORISOLUB_7%>" controlWidth="w100"/></a></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;tipoErrore=INVIO_OGGETTO&amp;periodo=TUTTI&amp;tiStato=CHIUSO_ERR&amp;tiStatoRisoluz=NON_RISOLTO&amp;flVerif=1&amp;flNonRisolub=1&amp;pagina=SES_RANGE_DT"><slf:field name="<%= MonitoraggioForm.RiepilogoInviiOggettiFalliti.NI_OGG_FALL_NORISOLUB_TOT%>" controlWidth="w100"/></a></td>
                    </tr>
                    <tr class="nascondiRiga">
                        <td></td>
                        <td><b>Non risolti e verificati</b></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;tipoErrore=INVIO_OGGETTO&amp;periodo=OGGI&amp;tiStato=CHIUSO_ERR&amp;tiStatoRisoluz=NON_RISOLTO&amp;flVerif=1&amp;flNonRisolub=0&amp;pagina=SES_RANGE_DT"><slf:field name="<%= MonitoraggioForm.RiepilogoInviiOggettiFalliti.NI_OGG_FALL_NORIS_VER_CORR%>" controlWidth="w100"/></a></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;tipoErrore=INVIO_OGGETTO&amp;periodo=ULTIMI7&amp;tiStato=CHIUSO_ERR&amp;tiStatoRisoluz=NON_RISOLTO&amp;flVerif=1&amp;flNonRisolub=0&amp;pagina=SES_RANGE_DT"><slf:field name="<%= MonitoraggioForm.RiepilogoInviiOggettiFalliti.NI_OGG_FALL_NORIS_VER_7%>" controlWidth="w100"/></a></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;tipoErrore=INVIO_OGGETTO&amp;periodo=TUTTI&amp;tiStato=CHIUSO_ERR&amp;tiStatoRisoluz=NON_RISOLTO&amp;flVerif=1&amp;flNonRisolub=0&amp;pagina=SES_RANGE_DT"><slf:field name="<%= MonitoraggioForm.RiepilogoInviiOggettiFalliti.NI_OGG_FALL_NORIS_VER_TOT%>" controlWidth="w100"/></a></td>
                    </tr>
                    <tr class="nascondiRiga">
                        <td></td>
                        <td><b>Non risolti e non verificati</b></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;tipoErrore=INVIO_OGGETTO&amp;periodo=OGGI&amp;tiStato=CHIUSO_ERR&amp;tiStatoRisoluz=NON_RISOLTO&amp;flVerif=0&amp;pagina=SES_RANGE_DT"><slf:field name="<%= MonitoraggioForm.RiepilogoInviiOggettiFalliti.NI_OGG_FALL_NORIS_NOVER_CORR%>" controlWidth="w100"/></a></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;tipoErrore=INVIO_OGGETTO&amp;periodo=ULTIMI7&amp;tiStato=CHIUSO_ERR&amp;tiStatoRisoluz=NON_RISOLTO&amp;flVerif=0&amp;pagina=SES_RANGE_DT"><slf:field name="<%= MonitoraggioForm.RiepilogoInviiOggettiFalliti.NI_OGG_FALL_NORIS_NOVER_7%>" controlWidth="w100"/></a></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;tipoErrore=INVIO_OGGETTO&amp;periodo=TUTTI&amp;tiStato=CHIUSO_ERR&amp;tiStatoRisoluz=NON_RISOLTO&amp;flVerif=0&amp;pagina=SES_RANGE_DT"><slf:field name="<%= MonitoraggioForm.RiepilogoInviiOggettiFalliti.NI_OGG_FALL_NORIS_NOVER_TOT%>" controlWidth="w100"/></a></td>
                    </tr>
                </tbody>
            </table>
            <sl:newLine skipLine="true"/>
            <sl:newLine skipLine="true"/>
            <%-- Tabella RIEPILOGO NOTIFICHE FILE FALLITE costruita "a manoni" --%>
            <table class="grid" >
                <caption>
                    <div class="livello1"><b>RIEPILOGO NOTIFICHE FILE FALLITE</b></div>
                </caption>
                <thead>
                    <tr>
                        <th></th>
                        <th></th>
                        <th>&nbsp;&nbsp;&nbsp;&nbsp;Oggi&nbsp;&nbsp;&nbsp;&nbsp;</th>
                        <th>&nbsp;&nbsp;&nbsp;&nbsp;Ultimi 7 giorni&nbsp;&nbsp;&nbsp;&nbsp;</th>
                        <th>&nbsp;&nbsp;&nbsp;&nbsp;Totale&nbsp;&nbsp;&nbsp;&nbsp;</th>
                    </tr>
                </thead>
                <tbody class="livello2">
                    <tr>
                        <slf:tableSection name="<%= MonitoraggioForm.NotificheFileFallite.NAME%>" />
                        <td><b>Totale</b></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;tipoErrore=NOTIFICA_FILE&amp;periodo=OGGI&amp;tiStato=CHIUSO_ERR_NOTIF&amp;pagina=SES_RANGE_DT"><slf:field name="<%= MonitoraggioForm.RiepilogoNotificheFileFallite.NI_NOT_FILE_TOT_CORR%>" controlWidth="w100"/></a></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;tipoErrore=NOTIFICA_FILE&amp;periodo=ULTIMI7&amp;tiStato=CHIUSO_ERR_NOTIF&amp;pagina=SES_RANGE_DT"><slf:field name="<%= MonitoraggioForm.RiepilogoNotificheFileFallite.NI_NOT_FILE_TOT_7%>" controlWidth="w100"/></a></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;tipoErrore=NOTIFICA_FILE&amp;periodo=TUTTI&amp;tiStato=CHIUSO_ERR_NOTIF&amp;pagina=SES_RANGE_DT"><slf:field name="<%= MonitoraggioForm.RiepilogoNotificheFileFallite.NI_NOT_FILE_TOT_TOT%>" controlWidth="w100"/></a></td>
                    </tr>
                    <tr class="nascondiRiga">
                        <td></td>
                        <td><b>Risolte</b></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;tipoErrore=NOTIFICA_FILE&amp;periodo=OGGI&amp;tiStato=CHIUSO_ERR_NOTIF&amp;tiStatoRisoluz=RISOLTO&amp;pagina=SES_RANGE_DT"><slf:field name="<%= MonitoraggioForm.RiepilogoNotificheFileFallite.NI_NOT_FILE_RIS_VER_CORR%>" controlWidth="w100"/></a></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;tipoErrore=NOTIFICA_FILE&amp;periodo=ULTIMI7&amp;tiStato=CHIUSO_ERR_NOTIF&amp;tiStatoRisoluz=RISOLTO&amp;pagina=SES_RANGE_DT"><slf:field name="<%= MonitoraggioForm.RiepilogoNotificheFileFallite.NI_NOT_FILE_RIS_VER_7%>" controlWidth="w100"/></a></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;tipoErrore=NOTIFICA_FILE&amp;periodo=TUTTI&amp;tiStato=CHIUSO_ERR_NOTIF&amp;tiStatoRisoluz=RISOLTO&amp;pagina=SES_RANGE_DT"><slf:field name="<%= MonitoraggioForm.RiepilogoNotificheFileFallite.NI_NOT_FILE_RIS_VER_TOT%>" controlWidth="w100"/></a></td>
                    </tr>
                    <tr class="nascondiRiga">
                        <td></td>
                        <td><b>In corso di risoluzione</b></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;tipoErrore=NOTIFICA_FILE&amp;periodo=OGGI&amp;tiStato=CHIUSO_ERR_NOTIF&amp;tiStatoRisoluz=IN_CORSO&amp;pagina=SES_RANGE_DT"><slf:field name="<%= MonitoraggioForm.RiepilogoNotificheFileFallite.NI_NOT_FILE_CORSO_RIS_VER_CORR%>" controlWidth="w100"/></a></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;tipoErrore=NOTIFICA_FILE&amp;periodo=ULTIMI7&amp;tiStato=CHIUSO_ERR_NOTIF&amp;tiStatoRisoluz=IN_CORSO&amp;pagina=SES_RANGE_DT"><slf:field name="<%= MonitoraggioForm.RiepilogoNotificheFileFallite.NI_NOT_FILE_CORSO_RIS_VER_7%>" controlWidth="w100"/></a></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;tipoErrore=NOTIFICA_FILE&amp;periodo=TUTTI&amp;tiStato=CHIUSO_ERR_NOTIF&amp;tiStatoRisoluz=IN_CORSO&amp;pagina=SES_RANGE_DT"><slf:field name="<%= MonitoraggioForm.RiepilogoNotificheFileFallite.NI_NOT_FILE_CORSO_RIS_VER_TOT%>" controlWidth="w100"/></a></td>
                    </tr>
                    <tr class="nascondiRiga">
                        <td></td>
                        <td><b>Non risolubili</b></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;tipoErrore=NOTIFICA_FILE&amp;periodo=OGGI&amp;tiStato=CHIUSO_ERR_NOTIF&amp;tiStatoRisoluz=NON_RISOLTO&amp;flVerif=1&amp;flNonRisolub=1&amp;pagina=SES_RANGE_DT"><slf:field name="<%= MonitoraggioForm.RiepilogoNotificheFileFallite.NI_NOT_FILE_NORISOLUB_CORR%>" controlWidth="w100"/></a></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;tipoErrore=NOTIFICA_FILE&amp;periodo=ULTIMI7&amp;tiStato=CHIUSO_ERR_NOTIF&amp;tiStatoRisoluz=NON_RISOLTO&amp;flVerif=1&amp;flNonRisolub=1&amp;pagina=SES_RANGE_DT"><slf:field name="<%= MonitoraggioForm.RiepilogoNotificheFileFallite.NI_NOT_FILE_NORISOLUB_7%>" controlWidth="w100"/></a></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;tipoErrore=NOTIFICA_FILE&amp;periodo=TUTTI&amp;tiStato=CHIUSO_ERR_NOTIF&amp;tiStatoRisoluz=NON_RISOLTO&amp;flVerif=1&amp;flNonRisolub=1&amp;pagina=SES_RANGE_DT"><slf:field name="<%= MonitoraggioForm.RiepilogoNotificheFileFallite.NI_NOT_FILE_NORISOLUB_TOT%>" controlWidth="w100"/></a></td>
                    </tr>
                    <tr class="nascondiRiga">
                        <td></td>
                        <td><b>Non risolte e verificate</b></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;tipoErrore=NOTIFICA_FILE&amp;periodo=OGGI&amp;tiStato=CHIUSO_ERR_NOTIF&amp;tiStatoRisoluz=NON_RISOLTO&amp;flVerif=1&amp;flNonRisolub=0&amp;pagina=SES_RANGE_DT"><slf:field name="<%= MonitoraggioForm.RiepilogoNotificheFileFallite.NI_NOT_FILE_NORIS_VER_CORR%>" controlWidth="w100"/></a></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;tipoErrore=NOTIFICA_FILE&amp;periodo=ULTIMI7&amp;tiStato=CHIUSO_ERR_NOTIF&amp;tiStatoRisoluz=NON_RISOLTO&amp;flVerif=1&amp;flNonRisolub=0&amp;pagina=SES_RANGE_DT"><slf:field name="<%= MonitoraggioForm.RiepilogoNotificheFileFallite.NI_NOT_FILE_NORIS_VER_7%>" controlWidth="w100"/></a></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;tipoErrore=NOTIFICA_FILE&amp;periodo=TUTTI&amp;tiStato=CHIUSO_ERR_NOTIF&amp;tiStatoRisoluz=NON_RISOLTO&amp;flVerif=1&amp;flNonRisolub=0&amp;pagina=SES_RANGE_DT"><slf:field name="<%= MonitoraggioForm.RiepilogoNotificheFileFallite.NI_NOT_FILE_NORIS_VER_TOT%>" controlWidth="w100"/></a></td>
                    </tr>
                    <tr class="nascondiRiga">
                        <td></td>
                        <td><b>Non risolte e non verificate</b></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;tipoErrore=NOTIFICA_FILE&amp;periodo=OGGI&amp;tiStato=CHIUSO_ERR_NOTIF&amp;tiStatoRisoluz=NON_RISOLTO&amp;flVerif=0&amp;pagina=SES_RANGE_DT"><slf:field name="<%= MonitoraggioForm.RiepilogoNotificheFileFallite.NI_NOT_FILE_NORIS_NOVER_CORR%>" controlWidth="w100"/></a></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;tipoErrore=NOTIFICA_FILE&amp;periodo=ULTIMI7&amp;tiStato=CHIUSO_ERR_NOTIF&amp;tiStatoRisoluz=NON_RISOLTO&amp;flVerif=0&amp;pagina=SES_RANGE_DT"><slf:field name="<%= MonitoraggioForm.RiepilogoNotificheFileFallite.NI_NOT_FILE_NORIS_NOVER_7%>" controlWidth="w100"/></a></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;tipoErrore=NOTIFICA_FILE&amp;periodo=TUTTI&amp;tiStato=CHIUSO_ERR_NOTIF&amp;tiStatoRisoluz=NON_RISOLTO&amp;flVerif=0&amp;pagina=SES_RANGE_DT"><slf:field name="<%= MonitoraggioForm.RiepilogoNotificheFileFallite.NI_NOT_FILE_NORIS_NOVER_TOT%>" controlWidth="w100"/></a></td>
                    </tr>
                </tbody>
            </table>
            <sl:newLine skipLine="true"/>
            <sl:newLine skipLine="true"/>
            <%-- Tabella RIEPILOGO PREPARAZIONI XML FALLITE costruita "a manoni" --%>
            <table class="grid" >
                <caption>
                    <div class="livello1"><b>RIEPILOGO PREPARAZIONI XML FALLITE</b></div>
                </caption>
                <thead>
                    <tr>
                        <th></th>
                        <th></th>
                        <th>&nbsp;&nbsp;&nbsp;&nbsp;Oggi&nbsp;&nbsp;&nbsp;&nbsp;</th>
                        <th>&nbsp;&nbsp;&nbsp;&nbsp;Ultimi 7 giorni&nbsp;&nbsp;&nbsp;&nbsp;</th>
                        <th>&nbsp;&nbsp;&nbsp;&nbsp;Totale&nbsp;&nbsp;&nbsp;&nbsp;</th>
                    </tr>
                </thead>
                <tbody class="livello2">
                    <tr>
                        <slf:tableSection name="<%= MonitoraggioForm.PreparazioniXmlFallite.NAME%>" />
                        <td><b>Totale</b></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;tipoErrore=PREPARAZIONE_XML&amp;periodo=OGGI&amp;tiStato=CHIUSO_ERR_SCHED&amp;pagina=SES_RANGE_DT"><slf:field name="<%= MonitoraggioForm.RiepilogoPreparazioniXMLFallite.NI_PREP_XML_FALL_TOT_CORR%>" controlWidth="w100"/></a></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;tipoErrore=PREPARAZIONE_XML&amp;periodo=ULTIMI7&amp;tiStato=CHIUSO_ERR_SCHED&amp;pagina=SES_RANGE_DT"><slf:field name="<%= MonitoraggioForm.RiepilogoPreparazioniXMLFallite.NI_PREP_XML_FALL_TOT_7%>" controlWidth="w100"/></a></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;tipoErrore=PREPARAZIONE_XML&amp;periodo=TUTTI&amp;tiStato=CHIUSO_ERR_SCHED&amp;pagina=SES_RANGE_DT"><slf:field name="<%= MonitoraggioForm.RiepilogoPreparazioniXMLFallite.NI_PREP_XML_FALL_TOT_TOT%>" controlWidth="w100"/></a></td>
                    </tr>
                    <tr class="nascondiRiga">
                        <td></td> 
                        <td><b>Risolte</b></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;tipoErrore=PREPARAZIONE_XML&amp;periodo=OGGI&amp;tiStato=CHIUSO_ERR_SCHED&amp;tiStatoRisoluz=RISOLTO&amp;pagina=SES_RANGE_DT"><slf:field name="<%= MonitoraggioForm.RiepilogoPreparazioniXMLFallite.NI_PREP_XML_FALL_RIS_VER_CORR%>" controlWidth="w100"/></a></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;tipoErrore=PREPARAZIONE_XML&amp;periodo=ULTIMI7&amp;tiStato=CHIUSO_ERR_SCHED&amp;tiStatoRisoluz=RISOLTO&amp;pagina=SES_RANGE_DT"><slf:field name="<%= MonitoraggioForm.RiepilogoPreparazioniXMLFallite.NI_PREP_XML_FALL_RIS_VER_7%>" controlWidth="w100"/></a></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;tipoErrore=PREPARAZIONE_XML&amp;periodo=TUTTI&amp;tiStato=CHIUSO_ERR_SCHED&amp;tiStatoRisoluz=RISOLTO&amp;pagina=SES_RANGE_DT"><slf:field name="<%= MonitoraggioForm.RiepilogoPreparazioniXMLFallite.NI_PREP_XML_FALL_RIS_VER_TOT%>" controlWidth="w100"/></a></td>
                    </tr>
                    <tr class="nascondiRiga">
                        <td></td>
                        <td><b>In corso di risoluzione</b></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;tipoErrore=PREPARAZIONE_XML&amp;periodo=OGGI&amp;tiStato=CHIUSO_ERR_SCHED&amp;tiStatoRisoluz=IN_CORSO&amp;pagina=SES_RANGE_DT"><slf:field name="<%= MonitoraggioForm.RiepilogoPreparazioniXMLFallite.NI_PREP_XML_FALL_CORSO_RIS_VER_CORR%>" controlWidth="w100"/></a></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;tipoErrore=PREPARAZIONE_XML&amp;periodo=ULTIMI7&amp;tiStato=CHIUSO_ERR_SCHED&amp;tiStatoRisoluz=IN_CORSO&amp;&amp;pagina=SES_RANGE_DT"><slf:field name="<%= MonitoraggioForm.RiepilogoPreparazioniXMLFallite.NI_PREP_XML_FALL_CORSO_RIS_VER_7%>" controlWidth="w100"/></a></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;tipoErrore=PREPARAZIONE_XML&amp;periodo=TUTTI&amp;tiStato=CHIUSO_ERR_SCHED&amp;tiStatoRisoluz=IN_CORSO&amp;pagina=SES_RANGE_DT"><slf:field name="<%= MonitoraggioForm.RiepilogoPreparazioniXMLFallite.NI_PREP_XML_FALL_CORSO_RIS_VER_TOT%>" controlWidth="w100"/></a></td>
                    </tr>
                    <tr class="nascondiRiga">
                        <td></td>
                        <td><b>Non risolubili</b></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;tipoErrore=PREPARAZIONE_XML&amp;periodo=OGGI&amp;tiStato=CHIUSO_ERR_SCHED&amp;tiStatoRisoluz=NON_RISOLTO&amp;flVerif=1&amp;flNonRisolub=1&amp;pagina=SES_RANGE_DT"><slf:field name="<%= MonitoraggioForm.RiepilogoPreparazioniXMLFallite.NI_PREP_XML_FALL_NORISOLUB_CORR%>" controlWidth="w100"/></a></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;tipoErrore=PREPARAZIONE_XML&amp;periodo=ULTIMI7&amp;tiStato=CHIUSO_ERR_SCHED&amp;tiStatoRisoluz=NON_RISOLTO&amp;flVerif=1&amp;flNonRisolub=1&amp;pagina=SES_RANGE_DT"><slf:field name="<%= MonitoraggioForm.RiepilogoPreparazioniXMLFallite.NI_PREP_XML_FALL_NORISOLUB_7%>" controlWidth="w100"/></a></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;tipoErrore=PREPARAZIONE_XML&amp;periodo=TUTTI&amp;tiStato=CHIUSO_ERR_SCHED&amp;tiStatoRisoluz=NON_RISOLTO&amp;flVerif=1&amp;flNonRisolub=1&amp;pagina=SES_RANGE_DT"><slf:field name="<%= MonitoraggioForm.RiepilogoPreparazioniXMLFallite.NI_PREP_XML_FALL_NORISOLUB_TOT%>" controlWidth="w100"/></a></td>
                    </tr>
                    <tr class="nascondiRiga">
                        <td></td>
                        <td><b>Non risolte e verificate</b></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;tipoErrore=PREPARAZIONE_XML&amp;periodo=OGGI&amp;tiStato=CHIUSO_ERR_SCHED&amp;tiStatoRisoluz=NON_RISOLTO&amp;flVerif=1&amp;flNonRisolub=0&amp;pagina=SES_RANGE_DT"><slf:field name="<%= MonitoraggioForm.RiepilogoPreparazioniXMLFallite.NI_PREP_XML_FALL_NORIS_VER_CORR%>" controlWidth="w100"/></a></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;tipoErrore=PREPARAZIONE_XML&amp;periodo=ULTIMI7&amp;tiStato=CHIUSO_ERR_SCHED&amp;tiStatoRisoluz=NON_RISOLTO&amp;flVerif=1&amp;flNonRisolub=0&amp;pagina=SES_RANGE_DT"><slf:field name="<%= MonitoraggioForm.RiepilogoPreparazioniXMLFallite.NI_PREP_XML_FALL_NORIS_VER_7%>" controlWidth="w100"/></a></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;tipoErrore=PREPARAZIONE_XML&amp;periodo=TUTTI&amp;tiStato=CHIUSO_ERR_SCHED&amp;tiStatoRisoluz=NON_RISOLTO&amp;flVerif=1&amp;flNonRisolub=0&amp;pagina=SES_RANGE_DT"><slf:field name="<%= MonitoraggioForm.RiepilogoPreparazioniXMLFallite.NI_PREP_XML_FALL_NORIS_VER_TOT%>" controlWidth="w100"/></a></td>
                    </tr>
                    <tr class="nascondiRiga">
                        <td></td>
                        <td><b>Non risolte e non verificate</b></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;tipoErrore=PREPARAZIONE_XML&amp;periodo=OGGI&amp;tiStato=CHIUSO_ERR_SCHED&amp;tiStatoRisoluz=NON_RISOLTO&amp;flVerif=0&amp;pagina=SES_RANGE_DT"><slf:field name="<%= MonitoraggioForm.RiepilogoPreparazioniXMLFallite.NI_PREP_XML_FALL_NORIS_NOVER_CORR%>" controlWidth="w100"/></a></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;tipoErrore=PREPARAZIONE_XML&amp;periodo=ULTIMI7&amp;tiStato=CHIUSO_ERR_SCHED&amp;tiStatoRisoluz=NON_RISOLTO&amp;flVerif=0&amp;pagina=SES_RANGE_DT"><slf:field name="<%= MonitoraggioForm.RiepilogoPreparazioniXMLFallite.NI_PREP_XML_FALL_NORIS_NOVER_7%>" controlWidth="w100"/></a></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;tipoErrore=PREPARAZIONE_XML&amp;periodo=TUTTI&amp;tiStato=CHIUSO_ERR_SCHED&amp;tiStatoRisoluz=NON_RISOLTO&amp;flVerif=0&amp;pagina=SES_RANGE_DT"><slf:field name="<%= MonitoraggioForm.RiepilogoPreparazioniXMLFallite.NI_PREP_XML_FALL_NORIS_NOVER_TOT%>" controlWidth="w100"/></a></td>
                    </tr>
                </tbody>
            </table>
            <sl:newLine skipLine="true"/>
            <sl:newLine skipLine="true"/>
            <%-- Tabella RIEPILOGO REGISTRAZIONI IN CODA FALLITE costruita "a manoni" --%>
            <table class="grid" >
                <caption>
                    <div class="livello1"><b>RIEPILOGO REGISTRAZIONI IN CODA FALLITE</b></div>
                </caption>
                <thead>
                    <tr>
                        <th></th>
                        <th></th>
                        <th>&nbsp;&nbsp;&nbsp;&nbsp;Oggi&nbsp;&nbsp;&nbsp;&nbsp;</th>
                        <th>&nbsp;&nbsp;&nbsp;&nbsp;Ultimi 7 giorni&nbsp;&nbsp;&nbsp;&nbsp;</th>
                        <th>&nbsp;&nbsp;&nbsp;&nbsp;Totale&nbsp;&nbsp;&nbsp;&nbsp;</th>
                    </tr>
                </thead>
                <tbody class="livello2">
                    <tr>
                        <slf:tableSection name="<%= MonitoraggioForm.RegistrazioniInCodaFallite.NAME%>" />
                        <td><b>Totale</b></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;tipoErrore=REGISTRAZIONE_IN_CODA&amp;periodo=OGGI&amp;tiStato=CHIUSO_ERR_CODA&amp;pagina=SES_RANGE_DT"><slf:field name="<%= MonitoraggioForm.RiepilogoRegistrazioniCodaFallite.NI_REG_CODA_FALL_TOT_CORR%>" controlWidth="w100"/></a></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;tipoErrore=REGISTRAZIONE_IN_CODA&amp;periodo=ULTIMI7&amp;tiStato=CHIUSO_ERR_CODA&amp;pagina=SES_RANGE_DT"><slf:field name="<%= MonitoraggioForm.RiepilogoRegistrazioniCodaFallite.NI_REG_CODA_FALL_TOT_7%>" controlWidth="w100"/></a></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;tipoErrore=REGISTRAZIONE_IN_CODA&amp;periodo=TUTTI&amp;tiStato=CHIUSO_ERR_CODA&amp;pagina=SES_RANGE_DT"><slf:field name="<%= MonitoraggioForm.RiepilogoRegistrazioniCodaFallite.NI_REG_CODA_FALL_TOT_TOT%>" controlWidth="w100"/></a></td>
                    </tr>
                    <tr class="nascondiRiga">
                        <td></td>
                        <td><b>Risolte</b></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;tipoErrore=REGISTRAZIONE_IN_CODA&amp;periodo=OGGI&amp;tiStato=CHIUSO_ERR_CODA&amp;tiStatoRisoluz=RISOLTO&amp;pagina=SES_RANGE_DT"><slf:field name="<%= MonitoraggioForm.RiepilogoRegistrazioniCodaFallite.NI_REG_CODA_FALL_RIS_VER_CORR%>" controlWidth="w100"/></a></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;tipoErrore=REGISTRAZIONE_IN_CODA&amp;periodo=ULTIMI7&amp;tiStato=CHIUSO_ERR_CODA&amp;tiStatoRisoluz=RISOLTO&amp;pagina=SES_RANGE_DT"><slf:field name="<%= MonitoraggioForm.RiepilogoRegistrazioniCodaFallite.NI_REG_CODA_FALL_RIS_VER_7%>" controlWidth="w100"/></a></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;tipoErrore=REGISTRAZIONE_IN_CODA&amp;periodo=TUTTI&amp;tiStato=CHIUSO_ERR_CODA&amp;tiStatoRisoluz=RISOLTO&amp;pagina=SES_RANGE_DT"><slf:field name="<%= MonitoraggioForm.RiepilogoRegistrazioniCodaFallite.NI_REG_CODA_FALL_RIS_VER_TOT%>" controlWidth="w100"/></a></td>
                    </tr>
                    <tr class="nascondiRiga">
                        <td></td>
                        <td><b>In corso di risoluzione</b></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;tipoErrore=REGISTRAZIONE_IN_CODA&amp;periodo=OGGI&amp;tiStato=CHIUSO_ERR_CODA&amp;tiStatoRisoluz=IN_CORSO&amp;pagina=SES_RANGE_DT"><slf:field name="<%= MonitoraggioForm.RiepilogoRegistrazioniCodaFallite.NI_REG_CODA_FALL_CORSO_RIS_VER_CORR%>" controlWidth="w100"/></a></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;tipoErrore=REGISTRAZIONE_IN_CODA&amp;periodo=ULTIMI7&amp;tiStato=CHIUSO_ERR_CODA&amp;tiStatoRisoluz=IN_CORSO&amp;pagina=SES_RANGE_DT"><slf:field name="<%= MonitoraggioForm.RiepilogoRegistrazioniCodaFallite.NI_REG_CODA_FALL_CORSO_RIS_VER_7%>" controlWidth="w100"/></a></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;tipoErrore=REGISTRAZIONE_IN_CODA&amp;periodo=TUTTI&amp;tiStato=CHIUSO_ERR_CODA&amp;tiStatoRisoluz=IN_CORSO&amp;pagina=SES_RANGE_DT"><slf:field name="<%= MonitoraggioForm.RiepilogoRegistrazioniCodaFallite.NI_REG_CODA_FALL_CORSO_RIS_VER_TOT%>" controlWidth="w100"/></a></td>
                    </tr>
                    <tr class="nascondiRiga">
                        <td></td>
                        <td><b>Non risolubili</b></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;tipoErrore=REGISTRAZIONE_IN_CODA&amp;periodo=OGGI&amp;tiStato=CHIUSO_ERR_CODA&amp;tiStatoRisoluz=NON_RISOLTO&amp;flVerif=1&amp;flNonRisolub=1&amp;pagina=SES_RANGE_DT"><slf:field name="<%= MonitoraggioForm.RiepilogoRegistrazioniCodaFallite.NI_REG_CODA_FALL_NORISOLUB_CORR%>" controlWidth="w100"/></a></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;tipoErrore=REGISTRAZIONE_IN_CODA&amp;periodo=ULTIMI7&amp;tiStato=CHIUSO_ERR_CODA&amp;tiStatoRisoluz=NON_RISOLTO&amp;flVerif=1&amp;flNonRisolub=1&amp;pagina=SES_RANGE_DT"><slf:field name="<%= MonitoraggioForm.RiepilogoRegistrazioniCodaFallite.NI_REG_CODA_FALL_NORISOLUB_7%>" controlWidth="w100"/></a></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;tipoErrore=REGISTRAZIONE_IN_CODA&amp;periodo=TUTTI&amp;tiStato=CHIUSO_ERR_CODA&amp;tiStatoRisoluz=NON_RISOLTO&amp;flVerif=1&amp;flNonRisolub=1&amp;pagina=SES_RANGE_DT"><slf:field name="<%= MonitoraggioForm.RiepilogoRegistrazioniCodaFallite.NI_REG_CODA_FALL_NORISOLUB_TOT%>" controlWidth="w100"/></a></td>
                    </tr>
                    <tr class="nascondiRiga">
                        <td></td>
                        <td><b>Non risolte e verificate</b></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;tipoErrore=REGISTRAZIONE_IN_CODA&amp;periodo=OGGI&amp;tiStato=CHIUSO_ERR_CODA&amp;tiStatoRisoluz=NON_RISOLTO&amp;flVerif=1&amp;flNonRisolub=0&amp;pagina=SES_RANGE_DT"><slf:field name="<%= MonitoraggioForm.RiepilogoRegistrazioniCodaFallite.NI_REG_CODA_FALL_NORIS_VER_CORR%>" controlWidth="w100"/></a></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;tipoErrore=REGISTRAZIONE_IN_CODA&amp;periodo=ULTIMI7&amp;tiStato=CHIUSO_ERR_CODA&amp;tiStatoRisoluz=NON_RISOLTO&amp;flVerif=1&amp;flNonRisolub=0&amp;pagina=SES_RANGE_DT"><slf:field name="<%= MonitoraggioForm.RiepilogoRegistrazioniCodaFallite.NI_REG_CODA_FALL_NORIS_VER_7%>" controlWidth="w100"/></a></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;tipoErrore=REGISTRAZIONE_IN_CODA&amp;periodo=TUTTI&amp;tiStato=CHIUSO_ERR_CODA&amp;tiStatoRisoluz=NON_RISOLTO&amp;flVerif=1&amp;flNonRisolub=0&amp;pagina=SES_RANGE_DT"><slf:field name="<%= MonitoraggioForm.RiepilogoRegistrazioniCodaFallite.NI_REG_CODA_FALL_NORIS_VER_TOT%>" controlWidth="w100"/></a></td>
                    </tr>
                    <tr class="nascondiRiga">
                        <td></td>
                        <td><b>Non risolte e non verificate</b></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;tipoErrore=REGISTRAZIONE_IN_CODA&amp;periodo=OGGI&amp;tiStato=CHIUSO_ERR_CODA&amp;tiStatoRisoluz=NON_RISOLTO&amp;flVerif=0&amp;pagina=SES_RANGE_DT"><slf:field name="<%= MonitoraggioForm.RiepilogoRegistrazioniCodaFallite.NI_REG_CODA_FALL_NORIS_NOVER_CORR%>" controlWidth="w100"/></a></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;tipoErrore=REGISTRAZIONE_IN_CODA&amp;periodo=ULTIMI7&amp;tiStato=CHIUSO_ERR_CODA&amp;tiStatoRisoluz=NON_RISOLTO&amp;flVerif=0&amp;pagina=SES_RANGE_DT"><slf:field name="<%= MonitoraggioForm.RiepilogoRegistrazioniCodaFallite.NI_REG_CODA_FALL_NORIS_NOVER_7%>" controlWidth="w100"/></a></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;tipoErrore=REGISTRAZIONE_IN_CODA&amp;periodo=TUTTI&amp;tiStato=CHIUSO_ERR_CODA&amp;tiStatoRisoluz=NON_RISOLTO&amp;flVerif=0&amp;pagina=SES_RANGE_DT"><slf:field name="<%= MonitoraggioForm.RiepilogoRegistrazioniCodaFallite.NI_REG_CODA_FALL_NORIS_NOVER_TOT%>" controlWidth="w100"/></a></td>
                    </tr>
                </tbody>
            </table>
            <sl:newLine skipLine="true"/>
            <sl:newLine skipLine="true"/>
            <%-- Tabella RIEPILOGO VERSAMENTI A SACER FALLITI costruita "a manoni" --%>
            <table class="grid" >
                <caption>
                    <div class="livello1"><b>RIEPILOGO VERSAMENTI A SACER FALLITI</b></div>
                </caption>
                <thead>
                    <tr>
                        <th></th>
                        <th></th>
                        <th>&nbsp;&nbsp;&nbsp;&nbsp;Oggi&nbsp;&nbsp;&nbsp;&nbsp;</th>
                        <th>&nbsp;&nbsp;&nbsp;&nbsp;Ultimi 7 giorni&nbsp;&nbsp;&nbsp;&nbsp;</th>
                        <th>&nbsp;&nbsp;&nbsp;&nbsp;Totale&nbsp;&nbsp;&nbsp;&nbsp;</th>
                    </tr>
                </thead>
                <tbody class="livello2">
                    <tr>
                        <slf:tableSection name="<%= MonitoraggioForm.VersamentiASacerFalliti.NAME%>" />
                        <td><b>Totale</b></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;tipoErrore=VERSAMENTO_SACER&amp;periodo=OGGI&amp;tiStato=CHIUSO_ERR_VERS&amp;tiStato=CHIUSO_ERR_RECUPERABILE&amp;pagina=SES_RANGE_DT"><slf:field name="<%= MonitoraggioForm.RiepilogoVersamentiSacerFalliti.NI_VERS_SACER_FALL_TOT_CORR%>" controlWidth="w100"/></a></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;tipoErrore=VERSAMENTO_SACER&amp;periodo=ULTIMI7&amp;tiStato=CHIUSO_ERR_VERS&amp;tiStato=CHIUSO_ERR_RECUPERABILE&amp;pagina=SES_RANGE_DT"><slf:field name="<%= MonitoraggioForm.RiepilogoVersamentiSacerFalliti.NI_VERS_SACER_FALL_TOT_7%>" controlWidth="w100"/></a></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;tipoErrore=VERSAMENTO_SACER&amp;periodo=TUTTI&amp;tiStato=CHIUSO_ERR_VERS&amp;tiStato=CHIUSO_ERR_RECUPERABILE&amp;pagina=SES_RANGE_DT"><slf:field name="<%= MonitoraggioForm.RiepilogoVersamentiSacerFalliti.NI_VERS_SACER_FALL_TOT_TOT%>" controlWidth="w100"/></a></td>
                    </tr>
                    <tr class="nascondiRiga">
                        <td></td>
                        <td><b>Risolti</b></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;tipoErrore=VERSAMENTO_SACER&amp;periodo=OGGI&amp;tiStato=CHIUSO_ERR_VERS&amp;tiStato=CHIUSO_ERR_RECUPERABILE&amp;tiStatoRisoluz=RISOLTO&amp;pagina=SES_RANGE_DT"><slf:field name="<%= MonitoraggioForm.RiepilogoVersamentiSacerFalliti.NI_VERS_SACER_FALL_RIS_VER_CORR%>" controlWidth="w100"/></a></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;tipoErrore=VERSAMENTO_SACER&amp;periodo=ULTIMI7&amp;tiStato=CHIUSO_ERR_VERS&amp;tiStato=CHIUSO_ERR_RECUPERABILE&amp;tiStatoRisoluz=RISOLTO&amp;pagina=SES_RANGE_DT"><slf:field name="<%= MonitoraggioForm.RiepilogoVersamentiSacerFalliti.NI_VERS_SACER_FALL_RIS_VER_7%>" controlWidth="w100"/></a></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;tipoErrore=VERSAMENTO_SACER&amp;periodo=TUTTI&amp;tiStato=CHIUSO_ERR_VERS&amp;tiStato=CHIUSO_ERR_RECUPERABILE&amp;tiStatoRisoluz=RISOLTO&amp;pagina=SES_RANGE_DT"><slf:field name="<%= MonitoraggioForm.RiepilogoVersamentiSacerFalliti.NI_VERS_SACER_FALL_RIS_VER_TOT%>" controlWidth="w100"/></a></td>
                    </tr>
                    <tr class="nascondiRiga">
                        <td></td>
                        <td><b>In corso di risoluzione</b></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;tipoErrore=VERSAMENTO_SACER&amp;periodo=OGGI&amp;tiStato=CHIUSO_ERR_VERS&amp;tiStato=CHIUSO_ERR_RECUPERABILE&amp;tiStatoRisoluz=IN_CORSO&amp;pagina=SES_RANGE_DT"><slf:field name="<%= MonitoraggioForm.RiepilogoVersamentiSacerFalliti.NI_VERS_SACER_FALL_CORSO_RIS_VER_CORR%>" controlWidth="w100"/></a></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;tipoErrore=VERSAMENTO_SACER&amp;periodo=ULTIMI7&amp;tiStato=CHIUSO_ERR_VERS&amp;tiStato=CHIUSO_ERR_RECUPERABILE&amp;tiStatoRisoluz=IN_CORSO&amp;pagina=SES_RANGE_DT"><slf:field name="<%= MonitoraggioForm.RiepilogoVersamentiSacerFalliti.NI_VERS_SACER_FALL_CORSO_RIS_VER_7%>" controlWidth="w100"/></a></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;tipoErrore=VERSAMENTO_SACER&amp;periodo=TUTTI&amp;tiStato=CHIUSO_ERR_VERS&amp;tiStato=CHIUSO_ERR_RECUPERABILE&amp;tiStatoRisoluz=IN_CORSO&amp;pagina=SES_RANGE_DT"><slf:field name="<%= MonitoraggioForm.RiepilogoVersamentiSacerFalliti.NI_VERS_SACER_FALL_CORSO_RIS_VER_TOT%>" controlWidth="w100"/></a></td>
                    </tr>
                    <tr class="nascondiRiga">
                        <td></td>
                        <td><b>Non risolubili</b></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;tipoErrore=VERSAMENTO_SACER&amp;periodo=OGGI&amp;tiStato=CHIUSO_ERR_VERS&amp;tiStato=CHIUSO_ERR_RECUPERABILE&amp;tiStatoRisoluz=NON_RISOLTO&amp;flVerif=1&amp;flNonRisolub=1&amp;pagina=SES_RANGE_DT"><slf:field name="<%= MonitoraggioForm.RiepilogoVersamentiSacerFalliti.NI_VERS_SACER_FALL_NORISOLUB_CORR%>" controlWidth="w100"/></a></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;tipoErrore=VERSAMENTO_SACER&amp;periodo=ULTIMI7&amp;tiStato=CHIUSO_ERR_VERS&amp;tiStato=CHIUSO_ERR_RECUPERABILE&amp;tiStatoRisoluz=NON_RISOLTO&amp;flVerif=1&amp;flNonRisolub=1&amp;pagina=SES_RANGE_DT"><slf:field name="<%= MonitoraggioForm.RiepilogoVersamentiSacerFalliti.NI_VERS_SACER_FALL_NORISOLUB_7%>" controlWidth="w100"/></a></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;tipoErrore=VERSAMENTO_SACER&amp;periodo=TUTTI&amp;tiStato=CHIUSO_ERR_VERS&amp;tiStato=CHIUSO_ERR_RECUPERABILE&amp;tiStatoRisoluz=NON_RISOLTO&amp;flVerif=1&amp;flNonRisolub=1&amp;pagina=SES_RANGE_DT"><slf:field name="<%= MonitoraggioForm.RiepilogoVersamentiSacerFalliti.NI_VERS_SACER_FALL_NORISOLUB_TOT%>" controlWidth="w100"/></a></td>
                    </tr>
                    <tr class="nascondiRiga">
                        <td></td>
                        <td><b>Non risolti e verificati</b></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;tipoErrore=VERSAMENTO_SACER&amp;periodo=OGGI&amp;tiStato=CHIUSO_ERR_VERS&amp;tiStato=CHIUSO_ERR_RECUPERABILE&amp;tiStatoRisoluz=NON_RISOLTO&amp;flVerif=1&amp;flNonRisolub=0&amp;pagina=SES_RANGE_DT"><slf:field name="<%= MonitoraggioForm.RiepilogoVersamentiSacerFalliti.NI_VERS_SACER_FALL_NORIS_VER_CORR%>" controlWidth="w100"/></a></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;tipoErrore=VERSAMENTO_SACER&amp;periodo=ULTIMI7&amp;tiStato=CHIUSO_ERR_VERS&amp;tiStato=CHIUSO_ERR_RECUPERABILE&amp;tiStatoRisoluz=NON_RISOLTO&amp;flVerif=1&amp;flNonRisolub=0&amp;pagina=SES_RANGE_DT"><slf:field name="<%= MonitoraggioForm.RiepilogoVersamentiSacerFalliti.NI_VERS_SACER_FALL_NORIS_VER_7%>" controlWidth="w100"/></a></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;tipoErrore=VERSAMENTO_SACER&amp;periodo=TUTTI&amp;tiStato=CHIUSO_ERR_VERS&amp;tiStato=CHIUSO_ERR_RECUPERABILE&amp;tiStatoRisoluz=NON_RISOLTO&amp;flVerif=1&amp;flNonRisolub=0&amp;pagina=SES_RANGE_DT"><slf:field name="<%= MonitoraggioForm.RiepilogoVersamentiSacerFalliti.NI_VERS_SACER_FALL_NORIS_VER_TOT%>" controlWidth="w100"/></a></td>
                    </tr>
                    <tr class="nascondiRiga">
                        <td></td>
                        <td><b>Non risolti e non verificati</b></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;tipoErrore=VERSAMENTO_SACER&amp;periodo=OGGI&amp;tiStato=CHIUSO_ERR_VERS&amp;tiStato=CHIUSO_ERR_RECUPERABILE&amp;tiStatoRisoluz=NON_RISOLTO&amp;flVerif=0&amp;pagina=SES_RANGE_DT"><slf:field name="<%= MonitoraggioForm.RiepilogoVersamentiSacerFalliti.NI_VERS_SACER_FALL_NORIS_NOVER_CORR%>" controlWidth="w100"/></a></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;tipoErrore=VERSAMENTO_SACER&amp;periodo=ULTIMI7&amp;tiStato=CHIUSO_ERR_VERS&amp;tiStato=CHIUSO_ERR_RECUPERABILE&amp;tiStatoRisoluz=NON_RISOLTO&amp;flVerif=0&amp;pagina=SES_RANGE_DT"><slf:field name="<%= MonitoraggioForm.RiepilogoVersamentiSacerFalliti.NI_VERS_SACER_FALL_NORIS_NOVER_7%>" controlWidth="w100"/></a></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;tipoErrore=VERSAMENTO_SACER&amp;periodo=TUTTI&amp;tiStato=CHIUSO_ERR_VERS&amp;tiStato=CHIUSO_ERR_RECUPERABILE&amp;tiStatoRisoluz=NON_RISOLTO&amp;flVerif=0&amp;pagina=SES_RANGE_DT"><slf:field name="<%= MonitoraggioForm.RiepilogoVersamentiSacerFalliti.NI_VERS_SACER_FALL_NORIS_NOVER_TOT%>" controlWidth="w100"/></a></td>
                    </tr>
                </tbody>
            </table>
            <sl:newLine skipLine="true"/>
            <sl:newLine skipLine="true"/>
            <%-- Tabella RIEPILOGO TRASFORMAZIONI FALLITE costruita "a manoni" --%>
            <table class="grid" >
                <caption>
                    <div class="livello1"><b>RIEPILOGO TRASFORMAZIONI FALLITE</b></div>
                </caption>
                <thead>
                    <tr>
                        <th></th>
                        <th></th>
                        <th>&nbsp;&nbsp;&nbsp;&nbsp;Oggi&nbsp;&nbsp;&nbsp;&nbsp;</th>
                        <th>&nbsp;&nbsp;&nbsp;&nbsp;Ultimi 7 giorni&nbsp;&nbsp;&nbsp;&nbsp;</th>
                        <th>&nbsp;&nbsp;&nbsp;&nbsp;Totale&nbsp;&nbsp;&nbsp;&nbsp;</th>
                    </tr>
                </thead>
                <tbody class="livello2">
                    <tr>
                        <slf:tableSection name="<%= MonitoraggioForm.TrasformazioniFallite.NAME%>" />
                        <td><b>Totale</b></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;periodo=OGGI&amp;tiStato=CHIUSO_ERR_TRASFORMAZIONE&amp;pagina=SES_RANGE_DT"><slf:field name="<%= MonitoraggioForm.RiepilogoTrasformazioniFallite.NI_TRASF_FALL_TOT_CORR%>" controlWidth="w100"/></a></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;periodo=ULTIMI7&amp;tiStato=CHIUSO_ERR_TRASFORMAZIONE&amp;pagina=SES_RANGE_DT"><slf:field name="<%= MonitoraggioForm.RiepilogoTrasformazioniFallite.NI_TRASF_FALL_TOT_7%>" controlWidth="w100"/></a></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;periodo=TUTTI&amp;tiStato=CHIUSO_ERR_TRASFORMAZIONE&amp;pagina=SES_RANGE_DT"><slf:field name="<%= MonitoraggioForm.RiepilogoTrasformazioniFallite.NI_TRASF_FALL_TOT_TOT%>" controlWidth="w100"/></a></td>
                    </tr>
                    <tr class="nascondiRiga">
                        <td></td>
                        <td><b>Risolti</b></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;periodo=OGGI&amp;tiStato=CHIUSO_ERR_TRASFORMAZIONE&amp;tiStatoRisoluz=RISOLTO&amp;pagina=SES_RANGE_DT"><slf:field name="<%= MonitoraggioForm.RiepilogoTrasformazioniFallite.NI_TRASF_FALL_RIS_VER_CORR%>" controlWidth="w100"/></a></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;periodo=ULTIMI7&amp;tiStato=CHIUSO_ERR_TRASFORMAZIONE&amp;tiStatoRisoluz=RISOLTO&amp;pagina=SES_RANGE_DT"><slf:field name="<%= MonitoraggioForm.RiepilogoTrasformazioniFallite.NI_TRASF_FALL_RIS_VER_7%>" controlWidth="w100"/></a></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;periodo=TUTTI&amp;tiStato=CHIUSO_ERR_TRASFORMAZIONE&amp;tiStatoRisoluz=RISOLTO&amp;pagina=SES_RANGE_DT"><slf:field name="<%= MonitoraggioForm.RiepilogoTrasformazioniFallite.NI_TRASF_FALL_RIS_VER_TOT%>" controlWidth="w100"/></a></td>
                    </tr>
                    <tr class="nascondiRiga">
                        <td></td>
                        <td><b>In corso di risoluzione</b></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;periodo=OGGI&amp;tiStato=CHIUSO_ERR_TRASFORMAZIONE&amp;tiStatoRisoluz=IN_CORSO&amp;pagina=SES_RANGE_DT"><slf:field name="<%= MonitoraggioForm.RiepilogoTrasformazioniFallite.NI_TRASF_FALL_CORSO_RIS_VER_CORR%>" controlWidth="w100"/></a></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;periodo=ULTIMI7&amp;tiStato=CHIUSO_ERR_TRASFORMAZIONE&amp;tiStatoRisoluz=IN_CORSO&amp;pagina=SES_RANGE_DT"><slf:field name="<%= MonitoraggioForm.RiepilogoTrasformazioniFallite.NI_TRASF_FALL_CORSO_RIS_VER_7%>" controlWidth="w100"/></a></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;periodo=TUTTI&amp;tiStato=CHIUSO_ERR_TRASFORMAZIONE&amp;tiStatoRisoluz=IN_CORSO&amp;pagina=SES_RANGE_DT"><slf:field name="<%= MonitoraggioForm.RiepilogoTrasformazioniFallite.NI_TRASF_FALL_CORSO_RIS_VER_TOT%>" controlWidth="w100"/></a></td>
                    </tr>
                    <tr class="nascondiRiga">
                        <td></td>
                        <td><b>Non risolubili</b></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;periodo=OGGI&amp;tiStato=CHIUSO_ERR_TRASFORMAZIONE&amp;tiStatoRisoluz=NON_RISOLTO&amp;flVerif=1&amp;flNonRisolub=1&amp;pagina=SES_RANGE_DT"><slf:field name="<%= MonitoraggioForm.RiepilogoTrasformazioniFallite.NI_TRASF_FALL_NORISOLUB_CORR%>" controlWidth="w100"/></a></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;periodo=ULTIMI7&amp;tiStato=CHIUSO_ERR_TRASFORMAZIONE&amp;tiStatoRisoluz=NON_RISOLTO&amp;flVerif=1&amp;flNonRisolub=1&amp;pagina=SES_RANGE_DT"><slf:field name="<%= MonitoraggioForm.RiepilogoTrasformazioniFallite.NI_TRASF_FALL_NORISOLUB_7%>" controlWidth="w100"/></a></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;periodo=TUTTI&amp;tiStato=CHIUSO_ERR_TRASFORMAZIONE&amp;tiStatoRisoluz=NON_RISOLTO&amp;flVerif=1&amp;flNonRisolub=1&amp;pagina=SES_RANGE_DT"><slf:field name="<%= MonitoraggioForm.RiepilogoTrasformazioniFallite.NI_TRASF_FALL_NORISOLUB_TOT%>" controlWidth="w100"/></a></td>
                    </tr>
                    <tr class="nascondiRiga">
                        <td></td>
                        <td><b>Non risolti e verificati</b></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;periodo=OGGI&amp;tiStato=CHIUSO_ERR_TRASFORMAZIONE&amp;tiStatoRisoluz=NON_RISOLTO&amp;flVerif=1&amp;flNonRisolub=0&amp;pagina=SES_RANGE_DT"><slf:field name="<%= MonitoraggioForm.RiepilogoTrasformazioniFallite.NI_TRASF_FALL_NORIS_VER_CORR%>" controlWidth="w100"/></a></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;periodo=ULTIMI7&amp;tiStato=CHIUSO_ERR_TRASFORMAZIONE&amp;tiStatoRisoluz=NON_RISOLTO&amp;flVerif=1&amp;flNonRisolub=0&amp;pagina=SES_RANGE_DT"><slf:field name="<%= MonitoraggioForm.RiepilogoTrasformazioniFallite.NI_TRASF_FALL_NORIS_VER_7%>" controlWidth="w100"/></a></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;periodo=TUTTI&amp;tiStato=CHIUSO_ERR_TRASFORMAZIONE&amp;tiStatoRisoluz=NON_RISOLTO&amp;flVerif=1&amp;flNonRisolub=0&amp;pagina=SES_RANGE_DT"><slf:field name="<%= MonitoraggioForm.RiepilogoTrasformazioniFallite.NI_TRASF_FALL_NORIS_VER_TOT%>" controlWidth="w100"/></a></td>
                    </tr>
                    <tr class="nascondiRiga">
                        <td></td>
                        <td><b>Non risolti e non verificati</b></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;periodo=OGGI&amp;tiStato=CHIUSO_ERR_TRASFORMAZIONE&amp;tiStatoRisoluz=NON_RISOLTO&amp;flVerif=0&amp;pagina=SES_RANGE_DT"><slf:field name="<%= MonitoraggioForm.RiepilogoTrasformazioniFallite.NI_TRASF_FALL_NORIS_NOVER_CORR%>" controlWidth="w100"/></a></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;periodo=ULTIMI7&amp;tiStato=CHIUSO_ERR_TRASFORMAZIONE&amp;tiStatoRisoluz=NON_RISOLTO&amp;flVerif=0&amp;pagina=SES_RANGE_DT"><slf:field name="<%= MonitoraggioForm.RiepilogoTrasformazioniFallite.NI_TRASF_FALL_NORIS_NOVER_7%>" controlWidth="w100"/></a></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;periodo=TUTTI&amp;tiStato=CHIUSO_ERR_TRASFORMAZIONE&amp;tiStatoRisoluz=NON_RISOLTO&amp;flVerif=0&amp;pagina=SES_RANGE_DT"><slf:field name="<%= MonitoraggioForm.RiepilogoTrasformazioniFallite.NI_TRASF_FALL_NORIS_NOVER_TOT%>" controlWidth="w100"/></a></td>
                    </tr>
                </tbody>
            </table>
            <sl:newLine skipLine="true"/>
            <sl:newLine skipLine="true"/>
            <%-- Tabella RIEPILOGO VERSAMENTI A PING FALLITI costruita "a manoni" --%>
            <table class="grid" >
                <caption>
                    <div class="livello1"><b>RIEPILOGO VERSAMENTI A PRE-INGEST FALLITI</b></div>
                </caption>
                <thead>
                    <tr>
                        <th></th>
                        <th></th>
                        <th>&nbsp;&nbsp;&nbsp;&nbsp;Oggi&nbsp;&nbsp;&nbsp;&nbsp;</th>
                        <th>&nbsp;&nbsp;&nbsp;&nbsp;Ultimi 7 giorni&nbsp;&nbsp;&nbsp;&nbsp;</th>
                        <th>&nbsp;&nbsp;&nbsp;&nbsp;Totale&nbsp;&nbsp;&nbsp;&nbsp;</th>
                    </tr>
                </thead>
                <tbody class="livello2">
                    <tr>
                        <slf:tableSection name="<%= MonitoraggioForm.VersamentiAPingFalliti.NAME%>" />
                        <td><b>Totale</b></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;periodo=OGGI&amp;tiStato=CHIUSO_ERR_VERSAMENTO_A_PING&amp;pagina=SES_RANGE_DT"><slf:field name="<%= MonitoraggioForm.RiepilogoVersamentiPingFalliti.NI_VERS_PING_FALL_TOT_CORR%>" controlWidth="w100"/></a></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;periodo=ULTIMI7&amp;tiStato=CHIUSO_ERR_VERSAMENTO_A_PING&amp;pagina=SES_RANGE_DT"><slf:field name="<%= MonitoraggioForm.RiepilogoVersamentiPingFalliti.NI_VERS_PING_FALL_TOT_7%>" controlWidth="w100"/></a></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;periodo=TUTTI&amp;tiStato=CHIUSO_ERR_VERSAMENTO_A_PING&amp;pagina=SES_RANGE_DT"><slf:field name="<%= MonitoraggioForm.RiepilogoVersamentiPingFalliti.NI_VERS_PING_FALL_TOT_TOT%>" controlWidth="w100"/></a></td>
                    </tr>
                    <tr class="nascondiRiga">
                        <td></td>
                        <td><b>Risolti</b></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;periodo=OGGI&amp;tiStato=CHIUSO_ERR_VERSAMENTO_A_PING&amp;tiStatoRisoluz=RISOLTO&amp;pagina=SES_RANGE_DT"><slf:field name="<%= MonitoraggioForm.RiepilogoVersamentiPingFalliti.NI_VERS_PING_FALL_RIS_VER_CORR%>" controlWidth="w100"/></a></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;periodo=ULTIMI7&amp;tiStato=CHIUSO_ERR_VERSAMENTO_A_PING&amp;tiStatoRisoluz=RISOLTO&amp;pagina=SES_RANGE_DT"><slf:field name="<%= MonitoraggioForm.RiepilogoVersamentiPingFalliti.NI_VERS_PING_FALL_RIS_VER_7%>" controlWidth="w100"/></a></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;periodo=TUTTI&amp;tiStato=CHIUSO_ERR_VERSAMENTO_A_PING&amp;tiStatoRisoluz=RISOLTO&amp;pagina=SES_RANGE_DT"><slf:field name="<%= MonitoraggioForm.RiepilogoVersamentiPingFalliti.NI_VERS_PING_FALL_RIS_VER_TOT%>" controlWidth="w100"/></a></td>
                    </tr>
                    <tr class="nascondiRiga">
                        <td></td>
                        <td><b>In corso di risoluzione</b></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;periodo=OGGI&amp;tiStato=CHIUSO_ERR_VERSAMENTO_A_PING&amp;tiStatoRisoluz=IN_CORSO&amp;pagina=SES_RANGE_DT"><slf:field name="<%= MonitoraggioForm.RiepilogoVersamentiPingFalliti.NI_VERS_PING_FALL_CORSO_RIS_VER_CORR%>" controlWidth="w100"/></a></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;periodo=ULTIMI7&amp;tiStato=CHIUSO_ERR_VERSAMENTO_A_PING&amp;tiStatoRisoluz=IN_CORSO&amp;pagina=SES_RANGE_DT"><slf:field name="<%= MonitoraggioForm.RiepilogoVersamentiPingFalliti.NI_VERS_PING_FALL_CORSO_RIS_VER_7%>" controlWidth="w100"/></a></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;periodo=TUTTI&amp;tiStato=CHIUSO_ERR_VERSAMENTO_A_PING&amp;tiStatoRisoluz=IN_CORSO&amp;pagina=SES_RANGE_DT"><slf:field name="<%= MonitoraggioForm.RiepilogoVersamentiPingFalliti.NI_VERS_PING_FALL_CORSO_RIS_VER_TOT%>" controlWidth="w100"/></a></td>
                    </tr>
                    <tr class="nascondiRiga">
                        <td></td>
                        <td><b>Non risolubili</b></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;periodo=OGGI&amp;tiStato=CHIUSO_ERR_VERSAMENTO_A_PING&amp;tiStatoRisoluz=NON_RISOLTO&amp;flVerif=1&amp;flNonRisolub=1&amp;pagina=SES_RANGE_DT"><slf:field name="<%= MonitoraggioForm.RiepilogoVersamentiPingFalliti.NI_VERS_PING_FALL_NORISOLUB_CORR%>" controlWidth="w100"/></a></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;periodo=ULTIMI7&amp;tiStato=CHIUSO_ERR_VERSAMENTO_A_PING&amp;tiStatoRisoluz=NON_RISOLTO&amp;flVerif=1&amp;flNonRisolub=1&amp;pagina=SES_RANGE_DT"><slf:field name="<%= MonitoraggioForm.RiepilogoVersamentiPingFalliti.NI_VERS_PING_FALL_NORISOLUB_7%>" controlWidth="w100"/></a></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;periodo=TUTTI&amp;tiStato=CHIUSO_ERR_VERSAMENTO_A_PING&amp;tiStatoRisoluz=NON_RISOLTO&amp;flVerif=1&amp;flNonRisolub=1&amp;pagina=SES_RANGE_DT"><slf:field name="<%= MonitoraggioForm.RiepilogoVersamentiPingFalliti.NI_VERS_PING_FALL_NORISOLUB_TOT%>" controlWidth="w100"/></a></td>
                    </tr>
                    <tr class="nascondiRiga">
                        <td></td>
                        <td><b>Non risolti e verificati</b></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;periodo=OGGI&amp;tiStato=CHIUSO_ERR_VERSAMENTO_A_PING&amp;tiStatoRisoluz=NON_RISOLTO&amp;flVerif=1&amp;flNonRisolub=0&amp;pagina=SES_RANGE_DT"><slf:field name="<%= MonitoraggioForm.RiepilogoVersamentiPingFalliti.NI_VERS_PING_FALL_NORIS_VER_CORR%>" controlWidth="w100"/></a></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;periodo=ULTIMI7&amp;tiStato=CHIUSO_ERR_VERSAMENTO_A_PING&amp;tiStatoRisoluz=NON_RISOLTO&amp;flVerif=1&amp;flNonRisolub=0&amp;pagina=SES_RANGE_DT"><slf:field name="<%= MonitoraggioForm.RiepilogoVersamentiPingFalliti.NI_VERS_PING_FALL_NORIS_VER_7%>" controlWidth="w100"/></a></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;periodo=TUTTI&amp;tiStato=CHIUSO_ERR_VERSAMENTO_A_PING&amp;tiStatoRisoluz=NON_RISOLTO&amp;flVerif=1&amp;flNonRisolub=0&amp;pagina=SES_RANGE_DT"><slf:field name="<%= MonitoraggioForm.RiepilogoVersamentiPingFalliti.NI_VERS_PING_FALL_NORIS_VER_TOT%>" controlWidth="w100"/></a></td>
                    </tr>
                    <tr class="nascondiRiga">
                        <td></td>
                        <td><b>Non risolti e non verificati</b></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;periodo=OGGI&amp;tiStato=CHIUSO_ERR_VERSAMENTO_A_PING&amp;tiStatoRisoluz=NON_RISOLTO&amp;flVerif=0&amp;pagina=SES_RANGE_DT"><slf:field name="<%= MonitoraggioForm.RiepilogoVersamentiPingFalliti.NI_VERS_PING_FALL_NORIS_NOVER_CORR%>" controlWidth="w100"/></a></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;periodo=ULTIMI7&amp;tiStato=CHIUSO_ERR_VERSAMENTO_A_PING&amp;tiStatoRisoluz=NON_RISOLTO&amp;flVerif=0&amp;pagina=SES_RANGE_DT"><slf:field name="<%= MonitoraggioForm.RiepilogoVersamentiPingFalliti.NI_VERS_PING_FALL_NORIS_NOVER_7%>" controlWidth="w100"/></a></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;periodo=TUTTI&amp;tiStato=CHIUSO_ERR_VERSAMENTO_A_PING&amp;tiStatoRisoluz=NON_RISOLTO&amp;flVerif=0&amp;pagina=SES_RANGE_DT"><slf:field name="<%= MonitoraggioForm.RiepilogoVersamentiPingFalliti.NI_VERS_PING_FALL_NORIS_NOVER_TOT%>" controlWidth="w100"/></a></td>
                    </tr>
                </tbody>
            </table>
            <sl:newLine skipLine="true"/>
            <sl:newLine skipLine="true"/>
            <%-- Tabella RIEPILOGO OGGETTI DERIVANTI DA VERSAMENTI FALLITI costruita "a manoni" --%>
            <table class="grid" >
                <caption>
                    <div class="livello1"><b>RIEPILOGO OGGETTI DERIVANTI DA VERSAMENTI FALLITI</b></div>
                </caption>
                <thead>
                    <tr>
                        <th></th>
                        <th></th>
                        <th>&nbsp;&nbsp;&nbsp;&nbsp;Totale&nbsp;&nbsp;&nbsp;&nbsp;</th>
                    </tr>
                </thead>
                <tbody class="livello2">
                    <tr>
                        <td style="color: #055122;"><b>Riepilogo oggetti derivanti da versamenti falliti</b></td>
                        <td><b>Totale</b></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;pagina=OBJ_NON_VERS"><slf:field name="<%= MonitoraggioForm.RiepilogoOggettiDerivantiVersamentiFalliti.NI_OGG_DER_VERS_FALL_TOT_TOT%>" controlWidth="w100"/></a></td>
                    </tr>
                    <tr>
                        <td></td>
                        <td><b>Non risolubili</b></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;flVerif=1&amp;flNonRisolub=1&amp;pagina=OBJ_NON_VERS"><slf:field name="<%= MonitoraggioForm.RiepilogoOggettiDerivantiVersamentiFalliti.NI_OGG_DER_VERS_FALL_NORISOLUB_TOT%>" controlWidth="w100"/></a></td>
                    </tr>
                    <tr>
                        <td></td>
                        <td><b>Verificati</b></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;flVerif=1&amp;flNonRisolub=0&amp;pagina=OBJ_NON_VERS"><slf:field name="<%= MonitoraggioForm.RiepilogoOggettiDerivantiVersamentiFalliti.NI_OGG_DER_VERS_FALL_VER_TOT%>" controlWidth="w100"/></a></td>
                    </tr>
                    <tr>
                        <td></td>
                        <td><b>Non verificati</b></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;flVerif=0&amp;pagina=OBJ_NON_VERS"><slf:field name="<%= MonitoraggioForm.RiepilogoOggettiDerivantiVersamentiFalliti.NI_OGG_DER_VERS_FALL_NOVER_TOT%>" controlWidth="w100"/></a></td>
                    </tr>
                </tbody>
            </table>
            <sl:newLine skipLine="true"/>
            <sl:newLine skipLine="true"/>
            <%-- Tabella RIEPILOGO OGGETTI ANNULLATI O IN CORSO DI ANNULLAMENTO costruita "a manoni" --%>
            <table class="grid" >
                <caption>
                    <div class="livello1"><b>RIEPILOGO OGGETTI ANNULLATI O IN CORSO DI ANNULLAMENTO</b></div>
                </caption>
                <thead>
                    <tr>
                        <th></th>
                        <th></th>
                        <th>&nbsp;&nbsp;&nbsp;&nbsp;Oggi&nbsp;&nbsp;&nbsp;&nbsp;</th>
                        <th>&nbsp;&nbsp;&nbsp;&nbsp;Ultimi 7 giorni&nbsp;&nbsp;&nbsp;&nbsp;</th>
                        <th>&nbsp;&nbsp;&nbsp;&nbsp;Totale&nbsp;&nbsp;&nbsp;&nbsp;</th>
                    </tr>
                </thead>
                <tbody class="livello2">
                    <tr>
                        <slf:tableSection name="<%= MonitoraggioForm.OggettiAnnullatiOInCorso.NAME%>" />
                        <td><b>Totale</b></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;periodo=OGGI&amp;tiStato=ANNULLATO&amp;tiStato=IN_CORSO_ANNULLAMENTO&amp;pagina=OBJ_RANGE_DT"><slf:field name="<%= MonitoraggioForm.RiepilogoOggettiAnnullatiInCorso.NI_OGG_ANNUL_TOT_CORR%>" controlWidth="w100"/></a></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;periodo=ULTIMI7&amp;tiStato=ANNULLATO&amp;tiStato=IN_CORSO_ANNULLAMENTO&amp;pagina=OBJ_RANGE_DT"><slf:field name="<%= MonitoraggioForm.RiepilogoOggettiAnnullatiInCorso.NI_OGG_ANNUL_TOT_7%>" controlWidth="w100"/></a></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;periodo=TUTTI&amp;tiStato=ANNULLATO&amp;tiStato=IN_CORSO_ANNULLAMENTO&amp;pagina=OBJ_RANGE_DT"><slf:field name="<%= MonitoraggioForm.RiepilogoOggettiAnnullatiInCorso.NI_OGG_ANNUL_TOT_TOT%>" controlWidth="w100"/></a></td>
                    </tr>
                    <tr class="nascondiRiga">
                        <td></td>
                        <td><b>In corso di annullamento</b></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;periodo=OGGI&amp;tiStato=IN_CORSO_ANNULLAMENTO&amp;pagina=OBJ_RANGE_DT"><slf:field name="<%= MonitoraggioForm.RiepilogoOggettiAnnullatiInCorso.NI_OGG_CORSO_ANNUL_CORR%>" controlWidth="w100"/></a></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;periodo=ULTIMI7&amp;tiStato=IN_CORSO_ANNULLAMENTO&amp;pagina=OBJ_RANGE_DT"><slf:field name="<%= MonitoraggioForm.RiepilogoOggettiAnnullatiInCorso.NI_OGG_CORSO_ANNUL_7%>" controlWidth="w100"/></a></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;periodo=TUTTI&amp;tiStato=IN_CORSO_ANNULLAMENTO&amp;pagina=OBJ_RANGE_DT"><slf:field name="<%= MonitoraggioForm.RiepilogoOggettiAnnullatiInCorso.NI_OGG_CORSO_ANNUL_TOT%>" controlWidth="w100"/></a></td>
                    </tr>
                    <tr class="nascondiRiga">
                        <td></td>
                        <td><b>Annullati</b></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;periodo=OGGI&amp;tiStato=ANNULLATO&amp;pagina=OBJ_RANGE_DT"><slf:field name="<%= MonitoraggioForm.RiepilogoOggettiAnnullatiInCorso.NI_OGG_ANNUL_CORR%>" controlWidth="w100"/></a></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;periodo=ULTIMI7&amp;tiStato=ANNULLATO&amp;pagina=OBJ_RANGE_DT"><slf:field name="<%= MonitoraggioForm.RiepilogoOggettiAnnullatiInCorso.NI_OGG_ANNUL_7%>" controlWidth="w100"/></a></td>
                        <td><a href="Monitoraggio.html?operation=monitoraggioListe&amp;periodo=TUTTI&amp;tiStato=ANNULLATO&amp;pagina=OBJ_RANGE_DT"><slf:field name="<%= MonitoraggioForm.RiepilogoOggettiAnnullatiInCorso.NI_OGG_ANNUL_TOT%>" controlWidth="w100"/></a></td>
                    </tr>
                </tbody>
            </table>
            <sl:newLine skipLine="true"/>
            <sl:newLine skipLine="true"/>

        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>
