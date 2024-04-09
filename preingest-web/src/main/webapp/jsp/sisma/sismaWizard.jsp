<%@ page import="it.eng.sacerasi.slite.gen.form.SismaForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<sl:html>
    <sl:head title="<%=SismaForm.InserimentoWizard.DESCRIPTION%>" >
        <link rel="stylesheet" type="text/css" href="/sacerping/css/sisma.css" type="text/css" media="screen" />
        <link rel="stylesheet" type="text/css" href="/sacerping/js/plupload/jquery.plupload.queue/css/jquery.plupload.queue.css" type="text/css" media="screen" />
        <script type="text/javascript" src="/sacerping/js/plupload/plupload.full.min.js"></script>
        <script type="text/javascript" src="/sacerping/js/plupload/jquery.plupload.queue/jquery.plupload.queue.js"></script>
        <script type="text/javascript" src="/sacerping/js/plupload/i18n/it.js"></script>
        <script type="text/javascript" src="<c:url value='/js/sips/customPollVerificaDocumentiSisma.js' />" ></script>
        <script type="text/javascript" src="<c:url value='/js/sisma.js' />" ></script>

        <script type="text/javascript">
            var parerUploader = new Array();
            var errori = null; // Variabile globale usata per gli errori caricati nello step di upload

            $(function () {

            <c:set value="${sessionScope['###_FORM_CONTAINER']}" var="form" />

            <c:choose>
                <c:when test="${form.inserimentoWizard.currentStep.name eq 'Sisma'}">
                //STEP 1 del wizard
                initWizardStep1();

                //In caso di modifica aggiunge la domanda se si è sicuri di modificarlo
                    <c:if test="${form.datiGeneraliInput.status eq 'update'}">
                initWizardStep1UpdateMode();
                // Valorizza gli eventuali campi dei collegamenti presi dai campi nascosti     
                    </c:if>

                    <c:if test="${form.datiGeneraliInput.status eq 'insert'}">
                initWizardStep1InsertMode();
                // Attiva o disattiva la valorizzazione dei collegamenti in inserimento
                    </c:if>

                // STEP 1 STATO != BOZZA vanno in sola lettura le combo l'anno e il numero!
                    <c:if test="${not(form.datiGeneraliOutput.ti_stato_out.value eq 'BOZZA') && not(form.datiGeneraliOutput.ti_stato_out.value eq null)}">
                var comboBox = $("#Id_sisma_finanziamento, #Id_sisma_progetti_ag, #Id_sisma_fase_progetto, #Id_sisma_stato_progetto, #Id_sisma_val_atto, #Numero, #Anno");
                comboBox.attr("disabled", "true");
                $("#spagoLiteAppForm").submit(function () {
                    comboBox.removeAttr("disabled");
                });
                    </c:if>

                // STEP 1 STATO !0 BOZZA e != DA_RIVEDERE vanno in sola lettura TUTTI i campi!
                    <c:if test="${not(form.datiGeneraliOutput.ti_stato_out.value eq 'BOZZA') && not(form.datiGeneraliOutput.ti_stato_out.value eq 'DA_RIVEDERE') && not(form.datiGeneraliOutput.ti_stato_out.value eq null)}">
                $("#Numero, #Anno, #Data, #Ds_descrizione, #Classifica, #Id_fascicolo, #Oggetto_fascicolo, #Id_sottofascicolo, #Oggetto_sottofascicolo, #Fl_intervento_soggetto_a_tutela").attr("disabled", true);
                    </c:if>

                </c:when>
                <c:when test="${form.inserimentoWizard.currentStep.name eq 'UploadDocumenti'}">
                    <%@ include file="wizardStep2.jspf"%>
                </c:when>
            </c:choose>

                // STEP 2 STATO DIVERSO DA BOZZA va in sola lettura!
            <c:if test="${ (form.inserimentoWizard.currentStep.name eq 'UploadDocumenti') && not(form.datiGeneraliOutput.ti_stato_out.value eq 'BOZZA') && not(form.datiGeneraliOutput.ti_stato_out.value eq 'DA_RIVEDERE') }">
                $("[id^=browse_], [id^=rimuovi_]").attr('disabled', true);
            </c:if>

                // STEP 3 STATO DIVERSO DA BOZZA va in sola lettura!
            <c:if test="${ (form.inserimentoWizard.currentStep.name eq 'Riepilogo') && not(form.datiGeneraliOutput.ti_stato_out.value eq 'BOZZA') }">
                $('input[name="operation__versaSisma"]').attr('disabled', true);
            </c:if>

            <c:if test="${ (form.inserimentoWizard.currentStep.name eq 'Riepilogo')}">
                poll();
                var pulsante = $("input[name='operation__verificaAgenzia']");
                if (pulsante !== null) {
                    pulsante.unbind();
                    pulsante.click(function (event) {
                        event.preventDefault();
                        event.stopPropagation();
                        $('.confermaVerificaAgenzia').dialog({
                            autoOpen: true,
                            width: 600,
                            modal: true,
                            closeOnEscape: true,
                            resizable: true,
                            dialogClass: "alertBox",
                            buttons: {
                                "Ok": function () {
                                    $(this).dialog("close");
                                    pulsante.unbind();
                                    pulsante.click();
                                },
                                "Annulla": function () {
                                    $(this).dialog("close");
                                }
                            }
                        });
                    });


                }
            </c:if>
                // Cambia l'icona informativa dall'eventuale messaggio di info in check (baffetto)
                $('.ui-icon-info').toggleClass('ui-icon-info').addClass('ui-icon-check');
            });
        </script>

    </sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia versatore" description="Versamenti sisma"/>
        <sl:menu />
        <sl:content >
            <div class="verificaDocumentiMessageBox"></div>
            <slf:messageBox />
            <div class="messages confermaVerificaAgenzia ui-widget ui-widget-content" style="display: none; padding: 0px">
                <div class="message info ui-state-highlight">
                    <p>
                        <span>E' stato richiesto di sottoporre il progetto alla verifica dell'Agenzia, confermare? Non sarà più possibile modificarne il contenuto.</span>
                        <br/>
                        <span>Per completare l'inoltro è necessario scaricare la lista di versamento e inviarla all'Agenzia tramite PEC all'indirizzo <a href= "mailto:tecnicosisma@postacert.regione.emilia-romagna.it">tecnicosisma@postacert.regione.emilia-romagna.it</a></span>
                        <ul>
                            <li>Per <b>OOPP e Ord 10 /2019</b> inviare: lettera di trasmissione + lista di versamento + MODELLO di autorizzazione al versamento.</li>
                            <li>Per <b>Ordinanza BBCC Privati</b> inviare: G2 MODULO PEC CARICAMENTO DOCUMENTAZIONE SU SACER + Lista di versamento</li>
                    </p>
                </div>
            </div>
            <slf:wizard name="<%=SismaForm.InserimentoWizard.NAME%>">
                <slf:wizardNavBar name="<%=SismaForm.InserimentoWizard.NAME%>" />
                <sl:newLine skipLine="true"/>
                <slf:step name="<%=SismaForm.InserimentoWizard.SISMA%>">
                    <slf:fieldSet borderHidden="false">
                        <%@ include file="datiGeneraliLarghi.jspf"%>
                        <div id="container1" class="w100">
                            <slf:section name="<%=SismaForm.InputDatiSismaSection.NAME%>" styleClass="importantContainer containerLeft w70">  
                                <slf:lblField name="<%=SismaForm.DatiGeneraliInput.ID_SISMA_FINANZIAMENTO%>" width="w100" controlWidth="w60" labelWidth="w20" />
                                <sl:newLine />
                                <slf:lblField name="<%=SismaForm.DatiGeneraliInput.ID_SISMA_PROGETTI_AG%>" width="w100" controlWidth="w60" labelWidth="w20" />
                                <sl:newLine />
                                <slf:lblField name="<%=SismaForm.DatiGeneraliInput.FL_INTERVENTO_SOGGETTO_A_TUTELA%>" width="w100" controlWidth="w80" labelWidth="w20"/>
                                <sl:newLine />
                                <slf:lblField name="<%=SismaForm.DatiGeneraliInput.ID_SISMA_FASE_PROGETTO%>" width="w100" controlWidth="w60" labelWidth="w20" />
                                <sl:newLine />
                                <slf:lblField name="<%=SismaForm.DatiGeneraliInput.ID_SISMA_STATO_PROGETTO%>" width="w100" controlWidth="w60" labelWidth="w20" />
                                <sl:newLine />
                                <slf:lblField name="<%=SismaForm.DatiGeneraliInput.ID_SISMA_VAL_ATTO%>" width="w100" controlWidth="w60" labelWidth="w20" />
                                <sl:newLine />
                                <slf:lblField name="<%=SismaForm.DatiGeneraliInput.NUMERO%>" width="w100" controlWidth="w25" labelWidth="w20" />
                                <sl:newLine />
                                <slf:lblField name="<%=SismaForm.DatiGeneraliInput.ANNO%>" width="w100" controlWidth="w60" labelWidth="w20" />
                                <sl:newLine />
                                <slf:lblField name="<%=SismaForm.DatiGeneraliInput.DATA%>" width="w100" controlWidth="w25" labelWidth="w20" />
                                <sl:newLine skipLine="true" />
                                <slf:lblField name="<%=SismaForm.DatiGeneraliInput.OGGETTO%>" width="w100" controlWidth="w80" labelWidth="w20"/>
                                <sl:newLine skipLine="true" />
                                <slf:lblField name="<%=SismaForm.DatiGeneraliInput.DS_DESCRIZIONE%>" width="w100" controlWidth="w80" labelWidth="w20"/>
                                <sl:newLine skipLine="true" />
                                <slf:lblField name="<%=SismaForm.DatiGeneraliInput.MODIFICATO%>" width="w100" controlWidth="w60" labelWidth="w20" />
                            </slf:section> 

                            <slf:section name="<%=SismaForm.AltreFasiSection.NAME%>" styleClass="importantContainer containerRight w30"> 
                            </slf:section>    

                            <slf:section name="<%=SismaForm.DatiProfiloArchivisticoSection.NAME%>" styleClass="importantContainer containerLeft w70"> 
                                <slf:lblField name="<%=SismaForm.DatiProfiloArchivistico.CLASSIFICA%>" width="w100" controlWidth="w60" labelWidth="w30" />
                                <sl:newLine />
                                <slf:lblField name="<%=SismaForm.DatiProfiloArchivistico.ID_FASCICOLO%>" width="w100" controlWidth="w60" labelWidth="w30" />
                                <sl:newLine />
                                <slf:lblField name="<%=SismaForm.DatiProfiloArchivistico.OGGETTO_FASCICOLO%>" width="w100" controlWidth="w60" labelWidth="w30" />
                                <sl:newLine />
                                <slf:lblField name="<%=SismaForm.DatiProfiloArchivistico.ID_SOTTOFASCICOLO%>" width="w100" controlWidth="w60" labelWidth="w30" />
                                <sl:newLine />
                                <slf:lblField name="<%=SismaForm.DatiProfiloArchivistico.OGGETTO_SOTTOFASCICOLO%>" width="w100" controlWidth="w60" labelWidth="w30" />
                                <sl:newLine />
                            </slf:section>    

                        </slf:fieldSet>

                        <sl:pulsantiera>
                            <slf:lblField  name="<%=SismaForm.DatiGeneraliInput.SALVA_BOZZA%>" width="w50" />
                        </sl:pulsantiera>

                    </slf:step>
                    <slf:step name="<%=SismaForm.InserimentoWizard.UPLOAD_DOCUMENTI%>">
                        <slf:fieldSet borderHidden="false">
                            <%@ include file="datiGeneraliLarghi.jspf"%>
                            <sl:newLine />
                            <div>
                                <table id="tabella-obbligatori" width="90%" class="grid" >
                                    <caption>
                                        <div class="livello1"><b>Documenti obbligatori</b></div>
                                    </caption>
                                    <thead>
                                        <tr>
                                            <th width="30%">File zip caricati</th>
                                            <th width="25%">Tipo documento</th>
                                            <th width="10%">Azioni</th>
                                            <th width="5%">Caricamento</th>
                                            <th width="10%">Stato verifica</th>
                                            <th width="10%">Dimensione</th>
                                            <th width="10%">Data caricamento</th>
                                        </tr>
                                    </thead>
                                    <tbody id="body-table-obbligatori">
                                    </tbody>
                                </table>
                                <sl:newLine />
                                <sl:newLine />
                                <table id="tabella-opzionali" width="90%" class="grid table-layout: fixed;">
                                    <caption>
                                        <div class="livello1"><b>Documenti facoltativi</b></div>
                                    </caption>
                                    <thead>
                                        <tr>
                                            <th width="30%">File zip caricati</th>
                                            <th width="25%">Tipo documento</th>
                                            <th width="10%">Azioni</th>
                                            <th width="5%">Caricamento</th>
                                            <th width="10%">Stato verifica</th>
                                            <th width="10%">Dimensione</th>
                                            <th width="10%">Data caricamento</th>
                                        </tr>
                                    </thead>
                                    <tbody id="body-table-opzionali">

                                    </tbody>
                                </table>
                                <div id="container-per-plupload">
                                </div>
                                <br />
                                <pre id="console"></pre>
                            </div>

                        </slf:fieldSet>
                    </slf:step>
                    <slf:step name="<%=SismaForm.InserimentoWizard.RIEPILOGO%>">
                        <slf:fieldSet borderHidden="false">
                            <%@ include file="datiGeneraliStretti.jspf"%>
                            <slf:section name="<%=SismaForm.AltreFasiSection.NAME%>" styleClass="importantContainer containerRight w30">  
                            </slf:section> 
                            <sl:newLine />
                            <sl:newLine />
                            <slf:section name="<%=SismaForm.DocumentiCaricatiSection.NAME%>" styleClass="importantContainer">  
                                <slf:list  name="<%= SismaForm.DocumentiCaricatiList.NAME%>"  />
                            </slf:section>
                            <!-- Se non ci sono segnalazioni si fa sparire proprio la sezione -->
                            <c:if test="${fn:length(requestScope['alObbNonCaricati']) gt 0 || fn:length(requestScope['alDocInErrore']) gt 0}">
                                <slf:section name="<%=SismaForm.ErroriRiepilogoSection.NAME%>" styleClass="importantContainer">
                                    <c:forEach var="elem" items="${requestScope['alObbNonCaricati']}">
                                        &nbsp;<c:out value="${elem}" /><BR/>
                                    </c:forEach>
                                    <c:forEach var="elem" items="${requestScope['alDocInErrore']}">
                                        &nbsp;<c:out value="${elem}" /><BR/>
                                    </c:forEach>
                                </slf:section>
                            </c:if>
                            <!-- Se non ci sono segnalazioni oppure se il sisma è BOZZA non si mostra proprio la sezione -->
                            <c:if test="${(requestScope['statoSisma'] eq 'BOZZA') and not(strDocFacolativi eq '')}">
                                <slf:section name="<%=SismaForm.AggiuntaDocumentiFacoltativiSection.NAME%>" styleClass="importantContainer"> 
                                    <c:set value="${requestScope['strDocFacolativi']}" var="strDocFacolativi" />
                                    <c:if test="${ not(strDocFacolativi eq '') }">
                                        &nbsp;E' ancora possibile caricare nel Progetto di ricostruzione corrente i seguenti documenti facoltativi: <STRONG><c:out value="${strDocFacolativi}" /></STRONG>.
                                        <BR/>
                                        <BR/>
                                        &nbsp;Per versare altri file zip tornare alla schermata precedente tramite il tasto indietro.
                                    </c:if>
                                </slf:section>
                            </c:if>
                            <sl:newLine skipLine="true"/>
                            <sl:pulsantiera>
                                <slf:buttonList name="<%=SismaForm.RiepilogoButtonList.NAME%>" />                                    
                            </sl:pulsantiera>
                        </slf:fieldSet>

                        <c:choose>
                            <c:when test="${form.inserimentoWizard.currentStep.name eq 'Riepilogo'}">
                                <%@ include file="mascheraRecuperoErrori.jspf"%>
                            </c:when>
                        </c:choose>

                    </slf:step>
                </slf:wizard>
            </sl:content>
            <sl:footer />
        </sl:body>
    </sl:html>