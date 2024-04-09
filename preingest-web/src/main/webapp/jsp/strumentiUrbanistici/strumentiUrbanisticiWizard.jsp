<%@ page import="it.eng.sacerasi.slite.gen.form.StrumentiUrbanisticiForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<sl:html>
    <sl:head title="<%=StrumentiUrbanisticiForm.InserimentoWizard.DESCRIPTION%>" >
        <link rel="stylesheet" type="text/css" href="/sacerping/css/strumentiUrbanistici.css" type="text/css" media="screen" />
        <link rel="stylesheet" type="text/css" href="/sacerping/js/plupload/jquery.plupload.queue/css/jquery.plupload.queue.css" type="text/css" media="screen" />
        <script type="text/javascript" src="/sacerping/js/plupload/plupload.full.min.js"></script>
        <script type="text/javascript" src="/sacerping/js/plupload/jquery.plupload.queue/jquery.plupload.queue.js"></script>
        <script type="text/javascript" src="/sacerping/js/plupload/i18n/it.js"></script>
        <script type="text/javascript" src="<c:url value='/js/sips/customPollVerificaDocumenti.js' />" ></script>
        <script type="text/javascript" src="<c:url value='/js/strumentiUrbanistici.js' />" ></script>

        <script type="text/javascript">
            var parerUploader = new Array();
            var errori = null; // Variabile globale usata per gli errori caricati nello step di upload

            $(function () {

            <c:set value="${sessionScope['###_FORM_CONTAINER']}" var="form" />

            <c:choose>
                <c:when test="${form.inserimentoWizard.currentStep.name eq 'StrumentoUrbanistico'}">
                    //STEP 1 del wizard
                    initWizardStep1();

                    //In caso di modifica aggiunge la domanda se si è sicuri di modificarlo
                    <c:if test="${form.datiGeneraliInput.status eq 'update'}">
                    initWizardStep1UpdateMode();
                    // Valorizza gli eventuali campi dei collegamenti presi dai campi nascosti     
                    attivazioneCollegamentiPerFaseModifica();
                    </c:if>

                    <c:if test="${form.datiGeneraliInput.status eq 'insert'}">
                    initWizardStep1InsertMode();
                    // Attiva o disattiva la valorizzazione dei collegamenti in inserimento
                    attivazioneCollegamentiPerFase();
                    </c:if>
                        
                    // STEP 1 STATO DIVERSO DA BOZZA va in sola lettura!
                    <c:if test="${ not(form.datiGeneraliOutput.ti_stato_out.value eq 'BOZZA') && not(form.datiGeneraliOutput.ti_stato_out.value eq null)}">
                        $("#Nm_tipo_strumento_urbanistico, #Ti_fase_strumento, #Ti_atto, #Numero, #Anno, #Data, #Ds_descrizione, #IdentificativoCollegato1, #IdentificativoCollegato1, #AnnoCollegato1, #AnnoCollegato2").attr("disabled", true);
                    </c:if>
                </c:when>
                <c:when test="${form.inserimentoWizard.currentStep.name eq 'UploadDocumenti'}">
                    <%@ include file="wizardStep2.jspf"%>
                </c:when>
            </c:choose>

                // STEP 2 STATO DIVERSO DA BOZZA va in sola lettura!
            <c:if test="${ (form.inserimentoWizard.currentStep.name eq 'UploadDocumenti') && not(form.datiGeneraliOutput.ti_stato_out.value eq 'BOZZA') }">
                $("[id^=browse_], [id^=rimuovi_]").attr('disabled', true);
            </c:if>

                // STEP 3 STATO DIVERSO DA BOZZA va in sola lettura!
            <c:if test="${ (form.inserimentoWizard.currentStep.name eq 'Riepilogo') && not(form.datiGeneraliOutput.ti_stato_out.value eq 'BOZZA') }">
                $('input[name="operation__versaSU"]').attr('disabled', true);
            </c:if>

            <c:if test="${ (form.inserimentoWizard.currentStep.name eq 'Riepilogo')}">
                poll();
            </c:if>

            });
        </script>

    </sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia versatore" description="Versamenti strumenti urbanistici"/>
        <sl:menu />
        <sl:content >
            <div class="verificaDocumentiMessageBox "></div>
            <slf:messageBox />
            <slf:wizard name="<%=StrumentiUrbanisticiForm.InserimentoWizard.NAME%>">
                <slf:wizardNavBar name="<%=StrumentiUrbanisticiForm.InserimentoWizard.NAME%>" />
                <sl:newLine skipLine="true"/>
                <slf:step name="<%=StrumentiUrbanisticiForm.InserimentoWizard.STRUMENTO_URBANISTICO%>">
                    <slf:fieldSet borderHidden="false">
                        <slf:section name="<%=StrumentiUrbanisticiForm.DatiGeneraliSUSection.NAME%>" styleClass="importantContainer">  
                            <slf:lblField name="<%=StrumentiUrbanisticiForm.DatiGeneraliOutput.NM_ENTE_OUT%>" colSpan="4"  />
                            <sl:newLine />
                            <slf:lblField name="<%=StrumentiUrbanisticiForm.DatiGeneraliOutput.NM_PROVINCIA_OUT%>" colSpan="4"  />
                            <sl:newLine />
                            <slf:lblField name="<%=StrumentiUrbanisticiForm.DatiGeneraliOutput.NM_UNIONE_OUT%>" colSpan="4"  />
                            <sl:newLine />
                            <slf:lblField name="<%=StrumentiUrbanisticiForm.DatiGeneraliOutput.DT_CREAZIONE_OUT%>" colSpan="4"  />
                            <sl:newLine />
                            <slf:lblField name="<%=StrumentiUrbanisticiForm.DatiGeneraliOutput.NM_TIPO_STRUMENTO_URBANISTICO_OUT%>" colSpan="4"  />
                            <sl:newLine />
                            <slf:lblField name="<%=StrumentiUrbanisticiForm.DatiGeneraliOutput.TI_FASE_STRUMENTO_OUT%>" colSpan="4"  />
                            <sl:newLine />
                            <slf:lblField name="<%=StrumentiUrbanisticiForm.DatiGeneraliOutput.TI_STATO_OUT%>" colSpan="4"  />                           
                            <sl:newLine />
                            <slf:lblField name="<%=StrumentiUrbanisticiForm.DatiGeneraliOutput.TI_ATTO_OUT%>" colSpan="4"  />
                            <sl:newLine />
                            <slf:lblField name="<%=StrumentiUrbanisticiForm.DatiGeneraliOutput.ANNO_OUT%>" colSpan="4"  />
                            <sl:newLine />
                            <slf:lblField name="<%=StrumentiUrbanisticiForm.DatiGeneraliOutput.NUMERO_OUT%>" colSpan="4"  />
                            <sl:newLine />
                            <slf:lblField name="<%=StrumentiUrbanisticiForm.DatiGeneraliOutput.CD_KEY_OUT%>" colSpan="4"  />
                            <sl:newLine />
                            <slf:lblField name="<%=StrumentiUrbanisticiForm.DatiGeneraliOutput.DT_STATO_OUT%>" colSpan="4"  />
                            <sl:newLine />
                        </slf:section> 
                        <slf:section name="<%=StrumentiUrbanisticiForm.InputDatiSUSection.NAME%>" styleClass="importantContainer containerLeft w60">  
                            <slf:lblField name="<%=StrumentiUrbanisticiForm.DatiGeneraliInput.NM_TIPO_STRUMENTO_URBANISTICO%>" colSpan="4"  />
                            <sl:newLine />
                            <slf:lblField name="<%=StrumentiUrbanisticiForm.DatiGeneraliInput.TI_FASE_STRUMENTO%>" colSpan="4"  />
                            <sl:newLine />
                            <slf:lblField name="<%=StrumentiUrbanisticiForm.DatiGeneraliInput.TI_ATTO%>" colSpan="4"  />
                            <sl:newLine />
                            <slf:lblField name="<%=StrumentiUrbanisticiForm.DatiGeneraliInput.NUMERO%>" colSpan="4"  />
                            <sl:newLine />
                            <slf:lblField name="<%=StrumentiUrbanisticiForm.DatiGeneraliInput.ANNO%>" colSpan="4"  />
                            <sl:newLine />
                            <slf:lblField name="<%=StrumentiUrbanisticiForm.DatiGeneraliInput.DATA%>" colSpan="4"  />
                            <sl:newLine skipLine="true" />
                            <slf:lblField name="<%=StrumentiUrbanisticiForm.DatiGeneraliInput.OGGETTO%>" colSpan="4"  />
                            <sl:newLine skipLine="true" />
                            <slf:lblField name="<%=StrumentiUrbanisticiForm.DatiGeneraliInput.DS_DESCRIZIONE%>" colSpan="4" />
                            <sl:newLine />
                            <slf:lblField name="<%=StrumentiUrbanisticiForm.DatiGeneraliInput.MODIFICATO%>" colSpan="4"  />
                        </slf:section> 
                        <slf:section name="<%=StrumentiUrbanisticiForm.AltreFasiSection.NAME%>" styleClass="importantContainer containerRight w40">
                            <slf:lblField name="<%=StrumentiUrbanisticiForm.DatiGeneraliInput.FASE_COLLEGATA1%>" />
                            <div class="containerLeft w100">
                                <label for="FaseCollegata1_fake" class="slLabel w30">Fase</label>
                                <span id="FaseCollegata1_fake" name="FaseCollegata1_fake" class="slText w70"></span>
                            </div>
                            <sl:newLine />
                            <slf:lblField name="<%=StrumentiUrbanisticiForm.DatiGeneraliInput.ANNO_COLLEGATO1%>"  width="w100" labelWidth="w30" controlWidth="w70" />
                            <sl:newLine />
                            <slf:lblField name="<%=StrumentiUrbanisticiForm.DatiGeneraliInput.IDENTIFICATIVO_COLLEGATO1%>" width="w100" labelWidth="w30" controlWidth="w70"/>
                            <sl:newLine />
                            <sl:newLine />
                            <sl:newLine />
                            <slf:lblField name="<%=StrumentiUrbanisticiForm.DatiGeneraliInput.FASE_COLLEGATA2%>" />
                            <div class="containerLeft w100">
                                <label for="FaseCollegata2_fake" class="slLabel w30">Fase</label>
                                <span id="FaseCollegata2_fake" name="FaseCollegata2_fake" class="slText w70"></span>
                            </div>
                            <sl:newLine />
                            <slf:lblField name="<%=StrumentiUrbanisticiForm.DatiGeneraliInput.ANNO_COLLEGATO2%>" width="w100" labelWidth="w30" controlWidth="w70" />
                            <sl:newLine />
                            <slf:lblField name="<%=StrumentiUrbanisticiForm.DatiGeneraliInput.IDENTIFICATIVO_COLLEGATO2%>" width="w100" labelWidth="w30" controlWidth="w70"/>
                            <sl:newLine />
                        </slf:section> 
                    </slf:fieldSet>
                    <sl:pulsantiera>
                        <slf:lblField  name="<%=StrumentiUrbanisticiForm.DatiGeneraliInput.SALVA_BOZZA%>" width="w50" />
                    </sl:pulsantiera>
                </slf:step>
                <slf:step name="<%=StrumentiUrbanisticiForm.InserimentoWizard.UPLOAD_DOCUMENTI%>">
                    <slf:fieldSet borderHidden="false">
                        <slf:section name="<%=StrumentiUrbanisticiForm.DatiGeneraliSUSection.NAME%>" styleClass="importantContainer">  
                            <slf:lblField name="<%=StrumentiUrbanisticiForm.DatiGeneraliOutput.NM_ENTE_OUT%>" colSpan="4"  />
                            <sl:newLine />
                            <slf:lblField name="<%=StrumentiUrbanisticiForm.DatiGeneraliOutput.NM_PROVINCIA_OUT%>" colSpan="4"  />
                            <sl:newLine />
                            <slf:lblField name="<%=StrumentiUrbanisticiForm.DatiGeneraliOutput.NM_UNIONE_OUT%>" colSpan="4"  />
                            <sl:newLine />
                            <slf:lblField name="<%=StrumentiUrbanisticiForm.DatiGeneraliOutput.DT_CREAZIONE_OUT%>" colSpan="4"  />
                            <sl:newLine />
                            <slf:lblField name="<%=StrumentiUrbanisticiForm.DatiGeneraliOutput.NM_TIPO_STRUMENTO_URBANISTICO_OUT%>" colSpan="4"  />
                            <sl:newLine />
                            <slf:lblField name="<%=StrumentiUrbanisticiForm.DatiGeneraliOutput.TI_FASE_STRUMENTO_OUT%>" colSpan="4"  />
                            <sl:newLine />
                            <slf:lblField name="<%=StrumentiUrbanisticiForm.DatiGeneraliOutput.TI_STATO_OUT%>" colSpan="4"  />
                            <sl:newLine />
                            <slf:lblField name="<%=StrumentiUrbanisticiForm.DatiGeneraliOutput.TI_ATTO_OUT%>" colSpan="4"  />
                            <sl:newLine />
                            <slf:lblField name="<%=StrumentiUrbanisticiForm.DatiGeneraliOutput.ANNO_OUT%>" colSpan="4"  />
                            <sl:newLine />
                            <slf:lblField name="<%=StrumentiUrbanisticiForm.DatiGeneraliOutput.NUMERO_OUT%>" colSpan="4"  />
                            <sl:newLine />
                            <slf:lblField name="<%=StrumentiUrbanisticiForm.DatiGeneraliOutput.CD_KEY_OUT%>" colSpan="4" />
                            <sl:newLine />
                            <slf:lblField name="<%=StrumentiUrbanisticiForm.DatiGeneraliOutput.DT_STATO_OUT%>" colSpan="4"  />
                            <sl:newLine />
                        </slf:section> 
                        <sl:newLine />
                        <div>
                            <!--                            <form id="formId" action="Submit.action" method="post"> -->
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
                            <!--                            </form> -->
                            <br />
                            <pre id="console"></pre>
                        </div>

                    </slf:fieldSet>
                </slf:step>
                <slf:step name="<%=StrumentiUrbanisticiForm.InserimentoWizard.RIEPILOGO%>">
                    <slf:fieldSet borderHidden="false">
                        <slf:section name="<%=StrumentiUrbanisticiForm.DatiGeneraliSUSection.NAME%>" styleClass="importantContainer containerLeft w60">  
                            <slf:lblField name="<%=StrumentiUrbanisticiForm.DatiGeneraliOutput.NM_ENTE_OUT%>" colSpan="3" labelWidth="w30" controlWidth="w100"/>
                            <sl:newLine />
                            <slf:lblField name="<%=StrumentiUrbanisticiForm.DatiGeneraliOutput.NM_PROVINCIA_OUT%>" colSpan="3" labelWidth="w30" controlWidth="w100"/>
                            <sl:newLine />
                            <slf:lblField name="<%=StrumentiUrbanisticiForm.DatiGeneraliOutput.NM_UNIONE_OUT%>" colSpan="3" labelWidth="w30" controlWidth="w100"/>
                            <sl:newLine />
                            <slf:lblField name="<%=StrumentiUrbanisticiForm.DatiGeneraliOutput.DT_CREAZIONE_OUT%>" colSpan="3" labelWidth="w30" controlWidth="w100" />
                            <sl:newLine />
                            <slf:lblField name="<%=StrumentiUrbanisticiForm.DatiGeneraliOutput.NM_TIPO_STRUMENTO_URBANISTICO_OUT%>" colSpan="3" labelWidth="w30" controlWidth="w100"/>
                            <sl:newLine />
                            <slf:lblField name="<%=StrumentiUrbanisticiForm.DatiGeneraliOutput.TI_FASE_STRUMENTO_OUT%>" colSpan="3" labelWidth="w30" controlWidth="w100"/>
                            <sl:newLine />
                            <slf:lblField name="<%=StrumentiUrbanisticiForm.DatiGeneraliOutput.OGGETTO_OUT%>" width="w100" labelWidth="w30" controlWidth="w70" />
                            <sl:newLine />
                            <slf:lblField name="<%=StrumentiUrbanisticiForm.DatiGeneraliOutput.DS_DESCRIZIONE_OUT%>" width="w100" labelWidth="w30" controlWidth="w70" />
                            <sl:newLine />
                            <slf:lblField name="<%=StrumentiUrbanisticiForm.DatiGeneraliOutput.TI_STATO_OUT%>" colSpan="3" labelWidth="w30" controlWidth="w100"/>                                   
                            <sl:newLine />
                            <slf:lblField name="<%=StrumentiUrbanisticiForm.DatiGeneraliOutput.TI_ATTO_OUT%>" colSpan="3" labelWidth="w30" controlWidth="w100"/>
                            <sl:newLine />
                            <slf:lblField name="<%=StrumentiUrbanisticiForm.DatiGeneraliOutput.ANNO_OUT%>" colSpan="3" labelWidth="w30" controlWidth="w100"/>
                            <sl:newLine />
                            <slf:lblField name="<%=StrumentiUrbanisticiForm.DatiGeneraliOutput.NUMERO_OUT%>" colSpan="3" labelWidth="w30" controlWidth="w100"/>
                            <sl:newLine />
                            <slf:lblField name="<%=StrumentiUrbanisticiForm.DatiGeneraliOutput.CD_KEY_OUT%>" colSpan="3" labelWidth="w30" controlWidth="w100"/>
                            <sl:newLine />
                            <slf:lblField name="<%=StrumentiUrbanisticiForm.DatiGeneraliOutput.DT_STATO_OUT%>" colSpan="3" labelWidth="w30" controlWidth="w100" />
                            <sl:newLine />
                        </slf:section> 
                        <slf:section name="<%=StrumentiUrbanisticiForm.AltreFasiSection.NAME%>" styleClass="importantContainer containerRight w40">  
                            <slf:lblField name="<%=StrumentiUrbanisticiForm.DatiGeneraliOutput.FASE_COLLEGATA1_OUT%>" colSpan="4" />
                            <sl:newLine />
                            <slf:lblField name="<%=StrumentiUrbanisticiForm.DatiGeneraliOutput.IDENTIFICATIVO_COLLEGATO1_OUT%>" colSpan="2" />
                            <slf:lblField name="<%=StrumentiUrbanisticiForm.DatiGeneraliOutput.ANNO_COLLEGATO1_OUT%>" colSpan="2"  />
                            <sl:newLine />
                            <sl:newLine />
                            <sl:newLine />
                            <slf:lblField name="<%=StrumentiUrbanisticiForm.DatiGeneraliOutput.FASE_COLLEGATA2_OUT%>" colSpan="4" />
                            <sl:newLine />
                            <slf:lblField name="<%=StrumentiUrbanisticiForm.DatiGeneraliOutput.IDENTIFICATIVO_COLLEGATO2_OUT%>" colSpan="2" />
                            <slf:lblField name="<%=StrumentiUrbanisticiForm.DatiGeneraliOutput.ANNO_COLLEGATO2_OUT%>" colSpan="2"  />
                        </slf:section> 
                        <sl:newLine />
                        <sl:newLine />
                        <slf:section name="<%=StrumentiUrbanisticiForm.DocumentiCaricatiSection.NAME%>" styleClass="importantContainer">  
                            <slf:list  name="<%= StrumentiUrbanisticiForm.DocumentiCaricatiList.NAME%>"  />
                        </slf:section>
                        <!-- Se non ci sono segnalazioni si fa sparire proprio la sezione -->
                        <c:if test="${fn:length(requestScope['alObbNonCaricati']) gt 0 || fn:length(requestScope['alDocInErrore']) gt 0}">
                            <slf:section name="<%=StrumentiUrbanisticiForm.ErroriRiepilogoSection.NAME%>" styleClass="importantContainer">
                                <c:forEach var="elem" items="${requestScope['alObbNonCaricati']}">
                                    &nbsp;<c:out value="${elem}" /><BR/>
                                </c:forEach>
                                <c:forEach var="elem" items="${requestScope['alDocInErrore']}">
                                    &nbsp;<c:out value="${elem}" /><BR/>
                                </c:forEach>
                            </slf:section>
                        </c:if>
                        <!-- Se non ci sono segnalazioni oppure se il SU è BOZZA non si mostra proprio la sezione -->
                        <c:if test="${(requestScope['statoSU'] eq 'BOZZA') and not(strDocFacolativi eq '')}">
                            <slf:section name="<%=StrumentiUrbanisticiForm.AggiuntaDocumentiFacoltativiSection.NAME%>" styleClass="importantContainer"> 
                                <c:set value="${requestScope['strDocFacolativi']}" var="strDocFacolativi" />
                                <c:if test="${ not(strDocFacolativi eq '') }">
                                    &nbsp;E' ancora possibile caricare nello Strumento urbanistico corrente i seguenti documenti facoltativi: <STRONG><c:out value="${strDocFacolativi}" /></STRONG>.
                                    <BR/>
                                    <BR/>
                                    &nbsp;Per versare altri file zip tornare alla schermata precedente tramite il tasto indietro.
                                </c:if>
                            </slf:section>
                        </c:if>
                        <sl:newLine skipLine="true"/>
                        <sl:pulsantiera>
                            <slf:buttonList name="<%=StrumentiUrbanisticiForm.RiepilogoButtonList.NAME%>" />                                    
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

