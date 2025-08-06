<%@ page import="it.eng.sacerasi.slite.gen.form.MonitoraggioForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>
<c:set scope="request" var="navTable" value="${(empty param.mainNavTable) ? fn:escapeXml(param.table) : fn:escapeXml(param.mainNavTable)  }" />
<sl:html>
    <sl:head  title="Monitoraggio - Dettaglio oggetto" >
<script src="<c:url value='/js/help/inithighlightingjs.js' />" type="text/javascript"></script> 
<script type="text/javascript">
    $(document).ready(function () {
        //MEV28877
        $('.confermaModificaTipoOggetto').dialog({
            autoOpen: true,
            width: 600,
            modal: true,
            closeOnEscape: true,
            resizable: false,
            dialogClass: "alertBox",
            buttons: {
                "Ok": function () {
                    $(this).dialog("close");
                    var tipoOggetto = $("#Id_tipo_object").val();
                    if (tipoOggetto) {
                        $.post("Monitoraggio.html", {operation: "confermaModificaTipoOggetto", Id_tipo_object: tipoOggetto}).done(function (data) {
                            CAjaxDataFormWalk(data);

                            window.location = "Monitoraggio.html?operation=listNavigationOnClick&table=OggettiList&navigationEvent=elenco&riga=-1&forceReload=false";
                        });
                    }
                },
                "Annulla": function () {
                    $(this).dialog("close");
                }
            }
        });

        $('.confermaRecuperoErrore').dialog({
            autoOpen: true,
            width: 600,
            modal: true,
            closeOnEscape: true,
            resizable: false,
            dialogClass: "alertBox",
            buttons: {
                "Ok": function () {
                    $(this).dialog("close");
                    var stato = $("#Ti_stato_popup").val();
                    var tipo = $("#Ti_recupero").val();
                    var tipoOggetto = $("#Id_tipo_object").val();
                    if (stato) {
                        $.post("Monitoraggio.html", {operation: "confermaRecuperoErrore", Ti_stato_popup: stato, Ti_recupero: tipo, Id_tipo_object: tipoOggetto}).done(function (data) {
                            CAjaxDataFormWalk(data);
                            // Al momento il bottone non può essere nascosto dal framework, faccio a manoni
                            $('input[type="submit"][name*="recuperoErr"]').hide();
                        });
                    }
                },
                "Annulla": function () {
                    $(this).dialog("close");
                }
            }
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
                    //MEV 26942 - nascondo i pulsanti di annullamento.
                    $(this).dialog("close");
                    var idObject = $("#Id_object_hidden").val();
                    var tiAnnullamentoUD = $('input[name="ti_annullamento_ud"]:checked').val();
                    var dsAnnullamentoUd = $('#ds_annullamento_ud').val();
                    if (idObject) {
                        $.post("Monitoraggio.html", {operation: "annullaVersamentiUDDetailAction", Ti_annullamento_ud: tiAnnullamentoUD, ds_annullamento_ud: dsAnnullamentoUd}).done(function (data) {

                            CAjaxDataFormWalk(data);

                            //window.location = "Monitoraggio.html?operation=listNavigationOnClick&table=OggettiList&navigationEvent=elenco&riga=-1&forceReload=false";
                        });
                    }
                },
                "Annulla": function () {
                    $(this).dialog("close");
                }
            }
        });

        //MEV26398
        $('.confermaAnnullamentoOggetto').dialog({
            autoOpen: true,
            width: 600,
            modal: true,
            closeOnEscape: true,
            resizable: false,
            dialogClass: "alertBox",
            title: "Conferma annullamento",
            buttons: {
                "Conferma": function () {
                    $(this).dialog("close");
                    
                    //attiva lo spinner, codice copiato dal framework usato per i bottoni "submit"
                    $("body").append("<div class='overlay dialog_spinner'><div class='overlay__inner'><div class='overlay__content'><span class='spinner'></span></div></div></div>");
                            
                    //MEV 26942 - nascondi pulsanti annullamento.
                    $('input[name="operation__annullaOggettoDetail"]').parent().hide();
                    $('input[name="operation__annullaVersamentiUDDetail"]').parent().hide();
                            
                    var idObject = $("#Id_object_hidden").val();
                    if (idObject) {
                        $.post("Monitoraggio.html", {operation: "annullaOggettoDetailAction"}).done(function (data) {
                            CAjaxDataFormWalk(data);
                            
                            //rimove lo spinner
                            $("div.dialog_spinner").remove();

                            //window.location = "Monitoraggio.html?operation=listNavigationOnClick&table=OggettiList&navigationEvent=elenco&riga=-1&forceReload=false";
                        });
                    }
                },
                "Annulla": function () {
                    $(this).dialog("close");
                }
            }
        });

        $('.setAnnullatoInDaTrasformare').dialog({
            autoOpen: true,
            width: 600,
            modal: true,
            closeOnEscape: true,
            resizable: false,
            dialogClass: "alertBox",
            buttons: {
                "Ok": function () {
                    $(this).dialog("close");
                    var stato = $("#Ti_stato_popup").val();
                    var tipoOggetto = $("#Id_tipo_object").val();
                    if (stato) {
                        $.post("Monitoraggio.html", {operation: "confermaSetAnnullatoDaTrasformare", Ti_stato_popup: stato, Id_tipo_object: tipoOggetto}).done(function (data) {
                            CAjaxDataFormWalk(data);
                            // Al momento il bottone non può essere nascosto dal framework, faccio a manoni
                            $('input[type="submit"][name*="settaDa"]').hide();
                        });
                    }
                },
                "Annulla": function () {
                    $(this).dialog("close");
                }
            }
        });
        $('.setErroreTrasformazione').dialog({
            autoOpen: true,
            width: 600,
            modal: true,
            closeOnEscape: true,
            resizable: false,
            dialogClass: "alertBox",
            buttons: {
                "Ok": function () {
                    $(this).dialog("close");
                    $.post("Monitoraggio.html", {operation: "confermaSettaErroreTrasformazione"}).done(function (data) {
                        CAjaxDataFormWalk(data);
                        // Al momento il bottone non può essere nascosto dal framework, faccio a manoni
                        $('input[type="submit"][name*="settaEr"]').hide();
                    });
                },
                "Annulla": function () {
                    $(this).dialog("close");
                }
            }
        });
                
        $('.setChiusoErrVersamento').dialog({
            autoOpen: true,
            width: 600,
            modal: true,
            closeOnEscape: true,
            resizable: false,
            dialogClass: "alertBox",
            buttons: {
                "Ok": function () {
                    $(this).dialog("close");
                    $.post("Monitoraggio.html", {operation: "confermaSettaChiusoErrVersamento"}).done(function (data) {
                        CAjaxDataFormWalk(data);
                        // Al momento il bottone non può essere nascosto dal framework, faccio a manoni
                        $('input[type="submit"][name*="setChiusoErr"]').hide();
                    });
                },
                "Annulla": function () {
                    $(this).dialog("close");
                }
            }
        });

        // Vale sia per il popup di recupero errore che di set annullato in da trasformare
        $("#Ti_stato_popup").on('change', function () {
            var valore = this.value;
            if (valore === 'DA_TRASFORMARE') {
                $('#Id_tipo_object').parent().show();
            } else {
                $('#Id_tipo_object').parent().hide();
            }
        });
        if ($('#Ti_stato_popup').find('option:selected').val() === 'DA_TRASFORMARE') {
            $('#Id_tipo_object').parent().show();
        }
    });
