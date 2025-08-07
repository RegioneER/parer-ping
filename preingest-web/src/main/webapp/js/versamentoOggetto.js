//MEV25602
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

                VersamentoOSClient.init(filters);

                // MEV 38818 - spostato a dopo init di VersamentoOSClient
                if ($("#Nm_tipo_object") && $("#Nm_tipo_object").val().length > 0) {
                   $("#Nm_tipo_object").trigger('change');
                }
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

function initTipoOggettoFieldHandler() {
    $("#Nm_tipo_object").change(function () {
        var formData = $("#spagoLiteAppForm").serializeArray();
        formData.push({name: 'operation', value: 'triggerVersamentoOggettoDetailNm_tipo_objectOnTrigger'});
        $.post("VersamentoOggetto.html", formData).done(function (data) {
            CAjaxDataFormWalk(data);
            disableForm(data);

            // MEV25601
            if (data.map[0].useObjectStorage) {
                VersamentoOSClient.showObjectStorageUpload(data.map[0].isDaTrasformare);
            } else {
                VersamentoOSClient.hideObjectStorageUpload(data.map[0].isDaTrasformare);
            }
        });
    });
}

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