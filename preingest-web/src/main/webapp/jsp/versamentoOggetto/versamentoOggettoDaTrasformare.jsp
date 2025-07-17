<%@ page import="it.eng.sacerasi.slite.gen.form.VersamentoOggettoForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head title="Versamento oggetto da trasformare" >
        <link rel="stylesheet" type="text/css" href="/sacerping/webjars/plupload/2.3.9/js/jquery.plupload.queue/css/jquery.plupload.queue.css" type="text/css" media="screen" />
        <script type="text/javascript" src="/sacerping/webjars/plupload/2.3.9/js/plupload.full.min.js"></script>
        <script type="text/javascript" src="/sacerping/webjars/plupload/2.3.9/js/jquery.plupload.queue/jquery.plupload.queue.js"></script>
        <script type="text/javascript" src="/sacerping/webjars/plupload/2.3.9/js/i18n/it.js"></script>

        <script type="text/javascript" src="/sacerping/js/versamentoOggettoDaTrasformare.js"></script>

        <style>
            #os_upload_progress {
                text-align: center;
                height: auto;
            }
        </style>

        <script type="text/javascript">
            $(document).ready(function () {
                // Funzione che tenta di recuperare i formati supportati
                function getSupportedFormats(maxRetries = 5, retryInterval = 1000, currentRetry = 0) {
                    $.get("VersamentoOggetto.html", {operation: "getSupportedArchiveFormatList"})
                        .done(function (data) {
                            if (data && data.map && data.map.length > 0 && data.map[0].estensioni_ammesse) {
                                // Dati disponibili, procedi
                                let estensioni = data.map[0].estensioni_ammesse;
                                let filters = [];

                                for (let i = 0; i < estensioni.length; i++) {
                                    let ext = estensioni[i].substring(1);
                                    filters.push({title: "Archivi " + ext, extensions: ext});
                                }

                                verODT.init(filters);
                            } else if (currentRetry < maxRetries) {
                                // Dati non disponibili, riprova dopo un intervallo
                                console.log("Dati non disponibili, nuovo tentativo in " + retryInterval + "ms...");
                                setTimeout(function() {
                                    getSupportedFormats(maxRetries, retryInterval, currentRetry + 1);
                                }, retryInterval);
                            } else {
                                // Superato numero massimo tentativi
                                console.error("Impossibile ottenere i formati supportati dopo " + maxRetries + " tentativi");
                                $("#customMessageBox #error_text").text("Impossibile caricare i formati di archivio supportati.");
                                $("#customMessageBox").show();
                            }
                        })
                        .fail(function(jqXHR, textStatus, errorThrown) {
                            if (currentRetry < maxRetries) {
                                // Errore nella richiesta, riprova
                                console.log("Errore nella richiesta, nuovo tentativo in " + retryInterval + "ms...");
                                setTimeout(function() {
                                    getSupportedFormats(maxRetries, retryInterval, currentRetry + 1);
                                }, retryInterval);
                            } else {
                                console.error("Errore nella richiesta:", textStatus, errorThrown);
                                $("#customMessageBox #error_text").text("Errore nel caricamento dei formati di archivio supportati.");
                                $("#customMessageBox").show();
                            }
                        });
                }

                // Avvia il processo di recupero dei formati supportati
                getSupportedFormats();

                $("#Nm_tipo_object").change(function () {
                    var formData = $("#spagoLiteAppForm").serializeArray();
                    formData.push({name: 'operation', value: 'triggerVersamentoOggettoDetailNm_tipo_objectOnTrigger'});
                    $.post("VersamentoOggetto.html", formData).done(function (data) {
                        CAjaxDataFormWalk(data);
                        disableForm(data);

                        if (data.map[0].useObjectStorage) {
                            verODT.showObjectStorageUpload();
                        } else {
                            verODT.hideObjectStorageUpload();
                        }
                    });
                });

                if ($("#Nm_tipo_object") && $("#Nm_tipo_object").val().length > 0) {
                    $("#Nm_tipo_object").trigger('change');
                }
            }
            );

            function disableForm(jsonData) {
                switch (jsonData.type) {
                    case "Form":
                    case "Fields":
                        $.each(jsonData.map, function (property, value) {
                            disableForm(value);
                        });
                        break;
                    case "Input":
                    case "ComboBox":
                        var obj = $('#' + jsonData.name);
                        switch (jsonData.state) {
                            case "readonly":
                                obj.attr('readonly', true);
                                break;
                            case "view":
                                obj.attr('readonly', false);
                                break;
                            case "edit":
                                obj.attr('readonly', false);
                                break;
                        }
                        break;
                }
            }
        </script>
    </sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia versatore"/>
        <sl:menu showChangePasswordBtn="true" />
        <sl:content multipartForm="true">
            <slf:messageBox />
            <div id="customMessageBox" class="messages plainError" style="display: none;">
                <ul>
                    <span class="ui-icon ui-icon-alert"></span>
                    <span id="error_text"></span>
                </ul>
            </div>
            <sl:contentTitle title="Versamento oggetto da trasformare" />
            <slf:fieldBarDetailTag name="<%=VersamentoOggettoForm.VersamentoOggettoDetail.NAME%>" hideBackButton="${!((fn:length(sessionScope['###_NAVHIS_CONTAINER'])) gt 1 )}" />
            <slf:fieldSet borderHidden="true">
                <slf:lblField name="<%=VersamentoOggettoForm.VersamentoOggettoDetail.NM_AMBIENTE_VERS%>" width="w50" labelWidth="w20" /><sl:newLine />
                <slf:lblField name="<%=VersamentoOggettoForm.VersamentoOggettoDetail.NM_VERS%>" width="w50" labelWidth="w20" /><sl:newLine />
                <slf:lblField name="<%=VersamentoOggettoForm.VersamentoOggettoDetail.NM_TIPO_OBJECT%>" width="w50" labelWidth="w20" controlWidth="w70"/><sl:newLine />
                <slf:lblField name="<%=VersamentoOggettoForm.VersamentoOggettoDetail.DS_OBJECT%>" width="w50" labelWidth="w20"/><sl:newLine />
                <slf:lblField name="<%=VersamentoOggettoForm.VersamentoOggettoDetail.DS_HASH_FILE_VERS%>" width="w50" labelWidth="w20"/><sl:newLine />
                <sl:newLine skipLine="true" />
                <slf:lblField name="<%=VersamentoOggettoForm.VersamentoOggettoDetail.FL_TRASM_FTP%>" width="w50" labelWidth="w20"/><sl:newLine />
                <slf:lblField name="<%=VersamentoOggettoForm.VersamentoOggettoDetail.DS_PATH_FTP%>" width="w50" labelWidth="w20"/><sl:newLine />
                <slf:lblField name="<%= VersamentoOggettoForm.VersamentoOggettoDetail.CD_KEY_OBJECT%>" width="w50" labelWidth="w20"/><sl:newLine />
                <sl:newLine skipLine="true" />
                <sl:newLine />
                <slf:lblField name="<%=VersamentoOggettoForm.VersamentoOggettoDetail.FILE_TO_UPLOAD%>" width="w50" labelWidth="w20" controlWidth="w60"/><sl:newLine />
                <div class="containerLeft w50" style="display: none">
                    <label for="File_to_upload_Object_Storage" class="slLabel w20">File</label>
                    <input id="File_to_upload_Object_Storage" name="File_to_upload_Object_Storage" class="pulsanteUpload" type="button" value="Carica" >
                    <span id="File_to_upload_name_Object_Storage" style="display: none"><span></span><input class="pulsanteUpload" id="File_to_upload_rimuovi_Object_Storage" name="File_to_upload_rimuovi_Object_Storage" type="button" value="Rimuovi file"/></span>
                </div>
                <div class="containerLeft w50" style="display: none">
                    <label for="os_upload_progress" class="slLabel w20">File</label>
                    <div class="slText w60" style="border: 1px #333 groove;">
                        <div id="os_upload_progress" style="background-color: #07752C; color:white;"></div>
                    </div>
                </div>
                <sl:newLine skipLine="true" />
                <slf:lblField name="<%=VersamentoOggettoForm.VersamentoOggettoDetail.CD_VERSIONE_XML%>" width="w50" labelWidth="w20" controlWidth="w60"/><sl:newLine />
                <slf:lblField name="<%=VersamentoOggettoForm.VersamentoOggettoDetail.XML_TO_UPLOAD%>" width="w50" labelWidth="w20" controlWidth="w60"/><sl:newLine />
                <input type="hidden" id="Xml_to_upload_string" name="Xml_to_upload_string"/>
            </slf:fieldSet>
            <sl:pulsantiera>
                <slf:lblField name="<%=VersamentoOggettoForm.VersamentoOggettoDetail.VERSA_OGGETTO%>" width="w50" />
                <div class="containerLeft w50" style="display: none">
                    <input type="button" name="operation__versaOggettoObjectStorage" value="Versa oggetto in Ping" class="pulsante" disabled="true">
                </div>
                <slf:lblField  name="<%=VersamentoOggettoForm.VersamentoOggettoDetail.NUOVO_VERSAMENTO%>" width="w50" />
            </sl:pulsantiera>
        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>