</script>
    </sl:head>
    <sl:body>
        <c:set value="${sessionScope['###_FORM_CONTAINER']}" var="form" />
        <sl:header changeOrganizationBtnDescription="Cambia versatore"/>
        <sl:menu showChangePasswordBtn="true" />
        <sl:content>
            <slf:messageBox  />
            <c:if test="${!empty requestScope.confermaAnnullamentoUD}">
                <c:if test="${requestScope.ni_unita_doc_vers_dup != 0}">
<div class="messages confermaAnnullamentoUD">
    <div class="message info ">
        <p>
            <span>Risultano <c:out value="${requestScope.ni_unita_doc_vers}" /> UD versate, di cui <c:out value="${requestScope.ni_unita_doc_vers_dup}" /> UD in stato di errore per chiave già presente (potrebbero essere stati versati da altri oggetti).</span>
            <br/>
                                <c:if test="${!empty requestScope.confermaAnnullamentoOggettoSisma}">
        <p>Il versamento da annullare sul soggetto attuatore è già stato versato in agenzia, si vuole continuare?</p>
                            </c:if>
        <span>Selezionare l'operazione da completare: </span>
        </p>
    </div>
    <div class="message">
        <input type="radio" id="ti_annullamento_ud_1" name="ti_annullamento_ud" value="0" checked>
        <label for="ti_annullamento_ud_1">preservare queste <c:out value="${requestScope.ni_unita_doc_vers_dup}" />  UD e annullare le UD rimanenti.</label>
    </div>
    <div class="message">
        <input type="radio" id="ti_annullamento_ud_2" name="ti_annullamento_ud" value="1">
        <label for="ti_annullamento_ud_2">annullare tutte le <c:out value="${requestScope.ni_unita_doc_vers}" /> UD.</label>
    </div>
    <div class="message">
        <p>
            <label for="ds_annullamento_ud">Scrivere una motivazione per questo annullamento (opzionale, max 2000 caratteri)</label>
        <div>
            <textarea id="ds_annullamento_ud" name="ds_annullamento_ud" style="width: 100%"></textarea>
        </div>
        </p>
    </div>
