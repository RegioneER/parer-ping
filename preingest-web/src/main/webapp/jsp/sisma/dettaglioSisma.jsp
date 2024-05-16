<%@ page import="it.eng.sacerasi.slite.gen.form.SismaForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<sl:html>
    <sl:head title="<%=SismaForm.InserimentoWizard.DESCRIPTION%>" >
        <link rel="stylesheet" type="text/css" href="/sacerping/css/sisma.css" type="text/css" media="screen" />
        <script type="text/javascript">
            /* Imposta il flag grafico al campo su tutti i campi di una colonna che hanno valore 1 o zero */
            function mettiFlagGrafico(nomeCampo) {
                $("input[name^='"+nomeCampo+"']").each(function() {
                    var valore=$(this).parent().text().trim().substring(0,1);
                    if (valore==='1') {
                        $(this).parent().html('<center><img src="img/checkbox-on.png" alt="Selezionato"></center>');
                    } else {
                        $(this).parent().html('<center><img src="img/checkbox-off.png" alt="Non selezionato"></center>');
                    }
                });                
            }
            
            $(function () {
                $('#DocumentiCaricatiList tbody tr').each(function (idx, elemento) {
                    var nomeOriginale = $($(elemento).find('td')[0]).html();
                    // Ora che è editabile bisogna buttare l'input hidden
                    var nomeOriginaleRipulito=nomeOriginale.slice(1, nomeOriginale.indexOf('<')-1 );
                    var nomeFile = $($(elemento).find('td')[1]).html();
                    // Ora che è editabile bisogna buttare l'input hidden
                    var nomeFileRipulito=nomeFile.slice(1, nomeFile.indexOf('<')-1 );
                    // Altrimenti mette spazi all'inizio e alla fine!
                    nomeFileRipulito=nomeFileRipulito.trim();
                    var link = '<a href="SismaDownloadServlet?chiave=' + nomeFileRipulito + '" download id="download" class="DownloadSisma"></a>';
                    $($(elemento).find('td')[0]).html(link + '&nbsp;' + nomeOriginaleRipulito);
                });
                // Attacco al pulsante la conferma da parte dell'utente dell'operazione
                var pulsante = $("input[name='operation__riportaInBozza']");
                if (pulsante!=null) {
                    pulsante.unbind();
                    pulsante.click(function (event) {
                        var c = confirm("E' stato richiesto di riportare il progetto in stato BOZZA, confermare?");
                        if (c == false) {
                            event.preventDefault();
                            event.stopPropagation();
                            return c;
                        }
                    });
                }
                // Attacco al pulsante la conferma da parte dell'utente dell'operazione
                var pulsanteVersato = $("input[name='operation__riportaInStatoVersato']");
                if (pulsanteVersato!=null) {
                    pulsanteVersato.unbind();
                    pulsanteVersato.click(function (event) {
                        var c = confirm("E' stato richiesto di riportare il progetto in stato VERSATO, confermare?");
                        if (c == false) {
                            event.preventDefault();
                            event.stopPropagation();
                            return c;
                        }
                    });
                }
                mettiFlagGrafico('Fl_obbligatorio_');
                mettiFlagGrafico('Fl_esito_verifica_');

                // Cambia l'icona informativa dall'eventuale messaggio di info in check (baffetto)
                $('.ui-icon-info').toggleClass('ui-icon-info').addClass('ui-icon-check');
                
                //MEV 30691
                $('.popUpDaRivedere').dialog({
                    autoOpen: true,
                    width: 600,
                    modal: true,
                    closeOnEscape: true,
                    resizable: false,
                    dialogClass: "alertBox",
                    buttons: {
                        "Ok": function () {
                            $(this).dialog("close");
                            let _csrf = $('input[name="_csrf"]');
                            var fakeFormStr = '<form id="spagoLiteAppForm" action="Sisma.html" method="post">';
                            
                            let ti_verifica_agenzia = $('select[name="Ti_verifica_agenzia"]');
                            
                            ti_verifica_agenzia.each(function (index) {
                                fakeFormStr += '<input type="hidden" name="Ti_verifica_agenzia" value="' + $(this).val() + '"/>';
                            });
                            
                            fakeFormStr = fakeFormStr + '<input type="hidden" name="operation" value="confermaStatoDaRivedere"/>' +
                            '<input type="hidden" name="_csrf" value="' + _csrf.val() + '">' +
                            '</form>';
                            
                            $(fakeFormStr).appendTo('body').submit();
                        },
                        "Annulla": function () {
                            $(this).dialog("close");
                        }
                    }
                });
                
            });
            
        </script>
        <script type="text/javascript" src="<c:url value='/js/sips/customRecuperaErroriSismaMessageBox.js'/>" ></script>
    </sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia versatore" description="Dettaglio progetto ricostruzione"/>
        <sl:menu />
        <sl:content >
            <slf:messageBox />
            <%@ include file="mascheraRecuperoErrori.jspf"%>
            
             <c:if test="${!empty requestScope.popUpDaRivedere}">
                <div class="messages popUpDaRivedere">
                    <div class="message info ">
                        <p>Almeno un tipo documento è da rivedere: procedendo con il salvataggio il progetto sarà inviato in revisione al Soggetto Attuatore e non sarà più possibile modificarlo fino a che non verrà richiesta una nuova verifica ad Agenzia.
