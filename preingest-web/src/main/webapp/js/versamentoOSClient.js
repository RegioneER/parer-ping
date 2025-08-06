var VersamentoOSClient = {} || VersamentoOSClient;

VersamentoOSClient.init = function (extensions_filters) {
    VersamentoOSClient.os_uploader = new plupload.Uploader({
        browse_button: 'File_to_upload_Object_Storage', // you can pass in id...
        url: 'MultipartFileUploadToS3ForVersamentoOggettoServlet',
        runtimes: 'html5,html4',
        max_file_size: '0', // unlimited, la dimensione viene gestita da controlli lato java.
        unique_names: false,
        chunk_size: '20mb',
        prevent_duplicates: true,
        multi_selection: false,
        filters: extensions_filters,
        preinit: {
            PostInit: function () {
                $('input[name="operation__versaOggettoObjectStorage"]').click(function () {
                    $('input[name="File_to_upload_Object_Storage"]').parent().hide();

                    //prima di far partire l'upload mostrimao la barra di avanzamento
                    $('#os_upload_progress').progressbar({max: 100, value: 0});
                    $('#os_upload_progress').parent().parent().show();
                    $('#os_upload_progress').html('0%');
                    
                    $('input[name="operation__versaOggettoObjectStorage"]').prop('disabled', true);

                    VersamentoOSClient.os_uploader.start();
                    return false;
                });

                $('#File_to_upload_rimuovi_Object_Storage').click(function () {
                    VersamentoOSClient.removeFile();
                });
            },
            Error: function (up, err) {
                VersamentoOSClient.showErrorMessage(err);
                VersamentoOSClient.showObjectStorageUpload();
            },
            FilesAdded: function (up, files) {
                plupload.each(files, function (file) {
                    $('#File_to_upload_Object_Storage').hide();
                    $('#File_to_upload_name_Object_Storage span').text(file.name);
                    $('#File_to_upload_name_Object_Storage').show();
                });

                $('input[name="operation__versaOggettoObjectStorage"]').prop('disabled', false);
            },
            UploadFile: function (up, file) {
                let cd_vers_gen = $('input[name="Cd_vers_gen"]') ? $('input[name="Cd_vers_gen"]').val() : null;
                let ti_gest_oggetti_figli = $('select[name="Ti_gest_oggetti_figli"]') ? $('select[name="Ti_gest_oggetti_figli"]').val() : null;
                let xml_to_upload_string = $('#Xml_to_upload_string') ? $('#Xml_to_upload_string').val() : null;
                let cd_versione_xml = $('select[name="Cd_versione_xml"] option:selected') ? $('select[name="Cd_versione_xml"] option:selected').text() : null;

                // MEV 25602 - oggetto padre
                let nm_ambiente_vers_padre = $('input[name="Nm_ambiente_vers_padre"]') ? $('input[name="Nm_ambiente_vers_padre"]').val() : null;
                let nm_vers_padre = $('select[name="Nm_vers_padre"]') ? $('input[name="Nm_vers_padre"]').val() : null;
                let nm_tipo_object_padre = $('select[name="Nm_tipo_object_padre"]') ? $('input[name="Nm_tipo_object_padre"]').val() : null;
                let cd_key_object_padre = $('select[name="Cd_key_object_padre"]') ? $('input[name="Cd_key_object_padre"]').val() : null;
                let ds_object_padre = $('input[name="Ds_object_padre"]') ? $('input[name="Ds_object_padre"]').val() : null;
                let ni_tot_object_trasf = $('input[name="Ni_tot_object_trasf"]') ? $('input[name="Ni_tot_object_trasf"]').val() : null;
                let pg_oggetto_trasf = $('input[name="Pg_oggetto_trasf"]') ? $('input[name="Pg_oggetto_trasf"]').val() : null;
                let ti_priorita_versamento = $('select[name="Ti_priorita_versamento"]') ? $('select[name="Ti_priorita_versamento"]').val() : null;

                up.setOption('multipart_params', {
                    'idSessione': up.id,
                    'dimensione': file.origSize,
                    'nm_ambiente_vers': $('input[name="Nm_ambiente_vers"]').val(),
                    'nm_vers': $('input[name="Nm_vers"]').val(),
                    'nm_tipo_object': $('select[name="Nm_tipo_object"]').val(),
                    'ds_object': $('textarea[name="Ds_object"]').val(),
                    'ds_hash_file_vers': $('input[name="Ds_hash_file_vers"]').val(),
                    'cd_vers_gen': cd_vers_gen,
                    'ti_gest_oggetti_figli': ti_gest_oggetti_figli,
                    'xml_to_upload_string': xml_to_upload_string,
                    'cd_versione_xml': cd_versione_xml,
                    'nm_ambiente_vers_padre': nm_ambiente_vers_padre,
                    'nm_vers_padre': nm_vers_padre,
                    'nm_tipo_object_padre': nm_tipo_object_padre,
                    'cd_key_object_padre': cd_key_object_padre,
                    'ds_object_padre': ds_object_padre,
                    'ni_tot_object_trasf': ni_tot_object_trasf,
                    'pg_oggetto_trasf': pg_oggetto_trasf,
                    'ti_priorita_versamento': ti_priorita_versamento
                });
            },
            UploadProgress: function (up, file) {
                var sizeParent = $('#os_upload_progress').parent().width();
                $('#os_upload_progress').width((sizeParent / 100) * file.percent);
                $('#os_upload_progress').html(file.percent + '%');
                $('#os_upload_progress').progressbar({max: 100, value: file.percent});
            },
            ChunkUploaded: function (up, file, result) {
                var esito = jQuery.parseJSON(result.response);
                if (esito.error || (esito.result && esito.result.code > 500)) {
                    up.stop();
                    VersamentoOSClient.showErrorMessage(esito.error? esito.error : esito.result);
                    VersamentoOSClient.showObjectStorageUpload();
                }
            },
            FileUploaded: function (up, file, result) {
                var esito = jQuery.parseJSON(result.response);
                //MEV#21995 gestire gli errori.
                if (esito.result.code != 200) {
                    up.stop();
                    VersamentoOSClient.showErrorMessage(esito.result);
                    VersamentoOSClient.showObjectStorageUpload();
                } else {
                    //successo, redirect al dettaglio versamento.
                    var _csrf = $('#spagoLiteAppForm').attr('action').substr("VersamentoOggetto.html?".length);
                    $('<form id="spagoLiteAppForm" action="VersamentoOggetto.html?' + _csrf + '" method="post">' +
                            '<input type="hidden" name="Nm_ambiente_vers" value="' + $('#Nm_ambiente_vers_hidden').val() + '"/>' +
                            '<input type="hidden" name="Nm_vers" value="' + $('#Nm_vers_hidden').val() + '"/>' +
                            '<input type="hidden" name="Ds_object" value="' + $('#Ds_object').val() + '"/>' +
                            '<input type="hidden" name="Ds_hash_file_vers" value="' + $('#Ds_hash_file_vers').val() + '"/>' +
                            '<input type="hidden" name="Nm_tipo_object" value="' + $('select[name="Nm_tipo_object"]').val() + '"/>' +
                            '<input type="hidden" name="File_to_upload" value="' + file.name + '"/>' +
                            '<input type="hidden" name="operation" value="versamentoOggettoSuObjectStorageCompletato"/>' +
                            '</form>').appendTo('body').submit();

                }
            }
        }
    });

    VersamentoOSClient.os_uploader.init();
};