</div>
                </c:if>
                <c:if test="${requestScope.ni_unita_doc_vers_dup == 0}">
<div class="messages confermaAnnullamentoUD">
    <div class="message info ">
        <p>
            <span>Risultano <c:out value="${requestScope.ni_unita_doc_vers}" /> UD versate.</span>
            <br/>
                                <c:if test="${!empty requestScope.confermaAnnullamentoOggettoSisma}">
        <p>Il versamento da annullare sul soggetto attuatore è già stato versato in agenzia, si vuole continuare?</p>
                                </c:if>
        <div class="message">
            <p>
                <label for="ds_annullamento_ud">Scrivere una motivazione per questo annullamento (opzionale, max 2000 caratteri)</label>
            <div>
                <textarea id="ds_annullamento_ud" name="ds_annullamento_ud" style="width: 100%"></textarea>
            </div>
            </p>
        </div>
        <span>Procedere con l'annullamento?</span>

        </p>
    </div>
    <input type="radio" id="ti_annullamento_ud_2" name="ti_annullamento_ud" value="0" checked style="display: none"/>
</div>
                </c:if>
            </c:if>
            <c:if test="${!empty requestScope.confermaAnnullamentoOggetto}">
<div class="messages confermaAnnullamentoOggetto">
    <div class="message info ">
                        <c:if test="${empty requestScope.confermaAnnullamentoOggettoSisma}">
                            <c:choose>
                                <c:when test="${(form.oggettoDetail.ti_vers_file.value eq 'DA_TRASFORMARE')}">
        <p>Procedere con l'annullamento dell'Oggetto in PING? Le UD versate in SACER non subiranno modifiche.</p>
                                </c:when>
                                <c:otherwise>
        <p>Procedere con l'annullamento dell'Oggetto in PING? Le UD versate in SACER non subiranno modifiche ma procedendo il sistema eliminerà il file dell'oggetto ZIP_CON_XML_SACER e non sarà più possibile rimetterlo in versamento o in trasformazione.</p>
                                </c:otherwise>
                            </c:choose>
                        </c:if>
                        <c:if test="${!empty requestScope.confermaAnnullamentoOggettoSisma}">
        <p>Il versamento da annullare sul soggetto attuatore è già stato versato in agenzia, si vuole continuare?</p>
                        </c:if>
    </div>
</div>
            </c:if>
            <c:if test="${!empty requestScope.confermaRecuperoErrore}">
<div class="messages confermaRecuperoErrore ">
    <ul>
        <li class="message info ">
            <p>Seleziona lo stato da assegnare per il recupero</p><br/>
                            <slf:lblField name="<%=MonitoraggioForm.OggettoDetail.TI_STATO_POPUP%>" width="w100" controlWidth="w60" labelWidth="w20" /><sl:newLine />
                            <slf:lblField name="<%=MonitoraggioForm.OggettoDetail.ID_TIPO_OBJECT%>" width="w100" controlWidth="w60" labelWidth="w20" /><sl:newLine />
                            <slf:lblField name="<%=MonitoraggioForm.OggettoDetail.TI_RECUPERO%>" width="w100" controlWidth="w60" labelWidth="w20" /><sl:newLine />
        </li>
    </ul>
</div>
            </c:if>
            <c:if test="${!empty requestScope.setAnnullatoInDaTrasformare}">
<div class="messages setAnnullatoInDaTrasformare ">
    <ul>
        <li class="message info ">
            <p>Seleziona lo stato da assegnare per il recupero</p><br/>
                            <slf:lblField name="<%=MonitoraggioForm.OggettoDetail.TI_STATO_POPUP%>" width="w100" controlWidth="w60" labelWidth="w20" /><sl:newLine />
                            <slf:lblField name="<%=MonitoraggioForm.OggettoDetail.ID_TIPO_OBJECT%>" width="w100" controlWidth="w60" labelWidth="w20" /><sl:newLine />
        </li>
    </ul>
</div>
            </c:if>
            <c:if test="${!empty requestScope.setErroreTrasformazione}">
<div class="messages setErroreTrasformazione ">
    <ul>
        <li class="message info ">
            <p>Confermi l'operazione?</p><br/>
        </li>
    </ul>
