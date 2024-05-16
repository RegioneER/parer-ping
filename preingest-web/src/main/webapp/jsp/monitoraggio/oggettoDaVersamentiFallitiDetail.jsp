<%@ page import="it.eng.sacerasi.slite.gen.form.MonitoraggioForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head  title="Monitoraggio - Dettaglio oggetto derivante da versamenti falliti" >
        <script src="<c:url value='/js/help/inithighlightingjs.js' />" type="text/javascript"></script> 
        <script type='text/javascript' src="<c:url value="/js/sips/customCheckBoxSesVerif.js"/>" ></script>
        <script type='text/javascript'>
            $(document).ready(function () {
                checkVerificati = $('table.list tr:not(:first-child) td > input[name="Fl_verif"]');
                checkNonRisolubili = $('table.list tr:not(:first-child) td > input[name="Fl_non_risolub"]');
                
                checkNonRisolubili.each((index, value) => {
                    $(value).css('visibility', 'hidden');
                });
                
                checkVerificati.each((index, value) => {
                    $(value).css('visibility', 'hidden');
                });
                
                checkFirstVerificato = $('table.list tr td > input[name="Fl_verif"]:checked');
                checkFirstRisolubile = $('table.list tr td > input[name="Fl_non_risolub"]:checked');
                
                checkFirstVerificato.each((index, value) => {
                    $(value).parent().append('<img src="/sacerping/img/checkbox-on.png" alt=" Selezionato">');
                    $(value).css('visibility', 'hidden');
                });
                
                checkFirstRisolubile.each((index, value) => {
                    $(value).parent().append('<img src="/sacerping/img/checkbox-on.png" alt=" Selezionato">');
                    $(value).css('visibility', 'hidden');
                });

                //SUE26200
                $('.confermaAnnullamentoUD').dialog({
                    autoOpen: true,
                    width: 600,
                    modal: true,
                    closeOnEscape: true,
                    resizable: false,
                    dialogClass: "alertBox",
                    title: "Conferma annullamento unità documentarie",
                    buttons: {
                        "Conferma": function () {
                            $(this).dialog("close");
                            var cdKey = $("#Cd_key_object_hidden").val();
                            var tiAnnullamentoUD = $('input[name="ti_annullamento_ud"]:checked').val();
                            if (cdKey) {
                                $.post("Monitoraggio.html", {operation: "annullaVersamentiUDDerVersFallitiAction", Ti_annullamento_ud: tiAnnullamentoUD}).done(function (data) {
                                    CAjaxDataFormWalk(data);

                                    window.location = "Monitoraggio.html?operation=listNavigationOnClick&table=OggettiDaVersamentiFallitiList&navigationEvent=elenco&riga=-1&forceReload=false";
                                });
                            }
                        },
                        "Annulla": function () {
                            $(this).dialog("close");
                        }
                    }
                });

                $('.confermaImpostaVerificNonRisolubOggettiDaVersFalliti').dialog({
                    autoOpen: true,
                    width: 600,
                    modal: true,
                    closeOnEscape: true,
                    resizable: false,
                    dialogClass: "alertBox",
                    title: "Conferma imposta versamenti verificati / non risolubili",
                    buttons: {
                        "Conferma": function () {
                            $(this).dialog("close");
                            let flVerif = Array.from(document.querySelectorAll('table.list td > input[name="Fl_verif"]:checked')).map((element) => {
                                return element.getAttribute('value');
                            });
                            let flNonRisolub = Array.from(document.querySelectorAll('table.list td > input[name="Fl_non_risolub"]:checked')).map((element) => {
                                return element.getAttribute('value');
                            });
                            
                            let csrf = $('input[name="_csrf"]').val();
                            
                            let fakeForm = '<form id="spagoLiteAppForm" action="Monitoraggio.html" method="post">' +
                                    '<input type="hidden" name="_csrf" value="' + csrf + '">' + 
                                    '<input type="hidden" name="operation__impostaVerificNonRisolubOggettiDaVersFallitiAction" value="Imposta versamenti verificati / non risolubili">';
                            
                            for (let i = 0; i < flVerif.length; i++) {
                                fakeForm = fakeForm + '<input id="Fl_verif_' + i + '" name="Fl_verif" type="checkbox" value="' + flVerif[i] + '" checked="checked" style="visibility: hidden">';
                            }
                            
                            for (let i = 0; i < flNonRisolub.length; i++) {
                                fakeForm = fakeForm + '<input id="Fl_non_risolub_' + i + '" name="Fl_non_risolub" type="checkbox" value="' + flNonRisolub[i] + '" checked="checked" style="visibility: hidden">';
                            }
                            
                            fakeForm = fakeForm + '</form>';
                            
                            $(fakeForm).appendTo($(document.body)).submit();
                        },
                        "Annulla": function () {
                            $(this).dialog("close");
                        }
                    }
                });
                
                $('.confermaImpostaTuttiNonRisolubOggettiDaVersFallitiAction').dialog({
                    autoOpen: true,
                    width: 600,
                    modal: true,
                    closeOnEscape: true,
                    resizable: false,
                    dialogClass: "alertBox",
                    title: "Conferma imposta versamenti verificati / non risolubili",
                    buttons: {
                        "Conferma": function () {
                            $(this).dialog("close");
                            let csrf = $('input[name="_csrf"]').val();
                            
                            let fakeForm = '<form id="spagoLiteAppForm" action="Monitoraggio.html" method="post">' +
                                    '<input type="hidden" name="_csrf" value="' + csrf + '">' + 
                                    '<input type="hidden" name="operation__impostaTuttiNonRisolubOggettiDaVersFallitiAction" value="Imposta versamenti verificati / non risolubili">' +
                                    '</form>';
                            
                            $(fakeForm).appendTo($(document.body)).submit();
                        },
                        "Annulla": function () {
                            $(this).dialog("close");
                        }
                    }
                });
            });
        </script>
    </sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia versatore"/>
        <sl:menu showChangePasswordBtn="true" />
        <sl:content>
            <slf:messageBox  />
            <c:if test="${!empty requestScope.confermaAnnullamentoUD}">
                <div class="messages confermaAnnullamentoUD">
                    <div class="message info ">
                        <p><span>L'operazione coinvolge <c:out value="${requestScope.ni_unita_doc_vers}" /> UD. Risultano presenti <c:out value="${requestScope.ni_unita_doc_vers_dup}" /> UD in stato di errore per chiave già presente che potrebbero essere stati versati da altri oggetti.</span></p>
                    </div>
                    <div class="message">
                        <input type="radio" id="ti_annullamento_ud_1" name="ti_annullamento_ud" value="0" checked>
                        <label for="ti_annullamento_ud_1">preservare queste <c:out value="${requestScope.ni_unita_doc_vers_dup}" />  UD e annullare le UD rimanenti.</label>
                    </div>
                    <div class="message">
                        <input type="radio" id="ti_annullamento_ud_2" name="ti_annullamento_ud" value="1">
                        <label for="ti_annullamento_ud_2">annullare tutte le <c:out value="${requestScope.ni_unita_doc_vers}" /> UD.</label>
                    </div>
                </div>
            </c:if>
            <c:if test="${!empty requestScope.confermaImpostaVerificNonRisolubOggettiDaVersFalliti}">
                <div class="messages confermaImpostaVerificNonRisolubOggettiDaVersFalliti">
                    <div class="message info ">
                        <p><span>ATTENZIONE! E' stata selezionato il valore NON RISOLUBILE. Procedendo con il salvataggio il sistema eliminerà il file dell'oggetto e non sarà più possibile rimetterlo in versamento o in trasformazione. Confermi?</span></p>
                    </div>
                </div>
            </c:if>
            <c:if test="${!empty requestScope.confermaImpostaTuttiNonRisolubOggettiDaVersFallitiAction}">
                <div class="messages confermaImpostaTuttiNonRisolubOggettiDaVersFallitiAction">
                    <div class="message info ">
                        <p><span>ATTENZIONE! E' stata selezionato il valore NON RISOLUBILE. Procedendo con il salvataggio il sistema eliminerà il file dell'oggetto e non sarà più possibile rimetterlo in versamento o in trasformazione. Confermi?</span></p>
                    </div>
                </div>
            </c:if>
            <sl:newLine skipLine="true"/>

            <sl:contentTitle title="DETTAGLIO OGGETTO DERIVANTE DA VERSAMENTI FALLITI"/>
            <slf:listNavBarDetail name="<%= MonitoraggioForm.OggettiDaVersamentiFallitiList.NAME%>" />

            <slf:fieldSet  borderHidden="false">
                <slf:lblField name="<%=MonitoraggioForm.OggettoDaVersamentiFallitiDetail.VERSATORE%>" colSpan="4"/>
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.OggettoDaVersamentiFallitiDetail.NM_TIPO_OBJECT%>" colSpan="4"/>
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.OggettoDaVersamentiFallitiDetail.CD_KEY_OBJECT%>" colSpan="4"/>
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.OggettoDaVersamentiFallitiDetail.DS_OBJECT%>" colSpan="4"/>
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.OggettoDaVersamentiFallitiDetail.CD_VERS_GEN%>" colSpan="4"/>
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.OggettoDaVersamentiFallitiDetail.CD_TRASF%>" colSpan="4"/>
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.OggettoDaVersamentiFallitiDetail.CD_VERSIONE_TRASF%>" colSpan="4"/>
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.OggettoDaVersamentiFallitiDetail.TI_GEST_OGGETTI_FIGLI%>" colSpan="4"/>
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.OggettoDaVersamentiFallitiDetail.OGGETTO_FL_VERIF%>" colSpan="4"/>
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.OggettoDaVersamentiFallitiDetail.OGGETTO_FL_NON_RISOLUB%>" colSpan="4"/>
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.OggettoDaVersamentiFallitiDetail.FL_VERS_SACER_DA_RECUP%>" colSpan="4"/>
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.OggettoDaVersamentiFallitiDetail.DT_FIRST_SES_ERR%>" colSpan="4"/>
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.OggettoDaVersamentiFallitiDetail.DT_LAST_SES_ERR%>" colSpan="4"/>
            </slf:fieldSet>

            <sl:newLine skipLine="true"/>
            <h2 class="titleFiltri">Lista versamenti falliti</h2>
            <sl:newLine skipLine="true"/>

            <slf:list   name="<%= MonitoraggioForm.OggettoDaVersamentiFallitiDetailVersamentiList.NAME%>" />
            <slf:listNavBar  name="<%= MonitoraggioForm.OggettoDaVersamentiFallitiDetailVersamentiList.NAME%>" />

            <sl:pulsantiera>
                <slf:lblField name="<%=MonitoraggioForm.OggettiDerVersFallitiButtonList.IMPOSTA_VERIFIC_NON_RISOLUB_OGGETTI_DA_VERS_FALLITI%>" colSpan="2" />
                <slf:lblField name="<%=MonitoraggioForm.OggettiDerVersFallitiButtonList.IMPOSTA_TUTTI_VERIFIC_OGGETTI_DA_VERS_FALLITI%>" colSpan="2" />
                <slf:lblField name="<%=MonitoraggioForm.OggettiDerVersFallitiButtonList.IMPOSTA_TUTTI_NON_RISOLUB_OGGETTI_DA_VERS_FALLITI%>" colSpan="2" />
                <slf:lblField name="<%=MonitoraggioForm.OggettiDerVersFallitiButtonList.ANNULLA_OGGETTO_DER_VERS_FALLITI%>" colSpan="2" />
                <slf:lblField name="<%=MonitoraggioForm.OggettiDerVersFallitiButtonList.ANNULLA_VERSAMENTI_UDDER_VERS_FALLITI%>" colSpan="2" />
            </sl:pulsantiera>

        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>