VersamentoOSClient.showObjectStorageUpload = function (isDaTrasformare) {
    $('input[name="operation__versaOggetto"]').parent().hide();
    $('input[name="operation__versaOggettoObjectStorage"]').parent().show();
    $('input[name="File_to_upload_Object_Storage"]').parent().show();
    $('#os_upload_progress').parent().parent().hide();

    //MEV 25601
    let oggettoDaTrasformareSection = $('#OggettoDaTrasformareSection');
    if (oggettoDaTrasformareSection && isDaTrasformare) {
        oggettoDaTrasformareSection.hide();
    }
    else {
        oggettoDaTrasformareSection.show();
    }

    $('#Xml_to_upload').change((event) => {
        if (!window.FileReader)
            return; // Browser is not compatible

        var reader = new FileReader();

        reader.onload = function (evt) {
            if (evt.target.readyState != 2)
                return;
            if (evt.target.error) {
                alert('Error while reading file');
                return;
            }

            $('#Xml_to_upload_string').val(evt.target.result);
        };

        reader.readAsText(event.target.files[0]);
    });

    VersamentoOSClient.removeFile();
};

VersamentoOSClient.hideObjectStorageUpload = function (isDaTrasformare) {
    $('input[name="operation__versaOggetto"]').parent().show();
    $('input[name="operation__versaOggettoObjectStorage"]').parent().hide();
    $('input[name="File_to_upload_Object_Storage"]').parent().hide();
    $('#os_upload_progress').parent().parent().hide();

    //MEV 25601 - 
    let oggettoDaTrasformareSection = $('#OggettoDaTrasformareSection');
    if (oggettoDaTrasformareSection && isDaTrasformare) {
        oggettoDaTrasformareSection.hide();
    }
    else {
        oggettoDaTrasformareSection.show();
    }

    $('#Xml_to_upload').off();
};

VersamentoOSClient.removeFile = function () {
    plupload.each(VersamentoOSClient.os_uploader.files, function (file) {
        VersamentoOSClient.os_uploader.removeFile(file);
    });

    $('input[name="operation__versaOggettoObjectStorage"]').prop('disabled', true);

    $('#File_to_upload_name_Object_Storage').hide();
    $('#File_to_upload_name_Object_Storage span').text("");
    $('#File_to_upload_Object_Storage').show();

};

VersamentoOSClient.showErrorMessage = function (error) {
    $('div.messages.plainError').hide();
    $('#error_text').text(error.message);
    $('#customMessageBox').show();
};