</div>
            </c:if>
            <c:if test="${!empty requestScope.setChiusoErrVersamento}">
<div class="messages setChiusoErrVersamento ">
    <ul>
        <li class="message info ">
            <p>Confermi l'operazione?</p><br/>
        </li>
    </ul>
</div>
            </c:if>
            <c:if test="${!empty requestScope.confermaModificaTipoOggetto}">
<div class="messages confermaModificaTipoOggetto ">
    <ul>
        <li class="message info ">
            <p>Seleziona il tipo oggetto da assegnare.</p><br/>
                            <slf:lblField name="<%=MonitoraggioForm.OggettoDetail.ID_TIPO_OBJECT%>" width="w100" controlWidth="w60" labelWidth="w20" /><sl:newLine />
        </li>
    </ul>
</div>
            </c:if>
            <sl:newLine skipLine="true"/>
            <sl:contentTitle title="DETTAGLIO OGGETTO"/>
            <c:choose>
                <c:when test='<%= request.getAttribute("navTable").equals(MonitoraggioForm.OggettiList.NAME)
                        || request.getAttribute("navTable").equals(MonitoraggioForm.OggettoDetailUnitaDocList.NAME)
                        || request.getAttribute("navTable").equals(MonitoraggioForm.OggettoDetailSessioniList.NAME)
                        || request.getAttribute("navTable").equals(MonitoraggioForm.OggettoDetailOggettiDCMHashList.NAME)%>'>
                    <slf:listNavBarDetail name="<%= MonitoraggioForm.OggettiList.NAME%>"/>
                </c:when>
                <c:otherwise>
                    <slf:fieldBarDetailTag name="<%= MonitoraggioForm.OggettoDetail.NAME%>" />
                </c:otherwise>
            </c:choose>

            <slf:tab name="<%= MonitoraggioForm.OggettoTabs.NAME%>" tabElement="<%= MonitoraggioForm.OggettoTabs.dettaglio_oggetto%>">
                <slf:fieldSet  borderHidden="false">
                    <slf:lblField name="<%=MonitoraggioForm.OggettoDetail.ID_OBJECT%>" colSpan="4"/>
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioForm.OggettoDetail.VERSATORE%>" colSpan="4"/>
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioForm.OggettoDetail.CD_KEY_OBJECT%>" colSpan="4" />
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioForm.OggettoDetail.NM_TIPO_OBJECT%>" colSpan="4"/>
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioForm.OggettoDetail.TI_VERS_FILE%>" colSpan="4"/>
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioForm.OggettoDetail.DS_INFO_OBJECT%>" colSpan="4"/>
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioForm.OggettoDetail.TI_STATO_OBJECT%>" colSpan="4"/>
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioForm.OggettoDetail.FL_RICH_ANNUL_TIMEOUT%>" colSpan="4"/>
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioForm.OggettoDetail.DS_ESITO_ANNULLAMENTO%>" colSpan="4"/>
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioForm.OggettoDetail.TI_STATO_VERIFICA_HASH%>" colSpan="4"/>
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioForm.OggettoDetail.CD_VERS_GEN%>" colSpan="4"/>
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioForm.OggettoDetail.TI_CONSERVATO_SU_OS%>" colSpan="4"/>
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioForm.OggettoDetail.CD_TRASF%>" colSpan="4"/>
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioForm.OggettoDetail.CD_VERSIONE_TRASF%>" colSpan="4"/>
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioForm.OggettoDetail.TI_PRIORITA%>" colSpan="4"/>
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioForm.OggettoDetail.TI_PRIORITA_VERSAMENTO%>" colSpan="4"/>
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioForm.OggettoDetail.NM_KS_INSTANCE%>" colSpan="4"/>
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioForm.OggettoDetail.TI_GEST_OGGETTI_FIGLI%>" colSpan="4"/>
                    <sl:newLine />
                    <slf:section name="<%=MonitoraggioForm.NumUdSection.NAME%>" styleClass="noborder">
                        <slf:lblField name="<%=MonitoraggioForm.OggettoDetail.NI_UNITA_DOC_ATTESE%>" colSpan="2"/>
                        <slf:lblField name="<%=MonitoraggioForm.OggettoDetail.NI_UNITA_DOC_DA_VERS%>" colSpan="1"/>
                        <slf:lblField name="<%=MonitoraggioForm.OggettoDetail.NI_UNITA_DOC_VERS%>" colSpan="1"/>
                        <sl:newLine />
                        <slf:lblField name="<%=MonitoraggioForm.OggettoDetail.NI_UNITA_DOC_VERS__OK%>" colSpan="2"/>
                        <slf:lblField name="<%=MonitoraggioForm.OggettoDetail.NI_UNITA_DOC_VERS_ERR%>" colSpan="1"/>
                        <slf:lblField name="<%=MonitoraggioForm.OggettoDetail.NI_UNITA_DOC_VERS_TIMEOUT%>" colSpan="1"/>
                    </slf:section>
                    <slf:lblField name="<%=MonitoraggioForm.OggettoDetail.PG_OGGETTO_TRASF%>" colSpan="2"/>
                    <sl:newLine skipLine="true"/>
                    <slf:lblField name="<%=MonitoraggioForm.OggettoDetail.NOTE%>" colSpan="4"/>
                    <sl:newLine skipLine="true"/>
                    <slf:section name="<%=MonitoraggioForm.OggettoDaTrasformareObjDetailSection.NAME%>" styleClass="importantContainer">
                        <slf:lblField name="<%=MonitoraggioForm.OggettoDetail.NM_AMBIENTE_VERS_PADRE%>" colSpan="4"/>
                        <sl:newLine />
                        <slf:lblField name="<%=MonitoraggioForm.OggettoDetail.NM_VERS_PADRE%>" colSpan="4"/>
                        <sl:newLine />
                        <slf:lblField name="<%=MonitoraggioForm.OggettoDetail.CD_KEY_OBJECT_PADRE%>" colSpan="4"/>
                        <sl:newLine />
                        <slf:lblField name="<%=MonitoraggioForm.OggettoDetail.NM_TIPO_OBJECT_PADRE%>" colSpan="4"/>
                        <sl:newLine />
                        <slf:lblField name="<%=MonitoraggioForm.OggettoDetail.DS_OBJECT_PADRE%>" colSpan="4"/>
                        <sl:newLine />
                        <slf:lblField name="<%=MonitoraggioForm.OggettoDetail.NI_TOT_OBJECT_TRASF%>" colSpan="4"/>
                        <sl:newLine />
                        <slf:lblField name="<%=MonitoraggioForm.OggettoDetail.TI_STATO_OBJECT_PADRE%>" colSpan="4"/>
                        <sl:newLine />
                        <slf:lblField name="<%=MonitoraggioForm.OggettoDetail.DT_STATO_COR_PADRE%>" colSpan="4"/>
                        <sl:pulsantiera>
                            <slf:lblField name="<%=MonitoraggioForm.OggettoDetailButtonList.OGGETTO_DA_TRASFORMARE_OBJ_DETAIL%>" position="right"/>
                        </sl:pulsantiera>
                    </slf:section>
                    <sl:newLine skipLine="true"/>
                    <slf:section name="<%=MonitoraggioForm.InfoUltimaSessioneSection.NAME%>" styleClass="importantContainer">
                        <slf:lblField name="<%=MonitoraggioForm.OggettoDetail.DT_APERTURA%>" colSpan="2"/>
                        <slf:lblField name="<%=MonitoraggioForm.OggettoDetail.DT_CHIUSURA%>" colSpan="2"/>
                        <sl:newLine />
                        <slf:lblField name="<%=MonitoraggioForm.OggettoDetail.FL_FORZA_WARNING%>" colSpan="4"/>
                        <sl:newLine />
                        <slf:lblField name="<%=MonitoraggioForm.OggettoDetail.FL_FORZA_ACCETTAZIONE%>" colSpan="2"/>
                        <slf:lblField name="<%=MonitoraggioForm.OggettoDetail.DL_MOTIVO_FORZA_ACCETTAZIONE%>" colSpan="2"/>
                        <sl:newLine />
                        <slf:lblField name="<%=MonitoraggioForm.OggettoDetail.DL_MOTIVO_CHIUSO_WARNING%>" colSpan="4"/>
                        <sl:newLine />
                        <slf:lblField name="<%=MonitoraggioForm.OggettoDetail.NM_USERID_VERS%>" colSpan="4"/>
                    </slf:section>
                </slf:fieldSet>
            </slf:tab>

            <slf:tab name="<%= MonitoraggioForm.OggettoTabs.NAME%>" tabElement="<%= MonitoraggioForm.OggettoTabs.oggetto_xmlversato%>">
                <slf:fieldSet  borderHidden="false">
                    <slf:lblField name="<%=MonitoraggioForm.OggettoDetail.CD_VERSIONE_XML_VERS%>" colSpan="4" />
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioForm.OggettoDetail.BL_XML%>" colSpan="4" controlWidth="w100"/>
                </slf:fieldSet>
                <sl:newLine skipLine="true" />

                <sl:pulsantiera>
                    <slf:lblField name="<%=MonitoraggioForm.OggettoDetailButtonList.DOWNLOAD_XMLOGGETTO%>" width="w50" controlWidth="w30" labelWidth="w40"/>
                </sl:pulsantiera>
            </slf:tab>
            <slf:tab name="<%= MonitoraggioForm.OggettoTabs.NAME%>" tabElement="<%= MonitoraggioForm.OggettoTabs.studio_dicom%>">
                <slf:fieldSet  borderHidden="false">
                    <slf:lblField name="<%=MonitoraggioForm.StudioDICOM.CD_VERSIONE_DATI_SPEC_DICOM%>" colSpan="4"/>
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioForm.StudioDICOM.CD__AET_NODO_DICOM%>" colSpan="4" />
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioForm.StudioDICOM.DS_STUDY_INSTANCE__UID%>" colSpan="2"/>
                    <slf:lblField name="<%=MonitoraggioForm.StudioDICOM.DT_STUDY_DATE%>" colSpan="2"/>
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioForm.StudioDICOM.DS_ACCESSION_NUMBER%>" colSpan="4"/>
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioForm.StudioDICOM.NI_STUDY_RELATED_SERIES%>" colSpan="2"/>
                    <slf:lblField name="<%=MonitoraggioForm.StudioDICOM.NI_STUDY_RELATED_IMAGES%>" colSpan="2"/>
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioForm.StudioDICOM.CD_PATIENT_ID%>" colSpan="2"/>
                    <slf:lblField name="<%=MonitoraggioForm.StudioDICOM.CD_PATIENT_ID_ISSUER%>" colSpan="2"/>
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioForm.StudioDICOM.DS_PATIENT_NAME%>" colSpan="3"/>
                    <slf:lblField name="<%=MonitoraggioForm.StudioDICOM.DT_PATIENT_BIRTH_DATE%>" colSpan="3"/>
                    <slf:lblField name="<%=MonitoraggioForm.StudioDICOM.TI_PATIENT_SEX%>" colSpan="3"/>
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioForm.StudioDICOM.DL_LISTA_SOP_CLASS%>" colSpan="4"/>
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioForm.StudioDICOM.DL_LISTA_MODALITY_IN_STUDY%>" colSpan="2"/>
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioForm.StudioDICOM.DS_ISTITUTION_NAME%>" colSpan="2"/>
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioForm.StudioDICOM.DS_REF_PHYSICIAN_NAME%>" colSpan="4"/>
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioForm.StudioDICOM.DL_STUDY_DESCRIPTION%>" colSpan="2"/>
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioForm.StudioDICOM.DS_STUDY_ID%>" colSpan="4"/>
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioForm.StudioDICOM.DT_PRESA_IN_CARICO%>" colSpan="4"/>
                    <sl:newLine />
                    <slf:section name="<%=MonitoraggioForm.DCMHashSection.NAME%>" styleClass="importantContainer">
                        <slf:lblField name="<%=MonitoraggioForm.StudioDICOM.DS_DCM_HASH%>" colSpan="3"/>
                        <slf:lblField name="<%=MonitoraggioForm.StudioDICOM.TI_ALGO_DCM_HASH%>" colSpan="3"/>
                        <slf:lblField name="<%=MonitoraggioForm.StudioDICOM.CD_ENCODING_DCM_HASH%>" colSpan="3"/>
                        <sl:newLine />
                        <slf:lblField name="<%=MonitoraggioForm.StudioDICOM.BL_DCM_HASH_TXT%>" colSpan="4"/>
                    </slf:section>
                    <sl:newLine />
                    <slf:section name="<%=MonitoraggioForm.GLOBALHashSection.NAME%>" styleClass="importantContainer">
                        <slf:lblField name="<%=MonitoraggioForm.StudioDICOM.DS_GLOBAL_HASH%>" colSpan="3"/>
                        <slf:lblField name="<%=MonitoraggioForm.StudioDICOM.TI_ALGO_GLOBAL_HASH%>" colSpan="3"/>
                        <slf:lblField name="<%=MonitoraggioForm.StudioDICOM.CD_ENCODING_GLOBAL_HASH%>" colSpan="3"/>
                        <sl:newLine />
                        <slf:lblField name="<%=MonitoraggioForm.StudioDICOM.BL_GLOBAL_HASH_TXT%>" colSpan="4"/>
                    </slf:section>
                    <sl:newLine />
                    <slf:section name="<%=MonitoraggioForm.FileHashSection.NAME%>" styleClass="importantContainer">
                        <slf:lblField name="<%=MonitoraggioForm.StudioDICOM.DS_FILE_HASH%>" colSpan="3"/>
                        <slf:lblField name="<%=MonitoraggioForm.StudioDICOM.TI_ALGO_FILE_HASH%>" colSpan="3"/>
                        <slf:lblField name="<%=MonitoraggioForm.StudioDICOM.CD_ENCODING_FILE_HASH%>" colSpan="3"/>
                    </slf:section>
                </slf:fieldSet>
            </slf:tab>
            <slf:tab name="<%= MonitoraggioForm.OggettoTabs.NAME%>" tabElement="<%= MonitoraggioForm.OggettoTabs.xmlrich_annul%>">
                <slf:fieldSet  borderHidden="false">
                    <slf:lblField name="<%=MonitoraggioForm.XmlAnnulRich.DT_REG_XML_ANNUL%>" colSpan="4" /><sl:newLine />
                    <slf:lblField name="<%=MonitoraggioForm.XmlAnnulRich.BL_XML_ANNUL%>" colSpan="4" controlWidth="w100"/>
                </slf:fieldSet>
            </slf:tab>
            <slf:tab name="<%= MonitoraggioForm.OggettoTabs.NAME%>" tabElement="<%= MonitoraggioForm.OggettoTabs.xmlrisp_annul%>">
                <slf:fieldSet  borderHidden="false">
                    <slf:lblField name="<%=MonitoraggioForm.XmlAnnulRisp.DT_REG_XML_ANNUL%>" colSpan="4" /><sl:newLine />
                    <slf:lblField name="<%=MonitoraggioForm.XmlAnnulRisp.BL_XML_ANNUL%>" colSpan="4" controlWidth="w100"/>
                </slf:fieldSet>
            </slf:tab>
            <slf:tab name="<%= MonitoraggioForm.OggettoTabs.NAME%>" tabElement="<%= MonitoraggioForm.OggettoTabs.filtri_unita_doc_obj%>">
                <slf:fieldSet  borderHidden="false">
                    <slf:lblField name="<%=MonitoraggioForm.FiltriUnitaDocObj.CD_REGISTRO_UNITA_DOC_SACER%>" colSpan="2"/><sl:newLine />              
                    <slf:lblField name="<%=MonitoraggioForm.FiltriUnitaDocObj.AA_UNITA_DOC_SACER%>" colSpan="2" /><sl:newLine />
                    <slf:lblField name="<%=MonitoraggioForm.FiltriUnitaDocObj.CD_KEY_UNITA_DOC_SACER%>" colSpan="2" /><sl:newLine />
                    <slf:lblField name="<%=MonitoraggioForm.FiltriUnitaDocObj.TI_STATO_UNITA_DOC_OBJECT%>" colSpan="2" /><sl:newLine />
                    <slf:lblField name="<%=MonitoraggioForm.FiltriUnitaDocObj.CD_CONCAT_DL_ERR_SACER%>" colSpan="2" />
                    <sl:pulsantiera>
                        <slf:lblField name="<%=MonitoraggioForm.OggettoDetailButtonList.FILTRA_UNITA_DOC_OBJ%>"/>
                    </sl:pulsantiera>
                </slf:fieldSet>
            </slf:tab>
            <slf:tab name="<%= MonitoraggioForm.OggettoTabs.NAME%>" tabElement="<%= MonitoraggioForm.OggettoTabs.report_trasformazione%>">
                <slf:lblField name="<%=MonitoraggioForm.OggettoDetail.REPORT_XML%>" colSpan="4" controlWidth="w100"/>
            </slf:tab>
            <sl:newLine skipLine="true"/>
            <sl:pulsantiera>
                <slf:lblField name="<%=MonitoraggioForm.OggettoDetailButtonList.RECUPERO_ERR_TRASFORMAZIONE%>"/>
                <slf:lblField name="<%=MonitoraggioForm.OggettoDetailButtonList.RECUPERO_CHIUS_ERR_SCHED%>"/>
                <slf:lblField name="<%=MonitoraggioForm.OggettoDetailButtonList.RECUPERO_ERR_VERSAMENTO_PING%>"/>
                <slf:lblField name="<%=MonitoraggioForm.OggettoDetailButtonList.RECUPERO_CHIUS_ERR_VERS%>"/>
                <slf:lblField name="<%=MonitoraggioForm.OggettoDetailButtonList.MODIFICA_TIPO_OGGETTO%>"/>
                <slf:lblField name="<%=MonitoraggioForm.OggettoDetailButtonList.ANNULLA_OGGETTO_DETAIL%>"/>
                <slf:lblField name="<%=MonitoraggioForm.OggettoDetailButtonList.ANNULLA_VERSAMENTI_UDDETAIL%>"/>
                <slf:lblField name="<%=MonitoraggioForm.OggettoDetailButtonList.SETTA_DA_TRASFORMARE_DETAIL%>"/>
                <slf:lblField name="<%=MonitoraggioForm.OggettoDetailButtonList.SETTA_ERRORE_TRASFORMAZIONE_DETAIL%>"/>
                <slf:lblField name="<%=MonitoraggioForm.OggettoDetailButtonList.SETTA_CHIUSO_ERR_VERSAMENTO%>"/>
                <slf:lblField name="<%=MonitoraggioForm.OggettoDetailButtonList.VERIFICA_ANNULLAMENTO%>"/>
                <slf:lblField name="<%=MonitoraggioForm.OggettoDetailButtonList.ACCETTA_ANNULLAMENTO_FALLITO%>"/>
            </sl:pulsantiera>
            <sl:newLine skipLine="true"/>
            <slf:tab name="<%= MonitoraggioForm.OggettoSubTabs.NAME%>" tabElement="<%= MonitoraggioForm.OggettoSubTabs.lista_unita_doc%>">
                <slf:list   name="<%= MonitoraggioForm.OggettoDetailUnitaDocList.NAME%>" />
                <slf:listNavBar  name="<%= MonitoraggioForm.OggettoDetailUnitaDocList.NAME%>" />
            </slf:tab>
            <slf:tab name="<%= MonitoraggioForm.OggettoSubTabs.NAME%>" tabElement="<%= MonitoraggioForm.OggettoSubTabs.lista_file%>">
                <slf:list   name="<%= MonitoraggioForm.FileList.NAME%>" />
                <slf:listNavBar  name="<%= MonitoraggioForm.FileList.NAME%>" />
                <sl:newLine skipLine="true"/>
                <sl:pulsantiera>
                    <slf:lblField name="<%=MonitoraggioForm.OggettoDetailButtonList.DOWNLOAD_FILE_OGGETTO_OBJ_DETAIL%>"/>
                </sl:pulsantiera>
            </slf:tab>
            <slf:tab name="<%= MonitoraggioForm.OggettoSubTabs.NAME%>" tabElement="<%= MonitoraggioForm.OggettoSubTabs.lista_versamenti%>">
                <slf:list   name="<%= MonitoraggioForm.OggettoDetailSessioniList.NAME%>" />
                <slf:listNavBar  name="<%= MonitoraggioForm.OggettoDetailSessioniList.NAME%>" />
            </slf:tab>
            <slf:tab name="<%= MonitoraggioForm.OggettoSubTabs.NAME%>" tabElement="<%= MonitoraggioForm.OggettoSubTabs.lista_oggetti%>">
                <slf:list   name="<%= MonitoraggioForm.OggettoDetailOggettiDCMHashList.NAME%>" />
                <slf:listNavBar  name="<%= MonitoraggioForm.OggettoDetailOggettiDCMHashList.NAME%>" />
            </slf:tab>
            <slf:tab name="<%= MonitoraggioForm.OggettoSubTabs.NAME%>" tabElement="<%= MonitoraggioForm.OggettoSubTabs.lista_oggetti_trasf%>">
                <slf:list   name="<%= MonitoraggioForm.OggettoDetailOggettiTrasfList.NAME%>" />
                <slf:listNavBar  name="<%= MonitoraggioForm.OggettoDetailOggettiTrasfList.NAME%>" />
            </slf:tab>
            <slf:tab name="<%= MonitoraggioForm.OggettoSubTabs.NAME%>" tabElement="<%= MonitoraggioForm.OggettoSubTabs.lista_stati_versamenti%>">
                <slf:list   name="<%= MonitoraggioForm.OggettoDetailStatiVersamentiList.NAME%>" />
                <slf:listNavBar  name="<%= MonitoraggioForm.OggettoDetailStatiVersamentiList.NAME%>" />
            </slf:tab>
            <slf:tab name="<%= MonitoraggioForm.OggettoSubTabs.NAME%>" tabElement="<%= MonitoraggioForm.OggettoSubTabs.lista_priorita_versamento%>">
                <slf:list   name="<%= MonitoraggioForm.OggettoDetailPrioritaVersamentoList.NAME%>" />
                <slf:listNavBar  name="<%= MonitoraggioForm.OggettoDetailPrioritaVersamentoList.NAME%>" />
            </slf:tab>

<div><input name="mainNavTable" type="hidden" value="${(empty param.mainNavTable) ? fn:escapeXml(param.table) : fn:escapeXml(param.mainNavTable)  }" /></div>
            </sl:content>
            <sl:footer />
        </sl:body>
    </sl:html>