Si vuole procedere?</p>
                    </div>
                </div>
            </c:if>
            
            <sl:contentTitle title="Dettaglio progetto ricostruzione" />            
            <c:choose>
                <c:when test="${requestScope.nascondiUpdate}">
                    <slf:fieldBarDetailTag name="<%=SismaForm.DatiAgenzia.NAME%>" hideOperationButton="true"/>  
                </c:when>
                <c:otherwise>
                    <slf:fieldBarDetailTag name="<%=SismaForm.DatiAgenzia.NAME%>" hideOperationButton="false" hideUpdateButton="false" hideDeleteButton="true" />  
                </c:otherwise>
            </c:choose>
            <sl:newLine />
            <slf:fieldSet borderHidden="false">
                <%@ include file="datiGeneraliStretti.jspf"%>
                <slf:section name="<%=SismaForm.DatiAgenziaSection.NAME%>" styleClass="importantContainer containerRight w30">  
                    <slf:lblField name="<%=SismaForm.DatiAgenzia.CLASSIFICA_AG%>" width="w100" controlWidth="w60" labelWidth="w30" />
                    <sl:newLine />
                    <slf:lblField name="<%=SismaForm.DatiAgenzia.ID_FASCICOLO_AG%>" width="w100" controlWidth="w60" labelWidth="w30" />
                    <sl:newLine />
                    <slf:lblField name="<%=SismaForm.DatiAgenzia.OGGETTO_FASCICOLO_AG%>" width="w100" controlWidth="w60" labelWidth="w30" />
                    <sl:newLine />
                    <slf:lblField name="<%=SismaForm.DatiAgenzia.ID_SOTTOFASCICOLO_AG%>" width="w100" controlWidth="w60" labelWidth="w30" />
                    <sl:newLine />
                    <slf:lblField name="<%=SismaForm.DatiAgenzia.OGGETTO_SOTTOFASCICOLO_AG%>" width="w100" controlWidth="w60" labelWidth="w30" />
                    <sl:newLine />
                    <slf:lblField name="<%=SismaForm.DatiAgenzia.REGISTRO_AG%>" colSpan="2" labelWidth="w30" controlWidth="w100"/>
                    <sl:newLine />
                    <slf:lblField name="<%=SismaForm.DatiAgenzia.ANNO_AG%>" colSpan="2" labelWidth="w30" controlWidth="w100" />
                    <sl:newLine />
                    <slf:lblField name="<%=SismaForm.DatiAgenzia.NUMERO_AG%>" colSpan="2" labelWidth="w30" controlWidth="w100"/>
                    <sl:newLine />
                    <slf:lblField name="<%=SismaForm.DatiAgenzia.DATA_AG%>" colSpan="2" labelWidth="w30" controlWidth="w100"/>
                    <sl:newLine />
                </slf:section> 
                <sl:newLine />
                <sl:newLine />
                <slf:section name="<%=SismaForm.DocumentiCaricatiSection.NAME%>" styleClass="importantContainer">  
                    <slf:editableList name="<%=SismaForm.DocumentiCaricatiList.NAME%>" multiRowEdit="true"/>
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
                <!-- Se non ci sono segnalazioni oppure se il SU è IN_ELABORAZIONE non si mostra proprio la sezione -->
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
                    <slf:buttonList name="<%=SismaForm.DettaglioButtonList.NAME%>" />
                </sl:pulsantiera>
            </slf:fieldSet>
        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>