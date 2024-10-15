<%@ page import="it.eng.sacerasi.slite.gen.form.VersamentoOggettoForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head title="Versamento da archivio" >
        <script type="text/javascript">
            $(document).ready(function () {
                //MEV25602                
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
            <sl:contentTitle title="Versamento da archivio" />
            <slf:fieldBarDetailTag name="<%=VersamentoOggettoForm.VersamentoOggettoDetail.NAME%>" hideBackButton="${!((fn:length(sessionScope['###_NAVHIS_CONTAINER'])) gt 1 )}" />
            <slf:fieldSet borderHidden="true">
                <slf:lblField name="<%=VersamentoOggettoForm.VersamentoOggettoDetail.NM_AMBIENTE_VERS%>" width="w50" labelWidth="w20" /><sl:newLine />
                <slf:lblField name="<%=VersamentoOggettoForm.VersamentoOggettoDetail.NM_VERS%>" width="w50" labelWidth="w20" /><sl:newLine />
                <slf:lblField name="<%=VersamentoOggettoForm.VersamentoOggettoDetail.NM_TIPO_OBJECT%>" width="w50" labelWidth="w20" controlWidth="w70"/><sl:newLine />
                <sl:newLine skipLine="true" />
                <slf:lblField name="<%=VersamentoOggettoForm.VersamentoOggettoDetail.DS_PATH_ARCHIVIO%>" width="w50" labelWidth="w20"/><sl:newLine />
                <slf:lblField name="<%= VersamentoOggettoForm.VersamentoOggettoDetail.DS_PATH_ARCHIVIO_OBJECT%>" width="w50" labelWidth="w20"/><sl:newLine />
                <sl:newLine skipLine="true" />
            </slf:fieldSet>
            <sl:pulsantiera>
                <slf:lblField  name="<%=VersamentoOggettoForm.VersamentoOggettoDetail.VERSA_OGGETTO_DA_ARCHIVIO %>" width="w50" />
                <slf:lblField  name="<%=VersamentoOggettoForm.VersamentoOggettoDetail.NUOVO_VERSAMENTO%>" width="w50" />
            </sl:pulsantiera>
        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>
