<%@ page import="it.eng.sacerasi.slite.gen.form.VersamentoOggettoForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head title="<%= VersamentoOggettoForm.VersamentoOggettoDetail.DESCRIPTION%>" >
        <link rel="stylesheet" type="text/css" href="/sacerping/js/plupload/jquery.plupload.queue/css/jquery.plupload.queue.css" type="text/css" media="screen" />
        
        <script type="text/javascript" src="/sacerping/js/plupload/plupload.full.min.js"></script>
        <script type="text/javascript" src="/sacerping/js/plupload/jquery.plupload.queue/jquery.plupload.queue.js"></script>
        <script type="text/javascript" src="/sacerping/js/plupload/i18n/it.js"></script>
        
        <script type="text/javascript" src="/sacerping/js/versamentoOggettoDaTrasformare.js"></script>
        
        <script type="text/javascript">
            $(document).ready(function () {
                // MEV25601
                $.get("VersamentoOggetto.html", {operation: "getSupportedArchiveFormatList"}).done(function (data) {
                    let estensioni = data.map[0].estensioni_ammesse;

                    let filters = [];

                    for (let i = 0; i < estensioni.length; i++) {
                        let ext = estensioni[i].substring(1);
                        filters.push(
                                {title: "Archivi " + ext, extensions: ext}
                        );
                    }

                    verODT.init(filters);
                });
                
                $("#Nm_tipo_object").change(function () {
                    var formData = $("#spagoLiteAppForm").serializeArray();
                    formData.push({name: 'operation', value: 'triggerVersamentoOggettoDetailNm_tipo_objectOnTrigger'});
                    $.post("VersamentoOggetto.html", formData).done(function (data) {
                        CAjaxDataFormWalk(data);
                        disableForm(data);
                        
                        // MEV25601
                        if (data.map[0].useObjectStorage) {
                            verODT.showObjectStorageUpload(data.map[0].isDaTrasformare);
                        } else {
                            verODT.hideObjectStorageUpload(data.map[0].isDaTrasformare);
                        }
                    });
                });

                $("#Cd_key_object_padre").change(function () {
                    var formData = $("#spagoLiteAppForm").serializeArray();
                    formData.push({name: 'operation', value: 'triggerVersamentoOggettoDetailCd_key_object_padreOnTriggerAjax'});
                    $.post("VersamentoOggetto.html", formData).done(function (data) {
                        CAjaxDataFormWalk(data);
                        disableForm(data);
                    });
                });

                if ($("#Nm_tipo_object") && $("#Nm_tipo_object").val().length > 0) {
                    $("#Nm_tipo_object").trigger('change');
                }
            });


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
            <sl:contentTitle title="<%= VersamentoOggettoForm.VersamentoOggettoDetail.DESCRIPTION%>" />
            <slf:fieldBarDetailTag name="<%=VersamentoOggettoForm.VersamentoOggettoDetail.NAME%>" hideBackButton="${!((fn:length(sessionScope['###_NAVHIS_CONTAINER'])) gt 1 )}" />
            <slf:fieldSet borderHidden="true">
                <slf:lblField name="<%=VersamentoOggettoForm.VersamentoOggettoDetail.NM_AMBIENTE_VERS%>" width="w50" labelWidth="w20" /><sl:newLine />
                <slf:lblField name="<%=VersamentoOggettoForm.VersamentoOggettoDetail.NM_VERS%>" width="w50" labelWidth="w20" /><sl:newLine />
                <slf:lblField name="<%=VersamentoOggettoForm.VersamentoOggettoDetail.NM_TIPO_OBJECT%>" width="w50" labelWidth="w20" controlWidth="w70"/><sl:newLine />
                <slf:lblField name="<%=VersamentoOggettoForm.VersamentoOggettoDetail.TI_PRIORITA%>" width="w50" labelWidth="w20" controlWidth="w70"/><sl:newLine />
                <slf:lblField name="<%=VersamentoOggettoForm.VersamentoOggettoDetail.TI_PRIORITA_VERSAMENTO%>" width="w50" labelWidth="w20" controlWidth="w70"/><sl:newLine />
                <slf:lblField name="<%=VersamentoOggettoForm.VersamentoOggettoDetail.DS_OBJECT%>" width="w50" labelWidth="w20"/><sl:newLine />
                <slf:lblField name="<%=VersamentoOggettoForm.VersamentoOggettoDetail.DS_HASH_FILE_VERS%>" width="w50" labelWidth="w20" /><sl:newLine />
                <slf:lblField name="<%=VersamentoOggettoForm.VersamentoOggettoDetail.CD_VERS_GEN%>" width="w50" labelWidth="w20"/><sl:newLine />
                <slf:lblField name="<%=VersamentoOggettoForm.VersamentoOggettoDetail.TI_GEST_OGGETTI_FIGLI%>" width="w50" labelWidth="w20"/><sl:newLine />
                <sl:newLine skipLine="true" />
                <slf:lblField name="<%=VersamentoOggettoForm.VersamentoOggettoDetail.FL_TRASM_FTP%>" width="w50" labelWidth="w20"/><sl:newLine />
                <slf:lblField name="<%=VersamentoOggettoForm.VersamentoOggettoDetail.DS_PATH_FTP%>" width="w50" labelWidth="w20"/><sl:newLine />
                <slf:lblField name="<%=VersamentoOggettoForm.VersamentoOggettoDetail.CD_KEY_OBJECT%>" width="w50" labelWidth="w20"/><sl:newLine />
                <sl:newLine skipLine="true" />
                <sl:newLine />
                <slf:lblField name="<%=VersamentoOggettoForm.VersamentoOggettoDetail.FILE_TO_UPLOAD%>" width="w50" labelWidth="w20" controlWidth="w60"/><sl:newLine />
                <!-- MEV25601 -->
                <div class="containerLeft w50" style="display: none">
                    <label for="File_to_upload_Object_Storage" class="slLabel w20">File</label>
                    <input id="File_to_upload_Object_Storage" name="File_to_upload_Object_Storage" class="pulsanteUpload" type="button" value="Carica" >
                    <span id="File_to_upload_name_Object_Storage" style="display: none"><span></span><input class="pulsanteUpload" id="File_to_upload_rimuovi_Object_Storage" name="File_to_upload_rimuovi_Object_Storage" type="button" value="Rimuovi file"/></span>
                </div>
                <div class="containerLeft w50" style="display: none">
                    <label for="os_upload_progress" class="slLabel w20">File</label>
                    <div class="slText w60" style="border: 1px #333 groove;">
                        <div id="os_upload_progress" style="background-color: #07752C; color: white;"></div>
                    </div>
                </div>
                <sl:newLine skipLine="true" />
                <slf:lblField name="<%=VersamentoOggettoForm.VersamentoOggettoDetail.CD_VERSIONE_XML%>" width="w50" labelWidth="w20" controlWidth="w60"/><sl:newLine />
                <slf:lblField name="<%=VersamentoOggettoForm.VersamentoOggettoDetail.XML_TO_UPLOAD%>" width="w50" labelWidth="w20" controlWidth="w60"/><sl:newLine />
                <input type="hidden" id="Xml_to_upload_string" name="Xml_to_upload_string"/>
                <sl:newLine skipLine="true" />
                <slf:section name="<%=VersamentoOggettoForm.OggettoDaTrasformareSection.NAME%>" styleClass="importantContainer">
                    <slf:lblField name="<%=VersamentoOggettoForm.VersamentoOggettoDetail.NM_AMBIENTE_VERS_PADRE%>" width="w50" labelWidth="w20" controlWidth="w80"/><sl:newLine />
                    <slf:lblField name="<%=VersamentoOggettoForm.VersamentoOggettoDetail.NM_VERS_PADRE%>" width="w50" labelWidth="w20" controlWidth="w80"/><sl:newLine />
                    <slf:lblField name="<%=VersamentoOggettoForm.VersamentoOggettoDetail.NM_TIPO_OBJECT_PADRE%>" width="w50" labelWidth="w20" controlWidth="w80"/><sl:newLine />
                    <slf:lblField name="<%=VersamentoOggettoForm.VersamentoOggettoDetail.CD_KEY_OBJECT_PADRE%>" width="w50" labelWidth="w20"/><sl:newLine />
                    <slf:lblField name="<%=VersamentoOggettoForm.VersamentoOggettoDetail.DS_OBJECT_PADRE%>" width="w50" labelWidth="w20"/><sl:newLine />
                    <slf:lblField name="<%=VersamentoOggettoForm.VersamentoOggettoDetail.NI_TOT_OBJECT_TRASF%>" width="w50" labelWidth="w20"/><sl:newLine />
                    <slf:lblField name="<%=VersamentoOggettoForm.VersamentoOggettoDetail.PG_OGGETTO_TRASF%>" width="w50" labelWidth="w20"/>
                </slf:section>
            </slf:fieldSet>
            <sl:pulsantiera>
                <slf:lblField  name="<%=VersamentoOggettoForm.VersamentoOggettoDetail.VERSA_OGGETTO%>" width="w50" />
                <!-- MEV25601 -->
                <div class="containerLeft w50" style="display: none">
                    <input type="button" name="operation__versaOggettoObjectStorage" value="Versa oggetto in Ping" class="pulsante" disabled="true">
                </div>
                <slf:lblField  name="<%=VersamentoOggettoForm.VersamentoOggettoDetail.NUOVO_VERSAMENTO%>" width="w50" />
            </sl:pulsantiera>
        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>
